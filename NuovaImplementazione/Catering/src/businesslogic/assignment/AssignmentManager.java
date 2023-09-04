package businesslogic.assignment;

import businesslogic.AssignmentException;
import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.recipe.KitchenTask;
import businesslogic.shift.Availability;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftManager;
import businesslogic.user.Cook;
import businesslogic.user.User;
import businesslogic.user.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class AssignmentManager {
    private Service currentService;
    private ObservableList<Service> openedServices = FXCollections.observableArrayList();
    private ArrayList<AssignmentEventReceiver> eventReceivers = new ArrayList<>();

    public Service createSummarySheet(Service srv) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        Event ev = srv.getEvent();
        Menu menu = srv.getMenu();
        ObservableList<Assignment> assignments = srv.getAssignments();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(assignments.size() > 0 ||
                ev == null ||
                menu == null ||
                ev.getChef().getId() != user.getId()){
            throw new AssignmentException();
        }
        srv.createSummarySheet(ev);
        this.setCurrentService(srv);
        this.notifySummarySheetCreated(srv);
        return srv;
    }

    public boolean openSummarySheet(Service srv) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        Event ev = srv.getEvent();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(srv.getAssignments() == null ||
                ev == null ||
                ev.getChef().getId() != user.getId()){
            throw new AssignmentException();
        }
        boolean operationCompleted = this.openedServices.add(srv);
        this.setCurrentService(srv);
        return operationCompleted;
    }

    public ObservableList<Assignment> addAssignment(KitchenTask task, Assignment prevKt) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        Service currentService = this.currentService;
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService.getAssignments() == null){
            throw new AssignmentException();
        }
        ObservableList<Assignment> newAssignments = this.currentService.addAssignment(task, prevKt);
        this.notifyAddedAssignment(newAssignments, currentService);
        return newAssignments;
    }

    public Assignment deleteAssignment(Assignment a) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                a.getShift() != null ||
                a.getCook() != null){
            throw new AssignmentException();
        }
        Assignment removedAssignment = this.currentService.deleteAssignment(a);
        this.notifyDeletedAssignment(a, currentService);
        return removedAssignment;
    }

    public void sortSummarySheet(Assignment a, int position) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if(!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                position <= 0 || position > this.currentService.getAssignments().size()) {
            throw new AssignmentException();
        }

        this.currentService.sortSummarySheet(a, position);
        this.notifySummarySheetSorted(currentService);
    }

    public ObservableList<Shift> showShiftsTable() throws UseCaseLogicException {
        User user = getCurrentUser();
        if(!user.isChef()) {
            throw new UseCaseLogicException();
        }
        return ShiftManager.getShifts();
    }

    public void associateAssignment(Assignment a, Shift s, Cook c) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        ObservableList<Availability> availabilities = c.getDeclaredAvailabilities();
        boolean isAvailable = true;
        for(Availability av: availabilities) {
            isAvailable = av.getService().getId() == currentService.getId() && av.getShift().getId() == s.getId() && isAvailable;
        }

        if (!user.isChef() || !isAvailable) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                s == null ||
                s.isFull() ||
                !a.hasToBePrepared()){
            throw new AssignmentException();
        }
        a.associateAssignment(s, c);
        this.notifyAssignmentAssociated(a);
    }

    public void associateAssignment(Assignment a, Shift s) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                s == null ||
                s.isFull() ||
                !a.hasToBePrepared()) {
            throw new AssignmentException();
        }
        a.associateAssignment(s, null);
    }

    public void setShiftSaturation(Shift shift, boolean full) throws UseCaseLogicException {
        User user = getCurrentUser();
        if(!user.isChef()) {
            throw new UseCaseLogicException();
        }
        shift.setFull(full);
        this.notifySaturationChanged(shift);
    }

    public void deleteAssociation(Assignment a, Shift s, Cook c) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                a.getShift().getId() != s.getId() ||
                c == null ||
                a.getCook().getId() != c.getId()) {
            throw new AssignmentException();
        }
        a.deleteAssociation(a);
        this.notifyAssociationRemoved(a);
    }

    public void changeAssignmentDetails(Assignment a, String time, String amount) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                !a.hasToBePrepared()) {
            throw new AssignmentException();
        }
        a.changeAssignmentDetails(time, amount);
        this.notifyAssignmentDetailsChanged(a);
    }

    public void changeAssignmentTimeDetail(Assignment a, String time) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                !a.hasToBePrepared()){
            throw new AssignmentException();
        }
        a.changeAssignmentDetails(time, null);
    }

    public void changeAssignmentAmountDetail(Assignment a, String amount) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                !a.hasToBePrepared()){
            throw new AssignmentException();
        }
        a.changeAssignmentDetails(null, amount);
    }

    public void setToBePrepared(Assignment a, boolean toBePrepared) throws UseCaseLogicException, AssignmentException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(this.currentService == null ||
                this.currentService.getAssignments() == null ||
                !this.currentService.getAssignments().contains(a) ||
                a.getShift() != null ||
                a.getCook() != null){
            throw new AssignmentException();
        }
        a.setToBePrepared(toBePrepared);
        this.notifyToBePreparedSet(a);
    }

    private User getCurrentUser() {
        return CatERing.getInstance().getUserManager().getCurrentUser();
    }

    private void notifySummarySheetCreated(Service srv) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSummarySheetCreated(srv);
        }
    }

    private void notifyAddedAssignment(ObservableList<Assignment> assignments, Service currentService) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAddedAssignment(assignments, currentService);
        }
    }

    public void notifyDeletedAssignment(Assignment a, Service currentService) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateDeletedAssignment(a, currentService);
        }
    }

    private void notifySummarySheetSorted(Service currentService) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSummarySheetSorted(currentService);
        }
    }

    private void notifyAssignmentAssociated(Assignment assignment) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentAssociated(assignment);
        }
    }

    private void notifySaturationChanged(Shift s) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateSaturationChanged(s);
        }
    }

    private void notifyAssociationRemoved(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssociationRemoved(a);
        }
    }

    private void notifyAssignmentDetailsChanged(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateAssignmentDetailsChanged(a);
        }
    }

    private void notifyToBePreparedSet(Assignment a) {
        for (AssignmentEventReceiver ar : this.eventReceivers) {
            ar.updateToBePreparedSet(a);
        }
    }

    public void setCurrentService(Service s) {
        this.currentService = s;
    }

    public void addEventReceiver(AssignmentEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void removeEventReceiver(AssignmentEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }

    public ObservableList<Service> getOpenedServices() {
        return openedServices;
    }

    public Service getCurrentService() {
        return currentService;
    }
    public static ObservableList<Assignment> getAssignments() {
        return Assignment.fetchAssignments();
    }
}
