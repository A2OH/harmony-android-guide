package android.gesture;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class GestureStore {
    public static final int ORIENTATION_INVARIANT = 0;
    public static final int ORIENTATION_SENSITIVE = 0;
    public static final int SEQUENCE_INVARIANT = 0;
    public static final int SEQUENCE_SENSITIVE = 0;

    public GestureStore() {}

    public void addGesture(String p0, Gesture p1) {}
    public Set<?> getGestureEntries() { return null; }
    public ArrayList<Object> getGestures(String p0) { return null; }
    public int getOrientationStyle() { return 0; }
    public int getSequenceType() { return 0; }
    public boolean hasChanged() { return false; }
    public void load(InputStream p0) {}
    public void load(InputStream p0, boolean p1) {}
    public ArrayList<Object> recognize(Gesture p0) { return null; }
    public void removeEntry(String p0) {}
    public void removeGesture(String p0, Gesture p1) {}
    public void save(OutputStream p0) {}
    public void save(OutputStream p0, boolean p1) {}
    public void setOrientationStyle(int p0) {}
    public void setSequenceType(int p0) {}
}
