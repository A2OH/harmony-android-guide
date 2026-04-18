package com.westlake.host

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Instrumentation that runs inside the MCD app's process with full framework access.
 * Launched via: am instrument -w com.westlake.host/.WestlakeBridgeInstrumentation
 *
 * This gives us: real ActivityThread, real AMS registration, real Resources,
 * real ClassLoader for the target app. The ENTIRE Android framework works.
 */
class WestlakeBridgeInstrumentation : Instrumentation() {

    companion object {
        private const val TAG = "WestlakeBridge"
    }

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        Log.i(TAG, "Instrumentation created in process ${android.os.Process.myPid()}")
        Log.i(TAG, "Target context: ${targetContext?.packageName}")
        Log.i(TAG, "Target classloader: ${targetContext?.classLoader?.javaClass?.simpleName}")

        // We're running inside a properly registered process
        // targetContext gives us the MCD app's context with real Resources
        start()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart — we're in a properly registered process!")

        // Launch MCD on main thread and block this thread to keep process alive
        val latch = java.util.concurrent.CountDownLatch(1)
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            doLaunchMcd()
            // Don't count down — keep the latch waiting forever
        }
        // Block the instrumentation thread so the process stays alive
        try { latch.await() } catch (_: InterruptedException) {}
    }

    private fun doLaunchMcd() {

        try {
            // We run in OUR process (com.westlake.host) — properly registered with AMS
            val ctx = targetContext ?: throw RuntimeException("No target context")
            Log.i(TAG, "Our context: ${ctx.packageName}")

            // Get MCD's context via createPackageContext — gives us MCD's resources + classloader
            val mcdCtx = ctx.createPackageContext("com.mcdonalds.app",
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            Log.i(TAG, "MCD context: ${mcdCtx.packageName}")
            Log.i(TAG, "MCD resources: ${mcdCtx.resources}")
            Log.i(TAG, "MCD classloader: ${mcdCtx.classLoader.javaClass.simpleName}")

            // Create MCD Application first (needed by AppCompat/Hilt)
            val mcdAppInfo = ctx.packageManager.getApplicationInfo("com.mcdonalds.app", 0)
            var mcdApp: android.app.Application? = null
            if (mcdAppInfo.className != null) {
                try {
                    val appCls = mcdCtx.classLoader.loadClass(mcdAppInfo.className)
                    mcdApp = appCls.newInstance() as android.app.Application
                    val abc = android.content.ContextWrapper::class.java
                        .getDeclaredMethod("attachBaseContext", Context::class.java)
                    abc.isAccessible = true
                    abc.invoke(mcdApp, mcdCtx)
                    Log.i(TAG, "MCD Application created: ${mcdApp.javaClass.simpleName}")
                    // Register with ActivityThread so getApplicationContext() works
                    try {
                        val atClass = Class.forName("android.app.ActivityThread")
                        val at = atClass.getMethod("currentActivityThread").invoke(null)
                        val mAllApps = atClass.getDeclaredField("mAllApplications")
                        mAllApps.isAccessible = true
                        @Suppress("UNCHECKED_CAST")
                        val appList = mAllApps.get(at) as java.util.ArrayList<android.app.Application>
                        appList.add(mcdApp)
                        // Also set as initial application
                        val mInitApp = atClass.getDeclaredField("mInitialApplication")
                        mInitApp.isAccessible = true
                        mInitApp.set(at, mcdApp)
                        Log.i(TAG, "Application registered with ActivityThread")
                    } catch (e: Throwable) { Log.w(TAG, "App register: ${e.message}") }
                    // Wire Application into LoadedApk BEFORE onCreate
                    // so getApplicationContext() returns mcdApp during DI init
                    try {
                        val mPkgInfoField = mcdCtx.javaClass.getDeclaredField("mPackageInfo")
                        mPkgInfoField.isAccessible = true
                        val loadedApk = mPkgInfoField.get(mcdCtx)
                        val mAppField = loadedApk.javaClass.getDeclaredField("mApplication")
                        mAppField.isAccessible = true
                        mAppField.set(loadedApk, mcdApp)
                        Log.i(TAG, "Application wired to LoadedApk (pre-onCreate)")
                    } catch (e: Throwable) { Log.w(TAG, "Early LoadedApk wire: ${e.message}") }
                    try { mcdApp.onCreate() }
                    catch (e: Throwable) { Log.w(TAG, "App.onCreate: ${e.message}") }
                } catch (e: Throwable) {
                    Log.w(TAG, "App create: ${e.message}")
                }
            }

            // Create MCD Activity in-process with full framework
            val activityClass = "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity"

            // Create using Instrumentation.newActivity (proper framework way)
            val activity = newActivity(mcdCtx.classLoader, activityClass,
                Intent(Intent.ACTION_MAIN)) as Activity
            Log.i(TAG, "Activity created: ${activity.javaClass.name}")

            // Wire Application into LoadedApk so getApplicationContext() returns it
            if (mcdApp != null) {
                try {
                    // mcdCtx is a ContextImpl — get its mPackageInfo (LoadedApk)
                    val mPkgInfoField = mcdCtx.javaClass.getDeclaredField("mPackageInfo")
                    mPkgInfoField.isAccessible = true
                    val loadedApk = mPkgInfoField.get(mcdCtx)
                    val mAppField = loadedApk.javaClass.getDeclaredField("mApplication")
                    mAppField.isAccessible = true
                    mAppField.set(loadedApk, mcdApp)
                    Log.i(TAG, "Application wired to LoadedApk")
                } catch (e: Throwable) { Log.w(TAG, "LoadedApk wire: ${e.message}") }
            }

            // Attach with real framework — we're in a registered process!
            val appInfo = ctx.packageManager.getApplicationInfo("com.mcdonalds.app", 0)
            val actInfo = ctx.packageManager.getActivityInfo(
                android.content.ComponentName("com.mcdonalds.app", activityClass), 0)
            actInfo.applicationInfo = appInfo

            val attachMethod = Activity::class.java.declaredMethods.find {
                it.name == "attach" && it.parameterCount >= 10
            }
            if (attachMethod != null) {
                attachMethod.isAccessible = true
                val atClass = Class.forName("android.app.ActivityThread")
                val at = atClass.getMethod("currentActivityThread").invoke(null)
                val types = attachMethod.parameterTypes
                val params = arrayOfNulls<Any>(types.size)
                for (i in types.indices) {
                    when {
                        types[i] == Context::class.java -> params[i] = mcdCtx
                        types[i] == atClass -> params[i] = at
                        types[i] == Instrumentation::class.java -> params[i] = this
                        types[i] == android.app.Application::class.java -> params[i] = mcdApp ?: (ctx.applicationContext as? android.app.Application)
                        types[i] == Intent::class.java -> params[i] = Intent(Intent.ACTION_MAIN).apply {
                            component = android.content.ComponentName("com.mcdonalds.app", activityClass)
                        }
                        types[i] == android.content.pm.ActivityInfo::class.java -> params[i] = actInfo
                        types[i] == Int::class.javaPrimitiveType -> params[i] = 0
                        types[i] == CharSequence::class.java -> params[i] = "McDonald's" as CharSequence
                        types[i] == android.content.res.Configuration::class.java -> params[i] = mcdCtx.resources.configuration
                        else -> params[i] = null
                    }
                }
                attachMethod.invoke(activity, *params)
                Log.i(TAG, "Activity.attach() OK!")
            }

            // Apply theme
            if (actInfo.theme != 0) activity.setTheme(actInfo.theme)

            // Launch McdInProcessActivity which loads MCD resources in-process
            // It stays on screen — we DON'T call finish() so the process keeps running
            Log.i(TAG, "Launching McdInProcessActivity...")
            val launchIntent = Intent(ctx, McdInProcessActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(launchIntent)
            Log.i(TAG, "Activity launched — process stays alive via latch")

        } catch (e: Throwable) {
            Log.e(TAG, "Error: ${e.message}", e)
            val result = Bundle()
            result.putString("status", "ERROR")
            result.putString("error", e.message)
            finish(Activity.RESULT_CANCELED, result)
        }
    }

    private fun dumpViewTree(view: View, depth: Int) {
        val indent = "  ".repeat(depth)
        val id = if (view.id != View.NO_ID) "id=0x${Integer.toHexString(view.id)}" else ""
        Log.i(TAG, "$indent${view.javaClass.simpleName} $id ${view.width}x${view.height}")
        if (view is ViewGroup && depth < 5) {
            for (i in 0 until view.childCount) {
                dumpViewTree(view.getChildAt(i), depth + 1)
            }
        }
    }
}
