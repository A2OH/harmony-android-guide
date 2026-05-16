#!/usr/bin/env python3
"""apply_BUILD_gn.py — minimal libbms BUILD.gn patcher (Route C).

Per feedback.txt (2026-04-17) Route C architectural cleanup: APK AXML
parsing lives in the adapter-owned libapk_installer.so, NOT compiled into
libbms. libbms keeps ZERO AOSP-chain DT_NEEDED (no libandroidfw, no
libbase/libcutils/libutils/liblog/libicuuc) so BMS SA can start in OH
foundation without /system/android/lib being in the namespace linker path.

The only BUILD.gn modification this script makes:

    defines += [ "OH_ADAPTER_ANDROID" ]

The define enables the #ifdef OH_ADAPTER_ANDROID branch in bundle_installer.cpp
which runtime-dlopen's libapk_installer.so to dispatch .apk installs.

Fully idempotent: re-running is a no-op.

If an earlier (now-obsolete) apply pattern left stale entries in BUILD.gn
(sources += adapter_apk_install_minimal.cpp; ldflags for -landroidfw;
AOSP -I cflags), this script detects and removes them before re-applying
the define — migrations from pre-Route-C builds are automatic. Safest path
is still to restore BUILD.gn from BUILD.gn.adapter_orig before rerunning.

Usage:
    apply_BUILD_gn.py [--bms-build-gn /path/to/BUILD.gn]
"""
import argparse
import os
import re
import sys

DEFAULT_BMS = "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/services/bundlemgr/BUILD.gn"


def strip_old_adapter_patches(text: str, actions: list) -> str:
    """Remove any stale pre-Route-C modifications (sources, ldflags, cflags_cc -I)."""
    # Strip: adapter_apk_install_minimal.cpp source insertion block.
    block_re = re.compile(
        r"\n\s*#\s*gap 0\.2/0\.6[^\n]*\n"
        r"(?:\s*#[^\n]*\n)*"
        r'\s*sources \+= \[ "src/adapter_apk_install_minimal\.cpp" \][^\n]*\n'
        r'(?:\s*sources \+= \[ "src/apk_manifest_parser\.cpp" \][^\n]*\n?)?',
        re.MULTILINE,
    )
    if block_re.search(text):
        text = block_re.sub("\n", text)
        actions.append("stripped stale adapter_apk_install_minimal.cpp source insertion")

    # Strip: ldflags for -landroidfw (with or without DT_RUNPATH form).
    ldflags_re = re.compile(
        r"\n\s*#\s*gap 6[^\n]*\n"
        r"(?:\s*#[^\n]*\n)*"
        r"\s*ldflags\s*=\s*\[[^\]]*-landroidfw[^\]]*\]\n",
        re.MULTILINE,
    )
    if ldflags_re.search(text):
        text = ldflags_re.sub("\n", text)
        actions.append("stripped stale libandroidfw ldflags")

    # Strip: AOSP header -I entries from cflags_cc (appended by the old Step 5).
    # Each line matches: "-I<aosp>/<aosp-subpath>",
    aosp_i_re = re.compile(
        r'\s*"-I[^"]*?/(?:aosp|frameworks/base/libs/androidfw|'
        r"system/libbase|system/core/(?:lib(?:utils|cutils|system)|include)|"
        r"system/incremental_delivery|external/fmtlib|"
        r'system/logging/liblog|frameworks/native)[^"]*",\s*\n',
    )
    new_text = aosp_i_re.sub("", text)
    if new_text != text:
        text = new_text
        actions.append("stripped stale AOSP -I lines from cflags_cc")

    return text


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("--bms-build-gn", default=os.environ.get("BMS_BUILD_GN", DEFAULT_BMS))
    args = p.parse_args()

    if not os.path.isfile(args.bms_build_gn):
        print(f"[apply_BUILD_gn] ERROR: not found: {args.bms_build_gn}", file=sys.stderr)
        return 1

    with open(args.bms_build_gn) as f:
        text = f.read()

    actions = []

    # ---- Step 1: locate libbms target span ----
    m = re.search(r'ohos_shared_library\("libbms"\)\s*\{', text)
    if not m:
        print("[apply_BUILD_gn] ERROR: libbms target not found", file=sys.stderr)
        return 1
    start = m.end()
    depth = 1
    i = start
    while i < len(text) and depth > 0:
        if text[i] == "{":
            depth += 1
        elif text[i] == "}":
            depth -= 1
        i += 1
    end = i
    target_text = text[start:end]
    actions.append(f"located libbms target span: chars {start}..{end}")

    # ---- Step 2: strip stale pre-Route-C patches, if any ----
    target_text = strip_old_adapter_patches(target_text, actions)

    # ---- Step 3: insert OH_ADAPTER_ANDROID define (idempotent) ----
    if "OH_ADAPTER_ANDROID" not in target_text:
        defines_close_re = re.compile(r'^(\s*)("LOG_DOMAIN[^,\n]*,)\s*\n(\s*)\]', re.MULTILINE)
        dc = defines_close_re.search(target_text)
        if dc:
            indent = dc.group(1)
            insertion = (
                f'\n{indent[:-2]}defines += [ "OH_ADAPTER_ANDROID" ]'
                f"  # enables bundle_installer.cpp .apk routing → libapk_installer.so (Route C)"
            )
            close_pos = target_text.find("]", dc.end() - 1)
            close_end = close_pos + 1
            target_text = target_text[:close_end] + insertion + target_text[close_end:]
            actions.append("inserted OH_ADAPTER_ANDROID define")
        else:
            print("[apply_BUILD_gn] WARN: defines anchor not found; skipping OH_ADAPTER_ANDROID", file=sys.stderr)
    else:
        actions.append("OH_ADAPTER_ANDROID already present (idempotent skip)")

    # ---- Step 4: write back if changed ----
    new_text = text[:start] + target_text + text[end:]
    if new_text == text:
        print("[apply_BUILD_gn] no changes (already fully patched)")
    else:
        # Backup the original if no .adapter_orig exists yet.
        bak = args.bms_build_gn + ".adapter_orig"
        if not os.path.isfile(bak):
            with open(bak, "w") as f:
                f.write(text)
            print(f"[apply_BUILD_gn] backup saved: {bak}")
        with open(args.bms_build_gn, "w") as f:
            f.write(new_text)
        print(f"[apply_BUILD_gn] patched: {args.bms_build_gn}")

    print()
    print("Actions taken:")
    for a in actions:
        print(f"  - {a}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
