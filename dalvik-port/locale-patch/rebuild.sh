#!/usr/bin/env bash
# ============================================================================
# rebuild.sh — regenerate the CR-Y+1 patched core-android-x86.jar.
#
# This is the source-of-truth build script for the BCP-level
# java.util.Locale.forLanguageTag / Locale.toLanguageTag patch. Run it any
# time src/java/util/Locale.java changes.
#
# Pipeline:
#   1. javac:  src/java/util/Locale.java + src/libcore/icu/ICU.java -> .class
#   2. d8:     Locale.class -> patch-only classes.dex (10 KB; contains just
#              the augmented Locale)
#   3. baksmali: ../core-android-x86.jar/classes.dex -> smali/ (the kitkat
#              libcore disassembly)
#   4. baksmali: patch-only dex -> smali-patched/ (extract the new methods)
#   5. append: graft the 8 new methods (2 public + 6 private helpers) into
#              smali/java/util/Locale.smali  -- ALL existing methods preserved
#   6. smali:  smali/ -> classes-patched.dex
#   7. zip:    repack into core-android-x86.jar (overwrites classes.dex in
#              the jar; META-INF + property files preserved)
#   8. copy:   final artifact -> ohos-deploy/core-android-x86.jar (BCP slot)
#
# Tools required (already on PATH inside this WSL):
#   - javac (Java 8+ source/target)
#   - d8 from /home/dspfac/android-sdk/build-tools/34.0.0/
#   - smali / baksmali 2.5.2 jars in /tmp (downloaded from bitbucket once)
#   - zip
#
# Verification (no per-app code; pure AOSP-default RFC-5646 prefix parser):
#   - dexdump method-name diff vs original: 52 preserved, 8 added.
#   - dalvikvm BCP verify pass (host x86_64): all method bodies green-grm.
#   - emoji2.attachBaseContext / Locale.forLanguageTag NSME unblocked.
# ============================================================================

set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$HERE/../.." && pwd)"

SRC_LOCALE="$HERE/src/java/util/Locale.java"
SRC_ICU_STUB="$HERE/src/libcore/icu/ICU.java"
ORIG_JAR="$REPO_ROOT/dalvik-port/core-android-x86.jar"
DEPLOY_JAR="$REPO_ROOT/ohos-deploy/core-android-x86.jar"

D8="${D8:-/home/dspfac/android-sdk/build-tools/34.0.0/d8}"
BAKSMALI="${BAKSMALI:-/tmp/baksmali-2.5.2.jar}"
SMALI="${SMALI:-/tmp/smali-2.5.2.jar}"

log()  { echo "[$(date +%H:%M:%S)] [locale-patch] $*"; }
fail() { echo "[locale-patch] ERR: $*" >&2; exit 1; }

[ -f "$SRC_LOCALE" ]   || fail "missing source: $SRC_LOCALE"
[ -f "$ORIG_JAR" ]     || fail "missing original jar: $ORIG_JAR"
[ -x "$D8" ]           || fail "missing d8 at $D8"
[ -f "$BAKSMALI" ]     || fail "missing baksmali at $BAKSMALI (download from https://bitbucket.org/JesusFreke/smali/downloads/baksmali-2.5.2.jar)"
[ -f "$SMALI" ]        || fail "missing smali at $SMALI (download from https://bitbucket.org/JesusFreke/smali/downloads/smali-2.5.2.jar)"

WORK="$HERE/build"
rm -rf "$WORK"
mkdir -p "$WORK/classes" "$WORK/dex" "$WORK/smali-orig" "$WORK/smali-patch"

log "[1/8] javac Locale.java + ICU stub"
javac -source 1.8 -target 1.8 -d "$WORK/classes" -sourcepath "$HERE/src" \
    "$SRC_LOCALE" "$SRC_ICU_STUB" 2>&1 | grep -v "^Note\|^warning\|^4 warnings" || true

log "[2/8] d8 Locale.class -> patch-only dex"
"$D8" --min-api 19 --output "$WORK/dex" "$WORK/classes/java/util/Locale.class" \
    2>&1 | grep -v "^Warning" || true

log "[3/8] baksmali original core-android-x86.jar's classes.dex"
unzip -p "$ORIG_JAR" classes.dex > "$WORK/orig-classes.dex"
java -jar "$BAKSMALI" d -o "$WORK/smali-orig" "$WORK/orig-classes.dex"

log "[4/8] baksmali patch-only dex"
java -jar "$BAKSMALI" d -o "$WORK/smali-patch" "$WORK/dex/classes.dex"

log "[5/8] graft 8 new methods into Locale.smali"
awk '
  /^\.method.*(forLanguageTag|toLanguageTag|isAlnum|isAllAlpha|isAllDigit|isWellFormedVariant|scrubPrivateUse|splitTag)\(/ { in_method=1 }
  in_method { print }
  in_method && /^\.end method/ { print ""; in_method=0 }
' "$WORK/smali-patch/java/util/Locale.smali" > "$WORK/new-methods.smali"
local_count="$(grep -c '^\.method' "$WORK/new-methods.smali")"
log "  extracted $local_count new method bodies"
[ "$local_count" = "8" ] || fail "expected 8 new methods, found $local_count"
printf '\n' >> "$WORK/smali-orig/java/util/Locale.smali"
cat "$WORK/new-methods.smali" >> "$WORK/smali-orig/java/util/Locale.smali"

log "[6/8] smali assemble back to classes.dex"
java -jar "$SMALI" a -o "$WORK/classes-patched.dex" "$WORK/smali-orig"

log "[7/8] repack jar (overwrite classes.dex inside)"
cp "$ORIG_JAR" "$WORK/core-android-x86.jar"
( cd "$WORK" && zip -d core-android-x86.jar classes.dex >/dev/null )
mv "$WORK/classes-patched.dex" "$WORK/classes.dex"
( cd "$WORK" && zip core-android-x86.jar classes.dex >/dev/null )

log "[8/8] deploy"
cp "$WORK/core-android-x86.jar" "$DEPLOY_JAR"
ls -la "$DEPLOY_JAR"

log "DONE — patched jar at $DEPLOY_JAR"
log "Next: scripts/run-ohos-test.sh push-bcp  (pushes ohos-deploy/* to the rk3568 board)"
