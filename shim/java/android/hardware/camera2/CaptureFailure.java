package android.hardware.camera2;

public class CaptureFailure {
    public static final int REASON_ERROR = 0;
    public static final int REASON_FLUSHED = 0;

    public CaptureFailure() {}

    public long getFrameNumber() { return 0L; }
    public int getReason() { return 0; }
    public int getSequenceId() { return 0; }
    public boolean wasImageCaptured() { return false; }
}
