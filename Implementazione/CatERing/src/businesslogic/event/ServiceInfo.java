package businesslogic.event;

import businesslogic.UseCaseLogicException;
import businesslogic.assignment.Assignment;
import businesslogic.menu.Menu;
import businesslogic.recipe.KitchenTask;
import businesslogic.recipe.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class ServiceInfo implements EventItemInfo {
    private static Map<Integer, ServiceInfo> loadedServices = FXCollections.observableHashMap();
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu approvedMenu;
    private ObservableList<Assignment> assignments;

    public ServiceInfo(String name) {
        assignments = FXCollections.observableArrayList();
        this.name = name;
    }


    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp." + approvedMenu;
    }

    public void createSummarySheet(EventInfo e, Menu m){
        ObservableList<Recipe> recipes = m.getRecipes();
        for (Recipe r: recipes) {
            // recupero la lista di attività da svolgere
            // nell'ambito del compito (tutte quelle
            // che servono per completare una ricetta)
            Assignment a = new Assignment(r.getKitchenTasks());
            assignments.add(a);
        }
    }

    public int getId() {
        return id;
    }

    public Menu getApprovedMenu(){
        return this.approvedMenu;
    }

    public ObservableList<Assignment> getAssignments(){
        return this.assignments;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, approved_menu_id, service_date, time_start, time_end, expected_participants " +
                "FROM Services WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int approvedMenuId;
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                approvedMenuId = rs.getInt("approved_menu_id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                serv.approvedMenu = Menu.loadMenuById(approvedMenuId);
                result.add(serv);
            }
        });

        for(int i = 0; i < result.size(); i++){
            ServiceInfo si = result.get(i);

            String featQ = "SELECT assignment_id FROM ServiceAssignment WHERE service_id = " + si.id + " ORDER BY position";
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int assignment_id = rs.getInt("assignment_id");
                    si.assignments.add(Assignment.loadAssignmentById(assignment_id));
                }
            });

            loadedServices.put(si.id, si);
        }

        return result;
    }

    public static ServiceInfo loadServiceById(int id){
        return loadedServices.get(id);
    }

    public void deleteAssignment(KitchenTask kt, Assignment a) {
        a.deleteKitchenTask(kt);
    }

    public static void saveSummarySheet(ServiceInfo srv) {
        Assignment.saveSummarySheet(srv);
    }

    public void sortSommarySheet(Assignment a, int position) throws UseCaseLogicException {
        int assignmentsSize = this.assignments.size();
        if(assignmentsSize >= position){
            this.assignments.remove(a);
            this.assignments.add(position, a);
        } else
            throw new UseCaseLogicException();
    }

    public Assignment addAssignmentDetails(Assignment a, Integer time, Integer amount) {
        int position = this.assignments.lastIndexOf(a);
        if(position > -1){
            this.assignments.remove(a);
            if(time != null)
                a.setTimeEstimate(time);
            if(amount != null)
                a.setQuantity(amount);
            this.assignments.add(position, a);
            return a;
        } else
            return null;
    }

    public Assignment removeTimeEstimate(Assignment a) {
        int position = this.assignments.lastIndexOf(a);
        if(position > -1){
            this.assignments.remove(a);
            a.setTimeEstimate(null);
            this.assignments.add(position, a);
            return a;
        } else
            return null;
    }

    public Assignment removeQuantityEstimate(Assignment a) {
        int position = this.assignments.lastIndexOf(a);
        if(position > -1){
            this.assignments.remove(a);
            a.setQuantity(null);
            this.assignments.add(position, a);
            return a;
        } else
            return null;
    }

    public Assignment addAssignment(KitchenTask kt) {
        Assignment a = new Assignment(kt);
        this.assignments.add(a);
        return a;
    }

    public void saveSummarySheetOrder(ServiceInfo s) {
        String upd = "UPDATE ServiceAssignment SET position = ? WHERE assignment_id = ?";
        PersistenceManager.executeBatchUpdate(upd, s.assignments.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, batchCount);
                ps.setInt(2, s.assignments.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }
}
