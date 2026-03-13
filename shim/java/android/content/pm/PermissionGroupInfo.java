package android.content.pm;

// Shim: android.content.pm.PermissionGroupInfo
// android.content.pm.PackageItemInfo does not exist in this shim set; Object is used as the
// superclass per project rules (use Object for missing types).
// Android-to-OpenHarmony migration compatibility stub.

public class PermissionGroupInfo extends Object {

    /** Resource identifier for the description of this permission group. */
    public int descriptionRes;

    /**
     * Non-localised description of this permission group, used when {@link #descriptionRes} is 0.
     * May be null.
     */
    public CharSequence nonLocalizedDescription;

    /** Additional flags controlling this permission group. */
    public int flags;

    /**
     * Priority of this permission group relative to others. Higher values are higher priority.
     */
    public int priority;

    public PermissionGroupInfo() {
    }

    /**
     * Convenience constructor.
     *
     * @param descriptionRes         string resource id for the description
     * @param nonLocalizedDescription non-localised fallback description, or null
     * @param flags                  permission group flags
     * @param priority               display priority
     */
    public PermissionGroupInfo(int descriptionRes, CharSequence nonLocalizedDescription,
            int flags, int priority) {
        this.descriptionRes = descriptionRes;
        this.nonLocalizedDescription = nonLocalizedDescription;
        this.flags = flags;
        this.priority = priority;
    }
}
