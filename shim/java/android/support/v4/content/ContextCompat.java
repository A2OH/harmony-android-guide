package android.support.v4.content;

import android.content.Context;

/**
 * Android-compatible ContextCompat shim (AndroidX/support-v4).
 * Provides static wrappers around Context methods with backward compatibility.
 */
public class ContextCompat {

    private ContextCompat() {}

    /** Returns a color associated with a resource ID. Stub returns the id as color. */
    public static int getColor(Context context, int id) {
        return id; // stub — real impl would look up resource
    }

    /** Returns a drawable for a resource ID. Stub returns null. */
    public static Object getDrawable(Context context, int id) {
        return null; // stub — real impl would look up resource
    }

    /** Check permission. Stub returns PERMISSION_GRANTED (0). */
    public static int checkSelfPermission(Context context, String permission) {
        return 0; // PackageManager.PERMISSION_GRANTED
    }

    /** Returns system service by name. Delegates to context.getSystemService(). */
    public static Object getSystemService(Context context, String name) {
        return context.getSystemService(name);
    }
}
