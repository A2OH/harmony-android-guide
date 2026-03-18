package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Detail screen: shows/edit a single todo item. Loaded from Intent extras. */
public class TodoDetailActivity extends Activity {
    private int todoId;
    private String todoTitle;
    private String todoDescription;
    private boolean todoCompleted;
    private int todoPriority;

    private EditText titleEdit;
    private EditText descEdit;
    private CheckBox completedCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        todoId = intent.getIntExtra("todo_id", -1);
        todoTitle = intent.getStringExtra("todo_title");
        todoDescription = intent.getStringExtra("todo_description");
        todoCompleted = intent.getBooleanExtra("todo_completed", false);
        todoPriority = intent.getIntExtra("todo_priority", 1);

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView();
        header.setText("TODO Detail");
        header.setTextSize(24);
        header.setTextColor(0xFF1976D2);
        root.addView(header);

        // Title edit
        TextView titleLabel = new TextView();
        titleLabel.setText("Title:");
        titleLabel.setTextSize(14);
        root.addView(titleLabel);

        titleEdit = new EditText(new android.content.Context());
        titleEdit.setText(todoTitle != null ? todoTitle : "");
        titleEdit.setTextSize(18);
        root.addView(titleEdit);

        // Description edit
        TextView descLabel = new TextView();
        descLabel.setText("Description:");
        descLabel.setTextSize(14);
        root.addView(descLabel);

        descEdit = new EditText(new android.content.Context());
        descEdit.setText(todoDescription != null ? todoDescription : "");
        descEdit.setTextSize(14);
        root.addView(descEdit);

        // Completed checkbox
        completedCheck = new CheckBox();
        completedCheck.setChecked(todoCompleted);
        completedCheck.setText("Completed");
        root.addView(completedCheck);

        // Priority display
        TextView priorityView = new TextView();
        String pStr = todoPriority == 3 ? "High (!!!)" : todoPriority == 2 ? "Medium (!!)" : "Low (!)";
        priorityView.setText("Priority: " + pStr);
        priorityView.setTextSize(14);
        root.addView(priorityView);

        // Save button
        Button saveBtn = new Button(new android.content.Context());
        saveBtn.setText("Save");
        saveBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                // Update the item via DB
                TodoDbHelper db = TodoDbHelper.getInstance(TodoDetailActivity.this);
                TodoItem item = db.getTodo(todoId);
                if (item != null) {
                    item.title = titleEdit.getText().toString();
                    item.description = descEdit.getText().toString();
                    item.completed = completedCheck.isChecked();
                    db.updateTodo(item);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
        root.addView(saveBtn);

        // Delete button
        Button deleteBtn = new Button(new android.content.Context());
        deleteBtn.setText("Delete");
        deleteBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                TodoDbHelper db = TodoDbHelper.getInstance(TodoDetailActivity.this);
                db.deleteTodo(todoId);
                setResult(RESULT_OK);
                finish();
            }
        });
        root.addView(deleteBtn);

        // Back button
        Button backBtn = new Button(new android.content.Context());
        backBtn.setText("Back");
        backBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) { finish(); }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    public int getTodoId() { return todoId; }
    public String getTodoTitle() { return todoTitle; }
    public String getTodoDescription() { return todoDescription; }
    public boolean isTodoCompleted() { return todoCompleted; }
    public int getTodoPriority() { return todoPriority; }
    public EditText getTitleEdit() { return titleEdit; }
    public EditText getDescEdit() { return descEdit; }
    public CheckBox getCompletedCheck() { return completedCheck; }
}
