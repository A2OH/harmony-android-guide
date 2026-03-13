package android.media;

import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible MediaMetadata shim. Stub for media metadata.
 */
public final class MediaMetadata {
    public static final String METADATA_KEY_TITLE          = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_ARTIST         = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_ALBUM          = "android.media.metadata.ALBUM";
    public static final String METADATA_KEY_AUTHOR         = "android.media.metadata.AUTHOR";
    public static final String METADATA_KEY_WRITER         = "android.media.metadata.WRITER";
    public static final String METADATA_KEY_COMPOSER       = "android.media.metadata.COMPOSER";
    public static final String METADATA_KEY_DATE           = "android.media.metadata.DATE";
    public static final String METADATA_KEY_YEAR           = "android.media.metadata.YEAR";
    public static final String METADATA_KEY_GENRE          = "android.media.metadata.GENRE";
    public static final String METADATA_KEY_TRACK_NUMBER   = "android.media.metadata.TRACK_NUMBER";
    public static final String METADATA_KEY_NUM_TRACKS     = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_DISC_NUMBER    = "android.media.metadata.DISC_NUMBER";
    public static final String METADATA_KEY_ALBUM_ARTIST   = "android.media.metadata.ALBUM_ARTIST";
    public static final String METADATA_KEY_ART            = "android.media.metadata.ART";
    public static final String METADATA_KEY_ART_URI        = "android.media.metadata.ART_URI";
    public static final String METADATA_KEY_ALBUM_ART      = "android.media.metadata.ALBUM_ART";
    public static final String METADATA_KEY_ALBUM_ART_URI  = "android.media.metadata.ALBUM_ART_URI";
    public static final String METADATA_KEY_USER_RATING    = "android.media.metadata.USER_RATING";
    public static final String METADATA_KEY_RATING         = "android.media.metadata.RATING";
    public static final String METADATA_KEY_DISPLAY_TITLE  = "android.media.metadata.DISPLAY_TITLE";
    public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
    public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
    public static final String METADATA_KEY_DISPLAY_ICON   = "android.media.metadata.DISPLAY_ICON";
    public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
    public static final String METADATA_KEY_MEDIA_ID       = "android.media.metadata.MEDIA_ID";
    public static final String METADATA_KEY_DURATION       = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_ADVERTISEMENT  = "android.media.metadata.ADVERTISEMENT";
    public static final String METADATA_KEY_DOWNLOAD_STATUS = "android.media.metadata.DOWNLOAD_STATUS";
    public static final String METADATA_KEY_MEDIA_URI      = "android.media.metadata.MEDIA_URI";

    private final Map<String, String>  mStrings;
    private final Map<String, Long>    mLongs;
    private final Map<String, Object>  mBitmaps;

    private MediaMetadata(Builder b) {
        mStrings = new HashMap<>(b.mStrings);
        mLongs   = new HashMap<>(b.mLongs);
        mBitmaps = new HashMap<>(b.mBitmaps);
    }

    public String getString(String key) {
        return mStrings.get(key);
    }

    public long getLong(String key) {
        Long v = mLongs.get(key);
        return v != null ? v : 0L;
    }

    /** Returns the bitmap stored under key, or null. Type is Object to avoid Bitmap dependency. */
    public Object getBitmap(String key) {
        return mBitmaps.get(key);
    }

    public CharSequence getText(String key) {
        return mStrings.get(key);
    }

    /** Returns a simple description object wrapping title/artist/album. */
    public MediaDescription getDescription() {
        return new MediaDescription(
                getString(METADATA_KEY_MEDIA_ID),
                getText(METADATA_KEY_TITLE),
                getText(METADATA_KEY_ARTIST),
                getText(METADATA_KEY_ALBUM));
    }

    public boolean containsKey(String key) {
        return mStrings.containsKey(key) || mLongs.containsKey(key) || mBitmaps.containsKey(key);
    }

    @Override
    public String toString() {
        return "MediaMetadata{title=" + getString(METADATA_KEY_TITLE)
                + ", artist=" + getString(METADATA_KEY_ARTIST) + "}";
    }

    // -----------------------------------------------------------------------
    /** Lightweight description returned by getDescription(). */
    public static final class MediaDescription {
        private final String      mMediaId;
        private final CharSequence mTitle;
        private final CharSequence mSubtitle;
        private final CharSequence mDescription;

        public MediaDescription(String mediaId, CharSequence title,
                CharSequence subtitle, CharSequence description) {
            mMediaId     = mediaId;
            mTitle       = title;
            mSubtitle    = subtitle;
            mDescription = description;
        }

        public String      getMediaId()    { return mMediaId; }
        public CharSequence getTitle()     { return mTitle; }
        public CharSequence getSubtitle()  { return mSubtitle; }
        public CharSequence getDescription() { return mDescription; }

        @Override
        public String toString() {
            return "MediaDescription{mediaId=" + mMediaId + ", title=" + mTitle + "}";
        }
    }

    // -----------------------------------------------------------------------
    public static final class Builder {
        final Map<String, String>  mStrings = new HashMap<>();
        final Map<String, Long>    mLongs   = new HashMap<>();
        final Map<String, Object>  mBitmaps = new HashMap<>();

        public Builder() {}

        public Builder putString(String key, String value) {
            if (value == null) { mStrings.remove(key); } else { mStrings.put(key, value); }
            return this;
        }

        public Builder putLong(String key, long value) {
            mLongs.put(key, value);
            return this;
        }

        public Builder putBitmap(String key, Object bitmap) {
            if (bitmap == null) { mBitmaps.remove(key); } else { mBitmaps.put(key, bitmap); }
            return this;
        }

        public Builder putText(String key, CharSequence value) {
            return putString(key, value == null ? null : value.toString());
        }

        public MediaMetadata build() {
            return new MediaMetadata(this);
        }
    }
}
