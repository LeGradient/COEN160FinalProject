package edu.scu.coen160.finalproject.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

public class RecyclingMachine extends Observable {

    /**
     * Keeps track of the number of existing RCMs.
     * Used to assign unique IDs to each RCM.
     */
    private static int objCount = 0;

    /**
     * Used to round money and weight values to 2 digits.
     */
    private static DecimalFormat displayFormat = new DecimalFormat("####0.00");

    /**
     * Location of this RCM.
     */
    private String location;

    /**
     * ID of this RCM.
     */
    private int id;

    /**
     * Maximum capacity of this RCM.
     */
    private double capacity;

    /**
     * Current weight of the items recycled in this RCM.
     */
    private double weight;

    /**
     * Amount of money available to dispense in this RCM.
     */
    private double money;

    /**
     * A HashMap of accepted materials and their prices for this RCM.
     */
    private HashMap<String, Double> prices;

    /**
     * A flag to indicate whether or not this RCM is in a session.
     */
    private boolean isSession;

    /**
     * An ArrayList of transactions that took place in a session.
     */
    private ArrayList<TransactionRecord> sessionRecords;

    /**
     * An ArrayList of items that were recycled in a session.
     */
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
        return Double.valueOf(RecyclingMachine.displayFormat.format(this.weight));
    }

    public void setWeight(double weight) {
        this.weight += weight;
        setChanged();
        notifyObservers();
    }

    public double getMoney() {
        return Double.valueOf(RecyclingMachine.displayFormat.format(this.money));
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

    /**
     * Calculates the value of an item based on its material, weight, price.
     *
     * @param item  The item to calculate the value of.
     * @return      The value of the item.
     */
    public double calculateItemPrice(RecyclableItem item) {
        double result = getPrice(item.getMaterial()) * item.getWeight();
        result = Double.valueOf(RecyclingMachine.displayFormat.format(result));
        return result;
    }

    /**
     * Recycles an item.
     *
     * Checks if the machine has sufficient capacity to hold the item. Gets the value of the item and writes a
     * transaction to the database. Updates the weight of the RCM.
     *
     * @param item                      The item to recycle.
     * @throws IllegalArgumentException Throws an exception if the machine does not have sufficient capacity.
     */
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

    /**
     * Starts a new session.
     *
     * Cancels any previous session and sets the boolean flag for a session to true.
     */
    public void startSession() {
        cancelSession();
        this.isSession = true;
    }

    /**
     * Cancels the current session.
     *
     * Sets the boolean flag for a session to false, and creates empty list of transactions and items for a new session.
     */
    public void cancelSession() {
        this.sessionRecords = new ArrayList<>();
        this.sessionItems = new ArrayList<>();
        this.isSession = false;
    }

    /**
     * Recycles all the items from a session and stores transactions in the database for each of them.
     */
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

    /**
     * Empties the RCM.
     *
     * Sets the current weight of the RCM to zero and stores a transaction in the database to indicate when the
     * RCM was emptied.
     */
    public void empty() {
        setWeight(0);
        TransactionRecord record = new TransactionRecord(null, 0);
        record.writeToTable(this.getTableName());
    }

    /**
     * Pays out an amount of money to the user in exchange for their item. Takes money from the RCM's funds.
     * If funds are insufficient, pays the remaining difference in the form of a coupon.
     *
     * @param price The price to pay out.
     * @return      The amount of money paid directly to the user.
     */
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

    /**
     * Pays out money to the user after a session. Takes money from the RCM's funds.
     * If funds are insufficient, pays the remaining difference in the form of a coupon.
     *
     * @return  The amount of money paid directly to the user.
     */
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

    /**
     * Returns a string containing all the items accepted by this RCM and their prices.
     *
     * @return  A string containing all the items accepted by this RCM and their prices.
     */
    public String printPrices() {
        String result = "";
        for (HashMap.Entry<String, Double> entry : prices.entrySet()) {
            result += entry.getKey() + ": " + entry.getValue() + "\n";
        }
        return result;
    }
}
