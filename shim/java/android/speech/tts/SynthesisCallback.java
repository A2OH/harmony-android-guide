package android.speech.tts;

public interface SynthesisCallback {
    int audioAvailable(byte[] p0, int p1, int p2);
    int done();
    void error();
    void error(int p0);
    int getMaxBufferSize();
    boolean hasFinished();
    boolean hasStarted();
    void rangeStart(int p0, int p1, int p2);
    int start(int p3);
}
