package edu.scu.coen160.finalproject.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TransactionRecord class
 * Represents a record of a transaction made by an RCM.
 */
class TransactionRecord {
    /**
     * The item used in this transaction.
     */
    private RecyclableItem item;

    /**
     * The value dispensed in this transaction.
     */
    private double price;

    /**
     * Specifies the database to use.
     */
    public static final String database = "jdbc:sqlite:TransactionRecords.db";

    /**
     * SQL statement for creating a new table.
     */
    public static final String newTable = "DROP TABLE IF EXISTS %s;" +
                                    "CREATE TABLE %s (" +
                                        "material VARCHAR(15) DEFAULT NULL," +
                                        "weight NUMBER(4,2) DEFAULT NULL," +
                                        "price NUMBER(4,2) DEFAULT NULL," +
                                        "stamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                                    ");";

    /**
     * Gets the value dispensed in this transaction.
     * @return  The value dispensed in this transaction.
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Gets the weight of the item in this transaction.
     * @return  The weight of the item in this transaction.
     */
    public double getWeight() {
        return item.getWeight();
    }

    /**
     * Initializes a TransactionRecord with the given item and value.
     * @param item  The item involved in this transaction.
     * @param price The value dispensed in this transaction.
     */
    public TransactionRecord(RecyclableItem item, double price) {
        this.item = item;
        this.price = price;
    }

    /**
     * Writes a transaction to the database.
     * @param tableName The table in the database to write to.
     */
    public void writeToTable(String tableName) {
        // try connecting to DB
        try (Connection connection = DriverManager.getConnection(database)) {
            //if successful
            String sql;
            Statement stmt = connection.createStatement();

            // if this transaction represents an emptying of the RCM, insert null values into table
            if (this.item == null) {
                sql = "INSERT INTO " + tableName + " DEFAULT VALUES;";
            } else {
                // this is a real transaction, so insert the appropriate values into the DB
                sql = "INSERT INTO " + tableName + " (material, weight, price) VALUES ('" + this.item.getMaterial() + "'," +
                                                                    this.item.getWeight() + "," +
                                                                    this.price + ");";
            }

            // check to make sure the statement executed
            int numRows = stmt.executeUpdate(sql);
            assert(numRows == 1);
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement elt : trace) {
                System.out.println(elt);
            }
            System.out.println(e);
        }
    }

    /**
     * Generates a string representation of the transaction.
     *
     * @return  A string containing the material, weight, and value of the item in the transaction.
     */
    public String toString() {
        String result = item.getMaterial();
        result += " " + String.valueOf(item.getWeight());
        result += " " + String.valueOf(this.price);
        return result;
    }
}
