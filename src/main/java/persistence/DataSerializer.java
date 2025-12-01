package persistence;

import model.Player;
import model.Team;

import java.io.IOException;
import java.util.List;

/**
 * Interfaz que abstrae el mecanismo de persistencia.
 * Permite guardar y cargar el estado completo de la aplicación.
 */
public interface DataSerializer {

    /**
     * Guarda el estado actual de equipos y jugadores.
     */
    void save(List<Team> teams, List<Player> players) throws IOException;

    /**
     * Carga la lista de equipos.
     */
    List<Team> loadTeams() throws IOException;

    /**
     * Carga la lista de jugadores.
     */
    List<Player> loadPlayers() throws IOException;
}