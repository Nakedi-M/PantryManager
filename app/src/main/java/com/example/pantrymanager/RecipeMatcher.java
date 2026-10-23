package com.example.pantrymanager;

import com.example.pantrymanager.data.AppDatabase;
import com.example.pantrymanager.data.PantryItem;
import com.example.pantrymanager.data.Recipe;
import com.example.pantrymanager.data.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The strict-matching rule: a recipe is suggested ONLY if every one of its
 * ingredients is in the pantry in at least the required quantity.
 */
public class RecipeMatcher {

    // Words that end in "s" but are not plurals
    private static final Set<String> NOT_PLURAL = new HashSet<>();

    // Unit -> multiplier that converts it to the base unit of its type
    private static final Map<String, Double> FACTOR = new HashMap<>();

    // Unit -> kind of measurement: "mass", "volume" or "count"
    private static final Map<String, String> TYPE = new HashMap<>();

    static {
        NOT_PLURAL.add("hummus");
        NOT_PLURAL.add("couscous");
        NOT_PLURAL.add("asparagus");
        NOT_PLURAL.add("molasses");

        // Mass: base unit is grams
        addUnits("mass", 1, "g", "gram", "grams");
        addUnits("mass", 1000, "kg", "kilogram", "kilograms");
        addUnits("mass", 28.35, "oz", "ounce", "ounces");
        addUnits("mass", 453.6, "lb", "lbs", "pound", "pounds");

        // Volume: base unit is millilitres
        addUnits("volume", 1, "ml", "millilitre", "milliliter", "millilitres", "milliliters");
        addUnits("volume", 1000, "l", "litre", "liter", "litres", "liters");
        addUnits("volume", 5, "tsp", "teaspoon", "teaspoons");
        addUnits("volume", 15, "tbsp", "tablespoon", "tablespoons");
        addUnits("volume", 250, "cup", "cups");

        // Count: base unit is one item
        addUnits("count", 1, "pcs", "pc", "piece", "pieces", "whole", "each", "unit", "units", "");
    }

    private static void addUnits(String type, double factor, String... names) {
        for (String name : names) {
            TYPE.put(name, type);
            FACTOR.put(name, factor);
        }
    }

    // Lowercases, trims and turns simple plurals into singular
    // (tomatoes -> tomato, eggs -> egg, chicken breasts -> chicken breast)
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (NOT_PLURAL.contains(s) || s.length() <= 3) {
            return s;
        }
        if (s.endsWith("ies")) {
            return s.substring(0, s.length() - 3) + "y";          // berries -> berry
        }
        if (s.endsWith("oes") || s.endsWith("ches") || s.endsWith("shes")
                || s.endsWith("sses") || s.endsWith("xes")) {
            return s.substring(0, s.length() - 2);                // tomatoes -> tomato
        }
        if (s.endsWith("ss")) {
            return s;                                             // watercress stays
        }
        if (s.endsWith("s")) {
            return s.substring(0, s.length() - 1);                // eggs -> egg
        }
        return s;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        return unit.trim().toLowerCase(Locale.ROOT).replace(".", "");
    }

    // Units we do not know (e.g. "can") only match the same unit ("cans" = "can")
    private static String typeOf(String unit) {
        String type = TYPE.get(unit);
        return type != null ? type : "other:" + normalizeName(unit);
    }

    private static double factorOf(String unit) {
        Double factor = FACTOR.get(unit);
        return factor != null ? factor : 1.0;
    }

    // True only if EVERY required ingredient is in the pantry in a big enough amount
    public static boolean canMake(List<RecipeIngredient> required, List<PantryItem> pantry) {
        if (required == null || required.isEmpty()) {
            return false;   // a recipe with no ingredients is never suggested
        }

        for (RecipeIngredient need : required) {
            String needName = normalizeName(need.name);
            String needUnit = normalizeUnit(need.unit);
            String needType = typeOf(needUnit);
            double needed = need.quantity * factorOf(needUnit);

            // Add up everything in the pantry that is the same ingredient
            // and measured in a compatible unit
            double available = 0;
            for (PantryItem have : pantry) {
                if (!needName.equals(normalizeName(have.name))) {
                    continue;
                }
                String haveUnit = normalizeUnit(have.unit);
                if (!needType.equals(typeOf(haveUnit))) {
                    continue;   // e.g. grams cannot cover "2 pieces"
                }
                available += have.quantity * factorOf(haveUnit);
            }

            if (available + 1e-9 < needed) {
                return false;   // one missing or short ingredient rules the recipe out
            }
        }
        return true;
    }

    // Loads the pantry and recipes and returns only the recipes that pass.
    // Must be called on a background thread (Room rule).
    public static List<Recipe> getSuggestedRecipes(AppDatabase db) {
        List<Recipe> suggested = new ArrayList<>();
        List<PantryItem> pantry = db.pantryDao().getAll();
        if (pantry.isEmpty()) {
            return suggested;
        }
        for (Recipe recipe : db.recipeDao().getAllRecipes()) {
            List<RecipeIngredient> required = db.recipeDao().getIngredientsFor(recipe.id);
            if (canMake(required, pantry)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }
}