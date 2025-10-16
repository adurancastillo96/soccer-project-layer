import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class Main {

    // --------------- ATRIBUTOS ----------------

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Team> teams = new ArrayList<>();
    private static final SoccerDatabase database = new SoccerDatabaseCSV();

    // --------------- MAIN ----------------

    public static void main(String[] args) {

        // Cargar datos guardados
        teams.addAll(database.load());

        int option;
        while (true) {
            System.out.print("\n===== MENU SOCCER =====\n");
            System.out.println("1. Crear equipo");
            System.out.println("2. Añadir jugador a equipo");
            System.out.println("3. Mostrar info de un jugador");
            System.out.println("4. Mostrar info de jugadores de un equipo");
            System.out.println("5. Mostrar info de un equipo");
            System.out.println("6. Eliminar jugador de un equipo");
            System.out.println("7. Eliminar un equipo");
            System.out.println("8. Mostrar resumen");
            System.out.println("9. Simular partido");
            System.out.println("0. Exit");
            System.out.print("Elige una opción: ");

            option = Integer.parseInt(scanner.nextLine()); //Excepcion al convertir a int
            switch (option) {
                case 0 -> salir();
                case 1 -> createTeam(); // Crear equipo
                case 2 -> addPlayerToTeam(); //Añadir jugador a equipo
                case 3 -> showInfoPlayer(); // Mostrar info de un jugador
                case 4 -> showInfoPlayers(); // Mostrar info de Jugadores de un equipo
                case 5 -> showInfoTeam(); //
                case 6 -> removePlayer(); // Eliminar jugador de equipo
                case 7 -> removeTeam(); // Eliminar un equipo
                case 8 -> showInfoTeams(); // Mostrar resumen (matriz)
                case 9 -> simulateMatch();
                default -> System.out.println("Opción inválida");
            }
        }
    }

    // --------------- METODOS PRINCIPALES ----------------

    private static void createTeam() {
        System.out.println("\n--- Crear nuevo equipo ---");
        try {
            System.out.print("Nombre del equipo: ");
            String name = scanner.nextLine().trim();
            System.out.print("Ciudad: ");
            String city = scanner.nextLine().trim();
            System.out.print("Entrenador: ");
            String coach = scanner.nextLine().trim();
            System.out.print("Formacion: ");
            String formation = scanner.nextLine().trim();

            Team team = new Team(name, city, coach, formation);
            teams.add(team);
            System.out.println("> Equipo creado correctamente:\n" + team);
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear equipo: " + e.getMessage());
        }
    }

    private static void addPlayerToTeam() {
        System.out.println("\n--- Agregar un jugador a un equipo ---");
        try {
            System.out.print("ID del equipo: ");
            String idStr = scanner.nextLine().trim();
            if (idStr.isBlank()) {
                System.out.println("Error al agregar un jugador a un equipo");
                return;
            }
            UUID teamId = UUID.fromString(idStr);
            Team team = findTeamById(teamId);
            if (team == null) {
                System.out.println("Error al agregar un jugador a un equipo");
                return;
            }

            System.out.print("Nombre completo: ");
            String fullName = scanner.nextLine().trim();
            System.out.print("Año de nacimiento: ");
            int birthYear = Integer.parseInt(scanner.nextLine());
            System.out.print("Posición: ");
            String fieldLocation = scanner.nextLine().trim();
            System.out.print("Dorsal (0-100): ");
            int squadNumber = Integer.parseInt(scanner.nextLine());
            System.out.print("Goles: ");
            int goals = Integer.parseInt(scanner.nextLine());

            Player player = new Player(fullName, birthYear, fieldLocation, squadNumber, goals);
            boolean ok = team.addPlayer(player);
            if (!ok) {
                System.out.println("Error al agregar jugador al equipo: " + team.getName());
            }

            System.out.println("> Jugador añadido correctamente:\n" + player);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showInfoPlayer() {
        System.out.print("ID del jugador: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("Operación no efectuada.");
            return;
        }

        try {
            UUID playerId = UUID.fromString(idStr);
            for (Team team : teams) {
                Player player = team.findPlayerById(playerId);
                if (player != null) {
                    System.out.println("> Info del jugador:\n" + player);
                    return;
                }
            }
            throw new IllegalArgumentException("Jugador no encontrado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showInfoPlayers() {
        System.out.print("ID del equipo: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("Operación no efectuada.");
            return;
        }

        try {
            UUID teamId = UUID.fromString(idStr);
            Team team = findTeamById(teamId);
            if (team.getPlayers().isEmpty()) {
                System.out.println(team.getName() + " no tiene jugadores.");
            } else {
                System.out.println("Jugadores del equipo " + team.getName() + ":");
                team.getPlayers().forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showInfoTeam() {
        System.out.print("ID del equipo: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("Operación no efectuada");
            return;
        }

        try {
            UUID teamId = UUID.fromString(idStr);
            Team team = findTeamById(teamId);
            System.out.println("> Info del equipo:\n" + team);
        } catch  (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removePlayer() {
        System.out.print("ID del equipo: ");
        String idStrTeam = scanner.nextLine().trim();
        System.out.print("ID del jugador: ");
        String idStrPlayer = scanner.nextLine().trim();

        if (idStrTeam.isBlank() || idStrPlayer.isBlank()) {
            System.out.println("Operación no efectuada.");
            return;
        }

        try {
            UUID teamId = UUID.fromString(idStrTeam);
            UUID playerId = UUID.fromString(idStrPlayer);
            Team team = findTeamById(teamId);
            boolean deleted = team.removePlayerById(playerId);
            if (!deleted) throw new IllegalArgumentException("Jugador no encontrado.");
            System.out.println("Jugador eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private static void removeTeam() {
        System.out.print("ID del equipo a eliminar: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("Operación no efectuada.");
            return;
        }

        try {
            UUID teamId = UUID.fromString(idStr);
            boolean deleted = teams.removeIf(team -> team.getId().equals(teamId));
            if (!deleted) throw new IllegalArgumentException("El equipo no existe.");
            System.out.println("Equipo eliminado correctamente.");
        } catch  (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showInfoTeams() {
        System.out.println("\n=== Resumen de equipos ===");
        for (Team team : teams) {
            System.out.println(team.getSummary());
        }
    }

    private static void simulateMatch() {
        showInfoTeams();
        System.out.println("\n=== Simulador Partido ===");
        System.out.print("ID del equipo 1: ");
        String idStrTeam1 = scanner.nextLine().trim();
        System.out.print("ID del equipo 2: ");
        String idStrTeam2 = scanner.nextLine().trim();

        Random rand = new Random();
        int goalsTeam1 = rand.nextInt(10);
        int goalsTeam2 = rand.nextInt(10);

        if (idStrTeam1.isBlank() || idStrTeam2.isBlank()) {
            System.out.println("Operación no efectuada.");
            return;
        }

        try {
            UUID teamId1 = UUID.fromString(idStrTeam1);
            Team team1 = findTeamById(teamId1);
            UUID teamId2 = UUID.fromString(idStrTeam2);
            Team team2 = findTeamById(teamId2);

            if (goalsTeam1 > goalsTeam2) {
                team1.setMatchesWon(team1.getMatchesWon() + 1);
                System.out.println("> Equipo ganador:\n" + team1.getName() + " con " + goalsTeam1 + " goles.");
            } else {
                team2.setMatchesWon(team2.getMatchesWon() + 1);
                System.out.println("> Equipo ganador:\n" + team2.getName() + " con " + goalsTeam2 + " goles.");
            }


        } catch  (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    // --------------- METODOS AUXILIARES ----------------
    /** findTeamById:
     * Retorna Team o null.
    * */
    private static Team findTeamById(UUID id) {
        return teams.stream().filter(team -> team.getId().equals(id)).findFirst().orElse(null);
    }

    private static void salir() {
        database.save(teams);
        System.out.println("Datos guardados.");
        System.out.print("Saliendo del programa...");
        System.exit(0);
    }

}