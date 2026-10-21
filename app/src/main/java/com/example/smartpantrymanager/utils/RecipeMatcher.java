
package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // A small tolerance for floating-point comparisons
    private static final double EPSILON = 0.000001;

    // Convert ingredient names to a consistent form
    public static String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        String normalized = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        // Handle commonly encountered plural names
        if (normalized.equals("tomatoes")) {
            return "tomato";
        }

        if (normalized.equals("potatoes")) {
            return "potato";
        }

        if (normalized.equals("eggs")) {
            return "egg";
        }

        if (normalized.equals("bananas")) {
            return "banana";
        }

        if (normalized.equals("apples")) {
            return "apple";
        }

        if (normalized.equals("beans")) {
            return "bean";
        }

        // Handle simple regular plurals
        if (normalized.length() > 3 &&
                normalized.endsWith("s") &&
                !normalized.endsWith("ss")) {

            normalized = normalized.substring(
                    0,
                    normalized.length() - 1
            );
        }

        return normalized;
    }

    // Determine the measurement category
    private static String getUnitCategory(String unit) {

        if (unit == null) {
            return "invalid";
        }

        switch (unit.trim().toLowerCase(Locale.ROOT)) {

            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "pieces":
                return "count";

            default:
                return "invalid";
        }
    }

    // Convert quantities into a common base unit
    private static double convertToBase(
            double quantity,
            String unit) {

        switch (unit.trim().toLowerCase(Locale.ROOT)) {

            case "kg":
                return quantity * 1000.0;

            case "l":
                return quantity * 1000.0;

            case "g":
            case "ml":
            case "pieces":
                return quantity;

            default:
                return Double.NaN;
        }
    }

    // Find recipes that can be made using the pantry
    public static List<Recipe> findMatchingRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        List<Recipe> matchingRecipes = new ArrayList<>();

        if (recipes == null || pantryItems == null) {
            return matchingRecipes;
        }

        // Store available pantry quantities by ingredient and unit type
        Map<String, Double> available = new HashMap<>();

        for (PantryItem pantryItem : pantryItems) {

            String name = normalizeName(pantryItem.getName());

            String category = getUnitCategory(
                    pantryItem.getUnit()
            );

            double amount = convertToBase(
                    pantryItem.getQuantity(),
                    pantryItem.getUnit()
            );

            // Ignore invalid pantry quantities and units
            if (name.isEmpty() ||
                    category.equals("invalid") ||
                    !Double.isFinite(amount) ||
                    amount <= 0) {
                continue;
            }

            String key = name + "|" + category;

            // Combine duplicate pantry records using compatible units
            available.merge(key, amount, Double::sum);
        }

        // Check each recipe against the current pantry
        for (Recipe recipe : recipes) {

            List<RecipeIngredient> requirements =
                    recipe.getIngredients();

            // A recipe must have at least one requirement
            if (requirements == null || requirements.isEmpty()) {
                continue;
            }

            // Keep track of the total amount required per ingredient
            Map<String, Double> required = new HashMap<>();

            boolean validRequirements = true;

            for (RecipeIngredient ingredient : requirements) {

                String name = normalizeName(
                        ingredient.getName()
                );

                String category = getUnitCategory(
                        ingredient.getUnit()
                );

                double amount = convertToBase(
                        ingredient.getRequiredQuantity(),
                        ingredient.getUnit()
                );

                if (name.isEmpty() ||
                        category.equals("invalid") ||
                        !Double.isFinite(amount) ||
                        amount <= 0) {

                    validRequirements = false;
                    break;
                }

                String key = name + "|" + category;

                required.merge(key, amount, Double::sum);
            }

            if (!validRequirements) {
                continue;
            }

            boolean canCook = true;

            // Every required ingredient must be available
            for (Map.Entry<String, Double> entry :
                    required.entrySet()) {

                double availableAmount = available.getOrDefault(
                        entry.getKey(),
                        0.0
                );

                double requiredAmount = entry.getValue();

                if (availableAmount + EPSILON < requiredAmount) {
                    canCook = false;
                    break;
                }
            }

            // Suggest the recipe only if all requirements pass
            if (canCook) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }
}
