package dagger.hilt.android.internal.managers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hilt ActivityComponentManager — manages the activity-scoped DI component.
 * Creates a universal Proxy that:
 * 1. Implements ANY interface (via custom ClassLoader trick)
 * 2. When inject*() is called, populates @Inject fields on the target with Proxy stubs
 */
public final class ActivityComponentManager {
    private volatile Object component;
    private final Object activity;
    // Fields matching the DEX's obfuscated layout (a, b, c, d)
    public volatile Object a;
    public final Object b;
    public final android.app.Activity c;
    public final Object d; // GeneratedComponentManager

    /** InvocationHandler for the activity component proxy */
    private static final InvocationHandler COMPONENT_HANDLER = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            // inject* methods: fill null interface fields on the target object
            if (name.startsWith("inject") && args != null && args.length == 1 && args[0] != null) {
                fillNullInterfaceFields(args[0]);
            }
            Class<?> rt = method.getReturnType();
            if (rt == void.class) return null;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (rt == long.class) return 0L;
            if (rt == float.class) return 0f;
            if (rt == double.class) return 0.0;
            // For interface return types, return a proxy of that interface
            if (rt.isInterface()) {
                return createInterfaceProxy(rt);
            }
            return null;
        }
    };

    /** Handler for individual field stub proxies — returns proxies for nested interface calls */
    private static final InvocationHandler FIELD_STUB_HANDLER = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.equals("toString")) return "StubProxy[" + proxy.getClass().getInterfaces()[0].getSimpleName() + "]";
            if (name.equals("hashCode")) return System.identityHashCode(proxy);
            if (name.equals("equals")) return proxy == args[0];
            Class<?> rt = method.getReturnType();
            if (rt == void.class) return null;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (rt == long.class) return 0L;
            if (rt == float.class) return 0f;
            if (rt == double.class) return 0.0;
            if (rt == String.class) return "";
            // Common concrete types that shouldn't return null
            try {
                if (rt.getName().equals("org.json.JSONObject")) return rt.newInstance();
                if (rt.getName().equals("org.json.JSONArray")) return rt.newInstance();
                if (rt.getName().equals("android.os.Bundle")) return rt.newInstance();
                if (java.util.List.class.isAssignableFrom(rt)) return new java.util.ArrayList();
                if (java.util.Map.class.isAssignableFrom(rt)) return new java.util.HashMap();
                if (java.util.Set.class.isAssignableFrom(rt)) return new java.util.HashSet();
            } catch (Throwable t) {}
            // Return proxies for ALL interface return types
            if (rt.isInterface()) return createInterfaceProxy(rt);
            // Try no-arg constructor for concrete types
            try { return rt.getDeclaredConstructor().newInstance(); } catch (Throwable t) {}
            return null;
        }
    };

    /** Handler for the activity component proxy — delegates to singleton + fills inject fields */
    private static final InvocationHandler COMPONENT_HANDLER_V2 = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.equals("toString")) return "ActivityComponentProxy";
            if (name.equals("hashCode")) return 42;
            if (name.equals("equals")) return proxy == args[0];

            System.err.println("[Hilt-PROXY] " + name + "(" + (args != null ? args.length : 0) + " args) → " + method.getReturnType().getSimpleName());
            // inject/x/y/z methods (obfuscated): fill null interface fields AND try singleton delegation
            if (args != null && args.length == 1 && args[0] != null && method.getReturnType() == void.class) {
                // This is likely an inject method (obfuscated name like x, y, z)
                // First try delegating to singleton (which has the real Dagger-generated inject code)
                Object singleton = ApplicationComponentManager.singletonComponent;
                if (singleton != null) {
                    try {
                        Method real = singleton.getClass().getMethod(name, method.getParameterTypes());
                        real.invoke(singleton, args);
                        System.err.println("[Hilt-ACM] Delegated " + name + "() to singleton — SUCCESS");
                        return null;
                    } catch (NoSuchMethodException e) { /* fall through */ }
                    catch (java.lang.reflect.InvocationTargetException e) {
                        System.err.println("[Hilt-ACM] Singleton " + name + "() threw: " + e.getTargetException().getMessage());
                    }
                }
                // Try MembersInjector classes (Dagger generates SplashActivity_MembersInjector)
                try {
                    String actName = args[0].getClass().getName();
                    String miName = actName + "_MembersInjector";
                    ClassLoader acl = args[0].getClass().getClassLoader();
                    Class<?> miClass = acl.loadClass(miName);
                    // MembersInjector has static injectXxx methods
                    for (Method im : miClass.getDeclaredMethods()) {
                        if (java.lang.reflect.Modifier.isStatic(im.getModifiers())
                                && im.getName().startsWith("inject")
                                && im.getParameterTypes().length == 2
                                && im.getParameterTypes()[0].isInstance(args[0])) {
                            // Second param is the value to inject — create proxy for it
                            Class<?> valType = im.getParameterTypes()[1];
                            Object val = valType.isInterface() ? createInterfaceProxy(valType) : null;
                            if (val != null) {
                                im.invoke(null, args[0], val);
                                System.err.println("[Hilt-ACM] MembersInjector: " + im.getName() + " → " + valType.getSimpleName());
                            }
                        }
                    }
                } catch (Throwable miEx) {
                    System.err.println("[Hilt-ACM] MembersInjector failed: " + miEx.getMessage());
                }
                // Fallback: fill null fields generically
                fillNullInterfaceFields(args[0]);
                return null;
            }

            // Try delegating to the singleton component first
            Object singleton = ApplicationComponentManager.singletonComponent;
            if (singleton != null) {
                try {
                    Method real = singleton.getClass().getMethod(name, method.getParameterTypes());
                    return real.invoke(singleton, args);
                } catch (NoSuchMethodException e) { /* fall through */ }
                catch (java.lang.reflect.InvocationTargetException e) { throw e.getTargetException(); }
            }

            Class<?> rt = method.getReturnType();
            if (rt == void.class) return null;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (rt == long.class) return 0L;
            if (rt == float.class) return 0f;
            if (rt == double.class) return 0.0;
            if (rt == String.class) return "";
            if (rt.isInterface()) return createInterfaceProxy(rt);
            return null;
        }
    };

    public ActivityComponentManager(android.app.Activity act) {
        this.activity = act;
        this.c = act;
        this.b = new Object(); // lock
        this.d = null;
        System.err.println("[Hilt] ActivityComponentManager(<Activity>) created for " + (act != null ? act.getClass().getSimpleName() : "null"));
    }

    // Fallback constructor
    public ActivityComponentManager(Object activity) {
        this.activity = activity;
        this.c = (activity instanceof android.app.Activity) ? (android.app.Activity) activity : null;
        this.b = new Object();
        this.d = null;
        System.err.println("[Hilt] ActivityComponentManager(<Object>) created for " + (activity != null ? activity.getClass().getSimpleName() : "null"));
    }

    /**
     * Returns the activity-scoped DI component.
     * Tries to build the real component from the singleton, falls back to universal proxy.
     */
    /** Obfuscated b() — returns SavedStateHandleHolder */
    public SavedStateHandleHolder b() { return new SavedStateHandleHolder(); }

    /** Obfuscated alias — R8 renames generatedComponent() to a() */
    public Object a() {
        System.err.println("[Hilt-ACM] a() called!");
        return generatedComponent();
    }

    public Object generatedComponent() {
        System.err.println("[Hilt-ACM] generatedComponent() called, activity=" + (activity != null ? activity.getClass().getSimpleName() : "null"));
        if (component != null) return component;
        synchronized (this) {
            if (component != null) return component;

            // Try to get the real activity component from singleton
            Object singleton = ApplicationComponentManager.singletonComponent;
            if (singleton != null) {
                // The singleton component may have an ActivityComponentBuilder entry point
                try {
                    // Look for activityComponentBuilder() method
                    for (Method m : singleton.getClass().getMethods()) {
                        if (m.getName().contains("activityComponent") || m.getName().contains("ActivityComponent")) {
                            Object builder = m.invoke(singleton);
                            if (builder != null) {
                                // Try to call activity(Activity) then build()
                                for (Method bm : builder.getClass().getMethods()) {
                                    if (bm.getName().equals("activity") && bm.getParameterTypes().length == 1) {
                                        builder = bm.invoke(builder, activity);
                                    }
                                }
                                for (Method bm : builder.getClass().getMethods()) {
                                    if (bm.getName().equals("build") && bm.getParameterTypes().length == 0) {
                                        Object actComponent = bm.invoke(builder);
                                        if (actComponent != null) {
                                            component = actComponent;
                                            System.err.println("[Hilt] Real ActivityComponent built: " + actComponent.getClass().getName());
                                            return component;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    System.err.println("[Hilt] ActivityComponent build failed: " + t.getMessage());
                }
            }

            // Fallback: create a universal proxy that implements any cast
            component = createUniversalProxy();
            System.err.println("[Hilt] ActivityComponent: using universal proxy");
            return component;
        }
    }

    /**
     * Create a proxy that handles being cast to any interface.
     * When an inject*() method is called on it, populates @Inject fields on the target.
     */
    private Object createUniversalProxy() {
        // Discover all *_GeneratedInjector interfaces for the activity
        List<Class<?>> ifaces = new ArrayList<>();
        // Use the ACTIVITY's classloader — it can find app DEX classes
        ClassLoader cl = activity != null ? activity.getClass().getClassLoader() : Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();

        // Common McDonald's activity injector interfaces
        String[] candidates = {
            "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity_GeneratedInjector",
            "com.mcdonalds.mcdcoreapp.common.activity.BaseActivity_GeneratedInjector",
            "com.mcdonalds.mcdcoreapp.common.activity.McdLauncherActivity_GeneratedInjector",
            "dagger.hilt.android.internal.managers.ActivityComponentManager$ActivityComponentBuilderEntryPoint",
        };
        for (String c : candidates) {
            try { Class<?> iface = cl.loadClass(c); if (iface.isInterface()) ifaces.add(iface); }
            catch (ClassNotFoundException e) {}
        }

        // Also find ALL interfaces of the singleton component (transitively)
        Object singleton = ApplicationComponentManager.singletonComponent;
        if (singleton != null) {
            collectAllInterfaces(singleton.getClass(), ifaces);
        }

        // Log all discovered interfaces
        System.err.println("[Hilt-ACM] Universal proxy interfaces (" + ifaces.size() + "):");
        for (Class<?> i : ifaces) System.err.println("[Hilt-ACM]   " + i.getName());
        if (ifaces.isEmpty()) ifaces.add(java.io.Serializable.class);

        try {
            return Proxy.newProxyInstance(cl, ifaces.toArray(new Class<?>[0]), COMPONENT_HANDLER_V2);
        } catch (Throwable t) {
            System.err.println("[Hilt] Universal proxy failed: " + t);
            // Last resort: bare object
            return new Object() {};
        }
    }

    /** Create a proxy implementing a single interface with stub returns */
    public static Object createInterfaceProxy(Class<?> iface) {
        try {
            if ("com.mcdonalds.mcdcoreapp.helper.interfaces.LocalCacheManagerDataSource"
                    .equals(iface.getName())) {
                return createLocalCacheManagerDataSourceProxy(iface);
            }
            return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{ iface }, FIELD_STUB_HANDLER);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object createLocalCacheManagerDataSourceProxy(final Class<?> iface) {
        try {
            return Proxy.newProxyInstance(
                    iface.getClassLoader(),
                    new Class<?>[]{iface},
                    new InvocationHandler() {
                        private final java.util.Map<String, Object> cache = new java.util.HashMap<>();

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            String name = method.getName();
                            if ("toString".equals(name)) return "StubProxy[LocalCacheManagerDataSource]";
                            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                            if ("equals".equals(name)) return proxy == args[0];

                            String key = (args != null && args.length > 0 && args[0] instanceof String)
                                    ? (String) args[0] : null;
                            if (key != null) {
                                if ("remove".equals(name) || "b".equals(name) || "d".equals(name)
                                        || "q".equals(name)) {
                                    cache.remove(key);
                                    return null;
                                }
                                if ("putString".equals(name) || "putBoolean".equals(name)
                                        || "putBooleanWithExpiry".equals(name) || "putInt".equals(name)
                                        || "putLong".equals(name) || "f".equals(name)
                                        || "c".equals(name) || "g".equals(name) || "i".equals(name)
                                        || "m".equals(name) || "p".equals(name)) {
                                    if (args.length > 1) {
                                        cache.put(key, args[1]);
                                    }
                                    return null;
                                }
                                if ("s".equals(name) || "o".equals(name)) {
                                    Object cached = cache.get(key);
                                    if (args.length > 1 && args[1] instanceof Class<?>) {
                                        Class<?> requested = (Class<?>) args[1];
                                        if (cached != null && requested.isInstance(cached)) {
                                            return cached;
                                        }
                                        // McD immediately check-casts this value to the requested class.
                                        return null;
                                    }
                                    return cached;
                                }
                                if ("e".equals(name)) {
                                    return cache.get(key);
                                }
                                if ("getString".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof String ? cached
                                            : (args.length > 1 ? args[1] : "");
                                }
                                if ("getBoolean".equals(name) || "getBooleanWithExpiry".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Boolean ? cached
                                            : (args.length > 1 ? args[1] : false);
                                }
                                if ("getInt".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Integer ? cached
                                            : (args.length > 1 ? args[1] : 0);
                                }
                                if ("getLong".equals(name)) {
                                    Object cached = cache.get(key);
                                    return cached instanceof Long ? cached
                                            : (args.length > 1 ? args[1] : 0L);
                                }
                            }

                            Class<?> rt = method.getReturnType();
                            if (rt == void.class) return null;
                            if (rt == boolean.class) return false;
                            if (rt == int.class) return 0;
                            if (rt == long.class) return 0L;
                            if (rt == float.class) return 0f;
                            if (rt == double.class) return 0.0;
                            if (rt == String.class) return "";
                            if (java.util.List.class.isAssignableFrom(rt)) return new java.util.ArrayList();
                            if (java.util.Map.class.isAssignableFrom(rt)) return new java.util.HashMap();
                            if (java.util.Set.class.isAssignableFrom(rt)) return new java.util.HashSet();
                            if (rt == Object.class) return null;
                            if (rt.isInterface()) return createInterfaceProxy(rt);
                            try { return rt.getDeclaredConstructor().newInstance(); } catch (Throwable t) {}
                            return null;
                        }
                    });
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Fill all null interface-typed fields in the object and its superclasses with Proxy stubs.
     * This is called when inject*(target) is invoked on the component proxy.
     */
    public static void fillNullInterfaceFields(Object target) {
        int filled = 0;
        Set<String> seen = new HashSet<>();
        try {
            Class<?> cls = target.getClass();
            while (cls != null && cls != Object.class) {
                for (Field f : cls.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    String key = f.getName() + ":" + f.getType().getName();
                    if (seen.contains(key)) continue;
                    seen.add(key);
                    Class<?> type = f.getType();
                    if (type.isPrimitive()) continue;
                    if (type == String.class || type == Object.class) continue;
                    f.setAccessible(true);
                    Object val = f.get(target);
                    if (val != null) continue;
                    String tn = type.getName();
                    // For String fields: set empty string (fixes Kotlin lateinit String properties)
                    if (type == String.class) { f.set(target, ""); filled++; continue; }
                    // For Android widget/view fields: create empty stub views
                    if (target instanceof android.content.Context) {
                        android.content.Context ctx = (android.content.Context) target;
                        if (tn.equals("android.widget.LinearLayout")) { f.set(target, new android.widget.LinearLayout(ctx)); filled++; continue; }
                        if (tn.equals("android.widget.FrameLayout")) { f.set(target, new android.widget.FrameLayout(ctx)); filled++; continue; }
                        if (tn.equals("android.widget.RelativeLayout")) { f.set(target, new android.widget.RelativeLayout(ctx)); filled++; continue; }
                        if (tn.equals("android.widget.ImageView")) { f.set(target, new android.widget.ImageView(ctx)); filled++; continue; }
                        if (tn.equals("android.widget.TextView")) { f.set(target, new android.widget.TextView(ctx)); filled++; continue; }
                        if (tn.equals("android.widget.ScrollView")) { f.set(target, new android.widget.ScrollView(ctx)); filled++; continue; }
                        if (tn.equals("android.view.View")) { f.set(target, new android.view.View(ctx)); filled++; continue; }
                    }
                    // Skip other basic types
                    if (tn.startsWith("java.lang.") || tn.startsWith("java.util.")) continue;
                    // Create proxy for interfaces
                    if (type.isInterface()) {
                        Object proxy = createInterfaceProxy(type);
                        if (proxy != null) { f.set(target, proxy); filled++; }
                    }
                    // For concrete types: try no-arg constructor (Kotlin lateinit fields)
                    else if (!tn.startsWith("android.widget.") && !tn.startsWith("android.view.")) {
                        try {
                            java.lang.reflect.Constructor<?> ctor = type.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            f.set(target, ctor.newInstance());
                            filled++;
                        } catch (Throwable noctor) {
                            // Try with Context parameter
                            try {
                                java.lang.reflect.Constructor<?> ctxCtor = type.getDeclaredConstructor(android.content.Context.class);
                                ctxCtor.setAccessible(true);
                                android.content.Context ctx = (target instanceof android.content.Context) ? (android.content.Context) target : null;
                                if (ctx != null) { f.set(target, ctxCtor.newInstance(ctx)); filled++; }
                            } catch (Throwable noCtxCtor) { /* skip */ }
                        }
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) { /* reflection failure */ }
        if (filled > 0) {
            System.err.println("[Hilt] Injected " + filled + " stub proxies into " + target.getClass().getSimpleName());
        }
    }

    /** Collect ALL interfaces transitively from a class and its superclasses/superinterfaces */
    private static void collectAllInterfaces(Class<?> cls, List<Class<?>> result) {
        if (cls == null || cls == Object.class) return;
        for (Class<?> iface : cls.getInterfaces()) {
            if (!result.contains(iface)) {
                result.add(iface);
                collectAllInterfaces(iface, result); // recurse into super-interfaces
            }
        }
        collectAllInterfaces(cls.getSuperclass(), result);
    }

    /** Entry point interface for getting ActivityComponentBuilder from the singleton component */
    public interface ActivityComponentBuilderEntryPoint {
        Object activityComponentBuilder();
    }
}
