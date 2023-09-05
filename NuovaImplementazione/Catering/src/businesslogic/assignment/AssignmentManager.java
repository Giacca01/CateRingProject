package businesslogic.assignment;

import businesslogic.AssignmentException;
import businesslogic.CatERing;
import businesslogic.NoSummarySheetException;
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
        validateUserIsChef();
        validateServiceForCreation(srv);

        srv.createSummarySheet(srv.getEvent());
        setCurrentService(srv);
        notifySummarySheetCreated(srv);
        return srv;
    }

    public boolean openSummarySheet(Service srv) throws UseCaseLogicException, NoSummarySheetException {
        validateUserIsChef();
        validateServiceForOpening(srv);

        boolean operationCompleted = openedServices.add(srv);
        setCurrentService(srv);
        return operationCompleted;
    }

    public ObservableList<Assignment> addAssignment(KitchenTask task, Assignment prevKt) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateCurrentServiceForAssignment();

        ObservableList<Assignment> newAssignments = currentService.addAssignment(task, prevKt);
        notifyAddedAssignment(newAssignments, currentService);
        return newAssignments;
    }

    public Assignment deleteAssignment(Assignment a) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateCurrentServiceForAssignment();

        Assignment removedAssignment = currentService.deleteAssignment(a);
        notifyDeletedAssignment(a, currentService);
        return removedAssignment;
    }

    public void sortSummarySheet(Assignment a, int position) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateCurrentServiceForSorting(position);

        currentService.sortSummarySheet(a, position);
        notifySummarySheetSorted(currentService);
    }

    public ObservableList<Shift> showShiftsTable() throws UseCaseLogicException {
        validateUserIsChef();
        return ShiftManager.getShifts();
    }

    public void associateAssignment(Assignment a, Shift s, Cook c) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForAssociation(a, s, c);

        a.associateAssignment(s, c);
        notifyAssignmentAssociated(a);
    }

    public void associateAssignment(Assignment a, Shift s) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForAssociation(a, s, null);

        a.associateAssignment(s, null);
    }

    public void setShiftSaturation(Shift shift, boolean full) throws UseCaseLogicException {
        validateUserIsChef();
        shift.setFull(full);
        notifySaturationChanged(shift);
    }

    public void deleteAssociation(Assignment a, Shift s, Cook c) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssociationForDeletion(a, s, c);

        a.deleteAssociation(a);
        notifyAssociationRemoved(a);
    }

    public void changeAssignmentDetails(Assignment a, String time, String amount) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForDetailsChange(a);

        a.changeAssignmentDetails(time, amount);
        notifyAssignmentDetailsChanged(a);
    }

    public void changeAssignmentTimeDetail(Assignment a, String time) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForDetailsChange(a);

        a.changeAssignmentDetails(time, null);
    }

    public void changeAssignmentAmountDetail(Assignment a, String amount) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForDetailsChange(a);

        a.changeAssignmentDetails(null, amount);
    }

    public void setToBePrepared(Assignment a, boolean toBePrepared) throws UseCaseLogicException, AssignmentException {
        validateUserIsChef();
        validateAssignmentForToBePreparedChange(a);

        a.setToBePrepared(toBePrepared);
        notifyToBePreparedSet(a);
    }

    private User getCurrentUser() {
        return CatERing.getInstance().getUserManager().getCurrentUser();
    }

    private void validateUserIsChef() throws UseCaseLogicException {
        User user = getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
    }

    private void validateServiceForCreation(Service srv) throws AssignmentException {
        Event ev = srv.getEvent();
        Menu menu = srv.getMenu();
        ObservableList<Assignment> assignments = srv.getAssignments();

        if (!assignments.isEmpty() || ev == null || menu == null || ev.getChef().getId() != getCurrentUser().getId()) {
            throw new AssignmentException();
        }
    }

    private void validateServiceForOpening(Service srv) throws NoSummarySheetException {
        Event ev = srv.getEvent();

        if (srv.getAssignments().isEmpty() || ev == null || ev.getChef().getId() != getCurrentUser().getId()) {
            throw new NoSummarySheetException();
        }
    }

    private void validateCurrentServiceForAssignment() throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null) {
            throw new AssignmentException();
        }
    }

    private void validateCurrentServiceForSorting(int position) throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null || position <= 0 || position > currentService.getAssignments().size()) {
            throw new AssignmentException();
        }
    }

    private void validateAssignmentForAssociation(Assignment a, Shift s, Cook c) throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null ||
                !currentService.getAssignments().contains(a) || s == null || s.isFull() || !a.hasToBePrepared()) {
            throw new AssignmentException();
        }

        if (c != null) {
            ObservableList<Availability> availabilities = c.getDeclaredAvailabilities();
            boolean isAvailable = true;
            for (Availability av : availabilities) {
                isAvailable = av.getService().getId() == currentService.getId() && av.getShift().getId() == s.getId() && isAvailable;
            }

            if (!isAvailable) {
                throw new AssignmentException();
            }
        }
    }

    private void validateAssignmentForDetailsChange(Assignment a) throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null ||
                !currentService.getAssignments().contains(a) || !a.hasToBePrepared()) {
            throw new AssignmentException();
        }
    }

    private void validateAssignmentForToBePreparedChange(Assignment a) throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null ||
                !currentService.getAssignments().contains(a) || a.getShift() != null || a.getCook() != null) {
            throw new AssignmentException();
        }
    }

    private void validateAssociationForDeletion(Assignment a, Shift s, Cook c) throws AssignmentException {
        if (currentService == null || currentService.getAssignments() == null ||
                !currentService.getAssignments().contains(a) || a.getShift().getId() != s.getId() || c == null ||
                a.getCook().getId() != c.getId()) {
            throw new AssignmentException();
        }
    }

    private void setCurrentService(Service s) {
        this.currentService = s;
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

    private void notifyDeletedAssignment(Assignment a, Service currentService) {
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
