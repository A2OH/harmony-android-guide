package android.transition;

import android.view.View;
import android.view.ViewGroup;

/**
 * Android-compatible Scene shim.
 * Represents a UI state for use with TransitionManager. Stub — enter/exit are no-ops.
 */
public final class Scene {

    private final ViewGroup mSceneRoot;
    private View mLayout;
    private Runnable mEnterAction;
    private Runnable mExitAction;

    public Scene(ViewGroup sceneRoot) {
        mSceneRoot = sceneRoot;
    }

    public Scene(ViewGroup sceneRoot, View layout) {
        mSceneRoot = sceneRoot;
        mLayout = layout;
    }

    public static Scene getSceneForLayout(ViewGroup sceneRoot, int layoutId, Object context) {
        return new Scene(sceneRoot);
    }

    public void enter() {
        if (mEnterAction != null) {
            mEnterAction.run();
        }
    }

    public void exit() {
        if (mExitAction != null) {
            mExitAction.run();
        }
    }

    public ViewGroup getSceneRoot() {
        return mSceneRoot;
    }

    public void setEnterAction(Runnable action) {
        mEnterAction = action;
    }

    public void setExitAction(Runnable action) {
        mExitAction = action;
    }
}
