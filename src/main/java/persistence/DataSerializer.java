package persistence;

import java.io.IOException;

/**
 * Interfaz que abstrae el mecanismo de persistencia.
 * Permite guardar y cargar el estado completo de la aplicación.
 */
public interface DataSerializer {

    /**
     * Guarda el estado actual de equipos y jugadores.
     */
    void save(SoccerData data) throws IOException;

    /**
     * Carga el snapshot del sistema
     */
    SoccerData load() throws IOException;
}