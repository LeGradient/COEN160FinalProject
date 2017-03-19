package edu.scu.coen160.finalproject.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

public class RecyclingMachine extends Observable {

    // keep track of the number of existing recycling machines
    // used to assign unique IDs to each machine
    private static int objCount = 0;

    // format money values
    private static DecimalFormat moneyFormat = new DecimalFormat("####0.00");

    // FIELDS

    private String location;
    private int id;
    private double capacity;
    private double weight;
    private double money;
    private HashMap<String, Double> prices;
    private boolean isSession;
    private ArrayList<TransactionRecord> sessionRecords;
    private ArrayList<RecyclableItem> sessionItems;


    // GETTERS & SETTERS

    public String getTableName() {
        return "RCM" + ((Integer) id).toString();
    }

    public String getLocation() {
        return this.location;
    }

    public int getId() {
        return this.id;
    }

    public double getCapacity() {
        return this.capacity;
    }

    public double getWeight() {
        return Double.valueOf(RecyclingMachine.moneyFormat.format(this.weight));
    }

    public void setWeight(double weight) {
        this.weight += weight;
        setChanged();
        notifyObservers();
    }

    public double getMoney() {
        return Double.valueOf(RecyclingMachine.moneyFormat.format(this.money));
    }

    public void addMoney(double money) {
        this.money += money;
        setChanged();
        notifyObservers();
    }

    public double getPrice(String material) {
        return this.prices.get(material);
    }

    public void setPrice(String material, double price) {
        prices.put(material, price);
        setChanged();
        notifyObservers();
    }

    public boolean isSession() {
        return isSession;
    }

    public Set<String> getAcceptableItems() {
        return this.prices.keySet();
    }

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

        this.sessionRecords = new ArrayList<>();
        this.sessionItems = new ArrayList<>();

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

    public double calculateItemPrice(RecyclableItem item) {
        double result = getPrice(item.getMaterial()) * item.getWeight();
        result = Double.valueOf(RecyclingMachine.moneyFormat.format(result));
        return result;
    }

    public void recycleItem(RecyclableItem item) throws IllegalArgumentException {
        if (getWeight() + item.getWeight() > getCapacity()) {
            throw new IllegalArgumentException("Recycling machine capacity exceeded");
        }
        double price = calculateItemPrice(item);
        TransactionRecord record = new TransactionRecord(item, price);
        if (this.isSession()) {
            this.sessionRecords.add(record);
            this.sessionItems.add(item);
        } else {
            setWeight(getWeight() + item.getWeight());
            record.writeToTable(this.getTableName());
        }
    }

    public void startSession() {
        cancelSession();
        this.isSession = true;
    }

    public void cancelSession() {
        this.sessionRecords = new ArrayList<>();
        this.sessionItems = new ArrayList<>();
        this.isSession = false;
    }

    public void submitSession() {
        assert this.isSession();
        this.isSession = false;
        for (RecyclableItem item : sessionItems) {
            recycleItem(item);
        }
        for (TransactionRecord record : sessionRecords) {
            record.writeToTable(this.getTableName());
        }
    }

    public void empty() {
        setWeight(0);
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
        assert this.isSession();
        double owedValue = 0;

        for (TransactionRecord record : sessionRecords) {
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
