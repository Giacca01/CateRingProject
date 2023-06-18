import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.employee.PersonnelMember;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import businesslogic.shift.Shift;
import javafx.collections.ObservableList;

public class TestAssignment {
    public static void main(String[] args) throws UseCaseLogicException {
        try {
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();

            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            EventInfo e = events.get(0);
            ServiceInfo s = e.getRelatedServices().get(0);
            Menu m = CatERing.getInstance().getMenuManager().getAllMenus().get(2);

            System.out.println("TEST CREATE SUMMARY SHEET");
            CatERing.getInstance().getAssignmentManager().createSummarySheet(e, s, m);
            System.out.println("Summary Sheet:");
            for (Assignment a : s.getAssignments()) {
                System.out.println(a);
            }

            System.out.println();

            System.out.println("TEST SORT SUMMARY SHEET");
            Assignment a = s.getAssignments().get(0);
            CatERing.getInstance().getAssignmentManager().sortSummarySheet(a, 2);
            System.out.println("Sorted Summary Sheet:");
            for (Assignment as : s.getAssignments()) {
                System.out.println(as);
            }

            System.out.println();

            System.out.println("TEST SHOW SHIFT TABLE");
            System.out.println("Shift table:");
            ObservableList<Shift> shifts = CatERing.getInstance().getAssignmentManager().showShiftTable();
            for (Shift sh : shifts) {
                System.out.println(sh);
            }

            System.out.println();

            System.out.println("TEST ASSIGN TASK");
            a = s.getAssignments().get(0);
            Shift sh = shifts.get(0);
            ObservableList<PersonnelMember> members = CatERing.getInstance().getEmployeesManager().getAllMembers();
            PersonnelMember me = members.get(0);
            CatERing.getInstance().getAssignmentManager().assignTask(a, sh, me);
            System.out.println("Assignment selezionato:");
            System.out.println(a);
            System.out.println("Assignments associati al turno selezionato:");
            System.out.println(sh.getTasksToComplete());
            System.out.println("Assignments associati al cuoco selezionato:");
            System.out.println(me.getAssignments());
            System.out.println("Task Assigned");

            System.out.println();

            System.out.println("TEST ADD DETAILS");
            Assignment a1 = s.getAssignments().get(1);
            CatERing.getInstance().getAssignmentManager().addAssignmentDetails(a1, 2, 5);
            System.out.println(a1);

        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }

    }
}
