# Dalvik Portable VM — OHOS Port

Standalone Dalvik VM interpreter extracted from AOSP KitKat (android-4.4),
configured to run on any Linux system (including OpenHarmony).

## What this is

A minimal Dalvik VM that can execute .dex files without any Android framework.
Uses the portable C interpreter (no assembly, no JIT).

## Source

- Base: `aosp-mirror/platform_dalvik` tag `kitkat-release`
- Path: `/home/dspfac/dalvik-kitkat/vm/` (167K lines C++)
- Interpreter: `mterp/out/InterpC-portable.cpp` (4K lines, pure C)

## Porting approach

1. Stub Android-specific headers (`cutils/log.h`, `cutils/atomic.h`, `cutils/ashmem.h`)
2. Use `os/linux.cpp` (generic, no Android deps)
3. Use `arch/generic/` (FFI-based native call bridge)
4. Disable: JIT, JDWP, Zygote, hprof, copying GC
5. Build with CMake (not Android.mk)

## Build

```bash
mkdir build && cd build
cmake .. -DDALVIK_SRC=/home/dspfac/dalvik-kitkat
make -j$(nproc)
```

## Dependencies

- C++11 compiler (gcc/clang)
- libffi (for generic arch native calls)
- zlib (for JAR/ZIP reading)
- pthreads
- libdl
