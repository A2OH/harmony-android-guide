package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Edit screen: loads a note by ID from Intent extra, shows title/body editors, Save/Delete/Back. */
public class NoteEditActivity extends Activity {
    private int noteId;
    private Note currentNote;
    private NoteDbHelper dbHelper;
    private SharedPreferences prefs;

    private EditText titleEdit;
    private EditText bodyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = NoteDbHelper.getInstance(this);
        prefs = getSharedPreferences("notes_prefs", 0);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("note_id", -1);
        currentNote = dbHelper.getNote(noteId);

        // Store last opened note id in SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("last_opened_note_id", noteId);
        editor.apply();

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView();
        header.setText("Edit Note");
        header.setTextSize(24);
        header.setTextColor(0xFF1976D2);
        root.addView(header);

        // Title label
        TextView titleLabel = new TextView();
        titleLabel.setText("Title:");
        titleLabel.setTextSize(14);
        root.addView(titleLabel);

        // Title edit
        titleEdit = new EditText(new android.content.Context());
        titleEdit.setText(currentNote != null ? currentNote.title : "");
        titleEdit.setTextSize(18);
        root.addView(titleEdit);

        // Body label
        TextView bodyLabel = new TextView();
        bodyLabel.setText("Body:");
        bodyLabel.setTextSize(14);
        root.addView(bodyLabel);

        // Body edit (multiline)
        bodyEdit = new EditText(new android.content.Context());
        bodyEdit.setText(currentNote != null ? currentNote.body : "");
        bodyEdit.setTextSize(14);
        root.addView(bodyEdit);

        // Save button
        Button saveBtn = new Button(new android.content.Context());
        saveBtn.setText("Save");
        saveBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (currentNote != null) {
                    currentNote.title = titleEdit.getText().toString();
                    currentNote.body = bodyEdit.getText().toString();
                    dbHelper.updateNote(currentNote);
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
            @Override
            public void onClick(android.view.View v) {
                if (currentNote != null) {
                    dbHelper.deleteNote(currentNote.id);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
        root.addView(deleteBtn);

        // Back button
        Button backBtn = new Button(new android.content.Context());
        backBtn.setText("Back");
        backBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
            }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    public int getNoteId() { return noteId; }
    public Note getCurrentNote() { return currentNote; }
    public EditText getTitleEdit() { return titleEdit; }
    public EditText getBodyEdit() { return bodyEdit; }
    public SharedPreferences getPrefs() { return prefs; }
}
