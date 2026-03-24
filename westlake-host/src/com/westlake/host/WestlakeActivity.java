package com.westlake.host;

import android.app.Activity;
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
 * Westlake Engine Host — runs the Westlake shim + app on the phone's own ART.
 *
 * Architecture:
 * - This Activity provides a SurfaceView for rendering
 * - Loads aosp-shim.dex + app.dex via a child-first DexClassLoader
 * - The shim's OHBridge calls back to this Activity for Canvas drawing
 * - Touch events dispatch directly to the shim's Activity
 *
 * The child-first classloader ensures the shim's android.app.Activity
 * shadows the phone's version. Core java.* classes still come from boot.
 */
public class WestlakeActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "Westlake";
    private SurfaceView surfaceView;
    private volatile boolean surfaceReady;
    private Thread engineThread;

    // Rendering state — accessed by OHBridge native methods
    public static Canvas currentCanvas;
    public static SurfaceHolder currentHolder;
    public static int canvasWidth, canvasHeight;
    public static Paint[] paintPool = new Paint[256];
    public static Path[] pathPool = new Path[256];
    public static int paintNext = 1, pathNext = 1;
    public static WestlakeActivity instance;

    // The shim's current Activity for touch dispatch
    public static Object shimActivity;
    public static Method shimDispatchTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        currentHolder = holder;
        canvasWidth = surfaceView.getWidth();
        canvasHeight = surfaceView.getHeight();
        surfaceReady = true;
        Log.i(TAG, "Surface ready: " + canvasWidth + "x" + canvasHeight);

        engineThread = new Thread("WestlakeEngine") {
            public void run() { runEngine(); }
        };
        engineThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder h, int fmt, int w, int h2) {
        canvasWidth = w;
        canvasHeight = h2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        currentHolder = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && shimActivity != null && shimDispatchTouch != null) {
            try {
                // Scale to 480x800
                float x = event.getX() * 480f / canvasWidth;
                float y = event.getY() * 800f / canvasHeight;
                // Create shim MotionEvent and dispatch
                // For now, just log it — will wire up properly
                Log.i(TAG, "Touch UP at (" + (int)x + "," + (int)y + ")");
            } catch (Exception e) {
                Log.e(TAG, "Touch dispatch error", e);
            }
        }
        return true;
    }

    private void runEngine() {
        try {
            Log.i(TAG, "Extracting DEX files...");
            File cacheDir = getCacheDir();
            File shimDex = extractAsset("aosp-shim.dex", cacheDir);
            File appDex = extractAsset("app.dex", cacheDir);

            if (shimDex == null || appDex == null) {
                Log.e(TAG, "Failed to extract DEX files");
                return;
            }

            // Create child-first classloader
            String dexPath = shimDex.getAbsolutePath() + ":" + appDex.getAbsolutePath();
            File optDir = new File(cacheDir, "oat");
            optDir.mkdirs();
            String nativeLibDir = getApplicationInfo().nativeLibraryDir;

            final DexClassLoader dexLoader = new DexClassLoader(
                dexPath, optDir.getAbsolutePath(), nativeLibDir,
                getClassLoader());

            // Child-first wrapper: use findClass to bypass parent delegation
            // findClass only searches the DEX, doesn't delegate to parent
            final java.lang.reflect.Method findClassMethod;
            try {
                findClassMethod = ClassLoader.class.getDeclaredMethod("findClass", String.class);
                findClassMethod.setAccessible(true);
            } catch (Exception ex) { throw new RuntimeException(ex); }

            ClassLoader childFirst = new ClassLoader(getClassLoader()) {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    // Check if already loaded
                    Class<?> c = findLoadedClass(name);
                    if (c != null) return c;
                    // Core java.* always from boot
                    if (name.startsWith("java.") || name.startsWith("javax.") ||
                        name.startsWith("sun.") || name.startsWith("dalvik.system.")) {
                        return super.loadClass(name, resolve);
                    }
                    // Try DEX FIRST via findClass (bypasses parent delegation)
                    try {
                        c = (Class<?>) findClassMethod.invoke(dexLoader, name);
                        if (c != null) return c;
                    } catch (Exception e) { /* not in DEX */ }
                    // Fall back to parent
                    return super.loadClass(name, resolve);
                }
            };

            Thread.currentThread().setContextClassLoader(childFirst);

            Log.i(TAG, "Loading MockDonaldsApp...");
            Class<?> appClass = childFirst.loadClass("com.example.mockdonalds.MockDonaldsApp");
            Method main = appClass.getMethod("main", String[].class);
            Log.i(TAG, "Running...");
            main.invoke(null, (Object) new String[0]);
            Log.i(TAG, "Engine finished");

        } catch (Exception e) {
            Log.e(TAG, "Engine error: " + e.getMessage(), e);
        }
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

    // ═══ Called by OHBridge native methods for rendering ═══

    public static void beginFrame() {
        if (currentHolder == null) return;
        currentCanvas = currentHolder.lockCanvas();
    }

    public static void endFrame() {
        if (currentHolder != null && currentCanvas != null) {
            currentHolder.unlockCanvasAndPost(currentCanvas);
            currentCanvas = null;
        }
    }

    public static Paint getPaint(int id) {
        if (id > 0 && id < 256) return paintPool[id];
        return null;
    }

    public static int newPaint(int style) {
        int id = paintNext++;
        if (id >= 256) { id = 1; paintNext = 2; }
        paintPool[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPool[id].setStyle(style == 0 ? Paint.Style.FILL : Paint.Style.STROKE);
        return id;
    }

    public static Path getPath(int id) {
        if (id > 0 && id < 256) return pathPool[id];
        return null;
    }

    public static int newPath() {
        int id = pathNext++;
        if (id >= 256) { id = 1; pathNext = 2; }
        pathPool[id] = new Path();
        return id;
    }
}
