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


    // GETTERS & SETTERS

    public double getWeight(int index) { return machines.get(index).getWeight(); }
    public double getMoney(int index) { return machines.get(index).getMoney(); }
    public void setPrice(int index, String material, double price) { machines.get(index).setPrice(material, price); }
    public String printPrices(int index) { return machines.get(index).printPrices(); }


    // OTHER METHODS

    public Timestamp lastEmptied(int index) {
        Timestamp result = null;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT MAX(date) FROM " + machines.get(index).getTableName() + ";";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getTimestamp(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public double totalWeightCollected(int index, Timestamp start, Timestamp end) {
        double result = 0;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT SUM(weight) " +
                    "FROM " + machines.get(index).getTableName() + " " +
                    "WHERE stamp >= " + start.toString() + " " +
                    "AND stamp <= " + end.toString() + ";";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getDouble(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
