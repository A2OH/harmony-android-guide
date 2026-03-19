package android.app;

/**
 * Android-compatible PendingIntent shim.
 * Extended with OH-specific fields for AlarmManager→reminderAgent mapping.
 */
public class PendingIntent implements android.os.Parcelable {
    public static final int FLAG_ONE_SHOT = 1 << 30;
    public static final int FLAG_NO_CREATE = 1 << 29;
    public static final int FLAG_CANCEL_CURRENT = 1 << 28;
    public static final int FLAG_UPDATE_CURRENT = 1 << 27;
    public static final int FLAG_IMMUTABLE = 1 << 26;

    private final int mRequestCode;
    private String mTitle = "";
    private String mContent = "";
    private String mTargetAbility = "";
    private String mParamsJson = "{}";
    private int mReminderId = -1;

    public PendingIntent(int requestCode) {
        mRequestCode = requestCode;
    }

    public int getRequestCode() { return mRequestCode; }

    // OH reminder mapping fields
    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }
    public String getContent() { return mContent; }
    public void setContent(String content) { mContent = content; }
    public String getTargetAbility() { return mTargetAbility; }
    public void setTargetAbility(String ability) { mTargetAbility = ability; }
    public String getParamsJson() { return mParamsJson; }
    public void setParamsJson(String json) { mParamsJson = json; }
    public int getReminderId() { return mReminderId; }
    public void setReminderId(int id) { mReminderId = id; }

    public static PendingIntent getActivity(android.content.Context context, int requestCode,
            android.content.Intent intent, int flags) {
        return new PendingIntent(requestCode);
    }

    public static PendingIntent getBroadcast(android.content.Context context, int requestCode,
            android.content.Intent intent, int flags) {
        return new PendingIntent(requestCode);
    }

    public static PendingIntent getService(android.content.Context context, int requestCode,
            android.content.Intent intent, int flags) {
        return new PendingIntent(requestCode);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {}

    public void send() throws CanceledException {
        // stub — no-op in shim layer
    }

    public void send(android.content.Context context, int code, android.content.Intent intent)
            throws CanceledException {
        // stub — no-op in shim layer
    }

    public static class CanceledException extends Exception {
        public CanceledException() { super(); }
        public CanceledException(String message) { super(message); }
        public CanceledException(Exception cause) { super(cause); }
    }
}
