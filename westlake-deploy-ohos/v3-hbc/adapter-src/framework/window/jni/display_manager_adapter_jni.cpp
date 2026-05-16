// ============================================================================
// display_manager_adapter_jni.cpp
//
// JNI bridge for adapter.window.DisplayManagerAdapter → OH Rosen DisplayManager.
// Compiled into liboh_adapter_bridge.so; loaded by OHEnvironment static init.
//
// Authoritative spec: doc/window_manager_ipc_adapter_design.html §1.1
//
// Uses raw JNI naming (no RegisterNatives) — symbol names match Java side
// exactly, matched by ART at first call.  Methods are non-instance / non-CRITICAL,
// regular ABI: (JNIEnv*, jclass).
//
// All accessors are guarded against null sptr<Display> to avoid SEGV during
// early-boot adapter init when OH DisplayManagerService may not be ready.
// ============================================================================

#include <jni.h>
#include <android/log.h>
#include <vector>

#include "display_manager.h"
#include "display.h"
#include "display_info.h"
#include "dm_common.h"
#include "cutout_info.h"

#define LOG_TAG "OH_DisplayMgrAdapter"
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace {
constexpr int kDefaultWidth        = 1280;
constexpr int kDefaultHeight       = 720;
constexpr int kDefaultRefreshRate  = 60;
constexpr int kDefaultDpi          = 320;
constexpr float kDefaultDensity    = 2.0f;

OHOS::sptr<OHOS::Rosen::Display> getDisplay() {
    auto display = OHOS::Rosen::DisplayManager::GetInstance().GetDefaultDisplay();
    if (!display) {
        LOGW("OH GetDefaultDisplay() returned null");
    }
    return display;
}

// Resolve OH DisplayInfo via cache to avoid Binder churn on the hot
// setContentView/Choreographer path.  Returns null on transient failure.
OHOS::sptr<OHOS::Rosen::DisplayInfo> getDisplayInfo() {
    auto d = getDisplay();
    if (!d) return nullptr;
    auto info = d->GetDisplayInfoWithCache();
    if (!info) {
        LOGW("OH Display::GetDisplayInfoWithCache() returned null");
    }
    return info;
}
}  // namespace

extern "C" {

JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayWidth(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jint>(d->GetWidth()) : kDefaultWidth;
}

JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayHeight(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jint>(d->GetHeight()) : kDefaultHeight;
}

JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayRefreshRate(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jint>(d->GetRefreshRate()) : kDefaultRefreshRate;
}

JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayDpi(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jint>(d->GetDpi()) : kDefaultDpi;
}

JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayRotation(JNIEnv*, jclass) {
    auto d = getDisplay();
    if (!d) return 0;
    // OH Rosen::Rotation enum values 0..3 align with Android Surface.ROTATION_*.
    return static_cast<jint>(static_cast<uint32_t>(d->GetRotation()));
}

JNIEXPORT jfloat JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayXDpi(JNIEnv*, jclass) {
    auto d = getDisplay();
    // OH Display only exposes a single dpi; reuse for both x/y axes.
    return d ? static_cast<jfloat>(d->GetDpi()) : static_cast<jfloat>(kDefaultDpi);
}

JNIEXPORT jfloat JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayYDpi(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jfloat>(d->GetDpi()) : static_cast<jfloat>(kDefaultDpi);
}

JNIEXPORT jfloat JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDefaultDisplayDensity(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jfloat>(d->GetVirtualPixelRatio()) : kDefaultDensity;
}

// ---------------------------------------------------------------------------
// G2.0 (window_manager_ipc_adapter_design §1.1.5.2) — fields beyond the
// basic 8 above so DisplayInfo can be populated completely enough for
// setContentView / findMode / Choreographer to not throw.
// ---------------------------------------------------------------------------

// Returns supportedRefreshRate vector as int[].  Empty array on failure;
// Java side falls back to [current refresh rate] in that case.
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetSupportedRefreshRates(JNIEnv* env, jclass) {
    std::vector<uint32_t> rates;
    auto info = getDisplayInfo();
    if (info) rates = info->GetSupportedRefreshRate();
    jintArray arr = env->NewIntArray(static_cast<jsize>(rates.size()));
    if (!rates.empty() && arr) {
        std::vector<jint> tmp(rates.begin(), rates.end());
        env->SetIntArrayRegion(arr, 0, static_cast<jsize>(tmp.size()), tmp.data());
    }
    return arr;
}

// Returns availableArea as int[4] = {x, y, width, height}.  Falls back to
// {0, 0, displayWidth, displayHeight} if OH GetAvailableArea fails.
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetAvailableArea(JNIEnv* env, jclass) {
    int x = 0, y = 0, w = 0, h = 0;
    auto d = getDisplay();
    if (d) {
        OHOS::Rosen::DMRect rect;
        if (d->GetAvailableArea(rect) == OHOS::Rosen::DMError::DM_OK) {
            x = rect.posX_;  y = rect.posY_;
            w = static_cast<int>(rect.width_);  h = static_cast<int>(rect.height_);
        } else {
            w = d->GetWidth();  h = d->GetHeight();
        }
    } else {
        w = kDefaultWidth;  h = kDefaultHeight;
    }
    jintArray arr = env->NewIntArray(4);
    if (arr) {
        jint vals[4] = { x, y, w, h };
        env->SetIntArrayRegion(arr, 0, 4, vals);
    }
    return arr;
}

// Returns Android Display.STATE_* equivalent.
//   STATE_UNKNOWN=0  STATE_OFF=1  STATE_ON=2  STATE_DOZE=3  STATE_DOZE_SUSPEND=4
//   STATE_VR=5  STATE_ON_SUSPEND=6
JNIEXPORT jint JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDisplayState(JNIEnv*, jclass) {
    auto info = getDisplayInfo();
    if (!info) return 0;  // STATE_UNKNOWN
    auto s = info->GetDisplayState();
    using OHOS::Rosen::DisplayState;
    switch (s) {
        case DisplayState::ON:      return 2;  // STATE_ON
        case DisplayState::OFF:     return 1;  // STATE_OFF
        case DisplayState::DOZE:    return 3;
        case DisplayState::DOZE_SUSPEND: return 4;
        case DisplayState::VR:      return 5;
        case DisplayState::ON_SUSPEND:   return 6;
        case DisplayState::UNKNOWN:
        default:                    return 0;
    }
}

// Returns OH Display 64-bit ID (truncated to long for Java).
JNIEXPORT jlong JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetDisplayId(JNIEnv*, jclass) {
    auto d = getDisplay();
    return d ? static_cast<jlong>(d->GetId()) : 0L;
}

// Returns OH supported color spaces as int[].  Java side uses these to
// populate DisplayInfo.supportedColorModes (or falls back to [DEFAULT]).
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetSupportedColorSpaces(JNIEnv* env, jclass) {
    std::vector<uint32_t> colorSpaces;
    auto d = getDisplay();
    if (d) (void)d->GetSupportedColorSpaces(colorSpaces);
    jintArray arr = env->NewIntArray(static_cast<jsize>(colorSpaces.size()));
    if (!colorSpaces.empty() && arr) {
        std::vector<jint> tmp(colorSpaces.begin(), colorSpaces.end());
        env->SetIntArrayRegion(arr, 0, static_cast<jsize>(tmp.size()), tmp.data());
    }
    return arr;
}

// Returns OH supported HDR formats as int[].
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetSupportedHdrFormats(JNIEnv* env, jclass) {
    std::vector<uint32_t> hdrFormats;
    auto d = getDisplay();
    if (d) (void)d->GetSupportedHDRFormats(hdrFormats);
    jintArray arr = env->NewIntArray(static_cast<jsize>(hdrFormats.size()));
    if (!hdrFormats.empty() && arr) {
        std::vector<jint> tmp(hdrFormats.begin(), hdrFormats.end());
        env->SetIntArrayRegion(arr, 0, static_cast<jsize>(tmp.size()), tmp.data());
    }
    return arr;
}

// Returns rounded corner radii as int[4] = {topLeft, topRight, bottomLeft, bottomRight}.
// Empty (or zero-radius) result when display has no rounded corners.
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetRoundedCorners(JNIEnv* env, jclass) {
    int radii[4] = { 0, 0, 0, 0 };
    auto d = getDisplay();
    if (d) {
        std::vector<OHOS::Rosen::RoundedCorner> corners;
        if (d->GetRoundedCorner(corners) == OHOS::Rosen::DMError::DM_OK) {
            for (auto& c : corners) {
                int r = static_cast<int>(c.radius);
                switch (c.type) {
                    case OHOS::Rosen::CornerType::TOP_LEFT:     radii[0] = r; break;
                    case OHOS::Rosen::CornerType::TOP_RIGHT:    radii[1] = r; break;
                    case OHOS::Rosen::CornerType::BOTTOM_LEFT:  radii[2] = r; break;
                    case OHOS::Rosen::CornerType::BOTTOM_RIGHT: radii[3] = r; break;
                    default: break;
                }
            }
        }
    }
    jintArray arr = env->NewIntArray(4);
    if (arr) env->SetIntArrayRegion(arr, 0, 4, radii);
    return arr;
}

// Returns cutout bounding rects flattened as int[N*4] = (x,y,w,h)*N.
// Empty array when display has no cutout.
JNIEXPORT jintArray JNICALL
Java_adapter_window_DisplayManagerAdapter_nativeGetCutoutBoundingRects(JNIEnv* env, jclass) {
    std::vector<jint> flat;
    auto d = getDisplay();
    if (d) {
        auto cutout = d->GetCutoutInfo();
        if (cutout) {
            for (const auto& r : cutout->GetBoundingRects()) {
                flat.push_back(r.posX_);
                flat.push_back(r.posY_);
                flat.push_back(static_cast<jint>(r.width_));
                flat.push_back(static_cast<jint>(r.height_));
            }
        }
    }
    jintArray arr = env->NewIntArray(static_cast<jsize>(flat.size()));
    if (!flat.empty() && arr) {
        env->SetIntArrayRegion(arr, 0, static_cast<jsize>(flat.size()), flat.data());
    }
    return arr;
}

}  // extern "C"
