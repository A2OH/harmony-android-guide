package android.media;

/**
 * Android-compatible MediaDescription shim. Stub for media item metadata.
 * android.net.Uri is not yet shimmed; getMediaUri() returns Object.
 */
public class MediaDescription {
    private final String mMediaId;
    private final CharSequence mTitle;
    private final CharSequence mSubtitle;
    private final CharSequence mDescription;
    private final Object mIconBitmap;  // android.graphics.Bitmap not in shim yet
    private final Object mMediaUri;    // android.net.Uri not in shim yet

    private MediaDescription(Builder b) {
        mMediaId    = b.mMediaId;
        mTitle      = b.mTitle;
        mSubtitle   = b.mSubtitle;
        mDescription = b.mDescription;
        mIconBitmap = b.mIconBitmap;
        mMediaUri   = b.mMediaUri;
    }

    public String getMediaId()          { return mMediaId; }
    public CharSequence getTitle()      { return mTitle; }
    public CharSequence getSubtitle()   { return mSubtitle; }
    public CharSequence getDescription(){ return mDescription; }
    public Object getIconBitmap()       { return mIconBitmap; }
    /** Returns the media URI as Object (android.net.Uri not yet shimmed). */
    public Object getMediaUri()         { return mMediaUri; }

    @Override
    public String toString() {
        return "MediaDescription{id=" + mMediaId + ", title=" + mTitle + "}";
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static class Builder {
        private String mMediaId;
        private CharSequence mTitle;
        private CharSequence mSubtitle;
        private CharSequence mDescription;
        private Object mIconBitmap;
        private Object mMediaUri;  // android.net.Uri not yet shimmed

        public Builder setMediaId(String mediaId) {
            mMediaId = mediaId;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            mSubtitle = subtitle;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            mDescription = description;
            return this;
        }

        public Builder setIconBitmap(Object bitmap) {
            mIconBitmap = bitmap;
            return this;
        }

        /** Accepts android.net.Uri as Object (not yet shimmed). */
        public Builder setMediaUri(Object mediaUri) {
            mMediaUri = mediaUri;
            return this;
        }

        public MediaDescription build() {
            return new MediaDescription(this);
        }
    }
}
