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

public class TestAssignment5a {
    public static void printShiftsTable(ObservableList<Shift> shifts) {
        System.out.println("Shifts Table:");
        for(Shift shift: shifts){
            shift.printDetails();
        }
    }

    public static void main(String[] args) {
        try{
            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Login eseguito con successo!");

            System.out.println();

            ObservableList<Shift> shiftTable = CatERing.getInstance().getAssignmentManager().showShiftsTable();
            printShiftsTable(shiftTable);

            System.out.println();

            System.out.println("TEST SET SHIFT SATURATION");
            Shift shiftToSaturate = ShiftManager.getShifts().get(0);
            CatERing.getInstance().getAssignmentManager().setShiftSaturation(shiftToSaturate, true);
            printShiftsTable(shiftTable);
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
