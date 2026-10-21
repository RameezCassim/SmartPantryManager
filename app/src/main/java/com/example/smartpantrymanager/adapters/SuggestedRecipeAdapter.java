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

// Java collection classes
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipeAdapter
        extends RecyclerView.Adapter<SuggestedRecipeAdapter.RecipeViewHolder> {

    // Stores only recipes that passed strict matching
    private final List<Recipe> recipes = new ArrayList<>();

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
    public void updateRecipes(List<Recipe> newRecipes) {

        // Remove previously displayed recipes
        recipes.clear();

        // Add the latest strictly matching recipes
        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }

        // Notify RecyclerView that its data has changed
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
        }
    }
}
