//! JNI bridge: Cursor shim → OH_Cursor (vtable struct, real NDK)
//!
//! OH_Cursor uses function pointers (vtable pattern), not flat C functions.
//! We call cursor->goToNextRow(cursor), cursor->getText(cursor, col, buf, len), etc.

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jboolean, jbyteArray, jdouble, jfloat, jint, jlong, JNI_FALSE, JNI_TRUE};
use std::ffi::CString;

use crate::oh_ffi;

fn cursor(handle: jlong) -> *mut oh_ffi::OH_Cursor {
    handle as *mut oh_ffi::OH_Cursor
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGoToFirstRow(
    _env: JNIEnv, _class: JClass, handle: jlong,
) -> jboolean {
    // OH_Cursor has no goToFirstRow — only goToNextRow.
    // The cursor starts before the first row, so first goToNextRow = goToFirstRow.
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    if let Some(go_next) = c_ref.go_to_next_row {
        let rc = unsafe { go_next(c) };
        if rc == 0 { JNI_TRUE } else { JNI_FALSE }
    } else {
        JNI_FALSE
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGoToNextRow(
    _env: JNIEnv, _class: JClass, handle: jlong,
) -> jboolean {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    if let Some(go_next) = c_ref.go_to_next_row {
        let rc = unsafe { go_next(c) };
        if rc == 0 { JNI_TRUE } else { JNI_FALSE }
    } else {
        JNI_FALSE
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetColumnIndex(
    mut env: JNIEnv, _class: JClass, handle: jlong, column_name: JString,
) -> jint {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let rs: String = env.get_string(&column_name).expect("Invalid JString").into();
    let c_name = CString::new(rs).unwrap();
    let mut idx: std::os::raw::c_int = -1;
    if let Some(get_col_idx) = c_ref.get_column_index {
        unsafe { get_col_idx(c, c_name.as_ptr(), &mut idx) };
    }
    idx
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetString<'a>(
    mut env: JNIEnv<'a>, _class: JClass, handle: jlong, col: jint,
) -> JString<'a> {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };

    // First get size, then read text
    let mut size: usize = 0;
    if let Some(get_size) = c_ref.get_size {
        unsafe { get_size(c, col, &mut size) };
    }
    if size == 0 {
        return env.new_string("").expect("Failed to create JString");
    }

    let mut buf = vec![0u8; size + 1];
    if let Some(get_text) = c_ref.get_text {
        unsafe { get_text(c, col, buf.as_mut_ptr() as *mut std::os::raw::c_char, (size + 1) as std::os::raw::c_int) };
    }

    let result = String::from_utf8_lossy(&buf[..size]).into_owned();
    env.new_string(&result).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetInt(
    _env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jint {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut val: i64 = 0;
    if let Some(get_int64) = c_ref.get_int64 {
        unsafe { get_int64(c, col, &mut val) };
    }
    val as jint
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetLong(
    _env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jlong {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut val: i64 = 0;
    if let Some(get_int64) = c_ref.get_int64 {
        unsafe { get_int64(c, col, &mut val) };
    }
    val
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetFloat(
    _env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jfloat {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut val: f64 = 0.0;
    if let Some(get_real) = c_ref.get_real {
        unsafe { get_real(c, col, &mut val) };
    }
    val as jfloat
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetDouble(
    _env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jdouble {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut val: f64 = 0.0;
    if let Some(get_real) = c_ref.get_real {
        unsafe { get_real(c, col, &mut val) };
    }
    val
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetBlob(
    mut env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jbyteArray {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };

    let mut size: usize = 0;
    if let Some(get_size) = c_ref.get_size {
        unsafe { get_size(c, col, &mut size) };
    }
    if size == 0 {
        return *env.new_byte_array(0).expect("Failed to create byte array");
    }

    let mut buf = vec![0u8; size];
    if let Some(get_blob) = c_ref.get_blob {
        unsafe { get_blob(c, col, buf.as_mut_ptr(), size as std::os::raw::c_int) };
    }

    let arr = env.new_byte_array(size as jint).expect("Failed to create byte array");
    let i8_slice: &[i8] = unsafe { std::mem::transmute(buf.as_slice()) };
    env.set_byte_array_region(&arr, 0, i8_slice).expect("Failed to set byte array");
    *arr
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetIsNull(
    _env: JNIEnv, _class: JClass, handle: jlong, col: jint,
) -> jboolean {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut is_null = false;
    if let Some(is_null_fn) = c_ref.is_null {
        unsafe { is_null_fn(c, col, &mut is_null) };
    }
    if is_null { JNI_TRUE } else { JNI_FALSE }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetRowCount(
    _env: JNIEnv, _class: JClass, handle: jlong,
) -> jint {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut count: std::os::raw::c_int = 0;
    if let Some(get_row_count) = c_ref.get_row_count {
        unsafe { get_row_count(c, &mut count) };
    }
    count
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetColumnCount(
    _env: JNIEnv, _class: JClass, handle: jlong,
) -> jint {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut count: std::os::raw::c_int = 0;
    if let Some(get_col_count) = c_ref.get_column_count {
        unsafe { get_col_count(c, &mut count) };
    }
    count
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetGetColumnName<'a>(
    mut env: JNIEnv<'a>, _class: JClass, handle: jlong, col: jint,
) -> JString<'a> {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    let mut buf = vec![0u8; 256];
    if let Some(get_col_name) = c_ref.get_column_name {
        unsafe { get_col_name(c, col, buf.as_mut_ptr() as *mut std::os::raw::c_char, 256) };
    }
    let len = buf.iter().position(|&b| b == 0).unwrap_or(0);
    let result = String::from_utf8_lossy(&buf[..len]).into_owned();
    env.new_string(&result).expect("Failed to create JString")
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_resultSetClose(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    let c = cursor(handle);
    let c_ref = unsafe { &*c };
    if let Some(destroy) = c_ref.destroy {
        unsafe { destroy(c) };
    }
}
