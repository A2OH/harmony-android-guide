package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;

public class ViewerActivity extends Activity {
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);

        try {
            String path = "/storage/emulated/0/Android/data/com.westlake.host/files/framebuffer.raw";
            byte[] raw = new byte[480*800*4];
            FileInputStream fis = new FileInputStream(path);
            int t = 0;
            while (t < raw.length) { int n = fis.read(raw, t, raw.length-t); if (n<=0) break; t+=n; }
            fis.close();

            int[] px = new int[480*800];
            for (int i = 0; i < 480*800; i++) {
                int o = i*4;
                px[i] = 0xFF000000 | ((raw[o+2]&0xFF)<<16) | ((raw[o+1]&0xFF)<<8) | (raw[o]&0xFF);
            }
            Bitmap bmp = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
            bmp.setPixels(px, 0, 480, 0, 0, 480, 800);
            iv.setImageBitmap(bmp);
        } catch (Exception e) {}
    }
}
