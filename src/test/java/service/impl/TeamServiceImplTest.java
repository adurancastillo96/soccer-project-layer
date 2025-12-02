package service.impl;

import events.bus.EventBus;
import model.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.InMemoryDatabase;
import domain.DomainException;
import domain.DomainErrorCode;

import java.util.Optional;
import java.util.UUID;

// Importaciones estáticas para que las aserciones sean más legibles
import static org.junit.jupiter.api.Assertions.*;

class TeamServiceImplTest {

    @Test
    @DisplayName("createTeam() debe guardar el equipo en el repositorio y devolverlo con ID")
    void shouldCreateTeamSuccessfully() {
        // 1. PREPARACIÓN (Arrange)
        // Usamos la implementación en memoria, que es rápida y perfecta para tests
        InMemoryDatabase repo = new InMemoryDatabase();
        EventBus eventBus = new EventBus();

        // Instanciamos el servicio pasando el repo (dos veces, ya que implementa ambas interfaces)
        TeamServiceImpl teamService = new TeamServiceImpl(repo, repo, eventBus);

        // 2. EJECUCIÓN (Act)
        String nombre = "Dream Team";
        Team createdTeam = teamService.createTeam(nombre, "Barcelona", "Pep", "4-3-3");

        // 3. VERIFICACIÓN (Assert)
        // Verificamos que el objeto devuelto no es nulo
        assertNotNull(createdTeam, "El equipo creado no debe ser null");

        // Verificamos que tiene un ID asignado
        assertNotNull(createdTeam.getTeamId(), "El equipo debe tener un ID generado");

        // Verificamos los datos
        assertEquals(nombre, createdTeam.getName());
        assertEquals("Barcelona", createdTeam.getCity());

        // Verificamos que realmente se guardó en el repositorio
        Optional<Team> foundInRepo = repo.findTeam(createdTeam.getTeamId());
        assertTrue(foundInRepo.isPresent(), "El equipo debería estar en el repositorio");
        assertEquals(nombre, foundInRepo.get().getName());
    }

    @Test
    @DisplayName("findTeam() debe lanzar excepción si el ID no existe")
    void shouldThrowExceptionWhenTeamNotFound() {
        // 1. PREPARACIÓN
        InMemoryDatabase repo = new InMemoryDatabase();
        EventBus eventBus = new EventBus();
        TeamServiceImpl teamService = new TeamServiceImpl(repo, repo, eventBus);

        // Generamos un ID aleatorio que seguro no existe
        UUID randomId = UUID.randomUUID();

        // 2. y 3. EJECUCIÓN Y VERIFICACIÓN
        // Esperamos que al llamar a findTeam lance una DomainException
        DomainException exception = assertThrows(DomainException.class, () -> {
            teamService.findTeam(randomId);
        });

        // Verificamos que el código de error sea el correcto
        assertEquals(DomainErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
}