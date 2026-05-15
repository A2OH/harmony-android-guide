// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 Step 2/3 surface-transaction smoke test.
//
// Sandbox protocol (mirrors M2 sm_smoke pattern, run on cfb7c9e3):
//   1. caller (m6step3-smoke.sh) has already done:
//        setprop ctl.stop vndservicemanager
//        ./servicemanager /dev/vndbinder &
//        ./surfaceflinger /dev/vndbinder &   (registers as "SurfaceFlinger")
//   2. this test:
//        opens /dev/vndbinder, asks the SM for "SurfaceFlinger", sends real
//        ISurfaceComposer transactions, parses the replies.
//   3. exits 0 on PASS, 1 on FAIL.
//
// Acceptance checks:
//   A. checkService("SurfaceFlinger") returns a non-null remote BpBinder.
//   B. GET_PHYSICAL_DISPLAY_IDS replies with a uint64 vector of length 1
//      whose single element is 0 (the Phase-1 canned physical display).
//   C. GET_PHYSICAL_DISPLAY_TOKEN(0) returns a non-null strong binder.
//   D. GET_DISPLAY_STATS(token) returns NO_ERROR + the canned vsync period
//      (16,666,667 ns).
//   E. CREATE_CONNECTION returns a non-null strong binder (the
//      ISurfaceComposerClient stub).
//   F. (Step 3) CREATE_SURFACE returns non-null handle + non-null
//      IGraphicBufferProducer; on that GBP, DEQUEUE_BUFFER returns a slot in
//      [0,2); REQUEST_BUFFER(slot) returns a non-null GraphicBuffer
//      flattenable with the expected magic + dimensions + 1 memfd fd;
//      QUEUE_BUFFER returns NO_ERROR + flattened QueueBufferOutput.  A
//      second DEQUEUE → REQUEST → QUEUE cycles to the other slot.
//
// Each check that fails is logged but the test continues so the operator sees
// the full failure surface in one run.  Final exit code is 0 iff all checks
// pass.

#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <unistd.h>

#include <atomic>
#include <chrono>
#include <thread>
#include <vector>

#include <binder/Binder.h>
#include <binder/BpBinder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <binder/ProcessState.h>
#include <utils/String8.h>
#include <utils/String16.h>

using namespace android;

// AOSP-11 transaction codes — must match WestlakeSurfaceComposer.h::Tag.
namespace ifc {
    constexpr uint32_t CREATE_CONNECTION                = 2;
    constexpr uint32_t CREATE_DISPLAY_EVENT_CONNECTION  = 4;
    constexpr uint32_t GET_PHYSICAL_DISPLAY_TOKEN       = 7;
    constexpr uint32_t GET_DISPLAY_STATS                = 19;
    constexpr uint32_t GET_PHYSICAL_DISPLAY_IDS         = 35;
}  // namespace ifc

// AOSP-11 IDisplayEventConnection tags — see
// frameworks/native/libs/gui/IDisplayEventConnection.cpp::Tag and
// WestlakeDisplayEventConnection.h::Tag.
namespace idec {
    constexpr uint32_t STEAL_RECEIVE_CHANNEL = 1;
    constexpr uint32_t SET_VSYNC_RATE        = 2;
    [[maybe_unused]] constexpr uint32_t REQUEST_NEXT_VSYNC = 3;
}  // namespace idec

// ISurfaceComposerClient SafeInterface tag — see
// WestlakeSurfaceComposerClient.h::Tag (CREATE_SURFACE = 1).
namespace ifcc {
    constexpr uint32_t CREATE_SURFACE = 1;
}  // namespace ifcc

// AOSP-11 IGraphicBufferProducer tags — see
// WestlakeGraphicBufferProducer.h::Tag.  Only the codes the smoke driver
// exercises are declared here; the daemon-side enum covers the rest.
namespace igbp {
    constexpr uint32_t REQUEST_BUFFER = 1;
    constexpr uint32_t DEQUEUE_BUFFER = 2;
    constexpr uint32_t QUEUE_BUFFER   = 6;
}  // namespace igbp

static const String16 kSfDescriptor("android.ui.ISurfaceComposer");
static const String16 kSfService("SurfaceFlinger");
static const String16 kSfClientDescriptor("android.ui.ISurfaceComposerClient");
static const String16 kIgbpDescriptor("android.gui.IGraphicBufferProducer");
static const String16 kIdecDescriptor("android.gui.DisplayEventConnection");

static const char* dev_for_run() {
    const char* d = getenv("BINDER_DEVICE");
    return (d && *d) ? d : "/dev/vndbinder";
}

// Each helper returns 0 on PASS, 1 on FAIL; logs progress on stderr.
static int check_a_find_service(sp<IBinder>* outBinder) {
    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[surface_smoke] A: FAIL defaultServiceManager() null\n");
        return 1;
    }
    sp<IBinder> proxy = sm->checkService(kSfService);
    if (proxy == nullptr) {
        fprintf(stderr, "[surface_smoke] A: FAIL checkService(\"SurfaceFlinger\") -> null\n");
        return 1;
    }
    BpBinder* bp = proxy->remoteBinder();
    if (bp == nullptr) {
        fprintf(stderr, "[surface_smoke] A: FAIL got local binder, expected remote BpBinder\n");
        return 1;
    }
    fprintf(stderr, "[surface_smoke] A: PASS SurfaceFlinger remote BpBinder=%p\n", bp);
    *outBinder = proxy;
    return 0;
}

static int check_b_physical_ids(const sp<IBinder>& sf, std::vector<uint64_t>* outIds) {
    Parcel data, reply;
    data.writeInterfaceToken(kSfDescriptor);
    status_t st = sf->transact(ifc::GET_PHYSICAL_DISPLAY_IDS, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] B: FAIL transact GET_PHYSICAL_DISPLAY_IDS -> %d\n", st);
        return 1;
    }
    std::vector<uint64_t> ids;
    st = reply.readUint64Vector(&ids);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] B: FAIL readUint64Vector -> %d\n", st);
        return 1;
    }
    if (ids.size() != 1 || ids[0] != 0) {
        fprintf(stderr, "[surface_smoke] B: FAIL expected {0}, got size=%zu", ids.size());
        for (size_t i = 0; i < ids.size(); ++i) {
            fprintf(stderr, " [%zu]=%llu", i, (unsigned long long)ids[i]);
        }
        fprintf(stderr, "\n");
        return 1;
    }
    fprintf(stderr, "[surface_smoke] B: PASS GET_PHYSICAL_DISPLAY_IDS -> {0}\n");
    *outIds = ids;
    return 0;
}

static int check_c_physical_token(const sp<IBinder>& sf, sp<IBinder>* outToken) {
    Parcel data, reply;
    data.writeInterfaceToken(kSfDescriptor);
    data.writeUint64(0);  // physical display id
    status_t st = sf->transact(ifc::GET_PHYSICAL_DISPLAY_TOKEN, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] C: FAIL transact GET_PHYSICAL_DISPLAY_TOKEN -> %d\n", st);
        return 1;
    }
    sp<IBinder> token = reply.readStrongBinder();
    if (token == nullptr) {
        fprintf(stderr, "[surface_smoke] C: FAIL got null token for display 0\n");
        return 1;
    }
    fprintf(stderr, "[surface_smoke] C: PASS GET_PHYSICAL_DISPLAY_TOKEN(0) -> binder=%p\n",
            token.get());
    *outToken = token;
    return 0;
}

static int check_d_display_stats(const sp<IBinder>& sf, const sp<IBinder>& token) {
    Parcel data, reply;
    data.writeInterfaceToken(kSfDescriptor);
    data.writeStrongBinder(token);
    status_t st = sf->transact(ifc::GET_DISPLAY_STATS, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] D: FAIL transact GET_DISPLAY_STATS -> %d\n", st);
        return 1;
    }
    int32_t result = reply.readInt32();
    if (result != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] D: FAIL reply result=%d (expected 0)\n", result);
        return 1;
    }
    // 2× int64 = 16 bytes: vsyncTime then vsyncPeriod.
    int64_t vsyncTime = reply.readInt64();
    int64_t vsyncPeriod = reply.readInt64();
    constexpr int64_t kExpectedPeriod = 16'666'667;
    if (vsyncPeriod != kExpectedPeriod) {
        fprintf(stderr,
                "[surface_smoke] D: FAIL vsyncPeriod=%lld (expected %lld)\n",
                (long long)vsyncPeriod, (long long)kExpectedPeriod);
        return 1;
    }
    fprintf(stderr,
            "[surface_smoke] D: PASS GET_DISPLAY_STATS vsyncTime=%lld vsyncPeriod=%lld\n",
            (long long)vsyncTime, (long long)vsyncPeriod);
    return 0;
}

// Helper for Step-3 buffer-pipeline check: issue one DEQUEUE_BUFFER + check
// the reply parses as {slot in [0,2), Fence(0 fds), bufferAge, status=1
// (BUFFER_NEEDS_REALLOCATION)}.  Returns 0 on PASS, 1 on FAIL.  outSlot is
// set to the slot index for the follow-up REQUEST_BUFFER + QUEUE_BUFFER.
static int dequeue_one(const sp<IBinder>& gbp, int* outSlot,
                       uint32_t reqW, uint32_t reqH) {
    Parcel data, reply;
    data.writeInterfaceToken(kIgbpDescriptor);
    data.writeUint32(reqW);
    data.writeUint32(reqH);
    data.writeInt32(1);              // format RGBA_8888
    data.writeUint64(0);             // usage
    data.writeBool(false);           // getFrameTimestamps
    status_t st = gbp->transact(igbp::DEQUEUE_BUFFER, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] F.deq: transact -> %d\n", st);
        return 1;
    }
    int32_t slot = reply.readInt32();
    // Fence flattenable: int32 len, int32 fdCount, payload, fds.
    int32_t fLen = reply.readInt32();
    int32_t fFdCount = reply.readInt32();
    if (fLen != 4 || fFdCount != 0) {
        fprintf(stderr,
                "[surface_smoke] F.deq: unexpected Fence header len=%d fdCount=%d\n",
                fLen, fFdCount);
        return 1;
    }
    (void)reply.readInplace(static_cast<size_t>(fLen));     // skip payload
    uint64_t bufferAge = reply.readUint64();
    int32_t status = reply.readInt32();
    fprintf(stderr,
            "[surface_smoke] F.deq: slot=%d bufferAge=%llu status=%d\n",
            slot, (unsigned long long)bufferAge, status);
    if (slot < 0 || slot >= 2) {
        fprintf(stderr, "[surface_smoke] F.deq: slot out of range\n");
        return 1;
    }
    // status == 1 == BUFFER_NEEDS_REALLOCATION on first use, == 0 on cached.
    if (status != 0 && status != 1) {
        fprintf(stderr, "[surface_smoke] F.deq: unexpected status=%d\n", status);
        return 1;
    }
    *outSlot = slot;
    return 0;
}

static int request_one(const sp<IBinder>& gbp, int slot,
                       uint32_t expectedW, uint32_t expectedH) {
    Parcel data, reply;
    data.writeInterfaceToken(kIgbpDescriptor);
    data.writeInt32(slot);
    status_t st = gbp->transact(igbp::REQUEST_BUFFER, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] F.req: transact -> %d\n", st);
        return 1;
    }
    int32_t nonNull = reply.readInt32();
    if (nonNull != 1) {
        fprintf(stderr,
                "[surface_smoke] F.req: nonNull=%d (expected 1)\n", nonNull);
        return 1;
    }
    // Flattenable header: int32 len, int32 fdCount.
    int32_t gbLen = reply.readInt32();
    int32_t gbFdCount = reply.readInt32();
    if (gbLen != 17 * 4 || gbFdCount != 1) {
        fprintf(stderr,
                "[surface_smoke] F.req: unexpected GraphicBuffer header "
                "len=%d fdCount=%d (expected 68 + 1)\n",
                gbLen, gbFdCount);
        return 1;
    }
    const int32_t* payload =
            reinterpret_cast<const int32_t*>(reply.readInplace(gbLen));
    if (payload == nullptr) {
        fprintf(stderr, "[surface_smoke] F.req: readInplace(payload) null\n");
        return 1;
    }
    constexpr int32_t kMagicGB01 = 'GB01';
    if (payload[0] != kMagicGB01) {
        fprintf(stderr,
                "[surface_smoke] F.req: bad magic 0x%08x (expected 'GB01')\n",
                payload[0]);
        return 1;
    }
    if (static_cast<uint32_t>(payload[1]) != expectedW
        || static_cast<uint32_t>(payload[2]) != expectedH) {
        fprintf(stderr,
                "[surface_smoke] F.req: dims mismatch got %dx%d expected %ux%u\n",
                payload[1], payload[2], expectedW, expectedH);
        return 1;
    }
    if (payload[10] != 1 || payload[11] != 4) {
        fprintf(stderr,
                "[surface_smoke] F.req: numFds=%d numInts=%d (expected 1, 4)\n",
                payload[10], payload[11]);
        return 1;
    }
    // Now read the fd that backs the GraphicBuffer.  AOSP Parcel writes
    // file descriptors as flat_binder_object entries; the read side dups
    // them and hands a usable fd back.  We just need to confirm one fd
    // arrives — full mmap-and-write is left for Step 4 (writer wiring).
    int fd = reply.readFileDescriptor();
    if (fd < 0) {
        fprintf(stderr,
                "[surface_smoke] F.req: readFileDescriptor() -> %d "
                "(expected a valid memfd)\n",
                fd);
        return 1;
    }
    int32_t status = reply.readInt32();
    fprintf(stderr,
            "[surface_smoke] F.req: slot=%d magic=GB01 dims=%dx%d "
            "stride=%d format=%d numFds=%d numInts=%d fd=%d status=%d\n",
            slot, payload[1], payload[2], payload[3], payload[4],
            payload[10], payload[11], fd, status);
    if (status != NO_ERROR) return 1;
    return 0;
}

static int queue_one(const sp<IBinder>& gbp, int slot) {
    Parcel data, reply;
    data.writeInterfaceToken(kIgbpDescriptor);
    data.writeInt32(slot);
    // Empty QueueBufferInput — the daemon Phase-1 doesn't decode it.  We
    // write a 4-byte zero payload + 0 fds so the read side gets a sane
    // Flattenable header even though our parser ignores it.
    data.writeInt32(4);    // len
    data.writeInt32(0);    // fdCount
    int32_t zero = 0;
    data.write(&zero, sizeof(zero));
    status_t st = gbp->transact(igbp::QUEUE_BUFFER, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] F.que: transact -> %d\n", st);
        return 1;
    }
    // Reply: QueueBufferOutput (flattenable, len + fdCount + payload),
    // then int32 status.
    int32_t qLen = reply.readInt32();
    int32_t qFds = reply.readInt32();
    if (qLen != 57 || qFds != 0) {
        fprintf(stderr,
                "[surface_smoke] F.que: unexpected QueueBufferOutput header "
                "len=%d fdCount=%d (expected 57, 0)\n",
                qLen, qFds);
        return 1;
    }
    (void)reply.readInplace(static_cast<size_t>(qLen));
    int32_t status = reply.readInt32();
    fprintf(stderr, "[surface_smoke] F.que: slot=%d status=%d\n", slot, status);
    if (status != NO_ERROR) return 1;
    return 0;
}

// Step-3 check F: end-to-end buffer pipeline.
//   1. CREATE_SURFACE through the client returns handle + GBP.
//   2. DEQUEUE_BUFFER on the GBP returns a slot in [0,2).
//   3. REQUEST_BUFFER(slot) returns a GraphicBuffer flattenable with the
//      expected magic + dims + 1 fd.
//   4. QUEUE_BUFFER returns NO_ERROR + a QueueBufferOutput.
//   5. Repeat once more — the second DEQUEUE should pick the other slot
//      (Phase-1 ring of 2; QUEUE marks the slot FREE again so we should
//      get slot 0 or 1 either time).
static int check_f_buffer_pipeline(const sp<IBinder>& client) {
    constexpr uint32_t kW = 1080, kH = 2280;

    // Issue CREATE_SURFACE.  Parcel layout matches AOSP-11
    // BpSurfaceComposerClient::createSurface (SafeInterface auto-marshal):
    //   String8 name, uint32 w, uint32 h, int32 format, uint32 flags,
    //   StrongBinder parent, LayerMetadata.
    Parcel data, reply;
    data.writeInterfaceToken(kSfClientDescriptor);
    data.writeString8(String8("westlake-m6step3-smoke"));
    data.writeUint32(kW);
    data.writeUint32(kH);
    data.writeInt32(1);                 // PIXEL_FORMAT_RGBA_8888
    data.writeUint32(0);                // flags
    data.writeStrongBinder(nullptr);    // parent
    data.writeInt32(0);                 // LayerMetadata: empty map count
    status_t st = client->transact(ifcc::CREATE_SURFACE, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] F: FAIL CREATE_SURFACE transact -> %d\n", st);
        return 1;
    }
    sp<IBinder> handle = reply.readStrongBinder();
    sp<IBinder> gbp = reply.readStrongBinder();
    uint32_t transformHint = reply.readUint32();
    int32_t createStatus = reply.readInt32();
    fprintf(stderr,
            "[surface_smoke] F.cs: handle=%p gbp=%p transformHint=%u status=%d\n",
            handle.get(), gbp.get(), transformHint, createStatus);
    if (handle == nullptr || gbp == nullptr || createStatus != NO_ERROR) {
        fprintf(stderr,
                "[surface_smoke] F: FAIL CREATE_SURFACE -> handle=%p gbp=%p status=%d\n",
                handle.get(), gbp.get(), createStatus);
        return 1;
    }

    // First DEQUEUE/REQUEST/QUEUE round-trip.
    int slotA = -1;
    if (dequeue_one(gbp, &slotA, kW, kH) != 0) return 1;
    if (request_one(gbp, slotA, kW, kH) != 0) return 1;
    if (queue_one(gbp, slotA) != 0) return 1;

    // Second round-trip — slot should still be a valid index in [0,2).
    int slotB = -1;
    if (dequeue_one(gbp, &slotB, kW, kH) != 0) return 1;
    if (request_one(gbp, slotB, kW, kH) != 0) return 1;
    if (queue_one(gbp, slotB) != 0) return 1;

    fprintf(stderr,
            "[surface_smoke] F: PASS buffer pipeline (slotA=%d slotB=%d)\n",
            slotA, slotB);
    return 0;
}

// ---------------------------------------------------------------------------
// Step-4 check G: DLST pipe consumer fires + writes magic + raw RGBA bytes.
//
// Acceptance protocol:
//   1. mkfifo($WESTLAKE_DLST_PIPE) if not already a FIFO.
//   2. Spawn a reader thread that opens the FIFO O_RDONLY and reads up to
//      kHeaderBytes (8 + 21 = 29 bytes envelope+inline-header) so we can
//      validate the wire shape end-to-end.  Reader runs with a deadline.
//   3. CREATE_SURFACE (a fresh one — Step-3 leaves no per-test surface
//      hanging around since check F's surface dropped with its client),
//      DEQUEUE_BUFFER + REQUEST_BUFFER to get the memfd fd, mmap+write a
//      simple test pattern (top half 0xFF0000FF red, bottom half
//      0x00FF00FF green in RGBA byte order — alpha=0xFF, red=0xFF for top,
//      green=0xFF for bottom).  QUEUE_BUFFER.
//   4. The daemon's DlstConsumer thread wakes, reads the slot, writes a
//      DLST frame to the FIFO.  Reader thread captures the bytes.
//   5. Verify: magic == 0x444C5354; size in plausible bounds; opcode == 12
//      (OP_ARGB_BITMAP); width/height match; first 4 pixel bytes match the
//      red pattern.
// ---------------------------------------------------------------------------

namespace {
constexpr uint32_t kDlstMagic    = 0x444C5354u;
constexpr uint8_t  kOpArgbBitmap = 12;

// Reader-side rendezvous structure.
struct DlstReaderResult {
    std::atomic<bool> done{false};
    std::atomic<bool> ok{false};
    uint32_t magic{0};
    uint32_t payloadSize{0};
    uint8_t  opcode{0};
    int32_t  width{0};
    int32_t  height{0};
    int32_t  dataLen{0};
    uint8_t  firstPixel[4]{0,0,0,0};
    std::string err;
};

static bool readExactly(int fd, void* buf, size_t len, int deadlineMs) {
    auto t0 = std::chrono::steady_clock::now();
    uint8_t* p = static_cast<uint8_t*>(buf);
    size_t rem = len;
    while (rem > 0) {
        ssize_t n = ::read(fd, p, rem);
        if (n > 0) {
            p   += n;
            rem -= static_cast<size_t>(n);
            continue;
        }
        if (n == 0) {
            // EOF — writer hasn't opened yet, or short-write happened.
            std::this_thread::sleep_for(std::chrono::milliseconds(2));
        } else if (errno == EINTR || errno == EAGAIN) {
            std::this_thread::sleep_for(std::chrono::milliseconds(2));
        } else {
            return false;
        }
        auto now = std::chrono::steady_clock::now();
        if (std::chrono::duration_cast<std::chrono::milliseconds>(now - t0).count()
            > deadlineMs) {
            return false;
        }
    }
    return true;
}

static void dlst_reader_thread(const std::string& fifoPath,
                                DlstReaderResult* out, int deadlineMs) {
    // Open the FIFO for reading.  This blocks until a writer attaches,
    // which is exactly what we want — the daemon's consumer thread will
    // open the pipe for writing once QUEUE_BUFFER fires.
    int fd = ::open(fifoPath.c_str(), O_RDONLY);
    if (fd < 0) {
        out->err = std::string("open(rd) failed: ") + strerror(errno);
        out->done.store(true);
        return;
    }
    // Read envelope: magic, payloadSize.
    uint32_t hdr[2];
    if (!readExactly(fd, hdr, sizeof(hdr), deadlineMs)) {
        out->err = "envelope read timeout/error";
        ::close(fd);
        out->done.store(true);
        return;
    }
    out->magic       = hdr[0];
    out->payloadSize = hdr[1];

    // Now read the inline header (opcode + 2×float + 3×int = 21 bytes).
    uint8_t inline_hdr[21];
    if (!readExactly(fd, inline_hdr, sizeof(inline_hdr), deadlineMs)) {
        out->err = "inline-header read timeout/error";
        ::close(fd);
        out->done.store(true);
        return;
    }
    out->opcode = inline_hdr[0];
    int32_t w, h, dl;
    memcpy(&w,  inline_hdr + 9,  4);
    memcpy(&h,  inline_hdr + 13, 4);
    memcpy(&dl, inline_hdr + 17, 4);
    out->width   = w;
    out->height  = h;
    out->dataLen = dl;

    // Read first 4 bytes of pixel payload to spot-check pattern.
    if (!readExactly(fd, out->firstPixel, sizeof(out->firstPixel), deadlineMs)) {
        out->err = "first-pixel read timeout/error";
        ::close(fd);
        out->done.store(true);
        return;
    }

    // Drain the remainder so the writer side doesn't EPIPE.  We don't
    // validate every pixel — the spot-check above is sufficient acceptance
    // per the M6_STEP3 → STEP4 handoff acceptance note.
    int64_t remaining = static_cast<int64_t>(dl) - 4;
    uint8_t sink[8192];
    while (remaining > 0) {
        ssize_t toRead = remaining > (ssize_t)sizeof(sink)
                       ? (ssize_t)sizeof(sink)
                       : (ssize_t)remaining;
        if (!readExactly(fd, sink, toRead, deadlineMs)) break;
        remaining -= toRead;
    }
    ::close(fd);
    out->ok.store(true);
    out->done.store(true);
}

// Helper: mmap the memfd we received via REQUEST_BUFFER and paint a
// red-top/green-bottom pattern (RGBA byte order).  Returns 0 on success.
static int paint_test_pattern(int memfd, uint32_t w, uint32_t h, uint32_t stride) {
    if (memfd < 0) return -EINVAL;
    size_t mapSize = static_cast<size_t>(stride) * h * 4u;
    void* p = ::mmap(nullptr, mapSize, PROT_READ | PROT_WRITE, MAP_SHARED,
                     memfd, 0);
    if (p == MAP_FAILED) {
        fprintf(stderr,
                "[surface_smoke] G.paint: mmap(memfd=%d size=%zu) FAILED errno=%d\n",
                memfd, mapSize, errno);
        return -errno;
    }
    uint8_t* base = static_cast<uint8_t*>(p);
    for (uint32_t y = 0; y < h; ++y) {
        uint8_t* row = base + static_cast<size_t>(y) * stride * 4u;
        uint8_t r = (y < h / 2) ? 0xFF : 0x00;
        uint8_t g = (y < h / 2) ? 0x00 : 0xFF;
        uint8_t b = 0x00;
        uint8_t a = 0xFF;
        for (uint32_t x = 0; x < w; ++x) {
            row[x*4 + 0] = r;
            row[x*4 + 1] = g;
            row[x*4 + 2] = b;
            row[x*4 + 3] = a;
        }
    }
    ::munmap(p, mapSize);
    return 0;
}

static int request_one_capture_fd(const sp<IBinder>& gbp, int slot,
                                  uint32_t expectedW, uint32_t expectedH,
                                  int* outFd, uint32_t* outStride) {
    Parcel data, reply;
    data.writeInterfaceToken(kIgbpDescriptor);
    data.writeInt32(slot);
    status_t st = gbp->transact(igbp::REQUEST_BUFFER, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] G.req: transact -> %d\n", st);
        return 1;
    }
    int32_t nonNull = reply.readInt32();
    if (nonNull != 1) return 1;
    int32_t gbLen = reply.readInt32();
    int32_t gbFdCount = reply.readInt32();
    if (gbLen != 17 * 4 || gbFdCount != 1) return 1;
    const int32_t* payload =
            reinterpret_cast<const int32_t*>(reply.readInplace(gbLen));
    if (payload == nullptr) return 1;
    if (static_cast<uint32_t>(payload[1]) != expectedW
        || static_cast<uint32_t>(payload[2]) != expectedH) return 1;
    if (outStride) *outStride = static_cast<uint32_t>(payload[3]);
    int fd = reply.readFileDescriptor();
    if (fd < 0) return 1;
    // AOSP Parcel::readFileDescriptor returns a non-owned fd whose lifetime
    // ends when the Parcel is destructed.  Since we want to use it AFTER
    // this function returns (mmap + paint test pattern, queue), dup() it
    // into our own fd table.
    int duped = ::dup(fd);
    if (duped < 0) {
        fprintf(stderr,
                "[surface_smoke] G.req: dup(memfd=%d) FAILED errno=%d\n",
                fd, errno);
        return 1;
    }
    *outFd = duped;
    int32_t status = reply.readInt32();
    return status == NO_ERROR ? 0 : 1;
}
}  // namespace

// Step-4 check G: DLST consumer thread fires.  CREATE_SURFACE, get GBP,
// dequeue+request+write pattern+queue, verify reader sees correct bytes.
static int check_g_dlst_consumer(const sp<IBinder>& client) {
    // Smoke test pattern: small surface to keep mmap + write cheap.
    constexpr uint32_t kW = 64, kH = 32;

    const char* fifoEnv = getenv("WESTLAKE_DLST_PIPE");
    std::string fifoPath = fifoEnv && *fifoEnv ? fifoEnv
                                               : "/data/local/tmp/westlake/dlst.fifo";
    fprintf(stderr,
            "[surface_smoke] G: starting; fifo=%s pattern=%ux%u red-top/green-bottom\n",
            fifoPath.c_str(), kW, kH);

    // Ensure the FIFO exists.  mkfifo() returns -1/EEXIST if it's already
    // there — that's fine (caller's launcher may have created it).
    struct stat sbuf;
    if (::stat(fifoPath.c_str(), &sbuf) == 0 && !S_ISFIFO(sbuf.st_mode)) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL path %s exists and is not a FIFO\n",
                fifoPath.c_str());
        return 1;
    }
    if (::stat(fifoPath.c_str(), &sbuf) != 0) {
        if (::mkfifo(fifoPath.c_str(), 0660) != 0 && errno != EEXIST) {
            fprintf(stderr,
                    "[surface_smoke] G: FAIL mkfifo(%s) errno=%d (%s)\n",
                    fifoPath.c_str(), errno, strerror(errno));
            return 1;
        }
    }

    // Spawn reader BEFORE issuing the writer-side transactions; otherwise
    // the daemon's consumer hits ENXIO and silently drops the frame.
    DlstReaderResult result;
    std::thread reader(dlst_reader_thread, fifoPath, &result, /*deadlineMs=*/4000);

    // Brief pause to let the reader's open() block.
    std::this_thread::sleep_for(std::chrono::milliseconds(50));

    // CREATE_SURFACE.
    Parcel data, reply;
    data.writeInterfaceToken(kSfClientDescriptor);
    data.writeString8(String8("westlake-m6step4-smoke"));
    data.writeUint32(kW);
    data.writeUint32(kH);
    data.writeInt32(1);
    data.writeUint32(0);
    data.writeStrongBinder(nullptr);
    data.writeInt32(0);
    status_t st = client->transact(ifcc::CREATE_SURFACE, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] G: FAIL CREATE_SURFACE transact -> %d\n", st);
        reader.join();
        return 1;
    }
    sp<IBinder> handle = reply.readStrongBinder();
    sp<IBinder> gbp = reply.readStrongBinder();
    reply.readUint32();    // transformHint
    int32_t createStatus = reply.readInt32();
    if (createStatus != NO_ERROR || gbp == nullptr) {
        fprintf(stderr, "[surface_smoke] G: FAIL CREATE_SURFACE status=%d\n", createStatus);
        reader.join();
        return 1;
    }

    // DEQUEUE.
    int slot = -1;
    if (dequeue_one(gbp, &slot, kW, kH) != 0) {
        fprintf(stderr, "[surface_smoke] G: FAIL dequeue\n");
        reader.join();
        return 1;
    }

    // REQUEST_BUFFER + capture the memfd fd.
    int memfd = -1;
    uint32_t stride = 0;
    if (request_one_capture_fd(gbp, slot, kW, kH, &memfd, &stride) != 0) {
        fprintf(stderr, "[surface_smoke] G: FAIL request_buffer\n");
        reader.join();
        return 1;
    }
    fprintf(stderr,
            "[surface_smoke] G: got memfd=%d stride=%u — painting test pattern\n",
            memfd, stride);

    if (paint_test_pattern(memfd, kW, kH, stride) != 0) {
        ::close(memfd);
        reader.join();
        return 1;
    }
    // We can close our local dup; the daemon's MemfdGraphicBuffer holds
    // the authoritative fd.
    ::close(memfd);

    // QUEUE.
    if (queue_one(gbp, slot) != 0) {
        fprintf(stderr, "[surface_smoke] G: FAIL queue\n");
        reader.join();
        return 1;
    }
    fprintf(stderr, "[surface_smoke] G: QUEUE_BUFFER sent; awaiting reader\n");

    // Wait for reader to finish (deadline already encoded in dlst_reader_thread).
    reader.join();

    if (!result.ok.load()) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL reader did not capture full frame: %s\n",
                result.err.c_str());
        return 1;
    }
    if (result.magic != kDlstMagic) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL magic mismatch 0x%08x (expected 0x%08x)\n",
                result.magic, kDlstMagic);
        return 1;
    }
    if (result.opcode != kOpArgbBitmap) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL opcode=%u (expected %u)\n",
                (unsigned)result.opcode, (unsigned)kOpArgbBitmap);
        return 1;
    }
    if (static_cast<uint32_t>(result.width) != kW
        || static_cast<uint32_t>(result.height) != kH) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL dims %dx%d (expected %ux%u)\n",
                result.width, result.height, kW, kH);
        return 1;
    }
    const int32_t expectedDataLen = static_cast<int32_t>(kW * kH * 4u);
    if (result.dataLen != expectedDataLen) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL dataLen=%d (expected %d)\n",
                result.dataLen, expectedDataLen);
        return 1;
    }
    // First pixel should be red-top (R=0xFF, G=0x00, B=0x00, A=0xFF).
    if (result.firstPixel[0] != 0xFF || result.firstPixel[1] != 0x00
        || result.firstPixel[2] != 0x00 || result.firstPixel[3] != 0xFF) {
        fprintf(stderr,
                "[surface_smoke] G: FAIL first pixel = %02x %02x %02x %02x "
                "(expected FF 00 00 FF)\n",
                result.firstPixel[0], result.firstPixel[1],
                result.firstPixel[2], result.firstPixel[3]);
        return 1;
    }

    fprintf(stderr,
            "[surface_smoke] G: PASS magic=0x%08x payloadSize=%u opcode=%u "
            "dims=%dx%d dataLen=%d firstPixel=FF 00 00 FF\n",
            result.magic, result.payloadSize, (unsigned)result.opcode,
            result.width, result.height, result.dataLen);
    return 0;
}

// ---------------------------------------------------------------------------
// Step-5 check H: IDisplayEventConnection round-trip + 60 Hz vsync cadence.
//
// Acceptance protocol:
//   1. CREATE_DISPLAY_EVENT_CONNECTION on the SurfaceFlinger binder — verify
//      reply has a non-null IDisplayEventConnection strong binder.
//   2. STEAL_RECEIVE_CHANNEL on that connection — verify reply parcels two
//      file descriptors (BitTube wire format: receive then send).  We dup
//      the first one (receive fd) to our own table and use it for reads.
//   3. SET_VSYNC_RATE(1) — gate the daemon's loop on.
//   4. Read N=3 WireEvent (40-byte) messages from the receive fd.  Each
//      should have:
//        - magic 'vsyn' (0x7673796E) in headerType
//        - displayId == 0
//        - monotonic vsyncCount
//        - timestamps within plausible bounds of CLOCK_MONOTONIC now
//      And the inter-event spacing should be ~16.666 ms ± 5 ms (slack for
//      jitter on a busy device).
//   5. SET_VSYNC_RATE(0) — pause emission.
// ---------------------------------------------------------------------------

namespace {

// Wire-event mirror — MUST match WestlakeDisplayEventConnection::WireEvent.
struct __attribute__((packed)) SmokeWireEvent {
    uint32_t headerType;
    uint32_t headerPad;
    uint64_t headerDisplayId;
    int64_t  headerTimestampNs;
    uint32_t vsyncCount;
    uint32_t vsyncPad;
    int64_t  vsyncExpectedTs;
};
static_assert(sizeof(SmokeWireEvent) == 40, "WireEvent layout mismatch");

constexpr uint32_t kVsyncFourcc =
    (static_cast<uint32_t>('v') << 24) |
    (static_cast<uint32_t>('s') << 16) |
    (static_cast<uint32_t>('y') <<  8) |
    (static_cast<uint32_t>('n'));

// Read exactly one SOCK_SEQPACKET message of `len` bytes.  Returns true on
// success.  Polls in a tight loop with a per-call deadline because the
// socket is O_NONBLOCK from the daemon side.
static bool recvOneMessage(int fd, void* buf, size_t len, int deadlineMs) {
    auto t0 = std::chrono::steady_clock::now();
    while (true) {
        ssize_t n = ::recv(fd, buf, len, MSG_DONTWAIT);
        if (n == static_cast<ssize_t>(len)) return true;
        if (n >= 0 && static_cast<size_t>(n) != len) {
            fprintf(stderr,
                    "[surface_smoke] H.recv: partial recv %zd/%zu "
                    "(SEQPACKET should be atomic)\n",
                    n, len);
            return false;
        }
        if (n < 0 && errno != EAGAIN && errno != EWOULDBLOCK && errno != EINTR) {
            fprintf(stderr,
                    "[surface_smoke] H.recv: recv() errno=%d (%s)\n",
                    errno, strerror(errno));
            return false;
        }
        auto now = std::chrono::steady_clock::now();
        if (std::chrono::duration_cast<std::chrono::milliseconds>(now - t0).count()
            > deadlineMs) {
            fprintf(stderr,
                    "[surface_smoke] H.recv: deadline %dms exceeded\n",
                    deadlineMs);
            return false;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
}

}  // namespace

static int check_h_display_event_connection(const sp<IBinder>& sf) {
    fprintf(stderr, "[surface_smoke] H: starting IDisplayEventConnection round-trip\n");

    // Step 1: CREATE_DISPLAY_EVENT_CONNECTION.
    Parcel cdec_data, cdec_reply;
    cdec_data.writeInterfaceToken(kSfDescriptor);
    cdec_data.writeInt32(0);   // vsyncSource: eVsyncSourceApp
    cdec_data.writeInt32(0);   // configChanged: eConfigChangedSuppress
    status_t st = sf->transact(ifc::CREATE_DISPLAY_EVENT_CONNECTION,
                               cdec_data, &cdec_reply);
    if (st != NO_ERROR) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL CREATE_DISPLAY_EVENT_CONNECTION "
                "transact -> %d\n", st);
        return 1;
    }
    sp<IBinder> conn = cdec_reply.readStrongBinder();
    if (conn == nullptr) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL CREATE_DISPLAY_EVENT_CONNECTION "
                "returned null binder\n");
        return 1;
    }
    fprintf(stderr, "[surface_smoke] H.cdec: conn=%p\n", conn.get());

    // Step 2: STEAL_RECEIVE_CHANNEL — fetch the BitTube fds.
    Parcel src_data, src_reply;
    src_data.writeInterfaceToken(kIdecDescriptor);
    st = conn->transact(idec::STEAL_RECEIVE_CHANNEL, src_data, &src_reply);
    if (st != NO_ERROR) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL STEAL_RECEIVE_CHANNEL -> %d\n", st);
        return 1;
    }
    // Reply parcel contains two file descriptors (receive then send) —
    // BitTube::writeToParcel order.  AOSP Parcel::readFileDescriptor
    // returns a Parcel-owned fd; we dup() it because the Parcel goes out
    // of scope at the end of this function.
    int rawRecv = src_reply.readFileDescriptor();
    int rawSend = src_reply.readFileDescriptor();
    if (rawRecv < 0 || rawSend < 0) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL STEAL_RECEIVE_CHANNEL fds: "
                "recv=%d send=%d (expected both >=0)\n",
                rawRecv, rawSend);
        return 1;
    }
    int recvFd = ::dup(rawRecv);
    if (recvFd < 0) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL dup(recv=%d) errno=%d\n",
                rawRecv, errno);
        return 1;
    }
    fprintf(stderr,
            "[surface_smoke] H.src: parceled rawRecv=%d rawSend=%d -> duped recvFd=%d\n",
            rawRecv, rawSend, recvFd);

    // Step 3: SET_VSYNC_RATE(1).
    Parcel svr_data, svr_reply;
    svr_data.writeInterfaceToken(kIdecDescriptor);
    svr_data.writeInt32(1);  // every tick
    st = conn->transact(idec::SET_VSYNC_RATE, svr_data, &svr_reply);
    if (st != NO_ERROR) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL SET_VSYNC_RATE(1) -> %d\n", st);
        ::close(recvFd);
        return 1;
    }
    auto t_subscribe = std::chrono::steady_clock::now();
    fprintf(stderr, "[surface_smoke] H.svr: subscribed @ rate=1\n");

    // Step 4: read N events, capture timestamps, validate cadence.
    constexpr int kN = 3;
    SmokeWireEvent evs[kN] = {};
    std::chrono::steady_clock::time_point recvTimes[kN];

    for (int i = 0; i < kN; ++i) {
        // Per-event deadline: 100 ms is generous (one frame is ~16.7 ms).
        if (!recvOneMessage(recvFd, &evs[i], sizeof(SmokeWireEvent), /*deadlineMs=*/200)) {
            fprintf(stderr,
                    "[surface_smoke] H: FAIL waiting for event #%d\n", i);
            ::close(recvFd);
            return 1;
        }
        recvTimes[i] = std::chrono::steady_clock::now();
        fprintf(stderr,
                "[surface_smoke] H.ev[%d]: type=0x%08x displayId=%llu "
                "tsNs=%lld count=%u expTsNs=%lld\n",
                i, evs[i].headerType,
                (unsigned long long)evs[i].headerDisplayId,
                (long long)evs[i].headerTimestampNs,
                evs[i].vsyncCount,
                (long long)evs[i].vsyncExpectedTs);
    }

    // Step 5: pause emission to be a good citizen for any future checks
    // that share this connection.
    Parcel svr2_data, svr2_reply;
    svr2_data.writeInterfaceToken(kIdecDescriptor);
    svr2_data.writeInt32(0);
    (void)conn->transact(idec::SET_VSYNC_RATE, svr2_data, &svr2_reply);
    fprintf(stderr, "[surface_smoke] H.svr: unsubscribed @ rate=0\n");

    ::close(recvFd);

    // Validation phase.
    int failures = 0;
    for (int i = 0; i < kN; ++i) {
        if (evs[i].headerType != kVsyncFourcc) {
            fprintf(stderr,
                    "[surface_smoke] H: FAIL ev[%d] magic 0x%08x "
                    "(expected 0x%08x 'vsyn')\n",
                    i, evs[i].headerType, kVsyncFourcc);
            ++failures;
        }
        if (evs[i].headerDisplayId != 0) {
            fprintf(stderr,
                    "[surface_smoke] H: FAIL ev[%d] displayId=%llu "
                    "(expected 0)\n",
                    i, (unsigned long long)evs[i].headerDisplayId);
            ++failures;
        }
        if (i > 0 && evs[i].vsyncCount <= evs[i-1].vsyncCount) {
            fprintf(stderr,
                    "[surface_smoke] H: FAIL ev[%d].count=%u not strictly "
                    "greater than ev[%d].count=%u\n",
                    i, evs[i].vsyncCount, i-1, evs[i-1].vsyncCount);
            ++failures;
        }
    }
    // Cadence check: inter-event spacing should be ~16.666 ms.  We allow
    // wide slack (8–35 ms) because (a) the first event after subscribe may
    // arrive at any phase of the period, (b) the OS may briefly preempt the
    // daemon's vsync thread under load, and (c) we observe at the receive
    // wallclock, not the daemon's emit wallclock.
    constexpr int kMinMs = 8;
    constexpr int kMaxMs = 35;
    for (int i = 1; i < kN; ++i) {
        auto deltaMs = std::chrono::duration_cast<std::chrono::milliseconds>(
                recvTimes[i] - recvTimes[i-1]).count();
        if (deltaMs < kMinMs || deltaMs > kMaxMs) {
            fprintf(stderr,
                    "[surface_smoke] H: FAIL ev[%d]<-ev[%d] spacing=%lldms "
                    "(expected %d-%d for ~60Hz)\n",
                    i, i-1, (long long)deltaMs, kMinMs, kMaxMs);
            ++failures;
        } else {
            fprintf(stderr,
                    "[surface_smoke] H.cadence: ev[%d]<-ev[%d] spacing=%lldms (OK)\n",
                    i, i-1, (long long)deltaMs);
        }
    }
    // Sanity: total elapsed since subscribe shouldn't be wildly off.
    auto totalMs = std::chrono::duration_cast<std::chrono::milliseconds>(
            recvTimes[kN-1] - t_subscribe).count();
    fprintf(stderr,
            "[surface_smoke] H.cadence: total subscribe->last spacing=%lldms\n",
            (long long)totalMs);

    if (failures != 0) {
        fprintf(stderr,
                "[surface_smoke] H: FAIL %d sub-check(s) of %d events\n",
                failures, kN);
        return 1;
    }
    fprintf(stderr,
            "[surface_smoke] H: PASS %d vsync events at ~60Hz (magic=vsyn, "
            "monotonic counts %u->%u)\n",
            kN, evs[0].vsyncCount, evs[kN-1].vsyncCount);
    return 0;
}

static int check_e_create_connection(const sp<IBinder>& sf, sp<IBinder>* outClient) {
    Parcel data, reply;
    data.writeInterfaceToken(kSfDescriptor);
    status_t st = sf->transact(ifc::CREATE_CONNECTION, data, &reply);
    if (st != NO_ERROR) {
        fprintf(stderr, "[surface_smoke] E: FAIL transact CREATE_CONNECTION -> %d\n", st);
        return 1;
    }
    sp<IBinder> client = reply.readStrongBinder();
    if (client == nullptr) {
        fprintf(stderr, "[surface_smoke] E: FAIL CREATE_CONNECTION -> null binder\n");
        return 1;
    }
    fprintf(stderr, "[surface_smoke] E: PASS CREATE_CONNECTION -> binder=%p\n", client.get());
    *outClient = client;
    return 0;
}

int main(int argc, char** argv) {
    (void)argc;
    (void)argv;

    const char* dev = dev_for_run();
    fprintf(stderr, "[surface_smoke pid=%d] opening %s\n", getpid(), dev);
    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[surface_smoke] FAIL: initWithDriver(%s) returned null\n", dev);
        return 1;
    }
    ps->startThreadPool();

    int failures = 0;

    sp<IBinder> sf;
    failures += check_a_find_service(&sf);
    if (sf == nullptr) {
        fprintf(stderr, "[surface_smoke] aborting (no SurfaceFlinger handle)\n");
        return 1;
    }

    std::vector<uint64_t> ids;
    failures += check_b_physical_ids(sf, &ids);

    sp<IBinder> token;
    failures += check_c_physical_token(sf, &token);

    if (token != nullptr) {
        failures += check_d_display_stats(sf, token);
    } else {
        fprintf(stderr, "[surface_smoke] D: SKIP (no token from check C)\n");
        ++failures;
    }

    sp<IBinder> client;
    failures += check_e_create_connection(sf, &client);

    if (client != nullptr) {
        failures += check_f_buffer_pipeline(client);
    } else {
        fprintf(stderr, "[surface_smoke] F: SKIP (no client from check E)\n");
        ++failures;
    }

    // Step-4 check G: DLST pipe consumer thread fires + writes bytes.  Only
    // run if the env var WESTLAKE_SMOKE_CHECK_G=1 OR the FIFO path is
    // explicitly set; otherwise some Step-3-only sandboxes can still pass
    // checks A..F without the pipe machinery.  The default smoke driver
    // (m6step4-smoke.sh) always sets WESTLAKE_SMOKE_CHECK_G=1.
    const char* doG = getenv("WESTLAKE_SMOKE_CHECK_G");
    if (doG && *doG == '1' && client != nullptr) {
        // CREATE_CONNECTION returned a fresh client for check E + F; we can
        // reuse it (the surface from F has already cycled).  But to be
        // safe, get a NEW client so the consumer thread under check G is
        // not tangled with check F's lifecycle.
        sp<IBinder> clientG;
        if (check_e_create_connection(sf, &clientG) == 0 && clientG != nullptr) {
            failures += check_g_dlst_consumer(clientG);
        } else {
            fprintf(stderr, "[surface_smoke] G: SKIP (CREATE_CONNECTION for G failed)\n");
            ++failures;
        }
    } else {
        fprintf(stderr, "[surface_smoke] G: SKIP "
                "(WESTLAKE_SMOKE_CHECK_G != 1 or no client)\n");
    }

    // Step-5 check H: IDisplayEventConnection round-trip + 60 Hz vsync.
    // Always-on (no gating env var) once the daemon supports it — the
    // CREATE_DISPLAY_EVENT_CONNECTION transaction is independent of the
    // DLST pipe machinery so it can run even when check G is skipped.
    const char* skipH = getenv("WESTLAKE_SMOKE_SKIP_H");
    bool runH = !(skipH && *skipH == '1');
    if (runH) {
        failures += check_h_display_event_connection(sf);
    } else {
        fprintf(stderr, "[surface_smoke] H: SKIP (WESTLAKE_SMOKE_SKIP_H=1)\n");
    }

    int total = 6;
    if (doG && *doG == '1') ++total;
    if (runH)               ++total;
    fprintf(stderr, "[surface_smoke] summary: %d failure(s) of %d checks\n",
            failures, total);
    return failures == 0 ? 0 : 1;
}
