package businesslogic.user;

import businesslogic.shift.Availability;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface Employee extends User{
    public int getId();
    public String getUsername();
    public boolean isChef();
    public boolean isManager();
    // public ObservableList<Availability> declaredAvailabilities = FXCollections.observableArrayList();
}
