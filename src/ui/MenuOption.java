package ui;

public enum MenuOption {
    CREATE_TEAM(1, "Crear equipo"),
    ADD_PLAYER_TO_TEAM(2, "Añadir jugador a equipo"),
    SHOW_PLAYER_INFO(3, "Mostrar info de un jugador"),
    SHOW_TEAM_PLAYERS(4, "Mostrar info de jugadores de un equipo"),
    SHOW_TEAM_INFO(5, "Mostrar info de un equipo"),
    REMOVE_PLAYER_FROM_TEAM(6,"Eliminar jugador de un equipo"),
    REMOVE_TEAM(7, "Eliminar un equipo"),
    SHOW_SUMMARY(8, "Mostrar resumen"),
    SIMULATE_MATCH(9, "Simular partido"),
    EXIT(0, "Salir");

    private final int optionNumber;
    private final String description;

    // --- CONSTRUCTOR ---
    MenuOption(int optionNumber, String description) {
        this.optionNumber = optionNumber;
        this.description = description;
    }

    // --- GETTERS ---
    public int getOptionNumber() { return this.optionNumber; }
    public String getDescription() { return this.description; }

    // --- METODOS ---
    /** Metodo de búsqueda
     * Devuelve null o objetos MenuOption*/
    public static MenuOption fromInt(int optionNumber) {
        for (MenuOption option : values()) {
            if (option.getOptionNumber() == optionNumber) return option;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.optionNumber + ". " + this.description;
    }

}