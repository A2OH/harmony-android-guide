# Android 11 (API 30) Public API Enumeration: Android Telephony Telecom

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.telecom` | 29 | 494 | 266 | 32 |
| `android.telephony` | 71 | 415 | 1127 | 20 |
| `android.telephony.cdma` | 1 | 10 | 0 | 2 |
| `android.telephony.data` | 2 | 22 | 26 | 1 |
| `android.telephony.emergency` | 1 | 6 | 16 | 0 |
| `android.telephony.euicc` | 3 | 7 | 42 | 1 |
| `android.telephony.gsm` | 5 | 6 | 0 | 2 |
| `android.telephony.ims` | 9 | 10 | 187 | 3 |
| `android.telephony.ims.feature` | 2 | 0 | 4 | 0 |
| `android.telephony.mbms` | 22 | 55 | 46 | 7 |
| **TOTAL** | **145** | **1025** | **1714** | **68** |

---

## Package: `android.telecom`

### `class final Call`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Call.Callback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_LAST_EMERGENCY_CALLBACK_TIME_MILLIS = "android.telecom.extra.LAST_EMERGENCY_CALLBACK_TIME_MILLIS"` |  |
| `static final String EXTRA_SILENT_RINGING_REQUESTED = "android.telecom.extra.SILENT_RINGING_REQUESTED"` |  |
| `static final String EXTRA_SUGGESTED_PHONE_ACCOUNTS = "android.telecom.extra.SUGGESTED_PHONE_ACCOUNTS"` |  |
| `static final int REJECT_REASON_DECLINED = 1` |  |
| `static final int REJECT_REASON_UNWANTED = 2` |  |
| `static final int STATE_ACTIVE = 4` |  |
| `static final int STATE_AUDIO_PROCESSING = 12` |  |
| `static final int STATE_CONNECTING = 9` |  |
| `static final int STATE_DIALING = 1` |  |
| `static final int STATE_DISCONNECTED = 7` |  |
| `static final int STATE_DISCONNECTING = 10` |  |
| `static final int STATE_HOLDING = 3` |  |
| `static final int STATE_NEW = 0` |  |
| `static final int STATE_PULLING_CALL = 11` |  |
| `static final int STATE_RINGING = 2` |  |
| `static final int STATE_SELECT_PHONE_ACCOUNT = 8` |  |
| `static final int STATE_SIMULATED_RINGING = 13` |  |
| `static final int HANDOVER_FAILURE_DEST_APP_REJECTED = 1` |  |
| `static final int HANDOVER_FAILURE_NOT_SUPPORTED = 2` |  |
| `static final int HANDOVER_FAILURE_ONGOING_EMERGENCY_CALL = 4` |  |
| `static final int HANDOVER_FAILURE_UNKNOWN = 5` |  |
| `static final int HANDOVER_FAILURE_USER_REJECTED = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void answer(int)` |  |
| `void conference(android.telecom.Call)` |  |
| `void deflect(android.net.Uri)` |  |
| `void disconnect()` |  |
| `java.util.List<java.lang.String> getCannedTextResponses()` |  |
| `java.util.List<android.telecom.Call> getChildren()` |  |
| `java.util.List<android.telecom.Call> getConferenceableCalls()` |  |
| `android.telecom.Call.Details getDetails()` |  |
| `android.telecom.Call getParent()` |  |
| `String getRemainingPostDialSequence()` |  |
| `int getState()` |  |
| `android.telecom.InCallService.VideoCall getVideoCall()` |  |
| `void handoverTo(android.telecom.PhoneAccountHandle, int, android.os.Bundle)` |  |
| `void hold()` |  |
| `boolean isRttActive()` |  |
| `void mergeConference()` |  |
| `void phoneAccountSelected(android.telecom.PhoneAccountHandle, boolean)` |  |
| `void playDtmfTone(char)` |  |
| `void postDialContinue(boolean)` |  |
| `void pullExternalCall()` |  |
| `void putExtras(android.os.Bundle)` |  |
| `void registerCallback(android.telecom.Call.Callback)` |  |
| `void registerCallback(android.telecom.Call.Callback, android.os.Handler)` |  |
| `void reject(boolean, String)` |  |
| `void reject(int)` |  |
| `void removeExtras(java.util.List<java.lang.String>)` |  |
| `void removeExtras(java.lang.String...)` |  |
| `void respondToRttRequest(int, boolean)` |  |
| `void sendCallEvent(String, android.os.Bundle)` |  |
| `void sendRttRequest()` |  |
| `void splitFromConference()` |  |
| `void stopDtmfTone()` |  |
| `void stopRtt()` |  |
| `void swapConference()` |  |
| `void unhold()` |  |
| `void unregisterCallback(android.telecom.Call.Callback)` |  |
| `void onCallDestroyed(android.telecom.Call)` |  |
| `void onCannedTextResponsesLoaded(android.telecom.Call, java.util.List<java.lang.String>)` |  |
| `void onChildrenChanged(android.telecom.Call, java.util.List<android.telecom.Call>)` |  |
| `void onConferenceableCallsChanged(android.telecom.Call, java.util.List<android.telecom.Call>)` |  |
| `void onConnectionEvent(android.telecom.Call, String, android.os.Bundle)` |  |
| `void onDetailsChanged(android.telecom.Call, android.telecom.Call.Details)` |  |
| `void onHandoverComplete(android.telecom.Call)` |  |
| `void onHandoverFailed(android.telecom.Call, int)` |  |
| `void onParentChanged(android.telecom.Call, android.telecom.Call)` |  |
| `void onPostDialWait(android.telecom.Call, String)` |  |
| `void onRttInitiationFailure(android.telecom.Call, int)` |  |
| `void onRttModeChanged(android.telecom.Call, int)` |  |
| `void onRttRequest(android.telecom.Call, int)` |  |
| `void onRttStatusChanged(android.telecom.Call, boolean, android.telecom.Call.RttCall)` |  |
| `void onStateChanged(android.telecom.Call, int)` |  |
| `void onVideoCallChanged(android.telecom.Call, android.telecom.InCallService.VideoCall)` |  |

---

### `class static Call.Details`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO = 4194304` |  |
| `static final int CAPABILITY_CAN_PAUSE_VIDEO = 1048576` |  |
| `static final int CAPABILITY_CAN_PULL_CALL = 8388608` |  |
| `static final int CAPABILITY_DISCONNECT_FROM_CONFERENCE = 8192` |  |
| `static final int CAPABILITY_HOLD = 1` |  |
| `static final int CAPABILITY_MANAGE_CONFERENCE = 128` |  |
| `static final int CAPABILITY_MERGE_CONFERENCE = 4` |  |
| `static final int CAPABILITY_MUTE = 64` |  |
| `static final int CAPABILITY_RESPOND_VIA_TEXT = 32` |  |
| `static final int CAPABILITY_SEPARATE_FROM_CONFERENCE = 4096` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL = 768` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_RX = 256` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_TX = 512` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL = 3072` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_RX = 1024` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_TX = 2048` |  |
| `static final int CAPABILITY_SUPPORT_DEFLECT = 16777216` |  |
| `static final int CAPABILITY_SUPPORT_HOLD = 2` |  |
| `static final int CAPABILITY_SWAP_CONFERENCE = 8` |  |
| `static final int DIRECTION_INCOMING = 0` |  |
| `static final int DIRECTION_OUTGOING = 1` |  |
| `static final int DIRECTION_UNKNOWN = -1` |  |
| `static final int PROPERTY_ASSISTED_DIALING = 512` |  |
| `static final int PROPERTY_CONFERENCE = 1` |  |
| `static final int PROPERTY_EMERGENCY_CALLBACK_MODE = 4` |  |
| `static final int PROPERTY_ENTERPRISE_CALL = 32` |  |
| `static final int PROPERTY_GENERIC_CONFERENCE = 2` |  |
| `static final int PROPERTY_HAS_CDMA_VOICE_PRIVACY = 128` |  |
| `static final int PROPERTY_HIGH_DEF_AUDIO = 16` |  |
| `static final int PROPERTY_IS_EXTERNAL_CALL = 64` |  |
| `static final int PROPERTY_NETWORK_IDENTIFIED_EMERGENCY_CALL = 2048` |  |
| `static final int PROPERTY_RTT = 1024` |  |
| `static final int PROPERTY_SELF_MANAGED = 256` |  |
| `static final int PROPERTY_VOIP_AUDIO_MODE = 4096` |  |
| `static final int PROPERTY_WIFI = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean can(int, int)` |  |
| `boolean can(int)` |  |
| `static String capabilitiesToString(int)` |  |
| `android.telecom.PhoneAccountHandle getAccountHandle()` |  |
| `int getCallCapabilities()` |  |
| `int getCallDirection()` |  |
| `int getCallProperties()` |  |
| `String getCallerDisplayName()` |  |
| `int getCallerDisplayNamePresentation()` |  |
| `int getCallerNumberVerificationStatus()` |  |
| `final long getConnectTimeMillis()` |  |
| `long getCreationTimeMillis()` |  |
| `android.telecom.DisconnectCause getDisconnectCause()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.telecom.GatewayInfo getGatewayInfo()` |  |
| `android.net.Uri getHandle()` |  |
| `int getHandlePresentation()` |  |
| `android.os.Bundle getIntentExtras()` |  |
| `android.telecom.StatusHints getStatusHints()` |  |
| `int getVideoState()` |  |
| `static boolean hasProperty(int, int)` |  |
| `boolean hasProperty(int)` |  |
| `static String propertiesToString(int)` |  |

---

### `class static final Call.RttCall`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int RTT_MODE_FULL = 1` |  |
| `static final int RTT_MODE_HCO = 2` |  |
| `static final int RTT_MODE_VCO = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getRttAudioMode()` |  |
| `String read()` |  |
| `String readImmediately() throws java.io.IOException` |  |
| `void setRttMode(int)` |  |
| `void write(String) throws java.io.IOException` |  |

---

### `class final CallAudioState`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallAudioState(boolean, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ROUTE_BLUETOOTH = 2` |  |
| `static final int ROUTE_EARPIECE = 1` |  |
| `static final int ROUTE_SPEAKER = 8` |  |
| `static final int ROUTE_WIRED_HEADSET = 4` |  |
| `static final int ROUTE_WIRED_OR_EARPIECE = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String audioRouteToString(int)` |  |
| `int describeContents()` |  |
| `android.bluetooth.BluetoothDevice getActiveBluetoothDevice()` |  |
| `int getRoute()` |  |
| `java.util.Collection<android.bluetooth.BluetoothDevice> getSupportedBluetoothDevices()` |  |
| `int getSupportedRouteMask()` |  |
| `boolean isMuted()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract CallRedirectionService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallRedirectionService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.telecom.CallRedirectionService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void cancelCall()` |  |
| `abstract void onPlaceCall(@NonNull android.net.Uri, @NonNull android.telecom.PhoneAccountHandle, boolean)` |  |
| `final boolean onUnbind(@NonNull android.content.Intent)` |  |
| `final void placeCallUnmodified()` |  |
| `final void redirectCall(@NonNull android.net.Uri, @NonNull android.telecom.PhoneAccountHandle, boolean)` |  |

---

### `class abstract CallScreeningService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallScreeningService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.telecom.CallScreeningService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onScreenCall(@NonNull android.telecom.Call.Details)` |  |
| `final void respondToCall(@NonNull android.telecom.Call.Details, @NonNull android.telecom.CallScreeningService.CallResponse)` |  |

---

### `class static CallScreeningService.CallResponse`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getDisallowCall()` |  |
| `boolean getRejectCall()` |  |
| `boolean getSilenceCall()` |  |
| `boolean getSkipCallLog()` |  |
| `boolean getSkipNotification()` |  |

---

### `class static CallScreeningService.CallResponse.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallScreeningService.CallResponse.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telecom.CallScreeningService.CallResponse build()` |  |
| `android.telecom.CallScreeningService.CallResponse.Builder setDisallowCall(boolean)` |  |
| `android.telecom.CallScreeningService.CallResponse.Builder setRejectCall(boolean)` |  |
| `android.telecom.CallScreeningService.CallResponse.Builder setSkipCallLog(boolean)` |  |
| `android.telecom.CallScreeningService.CallResponse.Builder setSkipNotification(boolean)` |  |

---

### `class abstract Conference`

- **继承：** `android.telecom.Conferenceable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Conference(android.telecom.PhoneAccountHandle)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long CONNECT_TIME_NOT_SPECIFIED = 0L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean addConnection(android.telecom.Connection)` |  |
| `final void destroy()` |  |
| `final android.telecom.CallAudioState getCallAudioState()` |  |
| `final java.util.List<android.telecom.Connection> getConferenceableConnections()` |  |
| `final int getConnectionCapabilities()` |  |
| `final int getConnectionProperties()` |  |
| `final long getConnectionStartElapsedRealtimeMillis()` |  |
| `final java.util.List<android.telecom.Connection> getConnections()` |  |
| `final android.telecom.DisconnectCause getDisconnectCause()` |  |
| `final android.os.Bundle getExtras()` |  |
| `final android.telecom.PhoneAccountHandle getPhoneAccountHandle()` |  |
| `final int getState()` |  |
| `final android.telecom.StatusHints getStatusHints()` |  |
| `android.telecom.Connection.VideoProvider getVideoProvider()` |  |
| `int getVideoState()` |  |
| `void onCallAudioStateChanged(android.telecom.CallAudioState)` |  |
| `void onConnectionAdded(android.telecom.Connection)` |  |
| `void onDisconnect()` |  |
| `void onExtrasChanged(android.os.Bundle)` |  |
| `void onHold()` |  |
| `void onMerge(android.telecom.Connection)` |  |
| `void onMerge()` |  |
| `void onPlayDtmfTone(char)` |  |
| `void onSeparate(android.telecom.Connection)` |  |
| `void onStopDtmfTone()` |  |
| `void onSwap()` |  |
| `void onUnhold()` |  |
| `final void putExtras(@NonNull android.os.Bundle)` |  |
| `final void removeConnection(android.telecom.Connection)` |  |
| `final void removeExtras(java.util.List<java.lang.String>)` |  |
| `final void removeExtras(java.lang.String...)` |  |
| `void sendConferenceEvent(@NonNull String, @Nullable android.os.Bundle)` |  |
| `final void setActive()` |  |
| `final void setConferenceableConnections(java.util.List<android.telecom.Connection>)` |  |
| `final void setConnectionCapabilities(int)` |  |
| `final void setConnectionProperties(int)` |  |
| `final void setConnectionStartElapsedRealtimeMillis(long)` |  |
| `final void setConnectionTime(@IntRange(from=0) long)` |  |
| `final void setDialing()` |  |
| `final void setDisconnected(android.telecom.DisconnectCause)` |  |
| `final void setExtras(@Nullable android.os.Bundle)` |  |
| `final void setOnHold()` |  |
| `final void setStatusHints(android.telecom.StatusHints)` |  |
| `final void setVideoProvider(android.telecom.Connection, android.telecom.Connection.VideoProvider)` |  |
| `final void setVideoState(android.telecom.Connection, int)` |  |

---

### `class abstract Conferenceable`


---

### `class abstract Connection`

- **继承：** `android.telecom.Conferenceable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Connection()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_CODEC_AMR = 1` |  |
| `static final int AUDIO_CODEC_AMR_WB = 2` |  |
| `static final int AUDIO_CODEC_EVRC = 4` |  |
| `static final int AUDIO_CODEC_EVRC_B = 5` |  |
| `static final int AUDIO_CODEC_EVRC_NW = 7` |  |
| `static final int AUDIO_CODEC_EVRC_WB = 6` |  |
| `static final int AUDIO_CODEC_EVS_FB = 20` |  |
| `static final int AUDIO_CODEC_EVS_NB = 17` |  |
| `static final int AUDIO_CODEC_EVS_SWB = 19` |  |
| `static final int AUDIO_CODEC_EVS_WB = 18` |  |
| `static final int AUDIO_CODEC_G711A = 13` |  |
| `static final int AUDIO_CODEC_G711AB = 15` |  |
| `static final int AUDIO_CODEC_G711U = 11` |  |
| `static final int AUDIO_CODEC_G722 = 14` |  |
| `static final int AUDIO_CODEC_G723 = 12` |  |
| `static final int AUDIO_CODEC_G729 = 16` |  |
| `static final int AUDIO_CODEC_GSM_EFR = 8` |  |
| `static final int AUDIO_CODEC_GSM_FR = 9` |  |
| `static final int AUDIO_CODEC_GSM_HR = 10` |  |
| `static final int AUDIO_CODEC_NONE = 0` |  |
| `static final int AUDIO_CODEC_QCELP13K = 3` |  |
| `static final int CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO = 8388608` |  |
| `static final int CAPABILITY_CAN_PAUSE_VIDEO = 1048576` |  |
| `static final int CAPABILITY_CAN_PULL_CALL = 16777216` |  |
| `static final int CAPABILITY_CAN_SEND_RESPONSE_VIA_CONNECTION = 4194304` |  |
| `static final int CAPABILITY_DISCONNECT_FROM_CONFERENCE = 8192` |  |
| `static final int CAPABILITY_HOLD = 1` |  |
| `static final int CAPABILITY_MANAGE_CONFERENCE = 128` |  |
| `static final int CAPABILITY_MERGE_CONFERENCE = 4` |  |
| `static final int CAPABILITY_MUTE = 64` |  |
| `static final int CAPABILITY_RESPOND_VIA_TEXT = 32` |  |
| `static final int CAPABILITY_SEPARATE_FROM_CONFERENCE = 4096` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL = 768` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_RX = 256` |  |
| `static final int CAPABILITY_SUPPORTS_VT_LOCAL_TX = 512` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL = 3072` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_RX = 1024` |  |
| `static final int CAPABILITY_SUPPORTS_VT_REMOTE_TX = 2048` |  |
| `static final int CAPABILITY_SUPPORT_DEFLECT = 33554432` |  |
| `static final int CAPABILITY_SUPPORT_HOLD = 2` |  |
| `static final int CAPABILITY_SWAP_CONFERENCE = 8` |  |
| `static final String EVENT_CALL_HOLD_FAILED = "android.telecom.event.CALL_HOLD_FAILED"` |  |
| `static final String EVENT_CALL_MERGE_FAILED = "android.telecom.event.CALL_MERGE_FAILED"` |  |
| `static final String EVENT_CALL_PULL_FAILED = "android.telecom.event.CALL_PULL_FAILED"` |  |
| `static final String EVENT_CALL_REMOTELY_HELD = "android.telecom.event.CALL_REMOTELY_HELD"` |  |
| `static final String EVENT_CALL_REMOTELY_UNHELD = "android.telecom.event.CALL_REMOTELY_UNHELD"` |  |
| `static final String EVENT_CALL_SWITCH_FAILED = "android.telecom.event.CALL_SWITCH_FAILED"` |  |
| `static final String EVENT_MERGE_COMPLETE = "android.telecom.event.MERGE_COMPLETE"` |  |
| `static final String EVENT_MERGE_START = "android.telecom.event.MERGE_START"` |  |
| `static final String EVENT_ON_HOLD_TONE_END = "android.telecom.event.ON_HOLD_TONE_END"` |  |
| `static final String EVENT_ON_HOLD_TONE_START = "android.telecom.event.ON_HOLD_TONE_START"` |  |
| `static final String EVENT_RTT_AUDIO_INDICATION_CHANGED = "android.telecom.event.RTT_AUDIO_INDICATION_CHANGED"` |  |
| `static final String EXTRA_ANSWERING_DROPS_FG_CALL = "android.telecom.extra.ANSWERING_DROPS_FG_CALL"` |  |
| `static final String EXTRA_ANSWERING_DROPS_FG_CALL_APP_NAME = "android.telecom.extra.ANSWERING_DROPS_FG_CALL_APP_NAME"` |  |
| `static final String EXTRA_AUDIO_CODEC = "android.telecom.extra.AUDIO_CODEC"` |  |
| `static final String EXTRA_CALL_SUBJECT = "android.telecom.extra.CALL_SUBJECT"` |  |
| `static final String EXTRA_CHILD_ADDRESS = "android.telecom.extra.CHILD_ADDRESS"` |  |
| `static final String EXTRA_IS_RTT_AUDIO_PRESENT = "android.telecom.extra.IS_RTT_AUDIO_PRESENT"` |  |
| `static final String EXTRA_LAST_FORWARDED_NUMBER = "android.telecom.extra.LAST_FORWARDED_NUMBER"` |  |
| `static final String EXTRA_SIP_INVITE = "android.telecom.extra.SIP_INVITE"` |  |
| `static final int PROPERTY_ASSISTED_DIALING = 512` |  |
| `static final int PROPERTY_HAS_CDMA_VOICE_PRIVACY = 32` |  |
| `static final int PROPERTY_HIGH_DEF_AUDIO = 4` |  |
| `static final int PROPERTY_IS_EXTERNAL_CALL = 16` |  |
| `static final int PROPERTY_IS_RTT = 256` |  |
| `static final int PROPERTY_NETWORK_IDENTIFIED_EMERGENCY_CALL = 1024` |  |
| `static final int PROPERTY_SELF_MANAGED = 128` |  |
| `static final int PROPERTY_WIFI = 8` |  |
| `static final int STATE_ACTIVE = 4` |  |
| `static final int STATE_DIALING = 3` |  |
| `static final int STATE_DISCONNECTED = 6` |  |
| `static final int STATE_HOLDING = 5` |  |
| `static final int STATE_INITIALIZING = 0` |  |
| `static final int STATE_NEW = 1` |  |
| `static final int STATE_PULLING_CALL = 7` |  |
| `static final int STATE_RINGING = 2` |  |
| `static final int VERIFICATION_STATUS_FAILED = 2` |  |
| `static final int VERIFICATION_STATUS_NOT_VERIFIED = 0` |  |
| `static final int VERIFICATION_STATUS_PASSED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String capabilitiesToString(int)` |  |
| `static android.telecom.Connection createCanceledConnection()` |  |
| `static android.telecom.Connection createFailedConnection(android.telecom.DisconnectCause)` |  |
| `final void destroy()` |  |
| `final android.net.Uri getAddress()` |  |
| `final int getAddressPresentation()` |  |
| `final boolean getAudioModeIsVoip()` |  |
| `final android.telecom.CallAudioState getCallAudioState()` |  |
| `final String getCallerDisplayName()` |  |
| `final int getCallerDisplayNamePresentation()` |  |
| `final int getCallerNumberVerificationStatus()` |  |
| `final android.telecom.Conference getConference()` |  |
| `final java.util.List<android.telecom.Conferenceable> getConferenceables()` |  |
| `final int getConnectionCapabilities()` |  |
| `final int getConnectionProperties()` |  |
| `final android.telecom.DisconnectCause getDisconnectCause()` |  |
| `final android.os.Bundle getExtras()` |  |
| `final int getState()` |  |
| `final android.telecom.StatusHints getStatusHints()` |  |
| `final android.telecom.Connection.VideoProvider getVideoProvider()` |  |
| `final int getVideoState()` |  |
| `void handleRttUpgradeResponse(@Nullable android.telecom.Connection.RttTextStream)` |  |
| `final boolean isRingbackRequested()` |  |
| `final void notifyConferenceMergeFailed()` |  |
| `void onAbort()` |  |
| `void onAnswer(int)` |  |
| `void onAnswer()` |  |
| `void onCallAudioStateChanged(android.telecom.CallAudioState)` |  |
| `void onCallEvent(String, android.os.Bundle)` |  |
| `void onDeflect(android.net.Uri)` |  |
| `void onDisconnect()` |  |
| `void onExtrasChanged(android.os.Bundle)` |  |
| `void onHandoverComplete()` |  |
| `void onHold()` |  |
| `void onPlayDtmfTone(char)` |  |
| `void onPostDialContinue(boolean)` |  |
| `void onPullExternalCall()` |  |
| `void onReject()` |  |
| `void onReject(int)` |  |
| `void onReject(String)` |  |
| `void onSeparate()` |  |
| `void onShowIncomingCallUi()` |  |
| `void onSilence()` |  |
| `void onStartRtt(@NonNull android.telecom.Connection.RttTextStream)` |  |
| `void onStateChanged(int)` |  |
| `void onStopDtmfTone()` |  |
| `void onStopRtt()` |  |
| `void onUnhold()` |  |
| `static String propertiesToString(int)` |  |
| `final void putExtras(@NonNull android.os.Bundle)` |  |
| `final void removeExtras(java.util.List<java.lang.String>)` |  |
| `final void removeExtras(java.lang.String...)` |  |
| `void requestBluetoothAudio(@NonNull android.bluetooth.BluetoothDevice)` |  |
| `void sendConnectionEvent(String, android.os.Bundle)` |  |
| `final void sendRemoteRttRequest()` |  |
| `final void sendRttInitiationFailure(int)` |  |
| `final void sendRttInitiationSuccess()` |  |
| `final void sendRttSessionRemotelyTerminated()` |  |
| `final void setActive()` |  |
| `final void setAddress(android.net.Uri, int)` |  |
| `final void setAudioModeIsVoip(boolean)` |  |
| `final void setAudioRoute(int)` |  |
| `final void setCallerDisplayName(String, int)` |  |
| `final void setCallerNumberVerificationStatus(int)` |  |
| `final void setConferenceableConnections(java.util.List<android.telecom.Connection>)` |  |
| `final void setConferenceables(java.util.List<android.telecom.Conferenceable>)` |  |
| `final void setConnectionCapabilities(int)` |  |
| `final void setConnectionProperties(int)` |  |
| `final void setDialing()` |  |
| `final void setDisconnected(android.telecom.DisconnectCause)` |  |
| `final void setExtras(@Nullable android.os.Bundle)` |  |
| `final void setInitialized()` |  |
| `final void setInitializing()` |  |
| `final void setNextPostDialChar(char)` |  |
| `final void setOnHold()` |  |
| `final void setPostDialWait(String)` |  |
| `final void setPulling()` |  |
| `final void setRingbackRequested(boolean)` |  |
| `final void setRinging()` |  |
| `final void setStatusHints(android.telecom.StatusHints)` |  |
| `final void setVideoProvider(android.telecom.Connection.VideoProvider)` |  |
| `final void setVideoState(int)` |  |
| `static String stateToString(int)` |  |

---

### `class static final Connection.RttModifyStatus`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SESSION_MODIFY_REQUEST_FAIL = 2` |  |
| `static final int SESSION_MODIFY_REQUEST_INVALID = 3` |  |
| `static final int SESSION_MODIFY_REQUEST_REJECTED_BY_REMOTE = 5` |  |
| `static final int SESSION_MODIFY_REQUEST_SUCCESS = 1` |  |
| `static final int SESSION_MODIFY_REQUEST_TIMED_OUT = 4` |  |

---

### `class static final Connection.RttTextStream`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Connection.VideoProvider()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SESSION_EVENT_CAMERA_FAILURE = 5` |  |
| `static final int SESSION_EVENT_CAMERA_PERMISSION_ERROR = 7` |  |
| `static final int SESSION_EVENT_CAMERA_READY = 6` |  |
| `static final int SESSION_EVENT_RX_PAUSE = 1` |  |
| `static final int SESSION_EVENT_RX_RESUME = 2` |  |
| `static final int SESSION_EVENT_TX_START = 3` |  |
| `static final int SESSION_EVENT_TX_STOP = 4` |  |
| `static final int SESSION_MODIFY_REQUEST_FAIL = 2` |  |
| `static final int SESSION_MODIFY_REQUEST_INVALID = 3` |  |
| `static final int SESSION_MODIFY_REQUEST_REJECTED_BY_REMOTE = 5` |  |
| `static final int SESSION_MODIFY_REQUEST_SUCCESS = 1` |  |
| `static final int SESSION_MODIFY_REQUEST_TIMED_OUT = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String read() throws java.io.IOException` |  |
| `String readImmediately() throws java.io.IOException` |  |
| `void write(String) throws java.io.IOException` |  |
| `void changeCameraCapabilities(android.telecom.VideoProfile.CameraCapabilities)` |  |
| `void changePeerDimensions(int, int)` |  |
| `void changeVideoQuality(int)` |  |
| `void handleCallSessionEvent(int)` |  |
| `abstract void onRequestCameraCapabilities()` |  |
| `abstract void onRequestConnectionDataUsage()` |  |
| `abstract void onSendSessionModifyRequest(android.telecom.VideoProfile, android.telecom.VideoProfile)` |  |
| `abstract void onSendSessionModifyResponse(android.telecom.VideoProfile)` |  |
| `abstract void onSetCamera(String)` |  |
| `abstract void onSetDeviceOrientation(int)` |  |
| `abstract void onSetDisplaySurface(android.view.Surface)` |  |
| `abstract void onSetPauseImage(android.net.Uri)` |  |
| `abstract void onSetPreviewSurface(android.view.Surface)` |  |
| `abstract void onSetZoom(float)` |  |
| `void receiveSessionModifyRequest(android.telecom.VideoProfile)` |  |
| `void receiveSessionModifyResponse(int, android.telecom.VideoProfile, android.telecom.VideoProfile)` |  |
| `void setCallDataUsage(long)` |  |

---

### `class final ConnectionRequest`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectionRequest(android.telecom.PhoneAccountHandle, android.net.Uri, android.os.Bundle)` |  |
| `ConnectionRequest(android.telecom.PhoneAccountHandle, android.net.Uri, android.os.Bundle, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.telecom.PhoneAccountHandle getAccountHandle()` |  |
| `android.net.Uri getAddress()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.telecom.Connection.RttTextStream getRttTextStream()` |  |
| `int getVideoState()` |  |
| `boolean isAdhocConferenceCall()` |  |
| `boolean isRequestingRtt()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract ConnectionService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectionService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.telecom.ConnectionService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void addConference(android.telecom.Conference)` |  |
| `final void addExistingConnection(android.telecom.PhoneAccountHandle, android.telecom.Connection)` |  |
| `final void conferenceRemoteConnections(android.telecom.RemoteConnection, android.telecom.RemoteConnection)` |  |
| `final void connectionServiceFocusReleased()` |  |
| `final android.telecom.RemoteConnection createRemoteIncomingConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `final android.telecom.RemoteConnection createRemoteOutgoingConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `final java.util.Collection<android.telecom.Conference> getAllConferences()` |  |
| `final java.util.Collection<android.telecom.Connection> getAllConnections()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `void onConference(android.telecom.Connection, android.telecom.Connection)` |  |
| `void onConnectionServiceFocusGained()` |  |
| `void onConnectionServiceFocusLost()` |  |
| `android.telecom.Connection onCreateIncomingConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `void onCreateIncomingConnectionFailed(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `android.telecom.Connection onCreateIncomingHandoverConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `android.telecom.Connection onCreateOutgoingConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `void onCreateOutgoingConnectionFailed(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `android.telecom.Connection onCreateOutgoingHandoverConnection(android.telecom.PhoneAccountHandle, android.telecom.ConnectionRequest)` |  |
| `void onHandoverFailed(android.telecom.ConnectionRequest, int)` |  |
| `void onRemoteConferenceAdded(android.telecom.RemoteConference)` |  |
| `void onRemoteExistingConnectionAdded(android.telecom.RemoteConnection)` |  |

---

### `class final DisconnectCause`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DisconnectCause(int)` |  |
| `DisconnectCause(int, String)` |  |
| `DisconnectCause(int, CharSequence, CharSequence, String)` |  |
| `DisconnectCause(int, CharSequence, CharSequence, String, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ANSWERED_ELSEWHERE = 11` |  |
| `static final int BUSY = 7` |  |
| `static final int CALL_PULLED = 12` |  |
| `static final int CANCELED = 4` |  |
| `static final int CONNECTION_MANAGER_NOT_SUPPORTED = 10` |  |
| `static final int ERROR = 1` |  |
| `static final int LOCAL = 2` |  |
| `static final int MISSED = 5` |  |
| `static final int OTHER = 9` |  |
| `static final String REASON_EMERGENCY_CALL_PLACED = "REASON_EMERGENCY_CALL_PLACED"` |  |
| `static final int REJECTED = 6` |  |
| `static final int REMOTE = 3` |  |
| `static final int RESTRICTED = 8` |  |
| `static final int UNKNOWN = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCode()` |  |
| `CharSequence getDescription()` |  |
| `CharSequence getLabel()` |  |
| `String getReason()` |  |
| `int getTone()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class GatewayInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GatewayInfo(String, android.net.Uri, android.net.Uri)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.net.Uri getGatewayAddress()` |  |
| `String getGatewayProviderPackageName()` |  |
| `android.net.Uri getOriginalAddress()` |  |
| `boolean isEmpty()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract InCallService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InCallService()` |  |
| `InCallService.VideoCall()` |  |
| `InCallService.VideoCall.Callback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.telecom.InCallService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean canAddCall()` |  |
| `final android.telecom.CallAudioState getCallAudioState()` |  |
| `final java.util.List<android.telecom.Call> getCalls()` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onBringToForeground(boolean)` |  |
| `void onCallAdded(android.telecom.Call)` |  |
| `void onCallAudioStateChanged(android.telecom.CallAudioState)` |  |
| `void onCallRemoved(android.telecom.Call)` |  |
| `void onCanAddCallChanged(boolean)` |  |
| `void onConnectionEvent(android.telecom.Call, String, android.os.Bundle)` |  |
| `void onSilenceRinger()` |  |
| `final void requestBluetoothAudio(@NonNull android.bluetooth.BluetoothDevice)` |  |
| `final void setAudioRoute(int)` |  |
| `final void setMuted(boolean)` |  |
| `abstract void registerCallback(android.telecom.InCallService.VideoCall.Callback)` |  |
| `abstract void registerCallback(android.telecom.InCallService.VideoCall.Callback, android.os.Handler)` |  |
| `abstract void requestCallDataUsage()` |  |
| `abstract void requestCameraCapabilities()` |  |
| `abstract void sendSessionModifyRequest(android.telecom.VideoProfile)` |  |
| `abstract void sendSessionModifyResponse(android.telecom.VideoProfile)` |  |
| `abstract void setCamera(String)` |  |
| `abstract void setDeviceOrientation(int)` |  |
| `abstract void setDisplaySurface(android.view.Surface)` |  |
| `abstract void setPauseImage(android.net.Uri)` |  |
| `abstract void setPreviewSurface(android.view.Surface)` |  |
| `abstract void setZoom(float)` |  |
| `abstract void unregisterCallback(android.telecom.InCallService.VideoCall.Callback)` |  |
| `abstract void onCallDataUsageChanged(long)` |  |
| `abstract void onCallSessionEvent(int)` |  |
| `abstract void onCameraCapabilitiesChanged(android.telecom.VideoProfile.CameraCapabilities)` |  |
| `abstract void onPeerDimensionsChanged(int, int)` |  |
| `abstract void onSessionModifyRequestReceived(android.telecom.VideoProfile)` |  |
| `abstract void onSessionModifyResponseReceived(int, android.telecom.VideoProfile, android.telecom.VideoProfile)` |  |
| `abstract void onVideoQualityChanged(int)` |  |

---

### `class final PhoneAccount`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CAPABILITY_ADHOC_CONFERENCE_CALLING = 16384` |  |
| `static final int CAPABILITY_CALL_PROVIDER = 2` |  |
| `static final int CAPABILITY_CALL_SUBJECT = 64` |  |
| `static final int CAPABILITY_CONNECTION_MANAGER = 1` |  |
| `static final int CAPABILITY_PLACE_EMERGENCY_CALLS = 16` |  |
| `static final int CAPABILITY_RTT = 4096` |  |
| `static final int CAPABILITY_SELF_MANAGED = 2048` |  |
| `static final int CAPABILITY_SIM_SUBSCRIPTION = 4` |  |
| `static final int CAPABILITY_SUPPORTS_VIDEO_CALLING = 1024` |  |
| `static final int CAPABILITY_VIDEO_CALLING = 8` |  |
| `static final int CAPABILITY_VIDEO_CALLING_RELIES_ON_PRESENCE = 256` |  |
| `static final String EXTRA_CALL_SUBJECT_CHARACTER_ENCODING = "android.telecom.extra.CALL_SUBJECT_CHARACTER_ENCODING"` |  |
| `static final String EXTRA_CALL_SUBJECT_MAX_LENGTH = "android.telecom.extra.CALL_SUBJECT_MAX_LENGTH"` |  |
| `static final String EXTRA_LOG_SELF_MANAGED_CALLS = "android.telecom.extra.LOG_SELF_MANAGED_CALLS"` |  |
| `static final String EXTRA_SUPPORTS_HANDOVER_FROM = "android.telecom.extra.SUPPORTS_HANDOVER_FROM"` |  |
| `static final String EXTRA_SUPPORTS_HANDOVER_TO = "android.telecom.extra.SUPPORTS_HANDOVER_TO"` |  |
| `static final int NO_HIGHLIGHT_COLOR = 0` |  |
| `static final int NO_RESOURCE_ID = -1` |  |
| `static final String SCHEME_SIP = "sip"` |  |
| `static final String SCHEME_TEL = "tel"` |  |
| `static final String SCHEME_VOICEMAIL = "voicemail"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.telecom.PhoneAccount.Builder builder(android.telecom.PhoneAccountHandle, CharSequence)` |  |
| `int describeContents()` |  |
| `android.telecom.PhoneAccountHandle getAccountHandle()` |  |
| `android.net.Uri getAddress()` |  |
| `int getCapabilities()` |  |
| `android.os.Bundle getExtras()` |  |
| `int getHighlightColor()` |  |
| `android.graphics.drawable.Icon getIcon()` |  |
| `CharSequence getLabel()` |  |
| `CharSequence getShortDescription()` |  |
| `android.net.Uri getSubscriptionAddress()` |  |
| `java.util.List<java.lang.String> getSupportedUriSchemes()` |  |
| `boolean hasCapabilities(int)` |  |
| `boolean isEnabled()` |  |
| `boolean supportsUriScheme(String)` |  |
| `android.telecom.PhoneAccount.Builder toBuilder()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static PhoneAccount.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneAccount.Builder(android.telecom.PhoneAccountHandle, CharSequence)` |  |
| `PhoneAccount.Builder(android.telecom.PhoneAccount)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telecom.PhoneAccount.Builder addSupportedUriScheme(String)` |  |
| `android.telecom.PhoneAccount build()` |  |
| `android.telecom.PhoneAccount.Builder setAddress(android.net.Uri)` |  |
| `android.telecom.PhoneAccount.Builder setCapabilities(int)` |  |
| `android.telecom.PhoneAccount.Builder setExtras(android.os.Bundle)` |  |
| `android.telecom.PhoneAccount.Builder setHighlightColor(int)` |  |
| `android.telecom.PhoneAccount.Builder setIcon(android.graphics.drawable.Icon)` |  |
| `android.telecom.PhoneAccount.Builder setShortDescription(CharSequence)` |  |
| `android.telecom.PhoneAccount.Builder setSubscriptionAddress(android.net.Uri)` |  |
| `android.telecom.PhoneAccount.Builder setSupportedUriSchemes(java.util.List<java.lang.String>)` |  |

---

### `class final PhoneAccountHandle`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneAccountHandle(@NonNull android.content.ComponentName, @NonNull String)` |  |
| `PhoneAccountHandle(@NonNull android.content.ComponentName, @NonNull String, @NonNull android.os.UserHandle)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.content.ComponentName getComponentName()` |  |
| `String getId()` |  |
| `android.os.UserHandle getUserHandle()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PhoneAccountSuggestion`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneAccountSuggestion(@NonNull android.telecom.PhoneAccountHandle, int, boolean)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REASON_FREQUENT = 2` |  |
| `static final int REASON_INTRA_CARRIER = 1` |  |
| `static final int REASON_NONE = 0` |  |
| `static final int REASON_OTHER = 4` |  |
| `static final int REASON_USER_SET = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getReason()` |  |
| `boolean shouldAutoSelect()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final RemoteConference`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteConference.Callback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void disconnect()` |  |
| `java.util.List<android.telecom.RemoteConnection> getConferenceableConnections()` |  |
| `int getConnectionCapabilities()` |  |
| `int getConnectionProperties()` |  |
| `java.util.List<android.telecom.RemoteConnection> getConnections()` |  |
| `android.telecom.DisconnectCause getDisconnectCause()` |  |
| `android.os.Bundle getExtras()` |  |
| `int getState()` |  |
| `void hold()` |  |
| `void merge()` |  |
| `void playDtmfTone(char)` |  |
| `void registerCallback(android.telecom.RemoteConference.Callback)` |  |
| `void registerCallback(android.telecom.RemoteConference.Callback, android.os.Handler)` |  |
| `void separate(android.telecom.RemoteConnection)` |  |
| `void setCallAudioState(android.telecom.CallAudioState)` |  |
| `void stopDtmfTone()` |  |
| `void swap()` |  |
| `void unhold()` |  |
| `void unregisterCallback(android.telecom.RemoteConference.Callback)` |  |
| `void onConferenceableConnectionsChanged(android.telecom.RemoteConference, java.util.List<android.telecom.RemoteConnection>)` |  |
| `void onConnectionAdded(android.telecom.RemoteConference, android.telecom.RemoteConnection)` |  |
| `void onConnectionCapabilitiesChanged(android.telecom.RemoteConference, int)` |  |
| `void onConnectionPropertiesChanged(android.telecom.RemoteConference, int)` |  |
| `void onConnectionRemoved(android.telecom.RemoteConference, android.telecom.RemoteConnection)` |  |
| `void onDestroyed(android.telecom.RemoteConference)` |  |
| `void onDisconnected(android.telecom.RemoteConference, android.telecom.DisconnectCause)` |  |
| `void onExtrasChanged(android.telecom.RemoteConference, @Nullable android.os.Bundle)` |  |
| `void onStateChanged(android.telecom.RemoteConference, int, int)` |  |

---

### `class final RemoteConnection`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteConnection.Callback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void abort()` |  |
| `void answer()` |  |
| `void disconnect()` |  |
| `android.net.Uri getAddress()` |  |
| `int getAddressPresentation()` |  |
| `CharSequence getCallerDisplayName()` |  |
| `int getCallerDisplayNamePresentation()` |  |
| `android.telecom.RemoteConference getConference()` |  |
| `java.util.List<android.telecom.RemoteConnection> getConferenceableConnections()` |  |
| `int getConnectionCapabilities()` |  |
| `int getConnectionProperties()` |  |
| `android.telecom.DisconnectCause getDisconnectCause()` |  |
| `android.os.Bundle getExtras()` |  |
| `int getState()` |  |
| `android.telecom.StatusHints getStatusHints()` |  |
| `android.telecom.RemoteConnection.VideoProvider getVideoProvider()` |  |
| `int getVideoState()` |  |
| `void hold()` |  |
| `boolean isRingbackRequested()` |  |
| `boolean isVoipAudioMode()` |  |
| `void playDtmfTone(char)` |  |
| `void postDialContinue(boolean)` |  |
| `void pullExternalCall()` |  |
| `void registerCallback(android.telecom.RemoteConnection.Callback)` |  |
| `void registerCallback(android.telecom.RemoteConnection.Callback, android.os.Handler)` |  |
| `void reject()` |  |
| `void setCallAudioState(android.telecom.CallAudioState)` |  |
| `void stopDtmfTone()` |  |
| `void unhold()` |  |
| `void unregisterCallback(android.telecom.RemoteConnection.Callback)` |  |
| `void onAddressChanged(android.telecom.RemoteConnection, android.net.Uri, int)` |  |
| `void onCallerDisplayNameChanged(android.telecom.RemoteConnection, String, int)` |  |
| `void onConferenceChanged(android.telecom.RemoteConnection, android.telecom.RemoteConference)` |  |
| `void onConferenceableConnectionsChanged(android.telecom.RemoteConnection, java.util.List<android.telecom.RemoteConnection>)` |  |
| `void onConnectionCapabilitiesChanged(android.telecom.RemoteConnection, int)` |  |
| `void onConnectionEvent(android.telecom.RemoteConnection, String, android.os.Bundle)` |  |
| `void onConnectionPropertiesChanged(android.telecom.RemoteConnection, int)` |  |
| `void onDestroyed(android.telecom.RemoteConnection)` |  |
| `void onDisconnected(android.telecom.RemoteConnection, android.telecom.DisconnectCause)` |  |
| `void onExtrasChanged(android.telecom.RemoteConnection, @Nullable android.os.Bundle)` |  |
| `void onPostDialChar(android.telecom.RemoteConnection, char)` |  |
| `void onPostDialWait(android.telecom.RemoteConnection, String)` |  |
| `void onRingbackRequested(android.telecom.RemoteConnection, boolean)` |  |
| `void onStateChanged(android.telecom.RemoteConnection, int)` |  |
| `void onStatusHintsChanged(android.telecom.RemoteConnection, android.telecom.StatusHints)` |  |
| `void onVideoProviderChanged(android.telecom.RemoteConnection, android.telecom.RemoteConnection.VideoProvider)` |  |
| `void onVideoStateChanged(android.telecom.RemoteConnection, int)` |  |
| `void onVoipAudioChanged(android.telecom.RemoteConnection, boolean)` |  |

---

### `class static RemoteConnection.VideoProvider`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteConnection.VideoProvider.Callback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void registerCallback(android.telecom.RemoteConnection.VideoProvider.Callback)` |  |
| `void requestCallDataUsage()` |  |
| `void requestCameraCapabilities()` |  |
| `void sendSessionModifyRequest(android.telecom.VideoProfile, android.telecom.VideoProfile)` |  |
| `void sendSessionModifyResponse(android.telecom.VideoProfile)` |  |
| `void setCamera(String)` |  |
| `void setDeviceOrientation(int)` |  |
| `void setDisplaySurface(android.view.Surface)` |  |
| `void setPauseImage(android.net.Uri)` |  |
| `void setPreviewSurface(android.view.Surface)` |  |
| `void setZoom(float)` |  |
| `void unregisterCallback(android.telecom.RemoteConnection.VideoProvider.Callback)` |  |
| `void onCallDataUsageChanged(android.telecom.RemoteConnection.VideoProvider, long)` |  |
| `void onCallSessionEvent(android.telecom.RemoteConnection.VideoProvider, int)` |  |
| `void onCameraCapabilitiesChanged(android.telecom.RemoteConnection.VideoProvider, android.telecom.VideoProfile.CameraCapabilities)` |  |
| `void onPeerDimensionsChanged(android.telecom.RemoteConnection.VideoProvider, int, int)` |  |
| `void onSessionModifyRequestReceived(android.telecom.RemoteConnection.VideoProvider, android.telecom.VideoProfile)` |  |
| `void onSessionModifyResponseReceived(android.telecom.RemoteConnection.VideoProvider, int, android.telecom.VideoProfile, android.telecom.VideoProfile)` |  |
| `void onVideoQualityChanged(android.telecom.RemoteConnection.VideoProvider, int)` |  |

---

### `class final StatusHints`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StatusHints(CharSequence, android.graphics.drawable.Icon, android.os.Bundle)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.graphics.drawable.Icon getIcon()` |  |
| `CharSequence getLabel()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class TelecomManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CHANGE_DEFAULT_DIALER = "android.telecom.action.CHANGE_DEFAULT_DIALER"` |  |
| `static final String ACTION_CHANGE_PHONE_ACCOUNTS = "android.telecom.action.CHANGE_PHONE_ACCOUNTS"` |  |
| `static final String ACTION_CONFIGURE_PHONE_ACCOUNT = "android.telecom.action.CONFIGURE_PHONE_ACCOUNT"` |  |
| `static final String ACTION_DEFAULT_CALL_SCREENING_APP_CHANGED = "android.telecom.action.DEFAULT_CALL_SCREENING_APP_CHANGED"` |  |
| `static final String ACTION_DEFAULT_DIALER_CHANGED = "android.telecom.action.DEFAULT_DIALER_CHANGED"` |  |
| `static final String ACTION_PHONE_ACCOUNT_REGISTERED = "android.telecom.action.PHONE_ACCOUNT_REGISTERED"` |  |
| `static final String ACTION_PHONE_ACCOUNT_UNREGISTERED = "android.telecom.action.PHONE_ACCOUNT_UNREGISTERED"` |  |
| `static final String ACTION_POST_CALL = "android.telecom.action.POST_CALL"` |  |
| `static final String ACTION_SHOW_CALL_ACCESSIBILITY_SETTINGS = "android.telecom.action.SHOW_CALL_ACCESSIBILITY_SETTINGS"` |  |
| `static final String ACTION_SHOW_CALL_SETTINGS = "android.telecom.action.SHOW_CALL_SETTINGS"` |  |
| `static final String ACTION_SHOW_MISSED_CALLS_NOTIFICATION = "android.telecom.action.SHOW_MISSED_CALLS_NOTIFICATION"` |  |
| `static final String ACTION_SHOW_RESPOND_VIA_SMS_SETTINGS = "android.telecom.action.SHOW_RESPOND_VIA_SMS_SETTINGS"` |  |
| `static final char DTMF_CHARACTER_PAUSE = 44` |  |
| `static final char DTMF_CHARACTER_WAIT = 59` |  |
| `static final int DURATION_LONG = 3` |  |
| `static final int DURATION_MEDIUM = 2` |  |
| `static final int DURATION_SHORT = 1` |  |
| `static final int DURATION_VERY_SHORT = 0` |  |
| `static final String EXTRA_CALL_BACK_NUMBER = "android.telecom.extra.CALL_BACK_NUMBER"` |  |
| `static final String EXTRA_CALL_DISCONNECT_CAUSE = "android.telecom.extra.CALL_DISCONNECT_CAUSE"` |  |
| `static final String EXTRA_CALL_DISCONNECT_MESSAGE = "android.telecom.extra.CALL_DISCONNECT_MESSAGE"` |  |
| `static final String EXTRA_CALL_DURATION = "android.telecom.extra.CALL_DURATION"` |  |
| `static final String EXTRA_CALL_NETWORK_TYPE = "android.telecom.extra.CALL_NETWORK_TYPE"` |  |
| `static final String EXTRA_CALL_SUBJECT = "android.telecom.extra.CALL_SUBJECT"` |  |
| `static final String EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME = "android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME"` |  |
| `static final String EXTRA_DEFAULT_CALL_SCREENING_APP_COMPONENT_NAME = "android.telecom.extra.DEFAULT_CALL_SCREENING_APP_COMPONENT_NAME"` |  |
| `static final String EXTRA_DISCONNECT_CAUSE = "android.telecom.extra.DISCONNECT_CAUSE"` |  |
| `static final String EXTRA_HANDLE = "android.telecom.extra.HANDLE"` |  |
| `static final String EXTRA_INCOMING_CALL_ADDRESS = "android.telecom.extra.INCOMING_CALL_ADDRESS"` |  |
| `static final String EXTRA_INCOMING_CALL_EXTRAS = "android.telecom.extra.INCOMING_CALL_EXTRAS"` |  |
| `static final String EXTRA_INCOMING_VIDEO_STATE = "android.telecom.extra.INCOMING_VIDEO_STATE"` |  |
| `static final String EXTRA_IS_DEFAULT_CALL_SCREENING_APP = "android.telecom.extra.IS_DEFAULT_CALL_SCREENING_APP"` |  |
| `static final String EXTRA_NOTIFICATION_COUNT = "android.telecom.extra.NOTIFICATION_COUNT"` |  |
| `static final String EXTRA_NOTIFICATION_PHONE_NUMBER = "android.telecom.extra.NOTIFICATION_PHONE_NUMBER"` |  |
| `static final String EXTRA_OUTGOING_CALL_EXTRAS = "android.telecom.extra.OUTGOING_CALL_EXTRAS"` |  |
| `static final String EXTRA_PHONE_ACCOUNT_HANDLE = "android.telecom.extra.PHONE_ACCOUNT_HANDLE"` |  |
| `static final String EXTRA_START_CALL_WITH_RTT = "android.telecom.extra.START_CALL_WITH_RTT"` |  |
| `static final String EXTRA_START_CALL_WITH_SPEAKERPHONE = "android.telecom.extra.START_CALL_WITH_SPEAKERPHONE"` |  |
| `static final String EXTRA_START_CALL_WITH_VIDEO_STATE = "android.telecom.extra.START_CALL_WITH_VIDEO_STATE"` |  |
| `static final String EXTRA_USE_ASSISTED_DIALING = "android.telecom.extra.USE_ASSISTED_DIALING"` |  |
| `static final String GATEWAY_ORIGINAL_ADDRESS = "android.telecom.extra.GATEWAY_ORIGINAL_ADDRESS"` |  |
| `static final String GATEWAY_PROVIDER_PACKAGE = "android.telecom.extra.GATEWAY_PROVIDER_PACKAGE"` |  |
| `static final String METADATA_INCLUDE_EXTERNAL_CALLS = "android.telecom.INCLUDE_EXTERNAL_CALLS"` |  |
| `static final String METADATA_INCLUDE_SELF_MANAGED_CALLS = "android.telecom.INCLUDE_SELF_MANAGED_CALLS"` |  |
| `static final String METADATA_IN_CALL_SERVICE_CAR_MODE_UI = "android.telecom.IN_CALL_SERVICE_CAR_MODE_UI"` |  |
| `static final String METADATA_IN_CALL_SERVICE_RINGING = "android.telecom.IN_CALL_SERVICE_RINGING"` |  |
| `static final String METADATA_IN_CALL_SERVICE_UI = "android.telecom.IN_CALL_SERVICE_UI"` |  |
| `static final int PRESENTATION_ALLOWED = 1` |  |
| `static final int PRESENTATION_PAYPHONE = 4` |  |
| `static final int PRESENTATION_RESTRICTED = 2` |  |
| `static final int PRESENTATION_UNKNOWN = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void acceptHandover(android.net.Uri, int, android.telecom.PhoneAccountHandle)` |  |
| `void addNewIncomingCall(android.telecom.PhoneAccountHandle, android.os.Bundle)` |  |
| `android.content.Intent createManageBlockedNumbersIntent()` |  |
| `String getDefaultDialerPackage()` |  |
| `android.telecom.PhoneAccount getPhoneAccount(android.telecom.PhoneAccountHandle)` |  |
| `android.telecom.PhoneAccountHandle getSimCallManager()` |  |
| `boolean isIncomingCallPermitted(android.telecom.PhoneAccountHandle)` |  |
| `boolean isOutgoingCallPermitted(android.telecom.PhoneAccountHandle)` |  |
| `void registerPhoneAccount(android.telecom.PhoneAccount)` |  |
| `void unregisterPhoneAccount(android.telecom.PhoneAccountHandle)` |  |

---

### `class VideoProfile`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VideoProfile(int)` |  |
| `VideoProfile(int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int QUALITY_DEFAULT = 4` |  |
| `static final int QUALITY_HIGH = 1` |  |
| `static final int QUALITY_LOW = 3` |  |
| `static final int QUALITY_MEDIUM = 2` |  |
| `static final int STATE_AUDIO_ONLY = 0` |  |
| `static final int STATE_BIDIRECTIONAL = 3` |  |
| `static final int STATE_PAUSED = 4` |  |
| `static final int STATE_RX_ENABLED = 2` |  |
| `static final int STATE_TX_ENABLED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getQuality()` |  |
| `int getVideoState()` |  |
| `static boolean isAudioOnly(int)` |  |
| `static boolean isBidirectional(int)` |  |
| `static boolean isPaused(int)` |  |
| `static boolean isReceptionEnabled(int)` |  |
| `static boolean isTransmissionEnabled(int)` |  |
| `static boolean isVideo(int)` |  |
| `static String videoStateToString(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final VideoProfile.CameraCapabilities`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VideoProfile.CameraCapabilities(@IntRange(from=0) int, @IntRange(from=0) int)` |  |
| `VideoProfile.CameraCapabilities(@IntRange(from=0) int, @IntRange(from=0) int, boolean, @FloatRange(from=1.0f) float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getHeight()` |  |
| `float getMaxZoom()` |  |
| `int getWidth()` |  |
| `boolean isZoomSupported()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.telephony`

### `class final AccessNetworkConstants`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TRANSPORT_TYPE_WLAN = 2` |  |
| `static final int TRANSPORT_TYPE_WWAN = 1` |  |

---

### `class static final AccessNetworkConstants.AccessNetworkType`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CDMA2000 = 4` |  |
| `static final int EUTRAN = 3` |  |
| `static final int GERAN = 1` |  |
| `static final int IWLAN = 5` |  |
| `static final int NGRAN = 6` |  |
| `static final int UNKNOWN = 0` |  |
| `static final int UTRAN = 2` |  |

---

### `class static final AccessNetworkConstants.EutranBand`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BAND_1 = 1` |  |
| `static final int BAND_10 = 10` |  |
| `static final int BAND_11 = 11` |  |
| `static final int BAND_12 = 12` |  |
| `static final int BAND_13 = 13` |  |
| `static final int BAND_14 = 14` |  |
| `static final int BAND_17 = 17` |  |
| `static final int BAND_18 = 18` |  |
| `static final int BAND_19 = 19` |  |
| `static final int BAND_2 = 2` |  |
| `static final int BAND_20 = 20` |  |
| `static final int BAND_21 = 21` |  |
| `static final int BAND_22 = 22` |  |
| `static final int BAND_23 = 23` |  |
| `static final int BAND_24 = 24` |  |
| `static final int BAND_25 = 25` |  |
| `static final int BAND_26 = 26` |  |
| `static final int BAND_27 = 27` |  |
| `static final int BAND_28 = 28` |  |
| `static final int BAND_3 = 3` |  |
| `static final int BAND_30 = 30` |  |
| `static final int BAND_31 = 31` |  |
| `static final int BAND_33 = 33` |  |
| `static final int BAND_34 = 34` |  |
| `static final int BAND_35 = 35` |  |
| `static final int BAND_36 = 36` |  |
| `static final int BAND_37 = 37` |  |
| `static final int BAND_38 = 38` |  |
| `static final int BAND_39 = 39` |  |
| `static final int BAND_4 = 4` |  |
| `static final int BAND_40 = 40` |  |
| `static final int BAND_41 = 41` |  |
| `static final int BAND_42 = 42` |  |
| `static final int BAND_43 = 43` |  |
| `static final int BAND_44 = 44` |  |
| `static final int BAND_45 = 45` |  |
| `static final int BAND_46 = 46` |  |
| `static final int BAND_47 = 47` |  |
| `static final int BAND_48 = 48` |  |
| `static final int BAND_49 = 49` |  |
| `static final int BAND_5 = 5` |  |
| `static final int BAND_50 = 50` |  |
| `static final int BAND_51 = 51` |  |
| `static final int BAND_52 = 52` |  |
| `static final int BAND_53 = 53` |  |
| `static final int BAND_6 = 6` |  |
| `static final int BAND_65 = 65` |  |
| `static final int BAND_66 = 66` |  |
| `static final int BAND_68 = 68` |  |
| `static final int BAND_7 = 7` |  |
| `static final int BAND_70 = 70` |  |
| `static final int BAND_71 = 71` |  |
| `static final int BAND_72 = 72` |  |
| `static final int BAND_73 = 73` |  |
| `static final int BAND_74 = 74` |  |
| `static final int BAND_8 = 8` |  |
| `static final int BAND_85 = 85` |  |
| `static final int BAND_87 = 87` |  |
| `static final int BAND_88 = 88` |  |
| `static final int BAND_9 = 9` |  |

---

### `class static final AccessNetworkConstants.GeranBand`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BAND_450 = 3` |  |
| `static final int BAND_480 = 4` |  |
| `static final int BAND_710 = 5` |  |
| `static final int BAND_750 = 6` |  |
| `static final int BAND_850 = 8` |  |
| `static final int BAND_DCS1800 = 12` |  |
| `static final int BAND_E900 = 10` |  |
| `static final int BAND_ER900 = 14` |  |
| `static final int BAND_P900 = 9` |  |
| `static final int BAND_PCS1900 = 13` |  |
| `static final int BAND_R900 = 11` |  |
| `static final int BAND_T380 = 1` |  |
| `static final int BAND_T410 = 2` |  |
| `static final int BAND_T810 = 7` |  |

---

### `class static final AccessNetworkConstants.NgranBands`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BAND_1 = 1` |  |
| `static final int BAND_12 = 12` |  |
| `static final int BAND_14 = 14` |  |
| `static final int BAND_18 = 18` |  |
| `static final int BAND_2 = 2` |  |
| `static final int BAND_20 = 20` |  |
| `static final int BAND_25 = 25` |  |
| `static final int BAND_257 = 257` |  |
| `static final int BAND_258 = 258` |  |
| `static final int BAND_260 = 260` |  |
| `static final int BAND_261 = 261` |  |
| `static final int BAND_28 = 28` |  |
| `static final int BAND_29 = 29` |  |
| `static final int BAND_3 = 3` |  |
| `static final int BAND_30 = 30` |  |
| `static final int BAND_34 = 34` |  |
| `static final int BAND_38 = 38` |  |
| `static final int BAND_39 = 39` |  |
| `static final int BAND_40 = 40` |  |
| `static final int BAND_41 = 41` |  |
| `static final int BAND_48 = 48` |  |
| `static final int BAND_5 = 5` |  |
| `static final int BAND_50 = 50` |  |
| `static final int BAND_51 = 51` |  |
| `static final int BAND_65 = 65` |  |
| `static final int BAND_66 = 66` |  |
| `static final int BAND_7 = 7` |  |
| `static final int BAND_70 = 70` |  |
| `static final int BAND_71 = 71` |  |
| `static final int BAND_74 = 74` |  |
| `static final int BAND_75 = 75` |  |
| `static final int BAND_76 = 76` |  |
| `static final int BAND_77 = 77` |  |
| `static final int BAND_78 = 78` |  |
| `static final int BAND_79 = 79` |  |
| `static final int BAND_8 = 8` |  |
| `static final int BAND_80 = 80` |  |
| `static final int BAND_81 = 81` |  |
| `static final int BAND_82 = 82` |  |
| `static final int BAND_83 = 83` |  |
| `static final int BAND_84 = 84` |  |
| `static final int BAND_86 = 86` |  |
| `static final int BAND_89 = 89` |  |
| `static final int BAND_90 = 90` |  |
| `static final int BAND_91 = 91` |  |
| `static final int BAND_92 = 92` |  |
| `static final int BAND_93 = 93` |  |
| `static final int BAND_94 = 94` |  |
| `static final int BAND_95 = 95` |  |

---

### `class static final AccessNetworkConstants.UtranBand`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BAND_1 = 1` |  |
| `static final int BAND_10 = 10` |  |
| `static final int BAND_11 = 11` |  |
| `static final int BAND_12 = 12` |  |
| `static final int BAND_13 = 13` |  |
| `static final int BAND_14 = 14` |  |
| `static final int BAND_19 = 19` |  |
| `static final int BAND_2 = 2` |  |
| `static final int BAND_20 = 20` |  |
| `static final int BAND_21 = 21` |  |
| `static final int BAND_22 = 22` |  |
| `static final int BAND_25 = 25` |  |
| `static final int BAND_26 = 26` |  |
| `static final int BAND_3 = 3` |  |
| `static final int BAND_4 = 4` |  |
| `static final int BAND_5 = 5` |  |
| `static final int BAND_6 = 6` |  |
| `static final int BAND_7 = 7` |  |
| `static final int BAND_8 = 8` |  |
| `static final int BAND_9 = 9` |  |
| `static final int BAND_A = 101` |  |
| `static final int BAND_B = 102` |  |
| `static final int BAND_C = 103` |  |
| `static final int BAND_D = 104` |  |
| `static final int BAND_E = 105` |  |
| `static final int BAND_F = 106` |  |

---

### `class final AvailableNetworkInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AvailableNetworkInfo(int, int, @NonNull java.util.List<java.lang.String>, @NonNull java.util.List<java.lang.Integer>)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int PRIORITY_HIGH = 1` |  |
| `static final int PRIORITY_LOW = 3` |  |
| `static final int PRIORITY_MED = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getPriority()` |  |
| `int getSubId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BarringInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BARRING_SERVICE_TYPE_CS_FALLBACK = 5` |  |
| `static final int BARRING_SERVICE_TYPE_CS_SERVICE = 0` |  |
| `static final int BARRING_SERVICE_TYPE_CS_VOICE = 2` |  |
| `static final int BARRING_SERVICE_TYPE_EMERGENCY = 8` |  |
| `static final int BARRING_SERVICE_TYPE_MMTEL_VIDEO = 7` |  |
| `static final int BARRING_SERVICE_TYPE_MMTEL_VOICE = 6` |  |
| `static final int BARRING_SERVICE_TYPE_MO_DATA = 4` |  |
| `static final int BARRING_SERVICE_TYPE_MO_SIGNALLING = 3` |  |
| `static final int BARRING_SERVICE_TYPE_PS_SERVICE = 1` |  |
| `static final int BARRING_SERVICE_TYPE_SMS = 9` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final BarringInfo.BarringServiceInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BARRING_TYPE_CONDITIONAL = 1` |  |
| `static final int BARRING_TYPE_NONE = 0` |  |
| `static final int BARRING_TYPE_UNCONDITIONAL = 2` |  |
| `static final int BARRING_TYPE_UNKNOWN = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getBarringType()` |  |
| `int getConditionalBarringFactor()` |  |
| `int getConditionalBarringTimeSeconds()` |  |
| `boolean isBarred()` |  |
| `boolean isConditionallyBarred()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class CarrierConfigManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CARRIER_CONFIG_CHANGED = "android.telephony.action.CARRIER_CONFIG_CHANGED"` |  |
| `static final int DATA_CYCLE_THRESHOLD_DISABLED = -2` |  |
| `static final int DATA_CYCLE_USE_PLATFORM_DEFAULT = -1` |  |
| `static final String ENABLE_EAP_METHOD_PREFIX_BOOL = "enable_eap_method_prefix_bool"` |  |
| `static final String EXTRA_SLOT_INDEX = "android.telephony.extra.SLOT_INDEX"` |  |
| `static final String EXTRA_SUBSCRIPTION_INDEX = "android.telephony.extra.SUBSCRIPTION_INDEX"` |  |
| `static final String IMSI_KEY_AVAILABILITY_INT = "imsi_key_availability_int"` |  |
| `static final String KEY_5G_NR_SSRSRP_THRESHOLDS_INT_ARRAY = "5g_nr_ssrsrp_thresholds_int_array"` |  |
| `static final String KEY_5G_NR_SSRSRQ_THRESHOLDS_INT_ARRAY = "5g_nr_ssrsrq_thresholds_int_array"` |  |
| `static final String KEY_5G_NR_SSSINR_THRESHOLDS_INT_ARRAY = "5g_nr_sssinr_thresholds_int_array"` |  |
| `static final String KEY_ADDITIONAL_CALL_SETTING_BOOL = "additional_call_setting_bool"` |  |
| `static final String KEY_ALLOW_ADDING_APNS_BOOL = "allow_adding_apns_bool"` |  |
| `static final String KEY_ALLOW_ADD_CALL_DURING_VIDEO_CALL_BOOL = "allow_add_call_during_video_call"` |  |
| `static final String KEY_ALLOW_EMERGENCY_NUMBERS_IN_CALL_LOG_BOOL = "allow_emergency_numbers_in_call_log_bool"` |  |
| `static final String KEY_ALLOW_EMERGENCY_VIDEO_CALLS_BOOL = "allow_emergency_video_calls_bool"` |  |
| `static final String KEY_ALLOW_HOLD_CALL_DURING_EMERGENCY_BOOL = "allow_hold_call_during_emergency_bool"` |  |
| `static final String KEY_ALLOW_HOLD_VIDEO_CALL_BOOL = "allow_hold_video_call_bool"` |  |
| `static final String KEY_ALLOW_LOCAL_DTMF_TONES_BOOL = "allow_local_dtmf_tones_bool"` |  |
| `static final String KEY_ALLOW_MERGE_WIFI_CALLS_WHEN_VOWIFI_OFF_BOOL = "allow_merge_wifi_calls_when_vowifi_off_bool"` |  |
| `static final String KEY_ALLOW_NON_EMERGENCY_CALLS_IN_ECM_BOOL = "allow_non_emergency_calls_in_ecm_bool"` |  |
| `static final String KEY_ALLOW_VIDEO_CALLING_FALLBACK_BOOL = "allow_video_calling_fallback_bool"` |  |
| `static final String KEY_ALWAYS_SHOW_DATA_RAT_ICON_BOOL = "always_show_data_rat_icon_bool"` |  |
| `static final String KEY_ALWAYS_SHOW_PRIMARY_SIGNAL_BAR_IN_OPPORTUNISTIC_NETWORK_BOOLEAN = "always_show_primary_signal_bar_in_opportunistic_network_boolean"` |  |
| `static final String KEY_APN_EXPAND_BOOL = "apn_expand_bool"` |  |
| `static final String KEY_APN_SETTINGS_DEFAULT_APN_TYPES_STRING_ARRAY = "apn_settings_default_apn_types_string_array"` |  |
| `static final String KEY_AUTO_RETRY_ENABLED_BOOL = "auto_retry_enabled_bool"` |  |
| `static final String KEY_CALL_BARRING_SUPPORTS_DEACTIVATE_ALL_BOOL = "call_barring_supports_deactivate_all_bool"` |  |
| `static final String KEY_CALL_BARRING_SUPPORTS_PASSWORD_CHANGE_BOOL = "call_barring_supports_password_change_bool"` |  |
| `static final String KEY_CALL_BARRING_VISIBILITY_BOOL = "call_barring_visibility_bool"` |  |
| `static final String KEY_CALL_FORWARDING_BLOCKS_WHILE_ROAMING_STRING_ARRAY = "call_forwarding_blocks_while_roaming_string_array"` |  |
| `static final String KEY_CALL_REDIRECTION_SERVICE_COMPONENT_NAME_STRING = "call_redirection_service_component_name_string"` |  |
| `static final String KEY_CARRIER_ALLOW_DEFLECT_IMS_CALL_BOOL = "carrier_allow_deflect_ims_call_bool"` |  |
| `static final String KEY_CARRIER_ALLOW_TURNOFF_IMS_BOOL = "carrier_allow_turnoff_ims_bool"` |  |
| `static final String KEY_CARRIER_APP_REQUIRED_DURING_SIM_SETUP_BOOL = "carrier_app_required_during_setup_bool"` |  |
| `static final String KEY_CARRIER_CALL_SCREENING_APP_STRING = "call_screening_app"` |  |
| `static final String KEY_CARRIER_CERTIFICATE_STRING_ARRAY = "carrier_certificate_string_array"` |  |
| `static final String KEY_CARRIER_CONFIG_APPLIED_BOOL = "carrier_config_applied_bool"` |  |
| `static final String KEY_CARRIER_CONFIG_VERSION_STRING = "carrier_config_version_string"` |  |
| `static final String KEY_CARRIER_DATA_CALL_PERMANENT_FAILURE_STRINGS = "carrier_data_call_permanent_failure_strings"` |  |
| `static final String KEY_CARRIER_DEFAULT_ACTIONS_ON_DCFAILURE_STRING_ARRAY = "carrier_default_actions_on_dcfailure_string_array"` |  |
| `static final String KEY_CARRIER_DEFAULT_ACTIONS_ON_DEFAULT_NETWORK_AVAILABLE = "carrier_default_actions_on_default_network_available_string_array"` |  |
| `static final String KEY_CARRIER_DEFAULT_ACTIONS_ON_REDIRECTION_STRING_ARRAY = "carrier_default_actions_on_redirection_string_array"` |  |
| `static final String KEY_CARRIER_DEFAULT_ACTIONS_ON_RESET = "carrier_default_actions_on_reset_string_array"` |  |
| `static final String KEY_CARRIER_DEFAULT_REDIRECTION_URL_STRING_ARRAY = "carrier_default_redirection_url_string_array"` |  |
| `static final String KEY_CARRIER_DEFAULT_WFC_IMS_ENABLED_BOOL = "carrier_default_wfc_ims_enabled_bool"` |  |
| `static final String KEY_CARRIER_DEFAULT_WFC_IMS_MODE_INT = "carrier_default_wfc_ims_mode_int"` |  |
| `static final String KEY_CARRIER_DEFAULT_WFC_IMS_ROAMING_MODE_INT = "carrier_default_wfc_ims_roaming_mode_int"` |  |
| `static final String KEY_CARRIER_IMS_GBA_REQUIRED_BOOL = "carrier_ims_gba_required_bool"` |  |
| `static final String KEY_CARRIER_INSTANT_LETTERING_AVAILABLE_BOOL = "carrier_instant_lettering_available_bool"` |  |
| `static final String KEY_CARRIER_INSTANT_LETTERING_ENCODING_STRING = "carrier_instant_lettering_encoding_string"` |  |
| `static final String KEY_CARRIER_INSTANT_LETTERING_ESCAPED_CHARS_STRING = "carrier_instant_lettering_escaped_chars_string"` |  |
| `static final String KEY_CARRIER_INSTANT_LETTERING_INVALID_CHARS_STRING = "carrier_instant_lettering_invalid_chars_string"` |  |
| `static final String KEY_CARRIER_INSTANT_LETTERING_LENGTH_LIMIT_INT = "carrier_instant_lettering_length_limit_int"` |  |
| `static final String KEY_CARRIER_NAME_OVERRIDE_BOOL = "carrier_name_override_bool"` |  |
| `static final String KEY_CARRIER_NAME_STRING = "carrier_name_string"` |  |
| `static final String KEY_CARRIER_RCS_PROVISIONING_REQUIRED_BOOL = "carrier_rcs_provisioning_required_bool"` |  |
| `static final String KEY_CARRIER_SETTINGS_ACTIVITY_COMPONENT_NAME_STRING = "carrier_settings_activity_component_name_string"` |  |
| `static final String KEY_CARRIER_SETTINGS_ENABLE_BOOL = "carrier_settings_enable_bool"` |  |
| `static final String KEY_CARRIER_SUPPORTS_SS_OVER_UT_BOOL = "carrier_supports_ss_over_ut_bool"` |  |
| `static final String KEY_CARRIER_USE_IMS_FIRST_FOR_EMERGENCY_BOOL = "carrier_use_ims_first_for_emergency_bool"` |  |
| `static final String KEY_CARRIER_UT_PROVISIONING_REQUIRED_BOOL = "carrier_ut_provisioning_required_bool"` |  |
| `static final String KEY_CARRIER_VOLTE_AVAILABLE_BOOL = "carrier_volte_available_bool"` |  |
| `static final String KEY_CARRIER_VOLTE_OVERRIDE_WFC_PROVISIONING_BOOL = "carrier_volte_override_wfc_provisioning_bool"` |  |
| `static final String KEY_CARRIER_VOLTE_PROVISIONED_BOOL = "carrier_volte_provisioned_bool"` |  |
| `static final String KEY_CARRIER_VOLTE_PROVISIONING_REQUIRED_BOOL = "carrier_volte_provisioning_required_bool"` |  |
| `static final String KEY_CARRIER_VOLTE_TTY_SUPPORTED_BOOL = "carrier_volte_tty_supported_bool"` |  |
| `static final String KEY_CARRIER_VT_AVAILABLE_BOOL = "carrier_vt_available_bool"` |  |
| `static final String KEY_CARRIER_VVM_PACKAGE_NAME_STRING_ARRAY = "carrier_vvm_package_name_string_array"` |  |
| `static final String KEY_CARRIER_WFC_IMS_AVAILABLE_BOOL = "carrier_wfc_ims_available_bool"` |  |
| `static final String KEY_CARRIER_WFC_SUPPORTS_WIFI_ONLY_BOOL = "carrier_wfc_supports_wifi_only_bool"` |  |
| `static final String KEY_CDMA_3WAYCALL_FLASH_DELAY_INT = "cdma_3waycall_flash_delay_int"` |  |
| `static final String KEY_CDMA_DTMF_TONE_DELAY_INT = "cdma_dtmf_tone_delay_int"` |  |
| `static final String KEY_CDMA_NONROAMING_NETWORKS_STRING_ARRAY = "cdma_nonroaming_networks_string_array"` |  |
| `static final String KEY_CDMA_ROAMING_MODE_INT = "cdma_roaming_mode_int"` |  |
| `static final String KEY_CDMA_ROAMING_NETWORKS_STRING_ARRAY = "cdma_roaming_networks_string_array"` |  |
| `static final String KEY_CHECK_PRICING_WITH_CARRIER_FOR_DATA_ROAMING_BOOL = "check_pricing_with_carrier_data_roaming_bool"` |  |
| `static final String KEY_CI_ACTION_ON_SYS_UPDATE_BOOL = "ci_action_on_sys_update_bool"` |  |
| `static final String KEY_CI_ACTION_ON_SYS_UPDATE_EXTRA_STRING = "ci_action_on_sys_update_extra_string"` |  |
| `static final String KEY_CI_ACTION_ON_SYS_UPDATE_EXTRA_VAL_STRING = "ci_action_on_sys_update_extra_val_string"` |  |
| `static final String KEY_CI_ACTION_ON_SYS_UPDATE_INTENT_STRING = "ci_action_on_sys_update_intent_string"` |  |
| `static final String KEY_CONFIG_IMS_MMTEL_PACKAGE_OVERRIDE_STRING = "config_ims_mmtel_package_override_string"` |  |
| `static final String KEY_CONFIG_IMS_RCS_PACKAGE_OVERRIDE_STRING = "config_ims_rcs_package_override_string"` |  |
| `static final String KEY_CONFIG_PLANS_PACKAGE_OVERRIDE_STRING = "config_plans_package_override_string"` |  |
| `static final String KEY_CONFIG_TELEPHONY_USE_OWN_NUMBER_FOR_VOICEMAIL_BOOL = "config_telephony_use_own_number_for_voicemail_bool"` |  |
| `static final String KEY_CONFIG_WIFI_DISABLE_IN_ECBM = "config_wifi_disable_in_ecbm"` |  |
| `static final String KEY_CSP_ENABLED_BOOL = "csp_enabled_bool"` |  |
| `static final String KEY_DATA_LIMIT_NOTIFICATION_BOOL = "data_limit_notification_bool"` |  |
| `static final String KEY_DATA_LIMIT_THRESHOLD_BYTES_LONG = "data_limit_threshold_bytes_long"` |  |
| `static final String KEY_DATA_RAPID_NOTIFICATION_BOOL = "data_rapid_notification_bool"` |  |
| `static final String KEY_DATA_SWITCH_VALIDATION_TIMEOUT_LONG = "data_switch_validation_timeout_long"` |  |
| `static final String KEY_DATA_WARNING_NOTIFICATION_BOOL = "data_warning_notification_bool"` |  |
| `static final String KEY_DATA_WARNING_THRESHOLD_BYTES_LONG = "data_warning_threshold_bytes_long"` |  |
| `static final String KEY_DEFAULT_SIM_CALL_MANAGER_STRING = "default_sim_call_manager_string"` |  |
| `static final String KEY_DEFAULT_VM_NUMBER_ROAMING_AND_IMS_UNREGISTERED_STRING = "default_vm_number_roaming_and_ims_unregistered_string"` |  |
| `static final String KEY_DEFAULT_VM_NUMBER_STRING = "default_vm_number_string"` |  |
| `static final String KEY_DIAL_STRING_REPLACE_STRING_ARRAY = "dial_string_replace_string_array"` |  |
| `static final String KEY_DISABLE_CDMA_ACTIVATION_CODE_BOOL = "disable_cdma_activation_code_bool"` |  |
| `static final String KEY_DISABLE_CHARGE_INDICATION_BOOL = "disable_charge_indication_bool"` |  |
| `static final String KEY_DISABLE_SUPPLEMENTARY_SERVICES_IN_AIRPLANE_MODE_BOOL = "disable_supplementary_services_in_airplane_mode_bool"` |  |
| `static final String KEY_DISCONNECT_CAUSE_PLAY_BUSYTONE_INT_ARRAY = "disconnect_cause_play_busytone_int_array"` |  |
| `static final String KEY_DISPLAY_HD_AUDIO_PROPERTY_BOOL = "display_hd_audio_property_bool"` |  |
| `static final String KEY_DROP_VIDEO_CALL_WHEN_ANSWERING_AUDIO_CALL_BOOL = "drop_video_call_when_answering_audio_call_bool"` |  |
| `static final String KEY_DTMF_TYPE_ENABLED_BOOL = "dtmf_type_enabled_bool"` |  |
| `static final String KEY_DURATION_BLOCKING_DISABLED_AFTER_EMERGENCY_INT = "duration_blocking_disabled_after_emergency_int"` |  |
| `static final String KEY_EDITABLE_ENHANCED_4G_LTE_BOOL = "editable_enhanced_4g_lte_bool"` |  |
| `static final String KEY_EDITABLE_VOICEMAIL_NUMBER_BOOL = "editable_voicemail_number_bool"` |  |
| `static final String KEY_EDITABLE_VOICEMAIL_NUMBER_SETTING_BOOL = "editable_voicemail_number_setting_bool"` |  |
| `static final String KEY_EDITABLE_WFC_MODE_BOOL = "editable_wfc_mode_bool"` |  |
| `static final String KEY_EDITABLE_WFC_ROAMING_MODE_BOOL = "editable_wfc_roaming_mode_bool"` |  |
| `static final String KEY_EMERGENCY_NOTIFICATION_DELAY_INT = "emergency_notification_delay_int"` |  |
| `static final String KEY_EMERGENCY_NUMBER_PREFIX_STRING_ARRAY = "emergency_number_prefix_string_array"` |  |
| `static final String KEY_ENABLE_DIALER_KEY_VIBRATION_BOOL = "enable_dialer_key_vibration_bool"` |  |
| `static final String KEY_ENHANCED_4G_LTE_ON_BY_DEFAULT_BOOL = "enhanced_4g_lte_on_by_default_bool"` |  |
| `static final String KEY_ENHANCED_4G_LTE_TITLE_VARIANT_INT = "enhanced_4g_lte_title_variant_int"` |  |
| `static final String KEY_FORCE_HOME_NETWORK_BOOL = "force_home_network_bool"` |  |
| `static final String KEY_GSM_DTMF_TONE_DELAY_INT = "gsm_dtmf_tone_delay_int"` |  |
| `static final String KEY_GSM_NONROAMING_NETWORKS_STRING_ARRAY = "gsm_nonroaming_networks_string_array"` |  |
| `static final String KEY_GSM_ROAMING_NETWORKS_STRING_ARRAY = "gsm_roaming_networks_string_array"` |  |
| `static final String KEY_HAS_IN_CALL_NOISE_SUPPRESSION_BOOL = "has_in_call_noise_suppression_bool"` |  |
| `static final String KEY_HIDE_CARRIER_NETWORK_SETTINGS_BOOL = "hide_carrier_network_settings_bool"` |  |
| `static final String KEY_HIDE_ENHANCED_4G_LTE_BOOL = "hide_enhanced_4g_lte_bool"` |  |
| `static final String KEY_HIDE_IMS_APN_BOOL = "hide_ims_apn_bool"` |  |
| `static final String KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL = "hide_lte_plus_data_icon_bool"` |  |
| `static final String KEY_HIDE_PREFERRED_NETWORK_TYPE_BOOL = "hide_preferred_network_type_bool"` |  |
| `static final String KEY_HIDE_PRESET_APN_DETAILS_BOOL = "hide_preset_apn_details_bool"` |  |
| `static final String KEY_HIDE_SIM_LOCK_SETTINGS_BOOL = "hide_sim_lock_settings_bool"` |  |
| `static final String KEY_IGNORE_DATA_ENABLED_CHANGED_FOR_VIDEO_CALLS = "ignore_data_enabled_changed_for_video_calls"` |  |
| `static final String KEY_IGNORE_RTT_MODE_SETTING_BOOL = "ignore_rtt_mode_setting_bool"` |  |
| `static final String KEY_IGNORE_SIM_NETWORK_LOCKED_EVENTS_BOOL = "ignore_sim_network_locked_events_bool"` |  |
| `static final String KEY_IMS_CONFERENCE_SIZE_LIMIT_INT = "ims_conference_size_limit_int"` |  |
| `static final String KEY_IMS_DTMF_TONE_DELAY_INT = "ims_dtmf_tone_delay_int"` |  |
| `static final String KEY_IS_IMS_CONFERENCE_SIZE_ENFORCED_BOOL = "is_ims_conference_size_enforced_bool"` |  |
| `static final String KEY_LTE_ENABLED_BOOL = "lte_enabled_bool"` |  |
| `static final String KEY_LTE_RSRQ_THRESHOLDS_INT_ARRAY = "lte_rsrq_thresholds_int_array"` |  |
| `static final String KEY_LTE_RSSNR_THRESHOLDS_INT_ARRAY = "lte_rssnr_thresholds_int_array"` |  |
| `static final String KEY_MDN_IS_ADDITIONAL_VOICEMAIL_NUMBER_BOOL = "mdn_is_additional_voicemail_number_bool"` |  |
| `static final String KEY_MMS_ALIAS_ENABLED_BOOL = "aliasEnabled"` |  |
| `static final String KEY_MMS_ALIAS_MAX_CHARS_INT = "aliasMaxChars"` |  |
| `static final String KEY_MMS_ALIAS_MIN_CHARS_INT = "aliasMinChars"` |  |
| `static final String KEY_MMS_ALLOW_ATTACH_AUDIO_BOOL = "allowAttachAudio"` |  |
| `static final String KEY_MMS_APPEND_TRANSACTION_ID_BOOL = "enabledTransID"` |  |
| `static final String KEY_MMS_CLOSE_CONNECTION_BOOL = "mmsCloseConnection"` |  |
| `static final String KEY_MMS_EMAIL_GATEWAY_NUMBER_STRING = "emailGatewayNumber"` |  |
| `static final String KEY_MMS_GROUP_MMS_ENABLED_BOOL = "enableGroupMms"` |  |
| `static final String KEY_MMS_HTTP_PARAMS_STRING = "httpParams"` |  |
| `static final String KEY_MMS_HTTP_SOCKET_TIMEOUT_INT = "httpSocketTimeout"` |  |
| `static final String KEY_MMS_MAX_IMAGE_HEIGHT_INT = "maxImageHeight"` |  |
| `static final String KEY_MMS_MAX_IMAGE_WIDTH_INT = "maxImageWidth"` |  |
| `static final String KEY_MMS_MAX_MESSAGE_SIZE_INT = "maxMessageSize"` |  |
| `static final String KEY_MMS_MESSAGE_TEXT_MAX_SIZE_INT = "maxMessageTextSize"` |  |
| `static final String KEY_MMS_MMS_DELIVERY_REPORT_ENABLED_BOOL = "enableMMSDeliveryReports"` |  |
| `static final String KEY_MMS_MMS_ENABLED_BOOL = "enabledMMS"` |  |
| `static final String KEY_MMS_MMS_READ_REPORT_ENABLED_BOOL = "enableMMSReadReports"` |  |
| `static final String KEY_MMS_MULTIPART_SMS_ENABLED_BOOL = "enableMultipartSMS"` |  |
| `static final String KEY_MMS_NAI_SUFFIX_STRING = "naiSuffix"` |  |
| `static final String KEY_MMS_NOTIFY_WAP_MMSC_ENABLED_BOOL = "enabledNotifyWapMMSC"` |  |
| `static final String KEY_MMS_RECIPIENT_LIMIT_INT = "recipientLimit"` |  |
| `static final String KEY_MMS_SEND_MULTIPART_SMS_AS_SEPARATE_MESSAGES_BOOL = "sendMultipartSmsAsSeparateMessages"` |  |
| `static final String KEY_MMS_SHOW_CELL_BROADCAST_APP_LINKS_BOOL = "config_cellBroadcastAppLinks"` |  |
| `static final String KEY_MMS_SMS_DELIVERY_REPORT_ENABLED_BOOL = "enableSMSDeliveryReports"` |  |
| `static final String KEY_MMS_SMS_TO_MMS_TEXT_LENGTH_THRESHOLD_INT = "smsToMmsTextLengthThreshold"` |  |
| `static final String KEY_MMS_SMS_TO_MMS_TEXT_THRESHOLD_INT = "smsToMmsTextThreshold"` |  |
| `static final String KEY_MMS_SUBJECT_MAX_LENGTH_INT = "maxSubjectLength"` |  |
| `static final String KEY_MMS_SUPPORT_HTTP_CHARSET_HEADER_BOOL = "supportHttpCharsetHeader"` |  |
| `static final String KEY_MMS_SUPPORT_MMS_CONTENT_DISPOSITION_BOOL = "supportMmsContentDisposition"` |  |
| `static final String KEY_MMS_UA_PROF_TAG_NAME_STRING = "uaProfTagName"` |  |
| `static final String KEY_MMS_UA_PROF_URL_STRING = "uaProfUrl"` |  |
| `static final String KEY_MMS_USER_AGENT_STRING = "userAgent"` |  |
| `static final String KEY_MONTHLY_DATA_CYCLE_DAY_INT = "monthly_data_cycle_day_int"` |  |
| `static final String KEY_ONLY_AUTO_SELECT_IN_HOME_NETWORK_BOOL = "only_auto_select_in_home_network"` |  |
| `static final String KEY_ONLY_SINGLE_DC_ALLOWED_INT_ARRAY = "only_single_dc_allowed_int_array"` |  |
| `static final String KEY_OPERATOR_SELECTION_EXPAND_BOOL = "operator_selection_expand_bool"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_BACKOFF_TIME_LONG = "opportunistic_network_backoff_time_long"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_DATA_SWITCH_EXIT_HYSTERESIS_TIME_LONG = "opportunistic_network_data_switch_exit_hysteresis_time_long"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_DATA_SWITCH_HYSTERESIS_TIME_LONG = "opportunistic_network_data_switch_hysteresis_time_long"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_ENTRY_OR_EXIT_HYSTERESIS_TIME_LONG = "opportunistic_network_entry_or_exit_hysteresis_time_long"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_ENTRY_THRESHOLD_BANDWIDTH_INT = "opportunistic_network_entry_threshold_bandwidth_int"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_ENTRY_THRESHOLD_RSRP_INT = "opportunistic_network_entry_threshold_rsrp_int"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_ENTRY_THRESHOLD_RSSNR_INT = "opportunistic_network_entry_threshold_rssnr_int"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_EXIT_THRESHOLD_RSRP_INT = "opportunistic_network_exit_threshold_rsrp_int"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_EXIT_THRESHOLD_RSSNR_INT = "opportunistic_network_exit_threshold_rssnr_int"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_MAX_BACKOFF_TIME_LONG = "opportunistic_network_max_backoff_time_long"` |  |
| `static final String KEY_OPPORTUNISTIC_NETWORK_PING_PONG_TIME_LONG = "opportunistic_network_ping_pong_time_long"` |  |
| `static final String KEY_PING_TEST_BEFORE_DATA_SWITCH_BOOL = "ping_test_before_data_switch_bool"` |  |
| `static final String KEY_PREFER_2G_BOOL = "prefer_2g_bool"` |  |
| `static final String KEY_PREVENT_CLIR_ACTIVATION_AND_DEACTIVATION_CODE_BOOL = "prevent_clir_activation_and_deactivation_code_bool"` |  |
| `static final String KEY_RADIO_RESTART_FAILURE_CAUSES_INT_ARRAY = "radio_restart_failure_causes_int_array"` |  |
| `static final String KEY_RCS_CONFIG_SERVER_URL_STRING = "rcs_config_server_url_string"` |  |
| `static final String KEY_READ_ONLY_APN_FIELDS_STRING_ARRAY = "read_only_apn_fields_string_array"` |  |
| `static final String KEY_READ_ONLY_APN_TYPES_STRING_ARRAY = "read_only_apn_types_string_array"` |  |
| `static final String KEY_REQUIRE_ENTITLEMENT_CHECKS_BOOL = "require_entitlement_checks_bool"` |  |
| `static final String KEY_RTT_SUPPORTED_BOOL = "rtt_supported_bool"` |  |
| `static final String KEY_SHOW_4G_FOR_3G_DATA_ICON_BOOL = "show_4g_for_3g_data_icon_bool"` |  |
| `static final String KEY_SHOW_4G_FOR_LTE_DATA_ICON_BOOL = "show_4g_for_lte_data_icon_bool"` |  |
| `static final String KEY_SHOW_APN_SETTING_CDMA_BOOL = "show_apn_setting_cdma_bool"` |  |
| `static final String KEY_SHOW_BLOCKING_PAY_PHONE_OPTION_BOOL = "show_blocking_pay_phone_option_bool"` |  |
| `static final String KEY_SHOW_CALL_BLOCKING_DISABLED_NOTIFICATION_ALWAYS_BOOL = "show_call_blocking_disabled_notification_always_bool"` |  |
| `static final String KEY_SHOW_CDMA_CHOICES_BOOL = "show_cdma_choices_bool"` |  |
| `static final String KEY_SHOW_FORWARDED_NUMBER_BOOL = "show_forwarded_number_bool"` |  |
| `static final String KEY_SHOW_ICCID_IN_SIM_STATUS_BOOL = "show_iccid_in_sim_status_bool"` |  |
| `static final String KEY_SHOW_IMS_REGISTRATION_STATUS_BOOL = "show_ims_registration_status_bool"` |  |
| `static final String KEY_SHOW_ONSCREEN_DIAL_BUTTON_BOOL = "show_onscreen_dial_button_bool"` |  |
| `static final String KEY_SHOW_SIGNAL_STRENGTH_IN_SIM_STATUS_BOOL = "show_signal_strength_in_sim_status_bool"` |  |
| `static final String KEY_SHOW_VIDEO_CALL_CHARGES_ALERT_DIALOG_BOOL = "show_video_call_charges_alert_dialog_bool"` |  |
| `static final String KEY_SHOW_WFC_LOCATION_PRIVACY_POLICY_BOOL = "show_wfc_location_privacy_policy_bool"` |  |
| `static final String KEY_SIMPLIFIED_NETWORK_SETTINGS_BOOL = "simplified_network_settings_bool"` |  |
| `static final String KEY_SIM_NETWORK_UNLOCK_ALLOW_DISMISS_BOOL = "sim_network_unlock_allow_dismiss_bool"` |  |
| `static final String KEY_SMS_REQUIRES_DESTINATION_NUMBER_CONVERSION_BOOL = "sms_requires_destination_number_conversion_bool"` |  |
| `static final String KEY_SUPPORT_3GPP_CALL_FORWARDING_WHILE_ROAMING_BOOL = "support_3gpp_call_forwarding_while_roaming_bool"` |  |
| `static final String KEY_SUPPORT_CLIR_NETWORK_DEFAULT_BOOL = "support_clir_network_default_bool"` |  |
| `static final String KEY_SUPPORT_CONFERENCE_CALL_BOOL = "support_conference_call_bool"` |  |
| `static final String KEY_SUPPORT_EMERGENCY_SMS_OVER_IMS_BOOL = "support_emergency_sms_over_ims_bool"` |  |
| `static final String KEY_SUPPORT_ENHANCED_CALL_BLOCKING_BOOL = "support_enhanced_call_blocking_bool"` |  |
| `static final String KEY_SUPPORT_IMS_CONFERENCE_EVENT_PACKAGE_BOOL = "support_ims_conference_event_package_bool"` |  |
| `static final String KEY_SUPPORT_PAUSE_IMS_VIDEO_CALLS_BOOL = "support_pause_ims_video_calls_bool"` |  |
| `static final String KEY_SUPPORT_SWAP_AFTER_MERGE_BOOL = "support_swap_after_merge_bool"` |  |
| `static final String KEY_SUPPORT_TDSCDMA_BOOL = "support_tdscdma_bool"` |  |
| `static final String KEY_SUPPORT_TDSCDMA_ROAMING_NETWORKS_STRING_ARRAY = "support_tdscdma_roaming_networks_string_array"` |  |
| `static final String KEY_SWITCH_DATA_TO_PRIMARY_IF_PRIMARY_IS_OOS_BOOL = "switch_data_to_primary_if_primary_is_oos_bool"` |  |
| `static final String KEY_TREAT_DOWNGRADED_VIDEO_CALLS_AS_VIDEO_CALLS_BOOL = "treat_downgraded_video_calls_as_video_calls_bool"` |  |
| `static final String KEY_TTY_SUPPORTED_BOOL = "tty_supported_bool"` |  |
| `static final String KEY_UNLOGGABLE_NUMBERS_STRING_ARRAY = "unloggable_numbers_string_array"` |  |
| `static final String KEY_USE_HFA_FOR_PROVISIONING_BOOL = "use_hfa_for_provisioning_bool"` |  |
| `static final String KEY_USE_OTASP_FOR_PROVISIONING_BOOL = "use_otasp_for_provisioning_bool"` |  |
| `static final String KEY_USE_RCS_PRESENCE_BOOL = "use_rcs_presence_bool"` |  |
| `static final String KEY_USE_RCS_SIP_OPTIONS_BOOL = "use_rcs_sip_options_bool"` |  |
| `static final String KEY_USE_WFC_HOME_NETWORK_MODE_IN_ROAMING_NETWORK_BOOL = "use_wfc_home_network_mode_in_roaming_network_bool"` |  |
| `static final String KEY_VOICEMAIL_NOTIFICATION_PERSISTENT_BOOL = "voicemail_notification_persistent_bool"` |  |
| `static final String KEY_VOICE_PRIVACY_DISABLE_UI_BOOL = "voice_privacy_disable_ui_bool"` |  |
| `static final String KEY_VOLTE_REPLACEMENT_RAT_INT = "volte_replacement_rat_int"` |  |
| `static final String KEY_VVM_CELLULAR_DATA_REQUIRED_BOOL = "vvm_cellular_data_required_bool"` |  |
| `static final String KEY_VVM_CLIENT_PREFIX_STRING = "vvm_client_prefix_string"` |  |
| `static final String KEY_VVM_DESTINATION_NUMBER_STRING = "vvm_destination_number_string"` |  |
| `static final String KEY_VVM_DISABLED_CAPABILITIES_STRING_ARRAY = "vvm_disabled_capabilities_string_array"` |  |
| `static final String KEY_VVM_LEGACY_MODE_ENABLED_BOOL = "vvm_legacy_mode_enabled_bool"` |  |
| `static final String KEY_VVM_PORT_NUMBER_INT = "vvm_port_number_int"` |  |
| `static final String KEY_VVM_PREFETCH_BOOL = "vvm_prefetch_bool"` |  |
| `static final String KEY_VVM_SSL_ENABLED_BOOL = "vvm_ssl_enabled_bool"` |  |
| `static final String KEY_VVM_TYPE_STRING = "vvm_type_string"` |  |
| `static final String KEY_WFC_EMERGENCY_ADDRESS_CARRIER_APP_STRING = "wfc_emergency_address_carrier_app_string"` |  |
| `static final String KEY_WORLD_MODE_ENABLED_BOOL = "world_mode_enabled_bool"` |  |
| `static final String KEY_WORLD_PHONE_BOOL = "world_phone_bool"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isConfigForIdentifiedCarrier(android.os.PersistableBundle)` |  |
| `void notifyConfigChangedForSubId(int)` |  |

---

### `class static final CarrierConfigManager.Apn`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY_PREFIX = "apn."` |  |
| `static final String KEY_SETTINGS_DEFAULT_PROTOCOL_STRING = "apn.settings_default_protocol_string"` |  |
| `static final String KEY_SETTINGS_DEFAULT_ROAMING_PROTOCOL_STRING = "apn.settings_default_roaming_protocol_string"` |  |
| `static final String PROTOCOL_IPV4 = "IP"` |  |
| `static final String PROTOCOL_IPV4V6 = "IPV4V6"` |  |
| `static final String PROTOCOL_IPV6 = "IPV6"` |  |

---

### `class static final CarrierConfigManager.Gps`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY_PERSIST_LPP_MODE_BOOL = "gps.persist_lpp_mode_bool"` |  |
| `static final String KEY_PREFIX = "gps."` |  |

---

### `class static final CarrierConfigManager.Ims`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY_PREFIX = "ims."` |  |
| `static final String KEY_WIFI_OFF_DEFERRING_TIME_MILLIS_INT = "ims.wifi_off_deferring_time_millis_int"` |  |

---

### `class abstract CellIdentity`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |

---

### `class final CellIdentityCdma`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getBasestationId()` |  |
| `int getLatitude()` |  |
| `int getLongitude()` |  |
| `int getNetworkId()` |  |
| `int getSystemId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellIdentityGsm`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getArfcn()` |  |
| `int getBsic()` |  |
| `int getCid()` |  |
| `int getLac()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellIdentityLte`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getBandwidth()` |  |
| `int getCi()` |  |
| `int getEarfcn()` |  |
| `int getPci()` |  |
| `int getTac()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellIdentityNr`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getNci()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellIdentityTdscdma`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCid()` |  |
| `int getCpid()` |  |
| `int getLac()` |  |
| `int getUarfcn()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellIdentityWcdma`

- **继承：** `android.telephony.CellIdentity`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCid()` |  |
| `int getLac()` |  |
| `int getPsc()` |  |
| `int getUarfcn()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract CellInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CONNECTION_NONE = 0` |  |
| `static final int CONNECTION_PRIMARY_SERVING = 1` |  |
| `static final int CONNECTION_SECONDARY_SERVING = 2` |  |
| `static final int CONNECTION_UNKNOWN = 2147483647` |  |
| `static final int UNAVAILABLE = 2147483647` |  |
| `static final long UNAVAILABLE_LONG = 9223372036854775807L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCellConnectionStatus()` |  |
| `long getTimestampMillis()` |  |
| `boolean isRegistered()` |  |

---

### `class final CellInfoCdma`

- **继承：** `android.telephony.CellInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellInfoGsm`

- **继承：** `android.telephony.CellInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellInfoLte`

- **继承：** `android.telephony.CellInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellInfoNr`

- **继承：** `android.telephony.CellInfo`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellInfoTdscdma`

- **继承：** `android.telephony.CellInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellInfoWcdma`

- **继承：** `android.telephony.CellInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telephony.CellIdentityWcdma getCellIdentity()` |  |
| `android.telephony.CellSignalStrengthWcdma getCellSignalStrength()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract CellLocation`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CellLocation()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.telephony.CellLocation getEmpty()` |  |
| `static void requestLocationUpdate()` |  |

---

### `class abstract CellSignalStrength`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SIGNAL_STRENGTH_GOOD = 3` |  |
| `static final int SIGNAL_STRENGTH_GREAT = 4` |  |
| `static final int SIGNAL_STRENGTH_MODERATE = 2` |  |
| `static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0` |  |
| `static final int SIGNAL_STRENGTH_POOR = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract boolean equals(Object)` |  |
| `abstract int getAsuLevel()` |  |
| `abstract int getDbm()` |  |
| `abstract int hashCode()` |  |

---

### `class final CellSignalStrengthCdma`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getCdmaDbm()` |  |
| `int getCdmaEcio()` |  |
| `int getCdmaLevel()` |  |
| `int getDbm()` |  |
| `int getEvdoDbm()` |  |
| `int getEvdoEcio()` |  |
| `int getEvdoLevel()` |  |
| `int getEvdoSnr()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellSignalStrengthGsm`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getBitErrorRate()` |  |
| `int getDbm()` |  |
| `int getRssi()` |  |
| `int getTimingAdvance()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellSignalStrengthLte`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getCqi()` |  |
| `int getDbm()` |  |
| `int getRsrp()` |  |
| `int getRsrq()` |  |
| `int getRssi()` |  |
| `int getRssnr()` |  |
| `int getTimingAdvance()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellSignalStrengthNr`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getCsiRsrp()` |  |
| `int getCsiRsrq()` |  |
| `int getCsiSinr()` |  |
| `int getDbm()` |  |
| `int getSsRsrp()` |  |
| `int getSsRsrq()` |  |
| `int getSsSinr()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellSignalStrengthTdscdma`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getDbm()` |  |
| `int getRscp()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CellSignalStrengthWcdma`

- **继承：** `android.telephony.CellSignalStrength`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAsuLevel()` |  |
| `int getDbm()` |  |
| `int getEcNo()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ClosedSubscriberGroupInfo`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getCsgIndicator()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final DataFailCause`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ACCESS_ATTEMPT_ALREADY_IN_PROGRESS = 2219` |  |
| `static final int ACCESS_BLOCK = 2087` |  |
| `static final int ACCESS_BLOCK_ALL = 2088` |  |
| `static final int ACCESS_CLASS_DSAC_REJECTION = 2108` |  |
| `static final int ACCESS_CONTROL_LIST_CHECK_FAILURE = 2128` |  |
| `static final int ACTIVATION_REJECTED_BCM_VIOLATION = 48` |  |
| `static final int ACTIVATION_REJECT_GGSN = 30` |  |
| `static final int ACTIVATION_REJECT_UNSPECIFIED = 31` |  |
| `static final int ACTIVE_PDP_CONTEXT_MAX_NUMBER_REACHED = 65` |  |
| `static final int APN_DISABLED = 2045` |  |
| `static final int APN_DISALLOWED_ON_ROAMING = 2059` |  |
| `static final int APN_MISMATCH = 2054` |  |
| `static final int APN_PARAMETERS_CHANGED = 2060` |  |
| `static final int APN_PENDING_HANDOVER = 2041` |  |
| `static final int APN_TYPE_CONFLICT = 112` |  |
| `static final int AUTH_FAILURE_ON_EMERGENCY_CALL = 122` |  |
| `static final int BEARER_HANDLING_NOT_SUPPORTED = 60` |  |
| `static final int CALL_DISALLOWED_IN_ROAMING = 2068` |  |
| `static final int CALL_PREEMPT_BY_EMERGENCY_APN = 127` |  |
| `static final int CANNOT_ENCODE_OTA_MESSAGE = 2159` |  |
| `static final int CDMA_ALERT_STOP = 2077` |  |
| `static final int CDMA_INCOMING_CALL = 2076` |  |
| `static final int CDMA_INTERCEPT = 2073` |  |
| `static final int CDMA_LOCK = 2072` |  |
| `static final int CDMA_RELEASE_DUE_TO_SO_REJECTION = 2075` |  |
| `static final int CDMA_REORDER = 2074` |  |
| `static final int CDMA_RETRY_ORDER = 2086` |  |
| `static final int CHANNEL_ACQUISITION_FAILURE = 2078` |  |
| `static final int CLOSE_IN_PROGRESS = 2030` |  |
| `static final int COLLISION_WITH_NETWORK_INITIATED_REQUEST = 56` |  |
| `static final int COMPANION_IFACE_IN_USE = 118` |  |
| `static final int CONCURRENT_SERVICES_INCOMPATIBLE = 2083` |  |
| `static final int CONCURRENT_SERVICES_NOT_ALLOWED = 2091` |  |
| `static final int CONCURRENT_SERVICE_NOT_SUPPORTED_BY_BASE_STATION = 2080` |  |
| `static final int CONDITIONAL_IE_ERROR = 100` |  |
| `static final int CONGESTION = 2106` |  |
| `static final int CONNECTION_RELEASED = 2113` |  |
| `static final int CS_DOMAIN_NOT_AVAILABLE = 2181` |  |
| `static final int CS_FALLBACK_CALL_ESTABLISHMENT_NOT_ALLOWED = 2188` |  |
| `static final int DATA_PLAN_EXPIRED = 2198` |  |
| `static final int DATA_ROAMING_SETTINGS_DISABLED = 2064` |  |
| `static final int DATA_SETTINGS_DISABLED = 2063` |  |
| `static final int DBM_OR_SMS_IN_PROGRESS = 2211` |  |
| `static final int DDS_SWITCHED = 2065` |  |
| `static final int DDS_SWITCH_IN_PROGRESS = 2067` |  |
| `static final int DRB_RELEASED_BY_RRC = 2112` |  |
| `static final int DS_EXPLICIT_DEACTIVATION = 2125` |  |
| `static final int DUAL_SWITCH = 2227` |  |
| `static final int DUN_CALL_DISALLOWED = 2056` |  |
| `static final int DUPLICATE_BEARER_ID = 2118` |  |
| `static final int EHRPD_TO_HRPD_FALLBACK = 2049` |  |
| `static final int EMBMS_NOT_ENABLED = 2193` |  |
| `static final int EMBMS_REGULAR_DEACTIVATION = 2195` |  |
| `static final int EMERGENCY_IFACE_ONLY = 116` |  |
| `static final int EMERGENCY_MODE = 2221` |  |
| `static final int EMM_ACCESS_BARRED = 115` |  |
| `static final int EMM_ACCESS_BARRED_INFINITE_RETRY = 121` |  |
| `static final int EMM_ATTACH_FAILED = 2115` |  |
| `static final int EMM_ATTACH_STARTED = 2116` |  |
| `static final int EMM_DETACHED = 2114` |  |
| `static final int EMM_T3417_EXPIRED = 2130` |  |
| `static final int EMM_T3417_EXT_EXPIRED = 2131` |  |
| `static final int EPS_SERVICES_AND_NON_EPS_SERVICES_NOT_ALLOWED = 2178` |  |
| `static final int EPS_SERVICES_NOT_ALLOWED_IN_PLMN = 2179` |  |
| `static final int ERROR_UNSPECIFIED = 65535` |  |
| `static final int ESM_BAD_OTA_MESSAGE = 2122` |  |
| `static final int ESM_BEARER_DEACTIVATED_TO_SYNC_WITH_NETWORK = 2120` |  |
| `static final int ESM_COLLISION_SCENARIOS = 2119` |  |
| `static final int ESM_CONTEXT_TRANSFERRED_DUE_TO_IRAT = 2124` |  |
| `static final int ESM_DOWNLOAD_SERVER_REJECTED_THE_CALL = 2123` |  |
| `static final int ESM_FAILURE = 2182` |  |
| `static final int ESM_INFO_NOT_RECEIVED = 53` |  |
| `static final int ESM_LOCAL_CAUSE_NONE = 2126` |  |
| `static final int ESM_NW_ACTIVATED_DED_BEARER_WITH_ID_OF_DEF_BEARER = 2121` |  |
| `static final int ESM_PROCEDURE_TIME_OUT = 2155` |  |
| `static final int ESM_UNKNOWN_EPS_BEARER_CONTEXT = 2111` |  |
| `static final int EVDO_CONNECTION_DENY_BY_BILLING_OR_AUTHENTICATION_FAILURE = 2201` |  |
| `static final int EVDO_CONNECTION_DENY_BY_GENERAL_OR_NETWORK_BUSY = 2200` |  |
| `static final int EVDO_HDR_CHANGED = 2202` |  |
| `static final int EVDO_HDR_CONNECTION_SETUP_TIMEOUT = 2206` |  |
| `static final int EVDO_HDR_EXITED = 2203` |  |
| `static final int EVDO_HDR_NO_SESSION = 2204` |  |
| `static final int EVDO_USING_GPS_FIX_INSTEAD_OF_HDR_CALL = 2205` |  |
| `static final int FADE = 2217` |  |
| `static final int FAILED_TO_ACQUIRE_COLOCATED_HDR = 2207` |  |
| `static final int FEATURE_NOT_SUPP = 40` |  |
| `static final int FILTER_SEMANTIC_ERROR = 44` |  |
| `static final int FILTER_SYTAX_ERROR = 45` |  |
| `static final int FORBIDDEN_APN_NAME = 2066` |  |
| `static final int GPRS_REGISTRATION_FAIL = -2` |  |
| `static final int GPRS_SERVICES_AND_NON_GPRS_SERVICES_NOT_ALLOWED = 2097` |  |
| `static final int GPRS_SERVICES_NOT_ALLOWED = 2098` |  |
| `static final int GPRS_SERVICES_NOT_ALLOWED_IN_THIS_PLMN = 2103` |  |
| `static final int HANDOFF_PREFERENCE_CHANGED = 2251` |  |
| `static final int HDR_ACCESS_FAILURE = 2213` |  |
| `static final int HDR_FADE = 2212` |  |
| `static final int HDR_NO_LOCK_GRANTED = 2210` |  |
| `static final int IFACE_AND_POL_FAMILY_MISMATCH = 120` |  |
| `static final int IFACE_MISMATCH = 117` |  |
| `static final int ILLEGAL_ME = 2096` |  |
| `static final int ILLEGAL_MS = 2095` |  |
| `static final int IMEI_NOT_ACCEPTED = 2177` |  |
| `static final int IMPLICITLY_DETACHED = 2100` |  |
| `static final int IMSI_UNKNOWN_IN_HOME_SUBSCRIBER_SERVER = 2176` |  |
| `static final int INCOMING_CALL_REJECTED = 2092` |  |
| `static final int INSUFFICIENT_RESOURCES = 26` |  |
| `static final int INTERFACE_IN_USE = 2058` |  |
| `static final int INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN = 114` |  |
| `static final int INTERNAL_EPC_NONEPC_TRANSITION = 2057` |  |
| `static final int INVALID_CONNECTION_ID = 2156` |  |
| `static final int INVALID_DNS_ADDR = 123` |  |
| `static final int INVALID_EMM_STATE = 2190` |  |
| `static final int INVALID_MANDATORY_INFO = 96` |  |
| `static final int INVALID_MODE = 2223` |  |
| `static final int INVALID_PCSCF_ADDR = 113` |  |
| `static final int INVALID_PCSCF_OR_DNS_ADDRESS = 124` |  |
| `static final int INVALID_PRIMARY_NSAPI = 2158` |  |
| `static final int INVALID_SIM_STATE = 2224` |  |
| `static final int INVALID_TRANSACTION_ID = 81` |  |
| `static final int IPV6_ADDRESS_TRANSFER_FAILED = 2047` |  |
| `static final int IPV6_PREFIX_UNAVAILABLE = 2250` |  |
| `static final int IP_ADDRESS_MISMATCH = 119` |  |
| `static final int IP_VERSION_MISMATCH = 2055` |  |
| `static final int IRAT_HANDOVER_FAILED = 2194` |  |
| `static final int IS707B_MAX_ACCESS_PROBES = 2089` |  |
| `static final int LIMITED_TO_IPV4 = 2234` |  |
| `static final int LIMITED_TO_IPV6 = 2235` |  |
| `static final int LLC_SNDCP = 25` |  |
| `static final int LOCAL_END = 2215` |  |
| `static final int LOCATION_AREA_NOT_ALLOWED = 2102` |  |
| `static final int LOST_CONNECTION = 65540` |  |
| `static final int LOWER_LAYER_REGISTRATION_FAILURE = 2197` |  |
| `static final int LOW_POWER_MODE_OR_POWERING_DOWN = 2044` |  |
| `static final int LTE_NAS_SERVICE_REQUEST_FAILED = 2117` |  |
| `static final int LTE_THROTTLING_NOT_REQUIRED = 2127` |  |
| `static final int MAC_FAILURE = 2183` |  |
| `static final int MAXIMIUM_NSAPIS_EXCEEDED = 2157` |  |
| `static final int MAXINUM_SIZE_OF_L2_MESSAGE_EXCEEDED = 2166` |  |
| `static final int MAX_ACCESS_PROBE = 2079` |  |
| `static final int MAX_IPV4_CONNECTIONS = 2052` |  |
| `static final int MAX_IPV6_CONNECTIONS = 2053` |  |
| `static final int MAX_PPP_INACTIVITY_TIMER_EXPIRED = 2046` |  |
| `static final int MESSAGE_INCORRECT_SEMANTIC = 95` |  |
| `static final int MESSAGE_TYPE_UNSUPPORTED = 97` |  |
| `static final int MIP_CONFIG_FAILURE = 2050` |  |
| `static final int MIP_FA_ADMIN_PROHIBITED = 2001` |  |
| `static final int MIP_FA_DELIVERY_STYLE_NOT_SUPPORTED = 2012` |  |
| `static final int MIP_FA_ENCAPSULATION_UNAVAILABLE = 2008` |  |
| `static final int MIP_FA_HOME_AGENT_AUTHENTICATION_FAILURE = 2004` |  |
| `static final int MIP_FA_INSUFFICIENT_RESOURCES = 2002` |  |
| `static final int MIP_FA_MALFORMED_REPLY = 2007` |  |
| `static final int MIP_FA_MALFORMED_REQUEST = 2006` |  |
| `static final int MIP_FA_MISSING_CHALLENGE = 2017` |  |
| `static final int MIP_FA_MISSING_HOME_ADDRESS = 2015` |  |
| `static final int MIP_FA_MISSING_HOME_AGENT = 2014` |  |
| `static final int MIP_FA_MISSING_NAI = 2013` |  |
| `static final int MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE = 2003` |  |
| `static final int MIP_FA_REASON_UNSPECIFIED = 2000` |  |
| `static final int MIP_FA_REQUESTED_LIFETIME_TOO_LONG = 2005` |  |
| `static final int MIP_FA_REVERSE_TUNNEL_IS_MANDATORY = 2011` |  |
| `static final int MIP_FA_REVERSE_TUNNEL_UNAVAILABLE = 2010` |  |
| `static final int MIP_FA_STALE_CHALLENGE = 2018` |  |
| `static final int MIP_FA_UNKNOWN_CHALLENGE = 2016` |  |
| `static final int MIP_FA_VJ_HEADER_COMPRESSION_UNAVAILABLE = 2009` |  |
| `static final int MIP_HA_ADMIN_PROHIBITED = 2020` |  |
| `static final int MIP_HA_ENCAPSULATION_UNAVAILABLE = 2029` |  |
| `static final int MIP_HA_FOREIGN_AGENT_AUTHENTICATION_FAILURE = 2023` |  |
| `static final int MIP_HA_INSUFFICIENT_RESOURCES = 2021` |  |
| `static final int MIP_HA_MALFORMED_REQUEST = 2025` |  |
| `static final int MIP_HA_MOBILE_NODE_AUTHENTICATION_FAILURE = 2022` |  |
| `static final int MIP_HA_REASON_UNSPECIFIED = 2019` |  |
| `static final int MIP_HA_REGISTRATION_ID_MISMATCH = 2024` |  |
| `static final int MIP_HA_REVERSE_TUNNEL_IS_MANDATORY = 2028` |  |
| `static final int MIP_HA_REVERSE_TUNNEL_UNAVAILABLE = 2027` |  |
| `static final int MIP_HA_UNKNOWN_HOME_AGENT_ADDRESS = 2026` |  |
| `static final int MISSING_UNKNOWN_APN = 27` |  |
| `static final int MODEM_APP_PREEMPTED = 2032` |  |
| `static final int MODEM_RESTART = 2037` |  |
| `static final int MSC_TEMPORARILY_NOT_REACHABLE = 2180` |  |
| `static final int MSG_AND_PROTOCOL_STATE_UNCOMPATIBLE = 101` |  |
| `static final int MSG_TYPE_NONCOMPATIBLE_STATE = 98` |  |
| `static final int MS_IDENTITY_CANNOT_BE_DERIVED_BY_THE_NETWORK = 2099` |  |
| `static final int MULTIPLE_PDP_CALL_NOT_ALLOWED = 2192` |  |
| `static final int MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED = 55` |  |
| `static final int NAS_LAYER_FAILURE = 2191` |  |
| `static final int NAS_REQUEST_REJECTED_BY_NETWORK = 2167` |  |
| `static final int NAS_SIGNALLING = 14` |  |
| `static final int NETWORK_FAILURE = 38` |  |
| `static final int NETWORK_INITIATED_DETACH_NO_AUTO_REATTACH = 2154` |  |
| `static final int NETWORK_INITIATED_DETACH_WITH_AUTO_REATTACH = 2153` |  |
| `static final int NETWORK_INITIATED_TERMINATION = 2031` |  |
| `static final int NONE = 0` |  |
| `static final int NON_IP_NOT_SUPPORTED = 2069` |  |
| `static final int NORMAL_RELEASE = 2218` |  |
| `static final int NO_CDMA_SERVICE = 2084` |  |
| `static final int NO_COLLOCATED_HDR = 2225` |  |
| `static final int NO_EPS_BEARER_CONTEXT_ACTIVATED = 2189` |  |
| `static final int NO_GPRS_CONTEXT = 2094` |  |
| `static final int NO_HYBRID_HDR_SERVICE = 2209` |  |
| `static final int NO_PDP_CONTEXT_ACTIVATED = 2107` |  |
| `static final int NO_RESPONSE_FROM_BASE_STATION = 2081` |  |
| `static final int NO_SERVICE = 2216` |  |
| `static final int NO_SERVICE_ON_GATEWAY = 2093` |  |
| `static final int NSAPI_IN_USE = 35` |  |
| `static final int NULL_APN_DISALLOWED = 2061` |  |
| `static final int OEM_DCFAILCAUSE_1 = 4097` |  |
| `static final int OEM_DCFAILCAUSE_10 = 4106` |  |
| `static final int OEM_DCFAILCAUSE_11 = 4107` |  |
| `static final int OEM_DCFAILCAUSE_12 = 4108` |  |
| `static final int OEM_DCFAILCAUSE_13 = 4109` |  |
| `static final int OEM_DCFAILCAUSE_14 = 4110` |  |
| `static final int OEM_DCFAILCAUSE_15 = 4111` |  |
| `static final int OEM_DCFAILCAUSE_2 = 4098` |  |
| `static final int OEM_DCFAILCAUSE_3 = 4099` |  |
| `static final int OEM_DCFAILCAUSE_4 = 4100` |  |
| `static final int OEM_DCFAILCAUSE_5 = 4101` |  |
| `static final int OEM_DCFAILCAUSE_6 = 4102` |  |
| `static final int OEM_DCFAILCAUSE_7 = 4103` |  |
| `static final int OEM_DCFAILCAUSE_8 = 4104` |  |
| `static final int OEM_DCFAILCAUSE_9 = 4105` |  |
| `static final int ONLY_IPV4V6_ALLOWED = 57` |  |
| `static final int ONLY_IPV4_ALLOWED = 50` |  |
| `static final int ONLY_IPV6_ALLOWED = 51` |  |
| `static final int ONLY_NON_IP_ALLOWED = 58` |  |
| `static final int ONLY_SINGLE_BEARER_ALLOWED = 52` |  |
| `static final int OPERATOR_BARRED = 8` |  |
| `static final int OTASP_COMMIT_IN_PROGRESS = 2208` |  |
| `static final int PDN_CONN_DOES_NOT_EXIST = 54` |  |
| `static final int PDN_INACTIVITY_TIMER_EXPIRED = 2051` |  |
| `static final int PDN_IPV4_CALL_DISALLOWED = 2033` |  |
| `static final int PDN_IPV4_CALL_THROTTLED = 2034` |  |
| `static final int PDN_IPV6_CALL_DISALLOWED = 2035` |  |
| `static final int PDN_IPV6_CALL_THROTTLED = 2036` |  |
| `static final int PDN_NON_IP_CALL_DISALLOWED = 2071` |  |
| `static final int PDN_NON_IP_CALL_THROTTLED = 2070` |  |
| `static final int PDP_ACTIVATE_MAX_RETRY_FAILED = 2109` |  |
| `static final int PDP_DUPLICATE = 2104` |  |
| `static final int PDP_ESTABLISH_TIMEOUT_EXPIRED = 2161` |  |
| `static final int PDP_INACTIVE_TIMEOUT_EXPIRED = 2163` |  |
| `static final int PDP_LOWERLAYER_ERROR = 2164` |  |
| `static final int PDP_MODIFY_COLLISION = 2165` |  |
| `static final int PDP_MODIFY_TIMEOUT_EXPIRED = 2162` |  |
| `static final int PDP_PPP_NOT_SUPPORTED = 2038` |  |
| `static final int PDP_WITHOUT_ACTIVE_TFT = 46` |  |
| `static final int PHONE_IN_USE = 2222` |  |
| `static final int PHYSICAL_LINK_CLOSE_IN_PROGRESS = 2040` |  |
| `static final int PLMN_NOT_ALLOWED = 2101` |  |
| `static final int PPP_AUTH_FAILURE = 2229` |  |
| `static final int PPP_CHAP_FAILURE = 2232` |  |
| `static final int PPP_CLOSE_IN_PROGRESS = 2233` |  |
| `static final int PPP_OPTION_MISMATCH = 2230` |  |
| `static final int PPP_PAP_FAILURE = 2231` |  |
| `static final int PPP_TIMEOUT = 2228` |  |
| `static final int PREF_RADIO_TECH_CHANGED = -4` |  |
| `static final int PROFILE_BEARER_INCOMPATIBLE = 2042` |  |
| `static final int PROTOCOL_ERRORS = 111` |  |
| `static final int QOS_NOT_ACCEPTED = 37` |  |
| `static final int RADIO_ACCESS_BEARER_FAILURE = 2110` |  |
| `static final int RADIO_ACCESS_BEARER_SETUP_FAILURE = 2160` |  |
| `static final int RADIO_NOT_AVAILABLE = 65537` |  |
| `static final int RADIO_POWER_OFF = -5` |  |
| `static final int REDIRECTION_OR_HANDOFF_IN_PROGRESS = 2220` |  |
| `static final int REGISTRATION_FAIL = -1` |  |
| `static final int REGULAR_DEACTIVATION = 36` |  |
| `static final int REJECTED_BY_BASE_STATION = 2082` |  |
| `static final int RRC_CONNECTION_ABORTED_AFTER_HANDOVER = 2173` |  |
| `static final int RRC_CONNECTION_ABORTED_AFTER_IRAT_CELL_CHANGE = 2174` |  |
| `static final int RRC_CONNECTION_ABORTED_DUE_TO_IRAT_CHANGE = 2171` |  |
| `static final int RRC_CONNECTION_ABORTED_DURING_IRAT_CELL_CHANGE = 2175` |  |
| `static final int RRC_CONNECTION_ABORT_REQUEST = 2151` |  |
| `static final int RRC_CONNECTION_ACCESS_BARRED = 2139` |  |
| `static final int RRC_CONNECTION_ACCESS_STRATUM_FAILURE = 2137` |  |
| `static final int RRC_CONNECTION_ANOTHER_PROCEDURE_IN_PROGRESS = 2138` |  |
| `static final int RRC_CONNECTION_CELL_NOT_CAMPED = 2144` |  |
| `static final int RRC_CONNECTION_CELL_RESELECTION = 2140` |  |
| `static final int RRC_CONNECTION_CONFIG_FAILURE = 2141` |  |
| `static final int RRC_CONNECTION_INVALID_REQUEST = 2168` |  |
| `static final int RRC_CONNECTION_LINK_FAILURE = 2143` |  |
| `static final int RRC_CONNECTION_NORMAL_RELEASE = 2147` |  |
| `static final int RRC_CONNECTION_OUT_OF_SERVICE_DURING_CELL_REGISTER = 2150` |  |
| `static final int RRC_CONNECTION_RADIO_LINK_FAILURE = 2148` |  |
| `static final int RRC_CONNECTION_REESTABLISHMENT_FAILURE = 2149` |  |
| `static final int RRC_CONNECTION_REJECT_BY_NETWORK = 2146` |  |
| `static final int RRC_CONNECTION_RELEASED_SECURITY_NOT_ACTIVE = 2172` |  |
| `static final int RRC_CONNECTION_RF_UNAVAILABLE = 2170` |  |
| `static final int RRC_CONNECTION_SYSTEM_INFORMATION_BLOCK_READ_ERROR = 2152` |  |
| `static final int RRC_CONNECTION_SYSTEM_INTERVAL_FAILURE = 2145` |  |
| `static final int RRC_CONNECTION_TIMER_EXPIRED = 2142` |  |
| `static final int RRC_CONNECTION_TRACKING_AREA_ID_CHANGED = 2169` |  |
| `static final int RRC_UPLINK_CONNECTION_RELEASE = 2134` |  |
| `static final int RRC_UPLINK_DATA_TRANSMISSION_FAILURE = 2132` |  |
| `static final int RRC_UPLINK_DELIVERY_FAILED_DUE_TO_HANDOVER = 2133` |  |
| `static final int RRC_UPLINK_ERROR_REQUEST_FROM_NAS = 2136` |  |
| `static final int RRC_UPLINK_RADIO_LINK_FAILURE = 2135` |  |
| `static final int RUIM_NOT_PRESENT = 2085` |  |
| `static final int SECURITY_MODE_REJECTED = 2186` |  |
| `static final int SERVICE_NOT_ALLOWED_ON_PLMN = 2129` |  |
| `static final int SERVICE_OPTION_NOT_SUBSCRIBED = 33` |  |
| `static final int SERVICE_OPTION_NOT_SUPPORTED = 32` |  |
| `static final int SERVICE_OPTION_OUT_OF_ORDER = 34` |  |
| `static final int SIGNAL_LOST = -3` |  |
| `static final int SIM_CARD_CHANGED = 2043` |  |
| `static final int SYNCHRONIZATION_FAILURE = 2184` |  |
| `static final int TEST_LOOPBACK_REGULAR_DEACTIVATION = 2196` |  |
| `static final int TETHERED_CALL_ACTIVE = -6` |  |
| `static final int TFT_SEMANTIC_ERROR = 41` |  |
| `static final int TFT_SYTAX_ERROR = 42` |  |
| `static final int THERMAL_EMERGENCY = 2090` |  |
| `static final int THERMAL_MITIGATION = 2062` |  |
| `static final int TRAT_SWAP_FAILED = 2048` |  |
| `static final int UE_INITIATED_DETACH_OR_DISCONNECT = 128` |  |
| `static final int UE_IS_ENTERING_POWERSAVE_MODE = 2226` |  |
| `static final int UE_RAT_CHANGE = 2105` |  |
| `static final int UE_SECURITY_CAPABILITIES_MISMATCH = 2185` |  |
| `static final int UMTS_HANDOVER_TO_IWLAN = 2199` |  |
| `static final int UMTS_REACTIVATION_REQ = 39` |  |
| `static final int UNACCEPTABLE_NETWORK_PARAMETER = 65538` |  |
| `static final int UNACCEPTABLE_NON_EPS_AUTHENTICATION = 2187` |  |
| `static final int UNKNOWN = 65536` |  |
| `static final int UNKNOWN_INFO_ELEMENT = 99` |  |
| `static final int UNKNOWN_PDP_ADDRESS_TYPE = 28` |  |
| `static final int UNKNOWN_PDP_CONTEXT = 43` |  |
| `static final int UNPREFERRED_RAT = 2039` |  |
| `static final int UNSUPPORTED_1X_PREV = 2214` |  |
| `static final int UNSUPPORTED_APN_IN_CURRENT_PLMN = 66` |  |
| `static final int UNSUPPORTED_QCI_VALUE = 59` |  |
| `static final int USER_AUTHENTICATION = 29` |  |
| `static final int VSNCP_ADMINISTRATIVELY_PROHIBITED = 2245` |  |
| `static final int VSNCP_APN_UNAUTHORIZED = 2238` |  |
| `static final int VSNCP_GEN_ERROR = 2237` |  |
| `static final int VSNCP_INSUFFICIENT_PARAMETERS = 2243` |  |
| `static final int VSNCP_NO_PDN_GATEWAY_ADDRESS = 2240` |  |
| `static final int VSNCP_PDN_EXISTS_FOR_THIS_APN = 2248` |  |
| `static final int VSNCP_PDN_GATEWAY_REJECT = 2242` |  |
| `static final int VSNCP_PDN_GATEWAY_UNREACHABLE = 2241` |  |
| `static final int VSNCP_PDN_ID_IN_USE = 2246` |  |
| `static final int VSNCP_PDN_LIMIT_EXCEEDED = 2239` |  |
| `static final int VSNCP_RECONNECT_NOT_ALLOWED = 2249` |  |
| `static final int VSNCP_RESOURCE_UNAVAILABLE = 2244` |  |
| `static final int VSNCP_SUBSCRIBER_LIMITATION = 2247` |  |
| `static final int VSNCP_TIMEOUT = 2236` |  |

---

### `class IccOpenLogicalChannelResponse`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INVALID_CHANNEL = -1` |  |
| `static final int STATUS_MISSING_RESOURCE = 2` |  |
| `static final int STATUS_NO_ERROR = 1` |  |
| `static final int STATUS_NO_SUCH_ELEMENT = 3` |  |
| `static final int STATUS_UNKNOWN_ERROR = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getChannel()` |  |
| `byte[] getSelectResponse()` |  |
| `int getStatus()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class MbmsDownloadSession`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFAULT_TOP_LEVEL_TEMP_DIRECTORY = "androidMbmsTempFileRoot"` |  |
| `static final String EXTRA_MBMS_COMPLETED_FILE_URI = "android.telephony.extra.MBMS_COMPLETED_FILE_URI"` |  |
| `static final String EXTRA_MBMS_DOWNLOAD_REQUEST = "android.telephony.extra.MBMS_DOWNLOAD_REQUEST"` |  |
| `static final String EXTRA_MBMS_DOWNLOAD_RESULT = "android.telephony.extra.MBMS_DOWNLOAD_RESULT"` |  |
| `static final String EXTRA_MBMS_FILE_INFO = "android.telephony.extra.MBMS_FILE_INFO"` |  |
| `static final int RESULT_CANCELLED = 2` |  |
| `static final int RESULT_DOWNLOAD_FAILURE = 6` |  |
| `static final int RESULT_EXPIRED = 3` |  |
| `static final int RESULT_FILE_ROOT_UNREACHABLE = 8` |  |
| `static final int RESULT_IO_ERROR = 4` |  |
| `static final int RESULT_OUT_OF_STORAGE = 7` |  |
| `static final int RESULT_SERVICE_ID_NOT_DEFINED = 5` |  |
| `static final int RESULT_SUCCESSFUL = 1` |  |
| `static final int STATUS_ACTIVELY_DOWNLOADING = 1` |  |
| `static final int STATUS_PENDING_DOWNLOAD = 2` |  |
| `static final int STATUS_PENDING_DOWNLOAD_WINDOW = 4` |  |
| `static final int STATUS_PENDING_REPAIR = 3` |  |
| `static final int STATUS_UNKNOWN = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addProgressListener(@NonNull android.telephony.mbms.DownloadRequest, @NonNull java.util.concurrent.Executor, @NonNull android.telephony.mbms.DownloadProgressListener)` |  |
| `void addStatusListener(@NonNull android.telephony.mbms.DownloadRequest, @NonNull java.util.concurrent.Executor, @NonNull android.telephony.mbms.DownloadStatusListener)` |  |
| `void cancelDownload(@NonNull android.telephony.mbms.DownloadRequest)` |  |
| `void close()` |  |
| `static android.telephony.MbmsDownloadSession create(@NonNull android.content.Context, @NonNull java.util.concurrent.Executor, @NonNull android.telephony.mbms.MbmsDownloadSessionCallback)` |  |
| `void download(@NonNull android.telephony.mbms.DownloadRequest)` |  |
| `void removeProgressListener(@NonNull android.telephony.mbms.DownloadRequest, @NonNull android.telephony.mbms.DownloadProgressListener)` |  |
| `void removeStatusListener(@NonNull android.telephony.mbms.DownloadRequest, @NonNull android.telephony.mbms.DownloadStatusListener)` |  |
| `void requestDownloadState(android.telephony.mbms.DownloadRequest, android.telephony.mbms.FileInfo)` |  |
| `void requestUpdateFileServices(@NonNull java.util.List<java.lang.String>)` |  |
| `void resetDownloadKnowledge(android.telephony.mbms.DownloadRequest)` |  |
| `void setTempFileRootDirectory(@NonNull java.io.File)` |  |

---

### `class MbmsGroupCallSession`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |

---

### `class MbmsStreamingSession`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `static android.telephony.MbmsStreamingSession create(@NonNull android.content.Context, @NonNull java.util.concurrent.Executor, @NonNull android.telephony.mbms.MbmsStreamingSessionCallback)` |  |
| `void requestUpdateStreamingServices(java.util.List<java.lang.String>)` |  |

---

### `class NeighboringCellInfo` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **注解：** `@Deprecated`

---

### `class final NetworkRegistrationInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DOMAIN_CS = 1` |  |
| `static final int DOMAIN_CS_PS = 3` |  |
| `static final int DOMAIN_PS = 2` |  |
| `static final int DOMAIN_UNKNOWN = 0` |  |
| `static final int NR_STATE_CONNECTED = 3` |  |
| `static final int NR_STATE_NONE = 0` |  |
| `static final int NR_STATE_NOT_RESTRICTED = 2` |  |
| `static final int NR_STATE_RESTRICTED = 1` |  |
| `static final int SERVICE_TYPE_DATA = 2` |  |
| `static final int SERVICE_TYPE_EMERGENCY = 5` |  |
| `static final int SERVICE_TYPE_SMS = 3` |  |
| `static final int SERVICE_TYPE_UNKNOWN = 0` |  |
| `static final int SERVICE_TYPE_VIDEO = 4` |  |
| `static final int SERVICE_TYPE_VOICE = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAccessNetworkTechnology()` |  |
| `int getDomain()` |  |
| `int getTransportType()` |  |
| `boolean isRegistered()` |  |
| `boolean isRoaming()` |  |
| `boolean isSearching()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class NetworkScan`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_INTERRUPTED = 10002` |  |
| `static final int ERROR_INVALID_SCAN = 2` |  |
| `static final int ERROR_INVALID_SCANID = 10001` |  |
| `static final int ERROR_MODEM_ERROR = 1` |  |
| `static final int ERROR_MODEM_UNAVAILABLE = 3` |  |
| `static final int ERROR_RADIO_INTERFACE_ERROR = 10000` |  |
| `static final int ERROR_UNSUPPORTED = 4` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void stopScan()` |  |

---

### `class final NetworkScanRequest`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NetworkScanRequest(int, android.telephony.RadioAccessSpecifier[], int, int, boolean, int, java.util.ArrayList<java.lang.String>)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SCAN_TYPE_ONE_SHOT = 0` |  |
| `static final int SCAN_TYPE_PERIODIC = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getIncrementalResults()` |  |
| `int getIncrementalResultsPeriodicity()` |  |
| `int getMaxSearchTime()` |  |
| `java.util.ArrayList<java.lang.String> getPlmns()` |  |
| `int getScanType()` |  |
| `int getSearchPeriodicity()` |  |
| `android.telephony.RadioAccessSpecifier[] getSpecifiers()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PhoneNumberFormattingTextWatcher`

- **实现：** `android.text.TextWatcher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneNumberFormattingTextWatcher()` |  |
| `PhoneNumberFormattingTextWatcher(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void afterTextChanged(android.text.Editable)` |  |
| `void beforeTextChanged(CharSequence, int, int, int)` |  |
| `void onTextChanged(CharSequence, int, int, int)` |  |

---

### `class PhoneNumberUtils`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneNumberUtils()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BCD_EXTENDED_TYPE_CALLED_PARTY = 2` |  |
| `static final int BCD_EXTENDED_TYPE_EF_ADN = 1` |  |
| `static final int FORMAT_JAPAN = 2` |  |
| `static final int FORMAT_NANP = 1` |  |
| `static final int FORMAT_UNKNOWN = 0` |  |
| `static final char PAUSE = 44` |  |
| `static final int TOA_International = 145` |  |
| `static final int TOA_Unknown = 129` |  |
| `static final char WAIT = 59` |  |
| `static final char WILD = 78` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void addTtsSpan(android.text.Spannable, int, int)` |  |
| `static String calledPartyBCDFragmentToString(byte[], int, int, int)` |  |
| `static String calledPartyBCDToString(byte[], int, int, int)` |  |
| `static boolean compare(String, String)` |  |
| `static boolean compare(android.content.Context, String, String)` |  |
| `static String convertKeypadLettersToDigits(String)` |  |
| `static android.text.style.TtsSpan createTtsSpan(String)` |  |
| `static CharSequence createTtsSpannable(CharSequence)` |  |
| `static String extractNetworkPortion(String)` |  |
| `static String extractPostDialPortion(String)` |  |
| `static String formatNumber(String, String)` |  |
| `static String formatNumber(String, String, String)` |  |
| `static String formatNumberToE164(String, String)` |  |
| `static String formatNumberToRFC3966(String, String)` |  |
| `static String getNumberFromIntent(android.content.Intent, android.content.Context)` |  |
| `static String getStrippedReversed(String)` |  |
| `static final boolean is12Key(char)` |  |
| `static final boolean isDialable(char)` |  |
| `static boolean isGlobalPhoneNumber(String)` |  |
| `static boolean isISODigit(char)` |  |
| `static final boolean isNonSeparator(char)` |  |
| `static final boolean isReallyDialable(char)` |  |
| `static final boolean isStartsPostDial(char)` |  |
| `static boolean isVoiceMailNumber(String)` |  |
| `static boolean isWellFormedSmsAddress(String)` |  |
| `static byte[] networkPortionToCalledPartyBCD(String)` |  |
| `static byte[] networkPortionToCalledPartyBCDWithLength(String)` |  |
| `static String normalizeNumber(String)` |  |
| `static byte[] numberToCalledPartyBCD(String, int)` |  |
| `static String replaceUnicodeDigits(String)` |  |
| `static String stringFromStringAndTOA(String, int)` |  |
| `static String stripSeparators(String)` |  |
| `static String toCallerIDMinMatch(String)` |  |
| `static int toaFromString(String)` |  |

---

### `class PhoneStateListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PhoneStateListener()` |  |
| `PhoneStateListener(@NonNull java.util.concurrent.Executor)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE = 4194304` |  |
| `static final int LISTEN_CALL_FORWARDING_INDICATOR = 8` |  |
| `static final int LISTEN_CALL_STATE = 32` |  |
| `static final int LISTEN_CELL_INFO = 1024` |  |
| `static final int LISTEN_CELL_LOCATION = 16` |  |
| `static final int LISTEN_DATA_ACTIVITY = 128` |  |
| `static final int LISTEN_DATA_CONNECTION_STATE = 64` |  |
| `static final int LISTEN_DISPLAY_INFO_CHANGED = 1048576` |  |
| `static final int LISTEN_EMERGENCY_NUMBER_LIST = 16777216` |  |
| `static final int LISTEN_MESSAGE_WAITING_INDICATOR = 4` |  |
| `static final int LISTEN_NONE = 0` |  |
| `static final int LISTEN_SERVICE_STATE = 1` |  |
| `static final int LISTEN_SIGNAL_STRENGTHS = 256` |  |
| `static final int LISTEN_USER_MOBILE_DATA_STATE = 524288` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onActiveDataSubscriptionIdChanged(int)` |  |
| `void onBarringInfoChanged(@NonNull android.telephony.BarringInfo)` |  |
| `void onCallForwardingIndicatorChanged(boolean)` |  |
| `void onCallStateChanged(int, String)` |  |
| `void onCellInfoChanged(java.util.List<android.telephony.CellInfo>)` |  |
| `void onCellLocationChanged(android.telephony.CellLocation)` |  |
| `void onDataActivity(int)` |  |
| `void onDataConnectionStateChanged(int)` |  |
| `void onDataConnectionStateChanged(int, int)` |  |
| `void onMessageWaitingIndicatorChanged(boolean)` |  |
| `void onRegistrationFailed(@NonNull android.telephony.CellIdentity, @NonNull String, int, int, int)` |  |
| `void onServiceStateChanged(android.telephony.ServiceState)` |  |
| `void onSignalStrengthsChanged(android.telephony.SignalStrength)` |  |
| `void onUserMobileDataStateChanged(boolean)` |  |

---

### `class final PreciseDataConnectionState`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getLastCauseCode()` |  |
| `int getNetworkType()` |  |
| `int getState()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final RadioAccessSpecifier`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RadioAccessSpecifier(int, int[], int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int[] getBands()` |  |
| `int[] getChannels()` |  |
| `int getRadioAccessNetwork()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ServiceState`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ServiceState()` |  |
| `ServiceState(android.telephony.ServiceState)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DUPLEX_MODE_FDD = 1` |  |
| `static final int DUPLEX_MODE_TDD = 2` |  |
| `static final int DUPLEX_MODE_UNKNOWN = 0` |  |
| `static final int STATE_EMERGENCY_ONLY = 2` |  |
| `static final int STATE_IN_SERVICE = 0` |  |
| `static final int STATE_OUT_OF_SERVICE = 1` |  |
| `static final int STATE_POWER_OFF = 3` |  |
| `static final int UNKNOWN_ID = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copyFrom(android.telephony.ServiceState)` |  |
| `int describeContents()` |  |
| `int getCdmaNetworkId()` |  |
| `int getCdmaSystemId()` |  |
| `int[] getCellBandwidths()` |  |
| `int getChannelNumber()` |  |
| `int getDuplexMode()` |  |
| `boolean getIsManualSelection()` |  |
| `String getOperatorAlphaLong()` |  |
| `String getOperatorAlphaShort()` |  |
| `String getOperatorNumeric()` |  |
| `boolean getRoaming()` |  |
| `int getState()` |  |
| `boolean isSearching()` |  |
| `void setIsManualSelection(boolean)` |  |
| `void setOperatorName(String, String, String)` |  |
| `void setRoaming(boolean)` |  |
| `void setState(int)` |  |
| `void setStateOff()` |  |
| `void setStateOutOfService()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SignalStrength`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INVALID = 2147483647` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getLevel()` |  |
| `long getTimestampMillis()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SmsManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SmsManager.FinancialSmsCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_MMS_DATA = "android.telephony.extra.MMS_DATA"` |  |
| `static final String EXTRA_MMS_HTTP_STATUS = "android.telephony.extra.MMS_HTTP_STATUS"` |  |
| `static final String MMS_CONFIG_ALIAS_ENABLED = "aliasEnabled"` |  |
| `static final String MMS_CONFIG_ALIAS_MAX_CHARS = "aliasMaxChars"` |  |
| `static final String MMS_CONFIG_ALIAS_MIN_CHARS = "aliasMinChars"` |  |
| `static final String MMS_CONFIG_ALLOW_ATTACH_AUDIO = "allowAttachAudio"` |  |
| `static final String MMS_CONFIG_APPEND_TRANSACTION_ID = "enabledTransID"` |  |
| `static final String MMS_CONFIG_EMAIL_GATEWAY_NUMBER = "emailGatewayNumber"` |  |
| `static final String MMS_CONFIG_GROUP_MMS_ENABLED = "enableGroupMms"` |  |
| `static final String MMS_CONFIG_HTTP_PARAMS = "httpParams"` |  |
| `static final String MMS_CONFIG_HTTP_SOCKET_TIMEOUT = "httpSocketTimeout"` |  |
| `static final String MMS_CONFIG_MAX_IMAGE_HEIGHT = "maxImageHeight"` |  |
| `static final String MMS_CONFIG_MAX_IMAGE_WIDTH = "maxImageWidth"` |  |
| `static final String MMS_CONFIG_MAX_MESSAGE_SIZE = "maxMessageSize"` |  |
| `static final String MMS_CONFIG_MESSAGE_TEXT_MAX_SIZE = "maxMessageTextSize"` |  |
| `static final String MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED = "enableMMSDeliveryReports"` |  |
| `static final String MMS_CONFIG_MMS_ENABLED = "enabledMMS"` |  |
| `static final String MMS_CONFIG_MMS_READ_REPORT_ENABLED = "enableMMSReadReports"` |  |
| `static final String MMS_CONFIG_MULTIPART_SMS_ENABLED = "enableMultipartSMS"` |  |
| `static final String MMS_CONFIG_NAI_SUFFIX = "naiSuffix"` |  |
| `static final String MMS_CONFIG_NOTIFY_WAP_MMSC_ENABLED = "enabledNotifyWapMMSC"` |  |
| `static final String MMS_CONFIG_RECIPIENT_LIMIT = "recipientLimit"` |  |
| `static final String MMS_CONFIG_SEND_MULTIPART_SMS_AS_SEPARATE_MESSAGES = "sendMultipartSmsAsSeparateMessages"` |  |
| `static final String MMS_CONFIG_SHOW_CELL_BROADCAST_APP_LINKS = "config_cellBroadcastAppLinks"` |  |
| `static final String MMS_CONFIG_SMS_DELIVERY_REPORT_ENABLED = "enableSMSDeliveryReports"` |  |
| `static final String MMS_CONFIG_SMS_TO_MMS_TEXT_LENGTH_THRESHOLD = "smsToMmsTextLengthThreshold"` |  |
| `static final String MMS_CONFIG_SMS_TO_MMS_TEXT_THRESHOLD = "smsToMmsTextThreshold"` |  |
| `static final String MMS_CONFIG_SUBJECT_MAX_LENGTH = "maxSubjectLength"` |  |
| `static final String MMS_CONFIG_SUPPORT_HTTP_CHARSET_HEADER = "supportHttpCharsetHeader"` |  |
| `static final String MMS_CONFIG_SUPPORT_MMS_CONTENT_DISPOSITION = "supportMmsContentDisposition"` |  |
| `static final String MMS_CONFIG_UA_PROF_TAG_NAME = "uaProfTagName"` |  |
| `static final String MMS_CONFIG_UA_PROF_URL = "uaProfUrl"` |  |
| `static final String MMS_CONFIG_USER_AGENT = "userAgent"` |  |
| `static final int MMS_ERROR_CONFIGURATION_ERROR = 7` |  |
| `static final int MMS_ERROR_HTTP_FAILURE = 4` |  |
| `static final int MMS_ERROR_INVALID_APN = 2` |  |
| `static final int MMS_ERROR_IO_ERROR = 5` |  |
| `static final int MMS_ERROR_NO_DATA_NETWORK = 8` |  |
| `static final int MMS_ERROR_RETRY = 6` |  |
| `static final int MMS_ERROR_UNABLE_CONNECT_MMS = 3` |  |
| `static final int MMS_ERROR_UNSPECIFIED = 1` |  |
| `static final int RESULT_BLUETOOTH_DISCONNECTED = 27` |  |
| `static final int RESULT_CANCELLED = 23` |  |
| `static final int RESULT_ENCODING_ERROR = 18` |  |
| `static final int RESULT_ERROR_FDN_CHECK_FAILURE = 6` |  |
| `static final int RESULT_ERROR_GENERIC_FAILURE = 1` |  |
| `static final int RESULT_ERROR_LIMIT_EXCEEDED = 5` |  |
| `static final int RESULT_ERROR_NONE = 0` |  |
| `static final int RESULT_ERROR_NO_SERVICE = 4` |  |
| `static final int RESULT_ERROR_NULL_PDU = 3` |  |
| `static final int RESULT_ERROR_RADIO_OFF = 2` |  |
| `static final int RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED = 8` |  |
| `static final int RESULT_ERROR_SHORT_CODE_NOT_ALLOWED = 7` |  |
| `static final int RESULT_INTERNAL_ERROR = 21` |  |
| `static final int RESULT_INVALID_ARGUMENTS = 11` |  |
| `static final int RESULT_INVALID_BLUETOOTH_ADDRESS = 26` |  |
| `static final int RESULT_INVALID_SMSC_ADDRESS = 19` |  |
| `static final int RESULT_INVALID_SMS_FORMAT = 14` |  |
| `static final int RESULT_INVALID_STATE = 12` |  |
| `static final int RESULT_MODEM_ERROR = 16` |  |
| `static final int RESULT_NETWORK_ERROR = 17` |  |
| `static final int RESULT_NETWORK_REJECT = 10` |  |
| `static final int RESULT_NO_BLUETOOTH_SERVICE = 25` |  |
| `static final int RESULT_NO_DEFAULT_SMS_APP = 32` |  |
| `static final int RESULT_NO_MEMORY = 13` |  |
| `static final int RESULT_NO_RESOURCES = 22` |  |
| `static final int RESULT_OPERATION_NOT_ALLOWED = 20` |  |
| `static final int RESULT_RADIO_NOT_AVAILABLE = 9` |  |
| `static final int RESULT_RECEIVE_DISPATCH_FAILURE = 500` |  |
| `static final int RESULT_RECEIVE_INJECTED_NULL_PDU = 501` |  |
| `static final int RESULT_RECEIVE_NULL_MESSAGE_FROM_RIL = 503` |  |
| `static final int RESULT_RECEIVE_RUNTIME_EXCEPTION = 502` |  |
| `static final int RESULT_RECEIVE_SQL_EXCEPTION = 505` |  |
| `static final int RESULT_RECEIVE_URI_EXCEPTION = 506` |  |
| `static final int RESULT_RECEIVE_WHILE_ENCRYPTED = 504` |  |
| `static final int RESULT_REMOTE_EXCEPTION = 31` |  |
| `static final int RESULT_REQUEST_NOT_SUPPORTED = 24` |  |
| `static final int RESULT_RIL_CANCELLED = 119` |  |
| `static final int RESULT_RIL_ENCODING_ERR = 109` |  |
| `static final int RESULT_RIL_INTERNAL_ERR = 113` |  |
| `static final int RESULT_RIL_INVALID_ARGUMENTS = 104` |  |
| `static final int RESULT_RIL_INVALID_MODEM_STATE = 115` |  |
| `static final int RESULT_RIL_INVALID_SMSC_ADDRESS = 110` |  |
| `static final int RESULT_RIL_INVALID_SMS_FORMAT = 107` |  |
| `static final int RESULT_RIL_INVALID_STATE = 103` |  |
| `static final int RESULT_RIL_MODEM_ERR = 111` |  |
| `static final int RESULT_RIL_NETWORK_ERR = 112` |  |
| `static final int RESULT_RIL_NETWORK_NOT_READY = 116` |  |
| `static final int RESULT_RIL_NETWORK_REJECT = 102` |  |
| `static final int RESULT_RIL_NO_MEMORY = 105` |  |
| `static final int RESULT_RIL_NO_RESOURCES = 118` |  |
| `static final int RESULT_RIL_OPERATION_NOT_ALLOWED = 117` |  |
| `static final int RESULT_RIL_RADIO_NOT_AVAILABLE = 100` |  |
| `static final int RESULT_RIL_REQUEST_NOT_SUPPORTED = 114` |  |
| `static final int RESULT_RIL_REQUEST_RATE_LIMITED = 106` |  |
| `static final int RESULT_RIL_SIM_ABSENT = 120` |  |
| `static final int RESULT_RIL_SMS_SEND_FAIL_RETRY = 101` |  |
| `static final int RESULT_RIL_SYSTEM_ERR = 108` |  |
| `static final int RESULT_SMS_BLOCKED_DURING_EMERGENCY = 29` |  |
| `static final int RESULT_SMS_SEND_RETRY_FAILED = 30` |  |
| `static final int RESULT_SYSTEM_ERROR = 15` |  |
| `static final int RESULT_UNEXPECTED_EVENT_STOP_SENDING = 28` |  |
| `static final int STATUS_ON_ICC_FREE = 0` |  |
| `static final int STATUS_ON_ICC_READ = 1` |  |
| `static final int STATUS_ON_ICC_SENT = 5` |  |
| `static final int STATUS_ON_ICC_UNREAD = 3` |  |
| `static final int STATUS_ON_ICC_UNSENT = 7` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String createAppSpecificSmsToken(android.app.PendingIntent)` |  |
| `java.util.ArrayList<java.lang.String> divideMessage(String)` |  |
| `void downloadMultimediaMessage(android.content.Context, String, android.net.Uri, android.os.Bundle, android.app.PendingIntent)` |  |
| `static android.telephony.SmsManager getDefault()` |  |
| `static int getDefaultSmsSubscriptionId()` |  |
| `static android.telephony.SmsManager getSmsManagerForSubscriptionId(int)` |  |
| `int getSubscriptionId()` |  |
| `void injectSmsPdu(byte[], String, android.app.PendingIntent)` |  |
| `void sendDataMessage(String, String, short, byte[], android.app.PendingIntent, android.app.PendingIntent)` |  |
| `void sendMultimediaMessage(android.content.Context, android.net.Uri, String, android.os.Bundle, android.app.PendingIntent)` |  |
| `void sendMultipartTextMessage(String, String, java.util.ArrayList<java.lang.String>, java.util.ArrayList<android.app.PendingIntent>, java.util.ArrayList<android.app.PendingIntent>)` |  |
| `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, long)` |  |
| `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, @NonNull String, @Nullable String)` |  |
| `void sendTextMessage(String, String, String, android.app.PendingIntent, android.app.PendingIntent)` |  |
| `void sendTextMessage(@NonNull String, @Nullable String, @NonNull String, @Nullable android.app.PendingIntent, @Nullable android.app.PendingIntent, long)` |  |
| `abstract void onFinancialSmsMessages(android.database.CursorWindow)` |  |

---

### `class SmsMessage`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ENCODING_16BIT = 3` |  |
| `static final int ENCODING_7BIT = 1` |  |
| `static final int ENCODING_8BIT = 2` |  |
| `static final int ENCODING_UNKNOWN = 0` |  |
| `static final String FORMAT_3GPP = "3gpp"` |  |
| `static final String FORMAT_3GPP2 = "3gpp2"` |  |
| `static final int MAX_USER_DATA_BYTES = 140` |  |
| `static final int MAX_USER_DATA_BYTES_WITH_HEADER = 134` |  |
| `static final int MAX_USER_DATA_SEPTETS = 160` |  |
| `static final int MAX_USER_DATA_SEPTETS_WITH_HEADER = 153` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int[] calculateLength(CharSequence, boolean)` |  |
| `static int[] calculateLength(String, boolean)` |  |
| `static android.telephony.SmsMessage createFromPdu(byte[], String)` |  |
| `String getDisplayMessageBody()` |  |
| `String getDisplayOriginatingAddress()` |  |
| `String getEmailBody()` |  |
| `String getEmailFrom()` |  |
| `int getIndexOnIcc()` |  |
| `String getMessageBody()` |  |
| `android.telephony.SmsMessage.MessageClass getMessageClass()` |  |
| `byte[] getPdu()` |  |
| `int getProtocolIdentifier()` |  |
| `String getPseudoSubject()` |  |
| `String getServiceCenterAddress()` |  |
| `int getStatus()` |  |
| `int getStatusOnIcc()` |  |
| `static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(String, String, String, boolean)` |  |
| `static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(String, String, short, byte[], boolean)` |  |
| `static int getTPLayerLengthForPDU(String)` |  |
| `long getTimestampMillis()` |  |
| `byte[] getUserData()` |  |
| `boolean isCphsMwiMessage()` |  |
| `boolean isEmail()` |  |
| `boolean isMWIClearMessage()` |  |
| `boolean isMWISetMessage()` |  |
| `boolean isMwiDontStore()` |  |
| `boolean isReplace()` |  |
| `boolean isReplyPathPresent()` |  |
| `boolean isStatusReportMessage()` |  |

---

### `enum SmsMessage.MessageClass`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.telephony.SmsMessage.MessageClass CLASS_0` |  |
| `static final android.telephony.SmsMessage.MessageClass CLASS_1` |  |
| `static final android.telephony.SmsMessage.MessageClass CLASS_2` |  |
| `static final android.telephony.SmsMessage.MessageClass CLASS_3` |  |
| `static final android.telephony.SmsMessage.MessageClass UNKNOWN` |  |

---

### `class static SmsMessage.SubmitPdu`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `byte[] encodedMessage` |  |
| `byte[] encodedScAddress` |  |

---

### `class SubscriptionInfo`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Bitmap createIconBitmap(android.content.Context)` |  |
| `int describeContents()` |  |
| `int getCardId()` |  |
| `int getCarrierId()` |  |
| `CharSequence getCarrierName()` |  |
| `String getCountryIso()` |  |
| `int getDataRoaming()` |  |
| `CharSequence getDisplayName()` |  |
| `String getIccId()` |  |
| `int getIconTint()` |  |
| `String getNumber()` |  |
| `int getSimSlotIndex()` |  |
| `int getSubscriptionId()` |  |
| `int getSubscriptionType()` |  |
| `boolean isEmbedded()` |  |
| `boolean isOpportunistic()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SubscriptionManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_DEFAULT_SMS_SUBSCRIPTION_CHANGED = "android.telephony.action.DEFAULT_SMS_SUBSCRIPTION_CHANGED"` |  |
| `static final String ACTION_DEFAULT_SUBSCRIPTION_CHANGED = "android.telephony.action.DEFAULT_SUBSCRIPTION_CHANGED"` |  |
| `static final String ACTION_MANAGE_SUBSCRIPTION_PLANS = "android.telephony.action.MANAGE_SUBSCRIPTION_PLANS"` |  |
| `static final String ACTION_REFRESH_SUBSCRIPTION_PLANS = "android.telephony.action.REFRESH_SUBSCRIPTION_PLANS"` |  |
| `static final int DATA_ROAMING_DISABLE = 0` |  |
| `static final int DATA_ROAMING_ENABLE = 1` |  |
| `static final int DEFAULT_SUBSCRIPTION_ID = 2147483647` |  |
| `static final String EXTRA_SLOT_INDEX = "android.telephony.extra.SLOT_INDEX"` |  |
| `static final String EXTRA_SUBSCRIPTION_INDEX = "android.telephony.extra.SUBSCRIPTION_INDEX"` |  |
| `static final int INVALID_SIM_SLOT_INDEX = -1` |  |
| `static final int INVALID_SUBSCRIPTION_ID = -1` |  |
| `static final int SUBSCRIPTION_TYPE_LOCAL_SIM = 0` |  |
| `static final int SUBSCRIPTION_TYPE_REMOTE_SIM = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addOnOpportunisticSubscriptionsChangedListener(@NonNull java.util.concurrent.Executor, @NonNull android.telephony.SubscriptionManager.OnOpportunisticSubscriptionsChangedListener)` |  |
| `void addOnSubscriptionsChangedListener(android.telephony.SubscriptionManager.OnSubscriptionsChangedListener)` |  |
| `void addOnSubscriptionsChangedListener(@NonNull java.util.concurrent.Executor, @NonNull android.telephony.SubscriptionManager.OnSubscriptionsChangedListener)` |  |
| `boolean canManageSubscription(android.telephony.SubscriptionInfo)` |  |
| `java.util.List<android.telephony.SubscriptionInfo> getAccessibleSubscriptionInfoList()` |  |
| `static int getActiveDataSubscriptionId()` |  |
| `int getActiveSubscriptionInfoCountMax()` |  |
| `static int getDefaultDataSubscriptionId()` |  |
| `static int getDefaultSmsSubscriptionId()` |  |
| `static int getDefaultSubscriptionId()` |  |
| `static int getDefaultVoiceSubscriptionId()` |  |
| `static int getSlotIndex(int)` |  |
| `boolean isNetworkRoaming(int)` |  |
| `static boolean isUsableSubscriptionId(int)` |  |
| `static boolean isValidSubscriptionId(int)` |  |
| `void removeOnOpportunisticSubscriptionsChangedListener(@NonNull android.telephony.SubscriptionManager.OnOpportunisticSubscriptionsChangedListener)` |  |
| `void removeOnSubscriptionsChangedListener(android.telephony.SubscriptionManager.OnSubscriptionsChangedListener)` |  |
| `void setSubscriptionOverrideCongested(int, boolean, long)` |  |
| `void setSubscriptionOverrideUnmetered(int, boolean, long)` |  |
| `void setSubscriptionPlans(int, @NonNull java.util.List<android.telephony.SubscriptionPlan>)` |  |

---

### `class static SubscriptionManager.OnOpportunisticSubscriptionsChangedListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SubscriptionManager.OnOpportunisticSubscriptionsChangedListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onOpportunisticSubscriptionsChanged()` |  |

---

### `class static SubscriptionManager.OnSubscriptionsChangedListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SubscriptionManager.OnSubscriptionsChangedListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onSubscriptionsChanged()` |  |

---

### `class final SubscriptionPlan`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long BYTES_UNKNOWN = -1L` |  |
| `static final long BYTES_UNLIMITED = 9223372036854775807L` |  |
| `static final int LIMIT_BEHAVIOR_BILLED = 1` |  |
| `static final int LIMIT_BEHAVIOR_DISABLED = 0` |  |
| `static final int LIMIT_BEHAVIOR_THROTTLED = 2` |  |
| `static final int LIMIT_BEHAVIOR_UNKNOWN = -1` |  |
| `static final long TIME_UNKNOWN = -1L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.Iterator<android.util.Range<java.time.ZonedDateTime>> cycleIterator()` |  |
| `int describeContents()` |  |
| `int getDataLimitBehavior()` |  |
| `long getDataLimitBytes()` |  |
| `long getDataUsageBytes()` |  |
| `long getDataUsageTime()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static SubscriptionPlan.Builder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telephony.SubscriptionPlan build()` |  |
| `static android.telephony.SubscriptionPlan.Builder createNonrecurring(java.time.ZonedDateTime, java.time.ZonedDateTime)` |  |
| `static android.telephony.SubscriptionPlan.Builder createRecurring(java.time.ZonedDateTime, java.time.Period)` |  |
| `android.telephony.SubscriptionPlan.Builder setDataLimit(long, int)` |  |
| `android.telephony.SubscriptionPlan.Builder setDataUsage(long, long)` |  |
| `android.telephony.SubscriptionPlan.Builder setSummary(@Nullable CharSequence)` |  |
| `android.telephony.SubscriptionPlan.Builder setTitle(@Nullable CharSequence)` |  |

---

### `class final TelephonyDisplayInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO = 2` |  |
| `static final int OVERRIDE_NETWORK_TYPE_LTE_CA = 1` |  |
| `static final int OVERRIDE_NETWORK_TYPE_NONE = 0` |  |
| `static final int OVERRIDE_NETWORK_TYPE_NR_NSA = 3` |  |
| `static final int OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getNetworkType()` |  |
| `int getOverrideNetworkType()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class TelephonyManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TelephonyManager.CellInfoCallback()` |  |
| `TelephonyManager.UssdResponseCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CARRIER_MESSAGING_CLIENT_SERVICE = "android.telephony.action.CARRIER_MESSAGING_CLIENT_SERVICE"` |  |
| `static final String ACTION_CONFIGURE_VOICEMAIL = "android.telephony.action.CONFIGURE_VOICEMAIL"` |  |
| `static final String ACTION_MULTI_SIM_CONFIG_CHANGED = "android.telephony.action.MULTI_SIM_CONFIG_CHANGED"` |  |
| `static final String ACTION_NETWORK_COUNTRY_CHANGED = "android.telephony.action.NETWORK_COUNTRY_CHANGED"` |  |
| `static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE"` |  |
| `static final String ACTION_SECRET_CODE = "android.telephony.action.SECRET_CODE"` |  |
| `static final String ACTION_SHOW_VOICEMAIL_NOTIFICATION = "android.telephony.action.SHOW_VOICEMAIL_NOTIFICATION"` |  |
| `static final String ACTION_SUBSCRIPTION_CARRIER_IDENTITY_CHANGED = "android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED"` |  |
| `static final String ACTION_SUBSCRIPTION_SPECIFIC_CARRIER_IDENTITY_CHANGED = "android.telephony.action.SUBSCRIPTION_SPECIFIC_CARRIER_IDENTITY_CHANGED"` |  |
| `static final int APPTYPE_CSIM = 4` |  |
| `static final int APPTYPE_ISIM = 5` |  |
| `static final int APPTYPE_RUIM = 3` |  |
| `static final int APPTYPE_SIM = 1` |  |
| `static final int APPTYPE_USIM = 2` |  |
| `static final int AUTHTYPE_EAP_AKA = 129` |  |
| `static final int AUTHTYPE_EAP_SIM = 128` |  |
| `static final int CALL_STATE_IDLE = 0` |  |
| `static final int CALL_STATE_OFFHOOK = 2` |  |
| `static final int CALL_STATE_RINGING = 1` |  |
| `static final int CDMA_ROAMING_MODE_AFFILIATED = 1` |  |
| `static final int CDMA_ROAMING_MODE_ANY = 2` |  |
| `static final int CDMA_ROAMING_MODE_HOME = 0` |  |
| `static final int CDMA_ROAMING_MODE_RADIO_DEFAULT = -1` |  |
| `static final int DATA_ACTIVITY_DORMANT = 4` |  |
| `static final int DATA_ACTIVITY_IN = 1` |  |
| `static final int DATA_ACTIVITY_INOUT = 3` |  |
| `static final int DATA_ACTIVITY_NONE = 0` |  |
| `static final int DATA_ACTIVITY_OUT = 2` |  |
| `static final int DATA_CONNECTED = 2` |  |
| `static final int DATA_CONNECTING = 1` |  |
| `static final int DATA_DISCONNECTED = 0` |  |
| `static final int DATA_DISCONNECTING = 4` |  |
| `static final int DATA_SUSPENDED = 3` |  |
| `static final int DATA_UNKNOWN = -1` |  |
| `static final String EXTRA_ACTIVE_SIM_SUPPORTED_COUNT = "android.telephony.extra.ACTIVE_SIM_SUPPORTED_COUNT"` |  |
| `static final String EXTRA_CALL_VOICEMAIL_INTENT = "android.telephony.extra.CALL_VOICEMAIL_INTENT"` |  |
| `static final String EXTRA_CARRIER_ID = "android.telephony.extra.CARRIER_ID"` |  |
| `static final String EXTRA_CARRIER_NAME = "android.telephony.extra.CARRIER_NAME"` |  |
| `static final String EXTRA_HIDE_PUBLIC_SETTINGS = "android.telephony.extra.HIDE_PUBLIC_SETTINGS"` |  |
| `static final String EXTRA_IS_REFRESH = "android.telephony.extra.IS_REFRESH"` |  |
| `static final String EXTRA_LAUNCH_VOICEMAIL_SETTINGS_INTENT = "android.telephony.extra.LAUNCH_VOICEMAIL_SETTINGS_INTENT"` |  |
| `static final String EXTRA_NETWORK_COUNTRY = "android.telephony.extra.NETWORK_COUNTRY"` |  |
| `static final String EXTRA_NOTIFICATION_COUNT = "android.telephony.extra.NOTIFICATION_COUNT"` |  |
| `static final String EXTRA_PHONE_ACCOUNT_HANDLE = "android.telephony.extra.PHONE_ACCOUNT_HANDLE"` |  |
| `static final String EXTRA_SPECIFIC_CARRIER_ID = "android.telephony.extra.SPECIFIC_CARRIER_ID"` |  |
| `static final String EXTRA_SPECIFIC_CARRIER_NAME = "android.telephony.extra.SPECIFIC_CARRIER_NAME"` |  |
| `static final String EXTRA_STATE = "state"` |  |
| `static final String EXTRA_STATE_IDLE` |  |
| `static final String EXTRA_STATE_OFFHOOK` |  |
| `static final String EXTRA_STATE_RINGING` |  |
| `static final String EXTRA_SUBSCRIPTION_ID = "android.telephony.extra.SUBSCRIPTION_ID"` |  |
| `static final String EXTRA_VOICEMAIL_NUMBER = "android.telephony.extra.VOICEMAIL_NUMBER"` |  |
| `static final String METADATA_HIDE_VOICEMAIL_SETTINGS_MENU = "android.telephony.HIDE_VOICEMAIL_SETTINGS_MENU"` |  |
| `static final int MULTISIM_ALLOWED = 0` |  |
| `static final int MULTISIM_NOT_SUPPORTED_BY_CARRIER = 2` |  |
| `static final int MULTISIM_NOT_SUPPORTED_BY_HARDWARE = 1` |  |
| `static final int NETWORK_SELECTION_MODE_AUTO = 1` |  |
| `static final int NETWORK_SELECTION_MODE_MANUAL = 2` |  |
| `static final int NETWORK_SELECTION_MODE_UNKNOWN = 0` |  |
| `static final int NETWORK_TYPE_1xRTT = 7` |  |
| `static final int NETWORK_TYPE_CDMA = 4` |  |
| `static final int NETWORK_TYPE_EDGE = 2` |  |
| `static final int NETWORK_TYPE_EHRPD = 14` |  |
| `static final int NETWORK_TYPE_EVDO_0 = 5` |  |
| `static final int NETWORK_TYPE_EVDO_A = 6` |  |
| `static final int NETWORK_TYPE_EVDO_B = 12` |  |
| `static final int NETWORK_TYPE_GPRS = 1` |  |
| `static final int NETWORK_TYPE_GSM = 16` |  |
| `static final int NETWORK_TYPE_HSDPA = 8` |  |
| `static final int NETWORK_TYPE_HSPA = 10` |  |
| `static final int NETWORK_TYPE_HSPAP = 15` |  |
| `static final int NETWORK_TYPE_HSUPA = 9` |  |
| `static final int NETWORK_TYPE_IDEN = 11` |  |
| `static final int NETWORK_TYPE_IWLAN = 18` |  |
| `static final int NETWORK_TYPE_LTE = 13` |  |
| `static final int NETWORK_TYPE_NR = 20` |  |
| `static final int NETWORK_TYPE_TD_SCDMA = 17` |  |
| `static final int NETWORK_TYPE_UMTS = 3` |  |
| `static final int NETWORK_TYPE_UNKNOWN = 0` |  |
| `static final int PHONE_TYPE_CDMA = 2` |  |
| `static final int PHONE_TYPE_GSM = 1` |  |
| `static final int PHONE_TYPE_NONE = 0` |  |
| `static final int PHONE_TYPE_SIP = 3` |  |
| `static final int SET_OPPORTUNISTIC_SUB_INACTIVE_SUBSCRIPTION = 2` |  |
| `static final int SET_OPPORTUNISTIC_SUB_NO_OPPORTUNISTIC_SUB_AVAILABLE = 3` |  |
| `static final int SET_OPPORTUNISTIC_SUB_REMOTE_SERVICE_EXCEPTION = 4` |  |
| `static final int SET_OPPORTUNISTIC_SUB_SUCCESS = 0` |  |
| `static final int SET_OPPORTUNISTIC_SUB_VALIDATION_FAILED = 1` |  |
| `static final int SIM_STATE_ABSENT = 1` |  |
| `static final int SIM_STATE_CARD_IO_ERROR = 8` |  |
| `static final int SIM_STATE_CARD_RESTRICTED = 9` |  |
| `static final int SIM_STATE_NETWORK_LOCKED = 4` |  |
| `static final int SIM_STATE_NOT_READY = 6` |  |
| `static final int SIM_STATE_PERM_DISABLED = 7` |  |
| `static final int SIM_STATE_PIN_REQUIRED = 2` |  |
| `static final int SIM_STATE_PUK_REQUIRED = 3` |  |
| `static final int SIM_STATE_READY = 5` |  |
| `static final int SIM_STATE_UNKNOWN = 0` |  |
| `static final int UNINITIALIZED_CARD_ID = -2` |  |
| `static final int UNKNOWN_CARRIER_ID = -1` |  |
| `static final int UNSUPPORTED_CARD_ID = -1` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_ABORTED = 2` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_DISABLE_MODEM_FAIL = 5` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_ENABLE_MODEM_FAIL = 6` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_INVALID_ARGUMENTS = 3` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_MULTIPLE_NETWORKS_NOT_SUPPORTED = 7` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_NO_CARRIER_PRIVILEGE = 4` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_NO_OPPORTUNISTIC_SUB_AVAILABLE = 8` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_REMOTE_SERVICE_EXCEPTION = 9` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_SERVICE_IS_DISABLED = 10` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_SUCCESS = 0` |  |
| `static final int UPDATE_AVAILABLE_NETWORKS_UNKNOWN_FAILURE = 1` |  |
| `static final int USSD_ERROR_SERVICE_UNAVAIL = -2` |  |
| `static final int USSD_RETURN_FAILURE = -1` |  |
| `static final String VVM_TYPE_CVVM = "vvm_type_cvvm"` |  |
| `static final String VVM_TYPE_OMTP = "vvm_type_omtp"` |  |
| `static final int ERROR_MODEM_ERROR = 2` |  |
| `static final int ERROR_TIMEOUT = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canChangeDtmfToneLength()` |  |
| `android.telephony.TelephonyManager createForSubscriptionId(int)` |  |
| `int getActiveModemCount()` |  |
| `int getCallState()` |  |
| `int getCardIdForDefaultEuicc()` |  |
| `int getCarrierIdFromSimMccMnc()` |  |
| `int getDataActivity()` |  |
| `int getDataState()` |  |
| `String getIccAuthentication(int, int, String)` |  |
| `String getMmsUAProfUrl()` |  |
| `String getMmsUserAgent()` |  |
| `String getNetworkCountryIso()` |  |
| `String getNetworkOperator()` |  |
| `String getNetworkOperatorName()` |  |
| `String getNetworkSpecifier()` |  |
| `int getPhoneType()` |  |
| `int getSimCarrierId()` |  |
| `String getSimCountryIso()` |  |
| `String getSimOperator()` |  |
| `String getSimOperatorName()` |  |
| `int getSimSpecificCarrierId()` |  |
| `int getSimState()` |  |
| `int getSimState(int)` |  |
| `int getSubscriptionId()` |  |
| `int getSupportedModemCount()` |  |
| `android.net.Uri getVoicemailRingtoneUri(android.telecom.PhoneAccountHandle)` |  |
| `boolean hasCarrierPrivileges()` |  |
| `boolean hasIccCard()` |  |
| `boolean isConcurrentVoiceAndDataSupported()` |  |
| `boolean isEmergencyNumber(@NonNull String)` |  |
| `boolean isHearingAidCompatibilitySupported()` |  |
| `boolean isNetworkRoaming()` |  |
| `boolean isRttSupported()` |  |
| `boolean isSmsCapable()` |  |
| `boolean isVoiceCapable()` |  |
| `boolean isVoicemailVibrationEnabled(android.telecom.PhoneAccountHandle)` |  |
| `boolean isWorldPhone()` |  |
| `void listen(android.telephony.PhoneStateListener, int)` |  |
| `void sendDialerSpecialCode(String)` |  |
| `void sendVisualVoicemailSms(String, int, String, android.app.PendingIntent)` |  |
| `boolean setLine1NumberForDisplay(String, String)` |  |
| `boolean setOperatorBrandOverride(String)` |  |
| `boolean setPreferredNetworkTypeToGlobal()` |  |
| `void setPreferredOpportunisticDataSubscription(int, boolean, @Nullable java.util.concurrent.Executor, @Nullable java.util.function.Consumer<java.lang.Integer>)` |  |
| `void setVisualVoicemailSmsFilterSettings(android.telephony.VisualVoicemailSmsFilterSettings)` |  |
| `boolean setVoiceMailNumber(String, String)` |  |
| `void updateAvailableNetworks(@NonNull java.util.List<android.telephony.AvailableNetworkInfo>, @Nullable java.util.concurrent.Executor, @Nullable java.util.function.Consumer<java.lang.Integer>)` |  |
| `abstract void onCellInfo(@NonNull java.util.List<android.telephony.CellInfo>)` |  |
| `void onError(int, @Nullable Throwable)` |  |
| `void onReceiveUssdResponse(android.telephony.TelephonyManager, String, CharSequence)` |  |
| `void onReceiveUssdResponseFailed(android.telephony.TelephonyManager, String, int)` |  |

---

### `class final TelephonyScanManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TelephonyScanManager()` |  |
| `TelephonyScanManager.NetworkScanCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onComplete()` |  |
| `void onError(int)` |  |
| `void onResults(java.util.List<android.telephony.CellInfo>)` |  |

---

### `class final UiccCardInfo`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCardId()` |  |
| `int getSlotIndex()` |  |
| `boolean isEuicc()` |  |
| `boolean isRemovable()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract VisualVoicemailService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VisualVoicemailService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.telephony.VisualVoicemailService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |

---

### `class static VisualVoicemailService.VisualVoicemailTask`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void finish()` |  |

---

### `class final VisualVoicemailSms`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.os.Bundle getFields()` |  |
| `String getMessageBody()` |  |
| `android.telecom.PhoneAccountHandle getPhoneAccountHandle()` |  |
| `String getPrefix()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final VisualVoicemailSmsFilterSettings`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DESTINATION_PORT_ANY = -1` |  |
| `static final int DESTINATION_PORT_DATA_SMS = -2` |  |
| `final String clientPrefix` |  |
| `final int destinationPort` |  |
| `final java.util.List<java.lang.String> originatingNumbers` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static VisualVoicemailSmsFilterSettings.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VisualVoicemailSmsFilterSettings.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telephony.VisualVoicemailSmsFilterSettings build()` |  |
| `android.telephony.VisualVoicemailSmsFilterSettings.Builder setClientPrefix(String)` |  |
| `android.telephony.VisualVoicemailSmsFilterSettings.Builder setDestinationPort(int)` |  |
| `android.telephony.VisualVoicemailSmsFilterSettings.Builder setOriginatingNumbers(java.util.List<java.lang.String>)` |  |

---

## Package: `android.telephony.cdma`

### `class CdmaCellLocation`

- **继承：** `android.telephony.CellLocation`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CdmaCellLocation()` |  |
| `CdmaCellLocation(android.os.Bundle)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static double convertQuartSecToDecDegrees(int)` |  |
| `void fillInNotifierBundle(android.os.Bundle)` |  |
| `int getBaseStationId()` |  |
| `int getBaseStationLatitude()` |  |
| `int getBaseStationLongitude()` |  |
| `int getNetworkId()` |  |
| `int getSystemId()` |  |
| `void setCellLocationData(int, int, int)` |  |
| `void setCellLocationData(int, int, int, int, int)` |  |
| `void setStateInvalid()` |  |

---

## Package: `android.telephony.data`

### `class ApnSetting`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUTH_TYPE_CHAP = 2` |  |
| `static final int AUTH_TYPE_NONE = 0` |  |
| `static final int AUTH_TYPE_PAP = 1` |  |
| `static final int AUTH_TYPE_PAP_OR_CHAP = 3` |  |
| `static final int MVNO_TYPE_GID = 2` |  |
| `static final int MVNO_TYPE_ICCID = 3` |  |
| `static final int MVNO_TYPE_IMSI = 1` |  |
| `static final int MVNO_TYPE_SPN = 0` |  |
| `static final int PROTOCOL_IP = 0` |  |
| `static final int PROTOCOL_IPV4V6 = 2` |  |
| `static final int PROTOCOL_IPV6 = 1` |  |
| `static final int PROTOCOL_NON_IP = 4` |  |
| `static final int PROTOCOL_PPP = 3` |  |
| `static final int PROTOCOL_UNSTRUCTURED = 5` |  |
| `static final int TYPE_CBS = 128` |  |
| `static final int TYPE_DEFAULT = 17` |  |
| `static final int TYPE_DUN = 8` |  |
| `static final int TYPE_EMERGENCY = 512` |  |
| `static final int TYPE_FOTA = 32` |  |
| `static final int TYPE_HIPRI = 16` |  |
| `static final int TYPE_IA = 256` |  |
| `static final int TYPE_IMS = 64` |  |
| `static final int TYPE_MCX = 1024` |  |
| `static final int TYPE_MMS = 2` |  |
| `static final int TYPE_SUPL = 4` |  |
| `static final int TYPE_XCAP = 2048` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getApnName()` |  |
| `int getApnTypeBitmask()` |  |
| `int getAuthType()` |  |
| `int getCarrierId()` |  |
| `String getEntryName()` |  |
| `int getId()` |  |
| `String getMmsProxyAddressAsString()` |  |
| `int getMmsProxyPort()` |  |
| `android.net.Uri getMmsc()` |  |
| `int getMvnoType()` |  |
| `int getNetworkTypeBitmask()` |  |
| `String getOperatorNumeric()` |  |
| `String getPassword()` |  |
| `int getProtocol()` |  |
| `String getProxyAddressAsString()` |  |
| `int getProxyPort()` |  |
| `int getRoamingProtocol()` |  |
| `String getUser()` |  |
| `boolean isEnabled()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static ApnSetting.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ApnSetting.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telephony.data.ApnSetting build()` |  |

---

## Package: `android.telephony.emergency`

### `class final EmergencyNumber`

- **实现：** `java.lang.Comparable<android.telephony.emergency.EmergencyNumber> android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EMERGENCY_CALL_ROUTING_EMERGENCY = 1` |  |
| `static final int EMERGENCY_CALL_ROUTING_NORMAL = 2` |  |
| `static final int EMERGENCY_CALL_ROUTING_UNKNOWN = 0` |  |
| `static final int EMERGENCY_NUMBER_SOURCE_DATABASE = 16` |  |
| `static final int EMERGENCY_NUMBER_SOURCE_DEFAULT = 8` |  |
| `static final int EMERGENCY_NUMBER_SOURCE_MODEM_CONFIG = 4` |  |
| `static final int EMERGENCY_NUMBER_SOURCE_NETWORK_SIGNALING = 1` |  |
| `static final int EMERGENCY_NUMBER_SOURCE_SIM = 2` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_AIEC = 64` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_AMBULANCE = 2` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_FIRE_BRIGADE = 4` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_MARINE_GUARD = 8` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_MIEC = 32` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_MOUNTAIN_RESCUE = 16` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_POLICE = 1` |  |
| `static final int EMERGENCY_SERVICE_CATEGORY_UNSPECIFIED = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int compareTo(@NonNull android.telephony.emergency.EmergencyNumber)` |  |
| `int describeContents()` |  |
| `int getEmergencyCallRouting()` |  |
| `boolean isFromSources(int)` |  |
| `boolean isInEmergencyServiceCategories(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.telephony.euicc`

### `class final DownloadableSubscription`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.telephony.euicc.DownloadableSubscription forActivationCode(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final EuiccInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EuiccInfo(@Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class EuiccManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_MANAGE_EMBEDDED_SUBSCRIPTIONS = "android.telephony.euicc.action.MANAGE_EMBEDDED_SUBSCRIPTIONS"` |  |
| `static final String ACTION_NOTIFY_CARRIER_SETUP_INCOMPLETE = "android.telephony.euicc.action.NOTIFY_CARRIER_SETUP_INCOMPLETE"` |  |
| `static final String ACTION_START_EUICC_ACTIVATION = "android.telephony.euicc.action.START_EUICC_ACTIVATION"` |  |
| `static final int EMBEDDED_SUBSCRIPTION_RESULT_ERROR = 2` |  |
| `static final int EMBEDDED_SUBSCRIPTION_RESULT_OK = 0` |  |
| `static final int EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR = 1` |  |
| `static final int ERROR_ADDRESS_MISSING = 10011` |  |
| `static final int ERROR_CARRIER_LOCKED = 10000` |  |
| `static final int ERROR_CERTIFICATE_ERROR = 10012` |  |
| `static final int ERROR_CONNECTION_ERROR = 10014` |  |
| `static final int ERROR_DISALLOWED_BY_PPR = 10010` |  |
| `static final int ERROR_EUICC_INSUFFICIENT_MEMORY = 10004` |  |
| `static final int ERROR_EUICC_MISSING = 10006` |  |
| `static final int ERROR_INCOMPATIBLE_CARRIER = 10003` |  |
| `static final int ERROR_INSTALL_PROFILE = 10009` |  |
| `static final int ERROR_INVALID_ACTIVATION_CODE = 10001` |  |
| `static final int ERROR_INVALID_CONFIRMATION_CODE = 10002` |  |
| `static final int ERROR_INVALID_RESPONSE = 10015` |  |
| `static final int ERROR_NO_PROFILES_AVAILABLE = 10013` |  |
| `static final int ERROR_OPERATION_BUSY = 10016` |  |
| `static final int ERROR_SIM_MISSING = 10008` |  |
| `static final int ERROR_TIME_OUT = 10005` |  |
| `static final int ERROR_UNSUPPORTED_VERSION = 10007` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DETAILED_CODE"` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DOWNLOADABLE_SUBSCRIPTION"` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_ERROR_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_ERROR_CODE"` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_OPERATION_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_OPERATION_CODE"` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_SMDX_REASON_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_REASON_CODE"` |  |
| `static final String EXTRA_EMBEDDED_SUBSCRIPTION_SMDX_SUBJECT_CODE = "android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_SMDX_SUBJECT_CODE"` |  |
| `static final String EXTRA_USE_QR_SCANNER = "android.telephony.euicc.extra.USE_QR_SCANNER"` |  |
| `static final String META_DATA_CARRIER_ICON = "android.telephony.euicc.carriericon"` |  |
| `static final int OPERATION_APDU = 8` |  |
| `static final int OPERATION_DOWNLOAD = 5` |  |
| `static final int OPERATION_EUICC_CARD = 3` |  |
| `static final int OPERATION_EUICC_GSMA = 7` |  |
| `static final int OPERATION_HTTP = 11` |  |
| `static final int OPERATION_METADATA = 6` |  |
| `static final int OPERATION_SIM_SLOT = 2` |  |
| `static final int OPERATION_SMDX = 9` |  |
| `static final int OPERATION_SMDX_SUBJECT_REASON_CODE = 10` |  |
| `static final int OPERATION_SWITCH = 4` |  |
| `static final int OPERATION_SYSTEM = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isEnabled()` |  |
| `void startResolutionActivity(android.app.Activity, int, android.content.Intent, android.app.PendingIntent) throws android.content.IntentSender.SendIntentException` |  |

---

## Package: `android.telephony.gsm`

### `class GsmCellLocation`

- **继承：** `android.telephony.CellLocation`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GsmCellLocation()` |  |
| `GsmCellLocation(android.os.Bundle)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void fillInNotifierBundle(android.os.Bundle)` |  |
| `int getCid()` |  |
| `int getLac()` |  |
| `int getPsc()` |  |
| `void setLacAndCid(int, int)` |  |
| `void setStateInvalid()` |  |

---

### `class final SmsManager` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class SmsMessage` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `enum SmsMessage.MessageClass` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static SmsMessage.SubmitPdu` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

## Package: `android.telephony.ims`

### `class final ImsException`

- **继承：** `java.lang.Exception`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CODE_ERROR_INVALID_SUBSCRIPTION = 3` |  |
| `static final int CODE_ERROR_SERVICE_UNAVAILABLE = 1` |  |
| `static final int CODE_ERROR_UNSPECIFIED = 0` |  |
| `static final int CODE_ERROR_UNSUPPORTED_OPERATION = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCode()` |  |

---

### `class ImsManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_WFC_IMS_REGISTRATION_ERROR = "android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR"` |  |
| `static final String EXTRA_WFC_REGISTRATION_FAILURE_MESSAGE = "android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_MESSAGE"` |  |
| `static final String EXTRA_WFC_REGISTRATION_FAILURE_TITLE = "android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_TITLE"` |  |

---

### `class ImsMmTelManager`

- **实现：** `android.telephony.ims.RegistrationManager`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int WIFI_MODE_CELLULAR_PREFERRED = 1` |  |
| `static final int WIFI_MODE_WIFI_ONLY = 0` |  |
| `static final int WIFI_MODE_WIFI_PREFERRED = 2` |  |

---

### `class static ImsMmTelManager.CapabilityCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImsMmTelManager.CapabilityCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCapabilitiesStatusChanged(@NonNull android.telephony.ims.feature.MmTelFeature.MmTelCapabilities)` |  |

---

### `class ImsRcsManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_SHOW_CAPABILITY_DISCOVERY_OPT_IN = "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN"` |  |

---

### `class final ImsReasonInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImsReasonInfo(int, int, @Nullable String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CODE_ACCESS_CLASS_BLOCKED = 1512` |  |
| `static final int CODE_ANSWERED_ELSEWHERE = 1014` |  |
| `static final int CODE_BLACKLISTED_CALL_ID = 506` |  |
| `static final int CODE_CALL_BARRED = 240` |  |
| `static final int CODE_CALL_DROP_IWLAN_TO_LTE_UNAVAILABLE = 1100` |  |
| `static final int CODE_CALL_END_CAUSE_CALL_PULL = 1016` |  |
| `static final int CODE_CALL_PULL_OUT_OF_SYNC = 1015` |  |
| `static final int CODE_DATA_DISABLED = 1406` |  |
| `static final int CODE_DATA_LIMIT_REACHED = 1405` |  |
| `static final int CODE_DIAL_MODIFIED_TO_DIAL = 246` |  |
| `static final int CODE_DIAL_MODIFIED_TO_DIAL_VIDEO = 247` |  |
| `static final int CODE_DIAL_MODIFIED_TO_SS = 245` |  |
| `static final int CODE_DIAL_MODIFIED_TO_USSD = 244` |  |
| `static final int CODE_DIAL_VIDEO_MODIFIED_TO_DIAL = 248` |  |
| `static final int CODE_DIAL_VIDEO_MODIFIED_TO_DIAL_VIDEO = 249` |  |
| `static final int CODE_DIAL_VIDEO_MODIFIED_TO_SS = 250` |  |
| `static final int CODE_DIAL_VIDEO_MODIFIED_TO_USSD = 251` |  |
| `static final int CODE_ECBM_NOT_SUPPORTED = 901` |  |
| `static final int CODE_EMERGENCY_PERM_FAILURE = 364` |  |
| `static final int CODE_EMERGENCY_TEMP_FAILURE = 363` |  |
| `static final int CODE_EPDG_TUNNEL_ESTABLISH_FAILURE = 1400` |  |
| `static final int CODE_EPDG_TUNNEL_LOST_CONNECTION = 1402` |  |
| `static final int CODE_EPDG_TUNNEL_REKEY_FAILURE = 1401` |  |
| `static final int CODE_FDN_BLOCKED = 241` |  |
| `static final int CODE_IKEV2_AUTH_FAILURE = 1408` |  |
| `static final int CODE_IMEI_NOT_ACCEPTED = 243` |  |
| `static final int CODE_IWLAN_DPD_FAILURE = 1300` |  |
| `static final int CODE_LOCAL_CALL_BUSY = 142` |  |
| `static final int CODE_LOCAL_CALL_CS_RETRY_REQUIRED = 146` |  |
| `static final int CODE_LOCAL_CALL_DECLINE = 143` |  |
| `static final int CODE_LOCAL_CALL_EXCEEDED = 141` |  |
| `static final int CODE_LOCAL_CALL_RESOURCE_RESERVATION_FAILED = 145` |  |
| `static final int CODE_LOCAL_CALL_TERMINATED = 148` |  |
| `static final int CODE_LOCAL_CALL_VCC_ON_PROGRESSING = 144` |  |
| `static final int CODE_LOCAL_CALL_VOLTE_RETRY_REQUIRED = 147` |  |
| `static final int CODE_LOCAL_ENDED_BY_CONFERENCE_MERGE = 108` |  |
| `static final int CODE_LOCAL_HO_NOT_FEASIBLE = 149` |  |
| `static final int CODE_LOCAL_ILLEGAL_ARGUMENT = 101` |  |
| `static final int CODE_LOCAL_ILLEGAL_STATE = 102` |  |
| `static final int CODE_LOCAL_IMS_SERVICE_DOWN = 106` |  |
| `static final int CODE_LOCAL_INTERNAL_ERROR = 103` |  |
| `static final int CODE_LOCAL_LOW_BATTERY = 112` |  |
| `static final int CODE_LOCAL_NETWORK_IP_CHANGED = 124` |  |
| `static final int CODE_LOCAL_NETWORK_NO_LTE_COVERAGE = 122` |  |
| `static final int CODE_LOCAL_NETWORK_NO_SERVICE = 121` |  |
| `static final int CODE_LOCAL_NETWORK_ROAMING = 123` |  |
| `static final int CODE_LOCAL_NOT_REGISTERED = 132` |  |
| `static final int CODE_LOCAL_NO_PENDING_CALL = 107` |  |
| `static final int CODE_LOCAL_POWER_OFF = 111` |  |
| `static final int CODE_LOCAL_SERVICE_UNAVAILABLE = 131` |  |
| `static final int CODE_LOW_BATTERY = 505` |  |
| `static final int CODE_MAXIMUM_NUMBER_OF_CALLS_REACHED = 1403` |  |
| `static final int CODE_MEDIA_INIT_FAILED = 401` |  |
| `static final int CODE_MEDIA_NOT_ACCEPTABLE = 403` |  |
| `static final int CODE_MEDIA_NO_DATA = 402` |  |
| `static final int CODE_MEDIA_UNSPECIFIED = 404` |  |
| `static final int CODE_MULTIENDPOINT_NOT_SUPPORTED = 902` |  |
| `static final int CODE_NETWORK_DETACH = 1513` |  |
| `static final int CODE_NETWORK_REJECT = 1504` |  |
| `static final int CODE_NETWORK_RESP_TIMEOUT = 1503` |  |
| `static final int CODE_NO_CSFB_IN_CS_ROAM = 1516` |  |
| `static final int CODE_NO_VALID_SIM = 1501` |  |
| `static final int CODE_OEM_CAUSE_1 = 61441` |  |
| `static final int CODE_OEM_CAUSE_10 = 61450` |  |
| `static final int CODE_OEM_CAUSE_11 = 61451` |  |
| `static final int CODE_OEM_CAUSE_12 = 61452` |  |
| `static final int CODE_OEM_CAUSE_13 = 61453` |  |
| `static final int CODE_OEM_CAUSE_14 = 61454` |  |
| `static final int CODE_OEM_CAUSE_15 = 61455` |  |
| `static final int CODE_OEM_CAUSE_2 = 61442` |  |
| `static final int CODE_OEM_CAUSE_3 = 61443` |  |
| `static final int CODE_OEM_CAUSE_4 = 61444` |  |
| `static final int CODE_OEM_CAUSE_5 = 61445` |  |
| `static final int CODE_OEM_CAUSE_6 = 61446` |  |
| `static final int CODE_OEM_CAUSE_7 = 61447` |  |
| `static final int CODE_OEM_CAUSE_8 = 61448` |  |
| `static final int CODE_OEM_CAUSE_9 = 61449` |  |
| `static final int CODE_RADIO_ACCESS_FAILURE = 1505` |  |
| `static final int CODE_RADIO_INTERNAL_ERROR = 1502` |  |
| `static final int CODE_RADIO_LINK_FAILURE = 1506` |  |
| `static final int CODE_RADIO_LINK_LOST = 1507` |  |
| `static final int CODE_RADIO_OFF = 1500` |  |
| `static final int CODE_RADIO_RELEASE_ABNORMAL = 1511` |  |
| `static final int CODE_RADIO_RELEASE_NORMAL = 1510` |  |
| `static final int CODE_RADIO_SETUP_FAILURE = 1509` |  |
| `static final int CODE_RADIO_UPLINK_FAILURE = 1508` |  |
| `static final int CODE_REGISTRATION_ERROR = 1000` |  |
| `static final int CODE_REJECTED_ELSEWHERE = 1017` |  |
| `static final int CODE_REJECT_1X_COLLISION = 1603` |  |
| `static final int CODE_REJECT_CALL_ON_OTHER_SUB = 1602` |  |
| `static final int CODE_REJECT_CALL_TYPE_NOT_ALLOWED = 1605` |  |
| `static final int CODE_REJECT_CONFERENCE_TTY_NOT_ALLOWED = 1617` |  |
| `static final int CODE_REJECT_INTERNAL_ERROR = 1612` |  |
| `static final int CODE_REJECT_MAX_CALL_LIMIT_REACHED = 1608` |  |
| `static final int CODE_REJECT_ONGOING_CALL_SETUP = 1607` |  |
| `static final int CODE_REJECT_ONGOING_CALL_TRANSFER = 1611` |  |
| `static final int CODE_REJECT_ONGOING_CALL_UPGRADE = 1616` |  |
| `static final int CODE_REJECT_ONGOING_CALL_WAITING_DISABLED = 1601` |  |
| `static final int CODE_REJECT_ONGOING_CONFERENCE_CALL = 1618` |  |
| `static final int CODE_REJECT_ONGOING_CS_CALL = 1621` |  |
| `static final int CODE_REJECT_ONGOING_E911_CALL = 1606` |  |
| `static final int CODE_REJECT_ONGOING_ENCRYPTED_CALL = 1620` |  |
| `static final int CODE_REJECT_ONGOING_HANDOVER = 1614` |  |
| `static final int CODE_REJECT_QOS_FAILURE = 1613` |  |
| `static final int CODE_REJECT_SERVICE_NOT_REGISTERED = 1604` |  |
| `static final int CODE_REJECT_UNKNOWN = 1600` |  |
| `static final int CODE_REJECT_UNSUPPORTED_SDP_HEADERS = 1610` |  |
| `static final int CODE_REJECT_UNSUPPORTED_SIP_HEADERS = 1609` |  |
| `static final int CODE_REJECT_VT_AVPF_NOT_ALLOWED = 1619` |  |
| `static final int CODE_REJECT_VT_TTY_NOT_ALLOWED = 1615` |  |
| `static final int CODE_REMOTE_CALL_DECLINE = 1404` |  |
| `static final int CODE_SESSION_MODIFICATION_FAILED = 1517` |  |
| `static final int CODE_SIP_ALTERNATE_EMERGENCY_CALL = 1514` |  |
| `static final int CODE_SIP_AMBIGUOUS = 376` |  |
| `static final int CODE_SIP_BAD_ADDRESS = 337` |  |
| `static final int CODE_SIP_BAD_REQUEST = 331` |  |
| `static final int CODE_SIP_BUSY = 338` |  |
| `static final int CODE_SIP_CALL_OR_TRANS_DOES_NOT_EXIST = 372` |  |
| `static final int CODE_SIP_CLIENT_ERROR = 342` |  |
| `static final int CODE_SIP_EXTENSION_REQUIRED = 370` |  |
| `static final int CODE_SIP_FORBIDDEN = 332` |  |
| `static final int CODE_SIP_GLOBAL_ERROR = 362` |  |
| `static final int CODE_SIP_INTERVAL_TOO_BRIEF = 371` |  |
| `static final int CODE_SIP_LOOP_DETECTED = 373` |  |
| `static final int CODE_SIP_METHOD_NOT_ALLOWED = 366` |  |
| `static final int CODE_SIP_NOT_ACCEPTABLE = 340` |  |
| `static final int CODE_SIP_NOT_FOUND = 333` |  |
| `static final int CODE_SIP_NOT_REACHABLE = 341` |  |
| `static final int CODE_SIP_NOT_SUPPORTED = 334` |  |
| `static final int CODE_SIP_PROXY_AUTHENTICATION_REQUIRED = 367` |  |
| `static final int CODE_SIP_REDIRECTED = 321` |  |
| `static final int CODE_SIP_REQUEST_CANCELLED = 339` |  |
| `static final int CODE_SIP_REQUEST_ENTITY_TOO_LARGE = 368` |  |
| `static final int CODE_SIP_REQUEST_PENDING = 377` |  |
| `static final int CODE_SIP_REQUEST_TIMEOUT = 335` |  |
| `static final int CODE_SIP_REQUEST_URI_TOO_LARGE = 369` |  |
| `static final int CODE_SIP_SERVER_ERROR = 354` |  |
| `static final int CODE_SIP_SERVER_INTERNAL_ERROR = 351` |  |
| `static final int CODE_SIP_SERVER_TIMEOUT = 353` |  |
| `static final int CODE_SIP_SERVICE_UNAVAILABLE = 352` |  |
| `static final int CODE_SIP_TEMPRARILY_UNAVAILABLE = 336` |  |
| `static final int CODE_SIP_TOO_MANY_HOPS = 374` |  |
| `static final int CODE_SIP_TRANSACTION_DOES_NOT_EXIST = 343` |  |
| `static final int CODE_SIP_UNDECIPHERABLE = 378` |  |
| `static final int CODE_SIP_USER_MARKED_UNWANTED = 365` |  |
| `static final int CODE_SIP_USER_REJECTED = 361` |  |
| `static final int CODE_SUPP_SVC_CANCELLED = 1202` |  |
| `static final int CODE_SUPP_SVC_FAILED = 1201` |  |
| `static final int CODE_SUPP_SVC_REINVITE_COLLISION = 1203` |  |
| `static final int CODE_TIMEOUT_1XX_WAITING = 201` |  |
| `static final int CODE_TIMEOUT_NO_ANSWER = 202` |  |
| `static final int CODE_TIMEOUT_NO_ANSWER_CALL_UPDATE = 203` |  |
| `static final int CODE_UNSPECIFIED = 0` |  |
| `static final int CODE_USER_CANCELLED_SESSION_MODIFICATION = 512` |  |
| `static final int CODE_USER_DECLINE = 504` |  |
| `static final int CODE_USER_IGNORE = 503` |  |
| `static final int CODE_USER_NOANSWER = 502` |  |
| `static final int CODE_USER_REJECTED_SESSION_MODIFICATION = 511` |  |
| `static final int CODE_USER_TERMINATED = 501` |  |
| `static final int CODE_USER_TERMINATED_BY_REMOTE = 510` |  |
| `static final int CODE_UT_CB_PASSWORD_MISMATCH = 821` |  |
| `static final int CODE_UT_NETWORK_ERROR = 804` |  |
| `static final int CODE_UT_NOT_SUPPORTED = 801` |  |
| `static final int CODE_UT_OPERATION_NOT_ALLOWED = 803` |  |
| `static final int CODE_UT_SERVICE_UNAVAILABLE = 802` |  |
| `static final int CODE_UT_SS_MODIFIED_TO_DIAL = 822` |  |
| `static final int CODE_UT_SS_MODIFIED_TO_DIAL_VIDEO = 825` |  |
| `static final int CODE_UT_SS_MODIFIED_TO_SS = 824` |  |
| `static final int CODE_UT_SS_MODIFIED_TO_USSD = 823` |  |
| `static final int CODE_WIFI_LOST = 1407` |  |
| `static final int EXTRA_CODE_CALL_RETRY_BY_SETTINGS = 3` |  |
| `static final int EXTRA_CODE_CALL_RETRY_NORMAL = 1` |  |
| `static final int EXTRA_CODE_CALL_RETRY_SILENT_REDIAL = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCode()` |  |
| `int getExtraCode()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class RcsUceAdapter`


---

### `interface RegistrationManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REGISTRATION_STATE_NOT_REGISTERED = 0` |  |
| `static final int REGISTRATION_STATE_REGISTERED = 2` |  |
| `static final int REGISTRATION_STATE_REGISTERING = 1` |  |

---

### `class static RegistrationManager.RegistrationCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RegistrationManager.RegistrationCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onRegistered(int)` |  |
| `void onRegistering(int)` |  |
| `void onTechnologyChangeFailed(int, @NonNull android.telephony.ims.ImsReasonInfo)` |  |
| `void onUnregistered(@NonNull android.telephony.ims.ImsReasonInfo)` |  |

---

## Package: `android.telephony.ims.feature`

### `class MmTelFeature`


---

### `class static MmTelFeature.MmTelCapabilities`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CAPABILITY_TYPE_SMS = 8` |  |
| `static final int CAPABILITY_TYPE_UT = 4` |  |
| `static final int CAPABILITY_TYPE_VIDEO = 2` |  |
| `static final int CAPABILITY_TYPE_VOICE = 1` |  |

---

## Package: `android.telephony.mbms`

### `class DownloadProgressListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DownloadProgressListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onProgressUpdated(android.telephony.mbms.DownloadRequest, android.telephony.mbms.FileInfo, int, int, int, int)` |  |

---

### `class final DownloadRequest`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.net.Uri getDestinationUri()` |  |
| `String getFileServiceId()` |  |
| `static int getMaxAppIntentSize()` |  |
| `static int getMaxDestinationUriSize()` |  |
| `android.net.Uri getSourceUri()` |  |
| `int getSubscriptionId()` |  |
| `byte[] toByteArray()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static DownloadRequest.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DownloadRequest.Builder(@NonNull android.net.Uri, @NonNull android.net.Uri)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.telephony.mbms.DownloadRequest build()` |  |
| `static android.telephony.mbms.DownloadRequest.Builder fromDownloadRequest(android.telephony.mbms.DownloadRequest)` |  |
| `static android.telephony.mbms.DownloadRequest.Builder fromSerializedRequest(byte[])` |  |
| `android.telephony.mbms.DownloadRequest.Builder setAppIntent(android.content.Intent)` |  |
| `android.telephony.mbms.DownloadRequest.Builder setServiceInfo(android.telephony.mbms.FileServiceInfo)` |  |
| `android.telephony.mbms.DownloadRequest.Builder setSubscriptionId(int)` |  |

---

### `class DownloadStatusListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DownloadStatusListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onStatusUpdated(android.telephony.mbms.DownloadRequest, android.telephony.mbms.FileInfo, int)` |  |

---

### `class final FileInfo`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getMimeType()` |  |
| `android.net.Uri getUri()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final FileServiceInfo`

- **继承：** `android.telephony.mbms.ServiceInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.List<android.telephony.mbms.FileInfo> getFiles()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class GroupCall`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REASON_BY_USER_REQUEST = 1` |  |
| `static final int REASON_FREQUENCY_CONFLICT = 3` |  |
| `static final int REASON_LEFT_MBMS_BROADCAST_AREA = 6` |  |
| `static final int REASON_NONE = 0` |  |
| `static final int REASON_NOT_CONNECTED_TO_HOMECARRIER_LTE = 5` |  |
| `static final int REASON_OUT_OF_MEMORY = 4` |  |
| `static final int STATE_STALLED = 3` |  |
| `static final int STATE_STARTED = 2` |  |
| `static final int STATE_STOPPED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `long getTmgi()` |  |
| `void updateGroupCall(@NonNull java.util.List<java.lang.Integer>, @NonNull java.util.List<java.lang.Integer>)` |  |

---

### `interface GroupCallCallback`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SIGNAL_STRENGTH_UNAVAILABLE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `default void onBroadcastSignalStrengthUpdated(@IntRange(from=0xffffffff, to=4) int)` |  |
| `default void onError(int, @Nullable String)` |  |
| `default void onGroupCallStateChanged(int, int)` |  |

---

### `class MbmsDownloadReceiver`

- **继承：** `android.content.BroadcastReceiver`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MbmsDownloadReceiver()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onReceive(android.content.Context, android.content.Intent)` |  |

---

### `class MbmsDownloadSessionCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MbmsDownloadSessionCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onError(int, String)` |  |
| `void onFileServicesUpdated(java.util.List<android.telephony.mbms.FileServiceInfo>)` |  |
| `void onMiddlewareReady()` |  |

---

### `class MbmsErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_MIDDLEWARE_LOST = 3` |  |
| `static final int ERROR_MIDDLEWARE_NOT_BOUND = 2` |  |
| `static final int ERROR_NO_UNIQUE_MIDDLEWARE = 1` |  |
| `static final int SUCCESS = 0` |  |
| `static final int UNKNOWN = -1` |  |

---

### `class static MbmsErrors.DownloadErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_CANNOT_CHANGE_TEMP_FILE_ROOT = 401` |  |
| `static final int ERROR_UNKNOWN_DOWNLOAD_REQUEST = 402` |  |
| `static final int ERROR_UNKNOWN_FILE_INFO = 403` |  |

---

### `class static MbmsErrors.GeneralErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_CARRIER_CHANGE_NOT_ALLOWED = 207` |  |
| `static final int ERROR_IN_E911 = 204` |  |
| `static final int ERROR_MIDDLEWARE_NOT_YET_READY = 201` |  |
| `static final int ERROR_MIDDLEWARE_TEMPORARILY_UNAVAILABLE = 203` |  |
| `static final int ERROR_NOT_CONNECTED_TO_HOME_CARRIER_LTE = 205` |  |
| `static final int ERROR_OUT_OF_MEMORY = 202` |  |
| `static final int ERROR_UNABLE_TO_READ_SIM = 206` |  |

---

### `class static MbmsErrors.GroupCallErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_DUPLICATE_START_GROUP_CALL = 502` |  |
| `static final int ERROR_UNABLE_TO_START_SERVICE = 501` |  |

---

### `class static MbmsErrors.InitializationErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_APP_PERMISSIONS_NOT_GRANTED = 102` |  |
| `static final int ERROR_DUPLICATE_INITIALIZE = 101` |  |
| `static final int ERROR_UNABLE_TO_INITIALIZE = 103` |  |

---

### `class static MbmsErrors.StreamingErrors`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_CONCURRENT_SERVICE_LIMIT_REACHED = 301` |  |
| `static final int ERROR_DUPLICATE_START_STREAM = 303` |  |
| `static final int ERROR_UNABLE_TO_START_SERVICE = 302` |  |

---

### `interface MbmsGroupCallSessionCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `default void onAvailableSaisUpdated(@NonNull java.util.List<java.lang.Integer>, @NonNull java.util.List<java.util.List<java.lang.Integer>>)` |  |
| `default void onError(int, @Nullable String)` |  |
| `default void onMiddlewareReady()` |  |
| `default void onServiceInterfaceAvailable(@NonNull String, int)` |  |

---

### `class MbmsStreamingSessionCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MbmsStreamingSessionCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onError(int, @Nullable String)` |  |
| `void onMiddlewareReady()` |  |
| `void onStreamingServicesUpdated(java.util.List<android.telephony.mbms.StreamingServiceInfo>)` |  |

---

### `class ServiceInfo`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.List<java.util.Locale> getLocales()` |  |
| `String getServiceClassName()` |  |
| `String getServiceId()` |  |
| `java.util.Date getSessionEndTime()` |  |
| `java.util.Date getSessionStartTime()` |  |

---

### `class StreamingService`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BROADCAST_METHOD = 1` |  |
| `static final int REASON_BY_USER_REQUEST = 1` |  |
| `static final int REASON_END_OF_SESSION = 2` |  |
| `static final int REASON_FREQUENCY_CONFLICT = 3` |  |
| `static final int REASON_LEFT_MBMS_BROADCAST_AREA = 6` |  |
| `static final int REASON_NONE = 0` |  |
| `static final int REASON_NOT_CONNECTED_TO_HOMECARRIER_LTE = 5` |  |
| `static final int REASON_OUT_OF_MEMORY = 4` |  |
| `static final int STATE_STALLED = 3` |  |
| `static final int STATE_STARTED = 2` |  |
| `static final int STATE_STOPPED = 1` |  |
| `static final int UNICAST_METHOD = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `android.telephony.mbms.StreamingServiceInfo getInfo()` |  |

---

### `class StreamingServiceCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StreamingServiceCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SIGNAL_STRENGTH_UNAVAILABLE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onBroadcastSignalStrengthUpdated(int)` |  |
| `void onError(int, @Nullable String)` |  |
| `void onMediaDescriptionUpdated()` |  |
| `void onStreamMethodUpdated(int)` |  |
| `void onStreamStateUpdated(int, int)` |  |

---

### `class final StreamingServiceInfo`

- **继承：** `android.telephony.mbms.ServiceInfo`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

