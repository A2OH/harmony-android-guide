//! OH Bridge: JNI glue between Java Android shim and OpenHarmony native APIs.
//!
//! Architecture:
//! Java shim (android.*) → JNI → this Rust lib → OH C/N-API → OH runtime
//!
//! Each module below corresponds to an OH subsystem.

mod oh_ffi;
mod preferences;
mod rdb_store;
mod result_set;
mod notification;
mod reminder;
mod navigation;
mod logging;
mod toast;
mod network;
mod device_info;
mod view;

use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::jint;

/// Library initialization — called when System.loadLibrary("oh_bridge") runs.
#[no_mangle]
pub extern "system" fn JNI_OnLoad(vm: jni::JavaVM, _reserved: *mut std::ffi::c_void) -> jint {
    let _env = vm.get_env().expect("Failed to get JNI env");
    log::info!("oh_bridge native library loaded");
    jni::sys::JNI_VERSION_1_6
}
