# Android 11 AOSP Build System - Code Review Report

## Executive Summary

The Android 11 build system is a dual-architecture system comprising the **Soong** build system (Android.bp files, written in Go, backed by Blueprint/Ninja) and the **legacy Make** build system (Android.mk files). Soong is the primary build system for module definitions, while Make handles product configuration, image assembly, and orchestration. The build system manages compilation of Java/Kotlin, C/C++, AIDL, resources (aapt2), dexing (d8/r8), signing, and packaging across multiple architectures and build variants.

**Key source locations:**
- `/build/soong/` -- Soong build system (Go implementation)
- `/build/make/` -- Make build system (Makefile infrastructure)
- `/build/make/core/` -- Core Make rules and configuration
- `/build/make/target/` -- Product and board definitions
- `/build/blueprint/` -- Blueprint meta-build system (Soong's foundation)

---

## 1. Build System Architecture Overview

### 1.1 Soong (Android.bp)

Soong is a Go-based build system built on top of Google's Blueprint framework. It parses `Android.bp` files (a JSON-like declarative format), resolves dependencies, and generates Ninja build files.

**Entry point:** `build/soong/soong_ui.bash`

**Architecture layers:**
1. **Blueprint** (`build/blueprint/`) -- Generic meta-build system that parses `.bp` files and manages module/dependency graphs
2. **Soong Android package** (`build/soong/android/`) -- Android-specific module base types, configuration, mutators, and path handling
3. **Language-specific packages** -- `build/soong/java/`, `build/soong/cc/`, `build/soong/python/`, `build/soong/rust/`
4. **Module-specific packages** -- `build/soong/apex/`, `build/soong/genrule/`

**Key file:** `build/soong/android/register.go` -- Central module type registration:
```go
type ModuleFactory func() Module

func RegisterModuleType(name string, factory ModuleFactory) {
    moduleTypes = append(moduleTypes, moduleType{name, factory})
}
```

**Key file:** `build/soong/android/module.go` -- Defines `BuildParams`, `EarlyModuleContext`, `BaseModuleContext`, and the core module lifecycle interfaces. Every module flows through: parse properties -> run mutators -> resolve dependencies -> generate build actions.

### 1.2 Make (Android.mk)

The legacy Make system handles:
- Product/device/board configuration
- System image assembly
- Build variant selection
- Integration with Soong outputs

**Entry point:** `build/make/core/config.mk` (loaded by Kati)

**Key directories:**
- `build/make/core/` -- Core build rules (~100+ .mk files)
- `build/make/target/product/` -- Product definitions (aosp_arm64.mk, base_system.mk, etc.)
- `build/make/target/board/` -- Board configurations (generic_arm64, etc.)

**Key file:** `build/make/core/clear_vars.mk` -- Resets all `LOCAL_*` variables between module definitions. Lists every variable the Make system uses (200+ variables including `LOCAL_CERTIFICATE`, `LOCAL_AIDL_INCLUDES`, `LOCAL_AAPT_FLAGS`, etc.).

### 1.3 Build Flow

```
source build/envsetup.sh
    -> lunch <product>-<variant>
        -> soong_ui.bash
            -> Blueprint parses Android.bp files
            -> Soong mutators run (arch, SDK, VNDK, sanitizer, etc.)
            -> Ninja files generated
            -> Kati processes Android.mk files
            -> Ninja executes combined build graph
```

---

## 2. Build Environment Setup

### 2.1 envsetup.sh

**File:** `build/make/envsetup.sh`

Sources shell functions into the developer's environment. Key functions:

| Function | Purpose |
|----------|---------|
| `lunch` | Select product and build variant |
| `m` | Build from tree top |
| `mm` | Build modules in current directory and dependencies |
| `mmm` | Build modules in specified directories |
| `tapas` | Build specific apps: `tapas App1 App2 arm64 userdebug` |
| `croot` | Navigate to tree top |
| `gomod` | Go to directory containing a module |
| `allmod` | List all modules |

**Helper greps:** `cgrep` (C/C++), `jgrep` (Java), `resgrep` (resources), `mgrep` (Makefiles/bp), `sgrep` (all source).

### 2.2 lunch Function

**File:** `build/make/envsetup.sh` (line 598)

The `lunch` function accepts a `<product>-<variant>` string, parses it into `TARGET_PRODUCT` and `TARGET_BUILD_VARIANT`, and configures the environment:

```bash
function lunch() {
    # Parse selection into product and variant
    product=${selection%%-*}
    variant_and_version=${selection#*-}

    TARGET_PRODUCT=$product \
    TARGET_BUILD_VARIANT=$variant \
    build_build_var_cache

    export TARGET_PRODUCT=$(get_build_var TARGET_PRODUCT)
    export TARGET_BUILD_VARIANT=$(get_build_var TARGET_BUILD_VARIANT)
    export TARGET_BUILD_TYPE=release
}
```

### 2.3 Build Variants

**File:** `build/make/envsetup.sh` (line 149)

Three build variants:
```bash
VARIANT_CHOICES=(user userdebug eng)
```

| Variant | Description |
|---------|-------------|
| `user` | Production build. Limited debugging, `ro.debuggable=0` |
| `userdebug` | Like user but with root access and debugging enabled |
| `eng` | Development build with additional debugging tools and relaxed security |

---

## 3. Soong Module Types

### 3.1 Java Module Types

**File:** `build/soong/java/java.go` -- `RegisterJavaBuildComponents()`

| Module Type | Purpose |
|-------------|---------|
| `java_library` | Compile Java sources into a jar |
| `java_library_static` | Static Java library |
| `java_library_host` | Host-only Java library |
| `java_binary` | Java binary (device) |
| `java_binary_host` | Java binary (host) |
| `java_test` | Java test module |
| `java_test_host` | Host Java test |
| `java_import` | Prebuilt jar import |
| `java_defaults` | Default properties for java modules |
| `dex_import` | Prebuilt dex import |

**Key `CompilerProperties`** (from `build/soong/java/java.go` line 142):
```
srcs                 -- Source files (.java, .kt, .aidl, .proto, .logtags)
exclude_srcs         -- Files to exclude
libs                 -- Classpath dependencies (not compiled into jar)
static_libs          -- Dependencies compiled into resulting jar
plugins              -- Annotation processor modules
java_version         -- Java source/target version
javacflags           -- Additional javac flags
kotlincflags         -- Kotlin compiler flags
jarjar_rules         -- JarJar rules file for package renaming
services             -- META-INF/services entries
```

### 3.2 Android App Module Types

**File:** `build/soong/java/app.go` -- `RegisterAppBuildComponents()`

| Module Type | Purpose |
|-------------|---------|
| `android_app` | Full Android application |
| `android_test` | Android instrumentation test |
| `android_test_helper_app` | Helper app for tests |
| `android_app_certificate` | Signing certificate definition |
| `override_android_app` | Override properties of an android_app |
| `android_app_import` | Import prebuilt APK |
| `android_app_set` | Import APK set (split APKs) |
| `runtime_resource_overlay` | RRO package |

**Key `appProperties`** (from `build/soong/java/app.go` line 187):
```
additional_certificates  -- Extra signing certificates
privileged              -- Install to priv-app directory
package_splits          -- Resource split labels
overrides               -- Modules this overrides
jni_libs                -- Native libraries to include
use_embedded_native_libs -- Store JNI libs uncompressed in APK
use_embedded_dex        -- Store dex uncompressed
updatable               -- Whether app is mainline-updatable
certificate             -- Signing certificate (overridable)
```

### 3.3 C/C++ Module Types

**File:** `build/soong/cc/cc.go` -- `RegisterCCBuildComponents()` and related files

| Module Type | Purpose |
|-------------|---------|
| `cc_library` | Shared + static library (builds both) |
| `cc_library_shared` | Shared library only |
| `cc_library_static` | Static library only |
| `cc_library_headers` | Header-only library |
| `cc_binary` | Executable |
| `cc_binary_host` | Host executable |
| `cc_test` | Native test |
| `cc_fuzz` | Fuzz test |
| `cc_object` | Object file |
| `cc_defaults` | Default properties |
| `cc_prebuilt_library_shared` | Prebuilt shared library |
| `cc_prebuilt_binary` | Prebuilt binary |
| `ndk_library` | NDK stub library |
| `llndk_library` | LL-NDK library |
| `vndk_prebuilt_shared` | VNDK prebuilt |

**CC mutator pipeline** (from `build/soong/cc/cc.go` line 44):
```
PreDepsMutators:  sdk -> vndk -> link -> ndk_api -> test_per_src -> version -> begin -> sysprop_cc -> vendor_snapshot
PostDepsMutators: asan -> hwasan -> fuzzer -> cfi -> scs -> tsan -> sanitize_runtime -> coverage -> vndk_deps -> lto
```

**Key `LibraryProperties`** (from `build/soong/cc/library.go` line 34):
```
stubs.symbol_file    -- Symbol map for stub generation
stubs.versions       -- Stub versions to generate
stem                 -- Output file name override
suffix               -- Name suffix
aidl.export_aidl_headers  -- Export AIDL-generated headers
```

### 3.4 Other Module Types

| Module Type | File | Purpose |
|-------------|------|---------|
| `genrule` | `build/soong/genrule/genrule.go` | Custom build rule with command |
| `gensrcs` | `build/soong/genrule/genrule.go` | Generate sources per-file |
| `filegroup` | `build/soong/android/filegroup.go` | Group source files for references |
| `apex` | `build/soong/apex/apex.go` | APEX module (updatable package) |
| `droiddoc` | `build/soong/java/droiddoc.go` | API documentation generation |
| `droidstubs` | `build/soong/java/droiddoc.go` | API stub generation (metalava) |
| `java_sdk_library` | `build/soong/java/sdk_library.go` | SDK library with API management |
| `python_binary_host` | `build/soong/python/` | Host Python binary |

---

## 4. SDK and API Level Configuration

### 4.1 Platform SDK Version

**File:** `build/make/core/version_defaults.mk`

Android 11 defines:
```makefile
PLATFORM_VERSION_LAST_STABLE := 11
PLATFORM_SDK_VERSION := 30
PLATFORM_VERSION_CODENAME.RP1A := REL   # REL = released
```

The `PLATFORM_SDK_VERSION` (30) is the canonical API level. `FutureApiLevel` is defined as 10000 in `build/soong/android/config.go` for unreleased APIs.

### 4.2 SDK Version Specification in Modules

**File:** `build/soong/java/sdk.go`

The `sdkSpec` system defines how modules target different API surfaces:

```go
type sdkKind int
const (
    sdkInvalid sdkKind = iota
    sdkNone            // no SDK (framework internal)
    sdkCore            // core Java APIs only
    sdkCorePlatform    // core platform APIs (unstable)
    sdkPublic          // public SDK APIs
    sdkSystem          // system APIs
    sdkTest            // test APIs
    sdkModule          // module APIs (for mainline modules)
    sdkSystemServer    // system_server APIs
    sdkPrivate         // platform internals (no sdk_version set)
)
```

**Module properties** (from `build/soong/java/java.go` line 261):

| Property | Purpose |
|----------|---------|
| `sdk_version` | SDK to compile against (e.g., `"current"`, `"system_current"`, `"30"`) |
| `min_sdk_version` | Minimum SDK version for runtime |
| `target_sdk_version` | Target SDK version in manifest |
| `platform_apis` | Use platform (private) APIs instead of SDK |
| `system_modules` | Java module system for Java 9+ |

**Validation rule** (line 120-131): `platform_apis` and `sdk_version` are mutually exclusive -- exactly one must be set for android_app modules.

**SDK version strings:** `"current"` (public), `"system_current"` (system), `"test_current"` (test), `"core_current"` (core), `"module_current"` (module), or a numeric version like `"30"`.

### 4.3 SDK Stability Enforcement

Stable API surfaces (those with managed .txt files reviewed by API council):
- `sdkNone`, `sdkCore`, `sdkPublic`, `sdkSystem`, `sdkModule`, `sdkSystemServer`

Unstable surfaces:
- `sdkCorePlatform`, `sdkTest`, `sdkPrivate`

Modules on vendor/product partitions must specify `sdk_version` (cannot use platform APIs).

### 4.4 Product Variables for SDK

**File:** `build/soong/android/variable.go` (line 139)

Key product variables passed to Soong:
```go
Platform_version_name                     *string
Platform_sdk_version                      *int     // 30 for Android 11
Platform_sdk_codename                     *string  // "REL" for release
Platform_sdk_final                        *bool
Platform_version_active_codenames         []string
Platform_vndk_version                     *string
Platform_systemsdk_versions               []string
Platform_min_supported_target_sdk_version *string
```

---

## 5. Product, Device, and Board Configuration

### 5.1 Product Configuration Hierarchy

**Key files:**
- `build/make/core/product_config.mk` -- Product configuration loading logic
- `build/make/core/product.mk` -- Product variable definitions
- `build/make/target/product/` -- Product definition files

**Example product definition** (`build/make/target/product/aosp_arm64.mk`):
```makefile
# Inherit base configurations
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/mainline_system.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/handheld_system_ext.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/telephony_system_ext.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_product.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/emulator_vendor.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/board/generic_arm64/device.mk)

PRODUCT_NAME := aosp_arm64
PRODUCT_DEVICE := generic_arm64
PRODUCT_BRAND := Android
PRODUCT_MODEL := AOSP on ARM64
```

**Key product variables:**

| Variable | Purpose |
|----------|---------|
| `PRODUCT_NAME` | Build product name |
| `PRODUCT_DEVICE` | Target device (maps to BoardConfig) |
| `PRODUCT_BRAND` | Brand name for build.prop |
| `PRODUCT_MODEL` | Model name for build.prop |
| `PRODUCT_PACKAGES` | Modules to install |
| `PRODUCT_COPY_FILES` | Files to copy to output |
| `PRODUCT_ENFORCE_ARTIFACT_PATH_REQUIREMENTS` | Path validation |

### 5.2 Board Configuration

**File:** `build/make/core/board_config.mk`

Board configs define hardware-level properties. Read-only variables include:

**Architecture:**
- `TARGET_ARCH`, `TARGET_ARCH_VARIANT`, `TARGET_CPU_ABI`, `TARGET_CPU_VARIANT`
- `TARGET_2ND_ARCH`, `TARGET_2ND_CPU_ABI` (for 32-bit support on 64-bit)

**Hardware:**
- `TARGET_BOARD_PLATFORM`, `TARGET_BOOTLOADER_BOARD_NAME`
- `TARGET_NO_BOOTLOADER`, `TARGET_NO_KERNEL`, `TARGET_NO_RECOVERY`

**Partitions:**
- `BOARD_SYSTEMIMAGE_PARTITION_SIZE`, `BOARD_VENDORIMAGE_PARTITION_SIZE`
- `BOARD_SYSTEMIMAGE_FILE_SYSTEM_TYPE` (ext4, f2fs, etc.)
- `BOARD_FLASH_BLOCK_SIZE`

**Dynamic partitions:**
- `BOARD_SYSTEMIMAGE_PARTITION_RESERVED_SIZE`
- `BOARD_VENDORIMAGE_PARTITION_RESERVED_SIZE`

### 5.3 Base System Package List

**File:** `build/make/target/product/base_system.mk`

Defines the minimal system image contents via `PRODUCT_PACKAGES`. Includes core components like `app_process`, `bootanimation`, `surfaceflinger`, `audioserver`, APEX modules (`com.android.conscrypt`, `com.android.media`, `com.android.wifi`, etc.), and essential apps (`DownloadProvider`, `ContactsProvider`, etc.).

---

## 6. AIDL Compilation

### 6.1 Java AIDL Compilation

**File:** `build/soong/java/gen.go` (line 47)

AIDL files in Java modules are compiled via the `genAidl()` function:
1. AIDL sources are sharded into groups of 50
2. Each shard is processed by the `aidl` host tool
3. Generated .java files are packaged into srcjar files via `soong_zip`
4. Srcjars are merged into the module's source compilation

```go
func genAidl(ctx android.ModuleContext, aidlFiles android.Paths, aidlFlags string, deps android.Paths) android.Paths {
    shards := android.ShardPaths(aidlFiles, 50)
    for i, shard := range shards {
        // aidl -d <depfile> $FLAGS <input> <output.java>
        // soong_zip -o <srcjar> -C <dir> -D <dir>
    }
}
```

### 6.2 AIDL Configuration Properties

**File:** `build/soong/java/java.go` (line 282, inside `CompilerDeviceProperties`)

```
aidl.include_dirs           -- Top-level include directories
aidl.local_include_dirs     -- Directories relative to Android.bp
aidl.export_include_dirs    -- Directories exported to dependents
aidl.generate_traces        -- Generate systrace support
aidl.generate_get_transaction_name -- Generate GetTransaction name method
```

### 6.3 C++ AIDL Support

**File:** `build/soong/cc/library.go` (line 45)
```
aidl.export_aidl_headers -- Export headers generated from .aidl sources in cc_library
```

---

## 7. Resource Compilation (aapt2)

### 7.1 aapt2 Compilation Pipeline

**File:** `build/soong/java/aapt2.go`

Resource compilation uses aapt2 in a sharded pipeline (shard size = 100 files):

1. **Compile phase:** Individual resource files are compiled to `.flat` format
   - Values XML files: `values-[config]/<file>.xml` -> `values-[config]_<file>.arsc.flat`
   - Other resources: replaces last `/` with `_` and adds `.flat`

2. **Link phase:** Flat files are linked into a single APK resource table

```go
var aapt2CompileRule = pctx.AndroidStaticRule("aapt2Compile",
    blueprint.RuleParams{
        Command: `${config.Aapt2Cmd} compile -o $outDir $cFlags $in`,
    }, "outDir", "cFlags")
```

### 7.2 Resource Overlay System

**File:** `build/soong/java/android_resources.go`

- Overlay resolution uses `DEVICE_RESOURCE_OVERLAYS` and `PRODUCT_RESOURCE_OVERLAYS` product variables
- Runtime Resource Overlays (RRO) are controlled by `PRODUCT_ENFORCE_RRO_TARGETS`
- RRO packages are built as `runtime_resource_overlay` module type

### 7.3 Ignored Files

Resources automatically ignore: `.svn`, `.git`, `.ds_store`, `*.scc`, `.*`, `CVS`, `thumbs.db`, `picasa.ini`, `*~`

---

## 8. Dexing: d8 and r8

### 8.1 d8 (DEX Compiler)

**File:** `build/soong/java/dex.go`

d8 converts Java bytecode to DEX format:
```
d8 ${DexFlags} --output $outDir $d8Flags $in
soong_zip $zipFlags -o $outDir/classes.dex.jar -C $outDir -f "$outDir/classes*.dex"
merge_zips -D -stripFile "**/*.class" $out $outDir/classes.dex.jar $in
```

### 8.2 r8 (Optimizer + Shrinker)

r8 performs optimization, shrinking, and obfuscation (ProGuard replacement):
```
r8 ${DexFlags} -injars $in --output $outDir \
    --force-proguard-compatibility --no-data-resources \
    -printmapping $outDict $r8Flags
```

### 8.3 Optimization Properties

**File:** `build/soong/java/java.go` (line 313)

```
optimize.enabled     -- Enable optimization (default: true for apps, false for libraries)
optimize.shrink      -- Remove unused code (default: true for apps)
optimize.optimize    -- Optimize bytecode (default: false)
optimize.obfuscate   -- Obfuscate code (default: false)
optimize.proguard_flags       -- Inline ProGuard flags
optimize.proguard_flags_files -- ProGuard flag files
optimize.no_aapt_flags        -- Skip aapt-generated keep rules
```

---

## 9. Kotlin Support

**File:** `build/soong/java/kotlin.go`

Kotlin compilation is integrated directly into the Java build pipeline:

1. `.kt` and `.java` sources are compiled together by `kotlinc`
2. Kotlin stdlib can be packaged into the jar (`static_kotlin_stdlib`, defaults to true)
3. Kotlin compiler uses a build file generated by `GenKotlinBuildFileCmd`
4. Module properties: `kotlincflags` for Kotlin-specific compiler flags

Dependencies: `KotlincCmd`, `KotlinCompilerJar`, `KotlinStdlibJar`, `KotlinReflectJar`, `KotlinAnnotationJar`

---

## 10. Signing and Key Management

### 10.1 Default Signing Keys

**Directory:** `build/make/target/product/security/`

| Key | Purpose |
|-----|---------|
| `testkey` | Default signing key for development builds |
| `platform` | Platform component signing (system apps) |
| `shared` | Shared process signing |
| `media` | Media/download system signing |
| `networkstack` | Network stack module signing |
| `verity` | Verified boot signing |

Each key pair consists of a `.pk8` (private key) and `.x509.pem` (certificate).

### 10.2 Certificate Configuration in Modules

**File:** `build/soong/java/app.go` (line 257)

```
certificate         -- Certificate name, blank for default, or ":module" reference
lineage             -- Signing certificate lineage file (for key rotation)
additional_certificates -- Extra certificates for multi-signing
```

**Validation:** `build/make/core/app_certificate_validate.mk`

### 10.3 APEX Signing

APEX modules have their own key and certificate tags:
```go
keyTag         = dependencyTag{name: "key"}
certificateTag = dependencyTag{name: "certificate"}
```

---

## 11. Hidden API Management

**File:** `build/soong/java/hiddenapi.go`

The hidden API system classifies framework APIs into restriction categories:

1. **CSV Generation:** `Class2Greylist` tool generates flags CSV, metadata CSV, and index CSV from compiled classes against stub API flags
2. **Boot DEX Jars:** Tracked via `bootDexJarPath` for hidden API enforcement
3. **Singleton:** `build/soong/java/hiddenapi_singleton.go` manages global hidden API processing

---

## 12. APEX Modules

**File:** `build/soong/apex/apex.go`

APEX (Android Pony EXpress) is the modular system component format:

**Payload types:** shared libraries, executables, Java libraries, prebuilts, tests, Android apps

**APEX types:**
- `image` (`.apex`) -- Production format with dm-verity
- `zip` (`.zipapex`) -- Zip-based for testing
- `flattened` (`.flattened`) -- Flattened into system partition

**Key dependency tags:**
```go
sharedLibTag   -- Native shared libraries
executableTag  -- Native executables
javaLibTag     -- Java libraries
androidAppTag  -- Android applications
keyTag         -- APEX signing key
certificateTag -- APEX certificate
```

---

## 13. Building Individual Modules

### 13.1 Using m/mm/mmm

```bash
# Build everything
m

# Build a specific module by name
m <module-name>

# Build modules in current directory
mm

# Build modules in a specific directory
mmm frameworks/base/services/

# Build specific targets from a directory
mmm dir/:target1,target2
```

### 13.2 Using tapas

For building individual apps without a full product configuration:
```bash
tapas <App1> <App2> [arm|arm64|x86|x86_64] [eng|userdebug|user]
```

### 13.3 Soong UI Direct Commands

```bash
# Dump a build variable
build/soong/soong_ui.bash --dumpvar-mode TARGET_PRODUCT

# Dump multiple variables
build/soong/soong_ui.bash --dumpvars-mode --vars="TARGET_PRODUCT TARGET_BUILD_VARIANT"
```

---

## 14. Android.bp Format Reference

### 14.1 Basic Structure

Android.bp uses a JSON-like syntax:
```
module_type {
    name: "module_name",
    property: "value",
    list_property: ["item1", "item2"],
    nested_property: {
        sub_property: true,
    },
    arch: {
        arm: { srcs: ["arm_specific.c"] },
        arm64: { srcs: ["arm64_specific.c"] },
    },
    target: {
        android: { cflags: ["-DANDROID"] },
        host: { cflags: ["-DHOST"] },
    },
}
```

### 14.2 Common Patterns

**Defaults pattern:**
```
java_defaults {
    name: "my_defaults",
    sdk_version: "current",
    min_sdk_version: "28",
}

java_library {
    name: "my_lib",
    defaults: ["my_defaults"],
    srcs: ["**/*.java"],
}
```

**Filegroup reference:**
```
filegroup {
    name: "my-sources",
    srcs: ["java/**/*.java"],
    path: "java",
}

java_library {
    name: "my_lib",
    srcs: [":my-sources"],
}
```

**Genrule pattern:**
```
genrule {
    name: "my_generated",
    srcs: ["input.txt"],
    out: ["output.h"],
    tools: ["my_tool"],
    cmd: "$(location my_tool) $(in) > $(out)",
}
```

### 14.3 Subdirectory Inclusion

```
subdirs = ["subdir1", "subdir2"]
```

### 14.4 Visibility Control

```
java_library {
    name: "internal_lib",
    visibility: ["//visibility:private"],
    // or: visibility: ["//path/to/allowed:__subpackages__"],
}
```

---

## 15. Example Android.bp: Real-World Pattern

**From `frameworks/base/services/Android.bp`:**
```
java_defaults {
    name: "services_defaults",
    plugins: ["error_prone_android_framework"],
}

filegroup {
    name: "services-all-sources",
    srcs: [
        ":services.core-sources",
        ":services.accessibility-sources",
        // ... many more service source groups
    ],
    visibility: ["//visibility:private"],
}

java_library {
    name: "services",
    installable: true,
    dex_preopt: {
        app_image: true,
        profile: "art-profile",
    },
    srcs: [":services-main-sources"],
    static_libs: [/* service modules */],
}
```

---

## 16. Practical Guidance for Developers

### 16.1 Adding a New Java Library

```
// In your Android.bp:
java_library {
    name: "my-library",
    srcs: ["src/**/*.java"],
    sdk_version: "current",          // or "system_current" for system APIs
    min_sdk_version: "28",
    libs: ["framework"],             // compile-only dependency
    static_libs: ["my-utils"],       // bundled dependency
}
```

### 16.2 Adding a New System App

```
android_app {
    name: "MySystemApp",
    srcs: ["src/**/*.java"],
    platform_apis: true,             // use platform APIs
    certificate: "platform",         // sign with platform key
    privileged: true,                // install to priv-app
    resource_dirs: ["res"],
    manifest: "AndroidManifest.xml",
    static_libs: ["my-library"],
    jni_libs: ["libnative"],
}
```

### 16.3 Adding a Native Library

```
cc_library_shared {
    name: "libnative",
    srcs: ["native/*.cpp"],
    shared_libs: ["liblog", "libutils"],
    cflags: ["-Wall", "-Werror"],
    export_include_dirs: ["include"],
    vendor_available: true,
}
```

### 16.4 Adding AIDL Interface Sources

In a java_library or android_app:
```
java_library {
    name: "my-service-lib",
    srcs: [
        "src/**/*.java",
        "aidl/**/*.aidl",
    ],
    aidl: {
        local_include_dirs: ["aidl"],
        export_include_dirs: ["aidl"],
    },
    platform_apis: true,
}
```

### 16.5 Converting Android.mk to Android.bp

Use the `androidmk` tool:
```bash
androidmk Android.mk > Android.bp
```
Then manually verify and adjust the output. Not all Make patterns have direct Soong equivalents.

### 16.6 Key Environment Variables

| Variable | Purpose |
|----------|---------|
| `TARGET_PRODUCT` | Product being built |
| `TARGET_BUILD_VARIANT` | Build variant (user/userdebug/eng) |
| `OUT_DIR` | Output directory (default: `out`) |
| `ANDROID_PRODUCT_OUT` | Product-specific output directory |
| `ANDROID_HOST_OUT` | Host tools output directory |
| `SANITIZE_HOST` | Set to `address` for ASAN on host modules |

---

## 17. Architecture Observations and Recommendations

### 17.1 Strengths

1. **Soong's declarative model** eliminates many classes of Make-related build errors and enables better parallelism
2. **SDK version system** provides fine-grained API surface control with clear enforcement boundaries
3. **Mutator pipeline** in CC modules (sanitizers, VNDK, coverage, LTO) enables composable build transformations
4. **Hidden API tracking** is well-integrated into the build, ensuring API surface compliance
5. **APEX support** enables modular updates with proper signing and verification

### 17.2 Complexity Concerns

1. **Dual build system** -- The coexistence of Soong and Make creates cognitive overhead. Product config remains entirely in Make while module definitions are in Soong
2. **SDK version parsing** -- The `sdkSpec` system with 9 different `sdkKind` values and multiple string formats (`"current"`, `"system_current"`, `"30"`, `""`) creates complexity for module authors
3. **Mutator ordering** -- The CC module has 15+ mutators with specific ordering requirements; incorrect ordering can cause subtle build issues

### 17.3 Developer Recommendations

1. **Always prefer Android.bp** over Android.mk for new modules -- the Make system is in maintenance mode
2. **Set `sdk_version` explicitly** for all app and library modules to avoid accidentally depending on private APIs
3. **Use `java_defaults`/`cc_defaults`** to share common configuration and reduce duplication
4. **Use filegroups** to organize large source sets rather than complex glob patterns
5. **Test with `m checkbuild`** to validate build rules without running full builds
6. **Use `m <module>-check-aidl-api`** to verify AIDL interface compatibility
