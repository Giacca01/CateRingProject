package businesslogic.assignment;

import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenTask;
import businesslogic.recipe.Recipe;
import businesslogic.recipe.RecipeManager;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftManager;
import businesslogic.user.Cook;
import businesslogic.user.User;
import businesslogic.user.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

public class Assignment {
    private static Map<Integer, Assignment> loadedAssignments = FXCollections.observableHashMap();
    private int id;
    private String timeEstimate;
    private String quantity;
    private KitchenTask task;
    private Shift shift;
    private Cook cook;
    private Service service;
    private boolean toBePrepared;
    private Assignment continuationOf;
    private int position;

    public Assignment() {
    }

    public Assignment(KitchenTask task, Service srv, int position) {
        this.timeEstimate = null;
        this.quantity = null;
        this.task = task;
        this.shift = null;
        this.cook = null;
        this.service = srv;
        this.toBePrepared = true;
        this.continuationOf = null;
        this.position = position;
    }

    public Assignment(KitchenTask task, Service srv, Assignment continuationOf, int position){
        this.timeEstimate = null;
        this.quantity = null;
        this.task = task;
        this.shift = null;
        this.cook = null;
        this.service = srv;
        this.toBePrepared = true;
        this.continuationOf = continuationOf;
        this.position = position;
    }

    public Assignment(int id, String timeEstimate, String quantity, KitchenTask task, Shift shift, Cook cook, boolean toBePrepared, Assignment continuationOf, int position) {
        this.id = id;
        this.timeEstimate = timeEstimate;
        this.quantity = quantity;
        this.task = task;
        this.shift = shift;
        this.cook = cook;
        this.toBePrepared = toBePrepared;
        this.continuationOf = continuationOf;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(String timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public KitchenTask getTask() {
        return task;
    }

    public void setTask(KitchenTask task) {
        this.task = task;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public Assignment getContinuationOf() {
        return continuationOf;
    }

    public void setContinuationOf(Assignment continuationOf) {
        this.continuationOf = continuationOf;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean hasToBePrepared() {
        return this.toBePrepared;
    }

    public void setToBePrepared(boolean toBePrepared){
        this.toBePrepared = toBePrepared;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void associateAssignment(Shift s, Cook c) {
        this.shift = s;
        if(c != null)
            this.cook = c;
    }

    public void deleteAssociation(Assignment a) {
        a.shift = null;
        a.cook = null;
    }

    public void changeAssignmentDetails(String time, String amount) {
        if(time != null)
            this.timeEstimate = time;
        if(amount != null)
            this.quantity = amount;
    }

    public void printDetails() {
        System.out.println("Assignment " + this.id + "; timeEstimate: " + (this.timeEstimate != null ? this.timeEstimate : "non inserito")  + "; quantity: " + (this.quantity != null ? this.quantity : "non inserito") + "; task: " + this.task.getName() + "; shift: " + (this.shift != null ? this.shift.getId() : "non assegnato") + "; cook: " + (this.cook != null ? this.cook.getUsername() : "non assegnato") + "; service: " + this.service.getType() + "; toBePrepared: " + this.toBePrepared + "; continuationOf: " + (this.continuationOf != null ? this.continuationOf.id : "no" + "; position: " + this.position));
    }

    public static ObservableList<Assignment> fetchAssignments() {
        String query = "SELECT * FROM Assignments ORDER BY position";

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            Assignment a = loadedAssignments.computeIfAbsent(id, k -> new Assignment());

            a.id = id;
            a.timeEstimate = rs.getString("timeEstimate");
            a.quantity = rs.getString("quantity");
            a.toBePrepared = rs.getBoolean("toBePrepared");
            a.position = rs.getInt("position");

            int taskId = rs.getInt("task_id");
            int shiftId = rs.getInt("shift_id");
            int cookId = rs.getInt("cook_id");
            int continuationOf = rs.getInt("continuationOf");

            if (taskId != 0) a.task = Recipe.loadRecipeById(taskId);
            if (shiftId != 0) a.shift = Shift.loadShiftById(shiftId);
            if (cookId != 0) a.cook = Cook.loadCookById(cookId);

            if (continuationOf != 0) {
                a.continuationOf = loadedAssignments.computeIfAbsent(continuationOf, k -> new Assignment());
            }
        });

        Shift.loadShiftTasksToComplete();
        Cook.loadCookAssignments();

        return FXCollections.observableArrayList(loadedAssignments.values());
    }

    public static Assignment loadAssignmentById(int id) {
        if(loadedAssignments.containsKey(id)) {
            return loadedAssignments.get(id);
        }

        Assignment assignment = new Assignment();
        String query = "SELECT * FROM Assignments where id = '" + id + "' ORDER BY position";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                assignment.id = rs.getInt("id");
                assignment.timeEstimate = rs.getString("timeEstimate");
                assignment.quantity = rs.getString("quantity");
                assignment.task = Recipe.loadRecipeById(rs.getInt("task_id"));
                assignment.shift = Shift.loadShiftById(rs.getInt("shift_id"));
                assignment.cook = Cook.loadCookById(rs.getInt("cook_id"));
                assignment.toBePrepared = rs.getBoolean("toBePrepared");
                assignment.continuationOf = Assignment.loadAssignmentById(rs.getInt("continuationOf"));
                assignment.position = rs.getInt("position");
            }
        });
        loadedAssignments.put(assignment.id, assignment);
        return assignment;
    }

    public static void saveSummarySheet(Service srv, Assignment assignemnt) {
        ObservableList<Assignment> a = srv.getAssignments();
        // Registrare il foglio riepilogativo vuol dire registrare gli assignment
        // che lo compongono
        String assignmentInsert = "INSERT INTO Assignments (timeEstimate, quantity, task_id, shift_id, cook_id, service_id, toBePrepared, continuationOf, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(assignmentInsert, a.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, a.get(batchCount).getTimeEstimate());
                ps.setString(2, a.get(batchCount).getQuantity());
                ps.setObject(3, a.get(batchCount).getTask() != null ? a.get(batchCount).getTask().getId() : null);
                ps.setObject(4, a.get(batchCount).getShift() != null ? a.get(batchCount).getShift().getId() : null);
                ps.setObject(5, a.get(batchCount).getCook() != null ? a.get(batchCount).getCook().getId() : null);
                ps.setInt(6, a.get(batchCount).getService().getId());
                ps.setBoolean(7, a.get(batchCount).toBePrepared);
                ps.setObject(8, a.get(batchCount).getContinuationOf() != null ? a.get(batchCount).getContinuationOf().getId() : null);
                ps.setInt(9, a.get(batchCount).getPosition());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                rs.beforeFirst();
                int index = 0;
                while (rs.next()) {
                    a.get(index).setId(rs.getInt(1)); // salvo l'id del nuovo assignment nel vettore
                    index++;
                }
            }
        });

        if (result[0] > 0) {
            for (Assignment as : a) {
                loadedAssignments.put(as.id, as);
            }
        }
    }

    public static void saveAddedAssignment(Service srv, ObservableList<Assignment> assignments) {
        // Registrare il foglio riepilogativo vuol dire registrare gli assignment
        // che lo compongono
        String assignmentInsert = "INSERT INTO Assignments (timeEstimate, quantity, task_id, shift_id, cook_id, service_id, toBePrepared, continuationOf, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(assignmentInsert, assignments.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, assignments.get(batchCount).getTimeEstimate());
                ps.setString(2, assignments.get(batchCount).getQuantity());
                ps.setObject(3, assignments.get(batchCount).getTask() != null ? assignments.get(batchCount).getTask().getId() : null);
                ps.setObject(4, assignments.get(batchCount).getShift() != null ? assignments.get(batchCount).getShift().getId() : null);
                ps.setObject(5, assignments.get(batchCount).getCook() != null ? assignments.get(batchCount).getCook().getId() : null);
                ps.setInt(6, assignments.get(batchCount).getService().getId());
                ps.setBoolean(7, assignments.get(batchCount).toBePrepared);
                ps.setObject(8, assignments.get(batchCount).getContinuationOf() != null ? assignments.get(batchCount).getContinuationOf().getId() : null);
                ps.setInt(9, assignments.get(batchCount).getPosition());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                rs.beforeFirst();
                int index = 0;
                while (rs.next()) {
                    assignments.get(index).setId(rs.getInt(1)); // salvo l'id del nuovo assignment nel vettore
                    index++;
                }
            }
        });

        if (result[0] > 0) {
            for (Assignment as : assignments) {
                loadedAssignments.put(as.id, as);
            }
        }
    }

    public static void saveDeletedAssignment(Service srv, Assignment a) {
        String del = "DELETE FROM Assignments WHERE id = " + a.getId();
        PersistenceManager.executeUpdate(del);
        loadedAssignments.remove(a);
    }

    public static void saveAssignmentAssociated(Assignment a){
        String upd;
        if(a.getCook() != null)
            upd = "UPDATE Assignments SET shift_id = ?, cook_id = ? WHERE id = ?";
        else
            upd = "UPDATE Assignments SET shift_id = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
        @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.getShift().getId());
                loadedAssignments.get(a.getId()).shift = Shift.loadShiftById(a.getShift().getId());
                if(a.getCook() != null) {
                    ps.setInt(2, a.getCook().getId());
                    ps.setInt(3, a.getId());
                    loadedAssignments.get(a.getId()).cook = Cook.loadCookById(a.getCook().getId());
                } else
                    ps.setInt(2, a.getId());
            }

            @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                    // no generated ids to handle
                }
            });
    }

    public static void saveAssignmentMarkedDone(Assignment a){
        String upd = "UPDATE Assignments SET toBePrepared = false WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).toBePrepared = false;
    }

    public static void saveAssociationRemoved(Assignment a){
        String upd = "UPDATE Assignments SET shift_id = null, cook_id = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).cook = null;
        loadedAssignments.get(a.getId()).shift = null;
    }

    public static void saveAssignmentDetailsChanged(Assignment a){
        String upd = "UPDATE Assignments SET timeEstimate = ?, quantity = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, a.getTimeEstimate());
                ps.setString(2, a.getQuantity());
                ps.setInt(3, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).timeEstimate = a.getTimeEstimate();
        loadedAssignments.get(a.getId()).quantity = a.getQuantity();
    }

    public static void saveTimeEstimateDeleted(Assignment a){
        String upd = "UPDATE Assignments SET timeEstimate = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).timeEstimate = null;
    }

    public static void saveToBePreparedSet(Assignment a) {
        String upd = "UPDATE Assignments SET toBePrepared = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setBoolean(1, a.hasToBePrepared());
                ps.setInt(2, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).toBePrepared = a.hasToBePrepared();
    }

    public static void saveQuantityEstimateDeleted(Assignment a){
        String upd = "UPDATE Assignments SET quantity = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.getId()).quantity = null;
    }
}
