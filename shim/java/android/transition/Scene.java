package android.transition;

/**
 * Shim for android.transition.Scene.
 * Represents a snapshot of a view hierarchy at a particular po(int in time.
 */
public class Scene {
    private final Object sceneRoot;

    public Scene(Object sceneRoot) {
        this.sceneRoot = sceneRoot;
    }

    /**
     * Object method: returns a Scene inflated from a layout resource.
     * Shim simply wraps the sceneRoot; layoutId and context are ignored.
     */
    public static Scene getSceneForLayout(Object sceneRoot, int layoutId, Object context) {
        return new Scene(sceneRoot);
    }

    /** Enter this scene -- no-op in shim. */
    public void enter() {
        // no-op
    }

    /** Exit this scene -- no-op in shim. */
    public void exit() {
        // no-op
    }

    /** Returns the root of the scene. */
    public Object getSceneRoot() {
        return sceneRoot;
    }
}
