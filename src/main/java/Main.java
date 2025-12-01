import events.*;
import events.bus.EventBus;
import model.Player;
import model.Team;
import persistence.DataSerializer;
import persistence.SnapshotSerializer;
import persistence.FilePersistenceListener;
import repository.InMemoryTeamRepository;
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

    // --- CONSTANTES DE CONFIGURACIÓN ---
    private static final String TEAMS_CSV_PATH = "data/teams.csv";
    private static final String PLAYERS_CSV_PATH = "data/players.csv";
    private static final String TEAMS_JSON_PATH = "data/teams.json";
    private static final String PLAYERS_JSON_PATH = "data/players.json";

    // Tiempo de espera para guardar en disco tras un evento (Debounce)
    private static final long PERSISTENCE_DEBOUNCE_MS = 300;

    public static void main(String[] args) {

        // Configure file paths relative to the working directory
        Path teamsCsv = Path.of(TEAMS_CSV_PATH);
        Path playersCsv = Path.of(PLAYERS_CSV_PATH);
        Path teamsJson = Path.of(TEAMS_JSON_PATH);
        Path playersJson = Path.of(PLAYERS_JSON_PATH);

        //SoccerDatabase database = new CsvSoccerDatabase(teamCsv, playerCsv);
        DataSerializer serializer = new SnapshotSerializer(teamsCsv, playersCsv, teamsJson, playersJson);

        // Create main.java.repository and preload teams from main.java.persistence.
        InMemoryTeamRepository memoryRepo = new InMemoryTeamRepository();

        // Attempt to load data from JSON, falling back to CSV
        try {
            System.out.println("Cargando datos...");
            List<Team> loadedTeams = serializer.loadTeams();
            List<Player> loadedPlayers = serializer.loadPlayers();

            // Persist loaded teams to main.java.repository
            memoryRepo.saveTeams(loadedTeams);
            memoryRepo.savePlayers(loadedPlayers);
            System.out.println("Carga completada: " + loadedTeams.size() + " equipos y " + loadedPlayers.size() + " jugadores.");

        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo cargar la información guardada: " + e.getMessage());
        }

        /** Inicializar objetos*/
        EventBus eventBus = new EventBus();
        TeamService teamService = new TeamServiceImpl(memoryRepo, memoryRepo, eventBus);
        PlayerService playerService = new PlayerServiceImpl(memoryRepo, memoryRepo, eventBus);

        /** UI controller*/
        AppController controller = new AppController(teamService, playerService);

        /** Registrar listeners*/
        UiEventListener uiEventListener = new UiEventListener();
        FilePersistenceListener persistenceListener = new FilePersistenceListener(
                memoryRepo,
                memoryRepo,
                serializer,
                PERSISTENCE_DEBOUNCE_MS);

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