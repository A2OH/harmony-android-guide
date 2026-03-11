# Bionic C Library and NDK -- 代码审查 Report

## Android 11 (AOSP) -- Comprehensive Architecture and API Surface Review

---

## 1. Executive Summary

Bionic is Android's custom C library, math library, and dynamic linker. Unlike desktop Linux distributions that use glibc or musl, Bionic is purpose-built for Android's constraints: smaller memory footprint, BSD-licensed where possible, tight integration with the Android security model, and support for the NDK stable API surface. This review covers the architecture of bionic's libc, libm, the dynamic linker, threading, memory allocation, security features, and the NDK API surface as found in the Android 11 AOSP source tree at `~/aosp-android-11/bionic/`.

---

## 2. Overall Architecture

### 2.1 Component Structure

Bionic consists of five major components:

| 组件 | Output | 描述 |
|-----------|--------|-------------|
| `libc/` | `libc.so`, `libc.a` | Core C library (stdio, string, syscall wrappers) |
| `libm/` | `libm.so`, `libm.a` | Math library (sin, cos, sqrt, etc.) |
| `libdl/` | `libdl.so` | Dynamic linker interface stubs (dlopen, dlsym) |
| `libstdc++/` | `libstdc++.so` | Minimal C++ ABI support (__cxa_guard_acquire, etc.) |
| `linker/` | `/system/bin/linker`, `linker64` | Dynamic linker/loader |

Key source paths:
- `/home/dspfac/aosp-android-11/bionic/libc/bionic/` -- Core C library implementations
- `/home/dspfac/aosp-android-11/bionic/libc/include/` -- Public header files
- `/home/dspfac/aosp-android-11/bionic/libc/kernel/` -- Scrubbed kernel UAPI headers
- `/home/dspfac/aosp-android-11/bionic/libc/arch-arm/`, `arch-arm64/`, `arch-x86/`, `arch-x86_64/` -- Architecture-specific code
- `/home/dspfac/aosp-android-11/bionic/libc/upstream-freebsd/`, `upstream-netbsd/`, `upstream-openbsd/` -- BSD-origin code

### 2.2 Key Differences from glibc/musl

| 功能 | Bionic | glibc | musl |
|---------|--------|-------|------|
| License | BSD | LGPL | MIT |
| Size | ~1 MB | ~8 MB | ~1 MB |
| `locale` support | Minimal (C/C.UTF-8 only) | Full ICU | Limited |
| `pthread_mutex_t` size | 40 bytes (LP64) | 40 bytes | 40 bytes |
| DNS resolver | NetBSD-derived | glibc-native | Minimal |
| Allocator | Scudo (default) / jemalloc | ptmalloc2 | Simple bump |
| `FORTIFY_SOURCE` | Clang-based with `__pass_object_size` | GCC `__builtin_object_size` | Not supported |
| Symbol versioning | Supported (API 23+) | Full | Partial |
| System call mechanism | Auto-generated stubs from `SYSCALLS.TXT` | Hand-written | Hand-written |

Bionic deliberately omits many glibc-isms: no `LD_LIBRARY_PATH` for setuid binaries, no NSS, no `locale` infrastructure beyond basic C/UTF-8, and no backwards-compatible ABI cruft. This makes it smaller, faster to load, and more secure, but means NDK developers cannot rely on glibc-specific behavior.

---

## 3. Dynamic Linker (linker/linker64)

### 3.1 Architecture

The dynamic linker is a standalone executable (`/system/bin/linker` for 32-bit, `/system/bin/linker64` for 64-bit) specified in the ELF `DT_INTERP` header. Its entry point is `__linker_init()` in:

**Key file:** `/home/dspfac/aosp-android-11/bionic/linker/linker_main.cpp`

The boot sequence:
1. `__linker_init()` -- Initialize TLS, fix the linker's own relocations, call IFUNC resolvers
2. `__linker_init_post_relocation()` -- Initialize main thread, protect RELRO, call constructors
3. `linker_main()` -- Sanitize environment, initialize system properties, load executable and dependencies, resolve symbols, call constructors, return entry point

The linker self-bootstraps: it must relocate itself before it can use any global variables or call library functions. This is achieved by initially operating without GOT references, then linking itself as the first `soinfo` object.

### 3.2 Linker Namespaces

**Key file:** `/home/dspfac/aosp-android-11/bionic/linker/linker_namespaces.h`

Android 7.0+ introduced linker namespaces (`android_namespace_t`) to isolate library visibility. Each namespace has:
- `ld_library_paths_` -- Search paths for `dlopen`
- `default_library_paths_` -- Default search paths
- `permitted_paths_` -- Allowed paths for isolated namespaces
- `whitelisted_libs_` -- Explicitly allowed libraries
- `linked_namespaces_` -- Cross-namespace links with shared library allowlists

This is the mechanism that enforces the **private API restriction** (API 24+): apps cannot load non-NDK platform libraries. The linker creates isolated namespaces for apps and links them only to the public NDK namespace.

Configuration is loaded from `/system/etc/ld.config.txt` (or architecture-specific variants) or the generated `/linkerconfig/ld.config.txt`.

### 3.3 Library Loading (dlopen/dlsym)

**Key file:** `/home/dspfac/aosp-android-11/bionic/linker/dlfcn.cpp`

All `dlopen`/`dlsym`/`dlclose` calls are serialized behind a single recursive mutex (`g_dl_mutex`). The actual implementation functions (`__loader_dlopen`, `__loader_dlsym`, etc.) are exported with `__LINKER_PUBLIC__` visibility from the linker and patched into `libdl.so`'s stub table at runtime.

Key design decisions:
- `dlopen` uses caller address to determine which namespace to search (via `caller_addr` parameter)
- Symbol lookup uses GNU hash tables (API 23+) for faster resolution
- Breadth-first dependency loading (API 22+, fixing earlier depth-first bugs)
- `RTLD_LOCAL` properly implemented from API 23
- Libraries can be loaded directly from APK/ZIP files (API 23+) if page-aligned and uncompressed

### 3.4 Greylist for Backwards Compatibility

**Key file:** `/home/dspfac/aosp-android-11/bionic/linker/linker.cpp` (line ~192)

A hardcoded greylist allows certain private platform libraries to be loaded by apps targeting API < 24:
```
libandroid_runtime.so, libbinder.so, libcrypto.so, libcutils.so,
libexpat.so, libgui.so, libmedia.so, ...
```

This is a transitional mechanism; apps should migrate to NDK-only APIs.

### 3.5 Impact on App Developers

- **Private API enforcement**: Native code must use only NDK-stable libraries. Linking against internal platform libraries will fail at load time on API 24+.
- **Namespace isolation**: `System.loadLibrary()` automatically sets up the correct namespace. Direct `dlopen` calls from JNI should use relative paths.
- **Library from APK**: Set `android:extractNativeLibs="false"` to load `.so` files directly from the APK without extraction, saving disk space.
- **SONAME requirements**: Libraries must have a proper SONAME (API 23+) and no text relocations (API 23+).

---

## 4. System Call Wrappers

### 4.1 Auto-Generated Stubs

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/SYSCALLS.TXT`

Bionic generates system call stubs automatically from a declarative file (`SYSCALLS.TXT`). Each line specifies:
```
return_type func_name[:syscall_name]([parameters]) arch_list
```

For example:
```
uid_t   getuid:getuid32()         lp32
uid_t   getuid:getuid()           lp64
```

The `gensyscalls.py` script generates architecture-specific assembly stubs into `arch-{arm,arm64,x86,x86_64}/syscalls/`. This approach ensures consistency and makes adding new syscalls mechanical.

### 4.2 VDSO Integration

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/vdso.cpp`

Bionic uses the kernel's Virtual Dynamic Shared Object (VDSO) to avoid system call overhead for frequently-used functions:
- `clock_gettime()` -- Falls back to `__clock_gettime` syscall if VDSO unavailable
- `clock_getres()`
- `gettimeofday()`
- `time()`

The VDSO function pointers are resolved during `__libc_init_vdso()` by walking the VDSO's ELF symbol table. This is transparent to app developers but provides measurable performance improvements for time-related calls.

### 4.3 Impact on App Developers

- System calls are architecture-transparent; NDK code uses standard POSIX wrappers.
- Not all Linux syscalls have libc wrappers. For unusual syscalls, use `syscall(3)`.
- Certain syscalls are blocked by seccomp filters on app processes (see Section 8).

---

## 5. Thread Implementation (pthread)

### 5.1 Thread Creation

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp`

Thread creation in Bionic follows this sequence:

1. **Allocate thread mapping**: A single `mmap` call allocates stack + guard page + static TLS + trailing guard page (`__allocate_thread_mapping`). The entire region is initially `PROT_NONE`, then the writable portion is `mprotect`'d to `PROT_READ|PROT_WRITE`.

2. **Initialize TLS**: The `bionic_tcb` (Thread Control Block) and `bionic_tls` structures are placed within the static TLS region. Stack guard canaries are copied to TLS.

3. **Clone**: The thread is created via the `clone()` syscall with flags: `CLONE_VM | CLONE_FS | CLONE_FILES | CLONE_SIGHAND | CLONE_THREAD | CLONE_SYSVSEM | CLONE_SETTLS | CLONE_PARENT_SETTID | CLONE_CHILD_CLEARTID`.

4. **Startup handshake**: The new thread waits on `startup_handshake_lock` until the parent has registered it with the debugger and returned `pthread_t`.

5. **Signal stack**: Each thread gets a dedicated alternate signal stack (`sigaltstack`) and, on AArch64, a Shadow Call Stack (SCS).

### 5.2 Mutex Implementation

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_mutex.cpp`

Mutexes are implemented using Linux futexes. The mutex state is packed into atomic integers:
- Bits 0-3: type (normal, recursive, errorcheck)
- Bit 4: process-shared flag
- Bit 5: priority inheritance protocol

The implementation uses futex operations directly rather than any higher-level kernel synchronization.

### 5.3 Thread-Local Storage (TLS)

**Key files:**
- `/home/dspfac/aosp-android-11/bionic/libc/bionic/bionic_elf_tls.cpp`
- `/home/dspfac/aosp-android-11/bionic/libc/private/bionic_tls.h`

Bionic supports ELF TLS (Thread-Local Storage) with both static and dynamic TLS models. A generation counter tracks when new TLS modules are loaded (via `dlopen`), and `__tls_get_addr` uses a fast path when the generation hasn't changed. TLS slot allocation includes reserved slots for: thread self-pointer, stack guard, bionic TLS structure, and errno.

### 5.4 Impact on App Developers

- `pthread_create` uses `clone()` directly, not `fork+exec`, making thread creation efficient.
- Default stack size is 1 MB (adjustable via `pthread_attr_setstacksize`).
- `pthread_gettid_np()` is an Android extension that returns the Linux thread ID (useful for logging).
- `pthread_setname_np()` works on Android and sets the `/proc/[pid]/task/[tid]/comm` name.
- Priority inheritance mutexes are available via `PTHREAD_PRIO_INHERIT`.

---

## 6. Memory Allocation

### 6.1 Allocator Architecture

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_common.cpp`

Bionic uses a dispatch-table architecture for memory allocation. All standard functions (`malloc`, `free`, `calloc`, `realloc`, etc.) check a `dispatch_table` pointer:
- If non-null, calls are forwarded to a debug/interceptor allocator
- Otherwise, calls go to the default allocator via the `Malloc()` macro

### 6.2 Default Allocator: Scudo vs jemalloc

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/Android.bp` (lines 94-120)

Android 11 uses **Scudo** as the default allocator on non-svelte (non-low-memory) devices, falling back to **jemalloc5** on svelte devices:

```
libc_scudo_product_variables = {
    malloc_not_svelte: {
        cflags: ["-DUSE_SCUDO"],
        whole_static_libs: ["libscudo"],
        exclude_static_libs: ["libjemalloc5", "libc_jemalloc_wrapper"],
    },
}
```

Scudo is a hardened allocator designed to detect and mitigate heap-based vulnerabilities. It provides:
- Chunk headers with checksums to detect corruption
- Quarantine for recently freed memory
- Randomized allocation patterns
- Guard pages between size classes

### 6.3 GWP-ASan (Guarded With Probability - ASan)

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/gwp_asan_wrappers.cpp`

GWP-ASan is a sampling allocator integrated into bionic that probabilistically places allocations in guarded memory. Configuration:
- `MaxSimultaneousAllocations`: 32
- `SampleRate`: 2500 (1 in 2500 allocations are guarded)
- Catches use-after-free and buffer overflows in production at minimal performance cost

### 6.4 Tagged Pointers (Heap Pointer Tagging)

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_tagged_pointers.h`

On AArch64, bionic uses ARM's Top Byte Ignore (TBI) feature to tag heap pointers with a fixed tag (`0xB4`). The `MaybeTagPointer()` function sets the top byte on every allocation, and `MaybeUntagAndCheckPointer()` validates and strips it on deallocation. This catches:
- Use-after-free (freed memory has tag stripped)
- Pointer truncation bugs (code that masks off the top byte)

The tag `0xB4` was chosen to be: identifiable by developers, different from pattern-init (`0xAA`), always pointing to inaccessible memory if dereferenced as a raw address, and having a bitset-mirror nibble pattern.

### 6.5 Heap Tagging Levels

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/heap_tagging.cpp`

Three levels are supported:
- `M_HEAP_TAGGING_LEVEL_NONE` -- No tagging
- `M_HEAP_TAGGING_LEVEL_TBI` -- Top-Byte-Ignore pointer tagging
- `M_HEAP_TAGGING_LEVEL_ASYNC` -- ARM MTE (Memory Tagging Extension) asynchronous mode (experimental in Android 11)

### 6.6 Impact on App Developers

- NDK code should use standard `malloc`/`free`; the allocator is transparent.
- `malloc_usable_size()` returns the usable size of an allocation, which may be larger than requested.
- Debug malloc can be enabled via `libc.debug.malloc.options` system property.
- On AArch64 devices, heap pointers will have non-zero top bytes. Code that assumes pointers fit in fewer bits will break.
- GWP-ASan crash reports appear in tombstones and provide allocation/deallocation stack traces.

---

## 7. NDK Stable API Surface

### 7.1 API Versioning Mechanism

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/include/android/versioning.h`

The `__INTRODUCED_IN(api_level)` macro annotates every NDK API with the minimum API level at which it became available:
```c
#define __INTRODUCED_IN(api_level) __attribute__((annotate("introduced_in=" #api_level)))
```

The NDK build system uses these annotations to generate weak symbol references, ensuring that apps targeting older API levels get link-time errors rather than runtime crashes.

### 7.2 Symbol Map

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/libc.map.txt`

The `libc.map.txt` file defines every exported symbol in `libc.so`, organized by version block (`LIBC`, `LIBC_N`, `LIBC_O`, etc.). Each symbol optionally has:
- Architecture restrictions (`# arm`, `# x86`, etc.)
- API level introduction (`# introduced=21`)
- Architecture-specific introduction (`# introduced-arm=17`)

This file controls the NDK stable API surface. Symbols not listed here are not available to NDK apps.

### 7.3 Public Headers

**Key directory:** `/home/dspfac/aosp-android-11/bionic/libc/include/`

The public headers include standard POSIX headers (`stdio.h`, `stdlib.h`, `pthread.h`, etc.) plus Android-specific extensions:
- `<android/fdsan.h>` -- File descriptor sanitizer
- `<android/set_abort_message.h>` -- Set abort message for crash reporting
- `<android/versioning.h>` -- API level macros
- `<dlfcn.h>` -- Dynamic linking (with `dlvsym` from API 24)

### 7.4 dlopen/dlsym API

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/include/dlfcn.h`

The dynamic linking API provides:
- `dlopen()`, `dlclose()`, `dlsym()`, `dlerror()`, `dladdr()`
- `dlvsym()` (API 24+) -- Symbol lookup with version
- Android extensions: `android_dlopen_ext()` with `android_dlextinfo` for namespace-aware loading

Notable: LP32 (32-bit) has different `RTLD_NOW`/`RTLD_GLOBAL` values than LP64 for historical compatibility:
```c
#if !defined(__LP64__)
#undef RTLD_NOW
#define RTLD_NOW      0x00000
#undef RTLD_GLOBAL
#define RTLD_GLOBAL   0x00002
#endif
```

### 7.5 Impact on App Developers

- Always set `minSdkVersion` correctly; APIs guarded by `__INTRODUCED_IN` will fail to link if unavailable.
- Use `__builtin_available()` or weak symbol checks for runtime feature detection.
- The NDK sysroot (`platforms/android-{N}/usr/lib/`) defines exactly which symbols are available at each API level.
- Private symbols (not in `libc.map.txt`) are not part of the stable API and may change or disappear.

---

## 8. Security Features

### 8.1 ASLR (Address Space Layout Randomization)

The linker enforces PIE (Position-Independent Executable) for all executables since Lollipop (API 21). Non-PIE executables are rejected at `linker_main.cpp` line 401:
```cpp
if (elf_hdr->e_type != ET_DYN) {
    // error: Android 5.0 and later only support position-independent executables (-fPIE)
}
```

Library load addresses are randomized by the kernel's mmap randomization.

### 8.2 Stack Canaries

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/__stack_chk_fail.cpp`

Stack canaries (`-fstack-protector`) are enabled by default. The canary value is stored in TLS (`TLS_SLOT_STACK_GUARD`) and initialized from `__stack_chk_guard` global. On corruption, `__stack_chk_fail()` calls `async_safe_fatal("stack corruption detected (-fstack-protector)")`.

### 8.3 FORTIFY_SOURCE

**Key files:** `/home/dspfac/aosp-android-11/bionic/libc/include/bits/fortify/string.h` and sibling headers

Bionic implements `FORTIFY_SOURCE` using Clang-specific features:
- `__pass_object_size` attribute for compile-time buffer size tracking
- `__builtin___memcpy_chk`, `__builtin___strcpy_chk`, etc. for runtime bounds checking
- `__clang_error_if` for compile-time error diagnostics

FORTIFY is applied to: `string.h` (memcpy, strcpy, strcat, strlen, etc.), `stdio.h` (fgets, sprintf, snprintf), `unistd.h` (read, write, readlink), `stdlib.h` (realpath), `poll.h`, `fcntl.h`, `sys/socket.h`.

This is more advanced than glibc's FORTIFY because it leverages Clang's compiler support for `__builtin_object_size` with the `__pass_object_size` attribute, catching more bugs at compile time.

### 8.4 Shadow Call Stack (SCS)

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp` (lines 109-133)

On AArch64, each thread gets a Shadow Call Stack -- a separate stack that stores only return addresses, using register `x18` as the SCS pointer. The SCS is placed at a random offset within a large guard region (`SCS_GUARD_REGION_SIZE`). This protects against ROP (Return-Oriented Programming) attacks by keeping return addresses in a location that cannot be overwritten by stack buffer overflows.

### 8.5 Control Flow Integrity (CFI)

**Key file:** `/home/dspfac/aosp-android-11/bionic/linker/linker_cfi.h`

The linker maintains a CFI shadow map that tracks `__cfi_check` functions for each loaded DSO. When a CFI-enabled library is loaded, the shadow is updated to map code addresses to their CFI validation functions. This enables cross-DSO CFI checks.

### 8.6 RELRO (RELocation Read-Only)

The linker protects relocation data after resolving symbols by marking it read-only (`protect_relro()`), preventing attackers from overwriting GOT entries.

### 8.7 Seccomp-BPF Filtering

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/seccomp/seccomp_policy.cpp`

Bionic installs seccomp-BPF filters that restrict which system calls app processes can make. Three filter levels exist:
- `APP` -- Regular app processes (most restrictive)
- `APP_ZYGOTE` -- App zygote processes
- `SYSTEM` -- System processes (least restrictive)

Filters support dual-architecture mode (64-bit primary + 32-bit secondary) and validate both architecture and syscall number. Disallowed syscalls trigger `SECCOMP_RET_TRAP` (SIGSYS).

The whitelist files define which syscalls are permitted:
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_APP.TXT` -- Additional syscalls for app compatibility
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_COMMON.TXT` -- Common whitelist
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_BLACKLIST_APP.TXT` -- Explicitly blocked syscalls

### 8.8 File Descriptor Sanitizer (fdsan)

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/bionic/fdsan.cpp`

fdsan tracks file descriptor ownership using tagged close operations. Each fd has an associated 64-bit owner tag encoding the owner type (FILE*, DIR*, unique_fd, FileInputStream, ParcelFileDescriptor, SQLite, etc.) and an identifier. Common bugs detected:
- Double-close: closing an fd that was already closed
- Ownership violation: closing an fd owned by a different object (e.g., closing a FileInputStream's fd from native code)

In Android 11, fdsan defaults to `ANDROID_FDSAN_ERROR_LEVEL_FATAL`, making fd misuse a fatal error. The `close()` function itself routes through `android_fdsan_close_with_tag()`.

### 8.9 Impact on App Developers

- **PIE is mandatory**: All native executables must be position-independent.
- **FORTIFY catches bugs at compile time**: Using NDK r21+ with default flags enables FORTIFY automatically.
- **fdsan errors are fatal**: Code that double-closes fds or violates ownership will crash. Use RAII wrappers like `unique_fd`.
- **Seccomp restrictions**: Certain system calls (especially 32-bit legacy calls) are blocked for app processes. Use standard libc wrappers rather than raw `syscall()`.
- **Tagged heap pointers**: On AArch64, pointers returned by `malloc` have non-zero top bytes. Pointer arithmetic that strips the top byte will crash.

---

## 9. Native Library Loading Mechanism

### 9.1 Java to Native Bridge

When an Android app calls `System.loadLibrary("foo")`, the following chain executes:
1. **Java layer**: `Runtime.loadLibrary0()` resolves the library path using the ClassLoader's native library directory
2. **Native bridge**: `android_dlopen_ext()` is called with the appropriate `android_namespace_t`
3. **Linker**: `do_dlopen()` loads the ELF file, resolves dependencies, applies relocations, calls constructors
4. **JNI registration**: `JNI_OnLoad()` is called if present, allowing the library to register native methods

### 9.2 Library Search Order

Since API 23, the linker divides libraries into:
- **Global group**: main executable, `LD_PRELOAD`, libraries with `DF_1_GLOBAL`
- **Local group**: breadth-first transitive closure of the library's `DT_NEEDED` entries

Search order: global group first, then local group. This allows tools like ASAN to intercept any symbol.

### 9.3 Loading from APK

Since API 23, `.so` files can be loaded directly from APKs/ZIPs:
```
dlopen("my_app.apk!/lib/arm64-v8a/libfoo.so", RTLD_NOW)
```
Requirements: page-aligned (4096-byte boundary) and stored uncompressed.

### 9.4 Impact on App Developers

- Use `System.loadLibrary()` rather than `System.load()` when possible -- it handles architecture directories automatically.
- For complex dependency graphs, consider linking everything into a single `.so` to avoid load-order issues.
- The linker logs can be enabled for debugging: `adb shell setprop debug.ld.app.com.example.myapp dlerror,dlopen,dlsym`

---

## 10. JNI Interface and Java-Native Bridge

### 10.1 JNI from Bionic's Perspective

Bionic itself does not implement JNI -- that is the ART runtime's responsibility. However, bionic provides the infrastructure that JNI relies on:

- **Thread model**: JNI's `AttachCurrentThread`/`DetachCurrentThread` depend on bionic's pthread implementation
- **Dynamic linking**: `System.loadLibrary` uses bionic's linker with namespace isolation
- **Signal handling**: Bionic's signal infrastructure coexists with ART's signal handlers for null pointer checks and stack overflow detection
- **TLS**: JNI thread-local data uses bionic's TLS slot mechanism

### 10.2 `__BIONIC_WEAK_FOR_NATIVE_BRIDGE`

Functions like `pthread_create` are marked with `__BIONIC_WEAK_FOR_NATIVE_BRIDGE`, allowing the native bridge (used for running ARM apps on x86 via translation) to intercept and wrap these calls.

---

## 11. libm -- Math Library

### 11.1 Architecture

**Key directory:** `/home/dspfac/aosp-android-11/bionic/libm/`

libm is organized by architecture with upstream code from FreeBSD and NetBSD:
- `upstream-freebsd/` -- Most math functions (sin, cos, exp, log, etc.)
- `upstream-netbsd/` -- Some specialized functions
- `arm64/`, `amd64/`, `arm/`, `x86/`, `x86_64/` -- Architecture-optimized implementations
- `fake_long_double.c` -- On architectures where `long double` equals `double`, provides wrappers

### 11.2 Impact on App Developers

- `libm.so` is automatically linked when using the NDK.
- `long double` behavior differs by architecture: it is 64-bit on ARM and 80-bit on x86.
- All standard C99/C11 math functions are available.

---

## 12. Notable Android-Specific APIs and Extensions

### 12.1 Android-Only APIs in libc

| API | Header | 描述 |
|-----|--------|-------------|
| `android_fdsan_*` | `<android/fdsan.h>` | File descriptor ownership tracking |
| `android_set_abort_message()` | `<android/set_abort_message.h>` | Set message for crash reports |
| `android_mallopt()` | Platform-internal | Control allocator behavior |
| `pthread_gettid_np()` | `<pthread.h>` | Get Linux thread ID |
| `__system_property_get()` | `<sys/system_properties.h>` | Read system properties |
| `android_dlopen_ext()` | `<android/dlext.h>` | Extended dlopen with namespace support |

### 12.2 Missing POSIX Features

Bionic deliberately omits or stubs certain POSIX features:
- No `locale` support beyond C/C.UTF-8
- No user/group database functions (`getpwnam_r` works but only for the current user)
- No `wordexp()`
- Limited `regex` support
- No `aio_*` (POSIX asynchronous I/O)

---

## 13. Build System and Configuration

### 13.1 Build Configuration

**Key file:** `/home/dspfac/aosp-android-11/bionic/libc/Android.bp`

The build uses the Soong build system with these notable configuration choices:
- `-D_LIBC=1` -- Internal bionic builds
- `-Werror=pointer-to-int-cast`, `-Werror=int-to-pointer-cast` -- Catch 32/64-bit porting bugs
- `-Wframe-larger-than=2048` -- Prevent large stack frames
- `-fno-emulated-tls` -- Required for GWP-ASan
- MTE support via `experimental_mte` product variable

### 13.2 Allocator Selection

The default allocator is selected at build time:
- **Non-svelte devices**: Scudo (security-hardened)
- **Svelte (low-memory) devices**: jemalloc5 (memory-efficient)

The `libc_jemalloc_wrapper` library provides functions not directly implemented by jemalloc.

---

## 14. Key Concerns and Recommendations for App Developers

### 14.1 Common Pitfalls

1. **Pointer tagging on AArch64**: Do not assume pointers use only 48 bits. Heap pointers have `0xB4` in the top byte.
2. **fdsan crashes**: Ensure all file descriptors are closed exactly once and through the correct owner.
3. **Private API access**: Do not `dlopen` platform-internal libraries. Use NDK APIs only.
4. **32-bit legacy syscalls**: Some 32-bit syscalls are blocked by seccomp. Use standard libc wrappers.
5. **Thread stack size**: Default is 1 MB, which may be insufficient for deeply recursive native code.
6. **`locale` limitations**: Do not rely on locale-dependent behavior in native code.

### 14.2 Debugging and Diagnostics

- Enable linker debug logging: `adb shell setprop debug.ld.app.<package> dlerror,dlopen,dlsym`
- Enable malloc debugging: `adb shell setprop libc.debug.malloc.options "backtrace guard"`
- Use fdsan: fatal by default on API 30+; check logcat for ownership violation messages
- GWP-ASan crash reports include allocation and deallocation stack traces in tombstones

### 14.3 Performance Considerations

- VDSO eliminates syscall overhead for time-related functions
- Scudo is slightly slower than jemalloc but provides security guarantees
- GNU hash tables (API 23+) significantly speed up symbol resolution
- Loading libraries from APK avoids extraction overhead

---

## 15. File Reference Index

| Area | Key File(s) |
|------|-------------|
| Bionic overview | `/home/dspfac/aosp-android-11/bionic/README.md` |
| NDK changes guide | `/home/dspfac/aosp-android-11/bionic/android-changes-for-ndk-developers.md` |
| Linker entry point | `/home/dspfac/aosp-android-11/bionic/linker/linker_main.cpp` |
| Linker core logic | `/home/dspfac/aosp-android-11/bionic/linker/linker.cpp` |
| dlopen/dlsym impl | `/home/dspfac/aosp-android-11/bionic/linker/dlfcn.cpp` |
| Linker namespaces | `/home/dspfac/aosp-android-11/bionic/linker/linker_namespaces.h` |
| soinfo structure | `/home/dspfac/aosp-android-11/bionic/linker/linker_soinfo.h` |
| CFI shadow | `/home/dspfac/aosp-android-11/bionic/linker/linker_cfi.h` |
| Syscall definitions | `/home/dspfac/aosp-android-11/bionic/libc/SYSCALLS.TXT` |
| Thread creation | `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp` |
| Mutex implementation | `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_mutex.cpp` |
| ELF TLS | `/home/dspfac/aosp-android-11/bionic/libc/bionic/bionic_elf_tls.cpp` |
| Malloc dispatch | `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_common.cpp` |
| GWP-ASan | `/home/dspfac/aosp-android-11/bionic/libc/bionic/gwp_asan_wrappers.cpp` |
| Tagged pointers | `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_tagged_pointers.h` |
| Heap tagging | `/home/dspfac/aosp-android-11/bionic/libc/bionic/heap_tagging.cpp` |
| VDSO integration | `/home/dspfac/aosp-android-11/bionic/libc/bionic/vdso.cpp` |
| Stack canary | `/home/dspfac/aosp-android-11/bionic/libc/bionic/__stack_chk_fail.cpp` |
| FORTIFY string.h | `/home/dspfac/aosp-android-11/bionic/libc/include/bits/fortify/string.h` |
| fdsan | `/home/dspfac/aosp-android-11/bionic/libc/bionic/fdsan.cpp` |
| Seccomp policy | `/home/dspfac/aosp-android-11/bionic/libc/seccomp/seccomp_policy.cpp` |
| Seccomp app whitelist | `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_APP.TXT` |
| API versioning | `/home/dspfac/aosp-android-11/bionic/libc/include/android/versioning.h` |
| Symbol map | `/home/dspfac/aosp-android-11/bionic/libc/libc.map.txt` |
| dlfcn header | `/home/dspfac/aosp-android-11/bionic/libc/include/dlfcn.h` |
| Build configuration | `/home/dspfac/aosp-android-11/bionic/libc/Android.bp` |

---

*Report generated from Android 11 AOSP source code review.*
