package businesslogic.event;

import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.menu.Menu;
import businesslogic.recipe.KitchenTask;
import businesslogic.recipe.Preparation;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import businesslogic.user.Cook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Service {
    private static Map<Integer, Service> loadedServices = FXCollections.observableHashMap();
    private int id;
    private String type;
    private String date;
    private String location;
    private int attendance;
    private boolean approved;
    private String timeSlot;
    private Event event;
    private Menu menu;
    private ObservableList<Assignment> assignments = FXCollections.observableArrayList();

    public Service() {
    }

    public Service(int id, String type, String date, String location, int attendance, boolean approved, String timeSlot, Event event, Menu menu) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.location = location;
        this.attendance = attendance;
        this.approved = approved;
        this.timeSlot = timeSlot;
        this.event = event;
        this.menu = menu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public ObservableList<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(ObservableList<Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void printDetails() {
        System.out.println("Service " + this.id + "; event: " + this.event.getDescription() + "; type: " + this.type + "; menu: " + this.menu.getTitle() + "; date: " + this.date + "; location: " + this.location + "; attendance: " + this.attendance + "; approved: " + this.approved + "; timeSlot: " + this.timeSlot);
    }

    public void createSummarySheet(Event e) {
        ObservableList<KitchenTask> kitchenTasks = this.menu.getRecipes();
        for (KitchenTask task : kitchenTasks) {
            // recupero la lista di attività da svolgere
            // nell'ambito del compito (tutte quelle
            // che servono per completare una ricetta)
            int lastAssignmentPosition = getLastAssignmentPosition();
            Assignment a;
            if(lastAssignmentPosition != -1) {
                a = new Assignment(task, this, lastAssignmentPosition + 1);
            } else {
                a = new Assignment(task, this, 1);
            }
            this.assignments.add(a);
        }
    }

    public ObservableList<Assignment> addAssignment(KitchenTask task, Assignment prevKt) {
        ObservableList<Assignment> newAssignments = FXCollections.observableArrayList();
        Assignment a;
        int lastAssignmentPosition = getLastAssignmentPosition();
        if(lastAssignmentPosition != -1) {
            a = new Assignment(task, this, prevKt, lastAssignmentPosition + 1);
        } else {
            a = new Assignment(task, this, prevKt, 1);
        }
        newAssignments.add(a);
        this.assignments.add(a);
        for(KitchenTask ingredient: task.getIngredients()) {
            lastAssignmentPosition = getLastAssignmentPosition();
            if(lastAssignmentPosition != -1) {
                a = new Assignment(ingredient, this, lastAssignmentPosition + 1);
            } else {
                a = new Assignment(ingredient, this, 1);
            }
            newAssignments.add(a);
            this.assignments.add(a);
        }
        return newAssignments;
    }

    public Assignment deleteAssignment(Assignment a) {
        this.assignments.remove(a);
        return a;
    }

    private int getLastAssignmentPosition() {
        if(!this.assignments.isEmpty())
            return this.assignments.get(this.assignments.size() - 1).getPosition();
        else
            return -1;
    }

    public void sortSummarySheet(Assignment a, int position) {
        this.assignments.remove(a);
        this.assignments.add((position - 1), a);
        int index = 1;
        for(Assignment assignment: this.assignments){
            assignment.setPosition(index);
            index++;
        }
    }

    public static ObservableList<Service> fetchServices() {
        String query = "SELECT id, event_id, type, menu_id, service_date, location, attendance, approved, timeSlot FROM Services";
        Map<Integer, Service> newServicesMap = new HashMap<>();
        Map<Integer, Integer> newEventIds = new HashMap<>();
        Map<Integer, Integer> newMenuIds = new HashMap<>();
        ArrayList<Service> oldServices = new ArrayList<>();
        ArrayList<Integer> oldEventIds = new ArrayList<>();
        ArrayList<Integer> oldMenuIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            int eventId = rs.getInt("event_id");
            String type = rs.getString("type");
            int menuId = rs.getInt("menu_id");
            String date = rs.getString("service_date");
            String location = rs.getString("location");
            int attendance = rs.getInt("attendance");
            boolean approved = rs.getBoolean("approved");
            String timeSlot = rs.getString("timeSlot");

            if (loadedServices.containsKey(id)) {
                Service s = loadedServices.get(id);
                oldEventIds.add(eventId);
                oldMenuIds.add(menuId);
                s.type = type;
                s.date = date;
                s.location = location;
                s.attendance = attendance;
                s.approved = approved;
                s.timeSlot = timeSlot;

                oldServices.add(s);
            } else {
                Service s = new Service();
                s.id = id;
                newEventIds.put(id, eventId);
                newMenuIds.put(id, menuId);
                s.type = type;
                s.date = date;
                s.location = location;
                s.attendance = attendance;
                s.approved = approved;
                s.timeSlot = timeSlot;

                newServicesMap.put(id, s);
            }
        });

        for (Map.Entry<Integer, Service> entry : newServicesMap.entrySet()) {
            int id = entry.getKey();
            Service s = entry.getValue();
            int eventId = newEventIds.get(id);
            int menuId = newMenuIds.get(id);

            s.event = MainEvent.fetchMainEventById(eventId);
            s.menu = Menu.loadMenuById(menuId);

            String featQ = "SELECT id FROM Assignments WHERE service_id = '" + id + "'";
            PersistenceManager.executeQuery(featQ, rs -> {
                int assignmentId = rs.getInt("id");
                s.assignments.add(Assignment.loadAssignmentById(assignmentId));
                s.assignments.get(s.assignments.size() - 1).setService(s);
            });

            // Ordinamento manuale degli assegnamenti
            s.assignments.sort((a1, a2) -> Integer.compare(a1.getPosition(), a2.getPosition()));
        }

        for (int i = 0; i < oldServices.size(); i++) {
            Service s = oldServices.get(i);
            int eventId = oldEventIds.get(i);
            int menuId = oldMenuIds.get(i);

            s.event = MainEvent.fetchMainEventById(eventId);
            s.menu = Menu.loadMenuById(menuId);

            String featQ = "SELECT id FROM Assignments WHERE service_id = '" + s.id + "'";
            PersistenceManager.executeQuery(featQ, rs -> {
                int assignmentId = rs.getInt("id");
                s.assignments.add(Assignment.loadAssignmentById(assignmentId));
                s.assignments.get(s.assignments.size() - 1).setService(s);
            });

            // Ordinamento manuale degli assegnamenti
            s.assignments.sort((a1, a2) -> Integer.compare(a1.getPosition(), a2.getPosition()));
        }

        loadedServices.putAll(newServicesMap);

        return FXCollections.observableArrayList(loadedServices.values());
    }


    public static Service loadServiceById(int  id) {
        if(loadedServices.containsKey(id)) {
            return loadedServices.get(id);
        }
        Service service = new Service();
        String query = "SELECT * FROM Services where id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                service.id = rs.getInt("id");
                service.type = rs.getString("type");
                service.menu = Menu.loadMenuById(rs.getInt("menu_id"));
                service.date = rs.getString("service_date");
                service.location = rs.getString("location");
                service.attendance = rs.getInt("attendance");
                service.approved = rs.getBoolean("approved");
                service.timeSlot = rs.getString("timeSlot");
            }
        });

        String featQ = "SELECT * FROM Assignments where service_id = '" + service.id + "'";
        PersistenceManager.executeQuery(featQ, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int assignment_id = rs.getInt("id");
                service.assignments.add(Assignment.loadAssignmentById(assignment_id));
                service.assignments.get(service.assignments.size() - 1).setService(service);
            }
        });
        Collections.sort(service.assignments, (a1, a2) -> Integer.compare(a1.getPosition(), a2.getPosition()));
        loadedServices.put(service.id, service);
        return service;
    }

    public static void saveSummarySheet(Service srv, Assignment a) {
        Assignment.saveSummarySheet(srv, a);
    }

    public static void saveAddedAssignment(Service srv, ObservableList<Assignment> assignments) {
        Assignment.saveAddedAssignment(srv, assignments);
    }

    public static void saveDeletedAssignment(Service srv, Assignment a) {
        Assignment.saveDeletedAssignment(srv, a);
    }

    public static void saveSortedSummarySheet(Service s) {
        String upd = "UPDATE Assignments SET position = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, s.assignments.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, s.assignments.get(batchCount).getPosition());
                ps.setInt(2, s.assignments.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }
}
