package businesslogic.shift;

import businesslogic.employee.PersonnelMember;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ShiftManager {
    private ObservableList<Shift> registeredShifts = FXCollections.observableArrayList();

    public ObservableList<Shift> getRegisteredShifts() {
        return this.registeredShifts;
    }

    public ObservableList<Shift> getAllShifts(){
        return Shift.loadAllShifts();
    }

    public Shift getShiftById(int id){
        return Shift.loadShiftById(id);
    }
}
