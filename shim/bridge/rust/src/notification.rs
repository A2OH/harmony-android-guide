//! JNI bridge: NotificationManager → OH Notification (via C++ shim)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jint;
use std::ffi::CString;

use crate::oh_ffi;

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_notificationPublish(
    mut env: JNIEnv, _class: JClass,
    id: jint, title: JString, text: JString, _channel_id: JString, priority: jint,
) {
    let c_title = jstr_to_c(&mut env, &title);
    let c_text = jstr_to_c(&mut env, &text);
    // Map Android priority to OH slot type (0=OTHER, 1=SOCIAL, 2=SERVICE, 3=CONTENT)
    let slot_type = match priority {
        1 | 2 => 2, // HIGH/MAX → SERVICE_INFORMATION
        _ => 3,     // DEFAULT → CONTENT_INFORMATION
    };
    unsafe { oh_ffi::shim_notification_publish(id, c_title.as_ptr(), c_text.as_ptr(), slot_type) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_notificationCancel(
    _env: JNIEnv, _class: JClass, id: jint,
) {
    unsafe { oh_ffi::shim_notification_cancel(id) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_notificationAddSlot(
    _env: JNIEnv, _class: JClass,
    _channel_id: JString, _channel_name: JString, importance: jint,
) {
    // Map Android importance to OH slot type
    let slot_type = match importance {
        4 => 2, // IMPORTANCE_HIGH → SERVICE_INFORMATION
        3 => 3, // IMPORTANCE_DEFAULT → CONTENT_INFORMATION
        _ => 3,
    };
    unsafe { oh_ffi::shim_notification_add_slot(slot_type) };
}
