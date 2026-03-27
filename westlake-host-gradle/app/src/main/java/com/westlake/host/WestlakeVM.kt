package com.westlake.host

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Westlake VM Display — spawns dalvikvm as subprocess,
 * displays rendered PNG output, forwards touch input.
 *
 * Pipeline:
 *   dalvikvm → OHBridge.surfaceFlush() → shared memory (mmap) or PNG fallback
 *   This screen → reads shm at ~60fps (or polls PNG at ~12fps) → displays via Compose Image
 *   This screen → touch event → /sdcard/westlake_touch.dat → dalvikvm reads
 */
private const val WLK_HEADER_SIZE = 128
private const val WLK_FRAME_SIZE = 480 * 800 * 4  // 1,536,000
private const val WLK_TOTAL_SIZE = WLK_HEADER_SIZE + 2 * WLK_FRAME_SIZE
private const val WLK_MAGIC = 0x574C4B46

// Display list mode constants
private const val DLIST_MAX_SIZE = 512 * 1024  // 512KB
private const val WLK_DLIST_TOTAL_SIZE = WLK_HEADER_SIZE + DLIST_MAX_SIZE + 64

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
    // Use app's external files dir (accessible by both app and subprocess)
    val PNG_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_frame.png"
    val TOUCH_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_touch.dat"
    // Shared memory path — /data/local/tmp (real ext4, not FUSE) for fast mmap
    // Pre-created with: adb shell "dd if=/dev/zero of=...westlake_shm bs=1024 count=3001 && chmod 666 ..."
    val SHM_PATH: String get() = "/data/local/tmp/westlake/westlake_shm"
    private const val DALVIKVM_DIR = "/data/local/tmp/westlake"

    var process: Process? = null
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
     * If apkConfig is null, runs the original MockDonalds app.
     * If apkConfig is provided, extracts the APK's classes.dex, copies the APK,
     * and launches via the generic WestlakeLauncher.
     */
    fun startWithConfig(apkConfig: ApkVmConfig?): List<String> {
        Log.i(TAG, "startWithConfig() called, config=$apkConfig")
        val log = mutableListOf<String>()
        val activity = WestlakeActivity.instance ?: return listOf("No activity").also { Log.e(TAG, "No activity instance!") }

        // Kill any existing
        process?.destroyForcibly()
        try { File(PNG_PATH).delete() } catch (_: Exception) {}

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

        // Use boot image if available (ART inserts /arm64/ automatically)
        val hasBootImage = File("$runDir/arm64/boot.art").exists()
        val bootArgs = if (hasBootImage) {
            log.add("Boot image found -- using AOT mode")
            arrayOf("-Ximage:$runDir/boot.art")
        } else {
            log.add("No boot image -- interpreter mode (slow startup)")
            arrayOf("-Xnoimage-dex2oat")
        }

        // Build command based on config
        val cmd: Array<String>
        val apkDevicePath: String?

        if (apkConfig != null) {
            // --- APK mode: extract DEX, copy APK, use WestlakeLauncher ---
            val apkSrc = apkConfig.apkSourceDir
            if (apkSrc == null || !File(apkSrc).exists()) {
                log.add("ERROR: APK not found: $apkSrc")
                return log
            }

            // Copy APK to app's private dir (writable by our process)
            val apkName = apkConfig.packageName.replace(".", "_") + ".apk"
            apkDevicePath = "${vmDir.absolutePath}/$apkName"
            val apkDst = File(apkDevicePath)
            if (!apkDst.exists() || apkDst.length() != File(apkSrc).length()) {
                File(apkSrc).copyTo(apkDst, overwrite = true)
                log.add("Copied APK: ${apkConfig.displayName}")
            }

            // Extract classes.dex from the APK
            val dexName = apkConfig.packageName.replace(".", "_") + ".dex"
            val dexPath = "${vmDir.absolutePath}/$dexName"
            try {
                val zipFile = java.util.zip.ZipFile(apkSrc)
                val dexEntry = zipFile.getEntry("classes.dex")
                if (dexEntry != null) {
                    val input = zipFile.getInputStream(dexEntry)
                    FileOutputStream(dexPath).use { out ->
                        val buf = ByteArray(8192)
                        var n: Int
                        while (input.read(buf).also { n = it } > 0) out.write(buf, 0, n)
                    }
                    input.close()
                    log.add("Extracted classes.dex (${File(dexPath).length() / 1024}KB)")
                } else {
                    log.add("WARNING: No classes.dex in APK")
                }
                zipFile.close()
            } catch (e: Exception) {
                log.add("DEX extraction error: ${e.message}")
            }

            // Classpath: the APK's DEX (shim is in bootclasspath already)
            val classpath = dexPath

            cmd = arrayOf(
                dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
                "-Dwestlake.apk.path=$apkDevicePath",
                "-Dwestlake.apk.activity=${apkConfig.activityName}",
                "-Dwestlake.apk.package=${apkConfig.packageName}",
                "-classpath", classpath,
                "com.westlake.engine.WestlakeLauncher"
            )
            log.add("Launching ${apkConfig.displayName} via WestlakeLauncher")
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
            pb.environment()["WESTLAKE_FRAME"] = PNG_PATH
            pb.environment()["WESTLAKE_TOUCH"] = TOUCH_PATH
            pb.environment()["WESTLAKE_SHM"] = SHM_PATH
            pb.environment()["WESTLAKE_DLIST"] = "1"
            pb.redirectErrorStream(true)
            process = pb.start()
            log.add("VM process started")

            // Read output in background (no line limit -- keep reading until process exits)
            Thread {
                try {
                    val reader = process!!.inputStream.bufferedReader()
                    var line = reader.readLine()
                    while (line != null) {
                        // Log everything important, filter only verbose noise
                        if (!line.contains("ziparchive:") && !line.contains("hidden_api") &&
                            line.isNotBlank()) {
                            Log.i(TAG, "VM: $line")
                        }
                        line = reader.readLine()
                    }
                    Log.i(TAG, "VM process exited")
                } catch (e: Exception) {
                    Log.e(TAG, "Reader error: $e")
                }
            }.start()

        } catch (e: Exception) {
            log.add("Failed: ${e.message}")
            Log.e(TAG, "Start failed", e)
        }

        return log
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WestlakeVMScreen() {
    val activity = WestlakeActivity.instance ?: return
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf(listOf("Waiting...")) }
    var frameBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var frameCount by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    // Start VM
    LaunchedEffect(Unit) {
        logs = WestlakeVM.start()
        isRunning = true
    }

    // Read frames from shared memory (mmap), fallback to PNG polling
    LaunchedEffect(isRunning) {
        // Wait for shm file to appear
        val shmFile = File(WestlakeVM.SHM_PATH)
        var attempts = 0
        while (isRunning && !shmFile.exists() && attempts < 100) {
            delay(100)
            attempts++
        }

        if (shmFile.exists() && shmFile.length() >= WLK_HEADER_SIZE) {
            // Wait for header magic to be written by C side (race with shm_init)
            var version = 0
            for (retry in 0 until 50) {
                val peekRaf = RandomAccessFile(shmFile, "r")
                val peekBuf = ByteArray(32)
                peekRaf.read(peekBuf)
                peekRaf.close()
                val peekBB = ByteBuffer.wrap(peekBuf).order(ByteOrder.LITTLE_ENDIAN)
                val magic = peekBB.getInt(0)
                version = peekBB.getInt(4)
                if (magic == WLK_MAGIC) break
                delay(100)
            }
            Log.i("WestlakeVM", "SHM version=$version, file=${shmFile.length()} bytes")

            if (version == 2 && shmFile.length() >= WLK_DLIST_TOTAL_SIZE) {
                // ── Display list replay mode ──
                Log.i("WestlakeVM", "Display list mode (version=2)")
                val raf = RandomAccessFile(shmFile, "r")
                val channel = raf.channel
                val shm = channel.map(FileChannel.MapMode.READ_ONLY, 0, WLK_DLIST_TOTAL_SIZE.toLong())
                shm.order(ByteOrder.LITTLE_ENDIAN)

                // Double-buffer: draw to bmpA, display bmpB, swap each frame
                val bmpA = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
                val bmpB = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
                var drawBmp = bmpA
                var showBmp = bmpB
                val canvasA = Canvas(bmpA)
                val canvasB = Canvas(bmpB)
                var canvas = canvasA
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = android.graphics.Typeface.createFromFile("/system/fonts/Roboto-Regular.ttf")
                }
                var lastSeq = -1

                try {
                    while (isRunning) {
                        val seq = shm.getInt(16) // frame_seq
                        if (seq != lastSeq) {
                            lastSeq = seq
                            val listSize = shm.getInt(20) // display_list_size

                            // Save base state and clear canvas
                            val baseCount = canvas.saveCount
                            canvas.save()
                            canvas.drawColor(android.graphics.Color.WHITE)

                            // Replay display list
                            var offset = WLK_HEADER_SIZE
                            val endOffset = WLK_HEADER_SIZE + listSize.coerceAtMost(DLIST_MAX_SIZE)
                            while (offset < endOffset) {
                                val op = shm.get(offset).toInt() and 0xFF
                                offset++
                                when (op) {
                                    OP_DRAW_COLOR -> {
                                        canvas.drawColor(shm.getInt(offset))
                                        offset += 4
                                    }
                                    OP_DRAW_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawRect(l, t, r, b, paint)
                                    }
                                    OP_DRAW_TEXT -> {
                                        val x = shm.getFloat(offset); offset += 4
                                        val y = shm.getFloat(offset); offset += 4
                                        val size = shm.getFloat(offset); offset += 4
                                        paint.textSize = size
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        val len = shm.getShort(offset).toInt() and 0xFFFF; offset += 2
                                        if (len > 0 && offset + len <= endOffset) {
                                            val chars = ByteArray(len)
                                            for (i in 0 until len) chars[i] = shm.get(offset + i)
                                            offset += len
                                            // Replace comma+space and period with clean ASCII to debug font issue
                                            val text = String(chars, Charsets.UTF_8)
                                            canvas.drawText(text, x, y, paint)
                                        } else {
                                            offset += len
                                        }
                                    }
                                    OP_DRAW_LINE -> {
                                        val x1 = shm.getFloat(offset); offset += 4
                                        val y1 = shm.getFloat(offset); offset += 4
                                        val x2 = shm.getFloat(offset); offset += 4
                                        val y2 = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.strokeWidth = shm.getFloat(offset); offset += 4
                                        paint.style = Paint.Style.STROKE
                                        canvas.drawLine(x1, y1, x2, y2, paint)
                                    }
                                    OP_SAVE -> {
                                        canvas.save()
                                    }
                                    OP_RESTORE -> {
                                        if (canvas.saveCount > baseCount + 1) canvas.restore()
                                    }
                                    OP_TRANSLATE -> {
                                        val dx = shm.getFloat(offset); offset += 4
                                        val dy = shm.getFloat(offset); offset += 4
                                        canvas.translate(dx, dy)
                                    }
                                    OP_CLIP_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        canvas.clipRect(l, t, r, b)
                                    }
                                    OP_DRAW_ROUND_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        val rx = shm.getFloat(offset); offset += 4
                                        val ry = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawRoundRect(l, t, r, b, rx, ry, paint)
                                    }
                                    OP_DRAW_CIRCLE -> {
                                        val cx = shm.getFloat(offset); offset += 4
                                        val cy = shm.getFloat(offset); offset += 4
                                        val radius = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawCircle(cx, cy, radius, paint)
                                    }
                                    else -> {
                                        Log.w("WestlakeVM", "Unknown dlist op $op at offset ${offset-1}, aborting frame")
                                        break
                                    }
                                }
                            }

                            // Restore to base state (handles unbalanced save/restores)
                            canvas.restoreToCount(baseCount)
                            // Swap buffers: show what we just drew, draw to the other next frame
                            frameBitmap = drawBmp
                            frameCount++
                            val tmp = drawBmp; drawBmp = showBmp; showBmp = tmp
                            canvas = if (drawBmp === bmpA) canvasA else canvasB
                        }
                        delay(8) // fast polling
                    }
                } finally {
                    channel.close()
                    raf.close()
                }
            } else if (shmFile.length() >= WLK_TOTAL_SIZE) {
                // ── Pixel buffer mode (version 1) ──
                Log.i("WestlakeVM", "Pixel buffer mode (version=1)")
                val raf = RandomAccessFile(shmFile, "r")
                val channel = raf.channel
                val shm = channel.map(FileChannel.MapMode.READ_ONLY, 0, WLK_TOTAL_SIZE.toLong())
                shm.order(ByteOrder.LITTLE_ENDIAN)

                val bmp = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
                var lastSeq = -1

                try {
                    while (isRunning) {
                        val seq = shm.getInt(16) // frame_seq
                        if (seq != lastSeq) {
                            lastSeq = seq
                            val activeBuf = shm.getInt(20)
                            val offset = WLK_HEADER_SIZE + activeBuf * WLK_FRAME_SIZE

                            // Copy pixels to bitmap
                            shm.position(offset)
                            bmp.copyPixelsFromBuffer(shm)

                            frameBitmap = bmp
                            frameCount++
                        }
                        delay(8) // fast polling
                    }
                } finally {
                    channel.close()
                    raf.close()
                }
            } else {
                Log.w("WestlakeVM", "SHM file too small (${shmFile.length()} bytes), falling back to PNG")
                // Inline PNG fallback for this edge case
                while (isRunning) {
                    delay(80)
                    try {
                        val file = File(WestlakeVM.PNG_PATH)
                        if (file.exists() && file.length() > 100) {
                            val bmp = BitmapFactory.decodeFile(file.absolutePath)
                            if (bmp != null) { frameBitmap = bmp; frameCount++ }
                        }
                    } catch (_: Exception) {}
                }
            }
        } else {
            // Fallback to PNG polling if no shm
            while (isRunning) {
                delay(80) // ~12fps polling for responsive scrolling
                try {
                    val file = File(WestlakeVM.PNG_PATH)
                    if (file.exists() && file.length() > 100) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (bmp != null) {
                            frameBitmap = bmp
                            frameCount++
                        }
                    }
                } catch (_: Exception) {}
            }
        }
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
                }) { Text("←", fontSize = 20.sp, color = Color.White) }
                Text("Westlake VM", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Frame: $frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            // Rendered frame display
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val scaleX = 480f / size.width
                            val scaleY = 800f / size.height

                            // Wait for first finger down
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val vx0 = down.position.x * scaleX
                            val vy0 = down.position.y * scaleY
                            WestlakeVM.sendTouch(0, vx0, vy0) // DOWN
                            Log.i("WestlakeVM", "DOWN: (${vx0.toInt()}, ${vy0.toInt()})")

                            // Track moves until release
                            var lastMoveSeq = 0
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                val vx = change.position.x * scaleX
                                val vy = change.position.y * scaleY

                                if (change.pressed) {
                                    // Send every MOVE event for responsive scrolling
                                    WestlakeVM.sendTouch(2, vx, vy) // MOVE
                                } else {
                                    // Finger lifted — send UP
                                    WestlakeVM.sendTouch(1, vx, vy) // UP
                                    Log.i("WestlakeVM", "UP: (${vx.toInt()}, ${vy.toInt()})")
                                    break
                                }
                            } while (true)
                        }
                    }
            ) {
                frameBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Westlake VM Output",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                "dalvikvm ARM64 | OHBridge → DisplayList IPC → Canvas replay | Touch → file → VM",
                fontSize = 10.sp, color = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}

/**
 * WestlakeVMApkScreen — runs a real APK in the dalvikvm subprocess.
 * Same display list IPC as WestlakeVMScreen but launches via WestlakeLauncher
 * with the APK's classes.dex extracted and loaded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WestlakeVMApkScreen(config: ApkVmConfig) {
    val activity = WestlakeActivity.instance ?: return
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf(listOf("Preparing ${config.displayName}...")) }
    var frameBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var frameCount by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    // Resolve APK path and start VM
    LaunchedEffect(Unit) {
        // Resolve APK source dir from PackageManager
        val resolvedConfig = try {
            val appInfo = activity.packageManager.getApplicationInfo(config.packageName, 0)
            config.copy(apkSourceDir = appInfo.sourceDir)
        } catch (e: Exception) {
            logs = listOf("ERROR: APK not installed: ${config.packageName}", e.message ?: "")
            return@LaunchedEffect
        }
        logs = WestlakeVM.startWithConfig(resolvedConfig)
        isRunning = true
    }

    // Frame reader — identical to WestlakeVMScreen (display list replay)
    LaunchedEffect(isRunning) {
        val shmFile = File(WestlakeVM.SHM_PATH)
        var attempts = 0
        while (isRunning && !shmFile.exists() && attempts < 100) {
            delay(100)
            attempts++
        }

        if (shmFile.exists() && shmFile.length() >= WLK_HEADER_SIZE) {
            var version = 0
            for (retry in 0 until 50) {
                val peekRaf = RandomAccessFile(shmFile, "r")
                val peekBuf = ByteArray(32)
                peekRaf.read(peekBuf)
                peekRaf.close()
                val peekBB = ByteBuffer.wrap(peekBuf).order(ByteOrder.LITTLE_ENDIAN)
                val magic = peekBB.getInt(0)
                version = peekBB.getInt(4)
                if (magic == WLK_MAGIC) break
                delay(100)
            }
            Log.i("WestlakeVM", "APK SHM version=$version, file=${shmFile.length()} bytes")

            if (version == 2 && shmFile.length() >= WLK_DLIST_TOTAL_SIZE) {
                Log.i("WestlakeVM", "APK display list mode (version=2)")
                val raf = RandomAccessFile(shmFile, "r")
                val channel = raf.channel
                val shm = channel.map(FileChannel.MapMode.READ_ONLY, 0, WLK_DLIST_TOTAL_SIZE.toLong())
                shm.order(ByteOrder.LITTLE_ENDIAN)

                val bmpA = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
                val bmpB = android.graphics.Bitmap.createBitmap(480, 800, android.graphics.Bitmap.Config.ARGB_8888)
                var drawBmp = bmpA
                var showBmp = bmpB
                val canvasA = Canvas(bmpA)
                val canvasB = Canvas(bmpB)
                var canvas = canvasA
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    try { typeface = android.graphics.Typeface.createFromFile("/system/fonts/Roboto-Regular.ttf") } catch (_: Exception) {}
                }
                var lastSeq = -1

                try {
                    while (isRunning) {
                        val seq = shm.getInt(16)
                        if (seq != lastSeq) {
                            lastSeq = seq
                            val listSize = shm.getInt(20)
                            val baseCount = canvas.saveCount
                            canvas.save()
                            canvas.drawColor(android.graphics.Color.WHITE)

                            var offset = WLK_HEADER_SIZE
                            val endOffset = WLK_HEADER_SIZE + listSize.coerceAtMost(DLIST_MAX_SIZE)
                            while (offset < endOffset) {
                                val op = shm.get(offset).toInt() and 0xFF
                                offset++
                                when (op) {
                                    OP_DRAW_COLOR -> { canvas.drawColor(shm.getInt(offset)); offset += 4 }
                                    OP_DRAW_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawRect(l, t, r, b, paint)
                                    }
                                    OP_DRAW_TEXT -> {
                                        val x = shm.getFloat(offset); offset += 4
                                        val y = shm.getFloat(offset); offset += 4
                                        val size = shm.getFloat(offset); offset += 4
                                        paint.textSize = size
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        val len = shm.getShort(offset).toInt() and 0xFFFF; offset += 2
                                        if (len > 0 && offset + len <= endOffset) {
                                            val chars = ByteArray(len)
                                            for (i in 0 until len) chars[i] = shm.get(offset + i)
                                            offset += len
                                            canvas.drawText(String(chars, Charsets.UTF_8), x, y, paint)
                                        } else { offset += len }
                                    }
                                    OP_DRAW_LINE -> {
                                        val x1 = shm.getFloat(offset); offset += 4
                                        val y1 = shm.getFloat(offset); offset += 4
                                        val x2 = shm.getFloat(offset); offset += 4
                                        val y2 = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.strokeWidth = shm.getFloat(offset); offset += 4
                                        paint.style = Paint.Style.STROKE
                                        canvas.drawLine(x1, y1, x2, y2, paint)
                                    }
                                    OP_SAVE -> canvas.save()
                                    OP_RESTORE -> { if (canvas.saveCount > baseCount + 1) canvas.restore() }
                                    OP_TRANSLATE -> {
                                        val dx = shm.getFloat(offset); offset += 4
                                        val dy = shm.getFloat(offset); offset += 4
                                        canvas.translate(dx, dy)
                                    }
                                    OP_CLIP_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        canvas.clipRect(l, t, r, b)
                                    }
                                    OP_DRAW_ROUND_RECT -> {
                                        val l = shm.getFloat(offset); offset += 4
                                        val t = shm.getFloat(offset); offset += 4
                                        val r = shm.getFloat(offset); offset += 4
                                        val b = shm.getFloat(offset); offset += 4
                                        val rx = shm.getFloat(offset); offset += 4
                                        val ry = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawRoundRect(l, t, r, b, rx, ry, paint)
                                    }
                                    OP_DRAW_CIRCLE -> {
                                        val cx = shm.getFloat(offset); offset += 4
                                        val cy = shm.getFloat(offset); offset += 4
                                        val radius = shm.getFloat(offset); offset += 4
                                        paint.color = shm.getInt(offset); offset += 4
                                        paint.style = Paint.Style.FILL
                                        canvas.drawCircle(cx, cy, radius, paint)
                                    }
                                    else -> { Log.w("WestlakeVM", "Unknown dlist op $op at offset ${offset-1}"); break }
                                }
                            }
                            canvas.restoreToCount(baseCount)
                            frameBitmap = drawBmp
                            frameCount++
                            val tmp = drawBmp; drawBmp = showBmp; showBmp = tmp
                            canvas = if (drawBmp === bmpA) canvasA else canvasB
                        }
                        delay(8)
                    }
                } finally { channel.close(); raf.close() }
            } else {
                // Fallback to PNG polling
                while (isRunning) {
                    delay(80)
                    try {
                        val file = File(WestlakeVM.PNG_PATH)
                        if (file.exists() && file.length() > 100) {
                            val bmp = BitmapFactory.decodeFile(file.absolutePath)
                            if (bmp != null) { frameBitmap = bmp; frameCount++ }
                        }
                    } catch (_: Exception) {}
                }
            }
        } else {
            while (isRunning) {
                delay(80)
                try {
                    val file = File(WestlakeVM.PNG_PATH)
                    if (file.exists() && file.length() > 100) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (bmp != null) { frameBitmap = bmp; frameCount++ }
                    }
                } catch (_: Exception) {}
            }
        }
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
                Text("Frame: $frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            // Rendered frame display
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val scaleX = 480f / size.width
                            val scaleY = 800f / size.height
                            val down = awaitFirstDown(requireUnconsumed = false)
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
                                    break
                                }
                            } while (true)
                        }
                    }
            ) {
                frameBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "${config.displayName} VM Output",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
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
                "dalvikvm ARM64 | ${config.packageName} | DisplayList IPC",
                fontSize = 10.sp, color = Color(0xFF9C27B0),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}
