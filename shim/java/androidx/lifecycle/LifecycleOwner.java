package androidx.lifecycle;

/**
 * Stub LifecycleOwner — real one in compose.dex.
 * Our Activity implements this so ComponentActivity can extend it.
 */
public interface LifecycleOwner {
    Lifecycle getLifecycle();
}
