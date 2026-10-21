package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.utils.ExpiryPreferences;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryWarnings;
    private Spinner spinnerExpiryDays;
    private TextView tvSettingsSaved;

    private ExpiryPreferences expiryPreferences;

    // Supported expiry warning thresholds
    private final int[] warningDays = {1, 3, 7, 14};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize preferences
        expiryPreferences = new ExpiryPreferences(this);

        // Find XML components
        switchExpiryWarnings = findViewById(
                R.id.switchExpiryWarnings
        );

        spinnerExpiryDays = findViewById(
                R.id.spinnerExpiryDays
        );

        tvSettingsSaved = findViewById(
                R.id.tvSettingsSaved
        );

        Button btnBack = findViewById(
                R.id.btnSettingsBackToPantry
        );

        // Restore the saved warning switch value
        boolean warningsEnabled =
                expiryPreferences.areWarningsEnabled();

        switchExpiryWarnings.setChecked(warningsEnabled);

        // Configure dropdown options
        String[] warningOptions = {
                "1 day before expiry",
                "3 days before expiry",
                "7 days before expiry",
                "14 days before expiry"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        warningOptions
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerExpiryDays.setAdapter(spinnerAdapter);

        // Restore previously selected warning threshold
        int savedDays = expiryPreferences.getWarningDays();

        for (int i = 0; i < warningDays.length; i++) {

            if (warningDays[i] == savedDays) {
                spinnerExpiryDays.setSelection(i);
                break;
            }
        }

        // Disable threshold selection when warnings are off
        spinnerExpiryDays.setEnabled(warningsEnabled);

        // Save switch changes
        switchExpiryWarnings.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    expiryPreferences.setWarningsEnabled(
                            isChecked
                    );

                    spinnerExpiryDays.setEnabled(isChecked);

                    tvSettingsSaved.setText(
                            "Preference saved successfully."
                    );
                }
        );

        // Save the selected threshold
        spinnerExpiryDays.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (position >= 0 &&
                                position < warningDays.length) {

                            expiryPreferences.setWarningDays(
                                    warningDays[position]
                            );

                            tvSettingsSaved.setText(
                                    "Preferences saved automatically."
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                        // Keep the current saved preference
                    }
                }
        );

        // Return to the previous screen
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }
}
