# Smart Pantry Manager

## Overview
Smart Pantry Manager is a Java-based Android application developed.
The application helps users reduce food waste by tracking ingredients they have at home and identifying recipes that can be prepared using those ingredients.
Its main feature is **strict recipe matching**: a recipe is suggested only when the pantry contains every required ingredient in sufficient quantity.

## Main Features

### 1. Pantry Management
- Add new ingredients with a name, quantity, unit and optional expiry date.
- View saved ingredients in a RecyclerView.
- Edit existing ingredient information.
- Delete ingredients.
- Preserve ingredient records between application sessions using SQLite.

### 2. Recipe Collection
- Stores 20 predefined recipes in SQLite.
- View the complete recipe collection using Explore Recipes.
- View each recipe's required ingredients and preparation instructions.

### 3. Strict Recipe Matching
- Compare the current pantry against the ingredients required by each recipe.
- Suggest recipes only when all ingredient requirements are met.
- Reject recipes with missing or insufficient ingredients.
- Support basic ingredient-name normalization and compatible unit conversions, including grams/kilograms and millilitres/litres.

### 4. Recipe Availability
- Display available recipes with green status indicators.
- Display missing ingredients and quantity shortages in red.
- Keep unavailable recipes out of the strict Suggested Recipes list.

### 5. Settings
- Enable or disable expiry warnings.
- Select an expiry-warning threshold.
- Save settings locally using SharedPreferences.

## Technologies Used:
- Java: Android application logic
- XML: Screen layouts and visual resources
- Android Studio: Development environment
- SQLite: Persistent storage of pantry items and recipes
- SQLiteOpenHelper: Database creation and management
- RecyclerView: Dynamic ingredient and recipe lists
- Custom Adapters: Connect Java objects to RecyclerView layouts
- Activities and Intents: Screen navigation and selected-recipe data passing
- SharedPreferences: Persistent application settings
- Git and GitHub: Version control and development history

## Database Design
The application uses a local SQLite database named `smart_pantry.db`.

### pantry_items
Stores ingredients entered by the user.

- `id`
- `name`
- `quantity`
- `unit`
- `expiry_date`

### recipes
Stores predefined recipe information.

- `id`
- `name`
- `instructions`

### recipe_ingredients
Stores the ingredients required for each recipe.

- `id`
- `recipe_id`
- `ingredient_name`
- `required_quantity`
- `unit`

The `recipe_id` field links each recipe ingredient to its corresponding recipe.

## Why SQLite?
SQLite was selected because it supports local, persistent data storage without requiring an internet connection or an external database server.
The application manages data using SQLiteOpenHelper and DAO classes, separating database operations from the user-interface code.
This makes SQLite suitable for the self-contained pantry application.

## Application Screens
1. **My Pantry** — View and manage pantry ingredients.
2. **Add/Edit Ingredient** — Create or update ingredient records.
3. **Explore All Recipes** — Browse predefined recipes and view availability.
4. **Suggested Recipes** — View only recipes that satisfy strict matching.
5. **Recipe Details** — Read ingredient requirements and preparation instructions.
6. **Settings** — Configure expiry-warning preferences.

The two recipe-list modes are presented using the recipe-list Activity.

## How to Run

### Requirements
- Android Studio with an installed Android SDK.
- A configured Android emulator or compatible physical Android device.
- Java/Gradle environment supported by the project.

### Installation
1. Download or clone this GitHub repository.
2. Open Android Studio.
3. Select **Open** and choose the `SmartPantryManager` project directory.
4. Allow Gradle synchronization to complete.
5. Select an available Android emulator or connected device.
6. Select the `app` Run configuration.
7. Click **Run**.

### Command-Line Build
From the project root on Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

## How to Use the Application
1. Launch Smart Pantry Manager.
2. Tap **Add Ingredient** to save ingredients to your pantry.
3. Tap an ingredient to edit it, or long-press it to delete it.
4. Tap **Explore Recipes** to browse all stored recipes and see missing ingredients.
5. Use **Suggested Recipes** to see only recipes that can be prepared using the current pantry.
6. Tap a recipe to view its complete requirements and preparation method.
7. Open **Settings** to configure expiry-warning preferences such as (Expire in 3 days, 7 days, etc.).

## Strict-Matching Example
**If a recipe requires:**

- 2 eggs
- 50 ml milk
- 30 g cheese

**And the pantry contains:**

- 6 eggs
- 20 ml milk
- 50 g cheese

The recipe will not appear in Suggested Recipes because the pantry lacks 30 ml of milk.
Once the user increases the milk quantity to at least 50 ml, the recipe can then qualify.

## Scope
The application focuses on ingredient tracking and recipe suggestions. It does not include maps, GPS, payments or external grocery-store services.

## Development
This project is maintained using Git and GitHub, with commits documenting its development stages and implemented features.

## Academic Context
Developed as part of the Mobile App Development 700 practical assignment.

## Author
**Rameez Cassim**