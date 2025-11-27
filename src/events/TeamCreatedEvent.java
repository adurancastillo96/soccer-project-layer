package events;

import java.util.UUID;

/**
 * Event published when a new team has been created.
 */
public class TeamCreatedEvent implements DomainEvent {
    private final UUID teamId;
    private final String name, city, coach, formation;

    // Team(String name, String city, String coach, String formation)
    public TeamCreatedEvent(UUID teamId, String name, String city, String coach, String formation) {
        this.teamId = teamId;
        this.name = name;
        this.city = city;
        this.coach = coach;
        this.formation = formation;
    }

    public UUID getTeamId() { return teamId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCoach() { return coach; }
    public String getFormation() { return formation; }

}
