import java.util.List;

public interface SoccerDatabase {
    /**
     * save(List<Team> Teams)
     * Guarda el archivo*/
    void save(List<Team> teams);

    /** load()
     * Carga el archivo*/
    List<Team> load();

}
