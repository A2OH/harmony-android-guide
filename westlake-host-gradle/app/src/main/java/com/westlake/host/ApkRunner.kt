package com.westlake.host

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dalvik.system.DexClassLoader
import java.io.*
import java.util.zip.ZipFile

/**
 * Runs a real APK inside the Westlake engine:
 * 1. Extracts classes.dex + resources.arsc from APK
 * 2. Loads classes via DexClassLoader
 * 3. Parses resources.arsc
 * 4. Instantiates the launcher Activity
 * 5. Calls onCreate → gets the content View
 * 6. Displays it inside our Compose UI
 */
object ApkRunner {
    private const val TAG = "ApkRunner"

    data class RunResult(
        val steps: List<String>,
        val contentView: View? = null,
        val error: String? = null
    )

    fun runApk(activity: WestlakeActivity, apkPath: String, launcherActivity: String): RunResult {
        val steps = mutableListOf<String>()

        try {
            steps.add("✅ APK: $apkPath (${File(apkPath).length() / 1024}KB)")

            // === KEY INSIGHT: Load APK through our CHILD-FIRST classloader ===
            // This makes the APK see our SHIM Activity (not phone's real Activity)
            // Same approach as MockDonalds/Dialer — the shim IS the framework

            val engineCl = activity.engineClassLoader
            if (engineCl == null) {
                steps.add("❌ Engine classloader not ready — wait for DEX to load")
                return RunResult(steps)
            }

            // Step 1: Add APK's classes.dex to the engine's DexClassLoader
            val cacheDir = File(activity.cacheDir, "apk_run").apply { mkdirs() }
            val dexFile = File(cacheDir, "classes.dex")
            ZipFile(apkPath).use { zip ->
                zip.getEntry("classes.dex")?.let { entry ->
                    zip.getInputStream(entry).use { input ->
                        FileOutputStream(dexFile).use { output -> input.copyTo(output) }
                    }
                    steps.add("✅ classes.dex: ${dexFile.length() / 1024}KB")
                } ?: return RunResult(steps, error = "No classes.dex")
            }

            // Create a DexClassLoader with APK's DEX, parented to our engine classloader
            // This way: APK classes → engine shim classes → phone boot classes
            val optDir = File(cacheDir, "oat").apply { mkdirs() }
            val apkLoader = DexClassLoader(
                dexFile.absolutePath, optDir.absolutePath,
                activity.applicationInfo.nativeLibraryDir,
                engineCl  // Parent = our shim classloader!
            )
            steps.add("✅ APK DexClassLoader (parent = engine shim)")

            // Step 2: Load Activity class through APK loader
            val actClass = apkLoader.loadClass(launcherActivity)
            steps.add("✅ Loaded: ${actClass.simpleName}")
            steps.add("  Super: ${actClass.superclass?.name}")
            steps.add("  Super2: ${actClass.superclass?.superclass?.name}")

            // Verify it extends our SHIM Activity, not phone's real Activity
            val shimActivity = engineCl.loadClass("android.app.Activity")
            val isShim = shimActivity.isAssignableFrom(actClass)
            steps.add(if (isShim) "✅ Extends SHIM Activity (not phone's)" else "⚠️ Extends phone's Activity")

            // Step 3: Parse resources.arsc via engine's ResourceTableParser
            try {
                val apkResLoader = engineCl.loadClass("android.content.res.ApkResourceLoader")
                val loadMethod = apkResLoader.getMethod("loadFromApk", String::class.java)
                val resources = loadMethod.invoke(null, apkPath)
                if (resources != null) {
                    val table = resources.javaClass.getMethod("getResourceTable").invoke(resources)
                    if (table != null) {
                        val strCount = table.javaClass.getMethod("getStringCount").invoke(table) as Int
                        val intCount = table.javaClass.getMethod("getIntegerCount").invoke(table) as Int
                        steps.add("✅ Resources: $strCount strings, $intCount integers")
                    }
                }
            } catch (e: Exception) {
                steps.add("⚠️ Resources: ${e.message?.take(80)}")
            }

            // Step 4: Use MiniServer to launch the Activity (same as MockDonalds)
            steps.add("⏳ Launching via MiniServer...")
            try {
                // Init MiniServer with APK's package name
                val pkg = launcherActivity.substringBeforeLast(".").substringBeforeLast(".")
                val miniServerClass = engineCl.loadClass("android.app.MiniServer")

                // MiniServer.init(packageName)
                miniServerClass.getMethod("init", String::class.java).invoke(null, pkg)
                steps.add("✅ MiniServer.init('$pkg')")

                // Get MiniServer instance and ActivityManager
                val server = miniServerClass.getMethod("get").invoke(null)
                val am = server.javaClass.getMethod("getActivityManager").invoke(server)
                steps.add("✅ MiniActivityManager ready")

                // Register the APK's Activity class
                am.javaClass.getMethod("registerActivityClass", String::class.java, Class::class.java)
                    .invoke(am, launcherActivity, actClass)
                steps.add("✅ Registered: $launcherActivity")

                // Try to create the APK's custom Application class
                try {
                    // Get the real app's Application class name from PackageManager
                    val realAppInfo = activity.packageManager.getApplicationInfo(pkg, 0)
                    val appClassName = realAppInfo.className
                    if (appClassName != null) {
                        val appCls = apkLoader.loadClass(appClassName)
                        val appObj = appCls.newInstance()
                        try { server.javaClass.getMethod("setApplication", engineCl.loadClass("android.app.Application")).invoke(server, appObj) } catch (_: Exception) {}
                        steps.add("✅ Application: ${appCls.simpleName}")
                    }
                } catch (e: Exception) {
                    steps.add("⚠️ Application: ${e.message?.take(60)}")
                }

                // Create Intent and start Activity via MiniActivityManager
                val intentClass = engineCl.loadClass("android.content.Intent")
                val compNameClass = engineCl.loadClass("android.content.ComponentName")
                val intent = intentClass.newInstance()
                val comp = compNameClass.getConstructor(String::class.java, String::class.java)
                    .newInstance(pkg, launcherActivity)
                intentClass.getMethod("setComponent", compNameClass).invoke(intent, comp)

                am.javaClass.getMethod("startActivity", engineCl.loadClass("android.app.Activity"),
                    intentClass, Int::class.javaPrimitiveType)
                    .invoke(am, null, intent, -1)
                steps.add("✅ startActivity called!")

                // Get the resumed activity's root view
                val resumed = am.javaClass.getMethod("getResumedActivity").invoke(am)
                if (resumed != null) {
                    steps.add("✅ Activity launched: ${resumed.javaClass.simpleName}")

                    // Get root view via getWindow().getDecorView()
                    try {
                        val window = resumed.javaClass.getMethod("getWindow").invoke(resumed)
                        if (window != null) {
                            val decorView = window.javaClass.getMethod("getDecorView").invoke(window) as? View
                            if (decorView != null) {
                                steps.add("✅ Got View: ${decorView.javaClass.simpleName} ${decorView.width}x${decorView.height}")
                                (decorView.parent as? ViewGroup)?.removeView(decorView)
                                return RunResult(steps, contentView = decorView)
                            }
                        }
                    } catch (e: Exception) {
                        steps.add("⚠️ getDecorView: ${e.cause?.message ?: e.message}")
                    }

                    // Try shimRootView
                    try {
                        val hostClass = engineCl.loadClass("com.westlake.host.WestlakeActivity")
                        val shimRoot = hostClass.getField("shimRootView").get(null) as? View
                        if (shimRoot != null) {
                            steps.add("✅ shimRootView: ${shimRoot.javaClass.simpleName}")
                            (shimRoot.parent as? ViewGroup)?.removeView(shimRoot)
                            return RunResult(steps, contentView = shimRoot)
                        }
                    } catch (_: Exception) {}
                } else {
                    steps.add("❌ No resumed activity")
                }

            } catch (e: Exception) {
                val cause = e.cause ?: e
                steps.add("❌ MiniServer failed: ${cause.javaClass.simpleName}")
                steps.add("  ${cause.message?.take(200)}")
                Log.e(TAG, "MiniServer launch failed", cause)
            }

            return RunResult(steps)

        } catch (e: Exception) {
            steps.add("❌ Fatal: ${e.javaClass.simpleName}: ${e.message}")
            return RunResult(steps, error = e.message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkRunnerScreen(apkPath: String, launcherActivity: String, appName: String) {
    val activity = WestlakeActivity.instance ?: return
    var result by remember { mutableStateOf<ApkRunner.RunResult?>(null) }

    LaunchedEffect(apkPath) {
        // Run on main thread since we need UI context for Views
        result = try {
            ApkRunner.runApk(activity, apkPath, launcherActivity)
        } catch (e: Exception) {
            ApkRunner.RunResult(listOf("❌ Crash: ${e.javaClass.simpleName}: ${e.message}"))
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Running: $appName", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { activity.showHome() }) {
                            Text("←", fontSize = 20.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A2E),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Diagnostic steps
                result?.let { r ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(r.steps.size) { i ->
                            Text(r.steps[i], fontSize = 13.sp,
                                color = if (r.steps[i].startsWith("❌")) Color.Red
                                else if (r.steps[i].startsWith("⚠")) Color.Yellow
                                else Color.White.copy(0.8f))
                        }
                    }

                    // Show the APK's content View if we got one
                    r.contentView?.let { view ->
                        AndroidView(
                            factory = { view },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(32.dp),
                        color = Color(0xFF90CAF9)
                    )
                }
            }
        }
    }
}
