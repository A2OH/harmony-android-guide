package android.media.midi;

public class MidiReceiver {
    public MidiReceiver() {}
    public MidiReceiver(int p0) {}

    public void flush() {}
    public int getMaxMessageSize() { return 0; }
    public void onFlush() {}
    public  void onSend(byte[] p0, int p1, int p2, long p3) { return; }
    public void send(byte[] p0, int p1, int p2) {}
    public void send(byte[] p0, int p1, int p2, long p3) {}
}
