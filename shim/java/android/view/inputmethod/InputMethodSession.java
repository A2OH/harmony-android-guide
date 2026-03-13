package android.view.inputmethod;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface InputMethodSession {
    void appPrivateCommand(String p0, Bundle p1);
    void dispatchGenericMotionEvent(int p0, MotionEvent p1, Object p2);
    void dispatchKeyEvent(int p0, KeyEvent p1, Object p2);
    void dispatchTrackballEvent(int p0, MotionEvent p1, Object p2);
    void displayCompletions(CompletionInfo[] p0);
    void finishInput();
    void toggleSoftInput(int p0, int p1);
    void updateCursor(Rect p0);
    void updateCursorAnchorInfo(CursorAnchorInfo p0);
    void updateExtractedText(int p0, ExtractedText p1);
    void updateSelection(int p0, int p1, int p2, int p3, int p4, int p5);
    void viewClicked(boolean p0);
}
