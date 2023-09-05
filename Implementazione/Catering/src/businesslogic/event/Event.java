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
    private String state;
    private int duration;
    private int serviceNumber;
    private String documentation;
    private String description;
    private String initialDate;
    private String finalNotes;
    private Organizer organizer;
    private Chef chef;
    private Client commisionedBy;
    private ObservableList<Service> relatedServices;

    public Event() {
    }

    public Event(int id, String state, int duration, int serviceNumber, String documentation, String description, String initialDate, String finalNotes, Organizer organizer, Chef chef, Client commisionedBy) {
        this.id = id;
        this.duration = duration;
        this.serviceNumber = serviceNumber;
        this.documentation = documentation;
        this.description = description;
        this.initialDate = initialDate;
        this.finalNotes = finalNotes;
        this.organizer = organizer;
        this.chef = chef;
        this.commisionedBy = commisionedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(int serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String finalNotes) {
        this.initialDate = initialDate;
    }

    public String getFinalNotes() {
        return finalNotes;
    }

    public void setFinalNotes(String finalNotes) {
        this.finalNotes = finalNotes;
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

    public Client getCommisionedBy() {
        return commisionedBy;
    }

    public void setCommisionedBy(Client commisionedBy) {
        this.commisionedBy = commisionedBy;
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
