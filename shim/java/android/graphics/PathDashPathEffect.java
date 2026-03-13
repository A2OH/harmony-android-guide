package android.graphics;

/**
 * Android-compatible PathDashPathEffect shim. Creates a dash effect by stamping
 * the specified shape at regular intervals along the path.
 */
public class PathDashPathEffect extends PathEffect {

    public PathDashPathEffect(Path shape, float advance, float phase, Style style) {}

    public enum Style {
        TRANSLATE,
        ROTATE,
        MORPH
    }
}
