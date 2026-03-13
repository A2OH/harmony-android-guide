# Dalvik Portable VM — OHOS Port

Standalone Dalvik VM interpreter extracted from AOSP KitKat (android-4.4),
ported to 64-bit Linux and cross-compiled for OpenHarmony aarch64.

## What this is

A minimal Dalvik VM that can execute .dex files without any Android framework.
Uses the portable C interpreter (no assembly, no JIT). Boots ~4000 core
classes from `core-android-x86.jar`, runs bytecode, GC works correctly.

## Status

- **Linux x86_64**: Fully boots and runs Hello World
- **OpenHarmony aarch64**: Fully boots and runs Hello World (static binary, tested via QEMU user-mode)
- **912 Java shim classes**: Compile-compatible Android API stubs for migration

## Source

- Base: `aosp-mirror/platform_dalvik` tag `kitkat-release`
- Path: `/home/dspfac/dalvik-kitkat/vm/` (167K lines C++)
- Interpreter: `mterp/out/InterpC-portable.cpp` (4K lines, pure C)

## 64-bit Fixes (31 total)

Android never shipped a 64-bit Dalvik — they switched to ART. We did the
64-bit port ourselves:

- `dreg_t` (= `uintptr_t`) replaces `u4` register slots throughout the interpreter
- `RegType` widened from `u4` to `uintptr_t` in the bytecode verifier
- `InlineArgType` widened for 64-bit Object* in inline natives
- `Object.lock` widened from `u4` to `uintptr_t` for fat lock Monitor* storage
- `HeapBitmap` fixed: `1 << 63` UB → `1UL`, `CLZ()` → `CLZL()`, mask width fix
- GC null-clazz guard for uninitialized heap objects
- Pointer-width atomics in `cutils/atomic.h` compat layer
- Libcore bridge: native implementations for OsConstants, Posix, ICU, System

## Build

```bash
# Linux x86_64 (native)
make -j$(nproc)

# OpenHarmony aarch64 (cross-compile)
./build-ohos.sh          # Full: sysroot + zlib + libffi + compile + link

# Or step by step:
make TARGET=ohos -j$(nproc)
```

## Run

```bash
# Set up boot environment
export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin

# Linux x86_64
./build/dalvikvm -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath hello.dex Hello

# OpenHarmony aarch64 (via QEMU user-mode)
qemu-aarch64-static ./build-ohos-aarch64/dalvikvm \
  -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath hello.dex Hello
```

Output:
```
Hello from Dalvik on Linux!
os.arch = aarch64
os.name = Linux
java.vm.name = Dalvik
1 + 1 = 2
Done!
```

## Creating DEX files

```bash
javac -source 1.8 -target 1.8 Hello.java
d8 Hello.class --output .    # produces classes.dex, rename to hello.dex
```

## Dependencies

- C++11 compiler (gcc/clang for native, OHOS LLVM for cross)
- libffi (for generic arch native calls)
- zlib (for JAR/ZIP reading)
- pthreads, libdl
- QEMU user-mode static (for testing aarch64 on x86_64 host)

## Architecture

```
┌──────────────────────────────────────────┐
│  .dex bytecode (Hello.dex)               │
├──────────────────────────────────────────┤
│  Dalvik VM (portable C interpreter)      │
│  - InterpC-portable.cpp                  │
│  - 64-bit dreg_t registers               │
├──────────────────────────────────────────┤
│  Libcore bridge (compat/libcore_bridge)  │
│  - System, Posix, ICU natives            │
├──────────────────────────────────────────┤
│  musl libc (OpenHarmony)                 │
│  - Static linking, no Android deps       │
└──────────────────────────────────────────┘
```
