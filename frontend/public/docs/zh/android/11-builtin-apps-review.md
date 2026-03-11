# Android 11 AOSP 代码审查: Built-in Apps and SystemUI

## 1. Executive Summary

This report reviews the architecture, patterns, and API usage of AOSP Android 11's built-in applications and SystemUI. These apps serve as reference implementations demonstrating how Google engineers use the Android framework APIs. The review covers SystemUI (status bar, notifications, quick settings, lock screen), the Settings app, Launcher3, and select other built-in apps. Key architectural patterns include Dagger dependency injection in SystemUI, the PreferenceController pattern in Settings, and the state machine architecture in Launcher3.

---

## 2. SystemUI Architecture

**Source:** `frameworks/base/packages/SystemUI/`

SystemUI is the most architecturally sophisticated AOSP app. It manages the status bar, notification shade, quick settings panel, lock screen, volume controls, screen recording, bubbles, device controls, and more.

### 2.1 Application Bootstrap

**Key file:** `SystemUIApplication.java`

SystemUI runs as a persistent system process with `android.uid.systemui` shared UID. The application bootstraps through:

1. `SystemUIAppComponentFactory` initializes the Dagger dependency graph before `Application.onCreate()`
2. `SystemUIApplication.onCreate()` obtains `SystemUIRootComponent` from `SystemUIFactory`
3. `startServicesIfNeeded()` loads service components from a resource array, instantiates them via reflection or Dagger, and calls `start()` on each

```
SystemUIAppComponentFactory -> SystemUIApplication.onCreate()
    -> SystemUIFactory.getInstance().getRootComponent()
    -> startServicesIfNeeded() -> instantiate SystemUI[] services -> start()
```

Each service extends the abstract `SystemUI` base class, which provides:
- `start()` -- initialization entry point
- `onBootCompleted()` -- post-boot callback
- `onConfigurationChanged()` -- config change handling
- `dump()` -- dumpsys integration via `Dumpable` interface

### 2.2 Dependency Injection with Dagger

**Key files:**
- `dagger/SystemUIRootComponent.java`
- `dagger/SystemUIModule.java`
- `dagger/DependencyProvider.java`
- `dagger/DependencyBinder.java`
- `Dependency.java`

SystemUI uses Dagger 2 for dependency injection. The root component `SystemUIRootComponent` is annotated `@Singleton` and composed of multiple modules:

| 模块 | Purpose |
|--------|---------|
| `SystemUIModule` | Core bindings (boot cache, component helper, concurrency) |
| `SystemUIDefaultModule` | Default implementations of interfaces |
| `SystemServicesModule` | Android system service bindings (WindowManager, PackageManager, etc.) |
| `DependencyProvider` | Legacy provider-style bindings (`@Provides`) |
| `DependencyBinder` | Interface-to-implementation bindings (`@Binds`) |
| `SystemUIBinder` | Bindings for SystemUI service components |
| `PipModule` | Picture-in-picture subcomponents |

Subcomponents for scoped injection:
- `StatusBarComponent` -- scoped to StatusBar lifecycle
- `NotificationRowComponent` -- per-notification row
- `ExpandableNotificationRowComponent` -- expandable notification rows

The legacy `Dependency.java` acts as a service locator (pre-Dagger migration artifact) using an `ArrayMap` of lazy initializers. New code uses `@Inject` constructor injection.

**Lesson for app developers:** SystemUI's migration from service locator (`Dependency.java`) to constructor injection demonstrates the recommended Dagger adoption path for large apps.

### 2.3 Status Bar (`StatusBar.java`)

**Key file:** `statusbar/phone/StatusBar.java` (4,411 lines)

`StatusBar` extends `SystemUI` and is the central coordinator for the phone's status bar UI. It implements multiple interfaces:

- `ActivityStarter` -- launching activities from system UI context
- `KeyguardStateController.Callback` -- lock screen state changes
- `CommandQueue.Callbacks` -- IPC commands from system_server
- `ColorExtractor.OnColorsChangedListener` -- wallpaper color changes
- `ConfigurationListener` -- configuration (rotation, density) changes
- `StatusBarStateController.StateListener` -- shade/keyguard state transitions
- `BatteryController.BatteryStateChangeCallback` -- battery status
- `LifecycleOwner` -- AndroidX lifecycle integration

The status bar manages three visual states via `StatusBarState`:
1. **SHADE** -- normal status bar with pull-down notification shade
2. **KEYGUARD** -- lock screen
3. **SHADE_LOCKED** -- notification shade pulled down on lock screen

Key injected dependencies include: `NotificationsController`, `BubbleController`, `NavigationBarController`, `BrightnessMirrorController`, `KeyguardViewMediator`, `BiometricUnlockController`, `NotificationPanelViewController`.

### 2.4 Notification Shade

**Key file:** `statusbar/phone/NotificationPanelViewController.java`

The notification panel is managed by `NotificationPanelViewController`, which handles:
- Touch event processing for swipe-to-expand/collapse
- Coordinating QS panel expansion with notification list scrolling
- Keyguard clock and status display
- Biometric unlock animations
- Media player integration

The notification system uses a complex pipeline:
```
NotificationListener (receives from system_server)
    -> NotificationEntryManager (manages notification entries)
    -> NotificationFilter (visibility filtering)
    -> NotificationGroupManager (grouping logic)
    -> VisualStabilityManager (prevents reordering during interaction)
    -> NotificationViewHierarchyManager (view binding)
```

### 2.5 Quick Settings

**Key files:**
- `qs/QSTileHost.java` -- tile management and lifecycle
- `qs/tileimpl/QSTileImpl.java` -- abstract base for all tiles
- `qs/tiles/` -- 20 built-in tile implementations

#### QSTileImpl Architecture

`QSTileImpl<TState extends State>` is the base class for all quick settings tiles. It uses a generic state pattern:

- State management runs on a background `Looper` via inner `Handler H`
- UI updates are posted to the main thread via `mUiHandler`
- Each tile implements `handleUpdateState(TState state, Object arg)` to populate its state
- State changes trigger callbacks to registered `QSTile.Callback` listeners
- Tiles implement `LifecycleOwner` for lifecycle-aware observation

Tile lifecycle:
```
QSTileHost.createTile(spec) -> QSFactory.createTile(spec)
    -> tile.initialize() -> tile.handleSetListening(true)
    -> tile.refreshState() -> tile.handleUpdateState()
```

**Built-in tiles** (in `qs/tiles/`):
`WifiTile`, `BluetoothTile`, `CellularTile`, `BatterySaverTile`, `DndTile`, `FlashlightTile`, `RotationLockTile`, `LocationTile`, `CastTile`, `HotspotTile`, `AirplaneModeTile`, `NfcTile`, `DataSaverTile`, `NightDisplayTile`, `ColorInversionTile`, `UiModeNightTile`, `ScreenRecordTile`, `UserTile`

**Example -- WifiTile pattern:**
```java
public class WifiTile extends QSTileImpl<SignalState> {
    @Inject
    public WifiTile(QSHost host, NetworkController networkController,
            ActivityStarter activityStarter) {
        super(host);
        mController = networkController;
        mController.observe(getLifecycle(), mSignalCallback);  // lifecycle-aware
    }
}
```

The `QSTileHost` reads tile specs from `Settings.Secure.QS_TILES` and uses `TunerService` to observe changes. Third-party tiles use the `TileService` API through `CustomTile` and `TileLifecycleManager`.

### 2.6 Lock Screen (Keyguard)

**Key file:** `keyguard/KeyguardViewMediator.java`

`KeyguardViewMediator` extends `SystemUI` and coordinates the lock screen:
- Listens to `KeyguardUpdateMonitor` for biometric events, SIM state, trust agents
- Manages lock/unlock transitions with `KeyguardViewController`
- Handles lock timeout via `AlarmManager`
- Coordinates with `BiometricUnlockController` for fingerprint/face unlock
- Uses `LockPatternUtils` for credential verification
- Integrates with `DeviceConfig` for feature flags

### 2.7 New Android 11 Features in SystemUI

#### Device Controls (`controls/`)
- `ControlsController` interface manages favorites and communicates with `ControlsProviderService` implementations
- Uses binding pattern with `ControlsBindingController` for service communication
- UI handled by `ControlsUiController`

#### Media Controls (`media/`)
Android 11 introduced the redesigned media player in notification shade:
- `MediaDataManager` -- central data manager for media sessions (Kotlin)
- `MediaControlPanel` -- UI for individual media controls
- `MediaCarouselController` -- scrollable carousel of media players
- `MediaHierarchyManager` -- manages media player placement across QS/notification/keyguard
- `SeekBarViewModel` / `SeekBarObserver` -- MVVM pattern for seek bar
- `MediaResumeListener` -- resumable media session tracking

#### Bubbles (`bubbles/`)
- `BubbleController` -- main controller for bubble management
- `BubbleData` -- data model for active bubbles
- `BubbleExpandedView` -- expanded bubble view
- `BubbleDataRepository` (Kotlin) -- persistence layer

### 2.8 SystemUI AndroidManifest Permissions

SystemUI declares over 100 permissions in its manifest, including privileged system permissions:

| 分类 | Example Permissions |
|----------|-------------------|
| Status bar | `STATUS_BAR_SERVICE`, `STATUS_BAR`, `EXPAND_STATUS_BAR` |
| Networking | `BLUETOOTH_PRIVILEGED`, `NETWORK_SETTINGS`, `TETHER_PRIVILEGED` |
| Hardware | `DEVICE_POWER`, `CONTROL_DISPLAY_BRIGHTNESS`, `MANAGE_USB` |
| Activity management | `REAL_GET_TASKS`, `REMOVE_TASKS`, `START_ACTIVITIES_FROM_BACKGROUND` |
| User management | `MANAGE_USERS`, `INTERACT_ACROSS_USERS_FULL` |
| Security | `MANAGE_BIOMETRIC`, `CONTROL_KEYGUARD_SECURE_NOTIFICATIONS` |

The `coreApp="true"` flag and `android.uid.systemui` shared UID grant SystemUI elevated privileges unavailable to third-party apps.

---

## 3. Settings App Architecture

**Source:** `packages/apps/Settings/`

### 3.1 App Structure Overview

The Settings app uses a fragment-based architecture with two entry points:

1. **SettingsHomepageActivity** (`FragmentActivity`) -- the main settings screen
   - Hosts `TopLevelSettings` fragment in `main_content` container
   - Hosts `ContextualCardsFragment` for contextual suggestions (high-RAM devices only)
   - Uses `AvatarViewMixin` as a lifecycle observer for user avatar

2. **SettingsActivity** (`SettingsBaseActivity`) -- hosts sub-setting screens
   - Supports `EXTRA_SHOW_FRAGMENT` to directly launch specific preference fragments
   - Manages fragment back stack with `FragmentManager.OnBackStackChangedListener`
   - Integrates with setup wizard via `WizardManagerHelper`

### 3.2 DashboardFragment Pattern

**Key file:** `dashboard/DashboardFragment.java`

`DashboardFragment` is the base class for most Settings screens. It extends `SettingsPreferenceFragment` and introduces a two-source preference system:

1. **Static preferences** defined in XML (`getPreferenceScreenResId()`)
2. **Dynamic preferences** injected at runtime from `DashboardCategory` tiles

The preference controller loading is a key pattern:
```java
// In DashboardFragment.onAttach():
// 1. Load controllers from code
List<AbstractPreferenceController> controllersFromCode = createPreferenceControllers(context);
// 2. Load controllers from XML (android:controller attribute)
List<BasePreferenceController> controllersFromXml =
    PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId());
// 3. Filter duplicates, merge, and bind to preferences
```

### 3.3 BasePreferenceController Hierarchy

**Key file:** `core/BasePreferenceController.java`

The controller hierarchy provides a clean separation of UI logic from preference display:

```
AbstractPreferenceController (settingslib)
    |
    +-- BasePreferenceController (implements Sliceable)
        |
        +-- TogglePreferenceController  -- for boolean settings
        +-- SliderPreferenceController   -- for slider/seekbar settings
        +-- LiveDataController           -- for LiveData-backed settings
```

`BasePreferenceController` defines availability status constants that control both visibility and searchability:
- `AVAILABLE` (0) -- visible and searchable
- `AVAILABLE_UNSEARCHABLE` (1) -- visible but not searchable
- `CONDITIONALLY_UNAVAILABLE` (2) -- temporarily hidden
- `UNSUPPORTED_ON_DEVICE` (3) -- never available on this device
- `DISABLED_FOR_USER` (4) -- blocked by user restrictions
- `DISABLED_DEPENDENT_SETTING` (5) -- blocked by dependent setting

**TogglePreferenceController** demonstrates the template method pattern:
```java
public abstract class TogglePreferenceController extends BasePreferenceController {
    public abstract boolean isChecked();       // read current state
    public abstract boolean setChecked(boolean isChecked);  // write state
    // updateState() automatically syncs UI with isChecked()
}
```

### 3.4 FeatureFactory Pattern

**Key file:** `overlay/FeatureFactory.java`

Settings uses an abstract factory pattern for OEM customization. `FeatureFactory` is loaded at runtime from a resource-configured class name (`R.string.config_featureFactory`):

```java
sFactory = (FeatureFactory) context.getClassLoader()
    .loadClass(clsName).newInstance();
```

Feature providers include:
- `DashboardFeatureProvider` -- dashboard tile handling
- `SearchFeatureProvider` -- settings search
- `MetricsFeatureProvider` -- analytics logging
- `SecurityFeatureProvider` -- security settings
- `PowerUsageFeatureProvider` -- battery stats
- `SuggestionFeatureProvider` -- setup suggestions
- `PanelFeatureProvider` -- Settings panels (Android 10+)
- `ContextualCardFeatureProvider` -- contextual cards

**Lesson for app developers:** This pattern enables clean product-line engineering where OEMs override behavior without forking the codebase.

### 3.5 Settings Slices

**Key files:**
- `slices/SettingsSliceProvider.java`
- `slices/SliceData.java`
- `slices/SlicesDatabaseHelper.java`

Settings implements `SliceProvider` to expose settings as Android Slices for inline display in other apps:

1. `SlicesIndexer` indexes all `BasePreferenceController` instances into a SQLite database
2. `SettingsSliceProvider` serves Slice content by looking up controllers by URI key
3. `SliceBroadcastReceiver` handles user actions on Slices
4. Controllers implement `Sliceable` to define slice type and background worker

The Slice pipeline:
```
SliceProvider.onBindSlice(uri)
    -> SlicesDatabaseAccessor.getSliceDataFromUri(uri)
    -> SliceBuilderUtils.buildSlice(sliceData)
    -> BasePreferenceController (handles toggle/slider actions)
```

### 3.6 Search Indexing

**Key file:** `search/BaseSearchIndexProvider.java`

Settings implements `SearchIndexableResource` for each preference screen, enabling the settings search feature:

```java
@SearchIndexable(forTarget = MOBILE)
public class TopLevelSettings extends DashboardFragment {
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider(R.xml.top_level_settings);
}
```

`BaseSearchIndexProvider` automatically:
- Parses XML preference screens for indexable keys
- Filters non-indexable keys via controllers' `getAvailabilityStatus()`
- Supports dynamic raw data indexing

### 3.7 Settings Manifest Highlights

The Settings app uses `android.uid.system` shared UID and declares 100+ permissions. Notable patterns:

- Multiple `<activity-alias>` entries for different entry points (WiFi settings, Bluetooth, etc.)
- Use of `android:exported="true"` with specific intent filters
- `READ_SEARCH_INDEXABLES` permission for search integration
- `WRITE_SECURE_SETTINGS` for modifying system settings

---

## 4. Launcher3 Architecture

**Source:** `packages/apps/Launcher3/`

### 4.1 Core Architecture

Launcher3 uses a Model-View-Controller variant with a state machine:

```
LauncherAppState (singleton) -- holds app-level state
    -> LauncherModel -- data model, background loading
    -> IconCache -- app icon caching
    -> InvariantDeviceProfile -- device profile calculations
    -> WidgetPreviewLoader -- widget preview caching

Launcher (StatefulActivity) -- main activity
    -> StateManager<LauncherState> -- state machine
    -> DragController -- drag and drop
    -> Workspace (ViewGroup) -- home screen pages
    -> AllAppsContainerView -- app drawer
    -> Hotseat -- bottom dock
```

### 4.2 State Machine

**Key files:**
- `statemanager/StateManager.java`
- `LauncherState.java`

Launcher3's state management is a sophisticated finite state machine. `StateManager<STATE_TYPE>` manages transitions between states with animated property changes:

**Defined states:**
| State | Ordinal | 描述 |
|-------|---------|-------------|
| `NORMAL` | 0 | Home screen, workspace visible |
| `SPRING_LOADED` | 1 | Drag-to-place mode |
| `ALL_APPS` | 2 | App drawer open |
| `OVERVIEW` | 3 | Recent apps view |
| `OVERVIEW_PEEK` | 4 | Overview hint on home button hold |
| `BACKGROUND_APP` | 5 | App is in foreground |
| `HINT` | 6 | Navigation hint state |
| `QUICK_SWITCH` | 7 | Quick switch gesture |
| `OVERVIEW_MODAL_TASK` | 8 | Modal task in overview |

Each `LauncherState` defines visibility flags as bitmasks:
```java
public static final int HOTSEAT_ICONS = 1 << 0;
public static final int ALL_APPS_CONTENT = 1 << 4;
public static final int OVERVIEW_BUTTONS = 1 << 6;
```

And behavioral flags:
```java
public static final int FLAG_MULTI_PAGE = BaseState.getFlag(0);
public static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = BaseState.getFlag(2);
public static final int FLAG_CLOSE_POPUPS = BaseState.getFlag(6);
```

`StateHandler` implementations animate components during transitions (workspace, all apps, overview).

### 4.3 LauncherModel -- Data Loading

**Key file:** `LauncherModel.java`

`LauncherModel` extends `LauncherApps.Callback` to receive package change events and manages all data loading:

- `LoaderTask` runs on `MODEL_EXECUTOR` (background thread) and loads workspace items, all apps, widgets, and shortcuts from the database
- `BgDataModel` holds all in-memory data: `itemsIdMap`, `workspaceItems`, `appWidgets`, `folders`
- `Callbacks` interface pushes loaded data back to the UI thread
- Model update tasks (`BaseModelUpdateTask`, `PackageUpdatedTask`, `CacheDataUpdatedTask`) process changes incrementally

Threading model:
```
MODEL_EXECUTOR (background) -- data loading, DB operations
MAIN_EXECUTOR (UI thread)   -- callback delivery, UI updates
```

### 4.4 LauncherProvider -- Content Provider

**Key file:** `LauncherProvider.java`

`LauncherProvider` is a `ContentProvider` managing the launcher database (schema version 28):

- Stores workspace items (shortcuts, folders, widgets) in `favorites` table
- Supports batch `ContentProviderOperation` for atomic workspace updates
- Implements grid migration (`GridSizeMigrationTask`) when device profile changes
- Handles backup/restore via `BackupManager` notifications
- `LauncherDbUtils` provides utility methods including `SQLiteTransaction` for safe transactions

Database schema includes: `_id`, `title`, `intent`, `container`, `screen`, `cellX`, `cellY`, `spanX`, `spanY`, `itemType`, `appWidgetId`, `appWidgetProvider`, `profileId`, and more.

### 4.5 Widget System

**Key files in `widget/`:**
- `WidgetsFullSheet.java` -- full-screen widget picker
- `WidgetsListAdapter.java` -- adapter for widget list
- `WidgetCell.java` -- individual widget preview cell
- `LauncherAppWidgetHostView.java` -- hosting widget views
- `WidgetManagerHelper.java` -- wrapper around `AppWidgetManager`
- `WidgetHostViewLoader.java` -- async widget view inflation

Widget lifecycle:
```
User picks widget -> PendingAddWidgetInfo created
    -> WidgetAddFlowHandler.startBindFlow()
    -> AppWidgetHost.allocateAppWidgetId()
    -> AppWidgetManager.bindAppWidgetIdIfAllowed()
    -> LauncherAppWidgetHostView added to workspace
    -> ModelWriter persists to database
```

### 4.6 Launcher3 Manifest

```xml
<activity android:name="com.android.launcher3.Launcher"
    android:launchMode="singleTask"
    android:clearTaskOnLaunch="true"
    android:stateNotNeeded="true"
    android:taskAffinity=""
    android:resumeWhilePausing="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

Notable attributes:
- `singleTask` launch mode -- only one instance
- `clearTaskOnLaunch` -- fresh state on return
- `stateNotNeeded` -- no state saving needed (model reloads from DB)
- `taskAffinity=""` -- separate task from launched apps
- `resumeWhilePausing` -- faster return to home

### 4.7 Plugin System

Launcher3 integrates with SystemUI's plugin system:
```java
public class Launcher extends StatefulActivity<LauncherState>
    implements PluginListener<OverlayPlugin> { ... }
```

This allows dynamic UI extensions (e.g., Google Feed panel) through `LauncherOverlayManager` and `OverlayPlugin` interfaces without modifying the launcher binary.

---

## 5. Other Built-in Apps

### 5.1 Contacts App

**Source:** `packages/apps/Contacts/`

Architecture:
- `ContactsApplication` -- custom Application class with `ContactPhotoManager` initialization
- `PeopleActivity` -- main activity with contact list (tab-based UI)
- **Loader pattern**: Uses `CursorLoader` and `AsyncTaskLoader` extensively for `ContactsProvider2` queries
- `ContactSaveService` -- `IntentService` for background save operations
- `DynamicShortcuts` -- publishes launcher shortcuts for frequent contacts
- Directory structure follows feature-based organization: `activities/`, `editor/`, `detail/`, `list/`, `group/`, `quickcontact/`

Key API usage:
- `ContactsContract` content provider for all contact data
- `AccountManager` for multi-account support
- NFC beam for contact sharing (`NfcHandler`)
- `ShortcutManager` for dynamic shortcuts

### 5.2 DocumentsUI

**Source:** `packages/apps/DocumentsUI/`

Reference implementation for the Storage Access Framework:
- `AbstractActionHandler` / `ActionHandler` -- command pattern for user actions
- `DirectoryLoader` -- `AsyncTaskLoader` for directory contents
- `DocsSelectionHelper` -- multi-select support
- `DragAndDropManager` -- cross-app drag and drop
- Implements both picker and file manager modes

### 5.3 Calendar, Camera2, Gallery2

These apps demonstrate standard Android patterns:
- **Calendar**: Content provider queries against `CalendarContract`, sync adapter pattern
- **Camera2**: Camera2 API usage with `CameraCaptureSession`, surface-based preview
- **Gallery2**: `MediaStore` content provider integration, bitmap memory management

---

## 6. Common Patterns Across AOSP Apps

### 6.1 Architecture Patterns

| Pattern | Used In | 描述 |
|---------|---------|-------------|
| **Dependency Injection (Dagger)** | SystemUI | Full Dagger 2 with component hierarchy |
| **Service Locator** | SystemUI (legacy) | `Dependency.java` -- being migrated to DI |
| **Abstract Factory** | Settings | `FeatureFactory` for OEM customization |
| **State Machine** | Launcher3 | `StateManager` with `LauncherState` |
| **Controller Pattern** | Settings | `BasePreferenceController` hierarchy |
| **Observer/Callback** | All apps | Extensive use of listener interfaces |
| **Template Method** | Settings, SystemUI | `TogglePreferenceController`, `QSTileImpl` |
| **Command Pattern** | DocumentsUI | `ActionHandler` for user actions |
| **MVC Variant** | Launcher3 | `LauncherModel` + `Launcher` + Views |

### 6.2 Threading Patterns

All AOSP apps follow strict threading discipline:

- **Named executors**: `MODEL_EXECUTOR`, `MAIN_EXECUTOR` (Launcher3), `BG_LOOPER` (SystemUI)
- **Background handlers**: Custom `Handler`/`Looper` pairs for specific subsystems
- **AsyncTask**: Still used in Settings and older code (deprecated in API 30)
- **ThreadUtils**: Utility class in settingslib for executor management
- **@WorkerThread / @UiThread annotations**: Used for enforcement

### 6.3 Content Provider Patterns

| App | Provider | URI Authority | Purpose |
|-----|----------|---------------|---------|
| Launcher3 | `LauncherProvider` | `com.android.launcher3.settings` | Workspace persistence |
| Settings | `SettingsSliceProvider` | `com.android.settings.slices` | Settings as Slices |
| Settings | Search indexing | `com.android.settings` | Search results |

Common content provider patterns in AOSP:
- `SQLiteTransaction` wrapper for safe transaction handling (Launcher3)
- `ContentProviderOperation` batch operations for atomicity
- `ContentObserver` for change notification
- URI-based routing with `UriMatcher`

### 6.4 Lifecycle Integration

AOSP apps demonstrate progressive adoption of AndroidX lifecycle:

- **SystemUI QSTileImpl**: Implements `LifecycleOwner` with `LifecycleRegistry` for lifecycle-aware component observation
- **Settings**: `DashboardFragment` uses `Lifecycle` from settingslib for controller lifecycle management; `SettingsHomepageActivity` uses `getLifecycle().addObserver()` for mixins
- **Launcher3**: Custom state lifecycle via `StateManager.StateListener`

### 6.5 Configuration Change Handling

```xml
<!-- Launcher3 approach: handle everything in-process -->
android:configChanges="keyboard|keyboardHidden|mcc|mnc|navigation|
    orientation|screenSize|screenLayout|smallestScreenSize"
```

- Launcher3 handles most config changes itself to avoid recreation
- Settings uses standard activity recreation with fragment save/restore
- SystemUI handles config changes via `ConfigurationController` broadcasting to all listeners

### 6.6 Permission Patterns

System apps use the `android:sharedUserId` mechanism for elevated access:

| App | Shared UID | Effect |
|-----|-----------|--------|
| SystemUI | `android.uid.systemui` | SystemUI-level privileges |
| Settings | `android.uid.system` | System-level privileges |
| Launcher3 | (none) | Standard app permissions |
| Contacts | (none) | Standard + dangerous permissions |

System apps can use `@SystemApi` and `@hide` APIs not available to third-party developers. Launcher3 is notable for operating mostly with standard app permissions, making it a better reference for third-party developers.

---

## 7. Key API Usage Patterns for App Developers

### 7.1 From SystemUI

- **Custom View with WindowManager**: SystemUI adds views directly to the window via `WindowManager.addView()` with `TYPE_STATUS_BAR` -- shows how system overlay UIs work
- **Notification Listener**: `NotificationListenerService` integration for processing notifications
- **MediaSession observation**: Media controls demonstrate `MediaController` and `MediaSession.Callback` usage
- **Lifecycle-aware components**: QS tiles show how to build lifecycle-aware controllers that clean up resources automatically

### 7.2 From Settings

- **Preference-based UI**: `DashboardFragment` + `BasePreferenceController` is the canonical pattern for settings screens
- **SliceProvider**: `SettingsSliceProvider` shows how to expose app data as Slices
- **Search indexing**: `BaseSearchIndexProvider` demonstrates making content searchable
- **Deferred binding from XML**: Controllers declared in preference XML via `android:controller` attribute

### 7.3 From Launcher3

- **AppWidgetHost**: Complete widget hosting implementation with allocation, binding, and view management
- **LauncherApps API**: `LauncherModel` shows proper use of `LauncherApps.Callback` for package monitoring
- **ShortcutManager**: Shortcut pinning, querying, and display
- **Drag and Drop**: Full drag-and-drop framework with `DragController`, `DragLayer`, `DropTarget`
- **Content Provider with migration**: `LauncherProvider` demonstrates database schema versioning and grid migration

### 7.4 From Contacts

- **ContentResolver patterns**: Efficient use of `CursorLoader` with `ContactsContract`
- **IntentService for writes**: `ContactSaveService` for background data persistence
- **Dynamic Shortcuts**: `DynamicShortcuts` for frequently-contacted people

---

## 8. Architectural Observations and Takeaways

### 8.1 Evolution of Patterns

The codebase shows clear evolution across Android versions:
- **Old pattern**: `AsyncTask`, manual threading, service locator (`Dependency.java`)
- **Current pattern**: Dagger injection, Kotlin coroutines/flows (in media code), lifecycle-aware components
- **Mixed state**: Some classes (e.g., `StatusBar.java` at 4,400+ lines) are legacy monoliths being gradually decomposed

### 8.2 Code Organization

- **Feature-based packaging**: Settings organizes by feature (`wifi/`, `bluetooth/`, `notification/`)
- **Layer-based packaging**: SystemUI organizes by architectural layer (`statusbar/`, `qs/`, `keyguard/`)
- **Hybrid**: Launcher3 uses both (`model/`, `widget/`, `allapps/`)

### 8.3 Testability Considerations

- `@VisibleForTesting` annotations are used extensively across all apps
- SystemUI's Dagger setup enables dependency substitution in tests
- Settings' `FeatureFactory` and controller pattern support mocking
- Launcher3's `TestProtocol` class defines constants for UI automation tests

### 8.4 Areas of Concern

- `StatusBar.java` at 4,400+ lines violates single-responsibility principle (ongoing decomposition via `StatusBarComponent`)
- `Dependency.java` service locator is a known anti-pattern (migration to Dagger ongoing)
- Mix of Java and Kotlin across SystemUI may cause friction (media package is Kotlin, most others Java)
- Some controllers in Settings still use deprecated `AsyncTask`

---

## 9. File Reference Index

### SystemUI
| File | Path |
|------|------|
| Application bootstrap | `frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUIApplication.java` |
| Base SystemUI class | `frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUI.java` |
| Status bar | `frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/StatusBar.java` |
| Notification panel | `frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/NotificationPanelViewController.java` |
| QS tile host | `frameworks/base/packages/SystemUI/src/com/android/systemui/qs/QSTileHost.java` |
| QS tile base | `frameworks/base/packages/SystemUI/src/com/android/systemui/qs/tileimpl/QSTileImpl.java` |
| Dagger root | `frameworks/base/packages/SystemUI/src/com/android/systemui/dagger/SystemUIRootComponent.java` |
| Dagger module | `frameworks/base/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java` |
| Keyguard mediator | `frameworks/base/packages/SystemUI/src/com/android/systemui/keyguard/KeyguardViewMediator.java` |
| Legacy DI | `frameworks/base/packages/SystemUI/src/com/android/systemui/Dependency.java` |
| Device controls | `frameworks/base/packages/SystemUI/src/com/android/systemui/controls/controller/ControlsController.kt` |
| Media controls | `frameworks/base/packages/SystemUI/src/com/android/systemui/media/MediaControlPanel.java` |
| Manifest | `frameworks/base/packages/SystemUI/AndroidManifest.xml` |

### Settings
| File | Path |
|------|------|
| Homepage | `packages/apps/Settings/src/com/android/settings/homepage/SettingsHomepageActivity.java` |
| Top-level settings | `packages/apps/Settings/src/com/android/settings/homepage/TopLevelSettings.java` |
| Settings activity | `packages/apps/Settings/src/com/android/settings/SettingsActivity.java` |
| Dashboard fragment | `packages/apps/Settings/src/com/android/settings/dashboard/DashboardFragment.java` |
| Base controller | `packages/apps/Settings/src/com/android/settings/core/BasePreferenceController.java` |
| Toggle controller | `packages/apps/Settings/src/com/android/settings/core/TogglePreferenceController.java` |
| Feature factory | `packages/apps/Settings/src/com/android/settings/overlay/FeatureFactory.java` |
| Slice provider | `packages/apps/Settings/src/com/android/settings/slices/SettingsSliceProvider.java` |
| Search index | `packages/apps/Settings/src/com/android/settings/search/BaseSearchIndexProvider.java` |
| Manifest | `packages/apps/Settings/AndroidManifest.xml` |

### Launcher3
| File | Path |
|------|------|
| Main activity | `packages/apps/Launcher3/src/com/android/launcher3/Launcher.java` |
| App state | `packages/apps/Launcher3/src/com/android/launcher3/LauncherAppState.java` |
| Data model | `packages/apps/Launcher3/src/com/android/launcher3/LauncherModel.java` |
| BG data model | `packages/apps/Launcher3/src/com/android/launcher3/model/BgDataModel.java` |
| State manager | `packages/apps/Launcher3/src/com/android/launcher3/statemanager/StateManager.java` |
| Launcher state | `packages/apps/Launcher3/src/com/android/launcher3/LauncherState.java` |
| Content provider | `packages/apps/Launcher3/src/com/android/launcher3/LauncherProvider.java` |
| DB utilities | `packages/apps/Launcher3/src/com/android/launcher3/provider/LauncherDbUtils.java` |
| Manifest | `packages/apps/Launcher3/AndroidManifest.xml` |

### Other Apps
| File | Path |
|------|------|
| Contacts manifest | `packages/apps/Contacts/AndroidManifest.xml` |

All paths are relative to `~/aosp-android-11/`.
