package com.westlake.host

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.io.File

class MultiProcInProcessActivity : Activity() {
    private val TAG = "MultiProcInProcess"
    private val MP_PKG = "com.westlake.multiproctest"
    private val MP_MAIN_CLS = "com.westlake.multiproctest.MainActivity"
    private val MP_APP_CLS = "android.app.Application"  // multiproctest has no custom Application
    private val DEFAULT_TARGET = MP_MAIN_CLS

    private var currentMpActivity: Activity? = null
    private var currentTarget: String? = null

    companion object {
        const val EXTRA_TARGET_CLASS = "mp_target_activity"
        // Process-wide singleton mp Application so we don't re-init on every activity launch
        @Volatile private var mpAppRef: Application? = null
        @Volatile private var mpCtxRef: Context? = null
    }

    private fun resolveTargetFromIntent(intent: Intent): String {
        val cnCls = intent.component?.className
        return intent.getStringExtra(EXTRA_TARGET_CLASS)
            ?: cnCls?.takeIf { it.startsWith("com.westlake.multiproctest.") }
            ?: DEFAULT_TARGET
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        val newTarget = resolveTargetFromIntent(newIntent)
        Log.i(TAG, "onNewIntent: target=$newTarget (current=$currentTarget)")
        if (newTarget != currentTarget) {
            setIntent(newIntent)
            // Tear down current activity and load the new target
            try {
                val onPause = Activity::class.java.getDeclaredMethod("onPause")
                onPause.isAccessible = true
                currentMpActivity?.let { onPause.invoke(it) }
            } catch (_: Throwable) {}
            launchTarget(newTarget)
        }
    }

    override fun onResume() {
        super.onResume()
        // Drive mp activity lifecycle into resumed state so its UI actually composes/draws
        currentMpActivity?.let { driveLifecycleToResumed(it) }
    }

    override fun onPause()    { super.onPause();   forwardLifecycle("performPause") }
    override fun onStop()     { super.onStop();    forwardLifecycle("performStop") }
    override fun onDestroy()  {
        if (isFinishing) forwardLifecycle("performDestroy")
        super.onDestroy()
    }

    private fun forwardLifecycle(method: String, vararg args: Any?) {
        val act = currentMpActivity ?: return
        try {
            val m = Activity::class.java.declaredMethods.firstOrNull {
                it.name == method && it.parameterCount == args.size
            } ?: Activity::class.java.declaredMethods.firstOrNull {
                it.name == method && it.parameterCount == 0
            } ?: return
            m.isAccessible = true
            if (m.parameterCount == 0) m.invoke(act) else m.invoke(act, *args)
            Log.i(TAG, "Forwarded $method to ${act.javaClass.simpleName}")
        } catch (e: Throwable) {
            val r = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
            Log.w(TAG, "forwardLifecycle($method) failed: ${r.javaClass.simpleName}: ${r.message?.take(120)}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bypassHiddenApiRestrictions()
        installSwallowingUncaughtHandler()
        installIntentRewriter(MP_PKG, MultiProcInProcessActivity::class.java.name, EXTRA_TARGET_CLASS)
        // Target class can come from (a) explicit extra, (b) activity-alias component name
        // (when mp's MainActivity does startActivity for AppIntroActivity etc., Android
        // resolves the alias and getIntent().component.className equals the alias = mp cls)
        val targetCls = resolveTargetFromIntent(intent)
        Log.i(TAG, "Starting in-process mp; target=$targetCls (cnCls=${intent.component?.className})")
        launchTarget(targetCls)
    }

    private fun launchTarget(targetCls: String) {
        currentTarget = targetCls

        val status = android.widget.TextView(this)
        status.textSize = 12f
        status.setPadding(24, 24, 24, 24)
        status.setTextColor(0xFFFFFFFF.toInt())
        status.setBackgroundColor(0xFF202830.toInt())
        setContentView(status)

        try {
            val (mpCtx, fullRes, themeId) = ensureMpContext()
            val mpApp = ensureMainActivity(mpCtx)

            // Load target activity class
            val mpCl = mpCtx.classLoader
            val activityCls = mpCl.loadClass(targetCls)
            val mpActivity = activityCls.newInstance() as Activity
            Log.i(TAG, "Instantiated ${targetCls}")

            attachAndCreate(mpActivity, mpCtx, mpApp, fullRes, themeId, targetCls)
            currentMpActivity = mpActivity

            stealContentInto(mpActivity, status, targetCls)
            // Drive lifecycle to RESUMED so views actually render (Compose / fragment / ViewModel
            // wiring depends on onStart + onResume; without these the view tree stays inert)
            driveLifecycleToResumed(mpActivity)
        } catch (e: Throwable) {
            val root = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
            Log.e(TAG, "mp in-process FAIL: ${root.javaClass.name}: ${root.message}", root)
            val sw = java.io.StringWriter()
            root.printStackTrace(java.io.PrintWriter(sw))
            val stackText = sw.toString().lineSequence().take(25).joinToString("\n")
            status.text = "FAIL ${root.javaClass.simpleName}: ${root.message?.take(160)}\n\n$stackText"
        }
    }

    /**
     * Defeats Android's hidden-API blocklist for this process so reflective access to
     * LoadedApk.mCredentialProtectedDataDirFile, ContextImpl.mPreferencesDir, etc.
     * doesn't get denied. Uses the double-reflection trick (meta-call via Class.getDeclaredMethod).
     */
    private fun driveLifecycleToResumed(activity: Activity) {
        for (method in listOf("performStart", "performResume", "performTopResumedActivityChanged")) {
            try {
                val m = Activity::class.java.declaredMethods.find {
                    it.name == method && (it.parameterCount == 0 || it.parameterCount == 2)
                } ?: continue
                m.isAccessible = true
                val args: Array<Any?> = when (m.parameterCount) {
                    0 -> emptyArray()
                    2 -> arrayOf(true, "in-process")  // performResume(reallyResume, reason)
                    else -> continue
                }
                m.invoke(activity, *args)
                Log.i(TAG, "Lifecycle $method called")
            } catch (e: Throwable) {
                val r = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
                Log.w(TAG, "Lifecycle $method failed: ${r.javaClass.simpleName}: ${r.message?.take(120)}")
            }
        }
    }

    /**
     * Install a default uncaught-exception handler that logs but does NOT kill the process.
     * mp fires lots of background coroutine work (network, db, audio) that will throw under
     * our stub environment. We don't want any of those to take down the foreground UI.
     */
    private fun installSwallowingUncaughtHandler() {
        try {
            val prev = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
                Log.w(TAG, "Swallowed uncaught on ${thread.name}: ${ex.javaClass.simpleName}: ${ex.message}")
                // Don't chain to prev (KillApplicationHandler) — we want to NOT die.
                // Main-thread crashes still go through Looper's own crash handling; for that,
                // wrap Looper.loop. Background-thread crashes stop here without killing.
                if (thread === android.os.Looper.getMainLooper().thread) {
                    Log.w(TAG, "  (main thread crash — chaining to prev to avoid wedged UI)")
                    try { prev?.uncaughtException(thread, ex) } catch (_: Throwable) {}
                }
            }
            Log.i(TAG, "Installed swallowing uncaught handler")
        } catch (e: Throwable) {
            Log.w(TAG, "installSwallowingUncaughtHandler failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    /**
     * Hook IActivityTaskManager binder so cross-package intents (component pkg == mp's)
     * get rewritten to com.westlake.host/.MultiProcInProcessActivity with the target FQCN in an extra.
     * Without this, the foreign app's hardcoded setClassName("com.westlake.multiproctest", ...)
     * calls would either SecurityException-kill us or spawn mp's standalone process.
     */
    private fun installIntentRewriter(foreignPkg: String, proxyActivity: String, extraKey: String) {
        // Returns true on success, false if anything reflective failed.
        try {
            // Try ActivityTaskManager first (Android 10+), fall back to ActivityManager
            var singletonOwner: Class<*>
            var singletonFieldName: String
            try {
                singletonOwner = Class.forName("android.app.ActivityTaskManager")
                singletonFieldName = "IActivityTaskManagerSingleton"
            } catch (_: ClassNotFoundException) {
                singletonOwner = Class.forName("android.app.ActivityManager")
                singletonFieldName = "IActivityManagerSingleton"
            }
            val singletonField = singletonOwner.getDeclaredField(singletonFieldName)
            singletonField.isAccessible = true
            val singleton = singletonField.get(null)

            val singletonCls = Class.forName("android.util.Singleton")
            val mInstanceField = singletonCls.getDeclaredField("mInstance")
            mInstanceField.isAccessible = true
            val original = mInstanceField.get(singleton)
                ?: run {
                    // Force-init the singleton by calling the public getter
                    singletonOwner.getDeclaredMethod("getService").invoke(null)
                    mInstanceField.get(singleton)
                }
                ?: return

            val ifaceCls = original.javaClass.interfaces.firstOrNull { it.name.endsWith(".IActivityTaskManager") || it.name.endsWith(".IActivityManager") }
                ?: return

            val hostPkg = packageName
            val proxy = java.lang.reflect.Proxy.newProxyInstance(
                ifaceCls.classLoader, arrayOf(ifaceCls)
            ) { _, method, args ->
                val effective: Array<Any?> = if (args == null) emptyArray() else args.copyOf()
                if (method.name.startsWith("startActivity")) {
                    for (i in effective.indices) {
                        val a = effective[i]
                        if (a is Intent) {
                            effective[i] = rewriteIntent(a, foreignPkg, hostPkg, proxyActivity, extraKey)
                        } else if (a is Array<*> && a.isArrayOf<Intent>()) {
                            @Suppress("UNCHECKED_CAST")
                            val arr = (a as Array<Intent>).copyOf()
                            for (j in arr.indices) {
                                arr[j] = rewriteIntent(arr[j], foreignPkg, hostPkg, proxyActivity, extraKey)
                            }
                            effective[i] = arr
                        }
                    }
                }
                try {
                    method.invoke(original, *effective)
                } catch (e: java.lang.reflect.InvocationTargetException) {
                    throw e.cause ?: e
                }
            }
            mInstanceField.set(singleton, proxy)
            Log.i(TAG, "Installed intent rewriter on ${singletonOwner.simpleName}.$singletonFieldName")
        } catch (e: Throwable) {
            Log.w(TAG, "installIntentRewriter failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun rewriteIntent(intent: Intent, foreignPkg: String, hostPkg: String,
                               proxyActivity: String, extraKey: String): Intent {
        val cn = intent.component ?: return intent
        if (cn.packageName != foreignPkg) return intent
        val newIntent = Intent(intent)
        newIntent.component = android.content.ComponentName(hostPkg, proxyActivity)
        newIntent.putExtra(extraKey, cn.className)
        Log.i(TAG, "Rewrote ${cn.flattenToShortString()} → ${newIntent.component?.flattenToShortString()}")
        return newIntent
    }

    /** Replace LocaleManager.mService with a Proxy returning empty for getApplicationLocales. */
    private fun stubLocaleManager(ctx: Context) {
        try {
            val lm = ctx.getSystemService(Context.LOCALE_SERVICE)
                ?: ctx.getSystemService("locale") ?: return
            val mServiceField = lm.javaClass.getDeclaredField("mService")
            mServiceField.isAccessible = true
            val original = mServiceField.get(lm)
            val iLm = Class.forName("android.app.ILocaleManager")
            val empty = android.os.LocaleList.getEmptyLocaleList()
            val proxy = java.lang.reflect.Proxy.newProxyInstance(
                iLm.classLoader, arrayOf(iLm)
            ) { _, method, args ->
                when (method.name) {
                    "getApplicationLocales" -> empty
                    else -> try {
                        method.invoke(original, *(args ?: emptyArray()))
                    } catch (e: java.lang.reflect.InvocationTargetException) {
                        throw e.cause ?: e
                    }
                }
            }
            mServiceField.set(lm, proxy)
            Log.i(TAG, "Stubbed LocaleManager.mService (getApplicationLocales → empty)")
        } catch (e: Throwable) {
            Log.w(TAG, "stubLocaleManager failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun bypassHiddenApiRestrictions() {
        try {
            val getDeclaredMethod = Class::class.java.getDeclaredMethod(
                "getDeclaredMethod", String::class.java, arrayOf<Class<*>>()::class.java)
            val vmRuntime = Class.forName("dalvik.system.VMRuntime")
            val getRuntime = getDeclaredMethod.invoke(vmRuntime, "getRuntime", arrayOf<Class<*>>())
                as java.lang.reflect.Method
            val runtime = getRuntime.invoke(null)
            val setExemptions = getDeclaredMethod.invoke(
                vmRuntime, "setHiddenApiExemptions",
                arrayOf<Class<*>>(Array<String>::class.java)) as java.lang.reflect.Method
            setExemptions.invoke(runtime, arrayOf("L"))
            Log.i(TAG, "Hidden API restrictions bypassed")
        } catch (e: Throwable) {
            Log.w(TAG, "bypassHiddenApiRestrictions failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun ensureMpContext(): Triple<Context, android.content.res.Resources, Int> {
        val mpCtx: Context = mpCtxRef ?: run {
            val ctx = createPackageContext(MP_PKG,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            redirectDataDir(ctx)
            mpCtxRef = ctx
            ctx
        }
        val fullRes = packageManager.getResourcesForApplication(MP_PKG)
        val themeId = fullRes.getIdentifier("Theme.App", "style", MP_PKG)
            .takeIf { it != 0 }
            ?: fullRes.getIdentifier("AppTheme", "style", MP_PKG).takeIf { it != 0 }
            ?: 0
        if (themeId != 0) theme.applyStyle(themeId, true)
        return Triple(mpCtx, fullRes, themeId)
    }

    /**
     * Patch mp's ApplicationInfo.dataDir + related fields to a host-writable scratch dir so
     * shared_prefs / databases / cache writes succeed (host UID can't write mp's data dir).
     */
    private fun redirectDataDir(mpCtx: Context) {
        try {
            val hostDataDir = File(filesDir.parentFile, "mp_data").apply { mkdirs() }
            for (sub in listOf("shared_prefs", "databases", "cache", "code_cache", "files", "no_backup", "app_textures")) {
                File(hostDataDir, sub).apply { mkdirs() }
            }
            val absPath = hostDataDir.absolutePath

            val ai = mpCtx.applicationInfo
            ai.dataDir = absPath
            try {
                val f = ApplicationInfo::class.java.getField("credentialProtectedDataDir")
                f.set(ai, absPath)
            } catch (_: Exception) {}
            try {
                val f = ApplicationInfo::class.java.getField("deviceProtectedDataDir")
                f.set(ai, absPath)
            } catch (_: Exception) {}

            // Patch LoadedApk's cached data dir (all three storage variants)
            try {
                val ciCls = Class.forName("android.app.ContextImpl")
                val mPi = ciCls.getDeclaredField("mPackageInfo")
                mPi.isAccessible = true
                val loadedApk = mPi.get(mpCtx)
                if (loadedApk != null) {
                    val fileFields = listOf(
                        "mDataDirFile" to hostDataDir,
                        "mDeviceProtectedDataDirFile" to hostDataDir,
                        "mCredentialProtectedDataDirFile" to hostDataDir,
                    )
                    for ((fname, v) in fileFields) {
                        try {
                            val f = loadedApk.javaClass.getDeclaredField(fname)
                            f.isAccessible = true
                            f.set(loadedApk, v)
                            Log.i(TAG, "Patched LoadedApk.$fname")
                        } catch (e: Exception) {
                            Log.w(TAG, "Patch LoadedApk.$fname failed: ${e.javaClass.simpleName}: ${e.message}")
                        }
                    }
                    try {
                        val f = loadedApk.javaClass.getDeclaredField("mDataDir")
                        f.isAccessible = true
                        f.set(loadedApk, absPath)
                    } catch (_: Exception) {}
                }
            } catch (e: Throwable) {
                Log.w(TAG, "LoadedApk dataDir patch: ${e.javaClass.simpleName}: ${e.message}")
            }

            // Null out ContextImpl's cached dir fields so next access recomputes from dataDir
            try {
                val ciCls = Class.forName("android.app.ContextImpl")
                for (fname in listOf("mPreferencesDir", "mDatabasesDir", "mFilesDir", "mCacheDir",
                        "mNoBackupFilesDir", "mCodeCacheDir")) {
                    try {
                        val f = ciCls.getDeclaredField(fname)
                        f.isAccessible = true
                        f.set(mpCtx, null)
                    } catch (_: NoSuchFieldException) {}
                }
            } catch (e: Throwable) {
                Log.w(TAG, "ContextImpl cache reset: ${e.javaClass.simpleName}: ${e.message}")
            }

            // Self-verify
            try {
                val obs = mpCtx.dataDir.absolutePath
                Log.i(TAG, "Post-patch mpCtx.dataDir = $obs (expected $absPath)")
            } catch (_: Throwable) {}
            try {
                val obsCache = mpCtx.cacheDir.absolutePath
                Log.i(TAG, "Post-patch mpCtx.cacheDir = $obsCache")
            } catch (e: Throwable) {
                Log.w(TAG, "mpCtx.cacheDir read failed: ${e.javaClass.simpleName}: ${e.message}")
            }
            Log.i(TAG, "Redirected mp dataDir → $absPath")
        } catch (e: Throwable) {
            Log.w(TAG, "redirectDataDir failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun ensureMainActivity(mpCtx: Context): Application {
        mpAppRef?.let { return it }
        val mpCl = mpCtx.classLoader
        val appCls = mpCl.loadClass(MP_APP_CLS)
        val app = appCls.newInstance() as Application
        Log.i(TAG, "Instantiated mp Application: ${app.javaClass.name}")
        val attachBase = android.content.ContextWrapper::class.java
            .getDeclaredMethod("attachBaseContext", Context::class.java)
        attachBase.isAccessible = true
        attachBase.invoke(app, MultiProcSafeContext(mpCtx, MP_PKG))

        try {
            val ai0 = app.applicationInfo
            ai0.packageName = applicationInfo.packageName
        } catch (_: Exception) {}

        // Wire LoadedApk.mApplication so getApplicationContext() reaches the Hilt-instrumented App
        try {
            val ciCls = Class.forName("android.app.ContextImpl")
            val mPi = ciCls.getDeclaredField("mPackageInfo")
            mPi.isAccessible = true
            val loadedApk = mPi.get(mpCtx)
            if (loadedApk != null) {
                val f = loadedApk.javaClass.getDeclaredField("mApplication")
                f.isAccessible = true
                f.set(loadedApk, app)
                Log.i(TAG, "Wired LoadedApk.mApplication = mpApp")
            }
        } catch (e: Throwable) {
            Log.w(TAG, "LoadedApk wire failed: ${e.javaClass.simpleName}: ${e.message}")
        }

        try {
            app.onCreate()
            Log.i(TAG, "mp Application.onCreate() OK")
        } catch (e: Throwable) {
            val r = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
            Log.w(TAG, "mp Application.onCreate threw: ${r.javaClass.simpleName}: ${r.message}")
        }
        mpAppRef = app
        return app
    }

    private fun attachAndCreate(
        mpActivity: Activity,
        mpCtx: Context,
        mpApp: Application,
        fullRes: android.content.res.Resources,
        themeId: Int,
        targetCls: String,
    ) {
        val inflateCtx = android.view.ContextThemeWrapper(mpCtx, themeId)

        val atClass = Class.forName("android.app.ActivityThread")
        val sCurrentAT = atClass.getDeclaredField("sCurrentActivityThread")
        sCurrentAT.isAccessible = true
        val at = sCurrentAT.get(null)
            ?: throw IllegalStateException("ActivityThread.sCurrentActivityThread is null")

        val attachMethod = Activity::class.java.declaredMethods.find {
            it.name == "attach" && it.parameterCount >= 19
        } ?: throw IllegalStateException("Activity.attach(...) not found")
        attachMethod.isAccessible = true

        val ai = ActivityInfo()
        ai.packageName = MP_PKG
        ai.name = targetCls
        try {
            ai.javaClass.getField("applicationInfo").set(ai, mpCtx.applicationInfo)
        } catch (_: Exception) {}
        if (themeId != 0) {
            try { ai.javaClass.getField("theme").setInt(ai, themeId) } catch (_: Exception) {}
        }

        val hostPkg = packageName
        val safeInflateCtx = MultiProcSafeContext(inflateCtx, MP_PKG)
        val attachCtx = object : android.content.ContextWrapper(safeInflateCtx) {
            override fun getPackageName(): String = hostPkg
            override fun getOpPackageName(): String = hostPkg
            override fun getAttributionTag(): String? = null
        }

        val args = arrayOfNulls<Any>(attachMethod.parameterCount)
        args[0] = attachCtx
        args[1] = at
        args[2] = Instrumentation()
        args[3] = android.os.Binder()
        args[4] = 0
        args[5] = mpApp
        args[6] = intent
        args[7] = ai
        args[8] = targetCls

        attachMethod.invoke(mpActivity, *args)
        Log.i(TAG, "Activity.attach() OK for $targetCls; window=${mpActivity.window}")

        if (themeId != 0) mpActivity.setTheme(themeId)

        try {
            val ai2 = mpActivity.applicationInfo
            ai2.packageName = hostPkg
        } catch (_: Exception) {}

        // Hook LocaleManager binder so getApplicationLocales returns empty instead of SecExc
        stubLocaleManager(mpCtx)

        Log.i(TAG, "Calling $targetCls.onCreate()...")
        val oc = Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)
        oc.isAccessible = true
        oc.invoke(mpActivity, null as Bundle?)
        Log.i(TAG, "$targetCls.onCreate() returned!")
    }

    private fun stealContentInto(mpActivity: Activity, status: android.widget.TextView, targetCls: String) {
        val nWin = mpActivity.window
        if (nWin == null) {
            status.text = "$targetCls attach OK but window is null"
            return
        }
        val nContent = nWin.decorView.findViewById<ViewGroup>(android.R.id.content)
        Log.i(TAG, "$targetCls content childCount=${nContent?.childCount}")
        if (nContent != null && nContent.childCount > 0) {
            val nView = nContent.getChildAt(0)
            nContent.removeView(nView)
            val wrap = android.widget.FrameLayout(this)
            wrap.addView(nView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT))
            setContentView(wrap)
            // Force layout + draw: stolen view was attached to mp's never-rendered window
            wrap.post {
                nView.requestLayout()
                nView.invalidate()
                wrap.requestLayout()
                wrap.invalidate()
                Log.i(TAG, "$targetCls forced layout: nView=${nView.width}x${nView.height} wrap=${wrap.width}x${wrap.height}")
            }
            Log.i(TAG, "$targetCls content view transferred to host (LP=${nView.layoutParams?.javaClass?.simpleName})")
        } else {
            status.text = "$targetCls attach+onCreate OK but no content view yet"
        }
    }
}

/**
 * Wraps a mp Context to stub out cross-package operations that would otherwise
 * SecurityException-kill the host process: bindService to mp's own services,
 * startService likewise. Returns "service unavailable"-ish defaults so the calling
 * code (e.g. LibraryViewModel) can continue without binding actual audio playback.
 */
/**
 * Multi-process aware safe-context. Cross-pkg service-bind intents whose target is in the
 * foreign mpPkg get REWRITTEN to a host process slot (MpServiceSlotN), then forwarded.
 * The slot's android:process=":mp_procN" ensures it runs in its own host process,
 * preserving the foreign app's process-isolation expectations.
 *
 * Mapping rule (static): hash the foreign service class name into one of N slots so the
 * same foreign service always lands in the same slot — preserves singleton-per-process
 * semantics across re-binds. For Phase A validation we use a deterministic per-class table.
 */
private class MultiProcSafeContext(
    base: Context,
    private val mpPkg: String,
    private val hostPkg: String = "com.westlake.host",
) : android.content.ContextWrapper(base) {
    private val LOG = "MultiProcInProcess.Safe"

    private fun route(service: Intent): Intent {
        val cn = service.component ?: return service
        // Match foreign service either by component package OR by FQCN prefix.
        // The latter catches `Intent(this, ServiceClass::class.java)` from foreign code
        // where `this.packageName` was patched to host (so cn.packageName=host) but the
        // class name is still in the foreign package.
        val isForeign = cn.packageName == mpPkg ||
                        cn.className.startsWith("$mpPkg.")
        if (!isForeign) return service
        val slot = SLOT_ASSIGNMENT[cn.className] ?: pickHashSlot(cn.className)
        val newIntent = Intent(service)
        newIntent.component = android.content.ComponentName(hostPkg,
            "com.westlake.host.MpServiceSlot$slot")
        newIntent.putExtra(MpServiceSlot.EXTRA_TARGET_CLASS, cn.className)
        Log.i(LOG, "Routed bind ${cn.flattenToShortString()} → $hostPkg/.MpServiceSlot$slot")
        return newIntent
    }

    private fun pickHashSlot(cls: String): Int = (Math.abs(cls.hashCode()) % SLOT_COUNT) + 1

    override fun bindService(service: Intent, conn: android.content.ServiceConnection, flags: Int): Boolean {
        return super.bindService(route(service), conn, flags)
    }
    override fun bindService(service: Intent, flags: Int, executor: java.util.concurrent.Executor,
                              conn: android.content.ServiceConnection): Boolean {
        return super.bindService(route(service), flags, executor, conn)
    }
    override fun startService(service: Intent): android.content.ComponentName? =
        super.startService(route(service))
    override fun startForegroundService(service: Intent): android.content.ComponentName? =
        super.startForegroundService(route(service))

    companion object {
        const val SLOT_COUNT = 4
        // Deterministic mapping (foreign-process → slot) so two distinct foreign processes
        // land in two distinct host processes. For multiproctest:
        //   :proc1 → slot 1, :proc2 → slot 2
        // The mapping is keyed by foreign service class name for simplicity; in a more
        // generic implementation we'd read foreign manifest's android:process attribute.
        val SLOT_ASSIGNMENT = mapOf(
            "com.westlake.multiproctest.CounterServiceProc1" to 1,
            "com.westlake.multiproctest.CounterServiceProc2" to 2,
        )
    }
}

