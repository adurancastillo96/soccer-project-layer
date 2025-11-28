package service;

import model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerService {

    /**
     * Adds a new player to the specified team.
     *
     * @param player player
     * @throws domain.DomainException if team not found
     * @throws domain.DomainException if squad number is invalid or duplicates existing player
     */
    void addPlayer(Player player);

    /**
     * Removes a player from a team.
     *
     * @param playerId identifier of the player to remove
     * @throws domain.DomainException if team not found
     * @throws domain.DomainException if player not found
     */
    void deletePlayer(UUID playerId);

    /**
     * Removes a player from a team.
     *
     * @param teamId identifier of the player to remove
     * @throws domain.DomainException if team not found
     * @throws domain.DomainException if player not found
     */
    void deletePlayers(UUID teamId);

    /**
     * Finds a player by their identifier across all teams.
     *
     * @param playerId identifier of the player
     * @return the Optional<Player>
     * @throws domain.DomainException if player not found
     */
    Optional<Player> getPlayer(UUID playerId);

    /**
     * Finds a List of players by their team identifier.
     *
     * @param teamId identifier of the team
     * @return the List<Player>
     * @throws domain.DomainException if player not found
     */
    List<Player> getPlayers(UUID teamId);
}
