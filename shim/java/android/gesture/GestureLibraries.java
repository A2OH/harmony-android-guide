package android.gesture;

import java.io.File;

/**
 * Android-compatible GestureLibraries factory shim.
 * All factory methods return a no-op GestureLibrary instance.
 */
public final class GestureLibraries {

    // Prevent instantiation
    private GestureLibraries() {}

    /**
     * Create a GestureLibrary backed by a file on disk.
     *
     * @param path path to the gesture store file
     */
    public static GestureLibrary fromFile(String path) {
        return new GestureLibrary.GestureStore();
    }

    /**
     * Create a GestureLibrary backed by a File.
     *
     * @param file the gesture store file
     */
    public static GestureLibrary fromFile(File file) {
        return new GestureLibrary.GestureStore();
    }

    /**
     * Create a GestureLibrary backed by a raw resource.
     *
     * @param context  application context (Object to avoid Context dependency)
     * @param resourceId resource identifier of the gesture file
     */
    public static GestureLibrary fromRawResource(Object context, int resourceId) {
        return new GestureLibrary.GestureStore();
    }

    /**
     * Create a GestureLibrary backed by a private file in the application's
     * data directory.
     *
     * @param context  application context (Object to avoid Context dependency)
     * @param name     name of the gesture store file
     */
    public static GestureLibrary fromPrivateFile(Object context, String name) {
        return new GestureLibrary.GestureStore();
    }
}
