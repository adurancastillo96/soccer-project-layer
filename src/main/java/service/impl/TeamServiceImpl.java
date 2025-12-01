package service.impl;

import domain.DomainErrorCode;
import domain.DomainException;
import events.MatchSimulatedEvent;
import events.TeamCreatedEvent;
import events.TeamDeletedEvent;
import events.bus.EventBus;
import model.Player;
import model.Team;
import repository.PlayerRepository;
import repository.TeamRepository;
import service.TeamService;

import java.util.*;

public class TeamServiceImpl implements TeamService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final EventBus eventBus;

    public TeamServiceImpl(PlayerRepository playerRepository ,TeamRepository teamRepository, EventBus eventBus) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Team createTeam(String name, String city, String coach, String formation) {
        Team team = new Team(name, city, coach, formation);
        teamRepository.saveTeam(team);

        // publish event
        eventBus.publish(new TeamCreatedEvent(team.getTeamId(), name, city, coach, formation));
        return team;
    }

    @Override
    public void deleteTeam(UUID teamId) {
        Optional<Team> existing = teamRepository.findTeam(teamId);
        if (existing.isEmpty()) {
            throw new DomainException(DomainErrorCode.TEAM_NOT_FOUND, "Equipo "+ teamId +" no encontrado.");
        }
        teamRepository.deleteTeam(teamId);
        eventBus.publish(new TeamDeletedEvent(teamId, existing.get().getName()));
    }

    @Override
    public Optional<Team> findTeam(UUID teamId) {
        Optional<Team> existing = teamRepository.findTeam(teamId);
        if (existing.isEmpty()) {
            throw new DomainException(DomainErrorCode.TEAM_NOT_FOUND, "Equipo "+ teamId +" no encontrado.");
        }
        return existing;
    }

    @Override
    public List<Team> findAllTeams() { return teamRepository.findAllTeams(); }

    @Override
    public Optional<Team> simulateMatch(UUID teamAid, UUID teamBid) {
        int goalsA = generateGoals(teamAid);
        int goalsB = generateGoals(teamBid);
        if (goalsA > goalsB) {
            Optional<Team> teamA = teamRepository.findTeam(teamAid);
            teamA.ifPresent(Team::incrementMatchesWon);
            teamA.ifPresent(teamRepository::saveTeam);
            // Publish event with result
            eventBus.publish(new MatchSimulatedEvent(teamAid, teamBid, goalsA, goalsB));
            return teamA;
        } else if (goalsB > goalsA) {
            Optional<Team> teamB = teamRepository.findTeam(teamBid);
            teamB.ifPresent(Team::incrementMatchesWon);
            teamB.ifPresent(teamRepository::saveTeam);
            // Publish event with result
            eventBus.publish(new MatchSimulatedEvent(teamAid, teamBid, goalsA, goalsB));
            return teamB;
        }
        return Optional.empty();
    }

    // UTILS
    private int generateGoals(UUID teamId) {
        Random random = new Random();
        int goals = 0;
        List<Player> players = playerRepository.findPlayersByTeam(teamId);
        for (Player player : players) {
            boolean outcome  = random.nextBoolean(); // true=goal
            if (outcome) {
                player.incrementGoals();
                playerRepository.savePlayer(player);
                goals++;
            }
        }
        return goals;
    }
}
