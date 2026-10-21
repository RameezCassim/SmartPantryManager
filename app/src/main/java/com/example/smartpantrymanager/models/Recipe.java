package com.example.smartpantrymanager.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private final long id;
    private final String name;
    private final String instructions;
    private final List<RecipeIngredient> ingredients;

    public Recipe(
            long id,
            String name,
            String instructions) {

        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }
}
