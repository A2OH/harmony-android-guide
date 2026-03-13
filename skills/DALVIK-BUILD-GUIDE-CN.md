# Dalvik VM 64位 Linux 移植 — 完整构建指南

## 目录
1. [概述](#概述)
2. [前置依赖](#前置依赖)
3. [源码目录结构](#源码目录结构)
4. [兼容层 (compat/)](#兼容层)
5. [构建系统 (Makefile)](#构建系统)
6. [编译器标志详解](#编译器标志详解)
7. [编译的源文件](#编译的源文件)
8. [链接库](#链接库)
9. [构建目标](#构建目标)
10. [启动器 (launcher.cpp)](#启动器)
11. [启动环境配置](#启动环境配置)
12. [启动 JAR 选择](#启动jar选择)
13. [64位问题](#64位问题)
14. [所有64位修复 — 详细说明](#所有64位修复)
15. [非致命变通方案](#非致命变通方案)
16. [调试历史](#调试历史)
17. [完整构建与运行步骤](#完整构建与运行步骤)
18. [后续工作](#后续工作)

---

## 概述

本项目将 **Android KitKat (4.4) 时代的 Dalvik 虚拟机** 移植到 **64位 Linux (x86_64)** 上独立运行。Google 从未解决 Dalvik 的64位问题——他们选择放弃 Dalvik，转而开发 ART。我们自己完成了64位适配。

该 VM 是 Android 到 OpenHarmony 适配层的运行时：Android APK 的 DEX 字节码在此 VM 上运行，调用 `android.*` 适配类，由适配层桥接到 OpenHarmony 原生 API。

**当前状态**：VM 在64位 Linux 上成功启动，加载280+核心类，执行字节码，运行 GC，正常关闭。阻塞点：`libjavacore.so`（核心 Java 类的 JNI 原生库）。

## 前置依赖

| 依赖项 | 版本 | 用途 |
|---|---|---|
| g++ / gcc | 支持 C++11 | 编译 VM 源码 |
| libz (zlib) | 系统自带 | ZIP/JAR 文件处理 |
| libffi | 8.x | 平台无关的 JNI 调用约定 |
| libpthread | 系统自带 | 线程支持 |
| libdl | 系统自带 | 动态库加载 |
| ar | 系统自带 | 创建静态库 |
| make | GNU Make | 构建系统 |

**所需源码树**：
- `~/dalvik-kitkat/` — AOSP KitKat Dalvik VM 源码 (vm/、libdex/、dexopt/)
- `~/aosp-android-11/` — AOSP 头文件 (libnativehelper/include_jni 中的 jni.h、external/zlib、external/dlmalloc)

**构建 VM 本身不需要 JDK**。JDK 仅在编译 .java → .class → .dex 测试文件时需要。

## 源码目录结构

```
android-to-openharmony-migration/
└── dalvik-port/
    ├── Makefile              # 主构建系统 (269行)
    ├── CMakeLists.txt        # 备选 CMake 构建 (252行)
    ├── launcher.cpp          # 独立 dalvikvm 启动器 (140行)
    ├── core-kitkat.jar       # 启动类路径 JAR (280个类) ← 使用这个
    ├── core-boot.jar         # 存根 JAR (1047个类) — 不要使用
    ├── mini-boot.jar         # 最小存根 JAR (48个类) — 不要使用
    ├── compat/               # 兼容性头文件和存根
    │   ├── RegSlot.h         # dreg_t 类型定义 — 64位修复的核心
    │   ├── link_fixups.cpp   # 缺失 bionic/mterp 符号的链接器存根
    │   ├── JNIHelp.h         # JNI 辅助存根
    │   ├── JniConstants.h    # JNI 常量存根
    │   ├── ScopedPthreadMutexLock.h
    │   ├── UniquePtr.h       # Android 智能指针 (C++11之前)
    │   ├── safe_iop.h        # 安全整数溢出检查
    │   ├── ffi.h             # libffi 包装头文件
    │   ├── cutils/           # Android cutils 替代实现
    │   │   ├── atomic.h          # 原子操作 → GCC 内建函数
    │   │   ├── atomic-inline.h   # 内联原子辅助
    │   │   ├── ashmem.h          # 匿名共享内存 → shm_open
    │   │   ├── log.h             # ALOG* → fprintf(stderr)
    │   │   ├── memory_barrier.h  # 内存屏障 → __sync_synchronize
    │   │   ├── fs.h              # 文件系统辅助
    │   │   ├── multiuser.h       # 多用户存根
    │   │   ├── open_memstream.h  # open_memstream 包装
    │   │   ├── process_name.h    # 进程名存根
    │   │   ├── sched_policy.h    # 调度策略存根
    │   │   ├── sockets.h         # 套接字存根
    │   │   └── trace.h           # Systrace 存根 (空操作)
    │   ├── selinux/
    │   │   └── android.h         # SELinux 存根 (Linux 上空操作)
    │   ├── sys/
    │   │   └── capability.h      # Linux capabilities 存根
    │   ├── system/
    │   │   └── thread_defs.h     # 线程优先级定义
    │   ├── log/                  # 日志头文件重定向
    │   ├── nativehelper/         # JNI 辅助重定向
    │   ├── utils/
    │   │   └── Compat.h          # Bionic 兼容性宏
    │   └── bionic/               # bionic 特定存根
    └── build/                    # 构建输出目录
        ├── libdvm.a              # 静态库 (所有 VM + libdex 目标文件)
        ├── dalvikvm              # VM 启动器可执行文件
        ├── dexopt                # DEX 优化工具
        └── *.o                   # 目标文件
```

**VM 源码（原地修改）**：
```
~/dalvik-kitkat/
├── vm/                       # Dalvik VM 核心
│   ├── mterp/out/InterpC-portable.cpp  # 可移植解释器 (大量修改)
│   ├── interp/Stack.cpp      # 栈帧分配 (已修改)
│   ├── Jni.cpp               # JNI 桥接 (已修改)
│   ├── Init.cpp              # VM 初始化 (已修改)
│   ├── Sync.cpp              # 监视器/同步 (已修改)
│   ├── AtomicCache.cpp/.h    # 原子缓存 (已修改)
│   ├── IndirectRefTable.h    # 间接引用 (已修改)
│   ├── UtfString.h           # 字符串字段偏移 (已修改)
│   ├── Common.h              # CLZ 宏 (已修改)
│   ├── Native.h              # 原生方法类型 (已修改)
│   ├── oo/
│   │   ├── Object.h          # DalvikBridgeFunc/NativeFunc 签名 (已修改)
│   │   ├── ObjectInlines.h   # volatile 字段访问器 (已修改)
│   │   └── Array.cpp         # 数组分配 CLZ 修复 (已修改)
│   ├── arch/generic/Call.cpp  # dvmPlatformInvoke (已修改)
│   └── native/*.cpp          # ~28个原生方法文件 (全部修改: u4→dreg_t)
├── libdex/                   # DEX 文件解析库
└── dexopt/                   # DEX 优化工具
    └── OptMain.cpp
```

## 兼容层

`compat/` 目录为 Android 特有的 API 提供独立 Linux 替代实现。

### RegSlot.h — 64位修复的核心

```c
#if __SIZEOF_POINTER__ > 4
typedef uintptr_t dreg_t;  // 64位上为8字节
#else
typedef uint32_t dreg_t;   // 32位上为4字节（不变）
#endif
```

这个单一的 typedef 是整个64位移植的基础。解释器帧中的每个寄存器槽、每个原生方法参数数组、每个 JNI 桥接缓冲区都使用 `dreg_t` 代替 `u4`。

### link_fixups.cpp — 链接器存根

单独编译（不包含 AOSP dlmalloc 头文件路径）以避免宏冲突。提供：

| 符号 | 作用 |
|---|---|
| `dlmalloc_trim()` | 转发到 glibc `malloc_trim()` |
| `dlmalloc_inspect_all()` | 空操作（不需要堆检查） |
| `dlmem2chunk()` | 恒等函数（dlmalloc 内部） |
| `jniRegisterSystemMethods()` | 空操作 |
| `dvmCreateStringFromCstrAndLength(u4)` | 转接函数：转发到 `size_t` 版本（64位上 u4/size_t 不匹配） |
| `dvmMterpStdRun()` | 致命存根——汇编解释器已禁用 |
| `dvmMterpStdBail()` | 致命存根——汇编解释器已禁用 |

### cutils/ — Android 系统库替代

**cutils/atomic.h**：Android 的 `android_atomic_*` 函数用 GCC `__sync_*` 内建函数重新实现。对64位移植至关重要，因为 Android 的原子操作假设32位值。

**cutils/ashmem.h**：Android 匿名共享内存 → POSIX `shm_open()` / `mmap()`。

**cutils/log.h**：ALOG* 宏 (ALOGE, ALOGW, ALOGI, ALOGD, ALOGV) → `fprintf(stderr, ...)`。

**cutils/trace.h**：Systrace 宏 → 空操作（独立 Linux 上不需要）。

## 构建系统

Makefile 从两个源码树构建三个目标：

### 路径配置
```makefile
DALVIK    := /home/dspfac/dalvik-kitkat       # AOSP KitKat Dalvik 源码
AOSP      := /home/dspfac/aosp-android-11     # AOSP 头文件
VM        := $(DALVIK)/vm                     # VM 子目录
LIBDEX    := $(DALVIK)/libdex                 # DEX 解析库
COMPAT    := $(CURDIR)/compat                 # 我们的兼容性头文件
BUILD     := $(CURDIR)/build                  # 构建输出
```

### 头文件搜索顺序
```
-I$(VM)                                   # VM 内部头文件
-I$(DALVIK)                               # 顶层 dalvik 头文件
-I$(COMPAT)                               # 我们的兼容头文件（覆盖 Android 的）
-I$(AOSP)/libnativehelper/include_jni     # jni.h
-I$(AOSP)/external/zlib                   # zlib.h
-I$(AOSP)/external/dlmalloc              # dlmalloc.h（仅 DlMalloc.cpp 使用）
```

**重要**：`$(COMPAT)` 在 AOSP 头文件*之前*加入搜索路径，因此我们的替代头文件（cutils/atomic.h、cutils/log.h 等）会被优先找到。

## 编译器标志详解

### C++ 标志（.cpp 文件——绝大多数源文件）
```
-std=c++11                  # C++11 标准
-fPIC -O2                   # 位置无关代码 + 优化

# 平台定义
-DHAVE_LITTLE_ENDIAN        # x86_64 是小端序
-DHAVE_ENDIAN_H             # <endian.h> 可用
-DHAVE_SYS_UIO_H            # <sys/uio.h> 可用
-D_GNU_SOURCE               # GNU 扩展 (prlimit 等)
-DANDROID_SMP=1             # SMP 内存序（内存屏障生效）
-DLOG_TAG='"dalvikvm"'      # ALOG* 宏的日志标签
-DDVM_SHOW_EXCEPTION=1      # 在 stderr 显示异常详情
-DNDEBUG                    # 禁用 assert()（发布模式）

# Dalvik 特定
-DHAVE_POSIX_FILEMAP        # 使用 mmap 映射文件（非 Win32）
-DDVM_NO_ASM_INTERP=1       # 禁用汇编解释器（使用可移植 C 版本）
-DDVM_JMP_TABLE_MTERP=1     # 可移植解释器中使用跳转表分派
-DHAVE_STRLCPY=1            # strlcpy 可用
-Dtypeof=__typeof__         # GNU typeof → 标准 __typeof__

# 编译器行为
-fpermissive                # 允许某些 C++ 类型不匹配（旧 AOSP 代码需要）
-include unistd.h           # 强制包含 POSIX 头文件
-include sys/uio.h
-include sys/resource.h

# 警告抑制
-Wno-unused-parameter       # 许多 AOSP 函数有未使用的参数
-Wno-sign-compare           # 旧代码中的有符号/无符号比较
-Wno-write-strings          # 旧代码中字符串字面量 → char*
-Wno-format                 # printf 格式不匹配
-Wno-narrowing              # 初始化器中的窄化转换
-Wno-pointer-arith          # void* 上的指针运算
```

## 编译的源文件

### libdex (18个文件) — DEX 文件解析器
```
DexFile         DexSwapVerify    DexCatch        DexClass
DexDataMap      DexDebugInfo     DexInlines      DexOpcodes
DexOptData      DexProto         DexUtf          InstrUtils
Leb128          OptInvocation    SysUtil         ZipArchive
CmdUtils        sha1
```

### DVM 核心 (107个文件) — VM 本体

**核心 VM** (28个文件)：
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

**垃圾回收器** (alloc/ — 10个文件)：
```
Alloc       CardTable    HeapBitmap   HeapDebug
Heap        DdmHeap      Verify       Visit
DlMalloc    HeapSource   MarkSweep
```

**字节码分析** (analysis/ — 7个文件)：
```
CodeVerify   DexPrepare   DexVerify    Liveness
Optimize     RegisterMap  VerifySubs   VfyBasicBlock
```

**解释器** (interp/ + mterp/ — 4个文件)：
```
Interp.cpp              # 解释器调度
Stack.cpp               # 栈帧管理
Mterp.cpp               # mterp 入口点（路由到可移植版本）
InterpC-portable.cpp    # 可移植 C 解释器（所有操作码）
```

**对象模型** (oo/ — 6个文件)：
```
AccessCheck   Array    Class    Object
Resolve       TypeCheck
```

**反射** (reflect/ — 3个文件)：
```
Annotation   Proxy    Reflect
```

**原生方法** (native/ — 28个文件)：
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

**调试/性能分析** (jdwp/ + hprof/ — 12个文件)：
```
ExpandBuf    JdwpAdb     JdwpConstants  JdwpEvent
JdwpHandler  JdwpMain    JdwpSocket
Hprof        HprofClass  HprofHeap      HprofOutput  HprofString
```

**平台层** (2个文件)：
```
os/linux.cpp            # Linux 特定的操作系统函数
arch/generic/Call.cpp   # 通用调用约定（libffi）
arch/generic/Hints.cpp  # 可移植构建无 JIT 提示
```

**测试** (1个文件)：
```
test/TestHash.cpp
```

### 兼容层 (1个文件——单独编译)：
```
link_fixups.cpp         # 链接器存根（不含 AOSP dlmalloc 路径）
```

**总计：18 (libdex) + 107 (dvm) + 1 (compat) = 126 个源文件**

## 链接库

```makefile
LDFLAGS := -lpthread -ldl -l:libz.so.1 -l:libffi.so.8
```

| 库 | 用途 |
|---|---|
| `-lpthread` | POSIX 线程（VM 线程、监视器、信号捕获器） |
| `-ldl` | 动态链接器（dlopen/dlsym 用于加载 JNI .so） |
| `-l:libz.so.1` | zlib（读取 ZIP/JAR 文件、DEX 压缩） |
| `-l:libffi.so.8` | 外部函数接口（JNI 原生方法调用） |

**注意**：使用 `-l:libname.so.N` 语法强制链接特定 soname 版本，避免版本不匹配。

## 构建目标

### `make all` — 完整构建
执行：`build-all` → `link` → `dalvikvm` → `dexopt`

### `make build-all` — 编译所有文件
逐一编译全部125个源文件。报告通过/失败计数和失败文件的错误摘要。

### `make link` — 创建静态库
```bash
ar rcs build/libdvm.a build/*.o
```
所有目标文件（libdex + dvm + link_fixups）合入一个静态库。

### `make dalvikvm` — 构建启动器
```bash
g++ $(CXXFLAGS) -o build/dalvikvm launcher.cpp build/libdvm.a $(LDFLAGS)
```

### `make dexopt` — 构建 DEX 优化器
```bash
g++ $(CXXFLAGS) -o build/dexopt $(DALVIK)/dexopt/OptMain.cpp build/libdvm.a $(LDFLAGS)
```
运行时必需——VM 在首次加载 DEX 文件时会派生 `dexopt` 进程进行验证和优化。

### `make first-pass` — 快速编译测试
仅编译 DexFile.cpp、Init.cpp 和 InterpC-portable.cpp，快速检查头文件/包含问题。

### `make clean`
```bash
rm -rf build/
```

## 启动器

`launcher.cpp` 是一个140行的独立程序，替代 Android 的 `dalvikvm` 命令。使用标准 **JNI Invocation API**：

1. **解析参数**：`-cp <类路径>`、`-X*` VM 标志、类名、应用参数
2. **构建 JNI 选项**：从 `$BOOTCLASSPATH` 环境变量获取启动类路径、用户类路径、`-X` 透传、默认堆 4MB–64MB
3. **创建 VM**：`JNI_CreateJavaVM(&vm, &env, &vmArgs)`
4. **查找主类**：`env->FindClass("com/foo/Bar")`
5. **查找 main 方法**：`env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V")`
6. **构建 String[] 参数**：`env->NewObjectArray()` + `env->NewStringUTF()`
7. **调用**：`env->CallStaticVoidMethod(cls, mainMethod, mainArgs)`
8. **清理**：`vm->DestroyJavaVM()`

包含崩溃处理器，在 SIGSEGV/SIGABRT 时打印回溯信息。

## 启动环境配置

VM 需要两个环境变量和特定的目录结构：

```bash
# 1. 数据目录（用于 dalvik-cache，存储优化后的 DEX 文件）
export ANDROID_DATA=/tmp/android-data
mkdir -p $ANDROID_DATA/dalvik-cache

# 2. 根目录（VM 在 $ANDROID_ROOT/bin/dexopt 查找 dexopt）
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_ROOT/bin
ln -sf $(pwd)/build/dexopt $ANDROID_ROOT/bin/dexopt
```

**为什么需要 dalvik-cache？** 当 VM 首次加载 JAR/DEX 时，它会派生 `dexopt` 进程验证字节码并生成优化的 `.odex` 文件。缓存在 `$ANDROID_DATA/dalvik-cache/` 中，后续运行更快。

**为什么需要 dexopt 符号链接？** VM 使用 `execv("$ANDROID_ROOT/bin/dexopt", ...)` 启动优化器。在真实 Android 上，路径是 `/system/bin/dexopt`。我们的符号链接指向构建输出。

## 启动 JAR 选择

| JAR | 类数量 | java.lang.Class 字段 | 是否可用？ |
|---|---|---|---|
| **core-kitkat.jar** | 280 | 1个静态, 4个实例 | **可以——使用这个** |
| core-boot.jar | 1047 | 0个静态, 0个实例 | **不可以** — 存根，VM 拒绝 |
| mini-boot.jar | 48 | 0个静态, 0个实例 | **不可以** — 存根，VM 拒绝 |

**为什么 java.lang.Class 重要？** VM 硬编码了对 `java.lang.Class` 的期望：
- 必须恰好有1个静态字段（`serialVersionUID`）
- 必须有特定的实例字段（`name`、`status`、`verifyErrorClass`、`initiatingLoaderList`）
- 存根 JAR 的 Class 定义字段数为0 → VM 在启动时中止

**core-kitkat.jar** 从真实的 KitKat ROM 中提取，包含正确编译的核心类。

## 64位问题

### 根本原因
Dalvik 仅为32位 ARM 设计。整个代码库中：
- 寄存器槽是 `u4`（uint32_t，4字节）
- 对象引用是 `Object*`（32位上4字节）
- 由于 `sizeof(Object*) == sizeof(u4) == 4`，指针正好放入寄存器槽

在64位 Linux 上：
- `sizeof(Object*) = 8` 字节
- `sizeof(u4) = 4` 字节
- **将指针存入 u4 会截断高32位**
- 指针 `0x00007f8a12345678` 变成 `0x12345678` → SIGSEGV 段错误

Google 的方案：放弃 Dalvik，从零开始构建支持64位的 ART。**我们的方案：使用 `dreg_t` 将所有寄存器槽加宽到指针宽度。**

### 64位上的大小变化

| 类型/结构体 | 32位 | 64位 | 影响 |
|---|---|---|---|
| `Object*` | 4字节 | 8字节 | 寄存器中的指针被截断 |
| `Object` 头部 | 8字节 (clazz + lock) | 16字节 (8 clazz + 4 lock + 4填充) | 所有字段偏移改变 |
| `ArrayObject.contents` 偏移 | 12 | 24 | 数组数据访问 |
| `DataObject.instanceData` 偏移 | 8 | 16 | 实例字段访问 |
| 寄存器槽 (`dreg_t`) | 4字节 | 8字节 | 帧大小翻倍 |
| `AtomicCacheEntry` 键 | 各4字节 | 各8字节 | 缓存查找 |

## 所有64位修复

### 修复1：寄存器槽 (InterpC-portable.cpp)

**问题**：`u4* fp`（帧指针）保存寄存器文件。每个槽4字节。对象指针8字节。

**修复**：修改帧指针类型和所有宏：
```cpp
// 修改前
u4* fp;
#define GET_REGISTER(_idx)  (fp[(_idx)])
#define SET_REGISTER(_idx, _val)  (fp[(_idx)] = (u4)(_val))

// 修改后
dreg_t* fp;
#define GET_REGISTER(_idx)  ((u4)fp[(_idx)])  // 整数值仍返回 u4
#define SET_REGISTER(_idx, _val)  (fp[(_idx)] = (dreg_t)(u4)(_val))  // 零扩展
```

对象专用宏读写完整指针宽度：
```cpp
#define GET_REGISTER_AS_OBJECT(_idx)  ((Object*)fp[(_idx)])
#define SET_REGISTER_AS_OBJECT(_idx, _val)  (fp[(_idx)] = (dreg_t)(_val))
```

### 修复2：帧分配 (Stack.cpp)

**问题**：帧分配计算大小为 `registersSize * 4`（每 u4 槽的字节数）。

**修复**：
```cpp
// 修改前
size_t frameSize = registersSize * 4;

// 修改后
size_t frameSize = registersSize * sizeof(dreg_t);  // 64位上每槽8字节
```

同时修复了：`ins` 指针计算（帧内传入参数的指针）和将对象引用写入帧槽的参数存储。

### 修复3：SET_REGISTER 中的对象指针截断 (InterpC-portable.cpp)

**问题**：许多操作码使用 `SET_REGISTER(vdst, (u4)obj)` 存储对象，先将指针转为 u4（截断）。

**修复**：将所有此类模式替换为 `SET_REGISTER_AS_OBJECT(vdst, obj)`：
- `OP_CONST_CLASS`、`OP_CONST_STRING`、`OP_CONST_STRING_JUMBO`
- `OP_NEW_INSTANCE`、`OP_NEW_ARRAY`
- `OP_CHECK_CAST`（将检查后的对象存回）
- `OP_IGET_OBJECT`、`OP_SGET_OBJECT`
- `OP_AGET_OBJECT`
- `OP_MOVE_RESULT_OBJECT`
- 所有异常处理器的对象存储

### 修复4：GET_REGISTER 中的对象指针截断 (InterpC-portable.cpp)

**问题**：读取对象的操作码使用 `(Object*)GET_REGISTER(vsrc)`。`GET_REGISTER` 返回 `u4`（已截断），然后转换为指针（错误地符号扩展）。

**修复**：替换为 `GET_REGISTER_AS_OBJECT(vsrc)`：
- `OP_IPUT_OBJECT`、`OP_SPUT_OBJECT`
- `OP_APUT_OBJECT`
- `OP_INSTANCE_OF`、`OP_CHECK_CAST`（读取源对象）
- `OP_MONITOR_ENTER`、`OP_MONITOR_EXIT`
- `OP_THROW`
- `OP_IF_EQZ`、`OP_IF_NEZ`（对象空检查）
- `OP_INVOKE_*`（this 指针）

### 修复5：move-object 宽度错误 (InterpC-portable.cpp)

**问题**：`OP_MOVE_OBJECT` 使用 `SET_REGISTER(d, GET_REGISTER(s))`，经过 u4 中间值。

**修复**：直接指针宽度复制：`fp[d] = fp[s]`

`OP_MOVE_OBJECT_FROM16` 和 `OP_MOVE_OBJECT_16` 同样修复。

### 修复6：return-object 丢失高位 (InterpC-portable.cpp)

**问题**：`OP_RETURN_OBJECT` 将结果存为 `retval.i = GET_REGISTER(vsrc)` — `retval.i` 是 `int32_t`。

**修复**：`retval.l = GET_REGISTER_AS_OBJECT(vsrc)` — `retval.l` 是 `Object*`（指针宽度）。

### 修复7：filled-new-array 对象内容 (InterpC-portable.cpp)

**问题**：`OP_FILLED_NEW_ARRAY` 使用 `u4*` 指针将寄存器值复制到数组内容中。对于对象数组，元素是 `Object*`（64位上8字节），但代码以 u4（4字节）写入。

**修复**：分为对象和基本类型两条路径：
```cpp
if (typeCh == 'L' || typeCh == '[') {
    // 对象数组：元素是 Object*（指针宽度）
    Object** objContents = (Object**)(void*)newArray->contents;
    for (i = 0; i < vsrc1; i++)
        objContents[i] = GET_REGISTER_AS_OBJECT(vdst+i);
    dvmWriteBarrierArray(newArray, 0, newArray->length);
} else {
    // 基本类型数组：元素是 u4（始终32位）
    contents = (u4*)(void*)newArray->contents;
    for (i = 0; i < vsrc1; i++)
        contents[i] = GET_REGISTER(vdst+i);
}
```

### 修复8：原生方法参数宽度 (所有 native/*.cpp)

**问题**：每个原生方法实现接收 `const u4* args` — 来自解释器的参数数组。在64位上，寄存器槽是 `dreg_t`（8字节），但函数按 `u4`（4字节）读取，只获得每个参数的低半部分。

**修复**：修改所有 ~28 个 native/*.cpp 文件：`const u4* args` → `const dreg_t* args`。同时更新了 `Object.h`（DalvikBridgeFunc、DalvikNativeFunc 签名）和 `Native.h`。

### 修复9：JNI 桥接参数宽度 (Jni.cpp, Call.cpp)

**问题**：`dvmCallJNIMethod()` 为 JNI 调用构建修改后的参数数组。它分配 `u4* modArgs` 并复制参数。JNI 桥接随后通过 libffi 读取这些参数，但 libffi 期望对象参数使用指针宽度的槽。

**修复**：Jni.cpp 中改为 `dreg_t* modArgs`。Call.cpp 中 `dvmPlatformInvoke()` 读取 `const dreg_t* argv`。

### 修复10：volatile 对象字段读取 (ObjectInlines.h)

**问题**：`dvmGetFieldObjectVolatile()` 使用 `android_atomic_acquire_load((int32_t*)ptr)` 以获取语义读取对象字段。在64位上，这只读取8字节指针中的32位。

**修复**：条件编译：
```cpp
#if __SIZEOF_POINTER__ > 4
    // 64位：全宽度指针读取 + 显式内存屏障
    Object* val = *ptr;
    ANDROID_MEMBAR_FULL();
    return val;
#else
    // 32位：原始32位原子加载
    return (Object*)android_atomic_acquire_load((int32_t*)ptr);
#endif
```

`dvmGetStaticFieldObjectVolatile()` 同样修复。

### 修复11：IndirectRef 索引提取 (IndirectRefTable.h)

**问题**：`(u4)iref` 将指针直接转为 u4，在64位上会丢失高位。某些编译器甚至可能以不可预测的方式优化掉该转换。

**修复**：两步转换：`(u4)(uintptr_t)iref`

同时修复了 `indirectRefKind()`：`(uintptr_t)iref & 0x03` 代替 `(u4)iref & 0x03`。

### 修复12：String 字段偏移 (UtfString.h)

**问题**：硬编码的 `java.lang.String` 字段偏移：
```c
#define STRING_FIELDOFF_VALUE    8   // 32位: Object(8) + 0
#define STRING_FIELDOFF_HASHCODE 12  // 32位: Object(8) + 4
#define STRING_FIELDOFF_OFFSET   16  // 32位: Object(8) + 8
#define STRING_FIELDOFF_COUNT    20  // 32位: Object(8) + 12
```
在64位上，`sizeof(Object) = 16`，所以这些值全部错误（应为 16、20、24、28）。

**修复**：取消注释 `#define USE_GLOBAL_STRING_DEFS`，使 VM 在运行时从实际的 `java.lang.String` 类查找字段偏移，而非使用硬编码值。

### 修复13：数组 CLZ 缺陷 (Array.cpp, Common.h)

**问题**：数组元素移位计算：
```cpp
size_t elementShift = sizeof(size_t) * CHAR_BIT - 1 - CLZ(elemWidth);
```
`CLZ` 使用 `__builtin_clz()`，操作的是 `unsigned int`（32位）。但 `sizeof(size_t)` 在64位上是8。因此：
- 对于 `elemWidth=2`：`CLZ(2) = 30`（在32位域中正确）
- 但：`8 * 8 - 1 - 30 = 33`（应为1！）
- 移位33意味着分配 `count << 33` 字节——小数组变成 ~1TB

**修复**：使用 `sizeof(unsigned int)` 匹配 CLZ 的域：
```cpp
size_t elementShift = sizeof(unsigned int) * CHAR_BIT - 1 - CLZ(elemWidth);
// 4 * 8 - 1 - 30 = 1  ✓
```

同时在 Common.h 中添加了 `CLZL` 宏，用于真正需要64位 CLZ 的场合：
```cpp
#if __SIZEOF_POINTER__ > 4
#define CLZL(x) __builtin_clzl(x)
#else
#define CLZL(x) __builtin_clz(x)
#endif
```

### 修复14：AtomicCache 键 (AtomicCache.h/cpp)

**问题**：`AtomicCacheEntry` 有 `u4 key1, key2` 字段。用于 instanceof 缓存查找时存储类指针。在64位上，指针8字节 → 截断为4字节 → 缓存未命中或错误匹配。

**修复**：加宽为 `uintptr_t key1, key2`。更新了 `ATOMIC_CACHE_LOOKUP` 宏的哈希计算和 `dvmUpdateAtomicCache()` 签名。

### 修复15：AtomicCache 对齐 (AtomicCache.cpp)

**问题**：缓存条目对齐计算：
```cpp
newCache->entries = (AtomicCacheEntry*)
    (((int) newCache->entryAlloc + 63) & ~63);
```
`(int)` 将堆指针从8字节截断为4字节。

**修复**：使用 `(uintptr_t)` 转换：
```cpp
newCache->entries = (AtomicCacheEntry*)
    (((uintptr_t) newCache->entryAlloc + 63) & ~63);
```

### 修复16：监视器列表 CAS (Sync.cpp)

**问题**：将监视器添加到全局列表：
```cpp
android_atomic_release_cas(
    (int32_t)mon->next,
    (int32_t)mon,
    (int32_t*)(void*)&gDvm.monitorList);
```
三个转换都将指针截断为32位。

**修复**：使用 GCC 的指针宽度 CAS：
```cpp
do {
    mon->next = gDvm.monitorList;
} while (!__sync_bool_compare_and_swap(&gDvm.monitorList, mon->next, mon));
```

### 修复17：Object 头部大小 (系统性问题)

**问题**：`sizeof(Object) = 8`（32位上：4字节 ClassObject* + 4字节 u4 lock）。在64位上，`sizeof(Object) = 16`（8字节 ClassObject* + 4字节 u4 lock + 4字节填充以对齐）。

所有假设 `sizeof(Object) = 8` 计算的字段偏移都是错误的。影响：
- String 字段偏移（修复12）
- DataObject.instanceData 起始位置（偏移 16 vs 8）
- ArrayObject.contents 起始位置（偏移 24 vs 12）
- 代码库中每个硬编码的偏移量

**修复**：尽可能使用运行时计算的偏移量。VM 的字段解析逻辑（`dvmResolveField`、`dvmResolveInstField`）使用编译时 `sizeof()` 计算正确偏移，因此已解析的字段访问是正确的。主要风险在于硬编码常量。

## 非致命变通方案

| 问题 | 位置 | 修复 | 原因 |
|---|---|---|---|
| `dvmCreateInlineSubsTable` 失败 | Init.cpp | `ALOGW` 警告代替返回错误 | `NativeTestTarget` 类不在我们精简的启动 JAR 中——仅用于测试 |
| `loadJniLibrary("javacore")` 失败 | Init.cpp | `ALOGW` 警告代替 `dvmAbort()` | libjavacore.so 尚未从 AOSP libcore 构建 |
| `loadJniLibrary("nativehelper")` 失败 | Init.cpp | 同上 | libnativehelper.so 尚未构建 |

这些变通方案让 VM 启动并运行不调用原生方法的字节码。`<clinit>` 调用 JNI 原生方法的类仍会在运行时失败。

## 调试历史

本节记录首次成功启动过程中遇到并修复的 bug 序列。

### 1. "Non-absolute bootclasspath entry"（启动类路径非绝对路径）
**原因**：使用了相对路径 `core-boot.jar` 而非绝对路径。
**修复**：`$(pwd)/core-boot.jar` 或 `-Xbootclasspath:$(pwd)/core-kitkat.jar`

### 2. "Could not stat dex cache directory '/data/dalvik-cache'"（无法访问 dalvik-cache 目录）
**原因**：VM 在 `$ANDROID_DATA/dalvik-cache` 查找 dalvik-cache，默认为 `/data/dalvik-cache`。
**修复**：`export ANDROID_DATA=/tmp/android-data; mkdir -p /tmp/android-data/dalvik-cache`

### 3. "execv '/system/bin/dexopt' failed"（执行 dexopt 失败）
**原因**：VM 派生 `$ANDROID_ROOT/bin/dexopt` 优化 DEX 文件。默认路径 `/system/bin/dexopt`。
**修复**：`export ANDROID_ROOT=/tmp/android-root; ln -sf $(pwd)/build/dexopt /tmp/android-root/bin/dexopt`

### 4. "java.lang.Class has 0 static fields (expected 1)"（Class 静态字段数不匹配）
**原因**：core-boot.jar 中 java.lang.Class 是存根，无字段。
**修复**：切换到 `core-kitkat.jar`，有正确的 Class（1个静态字段，4个实例字段）。

### 5. "softLimit of 4095.999MB hit for 1099511627800-byte allocation"（分配 ~1TB 内存）
**原因**：CLZ 缺陷。`sizeof(size_t)*CHAR_BIT-1-CLZ(2)` = `63-30` = 33（应为1）。
**修复**：改为 `sizeof(unsigned int)*CHAR_BIT-1-CLZ(2)` = `31-30` = 1。

### 6. "String.value offset = 16; expected 8"（String 字段偏移不匹配）
**原因**：Object 头部在64位上是16字节。硬编码 STRING_FIELDOFF_VALUE=8 错误。
**修复**：启用 `USE_GLOBAL_STRING_DEFS` 进行运行时字段偏移查找。

### 7. SIGSEGV：dvmInstanceofNonTrivial（AtomicCache 键截断）
**原因**：AtomicCacheEntry 键是 `u4`，截断64位类指针。缓存返回错误条目。
**修复**：将键加宽为 `uintptr_t`。

### 8. SIGSEGV：dvmInstanceofNonTrivial（AtomicCache 对齐截断）
**原因**：缓存对齐使用 `(int)ptr` 转换，截断堆指针。
**修复**：改为 `(uintptr_t)ptr`。

### 9. SIGSEGV：dvmFreeMonitorList（监视器 CAS 截断）
**原因**：监视器列表 CAS 操作使用 `(int32_t)` 转换监视器指针。
**修复**：替换为 `__sync_bool_compare_and_swap`。

### 10. "dvmCreateInlineSubsTable failed"（内联替换表创建失败）
**原因**：精简的启动 JAR 中缺少 `NativeTestTarget` 类。
**修复**：改为非致命（ALOGW）。

### 11. "dlopen libjavacore.so failed"（加载 libjavacore.so 失败）
**原因**：libjavacore.so 尚未构建。
**修复**：改为非致命（ALOGW）。VM 继续运行，但原生方法调用将失败。

所有修复完成后：**VM 启动，加载280+个类，执行类初始化器，运行 GC，正常关闭。**

## 完整构建与运行步骤

```bash
# ══════════════════════════════════════════
# 步骤1：检查前置依赖
# ══════════════════════════════════════════
# 确保你有：
#   - 支持 C++11 的 g++
#   - libz-dev（zlib 头文件 + 库）
#   - libffi-dev（libffi 头文件 + 库）
#   - 源码树在 ~/dalvik-kitkat/ 和 ~/aosp-android-11/
# 验证：
g++ --version
dpkg -l | grep -E 'libz|libffi'

# ══════════════════════════════════════════
# 步骤2：构建
# ══════════════════════════════════════════
cd ~/android-to-openharmony-migration/dalvik-port

# 完整构建（编译126个文件，链接，构建 dalvikvm + dexopt）
make all

# 预期输出：
#   Compiled: 126
#   Failed:   0
#   Total:    126
#   ═══ Linking libdvm.a ═══
#   Created: build/libdvm.a (XX MB)
#   ═══ Building dalvikvm launcher ═══
#   Created: build/dalvikvm
#   ═══ Building dexopt ═══
#   Created: build/dexopt

# 验证构建输出：
ls -lh build/dalvikvm build/dexopt build/libdvm.a

# ══════════════════════════════════════════
# 步骤3：配置启动环境
# ══════════════════════════════════════════
export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin
ln -sf $(pwd)/build/dexopt $ANDROID_ROOT/bin/dexopt

# ══════════════════════════════════════════
# 步骤4：运行 VM
# ══════════════════════════════════════════
# 启动测试（仅启动 VM 加载核心类，不指定用户类）
./build/dalvikvm \
  -Xbootclasspath:$(pwd)/core-kitkat.jar \
  -Xverify:none -Xdexopt:none \
  -cp /dev/null NoClass 2>&1 | head -50

# 预期：VM 创建，加载类，然后报 "class 'NoClass' not found"
# 这证明 VM 正确启动了！

# 运行实际的 DEX 字节码（当你有 test.jar 时）：
./build/dalvikvm \
  -Xbootclasspath:$(pwd)/core-kitkat.jar \
  -Xverify:none -Xdexopt:none \
  -classpath test.jar MainClass

# ══════════════════════════════════════════
# 步骤5：清理 dalvik-cache（如需要）
# ══════════════════════════════════════════
# 如果你更改了启动 JAR，清除缓存的优化版本：
rm -rf $ANDROID_DATA/dalvik-cache/*
```

## 后续工作

### 1. 构建 libjavacore.so
为 x86_64-linux 交叉编译 AOSP KitKat `libcore/` 原生代码。提供 `java.io.*`、`java.net.*`、`java.lang.Math`、`java.lang.System.arraycopy()` 等的 JNI 实现。

### 2. 创建最小测试 DEX
需要 `Hello.java` → `Hello.class` → `classes.dex` → `test.jar`，仅使用字节码操作（算术、对象、数组），不调用 JNI 原生方法。需要 javac（当前未安装 JDK）。

### 3. 清理调试日志
删除开发过程中添加的临时调试日志：
- FINISH 宏中的操作码追踪
- INIT_RANGE 日志
- 解释器入口日志
- dvmInitClass 日志

### 4. 完整64位审计
搜索剩余的指针截断模式：
```
grep -rn '(int)\|int32_t)\|(u4)' vm/ | grep -i 'ptr\|obj\|ref\|method\|class'
```
已知风险区域：
- `Exception.cpp` 栈追踪存储（`int*` 用于指针数组）
- `Debugger.cpp` JDWP 对象 ID 处理
- 任何剩余的 `android_atomic_*` 涉及指针字段

### 5. 交叉编译到 OpenHarmony
- 目标：aarch64-linux-ohos（OHOS NDK 工具链）
- C 库：musl libc（非 bionic，非 glibc）
- 需要：aarch64 版本的 libffi、libz
- 兼容层可能需要针对 musl 特定差异进行更新
