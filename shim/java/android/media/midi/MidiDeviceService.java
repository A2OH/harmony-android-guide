package android.media.midi;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MidiDeviceService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public MidiDeviceService() {}

    public MidiDeviceInfo getDeviceInfo() { return null; }
    public MidiReceiver[] getOutputPortReceivers() { return null; }
    public IBinder onBind(Intent p0) { return null; }
    public void onClose() {}
    public void onDeviceStatusChanged(MidiDeviceStatus p0) {}
    public MidiReceiver[] onGetInputPortReceivers() { return null; }
}
