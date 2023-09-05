package businesslogic.user;

import javafx.collections.ObservableList;

public class UserManager {
    private User currentUser;
    public void fakeLogin(String username)
    {
        this.currentUser = Chef.loadChef(username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }
    public static User getOrganizerById(int id) { return Organizer.loadOrganizerById(id); }
    public static User getChefById(int id) { return Chef.loadChefById(id); }
    public static User getCookById(int id) { return Cook.loadCookById(id); }
    public static ObservableList<Cook> getCooks() {
        return Cook.fetchCooks();
    }
}
