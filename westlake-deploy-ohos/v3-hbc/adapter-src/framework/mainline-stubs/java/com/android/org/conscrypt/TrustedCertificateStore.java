/*
 * Stub for com.android.org.conscrypt.TrustedCertificateStore.
 * Real impl is in Conscrypt mainline APEX (com.android.conscrypt).
 *
 * ActivityThread.main() line 8160:
 *   TrustedCertificateStore.setDefaultUserDirectory(configDir);
 * Adapter has no per-user CA trust store management; no-op stub.
 */
package com.android.org.conscrypt;

import java.io.File;

public class TrustedCertificateStore {
    public static void setDefaultUserDirectory(File dir) {
        // no-op
    }
}
