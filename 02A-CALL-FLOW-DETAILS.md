# Android-as-Engine: Exhaustive Call Flow Details

This document provides detailed call flow diagrams for every major operation
in the Android-as-Engine architecture. All diagrams use ASCII art and are
designed to render correctly in a monospace terminal at 120 columns width.

---

## 1. App Launch Flow (APK -> Running Activity)

Every step from `ActivityThread.main("hello.apk")` to `Activity.onCreate()`:

```
ActivityThread.main("hello.apk")
  |
  +--1--> ZipFile.open("hello.apk")
  |       +-- Read AndroidManifest.xml (binary AXML)
  |       +-- Read classes.dex
  |       +-- Read resources.arsc
  |
  +--2--> BinaryXmlParser.parse(manifestBytes)
  |       +-- Parse string pool (UTF-8/UTF-16)
  |       +-- Extract package="com.example.hello"
  |       +-- Extract <activity> declarations
  |       +-- Find intent-filter: action=MAIN, category=LAUNCHER
  |
  +--3--> ResourceTableParser.parse(resourcesBytes, resources)
  |       +-- Parse global string pool
  |       +-- Parse package chunk (id=0x7f)
  |       +-- For each TypeSpec + Type chunk:
  |       |     Map resource IDs --> string/color/int values
  |       +-- Populate Resources registry
  |
  +--4--> MiniServer.init("com.example.hello")
  |       +-- Create MiniActivityManager
  |       +-- Create MiniPackageManager (register activities)
  |       +-- Create MiniServiceManager
  |       +-- Create MiniContentResolver
  |
  +--5--> DexClassLoader.loadDex("classes.dex")
  |       +-- Dalvik VM loads DEX bytecode
  |
  +--6--> Application.onCreate()
  |       +-- App-specific init (if Application subclass declared)
  |
  +--7--> MiniActivityManager.startActivity(launcherIntent)
          +-- Resolve: ComponentName --> HelloActivity.class
          +-- Instantiate via reflection: Class.forName().newInstance()
          +-- activity.attach(context, intent, window)
          +-- activity.onCreate(null)          <-- APP CODE RUNS
          +-- activity.onStart()
          +-- activity.onResume()              <-- Activity is now visible
```

### Detailed Breakdown: Steps 1-3 (APK Parsing)

```
  +-----------------------------------------------------------------------+
  |                         hello.apk (ZIP archive)                       |
  |                                                                       |
  |  +---------------------+  +----------------+  +-------------------+   |
  |  | AndroidManifest.xml |  | classes.dex    |  | resources.arsc    |   |
  |  | (binary AXML)       |  | (DEX bytecode) |  | (compiled res)   |   |
  |  | ~2-10 KB            |  | ~10KB - 50MB   |  | ~1KB - 5MB       |   |
  |  +----------+----------+  +-------+--------+  +---------+---------+   |
  +-------------|----------------------|----------------------|-----------+
                |                      |                      |
                v                      v                      v
  +---------------------------+  +------------+  +------------------------+
  | BinaryXmlParser           |  | Dalvik VM  |  | ResourceTableParser    |
  | - string pool             |  | - DEX load |  | - global string pool   |
  | - namespace decls         |  | - verify   |  | - type string pool     |
  | - tag open/close          |  | - optimize |  | - key string pool      |
  | - attribute values        |  +------+-----+  | - type chunks          |
  +------------+--------------+         |         +----------+-------------+
               |                        |                    |
               v                        v                    v
  +---------------------------+  +------------+  +------------------------+
  | ApkInfo                   |  | Loaded     |  | Resources              |
  | .packageName              |  | Classes    |  | .getString(0x7f0300XX) |
  | .activities[]             |  | (heap)     |  | .getColor(0x7f0400XX)  |
  | .services[]               |  |            |  | .getInt(0x7f0500XX)    |
  | .launcherActivity         |  +------------+  +------------------------+
  +---------------------------+
```

### Detailed Breakdown: Steps 4-7 (Mini Framework Init)

```
  MiniServer.init("com.example.hello")
    |
    |   +------------------------------------------------------------+
    |   |                    MiniServer (singleton)                   |
    |   |                                                            |
    |   |  +---------------------+  +---------------------------+   |
    |   |  | MiniActivityManager |  | MiniPackageManager        |   |
    |   |  | - activityStack     |  | - registeredActivities[]  |   |
    |   |  | - resumedActivity   |  | - registeredServices[]    |   |
    |   |  +---------------------+  | - registeredProviders[]   |   |
    |   |                           +---------------------------+   |
    |   |  +---------------------+  +---------------------------+   |
    |   |  | MiniServiceManager  |  | MiniContentResolver       |   |
    |   |  | - runningServices{} |  | - providerMap{}           |   |
    |   |  +---------------------+  +---------------------------+   |
    |   +------------------------------------------------------------+
    |
    +--> DexClassLoader.loadDex("classes.dex")
    |
    |    Dalvik VM                    App Classes
    |    +------------------+         +-----------------------------+
    |    | dexopt            | ------> | com.example.hello           |
    |    | verify bytecode   |         |   HelloActivity.class       |
    |    | resolve classes   |         |   HelloApplication.class    |
    |    | link methods      |         |   R.class (resource IDs)    |
    |    +------------------+         +-----------------------------+
    |
    +--> Application.onCreate()
    |      (app global state init)
    |
    +--> MiniActivityManager.startActivity(launcherIntent)
           |
           |  1. Resolve target class
           |     MiniPackageManager.resolveActivity(intent)
           |       +-- Match: action=MAIN, category=LAUNCHER
           |       +-- Result: "com.example.hello.HelloActivity"
           |
           |  2. Instantiate
           |     Class cls = Class.forName("com.example.hello.HelloActivity")
           |     HelloActivity activity = (HelloActivity) cls.newInstance()
           |
           |  3. Attach context
           |     activity.attach(
           |       context,       // ApplicationContext with package info
           |       intent,        // The MAIN/LAUNCHER intent
           |       window,        // PhoneWindow (holds DecorView)
           |       resources      // Parsed resources.arsc
           |     )
           |
           |  4. Lifecycle callbacks
           |     activity.onCreate(null)    <-- savedInstanceState = null (fresh launch)
           |     activity.onStart()         <-- becoming visible
           |     activity.onResume()        <-- now in foreground
           |
           |  5. Push onto stack
           |     activityStack.push(activity)
           |     resumedActivity = activity
```

---

## 2. Rendering Flow (View Tree -> Pixels)

The complete render cycle from invalidate() to display:

```
View.invalidate()
  |
  +--1--> Walk up to root View
  |       +-- root.getTag() --> Activity reference
  |
  +--2--> Activity.renderFrame()
  |       +-- surfaceCtx = mSurfaceCtx
  |       +-- canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx)
  |       +-- canvas = new Canvas(canvasHandle, width, height)
  |       +-- canvas.drawColor(0xFFFFFFFF)     // clear
  |       +-- decorView.draw(canvas)           // start traversal
  |
  +--3--> ViewGroup.draw(canvas)  [decorView = LinearLayout]
  |       +-- drawBackground(canvas)
  |       |     +-- canvas.drawColor(bgColor)
  |       |           +-- JNI --> OH_Drawing_CanvasDrawColor()
  |       +-- onDraw(canvas)
  |       +-- dispatchDraw(canvas)
  |             +-- for each child:
  |             |     canvas.save()
  |             |       +-- JNI --> OH_Drawing_CanvasSave()
  |             |     canvas.translate(child.left, child.top)
  |             |       +-- JNI --> OH_Drawing_CanvasTranslate()
  |             |     canvas.clipRect(0, 0, child.width, child.height)
  |             |       +-- JNI --> OH_Drawing_CanvasClipRect()
  |             |     child.draw(canvas)       // RECURSIVE
  |             |     canvas.restore()
  |             |       +-- JNI --> OH_Drawing_CanvasRestore()
  |             +-- end for
  |
  +--4--> TextView.draw(canvas)  [child]
  |       +-- onDraw(canvas)
  |       |     +-- Paint paint = new Paint()
  |       |     |     paint.setColor(textColor)
  |       |     |     paint.setTextSize(size)
  |       |     +-- FontMetrics fm = paint.getFontMetrics()
  |       |     +-- x = paddingLeft
  |       |     +-- y = paddingTop + (-fm.ascent)
  |       |     +-- canvas.drawText(mText, x, y, paint)
  |       |           +-- OHBridge.penCreate() / brushCreate()
  |       |           +-- OHBridge.penSetColor(handle, color)
  |       |           +-- OHBridge.fontCreate()
  |       |           +-- OHBridge.fontSetSize(handle, size)
  |       |           +-- OHBridge.canvasDrawText(canvas, text, x, y, font, pen, brush)
  |       |                 +-- OH_Drawing_CanvasDrawTextBlob(...)
  |       |                       +-- Skia --> GPU --> pixels in NativeWindow buffer
  |       +-- (no children)
  |
  +--5--> OHBridge.surfaceFlush(surfaceCtx)
          +-- OH_NativeWindow_NativeWindowFlushBuffer()
                +-- OH compositor --> display
```

### View Tree Layout Phase (Precedes Drawing)

```
  Activity.setContentView(R.layout.activity_main)
    |
    +-- LayoutInflater.inflate(R.layout.activity_main, decorView)
    |     +-- Parse XML layout (binary AXML from APK)
    |     +-- For each <tag>:
    |     |     cls = Class.forName("android.widget." + tagName)
    |     |     view = cls.newInstance()
    |     |     Apply XML attributes (layoutParams, padding, text, etc.)
    |     +-- Return inflated View tree
    |
    +-- Measure pass: decorView.measure(widthSpec, heightSpec)
    |     |
    |     |   +------- decorView (LinearLayout, MATCH_PARENT x MATCH_PARENT) -------+
    |     |   |                                                                      |
    |     |   |  onMeasure(480|EXACTLY, 800|EXACTLY)                                 |
    |     |   |    |                                                                 |
    |     |   |    +-- child[0].measure(480|EXACTLY, WRAP_CONTENT)  // TextView      |
    |     |   |    |     +-- onMeasure: text="Hello" --> height = textSize + padding  |
    |     |   |    |     +-- setMeasuredDimension(480, 48)                            |
    |     |   |    |                                                                 |
    |     |   |    +-- child[1].measure(480|EXACTLY, WRAP_CONTENT)  // Button        |
    |     |   |    |     +-- onMeasure: text="Click Me" --> height = 56              |
    |     |   |    |     +-- setMeasuredDimension(480, 56)                            |
    |     |   |    |                                                                 |
    |     |   |    +-- setMeasuredDimension(480, 800)                                 |
    |     |   +----------------------------------------------------------------------+
    |
    +-- Layout pass: decorView.layout(0, 0, 480, 800)
    |     |
    |     |   +------- decorView (0,0)-(480,800) --------------+
    |     |   |                                                 |
    |     |   |  onLayout(true, 0, 0, 480, 800)                |
    |     |   |    |                                            |
    |     |   |    +-- child[0].layout(0, 0, 480, 48)           |
    |     |   |    |   (TextView at top, full width)            |
    |     |   |    |                                            |
    |     |   |    +-- child[1].layout(0, 48, 480, 104)         |
    |     |   |    |   (Button below TextView)                  |
    |     |   |    |                                            |
    |     |   +---------------------------------------------+  |
    |     |                                                     |
    |
    +-- Draw pass: (see rendering flow above)
```

### JNI Call Sequence for a Single Frame

```
  Frame N rendering (60 FPS target = 16.6ms budget)
  =================================================

  Java (Dalvik VM)                    C (liboh_bridge.so)              OHOS Native
  ---------------------------------   ----------------------------     ------------------
  OHBridge.surfaceGetCanvas(ctx)  --> oh_bridge_surface_get_canvas --> OH_Drawing_...
                                  <-- returns canvasHandle

  canvas.drawColor(0xFFFFFFFF)    --> oh_bridge_canvas_draw_color  --> OH_Drawing_CanvasClear
  canvas.save()                   --> oh_bridge_canvas_save         --> OH_Drawing_CanvasSave
  canvas.translate(0, 0)          --> oh_bridge_canvas_translate    --> OH_Drawing_CanvasTranslate
  canvas.drawRect(r, paint)       --> oh_bridge_canvas_draw_rect   --> OH_Drawing_CanvasDrawRect
  canvas.drawText("Hello",x,y,p) --> oh_bridge_canvas_draw_text   --> OH_Drawing_CanvasDrawTextBlob
  canvas.restore()                --> oh_bridge_canvas_restore      --> OH_Drawing_CanvasRestore
  canvas.save()                   --> oh_bridge_canvas_save         --> OH_Drawing_CanvasSave
  canvas.translate(0, 48)         --> oh_bridge_canvas_translate    --> OH_Drawing_CanvasTranslate
  canvas.drawRect(r, paint)       --> oh_bridge_canvas_draw_rect   --> OH_Drawing_CanvasDrawRect
  canvas.drawText("Click",x,y,p) --> oh_bridge_canvas_draw_text   --> OH_Drawing_CanvasDrawTextBlob
  canvas.restore()                --> oh_bridge_canvas_restore      --> OH_Drawing_CanvasRestore
  OHBridge.surfaceFlush(ctx)      --> oh_bridge_surface_flush      --> OH_NativeWindow_Flush

  Total JNI crossings: ~12 for a simple 2-widget layout
  Overhead: ~2-5 microseconds per crossing = ~24-60 us total
  Well within 16,600 us frame budget
```

---

## 3. Touch Event Flow (Finger -> onClick)

```
User touches screen at (240, 350)
  |
  +--1--> OHOS Input Service detects touch
  |       +-- Dispatches to XComponent
  |
  +--2--> XComponent.onTouchEvent(x=240, y=350, action=DOWN)
  |       +-- C++: on_touch_event()
  |             +-- Rust: shim_dispatch_touch()
  |                   +-- JNI: OHBridge.dispatchTouchEvent(ACTION_DOWN, 240, 350, timestamp)
  |
  +--3--> OHBridge.dispatchTouchEvent()  [Java]
  |       +-- MotionEvent event = MotionEvent.obtain(ACTION_DOWN, 240, 350, ts)
  |       +-- Activity activity = MiniServer.get().getActivityManager().getResumedActivity()
  |       +-- activity.dispatchTouchEvent(event)
  |
  +--4--> Activity.dispatchTouchEvent(event)
  |       +-- window.getDecorView().dispatchTouchEvent(event)
  |
  +--5--> ViewGroup.dispatchTouchEvent(event)  [LinearLayout at 0,0 480x800]
  |       +-- for each child (reverse order):
  |       |     if (x >= child.left && x < child.right &&
  |       |         y >= child.top && y < child.bottom):
  |       |       +-- Transform: childX = x - child.left
  |       |       +-- Transform: childY = y - child.top
  |       |       +-- child.dispatchTouchEvent(transformedEvent)
  |       +-- (first child that returns true consumes the event)
  |
  +--6--> Button.dispatchTouchEvent(event)  [Button at 0,300 480x50]
  |       +-- mTouchListener.onTouch()?  --> no listener, skip
  |       +-- onTouchEvent(event)
  |             +-- action == ACTION_DOWN --> return true (consume)
  |             +-- (later) action == ACTION_UP:
  |                   +-- performClick()
  |                         +-- mClickListener.onClick(this)
  |                               +-- APP CODE: "Button clicked!"
  |
  +--7--> Event consumed. Frame renders updated state.
```

### Hit Testing Detail (Nested ViewGroups)

```
  Touch at (240, 350) -- which view gets it?

  +---------------------------------------------------+  decorView (0,0)-(480,800)
  |                                                     |
  |  +-----------------------------------------------+  |
  |  | Header (0,0)-(480,60)                          |  |  350 > 60, MISS
  |  +-----------------------------------------------+  |
  |                                                     |
  |  +-----------------------------------------------+  |
  |  | ScrollView (0,60)-(480,700)                    |  |  60 <= 350 <= 700, HIT
  |  |                                                 |  |
  |  |  +-------------------------------------------+  |  |
  |  |  | LinearLayout (0,0)-(480,1200) scrollY=100  |  |  |  adjusted y = 350-60+100 = 390
  |  |  |                                             |  |  |
  |  |  |  +--------------------------------------+   |  |  |
  |  |  |  | CardView (20,340)-(460,440)           |   |  |  |  340 <= 390 <= 440, HIT
  |  |  |  |                                        |   |  |  |
  |  |  |  |  +------------------------------+     |   |  |  |  adjusted: (220, 50)
  |  |  |  |  | Button (10,30)-(300,70)       |     |   |  |  |  30 <= 50 <= 70, HIT
  |  |  |  |  |                                |     |   |  |  |
  |  |  |  |  | <-- THIS VIEW GETS THE EVENT   |     |   |  |  |  adjusted: (210, 20)
  |  |  |  |  +------------------------------+     |   |  |  |
  |  |  |  +--------------------------------------+   |  |  |
  |  |  +-------------------------------------------+  |  |
  |  +-----------------------------------------------+  |
  |                                                     |
  |  +-----------------------------------------------+  |
  |  | BottomBar (0,700)-(480,800)                    |  |  350 < 700, MISS
  |  +-----------------------------------------------+  |
  +---------------------------------------------------+
```

### Touch Event Sequence (DOWN -> MOVE -> UP)

```
  Time     OHOS Input         JNI Bridge          Java Framework           App Code
  ----     ----------         ----------          --------------           --------
  T+0ms    DOWN(240,350)  --> dispatchTouch() --> Activity
                                                    --> ViewGroup
                                                      --> Button
                                                        onTouchEvent(DOWN)
                                                        return true (consume)
                                                        mPressed = true
                                                        invalidate()         --> repaint

  T+16ms   MOVE(242,351)  --> dispatchTouch() --> Activity
                                                    --> ViewGroup
                                                      --> Button (has DOWN)
                                                        onTouchEvent(MOVE)
                                                        (still inside bounds)

  T+120ms  UP(243,352)    --> dispatchTouch() --> Activity
                                                    --> ViewGroup
                                                      --> Button
                                                        onTouchEvent(UP)
                                                        performClick()  ---> onClick(button)
                                                        mPressed = false
                                                        invalidate()         --> repaint
```

---

## 4. Activity Navigation Flow

```
MenuActivity: user clicks "View Item"
  |
  +--1--> App code:
  |       Intent intent = new Intent();
  |       intent.setComponent(new ComponentName(pkg, "ItemDetailActivity"));
  |       intent.putExtra("item_id", 42);
  |       startActivityForResult(intent, REQUEST_DETAIL);
  |
  +--2--> Activity.startActivityForResult()
  |       +-- MiniActivityManager.startActivity(this, intent, REQUEST_DETAIL)
  |
  +--3--> MiniActivityManager.startActivity()
  |       +-- Resolve: "ItemDetailActivity" --> Class object
  |       |     +-- MiniPackageManager.resolveActivity(intent)
  |       |           +-- Match ComponentName against registered activities
  |       |
  |       +-- Pause current: MenuActivity
  |       |     +-- menuActivity.onPause()
  |       |     +-- menuActivity.onStop()
  |       |
  |       +-- Create new: ItemDetailActivity
  |       |     +-- Class.forName("...ItemDetailActivity").newInstance()
  |       |     +-- activity.mIntent = intent (with extras)
  |       |     +-- activity.mCaller = menuActivity
  |       |     +-- activity.mRequestCode = REQUEST_DETAIL
  |       |     +-- Push onto activity stack
  |       |
  |       +-- Start new:
  |             +-- itemDetailActivity.onCreate(null)
  |             |     +-- getIntent().getIntExtra("item_id", 0) --> 42
  |             +-- itemDetailActivity.onStart()
  |             +-- itemDetailActivity.onResume()
  |
  +--4--> User presses Back (or activity.finish())
  |       +-- MiniActivityManager.finishActivity(itemDetailActivity)
  |             +-- itemDetailActivity.onPause()
  |             +-- itemDetailActivity.onStop()
  |             +-- itemDetailActivity.onDestroy()
  |             +-- Pop from stack
  |             |
  |             +-- Deliver result to caller:
  |             |     menuActivity.onActivityResult(REQUEST_DETAIL, resultCode, resultData)
  |             |
  |             +-- Resume previous:
  |                   +-- menuActivity.onRestart()
  |                   +-- menuActivity.onStart()
  |                   +-- menuActivity.onResume()
  |
  +--5--> MenuActivity is now visible again
```

### Activity Stack State Diagram

```
  STATE 1: App launched           STATE 2: Navigate forward       STATE 3: Back pressed
  ========================        ==========================      ========================

  +----------------------+        +----------------------+        +----------------------+
  | Activity Stack       |        | Activity Stack       |        | Activity Stack       |
  |                      |        |                      |        |                      |
  | [0] MenuActivity     | <-top  | [0] MenuActivity     |        | [0] MenuActivity     | <-top
  |     state: RESUMED   |        |     state: STOPPED   |        |     state: RESUMED   |
  |                      |        |                      |        |                      |
  |                      |        | [1] ItemDetail       | <-top  |                      |
  |                      |        |     state: RESUMED   |        | ItemDetail DESTROYED |
  +----------------------+        +----------------------+        +----------------------+

  Lifecycle calls:                Lifecycle calls:                Lifecycle calls:
  (none - initial)                Menu.onPause()                  Detail.onPause()
                                  Menu.onStop()                   Detail.onStop()
                                  Detail.onCreate()               Detail.onDestroy()
                                  Detail.onStart()                Menu.onActivityResult()
                                  Detail.onResume()               Menu.onRestart()
                                                                  Menu.onStart()
                                                                  Menu.onResume()
```

### Intent Resolution Detail

```
  Intent intent = new Intent("com.example.ACTION_VIEW_ITEM")
    |
    +-- MiniPackageManager.resolveActivity(intent)
          |
          +-- Check 1: Explicit component?
          |     intent.getComponent() != null?
          |     YES --> return component directly (skip resolution)
          |     NO  --> continue to implicit resolution
          |
          +-- Check 2: Implicit resolution (action matching)
          |     for each registered activity:
          |       for each intent-filter:
          |         if filter.action == intent.action:
          |           if filter.categories containsAll intent.categories:
          |             if filter.data matches intent.data:
          |               --> candidates.add(activity)
          |
          +-- Check 3: Single match?
          |     candidates.size() == 1 --> return candidates.get(0)
          |     candidates.size() > 1  --> return first match (no chooser in mini framework)
          |     candidates.size() == 0 --> throw ActivityNotFoundException
          |
          +-- Return: ComponentName of resolved activity
```

---

## 5. SQLite Data Flow

```
App: db.query("menu", null, "category=?", new String[]{"burgers"}, null, null, "price ASC")
  |
  +--1--> SQLiteDatabase.query()  [Pure Java]
  |       +-- Build SQL: "SELECT * FROM menu WHERE category=? ORDER BY price ASC"
  |       +-- Bind args: ["burgers"]
  |       +-- Execute via in-memory Java SQL engine
  |             +-- Parse SQL --> tokenize --> AST
  |             +-- Scan rows in HashMap<rowId, Map<column, value>>
  |             +-- Apply WHERE: filter rows where category=="burgers"
  |             +-- Apply ORDER BY: sort by price column
  |             +-- Return Cursor (MatrixCursor backed by ArrayList<Object[]>)
  |
  +--2--> Cursor navigation  [Pure Java]
  |       +-- cursor.moveToFirst() --> position = 0
  |       +-- cursor.getString(nameIdx) --> "Big Mock Burger"
  |       +-- cursor.getDouble(priceIdx) --> 5.99
  |       +-- cursor.moveToNext() --> position = 1
  |       +-- ... iterate all matching rows
  |
  +--3--> No OHOS API calls. Entire operation is pure Java.
          On device with persistent storage:
            SQLiteDatabase --> OHBridge.rdbStoreQuery() --> @ohos.data.relationalStore
```

### In-Memory SQL Engine Detail

```
  SQLiteDatabase.execSQL("CREATE TABLE menu (id INTEGER PRIMARY KEY, name TEXT,
                          category TEXT, price REAL)")
    |
    +-- MiniSQLEngine.execute("CREATE TABLE ...")
          |
          +-- Parse: CREATE TABLE menu (...)
          +-- tables.put("menu", new Table("menu"))
          +-- table.columns = ["id", "name", "category", "price"]
          +-- table.types   = [INTEGER, TEXT, TEXT, REAL]
          +-- table.rows    = new ArrayList<>()
          +-- table.nextId  = 1

  SQLiteDatabase.execSQL("INSERT INTO menu VALUES (null, 'Big Mock Burger', 'burgers', 5.99)")
    |
    +-- MiniSQLEngine.execute("INSERT ...")
          |
          +-- Parse: INSERT INTO menu VALUES (...)
          +-- table = tables.get("menu")
          +-- row = {id: 1, name: "Big Mock Burger", category: "burgers", price: 5.99}
          +-- table.rows.add(row)
          +-- table.nextId++

  db.query("menu", null, "category=?", ["burgers"], null, null, "price ASC")
    |
    +-- MiniSQLEngine.query(...)
          |
          +-- Phase 1: Full table scan
          |     allRows = table.rows  (N rows)
          |
          +-- Phase 2: WHERE filter
          |     filtered = []
          |     for row in allRows:
          |       if row.get("category").equals("burgers"):
          |         filtered.add(row)
          |
          +-- Phase 3: ORDER BY
          |     Collections.sort(filtered, (a,b) ->
          |       Double.compare(a.get("price"), b.get("price")))
          |
          +-- Phase 4: Projection (SELECT columns)
          |     columns = all (null projection = *)
          |
          +-- Phase 5: Build MatrixCursor
                cursor = new MatrixCursor(columnNames)
                for row in filtered:
                  cursor.addRow(row.values())
                return cursor
```

### Cursor State Machine

```
                 +----------------+
                 | BEFORE_FIRST   |   <-- initial state after query
                 | position = -1  |
                 +-------+--------+
                         |
                  moveToFirst()
                         |
                         v
                 +----------------+
          +----->| ROW 0          |   getString(), getInt(), etc. valid here
          |      | position = 0   |
          |      +-------+--------+
          |              |
          |       moveToNext()
          |              |
          |              v
          |      +----------------+
          |      | ROW 1          |
          |      | position = 1   |
          |      +-------+--------+
          |              |
          |       moveToNext()
          |              :
          |       (repeats for each row)
          |              :
          |              v
          |      +----------------+
          +------| AFTER_LAST     |   moveToNext() returns false
    moveTo       | position = N   |   getString() throws
    First()      +-------+--------+
                         |
                  close()
                         |
                         v
                 +----------------+
                 | CLOSED         |   all methods throw
                 +----------------+
```

---

## 6. SharedPreferences Flow

```
App: prefs.edit().putString("username", "John").putInt("login_count", 5).apply()
  |
  +--1--> SharedPreferences.edit()  [Pure Java]
  |       +-- Returns Editor backed by HashMap<String, Object>
  |
  +--2--> Editor.putString("username", "John")  [Pure Java]
  |       +-- pendingChanges.put("username", "John")
  |
  +--3--> Editor.putInt("login_count", 5)  [Pure Java]
  |       +-- pendingChanges.put("login_count", 5)
  |
  +--4--> Editor.apply()  [Bridges to OHOS for persistence]
  |       +-- Merge pendingChanges into main map
  |       +-- Notify OnSharedPreferenceChangeListeners
  |       +-- OHBridge.preferencesPutString(handle, "username", "John")
  |       |     +-- @ohos.data.preferences --> Preferences.put()
  |       +-- OHBridge.preferencesPutInt(handle, "login_count", 5)
  |       |     +-- @ohos.data.preferences --> Preferences.put()
  |       +-- OHBridge.preferencesFlush(handle)
  |             +-- @ohos.data.preferences --> Preferences.flush()
  |
  +--5--> Reading back:
          prefs.getString("username", null)
            +-- Check in-memory map first (fast path) --> "John"
            +-- If not in memory: OHBridge.preferencesGetString()
                  +-- @ohos.data.preferences --> Preferences.get()
```

### SharedPreferences Internal Architecture

```
  +---------------------------------------------------------------------+
  |  SharedPreferencesImpl ("app_prefs")                                |
  |                                                                     |
  |  +----------------------------+   +------------------------------+  |
  |  | In-Memory Map (fast path)  |   | Pending Changes (Editor)     |  |
  |  |                            |   |                              |  |
  |  | "username"    -> "John"    |   | "username"    -> "John"      |  |
  |  | "login_count" -> 5         |   | "login_count" -> 5          |  |
  |  | "theme"       -> "dark"    |   | (only dirty entries)        |  |
  |  +----------------------------+   +------------------------------+  |
  |                                                                     |
  |  +----------------------------+   +------------------------------+  |
  |  | Listeners                  |   | Persistence Layer            |  |
  |  |                            |   |                              |  |
  |  | listener1.onChanged(key)   |   | [JVM/Test] HashMap only     |  |
  |  | listener2.onChanged(key)   |   | [OHOS]     OHBridge --> @oh |  |
  |  +----------------------------+   +------------------------------+  |
  +---------------------------------------------------------------------+

  apply() vs commit():
  +------------------+-----------------------------+---------------------------+
  | Method           | Behavior                    | Returns                   |
  +------------------+-----------------------------+---------------------------+
  | editor.apply()   | Write in-memory immediately | void (async persist)      |
  |                  | Persist on background thread|                           |
  +------------------+-----------------------------+---------------------------+
  | editor.commit()  | Write in-memory immediately | boolean (sync persist)    |
  |                  | Persist synchronously       | true = success            |
  +------------------+-----------------------------+---------------------------+
```

---

## 7. Service Lifecycle Flow

```
App: startService(new Intent(this, MusicService.class))
  |
  +--1--> Context.startService(intent)
  |       +-- MiniServer.get().getServiceManager().startService(intent)
  |
  +--2--> MiniServiceManager.startService()  [Pure Java]
  |       +-- Resolve: MusicService.class
  |       +-- If not running:
  |       |     +-- service = MusicService.class.newInstance()
  |       |     +-- service.onCreate()           <-- APP CODE
  |       |     +-- Register in running services map
  |       +-- service.onStartCommand(intent, flags, startId)  <-- APP CODE
  |
  +--3--> App: bindService(intent, connection, flags)
  |       +-- MiniServiceManager.bindService()
  |             +-- If not running: create + onCreate() (same as above)
  |             +-- IBinder binder = service.onBind(intent)
  |             +-- connection.onServiceConnected(componentName, binder)
  |
  +--4--> App: stopService() or service.stopSelf()
          +-- MiniServiceManager.stopService()
                +-- service.onDestroy()           <-- APP CODE
                +-- Remove from running services map
```

### Service State Transitions

```
                            startService()
                                 |
                                 v
  +----------+    onCreate()    +----------+    onStartCommand()    +----------+
  |          | ---------------> |          | --------------------> |          |
  | NOT      |                  | CREATED  |                       | STARTED  |
  | RUNNING  |                  |          |                       |          |
  |          | <--------------  |          |                       |          |
  +----------+   onDestroy()    +-----+----+                       +----+-----+
       ^                              |                                  |
       |                              | onBind()                         |
       |                              v                                  |
       |                        +----------+                             |
       |                        |          |                             |
       |     onDestroy()        | BOUND    |     stopService()           |
       +----------------------- |          | <---------------------------+
                                |          |     or stopSelf()
                                +----------+

  startService() path:        bindService() path:         Mixed path:
  onCreate()                  onCreate()                  onCreate()
  onStartCommand()            onBind()                    onStartCommand()
  ...                         ...                         onBind()
  stopService()               unbindService()             unbindService()
  onDestroy()                 onUnbind()                  stopService()
                              onDestroy()                 onUnbind()
                                                          onDestroy()
```

### Bound Service Communication Detail

```
  Activity                     MiniServiceManager            MusicService
  --------                     ------------------            ------------
  bindService(intent,conn,0)
    |
    +------------------------>  resolve MusicService.class
                                |
                                +-- already running? NO
                                |   service = new MusicService()
                                |   service.onCreate()  ------------>  onCreate()
                                |                                       init MediaPlayer
                                |
                                +-- binder = service.onBind(intent) ->  onBind(intent)
                                |                                       return new MusicBinder()
                                |
                                +-- conn.onServiceConnected(name, binder)
                                      |
    <---------------------------------+
    |
    MusicBinder b = (MusicBinder) binder
    b.getService().play("song.mp3")  ----------------------->  play("song.mp3")
    b.getService().pause()           ----------------------->  pause()
    b.getService().isPlaying()       ----------------------->  return true
    |
    unbindService(conn)
    |
    +------------------------>  refCount-- (0 remaining)
                                |
                                +-- service.onUnbind(intent) ------->  onUnbind()
                                +-- service.onDestroy()      ------->  onDestroy()
                                +-- remove from running map             release resources
```

---

## 8. ContentProvider CRUD Flow

```
App: getContentResolver().query(Uri.parse("content://com.example/items"), ...)
  |
  +--1--> ContentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
  |       +-- MiniContentResolver.query()  [Pure Java]
  |
  +--2--> MiniContentResolver resolves authority
  |       +-- Parse URI: authority = "com.example"
  |       +-- Lookup registered providers: authority --> TestProvider instance
  |       +-- If not initialized: provider.onCreate()
  |
  +--3--> TestProvider.query(uri, projection, selection, selectionArgs, sortOrder)
  |       +-- APP CODE: query in-memory data or SQLite
  |       +-- Return Cursor with results
  |
  +--4--> Same for insert/update/delete:
          contentResolver.insert(uri, contentValues)
            --> provider.insert(uri, values)
            --> returns Uri with new ID
```

### URI Routing Detail

```
  content://com.example.notes/notes/42
  |          |                |     |
  |          |                |     +-- ID segment (specific row)
  |          |                +-- path segment (table name)
  |          +-- authority (maps to ContentProvider)
  +-- scheme (always "content" for providers)

  MiniContentResolver URI routing:
  +-----------------------------------------------------------------------+
  |                                                                       |
  |  URI                                Provider          Method          |
  |  ---------------------------------  ----------------  --------------- |
  |  content://com.example.notes/notes  NotesProvider     query/insert    |
  |  content://com.example.notes/notes/42                 query (single)  |
  |  content://contacts/people          ContactsProvider  query           |
  |  content://settings/system          SettingsProvider   query/update   |
  |                                                                       |
  +-----------------------------------------------------------------------+

  Provider resolution:
  +----------------------------------------------------------+
  |  providerMap: HashMap<String, ContentProvider>            |
  |                                                          |
  |  "com.example.notes"  -->  NotesProvider (lazy init)     |
  |  "contacts"           -->  ContactsProvider (lazy init)  |
  |  "settings"           -->  SettingsProvider (lazy init)  |
  +----------------------------------------------------------+
```

### Full CRUD Cycle

```
  INSERT:
    Uri uri = resolver.insert(
      Uri.parse("content://com.example.notes/notes"),
      contentValues {title: "Meeting", body: "10am standup"})
    |
    +-- NotesProvider.insert(uri, values)
    |     +-- db.insert("notes", null, values)
    |     +-- newId = 7
    |     +-- return Uri.parse("content://com.example.notes/notes/7")
    +-- Returned: content://com.example.notes/notes/7

  QUERY:
    Cursor c = resolver.query(
      Uri.parse("content://com.example.notes/notes"),
      new String[]{"title", "body"},    // projection
      "title LIKE ?",                    // selection
      new String[]{"%meet%"},            // selectionArgs
      "created DESC")                    // sortOrder
    |
    +-- NotesProvider.query(uri, proj, sel, args, sort)
    |     +-- db.query("notes", proj, sel, args, null, null, sort)
    |     +-- return cursor with matching rows
    +-- Returned: Cursor [{title:"Meeting", body:"10am standup"}]

  UPDATE:
    int count = resolver.update(
      Uri.parse("content://com.example.notes/notes/7"),
      contentValues {body: "10am standup - CANCELLED"},
      null, null)
    |
    +-- NotesProvider.update(uri, values, null, null)
    |     +-- id = uri.getLastPathSegment() --> "7"
    |     +-- db.update("notes", values, "id=?", new String[]{"7"})
    +-- Returned: 1 (one row updated)

  DELETE:
    int count = resolver.delete(
      Uri.parse("content://com.example.notes/notes/7"),
      null, null)
    |
    +-- NotesProvider.delete(uri, null, null)
    |     +-- id = uri.getLastPathSegment() --> "7"
    |     +-- db.delete("notes", "id=?", new String[]{"7"})
    +-- Returned: 1 (one row deleted)
```

---

## 9. Handler/Looper Message Flow

```
App: handler.postDelayed(runnable, 1000)
  |
  +--1--> Handler.postDelayed(runnable, 1000)  [Pure Java]
  |       +-- Message msg = Message.obtain()
  |       +-- msg.callback = runnable
  |       +-- msg.when = SystemClock.uptimeMillis() + 1000
  |       +-- sendMessageAtTime(msg, msg.when)
  |
  +--2--> MessageQueue.enqueueMessage(msg)  [Pure Java]
  |       +-- Insert into PriorityQueue sorted by msg.when
  |       +-- Wake up Looper if waiting
  |
  +--3--> Looper.loop()  [Pure Java, on Handler's thread]
  |       +-- msg = queue.next()  // blocks until msg.when <= now
  |       +-- msg.target.dispatchMessage(msg)
  |
  +--4--> Handler.dispatchMessage(msg)  [Pure Java]
  |       +-- If msg.callback != null:
  |       |     msg.callback.run()          <-- Runnable executes
  |       +-- Else:
  |             handleMessage(msg)          <-- APP CODE
  |
  +--5--> Message.recycle()  [Pure Java]
          +-- Return to Message pool for reuse
```

### Message Queue Internals

```
  MessageQueue (sorted linked list by delivery time)
  ==================================================

  head --> [msg.when=100] --> [msg.when=250] --> [msg.when=1100] --> null
           what=1             what=TICK          callback=Runnable
           target=handler1    target=handler2    target=handler1

  Looper.loop() behavior:
  +---------------------------------------------------------+
  |  for (;;) {                                             |
  |    Message msg = queue.next();  // may block             |
  |    if (msg == null) return;     // quit() was called     |
  |    msg.target.dispatchMessage(msg);                      |
  |    msg.recycleUnchecked();                               |
  |  }                                                       |
  +---------------------------------------------------------+

  queue.next() behavior:
  +---------------------------------------------------------+
  |  for (;;) {                                             |
  |    long now = SystemClock.uptimeMillis();                |
  |    Message msg = mMessages;  // peek head                |
  |    if (msg == null) {                                    |
  |      wait();  // no messages, block indefinitely         |
  |    } else if (msg.when > now) {                          |
  |      wait(msg.when - now);  // sleep until delivery time |
  |    } else {                                              |
  |      mMessages = msg.next;  // dequeue                   |
  |      return msg;                                         |
  |    }                                                     |
  |  }                                                       |
  +---------------------------------------------------------+
```

### Handler/Looper Thread Model

```
  Main Thread (UI Thread)                    Worker Thread
  =======================                    =============

  Looper.prepareMainLooper()
  |
  +-- new Looper()
  |     +-- mQueue = new MessageQueue()
  |
  +-- mainHandler = new Handler(mainLooper)
  |
  +-- Looper.loop()                          new Thread(() -> {
  |     |                                      // do work
  |     |   +--> [msg1] dispatchMessage()      long result = compute();
  |     |   |    handler.handleMessage()
  |     |   |                                  // post result to UI thread
  |     |   +--> [msg2] dispatchMessage()      mainHandler.post(() -> {
  |     |   |    msg.callback.run()              textView.setText("Done: " + result);
  |     |   |      textView.setText(...)       });
  |     |   |      invalidate()              }).start();
  |     |   |
  |     |   +--> [msg3] ...
  |     v
  |   (blocks when queue empty)
  |   (wakes when message posted)

  Thread-safety guarantee:
  - Handler.post() is thread-safe (synchronized on MessageQueue)
  - The Runnable executes on the Handler's Looper thread
  - All UI updates happen on the main thread via mainHandler
```

---

## 10. ArkUI Node Wiring Flow (View -> Native Widget)

```
App: LinearLayout layout = new LinearLayout(context)
  |
  +--1--> LinearLayout constructor
  |       +-- super(NODE_TYPE_COLUMN)    // NODE_TYPE_COLUMN = 16
  |             +-- View(int nodeType)
  |                   +-- nativeHandle = OHBridge.nodeCreate(16)
  |                         +-- [Real] OH_ArkUI_NodeCreate(ARKUI_COLUMN)
  |                         +-- [Mock] returns handle from AtomicLong
  |
  +--2--> layout.addView(textView)
  |       +-- mChildren.add(textView)
  |       +-- textView.mParent = this
  |       +-- OHBridge.nodeAddChild(this.nativeHandle, textView.nativeHandle)
  |             +-- [Real] OH_ArkUI_NodeAddChild(parent, child)
  |             +-- [Mock] no-op (tracked in Java)
  |
  +--3--> textView.setText("Hello")
  |       +-- mText = "Hello"
  |       +-- OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_CONTENT, "Hello")
  |             +-- [Real] OH_ArkUI_NodeSetAttribute(node, ATTR_TEXT, "Hello")
  |             +-- [Mock] no-op
  |
  +--4--> textView.setTextColor(0xFFFF0000)
  |       +-- mTextColor = 0xFFFF0000
  |       +-- OHBridge.nodeSetAttrColor(nativeHandle, ATTR_TEXT_COLOR, 0xFFFF0000)
  |             +-- [Real] OH_ArkUI_NodeSetAttribute(node, ATTR_COLOR, 0xFFFF0000)
  |
  +--5--> button.setOnClickListener(listener)
  |       +-- mClickListener = listener
  |       +-- OHBridge.nodeRegisterEvent(nativeHandle, EVENT_CLICK, eventId)
  |             +-- [Real] OH_ArkUI_NodeRegisterEvent(node, ON_CLICK, id)
  |             +-- When clicked: C++ --> Rust --> JNI --> OHBridge.dispatchNodeEvent()
  |                   --> View.findViewByHandle(handle) --> view.onNativeEvent()
  |                   --> performClick() --> mClickListener.onClick(view)
  |
  +--6--> Dual rendering path:
          Path A (Canvas/Skia): View.draw(canvas) --> OH_Drawing --> pixels
          Path B (ArkUI nodes): View properties --> nodeSetAttr --> native ArkUI rendering
          Both paths coexist. Path B gives native look & feel for standard widgets.
```

### Dual Rendering Architecture

```
  +-----------------------------------------------------------------------+
  |                          Android View Tree                            |
  |                                                                       |
  |  LinearLayout (vertical)                                              |
  |    +-- TextView  "Hello World"                                        |
  |    +-- EditText  (text input)                                         |
  |    +-- Button    "Submit"                                             |
  |    +-- CustomView (app draws manually via Canvas)                     |
  +-----------------------------------------------------------------------+
          |                                          |
          | Path B: Standard Widgets                 | Path A: Custom Drawing
          | (ArkUI native nodes)                     | (Canvas/Skia)
          v                                          v
  +-------------------------------+    +-------------------------------+
  | ArkUI Node Tree               |    | Canvas Drawing Commands       |
  |                                |    |                               |
  | Column                         |    | (only for CustomView)         |
  |   +-- Text("Hello World")     |    |                               |
  |   +-- TextInput()              |    | canvas.drawCircle(x,y,r,p)   |
  |   +-- Button("Submit")        |    | canvas.drawPath(path, paint)  |
  |                                |    | canvas.drawBitmap(bmp,x,y)   |
  +-------------------------------+    +-------------------------------+
          |                                          |
          v                                          v
  +-------------------------------+    +-------------------------------+
  | OH_ArkUI_NodeSetAttribute()   |    | OH_Drawing_CanvasDraw*()     |
  | Native ArkUI rendering        |    | Skia rendering to surface    |
  | (system theme, animations)    |    | (pixel-perfect custom draw)  |
  +-------------------------------+    +-------------------------------+
          |                                          |
          +------------------+-----------------------+
                             |
                             v
                   +-------------------+
                   | OH_NativeWindow   |
                   | Compositor        |
                   | Display           |
                   +-------------------+
```

### Event Callback Wiring (ArkUI Node Path)

```
  Java: button.setOnClickListener(myListener)
    |
    +-- eventId = EventRegistry.register(button, EVENT_CLICK)
    +-- OHBridge.nodeRegisterEvent(button.nativeHandle, EVENT_CLICK, eventId)
          |
          v
  C (liboh_bridge.so):
    OH_ArkUI_NodeRegisterEvent(nodeHandle, NODE_EVENT_ON_CLICK, eventId)
    // OHOS now knows: when this node clicked, fire event with eventId

  ... user taps the button on screen ...

  OHOS ArkUI framework:
    on_click_callback(nodeHandle, eventId)
      |
      v
  C (liboh_bridge.so):
    oh_bridge_dispatch_node_event(eventId, eventData)
      |
      +-- JNI call into Java:
            OHBridge.dispatchNodeEvent(eventId, eventData)
              |
              v
  Java:
    EventRegistry.dispatch(eventId)
      |
      +-- entry = registry.get(eventId)
      +-- view = entry.view        // the Button
      +-- type = entry.eventType   // EVENT_CLICK
      +-- view.onNativeEvent(type, data)
            |
            +-- performClick()
                  |
                  +-- mClickListener.onClick(this)
                        |
                        v
                  APP CODE RUNS
```

---

## 11. resources.arsc Parse Flow

```
ResourceTableParser.parse(bytes, resources)
  |
  +--1--> Read Table Header (type=0x0002)
  |       +-- headerSize, totalSize, packageCount
  |       +-- Validate: must be type 0x0002
  |
  +--2--> Read Global String Pool (type=0x0001)
  |       +-- stringCount, styleCount, flags
  |       +-- stringsStart offset
  |       +-- Read uint32 offsets[stringCount]
  |       +-- For each string:
  |       |     if (flags & 0x100): read UTF-8 (length + data + null)
  |       |     else: read UTF-16 (length + data + null)
  |       +-- Store in String[] globalPool
  |
  +--3--> Read Package Chunk (type=0x0200)
  |       +-- packageId (0x7f for app resources)
  |       +-- packageName (256 bytes UTF-16)
  |       +-- Read Type String Pool (type names: "attr", "layout", "string", ...)
  |       +-- Read Key String Pool (entry names: "app_name", "greeting", ...)
  |
  +--4--> Read TypeSpec Chunks (type=0x0202)
  |       +-- Per-type configuration flags (skip for basic parsing)
  |
  +--5--> Read Type Chunks (type=0x0201)
  |       +-- typeId (1-based index into type string pool)
  |       +-- entryCount
  |       +-- Read uint32 offsets[entryCount]
  |       +-- For each entry (where offset != 0xFFFFFFFF):
  |       |     +-- Read entry: size, flags, keyIndex
  |       |     +-- Read Res_value: size, res0, dataType, data
  |       |     +-- resourceId = 0x7f000000 | (typeId << 16) | entryIndex
  |       |     +-- Switch on dataType:
  |       |     |     0x03 (string): value = globalPool[data]
  |       |     |     0x10 (int_dec): value = data
  |       |     |     0x1c (color_argb8): value = data (as int)
  |       |     |     0x12 (boolean): value = (data != 0)
  |       |     +-- Register: resources.registerStringResource(resourceId, value)
  |       |                   resources.registerColorResource(resourceId, value)
  |       +-- end for each type chunk
  |
  +--6--> Result: Resources instance populated with all values
          resources.getString(0x7f030000) --> "Hello World APK"
          resources.getColor(0x7f040001) --> 0xFF2196F3
```

### Resource ID Format

```
  Resource ID: 0x7f030002
  ========================

  +----------+----------+----------+
  |  PP      |  TT      |  EEEE    |
  |  7f      |  03      |  0002    |
  +----------+----------+----------+
  |  Package |  Type    |  Entry   |
  |  ID      |  ID      |  Index   |
  +----------+----------+----------+

  PP = 0x7f --> app package (0x01 = android framework, 0x7f = app)
  TT = 0x03 --> type index in type string pool (e.g., "string" = 3)
  EEEE = 0x0002 --> entry index within that type (e.g., third string)

  Type String Pool:
  +-------+--------+
  | Index | Name   |
  +-------+--------+
  | 0x01  | attr   |
  | 0x02  | layout |
  | 0x03  | string |
  | 0x04  | color  |
  | 0x05  | dimen  |
  | 0x06  | style  |
  | 0x07  | drawable|
  +-------+--------+

  So 0x7f030002 = app package / string type / entry #2
  Might resolve to: "Hello World APK"
```

### resources.arsc Binary Layout

```
  Offset   Size     Description
  ------   ----     -----------
  0x0000   8        Table Header (type=0x0002, headerSize=12, size=total)
  0x000C   varies   Global String Pool
                      +0: type=0x0001, headerSize, size
                      +8: stringCount, styleCount, flags
                      +20: stringsStart, stylesStart
                      +28: offsets[stringCount]
                      +..: string data (UTF-8 or UTF-16)

  varies   varies   Package Chunk (type=0x0200)
                      +0: type, headerSize, size
                      +8: packageId (uint32)
                      +12: packageName (256 bytes UTF-16)
                      +268: typeStringsOffset
                      +272: lastPublicType
                      +276: keyStringsOffset
                      +280: lastPublicKey
                      +..: Type String Pool (type=0x0001)
                      +..: Key String Pool (type=0x0001)

  varies   varies   TypeSpec Chunk(s) (type=0x0202)
                      +0: type, headerSize, size
                      +8: typeId, res0, res1
                      +12: entryCount
                      +16: flags[entryCount]   // config change flags per entry

  varies   varies   Type Chunk(s) (type=0x0201)
                      +0: type, headerSize, size
                      +8: typeId, res0, res1
                      +12: entryCount
                      +16: entriesStart
                      +20: config (size + density/locale/screen/etc.)
                      +..: entryOffsets[entryCount]  (0xFFFFFFFF = no entry)
                      +..: entry data:
                             +0: size (uint16)
                             +2: flags (uint16, 0x0001 = complex)
                             +4: keyIndex (uint32, into key string pool)
                             +8: Res_value {size, res0, dataType, data}
```

---

## 12. Binary AndroidManifest.xml (AXML) Parse Flow

```
BinaryXmlParser.parse(manifestBytes)
  |
  +--1--> Read Chunk Header
  |       +-- type = 0x0003 (XML document)
  |       +-- headerSize = 8
  |       +-- totalSize
  |
  +--2--> Read String Pool (type=0x0001)
  |       +-- Same format as resources.arsc string pool
  |           Contains all tag/attribute names and values:
  |           ["manifest", "package", "application", "activity",
  |            "intent-filter", "action", "category", "android",
  |            "name", "label", "com.example.hello", ...]
  |
  +--3--> Read Resource ID Table (type=0x0180)
  |       +-- Maps string pool indices to android.R.attr.* resource IDs
  |           e.g., index 8 --> 0x01010003 (android:name)
  |
  +--4--> Read XML Nodes
  |       +-- START_NAMESPACE (type=0x0100)
  |       |     prefix="android", uri="http://schemas.android.com/apk/res/android"
  |       |
  |       +-- START_TAG (type=0x0102): <manifest>
  |       |     +-- attributes: package="com.example.hello"
  |       |     +-- Push onto tag stack
  |       |
  |       +-- START_TAG: <application>
  |       |     +-- attributes: label="Hello World"
  |       |
  |       +-- START_TAG: <activity>
  |       |     +-- attributes: name=".HelloActivity"
  |       |
  |       +-- START_TAG: <intent-filter>
  |       |     +-- <action name="android.intent.action.MAIN" />
  |       |     +-- <category name="android.intent.category.LAUNCHER" />
  |       |
  |       +-- END_TAG: </intent-filter>, </activity>, </application>, </manifest>
  |       +-- END_NAMESPACE
  |
  +--5--> Result: ApkInfo
          +-- packageName = "com.example.hello"
          +-- activities = [{name: ".HelloActivity", intentFilters: [MAIN/LAUNCHER]}]
          +-- services = []
          +-- providers = []
          +-- launcherActivity = "com.example.hello.HelloActivity"
```

### AXML Binary Node Format

```
  START_TAG (type=0x0102):
  +------------------------------------------------------------------+
  | Offset  | Size   | Field          | Example                      |
  +---------+--------+----------------+------------------------------+
  | +0      | 2      | type           | 0x0102 (START_TAG)           |
  | +2      | 2      | headerSize     | 0x0010                       |
  | +4      | 4      | size           | total chunk size             |
  | +8      | 4      | lineNumber     | source line (debug info)     |
  | +12     | 4      | comment        | 0xFFFFFFFF (none)            |
  | +16     | 4      | namespaceUri   | string pool index or -1      |
  | +20     | 4      | name           | string pool index ("activity")|
  | +24     | 2      | attributeStart | offset to first attribute    |
  | +26     | 2      | attributeSize  | 20 (each attribute = 20 B)   |
  | +28     | 2      | attributeCount | number of attributes         |
  | +30     | 2      | idIndex        | which attr is "id" (0-based) |
  | +32     | 2      | classIndex     | which attr is "class"        |
  | +34     | 2      | styleIndex     | which attr is "style"        |
  +---------+--------+----------------+------------------------------+

  Each Attribute (20 bytes):
  +---------+--------+----------------+------------------------------+
  | +0      | 4      | namespace      | string pool index            |
  | +4      | 4      | name           | string pool index ("name")   |
  | +8      | 4      | rawValue       | string pool idx or -1        |
  | +12     | 2      | typedValue.sz  | 8                            |
  | +14     | 1      | typedValue.res | 0                            |
  | +15     | 1      | typedValue.typ | data type (0x03=string, etc.)|
  | +16     | 4      | typedValue.dat | data (string idx, int, etc.) |
  +---------+--------+----------------+------------------------------+
```

### Manifest Parse -> MiniServer Registration

```
  BinaryXmlParser output                  MiniServer registration
  ========================                ========================

  ApkInfo {                               MiniPackageManager {
    package: "com.example.hello"            registeredActivities: [
    activities: [                             {
      {                                         className: "com.example.hello.HelloActivity"
        name: ".HelloActivity"                  intentFilters: [
        intentFilters: [                          {
          {                                         actions: ["android.intent.action.MAIN"]
            actions: [MAIN]       ------>            categories: ["android.intent.category.LAUNCHER"]
            categories: [LAUNCHER]                 }
          }                                      ]
        ]                                      }
      }                                      ]
    ]                                       }
    services: [
      {                                   MiniServiceManager {
        name: ".SyncService"                registeredServices: [
      }                           ------>     "com.example.hello.SyncService"
    ]                                       ]
    providers: [                          }
      {
        name: ".DataProvider"             MiniContentResolver {
        authority: "com.example.data"       providerMap: {
      }                           ------>     "com.example.data" --> DataProvider (lazy)
    ]                                       }
  }                                       }

  Note: ".HelloActivity" is expanded to full class name by prepending package:
  ".HelloActivity" --> "com.example.hello" + ".HelloActivity"
                   --> "com.example.hello.HelloActivity"
```

---

## Summary: What Crosses the Bridge

Of the entire Android framework (57,000+ APIs), only these categories
cross from Java into native OHOS code via liboh_bridge.so:

```
  Category          |  JNI Calls/Frame  |  Latency Added
  ------------------+-------------------+----------------
  Canvas draw ops   |  ~100-200         |  ~10-20 us
  ArkUI node attrs  |  ~0-50            |  ~5 us
  Surface flush     |  1                |  ~100 us
  Input events      |  ~1-10            |  ~1 us
  ------------------+-------------------+----------------
  Total per frame   |  ~100-260         |  ~15-125 us
  Frame budget      |                   |  16,600 us
  Bridge overhead   |                   |  <1%

Everything else (57,000 APIs) runs as pure Java in Dalvik.
The bridge is invisible to app performance.
```

### Bridge Call Categories

```
  +-----------------------------------------------------------------------+
  |                        Android App (Java / Dalvik VM)                  |
  |                                                                       |
  |   Pure Java (no bridge)              |    Bridge (JNI to OHOS)        |
  |   ================================   |   ==========================   |
  |   Bundle, Intent, ContentValues      |   Canvas.draw*()              |
  |   SharedPreferences (in-memory)      |   Surface acquire/flush       |
  |   SQLiteDatabase (in-memory)         |   ArkUI node create/set       |
  |   Handler, Looper, Message           |   Touch event dispatch        |
  |   Activity lifecycle                 |   Preferences persist         |
  |   Service lifecycle                  |   File I/O (optional)         |
  |   ContentProvider routing            |   Sensor/Camera/BT (Tier C)   |
  |   View layout & measure             |                                |
  |   XML/resource parsing               |                                |
  |   Collections, text, math            |                                |
  |                                      |                                |
  |   ~99% of API surface                |   ~1% of API surface          |
  |   ~0 us overhead                     |   ~15-125 us/frame            |
  +-----------------------------------------------------------------------+
                                         |
                                         v
  +-----------------------------------------------------------------------+
  |                     liboh_bridge.so (C / Rust)                        |
  |                                                                       |
  |   JNI entry points --> OHOS C API calls                               |
  |   ~50 functions total                                                 |
  +-----------------------------------------------------------------------+
                                         |
                                         v
  +-----------------------------------------------------------------------+
  |                     OpenHarmony Native APIs                           |
  |                                                                       |
  |   OH_Drawing_* (Skia 2D)     OH_ArkUI_* (UI nodes)                   |
  |   OH_NativeWindow_*          OH_Input_*                               |
  |   @ohos.data.preferences     @ohos.data.relationalStore               |
  +-----------------------------------------------------------------------+
```
