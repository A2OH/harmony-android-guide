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

/**
 * Configuration for launching an APK in the Westlake VM subprocess.
 * If null/blank, falls back to the original MockDonalds behavior.
 */
data class ApkVmConfig(
    val packageName: String,       // e.g. "me.tsukanov.counter"
    val activityName: String,      // e.g. "me.tsukanov.counter.ui.MainActivity"
    val displayName: String,       // e.g. "Simple Counter"
    val apkSourceDir: String? = null // resolved from PackageManager at launch time
)

object WestlakeVM {
    private const val TAG = "WestlakeVM"
    val TOUCH_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_touch.dat"
    private const val DALVIKVM_DIR = "/data/local/tmp/westlake"

    var process: Process? = null
    var pipeStream: java.io.InputStream? = null  // stdout pipe (binary display list frames)
    var touchSeq = 0

    // Boot image files for AOT startup (core JARs only, in arm64/ subdir)
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
        if (dvmSrc.exists() && (!dvmDst.exists() || dvmDst.length() != dvmSrc.length())) {
            dvmSrc.copyTo(dvmDst, overwrite = true)
            dvmDst.setExecutable(true, false)
            log.add("Copied dalvikvm")
        }

        // Run from /data/local/tmp/westlake/ directly so boot image paths match
        val dvm = dvmDst.absolutePath
        val runDir = DALVIKVM_DIR
        val bcp = "$runDir/core-oj.jar:$runDir/core-libart.jar:$runDir/core-icu4j.jar:$runDir/aosp-shim.dex"

        // Always use boot image when available (interpreter too slow for large shim)
        val hasBootImage = File("$runDir/arm64/boot.art").exists()
        val bootArgs = if (hasBootImage) {
            log.add("Boot image -- AOT mode")
            arrayOf("-Ximage:$runDir/boot.art")
        } else {
            log.add("Interpreter mode")
            arrayOf("-Xnoimage-dex2oat")
        }

        // Build command based on config
        val cmd: Array<String>

        if (apkConfig != null) {
            // --- APK mode: extract DEX, copy APK, use WestlakeLauncher ---
            val apkSrc = apkConfig.apkSourceDir
            // Check for DEX-only app (tipcalc.dex at runDir)
            val dexOnlyPath = "$runDir/${apkConfig.packageName.replace(".", "_")}.dex"
            val isDexOnly = (apkSrc == null || !File(apkSrc).exists()) && File(dexOnlyPath).exists()
            if (!isDexOnly && (apkSrc == null || !File(apkSrc).exists())) {
                log.add("ERROR: APK not found: $apkSrc (also checked $dexOnlyPath)")
                return log
            }

            if (isDexOnly) {
                // DEX-only app — add to bootclasspath, no APK extraction needed
                val fullBcp = "$bcp:$dexOnlyPath"
                cmd = arrayOf(
                    dvm, "-Xbootclasspath:$fullBcp", *bootArgs, "-Xverify:none",
                    "-Dwestlake.apk.path=$dexOnlyPath",
                    "-Dwestlake.apk.activity=${apkConfig.activityName}",
                    "-Dwestlake.apk.package=${apkConfig.packageName}",
                    "-classpath", dexOnlyPath,
                    "com.westlake.engine.WestlakeLauncher"
                )
                log.add("DEX-only: ${apkConfig.displayName}")
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
                "-Dwestlake.apk.path=$apkDevicePath",
                "-Dwestlake.apk.activity=${apkConfig.activityName}",
                "-Dwestlake.apk.package=${apkConfig.packageName}",
                "-Dwestlake.apk.resdir=${resDir.absolutePath}",
                "-Dwestlake.apk.manifest=$manifestPath",
                "-classpath", dexClasspath,
                "com.westlake.engine.WestlakeLauncher"
            )
            log.add("Launching ${apkConfig.displayName} via WestlakeLauncher")
            } // end else (APK mode, not DEX-only)
        } else {
            // --- Legacy MockDonalds mode ---
            cmd = arrayOf(dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
                "-classpath", "$runDir/app.dex", "com.example.mockdonalds.MockDonaldsApp")
            log.add("Launching MockDonalds (legacy)")
        }

        log.add("Starting from ${vmDir.absolutePath}...")
        try {
            val pb = ProcessBuilder(*cmd)
            pb.directory(File(runDir))
            pb.environment()["ANDROID_DATA"] = runDir
            pb.environment()["ANDROID_ROOT"] = runDir
            pb.environment()["WESTLAKE_TOUCH"] = TOUCH_PATH
            // Do NOT redirectErrorStream — stdout is binary pipe, stderr is text logs
            process = pb.start()
            pipeStream = process!!.inputStream  // stdout = binary display list frames
            log.add("VM process started (pipe mode)")

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
private fun replayDisplayList(canvas: Canvas, paint: Paint, data: ByteArray, size: Int) {
    val bb = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN)
    val baseCount = canvas.saveCount
    canvas.save()
    canvas.drawColor(android.graphics.Color.WHITE)

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
                else -> {
                    Log.w("WestlakeVM", "Unknown dlist op, aborting frame")
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
    holder: SurfaceHolder,
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
    holder: SurfaceHolder,
    stream: java.io.InputStream,
    isRunning: () -> Boolean,
    onFrame: () -> Unit
) {
    val input = DataInputStream(stream)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        try { typeface = android.graphics.Typeface.createFromFile("/system/fonts/Roboto-Regular.ttf") } catch (_: Exception) {}
    }
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
        if (skipped > 0) Log.i("WestlakeVM", "Synced past $skipped bytes of initial text")

        while (isRunning()) {
            // Read [4-byte LE size]
            val sizeBytes = ByteArray(4)
            input.readFully(sizeBytes)
            val size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt()
            if (size <= 0 || size > 512 * 1024) {
                Log.w("WestlakeVM", "Bad frame size $size, skipping")
                continue
            }
            // Read [size bytes data]
            val data = ByteArray(size)
            input.readFully(data)

            // Render to SurfaceView
            val canvas = holder.lockCanvas() ?: continue
            try {
                replayDisplayList(canvas, paint, data, size)
            } finally {
                holder.unlockCanvasAndPost(canvas)
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
                                    holder.setFixedSize(480, 800)
                                    holderState = holder
                                }
                                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                                override fun surfaceDestroyed(holder: SurfaceHolder) { holderState = null }
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

    // Resolve APK path and start VM
    LaunchedEffect(Unit) {
        val resolvedConfig = try {
            val appInfo = activity.packageManager.getApplicationInfo(config.packageName, 0)
            config.copy(apkSourceDir = appInfo.sourceDir)
        } catch (e: Exception) {
            // Not installed as APK — try as DEX-only app (e.g., tipcalc)
            config.copy(apkSourceDir = null)
        }
        logs = WestlakeVM.startWithConfig(resolvedConfig)
        isRunning = true
    }

    // Single pipe reader — waits for SurfaceHolder, then reads until done
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        val stream = WestlakeVM.pipeStream ?: return@LaunchedEffect
        val holder = snapshotFlow { holderState }.filterNotNull().first()
        readPipeAndRender(holder, stream, { isRunning }) { frameCount++ }
    }

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
                                    holder.setFixedSize(480, 800)
                                    holderState = holder
                                }
                                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                                override fun surfaceDestroyed(holder: SurfaceHolder) { holderState = null }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading overlay
                if (frameCount == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
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
