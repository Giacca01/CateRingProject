package businesslogic.employee;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class EmployeesManager {
    public ObservableList<PersonnelMember> getAllMembers(){
        return PersonnelMember.loadAllMembers();
    }
    public PersonnelMember getMemberById(int id){
        return PersonnelMember.getMemById(id);
    }
}
