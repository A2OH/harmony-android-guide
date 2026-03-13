package android.content.pm;
import android.os.PatternMatcher;
import android.os.PatternMatcher;

// Shim: android.content.pm.PathPermission
// android.os.PatternMatcher does not exist in this shim set; Object is used as the superclass
// per project rules (use Object for missing types).
// Android-to-OpenHarmony migration compatibility stub.

public class PathPermission extends Object {

    private final String path;
    private final int type;
    private final String readPermission;
    private final String writePermission;

    /**
     * Create a new PathPermission for a given path pattern.
     *
     * @param path            the path pattern string
     * @param type            the pattern match type (e.g., PatternMatcher.PATTERN_LITERAL)
     * @param readPermission  permission required for read access, or null
     * @param writePermission permission required for write access, or null
     */
    public PathPermission(String path, int type, String readPermission, String writePermission) {
        this.path = path;
        this.type = type;
        this.readPermission = readPermission;
        this.writePermission = writePermission;
    }

    /** Returns the path pattern string. */
    public String getPath() {
        return path;
    }

    /** Returns the pattern match type. */
    public int getType() {
        return type;
    }

    /** Returns the permission required for read access, or null if none. */
    public String getReadPermission() {
        return readPermission;
    }

    /** Returns the permission required for write access, or null if none. */
    public String getWritePermission() {
        return writePermission;
    }
}
