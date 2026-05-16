// SPDX-License-Identifier: Apache-2.0
//
// M6DrmClient — Java-side frame submitter for the M6 DRM daemon
// (PF-ohos-m6-002).
//
// Pipeline (matches the daemon's --test-client mode in C):
//
//   1. socket(AF_UNIX, SOCK_SEQPACKET).
//   2. connect to /data/local/tmp/westlake/m6-drm.sock (filesystem),
//      or fall back to abstract namespace @m6-drm.sock if that fails
//      (SELinux/path constraints; daemon binds both).
//   3. For each frame:
//        a. memfd_create("m6frame", MFD_CLOEXEC).
//        b. ftruncate(fd, width*height*4).
//        c. mmap+memcpy BGRA bytes into fd, then munmap.
//        d. sendmsg() iov={12-byte header}, cmsg=SCM_RIGHTS(fd).
//        e. recv() 12 bytes; verify magic='M6AK', status==0.
//        f. close(fd).
//
// Wire (little-endian, must match m6_drm_daemon.c):
//   client→daemon (12 bytes + 1 ancillary fd):
//     uint32 magic = 'M6FR' = 0x4D364652
//     uint32 frame_seq
//     uint32 size_bytes (= width*height*4)
//   daemon→client (12 bytes):
//     uint32 magic = 'M6AK' = 0x4D36414B
//     uint32 frame_seq
//     uint32 status (0=OK)
//
// Contract:
//   - All methods live on classes WE own (this class + UnixSocketBridge).
//   - No setAccessible. No Unsafe. No per-app branches.
//   - All failure paths log to System.out and return false / -1.

package com.westlake.ohostests.m6;

import com.westlake.compat.UnixSocketBridge;

public final class M6DrmClient {

    public static final String DEFAULT_SOCKET_PATH =
            "/data/local/tmp/westlake/m6-drm.sock";
    public static final String ABSTRACT_SOCKET_PATH = "@m6-drm.sock";

    private static final int MAGIC_M6FR = 0x4D364652;
    private static final int MAGIC_M6AK = 0x4D36414B;
    private static final int WIRE_SIZE  = 12;
    /** {@code memfd_create} flag (kernel value). */
    private static final int MFD_CLOEXEC = 0x0001;

    private int sockFd = -1;
    private String connectedPath = null;

    /**
     * Connect to the M6 daemon. Tries {@code path} first, then the
     * abstract-namespace fallback if {@code path} is the filesystem
     * variant. Returns true on success.
     */
    public boolean connect(String path) {
        if (sockFd >= 0) {
            System.out.println("[M6DrmClient] already connected fd=" + sockFd);
            return true;
        }
        int fd = UnixSocketBridge.socketUnixSeqpacket();
        if (fd < 0) {
            System.out.println("[M6DrmClient] socket() failed errno=" + (-fd));
            return false;
        }
        boolean ok = UnixSocketBridge.connectUnix(fd, path);
        if (!ok && path != null && !path.isEmpty() && path.charAt(0) != '@') {
            System.out.println("[M6DrmClient] connect(" + path
                    + ") failed; trying abstract " + ABSTRACT_SOCKET_PATH);
            UnixSocketBridge.closeFd(fd);
            fd = UnixSocketBridge.socketUnixSeqpacket();
            if (fd < 0) {
                System.out.println("[M6DrmClient] second socket() failed errno=" + (-fd));
                return false;
            }
            ok = UnixSocketBridge.connectUnix(fd, ABSTRACT_SOCKET_PATH);
            if (ok) path = ABSTRACT_SOCKET_PATH;
        }
        if (!ok) {
            System.out.println("[M6DrmClient] connect failed (both filesystem and abstract)");
            UnixSocketBridge.closeFd(fd);
            return false;
        }
        sockFd = fd;
        connectedPath = path;
        System.out.println("[M6DrmClient] connected fd=" + fd + " path=" + path);
        return true;
    }

    public String connectedSocketPath() { return connectedPath; }
    public boolean isConnected() { return sockFd >= 0; }

    /**
     * Submit one frame. {@code bgra} is row-major BGRA8888, length
     * {@code width*height*4}. {@code seq} is an opaque echo token —
     * we verify the ACK echoes it back.
     *
     * Returns true on PASS (ACK with status==0 and matching seq).
     */
    public boolean submit(byte[] bgra, int seq) {
        if (sockFd < 0) {
            System.out.println("[M6DrmClient] submit: not connected");
            return false;
        }
        if (bgra == null) {
            System.out.println("[M6DrmClient] submit: null bgra");
            return false;
        }
        int memfd = UnixSocketBridge.memfdCreate("m6frame", MFD_CLOEXEC);
        if (memfd < 0) {
            System.out.println("[M6DrmClient] memfd_create failed errno=" + (-memfd));
            return false;
        }
        try {
            long size = bgra.length;
            if (!UnixSocketBridge.ftruncateRaw(memfd, size)) {
                System.out.println("[M6DrmClient] ftruncate(" + size + ") failed");
                return false;
            }
            if (!UnixSocketBridge.writeAllToFd(memfd, bgra, size)) {
                System.out.println("[M6DrmClient] writeAllToFd(size=" + size + ") failed");
                return false;
            }
            byte[] payload = new byte[WIRE_SIZE];
            writeUint32LE(payload, 0, MAGIC_M6FR);
            writeUint32LE(payload, 4, seq);
            writeUint32LE(payload, 8, (int) size);
            int sent = UnixSocketBridge.sendFrameWithFd(sockFd, payload, memfd);
            if (sent < 0) {
                System.out.println("[M6DrmClient] sendmsg failed errno=" + (-sent));
                return false;
            }
            if (sent != WIRE_SIZE) {
                System.out.println("[M6DrmClient] short send: " + sent + "/" + WIRE_SIZE);
                return false;
            }
            byte[] ack = new byte[WIRE_SIZE];
            int n = UnixSocketBridge.recvBytes(sockFd, ack);
            if (n < 0) {
                System.out.println("[M6DrmClient] recv failed errno=" + (-n));
                return false;
            }
            if (n != WIRE_SIZE) {
                System.out.println("[M6DrmClient] short recv: " + n + "/" + WIRE_SIZE);
                return false;
            }
            int magic = readUint32LE(ack, 0);
            int echoSeq = readUint32LE(ack, 4);
            int status = readUint32LE(ack, 8);
            if (magic != MAGIC_M6AK) {
                System.out.println("[M6DrmClient] bad ack magic 0x"
                        + Integer.toHexString(magic)
                        + " (want 0x" + Integer.toHexString(MAGIC_M6AK) + ")");
                return false;
            }
            if (echoSeq != seq) {
                System.out.println("[M6DrmClient] ack seq mismatch: got=" + echoSeq
                        + " want=" + seq);
                // not fatal — daemon may have pipelined; treat as warn
            }
            if (status != 0) {
                System.out.println("[M6DrmClient] daemon returned status=" + status
                        + " for seq=" + seq);
                return false;
            }
            return true;
        } finally {
            UnixSocketBridge.closeFd(memfd);
        }
    }

    public void disconnect() {
        if (sockFd >= 0) {
            UnixSocketBridge.closeFd(sockFd);
            System.out.println("[M6DrmClient] disconnected fd=" + sockFd);
            sockFd = -1;
            connectedPath = null;
        }
    }

    // ---- little-endian helpers --------------------------------------------

    private static void writeUint32LE(byte[] b, int off, int v) {
        b[off    ] = (byte)  (v        & 0xFF);
        b[off + 1] = (byte) ((v >>> 8) & 0xFF);
        b[off + 2] = (byte) ((v >>> 16) & 0xFF);
        b[off + 3] = (byte) ((v >>> 24) & 0xFF);
    }

    private static int readUint32LE(byte[] b, int off) {
        return  ( b[off    ] & 0xFF)
              | ((b[off + 1] & 0xFF) << 8)
              | ((b[off + 2] & 0xFF) << 16)
              | ((b[off + 3] & 0xFF) << 24);
    }
}
