package android.media;
import java.util.concurrent.Executor;

public interface AudioRecordingMonitor {
    void registerAudioRecordingCallback(Executor p0, Object p1);
    void unregisterAudioRecordingCallback(Object p0);
}
