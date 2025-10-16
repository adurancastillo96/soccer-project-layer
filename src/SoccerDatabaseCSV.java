import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SoccerDatabaseCSV implements SoccerDatabase {

    private static final String TEAMS_FILE = "teams.csv";
    private static final String PLAYERS_FILE = "players.csv";

    @Override
    public void save(List<Team> teams) {
        try (PrintWriter teamOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEAMS_FILE))));
             PrintWriter playerOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PLAYERS_FILE))));
                ) {

            /** Write Teams:
             *  ID_TEAM | Name | City | Coach | Formation | MatchesWon
             *  */
            for (Team t : teams) {
                teamOut.printf("%s,%s,%s,%s,%s,%d%n",
                        t.getId(), t.getName(), t.getCity(), t.getCoach(), t.getFormation(), t.getMatchesWon());

                /** Write Players:
                 * ID_TEAM | ID_PLAYER | Full Name | BirthYear | FL | Squad | Goals */
                for(Player p : t.getPlayers()) {
                    playerOut.printf("%s,%s,%s,%d,%s,%d,%d%n",
                            t.getId(), p.getId(), p.getFullName(), p.getBirthYear(),
                            p.getFieldLocation(), p.getSquadNumber(), p.getGoals());
                }
            }

            System.out.println("Datos guardados correctamente.");

        } catch (IOException e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    @Override
    public List<Team> load() {
        List<Team> teams = new ArrayList<>();

        // --- Load Teams ---
        try (BufferedReader teamIn = new BufferedReader(new InputStreamReader(new FileInputStream(TEAMS_FILE)));){

            String teamLine;
            while ((teamLine = teamIn.readLine()) != null) {
                if (teamLine.isBlank()) continue;

                String[] teamFields = teamLine.split(",");
                if (teamFields.length < 6) continue;

                try {
                    UUID id = UUID.fromString(teamFields[0]);
                    String name = teamFields[1];
                    String city = teamFields[2];
                    String coach = teamFields[3];
                    String formation = teamFields[4];
                    int matchesWon = Integer.parseInt(teamFields[5]);

                    Team team = new Team(id, name, city, coach, formation, matchesWon);
                    teams.add(team);

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepción: " + e.getMessage());
        }

        // --- Load Players ---
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(PLAYERS_FILE)))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] fields = line.split(",");
                if(fields.length < 7) continue;

                try{
                    UUID teamId = UUID.fromString(fields[0]);
                    UUID playerId = UUID.fromString(fields[1]);
                    String fullName  = fields[2];
                    int birthYear = Integer.parseInt(fields[3]);
                    String fieldLocation = fields[4];
                    int squadNumber = Integer.parseInt(fields[5]);
                    int goals = Integer.parseInt(fields[6]);

                    //ID_TEAM | ID_PLAYER | Full Name | BirthYear | FL | Squad | Goals
                    Team team = teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().orElse(null);
                    if (team != null) {
                        Player player = new Player(playerId, fullName, birthYear, fieldLocation, squadNumber, goals);
                        team.addPlayer(player);
                    }

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepción: " + e.getMessage());
        }


        System.out.println("Datos cargados: " + teams.size() + " equipos.");
        return teams;
    }

}
