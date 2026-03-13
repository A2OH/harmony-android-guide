package android.webkit;

public class TracingConfig {
    public static final int CATEGORIES_ALL = 0;
    public static final int CATEGORIES_ANDROID_WEBVIEW = 0;
    public static final int CATEGORIES_FRAME_VIEWER = 0;
    public static final int CATEGORIES_INPUT_LATENCY = 0;
    public static final int CATEGORIES_JAVASCRIPT_AND_RENDERING = 0;
    public static final int CATEGORIES_NONE = 0;
    public static final int CATEGORIES_RENDERING = 0;
    public static final int CATEGORIES_WEB_DEVELOPER = 0;
    public static final int RECORD_CONTINUOUSLY = 0;
    public static final int RECORD_UNTIL_FULL = 0;

    public TracingConfig() {}

    public int getPredefinedCategories() { return 0; }
    public int getTracingMode() { return 0; }
}
