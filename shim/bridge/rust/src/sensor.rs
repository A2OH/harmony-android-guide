//! Sensor bridge: Android SensorManager → OHOS sensor service.
//!
//! OHOS sensor API (C NDK, since API 11):
//!   OH_Sensor_GetInfos(OH_Sensor_Info**, uint32_t*) → list available sensors
//!   OH_Sensor_Subscribe(sensorId, callback, interval)
//!   OH_Sensor_Unsubscribe(sensorId)
//!
//! Header: sensors/oh_sensor.h
//! Library: libohsensor.z.so

use jni::objects::JClass;
use jni::sys::{jboolean, jfloatArray, jint, JNI_FALSE, JNI_TRUE};
use jni::JNIEnv;

// Sensor type constants (matching Android)
const TYPE_ACCELEROMETER: i32 = 1;
const TYPE_GYROSCOPE: i32 = 4;
const TYPE_LIGHT: i32 = 5;

/// OHBridge.sensorIsAvailable(int sensorType) → boolean
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_sensorIsAvailable(
    _env: JNIEnv,
    _class: JClass,
    sensor_type: jint,
) -> jboolean {
    // On real OHOS: OH_Sensor_GetInfos then check if sensorType exists
    match sensor_type {
        TYPE_ACCELEROMETER | TYPE_GYROSCOPE | TYPE_LIGHT => JNI_TRUE as jboolean,
        _ => JNI_FALSE as jboolean,
    }
}

/// OHBridge.sensorGetData(int sensorType) → float[]
#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_sensorGetData(
    mut env: JNIEnv,
    _class: JClass,
    sensor_type: jint,
) -> jfloatArray {
    // On real OHOS: OH_Sensor_Subscribe callback provides the data
    // For stub: return mock data
    let data: &[f32] = match sensor_type {
        TYPE_ACCELEROMETER => &[0.0, 0.0, 9.8], // gravity on z-axis
        TYPE_GYROSCOPE => &[0.0, 0.0, 0.0],      // no rotation
        TYPE_LIGHT => &[250.0],                    // indoor ambient
        _ => return std::ptr::null_mut(),
    };

    match env.new_float_array(data.len() as i32) {
        Ok(arr) => {
            let _ = env.set_float_array_region(&arr, 0, data);
            arr.into_raw()
        }
        Err(_) => std::ptr::null_mut(),
    }
}
