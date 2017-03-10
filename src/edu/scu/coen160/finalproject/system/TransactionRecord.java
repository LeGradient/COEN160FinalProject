package edu.scu.coen160.finalproject.system;

import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// not sure if this should be public or package protected
class TransactionRecord {
    private double price;
    private RecyclableItem item;
    private Date timestamp;

    private boolean emptyTransaction = false;

    public static String database = "jdbc:sqlite:TransactionRecords.db";

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

    public void writeToDB() {
        try (Connection connection = DriverManager.getConnection(database)) {
            // do database stuff
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
