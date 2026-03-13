#!/usr/bin/env python3
"""Auto-map Android APIs to OpenHarmony APIs using multi-signal scoring pipeline.

Key design decisions:
- Only methods, constructors, and functions are treated as callable APIs
- Fields, enum_constants, macros, enum_values, and typedefs are treated as
  constants/types -- they only get mapped on EXACT name match (no fuzzy)
- Subsystem stats count only callable APIs for coverage percentages
- OH api_count is properly populated in subsystems table
- Multi-signal scoring: known mapping, name similarity, param compatibility,
  return type match, and structural context
- Top-5 candidates stored per Android API in mapping_candidates table
- Tier classification: tier1_direct, tier2_similar, tier3_capable, tier4_gap
- Capability assessment populated for tier3/tier4 APIs
"""

import sqlite3
import os
import re
import json
from difflib import SequenceMatcher

DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

# Kinds that are callable APIs (methods/functions you actually call)
ANDROID_CALLABLE_KINDS = {'method', 'constructor'}
OH_CALLABLE_KINDS = {'method', 'function', 'c_function', 'property'}

# Kinds that are constants/values (not callable APIs)
ANDROID_CONSTANT_KINDS = {'field', 'enum_constant'}
OH_CONSTANT_KINDS = {'enum_value', 'macro', 'typedef'}

# Manual subsystem correspondence
SUBSYSTEM_MAPPING = {
    'App Framework': ['App Framework'],
    'Content': ['App Framework', 'Data Management'],
    'View': ['UI Framework'],
    'Widget': ['UI Framework'],
    'Animation': ['UI Framework'],
    'Graphics': ['Graphics', 'UI Framework'],
    'Networking': ['Networking'],
    'WiFi': ['WiFi', 'Networking'],
    'Bluetooth': ['Bluetooth'],
    'NFC': ['NFC'],
    'Media': ['Multimedia'],
    'Camera': ['Multimedia'],
    'Telephony': ['Telephony'],
    'Security': ['Security'],
    'Biometrics': ['Security'],
    'Location': ['Sensors', 'Other'],
    'Database': ['Data Management'],
    'Storage': ['File System', 'Data Management'],
    'Provider': ['Data Management', 'App Framework'],
    'Notifications': ['Notifications'],
    'Accessibility': ['Accessibility'],
    'Input Method': ['Input Method', 'Input'],
    'Input': ['Input'],
    'OS': ['App Framework', 'Other'],
    'Package Manager': ['Package Manager'],
    'WebView': ['WebView'],
    'Accounts': ['Accounts', 'Other'],
    'Sensors': ['Sensors'],
    'Voice': ['Multimedia', 'AI'],
    'Text': ['UI Framework'],
    'Util': ['Common', 'Other'],
    'Resources': ['App Framework', 'Other'],
    'Java Standard': ['Common', 'Other'],
    'Third Party': ['Third Party', 'Other'],
    'ICU': ['Globalization', 'Other'],
    'Core': ['App Framework', 'Other'],
    'Dalvik': ['Runtime', 'Other'],
    'Service': ['App Framework'],
    'Print': ['Other'],
}

# Type equivalence table for param/return matching (Java -> TypeScript/ArkTS)
JAVA_TO_TS_TYPES = {
    'String': 'string', 'CharSequence': 'string',
    'int': 'number', 'long': 'number', 'float': 'number', 'double': 'number',
    'short': 'number', 'byte': 'number',
    'boolean': 'boolean', 'Boolean': 'boolean',
    'void': 'void',
    'Object': 'Object', 'Bundle': 'Record',
    'byte[]': 'ArrayBuffer', 'Bitmap': 'image.PixelMap',
    'Uri': 'string', 'URL': 'string',
    'List': 'Array', 'ArrayList': 'Array', 'Map': 'Map', 'HashMap': 'Map',
    'Set': 'Set', 'HashSet': 'Set',
    'Runnable': 'Function', 'Callable': 'Function',
    'Date': 'Date',
}

# --------------------------------------------------------------------------- #
#  Known mappings data (populated into DB table)
# --------------------------------------------------------------------------- #

# Each entry: (android_package, android_type, android_method_or_None,
#              oh_module, oh_type_or_None, oh_method_or_None,
#              paradigm_shift, notes)
KNOWN_MAPPINGS_DATA = [
    # === Activity lifecycle ===
    ('android.app', 'Activity', 'onCreate',
     '@ohos.app.ability', 'UIAbility', 'onCreate', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onDestroy',
     '@ohos.app.ability', 'UIAbility', 'onDestroy', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onStart',
     '@ohos.app.ability', 'UIAbility', 'onForeground', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onStop',
     '@ohos.app.ability', 'UIAbility', 'onBackground', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onResume',
     '@ohos.app.ability', 'UIAbility', 'onForeground', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onPause',
     '@ohos.app.ability', 'UIAbility', 'onBackground', False, 'Lifecycle'),
    ('android.app', 'Activity', 'onRestart',
     '@ohos.app.ability', 'UIAbility', 'onForeground', False, 'Lifecycle'),
    ('android.app', 'Activity', 'startActivity',
     '@ohos.app.ability', 'UIAbility', 'startAbility', False, 'Navigation'),
    ('android.app', 'Activity', 'startActivityForResult',
     '@ohos.app.ability', 'UIAbility', 'startAbilityForResult', False, 'Navigation'),
    ('android.app', 'Activity', 'finish',
     '@ohos.app.ability', 'UIAbility', 'terminateSelf', False, 'Lifecycle'),
    ('android.app', 'Activity', 'setResult',
     '@ohos.app.ability', 'UIAbility', 'terminateSelfWithResult', False, 'Lifecycle'),
    ('android.app', 'Activity', 'getIntent',
     '@ohos.app.ability', 'UIAbility', None, False, 'Want from onCreate param'),
    ('android.app', 'Activity', 'setContentView',
     '@ohos.app.ability', 'UIAbility', 'build', True, 'Paradigm shift to declarative UI'),
    ('android.app', 'Activity', 'onSaveInstanceState',
     '@ohos.app.ability', 'UIAbility', 'onSaveState', False, 'State persistence'),
    ('android.app', 'Activity', 'onRestoreInstanceState',
     '@ohos.app.ability', 'UIAbility', 'onSaveState', False, 'State persistence'),

    # === Service ===
    ('android.app', 'Service', 'onCreate',
     '@ohos.app.ability', 'ServiceExtensionAbility', 'onCreate', False, 'Lifecycle'),
    ('android.app', 'Service', 'onDestroy',
     '@ohos.app.ability', 'ServiceExtensionAbility', 'onDestroy', False, 'Lifecycle'),
    ('android.app', 'Service', 'onStartCommand',
     '@ohos.app.ability', 'ServiceExtensionAbility', 'onRequest', False, 'Lifecycle'),
    ('android.app', 'Service', 'onBind',
     '@ohos.app.ability', 'ServiceExtensionAbility', 'onConnect', False, 'Lifecycle'),
    ('android.app', 'Service', 'onUnbind',
     '@ohos.app.ability', 'ServiceExtensionAbility', 'onDisconnect', False, 'Lifecycle'),

    # === BroadcastReceiver ===
    ('android.content', 'BroadcastReceiver', 'onReceive',
     '@ohos.commonEventManager', 'CommonEventSubscriber', 'onReceiveEvent', False, 'Event'),
    ('android.content', 'Context', 'sendBroadcast',
     '@ohos.commonEventManager', None, 'publish', False, 'Event'),
    ('android.content', 'Context', 'registerReceiver',
     '@ohos.commonEventManager', None, 'subscribe', False, 'Event'),
    ('android.content', 'Context', 'unregisterReceiver',
     '@ohos.commonEventManager', None, 'unsubscribe', False, 'Event'),

    # === Context ===
    ('android.content', 'Context', 'getApplicationContext',
     '@ohos.app.ability', None, 'getContext', False, 'Context'),
    ('android.content', 'Context', 'getFilesDir',
     '@ohos.app.ability', 'Context', 'filesDir', False, 'File paths'),
    ('android.content', 'Context', 'getCacheDir',
     '@ohos.app.ability', 'Context', 'cacheDir', False, 'File paths'),
    ('android.content', 'Context', 'getPackageName',
     '@ohos.bundle.bundleManager', None, None, False, 'Use bundleManager'),
    ('android.content', 'Context', 'getSystemService',
     '@ohos.app.ability', None, None, False, 'Various OH modules'),
    ('android.content', 'Context', 'startService',
     '@ohos.app.ability', None, 'startServiceExtensionAbility', False, 'Service'),
    ('android.content', 'Context', 'bindService',
     '@ohos.app.ability', None, 'connectServiceExtensionAbility', False, 'Service'),
    ('android.content', 'Context', 'unbindService',
     '@ohos.app.ability', None, 'disconnectServiceExtensionAbility', False, 'Service'),

    # === Intent / Want ===
    ('android.content', 'Intent', 'putExtra',
     '@ohos.app.ability', 'Want', 'parameters', False, 'Want params'),
    ('android.content', 'Intent', 'getStringExtra',
     '@ohos.app.ability', 'Want', 'parameters', False, 'Want params'),
    ('android.content', 'Intent', 'getIntExtra',
     '@ohos.app.ability', 'Want', 'parameters', False, 'Want params'),
    ('android.content', 'Intent', 'getBooleanExtra',
     '@ohos.app.ability', 'Want', 'parameters', False, 'Want params'),
    ('android.content', 'Intent', 'getAction',
     '@ohos.app.ability', 'Want', 'action', False, 'Want action'),
    ('android.content', 'Intent', 'setAction',
     '@ohos.app.ability', 'Want', 'action', False, 'Want action'),
    ('android.content', 'Intent', 'getData',
     '@ohos.app.ability', 'Want', 'uri', False, 'Want uri'),
    ('android.content', 'Intent', 'setData',
     '@ohos.app.ability', 'Want', 'uri', False, 'Want uri'),
    ('android.content', 'Intent', 'setClass',
     '@ohos.app.ability', 'Want', None, False, 'bundleName+abilityName'),
    ('android.content', 'Intent', 'setComponent',
     '@ohos.app.ability', 'Want', None, False, 'bundleName+abilityName'),

    # === ContentProvider ===
    ('android.content', 'ContentProvider', 'query',
     '@ohos.data.dataShare', 'DataShareExtensionAbility', 'query', False, 'DataShare'),
    ('android.content', 'ContentProvider', 'insert',
     '@ohos.data.dataShare', 'DataShareExtensionAbility', 'insert', False, 'DataShare'),
    ('android.content', 'ContentProvider', 'update',
     '@ohos.data.dataShare', 'DataShareExtensionAbility', 'update', False, 'DataShare'),
    ('android.content', 'ContentProvider', 'delete',
     '@ohos.data.dataShare', 'DataShareExtensionAbility', 'delete', False, 'DataShare'),

    # === View/UI (paradigm_shift=True for all) ===
    # Type-level mappings
    ('android.view', 'View', None,
     'ArkUI', 'Component', None, True, 'Declarative UI component'),
    ('android.widget', 'TextView', None,
     'ArkUI', 'Text', None, True, 'Declarative UI'),
    ('android.widget', 'EditText', None,
     'ArkUI', 'TextInput', None, True, 'Declarative UI'),
    ('android.widget', 'Button', None,
     'ArkUI', 'Button', None, True, 'Declarative UI'),
    ('android.widget', 'ImageView', None,
     'ArkUI', 'Image', None, True, 'Declarative UI'),
    ('android.widget', 'ImageButton', None,
     'ArkUI', 'Image', None, True, 'Declarative UI'),
    ('android.support.v7.widget', 'RecyclerView', None,
     'ArkUI', 'List', None, True, 'Declarative UI'),
    ('androidx.recyclerview.widget', 'RecyclerView', None,
     'ArkUI', 'List', None, True, 'Declarative UI'),
    ('android.widget', 'ListView', None,
     'ArkUI', 'List', None, True, 'Declarative UI'),
    ('android.widget', 'GridView', None,
     'ArkUI', 'Grid', None, True, 'Declarative UI'),
    ('android.widget', 'LinearLayout', None,
     'ArkUI', 'Row/Column', None, True, 'Use Row for horizontal, Column for vertical'),
    ('android.widget', 'FrameLayout', None,
     'ArkUI', 'Stack', None, True, 'Declarative UI'),
    ('android.widget', 'RelativeLayout', None,
     'ArkUI', 'RelativeContainer', None, True, 'Declarative UI'),
    ('android.widget', 'ScrollView', None,
     'ArkUI', 'Scroll', None, True, 'Declarative UI'),
    ('android.widget', 'HorizontalScrollView', None,
     'ArkUI', 'Scroll', None, True, 'Declarative UI'),
    ('android.widget', 'ProgressBar', None,
     'ArkUI', 'Progress', None, True, 'Declarative UI'),
    ('android.widget', 'SeekBar', None,
     'ArkUI', 'Slider', None, True, 'Declarative UI'),
    ('android.widget', 'Switch', None,
     'ArkUI', 'Toggle', None, True, 'Declarative UI'),
    ('android.widget', 'CheckBox', None,
     'ArkUI', 'Checkbox', None, True, 'Declarative UI'),
    ('android.widget', 'RadioButton', None,
     'ArkUI', 'Radio', None, True, 'Declarative UI'),
    ('android.widget', 'RadioGroup', None,
     'ArkUI', 'Radio', None, True, 'Declarative UI group'),
    ('android.widget', 'Spinner', None,
     'ArkUI', 'Select', None, True, 'Declarative UI'),
    ('android.widget', 'TabHost', None,
     'ArkUI', 'Tabs', None, True, 'Declarative UI'),
    ('android.widget', 'ViewPager', None,
     'ArkUI', 'Swiper', None, True, 'Declarative UI'),
    ('android.webkit', 'WebView', None,
     '@ohos.web.webview', 'WebviewController', None, True, 'Web component'),
    ('android.widget', 'Toast', None,
     '@ohos.promptAction', None, 'showToast', False, 'Toast notification'),
    ('android.app', 'AlertDialog', None,
     '@ohos.promptAction', None, 'showDialog', False, 'Dialog'),
    ('android.app', 'AlertDialog.Builder', None,
     '@ohos.promptAction', None, 'showDialog', False, 'Dialog'),
    ('android.widget', 'Toolbar', None,
     'ArkUI', 'Navigation', None, True, 'Navigation bar'),
    ('android.widget', 'SearchView', None,
     'ArkUI', 'Search', None, True, 'Declarative UI'),
    ('android.widget', 'DatePicker', None,
     'ArkUI', 'DatePicker', None, True, 'Declarative UI'),
    ('android.widget', 'TimePicker', None,
     'ArkUI', 'TimePicker', None, True, 'Declarative UI'),
    ('android.widget', 'CalendarView', None,
     'ArkUI', 'CalendarPicker', None, True, 'Declarative UI'),
    ('android.widget', 'VideoView', None,
     'ArkUI', 'Video', None, True, 'Declarative UI'),

    # View methods (paradigm shift)
    ('android.view', 'View', 'setVisibility',
     'ArkUI', 'Component', 'visibility', True, 'Attribute'),
    ('android.view', 'View', 'setOnClickListener',
     'ArkUI', 'Component', 'onClick', True, 'Event'),
    ('android.view', 'View', 'setOnLongClickListener',
     'ArkUI', 'Component', 'onLongPress', True, 'Gesture event'),
    ('android.view', 'View', 'setBackgroundColor',
     'ArkUI', 'Component', 'backgroundColor', True, 'Attribute'),
    ('android.view', 'View', 'setAlpha',
     'ArkUI', 'Component', 'opacity', True, 'Attribute'),
    ('android.view', 'View', 'setPadding',
     'ArkUI', 'Component', 'padding', True, 'Attribute'),
    ('android.view', 'View', 'setLayoutParams',
     'ArkUI', 'Component', 'width/height', True, 'Attribute'),
    ('android.view', 'View', 'invalidate',
     'ArkUI', 'Component', None, True, 'Auto re-render in declarative UI'),
    ('android.view', 'View', 'requestLayout',
     'ArkUI', 'Component', None, True, 'Auto layout in declarative UI'),
    ('android.view', 'View', 'getWidth',
     'ArkUI', 'Component', 'width', True, 'Attribute'),
    ('android.view', 'View', 'getHeight',
     'ArkUI', 'Component', 'height', True, 'Attribute'),
    ('android.view', 'View', 'setEnabled',
     'ArkUI', 'Component', 'enabled', True, 'Attribute'),
    ('android.view', 'View', 'findViewById',
     'ArkUI', 'Component', None, True, 'No equivalent in declarative UI'),
    ('android.view', 'View', 'animate',
     'ArkUI', 'Component', 'animation', True, 'Declarative animation'),

    # === SharedPreferences ===
    ('android.content', 'SharedPreferences', 'getString',
     '@ohos.data.preferences', 'Preferences', 'getSync', False, 'Sync get'),
    ('android.content', 'SharedPreferences', 'getInt',
     '@ohos.data.preferences', 'Preferences', 'getSync', False, 'Sync get'),
    ('android.content', 'SharedPreferences', 'getLong',
     '@ohos.data.preferences', 'Preferences', 'getSync', False, 'Sync get'),
    ('android.content', 'SharedPreferences', 'getFloat',
     '@ohos.data.preferences', 'Preferences', 'getSync', False, 'Sync get'),
    ('android.content', 'SharedPreferences', 'getBoolean',
     '@ohos.data.preferences', 'Preferences', 'getSync', False, 'Sync get'),
    ('android.content', 'SharedPreferences', 'getAll',
     '@ohos.data.preferences', 'Preferences', 'getAllSync', False, 'Sync get all'),
    ('android.content', 'SharedPreferences', 'contains',
     '@ohos.data.preferences', 'Preferences', 'hasSync', False, 'Check key'),
    ('android.content', 'SharedPreferences.Editor', 'putString',
     '@ohos.data.preferences', 'Preferences', 'putSync', False, 'Sync put'),
    ('android.content', 'SharedPreferences.Editor', 'putInt',
     '@ohos.data.preferences', 'Preferences', 'putSync', False, 'Sync put'),
    ('android.content', 'SharedPreferences.Editor', 'putLong',
     '@ohos.data.preferences', 'Preferences', 'putSync', False, 'Sync put'),
    ('android.content', 'SharedPreferences.Editor', 'putFloat',
     '@ohos.data.preferences', 'Preferences', 'putSync', False, 'Sync put'),
    ('android.content', 'SharedPreferences.Editor', 'putBoolean',
     '@ohos.data.preferences', 'Preferences', 'putSync', False, 'Sync put'),
    ('android.content', 'SharedPreferences.Editor', 'remove',
     '@ohos.data.preferences', 'Preferences', 'deleteSync', False, 'Delete key'),
    ('android.content', 'SharedPreferences.Editor', 'clear',
     '@ohos.data.preferences', 'Preferences', 'clearSync', False, 'Clear all'),
    ('android.content', 'SharedPreferences.Editor', 'apply',
     '@ohos.data.preferences', 'Preferences', 'flush', False, 'Persist'),
    ('android.content', 'SharedPreferences.Editor', 'commit',
     '@ohos.data.preferences', 'Preferences', 'flush', False, 'Persist'),
    ('android.content', 'SharedPreferences.Editor', 'edit',
     '@ohos.data.preferences', 'Preferences', None, False, 'Direct put in OH'),

    # === SQLiteDatabase ===
    ('android.database.sqlite', 'SQLiteDatabase', 'query',
     '@ohos.data.relationalStore', 'RdbStore', 'query', False, 'Query'),
    ('android.database.sqlite', 'SQLiteDatabase', 'insert',
     '@ohos.data.relationalStore', 'RdbStore', 'insert', False, 'Insert'),
    ('android.database.sqlite', 'SQLiteDatabase', 'update',
     '@ohos.data.relationalStore', 'RdbStore', 'update', False, 'Update'),
    ('android.database.sqlite', 'SQLiteDatabase', 'delete',
     '@ohos.data.relationalStore', 'RdbStore', 'delete', False, 'Delete'),
    ('android.database.sqlite', 'SQLiteDatabase', 'execSQL',
     '@ohos.data.relationalStore', 'RdbStore', 'executeSql', False, 'Execute SQL'),
    ('android.database.sqlite', 'SQLiteDatabase', 'rawQuery',
     '@ohos.data.relationalStore', 'RdbStore', 'querySql', False, 'Raw query'),
    ('android.database.sqlite', 'SQLiteDatabase', 'beginTransaction',
     '@ohos.data.relationalStore', 'RdbStore', 'beginTransaction', False, 'Transaction'),
    ('android.database.sqlite', 'SQLiteDatabase', 'endTransaction',
     '@ohos.data.relationalStore', 'RdbStore', 'commit', False, 'Transaction'),
    ('android.database.sqlite', 'SQLiteDatabase', 'setTransactionSuccessful',
     '@ohos.data.relationalStore', 'RdbStore', 'commit', False, 'Transaction'),
    ('android.database.sqlite', 'SQLiteDatabase', 'close',
     '@ohos.data.relationalStore', 'RdbStore', None, False, 'Managed by OH'),
    ('android.database.sqlite', 'SQLiteOpenHelper', 'onCreate',
     '@ohos.data.relationalStore', 'RdbStore', None, False, 'Use RdbStoreConfig'),
    ('android.database.sqlite', 'SQLiteOpenHelper', 'onUpgrade',
     '@ohos.data.relationalStore', 'RdbStore', None, False, 'Use RdbStoreConfig'),
    ('android.database.sqlite', 'SQLiteOpenHelper', 'getWritableDatabase',
     '@ohos.data.relationalStore', None, 'getRdbStore', False, 'Get store'),
    ('android.database.sqlite', 'SQLiteOpenHelper', 'getReadableDatabase',
     '@ohos.data.relationalStore', None, 'getRdbStore', False, 'Get store'),

    # === MediaPlayer ===
    ('android.media', 'MediaPlayer', 'setDataSource',
     '@ohos.multimedia.media', 'AVPlayer', 'url', False, 'Set media source'),
    ('android.media', 'MediaPlayer', 'prepare',
     '@ohos.multimedia.media', 'AVPlayer', 'prepare', False, 'Prepare'),
    ('android.media', 'MediaPlayer', 'prepareAsync',
     '@ohos.multimedia.media', 'AVPlayer', 'prepare', False, 'Async prepare'),
    ('android.media', 'MediaPlayer', 'start',
     '@ohos.multimedia.media', 'AVPlayer', 'play', False, 'Play'),
    ('android.media', 'MediaPlayer', 'pause',
     '@ohos.multimedia.media', 'AVPlayer', 'pause', False, 'Pause'),
    ('android.media', 'MediaPlayer', 'stop',
     '@ohos.multimedia.media', 'AVPlayer', 'stop', False, 'Stop'),
    ('android.media', 'MediaPlayer', 'release',
     '@ohos.multimedia.media', 'AVPlayer', 'release', False, 'Release'),
    ('android.media', 'MediaPlayer', 'seekTo',
     '@ohos.multimedia.media', 'AVPlayer', 'seek', False, 'Seek'),
    ('android.media', 'MediaPlayer', 'reset',
     '@ohos.multimedia.media', 'AVPlayer', 'reset', False, 'Reset'),
    ('android.media', 'MediaPlayer', 'setOnPreparedListener',
     '@ohos.multimedia.media', 'AVPlayer', "on('stateChange')", False, 'State callback'),
    ('android.media', 'MediaPlayer', 'setOnCompletionListener',
     '@ohos.multimedia.media', 'AVPlayer', "on('stateChange')", False, 'State callback'),
    ('android.media', 'MediaPlayer', 'setOnErrorListener',
     '@ohos.multimedia.media', 'AVPlayer', "on('error')", False, 'Error callback'),
    ('android.media', 'MediaPlayer', 'getDuration',
     '@ohos.multimedia.media', 'AVPlayer', 'duration', False, 'Duration'),
    ('android.media', 'MediaPlayer', 'getCurrentPosition',
     '@ohos.multimedia.media', 'AVPlayer', 'currentTime', False, 'Current time'),
    ('android.media', 'MediaPlayer', 'isPlaying',
     '@ohos.multimedia.media', 'AVPlayer', 'state', False, 'Check state'),
    ('android.media', 'MediaPlayer', 'setVolume',
     '@ohos.multimedia.media', 'AVPlayer', 'setVolume', False, 'Volume'),
    ('android.media', 'MediaPlayer', 'setLooping',
     '@ohos.multimedia.media', 'AVPlayer', 'loop', False, 'Loop'),

    # === AudioManager ===
    ('android.media', 'AudioManager', 'setStreamVolume',
     '@ohos.multimedia.audio', 'AudioManager', 'setVolume', False, 'Volume'),
    ('android.media', 'AudioManager', 'getStreamVolume',
     '@ohos.multimedia.audio', 'AudioManager', 'getVolume', False, 'Volume'),
    ('android.media', 'AudioManager', 'getRingerMode',
     '@ohos.multimedia.audio', 'AudioManager', 'getRingerMode', False, 'Ringer'),

    # === Network ===
    ('android.net', 'ConnectivityManager', 'getActiveNetworkInfo',
     '@ohos.net.connection', None, 'getDefaultNet', False, 'Network info'),
    ('android.net', 'ConnectivityManager', 'getActiveNetwork',
     '@ohos.net.connection', None, 'getDefaultNet', False, 'Network info'),
    ('android.net', 'ConnectivityManager', 'registerNetworkCallback',
     '@ohos.net.connection', None, 'register', False, 'Network callback'),
    ('android.net', 'ConnectivityManager', 'unregisterNetworkCallback',
     '@ohos.net.connection', None, 'unregister', False, 'Network callback'),
    ('java.net', 'HttpURLConnection', None,
     '@ohos.net.http', None, 'createHttp', False, 'HTTP client'),
    ('java.net', 'HttpURLConnection', 'connect',
     '@ohos.net.http', None, 'request', False, 'HTTP request'),
    ('java.net', 'HttpURLConnection', 'getResponseCode',
     '@ohos.net.http', None, 'request', False, 'HTTP response'),
    ('java.net', 'HttpURLConnection', 'getInputStream',
     '@ohos.net.http', None, 'request', False, 'HTTP response body'),
    ('java.net', 'HttpURLConnection', 'disconnect',
     '@ohos.net.http', None, 'destroy', False, 'Close HTTP'),

    # === WiFi ===
    ('android.net.wifi', 'WifiManager', 'isWifiEnabled',
     '@ohos.wifi', None, 'isWifiActive', False, 'WiFi status'),
    ('android.net.wifi', 'WifiManager', 'setWifiEnabled',
     '@ohos.wifi', None, 'enableWifi', False, 'WiFi toggle'),
    ('android.net.wifi', 'WifiManager', 'startScan',
     '@ohos.wifi', None, 'scan', False, 'WiFi scan'),
    ('android.net.wifi', 'WifiManager', 'getScanResults',
     '@ohos.wifi', None, 'getScanResults', False, 'WiFi scan results'),
    ('android.net.wifi', 'WifiManager', 'getConnectionInfo',
     '@ohos.wifi', None, 'getLinkedInfo', False, 'WiFi info'),

    # === Bluetooth ===
    ('android.bluetooth', 'BluetoothAdapter', None,
     '@ohos.bluetooth.access', None, None, False, 'Bluetooth access'),
    ('android.bluetooth', 'BluetoothAdapter', 'enable',
     '@ohos.bluetooth.access', None, 'enableBluetooth', False, 'Enable BT'),
    ('android.bluetooth', 'BluetoothAdapter', 'disable',
     '@ohos.bluetooth.access', None, 'disableBluetooth', False, 'Disable BT'),
    ('android.bluetooth', 'BluetoothAdapter', 'isEnabled',
     '@ohos.bluetooth.access', None, 'getState', False, 'BT state'),
    ('android.bluetooth', 'BluetoothAdapter', 'startDiscovery',
     '@ohos.bluetooth.connection', None, 'startBluetoothDiscovery', False, 'BT discovery'),
    ('android.bluetooth', 'BluetoothAdapter', 'cancelDiscovery',
     '@ohos.bluetooth.connection', None, 'stopBluetoothDiscovery', False, 'BT discovery'),
    ('android.bluetooth', 'BluetoothAdapter', 'getBondedDevices',
     '@ohos.bluetooth.connection', None, 'getPairedDevices', False, 'Paired devices'),
    ('android.bluetooth', 'BluetoothDevice', 'createBond',
     '@ohos.bluetooth.connection', None, 'pairDevice', False, 'Pairing'),
    ('android.bluetooth', 'BluetoothDevice', 'getName',
     '@ohos.bluetooth.connection', None, 'getRemoteDeviceName', False, 'Device name'),
    ('android.bluetooth', 'BluetoothGatt', 'connect',
     '@ohos.bluetooth.ble', None, 'connect', False, 'BLE connect'),
    ('android.bluetooth', 'BluetoothGatt', 'disconnect',
     '@ohos.bluetooth.ble', None, 'disconnect', False, 'BLE disconnect'),
    ('android.bluetooth', 'BluetoothGatt', 'discoverServices',
     '@ohos.bluetooth.ble', None, 'getServices', False, 'BLE services'),

    # === Location ===
    ('android.location', 'LocationManager', None,
     '@ohos.geoLocationManager', None, None, False, 'Location manager'),
    ('android.location', 'LocationManager', 'requestLocationUpdates',
     '@ohos.geoLocationManager', None, "on('locationChange')", False, 'Location updates'),
    ('android.location', 'LocationManager', 'removeUpdates',
     '@ohos.geoLocationManager', None, "off('locationChange')", False, 'Stop updates'),
    ('android.location', 'LocationManager', 'getLastKnownLocation',
     '@ohos.geoLocationManager', None, 'getLastLocation', False, 'Last location'),
    ('android.location', 'LocationManager', 'isProviderEnabled',
     '@ohos.geoLocationManager', None, 'isLocationEnabled', False, 'Provider status'),
    ('android.location', 'Geocoder', 'getFromLocation',
     '@ohos.geoLocationManager', None, 'getAddressesFromLocation', False, 'Geocoding'),
    ('android.location', 'Geocoder', 'getFromLocationName',
     '@ohos.geoLocationManager', None, 'getAddressesFromLocationName', False, 'Geocoding'),

    # === Notifications ===
    ('android.app', 'NotificationManager', 'notify',
     '@ohos.notificationManager', None, 'publish', False, 'Publish notification'),
    ('android.app', 'NotificationManager', 'cancel',
     '@ohos.notificationManager', None, 'cancel', False, 'Cancel notification'),
    ('android.app', 'NotificationManager', 'cancelAll',
     '@ohos.notificationManager', None, 'cancelAll', False, 'Cancel all'),
    ('android.app', 'NotificationManager', 'createNotificationChannel',
     '@ohos.notificationManager', None, 'addSlot', False, 'Notification channel/slot'),
    ('android.app', 'NotificationManager', 'deleteNotificationChannel',
     '@ohos.notificationManager', None, 'removeSlot', False, 'Remove channel/slot'),
    ('android.app', 'Notification.Builder', 'setContentTitle',
     '@ohos.notificationManager', 'NotificationRequest', 'content', False, 'Content'),
    ('android.app', 'Notification.Builder', 'setContentText',
     '@ohos.notificationManager', 'NotificationRequest', 'content', False, 'Content'),
    ('android.app', 'Notification.Builder', 'setSmallIcon',
     '@ohos.notificationManager', 'NotificationRequest', 'smallIcon', False, 'Icon'),
    ('android.app', 'Notification.Builder', 'build',
     '@ohos.notificationManager', 'NotificationRequest', None, False, 'Build request'),

    # === Sensors ===
    ('android.hardware', 'SensorManager', 'registerListener',
     '@ohos.sensor', None, 'on', False, 'Sensor listener'),
    ('android.hardware', 'SensorManager', 'unregisterListener',
     '@ohos.sensor', None, 'off', False, 'Sensor listener'),
    ('android.hardware', 'SensorManager', 'getDefaultSensor',
     '@ohos.sensor', None, None, False, 'Sensor type param in on()'),
    ('android.hardware', 'SensorEvent', 'values',
     '@ohos.sensor', None, None, False, 'Callback data'),

    # === Telephony ===
    ('android.telephony', 'TelephonyManager', None,
     '@ohos.telephony.observer', None, None, False, 'Telephony observer'),
    ('android.telephony', 'TelephonyManager', 'getDeviceId',
     '@ohos.telephony.sim', None, 'getDefaultVoiceSlotId', False, 'Device ID'),
    ('android.telephony', 'TelephonyManager', 'getLine1Number',
     '@ohos.telephony.sim', None, 'getSimPhoneNumber', False, 'Phone number'),
    ('android.telephony', 'TelephonyManager', 'getNetworkOperatorName',
     '@ohos.telephony.sim', None, 'getSimSpn', False, 'Operator name'),
    ('android.telephony', 'TelephonyManager', 'getSimState',
     '@ohos.telephony.sim', None, 'getSimState', False, 'SIM state'),
    ('android.telephony', 'TelephonyManager', 'listen',
     '@ohos.telephony.observer', None, 'on', False, 'Telephony events'),
    ('android.telephony', 'SmsManager', None,
     '@ohos.telephony.sms', None, None, False, 'SMS manager'),
    ('android.telephony', 'SmsManager', 'sendTextMessage',
     '@ohos.telephony.sms', None, 'sendShortMessage', False, 'Send SMS'),

    # === Camera ===
    ('android.hardware.camera2', 'CameraManager', 'openCamera',
     '@ohos.multimedia.camera', None, 'createCameraInput', False, 'Open camera'),
    ('android.hardware.camera2', 'CameraManager', 'getCameraIdList',
     '@ohos.multimedia.camera', None, 'getSupportedCameras', False, 'Camera list'),
    ('android.hardware.camera2', 'CameraDevice', 'createCaptureSession',
     '@ohos.multimedia.camera', None, 'createSession', False, 'Capture session'),
    ('android.hardware.camera2', 'CameraCaptureSession', 'setRepeatingRequest',
     '@ohos.multimedia.camera', None, 'start', False, 'Start preview'),

    # === Permissions ===
    ('android.content', 'Context', 'checkSelfPermission',
     '@ohos.abilityAccessCtrl', None, 'checkAccessToken', False, 'Permission check'),
    ('android.app', 'Activity', 'requestPermissions',
     '@ohos.abilityAccessCtrl', None, 'requestPermissionsFromUser', False, 'Permission request'),

    # === PackageManager ===
    ('android.content.pm', 'PackageManager', 'getPackageInfo',
     '@ohos.bundle.bundleManager', None, 'getBundleInfoForSelf', False, 'Package info'),
    ('android.content.pm', 'PackageManager', 'getApplicationInfo',
     '@ohos.bundle.bundleManager', None, 'getApplicationInfo', False, 'App info'),
    ('android.content.pm', 'PackageManager', 'getInstalledPackages',
     '@ohos.bundle.bundleManager', None, 'getAllBundleInfo', False, 'Installed packages'),
    ('android.content.pm', 'PackageManager', 'getLaunchIntentForPackage',
     '@ohos.bundle.bundleManager', None, None, False, 'Use startAbility with Want'),

    # === AlarmManager / WorkScheduler ===
    ('android.app', 'AlarmManager', 'set',
     '@ohos.resourceschedule.workScheduler', None, 'startWork', False, 'Schedule work'),
    ('android.app', 'AlarmManager', 'setExact',
     '@ohos.resourceschedule.workScheduler', None, 'startWork', False, 'Schedule work'),
    ('android.app', 'AlarmManager', 'setRepeating',
     '@ohos.resourceschedule.workScheduler', None, 'startWork', False, 'Schedule work'),
    ('android.app', 'AlarmManager', 'cancel',
     '@ohos.resourceschedule.workScheduler', None, 'stopWork', False, 'Cancel work'),

    # === PowerManager ===
    ('android.os', 'PowerManager', 'isScreenOn',
     '@ohos.power', None, 'isActive', False, 'Screen state'),
    ('android.os', 'PowerManager', 'reboot',
     '@ohos.power', None, 'reboot', False, 'Reboot'),
    ('android.os', 'PowerManager', 'isInteractive',
     '@ohos.power', None, 'isActive', False, 'Interactive state'),
    ('android.os', 'PowerManager.WakeLock', 'acquire',
     '@ohos.runningLock', None, 'lock', False, 'Wake lock'),
    ('android.os', 'PowerManager.WakeLock', 'release',
     '@ohos.runningLock', None, 'unlock', False, 'Wake lock'),

    # === Vibrator ===
    ('android.os', 'Vibrator', 'vibrate',
     '@ohos.vibrator', None, 'startVibration', False, 'Vibrate'),
    ('android.os', 'Vibrator', 'cancel',
     '@ohos.vibrator', None, 'stopVibration', False, 'Stop vibration'),

    # === ScreenLock ===
    ('android.app', 'KeyguardManager', 'isKeyguardLocked',
     '@ohos.screenLock', None, 'isLocked', False, 'Lock state'),
    ('android.app', 'KeyguardManager', 'isDeviceSecure',
     '@ohos.screenLock', None, 'isSecure', False, 'Secure state'),

    # === Clipboard ===
    ('android.content', 'ClipboardManager', 'setPrimaryClip',
     '@ohos.pasteboard', None, 'setData', False, 'Set clipboard'),
    ('android.content', 'ClipboardManager', 'getPrimaryClip',
     '@ohos.pasteboard', None, 'getData', False, 'Get clipboard'),
    ('android.content', 'ClipboardManager', 'hasPrimaryClip',
     '@ohos.pasteboard', None, 'hasData', False, 'Has clipboard'),

    # === DownloadManager ===
    ('android.app', 'DownloadManager', 'enqueue',
     '@ohos.request', None, 'downloadFile', False, 'Download'),
    ('android.app', 'DownloadManager', 'remove',
     '@ohos.request', None, 'delete', False, 'Delete download'),

    # === WebView methods ===
    ('android.webkit', 'WebView', 'loadUrl',
     '@ohos.web.webview', 'WebviewController', 'loadUrl', False, 'Load URL'),
    ('android.webkit', 'WebView', 'loadData',
     '@ohos.web.webview', 'WebviewController', 'loadData', False, 'Load data'),
    ('android.webkit', 'WebView', 'goBack',
     '@ohos.web.webview', 'WebviewController', 'backward', False, 'Back'),
    ('android.webkit', 'WebView', 'goForward',
     '@ohos.web.webview', 'WebviewController', 'forward', False, 'Forward'),
    ('android.webkit', 'WebView', 'canGoBack',
     '@ohos.web.webview', 'WebviewController', 'accessBackward', False, 'Can go back'),
    ('android.webkit', 'WebView', 'canGoForward',
     '@ohos.web.webview', 'WebviewController', 'accessForward', False, 'Can go forward'),
    ('android.webkit', 'WebView', 'reload',
     '@ohos.web.webview', 'WebviewController', 'refresh', False, 'Reload'),
    ('android.webkit', 'WebView', 'stopLoading',
     '@ohos.web.webview', 'WebviewController', 'stop', False, 'Stop loading'),
    ('android.webkit', 'WebView', 'evaluateJavascript',
     '@ohos.web.webview', 'WebviewController', 'runJavaScript', False, 'Run JS'),

    # === Handler / TaskDispatcher ===
    ('android.os', 'Handler', 'post',
     '@ohos.taskpool', None, 'execute', False, 'Task dispatch'),
    ('android.os', 'Handler', 'postDelayed',
     '@ohos.taskpool', None, 'execute', False, 'Delayed task'),
    ('android.os', 'Handler', 'sendMessage',
     '@ohos.taskpool', None, 'execute', False, 'Message dispatch'),

    # === Animation ===
    ('android.animation', 'ObjectAnimator', 'ofFloat',
     'ArkUI', None, 'animateTo', True, 'Declarative animation'),
    ('android.animation', 'ObjectAnimator', 'ofInt',
     'ArkUI', None, 'animateTo', True, 'Declarative animation'),
    ('android.animation', 'ValueAnimator', 'start',
     'ArkUI', None, 'animateTo', True, 'Declarative animation'),
    ('android.animation', 'ValueAnimator', 'cancel',
     'ArkUI', None, None, True, 'Declarative animation'),
    ('android.view.animation', 'Animation', 'start',
     'ArkUI', None, 'animateTo', True, 'Declarative animation'),

    # === Fragment ===
    ('android.app', 'Fragment', 'onCreateView',
     'ArkUI', None, 'build', True, 'Declarative UI builder'),
    ('android.app', 'Fragment', 'onViewCreated',
     'ArkUI', None, 'aboutToAppear', True, 'Lifecycle'),
    ('android.app', 'Fragment', 'onDestroyView',
     'ArkUI', None, 'aboutToDisappear', True, 'Lifecycle'),
    ('android.app', 'FragmentManager', 'beginTransaction',
     'ArkUI', 'NavRouter', None, True, 'Navigation'),
    ('android.app', 'FragmentTransaction', 'replace',
     'ArkUI', 'NavRouter', None, True, 'Navigation'),
    ('android.app', 'FragmentTransaction', 'commit',
     'ArkUI', 'NavRouter', None, True, 'Navigation'),

    # === AccountManager ===
    ('android.accounts', 'AccountManager', 'getAccounts',
     '@ohos.account.osAccount', None, 'queryAllCreatedOsAccounts', False, 'Accounts'),
    ('android.accounts', 'AccountManager', 'addAccount',
     '@ohos.account.osAccount', None, 'createOsAccount', False, 'Create account'),
    ('android.accounts', 'AccountManager', 'removeAccount',
     '@ohos.account.osAccount', None, 'removeOsAccount', False, 'Remove account'),

    # === TextToSpeech ===
    ('android.speech.tts', 'TextToSpeech', 'speak',
     '@ohos.multimedia.media', None, None, False, 'TTS not directly available'),
    ('android.speech.tts', 'TextToSpeech', 'stop',
     '@ohos.multimedia.media', None, None, False, 'TTS not directly available'),

    # === Accessibility ===
    ('android.view.accessibility', 'AccessibilityManager', 'isEnabled',
     '@ohos.accessibility', None, 'isOpenAccessibility', False, 'Accessibility'),
    ('android.view.accessibility', 'AccessibilityEvent', None,
     '@ohos.accessibility', 'AccessibilityEvent', None, False, 'Accessibility event'),
]


# --------------------------------------------------------------------------- #
#  Database table creation and population
# --------------------------------------------------------------------------- #

def ensure_tables(conn):
    """Create required tables if they do not exist."""
    c = conn.cursor()
    c.execute("""
        CREATE TABLE IF NOT EXISTS known_mappings (
            id INTEGER PRIMARY KEY,
            android_package TEXT NOT NULL,
            android_type TEXT NOT NULL,
            android_method TEXT,
            oh_module TEXT,
            oh_type TEXT,
            oh_method TEXT,
            paradigm_shift BOOLEAN DEFAULT 0,
            notes TEXT,
            UNIQUE(android_package, android_type, android_method)
        )
    """)
    c.execute("""
        CREATE TABLE IF NOT EXISTS mapping_candidates (
            id INTEGER PRIMARY KEY,
            android_api_id INTEGER NOT NULL REFERENCES android_apis(id),
            oh_api_id INTEGER REFERENCES oh_apis(id),
            rank INTEGER NOT NULL,
            score REAL NOT NULL,
            confidence REAL,
            match_reason TEXT,
            UNIQUE(android_api_id, rank)
        )
    """)
    c.execute("""
        CREATE TABLE IF NOT EXISTS capability_assessment (
            id INTEGER PRIMARY KEY,
            android_api_id INTEGER NOT NULL REFERENCES android_apis(id),
            oh_subsystem TEXT,
            oh_module TEXT,
            oh_capability TEXT,
            implementation_hint TEXT,
            effort_estimate TEXT,
            requires_ndk BOOLEAN DEFAULT 0,
            requires_system_api BOOLEAN DEFAULT 0,
            UNIQUE(android_api_id)
        )
    """)
    conn.commit()


def populate_known_mappings(conn):
    """Insert all curated known mappings into the DB table."""
    c = conn.cursor()
    c.execute("DELETE FROM known_mappings")
    for entry in KNOWN_MAPPINGS_DATA:
        a_pkg, a_type, a_method, oh_module, oh_type, oh_method, paradigm, notes = entry
        c.execute("""
            INSERT OR REPLACE INTO known_mappings
            (android_package, android_type, android_method,
             oh_module, oh_type, oh_method, paradigm_shift, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (a_pkg, a_type, a_method, oh_module, oh_type, oh_method,
              1 if paradigm else 0, notes))
    conn.commit()
    print(f"  Populated {len(KNOWN_MAPPINGS_DATA)} known mappings into DB.")


def load_known_mappings(c):
    """Load known mappings from DB into lookup dicts."""
    rows = c.execute("""
        SELECT android_package, android_type, android_method,
               oh_module, oh_type, oh_method, paradigm_shift, notes
        FROM known_mappings
    """).fetchall()

    # Build two lookup dicts:
    #   method_lookup: (package, type, method) -> mapping row
    #   type_lookup: (package, type) -> mapping row (where android_method IS NULL)
    method_lookup = {}
    type_lookup = {}
    for r in rows:
        a_pkg, a_type, a_method, oh_module, oh_type, oh_method, paradigm, notes = r
        if a_method:
            method_lookup[(a_pkg, a_type, a_method)] = r
        else:
            type_lookup[(a_pkg, a_type)] = r
    return method_lookup, type_lookup


# --------------------------------------------------------------------------- #
#  Similarity and scoring functions
# --------------------------------------------------------------------------- #

def compute_similarity(name1, name2):
    """Compute name similarity score (0.0 to 1.0)."""
    if name1.lower() == name2.lower():
        return 1.0

    ratio = SequenceMatcher(None, name1.lower(), name2.lower()).ratio()

    # Bonus for shared tokens (camelCase splitting)
    tokens1 = set(re.findall(r'[A-Z][a-z]+|[a-z]+', name1))
    tokens2 = set(re.findall(r'[A-Z][a-z]+|[a-z]+', name2))
    if tokens1 and tokens2:
        token_overlap = len(tokens1 & tokens2) / max(len(tokens1), len(tokens2))
        ratio = max(ratio, token_overlap)

    return ratio


def compute_param_compatibility(android_params, oh_params):
    """Compare parameter lists. Returns 0.0-1.0."""
    if not android_params and not oh_params:
        return 1.0
    if not android_params or not oh_params:
        return 0.2

    # Parse param counts
    a_params = [p.strip() for p in android_params.split(',') if p.strip()]
    o_params = [p.strip() for p in oh_params.split(',') if p.strip()]

    if len(a_params) == len(o_params):
        count_score = 1.0
    elif abs(len(a_params) - len(o_params)) == 1:
        count_score = 0.7
    else:
        count_score = max(0.1, 1.0 - abs(len(a_params) - len(o_params)) * 0.2)

    # Check type compatibility for matching params
    type_scores = []
    for i in range(min(len(a_params), len(o_params))):
        matched = False
        for java_t, ts_t in JAVA_TO_TS_TYPES.items():
            if java_t.lower() in a_params[i].lower() and ts_t.lower() in o_params[i].lower():
                matched = True
                break
        type_scores.append(1.0 if matched else 0.3)

    if type_scores:
        return count_score * 0.5 + (sum(type_scores) / len(type_scores)) * 0.5
    return count_score * 0.5


def compute_return_type_match(android_ret, oh_ret):
    """Check if return types are compatible. Returns True, False, or None."""
    if not android_ret or not oh_ret:
        return None
    a = android_ret.strip().lower()
    o = oh_ret.strip().lower()

    if a == o:
        return True

    for java_t, ts_t in JAVA_TO_TS_TYPES.items():
        if java_t.lower() == a and ts_t.lower() in o:
            return True

    # Promise wrapping
    if a == 'void' and ('promise<void>' in o or o == 'void'):
        return True

    return False


def compute_type_name_similarity(a_type, oh_type):
    """Compute similarity between Android type name and OH type name."""
    if not a_type or not oh_type:
        return 0.0
    return compute_similarity(a_type, oh_type)


def effort_from_score(score):
    """Determine effort level from compatibility score."""
    if score >= 9:
        return 'trivial'
    elif score >= 7:
        return 'easy'
    elif score >= 5:
        return 'moderate'
    elif score >= 3:
        return 'hard'
    elif score >= 2:
        return 'rewrite'
    else:
        return 'impossible'


def mapping_type_from_score(score):
    if score >= 8:
        return 'direct'
    elif score >= 6:
        return 'near'
    elif score >= 4:
        return 'partial'
    elif score >= 2:
        return 'composite'
    else:
        return 'none'


def is_callable(kind, side='android'):
    """Check if an API kind represents a callable API (not a constant/type)."""
    if side == 'android':
        return kind in ANDROID_CALLABLE_KINDS
    return kind in OH_CALLABLE_KINDS


# --------------------------------------------------------------------------- #
#  Index building
# --------------------------------------------------------------------------- #

def build_oh_indexes(oh_apis):
    """Build multiple OH API indexes for fast lookup.

    Returns dict with keys:
        by_name: {lower_name: [api_tuple, ...]}
        by_module: {module_name: [api_tuple, ...]}
        by_module_type: {(module, type): [api_tuple, ...]}
        by_subsystem: {subsystem: [api_tuple, ...]}
        callable_by_name: {lower_name: [api_tuple, ...]}
        callable_by_subsystem: {subsystem: [api_tuple, ...]}
    """
    idx = {
        'by_name': {},
        'by_module': {},
        'by_module_type': {},
        'by_subsystem': {},
        'callable_by_name': {},
        'callable_by_subsystem': {},
    }
    for api in oh_apis:
        # api: (id, name, kind, signature, subsystem, type_name, module_name,
        #        return_type, params)
        name_lower = api[1].lower()
        kind = api[2]
        sub = api[4] or 'Other'
        module = api[6]
        type_name = api[5] or ''

        idx['by_name'].setdefault(name_lower, []).append(api)
        idx['by_module'].setdefault(module, []).append(api)
        idx['by_module_type'].setdefault((module, type_name), []).append(api)
        idx['by_subsystem'].setdefault(sub, []).append(api)

        if is_callable(kind, 'oh'):
            idx['callable_by_name'].setdefault(name_lower, []).append(api)
            idx['callable_by_subsystem'].setdefault(sub, []).append(api)

    return idx


def build_subsystem_module_map(c):
    """Map each OH subsystem to its modules."""
    rows = c.execute("""
        SELECT DISTINCT subsystem, name FROM oh_modules
        WHERE subsystem IS NOT NULL
    """).fetchall()
    result = {}
    for sub, mod in rows:
        result.setdefault(sub, []).append(mod)
    return result


# --------------------------------------------------------------------------- #
#  OH API finding helpers
# --------------------------------------------------------------------------- #

def find_oh_api_by_known(km, oh_indexes):
    """Find OH API matching a known mapping entry.

    km: (a_pkg, a_type, a_method, oh_module, oh_type, oh_method, paradigm, notes)
    Returns (oh_id, oh_name, ...) tuple or None.
    """
    _, _, _, oh_module, oh_type, oh_method, _, _ = km

    if not oh_module:
        return None

    # Try module+type+method exact match first
    if oh_type and oh_method:
        key = (oh_module, oh_type)
        if key in oh_indexes['by_module_type']:
            for api in oh_indexes['by_module_type'][key]:
                if api[1].lower() == oh_method.lower() and is_callable(api[2], 'oh'):
                    return api
            # Partial: match method name in module type
            for api in oh_indexes['by_module_type'][key]:
                if oh_method.lower() in api[1].lower() and is_callable(api[2], 'oh'):
                    return api

    # Try module + method (any type)
    if oh_method:
        if oh_module in oh_indexes['by_module']:
            for api in oh_indexes['by_module'][oh_module]:
                if api[1].lower() == oh_method.lower() and is_callable(api[2], 'oh'):
                    return api
            # Partial match
            for api in oh_indexes['by_module'][oh_module]:
                if oh_method.lower() in api[1].lower() and is_callable(api[2], 'oh'):
                    return api

    # Try module + type (type-level mapping)
    if oh_type and not oh_method:
        key = (oh_module, oh_type)
        if key in oh_indexes['by_module_type']:
            apis = oh_indexes['by_module_type'][key]
            if apis:
                return apis[0]

    # Fall back: any API in the module
    if oh_module in oh_indexes['by_module']:
        apis = oh_indexes['by_module'][oh_module]
        if apis:
            return apis[0]

    return None


def find_best_in_oh_module(a_name, km, oh_indexes):
    """Find best name match for a_name within the OH module from known mapping.

    Returns (oh_api, similarity) or (None, 0).
    """
    _, _, _, oh_module, oh_type, _, _, _ = km
    if not oh_module:
        return None, 0.0

    best_api = None
    best_sim = 0.0

    # Search in module+type first
    if oh_type:
        key = (oh_module, oh_type)
        if key in oh_indexes['by_module_type']:
            for api in oh_indexes['by_module_type'][key]:
                if is_callable(api[2], 'oh'):
                    sim = compute_similarity(a_name, api[1])
                    if sim > best_sim:
                        best_sim = sim
                        best_api = api

    # Search in entire module
    if best_sim < 0.7 and oh_module in oh_indexes['by_module']:
        for api in oh_indexes['by_module'][oh_module]:
            if is_callable(api[2], 'oh'):
                sim = compute_similarity(a_name, api[1])
                if sim > best_sim:
                    best_sim = sim
                    best_api = api

    return best_api, best_sim


# --------------------------------------------------------------------------- #
#  Multi-signal scoring
# --------------------------------------------------------------------------- #

def compute_confidence(match_method, signals, candidate_count):
    """Compute confidence score for a mapping."""
    base = {
        'known_exact': 0.95,
        'known_type_context': 0.80,
        'exact_name_same_sub': 0.75,
        'exact_name_diff_sub': 0.60,
        'multi_signal_high': 0.55,
        'fuzzy_name': 0.30,
        'subsystem_fuzzy': 0.20,
        'none': 0.05,
    }.get(match_method, 0.1)

    if candidate_count > 10:
        base *= 0.6
    elif candidate_count > 5:
        base *= 0.8

    return round(min(1.0, base), 2)


def classify_tier(score, confidence, has_oh_api, subsystem_has_oh):
    """Classify mapping into tiers."""
    if has_oh_api and score >= 6.0 and confidence >= 0.50:
        return 'tier1_direct'
    elif has_oh_api and score >= 3.0:
        return 'tier2_similar'
    elif subsystem_has_oh:
        return 'tier3_capable'
    else:
        return 'tier4_gap'


def map_api_multi_signal(android_api, method_lookup, type_lookup,
                         oh_indexes, subsystem_oh_modules):
    """Map Android API using 5 signals with weighted scoring.

    Returns (best_oh_id, final_score, signals, match_method, candidates_list).
    candidates_list items: (oh_id, cand_score, reason)
    """
    a_id, a_name, a_kind, a_sig, a_sub, a_type, a_pkg, a_ret, a_params = android_api

    signals = {
        'known': 0.0,
        'name': 0.0,
        'params': 0.0,
        'return': 0.0,
        'context': 0.0,
    }

    best_oh_id = None
    best_oh_api = None
    candidates = []  # (oh_id, score, reason)
    match_method = 'none'
    paradigm_shift = False

    # ---- Signal 1: Known mapping lookup (weight 0.35) ----
    known_key = (a_pkg, a_type, a_name)
    known_type_key = (a_pkg, a_type)
    km = None

    if known_key in method_lookup:
        km = method_lookup[known_key]
        oh_match = find_oh_api_by_known(km, oh_indexes)
        if oh_match:
            signals['known'] = 1.0
            best_oh_id = oh_match[0]
            best_oh_api = oh_match
            match_method = 'known_exact'
            candidates.append((oh_match[0], 1.0, 'known_exact_method'))
        else:
            signals['known'] = 0.6  # known mapping exists but OH API not in DB
            match_method = 'known_exact'
        paradigm_shift = bool(km[6])
    elif known_type_key in type_lookup:
        km = type_lookup[known_type_key]
        oh_match, sim = find_best_in_oh_module(a_name, km, oh_indexes)
        if oh_match:
            signals['known'] = sim * 0.8
            best_oh_id = oh_match[0]
            best_oh_api = oh_match
            match_method = 'known_type_context'
            candidates.append((oh_match[0], sim * 0.8, 'known_type_context'))
        paradigm_shift = bool(km[6])

    # ---- Signal 2: Name similarity (weight 0.20) ----
    # Search in relevant subsystems
    target_subs = SUBSYSTEM_MAPPING.get(a_sub, ['Other']) if a_sub else ['Other']
    best_name_sim = 0.0
    best_name_api = None

    for target_sub in target_subs:
        if target_sub in oh_indexes['callable_by_subsystem']:
            a_tokens = set(t.lower() for t in re.findall(r'[A-Z][a-z]+|[a-z]+', a_name))
            for oh_api in oh_indexes['callable_by_subsystem'][target_sub]:
                oh_tokens = set(t.lower() for t in re.findall(r'[A-Z][a-z]+|[a-z]+', oh_api[1]))
                if a_tokens & oh_tokens:
                    sim = compute_similarity(a_name, oh_api[1])
                    if sim > best_name_sim:
                        best_name_sim = sim
                        best_name_api = oh_api

    # Also check exact name across all callable OH APIs
    if a_name.lower() in oh_indexes['callable_by_name']:
        exact_candidates = oh_indexes['callable_by_name'][a_name.lower()]
        if exact_candidates:
            # Prefer one in matching subsystem
            for ec in exact_candidates:
                ec_sub = ec[4] or 'Other'
                if ec_sub in target_subs:
                    best_name_sim = 1.0
                    best_name_api = ec
                    match_method = 'exact_name_same_sub' if match_method == 'none' else match_method
                    break
            if best_name_sim < 1.0:
                best_name_sim = 1.0
                best_name_api = exact_candidates[0]
                if match_method == 'none':
                    match_method = 'exact_name_diff_sub'

    signals['name'] = best_name_sim

    if best_name_api and best_name_sim > 0.5:
        candidates.append((best_name_api[0], best_name_sim, 'name_similarity'))
        if not best_oh_id or (match_method not in ('known_exact', 'known_type_context')
                              and best_name_sim > 0.8):
            if not best_oh_id:
                best_oh_id = best_name_api[0]
                best_oh_api = best_name_api
                if best_name_sim >= 0.8:
                    match_method = 'fuzzy_name'

    # ---- Signal 3: Parameter compatibility (weight 0.20) ----
    if best_oh_api:
        oh_params = best_oh_api[8] if len(best_oh_api) > 8 else None
        param_score = compute_param_compatibility(a_params, oh_params)
        signals['params'] = param_score
    else:
        signals['params'] = 0.0

    # ---- Signal 4: Return type match (weight 0.10) ----
    if best_oh_api:
        oh_ret = best_oh_api[7] if len(best_oh_api) > 7 else None
        ret_match = compute_return_type_match(a_ret, oh_ret)
        if ret_match is True:
            signals['return'] = 1.0
        elif ret_match is False:
            signals['return'] = 0.2
        else:
            signals['return'] = 0.5  # unknown
    else:
        signals['return'] = 0.0

    # ---- Signal 5: Structural context (weight 0.15) ----
    # Subsystem alignment + type name similarity
    context_score = 0.0
    if best_oh_api:
        oh_sub = best_oh_api[4] or 'Other'
        if oh_sub in target_subs:
            context_score += 0.5

        # Type name similarity
        oh_type_name = best_oh_api[5] or ''
        if a_type and oh_type_name:
            type_sim = compute_type_name_similarity(a_type, oh_type_name)
            context_score += type_sim * 0.5
        elif not a_type and not oh_type_name:
            context_score += 0.3
    signals['context'] = min(1.0, context_score)

    # ---- Combine signals ----
    combined = (
        signals['known'] * 0.35 +
        signals['name'] * 0.20 +
        signals['params'] * 0.20 +
        signals['return'] * 0.10 +
        signals['context'] * 0.15
    )

    final_score = max(1.0, min(10.0, combined * 10))

    # Determine match method refinement
    if match_method == 'none' and best_oh_id:
        if combined >= 0.55:
            match_method = 'multi_signal_high'
        elif best_name_sim > 0.5:
            match_method = 'subsystem_fuzzy'
        else:
            match_method = 'fuzzy_name'

    # Also add subsystem-based alternatives as candidates
    if len(candidates) < 5 and best_name_sim < 0.9:
        for target_sub in target_subs:
            if target_sub in oh_indexes['callable_by_subsystem']:
                alt_apis = oh_indexes['callable_by_subsystem'][target_sub]
                scored_alts = []
                for oh_api in alt_apis:
                    sim = compute_similarity(a_name, oh_api[1])
                    if sim > 0.3 and oh_api[0] != best_oh_id:
                        scored_alts.append((oh_api[0], sim, 'subsystem_alt'))
                scored_alts.sort(key=lambda x: x[1], reverse=True)
                for alt in scored_alts[:3]:
                    if len(candidates) >= 5:
                        break
                    if alt[0] not in {c[0] for c in candidates}:
                        candidates.append(alt)

    # Sort candidates by score
    candidates.sort(key=lambda x: x[1], reverse=True)

    return best_oh_id, final_score, signals, match_method, paradigm_shift, candidates[:5]


def map_constant(android_api, oh_indexes):
    """Map a constant/field/enum_constant -- ONLY exact name match allowed."""
    a_id, a_name, a_kind, a_sig, a_sub, a_type, a_pkg, a_ret, a_params = android_api

    if a_name.lower() in oh_indexes['by_name']:
        candidates = oh_indexes['by_name'][a_name.lower()]
        # Prefer matching constant-like OH entries
        for c_api in candidates:
            if c_api[2] in OH_CONSTANT_KINDS:
                return c_api[0], 8.0, 1.0
        return candidates[0][0], 6.0, 1.0

    return None, 0, 0


# --------------------------------------------------------------------------- #
#  Main auto-mapping function
# --------------------------------------------------------------------------- #

def auto_map(db_path):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    # Step 0: Ensure required tables exist
    print("Ensuring tables exist...")
    ensure_tables(conn)

    # Step 1: Populate known_mappings table
    print("Populating known mappings...")
    populate_known_mappings(conn)

    # Step 2: Load all data
    print("Loading APIs...")
    android_apis = c.execute("""
        SELECT a.id, a.name, a.kind, a.signature, a.subsystem,
               t.name as type_name, p.name as package_name,
               a.return_type, a.params
        FROM android_apis a
        JOIN android_types t ON a.type_id = t.id
        JOIN android_packages p ON t.package_id = p.id
    """).fetchall()

    oh_apis = c.execute("""
        SELECT a.id, a.name, a.kind, a.signature, a.subsystem,
               COALESCE(t.name, '') as type_name, m.name as module_name,
               a.return_type, a.params
        FROM oh_apis a
        LEFT JOIN oh_types t ON a.type_id = t.id
        JOIN oh_modules m ON a.module_id = m.id
    """).fetchall()

    method_lookup, type_lookup = load_known_mappings(c)

    callable_count = sum(1 for a in android_apis if a[2] in ANDROID_CALLABLE_KINDS)
    constant_count = sum(1 for a in android_apis if a[2] in ANDROID_CONSTANT_KINDS)
    print(f"Android APIs: {len(android_apis)} total ({callable_count} callable, {constant_count} constants)")
    print(f"OH APIs: {len(oh_apis)} total")

    # Step 3: Build indexes
    print("Building indexes...")
    oh_idx = build_oh_indexes(oh_apis)
    subsystem_oh_modules = build_subsystem_module_map(c)

    # Step 4: Map each Android API
    print("Auto-mapping with multi-signal scoring...")
    mappings = []
    candidates_batch = []
    capability_batch = []
    mapped = 0
    unmapped = 0

    for i, android_api in enumerate(android_apis):
        a_id = android_api[0]
        a_name = android_api[1]
        a_kind = android_api[2]
        a_sub = android_api[4]

        if a_kind in ANDROID_CALLABLE_KINDS:
            best_oh_id, score, signals, method, paradigm_shift, candidates = \
                map_api_multi_signal(android_api, method_lookup, type_lookup,
                                     oh_idx, subsystem_oh_modules)

            confidence = compute_confidence(method, signals, len(candidates))

            # Determine if subsystem has OH coverage
            target_subs = SUBSYSTEM_MAPPING.get(a_sub, ['Other']) if a_sub else ['Other']
            subsystem_has_oh = any(
                ts in oh_idx['callable_by_subsystem'] for ts in target_subs
            )

            tier = classify_tier(score, confidence, best_oh_id is not None,
                                 subsystem_has_oh)

            # Paradigm shift enforcement for View/Widget
            needs_ui_rewrite = False
            if a_sub in ('View', 'Widget') and a_kind == 'method':
                paradigm_shift = True
                needs_ui_rewrite = True
                if score > 3.0 and not (method == 'known_exact' and signals['known'] >= 0.9):
                    score = 3.0

            effort = effort_from_score(score)
            mtype = mapping_type_from_score(score)

        else:
            # Constant/field/enum: ONLY exact name match
            best_oh_id, score, _ = map_constant(android_api, oh_idx)
            confidence = 0.9 if best_oh_id else 0.05
            paradigm_shift = False
            needs_ui_rewrite = False
            effort = effort_from_score(score)
            mtype = mapping_type_from_score(score)
            candidates = []
            method = 'exact_constant' if best_oh_id else 'none'
            tier = 'tier1_direct' if best_oh_id else 'tier4_gap'
            target_subs = SUBSYSTEM_MAPPING.get(a_sub, ['Other']) if a_sub else ['Other']

        if score < 1.0:
            score = 1.0

        mappings.append((
            a_id, best_oh_id, score, confidence, mtype, effort,
            paradigm_shift, needs_ui_rewrite
        ))

        if best_oh_id:
            mapped += 1
        else:
            unmapped += 1

        # Store top-5 candidates
        for rank, (cand_oh_id, cand_score, cand_reason) in enumerate(candidates[:5], 1):
            candidates_batch.append((
                a_id, cand_oh_id, rank, round(cand_score, 4),
                confidence, cand_reason
            ))

        # Capability assessment for tier3/tier4
        if tier in ('tier3_capable', 'tier4_gap'):
            oh_mods_avail = []
            for ts in target_subs:
                if ts in subsystem_oh_modules:
                    oh_mods_avail.extend(subsystem_oh_modules[ts])
            if tier == 'tier3_capable':
                hint = f"OH subsystem has modules ({', '.join(oh_mods_avail[:5])}) but no direct API match"
            else:
                hint = "No OH coverage found for this API"
            capability_batch.append((
                a_id,
                ', '.join(target_subs),           # oh_subsystem
                ', '.join(oh_mods_avail[:5]),      # oh_module
                tier,                              # oh_capability
                hint,                              # implementation_hint
                effort_from_score(score),           # effort_estimate
            ))

        if (i + 1) % 5000 == 0:
            print(f"  Processed {i+1}/{len(android_apis)}...")

    # Step 5: Write results
    print(f"Writing {len(mappings)} mappings, {len(candidates_batch)} candidates, "
          f"{len(capability_batch)} capability assessments...")

    c.execute("DELETE FROM api_mappings")
    c.execute("DELETE FROM mapping_candidates")
    c.execute("DELETE FROM capability_assessment")

    c.executemany("""
        INSERT INTO api_mappings
        (android_api_id, oh_api_id, score, confidence, mapping_type, effort_level,
         paradigm_shift, needs_ui_rewrite)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, mappings)

    c.executemany("""
        INSERT OR IGNORE INTO mapping_candidates
        (android_api_id, oh_api_id, rank, score, confidence, match_reason)
        VALUES (?, ?, ?, ?, ?, ?)
    """, candidates_batch)

    c.executemany("""
        INSERT OR REPLACE INTO capability_assessment
        (android_api_id, oh_subsystem, oh_module, oh_capability,
         implementation_hint, effort_estimate)
        VALUES (?, ?, ?, ?, ?, ?)
    """, capability_batch)

    # Step 6: Update Android API scores
    print("Updating Android API scores...")
    c.execute("""
        UPDATE android_apis SET
            compat_score = (SELECT m.score FROM api_mappings m
                            WHERE m.android_api_id = android_apis.id LIMIT 1),
            effort_level = (SELECT m.effort_level FROM api_mappings m
                            WHERE m.android_api_id = android_apis.id LIMIT 1)
    """)

    # Step 7: Populate subsystems table
    print("Building subsystem summary...")
    c.execute("DELETE FROM subsystems")
    c.execute("""
        INSERT INTO subsystems (name, api_count_android, api_count_oh,
                                overall_score, coverage_pct)
        SELECT
            a.subsystem,
            COUNT(CASE WHEN a.kind IN ('method', 'constructor') THEN 1 END)
                as callable_count,
            0,
            AVG(CASE WHEN a.kind IN ('method', 'constructor')
                      THEN COALESCE(a.compat_score, 1.0) END) as avg_score,
            CASE
                WHEN COUNT(CASE WHEN a.kind IN ('method', 'constructor')
                                THEN 1 END) = 0 THEN 0
                ELSE 100.0 * COUNT(
                    CASE WHEN a.kind IN ('method', 'constructor')
                              AND a.compat_score >= 3 THEN 1 END)
                     / COUNT(CASE WHEN a.kind IN ('method', 'constructor')
                                  THEN 1 END)
            END as coverage
        FROM android_apis a
        WHERE a.subsystem IS NOT NULL
        GROUP BY a.subsystem
    """)

    # Update OH api counts per subsystem
    c.execute("""
        UPDATE subsystems SET api_count_oh = COALESCE((
            SELECT COUNT(DISTINCT o.id)
            FROM api_mappings m
            JOIN android_apis a ON m.android_api_id = a.id
            JOIN oh_apis o ON m.oh_api_id = o.id
            WHERE a.subsystem = subsystems.name
              AND a.kind IN ('method', 'constructor')
              AND o.kind IN ('method', 'function', 'c_function', 'property')
        ), 0)
    """)

    conn.commit()

    # Print summary
    print(f"\nDone!")
    print(f"Total mapped: {mapped}, Unmapped: {unmapped}")

    callable_mapped = 0
    for b in mappings:
        # b: (a_id, oh_id, score, confidence, mtype, effort, paradigm, ui_rewrite)
        if b[1] is not None:
            a_kind_for_b = None
            for a in android_apis:
                if a[0] == b[0]:
                    a_kind_for_b = a[2]
                    break
            if a_kind_for_b in ANDROID_CALLABLE_KINDS:
                callable_mapped += 1
    print(f"Callable APIs mapped: {callable_mapped}/{callable_count}")

    # Tier summary
    tier_counts = {'tier1_direct': 0, 'tier2_similar': 0,
                   'tier3_capable': 0, 'tier4_gap': 0}
    for b in mappings:
        score_val = b[2]
        conf_val = b[3]
        has_oh = b[1] is not None
        # Approximate tier from stored values
        if has_oh and score_val >= 6.0 and conf_val >= 0.50:
            tier_counts['tier1_direct'] += 1
        elif has_oh and score_val >= 3.0:
            tier_counts['tier2_similar'] += 1
        elif has_oh:
            tier_counts['tier3_capable'] += 1
        else:
            tier_counts['tier4_gap'] += 1

    print(f"\nTier distribution:")
    for tier, cnt in tier_counts.items():
        print(f"  {tier}: {cnt}")

    print(f"\nCandidates stored: {len(candidates_batch)}")
    print(f"Capability assessments: {len(capability_batch)}")

    # Print subsystem table
    rows = c.execute("""
        SELECT name, api_count_android, api_count_oh, overall_score, coverage_pct
        FROM subsystems ORDER BY api_count_android DESC
    """).fetchall()
    print(f"\n{'Subsystem':<20} {'APIs':>6} {'OH APIs':>8} {'Avg Score':>10} {'Coverage':>10}")
    print("-" * 60)
    for row in rows:
        print(f"{row[0]:<20} {row[1]:>6} {row[2]:>8} {row[3]:>10.1f} {row[4]:>9.1f}%")

    conn.close()


def main():
    auto_map(DB_FILE)


if __name__ == '__main__':
    main()
