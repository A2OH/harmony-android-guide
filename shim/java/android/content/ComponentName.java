package android.content;

import java.util.Objects;

/**
 * Android-compatible ComponentName shim.
 *
 * Identifier for a specific application component: an Activity, Service,
 * BroadcastReceiver, or ContentProvider. Two pieces of information,
 * encapsulated here, are required to identify a component: the package
 * (a String) it exists in, and the class (a String) name inside of that
 * package.
 *
 * Pure Java stub — no OH bridge calls required.
 */
public final class ComponentName {

    private final String mPackage;
    private final String mClass;

    // ── Constructors ──

    /**
     * Create a new component identifier.
     * @param pkg   the name of the package that the component exists in
     * @param cls   the name of the class inside of the package
     */
    public ComponentName(String pkg, String cls) {
        if (pkg == null) throw new NullPointerException("package is null");
        if (cls == null) throw new NullPointerException("class is null");
        mPackage = pkg;
        mClass   = cls;
    }

    /**
     * Create a new component identifier from a context and a class.
     * @param ctx the context for the package
     * @param cls the class in that package
     */
    public ComponentName(Context ctx, Class<?> cls) {
        this(ctx.getPackageName(), cls.getName());
    }

    /**
     * Create a new component identifier from a context and a class name string.
     * @param ctx       the context for the package
     * @param className the full class name
     */
    public ComponentName(Context ctx, String className) {
        this(ctx.getPackageName(), className);
    }

    // ── Accessors ──

    /** @return the package name of this component */
    public String getPackageName() {
        return mPackage;
    }

    /** @return the class name of this component */
    public String getClassName() {
        return mClass;
    }

    /**
     * Return the class name, either fully qualified or in abbreviated form if it
     * is in the same package as the component's package.
     */
    public String getShortClassName() {
        if (mClass.startsWith(mPackage)) {
            String rest = mClass.substring(mPackage.length());
            if (rest.length() > 0 && rest.charAt(0) == '.') {
                return rest;
            }
        }
        return mClass;
    }

    // ── Flattening / unflattening ──

    /**
     * Return a string that unambiguously identifies the component, using the
     * format "package/class". The class part may use the abbreviated form
     * (see {@link #getShortClassName()}).
     */
    public String flattenToShortString() {
        return mPackage + "/" + getShortClassName();
    }

    /**
     * Return a string that unambiguously identifies the component, using the
     * format "package/class". This is safe to pass to {@link #unflattenFromString}.
     */
    public String flattenToString() {
        return mPackage + "/" + mClass;
    }

    /**
     * Recover a ComponentName from a string that was previously created with
     * {@link #flattenToString()}.
     * @param str the component name string
     * @return a new ComponentName, or null if the string is malformed
     */
    public static ComponentName unflattenFromString(String str) {
        if (str == null) return null;
        int sep = str.indexOf('/');
        if (sep < 0 || sep + 1 >= str.length()) return null;
        String pkg = str.substring(0, sep);
        String cls = str.substring(sep + 1);
        // Expand abbreviated class name
        if (cls.length() > 0 && cls.charAt(0) == '.') {
            cls = pkg + cls;
        }
        return new ComponentName(pkg, cls);
    }

    // ── Object overrides ──

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ComponentName)) return false;
        ComponentName other = (ComponentName) obj;
        return mPackage.equals(other.mPackage) && mClass.equals(other.mClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPackage, mClass);
    }

    @Override
    public String toString() {
        return "ComponentName{" + flattenToString() + "}";
    }
}
