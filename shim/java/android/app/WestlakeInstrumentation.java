package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

import com.westlake.engine.WestlakeLauncher;

/**
 * WestlakeInstrumentation -- enhanced Instrumentation for the Westlake Engine.
 *
 * Extends the existing shim Instrumentation with:
 *   - AppComponentFactory integration (for Hilt DI injection)
 *   - execStartActivity() delegation to WestlakeActivityThread
 *   - Robust error handling (onException returns true to prevent crashes)
 *   - Application lifecycle callbacks dispatch
 *
 * This is the bridge between the activity launch flow and the app's code.
 * In AOSP, Instrumentation is responsible for:
 *   1. Creating Activity instances (newActivity)
 *   2. Calling lifecycle methods (callActivityOnCreate, etc.)
 *   3. Intercepting startActivity calls (execStartActivity)
 *   4. Error handling (onException)
 *
 * The key addition over the base Instrumentation shim is AppComponentFactory
 * support, which enables Hilt dependency injection during activity creation.
 */
public class WestlakeInstrumentation extends Instrumentation {

    private static final String TAG = "WestlakeInstrumentation";

    private static String throwableSummary(Throwable t) {
        if (t == null) {
            return "null";
        }
        String message = null;
        try {
            message = t.getMessage();
        } catch (Throwable ignored) {
        }
        if (message == null || message.isEmpty()) {
            return t.getClass().getName();
        }
        return t.getClass().getName() + ": " + message;
    }

    private static java.lang.reflect.Field findFieldInHierarchy(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private void seedCtorBypassedHiltActivityState(Activity activity) {
        if (activity == null) {
            return;
        }
        boolean isHiltActivity = false;
        for (Class<?> c = activity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            if (c.getName().contains("Hilt_")) {
                isHiltActivity = true;
                break;
            }
        }
        if (!isHiltActivity) {
            return;
        }
        try {
            java.lang.reflect.Field lockField =
                    findFieldInHierarchy(activity.getClass(), "componentManagerLock");
            if (lockField != null && lockField.get(activity) == null) {
                lockField.set(activity, new Object());
            }
            java.lang.reflect.Field injectedField =
                    findFieldInHierarchy(activity.getClass(), "injected");
            if (injectedField != null) {
                injectedField.setBoolean(activity, false);
            }
            log("I", "Seeded ctor-bypassed Hilt activity state for "
                    + activity.getClass().getSimpleName());
        } catch (Throwable t) {
            log("W", "seedCtorBypassedHiltActivityState failed: " + throwableSummary(t));
        }
    }

    private static boolean isMinimalSplashActivity(Activity activity) {
        return activity != null && isMinimalSplashClassName(activity.getClass().getName());
    }

    private static boolean isMinimalSplashClassName(String className) {
        if (className == null) {
            return false;
        }
        final String suffix = "SplashActivity";
        final int classLen = className.length();
        final int suffixLen = suffix.length();
        if (classLen < suffixLen) {
            return false;
        }
        final int offset = classLen - suffixLen;
        for (int i = 0; i < suffixLen; i++) {
            if (className.charAt(offset + i) != suffix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    void finishMinimalSplashActivity(Activity activity, Intent intent) {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr minimal splash finish enter");
            WestlakeLauncher.marker(
                    "PF301 strict Instr minimal splash seedCtorBypassedHiltActivityState skipped");
        } else {
            seedCtorBypassedHiltActivityState(activity);
        }
        if (activity != null && intent != null) {
            try {
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict Instr minimal splash intent set call");
                }
                activity.setIntent(intent);
                if (strictStandalone) {
                    WestlakeLauncher.marker(
                            "PF301 strict Instr minimal splash intent set returned");
                }
            } catch (Throwable t) {
                if (strictStandalone) {
                    WestlakeLauncher.marker(
                            "PF301 strict Instr minimal splash intent set threw");
                }
                try {
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash intent field set call");
                    }
                    activity.mIntent = intent;
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash intent field set returned");
                    }
                } catch (Throwable ignored) {
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash intent field set threw");
                    }
                }
            }
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr minimal splash finish done");
        }
    }

    /** Back-reference to the ActivityThread that owns us. */
    private final WestlakeActivityThread mThread;

    /** Application component factory (may be a Hilt subclass). */
    private AppComponentFactory mFactory;

    // ── Construction ───────────────────────────────────────────────────────

    public WestlakeInstrumentation(WestlakeActivityThread thread) {
        mThread = thread;
    }

    /**
     * Set a custom AppComponentFactory. Called during WestlakeActivityThread.attach()
     * if the app declares one (e.g., via Hilt).
     */
    public void setAppComponentFactory(AppComponentFactory factory) {
        mFactory = factory;
    }

    /**
     * Get the AppComponentFactory. Returns the custom one if set, otherwise
     * falls back to WestlakeActivityThread's factory, or the default.
     */
    private AppComponentFactory getFactory() {
        if (mFactory != null) return mFactory;
        if (mThread != null) {
            AppComponentFactory f = mThread.getAppComponentFactory();
            if (f != null) return f;
        }
        return new AppComponentFactory();
    }

    // ── Activity creation ──────────────────────────────────────────────────

    /**
     * Create a new Activity instance via the AppComponentFactory.
     *
     * Flow:
     *   1. Try AppComponentFactory.instantiateActivity() (enables Hilt injection)
     *   2. Fall back to ClassLoader.loadClass().newInstance()
     *
     * This mirrors AOSP's Instrumentation.newActivity(ClassLoader, String, Intent)
     * which calls getFactory(pkg).instantiateActivity(cl, className, intent).
     *
     * @param cl        The ClassLoader to use for loading the activity class.
     * @param className The fully-qualified class name of the activity.
     * @param intent    The Intent that is launching the activity.
     * @return The new Activity instance.
     */
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Activity activity = null;

        WestlakeLauncher.marker("PF301 strict Instr newActivity entry");
        boolean minimalSplash = isMinimalSplashClassName(className);
        WestlakeLauncher.marker("PF301 strict Instr newActivity classifier ready");
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (!strictStandalone) {
            WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity begin: " + className);
        }
        if (strictStandalone) {
            WestlakeLauncher.marker(minimalSplash
                    ? "PF301 strict Instr newActivity minimal splash"
                    : "PF301 strict Instr newActivity non-splash");
        }

        // Keep splash on the narrowest possible path. The current regression is
        // before onCreate(), inside newActivity(), and the real dashboard work is
        // later. For splash, bypass custom AppComponentFactory/Hilt construction
        // and allocate directly so we preserve the accepted minimal-splash model.
        if (minimalSplash) {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr minimal splash resolve call");
            } else {
                WestlakeLauncher.trace(
                        "[WestlakeInstrumentation] newActivity minimal splash allocate: " + className);
            }
            Class<?> clazz = WestlakeLauncher.resolveAppClassOrNull(className);
            if (strictStandalone) {
                WestlakeLauncher.marker(clazz != null
                        ? "PF301 strict Instr minimal splash resolve nonnull"
                        : "PF301 strict Instr minimal splash resolve null");
            }
            if (clazz == null) {
                try {
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash Class.forName call");
                    }
                    clazz = Class.forName(className, false, cl);
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash Class.forName returned");
                    }
                } catch (Throwable ignored) {
                }
            }
            if (clazz == null) {
                if (strictStandalone && cl == null) {
                    WestlakeLauncher.marker(
                            "PF301 strict Instr minimal splash null cl skip loadClass");
                } else {
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash loadClass call");
                    }
                    clazz = cl.loadClass(className);
                    if (strictStandalone) {
                        WestlakeLauncher.marker(
                                "PF301 strict Instr minimal splash loadClass returned");
                    }
                }
            }
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr minimal splash allocate call");
            }
            activity = allocateActivity(clazz, className, null);
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr minimal splash allocate returned");
            }
            finishMinimalSplashActivity(activity, intent);
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr minimal splash done");
            } else {
                WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity minimal splash done: "
                        + (activity != null ? activity.getClass().getName() : "null"));
            }
            return activity;
        }

        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr default factory path begin");
            try {
                AppComponentFactory factory = getFactory();
                if (factory == null) {
                    factory = new AppComponentFactory();
                }
                boolean customFactory = factory.getClass() != AppComponentFactory.class;
                WestlakeLauncher.marker(customFactory
                        ? "PF301 strict Instr custom factory ready"
                        : "PF301 strict Instr default factory ready");
                activity = factory.instantiateActivity(cl, className, intent);
                WestlakeLauncher.marker(activity != null
                        ? (customFactory
                                ? "PF301 strict Instr custom factory returned activity"
                                : "PF301 strict Instr default factory returned activity")
                        : (customFactory
                                ? "PF301 strict Instr custom factory returned null"
                                : "PF301 strict Instr default factory returned null"));
                if (activity == null) {
                    throw new InstantiationException("Factory returned null for " + className);
                }
                if (intent != null) {
                    try {
                        activity.setIntent(intent);
                    } catch (Throwable ignored) {
                        activity.mIntent = intent;
                    }
                }
                WestlakeLauncher.marker("PF301 strict Instr default factory path returned");
                if (customFactory) {
                    WestlakeLauncher.marker("PF301 strict Instr custom factory path returned");
                }
                return activity;
            } catch (ClassNotFoundException e) {
                WestlakeLauncher.marker("PF301 strict Instr default factory class missing");
                throw e;
            } catch (Throwable t) {
                WestlakeLauncher.marker("PF301 strict Instr default factory path failed");
                InstantiationException wrapped =
                        new InstantiationException("Failed to instantiate " + className
                                + ": " + t);
                wrapped.initCause(t);
                throw wrapped;
            }
        }

        // Try AppComponentFactory first (for Hilt / custom factory support).
        // When the incoming ClassLoader is null, keep the default factory path
        // instead of handing null into an app-specific factory implementation.
        AppComponentFactory factory = cl != null ? getFactory() : new AppComponentFactory();
        if (strictStandalone && cl == null) {
            WestlakeLauncher.marker("PF301 strict Instr null classloader default factory");
        }
        try {
            log("D", "newActivity calling factory.instantiateActivity: " + className
                    + " factory=" + factory.getClass().getName());
            activity = factory.instantiateActivity(cl, className, intent);
            if (activity != null) {
                WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity factory OK: "
                        + activity.getClass().getName());
                log("D", "newActivity via AppComponentFactory: " + className
                        + " -> " + activity.getClass().getName());
            }
        } catch (Exception e) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeInstrumentation] newActivity factory failed", e);
            WestlakeLauncher.trace(
                    "[WestlakeInstrumentation] newActivity factory -> fallback allocate");
            // Fall through to manual creation
        }

        // Fallback: direct class loading
        if (activity == null) {
            WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity fallback allocate: "
                    + className);
            Class<?> clazz = WestlakeLauncher.resolveAppClassOrNull(className);
            if (clazz == null) {
                try {
                    clazz = Class.forName(className, false, cl);
                } catch (Throwable ignored) {
                }
            }
            if (clazz == null) {
                if (cl == null) {
                    throw new InstantiationException(
                            "Failed to instantiate " + className + ": null classloader");
                }
                clazz = cl.loadClass(className);
            }
            WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity fallback class: "
                    + (clazz != null ? clazz.getName() : "null"));
            activity = allocateActivity(clazz, className, null);
            if (activity != null) {
                WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity fallback OK: "
                        + activity.getClass().getName());
            }
        }

        // Set the intent on the activity (setIntent for framework compatibility)
        if (activity != null && intent != null) {
            try { activity.setIntent(intent); } catch (Throwable t) {
                try { activity.mIntent = intent; } catch (Throwable t2) { /* field access blocked */ }
            }
        }

        WestlakeLauncher.trace("[WestlakeInstrumentation] newActivity done: "
                + (activity != null ? activity.getClass().getName() : "null"));
        return activity;
    }

    private Activity allocateActivity(Class<?> clazz, String className, Throwable cause)
            throws InstantiationException, IllegalAccessException {
        final boolean strictStandalone = !WestlakeLauncher.isRealFrameworkFallbackAllowed();
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr allocateActivity entry");
        }
        if (clazz == null) {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr allocateActivity class null");
            }
            InstantiationException wrapped =
                    new InstantiationException("Failed to instantiate " + className + ": null class");
            if (cause != null) {
                wrapped.initCause(cause);
            }
            throw wrapped;
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr allocateActivity class nonnull");
        } else {
            WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity start: "
                    + clazz.getName());
        }
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr allocateActivity native alloc call");
        }
        Object nativeInstance = WestlakeLauncher.tryAllocInstance(clazz);
        if (strictStandalone) {
            WestlakeLauncher.marker("PF301 strict Instr allocateActivity native alloc returned");
        }
        if (nativeInstance instanceof Activity) {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr allocateActivity native alloc cast success");
                WestlakeLauncher.marker("PF301 strict Instr allocateActivity native alloc return");
                return (Activity) nativeInstance;
            }
            WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity native OK");
            log("W", "newActivity via nativeAllocInstance(): " + className);
            return (Activity) nativeInstance;
        }
        if (strictStandalone) {
            WestlakeLauncher.marker(nativeInstance != null
                    ? "PF301 strict Instr allocateActivity native alloc cast null"
                    : "PF301 strict Instr allocateActivity native alloc null");
        }
        WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity native miss: "
                + (nativeInstance != null ? nativeInstance.getClass().getName() : "null"));
        try {
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr allocateActivity sun Unsafe begin");
            }
            WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity sun Unsafe begin");
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            Activity activity = (Activity) unsafeClass.getMethod("allocateInstance", Class.class)
                    .invoke(unsafe, clazz);
            if (strictStandalone) {
                WestlakeLauncher.marker("PF301 strict Instr allocateActivity sun Unsafe returned");
            }
            WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity sun Unsafe OK");
            log("W", "newActivity via Unsafe.allocateInstance(): " + className);
            return activity;
        } catch (Throwable ignored) {
            WestlakeLauncher.dumpThrowable(
                    "[WestlakeInstrumentation] allocateActivity sun Unsafe failed", ignored);
            try {
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict Instr allocateActivity jdk Unsafe begin");
                }
                WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity jdk Unsafe begin");
                Class<?> unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
                java.lang.reflect.Field field = unsafeClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Object unsafe = field.get(null);
                Activity activity = (Activity) unsafeClass.getMethod("allocateInstance", Class.class)
                        .invoke(unsafe, clazz);
                if (strictStandalone) {
                    WestlakeLauncher.marker("PF301 strict Instr allocateActivity jdk Unsafe returned");
                }
                WestlakeLauncher.trace("[WestlakeInstrumentation] allocateActivity jdk Unsafe OK");
                log("W", "newActivity via jdk Unsafe.allocateInstance(): " + className);
                return activity;
            } catch (Throwable ignoredToo) {
                WestlakeLauncher.dumpThrowable(
                        "[WestlakeInstrumentation] allocateActivity jdk Unsafe failed", ignoredToo);
                if (cause instanceof InstantiationException) {
                    throw (InstantiationException) cause;
                }
                if (cause instanceof IllegalAccessException) {
                    throw (IllegalAccessException) cause;
                }
                InstantiationException wrapped =
                        new InstantiationException("Failed to instantiate " + className + ": " + cause);
                if (cause != null) {
                    wrapped.initCause(cause);
                }
                throw wrapped;
            }
        }
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    /**
     * Call Activity.onCreate() with lifecycle callback dispatch.
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        WestlakeLauncher.trace("[WestlakeInstrumentation] callActivityOnCreate begin: "
                + activity.getClass().getName());
        boolean minimalSplash = isMinimalSplashActivity(activity);
        if (!minimalSplash) {
            try {
                WestlakeLauncher.patchProblematicAppClasses(activity.getClass().getClassLoader());
            } catch (Throwable t) {
                log("W", "patchProblematicAppClasses failed: " + throwableSummary(t));
            }
        }
        if (!minimalSplash) {
            try {
                dispatchLifecycleCallback("onActivityPreCreated", activity, icicle);
            } catch (Throwable t) {
                log("W", "onActivityPreCreated failed: " + throwableSummary(t));
            }
            try {
                seedCtorBypassedHiltActivityState(activity);
            } catch (Throwable t) {
                log("W", "seedCtorBypassedHiltActivityState failed: " + throwableSummary(t));
            }
            try {
                maybeInitHilt(activity);
            } catch (Throwable t) {
                log("W", "maybeInitHilt failed: " + throwableSummary(t));
            }
            // ComponentActivity already dispatches OnContextAvailableListeners inside
            // its own onCreate() implementation. Firing the hook here as well can
            // double-invoke obfuscated Hilt/setup callbacks and recurse badly.
            if (!(activity instanceof androidx.activity.ComponentActivity)) {
                try {
                    maybeFireContextAvailable(activity);
                } catch (Throwable t) {
                    log("W", "maybeFireContextAvailable failed: " + throwableSummary(t));
                }
            }
        }

        Throwable onCreateFailure = null;
        try {
            WestlakeLauncher.trace("[WestlakeInstrumentation] invoking Activity.onCreate: "
                    + activity.getClass().getName());
            activity.onCreate(icicle);
        } catch (Throwable firstEx) {
            onCreateFailure = firstEx;
            log("W", "onCreate initial failure: " + throwableSummary(firstEx));
        }

        if (minimalSplash) {
            WestlakeLauncher.trace("[WestlakeInstrumentation] callActivityOnCreate done: "
                    + activity.getClass().getName());
            return;
        }

        if (onCreateFailure != null) {
            try {
                if (hasDecorChildren(activity)) {
                    log("I", "Content view already set, running post-crash setup");
                    runPostCrashSetup(activity);
                    resolveImageDrawables(activity);
                } else {
                    recoverAfterOnCreateFailure(activity);
                }
            } catch (Throwable recoveryFailure) {
                log("W", "onCreate recovery failed: " + throwableSummary(recoveryFailure));
            }
        }

        try {
            maybeInstallFallbackContent(activity);
        } catch (Throwable t) {
            log("W", "fallback content failed: " + throwableSummary(t));
        }

        try {
            dispatchLifecycleCallback("onActivityCreated", activity, icicle);
        } catch (Throwable t) {
            log("W", "onActivityCreated failed: " + throwableSummary(t));
        }
        try {
            dispatchLifecycleCallback("onActivityPostCreated", activity, icicle);
        } catch (Throwable t) {
            log("W", "onActivityPostCreated failed: " + throwableSummary(t));
        }
        WestlakeLauncher.trace("[WestlakeInstrumentation] callActivityOnCreate done: "
                + activity.getClass().getName());
    }

    /**
     * Call Activity.onCreate() with PersistableBundle.
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle,
                                      PersistableBundle persistentState) {
        dispatchLifecycleCallback("onActivityPreCreated", activity, icicle);
        activity.onCreate(icicle, persistentState);
        dispatchLifecycleCallback("onActivityCreated", activity, icicle);
        dispatchLifecycleCallback("onActivityPostCreated", activity, icicle);
    }

    private void maybeInitHilt(Activity activity) {
        try {
            java.lang.reflect.Method hiltInit = activity.getClass().getDeclaredMethod("_initHiltInternal");
            hiltInit.setAccessible(true);
            hiltInit.invoke(activity);
            log("I", "Hilt _initHiltInternal() invoked for " + activity.getClass().getSimpleName());
        } catch (NoSuchMethodException ignored) {
        } catch (Throwable t) {
            log("W", "Hilt init failed: " + t.getMessage());
        }
    }

    private void maybeFireContextAvailable(Activity activity) {
        try {
            for (java.lang.reflect.Method m : safeGetMethods(activity.getClass())) {
                if (m.getName().equals("a")
                        && m.getParameterTypes().length == 1
                        && m.getParameterTypes()[0] == android.content.Context.class) {
                    m.invoke(activity, activity);
                    log("I", "OnContextAvailable fired");
                    break;
                }
            }
        } catch (Throwable t) {
            log("W", "OnContextAvailable fire failed: " + t.getMessage());
        }
    }

    private boolean hasDecorChildren(Activity activity) {
        try {
            android.view.View decor = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            return decor instanceof android.view.ViewGroup
                    && ((android.view.ViewGroup) decor).getChildCount() > 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private android.view.ViewGroup findPageContentContainer(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            int pageContentId = resolveResourceId(activity, "id", "page_content");
            android.view.View pageContentView =
                    pageContentId != 0 ? activity.findViewById(pageContentId) : null;
            if (pageContentView instanceof android.view.ViewGroup) {
                return (android.view.ViewGroup) pageContentView;
            }
        } catch (Throwable t) {
            log("W", "findPageContentContainer failed: " + throwableSummary(t));
        }
        return null;
    }

    private boolean ensureMcdToolbarShell(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            int toolbarId = resolveResourceId(activity, "id", "toolbar");
            int basketLayoutId = resolveResourceId(activity, "id", "basket_layout");
            int pageRootId = resolveResourceId(activity, "id", "page_root");

            android.view.View toolbarView = toolbarId != 0 ? activity.findViewById(toolbarId) : null;
            if (basketLayoutId != 0 && toolbarView != null
                    && toolbarView.findViewById(basketLayoutId) != null) {
                return true;
            }

            com.mcdonalds.mcduikit.widget.McDToolBarView replacement =
                    new com.mcdonalds.mcduikit.widget.McDToolBarView(activity);
            if (toolbarId != 0) {
                replacement.setId(toolbarId);
            }

            android.view.ViewGroup.LayoutParams replacementLp = null;
            android.view.ViewGroup parent = null;
            int index = 0;
            if (toolbarView != null) {
                android.view.ViewParent rawParent = toolbarView.getParent();
                if (rawParent instanceof android.view.ViewGroup) {
                    parent = (android.view.ViewGroup) rawParent;
                    replacementLp = toolbarView.getLayoutParams();
                    index = parent.indexOfChild(toolbarView);
                    parent.removeView(toolbarView);
                }
            } else {
                android.view.View pageRoot = pageRootId != 0 ? activity.findViewById(pageRootId) : null;
                if (pageRoot != null) {
                    android.view.ViewParent rawParent = pageRoot.getParent();
                    if (rawParent instanceof android.view.ViewGroup) {
                        parent = (android.view.ViewGroup) rawParent;
                        index = parent.indexOfChild(pageRoot);
                    }
                }
            }

            if (parent == null) {
                return false;
            }
            if (replacementLp == null) {
                replacementLp = new android.widget.LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            parent.addView(replacement, index, replacementLp);
            return basketLayoutId != 0 && replacement.findViewById(basketLayoutId) != null;
        } catch (Throwable t) {
            log("W", "ensureMcdToolbarShell failed: " + throwableSummary(t));
            return false;
        }
    }

    private boolean ensureStructuredPageShell(Activity activity) {
        if (activity == null) {
            return false;
        }
        try {
            if (findPageContentContainer(activity) == null) {
                int baseLayoutId = resolveResourceId(activity, "layout", "base_layout");
                if (baseLayoutId != 0) {
                    activity.setContentView(baseLayoutId);
                    log("I", "Installed base_layout shell 0x"
                            + Integer.toHexString(baseLayoutId));
                }
            }
            android.view.ViewGroup pageContent = findPageContentContainer(activity);
            if (pageContent == null) {
                return false;
            }
            ensureMcdToolbarShell(activity);
            return true;
        } catch (Throwable t) {
            log("W", "ensureStructuredPageShell failed: " + throwableSummary(t));
            return false;
        }
    }

    private boolean inflateIntoPageContent(Activity activity, int contentLayoutId) {
        if (activity == null || contentLayoutId == 0) {
            return false;
        }
        try {
            android.view.ViewGroup pageContent = findPageContentContainer(activity);
            if (pageContent == null) {
                return false;
            }
            if (pageContent.getChildCount() > 0) {
                pageContent.removeAllViews();
            }
            activity.getLayoutInflater().inflate(contentLayoutId, pageContent, true);
            return true;
        } catch (Throwable t) {
            log("W", "inflateIntoPageContent failed: " + throwableSummary(t));
            return false;
        }
    }

    private void maybeInstallFallbackContent(Activity activity) {
        if (activity == null || hasDecorChildren(activity)) {
            return;
        }
        String actName = activity.getClass().getSimpleName().toLowerCase();
        if (!(actName.contains("dashboard") || actName.contains("home"))) {
            return;
        }
        WestlakeLauncher.populateDashboardFallback(activity);
        if (hasDecorChildren(activity)) {
            log("I", "Installed fallback dashboard for " + activity.getClass().getSimpleName());
            resolveImageDrawables(activity);
        } else {
            log("W", "Fallback dashboard install left decor empty for "
                    + activity.getClass().getSimpleName());
        }
    }

    private void recoverAfterOnCreateFailure(Activity activity) {
        try {
            if (recoverUsingStructuredPageLayouts(activity)) {
                resolveImageDrawables(activity);
                return;
            }

            int layoutId = invokeIntMethod(activity, "getContentPageLayoutId");
            String actName = activity.getClass().getSimpleName().toLowerCase();
            android.content.res.Resources res = activity.getResources();
            if (res != null) {
                String[] tries;
                if (layoutId != 0) {
                    tries = new String[0];
                } else if (actName.contains("dashboard") || actName.contains("home")) {
                    tries = new String[]{"activity_home_dashboard", "activity_dashboard_new", "activity_base"};
                } else if (actName.contains("splash")) {
                    tries = new String[]{"activity_splash_screen"};
                } else {
                    tries = new String[]{
                        "activity_" + actName.replace("activity", "").trim(),
                        "activity_base"
                    };
                }
                for (String name : tries) {
                    int id = resolveResourceId(activity, "layout", name);
                    if (id != 0) {
                        layoutId = id;
                        log("I", "Found layout '" + name + "' -> 0x" + Integer.toHexString(id));
                        break;
                    }
                }
            }
            if (layoutId == 0) {
                layoutId = 0x7f0e0530; // toolbar_close_back, true last-resort fallback
            }
            boolean structuredInflate = ensureStructuredPageShell(activity)
                    && inflateIntoPageContent(activity, layoutId);
            if (structuredInflate) {
                log("I", "Recovered page_content inflate(0x"
                        + Integer.toHexString(layoutId) + ") for "
                        + activity.getClass().getSimpleName());
            } else {
                activity.setContentView(layoutId);
                log("I", "Recovered setContentView(0x" + Integer.toHexString(layoutId)
                        + ") for " + activity.getClass().getSimpleName());
            }
            resolveImageDrawables(activity);
            maybeInstallFallbackContent(activity);
            try {
                java.lang.reflect.Method lh = activity.getClass().getDeclaredMethod("launchHome");
                lh.setAccessible(true);
                lh.invoke(activity);
                log("I", "launchHome() succeeded");
            } catch (Throwable lhEx) {
                log("W", "launchHome failed: " + lhEx);
                WestlakeActivityThread.pendingDashboardClass =
                        "com.mcdonalds.homedashboard.activity.HomeDashboardActivity";
                log("I", "Dashboard navigation queued");
            }
        } catch (Throwable recoverEx) {
            log("W", "Recovery failed: " + recoverEx.getMessage());
        }
    }

    private boolean recoverUsingStructuredPageLayouts(Activity activity) {
        boolean recovered = false;
        try {
            java.lang.reflect.Method setPage = findMethodInHierarchy(activity.getClass(), "setPageLayout");
            if (setPage != null) {
                setPage.setAccessible(true);
                setPage.invoke(activity);
                recovered = ensureStructuredPageShell(activity) || hasDecorChildren(activity);
                log("I", "recovery setPageLayout() done");
            }
        } catch (Throwable t) {
            log("W", "recovery setPageLayout failed: " + throwableSummary(t));
        }
        try {
            java.lang.reflect.Method setView = findMethodInHierarchy(activity.getClass(), "setPageView");
            if (setView != null) {
                ensureStructuredPageShell(activity);
                setView.setAccessible(true);
                setView.invoke(activity);
                recovered = hasDecorChildren(activity) || recovered;
                log("I", "recovery setPageView() done");
            }
        } catch (Throwable t) {
            log("W", "recovery setPageView failed: " + throwableSummary(t));
        }

        int pageContentId = resolveResourceId(activity, "id", "page_content");
        int contentLayoutId = invokeIntMethod(activity, "getContentPageLayoutId");
        if (pageContentId != 0 && contentLayoutId != 0) {
            try {
                if (ensureStructuredPageShell(activity)
                        && inflateIntoPageContent(activity, contentLayoutId)) {
                    recovered = true;
                    log("I", "recovery inflated content layout 0x"
                            + Integer.toHexString(contentLayoutId)
                            + " into page_content");
                }
            } catch (Throwable t) {
                log("W", "recovery content inflate failed: " + throwableSummary(t));
            }
        }

        String actName = activity.getClass().getSimpleName().toLowerCase();
        if (actName.contains("dashboard") || actName.contains("home")) {
            int containerId = invokeIntMethod(activity, "getFragmentContainerId");
            if (containerId == 0) {
                containerId = resolveResourceId(activity, "id", "home_dashboard_container");
            }
            try {
                android.view.View containerView = activity.findViewById(containerId);
                if (containerView != null) {
                    containerView.setVisibility(android.view.View.VISIBLE);
                }
                int intermediateId = resolveResourceId(activity, "id", "intermediate_layout_container");
                android.view.View intermediateView = activity.findViewById(intermediateId);
                if (intermediateView != null) {
                    intermediateView.setVisibility(android.view.View.GONE);
                }
                if (containerView instanceof android.view.ViewGroup) {
                    android.view.ViewGroup container = (android.view.ViewGroup) containerView;
                    if (container.getChildCount() == 0) {
                        int fragmentLayoutId = resolveResourceId(activity, "layout", "fragment_home_dashboard");
                        if (fragmentLayoutId != 0) {
                            activity.getLayoutInflater().inflate(fragmentLayoutId, container, true);
                            recovered = true;
                            log("I", "recovery inflated fragment_home_dashboard into container 0x"
                                    + Integer.toHexString(containerId));
                        }
                    }
                }
            } catch (Throwable t) {
                log("W", "recovery dashboard container inflate failed: " + throwableSummary(t));
            }
        }
        return recovered && hasDecorChildren(activity);
    }

    private int resolveResourceId(Activity activity, String type, String name) {
        if (activity == null || type == null || name == null) {
            return 0;
        }
        try {
            android.content.res.Resources res = activity.getResources();
            if (res == null) {
                return 0;
            }
            String[] packages = {
                    activity.getPackageName(),
                    "com.mcdonalds.app",
                    "com.mcdonalds.homedashboard"
            };
            for (int i = 0; i < packages.length; i++) {
                String pkg = packages[i];
                if (pkg == null || pkg.isEmpty()) {
                    continue;
                }
                int id = res.getIdentifier(name, type, pkg);
                if (id != 0) {
                    return id;
                }
            }
        } catch (Throwable t) {
            log("W", "resolveResourceId(" + type + "/" + name + ") failed: "
                    + throwableSummary(t));
        }
        return 0;
    }

    private int invokeIntMethod(Activity activity, String name) {
        if (activity == null || name == null) {
            return 0;
        }
        try {
            java.lang.reflect.Method method = findMethodInHierarchy(activity.getClass(), name);
            if (method == null) {
                return 0;
            }
            method.setAccessible(true);
            Object value = method.invoke(activity);
            if (value instanceof Integer) {
                return ((Integer) value).intValue();
            }
        } catch (Throwable t) {
            log("W", name + "() failed: " + throwableSummary(t));
        }
        return 0;
    }

    /**
     * Call Activity.onStart().
     */
    @Override
    public void callActivityOnStart(Activity activity) {
        activity.mStarted = true;
        activity.onStart();
    }

    /**
     * Call Activity.onResume() and onPostResume().
     */
    @Override
    public void callActivityOnResume(Activity activity) {
        activity.mResumed = true;
        activity.onResume();
        activity.onPostResume();
    }

    /**
     * Call Activity.onPause().
     */
    @Override
    public void callActivityOnPause(Activity activity) {
        activity.mResumed = false;
        activity.onPause();
    }

    /**
     * Call Activity.onStop().
     */
    @Override
    public void callActivityOnStop(Activity activity) {
        activity.mStarted = false;
        activity.onStop();
    }

    /**
     * Call Activity.onDestroy().
     */
    @Override
    public void callActivityOnDestroy(Activity activity) {
        activity.mDestroyed = true;
        activity.onDestroy();
    }

    /**
     * Call Activity.onSaveInstanceState().
     */
    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        activity.onSaveInstanceState(outState);
    }

    /**
     * Call Activity.onRestoreInstanceState().
     */
    @Override
    public void callActivityOnRestoreInstanceState(Activity activity,
                                                     Bundle savedInstanceState) {
        activity.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Call Activity.onNewIntent().
     */
    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        activity.onNewIntent(intent);
    }

    /**
     * Call Application.onCreate().
     */
    @Override
    public void callApplicationOnCreate(Application app) {
        app.onCreate();
    }

    // ── Exception handling ─────────────────────────────────────────────────

    /**
     * Handle an exception from the application.
     *
     * In AOSP, returning false causes the exception to propagate (crash).
     * In Westlake, we log the error and return true to keep running.
     * This prevents a single bad Activity from taking down the whole engine.
     *
     * @param obj The object that threw the exception (Activity, Service, etc.).
     * @param e   The exception.
     * @return true to suppress the exception, false to propagate it.
     */
    @Override
    public boolean onException(Object obj, Throwable e) {
        String objTag = obj != null ? obj.getClass().getName() : "null";
        String errTag = e != null ? e.getClass().getName() : "null";
        try {
            WestlakeLauncher.trace("[WestlakeInstrumentation] onException obj="
                    + objTag + " err=" + errTag);
        } catch (Throwable ignored) {
        }
        // Return true = exception handled, don't crash
        return true;
    }

    // ── Activity start interception ────────────────────────────────────────

    /**
     * Intercept startActivity calls. In AOSP, this calls into
     * ActivityTaskManagerService via Binder IPC. In Westlake, we delegate
     * directly to WestlakeActivityThread.
     *
     * This is called by Activity.startActivityForResult() in the shim.
     */
    public void execStartActivity(Context who, Activity caller,
                                   Intent intent, int requestCode,
                                   Bundle options) {
        if (mThread != null) {
            mThread.startActivityFromActivity(caller, intent, requestCode);
        } else {
            log("W", "execStartActivity: no ActivityThread, falling back to MiniServer");
            try {
                MiniServer.get().startActivity(intent);
            } catch (Exception e) {
                log("E", "execStartActivity fallback failed: " + e);
            }
        }
    }

    /**
     * AOSP-compatible overload: execStartActivity with IBinder parameters.
     * Adapts to our simplified signature.
     */
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token,
            Activity target, Intent intent, int requestCode,
            Bundle options) {
        execStartActivity(who, target, intent, requestCode, options);
        return null;
    }

    // ── Lifecycle callback dispatch ────────────────────────────────────────

    /**
     * Dispatch Application.ActivityLifecycleCallbacks for the given event.
     * This enables libraries like Hilt and AndroidX Lifecycle to observe
     * activity lifecycle events.
     */
    private void dispatchLifecycleCallback(String callbackName,
                                            Activity activity, Bundle bundle) {
        Application app = activity.getApplication();
        if (app == null && mThread != null) {
            app = mThread.getApplication();
        }
        if (app == null) return;

        // Get the callbacks list via reflection (it's package-private in the shim)
        java.util.List<Application.ActivityLifecycleCallbacks> callbacks = null;
        try {
            java.lang.reflect.Field f = Application.class.getDeclaredField("mCallbacks");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Application.ActivityLifecycleCallbacks> list =
                    (java.util.List<Application.ActivityLifecycleCallbacks>) f.get(app);
            if (list != null && !list.isEmpty()) {
                // Copy to avoid ConcurrentModificationException
                callbacks = new java.util.ArrayList<>(list);
            }
        } catch (Exception e) {
            // Field not found or access error -- skip
            return;
        }

        if (callbacks == null || callbacks.isEmpty()) return;

        for (Application.ActivityLifecycleCallbacks cb : callbacks) {
            try {
                switch (callbackName) {
                    case "onActivityPreCreated":
                        cb.onActivityPreCreated(activity, bundle);
                        break;
                    case "onActivityCreated":
                        cb.onActivityCreated(activity, bundle);
                        break;
                    case "onActivityPostCreated":
                        cb.onActivityPostCreated(activity, bundle);
                        break;
                    case "onActivityStarted":
                        cb.onActivityStarted(activity);
                        break;
                    case "onActivityResumed":
                        cb.onActivityResumed(activity);
                        break;
                    case "onActivityPaused":
                        cb.onActivityPaused(activity);
                        break;
                    case "onActivityStopped":
                        cb.onActivityStopped(activity);
                        break;
                    case "onActivityDestroyed":
                        cb.onActivityDestroyed(activity);
                        break;
                    case "onActivitySaveInstanceState":
                        cb.onActivitySaveInstanceState(activity, bundle);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log("W", "Lifecycle callback " + callbackName + " threw: " + e);
            }
        }
    }

    // ── ActivityResult inner class (AOSP compatibility) ────────────────────

    /**
     * Minimal ActivityResult holder. In AOSP this is Instrumentation.ActivityResult.
     * Included here so execStartActivity() can return it.
     */
    public static class ActivityResult {
        private final int mResultCode;
        private final Intent mResultData;

        public ActivityResult(int resultCode, Intent resultData) {
            mResultCode = resultCode;
            mResultData = resultData;
        }

        public int getResultCode() { return mResultCode; }
        public Intent getResultData() { return mResultData; }
    }

    // ── Logging ────────────────────────────────────────────────────────────

    private static void log(String level, String msg) {
        try {
            WestlakeLauncher.trace("[" + TAG + "/" + level + "] " + msg);
        } catch (Throwable ignored) {
        }
    }

    private static java.lang.reflect.Method[] safeGetDeclaredMethods(Class<?> cls) {
        if (cls == null) {
            return new java.lang.reflect.Method[0];
        }
        try {
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            return methods != null ? methods : new java.lang.reflect.Method[0];
        } catch (Throwable ignored) {
            return new java.lang.reflect.Method[0];
        }
    }

    private static java.lang.reflect.Method[] safeGetMethods(Class<?> cls) {
        if (cls == null) {
            return new java.lang.reflect.Method[0];
        }
        try {
            java.lang.reflect.Method[] methods = cls.getMethods();
            return methods != null ? methods : new java.lang.reflect.Method[0];
        } catch (Throwable ignored) {
            return new java.lang.reflect.Method[0];
        }
    }

    /**
     * After onCreate() crashes partway through, run the remaining BaseActivity setup steps.
     * BaseActivity.onCreate() calls these after clearV1OrderData():
     *   - inflateDefaultLayout() → setPageLayout() → setPageView() → initPageListeners()
     *   - checkForInAppNotification()
     *   - setActivityAccessibilityTitle()
     */
    private void runPostCrashSetup(Activity activity) {
        Class<?> cls = activity.getClass();
        boolean layoutDone = false;
        // Try inflateDefaultLayout → setPageLayout → setPageView → initPageListeners
        try {
            java.lang.reflect.Method inflateDefault = findMethodInHierarchy(cls, "inflateDefaultLayout");
            boolean shouldInflate = true;
            if (inflateDefault != null) {
                inflateDefault.setAccessible(true);
                shouldInflate = (Boolean) inflateDefault.invoke(activity);
                log("I", "inflateDefaultLayout() = " + shouldInflate);
            }
            if (shouldInflate) {
                // setPageLayout: calls setContentView(R.layout.base_layout) in McDBaseActivity
                java.lang.reflect.Method setPage = findMethodInHierarchy(cls, "setPageLayout");
                if (setPage != null) {
                    setPage.setAccessible(true);
                    setPage.invoke(activity);
                    layoutDone = true;
                    log("I", "setPageLayout() done");
                }
                // setPageView: finds toolbar views — will NPE on missing navigation/toolbar
                try {
                    java.lang.reflect.Method setView = findMethodInHierarchy(cls, "setPageView");
                    if (setView != null) {
                        setView.setAccessible(true);
                        setView.invoke(activity);
                        log("I", "setPageView() done");
                    }
                } catch (Throwable sv) {
                    log("W", "setPageView() threw: " +
                        (sv.getCause() != null ? sv.getCause().getMessage() : sv.getMessage()));
                }
                // initPageListeners
                try {
                    java.lang.reflect.Method initListeners = findMethodInHierarchy(cls, "initPageListeners");
                    if (initListeners != null) {
                        initListeners.setAccessible(true);
                        initListeners.invoke(activity);
                    }
                } catch (Throwable t) {}
            }
        } catch (Throwable t) {
            log("W", "runPostCrashSetup layout: " + t.getMessage());
            if (t.getCause() != null) log("W", "runPostCrashSetup cause: " + t.getCause().getMessage());
        }
        // Apply McD styling to the inflated layout
        if (layoutDone) {
            applyMcDStyling(activity);
        }
    }

    /** Apply McDonald's visual styling to the view tree after layout inflation */
    private void applyMcDStyling(Activity activity) {
        try {
            android.view.View decor = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            if (decor == null) return;

            // Clear all backgrounds first — prevents black overlays from XML backgrounds
            clearBlackBackgrounds(decor);
            // Set root to white (content area), toolbar gets set dark separately
            decor.setBackgroundColor(0xFFFFFFFF);

            // Find and fix toolbar height + McD dark background
            android.view.View toolbar = activity.findViewById(0x7f0b1965); // R.id.toolbar
            if (toolbar != null) {
                android.view.ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                if (lp != null && lp.height < 50) {
                    lp.height = 112;
                    toolbar.setLayoutParams(lp);
                    log("I", "Fixed toolbar height to 112px");
                }
                toolbar.setBackgroundColor(0xFF27251F); // McD dark
                // Also set dark on all toolbar children (ImageView backgrounds cover the toolbar bg)
                if (toolbar instanceof android.view.ViewGroup) {
                    setBackgroundRecursive((android.view.ViewGroup) toolbar, 0xFF27251F);
                }
            }

            // Find and fix BottomNavigationView — add tab labels
            android.view.View nav = findViewByType(decor, "BottomNavigationView");
            if (nav instanceof android.view.ViewGroup) {
                android.view.ViewGroup navGroup = (android.view.ViewGroup) nav;
                android.view.ViewGroup.LayoutParams nlp = nav.getLayoutParams();
                if (nlp != null) { nlp.height = 112; nlp.width = 480; nav.setLayoutParams(nlp); }
                nav.setBackgroundColor(0xFF27251F);
                // Add McD bottom nav tab labels
                String[] tabs = {"Home", "Offers", "Rewards", "Order", "More"};
                for (String tab : tabs) {
                    android.widget.TextView tv = new android.widget.TextView(activity);
                    tv.setText(tab);
                    tv.setTextColor(0xFFBBBBBB);
                    tv.setTextSize(9);
                    tv.setGravity(android.view.Gravity.CENTER);
                    android.widget.LinearLayout.LayoutParams tlp =
                        new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                    tv.setLayoutParams(tlp);
                    tv.setPadding(0, 40, 0, 0);
                    navGroup.addView(tv);
                }
                // Force layout at bottom of screen (override LinearLayout stacking)
                nav.layout(0, 688, 480, 800); // 800 - 112 = 688
                log("I", "Fixed navigation at bottom (688-800)");
            }
            // Add golden arches centered in toolbar
            if (toolbar instanceof android.view.ViewGroup) {
                android.view.View archesV = activity.findViewById(0x7f0b0eed); // toolbarCenterImageIcon
                android.widget.ImageView arches = (archesV instanceof android.widget.ImageView) ? (android.widget.ImageView) archesV : null;
                if (arches != null) {
                    try {
                        android.graphics.drawable.Drawable d = activity.getDrawable(0x7f0800fd); // archus.webp
                        if (d != null) {
                            arches.setImageDrawable(d);
                            arches.setBackgroundColor(0x00000000); // transparent
                            android.view.ViewGroup.LayoutParams alp = arches.getLayoutParams();
                            if (alp != null) { alp.width = 96; alp.height = 84; arches.setLayoutParams(alp); }
                        }
                    } catch (Throwable t) {}
                }
            }
            // Populate the REAL McD layout's content containers with real captured data.
            // Real layout: McDToolBarView + FrameLayout(5 LinearLayouts as fragment containers) + BottomNav
            // Find the first large LinearLayout (fragment container, id=0x7f0b0b83) and inject menu items.
            if (decor instanceof android.view.ViewGroup) {
                // Known McD fragment container IDs
                int[] containerIds = {0x7f0b0b83, 0x7f0b0ae8, 0x7f0b17b1, 0x7f0b17b2, 0x7f0b03f2};
                android.view.View container = null;
                for (int cid : containerIds) {
                    container = ((android.view.ViewGroup) decor).findViewById(cid);
                    if (container != null) break;
                }
                if (container instanceof android.view.ViewGroup) {
                    log("I", "Found real fragment container: id=0x"
                        + Integer.toHexString(container.getId()) + " " + container.getMeasuredWidth() + "x" + container.getMeasuredHeight());
                    // Force container to fill parent
                    android.view.ViewGroup.LayoutParams clp = container.getLayoutParams();
                    if (clp != null) {
                        clp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                        clp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                        container.setLayoutParams(clp);
                    }
                    populateRealMenuData(activity, (android.view.ViewGroup) container);
                } else {
                    log("I", "No fragment container found, trying decor root");
                    populateRealMenuData(activity, (android.view.ViewGroup) decor);
                }
            }
        } catch (Throwable t) {
            log("W", "applyMcDStyling: " + t.getMessage());
        }
    }

    /** Find the first FrameLayout in the real layout that has no children (fragment container). */
    private android.widget.FrameLayout findFirstEmptyFrameLayout(android.view.ViewGroup root) {
        // Search for FrameLayout with known McD content holder IDs or first empty one
        for (int i = 0; i < root.getChildCount(); i++) {
            android.view.View child = root.getChildAt(i);
            if (child instanceof android.widget.FrameLayout) {
                android.widget.FrameLayout fl = (android.widget.FrameLayout) child;
                // Skip tiny or zero-height frames
                if (fl.getChildCount() == 0 && fl.getMeasuredHeight() > 100) {
                    return fl;
                }
                // Recurse into FrameLayouts that have children
                android.widget.FrameLayout found = findFirstEmptyFrameLayout(fl);
                if (found != null) return found;
            } else if (child instanceof android.view.ViewGroup) {
                android.widget.FrameLayout found = findFirstEmptyFrameLayout((android.view.ViewGroup) child);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Populate a real McD content container with menu data read from captured API responses. */
    private void populateRealMenuData(android.app.Activity activity, android.view.ViewGroup container) {
        // Read menu data from captured JSON on disk
        String[][] menuItems = loadMenuFromCapturedData();
        if (menuItems == null || menuItems.length == 0) {
            log("I", "No captured menu data found, using fallback");
            menuItems = new String[][] {
                {"Big Mac\u00AE", "$5.99"}, {"Quarter Pounder\u00AE with Cheese", "$6.19"},
                {"10 pc. Chicken McNuggets\u00AE", "$5.49"}, {"McChicken\u00AE", "$2.49"},
                {"Big Arch\u2122 Burger", "$7.49"}, {"Double Quarter Pounder\u00AE", "$7.79"},
                {"Filet-O-Fish\u00AE", "$5.49"}, {"McCrispy\u2122", "$5.99"},
                {"Cheeseburger", "$2.49"}, {"McDouble\u00AE", "$3.19"},
            };
        }

        android.widget.ScrollView scrollView = new android.widget.ScrollView(activity);
        android.widget.LinearLayout list = new android.widget.LinearLayout(activity);
        list.setOrientation(android.widget.LinearLayout.VERTICAL);
        list.setBackgroundColor(0xFFFFFFFF);
        scrollView.addView(list);

        for (String[] item : menuItems) {
            android.widget.LinearLayout row = new android.widget.LinearLayout(activity);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setPadding(20, 14, 20, 14);

            android.widget.TextView nameView = new android.widget.TextView(activity);
            nameView.setText(item[0]);
            nameView.setTextColor(0xFF27251F);
            nameView.setTextSize(13);
            android.widget.LinearLayout.LayoutParams nlp =
                new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            nameView.setLayoutParams(nlp);
            row.addView(nameView);

            android.widget.TextView priceView = new android.widget.TextView(activity);
            priceView.setText(item[1]);
            priceView.setTextColor(0xFFDA291C);
            priceView.setTextSize(13);
            row.addView(priceView);

            list.addView(row);

            android.view.View divider = new android.view.View(activity);
            divider.setBackgroundColor(0xFFEEEEEE);
            divider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
            list.addView(divider);
        }

        container.addView(scrollView, new android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        log("I", "Populated " + menuItems.length + " real menu items into content container");
    }

    /** Load menu items from captured McDonald's API JSON file on device. */
    private String[][] loadMenuFromCapturedData() {
        try {
            String path = "/data/local/tmp/westlake/mock-backend/menu.json";
            java.io.File f = new java.io.File(path);
            if (!f.exists()) return null;

            byte[] data = new byte[(int) f.length()];
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            int off = 0;
            while (off < data.length) { int n = fis.read(data, off, data.length - off); if (n <= 0) break; off += n; }
            fis.close();

            String json = new String(data, 0, off, "UTF-8");
            // Simple JSON array parser: [{"name":"...","price":"...","img":"..."},...]
            java.util.List<String[]> items = new java.util.ArrayList<>();
            int pos = 0;
            while (pos < json.length() && items.size() < 50) {
                int nameIdx = json.indexOf("\"name\"", pos);
                if (nameIdx < 0) break;
                int nameStart = json.indexOf("\"", nameIdx + 7) + 1;
                int nameEnd = json.indexOf("\"", nameStart);
                if (nameStart <= 0 || nameEnd <= 0) break;
                String name = json.substring(nameStart, nameEnd)
                    .replace("\\u00ae", "\u00AE").replace("\\u00AE", "\u00AE")
                    .replace("\\u2122", "\u2122").replace("\\u00a9", "\u00A9");

                // Skip non-food items
                if (name.length() < 3 || name.startsWith("Do Not") || name.startsWith("--") || name.startsWith("**")) {
                    pos = nameEnd + 1; continue;
                }
                items.add(new String[]{name, ""});
                pos = nameEnd + 1;
            }
            if (items.isEmpty()) return null;
            log("I", "Loaded " + items.size() + " menu items from " + path);
            return items.toArray(new String[0][]);
        } catch (Throwable t) {
            log("W", "loadMenuFromCapturedData error: " + t);
            return null;
        }
    }

    /** Try to find price for a product ID in the JSON. */
    private String estimatePrice(String json, int productId) {
        // Look for "ID":productId pattern near "base":[price,...]
        String needle = "\"" + productId + "\":{\"ID\":" + productId + ",";
        int idx = json.indexOf(needle);
        if (idx < 0) {
            needle = "\"ID\":" + productId + ",";
            idx = json.indexOf(needle);
        }
        if (idx >= 0) {
            int baseIdx = json.indexOf("\"base\":[", idx);
            if (baseIdx >= 0 && baseIdx - idx < 200) {
                int numStart = baseIdx + 8;
                int numEnd = json.indexOf(",", numStart);
                if (numEnd < 0) numEnd = json.indexOf("]", numStart);
                if (numEnd > numStart) {
                    try {
                        int cents = Integer.parseInt(json.substring(numStart, numEnd).trim());
                        if (cents > 0 && cents < 5000) {
                            return "$" + (cents / 100) + "." + String.format("%02d", cents % 100);
                        }
                    } catch (NumberFormatException e) {}
                }
            }
        }
        return "";
    }

    /** Clear black/dark backgrounds from views — prevents XML backgrounds from covering content */
    private void clearBlackBackgrounds(android.view.View view) {
        android.graphics.drawable.Drawable bg = view.getBackground();
        if (bg instanceof android.graphics.drawable.ColorDrawable) {
            int color = ((android.graphics.drawable.ColorDrawable) bg).getColor();
            // Clear black and very dark backgrounds (keep McD dark for toolbar)
            if (color == 0xFF000000 || color == 0) {
                view.setBackground(null);
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                clearBlackBackgrounds(vg.getChildAt(i));
            }
        }
    }

    /** Recursively cap oversized views to prevent layout explosion from invalid dimensions */
    private void capOversizedViews(android.view.View view, int maxW, int maxH) {
        if (view.getMeasuredWidth() > maxW * 2 || view.getMeasuredHeight() > maxH * 2) {
            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                if (lp.width > maxW) lp.width = maxW;
                if (lp.height > maxH) lp.height = maxH;
                view.setLayoutParams(lp);
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                capOversizedViews(vg.getChildAt(i), maxW, maxH);
            }
        }
    }

    private void setBackgroundRecursive(android.view.ViewGroup vg, int color) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            android.view.View child = vg.getChildAt(i);
            // Only set background on views that currently have a non-transparent background
            android.graphics.drawable.Drawable bg = child.getBackground();
            if (bg != null) child.setBackgroundColor(color);
            if (child instanceof android.view.ViewGroup) {
                setBackgroundRecursive((android.view.ViewGroup) child, color);
            }
        }
    }

    private android.view.View findViewByType(android.view.View root, String typeName) {
        if (root.getClass().getSimpleName().contains(typeName)) return root;
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.view.View found = findViewByType(vg.getChildAt(i), typeName);
                if (found != null) return found;
            }
        }
        return null;
    }

    private java.lang.reflect.Method findMethodInHierarchy(Class<?> cls, String name) {
        while (cls != null && cls != Object.class) {
            try {
                for (java.lang.reflect.Method m : safeGetDeclaredMethods(cls)) {
                    if (m.getName().equals(name) && m.getParameterTypes().length == 0) return m;
                }
            } catch (Throwable t) {}
            cls = cls.getSuperclass();
        }
        return null;
    }

    /** Walk view tree and resolve drawable resources for ImageViews */
    private void resolveImageDrawables(Activity activity) {
        try {
            android.view.View root = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            if (root != null) resolveImageDrawablesRecursive(root, activity);
        } catch (Throwable t) {
            log("W", "resolveImageDrawables: " + t.getMessage());
        }
    }

    private void resolveImageDrawablesRecursive(android.view.View view, android.content.Context ctx) {
        if (view instanceof android.widget.ImageView) {
            android.widget.ImageView iv = (android.widget.ImageView) view;
            try {
                java.lang.reflect.Field resField = android.widget.ImageView.class.getDeclaredField("mResource");
                resField.setAccessible(true);
                int resId = resField.getInt(iv);
                if (resId != 0 && iv.getDrawable() == null) {
                    android.graphics.drawable.Drawable d = ctx.getDrawable(resId);
                    if (d != null && !(d instanceof android.graphics.drawable.ColorDrawable)) {
                        iv.setImageDrawable(d);
                        log("I", "Resolved drawable 0x" + Integer.toHexString(resId)
                            + " for ImageView -> " + d.getIntrinsicWidth() + "x" + d.getIntrinsicHeight());
                    }
                }
            } catch (Throwable t) { /* skip */ }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                resolveImageDrawablesRecursive(vg.getChildAt(i), ctx);
            }
        }
    }
}
