
package com.example.smartpantrymanager;

// Android imports
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// AndroidX imports
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Project classes
import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.models.PantryItem;

// Java utilities
import java.util.List;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.PantryAdapter;


import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.database.RecipeSeeder;


public class MainActivity extends AppCompatActivity {

    // Database access object
    private PantryDao pantryDao;
    private PantryAdapter pantryAdapter;

    // =====================================================
    // 1. ACTIVITY CREATION
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Load the My Pantry interface
        setContentView(R.layout.activity_main);
        Log.i("SmartPantryStartup", "MainActivity layout inflated successfully");

        // Initialize database access
        pantryDao = new PantryDao(this);

        // Initialize the recipe seeder
        RecipeSeeder recipeSeeder = new RecipeSeeder(this);

        // Insert 20 predefined recipes if needed
        recipeSeeder.seedRecipesIfNeeded();

        // Find RecyclerView in the XML layout
        RecyclerView recyclerViewPantry =
                findViewById(R.id.recyclerViewPantry);

        // Arrange ingredients vertically
        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Create the Adapter and handle ingredient interactions
        pantryAdapter = new PantryAdapter(
                new PantryAdapter.OnIngredientActionListener() {

                    @Override
                    public void onIngredientClick(PantryItem item) {

                        // Open the selected ingredient for editing
                        openEditIngredient(item.getId());
                    }

                    @Override
                    public void onIngredientLongClick(PantryItem item) {

                        // Ask for confirmation before deleting
                        showDeleteConfirmation(item);
                    }
                }
        );

        // Connect the Adapter to RecyclerView
        recyclerViewPantry.setAdapter(pantryAdapter);


        // Hide the default ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Prevent overlap with Android system bars
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

        // =================================================
        // FIND BUTTONS FROM XML
        // =================================================

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

        // =================================================
        // BUTTON CLICK LISTENERS
        // =================================================

        // Open the Add Ingredient form
        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        // Explore Recipes button
        btnExploreRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            // Explore mode: display all 20 recipes
            intent.putExtra(
                    SuggestedRecipesActivity.EXTRA_SHOW_ALL_RECIPES,
                    true
            );
            startActivity(intent);
        });



        // Pantry bottom navigation
        btnNavRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            // Suggested mode: only recipes that can be cooked
            intent.putExtra(
                    SuggestedRecipesActivity.EXTRA_SHOW_ALL_RECIPES,
                    false
            );
            startActivity(intent);
        });



        // Settings bottom navigation
        btnNavSettings.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

    }

    // =====================================================
    // 2. ACTIVITY RESUME
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();
        Log.i("SmartPantryStartup", "MainActivity resumed");

        // Refresh pantry information from SQLite
        refreshPantry();
    }

    // =====================================================
    // 3. REFRESH PANTRY INFORMATION
    // =====================================================


    private void refreshPantry() {

        // Retrieve all pantry ingredients from SQLite
        List<PantryItem> items = pantryDao.getAll();

        // Update the ingredient count
        int ingredientCount = items.size();

        TextView tvIngredientCount =
                findViewById(R.id.tvIngredientCount);

        tvIngredientCount.setText(
                String.valueOf(ingredientCount)
        );

        // Find the empty-pantry information card
        View emptyPantryCard =
                findViewById(R.id.emptyPantryCard);

        // Find RecyclerView
        RecyclerView recyclerViewPantry =
                findViewById(R.id.recyclerViewPantry);

        // Find the list instructions
        TextView tvIngredientListHint =
                findViewById(R.id.tvIngredientListHint);

        // Show or hide the correct interface
        if (items.isEmpty()) {
            emptyPantryCard.setVisibility(View.VISIBLE);
            recyclerViewPantry.setVisibility(View.GONE);
            tvIngredientListHint.setVisibility(View.GONE);
        } else {
            emptyPantryCard.setVisibility(View.GONE);
            recyclerViewPantry.setVisibility(View.VISIBLE);
            tvIngredientListHint.setVisibility(View.VISIBLE);
        }

        // Send the latest ingredient records to the Adapter
        pantryAdapter.updateIngredients(items);
        Log.i("SmartPantryStartup", "Pantry refreshed, ingredient count=" + items.size());

        // Get the number of recipes saved in SQLite
        RecipeDao recipeDao = new RecipeDao(this);

        int recipeCount = recipeDao.getRecipeCount();

        // Find the Recipes counter on the dashboard
        TextView tvRecipeCount = findViewById(R.id.tvRecipeCount);

        // Display the current recipe count
        tvRecipeCount.setText(String.valueOf(recipeCount)
        );
    }


    // =====================================================
    // 4. DISPLAY PANTRY INGREDIENTS
    // =====================================================



    // =====================================================
    // 5. OPEN EDIT INGREDIENT SCREEN
    // =====================================================

    private void openEditIngredient(long ingredientId) {

        // Create an Intent for the Add/Edit Activity
        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        // Pass the selected ingredient's database ID
        intent.putExtra(
                AddEditIngredientActivity.EXTRA_INGREDIENT_ID,
                ingredientId
        );

        // Open the Edit Ingredient screen
        startActivity(intent);
    }

    // =====================================================
    // 6. DELETE INGREDIENT
    // =====================================================

    private void showDeleteConfirmation(PantryItem item) {

        // Create the confirmation dialog
        new AlertDialog.Builder(this)

                // Set dialog title
                .setTitle(R.string.delete_ingredient_title)

                // Display the ingredient name
                .setMessage(
                        "Are you sure you want to delete " +
                                item.getName() + "?"
                )

                // Cancel button
                .setNegativeButton(
                        R.string.cancel,
                        (dialog, which) -> dialog.dismiss()
                )

                // Delete button
                .setPositiveButton(
                        R.string.delete,
                        (dialog, which) -> {

                            // Delete ingredient from SQLite
                            int deletedRows = pantryDao.delete(
                                    item.getId()
                            );

                            if (deletedRows > 0) {

                                Toast.makeText(
                                        this,
                                        "Ingredient deleted successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Refresh dashboard
                                refreshPantry();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Ingredient could not be deleted.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )

                // Display the confirmation dialog
                .show();
    }

    // =====================================================
    // 7. CONVERT DP TO PIXELS
    // =====================================================

    private int dpToPx(float dp) {

        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        getResources().getDisplayMetrics()
                )
        );
    }

    // =====================================================
    // 8. DISPLAY TOAST MESSAGE
    // =====================================================

    private void showMessage(int messageId) {

        Toast.makeText(
                this,
                getString(messageId),
                Toast.LENGTH_SHORT
        ).show();
    }
}
