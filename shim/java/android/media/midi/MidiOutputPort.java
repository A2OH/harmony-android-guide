package android.media.midi;
import java.io.Closeable;

public final class MidiOutputPort extends MidiSender implements Closeable {
    public MidiOutputPort() {}

    public void close() {}
    public int getPortNumber() { return 0; }
    public void onConnect(MidiReceiver p0) {}
    public void onDisconnect(MidiReceiver p0) {}
}
