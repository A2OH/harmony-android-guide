//! Vibrator bridge: Android Vibrator → OHOS vibrator service.
//!
//! OHOS vibrator API (C NDK, since API 11):
//!   OH_Vibrator_PlayVibration(int32_t duration, OH_Vibrator_Usage usage)
//!   OH_Vibrator_StopVibration()
//!   OH_Vibrator_IsSupportVibrator() → bool
//!
//! Header: sensors/oh_vibrator.h
//! Library: libohvibrator.z.so

use jni::objects::JClass;
use jni::sys::{jboolean, jlong, JNI_TRUE};
use jni::JNIEnv;

// OH vibrator FFI (when available)
// extern "C" {
//     fn OH_Vibrator_IsSupportVibrator() -> bool;
//     fn OH_Vibrator_PlayVibration(duration: i32, usage: i32) -> i32;
//     fn OH_Vibrator_StopVibration() -> i32;
// }

/// OHBridge.vibratorHasVibrator() → boolean
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_vibratorHasVibrator(
    _env: JNIEnv,
    _class: JClass,
) -> jboolean {
    // On real OHOS: OH_Vibrator_IsSupportVibrator()
    // For now, return true (most devices have vibrators)
    JNI_TRUE as jboolean
}

/// OHBridge.vibratorVibrate(long milliseconds)
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_vibratorVibrate(
    _env: JNIEnv,
    _class: JClass,
    milliseconds: jlong,
) {
    // On real OHOS: OH_Vibrator_PlayVibration(milliseconds as i32, 0 /* USAGE_UNKNOWN */)
    log::debug!("vibrate: {}ms", milliseconds);
}

/// OHBridge.vibratorCancel()
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_vibratorCancel(
    _env: JNIEnv,
    _class: JClass,
) {
    // On real OHOS: OH_Vibrator_StopVibration()
    log::debug!("vibrator cancel");
}
