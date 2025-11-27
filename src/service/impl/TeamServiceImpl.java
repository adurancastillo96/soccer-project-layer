package service.impl;

import domain.DomainErrorCode;
import domain.DomainException;
import events.MatchSimulatedEvent;
import events.TeamCreatedEvent;
import events.TeamDeletedEvent;
import events.bus.EventBus;
import model.Player;
import model.Team;
import repository.TeamRepository;
import service.TeamService;

import java.util.*;

public class TeamServiceImpl implements TeamService {
    private final TeamRepository repository;
    private final EventBus eventBus;
    //private final MatchSimulator simulator;

    public TeamServiceImpl(TeamRepository repository, EventBus eventBus) {
        this.repository = repository;
        this.eventBus = eventBus;
        //this.simulator = simulator;
    }

    @Override
    public Team createTeam(String name, String city, String coach, String formation) {
        Team team = new Team(name, city, coach, formation);
        repository.saveTeam(team);

        // publish event
        eventBus.publish(new TeamCreatedEvent(team.getTeamId(), name, city, coach, formation));
        return team;
    }

    @Override
    public void deleteTeam(UUID teamId) {
        Optional<Team> existing = repository.findTeam(teamId);
        if (existing.isEmpty()) {
            throw new DomainException(DomainErrorCode.TEAM_NOT_FOUND, "Equipo "+ teamId +" no encontrado.");
        }
        repository.deleteTeam(teamId);
        eventBus.publish(new TeamDeletedEvent(teamId));
    }

    @Override
    public Optional<Team> getTeam(UUID teamId) {
        Optional<Team> existing = repository.findTeam(teamId);
        if (existing.isEmpty()) {
            throw new DomainException(DomainErrorCode.TEAM_NOT_FOUND, "Equipo "+ teamId +" no encontrado.");
        }
        return existing;
    }

    @Override
    public Collection<Team> getAllTeams() { return repository.findAllTeams(); }
    // public List<Team> getAllTeams() { return new ArrayList<>(repository.findAllTeams()); }

    @Override
    public Optional<Team> simulateMatch(UUID teamAid, UUID teamBid) {
        Random random = new Random();
        int outcome = random.nextInt(3); // 0=draw,1=teamA,2=teamB
        int goalsA = generateGoals(teamAid);
        int goalsB = generateGoals(teamBid);
        if (goalsA > goalsB) {
            Optional<Team> teamA = repository.findTeam(teamAid);
            teamA.ifPresent(Team::incrementMatchesWon);
            teamA.ifPresent(repository::saveTeam);
            // Publish event with result
            eventBus.publish(new MatchSimulatedEvent(teamAid, teamBid, goalsA, goalsB));
            return teamA;
        } else if (goalsB > goalsA) {
            Optional<Team> teamB = repository.findTeam(teamBid);
            teamB.ifPresent(Team::incrementMatchesWon);
            teamB.ifPresent(repository::saveTeam);
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
        List<Player> players = repository.findPlayersByTeam(teamId);
        for (Player player : players) {
            boolean outcome  = random.nextBoolean(); // true=goal
            if (outcome) {
                player.incrementGoals();
                repository.savePlayer(player);
                goals++;
            }
        }
        return goals;
    }
}
