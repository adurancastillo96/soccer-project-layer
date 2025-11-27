package repository;

import model.Player;
import model.Team;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

/**
 * Repository interface for managing teams and their players. Implementations
 * of this interface abstract away the underlying storage mechanism (memory,
 * database, etc.).
 */
public interface TeamRepository {

    /**
     * Persists the given team in the repository. If a team with the same ID
     * already exists it is replaced.
     *
     * @param team the team to save
     * @return the saved team
     */
    void saveTeam(Team team);
    void saveTeams(List<Team> teamList);

    /**
     * Retrieves a team by its identifier.
     *
     * @param teamId the team's unique identifier
     * @return an optional containing the team if found
     */
    Optional<Team> findTeam(UUID teamId);

    /**
     * Removes the team with the given identifier from the repository.
     *
     * @param teamId the team's unique identifier
     */
    void deleteTeam(UUID teamId);

    /**
     * Returns a collection of all teams in the repository.
     *
     * @return all teams
     */
    Collection<Team> findAllTeams();

    /**
     * Adds the given player to the team identified by teamId.
     *
     * @param player the player to add
     */
    void savePlayer(Player player);
    void savePlayers(List<Player> playersList);

    /**
     * Retrieves a player by its identifier.
     *
     * @param playerId the player's unique identifier
     * @return an optional containing the player if found
     */
    Optional<Player> findPlayer(UUID playerId);

    /**
     * Removes the player with playerId from the specified team. If either
     * identifier does not exist the operation is silently ignored.
     *
     * @param playerId the player identifier
     */
    void deletePlayer(UUID playerId);
    Collection<Player> findAllPlayers();

    /**
     * Returns a list of all players belonging to the specified team.
     *
     * @param teamId the team identifier
     * @return list of players (possibly empty)
     */
    List<Player> findPlayersByTeam(UUID teamId);
    //void addPlayerToTeam(UUID teamId, Player player); // Creo que se debe eliminar

}
