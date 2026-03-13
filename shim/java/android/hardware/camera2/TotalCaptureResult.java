package android.hardware.camera2;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible TotalCaptureResult shim.
 * The total accumulated result of a single image capture, aggregating all
 * partial results into a single object.
 */
public class TotalCaptureResult extends CaptureResult {

    private final List<CaptureResult> mPartialResults;

    /**
     * Construct a TotalCaptureResult with no partial results.
     */
    public TotalCaptureResult() {
        super();
        mPartialResults = Collections.emptyList();
    }

    /**
     * Construct a TotalCaptureResult with the given partial results.
     *
     * @param tag            opaque request tag
     * @param frameNumber    frame number from the camera device
     * @param partialResults list of partial CaptureResults that compose this total
     */
    public TotalCaptureResult(Object tag, long frameNumber, List<CaptureResult> partialResults) {
        super(tag, frameNumber);
        mPartialResults = partialResults != null
                ? Collections.unmodifiableList(partialResults)
                : Collections.<CaptureResult>emptyList();
    }

    /**
     * Get the list of partial results that make up this TotalCaptureResult.
     * May be empty if the camera device does not support partial results.
     *
     * @return unmodifiable list of partial CaptureResults
     */
    public List<CaptureResult> getPartialResults() {
        return mPartialResults;
    }

    @Override
    public String toString() {
        return "TotalCaptureResult{frameNumber=" + getFrameNumber()
                + ", partials=" + mPartialResults.size() + "}";
    }
}
