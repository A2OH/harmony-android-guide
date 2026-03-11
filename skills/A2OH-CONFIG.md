# A2OH-CONFIG: Configuration File Conversion Rules

## 1. Quick Reference

| Android File | OpenHarmony File | Purpose |
|---|---|---|
| `AndroidManifest.xml` | `module.json5` | Module metadata, abilities, permissions |
| `build.gradle` (app) | `build-profile.json5` + `oh-package.json5` | Build config + dependencies |
| `res/values/strings.xml` | `resources/base/element/string.json` | String resources |
| `res/values/colors.xml` | `resources/base/element/color.json` | Color resources |
| `res/values/dimens.xml` | `resources/base/element/float.json` | Dimension resources |
| `res/drawable/` | `resources/base/media/` | Image assets |
| `res/mipmap/` | `resources/base/media/` | App icon |
| `res/values-zh/` | `resources/zh_CN/element/` | Localized resources |

## 2. AndroidManifest.xml → module.json5
### Element Mapping

| Android Manifest | module.json5 | Notes |
|---|---|---|
| `<application>` | `"module"` object | Top-level module descriptor |
| `android:name` (Application) | Not directly mapped | Use `AbilityStage` instead |
| `android:label` | `"module.description"` / `"abilities[].label"` | |
| `android:icon` | `"abilities[].icon"` | |
| `<activity>` | `abilities[]` with `"type": "page"` | |
| `<service>` | `abilities[]` with `"type": "service"` | |
| `<receiver>` | No manifest entry | Handle in code via `commonEventManager` |
| `<provider>` | `abilities[]` with `"type": "data"` | |
| `android:exported` | `"abilities[].exported"` | Same boolean semantics |
| `android:launchMode` | `"abilities[].launchType"` | `standard` / `singleton` / `specified` |
| `<intent-filter>` | `"abilities[].skills"` array | |
| `<action>` | `skills[].actions` | |
| `<category>` | `skills[].entities` | |
| `<data>` | `skills[].uris` | `[{scheme, host, path}]` |
| `<uses-permission>` | `"requestPermissions"` array | Objects with `name`, `reason`, `usedScene` |
| MAIN + LAUNCHER | `"ohos.want.action.home"` + `"entity.system.home"` | Marks the entry ability |

### RULES — apply when converting AndroidManifest.xml:

- R1: Create one `module.json5` per Android module. Set `"type"` to `"entry"` for the app module or `"feature"` for library modules.
- R2: Map each `<activity>` to an entry in the `"abilities"` array with `"type": "page"`.
- R3: Map each `<service>` to an entry in `"abilities"` with `"type": "service"`. The implementation class extends `ServiceExtensionAbility`.
- R4: Do NOT add `<receiver>` entries to `module.json5`. Convert receivers to runtime subscriptions using `commonEventManager`.
- R5: Map each `<provider>` to an entry in `"abilities"` with `"type": "data"`.
- R6: Convert the MAIN/LAUNCHER intent filter to a skill with action `"ohos.want.action.home"` and entity `"entity.system.home"`.
- R7: Map `android:launchMode="singleTask"` or `"singleInstance"` to `"launchType": "singleton"`. Map `"standard"` to `"standard"`.
- R8: Convert each `<uses-permission>` to a `requestPermissions` entry with `name` (prefixed `ohos.permission.`), `reason`, and `usedScene` fields.

### Complete Example

**Before — AndroidManifest.xml:**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:name=".MyApplication"
        android:label="@string/app_name"
        android:icon="@mipmap/ic_launcher">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".DetailActivity"
            android:exported="false" />

        <service
            android:name=".SyncService"
            android:exported="false" />

        <receiver android:name=".BootReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

**After — module.json5:**
```json5
{
  "module": {
    "name": "entry",
    "type": "entry",
    "description": "$string:app_name",
    "mainElement": "MainAbility",
    "deviceTypes": ["default"],
    "deliveryWithInstall": true,
    "installationFree": false,
    "pages": "$profile:main_pages",
    "abilities": [
      {
        "name": "MainAbility",
        "srcEntry": "./ets/entryability/MainAbility.ets",
        "description": "$string:MainAbility_desc",
        "icon": "$media:ic_launcher",
        "label": "$string:app_name",
        "startWindowIcon": "$media:startIcon",
        "startWindowBackground": "$color:start_window_background",
        "exported": true,
        "launchType": "singleton",
        "skills": [
          {
            "actions": ["ohos.want.action.home"],
            "entities": ["entity.system.home"]
          }
        ]
      },
      {
        "name": "DetailAbility",
        "srcEntry": "./ets/detailability/DetailAbility.ets",
        "description": "$string:DetailAbility_desc",
        "icon": "$media:icon",
        "label": "$string:DetailAbility_label",
        "exported": false,
        "launchType": "standard"
      },
      {
        "name": "SyncServiceAbility",
        "srcEntry": "./ets/syncservice/SyncServiceAbility.ets",
        "type": "service",
        "description": "$string:SyncService_desc",
        "exported": false
      }
    ],
    "requestPermissions": [
      {
        "name": "ohos.permission.INTERNET"
      },
      {
        "name": "ohos.permission.CAMERA",
        "reason": "$string:camera_reason",
        "usedScene": {
          "abilities": ["MainAbility"],
          "when": "inuse"
        }
      }
    ]
  }
}
```

Note: `BootReceiver` has no manifest entry. Convert to `commonEventManager` subscription (see A2OH-LIFECYCLE).

## 3. Intent Filters → Skills (Deep Links)

### RULES:
- R9: Each `<intent-filter>` becomes one object in the `"skills"` array.
- R10: Map `<data android:scheme/host/path>` to `"uris": [{ "scheme", "host", "path" }]`.
- R11: Custom actions become custom skill actions. Standard Android actions map to OH equivalents.

**Before — intent filter with deep link:**
```xml
<activity android:name=".LinkActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="https" android:host="example.com" android:path="/item" />
    </intent-filter>
</activity>
```

**After — skills with URI:**
```json5
{
  "name": "LinkAbility",
  "srcEntry": "./ets/linkability/LinkAbility.ets",
  "exported": true,
  "skills": [
    {
      "actions": ["ohos.want.action.viewData"],
      "entities": ["entity.system.default"],
      "uris": [
        {
          "scheme": "https",
          "host": "example.com",
          "path": "/item"
        }
      ]
    }
  ]
}
```

## 4. build.gradle → build-profile.json5 + oh-package.json5

### RULES:

- R12: Map `compileSdkVersion` to `build-profile.json5` `"compileSdkVersion"`.
- R13: Map `minSdkVersion` to `build-profile.json5` `"compatibleSdkVersion"`.
- R14: Map `dependencies { implementation ... }` to `oh-package.json5` `"dependencies"`.
- R15: Map `buildTypes` / `productFlavors` to `build-profile.json5` `"targets"` array.

**Before — build.gradle:**
```groovy
android {
    compileSdkVersion 33
    defaultConfig {
        applicationId "com.example.myapp"
        minSdkVersion 24
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
    }
}
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.google.code.gson:gson:2.10'
}
```

**After — build-profile.json5:**
```json5
{
  "app": {
    "signingConfigs": [],
    "products": [{
      "name": "default", "signingConfig": "default",
      "compatibleSdkVersion": "4.0.0(10)", "compileSdkVersion": "4.0.0(10)",
      "runtimeOS": "OpenHarmony"
    }],
    "buildModeSet": [{ "name": "debug" }, { "name": "release" }]
  },
  "modules": [{
    "name": "entry", "srcPath": "./entry",
    "targets": [{ "name": "default", "applyToProducts": ["default"] }]
  }]
}
```

**After — oh-package.json5:**
```json5
{
  "name": "myapp", "version": "1.0.0",
  "description": "MyApp for OpenHarmony",
  "license": "Apache-2.0",
  "dependencies": {
    // retrofit → use @ohos/net.http; gson → JSON is native in ArkTS
  }
}
```

## 5. Resource System: res/ → resources/

### RULES:

- R16: Convert `res/values/strings.xml` to `resources/base/element/string.json`. Each `<string name="x">value</string>` becomes `{ "name": "x", "value": "value" }` inside a `"string"` array.
- R17: Convert `res/values/colors.xml` to `resources/base/element/color.json`. Use hex format `#RRGGBB` or `#AARRGGBB`.
- R18: Convert `res/values/dimens.xml` to `resources/base/element/float.json`. Store numeric values as strings with units (e.g., `"16fp"`).
- R19: Copy `res/drawable/` and `res/mipmap/` files to `resources/base/media/`.
- R20: Convert localized resource dirs: `res/values-XX/` → `resources/XX_YY/element/`. Example: `values-zh/` → `zh_CN/element/`.
- R21: Replace resource references: `R.string.xxx` → `$r('app.string.xxx')`, `R.drawable.xxx` → `$r('app.media.xxx')`, `R.color.xxx` → `$r('app.color.xxx')`.

**Before — res/values/strings.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">MyApp</string>
    <string name="welcome_msg">Welcome to MyApp</string>
    <string name="btn_submit">Submit</string>
</resources>
```

**After — resources/base/element/string.json:**
```json
{
  "string": [
    { "name": "app_name", "value": "MyApp" },
    { "name": "welcome_msg", "value": "Welcome to MyApp" },
    { "name": "btn_submit", "value": "Submit" }
  ]
}
```

**Before — res/values/colors.xml → After — resources/base/element/color.json:**
```json
{ "color": [{ "name": "primary", "value": "#6200EE" }, { "name": "background", "value": "#FFFFFF" }] }
```

**Resource access in code:**
```
Android (Java/Kotlin)                 OpenHarmony (ArkTS)
─────────────────────                 ─────────────────────
getString(R.string.app_name)      →   $r('app.string.app_name')
getDrawable(R.drawable.icon)      →   $r('app.media.icon')
getColor(R.color.primary)         →   $r('app.color.primary')
R.dimen.text_size                 →   $r('app.float.text_size')
```
