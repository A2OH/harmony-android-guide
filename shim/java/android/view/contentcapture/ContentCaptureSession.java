package android.view.contentcapture;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;

/**
 * Android-compatible ContentCaptureSession shim.
 * Abstract base that tracks view lifecycle events for content capture.
 */
public class ContentCaptureSession implements AutoCloseable {

    /**
     * Creates a new ViewStructure for the given view, to be passed to
     * {@link #notifyViewAppeared(Object)}.
     *
     * @param view the view (android.view.View on real Android; Object here)
     * @return a new Object representing the ViewStructure (stub)
     */
    public Object newViewStructure(Object view) {
        return new Object();
    }

    /**
     * Notifies the content capture service that a view has appeared.
     *
     * @param viewStructure filled structure as returned by {@link #newViewStructure(Object)}
     */
    public void notifyViewAppeared(Object viewStructure) {
        // stub
    }

    /**
     * Notifies the content capture service that a view has disappeared.
     *
     * @param id the AutofillId of the disappeared view
     */
    public void notifyViewDisappeared(Object id) {
        // stub
    }

    /**
     * Notifies the content capture service that the text of a view has changed.
     *
     * @param id       the AutofillId of the changed view
     * @param charSeq  the new text content
     */
    public void notifyViewTextChanged(Object id, CharSequence charSeq) {
        // stub
    }

    /** Destroys this session and releases associated resources. */
    public void destroy() {
        // stub
    }

    /** Alias for {@link #destroy()} to satisfy {@link AutoCloseable}. */
    @Override
    public void close() {
        destroy();
    }

    public Object getId() { return null; }
    public void internalNotifyViewTreeEvent(boolean started) {}
}
