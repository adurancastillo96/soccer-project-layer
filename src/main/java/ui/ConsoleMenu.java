package ui;

import java.util.Scanner;

/** Se usa la interfaz {@link AppController} para ejecutar la acción seleccionada.*/
public class ConsoleMenu {
    // --- PRINCIPALES ATRIBUTOS ---
    private final AppController controller;
    private final Scanner scanner;
    private final Runnable onExit;
    private boolean running = true;

    // --- CONSTRUCTOR ---
    /**@param controller controller to delegar acciones
     * @param onExit */
    public ConsoleMenu(AppController controller, Runnable onExit) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.onExit = onExit;
    }

    // --- METODOS ---
    public void runLoop() {
        while (this.running) {
            this.printMenu();
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                System.out.println("Entrada invalida. Intente de nuevo.");
                continue;
            }
            int optionNumber;
            try {
                optionNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número para seleccionar una opción.");
                continue;
            }
            // Quizás se puede poner en ConsoleMenu en lugar de MenuOption numerador.
            MenuOption option = fromInt(optionNumber);
            if (option == null) {
                System.out.println("Opción no válida. Intente de nuevo.");
                continue;
            }
            this.handleOption(option);
        }

        // Callback for exit handling
        if (this.onExit != null) { onExit.run(); }

    }

    /** Imprime las opciones del menú disponible.*/
    private void printMenu() {
        System.out.println();
        System.out.println("================= MENÚ PRINCIPAL =================");
        for (MenuOption option : MenuOption.values()) {
            System.out.println(option);
        }
        System.out.print("Seleccione una opción: ");
    }

    /** acciona a la opción de menú seleccionada.*/
    private void handleOption(MenuOption option) {
        try {
            switch (option) {
                case CREATE_TEAM -> controller.createTeam(scanner);
                case ADD_PLAYER_TO_TEAM ->  controller.addPlayerToTeam(scanner);
                case SHOW_PLAYER_INFO -> controller.showPlayerInfo(scanner);
                case SHOW_TEAM_PLAYERS -> controller.showTeamPlayers(scanner);
                case SHOW_TEAM_INFO -> controller.showTeamInfo(scanner);
                case DELETE_PLAYER_FROM_TEAM -> controller.deletePlayer(scanner);
                case DELETE_TEAM -> controller.deleteTeam(scanner);
                case SHOW_SUMMARY -> controller.showSummary();
                case SIMULATE_MATCH -> controller.simulateMatch(scanner);
                case EXIT -> {
                    controller.exitRequested();
                    this.running = false;
                    System.out.println("\nSaliendo y guardando datos...");
                }
                default -> System.out.println("Opción no válida, intente de nuevo.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
        }
    }

    /** Metodo de busqueda. Devuelve null o objeto MenuOption*/
    private MenuOption fromInt(int optionNumber) {
        for (MenuOption option : MenuOption.values()) {
            if (option.getOptionNumber() == optionNumber) return option;
        }
        return null;
    }

    /** Cierra scanner asociada con el menú.*/
    public void close() { this.scanner.close(); }

}
