package android.content.res;

public class Configuration {
    public static final int ORIENTATION_UNDEFINED  = 0;
    public static final int ORIENTATION_PORTRAIT   = 1;
    public static final int ORIENTATION_LANDSCAPE  = 2;

    public int             orientation   = ORIENTATION_PORTRAIT;
    public int             screenWidthDp  = 360;
    public int             screenHeightDp = 640;
    public int             densityDpi     = 320;
    public java.util.Locale locale        = java.util.Locale.getDefault();
}
