package events.bus;

import events.DomainEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple asynchronous event bus implementation. Listeners can subscribe to
 * specific event types, and when an event is published all matching
 * listeners will be invoked asynchronously on a thread pool. The event bus
 * keeps references to listeners for the lifetime of the application.
 */
public class EventBus {
    private final Map<Class<? extends DomainEvent>, List<DomainEventListener<? super DomainEvent>>> listeners = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Subscribes the given listener to the specified type of event.
     *
     * @param eventType the type of event to listen for
     * @param listener  the listener to notify when main.java.events of the given type are published
     * @param <E>       the generic event type
     */
    public <E extends DomainEvent> void subscribe(Class<E> eventType, DomainEventListener<? super E> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add((DomainEventListener<? super DomainEvent>) listener);
    }

    /**
     * Publishes the given event to all listeners that have subscribed to the
     * event's class. Delivery is asynchronous.
     *
     * @param event the event to publish
     */
    public void publish(DomainEvent event) {
        Class<? extends DomainEvent> eventClass = event.getClass();
        // Deliver to listeners registered exactly for this event class
        List<DomainEventListener<? super DomainEvent>> registered = listeners.get(eventClass);
        if (registered != null) {
            for (DomainEventListener<? super DomainEvent> listener : registered) {
                // Capture listener and event in final variables for lambda
                executor.submit(() -> {
                    try {
                        @SuppressWarnings("unchecked")
                        DomainEventListener<DomainEvent> l = (DomainEventListener<DomainEvent>) listener;
                        l.onEvent(event);
                    } catch (ClassCastException e) {
                        // Shouldn't happen because of type erasure; silently ignore
                    }
                });
            }
        }
    }

    /**
     * Attempts to gracefully shut down the event bus, waiting for currently
     * queued main.java.events to be delivered. This method should be called when the
     * application is exiting to allow asynchronous tasks to complete.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
