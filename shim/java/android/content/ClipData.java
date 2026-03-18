package android.content;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ClipData implements Parcelable {

    private final ClipDescription mDescription;
    private final List<Item> mItems;

    /** Inner class representing a single item on the clipboard. */
    public static class Item {
        private final CharSequence mText;
        private final String mHtmlText;
        private final Intent mIntent;
        private final Uri mUri;

        public Item(CharSequence text) {
            this(text, null, null, null);
        }

        public Item(CharSequence text, String htmlText) {
            this(text, htmlText, null, null);
        }

        public Item(Intent intent) {
            this(null, null, intent, null);
        }

        public Item(Uri uri) {
            this(null, null, null, uri);
        }

        public Item(CharSequence text, String htmlText, Intent intent, Uri uri) {
            mText = text;
            mHtmlText = htmlText;
            mIntent = intent;
            mUri = uri;
        }

        public CharSequence getText() { return mText; }
        public String getHtmlText() { return mHtmlText; }
        public Intent getIntent() { return mIntent; }
        public Uri getUri() { return mUri; }
    }

    // ── Constructors ──

    public ClipData(CharSequence label, String[] mimeTypes, Item item) {
        mDescription = new ClipDescription(label, mimeTypes);
        mItems = new ArrayList<>();
        if (item != null) {
            mItems.add(item);
        }
    }

    public ClipData(ClipDescription description, Item item) {
        mDescription = description;
        mItems = new ArrayList<>();
        if (item != null) {
            mItems.add(item);
        }
    }

    /** Copy constructor. */
    public ClipData(ClipData other) {
        mDescription = other.mDescription;
        mItems = new ArrayList<>(other.mItems);
    }

    // Keep old Object-param constructors for binary compat
    public ClipData(CharSequence p0, String[] p1, Object p2) {
        this(p0, p1, (p2 instanceof Item) ? (Item) p2 : null);
    }
    public ClipData(ClipDescription p0, Object p1) {
        this(p0, (p1 instanceof Item) ? (Item) p1 : null);
    }

    // ── Factory methods ──

    public static ClipData newPlainText(CharSequence label, CharSequence text) {
        Item item = new Item(text);
        return new ClipData(label, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
    }

    public static ClipData newHtmlText(CharSequence label, CharSequence text, String htmlText) {
        Item item = new Item(text, htmlText);
        return new ClipData(label,
                new String[]{ClipDescription.MIMETYPE_TEXT_HTML}, item);
    }

    public static ClipData newIntent(CharSequence label, Intent intent) {
        Item item = new Item(intent);
        return new ClipData(label, new String[]{"text/vnd.android.intent"}, item);
    }

    public static ClipData newRawUri(CharSequence label, Uri uri) {
        Item item = new Item(uri);
        return new ClipData(label, new String[]{"application/vnd.android.uri"}, item);
    }

    public static ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri) {
        Item item = new Item(uri);
        return new ClipData(label, new String[]{"application/vnd.android.uri"}, item);
    }

    // ── Accessors ──

    public ClipDescription getDescription() { return mDescription; }

    public int getItemCount() { return mItems.size(); }

    public Item getItemAt(int index) { return mItems.get(index); }

    public void addItem(Item item) { mItems.add(item); }

    public void addItem(ContentResolver resolver, Item item) { mItems.add(item); }

    // Keep old Object-param overloads for binary compat
    public void addItem(Object p0) {
        if (p0 instanceof Item) mItems.add((Item) p0);
    }
    public void addItem(ContentResolver p0, Object p1) {
        if (p1 instanceof Item) mItems.add((Item) p1);
    }

    // ── Parcelable stubs ──

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}
    public void prepareToLeaveProcess(boolean leavingPackage) {}
}
