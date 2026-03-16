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

/** Cart screen — shows cart items + total + checkout button. */
public class CartActivity extends Activity {
    private CartManager cartManager;
    private List<CartManager.CartItem> cartItems;
    private TextView totalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartManager = new CartManager(this);
        cartItems = cartManager.getCartItems();

        LinearLayout root = new LinearLayout();
        root.setOrientation(LinearLayout.VERTICAL);

        TextView header = new TextView();
        header.setText("Your Cart");
        header.setTextSize(24);
        root.addView(header);

        if (cartItems.isEmpty()) {
            TextView emptyView = new TextView();
            emptyView.setText("Cart is empty");
            emptyView.setTextSize(16);
            root.addView(emptyView);
        } else {
            ListView listView = new ListView();
            listView.setAdapter(new CartAdapter());
            root.addView(listView);
        }

        totalView = new TextView();
        totalView.setText("Total: " + cartManager.getCartTotalString());
        totalView.setTextSize(20);
        totalView.setTextColor(0xFFFF0000);
        root.addView(totalView);

        Button checkoutBtn = new Button();
        checkoutBtn.setText("Checkout");
        checkoutBtn.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.mockdonalds.CheckoutActivity"));
                intent.putExtra("total", cartManager.getCartTotalString());
                intent.putExtra("item_count", cartItems.size());
                startActivityForResult(intent, 300);
            }
        });
        root.addView(checkoutBtn);

        Button backBtn = new Button();
        backBtn.setText("Back to Menu");
        backBtn.setOnClickListener(v -> finish());
        root.addView(backBtn);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Order placed — cart cleared
            setResult(RESULT_OK);
            finish();
        }
    }

    public CartManager getCartManager() { return cartManager; }
    public List<CartManager.CartItem> getCartItems() { return cartItems; }

    private class CartAdapter extends BaseAdapter {
        @Override public int getCount() { return cartItems.size(); }
        @Override public Object getItem(int pos) { return cartItems.get(pos); }
        @Override public long getItemId(int pos) { return cartItems.get(pos).itemId; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CartManager.CartItem item = cartItems.get(position);
            LinearLayout row = new LinearLayout();
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView nameView = new TextView();
            nameView.setText(item.name);
            row.addView(nameView);

            TextView priceView = new TextView();
            priceView.setText("  " + item.getPriceString());
            row.addView(priceView);

            return row;
        }
    }
}
