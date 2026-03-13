package android.net.rtp;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AudioGroup shim for A2OH migration.
 * Represents a mixing group for AudioStream instances.
 * All mixing operations are no-ops.
 */
public class AudioGroup {

    public static final int MODE_ON_HOLD          = 0;
    public static final int MODE_MUTED            = 1;
    public static final int MODE_NORMAL           = 2;
    public static final int MODE_ECHO_SUPPRESSION = 3;

    private int                  mMode    = MODE_NORMAL;
    private final List<AudioStream> mStreams = new ArrayList<AudioStream>();

    public AudioGroup() {}

    public int getMode() { return mMode; }

    public void setMode(int mode) {
        mMode = mode;
    }

    /** Returns a snapshot of the streams currently in this group. */
    public AudioStream[] getStreams() {
        return mStreams.toArray(new AudioStream[0]);
    }

    /** Removes all streams from this group. No-op on each stream in this stub. */
    public void clear() {
        mStreams.clear();
    }

    /** Sends a DTMF digit to all streams in this group. No-op in this stub. */
    public void sendDtmf(int event) {}

    // Package-private: called by AudioStream.join()
    void addStream(AudioStream stream) {
        if (!mStreams.contains(stream)) {
            mStreams.add(stream);
        }
    }

    // Package-private: called by AudioStream when it leaves the group
    void removeStream(AudioStream stream) {
        mStreams.remove(stream);
    }
}
