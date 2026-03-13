package android.text;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Shim: android.text.Annotation
 *
 * A span that attaches a key/value pair to a region of text. Typically
 * used by {@link android.text.Html} and text-styling tools to embed
 * arbitrary metadata (e.g. pronunciation hints, locale tags) directly in
 * a {@link Spanned} string.
 *
 * On Android, Annotation implements {@code android.os.Parcelable} via
 * {@link ParcelableSpan}. In this shim the Parcel IPC layer is absent, so
 * only the in-memory key/value contract is preserved; {@link #getSpanTypeId}
 * returns the same constant Android uses (17) for any code that inspects
 * span type numerically.
 *
 * Bridge delegation: pure Java — no OHBridge calls required.
 */
public class Annotation implements ParcelableSpan {

    // Android's TextUtils.ANNOTATION span-type constant (see TextUtils.java).
    private static final int SPAN_TYPE_ID = 17;

    private final String mKey;
    private final String mValue;

    // ── Constructor ───────────────────────────────────────────────────

    /**
     * Creates an Annotation with the given key/value pair.
     *
     * @param key   the annotation key; should not be {@code null}
     * @param value the annotation value; should not be {@code null}
     */
    public Annotation(String key, String value) {
        mKey   = key   != null ? key   : "";
        mValue = value != null ? value : "";
    }

    // ── Accessors ─────────────────────────────────────────────────────

    /**
     * Returns the annotation key.
     *
     * @return the key string; never {@code null}
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Returns the annotation value.
     *
     * @return the value string; never {@code null}
     */
    public String getValue() {
        return mValue;
    }

    // ── ParcelableSpan ────────────────────────────────────────────────

    /**
     * Returns the span-type identifier used when this span is parcelled.
     * Matches Android's {@code android.text.TextUtils.ANNOTATION} constant (17).
     *
     * @return 17
     */
    @Override
    public int getSpanTypeId() {
        return SPAN_TYPE_ID;
    }

    // ── Object overrides ──────────────────────────────────────────────

    @Override
    public String toString() {
        return "Annotation{key=" + mKey + ", value=" + mValue + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation other = (Annotation) o;
        return mKey.equals(other.mKey) && mValue.equals(other.mValue);
    }

    @Override
    public int hashCode() {
        return 31 * mKey.hashCode() + mValue.hashCode();
    }
}
