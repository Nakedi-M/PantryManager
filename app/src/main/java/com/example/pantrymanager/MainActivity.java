package com.example.pantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrymanager.data.AppDatabase;
import com.example.pantrymanager.data.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import com.example.pantrymanager.data.RecipeSeeder;
public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private PantryAdapter adapter;
    private TextView textEmpty;

    // Room cannot run on the main thread, so database work goes here
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keeps the content clear of the status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        executor.execute(() -> RecipeSeeder.seedIfEmpty(db));
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new PantryAdapter(new PantryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PantryItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(AddEditActivity.EXTRA_ITEM_ID, item.id);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(PantryItem item) {
                confirmDelete(item);
            }
        });

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditActivity.class)));
    }

    // Reload every time the screen comes back into view
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        executor.execute(() -> {
            List<PantryItem> items = db.pantryDao().getAll();
            runOnUiThread(() -> {
                adapter.setItems(items);
                textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.name + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) ->
                        executor.execute(() -> {
                            db.pantryDao().delete(item);
                            loadItems();
                        }))
                .setNegativeButton("Cancel", null)
                .show();
    }
}