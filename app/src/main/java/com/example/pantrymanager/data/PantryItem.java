package com.example.pantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;        // store lowercase and trimmed
    public double quantity;
    public String unit;        // e.g. "g", "ml", "pcs"
    public String expiryDate;  // optional, can be null
}