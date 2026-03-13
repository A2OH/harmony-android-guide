package android.graphics.drawable;

/**
 * Shim: android.graphics.drawable.Animatable
 * OH mapping: Animator / FrameAnimation lifecycle callbacks
 *
 * Interface implemented by Drawable subclasses that support frame-by-frame or
 * property animation.  Callers use {@link #start()} / {@link #stop()} to
 * control playback and {@link #isRunning()} to poll status.
 */
public interface Animatable {

    /**
     * Start the animation.  Has no effect if the animation is already running.
     */
    void start();

    /**
     * Stop the animation.  Has no effect if the animation is not running.
     */
    void stop();

    /**
     * Indicates whether the animation is currently running.
     *
     * @return {@code true} if the animation is running, {@code false} otherwise
     */
    boolean isRunning();
}
