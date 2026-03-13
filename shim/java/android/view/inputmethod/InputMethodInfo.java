package android.view.inputmethod;

/**
 * Android-compatible InputMethodInfo shim.
 * Information about an input method available on the system.
 */
public class InputMethodInfo {
    private String mId;
    private String mPackageName;
    private String mServiceName;
    private String mLabel;

    public InputMethodInfo(String packageName, String className) {
        this(packageName, className, null, null);
    }

    public InputMethodInfo(String packageName, String className, String label, String settingsActivity) {
        mPackageName = packageName;
        mServiceName = className;
        mId = packageName + "/" + className;
        mLabel = label != null ? label : "";
    }

    public String getId() {
        return mId;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getServiceName() {
        return mServiceName;
    }

    public CharSequence loadLabel() {
        return mLabel;
    }
}
