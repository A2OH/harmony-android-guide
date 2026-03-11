# Android 11 (AOSP) 代码审查: View System and UI Framework

**Review Date:** 2026-03-10
**Source Base:** Android 11 (API Level 30) AOSP
**Scope:** View hierarchy, widget library, animation framework, graphics subsystem

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Core View Hierarchy](#2-core-view-hierarchy)
   - 2.1 [View.java](#21-viewjava)
   - 2.2 [ViewGroup.java](#22-viewgroupjava)
3. [Window and Window Management](#3-window-and-window-management)
   - 3.1 [Window.java](#31-windowjava)
   - 3.2 [WindowManager.java](#32-windowmanagerjava)
4. [Layout Inflation](#4-layout-inflation)
5. [Input and Event Handling](#5-input-and-event-handling)
   - 5.1 [InputEvent.java](#51-inputeventjava)
   - 5.2 [MotionEvent.java](#52-motioneventjava)
   - 5.3 [KeyEvent.java](#53-keyeventjava)
   - 5.4 [GestureDetector.java](#54-gesturedetectorjava)
   - 5.5 [Touch Event Dispatch Mechanism](#55-touch-event-dispatch-mechanism)
6. [Widget Library](#6-widget-library)
   - 6.1 [TextView.java](#61-textviewjava)
   - 6.2 [EditText.java](#62-edittextjava)
   - 6.3 [Button.java](#63-buttonjava)
   - 6.4 [ImageView.java](#64-imageviewjava)
7. [Layout Managers](#7-layout-managers)
   - 7.1 [FrameLayout.java](#71-framelayoutjava)
   - 7.2 [LinearLayout.java](#72-linearlayoutjava)
   - 7.3 [RelativeLayout.java](#73-relativelayoutjava)
   - 7.4 [ConstraintLayout](#74-constraintlayout)
8. [Animation Framework](#8-animation-framework)
   - 8.1 [Animator.java](#81-animatorjava)
   - 8.2 [ValueAnimator.java](#82-valueanimatorjava)
   - 8.3 [ObjectAnimator.java](#83-objectanimatorjava)
   - 8.4 [AnimatorSet.java](#84-animatorsetjava)
   - 8.5 [ViewPropertyAnimator](#85-viewpropertyanimator)
9. [Graphics Subsystem](#9-graphics-subsystem)
   - 9.1 [Canvas.java](#91-canvasjava)
   - 9.2 [Paint.java](#92-paintjava)
   - 9.3 [Bitmap.java](#93-bitmapjava)
   - 9.4 [Drawable.java](#94-drawablejava)
10. [Surface Rendering](#10-surface-rendering)
    - 10.1 [SurfaceView.java](#101-surfaceviewjava)
    - 10.2 [TextureView.java](#102-textureviewjava)
    - 10.3 [Choreographer.java](#103-choreographerjava)
11. [RecyclerView (Internal)](#11-recyclerview-internal)
12. [Design Patterns Summary](#12-design-patterns-summary)
13. [Performance Considerations](#13-performance-considerations)
14. [Hidden and System API Summary](#14-hidden-and-system-api-summary)

---

## 1. Executive Summary

The Android View system is the foundation of all UI rendering and interaction on Android. At its core, it implements a tree-structured hierarchy (Composite pattern) where `View` is the leaf node and `ViewGroup` is the composite container. The system spans approximately 100,000+ lines of Java code across the `android.view`, `android.widget`, `android.animation`, and `android.graphics` packages.

**Key architectural properties:**
- **Single-threaded UI model**: All View operations must execute on the main (UI) thread, enforced by `@UiThread` annotations and runtime checks.
- **Three-phase rendering pipeline**: measure -> layout -> draw, coordinated by `ViewRootImpl` and `Choreographer` in sync with VSYNC.
- **Event dispatch via Chain of Responsibility**: Touch events flow top-down through `dispatchTouchEvent()` / `onInterceptTouchEvent()` / `onTouchEvent()`.
- **Hardware-accelerated rendering**: The default rendering path uses `RenderNode` objects and `RecordingCanvas` for GPU-backed drawing.
- **Extensive backward compatibility**: Pervasive `@UnsupportedAppUsage` annotations and SDK-version-gated behavior preserve compatibility back to early Android releases.

---

## 2. Core View Hierarchy

### 2.1 View.java

**File:** `frameworks/base/core/java/android/view/View.java` (30,408 lines)

#### Class Purpose
`View` is the fundamental building block of all UI components. It represents a rectangular area on screen, responsible for drawing its own content and handling user interaction. Every widget (Button, TextView, ImageView) and every layout (LinearLayout, FrameLayout) ultimately extends `View`.

**Class declaration (line 130 area, after extensive Javadoc):**
```java
@UiThread
public class View implements Drawable.Callback, KeyEvent.Callback,
        AccessibilityEventSource
```

#### Key Public APIs for App Developers

**Visibility and Positioning:**
- `setVisibility(int)` (line 11718) -- `VISIBLE`, `INVISIBLE`, `GONE`
- `setAlpha(float)`, `setTranslationX/Y/Z(float)`, `setRotation(float)`, `setScaleX/Y(float)`
- `setPadding(int, int, int, int)`, `setBackgroundColor(int)`, `setBackground(Drawable)`
- `setElevation(float)` -- Z-ordering for Material Design shadows

**Event Listeners:**
- `setOnClickListener(OnClickListener)` (line 7235)
- `setOnTouchListener(OnTouchListener)` (line 7721)
- `setOnLongClickListener(OnLongClickListener)`
- `setOnFocusChangeListener(OnFocusChangeListener)`
- `setOnDragListener(OnDragListener)`
- `setOnScrollChangeListener(OnScrollChangeListener)` (line 16328)

**Focus Management:**
- `requestFocus()`, `clearFocus()`, `isFocused()`, `isFocusable()`
- `setFocusableInTouchMode(boolean)`

**Animation:**
- `animate()` (line 28326) -- returns `ViewPropertyAnimator` for fluent animation API

#### The Measure-Layout-Draw Lifecycle

This is the most critical mechanism for app developers creating custom views.

**Phase 1: Measure** (`measure()` at line 25429, `onMeasure()` at line 25540)

`measure(int widthMeasureSpec, int heightMeasureSpec)` is `final` -- it cannot be overridden. It delegates to `onMeasure()`, which subclasses must override and call `setMeasuredDimension()`.

```
measure() [final]
  --> onMeasure(widthMeasureSpec, heightMeasureSpec) [override this]
       --> setMeasuredDimension(width, height) [MUST call]
```

The `MeasureSpec` class (line 28104) encodes both size and mode in a single int:
- `UNSPECIFIED` (0): Parent imposes no constraint
- `EXACTLY` (1 << 30): Parent has determined exact size
- `AT_MOST` (2 << 30): Child can be up to the specified size

MeasureSpec packs mode into the top 2 bits, size into the lower 30 bits via:
```java
public static int makeMeasureSpec(int size, int mode) {
    return (size & ~MODE_MASK) | (mode & MODE_MASK);
}
```

**Performance note (line 25440-25492):** `measure()` implements a `LongSparseLongArray` cache (`mMeasureCache`) keyed by `(widthSpec << 32 | heightSpec)`. This avoids redundant measurement when specs haven't changed.

**Phase 2: Layout** (`layout()` at line 22829, `onLayout()` at line 22934)

```
layout(l, t, r, b) [do not override]
  --> setFrame(l, t, r, b) [sets mLeft/mTop/mRight/mBottom]
  --> onLayout(changed, l, t, r, b) [override in ViewGroup subclasses]
```

`layout()` is not `final` but the Javadoc explicitly says "Derived classes should not override this method" (line 22818). `onLayout()` in the base `View` class is empty (line 22934) -- only ViewGroup subclasses need to position children.

**Phase 3: Draw** (`draw()` at line 22322, `onDraw()` at line 19909)

The `draw()` method documents a precise 7-step drawing order (lines 22326-22336):
1. Draw the background
2. Save canvas layers for fading edges (if needed)
3. Draw view's content (`onDraw(canvas)`)
4. Draw children (`dispatchDraw(canvas)`)
5. Draw fading edges and restore layers (if needed)
6. Draw decorations (scrollbars, foreground)
7. Draw default focus highlight

For the common case (no fading edges), steps 2 and 5 are skipped (line 22348).

**Invalidation:**
- `invalidate()` (line 18469) -- marks the view for redraw
- `requestLayout()` (line 25371) -- marks for re-measure and re-layout, propagates up via `mParent.requestLayout()`
- `postInvalidate()` -- thread-safe invalidation from background threads

#### Listener Interfaces (defined within View)

| Interface | Line | Purpose |
|---|---|---|
| `OnClickListener` | 28517 | Single tap/click events |
| `OnLongClickListener` | 28463 | Long-press events |
| `OnTouchListener` | 28411 | Raw touch events (pre-empts `onTouchEvent`) |
| `OnHoverListener` | 28429 | Mouse hover events |
| `OnDragListener` | 28486 | Drag-and-drop events |
| `OnFocusChangeListener` | 28504 | Focus gain/loss |
| `OnKeyListener` | 28371 | Hardware key events |
| `OnScrollChangeListener` | 28328 | Scroll position changes |
| `OnLayoutChangeListener` | 28345 | Layout bounds changes |
| `OnAttachStateChangeListener` | 28585 | Window attach/detach |
| `OnApplyWindowInsetsListener` | 28610 | System window insets |
| `OnContextClickListener` | 28529 | Context click (right-click) |
| `OnCapturedPointerListener` | 27865 | Captured pointer events |
| `OnSystemUiVisibilityChangeListener` | 28568 | System UI visibility changes |
| `OnUnhandledKeyEventListener` | 28394 | Unhandled key events |

#### Internal ListenerInfo Pattern (line 4672)

View uses a `ListenerInfo` inner class to hold all listener references. This is lazily allocated to save memory when no listeners are set:
```java
static class ListenerInfo {
    OnFocusChangeListener mOnFocusChangeListener;
    OnClickListener mOnClickListener;
    OnLongClickListener mOnLongClickListener;
    OnTouchListener mOnTouchListener;
    // ... many more
}
```

#### Notable Hidden/System APIs

- `@UnsupportedAppUsage` is used extensively (hundreds of instances) to mark fields and methods that apps should not access but historically could via reflection.
- `canReceivePointerEvents()` (line 14341, `@hide`) -- checks if view is VISIBLE or has an animation.
- `makeSafeMeasureSpec()` (line 28169, `@hide`) -- compatibility wrapper that zeroes out UNSPECIFIED specs for old apps.
- `setFrame()` -- internal method for setting position bounds.
- Various `PFLAG_*` constants control internal state via bit flags in `mPrivateFlags`, `mPrivateFlags2`, `mPrivateFlags3`.

---

### 2.2 ViewGroup.java

**File:** `frameworks/base/core/java/android/view/ViewGroup.java` (9,277 lines)

#### Class Purpose
`ViewGroup` is the abstract base class for all layout containers. It extends `View` and implements `ViewParent` and `ViewManager`. It manages a collection of child Views and handles the delegation of measure, layout, and draw operations to those children.

```java
@UiThread
public abstract class ViewGroup extends View implements ViewParent, ViewManager
```

#### Key Public APIs

**Child Management:**
- `addView(View child)` (line 4975) -- multiple overloads accepting index, width/height, LayoutParams
- `addView(View child, int index, LayoutParams params)` (line 5050) -- the core addView implementation
- `removeView(View view)` (line 5461)
- `removeViewAt(int index)` (line 5506)
- `removeAllViews()`
- `getChildCount()` (line 6872)
- `getChildAt(int index)` (line 6883)

**Measurement Helpers:**
- `measureChildren(int widthMeasureSpec, int heightMeasureSpec)` (line 6899)
- `measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec)` (line 6919)
- `measureChildWithMargins(View child, ...)` (line 6945) -- accounts for parent padding and child margins

**Drawing:**
- `drawChild(Canvas canvas, View child, long drawingTime)` (line 4515)
- `setClipChildren(boolean)`, `setClipToPadding(boolean)`

**Layout Animation:**
- `setLayoutTransition(LayoutTransition)` -- enables animated add/remove of children
- `setLayoutAnimation(LayoutAnimationController)` -- legacy XML-based layout animations

#### Touch Event Dispatch (The Chain of Responsibility)

The most architecturally important code in ViewGroup is `dispatchTouchEvent()` (line 2633). This implements the complete touch event dispatch algorithm:

```
ViewGroup.dispatchTouchEvent(MotionEvent)
  |
  |-- 1. On ACTION_DOWN: cancelAndClearTouchTargets(), resetTouchState()
  |
  |-- 2. Check interception:
  |      if (ACTION_DOWN || mFirstTouchTarget != null) {
  |          if (!FLAG_DISALLOW_INTERCEPT)
  |              intercepted = onInterceptTouchEvent(ev)  // <-- CAN STEAL EVENTS
  |      } else {
  |          intercepted = true  // No target, continue intercepting
  |      }
  |
  |-- 3. If not intercepted, find child target:
  |      for (i = childrenCount - 1; i >= 0; i--)  // front-to-back order
  |          if (child.canReceivePointerEvents() && isTransformedTouchPointInView())
  |              if (dispatchTransformedTouchEvent(ev, child))
  |                  addTouchTarget(child)
  |                  break
  |
  |-- 4. Dispatch to touch targets:
  |      if (mFirstTouchTarget == null)
  |          dispatchTransformedTouchEvent(ev, null)  // treat as ordinary view
  |      else
  |          for each target in linked list:
  |              dispatchTransformedTouchEvent(ev, target.child)
```

**`onInterceptTouchEvent(MotionEvent)`** (line 3282) -- Returns `false` by default. Subclasses (like `ScrollView`) override to intercept scroll gestures. When interception begins mid-gesture, the previously-targeted child receives `ACTION_CANCEL`.

**Touch target management uses a singly-linked list** (`mFirstTouchTarget`), where each `TouchTarget` holds a child View reference and a pointer ID bitmask. This enables multi-touch split motion event dispatch when `FLAG_SPLIT_MOTION_EVENTS` is set (line 2687).

**`requestDisallowInterceptTouchEvent(boolean)`** -- allows child views to prevent parent interception (commonly used in nested scrolling scenarios).

#### Focus Management (line 3306)

`requestFocus()` respects `descendantFocusability`:
- `FOCUS_BEFORE_DESCENDANTS`: ViewGroup tries to take focus first
- `FOCUS_AFTER_DESCENDANTS`: Children get priority
- `FOCUS_BLOCK_DESCENDANTS`: Only ViewGroup can receive focus

#### Design Patterns

- **Composite Pattern**: ViewGroup is a View that contains other Views, forming a tree
- **Chain of Responsibility**: Touch events flow through the view hierarchy via `dispatchTouchEvent()`
- **Template Method**: `onMeasure()`, `onLayout()`, `onDraw()` are template methods called by the framework
- **Observer Pattern**: `OnHierarchyChangeListener` for child add/remove notifications

---

## 3. Window and Window Management

### 3.1 Window.java

**File:** `frameworks/base/core/java/android/view/Window.java` (2,656 lines)

#### Class Purpose
`Window` is an abstract base class representing a top-level window with visual policy (background, title area, key processing). The only concrete implementation is `PhoneWindow` (line 64-65 Javadoc). Each Activity owns a Window, which in turn owns the root View hierarchy.

#### Key Features/Constants

**Window features (lines 69-165):**
| Constant | Value | Purpose |
|---|---|---|
| `FEATURE_NO_TITLE` | 1 | Removes title bar |
| `FEATURE_ACTION_BAR` | 8 | Enables Action Bar |
| `FEATURE_ACTION_BAR_OVERLAY` | 9 | Overlays Action Bar on content |
| `FEATURE_CONTENT_TRANSITIONS` | 12 | Enables content transition animations |
| `FEATURE_ACTIVITY_TRANSITIONS` | 13 | Enables Activity scene transitions |

#### Window.Callback Interface (starts ~line 430)

The `Callback` interface connects the Window to its host (typically Activity):
- `dispatchKeyEvent(KeyEvent)` -- Routes key events
- `dispatchTouchEvent(MotionEvent)` -- Routes touch events
- `onWindowFocusChanged(boolean)` -- Focus change notification
- `onAttachedToWindow()` / `onDetachedFromWindow()` -- Lifecycle callbacks
- `onWindowStartingActionMode(ActionMode.Callback, int)` -- Action mode management
- `onProvideKeyboardShortcuts(...)` -- Keyboard shortcut declarations

#### Key Public APIs
- `setContentView(int layoutResID)` / `setContentView(View view)` -- abstract, implemented by PhoneWindow
- `setStatusBarColor(int)`, `setNavigationBarColor(int)` -- system bar theming
- `requestFeature(int)` -- must be called before `setContentView()`
- `getDecorView()` -- returns the top-level DecorView
- `setWindowAnimations(int)` -- transition animations

---

### 3.2 WindowManager.java

**File:** `frameworks/base/core/java/android/view/WindowManager.java` (3,870 lines)

#### Class Purpose
`WindowManager` is the interface apps use to interact with the window manager service. It extends `ViewManager` (which defines `addView()`, `updateViewLayout()`, `removeView()`).

```java
@SystemService(Context.WINDOW_SERVICE)
public interface WindowManager extends ViewManager
```

#### Key APIs
- `getDefaultDisplay()` -- gets the Display this WindowManager is associated with
- `removeViewImmediate(View)` -- synchronous removal (vs. normal async `removeView()`)

#### WindowManager.LayoutParams (the bulk of the file)

The inner class `LayoutParams` extends `ViewGroup.LayoutParams` with window-specific attributes. Key window types (many are `@hide`):

**Application Windows (1-99):**
- `TYPE_BASE_APPLICATION` (1) -- base activity window
- `TYPE_APPLICATION` (2) -- normal application window
- `TYPE_APPLICATION_STARTING` (3) -- splash screen

**System Windows (2000+, mostly `@SystemApi` or `@hide`):**
- `TYPE_STATUS_BAR` (2000)
- `TYPE_SEARCH_BAR` (2001)
- `TYPE_TOAST` (2005)
- `TYPE_SYSTEM_OVERLAY` (2006, deprecated)
- `TYPE_APPLICATION_OVERLAY` (2038) -- the replacement for `SYSTEM_ALERT` for apps targeting O+

**Window Flags (bitmask):**
- `FLAG_NOT_FOCUSABLE` -- window doesn't receive input focus
- `FLAG_NOT_TOUCHABLE` -- window doesn't receive touch events
- `FLAG_FULLSCREEN` -- hide status bar
- `FLAG_KEEP_SCREEN_ON` -- prevent screen from sleeping
- `FLAG_HARDWARE_ACCELERATED` -- enable hardware-accelerated rendering
- `FLAG_SECURE` -- prevent screenshots/screen recording
- `FLAG_TRANSLUCENT_STATUS` / `FLAG_TRANSLUCENT_NAVIGATION` -- transparent bars

**Hidden transit constants (lines 137-225)** define window transition types (`TRANSIT_ACTIVITY_OPEN`, `TRANSIT_TASK_CLOSE`, etc.) used internally by the system for activity animations.

---

## 4. Layout Inflation

### LayoutInflater.java

**File:** `frameworks/base/core/java/android/view/LayoutInflater.java` (1,360 lines)

#### Class Purpose
Instantiates layout XML files into View object trees. Not used directly; obtained via `Activity.getLayoutInflater()` or `Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)`.

```java
@SystemService(Context.LAYOUT_INFLATER_SERVICE)
public abstract class LayoutInflater
```

#### Key Public APIs

**Inflate Methods:**
- `inflate(@LayoutRes int resource, @Nullable ViewGroup root)` (line 478)
- `inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)` (line 519)
- `inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot)` (line 627)

The `root` parameter serves dual purpose:
1. When `attachToRoot=true`: the inflated view is added to root
2. When `root != null` but `attachToRoot=false`: root provides LayoutParams for the inflated view's root element

**Factory System:**
- `setFactory(Factory factory)` -- can only be called once (line 106, `mFactorySet` guard)
- `setFactory2(Factory2 factory)` -- preferred; receives parent View context
- `cloneInContext(Context)` -- creates a new LayoutInflater for a different context

#### Design Patterns
- **Factory Method**: `Factory` / `Factory2` interfaces allow custom View creation
- **Flyweight**: `sConstructorMap` (line 135) caches `Constructor` objects to avoid repeated reflection

#### Performance Features
- **Precompiled layouts** (line 83-87): experimental feature to use precompiled DEX files instead of XML parsing at runtime
- **Constructor caching**: static `HashMap<String, Constructor<? extends View>>` avoids reflection overhead
- **Special tag handling**: `<merge>`, `<include>`, `<requestFocus>`, and `<tag>` are handled inline without constructor lookup

#### Notable Implementation Details
- View constructors must have the signature `(Context, AttributeSet)` (line 131-132)
- The special tag `<blink>` (line 144, `TAG_1995`) creates a BlinkLayout that blinks its children -- an easter egg referencing the HTML `<blink>` tag

---

## 5. Input and Event Handling

### 5.1 InputEvent.java

**File:** `frameworks/base/core/java/android/view/InputEvent.java` (272 lines)

#### Class Purpose
Abstract base class for all input events (key events and motion events). Implements `Parcelable` for IPC transport.

**Key APIs:**
- `getDeviceId()` -- source device ID
- `getDevice()` -- returns `InputDevice` object
- `getSource()` -- event source (touch, mouse, keyboard, etc.)
- `isFromSource(int source)` -- checks source with bitmask: `(getSource() & source) == source`
- `getEventTime()` -- timestamp in `uptimeMillis`

**Hidden APIs:**
- `setSource(int)`, `setDisplayId(int)` -- `@hide`, for framework internal use
- `getDisplayId()` -- `@hide`, identifies which display the event originated from
- `getId()` -- `@hide`, generated from CSPRNG for tracking across processes
- `recycle()` / `recycleIfNeededAfterDispatch()` -- `@hide`, object pooling support
- `isTainted()` / `setTainted(boolean)` -- `@hide`, marks inconsistent event sequences

**Design:** Uses atomic sequence numbers (`mNextSeq`) for local event ordering (line 35).

---

### 5.2 MotionEvent.java

**File:** `frameworks/base/core/java/android/view/MotionEvent.java` (4,179 lines)

#### Class Purpose
Reports movement events from touch screens, mice, pens, trackballs, and joysticks. Supports multi-touch via pointer tracking, and batches multiple `ACTION_MOVE` samples for efficiency.

#### Key Action Constants (lines 200-310)

| Constant | Value | 描述 |
|---|---|---|
| `ACTION_DOWN` | 0 | First finger touches screen |
| `ACTION_UP` | 1 | Last finger lifts from screen |
| `ACTION_MOVE` | 2 | Finger moves (may contain batched history) |
| `ACTION_CANCEL` | 3 | Gesture aborted |
| `ACTION_OUTSIDE` | 4 | Touch outside window bounds |
| `ACTION_POINTER_DOWN` | 5 | Additional finger touches screen |
| `ACTION_POINTER_UP` | 6 | Non-primary finger lifts |
| `ACTION_HOVER_MOVE` | 7 | Mouse/stylus movement without contact |
| `ACTION_SCROLL` | 8 | Scroll wheel / gesture |
| `ACTION_HOVER_ENTER` | 9 | Pointer enters view bounds |
| `ACTION_HOVER_EXIT` | 10 | Pointer leaves view bounds |
| `ACTION_BUTTON_PRESS` | 11 | Button pressed (mouse) |
| `ACTION_BUTTON_RELEASE` | 12 | Button released (mouse) |

**Multi-touch:** `ACTION_POINTER_DOWN/UP` encode the pointer index in the upper bits of `getAction()`. Always use `getActionMasked()` and `getActionIndex()` to decode.

#### Key APIs for Multi-Touch
- `getPointerCount()` -- number of active pointers
- `getPointerId(int pointerIndex)` -- stable ID for tracking across events
- `findPointerIndex(int pointerId)` -- reverse lookup: ID to current index
- `getX(int pointerIndex)`, `getY(int pointerIndex)` -- coordinates per pointer
- `getToolType(int pointerIndex)` -- `TOOL_TYPE_FINGER`, `TOOL_TYPE_STYLUS`, `TOOL_TYPE_MOUSE`
- `getPressure(int)`, `getSize(int)` -- pressure and contact area
- `getButtonState()` -- mouse button state

#### Historical Data (Batching)
For `ACTION_MOVE`, the system batches intermediate samples:
- `getHistorySize()` -- number of batched historical samples
- `getHistoricalX(int pointerIndex, int pos)`, `getHistoricalY(...)` -- past coordinates
- `getHistoricalEventTime(int pos)` -- timestamps of batched samples

Process all samples in time order: historical first, then current.

#### Performance Features
- Uses JNI with `@CriticalNative` and `@FastNative` annotations for coordinate accessors
- Object pooling via `obtain()` / `recycle()` pattern to minimize GC pressure
- Native backing store for event data (pointer coordinates stored in C++)

---

### 5.3 KeyEvent.java

**File:** `frameworks/base/core/java/android/view/KeyEvent.java` (3,166 lines)

#### Class Purpose
Represents hardware and virtual keyboard events. Contains the complete catalog of Android key codes.

**Key Actions:**
- `ACTION_DOWN` -- key pressed
- `ACTION_UP` -- key released
- `ACTION_MULTIPLE` -- repeated key or character sequence (legacy)

**Key Codes (hundreds defined, lines 88+):**
- Navigation: `KEYCODE_HOME` (3), `KEYCODE_BACK` (4), `KEYCODE_DPAD_*`
- Media: `KEYCODE_VOLUME_UP/DOWN`, `KEYCODE_MEDIA_PLAY/PAUSE`
- Alphabet: `KEYCODE_A` through `KEYCODE_Z`
- System: `KEYCODE_MENU`, `KEYCODE_SEARCH`, `KEYCODE_POWER`

**Important:** `KEYCODE_HOME` is "handled by the framework and is never delivered to applications" (line 101). Soft keyboard input is generally delivered via `InputConnection`, not `KeyEvent`.

**Key APIs:**
- `getKeyCode()`, `getScanCode()`, `getMetaState()`
- `getRepeatCount()` -- number of repeats for `ACTION_DOWN`
- `isShiftPressed()`, `isCtrlPressed()`, `isAltPressed()`
- `getUnicodeChar()` -- maps key code + meta state to Unicode character
- `dispatch(Callback, DispatcherState, Object)` -- dispatches to `Callback` interface

---

### 5.4 GestureDetector.java

**File:** `frameworks/base/core/java/android/view/GestureDetector.java` (920 lines)

#### Class Purpose
Detects higher-level gestures from raw `MotionEvent` streams. Provides callback interfaces for common gestures.

#### OnGestureListener Interface
- `onDown(MotionEvent)` -- every touch starts here
- `onShowPress(MotionEvent)` -- visual feedback tap (pre-click)
- `onSingleTapUp(MotionEvent)` -- single tap confirmed
- `onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)` -- drag/scroll
- `onLongPress(MotionEvent)` -- long press detected
- `onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)` -- fling gesture

#### OnDoubleTapListener Interface
- `onSingleTapConfirmed(MotionEvent)` -- single tap confirmed (no double-tap will follow)
- `onDoubleTap(MotionEvent)` -- double-tap detected
- `onDoubleTapEvent(MotionEvent)` -- events within a double-tap gesture

#### SimpleOnGestureListener
Convenience adapter class implementing all methods with no-op defaults, so you only override what you need.

**Usage Pattern:**
```java
GestureDetector detector = new GestureDetector(context, new SimpleOnGestureListener() {
    @Override public boolean onFling(...) { /* handle fling */ }
});
// In your View:
@Override public boolean onTouchEvent(MotionEvent event) {
    return detector.onTouchEvent(event);
}
```

---

### 5.5 Touch Event Dispatch Mechanism

The complete touch dispatch flow across the view hierarchy:

```
Activity.dispatchTouchEvent(MotionEvent)
  |
  +-- Window.superDispatchTouchEvent(MotionEvent)
       |
       +-- DecorView.superDispatchTouchEvent(MotionEvent)
            |
            +-- ViewGroup.dispatchTouchEvent(MotionEvent)    [2.2 above]
                 |
                 +-- onInterceptTouchEvent(MotionEvent)      [can steal events]
                 |
                 +-- child.dispatchTouchEvent(MotionEvent)   [recurse into children]
                      |
                      +-- View.dispatchTouchEvent(MotionEvent)  [line 14275]
                           |
                           +-- OnTouchListener.onTouch()     [listener first]
                           |
                           +-- onTouchEvent(MotionEvent)     [view handler second]
```

**Critical ordering in View.dispatchTouchEvent (line 14297-14311):**

1. Security filter: `onFilterTouchEventForSecurity()` -- drops events if window is obscured and `FILTER_TOUCHES_WHEN_OBSCURED` is set (tapjacking protection)
2. Scrollbar drag handling (if applicable)
3. `OnTouchListener.onTouch()` -- if returns `true`, event is consumed
4. `onTouchEvent()` -- only called if listener didn't consume

**Nested scrolling support** is integrated: `stopNestedScroll()` is called on `ACTION_DOWN` (line 14294) and on gesture end (lines 14321-14324).

---

## 6. Widget Library

### 6.1 TextView.java

**File:** `frameworks/base/core/java/android/widget/TextView.java` (13,705 lines)

#### Class Purpose
Displays text to the user and optionally allows editing. Base class for `EditText`, `Button`, and several other widgets. One of the largest and most complex widgets in the framework.

#### Key Public APIs
- `setText(CharSequence)` / `setText(@StringRes int)` -- set display text
- `getText()` -- returns current text (as `CharSequence`, may be `Spannable`)
- `setTextColor(int)`, `setTextSize(float)`, `setTypeface(Typeface)`
- `setGravity(int)` -- text alignment within the view
- `setInputType(int)` -- controls keyboard type for editing
- `setLines(int)`, `setMaxLines(int)`, `setEllipsize(TextUtils.TruncateAt)`
- `addTextChangedListener(TextWatcher)` -- observe text changes
- `setCompoundDrawables(...)` -- icon alongside text
- `setAutoSizeTextTypeUniformWithConfiguration(...)` -- auto-sizing text (API 26+)

#### Text Layout Engine
Internally uses `Layout` subclasses:
- `BoringLayout` -- optimized for single-line, LTR text
- `StaticLayout` -- immutable text layout
- `DynamicLayout` -- for editable text, updates layout incrementally

#### Performance Considerations
- Uses `PrecomputedText` (API 28+) to offload text measurement to background threads
- `BoringLayout` fast-path avoids full layout computation for simple text
- Font metric caching via `Paint.FontMetricsInt`

---

### 6.2 EditText.java

**File:** `frameworks/base/core/java/android/widget/EditText.java` (~160 lines)

#### Class Purpose
A thin veneer over `TextView` for text input. The source code comment (line 29) explicitly states: "This is supposed to be a *very* thin veneer over TextView."

**Key differences from TextView:**
- `getDefaultEditable()` returns `true` (line 96-98)
- `getFreezesText()` returns `true` (line 91-93) -- saves text state across configuration changes
- `getDefaultMovementMethod()` returns `ArrowKeyMovementMethod`
- `getText()` returns `Editable` (not just `CharSequence`)
- `setSelection(int start, int stop)` -- cursor/selection positioning
- `selectAll()`, `extendSelection(int index)`

**Design:** Demonstrates the Template Method pattern -- EditText overrides a few behavior hooks in TextView rather than reimplementing text editing.

---

### 6.3 Button.java

**File:** `frameworks/base/core/java/android/widget/Button.java` (~155 lines)

#### Class Purpose
A push button widget. Extends `TextView` with appropriate default styling. Annotated with `@RemoteView` for use in RemoteViews (widgets/notifications).

The actual button behavior (click handling, pressed state visual feedback) is entirely inherited from `View`. Button's role is primarily to apply the correct default style (`buttonStyle`).

**`onResolvePointerIcon()`** -- returns `PointerIcon.TYPE_HAND` for mouse hover (hand cursor).

---

### 6.4 ImageView.java

**File:** `frameworks/base/core/java/android/widget/ImageView.java` (1,730 lines)

#### Class Purpose
Displays image resources (Bitmaps, Drawables). Handles image scaling, tinting, and aspect ratio management.

#### Key Public APIs
- `setImageResource(@DrawableRes int)` -- set from resource ID
- `setImageDrawable(Drawable)` -- set from Drawable
- `setImageBitmap(Bitmap)` -- set from Bitmap (wraps in BitmapDrawable)
- `setImageURI(Uri)` -- load from content URI
- `setImageIcon(Icon)` -- set from Icon (API 23+)
- `setScaleType(ScaleType)` -- image scaling behavior
- `setImageTintList(ColorStateList)` -- color tinting
- `setAdjustViewBounds(boolean)` -- respect aspect ratio in measurement
- `setCropToPadding(boolean)` -- clip to padding area

#### ScaleType Enum
| Value | Behavior |
|---|---|
| `CENTER` | Center, no scaling |
| `CENTER_CROP` | Scale uniformly, fill entire view, crop excess |
| `CENTER_INSIDE` | Scale down to fit, center |
| `FIT_CENTER` | Scale to fit within bounds, center (default) |
| `FIT_START` | Scale to fit, align top/left |
| `FIT_END` | Scale to fit, align bottom/right |
| `FIT_XY` | Scale non-uniformly to fill exactly |
| `MATRIX` | Use custom matrix transformation |

#### Performance Considerations
- Image scaling uses a `Matrix` object that is computed once in `configureBounds()` and reused
- `setImageBitmap(null)` should be called to release references when done
- Large bitmaps should be sampled down via `BitmapFactory.Options.inSampleSize`

---

## 7. Layout Managers

### 7.1 FrameLayout.java

**File:** `frameworks/base/core/java/android/widget/FrameLayout.java` (500 lines)

#### Class Purpose
The simplest layout container. Designed to hold a single child, though multiple children stack with Z-ordering (most recently added on top). Size is determined by the largest child.

**Key APIs:**
- `setMeasureAllChildren(boolean)` / `setConsiderGoneChildrenWhenMeasuring(boolean)` -- whether GONE children affect size
- `setForegroundGravity(int)` -- positions the foreground drawable

**Measurement strategy:** Two passes when `match_parent` children exist. First pass measures all children normally. Second pass re-measures `match_parent` children using the maximum width/height found.

**Layout behavior:** Children are positioned according to their `layout_gravity` attribute (default: `Gravity.TOP | Gravity.START`, line 60).

---

### 7.2 LinearLayout.java

**File:** `frameworks/base/core/java/android/widget/LinearLayout.java` (2,099 lines)

#### Class Purpose
Arranges children in a single horizontal row or vertical column. Supports weight-based distribution of remaining space.

**Key APIs:**
- `setOrientation(int)` -- `HORIZONTAL` (0) or `VERTICAL` (1) (lines 96-97)
- `setGravity(int)` -- alignment of all children
- `setWeightSum(float)` -- total weight divisor
- `setDividerDrawable(Drawable)` -- visual divider between children
- `setShowDividers(int)` -- `SHOW_DIVIDER_NONE`, `SHOW_DIVIDER_BEGINNING`, `SHOW_DIVIDER_MIDDLE`, `SHOW_DIVIDER_END`
- `setBaselineAligned(boolean)` -- align children baselines (text alignment)

**Layout weight system:** Children with `layout_weight > 0` receive proportional shares of remaining space. When `weightSum` is set, weights are relative to that sum rather than the total of all child weights.

**Measurement performs up to two passes:**
1. First pass: measure all children with `0` weight normally
2. If any children have weight: redistribute remaining space proportionally and re-measure

**Performance note:** Nested weighted LinearLayouts (weight inside weight) cause exponential measurement -- each level doubles the measure passes. Consider `ConstraintLayout` (external library) or `RelativeLayout` for complex layouts.

---

### 7.3 RelativeLayout.java

**File:** `frameworks/base/core/java/android/widget/RelativeLayout.java` (2,081 lines)

#### Class Purpose
Positions children relative to each other or to the parent edges. Uses a directed acyclic graph (DAG) of layout rules.

**Key Rule Constants (lines 91+):**
| Constant | 描述 |
|---|---|
| `LEFT_OF` / `RIGHT_OF` | Position relative to sibling |
| `ABOVE` / `BELOW` | Position relative to sibling |
| `ALIGN_LEFT` / `ALIGN_RIGHT` / `ALIGN_TOP` / `ALIGN_BOTTOM` | Align edge with sibling |
| `ALIGN_PARENT_LEFT` / `ALIGN_PARENT_RIGHT` / `ALIGN_PARENT_TOP` / `ALIGN_PARENT_BOTTOM` | Align with parent edge |
| `CENTER_IN_PARENT` / `CENTER_HORIZONTAL` / `CENTER_VERTICAL` | Centering rules |
| `START_OF` / `END_OF` / `ALIGN_START` / `ALIGN_END` | RTL-aware variants (API 17+) |

**Measurement strategy:**
1. Sort children by dependency order using topological sort
2. Measure children in two passes: horizontal rules, then vertical rules
3. Children are measured based on resolved positions of their dependencies

**Known bug note (lines 63-76):** In API 17 and below, RelativeLayout had a measurement bug where children in scrolling containers would receive `AT_MOST` specs instead of `UNSPECIFIED`. This is preserved for backward compatibility with `targetSdkVersion <= 17`.

**Design Pattern:** DAG dependency resolution -- uses `DependencyGraph` internally with topological sorting for layout order.

---

### 7.4 ConstraintLayout

**Not present in the AOSP framework.** ConstraintLayout is distributed as a Jetpack (AndroidX) library, not part of the core framework. Apps include it via `implementation 'androidx.constraintlayout:constraintlayout:x.y.z'`.

---

## 8. Animation Framework

### 8.1 Animator.java

**File:** `frameworks/base/core/java/android/animation/Animator.java` (~400 lines)

#### Class Purpose
Abstract base class for all property-based animators. Defines the core animation lifecycle and listener interfaces.

**Key APIs:**
- `start()`, `cancel()`, `end()` -- lifecycle control
- `pause()` / `resume()` (line 113+) -- pause/resume without resetting
- `isStarted()`, `isRunning()`, `isPaused()`
- `setDuration(long)`, `getDuration()`
- `setStartDelay(long)`
- `setInterpolator(TimeInterpolator)`
- `addListener(AnimatorListener)` -- lifecycle callbacks
- `addPauseListener(AnimatorPauseListener)` -- pause/resume callbacks
- `clone()` -- deep copy support

**AnimatorListener interface:**
- `onAnimationStart(Animator)`
- `onAnimationEnd(Animator)`
- `onAnimationCancel(Animator)`
- `onAnimationRepeat(Animator)`

**AnimatorListenerAdapter** (separate file) provides empty default implementations.

**Threading rule:** Animations must be started on a thread with a `Looper`. Property animations modifying views must run on the UI thread.

---

### 8.2 ValueAnimator.java

**File:** `frameworks/base/core/java/android/animation/ValueAnimator.java` (~1,600 lines)

#### Class Purpose
Provides a timing engine that calculates animated values over time. Does not directly modify any object -- the caller registers an `UpdateListener` and applies values manually.

```java
public class ValueAnimator extends Animator
        implements AnimationHandler.AnimationFrameCallback
```

#### Key Factory Methods (lines 356-433)
- `ofInt(int... values)` -- integer interpolation
- `ofFloat(float... values)` -- float interpolation
- `ofArgb(int... values)` -- ARGB color interpolation
- `ofObject(TypeEvaluator evaluator, Object... values)` -- custom type interpolation
- `ofPropertyValuesHolder(PropertyValuesHolder... values)` -- multi-property animation

#### Key Configuration APIs
- `setDuration(long)` (line 596) -- animation duration in ms
- `setRepeatCount(int)` (line 899) -- `INFINITE` or a specific count
- `setRepeatMode(int)` (line 919) -- `RESTART` or `REVERSE`
- `setInterpolator(TimeInterpolator)` (line 985) -- default: `AccelerateDecelerateInterpolator`
- `setStartDelay(long)` (line 802)
- `addUpdateListener(AnimatorUpdateListener)` (line 940) -- called on each frame

#### Observing Animated Values
- `getAnimatedValue()` (line 863) -- current interpolated value
- `getAnimatedFraction()` (line 1538) -- current progress 0.0 to 1.0

#### System-wide Duration Scale
```java
private static float sDurationScale = 1.0f;  // line 88
```
This hidden field controls the global animation speed. When set to 0 (e.g., in Developer Options "Animator duration scale"), all animations complete instantly. `areAnimatorsEnabled()` checks if `sDurationScale != 0`.

#### Performance
- Driven by `AnimationHandler` which uses `Choreographer.postFrameCallback()` for VSYNC-aligned updates
- Uses `PropertyValuesHolder` internally for efficient value computation
- Specialized `FloatKeyframeSet` and `IntKeyframeSet` avoid boxing overhead

---

### 8.3 ObjectAnimator.java

**File:** `frameworks/base/core/java/android/animation/ObjectAnimator.java` (~800 lines)

#### Class Purpose
Extends `ValueAnimator` to automatically set animated values on a target object's property via reflection or `Property` objects.

```java
public final class ObjectAnimator extends ValueAnimator
```

#### Key Factory Methods
- `ofFloat(Object target, String propertyName, float... values)`
- `ofInt(Object target, String propertyName, int... values)`
- `ofArgb(Object target, String propertyName, int... values)`
- `ofObject(Object target, String propertyName, TypeEvaluator evaluator, Object... values)`
- `ofFloat(Object target, Property<?, Float> property, float... values)` -- type-safe variant
- `ofMultiFloat(Object target, String propertyName, Path path)` -- animate along a path

#### Property Resolution
Given a property name `"foo"`, ObjectAnimator:
1. Looks for `setFoo(float)` or `setFoo(int)` for the setter
2. Looks for `getFoo()` for the getter (when start value is needed)
3. Uses `Property<T, V>` objects for direct access when available (better performance)

**Performance note (lines 96-101):** "For best performance... use `float` or `int` typed values, and make the setter function have a `void` return value." This enables an optimized JNI path.

**Target reference:** Uses `WeakReference<Object>` for the target (line 80), preventing memory leaks. Animation auto-cancels if target is GC'd.

**Auto-cancel (line 86):** `setAutoCancel(true)` causes the animator to cancel any running animation on the same target+property combination when started.

---

### 8.4 AnimatorSet.java

**File:** `frameworks/base/core/java/android/animation/AnimatorSet.java` (~1,200 lines)

#### Class Purpose
Plays a set of `Animator` objects in a specified order: together, sequentially, or with custom dependencies.

```java
public final class AnimatorSet extends Animator
        implements AnimationHandler.AnimationFrameCallback
```

#### Key APIs
- `playTogether(Animator... items)` -- play all animations simultaneously
- `playSequentially(Animator... items)` -- play animations one after another
- `play(Animator)` -- returns a `Builder` for chaining dependencies

#### Builder Pattern (fluent API)
```java
AnimatorSet set = new AnimatorSet();
set.play(anim1).with(anim2).before(anim3).after(anim4);
```
- `with(Animator)` -- play simultaneously
- `before(Animator)` -- this animation plays before the argument
- `after(Animator)` -- this animation plays after the argument
- `after(long delay)` -- insert a delay

#### Internal Architecture
Uses a dependency graph based on `Node` objects (line 94) and `AnimationEvent` sorted timeline (line 87). The `mNodeMap` (`ArrayMap<Animator, Node>`, line 82) maps each child animator to its dependency node.

Circular dependency detection is mentioned in documentation (lines 47-51) but results in "undefined" behavior rather than throwing an exception.

---

### 8.5 ViewPropertyAnimator

**File:** `frameworks/base/core/java/android/view/ViewPropertyAnimator.java` (~1,000 lines)

#### Class Purpose
Provides an optimized, fluent API for animating View properties. Obtained via `view.animate()`.

#### Fluent API Example
```java
view.animate()
    .alpha(0f)
    .translationX(100f)
    .scaleX(0.5f)
    .setDuration(300)
    .setInterpolator(new AccelerateInterpolator())
    .withEndAction(() -> view.setVisibility(View.GONE))
    .start();
```

#### Animatable Properties
- `alpha(float)`, `alphaBy(float)`
- `translationX/Y/Z(float)`, `translationXBy/YBy/ZBy(float)`
- `rotation(float)`, `rotationX/Y(float)`
- `scaleX/Y(float)`, `scaleXBy/YBy(float)`
- `x(float)`, `y(float)`, `z(float)` -- absolute position

#### Performance Advantage
"This class may provide better performance for several simultaneous animations, because it will optimize invalidate calls to take place only once for several properties instead of each animated property independently causing its own invalidation" (lines 37-40).

Internally batches property changes and uses `RenderNode` properties for hardware-accelerated updates that bypass the main `draw()` path entirely.

---

## 9. Graphics Subsystem

### 9.1 Canvas.java

**File:** `frameworks/base/graphics/java/android/graphics/Canvas.java` (2,327 lines)

#### Class Purpose
Hosts "draw" calls for 2D rendering. Requires a target `Bitmap` (software) or is backed by a `RenderNode` (hardware-accelerated). Inherits actual draw methods from `BaseCanvas`.

#### Key Drawing Methods (from BaseCanvas)
- `drawColor(int color)`, `drawColor(int color, BlendMode mode)` -- fill entire canvas
- `drawRect(float l, float t, float r, float b, Paint)` -- rectangle
- `drawRoundRect(float l, float t, float r, float b, float rx, float ry, Paint)` -- rounded rectangle
- `drawCircle(float cx, float cy, float radius, Paint)` -- circle
- `drawOval(float l, float t, float r, float b, Paint)` -- oval
- `drawArc(...)` -- arc segment
- `drawLine(float startX, float startY, float stopX, float stopY, Paint)` -- line
- `drawPath(Path, Paint)` -- arbitrary path
- `drawText(String, float x, float y, Paint)` -- text rendering
- `drawBitmap(Bitmap, float left, float top, Paint)` -- bitmap rendering
- `drawBitmap(Bitmap, Rect src, Rect dst, Paint)` -- bitmap with source/dest rects
- `drawBitmapMesh(...)` -- distorted bitmap via mesh

#### Canvas State Management
- `save()` / `restore()` -- save/restore transformation and clip state
- `saveLayer(...)` -- save to off-screen buffer for blend mode effects
- `translate(float dx, float dy)` -- translate origin
- `rotate(float degrees)` -- rotate
- `scale(float sx, float sy)` -- scale
- `skew(float sx, float sy)` -- skew
- `concat(Matrix)` -- apply arbitrary matrix
- `clipRect(...)`, `clipPath(...)` -- clipping

#### Maximum Bitmap Size
```java
private static final int MAXMIMUM_BITMAP_SIZE = 32766;  // line 77
```
Note: the constant name has a typo ("MAXMIMUM" instead of "MAXIMUM").

#### Hardware Acceleration
`isHardwareAccelerated()` distinguishes software Canvas (backed by Bitmap) from hardware Canvas (backed by RenderNode/GPU). Some operations behave differently or are unsupported in hardware mode.

---

### 9.2 Paint.java

**File:** `frameworks/base/graphics/java/android/graphics/Paint.java` (3,218 lines)

#### Class Purpose
Holds style, color, and effect information for drawing operations.

#### Key Properties
- **Color:** `setColor(int)`, `setARGB(int a, int r, int g, int b)`
- **Style:** `setStyle(Style)` -- `FILL`, `STROKE`, `FILL_AND_STROKE`
- **Stroke:** `setStrokeWidth(float)`, `setStrokeCap(Cap)`, `setStrokeJoin(Join)`
- **Anti-aliasing:** `setAntiAlias(boolean)` -- smoothed edges
- **Text:** `setTextSize(float)`, `setTypeface(Typeface)`, `setTextAlign(Align)`
- **Effects:** `setShader(Shader)`, `setColorFilter(ColorFilter)`, `setPathEffect(PathEffect)`, `setMaskFilter(MaskFilter)`
- **Blend:** `setXfermode(Xfermode)`, `setBlendMode(BlendMode)` -- compositing modes
- **Shadow:** `setShadowLayer(float radius, float dx, float dy, int color)`
- **Subpixel:** `setSubpixelText(boolean)` -- subpixel text rendering

#### Text Measurement
- `measureText(String)` -- returns text width in pixels
- `getTextBounds(String, int start, int end, Rect bounds)` -- tight bounding box
- `getFontMetrics()` / `getFontMetricsInt()` -- ascent, descent, leading
- `breakText(String, boolean, float maxWidth, float[] measuredWidth)` -- line-breaking helper

#### Performance
- Native implementation via JNI with `@CriticalNative` annotations for frequent operations
- `NativeAllocationRegistry` for deterministic native memory management
- Locale caching via `sMinikinLocaleListIdCache` (line 98) to avoid repeated string-to-ID conversion

---

### 9.3 Bitmap.java

**File:** `frameworks/base/graphics/java/android/graphics/Bitmap.java` (2,335 lines)

#### Class Purpose
Represents a 2D pixel array. The most memory-intensive graphics object in typical Android apps.

#### Key Factory Methods
- `createBitmap(int width, int height, Config config)` -- empty bitmap
- `createBitmap(Bitmap src)` -- copy
- `createBitmap(Bitmap src, int x, int y, int width, int height)` -- sub-region
- `createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)` -- rescaled
- `BitmapFactory.decodeResource(...)`, `BitmapFactory.decodeFile(...)` -- from file/resource

#### Config Enum
| Config | Bits/Pixel | 描述 |
|---|---|---|
| `ALPHA_8` | 8 | Alpha channel only |
| `RGB_565` | 16 | No alpha, reduced color depth |
| `ARGB_4444` | 16 | Deprecated, poor quality |
| `ARGB_8888` | 32 | Full color + alpha (default) |
| `RGBA_F16` | 64 | Wide color gamut, HDR |
| `HARDWARE` | varies | GPU-only bitmap, no pixel access |

#### Key APIs
- `getWidth()`, `getHeight()`, `getConfig()`
- `getPixel(int x, int y)`, `setPixel(int x, int y, int color)` -- per-pixel access
- `getPixels(int[] pixels, ...)`, `setPixels(int[] pixels, ...)` -- bulk pixel access
- `compress(CompressFormat, int quality, OutputStream)` -- encode to PNG/JPEG/WEBP
- `recycle()` -- release native memory (important for memory management)
- `isRecycled()` -- check if recycled
- `copy(Config, boolean mutable)` -- copy with different config
- `isMutable()` -- whether pixels can be modified
- `setDensity(int)` -- set display density for scaling

#### Memory Management
- `getAllocationByteCount()` -- actual native memory size
- `getByteCount()` -- logical size (may differ for reused bitmaps)
- Hardware bitmaps (`Config.HARDWARE`) live in GPU memory and cannot have pixels read/written
- `prepareToDraw()` -- hint to pre-upload texture to GPU

#### Performance Considerations
- **Always recycle** bitmaps when done, or use `BitmapFactory.Options.inBitmap` to reuse allocations
- **Sample down** large images: set `BitmapFactory.Options.inSampleSize` to reduce memory
- **Use HARDWARE config** for display-only bitmaps to avoid CPU-side memory
- **Density awareness**: Bitmap density (`mDensity`) affects how the bitmap is scaled when drawn on displays with different densities

---

### 9.4 Drawable.java

**File:** `frameworks/base/graphics/java/android/graphics/drawable/Drawable.java` (1,737 lines)

#### Class Purpose
Abstract base for "something that can be drawn." Unlike View, a Drawable has no event-handling facility. Used for backgrounds, icons, state-dependent graphics, etc.

#### Key Abstract Methods
- `draw(Canvas canvas)` -- render the drawable
- `setAlpha(@IntRange(from=0, to=255) int alpha)` -- transparency
- `setColorFilter(@Nullable ColorFilter colorFilter)` -- color modification
- `getOpacity()` -- `PixelFormat.OPAQUE`, `TRANSLUCENT`, `TRANSPARENT`, or `UNKNOWN`

#### Important Concrete Methods
- `setBounds(int left, int top, int right, int bottom)` -- MUST be called before drawing
- `getBounds()` -- returns the drawing bounds
- `setState(int[] stateSet)` -- pressed, focused, selected, etc.
- `setLevel(int level)` -- continuous value (0-10000) for things like progress
- `setTint(int)`, `setTintList(ColorStateList)`, `setTintMode(BlendMode)` -- color tinting
- `getIntrinsicWidth()`, `getIntrinsicHeight()` -- preferred size (-1 if none)
- `getPadding(Rect)` -- content padding
- `setCallback(Callback)` -- for invalidation notifications (View implements `Drawable.Callback`)
- `mutate()` -- create a mutable copy (shared constant state becomes independent)

#### Key Subclasses (in android.graphics.drawable)
- `BitmapDrawable` -- wraps a Bitmap
- `ColorDrawable` -- solid color fill
- `GradientDrawable` -- shapes with gradients, strokes, corners
- `ShapeDrawable` -- programmable shapes
- `LayerDrawable` -- stacked drawables
- `StateListDrawable` -- state-dependent drawable selection
- `AnimatedVectorDrawable` -- animated vector graphics
- `VectorDrawable` -- resolution-independent vector graphics
- `NinePatchDrawable` -- stretchable 9-patch images
- `RippleDrawable` -- Material Design ripple effect
- `TransitionDrawable` -- cross-fade between two drawables
- `InsetDrawable`, `ScaleDrawable`, `ClipDrawable`, `RotateDrawable` -- transforming wrappers

#### State Management (Observer Pattern)
```
View implements Drawable.Callback {
    invalidateDrawable(Drawable who) --> invalidate()
    scheduleDrawable(Drawable who, Runnable what, long when) --> postDelayed()
    unscheduleDrawable(Drawable who, Runnable what) --> removeCallbacks()
}
```
When a Drawable's state changes (e.g., animated frame advance), it calls `invalidateSelf()`, which notifies its callback (the View), which triggers a redraw.

#### ConstantState Pattern
Drawables use a `ConstantState` inner class to share immutable data between instances inflated from the same resource. This saves memory when the same drawable resource is used by multiple views. `mutate()` breaks this sharing when individual modification is needed.

---

## 10. Surface Rendering

### 10.1 SurfaceView.java

**File:** `frameworks/base/core/java/android/view/SurfaceView.java` (1,888 lines)

#### Class Purpose
Provides a dedicated drawing surface embedded in the view hierarchy. The surface exists in a separate window layer behind (or in front of) the main window. SurfaceView "punches a hole" in the parent window to make its surface visible.

**Key characteristics:**
- The surface is rendered independently from the view hierarchy
- Can be drawn on from any thread (not limited to UI thread)
- Z-ordered behind the window by default (can be placed on top)
- Does NOT support View animations, transforms, or alpha blending with siblings (use TextureView instead)

#### Key APIs
- `getHolder()` -- returns `SurfaceHolder` for surface access
- `SurfaceHolder.addCallback(SurfaceHolder.Callback)` -- lifecycle events:
  - `surfaceCreated(SurfaceHolder)` -- surface ready for drawing
  - `surfaceChanged(SurfaceHolder, int format, int width, int height)` -- size/format changed
  - `surfaceDestroyed(SurfaceHolder)` -- surface being released
- `SurfaceHolder.lockCanvas()` / `unlockCanvasAndPost(Canvas)` -- software rendering
- `SurfaceHolder.getSurface()` -- get `Surface` for OpenGL/MediaCodec use
- `setZOrderOnTop(boolean)` -- place surface above the window
- `setZOrderMediaOverlay(boolean)` -- place above media but below the window
- `setSecure(boolean)` -- prevent screenshots

#### Use Cases
- Camera preview
- Video playback
- Game rendering (OpenGL ES)
- Media codec output

---

### 10.2 TextureView.java

**File:** `frameworks/base/core/java/android/view/TextureView.java` (862 lines)

#### Class Purpose
Displays a content stream (video, OpenGL scene, camera) as a regular View within the hierarchy. Unlike SurfaceView, TextureView participates in normal View rendering and supports transforms, alpha, and animations.

**Key requirement:** TextureView can ONLY be used in a hardware-accelerated window. In software rendering, it draws nothing (line 39-40).

#### Key APIs
- `setSurfaceTextureListener(SurfaceTextureListener)` -- lifecycle callbacks:
  - `onSurfaceTextureAvailable(SurfaceTexture, int width, int height)`
  - `onSurfaceTextureSizeChanged(SurfaceTexture, int width, int height)`
  - `onSurfaceTextureDestroyed(SurfaceTexture)` -- return `true` to release
  - `onSurfaceTextureUpdated(SurfaceTexture)` -- new frame available
- `getSurfaceTexture()` -- access the underlying `SurfaceTexture`
- `getBitmap()` -- capture current content as a Bitmap
- `lockCanvas()` / `unlockCanvasAndPost(Canvas)` -- software rendering
- `setTransform(Matrix)` -- custom transformation matrix
- `setOpaque(boolean)` -- whether content is opaque

#### SurfaceView vs TextureView

| 功能 | SurfaceView | TextureView |
|---|---|---|
| View animations | No | Yes |
| Alpha/transforms | No | Yes |
| Thread safety | Any thread | UI thread for lockCanvas |
| Compositing | Separate layer | Same layer as views |
| Performance | Better (dedicated surface) | Slightly worse (GPU compositing) |
| HW acceleration required | No | Yes |
| DRM content | Yes | Difficult |
| Available since | API 1 | API 14 |

---

### 10.3 Choreographer.java

**File:** `frameworks/base/core/java/android/view/Choreographer.java` (1,046 lines)

#### Class Purpose
Coordinates the timing of animations, input, and drawing with display VSYNC. Each `Looper` thread has its own Choreographer instance.

```java
public final class Choreographer
```

#### Key APIs
- `getInstance()` -- get the Choreographer for the current thread
- `postFrameCallback(FrameCallback callback)` -- schedule work for next frame
- `postFrameCallbackDelayed(FrameCallback callback, long delayMillis)` -- delayed callback
- `removeFrameCallback(FrameCallback callback)` -- cancel a pending callback

#### FrameCallback Interface
```java
public interface FrameCallback {
    void doFrame(long frameTimeNanos);
}
```

#### Internal Frame Processing Order
The Choreographer processes callbacks in four phases per frame:
1. **Input** (`CALLBACK_INPUT`) -- input events
2. **Animation** (`CALLBACK_ANIMATION`) -- animation frame updates
3. **Insets animation** (`CALLBACK_INSETS_ANIMATION`) -- window insets animation
4. **Traversal** (`CALLBACK_TRAVERSAL`) -- measure/layout/draw

#### VSYNC Integration
- Receives VSYNC signals from `DisplayEventReceiver`
- `DEFAULT_FRAME_DELAY = 10ms` (line 99) -- fallback when VSYNC is unavailable
- Detects and logs jank (skipped frames) when `DEBUG_JANK` is enabled (line 86)

#### Performance
- Uses `FrameInfo` for tracking frame timing
- Integrates with `Trace` for systrace visualization
- Thread-local singleton pattern avoids synchronization overhead

---

## 11. RecyclerView (Internal)

**File:** `frameworks/base/core/java/com/android/internal/widget/RecyclerView.java` (12,264 lines)

#### Important Note
This is an **internal copy** at `com.android.internal.widget.RecyclerView`, not the public AndroidX `RecyclerView`. The public version is in the AndroidX library (`androidx.recyclerview:recyclerview`).

The internal copy is used by the system UI (settings, notification shade, etc.) and is not part of the public SDK. App developers should always use the AndroidX version.

The internal version follows the same architecture:
- `LayoutManager` for positioning items
- `Adapter` for binding data to views
- `ViewHolder` pattern for view recycling
- `ItemDecoration` for visual additions
- `ItemAnimator` for add/remove/change animations

---

## 12. Design Patterns Summary

| Pattern | Where Applied | 描述 |
|---|---|---|
| **Composite** | View/ViewGroup | Tree structure where ViewGroup contains Views |
| **Chain of Responsibility** | Touch dispatch | Events flow through `dispatchTouchEvent()` chain |
| **Template Method** | `onMeasure()`, `onLayout()`, `onDraw()` | Framework calls these; subclasses override |
| **Observer** | Listener interfaces, `Drawable.Callback` | Event notification via callbacks |
| **Factory Method** | `LayoutInflater.Factory/Factory2` | Custom view creation during inflation |
| **Strategy** | `LayoutManager` (RecyclerView), `ScaleType` (ImageView) | Interchangeable algorithms |
| **Flyweight** | `LayoutInflater.sConstructorMap`, `MotionEvent.obtain()` | Object sharing/pooling |
| **Builder** | `AnimatorSet.Builder`, `ViewPropertyAnimator` | Fluent construction APIs |
| **Decorator** | `InsetDrawable`, `ScaleDrawable`, wrapping Drawables | Add behavior to objects dynamically |
| **State** | `StateListDrawable`, `StateListAnimator` | Behavior varies with state |
| **Bridge** | `Canvas` (Java) <-> native Skia | Abstraction/implementation separation |
| **Proxy** | `Bitmap.HARDWARE` config | GPU-resident bitmaps restrict operations |
| **MVC** | View/Adapter separation in list views | Model-View separation |
| **WeakReference** | `ObjectAnimator.mTarget` | Prevent animation memory leaks |

---

## 13. Performance Considerations

### Measurement and Layout

1. **Avoid deep view hierarchies** -- each level multiplies measurement work. Flat hierarchies (ConstraintLayout) are preferred.
2. **Avoid nested LinearLayouts with weights** -- causes exponential measurement passes.
3. **`requestLayout()` propagates to root** -- every call triggers a full measure/layout pass from the top. Batch layout changes within a single frame.
4. **MeasureSpec cache** (View line 25440) helps avoid redundant measurement, but only when specs haven't changed.
5. **`GONE` views** are still measured in some layouts (e.g., FrameLayout with `measureAllChildren`).

### Drawing

1. **`invalidate()` vs `postInvalidateOnAnimation()`** -- prefer `postInvalidateOnAnimation()` to coalesce with the next VSYNC frame.
2. **Avoid allocations in `onDraw()`** -- Paint, Rect, Path objects should be pre-allocated as instance fields.
3. **Hardware acceleration** -- enabled by default (API 14+). Some Canvas operations are unsupported (e.g., `drawBitmapMesh` with colors, some `clipPath` cases).
4. **`setWillNotDraw(true)`** -- skip draw traversal for ViewGroups that don't draw themselves.
5. **`setLayerType(LAYER_TYPE_HARDWARE, paint)`** -- cache complex views as GPU textures for animation. Must call `setLayerType(LAYER_TYPE_NONE, null)` when done to free GPU memory.

### Touch Events

1. **MotionEvent batching** -- always process `getHistorySize()` historical samples for smooth drawing/scrolling.
2. **`onInterceptTouchEvent()` performance** -- called for every event in the gesture. Keep it lightweight.
3. **Avoid allocating MotionEvents** -- use `obtain()` / `recycle()` when creating synthetic events.

### Animation

1. **ViewPropertyAnimator** is faster than individual ObjectAnimators for multiple properties -- it batches invalidation.
2. **Check `areAnimatorsEnabled()`** -- animations may be disabled in Developer Options. Animation logic should handle instant completion.
3. **Avoid `setLayerType(HARDWARE)` during animation** -- `ViewPropertyAnimator` and `ObjectAnimator` on RenderNode properties (alpha, translation, rotation, scale) are already hardware-accelerated without explicit layer types.
4. **Cancel animations** in `onDetachedFromWindow()` or `onPause()` to prevent leaks and wasted CPU.

### Bitmap Memory

1. **Use `inSampleSize`** to decode large images at reduced resolution.
2. **Use `Config.HARDWARE`** for display-only bitmaps (API 26+).
3. **Use `inBitmap`** for bitmap reuse to avoid allocation churn.
4. **Call `recycle()`** when done, especially before Android 8.0 (where Bitmap pixel data lived in the Java heap).
5. **Prefer `ImageDecoder`** (API 28+) over `BitmapFactory` for modern decoding with better error handling and header-first decoding.

---

## 14. Hidden and System API Summary

The View framework contains extensive hidden APIs used by the system UI and privileged apps. Key categories:

### View.java Hidden APIs
- **Private flags**: `mPrivateFlags`, `mPrivateFlags2`, `mPrivateFlags3` -- internal state bitmasks controlling layout, drawing, and focus state. Over 50 `PFLAG_*` constants.
- **System visibility**: `setSystemUiVisibility(int)` flags -- `SYSTEM_UI_FLAG_FULLSCREEN`, `SYSTEM_UI_FLAG_HIDE_NAVIGATION`, `SYSTEM_UI_FLAG_IMMERSIVE_STICKY`
- **`@UnsupportedAppUsage` fields**: `mAttachInfo`, `mParent`, `mLeft/mTop/mRight/mBottom`, `mScrollX/mScrollY` -- frequently accessed via reflection by apps despite being non-public.
- **Autofill integration**: `onProvideAutofillStructure()`, `autofill(AutofillValue)`, `getAutofillId()`
- **Content capture**: `notifyAppearedOrDisappearedForContentCaptureIfNeeded()`
- **Scroll capture** (Android 11): `ScrollCaptureInternal` integration for long screenshots

### WindowManager.java Hidden APIs
- **Window transition types**: `TRANSIT_ACTIVITY_OPEN`, `TRANSIT_TASK_CLOSE`, etc. -- 20+ hidden constants for system-managed window transitions
- **Input consumers**: `INPUT_CONSUMER_PIP`, `INPUT_CONSUMER_NAVIGATION` -- named input consumers for system gesture handling
- **Docking constants**: `DOCKED_LEFT`, `DOCKED_TOP`, etc. -- split-screen positioning
- **LayoutParams private flags**: `PRIVATE_FLAG_*` constants for system window behaviors

### InputEvent.java Hidden APIs
- `setSource(int)`, `setDisplayId(int)` -- event routing modification
- `copy()`, `recycle()`, `prepareForReuse()` -- object pool management
- `isTainted()` / `setTainted(boolean)` -- event consistency tracking
- `getEventTimeNano()` -- nanosecond precision timestamps
- `cancel()` -- marks event as canceled

### MotionEvent.java Hidden APIs
- `@CriticalNative` and `@FastNative` JNI methods for coordinate access
- `setTargetAccessibilityFocus(boolean)` -- accessibility focus routing
- `isTargetAccessibilityFocus()` -- check accessibility targeting
- `FLAG_WINDOW_IS_OBSCURED` -- tapjacking detection flag

### Animation Hidden APIs
- `ValueAnimator.sDurationScale` -- system-wide animation speed multiplier (`@UnsupportedAppUsage`)
- `AnimationHandler` -- internal frame scheduling not exposed to apps
- `RevealAnimator` -- circular reveal implementation detail

### Bitmap Hidden APIs
- `mNativePtr` -- direct native pointer (`@UnsupportedAppUsage`)
- `mNinePatchChunk`, `mNinePatchInsets` -- 9-patch metadata
- `createGraphicBufferHandle()` -- GPU buffer interop
- `BLASTBufferQueue` -- new buffer queue implementation in Android 11

---

## File Reference Index

| File | Path | Lines |
|---|---|---|
| View.java | `frameworks/base/core/java/android/view/View.java` | 30,408 |
| ViewGroup.java | `frameworks/base/core/java/android/view/ViewGroup.java` | 9,277 |
| Window.java | `frameworks/base/core/java/android/view/Window.java` | 2,656 |
| WindowManager.java | `frameworks/base/core/java/android/view/WindowManager.java` | 3,870 |
| LayoutInflater.java | `frameworks/base/core/java/android/view/LayoutInflater.java` | 1,360 |
| InputEvent.java | `frameworks/base/core/java/android/view/InputEvent.java` | 272 |
| MotionEvent.java | `frameworks/base/core/java/android/view/MotionEvent.java` | 4,179 |
| KeyEvent.java | `frameworks/base/core/java/android/view/KeyEvent.java` | 3,166 |
| GestureDetector.java | `frameworks/base/core/java/android/view/GestureDetector.java` | 920 |
| Choreographer.java | `frameworks/base/core/java/android/view/Choreographer.java` | 1,046 |
| ViewPropertyAnimator.java | `frameworks/base/core/java/android/view/ViewPropertyAnimator.java` | ~1,000 |
| SurfaceView.java | `frameworks/base/core/java/android/view/SurfaceView.java` | 1,888 |
| TextureView.java | `frameworks/base/core/java/android/view/TextureView.java` | 862 |
| TextView.java | `frameworks/base/core/java/android/widget/TextView.java` | 13,705 |
| EditText.java | `frameworks/base/core/java/android/widget/EditText.java` | ~160 |
| Button.java | `frameworks/base/core/java/android/widget/Button.java` | ~155 |
| ImageView.java | `frameworks/base/core/java/android/widget/ImageView.java` | 1,730 |
| FrameLayout.java | `frameworks/base/core/java/android/widget/FrameLayout.java` | 500 |
| LinearLayout.java | `frameworks/base/core/java/android/widget/LinearLayout.java` | 2,099 |
| RelativeLayout.java | `frameworks/base/core/java/android/widget/RelativeLayout.java` | 2,081 |
| Animator.java | `frameworks/base/core/java/android/animation/Animator.java` | ~400 |
| ValueAnimator.java | `frameworks/base/core/java/android/animation/ValueAnimator.java` | ~1,600 |
| ObjectAnimator.java | `frameworks/base/core/java/android/animation/ObjectAnimator.java` | ~800 |
| AnimatorSet.java | `frameworks/base/core/java/android/animation/AnimatorSet.java` | ~1,200 |
| Canvas.java | `frameworks/base/graphics/java/android/graphics/Canvas.java` | 2,327 |
| Paint.java | `frameworks/base/graphics/java/android/graphics/Paint.java` | 3,218 |
| Bitmap.java | `frameworks/base/graphics/java/android/graphics/Bitmap.java` | 2,335 |
| Drawable.java | `frameworks/base/graphics/java/android/graphics/drawable/Drawable.java` | 1,737 |
| RecyclerView.java (internal) | `frameworks/base/core/java/com/android/internal/widget/RecyclerView.java` | 12,264 |
