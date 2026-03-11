# Android 11 (API 30) Public API Enumeration: Android Hardware

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.hardware` | 29 | 81 | 149 | 5 |
| `android.hardware.biometrics` | 6 | 10 | 29 | 6 |
| `android.hardware.camera2` | 17 | 80 | 241 | 17 |
| `android.hardware.camera2.params` | 16 | 84 | 29 | 13 |
| `android.hardware.display` | 3 | 18 | 6 | 1 |
| `android.hardware.fingerprint` | 3 | 0 | 17 | 0 |
| `android.hardware.input` | 2 | 7 | 2 | 0 |
| `android.hardware.usb` | 9 | 68 | 40 | 2 |
| **TOTAL** | **85** | **348** | **513** | **44** |

---

## Package: `android.hardware`

### `class Camera` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static Camera.Area` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.AutoFocusCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.AutoFocusMoveCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static Camera.CameraInfo` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.ErrorCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static Camera.Face` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.FaceDetectionListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.OnZoomChangeListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class Camera.Parameters` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.PictureCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.PreviewCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Camera.ShutterCallback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class Camera.Size` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final ConsumerIrManager`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.hardware.ConsumerIrManager.CarrierFrequencyRange[] getCarrierFrequencies()` |  |
| `boolean hasIrEmitter()` |  |
| `void transmit(int, int[])` |  |

---

### `class final ConsumerIrManager.CarrierFrequencyRange`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConsumerIrManager.CarrierFrequencyRange(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getMaxFrequency()` |  |
| `int getMinFrequency()` |  |

---

### `class GeomagneticField`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GeomagneticField(float, float, float, long)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getDeclination()` |  |
| `float getFieldStrength()` |  |
| `float getHorizontalStrength()` |  |
| `float getInclination()` |  |
| `float getX()` |  |
| `float getY()` |  |
| `float getZ()` |  |

---

### `class final HardwareBuffer`

- **实现：** `java.lang.AutoCloseable android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BLOB = 33` |  |
| `static final int DS_24UI8 = 50` |  |
| `static final int DS_FP32UI8 = 52` |  |
| `static final int D_16 = 48` |  |
| `static final int D_24 = 49` |  |
| `static final int D_FP32 = 51` |  |
| `static final int RGBA_1010102 = 43` |  |
| `static final int RGBA_8888 = 1` |  |
| `static final int RGBA_FP16 = 22` |  |
| `static final int RGBX_8888 = 2` |  |
| `static final int RGB_565 = 4` |  |
| `static final int RGB_888 = 3` |  |
| `static final int S_UI8 = 53` |  |
| `static final long USAGE_CPU_READ_OFTEN = 3L` |  |
| `static final long USAGE_CPU_READ_RARELY = 2L` |  |
| `static final long USAGE_CPU_WRITE_OFTEN = 48L` |  |
| `static final long USAGE_CPU_WRITE_RARELY = 32L` |  |
| `static final long USAGE_GPU_COLOR_OUTPUT = 512L` |  |
| `static final long USAGE_GPU_CUBE_MAP = 33554432L` |  |
| `static final long USAGE_GPU_DATA_BUFFER = 16777216L` |  |
| `static final long USAGE_GPU_MIPMAP_COMPLETE = 67108864L` |  |
| `static final long USAGE_GPU_SAMPLED_IMAGE = 256L` |  |
| `static final long USAGE_PROTECTED_CONTENT = 16384L` |  |
| `static final long USAGE_SENSOR_DIRECT_DATA = 8388608L` |  |
| `static final long USAGE_VIDEO_ENCODE = 65536L` |  |
| `static final int YCBCR_420_888 = 35` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int describeContents()` |  |
| `int getFormat()` |  |
| `int getHeight()` |  |
| `int getLayers()` |  |
| `long getUsage()` |  |
| `int getWidth()` |  |
| `boolean isClosed()` |  |
| `static boolean isSupported(@IntRange(from=1) int, @IntRange(from=1) int, int, @IntRange(from=1) int, long)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final Sensor`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REPORTING_MODE_CONTINUOUS = 0` |  |
| `static final int REPORTING_MODE_ONE_SHOT = 2` |  |
| `static final int REPORTING_MODE_ON_CHANGE = 1` |  |
| `static final int REPORTING_MODE_SPECIAL_TRIGGER = 3` |  |
| `static final String STRING_TYPE_ACCELEROMETER = "android.sensor.accelerometer"` |  |
| `static final String STRING_TYPE_ACCELEROMETER_UNCALIBRATED = "android.sensor.accelerometer_uncalibrated"` |  |
| `static final String STRING_TYPE_AMBIENT_TEMPERATURE = "android.sensor.ambient_temperature"` |  |
| `static final String STRING_TYPE_GAME_ROTATION_VECTOR = "android.sensor.game_rotation_vector"` |  |
| `static final String STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR = "android.sensor.geomagnetic_rotation_vector"` |  |
| `static final String STRING_TYPE_GRAVITY = "android.sensor.gravity"` |  |
| `static final String STRING_TYPE_GYROSCOPE = "android.sensor.gyroscope"` |  |
| `static final String STRING_TYPE_GYROSCOPE_UNCALIBRATED = "android.sensor.gyroscope_uncalibrated"` |  |
| `static final String STRING_TYPE_HEART_BEAT = "android.sensor.heart_beat"` |  |
| `static final String STRING_TYPE_HEART_RATE = "android.sensor.heart_rate"` |  |
| `static final String STRING_TYPE_HINGE_ANGLE = "android.sensor.hinge_angle"` |  |
| `static final String STRING_TYPE_LIGHT = "android.sensor.light"` |  |
| `static final String STRING_TYPE_LINEAR_ACCELERATION = "android.sensor.linear_acceleration"` |  |
| `static final String STRING_TYPE_LOW_LATENCY_OFFBODY_DETECT = "android.sensor.low_latency_offbody_detect"` |  |
| `static final String STRING_TYPE_MAGNETIC_FIELD = "android.sensor.magnetic_field"` |  |
| `static final String STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED = "android.sensor.magnetic_field_uncalibrated"` |  |
| `static final String STRING_TYPE_MOTION_DETECT = "android.sensor.motion_detect"` |  |
| `static final String STRING_TYPE_POSE_6DOF = "android.sensor.pose_6dof"` |  |
| `static final String STRING_TYPE_PRESSURE = "android.sensor.pressure"` |  |
| `static final String STRING_TYPE_PROXIMITY = "android.sensor.proximity"` |  |
| `static final String STRING_TYPE_RELATIVE_HUMIDITY = "android.sensor.relative_humidity"` |  |
| `static final String STRING_TYPE_ROTATION_VECTOR = "android.sensor.rotation_vector"` |  |
| `static final String STRING_TYPE_SIGNIFICANT_MOTION = "android.sensor.significant_motion"` |  |
| `static final String STRING_TYPE_STATIONARY_DETECT = "android.sensor.stationary_detect"` |  |
| `static final String STRING_TYPE_STEP_COUNTER = "android.sensor.step_counter"` |  |
| `static final String STRING_TYPE_STEP_DETECTOR = "android.sensor.step_detector"` |  |
| `static final int TYPE_ACCELEROMETER = 1` |  |
| `static final int TYPE_ACCELEROMETER_UNCALIBRATED = 35` |  |
| `static final int TYPE_ALL = -1` |  |
| `static final int TYPE_AMBIENT_TEMPERATURE = 13` |  |
| `static final int TYPE_DEVICE_PRIVATE_BASE = 65536` |  |
| `static final int TYPE_GAME_ROTATION_VECTOR = 15` |  |
| `static final int TYPE_GEOMAGNETIC_ROTATION_VECTOR = 20` |  |
| `static final int TYPE_GRAVITY = 9` |  |
| `static final int TYPE_GYROSCOPE = 4` |  |
| `static final int TYPE_GYROSCOPE_UNCALIBRATED = 16` |  |
| `static final int TYPE_HEART_BEAT = 31` |  |
| `static final int TYPE_HEART_RATE = 21` |  |
| `static final int TYPE_HINGE_ANGLE = 36` |  |
| `static final int TYPE_LIGHT = 5` |  |
| `static final int TYPE_LINEAR_ACCELERATION = 10` |  |
| `static final int TYPE_LOW_LATENCY_OFFBODY_DETECT = 34` |  |
| `static final int TYPE_MAGNETIC_FIELD = 2` |  |
| `static final int TYPE_MAGNETIC_FIELD_UNCALIBRATED = 14` |  |
| `static final int TYPE_MOTION_DETECT = 30` |  |
| `static final int TYPE_POSE_6DOF = 28` |  |
| `static final int TYPE_PRESSURE = 6` |  |
| `static final int TYPE_PROXIMITY = 8` |  |
| `static final int TYPE_RELATIVE_HUMIDITY = 12` |  |
| `static final int TYPE_ROTATION_VECTOR = 11` |  |
| `static final int TYPE_SIGNIFICANT_MOTION = 17` |  |
| `static final int TYPE_STATIONARY_DETECT = 29` |  |
| `static final int TYPE_STEP_COUNTER = 19` |  |
| `static final int TYPE_STEP_DETECTOR = 18` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getFifoMaxEventCount()` |  |
| `int getFifoReservedEventCount()` |  |
| `int getHighestDirectReportRateLevel()` |  |
| `int getId()` |  |
| `int getMaxDelay()` |  |
| `float getMaximumRange()` |  |
| `int getMinDelay()` |  |
| `String getName()` |  |
| `float getPower()` |  |
| `int getReportingMode()` |  |
| `float getResolution()` |  |
| `String getStringType()` |  |
| `int getType()` |  |
| `String getVendor()` |  |
| `int getVersion()` |  |
| `boolean isAdditionalInfoSupported()` |  |
| `boolean isDirectChannelTypeSupported(int)` |  |
| `boolean isDynamicSensor()` |  |
| `boolean isWakeUpSensor()` |  |

---

### `class SensorAdditionalInfo`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_FRAME_BEGIN = 0` |  |
| `static final int TYPE_FRAME_END = 1` |  |
| `static final int TYPE_INTERNAL_TEMPERATURE = 65537` |  |
| `static final int TYPE_SAMPLING = 65540` |  |
| `static final int TYPE_SENSOR_PLACEMENT = 65539` |  |
| `static final int TYPE_UNTRACKED_DELAY = 65536` |  |
| `static final int TYPE_VEC3_CALIBRATION = 65538` |  |
| `final float[] floatValues` |  |
| `final int[] intValues` |  |
| `final android.hardware.Sensor sensor` |  |
| `final int serial` |  |
| `final int type` |  |

---

### `class final SensorDirectChannel`

- **实现：** `java.nio.channels.Channel`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int RATE_FAST = 2` |  |
| `static final int RATE_NORMAL = 1` |  |
| `static final int RATE_STOP = 0` |  |
| `static final int RATE_VERY_FAST = 3` |  |
| `static final int TYPE_HARDWARE_BUFFER = 2` |  |
| `static final int TYPE_MEMORY_FILE = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int configure(android.hardware.Sensor, int)` |  |
| `boolean isOpen()` |  |

---

### `class SensorEvent`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int accuracy` |  |
| `android.hardware.Sensor sensor` |  |
| `long timestamp` |  |
| `final float[] values` |  |

---

### `class abstract SensorEventCallback`

- **实现：** `android.hardware.SensorEventListener2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SensorEventCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onAccuracyChanged(android.hardware.Sensor, int)` |  |
| `void onFlushCompleted(android.hardware.Sensor)` |  |
| `void onSensorAdditionalInfo(android.hardware.SensorAdditionalInfo)` |  |
| `void onSensorChanged(android.hardware.SensorEvent)` |  |

---

### `interface SensorEventListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onAccuracyChanged(android.hardware.Sensor, int)` |  |
| `void onSensorChanged(android.hardware.SensorEvent)` |  |

---

### `interface SensorEventListener2`

- **继承：** `android.hardware.SensorEventListener`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFlushCompleted(android.hardware.Sensor)` |  |

---

### `interface SensorListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class abstract SensorManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SensorManager.DynamicSensorCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AXIS_MINUS_X = 129` |  |
| `static final int AXIS_MINUS_Y = 130` |  |
| `static final int AXIS_MINUS_Z = 131` |  |
| `static final int AXIS_X = 1` |  |
| `static final int AXIS_Y = 2` |  |
| `static final int AXIS_Z = 3` |  |
| `static final float GRAVITY_DEATH_STAR_I = 3.5303614E-7f` |  |
| `static final float GRAVITY_EARTH = 9.80665f` |  |
| `static final float GRAVITY_JUPITER = 23.12f` |  |
| `static final float GRAVITY_MARS = 3.71f` |  |
| `static final float GRAVITY_MERCURY = 3.7f` |  |
| `static final float GRAVITY_MOON = 1.6f` |  |
| `static final float GRAVITY_NEPTUNE = 11.0f` |  |
| `static final float GRAVITY_PLUTO = 0.6f` |  |
| `static final float GRAVITY_SATURN = 8.96f` |  |
| `static final float GRAVITY_SUN = 275.0f` |  |
| `static final float GRAVITY_THE_ISLAND = 4.815162f` |  |
| `static final float GRAVITY_URANUS = 8.69f` |  |
| `static final float GRAVITY_VENUS = 8.87f` |  |
| `static final float LIGHT_CLOUDY = 100.0f` |  |
| `static final float LIGHT_FULLMOON = 0.25f` |  |
| `static final float LIGHT_NO_MOON = 0.001f` |  |
| `static final float LIGHT_OVERCAST = 10000.0f` |  |
| `static final float LIGHT_SHADE = 20000.0f` |  |
| `static final float LIGHT_SUNLIGHT = 110000.0f` |  |
| `static final float LIGHT_SUNLIGHT_MAX = 120000.0f` |  |
| `static final float LIGHT_SUNRISE = 400.0f` |  |
| `static final float MAGNETIC_FIELD_EARTH_MAX = 60.0f` |  |
| `static final float MAGNETIC_FIELD_EARTH_MIN = 30.0f` |  |
| `static final float PRESSURE_STANDARD_ATMOSPHERE = 1013.25f` |  |
| `static final int SENSOR_DELAY_FASTEST = 0` |  |
| `static final int SENSOR_DELAY_GAME = 1` |  |
| `static final int SENSOR_DELAY_NORMAL = 3` |  |
| `static final int SENSOR_DELAY_UI = 2` |  |
| `static final int SENSOR_STATUS_ACCURACY_HIGH = 3` |  |
| `static final int SENSOR_STATUS_ACCURACY_LOW = 1` |  |
| `static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2` |  |
| `static final int SENSOR_STATUS_NO_CONTACT = -1` |  |
| `static final int SENSOR_STATUS_UNRELIABLE = 0` |  |
| `static final float STANDARD_GRAVITY = 9.80665f` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean cancelTriggerSensor(android.hardware.TriggerEventListener, android.hardware.Sensor)` |  |
| `android.hardware.SensorDirectChannel createDirectChannel(android.os.MemoryFile)` |  |
| `android.hardware.SensorDirectChannel createDirectChannel(android.hardware.HardwareBuffer)` |  |
| `boolean flush(android.hardware.SensorEventListener)` |  |
| `static float getAltitude(float, float)` |  |
| `static void getAngleChange(float[], float[], float[])` |  |
| `android.hardware.Sensor getDefaultSensor(int)` |  |
| `android.hardware.Sensor getDefaultSensor(int, boolean)` |  |
| `java.util.List<android.hardware.Sensor> getDynamicSensorList(int)` |  |
| `static float getInclination(float[])` |  |
| `static float[] getOrientation(float[], float[])` |  |
| `static void getQuaternionFromVector(float[], float[])` |  |
| `static boolean getRotationMatrix(float[], float[], float[], float[])` |  |
| `static void getRotationMatrixFromVector(float[], float[])` |  |
| `java.util.List<android.hardware.Sensor> getSensorList(int)` |  |
| `boolean isDynamicSensorDiscoverySupported()` |  |
| `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` |  |
| `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback, android.os.Handler)` |  |
| `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int)` |  |
| `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int)` |  |
| `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, android.os.Handler)` |  |
| `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int, android.os.Handler)` |  |
| `static boolean remapCoordinateSystem(float[], int, int, float[])` |  |
| `boolean requestTriggerSensor(android.hardware.TriggerEventListener, android.hardware.Sensor)` |  |
| `void unregisterDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` |  |
| `void unregisterListener(android.hardware.SensorEventListener, android.hardware.Sensor)` |  |
| `void unregisterListener(android.hardware.SensorEventListener)` |  |
| `void onDynamicSensorConnected(android.hardware.Sensor)` |  |
| `void onDynamicSensorDisconnected(android.hardware.Sensor)` |  |

---

### `class final TriggerEvent`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `android.hardware.Sensor sensor` |  |
| `long timestamp` |  |
| `final float[] values` |  |

---

### `class abstract TriggerEventListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TriggerEventListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void onTrigger(android.hardware.TriggerEvent)` |  |

---

## Package: `android.hardware.biometrics`

### `class BiometricManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BIOMETRIC_ERROR_HW_UNAVAILABLE = 1` |  |
| `static final int BIOMETRIC_ERROR_NONE_ENROLLED = 11` |  |
| `static final int BIOMETRIC_ERROR_NO_HARDWARE = 12` |  |
| `static final int BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15` |  |
| `static final int BIOMETRIC_SUCCESS = 0` |  |

---

### `interface static BiometricManager.Authenticators`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BIOMETRIC_STRONG = 15` |  |
| `static final int BIOMETRIC_WEAK = 255` |  |
| `static final int DEVICE_CREDENTIAL = 32768` |  |

---

### `class BiometricPrompt`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BiometricPrompt.AuthenticationCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUTHENTICATION_RESULT_TYPE_BIOMETRIC = 2` |  |
| `static final int AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL = 1` |  |
| `static final int BIOMETRIC_ACQUIRED_GOOD = 0` |  |
| `static final int BIOMETRIC_ACQUIRED_IMAGER_DIRTY = 3` |  |
| `static final int BIOMETRIC_ACQUIRED_INSUFFICIENT = 2` |  |
| `static final int BIOMETRIC_ACQUIRED_PARTIAL = 1` |  |
| `static final int BIOMETRIC_ACQUIRED_TOO_FAST = 5` |  |
| `static final int BIOMETRIC_ACQUIRED_TOO_SLOW = 4` |  |
| `static final int BIOMETRIC_ERROR_CANCELED = 5` |  |
| `static final int BIOMETRIC_ERROR_HW_NOT_PRESENT = 12` |  |
| `static final int BIOMETRIC_ERROR_HW_UNAVAILABLE = 1` |  |
| `static final int BIOMETRIC_ERROR_LOCKOUT = 7` |  |
| `static final int BIOMETRIC_ERROR_LOCKOUT_PERMANENT = 9` |  |
| `static final int BIOMETRIC_ERROR_NO_BIOMETRICS = 11` |  |
| `static final int BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = 14` |  |
| `static final int BIOMETRIC_ERROR_NO_SPACE = 4` |  |
| `static final int BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15` |  |
| `static final int BIOMETRIC_ERROR_TIMEOUT = 3` |  |
| `static final int BIOMETRIC_ERROR_UNABLE_TO_PROCESS = 2` |  |
| `static final int BIOMETRIC_ERROR_USER_CANCELED = 10` |  |
| `static final int BIOMETRIC_ERROR_VENDOR = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isConfirmationRequired()` |  |
| `void onAuthenticationError(int, CharSequence)` |  |
| `void onAuthenticationFailed()` |  |
| `void onAuthenticationHelp(int, CharSequence)` |  |
| `void onAuthenticationSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult)` |  |

---

### `class static BiometricPrompt.AuthenticationResult`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getAuthenticationType()` |  |
| `android.hardware.biometrics.BiometricPrompt.CryptoObject getCryptoObject()` |  |

---

### `class static BiometricPrompt.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BiometricPrompt.Builder(android.content.Context)` |  |

---

### `class static final BiometricPrompt.CryptoObject`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BiometricPrompt.CryptoObject(@NonNull java.security.Signature)` |  |
| `BiometricPrompt.CryptoObject(@NonNull javax.crypto.Cipher)` |  |
| `BiometricPrompt.CryptoObject(@NonNull javax.crypto.Mac)` |  |
| `BiometricPrompt.CryptoObject(@NonNull android.security.identity.IdentityCredential)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.crypto.Cipher getCipher()` |  |
| `javax.crypto.Mac getMac()` |  |
| `java.security.Signature getSignature()` |  |

---

## Package: `android.hardware.camera2`

### `class CameraAccessException`

- **继承：** `android.util.AndroidException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraAccessException(int)` |  |
| `CameraAccessException(int, String)` |  |
| `CameraAccessException(int, String, Throwable)` |  |
| `CameraAccessException(int, Throwable)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CAMERA_DISABLED = 1` |  |
| `static final int CAMERA_DISCONNECTED = 2` |  |
| `static final int CAMERA_ERROR = 3` |  |
| `static final int CAMERA_IN_USE = 4` |  |
| `static final int MAX_CAMERAS_IN_USE = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final int getReason()` |  |

---

### `class abstract CameraCaptureSession`

- **实现：** `java.lang.AutoCloseable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraCaptureSession()` |  |
| `CameraCaptureSession.CaptureCallback()` |  |
| `CameraCaptureSession.StateCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void abortCaptures() throws android.hardware.camera2.CameraAccessException` |  |
| `abstract int capture(@NonNull android.hardware.camera2.CaptureRequest, @Nullable android.hardware.camera2.CameraCaptureSession.CaptureCallback, @Nullable android.os.Handler) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract int captureBurst(@NonNull java.util.List<android.hardware.camera2.CaptureRequest>, @Nullable android.hardware.camera2.CameraCaptureSession.CaptureCallback, @Nullable android.os.Handler) throws android.hardware.camera2.CameraAccessException` |  |
| `int captureBurstRequests(@NonNull java.util.List<android.hardware.camera2.CaptureRequest>, @NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraCaptureSession.CaptureCallback) throws android.hardware.camera2.CameraAccessException` |  |
| `int captureSingleRequest(@NonNull android.hardware.camera2.CaptureRequest, @NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraCaptureSession.CaptureCallback) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract void close()` |  |
| `abstract void finalizeOutputConfigurations(java.util.List<android.hardware.camera2.params.OutputConfiguration>) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract boolean isReprocessable()` |  |
| `abstract void prepare(@NonNull android.view.Surface) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract int setRepeatingBurst(@NonNull java.util.List<android.hardware.camera2.CaptureRequest>, @Nullable android.hardware.camera2.CameraCaptureSession.CaptureCallback, @Nullable android.os.Handler) throws android.hardware.camera2.CameraAccessException` |  |
| `int setRepeatingBurstRequests(@NonNull java.util.List<android.hardware.camera2.CaptureRequest>, @NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraCaptureSession.CaptureCallback) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract int setRepeatingRequest(@NonNull android.hardware.camera2.CaptureRequest, @Nullable android.hardware.camera2.CameraCaptureSession.CaptureCallback, @Nullable android.os.Handler) throws android.hardware.camera2.CameraAccessException` |  |
| `int setSingleRepeatingRequest(@NonNull android.hardware.camera2.CaptureRequest, @NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraCaptureSession.CaptureCallback) throws android.hardware.camera2.CameraAccessException` |  |
| `abstract void stopRepeating() throws android.hardware.camera2.CameraAccessException` |  |
| `boolean supportsOfflineProcessing(@NonNull android.view.Surface)` |  |
| `void updateOutputConfiguration(android.hardware.camera2.params.OutputConfiguration) throws android.hardware.camera2.CameraAccessException` |  |
| `void onCaptureBufferLost(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.hardware.camera2.CaptureRequest, @NonNull android.view.Surface, long)` |  |
| `void onCaptureCompleted(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.hardware.camera2.CaptureRequest, @NonNull android.hardware.camera2.TotalCaptureResult)` |  |
| `void onCaptureFailed(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.hardware.camera2.CaptureRequest, @NonNull android.hardware.camera2.CaptureFailure)` |  |
| `void onCaptureProgressed(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.hardware.camera2.CaptureRequest, @NonNull android.hardware.camera2.CaptureResult)` |  |
| `void onCaptureSequenceAborted(@NonNull android.hardware.camera2.CameraCaptureSession, int)` |  |
| `void onCaptureSequenceCompleted(@NonNull android.hardware.camera2.CameraCaptureSession, int, long)` |  |
| `void onCaptureStarted(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.hardware.camera2.CaptureRequest, long, long)` |  |
| `void onActive(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `void onCaptureQueueEmpty(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `void onClosed(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `abstract void onConfigureFailed(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `abstract void onConfigured(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `void onReady(@NonNull android.hardware.camera2.CameraCaptureSession)` |  |
| `void onSurfacePrepared(@NonNull android.hardware.camera2.CameraCaptureSession, @NonNull android.view.Surface)` |  |

---

### `class final CameraCharacteristics`

- **继承：** `android.hardware.camera2.CameraMetadata<android.hardware.camera2.CameraCharacteristics.Key<?>>`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.List<android.hardware.camera2.CaptureRequest.Key<?>> getAvailablePhysicalCameraRequestKeys()` |  |
| `java.util.List<android.hardware.camera2.CaptureRequest.Key<?>> getAvailableSessionKeys()` |  |

---

### `class static final CameraCharacteristics.Key<T>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraCharacteristics.Key(@NonNull String, @NonNull Class<T>)` |  |

---

### `class abstract CameraConstrainedHighSpeedCaptureSession`

- **继承：** `android.hardware.camera2.CameraCaptureSession`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraConstrainedHighSpeedCaptureSession()` |  |

---

### `class abstract CameraDevice`

- **实现：** `java.lang.AutoCloseable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraDevice.StateCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_RESTRICTION_NONE = 0` |  |
| `static final int AUDIO_RESTRICTION_VIBRATION = 1` |  |
| `static final int AUDIO_RESTRICTION_VIBRATION_SOUND = 3` |  |
| `static final int TEMPLATE_MANUAL = 6` |  |
| `static final int TEMPLATE_PREVIEW = 1` |  |
| `static final int TEMPLATE_RECORD = 3` |  |
| `static final int TEMPLATE_STILL_CAPTURE = 2` |  |
| `static final int TEMPLATE_VIDEO_SNAPSHOT = 4` |  |
| `static final int TEMPLATE_ZERO_SHUTTER_LAG = 5` |  |
| `static final int ERROR_CAMERA_DEVICE = 4` |  |
| `static final int ERROR_CAMERA_DISABLED = 3` |  |
| `static final int ERROR_CAMERA_IN_USE = 1` |  |
| `static final int ERROR_CAMERA_SERVICE = 5` |  |
| `static final int ERROR_MAX_CAMERAS_IN_USE = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void close()` |  |
| `void createCaptureSession(android.hardware.camera2.params.SessionConfiguration) throws android.hardware.camera2.CameraAccessException` |  |
| `int getCameraAudioRestriction() throws android.hardware.camera2.CameraAccessException` |  |
| `boolean isSessionConfigurationSupported(@NonNull android.hardware.camera2.params.SessionConfiguration) throws android.hardware.camera2.CameraAccessException` |  |
| `void setCameraAudioRestriction(int) throws android.hardware.camera2.CameraAccessException` |  |
| `void onClosed(@NonNull android.hardware.camera2.CameraDevice)` |  |
| `abstract void onDisconnected(@NonNull android.hardware.camera2.CameraDevice)` |  |
| `abstract void onError(@NonNull android.hardware.camera2.CameraDevice, int)` |  |
| `abstract void onOpened(@NonNull android.hardware.camera2.CameraDevice)` |  |

---

### `class final CameraManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraManager.AvailabilityCallback()` |  |
| `CameraManager.TorchCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void registerAvailabilityCallback(@NonNull android.hardware.camera2.CameraManager.AvailabilityCallback, @Nullable android.os.Handler)` |  |
| `void registerAvailabilityCallback(@NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraManager.AvailabilityCallback)` |  |
| `void registerTorchCallback(@NonNull android.hardware.camera2.CameraManager.TorchCallback, @Nullable android.os.Handler)` |  |
| `void registerTorchCallback(@NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraManager.TorchCallback)` |  |
| `void setTorchMode(@NonNull String, boolean) throws android.hardware.camera2.CameraAccessException` |  |
| `void unregisterAvailabilityCallback(@NonNull android.hardware.camera2.CameraManager.AvailabilityCallback)` |  |
| `void unregisterTorchCallback(@NonNull android.hardware.camera2.CameraManager.TorchCallback)` |  |
| `void onCameraAccessPrioritiesChanged()` |  |
| `void onCameraAvailable(@NonNull String)` |  |
| `void onCameraUnavailable(@NonNull String)` |  |
| `void onPhysicalCameraAvailable(@NonNull String, @NonNull String)` |  |
| `void onPhysicalCameraUnavailable(@NonNull String, @NonNull String)` |  |
| `void onTorchModeChanged(@NonNull String, boolean)` |  |
| `void onTorchModeUnavailable(@NonNull String)` |  |

---

### `class abstract CameraMetadata<TKey>`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int COLOR_CORRECTION_ABERRATION_MODE_FAST = 1` |  |
| `static final int COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY = 2` |  |
| `static final int COLOR_CORRECTION_ABERRATION_MODE_OFF = 0` |  |
| `static final int COLOR_CORRECTION_MODE_FAST = 1` |  |
| `static final int COLOR_CORRECTION_MODE_HIGH_QUALITY = 2` |  |
| `static final int COLOR_CORRECTION_MODE_TRANSFORM_MATRIX = 0` |  |
| `static final int CONTROL_AE_ANTIBANDING_MODE_50HZ = 1` |  |
| `static final int CONTROL_AE_ANTIBANDING_MODE_60HZ = 2` |  |
| `static final int CONTROL_AE_ANTIBANDING_MODE_AUTO = 3` |  |
| `static final int CONTROL_AE_ANTIBANDING_MODE_OFF = 0` |  |
| `static final int CONTROL_AE_MODE_OFF = 0` |  |
| `static final int CONTROL_AE_MODE_ON = 1` |  |
| `static final int CONTROL_AE_MODE_ON_ALWAYS_FLASH = 3` |  |
| `static final int CONTROL_AE_MODE_ON_AUTO_FLASH = 2` |  |
| `static final int CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE = 4` |  |
| `static final int CONTROL_AE_MODE_ON_EXTERNAL_FLASH = 5` |  |
| `static final int CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL = 2` |  |
| `static final int CONTROL_AE_PRECAPTURE_TRIGGER_IDLE = 0` |  |
| `static final int CONTROL_AE_PRECAPTURE_TRIGGER_START = 1` |  |
| `static final int CONTROL_AE_STATE_CONVERGED = 2` |  |
| `static final int CONTROL_AE_STATE_FLASH_REQUIRED = 4` |  |
| `static final int CONTROL_AE_STATE_INACTIVE = 0` |  |
| `static final int CONTROL_AE_STATE_LOCKED = 3` |  |
| `static final int CONTROL_AE_STATE_PRECAPTURE = 5` |  |
| `static final int CONTROL_AE_STATE_SEARCHING = 1` |  |
| `static final int CONTROL_AF_MODE_AUTO = 1` |  |
| `static final int CONTROL_AF_MODE_CONTINUOUS_PICTURE = 4` |  |
| `static final int CONTROL_AF_MODE_CONTINUOUS_VIDEO = 3` |  |
| `static final int CONTROL_AF_MODE_EDOF = 5` |  |
| `static final int CONTROL_AF_MODE_MACRO = 2` |  |
| `static final int CONTROL_AF_MODE_OFF = 0` |  |
| `static final int CONTROL_AF_SCENE_CHANGE_DETECTED = 1` |  |
| `static final int CONTROL_AF_SCENE_CHANGE_NOT_DETECTED = 0` |  |
| `static final int CONTROL_AF_STATE_ACTIVE_SCAN = 3` |  |
| `static final int CONTROL_AF_STATE_FOCUSED_LOCKED = 4` |  |
| `static final int CONTROL_AF_STATE_INACTIVE = 0` |  |
| `static final int CONTROL_AF_STATE_NOT_FOCUSED_LOCKED = 5` |  |
| `static final int CONTROL_AF_STATE_PASSIVE_FOCUSED = 2` |  |
| `static final int CONTROL_AF_STATE_PASSIVE_SCAN = 1` |  |
| `static final int CONTROL_AF_STATE_PASSIVE_UNFOCUSED = 6` |  |
| `static final int CONTROL_AF_TRIGGER_CANCEL = 2` |  |
| `static final int CONTROL_AF_TRIGGER_IDLE = 0` |  |
| `static final int CONTROL_AF_TRIGGER_START = 1` |  |
| `static final int CONTROL_AWB_MODE_AUTO = 1` |  |
| `static final int CONTROL_AWB_MODE_CLOUDY_DAYLIGHT = 6` |  |
| `static final int CONTROL_AWB_MODE_DAYLIGHT = 5` |  |
| `static final int CONTROL_AWB_MODE_FLUORESCENT = 3` |  |
| `static final int CONTROL_AWB_MODE_INCANDESCENT = 2` |  |
| `static final int CONTROL_AWB_MODE_OFF = 0` |  |
| `static final int CONTROL_AWB_MODE_SHADE = 8` |  |
| `static final int CONTROL_AWB_MODE_TWILIGHT = 7` |  |
| `static final int CONTROL_AWB_MODE_WARM_FLUORESCENT = 4` |  |
| `static final int CONTROL_AWB_STATE_CONVERGED = 2` |  |
| `static final int CONTROL_AWB_STATE_INACTIVE = 0` |  |
| `static final int CONTROL_AWB_STATE_LOCKED = 3` |  |
| `static final int CONTROL_AWB_STATE_SEARCHING = 1` |  |
| `static final int CONTROL_CAPTURE_INTENT_CUSTOM = 0` |  |
| `static final int CONTROL_CAPTURE_INTENT_MANUAL = 6` |  |
| `static final int CONTROL_CAPTURE_INTENT_MOTION_TRACKING = 7` |  |
| `static final int CONTROL_CAPTURE_INTENT_PREVIEW = 1` |  |
| `static final int CONTROL_CAPTURE_INTENT_STILL_CAPTURE = 2` |  |
| `static final int CONTROL_CAPTURE_INTENT_VIDEO_RECORD = 3` |  |
| `static final int CONTROL_CAPTURE_INTENT_VIDEO_SNAPSHOT = 4` |  |
| `static final int CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG = 5` |  |
| `static final int CONTROL_EFFECT_MODE_AQUA = 8` |  |
| `static final int CONTROL_EFFECT_MODE_BLACKBOARD = 7` |  |
| `static final int CONTROL_EFFECT_MODE_MONO = 1` |  |
| `static final int CONTROL_EFFECT_MODE_NEGATIVE = 2` |  |
| `static final int CONTROL_EFFECT_MODE_OFF = 0` |  |
| `static final int CONTROL_EFFECT_MODE_POSTERIZE = 5` |  |
| `static final int CONTROL_EFFECT_MODE_SEPIA = 4` |  |
| `static final int CONTROL_EFFECT_MODE_SOLARIZE = 3` |  |
| `static final int CONTROL_EFFECT_MODE_WHITEBOARD = 6` |  |
| `static final int CONTROL_EXTENDED_SCENE_MODE_BOKEH_CONTINUOUS = 2` |  |
| `static final int CONTROL_EXTENDED_SCENE_MODE_BOKEH_STILL_CAPTURE = 1` |  |
| `static final int CONTROL_EXTENDED_SCENE_MODE_DISABLED = 0` |  |
| `static final int CONTROL_MODE_AUTO = 1` |  |
| `static final int CONTROL_MODE_OFF = 0` |  |
| `static final int CONTROL_MODE_OFF_KEEP_STATE = 3` |  |
| `static final int CONTROL_MODE_USE_EXTENDED_SCENE_MODE = 4` |  |
| `static final int CONTROL_MODE_USE_SCENE_MODE = 2` |  |
| `static final int CONTROL_SCENE_MODE_ACTION = 2` |  |
| `static final int CONTROL_SCENE_MODE_BARCODE = 16` |  |
| `static final int CONTROL_SCENE_MODE_BEACH = 8` |  |
| `static final int CONTROL_SCENE_MODE_CANDLELIGHT = 15` |  |
| `static final int CONTROL_SCENE_MODE_DISABLED = 0` |  |
| `static final int CONTROL_SCENE_MODE_FACE_PRIORITY = 1` |  |
| `static final int CONTROL_SCENE_MODE_FIREWORKS = 12` |  |
| `static final int CONTROL_SCENE_MODE_HDR = 18` |  |
| `static final int CONTROL_SCENE_MODE_LANDSCAPE = 4` |  |
| `static final int CONTROL_SCENE_MODE_NIGHT = 5` |  |
| `static final int CONTROL_SCENE_MODE_NIGHT_PORTRAIT = 6` |  |
| `static final int CONTROL_SCENE_MODE_PARTY = 14` |  |
| `static final int CONTROL_SCENE_MODE_PORTRAIT = 3` |  |
| `static final int CONTROL_SCENE_MODE_SNOW = 9` |  |
| `static final int CONTROL_SCENE_MODE_SPORTS = 13` |  |
| `static final int CONTROL_SCENE_MODE_STEADYPHOTO = 11` |  |
| `static final int CONTROL_SCENE_MODE_SUNSET = 10` |  |
| `static final int CONTROL_SCENE_MODE_THEATRE = 7` |  |
| `static final int CONTROL_VIDEO_STABILIZATION_MODE_OFF = 0` |  |
| `static final int CONTROL_VIDEO_STABILIZATION_MODE_ON = 1` |  |
| `static final int DISTORTION_CORRECTION_MODE_FAST = 1` |  |
| `static final int DISTORTION_CORRECTION_MODE_HIGH_QUALITY = 2` |  |
| `static final int DISTORTION_CORRECTION_MODE_OFF = 0` |  |
| `static final int EDGE_MODE_FAST = 1` |  |
| `static final int EDGE_MODE_HIGH_QUALITY = 2` |  |
| `static final int EDGE_MODE_OFF = 0` |  |
| `static final int EDGE_MODE_ZERO_SHUTTER_LAG = 3` |  |
| `static final int FLASH_MODE_OFF = 0` |  |
| `static final int FLASH_MODE_SINGLE = 1` |  |
| `static final int FLASH_MODE_TORCH = 2` |  |
| `static final int FLASH_STATE_CHARGING = 1` |  |
| `static final int FLASH_STATE_FIRED = 3` |  |
| `static final int FLASH_STATE_PARTIAL = 4` |  |
| `static final int FLASH_STATE_READY = 2` |  |
| `static final int FLASH_STATE_UNAVAILABLE = 0` |  |
| `static final int HOT_PIXEL_MODE_FAST = 1` |  |
| `static final int HOT_PIXEL_MODE_HIGH_QUALITY = 2` |  |
| `static final int HOT_PIXEL_MODE_OFF = 0` |  |
| `static final int INFO_SUPPORTED_HARDWARE_LEVEL_3 = 3` |  |
| `static final int INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL = 4` |  |
| `static final int INFO_SUPPORTED_HARDWARE_LEVEL_FULL = 1` |  |
| `static final int INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY = 2` |  |
| `static final int INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED = 0` |  |
| `static final int LENS_FACING_BACK = 1` |  |
| `static final int LENS_FACING_EXTERNAL = 2` |  |
| `static final int LENS_FACING_FRONT = 0` |  |
| `static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE = 1` |  |
| `static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED = 2` |  |
| `static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED = 0` |  |
| `static final int LENS_OPTICAL_STABILIZATION_MODE_OFF = 0` |  |
| `static final int LENS_OPTICAL_STABILIZATION_MODE_ON = 1` |  |
| `static final int LENS_POSE_REFERENCE_GYROSCOPE = 1` |  |
| `static final int LENS_POSE_REFERENCE_PRIMARY_CAMERA = 0` |  |
| `static final int LENS_POSE_REFERENCE_UNDEFINED = 2` |  |
| `static final int LENS_STATE_MOVING = 1` |  |
| `static final int LENS_STATE_STATIONARY = 0` |  |
| `static final int LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_APPROXIMATE = 0` |  |
| `static final int LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_CALIBRATED = 1` |  |
| `static final int NOISE_REDUCTION_MODE_FAST = 1` |  |
| `static final int NOISE_REDUCTION_MODE_HIGH_QUALITY = 2` |  |
| `static final int NOISE_REDUCTION_MODE_MINIMAL = 3` |  |
| `static final int NOISE_REDUCTION_MODE_OFF = 0` |  |
| `static final int NOISE_REDUCTION_MODE_ZERO_SHUTTER_LAG = 4` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE = 0` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE = 6` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO = 9` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT = 8` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA = 11` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING = 2` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR = 1` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME = 12` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING = 10` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING = 15` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING = 4` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_RAW = 3` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS = 5` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA = 13` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA = 14` |  |
| `static final int REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING = 7` |  |
| `static final int SCALER_CROPPING_TYPE_CENTER_ONLY = 0` |  |
| `static final int SCALER_CROPPING_TYPE_FREEFORM = 1` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR = 3` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG = 2` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG = 1` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO = 5` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR = 6` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB = 4` |  |
| `static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB = 0` |  |
| `static final int SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME = 1` |  |
| `static final int SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN = 0` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_CLOUDY_WEATHER = 10` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_COOL_WHITE_FLUORESCENT = 14` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_D50 = 23` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_D55 = 20` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_D65 = 21` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_D75 = 22` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT = 1` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT_FLUORESCENT = 12` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_DAY_WHITE_FLUORESCENT = 13` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_FINE_WEATHER = 9` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_FLASH = 4` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_FLUORESCENT = 2` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_ISO_STUDIO_TUNGSTEN = 24` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_SHADE = 11` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_A = 17` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_B = 18` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_C = 19` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_TUNGSTEN = 3` |  |
| `static final int SENSOR_REFERENCE_ILLUMINANT1_WHITE_FLUORESCENT = 15` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_COLOR_BARS = 2` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_COLOR_BARS_FADE_TO_GRAY = 3` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_CUSTOM1 = 256` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_OFF = 0` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_PN9 = 4` |  |
| `static final int SENSOR_TEST_PATTERN_MODE_SOLID_COLOR = 1` |  |
| `static final int SHADING_MODE_FAST = 1` |  |
| `static final int SHADING_MODE_HIGH_QUALITY = 2` |  |
| `static final int SHADING_MODE_OFF = 0` |  |
| `static final int STATISTICS_FACE_DETECT_MODE_FULL = 2` |  |
| `static final int STATISTICS_FACE_DETECT_MODE_OFF = 0` |  |
| `static final int STATISTICS_FACE_DETECT_MODE_SIMPLE = 1` |  |
| `static final int STATISTICS_LENS_SHADING_MAP_MODE_OFF = 0` |  |
| `static final int STATISTICS_LENS_SHADING_MAP_MODE_ON = 1` |  |
| `static final int STATISTICS_OIS_DATA_MODE_OFF = 0` |  |
| `static final int STATISTICS_OIS_DATA_MODE_ON = 1` |  |
| `static final int STATISTICS_SCENE_FLICKER_50HZ = 1` |  |
| `static final int STATISTICS_SCENE_FLICKER_60HZ = 2` |  |
| `static final int STATISTICS_SCENE_FLICKER_NONE = 0` |  |
| `static final int SYNC_MAX_LATENCY_PER_FRAME_CONTROL = 0` |  |
| `static final int SYNC_MAX_LATENCY_UNKNOWN = -1` |  |
| `static final int TONEMAP_MODE_CONTRAST_CURVE = 0` |  |
| `static final int TONEMAP_MODE_FAST = 1` |  |
| `static final int TONEMAP_MODE_GAMMA_VALUE = 3` |  |
| `static final int TONEMAP_MODE_HIGH_QUALITY = 2` |  |
| `static final int TONEMAP_MODE_PRESET_CURVE = 4` |  |
| `static final int TONEMAP_PRESET_CURVE_REC709 = 1` |  |
| `static final int TONEMAP_PRESET_CURVE_SRGB = 0` |  |

---

### `class abstract CameraOfflineSession`

- **继承：** `android.hardware.camera2.CameraCaptureSession`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraOfflineSession()` |  |
| `CameraOfflineSession.CameraOfflineSessionCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATUS_INTERNAL_ERROR = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void onClosed(@NonNull android.hardware.camera2.CameraOfflineSession)` |  |
| `abstract void onError(@NonNull android.hardware.camera2.CameraOfflineSession, int)` |  |
| `abstract void onIdle(@NonNull android.hardware.camera2.CameraOfflineSession)` |  |
| `abstract void onReady(@NonNull android.hardware.camera2.CameraOfflineSession)` |  |
| `abstract void onSwitchFailed(@NonNull android.hardware.camera2.CameraOfflineSession)` |  |

---

### `class CaptureFailure`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REASON_ERROR = 0` |  |
| `static final int REASON_FLUSHED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getFrameNumber()` |  |
| `int getReason()` |  |
| `int getSequenceId()` |  |
| `boolean wasImageCaptured()` |  |

---

### `class final CaptureRequest`

- **继承：** `android.hardware.camera2.CameraMetadata<android.hardware.camera2.CaptureRequest.Key<?>>`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isReprocess()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final CaptureRequest.Builder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addTarget(@NonNull android.view.Surface)` |  |
| `void removeTarget(@NonNull android.view.Surface)` |  |
| `<T> void set(@NonNull android.hardware.camera2.CaptureRequest.Key<T>, T)` |  |
| `<T> android.hardware.camera2.CaptureRequest.Builder setPhysicalCameraKey(@NonNull android.hardware.camera2.CaptureRequest.Key<T>, T, @NonNull String)` |  |
| `void setTag(@Nullable Object)` |  |

---

### `class static final CaptureRequest.Key<T>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CaptureRequest.Key(@NonNull String, @NonNull Class<T>)` |  |

---

### `class CaptureResult`

- **继承：** `android.hardware.camera2.CameraMetadata<android.hardware.camera2.CaptureResult.Key<?>>`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getFrameNumber()` |  |
| `int getSequenceId()` |  |

---

### `class static final CaptureResult.Key<T>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CaptureResult.Key(@NonNull String, @NonNull Class<T>)` |  |

---

### `class final DngCreator`

- **实现：** `java.lang.AutoCloseable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DngCreator(@NonNull android.hardware.camera2.CameraCharacteristics, @NonNull android.hardware.camera2.CaptureResult)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MAX_THUMBNAIL_DIMENSION = 256` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void writeByteBuffer(@NonNull java.io.OutputStream, @NonNull android.util.Size, @NonNull java.nio.ByteBuffer, @IntRange(from=0) long) throws java.io.IOException` |  |
| `void writeImage(@NonNull java.io.OutputStream, @NonNull android.media.Image) throws java.io.IOException` |  |
| `void writeInputStream(@NonNull java.io.OutputStream, @NonNull android.util.Size, @NonNull java.io.InputStream, @IntRange(from=0) long) throws java.io.IOException` |  |

---

### `class final TotalCaptureResult`

- **继承：** `android.hardware.camera2.CaptureResult`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.lang.String,android.hardware.camera2.CaptureResult> getPhysicalCameraResults()` |  |

---

## Package: `android.hardware.camera2.params`

### `class final BlackLevelPattern`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int COUNT = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyTo(int[], int)` |  |
| `int getOffsetForIndex(int, int)` |  |

---

### `class final Capability`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getMode()` |  |

---

### `class final ColorSpaceTransform`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorSpaceTransform(android.util.Rational[])` |  |
| `ColorSpaceTransform(int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyElements(android.util.Rational[], int)` |  |
| `void copyElements(int[], int)` |  |
| `android.util.Rational getElement(int, int)` |  |

---

### `class final Face`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ID_UNSUPPORTED = -1` |  |
| `static final int SCORE_MAX = 100` |  |
| `static final int SCORE_MIN = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Rect getBounds()` |  |
| `int getId()` |  |
| `android.graphics.Point getLeftEyePosition()` |  |
| `android.graphics.Point getMouthPosition()` |  |
| `android.graphics.Point getRightEyePosition()` |  |
| `int getScore()` |  |

---

### `class final InputConfiguration`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InputConfiguration(int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getFormat()` |  |
| `int getHeight()` |  |
| `int getWidth()` |  |

---

### `class final LensShadingMap`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final float MINIMUM_GAIN_FACTOR = 1.0f` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyGainFactors(float[], int)` |  |
| `int getColumnCount()` |  |
| `float getGainFactor(int, int, int)` |  |
| `int getGainFactorCount()` |  |
| `android.hardware.camera2.params.RggbChannelVector getGainFactorVector(int, int)` |  |
| `int getRowCount()` |  |

---

### `class final MandatoryStreamCombination`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isReprocessable()` |  |

---

### `class static final MandatoryStreamCombination.MandatoryStreamInformation`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getFormat()` |  |
| `boolean isInput()` |  |

---

### `class final MeteringRectangle`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MeteringRectangle(int, int, int, int, int)` |  |
| `MeteringRectangle(android.graphics.Point, android.util.Size, int)` |  |
| `MeteringRectangle(android.graphics.Rect, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int METERING_WEIGHT_DONT_CARE = 0` |  |
| `static final int METERING_WEIGHT_MAX = 1000` |  |
| `static final int METERING_WEIGHT_MIN = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean equals(android.hardware.camera2.params.MeteringRectangle)` |  |
| `int getHeight()` |  |
| `int getMeteringWeight()` |  |
| `android.graphics.Rect getRect()` |  |
| `android.util.Size getSize()` |  |
| `android.graphics.Point getUpperLeftPoint()` |  |
| `int getWidth()` |  |
| `int getX()` |  |
| `int getY()` |  |

---

### `class final OisSample`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `OisSample(long, float, float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getTimestamp()` |  |
| `float getXshift()` |  |
| `float getYshift()` |  |

---

### `class final OutputConfiguration`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `OutputConfiguration(@NonNull android.view.Surface)` |  |
| `OutputConfiguration(int, @NonNull android.view.Surface)` |  |
| `OutputConfiguration(@NonNull android.util.Size, @NonNull Class<T>)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SURFACE_GROUP_ID_NONE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addSurface(@NonNull android.view.Surface)` |  |
| `int describeContents()` |  |
| `void enableSurfaceSharing()` |  |
| `int getMaxSharedSurfaceCount()` |  |
| `int getSurfaceGroupId()` |  |
| `void removeSurface(@NonNull android.view.Surface)` |  |
| `void setPhysicalCameraId(@Nullable String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final RecommendedStreamConfigurationMap`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int USECASE_LOW_LATENCY_SNAPSHOT = 6` |  |
| `static final int USECASE_PREVIEW = 0` |  |
| `static final int USECASE_RAW = 5` |  |
| `static final int USECASE_RECORD = 1` |  |
| `static final int USECASE_SNAPSHOT = 3` |  |
| `static final int USECASE_VIDEO_SNAPSHOT = 2` |  |
| `static final int USECASE_ZSL = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getRecommendedUseCase()` |  |
| `boolean isOutputSupportedFor(int)` |  |
| `boolean isOutputSupportedFor(@NonNull android.view.Surface)` |  |

---

### `class final RggbChannelVector`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RggbChannelVector(float, float, float, float)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BLUE = 3` |  |
| `static final int COUNT = 4` |  |
| `static final int GREEN_EVEN = 1` |  |
| `static final int GREEN_ODD = 2` |  |
| `static final int RED = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyTo(float[], int)` |  |
| `float getBlue()` |  |
| `float getComponent(int)` |  |
| `float getGreenEven()` |  |
| `float getGreenOdd()` |  |
| `float getRed()` |  |

---

### `class final SessionConfiguration`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SessionConfiguration(int, @NonNull java.util.List<android.hardware.camera2.params.OutputConfiguration>, @NonNull java.util.concurrent.Executor, @NonNull android.hardware.camera2.CameraCaptureSession.StateCallback)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SESSION_HIGH_SPEED = 1` |  |
| `static final int SESSION_REGULAR = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.concurrent.Executor getExecutor()` |  |
| `android.hardware.camera2.params.InputConfiguration getInputConfiguration()` |  |
| `java.util.List<android.hardware.camera2.params.OutputConfiguration> getOutputConfigurations()` |  |
| `android.hardware.camera2.CaptureRequest getSessionParameters()` |  |
| `int getSessionType()` |  |
| `android.hardware.camera2.CameraCaptureSession.StateCallback getStateCallback()` |  |
| `void setInputConfiguration(@NonNull android.hardware.camera2.params.InputConfiguration)` |  |
| `void setSessionParameters(android.hardware.camera2.CaptureRequest)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final StreamConfigurationMap`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.util.Size[] getHighResolutionOutputSizes(int)` |  |
| `android.util.Range<java.lang.Integer>[] getHighSpeedVideoFpsRanges()` |  |
| `android.util.Range<java.lang.Integer>[] getHighSpeedVideoFpsRangesFor(android.util.Size)` |  |
| `android.util.Size[] getHighSpeedVideoSizes()` |  |
| `android.util.Size[] getHighSpeedVideoSizesFor(android.util.Range<java.lang.Integer>)` |  |
| `int[] getInputFormats()` |  |
| `android.util.Size[] getInputSizes(int)` |  |
| `int[] getOutputFormats()` |  |
| `long getOutputMinFrameDuration(int, android.util.Size)` |  |
| `<T> long getOutputMinFrameDuration(Class<T>, android.util.Size)` |  |
| `<T> android.util.Size[] getOutputSizes(Class<T>)` |  |
| `android.util.Size[] getOutputSizes(int)` |  |
| `long getOutputStallDuration(int, android.util.Size)` |  |
| `<T> long getOutputStallDuration(Class<T>, android.util.Size)` |  |
| `int[] getValidOutputFormatsForInput(int)` |  |
| `boolean isOutputSupportedFor(int)` |  |
| `static <T> boolean isOutputSupportedFor(Class<T>)` |  |
| `boolean isOutputSupportedFor(android.view.Surface)` |  |

---

### `class final TonemapCurve`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TonemapCurve(float[], float[], float[])` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CHANNEL_BLUE = 2` |  |
| `static final int CHANNEL_GREEN = 1` |  |
| `static final int CHANNEL_RED = 0` |  |
| `static final float LEVEL_BLACK = 0.0f` |  |
| `static final float LEVEL_WHITE = 1.0f` |  |
| `static final int POINT_SIZE = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyColorCurve(int, float[], int)` |  |
| `android.graphics.PointF getPoint(int, int)` |  |
| `int getPointCount(int)` |  |

---

## Package: `android.hardware.display`

### `class final DisplayManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION"` |  |
| `static final int VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR = 16` |  |
| `static final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 8` |  |
| `static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 2` |  |
| `static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1` |  |
| `static final int VIRTUAL_DISPLAY_FLAG_SECURE = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.hardware.display.VirtualDisplay createVirtualDisplay(@NonNull String, int, int, int, @Nullable android.view.Surface, int)` |  |
| `android.hardware.display.VirtualDisplay createVirtualDisplay(@NonNull String, int, int, int, @Nullable android.view.Surface, int, @Nullable android.hardware.display.VirtualDisplay.Callback, @Nullable android.os.Handler)` |  |
| `android.view.Display getDisplay(int)` |  |
| `android.view.Display[] getDisplays()` |  |
| `android.view.Display[] getDisplays(String)` |  |
| `void registerDisplayListener(android.hardware.display.DisplayManager.DisplayListener, android.os.Handler)` |  |
| `void unregisterDisplayListener(android.hardware.display.DisplayManager.DisplayListener)` |  |

---

### `interface static DisplayManager.DisplayListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDisplayAdded(int)` |  |
| `void onDisplayChanged(int)` |  |
| `void onDisplayRemoved(int)` |  |

---

### `class final VirtualDisplay`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VirtualDisplay.Callback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.Display getDisplay()` |  |
| `android.view.Surface getSurface()` |  |
| `void release()` |  |
| `void resize(int, int, int)` |  |
| `void setSurface(android.view.Surface)` |  |
| `void onPaused()` |  |
| `void onResumed()` |  |
| `void onStopped()` |  |

---

## Package: `android.hardware.fingerprint`

### `class FingerprintManager` ~~DEPRECATED~~

- **注解：** `@Deprecated`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FINGERPRINT_ACQUIRED_GOOD = 0` |  |
| `static final int FINGERPRINT_ACQUIRED_IMAGER_DIRTY = 3` |  |
| `static final int FINGERPRINT_ACQUIRED_INSUFFICIENT = 2` |  |
| `static final int FINGERPRINT_ACQUIRED_PARTIAL = 1` |  |
| `static final int FINGERPRINT_ACQUIRED_TOO_FAST = 5` |  |
| `static final int FINGERPRINT_ACQUIRED_TOO_SLOW = 4` |  |
| `static final int FINGERPRINT_ERROR_CANCELED = 5` |  |
| `static final int FINGERPRINT_ERROR_HW_NOT_PRESENT = 12` |  |
| `static final int FINGERPRINT_ERROR_HW_UNAVAILABLE = 1` |  |
| `static final int FINGERPRINT_ERROR_LOCKOUT = 7` |  |
| `static final int FINGERPRINT_ERROR_LOCKOUT_PERMANENT = 9` |  |
| `static final int FINGERPRINT_ERROR_NO_FINGERPRINTS = 11` |  |
| `static final int FINGERPRINT_ERROR_NO_SPACE = 4` |  |
| `static final int FINGERPRINT_ERROR_TIMEOUT = 3` |  |
| `static final int FINGERPRINT_ERROR_UNABLE_TO_PROCESS = 2` |  |
| `static final int FINGERPRINT_ERROR_USER_CANCELED = 10` |  |
| `static final int FINGERPRINT_ERROR_VENDOR = 8` |  |

---

### `class static FingerprintManager.AuthenticationResult` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final FingerprintManager.CryptoObject` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

## Package: `android.hardware.input`

### `class final InputManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_QUERY_KEYBOARD_LAYOUTS = "android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS"` |  |
| `static final String META_DATA_KEYBOARD_LAYOUTS = "android.hardware.input.metadata.KEYBOARD_LAYOUTS"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.InputDevice getInputDevice(int)` |  |
| `int[] getInputDeviceIds()` |  |
| `void registerInputDeviceListener(android.hardware.input.InputManager.InputDeviceListener, android.os.Handler)` |  |
| `void unregisterInputDeviceListener(android.hardware.input.InputManager.InputDeviceListener)` |  |

---

### `interface static InputManager.InputDeviceListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onInputDeviceAdded(int)` |  |
| `void onInputDeviceChanged(int)` |  |
| `void onInputDeviceRemoved(int)` |  |

---

## Package: `android.hardware.usb`

### `class UsbAccessory`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class UsbConfiguration`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getId()` |  |
| `int getInterfaceCount()` |  |
| `int getMaxPower()` |  |
| `boolean isRemoteWakeup()` |  |
| `boolean isSelfPowered()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final UsbConstants`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UsbConstants()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int USB_CLASS_APP_SPEC = 254` |  |
| `static final int USB_CLASS_AUDIO = 1` |  |
| `static final int USB_CLASS_CDC_DATA = 10` |  |
| `static final int USB_CLASS_COMM = 2` |  |
| `static final int USB_CLASS_CONTENT_SEC = 13` |  |
| `static final int USB_CLASS_CSCID = 11` |  |
| `static final int USB_CLASS_HID = 3` |  |
| `static final int USB_CLASS_HUB = 9` |  |
| `static final int USB_CLASS_MASS_STORAGE = 8` |  |
| `static final int USB_CLASS_MISC = 239` |  |
| `static final int USB_CLASS_PER_INTERFACE = 0` |  |
| `static final int USB_CLASS_PHYSICA = 5` |  |
| `static final int USB_CLASS_PRINTER = 7` |  |
| `static final int USB_CLASS_STILL_IMAGE = 6` |  |
| `static final int USB_CLASS_VENDOR_SPEC = 255` |  |
| `static final int USB_CLASS_VIDEO = 14` |  |
| `static final int USB_CLASS_WIRELESS_CONTROLLER = 224` |  |
| `static final int USB_DIR_IN = 128` |  |
| `static final int USB_DIR_OUT = 0` |  |
| `static final int USB_ENDPOINT_DIR_MASK = 128` |  |
| `static final int USB_ENDPOINT_NUMBER_MASK = 15` |  |
| `static final int USB_ENDPOINT_XFERTYPE_MASK = 3` |  |
| `static final int USB_ENDPOINT_XFER_BULK = 2` |  |
| `static final int USB_ENDPOINT_XFER_CONTROL = 0` |  |
| `static final int USB_ENDPOINT_XFER_INT = 3` |  |
| `static final int USB_ENDPOINT_XFER_ISOC = 1` |  |
| `static final int USB_INTERFACE_SUBCLASS_BOOT = 1` |  |
| `static final int USB_SUBCLASS_VENDOR_SPEC = 255` |  |
| `static final int USB_TYPE_CLASS = 32` |  |
| `static final int USB_TYPE_MASK = 96` |  |
| `static final int USB_TYPE_RESERVED = 96` |  |
| `static final int USB_TYPE_STANDARD = 0` |  |
| `static final int USB_TYPE_VENDOR = 64` |  |

---

### `class UsbDevice`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getConfigurationCount()` |  |
| `int getDeviceClass()` |  |
| `int getDeviceId()` |  |
| `static int getDeviceId(String)` |  |
| `static String getDeviceName(int)` |  |
| `int getDeviceProtocol()` |  |
| `int getDeviceSubclass()` |  |
| `int getInterfaceCount()` |  |
| `int getProductId()` |  |
| `int getVendorId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class UsbDeviceConnection`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int bulkTransfer(android.hardware.usb.UsbEndpoint, byte[], int, int)` |  |
| `int bulkTransfer(android.hardware.usb.UsbEndpoint, byte[], int, int, int)` |  |
| `boolean claimInterface(android.hardware.usb.UsbInterface, boolean)` |  |
| `void close()` |  |
| `int controlTransfer(int, int, int, int, byte[], int, int)` |  |
| `int controlTransfer(int, int, int, int, byte[], int, int, int)` |  |
| `int getFileDescriptor()` |  |
| `byte[] getRawDescriptors()` |  |
| `String getSerial()` |  |
| `boolean releaseInterface(android.hardware.usb.UsbInterface)` |  |
| `android.hardware.usb.UsbRequest requestWait()` |  |
| `android.hardware.usb.UsbRequest requestWait(long) throws java.util.concurrent.TimeoutException` |  |
| `boolean setConfiguration(android.hardware.usb.UsbConfiguration)` |  |
| `boolean setInterface(android.hardware.usb.UsbInterface)` |  |

---

### `class UsbEndpoint`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAddress()` |  |
| `int getAttributes()` |  |
| `int getDirection()` |  |
| `int getEndpointNumber()` |  |
| `int getInterval()` |  |
| `int getMaxPacketSize()` |  |
| `int getType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class UsbInterface`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAlternateSetting()` |  |
| `android.hardware.usb.UsbEndpoint getEndpoint(int)` |  |
| `int getEndpointCount()` |  |
| `int getId()` |  |
| `int getInterfaceClass()` |  |
| `int getInterfaceProtocol()` |  |
| `int getInterfaceSubclass()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class UsbManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED"` |  |
| `static final String ACTION_USB_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED"` |  |
| `static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"` |  |
| `static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"` |  |
| `static final String EXTRA_ACCESSORY = "accessory"` |  |
| `static final String EXTRA_DEVICE = "device"` |  |
| `static final String EXTRA_PERMISSION_GRANTED = "permission"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.hardware.usb.UsbAccessory[] getAccessoryList()` |  |
| `java.util.HashMap<java.lang.String,android.hardware.usb.UsbDevice> getDeviceList()` |  |
| `boolean hasPermission(android.hardware.usb.UsbDevice)` |  |
| `boolean hasPermission(android.hardware.usb.UsbAccessory)` |  |
| `android.os.ParcelFileDescriptor openAccessory(android.hardware.usb.UsbAccessory)` |  |
| `android.hardware.usb.UsbDeviceConnection openDevice(android.hardware.usb.UsbDevice)` |  |
| `void requestPermission(android.hardware.usb.UsbDevice, android.app.PendingIntent)` |  |
| `void requestPermission(android.hardware.usb.UsbAccessory, android.app.PendingIntent)` |  |

---

### `class UsbRequest`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UsbRequest()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean cancel()` |  |
| `void close()` |  |
| `Object getClientData()` |  |
| `android.hardware.usb.UsbEndpoint getEndpoint()` |  |
| `boolean initialize(android.hardware.usb.UsbDeviceConnection, android.hardware.usb.UsbEndpoint)` |  |
| `boolean queue(@Nullable java.nio.ByteBuffer)` |  |
| `void setClientData(Object)` |  |

---

