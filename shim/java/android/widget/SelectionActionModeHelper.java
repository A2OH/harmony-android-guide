package android.widget;

import android.graphics.Canvas;
import android.view.textclassifier.TextClassification;

/**
 * Stub for android.widget.SelectionActionModeHelper.
 * Assists with smart text selection and action mode.
 */
public class SelectionActionModeHelper {

    public SelectionActionModeHelper(Editor editor) {}

    public void startSelectionActionModeAsync(boolean adjustSelection) {}
    public void startLinkActionModeAsync(int start, int end) {}
    public void invalidateActionModeAsync() {}
    public void onSelectionAction(int menuItemId, String menuItemTitle) {}
    public void onSelectionDrag() {}
    public void onTextChanged(int start, int end) {}
    public void onDraw(Canvas canvas) {}
    public boolean isDrawingHighlight() { return false; }
    public void onSelectionChanged() {}
    public TextClassification getTextClassification() { return null; }
    public boolean resetSelection(int textIndex) { return false; }
    public void onDestroyActionMode() {}
}
