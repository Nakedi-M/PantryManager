package com.example.pantrymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrymanager.data.PantryItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // Lets MainActivity decide what happens on tap (edit) and long-press (delete)
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
    }

    // Items expiring within this many days are flagged
    private static final int SOON_DAYS = 3;

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private boolean expiryAlertsOn = true;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Turned on or off from the Settings screen
    public void setExpiryAlertsEnabled(boolean enabled) {
        this.expiryAlertsOn = enabled;
    }

    // Replaces the list contents and redraws the screen
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.textName.setText(item.name);
        holder.textQuantity.setText(item.quantity + " " + item.unit);

        String expiry = item.expiryDate;
        if (expiry == null || expiry.isEmpty()) {
            holder.textExpiry.setText("No expiry date");
            holder.textExpiry.setTextColor(holder.defaultColor);
        } else {
            String text = "Expires: " + expiry;
            int color = holder.defaultColor;

            if (expiryAlertsOn) {
                long days = daysUntil(expiry);
                if (days < 0) {
                    text += " (expired)";
                    color = Color.RED;
                } else if (days <= SOON_DAYS) {
                    text += " (expiring soon)";
                    color = Color.RED;
                }
            }
            holder.textExpiry.setText(text);
            holder.textExpiry.setTextColor(color);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Whole days from today until the date (negative = already passed).
    // Handles dd/mm/yyyy and the older yyyy-mm-dd; returns a huge number if unreadable.
    private long daysUntil(String saved) {
        try {
            int d, m, y;
            if (saved.contains("/")) {
                String[] p = saved.split("/");
                d = Integer.parseInt(p[0]);
                m = Integer.parseInt(p[1]);
                y = Integer.parseInt(p[2]);
            } else {
                String[] p = saved.split("-");
                y = Integer.parseInt(p[0]);
                m = Integer.parseInt(p[1]);
                d = Integer.parseInt(p[2]);
            }

            Calendar expiry = Calendar.getInstance();
            expiry.clear();
            expiry.set(y, m - 1, d);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diff = expiry.getTimeInMillis() - today.getTimeInMillis();
            return Math.round(diff / 86400000.0);
        } catch (Exception e) {
            return Long.MAX_VALUE;   // could not read the date, so no alert
        }
    }

    // Holds the views of one row so they can be reused as you scroll
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final int defaultColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            defaultColor = textExpiry.getCurrentTextColor();   // remembered so rows can be reset
        }
    }
}