package ui;

import events.*;
import events.bus.DomainEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener that provides immediate console feedback for domain events. It
 * reacts to every published event and prints a human-friendly message to
 * standard output. Running on its own executor via the event bus ensures
 * that the UI remains responsive.
 */
public class UiEventListener implements DomainEventListener<DomainEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UiEventListener.class);

    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof TeamCreatedEvent tc) {
            logger.info("Equipo creado: {} ({})", tc.getName(), tc.getTeamId());
        } else if (event instanceof TeamDeletedEvent tr) {
            logger.info("Equipo eliminado: {} ({})", tr.getName(), tr.getTeamId());
        } else if (event instanceof PlayerAddedToTeamEvent pa) {
            logger.info("Jugador añadido: {} al equipo {} (Dorsal {})",
                    pa.getPlayerId(), pa.getTeamId(), pa.getSquadNumber());
        } else if (event instanceof PlayerDeletedFromTeamEvent pr) {
            logger.info("Jugador eliminado: {} del equipo {}", pr.getPlayerId(), pr.getTeamId());
        } else if (event instanceof MatchSimulatedEvent ms) {
            // Determine outcome
            String result;
            if (ms.getGoalsA() > ms.getGoalsB()) {
                result = "Equipo " + ms.getTeamAId() + " gana";
            } else if (ms.getGoalsB() > ms.getGoalsA()) {
                result = "Equipo " + ms.getTeamBId() + " gana";
            } else {
                result = "Empate";
            }
            logger.info("Partido simulado: {} [{}-{}] {} -> Resultado: {}",
                    ms.getTeamAId(), ms.getGoalsA(), ms.getGoalsB(), ms.getTeamBId(), result);
        }
    }
}
