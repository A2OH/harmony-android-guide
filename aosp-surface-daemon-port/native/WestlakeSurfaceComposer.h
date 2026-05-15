// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 2: Tier-1 transaction dispatch)
//
// WestlakeSurfaceComposer is the BBinder that the daemon registers with our
// AOSP servicemanager under the canonical AOSP name "SurfaceFlinger".  Step 1
// landed a stub that ack'd every transaction with NO_ERROR + empty reply.
// Step 2 (this CR) implements the Tier-1 ISurfaceComposer surface listed in
// M6_SURFACE_DAEMON_PLAN.md §2.2 — every transaction code framework.jar's
// BpSurfaceComposer can plausibly invoke during app discovery is routed to
// a per-code handler.  Non-Tier-1 codes fall through to the Step-1 NO_ERROR
// stub (logged-and-ack'd) so future steps can promote them incrementally.
//
// Transaction-code enum values mirror AOSP-11's BnSurfaceComposer::
// ISurfaceComposerTag (frameworks/native/libs/gui/include/gui/ISurfaceComposer.h
// §549-603), the version of the gui interface that matches the
// "android.ui.ISurfaceComposer" descriptor framework.jar has shipped with
// since Android 7 and which is unchanged through Android 15 modulo
// appended enumerators (which only matters for Tier-2/3 codes).  See
// docs/engine/M6_STEP2_REPORT.md §3 for the cross-reference.
//
// Companion: docs/engine/M6_SURFACE_DAEMON_PLAN.md
//             docs/engine/M6_STEP1_REPORT.md (skeleton)
//             docs/engine/M6_STEP2_REPORT.md (this CR)
//
// Anti-drift: NO per-app branches.  Every Android app sees the exact same
// canned 1080×2280@60Hz display — that's a property of the daemon, not the
// caller.
#ifndef WESTLAKE_SURFACE_COMPOSER_H
#define WESTLAKE_SURFACE_COMPOSER_H

#include <mutex>
#include <string>
#include <vector>

#include <binder/Binder.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/RefBase.h>

namespace android {

class WestlakeDisplayEventConnection;

class WestlakeSurfaceComposer : public BBinder {
public:
    WestlakeSurfaceComposer();
    ~WestlakeSurfaceComposer() override;

    // BBinder override.  Step-2 dispatch switch — see M6_STEP2_REPORT.md §3.
    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    // Identification string returned to BBinder::getInterfaceDescriptor() so
    // peers that call BpBinder::getInterfaceDescriptor() get the canonical
    // AOSP descriptor.  ISurfaceComposer's AOSP-11 descriptor is the literal
    // string "android.ui.ISurfaceComposer"; CR35 §6.6 found that A15's AIDL-
    // generated `libgui.so` dropped the `ui` segment (the C++ mangled symbol
    // is `android::ISurfaceComposer::descriptor`, not `android::ui::...`).
    // We return the AOSP-11 form because the M6 surface_smoke binary links
    // against AOSP-11 libgui; the receive side (see onTransact) is widened
    // to accept either descriptor on the wire — see CR35 §7 §D-B.
    const String16& getInterfaceDescriptor() const override;

    // M6-Step4: install the DLST consumer pipe path for every subsequent
    // CREATE_CONNECTION → CREATE_SURFACE.  Called once at startup by
    // surfaceflinger_main after resolving $WESTLAKE_DLST_PIPE.
    void setDlstPipePath(std::string path) { mDlstPipePath = std::move(path); }

    // AOSP-11 BnSurfaceComposer::ISurfaceComposerTag transaction codes,
    // verbatim.  See header note above for cross-version stability.
    //
    // FIRST_CALL_TRANSACTION is 0x00000001 per IBinder::FIRST_CALL_TRANSACTION
    // — these codes are what flows over the wire from BpSurfaceComposer.
    enum Tag : uint32_t {
        BOOT_FINISHED                    = 1,   // 0x01
        CREATE_CONNECTION                = 2,   // 0x02
        GET_DISPLAY_INFO                 = 3,   // 0x03
        CREATE_DISPLAY_EVENT_CONNECTION  = 4,   // 0x04
        CREATE_DISPLAY                   = 5,   // 0x05
        DESTROY_DISPLAY                  = 6,   // 0x06
        GET_PHYSICAL_DISPLAY_TOKEN       = 7,   // 0x07
        SET_TRANSACTION_STATE            = 8,   // 0x08
        AUTHENTICATE_SURFACE             = 9,
        GET_SUPPORTED_FRAME_TIMESTAMPS   = 10,
        GET_DISPLAY_CONFIGS              = 11,  // 0x0B
        GET_ACTIVE_CONFIG                = 12,  // 0x0C
        GET_DISPLAY_STATE                = 13,  // 0x0D
        CAPTURE_SCREEN                   = 14,
        CAPTURE_LAYERS                   = 15,
        CLEAR_ANIMATION_FRAME_STATS      = 16,
        GET_ANIMATION_FRAME_STATS        = 17,
        SET_POWER_MODE                   = 18,
        GET_DISPLAY_STATS                = 19,  // 0x13
        GET_HDR_CAPABILITIES             = 20,
        GET_DISPLAY_COLOR_MODES          = 21,
        GET_ACTIVE_COLOR_MODE            = 22,
        SET_ACTIVE_COLOR_MODE            = 23,
        ENABLE_VSYNC_INJECTIONS          = 24,
        INJECT_VSYNC                     = 25,
        GET_LAYER_DEBUG_INFO             = 26,
        GET_COMPOSITION_PREFERENCE       = 27,
        GET_COLOR_MANAGEMENT             = 28,
        GET_DISPLAYED_CONTENT_SAMPLING_ATTRIBUTES = 29,
        SET_DISPLAY_CONTENT_SAMPLING_ENABLED      = 30,
        GET_DISPLAYED_CONTENT_SAMPLE     = 31,
        GET_PROTECTED_CONTENT_SUPPORT    = 32,
        IS_WIDE_COLOR_DISPLAY            = 33,
        GET_DISPLAY_NATIVE_PRIMARIES     = 34,
        GET_PHYSICAL_DISPLAY_IDS         = 35,  // 0x23
        ADD_REGION_SAMPLING_LISTENER     = 36,
        REMOVE_REGION_SAMPLING_LISTENER  = 37,
        SET_DESIRED_DISPLAY_CONFIG_SPECS = 38,
        GET_DESIRED_DISPLAY_CONFIG_SPECS = 39,
        GET_DISPLAY_BRIGHTNESS_SUPPORT   = 40,
        SET_DISPLAY_BRIGHTNESS           = 41,
        CAPTURE_SCREEN_BY_ID             = 42,
        NOTIFY_POWER_HINT                = 43,
        SET_GLOBAL_SHADOW_SETTINGS       = 44,
        GET_AUTO_LOW_LATENCY_MODE_SUPPORT= 45,
        SET_AUTO_LOW_LATENCY_MODE        = 46,
        GET_GAME_CONTENT_TYPE_SUPPORT    = 47,
        SET_GAME_CONTENT_TYPE            = 48,
        SET_FRAME_RATE                   = 49,
        ACQUIRE_FRAME_RATE_FLEXIBILITY_TOKEN = 50,
    };

    // CR35 §7 §D-C: small Android-15-AIDL → AOSP-11 code translation table.
    //
    // The CR35 phone-artifact extraction (libgui.so on cfb7c9e3) showed that
    // A15 has migrated ISurfaceComposer to AIDL-generated dispatch with 75
    // alphabetically-ordered methods.  AIDL assigns FIRST_CALL_TRANSACTION
    // codes in the order they appear in the .aidl source — which for the A15
    // ISurfaceComposer.aidl is alphabetical.  We hand-encode the codes for
    // the small subset of A15 methods that overlap our AOSP-11 Tier-1
    // handlers and route them to those same handlers.  Methods we don't
    // overlap fall through to the unhandled-code path (logged + ack'd) per
    // the Phase-1 graceful-degradation contract.
    //
    // CRITICAL: the A15 codes below are derived from CR35 §6.2's enumerated
    // 75 names.  Code 1 is the first method alphabetically (`addFpsListener`)
    // and so on.  When M7-Step2 wires real apps and `strace` confirms (or
    // contradicts) any code, the table below is the single point of fix.
    enum A15AidlCode : uint32_t {
        // The CR35-decoded alphabetical ordering of all 75 A15 ISurfaceComposer
        // methods, starting at 1.  Only the codes our daemon RECOGNIZES are
        // named here; others (listeners, jank/fps/window-infos, schedule*,
        // notifyShutdown, onPullAtom, override*, set*, get* for HDR/picture
        // capabilities, etc.) are unhandled-fall-through.
        //
        // Net new in A15 with no A11 equivalent (logged-and-acked):
        //   1 addFpsListener,           2 addHdrLayerInfoListener,
        //   3 addJankListener,          4 addRegionSamplingListener,
        //   5 addTunnelModeEnabledListener,  6 addWindowInfosListener,
        // Carryover-AOSP11-equivalent block:
        A15_BOOT_FINISHED                =  7,  // bootFinished — AOSP11 code 1
        A15_CAPTURE_DISPLAY              =  8,  // captureDisplay — closest A11 was CAPTURE_SCREEN(14)
        A15_CAPTURE_DISPLAY_BY_ID        =  9,  // captureDisplayById — closest A11 was CAPTURE_SCREEN_BY_ID(42)
        A15_CAPTURE_LAYERS               = 10,  // captureLayers — A11 code 15
        A15_CAPTURE_LAYERS_SYNC          = 11,  // captureLayersSync — new in A12+
        A15_CLEAR_ANIMATION_FRAME_STATS  = 12,  // clearAnimationFrameStats — A11 code 16
        A15_CLEAR_BOOT_DISPLAY_MODE      = 13,  // clearBootDisplayMode — new
        A15_CREATE_CONNECTION            = 14,  // createConnection — A11 code 2
        A15_CREATE_DISPLAY_EVENT_CONNECTION = 15,  // createDisplayEventConnection — A11 code 4
        A15_CREATE_VIRTUAL_DISPLAY       = 16,  // createVirtualDisplay — replaces A11 CREATE_DISPLAY(5)
        A15_DESTROY_VIRTUAL_DISPLAY      = 17,  // destroyVirtualDisplay — replaces A11 DESTROY_DISPLAY(6)
        // Methods 18..21 enableRefreshRateOverlay, flushJankData,
        // forceClientComposition, getAnimationFrameStats — net-new or
        // unhandled in Phase-1.
        A15_GET_ANIMATION_FRAME_STATS    = 21,  // A11 code 17
        // Methods 22..25 getBootDisplayMode*, getCompositionPreference,
        // getDesiredDisplayModeSpecs — unhandled.
        A15_GET_DISPLAY_BRIGHTNESS_SUPPORT = 26,  // A11 code 40
        A15_GET_DISPLAY_DECORATION_SUPPORT = 27,  // new
        A15_GET_DISPLAY_NATIVE_PRIMARIES = 28,  // A11 code 34
        A15_GET_DISPLAY_STATE            = 29,  // getDisplayState — A11 code 13
        A15_GET_DISPLAY_STATS            = 30,  // getDisplayStats — A11 code 19
        // Methods 31..34 getDisplayedContent*, getDynamicDisplayInfo* —
        // 31..33 unhandled; getDynamicDisplayInfoFromToken merges A11 codes
        // 11(GET_DISPLAY_CONFIGS) + 12(GET_ACTIVE_CONFIG) + 21(GET_DISPLAY_COLOR_MODES)
        // + 22(GET_ACTIVE_COLOR_MODE) — we route to GET_DISPLAY_CONFIGS.
        A15_GET_DYNAMIC_DISPLAY_INFO_FROM_TOKEN = 33,
        A15_GET_DYNAMIC_DISPLAY_INFO_FROM_ID    = 34,
        // Methods 35..43 getGpuContextPriority, getHdr*, getMax*,
        // getOverlaySupport, getPhysicalDisplayIds, etc.
        A15_GET_PHYSICAL_DISPLAY_IDS     = 41,  // A11 code 35
        A15_GET_PHYSICAL_DISPLAY_TOKEN   = 42,  // A11 code 7
        // Methods 43..46 getProtected*, getSchedulingPolicy, getStalled*,
        // getStaticDisplayInfo — getStaticDisplayInfo is the A15 split of
        // A11 GET_DISPLAY_INFO(3); route to onGetDisplayInfo.
        A15_GET_STATIC_DISPLAY_INFO      = 46,
        A15_GET_SUPPORTED_FRAME_TIMESTAMPS = 47,  // A11 code 10
        A15_IS_WIDE_COLOR_DISPLAY        = 48,  // A11 code 33
        A15_NOTIFY_POWER_BOOST           = 49,  // renamed from A11 NOTIFY_POWER_HINT(43)
        // Methods 50..54 notifyShutdown, onPullAtom, overrideHdrTypes,
        // removeFpsListener, removeHdrLayerInfoListener — net-new / unhandled.
        // Methods 55..60 removeJank/Region/Tunnel/WindowInfosListener,
        // scheduleCommit, scheduleComposite — net-new (we just ack).
        A15_SET_ACTIVE_COLOR_MODE        = 61,  // A11 code 23
        // setActivePictureListener, setAutoLowLatencyMode, setBootDisplayMode,
        // setDebugFlash, setDesiredDisplayModeSpecs — 62..66 unhandled.
        A15_SET_DISPLAY_BRIGHTNESS       = 67,  // A11 code 41
        A15_SET_DISPLAY_CONTENT_SAMPLING_ENABLED = 68,  // A11 code 30
        A15_SET_GAME_CONTENT_TYPE        = 69,  // A11 code 48
        // setGameDefault/Mode frame rate, setGlobalShadowSettings — 70..72.
        A15_SET_GLOBAL_SHADOW_SETTINGS   = 72,  // A11 code 44
        A15_SET_HDR_CONVERSION_STRATEGY  = 73,  // net-new (we ack)
        A15_SET_POWER_MODE               = 74,  // A11 code 18
        A15_SET_SMALL_AREA_DETECTION_THRESHOLD = 75,  // net-new
    };

private:
    // Per-transaction handlers — each is responsible for verifying the
    // interface header (via CHECK_INTERFACE) and writing a well-formed reply.
    status_t onCreateConnection(const Parcel& data, Parcel* reply);
    status_t onCreateDisplayEventConnection(const Parcel& data, Parcel* reply);
    status_t onCreateDisplay(const Parcel& data, Parcel* reply);
    status_t onDestroyDisplay(const Parcel& data, Parcel* reply);
    status_t onGetPhysicalDisplayToken(const Parcel& data, Parcel* reply);
    status_t onGetPhysicalDisplayIds(const Parcel& data, Parcel* reply);
    status_t onGetDisplayInfo(const Parcel& data, Parcel* reply);
    status_t onGetDisplayConfigs(const Parcel& data, Parcel* reply);
    status_t onGetActiveConfig(const Parcel& data, Parcel* reply);
    status_t onGetDisplayState(const Parcel& data, Parcel* reply);
    status_t onGetDisplayStats(const Parcel& data, Parcel* reply);
    status_t onSetTransactionState(const Parcel& data, Parcel* reply);
    status_t onBootFinished(const Parcel& data, Parcel* reply);

    // CR35 §7 §D-B: descriptor-tolerant header probe.  Reads the interface
    // descriptor written at the head of `data` (which CHECK_INTERFACE would
    // also have consumed), logs it, and accepts either the AOSP-11 form
    // (`android.ui.ISurfaceComposer`) or the A15 AIDL form
    // (`android.ISurfaceComposer`).  On accept, stores which variant was
    // observed in `*outIsA15` (true = A15 AIDL) so the caller can route to
    // the matching dispatcher.  Returns true on accept, false on reject;
    // callers should mirror CHECK_INTERFACE's PERMISSION_DENIED early-return
    // semantics on false.  Pattern is symmetric with CR11's libbinder kHeader
    // receive-tolerance in aosp-libbinder-port/aosp-src/libbinder/Parcel.cpp
    // §enforceInterface — receive widely, send canonically.
    bool checkSurfaceComposerInterface(const Parcel& data, bool* outIsA15) const;

    // CR35 §7 §D-C: dispatch by A15 AIDL transaction code.  Called from
    // onTransact's default branch only when checkSurfaceComposerInterface
    // observed the A15 descriptor — at which point the code is an AIDL-
    // generated FIRST_CALL_TRANSACTION+N value from the 75-method enumeration
    // in CR35 §6.2.  Returns NO_ERROR after dispatching (matches Phase-1
    // graceful-degradation contract — even unrecognized A15 codes are
    // logged-and-ack'd unless they're catastrophic Tier-1).
    status_t dispatchA15Code(uint32_t code, const Parcel& data, Parcel* reply);

    // Canned-display backing state.  Phase-1 hardcoded 1080×2280@60Hz, 320dpi
    // (OnePlus 6 native panel).  All apps see the same display — no per-app
    // branches.  See M6_STEP2_REPORT.md §4.
    //
    // mPhysicalDisplayToken is the BBinder we return to GET_PHYSICAL_DISPLAY_TOKEN
    // and recognize on subsequent calls (CREATE_DISPLAY / GET_DISPLAY_INFO /
    // GET_DISPLAY_STATE) — it must be a stable identity so peers can correlate.
    sp<IBinder> mPhysicalDisplayToken;
    std::string mDlstPipePath;

    // M6-Step5: keep every IDisplayEventConnection alive for the lifetime
    // of the daemon (or until the corresponding BpDisplayEventConnection
    // proxy is dropped — in Phase-1 we don't observe that signal, so we
    // simply leak until the daemon exits, which is acceptable for the
    // single-foreground-app assumption).  Protected by mLock so multiple
    // CREATE_DISPLAY_EVENT_CONNECTION transactions arriving on parallel
    // binder threads serialize cleanly.
    std::mutex mLock;
    std::vector<sp<WestlakeDisplayEventConnection>> mDisplayEventConnections;
};

}  // namespace android

#endif  // WESTLAKE_SURFACE_COMPOSER_H
