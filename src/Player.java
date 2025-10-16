import java.util.UUID;
import java.time.Year;

/**
 * Representa un jugador de fútbol con identificador único, nombre, edad y puntuación.
 */
public class Player {

    // -------- CONSTANTES DE VALIDACIÓN --------
    private static final int STRING_MIN = 2;
    private static final int STRING_MAX = 60;
    private static final int CURRENT_YEAR  = Year.now().getValue();

    // -------- ATRIBUTOS PRINCIPALES --------
    private final UUID id;
    private String fullName;
    private int birthYear;
    private String fieldLocation;
    private int squadNumber;
    private int goals;

    // ---------- CONSTRUCTOR ----------
    public Player(String fullName, int birthYear, String fieldLocation, int squadNumber, int goals) {
        this.id = UUID.randomUUID();
        this.setFullName(fullName);
        this.setBirthYear(birthYear);
        this.setFieldLocation(fieldLocation);
        this.setSquadNumber(squadNumber);
        this.setGoals(goals);
    }

    public Player(UUID id, String fullName, int birthYear, String fieldLocation, int squadNumber, int goals) {
        this.id = id;
        this.setFullName(fullName);
        this.setBirthYear(birthYear);
        this.setFieldLocation(fieldLocation);
        this.setSquadNumber(squadNumber);
        this.setGoals(goals);
    }

    // ---------- GETTERS ----------
    public UUID getId() { return this.id; }
    public String getFullName() { return this.fullName; }
    public int getBirthYear() { return this.birthYear; }
    public String getFieldLocation() { return this.fieldLocation; }
    public int getSquadNumber() { return this.squadNumber; }
    public int getGoals() { return this.goals; }

    // ---------- SETTERS ----------
    public void setFullName(String fullName) {
        if (fullName == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo.");
        }

        String trimmed = fullName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        if (trimmed.length() < STRING_MIN || trimmed.length() > STRING_MAX) {
            throw new IllegalArgumentException("El nombre debe tener entre " + STRING_MIN + " y " + STRING_MAX + " caracteres");
        }
        this.fullName = trimmed;
    }

    /** setBirthYear:
     * Validador de año entre 1900 a fecha actual
     * */
    public void setBirthYear(int birthYear) {
        if(birthYear < 1900 || birthYear > CURRENT_YEAR) {
            throw new IllegalArgumentException("Edad fuera de rango.");
        }
        this.birthYear = birthYear;
    }

    /** setFieldLocation:
     * Validador de posiciones. (Opcional: Numerador)
    * */
    public void setFieldLocation(String fieldLocation) {
        if (fieldLocation == null || fieldLocation.isBlank()) throw new IllegalArgumentException("Posición vacía.");
        this.fieldLocation = fieldLocation.trim();
    }

    /** setSquadNumber:
     * Validodor que limita el numero de dorsales entre 0 y 100.
     **/
    public void setSquadNumber(int squadNumber) {
        if (squadNumber < 0 || squadNumber > 100) throw new IllegalArgumentException("Dorsal fuera de rango (0-100).");
        this.squadNumber = squadNumber;
    }

    /** setFieldLocation:
     * Validador de numero de goles entre 0 y 999.
     * */
    public void setGoals(int goals) {
        if (goals < 0 || goals > 999) {
            throw new IllegalArgumentException("Nº Goles inválido.");
        }
        this.goals = goals;
    }

    public String getSummary() {
        return String.format("ID:%s | %s | Nac.:%d | Pos:%s | Dorsal:%d | Goles:%d",
                this.id, this.fullName, this.birthYear, this.fieldLocation, this.squadNumber, this.goals);
    }

    @Override
    public String toString() {
        return "Jugador{"+ this.getSummary() +"}";
    }

}