package android.media;
import android.os.Handler;
import android.view.Surface;
import java.nio.ByteBuffer;

public final class MediaSync {
    public static final int MEDIASYNC_ERROR_AUDIOTRACK_FAIL = 0;
    public static final int MEDIASYNC_ERROR_SURFACE_FAIL = 0;

    public MediaSync() {}

    public void finalize() {}
    public void flush() {}
    public void queueAudio(ByteBuffer p0, int p1, long p2) {}
    public void release() {}
    public void setAudioTrack(AudioTrack p0) {}
    public void setCallback(Object p0, Handler p1) {}
    public void setOnErrorListener(Object p0, Handler p1) {}
    public void setPlaybackParams(PlaybackParams p0) {}
    public void setSurface(Surface p0) {}
    public void setSyncParams(SyncParams p0) {}
    public void onAudioBufferConsumed(MediaSync p0, ByteBuffer p1, int p2) {}
}
