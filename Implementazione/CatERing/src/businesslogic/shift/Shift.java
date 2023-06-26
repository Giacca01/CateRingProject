package businesslogic.shift;

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

public class Shift {
    private static Map<Integer, Shift> loadedShifts = FXCollections.observableHashMap();
    private int id;
    private boolean full;
    private ArrayList<Availability> referredAvailabilities;
    private ArrayList<Assignment> tasksToComplete;

    public Shift(){
        referredAvailabilities = new ArrayList<>();
        tasksToComplete = new ArrayList<>();
    }

    public static Shift loadShiftById(int id) {
        return loadedShifts.get(id);
    }

    public boolean isFull(){
        return full;
    }

    public void addAssignment(Assignment a) {
        this.tasksToComplete.add(a);
    }

    public void markAsDone(Assignment a) {
        this.tasksToComplete.remove(a);
    }

    public ArrayList<Assignment> getTasksToComplete() {
        return tasksToComplete;
    }

    public void deleteAssociation(Assignment a) {
        this.tasksToComplete.remove(a);
    }

    public String toString(){
        return "id: " + id + "; full: " + full;
    }

    public static ObservableList<Shift> loadAllShifts() {
        String query = "SELECT * FROM Shift WHERE " + true;
        ArrayList<Shift> oldShifts = new ArrayList<>();
        ArrayList<Shift> newShifts = new ArrayList<>();

        // con le prossime tre query costruiamo la rappresentazione
        // dei turni interna al programma, composta da:
        //      1) dati generali del turno
        //      2) compiti associati al turno
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                // distinguiamo tra servizi nuovi e quelli già noti
                // per cancellare eliminare, dalla rappresentazione
                // di questi ultimi, la lista dei compiti e ricrearla
                if (loadedShifts.containsKey(id)) {
                    Shift s = loadedShifts.get(id);
                    s.full = rs.getBoolean("full");
                    oldShifts.add(s);
                } else {
                    Shift s = new Shift();
                    s.id = id;
                    s.full = rs.getBoolean("full");
                    newShifts.add(s);
                }
            }
        });

        for (int i = 0; i < newShifts.size(); i++) {
            Shift s = newShifts.get(i);
            // recuperiamo i compiti associati al turno
            String featQ = "SELECT assignment_id FROM ShiftAssignment WHERE shift_id = " + s.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    // recuperiamo i dettagli del compito
                    s.tasksToComplete.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }

        for (int i = 0; i < oldShifts.size(); i++) {
            Shift s = oldShifts.get(i);

            s.tasksToComplete.clear();
            String featQ = "SELECT assignment_id FROM ShiftAssignment WHERE shift_id = " + s.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    s.tasksToComplete.add(Assignment.loadAssignmentById(rs.getInt("assignment_id")));
                }
            });
        }
        for (Shift s : newShifts) {
            loadedShifts.put(s.id, s);
        }
        return FXCollections.observableArrayList(loadedShifts.values());
    }

    public void saveNewAssignment(ServiceInfo currentService, Shift shift, Assignment a) {
        String newA = "INSERT INTO ShiftAssignment (shift_id, assignment_id) VALUES (?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(newA, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, shift.id);
                ps.setInt(2, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });

        if (result[0] > 0) { // assignment effettivamente inserito ==> aggiorno la rappresentazione interna al programma
            loadedShifts.get(shift.id).tasksToComplete.add(a);
        }
    }

    public void changeAssociation(Assignment a, Shift cs, Shift ns) {
        String upd = "UPDATE ShiftAssignment SET shift_id = ? WHERE assignment_id = ? AND shift_id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, ns.id);
                ps.setInt(2, a.getId());
                ps.setInt(3, cs.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedShifts.get(cs.id).tasksToComplete.remove(a);
        loadedShifts.get(ns.id).tasksToComplete.add(a);
    }

    public void removeAssociation(Assignment a, Shift s) {
        String delSec = "DELETE FROM ShiftAssignment WHERE assignment_id = " + a.getId();
        PersistenceManager.executeUpdate(delSec);
        loadedShifts.get(s.id).tasksToComplete.remove(a);
    }
}
