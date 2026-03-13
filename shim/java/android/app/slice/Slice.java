package android.app.slice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible Slice shim. Stub — no-op on OpenHarmony.
 *
 * Slices are a UI templating framework introduced in Android P (API 28) that
 * allow apps to surface content in system surfaces like the launcher or
 * assistant. There is no direct OH equivalent; this shim preserves compile-time
 * compatibility only.
 *
 * OH migration note: Surface content via ArkUI widgets or service widgets
 * (@ohos.app.form.FormExtensionAbility) instead.
 */
public final class Slice {

    // ── Hint constants ─────────────────────────────────────────────────────────

    /** Hint that this content is a title. */
    public static final String HINT_TITLE = "title";

    /** Hint that this slice represents a list. */
    public static final String HINT_LIST = "list";

    /** Hint that this item is a list item. */
    public static final String HINT_LIST_ITEM = "list_item";

    /** Hint that this slice contains actions. */
    public static final String HINT_ACTIONS = "actions";

    // ── Fields ─────────────────────────────────────────────────────────────────

    private final Object mUri;
    private final List<Object> mItems;
    private final List<String> mHints;

    private Slice(Builder builder) {
        mUri   = builder.mUri;
        mItems = Collections.unmodifiableList(new ArrayList<>(builder.mItems));
        mHints = Collections.unmodifiableList(new ArrayList<>(builder.mHints));
    }

    public Object getUri()        { return mUri; }
    public List<Object> getItems(){ return mItems; }
    public List<String> getHints(){ return mHints; }

    // ── Builder ────────────────────────────────────────────────────────────────

    public static final class Builder {

        private final Object mUri;
        private final List<Object> mItems = new ArrayList<>();
        private final List<String> mHints = new ArrayList<>();

        /**
         * Start building a Slice for the given URI.
         *
         * @param uri the content URI for this slice (typed as Object for shim compatibility)
         */
        public Builder(Object uri) {
            mUri = uri;
        }

        /**
         * Add an action item to this slice.
         *
         * @param action  the pending action (Object — PendingIntent or similar)
         * @param subSlice a Slice describing the action's UI
         * @param hints   optional hints for this item
         */
        public Builder addAction(Object action, Slice subSlice, String... hints) {
            mItems.add(action);
            if (subSlice != null) mItems.add(subSlice);
            return this;
        }

        /**
         * Add a sub-slice to this slice.
         */
        public Builder addSubSlice(Slice subSlice, String... hints) {
            if (subSlice != null) mItems.add(subSlice);
            return this;
        }

        /**
         * Add a text item.
         */
        public Builder addText(CharSequence text, String subType, String... hints) {
            if (text != null) mItems.add(text);
            return this;
        }

        /**
         * Add an icon item (typed as Object — Icon or Drawable on real Android).
         */
        public Builder addIcon(Object icon, String subType, String... hints) {
            if (icon != null) mItems.add(icon);
            return this;
        }

        /**
         * Add hints to the slice itself (not to a specific item).
         */
        public Builder addHints(String... hints) {
            if (hints != null) mHints.addAll(Arrays.asList(hints));
            return this;
        }

        /** Build the Slice. */
        public Slice build() {
            return new Slice(this);
        }
    }
}
