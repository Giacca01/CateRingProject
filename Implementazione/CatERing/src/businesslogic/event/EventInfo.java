package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class EventInfo implements EventItemInfo {
    private static Map<Integer, EventInfo> loadedEvents = FXCollections.observableHashMap();
    private int id;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer;

    private ObservableList<ServiceInfo> relatedServices;

    public EventInfo(String name) {
        this.name = name;
        id = 0;
    }

    public void addService(ServiceInfo s){
        if(this.relatedServices == null)
            relatedServices = FXCollections.observableArrayList();
        this.relatedServices.add(s);
    }

    public ObservableList<ServiceInfo> getRelatedServices() {
        return FXCollections.unmodifiableObservableList(this.relatedServices);
    }

    public String toString() {
        return name + ": " + dateStart + "-" + dateEnd + ", " + participants + " pp. (" + organizer.getUserName() + ")";
    }

    public static EventInfo loadEventById(int id){
        return loadedEvents.get(id);
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<EventInfo> loadAllEventInfo() {
        ObservableList<EventInfo> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                EventInfo e = new EventInfo(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                all.add(e);
            }
        });

        for (EventInfo e : all) {
            e.relatedServices = ServiceInfo.loadServiceInfoForEvent(e.id);
            loadedEvents.put(e.id, e);
        }
        return all;
    }
}
