# A2OH-NETWORKING: Android to OpenHarmony Networking Migration

## Quick Reference

| Android | OpenHarmony | Import |
|---|---|---|
| `HttpURLConnection` | `@ohos.net.http` | `import http from '@ohos.net.http'` |
| `OkHttpClient` / `Retrofit` | `@ohos.net.http` | `import http from '@ohos.net.http'` |
| `WebSocket` (OkHttp) | `@ohos.net.webSocket` | `import webSocket from '@ohos.net.webSocket'` |
| `java.net.Socket` | `@ohos.net.socket` | `import socket from '@ohos.net.socket'` |
| `ConnectivityManager` | `@ohos.net.connection` | `import connection from '@ohos.net.connection'` |
| `WifiManager` | `@ohos.wifiManager` | `import wifiManager from '@ohos.wifiManager'` |
| `BluetoothAdapter` | `@ohos.bluetooth.access` | `import bluetooth from '@ohos.bluetooth.access'` |
| `BluetoothLeScanner` | `@ohos.bluetooth.ble` | `import ble from '@ohos.bluetooth.ble'` |

## Permission Mapping

| Android | OpenHarmony |
|---|---|
| `android.permission.INTERNET` | `ohos.permission.INTERNET` |
| `android.permission.ACCESS_NETWORK_STATE` | `ohos.permission.GET_NETWORK_INFO` |
| `android.permission.ACCESS_WIFI_STATE` | `ohos.permission.GET_WIFI_INFO` |
| `android.permission.CHANGE_WIFI_STATE` | `ohos.permission.SET_WIFI_INFO` |
| `android.permission.BLUETOOTH` | `ohos.permission.ACCESS_BLUETOOTH` |
| `android.permission.BLUETOOTH_ADMIN` | `ohos.permission.ACCESS_BLUETOOTH` |

Declare in `module.json5` under `requestPermissions`:
```json
"requestPermissions": [
  { "name": "ohos.permission.INTERNET" },
  { "name": "ohos.permission.GET_NETWORK_INFO" }
]
```

---

## RULE 1: HttpURLConnection / OkHttp GET Request → @ohos.net.http

**Android (Java):**
```java
URL url = new URL("https://api.example.com/data");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setRequestProperty("Authorization", "Bearer " + token);
int code = conn.getResponseCode();
InputStream is = conn.getInputStream();
String body = new BufferedReader(new InputStreamReader(is))
    .lines().collect(Collectors.joining("\n"));
conn.disconnect();
```

**OpenHarmony (ArkTS):**
```typescript
import http from '@ohos.net.http';

let httpRequest = http.createHttp();
let response = await httpRequest.request('https://api.example.com/data', {
  method: http.RequestMethod.GET,
  header: { 'Authorization': 'Bearer ' + token }
});
let code: number = response.responseCode;
let body: string = response.result as string;
httpRequest.destroy();
```

Key differences:
- One `createHttp()` per request. Call `destroy()` after use.
- Returns `Promise<HttpResponse>` with `responseCode`, `result`, `header`.
- No streams; `result` is the full response body.

---

## RULE 2: POST with JSON Body

**Android (OkHttp):**
```java
OkHttpClient client = new OkHttpClient();
MediaType JSON = MediaType.parse("application/json");
RequestBody body = RequestBody.create(JSON, jsonString);
Request request = new Request.Builder()
    .url("https://api.example.com/users")
    .post(body)
    .addHeader("Content-Type", "application/json")
    .build();
Response response = client.newCall(request).execute();
String result = response.body().string();
```

**OpenHarmony (ArkTS):**
```typescript
import http from '@ohos.net.http';

let httpRequest = http.createHttp();
let response = await httpRequest.request('https://api.example.com/users', {
  method: http.RequestMethod.POST,
  header: { 'Content-Type': 'application/json' },
  extraData: jsonString
});
let result: string = response.result as string;
httpRequest.destroy();
```

Key differences:
- `extraData` replaces `RequestBody`. Pass a string or object directly.
- No separate `MediaType`; set `Content-Type` in `header`.

---

## RULE 3: Retrofit-style Service → Helper Class

**Android (Retrofit):**
```java
public interface ApiService {
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int id);

    @POST("users")
    Call<User> createUser(@Body User user);
}
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
ApiService api = retrofit.create(ApiService.class);
```

**OpenHarmony (ArkTS):**
```typescript
import http from '@ohos.net.http';

class ApiService {
  private baseUrl: string = 'https://api.example.com';

  async getUser(id: number): Promise<User> {
    let req = http.createHttp();
    let resp = await req.request(`${this.baseUrl}/users/${id}`, {
      method: http.RequestMethod.GET
    });
    req.destroy();
    return JSON.parse(resp.result as string) as User;
  }

  async createUser(user: User): Promise<User> {
    let req = http.createHttp();
    let resp = await req.request(`${this.baseUrl}/users`, {
      method: http.RequestMethod.POST,
      header: { 'Content-Type': 'application/json' },
      extraData: JSON.stringify(user)
    });
    req.destroy();
    return JSON.parse(resp.result as string) as User;
  }
}
```

No Retrofit equivalent exists. Build a thin wrapper class manually.

---

## RULE 4: WebSocket → @ohos.net.webSocket

**Android (OkHttp):**
```java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder().url("wss://echo.example.com").build();
client.newWebSocket(request, new WebSocketListener() {
    @Override public void onOpen(WebSocket ws, Response resp) { }
    @Override public void onMessage(WebSocket ws, String text) { }
    @Override public void onClosed(WebSocket ws, int code, String reason) { }
    @Override public void onFailure(WebSocket ws, Throwable t, Response resp) { }
});
```

**OpenHarmony (ArkTS):**
```typescript
import webSocket from '@ohos.net.webSocket';

let ws = webSocket.createWebSocket();
ws.on('open', (err, value) => {
  console.info('WebSocket connected');
});
ws.on('message', (err, value: string) => {
  console.info('Received: ' + value);
});
ws.on('close', (err, value) => {
  console.info('Closed: ' + JSON.stringify(value));
});
ws.on('error', (err) => {
  console.error('Error: ' + JSON.stringify(err));
});
ws.connect('wss://echo.example.com');

// Send data after connection is open
ws.send('hello');
// Close when done
ws.close();
```

Key differences:
- Register event listeners with `ws.on()` before calling `ws.connect()`.
- Events: `'open'`, `'message'`, `'close'`, `'error'`.

---

## RULE 5: TCP Socket → @ohos.net.socket

**Android (Java):**
```java
Socket socket = new Socket("192.168.1.100", 8080);
OutputStream out = socket.getOutputStream();
out.write("hello".getBytes());
InputStream in = socket.getInputStream();
byte[] buf = new byte[1024];
int len = in.read(buf);
String received = new String(buf, 0, len);
socket.close();
```

**OpenHarmony (ArkTS):**
```typescript
import socket from '@ohos.net.socket';

let tcp = socket.constructTCPSocketInstance();
await tcp.bind({ address: '0.0.0.0', port: 0 });
await tcp.connect({ address: { address: '192.168.1.100', port: 8080 } });

tcp.on('message', (value) => {
  let received = String.fromCharCode(...new Uint8Array(value.message));
  console.info('Received: ' + received);
});

await tcp.send({ data: 'hello' });
// When done:
tcp.close();
```

Key differences:
- Must `bind()` before `connect()` (bind to `0.0.0.0:0` for auto-assign).
- Incoming data arrives via `on('message')` event, not blocking read.
- UDP uses `constructUDPSocketInstance()` with `send({address, data})`.

---

## RULE 6: ConnectivityManager → @ohos.net.connection

**Android (Java):**
```java
ConnectivityManager cm = (ConnectivityManager)
    getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo info = cm.getActiveNetworkInfo();
boolean isConnected = info != null && info.isConnected();

// Listen for changes
cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
    @Override public void onAvailable(Network network) { }
    @Override public void onLost(Network network) { }
});
```

**OpenHarmony (ArkTS):**
```typescript
import connection from '@ohos.net.connection';

let hasNet: boolean = await connection.hasDefaultNet();

let netHandle = await connection.getDefaultNet();
let props = await connection.getConnectionProperties(netHandle);
console.info('Interface: ' + props.interfaceName);

// Listen for changes
connection.on('netAvailable', (netHandle) => {
  console.info('Network available');
});
connection.on('netLost', (netHandle) => {
  console.info('Network lost');
});
```

Key differences:
- `hasDefaultNet()` replaces `getActiveNetworkInfo().isConnected()`.
- Event-based: `'netAvailable'`, `'netLost'`, `'netCapabilitiesChange'`.

---

## RULE 7: WifiManager → @ohos.wifiManager

**Android (Java):**
```java
WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
boolean enabled = wm.isWifiEnabled();
WifiInfo info = wm.getConnectionInfo();
String ssid = info.getSSID();
List<ScanResult> results = wm.getScanResults();
```

**OpenHarmony (ArkTS):**
```typescript
import wifiManager from '@ohos.wifiManager';

let active: boolean = wifiManager.isWifiActive();
let info = await wifiManager.getLinkedInfo();
let ssid: string = info.ssid;
let scanResults = await wifiManager.getScanResults();
for (let ap of scanResults) {
  console.info('SSID: ' + ap.ssid + ' RSSI: ' + ap.rssi);
}
```

Key differences:
- `isWifiActive()` replaces `isWifiEnabled()`.
- `getLinkedInfo()` replaces `getConnectionInfo()`.
- `getScanResults()` returns `Promise<Array<WifiScanInfo>>`.

---

## RULE 8: Bluetooth → @ohos.bluetooth.access / ble

**Android (Java):**
```java
BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
adapter.enable();
BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
scanner.startScan(new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String name = device.getName();
    }
});
```

**OpenHarmony (ArkTS):**
```typescript
import bluetooth from '@ohos.bluetooth.access';
import ble from '@ohos.bluetooth.ble';

bluetooth.enableBluetooth();
let state = bluetooth.getState(); // BluetoothState enum

ble.on('BLEDeviceFind', (devices) => {
  for (let device of devices) {
    console.info('Found: ' + device.deviceId);
  }
});
ble.startBLEScan([]);  // empty filter array = scan all

// Stop when done
ble.stopBLEScan();
ble.off('BLEDeviceFind');
```

Key differences:
- Bluetooth split into `@ohos.bluetooth.access` (state/power) and `@ohos.bluetooth.ble` (scanning/GATT).
- Classic Bluetooth connections use `@ohos.bluetooth.connection`.
- Register `'BLEDeviceFind'` listener before calling `startBLEScan()`.

---

## RULE 9: Error Handling Pattern

All OH networking APIs return Promises. Wrap calls in try/catch:

```typescript
import http from '@ohos.net.http';

async function fetchData(url: string): Promise<string | null> {
  let httpRequest = http.createHttp();
  try {
    let response = await httpRequest.request(url, {
      method: http.RequestMethod.GET,
      connectTimeout: 10000,
      readTimeout: 10000
    });
    if (response.responseCode === 200) {
      return response.result as string;
    }
    console.error('HTTP error: ' + response.responseCode);
    return null;
  } catch (err) {
    console.error('Network error: ' + JSON.stringify(err));
    return null;
  } finally {
    httpRequest.destroy();
  }
}
```

- Set `connectTimeout` and `readTimeout` in ms (no equivalent of OkHttp's builder timeouts).
- Always call `httpRequest.destroy()` in `finally` to avoid leaks.

---

## Common Pitfalls

1. **Forgetting `destroy()`**: Each `createHttp()` must be paired with `destroy()`. Leaking instances causes resource exhaustion.
2. **Missing `bind()` for sockets**: TCP/UDP sockets require `bind()` before `connect()` or `send()`, even with `0.0.0.0:0`.
3. **Cleartext HTTP blocked by default**: OH blocks non-HTTPS. To allow, add `"network"` config in `module.json5` with `"cleartextTraffic": true`.
4. **Permission at runtime**: `ACCESS_BLUETOOTH` and `GET_WIFI_INFO` require user authorization via `abilityAccessCtrl` at runtime, not just declaration.
5. **No interceptor chain**: OH has no OkHttp-style interceptors. Implement logging/auth headers in your wrapper class.
