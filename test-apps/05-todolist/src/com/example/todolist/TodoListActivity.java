package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/** Main screen: shows all TODOs in a ListView with header and Add button. */
public class TodoListActivity extends Activity {
    private TodoDbHelper dbHelper;
    private List<TodoItem> todoItems;
    private ListView listView;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = TodoDbHelper.getInstance(this);
        todoItems = dbHelper.getTodos();

        // Build UI programmatically
        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView();
        header.setText("My TODOs");
        header.setTextSize(24);
        header.setTextColor(0xFF1976D2);
        root.addView(header);

        // Count display
        TextView countView = new TextView();
        countView.setText(todoItems.size() + " items");
        countView.setTextSize(14);
        root.addView(countView);

        // ListView with todos
        listView = new ListView();
        adapter = new TodoAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override public void onItemClick(android.widget.AdapterView parent, android.view.View view, int position, long id) {
                TodoItem item = todoItems.get(position);
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.todolist.TodoDetailActivity"));
                intent.putExtra("todo_id", item.id);
                intent.putExtra("todo_title", item.title);
                intent.putExtra("todo_description", item.description);
                intent.putExtra("todo_completed", item.completed);
                intent.putExtra("todo_priority", item.priority);
                startActivityForResult(intent, 100);
            }
        });
        root.addView(listView);

        // Add TODO button
        Button addBtn = new Button(new android.content.Context());
        addBtn.setText("Add TODO");
        addBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                // In a real app this would open an add-item activity
                dbHelper.addTodo("New Task", "Description", 2);
                refreshList();
            }
        });
        root.addView(addBtn);

        // Stats button
        Button statsBtn = new Button(new android.content.Context());
        statsBtn.setText("View Stats");
        statsBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.todolist.TodoStatsActivity"));
                startActivityForResult(intent, 200);
            }
        });
        root.addView(statsBtn);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }

    private void refreshList() {
        todoItems = dbHelper.getTodos();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public List<TodoItem> getTodoItems() { return todoItems; }
    public TodoDbHelper getDbHelper() { return dbHelper; }

    /** Adapter for the todo ListView. */
    private class TodoAdapter extends BaseAdapter {
        @Override public int getCount() { return todoItems.size(); }
        @Override public Object getItem(int position) { return todoItems.get(position); }
        @Override public long getItemId(int position) { return todoItems.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TodoItem item = todoItems.get(position);
            LinearLayout row = new LinearLayout(new android.content.Context());
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Completed checkbox
            CheckBox checkBox = new CheckBox();
            checkBox.setChecked(item.completed);
            checkBox.setText("");
            row.addView(checkBox);

            // Title
            TextView titleView = new TextView();
            titleView.setText(item.title);
            titleView.setTextSize(16);
            row.addView(titleView);

            // Priority indicator
            TextView priorityView = new TextView();
            priorityView.setText("  " + item.getPriorityString());
            priorityView.setTextColor(item.priority == 3 ? 0xFFFF0000 :
                                      item.priority == 2 ? 0xFFFF8800 : 0xFF888888);
            row.addView(priorityView);

            row.setTag(item);
            return row;
        }
    }
}
