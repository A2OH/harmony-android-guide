# A2OH-DEVICE-API: Device & System API Conversion Rules

## 1. Paradigm Shift — No getSystemService()

Android accesses device APIs through `Context.getSystemService(Class)`. OpenHarmony does NOT have this pattern. Instead, import the module directly and call static-style functions.

```
Android                                    OpenHarmony
──────                                     ───────────
getSystemService(TelephonyManager.class)   import call from '@ohos.telephony.call'
getSystemService(SensorManager.class)      import sensor from '@ohos.sensor'
getSystemService(LocationManager.class)    import geoLocationManager from '@ohos.geoLocationManager'
getSystemService(NotificationManager...)   import notificationManager from '@ohos.notificationManager'
getSystemService(Vibrator.class)           import vibrator from '@ohos.vibrator'
getSystemService(PowerManager.class)       import power from '@ohos.power'
NfcAdapter.getDefaultAdapter(context)      import controller from '@ohos.nfc.controller'
```

### RULE R0: Every `getSystemService(Xxx.class)` call must be replaced by a direct module import. Delete the variable that held the system service; call the module's functions directly.

## 2. Permission Model

### Quick Reference

| Android | OpenHarmony |
|---------|-------------|
| `ActivityCompat.requestPermissions(activity, perms, code)` | `atManager.requestPermissionsFromUser(context, perms)` |
| `ContextCompat.checkSelfPermission(ctx, perm)` | `atManager.checkAccessToken(tokenId, perm)` |
| `onRequestPermissionsResult(code, perms, results)` | Promise/callback from `requestPermissionsFromUser` |
| `shouldShowRequestPermissionRationale()` | No direct equivalent; handle in UI before requesting |
| `<uses-permission>` in AndroidManifest.xml | `"requestPermissions"` in `module.json5` |

### Permission Name Mapping

| Android Permission | OpenHarmony Permission |
|---------------------|------------------------|
| `ACCESS_FINE_LOCATION` | `ohos.permission.APPROXIMATELY_LOCATION` + `ohos.permission.LOCATION` |
| `ACCESS_COARSE_LOCATION` | `ohos.permission.APPROXIMATELY_LOCATION` |
| `CAMERA` | `ohos.permission.CAMERA` |
| `RECORD_AUDIO` | `ohos.permission.MICROPHONE` |
| `READ_CONTACTS` | `ohos.permission.READ_CONTACTS` |
| `CALL_PHONE` | `ohos.permission.PLACE_CALL` |
| `SEND_SMS` | `ohos.permission.SEND_MESSAGES` |
| `READ_CALENDAR` | `ohos.permission.READ_CALENDAR` |
| `WRITE_CALENDAR` | `ohos.permission.WRITE_CALENDAR` |
| `BODY_SENSORS` | `ohos.permission.ACTIVITY_MOTION` |

### RULES — Permissions:

- R1: Replace `ActivityCompat.requestPermissions` with `abilityAccessCtrl.createAtManager().requestPermissionsFromUser(context, permList)`.
- R2: Replace `ContextCompat.checkSelfPermission` with `atManager.checkAccessToken(tokenId, permission)`. Obtain `tokenId` from `bundleManager.getBundleInfoForSelf`.
- R3: Remove `onRequestPermissionsResult` callback. Use the Promise returned by `requestPermissionsFromUser` instead.
- R4: Map every Android permission string to its OH equivalent using the table above. Note: fine location requires BOTH `APPROXIMATELY_LOCATION` and `LOCATION`.
- R5: Declare permissions in `module.json5` under `"requestPermissions"`, not `AndroidManifest.xml`.

### Before (Android Java)

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
}
```

### After (OpenHarmony ArkTS)

```typescript
import abilityAccessCtrl from '@ohos.abilityAccessCtrl';
import bundleManager from '@ohos.bundle.bundleManager';

let atManager = abilityAccessCtrl.createAtManager();
let bundleInfo = await bundleManager.getBundleInfoForSelf(
  bundleManager.BundleFlag.GET_BUNDLE_INFO_WITH_APPLICATION
);
let tokenId = bundleInfo.appInfo.accessTokenId;
let result = atManager.checkAccessToken(tokenId, 'ohos.permission.LOCATION');
if (result !== abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED) {
  let permResult = await atManager.requestPermissionsFromUser(this.context,
    ['ohos.permission.APPROXIMATELY_LOCATION', 'ohos.permission.LOCATION']);
}
```

## 3. TelephonyManager → @ohos.telephony.*

| Android | OpenHarmony | Import |
|---------|-------------|--------|
| `TelephonyManager.getNetworkType()` | `radio.getNetworkState()` | `import radio from '@ohos.telephony.radio'` |
| `TelephonyManager.getSignalStrength()` | `radio.getSignalInformation(slotId)` | same |
| `Intent.ACTION_CALL` / `TelecomManager` | `call.makeCall(phoneNumber)` | `import call from '@ohos.telephony.call'` |
| `TelephonyManager.isNetworkRoaming()` | `radio.getNetworkState().isRoaming` | same |
| `SmsManager.sendTextMessage(...)` | `sms.sendMessage({...})` | `import sms from '@ohos.telephony.sms'` |

### RULES — Telephony:

- R6: Replace `SmsManager.getDefault().sendTextMessage(dest, null, body, sentPI, deliverPI)` with `sms.sendMessage({ slotId: 0, destinationHost: dest, content: body })`.
- R7: Replace `TelephonyManager` signal/network queries with `radio.getNetworkState()` or `radio.getSignalInformation(slotId)`.
- R8: Replace `Intent(Intent.ACTION_CALL, Uri.parse("tel:..."))` with `call.makeCall(phoneNumber)`.
- R9: Use `call.hasCall()` to check if a call is active, replacing `TelephonyManager.getCallState()`.

### Before / After

```java
// Android
TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
SmsManager.getDefault().sendTextMessage("+123", null, "Hello", null, null);
```
```typescript
// OpenHarmony
import radio from '@ohos.telephony.radio';
import sms from '@ohos.telephony.sms';
let state = await radio.getNetworkState();
sms.sendMessage({ slotId: 0, destinationHost: '+123', content: 'Hello' });
```

## 4. SensorManager → @ohos.sensor

| Android | OpenHarmony |
|---------|-------------|
| `SensorManager.registerListener(listener, sensor, delay)` | `sensor.on(sensor.SensorId.XXX, callback)` |
| `SensorManager.unregisterListener(listener)` | `sensor.off(sensor.SensorId.XXX)` |
| `Sensor.TYPE_ACCELEROMETER` | `sensor.SensorId.ACCELEROMETER` |
| `Sensor.TYPE_GYROSCOPE` | `sensor.SensorId.GYROSCOPE` |
| `Sensor.TYPE_LIGHT` | `sensor.SensorId.AMBIENT_LIGHT` |
| `Sensor.TYPE_MAGNETIC_FIELD` | `sensor.SensorId.MAGNETIC_FIELD` |
| `Sensor.TYPE_PRESSURE` | `sensor.SensorId.BAROMETER` |
| `Sensor.TYPE_PROXIMITY` | `sensor.SensorId.PROXIMITY` |

### RULES — Sensors:

- R10: Replace `sensorManager.registerListener(this, sensor, DELAY)` with `sensor.on(sensor.SensorId.XXX, callback)`. The callback receives data directly (e.g., `data.x`, `data.y`, `data.z`).
- R11: Replace `sensorManager.unregisterListener(this)` with `sensor.off(sensor.SensorId.XXX)`.
- R12: If the Activity implements `SensorEventListener`, extract `onSensorChanged` body into the `sensor.on` callback. Remove the interface.
- R13: `event.values[0]` / `event.values[1]` / `event.values[2]` → `data.x` / `data.y` / `data.z`.

### Before / After

```java
// Android
SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
sm.registerListener(this, sm.getDefaultSensor(TYPE_ACCELEROMETER), SENSOR_DELAY_NORMAL);
// onSensorChanged: float x = event.values[0];
```
```typescript
// OpenHarmony
import sensor from '@ohos.sensor';
sensor.on(sensor.SensorId.ACCELEROMETER, (data) => { let x = data.x; });
sensor.off(sensor.SensorId.ACCELEROMETER); // cleanup
```

## 5. LocationManager → @ohos.geoLocationManager

| Android | OpenHarmony |
|---------|-------------|
| `LocationManager.requestLocationUpdates(provider, minTime, minDist, listener)` | `geoLocationManager.on('locationChange', requestInfo, callback)` |
| `LocationManager.getLastKnownLocation(provider)` | `geoLocationManager.getCurrentLocation(requestInfo)` |
| `LocationManager.removeUpdates(listener)` | `geoLocationManager.off('locationChange', callback)` |
| `LocationListener.onLocationChanged(location)` | callback param in `on('locationChange', ...)` |

### RULES — Location:

- R14: Replace `requestLocationUpdates` with `geoLocationManager.on('locationChange', requestInfo, callback)`.
- R15: Replace `getLastKnownLocation` with `geoLocationManager.getCurrentLocation(requestInfo)` (returns a Promise).
- R16: Replace `removeUpdates(listener)` with `geoLocationManager.off('locationChange', callback)`.
- R17: The `requestInfo` object uses `{ priority, scenario, timeInterval, distanceInterval }` instead of Android's provider/minTime/minDist params.
- R18: `location.getLatitude()` / `location.getLongitude()` → `location.latitude` / `location.longitude` (direct properties).

### Before / After

```java
// Android
LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
lm.requestLocationUpdates(GPS_PROVIDER, 5000, 10, loc -> {
    double lat = loc.getLatitude(); double lng = loc.getLongitude();
});
```
```typescript
// OpenHarmony
import geoLocationManager from '@ohos.geoLocationManager';
let cb = (loc: geoLocationManager.Location) => { let lat = loc.latitude; };
geoLocationManager.on('locationChange',
  { priority: geoLocationManager.LocationRequestPriority.FIRST_FIX,
    scenario: geoLocationManager.LocationRequestScenario.NAVIGATION,
    timeInterval: 5, distanceInterval: 10 }, cb);
geoLocationManager.off('locationChange', cb); // cleanup
```

## 6. NfcAdapter → @ohos.nfc.*

| Android | OpenHarmony | Import |
|---------|-------------|--------|
| `NfcAdapter.getDefaultAdapter(context)` | `controller` module | `import controller from '@ohos.nfc.controller'` |
| `NfcAdapter.isEnabled()` | `controller.isNfcAvailable()` | same |
| `NfcAdapter.enable()` | `controller.openNfc()` | same |
| `Tag` / `NdefMessage` handling | `tag` module | `import tag from '@ohos.nfc.tag'` |

### RULES — NFC:

- R19: Replace `NfcAdapter.getDefaultAdapter(context)` with `import controller from '@ohos.nfc.controller'`. No adapter object needed.
- R20: Replace `nfcAdapter.isEnabled()` with `controller.isNfcAvailable()`.
- R21: For tag reading/writing, use `import tag from '@ohos.nfc.tag'`.

## 7. NotificationManager → @ohos.notificationManager

### RULES — Notifications:

- R22: Replace `NotificationCompat.Builder(context, channelId)` chain with a `NotificationRequest` object.
- R23: Replace `notificationManager.notify(id, notification)` with `notificationManager.publish(request)`.
- R24: Replace `notificationManager.cancel(id)` with `notificationManager.cancel(id)` (same name).
- R25: Notification channels do not exist in OH. Use `slotType` on the request instead (e.g., `SOCIAL_COMMUNICATION`, `SERVICE_INFORMATION`, `CONTENT_INFORMATION`).

### Before / After

```java
// Android
NotificationCompat.Builder b = new NotificationCompat.Builder(this, "ch1")
    .setContentTitle("Msg").setContentText("New message").setSmallIcon(R.drawable.ic);
((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, b.build());
```
```typescript
// OpenHarmony
import notificationManager from '@ohos.notificationManager';
notificationManager.publish({ id: 1,
  content: { contentType: notificationManager.ContentType.NOTIFICATION_CONTENT_BASIC_TEXT,
    normal: { title: 'Msg', text: 'New message' } },
  slotType: notificationManager.SlotType.SOCIAL_COMMUNICATION });
notificationManager.cancel(1);
```

## 8. Vibrator → @ohos.vibrator

### RULES — Vibrator:

- R26: Replace `Vibrator v = getSystemService(Vibrator.class); v.vibrate(duration)` with `vibrator.startVibration({ type: 'time', duration: ms })`.
- R27: Replace `v.cancel()` with `vibrator.stopVibration()`.

### Before / After

```java
// Android
Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
v.vibrate(1000);
```

```typescript
// OpenHarmony
import vibrator from '@ohos.vibrator';
vibrator.startVibration({ type: 'time', duration: 1000 });
```

## 9. PowerManager → @ohos.power

### RULES — Power:

- R28: Replace `PowerManager.isScreenOn()` / `isInteractive()` with `power.isScreenOn()`.
- R29: Replace `WakeLock` usage with `@ohos.runningLock` module. OH uses `runningLock.create()` and `lock.hold(timeout)` / `lock.unhold()`.
- R30: `PowerManager.reboot(reason)` → `power.rebootDevice(reason)`.

## 10. Comprehensive Before/After — Sensors + Location + Permissions

### Before (Android Java)

```java
public class DeviceInfoActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private LocationManager locationManager;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != GRANTED)
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 100);
        else startLocation();
    }
    @Override protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY_NORMAL);
    }
    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locListener);
    }
    @Override public void onSensorChanged(SensorEvent e) { float x = e.values[0]; }
    private void startLocation() {
        locationManager.requestLocationUpdates(GPS_PROVIDER, 5000, 10, locListener);
    }
    private LocationListener locListener = loc -> {
        double lat = loc.getLatitude(); double lng = loc.getLongitude();
    };
}
```

### After (OpenHarmony ArkTS)

```typescript
// DeviceInfoPage.ets — ArkUI page (UIAbility just calls windowStage.loadContent)
import sensor from '@ohos.sensor';
import geoLocationManager from '@ohos.geoLocationManager';
import abilityAccessCtrl from '@ohos.abilityAccessCtrl';
import bundleManager from '@ohos.bundle.bundleManager';
import common from '@ohos.app.ability.common';

@Entry @Component
struct DeviceInfoPage {
  @State accelX: number = 0;
  @State lat: number = 0;
  @State lng: number = 0;
  private context = getContext(this) as common.UIAbilityContext;
  private locCb = (loc: geoLocationManager.Location) => {
    this.lat = loc.latitude; this.lng = loc.longitude;
  };
  async aboutToAppear() {
    let atManager = abilityAccessCtrl.createAtManager();
    let info = await bundleManager.getBundleInfoForSelf(
      bundleManager.BundleFlag.GET_BUNDLE_INFO_WITH_APPLICATION);
    let granted = atManager.checkAccessToken(info.appInfo.accessTokenId, 'ohos.permission.LOCATION');
    if (granted !== abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED)
      await atManager.requestPermissionsFromUser(this.context,
        ['ohos.permission.APPROXIMATELY_LOCATION', 'ohos.permission.LOCATION']);
    sensor.on(sensor.SensorId.ACCELEROMETER, (data) => { this.accelX = data.x; });
    geoLocationManager.on('locationChange',
      { priority: geoLocationManager.LocationRequestPriority.FIRST_FIX,
        scenario: geoLocationManager.LocationRequestScenario.NAVIGATION,
        timeInterval: 5, distanceInterval: 10 }, this.locCb);
  }
  aboutToDisappear() {
    sensor.off(sensor.SensorId.ACCELEROMETER);
    geoLocationManager.off('locationChange', this.locCb);
  }
  build() {
    Column() {
      Text(`Accel: ${this.accelX}`)
      Text(`Location: ${this.lat}, ${this.lng}`)
    }
  }
}
```

### module.json5 — permission declaration

```json5
{ "module": { "requestPermissions": [
  { "name": "ohos.permission.APPROXIMATELY_LOCATION", "reason": "$string:location_reason" },
  { "name": "ohos.permission.LOCATION", "reason": "$string:location_reason" },
  { "name": "ohos.permission.ACTIVITY_MOTION", "reason": "$string:sensor_reason" }
] } }
```

## Rule Summary

| Rule | Conversion |
|------|------------|
| R0 | Delete `getSystemService()` — use direct module imports |
| R1 | `requestPermissions` → `atManager.requestPermissionsFromUser` |
| R2 | `checkSelfPermission` → `atManager.checkAccessToken` |
| R3 | Remove `onRequestPermissionsResult` — use Promise |
| R4 | Map Android permission strings to OH equivalents |
| R5 | Declare permissions in `module.json5` |
| R6 | `SmsManager.sendTextMessage` → `sms.sendMessage({...})` |
| R7 | `TelephonyManager` queries → `radio.getNetworkState()` |
| R8 | `ACTION_CALL` intent → `call.makeCall(number)` |
| R9 | `getCallState()` → `call.hasCall()` |
| R10 | `registerListener` → `sensor.on(SensorId, callback)` |
| R11 | `unregisterListener` → `sensor.off(SensorId)` |
| R12 | Extract `SensorEventListener` into `sensor.on` callback |
| R13 | `event.values[n]` → `data.x / data.y / data.z` |
| R14 | `requestLocationUpdates` → `geoLocationManager.on('locationChange', ...)` |
| R15 | `getLastKnownLocation` → `geoLocationManager.getCurrentLocation()` |
| R16 | `removeUpdates` → `geoLocationManager.off('locationChange', ...)` |
| R17 | Provider/minTime/minDist → `requestInfo` object |
| R18 | `loc.getLatitude()` → `location.latitude` |
| R19 | `NfcAdapter.getDefaultAdapter` → import `controller` module |
| R20 | `nfcAdapter.isEnabled()` → `controller.isNfcAvailable()` |
| R21 | NFC tag ops → `import tag from '@ohos.nfc.tag'` |
| R22 | `NotificationCompat.Builder` chain → `NotificationRequest` object |
| R23 | `nm.notify(id, notif)` → `notificationManager.publish(request)` |
| R24 | `nm.cancel(id)` → `notificationManager.cancel(id)` |
| R25 | Notification channels → `slotType` on request |
| R26 | `vibrator.vibrate(ms)` → `vibrator.startVibration({type:'time', duration})` |
| R27 | `vibrator.cancel()` → `vibrator.stopVibration()` |
| R28 | `PowerManager.isScreenOn()` → `power.isScreenOn()` |
| R29 | `WakeLock` → `runningLock.create()` + `hold/unhold` |
| R30 | `PowerManager.reboot()` → `power.rebootDevice(reason)` |
