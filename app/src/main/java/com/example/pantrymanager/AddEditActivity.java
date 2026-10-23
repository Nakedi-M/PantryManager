package com.example.pantrymanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pantrymanager.data.AppDatabase;
import com.example.pantrymanager.data.PantryItem;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditActivity extends AppCompatActivity {

    // Key used to pass the item's ID in the Intent (-1 means "add new")
    public static final String EXTRA_ITEM_ID = "item_id";

    // Expiry years must fall in this range
    private static final int MIN_YEAR = 2026;
    private static final int MAX_YEAR = 2100;

    private EditText editName, editQuantity, editUnit;
    private EditText editDay, editMonth, editYear;
    private AppDatabase db;
    private int itemId = -1;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editDay = findViewById(R.id.editDay);
        editMonth = findViewById(R.id.editMonth);
        editYear = findViewById(R.id.editYear);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Once the day has 2 digits, jump to month; once month has 2, jump to year
        autoAdvance(editDay, 2, editMonth);
        autoAdvance(editMonth, 2, editYear);

        // If an ID was passed in, we are editing an existing item
        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            setTitle("Edit ingredient");
            loadItem();
        } else {
            setTitle("Add ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    // Moves the cursor to the next box when this one is full
    private void autoAdvance(EditText from, int length, EditText to) {
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // hasFocus() stops it jumping when the form is filled in by code
                if (from.hasFocus() && s.length() == length) {
                    to.requestFocus();
                }
            }
        });
    }

    // Fills the form with the existing item's values (background thread)
    private void loadItem() {
        executor.execute(() -> {
            PantryItem item = db.pantryDao().getById(itemId);
            runOnUiThread(() -> {
                if (item != null) {
                    editName.setText(item.name);
                    editQuantity.setText(String.valueOf(item.quantity));
                    editUnit.setText(item.unit);
                    showExpiry(item.expiryDate);
                }
            });
        });
    }

    // Splits a saved date into the three boxes (handles dd/mm/yyyy and older yyyy-mm-dd)
    private void showExpiry(String saved) {
        if (saved == null || saved.isEmpty()) {
            return;
        }
        if (saved.contains("/")) {
            String[] p = saved.split("/");
            if (p.length == 3) {
                editDay.setText(p[0]);
                editMonth.setText(p[1]);
                editYear.setText(p[2]);
            }
        } else if (saved.contains("-")) {
            String[] p = saved.split("-");
            if (p.length == 3) {
                editYear.setText(p[0]);
                editMonth.setText(p[1]);
                editDay.setText(p[2]);
            }
        }
    }

    // Returns an error message, or null if the date is valid
    private String validateExpiry(String day, String month, String year) {
        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            return "Enter day, month and year (dd/mm/yyyy)";
        }
        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);
        int y = Integer.parseInt(year);

        if (y < MIN_YEAR) {
            return "Year must be " + MIN_YEAR + " or later";
        }
        if (y > MAX_YEAR) {
            return "Year must be " + MAX_YEAR + " or earlier";
        }
        if (m < 1 || m > 12) {
            return "Month must be 1 to 12";
        }

        // Non-lenient Calendar rejects dates that do not exist (e.g. 31/02)
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.clear();
        cal.set(y, m - 1, d);
        try {
            cal.getTime();
        } catch (IllegalArgumentException e) {
            return "That date does not exist";
        }
        return null;
    }

    private void saveItem() {
        String name = editName.getText().toString().trim().toLowerCase();
        String qtyText = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim().toLowerCase();
        String day = editDay.getText().toString().trim();
        String month = editMonth.getText().toString().trim();
        String year = editYear.getText().toString().trim();

        // Input validation
        boolean valid = true;

        if (name.isEmpty()) {
            editName.setError("Name is required");
            valid = false;
        }

        double quantity = 0;
        if (qtyText.isEmpty()) {
            editQuantity.setError("Quantity is required");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText);
                if (quantity <= 0) {
                    editQuantity.setError("Quantity must be more than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                editQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (unit.isEmpty()) {
            editUnit.setError("Unit is required");
            valid = false;
        }

        // The expiry date is optional, but if any box is filled, all three must be valid
        String expiry = null;
        boolean anyDate = !day.isEmpty() || !month.isEmpty() || !year.isEmpty();
        if (anyDate) {
            String error = validateExpiry(day, month, year);
            if (error != null) {
                editDay.setError(error);
                valid = false;
            } else {
                expiry = String.format(Locale.US, "%02d/%02d/%04d",
                        Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));
            }
        }

        if (!valid) {
            return;
        }

        PantryItem item = new PantryItem();
        item.name = name;
        item.quantity = quantity;
        item.unit = unit;
        item.expiryDate = expiry;

        // Room must run off the main thread
        executor.execute(() -> {
            if (itemId == -1) {
                db.pantryDao().insert(item);       // Create
            } else {
                item.id = itemId;
                db.pantryDao().update(item);       // Update
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();   // go back to the list
            });
        });
    }
}