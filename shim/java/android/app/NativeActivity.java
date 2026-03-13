package android.app;

/**
 * Android-compatible NativeActivity shim.
 *
 * NativeActivity is a convenience class that allows an application to be
 * implemented entirely in native (C/C++) code. In practice it is a thin Java
 * wrapper that delegates all lifecycle events to a native shared library via JNI.
 *
 * In the shim layer there is no JNI bridge to native code, so this class is a
 * pure stub that simply inherits the Activity lifecycle. The two public metadata
 * constants are preserved so that AndroidManifest.xml references compile without
 * modification.
 *
 * Note: On OpenHarmony, native-only applications should use the NAPI / XComponent
 * path instead of NativeActivity.
 */
public class NativeActivity extends Activity {

    /**
     * Manifest meta-data tag specifying the name of the native shared library
     * that implements the NativeActivity glue (default: {@code "main"}).
     * Value: {@value}
     */
    public static final String META_DATA_LIB_NAME = "android.app.lib_name";

    /**
     * Manifest meta-data tag specifying the name of the entry-point function
     * exported by the native library. If not present the default
     * {@code ANativeActivity_onCreate} is used.
     * Value: {@value}
     */
    public static final String META_DATA_FUNC_NAME = "android.app.func_name";

    // No additional members — everything is inherited from Activity.
}
