package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import dalvik.system.DexClassLoader;

/**
 * Hosts the Westlake engine on the phone's own ART.
 * Loads aosp-shim.dex + app.dex in a DexClassLoader,
 * then runs MockDonaldsApp.main() which renders via OHBridge JNI
 * that draws directly to this Activity's SurfaceView.
 */
public class WestlakeHostActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "WestlakeHost";
    private SurfaceView surfaceView;
    private Thread engineThread;
    private volatile boolean running;

    /* Shared state — OHBridge native methods write here, SurfaceView reads */
    private static Canvas currentCanvas;
    private static SurfaceHolder currentHolder;
    private static final Object canvasLock = new Object();
    private static int surfaceWidth, surfaceHeight;
    private static Bitmap offscreenBitmap;
    private static Canvas offscreenCanvas;

    /* Paint objects reused by native code */
    private static Paint[] pens = new Paint[64];
    private static Paint[] brushes = new Paint[64];
    private static Paint[] fonts = new Paint[64];
    private static int penNext = 1, brushNext = 1, fontNext = 1;
    private static Path[] paths = new Path[64];
    private static int pathNext = 1;

    static {
        /* The OHBridge native methods are in this lib */
        System.loadLibrary("ohbridge_android");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        currentHolder = holder;
        surfaceWidth = surfaceView.getWidth();
        surfaceHeight = surfaceView.getHeight();

        /* Create offscreen bitmap for double-buffering */
        offscreenBitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);

        running = true;
        engineThread = new Thread() {
            public void run() {
                try {
                    /* Extract DEX files from assets to cache dir */
                    File cacheDir = getCacheDir();
                    File shimDex = extractAsset("aosp-shim.dex", cacheDir);
                    File appDex = extractAsset("app.dex", cacheDir);

                    if (shimDex == null || appDex == null) {
                        Log.e(TAG, "Failed to extract DEX files");
                        return;
                    }

                    /* Create child-first DexClassLoader that shadows android.* classes
                     * Normal: parent finds android.app.Activity → phone's real class
                     * Child-first: shim DEX finds android.app.Activity → our shim class */
                    String dexPath = shimDex.getAbsolutePath() + ":" + appDex.getAbsolutePath();
                    File optDir = new File(cacheDir, "opt");
                    optDir.mkdirs();
                    final String nativeLibDir = getApplicationInfo().nativeLibraryDir;

                    DexClassLoader inner = new DexClassLoader(
                        dexPath, optDir.getAbsolutePath(), nativeLibDir,
                        WestlakeHostActivity.class.getClassLoader());

                    /* Wrap in a child-first classloader */
                    ClassLoader loader = new ClassLoader(WestlakeHostActivity.class.getClassLoader()) {
                        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                            /* Always check shim first for android.* and app classes */
                            if (name.startsWith("android.") || name.startsWith("com.ohos.") ||
                                name.startsWith("com.example.") || name.startsWith("dalvik.")) {
                                try {
                                    Class<?> c = inner.loadClass(name);
                                    if (c != null) return c;
                                } catch (ClassNotFoundException e) { /* fall through */ }
                            }
                            /* Java core classes always from parent */
                            return super.loadClass(name, resolve);
                        }
                    };
                    Thread.currentThread().setContextClassLoader(loader);

                    /* Load and run MockDonaldsApp.main() */
                    Log.i(TAG, "Loading MockDonaldsApp...");
                    Class<?> appClass = loader.loadClass("com.example.mockdonalds.MockDonaldsApp");
                    Method main = appClass.getMethod("main", String[].class);
                    Log.i(TAG, "Running MockDonaldsApp.main()...");
                    main.invoke(null, (Object) new String[0]);
                    Log.i(TAG, "MockDonaldsApp.main() returned");
                } catch (Exception e) {
                    Log.e(TAG, "Engine error: " + e.getMessage(), e);
                }
            }
        };
        engineThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        surfaceWidth = w;
        surfaceHeight = h;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        currentHolder = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* TODO: forward touch to Westlake engine */
        return super.onTouchEvent(event);
    }

    private File extractAsset(String name, File dir) {
        try {
            InputStream is = getAssets().open(name);
            File out = new File(dir, name);
            FileOutputStream fos = new FileOutputStream(out);
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) fos.write(buf, 0, n);
            fos.close();
            is.close();
            return out;
        } catch (Exception e) {
            Log.e(TAG, "Extract " + name + ": " + e.getMessage());
            return null;
        }
    }

    /* ══════════════════════════════════════════════════════
     * Static methods called by native OHBridge via JNI
     * These draw to the offscreen canvas using Android APIs
     * ══════════════════════════════════════════════════════ */

    public static void nativeFlush() {
        if (currentHolder == null || offscreenBitmap == null) return;
        Canvas c = currentHolder.lockCanvas();
        if (c != null) {
            float sx = (float) c.getWidth() / 480f;
            float sy = (float) c.getHeight() / 800f;
            float s = Math.min(sx, sy);
            float dx = (c.getWidth() - 480 * s) / 2f;
            float dy = (c.getHeight() - 800 * s) / 2f;
            c.drawColor(Color.BLACK);
            c.translate(dx, dy);
            c.scale(s, s);
            c.drawBitmap(offscreenBitmap, 0, 0, null);
            currentHolder.unlockCanvasAndPost(c);
        }
    }

    public static Canvas getOffscreenCanvas() { return offscreenCanvas; }
    public static Bitmap getOffscreenBitmap() { return offscreenBitmap; }

    /* Pen management */
    public static int createPen() {
        int id = penNext++; if (id >= 64) { id = 1; penNext = 2; }
        pens[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pens[id].setStyle(Paint.Style.STROKE);
        pens[id].setColor(Color.BLACK);
        pens[id].setStrokeWidth(1);
        return id;
    }
    public static Paint getPen(int id) { return (id > 0 && id < 64) ? pens[id] : null; }

    /* Brush management */
    public static int createBrush() {
        int id = brushNext++; if (id >= 64) { id = 1; brushNext = 2; }
        brushes[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        brushes[id].setStyle(Paint.Style.FILL);
        brushes[id].setColor(Color.BLACK);
        return id;
    }
    public static Paint getBrush(int id) { return (id > 0 && id < 64) ? brushes[id] : null; }

    /* Font/text paint management */
    public static int createFont() {
        int id = fontNext++; if (id >= 64) { id = 1; fontNext = 2; }
        fonts[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        fonts[id].setTextSize(16);
        fonts[id].setColor(Color.BLACK);
        fonts[id].setTypeface(Typeface.DEFAULT);
        return id;
    }
    public static Paint getFont(int id) { return (id > 0 && id < 64) ? fonts[id] : null; }

    /* Path management */
    public static int createPath() {
        int id = pathNext++; if (id >= 64) { id = 1; pathNext = 2; }
        paths[id] = new Path();
        return id;
    }
    public static Path getPath(int id) { return (id > 0 && id < 64) ? paths[id] : null; }
}
