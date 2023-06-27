package businesslogic.employee;

import javafx.collections.ObservableList;

public class EmployeesManager {
    public ObservableList<Cook> getAllMembers(){
        return Cook.loadAllCooks();
    }
    public Cook getMemberById(int id){
        return Cook.getCookById(id);
    }
}
