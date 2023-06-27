import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.employee.Cook;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.shift.Shift;
import javafx.collections.ObservableList;

public class TestAssignment5a {
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
            Shift sh = shifts.get(0);
            ObservableList<Cook> members = CatERing.getInstance().getEmployeesManager().getAllMembers();
            Cook me = members.get(0);
            System.out.println("TEST MARK AS DONE");
            Assignment a = s.getAssignments().get(0);
            System.out.println("Assignment prima di essere segnato come fatto");
            System.out.println(a);
            CatERing.getInstance().getAssignmentManager().markAsDone(a, sh, me);
            System.out.println("Assignment dopo essere stato segnato come fatto");
            System.out.println(a);

        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
