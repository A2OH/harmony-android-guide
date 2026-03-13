package android.content.pm;

/**
 * Android-compatible ServiceInfo shim. Stub — no-op implementation for A2OH migration.
 */
public class ServiceInfo extends PackageItemInfo {

    public static final int FLAG_STOP_WITH_TASK   = 1;
    public static final int FLAG_ISOLATED_PROCESS = 2;

    public String  permission;
    public int     flags;
    public boolean exported;

    public ServiceInfo() {}

    public ServiceInfo(ServiceInfo orig) {
        super(orig);
        permission = orig.permission;
        flags      = orig.flags;
        exported   = orig.exported;
    }
}
