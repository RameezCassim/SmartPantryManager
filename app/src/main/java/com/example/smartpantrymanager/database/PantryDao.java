
package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryDao {

    // Database helper used by this DAO
    private final DatabaseHelper dbHelper;

    // Constructor
    public PantryDao(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    // CREATE: Insert an ingredient into SQLite
    public long insert(PantryItem item) {

        validate(item);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = toContentValues(item);

        return db.insertOrThrow(
                DatabaseHelper.TABLE_PANTRY,
                null,
                values
        );
    }

    // READ: Retrieve all pantry ingredients
    public List<PantryItem> getAll() {

        List<PantryItem> items = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COL_NAME + " COLLATE NOCASE ASC"
        )) {

            while (cursor.moveToNext()) {
                items.add(toPantryItem(cursor));
            }
        }

        return items;
    }

    // READ: Find one ingredient by its ID
    public PantryItem getById(long id) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                DatabaseHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {

            if (cursor.moveToFirst()) {
                return toPantryItem(cursor);
            }
        }

        return null;
    }

    // UPDATE: Modify an existing pantry ingredient
    public int update(PantryItem item) {

        validate(item);

        if (item.getId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid ingredient ID is required."
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = toContentValues(item);

        return db.update(
                DatabaseHelper.TABLE_PANTRY,
                values,
                DatabaseHelper.COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );
    }

    // DELETE: Remove an ingredient by ID
    public int delete(long id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "A valid ingredient ID is required."
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // COUNT: Return the number of saved ingredients
    public int getCount() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PANTRY,
                null
        )) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // Convert a PantryItem object into database values
    private ContentValues toContentValues(PantryItem item) {

        ContentValues values = new ContentValues();

        values.put(
                DatabaseHelper.COL_NAME,
                item.getName().trim()
        );

        values.put(
                DatabaseHelper.COL_QUANTITY,
                item.getQuantity()
        );

        values.put(
                DatabaseHelper.COL_UNIT,
                item.getUnit().trim()
        );

        String expiryDate = item.getExpiryDate();

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull(DatabaseHelper.COL_EXPIRY_DATE);
        } else {
            values.put(
                    DatabaseHelper.COL_EXPIRY_DATE,
                    expiryDate.trim()
            );
        }

        return values;
    }

    // Convert the current database row into a Java object
    private PantryItem toPantryItem(Cursor cursor) {

        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)
        );

        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
        );

        double quantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)
        );

        String unit = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)
        );

        int expiryColumn = cursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_EXPIRY_DATE
        );

        String expiryDate = cursor.isNull(expiryColumn)
                ? null
                : cursor.getString(expiryColumn);

        return new PantryItem(
                id,
                name,
                quantity,
                unit,
                expiryDate
        );
    }

    // Check that ingredient information is valid
    private void validate(PantryItem item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "Ingredient cannot be null."
            );
        }

        if (item.getName() == null ||
                item.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ingredient name is required."
            );
        }

        if (item.getUnit() == null ||
                item.getUnit().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ingredient unit is required."
            );
        }

        double quantity = item.getQuantity();

        if (Double.isNaN(quantity) ||
                Double.isInfinite(quantity) ||
                quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }
}
