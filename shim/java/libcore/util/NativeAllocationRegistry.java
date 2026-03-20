package libcore.util;

/**
 * Stub: NativeAllocationRegistry — registers native allocations for GC tracking.
 * No-op in our shim; native memory is managed by OHBridge.
 */
public class NativeAllocationRegistry {

    public NativeAllocationRegistry(ClassLoader classLoader, long freeFunction, long size) {}

    public static NativeAllocationRegistry createMalloced(ClassLoader classLoader, long freeFunction, long size) {
        return new NativeAllocationRegistry(classLoader, freeFunction, size);
    }

    public static NativeAllocationRegistry createMalloced(ClassLoader classLoader, long freeFunction) {
        return new NativeAllocationRegistry(classLoader, freeFunction, 0);
    }

    public static NativeAllocationRegistry createNonmalloced(ClassLoader classLoader, long freeFunction, long size) {
        return new NativeAllocationRegistry(classLoader, freeFunction, size);
    }

    public Runnable registerNativeAllocation(Object referent, long nativePtr) {
        return new Runnable() { public void run() {} };
    }
}
