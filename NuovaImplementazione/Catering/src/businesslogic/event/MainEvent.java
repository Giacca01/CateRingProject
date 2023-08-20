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

    public MainEvent() {
    }

    public MainEvent(int id, String initialNotes, boolean recurrent, String initialDate, String endDate, String state, Organizer organizer, Chef chef, int numOccurences, String dateEndRec, int period) {
        super(id, initialNotes, recurrent, initialDate, endDate, state, organizer, chef);
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
        System.out.println("Event " + getId() + ": " + getInitialNotes() + ", " + getInitialDate() + " - " + getEndDate() + ", Organizer: " + getOrganizer().getUsername() + ", Chef: " + getChef().getUsername());
    }

    public static ObservableList<MainEvent> fetchMainEvents() {
        ObservableList<MainEvent> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String initial_notes = rs.getString("initial_notes");
                boolean recurrent = rs.getBoolean("recurrent");
                String initial_date = rs.getString("initial_date");
                String end_date = rs.getString("end_date");
                String state = rs.getString("state");
                User organizer = UserManager.getOrganizerById(rs.getInt("organizer_id"));
                User chef = UserManager.getChefById(rs.getInt("chef_id"));
                int num_occurences = rs.getInt("num_occurences");
                String dateEndRec = rs.getString("dateEndRec");
                int period = rs.getInt("period");

                MainEvent event = new MainEvent(id, initial_notes, recurrent, initial_date, end_date, state, (Organizer) organizer, (Chef) chef, num_occurences, dateEndRec, period);
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
                event.setInitialDate(rs.getString("initial_notes"));
                event.setRecurrent(rs.getBoolean("recurrent"));
                event.setInitialDate(rs.getString("initial_date"));
                event.setEndDate(rs.getString("end_date"));
                event.setState(rs.getString("state"));
                event.setOrganizer((Organizer) UserManager.getOrganizerById(rs.getInt("organizer_id")));
                event.setChef((Chef) UserManager.getChefById(rs.getInt("chef_id")));
                event.setNumOccurences(rs.getInt("num_occurences"));
                event.setDateEndRec(rs.getString("dateEndRec"));
                event.setPeriod(rs.getInt("period"));
            }
        });
        return event;
    }
}
