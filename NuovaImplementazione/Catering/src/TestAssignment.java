import businesslogic.AssignmentException;
import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.event.Event;
import businesslogic.event.EventManager;
import businesslogic.event.RecurrentEvent;
import businesslogic.event.Service;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftManager;
import businesslogic.user.*;
import javafx.collections.ObservableList;

public class TestAssignment {
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

    public static void main(String[] args) throws UseCaseLogicException {
        try{
            ObservableList<Service> services = Service.fetchServices();

            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println("Login eseguito con successo!");
            Service s = services.get(0);

            System.out.println();

            System.out.println("TEST CREATE SUMMARY SHEET");
            Service currentService = CatERing.getInstance().getAssignmentManager().createSummarySheet(s);
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST ADD ASSIGNMENT");
            KitchenTask kt = CatERing.getInstance().getRecipeManager().getRecipes().get(7);
            Assignment prevKt = currentService.getAssignments().get(0);
            CatERing.getInstance().getAssignmentManager().addAssignment(kt, prevKt);
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST SORT SUMMARY SHEET");
            Assignment assignmentToSort = currentService.getAssignments().get(0);
            CatERing.getInstance().getAssignmentManager().sortSummarySheet(assignmentToSort, 2);
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST SHOW SHIFTS TABLE");
            ObservableList<Shift> shiftTable = CatERing.getInstance().getAssignmentManager().showShiftsTable();
            printShiftsTable(shiftTable);

            System.out.println();

            System.out.println("TEST ASSOCIATE ASSIGNMENT");
            Cook cook = (Cook) UserManager.getCookById(1);
            Shift shift = ShiftManager.getShifts().get(0);
            Assignment a = currentService.getAssignments().get(1);
            CatERing.getInstance().getAssignmentManager().associateAssignment(a, shift, cook);
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST CHANGE ASSIGNMENT DETAILS");
            Assignment a2 = currentService.getAssignments().get(1);
            CatERing.getInstance().getAssignmentManager().changeAssignmentDetails(a2, "12", "15");
            printSummarySheet(currentService.getAssignments());

            System.out.println();

            System.out.println("TEST SET TO BE PREPARED");
            Assignment assignmentToPrepare = currentService.getAssignments().get(0);
            CatERing.getInstance().getAssignmentManager().setToBePrepared(assignmentToPrepare, false);
            printSummarySheet(currentService.getAssignments());
            CatERing.getInstance().getAssignmentManager().setToBePrepared(assignmentToPrepare, true);
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (AssignmentException e) {
            System.out.println("Errore assignment");
        }
    }
}
