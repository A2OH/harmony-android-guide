#!/usr/bin/env python3
"""Import OpenHarmony JS/TS SDK APIs from .d.ts files into SQLite database."""

import re
import sqlite3
import json
import os
import glob

API_DIR = os.path.expanduser("~/openharmony/interface/sdk-js/api/")
DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

# Subsystem classification for OH modules
SUBSYSTEM_MAP = {
    'app': 'App Framework',
    'ability': 'App Framework',
    'application': 'App Framework',
    'bundle': 'Package Manager',
    'arkui': 'UI Framework',
    'data': 'Data Management',
    'file': 'File System',
    'net': 'Networking',
    'multimedia': 'Multimedia',
    'telephony': 'Telephony',
    'bluetooth': 'Bluetooth',
    'wifi': 'WiFi',
    'nfc': 'NFC',
    'sensor': 'Sensors',
    'vibrator': 'Sensors',
    'security': 'Security',
    'userIAM': 'Security',
    'account': 'Accounts',
    'notification': 'Notifications',
    'enterprise': 'Enterprise',
    'graphics': 'Graphics',
    'multimodalInput': 'Input',
    'inputMethod': 'Input Method',
    'accessibility': 'Accessibility',
    'ai': 'AI',
    'distributedHardware': 'Distributed',
    'resourceschedule': 'Resource Schedule',
    'advertising': 'Advertising',
}


def get_oh_subsystem(module_name):
    """Classify OH module into subsystem."""
    # module_name like "@ohos.net.http" or "@ohos.multimedia.media"
    clean = module_name.replace('@ohos.', '').replace('@system.', '')
    first_segment = clean.split('.')[0]
    return SUBSYSTEM_MAP.get(first_segment, 'Other')


def parse_dts_file(filepath):
    """Parse a .d.ts file and extract API elements."""
    with open(filepath, 'r', errors='replace') as f:
        content = f.read()

    result = {
        'module_name': None,
        'kit': None,
        'syscap': None,
        'since': None,
        'interfaces': [],
        'classes': [],
        'enums': [],
        'functions': [],
        'type_aliases': [],
    }

    # Extract module name from filename
    basename = os.path.basename(filepath)
    if basename.startswith('@ohos.') or basename.startswith('@system.'):
        result['module_name'] = basename.replace('.d.ts', '').replace('.d.ets', '')
    else:
        result['module_name'] = basename.replace('.d.ts', '').replace('.d.ets', '')

    # Extract kit from JSDoc
    kit_match = re.search(r'@kit\s+(\S+)', content)
    if kit_match:
        result['kit'] = kit_match.group(1)

    # Extract syscap
    syscap_match = re.search(r'@syscap\s+(SystemCapability\.\S+)', content)
    if syscap_match:
        result['syscap'] = syscap_match.group(1)

    # Extract since version
    since_matches = re.findall(r'@since\s+(\d+)', content)
    if since_matches:
        result['since'] = min(int(v) for v in since_matches)

    # Parse exported interfaces
    for m in re.finditer(r'export\s+interface\s+(\w+)(?:<[^>]*>)?\s*(?:extends\s+[^{]+)?\{([^}]*(?:\{[^}]*\}[^}]*)*)\}', content, re.DOTALL):
        iface_name = m.group(1)
        body = m.group(2)
        methods = []
        props = []

        # Parse methods: name(params): returnType
        for mm in re.finditer(r'(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);', body):
            methods.append({
                'name': mm.group(1),
                'params': mm.group(2).strip(),
                'return_type': mm.group(3).strip(),
                'signature': f"{mm.group(1)}({mm.group(2).strip()}): {mm.group(3).strip()}",
            })

        # Parse properties: name?: type
        for pm in re.finditer(r'(readonly\s+)?(\w+)(\?)?:\s*([^;]+);', body):
            if pm.group(2) not in [m['name'] for m in methods]:  # skip methods already parsed
                props.append({
                    'name': pm.group(2),
                    'type': pm.group(4).strip(),
                    'readonly': bool(pm.group(1)),
                    'optional': bool(pm.group(3)),
                    'signature': f"{'readonly ' if pm.group(1) else ''}{pm.group(2)}{'?' if pm.group(3) else ''}: {pm.group(4).strip()}",
                })

        result['interfaces'].append({
            'name': iface_name,
            'methods': methods,
            'properties': props,
        })

    # Parse exported classes
    for m in re.finditer(r'export\s+class\s+(\w+)(?:<[^>]*>)?\s*(?:extends\s+\S+)?\s*(?:implements\s+[^{]+)?\{([^}]*(?:\{[^}]*\}[^}]*)*)\}', content, re.DOTALL):
        cls_name = m.group(1)
        body = m.group(2)
        methods = []

        for mm in re.finditer(r'(?:static\s+)?(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);', body):
            methods.append({
                'name': mm.group(1),
                'params': mm.group(2).strip(),
                'return_type': mm.group(3).strip(),
                'signature': f"{mm.group(1)}({mm.group(2).strip()}): {mm.group(3).strip()}",
            })

        result['classes'].append({
            'name': cls_name,
            'methods': methods,
        })

    # Parse exported enums
    for m in re.finditer(r'export\s+enum\s+(\w+)\s*\{([^}]+)\}', content):
        enum_name = m.group(1)
        body = m.group(2)
        values = []

        for vm in re.finditer(r'(\w+)\s*=\s*([^,\n]+)', body):
            values.append({
                'name': vm.group(1),
                'value': vm.group(2).strip(),
                'signature': f"{vm.group(1)} = {vm.group(2).strip()}",
            })

        result['enums'].append({
            'name': enum_name,
            'values': values,
        })

    # Parse exported functions
    for m in re.finditer(r'export\s+function\s+(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);', content):
        result['functions'].append({
            'name': m.group(1),
            'params': m.group(2).strip(),
            'return_type': m.group(3).strip(),
            'signature': f"{m.group(1)}({m.group(2).strip()}): {m.group(3).strip()}",
        })

    # Parse type aliases
    for m in re.finditer(r'export\s+type\s+(\w+)(?:<[^>]*>)?\s*=\s*([^;]+);', content):
        result['type_aliases'].append({
            'name': m.group(1),
            'definition': m.group(2).strip(),
            'signature': f"type {m.group(1)} = {m.group(2).strip()}",
        })

    # Also parse namespace-level declarations
    for ns_match in re.finditer(r'declare\s+namespace\s+(\w+)\s*\{', content):
        ns_name = ns_match.group(1)
        # Find the namespace body (simplified — just grab what we can)
        start = ns_match.end()
        # Parse functions inside namespace
        # Use a slice of content from namespace start
        ns_body = content[start:start+50000]  # Reasonable limit

        for m in re.finditer(r'function\s+(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;{]+);', ns_body):
            fname = m.group(1)
            if not any(f['name'] == fname for f in result['functions']):
                result['functions'].append({
                    'name': fname,
                    'params': m.group(2).strip(),
                    'return_type': m.group(3).strip(),
                    'signature': f"{fname}({m.group(2).strip()}): {m.group(3).strip()}",
                })

        for m in re.finditer(r'interface\s+(\w+)(?:<[^>]*>)?\s*(?:extends\s+[^{]+)?\{([^}]*(?:\{[^}]*\}[^}]*)*)\}', ns_body, re.DOTALL):
            iface_name = m.group(1)
            if not any(i['name'] == iface_name for i in result['interfaces']):
                body = m.group(2)
                methods = []
                props = []
                for mm in re.finditer(r'(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);', body):
                    methods.append({
                        'name': mm.group(1),
                        'params': mm.group(2).strip(),
                        'return_type': mm.group(3).strip(),
                        'signature': f"{mm.group(1)}({mm.group(2).strip()}): {mm.group(3).strip()}",
                    })
                for pm in re.finditer(r'(readonly\s+)?(\w+)(\?)?:\s*([^;]+);', body):
                    if pm.group(2) not in [m['name'] for m in methods]:
                        props.append({
                            'name': pm.group(2),
                            'type': pm.group(4).strip(),
                            'readonly': bool(pm.group(1)),
                            'optional': bool(pm.group(3)),
                            'signature': f"{'readonly ' if pm.group(1) else ''}{pm.group(2)}{'?' if pm.group(3) else ''}: {pm.group(4).strip()}",
                        })
                result['interfaces'].append({
                    'name': iface_name,
                    'methods': methods,
                    'properties': props,
                })

        for m in re.finditer(r'enum\s+(\w+)\s*\{([^}]+)\}', ns_body):
            enum_name = m.group(1)
            if not any(e['name'] == enum_name for e in result['enums']):
                body = m.group(2)
                values = []
                for vm in re.finditer(r'(\w+)\s*=\s*([^,\n]+)', body):
                    values.append({
                        'name': vm.group(1),
                        'value': vm.group(2).strip(),
                        'signature': f"{vm.group(1)} = {vm.group(2).strip()}",
                    })
                result['enums'].append({
                    'name': enum_name,
                    'values': values,
                })

    return result


def import_to_db(db_path, api_dir):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    module_count = 0
    type_count = 0
    api_count = 0

    # Find all .d.ts and .d.ets files
    dts_files = glob.glob(os.path.join(api_dir, '*.d.ts'))
    dts_files += glob.glob(os.path.join(api_dir, '*.d.ets'))
    # Also check subdirectories
    dts_files += glob.glob(os.path.join(api_dir, '**', '*.d.ts'), recursive=True)
    dts_files += glob.glob(os.path.join(api_dir, '**', '*.d.ets'), recursive=True)
    # Deduplicate
    dts_files = sorted(set(dts_files))

    print(f"Found {len(dts_files)} declaration files")

    for filepath in dts_files:
        try:
            parsed = parse_dts_file(filepath)
        except Exception as e:
            print(f"  Error parsing {filepath}: {e}")
            continue

        module_name = parsed['module_name']
        if not module_name:
            continue

        subsystem = get_oh_subsystem(module_name)
        rel_path = os.path.relpath(filepath, os.path.expanduser("~/openharmony/"))

        # Insert module
        c.execute("""
            INSERT OR IGNORE INTO oh_modules
            (name, sdk_type, subsystem, file_path, kit, syscap, since_version)
            VALUES (?, 'js', ?, ?, ?, ?, ?)
        """, (module_name, subsystem, rel_path, parsed['kit'], parsed['syscap'], parsed['since']))

        module_id = c.execute(
            "SELECT id FROM oh_modules WHERE name=?", (module_name,)
        ).fetchone()[0]
        module_count += 1

        # Import interfaces
        for iface in parsed['interfaces']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'interface', ?, ?)
            """, (module_id, iface['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, iface['name'])
            ).fetchone()[0]
            type_count += 1

            for method in iface['methods']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type, params, subsystem)
                    VALUES (?, ?, 'method', ?, ?, ?, ?, ?)
                """, (type_id, module_id, method['name'], method['signature'],
                      method['return_type'], method['params'], subsystem))
                api_count += 1

            for prop in iface['properties']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type,
                     is_readonly, is_optional, subsystem)
                    VALUES (?, ?, 'property', ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, prop['name'], prop['signature'],
                      prop['type'], prop['readonly'], prop['optional'], subsystem))
                api_count += 1

        # Import classes
        for cls in parsed['classes']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'class', ?, ?)
            """, (module_id, cls['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, cls['name'])
            ).fetchone()[0]
            type_count += 1

            for method in cls['methods']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type, params, subsystem)
                    VALUES (?, ?, 'method', ?, ?, ?, ?, ?)
                """, (type_id, module_id, method['name'], method['signature'],
                      method['return_type'], method['params'], subsystem))
                api_count += 1

        # Import enums
        for enum in parsed['enums']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'enum', ?, ?)
            """, (module_id, enum['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, enum['name'])
            ).fetchone()[0]
            type_count += 1

            for val in enum['values']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, subsystem)
                    VALUES (?, ?, 'enum_value', ?, ?, ?)
                """, (type_id, module_id, val['name'], val['signature'], subsystem))
                api_count += 1

        # Import standalone functions
        for func in parsed['functions']:
            c.execute("""
                INSERT INTO oh_apis
                (type_id, module_id, kind, name, signature, return_type, params, subsystem)
                VALUES (NULL, ?, 'function', ?, ?, ?, ?, ?)
            """, (module_id, func['name'], func['signature'],
                  func['return_type'], func['params'], subsystem))
            api_count += 1

        # Import type aliases
        for ta in parsed['type_aliases']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'type', ?, ?)
            """, (module_id, ta['name'], parsed['syscap'], parsed['since']))
            type_count += 1

    conn.commit()

    # Build FTS index
    print("Building FTS index for OH APIs...")
    c.execute("""
        INSERT INTO oh_apis_fts(rowid, name, signature, subsystem, description)
        SELECT id, name, signature, subsystem, COALESCE(description, '')
        FROM oh_apis
    """)
    conn.commit()

    print(f"Imported: {module_count} modules, {type_count} types, {api_count} APIs")
    conn.close()
    return module_count, type_count, api_count


def main():
    print(f"Importing OpenHarmony JS/TS APIs from {API_DIR}...")
    modules, types, apis = import_to_db(DB_FILE, API_DIR)
    print(f"\nDone!")
    print(f"  Modules: {modules}")
    print(f"  Types: {types}")
    print(f"  APIs: {apis}")


if __name__ == '__main__':
    main()
