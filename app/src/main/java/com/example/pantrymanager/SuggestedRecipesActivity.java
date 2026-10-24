package com.example.pantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrymanager.data.AppDatabase;
import com.example.pantrymanager.data.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecipeAdapter adapter;
    private TextView textEmpty;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setTitle("Suggested Recipes");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new RecipeAdapter(recipe -> Toast.makeText(this,
                "Recipe detail screen coming next", Toast.LENGTH_SHORT).show());

        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        NavHelper.setup(this, R.id.nav_suggested);
    }

    // Re-run the matching every time the screen comes into view,
    // so changes made in the pantry show up straight away
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        executor.execute(() -> {
            List<Recipe> suggested = RecipeMatcher.getSuggestedRecipes(db);
            runOnUiThread(() -> {
                adapter.setRecipes(suggested);
                textEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}