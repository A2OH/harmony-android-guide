package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ViewerActivity extends Activity {
    private static final String PNG_PATH = "/sdcard/westlake_frame.png";
    private String touchPath;
    private static final int FB_W = 480;
    private static final int FB_H = 800;
    private ImageView iv;
    private Handler handler;

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setBackgroundColor(Color.BLACK);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);
        handler = new Handler();
        touchPath = getExternalFilesDir(null).getAbsolutePath() + "/touch.dat";
        handler.post(pollPng);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX() * FB_W / iv.getWidth();
        float y = event.getY() * FB_H / iv.getHeight();
        int action = 2;
        if (event.getAction() == MotionEvent.ACTION_DOWN) action = 0;
        else if (event.getAction() == MotionEvent.ACTION_UP) action = 1;
        writeTouchEvent(action, (int)x, (int)y);
        try { new java.io.FileOutputStream(getExternalFilesDir(null).getAbsolutePath() + "/touched").close(); } catch(Exception ex){}
        return true;
    }

    private Runnable pollPng = new Runnable() {
        public void run() {
            File f = new File(PNG_PATH);
            if (f.exists() && f.length() > 100) {
                Bitmap bmp = BitmapFactory.decodeFile(PNG_PATH);
                if (bmp != null) iv.setImageBitmap(bmp);
            }
            handler.postDelayed(this, 500);
        }
    };

    private void writeTouchEvent(int action, int x, int y) {
        try {
            RandomAccessFile raf = new RandomAccessFile(touchPath, "rw");
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(action);
            buf.putInt(x);
            buf.putInt(y);
            buf.putInt((int)(System.currentTimeMillis() & 0x7FFFFFFF));
            raf.write(buf.array());
            raf.close();
            // Also try /sdcard/
            try { RandomAccessFile raf2 = new RandomAccessFile("/sdcard/westlake_touch.dat", "rw"); raf2.write(buf.array()); raf2.close(); } catch (Exception e2) {}
        } catch (Exception e) {}
    }
}
