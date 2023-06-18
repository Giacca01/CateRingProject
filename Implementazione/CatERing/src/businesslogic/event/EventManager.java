package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.menu.Menu;
import businesslogic.user.User;
import javafx.collections.ObservableList;

public class EventManager {
    private EventInfo currentEvent;

    public ObservableList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }

    public EventInfo createEvent(String name) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }

        EventInfo e = new EventInfo(name);
        this.setCurrentEvent(e);

        return e;
    }

    public ServiceInfo createService(String name){
        ServiceInfo s = new ServiceInfo(name);
        return s;
    }

    private void setCurrentEvent(EventInfo e){
        this.currentEvent = e;
    }

    public static EventInfo getEventById(int id){
        return EventInfo.loadEventById(id);
    }

    public static ServiceInfo getServiceById(int id){
        return ServiceInfo.loadServiceById(id);
    }
}
