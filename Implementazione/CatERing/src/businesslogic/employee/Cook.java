package businesslogic.employee;

import businesslogic.assignment.Assignment;
import businesslogic.availability.Availability;
import businesslogic.event.ServiceInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Cook {
    private static Map<Integer, Cook> loadedCook = FXCollections.observableHashMap();
    private int id;
    private String name;
    private String surname;

    public static Cook getCookById(int id) {
        return loadedCook.get(id);
    }

    public void saveNewMemberAssignment(ServiceInfo currentService, Assignment a, Cook c) {
        String newA = "INSERT INTO CookAssignment (cook_id, assignment_id) VALUES (?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(newA, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, c.id);
                ps.setInt(2, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });

        /* if (result[0] > 0) { // assignment effettivamente inserito
            loadedCook.get(c.id).assignments.add(a);
        } */
    }

    public void changeAssociation(Assignment a, Cook cc, Cook nc) {
        String upd = "UPDATE CookAssignment SET member_id = ? WHERE assignment_id = ? AND member_id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, nc.id);
                ps.setInt(2, a.getId());
                ps.setInt(3, cc.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedCook.get(cc.id).assignments.remove(a);
        loadedCook.get(nc.id).assignments.add(a);
    }

    public void removeAssociation(Assignment a, Cook c) {
        String delSec = "DELETE FROM CookAssignment WHERE assignment_id = " + a.getId();
        PersistenceManager.executeUpdate(delSec);
        loadedCook.get(c.id).assignments.remove(a);
    }
    private ArrayList<Availability> declaredAvailabilities;
    private ObservableList<Assignment> assignments;

    public Cook(){
        declaredAvailabilities = new ArrayList<>();
        assignments = FXCollections.observableArrayList();
    }

    public ObservableList<Assignment> getAssignments(){
        return this.assignments;
    }

    public boolean addAssignment(Assignment a) {
        return this.assignments.add(a);
    }

    public void markAsDone(Assignment a) {
        this.assignments.remove(a);
    }

    public void deleteAssociation(Assignment a) {
        this.assignments.remove(a);
    }

    public String toString(){
        return "id: " + id + "; name: " + name + "; surname: " + surname;
    }

    public static ObservableList<Cook> loadAllCooks() {
        String query = "SELECT * FROM Cook WHERE " + true;
        ArrayList<Cook> oldMembers = new ArrayList<>();
        ArrayList<Cook> newMembers = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (loadedCook.containsKey(id)) {
                    Cook m = loadedCook.get(id);
                    m.name = rs.getString("name");
                    m.surname = rs.getString("surname");
                    oldMembers.add(m);
                } else {
                    Cook m = new Cook();
                    m.id = id;
                    m.name = rs.getString("name");
                    m.surname = rs.getString("surname");
                    newMembers.add(m);
                }
            }
        });

        for (int i = 0; i < newMembers.size(); i++) {
            Cook m = newMembers.get(i);

            String featQ = "SELECT assignment_id FROM CookAssignment WHERE cook_id = " + m.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    m.assignments.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }

        for (int i = 0; i < oldMembers.size(); i++) {
            Cook m = oldMembers.get(i);

            m.assignments.clear();
            String featQ = "SELECT assignment_id FROM CookAssignment WHERE cook_id = " + m.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    m.assignments.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }
        for (Cook s : newMembers) {
            loadedCook.put(s.id, s);
        }
        return FXCollections.observableArrayList(loadedCook.values());
    }
}
