package ui;

import events.*;
import events.bus.DomainEventListener;

/**
 * Listener that provides immediate console feedback for domain events. It
 * reacts to every published event and prints a human-friendly message to
 * standard output. Running on its own executor via the event bus ensures
 * that the UI remains responsive.
 */
public class UiEventListener implements DomainEventListener<DomainEvent> {
    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof TeamCreatedEvent tc) {
            System.out.println("[INFO] Equipo creado: " + tc.getName() + " (" + tc.getTeamId() + ")");
        } else if (event instanceof TeamDeletedEvent tr) {
            System.out.println("[INFO] Equipo eliminado: " + tr.getTeamId());
        } else if (event instanceof PlayerAddedToTeamEvent pa) {
            System.out.println("[INFO] Jugador añadido: " + pa.getPlayerId() + " al equipo " + pa.getTeamId() + " con dorsal " + pa.getSquadNumber());
        } else if (event instanceof PlayerDeletedFromTeamEvent pr) {
            System.out.println("[INFO] Jugador eliminado: " + pr.getPlayerId() + " del equipo " + pr.getTeamId());
        } else if (event instanceof MatchSimulatedEvent ms) {
            // Determine outcome
            String resultado;
            if (ms.getGoalsA() > ms.getGoalsB()) {
                resultado = "Equipo " + ms.getTeamAId() + " gana";
            } else if (ms.getGoalsB() > ms.getGoalsA()) {
                resultado = "Equipo " + ms.getTeamBId() + " gana";
            } else {
                resultado = "Empate";
            }
            System.out.println("[INFO] Partido simulado: " + ms.getTeamAId() + " " + ms.getGoalsA() + " - " + ms.getGoalsB() + " " + ms.getTeamBId() + " (" + resultado + ")");
        }
    }
}
