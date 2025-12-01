package model;

import domain.DomainErrorCode;
import domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    @DisplayName("Debe crear un equipo válido correctamente")
    void shouldCreateValidTeam() {
        Team team = new Team("Rayo Vallecano", "Madrid", "Iñigo Pérez", "4-4-2");

        assertNotNull(team.getTeamId());
        assertEquals("Rayo Vallecano", team.getName());
        assertEquals("Madrid", team.getCity());
        assertEquals("Iñigo Pérez", team.getCoach());
        assertEquals(0, team.getMatchesWon());
    }

    @Test
    @DisplayName("Validación de texto debe lanzar DomainException si los datos están vacíos")
    void shouldThrowExceptionForInvalidTextData() {
        // Probamos con el nombre del equipo vacío
        DomainException exName = assertThrows(DomainException.class, () -> {
            new Team("", "Madrid", "Coach", "4-4-2");
        });
        assertEquals(DomainErrorCode.VALIDATION_ERROR, exName.getErrorCode());

        // Probamos con la ciudad null
        DomainException exCity = assertThrows(DomainException.class, () -> {
            new Team("Nombre", null, "Coach", "4-4-2");
        });
        assertEquals(DomainErrorCode.VALIDATION_ERROR, exCity.getErrorCode());
    }

    @Test
    @DisplayName("setMatchesWon debe lanzar excepción si se intenta poner un valor negativo")
    void shouldThrowExceptionForNegativeMatches() {
        Team team = new Team("Test Team", "City", "Coach", "4-4-2");

        DomainException ex = assertThrows(DomainException.class, () -> {
            team.setMatchesWon(-5);
        });
        assertEquals(DomainErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    @DisplayName("incrementMatchesWon debe aumentar las victorias")
    void shouldIncrementMatchesWon() {
        Team team = new Team("Ganadores FC", "City", "Coach", "4-3-3");
        assertEquals(0, team.getMatchesWon());

        team.incrementMatchesWon();
        assertEquals(1, team.getMatchesWon());
    }
}