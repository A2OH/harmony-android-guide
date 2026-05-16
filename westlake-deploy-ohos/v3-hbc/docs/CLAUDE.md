# Android-OpenHarmony Adapter Project

## Project Overview

This project bridges Android Framework system service IPC calls to OpenHarmony (OH) system services, enabling Android Apps to run on an OH-based system. The adaptation layer intercepts Android Binder IPC calls via class inheritance (extends IXxx.Stub) and routes them to equivalent OH IPC interfaces through JNI.

## Target Hardware

- **Current dev board**: HH-SCDAYU200 (DAYU200), RK3568, 32-bit userspace
- **Build product**: `--product-name rk3568` (libs in `/system/lib/`, not `lib64/`)
- **Device OS version**: matches ECS OH source (weekly_20260302, OpenHarmony 7.0.0.18)

## Source Code Locations

- **Adapter project**: `D:\code\adapter\`
- **Android source (AOSP 14)**: `D:\code\android\frameworks\`
- **OpenHarmony source**: `D:\code\ohos\`
- **Design documents**: `D:\code\adapter\doc\`

## Project Structure

Top-level layout of `D:\code\adapter\`（每个特性目录内部都是 `java/` + `jni/` 子目录，文件级清单见各子目录代码）：

| Directory | Purpose |
|---|---|
| `framework/core/` | 共享基础设施：`OHEnvironment`、`ServiceInterceptor`、`adapter_bridge` JNI |
| `framework/activity/` | IActivityManager + IActivityTaskManager 适配（AMS/ATMS、AppScheduler、AbilityScheduler、Intent↔Want、Lifecycle 映射） |
| `framework/window/` | IWindowManager + IWindowSession 适配（WMS、SceneSession、MMI→InputEvent 桥） |
| `framework/surface/` | Graphics/Surface 桥接（JNI only，RSSurfaceNode + RSUIDirector，含像素格式映射） |
| `framework/broadcast/` | Broadcast ↔ OH CommonEvent 适配 |
| `framework/contentprovider/` | IContentProvider ↔ OH DataShareHelper (Stage) 适配 |
| `framework/package-manager/` | PackageManager/BMS 适配 + APK 安装（AXML/签名/dex2oat） |
| `framework/appspawn-x/` | Hybrid spawner（OH appspawn + Android Zygote，含 ART VM 启动） |
| `framework/jni/` | `liboh_adapter_bridge.so` 聚合 BUILD.gn |
| `aosp_patches/` | AOSP 源码补丁（镜像 AOSP 源码树结构） |
| `ohos_patches/` | OH 源码补丁（镜像 OH 源码树结构） |
| `app/` | Hello World 测试 APK（纯 Android 代码，本机 Android Studio 构建） |
| `build/` | **编译脚本**（cross_compile_arm32.sh / compile_*.sh / gen_boot_image.sh / restore_after_sync.sh / GN args / stub 模块）— **不含部署脚本** |
| `deploy/` | **部署脚本**（`deploy_to_dayu200.sh` / `deploy_stage.sh` / `DEPLOY_SOP.md`）— device 部署的唯一权威路径，**任何 hdc 部署操作必须从这里调，不要去 build/ 找** |
| `doc/` | 设计文档（HTML，中文） |
| `out/` | 构建产物（子目录：`oh-service/`、`aosp_fwk/`、`aosp_lib/`、`adapter/`、`boot-image/`、`host-tools/`、`app/`） |

## Design Documents

设计文档位于 `doc/`（HTML，中文）。入口：`doc/overall_design.html`（总体设计方案，汇总各子系统设计）；构建/部署相关：`doc/build and deployment design.html`；每次任务完成需同步更新 `doc/compile_report.html` 与 `doc/build_patch_log.html`（详见下方 Post-Completion Checklist）。各子系统设计（surface / mission_group / broadcast / content_provider / service_extension / appspawn_x / apk_installation / interface_bridge / hello_world 等）按功能命名，`ls doc/*.html` 可列出。

## Architecture

### Interface Mapping (Many-to-Many)

| Android Interface | OH Target Interfaces |
|---|---|
| IActivityManager | IAbilityManager, IAppMgr, IMissionManager |
| IActivityTaskManager | IAbilityManager, IMissionManager, ISceneSessionManager |
| IWindowManager | IWindowManager(OH), ISceneSessionManager, DisplayManager |
| IWindowSession | ISession, ISessionStage, ISceneSessionManager |

具体方法数见 AIDL/接口头文件；整体规模与方法级对照见 `doc/android_oh_adaptation_feasibility_report.html`。

### Reverse Callback Mapping (System → App)

| OH Callback Interface | Android Target |
|---|---|
| IAppScheduler | IApplicationThread (process lifecycle, memory) |
| IAbilityScheduler | IApplicationThread (activity/service) + ContentProvider |
| IAbilityConnection | IServiceConnection (1:1 mapping) |
| IWindow-OH | IWindow-Android (window state) |
| ISessionStage | IWindow-Android + IApplicationThread |
| IWindowManagerAgent | Internal handling (no Android equivalent) |

### Forward Bridge Pattern (Class Inheritance)

All 4 forward bridges use class inheritance (`extends IXxx.Stub`) instead of dynamic proxy. Each adapter:
- Extends the AIDL-generated `Stub` class (e.g., `IActivityManager.Stub`)
- Declares its own `native` methods for OH service access (no shared AdaptationLayer)
- Holds a native OH service handle (`long mOhAbilityManager`) obtained via its own JNI call
- Overrides every AIDL method with proper typed signatures (compile-time checked)
- Methods are tagged: **[BRIDGED]**, **[PARTIAL]**, **[STUB]**

### Installation (AOSP Source Modification)

AOSP source modified to check `OHEnvironment.isOHEnvironment()` and return adapters directly:
- `ActivityManager.java`: `IActivityManagerSingleton.create()` → `new ActivityManagerAdapter()`
- `ActivityTaskManager.java`: `IActivityTaskManagerSingleton.create()` → `new ActivityTaskManagerAdapter()`
- `ActivityThread.java`: `getPackageManager()` → `new PackageManagerAdapter()`
- `WindowManagerGlobal.java`: `getWindowManagerService()` → `new WindowManagerAdapter()`
- `WindowManagerGlobal.java`: `getWindowSession()` → `new WindowSessionAdapter()`

All modifications include fallback to original implementation if adapter initialization fails.

`ServiceInterceptor.java` exists as a runtime reflection fallback for unmodified AOSP builds.

### Key Classes

- **`OHEnvironment`** — Static utility: `isOHEnvironment()`, `initialize()`, `shutdown()`, `loadLibrary`. No singleton needed.
- **`LifecycleAdapter`** — Singleton. Maps Android 6 lifecycle states <-> OH 4 lifecycle states. Calls `OHEnvironment.nativeNotifyAppState()`.
- **`IntentWantConverter`** — Stateless converter. Android Intent <-> OH Want field mapping.

## Key Technical Details

- **Intent -> Want conversion**: Action mapping table in `IntentWantConverter.java`, component name splits to bundleName + abilityName, extras serialized to JSON
- **Lifecycle mapping**: Android 6 states (Created/Started/Resumed/Paused/Stopped/Destroyed) <-> OH 4 states (Initial/Inactive/Foreground/Background)
- **JNI bridge**: `liboh_adapter_bridge.so`, loaded via `OHEnvironment` static initializer
- **Native methods**: Each Adapter declares its own `native` methods (e.g., `ActivityManagerAdapter.nativeStartAbility`). C++ side uses helper functions to avoid duplication.
- **OH environment detection**: System property `persist.oh.adapter.enabled` + native fallback `nativeIsOHEnvironment()`
- **Phase 1 mode**: Most BRIDGED methods currently passthrough to original; OH IPC calls are logged but not connected to real OH services
- **ART VM 必须走 boot image (不变量)**: appspawn-x 启动 ART VM 必须 `-Ximage:/system/android/framework/arm/boot.art`，不得只靠 `-Xbootclasspath`（会触发 ClassLinker 深递归爆栈）。详见 memory `feedback_always_use_boot_image.md`。

## Cloud Build Server

- **Server**: Huawei Cloud ECS, `ssh oh-build`（Ubuntu 22.04, 16 vCPU / 60 GB RAM / 493 GB SSD），SSH key 见 `~/.ssh/config`
- **OH source**: `~/oh/`，weekly_20260302（对应设备镜像 OpenHarmony 7.0.0.18），product `rk3568`（32-bit ARM）
- **AOSP source**: `~/aosp/`，AOSP 14 (android-14.0.0_r1)，partial sync via Tsinghua mirror，custom product `oh_adapter`
- **构建命令/GN args/ninja patch/device .so fallback 等均由 `build/` 下脚本封装**；当前状态、里程碑、patch 列表见 `doc/compile_report.html` 与 `doc/build_patch_log.html`。动手改构建前先读 `build/auto_build.sh` 及相关 `compile_*.sh`。


## Build

### Build Isolation Rule

**CRITICAL: All modifications to AOSP / OH source must be authored as patch files under `adapter/aosp_patches/` or `adapter/ohos_patches/`. Never edit AOSP / OH source files directly with an editor — always go through the patch workflow described below.**

#### Patch workflow (the only allowed way to modify AOSP/OH source)

1. **Author the patch locally** in `D:\code\adapter\aosp_patches/` or `D:\code\adapter\ohos_patches/`. The patches mirror the target source tree structure (e.g. `aosp_patches/frameworks/base/core/java/android/app/ActivityManager.java.patch`).
2. **Sync the patch to ECS** along with the rest of the project (`scp -r aosp_patches/ ohos_patches/ oh-build:~/adapter/`).
3. **Apply the patch to the AOSP / OH source tree on ECS** before building:
   - Before applying, **back up the original file** (e.g. `cp file file.bak` if `.bak` does not already exist).
   - Apply the patch to the original file in-place.
   - **Do NOT revert the patch after the build.** The modified file stays in place so subsequent incremental builds can reuse it. If the patch needs to be re-derived, diff against the `.bak` backup.
4. **Record every modified AOSP / OH file** in `doc/build_patch_log.html` (append-only) so all source modifications stay traceable.

#### What lives where

- All build configuration files (BUILD.gn, .gni, config.json, Makefile, scripts) live under `adapter/build/` or appropriate adapter subdirectories — never under AOSP/OH source.
- All source code modifications to AOSP → `adapter/aosp_patches/` (as .patch files or full replacement files).
- All source code modifications to OH → `adapter/ohos_patches/` (as .patch files or full replacement files).
- Build outputs → `adapter/out/`, 按类别子目录组织（本机与 ECS 保持相同子目录结构）：
  - `adapter/out/oh-service/` — OH system service .so (libabilityms, libappms, libbms, libscene_session_manager, libscene_session, librender_service_base, libwms)
  - `adapter/out/aosp_fwk/` — Android Java artifacts (framework.jar, core-oj.jar, oh-adapter-framework.jar, etc.)
  - `adapter/out/aosp_lib/` — Android native cross-compiled .so (libart, libandroid_runtime, libhwui, libbase, libcutils, libutils, etc.)
  - `adapter/out/adapter/` — Adapter's own artifacts (appspawn-x, oh-adapter-framework.jar, liboh_adapter_bridge.so, libapk_installer.so, liboh_hwui_shim.so, liboh_stubs.so, libbionic_compat.so)
  - `adapter/out/boot-image/` — ARM32 boot image (boot.art, boot.oat, boot.vdex)
  - `adapter/out/host-tools/` — Host-only tools (dex2oat64, not deployed to device)
  - `adapter/out/app/` — APK (HelloWorld.apk)
  - `adapter/out/deploy_package/` — 部署打包目录（镜像设备目录树结构）
- **编译产物保全 + 一键恢复**：编译后产物不丢、repo sync 后可一键恢复——具体要求见下方"一键恢复规则"节。
- **Build scripts / 产物来源 / 同步方向**：见下方 Sync Map 一节，各目录的 authoritative side 在那里统一定义，不在此处重复。

### 一键恢复规则 (Single-Command Recovery Rule, 最高优先级)

**任何工作成果——包括但不限于源代码、`aosp_patches/`、`ohos_patches/`、build 脚本、GN args、ninja patches、配置文件、syscall.h.in 修补、stub 模块、被替换的 device .so prebuilt、Python 增量补丁脚本等等——都必须保证：在 AOSP / OH 源码经历一次 `repo sync` 把改动全部覆盖之后，能够通过执行一个 sh 命令，迅速恢复到上一次成功编译时的可用状态。**

具体要求：

1. **必须存在一个根入口脚本**（建议路径：`build/restore_after_sync.sh`），它从一个干净的 `repo sync` 后的 AOSP / OH 源码树出发，依次完成以下动作，最终能跑通既定的 build 命令：
   - 应用 `aosp_patches/` 下所有补丁到 `~/aosp/`
   - 应用 `ohos_patches/` 下所有补丁到 `~/oh/`
   - 把 `build/dex2oat_stubs/` 等 stub 模块软链接 / 拷贝到目标位置
   - 复写或追加 GN args / ninja patches / config.json
   - 部署 fallback 用的 device .so prebuilt 到正确路径
   - 任何其它"让上次能编过"的修改
2. **脚本必须满足三性**：
   - **自包含 (self-contained)**：从干净源码树出发即可，不依赖任何"我手上还残留的本地修改"。
   - **幂等 (idempotent)**：重复执行不破坏状态，可在已恢复的环境上再跑一次仍然 OK。
   - **可追溯 (traceable)**：每一步动作必须能在 `doc/build_patch_log.html` 找到对应记录；脚本本身的每个动作必须打 log，失败立即退出（`set -e`），不允许静默吞错。
3. **零孤儿改动**：任何对 AOSP / OH 源码 / build 脚本 / 配置的新修改，都必须**同步**反映到 `aosp_patches/` / `ohos_patches/` / `build/` 与 `restore_after_sync.sh`。如果一个改动只存在于 ECS 上的源码树里，没有沉淀到 patch / 脚本里，它就**不存在** —— 一次 `repo sync` 之后就归零。这条规则是"编译产物保全"的另一面：保全负责"不丢"，本规则负责"能恢复",两者缺一不可。
4. **典型反例（必须避免）**：
   - 用 Python 脚本 sed 式 in-place 改 hwui 源码，但脚本只在本次会话里跑过，没沉淀为 .patch 也没登记到 `restore_after_sync.sh`（当前 `build/hwui_phase2_round*.py` 等 40+ 脚本就处在这个状态，是高优先级补救项）
   - 在 ECS 上手工 `vim` 改 GN args 或 BUILD.gn，编译过了直接汇报"完成"，没回写到 `build/` 与恢复脚本
   - stub 模块只放在 ECS 的 `~/oh/external/.../` 下，没在 `adapter/build/dex2oat_stubs/` 留底
5. **验收标准**：在一台干净 ECS（或一台新拉的 AOSP / OH 源码树）上，执行 `bash restore_after_sync.sh && <既定 build 命令>`，必须能跑出与上次完全一致的产物。这条验收应当在每次重大编译里程碑后实跑一次，结果记录到 `doc/build_patch_log.html`。

参考样本与现状审计：`doc/technical_decision_overview.html` 第 3 节列出了当前所有"未沉淀手段"。本规则生效后，这一节里的每一行都必须在 `restore_after_sync.sh` 里有对应步骤，否则该行就属于违规。

### Local → ECS / ECS → Local Sync Map

Every top-level item has a single, fixed sync direction — there is **no bidirectional sync**. The authoritative source side owns the file; the other side is purely a backup. See `readme.txt` at the project root for the canonical table; the rules below are the ones the build workflow depends on:

| Direction | Items | Authoritative Source | Notes |
|---|---|---|---|
| **Local → ECS** (push only) | `framework/`, `aosp_patches/`, `ohos_patches/`, `app/`, `doc/`, `CLAUDE.md`, `readme.txt` | Local | Authored locally with editor; push to ECS before building. ECS copy is read-only mirror — never edit on ECS |
| **Local → ECS** (push only, temp drafts) | `cmd.txt`, `push_cmd.txt` | Local | Local working notes that may need to be visible on ECS for ad-hoc command runs |
| **ECS → Local** (pull only) | `build/`, `build.sh`, `bundle.json`, `out/` (selective) | ECS | Modified during compile iterations on ECS; pulled back to local for backup / GitHub. Local copy is read-only — never edit and push back |
| **Local only** (never to ECS) | `bkup/`, `OH_Git.txt`, `fix_and_build.py` (legacy drafts) | Local | Local working scratch, never synced |
| **ECS only** (selectively pulled) | `prebuilts/`, large `out/` artifacts | ECS | Cross-compile toolchain shortcuts; full `out/` stays on ECS, only individual artifacts pulled when needed |

**Critical rule**: Each item has a **single owner**. If you find yourself wanting to edit a file on the non-owner side, stop and either (a) push from the owner side instead, or (b) move the file to the other category in `readme.txt` first.

**Exception — `build/restore_after_sync.sh` & `build/apply_*_patches.py`**: 这些脚本属于"一键恢复"入口，必须在本机有最新可用副本，否则 `repo sync` 清掉 ECS 源码树后无从恢复。规则：日常迭代仍以 ECS 为 authoritative（ECS→Local 方向），但**每次 ECS 上改动这两类脚本后，必须立即 `scp` 到本机**。若本机比 ECS 新（如架构师在本机新增/重写恢复脚本），允许一次性 Local→ECS 推送，但必须在 commit message / `doc/build_patch_log.html` 记录原因。

**2026-04-11 one-off exception (closed)**: A Local→ECS push of the entire `build/` tree was authorized for the dayu210→rk3568 cleanup + `restore_after_sync.sh` creation + `apply_aosp_java_patches.py` portability fix. **This was a one-off** — all future `build/` edits happen on ECS first, then pulled back to local via `scp -r oh-build:~/adapter/build/ D:/code/adapter/`. If a future Local-authoring exception is needed (e.g., architect-authored script that doesn't exist on ECS yet), it must be explicitly requested, not silently assumed.

Sync commands:
```bash
# Local → ECS (push source code & docs & patches)
scp -r framework/ aosp_patches/ ohos_patches/ doc/ oh-build:~/adapter/
scp CLAUDE.md readme.txt oh-build:~/adapter/

# ECS → Local (pull build scripts & key artifacts)
scp -r oh-build:~/adapter/build/ D:/code/adapter/
scp oh-build:~/adapter/build.sh oh-build:~/adapter/bundle.json D:/code/adapter/

# ECS → Local (pull final build artifacts only)
bash build/pull_ecs_artifacts.sh
```

### 产物流向 & 同步规则 (最高优先级)

本节统一描述编译产物的三方流向（ECS → 本机 → 设备）、同步筛选规则、违规自查与恢复流程，以及已废弃的 `out/` 子目录。

#### 三条强制链路（ECS = 唯一编译源，本机 = 镜像，设备 = 运行时）

1. **编译方向**：编译全部发生在 ECS（`~/adapter/out/`）。本机 `out/` 和设备 `/system/...` 都不是编译来源。不得存在"本机编出来又 scp 回 ECS"或"设备上修改后拷回 ECS/本机"的逆向流。ECS 与本机/设备不一致时，**唯一正确的恢复路径**是在 ECS 上重编，而不是反向覆盖。
2. **同步方向**：ECS 编译完成 → scp 到本机 `D:\code\adapter\out\` 对应子目录 → 本机是"最新 ECS 产物的镜像"。本机 `out/` 不能独立于 ECS 产生内容。
3. **部署方向**：设备部署只能从本机 `hdc file send`。禁止用 ECS 直推设备的脚本，也禁止手工在设备上 `dd`/`cp` 引入来历不明的二进制。

**为何这样定**：避免三方出现"设备比 ECS 新"这种拓扑颠倒——这种颠倒会让 `repo sync` 清 ECS 后丢失事实上的最新版本，违反一键恢复规则；本机作为中转承担"最后一次已验证的 ECS 产物快照"角色；设备上的 .so/.jar 是运行时镜像，不是编译源头，绝对不能反向污染上游。

#### ECS → Local 同步筛选规则

**只同步最终产物，中间文件不同步。**

- **最终产物**（同步）：`.so`、`.jar`、可执行文件（appspawn-x）、`.apk`、boot image（`.art` / `.oat` / `.vdex`）、host 工具（dex2oat64）
- **中间文件**（不同步）：`.o` 目标文件、`.err` 日志、`obj/` 目录、历史版本 `.so`（如 `libhwui_v2.so`）、编译报告 HTML
- **本机 `out/` 子目录内部扁平化**：每个子目录只存放最终产物文件，不包含嵌套子目录或中间文件
- **同步脚本**：`build/pull_ecs_artifacts.sh`——从 ECS 各子目录拉取最终产物到本机对应子目录

#### 违规自查

若出现以下任一情况，立即停机诊断，不得继续部署：
- 设备上某个文件的 MD5 与 ECS `out/` 对应文件不同，而本机 `out/` 里是设备那版——本机或设备被外部写入过，来源不明。
- `ls -lt` 显示某本机产物的 mtime 晚于 ECS 同名产物——本机不可能比 ECS 新。
- 某个 .so 在 ECS 不存在但设备上存在——来源彻底不明，必须追查并在 ECS 重建。

#### 三方不一致时的恢复流程

1. 在 ECS 上找到或重建该文件的编译脚本。若脚本缺失（例如二进制 patch 操作只手工做过一次），先把它沉淀为脚本并登记到 `doc/build_patch_log.html` 与 `restore_after_sync.sh`。
2. 在 ECS 上跑脚本，产出落到 `~/adapter/out/` 正确子目录。
3. `scp oh-build:~/adapter/out/<sub>/<file>` → 本机 `D:\code\adapter\out\<sub>\<file>`。
4. `hdc file send <本机路径> /system/<device路径>`（Git Bash 下加 `MSYS_NO_PATHCONV=1`）。
5. 在设备上 `md5sum` 验证三边一致。

#### 废弃 `out/` 子目录清单 (不得再使用)

以下子目录在 ECS 和本机双边均**已废弃**，禁止部署、禁止同步、应从项目结构中彻底清除：
- `out/deploy_package/` — 已废弃
- `out/libart-full/` — 已废弃（libart.so 的唯一权威来源是 `out/aosp_lib/libart.so`）
- `out/native-arm32/` — 已废弃（2026-04-14，AOSP native .so 只放 `out/aosp_lib/`）


### Build Division Principle

**AOSP Soong builds: (a) Java/APK artifacts (framework.jar, core-oj.jar, oh-adapter-framework.jar 等), (b) host-side tools (dex2oat, aapt2 等), (c) 供 OH 部署的 AOSP native .so（通过交叉编译，落到 `out/aosp_lib/`：libart, libandroid_runtime, libhwui, libbase, libcutils, libutils 等）。OH BUILD.gn 构建: 适配层自身的 native 产物（liboh_adapter_bridge.so、libapk_installer.so、appspawn-x）和需要 patch 的 OH 系统服务（abilityms、libappms、scene_session_manager 等）。**

Rationale:
- Java `.jar` / `.apk` 是 Dex bytecode，平台无关，由 AOSP Soong + AIDL compiler 构建最直接。
- AOSP 自身的 native .so（libart 等）依赖图深度绑定 AOSP 源码（libdexfile/libartbase/libnativehelper 等），必须用 AOSP Soong 构建；但为了能在 OH/musl 上运行，需走交叉编译 + bionic→musl 兼容层（`libbionic_compat.so`）。
- 适配层自己的 native .so 需要链接 OH IPC 库（ipc_core、samgr_proxy），只有 OH BUILD.gn 能解析这些依赖，因此走 OH 构建。

**区分测试**："这个二进制运行在哪里？"
- DAYU200 设备运行、且依赖 AOSP 源码树 → AOSP Soong（交叉编译到 `out/aosp_lib/`）
- DAYU200 设备运行、且依赖 OH IPC/inner_api → OH BUILD.gn（落到 `out/adapter/` 或 `out/oh-service/`）
- ECS Linux x86_64 构建主机运行（install/build 辅助工具）→ AOSP Soong（落到 `out/host-tools/`）

### AOSP Build (Android.bp) — Java Artifacts

- Java library: `oh-adapter-framework` (java_library, depends on `framework`)
- AOSP runtime Java artifacts: framework.jar, core-oj.jar, framework-res.apk
- **Note**: APK（含 Hello World 测试 APK）**不**在 AOSP Soong 中构建——见下方"APK 编译位置规则"。

#### APK 透明适配原则（项目根本约束 / Core Project Invariant）

**本项目存在的唯一理由是: 任何现有 Android APK 不重新编译就能在 OH 上运行。** 任何违反此原则的设计都是错的。

具体规则:

1. **APK 源代码必须是纯原生 Android 代码**。绝不允许 `import adapter.*` 任何类,绝不允许调用 `OHEnvironment.*`、`ActivityManagerAdapter.*` 等适配层 API。APK 不应感知"OH"或"适配层"的存在。
2. **所有适配工作必须发生在 APK 之外**:
   - **Java 层**: framework.jar 中的 L5 patches (反射注入 Adapter) — 在 `ActivityManager.getService()` 等关键入口拦截
   - **Native 层**: libhwui.so / liboh_adapter_bridge.so / liboh_hwui_shim.so 等适配 .so
3. **任何 APK,包括 Hello World 测试用 APK,都应能不修改源码、不重编、直接安装到 OH 设备运行**。Hello World 的源代码必须保持纯 Android(只用 `android.*` 包),不能为了"测试适配层是否生效"而引入对适配层的引用。
4. **验证适配层是否工作的方法**:不是在 APK 里调用 `OHEnvironment.isOHEnvironment()`,而是通过 logcat / dumpsys / hdc shell 等**外部观察手段**确认 native 调用走到了 OH 服务。

#### APK 编译位置规则

**所有 Android APK 的编译必须在本机(Windows,`D:\code\adapter\app\`)通过 Android Studio 完成,不在 ECS 上编译。**

### OH Build (BUILD.gn) — All Native Artifacts

OH build system via `BUILD.gn` (GN + Ninja):
- Adapter JNI: `oh_adapter_bridge` (ohos_shared_library) → `framework/jni/BUILD.gn`
- APK installer: `apk_installer` (ohos_shared_library) → `framework/package-manager/BUILD.gn`
- Hybrid spawner: `appspawn-x` (ohos_executable) → `framework/appspawn-x/BUILD.gn`
- OH system service patches: `abilityms`, `libappms`, `scene_session_manager`, `scene_session`, `libbms`
- AOSP native cross-compilation: libart.so, libandroid_runtime.so, dex2oat (OH clang + musl)
- Build command: `cd ~/oh && ./build.sh --product-name rk3568 --ccache --gn-args allow_sanitize_debug=true --build-target <target>`
- OH build patches (backed up before applying, not reverted after build): stored in `ohos_patches/`

## Root Cause Analysis Discipline (项目级铁律)

**Android 和 OH 都是成熟系统，遇到运行/链接/解析等问题时，禁止从"质疑 Android 或 OH 现有实现的正确性"开始。** 第一嫌疑必须放在 adapter 层。具体排查顺序：

1. **适配层的转换哪里没有处理好** —— adapter 自己写的 Java/Native 代码（`framework/`）、JNI 桥接、stub/shim、register_X、IPC 接口转换、Java↔OH IPC 数据结构映射。这是绝大多数问题的真正出处。
2. **编译参数与 Android/OH 原生不一致** —— cross-compile 时缺的 `-D` 宏、wrong target triple、缺的 include path、`__ANDROID__` / `__OHOS__` 这类预定义宏被错误条件分支选中、ABI flag (`-fno-rtti` / CriticalNative ABI 等) 不匹配、`patches/` 内对原生代码的微调引入 ODR 违规。

只有上述两个方向都排查过且证据明确指向上游，才允许提出"原生实现可能有 bug"的假设——而且必须先做 control 实验（同一调用从两个不同 adapter 路径触发对比）才能成立。

历史血训（2026-04-29 B.21 r3）：连续两次在 framework-res 解码链路上把根因错指到 libandroidfw 和 OH libskia_canvaskit，实际真因都是 adapter 层（一次是 system context Resources 状态早期化未完成，另一次是 adapter `RegisterAllSkiaCodecs` 漏调）。

参考 memory：`feedback_blame_adapter_first.md`、`feedback_compile_align_aosp.md`、`feedback_critical_native_abi_macro.md`。

## Workflow

本节原先包含的"无需确认即可执行"、"不要 rm -rf out/"、"禁止 stub 回避编译"、"Post-Completion Feedback Rule"等条款已迁移到用户级 memory (`feedback_no_confirmation.md`、`feedback_no_clean_out.md`、`feedback_no_stub_compile.md`、`feedback_post_completion_report.md`、`feedback_decision_transparency.md`),由 agent 在每次会话自动加载。CLAUDE.md 仅保留下方项目级的产物更新约定。

### Post-Completion Checklist (任务完成后必须更新)

**Every successful task completion (compilation, deployment, feature implementation, bug fix) MUST update all of the following:**

1. **Design documents** (`doc/`): Update relevant design docs (`xxx_design.html`, `build and deployment design.html`, etc.) to reflect the new state — including build status, deployment status, architecture changes, and new components.
2. **Compile report** (`doc/compile_report.html`): Update compilation results, artifact sizes, problem/solution records, and status tables.
3. **Build patch report** (`doc/build_patch_log.html`): Record all AOSP/OH source modifications, patches applied, and build configuration changes made during this task.
4. **Build scripts & source code**: Sync any modified build scripts (`build/`), source code (`framework/`), and patches (`aosp_patches/`, `ohos_patches/`) to both local and ECS.
5. **Technical decision overview** (`doc/technical_decision_overview.html`): If this task introduced or retired any temporary workaround / stub / shortcut, or made a directional technical choice, update the corresponding section.

## GitHub Repository

> **当前状态：暂停同步到 GitHub**。未经用户明确指示不要主动推送、不要主动建议恢复。下方配置仅作为未来恢复同步时的参考。

- **GitHub**: `https://github.com/AlexYang-AgentCode/AI-Core-Team-Project/tree/add-m_HanBingChen`
- **Local**: `D:\code\adapter`
- **Branch**: `add-m_HanBingChen`
- **Repo path**: Local `D:\code\adapter\` maps to repo `m_HanBingChen/` (i.e., `https://github.com/AlexYang-AgentCode/AI-Core-Team-Project/tree/add-m_HanBingChen/m_HanBingChen`)
- **Sync scope**: Only sync the following directories and files to GitHub:
  - Directories: `.claude`, `aosp_patches`, `build`, `doc`, `framework`, `ohos_patches`
  - Files: `build.sh`, `CLAUDE.md`, `readme.txt`
  - Exclude from GitHub: `app/`, `prebuilts/`, `bundle.json`, `out/`, `bkup/`, `cmd.txt`, `push_cmd.txt`, `OH_Git.txt`, `fix_and_build.py`
- **Note**: The GitHub exclusion list is independent from the Local↔ECS sync map above. Items like `bundle.json` and `out/` ARE pulled from ECS to local for backup purposes, but are NOT pushed to GitHub.

### Sync Files to GitHub (without cloning)

Plumbing-based push script (避免 Windows checkout 遇到 remote 仓库里非法路径)：`build/github_sync.sh`。使用 `git hash-object` + `git mktree` + `git commit-tree`，通过 Windows Credential Manager 认证。当前暂停调用（见本节顶部状态）。

## Coding Conventions

- **All source code comments must be in English only**
- **When generating or modifying source code, synchronously generate or update the corresponding design documents (HTML format) in `doc/`**
- **Design documents must be written in Chinese**
- **HTML设计文档的标题和目录风格，统一到 `doc/build and deployment design.html` 的风格：**
  - 目录项必须带超链接（`<a href="#chN">`），点击可跳转到对应章节内容
  - 章节标题（`<h1>`, `<h2>`）必须带有底色背景（使用 CSS `background` 属性），参考 `service_extension_design.html` 中的渐变色或纯色底色样式
  - 所有新生成或重写的 HTML 设计文档必须遵循此风格
- Bridge methods organized by functional category with clear section separators
- Each bridge class has `logBridged()` / `logStub()` helpers for consistent logging
- Log tags: `OH_AMAdapter`, `OH_ATMAdapter`, `OH_WMAdapter`, `OH_WSAdapter`

## Source Reference Paths

OH inner_api 接口头文件、Android AIDL 源文件的完整路径表已迁至 `doc/source_reference_paths.md`。
