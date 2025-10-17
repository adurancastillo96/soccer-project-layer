package persistence;

import model.Team;
import java.util.List;

public interface SoccerDatabase {
    /**
     * save(List<model.Team> Teams)
     * Guarda el archivo*/
    void save(List<Team> teams);

    /** load()
     * Carga el archivo*/
    List<Team> load();

}
