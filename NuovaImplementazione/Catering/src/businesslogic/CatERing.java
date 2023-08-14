package businesslogic;

import businesslogic.assignment.AssignmentManager;
import businesslogic.event.EventManager;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.user.UserManager;
import persistence.AssignmentPersistence;
import persistence.MenuPersistence;

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

    private MenuPersistence menuPersistence;
    private AssignmentPersistence assignmentPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        assignmentMgr = new AssignmentManager();

        menuPersistence = new MenuPersistence();
        assignmentPersistence = new AssignmentPersistence();
        menuMgr.addEventReceiver(menuPersistence);
        assignmentMgr.addEventReceiver(assignmentPersistence);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public AssignmentManager getAssignmentManager() { return assignmentMgr; }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

}
