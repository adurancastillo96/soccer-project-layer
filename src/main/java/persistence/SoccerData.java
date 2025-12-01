package persistence;

import model.Player;
import model.Team;

import java.util.List;

/**
 * Clase contenedora (Snapshot) que representa el estado completo del sistema
 * en un momento dado. Se usa para transferir datos entre la persistencia y el dominio.
 */
public class SoccerData {
    private final List<Team> teams;
    private final List<Player> players;

    public SoccerData(List<Team> teams, List<Player> players) {
        this.teams = teams;
        this.players = players;
    }

    public List<Team> getTeams() { return teams; }
    public List<Player> getPlayers() { return players; }
}