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
                                        "material VARCHAR(15)," +
                                        "weight NUMBER(4,2)," +
                                        "price NUMBER(4,2)," +
                                        "date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL" +
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
                sql = "INSERT INTO " + tableName + " VALUES (" + this.item.getMaterial() + "," +
                                                                    this.item.getWeight() + "," +
                                                                    this.price + ");";
            } else {
                sql = "INSERT INTO " + tableName + " VALUES (" + this.item.getMaterial() + "," +
                                                                    this.item.getWeight() + "," +
                                                                    this.price + ");";
            }
            int numRows = stmt.executeUpdate(sql);
            assert(numRows == 1);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
