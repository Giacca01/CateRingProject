package persistence;

import businesslogic.CatERing;
import businesslogic.assignment.Assignment;
import businesslogic.assignment.AssignmentEventReceiver;
import businesslogic.event.Service;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;
import businesslogic.user.Cook;
import javafx.collections.ObservableList;

/*
* Listener della classe AssignmentManager: reagisce
* ai cambiamenti di quest'ultimo, per salvare su db
* le informazioni relative ai compiti che vogliamo memorizzare
* in modo permanente*/
public class AssignmentPersistence implements AssignmentEventReceiver {
    @Override
    public void updateSummarySheetCreated(Service currentService) {
        currentService.saveSummarySheet(currentService, null);
    }

    @Override
    public void updateAddedAssignment(ObservableList<Assignment> assignments, Service currentService) {
        currentService.saveAddedAssignment(currentService, assignments);
    }

    @Override
    public void updateDeletedAssignment(Assignment a, Service currentService) {
        currentService.saveDeletedAssignment(currentService, a);
    }

    public void updateSummarySheetSorted(Service currentService) {
        currentService.saveSortedSummarySheet(currentService);
    }

    public void updateAssignmentAssociated(Assignment assignment) {
        assignment.saveAssignmentAssociated(assignment);
    }

    public void updateAssignmentMarkedDone(Assignment assignment) {
        assignment.saveAssignmentMarkedDone(assignment);
    }

    public void updateSaturationChanged(Shift shift) {
        shift.saveSaturationChanged(shift);
    }

    public void updateAssociationRemoved(Assignment assignment) {
        assignment.saveAssociationRemoved(assignment);
    }

    public void updateAssignmentDetailsChanged(Assignment assignment) {
        assignment.saveAssignmentDetailsChanged(assignment);
    }

    public void updateTimeEstimateDeleted(Assignment assignment) {
        assignment.saveTimeEstimateDeleted(assignment);
    }

    public void updateToBePreparedSet(Assignment assignment) {
        assignment.saveToBePreparedSet(assignment);
    }

    public void updateQuantityEstimateDeleted(Assignment assignment) {
        assignment.saveQuantityEstimateDeleted(assignment);
    }
}
