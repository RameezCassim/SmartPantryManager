package com.example.smartpantrymanager.adapters;

// Android classes used to create and display item layouts
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// AndroidX RecyclerView classes
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Smart Pantry Manager resources and models
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.Recipe;

import android.graphics.Color;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.utils.RecipeMatcher;

// Java collection classes
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipeAdapter extends RecyclerView.Adapter<SuggestedRecipeAdapter.RecipeViewHolder> {

    // Stores only recipes that passed strict matching
    private final List<Recipe> recipes = new ArrayList<>();
    private final List<PantryItem> pantryItems = new ArrayList<>();

    // Interface used to communicate recipe clicks to the Activity
    public interface OnRecipeClickListener {

        void onRecipeClick(Recipe recipe);
    }

    // Reference to the click listener
    private final OnRecipeClickListener listener;

    // Constructor
    public SuggestedRecipeAdapter(OnRecipeClickListener listener) {

        this.listener = listener;
    }

    // =====================================================
    // 1. CREATE A RECIPE VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        // Convert the recipe card XML into an actual View
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_suggested_recipe,
                        parent,
                        false
                );

        // Create and return a ViewHolder
        return new RecipeViewHolder(view);
    }

    // =====================================================
    // 2. DISPLAY RECIPE INFORMATION
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        // Retrieve the recipe at the current position
        Recipe recipe = recipes.get(position);

        // Display the recipe name
        holder.tvName.setText(
                recipe.getName()
        );

        // Count how many ingredients the recipe requires
        int ingredientCount = recipe.getIngredients().size();

        // Display the number of required ingredients
        String ingredientText =
                ingredientCount == 1
                        ? "1 ingredient required"
                        : ingredientCount + " ingredients required";

        holder.tvIngredients.setText(ingredientText);

        // Handle clicks on the recipe card
        holder.itemView.setOnClickListener(view -> {

// Calculate what the user is missing for this recipe
            List<String> shortages = RecipeMatcher.getShortages(
                    recipe,
                    pantryItems
            );

            if (shortages.isEmpty()) {

                // All requirements are satisfied
                holder.tvAvailability.setText(
                        "You have everything needed!"
                );

                holder.tvAvailability.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.pantry_green_dark
                        )
                );

            } else {

                // Show every missing or insufficient ingredient
                holder.tvAvailability.setText(
                        android.text.TextUtils.join("\n", shortages)
                );

                holder.tvAvailability.setTextColor(
                        Color.rgb(190, 45, 45)
                );
            }


            // Inform the Activity which recipe was selected
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    // =====================================================
    // 3. RETURN THE NUMBER OF RECIPES
    // =====================================================

    @Override
    public int getItemCount() {

        return recipes.size();
    }

    // =====================================================
    // 4. UPDATE THE DISPLAYED RECIPE LIST
    // =====================================================

    @SuppressWarnings("NotifyDataSetChanged")
    public void updateRecipes(
            List<Recipe> newRecipes,
            List<PantryItem> currentPantry) {

        // Clear the previous data
        recipes.clear();
        pantryItems.clear();

        // Load the recipes to display
        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }

        // Load the latest pantry quantities
        if (currentPantry != null) {
            pantryItems.addAll(currentPantry);
        }

        // Refresh RecyclerView
        notifyDataSetChanged();
    }


    // =====================================================
    // 5. RECIPE VIEW HOLDER
    // =====================================================

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        // Recipe card TextViews
        final TextView tvName;
        final TextView tvIngredients;

        final TextView tvAvailability;


        // Constructor
        public RecipeViewHolder(@NonNull View itemView) {

            super(itemView);

            // Find the recipe name TextView
            tvName = itemView.findViewById(
                    R.id.tvSuggestedRecipeName
            );

            // Find the ingredient count TextView
            tvIngredients = itemView.findViewById(
                    R.id.tvSuggestedRecipeIngredients
            );

            tvAvailability = itemView.findViewById(
                    R.id.tvRecipeAvailability
            );

        }
    }
}
