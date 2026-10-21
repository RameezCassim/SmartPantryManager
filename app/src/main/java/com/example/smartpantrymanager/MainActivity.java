
package com.example.smartpantrymanager;

// Android imports
import android.app.AlertDialog;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    // Database access object
    private PantryDao pantryDao;

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

        // Initialize database access
        pantryDao = new PantryDao(this);

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

    // =====================================================
    // 2. ACTIVITY RESUME
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        // Refresh pantry information from SQLite
        refreshPantry();
    }

    // =====================================================
    // 3. REFRESH PANTRY INFORMATION
    // =====================================================

    private void refreshPantry() {

        // Retrieve the current number of ingredients
        int ingredientCount = pantryDao.getCount();

        // Find the ingredient counter
        TextView tvIngredientCount =
                findViewById(R.id.tvIngredientCount);

        // Update the counter
        tvIngredientCount.setText(
                String.valueOf(ingredientCount)
        );

        // Find the empty pantry card
        View emptyPantryCard =
                findViewById(R.id.emptyPantryCard);

        // Find the ingredient list container
        LinearLayout pantryListContainer =
                findViewById(R.id.pantryListContainer);

        // Find the list instructions
        TextView tvIngredientListHint =
                findViewById(R.id.tvIngredientListHint);

        // Check whether the pantry contains ingredients
        if (ingredientCount == 0) {

            // Show the empty pantry message
            emptyPantryCard.setVisibility(View.VISIBLE);

            // Hide the ingredient list
            pantryListContainer.setVisibility(View.GONE);

            // Hide the list instructions
            tvIngredientListHint.setVisibility(View.GONE);

        } else {

            // Hide the empty pantry message
            emptyPantryCard.setVisibility(View.GONE);

            // Show the ingredient list
            pantryListContainer.setVisibility(View.VISIBLE);

            // Show the list instructions
            tvIngredientListHint.setVisibility(View.VISIBLE);
        }

        // Update the displayed ingredient cards
        displayPantryItems();
    }

    // =====================================================
    // 4. DISPLAY PANTRY INGREDIENTS
    // =====================================================

    private void displayPantryItems() {

        // Retrieve ingredients from SQLite
        List<PantryItem> items = pantryDao.getAll();

        // Find the container for ingredient cards
        LinearLayout container =
                findViewById(R.id.pantryListContainer);

        // Remove old cards before loading new ones
        container.removeAllViews();

        // Loop through each ingredient
        for (PantryItem item : items) {

            // Create a TextView for the ingredient
            TextView ingredientCard = new TextView(this);

            // Format the ingredient quantity
            String quantityText =
                    String.valueOf(item.getQuantity());

            // Remove .0 from whole numbers
            if (item.getQuantity() == Math.floor(item.getQuantity())
                    && item.getQuantity() <= Long.MAX_VALUE) {

                quantityText = String.valueOf(
                        (long) item.getQuantity()
                );
            }

            // Create the ingredient display text
            String ingredientText =
                    item.getName() + "\n" +
                            quantityText + " " +
                            item.getUnit();

            // Include expiry date if available
            if (item.getExpiryDate() != null &&
                    !item.getExpiryDate().isEmpty()) {

                ingredientText +=
                        "\nExpiry: " + item.getExpiryDate();
            }

            // Set the card text
            ingredientCard.setText(ingredientText);

            // Set text appearance
            ingredientCard.setTextSize(16);

            ingredientCard.setTextColor(
                    getResources().getColor(
                            R.color.pantry_text,
                            getTheme()
                    )
            );

            ingredientCard.setGravity(
                    Gravity.CENTER_VERTICAL
            );

            // Set card padding using dp
            int cardPadding = dpToPx(20);

            ingredientCard.setPadding(
                    cardPadding,
                    cardPadding,
                    cardPadding,
                    cardPadding
            );

            // Apply rounded card background
            ingredientCard.setBackgroundResource(
                    R.drawable.bg_card
            );

            // Apply small elevation
            ingredientCard.setElevation(dpToPx(3));

            // Set layout dimensions
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            // Add spacing below each card
            params.setMargins(
                    0,
                    0,
                    0,
                    dpToPx(12)
            );

            ingredientCard.setLayoutParams(params);

            // Make the card clickable
            ingredientCard.setClickable(true);
            ingredientCard.setLongClickable(true);

            // Tap an ingredient to edit it
            ingredientCard.setOnClickListener(view -> {

                openEditIngredient(item.getId());
            });

            // Long-press an ingredient to delete it
            ingredientCard.setOnLongClickListener(view -> {

                showDeleteConfirmation(item);

                return true;
            });

            // Add the card to the list container
            container.addView(ingredientCard);
        }
    }

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
