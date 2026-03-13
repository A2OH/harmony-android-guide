package android.service.voice;

public class AlwaysOnHotwordDetector {
    public AlwaysOnHotwordDetector() {}

    public static final int AUDIO_CAPABILITY_ECHO_CANCELLATION = 0;
    public static final int AUDIO_CAPABILITY_NOISE_SUPPRESSION = 0;
    public static final int MODEL_PARAM_THRESHOLD_FACTOR = 0;
    public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 0;
    public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 0;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_ECHO_CANCELLATION = 0;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_NOISE_SUPPRESSION = 0;
    public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 0;
    public static final int RECOGNITION_MODE_VOICE_TRIGGER = 0;
    public static final int STATE_HARDWARE_UNAVAILABLE = 0;
    public static final int STATE_KEYPHRASE_ENROLLED = 0;
    public static final int STATE_KEYPHRASE_UNENROLLED = 0;
    public Object createEnrollIntent() { return null; }
    public Object createReEnrollIntent() { return null; }
    public Object createUnEnrollIntent() { return null; }
    public int getParameter(Object p0) { return 0; }
    public int getSupportedAudioCapabilities() { return 0; }
    public int getSupportedRecognitionModes() { return 0; }
    public int setParameter(Object p0, Object p1) { return 0; }
    public boolean startRecognition(Object p0) { return false; }
    public boolean stopRecognition() { return false; }
    public void onAvailabilityChanged(Object p0) {}
    public void onDetected(Object p0) {}
    public void onError() {}
    public void onRecognitionPaused() {}
    public void onRecognitionResumed() {}
}
