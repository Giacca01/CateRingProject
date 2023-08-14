package businesslogic.event;

import businesslogic.menu.Menu;
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
import java.util.ArrayList;

public abstract class Event {
    private int id;
    private String initialNotes;
    private boolean recurrent;
    private ArrayList<Menu> pastMenus;
    private String initialDate;
    private String endDate;
    private String state;
    private Organizer organizer;
    private Chef chef;
    private ObservableList<Service> relatedServices;

    public Event() {
    }

    public Event(int id, String initialNotes, boolean recurrent, String initialDate, String endDate, String state, Organizer organizer, Chef chef) {
        this.id = id;
        this.initialNotes = initialNotes;
        this.recurrent = recurrent;
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.state = state;
        this.organizer = organizer;
        this.chef = chef;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInitialNotes() {
        return initialNotes;
    }

    public void setInitialNotes(String initialNotes) {
        this.initialNotes = initialNotes;
    }

    public boolean isRecurrent() {
        return recurrent;
    }

    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }

    public ArrayList<Menu> getPastMenus() {
        return pastMenus;
    }

    public void setPastMenus(ArrayList<Menu> pastMenus) {
        this.pastMenus = pastMenus;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public ObservableList<Service> getRelatedServices() {
        return relatedServices;
    }

    public void setRelatedServices(ObservableList<Service> relatedServices) {
        this.relatedServices = relatedServices;
    }
}
