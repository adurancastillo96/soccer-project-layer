package events.bus;

import events.DomainEvent;

/**
 * Generic listener interface for main.java.domain main.java.events. Implementations of this
 * interface can be registered with the EventBus to receive main.java.events of a
 * particular type.
 *
 * @param <E> the type of main.java.domain event this listener handles
 */
public interface DomainEventListener<E extends DomainEvent> {
    void onEvent(E event);
}
