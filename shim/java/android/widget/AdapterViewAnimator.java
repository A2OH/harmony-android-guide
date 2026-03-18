package android.widget;
import android.view.animation.Animation;
import android.view.animation.Animation;

/**
 * Shim: android.widget.AdapterViewAnimator — abstract base for widgets
 * that animate between views provided by an Adapter.
 *
 * Real Android subclasses include AdapterViewFlipper, StackView.
 * This stub provides the core child-switching and animation-slot API;
 * actual transitions are no-ops until we wire ArkUI Swiper or custom
 * animation support.
 */
public class AdapterViewAnimator extends AdapterView {

    private Object adapter;
    private int displayedChild;
    private Object inAnimation;
    private Object outAnimation;
    private boolean animateFirstView = true;

    protected AdapterViewAnimator(android.content.Context context) {
        super(context);
    }
    protected AdapterViewAnimator(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    protected AdapterViewAnimator(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ── Adapter ──

    public void setAdapter(Object adapter) {
        this.adapter = adapter;
    }

    public Object getAdapter() {
        return null; // stub — no real adapter binding yet
    }

    // ── AdapterView abstract contract ──

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return null;
    }

    // ── Child switching ──

    public void showNext() {
        // stub — no-op until ArkUI animation wired
    }

    public void showPrevious() {
        // stub — no-op until ArkUI animation wired
    }

    public void setDisplayedChild(int whichChild) {
        this.displayedChild = whichChild;
    }

    public int getDisplayedChild() {
        return 0; // stub
    }

    // ── Animation slots ──

    public void setInAnimation(Object inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Object outAnimation) {
        this.outAnimation = outAnimation;
    }

    public void setAnimateFirstView(boolean animate) {
        this.animateFirstView = animate;
    }
}
