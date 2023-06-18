package businesslogic;

import businesslogic.assignment.Assignment;
import businesslogic.assignment.AssignmentManager;
import businesslogic.availability.AvailabilitiesManager;
import businesslogic.availability.Availability;
import businesslogic.employee.EmployeesManager;
import businesslogic.event.EventManager;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftManager;
import businesslogic.user.UserManager;
import persistence.AssignmentPersistence;
import persistence.MenuPersistence;
import persistence.PersistenceManager;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private AssignmentManager assignmentMgr;
    private ShiftManager shiftMgr;
    private AvailabilitiesManager availabilitiesMgr;
    private EmployeesManager employeesMgr;
    private MenuPersistence menuPersistence;
    private AssignmentPersistence assignmentPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        menuPersistence = new MenuPersistence();
        assignmentPersistence = new AssignmentPersistence();
        shiftMgr = new ShiftManager();
        assignmentMgr = new AssignmentManager();
        availabilitiesMgr = new AvailabilitiesManager();
        employeesMgr = new EmployeesManager();
        menuMgr.addEventReceiver(menuPersistence);
        assignmentMgr.addEventReceiver(assignmentPersistence);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }
    public ShiftManager getShiftManager() { return shiftMgr; }
    public AssignmentManager getAssignmentManager() { return assignmentMgr; }
    public AvailabilitiesManager getAvailabilitiesManager() { return availabilitiesMgr; }
    public EmployeesManager getEmployeesManager() { return employeesMgr; }
}
