#!/usr/bin/env python3
"""Robust fix for remaining shim compile errors - handles abstract, constructors, types."""

import os
import re
import subprocess
import sys

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")


def read_file(path):
    with open(path) as f:
        return f.read()


def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content)


def get_all_java_files():
    files = []
    for root, dirs, fnames in os.walk(SHIM_ROOT):
        for f in fnames:
            if f.endswith('.java'):
                files.append(os.path.join(root, f))
    return files


def compile_and_get_errors():
    files = get_all_java_files()
    result = subprocess.run(
        ["javac", "-sourcepath", SHIM_ROOT, "-d", "/tmp/shim-fix-iter",
         "-Xmaxerrs", "2000"] + files,
        capture_output=True, text=True, timeout=300
    )
    return result.stderr


def parse_errors(stderr):
    """Parse javac error output into structured data."""
    errors = []
    lines = stderr.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i]
        if ': error:' in line:
            parts = line.split(': error:', 1)
            file_line = parts[0]
            msg = parts[1].strip()
            filepath = file_line.rsplit(':', 1)[0]
            try:
                lineno = int(file_line.rsplit(':', 1)[1])
            except (ValueError, IndexError):
                lineno = 0

            # Collect context lines
            context = []
            i += 1
            while i < len(lines) and ': error:' not in lines[i]:
                context.append(lines[i])
                i += 1

            errors.append({
                'file': filepath,
                'line': lineno,
                'msg': msg,
                'context': '\n'.join(context),
            })
        else:
            i += 1
    return errors


def fix_abstract_with_body(filepath):
    """Remove 'abstract' from class declaration so methods with bodies compile."""
    content = read_file(filepath)
    content = re.sub(r'public abstract class ', 'public class ', content)
    write_file(filepath, content)


def add_no_arg_constructor(filepath, class_name, super_call=""):
    """Add a no-arg constructor to a class."""
    content = read_file(filepath)
    if f'{class_name}()' in content:
        return  # Already has one

    # Find class declaration
    patterns = [
        f'public class {class_name}',
        f'public abstract class {class_name}',
    ]
    for pat in patterns:
        if pat in content:
            # Find the opening brace
            idx = content.index(pat)
            brace_idx = content.index('{', idx)
            insert_point = brace_idx + 1
            if super_call:
                ctor = f'\n    public {class_name}() {{ {super_call} }}'
            else:
                ctor = f'\n    public {class_name}() {{}}'
            content = content[:insert_point] + ctor + content[insert_point:]
            write_file(filepath, content)
            return


def fix_constructor_errors():
    """Fix all 'constructor X cannot be applied to given types' errors."""
    # Map of class -> file path and what super constructor it needs
    needed = {
        "AudioEffect": ("android/media/audiofx/AudioEffect.java", ""),
        "ViewGroup": ("android/view/ViewGroup.java", ""),
        "View": ("android/view/View.java", ""),
        "EGLObjectHandle": ("android/opengl/EGLObjectHandle.java", ""),
        "BaseDexClassLoader": ("dalvik/system/BaseDexClassLoader.java", ""),
        "Handler": ("android/os/Handler.java", ""),
        "NumberFormat": ("android/icu/text/NumberFormat.java", ""),
        "CursorAdapter": ("android/widget/CursorAdapter.java", ""),
        "CursorTreeAdapter": ("android/widget/CursorTreeAdapter.java", ""),
        "RSRuntimeException": ("android/renderscript/RSRuntimeException.java", ""),
        "AudioCodec": ("android/net/rtp/AudioCodec.java", ""),
        "Rational": ("android/util/Rational.java", ""),
        "BitmapRegionDecoder": ("android/graphics/BitmapRegionDecoder.java", ""),
        "Collator": ("android/icu/text/Collator.java", ""),
        "MeasureUnit": ("android/icu/util/MeasureUnit.java", ""),
        "TextView": ("android/widget/TextView.java", ""),
        "ActionProvider": ("android/view/ActionProvider.java", ""),
        "ColorSpace": ("android/graphics/ColorSpace.java", ""),
        "LinkAddress": ("android/net/LinkAddress.java", ""),
    }

    for class_name, (rel_path, super_call) in needed.items():
        path = os.path.join(SHIM_ROOT, rel_path)
        if os.path.exists(path):
            add_no_arg_constructor(path, class_name, super_call)

    # Handle inner classes specially
    # PrintAttributes.MediaSize, Resolution, Margins
    path = os.path.join(SHIM_ROOT, "android/print/PrintAttributes.java")
    if os.path.exists(path):
        content = read_file(path)
        for inner in ["MediaSize", "Resolution", "Margins"]:
            if f'class {inner}' in content and f'{inner}()' not in content:
                content = content.replace(
                    f'public static class {inner} {{',
                    f'public static class {inner} {{\n        public {inner}() {{}}'
                )
        write_file(path, content)

    # ImageDecoder.ImageInfo
    path = os.path.join(SHIM_ROOT, "android/graphics/ImageDecoder.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class ImageInfo' in content and 'ImageInfo()' not in content:
            content = content.replace(
                'public static class ImageInfo {',
                'public static class ImageInfo {\n        public ImageInfo() {}'
            )
            write_file(path, content)

    # BatteryStats.TimerStat
    path = os.path.join(SHIM_ROOT, "android/os/health/HealthStats.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class TimerStat' in content and 'TimerStat()' not in content:
            content = content.replace(
                'public static class TimerStat {',
                'public static class TimerStat {\n        public TimerStat() {}'
            )
            write_file(path, content)

    # ColorSpace.Rgb
    path = os.path.join(SHIM_ROOT, "android/graphics/ColorSpace.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class Rgb' in content and 'Rgb()' not in content:
            content = content.replace(
                'public static class Rgb extends ColorSpace {',
                'public static class Rgb extends ColorSpace {\n        public Rgb() {}'
            )
            write_file(path, content)

    # IdentityCredentialException
    path = os.path.join(SHIM_ROOT, "android/security/identity/IdentityCredentialException.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'IdentityCredentialException()' not in content:
            content = content.replace(
                'public class IdentityCredentialException',
                'public class IdentityCredentialException'
            )
            # Add constructors
            idx = content.index('{', content.index('IdentityCredentialException'))
            content = content[:idx+1] + '\n    public IdentityCredentialException() {}\n    public IdentityCredentialException(String msg) { super(msg); }' + content[idx+1:]
            write_file(path, content)

    # SystemUpdatePolicy
    path = os.path.join(SHIM_ROOT, "android/app/admin/SystemUpdatePolicy.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'SystemUpdatePolicy()' not in content:
            add_no_arg_constructor(path, "SystemUpdatePolicy")

    # MediaDrmException - in MediaDrm.java or standalone
    for p in ["android/media/MediaDrm.java", "android/media/MediaDrmException.java"]:
        path = os.path.join(SHIM_ROOT, p)
        if os.path.exists(path):
            content = read_file(path)
            if 'class MediaDrmException' in content and 'MediaDrmException()' not in content:
                content = content.replace(
                    'public static class MediaDrmException extends Exception {',
                    'public static class MediaDrmException extends Exception {\n        public MediaDrmException() {}\n        public MediaDrmException(String msg) { super(msg); }'
                )
                content = content.replace(
                    'public class MediaDrmException extends Exception {',
                    'public class MediaDrmException extends Exception {\n    public MediaDrmException() {}\n    public MediaDrmException(String msg) { super(msg); }'
                )
                write_file(path, content)

    # CursorWrapper
    path = os.path.join(SHIM_ROOT, "android/database/CursorWrapper.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'CursorWrapper()' not in content:
            add_no_arg_constructor(path, "CursorWrapper")

    # PathClassLoader
    path = os.path.join(SHIM_ROOT, "dalvik/system/PathClassLoader.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'PathClassLoader()' not in content:
            add_no_arg_constructor(path, "PathClassLoader")

    # RectF
    path = os.path.join(SHIM_ROOT, "android/graphics/RectF.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'RectF()' not in content:
            add_no_arg_constructor(path, "RectF")

    print("Fixed constructor errors")


def fix_return_types():
    """Fix return type mismatches across all files."""
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            orig = content

            # char returning null -> '\0'
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?char\s+\w+\([^)]*\)\s*\{)\s*return null;\s*\}',
                r"\1 return '\0'; }",
                content
            )
            # char[] returning '\0' or 0
            content = re.sub(
                r"((?:public|protected|private)\s+(?:static\s+)?char\[\]\s+\w+\([^)]*\)\s*\{)\s*return '\\0';\s*\}",
                r"\1 return new char[0]; }",
                content
            )
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?char\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
                r"\1 return new char[0]; }",
                content
            )
            # byte[] returning 0
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?byte\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
                r"\1 return new byte[0]; }",
                content
            )
            # int[] returning 0
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?int\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
                r"\1 return new int[0]; }",
                content
            )
            # long[] returning 0L or 0
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?long\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0L?;\s*\}',
                r"\1 return new long[0]; }",
                content
            )
            # float[] returning 0f or 0
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?float\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0f?;\s*\}',
                r"\1 return new float[0]; }",
                content
            )
            # double[] returning 0.0 or 0
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?double\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0[.0]*;\s*\}',
                r"\1 return new double[0]; }",
                content
            )
            # boolean[] returning false
            content = re.sub(
                r'((?:public|protected|private)\s+(?:static\s+)?boolean\[\]\s+\w+\([^)]*\)\s*\{)\s*return false;\s*\}',
                r"\1 return new boolean[0]; }",
                content
            )
            # String[] returning null should be new String[0] - but null is valid, skip

            if content != orig:
                write_file(path, content)
    print("Fixed return types")


def fix_abstract_classes():
    """Remove abstract from classes that have method bodies."""
    stderr = compile_and_get_errors()
    errors = parse_errors(stderr)

    abstract_files = set()
    for e in errors:
        if 'abstract methods cannot have a body' in e['msg']:
            abstract_files.add(e['file'])

    for filepath in abstract_files:
        fix_abstract_with_body(filepath)

    print(f"Fixed {len(abstract_files)} abstract-with-body files")


def fix_override_errors():
    """Fix 'method does not override or implement' errors by removing @Override."""
    stderr = compile_and_get_errors()
    errors = parse_errors(stderr)

    override_errors = {}
    for e in errors:
        if 'method does not override' in e['msg']:
            filepath = e['file']
            lineno = e['line']
            if filepath not in override_errors:
                override_errors[filepath] = []
            override_errors[filepath].append(lineno)

    for filepath, line_numbers in override_errors.items():
        content = read_file(filepath)
        lines = content.split('\n')
        for lineno in sorted(line_numbers, reverse=True):
            # The @Override is typically on the line before the method
            idx = lineno - 1  # 0-indexed
            if idx > 0 and '@Override' in lines[idx - 1]:
                lines[idx - 1] = lines[idx - 1].replace('@Override', '').strip()
                if not lines[idx - 1]:
                    lines[idx - 1] = ''  # Remove empty line
            elif idx >= 0 and '@Override' in lines[idx]:
                lines[idx] = lines[idx].replace('@Override ', '').replace('@Override', '')
        content = '\n'.join(lines)
        write_file(filepath, content)

    print(f"Fixed {len(override_errors)} files with override errors")


def fix_missing_implementations():
    """Fix 'is not abstract and does not override abstract method' errors."""
    # These need either: the class to be abstract, or the method to be added

    # setNotificationUris in Cursor
    path = os.path.join(SHIM_ROOT, "android/database/Cursor.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'setNotificationUris' not in content:
            content = content.replace(
                'android.os.Bundle respond(android.os.Bundle extras);',
                'android.os.Bundle respond(android.os.Bundle extras);\n    default void setNotificationUris(android.content.ContentResolver cr, java.util.List<android.net.Uri> uris) {}'
            )
            write_file(path, content)

    # BroadcastReceiver.onReceive with typed params
    path = os.path.join(SHIM_ROOT, "android/content/BroadcastReceiver.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onReceive(Object' in content:
            content = content.replace('onReceive(Object p0, Object p1)',
                                     'onReceive(android.content.Context p0, android.content.Intent p1)')
            write_file(path, content)

    # TransformationMethod.onFocusChanged
    path = os.path.join(SHIM_ROOT, "android/text/method/TransformationMethod.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onFocusChanged(Object' in content:
            content = content.replace(
                'onFocusChanged(Object p0, CharSequence p1, boolean p2, int p3, Object p4)',
                'onFocusChanged(android.view.View p0, CharSequence p1, boolean p2, int p3, android.graphics.Rect p4)')
            write_file(path, content)

    # CharacterStyle.updateDrawState
    path = os.path.join(SHIM_ROOT, "android/text/style/CharacterStyle.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'updateDrawState(Object' in content:
            content = content.replace('updateDrawState(Object p0)',
                                     'updateDrawState(android.text.TextPaint p0)')
            write_file(path, content)

    # Parcelable.writeToParcel
    path = os.path.join(SHIM_ROOT, "android/os/Parcelable.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'writeToParcel(Object' in content:
            content = content.replace('writeToParcel(Object p0, int p1)',
                                     'writeToParcel(android.os.Parcel p0, int p1)')
            write_file(path, content)

    # BluetoothProfile.getConnectedDevices
    path = os.path.join(SHIM_ROOT, "android/bluetooth/BluetoothProfile.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'Object getConnectedDevices' in content:
            content = content.replace('Object getConnectedDevices()',
                                     'java.util.List<BluetoothDevice> getConnectedDevices()')
            write_file(path, content)

    # NetworkSpecifier
    path = os.path.join(SHIM_ROOT, "android/net/NetworkSpecifier.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'canBeSatisfiedBy' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public boolean canBeSatisfiedBy(NetworkSpecifier other) { return false; }\n' + content[idx:]
            write_file(path, content)

    # CookieSyncManager
    path = os.path.join(SHIM_ROOT, "android/webkit/CookieSyncManager.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'implements Runnable' in content and 'void run()' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void run() {}\n' + content[idx:]
            write_file(path, content)

    # CellInfo - make non-abstract
    path = os.path.join(SHIM_ROOT, "android/telephony/CellInfo.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'abstract class CellInfo' in content:
            content = re.sub(r'public abstract class CellInfo', 'public class CellInfo', content)
            # Replace abstract methods with stubs
            content = re.sub(
                r'public abstract (\w+(?:\[\])?)\s+(\w+)\(([^)]*)\)\s*;',
                lambda m: _make_stub(m.group(1), m.group(2), m.group(3)),
                content
            )
            write_file(path, content)

    # CellSignalStrength - make non-abstract
    path = os.path.join(SHIM_ROOT, "android/telephony/CellSignalStrength.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'abstract class CellSignalStrength' in content:
            content = re.sub(r'public abstract class CellSignalStrength', 'public class CellSignalStrength', content)
            content = re.sub(
                r'public abstract (\w+(?:\[\])?)\s+(\w+)\(([^)]*)\)\s*;',
                lambda m: _make_stub(m.group(1), m.group(2), m.group(3)),
                content
            )
            write_file(path, content)

    # Add missing implementations to concrete classes
    # CellInfoCdma, CellInfoTdscdma, CellInfoNr
    for cls in ["CellInfoCdma", "CellInfoTdscdma", "CellInfoNr"]:
        path = os.path.join(SHIM_ROOT, f"android/telephony/{cls}.java")
        if os.path.exists(path):
            content = read_file(path)
            if 'getCellSignalStrength' not in content:
                idx = content.rindex('}')
                content = content[:idx] + '    public android.telephony.CellSignalStrength getCellSignalStrength() { return null; }\n' + content[idx:]
                write_file(path, content)

    # CellSignalStrength subclasses need hashCode
    for cls in ["CellSignalStrengthTdscdma", "CellSignalStrengthNr", "CellSignalStrengthCdma"]:
        path = os.path.join(SHIM_ROOT, f"android/telephony/{cls}.java")
        if os.path.exists(path):
            content = read_file(path)
            if 'hashCode()' not in content:
                idx = content.rindex('}')
                content = content[:idx] + '    @Override public int hashCode() { return 0; }\n    @Override public boolean equals(Object o) { return false; }\n    @Override public String toString() { return ""; }\n' + content[idx:]
                write_file(path, content)

    # BluetoothHidDevice, BluetoothHearingAid, BluetoothHealth
    for cls in ["BluetoothHidDevice", "BluetoothHearingAid"]:
        path = os.path.join(SHIM_ROOT, f"android/bluetooth/{cls}.java")
        if os.path.exists(path):
            content = read_file(path)
            if 'getConnectedDevices' not in content and 'BluetoothProfile' in content:
                idx = content.rindex('}')
                content = content[:idx] + '    public java.util.List<BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }\n' + content[idx:]
                write_file(path, content)

    path = os.path.join(SHIM_ROOT, "android/bluetooth/BluetoothHealth.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'getConnectionState' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public int getConnectionState(BluetoothDevice device) { return 0; }\n    public java.util.List<BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }\n' + content[idx:]
            write_file(path, content)

    # BluetoothHealthAppConfiguration writeToParcel
    path = os.path.join(SHIM_ROOT, "android/bluetooth/BluetoothHealthAppConfiguration.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'writeToParcel' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }\n' + content[idx:]
            write_file(path, content)

    # NeighboringCellInfo writeToParcel
    path = os.path.join(SHIM_ROOT, "android/telephony/NeighboringCellInfo.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'writeToParcel' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }\n' + content[idx:]
            write_file(path, content)

    # ChooserTarget, ControlButton writeToParcel
    for cls_path in [
        "android/service/chooser/ChooserTarget.java",
        "android/service/controls/templates/ControlButton.java",
    ]:
        path = os.path.join(SHIM_ROOT, cls_path)
        if os.path.exists(path):
            content = read_file(path)
            if 'writeToParcel' not in content and 'Parcelable' in content:
                idx = content.rindex('}')
                content = content[:idx] + '    public void writeToParcel(android.os.Parcel dest, int flags) {}\n    public int describeContents() { return 0; }\n' + content[idx:]
                write_file(path, content)

    # SuggestionSpan
    path = os.path.join(SHIM_ROOT, "android/text/style/SuggestionSpan.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'updateDrawState' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void updateDrawState(android.text.TextPaint tp) {}\n' + content[idx:]
            write_file(path, content)

    # KeyListener implementations
    for cls in ["TimeKeyListener", "DialerKeyListener", "DateTimeKeyListener", "DateKeyListener"]:
        path = os.path.join(SHIM_ROOT, f"android/text/method/{cls}.java")
        if os.path.exists(path):
            content = read_file(path)
            if 'clearMetaKeyState' not in content:
                idx = content.rindex('}')
                content = content[:idx] + '    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}\n' + content[idx:]
                write_file(path, content)

    # SingleLineTransformationMethod, HideReturnsTransformationMethod
    for cls in ["SingleLineTransformationMethod", "HideReturnsTransformationMethod"]:
        path = os.path.join(SHIM_ROOT, f"android/text/method/{cls}.java")
        if os.path.exists(path):
            content = read_file(path)
            if 'onFocusChanged' not in content:
                idx = content.rindex('}')
                content = content[:idx] + '    public void onFocusChanged(android.view.View view, CharSequence sourceText, boolean focused, int direction, android.graphics.Rect previouslyFocusedRect) {}\n' + content[idx:]
                write_file(path, content)

    # MbmsDownloadReceiver
    path = os.path.join(SHIM_ROOT, "android/telephony/mbms/MbmsDownloadReceiver.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onReceive' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void onReceive(android.content.Context context, android.content.Intent intent) {}\n' + content[idx:]
            write_file(path, content)

    # DelegatedAdminReceiver
    path = os.path.join(SHIM_ROOT, "android/app/admin/DelegatedAdminReceiver.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onReceive' not in content:
            idx = content.rindex('}')
            content = content[:idx] + '    public void onReceive(android.content.Context context, android.content.Intent intent) {}\n' + content[idx:]
            write_file(path, content)

    # MbmsGroupCallSessionCallback should be interface
    path = os.path.join(SHIM_ROOT, "android/telephony/mbms/MbmsGroupCallSessionCallback.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public class MbmsGroupCallSessionCallback' in content:
            content = content.replace(
                'public class MbmsGroupCallSessionCallback',
                'public interface MbmsGroupCallSessionCallback'
            )
            # Remove method bodies for interface
            content = re.sub(r'\{\s*(?:return [^;]*;)?\s*\}', ';', content)
            # But not the interface opening brace
            # Fix: re-add the interface opening brace
            content = content.replace('MbmsGroupCallSessionCallback ;', 'MbmsGroupCallSessionCallback {')
            write_file(path, content)

    print("Fixed missing implementations")


def _make_stub(ret_type, method_name, params):
    defaults = {
        'int': '0', 'long': '0L', 'float': '0f', 'double': '0.0',
        'boolean': 'false', 'byte': '0', 'short': '0', 'char': "'\\0'",
        'void': '',
    }
    if ret_type in defaults:
        ret_val = defaults[ret_type]
        if ret_val:
            return f'public {ret_type} {method_name}({params}) {{ return {ret_val}; }}'
        else:
            return f'public {ret_type} {method_name}({params}) {{}}'
    elif ret_type.endswith('[]'):
        base = ret_type[:-2]
        return f'public {ret_type} {method_name}({params}) {{ return new {base}[0]; }}'
    else:
        return f'public {ret_type} {method_name}({params}) {{ return null; }}'


def fix_misc_errors():
    """Fix miscellaneous one-off errors."""

    # MessageFormat duplicate method
    path = os.path.join(SHIM_ROOT, "android/icu/text/MessageFormat.java")
    if os.path.exists(path):
        content = read_file(path)
        # Remove duplicate format methods
        sig = 'public Object format(Object p0, Object p1, Object p2)'
        count = content.count(sig)
        if count > 1:
            first_end = content.index('}', content.index(sig)) + 1
            second_start = content.index(sig, first_end)
            second_end = content.index('}', second_start) + 1
            content = content[:second_start] + content[second_end:]
            write_file(path, content)

    # LruCache toString
    path = os.path.join(SHIM_ROOT, "android/util/LruCache.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public Object toString()' in content:
            content = content.replace('public Object toString()', 'public String toString()')
            write_file(path, content)

    # Fix "recursive constructor invocation"
    # These are constructors like MyClass() { this(); }
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            # Remove this() calls in no-arg constructors
            new_content = re.sub(
                r'(public\s+\w+\(\)\s*\{)\s*this\(\);\s*\}',
                r'\1 }',
                content
            )
            if new_content != content:
                write_file(path, new_content)

    print("Fixed misc errors")


def main():
    os.makedirs("/tmp/shim-fix-iter", exist_ok=True)

    print("=== Phase 1: Fix constructors ===")
    fix_constructor_errors()

    print("\n=== Phase 2: Fix return types ===")
    fix_return_types()

    print("\n=== Phase 3: Fix abstract classes ===")
    fix_abstract_classes()

    print("\n=== Phase 4: Fix missing implementations ===")
    fix_missing_implementations()

    print("\n=== Phase 5: Fix override errors ===")
    fix_override_errors()

    print("\n=== Phase 6: Fix misc errors ===")
    fix_misc_errors()

    # Final compile
    print("\n=== Final compile ===")
    stderr = compile_and_get_errors()
    error_lines = [l for l in stderr.split('\n') if 'error:' in l and ': error:' in l]
    error_files = set()
    for line in error_lines:
        error_files.add(line.split(':')[0])

    print(f"Remaining errors: {len(error_lines)}")
    print(f"Remaining error files: {len(error_files)}")
    print(f"Total files: {len(get_all_java_files())}")

    # Summary
    error_types = {}
    for line in error_lines:
        msg = re.sub(r'^.*: error: ', '', line)
        error_types[msg] = error_types.get(msg, 0) + 1

    for msg, count in sorted(error_types.items(), key=lambda x: -x[1])[:15]:
        print(f"  {count}x {msg}")

    if error_files:
        print("\nError files:")
        for f in sorted(error_files):
            print(f"  {f}")


if __name__ == "__main__":
    main()
