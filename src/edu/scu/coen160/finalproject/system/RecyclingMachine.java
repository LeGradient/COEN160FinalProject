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
    private ArrayList<TransactionRecord> session;

    // GETTERS & SETTERS

    public String getLocation() { return this.location; }
    public int getId() { return this.id; }
    public double getCapacity() { return this.capacity; }
    public double getMoney() { return this.money; }
    public void addMoney(double money) { this.money += money; }
    public double getPrice(String material) { return this.prices.get(material); }

    public RecyclingMachine(HashMap<String, Double> prices, double money, double capacity) {
        // assign ID and increment object counter
        this.id = objCount++;

        // initialize fields from constructor parameters
        this.money = money;
        this.capacity = capacity;
        this.prices = prices;

        this.session = new ArrayList<>();
    }

    public void empty() {
        this.capacity = 0;
        TransactionRecord record = new TransactionRecord(Date.from(Instant.now()), null, 0);
        // TODO: write the record to the database
    }
}
