package com.westlake.host

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Frame magic (must match ohbridge_stub.c DLIST_MAGIC)
private const val DLIST_MAGIC = 0x444C5354  // "DLST"

// Display list op codes (must match ohbridge_stub.c)
private const val OP_DRAW_COLOR      = 1
private const val OP_DRAW_RECT       = 2
private const val OP_DRAW_TEXT       = 3
private const val OP_DRAW_LINE       = 4
private const val OP_SAVE            = 5
private const val OP_RESTORE         = 6
private const val OP_TRANSLATE       = 7
private const val OP_CLIP_RECT       = 8
private const val OP_DRAW_ROUND_RECT = 9
private const val OP_DRAW_CIRCLE     = 10
private const val OP_DRAW_IMAGE      = 11
private const val OP_ARGB_BITMAP     = 12

/**
 * Configuration for launching an APK in the Westlake VM subprocess.
 * If null/blank, falls back to the original MockDonalds behavior.
 */
data class ApkVmConfig(
    val packageName: String,       // e.g. "me.tsukanov.counter"
    val activityName: String,      // e.g. "me.tsukanov.counter.ui.MainActivity"
    val displayName: String,       // e.g. "Simple Counter"
    val apkSourceDir: String? = null, // resolved from PackageManager at launch time
    val allowRealFrameworkFallback: Boolean = false
)

object WestlakeVM {
    private const val TAG = "WestlakeVM"
    val TOUCH_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_touch.dat"
    private const val DALVIKVM_DIR = "/data/local/tmp/westlake"

    var process: Process? = null
    var pipeStream: java.io.InputStream? = null  // stdout pipe (binary display list frames)
    var touchSeq = 0
    @Volatile var surfaceHolder: Any? = null  // SurfaceHolder or Surface
    var onFrameCallback: (() -> Unit)? = null
    private var pipeReaderThread: Thread? = null

    fun startPipeReader() {
        Log.i(TAG, "startPipeReader: pipe=${pipeStream != null} holder=${surfaceHolder != null} thread=${pipeReaderThread?.isAlive}")
        if (pipeReaderThread?.isAlive == true) return
        val stream = pipeStream ?: return
        val holder = surfaceHolder ?: return
        pipeReaderThread = Thread({
            Log.i(TAG, "PIPE READER: started, holder=$holder")
            try {
                kotlinx.coroutines.runBlocking {
                    readPipeAndRender(holder, stream, { surfaceHolder != null }) {
                        onFrameCallback?.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "PIPE READER: error: $e")
            }
            Log.i(TAG, "PIPE READER: exited")
        }, "PipeReader")
        pipeReaderThread!!.isDaemon = true
        pipeReaderThread!!.start()
    }

    // Boot image files for AOT startup (core JARs only, in arm64/ subdir)
    // 3-component speed boot image: core-oj (primary), core-libart, core-icu4j
    private val BOOT_IMAGE_FILES = listOf(
        "boot.art", "boot.oat", "boot.vdex",
        "boot-core-libart.art", "boot-core-libart.oat", "boot-core-libart.vdex",
        "boot-core-icu4j.art", "boot-core-icu4j.oat", "boot-core-icu4j.vdex"
    )

    /** Start MockDonalds (legacy entry point) */
    fun start(): List<String> = startWithConfig(null)

    /**
     * Start the VM with an optional APK config.
     * stdout carries binary display list frames ([4-byte LE size][ops]).
     * stderr carries text log output.
     */
    fun startWithConfig(apkConfig: ApkVmConfig?): List<String> {
        Log.i(TAG, "startWithConfig() called, config=$apkConfig")
        val log = mutableListOf<String>()
        val activity = WestlakeActivity.instance ?: return listOf("No activity").also { Log.e(TAG, "No activity instance!") }

        // Kill any existing
        process?.destroyForcibly()
        pipeStream = null

        // Copy dalvikvm to app's private dir (SELinux allows execute there)
        val vmDir = File(activity.filesDir, "vm").apply { mkdirs() }
        val srcDir = File(DALVIKVM_DIR)
        val dvmSrc = File(srcDir, "dalvikvm")
        val dvmDst = File(vmDir, "dalvikvm")
        if (dvmSrc.exists() && (!dvmDst.exists()
                || dvmDst.length() != dvmSrc.length()
                || dvmDst.lastModified() != dvmSrc.lastModified())) {
            dvmSrc.copyTo(dvmDst, overwrite = true)
            dvmDst.setLastModified(dvmSrc.lastModified())
            dvmDst.setExecutable(true, false)
            log.add("Copied dalvikvm")
        }

        // Run from /data/local/tmp/westlake/ directly so boot image paths match
        val dvm = dvmDst.absolutePath
        val runDir = DALVIKVM_DIR
        // BCP must match boot image order: core-oj, core-libart, core-icu4j, then extras, then shim
        // bouncycastle.jar provides BouncyCastleProvider for java.security (UUID, SecureRandom)
        // Conscrypt stubs in aosp-shim.dex satisfy Providers strict init check
        // aosp-shim.dex FIRST (provides native stubs: SystemProperties, MessageQueue, etc.)
        // framework.jar SECOND (provides real TypedArray, Theme, Material support)
        val fwJar = if (File("$runDir/framework.jar").exists()) ":$runDir/framework.jar" else ""
        val bcp = "$runDir/core-oj.jar:$runDir/core-libart.jar:$runDir/core-icu4j.jar:$runDir/bouncycastle.jar:$runDir/aosp-shim.dex$fwJar"

        // Boot image: ALL files packaged as .so in APK → extracted to nativeLibraryDir
        // with apk_data_file SELinux context (allows mmap PROT_EXEC on EMUI)
        // ART discovers boot images at $ANDROID_ROOT/arm64/ — symlink .so files from nativeLibDir
        val fwArm64 = File(vmDir, "arm64").apply { mkdirs() }
        // Also create framework/arm64 for compatibility
        File(vmDir, "framework/arm64").mkdirs()
        val nativeLibDir = activity.applicationInfo.nativeLibraryDir
        val bootFileMap = mapOf(
            "boot.art" to "libboot_art.so", "boot.oat" to "libboot_oat.so", "boot.vdex" to "libboot_vdex.so",
            "boot-core-libart.art" to "libboot_core_libart_art.so",
            "boot-core-libart.oat" to "libboot_core_libart_oat.so",
            "boot-core-libart.vdex" to "libboot_core_libart_vdex.so",
            "boot-core-icu4j.art" to "libboot_core_icu4j_art.so",
            "boot-core-icu4j.oat" to "libboot_core_icu4j_oat.so",
            "boot-core-icu4j.vdex" to "libboot_core_icu4j_vdex.so"
        )
        for ((artName, soName) in bootFileMap) {
            val src = File(nativeLibDir, soName)
            val dst = File(fwArm64, artName)
            if (src.exists() && !dst.exists()) {
                try {
                    Runtime.getRuntime().exec(arrayOf("ln", "-sf", src.absolutePath, dst.absolutePath)).waitFor()
                } catch (e: Exception) {
                    Log.w(TAG, "Symlink failed for $artName: $e")
                    src.copyTo(dst, overwrite = true)
                }
            }
        }
        // Create fake dex2oat to prevent ART from hanging trying to generate images
        val fakeDex2oat = File(vmDir, "bin/dex2oat")
        if (!fakeDex2oat.exists()) {
            fakeDex2oat.parentFile.mkdirs()
            fakeDex2oat.writeText("#!/system/bin/sh\nexit 1\n")
            fakeDex2oat.setExecutable(true, false)
        }
        // Also create dalvik-cache dir (ART expects it)
        File(runDir, "dalvik-cache/arm64").mkdirs()

        // Symlink pre-compiled app odex/vdex files from nativeLibDir (apk_data_file context)
        // to oat/arm64/ where ART expects them. This enables AOT execution for app code.
        val oatDir = File(runDir, "oat/arm64").apply { mkdirs() }
        val nativeLibFiles = File(nativeLibDir).listFiles() ?: emptyArray()
        for (soFile in nativeLibFiles) {
            if (soFile.name.startsWith("libmcd_") && (soFile.name.endsWith("_odex.so") || soFile.name.endsWith("_vdex.so"))) {
                // libmcd_classes_odex.so -> mcd_classes.odex
                val artName = soFile.name.removePrefix("lib").replace("_odex.so", ".odex").replace("_vdex.so", ".vdex")
                val dst = File(oatDir, artName)
                if (!dst.exists()) {
                    try {
                        Runtime.getRuntime().exec(arrayOf("ln", "-sf", soFile.absolutePath, dst.absolutePath)).waitFor()
                    } catch (e: Exception) {
                        try { soFile.copyTo(dst, overwrite = true) } catch (_: Exception) {}
                    }
                }
            }
        }
        Log.i(TAG, "App OAT symlinks: ${oatDir.listFiles()?.size ?: 0} files in ${oatDir.absolutePath}")

        // Also try direct path to native lib dir (bypass framework discovery)
        val nativeBootArt = File(nativeLibDir, "libboot_art.so")
        val nativeBootOat = File(nativeLibDir, "libboot_oat.so")
        val hasBootImage = nativeBootArt.exists() && nativeBootOat.exists()
        val bootArgs = if (hasBootImage) {
            log.add("Boot image -- AOT mode")
            // Use relative "boot.art" — ART discovers at $ANDROID_ROOT/framework/arm64/boot.art
            // (symlinked to nativeLibDir which has apk_data_file context allowing PROT_EXEC)
            arrayOf("-Ximage:boot.art")
        } else {
            log.add("Interpreter mode (+ JIT)")
            arrayOf("-Xnoimage-dex2oat")
        }

        // Build command based on config
        val cmd: Array<String>
        var envApkPath: String? = null
        var envActivityName: String? = null
        var envPackageName: String? = null
        var envResDir: String? = null
        var envManifestPath: String? = null

        if (apkConfig != null) {
            val frameworkPolicy = if (apkConfig.allowRealFrameworkFallback) "allow_real" else "westlake_only"
            // --- APK mode: extract DEX, copy APK, use WestlakeLauncher ---
            val apkSrc = apkConfig.apkSourceDir
            // Check for DEX-only app (tipcalc.dex at runDir)
            val dexOnlyPath = "$runDir/${apkConfig.packageName.replace(".", "_")}.dex"
            val isDexOnly = (apkSrc == null || !File(apkSrc).exists()) && File(dexOnlyPath).exists()

            // Check for pre-pushed multi-DEX (e.g., mcd_classes.dex, mcd_classes2.dex, ...)
            // ALWAYS prefer pre-pushed DEX over installed APK (our shim + fixes are in the pre-pushed DEX)
            val multiDexPrefix = "mcd_classes"
            val multiDexFirst = File("$runDir/${multiDexPrefix}.dex")
            val isMultiDexPushed = !isDexOnly && multiDexFirst.exists()
                    && apkConfig.packageName == "com.mcdonalds.app"

            if (!isDexOnly && !isMultiDexPushed && (apkSrc == null || !File(apkSrc).exists())) {
                log.add("ERROR: APK not found: $apkSrc (also checked $dexOnlyPath)")
                return log
            }

            if (isDexOnly) {
                // DEX-only app — add to bootclasspath, no APK extraction needed
                val fullBcp = "$bcp:$dexOnlyPath"
                cmd = arrayOf(
                    dvm, "-Xbootclasspath:$fullBcp", *bootArgs, "-Xverify:none",
                    "-Dwestlake.framework.policy=$frameworkPolicy",
                    "-Dwestlake.apk.path=$dexOnlyPath",
                    "-Dwestlake.apk.activity=${apkConfig.activityName}",
                    "-Dwestlake.apk.package=${apkConfig.packageName}",
                    "-classpath", dexOnlyPath,
                    "com.westlake.engine.WestlakeLauncher",
                    "--apk-path", dexOnlyPath,
                    "--apk-activity", apkConfig.activityName,
                    "--apk-package", apkConfig.packageName
                )
                envApkPath = dexOnlyPath
                envActivityName = apkConfig.activityName
                envPackageName = apkConfig.packageName
                log.add("DEX-only: ${apkConfig.displayName}")
            } else if (isMultiDexPushed) {
                // Multi-DEX app (McDonald's) — run on Westlake's dalvikvm, not app_process64.
                val mcdCp = StringBuilder("$runDir/${multiDexPrefix}.dex")
                for (i in 2..33) {
                    val f = File("$runDir/${multiDexPrefix}${i}.dex")
                    if (f.exists()) mcdCp.append(":${f.absolutePath}")
                }

                // Start mock backend server for cached API responses
                val mockDataDir = "$runDir/mock-backend"
                val mockPort = if (File(mockDataDir, "url-map.json").exists()) {
                    val p = MockBackendServer.start(mockDataDir)
                    log.add("Mock backend on port $p (${File(mockDataDir, "url-map.json").length()/1024}KB map)")
                    p
                } else {
                    log.add("No mock-backend data found, network will fail")
                    0
                }
                val proxyArgs = if (mockPort > 0) arrayOf(
                    "-Dhttp.proxyHost=127.0.0.1", "-Dhttp.proxyPort=$mockPort",
                    "-Dhttps.proxyHost=127.0.0.1", "-Dhttps.proxyPort=$mockPort"
                ) else emptyArray()

                val sharedResDir = File("$runDir/mcd_res")
                val privateResDir = File(vmDir, "mcd_res")
                val guestResDir = if (sharedResDir.exists()) {
                    sharedResDir.absolutePath
                } else {
                    privateResDir.absolutePath
                }
                if (sharedResDir.exists()) {
                    try {
                        if (privateResDir.exists()) privateResDir.deleteRecursively()
                        sharedResDir.copyRecursively(privateResDir, overwrite = true)
                        log.add("Copied McD resources to private dir")
                    } catch (e: Exception) {
                        log.add("McD resource copy failed: ${e.message}")
                    }
                } else {
                    log.add("WARNING: missing extracted resources at ${sharedResDir.absolutePath}")
                }

                cmd = arrayOf(
                    dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
                    "-Xgc:nonconcurrent", "-Xms256m", "-Xmx768m",
                    *proxyArgs,
                    "-Dwestlake.framework.policy=$frameworkPolicy",
                    "-Dwestlake.apk.package=${apkConfig.packageName}",
                    "-Dwestlake.apk.activity=${apkConfig.activityName}",
                    "-Dwestlake.apk.path=$runDir/mcd_classes.dex",
                    "-Dwestlake.apk.resdir=$guestResDir",
                    "-Dwestlake.apk.manifest=$guestResDir/AndroidManifest.xml",
                    "-classpath", mcdCp.toString(),
                    "com.westlake.engine.WestlakeLauncher",
                    "--apk-path", "$runDir/mcd_classes.dex",
                    "--apk-activity", apkConfig.activityName,
                    "--apk-package", apkConfig.packageName,
                    "--apk-resdir", guestResDir,
                    "--apk-manifest", "$guestResDir/AndroidManifest.xml"
                )
                envApkPath = "$runDir/mcd_classes.dex"
                envActivityName = apkConfig.activityName
                envPackageName = apkConfig.packageName
                envResDir = guestResDir
                envManifestPath = "$guestResDir/AndroidManifest.xml"
                log.add("Multi-DEX: ${apkConfig.displayName} (Westlake dalvikvm)")
            } else {

            // Copy APK to app's private dir (writable by our process)
            val apkName = apkConfig.packageName.replace(".", "_") + ".apk"
            val apkDevicePath = "${vmDir.absolutePath}/$apkName"
            val apkDst = File(apkDevicePath)
            if (!apkDst.exists() || apkDst.length() != File(apkSrc).length()) {
                File(apkSrc).copyTo(apkDst, overwrite = true)
                log.add("Copied APK: ${apkConfig.displayName}")
            }

            // Extract ALL classesN.dex from the APK (multi-dex support)
            val dexPaths = mutableListOf<String>()
            try {
                val zipFile = java.util.zip.ZipFile(apkSrc)
                for (entry in zipFile.entries()) {
                    if (entry.name.startsWith("classes") && entry.name.endsWith(".dex")) {
                        val dexName = apkConfig.packageName.replace(".", "_") + "_" + entry.name
                        val dexPath = "${vmDir.absolutePath}/$dexName"
                        val dexFile = File(dexPath)
                        if (!dexFile.exists() || dexFile.length() != entry.size) {
                            zipFile.getInputStream(entry).use { inp ->
                                FileOutputStream(dexPath).use { out ->
                                    val buf = ByteArray(8192)
                                    var n: Int
                                    while (inp.read(buf).also { n = it } > 0) out.write(buf, 0, n)
                                }
                            }
                        }
                        dexPaths.add(dexPath)
                    }
                }
                zipFile.close()
                log.add("Extracted ${dexPaths.size} DEX files")
            } catch (e: Exception) {
                log.add("DEX extraction error: ${e.message}")
            }
            val dexClasspath = dexPaths.joinToString(":")

            // Extract resources.arsc for the shim's resource parser (no ZipFile JNI in dalvikvm)
            val resDir = File(vmDir, "apk_res").apply { mkdirs() }
            try {
                val zipFile = java.util.zip.ZipFile(apkSrc)
                for (entry in zipFile.entries()) {
                    if (entry.name == "resources.arsc" || entry.name.startsWith("res/") || entry.name == "AndroidManifest.xml") {
                        val outFile = File(resDir, entry.name)
                        outFile.parentFile?.mkdirs()
                        if (!entry.isDirectory) {
                            zipFile.getInputStream(entry).use { inp ->
                                FileOutputStream(outFile).use { out ->
                                    val buf = ByteArray(8192)
                                    var n: Int
                                    while (inp.read(buf).also { n = it } > 0) out.write(buf, 0, n)
                                }
                            }
                        }
                    }
                }
                zipFile.close()
                log.add("Extracted resources + res/ layouts")
            } catch (e: Exception) {
                log.add("Resource extraction: ${e.message}")
            }

            // Classpath: all the APK's DEX files (shim is in bootclasspath already)
            val manifestPath = "${resDir.absolutePath}/AndroidManifest.xml"
            cmd = arrayOf(
                dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
                "-Dwestlake.framework.policy=$frameworkPolicy",
                "-Dwestlake.apk.path=$apkDevicePath",
                "-Dwestlake.apk.activity=${apkConfig.activityName}",
                "-Dwestlake.apk.package=${apkConfig.packageName}",
                "-Dwestlake.apk.resdir=${resDir.absolutePath}",
                "-Dwestlake.apk.manifest=$manifestPath",
                "-classpath", dexClasspath,
                "com.westlake.engine.WestlakeLauncher",
                "--apk-path", apkDevicePath,
                "--apk-activity", apkConfig.activityName,
                "--apk-package", apkConfig.packageName,
                "--apk-resdir", resDir.absolutePath,
                "--apk-manifest", manifestPath
            )
            envApkPath = apkDevicePath
            envActivityName = apkConfig.activityName
            envPackageName = apkConfig.packageName
            envResDir = resDir.absolutePath
            envManifestPath = manifestPath
            log.add("Launching ${apkConfig.displayName} via WestlakeLauncher")
            } // end else (APK mode, not DEX-only)
        } else {
            // --- Legacy MockDonalds mode ---
            cmd = arrayOf(dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
                "-classpath", "$runDir/app.dex", "com.example.mockdonalds.MockDonaldsApp")
            log.add("Launching MockDonalds (legacy)")
        }

        // Ensure security.properties exists so SecureRandom/UUID work
        // (ANDROID_ROOT/etc/security/security.properties registers our Sun stub provider)
        val secDir = File(vmDir, "etc/security")
        secDir.mkdirs()
        val secProps = File(secDir, "security.properties")
        if (!secProps.exists()) {
            secProps.writeText("security.provider.1=sun.security.provider.Sun\n")
            log.add("Created security.properties")
        }

        // Ensure public.libraries.txt exists so libnativeloader doesn't abort
        val pubLibs = File(vmDir, "etc/public.libraries.txt")
        if (!pubLibs.exists()) {
            pubLibs.writeText("libandroid.so\nlibc.so\nlibdl.so\nliblog.so\nlibm.so\nlibz.so\n")
        }

        if (apkConfig != null) {
            val launchMeta = buildString {
                envApkPath?.let { append("westlake.apk.path=").append(it).append('\n') }
                envActivityName?.let { append("westlake.apk.activity=").append(it).append('\n') }
                envPackageName?.let { append("westlake.apk.package=").append(it).append('\n') }
                envResDir?.let { append("westlake.apk.resdir=").append(it).append('\n') }
                envManifestPath?.let { append("westlake.apk.manifest=").append(it).append('\n') }
            }
            if (launchMeta.isNotEmpty()) {
                File(runDir, "westlake-launch.properties").writeText(launchMeta)
                log.add("Wrote westlake-launch.properties")
            }
        }

        log.add("Starting from ${vmDir.absolutePath}...")
        try {
            val pb = ProcessBuilder(*cmd)
            pb.directory(File(runDir))
            // For dalvikvm64 (system binary): use real system paths
            val useSysDvm = cmd.firstOrNull() == "dalvikvm64" || cmd.firstOrNull() == "app_process64"
            pb.environment()["ANDROID_DATA"] = if (useSysDvm) "/data" else runDir
            pb.environment()["ANDROID_ROOT"] = if (useSysDvm) "/system" else runDir
            pb.environment()["ANDROID_ART_ROOT"] = "/apex/com.android.art"
            pb.environment()["ANDROID_I18N_ROOT"] = "/apex/com.android.i18n"
            pb.environment()["ANDROID_TZDATA_ROOT"] = "/apex/com.android.tzdata"
            envApkPath?.let { pb.environment()["WESTLAKE_APK_PATH"] = it }
            envActivityName?.let { pb.environment()["WESTLAKE_APK_ACTIVITY"] = it }
            envPackageName?.let { pb.environment()["WESTLAKE_APK_PACKAGE"] = it }
            envResDir?.let { pb.environment()["WESTLAKE_APK_RESDIR"] = it }
            envManifestPath?.let { pb.environment()["WESTLAKE_APK_MANIFEST"] = it }
            if (!useSysDvm) {
                pb.environment()["BOOTCLASSPATH"] = bcp
                pb.environment()["DEX2OATBOOTCLASSPATH"] = bcp
            }
            // For dalvikvm64: inherit system BOOTCLASSPATH (has all APEX JARs)
            pb.environment()["WESTLAKE_TOUCH"] = TOUCH_PATH
            // For dalvikvm64: .so files in shell_data_file context can't be executed.
            // Copy libframework_stubs.so to app's nativeLibDir (has app_data_file context).
            if (useSysDvm) {
                val stubSrc = File(runDir, "libframework_stubs.so")
                val stubDst = File(nativeLibDir, "libframework_stubs.so")
                if (stubSrc.exists() && (!stubDst.exists() || stubDst.length() != stubSrc.length())) {
                    try { stubSrc.copyTo(stubDst, overwrite = true); stubDst.setExecutable(true) }
                    catch (e: Exception) { Log.w(TAG, "Failed to copy stubs .so: $e") }
                }
                pb.environment()["LD_LIBRARY_PATH"] = "$nativeLibDir:$runDir"
            } else {
                pb.environment()["LD_LIBRARY_PATH"] = runDir
            }
            // For app_process64: CLASSPATH tells it which DEX to load
            val mcdApkEnv = try {
                activity.packageManager.getApplicationInfo("com.mcdonalds.app", 0).sourceDir
            } catch (e: Exception) { "" }
            if (cmd.firstOrNull() == "app_process64" && mcdApkEnv.isNotEmpty()) {
                pb.environment()["CLASSPATH"] = "$runDir/aosp-shim.dex:$mcdApkEnv"
            }
            // Do NOT redirectErrorStream — stdout is binary pipe, stderr is text logs
            process = pb.start()
            pipeStream = process!!.inputStream  // stdout = binary display list frames
            log.add("VM process started (pipe mode)")
            // Start a thread that polls until surface is ready, then starts pipe reader
            Thread({
                for (i in 0..100) {
                    if (surfaceHolder != null && pipeStream != null) {
                        startPipeReader()
                        if (pipeReaderThread?.isAlive == true) break
                    }
                    Thread.sleep(200)
                }
            }, "PipeReaderStarter").start()

            // Read stderr in background for logs
            Thread {
                try {
                    val reader = process!!.errorStream.bufferedReader()
                    var line = reader.readLine()
                    while (line != null) {
                        if (!line.contains("ziparchive:") && !line.contains("hidden_api") &&
                            line.isNotBlank()) {
                            Log.i(TAG, "VM: $line")
                        }
                        line = reader.readLine()
                    }
                    Log.i(TAG, "VM process exited")
                } catch (e: Exception) {
                    Log.e(TAG, "Stderr reader error: $e")
                }
            }.start()

        } catch (e: Exception) {
            log.add("Failed: ${e.message}")
            Log.e(TAG, "Start failed", e)
        }

        return log
    }

    fun sendText(text: String) {
        try {
            val textPath = File(TOUCH_PATH).parent + "/westlake_text.dat"
            FileOutputStream(textPath).use { it.write(text.toByteArray(Charsets.UTF_8)) }
            Log.i(TAG, "Sent text: $text")
        } catch (e: Exception) {
            Log.w(TAG, "Text write: $e")
        }
    }

    fun sendTouch(action: Int, x: Float, y: Float) {
        try {
            touchSeq++
            val buf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
            buf.putInt(action) // 0=DOWN, 1=UP, 2=MOVE
            buf.putInt(x.toInt())
            buf.putInt(y.toInt())
            buf.putInt(touchSeq)
            FileOutputStream(TOUCH_PATH).use { it.write(buf.array()) }
        } catch (e: Exception) {
            Log.w(TAG, "Touch write: $e")
        }
    }

    fun stop() {
        process?.destroyForcibly()
        process = null
        pipeStream = null
    }
}

/** Mutex to ensure only ONE pipe reader runs at a time */
private val pipeReaderMutex = Mutex()

/** Replay a display list byte array onto a Canvas. */
private var framesDumped = 0
private fun replayDisplayList(canvas: Canvas, paint: Paint, data: ByteArray, size: Int) {
    val bb = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN)
    val baseCount = canvas.saveCount
    canvas.save()
    canvas.drawColor(android.graphics.Color.BLACK)

    // Dump first 3 frames for debugging
    if (framesDumped < 3) {
        val ops = StringBuilder("Frame $framesDumped ($size bytes): ")
        val dbg = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN)
        while (dbg.hasRemaining()) {
            val op = dbg.get().toInt() and 0xFF
            ops.append("op$op ")
            if (ops.length > 200) { ops.append("..."); break }
        }
        Log.i("WestlakeVM", ops.toString())
        framesDumped++
    }

    try {
        while (bb.hasRemaining()) {
            when (bb.get().toInt() and 0xFF) {
                OP_DRAW_COLOR -> canvas.drawColor(bb.getInt())
                OP_DRAW_RECT -> {
                    val l = bb.getFloat(); val t = bb.getFloat()
                    val r = bb.getFloat(); val b = bb.getFloat()
                    paint.color = bb.getInt()
                    paint.style = Paint.Style.FILL
                    canvas.drawRect(l, t, r, b, paint)
                }
                OP_DRAW_TEXT -> {
                    val x = bb.getFloat(); val y = bb.getFloat()
                    paint.textSize = bb.getFloat()
                    paint.color = bb.getInt()
                    paint.style = Paint.Style.FILL
                    val len = bb.getShort().toInt() and 0xFFFF
                    if (len > 0 && bb.remaining() >= len) {
                        val chars = ByteArray(len)
                        bb.get(chars)
                        canvas.drawText(String(chars, Charsets.UTF_8), x, y, paint)
                    }
                }
                OP_DRAW_LINE -> {
                    val x1 = bb.getFloat(); val y1 = bb.getFloat()
                    val x2 = bb.getFloat(); val y2 = bb.getFloat()
                    paint.color = bb.getInt()
                    paint.strokeWidth = bb.getFloat()
                    paint.style = Paint.Style.STROKE
                    canvas.drawLine(x1, y1, x2, y2, paint)
                }
                OP_SAVE -> canvas.save()
                OP_RESTORE -> { if (canvas.saveCount > baseCount + 1) canvas.restore() }
                OP_TRANSLATE -> canvas.translate(bb.getFloat(), bb.getFloat())
                OP_CLIP_RECT -> {
                    val l = bb.getFloat(); val t = bb.getFloat()
                    val r = bb.getFloat(); val b = bb.getFloat()
                    canvas.clipRect(l, t, r, b)
                }
                OP_DRAW_ROUND_RECT -> {
                    val l = bb.getFloat(); val t = bb.getFloat()
                    val r = bb.getFloat(); val b = bb.getFloat()
                    val rx = bb.getFloat(); val ry = bb.getFloat()
                    paint.color = bb.getInt()
                    paint.style = Paint.Style.FILL
                    canvas.drawRoundRect(l, t, r, b, rx, ry, paint)
                }
                OP_DRAW_CIRCLE -> {
                    val cx = bb.getFloat(); val cy = bb.getFloat()
                    val radius = bb.getFloat()
                    paint.color = bb.getInt()
                    paint.style = Paint.Style.FILL
                    canvas.drawCircle(cx, cy, radius, paint)
                }
                OP_DRAW_IMAGE -> {
                    val x = bb.getFloat(); val y = bb.getFloat()
                    val w = bb.getInt(); val h = bb.getInt()
                    val dataLen = bb.getInt()
                    if (dataLen > 0 && bb.remaining() >= dataLen) {
                        val imgBytes = ByteArray(dataLen)
                        bb.get(imgBytes)
                        val bmp = android.graphics.BitmapFactory.decodeByteArray(imgBytes, 0, dataLen)
                        if (bmp != null) {
                            val dst = android.graphics.RectF(x, y, x + (if (w > 0) w.toFloat() else bmp.width.toFloat()),
                                y + (if (h > 0) h.toFloat() else bmp.height.toFloat()))
                            canvas.drawBitmap(bmp, null, dst, paint)
                            bmp.recycle()
                        }
                    }
                }
                OP_ARGB_BITMAP -> {
                    val x = bb.getFloat(); val y = bb.getFloat()
                    val w = bb.getInt(); val h = bb.getInt()
                    val dataLen = bb.getInt()
                    if (dataLen > 0 && w > 0 && h > 0 && bb.remaining() >= dataLen) {
                        val rgbaBytes = ByteArray(dataLen)
                        bb.get(rgbaBytes)
                        // Convert RGBA bytes to ARGB int array for Bitmap
                        val pixelCount = w * h
                        val pixels = IntArray(pixelCount)
                        for (i in 0 until minOf(pixelCount, dataLen / 4)) {
                            val r = rgbaBytes[i*4].toInt() and 0xFF
                            val g = rgbaBytes[i*4+1].toInt() and 0xFF
                            val b = rgbaBytes[i*4+2].toInt() and 0xFF
                            val a = rgbaBytes[i*4+3].toInt() and 0xFF
                            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
                        }
                        val bmp = android.graphics.Bitmap.createBitmap(pixels, w, h, android.graphics.Bitmap.Config.ARGB_8888)
                        val dst = android.graphics.RectF(x, y, x + w, y + h)
                        canvas.drawBitmap(bmp, null, dst, paint)
                        bmp.recycle()
                    }
                }
                else -> {
                    Log.w("WestlakeVM", "Unknown dlist op ${bb.position()-1}, aborting frame")
                    break
                }
            }
        }
    } catch (_: java.nio.BufferUnderflowException) {
        Log.w("WestlakeVM", "Display list truncated")
    }

    canvas.restoreToCount(baseCount)
}

/**
 * Read display list frames from pipe and render to SurfaceView.
 * Called from LaunchedEffect in composables.
 */
private suspend fun readPipeAndRender(
    holder: Any,
    stream: java.io.InputStream,
    isRunning: () -> Boolean,
    onFrame: () -> Unit
) = withContext(Dispatchers.IO) {
    if (!pipeReaderMutex.tryLock()) {
        Log.w("WestlakeVM", "Pipe reader already active, skipping duplicate")
        return@withContext
    }
    try { readPipeAndRenderLocked(holder, stream, isRunning, onFrame) }
    finally { pipeReaderMutex.unlock() }
}

private suspend fun readPipeAndRenderLocked(
    holder: Any,
    stream: java.io.InputStream,
    isRunning: () -> Boolean,
    onFrame: () -> Unit
) {
    val input = DataInputStream(stream)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        try { typeface = android.graphics.Typeface.createFromFile("/system/fonts/Roboto-Regular.ttf") } catch (_: Exception) {}
    }
    Log.i("WestlakeVM", "Pipe reader started, syncing to DLST magic...")
    try {
        // Sync past initial text to first DLST magic marker
        val syncBuf = ByteArray(4)
        input.readFully(syncBuf)
        var syncMagic = ByteBuffer.wrap(syncBuf).order(ByteOrder.LITTLE_ENDIAN).getInt()
        var skipped = 0
        while (syncMagic != DLIST_MAGIC && isRunning()) {
            syncBuf[0] = syncBuf[1]; syncBuf[1] = syncBuf[2]; syncBuf[2] = syncBuf[3]
            syncBuf[3] = input.readByte()
            syncMagic = ByteBuffer.wrap(syncBuf).order(ByteOrder.LITTLE_ENDIAN).getInt()
            skipped++
        }
        Log.i("WestlakeVM", "Sync complete: skipped=$skipped bytes, magic=0x${Integer.toHexString(syncMagic)}")

        while (isRunning()) {
            // Read [4-byte LE size]
            val sizeBytes = ByteArray(4)
            input.readFully(sizeBytes)
            val size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt()
            if (size <= 0 || size > 2 * 1024 * 1024) {  // 2MB for screen capture frames
                Log.w("WestlakeVM", "Bad frame size $size, skipping")
                continue
            }
            // Read [size bytes data]
            val data = ByteArray(size)
            input.readFully(data)

            val target = WestlakeVM.surfaceHolder ?: holder
            when (target) {
                is android.graphics.Bitmap -> {
                    val canvas = Canvas(target)
                    replayDisplayList(canvas, paint, data, size)
                    Log.i("WestlakeVM", "Frame: ${size} bytes → Bitmap, pixel(0,0)=0x${Integer.toHexString(target.getPixel(0,0))}")
                    // Save frames to app's cache dir (always writable)
                    if (framesDumped <= 3) {
                        try {
                            val cacheDir = WestlakeActivity.instance?.cacheDir
                            if (cacheDir != null) {
                                val f = java.io.File(cacheDir, "frame_${framesDumped}.png")
                                val os = java.io.FileOutputStream(f)
                                target.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, os)
                                os.close()
                                Log.i("WestlakeVM", "Saved ${f.absolutePath}")
                            }
                        } catch (e: Exception) { Log.e("WestlakeVM", "Save: $e") }
                    }
                }
                is SurfaceHolder -> {
                    val canvas = target.lockCanvas()
                    if (canvas == null) { Thread.sleep(100); continue }
                    try {
                        val sx = canvas.width / 480f; val sy = canvas.height / 800f
                        canvas.save(); canvas.scale(sx, sy)
                        replayDisplayList(canvas, paint, data, size)
                        canvas.restore()
                    } finally { target.unlockCanvasAndPost(canvas) }
                    Log.i("WestlakeVM", "Frame: ${size} bytes → Surface")
                }
                is android.view.Surface -> {
                    val canvas = try { target.lockHardwareCanvas() } catch (e: Exception) { null }
                    if (canvas == null) { Thread.sleep(100); continue }
                    try {
                        val sx = canvas.width / 480f; val sy = canvas.height / 800f
                        canvas.save(); canvas.scale(sx, sy)
                        replayDisplayList(canvas, paint, data, size)
                        canvas.restore()
                    } finally { target.unlockCanvasAndPost(canvas) }
                    Log.i("WestlakeVM", "Frame: ${size} bytes → TextureSurface")
                }
            }
            onFrame()

            // Read and sync to next frame's magic header
            val nb = ByteArray(4)
            input.readFully(nb)
            var nm = ByteBuffer.wrap(nb).order(ByteOrder.LITTLE_ENDIAN).getInt()
            if (nm != DLIST_MAGIC) {
                // Re-sync: scan forward for next DLST magic
                var rs = 0
                while (nm != DLIST_MAGIC && isRunning()) {
                    nb[0] = nb[1]; nb[1] = nb[2]; nb[2] = nb[3]
                    nb[3] = input.readByte()
                    nm = ByteBuffer.wrap(nb).order(ByteOrder.LITTLE_ENDIAN).getInt()
                    rs++
                }
                if (rs > 0) Log.w("WestlakeVM", "Re-synced past $rs bytes")
            }
        }
    } catch (_: java.io.EOFException) {
        Log.i("WestlakeVM", "Pipe closed (VM exited)")
    } catch (e: Exception) {
        Log.e("WestlakeVM", "Pipe reader error: $e")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WestlakeVMScreen() {
    val activity = WestlakeActivity.instance ?: return
    var logs by remember { mutableStateOf(listOf("Waiting...")) }
    var frameCount by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var holderState by remember { mutableStateOf<SurfaceHolder?>(null) }

    // Start VM
    LaunchedEffect(Unit) {
        logs = WestlakeVM.start()
        isRunning = true
    }

    // Single pipe reader — waits for SurfaceHolder, then reads until done
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        val stream = WestlakeVM.pipeStream ?: return@LaunchedEffect
        val holder = snapshotFlow { holderState }.filterNotNull().first()
        readPipeAndRender(holder, stream, { isRunning }) { frameCount++ }
    }

    // Cleanup on leave
    DisposableEffect(Unit) {
        onDispose {
            isRunning = false
            WestlakeVM.stop()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1B5E20)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isRunning = false
                    WestlakeVM.stop()
                    activity.showHome()
                }) { Text("\u2190", fontSize = 20.sp, color = Color.White) }
                Text("Westlake VM", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Frame: $frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            // Rendered frame display (SurfaceView + loading overlay)
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val scaleX = 480f / size.width
                            val scaleY = 800f / size.height
                            val down = awaitFirstDown(requireUnconsumed = false)
                            WestlakeVM.sendTouch(0, down.position.x * scaleX, down.position.y * scaleY)
                            Log.i("WestlakeVM", "DOWN: (${(down.position.x * scaleX).toInt()}, ${(down.position.y * scaleY).toInt()})")
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                val vx = change.position.x * scaleX
                                val vy = change.position.y * scaleY
                                if (change.pressed) {
                                    WestlakeVM.sendTouch(2, vx, vy)
                                } else {
                                    WestlakeVM.sendTouch(1, vx, vy)
                                    Log.i("WestlakeVM", "UP: (${vx.toInt()}, ${vy.toInt()})")
                                    break
                                }
                            } while (true)
                        }
                    }
            ) {
                // SurfaceView for direct Skia rendering
                AndroidView(
                    factory = { context ->
                        SurfaceView(context).also { sv ->
                            sv.holder.addCallback(object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    Log.i("WestlakeVM", "SurfaceCreated ${holder.surfaceFrame}")
                                    holder.setFixedSize(480, 800)
                                    WestlakeVM.surfaceHolder = holder
                                    WestlakeVM.onFrameCallback = { frameCount++ }
                                    WestlakeVM.startPipeReader()
                                }
                                override fun surfaceChanged(holder: SurfaceHolder, f: Int, w: Int, h: Int) {}
                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    WestlakeVM.surfaceHolder = null
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading overlay (shown until first frame arrives)
                if (frameCount == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(16.dp))
                        Text("Westlake VM starting...", color = Color.White.copy(0.6f), fontSize = 14.sp)
                        Text("(waiting for ART11 boot + first render)", color = Color.White.copy(0.4f), fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        logs.takeLast(3).forEach { line ->
                            Text(line, fontSize = 11.sp, color = Color.White.copy(0.5f))
                        }
                    }
                }
            }

            // Status bar
            Text(
                "dalvikvm ARM64 | OHBridge \u2192 Pipe \u2192 SurfaceView | Touch \u2192 file \u2192 VM",
                fontSize = 10.sp, color = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}

/**
 * WestlakeVMApkScreen — runs a real APK in the dalvikvm subprocess.
 * Same pipe IPC as WestlakeVMScreen but launches via WestlakeLauncher
 * with the APK's classes.dex extracted and loaded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WestlakeVMApkScreen(config: ApkVmConfig) {
    val activity = WestlakeActivity.instance ?: return
    var logs by remember { mutableStateOf(listOf("Preparing ${config.displayName}...")) }
    var frameCount by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var holderState by remember { mutableStateOf<SurfaceHolder?>(null) }

    // Resolve APK path and start VM on background thread (main thread must stay free for SurfaceView)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val resolvedConfig = try {
                val appInfo = activity.packageManager.getApplicationInfo(config.packageName, 0)
                config.copy(apkSourceDir = appInfo.sourceDir)
            } catch (e: Exception) {
                config.copy(apkSourceDir = null)
            }
            logs = WestlakeVM.startWithConfig(resolvedConfig)
        }
        isRunning = true
    }

    // Pipe reader is started directly from surfaceCreated callback via WestlakeVM.startPipeReader()

    DisposableEffect(Unit) {
        onDispose {
            isRunning = false
            WestlakeVM.stop()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF4A148C)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isRunning = false
                    WestlakeVM.stop()
                    activity.showHome()
                }) { Text("<-", fontSize = 20.sp, color = Color.White) }
                Text(config.displayName, fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    // Show text input dialog
                    val builder = android.app.AlertDialog.Builder(activity)
                    builder.setTitle("Enter text")
                    val input = android.widget.EditText(activity)
                    input.inputType = android.text.InputType.TYPE_CLASS_TEXT
                    builder.setView(input)
                    builder.setPositiveButton("Send") { _, _ ->
                        val text = input.text.toString()
                        if (text.isNotBlank()) WestlakeVM.sendText(text)
                    }
                    builder.setNegativeButton("Cancel", null)
                    builder.show()
                    input.requestFocus()
}) { Text("Aa", fontSize = 16.sp, color = Color.Yellow, fontWeight = FontWeight.Bold) }
                Text("$frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            // Rendered frame display
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val scaleX = 480f / size.width
                            val scaleY = 800f / size.height
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downTime = System.currentTimeMillis()
                            WestlakeVM.sendTouch(0, down.position.x * scaleX, down.position.y * scaleY)
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                val vx = change.position.x * scaleX
                                val vy = change.position.y * scaleY
                                if (change.pressed) {
                                    WestlakeVM.sendTouch(2, vx, vy)
                                } else {
                                    WestlakeVM.sendTouch(1, vx, vy)
                                    // Long press (>500ms) = show text input
                                    if (System.currentTimeMillis() - downTime > 500) {
                                        (activity as? android.app.Activity)?.runOnUiThread {
                                            val builder = android.app.AlertDialog.Builder(activity)
                                            builder.setTitle("Enter text")
                                            val input = android.widget.EditText(activity)
                                            input.inputType = android.text.InputType.TYPE_CLASS_TEXT
                                            builder.setView(input)
                                            builder.setPositiveButton("Send") { _, _ ->
                                                val text = input.text.toString()
                                                if (text.isNotBlank()) WestlakeVM.sendText(text)
                                            }
                                            builder.setNegativeButton("Cancel", null)
                                            builder.show()
                                            input.requestFocus()
                                        }
                                    }
                                    break
                                }
                            } while (true)
                        }
                    }
            ) {
                AndroidView(
                    factory = { context ->
                        SurfaceView(context).also { sv ->
                            sv.holder.addCallback(object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    Log.i("WestlakeVM", "SurfaceCreated ${holder.surfaceFrame}")
                                    holder.setFixedSize(480, 800)
                                    WestlakeVM.surfaceHolder = holder
                                    WestlakeVM.onFrameCallback = { frameCount++ }
                                    WestlakeVM.startPipeReader()
                                }
                                override fun surfaceChanged(holder: SurfaceHolder, f: Int, w: Int, h: Int) {}
                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    WestlakeVM.surfaceHolder = null
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading text — disabled to not interfere with SurfaceView creation
                if (false && frameCount == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF9C27B0))
                        Spacer(Modifier.height(16.dp))
                        Text("${config.displayName} starting...", color = Color.White.copy(0.6f), fontSize = 14.sp)
                        Text("(ART11 boot + APK load + first render)", color = Color.White.copy(0.4f), fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        logs.takeLast(4).forEach { line ->
                            Text(line, fontSize = 11.sp, color = Color.White.copy(0.5f))
                        }
                    }
                }
            }

            // Status bar
            Text(
                "dalvikvm ARM64 | ${config.packageName} | Pipe \u2192 SurfaceView",
                fontSize = 10.sp, color = Color(0xFF9C27B0),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}
