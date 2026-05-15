// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 2: Tier-1 transaction dispatch)
//
// Step 1 returned NO_ERROR with an empty reply for every transaction.  Step 2
// (this CR) implements the M6_SURFACE_DAEMON_PLAN.md §2.2 Tier-1 ISurfaceComposer
// methods that framework.jar's BpSurfaceComposer can issue during app
// discovery (CREATE_CONNECTION, GET_PHYSICAL_DISPLAY_*, GET_DISPLAY_*, etc.).
//
// For each Tier-1 method we read the args off `data`, invoke a canned handler
// that emits a 1080×2280@60Hz Phase-1 display, and write a well-formed reply
// matching AOSP-11's BpSurfaceComposer reader expectations (see
// frameworks/native/libs/gui/ISurfaceComposer.cpp §325-§411).  Non-Tier-1
// codes log + ack with NO_ERROR (Step-1 behavior preserved); Steps 3-6 will
// promote them as McD / noice exercise them.
//
// Anti-drift: zero per-app branches; every Android peer sees identical
// canned display state.

#include "WestlakeSurfaceComposer.h"
#include "WestlakeSurfaceComposerClient.h"
#include "WestlakeDisplayEventConnection.h"

#include <stdio.h>
#include <string.h>

#include <binder/Binder.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/String8.h>
#include <utils/String16.h>

namespace android {

// AOSP ISurfaceComposer interface descriptor — preserved verbatim so peers
// using BpBinder::getInterfaceDescriptor() see the canonical name (the AOSP-11
// `IMPLEMENT_META_INTERFACE(SurfaceComposer, ...)` value, which our
// surface_smoke / AOSP-11-libgui peers expect).
static const String16 kIfaceDescriptor("android.ui.ISurfaceComposer");
// CR35 §6.6 / §7 §D-B: Android-15 AIDL-generated libgui.so emits this
// descriptor instead.  See onTransact's checkSurfaceComposerInterface for
// the receive-side tolerance.
static const String16 kA15IfaceDescriptor("android.ISurfaceComposer");

// Phase-1 canned display constants (M6_STEP2_REPORT.md §4).  All values are
// what every Android app sees — no per-app branches.
static constexpr uint64_t kPhysicalDisplayId        = 0;
static constexpr int32_t  kCannedWidth              = 1080;
static constexpr int32_t  kCannedHeight             = 2280;
static constexpr float    kCannedXDpi               = 320.0f;
static constexpr float    kCannedYDpi               = 320.0f;
static constexpr float    kCannedRefreshRate        = 60.0f;
static constexpr int64_t  kCannedRefreshPeriodNs    = 16'666'667; // 1e9 / 60
static constexpr int32_t  kCannedActiveConfig       = 0;

// AOSP-11 DisplayInfo struct (frameworks/native/libs/ui/include/ui/DisplayInfo.h)
// is `{DisplayConnectionType, float density, bool secure, std::optional<DeviceProductInfo>}`.
// sizeof varies with libstdc++ version and is not part of any stable ABI; we
// reserve 96 bytes of zeroed payload as a worst-case envelope which the AOSP-11
// `memcpy(info, reply.readInplace(sizeof(DisplayInfo)), sizeof(DisplayInfo))`
// path can consume.  Phase-1 callers receive a default-initialized DisplayInfo
// (internal connection, density 0, secure false, no DeviceProductInfo) —
// graceful degradation per M6 plan §2.2.  Step 5+ may swap to a proper
// Parcelable once framework.jar exposes its read schema.
static constexpr size_t kDisplayInfoWorstCaseBytes = 96;
// AOSP-11 DisplayConfig is `{ui::Size resolution, float xDpi, float yDpi,
// float refreshRate, nsecs_t appVsyncOffset, nsecs_t sfVsyncOffset,
// nsecs_t presentationDeadline, int configGroup}` — `ui::Size` is two ints, so
// fixed at 2*4 + 4 + 4 + 4 + 8 + 8 + 8 + 4 = 48 bytes; round to 64 for safety.
static constexpr size_t kDisplayConfigWorstCaseBytes = 64;
// AOSP-11 DisplayStatInfo is just `{nsecs_t vsyncTime; nsecs_t vsyncPeriod;}`
// = 16 bytes exact.  Stable.
static constexpr size_t kDisplayStatInfoBytes = 16;
// AOSP-11 ui::DisplayState — `{int32 layerStack; ui::Rotation rotation; uint32 w; uint32 h;}`.
// Round to 32 bytes for safety.
static constexpr size_t kDisplayStateWorstCaseBytes = 32;

// ---------------------------------------------------------------------------
// Lifecycle.
// ---------------------------------------------------------------------------

WestlakeSurfaceComposer::WestlakeSurfaceComposer()
    : mPhysicalDisplayToken(new BBinder()) {
    fprintf(stderr,
            "[wlk-sf] WestlakeSurfaceComposer constructed; "
            "physicalDisplayToken=%p\n",
            mPhysicalDisplayToken.get());
}

WestlakeSurfaceComposer::~WestlakeSurfaceComposer() {
    // M6-Step5: stop every IDisplayEventConnection's vsync thread.  Each
    // connection's dtor (WestlakeDisplayEventConnection::~...) also calls
    // stop(), but doing it explicitly here keeps the teardown order
    // deterministic and lets the log show the join sequence clearly.
    std::vector<sp<WestlakeDisplayEventConnection>> conns;
    {
        std::lock_guard<std::mutex> g(mLock);
        conns.swap(mDisplayEventConnections);
    }
    fprintf(stderr,
            "[wlk-sf] WestlakeSurfaceComposer destructing; stopping %zu "
            "IDisplayEventConnection(s)\n",
            conns.size());
    for (auto& c : conns) {
        c->stop();
    }
    fprintf(stderr, "[wlk-sf] WestlakeSurfaceComposer destructed\n");
}

const String16& WestlakeSurfaceComposer::getInterfaceDescriptor() const {
    return kIfaceDescriptor;
}

// ---------------------------------------------------------------------------
// Per-transaction handlers.
// ---------------------------------------------------------------------------

// CREATE_CONNECTION (code 0x02) — framework.jar's SurfaceComposerClient
// constructor calls this; the returned binder is the ISurfaceComposerClient
// proxy that every per-app Surface allocation eventually flows through.
//
// AOSP-11 wire format: in=interface_header, out=writeStrongBinder(client).
status_t WestlakeSurfaceComposer::onCreateConnection(const Parcel& data, Parcel* reply) {
    (void)data;
    fprintf(stderr, "[wlk-sf] CREATE_CONNECTION\n");
    sp<WestlakeSurfaceComposerClient> clientImpl =
            sp<WestlakeSurfaceComposerClient>::make();
    // M6-Step4: thread the daemon-wide DLST pipe path down to the per-client
    // surface factory so each spawned IGraphicBufferProducer can pair with a
    // DlstConsumer that streams pixels to the Compose host SurfaceView.
    clientImpl->setDlstPipePath(mDlstPipePath);
    sp<IBinder> client = clientImpl;
    reply->writeStrongBinder(client);
    return NO_ERROR;
}

// CREATE_DISPLAY_EVENT_CONNECTION (code 0x04) — used by Choreographer for vsync
// subscription.  M6-Step5 wires this to a real WestlakeDisplayEventConnection
// BBinder whose daemon thread emits a 40-byte AOSP-format Vsync event every
// 16.666667 ms once the app calls setVsyncRate(>=1).  The connection's data
// channel (a SOCK_SEQPACKET socketpair) is parceled back via the
// STEAL_RECEIVE_CHANNEL transaction the app issues after binding the strong
// binder we return here.
//
// AOSP-11 wire format:
//   in:  readInt32(vsyncSource), readInt32(configChanged)
//   out: writeStrongBinder(connection)
status_t WestlakeSurfaceComposer::onCreateDisplayEventConnection(const Parcel& data,
                                                                  Parcel* reply) {
    int32_t vsyncSource = data.readInt32();
    int32_t configChanged = data.readInt32();
    sp<WestlakeDisplayEventConnection> connImpl =
            sp<WestlakeDisplayEventConnection>::make();
    connImpl->start();
    sp<IBinder> connBinder = connImpl;
    {
        std::lock_guard<std::mutex> g(mLock);
        mDisplayEventConnections.push_back(connImpl);
    }
    fprintf(stderr,
            "[wlk-sf] CREATE_DISPLAY_EVENT_CONNECTION vsyncSource=%d configChanged=%d "
            "-> binder=%p (60 Hz tick thread started; total live connections=%zu)\n",
            vsyncSource, configChanged, connImpl.get(),
            mDisplayEventConnections.size());
    reply->writeStrongBinder(connBinder);
    return NO_ERROR;
}

// CREATE_DISPLAY (code 0x05) — virtual display.  Phase-1 hands back the same
// synthetic token used for the physical display; sufficient for AOSP-11
// MediaProjectionManager-style flows that just want a non-null token.
//
// AOSP-11 wire format:
//   in:  readString8(displayName), readInt32(secure)
//   out: writeStrongBinder(token)
status_t WestlakeSurfaceComposer::onCreateDisplay(const Parcel& data, Parcel* reply) {
    String8 name = data.readString8();
    int32_t secure = data.readInt32();
    fprintf(stderr, "[wlk-sf] CREATE_DISPLAY name=\"%s\" secure=%d\n",
            name.c_str(), secure);
    // Synthetic token — Phase-1 returns a fresh BBinder per virtual display so
    // peers can compare-by-identity.
    sp<IBinder> token = new BBinder();
    reply->writeStrongBinder(token);
    return NO_ERROR;
}

// DESTROY_DISPLAY (code 0x06) — symmetric to CREATE_DISPLAY.  Phase-1 no-op.
status_t WestlakeSurfaceComposer::onDestroyDisplay(const Parcel& data, Parcel* reply) {
    sp<IBinder> token = data.readStrongBinder();
    (void)reply;
    fprintf(stderr, "[wlk-sf] DESTROY_DISPLAY token=%p (no-op)\n", token.get());
    return NO_ERROR;
}

// GET_PHYSICAL_DISPLAY_TOKEN (code 0x07) — returns the IBinder handle to the
// physical display ID requested.  Subsequent GET_DISPLAY_INFO /
// GET_DISPLAY_STATE / GET_DISPLAY_CONFIGS pass this token back, so identity
// matters — we cache the same BBinder for the lifetime of the daemon and
// return it for every well-known ID (Phase-1 only knows about ID=0).
//
// AOSP-11 wire format:
//   in:  readUint64(displayId)
//   out: writeStrongBinder(token)  // may be null if ID unknown
status_t WestlakeSurfaceComposer::onGetPhysicalDisplayToken(const Parcel& data, Parcel* reply) {
    uint64_t id = data.readUint64();
    fprintf(stderr, "[wlk-sf] GET_PHYSICAL_DISPLAY_TOKEN id=%llu\n",
            (unsigned long long)id);
    if (id == kPhysicalDisplayId) {
        reply->writeStrongBinder(mPhysicalDisplayToken);
    } else {
        // Unknown ID: return null — matches AOSP-11 SurfaceFlinger behaviour
        // for displays it doesn't know about.  Most callers iterate via
        // GET_PHYSICAL_DISPLAY_IDS first, so this branch is cold.
        reply->writeStrongBinder(nullptr);
    }
    return NO_ERROR;
}

// GET_PHYSICAL_DISPLAY_IDS (code 0x23) — enumerate physical displays.
// Phase-1 returns {0} — a single internal display.
//
// AOSP-11 wire format:
//   in:  (none)
//   out: writeUint64Vector(ids)
status_t WestlakeSurfaceComposer::onGetPhysicalDisplayIds(const Parcel& data, Parcel* reply) {
    (void)data;
    fprintf(stderr, "[wlk-sf] GET_PHYSICAL_DISPLAY_IDS -> {0}\n");
    std::vector<uint64_t> ids{kPhysicalDisplayId};
    reply->writeUint64Vector(ids);
    return NO_ERROR;
}

// GET_DISPLAY_INFO (code 0x03) — returns the (mostly-immutable) DisplayInfo
// struct AOSP-11 declares: connectionType, density, secure, deviceProductInfo.
//
// AOSP-11 wire format:
//   in:  readStrongBinder(display)
//   out: writeInt32(status), writeInplace(sizeof(DisplayInfo))
//
// Phase-1 caveat: AOSP-11 DisplayInfo contains a std::optional<DeviceProductInfo>
// whose sizeof is libstdc++-implementation-dependent.  We emit a zeroed
// kDisplayInfoWorstCaseBytes envelope; framework.jar's BpSurfaceComposer
// reads exactly its sizeof(DisplayInfo) bytes from the reply, which our
// envelope is sized to cover.  See WestlakeSurfaceComposer.cpp header comment
// near kDisplayInfoWorstCaseBytes for the rationale.
status_t WestlakeSurfaceComposer::onGetDisplayInfo(const Parcel& data, Parcel* reply) {
    sp<IBinder> display = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_DISPLAY_INFO display=%p\n", display.get());
    reply->writeInt32(NO_ERROR);  // result code
    void* out = reply->writeInplace(kDisplayInfoWorstCaseBytes);
    if (out != nullptr) {
        memset(out, 0, kDisplayInfoWorstCaseBytes);
    }
    return NO_ERROR;
}

// GET_DISPLAY_CONFIGS (code 0x0B) — vector of DisplayConfig.  Phase-1 emits a
// single 1080×2280@60 Hz config.
//
// AOSP-11 wire format:
//   in:  readStrongBinder(display)
//   out: writeInt32(status), writeUint32(count), N * writeInplace(sizeof(DisplayConfig))
//
// Phase-1 caveat: as with DisplayInfo we use a worst-case envelope; the
// individual fields (resolution / refresh rate) are NOT populated because
// AOSP-11's layout is implementation-defined.  Step 5+ will swap to an
// explicit Parcelable once framework.jar exposes its read schema.
status_t WestlakeSurfaceComposer::onGetDisplayConfigs(const Parcel& data, Parcel* reply) {
    sp<IBinder> display = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_DISPLAY_CONFIGS display=%p (count=1, %dx%d@%.0f)\n",
            display.get(), kCannedWidth, kCannedHeight, kCannedRefreshRate);
    reply->writeInt32(NO_ERROR);  // result code
    reply->writeUint32(1);        // one config
    void* out = reply->writeInplace(kDisplayConfigWorstCaseBytes);
    if (out != nullptr) {
        memset(out, 0, kDisplayConfigWorstCaseBytes);
        // Best-effort: populate the front of the envelope with the AOSP-11
        // DisplayConfig layout assuming the compiler-default packing
        // (resolution.width, resolution.height, xDpi, yDpi, refreshRate).
        // If sizeof(DisplayConfig) on the receiver matches this prefix the
        // values flow through; if not, the receiver sees zeros — graceful
        // degradation per Phase-1 contract.
        struct __attribute__((packed)) DisplayConfigPrefix {
            int32_t width;
            int32_t height;
            float   xDpi;
            float   yDpi;
            float   refreshRate;
        };
        DisplayConfigPrefix prefix{kCannedWidth, kCannedHeight, kCannedXDpi,
                                   kCannedYDpi, kCannedRefreshRate};
        memcpy(out, &prefix, sizeof(prefix));
    }
    return NO_ERROR;
}

// GET_ACTIVE_CONFIG (code 0x0C) — int32 index into the GET_DISPLAY_CONFIGS list.
//
// AOSP-11 wire format:
//   in:  readStrongBinder(display)
//   out: writeInt32(activeConfig)
status_t WestlakeSurfaceComposer::onGetActiveConfig(const Parcel& data, Parcel* reply) {
    sp<IBinder> display = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_ACTIVE_CONFIG display=%p -> %d\n",
            display.get(), kCannedActiveConfig);
    reply->writeInt32(kCannedActiveConfig);
    return NO_ERROR;
}

// GET_DISPLAY_STATE (code 0x0D) — ui::DisplayState struct.  Phase-1 zeroed.
//
// AOSP-11 wire format:
//   in:  readStrongBinder(display)
//   out: writeInt32(status), writeInplace(sizeof(ui::DisplayState))
status_t WestlakeSurfaceComposer::onGetDisplayState(const Parcel& data, Parcel* reply) {
    sp<IBinder> display = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_DISPLAY_STATE display=%p\n", display.get());
    reply->writeInt32(NO_ERROR);
    void* out = reply->writeInplace(kDisplayStateWorstCaseBytes);
    if (out != nullptr) {
        memset(out, 0, kDisplayStateWorstCaseBytes);
    }
    return NO_ERROR;
}

// GET_DISPLAY_STATS (code 0x13) — DisplayStatInfo (vsyncTime, vsyncPeriod).
// Layout is stable across Android versions: 2× nsecs_t = 16 bytes.
//
// AOSP-11 wire format:
//   in:  readStrongBinder(display)
//   out: writeInt32(status), writeInplace(sizeof(DisplayStatInfo))
status_t WestlakeSurfaceComposer::onGetDisplayStats(const Parcel& data, Parcel* reply) {
    sp<IBinder> display = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_DISPLAY_STATS display=%p -> vsyncPeriod=%lldns\n",
            display.get(), (long long)kCannedRefreshPeriodNs);
    reply->writeInt32(NO_ERROR);
    struct __attribute__((packed)) DisplayStatInfoLayout {
        int64_t vsyncTime;     // nsecs_t
        int64_t vsyncPeriod;   // nsecs_t
    };
    DisplayStatInfoLayout stats{0, kCannedRefreshPeriodNs};
    void* out = reply->writeInplace(kDisplayStatInfoBytes);
    if (out != nullptr) {
        memcpy(out, &stats, sizeof(stats));
    }
    return NO_ERROR;
}

// SET_TRANSACTION_STATE (code 0x08) — the heaviest method, ~50% of all calls
// once an app starts driving the UI.  M6-Step5 will wire this to the in-
// daemon layer map (M6 plan §3.4 LayerState.cpp).  Phase-1 / Step-2 reads
// nothing and ack's — the framework's BpSurfaceComposer expects no reply
// data, so an empty reply is correct.
status_t WestlakeSurfaceComposer::onSetTransactionState(const Parcel& data, Parcel* reply) {
    (void)data;
    (void)reply;
    fprintf(stderr, "[wlk-sf] SET_TRANSACTION_STATE (Step-2: no-op; Step-5 will wire LayerState)\n");
    return NO_ERROR;
}

// BOOT_FINISHED (code 0x01) — called once at boot by ActivityManagerService;
// in our discovery-wrapper environment it may also fire from the framework
// init path.  Phase-1: log + ack.
status_t WestlakeSurfaceComposer::onBootFinished(const Parcel& data, Parcel* reply) {
    (void)data;
    (void)reply;
    fprintf(stderr, "[wlk-sf] BOOT_FINISHED (ack)\n");
    return NO_ERROR;
}

// ---------------------------------------------------------------------------
// CR35 §7 §D-B: descriptor-tolerant header probe.
// ---------------------------------------------------------------------------
//
// AOSP's CHECK_INTERFACE (defined in IInterface.h §175) reduces to:
//   if (!data.checkInterface(this)) return PERMISSION_DENIED;
// which compares against `this->getInterfaceDescriptor()` only.  CR35 §6.6
// found that A15's AIDL-generated libgui.so writes a different descriptor
// ("android.ISurfaceComposer" — no `ui` segment), so a strict equality check
// rejects every A15 peer.  We replicate Parcel::enforceInterface's wire
// reads (StrictModePolicy, WorkSource, vendor-header, then the descriptor
// string) so the parcel read-position advances identically — peers' first
// `readInt32()` after the header lands on the same byte they expected —
// then accept either descriptor.  Pattern is symmetric to CR11's libbinder
// kHeader receive-tolerance.

bool WestlakeSurfaceComposer::checkSurfaceComposerInterface(const Parcel& data,
                                                            bool* outIsA15) const {
    // Match Parcel::enforceInterface header consumption order
    // (BINDER_WITH_KERNEL_IPC path): StrictModePolicy, WorkSource, kHeader.
    // We intentionally do NOT propagate StrictMode / WorkSource to
    // IPCThreadState because this daemon doesn't expose those semantics —
    // we only need the read-position to advance so handlers see the
    // post-header parcel.
    (void)data.readInt32();   // StrictModePolicy
    (void)data.readInt32();   // WorkSource
    (void)data.readInt32();   // kHeader (libbinder Parcel.cpp §1106-1117 — already
                              //          made tolerant by CR11 patch; we just consume)

    // Interface descriptor — read inline, log, accept either form.
    size_t parcel_interface_len = 0;
    const char16_t* parcel_interface = data.readString16Inplace(&parcel_interface_len);
    if (parcel_interface == nullptr) {
        fprintf(stderr,
                "[wlk-sf] checkSurfaceComposerInterface: null descriptor on wire — REJECT\n");
        return false;
    }
    String8 observed(parcel_interface, parcel_interface_len);

    const bool isAosp11 =
            parcel_interface_len == kIfaceDescriptor.size() &&
            memcmp(parcel_interface, kIfaceDescriptor.c_str(),
                   parcel_interface_len * sizeof(char16_t)) == 0;
    const bool isA15 =
            parcel_interface_len == kA15IfaceDescriptor.size() &&
            memcmp(parcel_interface, kA15IfaceDescriptor.c_str(),
                   parcel_interface_len * sizeof(char16_t)) == 0;

    if (isAosp11) {
        *outIsA15 = false;
        return true;
    }
    if (isA15) {
        *outIsA15 = true;
        fprintf(stderr,
                "[wlk-sf] CR35 §D-B: A15 AIDL descriptor observed (\"%s\") — "
                "routing via dispatchA15Code\n",
                observed.c_str());
        return true;
    }

    fprintf(stderr,
            "[wlk-sf] checkSurfaceComposerInterface: unrecognized descriptor "
            "\"%s\" — REJECT (expected \"%s\" or \"%s\")\n",
            observed.c_str(),
            String8(kIfaceDescriptor).c_str(),
            String8(kA15IfaceDescriptor).c_str());
    return false;
}

// ---------------------------------------------------------------------------
// CR35 §7 §D-C: Android-15 AIDL code → AOSP-11 handler translation.
// ---------------------------------------------------------------------------
//
// Called only when checkSurfaceComposerInterface observed the A15 descriptor.
// The code is an AIDL FIRST_CALL_TRANSACTION+N value from the 75-method
// alphabetical enumeration in CR35 §6.2.  We route the ~17 A15 codes that
// overlap our AOSP-11 Tier-1 / Tier-2 handlers to those same handlers; the
// remaining ~58 codes log + ack with an empty reply (Phase-1 graceful
// degradation contract).
//
// CRITICAL CAVEAT (per CR35 §D-A): the code numbers below are derived from
// the alphabetical ordering of CR35 §6.2's enumerated method names.  M7-Step2
// will strace `dalvikvm` and verify the actual on-wire codes; any mismatch
// is a single-line fix to the A15AidlCode enum in WestlakeSurfaceComposer.h.
// In Phase-1, hitting an unrecognized A15 code produces a log line — never a
// crash — so the ack-everything-else behavior is safe to land before the
// strace audit.

status_t WestlakeSurfaceComposer::dispatchA15Code(uint32_t code,
                                                   const Parcel& data,
                                                   Parcel* reply) {
    switch (code) {
        // Carryover semantics: A15 method has same name + signature as A11 enum.
        case A15_BOOT_FINISHED:
            return onBootFinished(data, reply);
        case A15_CREATE_CONNECTION:
            return onCreateConnection(data, reply);
        case A15_CREATE_DISPLAY_EVENT_CONNECTION:
            return onCreateDisplayEventConnection(data, reply);
        case A15_CREATE_VIRTUAL_DISPLAY:  // A15 rename of A11 CREATE_DISPLAY
            return onCreateDisplay(data, reply);
        case A15_DESTROY_VIRTUAL_DISPLAY:  // A15 rename of A11 DESTROY_DISPLAY
            return onDestroyDisplay(data, reply);
        case A15_GET_PHYSICAL_DISPLAY_TOKEN:
            return onGetPhysicalDisplayToken(data, reply);
        case A15_GET_PHYSICAL_DISPLAY_IDS:
            return onGetPhysicalDisplayIds(data, reply);
        case A15_GET_DISPLAY_STATE:
            return onGetDisplayState(data, reply);
        case A15_GET_DISPLAY_STATS:
            return onGetDisplayStats(data, reply);

        // A15 split of A11 GET_DISPLAY_INFO(3):  Static + Dynamic (per CR35
        // §6.2).  Both are routed to onGetDisplayInfo — our Phase-1 worst-case
        // envelope is 96 bytes of zeros which deserializes safely as either
        // a static-fields struct (immutable: connectionType, density, secure)
        // or a dynamic-fields struct (mutable: activeColorMode, supportedColorModes,
        // displayConfigs, etc.).  When real apps strictly read individual
        // fields the canned-zeroes path falls through to "no data" — same
        // as AOSP-11.
        case A15_GET_STATIC_DISPLAY_INFO:
        case A15_GET_DYNAMIC_DISPLAY_INFO_FROM_TOKEN:
        case A15_GET_DYNAMIC_DISPLAY_INFO_FROM_ID:
            return onGetDisplayInfo(data, reply);

        // A15 has no direct GET_ACTIVE_CONFIG / GET_DISPLAY_CONFIGS — those
        // folded into getDynamicDisplayInfo* above.  Nothing to route here.

        // Tier-2 / Tier-3 A11-rebrandings.  Phase-1 ack-everything covers
        // these even without explicit redirects, but naming them surfaces
        // them in the daemon log.
        case A15_CLEAR_ANIMATION_FRAME_STATS:
        case A15_GET_ANIMATION_FRAME_STATS:
        case A15_GET_DISPLAY_BRIGHTNESS_SUPPORT:
        case A15_GET_DISPLAY_NATIVE_PRIMARIES:
        case A15_GET_SUPPORTED_FRAME_TIMESTAMPS:
        case A15_IS_WIDE_COLOR_DISPLAY:
        case A15_NOTIFY_POWER_BOOST:           // renamed from A11 NOTIFY_POWER_HINT
        case A15_SET_ACTIVE_COLOR_MODE:
        case A15_SET_DISPLAY_BRIGHTNESS:
        case A15_SET_DISPLAY_CONTENT_SAMPLING_ENABLED:
        case A15_SET_GAME_CONTENT_TYPE:
        case A15_SET_GLOBAL_SHADOW_SETTINGS:
        case A15_SET_POWER_MODE:
            fprintf(stderr,
                    "[wlk-sf] A15 Tier-2/3 code=%u acked (Phase-1 no-op safe-default)\n",
                    code);
            return NO_ERROR;

        // Capture (screenshot) family — Tier-3, return BAD_VALUE so framework
        // falls back to default behavior.
        case A15_CAPTURE_DISPLAY:
        case A15_CAPTURE_DISPLAY_BY_ID:
        case A15_CAPTURE_LAYERS:
        case A15_CAPTURE_LAYERS_SYNC:
            fprintf(stderr,
                    "[wlk-sf] A15 capture code=%u — Phase-1 returns BAD_VALUE "
                    "(no screen-capture support)\n",
                    code);
            reply->writeInt32(BAD_VALUE);  // status_t for SafeInterface-style replies
            return NO_ERROR;

        // Net-new A15-only methods (no AOSP-11 equivalent).  Phase-1 ack to
        // avoid crashing app-discovery probes (graceful-degradation contract).
        // The fail-loud Tier-3 audit (§T3-A) is deferred to AFTER one full
        // noice/McD discovery run per CR35 §7.3 — this path produces a log
        // line that lets M7-Step2 see which A15 methods real apps actually
        // exercise.
        default:
            (void)data;
            fprintf(stderr,
                    "[wlk-sf] A15 code=%u not in CR35 §D-C translation table; "
                    "ack with empty reply (Phase-1 graceful)\n",
                    code);
            return NO_ERROR;
    }
}

// ---------------------------------------------------------------------------
// Top-level dispatch.
// ---------------------------------------------------------------------------

status_t WestlakeSurfaceComposer::onTransact(uint32_t code,
                                             const Parcel& data,
                                             Parcel* reply,
                                             uint32_t flags) {
    // CR35 §7 §D-B: verify interface header via the descriptor-tolerant probe
    // (accepts both AOSP-11 "android.ui.ISurfaceComposer" and A15 AIDL
    // "android.ISurfaceComposer").  Replaces the strict CHECK_INTERFACE line
    // that was here in Steps 2-5 and which would have rejected real-app A15
    // traffic with PERMISSION_DENIED in M7-Step2.
    bool isA15 = false;
    if (!checkSurfaceComposerInterface(data, &isA15)) {
        // Mirror CHECK_INTERFACE's PERMISSION_DENIED early-return semantics
        // (IInterface.h §175 — protocol-level reject, not a code dispatch).
        return PERMISSION_DENIED;
    }

    if (isA15) {
        // CR35 §7 §D-C: route to A15 AIDL translation table.
        return dispatchA15Code(code, data, reply);
    }

    // AOSP-11 path (the surface_smoke binary + every existing M6-Step2..5
    // regression).  Unchanged behavior — switch on the AOSP-11 enum.
    switch (code) {
        case BOOT_FINISHED:
            return onBootFinished(data, reply);
        case CREATE_CONNECTION:
            return onCreateConnection(data, reply);
        case GET_DISPLAY_INFO:
            return onGetDisplayInfo(data, reply);
        case CREATE_DISPLAY_EVENT_CONNECTION:
            return onCreateDisplayEventConnection(data, reply);
        case CREATE_DISPLAY:
            return onCreateDisplay(data, reply);
        case DESTROY_DISPLAY:
            return onDestroyDisplay(data, reply);
        case GET_PHYSICAL_DISPLAY_TOKEN:
            return onGetPhysicalDisplayToken(data, reply);
        case SET_TRANSACTION_STATE:
            return onSetTransactionState(data, reply);
        case GET_DISPLAY_CONFIGS:
            return onGetDisplayConfigs(data, reply);
        case GET_ACTIVE_CONFIG:
            return onGetActiveConfig(data, reply);
        case GET_DISPLAY_STATE:
            return onGetDisplayState(data, reply);
        case GET_DISPLAY_STATS:
            return onGetDisplayStats(data, reply);
        case GET_PHYSICAL_DISPLAY_IDS:
            return onGetPhysicalDisplayIds(data, reply);
        default:
            // Non-Tier-1.  Preserve Step-1 behavior: log + ack with empty
            // reply.  Steps 3-6 will promote individual codes here as McD /
            // noice exercise them.  We intentionally do NOT route to
            // BBinder::onTransact — that would route SHELL_COMMAND /
            // INTERFACE queries to BBinder defaults which we'll wire
            // correctly when we own the full surface.
            fprintf(stderr,
                    "[wlk-sf] onTransact code=%u flags=0x%x not in Tier-1; ack with empty reply\n",
                    code, flags);
            return NO_ERROR;
    }
}

}  // namespace android
