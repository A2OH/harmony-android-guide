package android.os;

/**
 * Android-compatible IInterface shim. Base interface for Binder-exposed objects.
 */
public interface IInterface {
    IBinder asBinder();
}
