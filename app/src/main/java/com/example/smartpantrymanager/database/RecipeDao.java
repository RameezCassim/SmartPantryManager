
package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeDao {

    private final DatabaseHelper dbHelper;

    public RecipeDao(Context context) {
        dbHelper = new DatabaseHelper(
                context.getApplicationContext()
        );
    }

    // Retrieve all recipes from the database
    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id, name, instructions " +
                        "FROM recipes ORDER BY name COLLATE NOCASE ASC",
                null
        )) {

            while (cursor.moveToNext()) {

                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String instructions = cursor.getString(2);

                Recipe recipe = new Recipe(
                        id,
                        name,
                        instructions
                );

                loadIngredients(db, recipe);

                recipes.add(recipe);
            }
        }

        return recipes;
    }

    // Retrieve one recipe using its ID
    public Recipe getRecipeById(long recipeId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id, name, instructions " +
                        "FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        )) {

            if (cursor.moveToFirst()) {

                Recipe recipe = new Recipe(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );

                loadIngredients(db, recipe);

                return recipe;
            }
        }

        return null;
    }

    // Retrieve all ingredients required by a recipe
    private void loadIngredients(
            SQLiteDatabase db,
            Recipe recipe) {

        try (Cursor cursor = db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ? ORDER BY id",
                new String[]{
                        String.valueOf(recipe.getId())
                }
        )) {

            while (cursor.moveToNext()) {

                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                cursor.getString(0),
                                cursor.getDouble(1),
                                cursor.getString(2)
                        );

                recipe.addIngredient(ingredient);
            }
        }
    }

    // Count available recipe records
    public int getRecipeCount() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes",
                null
        )) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }
}
