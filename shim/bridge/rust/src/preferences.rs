//! JNI bridge: SharedPreferences shim → OH Preferences (via C++ shim)
//!
//! The actual OH Preferences API is C++ only (no C NDK in this SDK version).
//! We go through cpp_shim.cpp which wraps OHOS::NativePreferences::Preferences.

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jboolean, jfloat, jint, jlong, JNI_FALSE, JNI_TRUE};
use std::ffi::CString;

use crate::oh_ffi;

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
}

// ── Open / Close ──

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesOpen(
    mut env: JNIEnv, _class: JClass, name: JString,
) -> jlong {
    let c_name = jstr_to_c(&mut env, &name);
    let handle = unsafe { oh_ffi::shim_preferences_open(c_name.as_ptr()) };
    handle as jlong
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesClose(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::shim_preferences_close(handle as *mut oh_ffi::ShimPreferences) };
}

// ── Get operations ──

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesGetString<'a>(
    mut env: JNIEnv<'a>, _class: JClass, handle: jlong, key: JString, def_value: JString,
) -> JString<'a> {
    let c_key = jstr_to_c(&mut env, &key);
    let c_def = jstr_to_c(&mut env, &def_value);
    let mut buf = vec![0u8; 8192]; // 8KB max value

    let len = unsafe {
        oh_ffi::shim_preferences_get_string(
            handle as *mut oh_ffi::ShimPreferences,
            c_key.as_ptr(),
            c_def.as_ptr(),
            buf.as_mut_ptr() as *mut std::os::raw::c_char,
            buf.len() as std::os::raw::c_int,
        )
    };

    let result = if len >= 0 {
        String::from_utf8_lossy(&buf[..len as usize]).into_owned()
    } else {
        env.get_string(&def_value).map(|s| s.into()).unwrap_or_default()
    };

    env.new_string(&result).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesGetInt(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, def_value: jint,
) -> jint {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_get_int(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), def_value)
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesGetLong(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, def_value: jlong,
) -> jlong {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_get_long(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), def_value)
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesGetFloat(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, def_value: jfloat,
) -> jfloat {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_get_float(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), def_value)
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesGetBoolean(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, def_value: jboolean,
) -> jboolean {
    let c_key = jstr_to_c(&mut env, &key);
    let result = unsafe {
        oh_ffi::shim_preferences_get_bool(
            handle as *mut oh_ffi::ShimPreferences,
            c_key.as_ptr(),
            def_value as std::os::raw::c_int,
        )
    };
    if result != 0 { JNI_TRUE } else { JNI_FALSE }
}

// ── Put operations ──

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesPutString(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, value: JString,
) {
    let c_key = jstr_to_c(&mut env, &key);
    let c_val = jstr_to_c(&mut env, &value);
    unsafe {
        oh_ffi::shim_preferences_put_string(
            handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), c_val.as_ptr(),
        );
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesPutInt(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, value: jint,
) {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_put_int(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), value);
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesPutLong(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, value: jlong,
) {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_put_long(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), value);
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesPutFloat(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, value: jfloat,
) {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_put_float(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), value);
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesPutBoolean(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString, value: jboolean,
) {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_put_bool(
            handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr(), value as std::os::raw::c_int,
        );
    }
}

// ── Flush / Remove / Clear ──

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesFlush(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::shim_preferences_flush(handle as *mut oh_ffi::ShimPreferences) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesRemove(
    mut env: JNIEnv, _class: JClass, handle: jlong, key: JString,
) {
    let c_key = jstr_to_c(&mut env, &key);
    unsafe {
        oh_ffi::shim_preferences_delete(handle as *mut oh_ffi::ShimPreferences, c_key.as_ptr());
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_preferencesClear(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::shim_preferences_clear(handle as *mut oh_ffi::ShimPreferences) };
}
