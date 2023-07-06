package businesslogic.assignment;

import businesslogic.employee.Cook;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;

public interface AssignmentEventReceiver {
    void updateSummarySheetCreated(ServiceInfo currentService);
    void updateSummarySheetOpened(ServiceInfo currentService);

    void updateDeletedAssignment(ServiceInfo currentService, Assignment a);

    void updateSortedSummarySheet(ServiceInfo currentService);

    void updateShiftsTablePrinted();

    void updateAssignmentAdded(ServiceInfo currentService, Shift shift, Assignment a, Cook c);

    void updateAssignmentMarkedDone(Assignment a, Shift s, Cook c);

    void updateAssociationChanged(Assignment a, Shift cs, Shift ns, Cook cc, Cook nc);

    void updateAssociationRemoved(Assignment a, Shift s, Cook c);

    void updateAssignmentDetailsSet(Assignment a);

    void updateTimeEstimateDeleted(Assignment a);

    void updateQuantityEstimateDeleted(Assignment a);

    void updateAddedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt);
}
