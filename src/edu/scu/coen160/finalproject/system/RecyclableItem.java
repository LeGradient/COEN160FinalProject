package edu.scu.coen160.finalproject.system;

import java.util.Random;

public class RecyclableItem {
    private double weight;
    private String material;

    public RecyclableItem(double weight, String material) {
        this.weight = weight;
        this.material = material;
    }

    public RecyclableItem(String material) {
        this.material = material;
        Random r = new Random();

        // generate a random weight in the range .5 to 10
        this.weight = .5 + r.nextDouble() * 10.0;
    }

    public String getMaterial() {
        return this.material;
    }

    public double getWeight() {
        return this.weight;
    }
}