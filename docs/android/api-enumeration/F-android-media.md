# Android 11 (API 30) Public API Enumeration: Android Media

Generated from `frameworks/base/api/current.txt`

## Summary

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.media` | 222 | 1065 | 1413 | 106 |
| `android.media.audiofx` | 38 | 241 | 109 | 38 |
| `android.media.browse` | 3 | 22 | 4 | 5 |
| `android.media.effect` | 4 | 11 | 27 | 1 |
| `android.media.midi` | 13 | 55 | 13 | 5 |
| `android.media.projection` | 2 | 7 | 0 | 1 |
| `android.media.session` | 14 | 131 | 34 | 10 |
| `android.media.tv` | 19 | 181 | 406 | 14 |
| **TOTAL** | **315** | **1713** | **2006** | **180** |

---

## Package: `android.media`

### `class AsyncPlayer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AsyncPlayer(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void play(@NonNull android.content.Context, @NonNull android.net.Uri, boolean, @NonNull android.media.AudioAttributes) throws java.lang.IllegalArgumentException` |  |
| `void stop()` |  |

---

### `class final AudioAttributes`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALLOW_CAPTURE_BY_ALL = 1` |  |
| `static final int ALLOW_CAPTURE_BY_NONE = 3` |  |
| `static final int ALLOW_CAPTURE_BY_SYSTEM = 2` |  |
| `static final int CONTENT_TYPE_MOVIE = 3` |  |
| `static final int CONTENT_TYPE_MUSIC = 2` |  |
| `static final int CONTENT_TYPE_SONIFICATION = 4` |  |
| `static final int CONTENT_TYPE_SPEECH = 1` |  |
| `static final int CONTENT_TYPE_UNKNOWN = 0` |  |
| `static final int FLAG_AUDIBILITY_ENFORCED = 1` |  |
| `static final int FLAG_HW_AV_SYNC = 16` |  |
| `static final int USAGE_ALARM = 4` |  |
| `static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11` |  |
| `static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12` |  |
| `static final int USAGE_ASSISTANCE_SONIFICATION = 13` |  |
| `static final int USAGE_ASSISTANT = 16` |  |
| `static final int USAGE_GAME = 14` |  |
| `static final int USAGE_MEDIA = 1` |  |
| `static final int USAGE_NOTIFICATION = 5` |  |
| `static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9` |  |
| `static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8` |  |
| `static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7` |  |
| `static final int USAGE_NOTIFICATION_EVENT = 10` |  |
| `static final int USAGE_NOTIFICATION_RINGTONE = 6` |  |
| `static final int USAGE_UNKNOWN = 0` |  |
| `static final int USAGE_VOICE_COMMUNICATION = 2` |  |
| `static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean areHapticChannelsMuted()` |  |
| `int describeContents()` |  |
| `int getAllowedCapturePolicy()` |  |
| `int getContentType()` |  |
| `int getFlags()` |  |
| `int getUsage()` |  |
| `int getVolumeControlStream()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static AudioAttributes.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioAttributes.Builder()` |  |
| `AudioAttributes.Builder(android.media.AudioAttributes)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.AudioAttributes build()` |  |
| `android.media.AudioAttributes.Builder setContentType(int)` |  |
| `android.media.AudioAttributes.Builder setFlags(int)` |  |
| `android.media.AudioAttributes.Builder setLegacyStreamType(int)` |  |
| `android.media.AudioAttributes.Builder setUsage(int)` |  |

---

### `class abstract AudioDeviceCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioDeviceCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAudioDevicesAdded(android.media.AudioDeviceInfo[])` |  |
| `void onAudioDevicesRemoved(android.media.AudioDeviceInfo[])` |  |

---

### `class final AudioDeviceInfo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_AUX_LINE = 19` |  |
| `static final int TYPE_BLUETOOTH_A2DP = 8` |  |
| `static final int TYPE_BLUETOOTH_SCO = 7` |  |
| `static final int TYPE_BUILTIN_EARPIECE = 1` |  |
| `static final int TYPE_BUILTIN_MIC = 15` |  |
| `static final int TYPE_BUILTIN_SPEAKER = 2` |  |
| `static final int TYPE_BUILTIN_SPEAKER_SAFE = 24` |  |
| `static final int TYPE_BUS = 21` |  |
| `static final int TYPE_DOCK = 13` |  |
| `static final int TYPE_FM = 14` |  |
| `static final int TYPE_FM_TUNER = 16` |  |
| `static final int TYPE_HDMI = 9` |  |
| `static final int TYPE_HDMI_ARC = 10` |  |
| `static final int TYPE_HEARING_AID = 23` |  |
| `static final int TYPE_IP = 20` |  |
| `static final int TYPE_LINE_ANALOG = 5` |  |
| `static final int TYPE_LINE_DIGITAL = 6` |  |
| `static final int TYPE_TELEPHONY = 18` |  |
| `static final int TYPE_TV_TUNER = 17` |  |
| `static final int TYPE_UNKNOWN = 0` |  |
| `static final int TYPE_USB_ACCESSORY = 12` |  |
| `static final int TYPE_USB_DEVICE = 11` |  |
| `static final int TYPE_USB_HEADSET = 22` |  |
| `static final int TYPE_WIRED_HEADPHONES = 4` |  |
| `static final int TYPE_WIRED_HEADSET = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getId()` |  |
| `CharSequence getProductName()` |  |
| `int getType()` |  |
| `boolean isSink()` |  |
| `boolean isSource()` |  |

---

### `class final AudioFocusRequest`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean acceptsDelayedFocusGain()` |  |
| `int getFocusGain()` |  |
| `boolean willPauseWhenDucked()` |  |

---

### `class static final AudioFocusRequest.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioFocusRequest.Builder(int)` |  |
| `AudioFocusRequest.Builder(@NonNull android.media.AudioFocusRequest)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.AudioFocusRequest build()` |  |

---

### `class final AudioFormat`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CHANNEL_INVALID = 0` |  |
| `static final int CHANNEL_IN_BACK = 32` |  |
| `static final int CHANNEL_IN_BACK_PROCESSED = 512` |  |
| `static final int CHANNEL_IN_DEFAULT = 1` |  |
| `static final int CHANNEL_IN_FRONT = 16` |  |
| `static final int CHANNEL_IN_FRONT_PROCESSED = 256` |  |
| `static final int CHANNEL_IN_LEFT = 4` |  |
| `static final int CHANNEL_IN_LEFT_PROCESSED = 64` |  |
| `static final int CHANNEL_IN_MONO = 16` |  |
| `static final int CHANNEL_IN_PRESSURE = 1024` |  |
| `static final int CHANNEL_IN_RIGHT = 8` |  |
| `static final int CHANNEL_IN_RIGHT_PROCESSED = 128` |  |
| `static final int CHANNEL_IN_STEREO = 12` |  |
| `static final int CHANNEL_IN_VOICE_DNLINK = 32768` |  |
| `static final int CHANNEL_IN_VOICE_UPLINK = 16384` |  |
| `static final int CHANNEL_IN_X_AXIS = 2048` |  |
| `static final int CHANNEL_IN_Y_AXIS = 4096` |  |
| `static final int CHANNEL_IN_Z_AXIS = 8192` |  |
| `static final int CHANNEL_OUT_5POINT1 = 252` |  |
| `static final int CHANNEL_OUT_7POINT1_SURROUND = 6396` |  |
| `static final int CHANNEL_OUT_BACK_CENTER = 1024` |  |
| `static final int CHANNEL_OUT_BACK_LEFT = 64` |  |
| `static final int CHANNEL_OUT_BACK_RIGHT = 128` |  |
| `static final int CHANNEL_OUT_DEFAULT = 1` |  |
| `static final int CHANNEL_OUT_FRONT_CENTER = 16` |  |
| `static final int CHANNEL_OUT_FRONT_LEFT = 4` |  |
| `static final int CHANNEL_OUT_FRONT_LEFT_OF_CENTER = 256` |  |
| `static final int CHANNEL_OUT_FRONT_RIGHT = 8` |  |
| `static final int CHANNEL_OUT_FRONT_RIGHT_OF_CENTER = 512` |  |
| `static final int CHANNEL_OUT_LOW_FREQUENCY = 32` |  |
| `static final int CHANNEL_OUT_MONO = 4` |  |
| `static final int CHANNEL_OUT_QUAD = 204` |  |
| `static final int CHANNEL_OUT_SIDE_LEFT = 2048` |  |
| `static final int CHANNEL_OUT_SIDE_RIGHT = 4096` |  |
| `static final int CHANNEL_OUT_STEREO = 12` |  |
| `static final int CHANNEL_OUT_SURROUND = 1052` |  |
| `static final int ENCODING_AAC_ELD = 15` |  |
| `static final int ENCODING_AAC_HE_V1 = 11` |  |
| `static final int ENCODING_AAC_HE_V2 = 12` |  |
| `static final int ENCODING_AAC_LC = 10` |  |
| `static final int ENCODING_AAC_XHE = 16` |  |
| `static final int ENCODING_AC3 = 5` |  |
| `static final int ENCODING_AC4 = 17` |  |
| `static final int ENCODING_DEFAULT = 1` |  |
| `static final int ENCODING_DOLBY_MAT = 19` |  |
| `static final int ENCODING_DOLBY_TRUEHD = 14` |  |
| `static final int ENCODING_DTS = 7` |  |
| `static final int ENCODING_DTS_HD = 8` |  |
| `static final int ENCODING_E_AC3 = 6` |  |
| `static final int ENCODING_E_AC3_JOC = 18` |  |
| `static final int ENCODING_IEC61937 = 13` |  |
| `static final int ENCODING_INVALID = 0` |  |
| `static final int ENCODING_MP3 = 9` |  |
| `static final int ENCODING_OPUS = 20` |  |
| `static final int ENCODING_PCM_16BIT = 2` |  |
| `static final int ENCODING_PCM_8BIT = 3` |  |
| `static final int ENCODING_PCM_FLOAT = 4` |  |
| `static final int SAMPLE_RATE_UNSPECIFIED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getChannelCount()` |  |
| `int getChannelIndexMask()` |  |
| `int getChannelMask()` |  |
| `int getEncoding()` |  |
| `int getSampleRate()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static AudioFormat.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioFormat.Builder()` |  |
| `AudioFormat.Builder(android.media.AudioFormat)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.AudioFormat build()` |  |
| `android.media.AudioFormat.Builder setEncoding(int) throws java.lang.IllegalArgumentException` |  |
| `android.media.AudioFormat.Builder setSampleRate(int) throws java.lang.IllegalArgumentException` |  |

---

### `class AudioManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioManager.AudioPlaybackCallback()` |  |
| `AudioManager.AudioRecordingCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY"` |  |
| `static final String ACTION_HDMI_AUDIO_PLUG = "android.media.action.HDMI_AUDIO_PLUG"` |  |
| `static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG"` |  |
| `static final String ACTION_MICROPHONE_MUTE_CHANGED = "android.media.action.MICROPHONE_MUTE_CHANGED"` |  |
| `static final String ACTION_SCO_AUDIO_STATE_UPDATED = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED"` |  |
| `static final String ACTION_SPEAKERPHONE_STATE_CHANGED = "android.media.action.SPEAKERPHONE_STATE_CHANGED"` |  |
| `static final int ADJUST_LOWER = -1` |  |
| `static final int ADJUST_MUTE = -100` |  |
| `static final int ADJUST_RAISE = 1` |  |
| `static final int ADJUST_SAME = 0` |  |
| `static final int ADJUST_TOGGLE_MUTE = 101` |  |
| `static final int ADJUST_UNMUTE = 100` |  |
| `static final int AUDIOFOCUS_GAIN = 1` |  |
| `static final int AUDIOFOCUS_GAIN_TRANSIENT = 2` |  |
| `static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4` |  |
| `static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3` |  |
| `static final int AUDIOFOCUS_LOSS = -1` |  |
| `static final int AUDIOFOCUS_LOSS_TRANSIENT = -2` |  |
| `static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3` |  |
| `static final int AUDIOFOCUS_NONE = 0` |  |
| `static final int AUDIOFOCUS_REQUEST_DELAYED = 2` |  |
| `static final int AUDIOFOCUS_REQUEST_FAILED = 0` |  |
| `static final int AUDIOFOCUS_REQUEST_GRANTED = 1` |  |
| `static final int AUDIO_SESSION_ID_GENERATE = 0` |  |
| `static final int ERROR = -1` |  |
| `static final int ERROR_DEAD_OBJECT = -6` |  |
| `static final String EXTRA_AUDIO_PLUG_STATE = "android.media.extra.AUDIO_PLUG_STATE"` |  |
| `static final String EXTRA_ENCODINGS = "android.media.extra.ENCODINGS"` |  |
| `static final String EXTRA_MAX_CHANNEL_COUNT = "android.media.extra.MAX_CHANNEL_COUNT"` |  |
| `static final String EXTRA_RINGER_MODE = "android.media.EXTRA_RINGER_MODE"` |  |
| `static final String EXTRA_SCO_AUDIO_PREVIOUS_STATE = "android.media.extra.SCO_AUDIO_PREVIOUS_STATE"` |  |
| `static final String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE"` |  |
| `static final int FLAG_ALLOW_RINGER_MODES = 2` |  |
| `static final int FLAG_PLAY_SOUND = 4` |  |
| `static final int FLAG_REMOVE_SOUND_AND_VIBRATE = 8` |  |
| `static final int FLAG_SHOW_UI = 1` |  |
| `static final int FLAG_VIBRATE = 16` |  |
| `static final int FX_FOCUS_NAVIGATION_DOWN = 2` |  |
| `static final int FX_FOCUS_NAVIGATION_LEFT = 3` |  |
| `static final int FX_FOCUS_NAVIGATION_RIGHT = 4` |  |
| `static final int FX_FOCUS_NAVIGATION_UP = 1` |  |
| `static final int FX_KEYPRESS_DELETE = 7` |  |
| `static final int FX_KEYPRESS_INVALID = 9` |  |
| `static final int FX_KEYPRESS_RETURN = 8` |  |
| `static final int FX_KEYPRESS_SPACEBAR = 6` |  |
| `static final int FX_KEYPRESS_STANDARD = 5` |  |
| `static final int FX_KEY_CLICK = 0` |  |
| `static final int GET_DEVICES_ALL = 3` |  |
| `static final int GET_DEVICES_INPUTS = 1` |  |
| `static final int GET_DEVICES_OUTPUTS = 2` |  |
| `static final int MODE_CALL_SCREENING = 4` |  |
| `static final int MODE_CURRENT = -1` |  |
| `static final int MODE_INVALID = -2` |  |
| `static final int MODE_IN_CALL = 2` |  |
| `static final int MODE_IN_COMMUNICATION = 3` |  |
| `static final int MODE_NORMAL = 0` |  |
| `static final int MODE_RINGTONE = 1` |  |
| `static final String PROPERTY_OUTPUT_FRAMES_PER_BUFFER = "android.media.property.OUTPUT_FRAMES_PER_BUFFER"` |  |
| `static final String PROPERTY_OUTPUT_SAMPLE_RATE = "android.media.property.OUTPUT_SAMPLE_RATE"` |  |
| `static final String PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED = "android.media.property.SUPPORT_AUDIO_SOURCE_UNPROCESSED"` |  |
| `static final String PROPERTY_SUPPORT_MIC_NEAR_ULTRASOUND = "android.media.property.SUPPORT_MIC_NEAR_ULTRASOUND"` |  |
| `static final String PROPERTY_SUPPORT_SPEAKER_NEAR_ULTRASOUND = "android.media.property.SUPPORT_SPEAKER_NEAR_ULTRASOUND"` |  |
| `static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED"` |  |
| `static final int RINGER_MODE_NORMAL = 2` |  |
| `static final int RINGER_MODE_SILENT = 0` |  |
| `static final int RINGER_MODE_VIBRATE = 1` |  |
| `static final int SCO_AUDIO_STATE_CONNECTED = 1` |  |
| `static final int SCO_AUDIO_STATE_CONNECTING = 2` |  |
| `static final int SCO_AUDIO_STATE_DISCONNECTED = 0` |  |
| `static final int SCO_AUDIO_STATE_ERROR = -1` |  |
| `static final int STREAM_ACCESSIBILITY = 10` |  |
| `static final int STREAM_ALARM = 4` |  |
| `static final int STREAM_DTMF = 8` |  |
| `static final int STREAM_MUSIC = 3` |  |
| `static final int STREAM_NOTIFICATION = 5` |  |
| `static final int STREAM_RING = 2` |  |
| `static final int STREAM_SYSTEM = 1` |  |
| `static final int STREAM_VOICE_CALL = 0` |  |
| `static final int USE_DEFAULT_STREAM_TYPE = -2147483648` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int abandonAudioFocusRequest(@NonNull android.media.AudioFocusRequest)` |  |
| `void adjustStreamVolume(int, int, int)` |  |
| `void adjustSuggestedStreamVolume(int, int, int)` |  |
| `void adjustVolume(int, int)` |  |
| `void dispatchMediaKeyEvent(android.view.KeyEvent)` |  |
| `int generateAudioSessionId()` |  |
| `int getAllowedCapturePolicy()` |  |
| `android.media.AudioDeviceInfo[] getDevices(int)` |  |
| `java.util.List<android.media.MicrophoneInfo> getMicrophones() throws java.io.IOException` |  |
| `int getMode()` |  |
| `String getParameters(String)` |  |
| `String getProperty(String)` |  |
| `int getRingerMode()` |  |
| `int getStreamMaxVolume(int)` |  |
| `int getStreamMinVolume(int)` |  |
| `int getStreamVolume(int)` |  |
| `float getStreamVolumeDb(int, int, int)` |  |
| `boolean isBluetoothScoAvailableOffCall()` |  |
| `boolean isBluetoothScoOn()` |  |
| `boolean isCallScreeningModeSupported()` |  |
| `static boolean isHapticPlaybackSupported()` |  |
| `boolean isMicrophoneMute()` |  |
| `boolean isMusicActive()` |  |
| `static boolean isOffloadedPlaybackSupported(@NonNull android.media.AudioFormat, @NonNull android.media.AudioAttributes)` |  |
| `boolean isSpeakerphoneOn()` |  |
| `boolean isStreamMute(int)` |  |
| `boolean isVolumeFixed()` |  |
| `void loadSoundEffects()` |  |
| `void playSoundEffect(int)` |  |
| `void playSoundEffect(int, float)` |  |
| `void registerAudioDeviceCallback(android.media.AudioDeviceCallback, @Nullable android.os.Handler)` |  |
| `void registerAudioPlaybackCallback(@NonNull android.media.AudioManager.AudioPlaybackCallback, @Nullable android.os.Handler)` |  |
| `void registerAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback, @Nullable android.os.Handler)` |  |
| `int requestAudioFocus(@NonNull android.media.AudioFocusRequest)` |  |
| `void setAllowedCapturePolicy(int)` |  |
| `void setBluetoothScoOn(boolean)` |  |
| `void setMicrophoneMute(boolean)` |  |
| `void setMode(int)` |  |
| `void setParameters(String)` |  |
| `void setRingerMode(int)` |  |
| `void setSpeakerphoneOn(boolean)` |  |
| `void setStreamVolume(int, int, int)` |  |
| `void startBluetoothSco()` |  |
| `void stopBluetoothSco()` |  |
| `void unloadSoundEffects()` |  |
| `void unregisterAudioDeviceCallback(android.media.AudioDeviceCallback)` |  |
| `void unregisterAudioPlaybackCallback(@NonNull android.media.AudioManager.AudioPlaybackCallback)` |  |
| `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` |  |
| `void onPlaybackConfigChanged(java.util.List<android.media.AudioPlaybackConfiguration>)` |  |
| `void onRecordingConfigChanged(java.util.List<android.media.AudioRecordingConfiguration>)` |  |

---

### `interface static AudioManager.OnAudioFocusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAudioFocusChange(int)` |  |

---

### `class final AudioMetadata`


---

### `class static AudioMetadata.Format`


---

### `interface static AudioMetadata.Key<T>`


---

### `interface AudioMetadataMap`

- **Extends:** `android.media.AudioMetadataReadMap`

---

### `interface AudioMetadataReadMap`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `<T> boolean containsKey(@NonNull android.media.AudioMetadata.Key<T>)` |  |

---

### `class final AudioPlaybackCaptureConfiguration`


---

### `class static final AudioPlaybackCaptureConfiguration.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioPlaybackCaptureConfiguration.Builder(@NonNull android.media.projection.MediaProjection)` |  |

---

### `class final AudioPlaybackConfiguration`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.AudioAttributes getAudioAttributes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final AudioPresentation`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MASTERED_FOR_3D = 3` |  |
| `static final int MASTERED_FOR_HEADPHONE = 4` |  |
| `static final int MASTERED_FOR_STEREO = 1` |  |
| `static final int MASTERED_FOR_SURROUND = 2` |  |
| `static final int MASTERING_NOT_INDICATED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.util.Locale,java.lang.String> getLabels()` |  |
| `java.util.Locale getLocale()` |  |
| `int getMasteringIndication()` |  |
| `int getPresentationId()` |  |
| `int getProgramId()` |  |
| `boolean hasAudioDescription()` |  |
| `boolean hasDialogueEnhancement()` |  |
| `boolean hasSpokenSubtitles()` |  |

---

### `class static final AudioPresentation.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioPresentation.Builder(int)` |  |

---

### `class AudioRecord`

- **Implements:** `android.media.AudioRecordingMonitor android.media.AudioRouting android.media.MicrophoneDirection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioRecord(int, int, int, int, int) throws java.lang.IllegalArgumentException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR = -1` |  |
| `static final int ERROR_BAD_VALUE = -2` |  |
| `static final int ERROR_DEAD_OBJECT = -6` |  |
| `static final int ERROR_INVALID_OPERATION = -3` |  |
| `static final int READ_BLOCKING = 0` |  |
| `static final int READ_NON_BLOCKING = 1` |  |
| `static final int RECORDSTATE_RECORDING = 3` |  |
| `static final int RECORDSTATE_STOPPED = 1` |  |
| `static final int STATE_INITIALIZED = 1` |  |
| `static final int STATE_UNINITIALIZED = 0` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` |  |
| `void finalize()` |  |
| `java.util.List<android.media.MicrophoneInfo> getActiveMicrophones() throws java.io.IOException` |  |
| `int getAudioFormat()` |  |
| `int getAudioSessionId()` |  |
| `int getAudioSource()` |  |
| `int getBufferSizeInFrames()` |  |
| `int getChannelConfiguration()` |  |
| `int getChannelCount()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `static int getMinBufferSize(int, int, int)` |  |
| `int getNotificationMarkerPosition()` |  |
| `int getPositionNotificationPeriod()` |  |
| `android.media.AudioDeviceInfo getPreferredDevice()` |  |
| `int getRecordingState()` |  |
| `android.media.AudioDeviceInfo getRoutedDevice()` |  |
| `int getSampleRate()` |  |
| `int getState()` |  |
| `int getTimestamp(@NonNull android.media.AudioTimestamp, int)` |  |
| `boolean isPrivacySensitive()` |  |
| `int read(@NonNull byte[], int, int)` |  |
| `int read(@NonNull byte[], int, int, int)` |  |
| `int read(@NonNull short[], int, int)` |  |
| `int read(@NonNull short[], int, int, int)` |  |
| `int read(@NonNull float[], int, int, int)` |  |
| `int read(@NonNull java.nio.ByteBuffer, int)` |  |
| `int read(@NonNull java.nio.ByteBuffer, int, int)` |  |
| `void registerAudioRecordingCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioManager.AudioRecordingCallback)` |  |
| `void release()` |  |
| `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` |  |
| `int setNotificationMarkerPosition(int)` |  |
| `int setPositionNotificationPeriod(int)` |  |
| `boolean setPreferredDevice(android.media.AudioDeviceInfo)` |  |
| `boolean setPreferredMicrophoneDirection(int)` |  |
| `boolean setPreferredMicrophoneFieldDimension(@FloatRange(from=-1.0, to=1.0) float)` |  |
| `void setRecordPositionUpdateListener(android.media.AudioRecord.OnRecordPositionUpdateListener)` |  |
| `void setRecordPositionUpdateListener(android.media.AudioRecord.OnRecordPositionUpdateListener, android.os.Handler)` |  |
| `void startRecording() throws java.lang.IllegalStateException` |  |
| `void startRecording(android.media.MediaSyncEvent) throws java.lang.IllegalStateException` |  |
| `void stop() throws java.lang.IllegalStateException` |  |
| `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` |  |

---

### `class static AudioRecord.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioRecord.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.AudioRecord build() throws java.lang.UnsupportedOperationException` |  |
| `android.media.AudioRecord.Builder setAudioFormat(@NonNull android.media.AudioFormat) throws java.lang.IllegalArgumentException` |  |
| `android.media.AudioRecord.Builder setAudioSource(int) throws java.lang.IllegalArgumentException` |  |
| `android.media.AudioRecord.Builder setBufferSizeInBytes(int) throws java.lang.IllegalArgumentException` |  |

---

### `class static final AudioRecord.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CHANNELS = "android.media.audiorecord.channels"` |  |
| `static final String ENCODING = "android.media.audiorecord.encoding"` |  |
| `static final String SAMPLERATE = "android.media.audiorecord.samplerate"` |  |
| `static final String SOURCE = "android.media.audiorecord.source"` |  |

---

### `interface static AudioRecord.OnRecordPositionUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onMarkerReached(android.media.AudioRecord)` |  |
| `void onPeriodicNotification(android.media.AudioRecord)` |  |

---

### `interface static AudioRecord.OnRoutingChangedListener` ~~DEPRECATED~~

- **Extends:** `android.media.AudioRouting.OnRoutingChangedListener`
- **Annotations:** `@Deprecated`

---

### `class final AudioRecordingConfiguration`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.AudioDeviceInfo getAudioDevice()` |  |
| `int getAudioSource()` |  |
| `int getClientAudioSessionId()` |  |
| `int getClientAudioSource()` |  |
| `android.media.AudioFormat getClientFormat()` |  |
| `android.media.AudioFormat getFormat()` |  |
| `boolean isClientSilenced()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface AudioRecordingMonitor`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void registerAudioRecordingCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioManager.AudioRecordingCallback)` |  |
| `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` |  |

---

### `interface AudioRouting`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` |  |
| `android.media.AudioDeviceInfo getPreferredDevice()` |  |
| `android.media.AudioDeviceInfo getRoutedDevice()` |  |
| `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` |  |
| `boolean setPreferredDevice(android.media.AudioDeviceInfo)` |  |

---

### `interface static AudioRouting.OnRoutingChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onRoutingChanged(android.media.AudioRouting)` |  |

---

### `class final AudioTimestamp`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioTimestamp()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TIMEBASE_BOOTTIME = 1` |  |
| `static final int TIMEBASE_MONOTONIC = 0` |  |
| `long framePosition` |  |
| `long nanoTime` |  |

---

### `class AudioTrack`

- **Implements:** `android.media.AudioRouting android.media.VolumeAutomation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioTrack(android.media.AudioAttributes, android.media.AudioFormat, int, int, int) throws java.lang.IllegalArgumentException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DUAL_MONO_MODE_LL = 2` |  |
| `static final int DUAL_MONO_MODE_LR = 1` |  |
| `static final int DUAL_MONO_MODE_OFF = 0` |  |
| `static final int DUAL_MONO_MODE_RR = 3` |  |
| `static final int ENCAPSULATION_METADATA_TYPE_DVB_AD_DESCRIPTOR = 2` |  |
| `static final int ENCAPSULATION_METADATA_TYPE_FRAMEWORK_TUNER = 1` |  |
| `static final int ENCAPSULATION_MODE_ELEMENTARY_STREAM = 1` |  |
| `static final int ENCAPSULATION_MODE_NONE = 0` |  |
| `static final int ERROR = -1` |  |
| `static final int ERROR_BAD_VALUE = -2` |  |
| `static final int ERROR_DEAD_OBJECT = -6` |  |
| `static final int ERROR_INVALID_OPERATION = -3` |  |
| `static final int MODE_STATIC = 0` |  |
| `static final int MODE_STREAM = 1` |  |
| `static final int PERFORMANCE_MODE_LOW_LATENCY = 1` |  |
| `static final int PERFORMANCE_MODE_NONE = 0` |  |
| `static final int PERFORMANCE_MODE_POWER_SAVING = 2` |  |
| `static final int PLAYSTATE_PAUSED = 2` |  |
| `static final int PLAYSTATE_PLAYING = 3` |  |
| `static final int PLAYSTATE_STOPPED = 1` |  |
| `static final int STATE_INITIALIZED = 1` |  |
| `static final int STATE_NO_STATIC_DATA = 2` |  |
| `static final int STATE_UNINITIALIZED = 0` |  |
| `static final int SUCCESS = 0` |  |
| `static final int WRITE_BLOCKING = 0` |  |
| `static final int WRITE_NON_BLOCKING = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnCodecFormatChangedListener(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioTrack.OnCodecFormatChangedListener)` |  |
| `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` |  |
| `int attachAuxEffect(int)` |  |
| `void finalize()` |  |
| `void flush()` |  |
| `float getAudioDescriptionMixLeveldB()` |  |
| `int getAudioFormat()` |  |
| `int getAudioSessionId()` |  |
| `int getChannelConfiguration()` |  |
| `int getChannelCount()` |  |
| `int getDualMonoMode()` |  |
| `static float getMaxVolume()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `static int getMinBufferSize(int, int, int)` |  |
| `static float getMinVolume()` |  |
| `static int getNativeOutputSampleRate(int)` |  |
| `int getNotificationMarkerPosition()` |  |
| `int getPerformanceMode()` |  |
| `int getPlayState()` |  |
| `int getPlaybackHeadPosition()` |  |
| `int getPlaybackRate()` |  |
| `int getPositionNotificationPeriod()` |  |
| `android.media.AudioDeviceInfo getPreferredDevice()` |  |
| `android.media.AudioDeviceInfo getRoutedDevice()` |  |
| `int getSampleRate()` |  |
| `int getState()` |  |
| `int getStreamType()` |  |
| `boolean getTimestamp(android.media.AudioTimestamp)` |  |
| `int getUnderrunCount()` |  |
| `static boolean isDirectPlaybackSupported(@NonNull android.media.AudioFormat, @NonNull android.media.AudioAttributes)` |  |
| `boolean isOffloadedPlayback()` |  |
| `void pause() throws java.lang.IllegalStateException` |  |
| `void play() throws java.lang.IllegalStateException` |  |
| `void registerStreamEventCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioTrack.StreamEventCallback)` |  |
| `void release()` |  |
| `int reloadStaticData()` |  |
| `void removeOnCodecFormatChangedListener(@NonNull android.media.AudioTrack.OnCodecFormatChangedListener)` |  |
| `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` |  |
| `boolean setAudioDescriptionMixLeveldB(@FloatRange(to=48.0f, toInclusive=true) float)` |  |
| `int setAuxEffectSendLevel(@FloatRange(from=0.0) float)` |  |
| `int setBufferSizeInFrames(@IntRange(from=0) int)` |  |
| `boolean setDualMonoMode(int)` |  |
| `int setLoopPoints(@IntRange(from=0) int, @IntRange(from=0) int, @IntRange(from=0xffffffff) int)` |  |
| `int setNotificationMarkerPosition(int)` |  |
| `void setOffloadDelayPadding(@IntRange(from=0) int, @IntRange(from=0) int)` |  |
| `void setOffloadEndOfStream()` |  |
| `int setPlaybackHeadPosition(@IntRange(from=0) int)` |  |
| `void setPlaybackParams(@NonNull android.media.PlaybackParams)` |  |
| `void setPlaybackPositionUpdateListener(android.media.AudioTrack.OnPlaybackPositionUpdateListener)` |  |
| `void setPlaybackPositionUpdateListener(android.media.AudioTrack.OnPlaybackPositionUpdateListener, android.os.Handler)` |  |
| `int setPlaybackRate(int)` |  |
| `int setPositionNotificationPeriod(int)` |  |
| `boolean setPreferredDevice(android.media.AudioDeviceInfo)` |  |
| `int setPresentation(@NonNull android.media.AudioPresentation)` |  |
| `int setVolume(float)` |  |
| `void stop() throws java.lang.IllegalStateException` |  |
| `void unregisterStreamEventCallback(@NonNull android.media.AudioTrack.StreamEventCallback)` |  |
| `int write(@NonNull byte[], int, int)` |  |
| `int write(@NonNull byte[], int, int, int)` |  |
| `int write(@NonNull short[], int, int)` |  |
| `int write(@NonNull short[], int, int, int)` |  |
| `int write(@NonNull float[], int, int, int)` |  |
| `int write(@NonNull java.nio.ByteBuffer, int, int)` |  |
| `int write(@NonNull java.nio.ByteBuffer, int, int, long)` |  |

---

### `class static AudioTrack.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioTrack.Builder()` |  |

---

### `class static final AudioTrack.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CONTENTTYPE = "android.media.audiotrack.type"` |  |
| `static final String STREAMTYPE = "android.media.audiotrack.streamtype"` |  |
| `static final String USAGE = "android.media.audiotrack.usage"` |  |

---

### `interface static AudioTrack.OnCodecFormatChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCodecFormatChanged(@NonNull android.media.AudioTrack, @Nullable android.media.AudioMetadataReadMap)` |  |

---

### `interface static AudioTrack.OnPlaybackPositionUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onMarkerReached(android.media.AudioTrack)` |  |
| `void onPeriodicNotification(android.media.AudioTrack)` |  |

---

### `interface static AudioTrack.OnRoutingChangedListener` ~~DEPRECATED~~

- **Extends:** `android.media.AudioRouting.OnRoutingChangedListener`
- **Annotations:** `@Deprecated`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioTrack.StreamEventCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDataRequest(@NonNull android.media.AudioTrack, @IntRange(from=0) int)` |  |
| `void onPresentationEnded(@NonNull android.media.AudioTrack)` |  |
| `void onTearDown(@NonNull android.media.AudioTrack)` |  |

---

### `class CamcorderProfile`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int QUALITY_1080P = 6` |  |
| `static final int QUALITY_2160P = 8` |  |
| `static final int QUALITY_2K = 12` |  |
| `static final int QUALITY_480P = 4` |  |
| `static final int QUALITY_4KDCI = 10` |  |
| `static final int QUALITY_720P = 5` |  |
| `static final int QUALITY_CIF = 3` |  |
| `static final int QUALITY_HIGH = 1` |  |
| `static final int QUALITY_HIGH_SPEED_1080P = 2004` |  |
| `static final int QUALITY_HIGH_SPEED_2160P = 2005` |  |
| `static final int QUALITY_HIGH_SPEED_480P = 2002` |  |
| `static final int QUALITY_HIGH_SPEED_4KDCI = 2008` |  |
| `static final int QUALITY_HIGH_SPEED_720P = 2003` |  |
| `static final int QUALITY_HIGH_SPEED_CIF = 2006` |  |
| `static final int QUALITY_HIGH_SPEED_HIGH = 2001` |  |
| `static final int QUALITY_HIGH_SPEED_LOW = 2000` |  |
| `static final int QUALITY_HIGH_SPEED_VGA = 2007` |  |
| `static final int QUALITY_LOW = 0` |  |
| `static final int QUALITY_QCIF = 2` |  |
| `static final int QUALITY_QHD = 11` |  |
| `static final int QUALITY_QVGA = 7` |  |
| `static final int QUALITY_TIME_LAPSE_1080P = 1006` |  |
| `static final int QUALITY_TIME_LAPSE_2160P = 1008` |  |
| `static final int QUALITY_TIME_LAPSE_2K = 1012` |  |
| `static final int QUALITY_TIME_LAPSE_480P = 1004` |  |
| `static final int QUALITY_TIME_LAPSE_4KDCI = 1010` |  |
| `static final int QUALITY_TIME_LAPSE_720P = 1005` |  |
| `static final int QUALITY_TIME_LAPSE_CIF = 1003` |  |
| `static final int QUALITY_TIME_LAPSE_HIGH = 1001` |  |
| `static final int QUALITY_TIME_LAPSE_LOW = 1000` |  |
| `static final int QUALITY_TIME_LAPSE_QCIF = 1002` |  |
| `static final int QUALITY_TIME_LAPSE_QHD = 1011` |  |
| `static final int QUALITY_TIME_LAPSE_QVGA = 1007` |  |
| `static final int QUALITY_TIME_LAPSE_VGA = 1009` |  |
| `static final int QUALITY_VGA = 9` |  |
| `int audioBitRate` |  |
| `int audioChannels` |  |
| `int audioCodec` |  |
| `int audioSampleRate` |  |
| `int duration` |  |
| `int fileFormat` |  |
| `int quality` |  |
| `int videoBitRate` |  |
| `int videoCodec` |  |
| `int videoFrameHeight` |  |
| `int videoFrameRate` |  |
| `int videoFrameWidth` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.CamcorderProfile get(int)` |  |
| `static android.media.CamcorderProfile get(int, int)` |  |
| `static boolean hasProfile(int)` |  |
| `static boolean hasProfile(int, int)` |  |

---

### `class CameraProfile`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CameraProfile()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int QUALITY_HIGH = 2` |  |
| `static final int QUALITY_LOW = 0` |  |
| `static final int QUALITY_MEDIUM = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int getJpegEncodingQualityParameter(int)` |  |
| `static int getJpegEncodingQualityParameter(int, int)` |  |

---

### `class final DeniedByServerException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DeniedByServerException(String)` |  |

---

### `class abstract DrmInitData`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getSchemeInitDataCount()` |  |

---

### `class static final DrmInitData.SchemeInitData`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final byte[] data` |  |
| `final String mimeType` |  |

---

### `class ExifInterface`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExifInterface(@NonNull java.io.File) throws java.io.IOException` |  |
| `ExifInterface(@NonNull String) throws java.io.IOException` |  |
| `ExifInterface(@NonNull java.io.FileDescriptor) throws java.io.IOException` |  |
| `ExifInterface(@NonNull java.io.InputStream) throws java.io.IOException` |  |
| `ExifInterface(@NonNull java.io.InputStream, int) throws java.io.IOException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ORIENTATION_FLIP_HORIZONTAL = 2` |  |
| `static final int ORIENTATION_FLIP_VERTICAL = 4` |  |
| `static final int ORIENTATION_NORMAL = 1` |  |
| `static final int ORIENTATION_ROTATE_180 = 3` |  |
| `static final int ORIENTATION_ROTATE_270 = 8` |  |
| `static final int ORIENTATION_ROTATE_90 = 6` |  |
| `static final int ORIENTATION_TRANSPOSE = 5` |  |
| `static final int ORIENTATION_TRANSVERSE = 7` |  |
| `static final int ORIENTATION_UNDEFINED = 0` |  |
| `static final int STREAM_TYPE_EXIF_DATA_ONLY = 1` |  |
| `static final int STREAM_TYPE_FULL_IMAGE_DATA = 0` |  |
| `static final String TAG_APERTURE_VALUE = "ApertureValue"` |  |
| `static final String TAG_ARTIST = "Artist"` |  |
| `static final String TAG_BITS_PER_SAMPLE = "BitsPerSample"` |  |
| `static final String TAG_BRIGHTNESS_VALUE = "BrightnessValue"` |  |
| `static final String TAG_CFA_PATTERN = "CFAPattern"` |  |
| `static final String TAG_COLOR_SPACE = "ColorSpace"` |  |
| `static final String TAG_COMPONENTS_CONFIGURATION = "ComponentsConfiguration"` |  |
| `static final String TAG_COMPRESSED_BITS_PER_PIXEL = "CompressedBitsPerPixel"` |  |
| `static final String TAG_COMPRESSION = "Compression"` |  |
| `static final String TAG_CONTRAST = "Contrast"` |  |
| `static final String TAG_COPYRIGHT = "Copyright"` |  |
| `static final String TAG_CUSTOM_RENDERED = "CustomRendered"` |  |
| `static final String TAG_DATETIME = "DateTime"` |  |
| `static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized"` |  |
| `static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal"` |  |
| `static final String TAG_DEFAULT_CROP_SIZE = "DefaultCropSize"` |  |
| `static final String TAG_DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription"` |  |
| `static final String TAG_DIGITAL_ZOOM_RATIO = "DigitalZoomRatio"` |  |
| `static final String TAG_DNG_VERSION = "DNGVersion"` |  |
| `static final String TAG_EXIF_VERSION = "ExifVersion"` |  |
| `static final String TAG_EXPOSURE_BIAS_VALUE = "ExposureBiasValue"` |  |
| `static final String TAG_EXPOSURE_INDEX = "ExposureIndex"` |  |
| `static final String TAG_EXPOSURE_MODE = "ExposureMode"` |  |
| `static final String TAG_EXPOSURE_PROGRAM = "ExposureProgram"` |  |
| `static final String TAG_EXPOSURE_TIME = "ExposureTime"` |  |
| `static final String TAG_FILE_SOURCE = "FileSource"` |  |
| `static final String TAG_FLASH = "Flash"` |  |
| `static final String TAG_FLASHPIX_VERSION = "FlashpixVersion"` |  |
| `static final String TAG_FLASH_ENERGY = "FlashEnergy"` |  |
| `static final String TAG_FOCAL_LENGTH = "FocalLength"` |  |
| `static final String TAG_FOCAL_LENGTH_IN_35MM_FILM = "FocalLengthIn35mmFilm"` |  |
| `static final String TAG_FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit"` |  |
| `static final String TAG_FOCAL_PLANE_X_RESOLUTION = "FocalPlaneXResolution"` |  |
| `static final String TAG_FOCAL_PLANE_Y_RESOLUTION = "FocalPlaneYResolution"` |  |
| `static final String TAG_F_NUMBER = "FNumber"` |  |
| `static final String TAG_GAIN_CONTROL = "GainControl"` |  |
| `static final String TAG_GPS_ALTITUDE = "GPSAltitude"` |  |
| `static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef"` |  |
| `static final String TAG_GPS_AREA_INFORMATION = "GPSAreaInformation"` |  |
| `static final String TAG_GPS_DATESTAMP = "GPSDateStamp"` |  |
| `static final String TAG_GPS_DEST_BEARING = "GPSDestBearing"` |  |
| `static final String TAG_GPS_DEST_BEARING_REF = "GPSDestBearingRef"` |  |
| `static final String TAG_GPS_DEST_DISTANCE = "GPSDestDistance"` |  |
| `static final String TAG_GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef"` |  |
| `static final String TAG_GPS_DEST_LATITUDE = "GPSDestLatitude"` |  |
| `static final String TAG_GPS_DEST_LATITUDE_REF = "GPSDestLatitudeRef"` |  |
| `static final String TAG_GPS_DEST_LONGITUDE = "GPSDestLongitude"` |  |
| `static final String TAG_GPS_DEST_LONGITUDE_REF = "GPSDestLongitudeRef"` |  |
| `static final String TAG_GPS_DIFFERENTIAL = "GPSDifferential"` |  |
| `static final String TAG_GPS_DOP = "GPSDOP"` |  |
| `static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection"` |  |
| `static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef"` |  |
| `static final String TAG_GPS_LATITUDE = "GPSLatitude"` |  |
| `static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef"` |  |
| `static final String TAG_GPS_LONGITUDE = "GPSLongitude"` |  |
| `static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef"` |  |
| `static final String TAG_GPS_MAP_DATUM = "GPSMapDatum"` |  |
| `static final String TAG_GPS_MEASURE_MODE = "GPSMeasureMode"` |  |
| `static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod"` |  |
| `static final String TAG_GPS_SATELLITES = "GPSSatellites"` |  |
| `static final String TAG_GPS_SPEED = "GPSSpeed"` |  |
| `static final String TAG_GPS_SPEED_REF = "GPSSpeedRef"` |  |
| `static final String TAG_GPS_STATUS = "GPSStatus"` |  |
| `static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp"` |  |
| `static final String TAG_GPS_TRACK = "GPSTrack"` |  |
| `static final String TAG_GPS_TRACK_REF = "GPSTrackRef"` |  |
| `static final String TAG_GPS_VERSION_ID = "GPSVersionID"` |  |
| `static final String TAG_IMAGE_DESCRIPTION = "ImageDescription"` |  |
| `static final String TAG_IMAGE_LENGTH = "ImageLength"` |  |
| `static final String TAG_IMAGE_UNIQUE_ID = "ImageUniqueID"` |  |
| `static final String TAG_IMAGE_WIDTH = "ImageWidth"` |  |
| `static final String TAG_INTEROPERABILITY_INDEX = "InteroperabilityIndex"` |  |
| `static final String TAG_ISO_SPEED_RATINGS = "ISOSpeedRatings"` |  |
| `static final String TAG_JPEG_INTERCHANGE_FORMAT = "JPEGInterchangeFormat"` |  |
| `static final String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = "JPEGInterchangeFormatLength"` |  |
| `static final String TAG_LIGHT_SOURCE = "LightSource"` |  |
| `static final String TAG_MAKE = "Make"` |  |
| `static final String TAG_MAKER_NOTE = "MakerNote"` |  |
| `static final String TAG_MAX_APERTURE_VALUE = "MaxApertureValue"` |  |
| `static final String TAG_METERING_MODE = "MeteringMode"` |  |
| `static final String TAG_MODEL = "Model"` |  |
| `static final String TAG_NEW_SUBFILE_TYPE = "NewSubfileType"` |  |
| `static final String TAG_OECF = "OECF"` |  |
| `static final String TAG_OFFSET_TIME = "OffsetTime"` |  |
| `static final String TAG_OFFSET_TIME_DIGITIZED = "OffsetTimeDigitized"` |  |
| `static final String TAG_OFFSET_TIME_ORIGINAL = "OffsetTimeOriginal"` |  |
| `static final String TAG_ORF_ASPECT_FRAME = "AspectFrame"` |  |
| `static final String TAG_ORF_PREVIEW_IMAGE_LENGTH = "PreviewImageLength"` |  |
| `static final String TAG_ORF_PREVIEW_IMAGE_START = "PreviewImageStart"` |  |
| `static final String TAG_ORF_THUMBNAIL_IMAGE = "ThumbnailImage"` |  |
| `static final String TAG_ORIENTATION = "Orientation"` |  |
| `static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation"` |  |
| `static final String TAG_PIXEL_X_DIMENSION = "PixelXDimension"` |  |
| `static final String TAG_PIXEL_Y_DIMENSION = "PixelYDimension"` |  |
| `static final String TAG_PLANAR_CONFIGURATION = "PlanarConfiguration"` |  |
| `static final String TAG_PRIMARY_CHROMATICITIES = "PrimaryChromaticities"` |  |
| `static final String TAG_REFERENCE_BLACK_WHITE = "ReferenceBlackWhite"` |  |
| `static final String TAG_RELATED_SOUND_FILE = "RelatedSoundFile"` |  |
| `static final String TAG_RESOLUTION_UNIT = "ResolutionUnit"` |  |
| `static final String TAG_ROWS_PER_STRIP = "RowsPerStrip"` |  |
| `static final String TAG_RW2_ISO = "ISO"` |  |
| `static final String TAG_RW2_JPG_FROM_RAW = "JpgFromRaw"` |  |
| `static final String TAG_RW2_SENSOR_BOTTOM_BORDER = "SensorBottomBorder"` |  |
| `static final String TAG_RW2_SENSOR_LEFT_BORDER = "SensorLeftBorder"` |  |
| `static final String TAG_RW2_SENSOR_RIGHT_BORDER = "SensorRightBorder"` |  |
| `static final String TAG_RW2_SENSOR_TOP_BORDER = "SensorTopBorder"` |  |
| `static final String TAG_SAMPLES_PER_PIXEL = "SamplesPerPixel"` |  |
| `static final String TAG_SATURATION = "Saturation"` |  |
| `static final String TAG_SCENE_CAPTURE_TYPE = "SceneCaptureType"` |  |
| `static final String TAG_SCENE_TYPE = "SceneType"` |  |
| `static final String TAG_SENSING_METHOD = "SensingMethod"` |  |
| `static final String TAG_SHARPNESS = "Sharpness"` |  |
| `static final String TAG_SHUTTER_SPEED_VALUE = "ShutterSpeedValue"` |  |
| `static final String TAG_SOFTWARE = "Software"` |  |
| `static final String TAG_SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse"` |  |
| `static final String TAG_SPECTRAL_SENSITIVITY = "SpectralSensitivity"` |  |
| `static final String TAG_STRIP_BYTE_COUNTS = "StripByteCounts"` |  |
| `static final String TAG_STRIP_OFFSETS = "StripOffsets"` |  |
| `static final String TAG_SUBFILE_TYPE = "SubfileType"` |  |
| `static final String TAG_SUBJECT_AREA = "SubjectArea"` |  |
| `static final String TAG_SUBJECT_DISTANCE = "SubjectDistance"` |  |
| `static final String TAG_SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange"` |  |
| `static final String TAG_SUBJECT_LOCATION = "SubjectLocation"` |  |
| `static final String TAG_SUBSEC_TIME = "SubSecTime"` |  |
| `static final String TAG_SUBSEC_TIME_DIGITIZED = "SubSecTimeDigitized"` |  |
| `static final String TAG_SUBSEC_TIME_ORIGINAL = "SubSecTimeOriginal"` |  |
| `static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength"` |  |
| `static final String TAG_THUMBNAIL_IMAGE_WIDTH = "ThumbnailImageWidth"` |  |
| `static final String TAG_THUMBNAIL_ORIENTATION = "ThumbnailOrientation"` |  |
| `static final String TAG_TRANSFER_FUNCTION = "TransferFunction"` |  |
| `static final String TAG_USER_COMMENT = "UserComment"` |  |
| `static final String TAG_WHITE_BALANCE = "WhiteBalance"` |  |
| `static final String TAG_WHITE_POINT = "WhitePoint"` |  |
| `static final String TAG_XMP = "Xmp"` |  |
| `static final String TAG_X_RESOLUTION = "XResolution"` |  |
| `static final String TAG_Y_CB_CR_COEFFICIENTS = "YCbCrCoefficients"` |  |
| `static final String TAG_Y_CB_CR_POSITIONING = "YCbCrPositioning"` |  |
| `static final String TAG_Y_CB_CR_SUB_SAMPLING = "YCbCrSubSampling"` |  |
| `static final String TAG_Y_RESOLUTION = "YResolution"` |  |
| `static final int WHITEBALANCE_AUTO = 0` |  |
| `static final int WHITEBALANCE_MANUAL = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `double getAltitude(double)` |  |
| `double getAttributeDouble(@NonNull String, double)` |  |
| `int getAttributeInt(@NonNull String, int)` |  |
| `boolean getLatLong(float[])` |  |
| `byte[] getThumbnail()` |  |
| `android.graphics.Bitmap getThumbnailBitmap()` |  |
| `byte[] getThumbnailBytes()` |  |
| `boolean hasAttribute(@NonNull String)` |  |
| `boolean hasThumbnail()` |  |
| `static boolean isSupportedMimeType(@NonNull String)` |  |
| `boolean isThumbnailCompressed()` |  |
| `void saveAttributes() throws java.io.IOException` |  |
| `void setAttribute(@NonNull String, @Nullable String)` |  |

---

### `class FaceDetector`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FaceDetector(int, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int findFaces(android.graphics.Bitmap, android.media.FaceDetector.Face[])` |  |

---

### `class FaceDetector.Face`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final float CONFIDENCE_THRESHOLD = 0.4f` |  |
| `static final int EULER_X = 0` |  |
| `static final int EULER_Y = 1` |  |
| `static final int EULER_Z = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float confidence()` |  |
| `float eyesDistance()` |  |
| `void getMidPoint(android.graphics.PointF)` |  |
| `float pose(int)` |  |

---

### `class abstract Image`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void close()` |  |
| `android.graphics.Rect getCropRect()` |  |
| `abstract int getFormat()` |  |
| `abstract int getHeight()` |  |
| `abstract android.media.Image.Plane[] getPlanes()` |  |
| `abstract long getTimestamp()` |  |
| `abstract int getWidth()` |  |
| `void setCropRect(android.graphics.Rect)` |  |
| `void setTimestamp(long)` |  |
| `abstract java.nio.ByteBuffer getBuffer()` |  |
| `abstract int getPixelStride()` |  |
| `abstract int getRowStride()` |  |

---

### `class ImageReader`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.Image acquireLatestImage()` |  |
| `android.media.Image acquireNextImage()` |  |
| `void close()` |  |
| `void discardFreeBuffers()` |  |
| `int getHeight()` |  |
| `int getImageFormat()` |  |
| `int getMaxImages()` |  |
| `android.view.Surface getSurface()` |  |
| `int getWidth()` |  |
| `void setOnImageAvailableListener(android.media.ImageReader.OnImageAvailableListener, android.os.Handler)` |  |

---

### `interface static ImageReader.OnImageAvailableListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onImageAvailable(android.media.ImageReader)` |  |

---

### `class ImageWriter`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `android.media.Image dequeueInputImage()` |  |
| `int getFormat()` |  |
| `int getMaxImages()` |  |
| `void queueInputImage(android.media.Image)` |  |
| `void setOnImageReleasedListener(android.media.ImageWriter.OnImageReleasedListener, android.os.Handler)` |  |

---

### `interface static ImageWriter.OnImageReleasedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onImageReleased(android.media.ImageWriter)` |  |

---

### `class JetPlayer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean clearQueue()` |  |
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `boolean closeJetFile()` |  |
| `void finalize()` |  |
| `static android.media.JetPlayer getJetPlayer()` |  |
| `static int getMaxTracks()` |  |
| `boolean loadJetFile(String)` |  |
| `boolean loadJetFile(android.content.res.AssetFileDescriptor)` |  |
| `boolean pause()` |  |
| `boolean play()` |  |
| `boolean queueJetSegment(int, int, int, int, int, byte)` |  |
| `boolean queueJetSegmentMuteArray(int, int, int, int, boolean[], byte)` |  |
| `void release()` |  |
| `void setEventListener(android.media.JetPlayer.OnJetEventListener)` |  |
| `void setEventListener(android.media.JetPlayer.OnJetEventListener, android.os.Handler)` |  |
| `boolean setMuteArray(boolean[], boolean)` |  |
| `boolean setMuteFlag(int, boolean, boolean)` |  |
| `boolean setMuteFlags(int, boolean)` |  |
| `boolean triggerClip(int)` |  |

---

### `interface static JetPlayer.OnJetEventListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onJetEvent(android.media.JetPlayer, short, byte, byte, byte, byte)` |  |
| `void onJetNumQueuedSegmentUpdate(android.media.JetPlayer, int)` |  |
| `void onJetPauseUpdate(android.media.JetPlayer, int)` |  |
| `void onJetUserIdUpdate(android.media.JetPlayer, int, int)` |  |

---

### `class MediaActionSound`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaActionSound()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FOCUS_COMPLETE = 1` |  |
| `static final int SHUTTER_CLICK = 0` |  |
| `static final int START_VIDEO_RECORDING = 2` |  |
| `static final int STOP_VIDEO_RECORDING = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void load(int)` |  |
| `void play(int)` |  |
| `void release()` |  |

---

### `class final MediaCas`

- **Implements:** `java.lang.AutoCloseable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCas(int) throws android.media.MediaCasException.UnsupportedCasException` |  |
| `MediaCas(@NonNull android.content.Context, int, @Nullable String, int) throws android.media.MediaCasException.UnsupportedCasException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PLUGIN_STATUS_PHYSICAL_MODULE_CHANGED = 0` |  |
| `static final int PLUGIN_STATUS_SESSION_NUMBER_CHANGED = 1` |  |
| `static final int SCRAMBLING_MODE_AES128 = 9` |  |
| `static final int SCRAMBLING_MODE_AES_ECB = 10` |  |
| `static final int SCRAMBLING_MODE_AES_SCTE52 = 11` |  |
| `static final int SCRAMBLING_MODE_DVB_CISSA_V1 = 6` |  |
| `static final int SCRAMBLING_MODE_DVB_CSA1 = 1` |  |
| `static final int SCRAMBLING_MODE_DVB_CSA2 = 2` |  |
| `static final int SCRAMBLING_MODE_DVB_CSA3_ENHANCE = 5` |  |
| `static final int SCRAMBLING_MODE_DVB_CSA3_MINIMAL = 4` |  |
| `static final int SCRAMBLING_MODE_DVB_CSA3_STANDARD = 3` |  |
| `static final int SCRAMBLING_MODE_DVB_IDSA = 7` |  |
| `static final int SCRAMBLING_MODE_MULTI2 = 8` |  |
| `static final int SCRAMBLING_MODE_RESERVED = 0` |  |
| `static final int SCRAMBLING_MODE_TDES_ECB = 12` |  |
| `static final int SCRAMBLING_MODE_TDES_SCTE52 = 13` |  |
| `static final int SESSION_USAGE_LIVE = 0` |  |
| `static final int SESSION_USAGE_PLAYBACK = 1` |  |
| `static final int SESSION_USAGE_RECORD = 2` |  |
| `static final int SESSION_USAGE_TIMESHIFT = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `static android.media.MediaCas.PluginDescriptor[] enumeratePlugins()` |  |
| `void finalize()` |  |
| `static boolean isSystemIdSupported(int)` |  |
| `android.media.MediaCas.Session openSession() throws android.media.MediaCasException` |  |
| `void processEmm(@NonNull byte[], int, int) throws android.media.MediaCasException` |  |
| `void processEmm(@NonNull byte[]) throws android.media.MediaCasException` |  |
| `void provision(@NonNull String) throws android.media.MediaCasException` |  |
| `void refreshEntitlements(int, @Nullable byte[]) throws android.media.MediaCasException` |  |
| `void sendEvent(int, int, @Nullable byte[]) throws android.media.MediaCasException` |  |
| `void setEventListener(@Nullable android.media.MediaCas.EventListener, @Nullable android.os.Handler)` |  |
| `void setPrivateData(@NonNull byte[]) throws android.media.MediaCasException` |  |

---

### `interface static MediaCas.EventListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onEvent(@NonNull android.media.MediaCas, int, int, @Nullable byte[])` |  |
| `default void onPluginStatusUpdate(@NonNull android.media.MediaCas, int, int)` |  |
| `default void onResourceLost(@NonNull android.media.MediaCas)` |  |
| `default void onSessionEvent(@NonNull android.media.MediaCas, @NonNull android.media.MediaCas.Session, int, int, @Nullable byte[])` |  |

---

### `class static MediaCas.PluginDescriptor`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getSystemId()` |  |

---

### `class final MediaCas.Session`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void processEcm(@NonNull byte[], int, int) throws android.media.MediaCasException` |  |
| `void processEcm(@NonNull byte[]) throws android.media.MediaCasException` |  |
| `void sendSessionEvent(int, int, @Nullable byte[]) throws android.media.MediaCasException` |  |
| `void setPrivateData(@NonNull byte[]) throws android.media.MediaCasException` |  |

---

### `class MediaCasException`

- **Extends:** `java.lang.Exception`

---

### `class static final MediaCasException.DeniedByServerException`

- **Extends:** `android.media.MediaCasException`

---

### `class static final MediaCasException.InsufficientResourceException`

- **Extends:** `android.media.MediaCasException`

---

### `class static final MediaCasException.NotProvisionedException`

- **Extends:** `android.media.MediaCasException`

---

### `class static final MediaCasException.ResourceBusyException`

- **Extends:** `android.media.MediaCasException`

---

### `class static final MediaCasException.UnsupportedCasException`

- **Extends:** `android.media.MediaCasException`

---

### `class MediaCasStateException`

- **Extends:** `java.lang.IllegalStateException`

---

### `class final MediaCodec`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BUFFER_FLAG_CODEC_CONFIG = 2` |  |
| `static final int BUFFER_FLAG_END_OF_STREAM = 4` |  |
| `static final int BUFFER_FLAG_KEY_FRAME = 1` |  |
| `static final int BUFFER_FLAG_PARTIAL_FRAME = 8` |  |
| `static final int CONFIGURE_FLAG_ENCODE = 1` |  |
| `static final int CONFIGURE_FLAG_USE_BLOCK_MODEL = 2` |  |
| `static final int CRYPTO_MODE_AES_CBC = 2` |  |
| `static final int CRYPTO_MODE_AES_CTR = 1` |  |
| `static final int CRYPTO_MODE_UNENCRYPTED = 0` |  |
| `static final int INFO_OUTPUT_FORMAT_CHANGED = -2` |  |
| `static final int INFO_TRY_AGAIN_LATER = -1` |  |
| `static final String PARAMETER_KEY_HDR10_PLUS_INFO = "hdr10-plus-info"` |  |
| `static final String PARAMETER_KEY_LOW_LATENCY = "low-latency"` |  |
| `static final String PARAMETER_KEY_OFFSET_TIME = "time-offset-us"` |  |
| `static final String PARAMETER_KEY_REQUEST_SYNC_FRAME = "request-sync"` |  |
| `static final String PARAMETER_KEY_SUSPEND = "drop-input-frames"` |  |
| `static final String PARAMETER_KEY_SUSPEND_TIME = "drop-start-time-us"` |  |
| `static final String PARAMETER_KEY_VIDEO_BITRATE = "video-bitrate"` |  |
| `static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1` |  |
| `static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void configure(@Nullable android.media.MediaFormat, @Nullable android.view.Surface, @Nullable android.media.MediaCrypto, int)` |  |
| `void configure(@Nullable android.media.MediaFormat, @Nullable android.view.Surface, int, @Nullable android.media.MediaDescrambler)` |  |
| `int dequeueInputBuffer(long)` |  |
| `int dequeueOutputBuffer(@NonNull android.media.MediaCodec.BufferInfo, long)` |  |
| `void finalize()` |  |
| `void flush()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `void queueInputBuffer(int, int, int, long, int) throws android.media.MediaCodec.CryptoException` |  |
| `void queueSecureInputBuffer(int, int, @NonNull android.media.MediaCodec.CryptoInfo, long, int) throws android.media.MediaCodec.CryptoException` |  |
| `void release()` |  |
| `void releaseOutputBuffer(int, boolean)` |  |
| `void releaseOutputBuffer(int, long)` |  |
| `void reset()` |  |
| `void setAudioPresentation(@NonNull android.media.AudioPresentation)` |  |
| `void setCallback(@Nullable android.media.MediaCodec.Callback, @Nullable android.os.Handler)` |  |
| `void setCallback(@Nullable android.media.MediaCodec.Callback)` |  |
| `void setInputSurface(@NonNull android.view.Surface)` |  |
| `void setOnFrameRenderedListener(@Nullable android.media.MediaCodec.OnFrameRenderedListener, @Nullable android.os.Handler)` |  |
| `void setOutputSurface(@NonNull android.view.Surface)` |  |
| `void setParameters(@Nullable android.os.Bundle)` |  |
| `void setVideoScalingMode(int)` |  |
| `void signalEndOfInputStream()` |  |
| `void start()` |  |
| `void stop()` |  |

---

### `class static final MediaCodec.BufferInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodec.BufferInfo()` |  |
| `MediaCodec.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int flags` |  |
| `int offset` |  |
| `long presentationTimeUs` |  |
| `int size` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void set(int, int, long, int)` |  |
| `abstract void onError(@NonNull android.media.MediaCodec, @NonNull android.media.MediaCodec.CodecException)` |  |
| `abstract void onInputBufferAvailable(@NonNull android.media.MediaCodec, int)` |  |
| `abstract void onOutputBufferAvailable(@NonNull android.media.MediaCodec, int, @NonNull android.media.MediaCodec.BufferInfo)` |  |
| `abstract void onOutputFormatChanged(@NonNull android.media.MediaCodec, @NonNull android.media.MediaFormat)` |  |

---

### `class static final MediaCodec.CodecException`

- **Extends:** `java.lang.IllegalStateException`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_INSUFFICIENT_RESOURCE = 1100` |  |
| `static final int ERROR_RECLAIMED = 1101` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorCode()` |  |
| `boolean isRecoverable()` |  |
| `boolean isTransient()` |  |

---

### `class static final MediaCodec.CryptoException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodec.CryptoException(int, @Nullable String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_FRAME_TOO_LARGE = 8` |  |
| `static final int ERROR_INSUFFICIENT_OUTPUT_PROTECTION = 4` |  |
| `static final int ERROR_INSUFFICIENT_SECURITY = 7` |  |
| `static final int ERROR_KEY_EXPIRED = 2` |  |
| `static final int ERROR_LOST_STATE = 9` |  |
| `static final int ERROR_NO_KEY = 1` |  |
| `static final int ERROR_RESOURCE_BUSY = 3` |  |
| `static final int ERROR_SESSION_NOT_OPENED = 5` |  |
| `static final int ERROR_UNSUPPORTED_OPERATION = 6` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorCode()` |  |

---

### `class static final MediaCodec.CryptoInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodec.CryptoInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] iv` |  |
| `byte[] key` |  |
| `int mode` |  |
| `int[] numBytesOfClearData` |  |
| `int[] numBytesOfEncryptedData` |  |
| `int numSubSamples` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void set(int, @NonNull int[], @NonNull int[], @NonNull byte[], @NonNull byte[], int)` |  |
| `void setPattern(android.media.MediaCodec.CryptoInfo.Pattern)` |  |

---

### `class static final MediaCodec.CryptoInfo.Pattern`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodec.CryptoInfo.Pattern(int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getEncryptBlocks()` |  |
| `int getSkipBlocks()` |  |
| `void set(int, int)` |  |

---

### `class MediaCodec.IncompatibleWithBlockModelException`

- **Extends:** `java.lang.RuntimeException`

---

### `class static final MediaCodec.LinearBlock`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `static boolean isCodecCopyFreeCompatible(@NonNull String[])` |  |
| `boolean isMappable()` |  |
| `void recycle()` |  |

---

### `class static final MediaCodec.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CODEC = "android.media.mediacodec.codec"` |  |
| `static final String ENCODER = "android.media.mediacodec.encoder"` |  |
| `static final String HEIGHT = "android.media.mediacodec.height"` |  |
| `static final String MIME_TYPE = "android.media.mediacodec.mime"` |  |
| `static final String MODE = "android.media.mediacodec.mode"` |  |
| `static final String MODE_AUDIO = "audio"` |  |
| `static final String MODE_VIDEO = "video"` |  |
| `static final String ROTATION = "android.media.mediacodec.rotation"` |  |
| `static final String SECURE = "android.media.mediacodec.secure"` |  |
| `static final String WIDTH = "android.media.mediacodec.width"` |  |

---

### `interface static MediaCodec.OnFrameRenderedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFrameRendered(@NonNull android.media.MediaCodec, long, long)` |  |

---

### `class static final MediaCodec.OutputFrame`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getFlags()` |  |
| `long getPresentationTimeUs()` |  |

---

### `class final MediaCodec.QueueRequest`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void queue()` |  |

---

### `class final MediaCodecInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.MediaCodecInfo.CodecCapabilities getCapabilitiesForType(String)` |  |
| `String[] getSupportedTypes()` |  |
| `boolean isAlias()` |  |
| `boolean isEncoder()` |  |
| `boolean isHardwareAccelerated()` |  |
| `boolean isSoftwareOnly()` |  |
| `boolean isVendor()` |  |

---

### `class static final MediaCodecInfo.AudioCapabilities`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.util.Range<java.lang.Integer> getBitrateRange()` |  |
| `int getMaxInputChannelCount()` |  |
| `android.util.Range<java.lang.Integer>[] getSupportedSampleRateRanges()` |  |
| `int[] getSupportedSampleRates()` |  |
| `boolean isSampleRateSupported(int)` |  |

---

### `class static final MediaCodecInfo.CodecCapabilities`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodecInfo.CodecCapabilities()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int COLOR_Format16bitRGB565 = 6` |  |
| `static final int COLOR_Format24bitBGR888 = 12` |  |
| `static final int COLOR_Format32bitABGR8888 = 2130747392` |  |
| `static final int COLOR_FormatL16 = 36` |  |
| `static final int COLOR_FormatL8 = 35` |  |
| `static final int COLOR_FormatRGBAFlexible = 2134288520` |  |
| `static final int COLOR_FormatRGBFlexible = 2134292616` |  |
| `static final int COLOR_FormatRawBayer10bit = 31` |  |
| `static final int COLOR_FormatRawBayer8bit = 30` |  |
| `static final int COLOR_FormatRawBayer8bitcompressed = 32` |  |
| `static final int COLOR_FormatSurface = 2130708361` |  |
| `static final int COLOR_FormatYUV420Flexible = 2135033992` |  |
| `static final int COLOR_FormatYUV422Flexible = 2135042184` |  |
| `static final int COLOR_FormatYUV444Flexible = 2135181448` |  |
| `static final String FEATURE_AdaptivePlayback = "adaptive-playback"` |  |
| `static final String FEATURE_DynamicTimestamp = "dynamic-timestamp"` |  |
| `static final String FEATURE_FrameParsing = "frame-parsing"` |  |
| `static final String FEATURE_IntraRefresh = "intra-refresh"` |  |
| `static final String FEATURE_LowLatency = "low-latency"` |  |
| `static final String FEATURE_MultipleFrames = "multiple-frames"` |  |
| `static final String FEATURE_PartialFrame = "partial-frame"` |  |
| `static final String FEATURE_SecurePlayback = "secure-playback"` |  |
| `static final String FEATURE_TunneledPlayback = "tunneled-playback"` |  |
| `int[] colorFormats` |  |
| `android.media.MediaCodecInfo.CodecProfileLevel[] profileLevels` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.MediaCodecInfo.CodecCapabilities createFromProfileLevel(String, int, int)` |  |
| `android.media.MediaCodecInfo.AudioCapabilities getAudioCapabilities()` |  |
| `android.media.MediaFormat getDefaultFormat()` |  |
| `android.media.MediaCodecInfo.EncoderCapabilities getEncoderCapabilities()` |  |
| `int getMaxSupportedInstances()` |  |
| `String getMimeType()` |  |
| `android.media.MediaCodecInfo.VideoCapabilities getVideoCapabilities()` |  |
| `boolean isFeatureRequired(String)` |  |
| `boolean isFeatureSupported(String)` |  |
| `boolean isFormatSupported(android.media.MediaFormat)` |  |

---

### `class static final MediaCodecInfo.CodecProfileLevel`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodecInfo.CodecProfileLevel()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AACObjectELD = 39` |  |
| `static final int AACObjectERLC = 17` |  |
| `static final int AACObjectERScalable = 20` |  |
| `static final int AACObjectHE = 5` |  |
| `static final int AACObjectHE_PS = 29` |  |
| `static final int AACObjectLC = 2` |  |
| `static final int AACObjectLD = 23` |  |
| `static final int AACObjectLTP = 4` |  |
| `static final int AACObjectMain = 1` |  |
| `static final int AACObjectSSR = 3` |  |
| `static final int AACObjectScalable = 6` |  |
| `static final int AACObjectXHE = 42` |  |
| `static final int AV1Level2 = 1` |  |
| `static final int AV1Level21 = 2` |  |
| `static final int AV1Level22 = 4` |  |
| `static final int AV1Level23 = 8` |  |
| `static final int AV1Level3 = 16` |  |
| `static final int AV1Level31 = 32` |  |
| `static final int AV1Level32 = 64` |  |
| `static final int AV1Level33 = 128` |  |
| `static final int AV1Level4 = 256` |  |
| `static final int AV1Level41 = 512` |  |
| `static final int AV1Level42 = 1024` |  |
| `static final int AV1Level43 = 2048` |  |
| `static final int AV1Level5 = 4096` |  |
| `static final int AV1Level51 = 8192` |  |
| `static final int AV1Level52 = 16384` |  |
| `static final int AV1Level53 = 32768` |  |
| `static final int AV1Level6 = 65536` |  |
| `static final int AV1Level61 = 131072` |  |
| `static final int AV1Level62 = 262144` |  |
| `static final int AV1Level63 = 524288` |  |
| `static final int AV1Level7 = 1048576` |  |
| `static final int AV1Level71 = 2097152` |  |
| `static final int AV1Level72 = 4194304` |  |
| `static final int AV1Level73 = 8388608` |  |
| `static final int AV1ProfileMain10 = 2` |  |
| `static final int AV1ProfileMain10HDR10 = 4096` |  |
| `static final int AV1ProfileMain10HDR10Plus = 8192` |  |
| `static final int AV1ProfileMain8 = 1` |  |
| `static final int AVCLevel1 = 1` |  |
| `static final int AVCLevel11 = 4` |  |
| `static final int AVCLevel12 = 8` |  |
| `static final int AVCLevel13 = 16` |  |
| `static final int AVCLevel1b = 2` |  |
| `static final int AVCLevel2 = 32` |  |
| `static final int AVCLevel21 = 64` |  |
| `static final int AVCLevel22 = 128` |  |
| `static final int AVCLevel3 = 256` |  |
| `static final int AVCLevel31 = 512` |  |
| `static final int AVCLevel32 = 1024` |  |
| `static final int AVCLevel4 = 2048` |  |
| `static final int AVCLevel41 = 4096` |  |
| `static final int AVCLevel42 = 8192` |  |
| `static final int AVCLevel5 = 16384` |  |
| `static final int AVCLevel51 = 32768` |  |
| `static final int AVCLevel52 = 65536` |  |
| `static final int AVCLevel6 = 131072` |  |
| `static final int AVCLevel61 = 262144` |  |
| `static final int AVCLevel62 = 524288` |  |
| `static final int AVCProfileBaseline = 1` |  |
| `static final int AVCProfileConstrainedBaseline = 65536` |  |
| `static final int AVCProfileConstrainedHigh = 524288` |  |
| `static final int AVCProfileExtended = 4` |  |
| `static final int AVCProfileHigh = 8` |  |
| `static final int AVCProfileHigh10 = 16` |  |
| `static final int AVCProfileHigh422 = 32` |  |
| `static final int AVCProfileHigh444 = 64` |  |
| `static final int AVCProfileMain = 2` |  |
| `static final int DolbyVisionLevelFhd24 = 4` |  |
| `static final int DolbyVisionLevelFhd30 = 8` |  |
| `static final int DolbyVisionLevelFhd60 = 16` |  |
| `static final int DolbyVisionLevelHd24 = 1` |  |
| `static final int DolbyVisionLevelHd30 = 2` |  |
| `static final int DolbyVisionLevelUhd24 = 32` |  |
| `static final int DolbyVisionLevelUhd30 = 64` |  |
| `static final int DolbyVisionLevelUhd48 = 128` |  |
| `static final int DolbyVisionLevelUhd60 = 256` |  |
| `static final int DolbyVisionProfileDvav110 = 1024` |  |
| `static final int DolbyVisionProfileDvavPen = 2` |  |
| `static final int DolbyVisionProfileDvavPer = 1` |  |
| `static final int DolbyVisionProfileDvavSe = 512` |  |
| `static final int DolbyVisionProfileDvheDen = 8` |  |
| `static final int DolbyVisionProfileDvheDer = 4` |  |
| `static final int DolbyVisionProfileDvheDtb = 128` |  |
| `static final int DolbyVisionProfileDvheDth = 64` |  |
| `static final int DolbyVisionProfileDvheDtr = 16` |  |
| `static final int DolbyVisionProfileDvheSt = 256` |  |
| `static final int DolbyVisionProfileDvheStn = 32` |  |
| `static final int H263Level10 = 1` |  |
| `static final int H263Level20 = 2` |  |
| `static final int H263Level30 = 4` |  |
| `static final int H263Level40 = 8` |  |
| `static final int H263Level45 = 16` |  |
| `static final int H263Level50 = 32` |  |
| `static final int H263Level60 = 64` |  |
| `static final int H263Level70 = 128` |  |
| `static final int H263ProfileBackwardCompatible = 4` |  |
| `static final int H263ProfileBaseline = 1` |  |
| `static final int H263ProfileH320Coding = 2` |  |
| `static final int H263ProfileHighCompression = 32` |  |
| `static final int H263ProfileHighLatency = 256` |  |
| `static final int H263ProfileISWV2 = 8` |  |
| `static final int H263ProfileISWV3 = 16` |  |
| `static final int H263ProfileInterlace = 128` |  |
| `static final int H263ProfileInternet = 64` |  |
| `static final int HEVCHighTierLevel1 = 2` |  |
| `static final int HEVCHighTierLevel2 = 8` |  |
| `static final int HEVCHighTierLevel21 = 32` |  |
| `static final int HEVCHighTierLevel3 = 128` |  |
| `static final int HEVCHighTierLevel31 = 512` |  |
| `static final int HEVCHighTierLevel4 = 2048` |  |
| `static final int HEVCHighTierLevel41 = 8192` |  |
| `static final int HEVCHighTierLevel5 = 32768` |  |
| `static final int HEVCHighTierLevel51 = 131072` |  |
| `static final int HEVCHighTierLevel52 = 524288` |  |
| `static final int HEVCHighTierLevel6 = 2097152` |  |
| `static final int HEVCHighTierLevel61 = 8388608` |  |
| `static final int HEVCHighTierLevel62 = 33554432` |  |
| `static final int HEVCMainTierLevel1 = 1` |  |
| `static final int HEVCMainTierLevel2 = 4` |  |
| `static final int HEVCMainTierLevel21 = 16` |  |
| `static final int HEVCMainTierLevel3 = 64` |  |
| `static final int HEVCMainTierLevel31 = 256` |  |
| `static final int HEVCMainTierLevel4 = 1024` |  |
| `static final int HEVCMainTierLevel41 = 4096` |  |
| `static final int HEVCMainTierLevel5 = 16384` |  |
| `static final int HEVCMainTierLevel51 = 65536` |  |
| `static final int HEVCMainTierLevel52 = 262144` |  |
| `static final int HEVCMainTierLevel6 = 1048576` |  |
| `static final int HEVCMainTierLevel61 = 4194304` |  |
| `static final int HEVCMainTierLevel62 = 16777216` |  |
| `static final int HEVCProfileMain = 1` |  |
| `static final int HEVCProfileMain10 = 2` |  |
| `static final int HEVCProfileMain10HDR10 = 4096` |  |
| `static final int HEVCProfileMain10HDR10Plus = 8192` |  |
| `static final int HEVCProfileMainStill = 4` |  |
| `static final int MPEG2LevelH14 = 2` |  |
| `static final int MPEG2LevelHL = 3` |  |
| `static final int MPEG2LevelHP = 4` |  |
| `static final int MPEG2LevelLL = 0` |  |
| `static final int MPEG2LevelML = 1` |  |
| `static final int MPEG2Profile422 = 2` |  |
| `static final int MPEG2ProfileHigh = 5` |  |
| `static final int MPEG2ProfileMain = 1` |  |
| `static final int MPEG2ProfileSNR = 3` |  |
| `static final int MPEG2ProfileSimple = 0` |  |
| `static final int MPEG2ProfileSpatial = 4` |  |
| `static final int MPEG4Level0 = 1` |  |
| `static final int MPEG4Level0b = 2` |  |
| `static final int MPEG4Level1 = 4` |  |
| `static final int MPEG4Level2 = 8` |  |
| `static final int MPEG4Level3 = 16` |  |
| `static final int MPEG4Level3b = 24` |  |
| `static final int MPEG4Level4 = 32` |  |
| `static final int MPEG4Level4a = 64` |  |
| `static final int MPEG4Level5 = 128` |  |
| `static final int MPEG4Level6 = 256` |  |
| `static final int MPEG4ProfileAdvancedCoding = 4096` |  |
| `static final int MPEG4ProfileAdvancedCore = 8192` |  |
| `static final int MPEG4ProfileAdvancedRealTime = 1024` |  |
| `static final int MPEG4ProfileAdvancedScalable = 16384` |  |
| `static final int MPEG4ProfileAdvancedSimple = 32768` |  |
| `static final int MPEG4ProfileBasicAnimated = 256` |  |
| `static final int MPEG4ProfileCore = 4` |  |
| `static final int MPEG4ProfileCoreScalable = 2048` |  |
| `static final int MPEG4ProfileHybrid = 512` |  |
| `static final int MPEG4ProfileMain = 8` |  |
| `static final int MPEG4ProfileNbit = 16` |  |
| `static final int MPEG4ProfileScalableTexture = 32` |  |
| `static final int MPEG4ProfileSimple = 1` |  |
| `static final int MPEG4ProfileSimpleFBA = 128` |  |
| `static final int MPEG4ProfileSimpleFace = 64` |  |
| `static final int MPEG4ProfileSimpleScalable = 2` |  |
| `static final int VP8Level_Version0 = 1` |  |
| `static final int VP8Level_Version1 = 2` |  |
| `static final int VP8Level_Version2 = 4` |  |
| `static final int VP8Level_Version3 = 8` |  |
| `static final int VP8ProfileMain = 1` |  |
| `static final int VP9Level1 = 1` |  |
| `static final int VP9Level11 = 2` |  |
| `static final int VP9Level2 = 4` |  |
| `static final int VP9Level21 = 8` |  |
| `static final int VP9Level3 = 16` |  |
| `static final int VP9Level31 = 32` |  |
| `static final int VP9Level4 = 64` |  |
| `static final int VP9Level41 = 128` |  |
| `static final int VP9Level5 = 256` |  |
| `static final int VP9Level51 = 512` |  |
| `static final int VP9Level52 = 1024` |  |
| `static final int VP9Level6 = 2048` |  |
| `static final int VP9Level61 = 4096` |  |
| `static final int VP9Level62 = 8192` |  |
| `static final int VP9Profile0 = 1` |  |
| `static final int VP9Profile1 = 2` |  |
| `static final int VP9Profile2 = 4` |  |
| `static final int VP9Profile2HDR = 4096` |  |
| `static final int VP9Profile2HDR10Plus = 16384` |  |
| `static final int VP9Profile3 = 8` |  |
| `static final int VP9Profile3HDR = 8192` |  |
| `static final int VP9Profile3HDR10Plus = 32768` |  |
| `int level` |  |
| `int profile` |  |

---

### `class static final MediaCodecInfo.EncoderCapabilities`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BITRATE_MODE_CBR = 2` |  |
| `static final int BITRATE_MODE_CQ = 0` |  |
| `static final int BITRATE_MODE_VBR = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.util.Range<java.lang.Integer> getComplexityRange()` |  |
| `android.util.Range<java.lang.Integer> getQualityRange()` |  |
| `boolean isBitrateModeSupported(int)` |  |

---

### `class static final MediaCodecInfo.VideoCapabilities`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean areSizeAndRateSupported(int, int, double)` |  |
| `android.util.Range<java.lang.Integer> getBitrateRange()` |  |
| `int getHeightAlignment()` |  |
| `android.util.Range<java.lang.Integer> getSupportedFrameRates()` |  |
| `android.util.Range<java.lang.Double> getSupportedFrameRatesFor(int, int)` |  |
| `android.util.Range<java.lang.Integer> getSupportedHeights()` |  |
| `android.util.Range<java.lang.Integer> getSupportedHeightsFor(int)` |  |
| `android.util.Range<java.lang.Integer> getSupportedWidths()` |  |
| `android.util.Range<java.lang.Integer> getSupportedWidthsFor(int)` |  |
| `int getWidthAlignment()` |  |
| `boolean isSizeSupported(int, int)` |  |

---

### `class static final MediaCodecInfo.VideoCapabilities.PerformancePoint`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodecInfo.VideoCapabilities.PerformancePoint(int, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean covers(@NonNull android.media.MediaFormat)` |  |
| `boolean covers(@NonNull android.media.MediaCodecInfo.VideoCapabilities.PerformancePoint)` |  |

---

### `class final MediaCodecList`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCodecList(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALL_CODECS = 1` |  |
| `static final int REGULAR_CODECS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String findDecoderForFormat(android.media.MediaFormat)` |  |
| `String findEncoderForFormat(android.media.MediaFormat)` |  |
| `android.media.MediaCodecInfo[] getCodecInfos()` |  |

---

### `class MediaController2`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancelSessionCommand(@NonNull Object)` |  |
| `void close()` |  |
| `boolean isPlaybackActive()` |  |

---

### `class static final MediaController2.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaController2.Builder(@NonNull android.content.Context, @NonNull android.media.Session2Token)` |  |
| `MediaController2.ControllerCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCommandResult(@NonNull android.media.MediaController2, @NonNull Object, @NonNull android.media.Session2Command, @NonNull android.media.Session2Command.Result)` |  |
| `void onConnected(@NonNull android.media.MediaController2, @NonNull android.media.Session2CommandGroup)` |  |
| `void onDisconnected(@NonNull android.media.MediaController2)` |  |
| `void onPlaybackActiveChanged(@NonNull android.media.MediaController2, boolean)` |  |

---

### `class final MediaCrypto`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCrypto(@NonNull java.util.UUID, @NonNull byte[]) throws android.media.MediaCryptoException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID)` |  |
| `void release()` |  |
| `boolean requiresSecureDecoderComponent(@NonNull String)` |  |
| `void setMediaDrmSession(@NonNull byte[]) throws android.media.MediaCryptoException` |  |

---

### `class final MediaCryptoException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaCryptoException(@Nullable String)` |  |

---

### `class abstract MediaDataSource`

- **Implements:** `java.io.Closeable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDataSource()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract long getSize() throws java.io.IOException` |  |
| `abstract int readAt(long, byte[], int, int) throws java.io.IOException` |  |

---

### `class final MediaDescrambler`

- **Implements:** `java.lang.AutoCloseable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDescrambler(int) throws android.media.MediaCasException.UnsupportedCasException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final byte SCRAMBLE_CONTROL_EVEN_KEY = 2` |  |
| `static final byte SCRAMBLE_CONTROL_ODD_KEY = 3` |  |
| `static final byte SCRAMBLE_CONTROL_RESERVED = 1` |  |
| `static final byte SCRAMBLE_CONTROL_UNSCRAMBLED = 0` |  |
| `static final byte SCRAMBLE_FLAG_PES_HEADER = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int descramble(@NonNull java.nio.ByteBuffer, @NonNull java.nio.ByteBuffer, @NonNull android.media.MediaCodec.CryptoInfo)` |  |
| `void finalize()` |  |
| `boolean requiresSecureDecoderComponent(@NonNull String)` |  |
| `void setMediaCasSession(@NonNull android.media.MediaCas.Session)` |  |

---

### `class MediaDescription`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long BT_FOLDER_TYPE_ALBUMS = 2L` |  |
| `static final long BT_FOLDER_TYPE_ARTISTS = 3L` |  |
| `static final long BT_FOLDER_TYPE_GENRES = 4L` |  |
| `static final long BT_FOLDER_TYPE_MIXED = 0L` |  |
| `static final long BT_FOLDER_TYPE_PLAYLISTS = 5L` |  |
| `static final long BT_FOLDER_TYPE_TITLES = 1L` |  |
| `static final long BT_FOLDER_TYPE_YEARS = 6L` |  |
| `static final String EXTRA_BT_FOLDER_TYPE = "android.media.extra.BT_FOLDER_TYPE"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static MediaDescription.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDescription.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.MediaDescription build()` |  |
| `android.media.MediaDescription.Builder setDescription(@Nullable CharSequence)` |  |
| `android.media.MediaDescription.Builder setExtras(@Nullable android.os.Bundle)` |  |
| `android.media.MediaDescription.Builder setIconBitmap(@Nullable android.graphics.Bitmap)` |  |
| `android.media.MediaDescription.Builder setIconUri(@Nullable android.net.Uri)` |  |
| `android.media.MediaDescription.Builder setMediaId(@Nullable String)` |  |
| `android.media.MediaDescription.Builder setMediaUri(@Nullable android.net.Uri)` |  |
| `android.media.MediaDescription.Builder setSubtitle(@Nullable CharSequence)` |  |
| `android.media.MediaDescription.Builder setTitle(@Nullable CharSequence)` |  |

---

### `class final MediaDrm`

- **Implements:** `java.lang.AutoCloseable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDrm(@NonNull java.util.UUID) throws android.media.UnsupportedSchemeException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int EVENT_KEY_REQUIRED = 2` |  |
| `static final int EVENT_SESSION_RECLAIMED = 5` |  |
| `static final int EVENT_VENDOR_DEFINED = 4` |  |
| `static final int HDCP_LEVEL_UNKNOWN = 0` |  |
| `static final int HDCP_NONE = 1` |  |
| `static final int HDCP_NO_DIGITAL_OUTPUT = 2147483647` |  |
| `static final int HDCP_V1 = 2` |  |
| `static final int HDCP_V2 = 3` |  |
| `static final int HDCP_V2_1 = 4` |  |
| `static final int HDCP_V2_2 = 5` |  |
| `static final int HDCP_V2_3 = 6` |  |
| `static final int KEY_TYPE_OFFLINE = 2` |  |
| `static final int KEY_TYPE_RELEASE = 3` |  |
| `static final int KEY_TYPE_STREAMING = 1` |  |
| `static final int OFFLINE_LICENSE_STATE_RELEASED = 2` |  |
| `static final int OFFLINE_LICENSE_STATE_UNKNOWN = 0` |  |
| `static final int OFFLINE_LICENSE_STATE_USABLE = 1` |  |
| `static final String PROPERTY_ALGORITHMS = "algorithms"` |  |
| `static final String PROPERTY_DESCRIPTION = "description"` |  |
| `static final String PROPERTY_DEVICE_UNIQUE_ID = "deviceUniqueId"` |  |
| `static final String PROPERTY_VENDOR = "vendor"` |  |
| `static final String PROPERTY_VERSION = "version"` |  |
| `static final int SECURITY_LEVEL_HW_SECURE_ALL = 5` |  |
| `static final int SECURITY_LEVEL_HW_SECURE_CRYPTO = 3` |  |
| `static final int SECURITY_LEVEL_HW_SECURE_DECODE = 4` |  |
| `static final int SECURITY_LEVEL_SW_SECURE_CRYPTO = 1` |  |
| `static final int SECURITY_LEVEL_SW_SECURE_DECODE = 2` |  |
| `static final int SECURITY_LEVEL_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearOnEventListener()` |  |
| `void clearOnExpirationUpdateListener()` |  |
| `void clearOnKeyStatusChangeListener()` |  |
| `void clearOnSessionLostStateListener()` |  |
| `void close()` |  |
| `void closeSession(@NonNull byte[])` |  |
| `android.media.MediaDrm.CryptoSession getCryptoSession(@NonNull byte[], @NonNull String, @NonNull String)` |  |
| `static int getMaxSecurityLevel()` |  |
| `int getMaxSessionCount()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `int getOfflineLicenseState(@NonNull byte[])` |  |
| `int getOpenSessionCount()` |  |
| `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID)` |  |
| `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID, @NonNull String)` |  |
| `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID, @NonNull String, @android.media.MediaDrm.SecurityLevel int)` |  |
| `void provideProvisionResponse(@NonNull byte[]) throws android.media.DeniedByServerException` |  |
| `void releaseSecureStops(@NonNull byte[])` |  |
| `void removeAllSecureStops()` |  |
| `void removeKeys(@NonNull byte[])` |  |
| `void removeOfflineLicense(@NonNull byte[])` |  |
| `void removeSecureStop(@NonNull byte[])` |  |
| `void restoreKeys(@NonNull byte[], @NonNull byte[])` |  |
| `void setOnEventListener(@Nullable android.media.MediaDrm.OnEventListener)` |  |
| `void setOnEventListener(@Nullable android.media.MediaDrm.OnEventListener, @Nullable android.os.Handler)` |  |
| `void setOnEventListener(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaDrm.OnEventListener)` |  |
| `void setOnExpirationUpdateListener(@Nullable android.media.MediaDrm.OnExpirationUpdateListener, @Nullable android.os.Handler)` |  |
| `void setOnExpirationUpdateListener(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaDrm.OnExpirationUpdateListener)` |  |
| `void setOnKeyStatusChangeListener(@Nullable android.media.MediaDrm.OnKeyStatusChangeListener, @Nullable android.os.Handler)` |  |
| `void setOnKeyStatusChangeListener(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaDrm.OnKeyStatusChangeListener)` |  |
| `void setOnSessionLostStateListener(@Nullable android.media.MediaDrm.OnSessionLostStateListener, @Nullable android.os.Handler)` |  |
| `void setOnSessionLostStateListener(@NonNull java.util.concurrent.Executor, @Nullable android.media.MediaDrm.OnSessionLostStateListener)` |  |
| `void setPropertyByteArray(@NonNull String, @NonNull byte[])` |  |
| `void setPropertyString(@NonNull String, @NonNull String)` |  |

---

### `class final MediaDrm.CryptoSession`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean verify(@NonNull byte[], @NonNull byte[], @NonNull byte[])` |  |

---

### `class static final MediaDrm.KeyRequest`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int REQUEST_TYPE_INITIAL = 0` |  |
| `static final int REQUEST_TYPE_NONE = 3` |  |
| `static final int REQUEST_TYPE_RELEASE = 2` |  |
| `static final int REQUEST_TYPE_RENEWAL = 1` |  |
| `static final int REQUEST_TYPE_UPDATE = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getRequestType()` |  |

---

### `class static final MediaDrm.KeyStatus`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATUS_EXPIRED = 1` |  |
| `static final int STATUS_INTERNAL_ERROR = 4` |  |
| `static final int STATUS_OUTPUT_NOT_ALLOWED = 2` |  |
| `static final int STATUS_PENDING = 3` |  |
| `static final int STATUS_USABLE = 0` |  |
| `static final int STATUS_USABLE_IN_FUTURE = 5` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getStatusCode()` |  |

---

### `class static final MediaDrm.MediaDrmStateException`

- **Extends:** `java.lang.IllegalStateException`

---

### `class static final MediaDrm.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CLOSE_SESSION_ERROR_COUNT = "drm.mediadrm.close_session.error.count"` |  |
| `static final String CLOSE_SESSION_ERROR_LIST = "drm.mediadrm.close_session.error.list"` |  |
| `static final String CLOSE_SESSION_OK_COUNT = "drm.mediadrm.close_session.ok.count"` |  |
| `static final String EVENT_KEY_EXPIRED_COUNT = "drm.mediadrm.event.KEY_EXPIRED.count"` |  |
| `static final String EVENT_KEY_NEEDED_COUNT = "drm.mediadrm.event.KEY_NEEDED.count"` |  |
| `static final String EVENT_PROVISION_REQUIRED_COUNT = "drm.mediadrm.event.PROVISION_REQUIRED.count"` |  |
| `static final String EVENT_SESSION_RECLAIMED_COUNT = "drm.mediadrm.event.SESSION_RECLAIMED.count"` |  |
| `static final String EVENT_VENDOR_DEFINED_COUNT = "drm.mediadrm.event.VENDOR_DEFINED.count"` |  |
| `static final String GET_DEVICE_UNIQUE_ID_ERROR_COUNT = "drm.mediadrm.get_device_unique_id.error.count"` |  |
| `static final String GET_DEVICE_UNIQUE_ID_ERROR_LIST = "drm.mediadrm.get_device_unique_id.error.list"` |  |
| `static final String GET_DEVICE_UNIQUE_ID_OK_COUNT = "drm.mediadrm.get_device_unique_id.ok.count"` |  |
| `static final String GET_KEY_REQUEST_ERROR_COUNT = "drm.mediadrm.get_key_request.error.count"` |  |
| `static final String GET_KEY_REQUEST_ERROR_LIST = "drm.mediadrm.get_key_request.error.list"` |  |
| `static final String GET_KEY_REQUEST_OK_COUNT = "drm.mediadrm.get_key_request.ok.count"` |  |
| `static final String GET_KEY_REQUEST_OK_TIME_MICROS = "drm.mediadrm.get_key_request.ok.average_time_micros"` |  |
| `static final String GET_PROVISION_REQUEST_ERROR_COUNT = "drm.mediadrm.get_provision_request.error.count"` |  |
| `static final String GET_PROVISION_REQUEST_ERROR_LIST = "drm.mediadrm.get_provision_request.error.list"` |  |
| `static final String GET_PROVISION_REQUEST_OK_COUNT = "drm.mediadrm.get_provision_request.ok.count"` |  |
| `static final String KEY_STATUS_EXPIRED_COUNT = "drm.mediadrm.key_status.EXPIRED.count"` |  |
| `static final String KEY_STATUS_INTERNAL_ERROR_COUNT = "drm.mediadrm.key_status.INTERNAL_ERROR.count"` |  |
| `static final String KEY_STATUS_OUTPUT_NOT_ALLOWED_COUNT = "drm.mediadrm.key_status_change.OUTPUT_NOT_ALLOWED.count"` |  |
| `static final String KEY_STATUS_PENDING_COUNT = "drm.mediadrm.key_status_change.PENDING.count"` |  |
| `static final String KEY_STATUS_USABLE_COUNT = "drm.mediadrm.key_status_change.USABLE.count"` |  |
| `static final String OPEN_SESSION_ERROR_COUNT = "drm.mediadrm.open_session.error.count"` |  |
| `static final String OPEN_SESSION_ERROR_LIST = "drm.mediadrm.open_session.error.list"` |  |
| `static final String OPEN_SESSION_OK_COUNT = "drm.mediadrm.open_session.ok.count"` |  |
| `static final String PROVIDE_KEY_RESPONSE_ERROR_COUNT = "drm.mediadrm.provide_key_response.error.count"` |  |
| `static final String PROVIDE_KEY_RESPONSE_ERROR_LIST = "drm.mediadrm.provide_key_response.error.list"` |  |
| `static final String PROVIDE_KEY_RESPONSE_OK_COUNT = "drm.mediadrm.provide_key_response.ok.count"` |  |
| `static final String PROVIDE_KEY_RESPONSE_OK_TIME_MICROS = "drm.mediadrm.provide_key_response.ok.average_time_micros"` |  |
| `static final String PROVIDE_PROVISION_RESPONSE_ERROR_COUNT = "drm.mediadrm.provide_provision_response.error.count"` |  |
| `static final String PROVIDE_PROVISION_RESPONSE_ERROR_LIST = "drm.mediadrm.provide_provision_response.error.list"` |  |
| `static final String PROVIDE_PROVISION_RESPONSE_OK_COUNT = "drm.mediadrm.provide_provision_response.ok.count"` |  |
| `static final String SESSION_END_TIMES_MS = "drm.mediadrm.session_end_times_ms"` |  |
| `static final String SESSION_START_TIMES_MS = "drm.mediadrm.session_start_times_ms"` |  |

---

### `interface static MediaDrm.OnEventListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onEvent(@NonNull android.media.MediaDrm, @Nullable byte[], int, int, @Nullable byte[])` |  |

---

### `interface static MediaDrm.OnExpirationUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onExpirationUpdate(@NonNull android.media.MediaDrm, @NonNull byte[], long)` |  |

---

### `interface static MediaDrm.OnKeyStatusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onKeyStatusChange(@NonNull android.media.MediaDrm, @NonNull byte[], @NonNull java.util.List<android.media.MediaDrm.KeyStatus>, boolean)` |  |

---

### `interface static MediaDrm.OnSessionLostStateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSessionLostState(@NonNull android.media.MediaDrm, @NonNull byte[])` |  |

---

### `class static final MediaDrm.ProvisionRequest`


---

### `class static final MediaDrm.SessionException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDrm.SessionException(int, @Nullable String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_RESOURCE_CONTENTION = 1` |  |
| `static final int ERROR_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorCode()` |  |

---

### `class MediaDrmException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDrmException(String)` |  |

---

### `class MediaDrmResetException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaDrmResetException(String)` |  |

---

### `class final MediaExtractor`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaExtractor()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SAMPLE_FLAG_ENCRYPTED = 2` |  |
| `static final int SAMPLE_FLAG_PARTIAL_FRAME = 4` |  |
| `static final int SAMPLE_FLAG_SYNC = 1` |  |
| `static final int SEEK_TO_CLOSEST_SYNC = 2` |  |
| `static final int SEEK_TO_NEXT_SYNC = 1` |  |
| `static final int SEEK_TO_PREVIOUS_SYNC = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean advance()` |  |
| `void finalize()` |  |
| `long getCachedDuration()` |  |
| `android.media.MediaExtractor.CasInfo getCasInfo(int)` |  |
| `android.media.DrmInitData getDrmInitData()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `boolean getSampleCryptoInfo(@NonNull android.media.MediaCodec.CryptoInfo)` |  |
| `int getSampleFlags()` |  |
| `long getSampleSize()` |  |
| `long getSampleTime()` |  |
| `int getSampleTrackIndex()` |  |
| `int getTrackCount()` |  |
| `boolean hasCacheReachedEndOfStream()` |  |
| `int readSampleData(@NonNull java.nio.ByteBuffer, int)` |  |
| `void release()` |  |
| `void seekTo(long, int)` |  |
| `void selectTrack(int)` |  |
| `void setDataSource(@NonNull android.media.MediaDataSource) throws java.io.IOException` |  |
| `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException` |  |
| `void setDataSource(@NonNull String, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException` |  |
| `void setDataSource(@NonNull String) throws java.io.IOException` |  |
| `void setDataSource(@NonNull android.content.res.AssetFileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setDataSource(@NonNull java.io.FileDescriptor) throws java.io.IOException` |  |
| `void setDataSource(@NonNull java.io.FileDescriptor, long, long) throws java.io.IOException` |  |
| `void setMediaCas(@NonNull android.media.MediaCas)` |  |
| `void unselectTrack(int)` |  |

---

### `class static final MediaExtractor.CasInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.MediaCas.Session getSession()` |  |
| `int getSystemId()` |  |

---

### `class static final MediaExtractor.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String FORMAT = "android.media.mediaextractor.fmt"` |  |
| `static final String MIME_TYPE = "android.media.mediaextractor.mime"` |  |
| `static final String TRACKS = "android.media.mediaextractor.ntrk"` |  |

---

### `class final MediaFormat`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaFormat()` |  |
| `MediaFormat(@NonNull android.media.MediaFormat)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int COLOR_RANGE_FULL = 1` |  |
| `static final int COLOR_RANGE_LIMITED = 2` |  |
| `static final int COLOR_STANDARD_BT2020 = 6` |  |
| `static final int COLOR_STANDARD_BT601_NTSC = 4` |  |
| `static final int COLOR_STANDARD_BT601_PAL = 2` |  |
| `static final int COLOR_STANDARD_BT709 = 1` |  |
| `static final int COLOR_TRANSFER_HLG = 7` |  |
| `static final int COLOR_TRANSFER_LINEAR = 1` |  |
| `static final int COLOR_TRANSFER_SDR_VIDEO = 3` |  |
| `static final int COLOR_TRANSFER_ST2084 = 6` |  |
| `static final String KEY_AAC_DRC_ALBUM_MODE = "aac-drc-album-mode"` |  |
| `static final String KEY_AAC_DRC_ATTENUATION_FACTOR = "aac-drc-cut-level"` |  |
| `static final String KEY_AAC_DRC_BOOST_FACTOR = "aac-drc-boost-level"` |  |
| `static final String KEY_AAC_DRC_EFFECT_TYPE = "aac-drc-effect-type"` |  |
| `static final String KEY_AAC_DRC_HEAVY_COMPRESSION = "aac-drc-heavy-compression"` |  |
| `static final String KEY_AAC_DRC_OUTPUT_LOUDNESS = "aac-drc-output-loudness"` |  |
| `static final String KEY_AAC_DRC_TARGET_REFERENCE_LEVEL = "aac-target-ref-level"` |  |
| `static final String KEY_AAC_ENCODED_TARGET_LEVEL = "aac-encoded-target-level"` |  |
| `static final String KEY_AAC_MAX_OUTPUT_CHANNEL_COUNT = "aac-max-output-channel_count"` |  |
| `static final String KEY_AAC_PROFILE = "aac-profile"` |  |
| `static final String KEY_AAC_SBR_MODE = "aac-sbr-mode"` |  |
| `static final String KEY_AUDIO_SESSION_ID = "audio-session-id"` |  |
| `static final String KEY_BITRATE_MODE = "bitrate-mode"` |  |
| `static final String KEY_BIT_RATE = "bitrate"` |  |
| `static final String KEY_CAPTION_SERVICE_NUMBER = "caption-service-number"` |  |
| `static final String KEY_CAPTURE_RATE = "capture-rate"` |  |
| `static final String KEY_CHANNEL_COUNT = "channel-count"` |  |
| `static final String KEY_CHANNEL_MASK = "channel-mask"` |  |
| `static final String KEY_CODECS_STRING = "codecs-string"` |  |
| `static final String KEY_COLOR_FORMAT = "color-format"` |  |
| `static final String KEY_COLOR_RANGE = "color-range"` |  |
| `static final String KEY_COLOR_STANDARD = "color-standard"` |  |
| `static final String KEY_COLOR_TRANSFER = "color-transfer"` |  |
| `static final String KEY_COMPLEXITY = "complexity"` |  |
| `static final String KEY_CREATE_INPUT_SURFACE_SUSPENDED = "create-input-buffers-suspended"` |  |
| `static final String KEY_DURATION = "durationUs"` |  |
| `static final String KEY_ENCODER_DELAY = "encoder-delay"` |  |
| `static final String KEY_ENCODER_PADDING = "encoder-padding"` |  |
| `static final String KEY_FLAC_COMPRESSION_LEVEL = "flac-compression-level"` |  |
| `static final String KEY_FRAME_RATE = "frame-rate"` |  |
| `static final String KEY_GRID_COLUMNS = "grid-cols"` |  |
| `static final String KEY_GRID_ROWS = "grid-rows"` |  |
| `static final String KEY_HAPTIC_CHANNEL_COUNT = "haptic-channel-count"` |  |
| `static final String KEY_HARDWARE_AV_SYNC_ID = "hw-av-sync-id"` |  |
| `static final String KEY_HDR10_PLUS_INFO = "hdr10-plus-info"` |  |
| `static final String KEY_HDR_STATIC_INFO = "hdr-static-info"` |  |
| `static final String KEY_HEIGHT = "height"` |  |
| `static final String KEY_INTRA_REFRESH_PERIOD = "intra-refresh-period"` |  |
| `static final String KEY_IS_ADTS = "is-adts"` |  |
| `static final String KEY_IS_AUTOSELECT = "is-autoselect"` |  |
| `static final String KEY_IS_DEFAULT = "is-default"` |  |
| `static final String KEY_IS_FORCED_SUBTITLE = "is-forced-subtitle"` |  |
| `static final String KEY_I_FRAME_INTERVAL = "i-frame-interval"` |  |
| `static final String KEY_LANGUAGE = "language"` |  |
| `static final String KEY_LATENCY = "latency"` |  |
| `static final String KEY_LEVEL = "level"` |  |
| `static final String KEY_LOW_LATENCY = "low-latency"` |  |
| `static final String KEY_MAX_B_FRAMES = "max-bframes"` |  |
| `static final String KEY_MAX_FPS_TO_ENCODER = "max-fps-to-encoder"` |  |
| `static final String KEY_MAX_HEIGHT = "max-height"` |  |
| `static final String KEY_MAX_INPUT_SIZE = "max-input-size"` |  |
| `static final String KEY_MAX_PTS_GAP_TO_ENCODER = "max-pts-gap-to-encoder"` |  |
| `static final String KEY_MAX_WIDTH = "max-width"` |  |
| `static final String KEY_MIME = "mime"` |  |
| `static final String KEY_OPERATING_RATE = "operating-rate"` |  |
| `static final String KEY_OUTPUT_REORDER_DEPTH = "output-reorder-depth"` |  |
| `static final String KEY_PCM_ENCODING = "pcm-encoding"` |  |
| `static final String KEY_PIXEL_ASPECT_RATIO_HEIGHT = "sar-height"` |  |
| `static final String KEY_PIXEL_ASPECT_RATIO_WIDTH = "sar-width"` |  |
| `static final String KEY_PREPEND_HEADER_TO_SYNC_FRAMES = "prepend-sps-pps-to-idr-frames"` |  |
| `static final String KEY_PRIORITY = "priority"` |  |
| `static final String KEY_PROFILE = "profile"` |  |
| `static final String KEY_PUSH_BLANK_BUFFERS_ON_STOP = "push-blank-buffers-on-shutdown"` |  |
| `static final String KEY_QUALITY = "quality"` |  |
| `static final String KEY_REPEAT_PREVIOUS_FRAME_AFTER = "repeat-previous-frame-after"` |  |
| `static final String KEY_ROTATION = "rotation-degrees"` |  |
| `static final String KEY_SAMPLE_RATE = "sample-rate"` |  |
| `static final String KEY_SLICE_HEIGHT = "slice-height"` |  |
| `static final String KEY_STRIDE = "stride"` |  |
| `static final String KEY_TEMPORAL_LAYERING = "ts-schema"` |  |
| `static final String KEY_TILE_HEIGHT = "tile-height"` |  |
| `static final String KEY_TILE_WIDTH = "tile-width"` |  |
| `static final String KEY_TRACK_ID = "track-id"` |  |
| `static final String KEY_WIDTH = "width"` |  |
| `static final String MIMETYPE_AUDIO_AAC = "audio/mp4a-latm"` |  |
| `static final String MIMETYPE_AUDIO_AC3 = "audio/ac3"` |  |
| `static final String MIMETYPE_AUDIO_AC4 = "audio/ac4"` |  |
| `static final String MIMETYPE_AUDIO_AMR_NB = "audio/3gpp"` |  |
| `static final String MIMETYPE_AUDIO_AMR_WB = "audio/amr-wb"` |  |
| `static final String MIMETYPE_AUDIO_EAC3 = "audio/eac3"` |  |
| `static final String MIMETYPE_AUDIO_EAC3_JOC = "audio/eac3-joc"` |  |
| `static final String MIMETYPE_AUDIO_FLAC = "audio/flac"` |  |
| `static final String MIMETYPE_AUDIO_G711_ALAW = "audio/g711-alaw"` |  |
| `static final String MIMETYPE_AUDIO_G711_MLAW = "audio/g711-mlaw"` |  |
| `static final String MIMETYPE_AUDIO_MPEG = "audio/mpeg"` |  |
| `static final String MIMETYPE_AUDIO_MSGSM = "audio/gsm"` |  |
| `static final String MIMETYPE_AUDIO_OPUS = "audio/opus"` |  |
| `static final String MIMETYPE_AUDIO_QCELP = "audio/qcelp"` |  |
| `static final String MIMETYPE_AUDIO_RAW = "audio/raw"` |  |
| `static final String MIMETYPE_AUDIO_SCRAMBLED = "audio/scrambled"` |  |
| `static final String MIMETYPE_AUDIO_VORBIS = "audio/vorbis"` |  |
| `static final String MIMETYPE_IMAGE_ANDROID_HEIC = "image/vnd.android.heic"` |  |
| `static final String MIMETYPE_TEXT_CEA_608 = "text/cea-608"` |  |
| `static final String MIMETYPE_TEXT_CEA_708 = "text/cea-708"` |  |
| `static final String MIMETYPE_TEXT_SUBRIP = "application/x-subrip"` |  |
| `static final String MIMETYPE_TEXT_VTT = "text/vtt"` |  |
| `static final String MIMETYPE_VIDEO_AV1 = "video/av01"` |  |
| `static final String MIMETYPE_VIDEO_AVC = "video/avc"` |  |
| `static final String MIMETYPE_VIDEO_DOLBY_VISION = "video/dolby-vision"` |  |
| `static final String MIMETYPE_VIDEO_H263 = "video/3gpp"` |  |
| `static final String MIMETYPE_VIDEO_HEVC = "video/hevc"` |  |
| `static final String MIMETYPE_VIDEO_MPEG2 = "video/mpeg2"` |  |
| `static final String MIMETYPE_VIDEO_MPEG4 = "video/mp4v-es"` |  |
| `static final String MIMETYPE_VIDEO_RAW = "video/raw"` |  |
| `static final String MIMETYPE_VIDEO_SCRAMBLED = "video/scrambled"` |  |
| `static final String MIMETYPE_VIDEO_VP8 = "video/x-vnd.on2.vp8"` |  |
| `static final String MIMETYPE_VIDEO_VP9 = "video/x-vnd.on2.vp9"` |  |
| `static final int TYPE_BYTE_BUFFER = 5` |  |
| `static final int TYPE_FLOAT = 3` |  |
| `static final int TYPE_INTEGER = 1` |  |
| `static final int TYPE_LONG = 2` |  |
| `static final int TYPE_NULL = 0` |  |
| `static final int TYPE_STRING = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean containsFeature(@NonNull String)` |  |
| `boolean containsKey(@NonNull String)` |  |
| `boolean getFeatureEnabled(@NonNull String)` |  |
| `float getFloat(@NonNull String)` |  |
| `float getFloat(@NonNull String, float)` |  |
| `int getInteger(@NonNull String)` |  |
| `int getInteger(@NonNull String, int)` |  |
| `long getLong(@NonNull String)` |  |
| `long getLong(@NonNull String, long)` |  |
| `int getValueTypeForKey(@NonNull String)` |  |
| `void removeFeature(@NonNull String)` |  |
| `void removeKey(@NonNull String)` |  |
| `void setByteBuffer(@NonNull String, @Nullable java.nio.ByteBuffer)` |  |
| `void setFeatureEnabled(@NonNull String, boolean)` |  |
| `void setFloat(@NonNull String, float)` |  |
| `void setInteger(@NonNull String, int)` |  |
| `void setLong(@NonNull String, long)` |  |
| `void setString(@NonNull String, @Nullable String)` |  |

---

### `class final MediaMetadata`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM"` |  |
| `static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART"` |  |
| `static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST"` |  |
| `static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI"` |  |
| `static final String METADATA_KEY_ART = "android.media.metadata.ART"` |  |
| `static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST"` |  |
| `static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI"` |  |
| `static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR"` |  |
| `static final String METADATA_KEY_BT_FOLDER_TYPE = "android.media.metadata.BT_FOLDER_TYPE"` |  |
| `static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION"` |  |
| `static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER"` |  |
| `static final String METADATA_KEY_DATE = "android.media.metadata.DATE"` |  |
| `static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER"` |  |
| `static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION"` |  |
| `static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON"` |  |
| `static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI"` |  |
| `static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE"` |  |
| `static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE"` |  |
| `static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION"` |  |
| `static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE"` |  |
| `static final String METADATA_KEY_MEDIA_ID = "android.media.metadata.MEDIA_ID"` |  |
| `static final String METADATA_KEY_MEDIA_URI = "android.media.metadata.MEDIA_URI"` |  |
| `static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS"` |  |
| `static final String METADATA_KEY_RATING = "android.media.metadata.RATING"` |  |
| `static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE"` |  |
| `static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER"` |  |
| `static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING"` |  |
| `static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER"` |  |
| `static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean containsKey(String)` |  |
| `int describeContents()` |  |
| `android.graphics.Bitmap getBitmap(String)` |  |
| `long getLong(String)` |  |
| `android.media.Rating getRating(String)` |  |
| `String getString(String)` |  |
| `CharSequence getText(String)` |  |
| `java.util.Set<java.lang.String> keySet()` |  |
| `int size()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final MediaMetadata.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaMetadata.Builder()` |  |
| `MediaMetadata.Builder(android.media.MediaMetadata)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.MediaMetadata build()` |  |
| `android.media.MediaMetadata.Builder putBitmap(String, android.graphics.Bitmap)` |  |
| `android.media.MediaMetadata.Builder putLong(String, long)` |  |
| `android.media.MediaMetadata.Builder putRating(String, android.media.Rating)` |  |
| `android.media.MediaMetadata.Builder putString(String, String)` |  |
| `android.media.MediaMetadata.Builder putText(String, CharSequence)` |  |

---

### `class abstract MediaMetadataEditor` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class MediaMetadataRetriever`

- **Implements:** `java.lang.AutoCloseable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaMetadataRetriever()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int METADATA_KEY_ALBUM = 1` |  |
| `static final int METADATA_KEY_ALBUMARTIST = 13` |  |
| `static final int METADATA_KEY_ARTIST = 2` |  |
| `static final int METADATA_KEY_AUTHOR = 3` |  |
| `static final int METADATA_KEY_BITRATE = 20` |  |
| `static final int METADATA_KEY_CAPTURE_FRAMERATE = 25` |  |
| `static final int METADATA_KEY_CD_TRACK_NUMBER = 0` |  |
| `static final int METADATA_KEY_COLOR_RANGE = 37` |  |
| `static final int METADATA_KEY_COLOR_STANDARD = 35` |  |
| `static final int METADATA_KEY_COLOR_TRANSFER = 36` |  |
| `static final int METADATA_KEY_COMPILATION = 15` |  |
| `static final int METADATA_KEY_COMPOSER = 4` |  |
| `static final int METADATA_KEY_DATE = 5` |  |
| `static final int METADATA_KEY_DISC_NUMBER = 14` |  |
| `static final int METADATA_KEY_DURATION = 9` |  |
| `static final int METADATA_KEY_EXIF_LENGTH = 34` |  |
| `static final int METADATA_KEY_EXIF_OFFSET = 33` |  |
| `static final int METADATA_KEY_GENRE = 6` |  |
| `static final int METADATA_KEY_HAS_AUDIO = 16` |  |
| `static final int METADATA_KEY_HAS_IMAGE = 26` |  |
| `static final int METADATA_KEY_HAS_VIDEO = 17` |  |
| `static final int METADATA_KEY_IMAGE_COUNT = 27` |  |
| `static final int METADATA_KEY_IMAGE_HEIGHT = 30` |  |
| `static final int METADATA_KEY_IMAGE_PRIMARY = 28` |  |
| `static final int METADATA_KEY_IMAGE_ROTATION = 31` |  |
| `static final int METADATA_KEY_IMAGE_WIDTH = 29` |  |
| `static final int METADATA_KEY_LOCATION = 23` |  |
| `static final int METADATA_KEY_MIMETYPE = 12` |  |
| `static final int METADATA_KEY_NUM_TRACKS = 10` |  |
| `static final int METADATA_KEY_TITLE = 7` |  |
| `static final int METADATA_KEY_VIDEO_FRAME_COUNT = 32` |  |
| `static final int METADATA_KEY_VIDEO_HEIGHT = 19` |  |
| `static final int METADATA_KEY_VIDEO_ROTATION = 24` |  |
| `static final int METADATA_KEY_VIDEO_WIDTH = 18` |  |
| `static final int METADATA_KEY_WRITER = 11` |  |
| `static final int METADATA_KEY_YEAR = 8` |  |
| `static final int OPTION_CLOSEST = 3` |  |
| `static final int OPTION_CLOSEST_SYNC = 2` |  |
| `static final int OPTION_NEXT_SYNC = 1` |  |
| `static final int OPTION_PREVIOUS_SYNC = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void release()` |  |
| `void setDataSource(String) throws java.lang.IllegalArgumentException` |  |
| `void setDataSource(String, java.util.Map<java.lang.String,java.lang.String>) throws java.lang.IllegalArgumentException` |  |
| `void setDataSource(java.io.FileDescriptor, long, long) throws java.lang.IllegalArgumentException` |  |
| `void setDataSource(java.io.FileDescriptor) throws java.lang.IllegalArgumentException` |  |
| `void setDataSource(android.content.Context, android.net.Uri) throws java.lang.IllegalArgumentException, java.lang.SecurityException` |  |
| `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException` |  |

---

### `class static final MediaMetadataRetriever.BitmapParams`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaMetadataRetriever.BitmapParams()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void setPreferredConfig(@NonNull android.graphics.Bitmap.Config)` |  |

---

### `class final MediaMuxer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaMuxer(@NonNull String, int) throws java.io.IOException` |  |
| `MediaMuxer(@NonNull java.io.FileDescriptor, int) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int addTrack(@NonNull android.media.MediaFormat)` |  |
| `void release()` |  |
| `void setLocation(float, float)` |  |
| `void setOrientationHint(int)` |  |
| `void start()` |  |
| `void stop()` |  |
| `void writeSampleData(int, @NonNull java.nio.ByteBuffer, @NonNull android.media.MediaCodec.BufferInfo)` |  |

---

### `class static final MediaMuxer.OutputFormat`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MUXER_OUTPUT_3GPP = 2` |  |
| `static final int MUXER_OUTPUT_HEIF = 3` |  |
| `static final int MUXER_OUTPUT_MPEG_4 = 0` |  |
| `static final int MUXER_OUTPUT_OGG = 4` |  |
| `static final int MUXER_OUTPUT_WEBM = 1` |  |

---

### `class final MediaParser`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String PARAMETER_ADTS_ENABLE_CBR_SEEKING = "android.media.mediaparser.adts.enableCbrSeeking"` |  |
| `static final String PARAMETER_AMR_ENABLE_CBR_SEEKING = "android.media.mediaparser.amr.enableCbrSeeking"` |  |
| `static final String PARAMETER_FLAC_DISABLE_ID3 = "android.media.mediaparser.flac.disableId3"` |  |
| `static final String PARAMETER_MATROSKA_DISABLE_CUES_SEEKING = "android.media.mediaparser.matroska.disableCuesSeeking"` |  |
| `static final String PARAMETER_MP3_DISABLE_ID3 = "android.media.mediaparser.mp3.disableId3"` |  |
| `static final String PARAMETER_MP3_ENABLE_CBR_SEEKING = "android.media.mediaparser.mp3.enableCbrSeeking"` |  |
| `static final String PARAMETER_MP3_ENABLE_INDEX_SEEKING = "android.media.mediaparser.mp3.enableIndexSeeking"` |  |
| `static final String PARAMETER_MP4_IGNORE_EDIT_LISTS = "android.media.mediaparser.mp4.ignoreEditLists"` |  |
| `static final String PARAMETER_MP4_IGNORE_TFDT_BOX = "android.media.mediaparser.mp4.ignoreTfdtBox"` |  |
| `static final String PARAMETER_MP4_TREAT_VIDEO_FRAMES_AS_KEYFRAMES = "android.media.mediaparser.mp4.treatVideoFramesAsKeyframes"` |  |
| `static final String PARAMETER_TS_ALLOW_NON_IDR_AVC_KEYFRAMES = "android.media.mediaparser.ts.allowNonIdrAvcKeyframes"` |  |
| `static final String PARAMETER_TS_DETECT_ACCESS_UNITS = "android.media.mediaparser.ts.ignoreDetectAccessUnits"` |  |
| `static final String PARAMETER_TS_ENABLE_HDMV_DTS_AUDIO_STREAMS = "android.media.mediaparser.ts.enableHdmvDtsAudioStreams"` |  |
| `static final String PARAMETER_TS_IGNORE_AAC_STREAM = "android.media.mediaparser.ts.ignoreAacStream"` |  |
| `static final String PARAMETER_TS_IGNORE_AVC_STREAM = "android.media.mediaparser.ts.ignoreAvcStream"` |  |
| `static final String PARAMETER_TS_IGNORE_SPLICE_INFO_STREAM = "android.media.mediaparser.ts.ignoreSpliceInfoStream"` |  |
| `static final String PARAMETER_TS_MODE = "android.media.mediaparser.ts.mode"` |  |
| `static final String PARSER_NAME_AC3 = "android.media.mediaparser.Ac3Parser"` |  |
| `static final String PARSER_NAME_AC4 = "android.media.mediaparser.Ac4Parser"` |  |
| `static final String PARSER_NAME_ADTS = "android.media.mediaparser.AdtsParser"` |  |
| `static final String PARSER_NAME_AMR = "android.media.mediaparser.AmrParser"` |  |
| `static final String PARSER_NAME_FLAC = "android.media.mediaparser.FlacParser"` |  |
| `static final String PARSER_NAME_FLV = "android.media.mediaparser.FlvParser"` |  |
| `static final String PARSER_NAME_FMP4 = "android.media.mediaparser.FragmentedMp4Parser"` |  |
| `static final String PARSER_NAME_MATROSKA = "android.media.mediaparser.MatroskaParser"` |  |
| `static final String PARSER_NAME_MP3 = "android.media.mediaparser.Mp3Parser"` |  |
| `static final String PARSER_NAME_MP4 = "android.media.mediaparser.Mp4Parser"` |  |
| `static final String PARSER_NAME_OGG = "android.media.mediaparser.OggParser"` |  |
| `static final String PARSER_NAME_PS = "android.media.mediaparser.PsParser"` |  |
| `static final String PARSER_NAME_TS = "android.media.mediaparser.TsParser"` |  |
| `static final String PARSER_NAME_UNKNOWN = "android.media.mediaparser.UNKNOWN"` |  |
| `static final String PARSER_NAME_WAV = "android.media.mediaparser.WavParser"` |  |
| `static final int SAMPLE_FLAG_DECODE_ONLY = -2147483648` |  |
| `static final int SAMPLE_FLAG_ENCRYPTED = 1073741824` |  |
| `static final int SAMPLE_FLAG_HAS_SUPPLEMENTAL_DATA = 268435456` |  |
| `static final int SAMPLE_FLAG_KEY_FRAME = 1` |  |
| `static final int SAMPLE_FLAG_LAST_SAMPLE = 536870912` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean advance(@NonNull android.media.MediaParser.SeekableInputReader) throws java.io.IOException` |  |
| `void release()` |  |
| `void seek(@NonNull android.media.MediaParser.SeekPoint)` |  |
| `boolean supportsParameter(@NonNull String)` |  |

---

### `interface static MediaParser.InputReader`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getLength()` |  |
| `long getPosition()` |  |
| `int read(@NonNull byte[], int, int) throws java.io.IOException` |  |

---

### `interface static MediaParser.OutputConsumer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSampleCompleted(int, long, int, int, int, @Nullable android.media.MediaCodec.CryptoInfo)` |  |
| `void onSampleDataFound(int, @NonNull android.media.MediaParser.InputReader) throws java.io.IOException` |  |
| `void onSeekMapFound(@NonNull android.media.MediaParser.SeekMap)` |  |
| `void onTrackCountFound(int)` |  |
| `void onTrackDataFound(int, @NonNull android.media.MediaParser.TrackData)` |  |

---

### `class static final MediaParser.ParsingException`

- **Extends:** `java.io.IOException`

---

### `class static final MediaParser.SeekMap`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int UNKNOWN_DURATION = -2147483648` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getDurationMicros()` |  |
| `boolean isSeekable()` |  |

---

### `class static final MediaParser.SeekPoint`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final long position` |  |
| `final long timeMicros` |  |

---

### `interface static MediaParser.SeekableInputReader`

- **Extends:** `android.media.MediaParser.InputReader`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void seekToPosition(long)` |  |

---

### `class static final MediaParser.TrackData`


---

### `class static final MediaParser.UnrecognizedInputFormatException`

- **Extends:** `java.io.IOException`

---

### `class MediaPlayer`

- **Implements:** `android.media.AudioRouting android.media.VolumeAutomation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaPlayer()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEDIA_ERROR_IO = -1004` |  |
| `static final int MEDIA_ERROR_MALFORMED = -1007` |  |
| `static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200` |  |
| `static final int MEDIA_ERROR_SERVER_DIED = 100` |  |
| `static final int MEDIA_ERROR_TIMED_OUT = -110` |  |
| `static final int MEDIA_ERROR_UNKNOWN = 1` |  |
| `static final int MEDIA_ERROR_UNSUPPORTED = -1010` |  |
| `static final int MEDIA_INFO_AUDIO_NOT_PLAYING = 804` |  |
| `static final int MEDIA_INFO_BAD_INTERLEAVING = 800` |  |
| `static final int MEDIA_INFO_BUFFERING_END = 702` |  |
| `static final int MEDIA_INFO_BUFFERING_START = 701` |  |
| `static final int MEDIA_INFO_METADATA_UPDATE = 802` |  |
| `static final int MEDIA_INFO_NOT_SEEKABLE = 801` |  |
| `static final int MEDIA_INFO_STARTED_AS_NEXT = 2` |  |
| `static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902` |  |
| `static final int MEDIA_INFO_UNKNOWN = 1` |  |
| `static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901` |  |
| `static final int MEDIA_INFO_VIDEO_NOT_PLAYING = 805` |  |
| `static final int MEDIA_INFO_VIDEO_RENDERING_START = 3` |  |
| `static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700` |  |
| `static final int PREPARE_DRM_STATUS_PREPARATION_ERROR = 3` |  |
| `static final int PREPARE_DRM_STATUS_PROVISIONING_NETWORK_ERROR = 1` |  |
| `static final int PREPARE_DRM_STATUS_PROVISIONING_SERVER_ERROR = 2` |  |
| `static final int PREPARE_DRM_STATUS_SUCCESS = 0` |  |
| `static final int SEEK_CLOSEST = 3` |  |
| `static final int SEEK_CLOSEST_SYNC = 2` |  |
| `static final int SEEK_NEXT_SYNC = 1` |  |
| `static final int SEEK_PREVIOUS_SYNC = 0` |  |
| `static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1` |  |
| `static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` |  |
| `void addTimedTextSource(String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void addTimedTextSource(android.content.Context, android.net.Uri, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void addTimedTextSource(java.io.FileDescriptor, String) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void addTimedTextSource(java.io.FileDescriptor, long, long, String) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void attachAuxEffect(int)` |  |
| `void clearOnMediaTimeDiscontinuityListener()` |  |
| `void clearOnSubtitleDataListener()` |  |
| `static android.media.MediaPlayer create(android.content.Context, android.net.Uri)` |  |
| `static android.media.MediaPlayer create(android.content.Context, android.net.Uri, android.view.SurfaceHolder)` |  |
| `static android.media.MediaPlayer create(android.content.Context, android.net.Uri, android.view.SurfaceHolder, android.media.AudioAttributes, int)` |  |
| `static android.media.MediaPlayer create(android.content.Context, int)` |  |
| `static android.media.MediaPlayer create(android.content.Context, int, android.media.AudioAttributes, int)` |  |
| `void deselectTrack(int) throws java.lang.IllegalStateException` |  |
| `void finalize()` |  |
| `int getAudioSessionId()` |  |
| `int getCurrentPosition()` |  |
| `android.media.MediaPlayer.DrmInfo getDrmInfo()` |  |
| `int getDuration()` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `android.media.AudioDeviceInfo getPreferredDevice()` |  |
| `android.media.AudioDeviceInfo getRoutedDevice()` |  |
| `int getSelectedTrack(int) throws java.lang.IllegalStateException` |  |
| `android.media.MediaPlayer.TrackInfo[] getTrackInfo() throws java.lang.IllegalStateException` |  |
| `int getVideoHeight()` |  |
| `int getVideoWidth()` |  |
| `boolean isLooping()` |  |
| `boolean isPlaying()` |  |
| `void pause() throws java.lang.IllegalStateException` |  |
| `void prepare() throws java.io.IOException, java.lang.IllegalStateException` |  |
| `void prepareAsync() throws java.lang.IllegalStateException` |  |
| `void prepareDrm(@NonNull java.util.UUID) throws android.media.MediaPlayer.ProvisioningNetworkErrorException, android.media.MediaPlayer.ProvisioningServerErrorException, android.media.ResourceBusyException, android.media.UnsupportedSchemeException` |  |
| `byte[] provideKeyResponse(@Nullable byte[], @NonNull byte[]) throws android.media.DeniedByServerException, android.media.MediaPlayer.NoDrmSchemeException` |  |
| `void release()` |  |
| `void releaseDrm() throws android.media.MediaPlayer.NoDrmSchemeException` |  |
| `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` |  |
| `void reset()` |  |
| `void restoreKeys(@NonNull byte[]) throws android.media.MediaPlayer.NoDrmSchemeException` |  |
| `void seekTo(long, int)` |  |
| `void seekTo(int) throws java.lang.IllegalStateException` |  |
| `void selectTrack(int) throws java.lang.IllegalStateException` |  |
| `void setAudioAttributes(android.media.AudioAttributes) throws java.lang.IllegalArgumentException` |  |
| `void setAudioSessionId(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setAuxEffectSendLevel(float)` |  |
| `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` |  |
| `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>, @Nullable java.util.List<java.net.HttpCookie>) throws java.io.IOException` |  |
| `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` |  |
| `void setDataSource(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` |  |
| `void setDataSource(@NonNull android.content.res.AssetFileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setDataSource(java.io.FileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setDataSource(java.io.FileDescriptor, long, long) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` |  |
| `void setDisplay(android.view.SurfaceHolder)` |  |
| `void setDrmPropertyString(@NonNull String, @NonNull String) throws android.media.MediaPlayer.NoDrmSchemeException` |  |
| `void setLooping(boolean)` |  |
| `void setNextMediaPlayer(android.media.MediaPlayer)` |  |
| `void setOnBufferingUpdateListener(android.media.MediaPlayer.OnBufferingUpdateListener)` |  |
| `void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener)` |  |
| `void setOnDrmConfigHelper(android.media.MediaPlayer.OnDrmConfigHelper)` |  |
| `void setOnDrmInfoListener(android.media.MediaPlayer.OnDrmInfoListener)` |  |
| `void setOnDrmInfoListener(android.media.MediaPlayer.OnDrmInfoListener, android.os.Handler)` |  |
| `void setOnDrmPreparedListener(android.media.MediaPlayer.OnDrmPreparedListener)` |  |
| `void setOnDrmPreparedListener(android.media.MediaPlayer.OnDrmPreparedListener, android.os.Handler)` |  |
| `void setOnErrorListener(android.media.MediaPlayer.OnErrorListener)` |  |
| `void setOnInfoListener(android.media.MediaPlayer.OnInfoListener)` |  |
| `void setOnMediaTimeDiscontinuityListener(@NonNull android.media.MediaPlayer.OnMediaTimeDiscontinuityListener, @NonNull android.os.Handler)` |  |
| `void setOnMediaTimeDiscontinuityListener(@NonNull android.media.MediaPlayer.OnMediaTimeDiscontinuityListener)` |  |
| `void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener)` |  |
| `void setOnSeekCompleteListener(android.media.MediaPlayer.OnSeekCompleteListener)` |  |
| `void setOnSubtitleDataListener(@NonNull android.media.MediaPlayer.OnSubtitleDataListener, @NonNull android.os.Handler)` |  |
| `void setOnSubtitleDataListener(@NonNull android.media.MediaPlayer.OnSubtitleDataListener)` |  |
| `void setOnTimedMetaDataAvailableListener(android.media.MediaPlayer.OnTimedMetaDataAvailableListener)` |  |
| `void setOnTimedTextListener(android.media.MediaPlayer.OnTimedTextListener)` |  |
| `void setOnVideoSizeChangedListener(android.media.MediaPlayer.OnVideoSizeChangedListener)` |  |
| `void setPlaybackParams(@NonNull android.media.PlaybackParams)` |  |
| `boolean setPreferredDevice(android.media.AudioDeviceInfo)` |  |
| `void setScreenOnWhilePlaying(boolean)` |  |
| `void setSurface(android.view.Surface)` |  |
| `void setSyncParams(@NonNull android.media.SyncParams)` |  |
| `void setVideoScalingMode(int)` |  |
| `void setVolume(float, float)` |  |
| `void setWakeMode(android.content.Context, int)` |  |
| `void start() throws java.lang.IllegalStateException` |  |
| `void stop() throws java.lang.IllegalStateException` |  |

---

### `class static final MediaPlayer.DrmInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Map<java.util.UUID,byte[]> getPssh()` |  |
| `java.util.UUID[] getSupportedSchemes()` |  |

---

### `class static final MediaPlayer.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CODEC_AUDIO = "android.media.mediaplayer.audio.codec"` |  |
| `static final String CODEC_VIDEO = "android.media.mediaplayer.video.codec"` |  |
| `static final String DURATION = "android.media.mediaplayer.durationMs"` |  |
| `static final String ERRORS = "android.media.mediaplayer.err"` |  |
| `static final String ERROR_CODE = "android.media.mediaplayer.errcode"` |  |
| `static final String FRAMES = "android.media.mediaplayer.frames"` |  |
| `static final String FRAMES_DROPPED = "android.media.mediaplayer.dropped"` |  |
| `static final String HEIGHT = "android.media.mediaplayer.height"` |  |
| `static final String MIME_TYPE_AUDIO = "android.media.mediaplayer.audio.mime"` |  |
| `static final String MIME_TYPE_VIDEO = "android.media.mediaplayer.video.mime"` |  |
| `static final String PLAYING = "android.media.mediaplayer.playingMs"` |  |
| `static final String WIDTH = "android.media.mediaplayer.width"` |  |

---

### `class static final MediaPlayer.NoDrmSchemeException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaPlayer.NoDrmSchemeException(String)` |  |

---

### `interface static MediaPlayer.OnBufferingUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onBufferingUpdate(android.media.MediaPlayer, int)` |  |

---

### `interface static MediaPlayer.OnCompletionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCompletion(android.media.MediaPlayer)` |  |

---

### `interface static MediaPlayer.OnDrmConfigHelper`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDrmConfig(android.media.MediaPlayer)` |  |

---

### `interface static MediaPlayer.OnDrmInfoListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDrmInfo(android.media.MediaPlayer, android.media.MediaPlayer.DrmInfo)` |  |

---

### `interface static MediaPlayer.OnDrmPreparedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDrmPrepared(android.media.MediaPlayer, int)` |  |

---

### `interface static MediaPlayer.OnErrorListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onError(android.media.MediaPlayer, int, int)` |  |

---

### `interface static MediaPlayer.OnInfoListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onInfo(android.media.MediaPlayer, int, int)` |  |

---

### `interface static MediaPlayer.OnMediaTimeDiscontinuityListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onMediaTimeDiscontinuity(@NonNull android.media.MediaPlayer, @NonNull android.media.MediaTimestamp)` |  |

---

### `interface static MediaPlayer.OnPreparedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onPrepared(android.media.MediaPlayer)` |  |

---

### `interface static MediaPlayer.OnSeekCompleteListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSeekComplete(android.media.MediaPlayer)` |  |

---

### `interface static MediaPlayer.OnSubtitleDataListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSubtitleData(@NonNull android.media.MediaPlayer, @NonNull android.media.SubtitleData)` |  |

---

### `interface static MediaPlayer.OnTimedMetaDataAvailableListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTimedMetaDataAvailable(android.media.MediaPlayer, android.media.TimedMetaData)` |  |

---

### `interface static MediaPlayer.OnTimedTextListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTimedText(android.media.MediaPlayer, android.media.TimedText)` |  |

---

### `interface static MediaPlayer.OnVideoSizeChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onVideoSizeChanged(android.media.MediaPlayer, int, int)` |  |

---

### `class static final MediaPlayer.ProvisioningNetworkErrorException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaPlayer.ProvisioningNetworkErrorException(String)` |  |

---

### `class static final MediaPlayer.ProvisioningServerErrorException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaPlayer.ProvisioningServerErrorException(String)` |  |

---

### `class static MediaPlayer.TrackInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEDIA_TRACK_TYPE_AUDIO = 2` |  |
| `static final int MEDIA_TRACK_TYPE_METADATA = 5` |  |
| `static final int MEDIA_TRACK_TYPE_SUBTITLE = 4` |  |
| `static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3` |  |
| `static final int MEDIA_TRACK_TYPE_UNKNOWN = 0` |  |
| `static final int MEDIA_TRACK_TYPE_VIDEO = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.MediaFormat getFormat()` |  |
| `String getLanguage()` |  |
| `int getTrackType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class MediaRecorder`

- **Implements:** `android.media.AudioRecordingMonitor android.media.AudioRouting android.media.MicrophoneDirection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRecorder()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEDIA_ERROR_SERVER_DIED = 100` |  |
| `static final int MEDIA_RECORDER_ERROR_UNKNOWN = 1` |  |
| `static final int MEDIA_RECORDER_INFO_MAX_DURATION_REACHED = 800` |  |
| `static final int MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING = 802` |  |
| `static final int MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED = 801` |  |
| `static final int MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED = 803` |  |
| `static final int MEDIA_RECORDER_INFO_UNKNOWN = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` |  |
| `void finalize()` |  |
| `java.util.List<android.media.MicrophoneInfo> getActiveMicrophones() throws java.io.IOException` |  |
| `static final int getAudioSourceMax()` |  |
| `int getMaxAmplitude() throws java.lang.IllegalStateException` |  |
| `android.os.PersistableBundle getMetrics()` |  |
| `android.media.AudioDeviceInfo getPreferredDevice()` |  |
| `android.media.AudioDeviceInfo getRoutedDevice()` |  |
| `android.view.Surface getSurface()` |  |
| `boolean isPrivacySensitive()` |  |
| `void pause() throws java.lang.IllegalStateException` |  |
| `void prepare() throws java.io.IOException, java.lang.IllegalStateException` |  |
| `void registerAudioRecordingCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioManager.AudioRecordingCallback)` |  |
| `void release()` |  |
| `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` |  |
| `void reset()` |  |
| `void resume() throws java.lang.IllegalStateException` |  |
| `void setAudioChannels(int)` |  |
| `void setAudioEncoder(int) throws java.lang.IllegalStateException` |  |
| `void setAudioEncodingBitRate(int)` |  |
| `void setAudioSamplingRate(int)` |  |
| `void setAudioSource(int) throws java.lang.IllegalStateException` |  |
| `void setCaptureRate(double)` |  |
| `void setInputSurface(@NonNull android.view.Surface)` |  |
| `void setLocation(float, float)` |  |
| `void setMaxDuration(int) throws java.lang.IllegalArgumentException` |  |
| `void setMaxFileSize(long) throws java.lang.IllegalArgumentException` |  |
| `void setNextOutputFile(java.io.FileDescriptor) throws java.io.IOException` |  |
| `void setNextOutputFile(java.io.File) throws java.io.IOException` |  |
| `void setOnErrorListener(android.media.MediaRecorder.OnErrorListener)` |  |
| `void setOnInfoListener(android.media.MediaRecorder.OnInfoListener)` |  |
| `void setOrientationHint(int)` |  |
| `void setOutputFile(java.io.FileDescriptor) throws java.lang.IllegalStateException` |  |
| `void setOutputFile(java.io.File)` |  |
| `void setOutputFile(String) throws java.lang.IllegalStateException` |  |
| `void setOutputFormat(int) throws java.lang.IllegalStateException` |  |
| `boolean setPreferredDevice(android.media.AudioDeviceInfo)` |  |
| `boolean setPreferredMicrophoneDirection(int)` |  |
| `boolean setPreferredMicrophoneFieldDimension(@FloatRange(from=-1.0, to=1.0) float)` |  |
| `void setPreviewDisplay(android.view.Surface)` |  |
| `void setPrivacySensitive(boolean)` |  |
| `void setProfile(android.media.CamcorderProfile)` |  |
| `void setVideoEncoder(int) throws java.lang.IllegalStateException` |  |
| `void setVideoEncodingBitRate(int)` |  |
| `void setVideoEncodingProfileLevel(int, int)` |  |
| `void setVideoFrameRate(int) throws java.lang.IllegalStateException` |  |
| `void setVideoSize(int, int) throws java.lang.IllegalStateException` |  |
| `void setVideoSource(int) throws java.lang.IllegalStateException` |  |
| `void start() throws java.lang.IllegalStateException` |  |
| `void stop() throws java.lang.IllegalStateException` |  |
| `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` |  |

---

### `class final MediaRecorder.AudioEncoder`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AAC = 3` |  |
| `static final int AAC_ELD = 5` |  |
| `static final int AMR_NB = 1` |  |
| `static final int AMR_WB = 2` |  |
| `static final int DEFAULT = 0` |  |
| `static final int HE_AAC = 4` |  |
| `static final int OPUS = 7` |  |
| `static final int VORBIS = 6` |  |

---

### `class final MediaRecorder.AudioSource`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CAMCORDER = 5` |  |
| `static final int DEFAULT = 0` |  |
| `static final int MIC = 1` |  |
| `static final int UNPROCESSED = 9` |  |
| `static final int VOICE_CALL = 4` |  |
| `static final int VOICE_COMMUNICATION = 7` |  |
| `static final int VOICE_DOWNLINK = 3` |  |
| `static final int VOICE_PERFORMANCE = 10` |  |
| `static final int VOICE_RECOGNITION = 6` |  |
| `static final int VOICE_UPLINK = 2` |  |

---

### `class static final MediaRecorder.MetricsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String AUDIO_BITRATE = "android.media.mediarecorder.audio-bitrate"` |  |
| `static final String AUDIO_CHANNELS = "android.media.mediarecorder.audio-channels"` |  |
| `static final String AUDIO_SAMPLERATE = "android.media.mediarecorder.audio-samplerate"` |  |
| `static final String AUDIO_TIMESCALE = "android.media.mediarecorder.audio-timescale"` |  |
| `static final String CAPTURE_FPS = "android.media.mediarecorder.capture-fps"` |  |
| `static final String CAPTURE_FPS_ENABLE = "android.media.mediarecorder.capture-fpsenable"` |  |
| `static final String FRAMERATE = "android.media.mediarecorder.frame-rate"` |  |
| `static final String HEIGHT = "android.media.mediarecorder.height"` |  |
| `static final String MOVIE_TIMESCALE = "android.media.mediarecorder.movie-timescale"` |  |
| `static final String ROTATION = "android.media.mediarecorder.rotation"` |  |
| `static final String VIDEO_BITRATE = "android.media.mediarecorder.video-bitrate"` |  |
| `static final String VIDEO_IFRAME_INTERVAL = "android.media.mediarecorder.video-iframe-interval"` |  |
| `static final String VIDEO_LEVEL = "android.media.mediarecorder.video-encoder-level"` |  |
| `static final String VIDEO_PROFILE = "android.media.mediarecorder.video-encoder-profile"` |  |
| `static final String VIDEO_TIMESCALE = "android.media.mediarecorder.video-timescale"` |  |
| `static final String WIDTH = "android.media.mediarecorder.width"` |  |

---

### `interface static MediaRecorder.OnErrorListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onError(android.media.MediaRecorder, int, int)` |  |

---

### `interface static MediaRecorder.OnInfoListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onInfo(android.media.MediaRecorder, int, int)` |  |

---

### `class final MediaRecorder.OutputFormat`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AAC_ADTS = 6` |  |
| `static final int AMR_NB = 3` |  |
| `static final int AMR_WB = 4` |  |
| `static final int DEFAULT = 0` |  |
| `static final int MPEG_2_TS = 8` |  |
| `static final int MPEG_4 = 2` |  |
| `static final int OGG = 11` |  |
| `static final int THREE_GPP = 1` |  |
| `static final int WEBM = 9` |  |

---

### `class final MediaRecorder.VideoEncoder`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEFAULT = 0` |  |
| `static final int H263 = 1` |  |
| `static final int H264 = 2` |  |
| `static final int HEVC = 5` |  |
| `static final int MPEG_4_SP = 3` |  |
| `static final int VP8 = 4` |  |

---

### `class final MediaRecorder.VideoSource`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CAMERA = 1` |  |
| `static final int DEFAULT = 0` |  |
| `static final int SURFACE = 2` |  |

---

### `class final MediaRoute2Info`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONNECTION_STATE_CONNECTED = 2` |  |
| `static final int CONNECTION_STATE_CONNECTING = 1` |  |
| `static final int CONNECTION_STATE_DISCONNECTED = 0` |  |
| `static final String FEATURE_LIVE_AUDIO = "android.media.route.feature.LIVE_AUDIO"` |  |
| `static final String FEATURE_LIVE_VIDEO = "android.media.route.feature.LIVE_VIDEO"` |  |
| `static final String FEATURE_REMOTE_AUDIO_PLAYBACK = "android.media.route.feature.REMOTE_AUDIO_PLAYBACK"` |  |
| `static final String FEATURE_REMOTE_PLAYBACK = "android.media.route.feature.REMOTE_PLAYBACK"` |  |
| `static final String FEATURE_REMOTE_VIDEO_PLAYBACK = "android.media.route.feature.REMOTE_VIDEO_PLAYBACK"` |  |
| `static final int PLAYBACK_VOLUME_FIXED = 0` |  |
| `static final int PLAYBACK_VOLUME_VARIABLE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getConnectionState()` |  |
| `int getVolume()` |  |
| `int getVolumeHandling()` |  |
| `int getVolumeMax()` |  |
| `boolean isSystemRoute()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final MediaRoute2Info.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRoute2Info.Builder(@NonNull String, @NonNull CharSequence)` |  |
| `MediaRoute2Info.Builder(@NonNull android.media.MediaRoute2Info)` |  |

---

### `class abstract MediaRoute2ProviderService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRoute2ProviderService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int REASON_INVALID_COMMAND = 4` |  |
| `static final int REASON_NETWORK_ERROR = 2` |  |
| `static final int REASON_REJECTED = 1` |  |
| `static final int REASON_ROUTE_NOT_AVAILABLE = 3` |  |
| `static final int REASON_UNKNOWN_ERROR = 0` |  |
| `static final long REQUEST_ID_NONE = 0L` |  |
| `static final String SERVICE_INTERFACE = "android.media.MediaRoute2ProviderService"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void notifyRequestFailed(long, int)` |  |
| `final void notifyRoutes(@NonNull java.util.Collection<android.media.MediaRoute2Info>)` |  |
| `final void notifySessionCreated(long, @NonNull android.media.RoutingSessionInfo)` |  |
| `final void notifySessionReleased(@NonNull String)` |  |
| `final void notifySessionUpdated(@NonNull android.media.RoutingSessionInfo)` |  |
| `abstract void onCreateSession(long, @NonNull String, @NonNull String, @Nullable android.os.Bundle)` |  |
| `abstract void onDeselectRoute(long, @NonNull String, @NonNull String)` |  |
| `void onDiscoveryPreferenceChanged(@NonNull android.media.RouteDiscoveryPreference)` |  |
| `abstract void onReleaseSession(long, @NonNull String)` |  |
| `abstract void onSelectRoute(long, @NonNull String, @NonNull String)` |  |
| `abstract void onSetRouteVolume(long, @NonNull String, int)` |  |
| `abstract void onSetSessionVolume(long, @NonNull String, int)` |  |
| `abstract void onTransferToRoute(long, @NonNull String, @NonNull String)` |  |

---

### `class MediaRouter`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 1` |  |
| `static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 2` |  |
| `static final int ROUTE_TYPE_LIVE_AUDIO = 1` |  |
| `static final int ROUTE_TYPE_LIVE_VIDEO = 2` |  |
| `static final int ROUTE_TYPE_USER = 8388608` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addCallback(int, android.media.MediaRouter.Callback)` |  |
| `void addCallback(int, android.media.MediaRouter.Callback, int)` |  |
| `void addUserRoute(android.media.MediaRouter.UserRouteInfo)` |  |
| `void clearUserRoutes()` |  |
| `android.media.MediaRouter.RouteCategory createRouteCategory(CharSequence, boolean)` |  |
| `android.media.MediaRouter.RouteCategory createRouteCategory(int, boolean)` |  |
| `android.media.MediaRouter.UserRouteInfo createUserRoute(android.media.MediaRouter.RouteCategory)` |  |
| `android.media.MediaRouter.RouteCategory getCategoryAt(int)` |  |
| `int getCategoryCount()` |  |
| `android.media.MediaRouter.RouteInfo getDefaultRoute()` |  |
| `android.media.MediaRouter.RouteInfo getRouteAt(int)` |  |
| `int getRouteCount()` |  |
| `android.media.MediaRouter.RouteInfo getSelectedRoute(int)` |  |
| `void removeCallback(android.media.MediaRouter.Callback)` |  |
| `void removeUserRoute(android.media.MediaRouter.UserRouteInfo)` |  |
| `void selectRoute(int, @NonNull android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteAdded(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteChanged(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteGrouped(android.media.MediaRouter, android.media.MediaRouter.RouteInfo, android.media.MediaRouter.RouteGroup, int)` |  |
| `void onRoutePresentationDisplayChanged(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteRemoved(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteSelected(android.media.MediaRouter, int, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteUngrouped(android.media.MediaRouter, android.media.MediaRouter.RouteInfo, android.media.MediaRouter.RouteGroup)` |  |
| `abstract void onRouteUnselected(android.media.MediaRouter, int, android.media.MediaRouter.RouteInfo)` |  |
| `abstract void onRouteVolumeChanged(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |

---

### `class static MediaRouter.RouteCategory`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `CharSequence getName()` |  |
| `CharSequence getName(android.content.Context)` |  |
| `java.util.List<android.media.MediaRouter.RouteInfo> getRoutes(java.util.List<android.media.MediaRouter.RouteInfo>)` |  |
| `int getSupportedTypes()` |  |
| `boolean isGroupable()` |  |

---

### `class static MediaRouter.RouteGroup`

- **Extends:** `android.media.MediaRouter.RouteInfo`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addRoute(android.media.MediaRouter.RouteInfo)` |  |
| `void addRoute(android.media.MediaRouter.RouteInfo, int)` |  |
| `android.media.MediaRouter.RouteInfo getRouteAt(int)` |  |
| `int getRouteCount()` |  |
| `void removeRoute(android.media.MediaRouter.RouteInfo)` |  |
| `void removeRoute(int)` |  |
| `void setIconDrawable(android.graphics.drawable.Drawable)` |  |
| `void setIconResource(@DrawableRes int)` |  |

---

### `class static MediaRouter.RouteInfo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEVICE_TYPE_BLUETOOTH = 3` |  |
| `static final int DEVICE_TYPE_SPEAKER = 2` |  |
| `static final int DEVICE_TYPE_TV = 1` |  |
| `static final int DEVICE_TYPE_UNKNOWN = 0` |  |
| `static final int PLAYBACK_TYPE_LOCAL = 0` |  |
| `static final int PLAYBACK_TYPE_REMOTE = 1` |  |
| `static final int PLAYBACK_VOLUME_FIXED = 0` |  |
| `static final int PLAYBACK_VOLUME_VARIABLE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.MediaRouter.RouteCategory getCategory()` |  |
| `CharSequence getDescription()` |  |
| `int getDeviceType()` |  |
| `android.media.MediaRouter.RouteGroup getGroup()` |  |
| `android.graphics.drawable.Drawable getIconDrawable()` |  |
| `CharSequence getName()` |  |
| `CharSequence getName(android.content.Context)` |  |
| `int getPlaybackStream()` |  |
| `int getPlaybackType()` |  |
| `android.view.Display getPresentationDisplay()` |  |
| `CharSequence getStatus()` |  |
| `int getSupportedTypes()` |  |
| `Object getTag()` |  |
| `int getVolume()` |  |
| `int getVolumeHandling()` |  |
| `int getVolumeMax()` |  |
| `boolean isConnecting()` |  |
| `boolean isEnabled()` |  |
| `void requestSetVolume(int)` |  |
| `void requestUpdateVolume(int)` |  |
| `void setTag(Object)` |  |

---

### `class static MediaRouter.SimpleCallback`

- **Extends:** `android.media.MediaRouter.Callback`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter.SimpleCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onRouteAdded(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `void onRouteChanged(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `void onRouteGrouped(android.media.MediaRouter, android.media.MediaRouter.RouteInfo, android.media.MediaRouter.RouteGroup, int)` |  |
| `void onRouteRemoved(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |
| `void onRouteSelected(android.media.MediaRouter, int, android.media.MediaRouter.RouteInfo)` |  |
| `void onRouteUngrouped(android.media.MediaRouter, android.media.MediaRouter.RouteInfo, android.media.MediaRouter.RouteGroup)` |  |
| `void onRouteUnselected(android.media.MediaRouter, int, android.media.MediaRouter.RouteInfo)` |  |
| `void onRouteVolumeChanged(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` |  |

---

### `class static MediaRouter.UserRouteInfo`

- **Extends:** `android.media.MediaRouter.RouteInfo`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter.VolumeCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.RemoteControlClient getRemoteControlClient()` |  |
| `void setDescription(CharSequence)` |  |
| `void setIconDrawable(android.graphics.drawable.Drawable)` |  |
| `void setIconResource(@DrawableRes int)` |  |
| `void setName(CharSequence)` |  |
| `void setName(int)` |  |
| `void setPlaybackStream(int)` |  |
| `void setPlaybackType(int)` |  |
| `void setRemoteControlClient(android.media.RemoteControlClient)` |  |
| `void setStatus(CharSequence)` |  |
| `void setVolume(int)` |  |
| `void setVolumeCallback(android.media.MediaRouter.VolumeCallback)` |  |
| `void setVolumeHandling(int)` |  |
| `void setVolumeMax(int)` |  |
| `abstract void onVolumeSetRequest(android.media.MediaRouter.RouteInfo, int)` |  |
| `abstract void onVolumeUpdateRequest(android.media.MediaRouter.RouteInfo, int)` |  |

---

### `class final MediaRouter2`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter2.ControllerCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void registerControllerCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.ControllerCallback)` |  |
| `void registerRouteCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.RouteCallback, @NonNull android.media.RouteDiscoveryPreference)` |  |
| `void registerTransferCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.TransferCallback)` |  |
| `void setOnGetControllerHintsListener(@Nullable android.media.MediaRouter2.OnGetControllerHintsListener)` |  |
| `void stop()` |  |
| `void transferTo(@NonNull android.media.MediaRoute2Info)` |  |
| `void unregisterControllerCallback(@NonNull android.media.MediaRouter2.ControllerCallback)` |  |
| `void unregisterRouteCallback(@NonNull android.media.MediaRouter2.RouteCallback)` |  |
| `void unregisterTransferCallback(@NonNull android.media.MediaRouter2.TransferCallback)` |  |
| `void onControllerUpdated(@NonNull android.media.MediaRouter2.RoutingController)` |  |

---

### `interface static MediaRouter2.OnGetControllerHintsListener`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter2.RouteCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onRoutesAdded(@NonNull java.util.List<android.media.MediaRoute2Info>)` |  |
| `void onRoutesChanged(@NonNull java.util.List<android.media.MediaRoute2Info>)` |  |
| `void onRoutesRemoved(@NonNull java.util.List<android.media.MediaRoute2Info>)` |  |

---

### `class MediaRouter2.RoutingController`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaRouter2.TransferCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void deselectRoute(@NonNull android.media.MediaRoute2Info)` |  |
| `int getVolume()` |  |
| `int getVolumeHandling()` |  |
| `int getVolumeMax()` |  |
| `boolean isReleased()` |  |
| `void release()` |  |
| `void selectRoute(@NonNull android.media.MediaRoute2Info)` |  |
| `void setVolume(int)` |  |
| `void onStop(@NonNull android.media.MediaRouter2.RoutingController)` |  |
| `void onTransfer(@NonNull android.media.MediaRouter2.RoutingController, @NonNull android.media.MediaRouter2.RoutingController)` |  |
| `void onTransferFailure(@NonNull android.media.MediaRoute2Info)` |  |

---

### `class MediaScannerConnection`

- **Implements:** `android.content.ServiceConnection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaScannerConnection(android.content.Context, android.media.MediaScannerConnection.MediaScannerConnectionClient)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect()` |  |
| `void disconnect()` |  |
| `boolean isConnected()` |  |
| `void onServiceConnected(android.content.ComponentName, android.os.IBinder)` |  |
| `void onServiceDisconnected(android.content.ComponentName)` |  |
| `void scanFile(String, String)` |  |
| `static void scanFile(android.content.Context, String[], String[], android.media.MediaScannerConnection.OnScanCompletedListener)` |  |

---

### `interface static MediaScannerConnection.MediaScannerConnectionClient`

- **Extends:** `android.media.MediaScannerConnection.OnScanCompletedListener`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onMediaScannerConnected()` |  |

---

### `interface static MediaScannerConnection.OnScanCompletedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onScanCompleted(String, android.net.Uri)` |  |

---

### `class MediaSession2`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void broadcastSessionCommand(@NonNull android.media.Session2Command, @Nullable android.os.Bundle)` |  |
| `void cancelSessionCommand(@NonNull android.media.MediaSession2.ControllerInfo, @NonNull Object)` |  |
| `void close()` |  |
| `boolean isPlaybackActive()` |  |
| `void setPlaybackActive(boolean)` |  |

---

### `class static final MediaSession2.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession2.Builder(@NonNull android.content.Context)` |  |

---

### `class static final MediaSession2.ControllerInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession2.SessionCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getUid()` |  |
| `void onCommandResult(@NonNull android.media.MediaSession2, @NonNull android.media.MediaSession2.ControllerInfo, @NonNull Object, @NonNull android.media.Session2Command, @NonNull android.media.Session2Command.Result)` |  |
| `void onDisconnected(@NonNull android.media.MediaSession2, @NonNull android.media.MediaSession2.ControllerInfo)` |  |
| `void onPostConnect(@NonNull android.media.MediaSession2, @NonNull android.media.MediaSession2.ControllerInfo)` |  |

---

### `class abstract MediaSession2Service`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession2Service()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.media.MediaSession2Service"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void addSession(@NonNull android.media.MediaSession2)` |  |
| `final void removeSession(@NonNull android.media.MediaSession2)` |  |

---

### `class static MediaSession2Service.MediaNotification`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession2Service.MediaNotification(int, @NonNull android.app.Notification)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getNotificationId()` |  |

---

### `class final MediaSync`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSync()` |  |
| `MediaSync.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEDIASYNC_ERROR_AUDIOTRACK_FAIL = 1` |  |
| `static final int MEDIASYNC_ERROR_SURFACE_FAIL = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `void flush()` |  |
| `void queueAudio(@NonNull java.nio.ByteBuffer, int, long)` |  |
| `void release()` |  |
| `void setAudioTrack(@Nullable android.media.AudioTrack)` |  |
| `void setCallback(@Nullable android.media.MediaSync.Callback, @Nullable android.os.Handler)` |  |
| `void setOnErrorListener(@Nullable android.media.MediaSync.OnErrorListener, @Nullable android.os.Handler)` |  |
| `void setPlaybackParams(@NonNull android.media.PlaybackParams)` |  |
| `void setSurface(@Nullable android.view.Surface)` |  |
| `void setSyncParams(@NonNull android.media.SyncParams)` |  |
| `abstract void onAudioBufferConsumed(@NonNull android.media.MediaSync, @NonNull java.nio.ByteBuffer, int)` |  |

---

### `interface static MediaSync.OnErrorListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onError(@NonNull android.media.MediaSync, int, int)` |  |

---

### `class MediaSyncEvent`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SYNC_EVENT_NONE = 0` |  |
| `static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.MediaSyncEvent createEvent(int) throws java.lang.IllegalArgumentException` |  |
| `int getAudioSessionId()` |  |
| `int getType()` |  |
| `android.media.MediaSyncEvent setAudioSessionId(int) throws java.lang.IllegalArgumentException` |  |

---

### `class final MediaTimestamp`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaTimestamp(long, long, @FloatRange(from=0.0f, to=java.lang.Float.MAX_VALUE) float)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.media.MediaTimestamp TIMESTAMP_UNKNOWN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getAnchorMediaTimeUs()` |  |
| `long getAnchorSystemNanoTime()` |  |

---

### `interface MicrophoneDirection`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MIC_DIRECTION_AWAY_FROM_USER = 2` |  |
| `static final int MIC_DIRECTION_EXTERNAL = 3` |  |
| `static final int MIC_DIRECTION_TOWARDS_USER = 1` |  |
| `static final int MIC_DIRECTION_UNSPECIFIED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean setPreferredMicrophoneDirection(int)` |  |
| `boolean setPreferredMicrophoneFieldDimension(@FloatRange(from=-1.0, to=1.0) float)` |  |

---

### `class final MicrophoneInfo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CHANNEL_MAPPING_DIRECT = 1` |  |
| `static final int CHANNEL_MAPPING_PROCESSED = 2` |  |
| `static final int DIRECTIONALITY_BI_DIRECTIONAL = 2` |  |
| `static final int DIRECTIONALITY_CARDIOID = 3` |  |
| `static final int DIRECTIONALITY_HYPER_CARDIOID = 4` |  |
| `static final int DIRECTIONALITY_OMNI = 1` |  |
| `static final int DIRECTIONALITY_SUPER_CARDIOID = 5` |  |
| `static final int DIRECTIONALITY_UNKNOWN = 0` |  |
| `static final int GROUP_UNKNOWN = -1` |  |
| `static final int INDEX_IN_THE_GROUP_UNKNOWN = -1` |  |
| `static final int LOCATION_MAINBODY = 1` |  |
| `static final int LOCATION_MAINBODY_MOVABLE = 2` |  |
| `static final int LOCATION_PERIPHERAL = 3` |  |
| `static final int LOCATION_UNKNOWN = 0` |  |
| `static final android.media.MicrophoneInfo.Coordinate3F ORIENTATION_UNKNOWN` |  |
| `static final android.media.MicrophoneInfo.Coordinate3F POSITION_UNKNOWN` |  |
| `static final float SENSITIVITY_UNKNOWN = -3.4028235E38f` |  |
| `static final float SPL_UNKNOWN = -3.4028235E38f` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<android.util.Pair<java.lang.Integer,java.lang.Integer>> getChannelMapping()` |  |
| `String getDescription()` |  |
| `int getDirectionality()` |  |
| `java.util.List<android.util.Pair<java.lang.Float,java.lang.Float>> getFrequencyResponse()` |  |
| `int getGroup()` |  |
| `int getId()` |  |
| `int getIndexInTheGroup()` |  |
| `int getLocation()` |  |
| `float getMaxSpl()` |  |
| `float getMinSpl()` |  |
| `android.media.MicrophoneInfo.Coordinate3F getOrientation()` |  |
| `android.media.MicrophoneInfo.Coordinate3F getPosition()` |  |
| `float getSensitivity()` |  |
| `int getType()` |  |

---

### `class static final MicrophoneInfo.Coordinate3F`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final float x` |  |
| `final float y` |  |
| `final float z` |  |

---

### `class final NotProvisionedException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NotProvisionedException(String)` |  |

---

### `class final PlaybackParams`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PlaybackParams()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_FALLBACK_MODE_DEFAULT = 0` |  |
| `static final int AUDIO_FALLBACK_MODE_FAIL = 2` |  |
| `static final int AUDIO_FALLBACK_MODE_MUTE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.PlaybackParams allowDefaults()` |  |
| `int describeContents()` |  |
| `int getAudioFallbackMode()` |  |
| `float getPitch()` |  |
| `float getSpeed()` |  |
| `android.media.PlaybackParams setAudioFallbackMode(int)` |  |
| `android.media.PlaybackParams setPitch(float)` |  |
| `android.media.PlaybackParams setSpeed(float)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final Rating`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int RATING_3_STARS = 3` |  |
| `static final int RATING_4_STARS = 4` |  |
| `static final int RATING_5_STARS = 5` |  |
| `static final int RATING_HEART = 1` |  |
| `static final int RATING_NONE = 0` |  |
| `static final int RATING_PERCENTAGE = 6` |  |
| `static final int RATING_THUMB_UP_DOWN = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `float getPercentRating()` |  |
| `int getRatingStyle()` |  |
| `float getStarRating()` |  |
| `boolean hasHeart()` |  |
| `boolean isRated()` |  |
| `boolean isThumbUp()` |  |
| `static android.media.Rating newHeartRating(boolean)` |  |
| `static android.media.Rating newPercentageRating(float)` |  |
| `static android.media.Rating newStarRating(int, float)` |  |
| `static android.media.Rating newThumbRating(boolean)` |  |
| `static android.media.Rating newUnratedRating(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class RemoteControlClient` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class RemoteControlClient.MetadataEditor` ~~DEPRECATED~~

- **Extends:** `android.media.MediaMetadataEditor`
- **Annotations:** `@Deprecated`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |

---

### `interface static RemoteControlClient.OnGetPlaybackPositionListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static RemoteControlClient.OnMetadataUpdateListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static RemoteControlClient.OnPlaybackPositionUpdateListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final RemoteController` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class RemoteController.MetadataEditor` ~~DEPRECATED~~

- **Extends:** `android.media.MediaMetadataEditor`
- **Annotations:** `@Deprecated`

---

### `interface static RemoteController.OnClientUpdateListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final ResourceBusyException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ResourceBusyException(String)` |  |

---

### `class Ringtone`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `android.media.AudioAttributes getAudioAttributes()` |  |
| `String getTitle(android.content.Context)` |  |
| `float getVolume()` |  |
| `boolean isLooping()` |  |
| `boolean isPlaying()` |  |
| `void play()` |  |
| `void setAudioAttributes(android.media.AudioAttributes) throws java.lang.IllegalArgumentException` |  |
| `void setLooping(boolean)` |  |
| `void setVolume(float)` |  |
| `void stop()` |  |

---

### `class RingtoneManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RingtoneManager(android.app.Activity)` |  |
| `RingtoneManager(android.content.Context)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_RINGTONE_PICKER = "android.intent.action.RINGTONE_PICKER"` |  |
| `static final String EXTRA_RINGTONE_DEFAULT_URI = "android.intent.extra.ringtone.DEFAULT_URI"` |  |
| `static final String EXTRA_RINGTONE_EXISTING_URI = "android.intent.extra.ringtone.EXISTING_URI"` |  |
| `static final String EXTRA_RINGTONE_PICKED_URI = "android.intent.extra.ringtone.PICKED_URI"` |  |
| `static final String EXTRA_RINGTONE_SHOW_DEFAULT = "android.intent.extra.ringtone.SHOW_DEFAULT"` |  |
| `static final String EXTRA_RINGTONE_SHOW_SILENT = "android.intent.extra.ringtone.SHOW_SILENT"` |  |
| `static final String EXTRA_RINGTONE_TITLE = "android.intent.extra.ringtone.TITLE"` |  |
| `static final String EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE"` |  |
| `static final int ID_COLUMN_INDEX = 0` |  |
| `static final int TITLE_COLUMN_INDEX = 1` |  |
| `static final int TYPE_ALARM = 4` |  |
| `static final int TYPE_ALL = 7` |  |
| `static final int TYPE_NOTIFICATION = 2` |  |
| `static final int TYPE_RINGTONE = 1` |  |
| `static final int URI_COLUMN_INDEX = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getActualDefaultRingtoneUri(android.content.Context, int)` |  |
| `android.database.Cursor getCursor()` |  |
| `static int getDefaultType(android.net.Uri)` |  |
| `static android.net.Uri getDefaultUri(int)` |  |
| `android.media.Ringtone getRingtone(int)` |  |
| `static android.media.Ringtone getRingtone(android.content.Context, android.net.Uri)` |  |
| `int getRingtonePosition(android.net.Uri)` |  |
| `android.net.Uri getRingtoneUri(int)` |  |
| `boolean getStopPreviousRingtone()` |  |
| `static android.net.Uri getValidRingtoneUri(android.content.Context)` |  |
| `boolean hasHapticChannels(int)` |  |
| `static boolean hasHapticChannels(@NonNull android.net.Uri)` |  |
| `int inferStreamType()` |  |
| `static boolean isDefault(android.net.Uri)` |  |
| `static void setActualDefaultRingtoneUri(android.content.Context, int, android.net.Uri)` |  |
| `void setStopPreviousRingtone(boolean)` |  |
| `void setType(int)` |  |
| `void stopPreviousRingtone()` |  |

---

### `class final RouteDiscoveryPreference`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean shouldPerformActiveScan()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final RouteDiscoveryPreference.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RouteDiscoveryPreference.Builder(@NonNull java.util.List<java.lang.String>, boolean)` |  |
| `RouteDiscoveryPreference.Builder(@NonNull android.media.RouteDiscoveryPreference)` |  |

---

### `class final RoutingSessionInfo`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getVolume()` |  |
| `int getVolumeHandling()` |  |
| `int getVolumeMax()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final RoutingSessionInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RoutingSessionInfo.Builder(@NonNull String, @NonNull String)` |  |
| `RoutingSessionInfo.Builder(@NonNull android.media.RoutingSessionInfo)` |  |

---

### `class final Session2Command`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Session2Command(int)` |  |
| `Session2Command(@NonNull String, @Nullable android.os.Bundle)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int COMMAND_CODE_CUSTOM = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCommandCode()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final Session2Command.Result`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Session2Command.Result(int, @Nullable android.os.Bundle)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int RESULT_ERROR_UNKNOWN_ERROR = -1` |  |
| `static final int RESULT_INFO_SKIPPED = 1` |  |
| `static final int RESULT_SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getResultCode()` |  |

---

### `class final Session2CommandGroup`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean hasCommand(@NonNull android.media.Session2Command)` |  |
| `boolean hasCommand(int)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final Session2CommandGroup.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Session2CommandGroup.Builder()` |  |
| `Session2CommandGroup.Builder(@NonNull android.media.Session2CommandGroup)` |  |

---

### `class final Session2Token`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Session2Token(@NonNull android.content.Context, @NonNull android.content.ComponentName)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_SESSION = 0` |  |
| `static final int TYPE_SESSION_SERVICE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getType()` |  |
| `int getUid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SoundPool`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void autoPause()` |  |
| `final void autoResume()` |  |
| `void finalize()` |  |
| `int load(String, int)` |  |
| `int load(android.content.Context, int, int)` |  |
| `int load(android.content.res.AssetFileDescriptor, int)` |  |
| `int load(java.io.FileDescriptor, long, long, int)` |  |
| `final void pause(int)` |  |
| `final int play(int, float, float, int, int, float)` |  |
| `final void release()` |  |
| `final void resume(int)` |  |
| `final void setLoop(int, int)` |  |
| `void setOnLoadCompleteListener(android.media.SoundPool.OnLoadCompleteListener)` |  |
| `final void setPriority(int, int)` |  |
| `final void setRate(int, float)` |  |
| `final void setVolume(int, float, float)` |  |
| `final void stop(int)` |  |
| `final boolean unload(int)` |  |

---

### `class static SoundPool.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SoundPool.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.SoundPool build()` |  |
| `android.media.SoundPool.Builder setAudioAttributes(android.media.AudioAttributes) throws java.lang.IllegalArgumentException` |  |
| `android.media.SoundPool.Builder setMaxStreams(int) throws java.lang.IllegalArgumentException` |  |

---

### `interface static SoundPool.OnLoadCompleteListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onLoadComplete(android.media.SoundPool, int, int)` |  |

---

### `class final SubtitleData`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SubtitleData(int, long, long, @NonNull byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getDurationUs()` |  |
| `long getStartTimeUs()` |  |
| `int getTrackIndex()` |  |

---

### `class final SyncParams`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SyncParams()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_ADJUST_MODE_DEFAULT = 0` |  |
| `static final int AUDIO_ADJUST_MODE_RESAMPLE = 2` |  |
| `static final int AUDIO_ADJUST_MODE_STRETCH = 1` |  |
| `static final int SYNC_SOURCE_AUDIO = 2` |  |
| `static final int SYNC_SOURCE_DEFAULT = 0` |  |
| `static final int SYNC_SOURCE_SYSTEM_CLOCK = 1` |  |
| `static final int SYNC_SOURCE_VSYNC = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.SyncParams allowDefaults()` |  |
| `int getAudioAdjustMode()` |  |
| `float getFrameRate()` |  |
| `int getSyncSource()` |  |
| `float getTolerance()` |  |
| `android.media.SyncParams setAudioAdjustMode(int)` |  |
| `android.media.SyncParams setFrameRate(float)` |  |
| `android.media.SyncParams setSyncSource(int)` |  |
| `android.media.SyncParams setTolerance(float)` |  |

---

### `class ThumbnailUtils`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ThumbnailUtils()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int OPTIONS_RECYCLE_INPUT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.graphics.Bitmap extractThumbnail(android.graphics.Bitmap, int, int)` |  |
| `static android.graphics.Bitmap extractThumbnail(android.graphics.Bitmap, int, int, int)` |  |

---

### `class final TimedMetaData`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimedMetaData(long, @NonNull byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] getMetaData()` |  |
| `long getTimestamp()` |  |

---

### `class final TimedText`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.graphics.Rect getBounds()` |  |
| `String getText()` |  |

---

### `class ToneGenerator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ToneGenerator(int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MAX_VOLUME = 100` |  |
| `static final int MIN_VOLUME = 0` |  |
| `static final int TONE_CDMA_ABBR_ALERT = 97` |  |
| `static final int TONE_CDMA_ABBR_INTERCEPT = 37` |  |
| `static final int TONE_CDMA_ABBR_REORDER = 39` |  |
| `static final int TONE_CDMA_ALERT_AUTOREDIAL_LITE = 87` |  |
| `static final int TONE_CDMA_ALERT_CALL_GUARD = 93` |  |
| `static final int TONE_CDMA_ALERT_INCALL_LITE = 91` |  |
| `static final int TONE_CDMA_ALERT_NETWORK_LITE = 86` |  |
| `static final int TONE_CDMA_ANSWER = 42` |  |
| `static final int TONE_CDMA_CALLDROP_LITE = 95` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_INTERGROUP = 46` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL = 45` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_PAT3 = 48` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_PAT5 = 50` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_PAT6 = 51` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_PAT7 = 52` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING = 49` |  |
| `static final int TONE_CDMA_CALL_SIGNAL_ISDN_SP_PRI = 47` |  |
| `static final int TONE_CDMA_CONFIRM = 41` |  |
| `static final int TONE_CDMA_DIAL_TONE_LITE = 34` |  |
| `static final int TONE_CDMA_EMERGENCY_RINGBACK = 92` |  |
| `static final int TONE_CDMA_HIGH_L = 53` |  |
| `static final int TONE_CDMA_HIGH_PBX_L = 71` |  |
| `static final int TONE_CDMA_HIGH_PBX_SLS = 80` |  |
| `static final int TONE_CDMA_HIGH_PBX_SS = 74` |  |
| `static final int TONE_CDMA_HIGH_PBX_SSL = 77` |  |
| `static final int TONE_CDMA_HIGH_PBX_S_X4 = 83` |  |
| `static final int TONE_CDMA_HIGH_SLS = 65` |  |
| `static final int TONE_CDMA_HIGH_SS = 56` |  |
| `static final int TONE_CDMA_HIGH_SSL = 59` |  |
| `static final int TONE_CDMA_HIGH_SS_2 = 62` |  |
| `static final int TONE_CDMA_HIGH_S_X4 = 68` |  |
| `static final int TONE_CDMA_INTERCEPT = 36` |  |
| `static final int TONE_CDMA_KEYPAD_VOLUME_KEY_LITE = 89` |  |
| `static final int TONE_CDMA_LOW_L = 55` |  |
| `static final int TONE_CDMA_LOW_PBX_L = 73` |  |
| `static final int TONE_CDMA_LOW_PBX_SLS = 82` |  |
| `static final int TONE_CDMA_LOW_PBX_SS = 76` |  |
| `static final int TONE_CDMA_LOW_PBX_SSL = 79` |  |
| `static final int TONE_CDMA_LOW_PBX_S_X4 = 85` |  |
| `static final int TONE_CDMA_LOW_SLS = 67` |  |
| `static final int TONE_CDMA_LOW_SS = 58` |  |
| `static final int TONE_CDMA_LOW_SSL = 61` |  |
| `static final int TONE_CDMA_LOW_SS_2 = 64` |  |
| `static final int TONE_CDMA_LOW_S_X4 = 70` |  |
| `static final int TONE_CDMA_MED_L = 54` |  |
| `static final int TONE_CDMA_MED_PBX_L = 72` |  |
| `static final int TONE_CDMA_MED_PBX_SLS = 81` |  |
| `static final int TONE_CDMA_MED_PBX_SS = 75` |  |
| `static final int TONE_CDMA_MED_PBX_SSL = 78` |  |
| `static final int TONE_CDMA_MED_PBX_S_X4 = 84` |  |
| `static final int TONE_CDMA_MED_SLS = 66` |  |
| `static final int TONE_CDMA_MED_SS = 57` |  |
| `static final int TONE_CDMA_MED_SSL = 60` |  |
| `static final int TONE_CDMA_MED_SS_2 = 63` |  |
| `static final int TONE_CDMA_MED_S_X4 = 69` |  |
| `static final int TONE_CDMA_NETWORK_BUSY = 40` |  |
| `static final int TONE_CDMA_NETWORK_BUSY_ONE_SHOT = 96` |  |
| `static final int TONE_CDMA_NETWORK_CALLWAITING = 43` |  |
| `static final int TONE_CDMA_NETWORK_USA_RINGBACK = 35` |  |
| `static final int TONE_CDMA_ONE_MIN_BEEP = 88` |  |
| `static final int TONE_CDMA_PIP = 44` |  |
| `static final int TONE_CDMA_PRESSHOLDKEY_LITE = 90` |  |
| `static final int TONE_CDMA_REORDER = 38` |  |
| `static final int TONE_CDMA_SIGNAL_OFF = 98` |  |
| `static final int TONE_CDMA_SOFT_ERROR_LITE = 94` |  |
| `static final int TONE_DTMF_0 = 0` |  |
| `static final int TONE_DTMF_1 = 1` |  |
| `static final int TONE_DTMF_2 = 2` |  |
| `static final int TONE_DTMF_3 = 3` |  |
| `static final int TONE_DTMF_4 = 4` |  |
| `static final int TONE_DTMF_5 = 5` |  |
| `static final int TONE_DTMF_6 = 6` |  |
| `static final int TONE_DTMF_7 = 7` |  |
| `static final int TONE_DTMF_8 = 8` |  |
| `static final int TONE_DTMF_9 = 9` |  |
| `static final int TONE_DTMF_A = 12` |  |
| `static final int TONE_DTMF_B = 13` |  |
| `static final int TONE_DTMF_C = 14` |  |
| `static final int TONE_DTMF_D = 15` |  |
| `static final int TONE_DTMF_P = 11` |  |
| `static final int TONE_DTMF_S = 10` |  |
| `static final int TONE_PROP_ACK = 25` |  |
| `static final int TONE_PROP_BEEP = 24` |  |
| `static final int TONE_PROP_BEEP2 = 28` |  |
| `static final int TONE_PROP_NACK = 26` |  |
| `static final int TONE_PROP_PROMPT = 27` |  |
| `static final int TONE_SUP_BUSY = 17` |  |
| `static final int TONE_SUP_CALL_WAITING = 22` |  |
| `static final int TONE_SUP_CONFIRM = 32` |  |
| `static final int TONE_SUP_CONGESTION = 18` |  |
| `static final int TONE_SUP_CONGESTION_ABBREV = 31` |  |
| `static final int TONE_SUP_DIAL = 16` |  |
| `static final int TONE_SUP_ERROR = 21` |  |
| `static final int TONE_SUP_INTERCEPT = 29` |  |
| `static final int TONE_SUP_INTERCEPT_ABBREV = 30` |  |
| `static final int TONE_SUP_PIP = 33` |  |
| `static final int TONE_SUP_RADIO_ACK = 19` |  |
| `static final int TONE_SUP_RADIO_NOTAVAIL = 20` |  |
| `static final int TONE_SUP_RINGTONE = 23` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `final int getAudioSessionId()` |  |
| `void release()` |  |
| `boolean startTone(int)` |  |
| `boolean startTone(int, int)` |  |
| `void stopTone()` |  |

---

### `class final UnsupportedSchemeException`

- **Extends:** `android.media.MediaDrmException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnsupportedSchemeException(String)` |  |

---

### `interface VolumeAutomation`


---

### `class abstract VolumeProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VolumeProvider(int, int, int)` |  |
| `VolumeProvider(int, int, int, @Nullable String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int VOLUME_CONTROL_ABSOLUTE = 2` |  |
| `static final int VOLUME_CONTROL_FIXED = 0` |  |
| `static final int VOLUME_CONTROL_RELATIVE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int getCurrentVolume()` |  |
| `final int getMaxVolume()` |  |
| `final int getVolumeControl()` |  |
| `void onAdjustVolume(int)` |  |
| `void onSetVolumeTo(int)` |  |
| `final void setCurrentVolume(int)` |  |

---

### `class final VolumeShaper`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void apply(@NonNull android.media.VolumeShaper.Operation)` |  |
| `void close()` |  |
| `void finalize()` |  |
| `float getVolume()` |  |
| `void replace(@NonNull android.media.VolumeShaper.Configuration, @NonNull android.media.VolumeShaper.Operation, boolean)` |  |

---

### `class static final VolumeShaper.Configuration`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.media.VolumeShaper.Configuration CUBIC_RAMP` |  |
| `static final int INTERPOLATOR_TYPE_CUBIC = 2` |  |
| `static final int INTERPOLATOR_TYPE_CUBIC_MONOTONIC = 3` |  |
| `static final int INTERPOLATOR_TYPE_LINEAR = 1` |  |
| `static final int INTERPOLATOR_TYPE_STEP = 0` |  |
| `static final android.media.VolumeShaper.Configuration LINEAR_RAMP` |  |
| `static final android.media.VolumeShaper.Configuration SCURVE_RAMP` |  |
| `static final android.media.VolumeShaper.Configuration SINE_RAMP` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getDuration()` |  |
| `int getInterpolatorType()` |  |
| `static int getMaximumCurvePoints()` |  |
| `float[] getTimes()` |  |
| `float[] getVolumes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final VolumeShaper.Configuration.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VolumeShaper.Configuration.Builder()` |  |
| `VolumeShaper.Configuration.Builder(@NonNull android.media.VolumeShaper.Configuration)` |  |

---

### `class static final VolumeShaper.Operation`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.media.VolumeShaper.Operation PLAY` |  |
| `static final android.media.VolumeShaper.Operation REVERSE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.media.audiofx`

### `class AcousticEchoCanceler`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.audiofx.AcousticEchoCanceler create(int)` |  |
| `static boolean isAvailable()` |  |

---

### `class AudioEffect`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION"` |  |
| `static final String ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL = "android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL"` |  |
| `static final String ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION"` |  |
| `static final int ALREADY_EXISTS = -2` |  |
| `static final int CONTENT_TYPE_GAME = 2` |  |
| `static final int CONTENT_TYPE_MOVIE = 1` |  |
| `static final int CONTENT_TYPE_MUSIC = 0` |  |
| `static final int CONTENT_TYPE_VOICE = 3` |  |
| `static final String EFFECT_AUXILIARY = "Auxiliary"` |  |
| `static final String EFFECT_INSERT = "Insert"` |  |
| `static final String EFFECT_POST_PROCESSING = "Post Processing"` |  |
| `static final String EFFECT_PRE_PROCESSING = "Pre Processing"` |  |
| `static final java.util.UUID EFFECT_TYPE_AEC` |  |
| `static final java.util.UUID EFFECT_TYPE_AGC` |  |
| `static final java.util.UUID EFFECT_TYPE_BASS_BOOST` |  |
| `static final java.util.UUID EFFECT_TYPE_DYNAMICS_PROCESSING` |  |
| `static final java.util.UUID EFFECT_TYPE_ENV_REVERB` |  |
| `static final java.util.UUID EFFECT_TYPE_EQUALIZER` |  |
| `static final java.util.UUID EFFECT_TYPE_LOUDNESS_ENHANCER` |  |
| `static final java.util.UUID EFFECT_TYPE_NS` |  |
| `static final java.util.UUID EFFECT_TYPE_PRESET_REVERB` |  |
| `static final java.util.UUID EFFECT_TYPE_VIRTUALIZER` |  |
| `static final int ERROR = -1` |  |
| `static final int ERROR_BAD_VALUE = -4` |  |
| `static final int ERROR_DEAD_OBJECT = -7` |  |
| `static final int ERROR_INVALID_OPERATION = -5` |  |
| `static final int ERROR_NO_INIT = -3` |  |
| `static final int ERROR_NO_MEMORY = -6` |  |
| `static final String EXTRA_AUDIO_SESSION = "android.media.extra.AUDIO_SESSION"` |  |
| `static final String EXTRA_CONTENT_TYPE = "android.media.extra.CONTENT_TYPE"` |  |
| `static final String EXTRA_PACKAGE_NAME = "android.media.extra.PACKAGE_NAME"` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `android.media.audiofx.AudioEffect.Descriptor getDescriptor() throws java.lang.IllegalStateException` |  |
| `boolean getEnabled() throws java.lang.IllegalStateException` |  |
| `int getId() throws java.lang.IllegalStateException` |  |
| `boolean hasControl() throws java.lang.IllegalStateException` |  |
| `static android.media.audiofx.AudioEffect.Descriptor[] queryEffects()` |  |
| `void release()` |  |
| `void setControlStatusListener(android.media.audiofx.AudioEffect.OnControlStatusChangeListener)` |  |
| `void setEnableStatusListener(android.media.audiofx.AudioEffect.OnEnableStatusChangeListener)` |  |
| `int setEnabled(boolean) throws java.lang.IllegalStateException` |  |

---

### `class static AudioEffect.Descriptor`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AudioEffect.Descriptor()` |  |
| `AudioEffect.Descriptor(String, String, String, String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `String connectMode` |  |
| `String implementor` |  |
| `String name` |  |
| `java.util.UUID type` |  |
| `java.util.UUID uuid` |  |

---

### `interface static AudioEffect.OnControlStatusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onControlStatusChange(android.media.audiofx.AudioEffect, boolean)` |  |

---

### `interface static AudioEffect.OnEnableStatusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onEnableStatusChange(android.media.audiofx.AudioEffect, boolean)` |  |

---

### `class AutomaticGainControl`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.audiofx.AutomaticGainControl create(int)` |  |
| `static boolean isAvailable()` |  |

---

### `class BassBoost`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BassBoost(int, int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_STRENGTH = 1` |  |
| `static final int PARAM_STRENGTH_SUPPORTED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.BassBoost.Settings getProperties() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getRoundedStrength() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `boolean getStrengthSupported()` |  |
| `void setParameterListener(android.media.audiofx.BassBoost.OnParameterChangeListener)` |  |
| `void setProperties(android.media.audiofx.BassBoost.Settings) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setStrength(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `interface static BassBoost.OnParameterChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onParameterChange(android.media.audiofx.BassBoost, int, int, short)` |  |

---

### `class static BassBoost.Settings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BassBoost.Settings()` |  |
| `BassBoost.Settings(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short strength` |  |

---

### `class final DynamicsProcessing`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing(int)` |  |
| `DynamicsProcessing(int, int, @Nullable android.media.audiofx.DynamicsProcessing.Config)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int VARIANT_FAVOR_FREQUENCY_RESOLUTION = 0` |  |
| `static final int VARIANT_FAVOR_TIME_RESOLUTION = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.DynamicsProcessing.Channel getChannelByChannelIndex(int)` |  |
| `int getChannelCount()` |  |
| `android.media.audiofx.DynamicsProcessing.Config getConfig()` |  |
| `float getInputGainByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.Limiter getLimiterByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.MbcBand getMbcBandByChannelIndex(int, int)` |  |
| `android.media.audiofx.DynamicsProcessing.Mbc getMbcByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPostEqBandByChannelIndex(int, int)` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPostEqByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPreEqBandByChannelIndex(int, int)` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPreEqByChannelIndex(int)` |  |
| `void setAllChannelsTo(android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `void setChannelTo(int, android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `void setInputGainAllChannelsTo(float)` |  |
| `void setInputGainbyChannel(int, float)` |  |
| `void setLimiterAllChannelsTo(android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `void setLimiterByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `void setMbcAllChannelsTo(android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `void setMbcBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |
| `void setMbcBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |
| `void setMbcByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `void setPostEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPostEqBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPostEqBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPostEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPreEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPreEqBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPreEqBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPreEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |

---

### `class static DynamicsProcessing.BandBase`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.BandBase(boolean, float)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getCutoffFrequency()` |  |
| `boolean isEnabled()` |  |
| `void setCutoffFrequency(float)` |  |
| `void setEnabled(boolean)` |  |

---

### `class static DynamicsProcessing.BandStage`

- **Extends:** `android.media.audiofx.DynamicsProcessing.Stage`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.BandStage(boolean, boolean, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getBandCount()` |  |

---

### `class static final DynamicsProcessing.Channel`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Channel(float, boolean, int, boolean, int, boolean, int, boolean)` |  |
| `DynamicsProcessing.Channel(android.media.audiofx.DynamicsProcessing.Channel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInputGain()` |  |
| `android.media.audiofx.DynamicsProcessing.Limiter getLimiter()` |  |
| `android.media.audiofx.DynamicsProcessing.Mbc getMbc()` |  |
| `android.media.audiofx.DynamicsProcessing.MbcBand getMbcBand(int)` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPostEq()` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPostEqBand(int)` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPreEq()` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPreEqBand(int)` |  |
| `void setInputGain(float)` |  |
| `void setLimiter(android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `void setMbc(android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `void setMbcBand(int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |
| `void setPostEq(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPostEqBand(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPreEq(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPreEqBand(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |

---

### `class static final DynamicsProcessing.Config`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.DynamicsProcessing.Channel getChannelByChannelIndex(int)` |  |
| `float getInputGainByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.Limiter getLimiterByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.MbcBand getMbcBandByChannelIndex(int, int)` |  |
| `int getMbcBandCount()` |  |
| `android.media.audiofx.DynamicsProcessing.Mbc getMbcByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPostEqBandByChannelIndex(int, int)` |  |
| `int getPostEqBandCount()` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPostEqByChannelIndex(int)` |  |
| `android.media.audiofx.DynamicsProcessing.EqBand getPreEqBandByChannelIndex(int, int)` |  |
| `int getPreEqBandCount()` |  |
| `android.media.audiofx.DynamicsProcessing.Eq getPreEqByChannelIndex(int)` |  |
| `float getPreferredFrameDuration()` |  |
| `int getVariant()` |  |
| `boolean isLimiterInUse()` |  |
| `boolean isMbcInUse()` |  |
| `boolean isPostEqInUse()` |  |
| `boolean isPreEqInUse()` |  |
| `void setAllChannelsTo(android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `void setChannelTo(int, android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `void setInputGainAllChannelsTo(float)` |  |
| `void setInputGainByChannelIndex(int, float)` |  |
| `void setLimiterAllChannelsTo(android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `void setLimiterByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `void setMbcAllChannelsTo(android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `void setMbcBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |
| `void setMbcBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |
| `void setMbcByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `void setPostEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPostEqBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPostEqBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPostEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPreEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `void setPreEqBandAllChannelsTo(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPreEqBandByChannelIndex(int, int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |
| `void setPreEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |

---

### `class static final DynamicsProcessing.Config.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Config.Builder(int, int, boolean, int, boolean, int, boolean, int, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.DynamicsProcessing.Config build()` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setAllChannelsTo(android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setChannelTo(int, android.media.audiofx.DynamicsProcessing.Channel)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setInputGainAllChannelsTo(float)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setInputGainByChannelIndex(int, float)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setLimiterAllChannelsTo(android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setLimiterByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Limiter)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setMbcAllChannelsTo(android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setMbcByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Mbc)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setPostEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setPostEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setPreEqAllChannelsTo(android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setPreEqByChannelIndex(int, android.media.audiofx.DynamicsProcessing.Eq)` |  |
| `android.media.audiofx.DynamicsProcessing.Config.Builder setPreferredFrameDuration(float)` |  |

---

### `class static final DynamicsProcessing.Eq`

- **Extends:** `android.media.audiofx.DynamicsProcessing.BandStage`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Eq(boolean, boolean, int)` |  |
| `DynamicsProcessing.Eq(android.media.audiofx.DynamicsProcessing.Eq)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.DynamicsProcessing.EqBand getBand(int)` |  |
| `void setBand(int, android.media.audiofx.DynamicsProcessing.EqBand)` |  |

---

### `class static final DynamicsProcessing.EqBand`

- **Extends:** `android.media.audiofx.DynamicsProcessing.BandBase`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.EqBand(boolean, float, float)` |  |
| `DynamicsProcessing.EqBand(android.media.audiofx.DynamicsProcessing.EqBand)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getGain()` |  |
| `void setGain(float)` |  |

---

### `class static final DynamicsProcessing.Limiter`

- **Extends:** `android.media.audiofx.DynamicsProcessing.Stage`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Limiter(boolean, boolean, int, float, float, float, float, float)` |  |
| `DynamicsProcessing.Limiter(android.media.audiofx.DynamicsProcessing.Limiter)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getAttackTime()` |  |
| `int getLinkGroup()` |  |
| `float getPostGain()` |  |
| `float getRatio()` |  |
| `float getReleaseTime()` |  |
| `float getThreshold()` |  |
| `void setAttackTime(float)` |  |
| `void setLinkGroup(int)` |  |
| `void setPostGain(float)` |  |
| `void setRatio(float)` |  |
| `void setReleaseTime(float)` |  |
| `void setThreshold(float)` |  |

---

### `class static final DynamicsProcessing.Mbc`

- **Extends:** `android.media.audiofx.DynamicsProcessing.BandStage`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Mbc(boolean, boolean, int)` |  |
| `DynamicsProcessing.Mbc(android.media.audiofx.DynamicsProcessing.Mbc)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.audiofx.DynamicsProcessing.MbcBand getBand(int)` |  |
| `void setBand(int, android.media.audiofx.DynamicsProcessing.MbcBand)` |  |

---

### `class static final DynamicsProcessing.MbcBand`

- **Extends:** `android.media.audiofx.DynamicsProcessing.BandBase`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.MbcBand(boolean, float, float, float, float, float, float, float, float, float, float)` |  |
| `DynamicsProcessing.MbcBand(android.media.audiofx.DynamicsProcessing.MbcBand)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getAttackTime()` |  |
| `float getExpanderRatio()` |  |
| `float getKneeWidth()` |  |
| `float getNoiseGateThreshold()` |  |
| `float getPostGain()` |  |
| `float getPreGain()` |  |
| `float getRatio()` |  |
| `float getReleaseTime()` |  |
| `float getThreshold()` |  |
| `void setAttackTime(float)` |  |
| `void setExpanderRatio(float)` |  |
| `void setKneeWidth(float)` |  |
| `void setNoiseGateThreshold(float)` |  |
| `void setPostGain(float)` |  |
| `void setPreGain(float)` |  |
| `void setRatio(float)` |  |
| `void setReleaseTime(float)` |  |
| `void setThreshold(float)` |  |

---

### `class static DynamicsProcessing.Stage`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DynamicsProcessing.Stage(boolean, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isEnabled()` |  |
| `boolean isInUse()` |  |
| `void setEnabled(boolean)` |  |

---

### `class EnvironmentalReverb`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EnvironmentalReverb(int, int) throws java.lang.IllegalArgumentException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_DECAY_HF_RATIO = 3` |  |
| `static final int PARAM_DECAY_TIME = 2` |  |
| `static final int PARAM_DENSITY = 9` |  |
| `static final int PARAM_DIFFUSION = 8` |  |
| `static final int PARAM_REFLECTIONS_DELAY = 5` |  |
| `static final int PARAM_REFLECTIONS_LEVEL = 4` |  |
| `static final int PARAM_REVERB_DELAY = 7` |  |
| `static final int PARAM_REVERB_LEVEL = 6` |  |
| `static final int PARAM_ROOM_HF_LEVEL = 1` |  |
| `static final int PARAM_ROOM_LEVEL = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `short getDecayHFRatio() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `int getDecayTime() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getDensity() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getDiffusion() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `android.media.audiofx.EnvironmentalReverb.Settings getProperties() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `int getReflectionsDelay() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getReflectionsLevel() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `int getReverbDelay() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getReverbLevel() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getRoomHFLevel() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getRoomLevel() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setDecayHFRatio(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setDecayTime(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setDensity(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setDiffusion(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setParameterListener(android.media.audiofx.EnvironmentalReverb.OnParameterChangeListener)` |  |
| `void setProperties(android.media.audiofx.EnvironmentalReverb.Settings) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setReflectionsDelay(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setReflectionsLevel(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setReverbDelay(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setReverbLevel(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setRoomHFLevel(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setRoomLevel(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `interface static EnvironmentalReverb.OnParameterChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onParameterChange(android.media.audiofx.EnvironmentalReverb, int, int, int)` |  |

---

### `class static EnvironmentalReverb.Settings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EnvironmentalReverb.Settings()` |  |
| `EnvironmentalReverb.Settings(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short decayHFRatio` |  |
| `int decayTime` |  |
| `short density` |  |
| `short diffusion` |  |
| `int reflectionsDelay` |  |
| `short reflectionsLevel` |  |
| `int reverbDelay` |  |
| `short reverbLevel` |  |
| `short roomHFLevel` |  |
| `short roomLevel` |  |

---

### `class Equalizer`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Equalizer(int, int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_BAND_FREQ_RANGE = 4` |  |
| `static final int PARAM_BAND_LEVEL = 2` |  |
| `static final int PARAM_CENTER_FREQ = 3` |  |
| `static final int PARAM_CURRENT_PRESET = 6` |  |
| `static final int PARAM_GET_BAND = 5` |  |
| `static final int PARAM_GET_NUM_OF_PRESETS = 7` |  |
| `static final int PARAM_GET_PRESET_NAME = 8` |  |
| `static final int PARAM_LEVEL_RANGE = 1` |  |
| `static final int PARAM_NUM_BANDS = 0` |  |
| `static final int PARAM_STRING_SIZE_MAX = 32` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `short getBand(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `int[] getBandFreqRange(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getBandLevel(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short[] getBandLevelRange() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `int getCenterFreq(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getCurrentPreset() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getNumberOfBands() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getNumberOfPresets() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `String getPresetName(short)` |  |
| `android.media.audiofx.Equalizer.Settings getProperties() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setBandLevel(short, short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setParameterListener(android.media.audiofx.Equalizer.OnParameterChangeListener)` |  |
| `void setProperties(android.media.audiofx.Equalizer.Settings) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void usePreset(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `interface static Equalizer.OnParameterChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onParameterChange(android.media.audiofx.Equalizer, int, int, int, int)` |  |

---

### `class static Equalizer.Settings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Equalizer.Settings()` |  |
| `Equalizer.Settings(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short[] bandLevels` |  |
| `short curPreset` |  |
| `short numBands` |  |

---

### `class LoudnessEnhancer`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LoudnessEnhancer(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_TARGET_GAIN_MB = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getTargetGain() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setTargetGain(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `class NoiseSuppressor`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.audiofx.NoiseSuppressor create(int)` |  |
| `static boolean isAvailable()` |  |

---

### `class PresetReverb`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PresetReverb(int, int) throws java.lang.IllegalArgumentException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_PRESET = 0` |  |
| `static final short PRESET_LARGEHALL = 5` |  |
| `static final short PRESET_LARGEROOM = 3` |  |
| `static final short PRESET_MEDIUMHALL = 4` |  |
| `static final short PRESET_MEDIUMROOM = 2` |  |
| `static final short PRESET_NONE = 0` |  |
| `static final short PRESET_PLATE = 6` |  |
| `static final short PRESET_SMALLROOM = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `short getPreset() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `android.media.audiofx.PresetReverb.Settings getProperties() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setParameterListener(android.media.audiofx.PresetReverb.OnParameterChangeListener)` |  |
| `void setPreset(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setProperties(android.media.audiofx.PresetReverb.Settings) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `interface static PresetReverb.OnParameterChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onParameterChange(android.media.audiofx.PresetReverb, int, int, short)` |  |

---

### `class static PresetReverb.Settings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PresetReverb.Settings()` |  |
| `PresetReverb.Settings(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short preset` |  |

---

### `class Virtualizer`

- **Extends:** `android.media.audiofx.AudioEffect`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Virtualizer(int, int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PARAM_STRENGTH = 1` |  |
| `static final int PARAM_STRENGTH_SUPPORTED = 0` |  |
| `static final int VIRTUALIZATION_MODE_AUTO = 1` |  |
| `static final int VIRTUALIZATION_MODE_BINAURAL = 2` |  |
| `static final int VIRTUALIZATION_MODE_OFF = 0` |  |
| `static final int VIRTUALIZATION_MODE_TRANSAURAL = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean canVirtualize(int, int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `boolean forceVirtualizationMode(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `android.media.audiofx.Virtualizer.Settings getProperties() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `short getRoundedStrength() throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `boolean getSpeakerAngles(int, int, int[]) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `boolean getStrengthSupported()` |  |
| `int getVirtualizationMode() throws java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setParameterListener(android.media.audiofx.Virtualizer.OnParameterChangeListener)` |  |
| `void setProperties(android.media.audiofx.Virtualizer.Settings) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |
| `void setStrength(short) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.UnsupportedOperationException` |  |

---

### `interface static Virtualizer.OnParameterChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onParameterChange(android.media.audiofx.Virtualizer, int, int, short)` |  |

---

### `class static Virtualizer.Settings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Virtualizer.Settings()` |  |
| `Virtualizer.Settings(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short strength` |  |

---

### `class Visualizer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Visualizer(int) throws java.lang.RuntimeException, java.lang.UnsupportedOperationException` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALREADY_EXISTS = -2` |  |
| `static final int ERROR = -1` |  |
| `static final int ERROR_BAD_VALUE = -4` |  |
| `static final int ERROR_DEAD_OBJECT = -7` |  |
| `static final int ERROR_INVALID_OPERATION = -5` |  |
| `static final int ERROR_NO_INIT = -3` |  |
| `static final int ERROR_NO_MEMORY = -6` |  |
| `static final int MEASUREMENT_MODE_NONE = 0` |  |
| `static final int MEASUREMENT_MODE_PEAK_RMS = 1` |  |
| `static final int SCALING_MODE_AS_PLAYED = 1` |  |
| `static final int SCALING_MODE_NORMALIZED = 0` |  |
| `static final int STATE_ENABLED = 2` |  |
| `static final int STATE_INITIALIZED = 1` |  |
| `static final int STATE_UNINITIALIZED = 0` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `int getCaptureSize() throws java.lang.IllegalStateException` |  |
| `static int[] getCaptureSizeRange()` |  |
| `boolean getEnabled()` |  |
| `int getFft(byte[]) throws java.lang.IllegalStateException` |  |
| `static int getMaxCaptureRate()` |  |
| `int getMeasurementMode() throws java.lang.IllegalStateException` |  |
| `int getMeasurementPeakRms(android.media.audiofx.Visualizer.MeasurementPeakRms)` |  |
| `int getSamplingRate() throws java.lang.IllegalStateException` |  |
| `int getScalingMode() throws java.lang.IllegalStateException` |  |
| `int getWaveForm(byte[]) throws java.lang.IllegalStateException` |  |
| `void release()` |  |
| `int setCaptureSize(int) throws java.lang.IllegalStateException` |  |
| `int setDataCaptureListener(android.media.audiofx.Visualizer.OnDataCaptureListener, int, boolean, boolean)` |  |
| `int setEnabled(boolean) throws java.lang.IllegalStateException` |  |
| `int setMeasurementMode(int) throws java.lang.IllegalStateException` |  |
| `int setScalingMode(int) throws java.lang.IllegalStateException` |  |

---

### `class static final Visualizer.MeasurementPeakRms`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Visualizer.MeasurementPeakRms()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int mPeak` |  |
| `int mRms` |  |

---

### `interface static Visualizer.OnDataCaptureListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFftDataCapture(android.media.audiofx.Visualizer, byte[], int)` |  |
| `void onWaveFormDataCapture(android.media.audiofx.Visualizer, byte[], int)` |  |

---

## Package: `android.media.browse`

### `class final MediaBrowser`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaBrowser(android.content.Context, android.content.ComponentName, android.media.browse.MediaBrowser.ConnectionCallback, android.os.Bundle)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_PAGE = "android.media.browse.extra.PAGE"` |  |
| `static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect()` |  |
| `void disconnect()` |  |
| `void getItem(@NonNull String, @NonNull android.media.browse.MediaBrowser.ItemCallback)` |  |
| `boolean isConnected()` |  |
| `void subscribe(@NonNull String, @NonNull android.media.browse.MediaBrowser.SubscriptionCallback)` |  |
| `void subscribe(@NonNull String, @NonNull android.os.Bundle, @NonNull android.media.browse.MediaBrowser.SubscriptionCallback)` |  |
| `void unsubscribe(@NonNull String)` |  |
| `void unsubscribe(@NonNull String, @NonNull android.media.browse.MediaBrowser.SubscriptionCallback)` |  |

---

### `class static MediaBrowser.ConnectionCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaBrowser.ConnectionCallback()` |  |
| `MediaBrowser.ItemCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onConnected()` |  |
| `void onConnectionFailed()` |  |
| `void onConnectionSuspended()` |  |
| `void onError(@NonNull String)` |  |
| `void onItemLoaded(android.media.browse.MediaBrowser.MediaItem)` |  |

---

### `class static MediaBrowser.MediaItem`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaBrowser.MediaItem(@NonNull android.media.MediaDescription, int)` |  |
| `MediaBrowser.SubscriptionCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_BROWSABLE = 1` |  |
| `static final int FLAG_PLAYABLE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `boolean isBrowsable()` |  |
| `boolean isPlayable()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `void onChildrenLoaded(@NonNull String, @NonNull java.util.List<android.media.browse.MediaBrowser.MediaItem>)` |  |
| `void onChildrenLoaded(@NonNull String, @NonNull java.util.List<android.media.browse.MediaBrowser.MediaItem>, @NonNull android.os.Bundle)` |  |
| `void onError(@NonNull String)` |  |
| `void onError(@NonNull String, @NonNull android.os.Bundle)` |  |

---

## Package: `android.media.effect`

### `class abstract Effect`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Effect()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void apply(int, int, int, int)` |  |
| `abstract String getName()` |  |
| `abstract void release()` |  |
| `abstract void setParameter(String, Object)` |  |
| `void setUpdateListener(android.media.effect.EffectUpdateListener)` |  |

---

### `class EffectContext`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.media.effect.EffectContext createWithCurrentGlContext()` |  |
| `android.media.effect.EffectFactory getFactory()` |  |
| `void release()` |  |

---

### `class EffectFactory`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EFFECT_AUTOFIX = "android.media.effect.effects.AutoFixEffect"` |  |
| `static final String EFFECT_BACKDROPPER = "android.media.effect.effects.BackDropperEffect"` |  |
| `static final String EFFECT_BITMAPOVERLAY = "android.media.effect.effects.BitmapOverlayEffect"` |  |
| `static final String EFFECT_BLACKWHITE = "android.media.effect.effects.BlackWhiteEffect"` |  |
| `static final String EFFECT_BRIGHTNESS = "android.media.effect.effects.BrightnessEffect"` |  |
| `static final String EFFECT_CONTRAST = "android.media.effect.effects.ContrastEffect"` |  |
| `static final String EFFECT_CROP = "android.media.effect.effects.CropEffect"` |  |
| `static final String EFFECT_CROSSPROCESS = "android.media.effect.effects.CrossProcessEffect"` |  |
| `static final String EFFECT_DOCUMENTARY = "android.media.effect.effects.DocumentaryEffect"` |  |
| `static final String EFFECT_DUOTONE = "android.media.effect.effects.DuotoneEffect"` |  |
| `static final String EFFECT_FILLLIGHT = "android.media.effect.effects.FillLightEffect"` |  |
| `static final String EFFECT_FISHEYE = "android.media.effect.effects.FisheyeEffect"` |  |
| `static final String EFFECT_FLIP = "android.media.effect.effects.FlipEffect"` |  |
| `static final String EFFECT_GRAIN = "android.media.effect.effects.GrainEffect"` |  |
| `static final String EFFECT_GRAYSCALE = "android.media.effect.effects.GrayscaleEffect"` |  |
| `static final String EFFECT_LOMOISH = "android.media.effect.effects.LomoishEffect"` |  |
| `static final String EFFECT_NEGATIVE = "android.media.effect.effects.NegativeEffect"` |  |
| `static final String EFFECT_POSTERIZE = "android.media.effect.effects.PosterizeEffect"` |  |
| `static final String EFFECT_REDEYE = "android.media.effect.effects.RedEyeEffect"` |  |
| `static final String EFFECT_ROTATE = "android.media.effect.effects.RotateEffect"` |  |
| `static final String EFFECT_SATURATE = "android.media.effect.effects.SaturateEffect"` |  |
| `static final String EFFECT_SEPIA = "android.media.effect.effects.SepiaEffect"` |  |
| `static final String EFFECT_SHARPEN = "android.media.effect.effects.SharpenEffect"` |  |
| `static final String EFFECT_STRAIGHTEN = "android.media.effect.effects.StraightenEffect"` |  |
| `static final String EFFECT_TEMPERATURE = "android.media.effect.effects.ColorTemperatureEffect"` |  |
| `static final String EFFECT_TINT = "android.media.effect.effects.TintEffect"` |  |
| `static final String EFFECT_VIGNETTE = "android.media.effect.effects.VignetteEffect"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.effect.Effect createEffect(String)` |  |
| `static boolean isEffectSupported(String)` |  |

---

### `interface EffectUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onEffectUpdated(android.media.effect.Effect, Object)` |  |

---

## Package: `android.media.midi`

### `class final MidiDevice`

- **Implements:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `android.media.midi.MidiDevice.MidiConnection connectPorts(android.media.midi.MidiInputPort, int)` |  |
| `android.media.midi.MidiDeviceInfo getInfo()` |  |
| `android.media.midi.MidiInputPort openInputPort(int)` |  |
| `android.media.midi.MidiOutputPort openOutputPort(int)` |  |

---

### `class MidiDevice.MidiConnection`

- **Implements:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |

---

### `class final MidiDeviceInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String PROPERTY_BLUETOOTH_DEVICE = "bluetooth_device"` |  |
| `static final String PROPERTY_MANUFACTURER = "manufacturer"` |  |
| `static final String PROPERTY_NAME = "name"` |  |
| `static final String PROPERTY_PRODUCT = "product"` |  |
| `static final String PROPERTY_SERIAL_NUMBER = "serial_number"` |  |
| `static final String PROPERTY_USB_DEVICE = "usb_device"` |  |
| `static final String PROPERTY_VERSION = "version"` |  |
| `static final int TYPE_BLUETOOTH = 3` |  |
| `static final int TYPE_USB = 1` |  |
| `static final int TYPE_VIRTUAL = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getId()` |  |
| `int getInputPortCount()` |  |
| `int getOutputPortCount()` |  |
| `android.media.midi.MidiDeviceInfo.PortInfo[] getPorts()` |  |
| `android.os.Bundle getProperties()` |  |
| `int getType()` |  |
| `boolean isPrivate()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final MidiDeviceInfo.PortInfo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_INPUT = 1` |  |
| `static final int TYPE_OUTPUT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getName()` |  |
| `int getPortNumber()` |  |
| `int getType()` |  |

---

### `class abstract MidiDeviceService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MidiDeviceService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.media.midi.MidiDeviceService"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.media.midi.MidiDeviceInfo getDeviceInfo()` |  |
| `final android.media.midi.MidiReceiver[] getOutputPortReceivers()` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onClose()` |  |
| `void onDeviceStatusChanged(android.media.midi.MidiDeviceStatus)` |  |
| `abstract android.media.midi.MidiReceiver[] onGetInputPortReceivers()` |  |

---

### `class final MidiDeviceStatus`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.midi.MidiDeviceInfo getDeviceInfo()` |  |
| `int getOutputPortOpenCount(int)` |  |
| `boolean isInputPortOpen(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final MidiInputPort`

- **Extends:** `android.media.midi.MidiReceiver`
- **Implements:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `int getPortNumber()` |  |
| `void onSend(byte[], int, int, long) throws java.io.IOException` |  |

---

### `class final MidiManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.midi.MidiDeviceInfo[] getDevices()` |  |
| `void openBluetoothDevice(android.bluetooth.BluetoothDevice, android.media.midi.MidiManager.OnDeviceOpenedListener, android.os.Handler)` |  |
| `void openDevice(android.media.midi.MidiDeviceInfo, android.media.midi.MidiManager.OnDeviceOpenedListener, android.os.Handler)` |  |
| `void registerDeviceCallback(android.media.midi.MidiManager.DeviceCallback, android.os.Handler)` |  |
| `void unregisterDeviceCallback(android.media.midi.MidiManager.DeviceCallback)` |  |

---

### `class static MidiManager.DeviceCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MidiManager.DeviceCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDeviceAdded(android.media.midi.MidiDeviceInfo)` |  |
| `void onDeviceRemoved(android.media.midi.MidiDeviceInfo)` |  |
| `void onDeviceStatusChanged(android.media.midi.MidiDeviceStatus)` |  |

---

### `interface static MidiManager.OnDeviceOpenedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDeviceOpened(android.media.midi.MidiDevice)` |  |

---

### `class final MidiOutputPort`

- **Extends:** `android.media.midi.MidiSender`
- **Implements:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `int getPortNumber()` |  |
| `void onConnect(android.media.midi.MidiReceiver)` |  |
| `void onDisconnect(android.media.midi.MidiReceiver)` |  |

---

### `class abstract MidiReceiver`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MidiReceiver()` |  |
| `MidiReceiver(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void flush() throws java.io.IOException` |  |
| `final int getMaxMessageSize()` |  |
| `void onFlush() throws java.io.IOException` |  |
| `abstract void onSend(byte[], int, int, long) throws java.io.IOException` |  |
| `void send(byte[], int, int) throws java.io.IOException` |  |
| `void send(byte[], int, int, long) throws java.io.IOException` |  |

---

### `class abstract MidiSender`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MidiSender()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect(android.media.midi.MidiReceiver)` |  |
| `void disconnect(android.media.midi.MidiReceiver)` |  |
| `abstract void onConnect(android.media.midi.MidiReceiver)` |  |
| `abstract void onDisconnect(android.media.midi.MidiReceiver)` |  |

---

## Package: `android.media.projection`

### `class final MediaProjection`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaProjection.Callback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.hardware.display.VirtualDisplay createVirtualDisplay(@NonNull String, int, int, int, int, @Nullable android.view.Surface, @Nullable android.hardware.display.VirtualDisplay.Callback, @Nullable android.os.Handler)` |  |
| `void registerCallback(android.media.projection.MediaProjection.Callback, android.os.Handler)` |  |
| `void stop()` |  |
| `void unregisterCallback(android.media.projection.MediaProjection.Callback)` |  |
| `void onStop()` |  |

---

### `class final MediaProjectionManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.content.Intent createScreenCaptureIntent()` |  |
| `android.media.projection.MediaProjection getMediaProjection(int, @NonNull android.content.Intent)` |  |

---

## Package: `android.media.session`

### `class final MediaController`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaController(@NonNull android.content.Context, @NonNull android.media.session.MediaSession.Token)` |  |
| `MediaController.Callback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void adjustVolume(int, int)` |  |
| `boolean dispatchMediaButtonEvent(@NonNull android.view.KeyEvent)` |  |
| `long getFlags()` |  |
| `String getPackageName()` |  |
| `int getRatingType()` |  |
| `void registerCallback(@NonNull android.media.session.MediaController.Callback)` |  |
| `void registerCallback(@NonNull android.media.session.MediaController.Callback, @Nullable android.os.Handler)` |  |
| `void sendCommand(@NonNull String, @Nullable android.os.Bundle, @Nullable android.os.ResultReceiver)` |  |
| `void setVolumeTo(int, int)` |  |
| `void unregisterCallback(@NonNull android.media.session.MediaController.Callback)` |  |
| `void onAudioInfoChanged(android.media.session.MediaController.PlaybackInfo)` |  |
| `void onExtrasChanged(@Nullable android.os.Bundle)` |  |
| `void onMetadataChanged(@Nullable android.media.MediaMetadata)` |  |
| `void onPlaybackStateChanged(@Nullable android.media.session.PlaybackState)` |  |
| `void onQueueChanged(@Nullable java.util.List<android.media.session.MediaSession.QueueItem>)` |  |
| `void onQueueTitleChanged(@Nullable CharSequence)` |  |
| `void onSessionDestroyed()` |  |
| `void onSessionEvent(@NonNull String, @Nullable android.os.Bundle)` |  |

---

### `class static final MediaController.PlaybackInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PLAYBACK_TYPE_LOCAL = 1` |  |
| `static final int PLAYBACK_TYPE_REMOTE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.AudioAttributes getAudioAttributes()` |  |
| `int getCurrentVolume()` |  |
| `int getMaxVolume()` |  |
| `int getPlaybackType()` |  |
| `int getVolumeControl()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final MediaController.TransportControls`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void fastForward()` |  |
| `void pause()` |  |
| `void play()` |  |
| `void playFromMediaId(String, android.os.Bundle)` |  |
| `void playFromSearch(String, android.os.Bundle)` |  |
| `void playFromUri(android.net.Uri, android.os.Bundle)` |  |
| `void prepare()` |  |
| `void prepareFromMediaId(String, android.os.Bundle)` |  |
| `void prepareFromSearch(String, android.os.Bundle)` |  |
| `void prepareFromUri(android.net.Uri, android.os.Bundle)` |  |
| `void rewind()` |  |
| `void seekTo(long)` |  |
| `void sendCustomAction(@NonNull android.media.session.PlaybackState.CustomAction, @Nullable android.os.Bundle)` |  |
| `void sendCustomAction(@NonNull String, @Nullable android.os.Bundle)` |  |
| `void setPlaybackSpeed(float)` |  |
| `void setRating(android.media.Rating)` |  |
| `void skipToNext()` |  |
| `void skipToPrevious()` |  |
| `void skipToQueueItem(long)` |  |
| `void stop()` |  |

---

### `class final MediaSession`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession(@NonNull android.content.Context, @NonNull String)` |  |
| `MediaSession(@NonNull android.content.Context, @NonNull String, @Nullable android.os.Bundle)` |  |
| `MediaSession.Callback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isActive()` |  |
| `void release()` |  |
| `void sendSessionEvent(@NonNull String, @Nullable android.os.Bundle)` |  |
| `void setActive(boolean)` |  |
| `void setCallback(@Nullable android.media.session.MediaSession.Callback)` |  |
| `void setCallback(@Nullable android.media.session.MediaSession.Callback, @Nullable android.os.Handler)` |  |
| `void setExtras(@Nullable android.os.Bundle)` |  |
| `void setFlags(int)` |  |
| `void setMediaButtonReceiver(@Nullable android.app.PendingIntent)` |  |
| `void setMetadata(@Nullable android.media.MediaMetadata)` |  |
| `void setPlaybackState(@Nullable android.media.session.PlaybackState)` |  |
| `void setPlaybackToLocal(android.media.AudioAttributes)` |  |
| `void setPlaybackToRemote(@NonNull android.media.VolumeProvider)` |  |
| `void setQueue(@Nullable java.util.List<android.media.session.MediaSession.QueueItem>)` |  |
| `void setQueueTitle(@Nullable CharSequence)` |  |
| `void setRatingType(int)` |  |
| `void setSessionActivity(@Nullable android.app.PendingIntent)` |  |
| `void onCommand(@NonNull String, @Nullable android.os.Bundle, @Nullable android.os.ResultReceiver)` |  |
| `void onCustomAction(@NonNull String, @Nullable android.os.Bundle)` |  |
| `void onFastForward()` |  |
| `boolean onMediaButtonEvent(@NonNull android.content.Intent)` |  |
| `void onPause()` |  |
| `void onPlay()` |  |
| `void onPlayFromMediaId(String, android.os.Bundle)` |  |
| `void onPlayFromSearch(String, android.os.Bundle)` |  |
| `void onPlayFromUri(android.net.Uri, android.os.Bundle)` |  |
| `void onPrepare()` |  |
| `void onPrepareFromMediaId(String, android.os.Bundle)` |  |
| `void onPrepareFromSearch(String, android.os.Bundle)` |  |
| `void onPrepareFromUri(android.net.Uri, android.os.Bundle)` |  |
| `void onRewind()` |  |
| `void onSeekTo(long)` |  |
| `void onSetPlaybackSpeed(float)` |  |
| `void onSetRating(@NonNull android.media.Rating)` |  |
| `void onSkipToNext()` |  |
| `void onSkipToPrevious()` |  |
| `void onSkipToQueueItem(long)` |  |
| `void onStop()` |  |

---

### `class static final MediaSession.QueueItem`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSession.QueueItem(android.media.MediaDescription, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int UNKNOWN_ID = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.media.MediaDescription getDescription()` |  |
| `long getQueueId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final MediaSession.Token`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final MediaSessionManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnActiveSessionsChangedListener(@NonNull android.media.session.MediaSessionManager.OnActiveSessionsChangedListener, @Nullable android.content.ComponentName)` |  |
| `void addOnActiveSessionsChangedListener(@NonNull android.media.session.MediaSessionManager.OnActiveSessionsChangedListener, @Nullable android.content.ComponentName, @Nullable android.os.Handler)` |  |
| `void addOnSession2TokensChangedListener(@NonNull android.media.session.MediaSessionManager.OnSession2TokensChangedListener)` |  |
| `void addOnSession2TokensChangedListener(@NonNull android.media.session.MediaSessionManager.OnSession2TokensChangedListener, @NonNull android.os.Handler)` |  |
| `boolean isTrustedForMediaControl(@NonNull android.media.session.MediaSessionManager.RemoteUserInfo)` |  |
| `void notifySession2Created(@NonNull android.media.Session2Token)` |  |
| `void removeOnActiveSessionsChangedListener(@NonNull android.media.session.MediaSessionManager.OnActiveSessionsChangedListener)` |  |
| `void removeOnSession2TokensChangedListener(@NonNull android.media.session.MediaSessionManager.OnSession2TokensChangedListener)` |  |

---

### `interface static MediaSessionManager.OnActiveSessionsChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onActiveSessionsChanged(@Nullable java.util.List<android.media.session.MediaController>)` |  |

---

### `interface static MediaSessionManager.OnSession2TokensChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSession2TokensChanged(@NonNull java.util.List<android.media.Session2Token>)` |  |

---

### `class static final MediaSessionManager.RemoteUserInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MediaSessionManager.RemoteUserInfo(@NonNull String, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getPackageName()` |  |
| `int getPid()` |  |
| `int getUid()` |  |

---

### `class final PlaybackState`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long ACTION_FAST_FORWARD = 64L` |  |
| `static final long ACTION_PAUSE = 2L` |  |
| `static final long ACTION_PLAY = 4L` |  |
| `static final long ACTION_PLAY_FROM_MEDIA_ID = 1024L` |  |
| `static final long ACTION_PLAY_FROM_SEARCH = 2048L` |  |
| `static final long ACTION_PLAY_FROM_URI = 8192L` |  |
| `static final long ACTION_PLAY_PAUSE = 512L` |  |
| `static final long ACTION_PREPARE = 16384L` |  |
| `static final long ACTION_PREPARE_FROM_MEDIA_ID = 32768L` |  |
| `static final long ACTION_PREPARE_FROM_SEARCH = 65536L` |  |
| `static final long ACTION_PREPARE_FROM_URI = 131072L` |  |
| `static final long ACTION_REWIND = 8L` |  |
| `static final long ACTION_SEEK_TO = 256L` |  |
| `static final long ACTION_SET_RATING = 128L` |  |
| `static final long ACTION_SKIP_TO_NEXT = 32L` |  |
| `static final long ACTION_SKIP_TO_PREVIOUS = 16L` |  |
| `static final long ACTION_SKIP_TO_QUEUE_ITEM = 4096L` |  |
| `static final long ACTION_STOP = 1L` |  |
| `static final long PLAYBACK_POSITION_UNKNOWN = -1L` |  |
| `static final int STATE_BUFFERING = 6` |  |
| `static final int STATE_CONNECTING = 8` |  |
| `static final int STATE_ERROR = 7` |  |
| `static final int STATE_FAST_FORWARDING = 4` |  |
| `static final int STATE_NONE = 0` |  |
| `static final int STATE_PAUSED = 2` |  |
| `static final int STATE_PLAYING = 3` |  |
| `static final int STATE_REWINDING = 5` |  |
| `static final int STATE_SKIPPING_TO_NEXT = 10` |  |
| `static final int STATE_SKIPPING_TO_PREVIOUS = 9` |  |
| `static final int STATE_SKIPPING_TO_QUEUE_ITEM = 11` |  |
| `static final int STATE_STOPPED = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getActions()` |  |
| `long getActiveQueueItemId()` |  |
| `long getBufferedPosition()` |  |
| `java.util.List<android.media.session.PlaybackState.CustomAction> getCustomActions()` |  |
| `CharSequence getErrorMessage()` |  |
| `long getLastPositionUpdateTime()` |  |
| `float getPlaybackSpeed()` |  |
| `long getPosition()` |  |
| `int getState()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PlaybackState.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PlaybackState.Builder()` |  |
| `PlaybackState.Builder(android.media.session.PlaybackState)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.session.PlaybackState.Builder addCustomAction(String, String, int)` |  |
| `android.media.session.PlaybackState.Builder addCustomAction(android.media.session.PlaybackState.CustomAction)` |  |
| `android.media.session.PlaybackState build()` |  |
| `android.media.session.PlaybackState.Builder setActions(long)` |  |
| `android.media.session.PlaybackState.Builder setActiveQueueItemId(long)` |  |
| `android.media.session.PlaybackState.Builder setBufferedPosition(long)` |  |
| `android.media.session.PlaybackState.Builder setErrorMessage(CharSequence)` |  |
| `android.media.session.PlaybackState.Builder setExtras(android.os.Bundle)` |  |
| `android.media.session.PlaybackState.Builder setState(int, long, float, long)` |  |
| `android.media.session.PlaybackState.Builder setState(int, long, float)` |  |

---

### `class static final PlaybackState.CustomAction`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getAction()` |  |
| `android.os.Bundle getExtras()` |  |
| `int getIcon()` |  |
| `CharSequence getName()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PlaybackState.CustomAction.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PlaybackState.CustomAction.Builder(String, CharSequence, @DrawableRes int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.session.PlaybackState.CustomAction build()` |  |
| `android.media.session.PlaybackState.CustomAction.Builder setExtras(android.os.Bundle)` |  |

---

## Package: `android.media.tv`

### `class final TvContentRating`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.media.tv.TvContentRating UNRATED` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean contains(@NonNull android.media.tv.TvContentRating)` |  |
| `static android.media.tv.TvContentRating createRating(String, String, String, java.lang.String...)` |  |
| `String flattenToString()` |  |
| `String getDomain()` |  |
| `String getMainRating()` |  |
| `String getRatingSystem()` |  |
| `java.util.List<java.lang.String> getSubRatings()` |  |
| `static android.media.tv.TvContentRating unflattenFromString(String)` |  |

---

### `class final TvContract`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_INITIALIZE_PROGRAMS = "android.media.tv.action.INITIALIZE_PROGRAMS"` |  |
| `static final String ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT = "android.media.tv.action.PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT"` |  |
| `static final String ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED = "android.media.tv.action.PREVIEW_PROGRAM_BROWSABLE_DISABLED"` |  |
| `static final String ACTION_REQUEST_CHANNEL_BROWSABLE = "android.media.tv.action.REQUEST_CHANNEL_BROWSABLE"` |  |
| `static final String ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED = "android.media.tv.action.WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED"` |  |
| `static final String AUTHORITY = "android.media.tv"` |  |
| `static final String EXTRA_CHANNEL_ID = "android.media.tv.extra.CHANNEL_ID"` |  |
| `static final String EXTRA_PREVIEW_PROGRAM_ID = "android.media.tv.extra.PREVIEW_PROGRAM_ID"` |  |
| `static final String EXTRA_WATCH_NEXT_PROGRAM_ID = "android.media.tv.extra.WATCH_NEXT_PROGRAM_ID"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.net.Uri buildChannelLogoUri(long)` |  |
| `static android.net.Uri buildChannelLogoUri(android.net.Uri)` |  |
| `static android.net.Uri buildChannelUri(long)` |  |
| `static android.net.Uri buildChannelUriForPassthroughInput(String)` |  |
| `static android.net.Uri buildChannelsUriForInput(@Nullable String)` |  |
| `static String buildInputId(android.content.ComponentName)` |  |
| `static android.net.Uri buildPreviewProgramUri(long)` |  |
| `static android.net.Uri buildPreviewProgramsUriForChannel(long)` |  |
| `static android.net.Uri buildPreviewProgramsUriForChannel(android.net.Uri)` |  |
| `static android.net.Uri buildProgramUri(long)` |  |
| `static android.net.Uri buildProgramsUriForChannel(long)` |  |
| `static android.net.Uri buildProgramsUriForChannel(android.net.Uri)` |  |
| `static android.net.Uri buildProgramsUriForChannel(long, long, long)` |  |
| `static android.net.Uri buildProgramsUriForChannel(android.net.Uri, long, long)` |  |
| `static android.net.Uri buildRecordedProgramUri(long)` |  |
| `static android.net.Uri buildWatchNextProgramUri(long)` |  |
| `static boolean isChannelUri(@NonNull android.net.Uri)` |  |
| `static boolean isChannelUriForPassthroughInput(@NonNull android.net.Uri)` |  |
| `static boolean isChannelUriForTunerInput(@NonNull android.net.Uri)` |  |
| `static boolean isProgramUri(@NonNull android.net.Uri)` |  |
| `static boolean isRecordedProgramUri(@NonNull android.net.Uri)` |  |
| `static void requestChannelBrowsable(android.content.Context, long)` |  |

---

### `interface static TvContract.BaseTvColumns`

- **Extends:** `android.provider.BaseColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_PACKAGE_NAME = "package_name"` |  |

---

### `class static final TvContract.Channels`

- **Implements:** `android.media.tv.TvContract.BaseTvColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_APP_LINK_COLOR = "app_link_color"` |  |
| `static final String COLUMN_APP_LINK_ICON_URI = "app_link_icon_uri"` |  |
| `static final String COLUMN_APP_LINK_INTENT_URI = "app_link_intent_uri"` |  |
| `static final String COLUMN_APP_LINK_POSTER_ART_URI = "app_link_poster_art_uri"` |  |
| `static final String COLUMN_APP_LINK_TEXT = "app_link_text"` |  |
| `static final String COLUMN_BROWSABLE = "browsable"` |  |
| `static final String COLUMN_DESCRIPTION = "description"` |  |
| `static final String COLUMN_DISPLAY_NAME = "display_name"` |  |
| `static final String COLUMN_DISPLAY_NUMBER = "display_number"` |  |
| `static final String COLUMN_GLOBAL_CONTENT_ID = "global_content_id"` |  |
| `static final String COLUMN_INPUT_ID = "input_id"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_ID = "internal_provider_id"` |  |
| `static final String COLUMN_LOCKED = "locked"` |  |
| `static final String COLUMN_NETWORK_AFFILIATION = "network_affiliation"` |  |
| `static final String COLUMN_ORIGINAL_NETWORK_ID = "original_network_id"` |  |
| `static final String COLUMN_SEARCHABLE = "searchable"` |  |
| `static final String COLUMN_SERVICE_ID = "service_id"` |  |
| `static final String COLUMN_SERVICE_TYPE = "service_type"` |  |
| `static final String COLUMN_TRANSIENT = "transient"` |  |
| `static final String COLUMN_TRANSPORT_STREAM_ID = "transport_stream_id"` |  |
| `static final String COLUMN_TYPE = "type"` |  |
| `static final String COLUMN_VERSION_NUMBER = "version_number"` |  |
| `static final String COLUMN_VIDEO_FORMAT = "video_format"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/channel"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/channel"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String SERVICE_TYPE_AUDIO = "SERVICE_TYPE_AUDIO"` |  |
| `static final String SERVICE_TYPE_AUDIO_VIDEO = "SERVICE_TYPE_AUDIO_VIDEO"` |  |
| `static final String SERVICE_TYPE_OTHER = "SERVICE_TYPE_OTHER"` |  |
| `static final String TYPE_1SEG = "TYPE_1SEG"` |  |
| `static final String TYPE_ATSC3_T = "TYPE_ATSC3_T"` |  |
| `static final String TYPE_ATSC_C = "TYPE_ATSC_C"` |  |
| `static final String TYPE_ATSC_M_H = "TYPE_ATSC_M_H"` |  |
| `static final String TYPE_ATSC_T = "TYPE_ATSC_T"` |  |
| `static final String TYPE_CMMB = "TYPE_CMMB"` |  |
| `static final String TYPE_DTMB = "TYPE_DTMB"` |  |
| `static final String TYPE_DVB_C = "TYPE_DVB_C"` |  |
| `static final String TYPE_DVB_C2 = "TYPE_DVB_C2"` |  |
| `static final String TYPE_DVB_H = "TYPE_DVB_H"` |  |
| `static final String TYPE_DVB_S = "TYPE_DVB_S"` |  |
| `static final String TYPE_DVB_S2 = "TYPE_DVB_S2"` |  |
| `static final String TYPE_DVB_SH = "TYPE_DVB_SH"` |  |
| `static final String TYPE_DVB_T = "TYPE_DVB_T"` |  |
| `static final String TYPE_DVB_T2 = "TYPE_DVB_T2"` |  |
| `static final String TYPE_ISDB_C = "TYPE_ISDB_C"` |  |
| `static final String TYPE_ISDB_S = "TYPE_ISDB_S"` |  |
| `static final String TYPE_ISDB_S3 = "TYPE_ISDB_S3"` |  |
| `static final String TYPE_ISDB_T = "TYPE_ISDB_T"` |  |
| `static final String TYPE_ISDB_TB = "TYPE_ISDB_TB"` |  |
| `static final String TYPE_NTSC = "TYPE_NTSC"` |  |
| `static final String TYPE_OTHER = "TYPE_OTHER"` |  |
| `static final String TYPE_PAL = "TYPE_PAL"` |  |
| `static final String TYPE_PREVIEW = "TYPE_PREVIEW"` |  |
| `static final String TYPE_SECAM = "TYPE_SECAM"` |  |
| `static final String TYPE_S_DMB = "TYPE_S_DMB"` |  |
| `static final String TYPE_T_DMB = "TYPE_T_DMB"` |  |
| `static final String VIDEO_FORMAT_1080I = "VIDEO_FORMAT_1080I"` |  |
| `static final String VIDEO_FORMAT_1080P = "VIDEO_FORMAT_1080P"` |  |
| `static final String VIDEO_FORMAT_2160P = "VIDEO_FORMAT_2160P"` |  |
| `static final String VIDEO_FORMAT_240P = "VIDEO_FORMAT_240P"` |  |
| `static final String VIDEO_FORMAT_360P = "VIDEO_FORMAT_360P"` |  |
| `static final String VIDEO_FORMAT_4320P = "VIDEO_FORMAT_4320P"` |  |
| `static final String VIDEO_FORMAT_480I = "VIDEO_FORMAT_480I"` |  |
| `static final String VIDEO_FORMAT_480P = "VIDEO_FORMAT_480P"` |  |
| `static final String VIDEO_FORMAT_576I = "VIDEO_FORMAT_576I"` |  |
| `static final String VIDEO_FORMAT_576P = "VIDEO_FORMAT_576P"` |  |
| `static final String VIDEO_FORMAT_720P = "VIDEO_FORMAT_720P"` |  |
| `static final String VIDEO_RESOLUTION_ED = "VIDEO_RESOLUTION_ED"` |  |
| `static final String VIDEO_RESOLUTION_FHD = "VIDEO_RESOLUTION_FHD"` |  |
| `static final String VIDEO_RESOLUTION_HD = "VIDEO_RESOLUTION_HD"` |  |
| `static final String VIDEO_RESOLUTION_SD = "VIDEO_RESOLUTION_SD"` |  |
| `static final String VIDEO_RESOLUTION_UHD = "VIDEO_RESOLUTION_UHD"` |  |

---

### `class static final TvContract.Channels.Logo`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "logo"` |  |

---

### `class static final TvContract.PreviewPrograms`

- **Implements:** `android.media.tv.TvContract.BaseTvColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ASPECT_RATIO_16_9 = 0` |  |
| `static final int ASPECT_RATIO_1_1 = 3` |  |
| `static final int ASPECT_RATIO_2_3 = 4` |  |
| `static final int ASPECT_RATIO_3_2 = 1` |  |
| `static final int ASPECT_RATIO_4_3 = 2` |  |
| `static final int AVAILABILITY_AVAILABLE = 0` |  |
| `static final int AVAILABILITY_FREE_WITH_SUBSCRIPTION = 1` |  |
| `static final int AVAILABILITY_PAID_CONTENT = 2` |  |
| `static final String COLUMN_AUDIO_LANGUAGE = "audio_language"` |  |
| `static final String COLUMN_AUTHOR = "author"` |  |
| `static final String COLUMN_AVAILABILITY = "availability"` |  |
| `static final String COLUMN_BROWSABLE = "browsable"` |  |
| `static final String COLUMN_CANONICAL_GENRE = "canonical_genre"` |  |
| `static final String COLUMN_CHANNEL_ID = "channel_id"` |  |
| `static final String COLUMN_CONTENT_ID = "content_id"` |  |
| `static final String COLUMN_CONTENT_RATING = "content_rating"` |  |
| `static final String COLUMN_DURATION_MILLIS = "duration_millis"` |  |
| `static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number"` |  |
| `static final String COLUMN_EPISODE_TITLE = "episode_title"` |  |
| `static final String COLUMN_INTENT_URI = "intent_uri"` |  |
| `static final String COLUMN_INTERACTION_COUNT = "interaction_count"` |  |
| `static final String COLUMN_INTERACTION_TYPE = "interaction_type"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_ID = "internal_provider_id"` |  |
| `static final String COLUMN_ITEM_COUNT = "item_count"` |  |
| `static final String COLUMN_LAST_PLAYBACK_POSITION_MILLIS = "last_playback_position_millis"` |  |
| `static final String COLUMN_LIVE = "live"` |  |
| `static final String COLUMN_LOGO_URI = "logo_uri"` |  |
| `static final String COLUMN_LONG_DESCRIPTION = "long_description"` |  |
| `static final String COLUMN_OFFER_PRICE = "offer_price"` |  |
| `static final String COLUMN_POSTER_ART_ASPECT_RATIO = "poster_art_aspect_ratio"` |  |
| `static final String COLUMN_POSTER_ART_URI = "poster_art_uri"` |  |
| `static final String COLUMN_PREVIEW_VIDEO_URI = "preview_video_uri"` |  |
| `static final String COLUMN_RELEASE_DATE = "release_date"` |  |
| `static final String COLUMN_REVIEW_RATING = "review_rating"` |  |
| `static final String COLUMN_REVIEW_RATING_STYLE = "review_rating_style"` |  |
| `static final String COLUMN_SEARCHABLE = "searchable"` |  |
| `static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number"` |  |
| `static final String COLUMN_SEASON_TITLE = "season_title"` |  |
| `static final String COLUMN_SERIES_ID = "series_id"` |  |
| `static final String COLUMN_SHORT_DESCRIPTION = "short_description"` |  |
| `static final String COLUMN_SPLIT_ID = "split_id"` |  |
| `static final String COLUMN_STARTING_PRICE = "starting_price"` |  |
| `static final String COLUMN_THUMBNAIL_ASPECT_RATIO = "poster_thumbnail_aspect_ratio"` |  |
| `static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final String COLUMN_TRANSIENT = "transient"` |  |
| `static final String COLUMN_TYPE = "type"` |  |
| `static final String COLUMN_VERSION_NUMBER = "version_number"` |  |
| `static final String COLUMN_VIDEO_HEIGHT = "video_height"` |  |
| `static final String COLUMN_VIDEO_WIDTH = "video_width"` |  |
| `static final String COLUMN_WEIGHT = "weight"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/preview_program"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/preview_program"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final int INTERACTION_TYPE_FANS = 3` |  |
| `static final int INTERACTION_TYPE_FOLLOWERS = 2` |  |
| `static final int INTERACTION_TYPE_LIKES = 4` |  |
| `static final int INTERACTION_TYPE_LISTENS = 1` |  |
| `static final int INTERACTION_TYPE_THUMBS = 5` |  |
| `static final int INTERACTION_TYPE_VIEWERS = 6` |  |
| `static final int INTERACTION_TYPE_VIEWS = 0` |  |
| `static final int REVIEW_RATING_STYLE_PERCENTAGE = 2` |  |
| `static final int REVIEW_RATING_STYLE_STARS = 0` |  |
| `static final int REVIEW_RATING_STYLE_THUMBS_UP_DOWN = 1` |  |
| `static final int TYPE_ALBUM = 8` |  |
| `static final int TYPE_ARTIST = 9` |  |
| `static final int TYPE_CHANNEL = 6` |  |
| `static final int TYPE_CLIP = 4` |  |
| `static final int TYPE_EVENT = 5` |  |
| `static final int TYPE_MOVIE = 0` |  |
| `static final int TYPE_PLAYLIST = 10` |  |
| `static final int TYPE_STATION = 11` |  |
| `static final int TYPE_TRACK = 7` |  |
| `static final int TYPE_TV_EPISODE = 3` |  |
| `static final int TYPE_TV_SEASON = 2` |  |
| `static final int TYPE_TV_SERIES = 1` |  |

---

### `class static final TvContract.Programs`

- **Implements:** `android.media.tv.TvContract.BaseTvColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_AUDIO_LANGUAGE = "audio_language"` |  |
| `static final String COLUMN_BROADCAST_GENRE = "broadcast_genre"` |  |
| `static final String COLUMN_CANONICAL_GENRE = "canonical_genre"` |  |
| `static final String COLUMN_CHANNEL_ID = "channel_id"` |  |
| `static final String COLUMN_CONTENT_RATING = "content_rating"` |  |
| `static final String COLUMN_END_TIME_UTC_MILLIS = "end_time_utc_millis"` |  |
| `static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number"` |  |
| `static final String COLUMN_EPISODE_TITLE = "episode_title"` |  |
| `static final String COLUMN_EVENT_ID = "event_id"` |  |
| `static final String COLUMN_GLOBAL_CONTENT_ID = "global_content_id"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4"` |  |
| `static final String COLUMN_LONG_DESCRIPTION = "long_description"` |  |
| `static final String COLUMN_POSTER_ART_URI = "poster_art_uri"` |  |
| `static final String COLUMN_RECORDING_PROHIBITED = "recording_prohibited"` |  |
| `static final String COLUMN_REVIEW_RATING = "review_rating"` |  |
| `static final String COLUMN_REVIEW_RATING_STYLE = "review_rating_style"` |  |
| `static final String COLUMN_SEARCHABLE = "searchable"` |  |
| `static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number"` |  |
| `static final String COLUMN_SEASON_TITLE = "season_title"` |  |
| `static final String COLUMN_SERIES_ID = "series_id"` |  |
| `static final String COLUMN_SHORT_DESCRIPTION = "short_description"` |  |
| `static final String COLUMN_SPLIT_ID = "split_id"` |  |
| `static final String COLUMN_START_TIME_UTC_MILLIS = "start_time_utc_millis"` |  |
| `static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final String COLUMN_VERSION_NUMBER = "version_number"` |  |
| `static final String COLUMN_VIDEO_HEIGHT = "video_height"` |  |
| `static final String COLUMN_VIDEO_WIDTH = "video_width"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/program"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/program"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final int REVIEW_RATING_STYLE_PERCENTAGE = 2` |  |
| `static final int REVIEW_RATING_STYLE_STARS = 0` |  |
| `static final int REVIEW_RATING_STYLE_THUMBS_UP_DOWN = 1` |  |

---

### `class static final TvContract.Programs.Genres`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ANIMAL_WILDLIFE = "ANIMAL_WILDLIFE"` |  |
| `static final String ARTS = "ARTS"` |  |
| `static final String COMEDY = "COMEDY"` |  |
| `static final String DRAMA = "DRAMA"` |  |
| `static final String EDUCATION = "EDUCATION"` |  |
| `static final String ENTERTAINMENT = "ENTERTAINMENT"` |  |
| `static final String FAMILY_KIDS = "FAMILY_KIDS"` |  |
| `static final String GAMING = "GAMING"` |  |
| `static final String LIFE_STYLE = "LIFE_STYLE"` |  |
| `static final String MOVIES = "MOVIES"` |  |
| `static final String MUSIC = "MUSIC"` |  |
| `static final String NEWS = "NEWS"` |  |
| `static final String PREMIER = "PREMIER"` |  |
| `static final String SHOPPING = "SHOPPING"` |  |
| `static final String SPORTS = "SPORTS"` |  |
| `static final String TECH_SCIENCE = "TECH_SCIENCE"` |  |
| `static final String TRAVEL = "TRAVEL"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String[] decode(@NonNull String)` |  |
| `static String encode(@NonNull java.lang.String...)` |  |
| `static boolean isCanonical(String)` |  |

---

### `class static final TvContract.RecordedPrograms`

- **Implements:** `android.media.tv.TvContract.BaseTvColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_AUDIO_LANGUAGE = "audio_language"` |  |
| `static final String COLUMN_BROADCAST_GENRE = "broadcast_genre"` |  |
| `static final String COLUMN_CANONICAL_GENRE = "canonical_genre"` |  |
| `static final String COLUMN_CHANNEL_ID = "channel_id"` |  |
| `static final String COLUMN_CONTENT_RATING = "content_rating"` |  |
| `static final String COLUMN_END_TIME_UTC_MILLIS = "end_time_utc_millis"` |  |
| `static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number"` |  |
| `static final String COLUMN_EPISODE_TITLE = "episode_title"` |  |
| `static final String COLUMN_INPUT_ID = "input_id"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4"` |  |
| `static final String COLUMN_LONG_DESCRIPTION = "long_description"` |  |
| `static final String COLUMN_POSTER_ART_URI = "poster_art_uri"` |  |
| `static final String COLUMN_RECORDING_DATA_BYTES = "recording_data_bytes"` |  |
| `static final String COLUMN_RECORDING_DATA_URI = "recording_data_uri"` |  |
| `static final String COLUMN_RECORDING_DURATION_MILLIS = "recording_duration_millis"` |  |
| `static final String COLUMN_RECORDING_EXPIRE_TIME_UTC_MILLIS = "recording_expire_time_utc_millis"` |  |
| `static final String COLUMN_REVIEW_RATING = "review_rating"` |  |
| `static final String COLUMN_REVIEW_RATING_STYLE = "review_rating_style"` |  |
| `static final String COLUMN_SEARCHABLE = "searchable"` |  |
| `static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number"` |  |
| `static final String COLUMN_SEASON_TITLE = "season_title"` |  |
| `static final String COLUMN_SERIES_ID = "series_id"` |  |
| `static final String COLUMN_SHORT_DESCRIPTION = "short_description"` |  |
| `static final String COLUMN_SPLIT_ID = "split_id"` |  |
| `static final String COLUMN_START_TIME_UTC_MILLIS = "start_time_utc_millis"` |  |
| `static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final String COLUMN_VERSION_NUMBER = "version_number"` |  |
| `static final String COLUMN_VIDEO_HEIGHT = "video_height"` |  |
| `static final String COLUMN_VIDEO_WIDTH = "video_width"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/recorded_program"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/recorded_program"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final int REVIEW_RATING_STYLE_PERCENTAGE = 2` |  |
| `static final int REVIEW_RATING_STYLE_STARS = 0` |  |
| `static final int REVIEW_RATING_STYLE_THUMBS_UP_DOWN = 1` |  |

---

### `class static final TvContract.WatchNextPrograms`

- **Implements:** `android.media.tv.TvContract.BaseTvColumns`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ASPECT_RATIO_16_9 = 0` |  |
| `static final int ASPECT_RATIO_1_1 = 3` |  |
| `static final int ASPECT_RATIO_2_3 = 4` |  |
| `static final int ASPECT_RATIO_3_2 = 1` |  |
| `static final int ASPECT_RATIO_4_3 = 2` |  |
| `static final int AVAILABILITY_AVAILABLE = 0` |  |
| `static final int AVAILABILITY_FREE_WITH_SUBSCRIPTION = 1` |  |
| `static final int AVAILABILITY_PAID_CONTENT = 2` |  |
| `static final String COLUMN_AUDIO_LANGUAGE = "audio_language"` |  |
| `static final String COLUMN_AUTHOR = "author"` |  |
| `static final String COLUMN_AVAILABILITY = "availability"` |  |
| `static final String COLUMN_BROWSABLE = "browsable"` |  |
| `static final String COLUMN_CANONICAL_GENRE = "canonical_genre"` |  |
| `static final String COLUMN_CONTENT_ID = "content_id"` |  |
| `static final String COLUMN_CONTENT_RATING = "content_rating"` |  |
| `static final String COLUMN_DURATION_MILLIS = "duration_millis"` |  |
| `static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number"` |  |
| `static final String COLUMN_EPISODE_TITLE = "episode_title"` |  |
| `static final String COLUMN_INTENT_URI = "intent_uri"` |  |
| `static final String COLUMN_INTERACTION_COUNT = "interaction_count"` |  |
| `static final String COLUMN_INTERACTION_TYPE = "interaction_type"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4"` |  |
| `static final String COLUMN_INTERNAL_PROVIDER_ID = "internal_provider_id"` |  |
| `static final String COLUMN_ITEM_COUNT = "item_count"` |  |
| `static final String COLUMN_LAST_ENGAGEMENT_TIME_UTC_MILLIS = "last_engagement_time_utc_millis"` |  |
| `static final String COLUMN_LAST_PLAYBACK_POSITION_MILLIS = "last_playback_position_millis"` |  |
| `static final String COLUMN_LIVE = "live"` |  |
| `static final String COLUMN_LOGO_URI = "logo_uri"` |  |
| `static final String COLUMN_LONG_DESCRIPTION = "long_description"` |  |
| `static final String COLUMN_OFFER_PRICE = "offer_price"` |  |
| `static final String COLUMN_POSTER_ART_ASPECT_RATIO = "poster_art_aspect_ratio"` |  |
| `static final String COLUMN_POSTER_ART_URI = "poster_art_uri"` |  |
| `static final String COLUMN_PREVIEW_VIDEO_URI = "preview_video_uri"` |  |
| `static final String COLUMN_RELEASE_DATE = "release_date"` |  |
| `static final String COLUMN_REVIEW_RATING = "review_rating"` |  |
| `static final String COLUMN_REVIEW_RATING_STYLE = "review_rating_style"` |  |
| `static final String COLUMN_SEARCHABLE = "searchable"` |  |
| `static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number"` |  |
| `static final String COLUMN_SEASON_TITLE = "season_title"` |  |
| `static final String COLUMN_SERIES_ID = "series_id"` |  |
| `static final String COLUMN_SHORT_DESCRIPTION = "short_description"` |  |
| `static final String COLUMN_SPLIT_ID = "split_id"` |  |
| `static final String COLUMN_STARTING_PRICE = "starting_price"` |  |
| `static final String COLUMN_THUMBNAIL_ASPECT_RATIO = "poster_thumbnail_aspect_ratio"` |  |
| `static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final String COLUMN_TRANSIENT = "transient"` |  |
| `static final String COLUMN_TYPE = "type"` |  |
| `static final String COLUMN_VERSION_NUMBER = "version_number"` |  |
| `static final String COLUMN_VIDEO_HEIGHT = "video_height"` |  |
| `static final String COLUMN_VIDEO_WIDTH = "video_width"` |  |
| `static final String COLUMN_WATCH_NEXT_TYPE = "watch_next_type"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/watch_next_program"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/watch_next_program"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final int INTERACTION_TYPE_FANS = 3` |  |
| `static final int INTERACTION_TYPE_FOLLOWERS = 2` |  |
| `static final int INTERACTION_TYPE_LIKES = 4` |  |
| `static final int INTERACTION_TYPE_LISTENS = 1` |  |
| `static final int INTERACTION_TYPE_THUMBS = 5` |  |
| `static final int INTERACTION_TYPE_VIEWERS = 6` |  |
| `static final int INTERACTION_TYPE_VIEWS = 0` |  |
| `static final int REVIEW_RATING_STYLE_PERCENTAGE = 2` |  |
| `static final int REVIEW_RATING_STYLE_STARS = 0` |  |
| `static final int REVIEW_RATING_STYLE_THUMBS_UP_DOWN = 1` |  |
| `static final int TYPE_ALBUM = 8` |  |
| `static final int TYPE_ARTIST = 9` |  |
| `static final int TYPE_CHANNEL = 6` |  |
| `static final int TYPE_CLIP = 4` |  |
| `static final int TYPE_EVENT = 5` |  |
| `static final int TYPE_MOVIE = 0` |  |
| `static final int TYPE_PLAYLIST = 10` |  |
| `static final int TYPE_STATION = 11` |  |
| `static final int TYPE_TRACK = 7` |  |
| `static final int TYPE_TV_EPISODE = 3` |  |
| `static final int TYPE_TV_SEASON = 2` |  |
| `static final int TYPE_TV_SERIES = 1` |  |
| `static final int WATCH_NEXT_TYPE_CONTINUE = 0` |  |
| `static final int WATCH_NEXT_TYPE_NEW = 2` |  |
| `static final int WATCH_NEXT_TYPE_NEXT = 1` |  |
| `static final int WATCH_NEXT_TYPE_WATCHLIST = 3` |  |

---

### `class final TvInputInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_INPUT_ID = "android.media.tv.extra.INPUT_ID"` |  |
| `static final int TYPE_COMPONENT = 1004` |  |
| `static final int TYPE_COMPOSITE = 1001` |  |
| `static final int TYPE_DISPLAY_PORT = 1008` |  |
| `static final int TYPE_DVI = 1006` |  |
| `static final int TYPE_HDMI = 1007` |  |
| `static final int TYPE_OTHER = 1000` |  |
| `static final int TYPE_SCART = 1003` |  |
| `static final int TYPE_SVIDEO = 1002` |  |
| `static final int TYPE_TUNER = 0` |  |
| `static final int TYPE_VGA = 1005` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean canRecord()` |  |
| `android.content.Intent createSetupIntent()` |  |
| `int describeContents()` |  |
| `android.os.Bundle getExtras()` |  |
| `String getId()` |  |
| `String getParentId()` |  |
| `android.content.pm.ServiceInfo getServiceInfo()` |  |
| `int getTunerCount()` |  |
| `int getType()` |  |
| `boolean isHidden(android.content.Context)` |  |
| `boolean isPassthroughInput()` |  |
| `CharSequence loadCustomLabel(android.content.Context)` |  |
| `android.graphics.drawable.Drawable loadIcon(@NonNull android.content.Context)` |  |
| `CharSequence loadLabel(@NonNull android.content.Context)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final TvInputInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvInputInfo.Builder(android.content.Context, android.content.ComponentName)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.media.tv.TvInputInfo build()` |  |
| `android.media.tv.TvInputInfo.Builder setCanRecord(boolean)` |  |
| `android.media.tv.TvInputInfo.Builder setExtras(android.os.Bundle)` |  |
| `android.media.tv.TvInputInfo.Builder setTunerCount(int)` |  |

---

### `class final TvInputManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvInputManager.TvInputCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_BLOCKED_RATINGS_CHANGED = "android.media.tv.action.BLOCKED_RATINGS_CHANGED"` |  |
| `static final String ACTION_PARENTAL_CONTROLS_ENABLED_CHANGED = "android.media.tv.action.PARENTAL_CONTROLS_ENABLED_CHANGED"` |  |
| `static final String ACTION_QUERY_CONTENT_RATING_SYSTEMS = "android.media.tv.action.QUERY_CONTENT_RATING_SYSTEMS"` |  |
| `static final String ACTION_SETUP_INPUTS = "android.media.tv.action.SETUP_INPUTS"` |  |
| `static final String ACTION_VIEW_RECORDING_SCHEDULES = "android.media.tv.action.VIEW_RECORDING_SCHEDULES"` |  |
| `static final int INPUT_STATE_CONNECTED = 0` |  |
| `static final int INPUT_STATE_CONNECTED_STANDBY = 1` |  |
| `static final int INPUT_STATE_DISCONNECTED = 2` |  |
| `static final String META_DATA_CONTENT_RATING_SYSTEMS = "android.media.tv.metadata.CONTENT_RATING_SYSTEMS"` |  |
| `static final int RECORDING_ERROR_INSUFFICIENT_SPACE = 1` |  |
| `static final int RECORDING_ERROR_RESOURCE_BUSY = 2` |  |
| `static final int RECORDING_ERROR_UNKNOWN = 0` |  |
| `static final long TIME_SHIFT_INVALID_TIME = -9223372036854775808L` |  |
| `static final int TIME_SHIFT_STATUS_AVAILABLE = 3` |  |
| `static final int TIME_SHIFT_STATUS_UNAVAILABLE = 2` |  |
| `static final int TIME_SHIFT_STATUS_UNKNOWN = 0` |  |
| `static final int TIME_SHIFT_STATUS_UNSUPPORTED = 1` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY = 4` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_BUFFERING = 3` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_BLACKOUT = 16` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_CARD_INVALID = 15` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_CARD_MUTE = 14` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_INSUFFICIENT_OUTPUT_PROTECTION = 7` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_LICENSE_EXPIRED = 10` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_NEED_ACTIVATION = 11` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_NEED_PAIRING = 12` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_NO_CARD = 13` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_PVR_RECORDING_NOT_ALLOWED = 8` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_REBOOTING = 17` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_CAS_UNKNOWN = 18` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_INSUFFICIENT_RESOURCE = 6` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_NOT_CONNECTED = 5` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_TUNING = 1` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_UNKNOWN = 0` |  |
| `static final int VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<android.media.tv.TvContentRating> getBlockedRatings()` |  |
| `int getInputState(@NonNull String)` |  |
| `java.util.List<android.media.tv.TvInputInfo> getTvInputList()` |  |
| `boolean isParentalControlsEnabled()` |  |
| `boolean isRatingBlocked(@NonNull android.media.tv.TvContentRating)` |  |
| `void registerCallback(@NonNull android.media.tv.TvInputManager.TvInputCallback, @NonNull android.os.Handler)` |  |
| `void unregisterCallback(@NonNull android.media.tv.TvInputManager.TvInputCallback)` |  |
| `void updateTvInputInfo(@NonNull android.media.tv.TvInputInfo)` |  |
| `void onInputAdded(String)` |  |
| `void onInputRemoved(String)` |  |
| `void onInputStateChanged(String, int)` |  |
| `void onInputUpdated(String)` |  |
| `void onTvInputInfoUpdated(android.media.tv.TvInputInfo)` |  |

---

### `class abstract TvInputService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvInputService()` |  |
| `TvInputService.HardwareSession(android.content.Context)` |  |
| `TvInputService.RecordingSession(android.content.Context)` |  |
| `TvInputService.Session(android.content.Context)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PRIORITY_HINT_USE_CASE_TYPE_BACKGROUND = 100` |  |
| `static final int PRIORITY_HINT_USE_CASE_TYPE_LIVE = 400` |  |
| `static final int PRIORITY_HINT_USE_CASE_TYPE_PLAYBACK = 300` |  |
| `static final int PRIORITY_HINT_USE_CASE_TYPE_RECORD = 500` |  |
| `static final int PRIORITY_HINT_USE_CASE_TYPE_SCAN = 200` |  |
| `static final String SERVICE_INTERFACE = "android.media.tv.TvInputService"` |  |
| `static final String SERVICE_META_DATA = "android.media.tv.input"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract String getHardwareInputId()` |  |
| `void onHardwareVideoAvailable()` |  |
| `void onHardwareVideoUnavailable(int)` |  |
| `final boolean onSetSurface(android.view.Surface)` |  |
| `void notifyError(int)` |  |
| `void notifyRecordingStopped(android.net.Uri)` |  |
| `void notifyTuned(android.net.Uri)` |  |
| `void onAppPrivateCommand(@NonNull String, android.os.Bundle)` |  |
| `abstract void onRelease()` |  |
| `abstract void onStartRecording(@Nullable android.net.Uri)` |  |
| `void onStartRecording(@Nullable android.net.Uri, @NonNull android.os.Bundle)` |  |
| `abstract void onStopRecording()` |  |
| `abstract void onTune(android.net.Uri)` |  |
| `void onTune(android.net.Uri, android.os.Bundle)` |  |
| `void layoutSurface(int, int, int, int)` |  |
| `void notifyChannelRetuned(android.net.Uri)` |  |
| `void notifyContentAllowed()` |  |
| `void notifyContentBlocked(@NonNull android.media.tv.TvContentRating)` |  |
| `void notifyTimeShiftStatusChanged(int)` |  |
| `void notifyTrackSelected(int, String)` |  |
| `void notifyTracksChanged(java.util.List<android.media.tv.TvTrackInfo>)` |  |
| `void notifyVideoAvailable()` |  |
| `void notifyVideoUnavailable(int)` |  |
| `void onAppPrivateCommand(@NonNull String, android.os.Bundle)` |  |
| `android.view.View onCreateOverlayView()` |  |
| `boolean onGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |
| `void onOverlayViewSizeChanged(int, int)` |  |
| `abstract void onRelease()` |  |
| `boolean onSelectTrack(int, @Nullable String)` |  |
| `abstract void onSetCaptionEnabled(boolean)` |  |
| `abstract void onSetStreamVolume(@FloatRange(from=0.0, to=1.0) float)` |  |
| `abstract boolean onSetSurface(@Nullable android.view.Surface)` |  |
| `void onSurfaceChanged(int, int, int)` |  |
| `long onTimeShiftGetCurrentPosition()` |  |
| `long onTimeShiftGetStartPosition()` |  |
| `void onTimeShiftPause()` |  |
| `void onTimeShiftPlay(android.net.Uri)` |  |
| `void onTimeShiftResume()` |  |
| `void onTimeShiftSeekTo(long)` |  |
| `void onTimeShiftSetPlaybackParams(android.media.PlaybackParams)` |  |
| `boolean onTouchEvent(android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.view.MotionEvent)` |  |
| `abstract boolean onTune(android.net.Uri)` |  |
| `boolean onTune(android.net.Uri, android.os.Bundle)` |  |
| `void onUnblockContent(android.media.tv.TvContentRating)` |  |
| `void setOverlayViewEnabled(boolean)` |  |

---

### `class TvRecordingClient`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvRecordingClient(android.content.Context, String, @NonNull android.media.tv.TvRecordingClient.RecordingCallback, android.os.Handler)` |  |
| `TvRecordingClient.RecordingCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void release()` |  |
| `void sendAppPrivateCommand(@NonNull String, android.os.Bundle)` |  |
| `void startRecording(@Nullable android.net.Uri)` |  |
| `void startRecording(@Nullable android.net.Uri, @NonNull android.os.Bundle)` |  |
| `void stopRecording()` |  |
| `void tune(String, android.net.Uri)` |  |
| `void tune(String, android.net.Uri, android.os.Bundle)` |  |
| `void onConnectionFailed(String)` |  |
| `void onDisconnected(String)` |  |
| `void onError(int)` |  |
| `void onRecordingStopped(android.net.Uri)` |  |
| `void onTuned(android.net.Uri)` |  |

---

### `class final TvTrackInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_AUDIO = 0` |  |
| `static final int TYPE_SUBTITLE = 2` |  |
| `static final int TYPE_VIDEO = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAudioChannelCount()` |  |
| `int getAudioSampleRate()` |  |
| `CharSequence getDescription()` |  |
| `android.os.Bundle getExtra()` |  |
| `String getId()` |  |
| `String getLanguage()` |  |
| `int getType()` |  |
| `byte getVideoActiveFormatDescription()` |  |
| `float getVideoFrameRate()` |  |
| `int getVideoHeight()` |  |
| `float getVideoPixelAspectRatio()` |  |
| `int getVideoWidth()` |  |
| `boolean isAudioDescription()` |  |
| `boolean isEncrypted()` |  |
| `boolean isHardOfHearing()` |  |
| `boolean isSpokenSubtitle()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final TvTrackInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvTrackInfo.Builder(int, @NonNull String)` |  |

---

### `class TvView`

- **Extends:** `android.view.ViewGroup`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvView(android.content.Context)` |  |
| `TvView(android.content.Context, android.util.AttributeSet)` |  |
| `TvView(android.content.Context, android.util.AttributeSet, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean dispatchUnhandledInputEvent(android.view.InputEvent)` |  |
| `String getSelectedTrack(int)` |  |
| `java.util.List<android.media.tv.TvTrackInfo> getTracks(int)` |  |
| `boolean onUnhandledInputEvent(android.view.InputEvent)` |  |
| `void reset()` |  |
| `void selectTrack(int, String)` |  |
| `void sendAppPrivateCommand(@NonNull String, android.os.Bundle)` |  |
| `void setCallback(@Nullable android.media.tv.TvView.TvInputCallback)` |  |
| `void setCaptionEnabled(boolean)` |  |
| `void setOnUnhandledInputEventListener(android.media.tv.TvView.OnUnhandledInputEventListener)` |  |
| `void setStreamVolume(@FloatRange(from=0.0, to=1.0) float)` |  |
| `void setTimeShiftPositionCallback(@Nullable android.media.tv.TvView.TimeShiftPositionCallback)` |  |
| `void setZOrderMediaOverlay(boolean)` |  |
| `void setZOrderOnTop(boolean)` |  |
| `void timeShiftPause()` |  |
| `void timeShiftPlay(String, android.net.Uri)` |  |
| `void timeShiftResume()` |  |
| `void timeShiftSeekTo(long)` |  |
| `void timeShiftSetPlaybackParams(@NonNull android.media.PlaybackParams)` |  |
| `void tune(@NonNull String, android.net.Uri)` |  |
| `void tune(String, android.net.Uri, android.os.Bundle)` |  |

---

### `interface static TvView.OnUnhandledInputEventListener`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TvView.TimeShiftPositionCallback()` |  |
| `TvView.TvInputCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onUnhandledInputEvent(android.view.InputEvent)` |  |
| `void onTimeShiftCurrentPositionChanged(String, long)` |  |
| `void onTimeShiftStartPositionChanged(String, long)` |  |
| `void onChannelRetuned(String, android.net.Uri)` |  |
| `void onConnectionFailed(String)` |  |
| `void onContentAllowed(String)` |  |
| `void onContentBlocked(String, android.media.tv.TvContentRating)` |  |
| `void onDisconnected(String)` |  |
| `void onTimeShiftStatusChanged(String, int)` |  |
| `void onTrackSelected(String, int, String)` |  |
| `void onTracksChanged(String, java.util.List<android.media.tv.TvTrackInfo>)` |  |
| `void onVideoAvailable(String)` |  |
| `void onVideoSizeChanged(String, int, int)` |  |
| `void onVideoUnavailable(String, int)` |  |

---

