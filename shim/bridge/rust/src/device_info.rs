//! JNI bridge: android.os.Build → OH deviceInfo
//!
//! Real NDK: OH_GetBrand() etc. return const char* (static, do NOT free).

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jint;

use crate::oh_ffi;

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_getDeviceBrand<'a>(
    mut env: JNIEnv<'a>, _class: JClass,
) -> JString<'a> {
    let s = unsafe { oh_ffi::static_cstr_to_string(oh_ffi::OH_GetBrand()) };
    env.new_string(&s).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_getDeviceModel<'a>(
    mut env: JNIEnv<'a>, _class: JClass,
) -> JString<'a> {
    let s = unsafe { oh_ffi::static_cstr_to_string(oh_ffi::OH_GetProductModel()) };
    env.new_string(&s).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_getOSVersion<'a>(
    mut env: JNIEnv<'a>, _class: JClass,
) -> JString<'a> {
    let s = unsafe { oh_ffi::static_cstr_to_string(oh_ffi::OH_GetOSFullName()) };
    env.new_string(&s).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_getSDKVersion(
    _env: JNIEnv, _class: JClass,
) -> jint {
    unsafe { oh_ffi::OH_GetSdkApiVersion() }
}
