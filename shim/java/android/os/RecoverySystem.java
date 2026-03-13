package android.os;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A2OH shim: RecoverySystem - stubs for recovery/update operations.
 * Real implementations require kernel-level access; all methods throw or no-op.
 */
public class RecoverySystem {

    public RecoverySystem() {}

    /**
     * Stub: verify an OTA package. Throws in production use; no-op in shim.
     */
    public static void verifyPackage(File packageFile, VerifyProgressListener listener, File deviceCertsZipFile)
            throws IOException, GeneralSecurityException {
        // No-op shim: verification not supported on OpenHarmony
    }

    /**
     * Stub: reboot and apply an OTA package.
     * Throws UnsupportedOperationException since the recovery partition is unavailable.
     */
    public static void installPackage(android.content.Context context, File packageFile) throws IOException {
        throw new UnsupportedOperationException("RecoverySystem.installPackage not supported in A2OH shim");
    }

    /**
     * Stub: reboot and wipe user data (factory reset).
     */
    public static void rebootWipeUserData(android.content.Context context) throws IOException {
        throw new UnsupportedOperationException("RecoverySystem.rebootWipeUserData not supported in A2OH shim");
    }

    /**
     * Stub: reboot and wipe the cache partition.
     */
    public static void rebootWipeCache(android.content.Context context) throws IOException {
        throw new UnsupportedOperationException("RecoverySystem.rebootWipeCache not supported in A2OH shim");
    }

    /** Listener interface for OTA verification progress. */
    public interface VerifyProgressListener {
        void onProgress(int progress);
    }
}
