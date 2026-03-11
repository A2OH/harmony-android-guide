# ART (Android Runtime) -- 代码审查 Report

## Source Paths
- **ART Runtime**: `~/aosp-android-11/art/`
- **Java Core Libraries**: `~/aosp-android-11/libcore/`
- **Dalvik Tools**: `~/aosp-android-11/dalvik/`

---

## 1. ART Runtime Architecture Overview

### 1.1 Top-Level Organization

The ART runtime (`art/`) is structured into several major subsystems:

| Directory | Purpose |
|-----------|---------|
| `art/runtime/` | Core runtime (class loading, GC, threading, JNI, interpreter) |
| `art/compiler/` | Optimizing compiler backend (AOT and JIT) |
| `art/dex2oat/` | Ahead-of-time compilation tool |
| `art/libdexfile/` | DEX file parsing and representation library |
| `art/libprofile/` | Profile data formats for PGO |
| `art/profman/` | Profile management tool |
| `art/dexdump/` | DEX file inspection tool |
| `art/dexlayout/` | DEX file layout optimization |
| `art/oatdump/` | OAT/VDEX file inspection tool |
| `art/openjdkjvm/` | OpenJDK JVM interface layer |
| `art/openjdkjvmti/` | JVMTI agent interface |
| `art/libnativebridge/` | Native bridge for running non-native-ISA code |
| `art/libnativeloader/` | Native library loading infrastructure |
| `art/sigchainlib/` | Signal chaining library |

### 1.2 Key Source Files

| File | Lines | Role |
|------|-------|------|
| `art/runtime/runtime.cc` | ~3,068 | Runtime initialization, singleton lifecycle |
| `art/runtime/class_linker.cc` | ~9,883 | Class loading, linking, verification |
| `art/runtime/gc/heap.cc` | ~4,321 | Heap management and GC orchestration |
| `art/runtime/gc/collector/concurrent_copying.cc` | ~3,870 | Default (CC) garbage collector |
| `art/runtime/jni/jni_internal.cc` | ~3,364 | JNI function implementations |
| `art/dex2oat/dex2oat.cc` | ~3,313 | AOT compilation driver |

### 1.3 Runtime Initialization (`Runtime::Init`)

**File**: `art/runtime/runtime.h`, `art/runtime/runtime.cc` (line 1186)

The `Runtime` class is a process-wide singleton. `Runtime::Init()` performs initialization in this order:

1. **Environment snapshot** -- protects subprocesses from LD_LIBRARY_PATH modifications.
2. **MemMap initialization** -- sets up memory-mapped region management.
3. **OatFileManager creation** -- manages OAT file discovery and loading.
4. **Monitor and Thread infrastructure** -- `MonitorList`, `MonitorPool`, `ThreadList`.
5. **InternTable** -- string interning for deduplication.
6. **Heap construction** -- creates GC heap with all configured spaces (see Section 3).
7. **ClassLinker initialization** -- boots the class loading infrastructure.
8. **JIT creation** -- initializes JIT compiler if enabled.
9. **Signal handlers** -- installs fault handlers for NullPointerException, StackOverflow.

Key configuration knobs set during init:
- `is_zygote_` / `is_primary_zygote_` -- affects GC strategy and fork behavior.
- `is_concurrent_gc_enabled_` -- defaults to `true`.
- `is_explicit_gc_disabled_` -- can disable `System.gc()`.
- `hidden_api_policy_` -- controls enforcement of hidden API restrictions.
- `target_sdk_version_` -- affects behavioral compatibility.
- `is_low_memory_mode_` -- triggers conservative GC tuning.

**App Developer Impact**: Runtime options (e.g., heap sizes, GC mode) are set by the system for apps. Developers influence them indirectly through manifest attributes like `android:largeHeap="true"` and the `targetSdkVersion` which governs hidden API enforcement.

---

## 2. Code Execution: Compiler, Interpreter, and JIT

### 2.1 Execution Modes

ART supports three execution modes that coexist:

| Mode | 描述 | When Used |
|------|-------------|-----------|
| **Interpreter** | Bytecode-by-bytecode execution | Debugging, uncompiled methods, first-run |
| **AOT (dex2oat)** | Ahead-of-time compilation to native code | Install time or background dexopt |
| **JIT** | Just-in-time compilation of hot methods | Runtime, based on invocation counts |

### 2.2 Interpreter

**Directory**: `art/runtime/interpreter/`

The interpreter has multiple implementation strategies:

- **Switch interpreter** (`interpreter_switch_impl*.cc`) -- Standard C++ switch-based dispatch. The implementation is split across 4 files (`interpreter_switch_impl0.cc` through `interpreter_switch_impl3.cc`) to reduce compile times.
- **Mterp** (`interpreter/mterp/`) -- Assembly-optimized interpreter with per-architecture implementations (`arm/`, `arm64/`, `x86/`, `x86_64/`). Generated via `gen_mterp.py`.
- **Nterp** (`interpreter/mterp/nterp.cc`) -- Next-generation interpreter, a more efficient replacement for mterp that uses the same infrastructure but with improved dispatch.

The interpreter function `InterpreterJni()` (in `interpreter.cc`, line 48) handles direct JNI calls from interpreted code by casting to typed function pointers based on method shorty descriptors, avoiding the full JNI compiler overhead for simple cases.

**App Developer Impact**: Interpreted execution is 10-100x slower than compiled code. Apps experience this during:
- First launch before JIT warms up
- Debugging sessions (forced interpretation)
- Methods that never reach JIT compilation thresholds

### 2.3 AOT Compilation (dex2oat)

**File**: `art/dex2oat/dex2oat.cc`

The `dex2oat` tool compiles DEX bytecode into native code stored in OAT files. It runs during:
- System image creation (boot image compilation)
- App installation (with appropriate compiler filter)
- Background optimization by the system (`bg-dexopt-job`)

#### Compiler Filters

**File**: `art/runtime/compiler_filter.cc`

Compiler filters control how much compilation happens:

| Filter | AOT | JNI | Quicken | 描述 |
|--------|-----|-----|---------|-------------|
| `kAssumeVerified` | No | No | No | Skip verification, no compilation |
| `kExtract` | No | No | No | Extract DEX only |
| `kVerify` | No | No | No | Verify bytecode only |
| `kQuicken` | No | Yes | Yes | Quicken bytecodes + compile JNI stubs |
| `kSpaceProfile` | Yes | Yes | Yes | Profile-guided, optimize for size |
| `kSpace` | Yes | Yes | Yes | Optimize for size |
| `kSpeedProfile` | Yes | Yes | Yes | Profile-guided, optimize for speed |
| `kSpeed` | Yes | Yes | Yes | Compile everything, optimize for speed |
| `kEverythingProfile` | Yes | Yes | Yes | Profile-guided, compile everything |
| `kEverything` | Yes | Yes | Yes | Compile absolutely everything |

**App Developer Impact**: The default for app install on Android 11 is typically `speed-profile`, meaning only methods seen in profiles are AOT compiled. Cold methods run in the interpreter or are JIT-compiled on demand. Developers can influence this through `dexopt` flags in certain build configurations but have no direct API control.

### 2.4 Optimizing Compiler

**Directory**: `art/compiler/optimizing/`

The optimizing compiler is a full SSA-based compiler with the following passes:

- **Builder** (`builder.cc`, `block_builder.cc`) -- Converts DEX bytecode to HGraph (SSA IR).
- **Optimization passes** -- Including:
  - `bounds_check_elimination.cc` -- Eliminates redundant array bounds checks
  - `code_sinking.cc` -- Moves code closer to its use
  - `cha_guard_optimization.cc` -- Class Hierarchy Analysis optimizations
  - Dead code elimination, constant folding, inlining, etc.
- **Code generators** -- Per-architecture backends:
  - `code_generator_arm_vixl.cc` -- ARM 32-bit (via VIXL assembler)
  - `code_generator_arm64.cc` -- ARM 64-bit
  - `code_generator_x86.cc` -- x86 32-bit
  - `code_generator_x86_64.cc` -- x86 64-bit
  - Vector extensions for each architecture (`code_generator_vector_*.cc`)

### 2.5 JIT Compiler

**Files**: `art/runtime/jit/jit.h`, `art/runtime/jit/jit.cc`, `art/runtime/jit/jit_code_cache.h`

The JIT compiler operates concurrently with app execution:

#### Compilation Thresholds

```
Default compile threshold:   20 * 512 = 10,240 invocations
Default warmup threshold:    10,240 / 2 = 5,120
OSR threshold:               configurable (for on-stack replacement)
Sample batch size:           512 (must be power of 2)
```

#### JIT Options (`JitOptions` class)

| Option | 描述 |
|--------|-------------|
| `compile_threshold_` | Invocations before JIT compilation triggers |
| `warmup_threshold_` | Invocations before profiling begins |
| `osr_threshold_` | Threshold for On-Stack Replacement |
| `code_cache_initial_capacity_` | Starting size of JIT code cache |
| `code_cache_max_capacity_` | Maximum JIT code cache size |
| `priority_thread_weight_` | Extra weight for UI thread invocations |
| `invoke_transition_weight_` | Weight for call transitions |
| `use_tiered_jit_compilation_` | Enable tiered compilation (baseline then optimized) |
| `use_baseline_compiler_` | Use simpler baseline compiler |

#### JIT Execution Flow

1. Method executes in interpreter; invocation counter increments in batches of 512.
2. At warmup threshold, profiling data begins collecting (inline caches, branch profiles).
3. At compile threshold, the JIT thread picks up the method for compilation.
4. `JitCompilerInterface::CompileMethod()` runs the optimizing compiler.
5. Compiled code is placed in the `JitCodeCache`.
6. The method's entry point is atomically updated to point to native code.
7. **OSR (On-Stack Replacement)**: Long-running loops can transition from interpreted to compiled mid-execution.

#### JIT Thread Priority

JIT compilation threads run at priority 9 (lowest foreground priority) to avoid competing with the UI thread.

**App Developer Impact**: JIT compilation means apps get progressively faster as hot code is compiled. However:
- First launch is always slower (cold start).
- JIT code cache is memory-bounded; under memory pressure, compiled code may be evicted.
- The `priority_thread_weight_` gives the UI thread 1000x more weight in compilation decisions, ensuring the methods it calls are compiled first.

---

## 3. Garbage Collection

### 3.1 GC Architecture

**Files**: `art/runtime/gc/heap.h`, `art/runtime/gc/heap.cc`

The GC subsystem is organized into:

| 组件 | Directory/File | Purpose |
|-----------|----------------|---------|
| Heap | `gc/heap.cc` | Central orchestrator for all GC operations |
| Collectors | `gc/collector/` | GC algorithm implementations |
| Spaces | `gc/space/` | Memory region management |
| Accounting | `gc/accounting/` | Card tables, bitmaps, remembered sets |

### 3.2 Collector Types

**File**: `art/runtime/gc/collector_type.h`

| Collector | Constant | 描述 |
|-----------|----------|-------------|
| **CMS** | `kCollectorTypeCMS` | Concurrent Mark-Sweep (legacy default) |
| **CC** | `kCollectorTypeCC` | Concurrent Copying (default with read barriers) |
| **CC Background** | `kCollectorTypeCCBackground` | Background compaction for CC |
| **MS** | `kCollectorTypeMS` | Non-concurrent Mark-Sweep |
| **SS** | `kCollectorTypeSS` | Semi-space (compacting) |
| **Homogeneous Compact** | `kCollectorTypeHomogeneousSpaceCompact` | Background compaction for CMS |

The default collector in Android 11 is **Concurrent Copying (CC)** when read barriers are enabled (which is the standard configuration):

```cpp
// From runtime.cc line 1394-1397:
kUseReadBarrier ? gc::kCollectorTypeCC : xgc_option.collector_type_,
kUseReadBarrier ? BackgroundGcOption(gc::kCollectorTypeCCBackground)
                : runtime_options.GetOrDefault(Opt::BackgroundGc),
```

### 3.3 Concurrent Copying Collector

**File**: `art/runtime/gc/collector/concurrent_copying.h`, `concurrent_copying.cc`

The CC collector is ART's primary GC in Android 11. Key design features:

- **Generational collection** -- Supports young-gen-only collections when `use_generational_cc` is true. This is enabled by default when Baker read barriers are available.
- **Read barriers** -- Uses Baker-style read barriers for concurrent object copying. Each object reference read includes a barrier check.
- **Region-based** -- Operates on `RegionSpace` which divides the heap into fixed-size regions.
- **Low pause times** -- Most work happens concurrently. The pause is limited to root scanning and reference processing.

Configuration constants:
```cpp
kDefaultGcMarkStackSize = 2 * MB
kGrayDirtyImmuneObjects = true  // Gray dirty objects in pause to prevent dirty pages
```

### 3.4 Other Collectors

- **Mark-Sweep** (`mark_sweep.cc`) -- Traditional mark-sweep. Variants include:
  - `partial_mark_sweep.cc` -- Only collects application heap, not zygote space
  - `sticky_mark_sweep.cc` -- Only collects objects allocated since last GC
- **Semi-Space** (`semi_space.cc`) -- Copying collector that compacts by copying live objects between two spaces.

### 3.5 Heap Spaces

**Directory**: `art/runtime/gc/space/`

| Space | File | Purpose |
|-------|------|---------|
| **RegionSpace** | `region_space.cc` | Main allocation space for CC collector |
| **RosAllocSpace** | `rosalloc_space.cc` | Runs-of-Slots allocator for CMS mode |
| **DlMallocSpace** | `dlmalloc_space.cc` | dlmalloc-based allocation space |
| **BumpPointerSpace** | `bump_pointer_space.cc` | Fast bump-pointer allocation (semi-space) |
| **LargeObjectSpace** | `large_object_space.cc` | For objects exceeding threshold |
| **ImageSpace** | `image_space.cc` | Memory-mapped boot/app image |
| **ZygoteSpace** | `zygote_space.cc` | Frozen zygote heap (shared across apps) |

### 3.6 Heap Configuration Constants

**File**: `art/runtime/gc/heap.h` (line 130+)

```
Default initial size:       2 MB
Default maximum size:       256 MB
Default TLAB size:          32 KB
Partial TLAB size:          16 KB
Non-moving space capacity:  64 MB
Default min free:           512 KB
Default max free:           2 MB
Target utilization:         0.75 (75%)
Heap growth multiplier:     2.0 (4.0 with read barriers due to +1.0 extra)
Large object threshold:     3 * page_size (12 KB)
Long pause log threshold:   5 ms
Long GC log threshold:      100 ms
Heap trim wait:             5 seconds
Collector transition wait:  5 seconds
```

### 3.7 GC Causes

**File**: `art/runtime/gc/gc_cause.h`

GC can be triggered by:
- `kGcCauseForAlloc` -- Allocation failure (blocking)
- `kGcCauseBackground` -- Proactive background collection
- `kGcCauseExplicit` -- `System.gc()` call
- `kGcCauseForNativeAlloc` -- Native allocation watermark exceeded
- `kGcCauseCollectorTransition` -- Foreground/background transition

### 3.8 Accounting Infrastructure

**Directory**: `art/runtime/gc/accounting/`

- **Card Table** (`card_table.cc`) -- Tracks dirty pages for generational collection. Each card covers 128 bytes of heap.
- **Space Bitmap** (`space_bitmap.cc`) -- Marks live objects within continuous spaces.
- **Mod Union Table** (`mod_union_table.cc`) -- Tracks cross-space references for partial collections.
- **Read Barrier Table** (`read_barrier_table.h`) -- Supports CC collector's read barriers.
- **Remembered Set** (`remembered_set.cc`) -- Tracks references from non-moving to moving spaces.

**App Developer Impact**:
- `System.gc()` is honored by default but can be disabled (`is_explicit_gc_disabled_`). Apps should not rely on it for correctness.
- Large allocations (>12KB primitive arrays) go to the large object space, which uses a different allocation strategy.
- The CC collector provides sub-5ms pause times, making GC largely transparent to apps.
- The 256MB default heap is adjustable via `android:largeHeap="true"` (typically increases to 512MB, device-dependent).
- Thread-Local Allocation Buffers (TLABs) of 32KB enable lock-free small object allocation.
- Low memory mode (triggered by system property) uses a 1.0x heap growth multiplier instead of the default 2.0-4.0x, causing more frequent GC.

---

## 4. DEX File Format and Loading

### 4.1 DEX File Structure

**File**: `art/libdexfile/dex/dex_file.h`

The DEX file header structure:

| Field | 描述 |
|-------|-------------|
| `magic_[8]` | DEX magic number ("dex\n035\0" or "dex\n039\0") |
| `checksum_` | Adler32 checksum of file (after magic and checksum fields) |
| `signature_[20]` | SHA-1 hash of file contents |
| `file_size_` | Total file size in bytes |
| `string_ids_size/off` | String constant pool |
| `type_ids_size/off` | Type descriptors |
| `proto_ids_size/off` | Method prototypes |
| `field_ids_size/off` | Field declarations |
| `method_ids_size/off` | Method declarations |
| `class_defs_size/off` | Class definitions |
| `data_size/off` | Data section |

Map item types enumerate all sections: `kDexTypeCodeItem` (bytecode), `kDexTypeStringDataItem`, `kDexTypeClassDataItem`, `kDexTypeAnnotationItem`, `kDexTypeHiddenapiClassData`, etc.

### 4.2 Compact DEX

**Files**: `art/libdexfile/dex/compact_dex_file.h`, `compact_dex_file.cc`

Compact DEX is an ART-internal optimized representation that reduces DEX size through:
- Shared data sections across multiple DEX files in a VDEX
- Compact offset tables (`compact_offset_table.cc`)
- More efficient encoding of debug info

### 4.3 OAT Files

**File**: `art/runtime/oat_file.h`

OAT files contain AOT-compiled native code alongside their source DEX files. Three class states tracked per OAT class:

```cpp
kOatClassAllCompiled = 0   // All methods compiled, OatMethodOffsets for each
kOatClassSomeCompiled = 1  // Bitmap indicates which methods are compiled
kOatClassNoneCompiled = 2  // All methods interpreted
```

### 4.4 VDEX Files

**File**: `art/runtime/vdex_file.h`

VDEX files contain the DEX files extracted from APKs plus verification metadata. They avoid re-extraction and re-verification on subsequent loads.

**App Developer Impact**:
- Apps ship DEX bytecode in APKs. ART transparently handles compilation to OAT/VDEX.
- Multidex is handled transparently; each DEX file in an APK gets its own entry in the OAT file.
- The 65,536 method limit per DEX file is a format constraint. The `type_ids` limit is also 65,535.
- DEX format version 37+ enforces class definition ordering rules.

---

## 5. Class Loading Mechanism

### 5.1 ClassLinker

**File**: `art/runtime/class_linker.cc` (~9,883 lines)

The `ClassLinker` is the central class loading engine. Its primary method `FindClass()` (line 2963) implements the following lookup algorithm:

1. **Primitive type check** -- Single-character descriptors are handled directly.
2. **Loaded class table lookup** -- Check if the class is already loaded via `LookupClass()`.
3. **Boot classpath search** -- For null class loader, search `boot_class_path_` entries.
4. **Array class creation** -- For descriptors starting with `[`, create array classes via `CreateArrayClass()`.
5. **Base DEX class loader search** -- For known class loader types (PathClassLoader, DexClassLoader), search directly via `FindClassInBaseDexClassLoader()`.
6. **Java-side fallback** -- For unknown class loader types, delegate to `ClassLoader.loadClass()` in Java.

The `DefineClass()` method (line 3194) handles the actual class creation from DEX data:
1. Allocate class object
2. Load fields and methods from DEX
3. Run runtime callbacks (`ClassPreDefine`)
4. Verify the class
5. Resolve superclass and interfaces
6. Link the class (compute vtable, IMT, field offsets)

### 5.2 Class Loader Hierarchy

**Directory**: `libcore/dalvik/src/main/java/dalvik/system/`

#### BaseDexClassLoader

**File**: `libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java`

Base class for all DEX-based class loaders. Key features:
- `pathList` (`DexPathList`) -- Contains the list of DEX files to search.
- `sharedLibraryLoaders` -- Array of class loaders for Android's `<uses-library>` feature. Searched before `pathList`.
- `findClass()` implementation: shared libraries -> pathList search -> ClassNotFoundException.
- Supports `ByteBuffer[]` constructor for in-memory DEX loading.
- `computeClassLoaderContextsNative()` -- Native method that computes the class loader context string for each classpath entry (used by dex2oat).

#### PathClassLoader

**File**: `libcore/dalvik/src/main/java/dalvik/system/PathClassLoader.java`

Used by the system for loading installed applications. Delegates to `BaseDexClassLoader` with `optimizedDirectory = null` (deprecated since API 26).

#### DexClassLoader

**File**: `libcore/dalvik/src/main/java/dalvik/system/DexClassLoader.java`

Used for loading code from JAR/APK files not installed as part of an application. Since API 26, the `optimizedDirectory` parameter is ignored (passed as `null`).

#### InMemoryDexClassLoader

**File**: `libcore/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java`

Loads classes from `ByteBuffer` objects containing DEX data. No file system access required. Used for dynamically generated or network-loaded code.

#### DelegateLastClassLoader

**File**: `libcore/dalvik/src/main/java/dalvik/system/DelegateLastClassLoader.java`

Implements **delegate-last** lookup order:
1. Boot classpath
2. Own DEX files
3. Parent class loader

This is the reverse of the standard Java parent-first delegation. Useful for plugin architectures where loaded code should override parent classes.

### 5.3 Class Loader Context

**File**: `art/runtime/class_loader_context.cc`

The class loader context encodes the entire class loader hierarchy as a string for dex2oat:
- `PCL` = PathClassLoader
- `DLC` = DelegateLastClassLoader
- `IMC` = InMemoryDexClassLoader

Format: `PCL[foo.jar:bar.jar]{shared_lib.jar#shared_lib2.jar};PCL[parent.jar]`

This context is critical for:
- Ensuring OAT files are valid for the current class loader configuration
- Detecting when re-compilation is needed due to classpath changes

### 5.4 Native Class Loader Support

**File**: `art/runtime/native/dalvik_system_DexFile.cc`, `dalvik_system_BaseDexClassLoader.cc`

The native implementations bridge Java class loader APIs to the ART runtime:
- `DexFile_openDexFileNative` -- Opens and verifies DEX files
- `DexFile_defineClassNative` -- Calls `ClassLinker::DefineClass()`
- `BaseDexClassLoader_computeClassLoaderContextsNative` -- Computes context strings

**App Developer Impact**:
- `PathClassLoader` is used automatically for app loading. Using `DexClassLoader` or `InMemoryDexClassLoader` for dynamic code loading can bypass Play Protect scanning.
- The class loader context must be stable between installation and execution; classpath changes invalidate compiled OAT files.
- `DelegateLastClassLoader` enables class shadowing but can cause subtle bugs if the boot classpath changes across OS updates.
- Class loading involves verification, which can be significant on first run (mitigated by VDEX caching).

---

## 6. JNI Bridge

### 6.1 Architecture

**Directory**: `art/runtime/jni/`

| File | Purpose |
|------|---------|
| `jni_internal.cc` | Full JNI function table implementation (~3,364 lines) |
| `jni_env_ext.cc` | Per-thread JNI environment extensions |
| `java_vm_ext.cc` | JavaVM interface extensions |
| `jni_id_manager.cc` | Manages JNI method/field ID mapping |
| `check_jni.cc` | Debug mode JNI validation |

### 6.2 JNI ID Management

**File**: `art/runtime/jni/jni_id_manager.cc`

Android 11 introduces configurable JNI ID management (`jni_ids_indirection_`):
- **Direct mode** -- JNI method/field IDs are direct pointers to `ArtMethod`/`ArtField`. Fast but prevents structural class redefinition.
- **Opaque/indirect mode** -- IDs are indices into a table. Enables class redefinition but adds indirection overhead.

### 6.3 Hidden API Enforcement at JNI Layer

**File**: `art/runtime/hidden_api.h`

JNI access to non-SDK APIs is checked via `AccessMethod::kJNI`. The enforcement policy has three levels:
- `kDisabled` -- No restrictions
- `kJustWarn` -- Log warnings but allow access
- `kEnabled` -- Block access to dark-greylist and blacklisted APIs

**App Developer Impact**:
- JNI is the primary way to call native (C/C++) code from Java. Each JNI call involves thread state transitions (`ScopedThreadStateChange`) which add overhead.
- JNI local references are tracked per-thread and must be managed carefully to avoid leaks. The `indirect_reference_table` tracks these.
- `CheckJNI` mode (enabled in debug builds) catches common JNI errors like using stale references, passing wrong types, or missing exception checks.
- Hidden API enforcement applies at the JNI layer -- `GetMethodID`/`GetFieldID` calls to restricted APIs will fail with `NoSuchMethodError`/`NoSuchFieldError`.
- JNI critical sections (`GetPrimitiveArrayCritical`) disable moving GC, which can cause GC pauses if held too long.

---

## 7. Profile-Guided Optimization (PGO)

### 7.1 Profile Collection

**File**: `art/runtime/jit/profile_saver.h`

The `ProfileSaver` runs as a background thread collecting profile data:
- **What is profiled**: Hot methods, inline cache data, class usage patterns.
- **When saved**: Periodically, on JIT activity notifications, and at startup completion.
- **Where saved**: Per-app profile files (typically in `/data/misc/profiles/`).

Key operations:
- `Start()` -- Begins profiling, tracking `(output_filename, code_paths)`.
- `NotifyJitActivity()` -- Triggers profile processing on JIT events.
- `NotifyStartupCompleted()` -- Signals transition from startup to steady-state profiling.
- `ForceProcessProfiles()` -- Manual trigger (SIGUSR1).

### 7.2 Profile Management Tool

**Directory**: `art/profman/`

The `profman` tool manages profile data:
- `profile_assistant.cc` -- Merges profiles from multiple sources.
- `boot_image_profile.cc` -- Generates boot image profiles.

### 7.3 Profile-Guided Compilation Flow

1. App runs with JIT; `ProfileSaver` records hot methods and classes.
2. Profile is saved to disk as a `.prof` file.
3. On idle maintenance, `bg-dexopt-job` runs `dex2oat` with `--compiler-filter=speed-profile`.
4. `dex2oat` reads the profile and compiles only profiled methods.
5. On next app launch, compiled code is loaded from the OAT file.

**App Developer Impact**:
- PGO means apps automatically get faster after repeated use without developer intervention.
- Baseline profiles (introduced conceptually here, formalized later) let developers ship profiles with their APKs for day-one compilation.
- The `speed-profile` filter typically compiles 10-30% of methods, balancing code size vs. performance.
- Startup methods are prioritized in profiling through `NotifyStartupCompleted()`.

---

## 8. App Startup Optimization

### 8.1 Boot Image

**File**: `art/runtime/gc/space/image_space.cc`

The boot image contains pre-compiled and pre-initialized core framework classes. It is memory-mapped into every app process via the Zygote:
- Pre-linked classes (no runtime linking needed)
- Pre-initialized static fields
- Pre-allocated commonly used objects (e.g., boxed integers, empty arrays)

### 8.2 App Images

Similar to boot images but per-app. Contain classes from the app's DEX files that were resolved at compile time. Loaded as `ImageSpace` in the heap.

### 8.3 Zygote Optimization

- **ZygoteSpace** (`gc/space/zygote_space.cc`) -- Heap created by Zygote is frozen and shared copy-on-write across all forked app processes.
- **Zygote Hooks** (`native/dalvik_system_ZygoteHooks.cc`) -- Native methods called during Zygote fork:
  - Pre-fork: GC, dump heap regions
  - Post-fork: Re-initialize thread pools, adjust GC for app process

### 8.4 Startup-Aware JIT

The JIT has startup awareness:
- Higher weight for the UI thread via `priority_thread_weight_` (1000x default).
- `NotifyStartupCompleted()` transitions from aggressive compilation to steady-state.
- On-stack replacement allows long-running startup loops to benefit from JIT mid-execution.

### 8.5 Image Space Loading Order

**File**: `art/runtime/gc/space/image_space_loading_order.h`

Controls whether system or data (updated) images are loaded first, affecting which pre-compiled code is used.

**App Developer Impact**:
- Cold start time is dominated by class loading and verification. Using fewer classes at startup helps.
- The Zygote fork model means framework classes are already loaded -- app startup cost is primarily app-specific class loading.
- Profile data from previous runs improves subsequent startup times through PGO.
- Large static initializers (`<clinit>`) execute synchronously and block class loading.

---

## 9. Runtime Configuration and Tuning

### 9.1 Heap Tuning Parameters

From `Runtime::Init()` (line 1381):

| Parameter | Option Key | Default | Effect |
|-----------|-----------|---------|--------|
| Initial heap size | `MemoryInitialSize` | 2 MB | Starting heap size |
| Growth limit | `HeapGrowthLimit` | Device-dependent | Soft limit for non-largeHeap apps |
| Maximum size | `MemoryMaximumSize` | 256 MB | Hard limit (or `largeHeap` limit) |
| Min free | `HeapMinFree` | 512 KB | Minimum free bytes after GC |
| Max free | `HeapMaxFree` | 2 MB | Maximum free bytes before GC |
| Target utilization | `HeapTargetUtilization` | 0.75 | Target ratio of live data to heap size |
| Foreground growth multiplier | `ForegroundHeapGrowthMultiplier` | 2.0 (+1.0 for CC) | How aggressively heap grows |
| TLAB usage | `UseTLAB` | true | Thread-local allocation buffers |
| Parallel GC threads | `ParallelGCThreads` | Device-dependent | GC worker threads |
| Concurrent GC threads | `ConcGCThreads` | Device-dependent | Concurrent GC threads |

### 9.2 GC Tuning

The `XGcOption` structure controls:
- `collector_type_` -- Foreground collector (overridden to CC when read barriers enabled)
- `generational_cc` -- Enable generational CC (default true with Baker read barriers)
- `verify_pre_gc_heap_` / `verify_post_gc_heap_` -- Debug verification (costly)
- `gcstress_` -- Continuous GC for testing
- `measure_` -- Measure GC performance metrics

### 9.3 Low Memory Mode

When `is_low_memory_mode_` is set:
- Foreground heap growth multiplier defaults to 1.0 (instead of 2.0-4.0).
- Class loading uses tighter hash table load factors (0.5-0.8 vs 0.4-0.7).
- More aggressive GC behavior.

### 9.4 Compiler Tuning

```cpp
// From runtime.cc constructor:
is_concurrent_gc_enabled_ = true    // Concurrent GC enabled by default
is_explicit_gc_disabled_ = false    // System.gc() honored by default
image_dex2oat_enabled_ = true       // Allow dex2oat for images
```

---

## 10. Java Core Libraries (libcore)

### 10.1 Organization

**Root**: `~/aosp-android-11/libcore/`

| Directory | 描述 |
|-----------|-------------|
| `ojluni/` | OpenJDK-derived classes (java.lang, java.util, java.io, java.net, etc.) |
| `dalvik/` | Dalvik/Android-specific classes (dalvik.system, dalvik.annotation) |
| `luni/` | Android-specific internal libraries (libcore.io, libcore.net, etc.) |
| `libart/` | ART-specific JNI implementations |
| `json/` | org.json implementation |
| `xml/` | XML parsing (org.xml.sax, javax.xml) |
| `dom/` | DOM XML implementation |
| `apex/` | APEX module integration |

### 10.2 Key Packages

#### java.lang (106 files)
Source: `libcore/ojluni/src/main/java/java/lang/`

Core runtime types including `Object`, `Class`, `String`, `Thread`, `ClassLoader`, `System`, `Runtime`, all wrapper types (`Integer`, `Boolean`, etc.), and exception hierarchy.

Notable Android-specific modifications:
- `ClassLoader.java` -- Modified to use DEX-based class loaders.
- `Thread.java` -- Integrated with ART's thread management.
- `System.java` -- Android-specific property and security implementations.

#### java.util (128 files)
Source: `libcore/ojluni/src/main/java/java/util/`

Collections framework, concurrent utilities, date/time, and utility classes. Largely unchanged from OpenJDK.

#### java.io (88 files)
Source: `libcore/ojluni/src/main/java/java/io/`

I/O streams, readers/writers, serialization. Android adds interception via `BlockGuard` for detecting I/O on the main thread.

### 10.3 dalvik.system Package

**Directory**: `libcore/dalvik/src/main/java/dalvik/system/`

| Class | Purpose |
|-------|---------|
| `BaseDexClassLoader` | Base for all DEX class loaders |
| `PathClassLoader` | System/app class loader |
| `DexClassLoader` | Dynamic code loading |
| `InMemoryDexClassLoader` | In-memory DEX loading |
| `DelegateLastClassLoader` | Delegate-last loading policy |
| `DexFile` | Low-level DEX file access |
| `DexPathList` | Ordered list of DEX/native paths |
| `VMRuntime` | Runtime configuration access |
| `VMDebug` | Debug and profiling utilities |
| `BlockGuard` | Policy-based I/O blocking detection |
| `CloseGuard` | Resource leak detection |
| `ZygoteHooks` | Zygote lifecycle callbacks |

### 10.4 libcore Internal Libraries

**Directory**: `libcore/luni/src/main/java/libcore/`

| Package | Purpose |
|---------|---------|
| `libcore.io` | Low-level I/O (Linux syscall wrappers, `Os` class) |
| `libcore.net` | Network internals (MIME types, URI parsing) |
| `libcore.reflect` | Reflection utilities |
| `libcore.util` | Internal utilities (NativeAllocationRegistry, etc.) |
| `libcore.timezone` | Timezone data management |
| `libcore.content` | Content type utilities |
| `libcore.icu` | ICU integration for internationalization |

**App Developer Impact**:
- `BlockGuard` enforces `StrictMode` disk/network policies. Apps see `NetworkOnMainThreadException` because `BlockGuard` intercepts socket operations.
- `CloseGuard` logs warnings when resources (streams, cursors, connections) are garbage collected without being closed.
- `NativeAllocationRegistry` (in `libcore.util`) enables correct accounting of native memory associated with Java objects, triggering GC appropriately.
- The `dalvik.system.VMRuntime` class exposes heap statistics and controls like `setTargetHeapUtilization()`, `getTargetSdkVersion()`, and `newNonMovableArray()`.

---

## 11. Dalvik Tools

### 11.1 Organization

**Root**: `~/aosp-android-11/dalvik/`

| Directory | Purpose |
|-----------|---------|
| `dx/` | Legacy DEX compiler (Java bytecode to DEX bytecode) |
| `dexgen/` | DEX file generation utilities |
| `tools/` | Miscellaneous tools |
| `opcode-gen/` | Dalvik/ART opcode definition generation |

Note: `dx` is the legacy tool; D8/R8 (in external build tools) is the modern replacement for DEX compilation.

---

## 12. Hidden API Enforcement

### 12.1 Architecture

**File**: `art/runtime/hidden_api.h`

Android 11 enforces restrictions on non-SDK ("hidden") APIs to maintain platform stability:

```cpp
enum class EnforcementPolicy {
    kDisabled = 0,    // No checks
    kJustWarn = 1,    // Log but allow
    kEnabled  = 2,    // Block dark grey & blacklist
};

enum class AccessMethod {
    kNone       = 0,  // Internal test
    kReflection = 1,  // Java reflection
    kJNI        = 2,  // JNI access
    kLinking    = 3,  // Class linking
};
```

### 12.2 Domain System

**File**: `art/runtime/base/hiddenapi_domain.h`

Classes are assigned to domains:
- **Platform domain** -- System framework code (unrestricted access)
- **Core platform domain** -- Core libraries (restricted even for platform code)
- **Application domain** -- App code (subject to hidden API restrictions)

**App Developer Impact**:
- Apps targeting Android 11+ are blocked from accessing blacklisted and dark-greylist APIs via reflection, JNI, or direct linking.
- The `targetSdkVersion` determines which APIs are restricted; higher targets face stricter enforcement.
- Violations are logged even in `kJustWarn` mode, appearing in logcat as `Accessing hidden method/field`.
- Developers should migrate to public SDK alternatives or use `@UnsupportedAppUsage`-annotated alternatives where available.

---

## 13. Performance Implications Summary

### 13.1 Memory

| Factor | 影响 | Mitigation |
|--------|--------|------------|
| Default 256MB heap limit | Apps hitting limit get OOM | Use `largeHeap`, optimize allocations |
| TLAB overhead (32KB per thread) | Memory per thread | ART auto-sizes TLABs |
| JIT code cache | Additional memory for compiled code | Bounded, GC'd when full |
| Boot image sharing | Significant memory savings via Zygote | Automatic |
| Profile data | Small disk overhead per app | Managed by system |

### 13.2 CPU/Performance

| Factor | 影响 | Mitigation |
|--------|--------|------------|
| JIT warmup | First-launch slower | Profile-guided compilation |
| GC pauses (CC) | Sub-5ms typically | Generational CC reduces frequency |
| Class verification | Startup overhead | VDEX caching avoids re-verification |
| JNI transitions | Per-call overhead | Minimize JNI crossings, use `@CriticalNative` |
| Read barriers | ~1-2% runtime overhead | Enables concurrent GC with much lower pauses |

### 13.3 Best Practices for App Developers

1. **Minimize class count at startup** -- Each class loaded requires verification and linking.
2. **Avoid JNI for hot paths** -- Thread state transitions add overhead. Use `@FastNative` or `@CriticalNative` for simple JNI methods.
3. **Don't call System.gc()** -- ART's GC is well-tuned; explicit GC rarely helps and can cause unnecessary pauses.
4. **Avoid excessive allocation in loops** -- While TLAB allocation is fast, it still creates GC pressure.
5. **Use android:largeHeap judiciously** -- Increases heap but also increases GC times.
6. **Minimize static initializers** -- `<clinit>` blocks class loading and can form initialization cycles.
7. **Ship baseline profiles** -- Provide profile data with your APK for day-one optimized performance.
8. **Avoid reflection on hidden APIs** -- Will break on current and future Android versions.
9. **Close resources explicitly** -- `CloseGuard` will log warnings, and finalization is expensive.
10. **Prefer primitive arrays for large data** -- Objects >12KB go to large object space with different allocation patterns.

---

## 14. Key Architecture Diagrams (Textual)

### 14.1 Method Execution Flow

```
App calls method
    |
    v
[Entry point check]
    |
    +-- Already compiled (AOT/JIT)?
    |       |
    |       v
    |   Execute native code
    |       |
    |       v
    |   [Read barriers for GC]
    |
    +-- Not compiled?
            |
            v
        [Interpreter (mterp/nterp/switch)]
            |
            +-- Increment hotness counter
            |       |
            |       v
            |   Counter >= warmup_threshold?
            |       |
            |       +-- Start profiling (inline caches)
            |       |
            |       v
            |   Counter >= compile_threshold?
            |       |
            |       v
            |   [JIT thread: Optimizing Compiler]
            |       |
            |       v
            |   Store in JitCodeCache
            |       |
            |       v
            |   Update method entry point
            |
            +-- In hot loop?
                    |
                    v
                [OSR: On-Stack Replacement]
```

### 14.2 Class Loading Flow

```
ClassLinker::FindClass(descriptor, class_loader)
    |
    +-- Primitive type? --> Return cached primitive class
    |
    +-- Already loaded? --> LookupClass() in class table
    |       |
    |       v
    |   EnsureResolved() --> Return
    |
    +-- Boot class path? (class_loader == null)
    |       |
    |       v
    |   FindInClassPath() --> DefineClass()
    |
    +-- Array type? (descriptor starts with '[')
    |       |
    |       v
    |   CreateArrayClass()
    |
    +-- Known class loader type? (PCL/DLC)
    |       |
    |       v
    |   FindClassInBaseDexClassLoader()
    |       |
    |       +-- Check shared libraries
    |       +-- Search parent (parent-first or delegate-last)
    |       +-- Search own DexPathList
    |       |
    |       v
    |   DefineClass() --> Verify --> Link --> Initialize
    |
    +-- Unknown class loader type?
            |
            v
        Call ClassLoader.loadClass() in Java
```

---

## 15. File Reference Index

### Core Runtime
- `art/runtime/runtime.h` / `runtime.cc` -- Runtime singleton
- `art/runtime/class_linker.h` / `class_linker.cc` -- Class loading engine
- `art/runtime/class_loader_context.cc` -- Class loader context serialization
- `art/runtime/art_method.h` / `art_method.cc` -- Method representation
- `art/runtime/art_field.h` / `art_field.cc` -- Field representation
- `art/runtime/compiler_filter.cc` -- Compiler filter definitions

### GC
- `art/runtime/gc/heap.h` / `heap.cc` -- Heap management
- `art/runtime/gc/collector_type.h` -- Collector type enumeration
- `art/runtime/gc/gc_cause.h` -- GC trigger causes
- `art/runtime/gc/collector/concurrent_copying.cc` -- CC collector
- `art/runtime/gc/collector/mark_sweep.cc` -- Mark-sweep collector
- `art/runtime/gc/space/region_space.cc` -- Region-based allocation space
- `art/runtime/gc/accounting/card_table.cc` -- Write barrier tracking

### JIT
- `art/runtime/jit/jit.h` / `jit.cc` -- JIT compiler driver
- `art/runtime/jit/jit_code_cache.h` -- JIT code storage
- `art/runtime/jit/profile_saver.h` -- Profile collection

### Interpreter
- `art/runtime/interpreter/interpreter.cc` -- Interpreter entry point
- `art/runtime/interpreter/mterp/` -- Assembly-optimized interpreter

### Compiler
- `art/compiler/optimizing/` -- Optimizing compiler
- `art/dex2oat/dex2oat.cc` -- AOT compilation tool

### DEX
- `art/libdexfile/dex/dex_file.h` -- DEX file format definition
- `art/libdexfile/dex/compact_dex_file.h` -- Compact DEX format

### JNI
- `art/runtime/jni/jni_internal.cc` -- JNI implementation
- `art/runtime/jni/java_vm_ext.cc` -- JavaVM extensions
- `art/runtime/jni/check_jni.cc` -- JNI debugging

### Class Loaders (Java)
- `libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/PathClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/DexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/DelegateLastClassLoader.java`

### Hidden API
- `art/runtime/hidden_api.h` -- Hidden API enforcement

### Core Libraries
- `libcore/ojluni/src/main/java/java/lang/` -- java.lang package (106 files)
- `libcore/ojluni/src/main/java/java/util/` -- java.util package (128 files)
- `libcore/ojluni/src/main/java/java/io/` -- java.io package (88 files)
- `libcore/dalvik/src/main/java/dalvik/system/` -- Dalvik system classes
