#!/usr/bin/env python3
"""Comprehensive fix for remaining shim compile errors."""

import os
import re
import subprocess

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")


def read_file(path):
    with open(path) as f:
        return f.read()


def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content)


def fix_abstract_methods_with_body():
    """Fix 'abstract methods cannot have a body' - remove body from abstract methods."""
    # These are classes declared abstract but with stub bodies in abstract methods
    # Solution: make them non-abstract (remove abstract keyword from class or method)
    files = [
        "android/hardware/TriggerEventListener.java",
        "android/hardware/camera2/CameraOfflineSession.java",
        "android/icu/text/UnicodeFilter.java",
        "android/media/effect/Effect.java",
        "android/media/midi/MidiReceiver.java",
        "android/media/midi/MidiSender.java",
        "android/printservice/PrinterDiscoverySession.java",
        "android/security/identity/ResultData.java",
        "android/service/controls/actions/ControlAction.java",
        "android/service/controls/templates/ControlTemplate.java",
        "android/telephony/CellSignalStrength.java",
        "android/text/method/ReplacementTransformationMethod.java",
        "android/webkit/PermissionRequest.java",
        "android/webkit/RenderProcessGoneDetail.java",
        "android/webkit/SafeBrowsingResponse.java",
        "android/webkit/ServiceWorkerController.java",
        "android/webkit/ServiceWorkerWebSettings.java",
        "android/webkit/TracingController.java",
        "android/webkit/WebViewRenderProcess.java",
        "android/webkit/WebViewRenderProcessClient.java",
    ]
    for rel in files:
        path = os.path.join(SHIM_ROOT, rel)
        if not os.path.exists(path):
            continue
        content = read_file(path)
        # Remove 'abstract' from class declaration but keep it a class
        # This allows the stub methods with bodies to compile
        content = re.sub(r'public abstract class ', 'public class ', content)
        write_file(path, content)
    print(f"Fixed {len(files)} abstract-with-body files")


def fix_null_to_char():
    """Fix 'null cannot be converted to char' - use '\\0' instead."""
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            orig = content
            # Fix methods returning char that have return null
            content = re.sub(
                r'(public\s+(?:static\s+)?char\s+\w+\([^)]*\)\s*\{)\s*return null;\s*\}',
                r"\1 return '\0'; }",
                content
            )
            if content != orig:
                write_file(path, content)


def fix_int_to_array():
    """Fix 'int cannot be converted to byte[]', 'int cannot be converted to int[]', etc."""
    array_defaults = {
        'byte[]': 'new byte[0]',
        'int[]': 'new int[0]',
        'long[]': 'new long[0]',
        'float[]': 'new float[0]',
        'double[]': 'new double[0]',
        'boolean[]': 'new boolean[0]',
        'char[]': 'new char[0]',
        'short[]': 'new short[0]',
        'String[]': 'new String[0]',
    }
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            orig = content
            for arr_type, default in array_defaults.items():
                # Match methods returning array type with wrong return value
                escaped = re.escape(arr_type)
                # Fix "return 0;" or "return null;" for array types
                content = re.sub(
                    rf'(public\s+(?:static\s+)?{escaped}\s+\w+\([^)]*\)\s*\{{)\s*return 0;\s*\}}',
                    rf'\1 return {default}; }}',
                    content
                )
            if content != orig:
                write_file(path, content)


def fix_no_arg_constructors():
    """Add no-arg constructors to parent classes that require args."""
    fixes = {
        # class -> add no-arg constructor
        "android/media/audiofx/AudioEffect.java": ("class AudioEffect", "public AudioEffect() {}"),
        "android/view/ViewGroup.java": ("class ViewGroup", "public ViewGroup() {}"),
        "android/opengl/EGLObjectHandle.java": ("class EGLObjectHandle", "public EGLObjectHandle() { super(0); }"),
        "android/view/View.java": ("class View", None),  # already should have one
        "android/graphics/Rational.java": ("class Rational", "public Rational() { this(0, 1); }"),
        "android/sip/SipAudioCall.java": None,
        "android/net/sip/SipAudioCall.java": None,
    }

    # AudioEffect needs a no-arg constructor for subclasses
    path = os.path.join(SHIM_ROOT, "android/media/audiofx/AudioEffect.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public AudioEffect()' not in content:
            content = content.replace(
                'public class AudioEffect {',
                'public class AudioEffect {\n    public AudioEffect() {}'
            )
            write_file(path, content)

    # ViewGroup needs no-arg constructor
    path = os.path.join(SHIM_ROOT, "android/view/ViewGroup.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public ViewGroup()' not in content:
            content = content.replace(
                'public class ViewGroup extends View {',
                'public class ViewGroup extends View {\n    public ViewGroup() {}'
            )
            write_file(path, content)

    # EGLObjectHandle needs no-arg or long constructor
    path = os.path.join(SHIM_ROOT, "android/opengl/EGLObjectHandle.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public EGLObjectHandle()' not in content and 'protected EGLObjectHandle()' not in content:
            content = content.replace(
                'public class EGLObjectHandle {',
                'public class EGLObjectHandle {\n    public EGLObjectHandle() {}\n    public EGLObjectHandle(long handle) {}'
            )
            write_file(path, content)

    # BaseDexClassLoader needs constructor
    path = os.path.join(SHIM_ROOT, "dalvik/system/BaseDexClassLoader.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public BaseDexClassLoader()' not in content and 'protected BaseDexClassLoader()' not in content:
            content = content.replace(
                'public class BaseDexClassLoader extends ClassLoader {',
                'public class BaseDexClassLoader extends ClassLoader {\n    public BaseDexClassLoader() {}\n    public BaseDexClassLoader(String dexPath, java.io.File optimizedDirectory, String librarySearchPath, ClassLoader parent) { super(parent); }'
            )
            write_file(path, content)

    # MediaDrmException needs String constructor
    path = os.path.join(SHIM_ROOT, "android/media/MediaDrm.java")
    if os.path.exists(path):
        content = read_file(path)
        # Check if MediaDrmException inner class exists
        if 'class MediaDrmException' in content and 'public MediaDrmException()' not in content:
            content = content.replace(
                'public static class MediaDrmException extends Exception {',
                'public static class MediaDrmException extends Exception {\n        public MediaDrmException() {}\n        public MediaDrmException(String message) { super(message); }'
            )
            write_file(path, content)

    # IdentityCredentialException needs no-arg constructor
    path = os.path.join(SHIM_ROOT, "android/security/identity/IdentityCredentialException.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public IdentityCredentialException()' not in content:
            content = content.replace(
                'public class IdentityCredentialException extends Exception {',
                'public class IdentityCredentialException extends Exception {\n    public IdentityCredentialException() {}\n    public IdentityCredentialException(String message) { super(message); }'
            )
            write_file(path, content)

    # MediaSize in print needs constructor
    path = os.path.join(SHIM_ROOT, "android/print/PrintAttributes.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class MediaSize' in content and 'public MediaSize()' not in content:
            content = content.replace(
                'public static class MediaSize {',
                'public static class MediaSize {\n        public MediaSize() {}\n        public MediaSize(String id, String label, int widthMils, int heightMils) {}'
            )
            write_file(path, content)

    # Resolution
    if os.path.exists(path):
        if 'class Resolution' in content and 'public Resolution()' not in content:
            content = read_file(path)
            content = content.replace(
                'public static class Resolution {',
                'public static class Resolution {\n        public Resolution() {}\n        public Resolution(String id, String label, int horizontalDpi, int verticalDpi) {}'
            )
            write_file(path, content)

    # Margins
    if os.path.exists(path):
        if 'class Margins' in content and 'public Margins()' not in content:
            content = read_file(path)
            content = content.replace(
                'public static class Margins {',
                'public static class Margins {\n        public Margins() {}\n        public Margins(int leftMils, int topMils, int rightMils, int bottomMils) {}'
            )
            write_file(path, content)

    # NumberFormat in icu
    path = os.path.join(SHIM_ROOT, "android/icu/text/NumberFormat.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'protected NumberFormat()' not in content and 'public NumberFormat()' not in content:
            content = content.replace(
                'public class NumberFormat {',
                'public class NumberFormat {\n    public NumberFormat() {}'
            )
            write_file(path, content)

    # CursorAdapter
    path = os.path.join(SHIM_ROOT, "android/widget/CursorAdapter.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public CursorAdapter()' not in content:
            if 'public class CursorAdapter' in content:
                content = content.replace(
                    'public class CursorAdapter extends android.widget.BaseAdapter {',
                    'public class CursorAdapter extends android.widget.BaseAdapter {\n    public CursorAdapter() {}'
                )
                write_file(path, content)

    # CursorTreeAdapter
    path = os.path.join(SHIM_ROOT, "android/widget/CursorTreeAdapter.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public CursorTreeAdapter()' not in content:
            content = content.replace(
                'public class CursorTreeAdapter extends android.widget.BaseExpandableListAdapter {',
                'public class CursorTreeAdapter extends android.widget.BaseExpandableListAdapter {\n    public CursorTreeAdapter() {}'
            )
            write_file(path, content)

    # PathClassLoader
    path = os.path.join(SHIM_ROOT, "dalvik/system/PathClassLoader.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public PathClassLoader()' not in content:
            content = content.replace(
                'public class PathClassLoader extends BaseDexClassLoader {',
                'public class PathClassLoader extends BaseDexClassLoader {\n    public PathClassLoader() {}\n    public PathClassLoader(String dexPath, ClassLoader parent) {}\n    public PathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {}'
            )
            write_file(path, content)

    # Handler
    path = os.path.join(SHIM_ROOT, "android/os/Handler.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public Handler()' not in content:
            content = content.replace(
                'public class Handler {',
                'public class Handler {\n    public Handler() {}\n    public Handler(android.os.Looper looper) {}'
            )
            write_file(path, content)

    # RSRuntimeException
    path = os.path.join(SHIM_ROOT, "android/renderscript/RSRuntimeException.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public RSRuntimeException()' not in content:
            content = content.replace(
                'public class RSRuntimeException extends RuntimeException {',
                'public class RSRuntimeException extends RuntimeException {\n    public RSRuntimeException() {}\n    public RSRuntimeException(String message) { super(message); }'
            )
            write_file(path, content)

    # AudioCodec in net.rtp
    path = os.path.join(SHIM_ROOT, "android/net/rtp/AudioCodec.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public AudioCodec()' not in content:
            content = content.replace(
                'public class AudioCodec {',
                'public class AudioCodec {\n    public AudioCodec() {}\n    public AudioCodec(int type, String rtpmap, String fmtp) {}'
            )
            write_file(path, content)

    # ImageInfo for ImageDecoder
    path = os.path.join(SHIM_ROOT, "android/graphics/ImageDecoder.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class ImageInfo' in content and 'public ImageInfo()' not in content:
            content = content.replace(
                'public static class ImageInfo {',
                'public static class ImageInfo {\n        public ImageInfo() {}'
            )
            write_file(path, content)

    # BitmapRegionDecoder
    path = os.path.join(SHIM_ROOT, "android/graphics/BitmapRegionDecoder.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public BitmapRegionDecoder()' not in content and 'private BitmapRegionDecoder' not in content:
            content = content.replace(
                'public class BitmapRegionDecoder {',
                'public class BitmapRegionDecoder {\n    public BitmapRegionDecoder() {}'
            )
            write_file(path, content)

    # CursorWrapper needs Cursor arg
    path = os.path.join(SHIM_ROOT, "android/database/CursorWrapper.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public CursorWrapper()' not in content:
            content = content.replace(
                'public class CursorWrapper implements Cursor {',
                'public class CursorWrapper implements Cursor {\n    public CursorWrapper() {}\n    public CursorWrapper(Cursor cursor) {}'
            )
            write_file(path, content)

    # RectF
    path = os.path.join(SHIM_ROOT, "android/graphics/RectF.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public RectF()' not in content:
            content = content.replace(
                'public class RectF {',
                'public class RectF {\n    public RectF() {}\n    public RectF(float left, float top, float right, float bottom) {}'
            )
            write_file(path, content)

    # Collator in icu
    path = os.path.join(SHIM_ROOT, "android/icu/text/Collator.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'protected Collator()' not in content and 'public Collator()' not in content:
            content = content.replace(
                'public class Collator {',
                'public class Collator {\n    public Collator() {}'
            )
            write_file(path, content)

    # MeasureUnit in icu
    path = os.path.join(SHIM_ROOT, "android/icu/util/MeasureUnit.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'protected MeasureUnit()' not in content and 'public MeasureUnit()' not in content:
            content = content.replace(
                'public class MeasureUnit {',
                'public class MeasureUnit {\n    public MeasureUnit() {}'
            )
            write_file(path, content)

    # TextView
    path = os.path.join(SHIM_ROOT, "android/widget/TextView.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public TextView()' not in content:
            content = content.replace(
                'public class TextView extends View {',
                'public class TextView extends View {\n    public TextView() {}'
            )
            write_file(path, content)

    # ActionProvider
    path = os.path.join(SHIM_ROOT, "android/view/ActionProvider.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public ActionProvider()' not in content:
            content = content.replace(
                'public class ActionProvider {',
                'public class ActionProvider {\n    public ActionProvider() {}\n    public ActionProvider(Object context) {}'
            )
            write_file(path, content)

    # SystemUpdatePolicy
    path = os.path.join(SHIM_ROOT, "android/app/admin/SystemUpdatePolicy.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public SystemUpdatePolicy()' not in content:
            content = content.replace(
                'public class SystemUpdatePolicy {',
                'public class SystemUpdatePolicy {\n    public SystemUpdatePolicy() {}'
            )
            write_file(path, content)

    # TimerStat in BatteryStats
    path = os.path.join(SHIM_ROOT, "android/os/BatteryStats.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class TimerStat' in content and 'public TimerStat()' not in content:
            content = content.replace(
                'public static class TimerStat {',
                'public static class TimerStat {\n        public TimerStat() {}\n        public TimerStat(int count, long totalTime) {}'
            )
            write_file(path, content)

    # Rgb in ColorSpace
    path = os.path.join(SHIM_ROOT, "android/graphics/ColorSpace.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'class Rgb' in content and 'public Rgb()' not in content:
            content = content.replace(
                'public static class Rgb extends ColorSpace {',
                'public static class Rgb extends ColorSpace {\n        public Rgb() {}'
            )
            write_file(path, content)

    # LinkAddress
    path = os.path.join(SHIM_ROOT, "android/net/LinkAddress.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public LinkAddress()' not in content:
            content = content.replace(
                'public class LinkAddress {',
                'public class LinkAddress {\n    public LinkAddress() {}\n    public LinkAddress(java.net.InetAddress address, int prefixLength) {}\n    public LinkAddress(java.net.InetAddress address, int prefixLength, int flags, int scope) {}'
            )
            write_file(path, content)

    print("Fixed no-arg constructor issues")


def fix_missing_abstract_implementations():
    """Add missing abstract method implementations."""
    # setNotificationUris in Cursor - add default method
    path = os.path.join(SHIM_ROOT, "android/database/Cursor.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'setNotificationUris' not in content:
            content = content.replace(
                'android.os.Bundle respond(android.os.Bundle extras);',
                'android.os.Bundle respond(android.os.Bundle extras);\n    default void setNotificationUris(android.content.ContentResolver cr, java.util.List<android.net.Uri> uris) {}'
            )
            write_file(path, content)

    # BroadcastReceiver.onReceive - check param types
    path = os.path.join(SHIM_ROOT, "android/content/BroadcastReceiver.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onReceive(Object' in content:
            content = content.replace(
                'public void onReceive(Object p0, Object p1)',
                'public void onReceive(android.content.Context p0, android.content.Intent p1)'
            )
            content = content.replace(
                'public abstract void onReceive(Object p0, Object p1)',
                'public abstract void onReceive(android.content.Context p0, android.content.Intent p1)'
            )
            write_file(path, content)

    # TransformationMethod.onFocusChanged - check param types
    path = os.path.join(SHIM_ROOT, "android/text/method/TransformationMethod.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'onFocusChanged(Object' in content:
            content = content.replace(
                'onFocusChanged(Object p0, CharSequence p1, boolean p2, int p3, Object p4)',
                'onFocusChanged(android.view.View p0, CharSequence p1, boolean p2, int p3, android.graphics.Rect p4)'
            )
            write_file(path, content)

    # CharacterStyle.updateDrawState
    path = os.path.join(SHIM_ROOT, "android/text/style/CharacterStyle.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'updateDrawState(Object' in content:
            content = content.replace(
                'updateDrawState(Object p0)',
                'updateDrawState(android.text.TextPaint p0)'
            )
            write_file(path, content)

    # Parcelable.writeToParcel
    path = os.path.join(SHIM_ROOT, "android/os/Parcelable.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'writeToParcel(Object' in content:
            content = content.replace(
                'writeToParcel(Object p0, int p1)',
                'writeToParcel(android.os.Parcel p0, int p1)'
            )
            write_file(path, content)

    # BluetoothProfile.getConnectedDevices return type
    path = os.path.join(SHIM_ROOT, "android/bluetooth/BluetoothProfile.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'Object getConnectedDevices' in content:
            content = content.replace(
                'Object getConnectedDevices()',
                'java.util.List<BluetoothDevice> getConnectedDevices()'
            )
            write_file(path, content)

    # NetworkSpecifier.canBeSatisfiedBy
    path = os.path.join(SHIM_ROOT, "android/net/NetworkSpecifier.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'canBeSatisfiedBy' not in content:
            # Add abstract method
            content = content.replace(
                'public class NetworkSpecifier {',
                'public class NetworkSpecifier {\n    public boolean canBeSatisfiedBy(NetworkSpecifier other) { return false; }'
            )
            write_file(path, content)

    # Runnable for CookieSyncManager
    path = os.path.join(SHIM_ROOT, "android/webkit/CookieSyncManager.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'implements Runnable' in content and 'public void run()' not in content:
            content = content.replace(
                'public class CookieSyncManager implements Runnable {',
                'public class CookieSyncManager implements Runnable {\n    public void run() {}'
            )
            write_file(path, content)

    # CellInfo abstract method
    path = os.path.join(SHIM_ROOT, "android/telephony/CellInfo.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'abstract' in content and 'getCellSignalStrength' in content:
            # Make it non-abstract with a default
            content = content.replace('public abstract class CellInfo', 'public class CellInfo')
            if 'abstract Object getCellSignalStrength' in content:
                content = content.replace(
                    'public abstract Object getCellSignalStrength();',
                    'public Object getCellSignalStrength() { return null; }'
                )
            elif 'abstract CellSignalStrength getCellSignalStrength' in content:
                content = content.replace(
                    'public abstract CellSignalStrength getCellSignalStrength();',
                    'public CellSignalStrength getCellSignalStrength() { return null; }'
                )
            write_file(path, content)

    # CellSignalStrength - make non-abstract
    path = os.path.join(SHIM_ROOT, "android/telephony/CellSignalStrength.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public abstract class' in content:
            content = content.replace('public abstract class', 'public class')
            # Replace abstract methods with stubs
            content = re.sub(
                r'public abstract (int|String|boolean|void|Object) (\w+)\(([^)]*)\);',
                lambda m: f'public {m.group(1)} {m.group(2)}({m.group(3)}) {{ {"return 0;" if m.group(1) == "int" else "return false;" if m.group(1) == "boolean" else "return null;" if m.group(1) in ("String", "Object") else ""} }}',
                content
            )
            write_file(path, content)

    print("Fixed missing abstract implementations")


def fix_mbms_interface():
    """Fix MbmsGroupCallSessionCallback - should be interface."""
    path = os.path.join(SHIM_ROOT, "android/telephony/mbms/MbmsGroupCallSessionCallback.java")
    if os.path.exists(path):
        content = read_file(path)
        if 'public class MbmsGroupCallSessionCallback' in content:
            content = content.replace(
                'public class MbmsGroupCallSessionCallback',
                'public interface MbmsGroupCallSessionCallback'
            )
            # Remove method bodies, make them interface methods
            content = re.sub(r'\{\s*(?:return [^;]*;)?\s*\}', ';', content)
            write_file(path, content)


def fix_duplicate_methods():
    """Fix duplicate method definitions."""
    path = os.path.join(SHIM_ROOT, "android/icu/text/MessageFormat.java")
    if os.path.exists(path):
        content = read_file(path)
        # Remove duplicate format(Object,Object,Object) - keep only one
        count = content.count('public Object format(Object p0, Object p1, Object p2)')
        if count > 1:
            # Remove the second occurrence
            first = content.index('public Object format(Object p0, Object p1, Object p2)')
            end_of_first = content.index('}', first) + 1
            second = content.index('public Object format(Object p0, Object p1, Object p2)', end_of_first)
            end_of_second = content.index('}', second) + 1
            content = content[:second] + content[end_of_second:]
            write_file(path, content)


def fix_arraymap_set():
    """Fix ArraySet name clash with Set interface."""
    path = os.path.join(SHIM_ROOT, "android/util/ArraySet.java")
    if os.path.exists(path):
        content = read_file(path)
        # Remove Set implementation since it causes name clashes with Object vs java.lang.Object
        if 'implements Set' in content or 'implements java.util.Set' in content:
            content = content.replace(' implements Set', '')
            content = content.replace(' implements java.util.Set', '')
            content = content.replace(' implements java.util.Collection, java.io.Serializable, Set', ' implements java.util.Collection, java.io.Serializable')
            write_file(path, content)


def fix_lru_cache_tostring():
    """Fix LruCache toString return type issue."""
    path = os.path.join(SHIM_ROOT, "android/util/LruCache.java")
    if os.path.exists(path):
        content = read_file(path)
        # If toString returns wrong type, fix it
        if 'public Object toString()' in content:
            content = content.replace(
                'public Object toString()',
                'public String toString()'
            )
            write_file(path, content)


def fix_recursive_constructors():
    """Fix recursive constructor invocation."""
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            # Find constructors that call this() with wrong args
            if 'this();' in content:
                # Count constructors - if there's only a no-arg constructor calling this(), remove it
                pass  # Complex to fix generically, skip for now


def fix_return_types():
    """Fix various return type mismatches."""
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            content = read_file(path)
            orig = content

            # Fix char[] return returning char
            content = re.sub(
                r'(public\s+(?:static\s+)?char\[\]\s+\w+\([^)]*\)\s*\{)\s*return \'\\0\';\s*\}',
                r"\1 return new char[0]; }",
                content
            )

            # Fix Object[] types
            content = re.sub(
                r'(public\s+(?:static\s+)?Object\[\]\s+\w+\([^)]*\)\s*\{)\s*return 0;\s*\}',
                r"\1 return new Object[0]; }",
                content
            )

            if content != orig:
                write_file(path, content)


def fix_keylistener_impls():
    """Fix KeyListener implementations missing clearMetaKeyState."""
    key_listeners = [
        "android/text/method/TimeKeyListener.java",
        "android/text/method/DialerKeyListener.java",
        "android/text/method/DateTimeKeyListener.java",
        "android/text/method/DateKeyListener.java",
    ]
    for rel in key_listeners:
        path = os.path.join(SHIM_ROOT, rel)
        if not os.path.exists(path):
            continue
        content = read_file(path)
        if 'clearMetaKeyState' not in content:
            # Add before closing brace
            content = content.rstrip()
            if content.endswith('}'):
                content = content[:-1] + '    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}\n}'
                write_file(path, content)


def fix_bluetooth_profile_impls():
    """Fix BluetoothProfile implementations."""
    bt_files = [
        "android/bluetooth/BluetoothHidDevice.java",
        "android/bluetooth/BluetoothHearingAid.java",
        "android/bluetooth/BluetoothHealth.java",
    ]
    for rel in bt_files:
        path = os.path.join(SHIM_ROOT, rel)
        if not os.path.exists(path):
            continue
        content = read_file(path)
        if 'implements BluetoothProfile' in content:
            if 'getConnectedDevices' not in content:
                content = content.rstrip()
                if content.endswith('}'):
                    content = content[:-1] + '    public java.util.List<BluetoothDevice> getConnectedDevices() { return new java.util.ArrayList<>(); }\n}'
                    write_file(path, content)
            if 'getConnectionState' not in content and 'BluetoothHealth' in rel:
                content = read_file(path)
                content = content.rstrip()
                if content.endswith('}'):
                    content = content[:-1] + '    public int getConnectionState(BluetoothDevice device) { return 0; }\n}'
                    write_file(path, content)


def main():
    print("=== Comprehensive shim fix pass ===")
    fix_abstract_methods_with_body()
    fix_null_to_char()
    fix_int_to_array()
    fix_no_arg_constructors()
    fix_missing_abstract_implementations()
    fix_mbms_interface()
    fix_duplicate_methods()
    fix_arraymap_set()
    fix_lru_cache_tostring()
    fix_return_types()
    fix_keylistener_impls()
    fix_bluetooth_profile_impls()
    fix_keylistener_impls()

    # Now run compile to check
    print("\n=== Recompiling... ===")
    import glob as g
    java_files = []
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if f.endswith('.java'):
                java_files.append(os.path.join(root, f))

    os.makedirs("/tmp/shim-fix2", exist_ok=True)
    result = subprocess.run(
        ["javac", "-sourcepath", SHIM_ROOT, "-d", "/tmp/shim-fix2", "-Xmaxerrs", "500"] + java_files,
        capture_output=True, text=True, timeout=300
    )

    errors = [l for l in result.stderr.split('\n') if 'error:' in l]
    error_files = set()
    for line in errors:
        if line.startswith('shim/') or line.startswith('/'):
            error_files.add(line.split(':')[0])

    print(f"Remaining errors: {len(errors)}")
    print(f"Remaining error files: {len(error_files)}")
    print(f"Total Java files: {len(java_files)}")

    # Print error summary
    error_types = {}
    for line in errors:
        if 'error:' in line:
            msg = re.sub(r'^.*error: ', '', line)
            error_types[msg] = error_types.get(msg, 0) + 1

    for msg, count in sorted(error_types.items(), key=lambda x: -x[1])[:20]:
        print(f"  {count}x {msg}")


if __name__ == "__main__":
    main()
