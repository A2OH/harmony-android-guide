package android.view.inputmethod;
import android.view.View;
import android.view.View;

import android.view.View;

/**
 * Shim: android.view.inputmethod.InputMethodManager
 * Stub — soft keyboard control is handled by ArkUI TextInput nodes natively.
 */
public class InputMethodManager {

    public static final int SHOW_IMPLICIT = 0x0001;
    public static final int SHOW_FORCED = 0x0002;
    public static final int HIDE_IMPLICIT_ONLY = 0x0001;
    public static final int HIDE_NOT_ALWAYS = 0x0002;
    public static final int RESULT_UNCHANGED_SHOWN = 0;
    public static final int RESULT_UNCHANGED_HIDDEN = 1;
    public static final int RESULT_SHOWN = 2;
    public static final int RESULT_HIDDEN = 3;

    private boolean active = false;

    public boolean showSoftInput(View view, int flags) {
        active = true;
        return true;
    }

    public boolean hideSoftInputFromWindow(android.os.IBinder windowToken, int flags) {
        active = false;
        return true;
    }

    public boolean hideSoftInputFromWindow(Object windowToken, int flags) {
        active = false;
        return true;
    }

    public void toggleSoftInput(int showFlags, int hideFlags) {
        active = !active;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isActive(View view) {
        return active;
    }

    public boolean isAcceptingText() {
        return active;
    }

    public void viewClicked(View view) {}
    public void restartInput(View view) {
        // no-op
    }

    public boolean showSoftInput(View view, int flags, android.os.ResultReceiver resultReceiver) {
        active = true;
        return true;
    }

    public void updateSelection(View view, int selStart, int selEnd, int candStart, int candEnd) {}
    public void updateSelection(android.widget.TextView view, int selStart, int selEnd, int candStart, int candEnd) {}
    public void updateCursorAnchorInfo(View view, CursorAnchorInfo cursorAnchorInfo) {}
    public void updateCursorAnchorInfo(android.widget.TextView view, CursorAnchorInfo cursorAnchorInfo) {}
    public boolean isFullscreenMode() { return false; }
    public boolean isCursorAnchorInfoEnabled() { return false; }

    public void displayCompletions(View view, CompletionInfo[] completions) {}
    public void updateExtractedText(View view, int token, ExtractedText text) {}
}
