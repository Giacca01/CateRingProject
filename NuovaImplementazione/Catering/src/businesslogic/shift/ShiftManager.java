package businesslogic.shift;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShiftManager {
    public ObservableList<Shift> registeredShifts = FXCollections.observableArrayList();

    public ShiftManager(ObservableList<Shift> registeredShifts) {
        this.registeredShifts = getShifts();
    }

    public ObservableList<Shift> showShiftTable() {
        return this.registeredShifts;
    }
    public static ObservableList<Shift> getShifts() {
        return Shift.fetchShifts();
    }
    public static ObservableList<Availability> getAvailabilities() { return Availability.fetchAvailabilities(); }
}
