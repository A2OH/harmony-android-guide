Android-OpenHarmony Adapter Project
====================================

源 (生成源/权威源)：本=本地 D:\code\adapter\，ECS=oh-build:~/adapter/
同步：→ 单向，↔ 双向，- 不同步
GH：✓ 进 GitHub，✗ 不进 GitHub

adapter/
  ├── CLAUDE.md                # 项目说明 + 工作流约定         [本] 本→ECS        ✓
  ├── readme.txt               # 本文件                         [本] 本→ECS        ✓
  ├── build.sh                 # 顶层构建入口                   [ECS] ECS→本      ✓
  ├── bundle.json              # OH GN bundle 配置              [ECS] ECS→本      ✓
  │
  ├── framework/               # Adapter 自有 Java + JNI 源码   [本] 本→ECS       ✓
  │   ├── core/                #   核心：OHEnvironment 等
  │   ├── activity/            #   AMS / ATMS 适配
  │   ├── window/              #   WMS / WindowSession 适配
  │   ├── surface/             #   Surface / RSSurfaceNode 桥接
  │   ├── broadcast/           #   广播 → CommonEvent
  │   ├── contentprovider/     #   ContentProvider → DataShare
  │   ├── package-manager/     #   PackageManager / BMS / APK 安装
  │   ├── appspawn-x/          #   混合进程孵化器
  │   ├── hwui-shim/           #   Android NDK → OH 桥接 (liboh_hwui_shim, 含 GrAHB shim)
  │   ├── android-runtime/     #   liboh_android_runtime.so — 渐进替代 libandroid_runtime (2026-04-17 新)
  │   └── jni/                 #   liboh_adapter_bridge JNI 入口
  │
  ├── aosp_patches/            # AOSP 14 源码补丁 (.patch)      [本] 本→ECS       ✓
  │   └── frameworks/          #   ActivityManager / WindowManagerGlobal 等
  │
  ├── ohos_patches/            # OpenHarmony 源码补丁           [本] 本→ECS       ✓
  │   ├── ability_rt/          #   AMS / AppMgr / Mission
  │   ├── bundle_framework/    #   BMS / installd
  │   └── (其它 OH 子模块)
  │
  ├── app/                     # Hello World 测试 APK 源码      [本] 本→ECS       ✗
  │   ├── java/com/example/helloworld/
  │   ├── res/
  │   └── AndroidManifest.xml
  │
  ├── doc/                     # 设计文档 (HTML, 中文)          [本主] 本→ECS    ✓
  │   ├── overall_design.html
  │   ├── compile_report.html
  │   ├── build_patch_log.html
  │   ├── helloworld_gap_analysis.html
  │   ├── hwui_runtime_analysis.html
  │   ├── graphics_rendering_design.html
  │   ├── (其它设计文档, 见 doc/readme.txt)
  │   └── readme.txt
  │
  ├── build/                   # 编译脚本 + Skia 兼容头文件     [ECS主] ECS→本   ✓
  │   ├── *.sh / *.py          #   编译调用脚本 (改动频繁)
  │   ├── compile_libhwui.sh   #   libhwui Phase 1+2 编译
  │   ├── compile_libhwui_jni.sh  # libhwui Phase 3 JNI 编译
  │   ├── link_libhwui.sh      #   libhwui.so 链接脚本
  │   ├── hwui_phase{2,3}_*.py #   迭代补丁脚本
  │   ├── skia_rebuild/        #   OH Skia 全量重编脚本
  │   ├── skia_compat_headers/ #   Skia M116→M133 兼容 shim 头
  │   ├── cross_compile_*.sh   #   AOSP Native 库交叉编译
  │   └── (其它编译脚本)
  │
  ├── out/                     # 编译产物 (仅 ECS, 不进 GitHub) [ECS]ECS→本           ✗
  │   ├── adapter/             #   适配层产物 + shim (appspawn-x, liboh_adapter_bridge, liboh_hwui_shim, liboh_stubs, libbionic_compat)
  │   ├── aosp_lib/            #   AOSP Native .so (incl. libhwui, 27 个)
  │   ├── aosp_fwk/            #   10 个 AOSP framework jar (80 MB)
  │   ├── oh-service/          #   7 个 OH 服务补丁 .so
  │   ├── host-tools/          #   host dex2oat64
  │   └── boot-image/          #   boot.art / boot.oat / boot.vdex
  │
  ├── prebuilts/               # OH/AOSP 工具链快捷方式         [ECS] -           ✗
  │
  ├── bkup/                    # 本地手工备份草稿               [本] 仅本地       ✗
  │
  └── (其它临时文件)            # cmd.txt / push_cmd.txt /    [本主] 本→ECS ✗
                              #   OH_Git.txt / fix_and_build.py [本] 仅本地       ✗

==================================================================
同步规则
==================================================================

1. 改源代码 / 写文档 → 本地编辑 → scp 推到 ECS
   scp -r framework/    oh-build:~/adapter/
   scp -r aosp_patches/ oh-build:~/adapter/
   scp -r ohos_patches/ oh-build:~/adapter/
   scp -r doc/          oh-build:~/adapter/

2. 改编译脚本 → ECS 直接改并验证 → scp 同步回本地备份
   scp -r oh-build:~/adapter/build/ D:/code/adapter/

3. 看编译产物 → 仅 ECS ~/adapter/out/，本地无 out/
   ssh oh-build "ls ~/adapter/out/aosp_lib/"

4. 推到 GitHub → 仅以下 6 目录 + 2 文件:
   .claude/  aosp_patches/  build/  doc/  framework/  ohos_patches/
   build.sh  CLAUDE.md  readme.txt
   排除: app/  prebuilts/  bundle.json  out/  bkup/  *.txt(临时)

==================================================================
AOSP / OH 源码补丁工作流
==================================================================

  所有对 AOSP / OH 源码的修改必须通过 patch 文件管理：

  1. patch 文件本地编写，放入：
       D:\code\adapter\aosp_patches\   (镜像 AOSP 源码树结构)
       D:\code\adapter\ohos_patches\   (镜像 OH   源码树结构)

  2. scp 同步到 ECS：
       scp -r aosp_patches/ ohos_patches/ oh-build:~/adapter/

  3. ECS 上应用 patch 到原文件 (~/aosp/ 或 ~/oh/)：
       - 应用前先备份原文件（cp file file.bak，若 .bak 不存在）
       - 把 patch 应用到原文件 (in-place)
       - **编译后不回退**，原文件保持已打补丁状态，便于增量编译

  4. 每次编译后追加记录到 doc/build_patch_log.html

  详见 CLAUDE.md 中的 "Build Isolation Rule" 一节。
