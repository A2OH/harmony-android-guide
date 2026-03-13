package android.media;
import android.os.Handler;

public class JetPlayer {
    public JetPlayer() {}

    public boolean clearQueue() { return false; }
    public Object clone() { return null; }
    public boolean closeJetFile() { return false; }
    public void finalize() {}
    public static JetPlayer getJetPlayer() { return null; }
    public static int getMaxTracks() { return 0; }
    public boolean loadJetFile(String p0) { return false; }
    public boolean pause() { return false; }
    public boolean play() { return false; }
    public boolean queueJetSegment(int p0, int p1, int p2, int p3, int p4, byte p5) { return false; }
    public boolean queueJetSegmentMuteArray(int p0, int p1, int p2, int p3, boolean[] p4, byte p5) { return false; }
    public void release() {}
    public void setEventListener(Object p0) {}
    public void setEventListener(Object p0, Handler p1) {}
    public boolean setMuteArray(boolean[] p0, boolean p1) { return false; }
    public boolean setMuteFlag(int p0, boolean p1, boolean p2) { return false; }
    public boolean setMuteFlags(int p0, boolean p1) { return false; }
    public boolean triggerClip(int p0) { return false; }
}
