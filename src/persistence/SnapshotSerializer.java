package persistence;

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
public class SnapshotSerializer {
    private final Path teamsCsvPath;
    private final Path playersCsvPath;
    private final Path teamsJsonPath;
    private final Path playersJsonPath;

    public SnapshotSerializer(Path teamsCsvPath, Path playersCsvPath, Path teamsJsonPath, Path playersJsonPath) {
        this.teamsCsvPath = teamsCsvPath;
        this.playersCsvPath = playersCsvPath;
        this.teamsJsonPath = teamsJsonPath;
        this.playersJsonPath = playersJsonPath;
    }

    /**
     * Writes the given teams and their players to CSV files using a semicolon
     * as the delimiter. Team data goes into the teams CSV and player data
     * into the players CSV. Writes are atomic via a temporary file.
     *
     * @param teams the current list of teams
     * @throws IOException if any IO error occurs
     */
    public void saveSnapshotToCsv(List<Team> teams, List<Player> players) throws IOException {
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
     * Writes the given teams and players into a single JSON file. The JSON
     * structure is a dictionary with a single key {@code "teams"} holding a
     * list of teams with nested player lists. Writes are atomic via a
     * temporary file.
     *
     * @param teams the current list of teams
     * @throws IOException if an IO error occurs
     */
    public void saveSnapshotToJson(List<Team> teams, List<Player> players) throws IOException {
        String jsonTeams = toJsonTeams(teams);
        String jsonPlayers = toJsonPlayers(players);
        writeAtomically(teamsJsonPath, jsonTeams);
        writeAtomically(playersJsonPath, jsonPlayers);
    }

    /**
     * Reads teams and players from existing CSV files. If neither file exists
     * then an empty list is returned. Both files must be present; if one
     * exists without the other the method treats it as no data.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    public List<Team> loadTeamsSnapshotFromCsv() throws IOException {
        if (!Files.exists(teamsCsvPath)) {
            return new ArrayList<>();
        }
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
    public List<Player> loadPlayersSnapshotFromCsv() throws IOException {
        if (!Files.exists(playersCsvPath)) {
            return new ArrayList<>();
        }
        Map<UUID, Player> playerMap = new HashMap<>();
        // Read players
        try (BufferedReader reader = Files.newBufferedReader(playersCsvPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 7) continue;
                UUID playerId = UUID.fromString(parts[0]);
                UUID teamId = UUID.fromString(parts[1]);
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

    /**
     * Reads teams and players from the JSON snapshot file. If the file does not
     * exist an empty list is returned. Only the exact structure produced by
     * {@link #saveSnapshotToJson(List, List)} is supported.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    public List<Team> loadTeamsSnapshotFromJson() throws IOException {
        if (!Files.exists(teamsJsonPath)) {
            return new ArrayList<>();
        }
        String json;
        try (Reader reader = Files.newBufferedReader(teamsJsonPath, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[2048];
            int n;
            while ((n = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, n);
            }
            json = sb.toString();
        }
        JsonParser parser = new JsonParser(json);
        Object parsed = parser.parse();
        if (!(parsed instanceof Map)) {
            return new ArrayList<>();
        }
        Map<?, ?> root = (Map<?, ?>) parsed;
        Object teamsObj = root.get("teams");
        if (!(teamsObj instanceof List)) {
            return new ArrayList<>();
        }
        List<?> teamsList = (List<?>) teamsObj;
        List<Team> teams = new ArrayList<>();
        for (Object tObj : teamsList) {
            if (!(tObj instanceof Map)) continue;
            Map<?, ?> tMap = (Map<?, ?>) tObj;
            UUID teamId = UUID.fromString((String) tMap.get("teamId"));
            String name = (String) tMap.get("name");
            String city = (String) tMap.get("city");
            String coach = (String) tMap.get("coach");
            String formation = (String) tMap.get("formation");
            int matchesWon = ((Number) tMap.get("matchesWon")).intValue();
            Team team = new Team(name, city, coach, formation);
            team.setTeamId(teamId);
            team.setMatchesWon(matchesWon);
            teams.add(team);
        }
        return teams;
    }

    /**
     * Reads teams and players from the JSON snapshot file. If the file does not
     * exist an empty list is returned. Only the exact structure produced by
     * {@link #saveSnapshotToJson(List, List)} is supported.
     *
     * @return the list of deserialized teams
     * @throws IOException if an IO error occurs while reading
     */
    public List<Player> loadPlayersSnapshotFromJson() throws IOException {
        if (!Files.exists(playersJsonPath)) {
            return new ArrayList<>();
        }
        String json;
        try (Reader reader = Files.newBufferedReader(playersJsonPath, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[2048];
            int n;
            while ((n = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, n);
            }
            json = sb.toString();
        }
        JsonParser parser = new JsonParser(json);
        Object parsed = parser.parse();
        if (!(parsed instanceof Map)) {
            return new ArrayList<>();
        }
        Map<?, ?> root = (Map<?, ?>) parsed;
        Object playersObj = root.get("players");
        if (!(playersObj instanceof List)) {
            return new ArrayList<>();
        }
        List<?> playersList = (List<?>) playersObj;
        List<Player> players = new ArrayList<>();
        for (Object pObj : playersList) {
            if (!(pObj instanceof Map)) continue;
            Map<?, ?> pMap = (Map<?, ?>) pObj;
            UUID teamId = UUID.fromString((String) pMap.get("teamId"));
            UUID playerId = UUID.fromString((String) pMap.get("playerId"));
            String fullName = (String) pMap.get("fullName");
            int birthYear = ((Number) pMap.get("birthYear")).intValue();
            String fieldLocation = (String) pMap.get("fieldLocation");
            int squadNumber = ((Number) pMap.get("squadNumber")).intValue();
            int goals = ((Number) pMap.get("goals")).intValue();
            Player player = new Player(teamId, fullName, birthYear, fieldLocation, squadNumber);
            player.setGoals(goals);
            player.setPlayerId(playerId);
            players.add(player);
        }
        return players;
    }

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
        if (needsQuotes) {
            return '"' + escaped + '"';
        }
        return escaped;
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

    /**
     * Serialises the given list of teams to a JSON string. The produced JSON
     * contains a single root object with an array named "teams". All
     * strings are properly escaped and numeric values remain numeric.
     *
     * @param teams the teams to serialise
     * @return a JSON string
     */
    private String toJsonTeams(List<Team> teams) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"teams\":[");
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            sb.append('{');
            sb.append("\"teamId\":\"").append(team.getTeamId()).append('\"');
            sb.append(',').append("\"name\":\"").append(escapeJson(team.getName())).append('\"');
            sb.append(',').append("\"city\":\"").append(escapeJson(team.getCity())).append('\"');
            sb.append(',').append("\"coach\":\"").append(escapeJson(team.getCoach())).append('\"');
            sb.append(',').append("\"formation\":\"").append(escapeJson(team.getFormation())).append('\"');
            sb.append(',').append("\"matchesWon\":").append(team.getMatchesWon());
            sb.append('}');
            if (i < teams.size() - 1) {
                sb.append(',');
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Serialises the given list of teams to a JSON string. The produced JSON
     * contains a single root object with an array named "teams". All
     * strings are properly escaped and numeric values remain numeric.
     *
     * @param players the teams to serialise
     * @return a JSON string
     */
    private String toJsonPlayers(List<Player> players) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"players\":[");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            sb.append('{');
            sb.append("\"teamId\":\"").append(p.getTeamId()).append('\"');
            sb.append("\"playerId\":\"").append(p.getPlayerId()).append('\"');
            sb.append(',').append("\"fullName\":\"").append(escapeJson(p.getFullName())).append('\"');
            sb.append(',').append("\"birthYear\":").append(p.getBirthYear());
            sb.append(',').append("\"fieldLocation\":\"").append(escapeJson(p.getFieldLocation())).append('\"');
            sb.append(',').append("\"squadNumber\":").append(p.getSquadNumber());
            sb.append(',').append("\"goals\":").append(p.getGoals());
            sb.append('}');
            if (i < players.size() - 1) {
                sb.append(',');
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Escapes a string for use in JSON. Handles control characters and
     * backslash/quote escaping. Only the escapes used in our dataset are
     * implemented.
     *
     * @param value the raw value
     * @return the escaped value
     */
    private static String escapeJson(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (ch < 0x20 || ch > 0x7E) {
                        // Unicode escape
                        sb.append(String.format("\\u%04x", (int) ch));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * A very small JSON parser capable of parsing objects, arrays, strings
     * and numbers. It does not support booleans, null or scientific notation.
     * It is intentionally minimal and assumes valid input according to the
     * structure produced by {@link #toJsonTeams(List)} {@link #toJsonPlayers(List)}.
     */
    private static class JsonParser {
        private final String input;
        private int pos;

        JsonParser(String input) {
            this.input = input;
        }

        Object parse() {
            skipWhitespace();
            Object value = parseValue();
            skipWhitespace();
            return value;
        }

        private Object parseValue() {
            skipWhitespace();
            if (pos >= input.length()) return null;
            char ch = input.charAt(pos);
            if (ch == '{') {
                return parseObject();
            }
            if (ch == '[') {
                return parseArray();
            }
            if (ch == '"') {
                return parseString();
            }
            // number
            return parseNumber();
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> obj = new HashMap<>();
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return obj;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                Object value = parseValue();
                obj.put(key, value);
                skipWhitespace();
                char ch = expect(',','}');
                if (ch == '}') {
                    break;
                }
            }
            return obj;
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return list;
            }
            while (true) {
                skipWhitespace();
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char ch = expect(',',']');
                if (ch == ']') {
                    break;
                }
            }
            return list;
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < input.length()) {
                char ch = input.charAt(pos++);
                if (ch == '"') {
                    break;
                }
                if (ch == '\\') {
                    // Escape sequence
                    if (pos >= input.length()) break;
                    char esc = input.charAt(pos++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            // Unicode escape
                            if (pos + 3 < input.length()) {
                                String hex = input.substring(pos, pos + 4);
                                try {
                                    int code = Integer.parseInt(hex, 16);
                                    sb.append((char) code);
                                    pos += 4;
                                } catch (NumberFormatException e) {
                                    // If invalid, append literally
                                    sb.append("\\u").append(hex);
                                    pos += 4;
                                }
                            }
                            break;
                        default:
                            sb.append(esc);
                    }
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }

        private Number parseNumber() {
            int start = pos;
            while (pos < input.length()) {
                char ch = input.charAt(pos);
                if ((ch >= '0' && ch <= '9') || ch == '-') {
                    pos++;
                } else {
                    break;
                }
            }
            String num = input.substring(start, pos);
            try {
                // parse as integer
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                try {
                    return Long.parseLong(num);
                } catch (NumberFormatException ex) {
                    return Double.parseDouble(num);
                }
            }
        }

        private void skipWhitespace() {
            while (pos < input.length()) {
                char ch = input.charAt(pos);
                if (Character.isWhitespace(ch)) {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private char peek() {
            return pos < input.length() ? input.charAt(pos) : '\0';
        }

        private char expect(char c1, char c2) {
            if (pos >= input.length()) throw new RuntimeException("Unexpected end of JSON");
            char ch = input.charAt(pos++);
            if (ch == c1 || ch == c2) {
                return ch;
            }
            throw new RuntimeException("Expected '" + c1 + "' or '" + c2 + "' but found '" + ch + "'");
        }

        private void expect(char ch) {
            if (pos >= input.length() || input.charAt(pos) != ch) {
                throw new RuntimeException("Expected '" + ch + "' at position " + pos);
            }
            pos++;
        }
    }
}