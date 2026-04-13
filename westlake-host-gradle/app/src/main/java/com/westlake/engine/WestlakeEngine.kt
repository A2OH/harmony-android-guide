package com.westlake.engine

import android.util.Log

/**
 * Westlake ART Engine — loads our custom ART v114 runtime as a shared library
 * and runs Android app DEX files through our interpreter.
 *
 * System API calls from the app go through the phone's real Android framework
 * (same process), while execution is controlled by our ART.
 */
object WestlakeEngine {
    private const val TAG = "WestlakeEngine"
    private var loaded = false

    /** Load the native library */
    fun init() {
        if (loaded) return
        // Always load from /data/local/tmp/westlake/ to get the latest version
        try {
            System.load("/data/local/tmp/westlake/libwestlake_art.so")
            loaded = true
            Log.i(TAG, "libwestlake_art.so loaded from /data/local/tmp")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load from tmp: ${e.message}")
            // Fallback to bundled
            try {
                System.loadLibrary("westlake_art")
                loaded = true
                Log.i(TAG, "libwestlake_art.so loaded (bundled)")
            } catch (e2: UnsatisfiedLinkError) {
                Log.e(TAG, "Also failed bundled: ${e2.message}")
            }
        }
    }

    /**
     * Start the Westlake ART engine.
     * @param bootClassPath Colon-separated boot JAR paths
     * @param dexPaths Colon-separated app DEX/JAR paths
     * @param mainClass Fully qualified main class name
     * @param args Arguments for main()
     * @return Exit code (0 = success)
     */
    fun start(bootClassPath: String, dexPaths: String, mainClass: String, args: Array<String> = emptyArray()): Int {
        if (!loaded) {
            Log.e(TAG, "Engine not loaded")
            return -99
        }
        Log.i(TAG, "Starting: main=$mainClass")
        return nativeStart(bootClassPath, dexPaths, mainClass, args)
    }

    /** Log via native stderr (bypasses Java I/O issues) */
    fun log(msg: String) {
        if (loaded) nativeLog(msg) else Log.i(TAG, msg)
    }

    // Native methods (implemented in westlake_jni.cc)
    @JvmStatic private external fun nativeStart(
        bootClassPath: String, dexPaths: String, mainClass: String, args: Array<String>
    ): Int

    @JvmStatic private external fun nativeLog(msg: String)
}
