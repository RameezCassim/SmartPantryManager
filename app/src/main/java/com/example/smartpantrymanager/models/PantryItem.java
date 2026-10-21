
package com.example.smartpantrymanager.models;

public class PantryItem {

    // Properties of a pantry ingredient
    private final long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;

    // Constructor for a new ingredient
    public PantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        this(0, name, quantity, unit, expiryDate);
    }

    // Constructor for an ingredient with an existing ID
    public PantryItem(
            long id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // Getter methods
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
