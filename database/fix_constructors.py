#!/usr/bin/env python3
"""Add missing constructor overloads to parent classes."""

import os
import re

SHIM_ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "shim", "java")


def read_file(path):
    with open(path) as f:
        return f.read()


def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content)


def add_constructors(rel_path, class_name, ctors):
    """Add constructor overloads to a class."""
    path = os.path.join(SHIM_ROOT, rel_path)
    if not os.path.exists(path):
        print(f"  SKIP {rel_path} (not found)")
        return
    content = read_file(path)
    added = []
    for ctor_sig in ctors:
        # e.g. "int nodeType" -> check if constructor with that exists
        sig_check = f'{class_name}({ctor_sig.split(")")[0].split("(")[1] if "(" in ctor_sig else ctor_sig}'
        # Simple check: just check if the full ctor string is already there
        if ctor_sig.split('{')[0].strip() in content:
            continue
        # Find the class opening brace
        pattern = re.compile(rf'(?:public|protected)\s+(?:static\s+)?(?:final\s+)?class\s+{re.escape(class_name)}\b[^{{]*\{{')
        m = pattern.search(content)
        if m:
            idx = m.end()
            content = content[:idx] + f'\n    {ctor_sig}' + content[idx:]
            added.append(ctor_sig.split('{')[0].strip())

    if added:
        write_file(path, content)
        for a in added:
            print(f"  Added: {a}")


# ViewGroup: needs int constructor
add_constructors("android/view/ViewGroup.java", "ViewGroup", [
    "public ViewGroup(int nodeType) {}",
    "public ViewGroup(android.content.Context context) {}",
    "public ViewGroup(android.content.Context context, android.util.AttributeSet attrs) {}",
])

# View: needs int constructor
add_constructors("android/view/View.java", "View", [
    "public View(int nodeType) {}",
    "public View(android.content.Context context) {}",
    "public View(android.content.Context context, android.util.AttributeSet attrs) {}",
    "public View(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {}",
])

# TextView: needs int constructor
add_constructors("android/widget/TextView.java", "TextView", [
    "public TextView(int nodeType) {}",
    "public TextView(android.content.Context context) {}",
])

# MediaSize in PrintAttributes: needs (String, int) constructor
add_constructors("android/print/PrintAttributes.java", "MediaSize", [
    "public MediaSize(String id, int widthMils) {}",
    "public MediaSize(String id, String label, int widthMils, int heightMils) {}",
])

# Resolution in PrintAttributes
add_constructors("android/print/PrintAttributes.java", "Resolution", [
    "public Resolution(String id, String label, int horizontalDpi, int verticalDpi) {}",
])

# Margins in PrintAttributes
add_constructors("android/print/PrintAttributes.java", "Margins", [
    "public Margins(int leftMils, int topMils, int rightMils, int bottomMils) {}",
])

# AudioCodec
add_constructors("android/net/rtp/AudioCodec.java", "AudioCodec", [
    "public AudioCodec(int type, String rtpmap, String fmtp) {}",
])

# Rational
add_constructors("android/util/Rational.java", "Rational", [
    "public Rational(int numerator, int denominator) {}",
])

# NumberFormat in icu
add_constructors("android/icu/text/NumberFormat.java", "NumberFormat", [
    "public NumberFormat(android.icu.text.DecimalFormat fmt) {}",
])

# SystemUpdatePolicy
add_constructors("android/app/admin/SystemUpdatePolicy.java", "SystemUpdatePolicy", [
    "public SystemUpdatePolicy(int type, int dummy) {}",
])

# ImageInfo in ImageDecoder
path = os.path.join(SHIM_ROOT, "android/graphics/ImageDecoder.java")
if os.path.exists(path):
    content = read_file(path)
    if 'ImageInfo(int' not in content and 'class ImageInfo' in content:
        content = content.replace(
            'public static class ImageInfo {',
            'public static class ImageInfo {\n        public ImageInfo() {}\n        public ImageInfo(int width, int height) {}'
        )
        write_file(path, content)
        print("  Added ImageInfo constructors")

# BitmapRegionDecoder
add_constructors("android/graphics/BitmapRegionDecoder.java", "BitmapRegionDecoder", [
    "public BitmapRegionDecoder(int width, int height) {}",
])

# TimerStat in HealthStats
path = os.path.join(SHIM_ROOT, "android/os/health/HealthStats.java")
if os.path.exists(path):
    content = read_file(path)
    if 'class TimerStat' in content and 'TimerStat(int' not in content:
        content = content.replace(
            'public static class TimerStat {',
            'public static class TimerStat {\n        public TimerStat() {}\n        public TimerStat(int count, long totalTime) {}'
        )
        write_file(path, content)
        print("  Added TimerStat constructors")

# Rgb in ColorSpace
path = os.path.join(SHIM_ROOT, "android/graphics/ColorSpace.java")
if os.path.exists(path):
    content = read_file(path)
    if 'class Rgb' in content and 'Rgb(String' not in content:
        content = content.replace(
            'public static class Rgb extends ColorSpace {',
            'public static class Rgb extends ColorSpace {\n        public Rgb() {}\n        public Rgb(String name, Object model) {}'
        )
        write_file(path, content)
        print("  Added Rgb constructors")

# LinkAddress
add_constructors("android/net/LinkAddress.java", "LinkAddress", [
    "public LinkAddress(java.net.InetAddress address) {}",
    "public LinkAddress(java.net.InetAddress address, int prefixLength) {}",
    "public LinkAddress(java.net.InetAddress address, int prefixLength, int flags) {}",
])

# RectF
add_constructors("android/graphics/RectF.java", "RectF", [
    "public RectF(int left, int top, int right) {}",
    "public RectF(float left, float top, float right, float bottom) {}",
])

# Handler
add_constructors("android/os/Handler.java", "Handler", [
    "public Handler(android.os.Looper looper) {}",
    "public Handler(android.os.Looper looper, Object callback) {}",
])

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
            print("  Added MediaDrmException constructors")

# IdentityCredentialException
path = os.path.join(SHIM_ROOT, "android/security/identity/IdentityCredentialException.java")
if os.path.exists(path):
    content = read_file(path)
    if 'IdentityCredentialException()' not in content:
        m = re.search(r'class IdentityCredentialException[^{]*\{', content)
        if m:
            idx = m.end()
            content = content[:idx] + '\n    public IdentityCredentialException() {}\n    public IdentityCredentialException(String msg) { super(msg); }' + content[idx:]
            write_file(path, content)
            print("  Added IdentityCredentialException constructors")

# RSRuntimeException
add_constructors("android/renderscript/RSRuntimeException.java", "RSRuntimeException", [
    "public RSRuntimeException(String msg) { super(msg); }",
])

# AudioEffect
add_constructors("android/media/audiofx/AudioEffect.java", "AudioEffect", [
    "public AudioEffect(int type) {}",
    "public AudioEffect(java.util.UUID effectType, java.util.UUID type, int priority, int audioSession) throws Exception {}",
])

# CursorWrapper
add_constructors("android/database/CursorWrapper.java", "CursorWrapper", [
    "public CursorWrapper(android.database.Cursor cursor) {}",
])

# BaseDexClassLoader
add_constructors("dalvik/system/BaseDexClassLoader.java", "BaseDexClassLoader", [
    "public BaseDexClassLoader(String dexPath, java.io.File optimizedDir, String libraryPath, ClassLoader parent) { super(parent); }",
])

# PathClassLoader
add_constructors("dalvik/system/PathClassLoader.java", "PathClassLoader", [
    "public PathClassLoader(String dexPath, ClassLoader parent) {}",
    "public PathClassLoader(String dexPath, String libraryPath, ClassLoader parent) {}",
])

# DecimalFormat(DecimalFormat) for NumberFormat
path = os.path.join(SHIM_ROOT, "android/icu/text/DecimalFormat.java")
if os.path.exists(path):
    content = read_file(path)
    if 'DecimalFormat(android.icu.text.DecimalFormat' not in content:
        add_constructors("android/icu/text/DecimalFormat.java", "DecimalFormat", [
            "public DecimalFormat(android.icu.text.DecimalFormat fmt) {}",
        ])

print("\nDone adding constructors.")
