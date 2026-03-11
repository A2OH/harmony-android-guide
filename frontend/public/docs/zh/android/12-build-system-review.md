# Android 11 AOSP 构建系统 - 代码审查报告

## 概述

Android 11 构建系统是一个双架构系统，由 **Soong** 构建系统（Android.bp 文件，使用 Go 编写，基于 Blueprint/Ninja）和**遗留 Make** 构建系统（Android.mk 文件）组成。Soong 是模块定义的主要构建系统，而 Make 处理产品配置、镜像组装和编排。构建系统管理 Java/Kotlin、C/C++、AIDL、资源（aapt2）、dex 化（d8/r8）、签名和打包的编译，支持多种架构和构建变体。

**关键源码位置：**
- `/build/soong/` -- Soong 构建系统（Go 实现）
- `/build/make/` -- Make 构建系统（Makefile 基础设施）
- `/build/make/core/` -- 核心 Make 规则和配置
- `/build/make/target/` -- 产品和板级定义
- `/build/blueprint/` -- Blueprint 元构建系统（Soong 的基础）

---

## 1. 构建系统架构概览

### 1.1 Soong（Android.bp）

Soong 是基于 Go 的构建系统，建立在 Google 的 Blueprint 框架之上。它解析 `Android.bp` 文件（一种类 JSON 的声明式格式），解析依赖关系，并生成 Ninja 构建文件。

**入口点：** `build/soong/soong_ui.bash`

**架构层次：**
1. **Blueprint**（`build/blueprint/`）-- 通用元构建系统，解析 `.bp` 文件并管理模块/依赖图
2. **Soong Android 包**（`build/soong/android/`）-- Android 特定的模块基类型、配置、变换器和路径处理
3. **语言特定包** -- `build/soong/java/`、`build/soong/cc/`、`build/soong/python/`、`build/soong/rust/`
4. **模块特定包** -- `build/soong/apex/`、`build/soong/genrule/`

**关键文件：** `build/soong/android/register.go` -- 中央模块类型注册：
```go
type ModuleFactory func() Module

func RegisterModuleType(name string, factory ModuleFactory) {
    moduleTypes = append(moduleTypes, moduleType{name, factory})
}
```

**关键文件：** `build/soong/android/module.go` -- 定义 `BuildParams`、`EarlyModuleContext`、`BaseModuleContext` 以及核心模块生命周期接口。每个模块的流程为：解析属性 -> 运行变换器 -> 解析依赖 -> 生成构建动作。

### 1.2 Make（Android.mk）

遗留 Make 系统负责：
- 产品/设备/板级配置
- 系统镜像组装
- 构建变体选择
- 与 Soong 输出的集成

**入口点：** `build/make/core/config.mk`（由 Kati 加载）

**关键目录：**
- `build/make/core/` -- 核心构建规则（约 100 多个 .mk 文件）
- `build/make/target/product/` -- 产品定义（aosp_arm64.mk、base_system.mk 等）
- `build/make/target/board/` -- 板级配置（generic_arm64 等）

**关键文件：** `build/make/core/clear_vars.mk` -- 在模块定义之间重置所有 `LOCAL_*` 变量。列出了 Make 系统使用的每个变量（200 多个变量，包括 `LOCAL_CERTIFICATE`、`LOCAL_AIDL_INCLUDES`、`LOCAL_AAPT_FLAGS` 等）。

### 1.3 构建流程

```
source build/envsetup.sh
    -> lunch <product>-<variant>
        -> soong_ui.bash
            -> Blueprint 解析 Android.bp 文件
            -> Soong 变换器运行（arch、SDK、VNDK、sanitizer 等）
            -> 生成 Ninja 文件
            -> Kati 处理 Android.mk 文件
            -> Ninja 执行合并的构建图
```

---

## 2. 构建环境设置

### 2.1 envsetup.sh

**文件：** `build/make/envsetup.sh`

将 Shell 函数加载到开发者的环境中。关键函数：

| 函数 | 用途 |
|----------|---------|
| `lunch` | 选择产品和构建变体 |
| `m` | 从代码树顶层构建 |
| `mm` | 构建当前目录中的模块及其依赖 |
| `mmm` | 构建指定目录中的模块 |
| `tapas` | 构建特定应用：`tapas App1 App2 arm64 userdebug` |
| `croot` | 导航到代码树顶层 |
| `gomod` | 转到包含某个模块的目录 |
| `allmod` | 列出所有模块 |

**辅助搜索命令：** `cgrep`（C/C++）、`jgrep`（Java）、`resgrep`（资源）、`mgrep`（Makefile/bp）、`sgrep`（所有源码）。

### 2.2 lunch 函数

**文件：** `build/make/envsetup.sh`（第 598 行）

`lunch` 函数接受 `<product>-<variant>` 字符串，将其解析为 `TARGET_PRODUCT` 和 `TARGET_BUILD_VARIANT`，并配置环境：

```bash
function lunch() {
    # 将选择解析为产品和变体
    product=${selection%%-*}
    variant_and_version=${selection#*-}

    TARGET_PRODUCT=$product \
    TARGET_BUILD_VARIANT=$variant \
    build_build_var_cache

    export TARGET_PRODUCT=$(get_build_var TARGET_PRODUCT)
    export TARGET_BUILD_VARIANT=$(get_build_var TARGET_BUILD_VARIANT)
    export TARGET_BUILD_TYPE=release
}
```

### 2.3 构建变体

**文件：** `build/make/envsetup.sh`（第 149 行）

三种构建变体：
```bash
VARIANT_CHOICES=(user userdebug eng)
```

| 变体 | 描述 |
|---------|-------------|
| `user` | 生产构建。有限的调试功能，`ro.debuggable=0` |
| `userdebug` | 类似 user 但启用了 root 访问和调试功能 |
| `eng` | 开发构建，包含额外的调试工具和宽松的安全策略 |

---

## 3. Soong 模块类型

### 3.1 Java 模块类型

**文件：** `build/soong/java/java.go` -- `RegisterJavaBuildComponents()`

| 模块类型 | 用途 |
|-------------|---------|
| `java_library` | 将 Java 源码编译为 jar |
| `java_library_static` | 静态 Java 库 |
| `java_library_host` | 仅主机端 Java 库 |
| `java_binary` | Java 二进制文件（设备端） |
| `java_binary_host` | Java 二进制文件（主机端） |
| `java_test` | Java 测试模块 |
| `java_test_host` | 主机端 Java 测试 |
| `java_import` | 预构建 jar 导入 |
| `java_defaults` | Java 模块的默认属性 |
| `dex_import` | 预构建 dex 导入 |

**关键 `CompilerProperties`**（来自 `build/soong/java/java.go` 第 142 行）：
```
srcs                 -- 源文件（.java、.kt、.aidl、.proto、.logtags）
exclude_srcs         -- 要排除的文件
libs                 -- 类路径依赖（不编译到 jar 中）
static_libs          -- 编译到结果 jar 中的依赖
plugins              -- 注解处理器模块
java_version         -- Java 源/目标版本
javacflags           -- 额外的 javac 标志
kotlincflags         -- Kotlin 编译器标志
jarjar_rules         -- 用于包重命名的 JarJar 规则文件
services             -- META-INF/services 条目
```

### 3.2 Android 应用模块类型

**文件：** `build/soong/java/app.go` -- `RegisterAppBuildComponents()`

| 模块类型 | 用途 |
|-------------|---------|
| `android_app` | 完整的 Android 应用 |
| `android_test` | Android 仪器测试 |
| `android_test_helper_app` | 测试辅助应用 |
| `android_app_certificate` | 签名证书定义 |
| `override_android_app` | 覆盖 android_app 的属性 |
| `android_app_import` | 导入预构建 APK |
| `android_app_set` | 导入 APK 集（拆分 APK） |
| `runtime_resource_overlay` | RRO 包 |

**关键 `appProperties`**（来自 `build/soong/java/app.go` 第 187 行）：
```
additional_certificates  -- 额外的签名证书
privileged              -- 安装到 priv-app 目录
package_splits          -- 资源拆分标签
overrides               -- 此模块覆盖的模块
jni_libs                -- 要包含的原生库
use_embedded_native_libs -- 在 APK 中以未压缩方式存储 JNI 库
use_embedded_dex        -- 以未压缩方式存储 dex
updatable               -- 应用是否可通过 Mainline 更新
certificate             -- 签名证书（可覆盖）
```

### 3.3 C/C++ 模块类型

**文件：** `build/soong/cc/cc.go` -- `RegisterCCBuildComponents()` 及相关文件

| 模块类型 | 用途 |
|-------------|---------|
| `cc_library` | 共享库 + 静态库（两者都构建） |
| `cc_library_shared` | 仅共享库 |
| `cc_library_static` | 仅静态库 |
| `cc_library_headers` | 仅头文件库 |
| `cc_binary` | 可执行文件 |
| `cc_binary_host` | 主机端可执行文件 |
| `cc_test` | 原生测试 |
| `cc_fuzz` | 模糊测试 |
| `cc_object` | 目标文件 |
| `cc_defaults` | 默认属性 |
| `cc_prebuilt_library_shared` | 预构建共享库 |
| `cc_prebuilt_binary` | 预构建二进制文件 |
| `ndk_library` | NDK 桩库 |
| `llndk_library` | LL-NDK 库 |
| `vndk_prebuilt_shared` | VNDK 预构建 |

**CC 变换器管道**（来自 `build/soong/cc/cc.go` 第 44 行）：
```
PreDepsMutators:  sdk -> vndk -> link -> ndk_api -> test_per_src -> version -> begin -> sysprop_cc -> vendor_snapshot
PostDepsMutators: asan -> hwasan -> fuzzer -> cfi -> scs -> tsan -> sanitize_runtime -> coverage -> vndk_deps -> lto
```

**关键 `LibraryProperties`**（来自 `build/soong/cc/library.go` 第 34 行）：
```
stubs.symbol_file    -- 用于桩生成的符号映射
stubs.versions       -- 要生成的桩版本
stem                 -- 输出文件名覆盖
suffix               -- 名称后缀
aidl.export_aidl_headers  -- 导出 AIDL 生成的头文件
```

### 3.4 其他模块类型

| 模块类型 | 文件 | 用途 |
|-------------|------|---------|
| `genrule` | `build/soong/genrule/genrule.go` | 带命令的自定义构建规则 |
| `gensrcs` | `build/soong/genrule/genrule.go` | 按文件生成源码 |
| `filegroup` | `build/soong/android/filegroup.go` | 分组源文件以供引用 |
| `apex` | `build/soong/apex/apex.go` | APEX 模块（可更新包） |
| `droiddoc` | `build/soong/java/droiddoc.go` | API 文档生成 |
| `droidstubs` | `build/soong/java/droiddoc.go` | API 桩生成（metalava） |
| `java_sdk_library` | `build/soong/java/sdk_library.go` | 带 API 管理的 SDK 库 |
| `python_binary_host` | `build/soong/python/` | 主机端 Python 二进制文件 |

---

## 4. SDK 和 API 级别配置

### 4.1 平台 SDK 版本

**文件：** `build/make/core/version_defaults.mk`

Android 11 定义：
```makefile
PLATFORM_VERSION_LAST_STABLE := 11
PLATFORM_SDK_VERSION := 30
PLATFORM_VERSION_CODENAME.RP1A := REL   # REL = 已发布
```

`PLATFORM_SDK_VERSION`（30）是规范的 API 级别。`FutureApiLevel` 在 `build/soong/android/config.go` 中定义为 10000，用于未发布的 API。

### 4.2 模块中的 SDK 版本指定

**文件：** `build/soong/java/sdk.go`

`sdkSpec` 系统定义了模块如何针对不同的 API 表面：

```go
type sdkKind int
const (
    sdkInvalid sdkKind = iota
    sdkNone            // 无 SDK（框架内部）
    sdkCore            // 仅核心 Java API
    sdkCorePlatform    // 核心平台 API（不稳定）
    sdkPublic          // 公共 SDK API
    sdkSystem          // 系统 API
    sdkTest            // 测试 API
    sdkModule          // 模块 API（用于 Mainline 模块）
    sdkSystemServer    // system_server API
    sdkPrivate         // 平台内部（未设置 sdk_version）
)
```

**模块属性**（来自 `build/soong/java/java.go` 第 261 行）：

| 属性 | 用途 |
|----------|---------|
| `sdk_version` | 编译所针对的 SDK（例如 `"current"`、`"system_current"`、`"30"`） |
| `min_sdk_version` | 运行时最低 SDK 版本 |
| `target_sdk_version` | 清单中的目标 SDK 版本 |
| `platform_apis` | 使用平台（私有）API 而非 SDK |
| `system_modules` | Java 9+ 的 Java 模块系统 |

**验证规则**（第 120-131 行）：`platform_apis` 和 `sdk_version` 互斥 -- android_app 模块必须且只能设置其中一个。

**SDK 版本字符串：** `"current"`（公共）、`"system_current"`（系统）、`"test_current"`（测试）、`"core_current"`（核心）、`"module_current"`（模块），或数字版本如 `"30"`。

### 4.3 SDK 稳定性强制执行

稳定的 API 表面（具有由 API 委员会审查的托管 .txt 文件）：
- `sdkNone`、`sdkCore`、`sdkPublic`、`sdkSystem`、`sdkModule`、`sdkSystemServer`

不稳定的表面：
- `sdkCorePlatform`、`sdkTest`、`sdkPrivate`

vendor/product 分区上的模块必须指定 `sdk_version`（不能使用平台 API）。

### 4.4 SDK 的产品变量

**文件：** `build/soong/android/variable.go`（第 139 行）

传递给 Soong 的关键产品变量：
```go
Platform_version_name                     *string
Platform_sdk_version                      *int     // Android 11 为 30
Platform_sdk_codename                     *string  // 发布版为 "REL"
Platform_sdk_final                        *bool
Platform_version_active_codenames         []string
Platform_vndk_version                     *string
Platform_systemsdk_versions               []string
Platform_min_supported_target_sdk_version *string
```

---

## 5. 产品、设备和板级配置

### 5.1 产品配置层次结构

**关键文件：**
- `build/make/core/product_config.mk` -- 产品配置加载逻辑
- `build/make/core/product.mk` -- 产品变量定义
- `build/make/target/product/` -- 产品定义文件

**产品定义示例**（`build/make/target/product/aosp_arm64.mk`）：
```makefile
# 继承基础配置
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/mainline_system.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/handheld_system_ext.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/telephony_system_ext.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_product.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/emulator_vendor.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/board/generic_arm64/device.mk)

PRODUCT_NAME := aosp_arm64
PRODUCT_DEVICE := generic_arm64
PRODUCT_BRAND := Android
PRODUCT_MODEL := AOSP on ARM64
```

**关键产品变量：**

| 变量 | 用途 |
|----------|---------|
| `PRODUCT_NAME` | 构建产品名称 |
| `PRODUCT_DEVICE` | 目标设备（映射到 BoardConfig） |
| `PRODUCT_BRAND` | 用于 build.prop 的品牌名称 |
| `PRODUCT_MODEL` | 用于 build.prop 的型号名称 |
| `PRODUCT_PACKAGES` | 要安装的模块 |
| `PRODUCT_COPY_FILES` | 要复制到输出的文件 |
| `PRODUCT_ENFORCE_ARTIFACT_PATH_REQUIREMENTS` | 路径验证 |

### 5.2 板级配置

**文件：** `build/make/core/board_config.mk`

板级配置定义硬件级属性。只读变量包括：

**架构：**
- `TARGET_ARCH`、`TARGET_ARCH_VARIANT`、`TARGET_CPU_ABI`、`TARGET_CPU_VARIANT`
- `TARGET_2ND_ARCH`、`TARGET_2ND_CPU_ABI`（用于 64 位上的 32 位支持）

**硬件：**
- `TARGET_BOARD_PLATFORM`、`TARGET_BOOTLOADER_BOARD_NAME`
- `TARGET_NO_BOOTLOADER`、`TARGET_NO_KERNEL`、`TARGET_NO_RECOVERY`

**分区：**
- `BOARD_SYSTEMIMAGE_PARTITION_SIZE`、`BOARD_VENDORIMAGE_PARTITION_SIZE`
- `BOARD_SYSTEMIMAGE_FILE_SYSTEM_TYPE`（ext4、f2fs 等）
- `BOARD_FLASH_BLOCK_SIZE`

**动态分区：**
- `BOARD_SYSTEMIMAGE_PARTITION_RESERVED_SIZE`
- `BOARD_VENDORIMAGE_PARTITION_RESERVED_SIZE`

### 5.3 基础系统包列表

**文件：** `build/make/target/product/base_system.mk`

通过 `PRODUCT_PACKAGES` 定义最小系统镜像内容。包括核心组件如 `app_process`、`bootanimation`、`surfaceflinger`、`audioserver`、APEX 模块（`com.android.conscrypt`、`com.android.media`、`com.android.wifi` 等）以及必要应用（`DownloadProvider`、`ContactsProvider` 等）。

---

## 6. AIDL 编译

### 6.1 Java AIDL 编译

**文件：** `build/soong/java/gen.go`（第 47 行）

Java 模块中的 AIDL 文件通过 `genAidl()` 函数编译：
1. AIDL 源文件被分片为每组 50 个
2. 每个分片由 `aidl` 主机工具处理
3. 生成的 .java 文件通过 `soong_zip` 打包为 srcjar 文件
4. Srcjar 被合并到模块的源码编译中

```go
func genAidl(ctx android.ModuleContext, aidlFiles android.Paths, aidlFlags string, deps android.Paths) android.Paths {
    shards := android.ShardPaths(aidlFiles, 50)
    for i, shard := range shards {
        // aidl -d <depfile> $FLAGS <input> <output.java>
        // soong_zip -o <srcjar> -C <dir> -D <dir>
    }
}
```

### 6.2 AIDL 配置属性

**文件：** `build/soong/java/java.go`（第 282 行，`CompilerDeviceProperties` 内）

```
aidl.include_dirs           -- 顶级包含目录
aidl.local_include_dirs     -- 相对于 Android.bp 的目录
aidl.export_include_dirs    -- 导出给依赖者的目录
aidl.generate_traces        -- 生成 systrace 支持
aidl.generate_get_transaction_name -- 生成 GetTransaction name 方法
```

### 6.3 C++ AIDL 支持

**文件：** `build/soong/cc/library.go`（第 45 行）
```
aidl.export_aidl_headers -- 在 cc_library 中导出从 .aidl 源生成的头文件
```

---

## 7. 资源编译（aapt2）

### 7.1 aapt2 编译管道

**文件：** `build/soong/java/aapt2.go`

资源编译使用 aapt2 进行分片管道处理（分片大小 = 100 个文件）：

1. **编译阶段：** 单个资源文件被编译为 `.flat` 格式
   - Values XML 文件：`values-[config]/<file>.xml` -> `values-[config]_<file>.arsc.flat`
   - 其他资源：替换最后一个 `/` 为 `_` 并添加 `.flat`

2. **链接阶段：** Flat 文件被链接到单个 APK 资源表中

```go
var aapt2CompileRule = pctx.AndroidStaticRule("aapt2Compile",
    blueprint.RuleParams{
        Command: `${config.Aapt2Cmd} compile -o $outDir $cFlags $in`,
    }, "outDir", "cFlags")
```

### 7.2 资源覆盖系统

**文件：** `build/soong/java/android_resources.go`

- 覆盖解析使用 `DEVICE_RESOURCE_OVERLAYS` 和 `PRODUCT_RESOURCE_OVERLAYS` 产品变量
- 运行时资源覆盖（RRO）由 `PRODUCT_ENFORCE_RRO_TARGETS` 控制
- RRO 包作为 `runtime_resource_overlay` 模块类型构建

### 7.3 忽略的文件

资源自动忽略：`.svn`、`.git`、`.ds_store`、`*.scc`、`.*`、`CVS`、`thumbs.db`、`picasa.ini`、`*~`

---

## 8. Dex 化：d8 和 r8

### 8.1 d8（DEX 编译器）

**文件：** `build/soong/java/dex.go`

d8 将 Java 字节码转换为 DEX 格式：
```
d8 ${DexFlags} --output $outDir $d8Flags $in
soong_zip $zipFlags -o $outDir/classes.dex.jar -C $outDir -f "$outDir/classes*.dex"
merge_zips -D -stripFile "**/*.class" $out $outDir/classes.dex.jar $in
```

### 8.2 r8（优化器 + 压缩器）

r8 执行优化、压缩和混淆（ProGuard 的替代品）：
```
r8 ${DexFlags} -injars $in --output $outDir \
    --force-proguard-compatibility --no-data-resources \
    -printmapping $outDict $r8Flags
```

### 8.3 优化属性

**文件：** `build/soong/java/java.go`（第 313 行）

```
optimize.enabled     -- 启用优化（默认：应用为 true，库为 false）
optimize.shrink      -- 移除未使用的代码（默认：应用为 true）
optimize.optimize    -- 优化字节码（默认：false）
optimize.obfuscate   -- 混淆代码（默认：false）
optimize.proguard_flags       -- 内联 ProGuard 标志
optimize.proguard_flags_files -- ProGuard 标志文件
optimize.no_aapt_flags        -- 跳过 aapt 生成的保留规则
```

---

## 9. Kotlin 支持

**文件：** `build/soong/java/kotlin.go`

Kotlin 编译直接集成到 Java 构建管道中：

1. `.kt` 和 `.java` 源文件由 `kotlinc` 一起编译
2. Kotlin 标准库可以打包到 jar 中（`static_kotlin_stdlib`，默认为 true）
3. Kotlin 编译器使用由 `GenKotlinBuildFileCmd` 生成的构建文件
4. 模块属性：`kotlincflags` 用于 Kotlin 特定的编译器标志

依赖项：`KotlincCmd`、`KotlinCompilerJar`、`KotlinStdlibJar`、`KotlinReflectJar`、`KotlinAnnotationJar`

---

## 10. 签名和密钥管理

### 10.1 默认签名密钥

**目录：** `build/make/target/product/security/`

| 密钥 | 用途 |
|-----|---------|
| `testkey` | 开发构建的默认签名密钥 |
| `platform` | 平台组件签名（系统应用） |
| `shared` | 共享进程签名 |
| `media` | 媒体/下载系统签名 |
| `networkstack` | 网络堆栈模块签名 |
| `verity` | 验证启动签名 |

每个密钥对由 `.pk8`（私钥）和 `.x509.pem`（证书）组成。

### 10.2 模块中的证书配置

**文件：** `build/soong/java/app.go`（第 257 行）

```
certificate         -- 证书名称，空白使用默认值，或 ":module" 引用
lineage             -- 签名证书传承文件（用于密钥轮换）
additional_certificates -- 多签名的额外证书
```

**验证：** `build/make/core/app_certificate_validate.mk`

### 10.3 APEX 签名

APEX 模块有自己的密钥和证书标签：
```go
keyTag         = dependencyTag{name: "key"}
certificateTag = dependencyTag{name: "certificate"}
```

---

## 11. 隐藏 API 管理

**文件：** `build/soong/java/hiddenapi.go`

隐藏 API 系统将框架 API 分类为限制类别：

1. **CSV 生成：** `Class2Greylist` 工具根据编译类与桩 API 标志生成标志 CSV、元数据 CSV 和索引 CSV
2. **Boot DEX Jars：** 通过 `bootDexJarPath` 跟踪以进行隐藏 API 强制执行
3. **单例：** `build/soong/java/hiddenapi_singleton.go` 管理全局隐藏 API 处理

---

## 12. APEX 模块

**文件：** `build/soong/apex/apex.go`

APEX（Android Pony EXpress）是模块化系统组件格式：

**载荷类型：** 共享库、可执行文件、Java 库、预构建文件、测试、Android 应用

**APEX 类型：**
- `image`（`.apex`）-- 带 dm-verity 的生产格式
- `zip`（`.zipapex`）-- 用于测试的 Zip 格式
- `flattened`（`.flattened`）-- 展平到系统分区

**关键依赖标签：**
```go
sharedLibTag   -- 原生共享库
executableTag  -- 原生可执行文件
javaLibTag     -- Java 库
androidAppTag  -- Android 应用
keyTag         -- APEX 签名密钥
certificateTag -- APEX 证书
```

---

## 13. 构建单个模块

### 13.1 使用 m/mm/mmm

```bash
# 构建所有内容
m

# 按名称构建特定模块
m <module-name>

# 构建当前目录中的模块
mm

# 构建特定目录中的模块
mmm frameworks/base/services/

# 从目录构建特定目标
mmm dir/:target1,target2
```

### 13.2 使用 tapas

用于在没有完整产品配置的情况下构建单个应用：
```bash
tapas <App1> <App2> [arm|arm64|x86|x86_64] [eng|userdebug|user]
```

### 13.3 Soong UI 直接命令

```bash
# 导出构建变量
build/soong/soong_ui.bash --dumpvar-mode TARGET_PRODUCT

# 导出多个变量
build/soong/soong_ui.bash --dumpvars-mode --vars="TARGET_PRODUCT TARGET_BUILD_VARIANT"
```

---

## 14. Android.bp 格式参考

### 14.1 基本结构

Android.bp 使用类 JSON 语法：
```
module_type {
    name: "module_name",
    property: "value",
    list_property: ["item1", "item2"],
    nested_property: {
        sub_property: true,
    },
    arch: {
        arm: { srcs: ["arm_specific.c"] },
        arm64: { srcs: ["arm64_specific.c"] },
    },
    target: {
        android: { cflags: ["-DANDROID"] },
        host: { cflags: ["-DHOST"] },
    },
}
```

### 14.2 常见模式

**Defaults 模式：**
```
java_defaults {
    name: "my_defaults",
    sdk_version: "current",
    min_sdk_version: "28",
}

java_library {
    name: "my_lib",
    defaults: ["my_defaults"],
    srcs: ["**/*.java"],
}
```

**Filegroup 引用：**
```
filegroup {
    name: "my-sources",
    srcs: ["java/**/*.java"],
    path: "java",
}

java_library {
    name: "my_lib",
    srcs: [":my-sources"],
}
```

**Genrule 模式：**
```
genrule {
    name: "my_generated",
    srcs: ["input.txt"],
    out: ["output.h"],
    tools: ["my_tool"],
    cmd: "$(location my_tool) $(in) > $(out)",
}
```

### 14.3 子目录包含

```
subdirs = ["subdir1", "subdir2"]
```

### 14.4 可见性控制

```
java_library {
    name: "internal_lib",
    visibility: ["//visibility:private"],
    // 或：visibility: ["//path/to/allowed:__subpackages__"],
}
```

---

## 15. 示例 Android.bp：实际模式

**来自 `frameworks/base/services/Android.bp`：**
```
java_defaults {
    name: "services_defaults",
    plugins: ["error_prone_android_framework"],
}

filegroup {
    name: "services-all-sources",
    srcs: [
        ":services.core-sources",
        ":services.accessibility-sources",
        // ... 更多服务源码组
    ],
    visibility: ["//visibility:private"],
}

java_library {
    name: "services",
    installable: true,
    dex_preopt: {
        app_image: true,
        profile: "art-profile",
    },
    srcs: [":services-main-sources"],
    static_libs: [/* 服务模块 */],
}
```

---

## 16. 开发者实用指南

### 16.1 添加新的 Java 库

```
// 在你的 Android.bp 中：
java_library {
    name: "my-library",
    srcs: ["src/**/*.java"],
    sdk_version: "current",          // 或使用 "system_current" 获取系统 API
    min_sdk_version: "28",
    libs: ["framework"],             // 仅编译时依赖
    static_libs: ["my-utils"],       // 打包依赖
}
```

### 16.2 添加新的系统应用

```
android_app {
    name: "MySystemApp",
    srcs: ["src/**/*.java"],
    platform_apis: true,             // 使用平台 API
    certificate: "platform",         // 使用平台密钥签名
    privileged: true,                // 安装到 priv-app
    resource_dirs: ["res"],
    manifest: "AndroidManifest.xml",
    static_libs: ["my-library"],
    jni_libs: ["libnative"],
}
```

### 16.3 添加原生库

```
cc_library_shared {
    name: "libnative",
    srcs: ["native/*.cpp"],
    shared_libs: ["liblog", "libutils"],
    cflags: ["-Wall", "-Werror"],
    export_include_dirs: ["include"],
    vendor_available: true,
}
```

### 16.4 添加 AIDL 接口源码

在 java_library 或 android_app 中：
```
java_library {
    name: "my-service-lib",
    srcs: [
        "src/**/*.java",
        "aidl/**/*.aidl",
    ],
    aidl: {
        local_include_dirs: ["aidl"],
        export_include_dirs: ["aidl"],
    },
    platform_apis: true,
}
```

### 16.5 将 Android.mk 转换为 Android.bp

使用 `androidmk` 工具：
```bash
androidmk Android.mk > Android.bp
```
然后手动验证并调整输出。并非所有 Make 模式都有直接的 Soong 等价物。

### 16.6 关键环境变量

| 变量 | 用途 |
|----------|---------|
| `TARGET_PRODUCT` | 正在构建的产品 |
| `TARGET_BUILD_VARIANT` | 构建变体（user/userdebug/eng） |
| `OUT_DIR` | 输出目录（默认：`out`） |
| `ANDROID_PRODUCT_OUT` | 产品特定输出目录 |
| `ANDROID_HOST_OUT` | 主机工具输出目录 |
| `SANITIZE_HOST` | 设置为 `address` 以在主机模块上启用 ASAN |

---

## 17. 架构观察与建议

### 17.1 优势

1. **Soong 的声明式模型**消除了许多类 Make 相关的构建错误，并实现了更好的并行化
2. **SDK 版本系统**提供了细粒度的 API 表面控制，具有明确的强制执行边界
3. **CC 模块中的变换器管道**（sanitizer、VNDK、coverage、LTO）支持可组合的构建转换
4. **隐藏 API 跟踪**与构建系统良好集成，确保 API 表面合规性
5. **APEX 支持**通过适当的签名和验证实现模块化更新

### 17.2 复杂性问题

1. **双构建系统** -- Soong 和 Make 的共存增加了认知负担。产品配置完全在 Make 中，而模块定义在 Soong 中
2. **SDK 版本解析** -- 具有 9 种不同 `sdkKind` 值和多种字符串格式（`"current"`、`"system_current"`、`"30"`、`""`）的 `sdkSpec` 系统为模块作者带来了复杂性
3. **变换器排序** -- CC 模块有 15 个以上的变换器，具有特定的排序要求；不正确的排序可能导致微妙的构建问题

### 17.3 开发者建议

1. **新模块始终优先使用 Android.bp** 而非 Android.mk -- Make 系统处于维护模式
2. **为所有应用和库模块显式设置 `sdk_version`**，以避免意外依赖私有 API
3. **使用 `java_defaults`/`cc_defaults`** 共享通用配置并减少重复
4. **使用 filegroup** 组织大型源码集，而不是复杂的 glob 模式
5. **使用 `m checkbuild` 测试**以在不运行完整构建的情况下验证构建规则
6. **使用 `m <module>-check-aidl-api`** 验证 AIDL 接口兼容性
