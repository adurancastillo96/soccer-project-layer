package events;

import java.util.UUID;

/**
 * Event published when a player has been removed from a team.
 */
public class PlayerDeletedFromTeamEvent implements DomainEvent {
    private final UUID teamId, playerId;

    public PlayerDeletedFromTeamEvent(UUID teamId, UUID playerId) {
        this.teamId = teamId;
        this.playerId = playerId;
    }

    public UUID getTeamId() { return this.teamId; }
    public UUID getPlayerId() { return this.playerId; }
}
