package android.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.content.ClipDescription — pure Java implementation.
 * Tier 1 — direct concept mapping; no OH API needed for metadata storage.
 *
 * Describes the content on the clipboard: label and MIME types.
 */
public class ClipDescription implements android.os.Parcelable {
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_TEXT_HTML  = "text/html";

    private final CharSequence mLabel;
    private final List<String> mMimeTypes;

    public ClipDescription(CharSequence label, String[] mimeTypes) {
        mLabel = label;
        mMimeTypes = new ArrayList<>();
        if (mimeTypes != null) {
            for (String mt : mimeTypes) {
                mMimeTypes.add(mt);
            }
        }
    }

    /** Returns the user-visible label for this clip. */
    public CharSequence getLabel() {
        return mLabel;
    }

    /** Returns the number of MIME types in this description. */
    public int getMimeTypeCount() {
        return mMimeTypes.size();
    }

    /** Returns the MIME type at the given index. */
    public String getMimeType(int index) {
        return mMimeTypes.get(index);
    }

    /** Returns true if this description contains the given MIME type. */
    public boolean hasMimeType(String mimeType) {
        if (mimeType == null) return false;
        for (String mt : mMimeTypes) {
            if (mimeType.equals(mt)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ClipDescription{label=" + mLabel + ", mimeTypes=" + mMimeTypes + "}";
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {}

    public static final android.os.Parcelable.Creator<ClipDescription> CREATOR =
            new android.os.Parcelable.Creator<ClipDescription>() {
                public ClipDescription createFromParcel(android.os.Parcel in) { return null; }
                public ClipDescription[] newArray(int size) { return new ClipDescription[size]; }
            };
}
