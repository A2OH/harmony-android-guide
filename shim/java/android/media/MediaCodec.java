package android.media;

import java.nio.ByteBuffer;

/**
 * Android-compatible MediaCodec shim. Stub — no actual codec processing.
 */
public class MediaCodec {

    // ---- BufferInfo inner class ----

    public static class BufferInfo {
        public int  offset;
        public int  size;
        public long presentationTimeUs;
        public int  flags;

        public void set(int offset, int size, long presentationTimeUs, int flags) {
            this.offset              = offset;
            this.size                = size;
            this.presentationTimeUs  = presentationTimeUs;
            this.flags               = flags;
        }
    }

    // ---- Callback abstract inner class ----

    public static abstract class Callback {
        public abstract void onInputBufferAvailable(MediaCodec codec, int index);
        public abstract void onOutputBufferAvailable(MediaCodec codec, int index, BufferInfo info);
        public abstract void onError(MediaCodec codec, CodecException e);
        public abstract void onOutputFormatChanged(MediaCodec codec, MediaFormat format);
    }

    // ---- CodecException (simple checked→unchecked wrapper) ----

    public static class CodecException extends RuntimeException {
        public CodecException(String message) { super(message); }
    }

    // ---- BUFFER_FLAG_* constants ----

    public static final int BUFFER_FLAG_KEY_FRAME   = 1;
    public static final int BUFFER_FLAG_CODEC_CONFIG = 2;
    public static final int BUFFER_FLAG_END_OF_STREAM = 4;
    public static final int BUFFER_FLAG_PARTIAL_FRAME = 8;

    // ---- INFO_* constants returned by dequeueOutputBuffer ----

    public static final int INFO_TRY_AGAIN_LATER     = -1;
    public static final int INFO_OUTPUT_FORMAT_CHANGED = -2;
    public static final int INFO_OUTPUT_BUFFERS_CHANGED = -3;

    // ---- CONFIGURE_FLAG_* ----

    public static final int CONFIGURE_FLAG_ENCODE = 1;

    // ---- private state ----

    private final String mName;
    private static final int BUFFER_COUNT = 4;
    private final ByteBuffer[] mInputBuffers  = new ByteBuffer[BUFFER_COUNT];
    private final ByteBuffer[] mOutputBuffers = new ByteBuffer[BUFFER_COUNT];

    private MediaCodec(String name) {
        mName = name;
        for (int i = 0; i < BUFFER_COUNT; i++) {
            mInputBuffers[i]  = ByteBuffer.allocate(65536);
            mOutputBuffers[i] = ByteBuffer.allocate(65536);
        }
    }

    // ---- static factory methods ----

    public static MediaCodec createDecoderByType(String type) {
        return new MediaCodec("decoder/" + type);
    }

    public static MediaCodec createEncoderByType(String type) {
        return new MediaCodec("encoder/" + type);
    }

    public static MediaCodec createByCodecName(String name) {
        return new MediaCodec(name);
    }

    // ---- lifecycle ----

    public void configure(MediaFormat format, Object surface, Object crypto, int flags) {
        // stub — no-op
    }

    public void setCallback(Callback callback) {
        // stub — no-op
    }

    public void start() {
        // stub — no-op
    }

    public void stop() {
        // stub — no-op
    }

    public void flush() {
        for (ByteBuffer buf : mInputBuffers)  buf.clear();
        for (ByteBuffer buf : mOutputBuffers) buf.clear();
    }

    public void release() {
        // stub — no-op
    }

    // ---- buffer access ----

    public ByteBuffer getInputBuffer(int index) {
        if (index < 0 || index >= BUFFER_COUNT) return null;
        mInputBuffers[index].clear();
        return mInputBuffers[index];
    }

    public ByteBuffer getOutputBuffer(int index) {
        if (index < 0 || index >= BUFFER_COUNT) return null;
        return mOutputBuffers[index];
    }

    // ---- queuing / dequeueing ----

    public int dequeueInputBuffer(long timeoutUs) {
        return 0; // always return buffer 0 as available
    }

    public int dequeueOutputBuffer(BufferInfo info, long timeoutUs) {
        if (info != null) {
            info.set(0, 0, 0L, BUFFER_FLAG_END_OF_STREAM);
        }
        return INFO_TRY_AGAIN_LATER;
    }

    public void queueInputBuffer(int index, int offset, int size,
                                  long presentationTimeUs, int flags) {
        // stub — no-op
    }

    public void releaseOutputBuffer(int index, boolean render) {
        // stub — no-op
    }

    public void releaseOutputBuffer(int index, long renderTimestampNs) {
        // stub — no-op
    }

    // ---- format ----

    public MediaFormat getOutputFormat() {
        return new MediaFormat();
    }

    public MediaFormat getInputFormat() {
        return new MediaFormat();
    }

    public String getName() { return mName; }
}
