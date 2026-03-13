package android.service.autofill;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible FillRequest shim. Represents a request to fill an Activity's fields.
 */
public final class FillRequest {

    public static final int FLAG_MANUAL_REQUEST              = 1;
    public static final int FLAG_COMPATIBILITY_MODE_REQUEST  = 2;

    private final int  mId;
    private final int  mFlags;
    private final List<Object> mFillContexts;

    /** Package-private constructor used by Builder-style creation in real Android. */
    FillRequest(int id, int flags, List<Object> fillContexts) {
        mId           = id;
        mFlags        = flags;
        mFillContexts = fillContexts != null ? fillContexts : Collections.emptyList();
    }

    public int getId() {
        return mId;
    }

    public int getFlags() {
        return mFlags;
    }

    public List<Object> getFillContexts() {
        return mFillContexts;
    }
}
