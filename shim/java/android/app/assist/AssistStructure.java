package android.app.assist;

public class AssistStructure {
    public AssistStructure() {}

    public int describeContents() { return 0; }
    public long getAcquisitionEndTime() { return 0L; }
    public long getAcquisitionStartTime() { return 0L; }
    public Object getActivityComponent() { return null; }
    public Object getWindowNodeAt(Object p0) { return null; }
    public int getWindowNodeCount() { return 0; }
    public boolean isHomeActivity() { return false; }
    public void writeToParcel(Object p0, Object p1) {}

    public static class ViewNode {
        public static final int TEXT_STYLE_BOLD = 1;
        public static final int TEXT_STYLE_ITALIC = 2;
        public static final int TEXT_STYLE_UNDERLINE = 4;
        public static final int TEXT_STYLE_STRIKE_THRU = 8;
        public static final int TEXT_COLOR_UNDEFINED = 1;

        public ViewNode() {}
        public int getChildCount() { return 0; }
        public ViewNode getChildAt(int index) { return null; }
        public CharSequence getText() { return null; }
        public int getTextSelectionStart() { return -1; }
        public int getTextSelectionEnd() { return -1; }
        public CharSequence getHint() { return null; }
        public android.os.Bundle getExtras() { return null; }
        public CharSequence getClassName() { return null; }
        public int getInputType() { return 0; }
        public int getId() { return 0; }
        public int getLeft() { return 0; }
        public int getTop() { return 0; }
        public int getWidth() { return 0; }
        public int getHeight() { return 0; }
        public boolean isEnabled() { return true; }
    }
}
