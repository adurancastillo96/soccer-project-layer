package events;

import java.util.UUID;

/**
 * Event published when a team has been removed from the system.
 */
public class TeamDeletedEvent implements DomainEvent {
    private final UUID teamId;
    private final String name;

    public TeamDeletedEvent(UUID teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }

    public UUID getTeamId() { return this.teamId; }
    public String getName() { return this.name; }
}
