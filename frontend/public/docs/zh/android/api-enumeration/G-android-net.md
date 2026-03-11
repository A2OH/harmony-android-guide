# Android 11 (API 30) Public API Enumeration: Android Net

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.net` | 62 | 307 | 140 | 35 |
| `android.net.http` | 5 | 30 | 6 | 5 |
| `android.net.nsd` | 5 | 30 | 8 | 1 |
| `android.net.rtp` | 4 | 23 | 15 | 2 |
| `android.net.sip` | 11 | 105 | 28 | 9 |
| `android.net.ssl` | 2 | 4 | 0 | 0 |
| `android.net.wifi` | 29 | 112 | 112 | 12 |
| `android.net.wifi.aware` | 18 | 61 | 9 | 7 |
| `android.net.wifi.hotspot2` | 2 | 9 | 0 | 2 |
| `android.net.wifi.hotspot2.omadm` | 1 | 1 | 0 | 0 |
| `android.net.wifi.hotspot2.pps` | 5 | 46 | 0 | 10 |
| `android.net.wifi.p2p` | 22 | 73 | 47 | 13 |
| `android.net.wifi.p2p.nsd` | 6 | 13 | 4 | 0 |
| `android.net.wifi.rtt` | 7 | 39 | 50 | 2 |
| **TOTAL** | **179** | **853** | **419** | **98** |

---

## Package: `android.net`

### `class CaptivePortal`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void ignoreNetwork()` |  |
| `void reportCaptivePortalDismissed()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ConnectivityDiagnosticsManager`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void registerConnectivityDiagnosticsCallback(@NonNull android.net.NetworkRequest, @NonNull java.util.concurrent.Executor, @NonNull android.net.ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback)` |  |
| `void unregisterConnectivityDiagnosticsCallback(@NonNull android.net.ConnectivityDiagnosticsManager.ConnectivityDiagnosticsCallback)` |  |
| `void onConnectivityReportAvailable(@NonNull android.net.ConnectivityDiagnosticsManager.ConnectivityReport)` |  |
| `void onDataStallSuspected(@NonNull android.net.ConnectivityDiagnosticsManager.DataStallReport)` |  |
| `void onNetworkConnectivityReported(@NonNull android.net.Network, boolean)` |  |

---

### `class static final ConnectivityDiagnosticsManager.ConnectivityReport`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectivityDiagnosticsManager.ConnectivityReport(@NonNull android.net.Network, long, @NonNull android.net.LinkProperties, @NonNull android.net.NetworkCapabilities, @NonNull android.os.PersistableBundle)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY_NETWORK_PROBES_ATTEMPTED_BITMASK = "networkProbesAttempted"` |  |
| `static final String KEY_NETWORK_PROBES_SUCCEEDED_BITMASK = "networkProbesSucceeded"` |  |
| `static final String KEY_NETWORK_VALIDATION_RESULT = "networkValidationResult"` |  |
| `static final int NETWORK_PROBE_DNS = 4` |  |
| `static final int NETWORK_PROBE_FALLBACK = 32` |  |
| `static final int NETWORK_PROBE_HTTP = 8` |  |
| `static final int NETWORK_PROBE_HTTPS = 16` |  |
| `static final int NETWORK_PROBE_PRIVATE_DNS = 64` |  |
| `static final int NETWORK_VALIDATION_RESULT_INVALID = 0` |  |
| `static final int NETWORK_VALIDATION_RESULT_PARTIALLY_VALID = 2` |  |
| `static final int NETWORK_VALIDATION_RESULT_SKIPPED = 3` |  |
| `static final int NETWORK_VALIDATION_RESULT_VALID = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getReportTimestamp()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final ConnectivityDiagnosticsManager.DataStallReport`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectivityDiagnosticsManager.DataStallReport(@NonNull android.net.Network, long, int, @NonNull android.net.LinkProperties, @NonNull android.net.NetworkCapabilities, @NonNull android.os.PersistableBundle)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DETECTION_METHOD_DNS_EVENTS = 1` |  |
| `static final int DETECTION_METHOD_TCP_METRICS = 2` |  |
| `static final String KEY_DNS_CONSECUTIVE_TIMEOUTS = "dnsConsecutiveTimeouts"` |  |
| `static final String KEY_TCP_METRICS_COLLECTION_PERIOD_MILLIS = "tcpMetricsCollectionPeriodMillis"` |  |
| `static final String KEY_TCP_PACKET_FAIL_RATE = "tcpPacketFailRate"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDetectionMethod()` |  |
| `long getReportTimestamp()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class ConnectivityManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CAPTIVE_PORTAL_SIGN_IN = "android.net.conn.CAPTIVE_PORTAL"` |  |
| `static final String ACTION_RESTRICT_BACKGROUND_CHANGED = "android.net.conn.RESTRICT_BACKGROUND_CHANGED"` |  |
| `static final String EXTRA_CAPTIVE_PORTAL = "android.net.extra.CAPTIVE_PORTAL"` |  |
| `static final String EXTRA_CAPTIVE_PORTAL_URL = "android.net.extra.CAPTIVE_PORTAL_URL"` |  |
| `static final String EXTRA_NETWORK = "android.net.extra.NETWORK"` |  |
| `static final String EXTRA_NETWORK_REQUEST = "android.net.extra.NETWORK_REQUEST"` |  |
| `static final String EXTRA_NO_CONNECTIVITY = "noConnectivity"` |  |
| `static final String EXTRA_REASON = "reason"` |  |
| `static final int MULTIPATH_PREFERENCE_HANDOVER = 1` |  |
| `static final int MULTIPATH_PREFERENCE_PERFORMANCE = 4` |  |
| `static final int MULTIPATH_PREFERENCE_RELIABILITY = 2` |  |
| `static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1` |  |
| `static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3` |  |
| `static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addDefaultNetworkActiveListener(android.net.ConnectivityManager.OnNetworkActiveListener)` |  |
| `boolean bindProcessToNetwork(@Nullable android.net.Network)` |  |
| `int getConnectionOwnerUid(int, @NonNull java.net.InetSocketAddress, @NonNull java.net.InetSocketAddress)` |  |
| `int getRestrictBackgroundStatus()` |  |
| `boolean isDefaultNetworkActive()` |  |
| `void releaseNetworkRequest(@NonNull android.app.PendingIntent)` |  |
| `void removeDefaultNetworkActiveListener(@NonNull android.net.ConnectivityManager.OnNetworkActiveListener)` |  |
| `void reportNetworkConnectivity(@Nullable android.net.Network, boolean)` |  |
| `boolean requestBandwidthUpdate(@NonNull android.net.Network)` |  |
| `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback)` |  |
| `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler)` |  |
| `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, int)` |  |
| `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler, int)` |  |
| `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.app.PendingIntent)` |  |
| `void unregisterNetworkCallback(@NonNull android.net.ConnectivityManager.NetworkCallback)` |  |
| `void unregisterNetworkCallback(@NonNull android.app.PendingIntent)` |  |

---

### `class static ConnectivityManager.NetworkCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ConnectivityManager.NetworkCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onAvailable(@NonNull android.net.Network)` |  |
| `void onBlockedStatusChanged(@NonNull android.net.Network, boolean)` |  |
| `void onCapabilitiesChanged(@NonNull android.net.Network, @NonNull android.net.NetworkCapabilities)` |  |
| `void onLinkPropertiesChanged(@NonNull android.net.Network, @NonNull android.net.LinkProperties)` |  |
| `void onLosing(@NonNull android.net.Network, int)` |  |
| `void onLost(@NonNull android.net.Network)` |  |
| `void onUnavailable()` |  |

---

### `interface static ConnectivityManager.OnNetworkActiveListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onNetworkActive()` |  |

---

### `class Credentials`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Credentials(int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getGid()` |  |
| `int getPid()` |  |
| `int getUid()` |  |

---

### `class DhcpInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DhcpInfo()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int dns1` |  |
| `int dns2` |  |
| `int gateway` |  |
| `int ipAddress` |  |
| `int leaseDuration` |  |
| `int netmask` |  |
| `int serverAddress` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final DnsResolver`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CLASS_IN = 1` |  |
| `static final int ERROR_PARSE = 0` |  |
| `static final int ERROR_SYSTEM = 1` |  |
| `static final int FLAG_EMPTY = 0` |  |
| `static final int FLAG_NO_CACHE_LOOKUP = 4` |  |
| `static final int FLAG_NO_CACHE_STORE = 2` |  |
| `static final int FLAG_NO_RETRY = 1` |  |
| `static final int TYPE_A = 1` |  |
| `static final int TYPE_AAAA = 28` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void query(@Nullable android.net.Network, @NonNull String, int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super java.util.List<java.net.InetAddress>>)` |  |
| `void query(@Nullable android.net.Network, @NonNull String, int, int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super java.util.List<java.net.InetAddress>>)` |  |
| `void rawQuery(@Nullable android.net.Network, @NonNull byte[], int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super byte[]>)` |  |
| `void rawQuery(@Nullable android.net.Network, @NonNull String, int, int, int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super byte[]>)` |  |

---

### `interface static DnsResolver.Callback<T>`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onAnswer(@NonNull T, int)` |  |
| `void onError(@NonNull android.net.DnsResolver.DnsException)` |  |

---

### `class static DnsResolver.DnsException`

- **继承：** `java.lang.Exception`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `final int code` |  |

---

### `class final Ikev2VpnProfile`

- **继承：** `android.net.PlatformVpnProfile`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getMaxMtu()` |  |
| `boolean isBypassable()` |  |
| `boolean isMetered()` |  |

---

### `class static final Ikev2VpnProfile.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Ikev2VpnProfile.Builder(@NonNull String, @NonNull String)` |  |

---

### `class InetAddresses`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isNumericAddress(@NonNull String)` |  |

---

### `class final IpPrefix`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean contains(@NonNull java.net.InetAddress)` |  |
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final IpSecAlgorithm`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IpSecAlgorithm(@NonNull String, @NonNull byte[])` |  |
| `IpSecAlgorithm(@NonNull String, @NonNull byte[], int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTH_CRYPT_AES_GCM = "rfc4106(gcm(aes))"` |  |
| `static final String AUTH_HMAC_MD5 = "hmac(md5)"` |  |
| `static final String AUTH_HMAC_SHA1 = "hmac(sha1)"` |  |
| `static final String AUTH_HMAC_SHA256 = "hmac(sha256)"` |  |
| `static final String AUTH_HMAC_SHA384 = "hmac(sha384)"` |  |
| `static final String AUTH_HMAC_SHA512 = "hmac(sha512)"` |  |
| `static final String CRYPT_AES_CBC = "cbc(aes)"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getTruncationLengthBits()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final IpSecManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DIRECTION_IN = 0` |  |
| `static final int DIRECTION_OUT = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void applyTransportModeTransform(@NonNull java.net.Socket, int, @NonNull android.net.IpSecTransform) throws java.io.IOException` |  |
| `void applyTransportModeTransform(@NonNull java.net.DatagramSocket, int, @NonNull android.net.IpSecTransform) throws java.io.IOException` |  |
| `void applyTransportModeTransform(@NonNull java.io.FileDescriptor, int, @NonNull android.net.IpSecTransform) throws java.io.IOException` |  |
| `void removeTransportModeTransforms(@NonNull java.net.Socket) throws java.io.IOException` |  |
| `void removeTransportModeTransforms(@NonNull java.net.DatagramSocket) throws java.io.IOException` |  |
| `void removeTransportModeTransforms(@NonNull java.io.FileDescriptor) throws java.io.IOException` |  |

---

### `class static final IpSecManager.ResourceUnavailableException`

- **继承：** `android.util.AndroidException`

---

### `class static final IpSecManager.SecurityParameterIndex`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int getSpi()` |  |

---

### `class static final IpSecManager.SpiUnavailableException`

- **继承：** `android.util.AndroidException`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSpi()` |  |

---

### `class static final IpSecManager.UdpEncapsulationSocket`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `int getPort()` |  |

---

### `class final IpSecTransform`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |

---

### `class static IpSecTransform.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IpSecTransform.Builder(@NonNull android.content.Context)` |  |

---

### `class LinkAddress`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.net.InetAddress getAddress()` |  |
| `int getFlags()` |  |
| `int getScope()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final LinkProperties`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LinkProperties()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean addRoute(@NonNull android.net.RouteInfo)` |  |
| `void clear()` |  |
| `int describeContents()` |  |
| `int getMtu()` |  |
| `boolean isPrivateDnsActive()` |  |
| `boolean isWakeOnLanSupported()` |  |
| `void setDhcpServerAddress(@Nullable java.net.Inet4Address)` |  |
| `void setDnsServers(@NonNull java.util.Collection<java.net.InetAddress>)` |  |
| `void setDomains(@Nullable String)` |  |
| `void setHttpProxy(@Nullable android.net.ProxyInfo)` |  |
| `void setInterfaceName(@Nullable String)` |  |
| `void setLinkAddresses(@NonNull java.util.Collection<android.net.LinkAddress>)` |  |
| `void setMtu(int)` |  |
| `void setNat64Prefix(@Nullable android.net.IpPrefix)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class LocalServerSocket`

- **实现：** `java.io.Closeable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LocalServerSocket(String) throws java.io.IOException` |  |
| `LocalServerSocket(java.io.FileDescriptor) throws java.io.IOException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.LocalSocket accept() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `android.net.LocalSocketAddress getLocalSocketAddress()` |  |

---

### `class LocalSocket`

- **实现：** `java.io.Closeable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LocalSocket()` |  |
| `LocalSocket(int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SOCKET_DGRAM = 1` |  |
| `static final int SOCKET_SEQPACKET = 3` |  |
| `static final int SOCKET_STREAM = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void bind(android.net.LocalSocketAddress) throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void connect(android.net.LocalSocketAddress) throws java.io.IOException` |  |
| `void connect(android.net.LocalSocketAddress, int) throws java.io.IOException` |  |
| `java.io.FileDescriptor[] getAncillaryFileDescriptors() throws java.io.IOException` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `android.net.LocalSocketAddress getLocalSocketAddress()` |  |
| `java.io.OutputStream getOutputStream() throws java.io.IOException` |  |
| `android.net.Credentials getPeerCredentials() throws java.io.IOException` |  |
| `int getReceiveBufferSize() throws java.io.IOException` |  |
| `android.net.LocalSocketAddress getRemoteSocketAddress()` |  |
| `int getSendBufferSize() throws java.io.IOException` |  |
| `int getSoTimeout() throws java.io.IOException` |  |
| `boolean isBound()` |  |
| `boolean isClosed()` |  |
| `boolean isConnected()` |  |
| `boolean isInputShutdown()` |  |
| `boolean isOutputShutdown()` |  |
| `void setFileDescriptorsForSend(java.io.FileDescriptor[])` |  |
| `void setReceiveBufferSize(int) throws java.io.IOException` |  |
| `void setSendBufferSize(int) throws java.io.IOException` |  |
| `void setSoTimeout(int) throws java.io.IOException` |  |
| `void shutdownInput() throws java.io.IOException` |  |
| `void shutdownOutput() throws java.io.IOException` |  |

---

### `class LocalSocketAddress`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LocalSocketAddress(String, android.net.LocalSocketAddress.Namespace)` |  |
| `LocalSocketAddress(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getName()` |  |
| `android.net.LocalSocketAddress.Namespace getNamespace()` |  |

---

### `enum LocalSocketAddress.Namespace`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.net.LocalSocketAddress.Namespace ABSTRACT` |  |
| `static final android.net.LocalSocketAddress.Namespace FILESYSTEM` |  |
| `static final android.net.LocalSocketAddress.Namespace RESERVED` |  |

---

### `class final MacAddress`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.MacAddress BROADCAST_ADDRESS` |  |
| `static final int TYPE_BROADCAST = 3` |  |
| `static final int TYPE_MULTICAST = 2` |  |
| `static final int TYPE_UNICAST = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAddressType()` |  |
| `boolean isLocallyAssigned()` |  |
| `boolean matches(@NonNull android.net.MacAddress, @NonNull android.net.MacAddress)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class MailTo`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String MAILTO_SCHEME = "mailto:"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getBody()` |  |
| `String getCc()` |  |
| `java.util.Map<java.lang.String,java.lang.String> getHeaders()` |  |
| `String getSubject()` |  |
| `String getTo()` |  |
| `static boolean isMailTo(String)` |  |
| `static android.net.MailTo parse(String) throws android.net.ParseException` |  |

---

### `class Network`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void bindSocket(java.net.DatagramSocket) throws java.io.IOException` |  |
| `void bindSocket(java.net.Socket) throws java.io.IOException` |  |
| `void bindSocket(java.io.FileDescriptor) throws java.io.IOException` |  |
| `int describeContents()` |  |
| `static android.net.Network fromNetworkHandle(long)` |  |
| `java.net.InetAddress[] getAllByName(String) throws java.net.UnknownHostException` |  |
| `java.net.InetAddress getByName(String) throws java.net.UnknownHostException` |  |
| `long getNetworkHandle()` |  |
| `javax.net.SocketFactory getSocketFactory()` |  |
| `java.net.URLConnection openConnection(java.net.URL) throws java.io.IOException` |  |
| `java.net.URLConnection openConnection(java.net.URL, java.net.Proxy) throws java.io.IOException` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final NetworkCapabilities`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NetworkCapabilities()` |  |
| `NetworkCapabilities(android.net.NetworkCapabilities)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int NET_CAPABILITY_CAPTIVE_PORTAL = 17` |  |
| `static final int NET_CAPABILITY_CBS = 5` |  |
| `static final int NET_CAPABILITY_DUN = 2` |  |
| `static final int NET_CAPABILITY_EIMS = 10` |  |
| `static final int NET_CAPABILITY_FOREGROUND = 19` |  |
| `static final int NET_CAPABILITY_FOTA = 3` |  |
| `static final int NET_CAPABILITY_IA = 7` |  |
| `static final int NET_CAPABILITY_IMS = 4` |  |
| `static final int NET_CAPABILITY_INTERNET = 12` |  |
| `static final int NET_CAPABILITY_MCX = 23` |  |
| `static final int NET_CAPABILITY_MMS = 0` |  |
| `static final int NET_CAPABILITY_NOT_CONGESTED = 20` |  |
| `static final int NET_CAPABILITY_NOT_METERED = 11` |  |
| `static final int NET_CAPABILITY_NOT_RESTRICTED = 13` |  |
| `static final int NET_CAPABILITY_NOT_ROAMING = 18` |  |
| `static final int NET_CAPABILITY_NOT_SUSPENDED = 21` |  |
| `static final int NET_CAPABILITY_NOT_VPN = 15` |  |
| `static final int NET_CAPABILITY_RCS = 8` |  |
| `static final int NET_CAPABILITY_SUPL = 1` |  |
| `static final int NET_CAPABILITY_TEMPORARILY_NOT_METERED = 25` |  |
| `static final int NET_CAPABILITY_TRUSTED = 14` |  |
| `static final int NET_CAPABILITY_VALIDATED = 16` |  |
| `static final int NET_CAPABILITY_WIFI_P2P = 6` |  |
| `static final int NET_CAPABILITY_XCAP = 9` |  |
| `static final int SIGNAL_STRENGTH_UNSPECIFIED = -2147483648` |  |
| `static final int TRANSPORT_BLUETOOTH = 2` |  |
| `static final int TRANSPORT_CELLULAR = 0` |  |
| `static final int TRANSPORT_ETHERNET = 3` |  |
| `static final int TRANSPORT_LOWPAN = 6` |  |
| `static final int TRANSPORT_VPN = 4` |  |
| `static final int TRANSPORT_WIFI = 1` |  |
| `static final int TRANSPORT_WIFI_AWARE = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getLinkDownstreamBandwidthKbps()` |  |
| `int getLinkUpstreamBandwidthKbps()` |  |
| `int getOwnerUid()` |  |
| `int getSignalStrength()` |  |
| `boolean hasCapability(int)` |  |
| `boolean hasTransport(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class NetworkInfo` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **注解：** `@Deprecated`

---

### `enum NetworkInfo.DetailedState` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `enum NetworkInfo.State` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class NetworkRequest`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canBeSatisfiedBy(@Nullable android.net.NetworkCapabilities)` |  |
| `int describeContents()` |  |
| `boolean hasCapability(int)` |  |
| `boolean hasTransport(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static NetworkRequest.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NetworkRequest.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.NetworkRequest.Builder addCapability(int)` |  |
| `android.net.NetworkRequest.Builder addTransportType(int)` |  |
| `android.net.NetworkRequest build()` |  |
| `android.net.NetworkRequest.Builder removeCapability(int)` |  |
| `android.net.NetworkRequest.Builder removeTransportType(int)` |  |
| `android.net.NetworkRequest.Builder setNetworkSpecifier(android.net.NetworkSpecifier)` |  |

---

### `class abstract NetworkSpecifier`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NetworkSpecifier()` |  |

---

### `class ParseException`

- **继承：** `java.lang.RuntimeException`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String response` |  |

---

### `class abstract PlatformVpnProfile`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_IKEV2_IPSEC_PSK = 7` |  |
| `static final int TYPE_IKEV2_IPSEC_RSA = 8` |  |
| `static final int TYPE_IKEV2_IPSEC_USER_PASS = 6` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final int getType()` |  |

---

### `class final Proxy`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Proxy()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE"` |  |

---

### `class ProxyInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ProxyInfo(@Nullable android.net.ProxyInfo)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.ProxyInfo buildDirectProxy(String, int)` |  |
| `static android.net.ProxyInfo buildDirectProxy(String, int, java.util.List<java.lang.String>)` |  |
| `static android.net.ProxyInfo buildPacProxy(android.net.Uri)` |  |
| `int describeContents()` |  |
| `String[] getExclusionList()` |  |
| `String getHost()` |  |
| `android.net.Uri getPacFileUrl()` |  |
| `int getPort()` |  |
| `boolean isValid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final RouteInfo`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean hasGateway()` |  |
| `boolean isDefaultRoute()` |  |
| `boolean matches(java.net.InetAddress)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SSLCertificateSocketFactory` ~~DEPRECATED~~

- **继承：** `javax.net.ssl.SSLSocketFactory`
- **注解：** `@Deprecated`

---

### `class final SSLSessionCache`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SSLSessionCache(java.io.File) throws java.io.IOException` |  |
| `SSLSessionCache(android.content.Context)` |  |

---

### `class abstract SocketKeepalive`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_HARDWARE_ERROR = -31` |  |
| `static final int ERROR_INSUFFICIENT_RESOURCES = -32` |  |
| `static final int ERROR_INVALID_INTERVAL = -24` |  |
| `static final int ERROR_INVALID_IP_ADDRESS = -21` |  |
| `static final int ERROR_INVALID_LENGTH = -23` |  |
| `static final int ERROR_INVALID_NETWORK = -20` |  |
| `static final int ERROR_INVALID_PORT = -22` |  |
| `static final int ERROR_INVALID_SOCKET = -25` |  |
| `static final int ERROR_SOCKET_NOT_IDLE = -26` |  |
| `static final int ERROR_UNSUPPORTED = -30` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void close()` |  |
| `final void start(@IntRange(from=0xa, to=0xe10) int)` |  |
| `final void stop()` |  |

---

### `class static SocketKeepalive.Callback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SocketKeepalive.Callback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDataReceived()` |  |
| `void onError(int)` |  |
| `void onStarted()` |  |
| `void onStopped()` |  |

---

### `class final TelephonyNetworkSpecifier`

- **继承：** `android.net.NetworkSpecifier`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSubscriptionId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final TelephonyNetworkSpecifier.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TelephonyNetworkSpecifier.Builder()` |  |

---

### `class TrafficStats`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TrafficStats()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int UNSUPPORTED = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void clearThreadStatsTag()` |  |
| `static void clearThreadStatsUid()` |  |
| `static int getAndSetThreadStatsTag(int)` |  |
| `static long getMobileRxBytes()` |  |
| `static long getMobileRxPackets()` |  |
| `static long getMobileTxBytes()` |  |
| `static long getMobileTxPackets()` |  |
| `static long getRxPackets(@NonNull String)` |  |
| `static int getThreadStatsTag()` |  |
| `static int getThreadStatsUid()` |  |
| `static long getTotalRxBytes()` |  |
| `static long getTotalRxPackets()` |  |
| `static long getTotalTxBytes()` |  |
| `static long getTotalTxPackets()` |  |
| `static long getTxPackets(@NonNull String)` |  |
| `static long getUidRxBytes(int)` |  |
| `static long getUidRxPackets(int)` |  |
| `static long getUidTxBytes(int)` |  |
| `static long getUidTxPackets(int)` |  |
| `static void incrementOperationCount(int)` |  |
| `static void incrementOperationCount(int, int)` |  |
| `static void setThreadStatsTag(int)` |  |
| `static void setThreadStatsUid(int)` |  |
| `static void tagDatagramSocket(java.net.DatagramSocket) throws java.net.SocketException` |  |
| `static void tagFileDescriptor(java.io.FileDescriptor) throws java.io.IOException` |  |
| `static void tagSocket(java.net.Socket) throws java.net.SocketException` |  |
| `static void untagDatagramSocket(java.net.DatagramSocket) throws java.net.SocketException` |  |
| `static void untagFileDescriptor(java.io.FileDescriptor) throws java.io.IOException` |  |
| `static void untagSocket(java.net.Socket) throws java.net.SocketException` |  |

---

### `interface TransportInfo`


---

### `class abstract Uri`

- **实现：** `java.lang.Comparable<android.net.Uri> android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri EMPTY` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract android.net.Uri.Builder buildUpon()` |  |
| `int compareTo(android.net.Uri)` |  |
| `static String decode(String)` |  |
| `static String encode(String)` |  |
| `static String encode(String, String)` |  |
| `static android.net.Uri fromFile(java.io.File)` |  |
| `static android.net.Uri fromParts(String, String, String)` |  |
| `boolean getBooleanQueryParameter(String, boolean)` |  |
| `abstract String getEncodedSchemeSpecificPart()` |  |
| `abstract java.util.List<java.lang.String> getPathSegments()` |  |
| `abstract int getPort()` |  |
| `java.util.Set<java.lang.String> getQueryParameterNames()` |  |
| `java.util.List<java.lang.String> getQueryParameters(String)` |  |
| `abstract String getSchemeSpecificPart()` |  |
| `boolean isAbsolute()` |  |
| `abstract boolean isHierarchical()` |  |
| `boolean isOpaque()` |  |
| `abstract boolean isRelative()` |  |
| `android.net.Uri normalizeScheme()` |  |
| `static android.net.Uri parse(String)` |  |
| `abstract String toString()` |  |
| `static android.net.Uri withAppendedPath(android.net.Uri, String)` |  |
| `static void writeToParcel(android.os.Parcel, android.net.Uri)` |  |

---

### `class static final Uri.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Uri.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.Uri.Builder appendEncodedPath(String)` |  |
| `android.net.Uri.Builder appendPath(String)` |  |
| `android.net.Uri.Builder appendQueryParameter(String, String)` |  |
| `android.net.Uri.Builder authority(String)` |  |
| `android.net.Uri build()` |  |
| `android.net.Uri.Builder clearQuery()` |  |
| `android.net.Uri.Builder encodedAuthority(String)` |  |
| `android.net.Uri.Builder encodedFragment(String)` |  |
| `android.net.Uri.Builder encodedOpaquePart(String)` |  |
| `android.net.Uri.Builder encodedPath(String)` |  |
| `android.net.Uri.Builder encodedQuery(String)` |  |
| `android.net.Uri.Builder fragment(String)` |  |
| `android.net.Uri.Builder opaquePart(String)` |  |
| `android.net.Uri.Builder path(String)` |  |
| `android.net.Uri.Builder query(String)` |  |
| `android.net.Uri.Builder scheme(String)` |  |

---

### `class UrlQuerySanitizer`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UrlQuerySanitizer()` |  |
| `UrlQuerySanitizer(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addSanitizedEntry(String, String)` |  |
| `void clear()` |  |
| `int decodeHexDigit(char)` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllButNulAndAngleBracketsLegal()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllButNulLegal()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllButWhitespaceLegal()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllIllegal()` |  |
| `boolean getAllowUnregisteredParamaters()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAmpAndSpaceLegal()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getAmpLegal()` |  |
| `android.net.UrlQuerySanitizer.ValueSanitizer getEffectiveValueSanitizer(String)` |  |
| `java.util.List<android.net.UrlQuerySanitizer.ParameterValuePair> getParameterList()` |  |
| `java.util.Set<java.lang.String> getParameterSet()` |  |
| `boolean getPreferFirstRepeatedParameter()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getSpaceLegal()` |  |
| `android.net.UrlQuerySanitizer.ValueSanitizer getUnregisteredParameterValueSanitizer()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getUrlAndSpaceLegal()` |  |
| `static final android.net.UrlQuerySanitizer.ValueSanitizer getUrlLegal()` |  |
| `String getValue(String)` |  |
| `android.net.UrlQuerySanitizer.ValueSanitizer getValueSanitizer(String)` |  |
| `boolean hasParameter(String)` |  |
| `boolean isHexDigit(char)` |  |
| `void parseEntry(String, String)` |  |
| `void parseQuery(String)` |  |
| `void parseUrl(String)` |  |
| `void registerParameter(String, android.net.UrlQuerySanitizer.ValueSanitizer)` |  |
| `void registerParameters(String[], android.net.UrlQuerySanitizer.ValueSanitizer)` |  |
| `void setAllowUnregisteredParamaters(boolean)` |  |
| `void setPreferFirstRepeatedParameter(boolean)` |  |
| `void setUnregisteredParameterValueSanitizer(android.net.UrlQuerySanitizer.ValueSanitizer)` |  |
| `String unescape(String)` |  |

---

### `class static UrlQuerySanitizer.IllegalCharacterValueSanitizer`

- **实现：** `android.net.UrlQuerySanitizer.ValueSanitizer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UrlQuerySanitizer.IllegalCharacterValueSanitizer(int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALL_BUT_NUL_AND_ANGLE_BRACKETS_LEGAL = 1439` |  |
| `static final int ALL_BUT_NUL_LEGAL = 1535` |  |
| `static final int ALL_BUT_WHITESPACE_LEGAL = 1532` |  |
| `static final int ALL_ILLEGAL = 0` |  |
| `static final int ALL_OK = 2047` |  |
| `static final int ALL_WHITESPACE_OK = 3` |  |
| `static final int AMP_AND_SPACE_LEGAL = 129` |  |
| `static final int AMP_LEGAL = 128` |  |
| `static final int AMP_OK = 128` |  |
| `static final int DQUOTE_OK = 8` |  |
| `static final int GT_OK = 64` |  |
| `static final int LT_OK = 32` |  |
| `static final int NON_7_BIT_ASCII_OK = 4` |  |
| `static final int NUL_OK = 512` |  |
| `static final int OTHER_WHITESPACE_OK = 2` |  |
| `static final int PCT_OK = 256` |  |
| `static final int SCRIPT_URL_OK = 1024` |  |
| `static final int SPACE_LEGAL = 1` |  |
| `static final int SPACE_OK = 1` |  |
| `static final int SQUOTE_OK = 16` |  |
| `static final int URL_AND_SPACE_LEGAL = 405` |  |
| `static final int URL_LEGAL = 404` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String sanitize(String)` |  |

---

### `class UrlQuerySanitizer.ParameterValuePair`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UrlQuerySanitizer.ParameterValuePair(String, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String mParameter` |  |
| `String mValue` |  |

---

### `interface static UrlQuerySanitizer.ValueSanitizer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String sanitize(String)` |  |

---

### `class VpnManager`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void deleteProvisionedVpnProfile()` |  |
| `void startProvisionedVpnProfile()` |  |
| `void stopProvisionedVpnProfile()` |  |

---

### `class VpnService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VpnService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.net.VpnService"` |  |
| `static final String SERVICE_META_DATA_SUPPORTS_ALWAYS_ON = "android.net.VpnService.SUPPORTS_ALWAYS_ON"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean isAlwaysOn()` |  |
| `final boolean isLockdownEnabled()` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onRevoke()` |  |
| `static android.content.Intent prepare(android.content.Context)` |  |
| `boolean protect(int)` |  |
| `boolean protect(java.net.Socket)` |  |
| `boolean protect(java.net.DatagramSocket)` |  |
| `boolean setUnderlyingNetworks(android.net.Network[])` |  |

---

### `class VpnService.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VpnService.Builder()` |  |

---

## Package: `android.net.http`

### `class final HttpResponseCache`

- **继承：** `java.net.ResponseCache`
- **实现：** `java.io.Closeable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void delete() throws java.io.IOException` |  |
| `void flush()` |  |
| `java.net.CacheResponse get(java.net.URI, String, java.util.Map<java.lang.String,java.util.List<java.lang.String>>) throws java.io.IOException` |  |
| `int getHitCount()` |  |
| `static android.net.http.HttpResponseCache getInstalled()` |  |
| `int getNetworkCount()` |  |
| `int getRequestCount()` |  |
| `static android.net.http.HttpResponseCache install(java.io.File, long) throws java.io.IOException` |  |
| `long maxSize()` |  |
| `java.net.CacheRequest put(java.net.URI, java.net.URLConnection) throws java.io.IOException` |  |
| `long size()` |  |

---

### `class SslCertificate`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SslCertificate(java.security.cert.X509Certificate)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.http.SslCertificate.DName getIssuedBy()` |  |
| `android.net.http.SslCertificate.DName getIssuedTo()` |  |
| `java.util.Date getValidNotAfterDate()` |  |
| `java.util.Date getValidNotBeforeDate()` |  |
| `static android.net.http.SslCertificate restoreState(android.os.Bundle)` |  |
| `static android.os.Bundle saveState(android.net.http.SslCertificate)` |  |

---

### `class SslCertificate.DName`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SslCertificate.DName(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getCName()` |  |
| `String getDName()` |  |
| `String getOName()` |  |
| `String getUName()` |  |

---

### `class SslError`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SslError(int, android.net.http.SslCertificate, String)` |  |
| `SslError(int, java.security.cert.X509Certificate, String)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SSL_DATE_INVALID = 4` |  |
| `static final int SSL_EXPIRED = 1` |  |
| `static final int SSL_IDMISMATCH = 2` |  |
| `static final int SSL_INVALID = 5` |  |
| `static final int SSL_NOTYETVALID = 0` |  |
| `static final int SSL_UNTRUSTED = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean addError(int)` |  |
| `android.net.http.SslCertificate getCertificate()` |  |
| `int getPrimaryError()` |  |
| `String getUrl()` |  |
| `boolean hasError(int)` |  |

---

### `class X509TrustManagerExtensions`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `X509TrustManagerExtensions(javax.net.ssl.X509TrustManager) throws java.lang.IllegalArgumentException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.List<java.security.cert.X509Certificate> checkServerTrusted(java.security.cert.X509Certificate[], String, String) throws java.security.cert.CertificateException` |  |
| `boolean isSameTrustConfiguration(String, String)` |  |
| `boolean isUserAddedCertificate(java.security.cert.X509Certificate)` |  |

---

## Package: `android.net.nsd`

### `class final NsdManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_NSD_STATE_CHANGED = "android.net.nsd.STATE_CHANGED"` |  |
| `static final String EXTRA_NSD_STATE = "nsd_state"` |  |
| `static final int FAILURE_ALREADY_ACTIVE = 3` |  |
| `static final int FAILURE_INTERNAL_ERROR = 0` |  |
| `static final int FAILURE_MAX_LIMIT = 4` |  |
| `static final int NSD_STATE_DISABLED = 1` |  |
| `static final int NSD_STATE_ENABLED = 2` |  |
| `static final int PROTOCOL_DNS_SD = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void discoverServices(String, int, android.net.nsd.NsdManager.DiscoveryListener)` |  |
| `void registerService(android.net.nsd.NsdServiceInfo, int, android.net.nsd.NsdManager.RegistrationListener)` |  |
| `void resolveService(android.net.nsd.NsdServiceInfo, android.net.nsd.NsdManager.ResolveListener)` |  |
| `void stopServiceDiscovery(android.net.nsd.NsdManager.DiscoveryListener)` |  |
| `void unregisterService(android.net.nsd.NsdManager.RegistrationListener)` |  |

---

### `interface static NsdManager.DiscoveryListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDiscoveryStarted(String)` |  |
| `void onDiscoveryStopped(String)` |  |
| `void onServiceFound(android.net.nsd.NsdServiceInfo)` |  |
| `void onServiceLost(android.net.nsd.NsdServiceInfo)` |  |
| `void onStartDiscoveryFailed(String, int)` |  |
| `void onStopDiscoveryFailed(String, int)` |  |

---

### `interface static NsdManager.RegistrationListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onRegistrationFailed(android.net.nsd.NsdServiceInfo, int)` |  |
| `void onServiceRegistered(android.net.nsd.NsdServiceInfo)` |  |
| `void onServiceUnregistered(android.net.nsd.NsdServiceInfo)` |  |
| `void onUnregistrationFailed(android.net.nsd.NsdServiceInfo, int)` |  |

---

### `interface static NsdManager.ResolveListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onResolveFailed(android.net.nsd.NsdServiceInfo, int)` |  |
| `void onServiceResolved(android.net.nsd.NsdServiceInfo)` |  |

---

### `class final NsdServiceInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NsdServiceInfo()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.Map<java.lang.String,byte[]> getAttributes()` |  |
| `java.net.InetAddress getHost()` |  |
| `int getPort()` |  |
| `String getServiceName()` |  |
| `String getServiceType()` |  |
| `void removeAttribute(String)` |  |
| `void setAttribute(String, String)` |  |
| `void setHost(java.net.InetAddress)` |  |
| `void setPort(int)` |  |
| `void setServiceName(String)` |  |
| `void setServiceType(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.net.rtp`

### `class AudioCodec`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.rtp.AudioCodec AMR` |  |
| `static final android.net.rtp.AudioCodec GSM` |  |
| `static final android.net.rtp.AudioCodec GSM_EFR` |  |
| `static final android.net.rtp.AudioCodec PCMA` |  |
| `static final android.net.rtp.AudioCodec PCMU` |  |
| `final String fmtp` |  |
| `final String rtpmap` |  |
| `final int type` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.rtp.AudioCodec getCodec(int, String, String)` |  |
| `static android.net.rtp.AudioCodec[] getCodecs()` |  |

---

### `class AudioGroup`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AudioGroup(@NonNull android.content.Context)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MODE_ECHO_SUPPRESSION = 3` |  |
| `static final int MODE_MUTED = 1` |  |
| `static final int MODE_NORMAL = 2` |  |
| `static final int MODE_ON_HOLD = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `int getMode()` |  |
| `android.net.rtp.AudioStream[] getStreams()` |  |
| `void sendDtmf(int)` |  |
| `void setMode(int)` |  |

---

### `class AudioStream`

- **继承：** `android.net.rtp.RtpStream`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AudioStream(java.net.InetAddress) throws java.net.SocketException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.rtp.AudioCodec getCodec()` |  |
| `int getDtmfType()` |  |
| `android.net.rtp.AudioGroup getGroup()` |  |
| `final boolean isBusy()` |  |
| `void join(android.net.rtp.AudioGroup)` |  |
| `void setCodec(android.net.rtp.AudioCodec)` |  |
| `void setDtmfType(int)` |  |

---

### `class RtpStream`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MODE_NORMAL = 0` |  |
| `static final int MODE_RECEIVE_ONLY = 2` |  |
| `static final int MODE_SEND_ONLY = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void associate(java.net.InetAddress, int)` |  |
| `java.net.InetAddress getLocalAddress()` |  |
| `int getLocalPort()` |  |
| `int getMode()` |  |
| `java.net.InetAddress getRemoteAddress()` |  |
| `int getRemotePort()` |  |
| `boolean isBusy()` |  |
| `void release()` |  |
| `void setMode(int)` |  |

---

## Package: `android.net.sip`

### `class SipAudioCall`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SipAudioCall(android.content.Context, android.net.sip.SipProfile)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void answerCall(int) throws android.net.sip.SipException` |  |
| `void attachCall(android.net.sip.SipSession, String) throws android.net.sip.SipException` |  |
| `void close()` |  |
| `void continueCall(int) throws android.net.sip.SipException` |  |
| `void endCall() throws android.net.sip.SipException` |  |
| `android.net.sip.SipProfile getLocalProfile()` |  |
| `android.net.sip.SipProfile getPeerProfile()` |  |
| `int getState()` |  |
| `void holdCall(int) throws android.net.sip.SipException` |  |
| `boolean isInCall()` |  |
| `boolean isMuted()` |  |
| `boolean isOnHold()` |  |
| `void makeCall(android.net.sip.SipProfile, android.net.sip.SipSession, int) throws android.net.sip.SipException` |  |
| `void sendDtmf(int)` |  |
| `void sendDtmf(int, android.os.Message)` |  |
| `void setListener(android.net.sip.SipAudioCall.Listener)` |  |
| `void setListener(android.net.sip.SipAudioCall.Listener, boolean)` |  |
| `void setSpeakerMode(boolean)` |  |
| `void startAudio()` |  |
| `void toggleMute()` |  |

---

### `class static SipAudioCall.Listener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SipAudioCall.Listener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCallBusy(android.net.sip.SipAudioCall)` |  |
| `void onCallEnded(android.net.sip.SipAudioCall)` |  |
| `void onCallEstablished(android.net.sip.SipAudioCall)` |  |
| `void onCallHeld(android.net.sip.SipAudioCall)` |  |
| `void onCalling(android.net.sip.SipAudioCall)` |  |
| `void onChanged(android.net.sip.SipAudioCall)` |  |
| `void onError(android.net.sip.SipAudioCall, int, String)` |  |
| `void onReadyToCall(android.net.sip.SipAudioCall)` |  |
| `void onRinging(android.net.sip.SipAudioCall, android.net.sip.SipProfile)` |  |
| `void onRingingBack(android.net.sip.SipAudioCall)` |  |

---

### `class SipErrorCode`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CLIENT_ERROR = -4` |  |
| `static final int CROSS_DOMAIN_AUTHENTICATION = -11` |  |
| `static final int DATA_CONNECTION_LOST = -10` |  |
| `static final int INVALID_CREDENTIALS = -8` |  |
| `static final int INVALID_REMOTE_URI = -6` |  |
| `static final int IN_PROGRESS = -9` |  |
| `static final int NO_ERROR = 0` |  |
| `static final int PEER_NOT_REACHABLE = -7` |  |
| `static final int SERVER_ERROR = -2` |  |
| `static final int SERVER_UNREACHABLE = -12` |  |
| `static final int SOCKET_ERROR = -1` |  |
| `static final int TIME_OUT = -5` |  |
| `static final int TRANSACTION_TERMINTED = -3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String toString(int)` |  |

---

### `class SipException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SipException()` |  |
| `SipException(String)` |  |
| `SipException(String, Throwable)` |  |

---

### `class SipManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_CALL_ID = "android:sipCallID"` |  |
| `static final String EXTRA_OFFER_SD = "android:sipOfferSD"` |  |
| `static final int INCOMING_CALL_RESULT_CODE = 101` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close(String) throws android.net.sip.SipException` |  |
| `android.net.sip.SipSession createSipSession(android.net.sip.SipProfile, android.net.sip.SipSession.Listener) throws android.net.sip.SipException` |  |
| `static String getCallId(android.content.Intent)` |  |
| `static String getOfferSessionDescription(android.content.Intent)` |  |
| `android.net.sip.SipSession getSessionFor(android.content.Intent) throws android.net.sip.SipException` |  |
| `static boolean isApiSupported(android.content.Context)` |  |
| `static boolean isIncomingCallIntent(android.content.Intent)` |  |
| `boolean isOpened(String) throws android.net.sip.SipException` |  |
| `boolean isRegistered(String) throws android.net.sip.SipException` |  |
| `static boolean isSipWifiOnly(android.content.Context)` |  |
| `static boolean isVoipSupported(android.content.Context)` |  |
| `android.net.sip.SipAudioCall makeAudioCall(android.net.sip.SipProfile, android.net.sip.SipProfile, android.net.sip.SipAudioCall.Listener, int) throws android.net.sip.SipException` |  |
| `android.net.sip.SipAudioCall makeAudioCall(String, String, android.net.sip.SipAudioCall.Listener, int) throws android.net.sip.SipException` |  |
| `static android.net.sip.SipManager newInstance(android.content.Context)` |  |
| `void open(android.net.sip.SipProfile) throws android.net.sip.SipException` |  |
| `void open(android.net.sip.SipProfile, android.app.PendingIntent, android.net.sip.SipRegistrationListener) throws android.net.sip.SipException` |  |
| `void register(android.net.sip.SipProfile, int, android.net.sip.SipRegistrationListener) throws android.net.sip.SipException` |  |
| `void setRegistrationListener(String, android.net.sip.SipRegistrationListener) throws android.net.sip.SipException` |  |
| `android.net.sip.SipAudioCall takeAudioCall(android.content.Intent, android.net.sip.SipAudioCall.Listener) throws android.net.sip.SipException` |  |
| `void unregister(android.net.sip.SipProfile, android.net.sip.SipRegistrationListener) throws android.net.sip.SipException` |  |

---

### `class SipProfile`

- **实现：** `java.lang.Cloneable android.os.Parcelable java.io.Serializable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.os.Parcelable.Creator<android.net.sip.SipProfile> CREATOR` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getAuthUserName()` |  |
| `boolean getAutoRegistration()` |  |
| `String getDisplayName()` |  |
| `String getPassword()` |  |
| `int getPort()` |  |
| `String getProfileName()` |  |
| `String getProtocol()` |  |
| `String getProxyAddress()` |  |
| `boolean getSendKeepAlive()` |  |
| `String getSipDomain()` |  |
| `String getUriString()` |  |
| `String getUserName()` |  |
| `void setCallingUid(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static SipProfile.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SipProfile.Builder(android.net.sip.SipProfile)` |  |
| `SipProfile.Builder(String) throws java.text.ParseException` |  |
| `SipProfile.Builder(String, String) throws java.text.ParseException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.sip.SipProfile build()` |  |
| `android.net.sip.SipProfile.Builder setAuthUserName(String)` |  |
| `android.net.sip.SipProfile.Builder setAutoRegistration(boolean)` |  |
| `android.net.sip.SipProfile.Builder setDisplayName(String)` |  |
| `android.net.sip.SipProfile.Builder setOutboundProxy(String)` |  |
| `android.net.sip.SipProfile.Builder setPassword(String)` |  |
| `android.net.sip.SipProfile.Builder setPort(int) throws java.lang.IllegalArgumentException` |  |
| `android.net.sip.SipProfile.Builder setProfileName(String)` |  |
| `android.net.sip.SipProfile.Builder setProtocol(String) throws java.lang.IllegalArgumentException` |  |
| `android.net.sip.SipProfile.Builder setSendKeepAlive(boolean)` |  |

---

### `interface SipRegistrationListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onRegistering(String)` |  |
| `void onRegistrationDone(String, long)` |  |
| `void onRegistrationFailed(String, int, String)` |  |

---

### `class final SipSession`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void answerCall(String, int)` |  |
| `void changeCall(String, int)` |  |
| `void endCall()` |  |
| `String getCallId()` |  |
| `String getLocalIp()` |  |
| `android.net.sip.SipProfile getLocalProfile()` |  |
| `android.net.sip.SipProfile getPeerProfile()` |  |
| `int getState()` |  |
| `boolean isInCall()` |  |
| `void makeCall(android.net.sip.SipProfile, String, int)` |  |
| `void register(int)` |  |
| `void setListener(android.net.sip.SipSession.Listener)` |  |
| `void unregister()` |  |

---

### `class static SipSession.Listener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SipSession.Listener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCallBusy(android.net.sip.SipSession)` |  |
| `void onCallChangeFailed(android.net.sip.SipSession, int, String)` |  |
| `void onCallEnded(android.net.sip.SipSession)` |  |
| `void onCallEstablished(android.net.sip.SipSession, String)` |  |
| `void onCalling(android.net.sip.SipSession)` |  |
| `void onError(android.net.sip.SipSession, int, String)` |  |
| `void onRegistering(android.net.sip.SipSession)` |  |
| `void onRegistrationDone(android.net.sip.SipSession, int)` |  |
| `void onRegistrationFailed(android.net.sip.SipSession, int, String)` |  |
| `void onRegistrationTimeout(android.net.sip.SipSession)` |  |
| `void onRinging(android.net.sip.SipSession, android.net.sip.SipProfile, String)` |  |
| `void onRingingBack(android.net.sip.SipSession)` |  |

---

### `class static SipSession.State`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DEREGISTERING = 2` |  |
| `static final int INCOMING_CALL = 3` |  |
| `static final int INCOMING_CALL_ANSWERING = 4` |  |
| `static final int IN_CALL = 8` |  |
| `static final int NOT_DEFINED = 101` |  |
| `static final int OUTGOING_CALL = 5` |  |
| `static final int OUTGOING_CALL_CANCELING = 7` |  |
| `static final int OUTGOING_CALL_RING_BACK = 6` |  |
| `static final int PINGING = 9` |  |
| `static final int READY_TO_CALL = 0` |  |
| `static final int REGISTERING = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String toString(int)` |  |

---

## Package: `android.net.ssl`

### `class SSLEngines`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isSupportedEngine(@NonNull javax.net.ssl.SSLEngine)` |  |
| `static void setUseSessionTickets(@NonNull javax.net.ssl.SSLEngine, boolean)` |  |

---

### `class SSLSockets`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isSupportedSocket(@NonNull javax.net.ssl.SSLSocket)` |  |
| `static void setUseSessionTickets(@NonNull javax.net.ssl.SSLSocket, boolean)` |  |

---

## Package: `android.net.wifi`

### `class abstract EasyConnectStatusCallback`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EASY_CONNECT_EVENT_FAILURE_AUTHENTICATION = -2` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_BUSY = -5` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_CANNOT_FIND_NETWORK = -10` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_CONFIGURATION = -4` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_ENROLLEE_AUTHENTICATION = -11` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_ENROLLEE_REJECTED_CONFIGURATION = -12` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_GENERIC = -7` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_INVALID_NETWORK = -9` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_INVALID_URI = -1` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_NOT_COMPATIBLE = -3` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_NOT_SUPPORTED = -8` |  |
| `static final int EASY_CONNECT_EVENT_FAILURE_TIMEOUT = -6` |  |

---

### `class final ScanResult`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScanResult(@NonNull android.net.wifi.ScanResult)` |  |
| `ScanResult()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String BSSID` |  |
| `static final int CHANNEL_WIDTH_160MHZ = 3` |  |
| `static final int CHANNEL_WIDTH_20MHZ = 0` |  |
| `static final int CHANNEL_WIDTH_40MHZ = 1` |  |
| `static final int CHANNEL_WIDTH_80MHZ = 2` |  |
| `static final int CHANNEL_WIDTH_80MHZ_PLUS_MHZ = 4` |  |
| `String SSID` |  |
| `static final int WIFI_STANDARD_11AC = 5` |  |
| `static final int WIFI_STANDARD_11AX = 6` |  |
| `static final int WIFI_STANDARD_11N = 4` |  |
| `static final int WIFI_STANDARD_LEGACY = 1` |  |
| `static final int WIFI_STANDARD_UNKNOWN = 0` |  |
| `String capabilities` |  |
| `int centerFreq0` |  |
| `int centerFreq1` |  |
| `int channelWidth` |  |
| `int frequency` |  |
| `int level` |  |
| `CharSequence operatorFriendlyName` |  |
| `long timestamp` |  |
| `CharSequence venueName` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getWifiStandard()` |  |
| `boolean is80211mcResponder()` |  |
| `boolean isPasspointNetwork()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ScanResult.InformationElement`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScanResult.InformationElement(@NonNull android.net.wifi.ScanResult.InformationElement)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getId()` |  |
| `int getIdExt()` |  |

---

### `class final SoftApConfiguration`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SECURITY_TYPE_OPEN = 0` |  |
| `static final int SECURITY_TYPE_WPA2_PSK = 1` |  |
| `static final int SECURITY_TYPE_WPA3_SAE = 3` |  |
| `static final int SECURITY_TYPE_WPA3_SAE_TRANSITION = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSecurityType()` |  |
| `boolean isHiddenSsid()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `enum SupplicantState`

- **实现：** `android.os.Parcelable`

#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.net.wifi.SupplicantState ASSOCIATED` |  |
| `static final android.net.wifi.SupplicantState ASSOCIATING` |  |
| `static final android.net.wifi.SupplicantState AUTHENTICATING` |  |
| `static final android.net.wifi.SupplicantState COMPLETED` |  |
| `static final android.net.wifi.SupplicantState DISCONNECTED` |  |
| `static final android.net.wifi.SupplicantState DORMANT` |  |
| `static final android.net.wifi.SupplicantState FOUR_WAY_HANDSHAKE` |  |
| `static final android.net.wifi.SupplicantState GROUP_HANDSHAKE` |  |
| `static final android.net.wifi.SupplicantState INACTIVE` |  |
| `static final android.net.wifi.SupplicantState INTERFACE_DISABLED` |  |
| `static final android.net.wifi.SupplicantState INVALID` |  |
| `static final android.net.wifi.SupplicantState SCANNING` |  |
| `static final android.net.wifi.SupplicantState UNINITIALIZED` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static boolean isValidState(android.net.wifi.SupplicantState)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiConfiguration` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **注解：** `@Deprecated`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static WifiConfiguration.AuthAlgorithm` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.GroupCipher` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.GroupMgmtCipher` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.KeyMgmt` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.PairwiseCipher` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.Protocol` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static WifiConfiguration.Status` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class WifiEnterpriseConfig`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiEnterpriseConfig()` |  |
| `WifiEnterpriseConfig(android.net.wifi.WifiEnterpriseConfig)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_WAPI_AS_CERTIFICATE_DATA = "android.net.wifi.extra.WAPI_AS_CERTIFICATE_DATA"` |  |
| `static final String EXTRA_WAPI_AS_CERTIFICATE_NAME = "android.net.wifi.extra.WAPI_AS_CERTIFICATE_NAME"` |  |
| `static final String EXTRA_WAPI_USER_CERTIFICATE_DATA = "android.net.wifi.extra.WAPI_USER_CERTIFICATE_DATA"` |  |
| `static final String EXTRA_WAPI_USER_CERTIFICATE_NAME = "android.net.wifi.extra.WAPI_USER_CERTIFICATE_NAME"` |  |
| `static final String WAPI_AS_CERTIFICATE = "WAPIAS_"` |  |
| `static final String WAPI_USER_CERTIFICATE = "WAPIUSR_"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getAltSubjectMatch()` |  |
| `String getAnonymousIdentity()` |  |
| `java.security.cert.X509Certificate getClientCertificate()` |  |
| `String getDomainSuffixMatch()` |  |
| `int getEapMethod()` |  |
| `String getIdentity()` |  |
| `String getPassword()` |  |
| `int getPhase2Method()` |  |
| `String getPlmn()` |  |
| `String getRealm()` |  |
| `boolean isAuthenticationSimBased()` |  |
| `void setAltSubjectMatch(String)` |  |
| `void setAnonymousIdentity(String)` |  |
| `void setCaCertificate(@Nullable java.security.cert.X509Certificate)` |  |
| `void setCaCertificates(@Nullable java.security.cert.X509Certificate[])` |  |
| `void setClientKeyEntry(java.security.PrivateKey, java.security.cert.X509Certificate)` |  |
| `void setClientKeyEntryWithCertificateChain(java.security.PrivateKey, java.security.cert.X509Certificate[])` |  |
| `void setDomainSuffixMatch(String)` |  |
| `void setEapMethod(int)` |  |
| `void setIdentity(String)` |  |
| `void setPassword(String)` |  |
| `void setPhase2Method(int)` |  |
| `void setPlmn(String)` |  |
| `void setRealm(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiEnterpriseConfig.Eap`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AKA = 5` |  |
| `static final int AKA_PRIME = 6` |  |
| `static final int NONE = -1` |  |
| `static final int PEAP = 0` |  |
| `static final int PWD = 3` |  |
| `static final int SIM = 4` |  |
| `static final int TLS = 1` |  |
| `static final int TTLS = 2` |  |
| `static final int UNAUTH_TLS = 7` |  |
| `static final int WAPI_CERT = 8` |  |

---

### `class static final WifiEnterpriseConfig.Phase2`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AKA = 6` |  |
| `static final int AKA_PRIME = 7` |  |
| `static final int GTC = 4` |  |
| `static final int MSCHAP = 2` |  |
| `static final int MSCHAPV2 = 3` |  |
| `static final int NONE = 0` |  |
| `static final int PAP = 1` |  |
| `static final int SIM = 5` |  |

---

### `class WifiInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FREQUENCY_UNITS = "MHz"` |  |
| `static final String LINK_SPEED_UNITS = "Mbps"` |  |
| `static final int LINK_SPEED_UNKNOWN = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getBSSID()` |  |
| `static android.net.NetworkInfo.DetailedState getDetailedStateOf(android.net.wifi.SupplicantState)` |  |
| `int getFrequency()` |  |
| `boolean getHiddenSSID()` |  |
| `int getIpAddress()` |  |
| `int getLinkSpeed()` |  |
| `String getMacAddress()` |  |
| `int getMaxSupportedRxLinkSpeedMbps()` |  |
| `int getMaxSupportedTxLinkSpeedMbps()` |  |
| `int getNetworkId()` |  |
| `int getRssi()` |  |
| `String getSSID()` |  |
| `android.net.wifi.SupplicantState getSupplicantState()` |  |
| `int getWifiStandard()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiInfo.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiInfo.Builder()` |  |

---

### `class WifiManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK"` |  |
| `static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = "android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE"` |  |
| `static final String ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION = "android.net.wifi.action.WIFI_NETWORK_SUGGESTION_POST_CONNECTION"` |  |
| `static final String ACTION_WIFI_SCAN_AVAILABILITY_CHANGED = "android.net.wifi.action.WIFI_SCAN_AVAILABILITY_CHANGED"` |  |
| `static final String EXTRA_NETWORK_INFO = "networkInfo"` |  |
| `static final String EXTRA_NETWORK_SUGGESTION = "android.net.wifi.extra.NETWORK_SUGGESTION"` |  |
| `static final String EXTRA_NEW_RSSI = "newRssi"` |  |
| `static final String EXTRA_PREVIOUS_WIFI_STATE = "previous_wifi_state"` |  |
| `static final String EXTRA_RESULTS_UPDATED = "resultsUpdated"` |  |
| `static final String EXTRA_SCAN_AVAILABLE = "android.net.wifi.extra.SCAN_AVAILABLE"` |  |
| `static final String EXTRA_WIFI_STATE = "wifi_state"` |  |
| `static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED"` |  |
| `static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE"` |  |
| `static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED"` |  |
| `static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS"` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE = 3` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP = 4` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID = 7` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED = 6` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED = 2` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL = 1` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID = 5` |  |
| `static final int STATUS_NETWORK_SUGGESTIONS_SUCCESS = 0` |  |
| `static final int STATUS_SUGGESTION_CONNECTION_FAILURE_ASSOCIATION = 1` |  |
| `static final int STATUS_SUGGESTION_CONNECTION_FAILURE_AUTHENTICATION = 2` |  |
| `static final int STATUS_SUGGESTION_CONNECTION_FAILURE_IP_PROVISIONING = 3` |  |
| `static final int STATUS_SUGGESTION_CONNECTION_FAILURE_UNKNOWN = 0` |  |
| `static final String UNKNOWN_SSID = "<unknown ssid>"` |  |
| `static final int WIFI_MODE_FULL_HIGH_PERF = 3` |  |
| `static final int WIFI_MODE_FULL_LOW_LATENCY = 4` |  |
| `static final String WIFI_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_STATE_CHANGED"` |  |
| `static final int WIFI_STATE_DISABLED = 1` |  |
| `static final int WIFI_STATE_DISABLING = 0` |  |
| `static final int WIFI_STATE_ENABLED = 3` |  |
| `static final int WIFI_STATE_ENABLING = 2` |  |
| `static final int WIFI_STATE_UNKNOWN = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addOrUpdatePasspointConfiguration(android.net.wifi.hotspot2.PasspointConfiguration)` |  |
| `static int compareSignalLevel(int, int)` |  |
| `android.net.wifi.WifiManager.MulticastLock createMulticastLock(String)` |  |
| `android.net.wifi.WifiManager.WifiLock createWifiLock(int, String)` |  |
| `android.net.wifi.WifiInfo getConnectionInfo()` |  |
| `android.net.DhcpInfo getDhcpInfo()` |  |
| `int getMaxNumberOfNetworkSuggestionsPerApp()` |  |
| `java.util.List<android.net.wifi.ScanResult> getScanResults()` |  |
| `int getWifiState()` |  |
| `boolean is5GHzBandSupported()` |  |
| `boolean is6GHzBandSupported()` |  |
| `boolean isEasyConnectSupported()` |  |
| `boolean isEnhancedOpenSupported()` |  |
| `boolean isEnhancedPowerReportingSupported()` |  |
| `boolean isP2pSupported()` |  |
| `boolean isPreferredNetworkOffloadSupported()` |  |
| `boolean isStaApConcurrencySupported()` |  |
| `boolean isTdlsSupported()` |  |
| `boolean isWapiSupported()` |  |
| `boolean isWifiEnabled()` |  |
| `boolean isWifiStandardSupported(int)` |  |
| `boolean isWpa3SaeSupported()` |  |
| `boolean isWpa3SuiteBSupported()` |  |
| `void setTdlsEnabled(java.net.InetAddress, boolean)` |  |
| `void setTdlsEnabledWithMacAddress(String, boolean)` |  |

---

### `class static WifiManager.LocalOnlyHotspotCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiManager.LocalOnlyHotspotCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ERROR_GENERIC = 2` |  |
| `static final int ERROR_INCOMPATIBLE_MODE = 3` |  |
| `static final int ERROR_NO_CHANNEL = 1` |  |
| `static final int ERROR_TETHERING_DISALLOWED = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFailed(int)` |  |
| `void onStarted(android.net.wifi.WifiManager.LocalOnlyHotspotReservation)` |  |
| `void onStopped()` |  |

---

### `class WifiManager.LocalOnlyHotspotReservation`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |

---

### `class WifiManager.MulticastLock`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiManager.ScanResultsCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void acquire()` |  |
| `boolean isHeld()` |  |
| `void release()` |  |
| `void setReferenceCounted(boolean)` |  |
| `abstract void onScanResultsAvailable()` |  |

---

### `interface static WifiManager.SuggestionConnectionStatusListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onConnectionStatus(@NonNull android.net.wifi.WifiNetworkSuggestion, int)` |  |

---

### `class WifiManager.WifiLock`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void acquire()` |  |
| `boolean isHeld()` |  |
| `void release()` |  |
| `void setReferenceCounted(boolean)` |  |
| `void setWorkSource(android.os.WorkSource)` |  |

---

### `class final WifiNetworkSpecifier`

- **继承：** `android.net.NetworkSpecifier`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiNetworkSpecifier.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiNetworkSpecifier.Builder()` |  |

---

### `class final WifiNetworkSuggestion`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isAppInteractionRequired()` |  |
| `boolean isCredentialSharedWithUser()` |  |
| `boolean isEnhancedOpen()` |  |
| `boolean isHiddenSsid()` |  |
| `boolean isInitialAutojoinEnabled()` |  |
| `boolean isMetered()` |  |
| `boolean isUntrusted()` |  |
| `boolean isUserInteractionRequired()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiNetworkSuggestion.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiNetworkSuggestion.Builder()` |  |

---

### `class WpsInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WpsInfo()` |  |
| `WpsInfo(android.net.wifi.WpsInfo)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String BSSID` |  |
| `static final int DISPLAY = 1` |  |
| `static final int INVALID = 4` |  |
| `static final int KEYPAD = 2` |  |
| `static final int LABEL = 3` |  |
| `static final int PBC = 0` |  |
| `String pin` |  |
| `int setup` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.net.wifi.aware`

### `class AttachCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AttachCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onAttachFailed()` |  |
| `void onAttached(android.net.wifi.aware.WifiAwareSession)` |  |

---

### `class final Characteristics`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int WIFI_AWARE_CIPHER_SUITE_NCS_SK_128 = 1` |  |
| `static final int WIFI_AWARE_CIPHER_SUITE_NCS_SK_256 = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getMaxMatchFilterLength()` |  |
| `int getMaxServiceNameLength()` |  |
| `int getMaxServiceSpecificInfoLength()` |  |
| `int getSupportedCipherSuites()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DiscoverySession`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void sendMessage(@NonNull android.net.wifi.aware.PeerHandle, int, @Nullable byte[])` |  |

---

### `class DiscoverySessionCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DiscoverySessionCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onMessageReceived(android.net.wifi.aware.PeerHandle, byte[])` |  |
| `void onMessageSendFailed(int)` |  |
| `void onMessageSendSucceeded(int)` |  |
| `void onPublishStarted(@NonNull android.net.wifi.aware.PublishDiscoverySession)` |  |
| `void onServiceDiscovered(android.net.wifi.aware.PeerHandle, byte[], java.util.List<byte[]>)` |  |
| `void onServiceDiscoveredWithinRange(android.net.wifi.aware.PeerHandle, byte[], java.util.List<byte[]>, int)` |  |
| `void onSessionConfigFailed()` |  |
| `void onSessionConfigUpdated()` |  |
| `void onSessionTerminated()` |  |
| `void onSubscribeStarted(@NonNull android.net.wifi.aware.SubscribeDiscoverySession)` |  |

---

### `class IdentityChangedListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IdentityChangedListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onIdentityChanged(byte[])` |  |

---

### `class final ParcelablePeerHandle`

- **继承：** `android.net.wifi.aware.PeerHandle`
- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ParcelablePeerHandle(@NonNull android.net.wifi.aware.PeerHandle)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PeerHandle`


---

### `class final PublishConfig`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int PUBLISH_TYPE_SOLICITED = 1` |  |
| `static final int PUBLISH_TYPE_UNSOLICITED = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PublishConfig.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PublishConfig.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.wifi.aware.PublishConfig build()` |  |
| `android.net.wifi.aware.PublishConfig.Builder setMatchFilter(@Nullable java.util.List<byte[]>)` |  |
| `android.net.wifi.aware.PublishConfig.Builder setPublishType(int)` |  |
| `android.net.wifi.aware.PublishConfig.Builder setRangingEnabled(boolean)` |  |
| `android.net.wifi.aware.PublishConfig.Builder setServiceName(@NonNull String)` |  |
| `android.net.wifi.aware.PublishConfig.Builder setServiceSpecificInfo(@Nullable byte[])` |  |
| `android.net.wifi.aware.PublishConfig.Builder setTerminateNotificationEnabled(boolean)` |  |
| `android.net.wifi.aware.PublishConfig.Builder setTtlSec(int)` |  |

---

### `class PublishDiscoverySession`

- **继承：** `android.net.wifi.aware.DiscoverySession`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void updatePublish(@NonNull android.net.wifi.aware.PublishConfig)` |  |

---

### `class final SubscribeConfig`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SUBSCRIBE_TYPE_ACTIVE = 1` |  |
| `static final int SUBSCRIBE_TYPE_PASSIVE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final SubscribeConfig.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SubscribeConfig.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.wifi.aware.SubscribeConfig build()` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setMatchFilter(@Nullable java.util.List<byte[]>)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setMaxDistanceMm(int)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setMinDistanceMm(int)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setServiceName(@NonNull String)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setServiceSpecificInfo(@Nullable byte[])` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setSubscribeType(int)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setTerminateNotificationEnabled(boolean)` |  |
| `android.net.wifi.aware.SubscribeConfig.Builder setTtlSec(int)` |  |

---

### `class SubscribeDiscoverySession`

- **继承：** `android.net.wifi.aware.DiscoverySession`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void updateSubscribe(@NonNull android.net.wifi.aware.SubscribeConfig)` |  |

---

### `class WifiAwareManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_WIFI_AWARE_STATE_CHANGED = "android.net.wifi.aware.action.WIFI_AWARE_STATE_CHANGED"` |  |
| `static final int WIFI_AWARE_DATA_PATH_ROLE_INITIATOR = 0` |  |
| `static final int WIFI_AWARE_DATA_PATH_ROLE_RESPONDER = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void attach(@NonNull android.net.wifi.aware.AttachCallback, @Nullable android.os.Handler)` |  |
| `void attach(@NonNull android.net.wifi.aware.AttachCallback, @NonNull android.net.wifi.aware.IdentityChangedListener, @Nullable android.os.Handler)` |  |
| `android.net.wifi.aware.Characteristics getCharacteristics()` |  |
| `boolean isAvailable()` |  |

---

### `class final WifiAwareNetworkInfo`

- **实现：** `android.os.Parcelable android.net.TransportInfo`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getPort()` |  |
| `int getTransportProtocol()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final WifiAwareNetworkSpecifier`

- **继承：** `android.net.NetworkSpecifier`
- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiAwareNetworkSpecifier.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiAwareNetworkSpecifier.Builder(@NonNull android.net.wifi.aware.DiscoverySession, @NonNull android.net.wifi.aware.PeerHandle)` |  |

---

### `class WifiAwareSession`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `android.net.NetworkSpecifier createNetworkSpecifierOpen(int, @NonNull byte[])` |  |
| `android.net.NetworkSpecifier createNetworkSpecifierPassphrase(int, @NonNull byte[], @NonNull String)` |  |
| `void publish(@NonNull android.net.wifi.aware.PublishConfig, @NonNull android.net.wifi.aware.DiscoverySessionCallback, @Nullable android.os.Handler)` |  |
| `void subscribe(@NonNull android.net.wifi.aware.SubscribeConfig, @NonNull android.net.wifi.aware.DiscoverySessionCallback, @Nullable android.os.Handler)` |  |

---

## Package: `android.net.wifi.hotspot2`

### `class final ConfigParser`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.hotspot2.PasspointConfiguration parsePasspointConfig(String, byte[])` |  |

---

### `class final PasspointConfiguration`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PasspointConfiguration()` |  |
| `PasspointConfiguration(android.net.wifi.hotspot2.PasspointConfiguration)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.net.wifi.hotspot2.pps.Credential getCredential()` |  |
| `android.net.wifi.hotspot2.pps.HomeSp getHomeSp()` |  |
| `long getSubscriptionExpirationTimeMillis()` |  |
| `boolean isOsuProvisioned()` |  |
| `void setCredential(android.net.wifi.hotspot2.pps.Credential)` |  |
| `void setHomeSp(android.net.wifi.hotspot2.pps.HomeSp)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.net.wifi.hotspot2.omadm`

### `class final PpsMoParser`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.hotspot2.PasspointConfiguration parseMoText(String)` |  |

---

## Package: `android.net.wifi.hotspot2.pps`

### `class final Credential`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Credential()` |  |
| `Credential(android.net.wifi.hotspot2.pps.Credential)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.security.cert.X509Certificate getCaCertificate()` |  |
| `android.net.wifi.hotspot2.pps.Credential.CertificateCredential getCertCredential()` |  |
| `java.security.cert.X509Certificate[] getClientCertificateChain()` |  |
| `java.security.PrivateKey getClientPrivateKey()` |  |
| `String getRealm()` |  |
| `android.net.wifi.hotspot2.pps.Credential.SimCredential getSimCredential()` |  |
| `android.net.wifi.hotspot2.pps.Credential.UserCredential getUserCredential()` |  |
| `void setCaCertificate(java.security.cert.X509Certificate)` |  |
| `void setCertCredential(android.net.wifi.hotspot2.pps.Credential.CertificateCredential)` |  |
| `void setClientCertificateChain(java.security.cert.X509Certificate[])` |  |
| `void setClientPrivateKey(java.security.PrivateKey)` |  |
| `void setRealm(String)` |  |
| `void setSimCredential(android.net.wifi.hotspot2.pps.Credential.SimCredential)` |  |
| `void setUserCredential(android.net.wifi.hotspot2.pps.Credential.UserCredential)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Credential.CertificateCredential`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Credential.CertificateCredential()` |  |
| `Credential.CertificateCredential(android.net.wifi.hotspot2.pps.Credential.CertificateCredential)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `byte[] getCertSha256Fingerprint()` |  |
| `String getCertType()` |  |
| `void setCertSha256Fingerprint(byte[])` |  |
| `void setCertType(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Credential.SimCredential`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Credential.SimCredential()` |  |
| `Credential.SimCredential(android.net.wifi.hotspot2.pps.Credential.SimCredential)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getEapType()` |  |
| `String getImsi()` |  |
| `void setEapType(int)` |  |
| `void setImsi(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Credential.UserCredential`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Credential.UserCredential()` |  |
| `Credential.UserCredential(android.net.wifi.hotspot2.pps.Credential.UserCredential)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getEapType()` |  |
| `String getNonEapInnerMethod()` |  |
| `String getPassword()` |  |
| `String getUsername()` |  |
| `void setEapType(int)` |  |
| `void setNonEapInnerMethod(String)` |  |
| `void setPassword(String)` |  |
| `void setUsername(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final HomeSp`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HomeSp()` |  |
| `HomeSp(android.net.wifi.hotspot2.pps.HomeSp)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getFqdn()` |  |
| `String getFriendlyName()` |  |
| `long[] getRoamingConsortiumOis()` |  |
| `void setFqdn(String)` |  |
| `void setFriendlyName(String)` |  |
| `void setRoamingConsortiumOis(long[])` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.net.wifi.p2p`

### `class WifiP2pConfig`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pConfig()` |  |
| `WifiP2pConfig(android.net.wifi.p2p.WifiP2pConfig)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GROUP_OWNER_BAND_2GHZ = 1` |  |
| `static final int GROUP_OWNER_BAND_5GHZ = 2` |  |
| `static final int GROUP_OWNER_BAND_AUTO = 0` |  |
| `static final int GROUP_OWNER_INTENT_AUTO = -1` |  |
| `static final int GROUP_OWNER_INTENT_MAX = 15` |  |
| `static final int GROUP_OWNER_INTENT_MIN = 0` |  |
| `String deviceAddress` |  |
| `android.net.wifi.WpsInfo wps` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getGroupOwnerBand()` |  |
| `int getNetworkId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final WifiP2pConfig.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pConfig.Builder()` |  |

---

### `class WifiP2pDevice`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pDevice()` |  |
| `WifiP2pDevice(android.net.wifi.p2p.WifiP2pDevice)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AVAILABLE = 3` |  |
| `static final int CONNECTED = 0` |  |
| `static final int FAILED = 2` |  |
| `static final int INVITED = 1` |  |
| `static final int UNAVAILABLE = 4` |  |
| `String deviceAddress` |  |
| `String deviceName` |  |
| `String primaryDeviceType` |  |
| `String secondaryDeviceType` |  |
| `int status` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isGroupOwner()` |  |
| `boolean isServiceDiscoveryCapable()` |  |
| `void update(@NonNull android.net.wifi.p2p.WifiP2pDevice)` |  |
| `boolean wpsDisplaySupported()` |  |
| `boolean wpsKeypadSupported()` |  |
| `boolean wpsPbcSupported()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pDeviceList`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pDeviceList()` |  |
| `WifiP2pDeviceList(android.net.wifi.p2p.WifiP2pDeviceList)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.net.wifi.p2p.WifiP2pDevice get(String)` |  |
| `java.util.Collection<android.net.wifi.p2p.WifiP2pDevice> getDeviceList()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pGroup`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pGroup()` |  |
| `WifiP2pGroup(android.net.wifi.p2p.WifiP2pGroup)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int NETWORK_ID_PERSISTENT = -2` |  |
| `static final int NETWORK_ID_TEMPORARY = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.Collection<android.net.wifi.p2p.WifiP2pDevice> getClientList()` |  |
| `int getFrequency()` |  |
| `String getInterface()` |  |
| `int getNetworkId()` |  |
| `String getNetworkName()` |  |
| `android.net.wifi.p2p.WifiP2pDevice getOwner()` |  |
| `String getPassphrase()` |  |
| `boolean isGroupOwner()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pInfo()` |  |
| `WifiP2pInfo(android.net.wifi.p2p.WifiP2pInfo)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean groupFormed` |  |
| `java.net.InetAddress groupOwnerAddress` |  |
| `boolean isGroupOwner` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BUSY = 2` |  |
| `static final int ERROR = 0` |  |
| `static final String EXTRA_DISCOVERY_STATE = "discoveryState"` |  |
| `static final String EXTRA_NETWORK_INFO = "networkInfo"` |  |
| `static final String EXTRA_P2P_DEVICE_LIST = "wifiP2pDeviceList"` |  |
| `static final String EXTRA_WIFI_P2P_DEVICE = "wifiP2pDevice"` |  |
| `static final String EXTRA_WIFI_P2P_GROUP = "p2pGroupInfo"` |  |
| `static final String EXTRA_WIFI_P2P_INFO = "wifiP2pInfo"` |  |
| `static final String EXTRA_WIFI_STATE = "wifi_p2p_state"` |  |
| `static final int NO_SERVICE_REQUESTS = 3` |  |
| `static final int P2P_UNSUPPORTED = 1` |  |
| `static final String WIFI_P2P_CONNECTION_CHANGED_ACTION = "android.net.wifi.p2p.CONNECTION_STATE_CHANGE"` |  |
| `static final String WIFI_P2P_DISCOVERY_CHANGED_ACTION = "android.net.wifi.p2p.DISCOVERY_STATE_CHANGE"` |  |
| `static final int WIFI_P2P_DISCOVERY_STARTED = 2` |  |
| `static final int WIFI_P2P_DISCOVERY_STOPPED = 1` |  |
| `static final String WIFI_P2P_PEERS_CHANGED_ACTION = "android.net.wifi.p2p.PEERS_CHANGED"` |  |
| `static final String WIFI_P2P_STATE_CHANGED_ACTION = "android.net.wifi.p2p.STATE_CHANGED"` |  |
| `static final int WIFI_P2P_STATE_DISABLED = 1` |  |
| `static final int WIFI_P2P_STATE_ENABLED = 2` |  |
| `static final String WIFI_P2P_THIS_DEVICE_CHANGED_ACTION = "android.net.wifi.p2p.THIS_DEVICE_CHANGED"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addServiceRequest(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.nsd.WifiP2pServiceRequest, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void cancelConnect(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void clearLocalServices(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void clearServiceRequests(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `android.net.wifi.p2p.WifiP2pManager.Channel initialize(android.content.Context, android.os.Looper, android.net.wifi.p2p.WifiP2pManager.ChannelListener)` |  |
| `void removeGroup(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void removeLocalService(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.nsd.WifiP2pServiceInfo, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void removeServiceRequest(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.nsd.WifiP2pServiceRequest, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |
| `void requestConnectionInfo(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener)` |  |
| `void requestDiscoveryState(@NonNull android.net.wifi.p2p.WifiP2pManager.Channel, @NonNull android.net.wifi.p2p.WifiP2pManager.DiscoveryStateListener)` |  |
| `void requestNetworkInfo(@NonNull android.net.wifi.p2p.WifiP2pManager.Channel, @NonNull android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener)` |  |
| `void requestP2pState(@NonNull android.net.wifi.p2p.WifiP2pManager.Channel, @NonNull android.net.wifi.p2p.WifiP2pManager.P2pStateListener)` |  |
| `void setDnsSdResponseListeners(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener, android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener)` |  |
| `void setServiceResponseListener(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ServiceResponseListener)` |  |
| `void setUpnpServiceResponseListener(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.UpnpServiceResponseListener)` |  |
| `void stopPeerDiscovery(android.net.wifi.p2p.WifiP2pManager.Channel, android.net.wifi.p2p.WifiP2pManager.ActionListener)` |  |

---

### `interface static WifiP2pManager.ActionListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFailure(int)` |  |
| `void onSuccess()` |  |

---

### `class static WifiP2pManager.Channel`

- **实现：** `java.lang.AutoCloseable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |

---

### `interface static WifiP2pManager.ChannelListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onChannelDisconnected()` |  |

---

### `interface static WifiP2pManager.ConnectionInfoListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onConnectionInfoAvailable(android.net.wifi.p2p.WifiP2pInfo)` |  |

---

### `interface static WifiP2pManager.DeviceInfoListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDeviceInfoAvailable(@Nullable android.net.wifi.p2p.WifiP2pDevice)` |  |

---

### `interface static WifiP2pManager.DiscoveryStateListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDiscoveryStateAvailable(int)` |  |

---

### `interface static WifiP2pManager.DnsSdServiceResponseListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDnsSdServiceAvailable(String, String, android.net.wifi.p2p.WifiP2pDevice)` |  |

---

### `interface static WifiP2pManager.DnsSdTxtRecordListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDnsSdTxtRecordAvailable(String, java.util.Map<java.lang.String,java.lang.String>, android.net.wifi.p2p.WifiP2pDevice)` |  |

---

### `interface static WifiP2pManager.GroupInfoListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onGroupInfoAvailable(android.net.wifi.p2p.WifiP2pGroup)` |  |

---

### `interface static WifiP2pManager.NetworkInfoListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onNetworkInfoAvailable(@NonNull android.net.NetworkInfo)` |  |

---

### `interface static WifiP2pManager.P2pStateListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onP2pStateAvailable(int)` |  |

---

### `interface static WifiP2pManager.PeerListListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onPeersAvailable(android.net.wifi.p2p.WifiP2pDeviceList)` |  |

---

### `interface static WifiP2pManager.ServiceResponseListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onServiceAvailable(int, byte[], android.net.wifi.p2p.WifiP2pDevice)` |  |

---

### `interface static WifiP2pManager.UpnpServiceResponseListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onUpnpServiceAvailable(java.util.List<java.lang.String>, android.net.wifi.p2p.WifiP2pDevice)` |  |

---

### `class final WifiP2pWfdInfo`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WifiP2pWfdInfo()` |  |
| `WifiP2pWfdInfo(@Nullable android.net.wifi.p2p.WifiP2pWfdInfo)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DEVICE_TYPE_PRIMARY_SINK = 1` |  |
| `static final int DEVICE_TYPE_SECONDARY_SINK = 2` |  |
| `static final int DEVICE_TYPE_SOURCE_OR_PRIMARY_SINK = 3` |  |
| `static final int DEVICE_TYPE_WFD_SOURCE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getControlPort()` |  |
| `int getDeviceType()` |  |
| `int getMaxThroughput()` |  |
| `boolean isContentProtectionSupported()` |  |
| `boolean isEnabled()` |  |
| `boolean isSessionAvailable()` |  |
| `void setContentProtectionSupported(boolean)` |  |
| `void setControlPort(@IntRange(from=0) int)` |  |
| `boolean setDeviceType(int)` |  |
| `void setEnabled(boolean)` |  |
| `void setMaxThroughput(@IntRange(from=0) int)` |  |
| `void setSessionAvailable(boolean)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

## Package: `android.net.wifi.p2p.nsd`

### `class WifiP2pDnsSdServiceInfo`

- **继承：** `android.net.wifi.p2p.nsd.WifiP2pServiceInfo`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo newInstance(String, String, java.util.Map<java.lang.String,java.lang.String>)` |  |

---

### `class WifiP2pDnsSdServiceRequest`

- **继承：** `android.net.wifi.p2p.nsd.WifiP2pServiceRequest`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest newInstance()` |  |
| `static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest newInstance(String)` |  |
| `static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest newInstance(String, String)` |  |

---

### `class WifiP2pServiceInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SERVICE_TYPE_ALL = 0` |  |
| `static final int SERVICE_TYPE_BONJOUR = 1` |  |
| `static final int SERVICE_TYPE_UPNP = 2` |  |
| `static final int SERVICE_TYPE_VENDOR_SPECIFIC = 255` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pServiceRequest`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.net.wifi.p2p.nsd.WifiP2pServiceRequest newInstance(int, String)` |  |
| `static android.net.wifi.p2p.nsd.WifiP2pServiceRequest newInstance(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiP2pUpnpServiceInfo`

- **继承：** `android.net.wifi.p2p.nsd.WifiP2pServiceInfo`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.p2p.nsd.WifiP2pUpnpServiceInfo newInstance(String, String, java.util.List<java.lang.String>)` |  |

---

### `class WifiP2pUpnpServiceRequest`

- **继承：** `android.net.wifi.p2p.nsd.WifiP2pServiceRequest`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest newInstance()` |  |
| `static android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest newInstance(String)` |  |

---

## Package: `android.net.wifi.rtt`

### `class CivicLocationKeys`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ADDITIONAL_CODE = 32` |  |
| `static final int APT = 26` |  |
| `static final int BOROUGH = 4` |  |
| `static final int BRANCH_ROAD_NAME = 36` |  |
| `static final int BUILDING = 25` |  |
| `static final int CITY = 3` |  |
| `static final int COUNTY = 2` |  |
| `static final int DESK = 33` |  |
| `static final int FLOOR = 27` |  |
| `static final int GROUP_OF_STREETS = 6` |  |
| `static final int HNO = 19` |  |
| `static final int HNS = 20` |  |
| `static final int LANGUAGE = 0` |  |
| `static final int LMK = 21` |  |
| `static final int LOC = 22` |  |
| `static final int NAM = 23` |  |
| `static final int NEIGHBORHOOD = 5` |  |
| `static final int PCN = 30` |  |
| `static final int POD = 17` |  |
| `static final int POSTAL_CODE = 24` |  |
| `static final int PO_BOX = 31` |  |
| `static final int PRD = 16` |  |
| `static final int PRIMARY_ROAD_NAME = 34` |  |
| `static final int ROAD_SECTION = 35` |  |
| `static final int ROOM = 28` |  |
| `static final int SCRIPT = 128` |  |
| `static final int STATE = 1` |  |
| `static final int STREET_NAME_POST_MODIFIER = 39` |  |
| `static final int STREET_NAME_PRE_MODIFIER = 38` |  |
| `static final int STS = 18` |  |
| `static final int SUBBRANCH_ROAD_NAME = 37` |  |
| `static final int TYPE_OF_PLACE = 29` |  |

---

### `class final RangingRequest`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static int getMaxPeers()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final RangingRequest.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RangingRequest.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.net.wifi.rtt.RangingRequest.Builder addAccessPoint(@NonNull android.net.wifi.ScanResult)` |  |
| `android.net.wifi.rtt.RangingRequest.Builder addAccessPoints(@NonNull java.util.List<android.net.wifi.ScanResult>)` |  |
| `android.net.wifi.rtt.RangingRequest.Builder addWifiAwarePeer(@NonNull android.net.MacAddress)` |  |
| `android.net.wifi.rtt.RangingRequest.Builder addWifiAwarePeer(@NonNull android.net.wifi.aware.PeerHandle)` |  |
| `android.net.wifi.rtt.RangingRequest build()` |  |

---

### `class final RangingResult`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATUS_FAIL = 1` |  |
| `static final int STATUS_RESPONDER_DOES_NOT_SUPPORT_IEEE80211MC = 2` |  |
| `static final int STATUS_SUCCESS = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDistanceMm()` |  |
| `int getDistanceStdDevMm()` |  |
| `int getNumAttemptedMeasurements()` |  |
| `int getNumSuccessfulMeasurements()` |  |
| `long getRangingTimestampMillis()` |  |
| `int getRssi()` |  |
| `int getStatus()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract RangingResultCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RangingResultCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATUS_CODE_FAIL = 1` |  |
| `static final int STATUS_CODE_FAIL_RTT_NOT_AVAILABLE = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void onRangingFailure(int)` |  |
| `abstract void onRangingResults(@NonNull java.util.List<android.net.wifi.rtt.RangingResult>)` |  |

---

### `class final ResponderLocation`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALTITUDE_FLOORS = 2` |  |
| `static final int ALTITUDE_METERS = 1` |  |
| `static final int ALTITUDE_UNDEFINED = 0` |  |
| `static final int DATUM_NAD83_MLLW = 3` |  |
| `static final int DATUM_NAD83_NAV88 = 2` |  |
| `static final int DATUM_UNDEFINED = 0` |  |
| `static final int DATUM_WGS84 = 1` |  |
| `static final int LCI_VERSION_1 = 1` |  |
| `static final int LOCATION_FIXED = 0` |  |
| `static final int LOCATION_MOVEMENT_UNKNOWN = 2` |  |
| `static final int LOCATION_RESERVED = 3` |  |
| `static final int LOCATION_VARIABLE = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `double getAltitude()` |  |
| `int getAltitudeType()` |  |
| `double getAltitudeUncertainty()` |  |
| `java.util.List<android.net.MacAddress> getColocatedBssids()` |  |
| `int getDatum()` |  |
| `int getExpectedToMove()` |  |
| `double getFloorNumber()` |  |
| `double getHeightAboveFloorMeters()` |  |
| `double getHeightAboveFloorUncertaintyMeters()` |  |
| `double getLatitude()` |  |
| `double getLatitudeUncertainty()` |  |
| `int getLciVersion()` |  |
| `double getLongitude()` |  |
| `double getLongitudeUncertainty()` |  |
| `boolean getRegisteredLocationAgreementIndication()` |  |
| `boolean isLciSubelementValid()` |  |
| `boolean isZaxisSubelementValid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WifiRttManager`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_WIFI_RTT_STATE_CHANGED = "android.net.wifi.rtt.action.WIFI_RTT_STATE_CHANGED"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isAvailable()` |  |

---

