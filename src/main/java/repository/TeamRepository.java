package repository;

import model.Team;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

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
    List<Team> findAllTeams();

}
