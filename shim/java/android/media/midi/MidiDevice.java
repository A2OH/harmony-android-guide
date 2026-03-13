package android.media.midi;
import java.io.Closeable;

public final class MidiDevice implements Closeable {
    public MidiDevice() {}

    public void close() {}
    public Object connectPorts(MidiInputPort p0, int p1) { return null; }
    public MidiDeviceInfo getInfo() { return null; }
    public MidiInputPort openInputPort(int p0) { return null; }
    public MidiOutputPort openOutputPort(int p0) { return null; }
}
