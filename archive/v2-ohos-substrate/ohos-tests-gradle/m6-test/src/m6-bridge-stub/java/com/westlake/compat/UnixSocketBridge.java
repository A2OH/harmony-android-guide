// SPDX-License-Identifier: Apache-2.0
//
// UnixSocketBridge — COMPILE-TIME STUB. The authoritative source lives
// at shim/java/com/westlake/compat/UnixSocketBridge.java and is dexed
// into ohos-deploy/aosp-shim-ohos.dex (BCP). At runtime, dalvikvm
// resolves this class FROM the BCP, NOT from the app dex.
//
// We keep this stub copy in the test module's source tree purely so
// javac can resolve `import com.westlake.compat.UnixSocketBridge;` in
// M6DrmClient.java at build time. The driver script
// (scripts/run-ohos-test.sh m6-java-client) selects only app classes
// (com/westlake/ohostests/m6/**) when invoking d8, so this stub does
// NOT end up in the on-device app dex.
//
// IF YOU EDIT THIS FILE: also edit the authoritative source at
// shim/java/com/westlake/compat/UnixSocketBridge.java to match, and
// re-run scripts/build-shim-dex-ohos.sh. Method signatures must be
// identical to what libcore_bridge.cpp's gUnixSocketBridgeMethods
// registers, otherwise RegisterNatives silently skips them at VM
// init.

package com.westlake.compat;

public final class UnixSocketBridge {

    private UnixSocketBridge() {}

    public static native int memfdCreate(String name, int flags);
    public static native boolean ftruncateRaw(int fd, long size);
    public static native boolean writeAllToFd(int fd, byte[] data, long size);
    public static native void closeFd(int fd);
    public static native int socketUnixSeqpacket();
    public static native boolean connectUnix(int fd, String path);
    public static native int sendFrameWithFd(int sockFd, byte[] payload, int frameFd);
    public static native int recvBytes(int sockFd, byte[] buf);
}
