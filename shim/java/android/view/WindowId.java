package android.view;

public class WindowId {
    public WindowId() {}
    public WindowId(IWindowId iWindowId) {}

    public int describeContents() { return 0; }
    public boolean isFocused() { return false; }
    public void registerFocusObserver(Object p0) {}
    public void unregisterFocusObserver(Object p0) {}
    public void writeToParcel(Object p0, Object p1) {}
    public void onFocusGained(Object p0) {}
    public void onFocusLost(Object p0) {}
}
