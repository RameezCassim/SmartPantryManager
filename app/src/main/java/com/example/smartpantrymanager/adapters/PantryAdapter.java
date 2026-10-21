
package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantrymanager.utils.ExpiryPreferences;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // List of ingredients displayed by RecyclerView
    private final List<PantryItem> ingredients = new ArrayList<>();

    // Interface for communicating clicks to MainActivity
    public interface OnIngredientActionListener {

        void onIngredientClick(PantryItem item);

        void onIngredientLongClick(PantryItem item);
    }

    // Listener supplied by MainActivity
    private final OnIngredientActionListener listener;

    // Constructor
    public PantryAdapter(OnIngredientActionListener listener) {
        this.listener = listener;
    }

    // Create a ViewHolder for an ingredient card
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        // Convert our XML item layout into an actual View
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_pantry_ingredient,
                        parent,
                        false
                );

        // Return a ViewHolder containing the card
        return new PantryViewHolder(view);
    }

    // Fill the ViewHolder with ingredient information
    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = ingredients.get(position);


        // Read the user's saved expiry preferences
        ExpiryPreferences expiryPreferences =
                new ExpiryPreferences(
                        holder.itemView.getContext()
                );

        // Calculate the expiry warning for this ingredient
        String warning = expiryPreferences.getExpiryWarning(
                item.getExpiryDate()
        );

        // Show or hide the warning
        if (warning != null) {
            holder.tvExpiryWarning.setText(warning);
            holder.tvExpiryWarning.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiryWarning.setText("");
            holder.tvExpiryWarning.setVisibility(View.GONE);
        }


        // Display the ingredient name
        holder.tvName.setText(item.getName());

        // Format quantity without unnecessary decimal zeros
        String quantityText = String.format(
                Locale.US,
                "%s %s",
                formatQuantity(item.getQuantity()),
                item.getUnit()
        );

        holder.tvQuantity.setText(quantityText);

        // Display expiry information
        if (item.getExpiryDate() != null &&
                !item.getExpiryDate().isEmpty()) {

            holder.tvExpiry.setText(
                    "Expiry: " + item.getExpiryDate()
            );

        } else {

            holder.tvExpiry.setText("No expiry date");
        }

        // Tap to edit
        holder.itemView.setOnClickListener(view -> {

            listener.onIngredientClick(item);
        });

        // Long-press to delete
        holder.itemView.setOnLongClickListener(view -> {

            listener.onIngredientLongClick(item);

            return true;
        });
    }

    // Tell RecyclerView how many ingredients exist
    @Override
    public int getItemCount() {

        return ingredients.size();
    }

    // Replace the displayed ingredients with fresh data
    public void updateIngredients(List<PantryItem> newItems) {

        ingredients.clear();

        if (newItems != null) {
            ingredients.addAll(newItems);
        }

        // Refresh visible rows after the data changes
        notifyDataSetChanged();
    }

    // Convert whole numbers to integers for display
    private String formatQuantity(double quantity) {

        if (Double.isFinite(quantity) &&
                quantity == Math.floor(quantity) &&
                quantity >= Long.MIN_VALUE &&
                quantity < 9223372036854775808.0) {

            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }

    // ViewHolder stores references to the card's views
    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        TextView tvExpiry;

        TextView tvExpiryWarning;


        public PantryViewHolder(@NonNull View itemView) {

            super(itemView);

            tvName = itemView.findViewById(R.id.tvItemName);

            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);

            tvExpiry = itemView.findViewById(R.id.tvItemExpiry);

            tvExpiryWarning = itemView.findViewById(R.id.tvExpiryWarning);
        }
    }
}
