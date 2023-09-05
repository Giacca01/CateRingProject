package businesslogic.user;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface User {
    public int getId();
    public String getUsername();
    public boolean isChef();
    public boolean isManager();
}
