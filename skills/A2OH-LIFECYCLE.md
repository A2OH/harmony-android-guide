# A2OH-LIFECYCLE: App Lifecycle Conversion Rules

## 1. Quick Reference

| Android | OpenHarmony (Stage Model) | Import / Module |
|---------|--------------------------|-----------------|
| `Activity` / `AppCompatActivity` | `UIAbility` | `import UIAbility from '@ohos.app.ability.UIAbility'` |
| `Service` / `IntentService` | `ServiceExtensionAbility` | `import ServiceExtensionAbility from '@ohos.app.ability.ServiceExtensionAbility'` |
| `BroadcastReceiver` | `commonEventManager` subscriber | `import commonEventManager from '@ohos.commonEventManager'` |
| `ContentProvider` | `DataShareExtensionAbility` | `import DataShareExtensionAbility from '@ohos.application.DataShareExtensionAbility'` |
| `Intent` | `Want` | `import Want from '@ohos.app.ability.Want'` |
| `AndroidManifest.xml` | `module.json5` (`abilities` array) | — |
| `Application` | `AbilityStage` | `import AbilityStage from '@ohos.app.ability.AbilityStage'` |

## 2. Activity → UIAbility

### Lifecycle Mapping

```
Android Activity          UIAbility (Stage Model)
─────────────────         ──────────────────────────
                          onCreate(want, launchParam)
                          onWindowStageCreate(windowStage)
onCreate(savedState)  →     windowStage.loadContent('pages/XxxPage')
onStart()             →   (absorbed into onWindowStageCreate)
onResume()            →   onForeground()
onPause()             →   onBackground()
onStop()              →   (absorbed into onBackground)
                          onWindowStageDestroy()
onDestroy()           →   onDestroy()
onNewIntent(intent)   →   onNewWant(want, launchParam)
onSaveInstanceState() →   (use AppStorage / PersistentStorage)
```

### RULES — apply when converting an Activity subclass:

- R1: Replace `extends Activity/AppCompatActivity` with `extends UIAbility`.
- R2: Move `onCreate` body into `onWindowStageCreate`. The UIAbility `onCreate` only initialises non-UI state.
- R3: `setContentView(R.layout.xxx)` → `windowStage.loadContent('pages/XxxPage')`. The layout XML becomes a separate ArkUI `@Entry @Component` page file.
- R4: `onResume` → `onForeground`. `onPause`/`onStop` → `onBackground`.
- R5: `onNewIntent(intent)` → `onNewWant(want, launchParam)`.
- R6: `savedInstanceState` / `onSaveInstanceState` → use `PersistentStorage` or `AppStorage`.
- R7: `getIntent()` → `this.launchWant` (available after `onCreate`).
- R8: `finish()` → `this.context.terminateSelf()`.
- R9: Activity must be registered in `module.json5` under `"abilities"`, not `AndroidManifest.xml`.

### Before (Android Java)

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userId = getIntent().getStringExtra("userId");
        initData(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void goToDetail(String id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }
}
```

### After (OpenHarmony ArkTS)

```typescript
// entry/src/main/ets/entryability/MainAbility.ets
import UIAbility from '@ohos.app.ability.UIAbility';
import AbilityConstant from '@ohos.app.ability.AbilityConstant';
import Want from '@ohos.app.ability.Want';
import window from '@ohos.window';

export default class MainAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    const userId = want.parameters?.['userId'] as string;
    this.initData(userId);
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    windowStage.loadContent('pages/MainPage');
  }

  onForeground(): void {
    // was onResume
    this.refreshUI();
  }

  onDestroy(): void {
    this.cleanup();
  }

  private goToDetail(id: string): void {
    const want: Want = {
      bundleName: 'com.example.myapp',
      abilityName: 'DetailAbility',
      parameters: { itemId: id }
    };
    this.context.startAbility(want);
  }

  private initData(userId: string): void { /* ... */ }
  private refreshUI(): void { /* ... */ }
  private cleanup(): void { /* ... */ }
}
```

```json5
// entry/src/main/module.json5 (partial)
{
  "module": {
    "abilities": [
      {
        "name": "MainAbility",
        "srcEntry": "./ets/entryability/MainAbility.ets",
        "launchType": "singleton",
        "exported": true,
        "skills": [
          { "entities": ["entity.system.home"], "actions": ["action.system.home"] }
        ]
      },
      {
        "name": "DetailAbility",
        "srcEntry": "./ets/entryability/DetailAbility.ets",
        "launchType": "standard"
      }
    ]
  }
}
```

## 3. Service → ServiceExtensionAbility

### RULES:

- R10: `extends Service` → `extends ServiceExtensionAbility`.
- R11: `onStartCommand(intent, flags, startId)` → `onRequest(want, startId)`.
- R12: `onBind(intent)` → `onConnect(want)` which must return an `rpc.RemoteObject`.
- R13: `onUnbind(intent)` → `onDisconnect(want)`.
- R14: `stopSelf()` → `this.context.terminateSelf()`.
- R15: `IntentService` has no direct equivalent; use `onRequest` + `TaskPool` for background work.
- R16: `startService(intent)` → `this.context.startServiceExtensionAbility(want)`.
- R17: `bindService(intent, conn, flags)` → `this.context.connectServiceExtensionAbility(want, options)`.
- R18: ServiceExtensionAbility requires system permission (`system_basic` or `system_core` app) for third-party apps; for normal apps, use background tasks via `@ohos.backgroundTaskManager`.

### Before (Android)

```java
public class SyncService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("syncUrl");
        doSync(url);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() { cleanup(); }
}
```

### After (OpenHarmony)

```typescript
import ServiceExtensionAbility from '@ohos.app.ability.ServiceExtensionAbility';
import Want from '@ohos.app.ability.Want';

export default class SyncServiceAbility extends ServiceExtensionAbility {
  onRequest(want: Want, startId: number): void {
    const url = want.parameters?.['syncUrl'] as string;
    this.doSync(url);
  }

  onConnect(want: Want): rpc.RemoteObject | null {
    return null;
  }

  onDisconnect(want: Want): void {}

  onDestroy(): void {
    this.cleanup();
  }

  private doSync(url: string): void { /* ... */ }
  private cleanup(): void { /* ... */ }
}
```

## 4. BroadcastReceiver → CommonEventSubscriber

### RULES:

- R19: Static `<receiver>` in manifest → `commonEventManager.createSubscriber` + `subscribe` at ability startup.
- R20: `sendBroadcast(intent)` → `commonEventManager.publish(event, options)`.
- R21: `onReceive(context, intent)` → subscriber callback `(err, data) => {}`.
- R22: `intent.getAction()` → `data.event`. `intent.getExtras()` → `data.parameters`.
- R23: `LocalBroadcastManager` → `emitter` module (`import emitter from '@ohos.events.emitter'`) for in-process events.
- R24: Always call `commonEventManager.unsubscribe(subscriber)` during teardown.

### Before (Android)

```java
// Registration in Activity
IntentFilter filter = new IntentFilter("com.example.DATA_UPDATED");
registerReceiver(myReceiver, filter);

// Receiver
public class DataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String payload = intent.getStringExtra("payload");
        handleUpdate(payload);
    }
}

// Sending
Intent intent = new Intent("com.example.DATA_UPDATED");
intent.putExtra("payload", jsonStr);
sendBroadcast(intent);
```

### After (OpenHarmony)

```typescript
import commonEventManager from '@ohos.commonEventManager';

const EVENT_DATA_UPDATED = 'com.example.DATA_UPDATED';

// Subscribe (in onWindowStageCreate or onForeground)
let subscriber: commonEventManager.CommonEventSubscriber;

const subscribeInfo: commonEventManager.CommonEventSubscribeInfo = {
  events: [EVENT_DATA_UPDATED]
};

commonEventManager.createSubscriber(subscribeInfo, (err, sub) => {
  if (err) { return; }
  subscriber = sub;
  commonEventManager.subscribe(subscriber, (err, data) => {
    const payload = data.parameters?.['payload'] as string;
    handleUpdate(payload);
  });
});

// Publish (replaces sendBroadcast)
const options: commonEventManager.CommonEventPublishData = {
  parameters: { payload: jsonStr }
};
commonEventManager.publish(EVENT_DATA_UPDATED, options);

// Unsubscribe (in onBackground or onDestroy)
commonEventManager.unsubscribe(subscriber);
```

### In-Process Events (replaces LocalBroadcastManager)

```typescript
import emitter from '@ohos.events.emitter';

// Define event
const EVENT_ID = { eventId: 1001 };

// Subscribe
emitter.on(EVENT_ID, (data) => {
  const payload = data?.data?.['payload'] as string;
});

// Emit
emitter.emit(EVENT_ID, { data: { payload: jsonStr } });

// Unsubscribe
emitter.off(EVENT_ID.eventId);
```

## 5. ContentProvider → DataShareExtensionAbility

### RULES:

- R25: `extends ContentProvider` → `extends DataShareExtensionAbility`.
- R26: `onCreate()` → `onCreate(want, callback)`.
- R27: `query(uri, projection, selection, selectionArgs, sortOrder)` → `query(uri, predicates, columns, callback)`.
- R28: `insert(uri, values)` → `insert(uri, valueBucket, callback)`.
- R29: `update(uri, values, selection, selectionArgs)` → `update(uri, predicates, valueBucket, callback)`.
- R30: `delete(uri, selection, selectionArgs)` → `delete(uri, predicates, callback)`.
- R31: `ContentValues` → `dataSharePredicates.DataSharePredicates` for conditions, `ValuesBucket` for data.
- R32: Content URIs change format: `content://authority/table` → `datashare:///com.example.myapp/table`.
- R33: Clients use `dataShare.createDataShareHelper(context, uri)` instead of `ContentResolver`.

### Before (Android)

```java
public class NoteProvider extends ContentProvider {
    @Override
    public boolean onCreate() { return initDb(); }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] args, String sort) {
        return db.query("notes", proj, sel, args, null, null, sort);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert("notes", null, values);
        return ContentUris.withAppendedId(uri, id);
    }
}
```

### After (OpenHarmony)

```typescript
import DataShareExtensionAbility from '@ohos.application.DataShareExtensionAbility';
import relationalStore from '@ohos.data.relationalStore';
import dataSharePredicates from '@ohos.data.dataSharePredicates';

export default class NoteDataShare extends DataShareExtensionAbility {
  private rdbStore: relationalStore.RdbStore | null = null;

  onCreate(want, callback): void {
    // init rdbStore here
    callback();
  }

  query(uri: string, predicates: dataSharePredicates.DataSharePredicates,
        columns: Array<string>, callback): void {
    const rdbPredicates = relationalStore.RdbPredicates('notes');
    // apply predicates
    this.rdbStore?.query(rdbPredicates, columns).then((resultSet) => {
      callback(null, resultSet);
    });
  }

  insert(uri: string, valueBucket: relationalStore.ValuesBucket, callback): void {
    this.rdbStore?.insert('notes', valueBucket).then((rowId) => {
      callback(null, rowId);
    });
  }

  update(uri: string, predicates: dataSharePredicates.DataSharePredicates,
         valueBucket: relationalStore.ValuesBucket, callback): void {
    const rdbPredicates = relationalStore.RdbPredicates('notes');
    this.rdbStore?.update(valueBucket, rdbPredicates).then((rows) => {
      callback(null, rows);
    });
  }

  delete(uri: string, predicates: dataSharePredicates.DataSharePredicates,
         callback): void {
    const rdbPredicates = relationalStore.RdbPredicates('notes');
    this.rdbStore?.delete(rdbPredicates).then((rows) => {
      callback(null, rows);
    });
  }
}
```

## 6. Intent → Want Mapping

### Structure

```
Android Intent                  OpenHarmony Want
──────────────                  ────────────────
component (package/class)   →   bundleName + abilityName
action                      →   action
categories                  →   entities
data (Uri)                  →   uri
extras (Bundle)             →   parameters (Record<string, Object>)
flags                       →   flags
type (MIME)                 →   type
```

### Common Operations

| Android | OpenHarmony |
|---------|-------------|
| `new Intent(this, Target.class)` | `{ bundleName: 'com.x.y', abilityName: 'Target' }` |
| `intent.putExtra("k", v)` | `want.parameters = { k: v }` |
| `intent.getStringExtra("k")` | `want.parameters?.['k'] as string` |
| `intent.getIntExtra("k", 0)` | `want.parameters?.['k'] as number` |
| `intent.getBooleanExtra("k", false)` | `want.parameters?.['k'] as boolean` |
| `startActivity(intent)` | `context.startAbility(want)` |
| `startActivityForResult(intent, code)` | `context.startAbilityForResult(want): Promise<AbilityResult>` |
| `setResult(RESULT_OK, data)` + `finish()` | `context.terminateSelfWithResult({ resultCode: 0, want: resultWant })` |
| `onActivityResult(req, res, data)` | `.then((result: AbilityResult) => { ... })` |
| `finish()` | `context.terminateSelf()` |
| `Intent.ACTION_VIEW` with Uri | `{ action: 'ohos.want.action.viewData', uri: '...' }` |
| `Intent.ACTION_SEND` | `{ action: 'ohos.want.action.sendData', ... }` |

### startActivityForResult → startAbilityForResult

**Before:**
```java
Intent intent = new Intent(this, PickerActivity.class);
startActivityForResult(intent, REQUEST_PICK);

@Override
protected void onActivityResult(int req, int res, Intent data) {
    if (req == REQUEST_PICK && res == RESULT_OK) {
        String result = data.getStringExtra("selected");
    }
}
```

**After:**
```typescript
const want: Want = {
  bundleName: 'com.example.myapp',
  abilityName: 'PickerAbility'
};
const result = await this.context.startAbilityForResult(want);
if (result.resultCode === 0) {
  const selected = result.want?.parameters?.['selected'] as string;
}

// In PickerAbility, to return a result:
const resultWant: Want = { parameters: { selected: chosenItem } };
this.context.terminateSelfWithResult({
  resultCode: 0,
  want: resultWant
});
```

## 7. Registration: AndroidManifest.xml → module.json5

### RULES:

- R34: Each `<activity>` → entry in `"abilities"` array with `"type": "page"` (default, can omit).
- R35: Each `<service>` → entry in `"extensionAbilities"` with `"type": "service"`.
- R36: Each `<provider>` → entry in `"extensionAbilities"` with `"type": "dataShare"`.
- R37: `<intent-filter>` with `MAIN`+`LAUNCHER` → `"exported": true` + `"skills": [{"entities":["entity.system.home"],"actions":["action.system.home"]}]`.
- R38: `android:launchMode="singleTask"` → `"launchType": "singleton"`. Default `"standard"` maps to `"standard"`.
- R39: `<uses-permission>` → `"requestPermissions"` array in `module.json5`.

### Before (AndroidManifest.xml excerpt)

```xml
<activity android:name=".MainActivity" android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>
<service android:name=".SyncService" android:exported="false"/>
<provider android:name=".NoteProvider"
    android:authorities="com.example.notes"
    android:exported="true"/>
```

### After (module.json5 excerpt)

```json5
{
  "module": {
    "abilities": [
      {
        "name": "MainAbility",
        "srcEntry": "./ets/entryability/MainAbility.ets",
        "launchType": "singleton",
        "exported": true,
        "skills": [
          { "entities": ["entity.system.home"], "actions": ["action.system.home"] }
        ]
      }
    ],
    "extensionAbilities": [
      {
        "name": "SyncServiceAbility",
        "srcEntry": "./ets/serviceability/SyncServiceAbility.ets",
        "type": "service",
        "exported": false
      },
      {
        "name": "NoteDataShare",
        "srcEntry": "./ets/datashare/NoteDataShare.ets",
        "type": "dataShare",
        "uri": "datashare:///com.example.notes",
        "exported": true
      }
    ],
    "requestPermissions": []
  }
}
```
