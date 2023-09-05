package businesslogic.user;

import businesslogic.assignment.Assignment;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.AssignmentPersistence;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cook implements Employee {
    private static Map<Integer, Cook> loadedCooks = FXCollections.observableHashMap();
    private int id;
    private String username;
    private ObservableList<Assignment> assignments = FXCollections.observableArrayList();
    private ObservableList<Availability> declaredAvailabilities = FXCollections.observableArrayList();

    public Cook() {
    }

    public Cook(int id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isChef() {
        return false;
    }

    @Override
    public boolean isManager() {
        return false;
    }

    public ObservableList<Availability> getDeclaredAvailabilities() {
        return declaredAvailabilities;
    }

    public void setDeclaredAvailabilities(ObservableList<Availability> declaredAvailabilities) {
        this.declaredAvailabilities = declaredAvailabilities;
    }

    public void printDetails() {
        System.out.println("Cook " + this.id + "; username: " + this.username);
    }

    public static Cook loadCookById(int  id) {
        if(loadedCooks.containsKey(id)) {
            return loadedCooks.get(id);
        }

        Cook cook = new Cook();
        String query = "SELECT * FROM Users where role='c' AND id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cook.id = rs.getInt("id");
                cook.username = rs.getString("username");
            }
        });
        if(cook.id > 0) {
            query = "SELECT * FROM Availability where employee_id = '" + cook.id + "'";
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int id = rs.getInt("id");
                    boolean reserved = rs.getBoolean("reserved");
                    String role = rs.getString("role");
                    Employee employee = ServicePersonnel.loadServicePersonnelById(rs.getInt("employee_id"));
                    Service service = Service.loadServiceById(rs.getInt("service_id"));
                    Shift shift = Shift.loadShiftById(rs.getInt("shift_id"));
                    User reservedBy = UserManager.getOrganizerById(rs.getInt("reserved_by"));

                    Availability availability = new Availability(id, reserved, role, employee, service, shift, reservedBy);
                    cook.declaredAvailabilities.add(availability);
                }
            });

            String featQ = "SELECT * FROM Assignments where cook_id = '" + cook.id + "'";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int assignment_id = rs.getInt("id");
                    cook.assignments.add(Assignment.loadAssignmentById(assignment_id));
                }
            });

            loadedCooks.put(cook.id, cook);
        }
        return cook;
    }

    public static ObservableList<Cook> fetchCooks() {
        String query = "SELECT id, username FROM Users where role='c'";
        Map<Integer, Cook> newCooksMap = new HashMap<>();

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            String username = rs.getString("username");

            if (loadedCooks.containsKey(id)) {
                Cook c = loadedCooks.get(id);
                c.username = username;
            } else {
                Cook c = new Cook();
                c.id = id;
                c.username = username;
                newCooksMap.put(id, c);
            }
        });

        loadedCooks.putAll(newCooksMap);
        return FXCollections.observableArrayList(loadedCooks.values());
    }


    public static void loadCookAssignments() {
        for(Cook cook: loadedCooks.values()) {
            String featQ = "SELECT * FROM Assignments where cook_id = '" + cook.id + "'";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int assignment_id = rs.getInt("id");
                    cook.assignments.add(Assignment.loadAssignmentById(assignment_id));
                }
            });
        }
    }

    public static void loadCookDeclaredAvailabilities() {
        for(Cook cook: loadedCooks.values()) {
            String featQ = "SELECT * FROM Availability where employee_id = '" + cook.id + "'";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int availability_id = rs.getInt("id");
                    cook.declaredAvailabilities.add(Availability.loadAvailabilityById(availability_id));
                }
            });
        }
    }
}
