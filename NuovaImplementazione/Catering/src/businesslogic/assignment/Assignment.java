package businesslogic.assignment;

import businesslogic.event.Service;
import businesslogic.menu.MenuItem;
import businesslogic.recipe.KitchenTask;
import businesslogic.recipe.Recipe;
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
import java.sql.Types;
import java.util.HashMap;
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

    public Assignment(KitchenTask task, Service service, int position) {
        this.timeEstimate = null;
        this.quantity = null;
        this.task = task;
        this.shift = null;
        this.cook = null;
        this.service = service;
        this.toBePrepared = true;
        this.continuationOf = null;
        this.position = position;
    }

    public Assignment(KitchenTask task, Service service, Assignment continuationOf, int position){
        this.timeEstimate = null;
        this.quantity = null;
        this.task = task;
        this.shift = null;
        this.cook = null;
        this.service = service;
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

    public void associateAssignment(Shift shift, Cook cook) {
        this.shift = shift;
        if(cook != null)
            this.cook = cook;
    }

    public void deleteAssociation(Assignment assignment) {
        assignment.shift = null;
        assignment.cook = null;
    }

    public void changeAssignmentDetails(String timeEstimate, String quantity) {
        if(timeEstimate != null)
            this.timeEstimate = timeEstimate;
        if(quantity != null)
            this.quantity = quantity;
    }

    public void printDetails() {
        System.out.println("Assignment " + this.id + "; timeEstimate: " + (this.timeEstimate != null ? this.timeEstimate : "non inserito")  + "; quantity: " + (this.quantity != null ? this.quantity : "non inserito") + "; task: " + this.task.getName() + "; shift: " + (this.shift != null ? this.shift.getId() : "non assegnato") + "; cook: " + (this.cook != null ? this.cook.getUsername() : "non assegnato") + "; service: " + this.service.getType() + "; toBePrepared: " + this.toBePrepared + "; continuationOf: " + (this.continuationOf != null ? this.continuationOf.id : "no" + "; position: " + this.position));
    }

    public static ObservableList<Assignment> fetchAssignments() {
        String query = "SELECT * FROM Assignments ORDER BY position";

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            Assignment assignment = loadedAssignments.computeIfAbsent(id, k -> new Assignment());

            assignment.id = id;
            assignment.timeEstimate = rs.getString("timeEstimate");
            assignment.quantity = rs.getString("quantity");
            assignment.toBePrepared = rs.getBoolean("toBePrepared");
            assignment.position = rs.getInt("position");

            int taskId = rs.getInt("task_id");
            int shiftId = rs.getInt("shift_id");
            int cookId = rs.getInt("cook_id");
            int continuationOfId = rs.getInt("continuationOf");

            if (taskId != 0) assignment.task = Recipe.loadRecipeById(taskId);
            if (shiftId != 0) assignment.shift = Shift.loadShiftById(shiftId);
            if (cookId != 0) assignment.cook = Cook.loadCookById(cookId);

            if (continuationOfId != 0) {
                assignment.continuationOf = loadedAssignments.computeIfAbsent(continuationOfId, k -> new Assignment());
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

    public static void saveSummarySheet(Service service, Assignment assignment) {
        ObservableList<Assignment> assignments = service.getAssignments();
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
                    assignments.get(index).setId(rs.getInt(1));
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

    public static void saveAddedAssignment(Service service, ObservableList<Assignment> assignments) {
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
                    assignments.get(index).setId(rs.getInt(1));
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

    public static void saveDeletedAssignment(Service service, Assignment assignment) {
        String deleteQuery = "DELETE FROM Assignments WHERE id = " + assignment.getId();
        PersistenceManager.executeUpdate(deleteQuery);
        loadedAssignments.remove(assignment.getId());
    }

    public static void saveAssignmentAssociated(Assignment assignment){
        String updateQuery;
        if(assignment.getCook() != null)
            updateQuery = "UPDATE Assignments SET shift_id = ?, cook_id = ? WHERE id = ?";
        else
            updateQuery = "UPDATE Assignments SET shift_id = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment.getShift().getId());
                loadedAssignments.get(assignment.getId()).shift = Shift.loadShiftById(assignment.getShift().getId());
                if(assignment.getCook() != null) {
                    ps.setInt(2, assignment.getCook().getId());
                    ps.setInt(3, assignment.getId());
                    loadedAssignments.get(assignment.getId()).cook = Cook.loadCookById(assignment.getCook().getId());
                } else
                    ps.setInt(2, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }

    public static void saveAssignmentMarkedDone(Assignment assignment){
        String updateQuery = "UPDATE Assignments SET toBePrepared = false WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).toBePrepared = false;
    }

    public static void saveAssociationRemoved(Assignment assignment){
        String updateQuery = "UPDATE Assignments SET shift_id = null, cook_id = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).cook = null;
        loadedAssignments.get(assignment.getId()).shift = null;
    }

    public static void saveAssignmentDetailsChanged(Assignment assignment){
        String updateQuery = "UPDATE Assignments SET timeEstimate = ?, quantity = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, assignment.getTimeEstimate());
                ps.setString(2, assignment.getQuantity());
                ps.setInt(3, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).timeEstimate = assignment.getTimeEstimate();
        loadedAssignments.get(assignment.getId()).quantity = assignment.getQuantity();
    }

    public static void saveTimeEstimateDeleted(Assignment assignment){
        String updateQuery = "UPDATE Assignments SET timeEstimate = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).timeEstimate = null;
    }

    public static void saveToBePreparedSet(Assignment assignment) {
        String updateQuery = "UPDATE Assignments SET toBePrepared = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setBoolean(1, assignment.hasToBePrepared());
                ps.setInt(2, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).toBePrepared = assignment.hasToBePrepared();
    }

    public static void saveQuantityEstimateDeleted(Assignment assignment){
        String updateQuery = "UPDATE Assignments SET quantity = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(updateQuery, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(assignment.getId()).quantity = null;
    }
}
