package businesslogic.event;

import javafx.collections.ObservableList;

public class EventManager {
    private Event currentEvent;
    public static ObservableList<MainEvent> getMainEvents() {
        return MainEvent.fetchMainEvents();
    }
}
