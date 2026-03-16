//! Permissions bridge: Android checkPermission → OHOS ATM (Access Token Manager).
//!
//! On OHOS, permissions are managed by the Access Token Manager (ATM) service.
//! OH NDK: OH_AT_CheckSelfPermission (since API 12)
//!
//! For the engine, we auto-grant all permissions (the HAP manifest declares them).
//! The JNI bridge is provided for apps that explicitly check.

use jni::objects::{JClass, JString};
use jni::sys::jint;
use jni::JNIEnv;

/// OHBridge.checkPermission(String permission) → int
/// Returns 0 (PERMISSION_GRANTED) for all permissions.
/// On real OHOS, would call OH_AT_CheckSelfPermission or the C++ shim.
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_checkPermission(
    mut env: JNIEnv,
    _class: JClass,
    permission: JString,
) -> jint {
    // Log the permission check for debugging
    if let Ok(perm) = env.get_string(&permission) {
        let perm_str: String = perm.into();
        log::debug!("checkPermission: {} → GRANTED", perm_str);
    }
    0 // PERMISSION_GRANTED
}
