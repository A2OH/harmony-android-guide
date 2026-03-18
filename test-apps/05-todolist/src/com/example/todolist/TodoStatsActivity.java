package com.example.todolist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/** Statistics screen: shows total/completed/pending counts and tracks last_viewed via SharedPreferences. */
public class TodoStatsActivity extends Activity {
    private static final String PREFS_NAME = "todolist_prefs";
    private static final String KEY_LAST_VIEWED = "last_viewed";

    private int totalCount;
    private int completedCount;
    private int pendingCount;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TodoDbHelper dbHelper = TodoDbHelper.getInstance(this);
        List<TodoItem> all = dbHelper.getTodos();
        totalCount = all.size();
        completedCount = dbHelper.getCompletedCount();
        pendingCount = dbHelper.getPendingCount();

        prefs = getSharedPreferences(PREFS_NAME, 0);
        long now = System.currentTimeMillis();
        prefs.edit().putLong(KEY_LAST_VIEWED, now).apply();

        // Build UI
        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        TextView header = new TextView();
        header.setText("TODO Statistics");
        header.setTextSize(24);
        header.setTextColor(0xFF1976D2);
        root.addView(header);

        TextView totalView = new TextView();
        totalView.setText("Total: " + totalCount);
        totalView.setTextSize(18);
        root.addView(totalView);

        TextView completedView = new TextView();
        completedView.setText("Completed: " + completedCount);
        completedView.setTextSize(18);
        completedView.setTextColor(0xFF4CAF50);
        root.addView(completedView);

        TextView pendingView = new TextView();
        pendingView.setText("Pending: " + pendingCount);
        pendingView.setTextSize(18);
        pendingView.setTextColor(0xFFFF9800);
        root.addView(pendingView);

        Button backBtn = new Button(new android.content.Context());
        backBtn.setText("Back");
        backBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) { finish(); }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    public int getTotalCount() { return totalCount; }
    public int getCompletedCount() { return completedCount; }
    public int getPendingCount() { return pendingCount; }
    public SharedPreferences getPrefs() { return prefs; }
}
