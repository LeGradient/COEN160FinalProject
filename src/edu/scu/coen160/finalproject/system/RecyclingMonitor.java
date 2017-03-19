package edu.scu.coen160.finalproject.system;

import java.sql.*;
import java.util.ArrayList;

public class RecyclingMonitor {
    // DATA MEMBERS

    /**
     * Username for this RMOS.
     */
    private String username;

    /**
     * Password for this RMOS.
     */
    private String password;

    /**
     * Flag to check login state.
     */
    private boolean isLoggedIn;

    /**
     * ArrayList of the RCMs that this RMOS manage.
     */
    private ArrayList<RecyclingMachine> machines;

    /**
     * Constructor.
     *
     * Initializes an RMOS with the given username, password, and RCMs.
     *
     * @param machines  An ArrayList of RCMs for this RMOS to manage.
     * @param username  The username of this RMOS.
     * @param password  The password of thsi RMOS.
     */
    public RecyclingMonitor(ArrayList<RecyclingMachine> machines, String username, String password) {
        // assign the username, password, and RCMs that this RMOS controls
        this.machines = machines;
        this.username = username;
        this.password = password;

        // RMOS is logged out by default
        this.isLoggedIn = false;
    }

    // GETTERS & SETTERS

    public double getWeight(int index) { return machines.get(index).getWeight(); }
    public double getCapacity(int index) {
        return machines.get(index).getCapacity();
    }
    public void empty(int index) { machines.get(index).empty(); }
    public double getMoney(int index) { return machines.get(index).getMoney(); }
    public void addMoney(int index, double money) { machines.get(index).addMoney(money); }
    public void setPrice(int index, String material, double price) { machines.get(index).setPrice(material, price); }
    public String[] getMachineNames() {
        ArrayList<String> names = new ArrayList<>();
        for (RecyclingMachine machine : machines) {
            names.add(machine.getTableName());
        }
        return names.toArray(new String[0]);
    }


    // OTHER METHODS

    public boolean login(String username, String password) {
        if (this.username.compareToIgnoreCase(username) == 0 && this.password.compareTo(password) == 0) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * Returns the last time an RCM was emptied.
     *
     * Constructs a SQL query to get the most recent 'empty' transaction for the specified RCM.
     *
     * @param index The index of the RCM you want to check the last empty time for.
     * @return      A String containing the timestamp of when the specified RCM was last emptied.
     */
    public String lastEmptied(int index) {
        String result;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT MAX(stamp) FROM " + machines.get(index).getTableName() + " WHERE price IS NULL;";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getString(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public double totalWeightCollected(int index) {
        double result;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT SUM(weight) " +
                            "FROM " + machines.get(index).getTableName() + " " +
                            "WHERE price IS NOT NULL;";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getDouble(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public int totalItemsCollected(int index) {
        int result;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT COUNT(*) " +
                            "FROM " + machines.get(index).getTableName() + " " +
                            "WHERE price IS NOT NULL;";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getInt(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public double totalValueIssued(int index) {
        double result;
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT SUM(price) " +
                            "FROM " + machines.get(index).getTableName() + " " +
                            "WHERE price IS NOT NULL;";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getDouble(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public int getMostUsedMachine() {
        int max = 0;
        int id = -1;
        for (int i = 0; i < machines.size(); i++) {
            if (totalItemsCollected(i) > max) {
                max = totalItemsCollected(i);
                id = machines.get(i).getId();
            }
        }
        return id;
    }

}
