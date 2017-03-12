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
        Date result = null;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT MAX(date) FROM " + machines.get(index).getTableName() + ";";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getDate(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
