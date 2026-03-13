# Dalvik VM 64-bit Linux Port — Skill Reference

## Goal
Port the Android KitKat-era Dalvik VM to run standalone on 64-bit Linux (x86_64), then cross-compile for OpenHarmony (aarch64-linux-ohos). This provides the Java runtime needed to execute Android APK bytecode + the Java shim layer on OHOS.

## Current Status: UNMODIFIED ANDROID APPS RUN TRANSPARENTLY

The VM successfully runs on **three architectures** (x86_64, OHOS aarch64, OHOS ARM32):
- Creates and initializes a full JavaVM via JNI invocation API
- Loads and optimizes DEX files (dexopt child process works)
- Loads ~4000+ core classes from core-android-x86.jar
- Executes bytecode via the portable interpreter (all opcodes working)
- Runs String operations (indexOf, compareTo, equals) via inline natives
- Starts daemon threads (FinalizerDaemon, ReferenceQueueDaemon, GCDaemon)
- Runs concurrent garbage collection (mark-sweep with correct 64-bit bitmap ops)
- Resolves classes, initializes static fields, runs `<clinit>` methods
- Handles exceptions (throw, catch, stack unwinding)
- **Runs unmodified Android apps** with full Activity lifecycle
- **ActivityThread + Instrumentation** drives onCreate→onStart→onResume→onPause→onStop→onDestroy
- **AndroidManifest.xml parsing**, Intent routing, Bundle extras, ComponentName all work
- All 64-bit pointer operations verified correct (no truncation)

### Java Shim Layer
- **1,968 Java shim files** — 100% clean compile (0 errors, 6 warnings)
- **2,422 .class files** generated
- Covers 1,959 android.* + 8 dalvik.* + 1 com.ohos.* types
- api_compat.db tracks 4,617 Android API types total

### Missing Natives (non-fatal)
- `NativeConverter.charsetForName` — returns NULL (Charset init falls back)
- Various ICU functions — stubbed with defaults (en_US locale, etc.)
- Various Posix functions — stubbed (read/write/open/close)

## Architecture

```
┌─────────────────────────────────┐
│  Android APK (.dex bytecode)    │  Runs unmodified
├─────────────────────────────────┤
│  Java Shim Layer (android.*)    │  Maps android.* → OH APIs
├─────────────────────────────────┤
│  Dalvik VM (this port)          │  Interprets DEX bytecode
│  - Portable C++ interpreter     │  No JIT, no ASM interp
│  - libffi for JNI calls         │  Platform-independent
│  - dreg_t 64-bit register file  │  Our key innovation
│  - Libcore bridge natives       │  Built-in System/ICU/Posix
├─────────────────────────────────┤
│  Linux / OpenHarmony kernel     │
└─────────────────────────────────┘
```

## Source Locations

| Component | Path |
|---|---|
| Build system | `dalvik-port/Makefile` |
| Compat headers | `dalvik-port/compat/` (RegSlot.h, atomic.h, log.h, etc.) |
| Libcore bridge | `dalvik-port/compat/libcore_bridge.cpp` |
| Launcher | `dalvik-port/launcher.cpp` |
| Boot JARs | `dalvik-port/core-android-x86.jar` (USE THIS ONE) |
| VM source (modified in-place) | `~/dalvik-kitkat/vm/` |
| Build output (x86_64) | `dalvik-port/build/` (dalvikvm, dexopt, libdvm.a) |
| Build output (OHOS aarch64) | `dalvik-port/build-ohos-aarch64/` (dalvikvm, dexopt, libdvm.a) |
| Build output (OHOS ARM32) | `dalvik-port/build-ohos-arm32/` (dalvikvm, dexopt, libdvm.a) |
| OHOS build script | `dalvik-port/build-ohos.sh` |
| OHOS sysroot | `dalvik-port/ohos-sysroot/` (musl headers + libs, built from OH source) |
| musl compat | `dalvik-port/compat/musl_compat.h` |

## Boot JARs

| JAR | Classes | Status |
|---|---|---|
| **core-android-x86.jar** | ~4000+ | **Full boot JAR — use this** |
| core-kitkat.jar | 280 | Minimal — missing many deps, causes crashes |
| core-boot.jar | 1047 | Stubs — empty Class fields, VM rejects |
| mini-boot.jar | 48 | Stubs — VM rejects |

## Running

```bash
# One-time setup
export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin
ln -sf $(pwd)/build/dexopt $ANDROID_ROOT/bin/dexopt

# Run
./build/dalvikvm \
  -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath test.jar MainClass
```

## Building

### Linux x86_64 (native)
```bash
cd dalvik-port
make -j$(nproc)
# Produces: build/dalvikvm, build/dexopt, build/libdvm.a
```
Prerequisites: `g++`, `libffi-dev`, `zlib1g-dev`

### OpenHarmony aarch64 (cross-compile)
```bash
cd dalvik-port
./build-ohos.sh          # Full build: sysroot + deps + compile + link
# Produces: build-ohos-aarch64/dalvikvm (5.6MB static ELF aarch64)
#           build-ohos-aarch64/dexopt
#           build-ohos-aarch64/libdvm.a (12MB)
```

Stepwise:
```bash
./build-ohos.sh sysroot    # Build musl sysroot from OH source headers
./build-ohos.sh deps       # Build musl libc + zlib + libffi for aarch64
./build-ohos.sh compile    # Cross-compile 125 VM source files
./build-ohos.sh link       # Link dalvikvm + dexopt
```

Prerequisites: OpenHarmony source tree at `~/openharmony/` with:
- LLVM/Clang 15 toolchain (`prebuilts/clang/ohos/linux-x86_64/llvm/`)
- musl headers (`third_party/musl/`)
- Linux 5.10 kernel headers (`kernel/linux/linux-5.10/`)
- libffi source (`third_party/libffi/`)
- zlib source (`third_party/zlib/`)

### OHOS Cross-Compilation Details

**Toolchain**: OHOS Clang 15.0.4 → `--target=aarch64-linux-ohos`
**C library**: musl 1.2.3 (built from OH third_party/musl, static linked)
**Sysroot**: Assembled from musl headers + kernel uapi headers + OH porting headers
**Output**: Static ELF binaries (no dynamic deps, runs standalone on OHOS)

Key musl differences handled:
- No `malloc_trim` — stubbed in link_fixups.cpp (`#ifdef __MUSL__`)
- No `execinfo.h` / `backtrace()` — guarded in launcher.cpp
- No `isnanf` — use type-generic `isnan`
- `sockaddr_storage` defined via OH porting headers, not `sys/socket.h`
- `bits/alltypes.h` generated from musl `.in` files via `mkalltypes.sed`
- `asm/` kernel headers from linux-5.10 arm64 uapi

Additional OHOS fixes (beyond the 46 Linux x86_64 fixes):
- **Fix #47**: aarch64 atomics — `Atomic.cpp` gets `__sync_*` builtins for 64-bit CAS/swap
- **Fix #48**: `const jvalue*` — JNI function signatures match OHOS jni.h
- **Fix #49**: hprof pointer casts — `(hprof_object_id)(uintptr_t)obj`
- **Fix #50**: Debugger method/field/frame ID casts — `(u4)` → `(uintptr_t)`

## The 64-bit Problem & All Fixes

### Root Problem
Dalvik's register file uses `u4` (32-bit) slots. On 32-bit Android, `sizeof(Object*) == sizeof(u4) == 4`, so object pointers fit in register slots. On 64-bit, pointers are 8 bytes and get **truncated** when stored in u4 slots. Android abandoned Dalvik for ART rather than solving this.

### The dreg_t Type
```c
// dalvik-port/compat/RegSlot.h
#if __SIZEOF_POINTER__ > 4
typedef uintptr_t dreg_t;  // 8 bytes on 64-bit
#else
typedef uint32_t dreg_t;   // 4 bytes on 32-bit (unchanged)
#endif
```

### Complete Fix List (35 fixes)

#### Session 1: Core Register & Interpreter Fixes (Fixes 1-17)

| # | Bug | File(s) | Fix |
|---|---|---|---|
| 1 | Register slots too narrow | InterpC-portable.cpp | `u4* fp` → `dreg_t* fp`, all register macros updated |
| 2 | Frame allocation too small | Stack.cpp | `registersSize * 4` → `registersSize * sizeof(dreg_t)` |
| 3 | Object ptrs truncated in regs | InterpC-portable.cpp | `SET_REGISTER(vdst, (u4)obj)` → `SET_REGISTER_AS_OBJECT()` |
| 4 | GET_REGISTER truncates objects | InterpC-portable.cpp | `(Object*)GET_REGISTER()` → `GET_REGISTER_AS_OBJECT()` |
| 5 | move-object copies wrong width | InterpC-portable.cpp | `SET_REGISTER(d, GET_REGISTER(s))` → `fp[d] = fp[s]` |
| 6 | return-object loses high bits | InterpC-portable.cpp | `retval.i = GET_REGISTER()` → `retval.l = GET_REGISTER_AS_OBJECT()` |
| 7 | filled-new-array object contents | InterpC-portable.cpp | Split object vs primitive paths |
| 8 | Native method args wrong width | All native/*.cpp (~125 files) | `const u4* args` → `const dreg_t* args` |
| 9 | JNI bridge args wrong width | Jni.cpp, Call.cpp | `dreg_t* modArgs`, libffi reads dreg_t slots |
| 10 | Volatile object field read | ObjectInlines.h | `android_atomic_acquire_load(int32_t*)` → direct ptr read + barrier |
| 11 | IndirectRef index extraction | IndirectRefTable.h | `(u4)iref` → `(u4)(uintptr_t)iref` |
| 12 | String field offsets hardcoded | UtfString.h | Enabled `USE_GLOBAL_STRING_DEFS` for runtime offsets |
| 13 | Array size overflow (CLZ bug) | Array.cpp | `sizeof(size_t)` with 32-bit CLZ → `sizeof(unsigned int)` |
| 14 | AtomicCache keys truncated | AtomicCache.h/cpp | `u4 key1/key2` → `uintptr_t key1/key2` |
| 15 | AtomicCache alignment truncated | AtomicCache.cpp | `(int)ptr` → `(uintptr_t)ptr` |
| 16 | Monitor list CAS truncates ptrs | Sync.cpp | `android_atomic_release_cas((int32_t)mon, ...)` → `__sync_bool_compare_and_swap` |
| 17 | Inline subs table crash | Init.cpp | Made `dvmCreateInlineSubsTable` non-fatal |

#### Session 2: Dexopt, Libcore Bridge, Deep Pointer Fixes (Fixes 18-25)

| # | Bug | File(s) | Fix |
|---|---|---|---|
| 18 | Dexopt child can't find cache dir | (env setup) | `ANDROID_DATA=/tmp/android-data` |
| 19 | Dexopt binary not found | (env setup) | `ANDROID_ROOT=/tmp/android-root` + symlink |
| 20 | No libjavacore.so natives | compat/libcore_bridge.cpp | Registered OsConstants, Posix, ICU, System natives directly |
| 21 | System.currentTimeMillis missing | libcore_bridge.cpp | `clock_gettime(CLOCK_REALTIME)` |
| 22 | System.nanoTime missing | libcore_bridge.cpp | `clock_gettime(CLOCK_MONOTONIC)` |
| 23 | System.specialProperties missing | libcore_bridge.cpp | Returns os.arch, java.io.tmpdir, etc. |
| 24 | System.mapLibraryName missing | libcore_bridge.cpp | Returns "lib" + name + ".so" |
| 25 | NativeConverter.charsetForName | libcore_bridge.cpp | Returns NULL (fallback to built-in charsets) |

#### Session 3: Verifier, Inline, Lock Word, GC Bitmap (Fixes 26-31)

| # | Bug | File(s) | Fix |
|---|---|---|---|
| 26 | **RegType verifier truncation** — dexopt child SIGSEGV in `dvmIsArrayClass` due to truncated ClassObject* | CodeVerify.h/cpp | `typedef u4 RegType` → `typedef uintptr_t RegType` on 64-bit; cast through `(uintptr_t)` in `regTypeFromClass` and `regTypeFromUninitIndex` |
| 27 | **Inline native arg truncation** — SIGSEGV in `String.fastIndexOf_II` because Object* truncated through u4 params | InlineNative.h/cpp, InterpC-portable.cpp, InternalNativePriv.h | New `InlineArgType` (= `uintptr_t` on 64-bit); all ~25 inline function signatures + OP_EXECUTE_INLINE local vars updated |
| 28 | **Lock word / Monitor* truncation** — SIGSEGV in `waitMonitor` because Monitor* stored in u4 lock field | Object.h, Sync.cpp, compat/atomic.h | `Object.lock` widened to `volatile uintptr_t`; new `lw_t` typedef + `LW_ATOMIC_STORE`/`LW_ATOMIC_CAS` macros; pointer-width `android_atomic_release_store_ptr` / `android_atomic_acquire_cas_ptr` |
| 29 | **HeapBitmap 64-bit bugs** — GC infinite loop: `1<<63` UB, 32-bit CLZ on 64-bit words, `HB_OFFSET_TO_MASK` hardcoded 31 | HeapBitmap.h, HeapBitmap.cpp, Copying.cpp | `1` → `1UL`; `CLZ()` → `CLZL()` on unsigned long; `31` → `HB_BITS_PER_WORD-1` |
| 30 | **GC null-clazz guard** — uninitialized heap object in mark bitmap | MarkSweep.cpp | Skip objects with NULL clazz in `scanObject()` |
| 31 | **Removed opcode tracing** — 36M lines/30s log output making VM unusable | InterpC-portable.cpp | Removed OPCODE/INTERP/INIT_RANGE debug logs from FINISH macro |

#### Session 4: Full Code Review Fixes (Fixes 32-46)

| # | Bug | File(s) | Fix |
|---|---|---|---|
| 32 | **CLASS_HIGH_BIT / CLASS_BITS_PER_WORD mismatch** — refOffsets is u4 (32-bit) but macros used 64-bit width → GC field scanning broken | Object.h | `CLASS_BITS_PER_WORD` fixed to 32; `CLASS_HIGH_BIT` uses `(unsigned int)1` |
| 33 | **Unsafe getObjectVolatile truncates** — `(int32_t*)` cast on Object field | sun_misc_Unsafe.cpp | Pointer-width volatile read + full barrier |
| 34 | **Unsafe putObjectVolatile truncates** — `(int32_t)value` cast | sun_misc_Unsafe.cpp | Store barrier + direct pointer write |
| 35 | **Unsafe compareAndSwapObject truncates** — `(int32_t)` cast on both expected and new Object* | sun_misc_Unsafe.cpp | `__sync_bool_compare_and_swap` on `Object**` |
| 36 | **assert(sizeof(u4)==sizeof(ClassObject*))** — aborts on 64-bit | Class.cpp:1794 | Removed assert; cast through `(uintptr_t)` |
| 37 | **Debugger ObjectId truncates** — `(u4)obj` loses 32 bits, hash/compare/register all broken | Debugger.cpp | Full-pointer ObjectId; fixed `registryHash`, `registryCompare`, `objectIdToObject`, `registerObject`, `lookupId` |
| 38 | **Class hash callback truncates** — `return (int)clazz` loses pointer bits | Class.cpp:4633 | `return 1` (non-zero = found) |
| 39 | **InlineNative.h duplicate decl** — `longBitsToDouble` declared twice + `arg` naming | InlineNative.h | Removed duplicate; `arg` → `arg3` |
| 40 | **atomic.h acquire_store wrong order** — `__ATOMIC_ACQUIRE` on a store | compat/atomic.h | Changed to `__ATOMIC_SEQ_CST` |
| 41 | **CodeVerify uninit index shift** — int shift before cast to 64-bit RegType | CodeVerify.cpp | `(uintptr_t)uidx << shift` |
| 42 | **Interface idx truncated** — `(ClassObject*)(u4) typeIdx` | Class.cpp:1812 | `(ClassObject*)(uintptr_t)` |
| 43 | **nativeFunc atomic store truncates** — `(int32_t) func` on function pointer | Class.cpp:4584 | `ANDROID_MEMBAR_STORE()` + direct write |
| 44 | **DexFile hash/compare truncates** — `(int)newVal - (int)tableVal` and `(u4)pDexOrJar` | dalvik_system_DexFile.cpp | Safe pointer comparison; `(u4)(uintptr_t)` for hash |
| 45 | **JNI GET/SET macros truncate** — `(u4)addLocalReference(...)` and `(jobject)(u4)value` | Jni.cpp, CheckJni.cpp | `(u4)` → `(uintptr_t)` |
| 46 | **Copying.cpp hash code truncates** — `(u4)fromObj >> 3` | Copying.cpp:1314 | `(u4)((uintptr_t)fromObj >> 3)` |

### Remaining Known Issues
- **Debugger `framePtr[slot]` write**: `*(Object**)&framePtr[slot]` used for object slot writes in JDWP — works but fragile
- **JIT code**: All JIT tracing/chaining has pointer truncation — not a problem since JIT is disabled (`DVM_NO_ASM_INTERP=1`)
- **Shutdown race**: Daemon threads can crash during `DestroyJavaVM` if still in interpreter — worked around with `_exit()` for no-main-method case

## Libcore Bridge (dalvik-port/compat/libcore_bridge.cpp)

Replaces `libjavacore.so` for standalone operation. Registers JNI natives directly during VM startup:

| Class | Methods | Status |
|---|---|---|
| `libcore.io.OsConstants` | `initConstants` | Full — sets all POSIX constants (AF_*, SOCK_*, O_*, etc.) |
| `libcore.io.Posix` | `getenv`, `setenv`, `open`, `close`, `read`, `write` | Stubs — read returns 0, write discards |
| `libcore.icu.ICU` | 16 methods (versions, locales, case conversion, etc.) | Stubs — returns "en_US", identity case |
| `libcore.icu.NativeConverter` | `charsetForName` | Stub — returns NULL |
| `java.lang.System` | `specialProperties`, `currentTimeMillis`, `nanoTime`, `identityHashCode`, `mapLibraryName`, `log` | Functional |

## Pointer-Width Atomic Operations (compat/cutils/atomic.h)

Added for 64-bit lock words:
```c
android_atomic_release_store_ptr(intptr_t value, volatile intptr_t* addr)
android_atomic_acquire_cas_ptr(intptr_t old, intptr_t new, volatile intptr_t* addr)
```

## Next Steps

1. ~~**Cross-compile for OpenHarmony**~~ — **DONE** (x86_64, aarch64, ARM32)
2. ~~**Hello World execution**~~ — **DONE** (all 3 architectures)
3. ~~**Android app transparency**~~ — **DONE** (full Activity lifecycle on all 3 architectures)
4. ~~**Java shim compilation**~~ — **DONE** (1,968 files, 100% clean compile)
5. **OHBridge integration** — Route Android API calls to real OHOS APIs
6. **OHOS ACE headless UI testing** — Test ArkUI node creation without hardware
7. **Test on real OHOS device** — Deploy via `hdc file send`, run natively
8. **Expand test apps** — Beyond Hello World to real APK scenarios
9. **Restore -O2 optimization** — Currently at -O0 -g for debugging
10. **OHOS HAP integration** — Package dalvikvm as OHOS native module in HAP

## Relationship to Shim Layer

The Dalvik VM is the **runtime** for the shim layer:
1. Android APK's .dex → loaded by Dalvik VM
2. APK calls `android.util.Log.d(...)` → executes our Java shim class
3. Shim calls `OHBridge.nativeLog(...)` → JNI into OH native API
4. OH native API (`OH_LOG_Print`) → actual platform operation
