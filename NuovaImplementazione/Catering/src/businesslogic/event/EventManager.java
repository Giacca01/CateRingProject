package businesslogic.event;

import javafx.collections.ObservableList;

public class EventManager {
    private Event currentEvent;
    public static ObservableList<RecurrentEvent> getRecurrentEvents() {
        return RecurrentEvent.fetchRecurrentEvents();
    }
}
