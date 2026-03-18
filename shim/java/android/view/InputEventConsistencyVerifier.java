package android.view;

/** AOSP compilation stub. */
public class InputEventConsistencyVerifier {
    public InputEventConsistencyVerifier(Object caller, int flags) {}
    public InputEventConsistencyVerifier(Object caller, int flags, String logTag) {}
    public static boolean isInstrumentationEnabled() { return false; }
    public void onTouchEvent(MotionEvent event, int nestingLevel) {}
    public void onGenericMotionEvent(MotionEvent event, int nestingLevel) {}
    public void onUnhandledEvent(InputEvent event, int nestingLevel) {}
    public void onUnhandledEvent(MotionEvent event, int nestingLevel) {}
    public void onUnhandledEvent(KeyEvent event, int nestingLevel) {}
    public void reset() {}
    public void onTrackballEvent(MotionEvent event, int nestingLevel) {}
    public void onKeyEvent(KeyEvent event, int nestingLevel) {}
}
