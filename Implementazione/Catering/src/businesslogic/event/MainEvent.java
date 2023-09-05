package businesslogic.event;

import businesslogic.user.Chef;
import businesslogic.user.Organizer;
import businesslogic.user.User;
import businesslogic.user.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainEvent extends Event {
    private int numOccurences;
    private String dateEndRec;
    private int period;
    User user;

    public MainEvent() {
    }

    public MainEvent(int id, String state, int duration, int serviceNumber, String documentation, String description, String initialDate, String initialNotes, Organizer organizer, Chef chef, Client client, int numOccurences, String dateEndRec, int period) {
        super(id, state, duration, serviceNumber, documentation, description, initialDate,  initialNotes, organizer, chef, client);
        this.numOccurences = numOccurences;
        this.dateEndRec = dateEndRec;
        this.period = period;
    }

    public int getNumOccurences() {
        return numOccurences;
    }

    public void setNumOccurences(int numOccurences) {
        this.numOccurences = numOccurences;
    }

    public String getDateEndRec() {
        return dateEndRec;
    }

    public void setDateEndRec(String dateEndRec) {
        this.dateEndRec = dateEndRec;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void printDetails() {
        System.out.println("Event " + getId() + ": " + getInitialDate() + ", " + getDuration() + ", " + getDescription() + ", Organizer: " + getOrganizer().getUsername() + ", Chef: " + getChef().getUsername());
    }

    public static ObservableList<MainEvent> fetchMainEvents() {
        ObservableList<MainEvent> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String state = rs.getString("state");
                int duration = rs.getInt("duration");
                int serviceNumber = rs.getInt("servicesNumber");
                String documentation = rs.getString("documentation");
                String description = rs.getString("description");
                String initialDate = rs.getString("initial_date");
                String finalNotes = rs.getString("final_notes");
                User organizer = UserManager.getOrganizerById(rs.getInt("organizer_id"));
                User chef = UserManager.getChefById(rs.getInt("chef_id"));
                Client client = Client.fetchClientById(rs.getInt("client_id"));
                int num_occurences = rs.getInt("num_occurences");
                String dateEndRec = rs.getString("dateEndRec");
                int period = rs.getInt("period");

                MainEvent event = new MainEvent(id, state, duration, serviceNumber, documentation, description, initialDate, finalNotes, (Organizer) organizer, (Chef) chef, (Client) client, num_occurences, dateEndRec, period);
                events.add(event);
            }
        });
        return events;
    }

    public static MainEvent fetchMainEventById(int id) {
        MainEvent event = new MainEvent();
        String query = "SELECT * FROM Events where id='" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                event.setId(rs.getInt("id"));
                event.setState(rs.getString("state"));
                event.setDuration(rs.getInt("duration"));
                event.setServiceNumber(rs.getInt("servicesNumber"));
                event.setDocumentation(rs.getString("documentation"));
                event.setDescription(rs.getString("description"));
                event.setInitialDate(rs.getString("initial_date"));
                event.setFinalNotes(rs.getString("final_notes"));
                event.setOrganizer((Organizer) UserManager.getOrganizerById(rs.getInt("organizer_id")));
                event.setChef((Chef) UserManager.getChefById(rs.getInt("chef_id")));
                // TODO: metti client
                event.setNumOccurences(rs.getInt("num_occurences"));
                event.setDateEndRec(rs.getString("dateEndRec"));
                event.setPeriod(rs.getInt("period"));
            }
        });
        return event;
    }
}
