import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import javafx.collections.ObservableList;

public class TestAssignment7b {
    public static void main(String[] args) {
        try{
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            System.out.println("TEST LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            EventInfo e = events.get(0);
            ServiceInfo s = e.getRelatedServices().get(0);

            CatERing.getInstance().getAssignmentManager().openSummarySheet(e, s);

            System.out.println("TEST REMOVE QUANTITY");
            Assignment a1 = s.getAssignments().get(1);
            CatERing.getInstance().getAssignmentManager().removeQuantityEstimate(a1);
            System.out.println(a1);

        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
