package com.example.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataActivity extends Activity {
    private EditText keyInput;
    private EditText valueInput;
    private TextView statusText;
    private SharedPreferences prefs;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        keyInput = new EditText(this);
        keyInput.setHint("Key");
        layout.addView(keyInput);

        valueInput = new EditText(this);
        valueInput.setHint("Value");
        layout.addView(valueInput);

        statusText = new TextView(this);
        statusText.setText("Ready");
        layout.addView(statusText);

        // SharedPreferences save
        Button saveBtn = new Button(this);
        saveBtn.setText(R.string.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToPrefs();
            }
        });
        layout.addView(saveBtn);

        // SharedPreferences load
        Button loadBtn = new Button(this);
        loadBtn.setText(R.string.btn_load);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromPrefs();
            }
        });
        layout.addView(loadBtn);

        // SQLite insert+query
        Button queryBtn = new Button(this);
        queryBtn.setText(R.string.btn_query);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSQLite();
            }
        });
        layout.addView(queryBtn);

        setContentView(layout);

        prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        initDatabase();
    }

    private void saveToPrefs() {
        String key = keyInput.getText().toString();
        String value = valueInput.getText().toString();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        statusText.setText("Saved: " + key + "=" + value);
    }

    private void loadFromPrefs() {
        String key = keyInput.getText().toString();
        String value = prefs.getString(key, "(not found)");
        statusText.setText("Loaded: " + key + "=" + value);
    }

    private void initDatabase() {
        DbHelper helper = new DbHelper();
        db = helper.getWritableDatabase();
    }

    private void testSQLite() {
        ContentValues cv = new ContentValues();
        cv.put("key", keyInput.getText().toString());
        cv.put("value", valueInput.getText().toString());
        db.insert("kvstore", null, cv);

        Cursor cursor = db.query("kvstore", null, null, null, null, null, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        statusText.setText("SQLite rows: " + count);
    }

    private class DbHelper extends SQLiteOpenHelper {
        DbHelper() {
            super(DataActivity.this, "test.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE kvstore (id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT, value TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS kvstore");
            onCreate(db);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
