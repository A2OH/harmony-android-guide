#!/usr/bin/env python3
"""Direct, aggressive fix for all remaining compile errors."""

import os
import re
import subprocess

SHIM_ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "shim", "java")


def read_file(path):
    with open(path) as f:
        return f.read()


def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content)


def get_java_files():
    files = []
    for root, dirs, fnames in os.walk(SHIM_ROOT):
        for f in fnames:
            if f.endswith('.java'):
                files.append(os.path.join(root, f))
    return files


def compile():
    files = get_java_files()
    os.makedirs("/tmp/shim-compile", exist_ok=True)
    result = subprocess.run(
        ["javac", "-sourcepath", SHIM_ROOT, "-d", "/tmp/shim-compile",
         "-Xmaxerrs", "2000"] + files,
        capture_output=True, text=True, timeout=300
    )
    return result.stderr


def pass1_remove_abstract_from_classes():
    """Remove abstract from ALL class declarations (they're stubs with bodies)."""
    count = 0
    for f in get_java_files():
        content = read_file(f)
        if 'public abstract class ' in content:
            new = content.replace('public abstract class ', 'public class ')
            if new != content:
                write_file(f, new)
                count += 1
    print(f"Pass 1: Removed abstract from {count} classes")


def pass2_remove_abstract_from_methods():
    """Remove abstract keyword from methods that have bodies."""
    count = 0
    for f in get_java_files():
        content = read_file(f)
        orig = content
        # Remove 'abstract' from methods that have a body { ... }
        # Match: public abstract TYPE METHOD(...) { ... }
        content = re.sub(
            r'(public\s+)abstract(\s+(?:static\s+)?[\w<>\[\]?,\s]+\s+\w+\s*\([^)]*\)\s*(?:throws\s+[\w,\s]+)?\s*\{)',
            r'\1\2',
            content
        )
        if content != orig:
            write_file(f, content)
            count += 1
    print(f"Pass 2: Removed abstract from methods in {count} files")


def pass3_add_no_arg_constructors():
    """Parse errors and add missing no-arg constructors."""
    stderr = compile()

    # Find "constructor X in class X cannot be applied to given types"
    # and "no suitable constructor found for X(no arguments)"
    pattern1 = re.compile(r'constructor (\w+) in class (\w+) cannot be applied')
    pattern2 = re.compile(r'no suitable constructor found for (\w+)\(no arguments\)')

    needed_classes = set()
    for line in stderr.split('\n'):
        m = pattern1.search(line)
        if m:
            needed_classes.add(m.group(1))
        m = pattern2.search(line)
        if m:
            needed_classes.add(m.group(1))

    if not needed_classes:
        print("Pass 3: No constructor fixes needed")
        return

    print(f"Pass 3: Need no-arg constructors for: {needed_classes}")

    for f in get_java_files():
        content = read_file(f)
        for cls in needed_classes:
            # Check if this file defines the class and doesn't have a no-arg constructor
            class_pat = re.compile(rf'(?:public|protected)\s+(?:static\s+)?class\s+{re.escape(cls)}\b')
            if class_pat.search(content) and f'{cls}()' not in content:
                # Find the class opening brace
                m = class_pat.search(content)
                if m:
                    brace = content.index('{', m.start())
                    ctor = f'\n    public {cls}() {{}}'
                    content = content[:brace+1] + ctor + content[brace+1:]
                    write_file(f, content)
                    print(f"  Added {cls}() to {os.path.relpath(f, SHIM_ROOT)}")


def pass4_fix_return_types():
    """Fix all return type mismatches."""
    count = 0
    for f in get_java_files():
        content = read_file(f)
        orig = content

        # char -> return null => return '\0'
        content = re.sub(
            r'(\bchar\s+\w+\([^)]*\)\s*\{)\s*return null;\s*\}',
            r"\1 return '\0'; }",
            content
        )
        # byte[] -> return 0
        content = re.sub(
            r'(\bbyte\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
            r"\1 return new byte[0]; }",
            content
        )
        # int[] -> return 0
        content = re.sub(
            r'(\bint\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
            r"\1 return new int[0]; }",
            content
        )
        # long[] -> return 0
        content = re.sub(
            r'(\blong\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0L?;\s*\}',
            r"\1 return new long[0]; }",
            content
        )
        # float[] -> return 0
        content = re.sub(
            r'(\bfloat\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0f?;\s*\}',
            r"\1 return new float[0]; }",
            content
        )
        # double[] -> return 0
        content = re.sub(
            r'(\bdouble\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0[.0]*;\s*\}',
            r"\1 return new double[0]; }",
            content
        )
        # boolean[] -> return false
        content = re.sub(
            r'(\bboolean\[\]\s+\w+\([^)]*\)\s*\{)\s*return false;\s*\}',
            r"\1 return new boolean[0]; }",
            content
        )
        # char[] returning '\0'
        content = re.sub(
            r"(\bchar\[\]\s+\w+\([^)]*\)\s*\{)\s*return '\\0';\s*\}",
            r"\1 return new char[0]; }",
            content
        )

        if content != orig:
            write_file(f, content)
            count += 1
    print(f"Pass 4: Fixed return types in {count} files")


def pass5_fix_cannot_find_symbol():
    """Parse 'cannot find symbol' errors and fix them."""
    stderr = compile()

    # Collect missing symbols per file
    missing = {}  # filepath -> set of missing symbols
    current_file = None

    for line in stderr.split('\n'):
        if ': error:' in line and 'cannot find symbol' in line:
            current_file = line.split(':')[0]
        elif current_file and 'symbol:' in line:
            m = re.match(r'\s*symbol:\s*(?:class|variable|method)\s+(\w+)', line)
            if m:
                sym = m.group(1)
                if current_file not in missing:
                    missing[current_file] = set()
                missing[current_file].add(sym)
            current_file = None

    if missing:
        # For now, most "cannot find symbol" are for variables/methods on Object
        # or corrupted names. Let's just count them.
        all_syms = set()
        for syms in missing.values():
            all_syms.update(syms)
        print(f"Pass 5: {len(missing)} files with missing symbols: {all_syms}")
    else:
        print("Pass 5: No missing symbols")


def pass6_fix_interfaces_with_bodies():
    """Fix methods in interfaces that should not have bodies."""
    stderr = compile()

    # "abstract methods cannot have a body" in files that are still interfaces
    affected = set()
    for line in stderr.split('\n'):
        if 'abstract methods cannot have a body' in line:
            affected.add(line.split(':')[0])

    for filepath in affected:
        if not os.path.exists(filepath):
            continue
        content = read_file(filepath)
        if 'interface ' in content.split('{')[0]:
            # This is actually an interface - remove method bodies
            content = re.sub(
                r'((?:public\s+)?(?:static\s+)?[\w<>\[\]?,\s]+\s+\w+\s*\([^)]*\))\s*\{[^}]*\}',
                r'\1;',
                content
            )
            write_file(filepath, content)
        else:
            # It's a class with leftover abstract methods - remove abstract keyword
            content = re.sub(r'\babstract\b\s+', '', content)
            # Re-add it for class declaration if needed
            # Actually just let it be non-abstract
            write_file(filepath, content)

    print(f"Pass 6: Fixed {len(affected)} files with abstract-body conflicts")


def pass7_fix_specific_files():
    """Handle individual file issues."""

    # MbmsGroupCallSessionCallback -> interface
    path = os.path.join(SHIM_ROOT, "android/telephony/mbms/MbmsGroupCallSessionCallback.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public class MbmsGroupCallSessionCallback' in content:
            content = content.replace('public class MbmsGroupCallSessionCallback',
                                     'public interface MbmsGroupCallSessionCallback')
            # Remove bodies from methods
            lines = content.split('\n')
            new_lines = []
            in_interface = False
            for line in lines:
                if 'interface MbmsGroupCallSessionCallback' in line:
                    in_interface = True
                if in_interface and re.match(r'\s+public\s+\w+\s+\w+\(', line):
                    # Remove body
                    line = re.sub(r'\s*\{[^}]*\}', ';', line)
                    line = line.replace('public ', '')  # interface methods don't need public
                new_lines.append(line)
            write_file(path, '\n'.join(new_lines))

    # Fix Object toString() -> String toString()
    for f in get_java_files():
        content = read_file(f)
        if 'public Object toString()' in content:
            content = content.replace('public Object toString()', 'public String toString()')
            write_file(f, content)

    # Fix duplicate format methods in MessageFormat
    path = os.path.join(SHIM_ROOT, "android/icu/text/MessageFormat.java")
    if os.path.exists(path):
        content = read_file(path)
        sig = 'public Object format(Object p0, Object p1, Object p2)'
        count = content.count(sig)
        if count > 1:
            idx1 = content.index(sig)
            end1 = content.index('}', idx1) + 1
            idx2 = content.index(sig, end1)
            end2 = content.index('}', idx2) + 1
            content = content[:idx2] + content[end2:]
            write_file(path, content)

    # Fix recursive constructor calls
    for f in get_java_files():
        content = read_file(f)
        new_content = re.sub(
            r'(public\s+\w+\(\)\s*\{)\s*this\(\);\s*\}',
            r'\1 }',
            content
        )
        if new_content != content:
            write_file(f, new_content)

    # BroadcastReceiver - fix onReceive params
    path = os.path.join(SHIM_ROOT, "android/content/BroadcastReceiver.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onReceive(Object' in content:
            content = content.replace('onReceive(Object p0, Object p1)',
                                     'onReceive(android.content.Context p0, android.content.Intent p1)')
            write_file(path, content)

    # TransformationMethod
    path = os.path.join(SHIM_ROOT, "android/text/method/TransformationMethod.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onFocusChanged(Object' in content:
            content = content.replace(
                'onFocusChanged(Object p0, CharSequence p1, boolean p2, int p3, Object p4)',
                'onFocusChanged(android.view.View p0, CharSequence p1, boolean p2, int p3, android.graphics.Rect p4)')
            write_file(path, content)

    # CharacterStyle
    path = os.path.join(SHIM_ROOT, "android/text/style/CharacterStyle.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'updateDrawState(Object' in content:
            content = content.replace('updateDrawState(Object p0)',
                                     'updateDrawState(android.text.TextPaint p0)')
            write_file(path, content)

    # Parcelable
    path = os.path.join(SHIM_ROOT, "android/os/Parcelable.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'writeToParcel(Object' in content:
            content = content.replace('writeToParcel(Object p0, int p1)',
                                     'writeToParcel(android.os.Parcel p0, int p1)')
            write_file(path, content)

    # BluetoothProfile
    path = os.path.join(SHIM_ROOT, "android/bluetooth/BluetoothProfile.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'Object getConnectedDevices' in content:
            content = content.replace('Object getConnectedDevices()',
                                     'java.util.List<BluetoothDevice> getConnectedDevices()')
            write_file(path, content)

    # Cursor - setNotificationUris
    path = os.path.join(SHIM_ROOT, "android/database/Cursor.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'setNotificationUris' not in content:
            content = content.replace(
                'android.os.Bundle respond(android.os.Bundle extras);',
                'android.os.Bundle respond(android.os.Bundle extras);\n    default void setNotificationUris(android.content.ContentResolver cr, java.util.List<android.net.Uri> uris) {}')
            write_file(path, content)

    # NetworkSpecifier
    path = os.path.join(SHIM_ROOT, "android/net/NetworkSpecifier.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'canBeSatisfiedBy' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public boolean canBeSatisfiedBy(NetworkSpecifier other) { return false; }\n}\n'
            write_file(path, content)

    # CookieSyncManager
    path = os.path.join(SHIM_ROOT, "android/webkit/CookieSyncManager.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'implements Runnable' in content and 'void run()' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void run() {}\n}\n'
            write_file(path, content)

    # Add missing methods to subclasses
    _add_method_if_missing("android/telephony/CellInfoCdma.java",
        "public android.telephony.CellSignalStrength getCellSignalStrength() { return null; }")
    _add_method_if_missing("android/telephony/CellInfoTdscdma.java",
        "public android.telephony.CellSignalStrength getCellSignalStrength() { return null; }")
    _add_method_if_missing("android/telephony/CellInfoNr.java",
        "public android.telephony.CellSignalStrength getCellSignalStrength() { return null; }")
    _add_method_if_missing("android/telephony/NeighboringCellInfo.java",
        "public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }")
    _add_method_if_missing("android/bluetooth/BluetoothHealthAppConfiguration.java",
        "public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }")
    _add_method_if_missing("android/bluetooth/BluetoothHidDevice.java",
        "public java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }")
    _add_method_if_missing("android/bluetooth/BluetoothHearingAid.java",
        "public java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }")
    _add_method_if_missing("android/bluetooth/BluetoothHealth.java",
        "public int getConnectionState(android.bluetooth.BluetoothDevice device) { return 0; }\n    public java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }")
    _add_method_if_missing("android/service/chooser/ChooserTarget.java",
        "public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }")
    _add_method_if_missing("android/service/controls/templates/ControlButton.java",
        "public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }")
    _add_method_if_missing("android/text/style/SuggestionSpan.java",
        "public void updateDrawState(android.text.TextPaint tp) {}")
    _add_method_if_missing("android/text/method/TimeKeyListener.java",
        "public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}")
    _add_method_if_missing("android/text/method/DialerKeyListener.java",
        "public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}")
    _add_method_if_missing("android/text/method/DateTimeKeyListener.java",
        "public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}")
    _add_method_if_missing("android/text/method/DateKeyListener.java",
        "public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}")
    _add_method_if_missing("android/text/method/SingleLineTransformationMethod.java",
        "public void onFocusChanged(android.view.View v, CharSequence s, boolean f, int d, android.graphics.Rect r) {}")
    _add_method_if_missing("android/text/method/HideReturnsTransformationMethod.java",
        "public void onFocusChanged(android.view.View v, CharSequence s, boolean f, int d, android.graphics.Rect r) {}")
    _add_method_if_missing("android/telephony/mbms/MbmsDownloadReceiver.java",
        "public void onReceive(android.content.Context ctx, android.content.Intent intent) {}")
    _add_method_if_missing("android/app/admin/DelegatedAdminReceiver.java",
        "public void onReceive(android.content.Context ctx, android.content.Intent intent) {}")

    # CellSignalStrength subclasses
    for cls in ["CellSignalStrengthTdscdma", "CellSignalStrengthNr", "CellSignalStrengthCdma"]:
        _add_method_if_missing(f"android/telephony/{cls}.java",
            "@Override public int hashCode() { return 0; }\n    @Override public boolean equals(Object o) { return false; }\n    @Override public String toString() { return \"\"; }")

    # IdentityCredentialException
    path = os.path.join(SHIM_ROOT, "android/security/identity/IdentityCredentialException.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'IdentityCredentialException()' not in content:
            # Find opening brace of class
            m = re.search(r'class IdentityCredentialException[^{]*\{', content)
            if m:
                idx = m.end()
                content = content[:idx] + '\n    public IdentityCredentialException() {}\n    public IdentityCredentialException(String msg) { super(msg); }' + content[idx:]
                write_file(path, content)

    # MediaDrmException
    path = os.path.join(SHIM_ROOT, "android/media/MediaDrmException.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'MediaDrmException()' not in content:
            m = re.search(r'class MediaDrmException[^{]*\{', content)
            if m:
                idx = m.end()
                content = content[:idx] + '\n    public MediaDrmException() {}\n    public MediaDrmException(String msg) { super(msg); }' + content[idx:]
                write_file(path, content)

    print("Pass 7: Fixed specific files")


def _add_method_if_missing(rel_path, method_code):
    """Add method to end of class if not present."""
    path = os.path.join(SHIM_ROOT, rel_path)
    if not os.path.exists(path):
        return
    content = read_file(path)
    # Check if first method name is already present
    method_name = re.search(r'\b(\w+)\s*\(', method_code)
    if method_name and method_name.group(1) in content:
        return
    idx = content.rindex('}')
    content = content[:idx] + f'    {method_code}\n' + content[idx:]
    write_file(path, content)


def main():
    print("=== Aggressive fix pass ===\n")

    pass1_remove_abstract_from_classes()
    pass2_remove_abstract_from_methods()
    pass4_fix_return_types()
    pass7_fix_specific_files()
    pass3_add_no_arg_constructors()

    # Run iteratively
    for iteration in range(5):
        print(f"\n--- Iteration {iteration} ---")
        pass3_add_no_arg_constructors()
        pass6_fix_interfaces_with_bodies()

        stderr = compile()
        error_count = stderr.count(': error:')
        error_files = set()
        for line in stderr.split('\n'):
            if ': error:' in line:
                error_files.add(line.split(':')[0])

        print(f"Errors: {error_count}, Error files: {len(error_files)}, Total: {len(get_java_files())}")

        if error_count == 0:
            print("\nCLEAN COMPILE!")
            break

        # Show error summary
        error_types = {}
        for line in stderr.split('\n'):
            if ': error:' in line:
                msg = re.sub(r'^.*: error: ', '', line)
                error_types[msg] = error_types.get(msg, 0) + 1
        for msg, count in sorted(error_types.items(), key=lambda x: -x[1])[:10]:
            print(f"  {count}x {msg}")


if __name__ == "__main__":
    main()
