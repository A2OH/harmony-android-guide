package android.content.pm;

/**
 * Android-compatible ResolveInfo shim. Stub — no-op implementation for A2OH migration.
 */
public class ResolveInfo {

    public ActivityInfo activityInfo;
    public ServiceInfo  serviceInfo;
    public ProviderInfo providerInfo;
    /** IntentFilter represented as Object to avoid dependency on android.content.IntentFilter. */
    public Object       filter;
    public int          priority;
    public int          preferredOrder;
    public int          match;
    public boolean      isDefault;

    public ResolveInfo() {}

    /**
     * Retrieve the best label associated with this resolution.
     * @param pm  PackageManager — typed as Object to avoid dependency.
     */
    public CharSequence loadLabel(Object pm) {
        if (activityInfo != null) return activityInfo.loadLabel(pm);
        if (serviceInfo  != null) return serviceInfo.loadLabel(pm);
        if (providerInfo != null) return providerInfo.loadLabel(pm);
        return "";
    }

    /**
     * Retrieve the best icon associated with this resolution.
     * @param pm  PackageManager — typed as Object to avoid dependency.
     * @return null (stub)
     */
    public Object loadIcon(Object pm) {
        return null; // stub
    }

    @Override
    public String toString() {
        return "ResolveInfo{priority=" + priority + ", match=" + match + "}";
    }
}
