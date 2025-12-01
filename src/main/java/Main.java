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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        // CARGAR CONFIGURACIÓN (application.properties)
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Lo siento, no se pudo encontrar application.properties");
                return;
            }
            prop.load(input);
        } catch (IOException ex) {
            System.err.println("Error al cargar la configuración: " + ex.getMessage());
            return;
        }

        // Leer valores del fichero (o usar valores por defecto si no existen)
        String teamsCsvPathStr = prop.getProperty("data.path.teams.csv", "data/teams.csv");
        String playersCsvPathStr = prop.getProperty("data.path.players.csv", "data/players.csv");
        String teamsJsonPathStr = prop.getProperty("data.path.teams.json", "data/teams.json");
        String playersJsonPathStr = prop.getProperty("data.path.players.json", "data/players.json");

        long debounceMs;
        try {
            debounceMs = Long.parseLong(prop.getProperty("persistence.debounce.ms", "300"));
        } catch (NumberFormatException e) {
            debounceMs = 300; // Valor por defecto seguro
            System.err.println("Advertencia: Valor de debounce inválido en config, usando 300ms.");
        }

        // CONFIGURAR RUTAS
        Path teamsCsv = Path.of(teamsCsvPathStr);
        Path playersCsv = Path.of(playersCsvPathStr);
        Path teamsJson = Path.of(teamsJsonPathStr);
        Path playersJson = Path.of(playersJsonPathStr);

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
                debounceMs);

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