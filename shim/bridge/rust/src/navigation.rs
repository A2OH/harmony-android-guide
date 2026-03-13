//! JNI bridge: Intent/startActivity → OH ability navigation (via C++ shim)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use std::ffi::CString;

use crate::oh_ffi;

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_startAbility(
    mut env: JNIEnv, _class: JClass,
    bundle_name: JString, ability_name: JString, params_json: JString,
) {
    let c_bundle = jstr_to_c(&mut env, &bundle_name);
    let c_ability = jstr_to_c(&mut env, &ability_name);
    let c_params = if params_json.is_null() {
        CString::new("{}").unwrap()
    } else {
        jstr_to_c(&mut env, &params_json)
    };
    unsafe {
        oh_ffi::shim_start_ability(c_bundle.as_ptr(), c_ability.as_ptr(), c_params.as_ptr());
    }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_terminateSelf(
    _env: JNIEnv, _class: JClass,
) {
    unsafe { oh_ffi::shim_terminate_self() };
}
