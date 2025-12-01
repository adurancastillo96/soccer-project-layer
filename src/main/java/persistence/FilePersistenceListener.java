package persistence;

import events.DomainEvent;
import events.bus.DomainEventListener;
import model.Player;
import model.Team;
import repository.PlayerRepository;
import repository.TeamRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FilePersistenceListener implements DomainEventListener<DomainEvent> {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final SnapshotSerializer serializer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingTask;
    private final long debounceMillis;

    public FilePersistenceListener(PlayerRepository playerRepository, TeamRepository teamRepository, SnapshotSerializer serializer, long debounceMillis) {
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
        try {
            serializer.saveSnapshotToCsv(teams, players);
            serializer.saveSnapshotToJson(teams, players);
        } catch (IOException e) {
            // If main.java.persistence fails we log to stderr but do not throw further
            System.err.println("Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the scheduler main.java.service. Call this when the application
     * terminates to ensure no threads remain.
     */
    public void shutdown() {
        scheduler.shutdown();
    }

}
