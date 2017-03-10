package edu.scu.coen160.finalproject.system;

import java.util.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclingMachine {

    // keep track of the number of existing recycling machines
    // used to assign unique IDs to each machine
    private static int objCount = 0;


    // FIELDS

    private String location;
    private int id;
    private double capacity;
    private double money;
    private HashMap<String, Double> prices;
    private boolean isSession;
    private ArrayList<TransactionRecord> session;


    // GETTERS & SETTERS

    public String getLocation() { return this.location; }
    public int getId() { return this.id; }
    public double getCapacity() { return this.capacity; }
    public double getMoney() { return this.money; }
    public void addMoney(double money) { this.money += money; }
    public double getPrice(String material) { return this.prices.get(material); }


    // CONSTRUCTOR

    public RecyclingMachine(HashMap<String, Double> prices, double money, double capacity) {
        // assign ID and increment object counter
        this.id = objCount++;

        // initialize fields from constructor parameters
        this.money = money;
        this.capacity = capacity;
        this.prices = prices;

        this.session = new ArrayList<>();
    }


    // OTHER METHODS

    public void recycleItem(RecyclableItem item) {
        double price = getPrice(item.getMaterial());
        TransactionRecord record = new TransactionRecord(Date.from(Instant.now()), item, price);
        if (this.isSession) {
            this.session.add(record);
        } else {
            // TODO: write the record to the database
        }
    }

    public void startSession() {
        this.isSession = true;
    }

    public void cancelSession() {
        this.session = new ArrayList<>();
        this.isSession = false;
    }

    public void submitSession() {
        assert this.isSession = true;
        for (TransactionRecord record : session) {
            // TODO: write the record to the database
        }
        this.isSession = false;
    }

    public void empty() {
        this.capacity = 0;
        TransactionRecord record = new TransactionRecord(Date.from(Instant.now()), null, 0);
        // TODO: write the record to the database
    }

}
