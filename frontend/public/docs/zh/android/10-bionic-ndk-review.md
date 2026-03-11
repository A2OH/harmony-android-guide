# Bionic C 库和 NDK -- 代码审查报告

## Android 11 (AOSP) -- 综合架构和 API 接口审查

---

## 1. 概要

Bionic 是 Android 的自定义 C 库、数学库和动态链接器。与使用 glibc 或 musl 的桌面 Linux 发行版不同，Bionic 是专为 Android 的约束条件而构建的：更小的内存占用、尽可能使用 BSD 许可证、与 Android 安全模型紧密集成，以及支持 NDK 稳定 API 接口。本审查涵盖 bionic 的 libc 架构、libm、动态链接器、线程、内存分配、安全特性以及在 Android 11 AOSP 源码树 `~/aosp-android-11/bionic/` 中找到的 NDK API 接口。

---

## 2. 总体架构

### 2.1 组件结构

Bionic 由五个主要组件组成：

| 组件 | 输出 | 描述 |
|-----------|--------|-------------|
| `libc/` | `libc.so`, `libc.a` | 核心 C 库（stdio、string、系统调用包装器） |
| `libm/` | `libm.so`, `libm.a` | 数学库（sin、cos、sqrt 等） |
| `libdl/` | `libdl.so` | 动态链接器接口桩（dlopen、dlsym） |
| `libstdc++/` | `libstdc++.so` | 最小 C++ ABI 支持（__cxa_guard_acquire 等） |
| `linker/` | `/system/bin/linker`, `linker64` | 动态链接器/加载器 |

关键源码路径：
- `/home/dspfac/aosp-android-11/bionic/libc/bionic/` -- 核心 C 库实现
- `/home/dspfac/aosp-android-11/bionic/libc/include/` -- 公共头文件
- `/home/dspfac/aosp-android-11/bionic/libc/kernel/` -- 经过清理的内核 UAPI 头文件
- `/home/dspfac/aosp-android-11/bionic/libc/arch-arm/`, `arch-arm64/`, `arch-x86/`, `arch-x86_64/` -- 架构特定代码
- `/home/dspfac/aosp-android-11/bionic/libc/upstream-freebsd/`, `upstream-netbsd/`, `upstream-openbsd/` -- 来自 BSD 的代码

### 2.2 与 glibc/musl 的主要区别

| 特性 | Bionic | glibc | musl |
|---------|--------|-------|------|
| 许可证 | BSD | LGPL | MIT |
| 大小 | ~1 MB | ~8 MB | ~1 MB |
| `locale` 支持 | 最小（仅 C/C.UTF-8） | 完整 ICU | 有限 |
| `pthread_mutex_t` 大小 | 40 字节 (LP64) | 40 字节 | 40 字节 |
| DNS 解析器 | 源自 NetBSD | glibc 原生 | 最小 |
| 分配器 | Scudo（默认）/ jemalloc | ptmalloc2 | 简单递增 |
| `FORTIFY_SOURCE` | 基于 Clang 的 `__pass_object_size` | GCC `__builtin_object_size` | 不支持 |
| 符号版本控制 | 支持（API 23+） | 完整 | 部分 |
| 系统调用机制 | 从 `SYSCALLS.TXT` 自动生成桩 | 手写 | 手写 |

Bionic 有意省略了许多 glibc 特性：setuid 二进制文件不使用 `LD_LIBRARY_PATH`、没有 NSS、没有超出基本 C/UTF-8 的 `locale` 基础设施，也没有向后兼容的 ABI 历史包袱。这使其更小、加载更快、更安全，但意味着 NDK 开发者不能依赖 glibc 特定行为。

---

## 3. 动态链接器（linker/linker64）

### 3.1 架构

动态链接器是一个独立的可执行文件（32 位为 `/system/bin/linker`，64 位为 `/system/bin/linker64`），在 ELF `DT_INTERP` 头中指定。其入口点是以下文件中的 `__linker_init()`：

**关键文件：** `/home/dspfac/aosp-android-11/bionic/linker/linker_main.cpp`

启动序列：
1. `__linker_init()` -- 初始化 TLS，修复链接器自身的重定位，调用 IFUNC 解析器
2. `__linker_init_post_relocation()` -- 初始化主线程，保护 RELRO，调用构造函数
3. `linker_main()` -- 清理环境变量，初始化系统属性，加载可执行文件和依赖项，解析符号，调用构造函数，返回入口点

链接器自举：它必须在能够使用任何全局变量或调用库函数之前重定位自身。这是通过最初不使用 GOT 引用来实现的，然后将自身作为第一个 `soinfo` 对象进行链接。

### 3.2 链接器命名空间

**关键文件：** `/home/dspfac/aosp-android-11/bionic/linker/linker_namespaces.h`

Android 7.0+ 引入了链接器命名空间（`android_namespace_t`）来隔离库可见性。每个命名空间包含：
- `ld_library_paths_` -- `dlopen` 的搜索路径
- `default_library_paths_` -- 默认搜索路径
- `permitted_paths_` -- 隔离命名空间的允许路径
- `whitelisted_libs_` -- 明确允许的库
- `linked_namespaces_` -- 跨命名空间链接及共享库允许列表

这是实施**私有 API 限制**（API 24+）的机制：应用无法加载非 NDK 平台库。链接器为应用创建隔离命名空间，仅将它们链接到公共 NDK 命名空间。

配置从 `/system/etc/ld.config.txt`（或架构特定变体）或生成的 `/linkerconfig/ld.config.txt` 加载。

### 3.3 库加载（dlopen/dlsym）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/linker/dlfcn.cpp`

所有 `dlopen`/`dlsym`/`dlclose` 调用都在单个递归互斥锁（`g_dl_mutex`）后面串行化。实际实现函数（`__loader_dlopen`、`__loader_dlsym` 等）从链接器以 `__LINKER_PUBLIC__` 可见性导出，并在运行时修补到 `libdl.so` 的桩表中。

关键设计决策：
- `dlopen` 使用调用者地址来确定搜索哪个命名空间（通过 `caller_addr` 参数）
- 符号查找使用 GNU 哈希表（API 23+）以加快解析速度
- 广度优先依赖加载（API 22+，修复了早期深度优先的错误）
- `RTLD_LOCAL` 从 API 23 起正确实现
- 库可以直接从 APK/ZIP 文件加载（API 23+），前提是页对齐且未压缩

### 3.4 灰名单向后兼容

**关键文件：** `/home/dspfac/aosp-android-11/bionic/linker/linker.cpp`（约第 192 行）

硬编码的灰名单允许目标 API < 24 的应用加载某些私有平台库：
```
libandroid_runtime.so, libbinder.so, libcrypto.so, libcutils.so,
libexpat.so, libgui.so, libmedia.so, ...
```

这是一个过渡机制；应用应迁移到仅使用 NDK API。

### 3.5 对应用开发者的影响

- **私有 API 执行**：原生代码必须仅使用 NDK 稳定库。在 API 24+ 上链接内部平台库将在加载时失败。
- **命名空间隔离**：`System.loadLibrary()` 自动设置正确的命名空间。来自 JNI 的直接 `dlopen` 调用应使用相对路径。
- **从 APK 加载库**：设置 `android:extractNativeLibs="false"` 可直接从 APK 加载 `.so` 文件而无需解压，节省磁盘空间。
- **SONAME 要求**：库必须具有正确的 SONAME（API 23+）且不能有文本重定位（API 23+）。

---

## 4. 系统调用包装器

### 4.1 自动生成的桩

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/SYSCALLS.TXT`

Bionic 从声明式文件（`SYSCALLS.TXT`）自动生成系统调用桩。每行指定：
```
return_type func_name[:syscall_name]([parameters]) arch_list
```

例如：
```
uid_t   getuid:getuid32()         lp32
uid_t   getuid:getuid()           lp64
```

`gensyscalls.py` 脚本将架构特定的汇编桩生成到 `arch-{arm,arm64,x86,x86_64}/syscalls/` 中。这种方法确保了一致性，使添加新系统调用成为机械性工作。

### 4.2 VDSO 集成

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/vdso.cpp`

Bionic 使用内核的虚拟动态共享对象（VDSO）来避免频繁使用函数的系统调用开销：
- `clock_gettime()` -- 如果 VDSO 不可用则回退到 `__clock_gettime` 系统调用
- `clock_getres()`
- `gettimeofday()`
- `time()`

VDSO 函数指针在 `__libc_init_vdso()` 期间通过遍历 VDSO 的 ELF 符号表来解析。这对应用开发者是透明的，但为时间相关调用提供了可测量的性能改进。

### 4.3 对应用开发者的影响

- 系统调用对架构透明；NDK 代码使用标准 POSIX 包装器。
- 并非所有 Linux 系统调用都有 libc 包装器。对于不常见的系统调用，使用 `syscall(3)`。
- 某些系统调用被应用进程的 seccomp 过滤器阻止（参见第 8 节）。

---

## 5. 线程实现（pthread）

### 5.1 线程创建

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp`

Bionic 中的线程创建遵循以下序列：

1. **分配线程映射**：一次 `mmap` 调用分配栈 + 守护页 + 静态 TLS + 尾部守护页（`__allocate_thread_mapping`）。整个区域最初为 `PROT_NONE`，然后可写部分通过 `mprotect` 设为 `PROT_READ|PROT_WRITE`。

2. **初始化 TLS**：`bionic_tcb`（线程控制块）和 `bionic_tls` 结构放置在静态 TLS 区域内。栈守护金丝雀值被复制到 TLS。

3. **Clone**：通过 `clone()` 系统调用创建线程，标志为：`CLONE_VM | CLONE_FS | CLONE_FILES | CLONE_SIGHAND | CLONE_THREAD | CLONE_SYSVSEM | CLONE_SETTLS | CLONE_PARENT_SETTID | CLONE_CHILD_CLEARTID`。

4. **启动握手**：新线程在 `startup_handshake_lock` 上等待，直到父线程将其注册到调试器并返回 `pthread_t`。

5. **信号栈**：每个线程获得一个专用的备用信号栈（`sigaltstack`），在 AArch64 上还有一个影子调用栈（SCS）。

### 5.2 互斥锁实现

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_mutex.cpp`

互斥锁使用 Linux futex 实现。互斥锁状态打包在原子整数中：
- 位 0-3：类型（normal、recursive、errorcheck）
- 位 4：进程共享标志
- 位 5：优先级继承协议

实现直接使用 futex 操作，而不使用任何更高层的内核同步机制。

### 5.3 线程本地存储（TLS）

**关键文件：**
- `/home/dspfac/aosp-android-11/bionic/libc/bionic/bionic_elf_tls.cpp`
- `/home/dspfac/aosp-android-11/bionic/libc/private/bionic_tls.h`

Bionic 支持 ELF TLS（线程本地存储），包括静态和动态 TLS 模型。代计数器跟踪何时加载了新的 TLS 模块（通过 `dlopen`），`__tls_get_addr` 在代未变化时使用快速路径。TLS 槽分配包括保留槽，用于：线程自身指针、栈守护、bionic TLS 结构和 errno。

### 5.4 对应用开发者的影响

- `pthread_create` 直接使用 `clone()`，而非 `fork+exec`，使线程创建高效。
- 默认栈大小为 1 MB（可通过 `pthread_attr_setstacksize` 调整）。
- `pthread_gettid_np()` 是 Android 扩展，返回 Linux 线程 ID（对日志记录有用）。
- `pthread_setname_np()` 在 Android 上可用，设置 `/proc/[pid]/task/[tid]/comm` 名称。
- 优先级继承互斥锁可通过 `PTHREAD_PRIO_INHERIT` 使用。

---

## 6. 内存分配

### 6.1 分配器架构

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_common.cpp`

Bionic 使用分派表架构进行内存分配。所有标准函数（`malloc`、`free`、`calloc`、`realloc` 等）检查一个 `dispatch_table` 指针：
- 如果非空，调用被转发到调试/拦截分配器
- 否则，调用通过 `Malloc()` 宏进入默认分配器

### 6.2 默认分配器：Scudo 与 jemalloc

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/Android.bp`（第 94-120 行）

Android 11 在非精简（非低内存）设备上使用 **Scudo** 作为默认分配器，在精简设备上回退到 **jemalloc5**：

```
libc_scudo_product_variables = {
    malloc_not_svelte: {
        cflags: ["-DUSE_SCUDO"],
        whole_static_libs: ["libscudo"],
        exclude_static_libs: ["libjemalloc5", "libc_jemalloc_wrapper"],
    },
}
```

Scudo 是一个加固分配器，旨在检测和缓解基于堆的漏洞。它提供：
- 带有校验和的块头以检测损坏
- 最近释放内存的隔离区
- 随机化的分配模式
- 大小类之间的守护页

### 6.3 GWP-ASan（概率守护 - ASan）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/gwp_asan_wrappers.cpp`

GWP-ASan 是集成到 bionic 中的采样分配器，以概率方式将分配放置在受保护的内存中。配置：
- `MaxSimultaneousAllocations`：32
- `SampleRate`：2500（每 2500 次分配中有 1 次被守护）
- 在生产环境中以最小性能成本捕获释放后使用和缓冲区溢出

### 6.4 标记指针（堆指针标记）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_tagged_pointers.h`

在 AArch64 上，bionic 使用 ARM 的高字节忽略（TBI）特性用固定标记（`0xB4`）标记堆指针。`MaybeTagPointer()` 函数在每次分配时设置高字节，`MaybeUntagAndCheckPointer()` 在释放时验证并去除标记。这可以捕获：
- 释放后使用（已释放的内存标记被去除）
- 指针截断错误（屏蔽高字节的代码）

标记 `0xB4` 的选择标准：开发者可识别、与模式初始化（`0xAA`）不同、作为原始地址解引用时总是指向不可访问的内存，以及具有位集镜像半字节模式。

### 6.5 堆标记级别

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/heap_tagging.cpp`

支持三个级别：
- `M_HEAP_TAGGING_LEVEL_NONE` -- 无标记
- `M_HEAP_TAGGING_LEVEL_TBI` -- 高字节忽略指针标记
- `M_HEAP_TAGGING_LEVEL_ASYNC` -- ARM MTE（内存标记扩展）异步模式（Android 11 中为实验性）

### 6.6 对应用开发者的影响

- NDK 代码应使用标准 `malloc`/`free`；分配器是透明的。
- `malloc_usable_size()` 返回分配的可用大小，可能大于请求的大小。
- 可以通过 `libc.debug.malloc.options` 系统属性启用调试 malloc。
- 在 AArch64 设备上，堆指针的高字节非零。假设指针占用较少位数的代码将会崩溃。
- GWP-ASan 崩溃报告出现在 tombstone 中，并提供分配/释放堆栈跟踪。

---

## 7. NDK 稳定 API 接口

### 7.1 API 版本控制机制

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/include/android/versioning.h`

`__INTRODUCED_IN(api_level)` 宏为每个 NDK API 标注其可用的最低 API 级别：
```c
#define __INTRODUCED_IN(api_level) __attribute__((annotate("introduced_in=" #api_level)))
```

NDK 构建系统使用这些注解生成弱符号引用，确保目标为较旧 API 级别的应用在链接时而非运行时获得错误。

### 7.2 符号映射

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/libc.map.txt`

`libc.map.txt` 文件定义了 `libc.so` 中导出的每个符号，按版本块组织（`LIBC`、`LIBC_N`、`LIBC_O` 等）。每个符号可选地包含：
- 架构限制（`# arm`、`# x86` 等）
- API 级别引入（`# introduced=21`）
- 架构特定引入（`# introduced-arm=17`）

此文件控制 NDK 稳定 API 接口。未在此列出的符号对 NDK 应用不可用。

### 7.3 公共头文件

**关键目录：** `/home/dspfac/aosp-android-11/bionic/libc/include/`

公共头文件包括标准 POSIX 头文件（`stdio.h`、`stdlib.h`、`pthread.h` 等）以及 Android 特定扩展：
- `<android/fdsan.h>` -- 文件描述符清理器
- `<android/set_abort_message.h>` -- 设置崩溃报告的中止消息
- `<android/versioning.h>` -- API 级别宏
- `<dlfcn.h>` -- 动态链接（API 24 起支持 `dlvsym`）

### 7.4 dlopen/dlsym API

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/include/dlfcn.h`

动态链接 API 提供：
- `dlopen()`、`dlclose()`、`dlsym()`、`dlerror()`、`dladdr()`
- `dlvsym()`（API 24+）-- 带版本的符号查找
- Android 扩展：`android_dlopen_ext()` 和 `android_dlextinfo` 用于命名空间感知加载

值得注意的是：LP32（32 位）的 `RTLD_NOW`/`RTLD_GLOBAL` 值与 LP64 不同，这是为了历史兼容性：
```c
#if !defined(__LP64__)
#undef RTLD_NOW
#define RTLD_NOW      0x00000
#undef RTLD_GLOBAL
#define RTLD_GLOBAL   0x00002
#endif
```

### 7.5 对应用开发者的影响

- 始终正确设置 `minSdkVersion`；由 `__INTRODUCED_IN` 守护的 API 在不可用时将无法链接。
- 使用 `__builtin_available()` 或弱符号检查进行运行时特性检测。
- NDK sysroot（`platforms/android-{N}/usr/lib/`）精确定义了每个 API 级别可用的符号。
- 私有符号（不在 `libc.map.txt` 中）不属于稳定 API，可能会更改或消失。

---

## 8. 安全特性

### 8.1 ASLR（地址空间布局随机化）

链接器自 Lollipop（API 21）起对所有可执行文件强制执行 PIE（位置无关可执行文件）。非 PIE 可执行文件在 `linker_main.cpp` 第 401 行被拒绝：
```cpp
if (elf_hdr->e_type != ET_DYN) {
    // 错误：Android 5.0 及更高版本仅支持位置无关可执行文件（-fPIE）
}
```

库加载地址由内核的 mmap 随机化来随机化。

### 8.2 栈金丝雀

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/__stack_chk_fail.cpp`

栈金丝雀（`-fstack-protector`）默认启用。金丝雀值存储在 TLS 中（`TLS_SLOT_STACK_GUARD`），从 `__stack_chk_guard` 全局变量初始化。检测到损坏时，`__stack_chk_fail()` 调用 `async_safe_fatal("stack corruption detected (-fstack-protector)")`。

### 8.3 FORTIFY_SOURCE

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/include/bits/fortify/string.h` 及同级头文件

Bionic 使用 Clang 特定特性实现 `FORTIFY_SOURCE`：
- `__pass_object_size` 属性用于编译时缓冲区大小跟踪
- `__builtin___memcpy_chk`、`__builtin___strcpy_chk` 等用于运行时边界检查
- `__clang_error_if` 用于编译时错误诊断

FORTIFY 应用于：`string.h`（memcpy、strcpy、strcat、strlen 等）、`stdio.h`（fgets、sprintf、snprintf）、`unistd.h`（read、write、readlink）、`stdlib.h`（realpath）、`poll.h`、`fcntl.h`、`sys/socket.h`。

这比 glibc 的 FORTIFY 更先进，因为它利用了 Clang 对 `__builtin_object_size` 配合 `__pass_object_size` 属性的编译器支持，在编译时捕获更多错误。

### 8.4 影子调用栈（SCS）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp`（第 109-133 行）

在 AArch64 上，每个线程获得一个影子调用栈 -- 一个仅存储返回地址的独立栈，使用寄存器 `x18` 作为 SCS 指针。SCS 放置在大型守护区域（`SCS_GUARD_REGION_SIZE`）内的随机偏移处。这通过将返回地址保存在栈缓冲区溢出无法覆盖的位置来防护 ROP（面向返回的编程）攻击。

### 8.5 控制流完整性（CFI）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/linker/linker_cfi.h`

链接器维护一个 CFI 影子映射，跟踪每个加载的 DSO 的 `__cfi_check` 函数。当加载 CFI 启用的库时，影子会更新以将代码地址映射到其 CFI 验证函数。这实现了跨 DSO 的 CFI 检查。

### 8.6 RELRO（重定位只读）

链接器在解析符号后通过将重定位数据标记为只读（`protect_relro()`）来保护它，防止攻击者覆盖 GOT 条目。

### 8.7 Seccomp-BPF 过滤

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/seccomp/seccomp_policy.cpp`

Bionic 安装 seccomp-BPF 过滤器来限制应用进程可以进行的系统调用。存在三个过滤级别：
- `APP` -- 普通应用进程（最严格）
- `APP_ZYGOTE` -- 应用 zygote 进程
- `SYSTEM` -- 系统进程（最宽松）

过滤器支持双架构模式（64 位主 + 32 位辅），同时验证架构和系统调用号。不允许的系统调用触发 `SECCOMP_RET_TRAP`（SIGSYS）。

白名单文件定义允许的系统调用：
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_APP.TXT` -- 用于应用兼容性的额外系统调用
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_COMMON.TXT` -- 通用白名单
- `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_BLACKLIST_APP.TXT` -- 明确阻止的系统调用

### 8.8 文件描述符清理器（fdsan）

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/bionic/fdsan.cpp`

fdsan 使用标记关闭操作跟踪文件描述符所有权。每个 fd 有一个关联的 64 位所有者标记，编码所有者类型（FILE*、DIR*、unique_fd、FileInputStream、ParcelFileDescriptor、SQLite 等）和标识符。常见检测的错误：
- 双重关闭：关闭已经关闭的 fd
- 所有权违规：关闭属于不同对象的 fd（例如，从原生代码关闭 FileInputStream 的 fd）

在 Android 11 中，fdsan 默认为 `ANDROID_FDSAN_ERROR_LEVEL_FATAL`，使 fd 误用成为致命错误。`close()` 函数本身通过 `android_fdsan_close_with_tag()` 路由。

### 8.9 对应用开发者的影响

- **PIE 是强制性的**：所有原生可执行文件必须是位置无关的。
- **FORTIFY 在编译时捕获错误**：使用 NDK r21+ 和默认标志会自动启用 FORTIFY。
- **fdsan 错误是致命的**：双重关闭 fd 或违反所有权的代码将崩溃。使用 RAII 包装器如 `unique_fd`。
- **Seccomp 限制**：某些系统调用（特别是 32 位遗留调用）对应用进程被阻止。使用标准 libc 包装器而非原始 `syscall()`。
- **标记堆指针**：在 AArch64 上，`malloc` 返回的指针高字节非零。去除高字节的指针运算将导致崩溃。

---

## 9. 原生库加载机制

### 9.1 Java 到原生桥接

当 Android 应用调用 `System.loadLibrary("foo")` 时，执行以下链条：
1. **Java 层**：`Runtime.loadLibrary0()` 使用 ClassLoader 的原生库目录解析库路径
2. **原生桥接**：使用适当的 `android_namespace_t` 调用 `android_dlopen_ext()`
3. **链接器**：`do_dlopen()` 加载 ELF 文件，解析依赖项，应用重定位，调用构造函数
4. **JNI 注册**：如果存在 `JNI_OnLoad()`，则调用它允许库注册原生方法

### 9.2 库搜索顺序

自 API 23 起，链接器将库分为：
- **全局组**：主可执行文件、`LD_PRELOAD`、带有 `DF_1_GLOBAL` 的库
- **本地组**：库的 `DT_NEEDED` 条目的广度优先传递闭包

搜索顺序：先全局组，后本地组。这允许 ASAN 等工具拦截任何符号。

### 9.3 从 APK 加载

自 API 23 起，`.so` 文件可以直接从 APK/ZIP 加载：
```
dlopen("my_app.apk!/lib/arm64-v8a/libfoo.so", RTLD_NOW)
```
要求：页对齐（4096 字节边界）且未压缩存储。

### 9.4 对应用开发者的影响

- 尽可能使用 `System.loadLibrary()` 而非 `System.load()` -- 它自动处理架构目录。
- 对于复杂的依赖关系图，考虑将所有内容链接到单个 `.so` 以避免加载顺序问题。
- 可以启用链接器日志进行调试：`adb shell setprop debug.ld.app.com.example.myapp dlerror,dlopen,dlsym`

---

## 10. JNI 接口和 Java 原生桥接

### 10.1 从 Bionic 角度看 JNI

Bionic 本身不实现 JNI -- 那是 ART 运行时的职责。然而，bionic 提供了 JNI 依赖的基础设施：

- **线程模型**：JNI 的 `AttachCurrentThread`/`DetachCurrentThread` 依赖于 bionic 的 pthread 实现
- **动态链接**：`System.loadLibrary` 使用 bionic 的链接器和命名空间隔离
- **信号处理**：bionic 的信号基础设施与 ART 的信号处理程序共存，用于空指针检查和栈溢出检测
- **TLS**：JNI 线程本地数据使用 bionic 的 TLS 槽机制

### 10.2 `__BIONIC_WEAK_FOR_NATIVE_BRIDGE`

像 `pthread_create` 这样的函数被标记为 `__BIONIC_WEAK_FOR_NATIVE_BRIDGE`，允许原生桥接（用于通过翻译在 x86 上运行 ARM 应用）拦截和包装这些调用。

---

## 11. libm -- 数学库

### 11.1 架构

**关键目录：** `/home/dspfac/aosp-android-11/bionic/libm/`

libm 按架构组织，上游代码来自 FreeBSD 和 NetBSD：
- `upstream-freebsd/` -- 大多数数学函数（sin、cos、exp、log 等）
- `upstream-netbsd/` -- 一些专门函数
- `arm64/`、`amd64/`、`arm/`、`x86/`、`x86_64/` -- 架构优化实现
- `fake_long_double.c` -- 在 `long double` 等于 `double` 的架构上提供包装器

### 11.2 对应用开发者的影响

- 使用 NDK 时 `libm.so` 自动链接。
- `long double` 行为因架构而异：ARM 上为 64 位，x86 上为 80 位。
- 所有标准 C99/C11 数学函数均可用。

---

## 12. 值得注意的 Android 特定 API 和扩展

### 12.1 libc 中的 Android 专有 API

| API | 头文件 | 描述 |
|-----|--------|-------------|
| `android_fdsan_*` | `<android/fdsan.h>` | 文件描述符所有权跟踪 |
| `android_set_abort_message()` | `<android/set_abort_message.h>` | 设置崩溃报告的消息 |
| `android_mallopt()` | 平台内部 | 控制分配器行为 |
| `pthread_gettid_np()` | `<pthread.h>` | 获取 Linux 线程 ID |
| `__system_property_get()` | `<sys/system_properties.h>` | 读取系统属性 |
| `android_dlopen_ext()` | `<android/dlext.h>` | 带命名空间支持的扩展 dlopen |

### 12.2 缺失的 POSIX 功能

Bionic 有意省略或桩化了某些 POSIX 功能：
- 不支持超出 C/C.UTF-8 的 `locale`
- 无用户/组数据库函数（`getpwnam_r` 可用但仅适用于当前用户）
- 无 `wordexp()`
- 有限的 `regex` 支持
- 无 `aio_*`（POSIX 异步 I/O）

---

## 13. 构建系统和配置

### 13.1 构建配置

**关键文件：** `/home/dspfac/aosp-android-11/bionic/libc/Android.bp`

构建使用 Soong 构建系统，具有以下值得注意的配置选择：
- `-D_LIBC=1` -- bionic 内部构建
- `-Werror=pointer-to-int-cast`、`-Werror=int-to-pointer-cast` -- 捕获 32/64 位移植错误
- `-Wframe-larger-than=2048` -- 防止大栈帧
- `-fno-emulated-tls` -- GWP-ASan 所需
- 通过 `experimental_mte` 产品变量支持 MTE

### 13.2 分配器选择

默认分配器在构建时选择：
- **非精简设备**：Scudo（安全加固）
- **精简（低内存）设备**：jemalloc5（内存高效）

`libc_jemalloc_wrapper` 库提供 jemalloc 未直接实现的函数。

---

## 14. 应用开发者的关键关注点和建议

### 14.1 常见陷阱

1. **AArch64 上的指针标记**：不要假设指针仅使用 48 位。堆指针的高字节为 `0xB4`。
2. **fdsan 崩溃**：确保所有文件描述符恰好关闭一次且通过正确的所有者关闭。
3. **私有 API 访问**：不要 `dlopen` 平台内部库。仅使用 NDK API。
4. **32 位遗留系统调用**：某些 32 位系统调用被 seccomp 阻止。使用标准 libc 包装器。
5. **线程栈大小**：默认为 1 MB，对深度递归的原生代码可能不足。
6. **`locale` 限制**：不要在原生代码中依赖区域设置相关行为。

### 14.2 调试和诊断

- 启用链接器调试日志：`adb shell setprop debug.ld.app.<package> dlerror,dlopen,dlsym`
- 启用 malloc 调试：`adb shell setprop libc.debug.malloc.options "backtrace guard"`
- 使用 fdsan：API 30+ 默认为致命错误；检查 logcat 中的所有权违规消息
- GWP-ASan 崩溃报告在 tombstone 中包含分配和释放堆栈跟踪

### 14.3 性能考虑

- VDSO 消除了时间相关函数的系统调用开销
- Scudo 比 jemalloc 略慢但提供安全保证
- GNU 哈希表（API 23+）显著加速符号解析
- 从 APK 加载库避免了解压开销

---

## 15. 文件参考索引

| 领域 | 关键文件 |
|------|-------------|
| Bionic 概述 | `/home/dspfac/aosp-android-11/bionic/README.md` |
| NDK 变更指南 | `/home/dspfac/aosp-android-11/bionic/android-changes-for-ndk-developers.md` |
| 链接器入口点 | `/home/dspfac/aosp-android-11/bionic/linker/linker_main.cpp` |
| 链接器核心逻辑 | `/home/dspfac/aosp-android-11/bionic/linker/linker.cpp` |
| dlopen/dlsym 实现 | `/home/dspfac/aosp-android-11/bionic/linker/dlfcn.cpp` |
| 链接器命名空间 | `/home/dspfac/aosp-android-11/bionic/linker/linker_namespaces.h` |
| soinfo 结构 | `/home/dspfac/aosp-android-11/bionic/linker/linker_soinfo.h` |
| CFI 影子 | `/home/dspfac/aosp-android-11/bionic/linker/linker_cfi.h` |
| 系统调用定义 | `/home/dspfac/aosp-android-11/bionic/libc/SYSCALLS.TXT` |
| 线程创建 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_create.cpp` |
| 互斥锁实现 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/pthread_mutex.cpp` |
| ELF TLS | `/home/dspfac/aosp-android-11/bionic/libc/bionic/bionic_elf_tls.cpp` |
| Malloc 分派 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_common.cpp` |
| GWP-ASan | `/home/dspfac/aosp-android-11/bionic/libc/bionic/gwp_asan_wrappers.cpp` |
| 标记指针 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/malloc_tagged_pointers.h` |
| 堆标记 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/heap_tagging.cpp` |
| VDSO 集成 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/vdso.cpp` |
| 栈金丝雀 | `/home/dspfac/aosp-android-11/bionic/libc/bionic/__stack_chk_fail.cpp` |
| FORTIFY string.h | `/home/dspfac/aosp-android-11/bionic/libc/include/bits/fortify/string.h` |
| fdsan | `/home/dspfac/aosp-android-11/bionic/libc/bionic/fdsan.cpp` |
| Seccomp 策略 | `/home/dspfac/aosp-android-11/bionic/libc/seccomp/seccomp_policy.cpp` |
| Seccomp 应用白名单 | `/home/dspfac/aosp-android-11/bionic/libc/SECCOMP_WHITELIST_APP.TXT` |
| API 版本控制 | `/home/dspfac/aosp-android-11/bionic/libc/include/android/versioning.h` |
| 符号映射 | `/home/dspfac/aosp-android-11/bionic/libc/libc.map.txt` |
| dlfcn 头文件 | `/home/dspfac/aosp-android-11/bionic/libc/include/dlfcn.h` |
| 构建配置 | `/home/dspfac/aosp-android-11/bionic/libc/Android.bp` |

---

*报告基于 Android 11 AOSP 源代码审查生成。*
