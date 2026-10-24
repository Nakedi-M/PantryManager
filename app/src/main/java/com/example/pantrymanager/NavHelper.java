package com.example.pantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavHelper {

    // Highlights the current tab and sends the user to the others using Intents
    public static void setup(Activity activity, int selectedId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) {
                return true;   // already on this screen
            }
            // Returning false keeps THIS screen's highlight on its own tab.
            // The screen we open highlights its own tab.
            if (id == R.id.nav_pantry) {
                open(activity, MainActivity.class);
                return false;
            }
            if (id == R.id.nav_suggested) {
                open(activity, SuggestedRecipesActivity.class);
                return false;
            }
            if (id == R.id.nav_settings) {
                Toast.makeText(activity, "Settings screen coming soon",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
        });
    }

    private static void open(Activity activity, Class<?> target) {
        Intent intent = new Intent(activity, target);
        // Re-use the screen if it is already open, with no slide animation
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        // Also cancel the leaving animation (may show a strikethrough warning,
        // which is fine, it still works)
        activity.overridePendingTransition(0, 0);
    }
}