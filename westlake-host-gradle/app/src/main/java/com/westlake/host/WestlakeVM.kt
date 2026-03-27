package com.westlake.host

import android.graphics.BitmapFactory
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

    fun start(): List<String> {
        Log.i(TAG, "start() called")
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
            log.add("Boot image found — using AOT mode")
            arrayOf("-Ximage:$runDir/boot.art")
        } else {
            log.add("No boot image — interpreter mode (slow startup)")
            arrayOf("-Xnoimage-dex2oat")
        }

        val cmd = arrayOf(dvm, "-Xbootclasspath:$bcp", *bootArgs, "-Xverify:none",
            "-classpath", "$runDir/app.dex", "com.example.mockdonalds.MockDonaldsApp")

        log.add("Starting from ${vmDir.absolutePath}...")
        try {
            val pb = ProcessBuilder(*cmd)
            pb.directory(File(runDir))
            pb.environment()["ANDROID_DATA"] = runDir
            pb.environment()["ANDROID_ROOT"] = runDir
            pb.environment()["WESTLAKE_FRAME"] = PNG_PATH
            pb.environment()["WESTLAKE_TOUCH"] = TOUCH_PATH
            pb.environment()["WESTLAKE_SHM"] = SHM_PATH
            pb.redirectErrorStream(true)
            process = pb.start()
            log.add("VM process started")

            // Read output in background (no line limit — keep reading until process exits)
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

        if (shmFile.exists() && shmFile.length() >= WLK_TOTAL_SIZE) {
            // mmap the shared memory
            val raf = RandomAccessFile(shmFile, "r")
            val channel = raf.channel
            val shm = channel.map(FileChannel.MapMode.READ_ONLY, 0, WLK_TOTAL_SIZE.toLong())
            shm.order(ByteOrder.LITTLE_ENDIAN)

            // Pre-allocate bitmap
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
                    delay(16) // ~60fps polling
                }
            } finally {
                channel.close()
                raf.close()
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
                "dalvikvm ARM64 | OHBridge → SHM/PNG → Compose | Touch → file → VM",
                fontSize = 10.sp, color = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}
