package android.view;

import android.graphics.Rect;
import android.os.IBinder;

/** Auto-generated stub for AOSP compilation. */
public interface IWindowSession {
    IWindowId getWindowId(IBinder window);
    void getDisplayFrame(IWindow window, Rect outDisplayFrame);
    void updatePointerIcon(IWindow window);
    void startMovingTask(IWindow window, float startX, float startY);
    void finishMovingTask(IWindow window);
    void cancelDragAndDrop(IBinder dragToken, boolean skipAnimation);
}
