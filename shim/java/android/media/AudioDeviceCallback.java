package android.media;

/**
 * Android-compatible AudioDeviceCallback shim. Stub for audio device change notifications.
 */
public class AudioDeviceCallback {

    public AudioDeviceCallback() {}

    public void onAudioDevicesAdded(Object[] addedDevices) {}

    public void onAudioDevicesRemoved(Object[] removedDevices) {}
}
