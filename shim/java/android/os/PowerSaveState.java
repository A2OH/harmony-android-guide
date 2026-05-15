// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-power -- compile-time stub for android.os.PowerSaveState
// (AOSP parcelable; stripped from aosp-shim.dex via framework_duplicates.txt).
// Real class lives in framework.jar.

package android.os;

public class PowerSaveState {
    public boolean batterySaverEnabled;
    public boolean globalBatterySaverEnabled;
    public int locationMode;
    public int soundTriggerMode;
    public float brightnessFactor;

    public PowerSaveState() {}
}
