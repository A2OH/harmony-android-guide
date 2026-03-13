package android.media;

import java.io.IOException;
import java.util.Map;

/**
 * Android-compatible MediaMetadataRetriever shim. Stub — returns null/empty data.
 */
public class MediaMetadataRetriever {

    // ---- METADATA_KEY_* constants ----

    public static final int METADATA_KEY_ALBUM              = 1;
    public static final int METADATA_KEY_ALBUMARTIST        = 13;
    public static final int METADATA_KEY_ARTIST             = 2;
    public static final int METADATA_KEY_AUTHOR             = 3;
    public static final int METADATA_KEY_BITRATE            = 20;
    public static final int METADATA_KEY_CD_TRACK_NUMBER    = 0;
    public static final int METADATA_KEY_COMPILATION        = 15;
    public static final int METADATA_KEY_COMPOSER           = 4;
    public static final int METADATA_KEY_DATE               = 5;
    public static final int METADATA_KEY_DISC_NUMBER        = 14;
    public static final int METADATA_KEY_DURATION           = 9;
    public static final int METADATA_KEY_GENRE              = 6;
    public static final int METADATA_KEY_HAS_AUDIO          = 16;
    public static final int METADATA_KEY_HAS_VIDEO          = 17;
    public static final int METADATA_KEY_LOCATION           = 23;
    public static final int METADATA_KEY_MIMETYPE           = 12;
    public static final int METADATA_KEY_NUM_TRACKS         = 10;
    public static final int METADATA_KEY_TITLE              = 7;
    public static final int METADATA_KEY_VIDEO_HEIGHT       = 19;
    public static final int METADATA_KEY_VIDEO_ROTATION     = 24;
    public static final int METADATA_KEY_VIDEO_WIDTH        = 18;
    public static final int METADATA_KEY_WRITER             = 11;
    public static final int METADATA_KEY_YEAR               = 8;
    public static final int METADATA_KEY_CAPTURE_FRAMERATE  = 25;
    public static final int METADATA_KEY_IMAGE_COUNT        = 26;
    public static final int METADATA_KEY_IMAGE_PRIMARY      = 27;
    public static final int METADATA_KEY_IMAGE_WIDTH        = 28;
    public static final int METADATA_KEY_IMAGE_HEIGHT       = 29;
    public static final int METADATA_KEY_IMAGE_ROTATION     = 30;
    public static final int METADATA_KEY_VIDEO_FRAME_COUNT  = 32;
    public static final int METADATA_KEY_EXIF_OFFSET        = 33;
    public static final int METADATA_KEY_EXIF_LENGTH        = 34;
    public static final int METADATA_KEY_COLOR_STANDARD     = 35;
    public static final int METADATA_KEY_COLOR_TRANSFER     = 36;
    public static final int METADATA_KEY_COLOR_RANGE        = 37;
    public static final int METADATA_KEY_SAMPLERATE         = 38;
    public static final int METADATA_KEY_BITS_PER_SAMPLE    = 39;

    // ---- getFrameAtTime option constants ----

    public static final int OPTION_PREVIOUS_SYNC = 0x00;
    public static final int OPTION_NEXT_SYNC     = 0x01;
    public static final int OPTION_CLOSEST_SYNC  = 0x02;
    public static final int OPTION_CLOSEST       = 0x03;

    private String mDataSource;

    public MediaMetadataRetriever() {}

    // ---- data source ----

    public void setDataSource(String path) throws IllegalArgumentException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("MediaMetadataRetriever: empty path");
        }
        mDataSource = path;
    }

    public void setDataSource(String uri, java.util.Map<String, String> headers)
            throws IllegalArgumentException {
        mDataSource = uri;
    }

    // ---- metadata extraction ----

    /**
     * Returns the metadata value for the given key, or null if unavailable.
     */
    public String extractMetadata(int keyCode) {
        return null; // stub: no real parser
    }

    // ---- frame extraction ----

    /**
     * Returns a frame at the given time as a raw ARGB bitmap byte array, or null.
     * In this stub we always return null.
     */
    public byte[] getFrameAtTime(long timeUs, int option) {
        return null;
    }

    public byte[] getFrameAtTime(long timeUs) {
        return getFrameAtTime(timeUs, OPTION_CLOSEST_SYNC);
    }

    public byte[] getFrameAtTime() {
        return getFrameAtTime(-1L, OPTION_CLOSEST_SYNC);
    }

    /** Returns the embedded album art, or null. */
    public byte[] getEmbeddedPicture() {
        return null;
    }

    // ---- teardown ----

    public void release() {
        mDataSource = null;
    }
}
