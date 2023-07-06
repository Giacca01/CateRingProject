package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe {
    private static Map<Integer, Recipe> loadedRecipes = new HashMap<>();

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
        return name + " {" + kitchenTasks + "}";
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
                if (loadedRecipes.containsKey(id)) {
                    Recipe rec = loadedRecipes.get(id);
                    rec.name = rs.getString("name");
                } else {
                    Recipe rec = new Recipe(rs.getString("name"));
                    rec.id = id;
                    loadedRecipes.put(rec.id, rec);
                }
            }
        });
        ObservableList<Recipe> ret =  FXCollections.observableArrayList(loadedRecipes.values());
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
        return FXCollections.observableArrayList(loadedRecipes.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (loadedRecipes.containsKey(id)) return loadedRecipes.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                    rec.name = rs.getString("name");
                    rec.id = id;
                    loadedRecipes.put(rec.id, rec);
            }
        });
        return rec;
    }


}
