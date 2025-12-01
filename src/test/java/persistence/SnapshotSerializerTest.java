package persistence;

import model.Player;
import model.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotSerializerTest {

    // JUnit inyecta aquí una ruta temporal que se borra al acabar
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Debe guardar y cargar correctamente los datos en formato JSON")
    void shouldSaveAndLoadJsonSnapshot() throws IOException {
        // 1. PREPARACIÓN (Arrange)
        // Definimos rutas de archivos dentro de la carpeta temporal
        Path teamsCsv = tempDir.resolve("teams.csv");
        Path playersCsv = tempDir.resolve("players.csv");
        Path teamsJson = tempDir.resolve("teams.json");
        Path playersJson = tempDir.resolve("players.json");

        // Instanciamos la clase concreta, pero probaremos sus métodos públicos
        SnapshotSerializer serializer = new SnapshotSerializer(teamsCsv, playersCsv, teamsJson, playersJson);

        // Creamos datos de prueba
        Team team = new Team("Test FC", "Test City", "Tester Coach", "4-4-2");
        // Forzamos algunas victorias para probar que se guardan
        team.incrementMatchesWon();

        Player player = new Player(team.getTeamId(), "Test Player", 1999, "Forward", 9);
        player.incrementGoals();

        List<Team> teamsToSave = List.of(team);
        List<Player> playersToSave = List.of(player);
        SoccerData data = new SoccerData(teamsToSave, playersToSave);

        // 2. EJECUCIÓN (Act)
        // Guardamos
        serializer.save(data);

        // 3. VERIFICACIÓN (Assert)
        // A. Verificar que el archivo físico existe
        assertTrue(Files.exists(teamsJson), "El archivo teams.json debería haberse creado");
        assertTrue(Files.exists(playersJson), "El archivo players.json debería haberse creado");
        assertTrue(Files.exists(teamsCsv), "Debería existir el CSV de equipos (backup)");
        assertTrue(Files.exists(playersCsv), "Debería existir el CSV de jugadores (backup)");

        // B. Cargar los datos desde el archivo recién creado
        SoccerData dataLoaded = serializer.load();

        // C. Comprobar que los datos cargados son iguales a los guardados
        assertNotNull(dataLoaded.getTeams());
        assertEquals(1, dataLoaded.getTeams().size());
        Team loadedTeam = dataLoaded.getTeams().get(0);
        assertEquals(team.getTeamId(), loadedTeam.getTeamId());
        assertEquals("Test FC", loadedTeam.getName());
        assertEquals(1, loadedTeam.getMatchesWon());

        assertNotNull(dataLoaded.getPlayers());
        assertEquals(1, dataLoaded.getPlayers().size());
        Player loadedPlayer = dataLoaded.getPlayers().get(0);
        assertEquals(player.getPlayerId(), loadedPlayer.getPlayerId());
        assertEquals("Test Player", loadedPlayer.getFullName());
        assertEquals(1, loadedPlayer.getGoals());
    }
}