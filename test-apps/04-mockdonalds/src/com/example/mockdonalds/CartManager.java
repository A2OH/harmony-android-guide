package com.example.mockdonalds;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** Manages the shopping cart via SQLite + SharedPreferences for order count. */
public class CartManager {
    private static final String PREFS_NAME = "mockdonalds_prefs";
    private static final String KEY_ORDER_COUNT = "order_count";

    private static CartDbHelper sharedDbHelper;
    private final CartDbHelper dbHelper;
    private final SharedPreferences prefs;

    public CartManager(Context context) {
        if (sharedDbHelper == null) {
            sharedDbHelper = new CartDbHelper(context);
        }
        dbHelper = sharedDbHelper;
        prefs = context.getSharedPreferences(PREFS_NAME, 0);
    }

    /** Add an item to the cart. Returns the new cart count. */
    public int addToCart(MenuItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id", item.id);
        cv.put("name", item.name);
        cv.put("price", item.price);
        cv.put("quantity", 1);
        db.insert("cart", null, cv);
        return getCartCount();
    }

    /** Get all cart items as a list of (name, price, quantity). */
    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("cart", null, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                items.add(new CartItem(
                    c.getInt(c.getColumnIndex("item_id")),
                    c.getString(c.getColumnIndex("name")),
                    c.getDouble(c.getColumnIndex("price")),
                    c.getInt(c.getColumnIndex("quantity"))
                ));
            } while (c.moveToNext());
            c.close();
        }
        return items;
    }

    /** Get total number of items in cart. */
    public int getCartCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM cart", null);
        int count = 0;
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
            c.close();
        }
        return count;
    }

    /** Get total price of all cart items. */
    public double getCartTotal() {
        List<CartItem> items = getCartItems();
        double total = 0;
        for (CartItem item : items) {
            total += item.price * item.quantity;
        }
        return total;
    }

    public String getCartTotalString() {
        double total = getCartTotal();
        int cents = (int)(total * 100 + 0.5);
        return "$" + (cents / 100) + "." + (cents % 100 < 10 ? "0" : "") + (cents % 100);
    }

    /** Clear the cart and increment order count. */
    public int checkout() {
        // Clear cart
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", null, null);

        // Increment order count in prefs
        int count = prefs.getInt(KEY_ORDER_COUNT, 0) + 1;
        prefs.edit().putInt(KEY_ORDER_COUNT, count).apply();
        return count;
    }

    /** Get total number of orders placed. */
    public int getOrderCount() {
        return prefs.getInt(KEY_ORDER_COUNT, 0);
    }

    /** Clear the cart without placing an order. */
    public void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cart", null, null);
    }

    /** Cart item data class. */
    public static class CartItem {
        public final int itemId;
        public final String name;
        public final double price;
        public final int quantity;

        public CartItem(int itemId, String name, double price, int quantity) {
            this.itemId = itemId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getPriceString() {
            int cents = (int)(price * 100 + 0.5);
            return "$" + (cents / 100) + "." + (cents % 100 < 10 ? "0" : "") + (cents % 100);
        }
    }

    /** Internal DB helper for the cart table. */
    private static class CartDbHelper extends SQLiteOpenHelper {
        CartDbHelper(Context context) {
            super(context, "cart.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE cart (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "item_id INTEGER, " +
                    "name TEXT, " +
                    "price REAL, " +
                    "quantity INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS cart");
            onCreate(db);
        }
    }
}
