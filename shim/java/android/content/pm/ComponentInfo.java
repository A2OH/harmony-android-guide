package android.content.pm;

/**
 * Android-compatible ComponentInfo shim. Base class for component descriptors
 * (ActivityInfo, ServiceInfo, ProviderInfo, etc.).
 */
public class ComponentInfo extends PackageItemInfo {

    /** Global information about the application/package this component is in. */
    public ApplicationInfo applicationInfo;

    /** The name of the process this component runs in. */
    public String processName;

    /** The name of the task affinity of this component. */
    public String taskAffinity;

    /** Whether this component is enabled. */
    public boolean enabled = true;

    /** Whether this component is exported. */
    public boolean exported;

    /** Whether this component is directly boot-aware. */
    public boolean directBootAware;

    /** Whether this component is encryptionAware (legacy alias for directBootAware). */
    public boolean encryptionAware;

    /** Split name under which this component is declared. May be null. */
    public String splitName;

    public ComponentInfo() {}

    public ComponentInfo(ComponentInfo orig) {
        super(orig);
        if (orig != null) {
            applicationInfo    = orig.applicationInfo;
            processName        = orig.processName;
            taskAffinity       = orig.taskAffinity;
            enabled            = orig.enabled;
            exported           = orig.exported;
            directBootAware    = orig.directBootAware;
            encryptionAware    = orig.encryptionAware;
            splitName          = orig.splitName;
        }
    }

    /** Returns true if this component is enabled and exported. */
    public final boolean isEnabled() { return enabled; }

    @Override
    public String toString() {
        return "ComponentInfo{" + packageName + "/" + name + "}";
    }
}
