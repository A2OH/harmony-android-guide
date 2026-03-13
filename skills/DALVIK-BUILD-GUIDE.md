# Dalvik VM 64-bit Linux Port — Complete Build Guide

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Source Tree Layout](#source-tree-layout)
4. [Compatibility Layer (compat/)](#compatibility-layer)
5. [Build System (Makefile)](#build-system)
6. [Compiler Flags Explained](#compiler-flags-explained)
7. [Source Files Compiled](#source-files-compiled)
8. [Link Libraries](#link-libraries)
9. [Build Targets](#build-targets)
10. [The Launcher (launcher.cpp)](#the-launcher)
11. [Boot Environment Setup](#boot-environment-setup)
12. [Boot JAR Selection](#boot-jar-selection)
13. [The 64-bit Problem](#the-64-bit-problem)
14. [All 64-bit Fixes — Detailed](#all-64-bit-fixes)
15. [Non-Fatal Workarounds](#non-fatal-workarounds)
16. [Debugging History](#debugging-history)
17. [Full Build & Run Walkthrough](#full-build-and-run-walkthrough)
18. [Remaining Work](#remaining-work)

---

## Overview

This project ports the **Android KitKat (4.4) era Dalvik Virtual Machine** to run standalone on **64-bit Linux (x86_64)**. Google never solved the 64-bit problem for Dalvik — they abandoned it for ART. We did the 64-bit fix ourselves.

The VM is the runtime for the Android-to-OpenHarmony shim layer: Android APK DEX bytecode runs on this VM, calls `android.*` shim classes, which bridge to OpenHarmony native APIs.

**Current status**: VM boots on 64-bit Linux, loads 280+ core classes, executes bytecode, runs GC, shuts down cleanly. Blocked on `libjavacore.so` (JNI native library for core Java classes).

## Prerequisites

| Dependency | Version | Purpose |
|---|---|---|
| g++ / gcc | C++11 capable | Compile VM source |
| libz (zlib) | System | ZIP/JAR file handling |
| libffi | 8.x | Platform-independent JNI calling convention |
| libpthread | System | Thread support |
| libdl | System | Dynamic library loading |
| ar | System | Create static library |
| make | GNU Make | Build system |

**Source trees required**:
- `~/dalvik-kitkat/` — AOSP KitKat Dalvik VM source (vm/, libdex/, dexopt/)
- `~/aosp-android-11/` — AOSP headers (libnativehelper/include_jni for jni.h, external/zlib, external/dlmalloc)

**No JDK required** for building the VM itself. JDK is only needed to compile .java → .class → .dex test files.

## Source Tree Layout

```
android-to-openharmony-migration/
└── dalvik-port/
    ├── Makefile              # Main build system (269 lines)
    ├── CMakeLists.txt        # Alternative CMake build (252 lines)
    ├── launcher.cpp          # Standalone dalvikvm launcher (140 lines)
    ├── core-kitkat.jar       # Boot classpath JAR (280 classes) ← USE THIS
    ├── core-boot.jar         # Stub JAR (1047 classes) — DO NOT USE
    ├── mini-boot.jar         # Minimal stub JAR (48 classes) — DO NOT USE
    ├── compat/               # Compatibility headers and stubs
    │   ├── RegSlot.h         # dreg_t typedef — the core 64-bit fix
    │   ├── link_fixups.cpp   # Linker stubs for missing bionic/mterp symbols
    │   ├── JNIHelp.h         # JNI helper stubs
    │   ├── JniConstants.h    # JNI constant stubs
    │   ├── ScopedPthreadMutexLock.h
    │   ├── UniquePtr.h       # Android smart pointer (pre-C++11)
    │   ├── safe_iop.h        # Safe integer overflow checking
    │   ├── ffi.h             # libffi wrapper header
    │   ├── cutils/           # Android cutils replacements
    │   │   ├── atomic.h          # Atomic operations → GCC builtins
    │   │   ├── atomic-inline.h   # Inline atomic helpers
    │   │   ├── ashmem.h          # Anonymous shared memory → shm_open
    │   │   ├── log.h             # ALOG* → fprintf(stderr)
    │   │   ├── memory_barrier.h  # Memory barriers → __sync_synchronize
    │   │   ├── fs.h              # Filesystem helpers
    │   │   ├── multiuser.h       # Multi-user stubs
    │   │   ├── open_memstream.h  # open_memstream wrapper
    │   │   ├── process_name.h    # Process name stubs
    │   │   ├── sched_policy.h    # Scheduler policy stubs
    │   │   ├── sockets.h         # Socket stubs
    │   │   └── trace.h           # Systrace stubs (no-op)
    │   ├── selinux/
    │   │   └── android.h         # SELinux stubs (no-op on Linux)
    │   ├── sys/
    │   │   └── capability.h      # Linux capabilities stubs
    │   ├── system/
    │   │   └── thread_defs.h     # Thread priority definitions
    │   ├── log/
    │   │   └── (log header redirects)
    │   ├── nativehelper/
    │   │   └── (JNI helper redirects)
    │   ├── utils/
    │   │   └── Compat.h          # Bionic compatibility macros
    │   └── bionic/
    │       └── (bionic-specific stubs)
    └── build/                # Build output directory
        ├── libdvm.a          # Static library (~all VM + libdex objects)
        ├── dalvikvm           # VM launcher executable
        ├── dexopt             # DEX optimizer tool
        └── *.o               # Object files
```

**VM source (modified in-place)**:
```
~/dalvik-kitkat/
├── vm/                       # Dalvik VM core
│   ├── mterp/out/InterpC-portable.cpp  # Portable interpreter (HEAVILY modified)
│   ├── interp/Stack.cpp      # Stack frame allocation (modified)
│   ├── Jni.cpp               # JNI bridge (modified)
│   ├── Init.cpp              # VM initialization (modified)
│   ├── Sync.cpp              # Monitor/synchronization (modified)
│   ├── AtomicCache.cpp/.h    # Atomic cache (modified)
│   ├── IndirectRefTable.h    # Indirect references (modified)
│   ├── UtfString.h           # String field offsets (modified)
│   ├── Common.h              # CLZ macros (modified)
│   ├── Native.h              # Native method types (modified)
│   ├── oo/
│   │   ├── Object.h          # DalvikBridgeFunc/NativeFunc signatures (modified)
│   │   ├── ObjectInlines.h   # Volatile field accessors (modified)
│   │   └── Array.cpp         # Array allocation CLZ fix (modified)
│   ├── arch/generic/Call.cpp  # dvmPlatformInvoke (modified)
│   └── native/*.cpp          # ~28 native method files (all modified: u4→dreg_t)
├── libdex/                   # DEX file parser library
└── dexopt/                   # DEX optimization tool
    └── OptMain.cpp
```

## Compatibility Layer

The `compat/` directory provides standalone Linux replacements for Android-specific APIs.

### RegSlot.h — The Core 64-bit Fix

```c
#if __SIZEOF_POINTER__ > 4
typedef uintptr_t dreg_t;  // 8 bytes on 64-bit
#else
typedef uint32_t dreg_t;   // 4 bytes on 32-bit (unchanged)
#endif
```

This single typedef is the foundation of the entire 64-bit port. Every register slot in the interpreter frame, every native method argument array, and every JNI bridge buffer uses `dreg_t` instead of `u4`.

### link_fixups.cpp — Linker Stubs

Compiled separately (without AOSP dlmalloc include path) to avoid macro conflicts. Provides:

| Symbol | What it does |
|---|---|
| `dlmalloc_trim()` | Forwards to glibc `malloc_trim()` |
| `dlmalloc_inspect_all()` | No-op (heap inspection not needed) |
| `dlmem2chunk()` | Identity function (dlmalloc internals) |
| `jniRegisterSystemMethods()` | No-op (JNI system method registration) |
| `dvmCreateStringFromCstrAndLength(u4)` | Thunk: forwards to `size_t` version (u4/size_t mismatch on 64-bit) |
| `dvmMterpStdRun()` | Fatal stub — assembly interpreter disabled |
| `dvmMterpStdBail()` | Fatal stub — assembly interpreter disabled |

### cutils/ — Android System Library Replacements

**cutils/atomic.h**: Android's `android_atomic_*` functions reimplemented using GCC `__sync_*` builtins. Critical for the 64-bit port since Android's atomics assume 32-bit values.

**cutils/ashmem.h**: Android anonymous shared memory → POSIX `shm_open()` / `mmap()`.

**cutils/log.h**: ALOG* macros (ALOGE, ALOGW, ALOGI, ALOGD, ALOGV) → `fprintf(stderr, ...)`.

**cutils/trace.h**: Systrace macros → no-op (not needed on standalone Linux).

## Build System

The Makefile builds three targets from two source trees:

### Paths
```makefile
DALVIK    := /home/dspfac/dalvik-kitkat       # AOSP KitKat Dalvik source
AOSP      := /home/dspfac/aosp-android-11     # AOSP headers
VM        := $(DALVIK)/vm                     # VM subdirectory
LIBDEX    := $(DALVIK)/libdex                 # DEX parser library
COMPAT    := $(CURDIR)/compat                 # Our compatibility headers
BUILD     := $(CURDIR)/build                  # Build output
```

### Include Path Order
```
-I$(VM)                                   # VM internal headers
-I$(DALVIK)                               # Top-level dalvik headers
-I$(COMPAT)                               # Our compat headers (override Android ones)
-I$(AOSP)/libnativehelper/include_jni     # jni.h
-I$(AOSP)/external/zlib                   # zlib.h
-I$(AOSP)/external/dlmalloc              # dlmalloc.h (for DlMalloc.cpp only)
```

**Important**: `$(COMPAT)` is in the include path *before* AOSP headers, so our replacement headers (cutils/atomic.h, cutils/log.h, etc.) are found first.

## Compiler Flags Explained

### C Flags (for .c files)
```
-std=c11                    # C11 standard
-fPIC                       # Position-independent code
-O2                         # Optimization level 2
```

### C++ Flags (for .cpp files — the majority)
```
-std=c++11                  # C++11 standard
-fPIC -O2                   # PIC + optimization

# Platform defines
-DHAVE_LITTLE_ENDIAN        # x86_64 is little-endian
-DHAVE_ENDIAN_H             # <endian.h> available
-DHAVE_SYS_UIO_H            # <sys/uio.h> available
-D_GNU_SOURCE               # GNU extensions (prlimit, etc.)
-DANDROID_SMP=1             # SMP memory ordering (memory barriers active)
-DLOG_TAG='"dalvikvm"'      # Log tag for ALOG* macros
-DDVM_SHOW_EXCEPTION=1      # Show exception details on stderr
-DNDEBUG                    # Disable assert() (release mode)

# Dalvik-specific
-DHAVE_POSIX_FILEMAP        # Use mmap for file mapping (not Win32)
-DDVM_NO_ASM_INTERP=1       # Disable assembly interpreter (use portable C)
-DDVM_JMP_TABLE_MTERP=1     # Use jump table dispatch in portable interp
-DHAVE_STRLCPY=1            # strlcpy available (via compat or glibc)
-Dtypeof=__typeof__         # GNU typeof → standard __typeof__

# Compiler behavior
-fpermissive                # Allow some C++ type mismatches (needed for old AOSP code)
-include unistd.h           # Force-include POSIX headers
-include sys/uio.h
-include sys/resource.h

# Warning suppression
-Wno-unused-parameter       # Many AOSP functions have unused params
-Wno-sign-compare           # Signed/unsigned comparison in old code
-Wno-write-strings          # String literal → char* in old code
-Wno-format                 # printf format mismatches
-Wno-narrowing              # Narrowing conversions in initializers
-Wno-pointer-arith          # Pointer arithmetic on void*
```

## Source Files Compiled

### libdex (18 files) — DEX File Parser
```
DexFile.cpp         DexSwapVerify.cpp    DexCatch.cpp
DexClass.cpp        DexDataMap.cpp       DexDebugInfo.cpp
DexInlines.cpp      DexOpcodes.cpp       DexOptData.cpp
DexProto.cpp        DexUtf.cpp           InstrUtils.cpp
Leb128.cpp          OptInvocation.cpp    SysUtil.cpp
ZipArchive.cpp      CmdUtils.cpp         sha1.cpp
```

### DVM Core (107 files) — The VM Itself

**Core VM** (28 files):
```
AllocTracker    Atomic         AtomicCache     BitVector
CheckJni        Ddm            Debugger         DvmDex
Exception       Hash           IndirectRefTable Init
InitRefs        InlineNative   Inlines          Intern
Jni             JarFile        LinearAlloc      Misc
Native          PointerSet     Profile          RawDexFile
ReferenceTable  SignalCatcher  StdioConverter   Sync
Thread          UtfString
```

**Garbage Collector** (alloc/ — 10 files):
```
Alloc       CardTable    HeapBitmap   HeapDebug
Heap        DdmHeap      Verify       Visit
DlMalloc    HeapSource   MarkSweep
```

**Bytecode Analysis** (analysis/ — 7 files):
```
CodeVerify   DexPrepare   DexVerify    Liveness
Optimize     RegisterMap  VerifySubs   VfyBasicBlock
```

**Interpreter** (interp/ + mterp/ — 4 files):
```
Interp.cpp              # Interpreter dispatch
Stack.cpp               # Stack frame management
Mterp.cpp               # mterp entry point (routes to portable)
InterpC-portable.cpp    # THE portable C interpreter (all opcodes)
```

**Object Model** (oo/ — 6 files):
```
AccessCheck   Array    Class    Object
Resolve       TypeCheck
```

**Reflection** (reflect/ — 3 files):
```
Annotation   Proxy    Reflect
```

**Native Methods** (native/ — 28 files):
```
InternalNative                          dalvik_bytecode_OpcodeInfo
dalvik_system_DexFile                   dalvik_system_VMDebug
dalvik_system_VMRuntime                 dalvik_system_VMStack
dalvik_system_Zygote                    java_lang_Class
java_lang_Double                        java_lang_Float
java_lang_Math                          java_lang_Object
java_lang_Runtime                       java_lang_String
java_lang_System                        java_lang_Throwable
java_lang_VMClassLoader                 java_lang_VMThread
java_lang_reflect_AccessibleObject      java_lang_reflect_Array
java_lang_reflect_Constructor           java_lang_reflect_Field
java_lang_reflect_Method                java_lang_reflect_Proxy
java_util_concurrent_atomic_AtomicLong
org_apache_harmony_dalvik_NativeTestTarget
org_apache_harmony_dalvik_ddmc_DdmServer
org_apache_harmony_dalvik_ddmc_DdmVmInternal
sun_misc_Unsafe
```

**Debug/Profile** (jdwp/ + hprof/ — 12 files):
```
ExpandBuf    JdwpAdb     JdwpConstants  JdwpEvent
JdwpHandler  JdwpMain    JdwpSocket
Hprof        HprofClass  HprofHeap      HprofOutput  HprofString
```

**Platform** (2 files):
```
os/linux.cpp            # Linux-specific OS functions
arch/generic/Call.cpp   # Generic calling convention (libffi)
arch/generic/Hints.cpp  # No JIT hints on portable build
```

**Test** (1 file):
```
test/TestHash.cpp
```

### Compat (1 file — compiled separately):
```
link_fixups.cpp         # Linker stubs (no AOSP dlmalloc include)
```

**Total: 18 (libdex) + 107 (dvm) + 1 (compat) = 126 source files**

## Link Libraries

```makefile
LDFLAGS := -lpthread -ldl -l:libz.so.1 -l:libffi.so.8
```

| Library | Purpose |
|---|---|
| `-lpthread` | POSIX threads (VM threading, monitors, signal catcher) |
| `-ldl` | Dynamic linker (dlopen/dlsym for JNI .so loading) |
| `-l:libz.so.1` | zlib (reading ZIP/JAR files, DEX compression) |
| `-l:libffi.so.8` | Foreign Function Interface (JNI native method calls) |

**Note**: Using `-l:libname.so.N` syntax forces linking against a specific soname version, avoiding version mismatches.

## Build Targets

### `make all` — Full Build
Runs: `build-all` → `link` → `dalvikvm` → `dexopt`

### `make build-all` — Compile Everything
Compiles all 125 source files one by one. Reports pass/fail count and error summary for failed files.

### `make link` — Create Static Library
```bash
ar rcs build/libdvm.a build/*.o
```
All object files (libdex + dvm + link_fixups) go into a single static library.

### `make dalvikvm` — Build Launcher
```bash
g++ $(CXXFLAGS) -o build/dalvikvm launcher.cpp build/libdvm.a $(LDFLAGS)
```

### `make dexopt` — Build DEX Optimizer
```bash
g++ $(CXXFLAGS) -o build/dexopt $(DALVIK)/dexopt/OptMain.cpp build/libdvm.a $(LDFLAGS)
```
Required at runtime — the VM spawns `dexopt` to verify and optimize DEX files on first load.

### `make first-pass` — Quick Compile Test
Compiles just DexFile.cpp, Init.cpp, and InterpC-portable.cpp to quickly check for header/include issues.

### `make clean`
```bash
rm -rf build/
```

## The Launcher

`launcher.cpp` is a 140-line standalone program that replaces Android's `dalvikvm` command. It uses the standard **JNI Invocation API**:

1. **Parse arguments**: `-cp <classpath>`, `-X*` VM flags, class name, app args
2. **Build JNI options**: Boot classpath from `$BOOTCLASSPATH` env var, user classpath, `-X` passthrough, default heap 4MB–64MB
3. **Create VM**: `JNI_CreateJavaVM(&vm, &env, &vmArgs)`
4. **Find main class**: `env->FindClass("com/foo/Bar")`
5. **Find main method**: `env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V")`
6. **Build String[] args**: `env->NewObjectArray()` + `env->NewStringUTF()`
7. **Invoke**: `env->CallStaticVoidMethod(cls, mainMethod, mainArgs)`
8. **Cleanup**: `vm->DestroyJavaVM()`

Includes a crash handler that prints a backtrace on SIGSEGV/SIGABRT.

## Boot Environment Setup

The VM requires two environment variables and a specific directory structure:

```bash
# 1. Data directory (for dalvik-cache, where optimized DEX files are stored)
export ANDROID_DATA=/tmp/android-data
mkdir -p $ANDROID_DATA/dalvik-cache

# 2. Root directory (the VM looks for dexopt at $ANDROID_ROOT/bin/dexopt)
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_ROOT/bin
ln -sf $(pwd)/build/dexopt $ANDROID_ROOT/bin/dexopt
```

**Why dalvik-cache?** When the VM loads a JAR/DEX for the first time, it spawns `dexopt` to verify the bytecode and produce an optimized `.odex` file. This is cached in `$ANDROID_DATA/dalvik-cache/` so subsequent runs are faster.

**Why dexopt symlink?** The VM uses `execv("$ANDROID_ROOT/bin/dexopt", ...)` to launch the optimizer. On real Android, this is at `/system/bin/dexopt`. Our symlink points to our build.

## Boot JAR Selection

| JAR | Classes | java.lang.Class fields | Works? |
|---|---|---|---|
| **core-kitkat.jar** | 280 | 1 static, 4 instance | **YES — use this** |
| core-boot.jar | 1047 | 0 static, 0 instance | **NO** — stubs, VM rejects |
| mini-boot.jar | 48 | 0 static, 0 instance | **NO** — stubs, VM rejects |

**Why does java.lang.Class matter?** The VM hardcodes expectations about `java.lang.Class`:
- It must have exactly 1 static field (the `serialVersionUID`)
- It must have specific instance fields (`name`, `status`, `verifyErrorClass`, `initiatingLoaderList`)
- The stub JARs have empty Class definitions with 0 fields → VM aborts during startup

**core-kitkat.jar** is extracted from a real KitKat ROM and contains properly compiled core classes.

## The 64-bit Problem

### Root Cause
Dalvik was designed exclusively for 32-bit ARM. Throughout the codebase:
- Register slots are `u4` (uint32_t, 4 bytes)
- Object references are `Object*` (4 bytes on 32-bit)
- Since `sizeof(Object*) == sizeof(u4) == 4`, pointers fit in register slots

On 64-bit Linux:
- `sizeof(Object*) = 8` bytes
- `sizeof(u4) = 4` bytes
- **Storing a pointer in a u4 truncates the upper 32 bits**
- The pointer `0x00007f8a12345678` becomes `0x12345678` → SIGSEGV

Google's solution: abandon Dalvik, build ART (Android Runtime) with 64-bit support from the start. **Our solution: widen all register slots to pointer-width using `dreg_t`.**

### Size Changes on 64-bit

| Type/Struct | 32-bit | 64-bit | Impact |
|---|---|---|---|
| `Object*` | 4 bytes | 8 bytes | Pointers in registers truncated |
| `Object` header | 8 bytes (clazz + lock) | 16 bytes (8 clazz + 4 lock + 4 pad) | All field offsets shift |
| `ArrayObject.contents` offset | 12 | 24 | Array data access |
| `DataObject.instanceData` offset | 8 | 16 | Instance field access |
| Register slot (`dreg_t`) | 4 bytes | 8 bytes | Frame size doubles |
| `AtomicCacheEntry` keys | 4 bytes each | 8 bytes each | Cache lookup |

## All 64-bit Fixes — Detailed

### Fix 1: Register Slots (InterpC-portable.cpp)

**Problem**: `u4* fp` (frame pointer) holds the register file. Each slot is 4 bytes. Object pointers are 8 bytes.

**Fix**: Change frame pointer type and all macros:
```cpp
// BEFORE
u4* fp;
#define GET_REGISTER(_idx)  (fp[(_idx)])
#define SET_REGISTER(_idx, _val)  (fp[(_idx)] = (u4)(_val))

// AFTER
dreg_t* fp;
#define GET_REGISTER(_idx)  ((u4)fp[(_idx)])  // Still returns u4 for int values
#define SET_REGISTER(_idx, _val)  (fp[(_idx)] = (dreg_t)(u4)(_val))  // Zero-extend
```

The object-specific macros read/write full pointer width:
```cpp
#define GET_REGISTER_AS_OBJECT(_idx)  ((Object*)fp[(_idx)])
#define SET_REGISTER_AS_OBJECT(_idx, _val)  (fp[(_idx)] = (dreg_t)(_val))
```

### Fix 2: Frame Allocation (Stack.cpp)

**Problem**: Frame allocation calculates size as `registersSize * 4` (bytes per u4 slot).

**Fix**:
```cpp
// BEFORE
size_t frameSize = registersSize * 4;

// AFTER
size_t frameSize = registersSize * sizeof(dreg_t);  // 8 bytes per slot on 64-bit
```

Also fixed: `ins` pointer calculation (pointer to incoming arguments within the frame) and argument stores that write object references into frame slots.

### Fix 3: Object Pointer Truncation in SET_REGISTER (InterpC-portable.cpp)

**Problem**: Many opcodes store objects using `SET_REGISTER(vdst, (u4)obj)`, which casts the pointer to u4 first (truncating it).

**Fix**: Replace all such patterns with `SET_REGISTER_AS_OBJECT(vdst, obj)`:
- `OP_CONST_CLASS`, `OP_CONST_STRING`, `OP_CONST_STRING_JUMBO`
- `OP_NEW_INSTANCE`, `OP_NEW_ARRAY`
- `OP_CHECK_CAST` (stores checked object back)
- `OP_IGET_OBJECT`, `OP_SGET_OBJECT`
- `OP_AGET_OBJECT`
- `OP_MOVE_RESULT_OBJECT`
- All exception handler object stores

### Fix 4: Object Pointer Truncation in GET_REGISTER (InterpC-portable.cpp)

**Problem**: Opcodes that read objects use `(Object*)GET_REGISTER(vsrc)`. `GET_REGISTER` returns `u4` (truncated), then casts to pointer (sign-extends wrong bits).

**Fix**: Replace with `GET_REGISTER_AS_OBJECT(vsrc)`:
- `OP_IPUT_OBJECT`, `OP_SPUT_OBJECT`
- `OP_APUT_OBJECT`
- `OP_INSTANCE_OF`, `OP_CHECK_CAST` (read source object)
- `OP_MONITOR_ENTER`, `OP_MONITOR_EXIT`
- `OP_THROW`
- `OP_IF_EQZ`, `OP_IF_NEZ` (null checks on objects)
- `OP_INVOKE_*` (this pointer)

### Fix 5: move-object Wrong Width (InterpC-portable.cpp)

**Problem**: `OP_MOVE_OBJECT` uses `SET_REGISTER(d, GET_REGISTER(s))` which goes through u4.

**Fix**: Direct pointer-width copy: `fp[d] = fp[s]`

Same fix for `OP_MOVE_OBJECT_FROM16` and `OP_MOVE_OBJECT_16`.

### Fix 6: return-object Loses High Bits (InterpC-portable.cpp)

**Problem**: `OP_RETURN_OBJECT` stores result as `retval.i = GET_REGISTER(vsrc)` — `retval.i` is `int32_t`.

**Fix**: `retval.l = GET_REGISTER_AS_OBJECT(vsrc)` — `retval.l` is `Object*` (pointer-width).

### Fix 7: filled-new-array Object Contents (InterpC-portable.cpp)

**Problem**: `OP_FILLED_NEW_ARRAY` copies register values into array contents using `u4*` pointer. For object arrays, elements are `Object*` (8 bytes on 64-bit), but the code writes them as u4 (4 bytes).

**Fix**: Split into object and primitive paths:
```cpp
if (typeCh == 'L' || typeCh == '[') {
    // Object array: elements are Object* (pointer-width)
    Object** objContents = (Object**)(void*)newArray->contents;
    for (i = 0; i < vsrc1; i++)
        objContents[i] = GET_REGISTER_AS_OBJECT(vdst+i);
    dvmWriteBarrierArray(newArray, 0, newArray->length);
} else {
    // Primitive array: elements are u4 (always 32-bit)
    contents = (u4*)(void*)newArray->contents;
    for (i = 0; i < vsrc1; i++)
        contents[i] = GET_REGISTER(vdst+i);
}
```

### Fix 8: Native Method Argument Width (All native/*.cpp)

**Problem**: Every native method implementation receives `const u4* args` — the array of arguments from the interpreter. On 64-bit, register slots are `dreg_t` (8 bytes), but the function reads them as `u4` (4 bytes), getting only the low half of each argument.

**Fix**: Changed all ~28 native/*.cpp files: `const u4* args` → `const dreg_t* args`. Also updated `Object.h` (DalvikBridgeFunc, DalvikNativeFunc signatures) and `Native.h`.

### Fix 9: JNI Bridge Argument Width (Jni.cpp, Call.cpp)

**Problem**: `dvmCallJNIMethod()` builds a modified argument array for the JNI call. It allocates `u4* modArgs` and copies arguments. The JNI bridge then reads these via libffi, but libffi expects pointer-width slots for object arguments.

**Fix**: `dreg_t* modArgs` in Jni.cpp. `dvmPlatformInvoke()` in Call.cpp reads `const dreg_t* argv`.

### Fix 10: Volatile Object Field Read (ObjectInlines.h)

**Problem**: `dvmGetFieldObjectVolatile()` uses `android_atomic_acquire_load((int32_t*)ptr)` to read an object field with acquire semantics. On 64-bit, this reads only 32 bits of an 8-byte pointer.

**Fix**: Conditional compilation:
```cpp
#if __SIZEOF_POINTER__ > 4
    // 64-bit: full-width pointer read + explicit memory barrier
    Object* val = *ptr;
    ANDROID_MEMBAR_FULL();
    return val;
#else
    // 32-bit: original 32-bit atomic load
    return (Object*)android_atomic_acquire_load((int32_t*)ptr);
#endif
```

Same fix applied to `dvmGetStaticFieldObjectVolatile()`.

### Fix 11: IndirectRef Index Extraction (IndirectRefTable.h)

**Problem**: `(u4)iref` casts a pointer directly to u4, which on 64-bit is an error (pointer → 32-bit integer loses high bits). Some compilers may even optimize away the cast unpredictably.

**Fix**: Two-step cast: `(u4)(uintptr_t)iref`

Also fixed `indirectRefKind()`: `(uintptr_t)iref & 0x03` instead of `(u4)iref & 0x03`.

### Fix 12: String Field Offsets (UtfString.h)

**Problem**: Hardcoded field offsets for `java.lang.String`:
```c
#define STRING_FIELDOFF_VALUE    8   // 32-bit: Object(8) + 0
#define STRING_FIELDOFF_HASHCODE 12  // 32-bit: Object(8) + 4
#define STRING_FIELDOFF_OFFSET   16  // 32-bit: Object(8) + 8
#define STRING_FIELDOFF_COUNT    20  // 32-bit: Object(8) + 12
```
On 64-bit, `sizeof(Object) = 16`, so these are all wrong (should be 16, 20, 24, 28).

**Fix**: Uncommented `#define USE_GLOBAL_STRING_DEFS`, which makes the VM look up field offsets at runtime from the actual `java.lang.String` class instead of using hardcoded values.

### Fix 13: Array CLZ Bug (Array.cpp, Common.h)

**Problem**: Array element shift calculation:
```cpp
size_t elementShift = sizeof(size_t) * CHAR_BIT - 1 - CLZ(elemWidth);
```
`CLZ` uses `__builtin_clz()` which operates on `unsigned int` (32 bits). But `sizeof(size_t)` is 8 on 64-bit. So:
- For `elemWidth=2`: `CLZ(2) = 30` (correct for 32-bit domain)
- But: `8 * 8 - 1 - 30 = 33` (should be 1!)
- A shift of 33 means allocating `count << 33` bytes — ~1TB for a small array

**Fix**: Use `sizeof(unsigned int)` to match CLZ's domain:
```cpp
size_t elementShift = sizeof(unsigned int) * CHAR_BIT - 1 - CLZ(elemWidth);
// 4 * 8 - 1 - 30 = 1  ✓
```

Also added `CLZL` macro in Common.h for cases that genuinely need 64-bit CLZ:
```cpp
#if __SIZEOF_POINTER__ > 4
#define CLZL(x) __builtin_clzl(x)
#else
#define CLZL(x) __builtin_clz(x)
#endif
```

### Fix 14: AtomicCache Keys (AtomicCache.h/cpp)

**Problem**: `AtomicCacheEntry` has `u4 key1, key2` fields. These store class pointers for instanceof cache lookups. On 64-bit, pointers are 8 bytes → truncated to 4 bytes → cache misses or wrong matches.

**Fix**: Widened to `uintptr_t key1, key2`. Updated `ATOMIC_CACHE_LOOKUP` macro hash computation and `dvmUpdateAtomicCache()` signature.

### Fix 15: AtomicCache Alignment (AtomicCache.cpp)

**Problem**: Cache entry alignment calculation:
```cpp
newCache->entries = (AtomicCacheEntry*)
    (((int) newCache->entryAlloc + 63) & ~63);
```
`(int)` truncates the heap pointer from 8 to 4 bytes.

**Fix**: `(uintptr_t)` cast:
```cpp
newCache->entries = (AtomicCacheEntry*)
    (((uintptr_t) newCache->entryAlloc + 63) & ~63);
```

### Fix 16: Monitor List CAS (Sync.cpp)

**Problem**: Adding a monitor to the global list:
```cpp
android_atomic_release_cas(
    (int32_t)mon->next,
    (int32_t)mon,
    (int32_t*)(void*)&gDvm.monitorList);
```
All three casts truncate pointers to 32 bits.

**Fix**: Use GCC's pointer-width CAS:
```cpp
do {
    mon->next = gDvm.monitorList;
} while (!__sync_bool_compare_and_swap(&gDvm.monitorList, mon->next, mon));
```

### Fix 17: Object Header Size (Systemic)

**Problem**: `sizeof(Object) = 8` on 32-bit (4-byte ClassObject* + 4-byte u4 lock). On 64-bit, `sizeof(Object) = 16` (8-byte ClassObject* + 4-byte u4 lock + 4-byte padding for alignment).

This means every field offset that was calculated assuming `sizeof(Object) = 8` is wrong. This affects:
- String field offsets (Fix 12)
- DataObject.instanceData start (offset 16 vs 8)
- ArrayObject.contents start (offset 24 vs 12)
- Every hardcoded offset in the codebase

**Fix**: Use runtime-computed offsets wherever possible. The VM's field resolution logic (`dvmResolveField`, `dvmResolveInstField`) computes correct offsets using `sizeof()` at compile time, so resolved field accesses are correct. The main risk is hardcoded constants.

## Non-Fatal Workarounds

| Issue | Location | Fix | Reason |
|---|---|---|---|
| `dvmCreateInlineSubsTable` fails | Init.cpp | `ALOGW` warning instead of returning error | `NativeTestTarget` class not in our stripped boot JAR — test-only, not needed |
| `loadJniLibrary("javacore")` fails | Init.cpp | `ALOGW` warning instead of `dvmAbort()` | libjavacore.so not yet built from AOSP libcore |
| `loadJniLibrary("nativehelper")` fails | Init.cpp | Same as above | libnativehelper.so not yet built |

These workarounds let the VM boot and run bytecode that doesn't call native methods. Classes whose `<clinit>` calls JNI natives will still fail at runtime.

## Debugging History

This section documents the sequence of bugs encountered and fixed during the first successful boot.

### 1. "Non-absolute bootclasspath entry"
**Cause**: Used relative path `core-boot.jar` instead of absolute path.
**Fix**: `$(pwd)/core-boot.jar` or `-Xbootclasspath:$(pwd)/core-kitkat.jar`

### 2. "Could not stat dex cache directory '/data/dalvik-cache'"
**Cause**: VM looks for dalvik-cache at `$ANDROID_DATA/dalvik-cache`, defaults to `/data/dalvik-cache`.
**Fix**: `export ANDROID_DATA=/tmp/android-data; mkdir -p /tmp/android-data/dalvik-cache`

### 3. "execv '/system/bin/dexopt' failed"
**Cause**: VM spawns `$ANDROID_ROOT/bin/dexopt` to optimize DEX files. Defaults to `/system/bin/dexopt`.
**Fix**: `export ANDROID_ROOT=/tmp/android-root; ln -sf $(pwd)/build/dexopt /tmp/android-root/bin/dexopt`

### 4. "java.lang.Class has 0 static fields (expected 1)"
**Cause**: core-boot.jar has stub java.lang.Class with no fields.
**Fix**: Switched to `core-kitkat.jar` which has proper Class (1 sfield, 4 ifields).

### 5. "softLimit of 4095.999MB hit for 1099511627800-byte allocation" (~1TB)
**Cause**: CLZ bug. `sizeof(size_t)*CHAR_BIT-1-CLZ(2)` = `63-30` = 33 (should be 1).
**Fix**: Changed to `sizeof(unsigned int)*CHAR_BIT-1-CLZ(2)` = `31-30` = 1.

### 6. "String.value offset = 16; expected 8"
**Cause**: Object header is 16 bytes on 64-bit. Hardcoded STRING_FIELDOFF_VALUE=8 is wrong.
**Fix**: Enabled `USE_GLOBAL_STRING_DEFS` for runtime field offset lookup.

### 7. SIGSEGV in dvmInstanceofNonTrivial (AtomicCache key truncation)
**Cause**: AtomicCacheEntry keys are `u4`, truncating 64-bit class pointers. Cache returns wrong entries.
**Fix**: Widened keys to `uintptr_t`.

### 8. SIGSEGV in dvmInstanceofNonTrivial (AtomicCache alignment truncation)
**Cause**: Cache alignment used `(int)ptr` cast, truncating heap pointer.
**Fix**: Changed to `(uintptr_t)ptr`.

### 9. SIGSEGV in dvmFreeMonitorList (monitor CAS truncation)
**Cause**: Monitor list CAS operations used `(int32_t)` casts on monitor pointers.
**Fix**: Replaced with `__sync_bool_compare_and_swap`.

### 10. "dvmCreateInlineSubsTable failed"
**Cause**: Missing `NativeTestTarget` class in stripped boot JAR.
**Fix**: Made non-fatal (ALOGW).

### 11. "dlopen libjavacore.so failed"
**Cause**: libjavacore.so not yet built.
**Fix**: Made non-fatal (ALOGW). VM continues but native method calls will fail.

After all fixes: **VM boots, loads 280+ classes, executes class initializers, runs GC, and shuts down cleanly.**

## Full Build & Run Walkthrough

```bash
# ══════════════════════════════════════════
# Step 1: Prerequisites
# ══════════════════════════════════════════
# Ensure you have:
#   - g++ with C++11 support
#   - libz-dev (zlib headers + library)
#   - libffi-dev (libffi headers + library)
#   - Source trees at ~/dalvik-kitkat/ and ~/aosp-android-11/
# Verify:
g++ --version
dpkg -l | grep -E 'libz|libffi'

# ══════════════════════════════════════════
# Step 2: Build
# ══════════════════════════════════════════
cd ~/android-to-openharmony-migration/dalvik-port

# Full build (compiles 126 files, links, builds dalvikvm + dexopt)
make all

# Expected output:
#   Compiled: 126
#   Failed:   0
#   Total:    126
#   ═══ Linking libdvm.a ═══
#   Created: build/libdvm.a (XX MB)
#   ═══ Building dalvikvm launcher ═══
#   Created: build/dalvikvm
#   ═══ Building dexopt ═══
#   Created: build/dexopt

# Verify build output:
ls -lh build/dalvikvm build/dexopt build/libdvm.a

# ══════════════════════════════════════════
# Step 3: Set Up Boot Environment
# ══════════════════════════════════════════
export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin
ln -sf $(pwd)/build/dexopt $ANDROID_ROOT/bin/dexopt

# ══════════════════════════════════════════
# Step 4: Run the VM
# ══════════════════════════════════════════
# Boot test (just boot the VM with core classes, no user class)
./build/dalvikvm \
  -Xbootclasspath:$(pwd)/core-kitkat.jar \
  -Xverify:none -Xdexopt:none \
  -cp /dev/null NoClass 2>&1 | head -50

# Expected: VM creates, loads classes, then "class 'NoClass' not found"
# This proves the VM boots correctly!

# Run actual DEX bytecode (when you have a test.jar):
./build/dalvikvm \
  -Xbootclasspath:$(pwd)/core-kitkat.jar \
  -Xverify:none -Xdexopt:none \
  -classpath test.jar MainClass

# ══════════════════════════════════════════
# Step 5: Clean dalvik-cache (if needed)
# ══════════════════════════════════════════
# If you change the boot JAR, clear the cached optimized version:
rm -rf $ANDROID_DATA/dalvik-cache/*
```

## Remaining Work

### 1. Build libjavacore.so
Cross-compile AOSP KitKat `libcore/` native code for x86_64-linux. This provides JNI implementations for `java.io.*`, `java.net.*`, `java.lang.Math`, `java.lang.System.arraycopy()`, etc.

### 2. Create Minimal Test DEX
Need a `Hello.java` → `Hello.class` → `classes.dex` → `test.jar` that only uses bytecode operations (arithmetic, objects, arrays) without calling JNI native methods. Requires javac (JDK not currently installed).

### 3. Clean Up Debug Logging
Remove temporary debug logging added during development:
- Opcode tracing in FINISH macro
- INIT_RANGE logging
- Interpreter entry logging
- dvmInitClass logging

### 4. Full 64-bit Audit
Search for remaining pointer truncation patterns:
```
grep -rn '(int)\|int32_t)\|(u4)' vm/ | grep -i 'ptr\|obj\|ref\|method\|class'
```
Known risk areas:
- `Exception.cpp` stack trace storage (`int*` for pointer arrays)
- `Debugger.cpp` JDWP object ID handling
- Any remaining `android_atomic_*` with pointer-containing fields

### 5. Cross-compile for OpenHarmony
- Target: aarch64-linux-ohos (OHOS NDK toolchain)
- C library: musl libc (not bionic, not glibc)
- Need: libffi for aarch64, libz for aarch64
- Compat layer may need updates for musl-specific differences
