package android.widget;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.WordIterator;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

/** AOSP compilation stub for android.widget.Editor. */
public class Editor {
    // Fields referenced by TextView
    boolean mAllowUndo;
    boolean mCreatedWithASelection;
    boolean mCursorVisible = true;
    Object mCustomInsertionActionModeCallback;
    Object mCustomSelectionActionModeCallback;
    boolean mDiscardNextActionUp;
    Drawable mDrawableForCursor;
    boolean mErrorWasChanged;
    CharSequence mError;
    boolean mFrozenWithFocus;
    boolean mIgnoreActionUpEvent;
    boolean mInBatchEditControllers;
    InputContentType mInputContentType;
    InputMethodState mInputMethodState;
    int mInputType;
    Object mInsertionPointCursorController;
    boolean mIsBeingLongClicked;
    boolean mIsBeingLongClickedByAccessibility;
    boolean mIsInsertionActionModeStartPending;
    Object mKeyListener;
    Object mProcessTextIntentActionsHandler;
    boolean mSelectAllOnFocus;
    Object mSelectionModifierCursorController;
    boolean mSelectionMoved;
    boolean mShowSoftInputOnFocus = true;
    Object mSpellChecker;
    Object mSuggestionRangeSpan;
    boolean mTextIsSelectable;
    boolean mTouchFocusSelected;
    Object mUndoInputFilter;

    public Editor() {}

    // Inner classes referenced by AOSP
    public static class InputContentType {
        public int imeOptions;
        public CharSequence imeActionLabel;
        public int imeActionId;
        public Object onEditorActionListener;
        public String privateImeOptions;
        public Object extras;
    }
    public static class InputMethodState {
        public Object mBatchEditNesting;
        public Object mCursorChanged;
        public Object mContentChanged;
        public Object mChangedStart;
        public Object mChangedEnd;
        public Object mChangedDelta;
        public Object mExtracting;
        public Object mExtractedText;
        public int mExtractedTextGeneration;
        public Object mCursorAnchorInfoBuilder;
    }

    // Methods called from TextView
    public void addSpanWatchers(Spannable s) {}
    public void adjustInputType(boolean a, boolean b, boolean c, boolean d) {}
    public void beginBatchEdit() {}
    public boolean canRedo() { return false; }
    public boolean canUndo() { return false; }
    public void createInputContentTypeIfNeeded() {
        if (mInputContentType == null) mInputContentType = new InputContentType();
    }
    public void createInputMethodStateIfNeeded() {
        if (mInputMethodState == null) mInputMethodState = new InputMethodState();
    }
    public void endBatchEdit() {}
    public boolean extractText(ExtractedTextRequest req, ExtractedText out) { return false; }
    public void forgetUndoRedo() {}
    public Object getTextActionMode() { return null; }
    public WordIterator getWordIterator() { return new WordIterator(); }
    public boolean hasInsertionController() { return false; }
    public boolean hasSelectionController() { return false; }
    public void hideCursorAndSpanControllers() {}
    public void hideFloatingToolbar(int duration) {}
    public void invalidateActionModeAsync() {}
    public void invalidateHandlesAndActionMode() {}
    public void invalidateTextDisplayList() {}
    public void invalidateTextDisplayList(Layout layout, int start, int end) {}
    public void loadCursorDrawable() {}
    public void loadHandleDrawables(boolean overwrite) {}
    public void makeBlink() {}
    public void onAttachedToWindow() {}
    public void onCommitCorrection(CorrectionInfo info) {}
    public void onCreateContextMenu(ContextMenu menu) {}
    public void onDetachedFromWindow() {}
    public void onDraw(Canvas c, Layout layout, Path highlight, Paint highlightPaint, int cursorOffset) {}
    public void onDrop(DragEvent event) {}
    public void onFocusChanged(boolean focused, int direction) {}
    public void onLocaleChanged() {}
    public void onScreenStateChanged(int state) {}
    public void onScrollChanged() {}
    public void onTextOperationUserChanged() {}
    public void onTouchEvent(MotionEvent event) {}
    public void onTouchUpEvent(MotionEvent event) {}
    public void onWindowFocusChanged(boolean focused) {}
    public void performLongClick(boolean handled) {}
    public void prepareCursorControllers() {}
    public void redo() {}
    public void refreshTextActionMode() {}
    public void replace() {}
    public void reportExtractedText() {}
    public void restoreInstanceState(Object state) {}
    public Object saveInstanceState() { return null; }
    public void sendOnTextChanged(int start, int before, int after) {}
    public void setContextMenuAnchor(float x, float y) {}
    public void setError(CharSequence error, Drawable icon) {}
    public void setFrame() {}
    public boolean shouldRenderCursor() { return true; }
    public boolean startInsertionActionMode() { return false; }
    public void startLinkActionModeAsync(int start, int end) {}
    public void startSelectionActionModeAsync(boolean adjustSelection) {}
    public void stopTextActionMode() {}
    public void undo() {}
    public void updateCursorPosition() {}
}
