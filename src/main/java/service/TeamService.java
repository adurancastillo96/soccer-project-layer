package service;

import model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {

    /**
     * Creates a new team with the given attributes and persists it.
     *
     * @param name      team name
     * @param city      team city
     * @param coach     coach name
     * @param formation preferred formation
     * @return the created Team
     */
    Team createTeam(String name, String city, String coach, String formation);

    /**
     * Removes the team with the given identifier.
     *
     * @param teamId team identifier
     */
    void deleteTeam(UUID teamId);

    /**
     * Finds a team by its identifier.
     *
     * @param teamId identifier of the team
     * @return the team if found
     * @throws domain.DomainException if not found
     */
    Optional<Team> findTeam(UUID teamId);

    /**
     * Returns a list of all teams.
     *
     * @return list of teams
     */
    List<Team> findAllTeams();

    /**
     * Simulates a match between two teams. Updates the number of matches won
     * depending on the outcome determined by the simulator.
     *
     * @param teamIdA id of the first team
     * @param teamIdB id of the second team
     * @throws domain.DomainException if any team is not found
     */
    Optional<Team> simulateMatch(UUID teamIdA, UUID teamIdB);
}
