//! JNI bridge: android.util.Log → OH hilog (OH_LOG_Print)
//!
//! Real signature: OH_LOG_Print(LogType, LogLevel, domain, tag, fmt, ...)
//! We call it with fmt="%{public}s" and pass the message as the single vararg.

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use std::ffi::CString;

use crate::oh_ffi::{self, LogLevel, LogType};

const DOMAIN: u32 = 0xFF00; // shim log domain
static FMT: &[u8] = b"%{public}s\0"; // OH format string — {public} makes it visible in hilog

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("(invalid)").unwrap())
}

fn log_print(level: LogLevel, tag: &CString, msg: &CString) {
    let fmt = FMT.as_ptr() as *const std::os::raw::c_char;
    unsafe {
        oh_ffi::OH_LOG_Print(LogType::LOG_APP, level, DOMAIN, tag.as_ptr(), fmt, msg.as_ptr());
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_logDebug(
    mut env: JNIEnv, _class: JClass, tag: JString, msg: JString,
) {
    let c_tag = jstr_to_c(&mut env, &tag);
    let c_msg = jstr_to_c(&mut env, &msg);
    log_print(LogLevel::LOG_DEBUG, &c_tag, &c_msg);
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_logInfo(
    mut env: JNIEnv, _class: JClass, tag: JString, msg: JString,
) {
    let c_tag = jstr_to_c(&mut env, &tag);
    let c_msg = jstr_to_c(&mut env, &msg);
    log_print(LogLevel::LOG_INFO, &c_tag, &c_msg);
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_logWarn(
    mut env: JNIEnv, _class: JClass, tag: JString, msg: JString,
) {
    let c_tag = jstr_to_c(&mut env, &tag);
    let c_msg = jstr_to_c(&mut env, &msg);
    log_print(LogLevel::LOG_WARN, &c_tag, &c_msg);
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_logError(
    mut env: JNIEnv, _class: JClass, tag: JString, msg: JString,
) {
    let c_tag = jstr_to_c(&mut env, &tag);
    let c_msg = jstr_to_c(&mut env, &msg);
    log_print(LogLevel::LOG_ERROR, &c_tag, &c_msg);
}
