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

/**
 * RecyclingMachine class
 *
 * Represents an RCM. Allows users to recycle items (individually and in sessions) and receive a payout for them.
 * Uses the Observer/Observable design pattern; this is an observable object.
 */
public class RecyclingMachine extends Observable {

    /**
     * Keeps track of the number of existing RCMs.
     * Used to assign unique IDs to each RCM.
     */
    private static int objCount = 0;

    /**
     * Used to round money and weight values to 2 digits.
     */
    private static DecimalFormat twoDigits = new DecimalFormat("####0.00");

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

    /**
     * Gets the name of the table in the database for this RCM.
     *
     * @return  The name of the table in the database for this RCM.
     */
    public String getTableName() {
        return "RCM" + ((Integer) id).toString();
    }

    /**
     * Gets the location of this RCM.
     *
     * @return  The location of this RCM.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Gets the ID of this RCM.
     *
     * @return  The ID of this RCM.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the capacity of this RCM.
     *
     * @return  The capacity of this RCM.
     */
    public double getCapacity() {
        return this.capacity;
    }

    /**
     * Gets the current used weight of this RCM, rounded to 2 decimal places.
     *
     * @return  The weight (rounded to 2 decimal places) of recycled items in this RCM.
     */
    public double getWeight() {
        return Double.valueOf(RecyclingMachine.twoDigits.format(this.weight));
    }

    /**
     * Sets the weight of the RCM to the specified amount. Notifies observers of an update.
     *
     * @param weight    The weight to set.
     */
    public void setWeight(double weight) {
        this.weight += weight;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets the current amount of money in the RCM.
     *
     * @return  The current amount of money in the RCM.
     */
    public double getMoney() {
        return Double.valueOf(RecyclingMachine.twoDigits.format(this.money));
    }

    /**
     * Adds money to the RCM. Notifies observers of an update.
     *
     * @param money The amount of money to add to the RCM.
     */
    public void addMoney(double money) {
        this.money += money;
        setChanged();
        notifyObservers();
    }

    /**
     * Get the price per unit weight of a material.
     * @param material  The material to check the price of.
     * @return          The price per unit weight of that material.
     */
    public double getPrice(String material) {
        return this.prices.get(material);
    }

    /**
     * Set the price per unit weight of a material. Notifies observers of an update.
     *
     * @param material  The material to set the price of.
     * @param price     The price to set.
     */
    public void setPrice(String material, double price) {
        prices.put(material, price);
        setChanged();
        notifyObservers();
    }

    /**
     * Check if this RCM is in a session or not.
     *
     * @return  true if in a session; else false.
     */
    public boolean isSession() {
        return isSession;
    }

    /**
     * Get the items accepted by this machine.
     *
     * @return  A Set containing the items accepted by this machine.
     */
    public Set<String> getAcceptableItems() {
        return this.prices.keySet();
    }

    /**
     * Initializes a RecyclingMachine object.
     *
     * @param prices    A mapping of materials to prices for this RCM.
     * @param money     The amount of money in this RCM.
     * @param capacity  The total capacity of this RCM.
     * @param location  The location of this RCM.
     */
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
        result = Double.valueOf(RecyclingMachine.twoDigits.format(result));
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
        // check if the machine has the capacity to store the new item.
        if (getWeight() + item.getWeight() > getCapacity()) {
            throw new IllegalArgumentException("Recycling machine capacity exceeded");
        }

        // calculate the price of the item
        double price = calculateItemPrice(item);

        // create a new transaction with the item and its price,  write it to the DB, and update the weight of the RCM
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

        // if we don't have enough money, pay out a coupon
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

        // calculate the total owed value and pay it out
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

        // append the material and price of every accepted item to the result string
        for (HashMap.Entry<String, Double> entry : prices.entrySet()) {
            result += entry.getKey() + ": " + entry.getValue() + "\n";
        }
        return result;
    }
}
