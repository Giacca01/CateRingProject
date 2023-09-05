package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RecipeManager {

    public RecipeManager() {
        Recipe.fetchRecipes();
    }

    public static ObservableList<Recipe> getRecipes() {
        return FXCollections.unmodifiableObservableList(Recipe.getAllRecipes());
    }
}
