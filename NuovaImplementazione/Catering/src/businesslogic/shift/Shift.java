package businesslogic.shift;

import businesslogic.assignment.Assignment;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.Cook;
import businesslogic.user.UserManager;
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

public class Shift {
    private static Map<Integer, Shift> loadedShifts = FXCollections.observableHashMap();
    private int id;
    private boolean full;
    private ObservableList<Assignment> tasksToComplete = FXCollections.observableArrayList();

    public Shift() {
    }

    public Shift(int id, boolean full) {
        this.id = id;
        this.full = full;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean markAsDone(Assignment a){
        loadedShifts.get(this.id).tasksToComplete.remove(a);
        return this.tasksToComplete.remove(a);
    }

    public void printDetails() {
        System.out.println("Shift " + this.id + "; full: " + this.full);
    }

    /*public static ObservableList<Shift> fetchShifts() {
        if(!loadedShifts.isEmpty()){
            return FXCollections.observableArrayList(loadedShifts.values());
        }

        String query = "SELECT * FROM Shift WHERE " + true;
        ArrayList<Shift> newShifts = new ArrayList<>();
        ArrayList<Shift> oldShifts = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
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

        for (Shift s: newShifts) {
            loadedShifts.put(s.id, s);
        }
        return FXCollections.observableArrayList(loadedShifts.values());
    }*/

    public static ObservableList<Shift> fetchShifts() {
        if (!loadedShifts.isEmpty()) {
            return FXCollections.observableArrayList(loadedShifts.values());
        }

        String query = "SELECT id, full FROM Shift";
        Map<Integer, Shift> newShiftsMap = new HashMap<>();

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            boolean full = rs.getBoolean("full");

            if (loadedShifts.containsKey(id)) {
                Shift s = loadedShifts.get(id);
                s.full = full;
            } else {
                Shift s = new Shift();
                s.id = id;
                s.full = full;

                newShiftsMap.put(id, s);
            }
        });

        loadedShifts.putAll(newShiftsMap);
        return FXCollections.observableArrayList(loadedShifts.values());
    }


    public static void loadShiftTasksToComplete() {
        for(Shift shift: loadedShifts.values()) {
            String featQ = "SELECT * FROM Assignments where shift_id = '" + shift.id + "'";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int assignment_id = rs.getInt("id");
                    shift.tasksToComplete.add(Assignment.loadAssignmentById(assignment_id));
                }
            });
        }
    }

    public static Shift loadShiftById(int  id) {
        if(loadedShifts.containsKey(id)) {
            return loadedShifts.get(id);
        }

        Shift shift = new Shift();
        String query = "SELECT * FROM Shift where id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shift.id = rs.getInt("id");
                shift.full = rs.getBoolean("full");
            }
        });

        String featQ = "SELECT * FROM Assignments where shift_id = '" + shift.id + "'";
        PersistenceManager.executeQuery(featQ, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int assignment_id = rs.getInt("id");
                shift.tasksToComplete.add(Assignment.loadAssignmentById(assignment_id));
            }
        });

        loadedShifts.put(shift.id, shift);
        return shift;
    }

    public static void saveSaturationChanged(Shift shift) {
        String upd = "UPDATE Shift SET full = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setBoolean(1, shift.full);
                ps.setInt(2, shift.getId());

                loadedShifts.get(shift.getId()).setFull(shift.full);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }
}
