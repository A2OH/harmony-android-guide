package com.example.mockdonalds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** SQLiteOpenHelper that creates and populates the menu table. */
public class MenuDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mockdonalds.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_MENU = "menu";

    public MenuDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MENU + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "price REAL, " +
                "category TEXT)");

        insertItem(db, 1, "Big Mock Burger", "Two all-beef patties, special sauce", 5.99, "Burgers");
        insertItem(db, 2, "Quarter Mocker", "Quarter pound of pure mock beef", 4.99, "Burgers");
        insertItem(db, 3, "Mock Nuggets (6)", "Six crispy mock chicken nuggets", 3.49, "Sides");
        insertItem(db, 4, "Mock Fries (L)", "Large golden mock fries", 2.99, "Sides");
        insertItem(db, 5, "Mock Cola (L)", "Large refreshing mock cola", 1.99, "Drinks");
        insertItem(db, 6, "Mock Shake", "Thick creamy mock shake", 3.99, "Drinks");
        insertItem(db, 7, "Mock Flurry", "Soft serve with mix-ins", 2.49, "Desserts");
        insertItem(db, 8, "Apple Mock Pie", "Warm apple mock pie", 1.49, "Desserts");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        onCreate(db);
    }

    private void insertItem(SQLiteDatabase db, int id, String name, String desc, double price, String category) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("price", price);
        cv.put("category", category);
        db.insert(TABLE_MENU, null, cv);
    }

    /** Get all menu items. */
    public List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MENU, null, null, null, null, null, "id ASC");
        if (c != null && c.moveToFirst()) {
            do {
                items.add(new MenuItem(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("name")),
                    c.getString(c.getColumnIndex("description")),
                    c.getDouble(c.getColumnIndex("price")),
                    c.getString(c.getColumnIndex("category"))
                ));
            } while (c.moveToNext());
            c.close();
        }
        return items;
    }

    /** Get items by category. */
    public List<MenuItem> getItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MENU, null, "category = ?", new String[]{category}, null, null, "id ASC");
        if (c != null && c.moveToFirst()) {
            do {
                items.add(new MenuItem(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("name")),
                    c.getString(c.getColumnIndex("description")),
                    c.getDouble(c.getColumnIndex("price")),
                    c.getString(c.getColumnIndex("category"))
                ));
            } while (c.moveToNext());
            c.close();
        }
        return items;
    }

    /** Get single item by id. */
    public MenuItem getItem(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MENU, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            MenuItem item = new MenuItem(
                c.getInt(c.getColumnIndex("id")),
                c.getString(c.getColumnIndex("name")),
                c.getString(c.getColumnIndex("description")),
                c.getDouble(c.getColumnIndex("price")),
                c.getString(c.getColumnIndex("category"))
            );
            c.close();
            return item;
        }
        return null;
    }
}
