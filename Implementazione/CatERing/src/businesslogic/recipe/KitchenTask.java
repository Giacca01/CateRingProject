package businesslogic.recipe;

import businesslogic.user.User;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KitchenTask {
    private int id;
    private String description;

    public static KitchenTask loadKitchenTaskById(int kitchentask_id) {
        KitchenTask load = new KitchenTask();
        String ktQuery = "SELECT * FROM KitchenTasks WHERE id='"+kitchentask_id+"'";
        PersistenceManager.executeQuery(ktQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.description = rs.getString("description");
            }
        });
        return load;
    }

    public String toString(){
        return "id: " + id + "; description: " + description;
    }

    public int getId() {
        return id;
    }
}