package persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.Player;
import model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility responsible for serializing and deserializing application state to
 * CSV and JSON files. It performs atomic writes by first writing to a
 * temporary file and then renaming it into place. The JSON parser included
 * here is deliberately simple and only supports the exact structure
 * produced by {@code toJson}.
 */
public class SnapshotSerializer implements DataSerializer {
    private final Path teamsCsvPath;
    private final Path playersCsvPath;
    private final Path teamsJsonPath;
    private final Path playersJsonPath;

    private final ObjectMapper mapper; // Jackson

    public SnapshotSerializer(Path teamsCsvPath, Path playersCsvPath, Path teamsJsonPath, Path playersJsonPath) {
        this.teamsCsvPath = teamsCsvPath;
        this.playersCsvPath = playersCsvPath;
        this.teamsJsonPath = teamsJsonPath;
        this.playersJsonPath = playersJsonPath;

        // Inicializamos Jackson y le decimos que ponga saltos de línea (INDENT_OUTPUT)
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ======================================================
    //    IMPLEMENTACIÓN DE LA INTERFAZ (Lógica Unificada)
    // ======================================================

    @Override
    public void save(List<Team> teams, List<Player> players) throws IOException {
        // Guardamos en AMBOS formatos para seguridad (como hacías antes)
        saveSnapshotToJson(teams, players);
        saveSnapshotToCsv(teams, players);
    }

    @Override
    public List<Team> loadTeams() throws IOException {
        // Estrategia: Intentar JSON primero
        List<Team> teams = loadTeamsSnapshotFromJson();

        // Si no hay datos (o archivo no existe), intentamos CSV (Fallback)
        if (teams.isEmpty()) {
            System.out.println("(Log interno) JSON de equipos vacío o no existente. Intentando cargar desde CSV...");
            teams = loadTeamsSnapshotFromCsv();
        }
        return teams;
    }

    @Override
    public List<Player> loadPlayers() throws IOException {
        // Estrategia: Intentar JSON primero
        List<Player> players = loadPlayersSnapshotFromJson();

        // Fallback a CSV
        if (players.isEmpty()) {
            System.out.println("(Log interno) JSON de jugadores vacío o no existente. Intentando cargar desde CSV...");
            players = loadPlayersSnapshotFromCsv();
        }
        return players;
    }

    // ==========================================
    //       NUEVA IMPLEMENTACIÓN CON JACKSON
    // ==========================================

    /**
     * Writes the given teams and players into a single JSON file. The JSON
     * structure is a dictionary with a single key {@code "teams"} holding a
     * list of teams with nested player lists. Writes are atomic via a
     * temporary file.
     *
     * @param teams the current list of teams
     * @throws IOException if an IO error occurs
     */
    private void saveSnapshotToJson(List<Team> teams, List<Player> players) throws IOException {
        // Convertimos los objetos a String JSON usando Jackson.
        String teamsJson = mapper.writeValueAsString(teams);
        String playersJson = mapper.writeValueAsString(players);

        // Guardamos en disco usando metodo atómico
        writeAtomically(teamsJsonPath, teamsJson);
        writeAtomically(playersJsonPath, playersJson);
    }

    /**
     * Reads teams and players from the JSON snapshot file. If the file does not
     * exist an empty list is returned. Only the exact structure produced by
     * {@link #saveSnapshotToJson(List, List)} is supported.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    private List<Team> loadTeamsSnapshotFromJson() throws IOException {
        if (!Files.exists(teamsJsonPath)) return new ArrayList<>();
        // Jackson lee el archivo y lo convierte a Lista de Teams automáticamente
        return mapper.readValue(teamsJsonPath.toFile(), new TypeReference<List<Team>>() {});
    }

    /**
     * Reads teams and players from the JSON snapshot file. If the file does not
     * exist an empty list is returned. Only the exact structure produced by
     * {@link #saveSnapshotToJson(List, List)} is supported.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    private List<Player> loadPlayersSnapshotFromJson() throws IOException {
        if (!Files.exists(playersJsonPath)) return new ArrayList<>();
        return mapper.readValue(playersJsonPath.toFile(), new TypeReference<List<Player>>() {});
    }

    // ==========================================
    //                MÉTODOS CSV
    // ==========================================
    // Es necesario mantener esto para que el sistema tenga un respaldo (fallback)

    /**
     * Writes the given teams and their players to CSV files using a semicolon
     * as the delimiter. Team data goes into the teams CSV and player data
     * into the players CSV. Writes are atomic via a temporary file.
     *
     * @param teams the current list of teams
     * @throws IOException if any IO error occurs
     */
    private void saveSnapshotToCsv(List<Team> teams, List<Player> players) throws IOException {
        // Build CSV content for teams and players
        StringBuilder teamsCsv = new StringBuilder();
        StringBuilder playersCsv = new StringBuilder();
        for (Team team : teams) {
            teamsCsv.append(team.getTeamId()).append(';')
                    .append(escapeCsv(team.getName())).append(';')
                    .append(escapeCsv(team.getCity())).append(';')
                    .append(escapeCsv(team.getCoach())).append(';')
                    .append(escapeCsv(team.getFormation())).append(';')
                    .append(team.getMatchesWon())
                    .append(System.lineSeparator());
        }
        for (Player player : players) {
            playersCsv.append(player.getPlayerId()).append(';')
                    .append(player.getTeamId()).append(';')
                    .append(escapeCsv(player.getFullName())).append(';')
                    .append(player.getBirthYear()).append(';')
                    .append(escapeCsv(player.getFieldLocation())).append(';')
                    .append(player.getSquadNumber()).append(';')
                    .append(player.getGoals())
                    .append(System.lineSeparator());
        }

        // Write teams file atomically
        writeAtomically(teamsCsvPath, teamsCsv.toString());
        // Write players file atomically
        writeAtomically(playersCsvPath, playersCsv.toString());
    }

    /**
     * Reads teams and players from existing CSV files. If neither file exists
     * then an empty list is returned. Both files must be present; if one
     * exists without the other the method treats it as no data.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    private List<Team> loadTeamsSnapshotFromCsv() throws IOException {
        if (!Files.exists(teamsCsvPath)) return new ArrayList<>();
        Map<UUID, Team> teamMap = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(teamsCsvPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue;
                UUID teamId = UUID.fromString(parts[0]);
                String name = unescapeCsv(parts[1]);
                String city = unescapeCsv(parts[2]);
                String coach = unescapeCsv(parts[3]);
                String formation = unescapeCsv(parts[4]);
                int matchesWon = Integer.parseInt(parts[5]);
                Team team = new Team(name, city, coach, formation);
                team.setTeamId(teamId);
                team.setMatchesWon(matchesWon);
                teamMap.put(teamId, team);
            }
        }
        return new ArrayList<>(teamMap.values());
    }

    /**
     * Reads teams and players from existing CSV files. If neither file exists
     * then an empty list is returned. Both files must be present; if one
     * exists without the other the method treats it as no data.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    private List<Player> loadPlayersSnapshotFromCsv() throws IOException {
        if (!Files.exists(playersCsvPath)) return new ArrayList<>();
        Map<UUID, Player> playerMap = new HashMap<>();
        // Read players
        try (BufferedReader reader = Files.newBufferedReader(playersCsvPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 7) continue;
                UUID teamId = UUID.fromString(parts[0]);
                UUID playerId = UUID.fromString(parts[1]);
                String fullName = unescapeCsv(parts[2]);
                int birthYear = Integer.parseInt(parts[3]);
                String fieldLocation = unescapeCsv(parts[4]);
                int squadNumber = Integer.parseInt(parts[5]);
                int goals = Integer.parseInt(parts[6]);
                Player player = new Player(teamId, fullName, birthYear, fieldLocation, squadNumber);
                player.setGoals(goals);
                player.setPlayerId(playerId);
                playerMap.put(playerId, player);
            }
        }
        return new ArrayList<>(playerMap.values());
    }

    // ==========================================
    //            UTILIDADES PRIVADAS
    // ==========================================

    /**
     * Helper to escape a string for CSV output. Simply wraps values containing
     * semicolons or quotes in double quotes and escapes any double quotes.
     *
     * @param value the raw value
     * @return the escaped value
     */
    private static String escapeCsv(String value) {
        if (value == null) return "";
        boolean needsQuotes = value.contains(";") || value.contains("\"");
        String escaped = value.replace("\"", "\"\"");
        return needsQuotes ? '"' + escaped + '"' : escaped;
    }

    /**
     * Undo CSV escaping for a value read from the file.
     *
     * @param value the escaped value
     * @return the unescaped raw value
     */
    private static String unescapeCsv(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            // Remove surrounding quotes
            trimmed = trimmed.substring(1, trimmed.length() - 1);
            return trimmed.replace("\"\"", "\"");
        }
        return trimmed;
    }

    /**
     * Performs an atomic write by writing to a temporary file and then moving
     * it into place. If the destination file exists it will be replaced.
     *
     * @param path    the target file to write
     * @param content the file content
     * @throws IOException if an IO error occurs
     */
    private void writeAtomically(Path path, String content) throws IOException {
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

}