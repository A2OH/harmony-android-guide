# OpenHarmony 4.1 分布式数据管理子系统 - 代码审查

## 目录

- [1. 执行摘要](#1-执行摘要)
- [2. 架构概述](#2-架构概述)
- [3. 分布式 KV 存储](#3-分布式-kv-存储)
  - [3.1 SingleStoreImpl 线程安全性](#31-singlestoreimpl-线程安全性)
  - [3.2 引用计数竞态条件](#32-引用计数竞态条件)
  - [3.3 安全管理器 - 加密中使用静态 Nonce/AAD](#33-安全管理器---加密中使用静态-nonceaad)
  - [3.4 自动同步定时器设计](#34-自动同步定时器设计)
  - [3.5 Store Factory Convertor 生命周期](#35-store-factory-convertor-生命周期)
  - [3.6 根密钥生成的无限重试](#36-根密钥生成的无限重试)
- [4. 关系数据库 (RDB)](#4-关系数据库-rdb)
  - [4.1 通过表名/列名的 SQL 注入](#41-通过表名列名的-sql-注入)
  - [4.2 ExecuteSql 接受来自应用层的原始 SQL](#42-executesql-接受来自应用层的原始-sql)
  - [4.3 连接池 - 原始指针所有权](#43-连接池---原始指针所有权)
  - [4.4 BatchInsert 在部分失败时返回 E_OK](#44-batchinsert-在部分失败时返回-e_ok)
  - [4.5 事务栈非线程安全](#45-事务栈非线程安全)
  - [4.6 AbsPredicates 字段名注入](#46-abspredicates-字段名注入)
  - [4.7 RDB Store Manager 使用弱指针的缓存](#47-rdb-store-manager-使用弱指针的缓存)
- [5. DataShare 提供者/消费者](#5-datashare-提供者消费者)
  - [5.1 静态 blockId_ 计数器溢出](#51-静态-blockid_-计数器溢出)
  - [5.2 GetDataType 缺少边界检查](#52-getdatatype-缺少边界检查)
  - [5.3 ClosedBlock 双重删除风险](#53-closedblock-双重删除风险)
  - [5.4 反序列化不验证输入](#54-反序列化不验证输入)
- [6. Preferences](#6-preferences)
  - [6.1 GetAll 返回内部 Map 且无锁保护](#61-getall-返回内部-map-且无锁保护)
  - [6.2 静态执行器池在所有实例间共享](#62-静态执行器池在所有实例间共享)
  - [6.3 Delete 不调用 AwaitLoadFile](#63-delete-不调用-awaitloadfile)
  - [6.4 观察者通知在锁外执行](#64-观察者通知在锁外执行)
- [7. 文件管理 API](#7-文件管理-api)
  - [7.1 文件打开不验证路径遍历](#71-文件打开不验证路径遍历)
  - [7.2 URI 方案处理可能允许意外分派](#72-uri-方案处理可能允许意外分派)
- [8. 分布式数据服务 (datamgr_service)](#8-分布式数据服务-datamgr_service)
  - [8.1 Bundle Checker 信任映射未受保护](#81-bundle-checker-信任映射未受保护)
  - [8.2 安全级别执行缺口](#82-安全级别执行缺口)
  - [8.3 Route Head Handler - userId 通过 atoi 解析](#83-route-head-handler---userid-通过-atoi-解析)
- [9. 跨领域问题](#9-跨领域问题)
  - [9.1 API 设计一致性](#91-api-设计一致性)
  - [9.2 错误码传播](#92-错误码传播)
  - [9.3 敏感数据日志记录](#93-敏感数据日志记录)
- [10. 按严重级别汇总的发现](#10-按严重级别汇总的发现)

---

## 1. 执行摘要

OpenHarmony 4.1 中的分布式数据管理子系统提供 KV 存储、关系数据库 (RDB)、DataShare（内容提供者模式）和 Preferences（键值 XML 存储）功能。代码总体结构良好，对 IPC 框架的使用合理，错误处理也比较规范。然而，审查发现了多个安全相关问题，包括 KV 存储加密中的静态 nonce 重用、RDB 中通过未转义的表名/列名的 SQL 注入、KV 存储中线程不安全的引用计数器、以及文件管理 API 中不充分的路径验证。最关键的发现集中在密码学弱点和 SQL 注入风险上。

---

## 2. 架构概述

该子系统组织如下：

| 组件 | 路径 | 用途 |
|------|------|------|
| KV 存储 | `distributeddatamgr/kv_store/` | 支持跨设备同步的分布式键值存储 |
| 关系存储 | `distributeddatamgr/relational_store/` | 基于 SQLite 的关系数据库，支持分布式同步 |
| DataShare | `distributeddatamgr/data_share/` | 基于 IPC 的数据共享（类似 Android ContentProvider） |
| Preferences | `distributeddatamgr/preferences/` | 简单的键值 XML 持久化 |
| 数据管理服务 | `distributeddatamgr/datamgr_service/` | 协调分布式数据的系统服务 |
| 文件 API | `filemanagement/file_api/` | 文件系统访问 JS/NAPI 绑定 |

---

## 3. 分布式 KV 存储

### 3.1 SingleStoreImpl 线程安全性

**严重级别：中危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/single_store_impl.cpp`

该实现使用 `std::shared_mutex rwMutex_` 保护 `dbStore_`。读操作正确获取共享锁，`Close()` 在清空 `dbStore_` 之前获取独占锁。这是一个合理的模式。

然而，`GetEntries(const Key&)` 和 `GetResultSet(const Key&)` 等多个方法委托给内部重载时未自行获取锁 —— 锁只在最终的 `GetEntries(const DBQuery&)` 或 `GetResultSet(const DBQuery&)` 中获取。这是正确的，因为委托链最终会到达加锁的重载。该设计可以接受但较为脆弱：任何未来的重构如果在到达加锁方法之前调用 `dbStore_`，都会引入竞态条件。

### 3.2 引用计数竞态条件

**严重级别：高危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/include/single_store_impl.h`（第 121 行）、`single_store_impl.cpp`（第 644-666 行）

```cpp
int32_t ref_ = 1;

int32_t SingleStoreImpl::AddRef()
{
    ref_++;
    return ref_;
}

int32_t SingleStoreImpl::Close(bool isForce)
{
    if (isForce) {
        ref_ = 1;
    }
    ref_--;
    if (ref_ != 0) {
        return ref_;
    }
    // 清理...
}
```

`ref_` 是普通 `int32_t`，不是 `std::atomic<int32_t>`。`AddRef()` 和 `Close()` 都从 `StoreFactory` 的 `ConcurrentMap::Compute` 回调中调用，这提供了一定的同步。然而，`Close(isForce=true)` 执行 `ref_ = 1; ref_--;` 是两个非原子步骤，`OnRemoteDied()` / `Register()` 访问 `taskId_` 时也没有与 `Close()` 的同步。

`ref_` 字段应改为 `std::atomic<int32_t>`，或者所有访问都必须在同一把锁下进行。

### 3.3 安全管理器 - 加密中使用静态 Nonce/AAD

**严重级别：严重**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/security_manager.cpp`（第 33-36 行、第 180-225 行）

```cpp
SecurityManager::SecurityManager()
{
    vecRootKeyAlias_ = std::vector<uint8_t>(ROOT_KEY_ALIAS, ROOT_KEY_ALIAS + strlen(ROOT_KEY_ALIAS));
    vecNonce_ = std::vector<uint8_t>(HKS_BLOB_TYPE_NONCE, HKS_BLOB_TYPE_NONCE + strlen(HKS_BLOB_TYPE_NONCE));
    vecAad_ = std::vector<uint8_t>(HKS_BLOB_TYPE_AAD, HKS_BLOB_TYPE_AAD + strlen(HKS_BLOB_TYPE_AAD));
}
```

AES-256-GCM 加密的 nonce 和 AAD 源自静态字符串常量（`HKS_BLOB_TYPE_NONCE` 和 `HKS_BLOB_TYPE_AAD`）。在 AES-GCM 中，使用相同密钥时 nonce 重用是灾难性的失败：它允许攻击者恢复两个明文的异或值，并可能伪造认证标签。

由于相同的根密钥和相同的 nonce 用于每次数据库密钥加密，任何两个加密的数据库密钥都可以通过异或来恢复相对明文。正确的实现应该为每次加密操作使用唯一的随机 nonce，并将该 nonce 与密文一起存储。

### 3.4 自动同步定时器设计

**严重级别：低危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/auto_sync_timer.cpp`

`AutoSyncTimer` 使用批处理设计，`SYNC_STORE_NUM` 限制每个周期同步的存储数量。`ProcessTask` lambda 捕获 `this`，这是安全的，因为 `AutoSyncTimer` 是单例。设计是功能性的，但 `StopTimer` / `StartTimer` 的交互可能导致短暂的窗口期，在 `ProcessTask` 和 `DoAutoSync` 竞争时任务触发频率高于预期。

### 3.5 Store Factory Convertor 生命周期

**严重级别：中危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/store_factory.cpp`（第 39-42 行）

```cpp
StoreFactory::StoreFactory()
{
    convertors_[DEVICE_COLLABORATION] = new DeviceConvertor();
    convertors_[SINGLE_VERSION] = new Convertor();
    convertors_[MULTI_VERSION] = new Convertor();
    convertors_[LOCAL_ONLY] = new Convertor();
```

这些堆分配的 `Convertor` 对象从未被删除。由于 `StoreFactory` 是单例，这是"退出时泄漏"而非增长型泄漏，但仍然是不良实践。`SingleStoreImpl` 存储这些 convertor 的常量引用（`const Convertor &convertor_`），因此生命周期隐式绑定到单例。应使用 `unique_ptr` 或在单例内部栈分配。

### 3.6 根密钥生成的无限重试

**严重级别：中危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/security_manager.cpp`（第 48-68 行）

```cpp
bool SecurityManager::Retry()
{
    // ... 如果生成失败：
    constexpr int32_t interval = 100;
    TaskExecutor::GetInstance().Schedule(std::chrono::milliseconds(interval), [this] {
        Retry();
    });
    return false;
}
```

如果 HKS (HUKS Key Store) 服务永久不可用，此函数每 100 毫秒递归调度自身，无限进行。没有退避、没有重试限制、也没有停止方式。这可能消耗大量 CPU 并填满日志缓冲区。应实现重试限制或指数退避。

---

## 4. 关系数据库 (RDB)

### 4.1 通过表名/列名的 SQL 注入

**严重级别：严重**

**文件：** `relational_store/frameworks/native/rdb/src/rdb_store_impl.cpp`（第 468-509 行）、`sqlite_sql_builder.cpp`

`ValuesBucket` 键中的表名和列名被直接插入 SQL 字符串，未经转义或加引号：

```cpp
sql.append("INSERT").append(conflictClause).append(" INTO ").append(table).append("(");
// ...
for (const auto &[key, val] : initialValues.values_) {
    sql.append(split).append(key);  // 列名直接追加
```

类似地，在 `UpdateWithConflictResolution` 中：
```cpp
sql.append("UPDATE").append(conflictClause).append(" ").append(table).append(" SET ");
for (auto &[key, val] : values.values_) {
    sql.append(split);
    sql.append(key).append("=?"); // 列名直接追加
```

虽然值使用参数化查询（`?`），但表名和列名是直接拼接的。如果应用将用户控制的数据作为表名或列名传递，这构成 SQL 注入向量。`AbsPredicates::RemoveQuotes` 方法会去除引号，但不应用于构建器中的表名或列名。

`SqliteSqlBuilder::BuildDeleteString`、`BuildUpdateString`、`BuildQueryString` 都有相同的模式 —— `tableName` 直接追加到 SQL 字符串中。

**建议：** 用双引号或方括号转义包裹所有表名和列名（例如 `"tableName"`），并验证它们不包含特殊字符。

### 4.2 ExecuteSql 接受来自应用层的原始 SQL

**严重级别：高危**

**文件：** `relational_store/frameworks/native/rdb/src/rdb_store_impl.cpp`（第 776 行）、`frameworks/js/napi/rdb/src/napi_rdb_store.cpp`

`ExecuteSql` 方法接受任意 SQL 字符串。NAPI 绑定将其作为 `executeSql` 暴露给 JavaScript。虽然 RDB 运行在应用自己的沙箱中，但这仍然允许应用执行任意 DDL/DML，包括 `ATTACH DATABASE`、`PRAGMA` 或其他危险语句。`CheckAttach` 方法尝试防范 `ATTACH`，但检查是基于字符串的，可能通过 SQL 注释或异常空白字符绕过。

### 4.3 连接池 - 原始指针所有权

**严重级别：中危**

**文件：** `relational_store/frameworks/native/rdb/src/rdb_store_impl.cpp`（第 289-311 行）

```cpp
RdbStoreImpl::RdbStoreImpl(const RdbStoreConfig &config, int &errCode)
    : ... connectionPool(nullptr) ...
{
    errCode = InnerOpen();
    if (errCode != E_OK) {
        if (connectionPool) {
            delete connectionPool;
            connectionPool = nullptr;
        }
    }
}

RdbStoreImpl::~RdbStoreImpl()
{
    if (connectionPool) {
        delete connectionPool;
        connectionPool = nullptr;
    }
}
```

`connectionPool` 是一个通过 `new`/`delete` 手动管理的原始指针（`SqliteConnectionPool *`）。这容易出错 —— 如果在创建和清理之间的任何代码路径抛出异常，连接池就会泄漏。应使用 `std::unique_ptr<SqliteConnectionPool>`。

### 4.4 BatchInsert 在部分失败时返回 E_OK

**严重级别：高危**

**文件：** `relational_store/frameworks/native/rdb/src/rdb_store_impl.cpp`（第 332-371 行）

```cpp
for (const auto &[sql, bindArgs] : executeSqlArgs) {
    for (const auto &args : bindArgs) {
        auto errCode = connection->ExecuteSql(sql, args);
        if (errCode != E_OK) {
            outInsertNum = -1;
            connectionPool->ReleaseConnection(connection);
            // ... LOG_ERROR
            return E_OK;  // <-- 即使出错也返回 E_OK！
        }
    }
}
```

当 `BatchInsert` 在插入行的过程中失败时，它设置 `outInsertNum = -1` 但**返回 `E_OK`**。这对检查返回码并假定成功的调用者来说是误导性的。调用者必须同时检查返回码和 `outInsertNum`，这是容易出错的 API 设计。应返回指示部分失败的错误码。

### 4.5 事务栈非线程安全

**严重级别：中危**

**文件：** `relational_store/frameworks/native/rdb/src/sqlite_connection_pool.cpp`（第 314-322 行）

```cpp
std::stack<BaseTransaction> &SqliteConnectionPool::GetTransactionStack()
{
    return transactionStack_;
}

std::mutex &SqliteConnectionPool::GetTransactionStackMutex()
{
    return transactionStackMutex_;
}
```

事务栈及其互斥锁通过引用暴露，依赖调用者在访问栈之前正确加锁。这违反了封装性，使调用者容易忘记加锁，导致竞态条件。连接池应提供受保护的 push/pop 操作。

### 4.6 AbsPredicates 字段名注入

**严重级别：高危**

**文件：** `relational_store/frameworks/native/rdb/src/abs_predicates.cpp`

`CheckParameter` 方法仅验证字段名非空：
```cpp
bool AbsPredicates::CheckParameter(
    const std::string &methodName, const std::string &field, ...) const
{
    if (field.empty()) {
        return false;
    }
    // ...
    return true;
}
```

字段名然后被直接拼接到 WHERE 子句中：
```cpp
whereClause += field + " = ? ";
whereClause += field + " LIKE ? ";
whereClause += field + " > ? ";
```

没有验证 `field` 是否为合法的列标识符（不包含 SQL 特殊字符如 `;`、`--`、`'` 或括号）。虽然值使用参数化，但控制字段名字符串的攻击者可以将任意 SQL 注入 WHERE 子句。`OrderByAsc` 和 `OrderByDesc` 方法在 ORDER BY 子句中也有同样的漏洞。

`RemoveQuotes` 存在但仅应用于 `IndexedBy` —— 不应用于 WHERE、ORDER BY 或 GROUP BY 子句中的字段名。

### 4.7 RDB Store Manager 使用弱指针的缓存

**严重级别：低危**

**文件：** `relational_store/frameworks/native/rdb/src/rdb_store_manager.cpp`（第 57-92 行）

```cpp
if (storeCache_.find(path) != storeCache_.end()) {
    std::shared_ptr<RdbStoreImpl> rdbStore = storeCache_[path].lock();
    if (rdbStore != nullptr && rdbStore->GetConfig() == config) {
        return rdbStore;
    }
    storeCache_.erase(path);
}
```

缓存使用 `weak_ptr`，这对于避免阻止析构是正确的。然而，`storeCache_[path]` 执行了两次查找（find + operator[]）。轻微的低效；应使用 find 返回的迭代器。

第 61 行（"this lock should only work on storeCache_"）和第 69 行（"reconfigure store should be repeated this"）的 TODO 注释表明设计不完整，可能对并发访问模式有影响。

---

## 5. DataShare 提供者/消费者

### 5.1 静态 blockId_ 计数器溢出

**严重级别：低危**

**文件：** `data_share/frameworks/native/common/src/datashare_result_set.cpp`（第 35 行、第 43 行）

```cpp
int DataShareResultSet::blockId_ = 0;
// ...
std::string name = "DataShare" + std::to_string(blockId_++);
```

`blockId_` 是一个静态 `int`，无限递增且不是原子的。在长时间运行的系统服务中，这最终会溢出。由于它用作共享内存块名称，溢出后的名称冲突可能导致数据损坏或访问陈旧的共享内存。

### 5.2 GetDataType 缺少边界检查

**严重级别：中危**

**文件：** `data_share/frameworks/native/common/src/datashare_result_set.cpp`（第 110-121 行）

```cpp
int DataShareResultSet::GetDataType(int columnIndex, DataType &dataType)
{
    int rowCount = 0;
    GetRowCount(rowCount);
    AppDataFwk::SharedBlock::CellUnit *cellUnit =
        sharedBlock_->GetCellUnit(static_cast<uint32_t>(rowPos_) - startRowPos_, ...);
```

`GetDataType` 调用 `GetRowCount` 但未使用结果进行验证。它没有调用 `CheckState`（不像 `GetBlob` 和 `GetDouble`），因此不验证 `columnIndex` 边界或检查 `rowPos_` 是否处于有效状态。负的 `rowPos_`（初始状态 `-1`）减去 `startRowPos_` 在转换为 `uint32_t` 时会产生下溢。

### 5.3 ClosedBlock 双重删除风险

**严重级别：中危**

**文件：** `data_share/frameworks/native/common/src/datashare_result_set.cpp`（第 370-376 行）

```cpp
void DataShareResultSet::ClosedBlock()
{
    if (sharedBlock_ != nullptr) {
        delete sharedBlock_;
        sharedBlock_ = nullptr;
    }
}
```

`sharedBlock_` 指针由 `SetBlock()` 设置，后者接受外部提供的 block。如果外部调用者也删除了该 block，这会导致双重释放。所有权模型不明确 —— `DataShareBlockWriterImpl::GetBlock()` 创建 block，但 `SetBlock` 可以用任何指针替换它。应通过 unique_ptr 或明确记录来澄清所有权。

### 5.4 反序列化不验证输入

**严重级别：中危**

**文件：** `data_share/frameworks/native/common/src/datashare_result_set.cpp`（第 412-422 行）

```cpp
bool DataShareResultSet::Unmarshalling(MessageParcel &parcel)
{
    if (sharedBlock_ != nullptr) {
        return false;
    }
    int result = AppDataFwk::SharedBlock::ReadMessageParcel(parcel, sharedBlock_);
    if (result < 0) {
        LOG_ERROR("create from parcel error is %{public}d.", result);
    }
    return true;  // 即使出错也返回 true！
}
```

该方法即使在 `ReadMessageParcel` 失败时也返回 `true`。调用者会认为结果集有效，但实际上不是，在访问 `sharedBlock_` 时可能导致空指针解引用。

---

## 6. Preferences

### 6.1 GetAll 返回内部 Map 且无锁保护

**严重级别：中危**

**文件：** `preferences/frameworks/native/src/preferences_impl.cpp`（第 205-209 行）

```cpp
std::map<std::string, PreferencesValue> PreferencesImpl::GetAll()
{
    AwaitLoadFile();
    return map_;
}
```

`GetAll()` 等待文件加载完成，但在返回 `map_` 之前没有获取 `mutex_`。虽然 `std::map` 拷贝构造本身在拷贝操作上不会产生竞争，但如果另一个线程同时修改 `map_`（通过 `Put`、`Delete`、`Clear`），拷贝期间 `map_` 的内部状态可能不一致。应在返回前获取 `mutex_`。

### 6.2 静态执行器池在所有实例间共享

**严重级别：低危**

**文件：** `preferences/frameworks/native/src/preferences_impl.cpp`（第 90 行）

```cpp
ExecutorPool PreferencesImpl::executorPool_ = ExecutorPool(1, 0);
```

一个单线程的静态执行器池在进程中的所有 `PreferencesImpl` 实例间共享。这意味着如果多个 preferences 文件同时加载或写入，它们都会通过一个线程串行化。在具有多个 preference 文件的应用中可能导致延迟问题。

### 6.3 Delete 不调用 AwaitLoadFile

**严重级别：中危**

**文件：** `preferences/frameworks/native/src/preferences_impl.cpp`（第 470-486 行）

```cpp
int PreferencesImpl::Delete(const std::string &key)
{
    int errCode = CheckKey(key);
    if (errCode != E_OK) {
        return errCode;
    }

    std::lock_guard<std::mutex> lock(mutex_);
    // ... 修改 map_
}
```

`Delete()` 在修改 map 之前没有调用 `AwaitLoadFile()`，不像 `Put()` 和 `Get()`。如果在初始加载完成之前调用 `Delete`，删除可能在文件加载完成并填充 `map_` 时被覆盖。同样，`Clear()` 也没有调用 `AwaitLoadFile()`。

### 6.4 观察者通知在锁外执行

**严重级别：低危**

**文件：** `preferences/frameworks/native/src/preferences_impl.cpp`（第 543-563 行）

`notifyPreferencesObserver` 方法迭代观察者弱指针并在不持有 `mutex_` 的情况下调用 `OnChange`。这是有意为之的（为了避免回调重新调用 Preferences 时的死锁），但 `localObservers_` 列表是从请求对象复制的，因此可能存在陈旧/过期的观察者。弱指针模式通过 `lock()` 安全地处理了这种情况。

---

## 7. 文件管理 API

### 7.1 文件打开不验证路径遍历

**严重级别：高危**

**文件：** `filemanagement/file_api/interfaces/kits/js/src/mod_fs/properties/open.cpp`

`Open::Sync` 和 `Open::Async` 方法接受来自 JavaScript 的路径字符串，并将其直接传递给 `uv_fs_open`（通过 `OpenFileByPath`），未验证路径遍历序列（`../`）、空字节或符号链接攻击：

```cpp
int ret = uv_fs_open(nullptr, open_req.get(), path.c_str(), mode, S_IRUSR |
    S_IWUSR | S_IRGRP | S_IWGRP, nullptr);
```

沙箱模型提供了一些保护（应用以有限的文件系统访问权限运行），但在沙箱边界内，应用可以通过构造带有 `../` 序列的路径来访问不应访问的文件，以逃离其数据目录。`CheckPublicDirPath` 函数存在但未在打开路径中调用。

### 7.2 URI 方案处理可能允许意外分派

**严重级别：中危**

**文件：** `filemanagement/file_api/interfaces/kits/js/src/mod_fs/properties/open.cpp`（第 194-214 行）

```cpp
static tuple<int, string> OpenFileByUri(const string &path, unsigned int mode)
{
    Uri uri(path);
    string uriType = uri.GetScheme();
    if (uriType == SCHEME_FILE) {
        return OpenByFileDataUri(uri, path, mode);
    } else if (uriType == SCHEME_BROKER) {
        return OpenFileByBroker(uri, mode);
    } else if (uriType == DATASHARE) {
        // ...
    }
    return { -EINVAL, path };
}
```

URI 方案检测基于 `://` 子字符串检测（第 237 行：`pathStr.find("://") != string::npos`）。如果文件名合法地包含 `://`（不常见但可能），它将被视为 URI 而非路径。回退返回 `EINVAL`，因此不可被利用，但可能导致令人困惑的错误。

---

## 8. 分布式数据服务 (datamgr_service)

### 8.1 Bundle Checker 信任映射未受保护

**严重级别：中危**

**文件：** `datamgr_service/services/distributeddataservice/app/src/checker/bundle_checker.cpp`

```cpp
bool BundleChecker::SetTrustInfo(const CheckerManager::Trust &trust)
{
    trusts_[trust.bundleName] = trust.appId;
    return true;
}
```

`trusts_` 和 `distrusts_` 映射在无同步的情况下被访问。`SetTrustInfo` 写入映射，而 `GetAppId` 和 `IsDistrust` 从中读取。如果这些从不同线程调用（例如在系统初始化和 IPC 请求处理期间），这就是数据竞争。`BundleChecker::instance_` 是通过 `__attribute__((used))` 初始化的静态单例，因此并发访问是可能的。

### 8.2 安全级别执行缺口

**严重级别：中危**

**文件：** `datamgr_service/services/distributeddataservice/app/src/security/security.cpp`（第 131-134 行）

```cpp
bool Security::IsSupportSecurity()
{
    return false;
}
```

`IsSupportSecurity()` 方法始终返回 `false`。这意味着安全子系统报告不支持设备安全标签。结合：

```cpp
int32_t Security::GetCurrentUserStatus() const
{
    return NO_PWD;
}
```

始终返回 `NO_PWD`（无密码），安全检查实际上被禁用了。虽然这对开发版本可能是合适的，但如果此代码发布到生产环境，这意味着：
- 访问控制检查报告设备从未锁定
- 目录上的安全标签验证不受支持（`SetDirSecurityOption` / `GetDirSecurityOption` 返回 `NOT_SUPPORT`）

### 8.3 Route Head Handler - userId 通过 atoi 解析

**严重级别：中危**

**文件：** `datamgr_service/services/distributeddataservice/app/src/session_manager/route_head_handler_impl.cpp`（第 60 行）

```cpp
SessionPoint localPoint { DmAdapter::GetInstance().GetLocalDevice().uuid,
    static_cast<uint32_t>(atoi(userId_.c_str())), appId_, storeId_ };
```

使用 `atoi` 将 `userId_` 转换为整数。`atoi` 没有错误处理 —— 如果 `userId_` 为空或非数字，它会静默返回 0，这将映射到有效的用户 ID。应使用带错误检查的 `strtoul` 或 `std::stoul`。如果从 IPC 消息接收到格式错误的 userId，这可能导致会话混淆。

---

## 9. 跨领域问题

### 9.1 API 设计一致性

**严重级别：信息性**

API 暴露了旧版和新版两种模式：
- KV 存储同时有 `distributeddata`（旧版）和 `distributedkvstore`（新版）JS 绑定
- RDB 同时有 `data.rdb` 和 `data.relationalStore` 命名空间
- 存在两个独立的 `napi_rdb_store.cpp` 文件（一个在 `rdb/`，一个在 `relationalstore/`）

这种双重 API 造成了维护负担和潜在的分歧。

### 9.2 错误码传播

**严重级别：低危**

多个子系统定义了各自的错误码枚举（`store_errno.h`、`rdb_errno.h`、`datashare_errno.h`、`preferences_errno.h`），数值范围存在重叠。跨子系统调用（例如 DataShare 调用 RDB）需要错误码转换，这在某些路径中是不完整的。

### 9.3 敏感数据日志记录

**严重级别：中危**

**文件：** `kv_store/frameworks/innerkitsimpl/kvdb/src/single_store_impl.cpp`

`StoreUtil::Anonymous` 函数为日志截断标识符（保留前 3 个和后 3 个字符）。这是良好的实践。然而，在某些路径中完整的 `appId_` 被记录：

```cpp
ZLOGE("failed! invalid agent app:%{public}s, store:%{public}s!", appId_.c_str(), ...);
```

`%{public}s` 格式意味着这些出现在其他应用可访问的日志中。应用包名通常不敏感，但模式应保持一致。

---

## 10. 按严重级别汇总的发现

### 严重 (2)

| # | 发现 | 组件 |
|---|------|------|
| 1 | AES-GCM 加密中静态 nonce/AAD 重用违反 GCM 安全要求 | KV 存储 SecurityManager |
| 2 | SQL 字符串构造中通过未转义的表名和列名的 SQL 注入 | RDB SqliteSqlBuilder、RdbStoreImpl |

### 高危 (4)

| # | 发现 | 组件 |
|---|------|------|
| 3 | SingleStoreImpl 中非原子引用计数器 `ref_` 可能导致释放后使用 | KV 存储 SingleStoreImpl |
| 4 | `BatchInsert` 在批处理中失败时返回 `E_OK`，掩盖错误 | RDB RdbStoreImpl |
| 5 | AbsPredicates 中字段名未验证/转义，允许 WHERE 子句注入 | RDB AbsPredicates |
| 6 | 文件打开 API 未验证沙箱内路径遍历攻击 | 文件管理 open.cpp |

### 中危 (10)

| # | 发现 | 组件 |
|---|------|------|
| 7 | 根密钥生成时无退避的无限递归重试 | KV 存储 SecurityManager |
| 8 | StoreFactory 单例中堆分配的 Convertors 从未释放 | KV 存储 StoreFactory |
| 9 | RdbStoreImpl 中 connectionPool 的原始指针所有权 | RDB RdbStoreImpl |
| 10 | 事务栈通过引用暴露，绕过封装 | RDB SqliteConnectionPool |
| 11 | GetDataType 缺少与同类方法不同的边界/状态验证 | DataShare DataShareResultSet |
| 12 | ClosedBlock 所有权不明确，可能存在双重删除 | DataShare DataShareResultSet |
| 13 | Unmarshalling 即使失败也返回 true | DataShare DataShareResultSet |
| 14 | GetAll 在 map 拷贝期间未持有互斥锁 | Preferences PreferencesImpl |
| 15 | Delete/Clear 不调用 AwaitLoadFile | Preferences PreferencesImpl |
| 16 | Bundle checker 信任映射在无同步的情况下被访问 | 数据管理服务 BundleChecker |

### 低危 (5)

| # | 发现 | 组件 |
|---|------|------|
| 17 | 静态 blockId_ 计数器不是原子的且会溢出 | DataShare DataShareResultSet |
| 18 | 单线程执行器池在所有 Preferences 实例间共享 | Preferences PreferencesImpl |
| 19 | RdbStoreManager 缓存中的冗余 map 查找 | RDB RdbStoreManager |
| 20 | 子系统间不一致的错误码域 | 跨领域 |
| 21 | 弱指针观察者通知的正确性（安全但微妙） | Preferences PreferencesImpl |

### 信息性 (2)

| # | 发现 | 组件 |
|---|------|------|
| 22 | KV 存储和 RDB 的双重 API 界面（旧版 + 新版） | 跨领域 |
| 23 | 安全检查实际上被禁用（IsSupportSecurity 返回 false） | 数据管理服务 Security |
