//! FFI bindings to actual OpenHarmony NDK C APIs.
//!
//! Sources verified against:
//! - interface/sdk_c/hiviewdfx/hilog/include/hilog/log.h
//! - interface/sdk_c/distributeddatamgr/relational_store/include/*.h
//! - interface/sdk_c/startup/init/syscap/include/deviceinfo.h
//! - interface/sdk_c/network/netmanager/include/net_connection.h
//!
//! APIs without C NDK (Preferences, Notification, Reminder, Toast, Ability nav)
//! go through our thin C++ shim (cpp_shim.cpp) which wraps the C++ inner APIs.

#![allow(non_camel_case_types, dead_code)]

use std::os::raw::{c_char, c_double, c_int, c_uint, c_void};

// ═══════════════════════════════════════════════════════════════════
// HiLog — libhilog_ndk.z.so (REAL NDK)
// ═══════════════════════════════════════════════════════════════════

#[repr(C)]
pub enum LogType {
    LOG_APP = 0,
}

#[repr(C)]
pub enum LogLevel {
    LOG_DEBUG = 3,
    LOG_INFO = 4,
    LOG_WARN = 5,
    LOG_ERROR = 6,
    LOG_FATAL = 7,
}

extern "C" {
    /// OH_LOG_Print(type, level, domain, tag, fmt, ...)
    /// The real signature is variadic. We call it with a single %s arg.
    pub fn OH_LOG_Print(
        log_type: LogType,
        level: LogLevel,
        domain: c_uint,
        tag: *const c_char,
        fmt: *const c_char,
        // variadic: we pass the message as the first vararg
        ...
    ) -> c_int;

    pub fn OH_LOG_IsLoggable(
        domain: c_uint,
        tag: *const c_char,
        level: LogLevel,
    ) -> bool;
}

// ═══════════════════════════════════════════════════════════════════
// RdbStore — libnative_rdb_ndk.z.so (REAL NDK)
// ═══════════════════════════════════════════════════════════════════

/// Opaque handle — actual struct has int64_t id
#[repr(C)]
pub struct OH_Rdb_Store {
    pub id: i64,
}

/// OH_Rdb_Config — #pragma pack(1) in the C header
#[repr(C, packed)]
pub struct OH_Rdb_Config {
    pub self_size: c_int,
    pub data_base_dir: *const c_char,
    pub store_name: *const c_char,
    pub bundle_name: *const c_char,
    pub module_name: *const c_char,
    pub is_encrypt: bool,
    pub security_level: c_int, // OH_Rdb_SecurityLevel: S1=1..S4=4
    pub area: c_int,           // Rdb_SecurityArea (API 11)
}

/// OH_Cursor is a vtable struct — all methods are function pointers.
/// We access methods through the struct fields.
#[repr(C)]
pub struct OH_Cursor {
    pub id: i64,
    pub get_column_count: Option<unsafe extern "C" fn(*mut OH_Cursor, *mut c_int) -> c_int>,
    pub get_column_type: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut c_int) -> c_int>,
    pub get_column_index: Option<unsafe extern "C" fn(*mut OH_Cursor, *const c_char, *mut c_int) -> c_int>,
    pub get_column_name: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut c_char, c_int) -> c_int>,
    pub get_row_count: Option<unsafe extern "C" fn(*mut OH_Cursor, *mut c_int) -> c_int>,
    pub go_to_next_row: Option<unsafe extern "C" fn(*mut OH_Cursor) -> c_int>,
    pub get_size: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut usize) -> c_int>,
    pub get_text: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut c_char, c_int) -> c_int>,
    pub get_int64: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut i64) -> c_int>,
    pub get_real: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut c_double) -> c_int>,
    pub get_blob: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut u8, c_int) -> c_int>,
    pub is_null: Option<unsafe extern "C" fn(*mut OH_Cursor, i32, *mut bool) -> c_int>,
    pub destroy: Option<unsafe extern "C" fn(*mut OH_Cursor) -> c_int>,
}

/// OH_VBucket — vtable struct for row values
#[repr(C)]
pub struct OH_VBucket {
    pub id: i64,
    pub capability: u16,
    pub put_text: Option<unsafe extern "C" fn(*mut OH_VBucket, *const c_char, *const c_char) -> c_int>,
    pub put_int64: Option<unsafe extern "C" fn(*mut OH_VBucket, *const c_char, i64) -> c_int>,
    pub put_real: Option<unsafe extern "C" fn(*mut OH_VBucket, *const c_char, c_double) -> c_int>,
    pub put_blob: Option<unsafe extern "C" fn(*mut OH_VBucket, *const c_char, *const u8, u32) -> c_int>,
    pub put_null: Option<unsafe extern "C" fn(*mut OH_VBucket, *const c_char) -> c_int>,
    pub clear: Option<unsafe extern "C" fn(*mut OH_VBucket) -> c_int>,
    pub destroy: Option<unsafe extern "C" fn(*mut OH_VBucket) -> c_int>,
}

/// OH_Predicates — vtable struct for WHERE clauses
/// All methods (except destroy) return *mut OH_Predicates for chaining.
pub type OH_Predicates = c_void; // opaque — we use factory + OH_VObject for predicates
pub type OH_VObject = c_void;    // opaque value object

extern "C" {
    // Factory
    pub fn OH_Rdb_CreateValueObject() -> *mut OH_VObject;
    pub fn OH_Rdb_CreateValuesBucket() -> *mut OH_VBucket;
    pub fn OH_Rdb_CreatePredicates(table: *const c_char) -> *mut OH_Predicates;

    // Store lifecycle
    pub fn OH_Rdb_GetOrOpen(config: *const OH_Rdb_Config, err_code: *mut c_int) -> *mut OH_Rdb_Store;
    pub fn OH_Rdb_CloseStore(store: *mut OH_Rdb_Store) -> c_int;
    pub fn OH_Rdb_DeleteStore(config: *const OH_Rdb_Config) -> c_int;

    // CRUD
    pub fn OH_Rdb_Insert(store: *mut OH_Rdb_Store, table: *const c_char, bucket: *mut OH_VBucket) -> c_int;
    pub fn OH_Rdb_Update(store: *mut OH_Rdb_Store, bucket: *mut OH_VBucket, predicates: *mut OH_Predicates) -> c_int;
    pub fn OH_Rdb_Delete(store: *mut OH_Rdb_Store, predicates: *mut OH_Predicates) -> c_int;
    pub fn OH_Rdb_Query(
        store: *mut OH_Rdb_Store, predicates: *mut OH_Predicates,
        column_names: *const *const c_char, length: c_int,
    ) -> *mut OH_Cursor;
    pub fn OH_Rdb_Execute(store: *mut OH_Rdb_Store, sql: *const c_char) -> c_int;
    pub fn OH_Rdb_ExecuteQuery(store: *mut OH_Rdb_Store, sql: *const c_char) -> *mut OH_Cursor;

    // Transactions
    pub fn OH_Rdb_BeginTransaction(store: *mut OH_Rdb_Store) -> c_int;
    pub fn OH_Rdb_Commit(store: *mut OH_Rdb_Store) -> c_int;
    pub fn OH_Rdb_RollBack(store: *mut OH_Rdb_Store) -> c_int;

    // Version
    pub fn OH_Rdb_GetVersion(store: *mut OH_Rdb_Store, version: *mut c_int) -> c_int;
    pub fn OH_Rdb_SetVersion(store: *mut OH_Rdb_Store, version: c_int) -> c_int;
}

// ═══════════════════════════════════════════════════════════════════
// DeviceInfo — libdeviceinfo_ndk.z.so (REAL NDK)
// All return const char* (static strings — do NOT free)
// ═══════════════════════════════════════════════════════════════════

extern "C" {
    pub fn OH_GetDeviceType() -> *const c_char;
    pub fn OH_GetManufacture() -> *const c_char;
    pub fn OH_GetBrand() -> *const c_char;
    pub fn OH_GetMarketName() -> *const c_char;
    pub fn OH_GetProductSeries() -> *const c_char;
    pub fn OH_GetProductModel() -> *const c_char;
    pub fn OH_GetSoftwareModel() -> *const c_char;
    pub fn OH_GetHardwareModel() -> *const c_char;
    pub fn OH_GetDisplayVersion() -> *const c_char;
    pub fn OH_GetOSFullName() -> *const c_char;
    pub fn OH_GetSdkApiVersion() -> c_int;
    pub fn OH_GetFirstApiVersion() -> c_int;
    pub fn OH_GetOsReleaseType() -> *const c_char;
}

// ═══════════════════════════════════════════════════════════════════
// Net Connection — libnet_connection.so (REAL NDK)
// ═══════════════════════════════════════════════════════════════════

extern "C" {
    pub fn OH_NetConn_HasDefaultNet(has_default_net: *mut i32) -> i32;
    // OH_NetConn_GetNetCapabilities etc. — add as needed
}

// ═══════════════════════════════════════════════════════════════════
// C++ Shim APIs — liboh_cpp_shim.so (our thin wrapper)
// For: Preferences, Notification, Reminder, Toast, Ability nav
// Defined in cpp_shim.cpp, exported as extern "C"
// ═══════════════════════════════════════════════════════════════════

/// Opaque handle to OHOS::NativePreferences::Preferences (shared_ptr stored in shim)
pub type ShimPreferences = c_void;

extern "C" {
    // Preferences
    pub fn shim_preferences_open(file_path: *const c_char) -> *mut ShimPreferences;
    pub fn shim_preferences_close(prefs: *mut ShimPreferences);
    pub fn shim_preferences_get_string(
        prefs: *mut ShimPreferences, key: *const c_char,
        def: *const c_char, out_buf: *mut c_char, buf_len: c_int,
    ) -> c_int;
    pub fn shim_preferences_get_int(prefs: *mut ShimPreferences, key: *const c_char, def: c_int) -> c_int;
    pub fn shim_preferences_get_long(prefs: *mut ShimPreferences, key: *const c_char, def: i64) -> i64;
    pub fn shim_preferences_get_float(prefs: *mut ShimPreferences, key: *const c_char, def: f32) -> f32;
    pub fn shim_preferences_get_bool(prefs: *mut ShimPreferences, key: *const c_char, def: c_int) -> c_int;
    pub fn shim_preferences_put_string(prefs: *mut ShimPreferences, key: *const c_char, value: *const c_char) -> c_int;
    pub fn shim_preferences_put_int(prefs: *mut ShimPreferences, key: *const c_char, value: c_int) -> c_int;
    pub fn shim_preferences_put_long(prefs: *mut ShimPreferences, key: *const c_char, value: i64) -> c_int;
    pub fn shim_preferences_put_float(prefs: *mut ShimPreferences, key: *const c_char, value: f32) -> c_int;
    pub fn shim_preferences_put_bool(prefs: *mut ShimPreferences, key: *const c_char, value: c_int) -> c_int;
    pub fn shim_preferences_delete(prefs: *mut ShimPreferences, key: *const c_char) -> c_int;
    pub fn shim_preferences_clear(prefs: *mut ShimPreferences) -> c_int;
    pub fn shim_preferences_flush(prefs: *mut ShimPreferences) -> c_int;

    // Notification
    pub fn shim_notification_publish(id: c_int, title: *const c_char, text: *const c_char, slot_type: c_int) -> c_int;
    pub fn shim_notification_cancel(id: c_int) -> c_int;
    pub fn shim_notification_add_slot(slot_type: c_int) -> c_int;

    // Reminder (backgroundTaskManager / reminderAgent — C++ inner API)
    pub fn shim_reminder_publish_timer(
        delay_seconds: c_int, title: *const c_char, content: *const c_char,
        bundle_name: *const c_char, ability_name: *const c_char,
    ) -> c_int; // returns reminder id

    pub fn shim_reminder_cancel(reminder_id: c_int) -> c_int;

    // Toast / PromptAction (ArkTS only — shim calls via NAPI)
    pub fn shim_show_toast(message: *const c_char, duration: c_int) -> c_int;

    // Ability navigation (C++ AbilityContext)
    pub fn shim_start_ability(
        bundle_name: *const c_char, ability_name: *const c_char, params_json: *const c_char,
    ) -> c_int;
    pub fn shim_terminate_self() -> c_int;

    // HTTP (via libcurl in cpp_shim)
    pub fn shim_http_request(
        url: *const c_char, method: *const c_char,
        headers_json: *const c_char, body: *const c_char,
        out_response: *mut *mut c_char, out_len: *mut c_int,
    ) -> c_int;
    pub fn shim_http_free(ptr: *mut c_char);
}

// ═══════════════════════════════════════════════════════════════════
// ArkUI Native Node API — View rendering (via cpp_shim)
// ═══════════════════════════════════════════════════════════════════

/// Event callback signature: (event_id, node_handle, event_kind, float_data, data_count, string_data)
pub type ShimNodeEventCallback = Option<
    unsafe extern "C" fn(
        event_id: c_int,
        node: i64,
        event_kind: c_int,
        data: *const f32,
        data_count: c_int,
        string_data: *const c_char,
    ),
>;

extern "C" {
    pub fn shim_arkui_init() -> c_int;
    pub fn shim_node_set_event_callback(callback: ShimNodeEventCallback);

    pub fn shim_node_create(node_type: c_int) -> i64;
    pub fn shim_node_dispose(node: i64);

    pub fn shim_node_add_child(parent: i64, child: i64);
    pub fn shim_node_remove_child(parent: i64, child: i64);
    pub fn shim_node_insert_child_at(parent: i64, child: i64, position: c_int);

    pub fn shim_node_set_attr_float(node: i64, attr_type: c_int, values: *const f32, count: c_int) -> c_int;
    pub fn shim_node_set_attr_u32(node: i64, attr_type: c_int, values: *const c_uint, count: c_int) -> c_int;
    pub fn shim_node_set_attr_i32(node: i64, attr_type: c_int, values: *const c_int, count: c_int) -> c_int;
    pub fn shim_node_set_attr_string(node: i64, attr_type: c_int, value: *const c_char) -> c_int;

    pub fn shim_node_register_event(node: i64, event_type: c_int, event_id: c_int) -> c_int;
    pub fn shim_node_unregister_event(node: i64, event_type: c_int);

    pub fn shim_node_mark_dirty(node: i64, flag: c_int);
    pub fn shim_xcomponent_attach_root(xcomponent: *mut c_void, root_node: i64) -> c_int;
}

/// ArkUI node type constants (from ArkUI_NodeType enum)
#[allow(unused)]
pub mod node_type {
    pub const CUSTOM: i32 = 0;
    pub const TEXT: i32 = 1;
    pub const SPAN: i32 = 2;
    pub const IMAGE_SPAN: i32 = 3;
    pub const IMAGE: i32 = 4;
    pub const TOGGLE: i32 = 5;
    pub const LOADING_PROGRESS: i32 = 6;
    pub const TEXT_INPUT: i32 = 7;
    pub const STACK: i32 = 8;
    pub const SCROLL: i32 = 9;
    pub const LIST: i32 = 10;
    pub const SWIPER: i32 = 11;
    pub const TEXT_AREA: i32 = 12;
    pub const BUTTON: i32 = 13;
    pub const PROGRESS: i32 = 14;
    pub const CHECKBOX: i32 = 15;
    pub const COLUMN: i32 = 16;
    pub const ROW: i32 = 17;
    pub const FLEX: i32 = 18;
    pub const LIST_ITEM: i32 = 19;
    pub const REFRESH: i32 = 20;
    pub const XCOMPONENT: i32 = 21;
    pub const LIST_ITEM_GROUP: i32 = 22;
    pub const DATE_PICKER: i32 = 23;
    pub const TIME_PICKER: i32 = 24;
    pub const TEXT_PICKER: i32 = 25;
    pub const SLIDER: i32 = 26;
}

/// Common attribute constants (from ArkUI_NodeAttributeType)
#[allow(unused)]
pub mod attr {
    pub const WIDTH: i32 = 0;
    pub const HEIGHT: i32 = 1;
    pub const BACKGROUND_COLOR: i32 = 2;
    pub const BACKGROUND_IMAGE: i32 = 3;
    pub const PADDING: i32 = 4;
    pub const ID: i32 = 5;
    pub const ENABLED: i32 = 6;
    pub const MARGIN: i32 = 7;
    pub const TRANSLATE: i32 = 8;
    pub const SCALE: i32 = 9;
    pub const ROTATE: i32 = 10;
    pub const BRIGHTNESS: i32 = 11;
    pub const SATURATE: i32 = 12;
    pub const BLUR: i32 = 13;
    pub const LINEAR_GRADIENT: i32 = 14;
    pub const ALIGN: i32 = 15;
    pub const OPACITY: i32 = 16;
    pub const BORDER_WIDTH: i32 = 17;
    pub const BORDER_RADIUS: i32 = 18;
    pub const BORDER_COLOR: i32 = 19;
    pub const BORDER_STYLE: i32 = 20;
    pub const ZINDEX: i32 = 21;
    pub const VISIBILITY: i32 = 22;
    pub const CLIP: i32 = 23;
    pub const CLIP_SHAPE: i32 = 24;
    pub const TRANSFORM: i32 = 25;
    pub const HIT_TEST_BEHAVIOR: i32 = 26;
    pub const POSITION: i32 = 27;
    pub const SHADOW: i32 = 28;
    pub const CUSTOM_SHADOW: i32 = 29;
    pub const BACKGROUND_IMAGE_SIZE: i32 = 30;
    pub const BACKGROUND_IMAGE_SIZE_WITH_STYLE: i32 = 31;
    pub const BACKGROUND_BLUR_STYLE: i32 = 32;
    pub const TRANSFORM_CENTER: i32 = 33;
    pub const OPACITY_TRANSITION: i32 = 34;
    pub const ROTATE_TRANSITION: i32 = 35;
    pub const SCALE_TRANSITION: i32 = 36;
    pub const TRANSLATE_TRANSITION: i32 = 37;
    pub const MOVE_TRANSITION: i32 = 38;
    pub const FOCUSABLE: i32 = 39;

    // MAX_NODE_SCOPE_NUM = 1000
    // Text-specific (base = 1000 * 1 = 1000)
    pub const TEXT_CONTENT: i32 = 1000;
    pub const FONT_COLOR: i32 = 1001;
    pub const FONT_SIZE: i32 = 1002;
    pub const FONT_STYLE: i32 = 1003;
    pub const FONT_WEIGHT: i32 = 1004;
    pub const TEXT_LINE_HEIGHT: i32 = 1005;
    pub const TEXT_DECORATION: i32 = 1006;
    pub const TEXT_CASE: i32 = 1007;
    pub const TEXT_LETTER_SPACING: i32 = 1008;
    pub const TEXT_MAX_LINES: i32 = 1009;
    pub const TEXT_ALIGN: i32 = 1010;
    pub const TEXT_OVERFLOW: i32 = 1011;
    pub const FONT_FAMILY: i32 = 1012;
    pub const TEXT_COPY_OPTION: i32 = 1013;

    // Image-specific (base = 1000 * 4 = 4000)
    pub const IMAGE_SRC: i32 = 4000;

    // Toggle-specific (base = 1000 * 5 = 5000)
    pub const TOGGLE_TYPE: i32 = 5000;
    pub const TOGGLE_STATE: i32 = 5001;

    // TextInput-specific (base = 1000 * 7 = 7000)
    pub const TEXT_INPUT_PLACEHOLDER: i32 = 7000;
    pub const TEXT_INPUT_TEXT: i32 = 7001;
    pub const TEXT_INPUT_CARET_COLOR: i32 = 7002;
    pub const TEXT_INPUT_PLACEHOLDER_COLOR: i32 = 7003;
    pub const TEXT_INPUT_PLACEHOLDER_FONT: i32 = 7004;
    pub const TEXT_INPUT_TYPE: i32 = 7005;

    // Scroll-specific (base = 1000 * 9 = 9000)
    pub const SCROLL_BAR_DISPLAY_MODE: i32 = 9000;

    // List-specific (base = 1000 * 10 = 10000)
    pub const LIST_DIRECTION: i32 = 10000;

    // TextArea-specific (base = 1000 * 12 = 12000)
    pub const TEXT_AREA_PLACEHOLDER: i32 = 12000;
    pub const TEXT_AREA_TEXT: i32 = 12001;

    // Button-specific (base = 1000 * 13 = 13000)
    pub const BUTTON_LABEL: i32 = 13000;

    // Progress-specific (base = 1000 * 14 = 14000)
    pub const PROGRESS_VALUE: i32 = 14000;
    pub const PROGRESS_TOTAL: i32 = 14001;
    pub const PROGRESS_TYPE: i32 = 14002;

    // Checkbox-specific (base = 1000 * 15 = 15000)
    pub const CHECKBOX_SELECT: i32 = 15000;
    pub const CHECKBOX_SELECT_COLOR: i32 = 15001;

    // Slider-specific (base = 1000 * 26 = 26000)
    pub const SLIDER_VALUE: i32 = 26000;
    pub const SLIDER_MIN: i32 = 26001;
    pub const SLIDER_MAX: i32 = 26002;
    pub const SLIDER_STEP: i32 = 26003;
}

/// Event type constants (from ArkUI_NodeEventType)
#[allow(unused)]
pub mod event {
    pub const TOUCH: i32 = 0;
    pub const ON_APPEAR: i32 = 1;
    pub const ON_AREA_CHANGE: i32 = 2;
    pub const ON_FOCUS: i32 = 3;
    pub const ON_BLUR: i32 = 4;
    pub const ON_CLICK: i32 = 5;

    pub const TOGGLE_ON_CHANGE: i32 = 5000;
    pub const TEXT_INPUT_ON_CHANGE: i32 = 7000;
    pub const TEXT_INPUT_ON_SUBMIT: i32 = 7001;
    pub const SCROLL_ON_SCROLL: i32 = 9000;
    pub const TEXT_AREA_ON_CHANGE: i32 = 12000;
    pub const CHECKBOX_ON_CHANGE: i32 = 15000;
    pub const SLIDER_ON_CHANGE: i32 = 26000;
}

// ═══════════════════════════════════════════════════════════════════
// Helpers
// ═══════════════════════════════════════════════════════════════════

/// Convert a static const char* from OH to Rust String (does NOT free the pointer).
pub unsafe fn static_cstr_to_string(ptr: *const c_char) -> String {
    if ptr.is_null() {
        return String::new();
    }
    std::ffi::CStr::from_ptr(ptr)
        .to_string_lossy()
        .into_owned()
}
