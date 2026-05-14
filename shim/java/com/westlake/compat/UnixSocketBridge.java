// SPDX-License-Identifier: Apache-2.0
//
// UnixSocketBridge — generic AF_UNIX + memfd + SCM_RIGHTS surfaces
// for dalvikvm clients on OHOS (PF-ohos-m6-002).
//
// Phase 2 M6-OHOS-Step2: the M6 daemon (DRM/KMS owner, sibling of
// aosp-surface-daemon-port on Android) accepts memfd handoffs via
// AF_UNIX SOCK_SEQPACKET + SCM_RIGHTS at
// /data/local/tmp/westlake/m6-drm.sock. To submit frames from a
// Java client, dalvikvm needs POSIX surfaces that
// libcore.io.Posix does NOT expose: memfd_create (Linux-specific,
// not on AOSP API 26 Posix surface), AF_UNIX socket family
// (libcore.io.Posix.socket takes int family but its connect()
// takes InetAddress, no UNIX path overload), sendmsg with
// SCM_RIGHTS ancillary data (only the IP/sendto variants exist).
//
// These are generic POSIX surfaces — every Linux-targeting JVM has
// equivalents in some form. We expose them on a class WE own to
// honor the macro-shim contract:
//
//   - No setAccessible / Unsafe.
//   - No per-app branches.
//   - No new methods on framework.jar internal classes
//     (WestlakeContextImpl, ResourcesImpl, etc.).
//   - All methods live on UnixSocketBridge (this class, in our shim).
//
// JNI registration: dvmRegisterLibcoreBridge in
// dalvik-port/compat/libcore_bridge.cpp calls registerClass on this
// class at VM init.
//
// FD handling: we return raw int fds (not java.io.FileDescriptor)
// because (a) the only callers are our own M6DrmClient, (b) the
// existing libcore_bridge already wraps int->FileDescriptor for the
// Posix surfaces that the BCP requires it on, (c) it sidesteps
// any FileDescriptor.<init> visibility differences across AOSP API
// levels. The caller (M6DrmClient) is responsible for closeFd().
//
// Error reporting: methods return -errno (negative) on failure for
// fd-returning calls, false for boolean calls, -1 for byte-returning
// calls. We do NOT throw — Java callers handle return-code checking
// explicitly, which keeps the JNI surface ABI-stable and avoids
// having to construct ErrnoException (which is in libcore.io. on
// some AOSP versions and android.system.ErrnoException on others;
// the BCP class tree differs from real Android here).

package com.westlake.compat;

public final class UnixSocketBridge {

    private UnixSocketBridge() {}

    // ---------- memfd / ftruncate / mmap-copy --------------------------------

    /**
     * Create an anonymous file in memory ({@code memfd_create(2)}).
     * Returns a positive fd on success, or {@code -errno} on failure.
     * On aarch64 OHOS this maps to syscall #279.
     */
    public static native int memfdCreate(String name, int flags);

    /** Truncate {@code fd} to exactly {@code size} bytes. */
    public static native boolean ftruncateRaw(int fd, long size);

    /**
     * Mmap {@code fd} as PROT_READ|PROT_WRITE / MAP_SHARED, memcpy
     * {@code data[0..size]} into it, munmap. Returns true on success.
     * Convenience for the common case of "fill memfd with byte[]".
     */
    public static native boolean writeAllToFd(int fd, byte[] data, long size);

    /** Bare {@code close(fd)}. No-op if fd is negative. */
    public static native void closeFd(int fd);

    // ---------- AF_UNIX socket -----------------------------------------------

    /**
     * {@code socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0)}.
     * Returns fd or {@code -errno}.
     */
    public static native int socketUnixSeqpacket();

    /**
     * {@code connect(fd, &sockaddr_un{path})}. If {@code path} starts with
     * {@code '@'}, uses Linux abstract namespace (leading NUL, no fs entry).
     * Returns true on success.
     */
    public static native boolean connectUnix(int fd, String path);

    /**
     * {@code sendmsg(fd, ...)} with iovec={@code payload} and one ancillary
     * SCM_RIGHTS cmsg conveying {@code frameFd} to the daemon. Returns the
     * number of payload bytes sent, or {@code -errno} on failure.
     */
    public static native int sendFrameWithFd(int sockFd, byte[] payload, int frameFd);

    /**
     * Blocking {@code recv(fd, buf, sizeof(buf), 0)}. Returns bytes received
     * (>=0) or {@code -errno}. EOF is reported as 0.
     */
    public static native int recvBytes(int sockFd, byte[] buf);
}
