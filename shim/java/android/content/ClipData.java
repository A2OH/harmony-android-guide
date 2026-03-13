package android.content;

import android.content.ContentResolver;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.content.ClipData — pure Java implementation.
 * Tier 1 — direct concept mapping; clipboard data storage needs no OH API.
 *
 * Holds one or more {@link Item} objects (each carrying text or a URI) plus a
 * {@link ClipDescription} describing the content type.
 */
public class ClipData {

    // ── Inner class: Item ────────────────────────────────────────────────────

    /** A single element inside a ClipData — either plain text or a URI. */
    public static class Item {
        private final CharSequence mText;
        private final Uri mUri;

        /** Construct a text item. */
        public Item(CharSequence text) {
            mText = text;
            mUri  = null;
        }

        /** Construct a URI item. */
        public Item(Uri uri) {
            mText = null;
            mUri  = uri;
        }

        /** Construct an item with both text and a URI. */
        public Item(CharSequence text, Uri uri) {
            mText = text;
            mUri  = uri;
        }

        /** Returns the text held by this item, or null. */
        public CharSequence getText() {
            return mText;
        }

        /** Returns the URI held by this item, or null. */
        public Uri getUri() {
            return mUri;
        }

        @Override
        public String toString() {
            if (mText != null && mUri != null) {
                return "Item{text=" + mText + ", uri=" + mUri + "}";
            } else if (mText != null) {
                return "Item{text=" + mText + "}";
            } else {
                return "Item{uri=" + mUri + "}";
            }
        }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final ClipDescription mDescription;
    private final List<Item>      mItems;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Create a ClipData with an explicit description and a single initial item.
     *
     * @param description the ClipDescription (label + MIME types)
     * @param item        the first Item
     */
    public ClipData(ClipDescription description, Item item) {
        mDescription = description;
        mItems = new ArrayList<>();
        mItems.add(item);
    }

    // ── Factory methods ───────────────────────────────────────────────────────

    /**
     * Creates a ClipData holding a single plain-text item.
     *
     * @param label human-readable label for the clip
     * @param text  the text to copy
     */
    public static ClipData newPlainText(CharSequence label, CharSequence text) {
        ClipDescription desc = new ClipDescription(label,
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN});
        return new ClipData(desc, new Item(text));
    }

    /**
     * Creates a ClipData holding a single URI item.
     *
     * @param resolver ignored in this shim (present for API compatibility)
     * @param label    human-readable label for the clip
     * @param uri      the URI to copy
     */
    public static ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri) {
        // Derive a MIME type hint from the URI scheme; fall back to text/plain.
        String mime = (uri != null && "content".equals(uri.getScheme()))
                ? "application/octet-stream"
                : ClipDescription.MIMETYPE_TEXT_PLAIN;
        ClipDescription desc = new ClipDescription(label, new String[]{mime});
        return new ClipData(desc, new Item(uri));
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Returns the ClipDescription (label + MIME types) for this clip. */
    public ClipDescription getDescription() {
        return mDescription;
    }

    /** Returns the number of Items in this clip. */
    public int getItemCount() {
        return mItems.size();
    }

    /** Returns the Item at the given index. */
    public Item getItemAt(int index) {
        return mItems.get(index);
    }

    /** Appends an additional Item to this clip. */
    public void addItem(Item item) {
        mItems.add(item);
    }

    @Override
    public String toString() {
        return "ClipData{description=" + mDescription + ", items=" + mItems + "}";
    }
}
