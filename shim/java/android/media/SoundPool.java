package android.media;
import android.content.Context;
import java.io.FileDescriptor;

public class SoundPool {
    public SoundPool() {}

    public void autoPause() {}
    public void autoResume() {}
    public void finalize() {}
    public int load(String p0, int p1) { return 0; }
    public int load(Context p0, int p1, int p2) { return 0; }
    public int load(FileDescriptor p0, long p1, long p2, int p3) { return 0; }
    public void pause(int p0) {}
    public int play(int p0, float p1, float p2, int p3, int p4, float p5) { return 0; }
    public void release() {}
    public void resume(int p0) {}
    public void setLoop(int p0, int p1) {}
    public void setOnLoadCompleteListener(Object p0) {}
    public void setPriority(int p0, int p1) {}
    public void setRate(int p0, float p1) {}
    public void setVolume(int p0, float p1, float p2) {}
    public void stop(int p0) {}
    public boolean unload(int p0) { return false; }
}
