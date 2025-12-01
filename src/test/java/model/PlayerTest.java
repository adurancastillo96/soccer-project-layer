package model;

import domain.DomainErrorCode;
import domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private final UUID teamId = UUID.randomUUID();

    @Test
    @DisplayName("Debe crear un jugador válido correctamente")
    void shouldCreateValidPlayer() {
        Player player = new Player(teamId, "Lionel Messi", 1987, "Delantero", 10);

        assertNotNull(player.getPlayerId());
        assertEquals("Lionel Messi", player.getFullName());
        assertEquals(10, player.getSquadNumber());
        assertEquals(0, player.getGoals()); // Por defecto 0
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre está vacío o es nulo")
    void shouldThrowExceptionForInvalidName() {
        // Caso Null
        assertThrows(IllegalArgumentException.class, () -> {
            new Player(teamId, null, 2000, "Medio", 5);
        });

        // Caso Vacío
        assertThrows(IllegalArgumentException.class, () -> {
            new Player(teamId, "   ", 2000, "Medio", 5);
        });
    }

    @Test
    @DisplayName("Debe lanzar DomainException si el dorsal es inválido (<0 o >100)")
    void shouldThrowExceptionForInvalidSquadNumber() {
        // Caso Negativo
        DomainException exNegative = assertThrows(DomainException.class, () -> {
            new Player(teamId, "Jugador", 2000, "Portero", -1);
        });
        assertEquals(DomainErrorCode.INVALID_SQUAD_NUMBER, exNegative.getErrorCode());

        // Caso Excesivo
        DomainException exExcessive = assertThrows(DomainException.class, () -> {
            new Player(teamId, "Jugador", 2000, "Portero", 101);
        });
        assertEquals(DomainErrorCode.INVALID_SQUAD_NUMBER, exExcessive.getErrorCode());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el año de nacimiento es ilógico")
    void shouldThrowExceptionForInvalidBirthYear() {
        // Caso muy antiguo
        assertThrows(IllegalArgumentException.class, () -> {
            new Player(teamId, "Abuelo", 1800, "Defensa", 4);
        });
    }
}