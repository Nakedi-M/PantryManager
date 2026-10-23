package com.example.pantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.pantrymanager.data.PantryItem;
import com.example.pantrymanager.data.RecipeIngredient;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    private PantryItem item(String name, double qty, String unit) {
        PantryItem p = new PantryItem();
        p.name = name;
        p.quantity = qty;
        p.unit = unit;
        return p;
    }

    private RecipeIngredient ing(String name, double qty, String unit) {
        RecipeIngredient r = new RecipeIngredient();
        r.name = name;
        r.quantity = qty;
        r.unit = unit;
        return r;
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                item("egg", 6, "pcs"), item("butter", 250, "g"));
        List<RecipeIngredient> recipe = Arrays.asList(
                ing("egg", 3, "pcs"), ing("butter", 10, "g"));
        assertTrue(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                item("egg", 6, "pcs"), item("tomato", 4, "pcs"),
                item("onion", 3, "pcs"), item("butter", 250, "g"));
        List<RecipeIngredient> recipe = Arrays.asList(
                ing("egg", 3, "pcs"), ing("tomato", 1, "pcs"),
                ing("onion", 1, "pcs"), ing("butter", 10, "g"),
                ing("cheese", 50, "g"));   // missing from the pantry
        assertFalse(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void notEnoughQuantity_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(item("rice", 100, "g"));
        List<RecipeIngredient> recipe = Arrays.asList(ing("rice", 200, "g"));
        assertFalse(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void pluralNames_stillMatch() {
        List<PantryItem> pantry = Arrays.asList(item("tomatoes", 4, "pcs"));
        List<RecipeIngredient> recipe = Arrays.asList(ing("tomato", 2, "pcs"));
        assertTrue(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void unitConversion_kilogramsCoverGrams() {
        List<PantryItem> pantry = Arrays.asList(item("rice", 1, "kg"));
        List<RecipeIngredient> recipe = Arrays.asList(ing("rice", 200, "g"));
        assertTrue(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void incompatibleUnits_doNotMatch() {
        List<PantryItem> pantry = Arrays.asList(item("potato", 500, "g"));
        List<RecipeIngredient> recipe = Arrays.asList(ing("potato", 2, "pcs"));
        assertFalse(RecipeMatcher.canMake(recipe, pantry));
    }

    @Test
    public void recipeWithNoIngredients_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(item("egg", 6, "pcs"));
        assertFalse(RecipeMatcher.canMake(Arrays.<RecipeIngredient>asList(), pantry));
    }

    @Test
    public void normalizeName_handlesCommonPlurals() {
        assertEquals("tomato", RecipeMatcher.normalizeName("Tomatoes"));
        assertEquals("egg", RecipeMatcher.normalizeName(" eggs "));
        assertEquals("chicken breast", RecipeMatcher.normalizeName("Chicken Breasts"));
        assertEquals("cheese", RecipeMatcher.normalizeName("cheese"));
    }
}