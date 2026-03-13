package android.gesture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Android-compatible GestureLibrary shim.
 * Abstract base that stores named gesture sets in memory.
 */
public abstract class GestureLibrary {

    // -------------------------------------------------------------------------
    // Prediction stub
    // -------------------------------------------------------------------------
    public static final class Prediction {
        public final String name;
        public final double score;

        public Prediction(String name, double score) {
            this.name  = name;
            this.score = score;
        }

        @Override public String toString() {
            return "Prediction{name=" + name + ", score=" + score + "}";
        }
    }

    // -------------------------------------------------------------------------
    // Concrete default implementation used by GestureLibraries factory
    // -------------------------------------------------------------------------
    static final class GestureStore extends GestureLibrary {
        GestureStore() {}

        @Override public boolean save() { return true; }

        @Override public boolean load() { return true; }
    }

    // -------------------------------------------------------------------------
    // In-memory storage
    // -------------------------------------------------------------------------
    private final Map<String, List<Gesture>> mLibrary = new HashMap<>();
    private boolean mChanged = false;

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    /** Persist the library. Returns true on success. */
    public abstract boolean save();

    /** Load the library from persistent storage. Returns true on success. */
    public abstract boolean load();

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /** Add a gesture under the given entry name. */
    public void addGesture(String entryName, Gesture gesture) {
        if (entryName == null || gesture == null) return;
        List<Gesture> list = mLibrary.get(entryName);
        if (list == null) {
            list = new ArrayList<>();
            mLibrary.put(entryName, list);
        }
        list.add(gesture);
        mChanged = true;
    }

    /** Remove a specific gesture from an entry. */
    public void removeGesture(String entryName, Gesture gesture) {
        List<Gesture> list = mLibrary.get(entryName);
        if (list != null) {
            list.remove(gesture);
            if (list.isEmpty()) mLibrary.remove(entryName);
            mChanged = true;
        }
    }

    /** Remove all gestures for an entry name. */
    public void removeEntry(String entryName) {
        if (mLibrary.remove(entryName) != null) mChanged = true;
    }

    /** Returns all known entry names. */
    public Set<String> getGestureEntries() {
        return Collections.unmodifiableSet(mLibrary.keySet());
    }

    /** Returns all gestures stored under the given entry name. */
    public List<Gesture> getGestures(String entryName) {
        List<Gesture> list = mLibrary.get(entryName);
        if (list == null) return Collections.emptyList();
        return Collections.unmodifiableList(list);
    }

    /**
     * Attempt to recognise a gesture.
     * Stub returns an empty list (no real recognition algorithm).
     */
    public List<Prediction> recognize(Gesture gesture) {
        return Collections.emptyList();
    }

    /** Returns true if the library has unsaved changes. */
    public boolean hasChanged() { return mChanged; }
}
