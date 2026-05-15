// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 4): consumer-side DLST pipe writer.
//
// One `DlstConsumer` runs alongside each `WestlakeGraphicBufferProducer`
// instance.  It polls the producer's slot ring for `QUEUED` slots, mmaps
// the slot's memfd, and emits a DLST-format frame to a configurable pipe
// path.  The Compose host's `WestlakeVM.kt` pipe reader (see
// `M6_SURFACE_DAEMON_PLAN.md §4.2 Phase 1 — DLST pipe backend`) consumes
// these frames and blits them to a SurfaceView.
//
// Frame envelope (matches `westlake-host-gradle/.../WestlakeVM.kt`
// constants `DLIST_MAGIC = 0x444C5354` and `OP_ARGB_BITMAP = 12`,
// little-endian throughout):
//
//   uint32  magic   = 0x444C5354 ("DLST")
//   uint32  size    = length of payload that follows
//   byte    opcode  = OP_ARGB_BITMAP (=12)        ┐
//   float   x       = 0.0                          │
//   float   y       = 0.0                          │
//   int32   width                                  ├ size bytes total
//   int32   height                                 │
//   int32   dataLen = width * height * 4           │
//   bytes[] rgba    — raw memfd pixels             ┘
//
// Lifecycle (per producer):
//   1. surfaceflinger_main starts the libbinder thread-pool first; CREATE_SURFACE
//      constructs a WestlakeGraphicBufferProducer + a DlstConsumer pointing
//      at it.
//   2. The consumer's `start()` spawns a single std::thread that loops on
//      `WestlakeGraphicBufferProducer::takeQueuedSlot()`.
//   3. When the producer's onTransact(QUEUE_BUFFER) signals, takeQueuedSlot()
//      returns the slot index; the consumer mmaps + writes a DLST frame +
//      calls releaseSlot() (marks slot FREE).
//   4. `stop()` calls the producer's `wake()` then joins the thread.  The
//      producer's destructor also calls `wake()` for safety.
//
// Pipe path resolution:
//   - Constructor accepts an explicit path (used by tests).
//   - Daemon's main() reads $WESTLAKE_DLST_PIPE; defaults to
//     `/data/local/tmp/westlake/dlst.fifo`.  The path must exist as a FIFO
//     (mkfifo) created by the launcher script before the daemon connects a
//     producer to it.  If `open(path, O_WRONLY | O_NONBLOCK)` returns
//     `ENXIO` (no reader), the consumer silently drops the frame and
//     retries on the next QUEUE — this matches the host's
//     "VM-may-not-be-attached-yet" semantics described in
//     `M6_SURFACE_DAEMON_PLAN.md §4.1` (DlstPipeBackend probe()/onQueued()).
//
// Anti-drift: no per-app branches.  Every WestlakeGraphicBufferProducer that
// gets paired with a DlstConsumer streams pixels through exactly the same
// envelope.  The pipe path is global (one host SurfaceView per daemon
// instance — Phase 1's single-foreground-app assumption).

#ifndef WESTLAKE_DLST_CONSUMER_H
#define WESTLAKE_DLST_CONSUMER_H

#include <atomic>
#include <string>
#include <thread>

#include <utils/RefBase.h>

namespace android {

class WestlakeGraphicBufferProducer;

class DlstConsumer {
public:
    // Caller retains ownership of the GBP (passes a wp/raw pointer).
    // The GBP must outlive the DlstConsumer.  In practice, since the
    // DlstConsumer is owned by the WestlakeSurfaceComposerClient (or its
    // surface entry) and holds a strong sp<> back to the GBP, lifetime is
    // anchored by the surface-creation transaction.
    DlstConsumer(const sp<WestlakeGraphicBufferProducer>& gbp,
                 std::string pipePath);
    ~DlstConsumer();

    // Spawn the consumer thread.  Idempotent.
    void start();

    // Signal shutdown.  Calls gbp->wake() then joins the thread.  Idempotent.
    void stop();

    // Stats (consulted by surface_smoke check G).
    uint64_t framesWritten() const { return mFramesWritten.load(); }
    uint64_t framesDropped() const { return mFramesDropped.load(); }
    uint64_t bytesWritten()  const { return mBytesWritten.load();  }

private:
    void run();

    // Emit one DLST frame for `slotIdx`.  Returns NO_ERROR on success or a
    // negative errno on failure.  Always non-blocking on the pipe side.
    int writeFrame(int slotIdx);

    sp<WestlakeGraphicBufferProducer> mGbp;
    std::string mPipePath;

    std::thread mThread;
    std::atomic<bool> mRunning{false};
    std::atomic<bool> mStopRequested{false};

    std::atomic<uint64_t> mFramesWritten{0};
    std::atomic<uint64_t> mFramesDropped{0};
    std::atomic<uint64_t> mBytesWritten{0};
};

}  // namespace android

#endif  // WESTLAKE_DLST_CONSUMER_H
