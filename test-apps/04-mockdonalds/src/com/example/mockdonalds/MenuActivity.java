package com.example.mockdonalds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity {
    private List<MenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try { super.onCreate(savedInstanceState); } catch (Exception e) {}

        // Get a real Context - try host Activity, fallback to this
        Context ctx = this;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object inst = host.getField("instance").get(null);
            if (inst instanceof Context) ctx = (Context) inst;
        } catch (Exception e) {}

        // Hardcoded menu data (no database needed)
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1, "Big Mock Burger", "Delicious", 5.99, "Burgers"));
        menuItems.add(new MenuItem(2, "Quarter Mocker", "Classic", 4.99, "Burgers"));
        menuItems.add(new MenuItem(3, "Mock Nuggets (6)", "Crispy", 3.49, "Sides"));
        menuItems.add(new MenuItem(4, "Mock Fries (L)", "Golden", 2.99, "Sides"));
        menuItems.add(new MenuItem(5, "Mock Cola (L)", "Refreshing", 1.99, "Drinks"));
        menuItems.add(new MenuItem(6, "Mock Shake", "Creamy", 3.99, "Drinks"));
        menuItems.add(new MenuItem(7, "Mock Flurry", "Sweet", 2.49, "Desserts"));
        menuItems.add(new MenuItem(8, "Apple Mock Pie", "Warm", 1.49, "Desserts"));

        // Build UI with real Android Views
        final Context fCtx = ctx;
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        TextView header = new TextView(ctx);
        header.setText("MockDonalds Menu");
        header.setTextSize(28);
        header.setTextColor(0xFFFF0000);
        header.setPadding(16, 16, 16, 8);
        root.addView(header);

        ListView listView = new ListView(ctx);
        listView.setAdapter(new BaseAdapter() {
            public int getCount() { return menuItems.size(); }
            public Object getItem(int p) { return menuItems.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                LinearLayout row = new LinearLayout(fCtx);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(16, 12, 16, 12);
                MenuItem item = menuItems.get(pos);
                TextView name = new TextView(fCtx);
                name.setText(item.name);
                name.setTextSize(20);
                name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(name);
                TextView price = new TextView(fCtx);
                price.setText("$" + String.format("%.2f", item.price));
                price.setTextSize(16);
                price.setTextColor(0xFF4CAF50);
                row.addView(price);
                return row;
            }
        });
        root.addView(listView, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        Button cartBtn = new Button(ctx);
        cartBtn.setText("View Cart");
        cartBtn.setTextSize(18);
        root.addView(cartBtn);

        // Store root view for WestlakeActivity to display
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            host.getField("shimRootView").set(null, root);
        } catch (Exception e) {}

        try { setContentView(root); } catch (Exception e) {}
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
}
