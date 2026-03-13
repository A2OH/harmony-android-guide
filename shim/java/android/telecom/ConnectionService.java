package android.telecom;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Collection;

public class ConnectionService extends Service {
    public static final int SERVICE_INTERFACE = 0;

    public ConnectionService() {}

    public void addConference(Conference p0) {}
    public void addExistingConnection(PhoneAccountHandle p0, Connection p1) {}
    public void conferenceRemoteConnections(RemoteConnection p0, RemoteConnection p1) {}
    public void connectionServiceFocusReleased() {}
    public RemoteConnection createRemoteIncomingConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public RemoteConnection createRemoteOutgoingConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public Collection<?> getAllConferences() { return null; }
    public Collection<?> getAllConnections() { return null; }
    public IBinder onBind(Intent p0) { return null; }
    public void onConference(Connection p0, Connection p1) {}
    public void onConnectionServiceFocusGained() {}
    public void onConnectionServiceFocusLost() {}
    public Connection onCreateIncomingConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle p0, ConnectionRequest p1) {}
    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public Connection onCreateOutgoingConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle p0, ConnectionRequest p1) {}
    public Connection onCreateOutgoingHandoverConnection(PhoneAccountHandle p0, ConnectionRequest p1) { return null; }
    public void onHandoverFailed(ConnectionRequest p0, int p1) {}
    public void onRemoteConferenceAdded(RemoteConference p0) {}
    public void onRemoteExistingConnectionAdded(RemoteConnection p0) {}
}
