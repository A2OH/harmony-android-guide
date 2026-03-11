# Android 11 AOSP 代码审查：操作系统和系统级 API

**审查范围：**
- `frameworks/base/core/java/android/os/` -- 线程、IPC、系统实用工具
- `frameworks/base/core/java/android/permission/` -- 权限管理
- `frameworks/base/core/java/android/security/` -- 安全和密钥库 API

---

## 目录

1. [线程模型 (Handler, Looper, MessageQueue)](#1-线程模型)
2. [AsyncTask（已废弃）](#2-asynctask)
3. [IPC / Binder 架构](#3-ipc--binder-架构)
4. [Bundle](#4-bundle)
5. [构建信息](#5-构建信息)
6. [Environment（文件系统路径）](#6-environment)
7. [PowerManager 和 BatteryManager](#7-powermanager-和-batterymanager)
8. [SystemClock](#8-systemclock)
9. [进程管理](#9-进程管理)
10. [UserManager](#10-usermanager)
11. [权限管理器](#11-权限管理器)
12. [安全 API](#12-安全-api)
13. [架构模式与观察总结](#13-架构模式与观察总结)

---

## 1. 线程模型

Android 线程模型建立在三个紧密耦合的类之上，实现了单线程事件循环模式。

### 1.1 Looper

**文件：** `frameworks/base/core/java/android/os/Looper.java`（476 行）

**用途：** 为线程运行消息循环。每个线程最多只能有一个 Looper。主（UI）线程始终拥有一个 Looper；工作线程必须显式创建。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `prepare()` | 103 | 将当前线程初始化为 Looper 线程 |
| `loop()` | 154 | 开始处理消息队列（阻塞） |
| `myLooper()` | 293 | 返回当前线程的 Looper（可能为 null） |
| `getMainLooper()` | 135 | 返回应用程序的主 Looper |
| `quit()` | 362 | 立即退出（可能丢弃待处理的消息） |
| `quitSafely()` | 378 | 处理完待处理的到期消息后退出 |
| `getThread()` | 387 | 获取与此 Looper 关联的线程 |
| `getQueue()` | 396 | 获取 MessageQueue |
| `isCurrentThread()` | 314 | 检查调用者是否在此 Looper 的线程上 |
| `setMessageLogging(Printer)` | 327 | 启用/禁用消息分发日志 |

**架构细节：**

- 使用 `ThreadLocal<Looper>` 存储（第 72 行）—— 每个线程拥有自己的 Looper。
- 主 Looper 以 `quitAllowed=false` 创建（第 123 行），防止应用意外退出主线程的事件循环。
- `loop()` 方法（第 154 行）是一个无限的 `for(;;)` 循环，执行以下操作：
  1. 调用 `queue.next()`，可能通过原生 `epoll` 阻塞
  2. 通过 `msg.target.dispatchMessage(msg)` 分发消息（第 223 行）
  3. 清除并验证 Binder 调用身份（第 263-270 行）—— 安全检查
  4. 分发后回收消息（第 272 行）

**隐藏 API：**
- `setObserver(Observer)`（第 146 行，`@hide`）—— 进程级消息分发观察器，用于遥测
- `setSlowLogThresholdMs()`（第 341 行，`@hide`）—— 可配置的慢消息检测
- `prepareMainLooper()`（第 122 行，`@Deprecated`）—— 仅在应用启动期间由系统使用

**性能监控：** 循环通过可配置的阈值检测慢消息传递和分发（第 89-95 行）。系统属性 `log.looper.<uid>.<thread>.slow` 可以覆盖阈值（第 174-178 行）。

### 1.2 Handler

**文件：** `frameworks/base/core/java/android/os/Handler.java`（1001 行）

**用途：** 在特定线程的 `MessageQueue` 上发送和处理 `Message` 和 `Runnable` 对象。Android 中主要的线程间通信机制。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `Handler(Looper)` | 161 | **推荐的构造函数** —— 显式指定 Looper |
| `Handler(Looper, Callback)` | 172 | 带 Looper 和回调 |
| `createAsync(Looper)` | 275 | 创建不受同步屏障影响的 Handler |
| `post(Runnable)` | 426 | 向消息队列发送 Runnable |
| `postDelayed(Runnable, long)` | 498 | 延迟发送（基于 uptimeMillis） |
| `postAtTime(Runnable, long)` | 448 | 在绝对 uptime 时间发送 |
| `sendMessage(Message)` | 634 | 发送 Message 对象 |
| `sendMessageDelayed(Message, long)` | 693 | 延迟发送消息 |
| `sendEmptyMessage(int)` | 645 | 发送仅包含 `what` 代码的消息 |
| `removeCallbacks(Runnable)` | 612 | 移除待处理的 Runnable |
| `removeMessages(int)` | 785 | 按 `what` 代码移除待处理消息 |
| `removeCallbacksAndMessages(Object)` | 814 | 移除所有匹配的回调/消息 |
| `hasMessages(int)` | 832 | 检查是否有待处理消息 |
| `hasCallbacks(Runnable)` | 866 | 检查是否有待处理回调 |
| `obtainMessage(...)` | 354-413 | 从池中获取回收的 Message |
| `getLooper()` | 873 | 获取关联的 Looper |
| `handleMessage(Message)` | 91 | 重写以接收消息 |

**关键设计决策 —— 默认构造函数已废弃（Android 11）：**

无参 `Handler()` 构造函数在此版本中已标记为 `@Deprecated`（第 127-128 行）。废弃原因（第 117-125 行）是隐式选择 Looper 可能导致：
- 如果 Handler 意外退出，消息会静默丢失
- 在没有 Looper 的线程上创建时崩溃
- 关于 Handler 绑定到哪个线程的竞态条件

**推荐模式：**
```java
Handler handler = new Handler(Looper.getMainLooper());
// 或
Handler handler = Handler.createAsync(Looper.getMainLooper());
```

**消息分发优先级（第 97-108 行）：**
```
1. 如果 msg.callback != null -> 直接运行 Runnable
2. 如果 Handler.Callback != null 且返回 true -> 停止
3. 否则 -> Handler.handleMessage(msg)
```

**隐藏 API：**
- `runWithScissors(Runnable, long)`（第 592 行，`@hide`）—— 同步跨线程执行。Javadoc 明确警告"此方法容易被滥用"，并指出可能导致死锁。内部使用 `BlockingRunnable` 配合 `wait()`/`notifyAll()`。
- `executeOrSendMessage(Message)`（第 762 行，`@hide`）—— 如果在同一线程则立即分发，否则入队。
- `getMain()`（第 302 行，`@hide`）—— 缓存的主线程 Handler 单例。
- `Handler(boolean async)`（第 193 行，`@hide`）—— 创建支持异步消息的 Handler。

**内存泄漏检测：** `FIND_POTENTIAL_LEAKS` 标志（第 72 行）可以检测持有外部 Activity 实例隐式引用的非静态内部类 Handler，但它被硬编码为 `false`。

**异步消息和同步屏障：** 异步 Handler（通过 `createAsync()` 或隐藏的 `async` 构造函数创建）会对所有消息设置 `Message.setAsynchronous(true)`（第 775-777 行）。这些消息不会被同步屏障阻塞，框架在 VSYNC 驱动的渲染期间使用同步屏障。

### 1.3 MessageQueue

**文件：** `frameworks/base/core/java/android/os/MessageQueue.java`（1045 行）

**用途：** 低级消息优先级队列，按传递时间（`when` 字段）排序。使用原生 `epoll` 实现高效阻塞。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `addIdleHandler(IdleHandler)` | 124 | 注册队列空闲事件回调 |
| `removeIdleHandler(IdleHandler)` | 142 | 取消注册空闲回调 |
| `isIdle()` | 107 | 检查队列是否没有到期消息 |
| `addOnFileDescriptorEventListener(...)` | 194 | 监控文件描述符事件 |
| `removeOnFileDescriptorEventListener(...)` | 221 | 停止监控文件描述符 |

**内部架构：**

`next()` 方法（第 319-423 行）是消息处理的核心：

1. 调用 `nativePollOnce(ptr, timeoutMillis)`（第 335 行）—— 在原生 `epoll_wait` 中阻塞
2. 处理**同步屏障**：`target == null` 的消息（第 342-347 行）会阻塞所有同步消息，但允许异步消息通过
3. 根据消息队列排序计算下次唤醒时间
4. 当队列为空或所有消息都在未来时，运行 **IdleHandler**（第 398-414 行）
5. 在阻塞前刷新待处理的 Binder 命令（第 332 行）

**同步屏障（`@hide`、`@TestApi`）：**
- `postSyncBarrier()`（第 472 行）—— 阻滞同步消息
- `removeSyncBarrier(int token)`（第 517 行）—— 移除屏障

这些在框架内部用于 VSYNC 协调。屏障是 `target == null` 的 Message（第 342 行）。遇到屏障时，队列跳过所有同步消息，只传递异步消息。这就是框架确保渲染消息获得优先级的方式。

**消息入队（第 549-602 行）：**
消息按 `when` 顺序插入（排序链表）。在以下情况下队列唤醒原生轮询：
- 新消息在队列头部（比当前头部更早）
- 在同步屏障活动时添加了异步消息

**IdleHandler 接口（第 945-954 行）：**
```java
public static interface IdleHandler {
    boolean queueIdle(); // 返回 true 保留，返回 false 自动移除
}
```
适用于将工作推迟到 UI 空闲时执行。每次泵送周期仅在第一次空闲迭代时运行。

---

## 2. AsyncTask

**文件：** `frameworks/base/core/java/android/os/AsyncTask.java`（812 行）

**用途：** 用于在后台运行工作并在 UI 线程上发布结果的辅助类。

**在 Android 11 中已废弃**（第 198 行）。类级 Javadoc（第 41-46 行）明确记录了以下问题：
- Context 泄漏
- 配置更改时回调丢失
- 配置更改时崩溃
- 跨平台版本行为不一致
- 吞没 `doInBackground` 中的异常

**推荐替代方案：** `java.util.concurrent.Executor`、Kotlin 协程。

**关键 API（全部已废弃）：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `doInBackground(Params...)` | 468 | 抽象方法 —— 在后台线程运行 |
| `onPreExecute()` | 478 | UI 线程，后台工作之前 |
| `onPostExecute(Result)` | 498 | UI 线程，完成之后 |
| `onProgressUpdate(Progress...)` | 514 | UI 线程，进度回调 |
| `publishProgress(Progress...)` | 760 | 从后台调用以触发进度更新 |
| `execute(Params...)` | 670 | 启动任务（默认串行执行） |
| `executeOnExecutor(Executor, Params...)` | 708 | 使用自定义执行器启动 |
| `cancel(boolean)` | 601 | 取消任务 |
| `isCancelled()` | 564 | 检查取消状态 |
| `getStatus()` | 443 | PENDING、RUNNING 或 FINISHED |

**线程池架构（第 211-265 行）：**

```
SERIAL_EXECUTOR（默认）-- 使用 ArrayDeque 的 SerialExecutor
    |
    v
THREAD_POOL_EXECUTOR -- ThreadPoolExecutor(core=1, max=20, keepAlive=3s)
    |
    v（拒绝时）
sBackupExecutor -- ThreadPoolExecutor(core=5, max=5, 无界队列)
```

`SerialExecutor`（第 297-321 行）将每个任务包装在一个 `Runnable` 中，在 `finally` 块中调用 `scheduleNext()`，即使任务抛出异常也能保证串行执行。

**隐藏 API：**
- `setDefaultExecutor(Executor)`（第 357 行，`@hide`）—— 覆盖进程级默认串行执行器
- `AsyncTask(Handler)` / `AsyncTask(Looper)`（第 373/382 行，`@hide`）—— 使用自定义回调 Looper

**值得注意的实现细节：** 工作 callable（第 387-404 行）将线程优先级设置为 `THREAD_PRIORITY_BACKGROUND`，并在完成后调用 `Binder.flushPendingCommands()`，确保释放 IPC 资源。

---

## 3. IPC / Binder 架构

### 3.1 IBinder 接口

**文件：** `frameworks/base/core/java/android/os/IBinder.java`（347 行）

**用途：** Android 轻量级远程过程调用（RPC）机制的核心接口。

**关键接口方法：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `transact(int, Parcel, Parcel, int)` | 289 | 执行通用 IPC 操作 |
| `queryLocalInterface(String)` | 221 | 检查本地实现 |
| `getInterfaceDescriptor()` | 195 | 获取规范接口名称 |
| `pingBinder()` | 205 | 检查远程进程是否存活 |
| `isBinderAlive()` | 213 | 检查宿主进程是否存在 |
| `linkToDeath(DeathRecipient, int)` | 325 | 注册死亡通知 |
| `unlinkToDeath(DeathRecipient, int)` | 346 | 取消注册死亡通知 |
| `dump(FileDescriptor, String[])` | 229 | 转储状态用于调试 |

**事务代码范围：**
- `FIRST_CALL_TRANSACTION`（0x00000001）到 `LAST_CALL_TRANSACTION`（0x00ffffff）—— 用户自定义代码
- 系统代码：`PING_TRANSACTION`、`DUMP_TRANSACTION`、`INTERFACE_TRANSACTION`、`SHELL_COMMAND_TRANSACTION`（`@hide`）

**IPC 大小限制：** `MAX_IPC_SIZE = 64 * 1024` 字节（第 182 行，`@hide`）。公共 API `getSuggestedMaxIpcSizeBytes()`（第 188 行）暴露了此值。

**单向标志：** `FLAG_ONEWAY = 0x00000001`（第 170 行）—— 调用者立即返回，无需等待结果。系统保证对同一 IBinder 的多个单向调用的顺序。

**DeathRecipient（第 298-307 行）：** 进程死亡通知的回调接口。对于服务生命周期管理至关重要。隐藏的 `binderDied(IBinder who)` 默认方法（第 304 行）提供了即将死亡的 Binder 引用。

**彩蛋：** `TWEET_TRANSACTION`（第 137 行）和 `LIKE_TRANSACTION`（第 150 行）是带有幽默 Javadoc 的搞笑协议代码。

### 3.2 Binder

**文件：** `frameworks/base/core/java/android/os/Binder.java`（1205 行）

**用途：** 可远程对象的基类。Binder IPC 机制的 Java 端实现。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `Binder()` | 580 | 默认构造函数 |
| `Binder(String descriptor)` | 596 | 带描述符的构造函数（用于令牌） |
| `getCallingPid()` | 289 | 当前 IPC 调用者的 PID |
| `getCallingUid()` | 299 | 当前 IPC 调用者的 UID |
| `getCallingUserHandle()` | 333 | 调用者的 UserHandle |
| `clearCallingIdentity()` | 354 | 重置调用者身份（返回令牌） |
| `restoreCallingIdentity(long)` | 366 | 恢复已保存的身份 |
| `flushPendingCommands()` | 549 | 将待处理的 IPC 命令刷新到内核 |
| `attachInterface(IInterface, String)` | 617 | 将接口与 Binder 关联 |
| `onTransact(int, Parcel, Parcel, int)` | 782 | 重写以处理 IPC 调用 |
| `getCallingWorkSourceUid()` | 485 | 获取工作源 UID（不可信） |
| `setCallingWorkSourceUid(int)` | 473 | 设置工作源以进行归因 |

**安全关键模式 —— 清除调用身份：**

```java
long token = Binder.clearCallingIdentity();
try {
    // 此处的调用将使用本地进程身份，
    // 而不是远程调用者的身份
} finally {
    Binder.restoreCallingIdentity(token);
}
```

此模式（记录在第 337-365 行）在系统服务代表调用者调用其他服务时被广泛使用。如果不这样做，嵌套的权限检查将失败，因为它们会看到错误的调用者 UID。

**隐藏便利方法：**
- `withCleanCallingIdentity(ThrowingRunnable)`（第 377 行，`@hide`）—— 基于 lambda 的版本
- `withCleanCallingIdentity(ThrowingSupplier<T>)`（第 401 行，`@hide`）—— 带返回值

**阻塞调用检测：**
- `setWarnOnBlocking(boolean)`（第 197 行，`@hide`）—— 系统进程使用此方法检测对不受信任外部代码的阻塞调用
- `allowBlocking(IBinder)`（第 213 行，`@hide`）—— 覆盖已知安全的系统接口

**ProxyTransactListener（`@SystemApi`，第 679-706 行）：** 用于监控所有代理端 Binder 调用的观察者接口。用于：
- 事务追踪
- 工作源传播（`PropagateWorkSourceTransactListener`，第 716 行）

**onTransact 实现（第 782 行）：** 基础实现处理系统事务代码：`INTERFACE_TRANSACTION` 返回描述符，`DUMP_TRANSACTION` 调用 `dump()`，`SHELL_COMMAND_TRANSACTION` 委托给 `shellCommand()`。

### 3.3 Parcel

**文件：** `frameworks/base/core/java/android/os/Parcel.java`（3683 行）

**用途：** 用于跨 IPC 边界编组数据的容器。针对 IPC 优化的高性能序列化，不适用于持久存储。

**Javadoc 中的关键警告（第 69-75 行）：** "Parcel **不是**通用序列化机制……不适合将任何 Parcel 数据放入持久存储。"

**数据类型类别：**

1. **基本类型：** `writeInt`/`readInt`、`writeLong`/`readLong`、`writeFloat`/`readFloat`、`writeDouble`/`readDouble`、`writeString`/`readString`、`writeByte`/`readByte`
2. **基本类型数组：** `writeIntArray`、`createIntArray`、`writeByteArray` 等
3. **Parcelable：** `writeParcelable`/`readParcelable`（带类信息）、`writeTypedObject`/`readTypedObject`（不带类信息，更高效）
4. **Bundle：** `writeBundle`/`readBundle` —— 类型安全的键值映射
5. **活跃对象：** `writeStrongBinder`/`readStrongBinder` —— IBinder 引用、`writeFileDescriptor`/`readFileDescriptor`
6. **无类型容器：** `writeValue`/`readValue`、`writeList`/`readList`

**对象池：** `obtain()` / `recycle()` 模式，用于避免分配开销。

### 3.4 Parcelable 接口

**文件：** `frameworks/base/core/java/android/os/Parcelable.java`（173 行）

**用途：** 可序列化到 Parcel 中进行 IPC 传输的对象接口。

**必需组件：**
1. `describeContents()` —— 如果包含文件描述符则返回 `CONTENTS_FILE_DESCRIPTOR`
2. `writeToParcel(Parcel, int)` —— 序列化到 Parcel
3. 静态 `CREATOR` 字段，实现 `Parcelable.Creator<T>` —— 反序列化工厂

**写入标志：**
- `PARCELABLE_WRITE_RETURN_VALUE`（0x0001）—— 对象是返回值，可以释放资源
- `PARCELABLE_ELIDE_DUPLICATES`（0x0002，`@hide`）—— 父对象管理重复数据

**ClassLoaderCreator（第 160-172 行）：** 接收 `ClassLoader` 的扩展 `Creator`，适用于跨进程类加载。

---

## 4. Bundle

**文件：** `frameworks/base/core/java/android/os/Bundle.java`（1363 行）

**用途：** 从 String 键到各种 `Parcelable` 值的类型安全映射。在组件（Activity、Fragment、Service）之间传递结构化数据的主要机制。

**类层次结构：** `Bundle extends BaseBundle implements Cloneable, Parcelable`

**关键常量：**
- `Bundle.EMPTY`（第 48 行）—— 不可变的空 Bundle 单例
- `Bundle.STRIPPED`（第 54 行，`@hide`）—— 剥离的 extras 哨兵值

**内部标志（第 40-46 行）：**
- `FLAG_HAS_FDS` —— 包含文件描述符
- `FLAG_HAS_FDS_KNOWN` —— FD 存在性已计算
- `FLAG_ALLOW_FDS` —— 是否允许 FD

**关键方法（继承自 BaseBundle + Bundle 特有）：**
- `putString(String, String)`、`getString(String)`、`getString(String, String)`
- `putInt(String, int)`、`getInt(String)`、`getInt(String, int)`
- `putParcelable(String, Parcelable)`、`getParcelable(String)`
- `putBundle(String, Bundle)`、`getBundle(String)`
- `putBinder(String, IBinder)`、`getBinder(String)` —— 用于传递 Binder 令牌
- `deepCopy()` —— 深拷贝，与浅拷贝的 `Bundle(Bundle)` 构造函数相对
- `hasFileDescriptors()` —— 检查 Bundle 是否包含 FD（惰性扫描）

**延迟反序列化：** 来自 Parcel 的 Bundle 数据不会立即反序列化。原始的 `mParcelledData` 会保留到首次访问时，然后按需反序列化。这是对可能永远不会被读取的 Intent extras 的重要性能优化。

---

## 5. 构建信息

**文件：** `frameworks/base/core/java/android/os/Build.java`（1335 行）

**用途：** 关于当前构建的静态信息，从系统属性（`ro.build.*`、`ro.product.*`）中提取。

**关键公共字段：**

| 字段 | 行号 | 描述 |
|-------|------|-------------|
| `DEVICE` | 60 | 设备名称（例如 "walleye"） |
| `MODEL` | 88 | 面向最终用户的型号名称 |
| `MANUFACTURER` | 82 | 产品制造商 |
| `BRAND` | 85 | 面向消费者的品牌 |
| `PRODUCT` | 57 | 整体产品名称 |
| `BOARD` | 63 | 底层主板名称 |
| `HARDWARE` | 106 | 来自内核的硬件名称 |
| `BOOTLOADER` | 91 | 引导加载程序版本 |
| `DISPLAY` | 54 | 用于显示的构建 ID |
| `SUPPORTED_ABIS` | 188 | 支持的 ABI 有序列表 |
| `SERIAL` | 126 | **已废弃** —— 始终返回 UNKNOWN |

**Build.VERSION（第 236 行）：**

| 字段 | 行号 | 描述 |
|-------|------|-------------|
| `SDK_INT` | 288 | SDK 版本整数（例如 Android 11 为 30） |
| `RELEASE` | 251 | 面向用户的版本字符串（例如 "11"） |
| `CODENAME` | 351 | 发布版为 "REL"，否则为代号 |
| `SECURITY_PATCH` | 269 | 安全补丁日期字符串 |
| `BASE_OS` | 263 | 基础 OS 构建字符串 |
| `PREVIEW_SDK_INT` | 324 | 预览 SDK 修订号（发布版为 0） |
| `INCREMENTAL` | 242 | 源代码控制变更列表/哈希 |

**Build.VERSION_CODES（第 389 行）：** 从 `BASE`（1）到 `R`（30，Android 11）的所有 Android API 级别枚举。

**安全敏感 API —— `getSerial()`（第 169 行）：**
需要 `READ_PRIVILEGED_PHONE_STATE` 权限。从 API 29 开始，设备标识符受到严格限制。该方法通过 Binder IPC 委托给 `IDeviceIdentifiersPolicyService`。

**隐藏 API：**
- `IS_EMULATOR`（第 114 行，`@hide`、`@TestApi`）—— 检查 `ro.kernel.qemu`
- `VERSION.FIRST_SDK_INT`（第 303 行，`@hide`、`@TestApi`）—— 最初出厂的 SDK 版本
- `VERSION.RESOURCES_SDK_INT`（第 371 行，`@hide`、`@TestApi`）—— `SDK_INT + ACTIVE_CODENAMES.length`
- `VERSION.MIN_SUPPORTED_TARGET_SDK_INT`（第 380 行，`@hide`）—— 最低目标 SDK

---

## 6. Environment

**文件：** `frameworks/base/core/java/android/os/Environment.java`（1434 行）

**用途：** 访问标准文件系统目录和外部存储状态。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `getRootDirectory()` | 218 | `/system` —— 只读系统分区 |
| `getDataDirectory()` | 353 | `/data` —— 内部数据 |
| `getDownloadCacheDirectory()` | 1045 | `/cache` —— 下载缓存 |
| `getExternalStorageDirectory()` | 677 | 主外部存储根目录 |
| `getExternalStoragePublicDirectory(type)` | 962 | 按类型的公共目录 |
| `getExternalStorageState()` | 1142 | 存储挂载状态 |
| `getExternalStorageState(File)` | 1165 | 特定路径的状态 |
| `isExternalStorageEmulated()` | ~1190 | 外部存储是否为模拟的 |
| `isExternalStorageRemovable()` | ~1210 | 存储是否物理可移除 |
| `getStorageDirectory()` | 227 | `/storage` —— 挂载点根目录 |

**公共目录类型常量：**
- `DIRECTORY_MUSIC`、`DIRECTORY_PODCASTS`、`DIRECTORY_RINGTONES`
- `DIRECTORY_ALARMS`、`DIRECTORY_NOTIFICATIONS`
- `DIRECTORY_PICTURES`、`DIRECTORY_MOVIES`
- `DIRECTORY_DOWNLOADS`、`DIRECTORY_DCIM`、`DIRECTORY_DOCUMENTS`
- `DIRECTORY_SCREENSHOTS`、`DIRECTORY_AUDIOBOOKS`

**存储状态常量：**
- `MEDIA_MOUNTED` —— 读写访问
- `MEDIA_MOUNTED_READ_ONLY` —— 只读访问
- `MEDIA_REMOVED`、`MEDIA_BAD_REMOVAL`、`MEDIA_UNMOUNTED`
- `MEDIA_CHECKING`、`MEDIA_EJECTING`、`MEDIA_UNKNOWN`

**分区存储（Android 10+/11）：**

第 94-132 行定义了分区存储兼容性标志：
- `DEFAULT_SCOPED_STORAGE`（ChangeId 149924527）—— 默认对所有应用启用
- `FORCE_ENABLE_SCOPED_STORAGE`（ChangeId 132649864）—— 默认 `@Disabled`，严格强制分区存储

退出机制文档（第 96-101 行）：
- 目标 SDK < Q
- 目标 SDK = Q + `requestLegacyExternalStorage` 清单属性
- 目标 SDK > Q + 升级 + `preserveLegacyExternalStorage`

**隐藏系统 API（`@SystemApi`）：**
- `getOemDirectory()`（第 239 行）—— `/oem` 分区
- `getOdmDirectory()`（第 251 行）—— `/odm` 分区
- `getVendorDirectory()`（第 262 行）—— `/vendor` 分区
- `getProductDirectory()`（第 274 行）—— `/product` 分区
- `getSystemExtDirectory()`（第 301 行）—— `/system_ext` 分区
- 大量 `getDataSystem*Directory()` 方法用于每用户系统数据

**UserEnvironment（第 150 行，`@hide`）：** 提供每用户外部存储路径的内部类。每个用户通过 `StorageManager.getVolumeList()` 获得隔离的外部存储。

---

## 7. PowerManager 和 BatteryManager

### 7.1 PowerManager

**文件：** `frameworks/base/core/java/android/os/PowerManager.java`（2634 行）

**用途：** 控制设备电源状态、唤醒锁和电源相关查询。

**获取方式：** `context.getSystemService(Context.POWER_SERVICE)`（`@SystemService`）

**唤醒锁级别：**

| 常量 | 行号 | CPU | 屏幕 | 键盘 |
|----------|------|-----|--------|----------|
| `PARTIAL_WAKE_LOCK` | 80 | 开启 | 关闭 | 关闭 |
| `SCREEN_DIM_WAKE_LOCK` | 97 | 开启 | 暗淡 | 关闭 |
| `SCREEN_BRIGHT_WAKE_LOCK` | 114 | 开启 | 明亮 | 关闭 |
| `FULL_WAKE_LOCK` | 132 | 开启 | 明亮 | 明亮 |
| `PROXIMITY_SCREEN_OFF_WAKE_LOCK` | 153 | 开启 | 接近传感器 | -- |
| `DOZE_WAKE_LOCK` | 168 | -- | 低功耗 | -- |
| `DRAW_WAKE_LOCK` | 182 | -- | 仅绘制 | -- |

所有屏幕级唤醒锁已 `@Deprecated`，推荐使用 `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON`。

**唤醒锁标志：**
- `ACQUIRE_CAUSES_WAKEUP`（第 202 行）—— 获取时点亮屏幕
- `ON_AFTER_RELEASE`（第 214 行）—— 释放时触发用户活动计时器

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `newWakeLock(int, String)` | 1101 | 创建新的 WakeLock |
| `isWakeLockLevelSupported(int)` | 1418 | 检查是否支持该级别 |
| `isInteractive()` | ~1450 | 设备是否处于唤醒状态 |
| `isDeviceIdleMode()` | ~1500 | 是否处于 Doze 模式 |
| `isPowerSaveMode()` | ~1480 | 省电模式是否激活 |
| `isScreenOn()` | ~已废弃 | 改用 `isInteractive()` |

**WakeLock 内部类（第 2320 行）：**
- `acquire()` / `acquire(long timeout)` —— 获取唤醒锁，可选自动释放
- `release()` / `release(int flags)` —— 释放唤醒锁
- `setReferenceCounted(boolean)` —— 默认 true，必须平衡 acquire/release
- `setWorkSource(WorkSource)` —— 将功耗归因到另一个 UID

**需要权限：** 使用任何 WakeLock 都需要 `android.permission.WAKE_LOCK`。

**隐藏/系统 API（`@SystemApi`）：**
- 用户活动事件类型：`USER_ACTIVITY_EVENT_OTHER/BUTTON/TOUCH/ACCESSIBILITY`（第 295-316 行）
- 睡眠原因代码：`GO_TO_SLEEP_REASON_APPLICATION/TIMEOUT/POWER_BUTTON/...`（第 354-416 行）
- `GO_TO_SLEEP_FLAG_NO_DOZE`（第 446 行）—— 跳过 Doze 状态
- 各种亮度常量（第 240-285 行）
- `DOZE_WAKE_LOCK` 和 `DRAW_WAKE_LOCK` 需要 `DEVICE_POWER` 权限

### 7.2 BatteryManager

**文件：** `frameworks/base/core/java/android/os/BatteryManager.java`（403 行）

**用途：** 电池状态信息和属性查询。

**获取方式：** `context.getSystemService(Context.BATTERY_SERVICE)`（`@SystemService`）

**`ACTION_BATTERY_CHANGED` 的 Intent Extra：**

| Extra | 行号 | 类型 | 描述 |
|-------|------|------|-------------|
| `EXTRA_STATUS` | 42 | int | 充电状态常量 |
| `EXTRA_HEALTH` | 48 | int | 电池健康状态常量 |
| `EXTRA_PRESENT` | 54 | boolean | 电池是否存在 |
| `EXTRA_LEVEL` | 61 | int | 当前电量（0 到 EXTRA_SCALE） |
| `EXTRA_SCALE` | 75 | int | 最大电量 |
| `EXTRA_PLUGGED` | 90 | int | 电源类型 |
| `EXTRA_VOLTAGE` | 96 | int | 当前电压 |
| `EXTRA_TEMPERATURE` | 102 | int | 当前温度 |
| `EXTRA_TECHNOLOGY` | 108 | String | 电池技术 |

**状态常量：** `BATTERY_STATUS_UNKNOWN/CHARGING/DISCHARGING/NOT_CHARGING/FULL`（第 168-172 行）

**健康常量：** `BATTERY_HEALTH_UNKNOWN/GOOD/OVERHEAT/DEAD/OVER_VOLTAGE/UNSPECIFIED_FAILURE/COLD`（第 175-181 行）

**插头类型：** `BATTERY_PLUGGED_AC`（1）、`BATTERY_PLUGGED_USB`（2）、`BATTERY_PLUGGED_WIRELESS`（4）（第 186-190 行）

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `isCharging()` | 285 | 电池电量是否在增加 |
| `getIntProperty(int)` | 329 | 查询 int 类型电池属性 |
| `getLongProperty(int)` | 349 | 查询 long 类型电池属性 |
| `computeChargeTimeRemaining()` | 373 | 预计充满电的剩余时间 |

**电池属性 ID：**
- `BATTERY_PROPERTY_CHARGE_COUNTER`（1）—— 容量，单位微安时
- `BATTERY_PROPERTY_CURRENT_NOW`（2）—— 瞬时电流，单位微安
- `BATTERY_PROPERTY_CURRENT_AVERAGE`（3）—— 平均电流
- `BATTERY_PROPERTY_CAPACITY`（4）—— 剩余百分比
- `BATTERY_PROPERTY_ENERGY_COUNTER`（5）—— 剩余能量，单位纳瓦时
- `BATTERY_PROPERTY_STATUS`（6）—— 充电状态

**隐藏 API：**
- `EXTRA_INVALID_CHARGER`、`EXTRA_MAX_CHARGING_CURRENT`、`EXTRA_MAX_CHARGING_VOLTAGE`（第 117-133 行）
- `setChargingStateUpdateDelayMillis(int)`（第 396 行，`@SystemApi`）—— 基于机器学习/启发式的充电状态延迟

---

## 8. SystemClock

**文件：** `frameworks/base/core/java/android/os/SystemClock.java`（345 行）

**用途：** 核心计时工具，提供多个时钟源用于不同用途。

**三种时钟类型（记录在类 Javadoc 中，第 36-72 行）：**

| 时钟 | 方法 | 休眠时停止 | 可调整 | 用途 |
|-------|--------|---------------|------------|----------|
| 挂钟 | `System.currentTimeMillis()` | 否 | 是（用户/网络） | 日历、真实世界日期 |
| 运行时间 | `uptimeMillis()` | 是 | 否 | 间隔计时、Handler |
| 经过的实时时间 | `elapsedRealtime()` | 否 | 否 | 通用间隔计时 |

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `sleep(long ms)` | 124 | 类似 `Thread.sleep()` 但忽略 `InterruptedException` |
| `uptimeMillis()` | 178 | 自启动以来的毫秒数（不包括深度睡眠） |
| `elapsedRealtime()` | 201 | 自启动以来的毫秒数（包括深度睡眠） |
| `elapsedRealtimeNanos()` | 224 | 自启动以来的纳秒数（包括深度睡眠） |
| `currentThreadTimeMillis()` | 232 | 线程 CPU 时间（毫秒） |
| `setCurrentTimeMillis(long)` | 153 | 设置挂钟（需要权限） |
| `currentGnssTimeClock()` | 322 | GNSS 同步时钟 |

**重要说明：** 大多数 Android 框架计时使用 `uptimeMillis()` 作为时间基准。Handler 延迟、动画计时和触摸事件时间戳都使用此时钟。深度睡眠会暂停此时钟，因此延迟的消息将在唤醒后以累积延迟执行。

**隐藏 API：**
- `currentNetworkTimeMillis()`（第 273 行）—— NTP 同步时间
- `currentTimeMicro()`（第 254 行）—— 挂钟时间，微秒
- `currentThreadTimeMicro()`（第 243 行）—— 线程 CPU 时间，微秒

**`sleep()` 的实现（第 124-145 行）：** 与 `Thread.sleep()` 不同，此方法在完成完整的睡眠持续时间后通过重新中断线程来保留中断状态。使用循环来处理中断而不丢失睡眠时间。

---

## 9. 进程管理

**文件：** `frameworks/base/core/java/android/os/Process.java`（1445 行）

**用途：** 管理操作系统进程的工具，包括进程创建、UID/GID 定义、线程优先级和信号。

**常用 UID 常量：**

| 常量 | 值 | 行号 | 描述 |
|----------|-------|------|-------------|
| `ROOT_UID` | 0 | 56 | Root 用户 |
| `SYSTEM_UID` | 1000 | 61 | 系统服务器 |
| `PHONE_UID` | 1001 | 66 | 电话 |
| `BLUETOOTH_UID` | 1002 | 141 | 蓝牙 |
| `SHELL_UID` | 2000 | 71 | ADB shell |
| `WIFI_UID` | 1010 | 84 | WiFi 服务 |
| `FIRST_APPLICATION_UID` | 10000 | 256 | 第一个应用 UID |
| `LAST_APPLICATION_UID` | 19999 | 262 | 最后一个应用 UID |
| `FIRST_ISOLATED_UID` | 99000 | 291 | 第一个隔离进程 UID |

**线程优先级常量（第 332-425 行）：**

| 常量 | 值 | 行号 | 描述 |
|----------|-------|------|-------------|
| `THREAD_PRIORITY_DEFAULT` | 0 | 332 | 标准应用 |
| `THREAD_PRIORITY_LOWEST` | 19 | 347 | 最低优先级 |
| `THREAD_PRIORITY_BACKGROUND` | 10 | 357 | 后台线程 |
| `THREAD_PRIORITY_FOREGROUND` | -2 | 368 | 交互式 UI |
| `THREAD_PRIORITY_DISPLAY` | -4 | 378 | 显示更新 |
| `THREAD_PRIORITY_URGENT_DISPLAY` | -8 | 388 | 合成/输入 |
| `THREAD_PRIORITY_AUDIO` | -16 | 406 | 音频处理 |
| `THREAD_PRIORITY_URGENT_AUDIO` | -19 | 415 | 关键音频 |

**关键公共 API：**

| 方法 | 描述 |
|--------|-------------|
| `myPid()` | 当前进程 PID |
| `myTid()` | 当前线程 TID |
| `myUid()` | 当前进程 UID |
| `myUserHandle()` | 当前用户句柄 |
| `setThreadPriority(int)` | 设置调用线程的优先级 |
| `setThreadPriority(int tid, int priority)` | 设置特定线程的优先级 |
| `getThreadPriority(int tid)` | 获取线程的当前优先级 |
| `killProcess(int pid)` | 向进程发送 SIGKILL |
| `sendSignal(int pid, int signal)` | 向进程发送信号 |

**信号常量：** `SIGNAL_QUIT`（3）、`SIGNAL_KILL`（9）、`SIGNAL_USR1`（10）（第 532-534 行）

**隐藏进程管理：**
- Zygote 策略标志（第 555-580 行）：`ZYGOTE_POLICY_FLAG_LATENCY_SENSITIVE`、`ZYGOTE_POLICY_FLAG_BATCH_LAUNCH`、`ZYGOTE_POLICY_FLAG_SYSTEM_PROCESS`
- 线程组（第 467-530 行）：`THREAD_GROUP_DEFAULT`、`THREAD_GROUP_BACKGROUND`、`THREAD_GROUP_TOP_APP` 等
- 调度策略（第 430-455 行）：`SCHED_OTHER`、`SCHED_FIFO`、`SCHED_RR`、`SCHED_BATCH`、`SCHED_IDLE`

---

## 10. UserManager

**文件：** `frameworks/base/core/java/android/os/UserManager.java`（4370 行）

**用途：** 管理多用户 Android 设备上的用户和用户配置文件。

**获取方式：** `context.getSystemService(Context.USER_SERVICE)`（`@SystemService`）

**用户类型常量（`@SystemApi`、`@hide`）：**

| 常量 | 行号 | 描述 |
|----------|------|-------------|
| `USER_TYPE_FULL_SYSTEM` | 100 | 系统用户（预先存在） |
| `USER_TYPE_FULL_SECONDARY` | 108 | 普通次要用户 |
| `USER_TYPE_FULL_GUEST` | 115 | 访客用户 |
| `USER_TYPE_FULL_DEMO` | 121 | 演示用户 |
| `USER_TYPE_FULL_RESTRICTED` | 128 | 受限配置文件 |
| `USER_TYPE_PROFILE_MANAGED` | 137 | 工作配置文件（DPC 管理） |
| `USER_TYPE_SYSTEM_HEADLESS` | 146 | 非人类系统用户 |

**关键公共 API（精选）：**

| 方法 | 描述 |
|--------|-------------|
| `isUserAGoat()` | 彩蛋 —— 检查是否安装了 com.coffeestainstudios.goatsimulator |
| `getUserCount()` | 设备上的用户数量 |
| `isUserRunning(UserHandle)` | 用户进程是否已启动 |
| `isUserUnlocked()` | 用户存储是否已解锁 |
| `isSystemUser()` | 当前用户是否为系统用户 |
| `isManagedProfile()` | 是否在工作配置文件中运行 |
| `isGuestUser()` | 当前用户是否为访客 |
| `isDemoUser()` | 当前用户是否为演示用户 |
| `isQuietModeEnabled(UserHandle)` | 静默模式是否激活 |
| `requestQuietModeEnabled(boolean, UserHandle)` | 切换静默模式 |

**用户限制系统：** 大量 `DISALLOW_*` 字符串常量（数百个），用于限制用户能力：
- `DISALLOW_INSTALL_APPS`、`DISALLOW_UNINSTALL_APPS`
- `DISALLOW_USB_FILE_TRANSFER`、`DISALLOW_MODIFY_ACCOUNTS`
- `DISALLOW_CONFIG_WIFI`、`DISALLOW_CONFIG_BLUETOOTH`
- `DISALLOW_CAMERA`、`DISALLOW_MICROPHONE`
- `DISALLOW_OUTGOING_CALLS`、`DISALLOW_SMS`
- 等等。

这些通过 `DevicePolicyManager` 设置，并通过 `getUserRestrictions()` / `hasUserRestriction(String)` 查询。

---

## 11. 权限管理器

**文件：** `frameworks/base/core/java/android/permission/PermissionManager.java`（719 行）

**用途：** 用于访问权限能力的系统级服务。几乎全部为 `@hide` / `@SystemApi`。

**获取方式：** `context.getSystemService(Context.PERMISSION_SERVICE)`（`@SystemService`）

**唯一的公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `getSplitPermissions()` | 170 | 获取跨 API 级别拆分的权限 |

**拆分权限（第 152-186 行）：**

`SplitPermissionInfo` 类（第 386 行）表示在更新的 API 级别中被拆分的权限。例如，`ACCESS_COARSE_LOCATION` 被拆分：目标 < Q 的应用在被授予旧权限时会自动获得 `ACCESS_BACKGROUND_LOCATION`。

`SplitPermissionInfo` 的关键方法：
- `getSplitPermission()` —— 原始权限
- `getNewPermissions()` —— 新的细粒度权限
- `getTargetSdk()` —— 发生拆分的 API 级别

**系统 API（`@SystemApi`）：**

| 方法 | 行号 | 所需权限 | 描述 |
|--------|------|-------------------|-------------|
| `getRuntimePermissionsVersion()` | 123 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | 获取权限数据库版本 |
| `setRuntimePermissionsVersion(int)` | 144 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | 设置权限数据库版本 |
| `getAutoRevokeExemptionRequestedPackages()` | 325 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | 自动撤销豁免请求的包 |
| `getAutoRevokeExemptionGrantedPackages()` | 345 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | 已授予自动撤销豁免的包 |
| `startOneTimePermissionSession(...)` | 475 | `MANAGE_ONE_TIME_PERMISSION_SESSIONS` | 一次性权限跟踪 |
| `stopOneTimePermissionSession(String)` | 496 | `MANAGE_ONE_TIME_PERMISSION_SESSIONS` | 停止一次性会话 |
| `checkDeviceIdentifierAccess(...)` | 519 | -- | 检查设备 ID 访问权限 |

**一次性权限（Android 11 特性，第 442-503 行）：**

`startOneTimePermissionSession()` 方法实现了 Android 11 的一次性权限授予。参数控制：
- `timeoutMillis` —— 应用不活跃多长时间后撤销
- `importanceToResetTimer` —— 重置计时器的进程重要性级别
- `importanceToKeepSessionAlive` —— 延长会话的重要性级别

**权限缓存（第 605-630 行）：**

`PermissionManager` 维护一个 `PropertyInvalidatedCache` 用于权限检查，以 `(permission, uid)` 为键 —— 注意 `pid` 包含用于追踪但不参与相等性比较（第 555-602 行）。这是一个重要的安全细节：权限检查基于 UID 而非 PID。

**隐藏电话权限授予（第 197-312 行）：**
多个方法用于向电话组件授予默认权限：
- `grantDefaultPermissionsToLuiApp()`
- `grantDefaultPermissionsToEnabledImsServices()`
- `grantDefaultPermissionsToEnabledTelephonyDataServices()`
- `grantDefaultPermissionsToEnabledCarrierApps()`

全部需要 `GRANT_RUNTIME_PERMISSIONS_TO_TELEPHONY_DEFAULTS` 权限。

### 相关权限文件

**文件：** `frameworks/base/core/java/android/permission/PermissionControllerService.java`
权限控制器应用必须实现的抽象 `Service`。处理运行时权限 UI 和一次性权限超时。

**文件：** `frameworks/base/core/java/android/permission/PermissionControllerManager.java`
与 PermissionControllerService 通信的客户端接口。

---

## 12. 安全 API

### 12.1 NetworkSecurityPolicy

**文件：** `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java`（119 行）

**用途：** 控制进程的明文（非 TLS）网络流量策略。

**关键公共 API：**

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `getInstance()` | 45 | 获取单例策略实例 |
| `isCleartextTrafficPermitted()` | 68 | 检查进程级是否允许明文流量 |
| `isCleartextTrafficPermitted(String hostname)` | 78 | 检查特定主机名 |

委托给 `libcore.net.NetworkSecurityPolicy`，后者由网络安全配置 XML 框架配置。

**隐藏 API：**
- `setCleartextTrafficPermitted(boolean)`（第 91 行）—— 在应用初始化期间使用
- `handleTrustStorageUpdate()`（第 100 行）—— 刷新证书信任存储
- `getApplicationConfigForPackage(Context, String)`（第 112 行）—— 获取包的安全配置

### 12.2 ConfirmationPrompt

**文件：** `frameworks/base/core/java/android/security/ConfirmationPrompt.java`

**用途：** 由 Android 受保护确认（硬件支持）支持的可信 UI 确认对话框。

### 12.3 FileIntegrityManager

**文件：** `frameworks/base/core/java/android/security/FileIntegrityManager.java`

**用途：** 提供对文件完整性功能（fs-verity）的访问。

### 12.4 Android Keystore

**目录：** `frameworks/base/keystore/java/android/security/keystore/`

**关键文件：**
- `AndroidKeyStoreProvider.java` —— Android Keystore 的 JCA 提供程序
- `AndroidKeyStoreSpi.java` —— `KeyStoreSpi` 实现
- `AndroidKeyStoreKeyGeneratorSpi.java` —— 对称密钥生成
- `AndroidKeyStoreKeyPairGeneratorSpi.java` —— 非对称密钥对生成
- `KeyGenParameterSpec.java` —— 密钥生成参数
- `KeyProtection.java` —— 导入保护参数
- `KeyProperties.java` —— 密钥算法/用途/模式常量

Android Keystore 是一个 JCA 提供程序，将加密密钥存储在硬件支持的存储中（TEE/StrongBox）。密钥绑定到设备，可以要求用户认证后才能使用。

---

## 13. 架构模式与观察总结

### 13.1 Handler/Looper/MessageQueue 线程模型

这三个类实现了**单线程事件循环**模式。关键设计特性：

1. **线程亲和性：** 每个 Handler 永久绑定到一个 Looper/线程。发送到 Handler 的消息始终在该线程上执行。
2. **协作式多任务：** 消息逐个处理。长时间运行的处理程序会阻塞整个队列。
3. **通过同步屏障实现优先级：** 框架使用同步屏障 + 异步消息来实现渲染优先级，而非抢占。
4. **原生集成：** `MessageQueue.next()` 在原生 `epoll_wait` 中阻塞，使事件循环在空闲时高效运行。

### 13.2 Binder IPC 安全模型

1. **调用者身份：** `getCallingUid()` 和 `getCallingPid()` 提供 IPC 调用者的身份用于权限检查。
2. **身份清除：** 当系统服务代表调用者进行调用时，`clearCallingIdentity()` / `restoreCallingIdentity()` 至关重要。
3. **死亡通知：** `linkToDeath()` 使得在远程进程死亡时进行清理成为可能。
4. **事务大小限制：** 64KB（`MAX_IPC_SIZE`）。大数据必须使用 `ashmem` 或内容提供者。
5. **单向语义：** `FLAG_ONEWAY` 提供即发即忘的 IPC，并保证每个 IBinder 的顺序。
6. **工作源归因：** `setCallingWorkSourceUid()` 允许将电池使用归因到真正的发起者。

### 13.3 Parcel/Parcelable 与 Serializable

Parcelable 是 Android 首选的序列化方式：
- **性能：** 比 Serializable 快约 10 倍（无反射）
- **范围：** 仅限 IPC，不适用于持久存储
- **类型安全：** 通过 CREATOR 模式进行编译时检查
- **IBinder 支持：** 可以跨进程编组活跃的 Binder 引用

### 13.4 隐藏 API 表面

操作系统 API 的很大一部分对应用开发者是隐藏的：

| 类 | 总行数 | 隐藏/系统 API 密度 |
|-------|------------|--------------------------|
| PowerManager | 2,634 | 非常高 —— 大多数常量和方法是 `@hide`/`@SystemApi` |
| UserManager | 4,370 | 非常高 —— 用户类型、限制大量隐藏 |
| PermissionManager | 719 | 几乎完全是 `@hide`/`@SystemApi` |
| Process | 1,445 | 高 —— 大多数 UID 常量和线程组隐藏 |
| Environment | 1,434 | 中等 —— 许多分区目录是 `@SystemApi` |
| Build | 1,335 | 低 —— 大多数字段是公开的 |
| Handler | 1,001 | 低 —— `runWithScissors` 和异步构造函数隐藏 |

### 13.5 Android 11 废弃模式

| API | 替代方案 |
|-----|------------|
| `Handler()`（无参） | `Handler(Looper.getMainLooper())` |
| `AsyncTask` | `java.util.concurrent`、Kotlin 协程 |
| `SCREEN_DIM_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `SCREEN_BRIGHT_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `FULL_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `Build.SERIAL` | `Build.getSerial()`（需要权限） |
| `Environment.getExternalStorageDirectory()` | 通过 `MediaStore` / `Context.getExternalFilesDir()` 使用分区存储 |

### 13.6 代码中可见的 Android 11 特定功能

1. **一次性权限**（PermissionManager，第 442 行）—— 应用不活跃时自动撤销的权限
2. **自动撤销**（PermissionManager，第 315-352 行）—— 未使用权限的自动撤销及豁免机制
3. **分区存储强制执行**（Environment，第 94-132 行）—— `DEFAULT_SCOPED_STORAGE` 和 `FORCE_ENABLE_SCOPED_STORAGE` 变更 ID
4. **Handler 默认构造函数废弃** —— 要求显式指定 Looper
5. **AsyncTask 完全废弃** —— 整个类标记为 `@Deprecated`
6. **Process.ZYGOTE_POLICY_FLAG_LATENCY_SENSITIVE**（第 564 行）—— USAP（非专用应用进程）池支持

---

*报告基于 `~/aosp-android-11/` 的 Android 11（API 级别 30）AOSP 源代码生成。*
