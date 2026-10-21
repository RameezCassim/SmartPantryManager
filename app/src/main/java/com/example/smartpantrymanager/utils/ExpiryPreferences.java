package com.example.smartpantrymanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpiryPreferences {

    private static final String PREFS_NAME = "smart_pantry_settings";

    private static final String KEY_WARNINGS_ENABLED =
            "expiry_warnings_enabled";

    private static final String KEY_WARNING_DAYS =
            "expiry_warning_days";

    private static final int DEFAULT_WARNING_DAYS = 3;

    private final SharedPreferences preferences;

    public ExpiryPreferences(Context context) {

        preferences = context.getApplicationContext()
                .getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );
    }

    // Check whether expiry warnings are enabled
    public boolean areWarningsEnabled() {

        return preferences.getBoolean(
                KEY_WARNINGS_ENABLED,
                true
        );
    }

    // Save whether warnings are enabled
    public void setWarningsEnabled(boolean enabled) {

        preferences.edit()
                .putBoolean(KEY_WARNINGS_ENABLED, enabled)
                .apply();
    }

    // Retrieve how many days before expiry to warn
    public int getWarningDays() {

        return preferences.getInt(
                KEY_WARNING_DAYS,
                DEFAULT_WARNING_DAYS
        );
    }

    // Save the expiry warning threshold
    public void setWarningDays(int days) {

        if (days != 1 && days != 3 &&
                days != 7 && days != 14) {
            return;
        }

        preferences.edit()
                .putInt(KEY_WARNING_DAYS, days)
                .apply();
    }

    // Work out how many calendar days remain
    // Returns null if the date is absent or invalid
    public Integer getDaysUntilExpiry(String expiryDate) {

        if (expiryDate == null ||
                expiryDate.trim().isEmpty()) {
            return null;
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );

        format.setLenient(false);

        try {

            Date date = format.parse(expiryDate.trim());

            if (date == null) {
                return null;
            }

            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar expiry = Calendar.getInstance();
            expiry.setTime(date);

            expiry.set(Calendar.HOUR_OF_DAY, 0);
            expiry.set(Calendar.MINUTE, 0);
            expiry.set(Calendar.SECOND, 0);
            expiry.set(Calendar.MILLISECOND, 0);

            // Compare calendar dates rather than hours
            Calendar cursor = (Calendar) today.clone();

            int days = 0;

            if (cursor.before(expiry)) {

                while (cursor.before(expiry)) {
                    cursor.add(Calendar.DAY_OF_MONTH, 1);
                    days++;
                }

            } else if (cursor.after(expiry)) {

                while (cursor.after(expiry)) {
                    cursor.add(Calendar.DAY_OF_MONTH, -1);
                    days--;
                }
            }

            return days;

        } catch (ParseException exception) {
            return null;
        }
    }

    // Return the expiry message appropriate for Settings
    public String getExpiryWarning(String expiryDate) {

        if (!areWarningsEnabled()) {
            return null;
        }

        Integer days = getDaysUntilExpiry(expiryDate);

        if (days == null) {
            return null;
        }

        if (days < 0) {
            return "Expired " + Math.abs(days) +
                    (Math.abs(days) == 1
                            ? " day ago"
                            : " days ago");
        }

        if (days == 0) {
            return "Expires today";
        }

        if (days <= getWarningDays()) {
            return "Expiring in " + days +
                    (days == 1 ? " day" : " days");
        }

        return null;
    }
}
