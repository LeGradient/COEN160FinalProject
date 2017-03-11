package edu.scu.coen160.finalproject.system;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class TestMain {
    public static void main(String[] args) {
        // dynamically load the SQLITE driver class
        // apparently nothing happens if this executes twice
        // but that doesn't mean we should do it
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        // verify that a SQLITE database exists in the expected directory
        File db = new File("TransactionRecords.db");
        assert(db.exists());

        // instantiate an RCM and test stuff
        HashMap<String, Double> prices = new HashMap<>();
        prices.put("plastic", 10.0);
        RecyclingMachine rcm = new RecyclingMachine(prices, 1000, 1000);
        RecyclableItem item = new RecyclableItem(5, "plastic");
        rcm.recycleItem(item);
        rcm.empty();
    }
}
