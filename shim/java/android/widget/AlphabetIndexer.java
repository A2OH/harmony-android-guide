package android.widget;
import android.database.Cursor;
import android.database.DataSetObserver;

public class AlphabetIndexer extends DataSetObserver implements SectionIndexer {
    public int mAlphabet = 0;
    public int mColumnIndex = 0;
    public int mDataCursor = 0;

    public AlphabetIndexer(Cursor p0, int p1, CharSequence p2) {}

    public int compare(String p0, String p1) { return 0; }
    public int getPositionForSection(int p0) { return 0; }
    public int getSectionForPosition(int p0) { return 0; }
    public Object[] getSections() { return null; }
    public void setCursor(Cursor p0) {}
}
