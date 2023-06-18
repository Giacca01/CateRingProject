import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import javafx.collections.ObservableList;

public class TestAssignment1a {
    public static void main(String[] args) {
        try{
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            EventInfo e = events.get(0);
            ServiceInfo s = e.getRelatedServices().get(0);

            System.out.println("TEST OPEN SUMMARY SHEET");
            CatERing.getInstance().getAssignmentManager().openSummarySheet(e, s);
            System.out.println("Summary Sheet:");
            for (Assignment a : s.getAssignments()) {
                System.out.println(a);
            }
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
