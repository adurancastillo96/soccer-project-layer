package service.impl;

import events.bus.EventBus;
import model.Player;
import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.InMemoryTeamRepository;
import domain.DomainException;
import domain.DomainErrorCode;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceImplTest {

    private InMemoryTeamRepository repository;
    private PlayerServiceImpl playerService;
    private TeamServiceImpl teamService;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        // 1. Preparación antes de cada test (Reset del entorno)
        repository = new InMemoryTeamRepository();
        EventBus eventBus = new EventBus();

        // Necesitamos ambos servicios
        // TeamService para crear el equipo donde ficharemos
        teamService = new TeamServiceImpl(repository, repository, eventBus);
        playerService = new PlayerServiceImpl(repository, repository, eventBus);

        // Creamos un equipo base para las pruebas
        Team team = teamService.createTeam("Real Madrid", "Madrid", "Ancelotti", "4-4-2");
        this.teamId = team.getTeamId();
    }

    @Test
    @DisplayName("addPlayer() debe añadir un jugador correctamente si el dorsal está libre")
    void shouldAddPlayerSuccessfully() {
        // 1. CREAR JUGADOR
        Player newPlayer = new Player(teamId, "Kylian Mbappé", 1998, "Delantero", 9);

        // 2. EJECUTAR
        playerService.addPlayer(newPlayer);

        // 3. VERIFICAR
        List<Player> players = repository.findPlayersByTeam(teamId);
        assertEquals(1, players.size(), "El equipo debería tener 1 jugador");
        assertEquals("Kylian Mbappé", players.get(0).getFullName());
    }

    @Test
    @DisplayName("addPlayer() debe lanzar excepción si el dorsal ya existe en el equipo")
    void shouldThrowExceptionWhenSquadNumberIsDuplicate() {
        // 1. PREPARACIÓN: Añadimos un primer jugador con dorsal 10
        Player p1 = new Player(teamId, "Luka Modric", 1985, "Medio", 10);
        playerService.addPlayer(p1);

        // 2. INTENTO: Añadir otro con el MISMO dorsal (10)
        Player p2 = new Player(teamId, "Vinicius Jr", 2000, "Delantero", 10);

        // 3. VERIFICAR QUE FALLA
        DomainException exception = assertThrows(DomainException.class, () -> {
            playerService.addPlayer(p2);
        });

        assertEquals(DomainErrorCode.INVALID_SQUAD_NUMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("addPlayer() debe lanzar excepción si el equipo no existe")
    void shouldThrowExceptionWhenTeamDoesNotExist() {
        // ID inventado
        UUID fakeTeamId = UUID.randomUUID();
        Player p1 = new Player(fakeTeamId, "Jugador Perdido", 2000, "Portero", 1);

        DomainException exception = assertThrows(DomainException.class, () -> {
            playerService.addPlayer(p1);
        });

        assertEquals(DomainErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
}