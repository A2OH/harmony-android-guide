package android.view.textservice;

/**
 * Android-compatible SpellCheckerInfo shim. Simple data holder — no real spell-checker metadata.
 */
public class SpellCheckerInfo {

    private final String id;
    private final String packageName;
    private final Object[] subtypes;

    public SpellCheckerInfo(String id, String packageName, Object[] subtypes) {
        this.id          = id          != null ? id          : "";
        this.packageName = packageName != null ? packageName : "";
        this.subtypes    = subtypes    != null ? subtypes    : new Object[0];
    }

    /** Convenience constructor for a minimal shim instance. */
    public SpellCheckerInfo(String id, String packageName) {
        this(id, packageName, new Object[0]);
    }

    public String getId()          { return id; }
    public String getPackageName() { return packageName; }
    public int    getSubtypeCount() { return subtypes.length; }

    public Object getSubtypeAt(int index) {
        if (index < 0 || index >= subtypes.length) return null;
        return subtypes[index];
    }
}
