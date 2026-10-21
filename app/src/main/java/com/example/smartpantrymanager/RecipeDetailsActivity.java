package com.example.smartpantrymanager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.RecipeMatcher;

import java.math.BigDecimal;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID =
            "com.example.smartpantrymanager.RECIPE_ID";

    private RecipeDao recipeDao;
    private PantryDao pantryDao;

    private TextView tvRecipeName;
    private TextView tvAvailability;
    private TextView tvPreparationSteps;
    private LinearLayout ingredientsContainer;

    private long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recipeDao = new RecipeDao(this);
        pantryDao = new PantryDao(this);

        // Connect Java variables to XML components
        tvRecipeName = findViewById(
                R.id.tvDetailRecipeName
        );

        tvAvailability = findViewById(
                R.id.tvDetailAvailability
        );

        tvPreparationSteps = findViewById(
                R.id.tvPreparationSteps
        );

        ingredientsContainer = findViewById(
                R.id.recipeIngredientsContainer
        );

        Button backButton = findViewById(
                R.id.btnBackFromRecipeDetails
        );

        backButton.setOnClickListener(view -> finish());

        // Receive the selected recipe's database ID
        recipeId = getIntent().getLongExtra(
                EXTRA_RECIPE_ID,
                -1L
        );

        if (recipeId <= 0) {
            Toast.makeText(
                    this,
                    "Invalid recipe selection",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (recipeId > 0) {
            loadRecipeDetails();
        }
    }

    // Read and display the recipe from SQLite
    private void loadRecipeDetails() {

        Recipe recipe = recipeDao.getRecipeById(recipeId);

        if (recipe == null) {

            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Read current pantry information
        List<PantryItem> pantryItems = pantryDao.getAll();

        // Display recipe name
        tvRecipeName.setText(recipe.getName());

        // Display instructions
        String instructions = recipe.getInstructions();

        tvPreparationSteps.setText(
                instructions == null || instructions.trim().isEmpty()
                        ? "No preparation instructions available."
                        : instructions
        );

        // Display all required ingredient quantities
        showIngredients(recipe);

        // Display accurate shortage feedback
        showAvailability(recipe, pantryItems);
    }

    // Create ingredient rows dynamically
    private void showIngredients(Recipe recipe) {

        ingredientsContainer.removeAllViews();

        for (RecipeIngredient ingredient :
                recipe.getIngredients()) {

            LinearLayout row = new LinearLayout(this);

            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(4, dp(12), 4, dp(12));
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Ingredient name
            TextView nameView = new TextView(this);

            nameView.setText(ingredient.getName());
            nameView.setTextSize(15f);

            nameView.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.pantry_text
                    )
            );

            LinearLayout.LayoutParams nameParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                    );

            row.addView(nameView, nameParams);

            // Required amount and unit
            TextView quantityView = new TextView(this);

            String amount = formatQuantity(
                    ingredient.getRequiredQuantity()
            );

            quantityView.setText(
                    amount + " " + ingredient.getUnit()
            );

            quantityView.setTextSize(14f);
            quantityView.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.pantry_green_dark
                    )
            );

            row.addView(quantityView);

            ingredientsContainer.addView(row);
        }
    }

    // Display whether all ingredient requirements are met
    private void showAvailability(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        List<String> shortages =
                RecipeMatcher.getShortages(
                        recipe,
                        pantryItems
                );

        if (shortages.isEmpty()) {

            tvAvailability.setText(
                    "✓ You have all the ingredients needed"
            );

            tvAvailability.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.pantry_green_dark
                    )
            );

        } else {

            StringBuilder message = new StringBuilder(
                    "Ingredients still needed:\n"
            );

            for (String shortage : shortages) {
                message.append("• ")
                        .append(shortage)
                        .append("\n");
            }

            tvAvailability.setText(
                    message.toString().trim()
            );

            tvAvailability.setTextColor(
                    Color.rgb(190, 45, 45)
            );
        }
    }

    // Format 2.0 as 2, while preserving values such as 0.5
    private String formatQuantity(double quantity) {

        return BigDecimal.valueOf(quantity)
                .stripTrailingZeros()
                .toPlainString();
    }

    // Convert density-independent layout measurements to pixels
    private int dp(float value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics().density
        );
    }
}
