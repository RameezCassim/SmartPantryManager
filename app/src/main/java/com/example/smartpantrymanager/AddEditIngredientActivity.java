package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.database.SQLException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.models.PantryItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    // Key for passing an ingredient ID using an Intent
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    // -1 indicates that we are adding a new ingredient
    private long ingredientId = -1;

    // Database access
    private PantryDao pantryDao;

    // Form components
    private EditText etIngredientName;
    private EditText etQuantity;
    private Spinner spinnerUnit;
    private TextView tvExpiryDate;
    private TextView tvFormTitle;
    private Button btnSaveIngredient;

    // Selected expiry date
    private String selectedExpiryDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        // Apply safe padding for Android system bars
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.addEditRoot),
                (view, insets) -> {
                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            bars.bottom
                    );

                    return insets;
                }
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database access
        pantryDao = new PantryDao(this);

        // Connect Java variables to the XML views
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        tvExpiryDate = findViewById(R.id.tvExpiryDate);
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        Button btnClearDate = findViewById(R.id.btnClearDate);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Check whether an existing ingredient ID was provided
        Intent intent = getIntent();

        ingredientId = intent.getLongExtra(
                EXTRA_INGREDIENT_ID,
                -1
        );

        if (ingredientId != -1) {
            loadIngredient();
        }

        // Open the date picker
        btnSelectDate.setOnClickListener(view -> {
            showDatePicker();
        });

        // Remove the optional expiry date
        btnClearDate.setOnClickListener(view -> {
            selectedExpiryDate = null;
            tvExpiryDate.setText(R.string.select_expiry);
        });

        // Save the new or updated ingredient
        btnSaveIngredient.setOnClickListener(view -> {
            saveIngredient();
        });

        // Close the form without saving
        btnCancel.setOnClickListener(view -> {
            finish();
        });
    }

    // Load an existing ingredient into the form
    private void loadIngredient() {

        PantryItem item = pantryDao.getById(ingredientId);

        if (item == null) {
            Toast.makeText(
                    this,
                    "Ingredient not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Update screen title and button label
        tvFormTitle.setText(R.string.edit_ingredient_title);
        btnSaveIngredient.setText(R.string.update_ingredient);

        // Populate text fields
        etIngredientName.setText(item.getName());

        etQuantity.setText(
                String.valueOf(item.getQuantity())
        );

        // Select the previously saved measurement unit
        for (int i = 0; i < spinnerUnit.getCount(); i++) {

            String unit = spinnerUnit.getItemAtPosition(i).toString();

            if (unit.equals(item.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }

        // Load optional expiry date
        selectedExpiryDate = item.getExpiryDate();

        if (selectedExpiryDate != null &&
                !selectedExpiryDate.isEmpty()) {

            tvExpiryDate.setText(selectedExpiryDate);
        }
    }

    // Display Android's date selection dialog
    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        // If a date exists, initially display that date
        if (selectedExpiryDate != null) {
            try {
                LocalDate savedDate = LocalDate.parse(
                        selectedExpiryDate
                );

                calendar.set(
                        savedDate.getYear(),
                        savedDate.getMonthValue() - 1,
                        savedDate.getDayOfMonth()
                );

            } catch (DateTimeParseException ignored) {
                // Keep the current date if the stored date is invalid
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                    selectedExpiryDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    tvExpiryDate.setText(selectedExpiryDate);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    // Validate the form and save the ingredient
    private void saveIngredient() {

        String name = etIngredientName.getText()
                .toString()
                .trim();

        String quantityText = etQuantity.getText()
                .toString()
                .trim();

        String unit = spinnerUnit.getSelectedItem()
                .toString();

        // Ingredient name is compulsory
        if (name.isEmpty()) {

            etIngredientName.setError(
                    "Ingredient name is required."
            );

            etIngredientName.requestFocus();
            return;
        }

        // Quantity is compulsory
        if (quantityText.isEmpty()) {

            etQuantity.setError(
                    "Quantity is required."
            );

            etQuantity.requestFocus();
            return;
        }

        // Convert quantity from text to a number
        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Enter a valid numeric quantity."
            );

            etQuantity.requestFocus();
            return;
        }

        // Quantity must be greater than zero
        if (!Double.isFinite(quantity) || quantity <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than zero."
            );

            etQuantity.requestFocus();
            return;
        }

        // Verify the optional expiry date
        if (selectedExpiryDate != null) {

            try {
                LocalDate.parse(
                        selectedExpiryDate,
                        DateTimeFormatter.ISO_LOCAL_DATE
                );

            } catch (DateTimeParseException e) {

                Toast.makeText(
                        this,
                        "Please select a valid expiry date.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        try {

            if (ingredientId == -1) {

                // CREATE: Save a new ingredient
                PantryItem item = new PantryItem(
                        name,
                        quantity,
                        unit,
                        selectedExpiryDate
                );

                pantryDao.insert(item);

                Toast.makeText(
                        this,
                        "Ingredient added successfully!",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                // UPDATE: Modify an existing ingredient
                PantryItem item = new PantryItem(
                        ingredientId,
                        name,
                        quantity,
                        unit,
                        selectedExpiryDate
                );

                int updatedRows = pantryDao.update(item);

                if (updatedRows == 0) {
                    Toast.makeText(
                            this,
                            "Ingredient could not be updated.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                Toast.makeText(
                        this,
                        "Ingredient updated successfully!",
                        Toast.LENGTH_SHORT
                ).show();
            }

            // Close the form and return to My Pantry
            setResult(RESULT_OK);
            finish();

        } catch (SQLException | IllegalArgumentException e) {

            Toast.makeText(
                    this,
                    "Unable to save ingredient: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
