package com.westlake.host

import android.content.Context
import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Base64
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
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
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipFile
import org.json.JSONArray
import org.json.JSONObject

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
private const val OP_DRAW_PATH       = 13
// Path command tags inside OP_DRAW_PATH payload (must match ohbridge_stub.c PCMD_*)
private const val PCMD_MOVE   = 0
private const val PCMD_LINE   = 1
private const val PCMD_QUAD   = 2
private const val PCMD_CUBIC  = 3
private const val PCMD_CLOSE  = 4
private const val IMAGE_CACHE_MAX = 48
private const val GUEST_FRAME_WIDTH = 480f
private const val GUEST_FRAME_HEIGHT = 800f
private const val YELP_GUEST_FRAME_WIDTH = 480f
private const val YELP_GUEST_FRAME_HEIGHT = 1013.3333f
private const val LEGACY_SURFACE_BUFFER_WIDTH = 480
private const val LEGACY_SURFACE_BUFFER_HEIGHT = 800
private const val YELP_SURFACE_BUFFER_WIDTH = 1080
private const val YELP_SURFACE_BUFFER_HEIGHT = 2280
private const val MATERIAL_YELP_SURFACE_BUFFER_HEIGHT = 1800
private val imageBitmapCacheLock = Any()
private val imageBitmapCache = object : LinkedHashMap<Int, Bitmap>(IMAGE_CACHE_MAX, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Bitmap>?): Boolean {
        val shouldRemove = size > IMAGE_CACHE_MAX
        if (shouldRemove) {
            eldest?.value?.recycle()
        }
        return shouldRemove
    }
}

/**
 * Configuration for launching an APK in the Westlake VM subprocess.
 * If null/blank, falls back to the original MockDonalds behavior.
 */
enum class BackendMode(
    val propertyValue: String,
    val frameworkPolicy: String,
) {
    TARGET_OHOS_BACKEND("target_ohos_backend", "westlake_only"),
    CONTROL_ANDROID_BACKEND("control_android_backend", "allow_real");

    companion object {
        fun fromLegacyAllowReal(allowRealFrameworkFallback: Boolean): BackendMode {
            return if (allowRealFrameworkFallback) {
                CONTROL_ANDROID_BACKEND
            } else {
                TARGET_OHOS_BACKEND
            }
        }
    }
}

data class ApkVmConfig(
    val packageName: String,       // e.g. "me.tsukanov.counter"
    val activityName: String,      // e.g. "me.tsukanov.counter.ui.MainActivity"
    val displayName: String,       // e.g. "Simple Counter"
    val apkSourceDir: String? = null, // resolved from PackageManager at launch time
    val allowRealFrameworkFallback: Boolean = false,
    val backendMode: BackendMode? = null,
    val extraVmProperties: Map<String, String> = emptyMap(),
) {
    fun effectiveBackendMode(): BackendMode {
        return backendMode ?: BackendMode.fromLegacyAllowReal(allowRealFrameworkFallback)
    }
}

object WestlakeVM {
    private const val TAG = "WestlakeVM"
    val TOUCH_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_touch.dat"
    private const val DALVIKVM_DIR = "/data/local/tmp/westlake"

    // M6-Step4 surface-daemon DLST FIFO path. Must match
    // `aosp-surface-daemon-port/native/surfaceflinger_main.cpp:kDefaultDlstPipe`
    // and the path written by `m6step4-smoke.sh` / `m6step5-smoke.sh`. The
    // daemon honors `$WESTLAKE_DLST_PIPE` to override; for the host this is
    // currently a compile-time constant (CR46 reader-path wiring).
    const val WESTLAKE_DLST_FIFO: String = "/data/local/tmp/westlake/dlst.fifo"

    var process: Process? = null
    var pipeStream: java.io.InputStream? = null  // stdout pipe (binary display list frames)
    var touchSeq = 0
    @Volatile var surfaceHolder: Any? = null  // SurfaceHolder, Surface, or DisplayListFrameView
    @Volatile var guestFrameWidth: Float = GUEST_FRAME_WIDTH
    @Volatile var guestFrameHeight: Float = GUEST_FRAME_HEIGHT
    var onFrameCallback: (() -> Unit)? = null
    private var pipeReaderThread: Thread? = null
    @Volatile private var httpBridgeRunning = false
    @Volatile private var httpBridgeThread: Thread? = null
    private var httpBridgeReadOffset = 0L

    private class TailFileInputStream(
        private val file: File,
        private val isProducerAlive: () -> Boolean,
    ) : java.io.InputStream() {
        private var offset = 0L
        @Volatile private var closed = false

        override fun read(): Int {
            val one = ByteArray(1)
            val count = read(one, 0, 1)
            return if (count <= 0) -1 else one[0].toInt() and 0xff
        }

        override fun read(buffer: ByteArray, byteOffset: Int, byteCount: Int): Int {
            if (byteCount == 0) return 0
            while (true) {
                if (closed || Thread.currentThread().isInterrupted) return -1
                val fileLength = file.length()
                if (fileLength < offset) offset = fileLength
                val available = fileLength - offset
                if (available > 0L) {
                    val count = minOf(byteCount.toLong(), available).toInt()
                    RandomAccessFile(file, "r").use { raf ->
                        raf.seek(offset)
                        val read = raf.read(buffer, byteOffset, count)
                        if (read > 0) {
                            offset += read.toLong()
                            return read
                        }
                    }
                }
                if (!isProducerAlive()) return -1
                try {
                    Thread.sleep(20)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return -1
                }
            }
        }

        override fun close() {
            closed = true
        }
    }

    private fun drainVmStdout(stream: java.io.InputStream) {
        Thread({
            try {
                stream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank()) Log.i(TAG, "VM-OUT: $line")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Stdout drain ended: $e")
            }
        }, "VMStdoutDrain").apply {
            isDaemon = true
            start()
        }
    }

    private fun closePipeReader() {
        val stream = pipeStream
        pipeStream = null
        try {
            stream?.close()
        } catch (_: Exception) {
        }
        val thread = pipeReaderThread
        if (thread != null && thread.isAlive) {
            thread.interrupt()
            try {
                thread.join(300)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        pipeReaderThread = null
    }

    /**
     * CR46 — connect this VM's pipe reader to the M6 surface-daemon's DLST
     * FIFO at `WESTLAKE_DLST_FIFO`. This is the daemon-side companion to
     * the legacy `process.inputStream` path (subprocess stdout): instead of
     * spawning a dalvikvm subprocess and reading its binary stdout, this
     * blocks-opens the named FIFO that `aosp-surface-daemon-port`'s
     * `DlstConsumer::writeFrame` writes into.
     *
     * The wire format the daemon emits is byte-for-byte identical to the
     * pipe mode used by `process.inputStream` (`DLIST_MAGIC=0x444C5354` +
     * 4B size + payload starting with `OP_ARGB_BITMAP=12`), so the existing
     * `readPipeAndRenderLocked` is reused unchanged.
     *
     * The call is idempotent: if a FIFO stream is already attached (or a
     * subprocess pipe is currently in use), the existing stream is left
     * alone. `O_RDONLY` on a FIFO blocks until the writer (daemon) opens
     * for write — that's the intended back-pressure: with no daemon
     * running, the FIFO open blocks indefinitely (we therefore open on a
     * daemon thread, never on the UI thread).
     *
     * Returns true if a fresh FIFO connection was made, false otherwise
     * (already connected / open failed). On open failure logs and gives
     * up; the caller can retry later.
     */
    fun connectDaemonFifo(path: String = WESTLAKE_DLST_FIFO): Boolean {
        if (pipeStream != null) {
            Log.i(TAG, "connectDaemonFifo($path): pipeStream already attached, skipping")
            return false
        }
        return try {
            Log.i(TAG, "connectDaemonFifo: opening $path (will block until daemon writer attaches)")
            val fis = java.io.FileInputStream(path)
            pipeStream = fis
            Log.i(TAG, "connectDaemonFifo: open OK, fifo=$path")
            if (surfaceHolder != null) startPipeReader()
            true
        } catch (e: Exception) {
            Log.e(TAG, "connectDaemonFifo($path) FAILED: $e")
            false
        }
    }

    /**
     * Convenience launcher for the CR46 / M7 host-daemon integration path:
     * opens the FIFO on a daemon thread (so the blocking `O_RDONLY` open
     * does not stall the UI thread) and starts the pipe reader once the
     * surface is attached. Caller is responsible for ensuring a
     * `surfaceHolder` exists either before or shortly after this call —
     * the existing `PipeReaderStarter` poll loop pattern handles either
     * order.
     */
    fun connectDaemonFifoAsync(path: String = WESTLAKE_DLST_FIFO) {
        Thread({
            connectDaemonFifo(path)
        }, "DaemonFifoConnect").apply { isDaemon = true; start() }
        // Mirror the subprocess start() polling pattern: kick off a watcher
        // thread that retries startPipeReader once both holder + stream are
        // present, so the order of surfaceHolder-vs-FIFO-open doesn't
        // matter.
        Thread({
            for (i in 0..100) {
                if (surfaceHolder != null && pipeStream != null) {
                    startPipeReader()
                    if (pipeReaderThread?.isAlive == true) break
                }
                Thread.sleep(200)
            }
        }, "DaemonFifoPipeReaderStarter").apply { isDaemon = true; start() }
    }

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

    private fun startHttpBridge(dir: File) {
        if (httpBridgeThread?.isAlive == true) return
        httpBridgeRunning = true
        httpBridgeReadOffset = 0L
        httpBridgeThread = Thread({
            val requests = File(dir, "http_requests.log")
            Log.i(TAG, "HTTP bridge started: ${dir.absolutePath}")
            while (httpBridgeRunning) {
                try {
                    if (!requests.exists()) {
                        Thread.sleep(50)
                        continue
                    }
                    RandomAccessFile(requests, "r").use { raf ->
                        if (httpBridgeReadOffset > raf.length()) {
                            httpBridgeReadOffset = 0L
                        }
                        raf.seek(httpBridgeReadOffset)
                        var line = raf.readLine()
                        while (line != null) {
                            handleHttpBridgeLine(dir, line.trim())
                            line = raf.readLine()
                        }
                        httpBridgeReadOffset = raf.filePointer
                    }
                    Thread.sleep(50)
                } catch (ie: InterruptedException) {
                    break
                } catch (e: Exception) {
                    Log.w(TAG, "HTTP bridge loop error: $e")
                    try { Thread.sleep(100) } catch (_: InterruptedException) { break }
                }
            }
            Log.i(TAG, "HTTP bridge stopped")
        }, "WestlakeHttpBridge")
        httpBridgeThread!!.isDaemon = true
        httpBridgeThread!!.start()
    }

    private fun stopHttpBridge() {
        httpBridgeRunning = false
        httpBridgeThread?.interrupt()
        httpBridgeThread = null
        httpBridgeReadOffset = 0L
    }

    private fun handleHttpBridgeLine(dir: File, line: String) {
        if (line.isEmpty()) return
        val first = line.indexOf('|')
        val second = if (first >= 0) line.indexOf('|', first + 1) else -1
        if (second > first && line.substring(first + 1, second) == "V2") {
            handleHttpBridgeV2Line(dir, line)
            return
        }
        val third = if (second >= 0) line.indexOf('|', second + 1) else -1
        if (first <= 0 || second <= first || third <= second) {
            Log.w(TAG, "HTTP bridge bad request line")
            return
        }
        val seq = line.substring(0, first).toIntOrNull()
        val method = line.substring(first + 1, second)
        val maxBytes = line.substring(second + 1, third).toIntOrNull()?.coerceIn(1, 4 * 1024 * 1024)
            ?: (128 * 1024)
        val rawUrl = line.substring(third + 1)
        if (seq == null || method != "GET") {
            seq?.let { writeHttpBridgeResponse(dir, it, 400, ByteArray(0), "bad_request") }
            return
        }
        try {
            val url = normalizeBridgeUrl(rawUrl)
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                writeHttpBridgeResponse(dir, seq, 400, ByteArray(0), "unsupported_scheme")
                return
            }
            val body = fetchHttpBridgeBytes(url, maxBytes)
            writeHttpBridgeResponse(dir, seq, body.first, body.second, null)
            Log.i(TAG, "HTTP bridge GET ${body.first} bytes=${body.second.size} url=$rawUrl")
        } catch (e: Exception) {
            Log.w(TAG, "HTTP bridge request failed: $e")
            writeHttpBridgeResponse(dir, seq, 599, ByteArray(0), sanitizeBridgeMeta(e.javaClass.simpleName + ":" + (e.message ?: "")))
        }
    }

    private data class HttpBridgeRequest(
        val url: String,
        val method: String,
        val headersJson: String,
        val body: ByteArray,
        val maxBytes: Int,
        val timeoutMs: Int,
        val followRedirects: Boolean,
    )

    private data class HttpBridgeBody(
        val bytes: ByteArray,
        val truncated: Boolean,
    )

    private data class HttpBridgeResult(
        val status: Int,
        val headersJson: String,
        val body: ByteArray,
        val error: String?,
        val truncated: Boolean,
        val finalUrl: String,
    )

    private interface HttpBridgeExecutor {
        fun execute(request: HttpBridgeRequest): HttpBridgeResult
    }

    private val httpBridgeExecutor: HttpBridgeExecutor = UrlConnectionHttpBridgeExecutor()

    private class UrlConnectionHttpBridgeExecutor : HttpBridgeExecutor {
        override fun execute(request: HttpBridgeRequest): HttpBridgeResult {
            val conn = (URL(request.url).openConnection() as HttpURLConnection)
            try {
                configureConnection(conn, request)
                val status = conn.responseCode
                val body = WestlakeVM.readHttpBridgeBody(responseStream(conn, status), request.maxBytes)
                return HttpBridgeResult(
                    status = status,
                    headersJson = responseHeadersJson(conn),
                    body = body.bytes,
                    error = null,
                    truncated = body.truncated,
                    finalUrl = conn.url?.toString() ?: request.url,
                )
            } finally {
                conn.disconnect()
            }
        }

        private fun configureConnection(conn: HttpURLConnection, request: HttpBridgeRequest) {
            conn.instanceFollowRedirects = request.followRedirects
            conn.connectTimeout = request.timeoutMs
            conn.readTimeout = request.timeoutMs
            setRequestMethodCompat(conn, request.method)
            applyRequestHeaders(conn, request.headersJson)
            if (conn.getRequestProperty("User-Agent").isNullOrEmpty()) {
                conn.setRequestProperty("User-Agent", "Westlake-HTTP-Bridge/2.0")
            }
            if (request.body.isNotEmpty()) {
                conn.doOutput = true
                conn.outputStream.use { it.write(request.body) }
            }
        }

        private fun setRequestMethodCompat(conn: HttpURLConnection, method: String) {
            try {
                conn.requestMethod = method
                return
            } catch (e: java.net.ProtocolException) {
                if (method != "PATCH") throw e
            }
            var cls: Class<*>? = conn.javaClass
            while (cls != null) {
                try {
                    val field = cls.getDeclaredField("method")
                    field.isAccessible = true
                    field.set(conn, method)
                    return
                } catch (_: NoSuchFieldException) {
                    cls = cls.superclass
                }
            }
            throw java.net.ProtocolException("unsupported_method_$method")
        }

        private fun applyRequestHeaders(conn: HttpURLConnection, headersJson: String) {
            if (headersJson.isBlank()) return
            val headers = JSONObject(headersJson)
            val keys = headers.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key.isNullOrBlank()) continue
                val value = headers.opt(key) ?: continue
                if (value == JSONObject.NULL) continue
                if (value is JSONArray) {
                    for (i in 0 until value.length()) {
                        val item = value.opt(i)
                        if (item != null && item != JSONObject.NULL) {
                            conn.addRequestProperty(key, item.toString())
                        }
                    }
                } else {
                    conn.setRequestProperty(key, value.toString())
                }
            }
        }

        private fun responseStream(conn: HttpURLConnection, status: Int): java.io.InputStream? {
            return try {
                if (status >= 400) conn.errorStream ?: conn.inputStream else conn.inputStream
            } catch (_: Exception) {
                conn.errorStream
            }
        }

        private fun responseHeadersJson(conn: HttpURLConnection): String {
            val out = JSONObject()
            conn.headerFields.forEach { (name, values) ->
                if (!name.isNullOrEmpty() && values != null) {
                    val arr = JSONArray()
                    values.forEach { arr.put(it) }
                    out.put(name, arr)
                }
            }
            return out.toString()
        }
    }

    private fun handleHttpBridgeV2Line(dir: File, line: String) {
        val parts = line.split('|', limit = 9)
        val seq = parts.getOrNull(0)?.toIntOrNull()
        if (seq == null || parts.size != 9 || parts[1] != "V2") {
            seq?.let { writeHttpBridgeResponseV2(dir, it, 400, "{}", ByteArray(0), "bad_request", false, "") }
            Log.w(TAG, "HTTP bridge bad v2 request line")
            return
        }
        try {
            val method = parts[2]
            val maxBytes = parts[3].toIntOrNull()?.coerceIn(1, 4 * 1024 * 1024) ?: (128 * 1024)
            val timeoutMs = parts[4].toIntOrNull()?.coerceIn(250, 60000) ?: 15000
            val followRedirects = parts[5] == "1"
            val headersJson = decodeBridgeBase64Utf8(parts[6]).ifBlank { "{}" }
            val body = decodeBridgeBase64(parts[7])
            val rawUrl = parts[8]
            if (!isSupportedHttpBridgeMethod(method)) {
                writeHttpBridgeResponseV2(dir, seq, 400, "{}", ByteArray(0), "unsupported_method", false, rawUrl)
                return
            }
            val url = normalizeBridgeUrl(rawUrl)
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                writeHttpBridgeResponseV2(dir, seq, 400, "{}", ByteArray(0), "unsupported_scheme", false, rawUrl)
                return
            }
            val result = httpBridgeExecutor.execute(
                HttpBridgeRequest(
                    url = url,
                    method = method,
                    headersJson = headersJson,
                    body = body,
                    maxBytes = maxBytes,
                    timeoutMs = timeoutMs,
                    followRedirects = followRedirects,
                )
            )
            writeHttpBridgeResponseV2(
                dir,
                seq,
                result.status,
                result.headersJson,
                result.body,
                result.error,
                result.truncated,
                result.finalUrl,
            )
            Log.i(TAG, "HTTP bridge $method ${result.status} bytes=${result.body.size} truncated=${result.truncated} url=$rawUrl")
        } catch (e: Exception) {
            Log.w(TAG, "HTTP bridge v2 request failed: $e")
            val timeout = e is java.net.SocketTimeoutException
            writeHttpBridgeResponseV2(
                dir,
                seq,
                if (timeout) -408 else 599,
                "{}",
                ByteArray(0),
                if (timeout) "timeout" else sanitizeBridgeMeta(e.javaClass.simpleName + ":" + (e.message ?: "")),
                false,
                parts.getOrNull(8) ?: "",
            )
        }
    }

    private fun isSupportedHttpBridgeMethod(method: String): Boolean {
        return method == "GET" || method == "POST" || method == "PUT" || method == "PATCH" ||
            method == "DELETE" || method == "HEAD" || method == "OPTIONS"
    }

    private fun normalizeBridgeUrl(rawUrl: String): String {
        val normalized = if (rawUrl.startsWith("http://httpbin.org/")) {
            "https://httpbin.org/" + rawUrl.removePrefix("http://httpbin.org/")
        } else {
            rawUrl
        }
        val proxyBase = bridgeProxyBase()
        return if (!proxyBase.isNullOrEmpty() &&
            (normalized.startsWith("http://") || normalized.startsWith("https://"))) {
            val base = proxyBase.trimEnd('/')
            if (normalized.startsWith("$base/")) {
                normalized
            } else {
                base + "/proxy?url=" + URLEncoder.encode(normalized, "UTF-8")
            }
        } else {
            normalized
        }
    }

    private fun bridgeProxyBase(): String? {
        val dir = WestlakeActivity.instance?.getExternalFilesDir(null) ?: return null
        val file = File(dir, "westlake_http_proxy_base.txt")
        if (!file.exists()) return null
        return try {
            file.readText().trim().takeIf { it.startsWith("http://") || it.startsWith("https://") }
        } catch (_: Exception) {
            null
        }
    }

    private fun fetchHttpBridgeBytes(urlText: String, maxBytes: Int): Pair<Int, ByteArray> {
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val conn = (URL(urlText).openConnection() as HttpURLConnection)
                conn.requestMethod = "GET"
                conn.instanceFollowRedirects = true
                conn.connectTimeout = 8000
                conn.readTimeout = 12000
                conn.setRequestProperty("User-Agent", "Westlake-HTTP-Bridge/1.0")
                return try {
                    val status = conn.responseCode
                    val stream = if (status in 200..399) conn.inputStream else conn.errorStream
                    status to readHttpBridgeBytes(stream, maxBytes)
                } finally {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                lastError = e
                if (attempt < 2) {
                    try {
                        Thread.sleep(750L * (attempt + 1))
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw e
                    }
                }
            }
        }
        throw lastError ?: java.io.IOException("http_bridge_failed")
    }

    private fun readHttpBridgeBytes(input: java.io.InputStream?, maxBytes: Int): ByteArray {
        return readHttpBridgeBody(input, maxBytes).bytes
    }

    private fun readHttpBridgeBody(input: java.io.InputStream?, maxBytes: Int): HttpBridgeBody {
        if (input == null) return HttpBridgeBody(ByteArray(0), false)
        input.use { stream ->
            val out = java.io.ByteArrayOutputStream(minOf(8192, maxBytes))
            val buf = ByteArray(8192)
            var truncated = false
            while (out.size() < maxBytes) {
                val read = stream.read(buf)
                if (read < 0) break
                if (read == 0) continue
                val remaining = maxBytes - out.size()
                out.write(buf, 0, minOf(read, remaining))
                if (read > remaining) {
                    truncated = true
                    break
                }
            }
            if (!truncated && out.size() >= maxBytes) {
                truncated = stream.read() >= 0
            }
            return HttpBridgeBody(out.toByteArray(), truncated)
        }
    }

    private fun writeHttpBridgeResponse(dir: File, seq: Int, status: Int, body: ByteArray, error: String?) {
        val bodyFile = File(dir, "http_${seq}.body")
        val metaFile = File(dir, "http_${seq}.meta")
        try {
            FileOutputStream(bodyFile).use { it.write(body) }
            bodyFile.setReadable(true, false)
            bodyFile.setWritable(true, false)
            val meta = buildString {
                append("status=").append(status).append('\n')
                append("bytes=").append(body.size).append('\n')
                append("transport=host_bridge").append('\n')
                if (!error.isNullOrEmpty()) {
                    append("error=").append(sanitizeBridgeMeta(error)).append('\n')
                }
            }
            metaFile.writeText(meta)
            metaFile.setReadable(true, false)
            metaFile.setWritable(true, false)
        } catch (e: Exception) {
            Log.w(TAG, "HTTP bridge response write failed: $e")
        }
    }

    private fun writeHttpBridgeResponseV2(
        dir: File,
        seq: Int,
        status: Int,
        headersJson: String,
        body: ByteArray,
        error: String?,
        truncated: Boolean,
        finalUrl: String,
    ) {
        val bodyFile = File(dir, "http_${seq}.body")
        val metaFile = File(dir, "http_${seq}.meta")
        try {
            FileOutputStream(bodyFile).use { it.write(body) }
            bodyFile.setReadable(true, false)
            bodyFile.setWritable(true, false)
            val meta = buildString {
                append("status=").append(status).append('\n')
                append("bytes=").append(body.size).append('\n')
                append("truncated=").append(truncated).append('\n')
                append("headersJsonBase64=").append(encodeBridgeBase64(headersJson.toByteArray(Charsets.UTF_8))).append('\n')
                append("finalUrlBase64=").append(encodeBridgeBase64(finalUrl.toByteArray(Charsets.UTF_8))).append('\n')
                append("transport=host_bridge").append('\n')
                append("protocol=2").append('\n')
                if (!error.isNullOrEmpty()) {
                    append("error=").append(sanitizeBridgeMeta(error)).append('\n')
                }
            }
            metaFile.writeText(meta)
            metaFile.setReadable(true, false)
            metaFile.setWritable(true, false)
        } catch (e: Exception) {
            Log.w(TAG, "HTTP bridge v2 response write failed: $e")
        }
    }

    private fun decodeBridgeBase64(value: String): ByteArray {
        return if (value.isEmpty()) ByteArray(0) else Base64.decode(value, Base64.NO_WRAP)
    }

    private fun decodeBridgeBase64Utf8(value: String): String {
        return String(decodeBridgeBase64(value), Charsets.UTF_8)
    }

    private fun encodeBridgeBase64(value: ByteArray): String {
        return if (value.isEmpty()) "" else Base64.encodeToString(value, Base64.NO_WRAP)
    }

    private fun sanitizeBridgeMeta(value: String): String {
        return value.replace('\n', '_').replace('\r', '_').replace('|', '_')
            .take(180)
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
        closePipeReader()
        process?.destroyForcibly()
        stopHttpBridge()

        fun allowGuestRead(file: File, executable: Boolean = false) {
            try {
                file.setReadable(true, false)
                if (file.isDirectory || executable) {
                    file.setExecutable(true, false)
                }
            } catch (_: Exception) {
            }
        }

        fun ensureGuestDir(dir: File) {
            try {
                dir.mkdirs()
                dir.setWritable(true, false)
                allowGuestRead(dir)
            } catch (_: Exception) {
            }
        }

        fun deleteIfPresent(file: File) {
            try {
                if (file.exists()) file.delete()
            } catch (_: Exception) {
            }
        }

        // Copy dalvikvm to app's private dir (SELinux allows execute there)
        val vmDir = File(activity.filesDir, "vm").apply { mkdirs() }
        val srcDir = File(DALVIKVM_DIR).also { ensureGuestDir(it) }
        // The legacy in-process loader extracted aosp-shim.dex into the host
        // cache and generated oat/vdex files. Remove them before each external
        // VM launch so ART cannot resolve stale shim code from app-private cache.
        deleteIfPresent(File(activity.cacheDir, "aosp-shim.dex"))
        deleteIfPresent(File(activity.cacheDir, "app.dex"))
        deleteIfPresent(File(activity.cacheDir, "oat/arm64/aosp-shim.odex"))
        deleteIfPresent(File(activity.cacheDir, "oat/arm64/aosp-shim.vdex"))
        deleteIfPresent(File(activity.cacheDir, "oat/arm64/app.odex"))
        deleteIfPresent(File(activity.cacheDir, "oat/arm64/app.vdex"))
        val httpBridgeDir = if (apkConfig != null) {
            File(vmDir, "http_bridge").also { dir ->
                ensureGuestDir(dir)
                dir.listFiles()?.forEach { f ->
                    if (f.name.startsWith("http_") && (f.name.endsWith(".meta") || f.name.endsWith(".body"))) {
                        try { f.delete() } catch (_: Exception) {}
                    }
                }
                val requests = File(dir, "http_requests.log")
                try {
                    requests.writeText("")
                    requests.setReadable(true, false)
                    requests.setWritable(true, false)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed preparing HTTP bridge request log: $e")
                }
            }
        } else {
            null
        }
        val frameBridgeFile = if (apkConfig?.packageName == "com.mcdonalds.app") {
            File(vmDir, "westlake_frames.dlst").also { file ->
                deleteIfPresent(file)
                try {
                    file.parentFile?.mkdirs()
                    file.createNewFile()
                    file.setReadable(true, false)
                    file.setWritable(true, false)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed preparing display-list frame bridge: $e")
                }
            }
        } else {
            null
        }
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
        // BCP order:
        //   1. core-oj, core-libart, core-icu4j  (matches boot image)
        //   2. bouncycastle  (BouncyCastleProvider for java.security: UUID, SecureRandom)
        //   3. aosp-shim.dex  (native stubs that framework.jar's java code calls into:
        //      SystemProperties, MessageQueue, Surface, AssetManager binders, etc.)
        //   4. framework.jar / ext.jar  (real Android framework; runs
        //      LayoutInflater, View hierarchy, Resources, ActivityThread on top of
        //      the aosp-shim native stubs.)
        // Architecture rule (CLAUDE.md): real framework code runs the show. Shim's
        // job is the JNI/native boundary, not parallel reimplementation.
        //
        // CR23-fix (2026-05-13): services.jar is INTENTIONALLY excluded.  It is a
        // system_server-only jar; including it in an app process triggers a
        // classpath collision between AOSP's stock Guava (services.jar!classes3.dex
        // ImmutableMap) and the launched APK's R8-obfuscated Guava — McD's
        // McDMarketApplication.onCreate() crashes with NoSuchMethodError:
        // `No InvokeType(0) method m()Lcom/google/common/collect/ImmutableMap;`
        // because R8's renamed accessor isn't present on services.jar's
        // ImmutableMap.  See aosp-libbinder-port/M4_PRE_NOTES.md and
        // aosp-libbinder-port/m3-dalvikvm-boot.sh:42-50 (--bcp-framework vs
        // --bcp-framework-strict) and docs/engine/M4_DISCOVERY.md §13.
        // App processes should mirror the `--bcp-framework-strict` BCP shape.
        val fwJar = if (File("$runDir/framework.jar").exists()) ":$runDir/framework.jar" else ""
        val extJar = if (File("$runDir/ext.jar").exists()) ":$runDir/ext.jar" else ""
        val bcp = "$runDir/core-oj.jar:$runDir/core-libart.jar:$runDir/core-icu4j.jar:$runDir/bouncycastle.jar:$runDir/aosp-shim.dex$fwJar$extJar"
        val icuDataDir = File(runDir, "icu").apply {
            mkdirs()
            setReadable(true, false)
            setExecutable(true, false)
            setWritable(true, false)
        }
        val icuDataPath = "${icuDataDir.absolutePath}:/apex/com.android.i18n/etc/icu"

        // Boot image: ALL files packaged as .so in APK → extracted to nativeLibraryDir
        // with apk_data_file SELinux context (allows mmap PROT_EXEC on EMUI)
        // ART discovers boot images at $ANDROID_ROOT/arm64/ — symlink .so files from nativeLibDir
        val fwArm64 = File(vmDir, "arm64").apply { mkdirs() }
        // Also create framework/arm64 for compatibility
        File(vmDir, "framework/arm64").mkdirs()
        val nativeLibDir = activity.applicationInfo.nativeLibraryDir
        val stagedRunLibs = listOf(
            "libc++_shared.so",
            "libframework_stubs.so",
            "libwestlake_art.so",
            "libwestlake_natives.so",
            "liboh_bridge.so",
        )
        for (libName in stagedRunLibs) {
            val src = File(nativeLibDir, libName)
            val dst = File(runDir, libName)
            if (!src.exists()) {
                Log.w(TAG, "Missing native lib for staging: $libName")
                continue
            }
            if (!dst.exists()
                || dst.length() != src.length()
                || dst.lastModified() != src.lastModified()
            ) {
                try {
                    src.copyTo(dst, overwrite = true)
                    dst.setLastModified(src.lastModified())
                    dst.setReadable(true, false)
                    dst.setExecutable(true, false)
                    log.add("Staged $libName")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed staging $libName: $e")
                }
            }
        }
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
        val appNativeLibDir = File(runDir, "app_lib").also { ensureGuestDir(it) }
        val guestLibraryPath = listOf(
            appNativeLibDir.absolutePath,
            runDir,
            nativeLibDir,
        ).joinToString(":")
        val nativeLibraryVmArgs = arrayOf(
            "-Djava.library.path=$guestLibraryPath",
            "-Dapp.native.lib.dir=${appNativeLibDir.absolutePath}",
            "-Dwestlake.native.lib.dir=${appNativeLibDir.absolutePath}",
        )

        fun extractApkNativeLibraries(apkPath: String?): Int {
            if (apkPath.isNullOrEmpty() || !apkPath.endsWith(".apk")) return 0
            val apkFile = File(apkPath)
            if (!apkFile.exists()) return 0
            var extracted = 0
            try {
                ZipFile(apkFile).use { zip ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.isDirectory || !entry.name.startsWith("lib/arm64-v8a/")
                            || !entry.name.endsWith(".so")) {
                            continue
                        }
                        val out = File(appNativeLibDir, entry.name.substringAfterLast('/'))
                        if (out.exists() && out.length() == entry.size) {
                            allowGuestRead(out)
                            out.setExecutable(true, false)
                            continue
                        }
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(out).use { output -> input.copyTo(output) }
                        }
                        allowGuestRead(out)
                        out.setExecutable(true, false)
                        extracted++
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed extracting APK native libraries from $apkPath: $e")
            }
            return extracted
        }

        fun extractSiblingSplitNativeLibraries(apkPath: String?): Int {
            if (apkPath.isNullOrEmpty() || !apkPath.endsWith(".apk")) return 0
            val apkFile = File(apkPath)
            val dir = apkFile.parentFile ?: return 0
            val splits = dir.listFiles { file ->
                file.isFile && file.name.startsWith("split_") && file.name.endsWith(".apk")
            } ?: return 0
            var extracted = 0
            for (split in splits.sortedBy { it.name }) {
                extracted += extractApkNativeLibraries(split.absolutePath)
            }
            return extracted
        }

        fun copySiblingSplits(apkPath: String?, dstDir: File): Int {
            if (apkPath.isNullOrEmpty() || !apkPath.endsWith(".apk")) return 0
            val apkFile = File(apkPath)
            val dir = apkFile.parentFile ?: return 0
            val splits = dir.listFiles { file ->
                file.isFile && file.name.startsWith("split_") && file.name.endsWith(".apk")
            } ?: return 0
            var copied = 0
            for (split in splits.sortedBy { it.name }) {
                val dst = File(dstDir, split.name)
                try {
                    if (!dst.exists() || dst.length() != split.length()) {
                        split.copyTo(dst, overwrite = true)
                        copied++
                    }
                    allowGuestRead(dst)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed copying split ${split.name}: $e")
                }
            }
            return copied
        }

        fun stagedSiblingSplitPaths(apkPath: String?, dstDir: File): List<String> {
            if (apkPath.isNullOrEmpty() || !apkPath.endsWith(".apk")) return emptyList()
            val apkFile = File(apkPath)
            val dir = apkFile.parentFile ?: return emptyList()
            val splits = dir.listFiles { file ->
                file.isFile && file.name.startsWith("split_") && file.name.endsWith(".apk")
            } ?: return emptyList()
            return splits.sortedBy { it.name }
                .map { File(dstDir, it.name) }
                .filter { it.exists() }
                .map { it.absolutePath }
        }

        fun splitSourceVmArgs(splitPaths: List<String>): Array<String> {
            if (splitPaths.isEmpty()) return emptyArray()
            return arrayOf("-Dwestlake.apk.splitSourceDirs=${splitPaths.joinToString(File.pathSeparator)}")
        }
        // The current packaged boot image set is not valid on the live phone path.
        // Stay on the accepted imageless Westlake path until the image artifacts are rebuilt coherently.
        val hasBootImage = false && nativeBootArt.exists() && nativeBootOat.exists()
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
            val backendMode = apkConfig.effectiveBackendMode()
            val frameworkPolicy = backendMode.frameworkPolicy
            val backendModeArg = "-Dwestlake.backend.mode=${backendMode.propertyValue}"
            val bridgeVmArgs = httpBridgeDir?.let {
                arrayOf("-Dwestlake.bridge.dir=${it.absolutePath}")
            } ?: emptyArray()
            val extraVmArgs = apkConfig.extraVmProperties.toSortedMap()
                .map { (key, value) -> "-D$key=$value" }
                .toTypedArray()
            val extraLauncherArgs = apkConfig.extraVmProperties.toSortedMap()
                .flatMap { (key, value) ->
                    when (key) {
                        "westlake.canary.stage" -> listOf("--canary-stage", value)
                        else -> emptyList()
                    }
                }
                .toTypedArray()
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
                    "-Dandroid.icu.impl.ICUBinary.dataPath=$icuDataPath",
                    "-Dwestlake.framework.policy=$frameworkPolicy",
                    backendModeArg,
                    *bridgeVmArgs,
                    *extraVmArgs,
                    *nativeLibraryVmArgs,
                    "-Dwestlake.apk.path=$dexOnlyPath",
                    "-Dwestlake.apk.activity=${apkConfig.activityName}",
                    "-Dwestlake.apk.package=${apkConfig.packageName}",
                    "-classpath", dexOnlyPath,
                    "com.westlake.engine.WestlakeLauncher",
                    "--apk-path", dexOnlyPath,
                    "--apk-activity", apkConfig.activityName,
                    "--apk-package", apkConfig.packageName,
                    *extraLauncherArgs
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
                val apkMetaPath = if (!apkSrc.isNullOrEmpty() && File(apkSrc).exists()) {
                    val apkName = apkConfig.packageName.replace(".", "_") + ".apk"
                    val apkDevicePath = "${vmDir.absolutePath}/$apkName"
                    val apkDst = File(apkDevicePath)
                    if (!apkDst.exists() || apkDst.length() != File(apkSrc).length()) {
                        File(apkSrc).copyTo(apkDst, overwrite = true)
                        log.add("Copied APK metadata for ${apkConfig.displayName}")
                    }
                    val copiedSplits = copySiblingSplits(apkSrc, vmDir)
                    if (copiedSplits > 0) log.add("Copied $copiedSplits APK split(s)")
                    apkDevicePath
                } else {
                    log.add("WARNING: missing APK metadata source for ${apkConfig.displayName}")
                    "$runDir/mcd_classes.dex"
                }
                val stagedSplitPaths = stagedSiblingSplitPaths(apkSrc, vmDir)
                if (stagedSplitPaths.isNotEmpty()) {
                    log.add("APK split metadata: ${stagedSplitPaths.size} split(s)")
                }
                if (apkMetaPath.endsWith(".apk")) {
                    // Keep patched DEX first for class resolution, but expose APK resources
                    // such as META-INF/services to the app PathClassLoader.
                    mcdCp.append(":").append(apkMetaPath)
                }
                val extractedNativeLibs =
                    extractApkNativeLibraries(apkMetaPath) + extractSiblingSplitNativeLibraries(apkMetaPath)
                log.add("App native libs: ${appNativeLibDir.absolutePath} ($extractedNativeLibs extracted incl splits)")

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
                    "-Dandroid.icu.impl.ICUBinary.dataPath=$icuDataPath",
                    *proxyArgs,
                    "-Dwestlake.framework.policy=$frameworkPolicy",
                    backendModeArg,
                    *bridgeVmArgs,
                    *extraVmArgs,
                    *nativeLibraryVmArgs,
                    *splitSourceVmArgs(stagedSplitPaths),
                    "-Dwestlake.apk.package=${apkConfig.packageName}",
                    "-Dwestlake.apk.activity=${apkConfig.activityName}",
                    "-Dwestlake.apk.path=$apkMetaPath",
                    "-Dwestlake.apk.resdir=$guestResDir",
                    "-Dwestlake.apk.manifest=$guestResDir/AndroidManifest.xml",
                    "-classpath", mcdCp.toString(),
                    "com.westlake.engine.WestlakeLauncher",
                    "--apk-path", apkMetaPath,
                    "--apk-activity", apkConfig.activityName,
                    "--apk-package", apkConfig.packageName,
                    "--apk-resdir", guestResDir,
                    "--apk-manifest", "$guestResDir/AndroidManifest.xml",
                    *extraLauncherArgs
                )
                envApkPath = apkMetaPath
                envActivityName = apkConfig.activityName
                envPackageName = apkConfig.packageName
                envResDir = guestResDir
                envManifestPath = "$guestResDir/AndroidManifest.xml"
                log.add("Multi-DEX: ${apkConfig.displayName} (Westlake dalvikvm)")
            } else {

            // The standalone target runtime cannot reliably native-open classpath
            // entries from app-private storage, so stage target APK artifacts in
            // the shared runtime directory. Control mode keeps the old private
            // staging path.
            val guestStageDir = if (backendMode == BackendMode.TARGET_OHOS_BACKEND) {
                srcDir
            } else {
                vmDir
            }
            ensureGuestDir(guestStageDir)

            // Copy APK to the guest staging dir.
            val apkName = apkConfig.packageName.replace(".", "_") + ".apk"
            val apkDevicePath = "${guestStageDir.absolutePath}/$apkName"
            val apkDst = File(apkDevicePath)
            if (!apkDst.exists()
                || apkDst.length() != File(apkSrc).length()
                || backendMode == BackendMode.TARGET_OHOS_BACKEND
            ) {
                if (backendMode == BackendMode.TARGET_OHOS_BACKEND && apkDst.exists()) {
                    apkDst.delete()
                }
                File(apkSrc).copyTo(apkDst, overwrite = true)
                allowGuestRead(apkDst)
                log.add("Copied APK: ${apkConfig.displayName}")
            } else {
                allowGuestRead(apkDst)
            }
            val copiedSplits = copySiblingSplits(apkSrc, guestStageDir)
            if (copiedSplits > 0) log.add("Copied $copiedSplits APK split(s)")
            val stagedSplitPaths = stagedSiblingSplitPaths(apkSrc, guestStageDir)
            if (stagedSplitPaths.isNotEmpty()) {
                log.add("APK split metadata: ${stagedSplitPaths.size} split(s)")
            }
            val extractedNativeLibs =
                extractApkNativeLibraries(apkDevicePath) + extractSiblingSplitNativeLibraries(apkSrc)
            log.add("App native libs: ${appNativeLibDir.absolutePath} ($extractedNativeLibs extracted incl splits)")

            // Extract ALL classesN.dex from the APK (multi-dex support)
            val dexPaths = mutableListOf<String>()
            try {
                val zipFile = java.util.zip.ZipFile(apkSrc)
                for (entry in zipFile.entries()) {
                    if (entry.name.startsWith("classes") && entry.name.endsWith(".dex")) {
                        val dexName = apkConfig.packageName.replace(".", "_") + "_" + entry.name
                        val dexPath = "${guestStageDir.absolutePath}/$dexName"
                        val dexFile = File(dexPath)
                        if (!dexFile.exists()
                            || dexFile.length() != entry.size
                            || backendMode == BackendMode.TARGET_OHOS_BACKEND
                        ) {
                            if (backendMode == BackendMode.TARGET_OHOS_BACKEND && dexFile.exists()) {
                                dexFile.delete()
                            }
                            zipFile.getInputStream(entry).use { inp ->
                                FileOutputStream(dexPath).use { out ->
                                    val buf = ByteArray(8192)
                                    var n: Int
                                    while (inp.read(buf).also { n = it } > 0) out.write(buf, 0, n)
                                }
                            }
                        }
                        allowGuestRead(dexFile)
                        dexPaths.add(dexPath)
                    }
                }
                zipFile.close()
                log.add("Extracted ${dexPaths.size} DEX files")
            } catch (e: Exception) {
                log.add("DEX extraction error: ${e.message}")
            }
            val dexClasspath = dexPaths.joinToString(":")
            val dexClasspathWithApk = if (apkDevicePath.endsWith(".apk")) {
                "$dexClasspath:$apkDevicePath"
            } else {
                dexClasspath
            }

            // Extract resources.arsc for the shim's resource parser (no ZipFile JNI in dalvikvm)
            val resDirName = if (backendMode == BackendMode.TARGET_OHOS_BACKEND) {
                apkConfig.packageName.replace(".", "_") + "_res"
            } else {
                "apk_res"
            }
            val resDir = File(guestStageDir, resDirName).apply { ensureGuestDir(this) }
            try {
                val zipFile = java.util.zip.ZipFile(apkSrc)
                for (entry in zipFile.entries()) {
                    if (entry.name == "resources.arsc" || entry.name.startsWith("res/") || entry.name == "AndroidManifest.xml") {
                        val outFile = File(resDir, entry.name)
                        outFile.parentFile?.let { ensureGuestDir(it) }
                        if (entry.isDirectory) {
                            ensureGuestDir(outFile)
                        } else {
                            if (backendMode == BackendMode.TARGET_OHOS_BACKEND && outFile.exists()) {
                                outFile.delete()
                            }
                            zipFile.getInputStream(entry).use { inp ->
                                FileOutputStream(outFile).use { out ->
                                    val buf = ByteArray(8192)
                                    var n: Int
                                    while (inp.read(buf).also { n = it } > 0) out.write(buf, 0, n)
                                }
                            }
                            allowGuestRead(outFile)
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
                "-Dandroid.icu.impl.ICUBinary.dataPath=$icuDataPath",
                "-Dwestlake.framework.policy=$frameworkPolicy",
                backendModeArg,
                *bridgeVmArgs,
                *extraVmArgs,
                *nativeLibraryVmArgs,
                *splitSourceVmArgs(stagedSplitPaths),
                "-Dwestlake.apk.path=$apkDevicePath",
                "-Dwestlake.apk.activity=${apkConfig.activityName}",
                "-Dwestlake.apk.package=${apkConfig.packageName}",
                "-Dwestlake.apk.resdir=${resDir.absolutePath}",
                "-Dwestlake.apk.manifest=$manifestPath",
                "-classpath", dexClasspathWithApk,
                "com.westlake.engine.WestlakeLauncher",
                "--apk-path", apkDevicePath,
                "--apk-activity", apkConfig.activityName,
                "--apk-package", apkConfig.packageName,
                "--apk-resdir", resDir.absolutePath,
                "--apk-manifest", manifestPath,
                *extraLauncherArgs
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
                *nativeLibraryVmArgs,
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
                append("westlake.backend.mode=")
                    .append(apkConfig.effectiveBackendMode().propertyValue)
                    .append('\n')
                envApkPath?.let { append("westlake.apk.path=").append(it).append('\n') }
                envActivityName?.let { append("westlake.apk.activity=").append(it).append('\n') }
                envPackageName?.let { append("westlake.apk.package=").append(it).append('\n') }
                envResDir?.let { append("westlake.apk.resdir=").append(it).append('\n') }
                envManifestPath?.let { append("westlake.apk.manifest=").append(it).append('\n') }
                httpBridgeDir?.let { append("westlake.bridge.dir=").append(it.absolutePath).append('\n') }
                for ((key, value) in apkConfig.extraVmProperties.toSortedMap()) {
                    append(key).append('=').append(value).append('\n')
                }
            }
            if (launchMeta.isNotEmpty()) {
                val launchFile = File(runDir, "westlake-launch.properties")
                launchFile.writeText(launchMeta)
                allowGuestRead(launchFile)
                log.add("Wrote westlake-launch.properties")
            }
        }

        log.add("Starting from ${vmDir.absolutePath}...")
        try {
            val launchBinary = cmd.firstOrNull()
            if ((launchBinary == "dalvikvm64" || launchBinary == "app_process64")
                && apkConfig?.effectiveBackendMode() == BackendMode.TARGET_OHOS_BACKEND
            ) {
                log.add("ERROR: target/OHOS mode forbids phone ART launcher: $launchBinary")
                return log
            }
            val pb = ProcessBuilder(*cmd)
            pb.directory(File(runDir))
            // For dalvikvm64 (system binary): use real system paths
            val useSysDvm = launchBinary == "dalvikvm64" || launchBinary == "app_process64"
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
            httpBridgeDir?.let { pb.environment()["WESTLAKE_BRIDGE_DIR"] = it.absolutePath }
            frameBridgeFile?.let { pb.environment()["WESTLAKE_FRAME_PATH"] = it.absolutePath }
            pb.environment()["WESTLAKE_NATIVE_LIB_DIR"] = appNativeLibDir.absolutePath
            pb.environment()["WESTLAKE_LIBRARY_PATH"] = guestLibraryPath
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
                pb.environment()["LD_LIBRARY_PATH"] = "$guestLibraryPath:$runDir"
            }
            // For app_process64: CLASSPATH tells it which DEX to load
            val mcdApkEnv = try {
                activity.packageManager.getApplicationInfo("com.mcdonalds.app", 0).sourceDir
            } catch (e: Exception) { "" }
            if (cmd.firstOrNull() == "app_process64" && mcdApkEnv.isNotEmpty()) {
                pb.environment()["CLASSPATH"] = "$runDir/aosp-shim.dex:$mcdApkEnv"
            }
            // Do NOT redirectErrorStream — stdout is binary pipe, stderr is text logs
            val startedProcess = pb.start()
            process = startedProcess
            if (frameBridgeFile != null) {
                drainVmStdout(startedProcess.inputStream)
                pipeStream = TailFileInputStream(frameBridgeFile) { startedProcess.isAlive }
                log.add("VM process started (frame file bridge)")
                Log.i(TAG, "Display-list frame bridge: ${frameBridgeFile.absolutePath}")
            } else {
                pipeStream = startedProcess.inputStream  // stdout = binary display list frames
                log.add("VM process started (pipe mode)")
            }
            httpBridgeDir?.let {
                startHttpBridge(it)
                log.add("HTTP bridge on ${it.absolutePath}")
            }
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
                    val reader = startedProcess.errorStream.bufferedReader()
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
                } finally {
                    stopHttpBridge()
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
        closePipeReader()
        process?.destroyForcibly()
        process = null
        surfaceHolder = null
        onFrameCallback = null
    }
}

/** Mutex to ensure only ONE pipe reader runs at a time */
private val pipeReaderMutex = Mutex()

/** Replay a display list byte array onto a Canvas. */
private var framesDumped = 0
private fun cachedDisplayListBitmap(bytes: ByteArray, length: Int): Bitmap? {
    val key = 31 * length + java.util.Arrays.hashCode(bytes)
    synchronized(imageBitmapCacheLock) {
        imageBitmapCache[key]?.let { cached ->
            if (!cached.isRecycled) return cached
            imageBitmapCache.remove(key)
        }
    }
    val decoded = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, length) ?: return null
    synchronized(imageBitmapCacheLock) {
        imageBitmapCache[key]?.let { existing ->
            decoded.recycle()
            return existing
        }
        imageBitmapCache[key] = decoded
    }
    return decoded
}

private fun replayDisplayList(canvas: Canvas, paint: Paint, data: ByteArray, size: Int) {
    val bb = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN)
    val baseCount = canvas.saveCount
    canvas.save()
    canvas.drawColor(android.graphics.Color.BLACK)

    // Dump first 3 frames for debugging
    if (framesDumped < 3) {
        val ops = StringBuilder("Frame $framesDumped ($size bytes): ")
        // Walk opcodes properly so we get a real op stream, counting paths
        val dbg = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN)
        var pathCount = 0
        while (dbg.hasRemaining()) {
            val op = dbg.get().toInt() and 0xFF
            when (op) {
                OP_DRAW_COLOR -> { dbg.position(dbg.position() + 4); ops.append("COL ") }
                OP_DRAW_RECT  -> { dbg.position(dbg.position() + 20); ops.append("RCT ") }
                OP_DRAW_TEXT  -> {
                    dbg.position(dbg.position() + 16)
                    val len = dbg.getShort().toInt() and 0xFFFF
                    dbg.position(dbg.position() + len)
                    ops.append("TXT($len) ")
                }
                OP_DRAW_LINE  -> { dbg.position(dbg.position() + 20); ops.append("LNE ") }
                OP_SAVE       -> ops.append("SV ")
                OP_RESTORE    -> ops.append("RT ")
                OP_TRANSLATE  -> { dbg.position(dbg.position() + 8); ops.append("TR ") }
                OP_CLIP_RECT  -> { dbg.position(dbg.position() + 16); ops.append("CL ") }
                OP_DRAW_ROUND_RECT -> { dbg.position(dbg.position() + 28); ops.append("RRC ") }
                OP_DRAW_CIRCLE -> { dbg.position(dbg.position() + 16); ops.append("CIR ") }
                OP_DRAW_IMAGE, OP_ARGB_BITMAP -> {
                    dbg.position(dbg.position() + 16)
                    val dl = dbg.getInt()
                    dbg.position(dbg.position() + dl)
                    ops.append("IMG ")
                }
                OP_DRAW_PATH -> {
                    dbg.position(dbg.position() + 4)
                    val pl = dbg.getShort().toInt() and 0xFFFF
                    dbg.position(dbg.position() + pl)
                    pathCount++; ops.append("PTH($pl) ")
                }
                else -> { ops.append("?$op "); break }
            }
            if (ops.length > 700) { ops.append("..."); break }
        }
        // Brute-force scan: count any 13 byte in payload (false-positive friendly but tells us if any OP_PATH was emitted at all)
        var rawPath13 = 0
        for (b in data.copyOfRange(0, size)) { if ((b.toInt() and 0xFF) == 13) rawPath13++ }
        ops.append(" [paths=$pathCount raw13=$rawPath13]")
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
                        val bmp = cachedDisplayListBitmap(imgBytes, dataLen)
                        if (bmp != null) {
                            val dst = android.graphics.RectF(x, y, x + (if (w > 0) w.toFloat() else bmp.width.toFloat()),
                                y + (if (h > 0) h.toFloat() else bmp.height.toFloat()))
                            canvas.drawBitmap(bmp, null, dst, paint)
                        }
                    }
                }
                OP_DRAW_PATH -> {
                    val color = bb.getInt()
                    val payloadLen = bb.getShort().toInt() and 0xFFFF
                    if (payloadLen > 0 && bb.remaining() >= payloadLen) {
                        val end = bb.position() + payloadLen
                        val androidPath = android.graphics.Path()
                        var haveMove = false
                        try {
                            while (bb.position() < end) {
                                when (bb.get().toInt() and 0xFF) {
                                    PCMD_MOVE -> {
                                        val x = bb.getFloat(); val y = bb.getFloat()
                                        androidPath.moveTo(x, y); haveMove = true
                                    }
                                    PCMD_LINE -> {
                                        val x = bb.getFloat(); val y = bb.getFloat()
                                        if (!haveMove) { androidPath.moveTo(0f, 0f); haveMove = true }
                                        androidPath.lineTo(x, y)
                                    }
                                    PCMD_QUAD -> {
                                        val x1 = bb.getFloat(); val y1 = bb.getFloat()
                                        val x2 = bb.getFloat(); val y2 = bb.getFloat()
                                        if (!haveMove) { androidPath.moveTo(0f, 0f); haveMove = true }
                                        androidPath.quadTo(x1, y1, x2, y2)
                                    }
                                    PCMD_CUBIC -> {
                                        val x1 = bb.getFloat(); val y1 = bb.getFloat()
                                        val x2 = bb.getFloat(); val y2 = bb.getFloat()
                                        val x3 = bb.getFloat(); val y3 = bb.getFloat()
                                        if (!haveMove) { androidPath.moveTo(0f, 0f); haveMove = true }
                                        androidPath.cubicTo(x1, y1, x2, y2, x3, y3)
                                    }
                                    PCMD_CLOSE -> androidPath.close()
                                    else -> {
                                        // Unknown sub-command: jump to end of payload to stay aligned
                                        bb.position(end)
                                    }
                                }
                            }
                        } catch (_: java.nio.BufferUnderflowException) {
                            bb.position(end)
                        }
                        if (bb.position() != end) bb.position(end)
                        paint.color = color
                        paint.style = Paint.Style.FILL
                        canvas.drawPath(androidPath, paint)
                    } else if (payloadLen > 0) {
                        // Truncated — skip remaining bytes
                        bb.position(bb.position() + minOf(payloadLen, bb.remaining()))
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

private class DisplayListFrameView(context: Context) : View(context) {
    private val frameLock = Any()
    private var frameData = ByteArray(0)
    private var frameSize = 0
    var guestFrameWidth: Float = GUEST_FRAME_WIDTH
    var guestFrameHeight: Float = GUEST_FRAME_HEIGHT
    private var lastTouchAction = -1
    private var lastTouchX = -1
    private var lastTouchY = -1
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isSubpixelText = true
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
    }

    fun submitFrame(data: ByteArray, size: Int) {
        val copy = data.copyOf(size)
        synchronized(frameLock) {
            frameData = copy
            frameSize = size
        }
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val data: ByteArray
        val size: Int
        synchronized(frameLock) {
            data = frameData
            size = frameSize
        }
        canvas.drawColor(android.graphics.Color.BLACK)
        if (size <= 0) return
        val frameWidth = guestFrameWidth.coerceAtLeast(1f)
        val frameHeight = guestFrameHeight.coerceAtLeast(1f)
        val scale = minOf(
            width / frameWidth,
            height / frameHeight,
        ).coerceAtLeast(0.001f)
        val dx = (width - frameWidth * scale) / 2f
        val dy = (height - frameHeight * scale) / 2f
        canvas.save()
        canvas.translate(dx, dy)
        canvas.scale(scale, scale)
        replayDisplayList(canvas, framePaint, data, size)
        canvas.restore()
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        val frameWidth = guestFrameWidth.coerceAtLeast(1f)
        val frameHeight = guestFrameHeight.coerceAtLeast(1f)
        val scale = minOf(
            width / frameWidth,
            height / frameHeight,
        ).coerceAtLeast(0.001f)
        val dx = (width - frameWidth * scale) / 2f
        val dy = (height - frameHeight * scale) / 2f
        val guestX = ((event.x - dx) / scale).coerceIn(0f, frameWidth)
        val guestY = ((event.y - dy) / scale).coerceIn(0f, frameHeight)
        val action = when (event.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> 0
            android.view.MotionEvent.ACTION_UP,
            android.view.MotionEvent.ACTION_CANCEL -> 1
            android.view.MotionEvent.ACTION_MOVE -> 2
            else -> return true
        }
        val ix = guestX.toInt()
        val iy = guestY.toInt()
        if (action != lastTouchAction || ix != lastTouchX || iy != lastTouchY) {
            WestlakeVM.sendTouch(action, guestX, guestY)
            lastTouchAction = action
            lastTouchX = ix
            lastTouchY = iy
        }
        return true
    }
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
        isSubpixelText = true
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
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
                val nb = ByteArray(4)
                input.readFully(nb)
                var nm = ByteBuffer.wrap(nb).order(ByteOrder.LITTLE_ENDIAN).getInt()
                var rs = 0
                while (nm != DLIST_MAGIC && isRunning()) {
                    nb[0] = nb[1]; nb[1] = nb[2]; nb[2] = nb[3]
                    nb[3] = input.readByte()
                    nm = ByteBuffer.wrap(nb).order(ByteOrder.LITTLE_ENDIAN).getInt()
                    rs++
                }
                Log.w("WestlakeVM", "Bad frame size $size, re-synced past $rs bytes")
                continue
            }
            // Read [size bytes data]
            val data = ByteArray(size)
            input.readFully(data)

            val target = WestlakeVM.surfaceHolder ?: holder
            when (target) {
                is DisplayListFrameView -> {
                    target.submitFrame(data, size)
                    Log.i("WestlakeVM", "Frame: ${size} bytes → View")
                }
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
                        val frameWidth = WestlakeVM.guestFrameWidth.coerceAtLeast(1f)
                        val frameHeight = WestlakeVM.guestFrameHeight.coerceAtLeast(1f)
                        val scale = minOf(
                            canvas.width / frameWidth,
                            canvas.height / frameHeight,
                        )
                        val dx = (canvas.width - frameWidth * scale) / 2f
                        val dy = (canvas.height - frameHeight * scale) / 2f
                        canvas.drawColor(android.graphics.Color.BLACK)
                        canvas.save(); canvas.translate(dx, dy); canvas.scale(scale, scale)
                        replayDisplayList(canvas, paint, data, size)
                        canvas.restore()
                    } finally { target.unlockCanvasAndPost(canvas) }
                    Log.i("WestlakeVM", "Frame: ${size} bytes → Surface")
                }
                is android.view.Surface -> {
                    val canvas = try { target.lockHardwareCanvas() } catch (e: Exception) { null }
                    if (canvas == null) { Thread.sleep(100); continue }
                    try {
                        val frameWidth = WestlakeVM.guestFrameWidth.coerceAtLeast(1f)
                        val frameHeight = WestlakeVM.guestFrameHeight.coerceAtLeast(1f)
                        val scale = minOf(
                            canvas.width / frameWidth,
                            canvas.height / frameHeight,
                        )
                        val dx = (canvas.width - frameWidth * scale) / 2f
                        val dy = (canvas.height - frameHeight * scale) / 2f
                        canvas.drawColor(android.graphics.Color.BLACK)
                        canvas.save(); canvas.translate(dx, dy); canvas.scale(scale, scale)
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
                            val frameScale = minOf(
                                size.width / GUEST_FRAME_WIDTH,
                                size.height / GUEST_FRAME_HEIGHT,
                            )
                                .coerceAtLeast(0.001f)
                            val offsetX = (size.width - GUEST_FRAME_WIDTH * frameScale) / 2f
                            val offsetY = (size.height - GUEST_FRAME_HEIGHT * frameScale) / 2f
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downX = ((down.position.x - offsetX) / frameScale)
                                .coerceIn(0f, GUEST_FRAME_WIDTH)
                            val downY = ((down.position.y - offsetY) / frameScale)
                                .coerceIn(0f, GUEST_FRAME_HEIGHT)
                            WestlakeVM.sendTouch(0, downX, downY)
                            Log.i("WestlakeVM", "DOWN: (${downX.toInt()}, ${downY.toInt()})")
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                val vx = ((change.position.x - offsetX) / frameScale)
                                    .coerceIn(0f, GUEST_FRAME_WIDTH)
                                val vy = ((change.position.y - offsetY) / frameScale)
                                    .coerceIn(0f, GUEST_FRAME_HEIGHT)
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
                                    WestlakeVM.guestFrameWidth = GUEST_FRAME_WIDTH
                                    WestlakeVM.guestFrameHeight = GUEST_FRAME_HEIGHT
                                    holder.setFixedSize(
                                        LEGACY_SURFACE_BUFFER_WIDTH,
                                        LEGACY_SURFACE_BUFFER_HEIGHT,
                                    )
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
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(GUEST_FRAME_WIDTH / GUEST_FRAME_HEIGHT)
                        .align(Alignment.Center)
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
	    val fullPhoneApp = config.packageName == "com.westlake.yelplive" ||
	        config.packageName == "com.westlake.mcdprofile" ||
	        config.packageName == "com.mcdonalds.app"
	    val immersiveApp = fullPhoneApp ||
	        config.packageName == "com.westlake.materialyelp"
	    val guestFrameWidth = if (fullPhoneApp) {
	        YELP_GUEST_FRAME_WIDTH
	    } else {
	        GUEST_FRAME_WIDTH
	    }
	    val guestFrameHeight = if (fullPhoneApp) {
	        YELP_GUEST_FRAME_HEIGHT
	    } else {
	        GUEST_FRAME_HEIGHT
	    }
	    val surfaceBufferWidth = if (immersiveApp) {
	        YELP_SURFACE_BUFFER_WIDTH
	    } else {
	        LEGACY_SURFACE_BUFFER_WIDTH
	    }
	    val surfaceBufferHeight = if (fullPhoneApp) {
	        YELP_SURFACE_BUFFER_HEIGHT
	    } else if (immersiveApp) {
	        MATERIAL_YELP_SURFACE_BUFFER_HEIGHT
	    } else {
	        LEGACY_SURFACE_BUFFER_HEIGHT
	    }
	    val surfaceModifier = if (fullPhoneApp) {
	        Modifier.fillMaxSize()
	    } else {
	        Modifier.fillMaxWidth()
	            .aspectRatio(guestFrameWidth / guestFrameHeight)
	    }
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
                config
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
            if (!immersiveApp) {
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
            }

            // Rendered frame display
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
	                            val frameScale = minOf(
	                                size.width / guestFrameWidth,
	                                size.height / guestFrameHeight,
	                            )
	                                .coerceAtLeast(0.001f)
	                            val offsetX = (size.width - guestFrameWidth * frameScale) / 2f
	                            val offsetY = (size.height - guestFrameHeight * frameScale) / 2f
	                            val down = awaitFirstDown(requireUnconsumed = false)
	                            val downTime = System.currentTimeMillis()
	                            val downX = ((down.position.x - offsetX) / frameScale)
	                                .coerceIn(0f, guestFrameWidth)
	                            val downY = ((down.position.y - offsetY) / frameScale)
	                                .coerceIn(0f, guestFrameHeight)
                            WestlakeVM.sendTouch(0, downX, downY)
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
	                                val vx = ((change.position.x - offsetX) / frameScale)
	                                    .coerceIn(0f, guestFrameWidth)
	                                val vy = ((change.position.y - offsetY) / frameScale)
	                                    .coerceIn(0f, guestFrameHeight)
                                if (change.pressed) {
                                    WestlakeVM.sendTouch(2, vx, vy)
                                } else {
                                    WestlakeVM.sendTouch(1, vx, vy)
                                    // Long press (>500ms) = show text input. Keep immersive
                                    // app proofs fully touch-routed through Westlake.
                                    if (!immersiveApp && System.currentTimeMillis() - downTime > 500) {
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
                        DisplayListFrameView(context).also { view ->
                            view.guestFrameWidth = guestFrameWidth
                            view.guestFrameHeight = guestFrameHeight
                            Log.i(
                                "WestlakeVM",
                                "DisplayListFrameView ${surfaceBufferWidth}x${surfaceBufferHeight} " +
                                    "for ${config.packageName}",
                            )
                            WestlakeVM.guestFrameWidth = guestFrameWidth
                            WestlakeVM.guestFrameHeight = guestFrameHeight
                            WestlakeVM.surfaceHolder = view
                            WestlakeVM.onFrameCallback = { frameCount++ }
                            WestlakeVM.startPipeReader()
                        }
                    },
                    update = { view ->
                        view.guestFrameWidth = guestFrameWidth
                        view.guestFrameHeight = guestFrameHeight
                    },
                    modifier = surfaceModifier.align(Alignment.Center)
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
            if (!immersiveApp) {
                Text(
                    "dalvikvm ARM64 | ${config.packageName} | Pipe \u2192 SurfaceView",
                    fontSize = 10.sp, color = Color(0xFF9C27B0),
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
                )
            }
        }
    }
}
