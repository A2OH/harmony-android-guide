package android.media;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Android-compatible MediaMuxer shim. Stub — no actual muxing.
 */
public class MediaMuxer {

    // ---- OutputFormat constants ----

    public static final class OutputFormat {
        private OutputFormat() {}

        public static final int MUXER_OUTPUT_MPEG_4   = 0;
        public static final int MUXER_OUTPUT_WEBM     = 1;
        public static final int MUXER_OUTPUT_3GPP     = 2;
        public static final int MUXER_OUTPUT_HEIF     = 3;
        public static final int MUXER_OUTPUT_OGG      = 4;
    }

    private final String mPath;
    private final int    mFormat;
    private boolean mStarted;
    private int     mNextTrackIndex;

    /**
     * @param path   output file path
     * @param format one of {@link OutputFormat} constants
     */
    public MediaMuxer(String path, int format) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IOException("MediaMuxer: invalid output path");
        }
        mPath   = path;
        mFormat = format;
    }

    // ---- track management ----

    /**
     * Adds a track and returns a track index to use with {@link #writeSampleData}.
     */
    public int addTrack(MediaFormat format) {
        if (mStarted) {
            throw new IllegalStateException("addTrack called after start()");
        }
        return mNextTrackIndex++;
    }

    // ---- lifecycle ----

    public void start() {
        if (mStarted) {
            throw new IllegalStateException("MediaMuxer already started");
        }
        mStarted = true;
    }

    public void stop() {
        if (!mStarted) {
            throw new IllegalStateException("MediaMuxer not started");
        }
        mStarted = false;
    }

    // ---- writing ----

    public void writeSampleData(int trackIndex, ByteBuffer byteBuf,
                                 MediaCodec.BufferInfo bufferInfo) {
        if (!mStarted) {
            throw new IllegalStateException("writeSampleData called before start()");
        }
        // stub — no-op
    }

    // ---- teardown ----

    public void release() {
        mStarted = false;
    }

    public String getOutputPath() { return mPath; }
    public int    getOutputFormat() { return mFormat; }
}
