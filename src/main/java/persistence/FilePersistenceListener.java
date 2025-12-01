package persistence;

import events.DomainEvent;
import events.bus.DomainEventListener;
import model.Player;
import model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.PlayerRepository;
import repository.TeamRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FilePersistenceListener implements DomainEventListener<DomainEvent> {

    private static final Logger logger = LoggerFactory.getLogger(FilePersistenceListener.class);
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final DataSerializer serializer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingTask;
    private final long debounceMillis;

    public FilePersistenceListener(PlayerRepository playerRepository, TeamRepository teamRepository, DataSerializer serializer, long debounceMillis) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.serializer = serializer;
        this.debounceMillis = debounceMillis;
    }

    @Override
    public synchronized void onEvent(DomainEvent event) {
        scheduleSave();
    }

    private synchronized void scheduleSave() {
        if (pendingTask != null && !pendingTask.isDone()) {
            pendingTask.cancel(false);
        }
        pendingTask = scheduler.schedule(this::saveSnapshot, debounceMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Immediately writes the current state to disk. This method blocks until
     * the write completes and can be invoked on application shutdown to
     * ensure a final save.
     */
    public void saveSnapshotNow() {
        saveSnapshot();
    }

    private void saveSnapshot() {
        List<Team> teams = teamRepository.findAllTeams();
        List<Player> players = playerRepository.findAllPlayers();

        // Empaquetar
        SoccerData snapshot = new SoccerData(teams, players);

        try {
            serializer.save(snapshot);
            logger.debug("Snapshot guardado correctamente en disco.");
        } catch (IOException e) {
            // If persistence fails we log to stderr but do not throw further
            logger.error("Fallo crítico al guardar el snapshot en disco", e);
        }
    }

    /**
     * Shuts down the scheduler service. Call this when the application
     * terminates to ensure no threads remain.
     */
    public void shutdown() {
        scheduler.shutdown();
    }

}
