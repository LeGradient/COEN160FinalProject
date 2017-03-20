package edu.scu.coen160.finalproject.system;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * RecyclableItem class
 *
 * Represents a recyclable item with a weight and material.
 */
public class RecyclableItem {

    /**
     * Used to round weight values to 2 digits.
     */
    DecimalFormat twoDigits = new DecimalFormat("####0.00");

    /**
     * Weight of this item.
     */
    private double weight;

    /**
     * Material of this item.
     */
    private String material;

    /**
     * Initializes a RecyclableItem with the specified weight and material.
     * @param weight    The weight of the item.
     * @param material  The material of the item.
     */
    public RecyclableItem(double weight, String material) {
        this.weight = weight;
        this.material = material;
    }

    /**
     * Initializes a RecyclableItem with the specified material. Randomly generates a weight.
     * @param material  The material of the item.
     */
    public RecyclableItem(String material) {
        this.material = material;
        Random r = new Random();

        // generate a random weight in the range .5 to 10
        this.weight = Double.valueOf(twoDigits.format(.5 + r.nextDouble() * 10.0));
    }

    /**
     * Gets the material of this item.
     * @return  The material of this item.
     */
    public String getMaterial() {
        return this.material;
    }

    /**
     * Gets the weight of this item.
     * @return  The weight of this item.
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Generates a string representation of this item.
     * @return  The material of this item, as a string.
     */
    public String toString() {
        return getMaterial();
    }
}