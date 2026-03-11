# OpenHarmony 4.1 Release - 构建系统审查

## 目录

1. [执行摘要](#执行摘要)
2. [构建系统架构](#构建系统架构)
3. [产品定义与目标配置](#产品定义与目标配置)
4. [工具链设置与交叉编译](#工具链设置与交叉编译)
5. [组件/子系统构建配置](#组件子系统构建配置)
6. [安全构建标志](#安全构建标志)
7. [可复现性与确定性构建](#可复现性与确定性构建)
8. [依赖管理与第三方集成](#依赖管理与第三方集成)
9. [构建脚本质量与可维护性](#构建脚本质量与可维护性)
10. [开发者体验](#开发者体验)
11. [发现汇总](#发现汇总)

---

## 执行摘要

OpenHarmony 4.1 构建系统是一个基于 GN/Ninja 的基础设施，源自 Chromium 的构建系统，经过大幅定制以支持多架构、多系统级别（轻量/小型/标准）的嵌入式操作系统开发。该系统使用 Clang/LLVM 工具链，支持 arm、arm64、x86_64、riscv64 和 mipsel 目标。虽然架构总体上是合理的，安全加固姿态在嵌入式系统中高于平均水平，但本次审查发现了几个与禁用安全功能、供应链中 SSL 验证绕过、硬编码凭据和构建脚本健壮性相关的关键和高严重性问题。

---

## 构建系统架构

### 概述

构建系统有三层：

1. **Shell/Python 入口点**：`build.sh` -> `build.py` -> `hb/main.py`（"hb"构建工具）
2. **GN 配置层**：`BUILDCONFIG.gn`、`ohos.gni`、`ohos_var.gni`、产品/设备配置
3. **Ninja 执行层**：由 GN 生成，驱动实际编译

**审查的关键文件：**
- `/home/dspfac/openharmony/build/build_scripts/build.sh`
- `/home/dspfac/openharmony/build/build_scripts/build.py`
- `/home/dspfac/openharmony/build/config/BUILDCONFIG.gn`
- `/home/dspfac/openharmony/build/ohos.gni`
- `/home/dspfac/openharmony/build/ohos_var.gni`
- `/home/dspfac/openharmony/build/hb/main.py`

### 发现

#### BS-01：build.sh 中 `set -e` 立即被 `set +e` 否定

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 15-16 行

```bash
set -e
set +e
```

`set -e`（出错即退出）标志被设置后立即在下一行被禁用。这意味着子命令的构建错误（除了最终的退出码检查外）将被静默吞没。构建脚本在某些地方确实有显式的 `$?` 检查，但并非所有命令调用都有保护。这可能导致静默的构建损坏。

#### BS-02：双构建入口点造成混淆

- **严重程度：低**
- **位置**：`build.sh` 第 198-212 行，`build.py`

构建系统支持两个代码路径：旧版 `entry.py` 路径（通过 `using_hb_new=false`）和新版 `hb/main.py` 路径（默认）。两者同时维护，增加了维护负担和行为分歧的可能性。

#### BS-03：BUILDCONFIG.gn 中过度使用 exec_script

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/BUILDCONFIG.gn`，第 52、56、128、677 行

多个 `exec_script` 调用在 GN 配置阶段运行，包括 `check_mac_system_and_cpu.py`、`check_file_exist.py` 和 `run_shell_cmd.py`。每次调用都会在 GN 评估期间生成一个 Python 进程。虽然文件自身的注释中已将其记录为已知反模式（第 282 行："Don't call exec_script inside declare_args"），但代码库在非 declare_args 上下文中大量使用它。这会减慢 GN 评估速度。

---

## 产品定义与目标配置

### 架构

产品定义遵循分层模型：
- **基础配置**：`/home/dspfac/openharmony/productdefine/common/base/`（轻量、小型、标准系统）
- **继承配置**：`/home/dspfac/openharmony/productdefine/common/inherit/`（rich、tablet、2in1、headless 等）
- **产品配置**：`/home/dspfac/openharmony/productdefine/common/products/`（system_arm64_default、ohos-sdk 等）

产品从基础系统定义继承，并在 JSON 中声明式地组合子系统/组件。

### 发现

#### PD-01：rich.json 中重复的组件条目

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/productdefine/common/inherit/rich.json`

`security_component_manager` 组件在安全子系统中出现了两次（第 163-166 行和第 173-176 行）。类似地，`drivers_interface_drm` 在 hdf 子系统中出现了两次（第 979-982 行和第 987-990 行）。`applications` 子系统也有两个独立的块（第 736-746 行和第 757-765 行），这对于大多数将重复键视为覆盖而非合并的 JSON 解析器来说在语义上是无效的。

#### PD-02：不一致的 JSON 字段命名

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/productdefine/common/inherit/rich.json`，第 1033 行

一些条目使用 `"feature":[]`（单数）而非标准的 `"features":[]`（复数），例如第 1033 行的 `drivers_interface_huks` 和第 1095 行的 `drivers_interface_intelligent_voice`。这很可能意味着这些条目被构建系统静默忽略。

#### PD-03：不一致的 JSON 格式

- **严重程度：信息性**
- **位置**：`/home/dspfac/openharmony/productdefine/common/inherit/rich.json`

文件中混合使用缩进（制表符与空格），例如第 489、620、887、967、979 行。虽然在功能上无害，但表明缺乏自动格式化强制执行。

---

## 工具链设置与交叉编译

### 架构

- 主编译器：Clang/LLVM（预构建在 `prebuilts/` 中）
- C 库：musl libc（自定义 sysroot）
- 链接器：默认使用 LLD（LLVM 链接器）
- 支持的目标：arm、arm64、x86_64、riscv64、mipsel
- 定义在 `/home/dspfac/openharmony/build/toolchain/ohos/BUILD.gn` 和 `ohos_toolchain.gni` 中

`ohos_clang_toolchain` 模板封装了 `gcc_toolchain` 并配置特定于目标的 sysroot、CRT 对象和 Rust ABI 目标。

### 发现

#### TC-01：硬编码的 Node.js 版本且无更新机制

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 95 行

```bash
EXPECTED_NODE_VERSION="14.21.1"
```

Node.js 14.x 已于 2023 年 4 月 30 日到达生命周期终点。该版本不再接收安全更新。构建系统通过严格的相等性检查（第 101-104 行）强制使用此精确版本，阻止开发者使用受支持的 Node.js 版本。

#### TC-02：重复的 Rust 工具链配置

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/toolchain/ohos/ohos_toolchain.gni`，第 94-104 行

Rust 编译的 `cc_command_args` 在 armv7、aarch64 和 x86_64 目标之间几乎相同。仅存在细微差异（目标三元组），但完整字符串被重复了 5 次。这容易出错且更难维护。

#### TC-03：LitOS M 工具链缺少安全配置

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/config/BUILDCONFIG.gn`，第 930-934 行

对于 `liteos_m` 目标，仅应用了 `stack_protector`。没有 PIE、没有 `-Wl,-z,relro`、没有 `-Wl,-z,now`，也没有 FORTIFY_SOURCE。虽然其中一些可能不适用于裸机目标，但链接器加固标志的完全缺失需要验证每个标志是否确实是有意为之。

---

## 组件/子系统构建配置

### 架构

组件通过 `bundle.json` 文件定义，使用 `/home/dspfac/openharmony/build/templates/cxx/cxx.gni` 中的模板：
- `ohos_executable` - 可执行文件
- `ohos_shared_library` - 共享库
- `ohos_static_library` - 静态库
- `ohos_source_set` - 源集

子系统注册表位于 `/home/dspfac/openharmony/build/subsystem_config.json`，将子系统名称映射到源码路径。

### 发现

#### CS-01：组件 sanitizer 强制执行可由厂商配置

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/config/sanitizers/sanitizers.gni`，第 351-405 行

`ohos_sanitizer_check` 模板为指定部分强制执行 CFI 和整数 sanitizer 的使用。然而，强制执行列表来自 `ext_sanitizer_check_list_path`，该路径可由厂商配置。厂商可以设置空列表，有效地禁用整个系统的所有 sanitizer 强制执行。没有机制来强制执行最小的 sanitizer 组件集。

#### CS-02：不受限制的按组件 auto-var-init 退出

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/security/security_config.gni`，第 52-78 行

任何组件都可以设置 `auto_var_init = "uninit"` 来禁用自动变量初始化，绕过安全默认值。没有白名单或退出审计机制。

---

## 安全构建标志

### 加固姿态摘要

| 功能 | 标准系统 | 轻量系统 | 状态 |
|---------|----------------|-------------|--------|
| 栈保护 | `-fstack-protector-strong` | `-fstack-protector-all` | 已启用 |
| 栈保护返回（arm64） | `-fstack-protector-ret-strong` | 不适用 | 已启用（如果编译器支持） |
| PAC/BTI（arm64） | PAC-RET | 不适用 | 已启用 |
| PIC | `-fPIC` | `-fPIC` | 已启用 |
| PIE | `-fPIE` / `-pie` | `-fPIE` / `-pie` | 已启用 |
| RELRO | `-Wl,-z,relro` | `-Wl,-z,relro` | 已启用 |
| 立即绑定 | `-Wl,-z,now` | `-Wl,-z,now` | 已启用 |
| NX 栈 | `-Wl,-z,noexecstack` | `-Wl,-z,noexecstack` | 已启用 |
| FORTIFY_SOURCE | 已注释掉 | 已注释掉 | **已禁用** |
| 自动变量初始化 | 零初始化（默认关闭） | 不适用 | 默认关闭 |
| CFI | 可用，按组件启用 | 不适用 | 选择性启用 |
| ThinLTO | 为 OHOS 启用 | 不适用 | 已启用 |
| ASLR | PIE 已启用（用户空间） | PIE 已启用 | 用户空间已启用 |

### 发现

#### SEC-01：FORTIFY_SOURCE 全局禁用

- **严重程度：高**
- **位置**：`/home/dspfac/openharmony/build/config/compiler/BUILD.gn`，第 1006-1016 行；`/home/dspfac/openharmony/build/lite/config/BUILD.gn`，第 82 行

```gn
# Need to support fortify ability first in musl libc, so disable the option temporarily
# defines += [ "_FORTIFY_SOURCE=2" ]
```

`_FORTIFY_SOURCE=2` 在标准系统和轻量系统构建配置中**均**被注释掉。注释说"暂时"禁用，但至少从 OpenHarmony 3.x 起就一直如此。FORTIFY_SOURCE 为常见 C 库函数（memcpy、sprintf 等）提供运行时缓冲区溢出检测，是 Android、Debian、Fedora 以及几乎所有现代 Linux 发行版默认启用的关键安全加固功能。

声明的原因是 musl libc 缺乏支持。然而，musl 具有部分 FORTIFY_SOURCE 支持，而且轻量系统配置中 `/home/dspfac/openharmony/build/config/compiler/lite/clang/BUILD.gn:49` 和 `/home/dspfac/openharmony/build/config/compiler/lite/gcc/BUILD.gn:49` **确实**定义了 `_FORTIFY_SOURCE=2`，造成了不一致。

#### SEC-02：x86 OHOS 的栈保护被禁用

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/config/security/BUILD.gn`，第 66-67 行

```gn
} else if (is_ohos && current_cpu == "x86") {
    cflags += [ "-fno-stack-protector" ]
```

x86 OHOS 构建的栈保护被明确禁用。模拟器平台（`x86_64`）是开发者的主要测试环境；在那里禁用栈保护意味着缓冲区溢出漏洞在开发测试期间可能不会被检测到。

#### SEC-03：自动变量初始化默认禁用

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/config/security/security_config.gni`，第 17 行

```gn
enable_auto_var_init = false
```

自动变量零初始化功能（`-ftrivial-auto-var-init=zero`）可用但默认禁用。启用后，它可以防止一类未初始化内存泄露漏洞。Android 从 Android 13 起已全局启用此功能。构建系统具有基础设施但未启用它。

#### SEC-04：构建配置中硬编码的签名凭据

- **严重程度：高**
- **位置**：`/home/dspfac/openharmony/build/ohos_var.gni`，第 303-308 行

```gn
default_key_alias = "OpenHarmony Application Release"
default_signature_algorithm = "SHA256withECDSA"
default_hap_private_key_path = "123456"
default_keystore_password = "123456"
default_keystore_path = "//developtools/hapsigner/dist/OpenHarmony.p12"
```

默认签名密码（"123456"）和密钥路径直接硬编码在 GN 构建配置中。虽然这些显然是开发/调试默认值，但它们嵌入在生产构建基础设施中，没有任何警告或编译时检查来确保发布构建中被覆盖。使用这些默认值的生产构建将导致应用签名被轻易破解。

#### SEC-05：检查列表允许禁用 PIC/PIE

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/hb/util/post_gn/check_list.json`，第 36-47 行

GN 后置检查器将 `-fno-PIC`、`-fno-pic`、`-fno-PIE`、`-fno-pie` 标记为 ldflags 和 cflags 检查列表中的禁止项。这是好的做法。然而，该机制是建议性检查而非硬性强制，且文件本身可以被厂商修改。

---

## 可复现性与确定性构建

### 发现

#### RP-01：时间戳宏仅对非正式构建移除

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/compiler/BUILD.gn`，第 257-275 行

```gn
if (!is_official_build) {
    cflags += [
        "-D__DATE__=",
        "-D__TIME__=",
        "-D__TIMESTAMP__=",
    ]
}
```

`__DATE__`、`__TIME__` 和 `__TIMESTAMP__` 宏在非正式构建中被清空以提高可复现性。矛盾的是，正式构建（可复现性最重要的场景，用于验证）**不会**移除这些宏。这继承自 Chromium 的逻辑，其中正式构建需要这些用于版本标识，但对于 OpenHarmony，正式发布构建也应该是可复现的。

#### RP-02：构建 ID 使用 MD5

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/compiler/BUILD.gn`，第 298 行

```gn
ldflags += [ "-Wl,--build-id=md5" ]
```

构建 ID 使用 MD5 生成，该算法在加密上已被破解。虽然构建 ID 不用于安全目的，但使用 SHA-256（至少 `--build-id=sha1`）会更符合安全最佳实践，且在构建性能方面没有额外开销。

#### RP-03：build.sh 在构建输出中打印时间戳

- **严重程度：信息性**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 42、220 行

构建脚本在开始和结束时打印当前日期/时间。这对日志记录来说没问题，但如果构建日志用于验证，可能会影响其可复现性。

---

## 依赖管理与第三方集成

### 发现

#### DM-01：npm 和 ohpm 全局禁用 SSL 验证

- **严重程度：关键**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 108、125 行

```bash
npm config set strict-ssl false
ohpm config set strict_ssl false
```

npm 和 ohpm 包管理器的 SSL 证书验证被无条件禁用。这使构建暴露于中间人攻击之下，网络攻击者可以在构建过程中注入恶意包。这是一个供应链安全漏洞，可能危及任何在不受信任网络上执行的 OpenHarmony 构建。

npm 锁文件也被明确禁用（第 109 行：`npm config set lockfile false`），进一步削弱了供应链完整性，因为依赖版本在不同构建之间可以浮动。

#### DM-02：npm 锁文件被明确禁用

- **严重程度：高**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 109-110 行

```bash
npm config set lockfile false
cat $HOME/.npmrc | grep 'lockfile=false' > /dev/null || echo 'lockfile=false' >> $HOME/.npmrc > /dev/null
```

npm 锁文件（`package-lock.json`）被全局明确禁用，甚至被持久化到用户的 `.npmrc` 中。锁文件是基本的供应链安全机制，确保可复现的、经过验证的依赖解析。没有锁文件，每次构建都可能拉取不同的（且可能被篡改的）包版本。

#### DM-03：oh-command-line-tools 的硬编码下载 URL

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 117 行

```bash
wget https://contentcenter-vali-drcn.dbankcdn.cn/pvt_2/DeveloperAlliance_package_901_9/...
```

oh-command-line-tools 通过未版本化的硬编码 URL 使用 `wget` 下载，且未进行校验和验证。如果此 URL 被篡改或返回不同内容，构建工具可能会被静默替换。下载应验证所下载归档文件的 SHA-256 校验和。

#### DM-04：全局 npm/ohpm 注册表配置修改

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 106-107 行

```bash
npm config set registry https://repo.huaweicloud.com/repository/npm/
npm config set @ohos:registry https://repo.harmonyos.com/npm/
```

构建脚本修改了**全局** npm 配置，将所有 npm 操作（不仅是 OpenHarmony 构建）重定向到华为的镜像。这是一个在构建完成后持续存在的副作用，会影响同一机器上的其他项目。配置应限定在构建范围内，而不是全局修改。

---

## 构建脚本质量与可维护性

### 发现

#### BQ-01：build.sh 中不一致的错误处理

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`

整个脚本中的错误处理不一致：
- `set -e` 立即被 `set +e` 否定（第 15-16 行）
- 某些命令显式检查 `$?`（第 148、164、214 行）
- 其他命令完全不检查返回码（例如 `npm config set` 命令，第 106-110 行）
- 第 117 行的 `wget` 命令没有错误检查
- `init_ohpm` 函数使用 `pushd`/`popd`，通过 `> /dev/null` 可能静默失败

#### BQ-02：build.py 错误消息中的拼写错误

- **严重程度：信息性**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.py`，第 82 行

```python
print("-python-dir parmeter missing value.")
```

"parmeter" 应为 "parameter"。

#### BQ-03：BUILDCONFIG.gn 中冗余的操作系统布尔赋值

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/BUILDCONFIG.gn`，第 531-615 行

操作系统标志赋值（`is_ohos`、`is_linux`、`is_mac` 等）在 7 个 OS 块中完全重复，每个块仅有一个变量不同。这可以使用默认为 false 然后设为 true 的模式进行整合，将 80 多行减少到约 20 行。

#### BQ-04：冗余的条件检查

- **严重程度：信息性**
- **位置**：`/home/dspfac/openharmony/build/config/compiler/BUILD.gn`，第 301 行

```gn
if (!is_ohos && !is_ohos) {
```

条件 `!is_ohos` 被检查了两次（很可能是复制粘贴错误；其中一个应该是 `!is_android`）。

#### BQ-05：Mac/iOS 的源文件过滤逻辑错误

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/config/BUILDCONFIG.gn`，第 644 行

```gn
if (!is_mac || !is_ios) {
```

此条件使用 `||`（或），意味着只要 `is_mac` 为 false **或** `is_ios` 为 false，Mac 源文件过滤器就会被应用。由于一次构建只能是一个操作系统，此过滤器总是被应用 -- 即使在 Mac 构建中也是如此（因为 `is_ios` 会为 false）。条件应该使用 `&&`（与）：`if (!is_mac && !is_ios)`。

---

## 开发者体验

### 发现

#### DX-01：首次编译时自动构建 SDK

- **严重程度：低**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 187-194 行

如果找不到预构建的 SDK，构建脚本会在主构建之前自动触发完整的 SDK 构建。这是一个巨大的时间成本（可能需要数小时），在首次构建时静默发生。虽然有 `--no-prebuilt-sdk` 标志，但默认行为令人意外且沟通不足。

#### DX-02：构建期间全局系统修改

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`

构建脚本修改了：
- 全局 npm 配置（注册表、SSL、锁文件设置）
- 全局 ohpm 配置
- `$HOME/.npmrc`（持久化修改）
- `$HOME/.hvigor/`（删除守护进程和包装器，然后重新创建）
- `$HOME/.ohpm/`（创建并安装包）

这些修改在构建后持续存在并影响其他项目。构建系统应该是自包含的，不应修改全局用户配置。

#### DX-03：过时的 Node.js 版本强制执行阻碍开发者

- **严重程度：中**
- **位置**：`/home/dspfac/openharmony/build/build_scripts/build.sh`，第 95-104 行

严格的 Node.js 14.21.1 版本检查阻止拥有更新（且更安全）Node.js 版本的开发者进行构建。开发者必须使用 prebuilts 目录中的精确预构建 Node.js，而该软件已到达生命周期终点。

#### DX-04：组件构建模板复杂性

- **严重程度：低**

创建新组件需要理解 `bundle.json`、GN 模板（`ohos_executable`、`ohos_shared_library`）、子系统配置、产品配置以及预加载器/加载器管道之间的交互。`cxx.gni` 模板文件近 60KB，模板逻辑深度嵌套。虽然功能强大，但学习曲线陡峭。

---

## 发现汇总

| 编号 | 发现 | 严重程度 |
|----|---------|----------|
| DM-01 | npm/ohpm 包管理器的 SSL 验证被禁用 | **关键** |
| SEC-01 | FORTIFY_SOURCE 在构建系统中全局禁用 | **高** |
| SEC-04 | 构建配置中硬编码签名凭据（密码"123456"） | **高** |
| DM-02 | npm 锁文件被明确禁用，削弱供应链完整性 | **高** |
| SEC-02 | x86 OHOS 构建的栈保护被禁用 | 中 |
| SEC-03 | 自动变量初始化默认禁用 | 中 |
| SEC-05 | PIC/PIE 禁用强制执行仅为建议性 | 中 |
| TC-01 | 已到期的 Node.js 14.x 被硬编码且无更新路径 | 中 |
| TC-03 | LitOS M 工具链缺乏大部分安全加固 | 中 |
| CS-01 | Sanitizer 强制执行可被厂商绕过 | 中 |
| DM-03 | 工具下载未进行校验和验证 | 中 |
| DM-04 | 构建修改全局 npm 注册表配置 | 中 |
| BS-01 | `set -e` 立即被否定，静默吞没错误 | 中 |
| BQ-01 | build.sh 中不一致的错误处理 | 中 |
| DX-02 | 构建修改全局用户配置（$HOME/.npmrc 等） | 中 |
| DX-03 | 已到期的 Node.js 版本被严格强制执行 | 中 |
| RP-01 | 正式构建中时间戳宏未被移除 | 低 |
| RP-02 | 构建 ID 使用 MD5 哈希 | 低 |
| BS-02 | 双构建入口点造成混淆（旧版 vs. hb） | 低 |
| BS-03 | 过度使用 exec_script 减慢 GN 评估 | 低 |
| PD-01 | 产品定义中重复的组件条目 | 低 |
| PD-02 | 不一致的 JSON 字段命名（feature vs features） | 低 |
| TC-02 | 重复的 Rust 工具链配置 | 低 |
| CS-02 | 不受限制的 auto-var-init 退出 | 低 |
| BQ-03 | 冗余的操作系统布尔赋值（约 80 行重复） | 低 |
| BQ-05 | 源文件过滤逻辑错误（Mac/iOS 的 OR vs AND） | 低 |
| DX-01 | SDK 自动构建在首次构建时令开发者意外 | 低 |
| DX-04 | 组件模板复杂度高 | 低 |
| PD-03 | 不一致的 JSON 格式（制表符与空格） | 信息性 |
| BQ-02 | 拼写错误：build.py 中的 "parmeter" | 信息性 |
| BQ-04 | 冗余的 `!is_ohos && !is_ohos` 条件 | 信息性 |
| RP-03 | 构建日志输出中的时间戳 | 信息性 |

### 优先建议

1. **立即**：为 npm 和 ohpm 启用 SSL 验证。如果企业代理需要自定义 CA，应配置 CA 证书包而非完全禁用验证。

2. **立即**：启用 npm 锁文件并提交。这是基本的供应链安全要求。

3. **短期**：在 musl libc 移植中实现 FORTIFY_SOURCE 支持。这已经"暂时"存在了多个版本，代表了与 Android/Linux 加固基线相比的重大差距。

4. **短期**：在非调试构建中使用默认签名凭据时添加构建时警告或断言。

5. **短期**：将 Node.js 更新到受支持的 LTS 版本（20.x 或 22.x）。

6. **中期**：默认启用自动变量初始化（`-ftrivial-auto-var-init=zero`），与 Android 13+ 基线保持一致。

7. **中期**：将构建时配置更改（npm 注册表、ohpm 配置）限定在构建目录范围内，而非修改全局用户设置。
