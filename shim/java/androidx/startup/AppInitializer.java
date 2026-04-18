package androidx.startup;

import android.content.Context;

public final class AppInitializer {
    private static void debug(String message) {}
    static { debug("[AppInit-shim] CLASS LOADED"); }
    private static AppInitializer sInstance;
    private AppInitializer(Context c) {}
    public static AppInitializer e(Context c) {
        if (sInstance == null) { sInstance = new AppInitializer(c); debug("[AppInit-shim] instance created"); }
        return sInstance;
    }
    public static AppInitializer getInstance(Context c) { return e(c); }
    public static Object d(Context c, java.util.Set s) { return null; }
    public void a() {}
    public void b(android.os.Bundle b) {}
    public Object c(Class cls) { debug("[AppInit-shim] c(" + cls.getName() + ") -> null"); return null; }
    public Object d(Class cls, java.util.Set s) { return null; }
    public Object f(Class cls) { debug("[AppInit-shim] f(" + cls.getName() + ") -> null"); return null; }
    public boolean g(Class cls) { return true; }
    public boolean isEagerlyInitialized(Class c) { return true; }
}
