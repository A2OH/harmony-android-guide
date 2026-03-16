//! Clipboard bridge: Android ClipboardManager → OHOS pasteboard.
//!
//! OHOS pasteboard API (C++):
//!   OHOS::MiscServices::PasteboardClient::GetInstance()
//!     →GetPasteData(PasteData&)
//!     →SetPasteData(PasteData&)
//!     →HasPasteData()
//!     →Clear()
//!
//! No C NDK for pasteboard — goes through our C++ shim.

use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use std::sync::Mutex;

use once_cell::sync::Lazy;

/// In-process clipboard storage (used when C++ shim isn't available)
static CLIPBOARD: Lazy<Mutex<Option<String>>> = Lazy::new(|| Mutex::new(None));

/// OHBridge.clipboardSet(String text)
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_clipboardSet(
    mut env: JNIEnv,
    _class: JClass,
    text: JString,
) {
    if let Ok(s) = env.get_string(&text) {
        let text_str: String = s.into();

        // Try C++ shim first (real OHOS pasteboard)
        // For now, store in-process
        if let Ok(mut clip) = CLIPBOARD.lock() {
            *clip = Some(text_str);
        }
    }
}

/// OHBridge.clipboardGet() → String
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_clipboardGet(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let text = CLIPBOARD.lock().ok().and_then(|c| c.clone());

    match text {
        Some(s) => env
            .new_string(&s)
            .map(|js| js.into_raw())
            .unwrap_or(std::ptr::null_mut()),
        None => std::ptr::null_mut(),
    }
}
