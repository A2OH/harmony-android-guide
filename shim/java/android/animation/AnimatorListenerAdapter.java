package android.animation;

/**
 * Android-compatible AnimatorListenerAdapter stub.
 * Abstract convenience class implementing both AnimatorListener and AnimatorPauseListener
 * with empty method bodies so subclasses can override only what they need.
 */
public class AnimatorListenerAdapter
        implements Animator.AnimatorListener, Animator.AnimatorPauseListener {

    @Override
    public void onAnimationStart(Animator animation) {}

    @Override
    public void onAnimationEnd(Animator animation) {}

    @Override
    public void onAnimationCancel(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}

    @Override
    public void onAnimationPause(Animator animation) {}

    @Override
    public void onAnimationResume(Animator animation) {}
}
