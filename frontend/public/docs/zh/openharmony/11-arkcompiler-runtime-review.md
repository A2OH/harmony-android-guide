# ArkCompiler 与运行时代码审查 - OpenHarmony 4.1

## 目录

1. [概述](#概述)
2. [架构概览](#架构概览)
3. [字节码格式与类加载](#字节码格式与类加载)
4. [GC 实现](#gc-实现)
5. [JIT/AOT 编译流水线](#jitaot-编译流水线)
6. [解释器与快速路径](#解释器与快速路径)
7. [内联缓存与性能](#内联缓存与性能)
8. [内置对象实现](#内置对象实现)
9. [FFI/NAPI 桥接](#ffinapi-桥接)
10. [安全性：沙箱、字节码验证、AOT 文件完整性](#安全性)
11. [C++ 运行时代码的内存安全](#内存安全)
12. [错误处理与崩溃恢复](#错误处理)
13. [综合发现](#综合发现)

---

## 概述

ArkCompiler 运行时（`arkcompiler/`）是 OpenHarmony 中与 Android ART 对等的组件——负责执行 ArkTS/JS 字节码的引擎。它由四个主要组件构成：

- **ets_runtime**：核心 ECMAScript 运行时——堆、GC、解释器、编译器（AOT/JIT）、IC、内置对象、NAPI
- **runtime_core**：底层基础设施——Panda 文件格式、汇编器、字节码优化器、验证器
- **ets_frontend**：ArkGuard 代码混淆工具
- **toolchain**：调试器、检查器（Chrome DevTools Protocol）、构建基础设施

该运行时是一个规模庞大的 C++ 代码库（数十万行代码）。对于这一规模的项目而言，整体质量尚可，系统性地使用了写屏障、GC 验证和受检内存操作（`memcpy_s`）。然而，在内存安全、安全加固不完善以及 JIT 成熟度方面，存在一些令人担忧的模式。

**识别出的主要风险领域：**
- JIT 编译器通过 `dlopen` 加载外部 `.so`，验证极少（高危）
- Panda 字节码文件格式缺乏加密完整性验证（高危）
- 写屏障存在微妙的正确性假设，在并发修改下可能被破坏（中危）
- 堆的 `Destroy()` 对 15 个以上独立分配的子系统使用原始 `delete`，未使用 RAII（中危）
- ELF/AOT 文件解析大量依赖 `reinterpret_cast`，边界检查有限（中危）
- ecmascript 目录中有 171 个以上的 TODO/FIXME/HACK 标记，表明存在已知的技术债务（低危）

---

## 架构概览

### 组件分解

```
arkcompiler/
  ets_runtime/ecmascript/
    mem/          -- 堆、GC（半空间、部分、完全、并发、增量）
    interpreter/  -- 字节码解释器（线程化分发）、快速/慢速运行时桩
    compiler/     -- AOT 编译器（LLVM 后端）、桩构建器、电路 IR
    jit/          -- JIT 编译（动态加载 libark_jsoptimizer.so）
    ic/           -- 内联缓存（单态、多态、超多态）
    builtins/     -- 所有 ES2021+ 内置对象（Array、Map、Promise 等）
    napi/         -- JS 原生 API 桥接（jsnapi.cpp、dfx_jsnapi.cpp）
    deoptimizer/  -- 从编译代码到解释代码的去优化
    jspandafile/  -- Panda 字节码文件加载和翻译
    module/       -- ES 模块系统
    snapshot/     -- 堆快照，用于启动优化

  runtime_core/
    libpandafile/ -- Panda 文件格式定义、读取器、字节码指令编码
    assembler/    -- Panda 汇编解析器和发射器
    bytecode_optimizer/ -- 寄存器分配、字节码优化传递
    verifier/     -- 字节码验证

  ets_frontend/
    arkguard/     -- 代码混淆（重命名标识符、禁用 console/hilog）

  toolchain/
    inspector/    -- Chrome DevTools Protocol 调试器桥接
```

### 内存空间

堆被划分为以下区域：
- **SemiSpace**（年轻代）：两个半空间用于复制 GC，最小/最大容量可配置
- **OldSpace**：用于晋升对象，基于区域管理，配有收集集合
- **NonMovableSpace**：不可重定位的对象（例如 HClass 元数据）
- **HugeObjectSpace**：在专用区域中分配的大对象
- **MachineCodeSpace / HugeMachineCodeSpace**：JIT/AOT 编译代码
- **ReadOnlySpace**：不可变对象（跨上下文共享）
- **AppSpawnSpace**：在应用孵化期间创建的对象（通过 zygote fork 共享）
- **SnapshotSpace**：快照反序列化目标

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/heap.h`

---

## 字节码格式与类加载

### Panda 文件格式

**文件**：`/home/dspfac/openharmony/arkcompiler/runtime_core/libpandafile/file.h`、`file.cpp`

字节码打包在 `.abc`（Ark Bytecode）文件中，采用"Panda"文件格式。该文件包含：

- **魔数**：`PANDA\0\0\0`（8 字节）
- **校验和**：`uint32_t`（CRC 风格，非加密）
- **版本**：4 字节
- **类索引**、**方法索引**、**字段索引**、**字面量数组索引**
- **通过哈希表进行实体查找**：`EntityPairHeader` 使用描述符哈希实现 O(1) 类查找

文件可以从以下来源加载：
1. 独立的 `.abc` 文件
2. ZIP 归档文件（HAP 包）——从 `classes.abc` 条目中提取
3. 匿名内存映射（用于 zip 提取的内容）

### 发现

**[中危] .abc 文件无加密完整性验证**：Panda 文件仅使用基本校验和（`file.h` 第 62 行：`uint32_t checksum`）。没有数字签名或 HMAC 验证。带有有效校验和的篡改 `.abc` 文件将被加载并执行。虽然字节码验证器（`runtime_core/verifier/`）提供结构性验证，但无法检测语义上有效但恶意的字节码。

**[低危] 热路径中缺少字节码验证**：在 `file.cpp` 中搜索"Verify"未返回结果——文件加载期间不执行验证。验证器作为单独组件存在，但似乎是可选传递而非强制关卡。

**[信息] Zip 提取分配匿名 RWX 相邻内存**：在 `file.cpp` 第 143 行，zip 提取的字节码通过 `MapRWAnonymousRaw` 映射。这是可读可写的；内容随后被用作可执行字节码数据。这是标准模式，但值得在 W^X 策略考量中注意。

---

## GC 实现

### 架构

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/heap.cpp`、`concurrent_marker.cpp`、`barriers-inl.h`、`verification.cpp`

GC 是分代式的，支持多种收集策略：

| GC 类型 | 描述 |
|---------|------|
| **STWYoungGC** | 全停顿年轻代（半空间复制） |
| **PartialGC** | 年轻代 + 选定的老年代区域（基于 CSet） |
| **FullGC** | 全堆压缩 |
| **ConcurrentMarker** | 三色并发标记 |
| **IncrementalMarker** | 空闲时间增量标记 |
| **ConcurrentSweeper** | 后台清扫老年代空间 |
| **ParallelEvacuator** | 多线程疏散 |

### 写屏障实现

```cpp
// barriers-inl.h, line 28-49
template<WriteBarrierType writeType = WriteBarrierType::NORMAL>
static ARK_INLINE void WriteBarrier(const JSThread *thread, void *obj, size_t offset, JSTaggedType value)
{
    ASSERT(value != JSTaggedValue::VALUE_UNDEFINED);
    Region *objectRegion = Region::ObjectAddressToRange(static_cast<TaggedObject *>(obj));
    Region *valueRegion = Region::ObjectAddressToRange(reinterpret_cast<TaggedObject *>(value));
    ...
    if (!objectRegion->InYoungSpace() && valueRegion->InYoungSpace()) {
        objectRegion->InsertOldToNewRSet(slotAddr);
    }
    if (thread->IsConcurrentMarkingOrFinished()) {
        Barriers::Update(thread, slotAddr, objectRegion, ...);
    }
}
```

**[中危] 写屏障对 VALUE_UNDEFINED 的断言**：第 30 行断言 `value != JSTaggedValue::VALUE_UNDEFINED`。这意味着在屏障层面不允许向堆槽写入 `undefined`。如果任何代码路径在未检查的情况下通过 `Barriers::SetObject` 写入 undefined，在调试构建中将崩溃，在发布版本中将静默跳过屏障（因为 `ASSERT` 被编译掉）。`SynchronizedSetObject` 变体（第 68 行）有 `isPrimitive` 绕过，这是基本类型的正确路径，但这种不对称性很脆弱。

**[信息] 并发标记正确性**：并发标记器使用起始快照（SATB）方法。`WriteBarrier` 检查 `IsConcurrentMarkingOrFinished()` 并转发到 `Barriers::Update`。重新标记阶段（`ConcurrentMarker::ReMark()` 第 74 行）在等待工作线程任务后，在主线程上处理根和标记栈。这是一种经过验证的成熟模式。

### GC 验证

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/verification.cpp`

运行时包含一个全面的堆验证系统，可验证：
- 所有根槽指向存活对象
- 老年代到年轻代的记忆集位正确
- 标记位与空间归属一致
- 转发地址指向有效的 to-space 对象
- 无悬空弱引用

这是出色的防御性工程——验证覆盖了 GC 前、GC 后和每阶段的不变量。但是，它受 `ShouldVerifyHeap()` 控制，默认关闭。

### 发现

**[中危] Heap::Destroy() 使用 15 个以上的原始 delete，未使用 RAII**：

```cpp
// heap.cpp lines 142-252
void Heap::Destroy() {
    if (workManager_ != nullptr) { delete workManager_; workManager_ = nullptr; }
    if (activeSemiSpace_ != nullptr) { activeSemiSpace_->Destroy(); delete activeSemiSpace_; ... }
    // ... 还有 15 个类似的
}
```

每个 GC 子系统在 `Initialize()` 中用 `new` 单独分配，在 `Destroy()` 中单独 `delete`。如果任何析构函数抛出异常或遗漏某个 delete，资源就会泄漏。使用 `std::unique_ptr` 可以消除这一整类 bug，并将方法从 110 行减少到约 10 行。

**[低危] SmartGC 敏感状态对读取使用 relaxed 内存排序，对 CAS 使用 seq_cst**：在 `heap.h` 第 250-272 行，`sensitiveStatus_` 对加载使用 `memory_order_relaxed`，对存储使用 `memory_order_release`，但 `compare_exchange_strong` 使用 `memory_order_seq_cst`。这种混合排序并非不正确，但暗示对所需一致性保证的意图不明确。

**[信息] 空闲 GC 和应用生命周期感知**：堆对应用生命周期事件提供一流支持（`NotifyPostFork`、`SetOnSerializeEvent`、`InSensitiveStatus`）。在高敏感时段（UI 动画、序列化）期间抑制 GC。这是移动运行时的合理方法。

---

## JIT/AOT 编译流水线

### AOT（预编译）

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/compiler/aot_file/aot_file_manager.cpp`、`elf_reader.cpp`、`elf_checker.cpp`

AOT 编译生成 ELF 格式的 `.an`（Ark Native）文件，包含：
- 机器代码段（按模块）
- 函数入口描述符
- 栈映射（用于 GC 和去优化）
- 快照数据（`.ai` 文件用于常量池预初始化）

AOT 流水线使用 LLVM 作为后端编译器。IR 是自定义的"电路"IR（`compiler/circuit.h`），在代码生成前经过多个传递进行降级。

**ELF 加载**：`ElfReader::VerifyELFHeader()` 验证：
1. ELF 魔数字节
2. 版本号（严格或兼容匹配）
3. 通过 `ElfChecker::CheckValidElf()` 进行结构完整性验证

`ElfChecker`（2024 年添加）执行全面的结构验证：节头边界、字符串表完整性、字节序处理和对齐检查。

### JIT（即时编译）

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/jit/jit.h`、`jit.cpp`

**[高危] JIT 通过 dlopen 加载编译器且无完整性检查**：

```cpp
// jit.cpp lines 33-65
static const std::string LIBARK_JSOPTIMIZER = "libark_jsoptimizer.so";
libHandle_ = LoadLib(LIBARK_JSOPTIMIZER);
jitCompile_ = reinterpret_cast<bool(*)(void*, JitTask*)>(FindSymbol(libHandle_, JITCOMPILE.c_str()));
```

JIT 编译器通过 `dlopen`/`dlsym` 从 `libark_jsoptimizer.so` 动态加载。存在以下问题：
- 无路径验证（依赖默认库搜索路径）
- 无加载库的签名/哈希验证
- 对加载代码无能力限制
- 函数指针直接强制转换并调用

如果攻击者能在库搜索路径的更前面放置恶意的 `libark_jsoptimizer.so`，即可在运行时进程中获得任意代码执行能力。在具有适当 SELinux 策略的设备上可以缓解此风险，但它代表了一个根本性的信任边界违规。

**[中危] JIT 缺陷：dlsym 后检查了错误的变量**：

```cpp
// jit.cpp line 61
deleteJitCompile_ = reinterpret_cast<void(*)(void*)>(FindSymbol(libHandle_, DELETEJITCOMPILE.c_str()));
if (createJitCompiler_ == nullptr) {  // 缺陷：应检查 deleteJitCompile_
    LOG_JIT(ERROR) << "jit can't find symbol deleteJitCompile";
    return;
}
```

第 61 行赋值给 `deleteJitCompile_`，但第 62 行检查了 `createJitCompiler_`（已在第 53 行检查过）。这意味着如果 `FindSymbol` 对 `DeleteJitCompile` 返回 `nullptr`，该错误将不会被检测到，`deleteJitCompile_` 将为 `nullptr`。后续调用 `DeleteJitCompile()`（第 93 行：`deleteJitCompile_(compiler)`）将解引用空函数指针，导致崩溃。

**[低危] JIT 仅支持有限的函数类型**：仅 `NORMAL_FUNCTION`、`BASE_CONSTRUCTOR` 和 `ARROW_FUNCTION` 可被 JIT 编译。生成器函数、异步函数和其他类型回退到解释器。这对于早期阶段的 JIT 是适当的，但限制了性能收益。

**[信息] JIT 任务生命周期管理**：JIT 任务在 `compilingJitTasks_` 和 `installJitTasks_` 双端队列中跟踪。在异步模式下，任务被发送到后台线程，代码安装在主线程上进行。这避免了在代码修补期间需要细粒度同步。

### 去优化

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/deoptimizer/deoptimizer.h`、`deoptimizer.cpp`

去优化器处理当假设失效时从编译代码（AOT/JIT）回到解释器的转换。它使用栈映射从编译帧状态重建解释器帧。这是 V8 和 HotSpot 共用的标准方法。

---

## 解释器与快速路径

### 线程化解释器

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/interpreter/interpreter-inl.h`

解释器使用**计算跳转（线程化分发）**：

```cpp
#define DISPATCH(curOpcode)                                       \
    do {                                                          \
        ADVANCE_PC(BytecodeInstruction::Size(EcmaOpcode::curOpcode))  \
        opcode = READ_INST_OP(); goto *dispatchTable[opcode];     \
    } while (false)
```

这是最快的可移植分发技术（CPython、LuaJIT 等使用）。解释器为以下类型有单独的分发表：
- 普通操作码
- 抛出操作码
- 宽操作码
- 已弃用操作码
- CallRuntime 操作码
- 调试模式操作码

帧布局使用 `InterpretedFrame`、`InterpretedEntryFrame` 和 `InterpretedBuiltinFrame`，配合指针算术：

```cpp
#define GET_FRAME(CurrentSp) \
    (reinterpret_cast<InterpretedFrame *>(CurrentSp) - 1)
```

### 快速运行时桩

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/interpreter/fast_runtime_stub-inl.h`

算术运算有快速路径，检查两个操作数是否都是数字并直接执行运算，返回 `Hole` 表示回退到慢速路径：

```cpp
JSTaggedValue FastRuntimeStub::FastDiv(JSTaggedValue left, JSTaggedValue right)
{
    if (left.IsNumber() && right.IsNumber()) {
        // 处理除法，包括 NaN 和无穷大边界情况
        return JSTaggedValue(dLeft / dRight);
    }
    return JSTaggedValue::Hole();  // 慢速路径
}
```

**[信息] 除零处理正确**：`FastDiv` 正确处理除零、NaN 传播和无穷大符号（第 57 行：符号位异或）。取模快速路径（`FastMod`）也正确处理了所有 IEEE 754 边界情况。

**[信息] 相等性快速路径全面**：`FastEqual` 和 `FastStrictEqual` 直接处理数字-数字、字符串-字符串（仅扁平字符串）、undefined/null、BigInt 比较，对类型强制转换或树结构字符串回退到慢速路径。

---

## 内联缓存与性能

### IC 系统

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/ic/ic_runtime_stub-inl.h`、`ic_handler.h`、`profile_type_info.h`

IC 系统支持：
- **单态**：单个 HClass 匹配（对 HClass 的弱引用 + 处理器）
- **多态**：（HClass, 处理器）对数组（上限可配置）
- **超多态**：回退到通用查找

属性访问模式：
- `TryLoadICByName`：检查 HClass 匹配，通过处理器加载
- `TryLoadICByValue`：元素访问，带类型化数组快速路径
- `TryStoreICByName` / `TryStoreICByValue`：对应的存储操作
- `LoadGlobalICByName` / `StoreGlobalICByName`：全局变量访问

```cpp
// ic_runtime_stub-inl.h line 86
ARK_INLINE JSTaggedValue ICRuntimeStub::TryLoadICByName(JSThread *thread, JSTaggedValue receiver,
                                                        JSTaggedValue firstValue, JSTaggedValue secondValue)
{
    if (LIKELY(receiver.IsHeapObject())) {
        auto hclass = receiver.GetTaggedObject()->GetClass();
        if (LIKELY(firstValue.GetWeakReferentUnChecked() == hclass)) {
            return LoadICWithHandler(thread, receiver, receiver, secondValue);
        }
        // 多态检查
        JSTaggedValue cachedHandler = CheckPolyHClass(firstValue, hclass);
        ...
    }
}
```

**[信息] 隐藏类（HClass）系统受 V8 启发**：对象使用 `JSHClass` 作为其形状描述符。属性转换创建新的 HClass，实现 IC 单态性。`ObjectFastOperator` 提供快速属性查找，检查字典模式与线性布局。

### 对象快速操作符

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/object_fast_operator-inl.h`

`ObjectFastOperator::GetPropertyByName` 沿原型链遍历，为以下情况提供快速路径：
- 非字典对象（通过 HClass 布局进行线性属性扫描）
- 字典对象（哈希表查找）
- 查找前的字符串驻留检查（避免误匹配）

**[信息] DisallowGarbageCollection 作用域**：快速路径使用 `[[maybe_unused]] DisallowGarbageCollection noGc` 来断言原始指针操作期间不发生 GC。这是良好的防御性实践。

---

## 内置对象实现

**目录**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/builtins/`

运行时实现了完整的 ES2021+ 内置对象集，包括：
- 标准对象：Array、Object、Function、String、Number、Boolean、Date、RegExp、JSON、Math、Promise
- 集合类：Map、Set、WeakMap、WeakSet
- 类型化数组：所有 Int/Uint/Float 变体
- 国际化：Collator、DateTimeFormat、NumberFormat（在 `ARK_SUPPORT_INTL` 宏后）
- 原子操作：SharedArrayBuffer 支持
- 生成器和异步函数
- BigInt

此外，`compiler/builtins/` 中有对应的桩构建器，为常见内置对象生成优化的机器代码（例如 `builtins_array_stub_builder.cpp`、`builtins_string_stub_builder.cpp`）。这允许 AOT/JIT 内联常见操作，如 `Array.prototype.forEach`。

**[信息] 内置对象的延迟初始化**：`builtins_lazy_callback.cpp` 表明某些内置对象在首次使用时才初始化，减少了启动开销。

---

## FFI/NAPI 桥接

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/napi/jsnapi.cpp`、`include/jsnapi.h`

JSNAPI 提供原生 C/C++ 代码与 JavaScript 运行时之间的桥接。主要观察：

**[信息] 通过 CROSS_THREAD_AND_EXCEPTION_CHECK 实现线程安全**：大多数 API 入口点使用一个宏来验证当前线程上下文并在继续之前检查待处理异常：

```cpp
Local<JSValueRef> JSON::Parse(const EcmaVM *vm, Local<StringRef> string)
{
    CROSS_THREAD_AND_EXCEPTION_CHECK_WITH_RETURN(vm, JSValueRef::Undefined(vm));
    ...
}
```

**[信息] 序列化/反序列化支持**：`JSSerializer`（`js_serializer.h`）实现了跨线程/跨进程对象传输的结构化克隆。它处理全面的类型集，包括 TypedArray、SharedArrayBuffer（转移语义）、RegExp、Date、Map、Set 和原生绑定对象。

**[低危] 序列化器中的原生绑定对象**：序列化器支持带有 `DetachFunc`/`AttachFunc` 回调的 `NATIVE_BINDING_OBJECT`（`js_serializer.h` 第 37-38 行）。这些是带 `void*` 参数的原始函数指针，是 C 回调 API 的典型做法，但不提供类型安全。

---

## 安全性

### AOT/ELF 文件完整性

**[中危] ELF 文件解析使用大量 reinterpret_cast**：`elf_reader.cpp` 对内存映射文件内容执行未检查的 `reinterpret_cast`：

```cpp
llvm::ELF::Elf64_Ehdr *ehdr = reinterpret_cast<llvm::ELF::Elf64_Ehdr *>(fileMapMem_.GetOriginAddr());
llvm::ELF::Elf64_Shdr *shdr = reinterpret_cast<llvm::ELF::Elf64_Shdr *>(addr + ehdr->e_shoff);
```

虽然 `ElfChecker` 验证了结构完整性，但 `ParseELFSections` 中的解析代码信任文件中的 `e_shoff`、`e_shnum`、`sh_offset` 和 `sh_size`。精心构造的 `.an` 文件如果检查器遗漏了某个边界情况，可能导致越界读取。检查器在 2024 年添加，表明这是一个已被认识到的差距。

### Panda 文件加载

**[高危] .abc 文件无加密验证**：Panda 文件格式（`runtime_core/libpandafile/file.h`）仅使用 `uint32_t checksum` 进行完整性验证。没有代码签名或哈希验证。在攻击者可以修改应用数据目录中文件的设备上，他们可以用通过基本校验和的恶意字节码替换 `.abc` 文件。

文件头结构：
```cpp
struct Header {
    std::array<uint8_t, MAGIC_SIZE> magic;
    uint32_t checksum;
    std::array<uint8_t, VERSION_SIZE> version;
    uint32_t file_size;
    ...
};
```

### 版本验证

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/base/file_header.h`

`FileHeaderBase::VerifyVersion` 支持严格匹配和兼容匹配。AOT 文件根据运行时期望的版本进行验证。这可以防止加载不兼容的编译代码，但不是安全措施（版本字节很容易伪造）。

### 调试器安全

**文件**：`/home/dspfac/openharmony/arkcompiler/toolchain/inspector/ws_server.cpp`

调试器使用 WebSocket 连接。在 iOS 构建中，调试器通过显式的 `extern "C"` 函数（`StartDebug`、`StopDebug`、`WaitForDebugger`）加载。工具链代码似乎没有为调试器连接实现身份验证，而是依赖平台级别的访问控制（仅限 ADB/USB）。

---

## 内存安全

### 原始指针模式

**[中危] 代码库中大量使用原始 new/delete**：`Heap::Initialize()` 用 `new` 分配 15 个以上的对象，`Heap::Destroy()` 逐个 `delete`。GC 子系统的所有权管理不使用智能指针。这种模式在其他管理器中也有重复。

**[低危] ELF/AOT 代码中 reinterpret_cast 密度高**：`aot_file/` 目录包含约 18 个对内存映射文件内容的 `reinterpret_cast` 实例（根据 grep 结果）。每一个都是潜在的类型安全违规，尽管 `ElfChecker` 缓解了许多风险。

### 并发安全

**[信息] 敏感状态正确使用原子操作**：`Heap` 中的 `sensitiveStatus_` 字段使用 `std::atomic` 和适当的内存排序。`ConcurrentMarker` 使用互斥锁保护的条件变量进行线程同步。

**[中危] ConcurrentMarker 中的静态可变状态**：

```cpp
// concurrent_marker.cpp line 33-34
size_t ConcurrentMarker::taskCounts_ = 0;
Mutex ConcurrentMarker::taskCountMutex_;
```

这些静态成员在所有 `ConcurrentMarker` 实例（进程中所有 VM 实例）之间共享。如果多个 `EcmaVM` 实例并发运行（例如多上下文场景），共享的 `taskCounts_` 可能导致错误的任务限制执行。

### ASAN 支持

**文件**：`/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/base/asan_interface.h`

运行时集成了 AddressSanitizer，表明团队在开发过程中运行了 sanitizer 构建。区域空间标志被设计为避免与 ASAN 的无效值标记冲突（`region.h` 第 38-39、58-59 行的注释）。

---

## 错误处理

### 异常传播

解释器使用一致的异常检查模式：

```cpp
#define HANDLE_EXCEPTION_IF_ABRUPT_COMPLETION(_thread)    \
    do {                                                  \
        if (UNLIKELY((_thread)->HasPendingException())) { \
            INTERPRETER_GOTO_EXCEPTION_HANDLER();         \
        }                                                 \
    } while (false)
```

异常存储在 `JSThread` 对象上，在每个可能抛出异常的操作之后进行检查。`INTERPRETER_GOTO_EXCEPTION_HANDLER()` 宏保存 PC 并跳转到异常分发表条目。

### GC 失败处理

**[信息] 堆初始化失败时的致命日志**：如果堆大小太小而无法初始化老年代空间，运行时记录 FATAL 错误并终止（heap.cpp 第 97 行：`LOG_ECMA_MEM(FATAL)`）。这是适当的——在堆大小不足的情况下继续运行会导致不可预测的故障。

**[信息] 验证失败是致命的**：`Verification::VerifyAll()` 在检测到损坏时记录 FATAL（verification.cpp 第 275-276 行）。这种快速失败方法对于堆损坏是正确的——继续执行可能导致更严重的故障。

### 崩溃恢复

运行时似乎没有除标准操作系统信号处理之外的显式崩溃恢复机制。没有检查点/重启设施。这对于 JavaScript 运行时来说是典型的——崩溃恢复预期在框架层面处理（应用重启）。

---

## 综合发现

### 严重/高危

| # | 发现 | 位置 | 影响 |
|---|------|------|------|
| 1 | JIT 通过 `dlopen` 加载 `libark_jsoptimizer.so` 且无完整性验证 | `jit/jit.cpp:35-38` | 如果攻击者控制库搜索路径则可注入代码 |
| 2 | Panda 字节码文件仅使用 CRC 校验和，无加密签名 | `runtime_core/libpandafile/file.h:62` | 如果攻击者有文件写入权限则可篡改字节码 |

### 中危

| # | 发现 | 位置 | 影响 |
|---|------|------|------|
| 3 | JIT：`dlsym` 后检查了错误的变量（检查 `createJitCompiler_` 而非 `deleteJitCompile_`） | `jit/jit.cpp:61-63` | 空函数指针解引用崩溃 |
| 4 | `Heap::Destroy()` 使用 15 个以上的原始 `delete` 调用而非 RAII | `mem/heap.cpp:142-252` | 如果析构函数顺序重要或发生异常则有资源泄漏风险 |
| 5 | ELF/AOT 解析对 mmap 数据使用 `reinterpret_cast`，边界检查有限 | `compiler/aot_file/elf_reader.cpp` | 精心构造的 `.an` 文件可能导致越界读取 |
| 6 | 写屏障断言 `value != VALUE_UNDEFINED`——不确定所有调用者是否遵守 | `mem/barriers-inl.h:30` | 发布版本中对 undefined 值静默跳过屏障 |
| 7 | `ConcurrentMarker` 中的静态 `taskCounts_`/`taskCountMutex_` 在 VM 实例间共享 | `mem/concurrent_marker.cpp:33-34` | 多 VM 场景下任务限制不正确 |

### 低危

| # | 发现 | 位置 | 影响 |
|---|------|------|------|
| 8 | 171 个以上的 TODO/FIXME 标记表明未解决的技术债务 | `ecmascript/` 中的各文件 | 累积的已知问题 |
| 9 | 仅 `module_path_helper.cpp` 中就有 42 个 TODO | `module/module_path_helper.cpp` | 模块路径处理可能存在不完整的边界情况 |
| 10 | `sensitiveStatus_` 原子操作的混合内存排序 | `mem/heap.h:250-272` | 一致性保证不明确（非错误但令人困惑） |
| 11 | JIT 仅支持 10 种以上函数类型中的 3 种 | `jit/jit.cpp:78-89` | JIT 收益有限；大部分代码仍在解释执行 |

### 正面观察

| # | 观察 | 位置 |
|---|------|------|
| P1 | 覆盖所有 GC 阶段的全面 GC 验证系统 | `mem/verification.cpp` |
| P2 | ASAN 集成，区域标志设计避免冲突 | `base/asan_interface.h`、`mem/region.h` |
| P3 | 算术快速路径中全面的 IEEE 754 处理 | `interpreter/fast_runtime_stub-inl.h` |
| P4 | 快速路径中的 `DisallowGarbageCollection` 作用域守卫 | `object_fast_operator-inl.h` |
| P5 | 为 AOT 文件结构验证添加了 ElfChecker | `compiler/aot_file/elf_checker.cpp` |
| P6 | 生命周期感知 GC（动画/序列化期间抑制） | `mem/heap.h` SmartGC |
| P7 | 在整个 AOT 文件处理中一致使用 `memcpy_s` | `compiler/aot_file/*.cpp` |
| P8 | 并发标记器使用经过验证的 SATB 方法和重新标记 | `mem/concurrent_marker.cpp` |
| P9 | 所有 NAPI 入口点上的线程安全宏 | `napi/jsnapi.cpp` |
| P10 | 多态 IC 优雅降级到超多态 | `ic/ic_runtime_stub-inl.h` |
