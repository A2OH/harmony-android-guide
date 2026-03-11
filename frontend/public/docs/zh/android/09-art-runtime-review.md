# ART（Android Runtime）-- 代码审查报告

## 源码路径
- **ART 运行时**: `~/aosp-android-11/art/`
- **Java 核心库**: `~/aosp-android-11/libcore/`
- **Dalvik 工具**: `~/aosp-android-11/dalvik/`

---

## 1. ART 运行时架构概述

### 1.1 顶层组织结构

ART 运行时（`art/`）由以下几个主要子系统组成：

| 目录 | 用途 |
|-----------|---------|
| `art/runtime/` | 核心运行时（类加载、GC、线程、JNI、解释器） |
| `art/compiler/` | 优化编译器后端（AOT 和 JIT） |
| `art/dex2oat/` | 预编译（AOT）工具 |
| `art/libdexfile/` | DEX 文件解析和表示库 |
| `art/libprofile/` | PGO 配置文件数据格式 |
| `art/profman/` | 配置文件管理工具 |
| `art/dexdump/` | DEX 文件检查工具 |
| `art/dexlayout/` | DEX 文件布局优化 |
| `art/oatdump/` | OAT/VDEX 文件检查工具 |
| `art/openjdkjvm/` | OpenJDK JVM 接口层 |
| `art/openjdkjvmti/` | JVMTI 代理接口 |
| `art/libnativebridge/` | 用于运行非原生 ISA 代码的 Native Bridge |
| `art/libnativeloader/` | 原生库加载基础设施 |
| `art/sigchainlib/` | 信号链库 |

### 1.2 关键源文件

| 文件 | 行数 | 作用 |
|------|-------|------|
| `art/runtime/runtime.cc` | ~3,068 | 运行时初始化，单例生命周期 |
| `art/runtime/class_linker.cc` | ~9,883 | 类加载、链接、验证 |
| `art/runtime/gc/heap.cc` | ~4,321 | 堆管理和 GC 编排 |
| `art/runtime/gc/collector/concurrent_copying.cc` | ~3,870 | 默认（CC）垃圾收集器 |
| `art/runtime/jni/jni_internal.cc` | ~3,364 | JNI 函数实现 |
| `art/dex2oat/dex2oat.cc` | ~3,313 | AOT 编译驱动 |

### 1.3 运行时初始化（`Runtime::Init`）

**文件**: `art/runtime/runtime.h`, `art/runtime/runtime.cc`（第 1186 行）

`Runtime` 类是进程范围的单例。`Runtime::Init()` 按以下顺序执行初始化：

1. **环境快照** -- 保护子进程免受 LD_LIBRARY_PATH 修改的影响。
2. **MemMap 初始化** -- 设置内存映射区域管理。
3. **OatFileManager 创建** -- 管理 OAT 文件的发现和加载。
4. **Monitor 和线程基础设施** -- `MonitorList`、`MonitorPool`、`ThreadList`。
5. **InternTable** -- 用于去重的字符串驻留。
6. **堆构建** -- 创建包含所有配置空间的 GC 堆（参见第 3 节）。
7. **ClassLinker 初始化** -- 引导类加载基础设施。
8. **JIT 创建** -- 如果启用，初始化 JIT 编译器。
9. **信号处理** -- 安装用于 NullPointerException、StackOverflow 的故障处理程序。

初始化期间设置的关键配置项：
- `is_zygote_` / `is_primary_zygote_` -- 影响 GC 策略和 fork 行为。
- `is_concurrent_gc_enabled_` -- 默认为 `true`。
- `is_explicit_gc_disabled_` -- 可以禁用 `System.gc()`。
- `hidden_api_policy_` -- 控制隐藏 API 限制的执行。
- `target_sdk_version_` -- 影响行为兼容性。
- `is_low_memory_mode_` -- 触发保守的 GC 调优。

**应用开发者影响**: 运行时选项（如堆大小、GC 模式）由系统为应用设置。开发者通过清单属性（如 `android:largeHeap="true"`）和 `targetSdkVersion`（控制隐藏 API 执行）间接影响这些选项。

---

## 2. 代码执行：编译器、解释器和 JIT

### 2.1 执行模式

ART 支持三种共存的执行模式：

| 模式 | 描述 | 使用时机 |
|------|-------------|-----------|
| **解释器** | 逐条字节码执行 | 调试、未编译方法、首次运行 |
| **AOT (dex2oat)** | 预编译为原生代码 | 安装时或后台 dexopt |
| **JIT** | 热方法的即时编译 | 运行时，基于调用计数 |

### 2.2 解释器

**目录**: `art/runtime/interpreter/`

解释器有多种实现策略：

- **Switch 解释器**（`interpreter_switch_impl*.cc`）-- 标准 C++ 基于 switch 的分派。实现分布在 4 个文件中（`interpreter_switch_impl0.cc` 到 `interpreter_switch_impl3.cc`），以减少编译时间。
- **Mterp**（`interpreter/mterp/`）-- 汇编优化的解释器，按架构提供实现（`arm/`、`arm64/`、`x86/`、`x86_64/`）。通过 `gen_mterp.py` 生成。
- **Nterp**（`interpreter/mterp/nterp.cc`）-- 下一代解释器，使用相同基础设施但改进了分派效率，是 mterp 的高效替代。

解释器函数 `InterpreterJni()`（在 `interpreter.cc` 第 48 行）通过基于方法简短描述符的类型函数指针转换来处理来自解释代码的直接 JNI 调用，避免了简单场景下完整 JNI 编译器的开销。

**应用开发者影响**: 解释执行比编译代码慢 10-100 倍。应用在以下场景会遇到解释执行：
- JIT 预热前的首次启动
- 调试会话（强制解释执行）
- 从未达到 JIT 编译阈值的方法

### 2.3 AOT 编译（dex2oat）

**文件**: `art/dex2oat/dex2oat.cc`

`dex2oat` 工具将 DEX 字节码编译为存储在 OAT 文件中的原生代码。运行时机包括：
- 系统镜像创建（引导镜像编译）
- 应用安装（使用适当的编译过滤器）
- 系统后台优化（`bg-dexopt-job`）

#### 编译过滤器

**文件**: `art/runtime/compiler_filter.cc`

编译过滤器控制编译程度：

| 过滤器 | AOT | JNI | Quicken | 描述 |
|--------|-----|-----|---------|-------------|
| `kAssumeVerified` | 否 | 否 | 否 | 跳过验证，不编译 |
| `kExtract` | 否 | 否 | 否 | 仅提取 DEX |
| `kVerify` | 否 | 否 | 否 | 仅验证字节码 |
| `kQuicken` | 否 | 是 | 是 | 快化字节码 + 编译 JNI 桩 |
| `kSpaceProfile` | 是 | 是 | 是 | 配置文件引导，优化空间 |
| `kSpace` | 是 | 是 | 是 | 优化空间 |
| `kSpeedProfile` | 是 | 是 | 是 | 配置文件引导，优化速度 |
| `kSpeed` | 是 | 是 | 是 | 编译所有内容，优化速度 |
| `kEverythingProfile` | 是 | 是 | 是 | 配置文件引导，编译所有内容 |
| `kEverything` | 是 | 是 | 是 | 编译全部内容 |

**应用开发者影响**: Android 11 上应用安装的默认过滤器通常是 `speed-profile`，这意味着只有在配置文件中出现的方法才会进行 AOT 编译。冷方法在解释器中运行或按需进行 JIT 编译。开发者可以在某些构建配置中通过 `dexopt` 标志来影响这一行为，但没有直接的 API 控制。

### 2.4 优化编译器

**目录**: `art/compiler/optimizing/`

优化编译器是一个完整的基于 SSA 的编译器，包含以下阶段：

- **构建器**（`builder.cc`、`block_builder.cc`）-- 将 DEX 字节码转换为 HGraph（SSA IR）。
- **优化阶段** -- 包括：
  - `bounds_check_elimination.cc` -- 消除冗余的数组边界检查
  - `code_sinking.cc` -- 将代码移到更靠近使用位置
  - `cha_guard_optimization.cc` -- 类层次分析优化
  - 死代码消除、常量折叠、内联等。
- **代码生成器** -- 按架构的后端：
  - `code_generator_arm_vixl.cc` -- ARM 32 位（通过 VIXL 汇编器）
  - `code_generator_arm64.cc` -- ARM 64 位
  - `code_generator_x86.cc` -- x86 32 位
  - `code_generator_x86_64.cc` -- x86 64 位
  - 每个架构的向量扩展（`code_generator_vector_*.cc`）

### 2.5 JIT 编译器

**文件**: `art/runtime/jit/jit.h`, `art/runtime/jit/jit.cc`, `art/runtime/jit/jit_code_cache.h`

JIT 编译器与应用执行并发运行：

#### 编译阈值

```
默认编译阈值：   20 * 512 = 10,240 次调用
默认预热阈值：   10,240 / 2 = 5,120
OSR 阈值：       可配置（用于栈上替换）
采样批次大小：   512（必须是 2 的幂）
```

#### JIT 选项（`JitOptions` 类）

| 选项 | 描述 |
|--------|-------------|
| `compile_threshold_` | 触发 JIT 编译前的调用次数 |
| `warmup_threshold_` | 开始分析前的调用次数 |
| `osr_threshold_` | 栈上替换的阈值 |
| `code_cache_initial_capacity_` | JIT 代码缓存的初始大小 |
| `code_cache_max_capacity_` | JIT 代码缓存的最大大小 |
| `priority_thread_weight_` | UI 线程调用的额外权重 |
| `invoke_transition_weight_` | 调用转换的权重 |
| `use_tiered_jit_compilation_` | 启用分层编译（基线然后优化） |
| `use_baseline_compiler_` | 使用更简单的基线编译器 |

#### JIT 执行流程

1. 方法在解释器中执行；调用计数器以 512 为批次递增。
2. 达到预热阈值时，开始收集分析数据（内联缓存、分支配置文件）。
3. 达到编译阈值时，JIT 线程接管该方法进行编译。
4. `JitCompilerInterface::CompileMethod()` 运行优化编译器。
5. 编译后的代码放入 `JitCodeCache`。
6. 方法的入口点被原子更新为指向原生代码。
7. **OSR（栈上替换）**：长时间运行的循环可以在执行过程中从解释切换到编译。

#### JIT 线程优先级

JIT 编译线程以优先级 9（最低前台优先级）运行，以避免与 UI 线程竞争。

**应用开发者影响**: JIT 编译意味着应用随着热代码的编译会逐渐变快。但是：
- 首次启动总是较慢（冷启动）。
- JIT 代码缓存受内存限制；在内存压力下，编译后的代码可能被驱逐。
- `priority_thread_weight_` 给予 UI 线程在编译决策中 1000 倍的权重，确保其调用的方法优先被编译。

---

## 3. 垃圾收集

### 3.1 GC 架构

**文件**: `art/runtime/gc/heap.h`, `art/runtime/gc/heap.cc`

GC 子系统的组织结构：

| 组件 | 目录/文件 | 用途 |
|-----------|----------------|---------|
| 堆 | `gc/heap.cc` | 所有 GC 操作的中央编排器 |
| 收集器 | `gc/collector/` | GC 算法实现 |
| 空间 | `gc/space/` | 内存区域管理 |
| 记账 | `gc/accounting/` | 卡表、位图、记忆集 |

### 3.2 收集器类型

**文件**: `art/runtime/gc/collector_type.h`

| 收集器 | 常量 | 描述 |
|-----------|----------|-------------|
| **CMS** | `kCollectorTypeCMS` | 并发标记-清除（旧默认值） |
| **CC** | `kCollectorTypeCC` | 并发复制（带读屏障的默认值） |
| **CC 后台** | `kCollectorTypeCCBackground` | CC 的后台压缩 |
| **MS** | `kCollectorTypeMS` | 非并发标记-清除 |
| **SS** | `kCollectorTypeSS` | 半空间（压缩） |
| **同构紧凑** | `kCollectorTypeHomogeneousSpaceCompact` | CMS 的后台压缩 |

Android 11 中的默认收集器是**并发复制（CC）**（当读屏障启用时，这是标准配置）：

```cpp
// 来自 runtime.cc 第 1394-1397 行：
kUseReadBarrier ? gc::kCollectorTypeCC : xgc_option.collector_type_,
kUseReadBarrier ? BackgroundGcOption(gc::kCollectorTypeCCBackground)
                : runtime_options.GetOrDefault(Opt::BackgroundGc),
```

### 3.3 并发复制收集器

**文件**: `art/runtime/gc/collector/concurrent_copying.h`, `concurrent_copying.cc`

CC 收集器是 Android 11 中 ART 的主要 GC。关键设计特性：

- **分代收集** -- 当 `use_generational_cc` 为 true 时支持仅年轻代收集。当 Baker 读屏障可用时默认启用。
- **读屏障** -- 使用 Baker 风格的读屏障进行并发对象复制。每次对象引用读取都包含屏障检查。
- **基于区域** -- 在 `RegionSpace` 上操作，将堆划分为固定大小的区域。
- **低暂停时间** -- 大部分工作并发进行。暂停仅限于根扫描和引用处理。

配置常量：
```cpp
kDefaultGcMarkStackSize = 2 * MB
kGrayDirtyImmuneObjects = true  // 在暂停期间灰化脏对象以防止脏页
```

### 3.4 其他收集器

- **标记-清除**（`mark_sweep.cc`）-- 传统标记-清除。变体包括：
  - `partial_mark_sweep.cc` -- 仅收集应用堆，不收集 zygote 空间
  - `sticky_mark_sweep.cc` -- 仅收集自上次 GC 以来分配的对象
- **半空间**（`semi_space.cc`）-- 通过在两个空间之间复制存活对象来压缩的复制收集器。

### 3.5 堆空间

**目录**: `art/runtime/gc/space/`

| 空间 | 文件 | 用途 |
|-------|------|---------|
| **RegionSpace** | `region_space.cc` | CC 收集器的主分配空间 |
| **RosAllocSpace** | `rosalloc_space.cc` | CMS 模式的 Runs-of-Slots 分配器 |
| **DlMallocSpace** | `dlmalloc_space.cc` | 基于 dlmalloc 的分配空间 |
| **BumpPointerSpace** | `bump_pointer_space.cc` | 快速指针递增分配（半空间） |
| **LargeObjectSpace** | `large_object_space.cc` | 用于超过阈值的对象 |
| **ImageSpace** | `image_space.cc` | 内存映射的引导/应用镜像 |
| **ZygoteSpace** | `zygote_space.cc` | 冻结的 zygote 堆（跨应用共享） |

### 3.6 堆配置常量

**文件**: `art/runtime/gc/heap.h`（第 130+ 行）

```
默认初始大小：       2 MB
默认最大大小：       256 MB
默认 TLAB 大小：     32 KB
部分 TLAB 大小：     16 KB
非移动空间容量：     64 MB
默认最小空闲：       512 KB
默认最大空闲：       2 MB
目标利用率：         0.75（75%）
堆增长乘数：         2.0（带读屏障时为 4.0，因为额外 +1.0）
大对象阈值：         3 * page_size（12 KB）
长暂停日志阈值：     5 ms
长 GC 日志阈值：     100 ms
堆修剪等待：         5 秒
收集器转换等待：     5 秒
```

### 3.7 GC 触发原因

**文件**: `art/runtime/gc/gc_cause.h`

GC 可由以下原因触发：
- `kGcCauseForAlloc` -- 分配失败（阻塞）
- `kGcCauseBackground` -- 主动后台收集
- `kGcCauseExplicit` -- `System.gc()` 调用
- `kGcCauseForNativeAlloc` -- 原生分配水位线超出
- `kGcCauseCollectorTransition` -- 前台/后台切换

### 3.8 记账基础设施

**目录**: `art/runtime/gc/accounting/`

- **卡表**（`card_table.cc`）-- 跟踪分代收集的脏页。每张卡覆盖 128 字节的堆。
- **空间位图**（`space_bitmap.cc`）-- 在连续空间内标记存活对象。
- **Mod Union 表**（`mod_union_table.cc`）-- 跟踪部分收集的跨空间引用。
- **读屏障表**（`read_barrier_table.h`）-- 支持 CC 收集器的读屏障。
- **记忆集**（`remembered_set.cc`）-- 跟踪从非移动空间到移动空间的引用。

**应用开发者影响**：
- `System.gc()` 默认被执行，但可以禁用（`is_explicit_gc_disabled_`）。应用不应依赖它来保证正确性。
- 大分配（>12KB 原始数组）进入大对象空间，使用不同的分配策略。
- CC 收集器提供低于 5ms 的暂停时间，使 GC 对应用基本透明。
- 256MB 的默认堆大小可通过 `android:largeHeap="true"` 调整（通常增加到 512MB，取决于设备）。
- 32KB 的线程本地分配缓冲区（TLAB）实现了无锁小对象分配。
- 低内存模式（由系统属性触发）使用 1.0 倍堆增长乘数，而非默认的 2.0-4.0 倍，导致更频繁的 GC。

---

## 4. DEX 文件格式和加载

### 4.1 DEX 文件结构

**文件**: `art/libdexfile/dex/dex_file.h`

DEX 文件头结构：

| 字段 | 描述 |
|-------|-------------|
| `magic_[8]` | DEX 魔数（"dex\n035\0" 或 "dex\n039\0"） |
| `checksum_` | 文件的 Adler32 校验和（在魔数和校验和字段之后） |
| `signature_[20]` | 文件内容的 SHA-1 哈希 |
| `file_size_` | 文件总大小（字节） |
| `string_ids_size/off` | 字符串常量池 |
| `type_ids_size/off` | 类型描述符 |
| `proto_ids_size/off` | 方法原型 |
| `field_ids_size/off` | 字段声明 |
| `method_ids_size/off` | 方法声明 |
| `class_defs_size/off` | 类定义 |
| `data_size/off` | 数据段 |

映射项类型枚举所有段：`kDexTypeCodeItem`（字节码）、`kDexTypeStringDataItem`、`kDexTypeClassDataItem`、`kDexTypeAnnotationItem`、`kDexTypeHiddenapiClassData` 等。

### 4.2 紧凑 DEX

**文件**: `art/libdexfile/dex/compact_dex_file.h`, `compact_dex_file.cc`

紧凑 DEX 是 ART 内部的优化表示，通过以下方式减小 DEX 大小：
- VDEX 中多个 DEX 文件共享数据段
- 紧凑偏移表（`compact_offset_table.cc`）
- 更高效的调试信息编码

### 4.3 OAT 文件

**文件**: `art/runtime/oat_file.h`

OAT 文件包含 AOT 编译的原生代码及其源 DEX 文件。每个 OAT 类跟踪三种类状态：

```cpp
kOatClassAllCompiled = 0   // 所有方法已编译，每个方法都有 OatMethodOffsets
kOatClassSomeCompiled = 1  // 位图指示哪些方法已编译
kOatClassNoneCompiled = 2  // 所有方法在解释器中运行
```

### 4.4 VDEX 文件

**文件**: `art/runtime/vdex_file.h`

VDEX 文件包含从 APK 中提取的 DEX 文件以及验证元数据。它们避免了后续加载时的重新提取和重新验证。

**应用开发者影响**：
- 应用在 APK 中发布 DEX 字节码。ART 透明地处理到 OAT/VDEX 的编译。
- Multidex 被透明处理；APK 中的每个 DEX 文件在 OAT 文件中都有自己的条目。
- 每个 DEX 文件 65,536 方法限制是格式约束。`type_ids` 限制也是 65,535。
- DEX 格式版本 37+ 强制执行类定义排序规则。

---

## 5. 类加载机制

### 5.1 ClassLinker

**文件**: `art/runtime/class_linker.cc`（约 9,883 行）

`ClassLinker` 是核心类加载引擎。其主要方法 `FindClass()`（第 2963 行）实现以下查找算法：

1. **原始类型检查** -- 单字符描述符直接处理。
2. **已加载类表查找** -- 通过 `LookupClass()` 检查类是否已加载。
3. **引导类路径搜索** -- 对于空类加载器，搜索 `boot_class_path_` 条目。
4. **数组类创建** -- 对于以 `[` 开头的描述符，通过 `CreateArrayClass()` 创建数组类。
5. **Base DEX 类加载器搜索** -- 对于已知的类加载器类型（PathClassLoader、DexClassLoader），通过 `FindClassInBaseDexClassLoader()` 直接搜索。
6. **Java 端回退** -- 对于未知的类加载器类型，委托给 Java 中的 `ClassLoader.loadClass()`。

`DefineClass()` 方法（第 3194 行）处理从 DEX 数据创建类的实际过程：
1. 分配类对象
2. 从 DEX 加载字段和方法
3. 运行运行时回调（`ClassPreDefine`）
4. 验证类
5. 解析超类和接口
6. 链接类（计算虚方法表、IMT、字段偏移）

### 5.2 类加载器层次结构

**目录**: `libcore/dalvik/src/main/java/dalvik/system/`

#### BaseDexClassLoader

**文件**: `libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java`

所有基于 DEX 的类加载器的基类。关键特性：
- `pathList`（`DexPathList`）-- 包含要搜索的 DEX 文件列表。
- `sharedLibraryLoaders` -- 用于 Android `<uses-library>` 功能的类加载器数组。在 `pathList` 之前搜索。
- `findClass()` 实现：共享库 -> pathList 搜索 -> ClassNotFoundException。
- 支持 `ByteBuffer[]` 构造函数用于内存中 DEX 加载。
- `computeClassLoaderContextsNative()` -- 原生方法，为每个类路径条目计算类加载器上下文字符串（供 dex2oat 使用）。

#### PathClassLoader

**文件**: `libcore/dalvik/src/main/java/dalvik/system/PathClassLoader.java`

系统用于加载已安装应用的类加载器。委托给 `BaseDexClassLoader`，`optimizedDirectory = null`（自 API 26 起已弃用）。

#### DexClassLoader

**文件**: `libcore/dalvik/src/main/java/dalvik/system/DexClassLoader.java`

用于从非应用组成部分的 JAR/APK 文件加载代码。自 API 26 起，`optimizedDirectory` 参数被忽略（传递为 `null`）。

#### InMemoryDexClassLoader

**文件**: `libcore/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java`

从包含 DEX 数据的 `ByteBuffer` 对象加载类。不需要文件系统访问。用于动态生成或网络加载的代码。

#### DelegateLastClassLoader

**文件**: `libcore/dalvik/src/main/java/dalvik/system/DelegateLastClassLoader.java`

实现**委托后置**查找顺序：
1. 引导类路径
2. 自身 DEX 文件
3. 父类加载器

这与标准 Java 的父优先委托相反。适用于已加载代码应覆盖父类的插件架构。

### 5.3 类加载器上下文

**文件**: `art/runtime/class_loader_context.cc`

类加载器上下文将整个类加载器层次结构编码为字符串，供 dex2oat 使用：
- `PCL` = PathClassLoader
- `DLC` = DelegateLastClassLoader
- `IMC` = InMemoryDexClassLoader

格式：`PCL[foo.jar:bar.jar]{shared_lib.jar#shared_lib2.jar};PCL[parent.jar]`

此上下文对以下方面至关重要：
- 确保 OAT 文件对当前类加载器配置有效
- 检测因类路径变更而需要重新编译的情况

### 5.4 原生类加载器支持

**文件**: `art/runtime/native/dalvik_system_DexFile.cc`, `dalvik_system_BaseDexClassLoader.cc`

原生实现将 Java 类加载器 API 桥接到 ART 运行时：
- `DexFile_openDexFileNative` -- 打开和验证 DEX 文件
- `DexFile_defineClassNative` -- 调用 `ClassLinker::DefineClass()`
- `BaseDexClassLoader_computeClassLoaderContextsNative` -- 计算上下文字符串

**应用开发者影响**：
- `PathClassLoader` 自动用于应用加载。使用 `DexClassLoader` 或 `InMemoryDexClassLoader` 进行动态代码加载可以绕过 Play Protect 扫描。
- 类加载器上下文必须在安装和执行之间保持稳定；类路径变更会使已编译的 OAT 文件失效。
- `DelegateLastClassLoader` 支持类覆盖，但如果引导类路径在系统更新中发生变化，可能会导致微妙的错误。
- 类加载涉及验证，首次运行时可能很显著（通过 VDEX 缓存缓解）。

---

## 6. JNI 桥接

### 6.1 架构

**目录**: `art/runtime/jni/`

| 文件 | 用途 |
|------|---------|
| `jni_internal.cc` | 完整的 JNI 函数表实现（约 3,364 行） |
| `jni_env_ext.cc` | 每线程 JNI 环境扩展 |
| `java_vm_ext.cc` | JavaVM 接口扩展 |
| `jni_id_manager.cc` | 管理 JNI 方法/字段 ID 映射 |
| `check_jni.cc` | 调试模式 JNI 验证 |

### 6.2 JNI ID 管理

**文件**: `art/runtime/jni/jni_id_manager.cc`

Android 11 引入了可配置的 JNI ID 管理（`jni_ids_indirection_`）：
- **直接模式** -- JNI 方法/字段 ID 是指向 `ArtMethod`/`ArtField` 的直接指针。速度快但阻止结构性类重定义。
- **不透明/间接模式** -- ID 是表中的索引。支持类重定义但增加间接开销。

### 6.3 JNI 层的隐藏 API 执行

**文件**: `art/runtime/hidden_api.h`

通过 `AccessMethod::kJNI` 检查对非 SDK API 的 JNI 访问。执行策略有三个级别：
- `kDisabled` -- 无限制
- `kJustWarn` -- 记录警告但允许访问
- `kEnabled` -- 阻止对深灰名单和黑名单 API 的访问

**应用开发者影响**：
- JNI 是从 Java 调用原生（C/C++）代码的主要方式。每次 JNI 调用都涉及线程状态转换（`ScopedThreadStateChange`），这会增加开销。
- JNI 本地引用按线程跟踪，必须仔细管理以避免泄漏。`indirect_reference_table` 跟踪这些引用。
- `CheckJNI` 模式（在调试构建中启用）捕获常见的 JNI 错误，如使用过期引用、传递错误类型或缺少异常检查。
- 隐藏 API 执行适用于 JNI 层 -- 对受限 API 的 `GetMethodID`/`GetFieldID` 调用将以 `NoSuchMethodError`/`NoSuchFieldError` 失败。
- JNI 临界区（`GetPrimitiveArrayCritical`）禁用移动 GC，如果持有时间过长可能导致 GC 暂停。

---

## 7. 配置文件引导优化（PGO）

### 7.1 配置文件收集

**文件**: `art/runtime/jit/profile_saver.h`

`ProfileSaver` 作为后台线程运行，收集配置文件数据：
- **分析内容**: 热方法、内联缓存数据、类使用模式。
- **保存时机**: 定期、在 JIT 活动通知时、以及启动完成时。
- **保存位置**: 每个应用的配置文件（通常在 `/data/misc/profiles/` 中）。

关键操作：
- `Start()` -- 开始分析，跟踪 `(output_filename, code_paths)`。
- `NotifyJitActivity()` -- 在 JIT 事件上触发配置文件处理。
- `NotifyStartupCompleted()` -- 发信号从启动阶段过渡到稳态分析。
- `ForceProcessProfiles()` -- 手动触发（SIGUSR1）。

### 7.2 配置文件管理工具

**目录**: `art/profman/`

`profman` 工具管理配置文件数据：
- `profile_assistant.cc` -- 合并来自多个来源的配置文件。
- `boot_image_profile.cc` -- 生成引导镜像配置文件。

### 7.3 配置文件引导编译流程

1. 应用使用 JIT 运行；`ProfileSaver` 记录热方法和类。
2. 配置文件作为 `.prof` 文件保存到磁盘。
3. 在空闲维护期间，`bg-dexopt-job` 使用 `--compiler-filter=speed-profile` 运行 `dex2oat`。
4. `dex2oat` 读取配置文件并仅编译被分析过的方法。
5. 在下次应用启动时，从 OAT 文件加载编译后的代码。

**应用开发者影响**：
- PGO 意味着应用在反复使用后会自动变快，无需开发者干预。
- 基线配置文件（概念在此引入，后来正式化）让开发者可以随 APK 一起发布配置文件，实现首日编译优化。
- `speed-profile` 过滤器通常编译 10-30% 的方法，在代码大小和性能之间取得平衡。
- 启动方法通过 `NotifyStartupCompleted()` 在分析中被优先处理。

---

## 8. 应用启动优化

### 8.1 引导镜像

**文件**: `art/runtime/gc/space/image_space.cc`

引导镜像包含预编译和预初始化的核心框架类。它通过 Zygote 内存映射到每个应用进程：
- 预链接的类（无需运行时链接）
- 预初始化的静态字段
- 预分配的常用对象（如装箱整数、空数组）

### 8.2 应用镜像

类似于引导镜像但针对每个应用。包含在编译时解析的应用 DEX 文件中的类。作为 `ImageSpace` 加载到堆中。

### 8.3 Zygote 优化

- **ZygoteSpace**（`gc/space/zygote_space.cc`）-- Zygote 创建的堆被冻结，并通过写时复制在所有 fork 的应用进程间共享。
- **Zygote Hooks**（`native/dalvik_system_ZygoteHooks.cc`）-- Zygote fork 期间调用的原生方法：
  - Fork 前：GC、转储堆区域
  - Fork 后：重新初始化线程池、为应用进程调整 GC

### 8.4 启动感知 JIT

JIT 具有启动感知能力：
- 通过 `priority_thread_weight_`（默认 1000 倍）给予 UI 线程更高权重。
- `NotifyStartupCompleted()` 从积极编译过渡到稳态。
- 栈上替换允许长时间运行的启动循环在执行过程中从 JIT 获益。

### 8.5 镜像空间加载顺序

**文件**: `art/runtime/gc/space/image_space_loading_order.h`

控制是先加载系统镜像还是数据（更新）镜像，影响使用哪个预编译代码。

**应用开发者影响**：
- 冷启动时间主要由类加载和验证决定。在启动时使用更少的类有助于改善。
- Zygote fork 模型意味着框架类已经加载 -- 应用启动成本主要是特定于应用的类加载。
- 来自之前运行的配置文件数据通过 PGO 改善后续启动时间。
- 大型静态初始化器（`<clinit>`）同步执行并阻塞类加载。

---

## 9. 运行时配置和调优

### 9.1 堆调优参数

来自 `Runtime::Init()`（第 1381 行）：

| 参数 | 选项键 | 默认值 | 效果 |
|-----------|-----------|---------|--------|
| 初始堆大小 | `MemoryInitialSize` | 2 MB | 起始堆大小 |
| 增长限制 | `HeapGrowthLimit` | 设备相关 | 非 largeHeap 应用的软限制 |
| 最大大小 | `MemoryMaximumSize` | 256 MB | 硬限制（或 `largeHeap` 限制） |
| 最小空闲 | `HeapMinFree` | 512 KB | GC 后最小空闲字节数 |
| 最大空闲 | `HeapMaxFree` | 2 MB | GC 前最大空闲字节数 |
| 目标利用率 | `HeapTargetUtilization` | 0.75 | 存活数据与堆大小的目标比率 |
| 前台增长乘数 | `ForegroundHeapGrowthMultiplier` | 2.0（CC 为 +1.0） | 堆增长的激进程度 |
| TLAB 使用 | `UseTLAB` | true | 线程本地分配缓冲区 |
| 并行 GC 线程 | `ParallelGCThreads` | 设备相关 | GC 工作线程 |
| 并发 GC 线程 | `ConcGCThreads` | 设备相关 | 并发 GC 线程 |

### 9.2 GC 调优

`XGcOption` 结构控制：
- `collector_type_` -- 前台收集器（启用读屏障时覆盖为 CC）
- `generational_cc` -- 启用分代 CC（Baker 读屏障时默认为 true）
- `verify_pre_gc_heap_` / `verify_post_gc_heap_` -- 调试验证（开销大）
- `gcstress_` -- 持续 GC 用于测试
- `measure_` -- 测量 GC 性能指标

### 9.3 低内存模式

当设置 `is_low_memory_mode_` 时：
- 前台堆增长乘数默认为 1.0（而非 2.0-4.0）。
- 类加载使用更紧凑的哈希表负载因子（0.5-0.8 对比 0.4-0.7）。
- 更积极的 GC 行为。

### 9.4 编译器调优

```cpp
// 来自 runtime.cc 构造函数：
is_concurrent_gc_enabled_ = true    // 默认启用并发 GC
is_explicit_gc_disabled_ = false    // 默认执行 System.gc()
image_dex2oat_enabled_ = true       // 允许为镜像运行 dex2oat
```

---

## 10. Java 核心库（libcore）

### 10.1 组织结构

**根目录**: `~/aosp-android-11/libcore/`

| 目录 | 描述 |
|-----------|-------------|
| `ojluni/` | 源自 OpenJDK 的类（java.lang、java.util、java.io、java.net 等） |
| `dalvik/` | Dalvik/Android 特定类（dalvik.system、dalvik.annotation） |
| `luni/` | Android 特定内部库（libcore.io、libcore.net 等） |
| `libart/` | ART 特定的 JNI 实现 |
| `json/` | org.json 实现 |
| `xml/` | XML 解析（org.xml.sax、javax.xml） |
| `dom/` | DOM XML 实现 |
| `apex/` | APEX 模块集成 |

### 10.2 关键包

#### java.lang（106 个文件）
来源：`libcore/ojluni/src/main/java/java/lang/`

核心运行时类型，包括 `Object`、`Class`、`String`、`Thread`、`ClassLoader`、`System`、`Runtime`、所有包装类型（`Integer`、`Boolean` 等）和异常层次结构。

值得注意的 Android 特定修改：
- `ClassLoader.java` -- 修改为使用基于 DEX 的类加载器。
- `Thread.java` -- 与 ART 的线程管理集成。
- `System.java` -- Android 特定的属性和安全实现。

#### java.util（128 个文件）
来源：`libcore/ojluni/src/main/java/java/util/`

集合框架、并发工具、日期/时间和实用类。基本未修改自 OpenJDK。

#### java.io（88 个文件）
来源：`libcore/ojluni/src/main/java/java/io/`

I/O 流、读写器、序列化。Android 通过 `BlockGuard` 添加拦截，用于检测主线程上的 I/O 操作。

### 10.3 dalvik.system 包

**目录**: `libcore/dalvik/src/main/java/dalvik/system/`

| 类 | 用途 |
|-------|---------|
| `BaseDexClassLoader` | 所有 DEX 类加载器的基类 |
| `PathClassLoader` | 系统/应用类加载器 |
| `DexClassLoader` | 动态代码加载 |
| `InMemoryDexClassLoader` | 内存中 DEX 加载 |
| `DelegateLastClassLoader` | 委托后置加载策略 |
| `DexFile` | 底层 DEX 文件访问 |
| `DexPathList` | DEX/原生路径的有序列表 |
| `VMRuntime` | 运行时配置访问 |
| `VMDebug` | 调试和分析工具 |
| `BlockGuard` | 基于策略的 I/O 阻塞检测 |
| `CloseGuard` | 资源泄漏检测 |
| `ZygoteHooks` | Zygote 生命周期回调 |

### 10.4 libcore 内部库

**目录**: `libcore/luni/src/main/java/libcore/`

| 包 | 用途 |
|---------|---------|
| `libcore.io` | 底层 I/O（Linux 系统调用包装器、`Os` 类） |
| `libcore.net` | 网络内部（MIME 类型、URI 解析） |
| `libcore.reflect` | 反射工具 |
| `libcore.util` | 内部工具（NativeAllocationRegistry 等） |
| `libcore.timezone` | 时区数据管理 |
| `libcore.content` | 内容类型工具 |
| `libcore.icu` | ICU 国际化集成 |

**应用开发者影响**：
- `BlockGuard` 执行 `StrictMode` 磁盘/网络策略。应用看到 `NetworkOnMainThreadException` 是因为 `BlockGuard` 拦截了套接字操作。
- `CloseGuard` 在资源（流、游标、连接）被垃圾收集而未关闭时记录警告。
- `NativeAllocationRegistry`（在 `libcore.util` 中）支持正确统计与 Java 对象关联的原生内存，适时触发 GC。
- `dalvik.system.VMRuntime` 类暴露堆统计信息和控制功能，如 `setTargetHeapUtilization()`、`getTargetSdkVersion()` 和 `newNonMovableArray()`。

---

## 11. Dalvik 工具

### 11.1 组织结构

**根目录**: `~/aosp-android-11/dalvik/`

| 目录 | 用途 |
|-----------|---------|
| `dx/` | 旧版 DEX 编译器（Java 字节码到 DEX 字节码） |
| `dexgen/` | DEX 文件生成工具 |
| `tools/` | 杂项工具 |
| `opcode-gen/` | Dalvik/ART 操作码定义生成 |

注：`dx` 是旧版工具；D8/R8（在外部构建工具中）是 DEX 编译的现代替代。

---

## 12. 隐藏 API 执行

### 12.1 架构

**文件**: `art/runtime/hidden_api.h`

Android 11 对非 SDK（"隐藏"）API 实施限制以维护平台稳定性：

```cpp
enum class EnforcementPolicy {
    kDisabled = 0,    // 不检查
    kJustWarn = 1,    // 记录但允许
    kEnabled  = 2,    // 阻止深灰名单和黑名单
};

enum class AccessMethod {
    kNone       = 0,  // 内部测试
    kReflection = 1,  // Java 反射
    kJNI        = 2,  // JNI 访问
    kLinking    = 3,  // 类链接
};
```

### 12.2 域系统

**文件**: `art/runtime/base/hiddenapi_domain.h`

类被分配到不同的域：
- **平台域** -- 系统框架代码（无限制访问）
- **核心平台域** -- 核心库（即使对平台代码也有限制）
- **应用域** -- 应用代码（受隐藏 API 限制）

**应用开发者影响**：
- 目标为 Android 11+ 的应用被阻止通过反射、JNI 或直接链接访问黑名单和深灰名单 API。
- `targetSdkVersion` 决定哪些 API 受到限制；目标值越高，执行越严格。
- 即使在 `kJustWarn` 模式下也会记录违规，在 logcat 中显示为 `Accessing hidden method/field`。
- 开发者应迁移到公共 SDK 替代方案，或在可用时使用标记了 `@UnsupportedAppUsage` 的替代方案。

---

## 13. 性能影响总结

### 13.1 内存

| 因素 | 影响 | 缓解措施 |
|--------|--------|------------|
| 默认 256MB 堆限制 | 应用达到限制会 OOM | 使用 `largeHeap`，优化分配 |
| TLAB 开销（每线程 32KB） | 每线程内存 | ART 自动调整 TLAB 大小 |
| JIT 代码缓存 | 编译代码的额外内存 | 有上限，满时被 GC |
| 引导镜像共享 | 通过 Zygote 显著节省内存 | 自动 |
| 配置文件数据 | 每应用少量磁盘开销 | 由系统管理 |

### 13.2 CPU/性能

| 因素 | 影响 | 缓解措施 |
|--------|--------|------------|
| JIT 预热 | 首次启动较慢 | 配置文件引导编译 |
| GC 暂停（CC） | 通常低于 5ms | 分代 CC 减少频率 |
| 类验证 | 启动开销 | VDEX 缓存避免重新验证 |
| JNI 转换 | 每次调用开销 | 最小化 JNI 调用，使用 `@CriticalNative` |
| 读屏障 | 约 1-2% 运行时开销 | 支持并发 GC，暂停时间更低 |

### 13.3 应用开发者最佳实践

1. **最小化启动时的类数量** -- 每个加载的类都需要验证和链接。
2. **避免在热路径使用 JNI** -- 线程状态转换增加开销。对简单 JNI 方法使用 `@FastNative` 或 `@CriticalNative`。
3. **不要调用 System.gc()** -- ART 的 GC 已经过良好调优；显式 GC 很少有帮助，反而可能导致不必要的暂停。
4. **避免在循环中过度分配** -- 虽然 TLAB 分配很快，但仍会产生 GC 压力。
5. **谨慎使用 android:largeHeap** -- 增加堆大小但也增加 GC 时间。
6. **最小化静态初始化器** -- `<clinit>` 阻塞类加载，可能形成初始化循环。
7. **发布基线配置文件** -- 随 APK 提供配置文件数据以获得首日优化性能。
8. **避免对隐藏 API 使用反射** -- 在当前和未来的 Android 版本中会中断。
9. **显式关闭资源** -- `CloseGuard` 会记录警告，终结操作开销大。
10. **大数据优先使用原始数组** -- 大于 12KB 的对象进入大对象空间，具有不同的分配模式。

---

## 14. 关键架构图（文本描述）

### 14.1 方法执行流程

```
应用调用方法
    |
    v
[入口点检查]
    |
    +-- 已编译（AOT/JIT）？
    |       |
    |       v
    |   执行原生代码
    |       |
    |       v
    |   [GC 读屏障]
    |
    +-- 未编译？
            |
            v
        [解释器（mterp/nterp/switch）]
            |
            +-- 递增热度计数器
            |       |
            |       v
            |   计数器 >= warmup_threshold？
            |       |
            |       +-- 开始分析（内联缓存）
            |       |
            |       v
            |   计数器 >= compile_threshold？
            |       |
            |       v
            |   [JIT 线程：优化编译器]
            |       |
            |       v
            |   存储到 JitCodeCache
            |       |
            |       v
            |   更新方法入口点
            |
            +-- 在热循环中？
                    |
                    v
                [OSR：栈上替换]
```

### 14.2 类加载流程

```
ClassLinker::FindClass(descriptor, class_loader)
    |
    +-- 原始类型？ --> 返回缓存的原始类
    |
    +-- 已加载？ --> 在类表中 LookupClass()
    |       |
    |       v
    |   EnsureResolved() --> 返回
    |
    +-- 引导类路径？（class_loader == null）
    |       |
    |       v
    |   FindInClassPath() --> DefineClass()
    |
    +-- 数组类型？（描述符以 '[' 开头）
    |       |
    |       v
    |   CreateArrayClass()
    |
    +-- 已知类加载器类型？（PCL/DLC）
    |       |
    |       v
    |   FindClassInBaseDexClassLoader()
    |       |
    |       +-- 检查共享库
    |       +-- 搜索父加载器（父优先或委托后置）
    |       +-- 搜索自身 DexPathList
    |       |
    |       v
    |   DefineClass() --> 验证 --> 链接 --> 初始化
    |
    +-- 未知类加载器类型？
            |
            v
        在 Java 中调用 ClassLoader.loadClass()
```

---

## 15. 文件参考索引

### 核心运行时
- `art/runtime/runtime.h` / `runtime.cc` -- 运行时单例
- `art/runtime/class_linker.h` / `class_linker.cc` -- 类加载引擎
- `art/runtime/class_loader_context.cc` -- 类加载器上下文序列化
- `art/runtime/art_method.h` / `art_method.cc` -- 方法表示
- `art/runtime/art_field.h` / `art_field.cc` -- 字段表示
- `art/runtime/compiler_filter.cc` -- 编译过滤器定义

### GC
- `art/runtime/gc/heap.h` / `heap.cc` -- 堆管理
- `art/runtime/gc/collector_type.h` -- 收集器类型枚举
- `art/runtime/gc/gc_cause.h` -- GC 触发原因
- `art/runtime/gc/collector/concurrent_copying.cc` -- CC 收集器
- `art/runtime/gc/collector/mark_sweep.cc` -- 标记-清除收集器
- `art/runtime/gc/space/region_space.cc` -- 基于区域的分配空间
- `art/runtime/gc/accounting/card_table.cc` -- 写屏障跟踪

### JIT
- `art/runtime/jit/jit.h` / `jit.cc` -- JIT 编译器驱动
- `art/runtime/jit/jit_code_cache.h` -- JIT 代码存储
- `art/runtime/jit/profile_saver.h` -- 配置文件收集

### 解释器
- `art/runtime/interpreter/interpreter.cc` -- 解释器入口点
- `art/runtime/interpreter/mterp/` -- 汇编优化的解释器

### 编译器
- `art/compiler/optimizing/` -- 优化编译器
- `art/dex2oat/dex2oat.cc` -- AOT 编译工具

### DEX
- `art/libdexfile/dex/dex_file.h` -- DEX 文件格式定义
- `art/libdexfile/dex/compact_dex_file.h` -- 紧凑 DEX 格式

### JNI
- `art/runtime/jni/jni_internal.cc` -- JNI 实现
- `art/runtime/jni/java_vm_ext.cc` -- JavaVM 扩展
- `art/runtime/jni/check_jni.cc` -- JNI 调试

### 类加载器（Java）
- `libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/PathClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/DexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java`
- `libcore/dalvik/src/main/java/dalvik/system/DelegateLastClassLoader.java`

### 隐藏 API
- `art/runtime/hidden_api.h` -- 隐藏 API 执行

### 核心库
- `libcore/ojluni/src/main/java/java/lang/` -- java.lang 包（106 个文件）
- `libcore/ojluni/src/main/java/java/util/` -- java.util 包（128 个文件）
- `libcore/ojluni/src/main/java/java/io/` -- java.io 包（88 个文件）
- `libcore/dalvik/src/main/java/dalvik/system/` -- Dalvik 系统类
