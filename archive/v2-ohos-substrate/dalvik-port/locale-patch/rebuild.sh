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

# ============================================================================
# CR-Z (2026-05-15): patch java.nio.ByteOrder.<clinit> so it doesn't depend
# on the missing native isLittleEndian().
#
# Without this, the kitkat dalvikvm on the rk3568 board throws
# UnsatisfiedLinkError when Lz6/a.get triggers ByteOrder.<clinit> via the
# Properties/Security/Algorithm chain inside Dagger DI graph construction.
# That UnsatisfiedLinkError escapes Hilt's inject method (Lv3/e;->m()V) and
# leaves every @Inject field unassigned, surfacing later as
# `kotlin.UninitializedPropertyAccessException: lateinit property
# subscriptionBillingProvider has not been initialized` at MainActivity.
# onResume:458.
#
# Fix: replace the body of Ljava/nio/ByteOrder;.<clinit>()V so it bypasses
# the native call. The result MUST be functionally identical to the AOSP
# behavior on an arm32 (little-endian) host:
#   BIG_ENDIAN  = new ByteOrder("BIG_ENDIAN", true)    // needsSwap = LE  (true on LE)
#   LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN", false) // needsSwap = !LE (false on LE)
#   NATIVE_ORDER = LITTLE_ENDIAN
# We hard-code the LE branch since both ohos targets (arm32 + arm64) are LE.
#
# Generic, not per-app: this is a pure BCP-side patch. ANY app that
# transitively initializes ByteOrder (java.security.Security on the dalvik-
# kitkat BCP loads Properties.load, which mmap-reads via ByteOrder) trips
# the same UnsatisfiedLinkError. The hello-color-apk smoke and trivial-
# activity tests don't transitively pull Properties/Security and have never
# tripped this.
# ============================================================================
log "[5b/8] patch java/nio/ByteOrder.smali <clinit> (bypass missing native isLittleEndian)"
BO="$WORK/smali-orig/java/nio/ByteOrder.smali"
[ -f "$BO" ] || fail "missing ByteOrder.smali in smali-orig tree"
# Sanity-check the unmodified <clinit> body to fail loudly if the
# baksmali output drifts.
if ! grep -q '^    invoke-static {}, Ljava/nio/ByteOrder;->isLittleEndian()Z$' "$BO"; then
    fail "ByteOrder.smali does NOT contain the expected unmodified invoke-static; refusing to patch (input drifted)."
fi
# In-place edit: replace the single `invoke-static` line with a no-op-ish
# label preserving instruction count, plus a sentinel comment so the patched
# file is greppable. The follow-up `move-result v0` consumes the still-set
# v0 — but we precede it with `const/4 v0, 0x1` (assigning LE=true) AND
# wrap the move-result as a `nop` so the smali decoder stays happy.
#
# Easiest sema-preserving rewrite: replace the two-instruction sequence
#   invoke-static {}, Ljava/nio/ByteOrder;->isLittleEndian()Z
#   move-result v0
# with
#   const/4 v0, 0x1     # LE=true on arm32/arm64
#   nop
# That keeps the instruction count and register state identical for the rest
# of <clinit>.
# Use python for a robust regex rewrite (sed's newline handling is brittle
# across the .line directives baksmali emits between instructions).
BO="$BO" python3 - <<'PYEOF'
import os, pathlib, re, sys
p = pathlib.Path(os.environ["BO"])
s = p.read_text()
# Match invoke-static + the immediately-following move-result v0, allowing
# blank lines / .line directives between them (baksmali emits .line markers).
pat = re.compile(
    r'^(\s*)invoke-static \{\}, Ljava/nio/ByteOrder;->isLittleEndian\(\)Z\s*$\n'
    r'((?:\s*\.line \d+\s*\n|\s*\n)*)'
    r'\s*move-result v0\s*$',
    re.MULTILINE)
m = pat.search(s)
if not m:
    print("[locale-patch] ByteOrder.smali: pattern not found", file=sys.stderr)
    sys.exit(1)
indent = m.group(1)
mid = m.group(2) or ""
# Keep instruction-count parity: const/4 (1 word) + nop (1 word) replaces
# invoke-static (2 words) + move-result (1 word). dalvik bytecode lengths
# differ — but smali assembles bytecode word-by-word and the offsets are
# patch-tracked by the assembler, so this is safe.
repl = f"{indent}const/4 v0, 0x1\n{mid}{indent}nop"
s2 = s[:m.start()] + repl + s[m.end():]
p.write_text(s2)
print(f"[locale-patch] ByteOrder.smali patched at byte {m.start()}..{m.end()}")
PYEOF
if grep -q '^    invoke-static {}, Ljava/nio/ByteOrder;->isLittleEndian()Z$' "$BO"; then
    fail "ByteOrder.smali patch FAILED: invoke-static still present"
fi
log "  ByteOrder.<clinit> isLittleEndian() bypass applied (LE=true)"

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
