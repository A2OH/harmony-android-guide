package android.telecom;

public final class RemoteConnection {
    public RemoteConnection() {}

    public void abort() {}
    public void answer() {}
    public void disconnect() {}
    public Object getAddress() { return null; }
    public int getAddressPresentation() { return 0; }
    public Object getCallerDisplayName() { return null; }
    public int getCallerDisplayNamePresentation() { return 0; }
    public Object getConference() { return null; }
    public Object getConferenceableConnections() { return null; }
    public int getConnectionCapabilities() { return 0; }
    public int getConnectionProperties() { return 0; }
    public Object getDisconnectCause() { return null; }
    public Object getExtras() { return null; }
    public int getState() { return 0; }
    public Object getStatusHints() { return null; }
    public Object getVideoProvider() { return null; }
    public int getVideoState() { return 0; }
    public void hold() {}
    public boolean isRingbackRequested() { return false; }
    public boolean isVoipAudioMode() { return false; }
    public void playDtmfTone(Object p0) {}
    public void postDialContinue(Object p0) {}
    public void pullExternalCall() {}
    public void registerCallback(Object p0) {}
    public void registerCallback(Object p0, Object p1) {}
    public void reject() {}
    public void setCallAudioState(Object p0) {}
    public void stopDtmfTone() {}
    public void unhold() {}
    public void unregisterCallback(Object p0) {}
    public void onAddressChanged(Object p0, Object p1, Object p2) {}
    public void onCallerDisplayNameChanged(Object p0, Object p1, Object p2) {}
    public void onConferenceChanged(Object p0, Object p1) {}
    public void onConferenceableConnectionsChanged(Object p0, Object p1) {}
    public void onConnectionCapabilitiesChanged(Object p0, Object p1) {}
    public void onConnectionEvent(Object p0, Object p1, Object p2) {}
    public void onConnectionPropertiesChanged(Object p0, Object p1) {}
    public void onDestroyed(Object p0) {}
    public void onDisconnected(Object p0, Object p1) {}
    public void onExtrasChanged(Object p0, Object p1) {}
    public void onPostDialChar(Object p0, Object p1) {}
    public void onPostDialWait(Object p0, Object p1) {}
    public void onRingbackRequested(Object p0, Object p1) {}
    public void onStateChanged(Object p0, Object p1) {}
    public void onStatusHintsChanged(Object p0, Object p1) {}
    public void onVideoProviderChanged(Object p0, Object p1) {}
    public void onVideoStateChanged(Object p0, Object p1) {}
    public void onVoipAudioChanged(Object p0, Object p1) {}
}
