package com.example.notes;

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

/** Main screen: shows all notes in a ListView with header and "New Note" button. */
public class NoteListActivity extends Activity {
    private NoteDbHelper dbHelper;
    private List<Note> notes;
    private ListView listView;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = NoteDbHelper.getInstance(this);
        notes = dbHelper.getNotes();

        // Build UI programmatically
        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView();
        header.setText("My Notes");
        header.setTextSize(24);
        header.setTextColor(0xFF1976D2);
        root.addView(header);

        // Count display
        TextView countView = new TextView();
        countView.setText(notes.size() + " notes");
        countView.setTextSize(14);
        root.addView(countView);

        // ListView with notes
        listView = new ListView();
        adapter = new NoteAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView parent, android.view.View view, int position, long id) {
                Note note = notes.get(position);
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.notes.NoteEditActivity"));
                intent.putExtra("note_id", note.id);
                startActivityForResult(intent, 100);
            }
        });
        root.addView(listView);

        // New Note button
        Button newBtn = new Button(new android.content.Context());
        newBtn.setText("New Note");
        newBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                int newId = dbHelper.addNote("Untitled", "");
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.notes.NoteEditActivity"));
                intent.putExtra("note_id", newId);
                startActivityForResult(intent, 100);
            }
        });
        root.addView(newBtn);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }

    private void refreshList() {
        notes = dbHelper.getNotes();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public List<Note> getNotes() { return notes; }
    public NoteDbHelper getDbHelper() { return dbHelper; }

    /** Adapter for the notes ListView. Each row shows title + first 50 chars of body. */
    private class NoteAdapter extends BaseAdapter {
        @Override public int getCount() { return notes.size(); }
        @Override public Object getItem(int position) { return notes.get(position); }
        @Override public long getItemId(int position) { return notes.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Note note = notes.get(position);
            LinearLayout row = new LinearLayout(new android.content.Context());
            row.setOrientation(LinearLayout.VERTICAL);

            // Title
            TextView titleView = new TextView();
            titleView.setText(note.title);
            titleView.setTextSize(16);
            titleView.setTextColor(0xFF000000);
            row.addView(titleView);

            // Preview (first 50 chars of body)
            TextView previewView = new TextView();
            previewView.setText(note.getPreview());
            previewView.setTextSize(12);
            previewView.setTextColor(0xFF888888);
            row.addView(previewView);

            row.setTag(note);
            return row;
        }
    }
}
