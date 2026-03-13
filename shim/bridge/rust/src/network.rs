//! JNI bridge: HttpURLConnection / ConnectivityManager → OH APIs
//!
//! - Connectivity: OH_NetConn_HasDefaultNet (real NDK)
//! - HTTP: libcurl via cpp_shim (shim_http_request)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jboolean, jint, JNI_FALSE, JNI_TRUE};
use std::ffi::CString;

use crate::oh_ffi;

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
}

fn nullable_jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    if s.is_null() {
        CString::new("").unwrap()
    } else {
        jstr_to_c(env, s)
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_isNetworkAvailable(
    _env: JNIEnv, _class: JClass,
) -> jboolean {
    let mut has_net: i32 = 0;
    let rc = unsafe { oh_ffi::OH_NetConn_HasDefaultNet(&mut has_net) };
    if rc == 0 && has_net != 0 { JNI_TRUE } else { JNI_FALSE }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_getNetworkType(
    _env: JNIEnv, _class: JClass,
) -> jint {
    let mut has_net: i32 = 0;
    let rc = unsafe { oh_ffi::OH_NetConn_HasDefaultNet(&mut has_net) };
    if rc == 0 && has_net != 0 { 1 } else { -1 }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_httpRequest<'a>(
    mut env: JNIEnv<'a>, _class: JClass,
    url: JString, method: JString, headers_json: JString, body: JString,
) -> JString<'a> {
    let c_url = jstr_to_c(&mut env, &url);
    let c_method = nullable_jstr_to_c(&mut env, &method);
    let c_headers = nullable_jstr_to_c(&mut env, &headers_json);
    let c_body = nullable_jstr_to_c(&mut env, &body);

    let mut out_response: *mut std::os::raw::c_char = std::ptr::null_mut();
    let mut out_len: std::os::raw::c_int = 0;

    let rc = unsafe {
        oh_ffi::shim_http_request(
            c_url.as_ptr(), c_method.as_ptr(),
            c_headers.as_ptr(), c_body.as_ptr(),
            &mut out_response, &mut out_len,
        )
    };

    let result = if rc == 0 && !out_response.is_null() && out_len > 0 {
        let s = unsafe {
            std::str::from_utf8_unchecked(
                std::slice::from_raw_parts(out_response as *const u8, out_len as usize)
            ).to_string()
        };
        unsafe { oh_ffi::shim_http_free(out_response) };
        s
    } else {
        String::new()
    };

    env.new_string(&result).expect("Failed to create JString")
}
