import events.*;
import events.bus.EventBus;
import model.Player;
import model.Team;
import persistence.SnapshotSerializer;
import persistence.FilePersistenceListener;
import repository.InMemoryTeamRepository;
import repository.TeamRepository;
import service.PlayerService;
import service.TeamService;
import service.impl.PlayerServiceImpl;
import service.impl.TeamServiceImpl;
import ui.AppController;
import ui.ConsoleMenu;
import ui.UiEventListener;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Configure file paths relative to the working directory
        Path teamsCsv = Path.of("data/teams.csv");
        Path playersCsv = Path.of("data/players.csv");
        Path teamsJson = Path.of("data/teams.json");
        Path playersJson = Path.of("data/players.json");

        //SoccerDatabase database = new CsvSoccerDatabase(teamCsv, playerCsv);
        SnapshotSerializer serializer = new SnapshotSerializer(teamsCsv, playersCsv, teamsJson, playersJson);

        // Create repository and preload teams from persistence.
        TeamRepository repository = new InMemoryTeamRepository();

        // Attempt to load data from JSON, falling back to CSV
        try {
            List<Team> loadedTeams = serializer.loadTeamsSnapshotFromJson();
            List<Player> loadedPlayers = serializer.loadPlayersSnapshotFromJson();
            if (loadedTeams.isEmpty() || loadedPlayers.isEmpty()) {
                loadedTeams = serializer.loadTeamsSnapshotFromCsv();
                loadedPlayers = serializer.loadPlayersSnapshotFromCsv();
            }
            // Persist loaded teams to repository
            repository.saveTeams(loadedTeams);
            repository.savePlayers(loadedPlayers);

        } catch (Exception e) {
            System.err.println("No se pudo cargar la información guardada: " + e.getMessage());
        }

        // Cargar datos guardados
        //teams.addAll(database.load());

        /** Inicializar objetos*/
        EventBus eventBus = new EventBus();
        TeamService teamService = new TeamServiceImpl(repository, eventBus);
        PlayerService playerService = new PlayerServiceImpl(repository, eventBus);

        /** UI controller*/
        AppController controller = new AppController(teamService, playerService);

        /** Registrar listeners*/
        UiEventListener uiEventListener = new UiEventListener();
        FilePersistenceListener persistenceListener = new FilePersistenceListener(repository, serializer, 300);

        /** Subscribir cada tipo de evento explicitamente*/
        eventBus.subscribe(TeamCreatedEvent.class, uiEventListener);
        eventBus.subscribe(TeamDeletedEvent.class, uiEventListener);
        eventBus.subscribe(PlayerAddedToTeamEvent.class, uiEventListener);
        eventBus.subscribe(PlayerDeletedFromTeamEvent.class, uiEventListener);
        eventBus.subscribe(MatchSimulatedEvent.class, uiEventListener);

        eventBus.subscribe(TeamCreatedEvent.class, persistenceListener);
        eventBus.subscribe(TeamDeletedEvent.class, persistenceListener);
        eventBus.subscribe(PlayerAddedToTeamEvent.class, persistenceListener);
        eventBus.subscribe(PlayerDeletedFromTeamEvent.class, persistenceListener);
        eventBus.subscribe(MatchSimulatedEvent.class, persistenceListener);

        /** Crea un menu con un hook de salida*/
        ConsoleMenu menu = new ConsoleMenu(controller, () -> {
            // on exit: flush pending saves and shut down listeners
            persistenceListener.saveSnapshotNow();
            persistenceListener.shutdown();
            eventBus.shutdown();
        });
        menu.runLoop();
        menu.close();
    }
}