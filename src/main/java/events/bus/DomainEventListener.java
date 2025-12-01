package events.bus;

import events.DomainEvent;

/**
 * Generic listener interface for domain events. Implementations of this
 * interface can be registered with the EventBus to receive events of a
 * particular type.
 *
 * @param <E> the type of domain event this listener handles
 */
public interface DomainEventListener<E extends DomainEvent> {
    void onEvent(E event);
}
