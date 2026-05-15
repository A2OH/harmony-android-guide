// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 5: IDisplayEventConnection + 60 Hz vsync)
//
// WestlakeDisplayEventConnection is the BBinder peer of AOSP-11's
// BpDisplayEventConnection (see frameworks/native/libs/gui/IDisplayEventConnection.cpp
// — descriptor "android.gui.DisplayEventConnection").  It is handed back by
// WestlakeSurfaceComposer::onCreateDisplayEventConnection (CREATE_DISPLAY_EVENT_CONNECTION
// transaction code 4).  Each instance owns a BitTube — a socketpair(AF_UNIX,
// SOCK_SEQPACKET) — and a daemon thread that emits a fixed-cadence Vsync
// event every 16.666667 ms while mVsyncRate > 0.
//
// Wire shapes preserved verbatim from AOSP-11:
//
//   - Transaction codes (IDisplayEventConnection.cpp::Tag, anon-namespaced):
//        STEAL_RECEIVE_CHANNEL = IBinder::FIRST_CALL_TRANSACTION = 1
//        SET_VSYNC_RATE         = 2
//        REQUEST_NEXT_VSYNC     = 3
//
//   - STEAL_RECEIVE_CHANNEL reply: a BitTube parceled via
//        BitTube::writeToParcel(reply) ==
//          reply->writeDupFileDescriptor(mReceiveFd);  // first
//          reply->writeDupFileDescriptor(mSendFd);     // second
//     The remote BpDisplayEventConnection's callRemote(STEAL_RECEIVE_CHANNEL,
//     outChannel) constructs a BitTube on the reply, which dups both fds.
//
//   - Vsync event struct (DisplayEventReceiver::Event, exact AOSP-11 layout):
//        struct Header {
//            uint32_t type;            // 'vsyn' = 0x7673796E
//            uint64_t displayId;       // PhysicalDisplayId == uint64_t
//            int64_t  timestamp        // nsecs_t, __attribute__((aligned(8)))
//        };
//        // union { VSync vsync; Hotplug hotplug; Config config; }
//        struct VSync {
//            uint32_t count;
//            int64_t  expectedVSyncTimestamp;  // 8-byte aligned implicitly
//        };
//     With aarch64 / armv7 LP{32,64} alignment the struct layout is:
//        Header:  4 (type) + 4 (pad) + 8 (displayId) + 8 (timestamp) = 24
//        Union:   4 (count) + 4 (pad) + 8 (expected) = 16
//        Total:   40 bytes
//     We define a packed wire struct that exactly mirrors this byte layout
//     so the AOSP DisplayEventReceiver on the app side parses it correctly.
//
// Phase-1 simplification:
//   - Only one event type (Vsync); hotplug + config-change deferred.
//   - Single physical display id 0.
//   - REQUEST_NEXT_VSYNC: while mVsyncRate > 0 the loop is already emitting
//     at 60 Hz, so REQUEST_NEXT_VSYNC is functionally a no-op.  When
//     mVsyncRate == 0 we set a flag that emits exactly one event on the
//     next loop iteration (then resets back to paused).  This matches the
//     Choreographer "tap-for-next-frame" semantics.
//
// Anti-drift: zero per-app branches.  Every CREATE_DISPLAY_EVENT_CONNECTION
// returns the same flavor of connection (60 Hz tick from a daemon thread).
//
// Companion: docs/engine/M6_SURFACE_DAEMON_PLAN.md §4 (vsync mention)
//             docs/engine/M6_STEP4_REPORT.md (predecessor — frame flow)
//             docs/engine/M6_STEP5_REPORT.md (this CR)

#ifndef WESTLAKE_DISPLAY_EVENT_CONNECTION_H
#define WESTLAKE_DISPLAY_EVENT_CONNECTION_H

#include <atomic>
#include <thread>

#include <binder/Binder.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/String16.h>

namespace android {

class WestlakeDisplayEventConnection : public BBinder {
public:
    WestlakeDisplayEventConnection();
    ~WestlakeDisplayEventConnection() override;

    // BBinder override.
    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    // Mirror of AOSP-11's IDisplayEventConnection::getInterfaceDescriptor()
    // (defined via IMPLEMENT_META_INTERFACE(DisplayEventConnection, ...)).
    // Peers using BpBinder::getInterfaceDescriptor() see the canonical
    // descriptor: "android.gui.DisplayEventConnection".
    const String16& getInterfaceDescriptor() const override;

    // Spawn the 60 Hz vsync thread.  Idempotent.
    void start();

    // Stop + join the vsync thread.  Idempotent.
    void stop();

    // Diagnostic accessors — used by surface_smoke check H to verify the
    // connection went through the expected lifecycle.
    int      receiveFd()         const { return mReceiveFd; }
    int      sendFd()            const { return mSendFd; }
    uint32_t framesEmitted()     const { return mFramesEmitted.load(); }
    uint32_t framesDropped()     const { return mFramesDropped.load(); }
    int32_t  vsyncRate()         const { return mVsyncRate.load(); }

    // AOSP-11 IDisplayEventConnection transaction codes (verbatim from
    // frameworks/native/libs/gui/IDisplayEventConnection.cpp::Tag).
    //
    // FIRST_CALL_TRANSACTION == 1.
    enum Tag : uint32_t {
        STEAL_RECEIVE_CHANNEL = 1,
        SET_VSYNC_RATE        = 2,
        REQUEST_NEXT_VSYNC    = 3,
    };

    // AOSP-11 DisplayEventReceiver::DISPLAY_EVENT_VSYNC fourcc 'vsyn' (big-
    // endian-by-character).  Defined as `fourcc('v','s','y','n')` which
    // packs c1 to MSB: 0x76737970.  We replicate the macro here for symmetry
    // with surface_smoke's check H reader.
    static constexpr uint32_t kVsyncFourcc =
        (static_cast<uint32_t>('v') << 24) |
        (static_cast<uint32_t>('s') << 16) |
        (static_cast<uint32_t>('y') <<  8) |
        (static_cast<uint32_t>('n'));

    // Wire-format event struct — must match
    // DisplayEventReceiver::Event byte-for-byte on the target ABI (aarch64
    // LP64, armv7 LP32 both produce the same layout for this struct because
    // every field's natural alignment is satisfied with the same paddings).
    //
    // We do NOT use AOSP's union here — Phase-1 only emits Vsync, so the
    // remaining 16 bytes are always the VSync sub-struct.  An app's
    // DisplayEventReceiver::Event union reads VSync correctly because its
    // Header.type field tells it which arm of the union is live.
    struct __attribute__((packed)) WireEvent {
        // Header
        uint32_t headerType;            // = kVsyncFourcc
        uint32_t headerPad;             // = 0 (alignment for displayId)
        uint64_t headerDisplayId;       // = 0 (Phase-1 single display)
        int64_t  headerTimestampNs;     // = CLOCK_MONOTONIC ns at emit time
        // VSync arm of the union
        uint32_t vsyncCount;            // monotonic frame counter
        uint32_t vsyncPad;              // = 0 (alignment for expected ts)
        int64_t  vsyncExpectedTs;       // = headerTimestampNs (Phase-1)
    };
    static_assert(sizeof(WireEvent) == 40,
                  "WireEvent must be 40 bytes to match AOSP-11 "
                  "DisplayEventReceiver::Event layout");

private:
    void vsyncLoop();
    void sendOneEvent();  // build + write a single WireEvent.

    // BitTube semantics — see frameworks/native/libs/gui/BitTube.cpp:
    // SOCK_SEQPACKET pair, both ends non-blocking, default 4 KB rcvbuf/sndbuf.
    // mReceiveFd is parceled to the app side via STEAL_RECEIVE_CHANNEL;
    // mSendFd is parceled too (matches BitTube::writeToParcel which dups
    // both).  After STEAL_RECEIVE_CHANNEL the daemon still holds its own
    // mSendFd and writes events to it.
    int mReceiveFd = -1;
    int mSendFd    = -1;

    std::thread          mThread;
    std::atomic<bool>    mRunning{false};
    std::atomic<int32_t> mVsyncRate{0};        // 0=paused, 1=every tick, 2=every other, ...
    std::atomic<bool>    mPendingOneShot{false};  // REQUEST_NEXT_VSYNC fires once
    std::atomic<uint32_t> mFramesEmitted{0};
    std::atomic<uint32_t> mFramesDropped{0};
    uint32_t             mTickCounter = 0;     // gates mVsyncRate=N throttling
    uint32_t             mEventCounter = 0;    // monotonic vsyncCount
};

}  // namespace android

#endif  // WESTLAKE_DISPLAY_EVENT_CONNECTION_H
