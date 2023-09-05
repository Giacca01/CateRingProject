package businesslogic.user;

import businesslogic.event.Service;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ServicePersonnel implements Employee{
    private static Map<Integer, ServicePersonnel> loadedServicePersonnels = FXCollections.observableHashMap();
    private int id;
    private String username;
    private static ObservableList<Availability> declaredAvailabilities;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public static ObservableList<Availability> getDeclaredAvailabilities() {
        return declaredAvailabilities;
    }

    public static void setDeclaredAvailabilities(ObservableList<Availability> declaredAvailabilities) {
        ServicePersonnel.declaredAvailabilities = declaredAvailabilities;
    }

    @Override
    public boolean isChef() {
        return false;
    }

    @Override
    public boolean isManager() {
        return false;
    }

    public ServicePersonnel() {
    }

    public ServicePersonnel(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void printDetails() {
        System.out.println("Serice Personnel " + this.id + "; username: " + this.username);
    }

    public static ServicePersonnel loadServicePersonnelById(int  id) {
        if(loadedServicePersonnels.containsKey(id)) {
            return loadedServicePersonnels.get(id);
        }

        ServicePersonnel servicePersonnel = new ServicePersonnel();
        String query = "SELECT * FROM Users where role='s' AND id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                servicePersonnel.id = rs.getInt("id");
                servicePersonnel.username = rs.getString("username");
            }
        });
        if(servicePersonnel.id > 0) {
            if(!loadedServicePersonnels.containsKey(servicePersonnel.id))
                loadedServicePersonnels.put(servicePersonnel.id, servicePersonnel);
        }
        return servicePersonnel;
    }

    public static ServicePersonnel loadServicePersonnel(String username) {
        ServicePersonnel servicePersonnel = new ServicePersonnel();
        String query = "SELECT * FROM Users where role='s' AND username = '" + username + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                servicePersonnel.id = rs.getInt("id");
                servicePersonnel.username = rs.getString("username");
            }
        });
        if(servicePersonnel.id > 0) {
            query = "SELECT * FROM Availability where employee_id = '" + servicePersonnel.id + "'";
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
                    servicePersonnel.declaredAvailabilities.add(availability);
                }
            });
            if(!loadedServicePersonnels.containsKey(servicePersonnel.id))
                loadedServicePersonnels.put(servicePersonnel.id, servicePersonnel);
        }
        return servicePersonnel;
    }
}
