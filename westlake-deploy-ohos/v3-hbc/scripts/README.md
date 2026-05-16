Android-OpenHarmony Adapter — 产物部署路径说明
===================================================
最后更新: 2026-04-21
目标设备: DAYU200 (RK3568, 32-bit ARM, OpenHarmony 7.0.0.18)
部署脚本: deploy/deploy_to_dayu200.sh (本机 → 设备, 经 hdc)
部署来源: 必须从本机 D:\code\adapter\out\ 推送, 禁止 ECS 直推设备
          (见 CLAUDE.md "编译产物来源与部署路径不变量")
权威 SOP: agent memory `reference_deploy_sop.md` (用户 2026-04-21 审核 v4)

===================================================
out/ 子目录 → 设备路径对照表
===================================================

[1] out/adapter/  —— 适配层自有产物 (5 个)
---------------------------------------------------
appspawn-x                    → /system/bin/appspawn-x                      (755)
liboh_adapter_bridge.so       → /system/lib/liboh_adapter_bridge.so         (644)
libapk_installer.so           → /system/lib/libapk_installer.so             (644)
liboh_hwui_shim.so            → /system/android/lib/liboh_hwui_shim.so      (644)
oh-adapter-framework.jar      → /system/android/framework/oh-adapter-framework.jar (644)

[2] out/oh-service/  —— OH 系统服务补丁 .so (7 个)
---------------------------------------------------
libwms.z.so                   → /system/lib/libwms.z.so                     (644)
libappms.z.so                 → /system/lib/libappms.z.so                   (644)
libabilityms.z.so             → /system/lib/platformsdk/libabilityms.z.so   (644)
libscene_session_manager.z.so → /system/lib/platformsdk/libscene_session_manager.z.so (644)
libscene_session.z.so         → /system/lib/platformsdk/libscene_session.z.so (644)
libbms.z.so                   → /system/lib/platformsdk/libbms.z.so         (644)
librender_service_base.z.so   → /system/lib/platformsdk/librender_service_base.z.so (644)

[3] out/aosp_lib/  —— AOSP Native 交叉编译 .so (30 个, OH clang + musl)
---------------------------------------------------
*.so (全部)                    → /system/android/lib/*.so                   (644)

  当前文件清单 (按类别):
  - ART 运行时        : libart.so, libart_runtime_stubs.so, libartbase.so,
                        libartpalette.so, libartpalette-system.so,
                        libdexfile.so, libprofile.so, libelffile.so,
                        libsigchain.so, libnativebridge.so, libnativeloader.so,
                        libnativehelper.so, libvixl.so, libunwindstack.so,
                        libbionic_compat.so
  - Android Runtime   : libandroid_runtime.so, libhwui.so, libandroidfw.so
  - 基础 libs         : libbase.so, libcutils.so, libutils.so, liblog.so,
                        libziparchive.so, liblz4.so, libtinyxml2.so
  - 文本/国际化       : libminikin.so, libharfbuzz_ng.so, libft2.so,
                        libicui18n.so, libicuuc.so

[4] out/aosp_fwk/  —— AOSP Java framework jars (10 个)
---------------------------------------------------
framework.jar                 → /system/android/framework/framework.jar              (644)
framework-classes.dex.jar     → /system/android/framework/framework-classes.dex.jar  (644)
framework-res-package.jar     → /system/android/framework/framework-res-package.jar  (644)
core-oj.jar                   → /system/android/framework/core-oj.jar                (644)
core-libart.jar               → /system/android/framework/core-libart.jar            (644)
core-icu4j.jar                → /system/android/framework/core-icu4j.jar             (644)
okhttp.jar                    → /system/android/framework/okhttp.jar                 (644)
bouncycastle.jar              → /system/android/framework/bouncycastle.jar           (644)
apache-xml.jar                → /system/android/framework/apache-xml.jar             (644)
oh-adapter-framework.jar      → (该 jar 的权威来源是 out/adapter/, 不从此目录推)

[5] out/boot-image/  —— ARM32 boot image (dex2oat 预编译产物, 24 个)
---------------------------------------------------
当前包含 8 组 boot image (每组 .art/.oat/.vdex 三件套):

主合并 image (供 ART runtime 加载的入口):
boot.art / boot.oat / boot.vdex                → /system/android/framework/arm/boot.{art,oat,vdex}  (644)

各 jar 独立 image (每个 jar 编成一组):
boot-core-libart.{art,oat,vdex}                → /system/android/framework/arm/boot-core-libart.{art,oat,vdex}  (644)
boot-core-icu4j.{art,oat,vdex}                 → /system/android/framework/arm/boot-core-icu4j.{art,oat,vdex}   (644)
boot-framework.{art,oat,vdex}                  → /system/android/framework/arm/boot-framework.{art,oat,vdex}    (644)
boot-oh-adapter-framework.{art,oat,vdex}       → /system/android/framework/arm/boot-oh-adapter-framework.{art,oat,vdex} (644)
boot-okhttp.{art,oat,vdex}                     → /system/android/framework/arm/boot-okhttp.{art,oat,vdex}       (644)
boot-bouncycastle.{art,oat,vdex}               → /system/android/framework/arm/boot-bouncycastle.{art,oat,vdex} (644)
boot-apache-xml.{art,oat,vdex}                 → /system/android/framework/arm/boot-apache-xml.{art,oat,vdex}   (644)

说明: ART 按 BOOTCLASSPATH 顺序加载. boot.art 是总入口, 其余 7 组是
      每个 jar 的独立 image 文件, 必须同时部署, 缺一会导致 ImageSpace::Init
      校验失败.

[6] out/app/  —— 测试 APK (1 个)
---------------------------------------------------
OHAdapterHelloWorld.apk       → 临时: /data/local/tmp/OHAdapterHelloWorld.apk
                                  安装: bm install -p /data/local/tmp/OHAdapterHelloWorld.apk
                                  (最终由 BMS 安装到 bundle 目录, 非直接推 /system)

[7] out/host-tools/  —— Host 端工具 (不部署到设备)
---------------------------------------------------
dex2oat64                     → 仅在 ECS/本机构建阶段生成 boot image 时使用,
                                  不推送到 DAYU200 设备

[8] out/device_backup_20260413/  —— 设备端产物备份 (不部署)
---------------------------------------------------
从设备拉回的 OH 原生 .so 备份, 用于与当前 out/ 产物做 md5 / 符号对照,
排查 "设备上跑的是不是我本机推的那版". 只读, 不应该被部署.

===================================================
appspawn-x init 配置 (来源非 out/, 来自 framework/appspawn-x/config/)
===================================================
appspawn_x.cfg                → /system/etc/init/appspawn_x.cfg          (644)
appspawn_x_sandbox.json       → /system/etc/appspawn_x_sandbox.json      (644)

===================================================
设备端目录结构 (部署完成后)
===================================================
/system/
├── bin/
│   └── appspawn-x                              <- 混合应用孵化器
├── lib/                                        <- OH 原生 lib 目录
│   ├── liboh_adapter_bridge.so                 <- 适配层 JNI 桥
│   ├── libapk_installer.so                     <- APK 安装器
│   ├── libwms.z.so                             <- OH WMS (打过适配补丁)
│   ├── libappms.z.so                           <- OH AppMgr (打过适配补丁)
│   ├── libc_musl.so -> /lib/ld-musl-arm.so.1   <- libsigchain dlopen 所需软链接
│   └── platformsdk/
│       ├── libabilityms.z.so
│       ├── libscene_session_manager.z.so
│       ├── libscene_session.z.so
│       ├── libbms.z.so
│       ├── librender_service_base.z.so
│       └── libskia_canvaskit.z.so              <- 30-50MB, 可选
├── android/                                    <- Android 运行时隔离目录
│   ├── lib/
│   │   ├── libart.so, libhwui.so, ... (全部 30 个 aosp_lib .so)
│   │   ├── liboh_hwui_shim.so                  <- hwui → OH RS 桥接
│   │   ├── libart_runtime_stubs.so             <- ART runtime 桩 (在 aosp_lib/)
│   │   └── libbionic_compat.so                 <- bionic-musl 兼容层 (在 aosp_lib/)
│   └── framework/
│       ├── framework.jar, core-oj.jar, ... (全部 Java framework jars)
│       ├── oh-adapter-framework.jar            <- 适配层 Java 类
│       └── arm/
│           ├── boot.art / boot.oat / boot.vdex             <- 总入口
│           ├── boot-core-libart.{art,oat,vdex}             <- 各 jar 独立 image
│           ├── boot-core-icu4j.{art,oat,vdex}
│           ├── boot-framework.{art,oat,vdex}
│           ├── boot-oh-adapter-framework.{art,oat,vdex}
│           ├── boot-okhttp.{art,oat,vdex}
│           ├── boot-bouncycastle.{art,oat,vdex}
│           └── boot-apache-xml.{art,oat,vdex}
└── etc/
    ├── init/appspawn_x.cfg                     <- OH init 服务配置
    └── appspawn_x_sandbox.json                 <- Android 应用沙箱挂载规则

===================================================
部署前置条件
===================================================
1. ECS 编译完成, 产物已 scp 到本机 out/ 对应子目录
   (bash build/pull_ecs_artifacts.sh)
2. DAYU200 通过 USB 连接, hdc list targets 可见
3. 设备 /system 需可写: mount -o remount,rw /system

===================================================
常用命令
===================================================
全量部署:       bash deploy/deploy_to_dayu200.sh
仅部署 APK:     bash deploy/deploy_to_dayu200.sh --apk-only
跳过大 libskia: bash deploy/deploy_to_dayu200.sh --skip-libskia
试运行 (不推):  bash deploy/deploy_to_dayu200.sh --dry-run
卸载:           bash deploy/deploy_to_dayu200.sh --uninstall

部署后:
  hdc shell reboot
  hdc shell am start -n com.example.helloworld/.MainActivity
  hdc shell logcat | grep -E 'OH_AMAdapter|OH_WMAdapter|HelloWorld'

===================================================
废弃目录 (已清理, 勿重建)
===================================================
out/deploy_package/  —— 已废弃 (2026-04-14), 部署脚本直接从各规范子目录读取
out/libart-full/     —— 已废弃 (2026-04-14), libart.so 唯一来源是 out/aosp_lib/libart.so
out/native-arm32/    —— 已废弃, AOSP native .so 只放 out/aosp_lib/
