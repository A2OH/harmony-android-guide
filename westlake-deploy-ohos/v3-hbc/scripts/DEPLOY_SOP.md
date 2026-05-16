# DAYU200 部署 SOP v4

> 项目级权威部署流程。2026-04-21 审核通过，每次 deploy 必须严格遵守，不得省略/重排。
> 本 SOP 是真源；`deploy/deploy_to_dayu200.sh` 目前缺失 4 件（见文末待沉淀章节），须以本 SOP 为准。

## 全局三条（贯穿始终）

1. **禁裸 PID / 禁碰 launcher**：任何 `kill <数字>` 禁用；要杀进程用 `begetctl stop_service` 或 `killall <name>`。不 kill/killall `com.ohos.launcher`——foundation 停了它自然下线。
2. **推 /system 必走 staging**：绝不 `hdc file send <src> /system/...`。一律 `send 到 /data/local/tmp/stage/<basename>` → `ls -la` 验是 `-rw-...` 文件（非 `drwx-`）→ `cp` 进 /system。
3. **中止条件**：hdc 返回 `connect-key` / 超时 / `[Fail]Not a directory` / `ls` 看到 `drwx` → **立即停手**，不链下一条命令。

---

## Stage 0 · 前置检查

- `hdc list targets` 非空 + `hdc shell "echo alive"` 正常
- `/system/android` 不存在（factory 态）
- 本机 `out/boot-image/boot-framework.art` size == 23760896（防截断复发）

---

## Stage 1 · 备份（13 件 device 原件，仅设备侧）

```bash
TS=20260421   # 实际用当天日期
hdc shell "mount -o remount,rw /"
# device-side only:  cp X X.orig_${TS}
# 不做本机 hdc file recv —— 设备同盘 .orig_${TS} 副本已足够，回滚直接 cp 回来
```

覆盖对象清单（13 件）：

| 设备路径 | 数量 | 文件 |
|---|---:|---|
| /system/lib/ | 6 | libwms.z.so / libappms.z.so / libbms.z.so / libskia_canvaskit.z.so / **libinstalls.z.so** / **libappspawn_client.z.so** |
| /system/lib/platformsdk/ | 5 | libabilityms.z.so / libscene_session.z.so / libscene_session_manager.z.so / librender_service_base.z.so / libappexecfwk_common.z.so |
| /system/etc/ | 1 | **ld-musl-namespace-arm.ini** (appspawn-x layer 2 namespace linker 前置) |
| /system/etc/selinux/targeted/contexts/ | 1 | **file_contexts** (appspawn-x layer 1 SELinux label 前置) |

**验证**：13 个目标路径下都应存在 `<file>.orig_${TS}` 副本。

---

## Stage 2 · 停服务（仅 2 个）

```bash
begetctl stop_service foundation
begetctl stop_service render_service
```

- **不停 appspawn**（OH native appspawn 停了会断 hdc 通信链，2026-04-21 事故场景）
- **不动 launcher**（foundation 停了它自然退出）

**验证**：`pidof foundation` 和 `pidof render_service` 为空 + `pidof hdcd` 非空 + `hdc shell "echo alive"` 正常。

---

## Stage 3 · 推送

每件走 staging 模板：`send → ls 验非 drwx → cp`。每 substage 末尾 `hdc shell "ls /system/<dir>"` grep `drwx` 必须无输出。

### 3.0 · 目录预置（一次做完，避免 push 时目录缺失触发 hdc 造目录 quirk）

`/system/etc/init` 是 factory 原有目录（OH 系统服务 cfg 都在这里），**不需 mkdir**；`/system/lib/platformsdk` 也是 factory 原有，mkdir -p 幂等保留兜底；其余 3 个是 adapter 新建。

```bash
hdc shell "mkdir -p /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu"
hdc shell "ls -ld /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu"
# 4 个目录全部显示 drwx
```

### 3b · OH 服务 .so（10 件 + libbms symlink）

| 源 | 目标 |
|---|---|
| out/oh-service/{libwms, libappms, libbms, libskia_canvaskit, **libappspawn_client**}.z.so | /system/lib/ |
| out/oh-service/{libabilityms, libscene_session, libscene_session_manager, librender_service_base}.z.so | /system/lib/platformsdk/ |
| out/aosp_lib/libappexecfwk_common.z.so | /system/lib/platformsdk/ |

libbms symlink（双路径规则）：

```bash
hdc shell "rm -f /system/lib/platformsdk/libbms.z.so && ln -sf /system/lib/libbms.z.so /system/lib/platformsdk/libbms.z.so"
```

### 3c · AOSP native .so（38 + 3 件）

| 源 | 目标 |
|---|---|
| out/aosp_lib/*.so（38 件，除 libappexecfwk_common） | /system/android/lib/ |
| out/adapter/{liboh_hwui_shim, liboh_android_runtime, **liboh_skia_rtti_shim**}.so | /system/android/lib/ + /system/lib/（dual-path）|

**liboh_skia_rtti_shim 是 libhwui 的 DT_NEEDED；漏推则 libhwui 直接 dlopen 失败 → UI 全挂。**

**3c 末尾必须 chcon adapter shims 为 system_lib_file:s0**（[P-1] / [P-3] 联动）：

```bash
hdc shell "chcon u:object_r:system_lib_file:s0 \
    /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so \
    /system/android/lib/liboh_hwui_shim.so /system/lib/liboh_hwui_shim.so \
    /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so"
```

不 chcon 后果：hdc file send + cp 给新副本的默认 label 是 system_file:s0（继承父目录），appspawn:s0 域 dlopen 时 flock 被拒 → ART JNI_CreateJavaVM Phase 2 SIGABRT。

### 3d · AOSP framework jars + ICU + fonts.xml 双路径（11 + 1 + 1 件）

| 源 | 目标 |
|---|---|
| out/aosp_fwk/{framework, framework-classes.dex, framework-res-package, core-oj, core-libart, core-icu4j, okhttp, bouncycastle, apache-xml}.jar | /system/android/framework/ |
| out/adapter/{oh-adapter-framework, oh-adapter-runtime, adapter-mainline-stubs}.jar | /system/android/framework/ |
| out/aosp_fwk/icu/icudt72l.dat | /system/android/etc/icu/ |
| aosp_patches/data/fonts/fonts.xml | /system/android/etc/fonts.xml + cp 到 /system/etc/fonts.xml（[P-12] 双路径）|

**3d 末尾必须 chcon fonts.xml 为 system_fonts_file:s0**（[P-13]）：

```bash
hdc shell "rm -f /system/etc/fonts.xml; cp /system/android/etc/fonts.xml /system/etc/fonts.xml"
hdc shell "chcon u:object_r:system_fonts_file:s0 /system/etc/fonts.xml /system/android/etc/fonts.xml"
hdc shell "ls -lZ /system/etc/fonts.xml | grep system_fonts_file"   # 验证
```

不 chcon 后果：restorecon 默认给 system_etc_file:s0，normal_hap App 域禁读 → SystemFonts.getSystemPreinstalledFontConfig 抛 EACCES → setSystemFontMap NPE → ensureBindApplication 失败 → child 退出 fork-respawn loop。

### 3e · Boot image（27 件，md5 全验）

| 源 | 目标 |
|---|---|
| out/boot-image/{boot, boot-core-libart, boot-core-icu4j, boot-okhttp, boot-bouncycastle, boot-apache-xml, **boot-adapter-mainline-stubs**, boot-framework, boot-oh-adapter-framework}.{art,oat,vdex}（**9 组 × 3 = 27**） | /system/android/framework/arm/ |

每件推完 `md5sum 本机 vs 设备` 必须一致（防 boot-framework.art 截断复发）。

**注**：原表格写"8 组 × 3 = 24"是 B.18 之前老数据；2026-04-30 起加 boot-adapter-mainline-stubs（BCP 第 7 段，含 149 mainline APEX stubs 类），漏推会导致 ART ValidateOatFile 拒载 → fallback InitWithoutImage abort "Class mismatch for Ljava/lang/String;"。**必须 9 段全推**。

**3e 末尾必须 chcon 全部 boot 段为 system_lib_file:s0**（[P-1]）：

```bash
hdc shell "for f in /system/android/framework/arm/boot*.art /system/android/framework/arm/boot*.oat /system/android/framework/arm/boot*.vdex; do
    case \$f in
        *.b40_pre|*.orig*|*.moved*|*.pre_b11*) ;;
        *) chcon u:object_r:system_lib_file:s0 \$f 2>/dev/null;;
    esac
done"
```

不 chcon 后果：hdc file send + cp 给 boot.{art,oat,vdex} 默认 label = system_file:s0（继承父目录 /system/android/framework/arm/），appspawn:s0 域 flock() 被拒 → ART JNI_CreateJavaVM Phase 2 SIGABRT 风暴 → aa start 立刻挂。case-statement 过滤备份/legacy 副本（不需 relabel）。

### 3f · 适配层 bin + 配置 + namespace linker + SELinux label + symlinks + chmod

| 源 | 目标 | chmod |
|---|---|---:|
| out/adapter/appspawn-x | /system/bin/appspawn-x | 755 |
| out/adapter/{liboh_adapter_bridge, libapk_installer, **libinstalls**}.so | /system/lib/ | 644 |
| framework/appspawn-x/config/appspawn_x.cfg | /system/etc/init/appspawn_x.cfg | 644 |
| framework/appspawn-x/config/appspawn_x_sandbox.json | /system/etc/appspawn_x_sandbox.json | 644 |
| **ohos_patches/third_party/musl/config/ld-musl-namespace-arm.ini** | **/system/etc/ld-musl-namespace-arm.ini** | 644 |
| **out/oh-service/file_contexts** | **/system/etc/selinux/targeted/contexts/file_contexts** | 644 |

3 个 symlink：

- `/system/lib/libc_musl.so` → `/lib/ld-musl-arm.so.1`
- `/system/android/lib/libshared_libz.z.so` → `/system/lib/chipset-sdk-sp/libshared_libz.z.so`
- `/system/android/lib/libappexecfwk_common.z.so` → `/system/lib/platformsdk/libappexecfwk_common.z.so`

全量 chmod（按 `deploy/deploy_to_dayu200.sh` [8/8] 规则批量执行）。

**ld-musl-namespace-arm.ini 不推的后果**：OH musl linker 默认 namespace 不含 `/system/android/lib/` → appspawn-x dlopen libart 等 AOSP native lib 必败 → appspawn-x 起不来（appspawn-x layer 2 前置）。

**file_contexts 不推的后果**：`/system/bin/appspawn-x` 的 SELinux context 留在 `system_bin_file`，init 试图 domain transition 到 `appspawn:s0` 会被 policy 拦，execv 返回 EACCES (errno 13)，appspawn-x 根本起不来（appspawn-x layer 1 前置）。本机 file_contexts 是 ECS 编译输出（factory 618 行 + adapter 3 行 = 621 行），source diff 在 `ohos_patches/selinux_adapter/file_contexts.patch`。**不推 policy.31**（本机 vs factory -5% 大小差，替换风险高，无必要——factory policy 已含 appspawn_exec/appspawn_socket/system_lib_file 三个 type）。

**3f 末尾必须跑 restorecon**：`hdc file send` + `cp` 创建新文件时，kernel 按父目录默认打 label（不查 file_contexts）。推 file_contexts 只是"把规则文件写到设备"，要让规则真正生效到 `/system/bin/appspawn-x` 和 `/system/android/lib/*.so` 的 xattr 上，必须显式跑：
```
hdc shell "restorecon /system/bin/appspawn-x"                        # → appspawn_exec
hdc shell "find /system/android/lib -exec restorecon {} \;"          # → system_lib_file
```
**OH toybox 的 restorecon 不支持 `-R`**（"invalid args!"），必须用 `find -exec` 递归。`find` 不加 `-type` 过滤才能覆盖 dir 本身 + 普通文件 + 符号链接三种 entry（`-type f` 会漏 symlink）。

restorecon 把 xattr 持久化到 filesystem inode，reboot 不丢，无需重复。只有再次 push 覆盖文件才需再跑。

### 3.9 · 完整性 + 一致性全量校验（不可跳过）

- **完整性**：对 3b-3f 所有推送目标路径，`ls` 存在 + size 与本机相等
- **一致性**：逐个 `md5sum(本机)` == `md5sum(设备)`
- **异常检测**：`ls /system/android/lib/ /system/android/framework/arm/ | grep ^d` 必须无意外 drwx 目录（防 hdc quirk 造目录）

任一项失败 → 停手，不进 Stage 3.5。

---

## Stage 3.5 · reboot + 系统健康验证

```bash
hdc shell "sync; reboot"
# 轮询 hdc shell "echo alive" 回来
pidof foundation / render_service / com.ohos.launcher / hdcd 全部非空
hilog | grep -i 'BMS.*ready'
```

任一未过 → 停手诊断，不进 Stage 4。

---

## Stage 4 · APK 验证

```bash
hdc file send out/app/HelloWorld.apk → /data/local/tmp/HelloWorld.apk
bm install -p /data/local/tmp/HelloWorld.apk
bm dump -n com.example.helloworld   # 应显示 label
aa start -a com.example.helloworld.MainActivity -b com.example.helloworld
pidof com.example.helloworld
```

---

## 推送件数统计

| 阶段 | 文件数 | symlink |
|---|---:|---:|
| 3b OH 服务 .so | 10 | 1 (libbms) |
| 3c AOSP native .so | 41 | — |
| 3d framework jars + ICU | 12 | — |
| 3e Boot image | 24 | — |
| 3f 适配层 + cfg + linker + selinux | 7 | 3 |
| **合计** | **94** | **4** |

Stage 1 备份：**13** 件 device 原件。

---

## 违规事故索引（血泪教训）

- **2026-04-21 裸 PID kill**：停 foundation/render_service 后 `kill 1569`（原 launcher PID）→ PID 被 hdcd 回收 → hdc 失联 → 设备需重刷
- **2026-04-21 hdc 造目录**：`hdc file send` 用 forward-slash 源路径直推 `/system/etc/selinux/targeted/contexts/file_contexts.bin`（device 不存在）→ hdc quirk 造目录 → SELinux 初始化挂 → 设备启动不起来

---

## deploy_to_dayu200.sh 与本 SOP 的差异（待沉淀）

`deploy/deploy_to_dayu200.sh`（2026-04-21 从 `build/` 迁来）当前缺失 **4 件**，下次 ECS 可用时须补到 script 里：

1. `libinstalls.z.so` → /system/lib/
2. `libappspawn_client.z.so` → /system/lib/
3. `liboh_skia_rtti_shim.so` → /system/android/lib/
4. `ld-musl-namespace-arm.ini` → /system/etc/

在 script 补齐前，**以本 SOP 为真源，手动覆盖 script 的默认行为**。
