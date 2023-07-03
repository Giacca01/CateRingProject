package persistence;

import businesslogic.assignment.Assignment;
import businesslogic.assignment.AssignmentEventReceiver;
import businesslogic.employee.Cook;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;

/*
* Listener della classe AssignmentManager: reagisce
* ai cambiamenti di quest'ultimo, per salvare su db
* le informazioni relative ai compiti che vogliamo memorizzare
* in modo permanente*/
public class AssignmentPersistence implements AssignmentEventReceiver {
    @Override
    public void updateSummarySheetCreated(ServiceInfo srv) {
        // per noi summary sheet e servizio si equivalgono
        srv.saveSummarySheet(srv);
    }

    @Override
    public void updateSummarySheetOpened(ServiceInfo currentService) {

    }

    @Override
    public void updateAddedAssignment(ServiceInfo currentService, Assignment a, KitchenTask kt) {
        // il salvataggio dell'assignment è implementato dall'assignment stesso
        // per applicazione di information expert e per evitare che la classe della persistenza
        // diventi poco coesa
        a.saveNewAssignment(a, kt);
    }

    @Override
    public void updateDeletedAssignment(ServiceInfo currentService, Assignment a) {
        a.removeAssignment(currentService, a);
    }

    @Override
    public void updateSortedSummarySheet(ServiceInfo currentService) {
        currentService.saveSummarySheetOrder(currentService);
    }

    @Override
    public void updateShiftsTablePrinted() {

    }

    @Override
    public void updateAssignmentAdded(ServiceInfo currentService, Shift shift, Assignment a, Cook c) {
        // registra il compito nel turno
        shift.saveNewAssignment(currentService, shift, a);
        // registra l'assegnazione del compito al membro del personale
        if (c != null)
            c.saveNewMemberAssignment(currentService, a, c);
    }

    @Override
    public void updateAssignmentMarkedDone(Assignment a, Shift s, Cook c) {
        if (s != null)
            s.removeAssociation(a, s);

        if (c != null)
            c.removeAssociation(a, c);
        a.saveMarkAsDone(a);
    }

    @Override
    public void updateAssociationChanged(Assignment a, Shift cs, Shift ns, Cook cc, Cook nc) {
        a.saveChangedAssociation(a, cs, ns, cc, nc);
    }

    @Override
    public void updateAssociationRemoved(Assignment a, Shift s, Cook c) {
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
