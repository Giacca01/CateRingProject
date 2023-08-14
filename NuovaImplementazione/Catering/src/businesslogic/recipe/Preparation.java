package businesslogic.recipe;

import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Preparation extends KitchenTask {
    private int id;
    private String name;

    public Preparation() {
    }

    public Preparation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ObservableList<KitchenTask> getIngredients() {
        return null;
    }

    public static KitchenTask loadPreparationById(int id) {
        Preparation p = new Preparation();
        String query = "SELECT * FROM KitchenTasks where type = 'p' and id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                p.id = rs.getInt("id");
                p.name = rs.getString("name");
            }
        });
        return p;
    }
}
