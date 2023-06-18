package businesslogic.employee;

import businesslogic.assignment.Assignment;
import businesslogic.availability.Availability;
import businesslogic.event.ServiceInfo;
import businesslogic.shift.Shift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonnelMember {
    private static Map<Integer, PersonnelMember> loadedPersonnelMember = FXCollections.observableHashMap();
    private int id;
    private String name;
    private String surname;

    public static PersonnelMember getMemById(int id) {
        return loadedPersonnelMember.get(id);
    }

    public void saveNewMemberAssignment(ServiceInfo currentService, Assignment a, PersonnelMember c) {
        String newA = "INSERT INTO PersonnelMemberAssignment (member_id, assignment_id) VALUES (?, ?)";
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

        if (result[0] > 0) { // assignment effettivamente inserito
            loadedPersonnelMember.get(c.id).assignments.add(a);
        }
    }

    public void changeAssociation(Assignment a, PersonnelMember cc, PersonnelMember nc) {
        String upd = "UPDATE PersonnelMemberAssignment SET member_id = ? WHERE assignment_id = ? AND member_id = ?";
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
        loadedPersonnelMember.get(cc.id).assignments.remove(a);
        loadedPersonnelMember.get(nc.id).assignments.add(a);
    }

    public void removeAssociation(Assignment a, PersonnelMember c) {
        String delSec = "DELETE FROM PersonnelMemberAssignment WHERE assignment_id = " + a.getId();
        PersistenceManager.executeUpdate(delSec);
        loadedPersonnelMember.get(c.id).assignments.remove(a);
    }

    public static enum Role {SERVIZIO, CUOCO};
    private ArrayList<Availability> declaredAvailabilities;
    private ObservableList<Assignment> assignments;

    public PersonnelMember(){
        declaredAvailabilities = new ArrayList<>();
        assignments = FXCollections.observableArrayList();
    }

    public ObservableList<Assignment> getAssignments(){
        return this.assignments;
    }

    public void addAssignment(Assignment a) {
        this.assignments.add(a);
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

    public static ObservableList<PersonnelMember> loadAllMembers() {
        String query = "SELECT * FROM PersonnelMember WHERE " + true;
        ArrayList<PersonnelMember> oldMembers = new ArrayList<>();
        ArrayList<PersonnelMember> newMembers = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (loadedPersonnelMember.containsKey(id)) {
                    PersonnelMember m = loadedPersonnelMember.get(id);
                    m.name = rs.getString("name");
                    m.surname = rs.getString("surname");
                    oldMembers.add(m);
                } else {
                    PersonnelMember m = new PersonnelMember();
                    m.id = id;
                    m.name = rs.getString("name");
                    m.surname = rs.getString("surname");
                    newMembers.add(m);
                }
            }
        });

        for (int i = 0; i < newMembers.size(); i++) {
            PersonnelMember m = newMembers.get(i);

            String featQ = "SELECT assignment_id FROM PersonnelMemberAssignment WHERE member_id = " + m.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    m.assignments.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }

        for (int i = 0; i < oldMembers.size(); i++) {
            PersonnelMember m = oldMembers.get(i);

            m.assignments.clear();
            String featQ = "SELECT assignment_id FROM PersonnelMemberAssignment WHERE member_id = " + m.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    m.assignments.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }
        for (PersonnelMember s : newMembers) {
            loadedPersonnelMember.put(s.id, s);
        }
        return FXCollections.observableArrayList(loadedPersonnelMember.values());
    }
}
