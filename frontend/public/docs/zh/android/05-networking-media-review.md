# Code 审查 Report: Networking, Telephony, and Media APIs

**Source**: Android 11 AOSP (`~/aosp-android-11/`)
**Scope**: Connectivity, Wi-Fi, Telephony, Media, Bluetooth, and NFC system APIs
**Date**: 2026-03-10

---

## Table of Contents

1. [Networking APIs](#1-networking-apis)
   - [ConnectivityManager](#11-connectivitymanager)
   - [NetworkInfo (Deprecated)](#12-networkinfo-deprecated)
   - [Network](#13-network)
   - [Uri](#14-uri)
2. [Wi-Fi API](#2-wi-fi-api)
   - [WifiManager](#21-wifimanager)
3. [Telephony APIs](#3-telephony-apis)
   - [TelephonyManager](#31-telephonymanager)
   - [SmsManager](#32-smsmanager)
4. [Media APIs](#4-media-apis)
   - [MediaPlayer](#41-mediaplayer)
   - [MediaRecorder](#42-mediarecorder)
   - [AudioManager](#43-audiomanager)
5. [Camera2 API](#5-camera2-api)
6. [Bluetooth APIs](#6-bluetooth-apis)
   - [BluetoothAdapter](#61-bluetoothadapter)
   - [BluetoothDevice](#62-bluetoothdevice)
7. [NFC API](#7-nfc-api)
   - [NfcAdapter](#71-nfcadapter)
8. [HttpURLConnection Patterns](#8-httpurlconnection-patterns)
9. [Cross-Cutting Observations](#9-cross-cutting-observations)

---

## 1. Networking APIs

### 1.1 ConnectivityManager

**File**: `frameworks/base/core/java/android/net/ConnectivityManager.java` (4770 lines)

#### Class Purpose and Responsibility

`ConnectivityManager` is the primary system service (`Context.CONNECTIVITY_SERVICE`) for querying and managing network connectivity. Annotated with `@SystemService` (line 104), it:
- Monitors network connections (Wi-Fi, cellular, Ethernet, VPN, etc.)
- Sends broadcast intents on connectivity changes
- Handles network failover
- Provides APIs for querying network state and requesting specific networks
- Manages tethering operations

#### Key Public APIs for App Developers

**Network State Queries (mostly deprecated):**
- `getActiveNetworkInfo()` (line 996) -- Returns `NetworkInfo` for the default data network. **Deprecated** in favor of `NetworkCallback`.
  - Permission: `ACCESS_NETWORK_STATE`
- `getActiveNetwork()` (line 1016) -- Returns the `Network` object for the current default network.
- `getNetworkInfo(int networkType)` (line 1201) -- Get info for a specific network type. **Deprecated**.
- `getNetworkCapabilities(Network)` (line 1383) -- Returns `NetworkCapabilities` for a given network.
- `getLinkProperties(Network)` (line 1366) -- Returns `LinkProperties` for a given network.
- `isActiveNetworkMetered()` (line 3059) -- Check if the active network is metered.

**Modern Network Request API (the recommended approach):**
- `requestNetwork(NetworkRequest, NetworkCallback)` (line 3814) -- Request a network matching specified capabilities; the system will try to bring one up.
  - Permission: `CHANGE_NETWORK_STATE` or `Settings.System.canWrite`
  - Limit: 100 outstanding requests per app UID
- `requestNetwork(NetworkRequest, NetworkCallback, int timeoutMs)` (line 3868) -- Same but with timeout; `onUnavailable()` is called if no match found.
- `requestNetwork(NetworkRequest, PendingIntent)` (line 3977) -- PendingIntent variant; survives app process death.
- `registerNetworkCallback(NetworkRequest, NetworkCallback)` (line 4047) -- Passively listen for networks matching criteria without requesting one be brought up.
  - Permission: `ACCESS_NETWORK_STATE`
- `registerDefaultNetworkCallback(NetworkCallback)` (line 4157) -- Listen for changes to the system default network.
  - Permission: `ACCESS_NETWORK_STATE`
- `unregisterNetworkCallback(NetworkCallback)` (line 4228) -- Release a callback and potentially the associated network.

**Utility Methods:**
- `bindProcessToNetwork(Network)` -- Bind all process traffic to a specific network.
- `isNetworkTypeValid(int)` (line 841) -- **Deprecated**. Validates legacy network type constants.
- `isNetworkTypeMobile(int)` (line 911) -- **Deprecated**. Checks if type is cellular.

#### NetworkCallback (Inner Class, line 3287)

The callback-based API is the modern, recommended approach. Key callback methods:
```
onAvailable(Network)                                  -- Network is ready (line 3361)
onLost(Network)                                       -- Network disconnected (line 3401)
onLosing(Network, int maxMsToLive)                    -- Network about to be lost (line 3380)
onUnavailable()                                       -- No matching network found (line 3411)
onCapabilitiesChanged(Network, NetworkCapabilities)   -- Capabilities changed (line 3428)
onLinkPropertiesChanged(Network, LinkProperties)      -- Link properties changed (line 3444)
onBlockedStatusChanged(Network, boolean)              -- Access blocked/unblocked (line 3491)
```

**Important guidance** from the source (lines 3351-3357): Do NOT call synchronous methods like `getNetworkCapabilities()` or `getLinkProperties()` inside callbacks -- they are prone to race conditions. Use the arguments provided to the callback methods instead.

#### Deprecated Constants (TYPE_* network types, lines 527-707)

All `TYPE_*` constants are deprecated:
- `TYPE_MOBILE = 0`, `TYPE_WIFI = 1`, `TYPE_WIMAX = 6`, `TYPE_BLUETOOTH = 7`, `TYPE_ETHERNET = 9`, `TYPE_VPN = 17`
- Developers should use `NetworkCapabilities.hasTransport()` with `TRANSPORT_*` constants instead.

#### Broadcast Actions

- `CONNECTIVITY_ACTION` (line 146) -- **Deprecated** since API 24 (cannot be declared in manifest). Use `NetworkCallback` instead.
- `ACTION_CAPTIVE_PORTAL_SIGN_IN` (line 175) -- When a captive portal is detected.

#### Hidden/System APIs

- Tethering operations: `startTethering()` (line 2450), `stopTethering()` (line 2527) -- `@SystemApi`
- `TETHERING_WIFI`, `TETHERING_USB`, `TETHERING_BLUETOOTH` -- `@SystemApi` constants
- `registerNetworkAgent()` (line 3253) -- Requires `NETWORK_FACTORY` permission
- `setGlobalProxy()` (line 2964) -- `@hide`
- `setAirplaneMode()` (line 3146) -- `@hide`
- Private DNS mode constants (lines 798-815) -- `@hide`
- `setAcceptUnvalidated()` (line 4288) -- `@hide`, requires `NETWORK_SETTINGS`
- `setAcceptPartialConnectivity()` (line 4311) -- `@hide`, requires `NETWORK_STACK`

#### State Management

Internal callback dispatch uses `CallbackHandler` (line 3561), a `Handler`-based message-passing system. Callback messages include `CALLBACK_AVAILABLE`, `CALLBACK_LOST`, `CALLBACK_CAP_CHANGED`, etc. (lines 3520-3540).

A static `HashMap<NetworkRequest, NetworkCallback>` (`sCallbacks`, line 3661) tracks all registered callbacks. Cleanup is performed in `unregisterNetworkCallback()` by iterating this map, removing entries, and calling `mService.releaseNetworkRequest()`.

---

### 1.2 NetworkInfo (Deprecated)

**File**: `frameworks/base/core/java/android/net/NetworkInfo.java` (599 lines)

#### Class Purpose

`NetworkInfo` describes the status of a network interface. The **entire class** is deprecated (line 47) -- apps should use `NetworkCallback`, `NetworkCapabilities`, and `LinkProperties` instead.

#### State Model

Two-level state model (lines 75-114):

**Coarse-grained (`State` enum)**: `CONNECTING`, `CONNECTED`, `SUSPENDED`, `DISCONNECTING`, `DISCONNECTED`, `UNKNOWN`

**Fine-grained (`DetailedState` enum)**: `IDLE`, `SCANNING`, `CONNECTING`, `AUTHENTICATING`, `OBTAINING_IPADDR`, `CONNECTED`, `SUSPENDED`, `DISCONNECTING`, `DISCONNECTED`, `FAILED`, `BLOCKED`, `VERIFYING_POOR_LINK`, `CAPTIVE_PORTAL_CHECK`

The mapping between them is defined in a static `EnumMap` (lines 121-138).

#### Key Methods (all deprecated)

- `isConnected()` (line 327) -- Whether the network is in `CONNECTED` state.
- `isConnectedOrConnecting()` (line 307)
- `isAvailable()` (line 355) -- Since Android L, always returns `true`.
- `isRoaming()` (line 419) -- Use `NET_CAPABILITY_NOT_ROAMING` instead.
- `getType()` (line 217) -- Use `NetworkCapabilities.hasTransport()` instead.
- `getState()` / `getDetailedState()` (lines 450, 468)

#### Hidden APIs

- `setType()`, `setSubtype()`, `setIsAvailable()`, `setFailover()`, `setRoaming()` -- All `@hide` / `@UnsupportedAppUsage`

---

### 1.3 Network

**File**: `frameworks/base/core/java/android/net/Network.java` (522 lines)

#### Class Purpose

`Network` identifies a specific network and provides methods to direct traffic through it. Implements `Parcelable` for IPC transfer. Obtained via `NetworkCallback.onAvailable()`.

#### Key Public APIs

- `getSocketFactory()` (line 287) -- Returns a `SocketFactory` whose sockets are bound to this network.
- `openConnection(URL)` (line 339) -- Opens an `HttpURLConnection` on this specific network, supporting HTTP/HTTPS.
- `openConnection(URL, Proxy)` (line 366) -- Same with explicit proxy.
- `bindSocket(Socket)` / `bindSocket(DatagramSocket)` / `bindSocket(FileDescriptor)` (lines 391, 379, 404) -- Bind an existing socket to this network.
- `getAllByName(String)` (line 142) -- DNS resolution on this network.
- `getByName(String)` (line 155) -- DNS resolution on this network.
- `getNetworkHandle()` (line 453) -- Returns an opaque long handle for NDK use. Uses `HANDLE_MAGIC = 0xcafed00dL` (line 91) for obfuscation.
- `fromNetworkHandle(long)` (line 437) -- Static factory from NDK handle.

#### Hidden/System APIs

- `netId` field (line 68) -- `@UnsupportedAppUsage`, the internal network identifier
- `getNetId()` (line 179) -- `@SystemApi @TestApi`
- `getPrivateDnsBypassingCopy()` (line 168) -- `@SystemApi @TestApi`, returns a copy that bypasses Private DNS
- `mPrivateDnsBypass` (line 107) -- transient flag, not parceled

#### Design Note

The `Network` class contains a `HttpURLConnectionFactory` per instance (line 75), with a dedicated connection pool. The source contains a TODO comment (lines 298-312) noting this is suboptimal -- ideally there should be one pool per `netId`, not per `Network` object.

---

### 1.4 Uri

**File**: `frameworks/base/core/java/android/net/Uri.java` (2431 lines)

#### Class Purpose

Immutable URI reference conforming to RFC 2396. Used pervasively across Android for content URIs, intent data, file URIs, and web URLs.

#### Architecture

`Uri` is an abstract class (line 56) with private implementations:
- `StringUri` -- Lazy-parsed from a URI string
- `OpaqueUri` -- Built from scheme + scheme-specific part + fragment
- `HierarchicalUri` -- Full hierarchical URI with authority, path, query, fragment

The class uses a caching pattern with a `NOT_CACHED` sentinel string (line 128) for thread-safe lazy initialization without synchronization.

#### Key Static Factory Methods

- `Uri.parse(String)` (line 457) -- Parse a URI string. Very forgiving -- returns garbage rather than throwing on invalid input.
- `Uri.fromFile(File)` (line 471) -- Create `file:///` URI.
- `Uri.fromParts(String scheme, String ssp, String fragment)` (line 845) -- Create an opaque URI.
- `Uri.withAppendedPath(Uri, String)` (line 2340) -- Append a path segment.
- `Uri.EMPTY` (line 133) -- The empty URI.

#### Key Instance Methods

- `getScheme()`, `getAuthority()`, `getHost()`, `getPort()`, `getPath()`, `getQuery()`, `getFragment()` -- Standard URI component accessors
- `getSchemeSpecificPart()` (line 192) -- Decoded version
- `getEncodedSchemeSpecificPart()` -- Encoded version
- `getQueryParameter(String key)` -- Get a single query parameter value
- `getQueryParameters(String key)` -- Get all values for a query parameter
- `getPathSegments()` -- List of decoded path segments
- `buildUpon()` -- Returns a `Builder` for modifications
- `isHierarchical()` / `isOpaque()` (lines 147, 153)
- `isRelative()` / `isAbsolute()` (lines 163, 171)

#### Builder Pattern

`Uri.Builder` provides a fluent API:
```
new Uri.Builder()
    .scheme("https")
    .authority("example.com")
    .appendPath("api")
    .appendQueryParameter("key", "value")
    .build();
```

---

## 2. Wi-Fi API

### 2.1 WifiManager

**File**: `frameworks/base/wifi/java/android/net/wifi/WifiManager.java` (6239 lines)

#### Class Purpose

`WifiManager` (`Context.WIFI_SERVICE`, line 108) is the primary API for managing Wi-Fi connectivity. It handles:
- Configured network management
- Wi-Fi state control (enable/disable)
- Scanning
- Connection info
- Network suggestions (Android 10+)
- Hotspot/tethering (system)

#### Key Public APIs

**Wi-Fi State:**
- `isWifiEnabled()` (line 2949) -- Check if Wi-Fi is on
- `setWifiEnabled(boolean)` (line 2921) -- Enable/disable Wi-Fi
- `getWifiState()` (line 2936) -- Returns `WIFI_STATE_DISABLED`, `_DISABLING`, `_ENABLED`, `_ENABLING`, `_UNKNOWN`

**Scanning:**
- `startScan()` (line 2716) -- Initiate a Wi-Fi scan (throttled in Android 8+)
- `getScanResults()` -- Get list of `ScanResult` access points

**Connection Info:**
- `getConnectionInfo()` (line 2756) -- Returns `WifiInfo` with SSID, BSSID, RSSI, link speed, etc.
- `getDhcpInfo()` (line 2889) -- Returns `DhcpInfo` with IP, gateway, DNS, etc.

**Network Configuration (Deprecated in API 29+):**
- `addNetwork(WifiConfiguration)` (line 1577) -- Add a new saved network. Returns network ID.
- `updateNetwork(WifiConfiguration)` (line 1618) -- Update a saved network.
- `removeNetwork(int)` (line 2169) -- Remove a saved network.
- `enableNetwork(int, boolean)` (line 2218) -- Enable and optionally connect.
- `disableNetwork(int)` (line 2254) -- Disable a saved network.

**Network Suggestions (Android 10+ replacement):**
- `addNetworkSuggestions(List<WifiNetworkSuggestion>)` -- Suggest networks for auto-connect
- `removeNetworkSuggestions(List<WifiNetworkSuggestion>)` -- Remove suggestions
- `getMaxNumberOfNetworkSuggestionsPerApp()` (line 1995) -- Low RAM: 256, High RAM: 1024 (lines 157-160)

**Signal Utilities:**
- `calculateSignalLevel(int rssi, int numLevels)` (line 2965) -- Static, maps RSSI to level bins (deprecated form)
- `calculateSignalLevel(int rssi)` (line 2986) -- Instance method, uses system-defined max levels
- `compareSignalLevel(int rssiA, int rssiB)` (line 3012) -- Compare two RSSI values

**Connection Control:**
- `disconnect()` (line 2283) -- Disconnect from current network
- `reconnect()` (line 2313) -- Reconnect to the currently active network
- `reassociate()` (line 2337) -- Reassociate to the current access point

#### Required Permissions

- `ACCESS_WIFI_STATE` -- Query Wi-Fi state, get scan results
- `CHANGE_WIFI_STATE` -- Enable/disable Wi-Fi, connect/disconnect
- `ACCESS_FINE_LOCATION` -- Required for `getScanResults()` since Android 8

#### Network Suggestion Status Codes (lines 165-222)

- `STATUS_NETWORK_SUGGESTIONS_SUCCESS = 0`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL = 1`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED = 2`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE = 3` (not returned in Android 11)
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP = 4`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID = 5`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED = 6`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID = 7`

#### Feature Detection Methods

- `isP2pSupported()` (line 2460)
- `isWifiAwareSupported()` (line 2486)
- `is5GHzBandSupported()` (line 2583)
- `is6GHzBandSupported()` (line 2595)
- `isStaApConcurrencySupported()` (line 2495)
- `isWifiStandardSupported(int)` (line 2609)
- `isPasspointSupported()` (line 2453)
- `isDeviceToDeviceRttSupported()` (line 2509)

#### Hidden/System APIs

- `WIFI_CREDENTIAL_CHANGED_ACTION` (line 288) -- `@SystemApi`
- `registerNetworkRequestMatchCallback()` (line 1863) -- `@SystemApi`
- `updateInterfaceIpState()` (line 3033) -- `@SystemApi`

---

## 3. Telephony APIs

### 3.1 TelephonyManager

**File**: `frameworks/base/telephony/java/android/telephony/TelephonyManager.java` (13561 lines)

#### Class Purpose

`TelephonyManager` (`Context.TELEPHONY_SERVICE`, line 156) provides access to telephony services and device information. It handles:
- Device and SIM identification
- Network operator information
- Call state monitoring
- Data network state
- SIM state
- Carrier configuration
- Multi-SIM support

#### Key Public APIs for App Developers

**Device Information:**
- `getDeviceId()` (line 1934) -- IMEI/MEID. **Requires `READ_PRIVILEGED_PHONE_STATE`** (no longer accessible to regular apps in Android 10+).
- `getImei()` / `getImei(int slotIndex)` (lines 2013, 2054) -- IMEI specifically. **Requires `READ_PRIVILEGED_PHONE_STATE`**.
- `getMeid()` / `getMeid(int slotIndex)` (lines 2130, 2170) -- MEID specifically. **Requires `READ_PRIVILEGED_PHONE_STATE`**.
- `getDeviceSoftwareVersion()` (line 1863) -- Requires `READ_PHONE_STATE`.
- `getTypeAllocationCode()` (line 2072) -- TAC of the device (no special permission).
- `getManufacturerCode()` (line 2194) -- Manufacturer code.

**Network Information:**
- `getNetworkOperatorName()` (line 2716) -- Human-readable carrier name.
- `getNetworkOperator()` (line 2743) -- MCC+MNC string.
- `getNetworkCountryIso()` (line 2854) -- ISO country code.
- `isNetworkRoaming()` (line 2825) -- Check roaming status.
- `getNetworkType()` (line ~3025) -- Returns `NETWORK_TYPE_LTE`, `_NR`, etc. Requires `READ_PHONE_STATE`.
- `getDataNetworkType()` (line ~3090) -- Data connection type. Requires `READ_PHONE_STATE`.
- `getVoiceNetworkType()` (line ~3129) -- Voice network type. Requires `READ_PHONE_STATE`.
- `getNetworkSpecifier()` (line 2791)

**SIM Information:**
- `hasIccCard()` (line 3452) -- Check if SIM is present.
- `getSimOperator()` (line 3713) -- SIM operator MCC+MNC.
- `getSimOperatorName()` -- SIM operator name.
- `getSimCountryIso()` -- SIM country code.
- `getSimState()` -- SIM state (absent, PIN required, ready, etc.)

**Phone State:**
- `getPhoneType()` (line 2527) -- `PHONE_TYPE_GSM`, `_CDMA`, `_SIP`, `_NONE`
- `getCurrentPhoneType()` (line 2465)
- `getPhoneCount()` (line 446) -- Number of phone slots.
- `getActiveModemCount()` (line 458) -- Active modem count.

**Location:**
- `getCellLocation()` (line 2329) -- Requires `ACCESS_FINE_LOCATION`.
- `getAllCellInfo()` -- Requires `ACCESS_FINE_LOCATION`.

**Multi-SIM:**
- `createForSubscriptionId(int)` -- Create a `TelephonyManager` for a specific subscription.
- `getActiveModemCount()` (line 458)
- `getSupportedModemCount()` (line 486)
- `isMultiSimEnabled()` (line 543)

#### Required Permissions

| Permission | Methods |
|---|---|
| `READ_PHONE_STATE` | `getDeviceSoftwareVersion()`, `getNetworkType()`, `getDataNetworkType()`, `getVoiceNetworkType()` |
| `READ_PRIVILEGED_PHONE_STATE` | `getDeviceId()`, `getImei()`, `getMeid()`, `getNai()`, `isApplicationOnUicc()` |
| `ACCESS_FINE_LOCATION` | `getCellLocation()`, `getAllCellInfo()` |
| `ACCESS_COARSE_LOCATION` | Some cell info methods |
| `CONTROL_LOCATION_UPDATES` | `enableLocationUpdates()`, `disableLocationUpdates()` |

#### PhoneStateListener Registration

The `listen()` method accepts a `PhoneStateListener` and event flags:
- `LISTEN_CALL_STATE` -- Call state changes
- `LISTEN_DATA_CONNECTION_STATE` -- Data connection state changes
- `LISTEN_SERVICE_STATE` -- Service state changes
- `LISTEN_SIGNAL_STRENGTHS` -- Signal strength changes
- `LISTEN_CELL_LOCATION` -- Cell location changes

#### Hidden/System APIs

The file is extremely large (13,561 lines) with extensive hidden APIs for:
- Call forwarding management (e.g., `getCallForwarding()`)
- Carrier privilege checking
- IMS feature management (lines 87-92)
- Modem activity info
- Network selection modes
- SIM provisioning
- Emergency number handling
- Number verification callbacks

---

### 3.2 SmsManager

**File**: `frameworks/base/telephony/java/android/telephony/SmsManager.java` (3007 lines)

#### Class Purpose

`SmsManager` manages SMS and MMS operations. Uses a singleton pattern with per-subscription instances.

#### Key Public APIs

**Sending Text Messages:**
- `sendTextMessage(String dest, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent)` (line 423) -- Primary text SMS API. The `sentIntent` receives result codes including `RESULT_OK`, `RESULT_ERROR_GENERIC_FAILURE`, `RESULT_ERROR_RADIO_OFF`, `RESULT_ERROR_NO_SERVICE`, etc.
- `sendTextMessage(..., long messageId)` (line 441) -- Variant with diagnostic message ID.
- `sendMultipartTextMessage(String dest, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents)` (line 936) -- For messages exceeding single SMS size.

**Sending Data Messages:**
- `sendDataMessage(String dest, String scAddress, short destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent)` (line 1389) -- Send raw data SMS to a specific port.

**MMS:**
- `sendMultimediaMessage(Context, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent)` (line 2525) -- Send MMS.
- `downloadMultimediaMessage(Context, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent)` (line 2559) -- Download MMS.

**Factory Methods:**
- `SmsManager.getDefault()` (line 1451) -- Get instance for default subscription.
- `SmsManager.getSmsManagerForSubscriptionId(int)` (line 1468) -- Get instance for specific subscription.

**SIM Card Operations (hidden):**
- `copyMessageToIcc()` (line 1678) -- `@SystemApi`
- `deleteMessageFromIcc()` (line 1723) -- `@SystemApi`
- `updateMessageOnIcc()` (line 1766) -- `@SystemApi`

#### MMS Configuration Constants (lines 109-200)

Extensive constants for carrier-specific MMS configuration:
- `MMS_CONFIG_MAX_MESSAGE_SIZE`, `MMS_CONFIG_MAX_IMAGE_WIDTH`, `MMS_CONFIG_MAX_IMAGE_HEIGHT`
- `MMS_CONFIG_MMS_ENABLED`, `MMS_CONFIG_GROUP_MMS_ENABLED`
- `MMS_CONFIG_RECIPIENT_LIMIT`
- `MMS_CONFIG_SMS_DELIVERY_REPORT_ENABLED`

#### Required Permissions

- `SEND_SMS` -- Required for `sendTextMessage()`, `sendMultipartTextMessage()`, `sendDataMessage()`
- `READ_PHONE_STATE` -- For multi-SIM subscription resolution

#### Hidden/System APIs

- `sendTextMessageWithoutPersisting()` (line 652) -- `@SystemApi`, sends without storing in SMS provider.
- `injectSmsPdu()` (line 796) -- `@SystemApi`, inject a raw SMS PDU.
- `enableCellBroadcastRange()` (line 1869) -- `@SystemApi`
- `disableCellBroadcastRange()` (line 1926) -- `@SystemApi`
- `setSmscAddress()` (line 2921) -- `@SystemApi`
- `getSmsMessagesForFinancialApp()` (line 2684) -- `@SystemApi`

#### Error Handling

The `sendTextMessage()` method defines an extensive set of result codes (lines 400-522):
- `RESULT_ERROR_GENERIC_FAILURE`, `RESULT_ERROR_RADIO_OFF`, `RESULT_ERROR_NULL_PDU`
- `RESULT_ERROR_NO_SERVICE`, `RESULT_ERROR_LIMIT_EXCEEDED`
- `RESULT_ERROR_FDN_CHECK_FAILURE`
- RIL-level errors: `RESULT_RIL_RADIO_NOT_AVAILABLE`, `RESULT_RIL_NETWORK_REJECT`, etc.

---

## 4. Media APIs

### 4.1 MediaPlayer

**File**: `frameworks/base/media/java/android/media/MediaPlayer.java` (5975 lines)

#### Class Purpose

`MediaPlayer` controls playback of audio/video files and streams. It operates as a state machine with clearly defined states and transitions.

#### State Machine (documented at lines 87-250)

States: **Idle** -> **Initialized** -> **Preparing/Prepared** -> **Started** -> **Paused** -> **Stopped** -> **End**

- `new MediaPlayer()` or `reset()` -> **Idle**
- `setDataSource()` -> **Initialized**
- `prepare()` (synchronous) or `prepareAsync()` -> **Prepared**
- `start()` -> **Started**
- `pause()` -> **Paused**
- `stop()` -> **Stopped**
- `release()` -> **End**

**Important**: Objects created via `create()` factory methods are in the **Prepared** state (line 164-167), not Idle.

#### Key Public APIs

**Factory Methods:**
- `MediaPlayer.create(Context, Uri)` (line 867) -- Create and prepare from a URI.
- `MediaPlayer.create(Context, int resid)` (line 950) -- Create and prepare from a raw resource.
- `MediaPlayer.create(Context, Uri, SurfaceHolder, AudioAttributes, int audioSessionId)` (line 903) -- Full-featured factory.

**Data Source:**
- `setDataSource(Context, Uri)` (line 1003) -- Set from content URI.
- `setDataSource(Context, Uri, Map<String,String> headers)` (line 1034) -- With HTTP headers.
- `setDataSource(String path)` (line 1126) -- Set from file path or URL.
- `setDataSource(FileDescriptor fd)` (line 1226) -- Set from file descriptor.
- `setDataSource(MediaDataSource)` (line 1259) -- Custom data source.

**Playback Control:**
- `prepare()` (line 1276) -- Synchronous prepare. Throws `IOException`.
- `prepareAsync()` -- Asynchronous prepare. Fires `OnPreparedListener`.
- `start()` (line 1308) -- Begin playback.
- `pause()` (line 1372) -- Pause playback.
- `stop()` (line 1358) -- Stop playback.
- `seekTo(long msec, @SeekMode mode)` (line 1886) -- Seek with mode: `SEEK_PREVIOUS_SYNC`, `SEEK_NEXT_SYNC`, `SEEK_CLOSEST_SYNC`, `SEEK_CLOSEST`.
- `seekTo(int msec)` (line 1910) -- Legacy seek (equivalent to `SEEK_PREVIOUS_SYNC`).
- `reset()` (line 2125) -- Reset to Idle state.
- `release()` (line 2083) -- Release resources. Call immediately when done.

**Display:**
- `setDisplay(SurfaceHolder)` (line 753) -- Set video output surface.
- `setSurface(Surface)` (line 785) -- Set video output surface (more flexible).
- `setVideoScalingMode(int)` (line 834) -- `VIDEO_SCALING_MODE_SCALE_TO_FIT` or `_SCALE_TO_FIT_WITH_CROPPING`.

**Audio:**
- `setAudioAttributes(AudioAttributes)` (line 2216)
- `setAudioStreamType(int)` (line 2187) -- **Deprecated**, use `setAudioAttributes()`.
- `setVolume(float left, float right)` (line 2259)
- `setAuxEffectSendLevel(float)` (line 2333)

**Track Management:**
- `getTrackInfo()` -- Get info about available tracks (audio, video, subtitle).
- `selectTrack(int index)` (line 3132) -- Select a track.
- `deselectTrack(int index)` (line 3150)
- `addTimedTextSource(String path, String mimeType)` (line 2860)
- `addSubtitleSource(InputStream, MediaFormat)` (line 2739)

**Routing:**
- `setPreferredDevice(AudioDeviceInfo)` (line 1433) -- Preferred output device.
- `addOnRoutingChangedListener()` (line 1508) -- Listen for routing changes.

#### Callbacks

All callbacks use the listener pattern:
- `OnPreparedListener.onPrepared(MediaPlayer)` -- Async prepare complete
- `OnCompletionListener.onCompletion(MediaPlayer)` -- Playback reached end
- `OnErrorListener.onError(MediaPlayer, int what, int extra)` -- Error occurred
- `OnInfoListener.onInfo(MediaPlayer, int what, int extra)` -- Informational events
- `OnBufferingUpdateListener.onBufferingUpdate(MediaPlayer, int percent)` -- Buffering progress
- `OnSeekCompleteListener.onSeekComplete(MediaPlayer)` -- Seek operation complete
- `OnVideoSizeChangedListener.onVideoSizeChanged(MediaPlayer, int width, int height)` -- Video dimensions known

#### Thread Safety Note (line 93)

MediaPlayer is **NOT** thread-safe. All access should be from the same thread. If registering callbacks, the thread must have a `Looper`.

---

### 4.2 MediaRecorder

**File**: `frameworks/base/media/java/android/media/MediaRecorder.java` (1875 lines)

#### Class Purpose

`MediaRecorder` records audio and video. It operates as a state machine similar to `MediaPlayer`. Implements `AudioRouting`, `AudioRecordingMonitor`, `MicrophoneDirection` (lines 95-98).

#### Typical Usage Pattern (lines 62-73)

```java
MediaRecorder recorder = new MediaRecorder();
recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
recorder.setOutputFile(PATH_NAME);
recorder.prepare();
recorder.start();
// ... recording ...
recorder.stop();
recorder.reset();    // Reusable
recorder.release();  // Done
```

#### AudioSource Constants (inner class, line 224)

- `DEFAULT = 0` (line 236)
- `MIC = 1` (line 239) -- Microphone
- `VOICE_UPLINK = 2` (line 249) -- Call uplink, requires `CAPTURE_AUDIO_OUTPUT` (system only)
- `VOICE_DOWNLINK = 3` (line 259) -- Call downlink, requires `CAPTURE_AUDIO_OUTPUT` (system only)
- `VOICE_CALL = 4` (line 269) -- Both uplink+downlink, requires `CAPTURE_AUDIO_OUTPUT` (system only)
- `CAMCORDER = 5` (line 273) -- Tuned for video recording
- `VOICE_RECOGNITION = 6` (line 276) -- Tuned for voice recognition
- `VOICE_COMMUNICATION = 7` (line 282) -- VoIP, with echo cancellation

#### Key Methods

- `setCamera(Camera)` (line 162) -- **Deprecated**, use Camera2 + `getSurface()`.
- `getSurface()` (line 174) -- Get recording surface (for Camera2 integration).
- `setInputSurface(Surface)` (line 191) -- Use a persistent surface from `MediaCodec`.
- `setPreviewDisplay(Surface)` (line 213)
- `setAudioSource(int)`, `setVideoSource(int)` -- Must be called before `setOutputFormat()`.
- `setOutputFormat(int)` -- `THREE_GPP`, `MPEG_4`, `AMR_NB`, `AMR_WB`, `WEBM`, etc.
- `setAudioEncoder(int)`, `setVideoEncoder(int)` -- Set codecs after output format.
- `setOutputFile(String)` / `setOutputFile(FileDescriptor)` / `setOutputFile(File)`
- `prepare()`, `start()`, `stop()`, `reset()`, `release()`

#### Required Permissions

- `RECORD_AUDIO` -- For any audio recording
- `CAMERA` -- When recording video with camera
- `CAPTURE_AUDIO_OUTPUT` -- For `VOICE_UPLINK`, `VOICE_DOWNLINK`, `VOICE_CALL` sources (system only)

#### Callbacks

- `OnErrorListener.onError(MediaRecorder, int what, int extra)` -- Set via `setOnErrorListener()` (lines 79-80)
- `OnInfoListener.onInfo(MediaRecorder, int what, int extra)` -- Set via `setOnInfoListener()`

---

### 4.3 AudioManager

**File**: `frameworks/base/media/java/android/media/AudioManager.java` (6259 lines)

#### Class Purpose

`AudioManager` (`Context.AUDIO_SERVICE`, line 92) controls volume, ringer mode, audio routing, and audio focus.

#### Key Public APIs

**Volume Control:**
- `adjustVolume(int direction, int flags)` (line 883) -- Adjust by one step: `ADJUST_RAISE`, `ADJUST_LOWER`, `ADJUST_SAME`.
- `setStreamVolume(int streamType, int index, int flags)` (line 1173) -- Set absolute volume.
- `getStreamVolume(int streamType)` (line 1023) -- Get current volume.
- `getStreamMaxVolume(int streamType)` -- Get maximum volume for stream.
- `isStreamMute(int streamType)` (line 1373) -- Check if muted.

Stream types: `STREAM_VOICE_CALL`, `STREAM_SYSTEM`, `STREAM_RING`, `STREAM_MUSIC`, `STREAM_ALARM`, `STREAM_NOTIFICATION`, `STREAM_DTMF`

**Ringer Mode:**
- `getRingerMode()` (line 937) -- Returns `RINGER_MODE_NORMAL`, `_VIBRATE`, `_SILENT`.
- `setRingerMode(int)` (line 1144) -- Set ringer mode.

**Audio Focus (critical for media apps):**
- `requestAudioFocus(OnAudioFocusChangeListener, int streamType, int durationHint)` (line 3058) -- **Deprecated**. Request audio focus.
- `requestAudioFocus(AudioFocusRequest)` (line 3131) -- Modern API using `AudioFocusRequest` builder.
- `abandonAudioFocus(OnAudioFocusChangeListener)` (line 3534) -- Release audio focus.

Duration hints: `AUDIOFOCUS_GAIN`, `AUDIOFOCUS_GAIN_TRANSIENT`, `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`, `AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE`

Focus change callbacks: `AUDIOFOCUS_GAIN`, `AUDIOFOCUS_LOSS`, `AUDIOFOCUS_LOSS_TRANSIENT`, `AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK`

**Audio Mode:**
- `setMode(int mode)` (line 2234) -- `MODE_NORMAL`, `MODE_RINGTONE`, `MODE_IN_CALL`, `MODE_IN_COMMUNICATION`
- `getMode()` (line 2249)

**Microphone:**
- `setMicrophoneMute(boolean)` (line 2146)
- `isMicrophoneMute()` (line 2181)

**Bluetooth SCO:**
- `isBluetoothScoAvailableOffCall()` (line 1939)
- `startBluetoothSco()` (line 1989)
- `stopBluetoothSco()` (line 2034)

#### Broadcast Actions

- `ACTION_AUDIO_BECOMING_NOISY` (line 117) -- Audio output about to switch to speaker (e.g., headphones unplugged). Apps should pause playback.
- `RINGER_MODE_CHANGED_ACTION` (line 126) -- Ringer mode changed.
- `VOLUME_CHANGED_ACTION` (line 175) -- `@hide`, volume changed.

#### Hidden/System APIs

- `INTERNAL_RINGER_MODE_CHANGED_ACTION` (line 136) -- `@hide`
- `VOLUME_CHANGED_ACTION` (line 175) -- `@hide @UnsupportedAppUsage`
- `STREAM_DEVICES_CHANGED_ACTION` (line 190) -- `@hide`
- Audio policy APIs via `AudioPolicy` class
- `requestAudioFocus(..., AudioPolicy)` (line 3286) -- System-level focus with audio policy

---

## 5. Camera2 API

### 概述

**Files**:
- `frameworks/base/core/java/android/hardware/camera2/CameraManager.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraDevice.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraCaptureSession.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraCharacteristics.java`
- `frameworks/base/core/java/android/hardware/camera2/CaptureRequest.java`
- `frameworks/base/core/java/android/hardware/camera2/CaptureResult.java`

#### CameraManager (`Context.CAMERA_SERVICE`)

**Key Methods:**
- `getCameraIdList()` (line 114) -- Returns array of available camera IDs.
- `getCameraCharacteristics(String cameraId)` (line 401) -- Get camera capabilities and properties.
- `openCamera(String cameraId, StateCallback callback, Handler handler)` (line 649) -- Open a camera device. **Requires `CAMERA` permission**.
- `openCamera(String cameraId, Executor executor, StateCallback callback)` (line 686) -- Executor-based variant.
- `registerAvailabilityCallback(AvailabilityCallback, Handler)` (line 248) -- Monitor camera availability.

#### CameraDevice

An abstract class (line 67) representing an opened camera. Key elements:

**Request Templates:**
- `TEMPLATE_PREVIEW = 1` (line 78) -- High frame rate preview
- `TEMPLATE_STILL_CAPTURE = 2` (line 90) -- Still image quality priority
- `TEMPLATE_RECORD = 3` (line 97) -- Stable frame rate for recording
- `TEMPLATE_VIDEO_SNAPSHOT` -- Capture still during video
- `TEMPLATE_ZERO_SHUTTER_LAG` -- Minimum shutter lag
- `TEMPLATE_MANUAL` -- Manual control

**Key Methods:**
- `createCaptureSession(List<Surface>, StateCallback, Handler)` (line 243) -- **Deprecated**, use `SessionConfiguration`.
- `createCaptureRequest(int templateType)` (line 983) -- Create a `CaptureRequest.Builder` for the given template.
- `close()` (line 1080) -- Release the camera device.

**Hardware Levels** (from `CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL`):
- `LEGACY` -- Minimum Camera2 support, backward compatibility mode
- `LIMITED` -- Roughly equivalent to old Camera API
- `EXTERNAL` -- Removable cameras (slightly less than LIMITED)
- `FULL` -- Substantially improved over old API
- `LEVEL3` -- Maximum capability

#### Usage Flow

1. `CameraManager.getCameraIdList()` -- Enumerate cameras
2. `CameraManager.getCameraCharacteristics()` -- Query capabilities
3. `CameraManager.openCamera()` -- Open, receive `CameraDevice` in `StateCallback.onOpened()`
4. `CameraDevice.createCaptureSession()` -- Create session with output surfaces
5. `CameraDevice.createCaptureRequest()` -- Build capture request
6. `CameraCaptureSession.setRepeatingRequest()` or `.capture()` -- Execute requests
7. `CameraDevice.close()` -- Release

#### Required Permission

- `android.permission.CAMERA`

---

## 6. Bluetooth APIs

### 6.1 BluetoothAdapter

**File**: `frameworks/base/core/java/android/bluetooth/BluetoothAdapter.java` (3626 lines)

#### Class Purpose

`BluetoothAdapter` represents the local Bluetooth hardware. It is the starting point for all Bluetooth operations: discovery, pairing, connecting. Thread-safe (line 93).

#### How to Obtain

- Modern: `BluetoothManager.getAdapter()` (via `Context.getSystemService(BluetoothManager.class)`)
- Legacy: `BluetoothAdapter.getDefaultAdapter()`

#### Key Public APIs

**State Management:**
- `isEnabled()` (line 866) -- Check if Bluetooth is on.
- `enable()` (line 1118) -- Turn on Bluetooth. Requires `BLUETOOTH_ADMIN`.
- `disable()` (line 1155) -- Turn off. Requires `BLUETOOTH_ADMIN`.
- `getState()` (line 1036) -- Returns `STATE_OFF` (10), `STATE_TURNING_ON` (11), `STATE_ON` (12), `STATE_TURNING_OFF` (13).

**Device Discovery:**
- `startDiscovery()` (line 1701) -- Start scan for nearby devices. Requires `BLUETOOTH_ADMIN` + location permission.
- `cancelDiscovery()` (line 1735) -- Stop discovery.
- `isDiscovering()` (line 1771) -- Check if discovery is active.

**Paired Devices:**
- `getBondedDevices()` -- Returns `Set<BluetoothDevice>` of paired devices.

**Device Creation:**
- `getRemoteDevice(String address)` (line 771) -- Get `BluetoothDevice` by MAC address.
- `getRemoteDevice(byte[] address)` (line 786) -- Get by byte array MAC.

**BLE:**
- `getBluetoothLeScanner()` (line 845) -- Get `BluetoothLeScanner` for BLE scanning.
- `isLeEnabled()` (line 879) -- Check BLE status.
- `isMultipleAdvertisementSupported()` (line 1931) -- BLE advertising support.
- `isLe2MPhySupported()` (line 2039) -- Bluetooth 5 2M PHY.
- `isLeCodedPhySupported()` (line 2061) -- Bluetooth 5 Coded PHY.
- `isLeExtendedAdvertisingSupported()` (line 2083)
- `isLePeriodicAdvertisingSupported()` (line 2105)
- `getLeMaximumAdvertisingDataLength()` (line 2128)

**Server Sockets:**
- `listenUsingRfcommWithServiceRecord(String name, UUID uuid)` -- Create secure RFCOMM server socket.
- `listenUsingInsecureRfcommWithServiceRecord(String name, UUID uuid)` -- Insecure RFCOMM.
- `listenUsingL2capChannel()` -- L2CAP CoC server socket (BLE).

**Adapter Info:**
- `getName()` (line 1207) -- Local Bluetooth name.
- `setName(String)` (line 1281) -- Set Bluetooth name.
- `getAddress()` (line 1191) -- Local MAC address (returns `02:00:00:00:00:00` without `LOCAL_MAC_ADDRESS` permission).
- `getScanMode()` (line 1472) -- `SCAN_MODE_NONE`, `_CONNECTABLE`, `_CONNECTABLE_DISCOVERABLE`.

#### Broadcast Actions

- `ACTION_STATE_CHANGED` (line 142) -- Bluetooth on/off state change, extras: `EXTRA_STATE`, `EXTRA_PREVIOUS_STATE`.
- `ACTION_DISCOVERY_STARTED` -- Discovery scan started.
- `ACTION_DISCOVERY_FINISHED` -- Discovery scan finished.
- `ACTION_SCAN_MODE_CHANGED` -- Scan mode changed.

#### Required Permissions

- `BLUETOOTH` -- Basic Bluetooth operations
- `BLUETOOTH_ADMIN` -- `enable()`, `disable()`, `startDiscovery()`, `setScanMode()`
- `ACCESS_FINE_LOCATION` (or `ACCESS_COARSE_LOCATION`) -- Required for discovery and BLE scanning

#### Hidden/System APIs

- `factoryReset()` (line 1224) -- `@SystemApi`
- `setBluetoothClass()` (line 1336) -- `@SystemApi`
- `setIoCapability()` (line 1391) -- `@SystemApi`
- `removeActiveDevice()` (line 1803) -- `@SystemApi`
- `setActiveDevice()` (line 1842) -- `@SystemApi`
- `connectAllEnabledProfiles()` (line 1883) -- `@SystemApi`
- `disconnectAllEnabledProfiles()` (line 1911) -- `@SystemApi`
- BLE state constants: `STATE_BLE_TURNING_ON`, `STATE_BLE_ON`, `STATE_BLE_TURNING_OFF` (lines 198+) -- `@hide`
- `enableBLE()` (line 961), `disableBLE()` (line 916) -- `@hide`

---

### 6.2 BluetoothDevice

**File**: `frameworks/base/core/java/android/bluetooth/BluetoothDevice.java` (2297 lines)

#### Class Purpose

`BluetoothDevice` represents a remote Bluetooth device. Immutable wrapper around a Bluetooth hardware address. Operations are performed on the remote device using the local `BluetoothAdapter`.

#### How to Obtain

- `BluetoothAdapter.getRemoteDevice(String address)` -- From known MAC.
- `BluetoothAdapter.getBondedDevices()` -- From paired devices set.
- `BluetoothDevice.ACTION_FOUND` broadcast during discovery (with `EXTRA_DEVICE`).

#### Key Public APIs

**Device Info:**
- `getAddress()` (line 1010) -- MAC address string.
- `getName()` (line 1025) -- Friendly name (may be null if not yet retrieved).
- `getAlias()` (line 1073) -- User-assigned alias or remote name.
- `setAlias(String)` (line 1103) -- Set a local alias.
- `getType()` (line 1050) -- `DEVICE_TYPE_CLASSIC`, `_LE`, `_DUAL`, `_UNKNOWN`.
- `getBondState()` (line 1341) -- `BOND_NONE`, `BOND_BONDING`, `BOND_BONDED`.
- `isConnected()` (line 1367) -- `@hide`
- `isEncrypted()` (line 1390) -- `@hide`

**Pairing:**
- `createBond()` (line 1152) -- Initiate pairing.
- `createBond(int transport)` (line 1173) -- Specify transport: `TRANSPORT_AUTO`, `_BREDR`, `_LE`.
- `cancelBondProcess()` (line 1261) -- Cancel pairing. `@SystemApi`.
- `removeBond()` (line 1289) -- Unpair. `@SystemApi`.
- `setPin(byte[])` (line 1520) -- Confirm PIN for pairing.
- `setPairingConfirmation(boolean)` (line 1556) -- Confirm pairing passkey.

**Connection:**
- `createRfcommSocketToServiceRecord(UUID)` (line 1922) -- Create secure RFCOMM `BluetoothSocket`.
- `createInsecureRfcommSocketToServiceRecord(UUID)` -- Insecure RFCOMM.
- `createL2capChannel(int psm)` -- L2CAP CoC connection (BLE).
- `connectGatt(Context, boolean autoConnect, BluetoothGattCallback)` -- Connect GATT for BLE.
- `connectGatt(Context, boolean, BluetoothGattCallback, int transport)` -- With transport.
- `connectGatt(Context, boolean, BluetoothGattCallback, int transport, int phy, Handler)` -- Full GATT connection.

**Service Discovery:**
- `fetchUuidsWithSdp()` (line 1465) -- Initiate SDP to get remote services.

#### Broadcast Actions

- `ACTION_FOUND` (line 114) -- Device discovered during scan. Extras: `EXTRA_DEVICE`, `EXTRA_CLASS`, `EXTRA_NAME`, `EXTRA_RSSI`.
- `ACTION_BOND_STATE_CHANGED` (line 197) -- Pairing state changed. Extras: `EXTRA_BOND_STATE`, `EXTRA_PREVIOUS_BOND_STATE`.
- `ACTION_ACL_CONNECTED` (line 137) -- Low-level connection established.
- `ACTION_ACL_DISCONNECT_REQUESTED` (line 150) -- Disconnection requested.
- `ACTION_ACL_DISCONNECTED` (line 162) -- Disconnected.
- `ACTION_NAME_CHANGED` (line 173) -- Remote name retrieved/changed.
- `ACTION_ALIAS_CHANGED` (line 184) -- Alias changed.

#### Required Permissions

- `BLUETOOTH` -- Most operations
- `BLUETOOTH_ADMIN` -- `createBond()`, `cancelBondProcess()`, `removeBond()`
- `ACCESS_COARSE_LOCATION` -- `ACTION_FOUND` broadcast

#### Hidden/System APIs

- `createBondOutOfBand()` (line 1195) -- `@SystemApi`
- `cancelBondProcess()` (line 1261) -- `@SystemApi`
- `removeBond()` (line 1289) -- `@SystemApi`
- `setSilenceMode()` (line 1651) -- `@SystemApi`
- `setPhonebookAccessPermission()` (line 1698) -- `@SystemApi`
- `setMessageAccessPermission()` (line 1743) -- `@SystemApi`
- `getBatteryLevel()` (line 1127) -- `@hide`
- `createRfcommSocket(int channel)` (line 1831) -- `@hide`, direct channel access
- `isBondingInitiatedLocally()` (line 1218) -- `@hide`

---

## 7. NFC API

### 7.1 NfcAdapter

**File**: `frameworks/base/core/java/android/nfc/NfcAdapter.java` (2113 lines)

#### Class Purpose

`NfcAdapter` represents the local NFC adapter. It manages tag discovery, NDEF message exchange, Android Beam, and foreground dispatch.

#### How to Obtain

- `NfcAdapter.getDefaultAdapter(Context)` (line 671) -- Recommended.
- `NfcAdapter.getDefaultAdapter()` (line 705) -- **Deprecated** static version.

#### Key Public APIs

**State:**
- `isEnabled()` (line 839) -- Check if NFC is on.
- `enable()` (line 889) -- `@SystemApi @RequiresPermission(WRITE_SECURE_SETTINGS)`.
- `disable()` (line 918) -- `@SystemApi`.

**Tag Discovery - Intent Dispatch:**

NFC uses a three-level intent dispatch system for discovered tags:

1. `ACTION_NDEF_DISCOVERED` (line 86) -- Highest priority. Dispatched when a tag has an NDEF payload with a recognized URI, SmartPoster, or MIME type.
2. `ACTION_TECH_DISCOVERED` (line 142) -- Second priority. Dispatched based on tag technologies (NfcA, NfcB, MifareClassic, Ndef, etc.). Requires `<meta-data>` with tech-list XML in manifest.
3. `ACTION_TAG_DISCOVERED` (line 151) -- Lowest priority fallback. Catch-all for unhandled tags.

Extra keys:
- `EXTRA_TAG` (line 189) -- The `Tag` object
- `EXTRA_NDEF_MESSAGES` (line 200) -- Array of `NdefMessage`

**Foreground Dispatch (high-priority tag handling):**
- `enableForegroundDispatch(Activity, PendingIntent, IntentFilter[], String[][])` (line 1485) -- Give foreground activity priority for tag handling.
- `disableForegroundDispatch(Activity)` (line 1527)

**Reader Mode (exclusive NFC access):**
- `enableReaderMode(Activity, ReaderCallback, int flags, Bundle extras)` (line 1579) -- Exclusive tag access in reader mode.
  - Flags: `FLAG_READER_NFC_A`, `FLAG_READER_NFC_B`, `FLAG_READER_NFC_F`, `FLAG_READER_NFC_V`, `FLAG_READER_NFC_BARCODE`, `FLAG_READER_SKIP_NDEF_CHECK`, `FLAG_READER_NO_PLATFORM_SOUNDS`
- `disableReaderMode(Activity)` (line 1597)

**Android Beam (deprecated in Android Q):**
- `setNdefPushMessage(NdefMessage, Activity, Activity...)` (line 1211) -- Set static NDEF message for Beam.
- `setNdefPushMessageCallback(CreateNdefMessageCallback, Activity, Activity...)` (line 1330) -- Dynamic NDEF message creation.
- `setBeamPushUris(Uri[], Activity)` (line 1033) -- Set URIs for Beam file transfer.
- `setBeamPushUrisCallback(CreateBeamUrisCallback, Activity)` (line 1121) -- Dynamic URI creation.
- `setOnNdefPushCompleteCallback(OnNdefPushCompleteCallback, Activity, Activity...)` (line 1417)
- `invokeBeam(Activity)` (line 1634) -- Programmatically trigger Beam.

**Secure NFC (Android 10+):**
- `isSecureNfcSupported()` (line 1781)
- `isSecureNfcEnabled()` (line 1801)
- `enableSecureNfc(boolean)` (line 1763) -- `@SystemApi`

#### Callback Interfaces

- `ReaderCallback.onTagDiscovered(Tag)` (line 404) -- Tag found in reader mode.
- `CreateNdefMessageCallback.createNdefMessage(NfcEvent)` -- Create NDEF for Beam.
- `CreateBeamUrisCallback.createBeamUris(NfcEvent)` -- Create URIs for Beam.
- `OnNdefPushCompleteCallback.onNdefPushComplete(NfcEvent)` (line 424) -- Beam push complete.
- `NfcUnlockHandler.onUnlockAttempted(Tag)` (line 495) -- NFC unlock attempt. `@SystemApi`.

#### Broadcast Actions

- `ACTION_ADAPTER_STATE_CHANGED` -- NFC on/off state changes.
- `ACTION_TRANSACTION_DETECTED` (line 163) -- Secure Element transaction event. Requires `NFC_TRANSACTION_EVENT` permission.
- `ACTION_PREFERRED_PAYMENT_CHANGED` (line 175) -- Preferred payment service changed. Requires `NFC_PREFERRED_PAYMENT_INFO` permission.

#### Required Permissions

- `NFC` -- Basic NFC operations
- `NFC_TRANSACTION_EVENT` -- Receive `ACTION_TRANSACTION_DETECTED`
- `NFC_PREFERRED_PAYMENT_INFO` -- Receive `ACTION_PREFERRED_PAYMENT_CHANGED`
- `WRITE_SECURE_SETTINGS` -- `enable()`, `disable()` (system only)

#### Hidden/System APIs

- `enable()` / `disable()` (lines 889, 918) -- `@SystemApi`
- `enableSecureNfc()` (line 1763) -- `@SystemApi`
- `dispatch(Tag)` (line 1974) -- `@SystemApi`
- `setP2pModes()` (line 1988) -- `@SystemApi`
- `addNfcUnlockHandler()` (line 2013) -- `@SystemApi`
- `ignore(Tag, int debounceMs, OnTagRemovedListener)` (line 1934) -- `@SystemApi`
- `ACTION_TAG_LEFT_FIELD` (line 182) -- `@hide`
- `attemptDeadServiceRecovery()` (line 791) -- `@hide`

---

## 8. HttpURLConnection Patterns

Android 11 AOSP uses `HttpURLConnection` via OkHttp internally. The `Network.openConnection(URL)` method (in `Network.java`, line 339) is the recommended way to make HTTP requests on a specific network:

```java
ConnectivityManager cm = (ConnectivityManager)
    context.getSystemService(Context.CONNECTIVITY_SERVICE);
Network network = cm.getActiveNetwork();
HttpURLConnection conn = (HttpURLConnection) network.openConnection(new URL("https://example.com"));
```

#### Key Implementation Details

1. **Per-Network Connection Pool**: `Network` creates a `HttpURLConnectionFactory` per instance with a private connection pool (lines 313-328). Pool defaults:
   - `httpKeepAlive = true` (line 82)
   - `httpMaxConnections = 5` (line 84)
   - `httpKeepAliveDurationMs = 300000` (5 minutes, line 86)

2. **DNS Resolution**: Each `Network` object has its own DNS lookup that resolves on that network:
   ```java
   Dns dnsLookup = hostname -> Arrays.asList(Network.this.getAllByName(hostname));
   ```
   (line 319)

3. **Proxy Support**: `openConnection()` automatically queries the system proxy for the network via `ConnectivityManager.getProxyForNetwork()` (line 345).

4. **Socket Binding**: The `NetworkBoundSocketFactory` (inner class, line 200) creates sockets that are bound to the specific network via `bindSocket()`. It tries all resolved addresses for a host before giving up (lines 206-222).

5. **Private DNS Bypass**: The `mPrivateDnsBypass` flag (line 107) allows system components to bypass Private DNS for resolution, using a special flag in the netId (`0x80000000L`, line 193).

---

## 9. Cross-Cutting Observations

### Deprecation Patterns

A massive deprecation wave is evident across Android 11's networking APIs:

1. **NetworkInfo** -- The entire class is deprecated. All its methods point to `NetworkCallback`, `NetworkCapabilities`, and `LinkProperties`.
2. **TYPE_* constants** in `ConnectivityManager` (all deprecated since API 21+) -- Replaced by `NetworkCapabilities.hasTransport()` with `TRANSPORT_*` constants.
3. **CONNECTIVITY_ACTION** broadcast -- Cannot be declared in manifest since API 24. Use `NetworkCallback` instead.
4. **Wi-Fi configuration APIs** (`addNetwork()`, `updateNetwork()`, etc.) -- Deprecated in API 29, replaced by `WifiNetworkSuggestion`.
5. **Old Camera API** -- `MediaRecorder.setCamera()` is deprecated in favor of Camera2 + `getSurface()`.
6. **Android Beam** -- Deprecated in Android Q.

### Permission Escalation Trends

- Device identifiers (`getDeviceId()`, `getImei()`, `getMeid()`) now require `READ_PRIVILEGED_PHONE_STATE` -- inaccessible to third-party apps.
- Location permission required for Wi-Fi scanning and Bluetooth discovery.
- `@SystemApi` used extensively for sensitive operations (tethering, NFC control, Bluetooth factory reset).

### Thread Safety Concerns

- `MediaPlayer` is explicitly NOT thread-safe (line 93).
- `MediaRecorder` requires a `Looper` thread for callbacks.
- `ConnectivityManager.NetworkCallback` guarantees ordered callback delivery on a single thread.
- `BluetoothAdapter` is documented as thread-safe (line 93).
- `NetworkInfo` uses `synchronized(this)` on all state-access methods.

### Callback Limit

`ConnectivityManager` imposes a limit of **100 outstanding requests per app UID** (documented at lines 3797-3803, 4031-4038), shared across `requestNetwork()`, `registerNetworkCallback()`, and `ConnectivityDiagnosticsManager`. Exceeding this throws a `RuntimeException`.

### State Machine Patterns

Both `MediaPlayer` and `MediaRecorder` use well-documented state machines. Calling methods in invalid states throws `IllegalStateException`. `MediaPlayer` also has an explicit **Error** state with recovery via `reset()`.

### Hidden API Surface

The hidden/system API surface is enormous:
- `TelephonyManager`: Over 50% of methods are `@hide` or `@SystemApi`.
- `ConnectivityManager`: Tethering, VPN, network agent APIs are `@SystemApi`.
- `BluetoothAdapter`: Device management, factory reset, IO capabilities are `@SystemApi`.
- `NfcAdapter`: Enable/disable, dispatch, Secure NFC control are `@SystemApi`.
- `SmsManager`: SIM operations, cell broadcast, injection are `@SystemApi`.

### File Summary

| File | Lines | Primary APIs |
|------|-------|-------------|
| `ConnectivityManager.java` | 4,770 | Network state, requests, callbacks |
| `NetworkInfo.java` | 599 | Network state (deprecated) |
| `Network.java` | 522 | Network-bound sockets, DNS, HTTP |
| `Uri.java` | 2,431 | URI parsing and building |
| `WifiManager.java` | 6,239 | Wi-Fi control, scanning, suggestions |
| `TelephonyManager.java` | 13,561 | Telephony info, SIM, call state |
| `SmsManager.java` | 3,007 | SMS/MMS sending |
| `MediaPlayer.java` | 5,975 | Audio/video playback |
| `MediaRecorder.java` | 1,875 | Audio/video recording |
| `AudioManager.java` | 6,259 | Volume, audio focus, routing |
| `BluetoothAdapter.java` | 3,626 | Local BT adapter, discovery |
| `BluetoothDevice.java` | 2,297 | Remote BT device, pairing, sockets |
| `NfcAdapter.java` | 2,113 | NFC tags, Beam, reader mode |
| **Total** | **53,274** | |
