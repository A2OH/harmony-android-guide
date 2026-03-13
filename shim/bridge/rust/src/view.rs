//! JNI bridge: Android View hierarchy → ArkUI Native Node API
//!
//! Each Android View maps to an ArkUI native node handle (i64).
//! The Java View shim stores the native handle and calls these JNI
//! methods to create/modify/destroy nodes.
//!
//! Mapping:
//! - View          → any node type (base properties: width, height, visibility, bg color, padding)
//! - TextView      → ARKUI_NODE_TEXT
//! - Button        → ARKUI_NODE_BUTTON
//! - EditText      → ARKUI_NODE_TEXT_INPUT / TEXT_AREA
//! - ImageView     → ARKUI_NODE_IMAGE
//! - LinearLayout  → ARKUI_NODE_COLUMN (vertical) / ARKUI_NODE_ROW (horizontal)
//! - FrameLayout   → ARKUI_NODE_STACK
//! - ScrollView    → ARKUI_NODE_SCROLL
//! - CheckBox      → ARKUI_NODE_CHECKBOX
//! - Switch        → ARKUI_NODE_TOGGLE
//! - SeekBar       → ARKUI_NODE_SLIDER
//! - ProgressBar   → ARKUI_NODE_PROGRESS
//! - ViewGroup     → ARKUI_NODE_STACK (generic container)

use jni::JNIEnv;
use jni::objects::JClass;
use jni::objects::JString;
use jni::sys::{jboolean, jfloat, jint, jlong, JNI_FALSE, JNI_TRUE};
use std::collections::HashMap;
use std::ffi::CString;
use std::os::raw::{c_char, c_int};
use std::sync::Mutex;

use crate::oh_ffi;

// ═══════════════════════════════════════════════════════════════════
// Event dispatch: ArkUI events → Java View callbacks
// ═══════════════════════════════════════════════════════════════════

/// Stored JVM reference for calling back into Java from the event thread
static EVENT_JVM: once_cell::sync::OnceCell<jni::JavaVM> = once_cell::sync::OnceCell::new();

/// Maps event_id → (java View class name, callback type) for dispatch
/// We use a simple approach: event_id encodes the native node handle's lower bits
/// The Java side holds the mapping from handle → View object

/// Global event callback — receives events from the C++ shim and forwards to Java
unsafe extern "C" fn event_dispatch(
    event_id: c_int,
    node: i64,
    event_kind: c_int,
    data: *const f32,
    data_count: c_int,
    string_data: *const c_char,
) {
    let jvm = match EVENT_JVM.get() {
        Some(jvm) => jvm,
        None => return,
    };

    let mut env = match jvm.attach_current_thread() {
        Ok(env) => env,
        Err(_) => return,
    };

    // Call OHBridge.dispatchNodeEvent(eventId, nodeHandle, eventKind, stringData)
    let class = match env.find_class("com/ohos/shim/bridge/OHBridge") {
        Ok(c) => c,
        Err(_) => return,
    };

    let str_data = if !string_data.is_null() {
        let cs = std::ffi::CStr::from_ptr(string_data);
        match env.new_string(cs.to_string_lossy().as_ref()) {
            Ok(s) => Some(s),
            Err(_) => None,
        }
    } else {
        None
    };

    let jstr = match &str_data {
        Some(s) => jni::objects::JValue::Object(s.as_ref()),
        None => jni::objects::JValue::Object(jni::objects::JObject::null().as_ref()),
    };

    let _ = env.call_static_method(
        class,
        "dispatchNodeEvent",
        "(IJILjava/lang/String;)V",
        &[
            jni::objects::JValue::Int(event_id),
            jni::objects::JValue::Long(node),
            jni::objects::JValue::Int(event_kind),
            jstr,
        ],
    );
}

// ═══════════════════════════════════════════════════════════════════
// Init
// ═══════════════════════════════════════════════════════════════════

/// Initialize ArkUI native node API. Called once at startup.
#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_arkuiInit(
    env: JNIEnv, _class: JClass,
) -> jint {
    // Store JVM for event callbacks
    let jvm = env.get_java_vm().expect("Failed to get JavaVM");
    let _ = EVENT_JVM.set(jvm);

    let rc = unsafe { oh_ffi::shim_arkui_init() };
    if rc != 0 { return rc; }

    unsafe { oh_ffi::shim_node_set_event_callback(Some(event_dispatch)); }
    0
}

// ═══════════════════════════════════════════════════════════════════
// Node lifecycle
// ═══════════════════════════════════════════════════════════════════

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeCreate(
    _env: JNIEnv, _class: JClass, node_type: jint,
) -> jlong {
    unsafe { oh_ffi::shim_node_create(node_type) }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeDispose(
    _env: JNIEnv, _class: JClass, node: jlong,
) {
    unsafe { oh_ffi::shim_node_dispose(node); }
}

// ═══════════════════════════════════════════════════════════════════
// Tree operations
// ═══════════════════════════════════════════════════════════════════

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeAddChild(
    _env: JNIEnv, _class: JClass, parent: jlong, child: jlong,
) {
    unsafe { oh_ffi::shim_node_add_child(parent, child); }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild(
    _env: JNIEnv, _class: JClass, parent: jlong, child: jlong,
) {
    unsafe { oh_ffi::shim_node_remove_child(parent, child); }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeInsertChildAt(
    _env: JNIEnv, _class: JClass, parent: jlong, child: jlong, position: jint,
) {
    unsafe { oh_ffi::shim_node_insert_child_at(parent, child, position); }
}

// ═══════════════════════════════════════════════════════════════════
// Attribute setters
// ═══════════════════════════════════════════════════════════════════

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrFloat(
    _env: JNIEnv, _class: JClass,
    node: jlong, attr_type: jint, v0: jfloat, v1: jfloat, v2: jfloat, v3: jfloat, count: jint,
) -> jint {
    let values = [v0, v1, v2, v3];
    unsafe { oh_ffi::shim_node_set_attr_float(node, attr_type, values.as_ptr(), count) }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor(
    _env: JNIEnv, _class: JClass,
    node: jlong, attr_type: jint, color: jint,
) -> jint {
    let c = color as u32;
    unsafe { oh_ffi::shim_node_set_attr_u32(node, attr_type, &c, 1) }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt(
    _env: JNIEnv, _class: JClass,
    node: jlong, attr_type: jint, value: jint,
) -> jint {
    unsafe { oh_ffi::shim_node_set_attr_i32(node, attr_type, &value, 1) }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString<'a>(
    mut env: JNIEnv<'a>, _class: JClass,
    node: jlong, attr_type: jint, value: JString,
) -> jint {
    let s: String = env.get_string(&value).expect("Invalid JString").into();
    let cs = CString::new(s).unwrap_or_else(|_| CString::new("").unwrap());
    unsafe { oh_ffi::shim_node_set_attr_string(node, attr_type, cs.as_ptr()) }
}

// ═══════════════════════════════════════════════════════════════════
// Events
// ═══════════════════════════════════════════════════════════════════

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeRegisterEvent(
    _env: JNIEnv, _class: JClass,
    node: jlong, event_type: jint, event_id: jint,
) -> jint {
    unsafe { oh_ffi::shim_node_register_event(node, event_type, event_id) }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeUnregisterEvent(
    _env: JNIEnv, _class: JClass,
    node: jlong, event_type: jint,
) {
    unsafe { oh_ffi::shim_node_unregister_event(node, event_type); }
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_nodeMarkDirty(
    _env: JNIEnv, _class: JClass,
    node: jlong, flag: jint,
) {
    unsafe { oh_ffi::shim_node_mark_dirty(node, flag); }
}
