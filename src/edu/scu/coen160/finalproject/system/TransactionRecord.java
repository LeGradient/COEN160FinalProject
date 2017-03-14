package edu.scu.coen160.finalproject.system;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// not sure if this should be public or package protected
class TransactionRecord {
    private RecyclableItem item;
    private double price;

    public static String database = "jdbc:sqlite:TransactionRecords.db";
    public static String newTable = "DROP TABLE IF EXISTS %s;" +
                                    "CREATE TABLE %s (" +
                                        "material VARCHAR(15) DEFAULT NULL," +
                                        "weight NUMBER(4,2) DEFAULT NULL," +
                                        "price NUMBER(4,2) DEFAULT NULL," +
                                        "stamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                                    ");";


    // GETTERS & SETTERS

    public double getPrice() { return this.price; }
    public double getWeight() { return item.getWeight(); }


    // CONSTRUCTOR

    public TransactionRecord(RecyclableItem item, double price) {
        this.item = item;
        this.price = price;
    }


    // OTHER METHODS

    public void writeToTable(String tableName) {
        try (Connection connection = DriverManager.getConnection(database)) {
            String sql;
            Statement stmt = connection.createStatement();
            if (this.item == null) {
                sql = "INSERT INTO " + tableName + " DEFAULT VALUES;";
            } else {
                sql = "INSERT INTO " + tableName + " (material, weight, price) VALUES ('" + this.item.getMaterial() + "'," +
                                                                    this.item.getWeight() + "," +
                                                                    this.price + ");";
            }
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

    public String toString() {
        String result = item.getMaterial();
        result += " " + String.valueOf(item.getWeight());
        result += " " + String.valueOf(this.price);
        return result;
    }
}
