package businesslogic.assignment;

import businesslogic.employee.PersonnelMember;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;

public interface AssignmentEventReceiver {
    void updateSummarySheetCreated(ServiceInfo currentService);
    void updateSummarySheetOpened(ServiceInfo currentService);

    void updateDeletedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt);

    void updateSortedSummarySheet(ServiceInfo currentService);

    void updateShiftsTablePrinted();

    void updateAssignmentAdded(ServiceInfo currentService, Shift shift, Assignment a, PersonnelMember c);

    void updateAssignmentMarkedDone(Assignment a, Shift s, PersonnelMember c);

    void updateAssociationChanged(Assignment a, Shift cs, Shift ns, PersonnelMember cc, PersonnelMember nc);

    void updateAssociationRemoved(Assignment a, Shift s, PersonnelMember c);

    void updateAssignmentDetailsSet(Assignment a);

    void updateTimeEstimateDeleted(Assignment a);

    void updateQuantityEstimateDeleted(Assignment a);

    void updateAddedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt);
}
