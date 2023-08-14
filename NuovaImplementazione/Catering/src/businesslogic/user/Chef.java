package businesslogic.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Chef implements User{
    private static Map<Integer, Chef> loadedChefs = FXCollections.observableHashMap();
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
        return true;
    }

    @Override
    public boolean isManager() {
        return false;
    }

    public Chef() {
    }

    public Chef(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void printDetails() {
        System.out.println("Chef " + this.id + "; username: " + this.username);
    }

    public static Chef loadChefById(int  id) {
        if(loadedChefs.containsKey(id)) {
            return loadedChefs.get(id);
        }

        Chef chef = new Chef();
        String query = "SELECT * FROM Users where role='h' AND id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                chef.id = rs.getInt("id");
                chef.username = rs.getString("username");
            }
        });

        loadedChefs.put(chef.id, chef);
        return chef;
    }

    public static Chef loadChef(String username) {
        Chef chef = new Chef();
        String query = "SELECT * FROM Users where role='h' AND username = '" + username + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                chef.id = rs.getInt("id");
                chef.username = rs.getString("username");
            }
        });
        if(chef.id > 0) {
            loadedChefs.put(chef.id, chef);
        }
        return chef;
    }
}
