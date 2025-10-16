import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    // -------- CONSTANTES DE VALIDACIÓN --------
    private static final int STRING_MAX   = 60;

    // -------- ATRIBUTOS --------
    private final UUID id;
    private String name;
    private String city;
    private String coach;
    private String formation;
    private int matchesWon;

    private final List<Player> players;

    // -------- CONSTRUCTOR --------
    public Team(String name, String city, String coach, String formation) {
        this.id = UUID.randomUUID();
        this.setName(name);
        this.setCity(city);
        this.setCoach(coach);
        this.setFormation(formation);
        this.matchesWon = 0;
        this.players = new ArrayList<>();
    }

    public Team(UUID id, String name, String city, String coach, String formation, int matchesWon) {
        this.id = id;
        this.setName(name);
        this.setCity(city);
        this.setCoach(coach);
        this.setFormation(formation);
        this.matchesWon = matchesWon;
        this.players = new ArrayList<>();
    }

    // -------- GETTERS --------
    public UUID getId() { return id; }
    public String getName(){ return name; }
    public String getCity(){ return city; }
    public String getCoach(){ return coach; }
    public String getFormation(){ return formation; }
    public int getMatchesWon(){ return matchesWon; }
    public int size() { return players.size(); }
    public List<Player> getPlayers() { return List.copyOf(players); }

    // -------- SETTERS (con validación) --------
    public void setName(String name) { this.name = validateText(name, STRING_MAX); }
    public void setCity(String city) { this.city = validateText(city, STRING_MAX); }
    public void setCoach(String coach) { this.coach = validateText(coach, STRING_MAX); }
    public void setFormation(String formation) { this.formation = validateText(formation, STRING_MAX); }

    /** Los partidos ganados no son negativas */
    public void setMatchesWon(int matchesWon) {
        if (matchesWon < 0) throw new IllegalArgumentException("Las victorias no pueden ser negativas.");
        this.matchesWon = matchesWon;
    }

    // -------- UTILIDADES --------
    private static String validateText(String value, int max) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Entrada inválida.");
        String trimmed = value.trim();
        int len = trimmed.length();
        if (len > max) {
            throw new IllegalArgumentException("Fuera de rango.");
        }
        return trimmed;
    }

    public String getSummary() {
        return String.format("ID:%s | %s | Ciudad:%s | Entrenador:%s | Formación:%s | Jugadores=%d | Victorias=%d",
                id, name, city, coach, formation, players.size(), matchesWon);
    }

    // -------- METODOS --------

    /** Añade un jugador si no existe otro con el mismo nombre (case-insensitive).
     * Retorna un boolean*/
    public boolean addPlayer(Player p) {
        if (p == null) throw new IllegalArgumentException("El jugador no puede ser null.");
        boolean duplicateName = players.stream()
                .anyMatch(existing -> existing.getFullName().equalsIgnoreCase(p.getFullName()));
        if (duplicateName) return false;
        return players.add(p);
    }

    /** Elimina un jugador por su UUID. */
    public boolean removePlayerById(UUID playerId) {
        if (playerId == null) throw new IllegalArgumentException("El ID del jugador no puede ser null.");
        return players.removeIf(pl -> pl.getId().equals(playerId));
    }

    /** Busca un jugador por su UUID. Devuelve null si no existe. */
    public Player findPlayerById(UUID playerId) {
        if (playerId == null) throw new IllegalArgumentException("El ID del jugador no puede ser null.");
        for (Player p : players) {
            if (p.getId().equals(playerId)) return p;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Equipo{" + getSummary() + "}";
    }
}