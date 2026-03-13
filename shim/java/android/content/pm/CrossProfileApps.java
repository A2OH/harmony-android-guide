package android.content.pm;

// Shim: android.content.pm.CrossProfileApps
// Android-to-OpenHarmony migration compatibility stub.

import java.util.Collections;
import java.util.List;

public class CrossProfileApps {

    public CrossProfileApps() {
    }

    /**
     * Return a list of user profiles that the caller can use with
     * {@link #startMainActivity(Object, Object)}.
     *
     * @return list of target UserHandle objects; empty on this platform.
     */
    public List<Object> getTargetUserProfiles() {
        return Collections.emptyList();
    }

    /**
     * Starts the main activity of the given component in the profile specified by userHandle.
     *
     * @param componentName the ComponentName of the activity to launch
     * @param userHandle    the UserHandle of the profile in which to launch the activity
     */
    public void startMainActivity(Object componentName, Object userHandle) {
        // No-op: cross-profile activity launching is not supported on this platform.
    }
}
