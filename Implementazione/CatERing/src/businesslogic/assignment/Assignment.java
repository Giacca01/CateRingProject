package businesslogic.assignment;

import businesslogic.employee.Cook;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenTask;
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
import java.util.Map;

public class Assignment {
    private static Map<Integer, Assignment> loadedAssignments = FXCollections.observableHashMap();
    private int id;
    private Integer timeEstimate;
    private Integer quantity;
    private boolean toBePrepared;
    private ObservableList<KitchenTask> tasks;

    public Assignment(KitchenTask kt) {
        tasks = FXCollections.observableArrayList();
        tasks.add(kt);
        this.toBePrepared = true;
    }

    public Assignment(ObservableList<KitchenTask> kt) {
        tasks = FXCollections.observableArrayList();
        tasks.addAll(kt);
        this.toBePrepared = true;
    }

    public Assignment() {
        tasks = FXCollections.observableArrayList();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTimeEstimate(Integer timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ObservableList<KitchenTask> getTasks() {
        return this.tasks;
    }

    public boolean hasToBePrepared() {
        return toBePrepared;
    }

    public String toString() {
        return "id: " + id + "; timeEstimate: " + timeEstimate + "; quantity: " + quantity + "; toBePrepared: " + toBePrepared;
    }

    public boolean deleteKitchenTask(KitchenTask kt) {
        return tasks.remove(kt);
    }

    public int getTasksSize() {
        return tasks.size();
    }

    public Assignment setToBePrepared(boolean b) {
        this.toBePrepared = b;
        return this;
    }

    public void addTask(KitchenTask kt) {
        this.tasks.add(kt);
    }

    public static Assignment loadAssignmentById(int assignment_id) {
        // qualora il compito sia già stato caricato evito di rileggerlo
        if (loadedAssignments.containsKey(assignment_id))
            return loadedAssignments.get(assignment_id);

        Assignment a = new Assignment();
        // costruisce la rappresentazione del singolo compito
        String assignmentQuery = "SELECT * FROM Assignments WHERE id='" + assignment_id + "'";
        PersistenceManager.executeQuery(assignmentQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                a.id = rs.getInt("id");
                a.timeEstimate = rs.getInt("timeEstimate");
                a.quantity = rs.getInt("quantity");
                a.toBePrepared = rs.getBoolean("toBePrepared");
            }
        });
        if (a.id > 0) {
            loadedAssignments.put(a.id, a);
            // recupero l'elenco di mansioni associate al compito
            String featQ = "SELECT kitchentask_id FROM AssignmentKitchenTasks WHERE assignment_id = " + a.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    a.tasks.add(KitchenTask.loadKitchenTaskById(rs.getInt("kitchentask_id")));
                }
            });
        }
        return a;
    }

    public static void saveKitchenTasks(int assignment_id, ObservableList<KitchenTask> tasks) {
        String assKitchInsert = "INSERT INTO AssignmentKitchenTasks (assignment_id, kitchentask_id) VALUES (?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(assKitchInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, assignment_id);
                ps.setInt(2, tasks.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public static ObservableList<Assignment> loadAllAssignments() {
        String query = "SELECT * FROM Assignments WHERE " + true;
        ArrayList<Assignment> oldAssignments = new ArrayList<>();
        ArrayList<Assignment> newAssignments = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (loadedAssignments.containsKey(id)) {
                    Assignment a = loadedAssignments.get(id);
                    a.timeEstimate = rs.getInt("timeEstimate");
                    a.quantity = rs.getInt("quantity");
                    a.toBePrepared = rs.getBoolean("toBePrepared");
                    oldAssignments.add(a);
                } else {
                    Assignment a = new Assignment();
                    a.id = id;
                    a.timeEstimate = rs.getInt("timeEstimate");
                    a.quantity = rs.getInt("quantity");
                    a.toBePrepared = rs.getBoolean("toBePrepared");
                    newAssignments.add(a);
                }
            }
        });

        for (int i = 0; i < newAssignments.size(); i++) {
            Assignment a = newAssignments.get(i);

            // load kitchenTasks
            String featQ = "SELECT kitchentask_id FROM AssignmentKitchenTasks WHERE assignment_id = " + a.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    a.tasks.add(KitchenTask.loadKitchenTaskById(rs.getInt("kitchentask_id")));
                }
            });
        }

        for (int i = 0; i < oldAssignments.size(); i++) {
            Assignment a = oldAssignments.get(i);

            a.tasks.clear();
            // load kitchenTasks
            String featQ = "SELECT kitchentask_id FROM AssignmentKitchenTasks WHERE assignment_id = " + a.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    a.tasks.add(KitchenTask.loadKitchenTaskById(rs.getInt("kitchentask_id")));
                }
            });
        }
        for (Assignment a : newAssignments) {
            loadedAssignments.put(a.id, a);
        }
        return FXCollections.observableArrayList(loadedAssignments.values());
    }

    public static void saveSummarySheet(ServiceInfo srv) {
        ObservableList<Assignment> a = srv.getAssignments();
        // Registrare il foglio riepilogativo vuol dire registrare gli assignment
        // che lo compongono
        String assignmentInsert = "INSERT INTO Assignments (toBePrepared) VALUES (true)";
        int[] result = PersistenceManager.executeBatchUpdate(assignmentInsert, a.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {

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
            // registrare un assignment vuol dire registrare le mansioni di cucina
            // che lo formano
            for (Assignment as : a) {
                as.saveKitchenTasks(as.getId(), as.getTasks());
            }
            String assignmentServiceInsert = "INSERT INTO ServiceAssignment (service_id, assignment_id, position) VALUES (?, ?, ?)";
            PersistenceManager.executeBatchUpdate(assignmentServiceInsert, a.size(), new BatchUpdateHandler() {
                @Override
                public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                    ps.setInt(1, srv.getId());
                    ps.setInt(2, a.get(batchCount).getId());
                    ps.setInt(3, batchCount);
                }

                @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

                }
            });
            for (Assignment as : a) {
                loadedAssignments.put(as.id, as);
            }
        }
    }

    public static void saveNewAssignment(Assignment a, KitchenTask kt) {
        a.saveKitchenTask(a, kt);
    }

    private void saveKitchenTask(Assignment a, KitchenTask kt) {
        String menuInsert = "INSERT INTO Assignments (toBePrepared) VALUES (true);";
        int[] result = PersistenceManager.executeBatchUpdate(menuInsert, 1, new BatchUpdateHandler() {
            // assegna valori ai parametri indicati con ?
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {

            }

            // trattamento nuovi id creati durante esecuzione query
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    a.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // assignment effettivamente inserito
            String assKitchInsert = "INSERT INTO AssignmentKitchenTasks (assignment_id, kitchentask_id) VALUES (?, ?)";
            int[] result2 = PersistenceManager.executeBatchUpdate(assKitchInsert, 1, new BatchUpdateHandler() {
                @Override
                public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                    ps.setInt(1, a.getId());
                    ps.setInt(2, kt.getId());
                }

                @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

                }
            });
            // aggiorniamo la rappresentazione del compito interna al programma
            // in modo che tenga conto delle nuove info registrate su db
            loadedAssignments.put(a.id, a);
        }
    }

    public void removeAssignment(ServiceInfo currentService, Assignment a) {
        String delAss = "DELETE FROM ServiceAssignment WHERE assignment_id = " + a.id + " and service_id = " + currentService.getId();
        PersistenceManager.executeUpdate(delAss);

        delAss = "DELETE FROM Assignments WHERE id = " + a.id;
        PersistenceManager.executeUpdate(delAss);
        loadedAssignments.remove(a);
    }

    public void saveMarkAsDone(Assignment a) {
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
        loadedAssignments.get(a.id).toBePrepared = false;
    }

    public void saveChangedAssociation(Assignment a, Shift cs, Shift ns, Cook cc, Cook nc) {
        if (ns != null)
            cs.changeAssociation(a, cs, ns);

        if (cc != null)
            cc.changeAssociation(a, cc, nc);
    }

    public void saveDeletedAssociation(Assignment a, Shift s, Cook c) {
        s.removeAssociation(a, s);

        if (c != null)
            c.removeAssociation(a, c);
    }

    public void saveAssignedDetails(Assignment a) {
        // Esecuzione query
        String upd = "UPDATE Assignments SET timeEstimate = ?, quantity = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.timeEstimate);
                ps.setInt(2, a.quantity);
                ps.setInt(3, a.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });

        // Aggiornamento rappresentazione interna al programma
        loadedAssignments.get(a.id).timeEstimate = a.timeEstimate;
        loadedAssignments.get(a.id).quantity = a.quantity;
    }

    public void saveRemovedDetailTime(Assignment a) {
        String upd = "UPDATE Assignments SET timeEstimate = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.id).timeEstimate = null;
    }

    public void saveRemovedDetailQuantity(Assignment a) {
        String upd = "UPDATE Assignments SET quantity = null WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, a.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        loadedAssignments.get(a.id).quantity = null;
    }
}