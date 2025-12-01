package events;

import java.util.UUID;

/**
 * Event published when a match simulation has been completed.
 */
public class MatchSimulatedEvent implements DomainEvent {
    private final UUID teamAId, teamBId;
    private final int goalsA,  goalsB;

    public MatchSimulatedEvent(UUID teamA, UUID teamB, int goalsA, int goalsB) {
        this.teamAId = teamA;
        this.teamBId = teamB;
        this.goalsA = goalsA;
        this.goalsB = goalsB;
    }

    public UUID getTeamAId() { return this.teamAId; }
    public UUID getTeamBId() { return this.teamBId; }
    public int getGoalsA() { return this.goalsA; }
    public int getGoalsB() { return this.goalsB; }

}
