#!/bin/bash
# ============================================================================
# M7-Step2 -- NoiceProductionLauncher.dex build helper
#
# Compiles NoiceProductionLauncher.java + NoiceDiscoverWrapper.java +
# DiscoverWrapperBase.java + CharsetPrimer.java into one .dex consumable
# by dalvikvm for run-noice-westlake.sh --production.
#
# Same pattern as build_noice_launcher.sh (M7-Step1) -- the production
# launcher's reuse of NoiceDiscoverWrapper.println/eprintln natives is
# the same trick M7-Step1 uses to avoid editing art-latest.
#
# Output: out/NoiceProductionLauncher.dex
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
BUILD=/tmp/noiceprodlauncher-build-$$
OUT_DEX="$SCRIPT_DIR/out/NoiceProductionLauncher.dex"

mkdir -p "$BUILD/classes" \
         "$BUILD/stubs/android/os" \
         "$BUILD/stubs/dalvik/system" \
         "$BUILD/stubs/com/westlake/services"

# Compile-time stubs -- runtime classes come from aosp-shim.dex /
# framework.jar / app APK.  Mirror build_noice_launcher.sh.
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
    "$SCRIPT_DIR/test/NoiceProductionLauncher.java" 2>&1 | grep -v "^Note:" || true

for c in NoiceProductionLauncher NoiceDiscoverWrapper DiscoverWrapperBase CharsetPrimer; do
    if [ ! -f "$BUILD/classes/${c}.class" ]; then
        echo "ERROR: javac failed (${c}.class missing)"
        rm -rf "$BUILD"
        exit 1
    fi
done

# Package real classes (NOT stubs) into the dex.
mkdir -p "$BUILD/dex-only"
cp "$BUILD/classes/NoiceProductionLauncher"*.class "$BUILD/dex-only/"
cp "$BUILD/classes/NoiceDiscoverWrapper"*.class    "$BUILD/dex-only/"
cp "$BUILD/classes/DiscoverWrapperBase"*.class     "$BUILD/dex-only/"
cp "$BUILD/classes/CharsetPrimer"*.class           "$BUILD/dex-only/"

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
