package businesslogic.event;

import businesslogic.user.Chef;
import businesslogic.user.Organizer;
import businesslogic.user.UserManager;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Client {
    private int id;
    private String nome;
    private String cognome;

    public Client() {
    }

    public Client(int id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public static Client fetchClientById(int id) {
        Client client = new Client();
        String query = "SELECT * FROM Clients where id='" + id + "'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                client.setId(rs.getInt("id"));
                client.setNome(rs.getString("nome"));
                client.setCognome(rs.getString("cognome"));
            }
        });
        return client;
    }
}
