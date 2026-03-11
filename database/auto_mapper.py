#!/usr/bin/env python3
"""Auto-map Android APIs to OpenHarmony APIs using name/subsystem similarity.

Key design decisions:
- Only methods, constructors, and functions are treated as callable APIs
- Fields, enum_constants, macros, enum_values, and typedefs are treated as
  constants/types — they only get mapped on EXACT name match (no fuzzy)
- Subsystem stats count only callable APIs for coverage percentages
- OH api_count is properly populated in subsystems table
"""

import sqlite3
import os
import re
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
    # Android subsystem → OH subsystems (searched in order)
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
    'Location': ['Other'],  # @ohos.geoLocationManager
    'Database': ['Data Management'],
    'Storage': ['File System', 'Data Management'],
    'Provider': ['Data Management'],
    'Notifications': ['Notifications'],
    'Accessibility': ['Accessibility'],
    'Input Method': ['Input Method'],
    'Input': ['Input'],
    'OS': ['Other'],
    'Package Manager': ['Package Manager'],
    'WebView': ['WebView'],
    'Accounts': ['Accounts'],
    'Sensors': ['Sensors'],
    'Voice': ['Other'],
    'Text': ['UI Framework'],
    'Util': ['Other'],
    'Resources': ['Other'],
}

# Known direct type-level mappings
KNOWN_TYPE_MAPPINGS = {
    # Android type → (OH module, OH type)
    'Activity': ('@ohos.app.ability', 'UIAbility'),
    'Service': ('@ohos.app.ability', 'ServiceExtensionAbility'),
    'BroadcastReceiver': ('@ohos.commonEventManager', 'CommonEventSubscriber'),
    'ContentProvider': ('@ohos.data.dataShare', 'DataShareExtensionAbility'),
    'SharedPreferences': ('@ohos.data.preferences', 'Preferences'),
    'Intent': ('@ohos.app.ability', 'Want'),
    'SQLiteDatabase': ('@ohos.data.relationalStore', 'RdbStore'),
    'SQLiteOpenHelper': ('@ohos.data.relationalStore', 'RdbStore'),
    'ConnectivityManager': ('@ohos.net.connection', None),
    'WifiManager': ('@ohos.wifi', None),
    'BluetoothAdapter': ('@ohos.bluetooth.connection', None),
    'TelephonyManager': ('@ohos.telephony.call', None),
    'SmsManager': ('@ohos.telephony.sms', None),
    'MediaPlayer': ('@ohos.multimedia.media', 'AVPlayer'),
    'AudioManager': ('@ohos.multimedia.audio', 'AudioManager'),
    'NotificationManager': ('@ohos.notificationManager', None),
    'AlarmManager': ('@ohos.resourceschedule.workScheduler', None),
    'SensorManager': ('@ohos.sensor', None),
    'LocationManager': ('@ohos.geoLocationManager', None),
    'PackageManager': ('@ohos.bundle.bundleManager', None),
    'KeyguardManager': ('@ohos.screenLock', None),
    'PowerManager': ('@ohos.power', None),
    'Vibrator': ('@ohos.vibrator', None),
    'WebView': ('@ohos.web.webview', 'WebviewController'),
}

# Known method name mappings
KNOWN_METHOD_MAPPINGS = {
    'onCreate': 'onCreate',
    'onDestroy': 'onDestroy',
    'onStart': 'onForeground',
    'onStop': 'onBackground',
    'onResume': 'onForeground',
    'onPause': 'onBackground',
    'startActivity': 'startAbility',
    'startActivityForResult': 'startAbilityForResult',
    'finish': 'terminateSelf',
    'setContentView': 'build',  # paradigm shift
    'findViewById': None,  # no equivalent in declarative UI
    'getString': 'getStringSync',
    'getSystemService': None,  # different pattern
    'sendBroadcast': 'publish',
    'registerReceiver': 'subscribe',
    'unregisterReceiver': 'unsubscribe',
    'query': 'query',
    'insert': 'insert',
    'update': 'update',
    'delete': 'delete',
    'getSharedPreferences': 'getPreferences',
    'play': 'play',
    'pause': 'pause',
    'stop': 'stop',
    'start': 'start',
    'connect': 'connect',
    'disconnect': 'disconnect',
    'requestPermissions': 'requestPermissionsFromUser',
    'checkSelfPermission': 'checkAccessToken',
}


def compute_similarity(name1, name2):
    """Compute name similarity score (0.0 to 1.0)."""
    # Exact match
    if name1.lower() == name2.lower():
        return 1.0

    # Sequence similarity
    ratio = SequenceMatcher(None, name1.lower(), name2.lower()).ratio()

    # Bonus for shared tokens (camelCase splitting)
    tokens1 = set(re.findall(r'[A-Z][a-z]+|[a-z]+', name1))
    tokens2 = set(re.findall(r'[A-Z][a-z]+|[a-z]+', name2))
    if tokens1 and tokens2:
        token_overlap = len(tokens1 & tokens2) / max(len(tokens1), len(tokens2))
        ratio = max(ratio, token_overlap)

    return ratio


def score_to_compat(similarity, has_known_mapping=False):
    """Convert similarity score to compatibility score (1-10)."""
    if has_known_mapping:
        return min(10.0, similarity * 10 + 2)
    return max(1.0, similarity * 10)


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


def map_callable_api(android_api, oh_apis, oh_by_subsystem, oh_by_name, oh_callable_by_subsystem, oh_callable_by_name):
    """Map a callable Android API (method/constructor) to an OH API."""
    a_id, a_name, a_kind, a_sig, a_sub, a_type, a_pkg = android_api

    best_oh_id = None
    best_score = 0
    best_sim = 0
    is_known = False

    # Strategy 1: Known method name mapping
    if a_name in KNOWN_METHOD_MAPPINGS:
        oh_method = KNOWN_METHOD_MAPPINGS[a_name]
        if oh_method and oh_method.lower() in oh_callable_by_name:
            candidates = oh_callable_by_name[oh_method.lower()]
            if candidates:
                best_oh_id = candidates[0][0]
                best_score = 7.0
                is_known = True

    # Strategy 2: Known type mapping → search within that OH module
    if not is_known and a_type in KNOWN_TYPE_MAPPINGS:
        oh_module, oh_type = KNOWN_TYPE_MAPPINGS[a_type]
        for oh_api in oh_apis:
            if oh_api[6] == oh_module and is_callable(oh_api[2], 'oh'):
                sim = compute_similarity(a_name, oh_api[1])
                if sim > best_sim:
                    best_sim = sim
                    best_oh_id = oh_api[0]
                    best_score = score_to_compat(sim, True)

    # Strategy 3: Subsystem-based search (only match callable OH APIs)
    if best_score < 5.0 and a_sub:
        target_subs = SUBSYSTEM_MAPPING.get(a_sub, ['Other'])
        for target_sub in target_subs:
            if target_sub in oh_callable_by_subsystem:
                a_tokens = set(t.lower() for t in re.findall(r'[A-Z][a-z]+|[a-z]+', a_name))
                for oh_api in oh_callable_by_subsystem[target_sub]:
                    oh_tokens = set(t.lower() for t in re.findall(r'[A-Z][a-z]+|[a-z]+', oh_api[1]))
                    if a_tokens & oh_tokens:  # Must share at least one token
                        sim = compute_similarity(a_name, oh_api[1])
                        if sim > best_sim:
                            best_sim = sim
                            best_oh_id = oh_api[0]
                            best_score = score_to_compat(sim)

    # Strategy 4: Exact name match across all callable OH APIs
    if best_score < 5.0:
        if a_name.lower() in oh_callable_by_name:
            candidates = oh_callable_by_name[a_name.lower()]
            best_oh_id = candidates[0][0]
            best_score = 8.0
            best_sim = 1.0

    return best_oh_id, best_score, best_sim


def map_constant(android_api, oh_by_name):
    """Map a constant/field/enum_constant — ONLY exact name match allowed."""
    a_id, a_name, a_kind, a_sig, a_sub, a_type, a_pkg = android_api

    # Only map constants on exact name match
    if a_name.lower() in oh_by_name:
        candidates = oh_by_name[a_name.lower()]
        # Prefer matching constant-like OH entries (enum_value, macro)
        for c in candidates:
            if c[2] in OH_CONSTANT_KINDS:
                return c[0], 8.0, 1.0
        # Fall back to any exact match
        return candidates[0][0], 6.0, 1.0

    return None, 0, 0


def auto_map(db_path):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    # Get all Android APIs with their type and package info
    android_apis = c.execute("""
        SELECT a.id, a.name, a.kind, a.signature, a.subsystem,
               t.name as type_name, p.name as package_name
        FROM android_apis a
        JOIN android_types t ON a.type_id = t.id
        JOIN android_packages p ON t.package_id = p.id
    """).fetchall()

    # Get all OH APIs with their type and module info
    oh_apis = c.execute("""
        SELECT a.id, a.name, a.kind, a.signature, a.subsystem,
               COALESCE(t.name, '') as type_name, m.name as module_name
        FROM oh_apis a
        LEFT JOIN oh_types t ON a.type_id = t.id
        JOIN oh_modules m ON a.module_id = m.id
    """).fetchall()

    # Build OH API lookups — separate callable from all
    oh_by_subsystem = {}
    oh_callable_by_subsystem = {}
    oh_by_name = {}
    oh_callable_by_name = {}

    for api in oh_apis:
        sub = api[4] or 'Other'
        name = api[1].lower()
        kind = api[2]

        # All OH APIs by subsystem
        oh_by_subsystem.setdefault(sub, []).append(api)
        # All OH APIs by name
        oh_by_name.setdefault(name, []).append(api)

        # Callable OH APIs only
        if is_callable(kind, 'oh'):
            oh_callable_by_subsystem.setdefault(sub, []).append(api)
            oh_callable_by_name.setdefault(name, []).append(api)

    callable_count = sum(1 for a in android_apis if a[2] in ANDROID_CALLABLE_KINDS)
    constant_count = sum(1 for a in android_apis if a[2] in ANDROID_CONSTANT_KINDS)
    print(f"Android APIs: {len(android_apis)} total ({callable_count} callable, {constant_count} constants)")
    print(f"OH APIs: {len(oh_apis)} total")
    print(f"Auto-mapping...")

    mapped = 0
    unmapped = 0
    batch = []

    for i, android_api in enumerate(android_apis):
        a_id, a_name, a_kind, a_sig, a_sub, a_type, a_pkg = android_api

        if a_kind in ANDROID_CALLABLE_KINDS:
            # Callable API: full fuzzy matching
            best_oh_id, best_score, best_sim = map_callable_api(
                android_api, oh_apis, oh_by_subsystem, oh_by_name,
                oh_callable_by_subsystem, oh_callable_by_name
            )
        else:
            # Constant/field/enum: ONLY exact name match
            best_oh_id, best_score, best_sim = map_constant(android_api, oh_by_name)

        # Set minimum score for unmapped
        if best_score < 1.0:
            best_score = 1.0

        effort = effort_from_score(best_score)
        mtype = mapping_type_from_score(best_score)

        # Detect paradigm shifts (only for callable View/Widget APIs)
        paradigm_shift = False
        needs_ui_rewrite = False
        if a_sub in ('View', 'Widget') and a_kind == 'method':
            paradigm_shift = True
            needs_ui_rewrite = True
            if best_score > 3.0:
                best_score = 3.0
                effort = 'rewrite'
                mtype = 'composite'

        batch.append((
            a_id, best_oh_id, best_score, 0.5, mtype, effort,
            paradigm_shift, needs_ui_rewrite
        ))

        if best_oh_id:
            mapped += 1
        else:
            unmapped += 1

        if (i + 1) % 5000 == 0:
            print(f"  Processed {i+1}/{len(android_apis)}...")

    # Clear old mappings and insert new ones
    print(f"Clearing old mappings and inserting {len(batch)} new mappings...")
    c.execute("DELETE FROM api_mappings")
    c.executemany("""
        INSERT INTO api_mappings
        (android_api_id, oh_api_id, score, confidence, mapping_type, effort_level,
         paradigm_shift, needs_ui_rewrite)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, batch)

    # Update android_apis with scores
    print("Updating Android API scores...")
    c.execute("""
        UPDATE android_apis SET
            compat_score = (SELECT m.score FROM api_mappings m WHERE m.android_api_id = android_apis.id LIMIT 1),
            effort_level = (SELECT m.effort_level FROM api_mappings m WHERE m.android_api_id = android_apis.id LIMIT 1)
    """)

    # Populate subsystems table — count ONLY callable APIs for stats
    print("Building subsystem summary...")
    c.execute("DELETE FROM subsystems")
    c.execute("""
        INSERT INTO subsystems (name, api_count_android, api_count_oh, overall_score, coverage_pct)
        SELECT
            a.subsystem,
            COUNT(CASE WHEN a.kind IN ('method', 'constructor') THEN 1 END) as callable_count,
            0,
            AVG(CASE WHEN a.kind IN ('method', 'constructor') THEN COALESCE(a.compat_score, 1.0) END) as avg_score,
            CASE
                WHEN COUNT(CASE WHEN a.kind IN ('method', 'constructor') THEN 1 END) = 0 THEN 0
                ELSE 100.0 * COUNT(CASE WHEN a.kind IN ('method', 'constructor') AND a.compat_score >= 3 THEN 1 END)
                     / COUNT(CASE WHEN a.kind IN ('method', 'constructor') THEN 1 END)
            END as coverage
        FROM android_apis a
        WHERE a.subsystem IS NOT NULL
        GROUP BY a.subsystem
    """)

    # Update OH api counts per subsystem
    # Count only callable OH APIs that are actually mapped to each Android subsystem
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

    callable_mapped = sum(1 for b in batch if b[1] is not None and android_apis[batch.index(b)][2] in ANDROID_CALLABLE_KINDS)
    print(f"\nDone!")
    print(f"Total mapped: {mapped}, Unmapped: {unmapped}")
    print(f"Callable APIs mapped: {callable_mapped}/{callable_count}")

    # Print summary by subsystem
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
