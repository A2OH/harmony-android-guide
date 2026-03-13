package android.transition;

import android.view.ViewGroup;

/**
 * Android-compatible TransitionManager shim. All methods are no-op stubs.
 * On OpenHarmony, scene transitions are not driven by this class.
 */
public class TransitionManager {

    private static final TransitionManager sInstance = new TransitionManager();

    // ── Static API ──

    public static void beginDelayedTransition(ViewGroup sceneRoot) {
        beginDelayedTransition(sceneRoot, null);
    }

    public static void beginDelayedTransition(ViewGroup sceneRoot, Transition transition) {
        // no-op stub — ArkUI handles layout transitions natively
    }

    public static void go(Scene scene) {
        go(scene, null);
    }

    public static void go(Scene scene, Transition transition) {
        if (scene != null) {
            scene.enter();
        }
    }

    // ── Instance API ──

    public void setTransition(Scene scene, Transition transition) {
        // no-op
    }

    public void setTransition(Scene fromScene, Scene toScene, Transition transition) {
        // no-op
    }

    public void transitionTo(Scene scene) {
        if (scene != null) {
            scene.enter();
        }
    }

    public static TransitionManager getInstance() {
        return sInstance;
    }
}
