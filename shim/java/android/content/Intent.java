package android.content;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
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
            this.mSourceBounds = o.mSourceBounds != null ? new Rect(o.mSourceBounds) : null;
            this.mClipData = o.mClipData;
            if (o.mSelector != null) this.mSelector = new Intent(o.mSelector);
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

    /** Returns a JSON-ish string of the extras for bridge serialisation. */
    public String getExtrasJson() {
        if (mExtras == null) return "{}";
        return mExtras.toString();
    }

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
        if (mFlags != 0) sb.append("flg=0x").append(Integer.toHexString(mFlags)).append(' ');
        if (mPackage != null) sb.append("pkg=").append(mPackage).append(' ');
        if (mExtras != null) sb.append("(has extras) ");
        sb.append('}');
        return sb.toString();
    }

    /* ── Parcelable stubs ── */
    public int describeContents() { return 0; }
    public void readFromParcel(Parcel in) {}
    public void writeToParcel(Parcel dest, int flags) {}

    /* ── Additional extra putters for primitive types ── */

    public Intent putExtra(String name, byte value) { ensureExtras(); mExtras.putByte(name, value); return this; }
    public Intent putExtra(String name, char value) { ensureExtras(); mExtras.putChar(name, value); return this; }
    public Intent putExtra(String name, float value) { ensureExtras(); mExtras.putFloat(name, value); return this; }
    public Intent putExtra(String name, short value) { ensureExtras(); mExtras.putShort(name, value); return this; }
    public Intent putExtra(String name, CharSequence value) { ensureExtras(); mExtras.putCharSequence(name, value); return this; }
    public Intent putExtra(String name, Parcelable value) { ensureExtras(); mExtras.putParcelable(name, value); return this; }
    public Intent putExtra(String name, java.io.Serializable value) { ensureExtras(); mExtras.putSerializable(name, value); return this; }
    public Intent putExtra(String name, boolean[] value) { ensureExtras(); mExtras.putBooleanArray(name, value); return this; }
    public Intent putExtra(String name, byte[] value) { ensureExtras(); mExtras.putByteArray(name, value); return this; }
    public Intent putExtra(String name, char[] value) { ensureExtras(); mExtras.putCharArray(name, value); return this; }
    public Intent putExtra(String name, double[] value) { ensureExtras(); mExtras.putDoubleArray(name, value); return this; }
    public Intent putExtra(String name, float[] value) { ensureExtras(); mExtras.putFloatArray(name, value); return this; }
    public Intent putExtra(String name, int[] value) { ensureExtras(); mExtras.putIntArray(name, value); return this; }
    public Intent putExtra(String name, long[] value) { ensureExtras(); mExtras.putLongArray(name, value); return this; }
    public Intent putExtra(String name, short[] value) { ensureExtras(); mExtras.putShortArray(name, value); return this; }
    public Intent putExtra(String name, String[] value) { ensureExtras(); mExtras.putStringArray(name, value); return this; }
    public Intent putExtra(String name, Parcelable[] value) { ensureExtras(); mExtras.putParcelableArray(name, value); return this; }
    public Intent putExtra(String name, Bundle value) { ensureExtras(); mExtras.putBundle(name, value); return this; }

    public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        ensureExtras();
        mExtras.putCharSequenceArrayList(name, (ArrayList) value);
        return this;
    }
    public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        ensureExtras();
        mExtras.putIntegerArrayList(name, (ArrayList) value);
        return this;
    }
    public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        ensureExtras();
        mExtras.putParcelableArrayList(name, (ArrayList) value);
        return this;
    }
    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        ensureExtras();
        mExtras.putStringArrayList(name, (ArrayList) value);
        return this;
    }

    /* ── Additional extra getters ── */

    public byte getByteExtra(String name, byte defaultValue) {
        return mExtras != null ? mExtras.getByte(name, defaultValue) : defaultValue;
    }
    public char getCharExtra(String name, char defaultValue) {
        return mExtras != null ? mExtras.getChar(name, defaultValue) : defaultValue;
    }
    public float getFloatExtra(String name, float defaultValue) {
        return mExtras != null ? mExtras.getFloat(name, defaultValue) : defaultValue;
    }
    public short getShortExtra(String name, short defaultValue) {
        return mExtras != null ? mExtras.getShort(name, defaultValue) : defaultValue;
    }
    public CharSequence getCharSequenceExtra(String name) {
        return mExtras != null ? mExtras.getCharSequence(name, null) : null;
    }
    public <T extends Parcelable> T getParcelableExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        try {
            @SuppressWarnings("unchecked")
            T result = (T) v;
            return result;
        } catch (ClassCastException e) {
            return null;
        }
    }
    public java.io.Serializable getSerializableExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof java.io.Serializable ? (java.io.Serializable) v : null;
    }
    public boolean[] getBooleanArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof boolean[] ? (boolean[]) v : null;
    }
    public byte[] getByteArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof byte[] ? (byte[]) v : null;
    }
    public char[] getCharArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof char[] ? (char[]) v : null;
    }
    public double[] getDoubleArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof double[] ? (double[]) v : null;
    }
    public float[] getFloatArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof float[] ? (float[]) v : null;
    }
    public int[] getIntArrayExtra(String name) {
        return mExtras != null ? mExtras.getIntArray(name) : null;
    }
    public long[] getLongArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof long[] ? (long[]) v : null;
    }
    public short[] getShortArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof short[] ? (short[]) v : null;
    }
    public String[] getStringArrayExtra(String name) {
        return mExtras != null ? mExtras.getStringArray(name) : null;
    }
    public Parcelable[] getParcelableArrayExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof Parcelable[] ? (Parcelable[]) v : null;
    }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof ArrayList ? (ArrayList<T>) v : null;
    }
    @SuppressWarnings("unchecked")
    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof ArrayList ? (ArrayList<CharSequence>) v : null;
    }
    @SuppressWarnings("unchecked")
    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof ArrayList ? (ArrayList<Integer>) v : null;
    }
    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringArrayListExtra(String name) {
        if (mExtras == null) return null;
        Object v = mExtras.get(name);
        return v instanceof ArrayList ? (ArrayList<String>) v : null;
    }

    /* ── Selector, ClipData, SourceBounds, ClassLoader ── */

    private Intent mSelector;
    private ClipData mClipData;
    private Rect mSourceBounds;

    public void setClipData(ClipData clip) { mClipData = clip; }
    public ClipData getClipData() { return mClipData; }
    public void setExtrasClassLoader(ClassLoader loader) {
        /* No-op in shim; real Android uses this for Parcelable deserialization */
    }
    public void setSelector(Intent selector) {
        if (mComponent != null) {
            throw new IllegalArgumentException("Can't set selector when component is already set");
        }
        mSelector = selector;
    }
    public Intent getSelector() { return mSelector; }
    public void setSourceBounds(Rect r) { mSourceBounds = r; }
    public Rect getSourceBounds() { return mSourceBounds; }

    /* ── Data scheme/authority helpers ── */

    public String getScheme() {
        return mData != null ? mData.getScheme() : null;
    }

    public String getDataString() {
        return mData != null ? mData.toString() : null;
    }

    public Intent setDataAndNormalize(Uri data) {
        return setData(data.normalizeScheme());
    }

    public Intent setTypeAndNormalize(String type) {
        return setType(normalizeType(type));
    }

    public Intent setDataAndTypeAndNormalize(Uri data, String type) {
        return setDataAndType(data.normalizeScheme(), normalizeType(type));
    }

    public String resolveType(ContentResolver resolver) {
        if (mType != null) return mType;
        if (mData != null) {
            if ("content".equals(mData.getScheme())) {
                return resolver != null ? resolver.getType(mData) : null;
            }
        }
        return null;
    }

    public String resolveTypeIfNeeded(ContentResolver resolver) {
        if (mComponent != null) return mType;
        return resolveType(resolver);
    }

    /* ── fillIn ── */

    public static final int FILL_IN_ACTION = 1 << 0;
    public static final int FILL_IN_DATA = 1 << 1;
    public static final int FILL_IN_CATEGORIES = 1 << 2;
    public static final int FILL_IN_COMPONENT = 1 << 3;
    public static final int FILL_IN_PACKAGE = 1 << 4;
    public static final int FILL_IN_SOURCE_BOUNDS = 1 << 5;
    public static final int FILL_IN_SELECTOR = 1 << 6;
    public static final int FILL_IN_CLIP_DATA = 1 << 7;

    public int fillIn(Intent other, int flags) {
        int changes = 0;
        if (other.mAction != null
                && (mAction == null || (flags & FILL_IN_ACTION) != 0)) {
            mAction = other.mAction;
            changes |= FILL_IN_ACTION;
        }
        if ((other.mData != null || other.mType != null)
                && ((mData == null && mType == null) || (flags & FILL_IN_DATA) != 0)) {
            mData = other.mData;
            mType = other.mType;
            changes |= FILL_IN_DATA;
        }
        if (other.mCategories != null
                && (mCategories == null || (flags & FILL_IN_CATEGORIES) != 0)) {
            if (other.mCategories != null) {
                mCategories = new HashSet<String>(other.mCategories);
            }
            changes |= FILL_IN_CATEGORIES;
        }
        if (other.mComponent != null
                && (mComponent == null || (flags & FILL_IN_COMPONENT) != 0)) {
            mComponent = other.mComponent;
            changes |= FILL_IN_COMPONENT;
        }
        if (other.mPackage != null
                && (mPackage == null || (flags & FILL_IN_PACKAGE) != 0)) {
            mPackage = other.mPackage;
            changes |= FILL_IN_PACKAGE;
        }
        if (other.mSourceBounds != null
                && (mSourceBounds == null || (flags & FILL_IN_SOURCE_BOUNDS) != 0)) {
            mSourceBounds = other.mSourceBounds;
            changes |= FILL_IN_SOURCE_BOUNDS;
        }
        if (other.mSelector != null
                && (mSelector == null || (flags & FILL_IN_SELECTOR) != 0)) {
            mSelector = other.mSelector;
            changes |= FILL_IN_SELECTOR;
        }
        if (other.mClipData != null
                && (mClipData == null || (flags & FILL_IN_CLIP_DATA) != 0)) {
            mClipData = other.mClipData;
            changes |= FILL_IN_CLIP_DATA;
        }
        if (other.mExtras != null) {
            if (mExtras == null) {
                mExtras = new Bundle(other.mExtras);
            } else {
                mExtras.putAll(other.mExtras);
            }
        }
        if (other.mFlags != 0) {
            mFlags |= other.mFlags;
        }
        return changes;
    }

    /* ── filterEquals / filterHashCode ── */

    public boolean filterEquals(Intent other) {
        if (other == null) return false;
        if (!objectsEqual(mAction, other.mAction)) return false;
        if (!objectsEqual(mData, other.mData)) return false;
        if (!objectsEqual(mType, other.mType)) return false;
        if (!objectsEqual(mPackage, other.mPackage)) return false;
        if (!objectsEqual(mComponent, other.mComponent)) return false;
        if (!objectsEqual(mCategories, other.mCategories)) return false;
        return true;
    }

    public int filterHashCode() {
        int code = 0;
        if (mAction != null) code += mAction.hashCode();
        if (mData != null) code += mData.hashCode();
        if (mType != null) code += mType.hashCode();
        if (mPackage != null) code += mPackage.hashCode();
        if (mComponent != null) code += mComponent.hashCode();
        if (mCategories != null) code += mCategories.hashCode();
        return code;
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /* ── resolveActivity ── */

    public ComponentName resolveActivity(PackageManager pm) { return mComponent; }
    public ActivityInfo resolveActivityInfo(PackageManager pm, int flags) { return null; }

    /* ── URI conversion constants ── */

    public static final int URI_INTENT_SCHEME = 1 << 0;
    public static final int URI_ANDROID_APP_SCHEME = 1 << 1;
    public static final int URI_ALLOW_UNSAFE = 1 << 2;

    /* ── toUri ── */

    public String toUri(int uriFlags) {
        StringBuilder uri = new StringBuilder(128);
        if ((uriFlags & URI_INTENT_SCHEME) != 0) {
            if (mData != null) {
                String dataStr = mData.toString();
                // Replace scheme with "intent:"
                uri.append("intent:");
                int schemeEnd = dataStr.indexOf(':');
                if (schemeEnd >= 0) {
                    uri.append(dataStr, schemeEnd + 1, dataStr.length());
                }
            } else {
                uri.append("intent:");
            }
        } else {
            if (mData != null) {
                uri.append(mData.toString());
            }
        }
        // Append fragment-style extras
        uri.append("#Intent;");
        if (mAction != null) uri.append("action=").append(Uri.encode(mAction)).append(';');
        if (mCategories != null) {
            for (String cat : mCategories) {
                uri.append("category=").append(Uri.encode(cat)).append(';');
            }
        }
        if (mType != null) uri.append("type=").append(Uri.encode(mType)).append(';');
        if (mFlags != 0) uri.append("launchFlags=0x").append(Integer.toHexString(mFlags)).append(';');
        if (mPackage != null) uri.append("package=").append(Uri.encode(mPackage)).append(';');
        if (mComponent != null) {
            uri.append("component=")
               .append(Uri.encode(mComponent.flattenToShortString()))
               .append(';');
        }
        if (mSourceBounds != null) {
            uri.append("sourceBounds=")
               .append(Uri.encode(mSourceBounds.flattenToString()))
               .append(';');
        }
        // Extras are not included in URI serialization (matches AOSP behavior for simple cases)
        uri.append("end");
        return uri.toString();
    }

    /* ── parseUri ── */

    public static Intent parseUri(String uri, int intentFlags) throws java.net.URISyntaxException {
        if (uri == null) throw new java.net.URISyntaxException("", "null uri");

        Intent intent = new Intent();
        boolean isIntentScheme = uri.startsWith("intent:");

        // Find the fragment portion "#Intent;...;end"
        int fragIdx = uri.indexOf("#Intent;");
        String dataStr = null;
        String fragmentPart = null;

        if (fragIdx >= 0) {
            fragmentPart = uri.substring(fragIdx + 8); // skip "#Intent;"
            dataStr = uri.substring(0, fragIdx);
        } else {
            // No Intent fragment — treat the whole thing as a data URI
            // Handle legacy "#Intent" without semicolons or just a plain URI
            if (isIntentScheme) {
                // intent: scheme without fragment
                dataStr = uri;
            } else {
                intent.mData = Uri.parse(uri);
                return intent;
            }
        }

        // Reconstruct data URI
        if (isIntentScheme && dataStr != null && dataStr.startsWith("intent:")) {
            // Restore original scheme — the real scheme is lost in intent: encoding
            // We can't recover it, so leave data null unless there's a real path
            String rest = dataStr.substring(7); // after "intent:"
            if (rest.length() > 0 && !rest.equals("")) {
                // Try to create a data URI; the scheme is unknown, use the fragment info
                // Don't set data for bare "intent:" with no real URI data
            }
        } else if (dataStr != null && dataStr.length() > 0) {
            intent.mData = Uri.parse(dataStr);
        }

        // Parse fragment key=value pairs
        if (fragmentPart != null) {
            // Remove trailing "end" if present
            if (fragmentPart.endsWith("end")) {
                fragmentPart = fragmentPart.substring(0, fragmentPart.length() - 3);
            }
            String[] pairs = fragmentPart.split(";");
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                int eq = pair.indexOf('=');
                if (eq < 0) continue;
                String key = pair.substring(0, eq);
                String value = Uri.decode(pair.substring(eq + 1));
                switch (key) {
                    case "action":
                        intent.mAction = value;
                        break;
                    case "category":
                        intent.addCategory(value);
                        break;
                    case "type":
                        intent.mType = value;
                        break;
                    case "launchFlags":
                        try {
                            if (value.startsWith("0x")) {
                                intent.mFlags = Integer.parseInt(value.substring(2), 16);
                            } else {
                                intent.mFlags = Integer.parseInt(value);
                            }
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                        break;
                    case "package":
                        intent.mPackage = value;
                        break;
                    case "component":
                        intent.mComponent = ComponentName.unflattenFromString(value);
                        break;
                    case "sourceBounds":
                        intent.mSourceBounds = Rect.unflattenFromString(value);
                        break;
                    // S.xxx=val for string extras
                    default:
                        if (key.length() > 2 && key.charAt(1) == '.') {
                            String extraName = key.substring(2);
                            char typeChar = key.charAt(0);
                            intent.ensureExtras();
                            switch (typeChar) {
                                case 'S': intent.mExtras.putString(extraName, value); break;
                                case 'B':
                                    intent.mExtras.putBoolean(extraName, Boolean.parseBoolean(value));
                                    break;
                                case 'i':
                                    try { intent.mExtras.putInt(extraName, Integer.parseInt(value)); }
                                    catch (NumberFormatException e) { /* skip */ }
                                    break;
                                case 'l':
                                    try { intent.mExtras.putLong(extraName, Long.parseLong(value)); }
                                    catch (NumberFormatException e) { /* skip */ }
                                    break;
                                case 'f':
                                    try { intent.mExtras.putFloat(extraName, Float.parseFloat(value)); }
                                    catch (NumberFormatException e) { /* skip */ }
                                    break;
                                case 'd':
                                    try { intent.mExtras.putDouble(extraName, Double.parseDouble(value)); }
                                    catch (NumberFormatException e) { /* skip */ }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        return intent;
    }

    /* ── getIntent / getIntentOld (legacy) ── */

    public static Intent getIntent(String uri) throws java.net.URISyntaxException {
        return parseUri(uri, 0);
    }

    public static Intent getIntentOld(String uri) {
        try {
            return parseUri(uri, 0);
        } catch (java.net.URISyntaxException e) {
            return null;
        }
    }

    /* ── Factory methods (remaining) ── */

    public static Intent makeMainSelectorActivity(String selectorAction, String selectorCategory) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.addCategory(CATEGORY_LAUNCHER);
        Intent selector = new Intent();
        selector.setAction(selectorAction);
        selector.addCategory(selectorCategory);
        intent.mSelector = selector;
        return intent;
    }

    public static Intent makeRestartActivityTask(ComponentName mainActivity) {
        Intent intent = makeMainActivity(mainActivity);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    /* ── replaceExtras ── */

    public Intent replaceExtras(Intent src) {
        mExtras = src.mExtras != null ? new Bundle(src.mExtras) : null;
        return this;
    }

    public Intent replaceExtras(Bundle extras) {
        mExtras = extras != null ? new Bundle(extras) : null;
        return this;
    }

    /* ── hasFileDescriptors ── */

    public boolean hasFileDescriptors() { return false; }

    /* ── normalize helpers ── */

    public static String normalizeMimeType(String type) {
        return normalizeType(type);
    }

    private static String normalizeType(String type) {
        if (type == null) return null;
        return type.trim().toLowerCase(java.util.Locale.US);
    }
}
