# Westlake 引擎 — 状态报告

**日期:** 2026-03-25
**状态:** Jetpack Compose + 多应用平台在华为 Mate 20 Pro 上运行

---

## 概要

Westlake 引擎现在在同一台手机上托管**多个应用**，使用真实 Android View，由自定义 Activity 管理器和兼容层驱动。三个应用在同一引擎实例中运行：

1. **MockDonalds** — 完整点餐应用（菜单、详情、购物车、结账、确认）
2. **拨号器** — 带键盘、通话记录、联系人、通话界面的电话拨号器（7 个界面）
3. **计算器** — 通过 XML 布局填充的功能计算器

所有应用使用原生 `LinearLayout`、`ListView`、`Button` 和 `TextView` — 而非自定义 Canvas 绘制 — 并支持完整触摸导航。

该引擎已在三个平台上验证：

| 平台 | 运行时 | FPS | 状态 |
|------|--------|-----|------|
| x86_64 主机 (Linux) | ART (AOT) | 60 | 稳定，主要开发目标 |
| ARM64 手机端 | dalvikvm (解释器) | 120 | 在 Mate 20 Pro 上原生运行 |
| 华为 Mate 20 Pro | Android 10 原生 ART | 原生 | 完整触摸，真实 View，3 个应用 |

---

## 架构

### 完整管线（文本图）

```
+------------------------------------------------------------------+
|                    Android 手机 (Mate 20 Pro)                     |
|                                                                   |
|  +-----------------------------+   +---------------------------+  |
|  |     WestlakeActivity.java   |   |    liboh_bridge.so        |  |
|  |  (继承 android.app.Activity)|   |  (~25 个 Canvas/Paint/Path |  |
|  |  - 子级优先 DexClassLoader  |   |   JNI 方法桥接)           |  |
|  |  - 运行时加载 app.dex       |   +---------------------------+  |
|  +-------------|---------------+               |                  |
|                |                               |                  |
|  +-------------|-------------------------------|---------+        |
|  |             v            App DEX 空间                 |        |
|  |                                                       |        |
|  |  +----------------+   +----------------------------+  |        |
|  |  | MockDonaldsApp |   | MiniServer                 |  |        |
|  |  | (入口点)       |-->| - MiniActivityManager      |  |        |
|  |  +----------------+   | - MiniWindowManager        |  |        |
|  |                       | - Activity 生命周期         |  |        |
|  |                       +------|---------------------+  |        |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           Activity 栈                              ||       |
|  |  |  MenuActivity -> ItemDetailActivity -> CartActivity||       |
|  |  |  (真实 LinearLayout, ListView, Button, TextView)   ||       |
|  |  +---------------------------------------------------+|       |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           View 树（原生 Android View）              ||       |
|  |  |  LinearLayout                                      ||       |
|  |  |  +-- TextView ("MockDonalds Menu")                 ||       |
|  |  |  +-- ListView (8 个菜单项，通过 BaseAdapter)       ||       |
|  |  |  +-- Button ("查看购物车 (2)")                     ||       |
|  |  +---------------------------------------------------+|       |
|  +-------------------------------------------------------+        |
|                              |                                    |
|                              v                                    |
|  +-----------------------------------------------------------+   |
|  |  ShimCompat（基于反射的框架兼容性）                         |   |
|  |  - 通过反射桥接框架版本差异                                 |   |
|  |  - 处理 Context、Resources、PackageManager 存根             |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  OHBridge JNI 层（170 个已注册方法）                        |   |
|  |  - Canvas: drawText, drawRect, drawLine, drawBitmap, ...   |   |
|  |  - Paint: setColor, setTextSize, measureText, ...          |   |
|  |  - Path: moveTo, lineTo, quadTo, close, ...               |   |
|  |  - Surface: createSurface, lockCanvas, unlockAndPost       |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  Android 框架（手机上的原生 ART）                           |   |
|  |  Canvas, Paint, FontMetrics, Path（真实实现）               |   |
|  +-----------------------------------------------------------+   |
+------------------------------------------------------------------+
```

### ClassLoader 层次结构

```
BootClassLoader (Android 框架)
  |
  +-- PathClassLoader (WestlakeActivity — 宿主应用)
        |
        +-- 子级优先 DexClassLoader (app.dex)
              |
              +-- MockDonalds 类
              +-- MiniServer, MiniActivityManager
              +-- ShimCompat, OHBridge
              +-- Shim 类 (android.widget.*, android.view.*)
              |
              (父级委托：仅用于 java.*, android.app.Activity)
```

子级优先类加载器至关重要：它确保加载我们的 shim `android.widget.ListView`（与 `MiniActivityManager` 配合使用），而不是框架的真实版本；同时仍将 `android.app.Activity` 委托给真实框架，以便 `WestlakeActivity` 可以继承它。

---

## 已测试内容

### 组件

| 组件 | 描述 | 状态 |
|------|------|------|
| MiniServer | 轻量级应用服务器，替代 Android 的 `ActivityManagerService` | 已测试 |
| MiniActivityManager | Activity 生命周期：create、start、resume、pause、stop、destroy、restart | 已测试 |
| MiniWindowManager | View 树管理，measure/layout/draw 循环 | 已测试 |
| OHBridge JNI | 170 个已注册 JNI 方法，用于 Canvas/Paint/Path/Surface/Prefs/RDB | 已测试 |
| 子级优先 DexClassLoader | 加载 app.dex，shim 类覆盖框架类 | 已测试 |
| ShimCompat | 基于反射的框架版本差异兼容层 | 已测试 |
| 原生 View | LinearLayout、ListView、Button、TextView、FrameLayout、RelativeLayout | 已测试 |
| 触摸输入 | DOWN/UP 事件路由到 View 树，点击处理器触发 | 已测试 |
| Activity 导航 | Menu -> ItemDetail -> Cart -> Checkout，支持返回栈 | 已测试 |
| 购物车持久化 | 购物车计数器在 Activity 导航间持久化 | 已测试 |
| Intent + extras | String、int、double、boolean extras 在 Activity 间传递 | 已测试 |
| BaseAdapter + ListView | 动态列表填充，View 回收 | 已测试 |
| 多应用托管 | 单 DEX 中多个应用，通过按钮切换 | 已测试 |
| GradientDrawable | 圆角矩形、圆形、描边，用于 Material Design UI | 已测试 |
| 字母分组 | ListView 中的分组标题（联系人 A-Z） | 已测试 |
| 通话计时器 | 通话界面通过后台线程实时更新计时器 | 已测试 |

### 引擎上运行的应用

| 应用 | 界面数 | 功能 |
|------|--------|------|
| MockDonalds | 5 | 分类菜单、商品详情、购物车、结账、订单确认 |
| 拨号器 | 7 | T9 键盘、通话记录（来电/去电/未接）、带头像的联系人、联系人详情、添加联系人、带计时器的通话界面、通话中 DTMF 键盘、语音信箱 |
| 计算器 | 1 | 从 AXML 填充的 XML 布局，功能算术运算 |

### 端到端流程（手机）

1. WestlakeActivity 启动，为 `app.dex` 创建子级优先 DexClassLoader
2. MockDonaldsApp.main() 运行，初始化 MiniServer
3. MiniActivityManager 启动 MenuActivity（ListView 中 8 个菜单项）
4. 用户点击菜单项 -> ItemDetailActivity 显示商品详情
5. 用户点击"加入购物车" -> CartActivity 显示购物车内容
6. 用户点击"返回" -> 返回 MenuActivity，购物车计数器已更新
7. 完整触摸导航循环已在物理设备上验证

---

## 平台耦合度

Westlake 引擎设计为最小平台耦合。只有两个组件是平台特定的：

### 平台特定（每个目标平台需更改）

| 组件 | 规模 | 描述 |
|------|------|------|
| `liboh_bridge.so` | ~25 个方法 | 到平台 Canvas/Paint/Path 的 JNI 桥接 |
| `WestlakeActivity.java` | ~150 行 | 带子级优先类加载器的宿主 Activity |

### liboh_bridge.so API 接口

原生桥接仅依赖 4 个 Android 类，共约 25 个方法：

```
android.graphics.Canvas
  - drawText(String, float, float, Paint)
  - drawRect(float, float, float, float, Paint)
  - drawLine(float, float, float, float, Paint)
  - drawBitmap(Bitmap, float, float, Paint)
  - drawCircle(float, float, float, Paint)
  - drawRoundRect(RectF, float, float, Paint)
  - clipRect(float, float, float, float)
  - save(), restore(), translate(float, float)
  - getWidth(), getHeight()

android.graphics.Paint
  - setColor(int), setTextSize(float), setStyle(Style)
  - setAntiAlias(boolean), setStrokeWidth(float)
  - measureText(String)
  - getFontMetrics(FontMetrics)

android.graphics.Paint.FontMetrics
  - ascent, descent, top, bottom (float 字段)

android.graphics.Path
  - moveTo(float, float), lineTo(float, float)
  - quadTo(float, float, float, float)
  - close(), reset()
```

这是**唯一**需要按平台移植的原生代码。在 OHOS 上，这 25 个方法将调用 ArkUI/Skia 等效实现。在任何具有 2D Canvas API 的平台上（如 SDL、Cairo、HTML5 Canvas），同样只需重新实现这 25 个方法。

### 平台无关（在所有平台保持不变）

| 组件 | 描述 |
|------|------|
| MiniServer | 应用服务器，Activity 生命周期管理 |
| MiniActivityManager | Activity 栈，生命周期状态机 |
| MiniWindowManager | View 树 measure/layout/draw |
| 所有 View 类 | LinearLayout、ListView、Button、TextView 等 |
| ShimCompat | 基于反射的兼容层 |
| OHBridge Java 端 | 170 个 JNI 方法声明（自动生成） |
| MockDonalds 应用 | 所有应用代码 |
| Intent、Bundle、extras | Activity 间通信 |
| BaseAdapter、ArrayAdapter | 列表数据绑定 |
| Canvas/Paint/Path Java API | 绘制 API（Java 端不变） |

总计，**约 150 行 Java + 约 25 个 C 函数**是平台特定的。其他所有内容（数千行 Java）都是平台无关的，在任何目标平台上无需修改即可运行。

---

## 测试结果

### x86_64 主机（ART，AOT 编译启动镜像）

```
[MockDonaldsApp] Starting on OHOS + ART ...
[OHBridge x86] 169 methods
[MockDonaldsApp] OHBridge native: LOADED
[MockDonaldsApp] arkuiInit() = 0
[MockDonaldsApp] MiniServer initialized
[D] MiniActivityManager: startActivity: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performCreate: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performStart: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performResume: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] MenuActivity launched: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] Creating surface 480x800
[MockDonaldsApp] Initial frame rendered
[MockDonaldsApp] Entering event loop...
[MockDonaldsApp] Frame 600 activity=MenuActivity
```

- **性能:** 稳定 ~60 fps
- **启动:** <1 秒（AOT 启动镜像）
- **MockDonalds 14 项测试全部通过**

### ARM64 dalvikvm（手机端）

```
[MockDonaldsApp] Starting via dalvikvm ...
[OHBridge arm64] 170 methods registered
[D] MiniActivityManager: startActivity: MenuActivity
[D] MiniActivityManager:   performCreate, performStart, performResume
[MockDonaldsApp] MenuActivity launched
[MockDonaldsApp] Frame rendered at 120fps
```

- **性能:** ~120 fps（解释器，轻量绘制）
- **运行时:** dalvikvm（KitKat 可移植解释器，64 位补丁版）

### 华为 Mate 20 Pro（原生 Android 10）

- **设备:** 华为 Mate 20 Pro (LYA-L29)
- **系统:** Android 10 (EMUI)
- **运行时:** 原生 ART
- **View:** 真实 Android LinearLayout、ListView、Button、TextView
- **触摸:** 完整触摸导航正常工作
- **导航流程:**
  1. 菜单界面（ListView 中 8 个项目）-- 点击项目
  2. 商品详情界面（名称、价格、描述）-- 点击"加入购物车"
  3. 购物车界面（商品列表、总价）-- 点击"返回"
  4. 菜单界面（按钮文本中购物车计数器已更新）
- **购物车计数器:** 在所有导航转换中持久化

---

## 里程碑：XML 布局填充（2026-03-24）

二进制 XML（AXML）布局填充已在手机上正常工作。引擎可以解析编译后的 Android XML 布局，并从解析事件中创建真实的 Android View，绕过手机的 `LayoutInflater`（它需要内部的 `XmlBlock.Parser`）。

### 组件

| 组件 | 描述 |
|------|------|
| `BinaryXmlParser` | 解析编译后的 AXML 格式（字符串池、资源 ID、XML 树） |
| `XmlTestHelper.inflateFromParser()` | 遍历 XML 事件，通过 `createViewForTag()` 创建 View |
| `XmlTestHelper.applyAttributes()` | 处理 layout_width/height/weight、text、textSize、textColor、orientation、padding、background、gravity |
| `XmlTestHelper.loadCalculatorApp()` | 加载独立 DEX + 填充 XML 布局 + 绑定按钮处理器 |

### 已测试 APK 布局

| APK | 结果 |
|-----|------|
| 自建计算器（我们的 DEX + XML） | 完全可用：布局填充、按钮绑定、算术运算正常 |
| 华为联系人拨号盘（dialpad_huawei.xml） | 根 LinearLayout 已填充，华为自定义 View 类被跳过 |

### 尺寸处理

AXML `decodeDimension()` 返回的 dp 值标记为 "px"。填充器乘以显示密度（如 Mate 20 Pro 上为 3.0x）以获得正确尺寸。

---

## 已知问题

| 问题 | 严重性 | 描述 | 临时解决方案 |
|------|--------|------|-------------|
| 无 SQLite | 中 | 手机路径上 SQLiteDatabase 不可用 | 内存数据结构，应用重启后不保留 |
| 无 SharedPreferences | 中 | 手机路径上未实现 SharedPreferences | 购物车状态保存在内存中，应用被杀后丢失 |
| APK 中的自定义 View 类 | 中 | 第三方 APK 使用不在 shim 层中的自定义 View 子类 | 需要将 APK 的 classes.dex 与布局 XML 一起加载 |
| 无 resources.arsc（手机端） | 低 | 资源字符串查找未接入 | Java 代码中硬编码字符串 |
| 无 Bitmap 加载 | 低 | 未实现 BitmapFactory.decodeResource() | 纯文本 UI，无图片 |
| 无动画 | 低 | 未实现 View 动画框架 | Activity 间静态转换 |

### 后续计划

1. **OHOS 移植** — 在 OpenHarmony 上针对 ArkUI/Skia 重新实现 `liboh_bridge.so`（约 25 个函数）
2. **完整 APK 加载** — 将 APK 的 classes.dex + XML 布局 + resources.arsc 一起加载
3. **SQLite** — 通过 JNI 将 `android.database.sqlite` 移植到原生 SQLite 库
4. **SharedPreferences** — 实现基于文件的 XML 存储

---

## 文件位置

| 内容 | 路径 |
|------|------|
| WestlakeActivity | `westlake-host/src/com/westlake/host/WestlakeActivity.java` |
| liboh_bridge.so（手机端） | `westlake-host/jni/ohbridge_native.c` |
| ShimCompat | `shim/java/android/app/ShimCompat.java` |
| MiniServer | `shim/java/android/app/MiniServer.java` |
| MiniActivityManager | `shim/java/android/app/MiniActivityManager.java` |
| MockDonalds 应用 | `test-apps/04-mockdonalds/src/com/example/mockdonalds/` |
| MockApp（Material UI） | `test-apps/04-mockdonalds/src/com/example/mockdonalds/MockApp.java` |
| XmlTestHelper | `test-apps/04-mockdonalds/src/com/example/mockdonalds/XmlTestHelper.java` |
| BinaryXmlParser | `shim/java/android/content/res/BinaryXmlParser.java` |
| OHBridge Java | `shim/java/com/ohos/shim/bridge/OHBridge.java` |
| x86_64 dalvikvm | `art-universal-build/build/bin/dalvikvm` |
| x86_64 OHBridge 存根 | `art-universal-build/stubs/ohbridge_stub.c` |
| 软件渲染器 | `art-universal-build/stubs/ohbridge_render.c` |
| ARM64 dalvikvm | `art-universal-build/build-ohos-arm64/bin/dalvikvm` |
