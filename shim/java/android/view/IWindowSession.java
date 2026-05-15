// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- compile-time stub for android.view.IWindowSession.
//
// Pre-M4b note: this file was a minimal placeholder with no Stub class
// because no code in shim referenced it.  M4b's WestlakeWindowManagerService
// returns an IWindowSession from openSession() and may need a sibling
// WestlakeWindowSession class extending IWindowSession.Stub.
//
// At runtime, framework.jar's IWindowSession.Stub wins (this shim is
// stripped via framework_duplicates.txt).  This stub merely satisfies
// javac.
//
// We keep the original methods from the pre-M4b stub so the shim source
// graph still compiles even where code uses the older API.

package android.view;

import android.graphics.Rect;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IWindowSession extends IInterface {
    IWindowId getWindowId(IBinder window) throws RemoteException;
    void getDisplayFrame(IWindow window, Rect outDisplayFrame) throws RemoteException;
    void updatePointerIcon(IWindow window) throws RemoteException;
    boolean startMovingTask(IWindow window, float startX, float startY) throws RemoteException;
    void finishMovingTask(IWindow window) throws RemoteException;
    void cancelDragAndDrop(IBinder dragToken, boolean skipAnimation) throws RemoteException;
    android.os.IBinder performDrag(IWindow window, int flags, SurfaceControl surface, int touchSource,
                        float touchX, float touchY, float thumbCenterX, float thumbCenterY,
                        android.content.ClipData data) throws RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Mirrors the framework.jar IWindowSession.Stub layout so
    // WestlakeWindowSession can extend Stub here for compile-time use; at
    // runtime, framework.jar's Stub (with ~40 methods on Android 16) wins.
    public static abstract class Stub extends android.os.Binder implements IWindowSession {
        private static final java.lang.String DESCRIPTOR = "android.view.IWindowSession";
        public Stub() { attachInterface(this, DESCRIPTOR); }
        public static IWindowSession asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            return (i instanceof IWindowSession) ? (IWindowSession) i : null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
