package edu.scu.coen160.finalproject.system;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

// not sure if this should be public or package protected
class TransactionRecord {
    private RecyclableItem item;
    private double price;
    private Date timestamp;

    private boolean emptyTransaction = false;

    public static String database = "jdbc:sqlite:TransactionRecords.db";
    public static String newTable = "CREATE TABLE %s (" +
                                        "item VARCHAR(20)," +
                                        "price NUMBER(4,2)," +
                                        "date DATETIME" +
                                    ");";

    public TransactionRecord(Date date, RecyclableItem item, double price) {
        this.timestamp = date;
        if (price == -1.0 || item == null)
            emptyTransaction = true;

        this.price = price;
        this.item = item;
    }

    public double getPrice() {
        return this.price;
    }

    public double getWeight() {
        return item.getWeight();
    }

    public void writeToDB(String tableName) {
        try (Connection connection = DriverManager.getConnection(database)) {
            Statement stmt = connection.createStatement();
            String sql = "INSERT INTO " + tableName + " VALUES ";   // TODO: insert values into the table
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
