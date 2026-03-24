package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;

public class ViewerActivity extends Activity {
    private static final String FB_PATH = "/data/local/tmp/a2oh/framebuffer.raw";
    private static final int FB_W = 480;
    private static final int FB_H = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(Color.BLACK);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(iv);

        /* Read framebuffer once and display */
        Bitmap bmp = readFramebuffer();
        if (bmp != null) {
            iv.setImageBitmap(bmp);
        }
    }

    private Bitmap readFramebuffer() {
        try {
            /* Wait for file to exist */
            File f = new File(FB_PATH);
            for (int i = 0; i < 50 && (!f.exists() || f.length() != FB_W * FB_H * 4); i++) {
                Thread.sleep(200);
            }
            if (!f.exists() || f.length() != FB_W * FB_H * 4) return null;

            /* Read */
            byte[] raw = new byte[FB_W * FB_H * 4];
            FileInputStream fis = new FileInputStream(f);
            int total = 0;
            while (total < raw.length) {
                int n = fis.read(raw, total, raw.length - total);
                if (n <= 0) break;
                total += n;
            }
            fis.close();
            if (total != raw.length) return null;

            /* BGRA → ARGB */
            int[] pixels = new int[FB_W * FB_H];
            for (int i = 0; i < FB_W * FB_H; i++) {
                int off = i * 4;
                int b = raw[off] & 0xFF;
                int g = raw[off + 1] & 0xFF;
                int r = raw[off + 2] & 0xFF;
                int a = raw[off + 3] & 0xFF;
                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
            Bitmap bmp = Bitmap.createBitmap(FB_W, FB_H, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, FB_W, 0, 0, FB_W, FB_H);
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }
}
