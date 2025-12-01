package events;

import java.util.UUID;

/**
 * Event published when a new player has been added to a team.
 */
public class PlayerAddedToTeamEvent implements DomainEvent {
    private final UUID teamId, playerId;
    private final int squadNumber;

    public PlayerAddedToTeamEvent(UUID teamId, UUID playerId, int squadNumber) {
        this.teamId = teamId;
        this.playerId = playerId;
        this.squadNumber = squadNumber;
    }

    public UUID getTeamId() { return this.teamId; }
    public UUID getPlayerId() { return this.playerId; }
    public int getSquadNumber() { return this.squadNumber; }

}
