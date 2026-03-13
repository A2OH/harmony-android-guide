//! JNI bridge: SQLiteDatabase shim → OH RdbStore (real NDK)
//!
//! Real API uses OH_Rdb_GetOrOpen(config, errCode), OH_Rdb_Execute(store, sql),
//! OH_Rdb_ExecuteQuery(store, sql) for raw SQL, and vtable-based OH_Cursor/OH_VBucket.

use jni::JNIEnv;
use jni::objects::{JClass, JObjectArray, JString};
use jni::sys::{jint, jlong};
use std::ffi::CString;

use crate::oh_ffi;

fn jstr_to_c(env: &mut JNIEnv, s: &JString) -> CString {
    let rs: String = env.get_string(s).expect("Invalid JString").into();
    CString::new(rs).unwrap_or_else(|_| CString::new("").unwrap())
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreOpen(
    mut env: JNIEnv, _class: JClass, db_name: JString, version: jint,
) -> jlong {
    let c_name = jstr_to_c(&mut env, &db_name);

    // Build OH_Rdb_Config. Paths are relative to the app's data directory.
    let data_dir = CString::new("/data/storage/el2/database").unwrap();
    let bundle_name = CString::new("com.example.app").unwrap();
    let module_name = CString::new("entry").unwrap();

    let config = oh_ffi::OH_Rdb_Config {
        self_size: std::mem::size_of::<oh_ffi::OH_Rdb_Config>() as std::os::raw::c_int,
        data_base_dir: data_dir.as_ptr(),
        store_name: c_name.as_ptr(),
        bundle_name: bundle_name.as_ptr(),
        module_name: module_name.as_ptr(),
        is_encrypt: false,
        security_level: 1, // S1
        area: 1,           // EL1
    };

    let mut err_code: std::os::raw::c_int = 0;
    let store = unsafe { oh_ffi::OH_Rdb_GetOrOpen(&config, &mut err_code) };
    if err_code != 0 || store.is_null() {
        return 0;
    }

    // Set version if specified
    if version > 0 {
        unsafe { oh_ffi::OH_Rdb_SetVersion(store, version) };
    }

    store as jlong
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreExecSQL(
    mut env: JNIEnv, _class: JClass, handle: jlong, sql: JString,
) {
    let c_sql = jstr_to_c(&mut env, &sql);
    unsafe { oh_ffi::OH_Rdb_Execute(handle as *mut oh_ffi::OH_Rdb_Store, c_sql.as_ptr()) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreQuery(
    mut env: JNIEnv, _class: JClass, handle: jlong, sql: JString, _args: JObjectArray,
) -> jlong {
    let c_sql = jstr_to_c(&mut env, &sql);
    let cursor = unsafe {
        oh_ffi::OH_Rdb_ExecuteQuery(handle as *mut oh_ffi::OH_Rdb_Store, c_sql.as_ptr())
    };
    cursor as jlong
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreInsert(
    mut env: JNIEnv, _class: JClass, handle: jlong, table: JString, values_json: JString,
) -> jlong {
    let c_table = jstr_to_c(&mut env, &table);
    let c_json = jstr_to_c(&mut env, &values_json);

    // Parse JSON values into OH_VBucket
    let bucket = unsafe { oh_ffi::OH_Rdb_CreateValuesBucket() };
    if bucket.is_null() {
        return -1;
    }

    // Parse the JSON and populate the bucket via vtable calls
    if let Ok(map) = serde_json::from_str::<serde_json::Map<String, serde_json::Value>>(
        c_json.to_str().unwrap_or("{}"),
    ) {
        let bucket_ref = unsafe { &mut *(bucket as *mut oh_ffi::OH_VBucket) };
        for (key, value) in &map {
            let c_key = CString::new(key.as_str()).unwrap();
            match value {
                serde_json::Value::String(s) => {
                    let c_val = CString::new(s.as_str()).unwrap();
                    if let Some(put_text) = bucket_ref.put_text {
                        unsafe { put_text(bucket as *mut oh_ffi::OH_VBucket, c_key.as_ptr(), c_val.as_ptr()) };
                    }
                }
                serde_json::Value::Number(n) => {
                    if let Some(i) = n.as_i64() {
                        if let Some(put_int64) = bucket_ref.put_int64 {
                            unsafe { put_int64(bucket as *mut oh_ffi::OH_VBucket, c_key.as_ptr(), i) };
                        }
                    } else if let Some(f) = n.as_f64() {
                        if let Some(put_real) = bucket_ref.put_real {
                            unsafe { put_real(bucket as *mut oh_ffi::OH_VBucket, c_key.as_ptr(), f) };
                        }
                    }
                }
                serde_json::Value::Null => {
                    if let Some(put_null) = bucket_ref.put_null {
                        unsafe { put_null(bucket as *mut oh_ffi::OH_VBucket, c_key.as_ptr()) };
                    }
                }
                _ => {} // Bool, Array, Object — skip
            }
        }
    }

    let rc = unsafe {
        oh_ffi::OH_Rdb_Insert(handle as *mut oh_ffi::OH_Rdb_Store, c_table.as_ptr(), bucket as *mut oh_ffi::OH_VBucket)
    };

    // Destroy the bucket
    let bucket_ref = unsafe { &mut *(bucket as *mut oh_ffi::OH_VBucket) };
    if let Some(destroy) = bucket_ref.destroy {
        unsafe { destroy(bucket as *mut oh_ffi::OH_VBucket) };
    }

    rc as jlong
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreUpdate(
    mut env: JNIEnv, _class: JClass, handle: jlong,
    _values_json: JString, _table: JString, _where_clause: JString, _where_args: JObjectArray,
) -> jint {
    // TODO: build OH_VBucket from JSON + OH_Predicates from WHERE clause
    // Complex — needs predicate builder. Placeholder.
    0
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreDelete(
    mut env: JNIEnv, _class: JClass, handle: jlong,
    _table: JString, _where_clause: JString, _where_args: JObjectArray,
) -> jint {
    // TODO: build OH_Predicates from WHERE clause
    0
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreBeginTransaction(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::OH_Rdb_BeginTransaction(handle as *mut oh_ffi::OH_Rdb_Store) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreCommit(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::OH_Rdb_Commit(handle as *mut oh_ffi::OH_Rdb_Store) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreRollback(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::OH_Rdb_RollBack(handle as *mut oh_ffi::OH_Rdb_Store) };
}

#[no_mangle]
pub extern "system" fn Java_com_ohos_shim_bridge_OHBridge_rdbStoreClose(
    _env: JNIEnv, _class: JClass, handle: jlong,
) {
    unsafe { oh_ffi::OH_Rdb_CloseStore(handle as *mut oh_ffi::OH_Rdb_Store) };
}
