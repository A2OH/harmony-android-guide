package android.telecom;
import android.os.Bundle;
import android.os.Handler;
import java.util.List;

public final class RemoteConference {

    public void disconnect() {}
    public List<?> getConferenceableConnections() { return null; }
    public int getConnectionCapabilities() { return 0; }
    public int getConnectionProperties() { return 0; }
    public List<?> getConnections() { return null; }
    public DisconnectCause getDisconnectCause() { return null; }
    public Bundle getExtras() { return null; }
    public int getState() { return 0; }
    public void hold() {}
    public void merge() {}
    public void playDtmfTone(char p0) {}
    public void registerCallback(Object p0) {}
    public void registerCallback(Object p0, Handler p1) {}
    public void separate(RemoteConnection p0) {}
    public void setCallAudioState(CallAudioState p0) {}
    public void stopDtmfTone() {}
    public void swap() {}
    public void unhold() {}
    public void unregisterCallback(Object p0) {}
    public void onConferenceableConnectionsChanged(RemoteConference p0, java.util.List<Object> p1) {}
    public void onConnectionAdded(RemoteConference p0, RemoteConnection p1) {}
    public void onConnectionCapabilitiesChanged(RemoteConference p0, int p1) {}
    public void onConnectionPropertiesChanged(RemoteConference p0, int p1) {}
    public void onConnectionRemoved(RemoteConference p0, RemoteConnection p1) {}
    public void onDestroyed(RemoteConference p0) {}
    public void onDisconnected(RemoteConference p0, DisconnectCause p1) {}
    public void onExtrasChanged(RemoteConference p0, Bundle p1) {}
    public void onStateChanged(RemoteConference p0, int p1, int p2) {}
}
