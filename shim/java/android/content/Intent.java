package android.content;

import android.net.Uri;
import android.os.Bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Shim: android.content.Intent → OH Want
 * Tier 2 — composite mapping. Intent's rich API surface maps to Want's
 * simpler {bundleName, abilityName, parameters} structure.
 *
 * The shim stores extras in a Bundle and converts to Want parameters
 * when startActivity/startService is called via Context.
 */
public class Intent {
    // ── Standard actions ──
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";

    // ── Standard categories ──
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";

    // ── Flags ──
    public static final int FLAG_ACTIVITY_NEW_TASK = 0x10000000;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 0x04000000;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 0x20000000;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 0x40000000;

    private String action;
    private Uri data;
    private String type;
    private String packageName;
    private String className;  // target component
    private int flags;
    private Bundle extras;
    private final java.util.List<String> categories = new java.util.ArrayList<>();

    // ── Constructors ──

    public Intent() {
        this.extras = new Bundle();
    }

    public Intent(String action) {
        this();
        this.action = action;
    }

    public Intent(String action, Uri uri) {
        this(action);
        this.data = uri;
    }

    public Intent(Context packageContext, Class<?> cls) {
        this();
        this.packageName = packageContext.getPackageName();
        this.className = cls.getName();
    }

    public Intent(Intent other) {
        this.action = other.action;
        this.data = other.data;
        this.type = other.type;
        this.packageName = other.packageName;
        this.className = other.className;
        this.flags = other.flags;
        this.extras = new Bundle(other.extras);
        this.categories.addAll(other.categories);
    }

    // ── Component ──

    public Intent setClass(Context packageContext, Class<?> cls) {
        this.packageName = packageContext.getPackageName();
        this.className = cls.getName();
        return this;
    }

    public Intent setClassName(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        return this;
    }

    public String getComponent() { return className; }

    // ── Action ──

    public Intent setAction(String action) { this.action = action; return this; }
    public String getAction() { return action; }

    // ── Data ──

    public Intent setData(Uri data) { this.data = data; this.type = null; return this; }
    public Uri getData() { return data; }

    public Intent setType(String type) { this.type = type; this.data = null; return this; }
    public String getType() { return type; }

    public Intent setDataAndType(Uri data, String type) {
        this.data = data;
        this.type = type;
        return this;
    }

    // ── Categories ──

    public Intent addCategory(String category) { categories.add(category); return this; }
    public java.util.Set<String> getCategories() { return new java.util.HashSet<>(categories); }
    public boolean hasCategory(String category) { return categories.contains(category); }

    // ── Flags ──

    public Intent setFlags(int flags) { this.flags = flags; return this; }
    public Intent addFlags(int flags) { this.flags |= flags; return this; }
    public int getFlags() { return flags; }

    // ── Extras (putExtra / getXxxExtra) ──

    public Intent putExtra(String name, String value) { extras.putString(name, value); return this; }
    public Intent putExtra(String name, int value) { extras.putInt(name, value); return this; }
    public Intent putExtra(String name, long value) { extras.putLong(name, value); return this; }
    public Intent putExtra(String name, float value) { extras.putFloat(name, value); return this; }
    public Intent putExtra(String name, double value) { extras.putDouble(name, value); return this; }
    public Intent putExtra(String name, boolean value) { extras.putBoolean(name, value); return this; }
    public Intent putExtra(String name, byte[] value) { extras.putByteArray(name, value); return this; }
    public Intent putExtra(String name, String[] value) { extras.putStringArray(name, value); return this; }
    public Intent putExtra(String name, int[] value) { extras.putIntArray(name, value); return this; }
    public Intent putExtra(String name, Serializable value) { extras.putSerializable(name, value); return this; }
    public Intent putExtra(String name, Bundle value) { extras.putBundle(name, value); return this; }

    public Intent putExtras(Bundle extras) { this.extras.putAll(extras); return this; }

    public String getStringExtra(String name) { return extras.getString(name); }
    public int getIntExtra(String name, int defaultValue) { return extras.getInt(name, defaultValue); }
    public long getLongExtra(String name, long defaultValue) { return extras.getLong(name, defaultValue); }
    public float getFloatExtra(String name, float defaultValue) { return extras.getFloat(name, defaultValue); }
    public double getDoubleExtra(String name, double defaultValue) { return extras.getDouble(name, defaultValue); }
    public boolean getBooleanExtra(String name, boolean defaultValue) { return extras.getBoolean(name, defaultValue); }
    public byte[] getByteArrayExtra(String name) { return extras.getByteArray(name); }
    public String[] getStringArrayExtra(String name) { return extras.getStringArray(name); }
    public int[] getIntArrayExtra(String name) { return extras.getIntArray(name); }
    public Serializable getSerializableExtra(String name) { return extras.getSerializable(name); }
    public Bundle getBundleExtra(String name) { return extras.getBundle(name); }

    public Bundle getExtras() { return extras; }
    public boolean hasExtra(String name) { return extras.containsKey(name); }
    public void removeExtra(String name) { extras.remove(name); }

    // ── Package ──

    public Intent setPackage(String packageName) { this.packageName = packageName; return this; }
    public String getPackage() { return packageName; }

    // ── Bridge helpers (used by Context shim internally) ──

    /**
     * Returns the target ability name derived from the class name.
     * e.g., "com.example.app.DetailActivity" → "DetailActivity"
     */
    public String getTargetAbilityName() {
        if (className == null) return null;
        int dot = className.lastIndexOf('.');
        return dot >= 0 ? className.substring(dot + 1) : className;
    }

    /**
     * Serialize extras to JSON for passing through the JNI bridge.
     */
    public String getExtrasJson() {
        Map<String, Object> map = extras.toMap();
        // Simple JSON serialization for primitive types
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(e.getKey())).append("\":");
            Object v = e.getValue();
            if (v instanceof String) {
                sb.append("\"").append(escapeJson((String) v)).append("\"");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else {
                sb.append("\"").append(escapeJson(String.valueOf(v))).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
