#!/usr/bin/env python3
"""Import OpenHarmony C/NDK APIs from header files into SQLite database."""

import re
import sqlite3
import os
import glob

NDK_DIR = os.path.expanduser("~/openharmony/interface/sdk_c/")
DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

SUBSYSTEM_MAP = {
    'multimedia': 'Multimedia',
    'graphic': 'Graphics',
    'network': 'Networking',
    'security': 'Security',
    'hiviewdfx': 'Diagnostics',
    'sensors': 'Sensors',
    'drivers': 'Drivers',
    'arkui': 'UI Framework',
    'startup': 'Startup',
    'global': 'Globalization',
    'ai': 'AI',
    'ark_runtime': 'Runtime',
    'web': 'WebView',
    'commonlibrary': 'Common',
    'bundlemanager': 'Package Manager',
    'resourceschedule': 'Resource Schedule',
    'distributeddatamgr': 'Data Management',
    'third_party': 'Third Party',
}


def get_ndk_subsystem(filepath):
    """Get subsystem from file path."""
    rel = os.path.relpath(filepath, NDK_DIR)
    first_dir = rel.split(os.sep)[0]
    return SUBSYSTEM_MAP.get(first_dir, 'Other')


def parse_header_file(filepath):
    """Parse a C header file and extract API elements."""
    with open(filepath, 'r', errors='replace') as f:
        content = f.read()

    result = {
        'functions': [],
        'structs': [],
        'enums': [],
        'typedefs': [],
        'macros': [],
        'syscap': None,
        'since': None,
        'library': None,
    }

    # Extract metadata from Doxygen
    syscap_match = re.search(r'@syscap\s+(SystemCapability\.\S+)', content)
    if syscap_match:
        result['syscap'] = syscap_match.group(1)

    since_matches = re.findall(r'@since\s+(\d+)', content)
    if since_matches:
        result['since'] = min(int(v) for v in since_matches)

    lib_match = re.search(r'@library\s+(\S+)', content)
    if lib_match:
        result['library'] = lib_match.group(1)

    # Strip comments for cleaner parsing
    # Keep Doxygen separately for description extraction
    clean = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    clean = re.sub(r'//[^\n]*', '', clean)

    # Parse function declarations
    # Pattern: return_type [*] FUNC_NAME(params);
    for m in re.finditer(
        r'(?:^|\n)\s*(?:OH_\w+|[A-Z][A-Za-z_]*_[A-Z][A-Za-z_]*|[a-z]\w+)\s*'
        r'(?:\*\s*)?'
        r'((?:OH_|[A-Z])\w+)\s*\(([^)]*)\)\s*;',
        clean
    ):
        # This is too greedy, let's use a better approach
        pass

    # Better function parsing - look for typical NDK function patterns
    for m in re.finditer(
        r'\b([\w*\s]+?)\s+(OH_\w+|OHOS_\w+|[A-Z][a-z]\w*_\w+)\s*\(([^)]*)\)\s*;',
        clean
    ):
        ret_type = m.group(1).strip()
        func_name = m.group(2).strip()
        params = m.group(3).strip()

        # Skip if ret_type looks like a macro or typedef
        if ret_type.startswith('#') or ret_type.startswith('typedef'):
            continue

        result['functions'].append({
            'name': func_name,
            'return_type': ret_type,
            'params': params,
            'signature': f"{ret_type} {func_name}({params})",
        })

    # Also catch simpler function patterns
    for m in re.finditer(
        r'^([\w\s\*]+?)\s+(\w+)\s*\(([^)]*)\)\s*;',
        clean, re.MULTILINE
    ):
        ret_type = m.group(1).strip()
        func_name = m.group(2).strip()
        params = m.group(3).strip()

        # Filter: must look like a function declaration
        if any(kw in ret_type for kw in ['typedef', '#define', 'struct', 'enum', 'union']):
            continue
        if func_name.startswith('_') or func_name.isupper():
            continue
        if not any(f['name'] == func_name for f in result['functions']):
            result['functions'].append({
                'name': func_name,
                'return_type': ret_type,
                'params': params,
                'signature': f"{ret_type} {func_name}({params})",
            })

    # Parse typedef struct
    for m in re.finditer(r'typedef\s+struct\s+(\w+)', clean):
        name = m.group(1)
        result['structs'].append({'name': name, 'signature': f"typedef struct {name}"})

    # Parse typedef enum
    for m in re.finditer(r'typedef\s+enum\s*(?:\w+)?\s*\{([^}]+)\}\s*(\w+)\s*;', clean, re.DOTALL):
        body = m.group(1)
        enum_name = m.group(2)
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

    # Parse simple typedefs
    for m in re.finditer(r'typedef\s+(?!struct|enum|union)(\S+(?:\s*\*)?)\s+(\w+)\s*;', clean):
        result['typedefs'].append({
            'name': m.group(2),
            'type': m.group(1),
            'signature': f"typedef {m.group(1)} {m.group(2)}",
        })

    # Parse #define macros (skip include guards)
    for m in re.finditer(r'^#define\s+(\w+)(?:\s+(.+?))?$', clean, re.MULTILINE):
        name = m.group(1)
        value = m.group(2) or ''
        if name.endswith('_H') or name.endswith('_H_') or name.startswith('_'):
            continue
        result['macros'].append({
            'name': name,
            'value': value.strip(),
            'signature': f"#define {name} {value.strip()}",
        })

    return result


def import_to_db(db_path, ndk_dir):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    module_count = 0
    type_count = 0
    api_count = 0

    header_files = glob.glob(os.path.join(ndk_dir, '**', '*.h'), recursive=True)
    print(f"Found {len(header_files)} header files")

    for filepath in sorted(header_files):
        try:
            parsed = parse_header_file(filepath)
        except Exception as e:
            print(f"  Error parsing {filepath}: {e}")
            continue

        # Skip files with no APIs
        total = len(parsed['functions']) + len(parsed['structs']) + len(parsed['enums']) + len(parsed['typedefs'])
        if total == 0:
            continue

        basename = os.path.basename(filepath).replace('.h', '')
        subsystem = get_ndk_subsystem(filepath)
        rel_path = os.path.relpath(filepath, os.path.expanduser("~/openharmony/"))
        module_name = f"ndk/{basename}"

        # Insert module
        c.execute("""
            INSERT OR IGNORE INTO oh_modules
            (name, sdk_type, subsystem, file_path, syscap, since_version)
            VALUES (?, 'ndk', ?, ?, ?, ?)
        """, (module_name, subsystem, rel_path, parsed['syscap'], parsed['since']))

        module_id = c.execute(
            "SELECT id FROM oh_modules WHERE name=?", (module_name,)
        ).fetchone()[0]
        module_count += 1

        # Import functions
        for func in parsed['functions']:
            c.execute("""
                INSERT INTO oh_apis
                (type_id, module_id, kind, name, signature, return_type, params, subsystem)
                VALUES (NULL, ?, 'c_function', ?, ?, ?, ?, ?)
            """, (module_id, func['name'], func['signature'],
                  func['return_type'], func['params'], subsystem))
            api_count += 1

        # Import structs
        for struct in parsed['structs']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'struct', ?, ?)
            """, (module_id, struct['name'], parsed['syscap'], parsed['since']))
            type_count += 1

        # Import enums
        for enum in parsed['enums']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'enum', ?, ?)
            """, (module_id, enum['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, enum['name'])
            ).fetchone()
            if type_id:
                type_id = type_id[0]
                type_count += 1
                for val in enum['values']:
                    c.execute("""
                        INSERT INTO oh_apis
                        (type_id, module_id, kind, name, signature, subsystem)
                        VALUES (?, ?, 'enum_value', ?, ?, ?)
                    """, (type_id, module_id, val['name'], val['signature'], subsystem))
                    api_count += 1

        # Import typedefs
        for td in parsed['typedefs']:
            c.execute("""
                INSERT INTO oh_apis
                (type_id, module_id, kind, name, signature, subsystem)
                VALUES (NULL, ?, 'typedef', ?, ?, ?)
            """, (module_id, td['name'], td['signature'], subsystem))
            api_count += 1

        # Import macros (only meaningful ones, skip pure value macros)
        for macro in parsed['macros']:
            if len(macro['name']) > 2:  # Skip single-letter macros
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, subsystem)
                    VALUES (NULL, ?, 'macro', ?, ?, ?)
                """, (module_id, macro['name'], macro['signature'], subsystem))
                api_count += 1

    conn.commit()
    print(f"Imported: {module_count} modules, {type_count} types, {api_count} APIs")
    conn.close()
    return module_count, type_count, api_count


def main():
    print(f"Importing OpenHarmony NDK APIs from {NDK_DIR}...")
    modules, types, apis = import_to_db(DB_FILE, NDK_DIR)
    print(f"\nDone!")
    print(f"  Modules: {modules}")
    print(f"  Types: {types}")
    print(f"  APIs: {apis}")


if __name__ == '__main__':
    main()
