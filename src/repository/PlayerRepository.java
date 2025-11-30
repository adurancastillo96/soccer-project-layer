package repository;

import model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing teams and their players. Implementations
 * of this interface abstract away the underlying storage mechanism (memory,
 * database, etc.).
 */
public interface PlayerRepository {

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
    List<Player> findAllPlayers();

    /**
     * Returns a list of all players belonging to the specified team.
     *
     * @param teamId the team identifier
     * @return list of players (possibly empty)
     */
    List<Player> findPlayersByTeam(UUID teamId);

}
