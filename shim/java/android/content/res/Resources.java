package android.content.res;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class Resources {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() { super(); }
        public NotFoundException(String message) { super(message); }
        public NotFoundException(String message, Throwable cause) { super(message, cause); }
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();

    public String getString(int id) {
        return "string_" + id;
    }

    public String getString(int id, Object... formatArgs) {
        return String.format(getString(id), formatArgs);
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public int getInteger(int id) {
        return 0;
    }

    public boolean getBoolean(int id) {
        return false;
    }

    public float getDimension(int id) {
        return 0f;
    }

    public int getColor(int id) {
        return 0xFF000000;
    }

    public Drawable getDrawable(int id) {
        return null;
    }

    public int getDimensionPixelSize(int id) {
        return 0;
    }

    public String[] getStringArray(int id) {
        return new String[0];
    }

    public int[] getIntArray(int id) {
        return new int[0];
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }
}
