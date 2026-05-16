**[English](V3-ARCHITECTURE.md)** | **[中文](V3-ARCHITECTURE_CN.md)**

# Westlake V3 架构 — HBC 运行时 + Westlake 应用承载引擎

**状态：** 权威版本（2026-05-15）。在 OHOS 路径上取代 V2 Phase-2-OHOS 方向（`project_v2_ohos_direction.md` 2026-05-15 上午版本）。**不取代** Android 手机 Phase-1 路径下的 V2 — 该栈完全保留。

**日期：** 2026-05-15
**作者：** agent 42，基于 CR-EE / CR-FF HBC 架构分析
**前向参考文档（最新有效）：**
- `V3-WORKSTREAMS.md` — V3 OHOS 路径 W1-W13 工作流分解
- `V3-SUPERVISION-PLAN.md` — 优先级排序、并行度、前 3 天派遣顺序
- `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` — 显式撤回 CR61 对 libipc/samgr 的禁令（仅 V3 路径）
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` — HBC 栈的结构概览（V3 所采用的运行时）
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` — V3 所采纳的战术模式
- `CR-DD-CANDIDATE-C-VS-V2-OHOS-RECONSIDERED.md` — 触发今天枢轴的分析
- `CR60_BITNESS_PIVOT_DECISION.md` — DAYU200 上 32 位 ARM 的前置条件（V3 保留）

**历史文档（原样保留）：**
- `BINDER_PIVOT_ARCHITECTURE.md` — V2 综合架构（Phase-1 Android 保持权威）
- `BINDER_PIVOT_DESIGN.md`（V1, 2026-05-12）
- `BINDER_PIVOT_DESIGN_V2.md`（V2, 2026-05-13）
- `CR41_PHASE2_OHOS_ROADMAP.md` — V2 Phase-2-OHOS 里程碑（M9-M13）— OHOS 路径下被 V3 取代
- `project_v2_ohos_direction.md` — V2-OHOS 承诺 — 被 V3 取代

---

## 0. 执行摘要

V2 Phase-1 substrate 在 Android（in-process Option 3：noice + McD 同时跑在 `com.westlake.host` 进程内）上已端到端验证。之后 Westlake 用了约 24 个 commit（CR-W → CR-X → CR-Y → CR-Z 链路）尝试把 dalvik-kitkat + Westlake substrate 栈延伸到 OHOS-DAYU200。端到端可见结果没推进（CR67 / CR-AA 诊断显示面板像素仍是 launcher 默认白）。`feedback_additive_shim_vs_architectural_pivot.md` 规则触发：错的是层本身，不是下一个 shim。

今天 CR-EE 与 CR-FF（agent 40 + 41）审计了同公司同事 HBC 的工作树。HBC 已经用 **真实 AOSP-14 ART + 真实 `framework.jar` 跨编译到 OHOS musl** 独立解决了 OHOS+Android 承载问题，成本是好几个工程师月：以 `appspawn-x` 作为 Zygote 等价物，5 个正向 `IXxx.Stub` 适配器 + 6 个反向 C++/Java 桥，用 `liboh_skia_rtti_shim.so`（12.6 KB）替代重建 Skia。今天他们已经走到 `MainActivity.onCreate` 第 83 行 — 比 Westlake V2-on-OHOS substrate 走得远得多。复用 HBC 的产物不存在知识产权问题：同公司同项目。

**V3 把 Westlake 锁定为：** 在 OHOS 路径上不再自己拥有 Java substrate / dalvikvm / libhwui / boot image；改为 **复用 HBC 的运行时 substrate**，把 Westlake 的投入重新聚焦到运行其上的 **应用承载引擎层** — Intent 重写、生命周期编排、多应用协调、Android 端宿主 APK、mock-APK 与单应用校验。Phase-1 Android 手机栈（V2 in-process 宿主）原样保留。

V3 在架构上比 V2-OHOS **更简单**（约 85% 的平台 substrate 工作不再是 Westlake 的事）；在战略上对 Westlake 而言**更聚焦** — 我们拥有 Westlake 真正带来产品价值的那一层，而不是 HBC 已经付出代价的那一层。

---

## 1. 战略背景 — 为何要 V3，为何是今天

### 1.1 V2 Phase-1（Android 手机）究竟证明了什么

V2 substrate 在原生 Android 手机（cfb7c9e3、Android 15 LineageOS 22）上的 in-process Android 承载是可用的。按 `MEMORY.md` 2026-05-14 "BOTH apps in-process (OPTION 3)" 条目：

- noice 渲染真实界面：Welcome → Library/Favorites/Profile 页签、完整 fragment 导航
- McD 渲染真实界面：SplashActivity → Wi-Fry McD 品牌离线页
- 通用 5-柱模式：隐藏 API 绕过 + LoadedApk 目录重定向 + 安全 context bind stub + LocaleManager binder hook + 生命周期推进到 Resumed
- 每应用差异 = 4 个常量 + manifest 别名（无每应用代码分支；遵守 macro-shim 契约）

这是 Westlake Phase-1 的产品交付物。**V3 100% 原样保留它。** 它跑在真实 Android、真实 `framework.jar`、真实 ART 之上。宿主 APK（`westlake-host-gradle/`）保留。

### 1.2 V2 Phase-2（OHOS）尝试了什么 / 被什么挡住

V2-OHOS 尝试把同一 substrate 移植到 OpenHarmony，靠的是：
- 自己实现 libbinder + servicemanager（M9-M10, CR61）
- 自己实现 audio daemon（M5/M11）+ surface daemon（M6/M12）
- 自己写一个 `SoftwareCanvas extends android.graphics.Canvas`，只记录 `drawColor` + 最后一次 `drawRect`
- 自己写 `drm_inproc_bridge.c` 直接 blit 到 `/dev/fb0`（后来是 DRM/KMS）
- 维护约 12,403 LOC 的 Westlake 影子 framework 类（Application、Activity、Resources、Window、PhoneWindow、DecorView、WindowManagerImpl）

CR-W → CR-X → CR-Y → CR-Z 链路（约半天战略时间、24 个 commit）加上了 arsc 解析、theme 解析、生命周期推进、BCP 级 Locale/Date/ByteOrder 补丁。最终结果：面板像素 = launcher 默认白。`feedback_additive_shim_vs_architectural_pivot.md` 把模式固化为：**当连续 ≥3 个 CR 都落在同一层、各自干净 land、但端到端可见结果不动时，问题在层本身**。

### 1.3 HBC 独立证明了什么（CR-EE / CR-FF）

HBC `~/adapter/` 工作树显示：
- 真实 AOSP-14 ART 跨编译到 OHOS 32 位 musl：`libart.so`（15 MB）、`libhwui.so`（1.85 MB）、`libandroid_runtime.so`，共 38 个 .so
- 真实 `framework.jar` 字节级一致 + **4 个外科手术式 L5 补丁**（共 228 行）：把 `IActivityManager` / `IActivityTaskManager` / `ActivityThread.getPackageManager` / `IWindowManager` 的 `Singleton.create()` 委派到 `OHEnvironment.getXxxAdapter()`（CR-EE §3）
- `appspawn-x` daemon 作为 Zygote 等价物（`/system/bin/appspawn-x`，OH AppSpawn 与 AOSP Zygote 的混合体，311 行 main.cpp）— 见 CR-EE §1.5 + §5 process model
- 真实 boot image：9 段 `dex2oat` AOT（`boot.art/oat/vdex` + 8 个模块段）
- `liboh_skia_rtti_shim.so`（12.6 KB，18 个 RTTI 符号）替代重建 23 MB Skia（CR-EE §6, CR-FF Pattern 4）
- `libbionic_compat.so`：AOSP bionic ABI ↔ OHOS musl 的兼容垫片
- 5 个正向桥（`extends IXxx.Stub`）+ 6 个反向回调 C++/Java 三明治，合计 **530+ AIDL 方法**（CR-EE §1.3, §7）
- HelloWorld.apk 跑到 `MainActivity.onCreate` 第 83 行（TextView 构造），有真实 `handleBindApplication`、真实 Application 实例、真实 `PhoneWindow.<init>`、真实 `Activity.attach`、`addToDisplayAsUser ADD_OKAY=0`、`activityResumed state=9 rc=0`（CR-EE §8）
- 工程不变量：**APK 透明性**（任何 APK 源码中均无 `import adapter.*`）

这远远超过 Westlake V2-OHOS 现在能达到的位置，背后是 10× 的投入。复用 HBC 的产物是理性选择。

### 1.4 决策

**Westlake 承诺 V3**，定义如下：
- 放弃 dalvik-kitkat OHOS 移植（退到 demo / 归档地位）
- 放弃 `aosp-shim-ohos.dex` framework 替身（由 HBC 真实 `framework.jar` 替代）
- 放弃 SoftwareCanvas + `drm_inproc_bridge.c`（由真实 HWUI + HBC 的 `ANativeWindow → IBufferProducer` 链路替代）
- 放弃 M5 audio daemon + M6 surface daemon 的 OHOS 目标（由 HBC `appspawn-x` + 渲染集成替代）
- 放弃 Westlake 自己的 libbinder + servicemanager 在 OHOS 路径上的角色（由 HBC 适配器框架替代，直接链接真实 OH IPC innerAPI）
- **V2 Phase-1 Android 栈保持不变**
- **Westlake 自有工程精力重新聚焦在 HBC 运行时之上的应用承载引擎层**

Phase-1 Android 栈（V2 in-process Option 3）不受影响。V3 仅替换 Phase-2-OHOS 方向。

---

## 2. V3 层栈

```
层栈（V3 OHOS 路径）
─────────────────────────────────────────────────────────────
APP（未修改的 APK：noice / McD / 任意 Android 应用）
  - APK 透明性不变量：任何宿主类的 import 都为零
  - 仅由外部观察者验证（logcat / hilog / dumpsys）
─────────────────────────────────────────────────────────────
WESTLAKE 应用承载引擎                       ← Westlake 自有
  - westlake-host-gradle（Android 侧；Phase 1 保持不变）
  - Intent 重写（Instrumentation 子类用于跨包）
  - 生命周期推进模式（V2 的 5-柱模式）
  - 多应用协调（每个 Westlake app 一个 HBC 子进程）
  - 每应用常量表（每应用 4 个常量，无代码分支）
  - macro-shim 契约执行（范围收窄：仅集成接缝）
─────────────────────────────────────────────────────────────
HBC 运行时 substrate                       ← 复用同事 HBC 的工作
  - AOSP-14 ART 跨编译到 OHOS 32 位 musl（38 个 native .so + 11 个 jar）
  - 真实 framework.jar + 4 个外科手术式 L5 补丁（共 228 LOC）
  - libhwui + Skia RTTI shim（liboh_skia_rtti_shim.so 12.6 KB）
  - libbionic_compat.so（musl ↔ bionic ABI 兼容）
  - 9 段 dex2oat AOT boot image（boot.art/oat/vdex ×9）
  - appspawn-x daemon（Zygote 等价物；与 OH AppSpawn socket 兼容）
  - 5 个正向桥（IXxx.Stub 子类）+ 6 个反向 C++/Java 三明治；共 530+ AIDL 方法
  - OHEnvironment + 双 classloader（PathClassLoader 用于非 BCP）
  - ScopedJniAttach RAII
─────────────────────────────────────────────────────────────
OHOS 平台                                   ← OHOS 原生（legacy WMS）
  - libipc_core + samgr_proxy（仅 innerAPI 变体 — 见 CR61.1）
  - render_service / composer_host
  - legacy WindowManagerService（DAYU200 上 window_manager_use_sceneboard=false）
  - SceneBoard 今天未启用；列入 W8（独立于 V3）
─────────────────────────────────────────────────────────────
硬件（DAYU200 rk3568, 32 位 ARM 用户态）
```

**跨平台 / 跨阶段映射：**

| 阶段 / 目标 | 栈 |
|---|---|
| Phase 1, Android 手机（cfb7c9e3） | V2 in-process Option 3 — `westlake-host-gradle` 跑在宿主手机原生 ART + 真实 framework.jar 之上 — V3 不变 |
| Phase 1, McD / noice / 任意 APK | V2 5-柱模式 + 每应用常量表 — V3 不变 |
| Phase 2, OHOS DAYU200 | 如上定义的 V3 栈 |
| 未来, OHOS PC / 2-in-1 / SceneBoard | 同一 V3 栈 + SceneBoard 板级配置 workstream（W8） |
| 未来, 64 位 OHOS ROM | 同一 V3 栈，重新构建 HBC 的 aarch64 变体（CR60 §"Cost of reverse-pivot"：2-4 天复验） |

---

## 3. 组件归属

| 组件 | 归属 | 来源 |
|---|---|---|
| `westlake-host-gradle/` Android 宿主 APK | Westlake | 本仓库 |
| 应用承载引擎（Phase 1 in-process Option 3） | Westlake | `NoiceInProcessActivity.kt`、`McdInProcessActivity.kt` |
| 应用承载引擎（Phase 2 OHOS，跑在 HBC 上） | Westlake | 新建 — 基于 HBC `appspawn-x` |
| Intent 重写（Instrumentation 子类） | Westlake | 开放项，按 `project_noice_inprocess_breakthrough.md` |
| 每应用常量表 | Westlake | 沿用 V2 契约，V3 下范围收窄 |
| macro-shim 契约执行 | Westlake | `feedback_macro_shim_contract.md`（在集成接缝处仍适用） |
| AOSP-14 ART（`libart.so`、dex2oat） | HBC | `~/adapter/out/aosp_lib/libart.so`、`~/adapter/out/host-tools/dex2oat` |
| `framework.jar` + 4 个 L5 补丁 | HBC | `~/adapter/aosp_patches/frameworks/core/java/android/{app,view}/*.patch` |
| `libhwui.so`（真实 AOSP-14） | HBC | `~/adapter/out/aosp_lib/libhwui.so` |
| `liboh_skia_rtti_shim.so` | HBC | `~/adapter/framework/surface/jni/skia_rtti_shim/` |
| `libbionic_compat.so` | HBC | `~/adapter/framework/appspawn-x/bionic_compat/` |
| boot image（9 段） | HBC | `~/adapter/out/boot-image/boot.{art,oat,vdex}` + 8 个模块段 |
| `appspawn-x` daemon | HBC | `~/adapter/framework/appspawn-x/` |
| 5 个正向桥（IXxx.Stub） | HBC | `~/adapter/framework/{activity,window,broadcast,contentprovider,package-manager}/java/` |
| 6 个反向 C++/Java 桥 | HBC | `~/adapter/framework/{activity,window}/jni/*_adapter.cpp` |
| `OHEnvironment` + 双 classloader | HBC | `~/adapter/framework/core/java/OHEnvironment.java` |
| `libipc_core` / `samgr_proxy`（innerAPI） | OHOS | 通过 HBC `liboh_adapter_bridge.so` 传递链接 |
| `render_service` / `composer_host` | OHOS | 保持不变 |
| legacy WMS（DAYU200） | OHOS | 保持不变 |
| SceneBoard | OHOS | DAYU200 上未启用 — 板级配置项（W8） |
| Linux 内核 binder（`/dev/binder`） | OHOS | 保持不变；CR60 已验证 |

**范围纪律：** Westlake 拥有 Westlake 行。HBC 拥有 HBC 行（我们拉取产物，**不 fork 源码**）。OHOS 拥有平台行（按 CR61.1 消费 innerAPI）。

---

## 4. V3 在 OHOS 路径上从 Westlake 删除的内容

每个删除项都列出替换方（HBC 组件或 OHOS 原生服务），让契约具体可查。

| V3 下删除的 V2-OHOS 组件 | 行数 | 替换方 | 引用 |
|---|---|---|---|
| `dalvik-port/` OHOS 目标（32 位静态 + 动态 dalvikvm） | ~50K（多数为上游） | HBC `libart.so` + `appspawn-x` | CR-EE §1, §5 |
| `aosp-shim-ohos.dex` framework 替身 | ~1.3 MB dex（约 70K LOC） | HBC 真实 `framework.jar` | CR-EE §3 |
| CR-W setContentView NPE 修复（3 个 NPE） | ~200 | 真实 `framework.jar` 此处不 NPE | CR-EE §8 |
| CR-X arsc 解析 + theme 解析 | ~400 | 真实 `Resources` / `AssetManager`（AOSP） | CR-EE §3 |
| CR-X+1 生命周期推进 | ~150 | 真实 ClientTransaction + `handleLaunchActivity` 经 `AbilitySchedulerBridge` | CR-EE §5, §7 |
| CR-Y+1 Locale.forLanguageTag BCP 补丁 | ~100 | 真实 AOSP libcore | CR-EE §3 |
| CR-Z Date/ByteOrder `<clinit>` 修复 | ~80 | 真实 AOSP libcore | 同上 |
| `SoftwareCanvas extends android.graphics.Canvas` | ~200 | 真实 libhwui + 真实 Skia | CR-EE §6, CR-FF Pattern 4 |
| `drm_inproc_bridge.c`（直接 `/dev/fb0` blit） | ~300 | HBC `ANativeWindow → OHNativeWindow shim → IBufferProducer → RS BufferQueue` | CR-EE §6 |
| `aosp-libbinder-port/` 的 OHOS arm32 目标（M10） | 已构建 | HBC `liboh_adapter_bridge.so` 链接真实 OH `libipc_core` + `samgr_proxy`（innerAPI） | CR-FF §"CR61-equivalent finding" |
| Westlake 自己的 servicemanager 在 OHOS `/dev/vndbinder` 上 | ~2K | HBC 的 5 正向 + 6 反向桥到真实 OH 服务 | CR-EE §1.3 |
| `aosp-audio-daemon-port/` 的 OHOS arm32 目标（M5/M11） | ~5K | HBC `liboh_adapter_bridge.so` 音频路径走 OH `audio_renderer` | CR-FF Pattern 1 |
| `aosp-surface-daemon-port/` 的 OHOS arm32 目标（M6/M12） | ~8K | HBC `OHGraphicBufferProducer` + `RSSurfaceNode` 路径 | CR-EE §6, §7 |
| `OhosMvpLauncher` 驱动 | ~600 | HBC `aa start <bundle>` → AMS → `AbilitySchedulerAdapter` → ActivityThread | CR-EE §5 |
| `InProcessAppLauncher` 缓冲区物化 | ~100 | 真实 hwui RenderThread + EGL eglSwapBuffers | CR-EE §6 |

**累计删除约 70K+ LOC substrate 工作 + 约 16K LOC native daemon 工作**，从 Westlake 在 V3 下的归属里移除。这部分工作不是"白做"（它带来的诊断让我们认识到层是错的），但它不再随 V3 出货。

**V3 不删除的内容：**

| 组件 | 保留原因 |
|---|---|
| 所有 V2 Phase-1 Android 手机代码（`westlake-host-gradle`, in-process Option 3, McD/noice 集成） | 手机路径保持。V3 只影响 OHOS 目标。 |
| V2 Java substrate 跑在 Android 手机（cfb7c9e3）上的部分 | 同上 — Android 侧不变 |
| M5 audio daemon + M6 surface daemon 用于 **Android 手机** 路径 | 手机 V2 路径仍需要。只删除这些 daemon 的 OHOS 目标。 |
| CR60 位宽纪律（32 位 ARM 用户态、intptr_t/size_t、双 arch CI） | 继承前提条件 — HBC 栈在 DAYU200 上也是 32 位 ARM（CR60 §"Empirical evidence"） |
| macro-shim 契约 | 仍适用。V3 下范围收窄到 Westlake 引擎与 HBC 运行时之间的**集成接缝**，不在 framework 类级别（那里是真实 AOSP） |
| Memory + handoff 系统 | 更新指向 V3（W10） |
| 测试基础设施（`scripts/run-ohos-test.sh` 等） | 适配到 HBC 产物路径（W2） |
| `aosp-libbinder-port/`（**Android 手机 V2 路径**的 musl + bionic 构建） | Phase 1 保持不变。仅 OHOS arm32 目标不再使用 |

---

## 5. V3 从 HBC 借用的内容（带引用）

每行都引用 CR-EE 或 CR-FF。

### 5.1 构建产物（二进制复用，不 fork）

| 产物 | 大致大小 | 来源 | 引用 |
|---|---|---|---|
| `libart.so` | ~15 MB | HBC `~/adapter/out/aosp_lib/libart.so` | CR-EE §3, App. B |
| `libhwui.so` | ~1.85 MB | HBC `~/adapter/out/aosp_lib/libhwui.so` | CR-EE §3 |
| `libandroid_runtime.so`（被 `liboh_android_runtime.so` 替换） | 可变 | HBC `~/adapter/out/aosp_lib/` | CR-EE §3，注意 §12-item-3 关于 register_* stub 的告警 |
| 38 个跨编译 AOSP native .so | 共 ~80 MB | HBC `~/adapter/out/aosp_lib/` | CR-EE App. B |
| 11 个 framework jar | ~80 MB | HBC `~/adapter/out/aosp_fwk/` | CR-EE §3 |
| 9 段 dex2oat AOT boot image | 可变，27 文件 | HBC `~/adapter/out/boot-image/` | CR-EE §3 |
| `libbionic_compat.so` | 小 | HBC `~/adapter/framework/appspawn-x/bionic_compat/` | CR-EE §3 |
| `appspawn-x` 二进制 | 小 | HBC `~/adapter/out/adapter/appspawn-x` | CR-EE §2 |
| `liboh_skia_rtti_shim.so` | 12.6 KB | HBC `~/adapter/framework/surface/jni/skia_rtti_shim/` | CR-EE §6, CR-FF Pattern 4 |
| 7 个补丁过的 OH 服务 .so | 可变 | HBC `~/adapter/out/oh-service/` | CR-EE §4 |
| 适配器 Java + JNI 源（12K+ LOC） | 源码 | HBC `~/adapter/framework/` | CR-EE §2 |

### 5.2 架构模式（概念复用）

| 模式 | Westlake 采用方式 | 引用 |
|---|---|---|
| 4 个 L5 framework 补丁通过 `OHEnvironment.getXxxAdapter()` | 原样采用 — Westlake 不撰写 | CR-FF Pattern 1 |
| `OHEnvironment` + 双 classloader（BCP 只放 `OHEnvironment`；真实适配器类放在 `oh-adapter-runtime.jar`，由系统 PathClassLoader 加载） | 原样采用；适配器迭代成本 = **0 次 boot-image 重建** | CR-FF Pattern 2 |
| 反向桥三明治（C++ IRemoteStub → JNI → Java Bridge） | 任何新的 Westlake 侧服务回调采用此模式 | CR-FF Pattern 3 |
| `ScopedJniAttach` RAII | V3 所有 JNI 辅助代码强制使用 | CR-FF Pattern 3 末段；CR-EE §7 |
| `liboh_skia_rtti_shim.so` 审计方法（`build/discover_skia_rtti_syms.sh`） | 通用"ABI 缺口、语义无缺口"技术 — 任何未来符号缺口可复用 | CR-FF Pattern 4 |
| `DEPLOY_SOP.md` 阶段化部署纪律 | 采用；W9 把 SOP 移植到我们的部署脚本 | CR-EE §9, CR-FF "Pattern 5" |
| 一键恢复规则（`bash restore_after_sync.sh && <build>` 从干净 `repo sync` 重现 last-green） | 采用为 `bash scripts/restore-v3-state.sh` | CR-EE §9 |
| "先怀疑适配器" RCA 纪律 | 在集成接缝处采用 | CR-EE §11.7 |
| `Handler(Looper.getMainLooper()).post(...)` 在 JNI 接缝处切线程 | 采用（已是 Westlake 经验，见 CR59） | CR-EE §7 |
| APK 透明性不变量 + anti-grep CI 检查 | W6/W7 验收要求 | CR-EE §11.1 |
| innerAPI > NDK 规则 | 作为 CR61.1 修正案采用 | CR-FF §"CR61-equivalent finding" |

### 5.3 显式不借用的反模式

按 CR-EE §12 / CR-FF §"TL;DR bullet 5"：

- "把 AOSP-14 native + Java + boot image 跨编译到 musl"的工作本身 — Westlake 复用 **输出**，不维护源补丁或构建管线
- libhwui 的 40+ 个孤立 in-place python `sed`-脚本（HBC 自己承认是反模式，分类为待整改；Westlake 永不复制）
- "多数 stub 的 `liboh_android_runtime.so`"模式（13/15 register_* 模块是 stub）— V3 验收闸要求 Westlake 侧验证我们承载的 App 实际只需要 HBC 已经接通的 AOSP-real 子集
- 给 OH `init` `MAX_ENV_VALUE` 打补丁 — 替代为在 app-hosting 引擎入口 `setenv()`
- 把 legacy WMS 作为唯一窗口路径（DAYU200 上 SCB 关，但 V3 计划通过 W8 启用 SCB）
- `IActivityClientController` 的 "Java Proxy stub-everything" 模式 — 隐藏语义缺口；Westlake 的 "用真实方法调用驱动生命周期到 Resumed" 仍是契约

---

## 6. 从 V2 到 V3 的迁移路径

### 6.1 V3 落地当天的变化

- 本文档 + `V3-WORKSTREAMS.md` + `V3-SUPERVISION-PLAN.md` + `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` 提交
- `project_v2_ohos_direction.md` 内存条目标注 SUPERSEDED-BY V3
- `CR41_PHASE2_OHOS_ROADMAP.md` 在 OHOS 路径上标注 SUPERSEDED-BY V3；V1/V2 历史内容原样保留
- `CR61_BINDER_STRATEGY_POST_CR60.md` 在 V3 路径上标注 AMENDED-BY `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`
- 为 W1-W13 开 GitHub issues

### 6.2 W1 期间（HBC 产物盘点 + 拉取）

- HBC 二进制产物拉到 Westlake 仓库 `third_party/hbc-runtime/`
- Westlake 自己的 `dalvik-port/` OHOS 目标归档（**不删除** — 移到 `archive/v2-ohos-substrate/`）
- `aosp-shim-ohos.dex` 归档

### 6.3 W2-W5 期间

- V3 在 DAYU200 上独立跑通 HBC 运行时（W2）
- Westlake 应用承载引擎集成替换 `OhosMvpLauncher`（W3）
- 针对 Westlake 范围定制适配器（W4）
- Mock APK 校验（W5）

### 6.4 W6-W7 期间

- noice on OHOS via V3（W6） — 与 V2 in-process Option 3 结果可直接对比
- McD on OHOS via V3（W7）

### 6.5 不变的内容

- 所有 Phase-1 Android 手机代码原地保留，是 Android 上的生产路径
- `westlake-host-gradle` 仍是宿主 APK
- macro-shim 契约仍适用（V3 下范围收窄 — 仅集成接缝；见 §7）
- CR60 位宽纪律仍适用（V3 栈是 DAYU200 上的 32 位 ARM）
- Memory + handoff 节奏不变

---

## 7. macro-shim 契约 — V3 范围

`feedback_macro_shim_contract.md` 中的契约 **仍适用**，范围调整：

**V2 范围：** Westlake 自有类覆盖几乎整个 AOSP framework 表面（Application/Activity/Resources/Window/PhoneWindow/DecorView/WindowManagerImpl + 7 个服务类）。

**V3 范围：** Westlake 自有类**仅**覆盖应用承载引擎表面。HBC 的真实 `framework.jar` 意味着我们不拥有（也不应 shim）任何 framework 类。契约在 Westlake 引擎与 HBC 运行时之间的**集成接缝**生效。

**V3 允许（macro shim）：**
- 在 Westlake 自有类上实现公开 / 受保护 API 方法：
  - 应用承载引擎入口
  - Intent 重写（如 Instrumentation 子类）
  - 生命周期编排回调
- 方法体必须是：
  - (a) AOSP-default 原文（通过 HBC 适配器委派到真实 OH 服务）
  - (b) 安全原语（return null / false / 0 / 空 list / no-op）
  - (c) 委派到本类另一方法

**V3 禁止（micro shim / 漂移）：**
- 对任何 framework 类（HBC 或 AOSP）调用 `sun.misc.Unsafe.allocateInstance(...)`
- 在 framework 内部（HBC 适配器内部 **或** 真实 `framework.jar` 内部）做 `Field.setAccessible(true)` + 反射 set
- 在 `LoadedApk` / `ContextImpl` / `ActivityThread` / `Theme` / `Configuration` / `AssetManager` 上"种"状态
- 每应用分支（`if (pkg.equals("com.mcdonalds.app"))`）— **同前规则，无例外**
- 捕获 framework 类反射的 `NoSuchMethodError` / `LinkageError`
- 修改 HBC 适配器 Java 源以加 Westlake 特定逻辑（向 HBC 提 bug，或在 Westlake 自有代码中复制该模式）

**自查闸（V3 CR 完成前运行）：**

```bash
# 触动文件中无新增 Unsafe
grep -rn "sun.misc.Unsafe\|jdk.internal.misc.Unsafe\|Unsafe.allocateInstance" <touched files> | grep -v "^.*://"
# 无新增 setAccessible
grep -rn "setAccessible(true)" <touched files> | grep -v "^.*://"
# 无新增每应用分支
grep -rniE "noice|mcdonalds|com\.mcd|noice\.fragment" <touched files> | grep -v "^.*://\|^.*// "
# 无对 HBC 适配器源的编辑
git diff --name-only HEAD~1 | grep -E "^third_party/hbc-runtime/" && echo "FAIL: edited HBC source"
```

任何上述命令出现新结果 → 停下并回滚。

---

## 8. V3 代码评审中要识别的反模式

源自 `feedback_additive_shim_vs_architectural_pivot.md`，附 V3 具体示例：

1. **"再加一个 shim 就行"** — 若一个 V3 CR 是同一集成接缝上的第 3 个连续 CR（如 3 个 CR 都在 Intent 重写），审计层而不是写第 4 个 CR。

2. **"技术 PASS 但语义 FAIL"** — V3 CR 干净 land 但可见结果（noice 面板像素 / McD Wi-Fry 屏）未动，可疑。检查测试标准是否在查正确的东西。

3. **"AOSP/HBC 已经出货了真东西，你却手撸"** — V3 任务诱使你写自定义 `Window` 或 `Canvas` 或 `View` 时停下。HBC 的 BCP 里有真实 AOSP `framework.jar`，用它。

4. **"为加方法 fork HBC"** — V3 任务诱使你编辑 `~/adapter/framework/.../*.java` 时停下。要么 (a) 向 HBC 提议变更，要么 (b) 在 Westlake `oh-adapter-runtime.jar` 中遮蔽该类（按 CR-FF Pattern 2 经 PathClassLoader 加载），要么 (c) 记下阻塞并停下。直接编辑 HBC 源是漂移的单向门。

5. **"每应用代码分支"** — 同 V2 规则；同自查 grep。

6. **"V3 层里出现 V2 形状的修复"** — 例如把 SoftwareCanvas 风格的 work-around 移植进 V3，是 V3 路径被误解的信号。停下并重读本文档 § 4。

---

## 9. V3 代码自查清单

每个 V3 CR 的报告必须包含：

- [ ] **层归属：** "本变更位于 Westlake 应用承载引擎层，不在 HBC 运行时，不在 OHOS 平台。"（引用文件路径）
- [ ] **未 fork HBC：** "`third_party/hbc-runtime/` 零编辑。"
- [ ] **无 framework shim：** "未新增任何遮蔽 `android.*` framework 类的类。"
- [ ] **无 Unsafe / setAccessible / 每应用分支。** 三个 grep 干净。
- [ ] **集成接缝处遵守 macro 契约：** "所有方法位于 Westlake 自有类；方法体属于 §7 (a)/(b)/(c)。"
- [ ] **APK 透明性保留：** "受测 APK 源码中 `import adapter.*`（HBC）或 `import com.westlake.*`（我们）为零。"
- [ ] **生命周期闸：** "如涉及生命周期推进，在 JNI 接缝处使用了真实 `Handler(Looper.getMainLooper()).post(...)`（CR59 经验）。"

---

## 10. 交叉引用

- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` — HBC 结构概览（V3 采用的运行时）
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` — V3 继承的战术模式
- `CR-DD-CANDIDATE-C-VS-V2-OHOS-RECONSIDERED.md` — 触发今天枢轴的分析
- `CR60_BITNESS_PIVOT_DECISION.md` — DAYU200 上 32 位 ARM 前提（保留）
- `CR61_BINDER_STRATEGY_POST_CR60.md` — 原始 CR61（CR61.1 为 V3 路径修订）
- `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` — 经 HBC 适配器合法化 libipc/samgr 的修正案
- `BINDER_PIVOT_ARCHITECTURE.md` — V2 综合架构（Phase-1 Android，保留）
- `V3-WORKSTREAMS.md` — W1-W13 工作分解
- `V3-SUPERVISION-PLAN.md` — 派遣顺序
- `feedback_additive_shim_vs_architectural_pivot.md` — V3 据之行动的经验
- `feedback_macro_shim_contract.md` — 契约仍适用，V3 下范围收窄
- `artifacts/ohos-mvp/multi-hap-peer-window-spike/20260515_181930/CHECKPOINT.md` — DAYU200 上 SceneBoard 关的发现（驱动 W8 范围）
- Memory: `project_v2_ohos_direction.md` — 被 V3 取代
- Memory: `project_noice_inprocess_breakthrough.md` — Phase-1 Android 基线（不变）
