package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

// interfaccia che fissa le caratteristiche
// delle classi che processeranno i risultati delle query
// evitando a chi la usa di conoscere il tipo
// effettivo delle stesse
public interface ResultHandler {
    public void handle(ResultSet rs) throws SQLException;
}
