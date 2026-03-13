package android.app;

import android.widget.ListView;

/**
 * Android-compatible ListActivity shim. Stub — list management is no-op.
 */
public class ListActivity extends Activity {
    private Object mAdapter;
    private ListView mListView = new ListView();
    private int mSelectedPosition = -1;

    public void setListAdapter(Object adapter) {
        mAdapter = adapter;
    }

    public Object getListAdapter() {
        return mAdapter;
    }

    public ListView getListView() {
        return mListView;
    }

    public void setSelection(int position) {
        mSelectedPosition = position;
    }

    public int getSelectedItemPosition() {
        return mSelectedPosition;
    }

    protected void onListItemClick(ListView l, android.view.View v, int position, long id) {}
}
