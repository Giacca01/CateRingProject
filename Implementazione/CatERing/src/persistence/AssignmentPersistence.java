package persistence;

import businesslogic.assignment.Assignment;
import businesslogic.assignment.AssignmentEventReceiver;
import businesslogic.employee.PersonnelMember;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;

public class AssignmentPersistence implements AssignmentEventReceiver {
    @Override
    public void updateSummarySheetCreated(ServiceInfo srv) {
        srv.saveSummarySheet(srv);
    }

    @Override
    public void updateSummarySheetOpened(ServiceInfo currentService) {

    }

    @Override
    public void updateAddedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt) {
        a.saveNewAssignment(currentService, a, kt);
    }

    @Override
    public void updateDeletedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt) {
        a.removeAssignment(currentService, a, kt);
    }

    @Override
    public void updateSortedSummarySheet(ServiceInfo currentService) {
        currentService.saveSummarySheetOrder(currentService);
    }

    @Override
    public void updateShiftsTablePrinted() {

    }

    @Override
    public void updateAssignmentAdded(ServiceInfo currentService, Shift shift, Assignment a, PersonnelMember c) {
        shift.saveNewAssignment(currentService, shift, a);
        c.saveNewMemberAssignment(currentService, a, c);
    }

    @Override
    public void updateAssignmentMarkedDone(Assignment a, Shift s, PersonnelMember c) {
        s.removeAssociation(a, s);
        c.removeAssociation(a, c);
        a.saveMarkAsDone(a);
    }

    @Override
    public void updateAssociationChanged(Assignment a, Shift cs, Shift ns, PersonnelMember cc, PersonnelMember nc) {
        a.saveChangedAssociation(a, cs, ns, cc, nc);
    }

    @Override
    public void updateAssociationRemoved(Assignment a, Shift s, PersonnelMember c) {
        a.saveDeletedAssociation(a, s, c);
    }

    @Override
    public void updateAssignmentDetailsSet(Assignment a) {
        a.saveAssignedDetails(a);
    }

    @Override
    public void updateTimeEstimateDeleted(Assignment a) {
        a.saveRemovedDetailTime(a);
    }

    @Override
    public void updateQuantityEstimateDeleted(Assignment a) {
        a.saveRemovedDetailQuantity(a);
    }
}
