package android.content.pm;

/**
 * Android-compatible ProviderInfo shim. Stub — no-op implementation for A2OH migration.
 */
public class ProviderInfo extends PackageItemInfo {

    public String  authority;
    public String  readPermission;
    public String  writePermission;
    public boolean exported;
    public boolean grantUriPermissions;
    public boolean multiprocess;

    public ProviderInfo() {}

    public ProviderInfo(ProviderInfo orig) {
        super(orig);
        authority           = orig.authority;
        readPermission      = orig.readPermission;
        writePermission     = orig.writePermission;
        exported            = orig.exported;
        grantUriPermissions = orig.grantUriPermissions;
        multiprocess        = orig.multiprocess;
    }
}
