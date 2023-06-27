import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.employee.Cook;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.shift.Shift;
import javafx.collections.ObservableList;

public class TestAssignment5c {
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
            System.out.println("TEST DELETE ASSOCIATION");
            Assignment a = s.getAssignments().get(0);
            System.out.println(a);
            System.out.println(sh2.getTasksToComplete());
            CatERing.getInstance().getAssignmentManager().deleteAssociation(a, sh2, me2);
            System.out.println("Operazione effettuata con successo!");
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
