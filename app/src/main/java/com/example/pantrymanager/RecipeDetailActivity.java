package com.example.pantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pantrymanager.data.AppDatabase;
import com.example.pantrymanager.data.Recipe;
import com.example.pantrymanager.data.RecipeIngredient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {

    // Key used to pass the recipe's ID in the Intent
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private TextView textName, textIngredients, textSteps;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setTitle("Recipe");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        textName = findViewById(R.id.textDetailName);
        textIngredients = findViewById(R.id.textDetailIngredients);
        textSteps = findViewById(R.id.textDetailSteps);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            finish();   // nothing to show
            return;
        }
        loadRecipe(recipeId);
    }

    // Reads the recipe and its ingredients off the main thread, then shows them
    private void loadRecipe(int recipeId) {
        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            Recipe recipe = db.recipeDao().getRecipe(recipeId);
            List<RecipeIngredient> ingredients = db.recipeDao().getIngredientsFor(recipeId);

            StringBuilder list = new StringBuilder();
            for (RecipeIngredient ing : ingredients) {
                list.append("\u2022 ")
                        .append(formatQuantity(ing.quantity))
                        .append(" ")
                        .append(ing.unit)
                        .append(" ")
                        .append(ing.name)
                        .append("\n");
            }

            runOnUiThread(() -> {
                if (recipe != null) {
                    textName.setText(recipe.name);
                    textSteps.setText(recipe.steps);
                    textIngredients.setText(list.toString().trim());
                }
            });
        });
    }

    // Shows 3.0 as "3" but keeps 0.5 as "0.5"
    private String formatQuantity(double q) {
        if (q == Math.floor(q)) {
            return String.valueOf((int) q);
        }
        return String.valueOf(q);
    }
}