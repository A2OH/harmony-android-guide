package android.view;

import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;

/** Auto-generated stub for AOSP compilation. */
public interface IWindowSession {
    IWindowId getWindowId(IBinder window) throws RemoteException;
    void getDisplayFrame(IWindow window, Rect outDisplayFrame) throws RemoteException;
    void updatePointerIcon(IWindow window) throws RemoteException;
    boolean startMovingTask(IWindow window, float startX, float startY) throws RemoteException;
    void finishMovingTask(IWindow window) throws RemoteException;
    void cancelDragAndDrop(IBinder dragToken, boolean skipAnimation) throws RemoteException;
    android.os.IBinder performDrag(IWindow window, int flags, SurfaceControl surface, int touchSource,
                        float touchX, float touchY, float thumbCenterX, float thumbCenterY,
                        android.content.ClipData data) throws RemoteException;
}
