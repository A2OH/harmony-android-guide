package com.ohos.shim.bridge;

/**
 * Minimal late-bridge state snapshot carried outside OHBridge.
 */
public final class OHBridgeLateSnapshot {
    public final boolean clinitComplete;
    public final boolean nativeAvailable;

    public OHBridgeLateSnapshot(boolean clinitComplete, boolean nativeAvailable) {
        this.clinitComplete = clinitComplete;
        this.nativeAvailable = nativeAvailable;
    }

    public boolean canUseNativeBridge() {
        return clinitComplete && nativeAvailable;
    }
}
