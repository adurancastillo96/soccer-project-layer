package ui;

/** ConsoleMenu invoca estos metodos para realizar las operaciones reales*/
public interface AppController {
    void createTeam();
    void addPlayerToTeam();
    void showPlayerInfo();
    void showTeamPlayers();
    void showTeamInfo();
    void removePlayerFromTeam();
    void removeTeam();
    void showSummary();
    void simulateMatch();
    void exitRequested();
}
