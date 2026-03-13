package android.media;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Android-compatible MediaExtractor shim. Stub — returns empty data.
 */
public class MediaExtractor {

    public static final int SEEK_TO_PREVIOUS_SYNC = 0;
    public static final int SEEK_TO_NEXT_SYNC     = 1;
    public static final int SEEK_TO_CLOSEST_SYNC  = 2;

    public static final int SAMPLE_FLAG_SYNC       = 1;
    public static final int SAMPLE_FLAG_ENCRYPTED  = 2;
    public static final int SAMPLE_FLAG_PARTIAL_FRAME = 4;

    private String mDataSource;
    private int    mSelectedTrack = -1;

    public MediaExtractor() {}

    // ---- data source ----

    public void setDataSource(String path) throws IOException {
        mDataSource = path;
    }

    public void setDataSource(String uri, java.util.Map<String, String> headers)
            throws IOException {
        mDataSource = uri;
    }

    // ---- track access ----

    public int getTrackCount() {
        return 0; // stub: no real demuxer
    }

    public MediaFormat getTrackFormat(int track) {
        return new MediaFormat();
    }

    public void selectTrack(int track) {
        mSelectedTrack = track;
    }

    public void unselectTrack(int track) {
        if (mSelectedTrack == track) mSelectedTrack = -1;
    }

    // ---- sample reading ----

    /**
     * Returns the number of bytes read, or -1 on end-of-stream.
     */
    public int readSampleData(ByteBuffer byteBuf, int offset) {
        return -1; // stub: EOS immediately
    }

    public long getSampleTime() {
        return -1L;
    }

    public int getSampleFlags() {
        return 0;
    }

    public int getSampleTrackIndex() {
        return mSelectedTrack;
    }

    /**
     * Advance to the next sample. Returns false at end-of-stream.
     */
    public boolean advance() {
        return false;
    }

    // ---- seek ----

    public void seekTo(long timeUs, int mode) {
        // stub — no-op
    }

    // ---- teardown ----

    public void release() {
        mDataSource = null;
    }
}
