package edu.scu.coen160.finalproject.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class RecyclingMachine {

    // keep track of the number of existing recycling machines
    // used to assign unique IDs to each machine
    private static int objCount = 0;


    // FIELDS

    private String location;
    private int id;
    private double capacity;
    private double weight;
    private double money;
    private HashMap<String, Double> prices;
    private boolean isSession;
    private ArrayList<TransactionRecord> session;


    // GETTERS & SETTERS

    public String getTableName() { return "RCM" + ((Integer)id).toString(); }
    public String getLocation() { return this.location; }
    public int getId() { return this.id; }
    public double getCapacity() { return this.capacity; }
    public double getWeight() { return this.weight; }
    public double getMoney() { return this.money; }
    public void addMoney(double money) { this.money += money; }
    public double getPrice(String material) { return this.prices.get(material); }
    public void setPrice(String material, double price) { prices.put(material, price); }
    public boolean isSession() { return isSession; }
    public Set<String> getAcceptableItems() { return this.prices.keySet(); }

    // CONSTRUCTOR

    public RecyclingMachine(HashMap<String, Double> prices, double money, double capacity, String location) {
        // assign ID and increment object counter
        this.id = objCount++;

        // initialize fields from constructor parameters
        this.money = money;
        this.capacity = capacity;
        this.weight = 0;
        this.prices = prices;
        this.location = location;

        this.session = new ArrayList<>();

        // create a database table for storing transaction records
        try (Connection connection = DriverManager.getConnection(TransactionRecord.database)) {
            Statement stmt = connection.createStatement();
            String sql = String.format(TransactionRecord.newTable, this.getTableName(), this.getTableName());
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    // OTHER METHODS

    public void recycleItem(RecyclableItem item) throws IllegalArgumentException {
        if (this.weight + item.getWeight() > this.capacity) {
            throw new IllegalArgumentException("Recycling machine capacity exceeded");
        }
        double price = getPrice(item.getMaterial()) * item.getWeight();
        TransactionRecord record = new TransactionRecord(item, price);
        if (this.isSession) {
            this.session.add(record);
        } else {
            this.weight += item.getWeight();
            record.writeToTable(this.getTableName());
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
        assert this.isSession;
        for (TransactionRecord record : session) {
            record.writeToTable(this.getTableName());
        }
        this.isSession = false;
    }

    public void empty() {
        this.weight = 0;
        TransactionRecord record = new TransactionRecord(null, 0);
        record.writeToTable(this.getTableName());
    }

    // payOut for single item
    public double payOut(double price) {
        double owedValue = price;
        if (this.money - owedValue < 0) {
            payCoupon(owedValue - this.money);
            owedValue = this.money;
            this.money = 0;
            return owedValue;
        }

        this.money -= owedValue;

        return owedValue;
    }

    // payOut for a session
    public double payOut() {
        assert this.isSession;
        double owedValue = 0;

        for (TransactionRecord record : session) {
            owedValue += record.getPrice();
        }
        return payOut(owedValue);
    }

    private void payCoupon(double amount) {
        // TODO pay out a coupon of value amount
    }

    public String printPrices() {
        String result = "";
        for (HashMap.Entry<String, Double> entry : prices.entrySet()) {
            result += entry.getKey() + ": " + entry.getValue() + "\n";
        }
        return result;
    }
}
