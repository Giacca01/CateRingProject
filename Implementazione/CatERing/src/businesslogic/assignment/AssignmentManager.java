package businesslogic.assignment;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.employee.PersonnelMember;
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
    private EventInfo currentEvent;
    private ObservableList<ServiceInfo> openedServices;
    private ArrayList<AssignmentEventReceiver> eventReceivers;

    public AssignmentManager() {
        eventReceivers = new ArrayList<>();
        openedServices = FXCollections.observableArrayList();
    }

    public void createSummarySheet(EventInfo e, ServiceInfo srv, Menu m) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || !user.getAssignedEvents().contains(e) || !e.getRelatedServices().contains(srv)) { //|| !m.getUsedIn().contains(srv)) {
            throw new UseCaseLogicException();
        }

        this.openedServices = FXCollections.observableArrayList();
        this.openedServices.add(srv);
        this.setCurrentEvent(e);
        this.setCurrentService(srv);

        this.currentService.createSummarySheet(e, m);
        this.notifySummarySheetCreated(this.currentService);
    }

    public void openSummarySheet(EventInfo e, ServiceInfo srv) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || !user.getAssignedEvents().contains(e) || !e.getRelatedServices().contains(srv)) {
            throw new UseCaseLogicException();
        }

        this.openedServices.add(srv);
        this.setCurrentEvent(e);
        this.setCurrentService(srv);

        this.notifySummarySheetOpened(this.currentService);
    }

    public void addAssignment(Assignment a, KitchenTask kt) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || !user.getAssignedEvents().contains(this.currentEvent)) {
            throw new UseCaseLogicException();
        }

        a.addTask(kt);
        this.notifyAddedAssignment(a, kt);
    }

    public void deleteAssignment(KitchenTask kt, Assignment a) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef() || !user.getAssignedEvents().contains(this.currentEvent) || currentService == null || !a.getTasks().contains(kt)) {
            throw new UseCaseLogicException();
        }

        this.currentService.deleteAssignment(kt, a);
        this.notifyDeletedAssignment(a, kt);
    }

    public void sortSummarySheet(Assignment a, int position) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!user.isChef()) {
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

        if (!user.isChef() || !user.getAssignedEvents().contains(this.currentEvent)) {
            throw new UseCaseLogicException();
        }

        if (CatERing.getInstance().getShiftManager().getAllShifts() != null) {
            this.notifyShiftsTablePrinted();
            return CatERing.getInstance().getShiftManager().getAllShifts();
        } else {
            this.notifyShiftsTablePrinted();
            return FXCollections.observableArrayList();
        }
    }

    public void assignTask(Assignment a, Shift s, PersonnelMember c) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getAssignments().contains(a) || s.isFull() || !a.hasToBePrepared()) {
            throw new UseCaseLogicException();
        }

        s.addAssignment(a);

        if (c != null)
            c.addAssignment(a);
        else
            throw new UseCaseLogicException();

        this.notifyAssignmentAdded(this.currentService, s, a, c);
    }

    public Assignment markAsDone(Assignment a, Shift s, PersonnelMember c) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getAssignments().contains(a)) {
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

    public void changeAssociation(Assignment a, Shift currS, Shift newS, PersonnelMember currC, PersonnelMember newC) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getAssignments().contains(a) || currS == null || !currS.getTasksToComplete().contains(a)) {
            throw new UseCaseLogicException();
        }

        if (newS != null) {
            currS.deleteAssociation(a);
            newS.addAssignment(a);
        }

        if (currC != null && newC != null) {
            currC.deleteAssociation(a);
            newC.addAssignment(a);
        } else
            throw new UseCaseLogicException();

        this.notifyAssociationChanged(a, currS, newS, currC, newC);
    }

    public Assignment deleteAssociation(Assignment a, Shift s, PersonnelMember c) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getAssignments().contains(a) || s == null || !s.getTasksToComplete().contains(a)) {
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
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSummarySheetOpened(this.currentService);
        }
    }

    private void notifyAddedAssignment(Assignment a, KitchenTask kt) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAddedAssignment(this.currentService, a, kt);
        }
    }

    private void notifyDeletedAssignment(Assignment a, KitchenTask kt) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateDeletedAssignment(this.currentService, a, kt);
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

    private void notifyAssignmentAdded(ServiceInfo s, Shift shift, Assignment a, PersonnelMember c) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentAdded(this.currentService, shift, a, c);
        }
    }

    private void notifyAssignmentMarkedDone(Assignment a, Shift s, PersonnelMember c) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentMarkedDone(a, s, c);
        }
    }

    private void notifyAssociationChanged(Assignment a, Shift cs, Shift ns, PersonnelMember cc, PersonnelMember nc) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssociationChanged(a, cs, ns, cc, nc);
        }
    }

    private void notifyAssociationRemoved(Assignment a, Shift s, PersonnelMember c) {
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

    public void setCurrentEvent(EventInfo e) {
        this.currentEvent = e;
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
