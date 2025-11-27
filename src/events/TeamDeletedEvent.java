package events;

import java.util.UUID;

/**
 * Event published when a team has been removed from the system.
 */
public class TeamDeletedEvent implements DomainEvent {
    private final UUID teamId;

    public TeamDeletedEvent(UUID teamId) { this.teamId = teamId; }

    public UUID getTeamId() { return this.teamId; }
}
