package com.example.pantrymanager.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("recipeId"))
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int recipeId;       // links to Recipe.id
    public String name;        // store lowercase and trimmed
    public double quantity;
    public String unit;
}