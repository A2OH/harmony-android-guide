package android.gesture;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible Gesture shim.
 * Represents a single gesture (a sequence of strokes).
 */
public class Gesture {

    // -------------------------------------------------------------------------
    // Inner class: GestureStroke  (minimal stub)
    // -------------------------------------------------------------------------
    public static class GestureStroke {
        public final float[] points;

        public GestureStroke(float[] points) {
            this.points = points != null ? points.clone() : new float[0];
        }

        public float getLength() {
            if (points == null || points.length < 4) return 0f;
            float len = 0f;
            for (int i = 2; i < points.length; i += 2) {
                float dx = points[i]     - points[i - 2];
                float dy = points[i + 1] - points[i - 1];
                len += (float) Math.sqrt(dx * dx + dy * dy);
            }
            return len;
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: RectF  (minimal stub — avoids android.graphics dependency)
    // -------------------------------------------------------------------------
    public static class RectF {
        public float left, top, right, bottom;

        public RectF(float left, float top, float right, float bottom) {
            this.left   = left;
            this.top    = top;
            this.right  = right;
            this.bottom = bottom;
        }

        public float width()  { return right - left; }
        public float height() { return bottom - top; }

        @Override public String toString() {
            return "RectF{" + left + "," + top + "," + right + "," + bottom + "}";
        }
    }

    // -------------------------------------------------------------------------
    // Gesture state
    // -------------------------------------------------------------------------
    private static long sNextId = 1;

    private final long mId;
    private final List<GestureStroke> mStrokes = new ArrayList<>();

    public Gesture() {
        mId = sNextId++;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public long getID() { return mId; }

    public List<GestureStroke> getStrokes() {
        return new ArrayList<>(mStrokes);
    }

    public int getStrokesCount() {
        return mStrokes.size();
    }

    public void addStroke(GestureStroke stroke) {
        if (stroke != null) mStrokes.add(stroke);
    }

    /**
     * Returns the bounding box that encloses all strokes.
     */
    public RectF getBoundingBox() {
        if (mStrokes.isEmpty()) return new RectF(0, 0, 0, 0);
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (GestureStroke stroke : mStrokes) {
            for (int i = 0; i < stroke.points.length - 1; i += 2) {
                float x = stroke.points[i];
                float y = stroke.points[i + 1];
                if (x < minX) minX = x;
                if (y < minY) minY = y;
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }
        }
        return new RectF(minX, minY, maxX, maxY);
    }

    /**
     * Returns the total length of all strokes.
     */
    public float getLength() {
        float total = 0f;
        for (GestureStroke s : mStrokes) total += s.getLength();
        return total;
    }

    /**
     * Serialize this gesture to a byte array. Stub — returns empty array.
     */
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override public String toString() {
        return "Gesture{id=" + mId + ", strokes=" + mStrokes.size() + "}";
    }
}
