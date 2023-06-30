package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe {
    private static Map<Integer, Recipe> all = new HashMap<>();

    private int id;
    private String name;
    private ObservableList<KitchenTask> kitchenTasks;

    private Recipe() {

    }

    public Recipe(String name) {
        kitchenTasks = FXCollections.observableArrayList();
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }

    public ObservableList<KitchenTask> getKitchenTasks(){
        return kitchenTasks;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Recipe> loadAllRecipes() {
        String query = "SELECT * FROM Recipes";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (all.containsKey(id)) {
                    Recipe rec = all.get(id);
                    rec.name = rs.getString("name");
                } else {
                    Recipe rec = new Recipe(rs.getString("name"));
                    rec.id = id;
                    all.put(rec.id, rec);
                }
            }
        });
        ObservableList<Recipe> ret =  FXCollections.observableArrayList(all.values());
        Collections.sort(ret, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe o1, Recipe o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        for(Recipe r: ret){
            String getKt = "SELECT DISTINCT kitchentask_id FROM RecipesKitchenTasks WHERE recipe_id = " + r.id;
            PersistenceManager.executeQuery(getKt, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int kitchentask_id = rs.getInt("kitchentask_id");
                    r.kitchenTasks.add(KitchenTask.loadKitchenTaskById(kitchentask_id));
                }
            });
        }
        return ret;
    }

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(all.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (all.containsKey(id)) return all.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                    rec.name = rs.getString("name");
                    rec.id = id;
                    all.put(rec.id, rec);
            }
        });
        return rec;
    }


}
