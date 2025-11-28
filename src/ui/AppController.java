package ui;

import domain.DomainException;
import model.Player;
import model.Team;
import service.PlayerService;
import service.TeamService;
import util.IdUtils;

import java.util.*;

public class AppController {
    private final TeamService teamService;
    private final PlayerService playerService;

    public AppController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    public void createTeam(Scanner scanner) {
        System.out.println("\n=== Crear equipo ===");
        System.out.print("Nombre del equipo: ");
        String name = scanner.nextLine().trim();
        System.out.print("Ciudad: ");
        String city = scanner.nextLine().trim();
        System.out.print("Entrenador: ");
        String coach = scanner.nextLine().trim();
        System.out.print("Formación (ej. 4-4-2): ");
        String formation = scanner.nextLine().trim();
        try {
            Team team = teamService.createTeam(name, city, coach, formation);
            System.out.println("> Equipo creado correctamente:\n" + team);
        } catch (DomainException e) {
            System.err.println("> Error: " + e.getMessage());
        }
    }

    public void addPlayerToTeam(Scanner scanner) {
        System.out.println("\n=== Añadir un jugador a un equipo ===");
        System.out.print("ID del equipo: ");
        try {
            String teamIdStr = scanner.nextLine().trim();
            UUID teamId = IdUtils.parse(teamIdStr);
            if (teamId == null) {
                System.out.println("> Error al agregar Id del equipo.");
                return;
            }

            System.out.print("Nombre completo del jugador: ");
            String fullName = scanner.nextLine().trim();
            System.out.print("Año de nacimiento (YYYY): ");
            int birthYear = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Posición en el campo: ");
            String fieldLocation = scanner.nextLine().trim();
            System.out.print("Número de dorsal (0-100): ");
            int squadNumber = Integer.parseInt(scanner.nextLine().trim());
            Player player = new Player(teamId, fullName, birthYear, fieldLocation, squadNumber);
            playerService.addPlayer(player);
            System.out.println("> Jugador añadido correctamente:\n" + player);
        } catch (DomainException e) {
            System.err.println("> Error: " + e.getMessage());
        }
    }

    public void showPlayerInfo(Scanner scanner) {
        System.out.println("\n=== Mostrar info de jugador ===");
        System.out.print("ID del jugador: ");
        String playerIdStr = scanner.nextLine().trim();
        try {
            UUID playerId = IdUtils.parse(playerIdStr);
            if (playerId == null) {
                System.out.println("> Error al agregar Id del jugador.");
                return;
            }

            Optional<Player> player = playerService.getPlayer(playerId);
            if (player.isPresent()) {
                System.out.println(player.get());
            } else {
                System.out.println("> Jugador no encontrado");
            }
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void showTeamPlayers(Scanner scanner) {
        System.out.println("\n=== Mostrar info de jugadores de un equipo ===");
        System.out.print("ID del equipo: ");
        String teamIdStr = scanner.nextLine().trim();
        try {
            UUID teamId = IdUtils.parse(teamIdStr);
            if (teamId == null) {
                System.out.println("> Error al agregar Id del equipo.");
                return;
            }

            List<Player> players = playerService.getPlayers(teamId);
            if (players.isEmpty()) {
                System.out.println("> El equipo no tiene jugadores.");
            } else {
                System.out.println("Jugadores del equipo " + teamId + ":\n");
                for (Player p : players) {
                    System.out.println("- " + p.getFullName() + " (ID: " + p.getPlayerId() + ", Dorsal: " + p.getSquadNumber() + ")");
                }
            }
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void showTeamInfo(Scanner scanner) {
        System.out.print("\n=== Mostrar info de un equipo ===");
        System.out.print("\nID del equipo: ");
        String teamIdStr = scanner.nextLine().trim();
        try {
            UUID teamId = IdUtils.parse(teamIdStr);
            if (teamId == null) {
                System.out.println("> Error al agregar Id del equipo.");
                return;
            }

            Optional<Team> team = teamService.getTeam(teamId);
            if (team.isEmpty()) {
                System.out.println("> Equipo no encontrado.");
            } else {
                List<Player> players = playerService.getPlayers(teamId);
                System.out.println(team.get());
                System.out.println("- Número de jugadores: " + players.size());
            }
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void deletePlayer(Scanner scanner) {
        System.out.println("\n=== Eliminar jugador de un equipo ===");
        System.out.print("ID del jugador: ");
        String playerIdStr = scanner.nextLine().trim();
        try {
            UUID playerId = IdUtils.parse(playerIdStr);
            if (playerId == null) {
                System.out.println("> Error al agregar Id del jugador.");
                return;
            }

            playerService.deletePlayer(playerId);
            System.out.println("> Jugador eliminado correctamente.");
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void deleteTeam(Scanner scanner) {
        System.out.println("\n=== Eliminar un equipo ===");
        System.out.print("ID del equipo: ");
        String teamIdStr = scanner.nextLine().trim();
        try {
            UUID teamId = IdUtils.parse(teamIdStr);
            if (teamId == null) {
                System.out.println("> Error al agregar Id del equipo.");
                return;
            }

            playerService.deletePlayers(teamId);
            teamService.deleteTeam(teamId);
            System.out.println("> Equipo y jugadores se eliminaron correctamente.");
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void showSummary() {
        System.out.println("\n=== Mostrar resumen ===");
        Collection<Team> teams = teamService.getAllTeams();
        if (teams.isEmpty()) {
            System.out.println("> No hay equipos registrados.");
        } else {
            System.out.println("Resumen de equipos:\n");
            for (Team team : teams) {
                List<Player> players = playerService.getPlayers(team.getTeamId());
                System.out.println("- " + team.getName() + " (ID: " + team.getTeamId() + ")" +
                        ", Jugadores: " + players.size() +
                        ", Partidos ganados: " + team.getMatchesWon());
            }
        }
    }

    public void simulateMatch(Scanner scanner) {
        System.out.println("\n=== Simular partidos ===");
        System.out.print("ID del equipo A: ");
        String teamAIdStr = scanner.nextLine().trim();
        System.out.print("ID del equipo B: ");
        String teamBIdStr = scanner.nextLine().trim();
        try {
            UUID teamAIdUUID = IdUtils.parse(teamAIdStr);
            UUID teamBIdUUID = IdUtils.parse(teamBIdStr);
            if (teamAIdUUID == null || teamBIdUUID == null) {
                System.out.println("> Error al agregar Id del equipo.");
                return;
            }

            Optional<Team> winner = teamService.simulateMatch(teamAIdUUID, teamBIdUUID);
            if (winner.isEmpty()) {
                System.out.println("El partido ha terminado en empate.");
            } else {
                System.out.println("El ganador es: " + winner.get().getName());
            }
        } catch (DomainException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void exitRequested() {
        // Save all data when exiting
        /// database.save(repository.findallTeams());
        /// database.save(teams);
        System.out.println("Datos guardados.");
        System.out.print("Saliendo del programa...");
        System.exit(0);
    }


}
