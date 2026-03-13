package android.content.pm;

import java.util.Collections;
import java.util.List;

/**
 * Shim for android.content.pm.ChangedPackages — stub implementation.
 * Holds the sequence number and list of package names that changed since
 * the previous sequence number.
 */
public final class ChangedPackages {

    private final int mSequenceNumber;
    private final List<String> mPackageNames;

    public ChangedPackages(int sequenceNumber, List<String> packageNames) {
        this.mSequenceNumber = sequenceNumber;
        this.mPackageNames   = packageNames != null ? packageNames : Collections.<String>emptyList();
    }

    /** Returns the last known sequence number for these changes. */
    public int getSequenceNumber() {
        return mSequenceNumber;
    }

    /** Returns the names of packages that changed. */
    public List<String> getPackageNames() {
        return mPackageNames;
    }
}
