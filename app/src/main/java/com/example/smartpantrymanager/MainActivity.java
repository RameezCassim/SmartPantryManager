package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.view.View;
import android.widget.TextView;

import com.example.smartpantrymanager.database.PantryDao;


public class MainActivity extends AppCompatActivity {

    private PantryDao pantryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Call the parent Activity's onCreate method
        super.onCreate(savedInstanceState);

        // Support modern edge-to-edge Android displays
        EdgeToEdge.enable(this);

        // Display the XML layout for this Activity
        setContentView(R.layout.activity_main);

        // Prepare database access for this Activity
        pantryDao = new PantryDao(this);


        // Hide the default ActionBar if the theme has one
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Keep content clear of system status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {

                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return windowInsets;
                }
        );

        // Find the buttons declared in activity_main.xml
        Button btnAddIngredient =
                findViewById(R.id.btnAddIngredient);

        Button btnExploreRecipes =
                findViewById(R.id.btnExploreRecipes);

        Button btnNavPantry =
                findViewById(R.id.btnNavPantry);

        Button btnNavRecipes =
                findViewById(R.id.btnNavRecipes);

        Button btnNavSettings =
                findViewById(R.id.btnNavSettings);

        // Add Ingredient button
        btnAddIngredient.setOnClickListener(view -> {
            showMessage(R.string.message_add);
        });

        // Explore Recipes button
        btnExploreRecipes.setOnClickListener(view -> {
            showMessage(R.string.message_recipes);
        });

        // Pantry bottom navigation
        btnNavPantry.setOnClickListener(view -> {
            showMessage(R.string.message_pantry);
        });

        // Recipes bottom navigation
        btnNavRecipes.setOnClickListener(view -> {
            showMessage(R.string.message_recipes);
        });

        // Settings bottom navigation
        btnNavSettings.setOnClickListener(view -> {
            showMessage(R.string.message_settings);
        });
    }


    @Override
    protected void onResume() {

        // Call the parent Activity's lifecycle method
        super.onResume();

        // Retrieve the actual number of pantry items
        int ingredientCount = pantryDao.getCount();

        // Find the ingredient counter on our dashboard
        TextView tvIngredientCount =
                findViewById(R.id.tvIngredientCount);

        // Display the current number of saved ingredients
        tvIngredientCount.setText(
                String.valueOf(ingredientCount)
        );

        // Find the empty-pantry card
        View emptyPantryCard =
                findViewById(R.id.emptyPantryCard);

        // Show the empty card only when no ingredients exist
        if (ingredientCount == 0) {
            emptyPantryCard.setVisibility(View.VISIBLE);
        } else {
            emptyPantryCard.setVisibility(View.GONE);
        }
    }

    // Reusable method to display a short message
    private void showMessage(int messageId) {

        Toast.makeText(
                this,
                getString(messageId),
                Toast.LENGTH_SHORT
        ).show();
    }
}
