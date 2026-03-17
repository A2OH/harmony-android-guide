package com.example.mockdonalds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Item detail screen — shows item info + "Add to Cart" button. */
public class ItemDetailActivity extends Activity {
    private int itemId;
    private String itemName;
    private double itemPrice;
    private String itemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        itemId = intent.getIntExtra("item_id", -1);
        itemName = intent.getStringExtra("item_name");
        itemPrice = intent.getDoubleExtra("item_price", 0.0);
        itemDescription = intent.getStringExtra("item_description");

        LinearLayout root = new LinearLayout();
        root.setOrientation(LinearLayout.VERTICAL);

        TextView nameView = new TextView();
        nameView.setText(itemName);
        nameView.setTextSize(24);
        root.addView(nameView);

        TextView priceView = new TextView();
        int cents = (int)(itemPrice * 100 + 0.5);
        priceView.setText("$" + (cents / 100) + "." + (cents % 100 < 10 ? "0" : "") + (cents % 100));
        priceView.setTextSize(18);
        priceView.setTextColor(0xFF008800);
        root.addView(priceView);

        TextView descView = new TextView();
        descView.setText(itemDescription != null ? itemDescription : "");
        descView.setTextSize(14);
        root.addView(descView);

        Button addBtn = new Button();
        addBtn.setText("Add to Cart");
        addBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                CartManager cart = new CartManager(ItemDetailActivity.this);
                MenuDbHelper db = new MenuDbHelper(ItemDetailActivity.this);
                MenuItem item = db.getItem(itemId);
                if (item != null) {
                    int count = cart.addToCart(item);
                    Intent result = new Intent();
                    result.putExtra("cart_count", count);
                    setResult(RESULT_OK, result);
                }
                finish();
            }
        });
        root.addView(addBtn);

        Button backBtn = new Button();
        backBtn.setText("Back");
        backBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) { finish(); }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getItemPrice() { return itemPrice; }
}
