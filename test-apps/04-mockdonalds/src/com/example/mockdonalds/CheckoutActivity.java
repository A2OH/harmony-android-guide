package com.example.mockdonalds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Checkout confirmation screen. */
public class CheckoutActivity extends Activity {
    private String total;
    private int itemCount;
    private int orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        total = intent.getStringExtra("total");
        itemCount = intent.getIntExtra("item_count", 0);

        CartManager cart = new CartManager(this);
        orderNumber = cart.checkout();

        LinearLayout root = new LinearLayout();
        root.setOrientation(LinearLayout.VERTICAL);

        TextView header = new TextView();
        header.setText("Order Confirmed!");
        header.setTextSize(24);
        header.setTextColor(0xFF008800);
        root.addView(header);

        TextView orderView = new TextView();
        orderView.setText("Order #" + orderNumber);
        orderView.setTextSize(18);
        root.addView(orderView);

        TextView summaryView = new TextView();
        summaryView.setText(itemCount + " items — " + total);
        summaryView.setTextSize(16);
        root.addView(summaryView);

        TextView thankYou = new TextView();
        thankYou.setText("Thank you for choosing MockDonalds!");
        thankYou.setTextSize(14);
        root.addView(thankYou);

        Button doneBtn = new Button();
        doneBtn.setText("Done");
        doneBtn.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        root.addView(doneBtn);

        setContentView(root);
    }

    public int getOrderNumber() { return orderNumber; }
    public String getTotal() { return total; }
    public int getItemCount() { return itemCount; }
}
