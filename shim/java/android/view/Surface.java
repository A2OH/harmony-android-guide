package android.view;

/**
 * Android-compatible Surface shim. Stub implementation.
 */
public class Surface {
    private final String mName;

    public Surface() { mName = "Surface"; }

    public Surface(String name) { mName = name; }

    public boolean isValid() { return true; }

    public android.graphics.Canvas lockCanvas(android.graphics.Rect dirty) {
        return new android.graphics.Canvas();
    }

    public void unlockCanvasAndPost(android.graphics.Canvas canvas) {
        // stub: no-op
    }

    public void release() { /* stub: no-op */ }

    @Override
    public String toString() { return "Surface{" + mName + "}"; }
}
