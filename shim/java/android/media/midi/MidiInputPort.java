package android.media.midi;
import java.io.Closeable;

public final class MidiInputPort extends MidiReceiver implements Closeable {
    public MidiInputPort() {}

    public void close() {}
    public int getPortNumber() { return 0; }
    public void onSend(byte[] p0, int p1, int p2, long p3) {}
}
