package businesslogic.shift;

import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Availability {
    private static Map<Integer, Availability> loadedAvailabilities = FXCollections.observableHashMap();
    private int id;
    private boolean reserved;
    private String role;
    private Employee employee;
    private Service service;
    private Shift shift;
    private User reservedBy;

    public Availability() {
    }

    public Availability(int id, boolean reserved, String role, Employee employee, Service service, Shift shift, User reservedBy) {
        this.id = id;
        this.reserved = reserved;
        this.role = role;
        this.employee = employee;
        this.service = service;
        this.shift = shift;
        this.reservedBy = reservedBy;
    }

    public static Map<Integer, Availability> getLoadedAvailabilities() {
        return loadedAvailabilities;
    }

    public static void setLoadedAvailabilities(Map<Integer, Availability> loadedAvailabilities) {
        Availability.loadedAvailabilities = loadedAvailabilities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public User getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(User reservedBy) {
        this.reservedBy = reservedBy;
    }

    public void printDetails() {
        System.out.println("Availability: " + this.id + "; reserved: " + this.reserved + ", employee: " + this.employee.getUsername() + ", service: " + this.service.getType() + ", shift: " + this.shift.getId() + ", reserved by: " + this.reservedBy.getUsername());
    }

    public static ObservableList<Availability> fetchAvailabilities() {
        String query = "SELECT id, reserved, role, employee_id, service_id, shift_id, reserved_by FROM Availability";
        Map<Integer, Availability> newAvailabilitiesMap = new HashMap<>();
        Map<Integer, Integer> newEmployeeIds = new HashMap<>();
        Map<Integer, Integer> newServiceIds = new HashMap<>();
        Map<Integer, Integer> newShiftIds = new HashMap<>();
        Map<Integer, Integer> newReservedByIds = new HashMap<>();
        ArrayList<Availability> oldAvailabilities = new ArrayList<>();
        ArrayList<Integer> oldEmployeeIds = new ArrayList<>();
        ArrayList<Integer> oldServiceIds = new ArrayList<>();
        ArrayList<Integer> oldShiftIds = new ArrayList<>();
        ArrayList<Integer> oldReservedByIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            boolean reserved = rs.getBoolean("reserved");
            String role = rs.getString("role");
            int employeeId = rs.getInt("employee_id");
            int serviceId = rs.getInt("service_id");
            int shiftId = rs.getInt("shift_id");
            int reservedById = rs.getInt("reserved_by");

            if (loadedAvailabilities.containsKey(id)) {
                Availability a = loadedAvailabilities.get(id);
                a.reserved = reserved;
                a.role = role;
                oldEmployeeIds.add(employeeId);
                oldServiceIds.add(serviceId);
                oldShiftIds.add(shiftId);
                oldReservedByIds.add(reservedById);

                oldAvailabilities.add(a);
            } else {
                Availability a = new Availability();
                a.id = id;
                a.reserved = reserved;
                a.role = role;
                newEmployeeIds.put(id, employeeId);
                newServiceIds.put(id, serviceId);
                newShiftIds.put(id, shiftId);
                newReservedByIds.put(id, reservedById);

                newAvailabilitiesMap.put(id, a);
            }
        });

        for (Map.Entry<Integer, Availability> entry : newAvailabilitiesMap.entrySet()) {
            int id = entry.getKey();
            Availability a = entry.getValue();
            int employeeId = newEmployeeIds.get(id);
            int serviceId = newServiceIds.get(id);
            int shiftId = newShiftIds.get(id);
            int reservedById = newReservedByIds.get(id);

            if (employeeId != 0) a.employee = Cook.loadCookById(employeeId);
            if (serviceId != 0) a.service = Service.loadServiceById(serviceId);
            if (shiftId != 0) a.shift = Shift.loadShiftById(shiftId);
            if (reservedById != 0) a.reservedBy = UserManager.getOrganizerById(reservedById);
        }

        for (int i = 0; i < oldAvailabilities.size(); i++) {
            Availability a = oldAvailabilities.get(i);
            int employeeId = oldEmployeeIds.get(i);
            int serviceId = oldServiceIds.get(i);
            int shiftId = oldShiftIds.get(i);
            int reservedById = oldReservedByIds.get(i);

            if (employeeId != 0) a.employee = Cook.loadCookById(employeeId);
            if (serviceId != 0) a.service = Service.loadServiceById(serviceId);
            if (shiftId != 0) a.shift = Shift.loadShiftById(shiftId);
            if (reservedById != 0) a.reservedBy = UserManager.getOrganizerById(reservedById);
        }

        loadedAvailabilities.putAll(newAvailabilitiesMap);
        Cook.loadCookDeclaredAvailabilities();
        return FXCollections.observableArrayList(loadedAvailabilities.values());
    }


    public static Availability loadAvailabilityById(int id) {
        if(loadedAvailabilities.containsKey(id)){
            return loadedAvailabilities.get(id);
        }

        Availability availability = new Availability();
        String query = "SELECT * FROM Availability where id = '" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                availability.id = rs.getInt("id");
                availability.reserved = rs.getBoolean("reserved");
                availability.role = rs.getString("role");
                availability.employee = Cook.loadCookById(rs.getInt("employee_id"));
                availability.service = Service.loadServiceById(rs.getInt("service_id"));
                availability.shift = Shift.loadShiftById(rs.getInt("shift_id"));
                availability.reservedBy = UserManager.getOrganizerById(rs.getInt("reserved_by"));
            }
        });
        if(availability.id > 0 && !loadedAvailabilities.containsKey(availability.id)) {
            loadedAvailabilities.put(availability.id, availability);
        }
        return availability;
    }
}
