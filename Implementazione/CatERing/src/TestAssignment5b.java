import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.employee.Cook;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.shift.Shift;
import javafx.collections.ObservableList;

public class TestAssignment5b {
    public static void main(String[] args) {
        try{
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            EventInfo e = events.get(0);
            ServiceInfo s = e.getRelatedServices().get(0);
            int s_id = s.getId();

            CatERing.getInstance().getAssignmentManager().openSummarySheet(e, s_id);

            ObservableList<Shift> shifts = CatERing.getInstance().getAssignmentManager().showShiftTable();
            Shift sh1 = shifts.get(0);
            Shift sh2 = shifts.get(1);
            ObservableList<Cook> members = CatERing.getInstance().getEmployeesManager().getAllMembers();
            Cook me1 = members.get(0);
            Cook me2 = members.get(1);
            System.out.println("TEST CHANGE ASSOCIATION");
            Assignment a = s.getAssignments().get(0);
            System.out.println(a);
            System.out.println("SHIFT1: " + sh1.getTasksToComplete());
            System.out.println("SHIFT2: " + sh2.getTasksToComplete());
            System.out.println("COOK1: " + me1.getAssignments());
            System.out.println("COOK2: " + me2.getAssignments());
            CatERing.getInstance().getAssignmentManager().changeAssociation(a, sh1, sh2, me1, me2);
            System.out.println("Operazione effettuata con successo!");
            System.out.println("SHIFT1: " + sh1.getTasksToComplete());
            System.out.println("SHIFT2: " + sh2.getTasksToComplete());
            System.out.println("COOK1: " + me1.getAssignments());
            System.out.println("COOK2: " + me2.getAssignments());
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
