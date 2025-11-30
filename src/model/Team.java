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
                        "- Victorias: %d",
                teamId, name, city, coach, formation, matchesWon);
    }

    // -------- METODOS --------
    public void incrementMatchesWon() { this.matchesWon++; }

    @Override
    public String toString() {
        return this.getSummary();
    }
}