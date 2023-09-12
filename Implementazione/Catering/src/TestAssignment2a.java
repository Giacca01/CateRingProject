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

public class TestAssignment2a {
    static ObservableList<Cook> cooks = FXCollections.observableArrayList();
    static ObservableList<Shift> shifts = FXCollections.observableArrayList();
    static ObservableList<Service> services = FXCollections.observableArrayList();
    public static void printSummarySheet(ObservableList<Assignment> assignments) {
        System.out.println("Summary Sheet:");
        for(Assignment assignment: assignments){
            assignment.printDetails();
        }
    }
    public static void fetchAllData() {
        cooks = UserManager.getCooks();
        shifts = ShiftManager.getShifts();
        services = Service.fetchServices();
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

            System.out.println("TEST REMOVE ASSIGNMENT");
            Assignment a = currentService.getAssignments().get(2);
            Assignment removedAssignment = CatERing.getInstance().getAssignmentManager().deleteAssignment(a);
            printSummarySheet(currentService.getAssignments());
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (AssignmentException e) {
            System.out.println("Errore assignment");
        }
    }
}
