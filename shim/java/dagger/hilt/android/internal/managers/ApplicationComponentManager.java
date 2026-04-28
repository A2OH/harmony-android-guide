package dagger.hilt.android.internal.managers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Hilt ApplicationComponentManager — stub that returns a dynamic proxy component.
 * The proxy implements any requested interface, returning no-op/null for all methods.
 * This avoids the Dagger DI graph construction which takes minutes in interpreter mode.
 */
public final class ApplicationComponentManager  {
    private volatile Object component;
    /** Obfuscated field — DEX code reads/writes this directly instead of 'component' */
    public volatile Object a;
    /** Obfuscated lock field */
    public final Object b = new Object();
    /** Obfuscated supplier field */
    public final ComponentSupplier c;
    private final ComponentSupplier componentSupplier;

    /** Static reference to the singleton component for cross-manager access */
    public static volatile Object singletonComponent;

    /** InvocationHandler that populates @Inject fields on inject* calls */
    private static final InvocationHandler STUB_HANDLER = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            // inject* methods: populate @Inject fields on the target
            if (name.startsWith("inject") && args != null && args.length == 1 && args[0] != null) {
                ActivityComponentManager.fillNullInterfaceFields(args[0]);
            }
            Class<?> rt = method.getReturnType();
            if (rt == void.class) return null;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (rt == long.class) return 0L;
            if (rt == float.class) return 0.0f;
            if (rt == double.class) return 0.0;
            if (rt.isInterface()) return ActivityComponentManager.createInterfaceProxy(rt);
            return null;
        }
    };

    public ApplicationComponentManager(ComponentSupplier componentSupplier) {
        this.componentSupplier = componentSupplier;
        this.c = componentSupplier;
    }

    /** Obfuscated alias — R8 renames generatedComponent() to a() */
    public Object a() { return generatedComponent(); }

        public Object generatedComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    // Try real Dagger component builder with timeout, fall back to proxy
                    System.err.println("[Hilt] Trying real DI (10s timeout)...");
                    final Object[] realComponent = {null};
                    if (componentSupplier != null) {
                        Thread diThread = new Thread(new Runnable() {
                            public void run() {
                                try { realComponent[0] = componentSupplier.get(); }
                                catch (Throwable t) { System.err.println("[Hilt] Real DI failed: " + t.getMessage()); }
                            }
                        }, "RealDI");
                        diThread.setDaemon(true);
                        diThread.start();
                        try { diThread.join(10000); } catch (InterruptedException ie) {}
                        if (realComponent[0] != null) {
                            // Check if the component can be cast to DataSourceModuleProvider
                            // (transitively through parent interfaces like SingletonC)
                            boolean hasDSP = false;
                            try {
                                Class<?> dsp = Class.forName("com.mcdonalds.mcdcoreapp.common.di.DataSourceModuleProvider");
                                hasDSP = dsp.isInstance(realComponent[0]);
                                System.err.println("[Hilt] Real component instanceof DataSourceModuleProvider: " + hasDSP);
                                if (hasDSP) {
                                    // Test calling v() directly
                                    java.lang.reflect.Method vm = dsp.getMethod("v");
                                    Object result = vm.invoke(realComponent[0]);
                                    System.err.println("[Hilt] DataSourceModuleProvider.v() = " + result);
                                }
                            } catch (Throwable t) {
                                System.err.println("[Hilt] DataSourceModuleProvider check: " + t);
                            }
                            component = realComponent[0];
                            a = component; // sync obfuscated field
                            singletonComponent = component;
                            System.err.println("[Hilt] Real DI completed! Component: " + realComponent[0].getClass().getName());
                            return component;
                        }
                        System.err.println("[Hilt] Real DI timeout/failed — using stub proxy");
                    }
                    // Create proxy implementing the Application's GeneratedInjector
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) cl = ClassLoader.getSystemClassLoader();
                    // Discover all *_GeneratedInjector interfaces
                    java.util.List<Class<?>> ifaces = new java.util.ArrayList<>();
                    // Scan for the Application injector by convention
                    String[] candidates = {
                        "com.mcdonalds.mcdcoreapp.common.di.DataSourceModuleProvider",
                        "com.mcdonalds.app.application.McDMarketApplication_GeneratedInjector",
                        "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity_GeneratedInjector",
                        "com.mcdonalds.mcdcoreapp.common.activity.BaseActivity_GeneratedInjector",
                        "dagger.hilt.android.internal.managers.ActivityComponentManager$ActivityComponentBuilderEntryPoint",
                    };
                    for (String c : candidates) {
                        try { Class<?> iface = cl.loadClass(c); if (iface.isInterface()) ifaces.add(iface); }
                        catch (ClassNotFoundException e) {}
                    }
                    // Also try generic pattern: scan for any loaded *_GeneratedInjector
                    if (ifaces.isEmpty()) ifaces.add(java.io.Serializable.class);
                    try {
                        component = java.lang.reflect.Proxy.newProxyInstance(
                            cl, ifaces.toArray(new Class<?>[0]), STUB_HANDLER);
                        a = component; // sync obfuscated field
                        singletonComponent = component; // make available for DataSourceHelper init
                        System.err.println("[Hilt] Stub proxy implements: " + ifaces);
                    } catch (Throwable t) {
                        System.err.println("[Hilt] Proxy failed: " + t.getMessage());
                        component = new Object();
                        a = component;
                    }
                }
            }
        }
        return component;
    }

    /**
     * Wrap a real DI component in a Proxy that delegates to it AND implements any extra interface.
     * This allows the component to be safely cast to interfaces it doesn't directly implement
     * (e.g., DataSourceModuleProvider) — the proxy returns stub values for unknown methods.
     */
    private static Object wrapComponent(final Object realComponent) {
        // Collect ALL interfaces from the real component
        java.util.Set<Class<?>> ifaces = new java.util.LinkedHashSet<>();
        for (Class<?> c = realComponent.getClass(); c != null; c = c.getSuperclass()) {
            for (Class<?> i : c.getInterfaces()) ifaces.add(i);
        }
        // Also add common McDonald's entry point interfaces that it might be cast to
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();
        String[] extras = {
            "com.mcdonalds.mcdcoreapp.common.di.DataSourceModuleProvider",
            "com.mcdonalds.app.application.McDMarketApplication_GeneratedInjector",
            "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity_GeneratedInjector",
            "com.mcdonalds.mcdcoreapp.common.activity.BaseActivity_GeneratedInjector",
        };
        for (String name : extras) {
            try { Class<?> c = cl.loadClass(name); if (c.isInterface()) ifaces.add(c); }
            catch (ClassNotFoundException e) {}
        }
        if (ifaces.isEmpty()) return realComponent;

        final InvocationHandler delegatingHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // Try the real component first
                try {
                    Method real = realComponent.getClass().getMethod(method.getName(), method.getParameterTypes());
                    return real.invoke(realComponent, args);
                } catch (NoSuchMethodException e) {
                    // Method not on real component — use stub behavior
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getTargetException();
                }
                // inject* methods: fill fields
                if (method.getName().startsWith("inject") && args != null && args.length == 1 && args[0] != null) {
                    ActivityComponentManager.fillNullInterfaceFields(args[0]);
                }
                Class<?> rt = method.getReturnType();
                if (rt == void.class) return null;
                if (rt == boolean.class) return false;
                if (rt == int.class) return 0;
                if (rt == long.class) return 0L;
                if (rt == float.class) return 0f;
                if (rt == double.class) return 0.0;
                if (rt.isInterface()) return ActivityComponentManager.createInterfaceProxy(rt);
                return null;
            }
        };
        try {
            return Proxy.newProxyInstance(cl, ifaces.toArray(new Class<?>[0]), delegatingHandler);
        } catch (Throwable t) {
            System.err.println("[Hilt] wrapComponent failed: " + t.getMessage());
            return realComponent;
        }
    }

    /**
     * Create a dynamic proxy that implements the given interface with no-op methods.
     * Used when the real Dagger component can't be created.
     */
    public static Object createStubProxy(Class<?>... interfaces) {
        try {
            return Proxy.newProxyInstance(
                interfaces[0].getClassLoader(),
                interfaces,
                STUB_HANDLER
            );
        } catch (Exception e) {
            return null;
        }
    }
}
