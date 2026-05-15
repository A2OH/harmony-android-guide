#!/bin/bash
# ============================================================================
# M7-Step1 -- NoiceLauncher.dex build helper
#
# Compiles NoiceLauncher.java + NoiceDiscoverWrapper.java +
# DiscoverWrapperBase.java + CharsetPrimer.java into one .dex consumable
# by dalvikvm for run-noice-westlake.sh.
#
# NoiceLauncher.main() calls NoiceDiscoverWrapper.main(), so the dex
# needs both classes in the same dex/classpath.  All four files are
# already used by build_discover.sh -- the only new entry is
# NoiceLauncher.java itself.  See test/NoiceLauncher.java for the
# rationale (re-use NoiceDiscoverWrapper's JNI-bound natives without
# touching art-latest).
#
# Output: out/NoiceLauncher.dex (~60 KB; includes NoiceLauncher +
#         NoiceDiscoverWrapper + DiscoverWrapperBase + CharsetPrimer).
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/noicelauncher-build-$$
OUT_DEX="$SCRIPT_DIR/out/NoiceLauncher.dex"

mkdir -p "$BUILD/classes" \
         "$BUILD/stubs/android/os" \
         "$BUILD/stubs/dalvik/system" \
         "$BUILD/stubs/com/westlake/services"

# Compile-time stubs (must NOT be packaged into the dex; runtime classes
# come from aosp-shim.dex / framework.jar).  Mirrors build_discover.sh.
cat > "$BUILD/stubs/android/os/ServiceManager.java" <<'EOF'
package android.os;
public final class ServiceManager {
    public static String[] listServices() { throw new RuntimeException("stub"); }
    public static IBinder getService(String name) { throw new RuntimeException("stub"); }
}
EOF

cat > "$BUILD/stubs/dalvik/system/PathClassLoader.java" <<'EOF'
package dalvik.system;
public class PathClassLoader extends ClassLoader {
    public PathClassLoader(String s, ClassLoader p) { super(p); }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException("stub");
    }
}
EOF

cat > "$BUILD/stubs/com/westlake/services/ServiceRegistrar.java" <<'EOF'
package com.westlake.services;
public final class ServiceRegistrar {
    public static int registerAllServices() { return 0; }
}
EOF

javac -source 1.8 -target 1.8 -classpath "$ANDROID_JAR" \
    -d "$BUILD/classes" \
    "$BUILD/stubs/android/os/ServiceManager.java" \
    "$BUILD/stubs/dalvik/system/PathClassLoader.java" \
    "$BUILD/stubs/com/westlake/services/ServiceRegistrar.java" \
    "$SCRIPT_DIR/test/CharsetPrimer.java" \
    "$SCRIPT_DIR/test/DiscoverWrapperBase.java" \
    "$SCRIPT_DIR/test/NoiceDiscoverWrapper.java" \
    "$SCRIPT_DIR/test/NoiceLauncher.java" 2>&1 | grep -v "^Note:" || true

for c in NoiceLauncher NoiceDiscoverWrapper DiscoverWrapperBase CharsetPrimer; do
    if [ ! -f "$BUILD/classes/${c}.class" ]; then
        echo "ERROR: javac failed (${c}.class missing)"
        rm -rf "$BUILD"
        exit 1
    fi
done

# Package the four real classes (NOT the stubs) into the dex.
# Wildcard picks up inner / anonymous classes
# (NoiceDiscoverWrapper$1 is the anonymous Printer in its main()).
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/NoiceLauncher"*.class       "$BUILD/dex-only/"
cp "$BUILD/classes/NoiceDiscoverWrapper"*.class "$BUILD/dex-only/"
cp "$BUILD/classes/DiscoverWrapperBase"*.class  "$BUILD/dex-only/"
cp "$BUILD/classes/CharsetPrimer"*.class        "$BUILD/dex-only/"

mkdir -p "$SCRIPT_DIR/out"
"$DX" --dex --output="$OUT_DEX" "$BUILD/dex-only" >/dev/null 2>&1

if [ ! -s "$OUT_DEX" ]; then
    echo "ERROR: $OUT_DEX not produced (or empty)"
    rm -rf "$BUILD"
    exit 1
fi

ls -lh "$OUT_DEX"
rm -rf "$BUILD"
echo "Done."
