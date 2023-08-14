import businesslogic.AssignmentException;
import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
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

public class TestAssignment7a {
    static ObservableList<Cook> cooks = FXCollections.observableArrayList();
    static ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    static ObservableList<Shift> shifts = FXCollections.observableArrayList();
    static ObservableList<Availability> availabilities = FXCollections.observableArrayList();
    static ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    static ObservableList<Service> services = FXCollections.observableArrayList();
    public static void printSummarySheet(ObservableList<Assignment> assignments) {
        System.out.println("Summary Sheet:");
        for(Assignment assignment: assignments){
            assignment.printDetails();
        }
    }

    public static void printShiftsTable(ObservableList<Shift> shifts) {
        System.out.println("Shifts Table:");
        for(Shift shift: shifts){
            shift.printDetails();
        }
    }

    public static void fetchAllData() {
        cooks = UserManager.getCooks();
        recipes = RecipeManager.getRecipes();
        shifts = ShiftManager.getShifts();
        assignments = AssignmentManager.getAssignments();
        services = Service.fetchServices();
        availabilities = Availability.fetchAvailabilities();
    }

    public static void main(String[] args) {
        try{
            fetchAllData();

            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Login eseguito con successo!");
            Service s = services.get(0);

            System.out.println();

            System.out.println("TEST OPEN SUMMARY SHEET");
            CatERing.getInstance().getAssignmentManager().openSummarySheet(s);
            Service currentService = CatERing.getInstance().getAssignmentManager().getCurrentService();
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST REMOVE QUANTITY ESTIMATE");
            Assignment a = currentService.getAssignments().get(1);
            CatERing.getInstance().getAssignmentManager().removeQuantityEstimate(a);
            printSummarySheet(currentService.getAssignments());
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (AssignmentException e) {
            System.out.println("Errore assignment");
        }
    }
}
