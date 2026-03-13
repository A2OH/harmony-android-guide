package android.service.notification;

public final class Condition {
    public Condition() {}

    public static final int FLAG_RELEVANT_ALWAYS = 0;
    public static final int FLAG_RELEVANT_NOW = 0;
    public static final int SCHEME = 0;
    public static final int STATE_ERROR = 0;
    public static final int STATE_FALSE = 0;
    public static final int STATE_TRUE = 0;
    public static final int STATE_UNKNOWN = 0;
    public int flags = 0;
    public int icon = 0;
    public int id = 0;
    public int line1 = 0;
    public int line2 = 0;
    public int state = 0;
    public int summary = 0;
    public Object copy() { return null; }
    public int describeContents() { return 0; }
    public static boolean isValidId(Object p0, Object p1) { return false; }
    public static Object newId(Object p0) { return null; }
    public static Object relevanceToString(Object p0) { return null; }
    public static Object stateToString(Object p0) { return null; }
    public void writeToParcel(Object p0, Object p1) {}
}
