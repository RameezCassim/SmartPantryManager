package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private static final double EPSILON = 0.000001;

    // Normalize common ingredient-name variations
    public static String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        String result = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (result) {
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "eggs": return "egg";
            case "bananas": return "banana";
            case "apples": return "apple";
            case "beans": return "bean";
            default:
                if (result.length() > 3
                        && result.endsWith("s")
                        && !result.endsWith("ss")) {
                    return result.substring(0, result.length() - 1);
                }
                return result;
        }
    }

    private static String unitCategory(String unit) {

        if (unit == null) {
            return "";
        }

        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "piece":
            case "pieces":
            case "pcs":
                return "count";

            default:
                return "";
        }
    }

    private static double toBase(double quantity, String unit) {

        if (unit == null) {
            return Double.NaN;
        }

        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "kg":
            case "l":
                return quantity * 1000.0;

            case "g":
            case "ml":
            case "piece":
            case "pieces":
            case "pcs":
                return quantity;

            default:
                return Double.NaN;
        }
    }

    private static String baseUnit(String category) {
        switch (category) {
            case "mass": return "g";
            case "volume": return "ml";
            case "count": return "pieces";
            default: return "";
        }
    }

    private static String formatNumber(double value) {
        return BigDecimal.valueOf(value)
                .stripTrailingZeros()
                .toPlainString();
    }

    // Return human-readable shortages for a single recipe.
    // An empty result means the recipe can be prepared.
    public static List<String> getShortages(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        List<String> shortages = new ArrayList<>();

        if (recipe == null ||
                recipe.getIngredients() == null ||
                recipe.getIngredients().isEmpty()) {

            shortages.add("Recipe has no valid ingredient requirements");
            return shortages;
        }

        Map<String, Double> available = new HashMap<>();

        if (pantryItems != null) {
            for (PantryItem item : pantryItems) {

                String name = normalizeName(item.getName());
                String category = unitCategory(item.getUnit());
                double quantity = toBase(
                        item.getQuantity(),
                        item.getUnit()
                );

                if (name.isEmpty()
                        || category.isEmpty()
                        || !Double.isFinite(quantity)
                        || quantity <= 0) {
                    continue;
                }

                String key = name + "|" + category;
                available.merge(key, quantity, Double::sum);
            }
        }

        // LinkedHashMap preserves the original ingredient order
        Map<String, Double> required = new LinkedHashMap<>();
        Map<String, String> displayNames = new HashMap<>();

        for (RecipeIngredient ingredient :
                recipe.getIngredients()) {

            String name = normalizeName(ingredient.getName());
            String category = unitCategory(ingredient.getUnit());

            double quantity = toBase(
                    ingredient.getRequiredQuantity(),
                    ingredient.getUnit()
            );

            if (name.isEmpty()
                    || category.isEmpty()
                    || !Double.isFinite(quantity)
                    || quantity <= 0) {

                shortages.add(
                        "Invalid requirement: " +
                                ingredient.getName() +
                                " (" + ingredient.getUnit() + ")"
                );
                continue;
            }

            String key = name + "|" + category;

            required.merge(key, quantity, Double::sum);
            displayNames.putIfAbsent(
                    key,
                    ingredient.getName()
            );
        }

        for (Map.Entry<String, Double> entry :
                required.entrySet()) {

            String key = entry.getKey();
            double needed = entry.getValue();
            double owned = available.getOrDefault(key, 0.0);

            String category = key.substring(key.lastIndexOf('|') + 1);
            String unit = baseUnit(category);
            String name = displayNames.get(key);

            // User has none of this ingredient
            if (owned <= EPSILON) {

                shortages.add(
                        "Missing: " + name +
                                " — need " +
                                formatNumber(needed) + " " + unit
                );

                // User has the ingredient, but too little
            } else if (owned + EPSILON < needed) {

                double difference = needed - owned;

                shortages.add(
                        "Not enough: " + name +
                                " — need " +
                                formatNumber(difference) + " " + unit +
                                " more (have " +
                                formatNumber(owned) + " " + unit + ")"
                );
            }
        }

        return shortages;
    }

    // Strict matching: include only recipes without shortages
    public static List<Recipe> findMatchingRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        List<Recipe> matchingRecipes = new ArrayList<>();

        if (recipes == null) {
            return matchingRecipes;
        }

        for (Recipe recipe : recipes) {

            if (getShortages(recipe, pantryItems).isEmpty()) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }
}
