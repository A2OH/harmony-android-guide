// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 5: IDisplayEventConnection + 60 Hz vsync)

#include "WestlakeDisplayEventConnection.h"

#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>

#include <chrono>

namespace android {

// AOSP-11 IDisplayEventConnection descriptor — from
// `IMPLEMENT_META_INTERFACE(DisplayEventConnection, "android.gui.DisplayEventConnection")`
// in frameworks/native/libs/gui/IDisplayEventConnection.cpp.  Apps that wrap
// our binder via `interface_cast<IDisplayEventConnection>(...)` see this
// exact string from BpBinder::getInterfaceDescriptor().
static const String16 kIfaceDescriptor("android.gui.DisplayEventConnection");

// Phase-1 vsync cadence: 60 Hz == 1e9/60 ns per period.  Matches
// kCannedRefreshPeriodNs in WestlakeSurfaceComposer.cpp.
static constexpr int64_t kVsyncPeriodNs = 16'666'667LL;

// 4 KB matches BitTube's default; one event is 40 bytes so the socket buffer
// holds ~100 ticks (~1.6s at 60 Hz) before the kernel drops new packets.
static constexpr int kSockBufBytes = 4 * 1024;

// ---------------------------------------------------------------------------
// Lifecycle
// ---------------------------------------------------------------------------

WestlakeDisplayEventConnection::WestlakeDisplayEventConnection() {
    int sockets[2] = { -1, -1 };
    if (::socketpair(AF_UNIX, SOCK_SEQPACKET, 0, sockets) != 0) {
        fprintf(stderr,
                "[wlk-vsync] ctor: socketpair() FAILED errno=%d (%s); "
                "connection will be inert\n",
                errno, strerror(errno));
        return;
    }
    // Match BitTube::init buffer sizing + non-blocking flag.
    int rcv = kSockBufBytes, snd = kSockBufBytes;
    ::setsockopt(sockets[0], SOL_SOCKET, SO_RCVBUF, &rcv, sizeof(rcv));
    ::setsockopt(sockets[1], SOL_SOCKET, SO_SNDBUF, &snd, sizeof(snd));
    // Keep the return-channel small (we don't use it).
    ::setsockopt(sockets[0], SOL_SOCKET, SO_SNDBUF, &snd, sizeof(snd));
    ::setsockopt(sockets[1], SOL_SOCKET, SO_RCVBUF, &rcv, sizeof(rcv));
    ::fcntl(sockets[0], F_SETFL, O_NONBLOCK);
    ::fcntl(sockets[1], F_SETFL, O_NONBLOCK);
    mReceiveFd = sockets[0];   // dup'd to the app via STEAL_RECEIVE_CHANNEL
    mSendFd    = sockets[1];   // we write events here from vsyncLoop
    fprintf(stderr,
            "[wlk-vsync] ctor: SOCK_SEQPACKET pair { recv=%d send=%d } "
            "buf=%d each, both O_NONBLOCK\n",
            mReceiveFd, mSendFd, kSockBufBytes);
}

WestlakeDisplayEventConnection::~WestlakeDisplayEventConnection() {
    stop();
    fprintf(stderr,
            "[wlk-vsync] dtor: emitted=%u dropped=%u (recv=%d send=%d)\n",
            mFramesEmitted.load(), mFramesDropped.load(),
            mReceiveFd, mSendFd);
    if (mReceiveFd >= 0) ::close(mReceiveFd);
    if (mSendFd    >= 0) ::close(mSendFd);
}

const String16& WestlakeDisplayEventConnection::getInterfaceDescriptor() const {
    return kIfaceDescriptor;
}

void WestlakeDisplayEventConnection::start() {
    bool expected = false;
    if (!mRunning.compare_exchange_strong(expected, true)) {
        return;  // already running
    }
    if (mSendFd < 0) {
        // ctor failed.  Pretend we started so stop() is symmetric, but the
        // loop just exits.
        mRunning.store(false);
        fprintf(stderr, "[wlk-vsync] start: SKIP — no usable socketpair\n");
        return;
    }
    fprintf(stderr,
            "[wlk-vsync] start: spawning 60 Hz tick thread "
            "(period=%lld ns)\n",
            (long long)kVsyncPeriodNs);
    mThread = std::thread(&WestlakeDisplayEventConnection::vsyncLoop, this);
}

void WestlakeDisplayEventConnection::stop() {
    bool expected = true;
    if (!mRunning.compare_exchange_strong(expected, false)) {
        return;  // already stopped or never started
    }
    if (mThread.joinable()) {
        mThread.join();
    }
    fprintf(stderr,
            "[wlk-vsync] stop: joined; emitted=%u dropped=%u\n",
            mFramesEmitted.load(), mFramesDropped.load());
}

// ---------------------------------------------------------------------------
// Vsync loop — fires every 16.666667 ms while the app has subscribed.
// ---------------------------------------------------------------------------

void WestlakeDisplayEventConnection::vsyncLoop() {
    using namespace std::chrono;

    // sleep_until offers monotonic-clock anchoring that doesn't drift when a
    // tick takes a fraction of a millisecond; per-tick jitter stays bounded.
    auto period = nanoseconds(kVsyncPeriodNs);
    auto next   = steady_clock::now() + period;

    while (mRunning.load(std::memory_order_relaxed)) {
        std::this_thread::sleep_until(next);
        next += period;

        int32_t rate = mVsyncRate.load(std::memory_order_relaxed);
        bool oneShot = mPendingOneShot.exchange(false, std::memory_order_acq_rel);

        // Paused state: emit only if a REQUEST_NEXT_VSYNC was pending.
        if (rate == 0 && !oneShot) {
            continue;
        }

        // setVsyncRate(N>=1) means "emit every Nth tick".  We track this
        // with mTickCounter; the very first tick after rate transitions
        // 0→N always fires (counter starts at 0 so 0 % N == 0).
        if (rate > 0) {
            bool fire = (mTickCounter % static_cast<uint32_t>(rate)) == 0;
            ++mTickCounter;
            if (!fire && !oneShot) continue;
        }

        sendOneEvent();
    }
    fprintf(stderr, "[wlk-vsync] vsyncLoop: exiting (mRunning=false)\n");
}

void WestlakeDisplayEventConnection::sendOneEvent() {
    // CLOCK_MONOTONIC nanoseconds — matches AOSP `systemTime(SYSTEM_TIME_MONOTONIC)`.
    struct timespec ts {};
    ::clock_gettime(CLOCK_MONOTONIC, &ts);
    const int64_t nowNs =
            static_cast<int64_t>(ts.tv_sec) * 1'000'000'000LL +
            static_cast<int64_t>(ts.tv_nsec);

    WireEvent ev;
    memset(&ev, 0, sizeof(ev));
    ev.headerType         = kVsyncFourcc;
    ev.headerPad          = 0;
    ev.headerDisplayId    = 0;
    ev.headerTimestampNs  = nowNs;
    ev.vsyncCount         = ++mEventCounter;
    ev.vsyncPad           = 0;
    ev.vsyncExpectedTs    = nowNs;

    // SOCK_SEQPACKET: each ::send writes one atomic message.  Non-blocking
    // (MSG_DONTWAIT) + MSG_NOSIGNAL so an app that crashes / closes its
    // receive end doesn't SIGPIPE us.
    ssize_t n = ::send(mSendFd, &ev, sizeof(ev), MSG_DONTWAIT | MSG_NOSIGNAL);
    if (n < 0) {
        int e = errno;
        if (e == EAGAIN || e == EWOULDBLOCK) {
            // App hasn't consumed; the kernel buffer is full.  Phase-1
            // policy: drop the frame (matches AOSP-11 DispEventThread's
            // "skip on EAGAIN" — see EventThread::dispatchEvent).
            mFramesDropped.fetch_add(1, std::memory_order_relaxed);
            return;
        }
        if (e == EPIPE || e == ECONNRESET || e == ENOTCONN || e == EBADF) {
            // App has closed its receive end.  Mark paused so we stop
            // calling send() until the app reattaches (which it won't —
            // BpDisplayEventConnection drops with the binder).  This
            // prevents log spam.
            fprintf(stderr,
                    "[wlk-vsync] send: peer closed errno=%d; pausing emission\n",
                    e);
            mVsyncRate.store(0, std::memory_order_relaxed);
            mFramesDropped.fetch_add(1, std::memory_order_relaxed);
            return;
        }
        fprintf(stderr,
                "[wlk-vsync] send: unexpected errno=%d (%s); dropping frame\n",
                e, strerror(e));
        mFramesDropped.fetch_add(1, std::memory_order_relaxed);
        return;
    }
    if (n != static_cast<ssize_t>(sizeof(ev))) {
        // Should never happen because SOCK_SEQPACKET is atomic, but be loud
        // if it does.
        fprintf(stderr,
                "[wlk-vsync] send: partial write %zd/%zu (impossible for "
                "SEQPACKET)\n",
                n, sizeof(ev));
        mFramesDropped.fetch_add(1, std::memory_order_relaxed);
        return;
    }
    mFramesEmitted.fetch_add(1, std::memory_order_relaxed);
}

// ---------------------------------------------------------------------------
// Transaction dispatch — mirrors BnDisplayEventConnection::onTransact in
// frameworks/native/libs/gui/IDisplayEventConnection.cpp:64.
// ---------------------------------------------------------------------------

status_t WestlakeDisplayEventConnection::onTransact(uint32_t code,
                                                    const Parcel& data,
                                                    Parcel* reply,
                                                    uint32_t flags) {
    // The AOSP `SafeInterface` framework writes the interface token before
    // each call.  Validate it via Parcel::enforceInterface (the public API
    // for this in our libbinder port — see Parcel.h:148).
    if (!data.enforceInterface(kIfaceDescriptor)) {
        fprintf(stderr,
                "[wlk-vsync] onTransact code=%u: bad interface token; "
                "returning PERMISSION_DENIED\n",
                code);
        return PERMISSION_DENIED;
    }

    switch (code) {
        case STEAL_RECEIVE_CHANNEL: {
            // BitTube::writeToParcel(reply):
            //   reply->writeDupFileDescriptor(mReceiveFd);
            //   mReceiveFd.reset();   // we keep ours; the BitTube on our
            //                         // side is implicit
            //   reply->writeDupFileDescriptor(mSendFd);
            //   mSendFd.reset();      // ditto
            //
            // Note: on the daemon side we keep BOTH fds open.  The AOSP
            // reference also keeps them in EventThread::Connection — see
            // services/surfaceflinger/Scheduler/EventThread.cpp; the BitTube
            // we parcel is a separate object on the SF side.  Effectively
            // we're emulating that pattern.
            (void)data;
            status_t r1 = reply->writeDupFileDescriptor(mReceiveFd);
            if (r1 != NO_ERROR) {
                fprintf(stderr,
                        "[wlk-vsync] STEAL_RECEIVE_CHANNEL: dup(recv=%d) -> %d\n",
                        mReceiveFd, r1);
                // BpDisplayEventConnection has no graceful path here; return
                // the error so the caller's SafeInterface marshal reflects it.
                return r1;
            }
            status_t r2 = reply->writeDupFileDescriptor(mSendFd);
            if (r2 != NO_ERROR) {
                fprintf(stderr,
                        "[wlk-vsync] STEAL_RECEIVE_CHANNEL: dup(send=%d) -> %d\n",
                        mSendFd, r2);
                return r2;
            }
            fprintf(stderr,
                    "[wlk-vsync] STEAL_RECEIVE_CHANNEL: parceled recv=%d send=%d\n",
                    mReceiveFd, mSendFd);
            // SafeInterface convention: status returned as the function's
            // return value, written to the reply by the binder transport
            // wrapper.  We've already written the payload above; return
            // NO_ERROR here so the wrapper writes 0 status.
            return NO_ERROR;
        }
        case SET_VSYNC_RATE: {
            int32_t count = data.readInt32();
            if (count < 0) {
                fprintf(stderr,
                        "[wlk-vsync] SET_VSYNC_RATE: negative %d -> BAD_VALUE\n",
                        count);
                return BAD_VALUE;
            }
            int32_t prev = mVsyncRate.exchange(count, std::memory_order_relaxed);
            // Reset the rate-throttle counter so the new rate takes effect
            // immediately rather than on the next mTickCounter % rate == 0
            // boundary.
            if (prev != count) {
                mTickCounter = 0;
            }
            fprintf(stderr,
                    "[wlk-vsync] SET_VSYNC_RATE: %d -> %d\n", prev, count);
            return NO_ERROR;
        }
        case REQUEST_NEXT_VSYNC: {
            // AOSP marshals this as an async one-way call (callRemoteAsync
            // in BpDisplayEventConnection); we just set a flag and the loop
            // emits one event next iteration.  No reply needed.
            mPendingOneShot.store(true, std::memory_order_relaxed);
            fprintf(stderr,
                    "[wlk-vsync] REQUEST_NEXT_VSYNC: pendingOneShot=true\n");
            return NO_ERROR;
        }
        default:
            fprintf(stderr,
                    "[wlk-vsync] onTransact code=%u flags=0x%x unknown; ack\n",
                    code, flags);
            return NO_ERROR;
    }
}

}  // namespace android
