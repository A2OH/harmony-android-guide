package android.view;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.autofill.AutofillId;
import java.util.Set;

/**
 * Android-compatible ViewStructure shim.
 * Abstract class used to build an autofill-compatible structure describing
 * the content of a view hierarchy. All methods are stubs.
 */
public abstract class ViewStructure {

    public void setClassName(String className) {}
    public void setText(CharSequence text) {}
    public void setText(CharSequence text, int selectionStart, int selectionEnd) {}
    public void setTextLines(int[] charOffsets, int[] baselines) {}
    public void setHint(CharSequence hint) {}
    public void setContentDescription(CharSequence contentDescription) {}
    public void setVisibility(int visibility) {}
    public void setDimens(int left, int top, int scrollX, int scrollY, int width, int height) {}
    public void setId(int id, String packageName, String typeName, String entryName) {}
    public void setAutofillType(int autofillType) {}
    public void setAutofillValue(Object autofillValue) {}
    public void setAutofillHints(String[] hints) {}
    public void setAutofillId(AutofillId id) {}
    public int getChildCount() { return 0; }
    public void setChildCount(int num) {}
    public void addChildCount(int num) {}
    public ViewStructure newChild(int index) { return null; }
    public ViewStructure asyncNewChild(int index) { return null; }

    // Methods needed for View.java compilation
    public Rect getTempRect() { return new Rect(); }
    public void setAccessibilityFocused(boolean focused) {}
    public void setActivated(boolean activated) {}
    public void setAssistBlocked(boolean blocked) {}
    public void setCheckable(boolean checkable) {}
    public void setChecked(boolean checked) {}
    public void setClickable(boolean clickable) {}
    public void setContextClickable(boolean contextClickable) {}
    public void setDataIsSensitive(boolean sensitive) {}
    public void setElevation(float elevation) {}
    public void setEnabled(boolean enabled) {}
    public void setFocusable(boolean focusable) {}
    public void setFocused(boolean focused) {}
    public void setImportantForAutofill(int mode) {}
    public void setInputType(int inputType) {}
    public void setLongClickable(boolean longClickable) {}
    public void setMaxTextLength(int max) {}
    public void setOpaque(boolean opaque) {}
    public void setSelected(boolean selected) {}
    public void setTransformation(Matrix matrix) {}
    public void setTextStyle(float textSize, int textColor, int bgColor, int style) {}
    public void setHintIdEntry(String entry) {}
    public void setTextIdEntry(String entry) {}
    public void setMaxTextEms(int maxEms) {}
    public void setMinTextEms(int minEms) {}
}
