package android.inputmethodservice;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class ExtractEditText extends EditText {
    public ExtractEditText(Context p0) { super(p0); }
    public ExtractEditText(Context p0, AttributeSet p1) { super(p0, p1); }
    public ExtractEditText(Context p0, AttributeSet p1, int p2) { super(p0, p1, p2); }
    public ExtractEditText(Context p0, AttributeSet p1, int p2, int p3) { super(p0, p1, p2, p3); }

    public void finishInternalChanges() {}
    public boolean hasVerticalScrollBar() { return false; }
    public void startInternalChanges() {}
}
