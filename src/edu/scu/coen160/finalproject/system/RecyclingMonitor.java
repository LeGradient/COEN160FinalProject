package edu.scu.coen160.finalproject.system;

import java.sql.*;
import java.util.ArrayList;

/**
 * RecyclingMonitor class
 *
 * Represents an RMOS. Allows users to login, and manage a number of RCMs as well as get statistics about them.
 */
public class RecyclingMonitor {

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
     * @param password  The password of this RMOS.
     */
    public RecyclingMonitor(ArrayList<RecyclingMachine> machines, String username, String password) {
        // assign the username, password, and RCMs that this RMOS controls
        this.machines = machines;
        this.username = username;
        this.password = password;

        // RMOS is logged out by default
        this.isLoggedIn = false;
    }

    /**
     * Gets the current used weight of an RCM.
     * @param index The index of the RCM to check.
     * @return      The current weight used of that RCM.
     */
    public double getWeight(int index) {
        return machines.get(index).getWeight();
    }

    /**
     * Gets the overall capacity of an RCM.
     * @param index The index of the RCM to check.
     * @return      The overall capacity of that RCM.
     */
    public double getCapacity(int index) {
        return machines.get(index).getCapacity();
    }

    /**
     * Empties an RCM.
     *
     * @param index The index of the RCM to empty.
     */
    public void empty(int index) {
        machines.get(index).empty();
    }

    /**
     * Gets the amount of money in an RCM.
     *
     * @param index The RCM to check.
     * @return      The amount of money left in that RCM.
     */
    public double getMoney(int index) {
        return machines.get(index).getMoney();
    }

    /**
     * Adds money to an RCM.
     * @param index The RCM to add money to.
     * @param money The amount of money to add.
     */
    public void addMoney(int index, double money) {
        machines.get(index).addMoney(money);
    }

    /**
     * Sets the price of a material for an RCM.
     * @param index     The RCM to update.
     * @param material  The material to update.
     * @param price     The price to set.
     */
    public void setPrice(int index, String material, double price) {
        machines.get(index).setPrice(material, price);
    }

    /**
     * Gets the names of all the RCMs.
     *
     * @return  An array of Strings containing the names of every RCM for this RMOS.
     */
    public String[] getMachineNames() {
        ArrayList<String> names = new ArrayList<>();
        for (RecyclingMachine machine : machines) {
            names.add(machine.getTableName());
        }
        return names.toArray(new String[0]);
    }

    /**
     * Allows user to login to the RMOS.
     *
     * @param username  Username to login with.
     * @param password  Password to login with.
     * @return          true if login succeeded; otherwise false.
     */
    public boolean login(String username, String password) {
        // if the username and password match, log in and return true
        if (this.username.compareToIgnoreCase(username) == 0 && this.password.compareTo(password) == 0) {
            this.isLoggedIn = true;
            return true;
        }

        // return false if login was unsuccessful
        return false;
    }

    /**
     * Gets the last time an RCM was emptied.
     *
     * Constructs a SQL query to get the most recent 'empty' transaction for the specified RCM.
     *
     * @param index The index of the RCM you want to check the last empty time for.
     * @return      A String containing the timestamp of when the specified RCM was last emptied.
     */
    public String lastEmptied(int index) {
        // string to hold the result
        String result;

        // try connecting to the database
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            // if successful
            Statement stmt = connection.createStatement();

            // construct the SQL query and get the results
            String sql = "SELECT MAX(stamp) FROM " + machines.get(index).getTableName() + " WHERE price IS NULL;";
            ResultSet resultset = stmt.executeQuery(sql);
            result = resultset.getString(1);  // SQL result columns are 1-indexed
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Gets the total weight of items recycled by an RCM.
     *
     * Constructs a SQL query to get the total weight of recycled items for the specified RCM.
     *
     * @param index The index of the RCM you want to check the total for.
     * @return      The total weight of the items recycled by that RCM.
     */
    public double totalWeightCollected(int index) {
        // double to hold the result
        double result;

        // try connecting to the database
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            //if successful
            Statement stmt = connection.createStatement();

            // construct the SQL query and get the results
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

    /**
     * Gets the total number of items recycled by an RCM.
     *
     * Constructs a SQL query to get the total number of items recycled by the specified RCM.
     *
     * @param index The index of the RCM you want to check the total for.
     * @return      The total number of items recycled by that RCM.
     */
    public int totalItemsCollected(int index) {
        // int to hold result
        int result;

        // try connecting to the database
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            // if successful
            Statement stmt = connection.createStatement();

            // construct the SQL query and get the results
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

    /**
     * Gets the total value issued by an RCM.
     *
     * Constructs a SQL query to get the total value issued by the specified RCM.
     *
     * @param index The index of the RCM you want to check the total for.
     * @return      The total value issued by that RCM.
     */
    public double totalValueIssued(int index) {
        // double to hold the result
        double result;

        // try connecting to the database
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            // if successful
            Statement stmt = connection.createStatement();

            // construct the SQL query and get the results
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
}
