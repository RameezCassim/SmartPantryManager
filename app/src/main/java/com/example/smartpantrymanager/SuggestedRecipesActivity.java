package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.SuggestedRecipeAdapter;
import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.RecipeMatcher;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {


    public static final String EXTRA_SHOW_ALL_RECIPES =
            "com.example.smartpantrymanager.SHOW_ALL_RECIPES";

    private boolean showAllRecipes = false;

    private PantryDao pantryDao;
    private RecipeDao recipeDao;
    private SuggestedRecipeAdapter adapter;

    private RecyclerView recyclerView;
    private View emptyRecipesState;
    private TextView tvMatchingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);


// Check whether the user opened Explore Recipes
        showAllRecipes = getIntent().getBooleanExtra(
                EXTRA_SHOW_ALL_RECIPES,
                false
        );

// Update the header for the selected mode
        TextView screenTitle = findViewById(
                R.id.tvRecipeScreenTitle
        );

        TextView screenSubtitle = findViewById(
                R.id.tvRecipeScreenSubtitle
        );

        if (showAllRecipes) {

            screenTitle.setText("Explore All Recipes");

            screenSubtitle.setText(
                    "Browse every recipe and see what ingredients you need."
            );

        } else {

            screenTitle.setText("Suggested Recipes");

            screenSubtitle.setText(
                    "Recipes you can cook with your current pantry."
            );
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database access
        pantryDao = new PantryDao(this);
        recipeDao = new RecipeDao(this);

        // Find screen components
        recyclerView = findViewById(
                R.id.recyclerSuggestedRecipes
        );

        emptyRecipesState = findViewById(
                R.id.emptyRecipesState
        );

        tvMatchingCount = findViewById(
                R.id.tvMatchingCount
        );

        Button btnBackToPantry = findViewById(
                R.id.btnBackToPantry
        );

        // Arrange recipe cards vertically
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Create RecyclerView Adapter
        adapter = new SuggestedRecipeAdapter(recipe -> {

            // Recipe Details Activity will be added in Phase 7
            Toast.makeText(
                    this,
                    "Recipe details coming in Phase 7: " +
                            recipe.getName(),
                    Toast.LENGTH_SHORT
            ).show();
        });

        recyclerView.setAdapter(adapter);

        // Return to the previous screen
        btnBackToPantry.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshSuggestions();
    }

    // Retrieve the pantry and calculate strict matches

    private void refreshSuggestions() {

        // Read the latest pantry data
        List<PantryItem> pantryItems = pantryDao.getAll();

        // Retrieve all 20 recipes from SQLite
        List<Recipe> allRecipes = recipeDao.getAllRecipes();

        // Calculate strictly available recipes
        List<Recipe> matchingRecipes =
                RecipeMatcher.findMatchingRecipes(
                        allRecipes,
                        pantryItems
                );

        // Choose which collection to show
        List<Recipe> displayedRecipes;

        if (showAllRecipes) {

            // Explore mode: show every recipe
            displayedRecipes = allRecipes;

            tvMatchingCount.setText(
                    "All Recipes (" + allRecipes.size() + ")" +
                            " • " + matchingRecipes.size() + " cookable"
            );

        } else {

            // Suggested mode: only strict matches
            displayedRecipes = matchingRecipes;

            int count = matchingRecipes.size();

            tvMatchingCount.setText(
                    count == 1
                            ? "1 recipe available"
                            : count + " recipes available"
            );
        }

        // Show empty state only when the displayed list is empty
        if (displayedRecipes.isEmpty()) {

            emptyRecipesState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        } else {

            emptyRecipesState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Supply both recipes and pantry data for status messages
        adapter.updateRecipes(
                displayedRecipes,
                pantryItems
        );
    }

}
