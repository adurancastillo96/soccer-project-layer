package ui;

import java.util.Scanner;

public class ConsoleMenu {
    // --- PRINCIPALES ATRIBUTOS ---
    private final AppController controller;
    private final Scanner scanner;
    private boolean running = true;

    // --- CONSTRUCTOR ---
    public ConsoleMenu(AppController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
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
            MenuOption option = MenuOption.fromInt(optionNumber);
            if (option == null) {
                System.out.println("Opción no válida. Intente de nuevo.");
                continue;
            }
            this.handleOption(option);
        }
    }

    /** Imprime las opciones del menú disponible */
    private void printMenu() {
        System.out.println();
        System.out.println("=== Menú principal ===");
        for (MenuOption option : MenuOption.values()) {
            System.out.println(option);
        }
        System.out.print("Seleccione una opción: ");
    }

    /** acciona a la opción de menú seleccionada*/
    private void handleOption(MenuOption option) {
        try {
            switch (option) {
                case CREATE_TEAM -> controller.createTeam();
                case ADD_PLAYER_TO_TEAM ->  controller.addPlayerToTeam();
                case SHOW_PLAYER_INFO -> controller.showPlayerInfo();
                case SHOW_TEAM_PLAYERS -> controller.showTeamPlayers();
                case SHOW_TEAM_INFO -> controller.showTeamInfo();
                case REMOVE_PLAYER_FROM_TEAM -> controller.removePlayerFromTeam();
                case REMOVE_TEAM -> controller.removeTeam();
                case SHOW_SUMMARY -> controller.showSummary();
                case SIMULATE_MATCH -> controller.simulateMatch();
                case EXIT -> {
                    controller.exitRequested();
                    this.running = false;
                    System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
                }
                default -> System.out.println("Opción no soportada");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
        }
    }

    /** Cierra scanner asociada con el menú */
    public void close() { this.scanner.close(); }







}
