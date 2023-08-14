package businesslogic.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Organizer implements User{
    private static Map<Integer, Organizer> loadedOrganizers = FXCollections.observableHashMap();
    private int id;
    private String username;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isChef() {
        return false;
    }

    @Override
    public boolean isManager() {
        return true;
    }

    public Organizer() {
    }

    public Organizer(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void printDetails() {
        System.out.println("Organizer " + this.id + "; username: " + this.username);
    }

    public static Organizer loadOrganizerById(int  id) {
        if(loadedOrganizers.containsKey(id)) {
            return loadedOrganizers.get(id);
        }

        Organizer organizer = new Organizer();
        String query = "SELECT * FROM Users where role='o' AND id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                organizer.id = rs.getInt("id");
                organizer.username = rs.getString("username");
            }
        });
        if(organizer.id > 0) {
            loadedOrganizers.put(organizer.id, organizer);
        }
        return organizer;
    }

    public static Organizer loadOrganizer(String username) {
        Organizer organizer = new Organizer();
        String query = "SELECT * FROM Users where role='o' AND username = '" + username + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                organizer.id = rs.getInt("id");
                organizer.username = rs.getString("username");
            }
        });
        if(organizer.id > 0) {
            loadedOrganizers.put(organizer.id, organizer);
        }
        return organizer;
    }
}
