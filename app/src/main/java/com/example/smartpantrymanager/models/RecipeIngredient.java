package com.example.smartpantrymanager.models;

public class RecipeIngredient {

    private final String name;
    private final double requiredQuantity;
    private final String unit;

    public RecipeIngredient(
            String name,
            double requiredQuantity,
            String unit) {

        this.name = name;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }
}
