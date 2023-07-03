package businesslogic.assignment;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.employee.Cook;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Shift;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class AssignmentManager {
    private ServiceInfo currentService;
    private ObservableList<ServiceInfo> openedServices;
    private ArrayList<AssignmentEventReceiver> eventReceivers;

    public AssignmentManager() {
        eventReceivers = new ArrayList<>();
        openedServices = FXCollections.observableArrayList();
    }

    public boolean createSummarySheet(EventInfo e, ServiceInfo srv, Menu m) throws UseCaseLogicException {
        boolean operationCompleted;
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() ||
                !user.getAssignedEvents().contains(e) ||
                !e.getRelatedServices().contains(srv) ||
                !m.getUsedIn().contains(srv)
        ) {
            throw new UseCaseLogicException();
        }

        this.openedServices = FXCollections.observableArrayList();
        this.openedServices.add(srv);
        this.setCurrentService(srv);

        operationCompleted = this.currentService.createSummarySheet(e, m);
        this.notifySummarySheetCreated(this.currentService);
        return operationCompleted;
    }

    public boolean openSummarySheet(EventInfo e, int srv_id) throws UseCaseLogicException {
        boolean operationCompleted = true;
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        ServiceInfo srv = CatERing.getInstance().getEventManager().getServiceById(srv_id);

        if (!user.isChef() || !user.getAssignedEvents().contains(e) || !e.getRelatedServices().contains(srv)) {
            throw new UseCaseLogicException();
        }

        operationCompleted = operationCompleted && this.openedServices.add(srv);
        this.currentService = srv;
        this.notifySummarySheetOpened(this.currentService);
        return operationCompleted;
    }

    public Assignment addAssignment(KitchenTask kt) throws UseCaseLogicException {
        Assignment a;
        User user = CatERing.getInstance().getUserManager().getCurrentUser();


        if (!user.isChef() || this.currentService == null) {
            throw new UseCaseLogicException();
        }

        a = this.currentService.addAssignment(kt);
        this.notifyAddedAssignment(a, kt);
        return a;
    }

    public boolean deleteAssignment(Assignment a, Shift s, Cook c) throws UseCaseLogicException {
        boolean operationCompleted;
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || currentService == null || !currentService.getAssignments().contains(a)) {
            throw new UseCaseLogicException();
        }

        operationCompleted = this.currentService.deleteAssignment(a, s, c);
        this.deleteAssociation(a, s, c);
        this.notifyDeletedAssignment(a);
        return operationCompleted;
    }

    public void sortSummarySheet(Assignment a, int position) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || currentService == null) {
            throw new UseCaseLogicException();
        }

        if (position > 0)
            this.currentService.sortSommarySheet(a, position);
        else
            throw new UseCaseLogicException();

        this.notifySortedSummarySheet(this.currentService);
    }

    public ObservableList<Shift> showShiftTable() throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        ObservableList<Shift> result = null;

        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }


        if (CatERing.getInstance().getShiftManager().getAllShifts() != null)
            result = CatERing.getInstance().getShiftManager().getAllShifts();
        else
            result = FXCollections.observableArrayList();

        this.notifyShiftsTablePrinted();
        return result;
    }

    public boolean assignTask(Assignment a, Shift s, Cook c) throws UseCaseLogicException {
        boolean wasAdded;
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService == null || !this.currentService.getAssignments().contains(a) || s.isFull() || !a.hasToBePrepared()) {
            throw new UseCaseLogicException();
        }

        wasAdded = s.addAssignment(a);

        if (c != null)
            wasAdded = wasAdded && c.addAssignment(a);

        this.notifyAssignmentAdded(this.currentService, s, a, c);
        return wasAdded;
    }

    public Assignment markAsDone(Assignment a, Shift s, Cook c) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService == null || !this.currentService.getAssignments().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (s != null)
            s.markAsDone(a);
        if (c != null)
            c.markAsDone(a);

        a.setToBePrepared(false);

        this.notifyAssignmentMarkedDone(a, s, c);
        return a;
    }

    public void changeAssociation(Assignment a, Shift currS, Shift newS, Cook currC, Cook newC) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService == null || !this.currentService.getAssignments().contains(a) || currS == null || !currS.getTasksToComplete().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (newS != null) {
            currS.deleteAssociation(a);
            newS.addAssignment(a);
        } else
            throw new UseCaseLogicException();

        if (currC != null && newC != null) {
            currC.deleteAssociation(a);
            newC.addAssignment(a);
        } else if (currC != null && newC == null)
            throw new UseCaseLogicException();

        this.notifyAssociationChanged(a, currS, newS, currC, newC);
    }

    public Assignment deleteAssociation(Assignment a, Shift s, Cook c) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() ||
                this.currentService == null ||
                !this.currentService.getAssignments().contains(a) ||
                s == null ||
                !s.getTasksToComplete().contains(a)
        ) {
            throw new UseCaseLogicException();
        }

        s.deleteAssociation(a);

        if (c != null) {
            c.deleteAssociation(a);
        }

        this.notifyAssociationRemoved(a, s, c);
        return a;
    }

    public Assignment addAssignmentDetails(Assignment a, int time, int amount) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || currentService == null || !this.currentService.getAssignments().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (this.currentService.addAssignmentDetails(a, time, amount) == null)
            throw new UseCaseLogicException();


        this.notifyAssignmentDetailsSet(a);
        return a;
    }

    public Assignment removeTimeEstimate(Assignment a) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || currentService == null || !this.currentService.getAssignments().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (this.currentService.removeTimeEstimate(a) == null)
            throw new UseCaseLogicException();

        this.notifyTimeEstimateDeleted(a);
        return a;
    }

    public Assignment removeQuantityEstimate(Assignment a) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || currentService == null || !this.currentService.getAssignments().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (this.currentService.removeQuantityEstimate(a) == null)
            throw new UseCaseLogicException();

        this.notifyQuantityEstimateDeleted(a);
        return a;
    }

    private void notifySummarySheetCreated(ServiceInfo srv) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSummarySheetCreated(srv);
        }
    }

    private void notifySummarySheetOpened(ServiceInfo srv) {
        // non sappiamo chi sia il ricevente: l'interfaccia garantisce basso accoppiamento
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSummarySheetOpened(this.currentService);
        }
    }

    private void notifyAddedAssignment(Assignment a, KitchenTask kt) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAddedAssignment(this.currentService, a, kt);
        }
    }

    private void notifyDeletedAssignment(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateDeletedAssignment(this.currentService, a);
        }
    }

    private void notifySortedSummarySheet(ServiceInfo s) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSortedSummarySheet(this.currentService);
        }
    }

    private void notifyShiftsTablePrinted() {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateShiftsTablePrinted();
        }
    }

    private void notifyAssignmentAdded(ServiceInfo s, Shift shift, Assignment a, Cook c) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentAdded(this.currentService, shift, a, c);
        }
    }

    private void notifyAssignmentMarkedDone(Assignment a, Shift s, Cook c) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentMarkedDone(a, s, c);
        }
    }

    private void notifyAssociationChanged(Assignment a, Shift cs, Shift ns, Cook cc, Cook nc) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssociationChanged(a, cs, ns, cc, nc);
        }
    }

    private void notifyAssociationRemoved(Assignment a, Shift s, Cook c) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssociationRemoved(a, s, c);
        }
    }

    private void notifyAssignmentDetailsSet(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentDetailsSet(a);
        }
    }

    private void notifyTimeEstimateDeleted(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateTimeEstimateDeleted(a);
        }
    }

    private void notifyQuantityEstimateDeleted(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateQuantityEstimateDeleted(a);
        }
    }

    public void setCurrentService(ServiceInfo s) {
        this.currentService = s;
    }

    public Assignment getAssignmentById(int id) {
        return Assignment.loadAssignmentById(id);
    }

    public ObservableList<Assignment> getAllAssignments() {
        return Assignment.loadAllAssignments();
    }

    public void addEventReceiver(AssignmentEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void removeEventReceiver(AssignmentEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }
}
