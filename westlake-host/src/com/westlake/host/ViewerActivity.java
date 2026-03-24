package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Reads /data/local/tmp/a2oh/framebuffer.raw and displays it on screen.
 * The framebuffer is written by dalvikvm's OHBridge software renderer.
 */
public class ViewerActivity extends Activity implements SurfaceHolder.Callback {
    private static final String FB_PATH = "/data/local/tmp/a2oh/framebuffer.raw";
    private static final int FB_W = 480;
    private static final int FB_H = 800;

    private SurfaceView surfaceView;
    private volatile boolean running = false;
    private Thread renderThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        final SurfaceHolder h = holder;
        renderThread = new Thread() { @Override public void run() {
            Bitmap bitmap = Bitmap.createBitmap(FB_W, FB_H, Bitmap.Config.ARGB_8888);
            int[] pixels = new int[FB_W * FB_H];
            Paint paint = new Paint();
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(24);

            byte[] raw = new byte[FB_W * FB_H * 4];
            long lastSize = 0;
            boolean hasFrame = false;

            while (running) {
                try {
                    /* Check for lock file — writer creates .lock before write, removes after */
                    File lockFile = new File(FB_PATH + ".lock");
                    File fbFile = new File(FB_PATH);
                    long curSize = fbFile.length();

                    /* Only read when no lock and file is complete size */
                    if (!lockFile.exists() && fbFile.exists() && curSize == FB_W * FB_H * 4) {
                        java.io.FileInputStream fis = new java.io.FileInputStream(fbFile);
                        int total = 0;
                        while (total < raw.length) {
                            int n = fis.read(raw, total, raw.length - total);
                            if (n <= 0) break;
                            total += n;
                        }
                        fis.close();
                        if (total == raw.length) {
                            for (int i = 0; i < FB_W * FB_H; i++) {
                                int off = i * 4;
                                int b2 = raw[off] & 0xFF;
                                int g2 = raw[off+1] & 0xFF;
                                int r2 = raw[off+2] & 0xFF;
                                int a2 = raw[off+3] & 0xFF;
                                pixels[i] = (a2 << 24) | (r2 << 16) | (g2 << 8) | b2;
                            }
                            bitmap.setPixels(pixels, 0, FB_W, 0, 0, FB_W, FB_H);
                            hasFrame = true;
                        }
                    }

                    /* Always draw the last good frame (no flicker) */
                    if (hasFrame) {

                        Canvas canvas = h.lockCanvas();
                        if (canvas != null) {
                            // Scale to fill screen
                            float scaleX = (float) canvas.getWidth() / FB_W;
                            float scaleY = (float) canvas.getHeight() / FB_H;
                            float scale = Math.min(scaleX, scaleY);
                            float dx = (canvas.getWidth() - FB_W * scale) / 2;
                            float dy = (canvas.getHeight() - FB_H * scale) / 2;

                            canvas.drawColor(Color.BLACK);
                            canvas.translate(dx, dy);
                            canvas.scale(scale, scale);
                            canvas.drawBitmap(bitmap, 0, 0, paint);
                            h.unlockCanvasAndPost(canvas);
                        }
                    } else {
                        // No framebuffer yet — show waiting message
                        Canvas canvas = h.lockCanvas();
                        if (canvas != null) {
                            canvas.drawColor(Color.BLACK);
                            canvas.drawText("Waiting for framebuffer...", 50, 100, textPaint);
                            canvas.drawText(FB_PATH, 50, 140, textPaint);
                            h.unlockCanvasAndPost(canvas);
                        }
                    }
                    Thread.sleep(16); // ~60fps
                } catch (Exception e) {
                    try { Thread.sleep(100); } catch (InterruptedException ie) { break; }
                }
            }
            bitmap.recycle();
        }};
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try { renderThread.join(1000); } catch (InterruptedException e) {}
    }
}
