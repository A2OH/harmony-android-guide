package com.example.mockdonalds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/** Main menu screen — shows all menu items in a ListView. */
public class MenuActivity extends Activity {
    private MenuDbHelper dbHelper;
    private CartManager cartManager;
    private List<MenuItem> menuItems;
    private TextView cartCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new MenuDbHelper(this);
        cartManager = new CartManager(this);
        menuItems = dbHelper.getAllItems();

        // Build UI programmatically
        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView(getApplicationContext());
        header.setText("MockDonalds Menu");
        header.setTextSize(24);
        header.setTextColor(0xFFFF0000);
        root.addView(header);

        // Cart count
        cartCountView = new TextView(getApplicationContext());
        updateCartCount();
        root.addView(cartCountView);

        // Menu ListView
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(new MenuAdapter());
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override public void onItemClick(android.widget.AdapterView parent, android.view.View view, int position, long id) {
                MenuItem item = menuItems.get(position);
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.mockdonalds.ItemDetailActivity"));
                intent.putExtra("item_id", item.id);
                intent.putExtra("item_name", item.name);
                intent.putExtra("item_price", item.price);
                intent.putExtra("item_description", item.description);
                startActivityForResult(intent, 100);
            }
        });
        root.addView(listView);

        // Cart button
        Button cartBtn = new Button(new android.content.Context());
        cartBtn.setText("View Cart");
        cartBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.mockdonalds.CartActivity"));
                startActivityForResult(intent, 200);
            }
        });
        root.addView(cartBtn);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateCartCount();
    }

    private void updateCartCount() {
        int count = cartManager.getCartCount();
        cartCountView.setText("Cart: " + count + " items");
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public CartManager getCartManager() { return cartManager; }
    public MenuDbHelper getDbHelper() { return dbHelper; }

    /** Adapter for the menu ListView. */
    private class MenuAdapter extends BaseAdapter {
        @Override public int getCount() { return menuItems.size(); }
        @Override public Object getItem(int position) { return menuItems.get(position); }
        @Override public long getItemId(int position) { return menuItems.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = menuItems.get(position);
            LinearLayout row = new LinearLayout(new android.content.Context());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView nameView = new TextView(getApplicationContext());
            nameView.setText(item.name);
            nameView.setTextSize(16);
            row.addView(nameView);

            TextView priceView = new TextView(getApplicationContext());
            priceView.setText("  " + item.getPriceString());
            priceView.setTextColor(0xFF008800);
            row.addView(priceView);

            row.setTag(item);
            return row;
        }
    }
}
