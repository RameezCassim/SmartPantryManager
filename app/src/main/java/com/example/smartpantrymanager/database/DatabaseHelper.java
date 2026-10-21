package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Name of the database stored on the Android device
    public static final String DATABASE_NAME = "smart_pantry.db";

    // First version of our database schema
    public static final int DATABASE_VERSION = 1;

    // Pantry table and column names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";

    // SQL to create the pantry inventory table
    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL COLLATE NOCASE, " +
                    COL_QUANTITY + " REAL NOT NULL CHECK (" +
                    COL_QUANTITY + " > 0), " +
                    COL_UNIT + " TEXT NOT NULL, " +
                    COL_EXPIRY_DATE + " TEXT" +
                    ")";

    // SQL to create the recipes table
    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE recipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE, " +
                    "instructions TEXT NOT NULL" +
                    ")";

    // SQL to connect recipes with required ingredients
    private static final String CREATE_RECIPE_INGREDIENTS_TABLE =
            "CREATE TABLE recipe_ingredients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "recipe_id INTEGER NOT NULL, " +
                    "ingredient_name TEXT NOT NULL COLLATE NOCASE, " +
                    "required_quantity REAL NOT NULL " +
                    "CHECK (required_quantity > 0), " +
                    "unit TEXT NOT NULL, " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id) " +
                    "ON DELETE CASCADE" +
                    ")";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Configure SQLite before database creation or opening
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);

        // Indexes to support ingredient and recipe lookups
        db.execSQL(
                "CREATE INDEX idx_pantry_name " +
                        "ON pantry_items(name)"
        );

        db.execSQL(
                "CREATE INDEX idx_recipe_ingredients_recipe " +
                        "ON recipe_ingredients(recipe_id)"
        );
    }

    // Called when the database version is increased
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Never silently delete the user's pantry records.
        // Add the appropriate ALTER TABLE or data migration
        // before increasing DATABASE_VERSION.

        throw new IllegalStateException(
                "Database migration required from version " +
                        oldVersion + " to " + newVersion
        );
    }
}

