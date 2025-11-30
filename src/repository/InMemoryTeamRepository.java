package repository;

import model.Player;
import model.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria de TeamRepository.
 * Usa Concurrent Maps - Thread-Safe
 */
public class InMemoryTeamRepository implements TeamRepository, PlayerRepository {
    private final Map<UUID, Team> teams = new ConcurrentHashMap<>();
    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    // --- MÉTODOS DE TEAM REPOSITORY ---
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
    public List<Team> findAllTeams() {
        return List.copyOf(teams.values());
    }

    // --- MÉTODOS DE PLAYER REPOSITORY ---
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
    public List<Player> findAllPlayers() {
        return List.copyOf(players.values());
    }

    @Override
    public List<Player> findPlayersByTeam(UUID teamId) {
        return players.values().stream()
                .filter(player -> player.getTeamId().equals(teamId))
                .toList();
    }

}
