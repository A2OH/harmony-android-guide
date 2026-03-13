package android.content;

/**
 * Android-compatible ContextWrapper shim.
 *
 * Proxies all Context method calls to a wrapped base Context. This mirrors
 * the real android.content.ContextWrapper which is the base class for
 * Activity, Service, Application, etc.
 *
 * Subclasses override specific methods to add functionality on top of the
 * wrapped context.
 */
public class ContextWrapper extends Context {

    private Context mBase;

    /**
     * Create a new ContextWrapper that delegates method calls to the given base context.
     * @param base The base context, or null. If null, {@link #attachBaseContext} must
     *             be called before any method is invoked.
     */
    public ContextWrapper(Context base) {
        mBase = base;
    }

    /**
     * Set the base context for this ContextWrapper. All method calls will be delegated
     * to the base context. Throws if a base context has already been set.
     */
    protected void attachBaseContext(Context base) {
        if (mBase != null) {
            throw new IllegalStateException("Base context already set");
        }
        mBase = base;
    }

    /**
     * @return the base context this ContextWrapper wraps
     */
    public Context getBaseContext() {
        return mBase;
    }

    // ── Delegating overrides ──

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mBase != null ? mBase.getSharedPreferences(name, mode) : super.getSharedPreferences(name, mode);
    }

    @Override
    public Object getSystemService(String name) {
        return mBase != null ? mBase.getSystemService(name) : super.getSystemService(name);
    }

    @Override
    public void startActivity(Intent intent) {
        if (mBase != null) {
            mBase.startActivity(intent);
        } else {
            super.startActivity(intent);
        }
    }

    @Override
    public void startService(Intent intent) {
        if (mBase != null) {
            mBase.startService(intent);
        } else {
            super.startService(intent);
        }
    }

    @Override
    public void sendBroadcast(Intent intent) {
        if (mBase != null) {
            mBase.sendBroadcast(intent);
        } else {
            super.sendBroadcast(intent);
        }
    }

    @Override
    public String getPackageName() {
        return mBase != null ? mBase.getPackageName() : super.getPackageName();
    }

    @Override
    public String getString(int resId) {
        return mBase != null ? mBase.getString(resId) : super.getString(resId);
    }

    @Override
    public ContentResolver getContentResolver() {
        return mBase != null ? mBase.getContentResolver() : super.getContentResolver();
    }

    @Override
    public Context getApplicationContext() {
        return mBase != null ? mBase.getApplicationContext() : super.getApplicationContext();
    }
}
