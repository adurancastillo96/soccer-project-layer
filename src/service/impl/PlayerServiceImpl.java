package service.impl;

import domain.DomainErrorCode;
import domain.DomainException;
import events.PlayerAddedToTeamEvent;
import events.PlayerDeletedFromTeamEvent;
import events.bus.EventBus;
import model.Player;
import model.Team;
import repository.TeamRepository;
import service.PlayerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerServiceImpl implements PlayerService {
    private final TeamRepository repository;
    private final EventBus eventBus;

    public PlayerServiceImpl(TeamRepository repository, EventBus eventBus) {
        this.repository = repository;
        this.eventBus = eventBus;
    }

    @Override
    public void addPlayer(Player player) {
        Team team = repository.findTeam(player.getTeamId())
                .orElseThrow(() -> new DomainException(DomainErrorCode.TEAM_NOT_FOUND, "Equipo del jugador no encontrado: " + player.getTeamId()));

        // Ensure no duplicate squad numbers in the same team
        boolean duplicate = repository.findPlayersByTeam(player.getTeamId())
                .stream()
                .anyMatch(p -> p.getSquadNumber() == player.getSquadNumber());

        if (duplicate) {
            throw new DomainException(DomainErrorCode.INVALID_SQUAD_NUMBER, "Dorsal " +  player.getSquadNumber() + " está ya cogido en el equipo " + team.getName());
        }
        repository.savePlayer(player); // persist updated team

        // publish event
        eventBus.publish(new PlayerAddedToTeamEvent(player.getTeamId(), player.getPlayerId(), player.getSquadNumber()));

    }

    @Override
    public void deletePlayer(UUID playerId) {
        Optional<Player> player = repository.findPlayer(playerId);
        if (player.isEmpty()) {
            throw new DomainException(DomainErrorCode.PLAYER_NOT_FOUND, "Equipo no encontrado");
        } else repository.deletePlayer(playerId);
        eventBus.publish(new PlayerDeletedFromTeamEvent(player.get().getTeamId(), playerId));
    }

    @Override
    public void deletePlayers(UUID teamId) {
        // Delete all players related to the team.
        List<Player> players = repository.findPlayersByTeam(teamId);
        players.forEach(player -> {deletePlayer(player.getPlayerId());});
    }

    @Override
    public Optional<Player> getPlayer(UUID playerId) {
        return repository.findPlayer(playerId);
    }

    @Override
    public List<Player> getPlayersOfTeam(UUID teamId) {
        List<Player> players = repository.findPlayersByTeam(teamId);
        if (players == null) return List.of();
        return players;
    }
}
