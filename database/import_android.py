#!/usr/bin/env python3
"""Import Android 11 APIs from current.txt into SQLite database."""

import re
import sqlite3
import json
import os

API_FILE = os.path.expanduser("~/aosp-android-11/frameworks/base/api/current.txt")
DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

# Subsystem classification for Android packages
SUBSYSTEM_MAP = {
    'android': 'Core',
    'android.annotation': 'Core',
    'android.app': 'App Framework',
    'android.app.admin': 'App Framework',
    'android.app.assist': 'App Framework',
    'android.app.backup': 'App Framework',
    'android.app.blob': 'App Framework',
    'android.app.job': 'App Framework',
    'android.app.role': 'App Framework',
    'android.app.slice': 'App Framework',
    'android.app.usage': 'App Framework',
    'android.appwidget': 'App Framework',
    'android.accessibilityservice': 'Accessibility',
    'android.accounts': 'Accounts',
    'android.animation': 'Animation',
    'android.bluetooth': 'Bluetooth',
    'android.bluetooth.le': 'Bluetooth',
    'android.companion': 'Companion',
    'android.content': 'Content',
    'android.content.pm': 'Package Manager',
    'android.content.res': 'Resources',
    'android.content.res.loader': 'Resources',
    'android.database': 'Database',
    'android.database.sqlite': 'Database',
    'android.drm': 'DRM',
    'android.gesture': 'Input',
    'android.graphics': 'Graphics',
    'android.graphics.drawable': 'Graphics',
    'android.graphics.drawable.shapes': 'Graphics',
    'android.graphics.fonts': 'Graphics',
    'android.graphics.pdf': 'Graphics',
    'android.graphics.text': 'Graphics',
    'android.hardware': 'Hardware',
    'android.hardware.biometrics': 'Biometrics',
    'android.hardware.camera2': 'Camera',
    'android.hardware.camera2.params': 'Camera',
    'android.hardware.display': 'Display',
    'android.hardware.fingerprint': 'Biometrics',
    'android.hardware.input': 'Input',
    'android.hardware.usb': 'USB',
    'android.icu.lang': 'ICU',
    'android.icu.math': 'ICU',
    'android.icu.number': 'ICU',
    'android.icu.text': 'ICU',
    'android.icu.util': 'ICU',
    'android.inputmethodservice': 'Input Method',
    'android.location': 'Location',
    'android.media': 'Media',
    'android.media.audiofx': 'Media',
    'android.media.browse': 'Media',
    'android.media.effect': 'Media',
    'android.media.midi': 'Media',
    'android.media.projection': 'Media',
    'android.media.session': 'Media',
    'android.media.tv': 'Media',
    'android.mtp': 'Media',
    'android.net': 'Networking',
    'android.net.http': 'Networking',
    'android.net.nsd': 'Networking',
    'android.net.rtp': 'Networking',
    'android.net.sip': 'Networking',
    'android.net.ssl': 'Networking',
    'android.net.wifi': 'WiFi',
    'android.net.wifi.aware': 'WiFi',
    'android.net.wifi.hotspot2': 'WiFi',
    'android.net.wifi.hotspot2.omadm': 'WiFi',
    'android.net.wifi.hotspot2.pps': 'WiFi',
    'android.net.wifi.p2p': 'WiFi',
    'android.net.wifi.p2p.nsd': 'WiFi',
    'android.net.wifi.rtt': 'WiFi',
    'android.nfc': 'NFC',
    'android.nfc.cardemulation': 'NFC',
    'android.nfc.tech': 'NFC',
    'android.opengl': 'Graphics',
    'android.os': 'OS',
    'android.os.health': 'OS',
    'android.os.storage': 'Storage',
    'android.os.strictmode': 'OS',
    'android.preference': 'UI',
    'android.print': 'Print',
    'android.print.pdf': 'Print',
    'android.printservice': 'Print',
    'android.provider': 'Provider',
    'android.renderscript': 'Graphics',
    'android.sax': 'XML',
    'android.se.omapi': 'Security',
    'android.security': 'Security',
    'android.security.identity': 'Security',
    'android.security.keystore': 'Security',
    'android.service.autofill': 'Service',
    'android.service.carrier': 'Telephony',
    'android.service.chooser': 'Service',
    'android.service.controls': 'Service',
    'android.service.controls.actions': 'Service',
    'android.service.controls.templates': 'Service',
    'android.service.dreams': 'Service',
    'android.service.media': 'Media',
    'android.service.notification': 'Notifications',
    'android.service.quickaccesswallet': 'Service',
    'android.service.quicksettings': 'Service',
    'android.service.restrictions': 'Service',
    'android.service.textservice': 'Input Method',
    'android.service.voice': 'Voice',
    'android.service.vr': 'VR',
    'android.service.wallpaper': 'Service',
    'android.speech': 'Voice',
    'android.speech.tts': 'Voice',
    'android.system': 'OS',
    'android.telecom': 'Telephony',
    'android.telephony': 'Telephony',
    'android.telephony.cdma': 'Telephony',
    'android.telephony.data': 'Telephony',
    'android.telephony.emergency': 'Telephony',
    'android.telephony.euicc': 'Telephony',
    'android.telephony.gsm': 'Telephony',
    'android.telephony.ims': 'Telephony',
    'android.telephony.ims.feature': 'Telephony',
    'android.telephony.mbms': 'Telephony',
    'android.text': 'Text',
    'android.text.format': 'Text',
    'android.text.method': 'Text',
    'android.text.style': 'Text',
    'android.text.util': 'Text',
    'android.transition': 'Animation',
    'android.util': 'Util',
    'android.util.proto': 'Util',
    'android.view': 'View',
    'android.view.accessibility': 'Accessibility',
    'android.view.animation': 'Animation',
    'android.view.autofill': 'Autofill',
    'android.view.contentcapture': 'Content',
    'android.view.inputmethod': 'Input Method',
    'android.view.inspector': 'View',
    'android.view.textclassifier': 'Text',
    'android.view.textservice': 'Input Method',
    'android.webkit': 'WebView',
    'android.widget': 'Widget',
    'android.widget.inline': 'Widget',
}


def get_subsystem(pkg_name):
    if pkg_name in SUBSYSTEM_MAP:
        return SUBSYSTEM_MAP[pkg_name]
    # Fallback: use first two segments
    if pkg_name.startswith('java.') or pkg_name.startswith('javax.'):
        return 'Java Standard'
    if pkg_name.startswith('dalvik.'):
        return 'Dalvik'
    if pkg_name.startswith('org.'):
        return 'Third Party'
    # Try prefix match
    parts = pkg_name.split('.')
    for i in range(len(parts), 0, -1):
        prefix = '.'.join(parts[:i])
        if prefix in SUBSYSTEM_MAP:
            return SUBSYSTEM_MAP[prefix]
    return 'Other'


def extract_method_name(signature):
    """Extract method name from signature like 'static void foo(int x)'"""
    # Remove generics for easier parsing
    clean = re.sub(r'<[^>]*>', '', signature)
    # Look for method name pattern: word followed by (
    m = re.search(r'(\w+)\s*\(', clean)
    if m:
        return m.group(1)
    # For fields: last word before = or end
    parts = clean.split()
    if parts:
        return parts[-1].split('=')[0].strip()
    return signature


def extract_return_type(signature):
    """Extract return type from method signature."""
    clean = re.sub(r'<[^>]*>', '', signature)
    m = re.match(r'(?:static\s+)?(?:final\s+)?(?:abstract\s+)?(?:default\s+)?(?:synchronized\s+)?([\w.@\[\]]+(?:<[^>]*>)?)\s+\w+\s*\(', signature)
    if m:
        return m.group(1)
    return None


def parse_and_import(db_path, api_file):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    current_pkg = None
    current_pkg_id = None
    current_type = None
    current_type_id = None

    api_count = 0
    type_count = 0
    pkg_count = 0

    with open(api_file, 'r') as f:
        for line_no, line in enumerate(f, 1):
            stripped = line.strip()
            if not stripped or stripped.startswith("//"):
                continue

            # Package
            pkg_match = re.match(r'^package\s+([\w.]+)\s*\{', stripped)
            if pkg_match:
                current_pkg = pkg_match.group(1)
                subsystem = get_subsystem(current_pkg)
                c.execute(
                    "INSERT OR IGNORE INTO android_packages (name, subsystem) VALUES (?, ?)",
                    (current_pkg, subsystem)
                )
                current_pkg_id = c.execute(
                    "SELECT id FROM android_packages WHERE name=?", (current_pkg,)
                ).fetchone()[0]
                current_type = None
                current_type_id = None
                pkg_count += 1
                continue

            # Type declaration
            type_match = re.match(
                r'^((?:@\S+\s+)*)'
                r'(public|protected)\s+'
                r'(static\s+)?'
                r'(final\s+)?'
                r'(abstract\s+)?'
                r'(class|interface|enum)\s+'
                r'([\w.<>,\s?]+?)'
                r'(?:\s+extends\s+([\w.<>,\s?]+?))?'
                r'(?:\s+implements\s+([\w.<>,\s?]+?))?\s*\{',
                stripped
            )
            if type_match and current_pkg_id:
                annotations = type_match.group(1).strip()
                is_static = bool(type_match.group(3))
                is_final = bool(type_match.group(4))
                is_abstract = bool(type_match.group(5))
                kind = type_match.group(6)
                full_name = type_match.group(7).strip()
                extends = type_match.group(8)
                implements = type_match.group(9)

                simple_name = re.sub(r'<.*>', '', full_name).strip()

                c.execute("""
                    INSERT OR IGNORE INTO android_types
                    (package_id, name, full_name, kind, is_abstract, is_final, is_static,
                     is_deprecated, extends_type, implements, annotations)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (
                    current_pkg_id, simple_name, full_name, kind,
                    is_abstract, is_final, is_static,
                    '@Deprecated' in annotations,
                    extends.strip() if extends else None,
                    implements.strip() if implements else None,
                    annotations if annotations else None
                ))
                current_type_id = c.execute(
                    "SELECT id FROM android_types WHERE package_id=? AND name=?",
                    (current_pkg_id, simple_name)
                ).fetchone()[0]
                current_type = simple_name
                type_count += 1
                continue

            if current_type_id is None:
                continue

            subsystem = get_subsystem(current_pkg) if current_pkg else 'Other'

            # Constructor
            ctor_match = re.match(r'^((?:@\S+\s+)*)ctor\s+(public|protected)\s+(.*?);', stripped)
            if ctor_match:
                annotations = ctor_match.group(1).strip()
                sig = ctor_match.group(3).strip()
                name = extract_method_name(sig) or current_type
                c.execute("""
                    INSERT INTO android_apis (type_id, kind, name, signature, is_deprecated, subsystem)
                    VALUES (?, 'constructor', ?, ?, ?, ?)
                """, (current_type_id, name, sig, '@Deprecated' in annotations, subsystem))
                api_count += 1
                continue

            # Method
            method_match = re.match(r'^((?:@\S+\s+)*)method\s+(public|protected)\s+(.*?);', stripped)
            if method_match:
                annotations = method_match.group(1).strip()
                sig = method_match.group(3).strip()
                name = extract_method_name(sig)
                ret = extract_return_type(sig)
                is_static = 'static ' in sig
                c.execute("""
                    INSERT INTO android_apis
                    (type_id, kind, name, signature, return_type, is_deprecated, is_static, subsystem)
                    VALUES (?, 'method', ?, ?, ?, ?, ?, ?)
                """, (current_type_id, name, sig, ret, '@Deprecated' in annotations, is_static, subsystem))
                api_count += 1
                continue

            # Field
            field_match = re.match(r'^((?:@\S+\s+)*)field\s+(public|protected)\s+(.*?);', stripped)
            if field_match:
                annotations = field_match.group(1).strip()
                sig = field_match.group(3).strip()
                # Extract field name: last identifier before = or end
                parts = sig.split('=')[0].strip().split()
                name = parts[-1] if parts else sig
                is_static = 'static ' in sig
                c.execute("""
                    INSERT INTO android_apis
                    (type_id, kind, name, signature, is_deprecated, is_static, subsystem)
                    VALUES (?, 'field', ?, ?, ?, ?, ?)
                """, (current_type_id, name, sig, '@Deprecated' in annotations, is_static, subsystem))
                api_count += 1
                continue

            # Enum constant
            enum_match = re.match(r'^((?:@\S+\s+)*)enum_constant\s+(public|protected)\s+(.*?);', stripped)
            if enum_match:
                annotations = enum_match.group(1).strip()
                sig = enum_match.group(3).strip()
                parts = sig.split()
                name = parts[-1] if parts else sig
                c.execute("""
                    INSERT INTO android_apis
                    (type_id, kind, name, signature, is_deprecated, subsystem)
                    VALUES (?, 'enum_constant', ?, ?, ?, ?)
                """, (current_type_id, name, sig, '@Deprecated' in annotations, subsystem))
                api_count += 1
                continue

    conn.commit()

    # Build FTS index
    print("Building FTS index for Android APIs...")
    c.execute("""
        INSERT INTO android_apis_fts(rowid, name, signature, subsystem, description)
        SELECT id, name, signature, subsystem, COALESCE(compat_summary, '')
        FROM android_apis
    """)
    conn.commit()

    print(f"Imported: {pkg_count} packages, {type_count} types, {api_count} APIs")
    conn.close()
    return pkg_count, type_count, api_count


def main():
    # Initialize DB
    schema_file = os.path.join(os.path.dirname(__file__), 'schema.sql')
    conn = sqlite3.connect(DB_FILE)
    with open(schema_file) as f:
        conn.executescript(f.read())
    conn.close()

    print(f"Importing Android APIs from {API_FILE}...")
    pkgs, types, apis = parse_and_import(DB_FILE, API_FILE)
    print(f"\nDone! Database: {DB_FILE}")
    print(f"  Packages: {pkgs}")
    print(f"  Types: {types}")
    print(f"  APIs: {apis}")


if __name__ == '__main__':
    main()
