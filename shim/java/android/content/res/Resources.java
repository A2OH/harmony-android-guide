package android.content.res;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class Resources {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() { super(); }
        public NotFoundException(String message) { super(message); }
        public NotFoundException(String message, Throwable cause) { super(message, cause); }
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();
    private ResourceTable mTable;

    /** Load a parsed ResourceTable for resource ID resolution. */
    public void loadResourceTable(ResourceTable table) {
        mTable = table;
    }

    /** Get the loaded ResourceTable, or null if none loaded. */
    public ResourceTable getResourceTable() {
        return mTable;
    }

    public String getString(int id) {
        if (mTable != null) {
            String s = mTable.getString(id);
            if (s != null) return s;
        }
        return "string_" + id;
    }

    public String getString(int id, Object... formatArgs) {
        // Simple substitution: replace %s, %d, %f etc. with arg.toString()
        // Cannot use String.format (JNI native on KitKat Dalvik)
        String template = getString(id);
        if (formatArgs == null || formatArgs.length == 0) return template;
        StringBuilder sb = new StringBuilder();
        int argIdx = 0;
        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            if (c == '%' && i + 1 < template.length() && argIdx < formatArgs.length) {
                char next = template.charAt(i + 1);
                if (next == '%') {
                    sb.append('%');
                    i++;
                } else {
                    // Skip format specifier chars until we hit the conversion char
                    int j = i + 1;
                    while (j < template.length() && "0123456789.-+ #,(".indexOf(template.charAt(j)) >= 0) j++;
                    if (j < template.length()) {
                        sb.append(formatArgs[argIdx] != null ? formatArgs[argIdx].toString() : "null");
                        argIdx++;
                        i = j; // skip the conversion char
                    } else {
                        sb.append(c);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public int getInteger(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public boolean getBoolean(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0) != 0;
        }
        return false;
    }

    public float getDimension(int id) {
        if (mTable != null) {
            return (float) mTable.getInteger(id, 0);
        }
        return 0f;
    }

    public int getColor(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0xFF000000);
        }
        return 0xFF000000;
    }

    public Drawable getDrawable(int id) {
        return null;
    }

    public int getDimensionPixelSize(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public String[] getStringArray(int id) {
        return new String[0];
    }

    public int[] getIntArray(int id) {
        return new int[0];
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Get a resource name by ID (e.g., "string/app_name").
     * Returns null if not found or no ResourceTable loaded.
     */
    public String getResourceName(int id) {
        if (mTable != null) {
            return mTable.getResourceName(id);
        }
        return null;
    }
}
