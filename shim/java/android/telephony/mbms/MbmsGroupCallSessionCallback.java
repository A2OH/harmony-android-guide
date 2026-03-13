package android.telephony.mbms;
import java.util.List;

public interface MbmsGroupCallSessionCallback {
    default void onAvailableSaisUpdated(java.util.List<Object> p0, java.util.List<Object> p1) {}
    default void onError(int p0, String p1) {}
    default void onMiddlewareReady() {}
    default void onServiceInterfaceAvailable(String p0, int p1) {}
}
