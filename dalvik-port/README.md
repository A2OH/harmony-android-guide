# Dalvik Portable VM — OHOS Port

Standalone Dalvik VM interpreter extracted from AOSP KitKat (android-4.4),
ported to 64-bit Linux and cross-compiled for OpenHarmony aarch64.

## What this is

A minimal Dalvik VM that can execute .dex files without any Android framework.
Uses the portable C interpreter (no assembly, no JIT). Boots ~4000 core
classes from `core-android-x86.jar`, runs bytecode, GC works correctly.

## Status

- **Linux x86_64**: Fully boots, runs Hello World, runs unmodified Android apps
- **OpenHarmony aarch64**: Fully boots and runs Hello World (static binary, tested via QEMU user-mode)
- **OpenHarmony ARM32**: Runs unmodified Android apps transparently via QEMU user-mode
- **1,968 Java shim classes**: 100% clean compile (0 errors), 2,422 .class files, covering android.* + dalvik.* + com.ohos.* types
- **Android app transparency**: Full Activity lifecycle (onCreate→onStart→onResume→onPause→onStop→onDestroy) driven by ActivityThread + Instrumentation, manifest parsing, Intent routing, Bundle passing — all transparent across x86_64, aarch64, and ARM32

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

## ARM32 OHOS Porting Notes

Key issues solved for running on OHOS ARM32 (QEMU system emulator):

- **OHOS musl `__musl_initialize` crash**: The constructor calls `CachedParameterCreate`
  which accesses the OHOS parameter service (unavailable in minimal environments).
  Fixed by removing `musl_preinit.o` and param objects from `libc.a`, providing
  stub implementations in `preinit_stubs.o`.
- **ARM EABI native call bridge**: The generic libffi path (`arch/generic/Call.cpp`)
  corrupts JNIEnv* argument on ARM32 OHOS. Replaced with the native ARM EABI
  assembly (`compat/CallEABI.S`) from AOSP, which directly manages the ARM calling
  convention and passes r0 (pEnv) straight through.
- **Raw syscall init binary**: PID 1 uses inline ARM EABI syscalls (no libc)
  to mount filesystems and exec dalvikvm. Uses `clone(SIGCHLD,0)` for fork and
  `wait4` instead of `waitpid` (not implemented on ARM Linux).
- **Missing `/etc/passwd`**: `getpwuid(0)` returns NULL without it, causing NPE
  in `System.initSystemProperties()`.

## Build

```bash
# Linux x86_64 (native)
make -j$(nproc)

# OpenHarmony aarch64 (cross-compile)
./build-ohos.sh          # Full: sysroot + zlib + libffi + compile + link

# Or step by step:
make TARGET=ohos -j$(nproc)

# OpenHarmony ARM32 (for QEMU system emulator)
make TARGET=ohos-arm32 -j$(nproc)
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
user.name = shell
1 + 1 = 2
Done!
```

### OpenHarmony ARM32 (via QEMU user-mode)

```bash
/tmp/qemu-arm-static ./build-ohos-arm32/dalvikvm \
  -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath hello.dex Hello

# Run unmodified Android app with full lifecycle:
/tmp/qemu-arm-static ./build-ohos-arm32/dalvikvm \
  -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath hello-android.dex com.example.hello.MainActivity
```

Android app output:
```
I/ActivityThread: Starting app: com.example.hello
I/MainActivity: onCreate called
I/MainActivity: Computation: 6 * 7 = 42
I/MainActivity: onStart called
I/MainActivity: onResume called
I/MainActivity: onPause called
I/MainActivity: onStop called
I/MainActivity: onDestroy called
I/ActivityThread: App finished
```

### QEMU System Emulator (ARM32 with OHOS Kernel)

Boots a full OHOS kernel with a minimal initramfs containing dalvikvm, dexopt,
core.jar, and hello.dex. Uses a raw-syscall init binary (no libc) as PID 1,
with a patched OHOS musl libc.a (parameter service constructor removed).

```bash
# Build initramfs (see /tmp/dalvik_init_raw.c for init source)
# Pack: init, bin/dalvikvm, bin/dexopt, data/core.jar, data/hello.dex,
#       system/bin/dexopt, etc/passwd
cd /tmp/dalvik-initramfs && find . | cpio -o -H newc | gzip > /tmp/dalvik-initramfs.cpio.gz

# Boot
qemu-system-arm -M virt -cpu cortex-a15 -m 256 \
  -kernel <ohos-zImage> -initrd /tmp/dalvik-initramfs.cpio.gz \
  -append "console=ttyAMA0 rdinit=/init" -nographic -no-reboot
```

## Creating DEX files

```bash
javac -source 1.8 -target 1.8 Hello.java
d8 Hello.class --output .    # produces classes.dex, rename to hello.dex
```

## Dependencies

- C++11 compiler (gcc/clang for native, OHOS LLVM for cross)
- libffi (for generic arch native calls; not used on ARM32 which has native asm)
- zlib (for JAR/ZIP reading)
- pthreads, libdl
- QEMU user-mode static (for testing aarch64 on x86_64 host)
- QEMU system-arm (for full OHOS kernel boot with ARM32)

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
│  ActivityThread + Instrumentation         │
│  - Manifest parsing, lifecycle mgmt      │
├──────────────────────────────────────────┤
│  Java Shim Layer (1,968 classes)         │
│  - android.*, dalvik.*, com.ohos.*       │
│  - Maps Android APIs → OHBridge          │
├──────────────────────────────────────────┤
│  musl libc (OpenHarmony)                 │
│  - Static linking, no Android deps       │
└──────────────────────────────────────────┘
```
