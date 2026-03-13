//! JNI bridge: Toast → OH promptAction (via C++ shim / NAPI)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jint;
use std::ffi::CString;

use crate::oh_ffi;

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_showToast(
    mut env: JNIEnv, _class: JClass, message: JString, duration: jint,
) {
    let rs: String = env.get_string(&message).expect("Invalid JString").into();
    let c_msg = CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap());
    unsafe { oh_ffi::shim_show_toast(c_msg.as_ptr(), duration) };
}
