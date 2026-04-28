package com.westlake.host

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method

/**
 * Westlake Engine Host — Compose-native Activity.
 *
 * Extends ComponentActivity (real AndroidX lifecycle) so Compose works natively.
 * Loads app.dex + aosp-shim.dex via child-first DexClassLoader for custom apps.
 * Can also host real APK Activities via startActivity().
 */
class WestlakeActivity : ComponentActivity() {

    companion object {
        private const val TAG = "Westlake"

        @JvmField
        var instance: WestlakeActivity? = null

        @JvmField
        var shimRootView: View? = null

        // For native bridge compatibility
        @JvmStatic var currentCanvas: android.graphics.Canvas? = null
        @JvmStatic var currentHolder: android.view.SurfaceHolder? = null
        @JvmStatic var canvasWidth = 0
        @JvmStatic var canvasHeight = 0
        @JvmStatic var paintPool = arrayOfNulls<android.graphics.Paint>(256)
        @JvmStatic var pathPool = arrayOfNulls<android.graphics.Path>(256)
        @JvmStatic var paintNext = 1
        @JvmStatic var pathNext = 1
        @JvmStatic var shimActivity: Any? = null
        @JvmStatic var shimDispatchTouch: Method? = null
    }

    var engineClassLoader: ClassLoader? = null

    /** Launch the Compose demo app */
    fun launchComposeDemo() {
        setContent { DemoComposeApp() }
    }

    /** Run a real APK inside the Westlake engine (not via startActivity) */
    fun runApkInEngine(apkPath: String, launcherActivity: String, appName: String) {
        setContent { ApkRunnerScreen(apkPath, launcherActivity, appName) }
    }

    /** Run an APK in the dalvikvm subprocess via WestlakeLauncher */
    fun launchVmApk(packageName: String, activityName: String, displayName: String) {
        val config = ApkVmConfig(packageName = packageName, activityName = activityName, displayName = displayName)
        launchVmApk(config)
    }

    fun launchVmApk(config: ApkVmConfig) {
        setContent { WestlakeVMApkScreen(config) }
    }

    /** Launch a custom app from the engine DEX by calling its init+show methods */
    fun launchCustomApp(className: String, initMethod: String?, showMethod: String) {
        if (className == "WESTLAKE_VM") { Log.i(TAG, "Launching WestlakeVM screen"); setContent { WestlakeVMScreen() }; return }
        if (className == "WESTLAKE_VM_TODO") {
            Log.i(TAG, "Launching TODO List VM")
            val config = ApkVmConfig(
                packageName = "com.example.todo",
                activityName = "com.example.todo.TodoListActivity",
                displayName = "TODO List"
            )
            setContent { WestlakeVMApkScreen(config) }
            return
        }
        if (className == "WESTLAKE_VM_TIP") {
            Log.i(TAG, "Launching Tip Calculator VM")
            val config = ApkVmConfig(
                packageName = "com.example.tipcalc",
                activityName = "com.example.tipcalc.TipCalculator",
                displayName = "Tip Calculator"
            )
            setContent { WestlakeVMApkScreen(config) }
            return
        }
        if (className == "WESTLAKE_MCD_REAL") {
            Log.i(TAG, "Launching REAL MCD SplashActivity via DexClassLoader")
            Thread {
                try {
                    // Load MCD APK's DEX files using the phone's own ART
                    val mcdApk = packageManager.getApplicationInfo("com.mcdonalds.app", 0).sourceDir
                    Log.i(TAG, "MCD APK: $mcdApk")
                    val loader = dalvik.system.DexClassLoader(
                        mcdApk,
                        cacheDir.absolutePath,
                        applicationInfo.nativeLibraryDir,
                        classLoader
                    )
                    val splashClass = loader.loadClass("com.mcdonalds.mcdcoreapp.common.activity.SplashActivity")
                    Log.i(TAG, "SplashActivity loaded: ${splashClass.name}")

                    // Launch it as a real Activity
                    runOnUiThread {
                        try {
                            // Launch via MCD's own package name — uses MCD's manifest
                            val intent = android.content.Intent()
                            intent.setClassName("com.mcdonalds.app",
                                "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity")
                            startActivity(intent)
                            Log.i(TAG, "MCD SplashActivity launched!")
                        } catch (e: Exception) {
                            Log.e(TAG, "Launch failed: ${e.message}")
                            // Fallback: try direct intent to MCD app
                            try {
                                val pm = packageManager
                                val launchIntent = pm.getLaunchIntentForPackage("com.mcdonalds.app")
                                if (launchIntent != null) {
                                    startActivity(launchIntent)
                                    Log.i(TAG, "MCD launched via package manager!")
                                }
                            } catch (e2: Exception) {
                                Log.e(TAG, "Fallback also failed: ${e2.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "MCD load failed: ${e.message}")
                }
            }.start()
            return
        }
        if (className == "WESTLAKE_ART_MCD") {
            Log.i(TAG, "Launching MCD via Westlake ART VM")
            val config = ApkVmConfig(
                packageName = "com.mcdonalds.app",
                activityName = "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity",
                displayName = "McDonald's (ART)",
                allowRealFrameworkFallback = false
            )
            setContent { WestlakeVMApkScreen(config) }
            return
        }
        if (className == "WESTLAKE_VM_MCD") {
            Log.i(TAG, "Launching McDonald's VM (raw SurfaceView)")
            // Disable edge-to-edge to prevent SurfaceView destruction during window setup
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // Use raw Android layout instead of Compose to avoid SurfaceView lifecycle issues
            val sv = android.view.SurfaceView(this)
            sv.holder.addCallback(object : android.view.SurfaceHolder.Callback {
                override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                    Log.i(TAG, "McD SurfaceCreated ${holder.surfaceFrame}")
                    // Don't setFixedSize — it destroys the surface. Scale during rendering instead.
                    WestlakeVM.surfaceHolder = holder
                    WestlakeVM.startPipeReader()
                }
                override fun surfaceChanged(holder: android.view.SurfaceHolder, f: Int, w: Int, h2: Int) {
                    Log.i(TAG, "McD SurfaceChanged ${w}x${h2}")
                    WestlakeVM.surfaceHolder = holder
                    WestlakeVM.startPipeReader()
                }
                override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                    Log.i(TAG, "McD SurfaceDestroyed (NOT clearing holder)")
                    // Don't clear holder — the surface will be recreated and we keep the reference
                }
            })
            // Touch handling
            sv.setOnTouchListener { _, event ->
                val sx = 480f / sv.width; val sy = 800f / sv.height
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> WestlakeVM.sendTouch(0, event.x * sx, event.y * sy)
                    android.view.MotionEvent.ACTION_MOVE -> WestlakeVM.sendTouch(2, event.x * sx, event.y * sy)
                    android.view.MotionEvent.ACTION_UP -> WestlakeVM.sendTouch(1, event.x * sx, event.y * sy)
                }
                true
            }
            // Use ImageView + Bitmap — simplest rendering approach, no surface lifecycle issues
            val imageView = android.widget.ImageView(this)
            imageView.scaleType = android.widget.ImageView.ScaleType.FIT_XY
            imageView.setBackgroundColor(android.graphics.Color.BLACK)
            val bitmap = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
            imageView.setImageBitmap(bitmap)
            WestlakeVM.surfaceHolder = bitmap  // pass bitmap as render target
            WestlakeVM.onFrameCallback = {
                Log.i(TAG, "onFrame → invalidate")
                imageView.postInvalidate()  // thread-safe invalidate
            }
            // Touch
            imageView.setOnTouchListener { _, event ->
                val sx = 480f / imageView.width; val sy = 800f / imageView.height
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> WestlakeVM.sendTouch(0, event.x * sx, event.y * sy)
                    android.view.MotionEvent.ACTION_MOVE -> WestlakeVM.sendTouch(2, event.x * sx, event.y * sy)
                    android.view.MotionEvent.ACTION_UP -> WestlakeVM.sendTouch(1, event.x * sx, event.y * sy)
                }
                true
            }
            // Create floating overlay window to show frames ON TOP of MCD
            if (false) { // Disabled — overlay window needs proper permission
                Log.i(TAG, "Overlay mode: creating floating window")
                val wm = getSystemService(WINDOW_SERVICE) as android.view.WindowManager
                // Use TYPE_TOAST (2005) — doesn't need SYSTEM_ALERT_WINDOW on most ROMs
                val overlayParams = android.view.WindowManager.LayoutParams(
                    480, 800,
                    2005, // TYPE_TOAST — less restricted than TYPE_APPLICATION_OVERLAY
                    android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    android.graphics.PixelFormat.TRANSLUCENT
                )
                overlayParams.gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
                overlayParams.x = 16; overlayParams.y = 100

                // Frame layout with border to show it's Westlake's window
                val frame = android.widget.FrameLayout(this)
                frame.setBackgroundColor(0xFF222222.toInt())
                frame.setPadding(4, 4, 4, 4)

                // Label
                val label = android.widget.TextView(this)
                label.text = "Westlake Engine"
                label.setTextColor(0xFFFFCC00.toInt())
                label.setBackgroundColor(0xCC000000.toInt())
                label.textSize = 10f
                label.setPadding(8, 4, 8, 4)

                val labelParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.view.Gravity.TOP or android.view.Gravity.START)

                imageView.layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT)
                frame.addView(imageView)
                frame.addView(label, labelParams)

                wm.addView(frame, overlayParams)
                Log.i(TAG, "Overlay window added")

                // Launch real MCD app
                val mcdIntent = android.content.Intent(android.content.Intent.ACTION_MAIN)
                mcdIntent.component = android.content.ComponentName("com.mcdonalds.app",
                    "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity")
                mcdIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(mcdIntent)

                // Minimize ourselves so MCD is visible
                moveTaskToBack(true)
            } else {
                Log.w(TAG, "No overlay permission — falling back to fullscreen")
                setContentView(imageView)
            }

            // Start bridge on background thread
            Thread({
                val config = ApkVmConfig(
                    packageName = "com.mcdonalds.app",
                    activityName = "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity",
                    displayName = "McDonald's"
                )
                val resolvedConfig = try {
                    val appInfo = packageManager.getApplicationInfo(config.packageName, 0)
                    config.copy(apkSourceDir = appInfo.sourceDir)
                } catch (e: Exception) { config }
                WestlakeVM.startWithConfig(resolvedConfig)
            }, "VMStarter").start()
            return
        }
        if (className.startsWith("VM_APK:")) {
            // Format: VM_APK:package:activity:displayName
            val parts = className.removePrefix("VM_APK:").split(":")
            val pkg = parts[0]; val act = parts[1]; val name = parts.getOrElse(2) { pkg }
            Log.i(TAG, "Launching VM APK: $pkg/$act ($name)")
            val config = ApkVmConfig(packageName = pkg, activityName = act, displayName = name)
            launchVmApk(config)
            return
        }
        if (className.startsWith("VM_APK_CONTROL:") || className.startsWith("VM_APK_TARGET:")) {
            val isControl = className.startsWith("VM_APK_CONTROL:")
            val prefix = if (isControl) "VM_APK_CONTROL:" else "VM_APK_TARGET:"
            val parts = className.removePrefix(prefix).split(":")
            val pkg = parts[0]
            val act = parts[1]
            val name = parts.getOrElse(2) { pkg }
            val stage = parts.getOrElse(3) { "L1" }
            val backendMode = if (isControl) {
                BackendMode.CONTROL_ANDROID_BACKEND
            } else {
                BackendMode.TARGET_OHOS_BACKEND
            }
            Log.i(TAG, "Launching VM APK $backendMode: $pkg/$act ($name) stage=$stage")
            launchVmApk(
                ApkVmConfig(
                    packageName = pkg,
                    activityName = act,
                    displayName = name,
                    backendMode = backendMode,
                    extraVmProperties = mapOf("westlake.canary.stage" to stage),
                )
            )
            return
        }
        if (className == "SHIM_CANVAS") { Log.i(TAG, "Launching ShimCanvas screen"); setContent { ShimCanvasScreen() }; return }
        if (className.startsWith("NATIVE_APK:")) {
            val parts = className.removePrefix("NATIVE_APK:").split(":")
            val pkg = parts[0]; val act = parts[1]; val name = parts.getOrElse(2) { pkg }
            setContent { NativeApkScreen(pkg, act, name) }; return
        }
        if (className == "COMPOSE_DEMO") { launchComposeDemo(); return }
        if (className.startsWith("APK_VIEW:")) {
            val parts = className.removePrefix("APK_VIEW:").split(":")
            val pkg = parts[0]; val name = parts.getOrElse(1) { pkg }
            try {
                val apkPath = packageManager.getApplicationInfo(pkg, 0).sourceDir
                setContent { ApkViewRunnerScreen(apkPath, name) }
            } catch (e: Exception) {
                Log.e(TAG, "APK not found: $pkg", e)
            }
            return
        }
        if (className.startsWith("RUN_APK:")) {
            val parts = className.removePrefix("RUN_APK:").split(":")
            val pkg = parts[0]; val act = parts[1]; val name = parts.getOrElse(2) { pkg }
            // Resolve APK path from package manager
            try {
                val apkPath = packageManager.getApplicationInfo(pkg, 0).sourceDir
                runApkInEngine(apkPath, act, name)
            } catch (e: Exception) {
                Log.e(TAG, "APK not found: $pkg", e)
            }
            return
        }
        val cl = engineClassLoader
        if (cl == null) {
            Log.e(TAG, "launchCustomApp: engineClassLoader is null!")
            return
        }
        Log.i(TAG, "launchCustomApp: $className init=$initMethod show=$showMethod")
        try {
            val appClass = cl.loadClass(className)
            if (initMethod != null) {
                try {
                    appClass.getMethod(initMethod, android.content.Context::class.java).invoke(null, this)
                    Log.i(TAG, "  init() called OK")
                } catch (_: NoSuchMethodException) {
                    Log.w(TAG, "  no init(Context) method")
                }
            }
            try {
                appClass.getMethod(showMethod, android.content.Context::class.java).invoke(null, this)
                Log.i(TAG, "  show(Context) called OK")
            } catch (_: NoSuchMethodException) {
                try {
                    appClass.getMethod(showMethod).invoke(null)
                    Log.i(TAG, "  show() called OK")
                } catch (e2: Exception) {
                    Log.e(TAG, "  show failed: $e2")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "launchCustomApp $className: $e", e)
        }
    }

    /** Return to Compose home */
    fun showHome() {
        setContent { WestlakeHome() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        Log.i(TAG, "WestlakeActivity onCreate (ComponentActivity + Compose)")

        // Check for auto-launch intent extra (e.g., --es launch WESTLAKE_VM_MCD)
        val autoLaunch = intent?.getStringExtra("launch")
        if (autoLaunch != null) {
            Log.i(TAG, "Auto-launching: $autoLaunch")
            launchCustomApp(autoLaunch, null, "")
        } else {
            // Show Compose UI immediately
            setContent {
                WestlakeHome()
            }
        }

        // Load engine DEX files in background (skip for McD raw SurfaceView mode)
        if (autoLaunch != "WESTLAKE_VM_MCD" && autoLaunch != "WESTLAKE_ART_MCD") {
            Thread(Runnable { loadEngine() }, "WestlakeEngine").start()
        }
    }

    private fun loadEngine() {
        android.os.Looper.prepare()
        try {
            Log.i(TAG, "Extracting DEX files...")
            val cacheDir = cacheDir

            val shimDex = extractAsset("aosp-shim.dex", cacheDir)
            val appDex = extractAsset("app.dex", cacheDir)

            if (shimDex == null || appDex == null) {
                Log.e(TAG, "Failed to extract DEX files")
                return
            }

            val optDir = File(cacheDir, "oat").apply { mkdirs() }
            val dexPath = "${shimDex.absolutePath}:${appDex.absolutePath}"
            val nativeLibDir = applicationInfo.nativeLibraryDir

            // Child-first DexClassLoader: classes from our shim DEX take priority over phone's framework.
            // This is critical so that shim's concrete Context/Window/View are used instead of
            // the phone's abstract versions. The DEFINING ClassLoader must be child-first so that
            // when the JVM resolves dependencies of shim classes, it also finds shim classes.
            val childFirst = object : DexClassLoader(dexPath, optDir.absolutePath, nativeLibDir, classLoader) {
                override fun loadClass(name: String, resolve: Boolean): Class<*> {
                    synchronized(this) {
                        findLoadedClass(name)?.let { return it }
                        // Always delegate core Java/Dalvik classes to parent
                        if (name.startsWith("java.") || name.startsWith("javax.") ||
                            name.startsWith("sun.") || name.startsWith("dalvik.") ||
                            name.startsWith("kotlin.") || name.startsWith("kotlinx.")) {
                            return super.loadClass(name, resolve)
                        }
                        // Try our DEX first (child-first)
                        try {
                            val c = findClass(name)
                            if (resolve) resolveClass(c)
                            return c
                        } catch (_: ClassNotFoundException) {}
                        // Fall back to parent (phone's framework)
                        return super.loadClass(name, resolve)
                    }
                }
            }

            engineClassLoader = childFirst
            Thread.currentThread().contextClassLoader = childFirst

            // Set HostBridge host reference so shim classes can delegate
            // host-dependent calls (PackageManager, Resources, Theme, etc.)
            // to the real phone framework via reflection.
            try {
                val bridgeCls = childFirst.loadClass("android.app.HostBridge")
                bridgeCls.getMethod("setHost", Any::class.java).invoke(null, this@WestlakeActivity)
                Log.i(TAG, "HostBridge: host set to WestlakeActivity")
            } catch (e: Exception) {
                Log.w(TAG, "HostBridge setup: $e")
            }

            // Pre-load critical shim classes so they're cached before any APK loads.
            // This ensures the shim's concrete Window/Activity are used, not the phone's abstract ones.
            // Pre-load shim classes that MUST come from our DEX (not the phone's boot CL).
            Log.i(TAG, "Engine DEX loaded (child-first DexClassLoader)")

        } catch (e: Exception) {
            Log.e(TAG, "Engine error: ${e.message}", e)
        }
    }

    private fun extractAsset(name: String, dir: File): File? {
        return try {
            val input = assets.open(name)
            val out = File(dir, name)
            FileOutputStream(out).use { fos ->
                val buf = ByteArray(8192)
                var n: Int
                while (input.read(buf).also { n = it } > 0) fos.write(buf, 0, n)
            }
            input.close()
            out
        } catch (e: Exception) {
            Log.e(TAG, "Extract $name: ${e.message}")
            null
        }
    }
}

/**
 * Shim Canvas Screen — runs the shim View tree IN-PROCESS and renders
 * to a real Android Canvas backed by phone's Skia/GPU.
 * No subprocess, no IPC, no OHBridge — just shim Views → Skia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShimCanvasScreen() {
    val activity = WestlakeActivity.instance ?: return
    var frameBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var frameCount by remember { mutableIntStateOf(0) }
    var shimActivity by remember { mutableStateOf<Any?>(null) }
    var isRunning by remember { mutableStateOf(true) }

    // Use the native path — MockApp creates real Android Views
    var rootView by remember { mutableStateOf<View?>(null) }
    LaunchedEffect(Unit) {
        val cl = activity.engineClassLoader ?: return@LaunchedEffect
        try {
            // Use MockApp.init(context) + build the menu via native Android Views
            val mockAppCls = cl.loadClass("com.example.mockdonalds.MockApp")
            mockAppCls.getMethod("init", android.content.Context::class.java).invoke(null, activity)
            // MockApp.showMenu(ctx) returns a View or calls setContentView
            try {
                mockAppCls.getMethod("showMenu", android.content.Context::class.java).invoke(null, activity)
            } catch (_: Exception) {
                mockAppCls.getMethod("showMenu").invoke(null)
            }
            // Get the root view from shimRootView (set by MockApp)
            rootView = WestlakeActivity.shimRootView
            Log.i("ShimCanvas", "Native View tree loaded: ${rootView?.javaClass?.simpleName}")
        } catch (e: Exception) {
            Log.e("ShimCanvas", "Error: ${e.message}", e)
        }
    }

    DisposableEffect(Unit) { onDispose { isRunning = false } }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF880E4F)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isRunning = false; activity.showHome() }) {
                    Text("←", fontSize = 20.sp, color = Color.White)
                }
                Text("Shim Canvas", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Frame: $frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                rootView?.let { view ->
                    // Display real Android Views via AndroidView — native rendering!
                    AndroidView(
                        factory = {
                            // Detach from any parent first
                            (view.parent as? ViewGroup)?.removeView(view)
                            view
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE91E63))
                    Spacer(Modifier.height(16.dp))
                    Text("Loading View tree...", color = Color.White.copy(0.6f))
                }
            }

            Text(
                "In-process: Shim Views → Android Canvas → Skia/GPU | 60fps",
                fontSize = 10.sp, color = Color(0xFFE91E63),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}

/**
 * Compose home screen — app gallery with Material Design 3.
 */
@Composable
fun WestlakeHome() {
    val apps = remember {
        listOf(
            AppInfo("Shim Canvas", "Shim View tree → phone's Skia (in-process)", Color(0xFFE91E63), "SHIM_CANVAS", null, ""),
            AppInfo("Counter (Native)", "Real APK in-process", Color(0xFF9C27B0), "NATIVE_APK:me.tsukanov.counter:me.tsukanov.counter.ui.MainActivity:Simple Counter", null, ""),
            AppInfo("Calculator (Native)", "Huawei Calculator in-process", Color(0xFF4CAF50), "NATIVE_APK:com.huawei.calculator:com.huawei.calculator.Calculator:Calculator", null, ""),
            AppInfo("Westlake VM", "Run MockDonalds in our own ART11 (subprocess)", Color(0xFF4CAF50), "WESTLAKE_VM", null, ""),
            AppInfo("Counter (VM)", "Simple Counter APK in ART11 subprocess", Color(0xFF9C27B0), "VM_APK:me.tsukanov.counter:me.tsukanov.counter.ui.MainActivity:Simple Counter", null, ""),
            AppInfo("Tip Calculator (VM)", "Full app in ART11 subprocess", Color(0xFFFF9800), "WESTLAKE_VM_TIP", null, ""),
            AppInfo("TODO List (VM)", "Multi-Activity app with ListView", Color(0xFF2196F3), "WESTLAKE_VM_TODO", null, ""),
            AppInfo("Canary L1 (Control)", "Westlake ART + Android backend baseline", Color(0xFF00ACC1), "VM_APK_CONTROL:com.westlake.cutoffcanary:com.westlake.cutoffcanary.StageActivity:Cutoff Canary L1 (Control):L1", null, ""),
            AppInfo("Canary L2 (Control)", "Adds XML resource/layout inflation on control backend", Color(0xFF00838F), "VM_APK_CONTROL:com.westlake.cutoffcanary:com.westlake.cutoffcanary.StageActivity:Cutoff Canary L2 (Control):L2", null, ""),
            AppInfo("Canary L1 (Target)", "Same runtime on target OHOS backend", Color(0xFFF9A825), "VM_APK_TARGET:com.westlake.cutoffcanary:com.westlake.cutoffcanary.StageActivity:Cutoff Canary L1 (Target):L1", null, ""),
            AppInfo("Canary L2 (Target)", "Adds XML resource/layout inflation on target backend", Color(0xFFF57F17), "VM_APK_TARGET:com.westlake.cutoffcanary:com.westlake.cutoffcanary.StageActivity:Cutoff Canary L2 (Target):L2", null, ""),
            AppInfo("Yelp Live (VM)", "Separate live-data APK via host network bridge", Color(0xFFD32323), "VM_APK:com.westlake.yelplive:com.westlake.yelplive.YelpLiveActivity:Westlake Yelp Live", null, ""),
            AppInfo("Material Yelp (VM)", "Material component API canary via Westlake", Color(0xFFD32323), "VM_APK:com.westlake.materialyelp:com.westlake.materialyelp.MaterialYelpActivity:Westlake Material Yelp", null, ""),
            AppInfo("McDonald's (REAL)", "Real SplashActivity via DexClassLoader", Color(0xFFDA291C), "WESTLAKE_MCD_REAL", null, ""),
            AppInfo("McDonald's (ART)", "In-process Westlake ART v114 engine", Color(0xFFDA291C), "WESTLAKE_ART_MCD", null, ""),
            AppInfo("McDonald's (VM)", "Subprocess ART11 pipe+SurfaceView", Color(0xFFDA291C), "WESTLAKE_VM_MCD", null, ""),
            AppInfo("Compose Demo", "Navigation + Retrofit + Coil + ViewModel", Color(0xFF00BCD4), "COMPOSE_DEMO", null, ""),
            AppInfo("Noice (APK Resources)", "Production app → resources.arsc → Views", Color(0xFF26A69A), "APK_VIEW:com.github.ashutoshgngwr.noice:Noice", null, ""),
            AppInfo("Counter (APK Resources)", "Real APK → resources.arsc → Views", Color(0xFF9C27B0), "APK_VIEW:me.tsukanov.counter:Counter", null, ""),
            AppInfo("MockDonalds", "Restaurant ordering", Color(0xFFDA291C), "com.example.mockdonalds.MockApp", "init", "showMenu"),
            AppInfo("Dialer", "Phone dialer", Color(0xFF1565C0), "com.example.dialer.DialerEntry", null, "launch"),
            AppInfo("Social Feed", "Social media", Color(0xFF1877F2), "com.example.socialfeed.SocialFeedApp", "init", "showFeed"),
            AppInfo("Huawei Calc", "Calculator (real APK)", Color(0xFF6A1B9A), "com.example.mockdonalds.XmlTestHelper", null, "loadHuaweiCalculator"),
        )
    }

    val realApps = remember {
        listOf(
            // Commercial apps
            RealAppInfo("Amazon", "com.amazon.mShop.android.shopping", "com.amazon.mShop.home.HomeActivity", Color(0xFFFF9900)),
            RealAppInfo("Meituan", "com.sankuai.meituan", "com.meituan.android.pt.homepage.activity.MainActivity", Color(0xFFFFD600)),
            RealAppInfo("WeChat", "com.tencent.mm", "com.tencent.mm.ui.LauncherUI", Color(0xFF07C160)),
            RealAppInfo("Didi", "com.sdu.didi.psnger", "com.didi.sdk.app.launch.splash.SplashActivity", Color(0xFFEF6C00)),
            RealAppInfo("PayPal", "com.paypal.android.p2pmobile", "com.paypal.android.p2pmobile.startup.activities.StartupActivity", Color(0xFF003087)),
            RealAppInfo("QQ Music", "com.tencent.qqmusic", "com.tencent.qqmusic.activity.AppStarterActivity", Color(0xFF31C27C)),
            RealAppInfo("Dianping", "com.dianping.v1", "com.dianping.v1.NovaMainActivity", Color(0xFFFF6633)),
            // Open source + system
            RealAppInfo("Noice", "com.github.ashutoshgngwr.noice", "com.github.ashutoshgngwr.noice.activity.MainActivity", Color(0xFF26A69A)),
            RealAppInfo("Simple Counter", "me.tsukanov.counter", "me.tsukanov.counter.ui.MainActivity", Color(0xFF9C27B0)),
            RealAppInfo("Calculator", "com.huawei.calculator", "com.huawei.calculator.Calculator", Color(0xFF4CAF50)),
            RealAppInfo("Settings", "com.android.settings", "com.android.settings.HWSettings", Color(0xFF607D8B)),
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF90CAF9),
            surface = Color(0xFF1A1A2E),
            background = Color(0xFF1A1A2E),
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                // Header
                Text(
                    "Westlake Apps",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.padding(20.dp, 40.dp, 20.dp, 4.dp)
                )
                Text(
                    "Android apps on Westlake Engine + Jetpack Compose",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Text("Custom Apps", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp))
                    }

                    items(apps) { app ->
                        AppCard(app)
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("Installed Apps (Real APKs)", fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp))
                    }

                    items(realApps) { app ->
                        RealAppCard(app)
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(app: AppInfo) {
    val activity = WestlakeActivity.instance
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                activity?.launchCustomApp(app.className, app.initMethod, app.showMethod)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(app.color),
                contentAlignment = Alignment.Center
            ) {
                Text(app.name.first().toString(), fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text(app.description, fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Text("▶", fontSize = 18.sp, color = app.color)
        }
    }
}

@Composable
fun RealAppCard(app: RealAppInfo) {
    val activity = WestlakeActivity.instance
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                activity?.let {
                    try {
                        val apkPath = it.packageManager.getApplicationInfo(app.packageName, 0).sourceDir
                        it.runApkInEngine(apkPath, app.activityName, app.name)
                    } catch (e: Exception) {
                        Log.e("Westlake", "Launch ${app.name}: $e")
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(app.color),
                contentAlignment = Alignment.Center
            ) {
                Text(app.name.first().toString(), fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text(app.packageName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
            }
            Text("▶", fontSize = 18.sp, color = app.color)
        }
    }
}

data class AppInfo(val name: String, val description: String, val color: Color, val className: String, val initMethod: String? = "init", val showMethod: String = "showMenu")
data class RealAppInfo(val name: String, val packageName: String, val activityName: String, val color: Color)
