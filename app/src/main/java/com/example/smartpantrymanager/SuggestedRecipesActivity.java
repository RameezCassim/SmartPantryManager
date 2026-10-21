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

        // Read all available pantry ingredients
        List<PantryItem> pantryItems =
                pantryDao.getAll();

        // Read all stored recipes and their requirements
        List<Recipe> allRecipes =
                recipeDao.getAllRecipes();

        // Include only recipes whose every requirement is met
        List<Recipe> matchingRecipes =
                RecipeMatcher.findMatchingRecipes(
                        allRecipes,
                        pantryItems
                );

        int matchingCount = matchingRecipes.size();

        // Update the results heading
        tvMatchingCount.setText(
                matchingCount == 1
                        ? "1 recipe available"
                        : matchingCount + " recipes available"
        );

        // Show a helpful message if nothing matches
        if (matchingRecipes.isEmpty()) {

            emptyRecipesState.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);

        } else {

            emptyRecipesState.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
        }

        // Display only strictly matching recipes
        adapter.updateRecipes(matchingRecipes);
    }
}
