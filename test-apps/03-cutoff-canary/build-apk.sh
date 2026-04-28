#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
GEN_DIR="$BUILD_DIR/gen"
STUB_DIR="$BUILD_DIR/stubs"
CLASSES_DIR="$BUILD_DIR/classes"
OBJ_DIR="$BUILD_DIR/obj"
DIST_DIR="$BUILD_DIR/dist"

TOOLS_DIR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin"
AAPT="$TOOLS_DIR/aapt"
ZIPALIGN="$TOOLS_DIR/zipalign"
DX_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/dx.jar"
APKSIGNER_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/apksigner.jar"
ANDROID_JAR="/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar"
KEY_ALIAS="${KEY_ALIAS:-androiddebugkey}"
KEY_PASS="${KEY_PASS:-android}"
APK_NAME="cutoff-canary-debug.apk"
HILT_APK_NAME="cutoff-canary-hilt-debug.apk"
UNSIGNED_APK="$DIST_DIR/cutoff-canary-unsigned.apk"
ALIGNED_APK="$DIST_DIR/cutoff-canary-aligned.apk"
SIGNED_APK="$DIST_DIR/$APK_NAME"
HILT_UNSIGNED_APK="$DIST_DIR/cutoff-canary-hilt-unsigned.apk"
HILT_ALIGNED_APK="$DIST_DIR/cutoff-canary-hilt-aligned.apk"
HILT_SIGNED_APK="$DIST_DIR/$HILT_APK_NAME"
KEYSTORE_DEFAULT="$BUILD_DIR/debug.keystore"
KEYSTORE="${KEYSTORE:-$KEYSTORE_DEFAULT}"

mkdir -p "$BUILD_DIR" "$GEN_DIR" "$STUB_DIR" "$CLASSES_DIR" "$OBJ_DIR" "$DIST_DIR"
rm -rf "$GEN_DIR" "$STUB_DIR" "$CLASSES_DIR" "$OBJ_DIR" "$DIST_DIR"
mkdir -p "$GEN_DIR" "$STUB_DIR" "$CLASSES_DIR" "$OBJ_DIR" "$DIST_DIR"

if [ ! -x "$AAPT" ] || [ ! -x "$ZIPALIGN" ]; then
    echo "Missing Android build tools under $TOOLS_DIR" >&2
    exit 1
fi

if [ ! -f "$ANDROID_JAR" ] || [ ! -f "$DX_JAR" ] || [ ! -f "$APKSIGNER_JAR" ]; then
    echo "Missing android build dependency under /home/dspfac/aosp-android-11/prebuilts/sdk" >&2
    exit 1
fi

if ! keytool -list -keystore "$KEYSTORE" -storepass "$KEY_PASS" -alias "$KEY_ALIAS" >/dev/null 2>&1; then
    rm -f "$KEYSTORE"
    keytool -genkeypair \
        -keystore "$KEYSTORE" \
        -storepass "$KEY_PASS" \
        -keypass "$KEY_PASS" \
        -alias "$KEY_ALIAS" \
        -dname "CN=Android Debug,O=Android,C=US" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 >/dev/null 2>&1
fi

echo "=== Building Westlake cutoff canary APK ==="

"$AAPT" package -f -m \
    -S "$SCRIPT_DIR/res" \
    -J "$GEN_DIR" \
    -M "$SCRIPT_DIR/AndroidManifest.xml" \
    -I "$ANDROID_JAR" \
    -F "$OBJ_DIR/resources.ap_" \
    --auto-add-overlay

find "$SCRIPT_DIR/src" "$GEN_DIR" -name "*.java" | sort > "$BUILD_DIR/sources.txt"

mkdir -p "$STUB_DIR/com/westlake/engine"
mkdir -p "$STUB_DIR/androidx/activity"
mkdir -p "$STUB_DIR/androidx/activity/contextaware"
mkdir -p "$STUB_DIR/androidx/appcompat/app"
mkdir -p "$STUB_DIR/androidx/core/app"
mkdir -p "$STUB_DIR/androidx/fragment/app"
mkdir -p "$STUB_DIR/androidx/lifecycle"
mkdir -p "$STUB_DIR/androidx/savedstate"
mkdir -p "$STUB_DIR/dagger/hilt/android/internal/managers"
cat > "$STUB_DIR/com/westlake/engine/WestlakeLauncher.java" <<'JAVA'
package com.westlake.engine;

public final class WestlakeLauncher {
    private WestlakeLauncher() {}
    public static boolean appendCutoffCanaryMarker(String message) {
        return false;
    }
    public static void noteMarker(String message) {
    }
}
JAVA

cat > "$STUB_DIR/dagger/hilt/android/internal/managers/ComponentSupplier.java" <<'JAVA'
package dagger.hilt.android.internal.managers;

public interface ComponentSupplier {
    Object get();
}
JAVA

cat > "$STUB_DIR/dagger/hilt/android/internal/managers/ApplicationComponentManager.java" <<'JAVA'
package dagger.hilt.android.internal.managers;

public final class ApplicationComponentManager {
    private final ComponentSupplier supplier;
    private Object component;
    public ApplicationComponentManager(ComponentSupplier supplier) {
        this.supplier = supplier;
    }
    public Object generatedComponent() {
        if (component == null && supplier != null) {
            component = supplier.get();
        }
        return component != null ? component : new Object();
    }
}
JAVA

cat > "$STUB_DIR/dagger/hilt/android/internal/managers/ActivityComponentManager.java" <<'JAVA'
package dagger.hilt.android.internal.managers;

public final class ActivityComponentManager {
    public ActivityComponentManager(android.app.Activity activity) {}
    public Object generatedComponent() { return new Object(); }
}
JAVA

cat > "$STUB_DIR/androidx/core/app/CoreComponentFactory.java" <<'JAVA'
package androidx.core.app;

public class CoreComponentFactory extends android.app.AppComponentFactory {
    public interface CompatWrapped {
        Object getWrapper();
    }
}
JAVA

cat > "$STUB_DIR/androidx/activity/contextaware/OnContextAvailableListener.java" <<'JAVA'
package androidx.activity.contextaware;

public interface OnContextAvailableListener {
    void onContextAvailable(android.content.Context context);
    default void a(android.content.Context context) { onContextAvailable(context); }
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/Lifecycle.java" <<'JAVA'
package androidx.lifecycle;

public abstract class Lifecycle {
    public enum State { DESTROYED, INITIALIZED, CREATED, STARTED, RESUMED }
    public enum Event { ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, ON_ANY }
    public abstract State getCurrentState();
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/LifecycleOwner.java" <<'JAVA'
package androidx.lifecycle;

public interface LifecycleOwner {
    Lifecycle getLifecycle();
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/LifecycleRegistry.java" <<'JAVA'
package androidx.lifecycle;

public class LifecycleRegistry extends Lifecycle {
    private State state = State.INITIALIZED;
    public LifecycleRegistry(LifecycleOwner owner) {}
    @Override public State getCurrentState() { return state; }
    public void handleLifecycleEvent(Event event) {}
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewModel.java" <<'JAVA'
package androidx.lifecycle;

public abstract class ViewModel {
    protected void onCleared() {}
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewModelStore.java" <<'JAVA'
package androidx.lifecycle;

public class ViewModelStore {
    public final void clear() {}
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewModelStoreOwner.java" <<'JAVA'
package androidx.lifecycle;

public interface ViewModelStoreOwner {
    ViewModelStore getViewModelStore();
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewModelProvider.java" <<'JAVA'
package androidx.lifecycle;

public class ViewModelProvider {
    public interface Factory {
        <T extends ViewModel> T create(Class<T> modelClass);
        default <T extends ViewModel> T create(Class<T> modelClass,
                androidx.lifecycle.viewmodel.CreationExtras extras) {
            return create(modelClass);
        }
    }
    public ViewModelProvider(ViewModelStoreOwner owner) {}
    public ViewModelProvider(ViewModelStoreOwner owner, Factory factory) {}
    public ViewModelProvider(ViewModelStore store, Factory factory,
            androidx.lifecycle.viewmodel.CreationExtras extras) {}
    public <T extends ViewModel> T get(Class<T> modelClass) { return null; }
    public <T extends ViewModel> T get(String key, Class<T> modelClass) { return null; }
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/SavedStateHandle.java" <<'JAVA'
package androidx.lifecycle;

public class SavedStateHandle {
    public SavedStateHandle() {}
    public boolean contains(String key) { return false; }
    public <T> T get(String key) { return null; }
    public java.util.Set<String> keys() { return java.util.Collections.emptySet(); }
    public <T> void set(String key, T value) {}
}
JAVA

mkdir -p "$STUB_DIR/androidx/lifecycle/viewmodel"
cat > "$STUB_DIR/androidx/lifecycle/viewmodel/CreationExtras.java" <<'JAVA'
package androidx.lifecycle.viewmodel;

public abstract class CreationExtras {
    public abstract <T> T a(Key<T> key);
    public java.util.Map<Key<?>, Object> b() { return new java.util.LinkedHashMap<>(); }
    public interface Key<T> {}
    public static final class Empty extends CreationExtras {
        public static final Empty c = new Empty();
        private Empty() {}
        @Override public <T> T a(Key<T> key) { return null; }
    }
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/viewmodel/MutableCreationExtras.java" <<'JAVA'
package androidx.lifecycle.viewmodel;

public final class MutableCreationExtras extends CreationExtras {
    public MutableCreationExtras() {}
    @Override public <T> T a(Key<T> key) { return null; }
    public <T> void set(Key<T> key, T value) {}
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewTreeLifecycleOwner.java" <<'JAVA'
package androidx.lifecycle;

public final class ViewTreeLifecycleOwner {
    public static void set(android.view.View view, LifecycleOwner owner) {}
    public static LifecycleOwner get(android.view.View view) { return null; }
}
JAVA

cat > "$STUB_DIR/androidx/lifecycle/ViewTreeViewModelStoreOwner.java" <<'JAVA'
package androidx.lifecycle;

public final class ViewTreeViewModelStoreOwner {
    public static void set(android.view.View view, ViewModelStoreOwner owner) {}
    public static ViewModelStoreOwner get(android.view.View view) { return null; }
}
JAVA

cat > "$STUB_DIR/androidx/savedstate/SavedStateRegistryOwner.java" <<'JAVA'
package androidx.savedstate;

public interface SavedStateRegistryOwner extends androidx.lifecycle.LifecycleOwner {
    SavedStateRegistry getSavedStateRegistry();
}
JAVA

cat > "$STUB_DIR/androidx/savedstate/SavedStateRegistry.java" <<'JAVA'
package androidx.savedstate;

public final class SavedStateRegistry {
    public interface SavedStateProvider {
        android.os.Bundle saveState();
    }
    public void registerSavedStateProvider(String key, SavedStateProvider provider) {}
    public SavedStateProvider b(String key) { return null; }
    public android.os.Bundle consumeRestoredStateForKey(String key) { return null; }
    public void performRestore(android.os.Bundle savedState) {}
    public void performSave(android.os.Bundle outBundle) {}
    public boolean isRestored() { return true; }
}
JAVA

cat > "$STUB_DIR/androidx/savedstate/ViewTreeSavedStateRegistryOwner.java" <<'JAVA'
package androidx.savedstate;

public final class ViewTreeSavedStateRegistryOwner {
    public static void set(android.view.View view, SavedStateRegistryOwner owner) {}
    public static SavedStateRegistryOwner get(android.view.View view) { return null; }
}
JAVA

cat > "$STUB_DIR/androidx/activity/ComponentActivity.java" <<'JAVA'
package androidx.activity;

public class ComponentActivity extends android.app.Activity
        implements androidx.lifecycle.LifecycleOwner,
                androidx.lifecycle.ViewModelStoreOwner,
                androidx.savedstate.SavedStateRegistryOwner {
    public ComponentActivity() {}
    @Override public androidx.lifecycle.Lifecycle getLifecycle() {
        return new androidx.lifecycle.LifecycleRegistry(this);
    }
    @Override public androidx.lifecycle.ViewModelStore getViewModelStore() {
        return new androidx.lifecycle.ViewModelStore();
    }
    @Override public androidx.savedstate.SavedStateRegistry getSavedStateRegistry() {
        return new androidx.savedstate.SavedStateRegistry();
    }
    public void addOnContextAvailableListener(
            androidx.activity.contextaware.OnContextAvailableListener listener) {}
    public void removeOnContextAvailableListener(
            androidx.activity.contextaware.OnContextAvailableListener listener) {}
}
JAVA

cat > "$STUB_DIR/androidx/appcompat/app/AppCompatActivity.java" <<'JAVA'
package androidx.appcompat.app;

public class AppCompatActivity extends androidx.fragment.app.FragmentActivity {
    public AppCompatActivity() {}
    public ActionBar getSupportActionBar() { return null; }
    public static class ActionBar {}
}
JAVA

cat > "$STUB_DIR/androidx/fragment/app/FragmentActivity.java" <<'JAVA'
package androidx.fragment.app;

public class FragmentActivity extends androidx.activity.ComponentActivity {
    public FragmentActivity() {}
    public FragmentManager getSupportFragmentManager() { return null; }
}
JAVA

cat > "$STUB_DIR/androidx/fragment/app/Fragment.java" <<'JAVA'
package androidx.fragment.app;

public class Fragment implements androidx.lifecycle.ViewModelStoreOwner,
        androidx.savedstate.SavedStateRegistryOwner {
    public Fragment() {}
    public void onCreate(android.os.Bundle savedInstanceState) {}
    public android.view.LayoutInflater onGetLayoutInflater(android.os.Bundle savedInstanceState) { return null; }
    public android.view.View onCreateView(android.view.LayoutInflater inflater,
            android.view.ViewGroup container, android.os.Bundle savedInstanceState) { return null; }
    public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {}
    public void onStart() {}
    public void onResume() {}
    public FragmentActivity getActivity() { return null; }
    public android.content.Context getContext() { return null; }
    @Override public androidx.lifecycle.ViewModelStore getViewModelStore() {
        return new androidx.lifecycle.ViewModelStore();
    }
    @Override public androidx.savedstate.SavedStateRegistry getSavedStateRegistry() {
        return new androidx.savedstate.SavedStateRegistry();
    }
    @Override public androidx.lifecycle.Lifecycle getLifecycle() {
        return new androidx.lifecycle.LifecycleRegistry(this);
    }
}
JAVA

cat > "$STUB_DIR/androidx/fragment/app/FragmentManager.java" <<'JAVA'
package androidx.fragment.app;

public class FragmentManager {
    public FragmentTransaction beginTransaction() { return null; }
    public Fragment findFragmentById(int id) { return null; }
    public Fragment findFragmentByTag(String tag) { return null; }
    public java.util.List<Fragment> getFragments() { return null; }
    public boolean executePendingTransactions() { return true; }
}
JAVA

cat > "$STUB_DIR/androidx/fragment/app/FragmentTransaction.java" <<'JAVA'
package androidx.fragment.app;

public abstract class FragmentTransaction {
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment, String tag);
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);
    public abstract void commitNow();
    public boolean isEmpty() { return true; }
}
JAVA

javac --release 8 \
    -cp "$ANDROID_JAR" \
    -sourcepath "$SCRIPT_DIR/src:$GEN_DIR:$STUB_DIR" \
    -d "$CLASSES_DIR" \
    @"$BUILD_DIR/sources.txt" \
    "$STUB_DIR/com/westlake/engine/WestlakeLauncher.java" \
    "$STUB_DIR/dagger/hilt/android/internal/managers/ComponentSupplier.java" \
    "$STUB_DIR/dagger/hilt/android/internal/managers/ApplicationComponentManager.java" \
    "$STUB_DIR/dagger/hilt/android/internal/managers/ActivityComponentManager.java" \
    "$STUB_DIR/androidx/activity/ComponentActivity.java" \
    "$STUB_DIR/androidx/activity/contextaware/OnContextAvailableListener.java" \
    "$STUB_DIR/androidx/appcompat/app/AppCompatActivity.java" \
    "$STUB_DIR/androidx/core/app/CoreComponentFactory.java" \
    "$STUB_DIR/androidx/fragment/app/FragmentActivity.java" \
    "$STUB_DIR/androidx/fragment/app/Fragment.java" \
    "$STUB_DIR/androidx/fragment/app/FragmentManager.java" \
    "$STUB_DIR/androidx/fragment/app/FragmentTransaction.java" \
    "$STUB_DIR/androidx/lifecycle/Lifecycle.java" \
    "$STUB_DIR/androidx/lifecycle/LifecycleOwner.java" \
    "$STUB_DIR/androidx/lifecycle/LifecycleRegistry.java" \
    "$STUB_DIR/androidx/lifecycle/ViewModel.java" \
    "$STUB_DIR/androidx/lifecycle/ViewModelProvider.java" \
    "$STUB_DIR/androidx/lifecycle/SavedStateHandle.java" \
    "$STUB_DIR/androidx/lifecycle/ViewModelStore.java" \
    "$STUB_DIR/androidx/lifecycle/ViewModelStoreOwner.java" \
    "$STUB_DIR/androidx/lifecycle/ViewTreeLifecycleOwner.java" \
    "$STUB_DIR/androidx/lifecycle/ViewTreeViewModelStoreOwner.java" \
    "$STUB_DIR/androidx/lifecycle/viewmodel/CreationExtras.java" \
    "$STUB_DIR/androidx/lifecycle/viewmodel/MutableCreationExtras.java" \
    "$STUB_DIR/androidx/savedstate/SavedStateRegistry.java" \
    "$STUB_DIR/androidx/savedstate/SavedStateRegistryOwner.java" \
    "$STUB_DIR/androidx/savedstate/ViewTreeSavedStateRegistryOwner.java"

rm -rf "$CLASSES_DIR/com/westlake/engine"
rm -rf "$CLASSES_DIR/androidx"
rm -rf "$CLASSES_DIR/dagger"

export SCRIPT_DIR UNSIGNED_APK
export HILT_UNSIGNED_APK

java -jar "$DX_JAR" --dex --output="$BUILD_DIR/classes.dex" "$CLASSES_DIR"

python3 - <<'PY'
import os
import zipfile

script_dir = os.environ["SCRIPT_DIR"]
unsigned_apk = os.environ["UNSIGNED_APK"]
classes_dex = os.path.join(script_dir, "build", "classes.dex")
resources_ap = os.path.join(script_dir, "build", "obj", "resources.ap_")

with zipfile.ZipFile(unsigned_apk, "w", zipfile.ZIP_DEFLATED) as apk:
    apk.write(classes_dex, "classes.dex")
    with zipfile.ZipFile(resources_ap, "r") as res:
        for name in res.namelist():
            apk.writestr(name, res.read(name))
PY

mkdir -p "$OBJ_DIR/hilt-manifest"
cp "$SCRIPT_DIR/AndroidManifest.hilt.xml" "$OBJ_DIR/hilt-manifest/AndroidManifest.xml"
"$AAPT" package -f \
    -S "$SCRIPT_DIR/res" \
    -M "$OBJ_DIR/hilt-manifest/AndroidManifest.xml" \
    -I "$ANDROID_JAR" \
    -F "$OBJ_DIR/resources-hilt.ap_" \
    --auto-add-overlay

python3 - <<'PY'
import os
import zipfile

script_dir = os.environ["SCRIPT_DIR"]
unsigned_apk = os.environ["HILT_UNSIGNED_APK"]
classes_dex = os.path.join(script_dir, "build", "classes.dex")
resources_ap = os.path.join(script_dir, "build", "obj", "resources-hilt.ap_")

with zipfile.ZipFile(unsigned_apk, "w", zipfile.ZIP_DEFLATED) as apk:
    apk.write(classes_dex, "classes.dex")
    with zipfile.ZipFile(resources_ap, "r") as res:
        for name in res.namelist():
            apk.writestr(name, res.read(name))
PY

"$ZIPALIGN" -f 4 "$UNSIGNED_APK" "$ALIGNED_APK"
"$ZIPALIGN" -f 4 "$HILT_UNSIGNED_APK" "$HILT_ALIGNED_APK"

java -jar "$APKSIGNER_JAR" sign \
    --ks "$KEYSTORE" \
    --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "pass:$KEY_PASS" \
    --key-pass "pass:$KEY_PASS" \
    --out "$SIGNED_APK" \
    "$ALIGNED_APK"

java -jar "$APKSIGNER_JAR" sign \
    --ks "$KEYSTORE" \
    --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "pass:$KEY_PASS" \
    --key-pass "pass:$KEY_PASS" \
    --out "$HILT_SIGNED_APK" \
    "$HILT_ALIGNED_APK"

if ! java -jar "$APKSIGNER_JAR" verify "$SIGNED_APK" >/dev/null 2>&1; then
    echo "WARNING: apksigner verify failed on this host toolchain; signed APK kept anyway." >&2
fi
if ! java -jar "$APKSIGNER_JAR" verify "$HILT_SIGNED_APK" >/dev/null 2>&1; then
    echo "WARNING: apksigner verify failed for Hilt APK on this host toolchain; signed APK kept anyway." >&2
fi

echo "Built APK:"
echo "  $SIGNED_APK"
echo "  $HILT_SIGNED_APK"
apk_sha256="$(sha256sum "$SIGNED_APK")"
hilt_apk_sha256="$(sha256sum "$HILT_SIGNED_APK")"
printf "%s\n" "$apk_sha256" > "$SIGNED_APK.sha256"
printf "%s\n" "$hilt_apk_sha256" > "$HILT_SIGNED_APK.sha256"
echo "SHA256:"
echo "  $apk_sha256"
echo "  $hilt_apk_sha256"
