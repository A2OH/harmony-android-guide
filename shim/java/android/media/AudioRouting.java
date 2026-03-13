package android.media;
import android.os.Handler;

public interface AudioRouting {
    void addOnRoutingChangedListener(Object p0, Handler p1);
    AudioDeviceInfo getPreferredDevice();
    AudioDeviceInfo getRoutedDevice();
    void removeOnRoutingChangedListener(Object p0);
    boolean setPreferredDevice(AudioDeviceInfo p0);
}
