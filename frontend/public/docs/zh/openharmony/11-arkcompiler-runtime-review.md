# ArkCompiler and Runtime 代码审查 - OpenHarmony 4.1

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Architecture Overview](#architecture-overview)
3. [Bytecode Format and Class Loading](#bytecode-format-and-class-loading)
4. [GC Implementation](#gc-implementation)
5. [JIT/AOT Compilation Pipeline](#jitaot-compilation-pipeline)
6. [Interpreter and Fast Paths](#interpreter-and-fast-paths)
7. [Inline Caches and Performance](#inline-caches-and-performance)
8. [Built-in Object Implementations](#built-in-object-implementations)
9. [FFI/NAPI Bridge](#ffinapi-bridge)
10. [Security: Sandbox, Bytecode Verification, AOT File Integrity](#security)
11. [Memory Safety in C++ Runtime Code](#memory-safety)
12. [Error Handling and Crash Recovery](#error-handling)
13. [Consolidated Findings](#consolidated-findings)

---

## 执行摘要

The ArkCompiler runtime (`arkcompiler/`) is OpenHarmony's equivalent of Android's ART -- the engine that executes ArkTS/JS bytecode. It comprises four major components:

- **ets_runtime**: The core ECMAScript runtime -- heap, GC, interpreter, compiler (AOT/JIT), IC, builtins, NAPI
- **runtime_core**: Lower-level infrastructure -- panda file format, assembler, bytecode optimizer, verifier
- **ets_frontend**: ArkGuard code obfuscation tooling
- **toolchain**: Debugger, inspector (Chrome DevTools Protocol), build infrastructure

The runtime is a substantial C++ codebase (hundreds of thousands of lines). Overall quality is reasonable for a project of this scale, with systematic use of write barriers, GC verification, and checked memory operations (`memcpy_s`). However, several patterns raise concerns around memory safety, incomplete security hardening, and JIT maturity.

**Key risk areas identified:**
- JIT compiler loads an external `.so` via `dlopen` with minimal validation (HIGH)
- Panda bytecode file format lacks cryptographic integrity verification (HIGH)
- Write barrier has a subtle correctness assumption that could break under concurrent mutation (MEDIUM)
- Heap `Destroy()` uses raw `delete` on 15+ individually allocated subsystems with no RAII (MEDIUM)
- ELF/AOT file parsing relies heavily on `reinterpret_cast` with limited bounds checking (MEDIUM)
- 171+ TODO/FIXME/HACK markers across the ecmascript directory indicate known tech debt (LOW)

---

## 架构 Overview

### Component Breakdown

```
arkcompiler/
  ets_runtime/ecmascript/
    mem/          -- Heap, GC (semi-space, partial, full, concurrent, incremental)
    interpreter/  -- Bytecode interpreter (threaded dispatch), fast/slow runtime stubs
    compiler/     -- AOT compiler (LLVM backend), stub builders, circuit IR
    jit/          -- JIT compilation (loads libark_jsoptimizer.so dynamically)
    ic/           -- Inline caches (monomorphic, polymorphic, megamorphic)
    builtins/     -- All ES2021+ built-in objects (Array, Map, Promise, etc.)
    napi/         -- JS Native API bridge (jsnapi.cpp, dfx_jsnapi.cpp)
    deoptimizer/  -- Deoptimization from compiled to interpreted code
    jspandafile/  -- Panda bytecode file loading and translation
    module/       -- ES module system
    snapshot/     -- Heap snapshot for startup optimization

  runtime_core/
    libpandafile/ -- Panda file format definition, reader, bytecode instruction encoding
    assembler/    -- Panda assembly parser and emitter
    bytecode_optimizer/ -- Register allocation, bytecode optimization passes
    verifier/     -- Bytecode verification

  ets_frontend/
    arkguard/     -- Code obfuscation (rename identifiers, disable console/hilog)

  toolchain/
    inspector/    -- Chrome DevTools Protocol debugger bridge
```

### Memory Spaces

The heap is divided into:
- **SemiSpace** (young generation): Two semi-spaces for copying GC, configurable min/max capacity
- **OldSpace**: For tenured objects, region-based with collection sets
- **NonMovableSpace**: Objects that must not be relocated (e.g., HClass metadata)
- **HugeObjectSpace**: Large objects allocated in dedicated regions
- **MachineCodeSpace / HugeMachineCodeSpace**: JIT/AOT compiled code
- **ReadOnlySpace**: Immutable objects (shared across contexts)
- **AppSpawnSpace**: For objects created during app spawn (shared via zygote fork)
- **SnapshotSpace**: Snapshot deserialization target

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/heap.h`

---

## Bytecode Format and Class Loading

### Panda File Format

**Files**: `/home/dspfac/openharmony/arkcompiler/runtime_core/libpandafile/file.h`, `file.cpp`

The bytecode is packaged in `.abc` (Ark Bytecode) files using the "Panda" file format. The file has:

- **Magic**: `PANDA\0\0\0` (8 bytes)
- **Checksum**: `uint32_t` (CRC-style, not cryptographic)
- **Version**: 4 bytes
- **Class index**, **method index**, **field index**, **literal array index**
- **Entity lookup via hash table**: `EntityPairHeader` with descriptor hash for O(1) class lookup

The file can be loaded from:
1. Standalone `.abc` files
2. ZIP archives (HAP packages) -- extracted from `classes.abc` entry
3. Anonymous memory maps (for zip-extracted content)

### Findings

**[MEDIUM] No cryptographic integrity on .abc files**: The panda file uses only a basic checksum (line 62 of `file.h`: `uint32_t checksum`). There is no digital signature or HMAC verification. A tampered `.abc` file with a valid checksum would be loaded and executed. While the bytecode verifier (`runtime_core/verifier/`) provides structural validation, it cannot detect semantically valid but malicious bytecode.

**[LOW] Missing bytecode verification in hot path**: Searching `file.cpp` for "Verify" yields no results -- verification is not performed during file loading. The verifier exists as a separate component but appears to be an optional pass rather than a mandatory gate.

**[INFO] Zip extraction allocates anonymous RWX-adjacent memory**: In `file.cpp` line 143, zip-extracted bytecode is mapped with `MapRWAnonymousRaw`. This is read-write; the content is then used as executable bytecode data. This is a standard pattern but worth noting for W^X policy considerations.

---

## GC Implementation

### Architecture

**Files**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/heap.cpp`, `concurrent_marker.cpp`, `barriers-inl.h`, `verification.cpp`

The GC is generational with multiple collection strategies:

| GC Type | 描述 |
|---------|-------------|
| **STWYoungGC** | Stop-the-world young generation (semi-space copying) |
| **PartialGC** | Young + selected old regions (CSet-based) |
| **FullGC** | Full heap compaction |
| **ConcurrentMarker** | Tri-color concurrent marking |
| **IncrementalMarker** | Idle-time incremental marking |
| **ConcurrentSweeper** | Background sweeping of old space |
| **ParallelEvacuator** | Multi-threaded evacuation |

### Write Barrier Implementation

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

**[MEDIUM] Write barrier assertion on VALUE_UNDEFINED**: Line 30 asserts `value != JSTaggedValue::VALUE_UNDEFINED`. This means writing `undefined` to a heap slot is disallowed at the barrier level. If any code path writes undefined through `Barriers::SetObject` without checking, this will crash in debug builds and silently skip the barrier in release (since `ASSERT` is compiled out). The `SynchronizedSetObject` variant (line 68) has an `isPrimitive` bypass, which is the correct path for primitives, but the asymmetry is fragile.

**[INFO] Concurrent marking correctness**: The concurrent marker uses a snapshot-at-the-beginning (SATB) approach. The `WriteBarrier` checks `IsConcurrentMarkingOrFinished()` and forwards to `Barriers::Update`. The re-mark phase (`ConcurrentMarker::ReMark()` at line 74) processes roots and mark stack on the main thread after waiting for worker tasks. This is a well-established pattern.

### GC Verification

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/mem/verification.cpp`

The runtime includes a thorough heap verification system that validates:
- All root slots point to live objects
- Old-to-new remembered set bits are correct
- Mark bits are consistent with space membership
- Forwarding addresses point to valid to-space objects
- No dangling weak references

This is excellent defensive engineering -- the verification covers pre-GC, post-GC, and per-phase invariants. However, it is gated behind `ShouldVerifyHeap()` which defaults to off.

### Findings

**[MEDIUM] Heap::Destroy() uses 15+ raw deletes with no RAII**:

```cpp
// heap.cpp lines 142-252
void Heap::Destroy() {
    if (workManager_ != nullptr) { delete workManager_; workManager_ = nullptr; }
    if (activeSemiSpace_ != nullptr) { activeSemiSpace_->Destroy(); delete activeSemiSpace_; ... }
    // ... 15 more of these
}
```

Every GC subsystem is individually `new`'d in `Initialize()` and `delete`'d in `Destroy()`. If any destructor throws or a delete is missed, resources leak. Using `std::unique_ptr` would eliminate this entire class of bugs and reduce the method from 110 lines to ~10.

**[LOW] SmartGC sensitive status uses relaxed memory ordering for read, seq_cst for CAS**: In `heap.h` lines 250-272, `sensitiveStatus_` uses `memory_order_relaxed` for loads and `memory_order_release` for stores, but `compare_exchange_strong` uses `memory_order_seq_cst`. This mixed ordering is not incorrect but suggests unclear intent about the required consistency guarantees.

**[INFO] Idle GC and app-lifecycle awareness**: The heap has first-class support for app lifecycle events (`NotifyPostFork`, `SetOnSerializeEvent`, `InSensitiveStatus`). GC is suppressed during high-sensitivity periods (UI animations, serialization). This is a sensible approach for a mobile runtime.

---

## JIT/AOT Compilation Pipeline

### AOT (Ahead-of-Time) Compilation

**Files**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/compiler/aot_file/aot_file_manager.cpp`, `elf_reader.cpp`, `elf_checker.cpp`

AOT compilation produces `.an` (Ark Native) files in ELF format containing:
- Machine code sections (per-module)
- Function entry descriptors
- Stack maps (for GC and deoptimization)
- Snapshot data (`.ai` files for constant pool pre-initialization)

The AOT pipeline uses LLVM as the backend compiler. The IR is a custom "circuit" IR (`compiler/circuit.h`) that is lowered through multiple passes before codegen.

**ELF Loading**: The `ElfReader::VerifyELFHeader()` validates:
1. ELF magic bytes
2. Version number (strict or compatible match)
3. Structural integrity via `ElfChecker::CheckValidElf()`

The `ElfChecker` (added in 2024) performs thorough structural validation: section header bounds, string table integrity, byte order handling, and alignment checks.

### JIT (Just-in-Time) Compilation

**Files**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/jit/jit.h`, `jit.cpp`

**[HIGH] JIT loads compiler via dlopen with no integrity check**:

```cpp
// jit.cpp lines 33-65
static const std::string LIBARK_JSOPTIMIZER = "libark_jsoptimizer.so";
libHandle_ = LoadLib(LIBARK_JSOPTIMIZER);
jitCompile_ = reinterpret_cast<bool(*)(void*, JitTask*)>(FindSymbol(libHandle_, JITCOMPILE.c_str()));
```

The JIT compiler is loaded dynamically from `libark_jsoptimizer.so` using `dlopen`/`dlsym`. There is:
- No path validation (relies on default library search path)
- No signature/hash verification of the loaded library
- No capability restriction on the loaded code
- Function pointers are cast and invoked directly

If an attacker can place a malicious `libark_jsoptimizer.so` earlier in the library search path, they gain arbitrary code execution in the runtime process. On a device with proper SELinux policies this is mitigated, but it represents a fundamental trust boundary violation.

**[MEDIUM] JIT bug: wrong variable checked after dlsym**:

```cpp
// jit.cpp line 61
deleteJitCompile_ = reinterpret_cast<void(*)(void*)>(FindSymbol(libHandle_, DELETEJITCOMPILE.c_str()));
if (createJitCompiler_ == nullptr) {  // BUG: should check deleteJitCompile_
    LOG_JIT(ERROR) << "jit can't find symbol deleteJitCompile";
    return;
}
```

Line 61 assigns to `deleteJitCompile_` but line 62 checks `createJitCompiler_` (which was already checked on line 53). This means if `FindSymbol` returns `nullptr` for `DeleteJitCompile`, the error goes undetected and `deleteJitCompile_` will be `nullptr`. Later calls to `DeleteJitCompile()` (line 93: `deleteJitCompile_(compiler)`) will dereference a null function pointer, causing a crash.

**[LOW] JIT supports limited function kinds**: Only `NORMAL_FUNCTION`, `BASE_CONSTRUCTOR`, and `ARROW_FUNCTION` are JIT-compilable. Generators, async functions, and other kinds fall back to the interpreter. This is appropriate for an early-stage JIT but limits performance benefits.

**[INFO] JIT task lifecycle management**: JIT tasks are tracked in `compilingJitTasks_` and `installJitTasks_` deques. In async mode, tasks are posted to a background thread and code installation happens on the main thread. This avoids the need for fine-grained synchronization during code patching.

### Deoptimization

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/deoptimizer/deoptimizer.h`, `deoptimizer.cpp`

The deoptimizer handles transitions from compiled (AOT/JIT) code back to the interpreter when assumptions are invalidated. It reconstructs interpreter frames from the compiled frame state using stack maps. This is a standard approach shared with V8 and HotSpot.

---

## Interpreter and Fast Paths

### Threaded Interpreter

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/interpreter/interpreter-inl.h`

The interpreter uses **computed goto (threaded dispatch)**:

```cpp
#define DISPATCH(curOpcode)                                       \
    do {                                                          \
        ADVANCE_PC(BytecodeInstruction::Size(EcmaOpcode::curOpcode))  \
        opcode = READ_INST_OP(); goto *dispatchTable[opcode];     \
    } while (false)
```

This is the fastest portable dispatch technique (used by CPython, LuaJIT, etc.). The interpreter has separate dispatch tables for:
- Normal opcodes
- Throw opcodes
- Wide opcodes
- Deprecated opcodes
- CallRuntime opcodes
- Debug mode opcodes

The frame layout uses `InterpretedFrame`, `InterpretedEntryFrame`, and `InterpretedBuiltinFrame` with pointer arithmetic:

```cpp
#define GET_FRAME(CurrentSp) \
    (reinterpret_cast<InterpretedFrame *>(CurrentSp) - 1)
```

### Fast Runtime Stubs

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/interpreter/fast_runtime_stub-inl.h`

Arithmetic operations have fast paths that check both operands are numbers and perform the operation directly, returning `Hole` to indicate fallback to the slow path:

```cpp
JSTaggedValue FastRuntimeStub::FastDiv(JSTaggedValue left, JSTaggedValue right)
{
    if (left.IsNumber() && right.IsNumber()) {
        // Handle division, including NaN and infinity edge cases
        return JSTaggedValue(dLeft / dRight);
    }
    return JSTaggedValue::Hole();  // Slow path
}
```

**[INFO] Division-by-zero handling is correct**: `FastDiv` properly handles division by zero, NaN propagation, and sign-of-infinity (line 57: XOR of sign bits). The modulo fast path (`FastMod`) also correctly handles all IEEE 754 edge cases.

**[INFO] Equality fast paths are thorough**: `FastEqual` and `FastStrictEqual` handle number-number, string-string (flat only), undefined/null, BigInt comparisons directly, falling back to the slow path for type coercion or tree-structured strings.

---

## Inline Caches and Performance

### IC System

**Files**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/ic/ic_runtime_stub-inl.h`, `ic_handler.h`, `profile_type_info.h`

The IC system supports:
- **Monomorphic**: Single HClass match (weak reference to HClass + handler)
- **Polymorphic**: Array of (HClass, handler) pairs (up to a configurable limit)
- **Megamorphic**: Falls back to generic lookup

Property access patterns:
- `TryLoadICByName`: Checks HClass match, loads via handler
- `TryLoadICByValue`: Element access with typed array fast paths
- `TryStoreICByName` / `TryStoreICByValue`: Corresponding store operations
- `LoadGlobalICByName` / `StoreGlobalICByName`: Global variable access

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
        // Polymorphic check
        JSTaggedValue cachedHandler = CheckPolyHClass(firstValue, hclass);
        ...
    }
}
```

**[INFO] Hidden class (HClass) system is V8-inspired**: Objects use `JSHClass` as their shape descriptor. Property transitions create new HClasses, enabling IC monomorphism. The `ObjectFastOperator` provides fast property lookup that checks dictionary mode vs. linear layout.

### Object Fast Operator

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/object_fast_operator-inl.h`

The `ObjectFastOperator::GetPropertyByName` walks the prototype chain with fast paths for:
- Non-dictionary objects (linear property scan via HClass layout)
- Dictionary objects (hash table lookup)
- String interning check before lookup (avoids false misses)

**[INFO] DisallowGarbageCollection scope**: Fast paths use `[[maybe_unused]] DisallowGarbageCollection noGc` to assert no GC occurs during raw pointer operations. This is a good defensive practice.

---

## Built-in Object Implementations

**Directory**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/builtins/`

The runtime implements the full ES2021+ built-in set, including:
- Standard: Array, Object, Function, String, Number, Boolean, Date, RegExp, JSON, Math, Promise
- Collections: Map, Set, WeakMap, WeakSet
- Typed Arrays: All Int/Uint/Float variants
- Internationalization: Collator, DateTimeFormat, NumberFormat (behind `ARK_SUPPORT_INTL`)
- Atomics: SharedArrayBuffer support
- Generators and Async Functions
- BigInt

Additionally, there are stub builder counterparts in `compiler/builtins/` that generate optimized machine code for common builtins (e.g., `builtins_array_stub_builder.cpp`, `builtins_string_stub_builder.cpp`). This allows AOT/JIT to inline common operations like `Array.prototype.forEach`.

**[INFO] Lazy initialization of builtins**: `builtins_lazy_callback.cpp` suggests some builtins are initialized on first use, reducing startup cost.

---

## FFI/NAPI Bridge

**Files**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/napi/jsnapi.cpp`, `include/jsnapi.h`

The JSNAPI provides the bridge between native C/C++ code and the JavaScript runtime. Key observations:

**[INFO] Thread safety via CROSS_THREAD_AND_EXCEPTION_CHECK**: Most API entry points use a macro that validates the current thread context and checks for pending exceptions before proceeding:

```cpp
Local<JSValueRef> JSON::Parse(const EcmaVM *vm, Local<StringRef> string)
{
    CROSS_THREAD_AND_EXCEPTION_CHECK_WITH_RETURN(vm, JSValueRef::Undefined(vm));
    ...
}
```

**[INFO] Serialization/Deserialization support**: The `JSSerializer` (`js_serializer.h`) implements structured clone for cross-thread/cross-process object transfer. It handles a comprehensive set of types including TypedArrays, SharedArrayBuffers (transfer semantics), RegExp, Date, Map, Set, and native binding objects.

**[LOW] Native binding objects in serializer**: The serializer supports `NATIVE_BINDING_OBJECT` with `DetachFunc`/`AttachFunc` callbacks (line 37-38 of `js_serializer.h`). These are raw function pointers with `void*` parameters, typical of C callback APIs but offering no type safety.

---

## Security

### AOT/ELF File Integrity

**[MEDIUM] ELF file parsing uses extensive reinterpret_cast**: The `elf_reader.cpp` performs unchecked `reinterpret_cast` on memory-mapped file content:

```cpp
llvm::ELF::Elf64_Ehdr *ehdr = reinterpret_cast<llvm::ELF::Elf64_Ehdr *>(fileMapMem_.GetOriginAddr());
llvm::ELF::Elf64_Shdr *shdr = reinterpret_cast<llvm::ELF::Elf64_Shdr *>(addr + ehdr->e_shoff);
```

While `ElfChecker` validates structural integrity, the parsing code in `ParseELFSections` trusts `e_shoff`, `e_shnum`, `sh_offset`, and `sh_size` from the file. A crafted `.an` file could potentially cause out-of-bounds reads if the checker misses an edge case. The checker was added in 2024, suggesting this was a recognized gap.

### Panda File Loading

**[HIGH] No cryptographic verification of .abc files**: The panda file format (`runtime_core/libpandafile/file.h`) uses only a `uint32_t checksum` for integrity. There is no code signing or hash verification. On a device where an attacker can modify files in the app's data directory, they could replace `.abc` files with malicious bytecode that passes the basic checksum.

The file header structure:
```cpp
struct Header {
    std::array<uint8_t, MAGIC_SIZE> magic;
    uint32_t checksum;
    std::array<uint8_t, VERSION_SIZE> version;
    uint32_t file_size;
    ...
};
```

### Version Verification

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/base/file_header.h`

The `FileHeaderBase::VerifyVersion` supports both strict and compatible matching. AOT files are verified against the runtime's expected version. This prevents loading incompatible compiled code but is not a security measure (version bytes are easily forged).

### Debugger Security

**File**: `/home/dspfac/openharmony/arkcompiler/toolchain/inspector/ws_server.cpp`

The debugger uses WebSocket connections. On iOS builds, the debugger is loaded via explicit `extern "C"` functions (`StartDebug`, `StopDebug`, `WaitForDebugger`). The toolchain code does not appear to implement authentication for debugger connections, relying instead on platform-level access control (ADB/USB only).

---

## Memory Safety

### Raw Pointer Patterns

**[MEDIUM] Extensive use of raw new/delete throughout the codebase**: The `Heap::Initialize()` allocates 15+ objects with `new` and `Heap::Destroy()` deletes them individually. Smart pointers are not used for GC subsystem ownership. This pattern is repeated in other managers.

**[LOW] reinterpret_cast density in ELF/AOT code**: The `aot_file/` directory contains ~18 instances of `reinterpret_cast` on memory-mapped file content (as shown by grep results). Each is a potential type-safety violation, though the `ElfChecker` mitigates many risks.

### Concurrency Safety

**[INFO] Atomic operations used correctly for sensitive status**: The `sensitiveStatus_` field in `Heap` uses `std::atomic` with appropriate memory orderings. The `ConcurrentMarker` uses mutex-protected condition variables for thread synchronization.

**[MEDIUM] Static mutable state in ConcurrentMarker**:

```cpp
// concurrent_marker.cpp line 33-34
size_t ConcurrentMarker::taskCounts_ = 0;
Mutex ConcurrentMarker::taskCountMutex_;
```

These static members are shared across all `ConcurrentMarker` instances (all VM instances in the process). If multiple `EcmaVM` instances run concurrently (e.g., in a multi-context scenario), the shared `taskCounts_` could cause incorrect task limit enforcement.

### ASAN Support

**File**: `/home/dspfac/openharmony/arkcompiler/ets_runtime/ecmascript/base/asan_interface.h`

The runtime has AddressSanitizer integration, suggesting the team runs sanitizer builds during development. Region space flags are designed to avoid conflicting with ASAN's invalid value markers (comments in `region.h` lines 38-39, 58-59).

---

## Error Handling

### Exception Propagation

The interpreter uses a consistent pattern for exception checking:

```cpp
#define HANDLE_EXCEPTION_IF_ABRUPT_COMPLETION(_thread)    \
    do {                                                  \
        if (UNLIKELY((_thread)->HasPendingException())) { \
            INTERPRETER_GOTO_EXCEPTION_HANDLER();         \
        }                                                 \
    } while (false)
```

Exceptions are stored on the `JSThread` object and checked after each potentially-throwing operation. The `INTERPRETER_GOTO_EXCEPTION_HANDLER()` macro saves the PC and jumps to the exception dispatch table entry.

### GC Failure Handling

**[INFO] Fatal logging on heap init failure**: If the heap size is too small to initialize old space, the runtime logs a FATAL error and terminates (heap.cpp line 97: `LOG_ECMA_MEM(FATAL)`). This is appropriate -- continuing with an under-sized heap would cause unpredictable failures.

**[INFO] Verification failures are fatal**: The `Verification::VerifyAll()` logs FATAL if corruptions are detected (verification.cpp line 275-276). This fail-fast approach is correct for heap corruption -- continuing execution would likely cause worse failures.

### Crash Recovery

The runtime does not appear to have explicit crash recovery mechanisms beyond standard OS signal handling. There is no checkpoint/restart facility. This is typical for a JavaScript runtime -- crash recovery is expected to be handled at the framework level (app restart).

---

## Consolidated Findings

### Critical / High Severity

| # | Finding | Location | 影响 |
|---|---------|----------|--------|
| 1 | JIT loads `libark_jsoptimizer.so` via `dlopen` with no integrity verification | `jit/jit.cpp:35-38` | Code injection if attacker controls library search path |
| 2 | Panda bytecode files use only CRC checksum, no cryptographic signing | `runtime_core/libpandafile/file.h:62` | Bytecode tampering if attacker has file write access |

### Medium Severity

| # | Finding | Location | 影响 |
|---|---------|----------|--------|
| 3 | JIT: wrong variable checked after `dlsym` (checks `createJitCompiler_` instead of `deleteJitCompile_`) | `jit/jit.cpp:61-63` | Null function pointer dereference crash |
| 4 | `Heap::Destroy()` uses 15+ individual raw `delete` calls instead of RAII | `mem/heap.cpp:142-252` | Resource leak risk if destructor order matters or exception occurs |
| 5 | ELF/AOT parsing uses `reinterpret_cast` on mmap'd data with limited bounds checking | `compiler/aot_file/elf_reader.cpp` | Potential OOB read from crafted `.an` file |
| 6 | Write barrier asserts `value != VALUE_UNDEFINED` -- unclear if all callers comply | `mem/barriers-inl.h:30` | Silent barrier skip in release builds for undefined values |
| 7 | Static `taskCounts_`/`taskCountMutex_` in `ConcurrentMarker` shared across VM instances | `mem/concurrent_marker.cpp:33-34` | Incorrect task limiting in multi-VM scenarios |

### Low Severity

| # | Finding | Location | 影响 |
|---|---------|----------|--------|
| 8 | 171+ TODO/FIXME markers indicate unresolved technical debt | Various files across `ecmascript/` | Accumulated known issues |
| 9 | 42 TODOs in `module_path_helper.cpp` alone | `module/module_path_helper.cpp` | Module path handling may have incomplete edge cases |
| 10 | Mixed memory orderings on `sensitiveStatus_` atomic | `mem/heap.h:250-272` | Unclear consistency guarantees (not incorrect but confusing) |
| 11 | JIT supports only 3 of 10+ function kinds | `jit/jit.cpp:78-89` | Limited JIT benefit; most code stays interpreted |

### Positive Observations

| # | Observation | Location |
|---|-------------|----------|
| P1 | Comprehensive GC verification system covering all GC phases | `mem/verification.cpp` |
| P2 | ASAN integration with region flags designed to avoid conflict | `base/asan_interface.h`, `mem/region.h` |
| P3 | Thorough IEEE 754 handling in arithmetic fast paths | `interpreter/fast_runtime_stub-inl.h` |
| P4 | `DisallowGarbageCollection` scope guards in fast paths | `object_fast_operator-inl.h` |
| P5 | ElfChecker added for structural validation of AOT files | `compiler/aot_file/elf_checker.cpp` |
| P6 | Lifecycle-aware GC (suppressed during animations/serialization) | `mem/heap.h` SmartGC |
| P7 | Consistent use of `memcpy_s` throughout AOT file handling | `compiler/aot_file/*.cpp` |
| P8 | Concurrent marker uses proven SATB approach with re-marking | `mem/concurrent_marker.cpp` |
| P9 | Thread safety macros on all NAPI entry points | `napi/jsnapi.cpp` |
| P10 | Polymorphic IC with graceful degradation to megamorphic | `ic/ic_runtime_stub-inl.h` |
