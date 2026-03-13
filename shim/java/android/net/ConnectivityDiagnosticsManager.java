package android.net;
import java.util.concurrent.Executor;

public class ConnectivityDiagnosticsManager {

    public void registerConnectivityDiagnosticsCallback(NetworkRequest p0, Executor p1, Object p2) {}
    public void unregisterConnectivityDiagnosticsCallback(Object p0) {}
    public void onConnectivityReportAvailable(Object p0) {}
    public void onDataStallSuspected(Object p0) {}
    public void onNetworkConnectivityReported(Network p0, boolean p1) {}
}
