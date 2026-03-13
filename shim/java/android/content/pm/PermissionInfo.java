package android.content.pm;

/**
 * Shim: android.content.pm.PermissionInfo
 * Tier 1 — data holder describing a single permission.
 *
 * OH mapping:
 *   name            → @ohos.security.permission permissionName
 *   protectionLevel → grantMode (GRANT_SYSTEM / GRANT_USER_CONFIRM)
 *   group           → label (no direct group concept in OH)
 *   descriptionRes  → no direct equivalent; use hardcoded string in OH manifest
 *
 * Protection levels translate as:
 *   PROTECTION_NORMAL      → OH system_grant (auto-granted at install)
 *   PROTECTION_DANGEROUS   → OH user_grant   (runtime permission dialog)
 *   PROTECTION_SIGNATURE   → OH signature permission (no OH equivalent; omit)
 */
public class PermissionInfo {

    // ── Protection level base values ──

    /**
     * A normal protection-level permission.
     * Automatically granted to requesting apps at install time; no user confirmation needed.
     * Maps to OH system_grant.
     */
    public static final int PROTECTION_NORMAL    = 0;

    /**
     * A dangerous protection-level permission.
     * Requires user confirmation at runtime; may be revoked.
     * Maps to OH user_grant.
     */
    public static final int PROTECTION_DANGEROUS = 1;

    /**
     * A signature protection-level permission.
     * Only granted to apps signed with the same certificate as the defining app.
     * No direct OH equivalent; treated as system-internal in the shim.
     */
    public static final int PROTECTION_SIGNATURE = 2;

    /**
     * A signatureOrSystem permission.
     * @deprecated Use {@link #PROTECTION_SIGNATURE} with {@link #PROTECTION_FLAG_PRIVILEGED}.
     */
    @Deprecated
    public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;

    // ── Protection flags (combined with base level via |) ──

    /** Additional flag: only pre-installed privileged apps may hold this permission. */
    public static final int PROTECTION_FLAG_PRIVILEGED    = 0x10;
    /** Additional flag: apps installed as part of the development tools. */
    public static final int PROTECTION_FLAG_DEVELOPMENT   = 0x20;
    /** Additional flag: permission is for apps installed by the installer. */
    public static final int PROTECTION_FLAG_INSTALLER     = 0x40;
    /** Additional flag: permission may be granted to pre-installed apps. */
    public static final int PROTECTION_FLAG_PREINSTALLED  = 0x80;
    /** Additional flag: permission is for system setup. */
    public static final int PROTECTION_FLAG_SETUP         = 0x100;
    /** Additional flag: permission is for apps that perform instant-app resolution. */
    public static final int PROTECTION_FLAG_INSTANT       = 0x1000;
    /** Additional flag: permission is for runtime permission management apps. */
    public static final int PROTECTION_FLAG_RUNTIME       = 0x2000;
    /** Additional flag: OEM-supplied permission. */
    public static final int PROTECTION_FLAG_OEM           = 0x4000;

    // ── Permission flags ──

    /** {@link #flags} value: this permission has been installed as part of the platform. */
    public static final int FLAG_INSTALLED        = 0x40000000;
    /** {@link #flags} value: this permission has been removed. */
    public static final int FLAG_REMOVED          = 0x02;
    /** {@link #flags} value: this permission requires a review before any app can hold it. */
    public static final int FLAG_HARD_RESTRICTED  = 0x04;
    /** {@link #flags} value: this permission is soft-restricted. */
    public static final int FLAG_SOFT_RESTRICTED  = 0x08;
    /** {@link #flags} value: this permission is immutably restricted. */
    public static final int FLAG_IMMUTABLY_RESTRICTED = 0x10;

    // ── Public fields ──

    /**
     * The fully-qualified name of this permission, e.g.
     * "android.permission.CAMERA".
     */
    public String name;

    /**
     * Optional name of a permission group this permission belongs to,
     * e.g. "android.permission-group.CAMERA".
     * May be null.
     */
    public String group;

    /**
     * Resource id of the description string for this permission, or 0 if none.
     */
    public int descriptionRes;

    /**
     * The protection level of this permission.  One of {@link #PROTECTION_NORMAL},
     * {@link #PROTECTION_DANGEROUS}, {@link #PROTECTION_SIGNATURE}, or
     * {@link #PROTECTION_SIGNATURE_OR_SYSTEM}, possibly combined with protection
     * flag bits.
     */
    public int protectionLevel;

    /**
     * Additional flags for this permission.  A combination of the FLAG_* constants.
     */
    public int flags;

    // ── Constructors ──

    /**
     * Creates an empty PermissionInfo.
     */
    public PermissionInfo() {}

    /**
     * Creates a PermissionInfo pre-populated from another instance (copy constructor).
     *
     * @param orig the source PermissionInfo
     */
    public PermissionInfo(PermissionInfo orig) {
        if (orig != null) {
            this.name            = orig.name;
            this.group           = orig.group;
            this.descriptionRes  = orig.descriptionRes;
            this.protectionLevel = orig.protectionLevel;
            this.flags           = orig.flags;
        }
    }

    // ── Helpers ──

    /**
     * Return the base protection level, stripping any flag bits.
     * Equivalent to {@code protectionLevel & 0xf}.
     */
    public int getProtection() {
        return protectionLevel & 0xf;
    }

    /**
     * Return the protection flags (the bits above the base level).
     */
    public int getProtectionFlags() {
        return protectionLevel & ~0xf;
    }

    @Override
    public String toString() {
        return "PermissionInfo{name=" + name
                + ", group=" + group
                + ", protectionLevel=" + protectionLevel + "}";
    }
}
