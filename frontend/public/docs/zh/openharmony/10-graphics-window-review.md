# OpenHarmony 4.1 图形与窗口管理子系统审查

## 目录

- [1. 执行摘要](#1-执行摘要)
- [2. 架构概述](#2-架构概述)
- [3. 发现：Surface 缓冲区管理](#3-发现surface-缓冲区管理)
  - [3.1 DoFlushBuffer 中的 Usage 截断 (高危)](#31-doflushbuffer-中的-usage-截断-高危)
  - [3.2 GetLastFlushedBuffer 缺少互斥锁保护 (高危)](#32-getlastflushedbuffer-缺少互斥锁保护-高危)
  - [3.3 未初始化的 lastFlusedSequence_ (高危)](#33-未初始化的-lastflusedsequence_-高危)
  - [3.4 DumpToFile 安全问题：可向 /data 写入任意文件 (中危)](#34-dumptofile-安全问题可向-data-写入任意文件-中危)
  - [3.5 IsSupportedAlloc 返回模拟/硬编码数据 (中危)](#35-issupportedalloc-返回模拟硬编码数据-中危)
  - [3.6 SurfaceBufferImpl 序列号生成：手动加锁 (低危)](#36-surfacebufferimpl-序列号生成手动加锁-低危)
  - [3.7 成员名称拼写错误：lastFlused vs lastFlushed (低危)](#37-成员名称拼写错误lastflused-vs-lastflushed-低危)
- [4. 发现：NativeWindow NDK API](#4-发现nativewindow-ndk-api)
  - [4.1 NativeWindowFlushBuffer 忽略返回值 (中危)](#41-nativewindowflushbuffer-忽略返回值-中危)
  - [4.2 GetLastFlushedBuffer 失败路径的内存泄漏 (中危)](#42-getlastflushedbuffer-失败路径的内存泄漏-中危)
  - [4.3 NativeWindowHandleOpt 始终返回成功 (中危)](#43-nativewindowhandleopt-始终返回成功-中危)
  - [4.4 公共 API 名称拼写错误：DestoryNativeWindow (低危)](#44-公共-api-名称拼写错误destorynativewindow-低危)
  - [4.5 NativeWindow 操作缺乏线程安全 (中危)](#45-nativewindow-操作缺乏线程安全-中危)
  - [4.6 NativeWindowHandleOpt va_arg 缺少边界验证 (中危)](#46-nativewindowhandleopt-va_arg-缺少边界验证-中危)
- [5. 发现：窗口管理器](#5-发现窗口管理器)
  - [5.1 静态全局 Map 缺乏一致的锁保护 (高危)](#51-静态全局-map-缺乏一致的锁保护-高危)
  - [5.2 WindowImpl::Snapshot 缺少权限检查 (高危)](#52-windowimplsnapshot-缺少权限检查-高危)
  - [5.3 全局构造/析构计数器非线程安全 (低危)](#53-全局构造析构计数器非线程安全-低危)
  - [5.4 Destroy 在服务端失败后继续清理 (中危)](#54-destroy-在服务端失败后继续清理-中危)
  - [5.5 VsyncStation::Init 在 Receiver 创建时进入无限循环 (高危)](#55-vsyncstationinit-在-receiver-创建时进入无限循环-高危)
  - [5.6 WindowImpl 与 WindowSessionImpl 的代码重复 (低危)](#56-windowimpl-与-windowsessionimpl-的代码重复-低危)
- [6. 发现：合成器与 Fence 管理](#6-发现合成器与-fence-管理)
  - [6.1 SyncFence 生命周期管理 (低危)](#61-syncfence-生命周期管理-低危)
  - [6.2 BufferQueueProducer IPC 处理程序缺少调用者验证 (中危)](#62-bufferqueueproducer-ipc-处理程序缺少调用者验证-中危)
- [7. 发现：API 设计](#7-发现api-设计)
  - [7.1 NativeWindowOperation 枚举稳定性风险 (中危)](#71-nativewindowoperation-枚举稳定性风险-中危)
  - [7.2 Surface 类型定义：C++ 命名空间中的 C 风格 (低危)](#72-surface-类型定义c-命名空间中的-c-风格-低危)
- [8. 汇总表](#8-汇总表)

---

## 1. 执行摘要

本审查涵盖 OpenHarmony 4.1 Release 图形 Surface 子系统（`foundation/graphic/graphic_surface/`）和窗口管理器（`foundation/window/window_manager/`）。审查发现了 **20 个问题**，涉及安全性、线程安全、正确性、API 设计和代码质量。

关键高危问题包括：
- `DoFlushBuffer` 中的 64 位到 32 位截断，静默禁用了使用高位 usage 标志的缓冲区的 CPU 缓存刷新
- `GetLastFlushedBuffer` 缺少互斥锁保护导致数据竞争
- 未初始化的 `lastFlusedSequence_` 在首次访问时导致未定义行为
- VsyncStation 初始化中如果 VSyncReceiver 创建失败会进入无限循环
- 窗口截屏功能缺少权限检查

代码库在缓冲区状态机管理和生产者/消费者隔离方面表现出良好的通用实践，但在线程安全规范和 NDK API 健壮性方面存在明显不足。

---

## 2. 架构概述

图形和窗口管理子系统遵循受 Android SurfaceFlinger 启发的生产者-消费者缓冲区队列模式：

- **BufferQueue**：核心缓冲区生命周期管理（Request -> Flush -> Acquire -> Release）
- **BufferQueueProducer / BufferQueueConsumer**：支持 IPC 的生产者和消费者端点
- **NativeWindow (NDK)**：面向应用开发者的 C API，使用 `OHNativeWindow` / `OHNativeWindowBuffer`
- **SyncFence**：基于 Linux sync_file 的 GPU/显示同步原语
- **WindowImpl / WindowSessionImpl**：客户端窗口管理及生命周期回调
- **VsyncStation**：VSync 信号分发到窗口渲染

相关文件：
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue.cpp`
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue_producer.cpp`
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_manager.cpp`
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/surface_buffer_impl.cpp`
- `/home/dspfac/openharmony/foundation/graphic/graphic_surface/interfaces/inner_api/surface/external_window.h`
- `/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_impl.cpp`
- `/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_session_impl.cpp`
- `/home/dspfac/openharmony/foundation/window/window_manager/wm/src/vsync_station.cpp`

---

## 3. 发现：Surface 缓冲区管理

### 3.1 DoFlushBuffer 中的 Usage 截断 (高危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue.cpp`，第 498 行

```cpp
uint64_t usage = static_cast<uint32_t>(bufferQueueCache_[sequence].config.usage);
if (usage & BUFFER_USAGE_CPU_WRITE) {
```

`config.usage` 字段是 `uint64_t`，但被强制转换为 `uint32_t` 后再赋值回 `uint64_t`。这会静默地将高 32 位清零。由于 `BUFFER_USAGE_CPU_WRITE` 是 `(1ULL << 1)`，它恰好在低 32 位中可以正常工作，但此截断会屏蔽任何厂商私有 usage 标志（`BUFFER_USAGE_VENDOR_PRI0` 到 `BUFFER_USAGE_VENDOR_PRI19`，第 44-63 位）。如果厂商扩展添加了暗示 CPU 写入语义的 usage 标志，缓存刷新将被跳过，导致屏幕上可见的损坏或陈旧数据。

**建议**：移除 `static_cast<uint32_t>`，直接使用 `uint64_t` 值。

---

### 3.2 GetLastFlushedBuffer 缺少互斥锁保护 (高危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue.cpp`，第 422-442 行

`BufferQueue::GetLastFlushedBuffer` 访问 `bufferQueueCache_`、`lastFlusedSequence_`、`lastFlusedFence_` 和 `lastFlushedTransform_` 时未持有 `mutex_`。相比之下，`DoFlushBuffer`（写入这些字段）持有互斥锁。这造成了数据竞争：并发的 `DoFlushBuffer` 可能在 `GetLastFlushedBuffer` 读取缓存时修改 `lastFlusedSequence_`，导致 TOCTOU 问题，即序列号在 `find()` 时有效但在访问前条目被删除。

**建议**：在 `GetLastFlushedBuffer` 顶部获取 `std::lock_guard<std::mutex> lockGuard(mutex_)`。

---

### 3.3 未初始化的 lastFlusedSequence_ (高危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/include/buffer_queue.h`，第 190 行

```cpp
uint32_t lastFlusedSequence_;
```

此成员在构造函数或类声明中未初始化。如果在任何缓冲区被刷新之前调用 `GetLastFlushedBuffer`，`lastFlusedSequence_` 包含不确定的值。`find()` 调用很可能优雅地失败（返回 `GSERROR_NO_ENTRY`），但依赖未定义行为实现不崩溃路径是脆弱的。同样，`lastFlusedFence_`（第 191 行）也未初始化，意味着 fence 指针可能是悬空的。

**建议**：在成员初始化列表中将 `lastFlusedSequence_` 初始化为 `UINT32_MAX`（或哨兵值），将 `lastFlusedFence_` 初始化为 `SyncFence::INVALID_FENCE`。

---

### 3.4 DumpToFile 安全问题：可向 /data 写入任意文件 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue.cpp`，第 445-471 行

如果目录 `/data/bq_dump` 存在，`DumpToFile` 函数会将原始 Surface 缓冲区内容写入 `/data/bq_*`。虽然这看起来是一个调试功能（由目录存在性检查保护），但存在安全隐患：

1. 文件路径由进程 PID、Surface 名称（`name_`）和时间戳构造。Surface 名称由调用者控制，可能包含路径遍历字符（`../`），使写入能够到达 `/data/` 之外的位置。
2. 缓冲区内容（可能包括摄像头帧、DRM 保护内容或私有 UI 内容）以明文写入磁盘。
3. 没有对谁触发此代码路径进行权限检查。

该函数在生产中未被调用（在 `DoFlushBuffer` 第 514 行被注释掉），但其存在于编译后的二进制文件中是一个风险。

**建议**：在发布版本中完全移除 `DumpToFile`，或对 `name_` 进行清理以防止路径遍历，并添加编译时保护（`#ifdef DEBUG_DUMP`）。

---

### 3.5 IsSupportedAlloc 返回模拟/硬编码数据 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_manager.cpp`，第 228-243 行

```cpp
GSError BufferManager::IsSupportedAlloc(const std::vector<BufferVerifyAllocInfo> &infos,
                                        std::vector<bool> &supporteds)
{
    CHECK_INIT();
    // mock data
    supporteds.clear();
    for (uint32_t index = 0; index < infos.size(); index++) {
        if (infos[index].format == GRAPHIC_PIXEL_FMT_RGBA_8888 ||
            infos[index].format == GRAPHIC_PIXEL_FMT_YCRCB_420_SP) {
            supporteds.push_back(true);
        } else {
            supporteds.push_back(false);
        }
    }
    return GSERROR_OK;
}
```

这被明确标记为"mock data"，仅报告两种格式为受支持。任何调用此函数检查硬件能力的应用或框架将收到不正确的结果。该函数应委托给 HAL（`g_displayBuffer`）但实际上没有。

**建议**：实现实际的 HAL 委托，或至少记录警告表明结果是模拟的。

---

### 3.6 SurfaceBufferImpl 序列号生成：手动加锁 (低危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/surface_buffer_impl.cpp`，第 101-111 行

```cpp
SurfaceBufferImpl::SurfaceBufferImpl()
{
    {
        static std::mutex mutex;
        mutex.lock();
        static uint32_t sequence_number_ = 0;
        sequenceNumber_ = sequence_number_++;
        mutex.unlock();
    }
```

使用手动 `lock()`/`unlock()` 而非 `std::lock_guard`。如果 `sequence_number_++` 抛出异常（对整数不会，但模式不好），互斥锁将泄漏。更重要的是，这可以简单地使用 `std::atomic<uint32_t>`，就像 buffer_queue.cpp 中的 `GetUniqueIdImpl()` 那样，完全避免互斥锁。

**建议**：使用 `static std::atomic<uint32_t>` 替代 `sequence_number_` 并移除互斥锁。

---

### 3.7 成员名称拼写错误：lastFlused vs lastFlushed (低危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/include/buffer_queue.h`，第 190-191 行

```cpp
uint32_t lastFlusedSequence_;
sptr<SyncFence> lastFlusedFence_;
```

这些拼写错误（缺少 'h' —— "Flused" 而非 "Flushed"）。紧接着下一行使用的是正确拼写的 `lastFlushedTransform_`。这种不一致使代码更难搜索和维护。

**建议**：重命名为 `lastFlushedSequence_` 和 `lastFlushedFence_` 以保持一致。

---

## 4. 发现：NativeWindow NDK API

### 4.1 NativeWindowFlushBuffer 忽略返回值 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`，第 168 行

```cpp
window->surface->FlushBuffer(buffer->sfbuffer, acquireFence, config);
```

`FlushBuffer` 的返回值被丢弃。该函数始终向调用者返回 `GSERROR_OK`，即使底层 Surface 拒绝了缓冲区（例如无效状态、无消费者）。这意味着使用 NDK API 的应用开发者无法检测刷新失败。

**建议**：捕获并将 `FlushBuffer` 结果返回给调用者。

---

### 4.2 GetLastFlushedBuffer 失败路径的内存泄漏 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`，第 186-197 行

```cpp
OHNativeWindowBuffer *nwBuffer = new OHNativeWindowBuffer();
...
int32_t ret = window->surface->GetLastFlushedBuffer(nwBuffer->sfbuffer, acquireFence, matrix);
if (ret != OHOS::GSError::GSERROR_OK || nwBuffer->sfbuffer == nullptr) {
    BLOGE("GetLastFlushedBuffer fail");
    return ret;  // nwBuffer 在此处泄漏
}
```

在错误路径上，`nwBuffer` 通过 `new` 分配但从未释放。由于 `NativeObjectReference` 尚未调用，引用计数尚未生效。每次失败调用都会泄漏内存。

**建议**：在错误返回前添加 `delete nwBuffer;`，或使用 `sptr` 进行自动清理。

---

### 4.3 NativeWindowHandleOpt 始终返回成功 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`，第 299-310 行

```cpp
int32_t NativeWindowHandleOpt(OHNativeWindow *window, int code, ...)
{
    if (window == nullptr) {
        return OHOS::GSERROR_INVALID_ARGUMENTS;
    }
    va_list args;
    va_start(args, code);
    InternalHandleNativeWindowOpt(window, code, args);
    va_end(args);
    return OHOS::GSERROR_OK;  // 始终返回成功
}
```

`InternalHandleNativeWindowOpt` 的返回值也始终是 `GSERROR_OK`（第 296 行）。未知/不支持的操作码静默地通过 `default: break;` 分支而不报错。传递无效码的应用会得到成功返回，掩盖了缺陷。

**建议**：对无法识别的操作码返回错误。

---

### 4.4 公共 API 名称拼写错误：DestoryNativeWindow (低危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`，第 57 行

```cpp
void DestoryNativeWindow(OHNativeWindow *window)
```

内部函数名为 "Destory"（"Destroy" 的拼写错误）。虽然公共 API 符号正确命名为 `OH_NativeWindow_DestroyNativeWindow`（通过 `WEAK_ALIAS`），但内部函数名会给维护者造成困惑。此问题自 API level 8 以来一直存在。

**建议**：在未来的清理中重命名为 `DestroyNativeWindow`。

---

### 4.5 NativeWindow 操作缺乏线程安全 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`

`NativeWindow` 结构体（定义在 `native_window.h` 中）包含一个 `config` 结构体和 `bufferCache_` 映射，在 `NativeWindowRequestBuffer`、`NativeWindowFlushBuffer` 和 `NativeWindowHandleOpt` 中被读写，但没有任何同步。在多线程渲染上下文中（例如渲染线程上的生产者、UI 线程上的配置更改），这会造成数据竞争。

底层 `Surface` 对象有自己的互斥锁，但 NativeWindow 包装层不保护其局部状态（`config`、`bufferCache_`、`uiTimestamp`、`lastBufferSeqNum`）。

**建议**：为 `NativeWindow` 添加互斥锁以保护其成员，或记录 NativeWindow 操作仅限单线程使用。

---

### 4.6 NativeWindowHandleOpt va_arg 缺少边界验证 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/native_window.cpp`，第 210-297 行

对于 `GET_*` 操作，来自 `va_arg` 的指针在解引用前未进行空检查。例如：

```cpp
case GET_USAGE: {
    uint64_t *value = va_arg(args, uint64_t*);
    *value = usage;  // 如果调用者传递 NULL 则崩溃
    break;
}
```

如果 NDK 应用对任何 GET 操作传递空指针，将导致段错误。由于这是一个可变参数 C API，编译时没有类型检查。

**建议**：在所有 GET 分支中解引用 va_arg 指针之前添加空检查。

---

## 5. 发现：窗口管理器

### 5.1 静态全局 Map 缺乏一致的锁保护 (高危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/include/window_impl.h`，第 596-614 行

`WindowImpl` 在静态类级别的 map 中存储所有窗口实例和监听器注册：

```cpp
static std::map<std::string, std::pair<uint32_t, sptr<Window>>> windowMap_;
static std::map<uint32_t, std::vector<sptr<WindowImpl>>> subWindowMap_;
static std::map<uint32_t, std::vector<sptr<IWindowLifeCycle>>> lifecycleListeners_;
// ... 更多静态 map
static std::recursive_mutex globalMutex_;
```

然而，这些 map 上的许多操作（例如 `Find()`、`FindWindowById()`、`GetSubWindow()`、`GetTopWindowWithContext()`）在访问 `windowMap_` 时未获取 `globalMutex_`。例如：

- `WindowImpl::Find()`（第 163-170 行）在无任何锁的情况下迭代 `windowMap_`
- `WindowImpl::FindWindowById()`（第 177-191 行）在无任何锁的情况下迭代 `windowMap_`
- `WindowImpl::Create()` 在第 1220 行不持有 `globalMutex_` 的情况下插入 `windowMap_`

如果从不同线程创建/销毁窗口（例如主线程和 ability 生命周期线程），这些 map 可能被并发修改，导致未定义行为。

**建议**：在访问任何静态 map 时一致地获取 `globalMutex_`，或记录所有窗口操作必须在单一线程上执行。

---

### 5.2 WindowImpl::Snapshot 缺少权限检查 (高危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_impl.cpp`，第 713-729 行

```cpp
std::shared_ptr<Media::PixelMap> WindowImpl::Snapshot()
{
    std::shared_ptr<SurfaceCaptureFuture> callback = std::make_shared<SurfaceCaptureFuture>();
    auto isSucceeded = RSInterfaces::GetInstance().TakeSurfaceCapture(surfaceNode_, callback);
    ...
}
```

`Snapshot()` 函数在不进行任何权限检查的情况下捕获窗口的 Surface 内容。对比第 1880 行的 `SetSnapshotSkip()` 正确检查了 `Permission::IsSystemCalling()`。在 `WindowSceneSessionImpl::Snapshot()`（较新的实现）中，同样缺少权限检查。

虽然窗口只能截取自己的 Surface，但恶意应用可以使用 `Window::Find()` 或相关函数获取其他窗口 Surface 的引用，如果这些引用通过静态 `windowMap_` 泄漏。

**建议**：添加权限检查或验证调用者拥有正在被捕获的窗口。

---

### 5.3 全局构造/析构计数器非线程安全 (低危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_impl.cpp`，第 75-76 行

```cpp
int constructorCnt = 0;
int deConstructorCnt = 0;
```

这些是在构造函数/析构函数中递增的非原子全局计数器。在多线程窗口创建中，并发递增会产生不正确的计数。这些看起来是调试计数器，不影响功能，但存在于发布版本中。

**建议**：使用 `std::atomic<int>` 或从发布版本中移除。

---

### 5.4 Destroy 在服务端失败后继续清理 (中危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_impl.cpp`，第 1368-1410 行

在 `WindowImpl::Destroy()` 中，如果 `WindowAdapter::DestroyWindow()` 失败（第 1383-1390 行），该函数仅对非对话框窗口返回错误。对于对话框，它继续清理本地状态（从 map 中移除、清除监听器、转换到 `STATE_DESTROYED`）。即使对于非对话框，如果服务端调用失败，客户端可能处于不一致状态，它认为窗口仍然存在但已部分清理。

实际上仔细检查：对于非对话框类型，如果 `DestroyWindow` 失败，它在第 1389 行提前返回，但第 1398 行（`windowMap_.erase`）在 if 块之下，所以它不会执行。因此在非对话框窗口的错误情况下 map 不会被清除。但第 1377 行的 `NotifyBeforeDestroy` 已经销毁了 UI 内容。这意味着失败的销毁使窗口留在 map 中但 UI 已被销毁。

**建议**：在服务端销毁成功之前不要调用 `NotifyBeforeDestroy`，或在失败时回滚。

---

### 5.5 VsyncStation::Init 在 Receiver 创建时进入无限循环 (高危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/src/vsync_station.cpp`，第 99-102 行

```cpp
while (receiver_ == nullptr) {
    receiver_ = rsClient.CreateVSyncReceiver("WM_" + std::to_string(::getprocpid()), frameRateLinker_->GetId(),
        vsyncHandler_);
}
```

如果 `CreateVSyncReceiver` 持续返回 nullptr（例如 RenderService 未运行、IPC 失败、资源耗尽），这将成为一个占满 CPU 核心且永不让步的无限忙循环。没有退避、重试限制或超时。

这可能在系统启动期间触发，如果窗口管理在 RenderService 之前初始化，或在 RenderService 崩溃期间触发。

**建议**：添加带指数退避的重试限制，或在无法在合理尝试次数内创建 receiver 时返回错误。

---

### 5.6 WindowImpl 与 WindowSessionImpl 的代码重复 (低危)

**文件**：`/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_impl.cpp` 和 `/home/dspfac/openharmony/foundation/window/window_manager/wm/src/window_session_impl.cpp`

`WindowImpl` 和 `WindowSessionImpl` 都复制了大量逻辑：
- 相同的 `CALL_LIFECYCLE_LISTENER` / `CALL_UI_CONTENT` 宏（在两个文件中都有定义）
- 类似的 `CreateSurfaceNode` 实现
- 并行的监听器静态 map
- 类似的权限检查模式

这种代码重复增加了维护负担，并造成修复只应用于其中一个的风险（例如 `SetSnapshotSkip` 权限检查在 `WindowSceneSessionImpl` 中使用 `SessionPermission::IsSystemCalling()`，而在 `WindowImpl` 中使用 `Permission::IsSystemCalling()`）。

**建议**：将共享逻辑提取到公共基类或工具类中。

---

## 6. 发现：合成器与 Fence 管理

### 6.1 SyncFence 生命周期管理 (低危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/interfaces/inner_api/utils/sync_fence.h`

`SyncFence` 类正确使用 `UniqueFd` 进行自动 fd 关闭，并禁用了拷贝/移动构造函数。`INVALID_FENCE` 静态单例模式是合适的。`Dup()` 方法返回调用者必须管理的原始 fd，这对于 NDK C API 桥接是必要的，但需要仔细记录。

`Get()` 上的注释（第 86 行）`/* this is dangerous, when you use it, do not operator the fd */` 是适当的警告。在 fence 生命周期管理中未发现问题。

---

### 6.2 BufferQueueProducer IPC 处理程序缺少调用者验证 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/surface/src/buffer_queue_producer.cpp`，第 89-110 行

`OnRemoteRequest` 处理程序验证了接口令牌（第 103-105 行），但对大多数操作不检查调用者身份。虽然 `RequestBuffer` 验证了 `connectedPid_`（第 401-408 行），但 `SetQueueSize`、`CleanCache` 和 `SetTransform` 等操作不验证调用者是否为已连接的生产者。获取 `IBufferProducer` binder 引用的进程可以调用 `SetQueueSize(0)` 或 `CleanCache()` 来干扰另一个应用的渲染。

**建议**：为 `SetQueueSize`、`SetTransform` 和 `SetTunnelHandle` 等敏感操作添加 `CheckConnectLocked()` 验证。

---

## 7. 发现：API 设计

### 7.1 NativeWindowOperation 枚举稳定性风险 (中危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/interfaces/inner_api/surface/external_window.h`，第 92-195 行

`NativeWindowOperation` 枚举使用自动递增值而没有显式的数值赋值：

```cpp
enum NativeWindowOperation {
    SET_BUFFER_GEOMETRY,  // 0
    GET_BUFFER_GEOMETRY,  // 1
    GET_FORMAT,           // 2
    SET_FORMAT,           // 3
    ...
};
```

在此枚举中间（而非末尾）添加新操作会破坏现有应用的 ABI 兼容性。由于这是公共 NDK API（标记为 `@since 8`），枚举值的稳定性至关重要。

**建议**：为所有枚举成员分配显式数值以防止意外的 ABI 破坏。

---

### 7.2 Surface 类型定义：C++ 命名空间中的 C 风格 (低危)

**文件**：`/home/dspfac/openharmony/foundation/graphic/graphic_surface/interfaces/inner_api/surface/surface_type.h`

该文件广泛使用 `using X = enum { ... }` 模式（例如第 38、50、70、93 行）。虽然是有效的 C++，但这创建了通过 `using` 别名化的匿名枚举类型，意味着枚举值在封闭命名空间范围内。这是遗留的 C 模式。现代 C++ 会使用 `enum class` 以实现类型安全。存在多个拼写不一致（`GRAPHIC_PRESION_LIMITED` vs `PRECISION`，`GRAPHIC_MATAKEY_*` vs `METAKEY`）。

**建议**：对新增项考虑使用 `enum class`，在下一个主要版本中修复拼写。

---

## 8. 汇总表

| # | 发现 | 严重级别 | 类别 | 位置 |
|---|------|----------|------|------|
| 3.1 | DoFlushBuffer 中的 Usage 截断 | 高危 | 正确性 | buffer_queue.cpp:498 |
| 3.2 | GetLastFlushedBuffer 缺少互斥锁 | 高危 | 线程安全 | buffer_queue.cpp:422 |
| 3.3 | 未初始化的 lastFlusedSequence_ | 高危 | 正确性 | buffer_queue.h:190 |
| 3.4 | DumpToFile 路径遍历风险 | 中危 | 安全性 | buffer_queue.cpp:445 |
| 3.5 | IsSupportedAlloc 模拟数据 | 中危 | 正确性 | buffer_manager.cpp:228 |
| 3.6 | 序列号生成中的手动加锁 | 低危 | 代码质量 | surface_buffer_impl.cpp:101 |
| 3.7 | 拼写错误：lastFlused | 低危 | 代码质量 | buffer_queue.h:190 |
| 4.1 | FlushBuffer 返回值被忽略 | 中危 | 错误处理 | native_window.cpp:168 |
| 4.2 | GetLastFlushedBuffer 中的内存泄漏 | 中危 | 资源泄漏 | native_window.cpp:186 |
| 4.3 | HandleOpt 始终返回成功 | 中危 | 错误处理 | native_window.cpp:296 |
| 4.4 | 拼写错误：DestoryNativeWindow | 低危 | 代码质量 | native_window.cpp:57 |
| 4.5 | NativeWindow 缺乏线程安全 | 中危 | 线程安全 | native_window.cpp |
| 4.6 | va_arg 空指针解引用 | 中危 | 健壮性 | native_window.cpp:210 |
| 5.1 | 静态 map 无保护访问 | 高危 | 线程安全 | window_impl.h:596 |
| 5.2 | Snapshot 缺少权限检查 | 高危 | 安全性 | window_impl.cpp:713 |
| 5.3 | 非原子全局计数器 | 低危 | 代码质量 | window_impl.cpp:75 |
| 5.4 | 错误时 Destroy 部分清理 | 中危 | 错误处理 | window_impl.cpp:1368 |
| 5.5 | VsyncStation 无限循环 | 高危 | 可靠性 | vsync_station.cpp:99 |
| 5.6 | WindowImpl/Session 代码重复 | 低危 | 可维护性 | wm/src/*.cpp |
| 6.2 | IPC 处理程序缺少调用者检查 | 中危 | 安全性 | buffer_queue_producer.cpp:89 |
| 7.1 | NDK API 中的枚举稳定性风险 | 中危 | API 设计 | external_window.h:92 |
| 7.2 | C++ 命名空间中的 C 风格枚举 | 低危 | 代码质量 | surface_type.h |

**严重级别分布**：高危：5，中危：10，低危：7
