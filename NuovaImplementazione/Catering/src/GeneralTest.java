import businesslogic.assignment.Assignment;
import businesslogic.assignment.AssignmentManager;
import businesslogic.event.Service;
import businesslogic.recipe.Recipe;
import businesslogic.recipe.RecipeManager;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftManager;
import businesslogic.user.Cook;
import businesslogic.user.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GeneralTest {
    static ObservableList<Cook> cooks = FXCollections.observableArrayList();
    static ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    static ObservableList<Shift> shifts = FXCollections.observableArrayList();
    static ObservableList<Availability> availabilities = FXCollections.observableArrayList();
    static ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    static ObservableList<Service> services = FXCollections.observableArrayList();
    public static void fetchAllData() {
        cooks = UserManager.getCooks();
        //recipes = RecipeManager.getRecipes();
        shifts = ShiftManager.getShifts();
        availabilities = Availability.fetchAvailabilities();
        assignments = AssignmentManager.getAssignments();
        //services = Service.fetchServices();
    }
    public static void main(String[] args) {
        fetchAllData();

        /*Cook c = cooks.get(0);
        System.out.println(c.getUsername());
        ObservableList<Availability> a = c.getDeclaredAvailabilities();
        for(Availability availability: a) {
            availability.printDetails();
        }*/
    }
}
