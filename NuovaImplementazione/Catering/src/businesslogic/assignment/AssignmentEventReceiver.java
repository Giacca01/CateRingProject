package businesslogic.assignment;

import businesslogic.user.Cook;
import businesslogic.event.Service;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;
import javafx.collections.ObservableList;

public interface AssignmentEventReceiver {
    void updateSummarySheetCreated(Service currentService);
    void updateAddedAssignment(ObservableList<Assignment> assignments, Service currentService);
    void updateDeletedAssignment(Assignment a, Service currentService);
    void updateSummarySheetSorted(Service currentService);
    void updateAssignmentAssociated(Assignment assignment);
    void updateAssignmentMarkedDone(Assignment assignment);
    void updateSaturationChanged(Shift shift);
    void updateAssociationRemoved(Assignment a);
    void updateAssignmentDetailsChanged(Assignment a);
    void updateTimeEstimateDeleted(Assignment a);
    void updateToBePreparedSet(Assignment a);
    void updateQuantityEstimateDeleted(Assignment a);
}
