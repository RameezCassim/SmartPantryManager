package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class RecipeSeeder {

    private final DatabaseHelper dbHelper;

    public RecipeSeeder(Context context) {
        dbHelper = new DatabaseHelper(
                context.getApplicationContext()
        );
    }

    // Insert predefined recipes only when the collection is empty
    public void seedRecipesIfNeeded() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Don't insert the same recipes again on every launch
        long count = DatabaseUtils.queryNumEntries(
                db,
                "recipes"
        );

        if (count > 0) {
            return;
        }

        // Keep all recipe inserts in a single transaction
        db.beginTransaction();

        try {

            addRecipe(db,
                    "Cheese Omelette",
                    "Beat the eggs with milk. Add grated cheese. Cook in a pan until set.",
                    new String[][]{
                            {"Eggs", "2", "pieces"},
                            {"Milk", "50", "ml"},
                            {"Cheese", "30", "g"}
                    });

            addRecipe(db,
                    "Scrambled Eggs",
                    "Beat eggs with milk. Cook gently while stirring until fluffy.",
                    new String[][]{
                            {"Eggs", "2", "pieces"},
                            {"Milk", "30", "ml"}
                    });

            addRecipe(db,
                    "Boiled Eggs",
                    "Place eggs in water. Bring to a boil and cook until done.",
                    new String[][]{
                            {"Eggs", "2", "pieces"}
                    });

            addRecipe(db,
                    "Egg Fried Rice",
                    "Cook the rice, scramble the eggs, then combine and fry.",
                    new String[][]{
                            {"Rice", "150", "g"},
                            {"Eggs", "2", "pieces"},
                            {"Oil", "15", "ml"}
                    });

            addRecipe(db,
                    "Cheese Toast",
                    "Place cheese on bread and toast until the cheese melts.",
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Cheese", "40", "g"}
                    });

            addRecipe(db,
                    "Tomato Sandwich",
                    "Slice the tomato and arrange on bread with butter.",
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Tomato", "1", "pieces"},
                            {"Butter", "10", "g"}
                    });

            addRecipe(db,
                    "Peanut Butter Toast",
                    "Toast the bread and spread peanut butter evenly.",
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Peanut Butter", "30", "g"}
                    });

            addRecipe(db,
                    "Banana Toast",
                    "Toast bread and arrange sliced banana on top.",
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Banana", "1", "pieces"}
                    });

            addRecipe(db,
                    "Banana Milkshake",
                    "Blend the banana with milk until smooth.",
                    new String[][]{
                            {"Banana", "2", "pieces"},
                            {"Milk", "250", "ml"}
                    });

            addRecipe(db,
                    "Fruit Yogurt Bowl",
                    "Slice the banana and mix with yogurt and apple.",
                    new String[][]{
                            {"Banana", "1", "pieces"},
                            {"Apple", "1", "pieces"},
                            {"Yogurt", "150", "g"}
                    });

            addRecipe(db,
                    "Apple Yogurt",
                    "Chop the apple and stir into yogurt.",
                    new String[][]{
                            {"Apple", "1", "pieces"},
                            {"Yogurt", "150", "g"}
                    });

            addRecipe(db,
                    "Porridge",
                    "Heat oats with milk, stirring until thick and cooked.",
                    new String[][]{
                            {"Oats", "70", "g"},
                            {"Milk", "250", "ml"}
                    });

            addRecipe(db,
                    "Banana Oatmeal",
                    "Cook oats in milk and top with sliced banana.",
                    new String[][]{
                            {"Oats", "70", "g"},
                            {"Milk", "250", "ml"},
                            {"Banana", "1", "pieces"}
                    });

            addRecipe(db,
                    "Tomato Pasta",
                    "Cook pasta. Simmer tomato with oil and toss together.",
                    new String[][]{
                            {"Pasta", "150", "g"},
                            {"Tomato", "2", "pieces"},
                            {"Oil", "15", "ml"}
                    });

            addRecipe(db,
                    "Cheesy Pasta",
                    "Cook pasta and stir in milk and cheese until creamy.",
                    new String[][]{
                            {"Pasta", "150", "g"},
                            {"Milk", "100", "ml"},
                            {"Cheese", "50", "g"}
                    });

            addRecipe(db,
                    "Potato Mash",
                    "Boil potatoes until tender. Mash with milk and butter.",
                    new String[][]{
                            {"Potato", "3", "pieces"},
                            {"Milk", "100", "ml"},
                            {"Butter", "20", "g"}
                    });

            addRecipe(db,
                    "Roasted Potatoes",
                    "Cut the potatoes, coat with oil and roast until tender.",
                    new String[][]{
                            {"Potato", "3", "pieces"},
                            {"Oil", "20", "ml"}
                    });

            addRecipe(db,
                    "Tomato Egg Scramble",
                    "Cook chopped tomatoes in oil, add beaten eggs and scramble.",
                    new String[][]{
                            {"Tomato", "2", "pieces"},
                            {"Eggs", "2", "pieces"},
                            {"Oil", "10", "ml"}
                    });

            addRecipe(db,
                    "Rice and Beans",
                    "Cook the rice and beans separately, then combine and serve.",
                    new String[][]{
                            {"Rice", "150", "g"},
                            {"Beans", "150", "g"}
                    });

            addRecipe(db,
                    "Cheese and Tomato Sandwich",
                    "Slice tomato, add cheese and assemble between bread slices.",
                    new String[][]{
                            {"Bread", "2", "pieces"},
                            {"Cheese", "40", "g"},
                            {"Tomato", "1", "pieces"}
                    });

            // Save the transaction only when every insert succeeds
            db.setTransactionSuccessful();

        } finally {
            // Commit on success, roll back if an exception occurred
            db.endTransaction();
        }
    }

    // Save one recipe and all its required ingredients
    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[][] ingredients) {

        // Store the recipe's name and preparation instructions
        ContentValues recipeValues = new ContentValues();

        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insertOrThrow(
                "recipes",
                null,
                recipeValues
        );

        // Store every ingredient required by this recipe
        for (String[] ingredient : ingredients) {

            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(
                    "recipe_id",
                    recipeId
            );

            ingredientValues.put(
                    "ingredient_name",
                    ingredient[0]
            );

            ingredientValues.put(
                    "required_quantity",
                    Double.parseDouble(ingredient[1])
            );

            ingredientValues.put(
                    "unit",
                    ingredient[2]
            );

            db.insertOrThrow(
                    "recipe_ingredients",
                    null,
                    ingredientValues
            );
        }
    }
}
