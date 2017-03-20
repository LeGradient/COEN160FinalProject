package edu.scu.coen160.finalproject.system;

import edu.scu.coen160.finalproject.gui.RecyclingUI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main driver class of the recycling system.
 */
public class TestMain {
    /**
     * Initializes SQL components.
     * Initializes 2 RCMs and an RMOS, and their respective GUIs.
     * @param args  unused
     */
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
        boolean dbExists = db.exists();
        assert(dbExists);

        // instantiate an RCM and item and test stuff
        HashMap<String, Double> prices = new HashMap<>();
        prices.put("plastic", 10.00);
        prices.put("rubber", 15.00);
        RecyclingMachine rcm = new RecyclingMachine(prices, 1000, 100, "Santa Clara");
        RecyclableItem item = new RecyclableItem(5, "plastic");

        ArrayList<RecyclingMachine> machines = new ArrayList<>();
        machines.add(rcm);

        // initialize the RMOS
        RecyclingMonitor rmos = new RecyclingMonitor(machines, "admin", "pass");

        // generate prices for a second RMOS
        HashMap<String, Double> prices2 = new HashMap<>();
        prices2.put("aluminium", 20.00);
        prices2.put("glass", 12.00);
        RecyclingMachine rcm2 = new RecyclingMachine(prices2, 100000, 10000, "San Jose");
        machines.add(rcm2);

        // initialize the UI
        new RecyclingUI(rcm, rcm2, rmos);
    }
}
