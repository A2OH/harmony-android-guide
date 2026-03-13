//! JNI bridge: AlarmManager → OH reminder (via C++ shim)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jint;
use std::ffi::CString;

use crate::oh_ffi;

fn nullable_jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    if s.is_null() {
        CString::new("").unwrap()
    } else {
        let rs: String = env.get_string(s).expect("Invalid JString").into();
        CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_reminderScheduleTimer(
    mut env: JNIEnv, _class: JClass,
    delay_seconds: jint, title: JString, content: JString,
    target_ability: JString, _params_json: JString,
) -> jint {
    let c_title = nullable_jstr_to_c(&mut env, &title);
    let c_content = nullable_jstr_to_c(&mut env, &content);
    let c_target = nullable_jstr_to_c(&mut env, &target_ability);
    // bundle_name is the current app — get from context or hardcode
    let c_bundle = CString::new("com.example.flashnote").unwrap();
    unsafe {
        oh_ffi::shim_reminder_publish_timer(
            delay_seconds, c_title.as_ptr(), c_content.as_ptr(),
            c_bundle.as_ptr(), c_target.as_ptr(),
        )
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_reminderCancel(
    _env: JNIEnv, _class: JClass, reminder_id: jint,
) {
    unsafe { oh_ffi::shim_reminder_cancel(reminder_id) };
}
