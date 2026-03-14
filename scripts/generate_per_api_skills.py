#!/usr/bin/env python3
"""
Generate per-API skill files from api_compat.db.

Each file gives the AI agent everything it needs to shim one Android class:
- Method table with OH equivalents, scores, gaps
- Scenario classification
- Code examples from DB
- Pitfalls and test patterns
"""

import sqlite3
import os
import sys
from pathlib import Path
from collections import defaultdict

DB_PATH = os.path.join(os.path.dirname(__file__), '..', 'database', 'api_compat.db')
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), '..', 'skills', 'per-api')

# Already-shimmed classes (skip these)
ALREADY_SHIMMED = {
    'android.util.Log', 'android.os.Bundle', 'android.os.Build',
    'android.content.SharedPreferences', 'android.content.SharedPreferences.Editor',
    'android.content.Intent', 'android.content.Context',
    'android.content.ContentValues', 'android.content.BroadcastReceiver',
    'android.app.Activity', 'android.app.NotificationManager',
    'android.app.NotificationChannel', 'android.app.Notification',
    'android.app.Notification.Builder', 'android.app.AlarmManager',
    'android.app.PendingIntent', 'android.database.sqlite.SQLiteDatabase',
    'android.database.sqlite.SQLiteOpenHelper', 'android.database.Cursor',
    'android.database.CursorWrapper', 'android.database.SQLException',
    'android.net.Uri', 'android.widget.Toast', 'android.widget.TextView',
    'android.widget.Button', 'android.widget.EditText',
    'android.widget.ImageView', 'android.widget.LinearLayout',
    'android.widget.FrameLayout', 'android.widget.ScrollView',
    'android.widget.ListView', 'android.widget.CheckBox',
    'android.widget.Switch', 'android.widget.SeekBar',
    'android.widget.ProgressBar', 'android.view.View',
    'android.view.ViewGroup', 'android.view.Gravity',
    'android.view.LayoutInflater',
}

# Priority packages (app-critical)
PRIORITY_PACKAGES = [
    'android.app', 'android.content', 'android.os', 'android.database',
    'android.net', 'android.util', 'android.widget', 'android.view',
    'android.media', 'android.graphics', 'android.text', 'android.telephony',
    'android.bluetooth', 'android.hardware', 'android.location', 'android.provider',
]

# Skill doc mapping
SKILL_DOC_MAP = {
    'android.database': 'A2OH-DATA-LAYER.md',
    'android.content': 'A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md',
    'android.app': 'A2OH-LIFECYCLE.md',
    'android.os': 'A2OH-DEVICE-API.md',
    'android.net': 'A2OH-NETWORKING.md',
    'android.media': 'A2OH-MEDIA.md',
    'android.widget': 'A2OH-UI-REWRITE.md',
    'android.view': 'A2OH-UI-REWRITE.md',
    'android.graphics': 'A2OH-UI-REWRITE.md',
    'android.text': 'A2OH-UI-REWRITE.md',
    'android.telephony': 'A2OH-DEVICE-API.md',
    'android.bluetooth': 'A2OH-NETWORKING.md',
    'android.hardware': 'A2OH-DEVICE-API.md',
    'android.location': 'A2OH-DEVICE-API.md',
    'android.provider': 'A2OH-DATA-LAYER.md',
    'android.util': 'SHIM-INDEX.md',
}

def classify_scenario(apis):
    """Classify the dominant scenario for a class based on its API mappings."""
    type_counts = defaultdict(int)
    flags = {'needs_native': 0, 'needs_ui_rewrite': 0, 'paradigm_shift': 0}
    total = len(apis)
    for api in apis:
        type_counts[api['mapping_type']] += 1
        if api['needs_native']: flags['needs_native'] += 1
        if api['needs_ui_rewrite']: flags['needs_ui_rewrite'] += 1
        if api['paradigm_shift']: flags['paradigm_shift'] += 1

    avg_score = sum(a['score'] for a in apis) / max(total, 1)

    # UI paradigm shift dominates
    if flags['needs_ui_rewrite'] > total * 0.5:
        return 'S6', 'UI Paradigm Shift', 'ViewTree + ArkUI declarative rendering'
    # Async/threading gap
    if flags['paradigm_shift'] > total * 0.3 and not flags['needs_ui_rewrite'] > total * 0.3:
        return 'S7', 'Async/Threading Gap', 'Promise wrapping, Handler/Looper emulation'
    # Native bridge needed
    if flags['needs_native'] > total * 0.5:
        return 'S5', 'Native Bridge Required', 'JNI/FFI through Rust bridge'
    # Direct mapping
    if type_counts['direct'] > total * 0.5 and avg_score >= 8:
        return 'S1', 'Direct Mapping (Thin Wrapper)', 'Simple delegation to OHBridge'
    # Near mapping
    if (type_counts['direct'] + type_counts['near']) > total * 0.5 and avg_score >= 7:
        return 'S2', 'Signature Adaptation', 'Type conversion at boundary'
    # Composite
    if type_counts['composite'] > total * 0.3:
        return 'S4', 'Multi-API Composition', 'Multiple OH calls per Android call'
    # Partial
    if type_counts['partial'] > total * 0.3:
        return 'S3', 'Partial Coverage', 'Implement feasible methods, stub the rest'
    # No mapping
    if type_counts['none'] > total * 0.5:
        return 'S8', 'No Mapping (Stub)', 'Stub with UnsupportedOperationException or no-op'
    # Default based on score
    if avg_score >= 7:
        return 'S2', 'Signature Adaptation', 'Type conversion at boundary'
    elif avg_score >= 5:
        return 'S3', 'Partial Coverage', 'Implement feasible methods, stub the rest'
    else:
        return 'S8', 'No Mapping (Stub)', 'Stub with UnsupportedOperationException or no-op'


def get_skill_doc(package):
    for prefix, doc in SKILL_DOC_MAP.items():
        if package.startswith(prefix):
            return doc
    return 'SHIM-INDEX.md'


def generate_skill_file(fqn, apis, output_dir):
    """Generate a per-API skill markdown file."""
    package = fqn.rsplit('.', 1)[0]
    class_name = fqn.rsplit('.', 1)[1]
    safe_filename = fqn.replace('.', '_').replace('<', '').replace('>', '').replace(' ', '')

    # Classify
    scenario_id, scenario_name, scenario_strategy = classify_scenario(apis)

    # Stats
    total = len(apis)
    avg_score = sum(a['score'] for a in apis) / max(total, 1)
    direct_count = sum(1 for a in apis if a['mapping_type'] in ('direct', 'near'))
    partial_count = sum(1 for a in apis if a['mapping_type'] in ('partial', 'composite'))
    none_count = sum(1 for a in apis if a['mapping_type'] == 'none')
    native_count = sum(1 for a in apis if a['needs_native'])
    ui_count = sum(1 for a in apis if a['needs_ui_rewrite'])
    async_count = sum(1 for a in apis if a['paradigm_shift'])

    skill_doc = get_skill_doc(package)

    # Separate implementable vs stub APIs
    impl_apis = [a for a in apis if a['score'] >= 5]
    stub_apis = [a for a in apis if a['score'] < 5]

    lines = []
    lines.append(f'# SKILL: {fqn}')
    lines.append('')
    lines.append(f'> Auto-generated from api_compat.db. Use this as the primary reference when shimming `{fqn}`.')
    lines.append('')

    # Summary table
    lines.append('## Summary')
    lines.append('')
    lines.append(f'| Property | Value |')
    lines.append(f'|---|---|')
    lines.append(f'| **Class** | `{fqn}` |')
    lines.append(f'| **Package** | `{package}` |')
    lines.append(f'| **Total Methods** | {total} |')
    lines.append(f'| **Avg Score** | {avg_score:.1f} |')
    lines.append(f'| **Scenario** | {scenario_id}: {scenario_name} |')
    lines.append(f'| **Strategy** | {scenario_strategy} |')
    lines.append(f'| **Direct/Near** | {direct_count} ({direct_count*100//max(total,1)}%) |')
    lines.append(f'| **Partial/Composite** | {partial_count} ({partial_count*100//max(total,1)}%) |')
    lines.append(f'| **No Mapping** | {none_count} ({none_count*100//max(total,1)}%) |')
    lines.append(f'| **Needs Native Bridge** | {native_count} |')
    lines.append(f'| **Needs UI Rewrite** | {ui_count} |')
    lines.append(f'| **Has Async Gap** | {async_count} |')
    lines.append(f'| **Related Skill Doc** | `{skill_doc}` |')
    lines.append(f'| **Expected AI Iterations** | {estimate_iterations(scenario_id)} |')
    lines.append(f'| **Test Level** | {test_level(scenario_id)} |')
    lines.append('')

    # Implementable APIs table
    if impl_apis:
        lines.append(f'## Implementable APIs (score >= 5): {len(impl_apis)} methods')
        lines.append('')
        lines.append('| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |')
        lines.append('|---|---|---|---|---|---|---|')
        for a in sorted(impl_apis, key=lambda x: -x['score']):
            oh_name = a['oh_name'] or '—'
            oh_sig = a['oh_sig'] or '—'
            lines.append(f"| `{a['name']}` | `{a['signature'] or ''}` | {a['score']:.0f} | {a['mapping_type']} | {a['effort_level'] or '—'} | `{oh_name}` | `{oh_sig}` |")
        lines.append('')

    # Gap descriptions for implementable APIs
    gaps = [(a['name'], a['gap_description']) for a in impl_apis if a['gap_description']]
    if gaps:
        lines.append('## Gap Descriptions (per method)')
        lines.append('')
        for name, gap in gaps:
            lines.append(f'- **`{name}`**: {gap}')
        lines.append('')

    # Migration guides
    guides = [(a['name'], a['migration_guide']) for a in impl_apis if a['migration_guide']]
    if guides:
        lines.append('## Migration Guides (per method)')
        lines.append('')
        for name, guide in guides:
            lines.append(f'- **`{name}`**: {guide}')
        lines.append('')

    # Code examples from DB
    android_examples = [(a['name'], a['code_example_android']) for a in impl_apis if a['code_example_android']]
    oh_examples = [(a['name'], a['code_example_oh']) for a in impl_apis if a['code_example_oh']]

    if android_examples or oh_examples:
        lines.append('## Code Examples (from api_compat.db)')
        lines.append('')
        for name, code in android_examples[:5]:  # limit to avoid huge files
            lines.append(f'### Android: `{name}`')
            lines.append('```java')
            lines.append(code.strip())
            lines.append('```')
            lines.append('')
        for name, code in oh_examples[:5]:
            lines.append(f'### OpenHarmony: `{name}`')
            lines.append('```typescript')
            lines.append(code.strip())
            lines.append('```')
            lines.append('')

    # Stubbed APIs
    if stub_apis:
        lines.append(f'## Stub APIs (score < 5): {len(stub_apis)} methods')
        lines.append('')
        lines.append('These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.')
        lines.append('')
        lines.append('| Method | Score | Type | Stub Strategy |')
        lines.append('|---|---|---|---|')
        for a in sorted(stub_apis, key=lambda x: -x['score']):
            strategy = stub_strategy(a)
            lines.append(f"| `{a['name']}` | {a['score']:.0f} | {a['mapping_type']} | {strategy} |")
        lines.append('')

    # Scenario-specific instructions
    lines.append('## AI Agent Instructions')
    lines.append('')
    lines.append(f'**Scenario: {scenario_id} — {scenario_name}**')
    lines.append('')
    lines.append(scenario_instructions(scenario_id, fqn, impl_apis, stub_apis))
    lines.append('')

    # Dependencies
    lines.append('## Dependencies')
    lines.append('')
    lines.append(f'Check if these related classes are already shimmed before generating `{fqn}`:')
    lines.append('')
    deps = infer_dependencies(fqn, package)
    for dep in deps:
        shimmed = '(already shimmed)' if dep in ALREADY_SHIMMED else '(not yet shimmed)'
        lines.append(f'- `{dep}` {shimmed}')
    lines.append('')

    # Quality gates
    lines.append('## Quality Gates')
    lines.append('')
    lines.append(f'Before marking `{fqn}` as done:')
    lines.append('')
    lines.append(f'1. **Compilation**: `javac` succeeds with zero errors')
    lines.append(f'2. **API Surface**: All {total} public methods present (implemented or stubbed)')
    lines.append(f'3. **Test Coverage**: At least {len(impl_apis)} test methods for implemented APIs')
    lines.append(f'4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`')
    lines.append(f'5. **Mock Consistency**: Every OHBridge method has both declaration and mock')
    lines.append('')

    # Write file
    filepath = os.path.join(output_dir, f'{safe_filename}.md')
    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

    return filepath


def estimate_iterations(scenario_id):
    return {'S1': '1', 'S2': '1-2', 'S3': '2-3', 'S4': '2-3',
            'S5': '2-4', 'S6': '3-5', 'S7': '3-5', 'S8': '1'}[scenario_id]


def test_level(scenario_id):
    return {
        'S1': 'Level 1 (Mock only)',
        'S2': 'Level 1 (Mock only)',
        'S3': 'Level 1 + Level 2 (Headless)',
        'S4': 'Level 1 + Level 2 (Headless)',
        'S5': 'Level 1 (Mock) + Level 3 (QEMU for native)',
        'S6': 'Level 1 (Mock) + Level 2 (Headless ArkUI)',
        'S7': 'Level 1 (Mock with concurrency tests)',
        'S8': 'Level 1 (Mock only)',
    }[scenario_id]


def stub_strategy(api):
    name = api['name'].lower()
    if any(w in name for w in ['create', 'open', 'init', 'start', 'connect']):
        return 'Return dummy instance / no-op'
    if any(w in name for w in ['destroy', 'close', 'stop', 'release', 'disconnect']):
        return 'No-op'
    if any(w in name for w in ['get', 'is', 'has', 'can', 'find', 'query', 'read']):
        return 'Return safe default (null/false/0/empty)'
    if any(w in name for w in ['set', 'put', 'write', 'add', 'remove', 'update']):
        return 'Log warning + no-op'
    if any(w in name for w in ['on', 'register', 'listen', 'subscribe']):
        return 'Store callback, never fire'
    return 'throw UnsupportedOperationException'


def scenario_instructions(scenario_id, fqn, impl_apis, stub_apis):
    instructions = {
        'S1': f"""1. Create Java shim at `shim/java/{fqn.replace('.', '/')}.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types""",

        'S2': f"""1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly""",

        'S3': f"""1. Implement {len(impl_apis)} methods that have score >= 5
2. Stub {len(stub_apis)} methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably""",

        'S4': f"""1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns""",

        'S5': f"""1. Create Java shim with `OHBridge.nativeXxx()` for each method
2. Create Rust bridge module at `shim/bridge/rust/src/{fqn.split('.')[-1].lower()}.rs`
3. Handle JNI type marshalling carefully (see Type Marshalling in AI Agent Playbook)
4. Mock returns plausible values for JVM tests
5. Mark for Level 3 (QEMU) testing to validate real native calls""",

        'S6': f"""1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage""",

        'S7': f"""1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock""",

        'S8': f"""1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions""",
    }
    return instructions.get(scenario_id, instructions['S3'])


def infer_dependencies(fqn, package):
    """Infer likely dependencies based on package."""
    deps = []
    dep_map = {
        'android.app.Activity': ['android.content.Context', 'android.content.Intent', 'android.os.Bundle'],
        'android.app.Service': ['android.content.Context', 'android.content.Intent'],
        'android.app.NotificationManager': ['android.app.Notification', 'android.app.NotificationChannel'],
        'android.app.AlertDialog.Builder': ['android.content.Context', 'android.app.Dialog'],
        'android.app.Dialog': ['android.content.Context', 'android.view.View'],
        'android.content.ContentResolver': ['android.net.Uri', 'android.content.ContentValues', 'android.database.Cursor'],
        'android.content.ContextWrapper': ['android.content.Context', 'android.content.Intent'],
        'android.content.IntentFilter': ['android.content.Intent'],
        'android.database.AbstractCursor': ['android.database.Cursor'],
        'android.media.MediaPlayer': ['android.content.Context', 'android.net.Uri'],
        'android.media.AudioManager': ['android.content.Context'],
        'android.media.AudioTrack': ['android.media.AudioManager'],
        'android.net.ConnectivityManager': ['android.content.Context'],
        'android.os.Handler': ['android.os.Looper', 'android.os.Message'],
        'android.os.Parcel': ['android.os.Bundle'],
        'android.telephony.TelephonyManager': ['android.content.Context'],
        'android.telephony.SmsManager': ['android.app.PendingIntent'],
        'android.location.Location': [],
        'android.location.Address': [],
        'android.widget.AutoCompleteTextView': ['android.widget.EditText', 'android.widget.TextView'],
        'android.widget.PopupWindow': ['android.view.View', 'android.content.Context'],
        'android.widget.Toolbar': ['android.view.View', 'android.view.ViewGroup'],
        'android.widget.RemoteViews': ['android.view.View', 'android.content.Intent'],
        'android.view.Window': ['android.view.View', 'android.content.Context'],
        'android.view.MotionEvent': ['android.view.View'],
        'android.view.KeyEvent': [],
        'android.graphics.Paint': [],
        'android.graphics.Canvas': ['android.graphics.Paint', 'android.graphics.Bitmap'],
        'android.graphics.Bitmap': [],
        'android.graphics.Color': [],
        'android.graphics.Path': [],
        'android.graphics.Matrix': [],
        'android.graphics.RectF': [],
        'android.graphics.Typeface': [],
    }
    if fqn in dep_map:
        deps = dep_map[fqn]
    else:
        # Generic: depend on Context if in app/content/media/telephony/net
        if package in ('android.app', 'android.content', 'android.media',
                       'android.telephony', 'android.net', 'android.location'):
            deps.append('android.content.Context')
        if package in ('android.widget', 'android.view'):
            deps.extend(['android.view.View', 'android.content.Context'])
    return [d for d in deps if d != fqn]


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    db = sqlite3.connect(DB_PATH)
    db.row_factory = sqlite3.Row

    # Get all classes with >= 3 methods in priority packages
    classes = db.execute("""
        SELECT p.name || '.' || t.full_name AS fqn,
               COUNT(*) AS api_count
        FROM api_mappings m
        JOIN android_apis a ON m.android_api_id = a.id
        JOIN android_types t ON a.type_id = t.id
        JOIN android_packages p ON t.package_id = p.id
        WHERE a.kind IN ('method','constructor')
          AND p.name IN ({})
        GROUP BY fqn
        HAVING api_count >= 3
        ORDER BY api_count DESC
    """.format(','.join(f"'{p}'" for p in PRIORITY_PACKAGES))).fetchall()

    generated = 0
    skipped_shimmed = 0
    skipped_small = 0

    for row in classes:
        fqn = row['fqn']

        # Skip already shimmed
        if fqn in ALREADY_SHIMMED:
            skipped_shimmed += 1
            continue

        # Skip generic types with weird names
        if any(c in fqn for c in ['<', '>', '$']):
            # Clean up generic type params but keep the class
            clean_fqn = fqn.split('<')[0].split('$')[0]
            if clean_fqn in ALREADY_SHIMMED:
                skipped_shimmed += 1
                continue

        # Get all APIs for this class, preferring curated known_mappings over auto api_mappings
        apis = db.execute("""
            SELECT a.name, a.signature, a.kind,
                   COALESCE(k.score_override, m.score) AS score,
                   CASE WHEN k.id IS NOT NULL THEN
                       CASE WHEN k.score_override >= 8 THEN 'direct'
                            WHEN k.score_override >= 6 THEN 'near'
                            WHEN k.score_override >= 4 THEN 'partial'
                            WHEN k.score_override >= 2 THEN 'composite'
                            ELSE 'none' END
                   ELSE m.mapping_type END AS mapping_type,
                   m.effort_level,
                   COALESCE(k.notes, m.gap_description) AS gap_description,
                   m.migration_guide,
                   m.code_example_android, m.code_example_oh,
                   COALESCE(m.needs_native, 0) AS needs_native,
                   COALESCE(m.needs_ui_rewrite, 0) AS needs_ui_rewrite,
                   COALESCE(k.paradigm_shift, m.paradigm_shift, 0) AS paradigm_shift,
                   COALESCE(k.oh_method, oa.name) AS oh_name,
                   COALESCE(k.oh_module || '.' || k.oh_type, oa.signature) AS oh_sig,
                   COALESCE(k.oh_module, om.name) AS oh_module
            FROM api_mappings m
            JOIN android_apis a ON m.android_api_id = a.id
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_types ot ON oa.type_id = ot.id
            LEFT JOIN oh_modules om ON ot.module_id = om.id
            LEFT JOIN known_mappings k ON k.android_type = t.name
                AND k.android_method = a.name
                AND k.android_package = p.name
            WHERE p.name || '.' || t.full_name = ?
              AND a.kind IN ('method','constructor')
            ORDER BY COALESCE(k.score_override, m.score) DESC
        """, (fqn,)).fetchall()

        if len(apis) < 3:
            skipped_small += 1
            continue

        api_dicts = [dict(a) for a in apis]
        filepath = generate_skill_file(fqn, api_dicts, OUTPUT_DIR)
        generated += 1

    # Generate index
    index_lines = [
        '# Per-API Skill Files Index',
        '',
        f'Generated: {generated} skill files from api_compat.db',
        f'Skipped: {skipped_shimmed} already shimmed, {skipped_small} too small (<3 methods)',
        '',
        '## Files',
        '',
    ]

    files = sorted(os.listdir(OUTPUT_DIR))
    for f in files:
        if f.endswith('.md') and f != 'INDEX.md':
            fqn = f.replace('.md', '').replace('_', '.')
            index_lines.append(f'- [{fqn}]({f})')

    with open(os.path.join(OUTPUT_DIR, 'INDEX.md'), 'w') as f:
        f.write('\n'.join(index_lines))

    print(f'Generated {generated} per-API skill files in {OUTPUT_DIR}')
    print(f'Skipped: {skipped_shimmed} already shimmed, {skipped_small} too small')
    db.close()


if __name__ == '__main__':
    main()
