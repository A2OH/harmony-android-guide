package android.content;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashSet;
import java.util.Set;

public class Intent implements Cloneable, Parcelable {

    /* ── Action constants (String, matching real Android) ── */
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_DEFAULT = ACTION_VIEW;
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";

    /* ── Category constants ── */
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_HOME = "android.intent.category.HOME";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
    public static final String CATEGORY_INFO = "android.intent.category.INFO";
    public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
    public static final String CATEGORY_TAB = "android.intent.category.TAB";
    public static final String CATEGORY_EMBED = "android.intent.category.EMBED";

    /* ── Extra key constants ── */
    public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
    public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
    public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
    public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    public static final String EXTRA_TITLE = "android.intent.extra.TITLE";

    /* ── Flags ── */
    public static final int FLAG_ACTIVITY_NEW_TASK = 0x10000000;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 0x04000000;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 0x20000000;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 0x40000000;
    public static final int FLAG_ACTIVITY_CLEAR_TASK = 0x00008000;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 0x00800000;
    public static final int FLAG_ACTIVITY_FORWARD_RESULT = 0x02000000;
    public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 0x08000000;
    public static final int FLAG_ACTIVITY_NO_ANIMATION = 0x00010000;
    public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 0x00020000;
    public static final int FLAG_GRANT_READ_URI_PERMISSION = 0x00000001;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 0x00000002;

    /* ── Instance fields ── */
    private String mAction;
    private Uri mData;
    private String mType;
    private ComponentName mComponent;
    private Bundle mExtras;
    private HashSet<String> mCategories;
    private int mFlags;
    private String mPackage;

    /* ── Constructors ── */

    public Intent() {}

    public Intent(Intent o) {
        if (o != null) {
            this.mAction = o.mAction;
            this.mData = o.mData;
            this.mType = o.mType;
            this.mComponent = o.mComponent;
            this.mFlags = o.mFlags;
            this.mPackage = o.mPackage;
            if (o.mExtras != null) this.mExtras = new Bundle(o.mExtras);
            if (o.mCategories != null) this.mCategories = new HashSet<String>(o.mCategories);
        }
    }

    public Intent(String action) {
        mAction = action;
    }

    public Intent(String action, Uri uri) {
        mAction = action;
        mData = uri;
    }

    public Intent(Context packageContext, Class<?> cls) {
        mComponent = new ComponentName(packageContext, cls);
    }

    public Intent(String action, Uri uri, Context packageContext, Class<?> cls) {
        mAction = action;
        mData = uri;
        mComponent = new ComponentName(packageContext, cls);
    }

    /* ── Fluent setters ── */

    public Intent setAction(String action) { mAction = action; return this; }
    public Intent setData(Uri data) { mData = data; mType = null; return this; }
    public Intent setType(String type) { mType = type; mData = null; return this; }
    public Intent setDataAndType(Uri data, String type) { mData = data; mType = type; return this; }
    public Intent setComponent(ComponentName component) { mComponent = component; return this; }
    public Intent setClassName(String packageName, String className) {
        mComponent = new ComponentName(packageName, className);
        return this;
    }
    public Intent setClassName(Context packageContext, String className) {
        mComponent = new ComponentName(packageContext, className);
        return this;
    }
    public Intent setPackage(String packageName) { mPackage = packageName; return this; }
    public Intent setFlags(int flags) { mFlags = flags; return this; }
    public Intent addFlags(int flags) { mFlags |= flags; return this; }
    public void removeFlags(int flags) { mFlags &= ~flags; }

    public Intent addCategory(String category) {
        if (mCategories == null) mCategories = new HashSet<String>();
        mCategories.add(category);
        return this;
    }
    public void removeCategory(String category) {
        if (mCategories != null) mCategories.remove(category);
    }

    /* ── Getters ── */

    public String getAction() { return mAction; }
    public Uri getData() { return mData; }
    public String getType() { return mType; }
    public ComponentName getComponent() { return mComponent; }
    public String getPackage() { return mPackage; }
    public int getFlags() { return mFlags; }
    public Set<String> getCategories() { return mCategories; }
    public boolean hasCategory(String category) {
        return mCategories != null && mCategories.contains(category);
    }

    /* ── Extras ── */

    public Bundle getExtras() { return mExtras; }

    public Intent putExtra(String name, boolean value) { ensureExtras(); mExtras.putBoolean(name, value); return this; }
    public Intent putExtra(String name, int value) { ensureExtras(); mExtras.putInt(name, value); return this; }
    public Intent putExtra(String name, long value) { ensureExtras(); mExtras.putLong(name, value); return this; }
    public Intent putExtra(String name, double value) { ensureExtras(); mExtras.putDouble(name, value); return this; }
    public Intent putExtra(String name, String value) { ensureExtras(); mExtras.putString(name, value); return this; }
    public Intent putExtras(Bundle extras) { ensureExtras(); mExtras.putAll(extras); return this; }
    public Intent putExtras(Intent src) { if (src.mExtras != null) { ensureExtras(); mExtras.putAll(src.mExtras); } return this; }

    public boolean getBooleanExtra(String name, boolean defaultValue) { return mExtras != null ? mExtras.getBoolean(name, defaultValue) : defaultValue; }
    public int getIntExtra(String name, int defaultValue) { return mExtras != null ? mExtras.getInt(name, defaultValue) : defaultValue; }
    public long getLongExtra(String name, long defaultValue) { return mExtras != null ? mExtras.getLong(name, defaultValue) : defaultValue; }
    public double getDoubleExtra(String name, double defaultValue) { return mExtras != null ? mExtras.getDouble(name, defaultValue) : defaultValue; }
    public String getStringExtra(String name) { return mExtras != null ? mExtras.getString(name) : null; }
    public Bundle getBundleExtra(String name) { return mExtras != null ? (Bundle) mExtras.get(name) : null; }
    public boolean hasExtra(String name) { return mExtras != null && mExtras.containsKey(name); }
    public void removeExtra(String name) { if (mExtras != null) mExtras.remove(name); }

    private void ensureExtras() { if (mExtras == null) mExtras = new Bundle(); }

    /* ── Factory methods ── */

    public static Intent createChooser(Intent target, CharSequence title) {
        Intent intent = new Intent(ACTION_CHOOSER);
        intent.putExtra(EXTRA_INTENT, title != null ? title.toString() : "");
        return intent;
    }

    public static Intent makeMainActivity(ComponentName mainActivity) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.setComponent(mainActivity);
        intent.addCategory(CATEGORY_LAUNCHER);
        return intent;
    }

    /* ── Object methods ── */

    @Override
    public Object clone() { return new Intent(this); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Intent { ");
        if (mAction != null) sb.append("act=").append(mAction).append(' ');
        if (mCategories != null) sb.append("cat=").append(mCategories).append(' ');
        if (mData != null) sb.append("dat=").append(mData).append(' ');
        if (mType != null) sb.append("typ=").append(mType).append(' ');
        if (mComponent != null) sb.append("cmp=").append(mComponent.flattenToShortString()).append(' ');
        if (mExtras != null) sb.append("(has extras) ");
        sb.append('}');
        return sb.toString();
    }

    /* ── Parcelable stubs ── */
    public int describeContents() { return 0; }
    public void readFromParcel(Parcel in) {}
    public void writeToParcel(Parcel dest, int flags) {}

    /* ── Remaining stubs ── */
    public byte getByteExtra(String name, byte defaultValue) { return defaultValue; }
    public char getCharExtra(String name, char defaultValue) { return defaultValue; }
    public float getFloatExtra(String name, float defaultValue) { return defaultValue; }
    public short getShortExtra(String name, short defaultValue) { return defaultValue; }
    public int fillIn(Intent other, int flags) { return 0; }
    public boolean filterEquals(Intent other) { return false; }
    public int filterHashCode() { return 0; }
    public ComponentName resolveActivity(PackageManager pm) { return mComponent; }
    public ActivityInfo resolveActivityInfo(PackageManager pm, int flags) { return null; }
    public void setClipData(ClipData clip) {}
    public void setExtrasClassLoader(ClassLoader loader) {}
    public void setSelector(Intent selector) {}
    public void setSourceBounds(Rect r) {}
    public String toUri(int flags) { return toString(); }
    public boolean hasFileDescriptors() { return false; }
    public static Intent parseUri(String uri, int flags) { return null; }
    public static Intent getIntentOld(String uri) { return null; }
    public static Intent makeMainSelectorActivity(String selectorAction, String selectorCategory) { return null; }
    public static Intent makeRestartActivityTask(ComponentName mainActivity) { return null; }
}
