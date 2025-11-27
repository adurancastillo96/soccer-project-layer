package repository;

import model.Player;
import model.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria de TeamRepository.
 * Usa Concurrent Maps - Thread-Safe
 */
public class InMemoryTeamRepository implements TeamRepository {
    private final Map<UUID, Team> teams = new ConcurrentHashMap<>();
    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    @Override
    public void saveTeam(Team team) {
        teams.put(team.getTeamId(), team);
    }

    @Override
    public void saveTeams(List<Team> teamList) {
        teamList.forEach(this::saveTeam);
    }

    @Override
    public Optional<Team> findTeam(UUID teamId) {
        return Optional.ofNullable(teams.get(teamId));
    }

    @Override
    public void deleteTeam(UUID teamId) {
        Team removed = teams.remove(teamId);
        if (removed != null) {
            players.values().removeIf(player -> player.getTeamId().equals(teamId));
        }
    }

    @Override
    public Collection<Team> findAllTeams() {
        // return new ArrayList<>(teams.values());
        return Collections.unmodifiableCollection(teams.values());
    }

    @Override
    public void savePlayer(Player player) {
        players.put(player.getPlayerId(), player);
    }

    @Override
    public void savePlayers(List<Player> playersList) {
        playersList.forEach(this::savePlayer);
    }

    @Override
    public Optional<Player> findPlayer(UUID playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

    @Override
    public void deletePlayer(UUID playerId) {
        players.remove(playerId);
    }

    @Override
    public Collection<Player> findAllPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public List<Player> findPlayersByTeam(UUID teamId) {
        return players.values().stream().filter(player -> player.getTeamId().equals(teamId)).toList();
    }

    //@Override
    //public void addPlayerToTeam(UUID teamId, Player player) {
    //    Team team = teams.get(teamId);
    //    if (team != null) {
    //        players.put(player.getPlayerId(), player);
    //    }
    //}

    //public int count() {
    //    return teams.size();
    //}

}
