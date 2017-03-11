package edu.scu.coen160.finalproject.system;

import java.sql.*;
import java.util.ArrayList;

public class RecyclingMonitor {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private ArrayList<RecyclingMachine> machines;

    public RecyclingMonitor(ArrayList<RecyclingMachine> machines, String username, String password) {
        this.machines = machines;
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }


    // OTHER METHODS

    public Date lastEmptied(int index) {
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT MAX(date) FROM " + machines.get(index).getTableName() + ";";
            ResultSet result = stmt.executeQuery(sql);
            // TODO: do something with the results
            stmt.close();
            connection.close();
            // TODO: return something
        } catch (SQLException e) {
            // TODO: construct a runtime exception and throw it?
        }
    }
}
