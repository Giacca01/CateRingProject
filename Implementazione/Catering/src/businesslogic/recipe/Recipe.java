package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe extends KitchenTask {
    private static Map<Integer, Recipe> loadedRecipes = new HashMap<>();

    private int id;
    private String name;
    private String type;
    private ObservableList<KitchenTask> ingredients = FXCollections.observableArrayList();

    private Recipe() {

    }

    public Recipe(int id, String name) {
        this.id = id;
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

    public static ObservableList<Recipe> fetchRecipes() {
        ObservableList<Recipe> recipes = FXCollections.observableArrayList();
        String query = "SELECT * FROM KitchenTasks where type = 'r'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("name");


                Recipe recipe = new Recipe(id, name);
                recipes.add(recipe);
            }
        });
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);

            String featQ = "SELECT * FROM KitchenTasks where recipe_id = '" + r.id + "'";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int preparation_id = rs.getInt("id");
                    r.ingredients.add(Preparation.loadPreparationById(preparation_id));
                }
            });

            loadedRecipes.put(r.id, r);
        }
        return FXCollections.observableArrayList(loadedRecipes.values());
    }

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(loadedRecipes.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (loadedRecipes.containsKey(id)) return loadedRecipes.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM KitchenTasks WHERE id = " + id;
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


    @Override
    public ObservableList<KitchenTask> getIngredients() {
        return this.ingredients;
    }
}
