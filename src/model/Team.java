package model;

import domain.DomainErrorCode;
import domain.DomainException;

import java.util.UUID;

public class Team {

    // -------- CONSTANTES DE VALIDACIÓN --------
    private static final int STRING_MAX   = 60;

    // -------- ATRIBUTOS --------
    private UUID teamId;
    private String name, city, coach, formation;
    private int matchesWon;
    //private final List<Player> players;

    // -------- CONSTRUCTOR --------
    public Team(String name, String city, String coach, String formation) {
        this.teamId = UUID.randomUUID();
        this.setName(name);
        this.setCity(city);
        this.setCoach(coach);
        this.setFormation(formation);
        this.setMatchesWon(0);
    }

    // -------- GETTERS --------
    public UUID getTeamId() { return teamId; }
    public String getName(){ return name; }
    public String getCity(){ return city; }
    public String getCoach(){ return coach; }
    public String getFormation(){ return formation; }
    public int getMatchesWon(){ return matchesWon; }
    //public int size() { return players.size(); }
    //public List<Player> getPlayers() { return List.copyOf(players); }

    // -------- SETTERS (con validación) --------
    public void setTeamId(UUID teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("El ID del equipo no puede ser nulo.");
        }
        this.teamId = teamId;
    }
    public void setName(String name) { this.name = validateText(name); }
    public void setCity(String city) { this.city = validateText(city); }
    public void setCoach(String coach) { this.coach = validateText(coach); }
    public void setFormation(String formation) { this.formation = validateText(formation); }

    /** Los partidos ganados no son negativas */
    public void setMatchesWon(int matchesWon) {
        if (matchesWon < 0) throw new DomainException(DomainErrorCode.VALIDATION_ERROR, "Las victorias no pueden ser negativas.");
        this.matchesWon = matchesWon;
    }

    // -------- UTILIDADES --------
    private static String validateText(String value) {
        if (value == null || value.isBlank()) throw new DomainException(DomainErrorCode.VALIDATION_ERROR, "Entrada inválida.");
        String trimmed = value.trim();
        int len = trimmed.length();
        if (len > STRING_MAX) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, "Fuera de rango.");
        }
        return trimmed;
    }

    public String getSummary() {
        return String.format("Información del equipo:\n" +
                        "- Equipo ID: %s\n" +
                        "- Nombre: %s\n" +
                        "- Ciudad: %s\n" +
                        "- Entrenador: %s\n" +
                        "- Formación: %s\n" +
                        "- Victorias: %d\n",
                teamId, name, city, coach, formation, matchesWon);
    }

    // -------- METODOS --------
    public void incrementMatchesWon() { this.matchesWon++; }

    /** Añade un jugador si no existe otro con el mismo nombre (case-insensitive).
     * Retorna un boolean*/
    //public boolean addPlayer(Player p) {
    //    if (p == null) throw new IllegalArgumentException("El jugador no puede ser null.");
    //    boolean duplicateName = players.stream()
    //            .anyMatch(existing -> existing.getFullName().equalsIgnoreCase(p.getFullName()));
    //    if (duplicateName) return false;
    //    return players.add(p);
    //}

    /** Elimina un jugador por su UUID. */
    //public boolean removePlayerById(UUID playerId) {
    //    if (playerId == null) throw new IllegalArgumentException("El ID del jugador no puede ser null.");
    //    return players.removeIf(pl -> pl.getId().equals(playerId));
    //}

    /** Busca un jugador por su UUID. Devuelve null si no existe. */
    // Alternativa: return players.stream().filter(p -> p.getId().equals(playerId)).findFirst();
    //public Player findPlayer(UUID playerId) {
    //    if (playerId == null) throw new IllegalArgumentException("El ID del jugador no puede ser null.");
    //    for (Player p : players) {
    //        if (p.getId().equals(playerId)) return p;
    //    }
    //    return null;
    //}

    @Override
    public String toString() {
        return this.getSummary();
    }
}