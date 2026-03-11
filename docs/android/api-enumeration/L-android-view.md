# Android 11 (API 30) Public API Enumeration: Android View

Generated from `frameworks/base/api/current.txt`

## Summary

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.view` | 154 | 1700 | 890 | 119 |
| `android.view.accessibility` | 18 | 290 | 155 | 21 |
| `android.view.animation` | 26 | 98 | 40 | 57 |
| `android.view.autofill` | 3 | 39 | 6 | 1 |
| `android.view.contentcapture` | 11 | 29 | 5 | 4 |
| `android.view.inputmethod` | 25 | 221 | 71 | 18 |
| `android.view.inspector` | 10 | 33 | 0 | 6 |
| `android.view.textclassifier` | 43 | 74 | 86 | 19 |
| `android.view.textservice` | 8 | 46 | 4 | 9 |
| **TOTAL** | **298** | **2530** | **1257** | **254** |

---

## Package: `android.view`

### `class abstract AbsSavedState`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbsSavedState(android.os.Parcelable)` |  |
| `AbsSavedState(android.os.Parcel)` |  |
| `AbsSavedState(android.os.Parcel, ClassLoader)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.view.AbsSavedState EMPTY_STATE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `final android.os.Parcelable getSuperState()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract ActionMode`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ActionMode()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEFAULT_HIDE_DURATION = -1` |  |
| `static final int TYPE_FLOATING = 1` |  |
| `static final int TYPE_PRIMARY = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void finish()` |  |
| `abstract android.view.View getCustomView()` |  |
| `abstract android.view.Menu getMenu()` |  |
| `abstract android.view.MenuInflater getMenuInflater()` |  |
| `abstract CharSequence getSubtitle()` |  |
| `Object getTag()` |  |
| `abstract CharSequence getTitle()` |  |
| `boolean getTitleOptionalHint()` |  |
| `int getType()` |  |
| `void hide(long)` |  |
| `abstract void invalidate()` |  |
| `void invalidateContentRect()` |  |
| `boolean isTitleOptional()` |  |
| `void onWindowFocusChanged(boolean)` |  |
| `abstract void setCustomView(android.view.View)` |  |
| `abstract void setSubtitle(CharSequence)` |  |
| `abstract void setSubtitle(@StringRes int)` |  |
| `void setTag(Object)` |  |
| `abstract void setTitle(CharSequence)` |  |
| `abstract void setTitle(@StringRes int)` |  |
| `void setTitleOptionalHint(boolean)` |  |
| `void setType(int)` |  |

---

### `interface static ActionMode.Callback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ActionMode.Callback2()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onActionItemClicked(android.view.ActionMode, android.view.MenuItem)` |  |
| `boolean onCreateActionMode(android.view.ActionMode, android.view.Menu)` |  |
| `void onDestroyActionMode(android.view.ActionMode)` |  |
| `boolean onPrepareActionMode(android.view.ActionMode, android.view.Menu)` |  |
| `void onGetContentRect(android.view.ActionMode, android.view.View, android.graphics.Rect)` |  |

---

### `class abstract ActionProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ActionProvider(android.content.Context)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasSubMenu()` |  |
| `boolean isVisible()` |  |
| `android.view.View onCreateActionView(android.view.MenuItem)` |  |
| `boolean onPerformDefaultAction()` |  |
| `void onPrepareSubMenu(android.view.SubMenu)` |  |
| `boolean overridesItemVisibility()` |  |
| `void refreshVisibility()` |  |
| `void setVisibilityListener(android.view.ActionProvider.VisibilityListener)` |  |

---

### `interface static ActionProvider.VisibilityListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onActionProviderVisibilityChanged(boolean)` |  |

---

### `class final Choreographer`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.view.Choreographer getInstance()` |  |
| `void postFrameCallback(android.view.Choreographer.FrameCallback)` |  |
| `void postFrameCallbackDelayed(android.view.Choreographer.FrameCallback, long)` |  |
| `void removeFrameCallback(android.view.Choreographer.FrameCallback)` |  |

---

### `interface static Choreographer.FrameCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void doFrame(long)` |  |

---

### `interface CollapsibleActionView`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onActionViewCollapsed()` |  |
| `void onActionViewExpanded()` |  |

---

### `interface ContextMenu`

- **Extends:** `android.view.Menu`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearHeader()` |  |
| `android.view.ContextMenu setHeaderIcon(@DrawableRes int)` |  |
| `android.view.ContextMenu setHeaderIcon(android.graphics.drawable.Drawable)` |  |
| `android.view.ContextMenu setHeaderTitle(@StringRes int)` |  |
| `android.view.ContextMenu setHeaderTitle(CharSequence)` |  |
| `android.view.ContextMenu setHeaderView(android.view.View)` |  |

---

### `interface static ContextMenu.ContextMenuInfo`


---

### `class ContextThemeWrapper`

- **Extends:** `android.content.ContextWrapper`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ContextThemeWrapper()` |  |
| `ContextThemeWrapper(android.content.Context, @StyleRes int)` |  |
| `ContextThemeWrapper(android.content.Context, android.content.res.Resources.Theme)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyOverrideConfiguration(android.content.res.Configuration)` |  |
| `void onApplyThemeResource(android.content.res.Resources.Theme, int, boolean)` |  |
| `void setTheme(@Nullable android.content.res.Resources.Theme)` |  |

---

### `class final Display`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEFAULT_DISPLAY = 0` |  |
| `static final int FLAG_PRESENTATION = 8` |  |
| `static final int FLAG_PRIVATE = 4` |  |
| `static final int FLAG_ROUND = 16` |  |
| `static final int FLAG_SECURE = 2` |  |
| `static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 1` |  |
| `static final int INVALID_DISPLAY = -1` |  |
| `static final int STATE_DOZE = 3` |  |
| `static final int STATE_DOZE_SUSPEND = 4` |  |
| `static final int STATE_OFF = 1` |  |
| `static final int STATE_ON = 2` |  |
| `static final int STATE_ON_SUSPEND = 6` |  |
| `static final int STATE_UNKNOWN = 0` |  |
| `static final int STATE_VR = 5` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getAppVsyncOffsetNanos()` |  |
| `void getCurrentSizeRange(android.graphics.Point, android.graphics.Point)` |  |
| `int getDisplayId()` |  |
| `int getFlags()` |  |
| `android.view.Display.HdrCapabilities getHdrCapabilities()` |  |
| `android.view.Display.Mode getMode()` |  |
| `String getName()` |  |
| `long getPresentationDeadlineNanos()` |  |
| `void getRealMetrics(android.util.DisplayMetrics)` |  |
| `void getRealSize(android.graphics.Point)` |  |
| `float getRefreshRate()` |  |
| `int getRotation()` |  |
| `int getState()` |  |
| `android.view.Display.Mode[] getSupportedModes()` |  |
| `boolean isHdr()` |  |
| `boolean isMinimalPostProcessingSupported()` |  |
| `boolean isValid()` |  |
| `boolean isWideColorGamut()` |  |

---

### `class static final Display.HdrCapabilities`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int HDR_TYPE_DOLBY_VISION = 1` |  |
| `static final int HDR_TYPE_HDR10 = 2` |  |
| `static final int HDR_TYPE_HDR10_PLUS = 4` |  |
| `static final int HDR_TYPE_HLG = 3` |  |
| `static final float INVALID_LUMINANCE = -1.0f` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `float getDesiredMaxAverageLuminance()` |  |
| `float getDesiredMaxLuminance()` |  |
| `float getDesiredMinLuminance()` |  |
| `int[] getSupportedHdrTypes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Display.Mode`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getModeId()` |  |
| `int getPhysicalHeight()` |  |
| `int getPhysicalWidth()` |  |
| `float getRefreshRate()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final DisplayCutout`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DisplayCutout(@NonNull android.graphics.Insets, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect)` |  |
| `DisplayCutout(@NonNull android.graphics.Insets, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect, @Nullable android.graphics.Rect, @NonNull android.graphics.Insets)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getSafeInsetBottom()` |  |
| `int getSafeInsetLeft()` |  |
| `int getSafeInsetRight()` |  |
| `int getSafeInsetTop()` |  |

---

### `class final DragAndDropPermissions`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void release()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DragEvent`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACTION_DRAG_ENDED = 4` |  |
| `static final int ACTION_DRAG_ENTERED = 5` |  |
| `static final int ACTION_DRAG_EXITED = 6` |  |
| `static final int ACTION_DRAG_LOCATION = 2` |  |
| `static final int ACTION_DRAG_STARTED = 1` |  |
| `static final int ACTION_DROP = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAction()` |  |
| `android.content.ClipData getClipData()` |  |
| `android.content.ClipDescription getClipDescription()` |  |
| `Object getLocalState()` |  |
| `boolean getResult()` |  |
| `float getX()` |  |
| `float getY()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class FocusFinder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.View findNearestTouchable(android.view.ViewGroup, int, int, int, int[])` |  |
| `final android.view.View findNextFocus(android.view.ViewGroup, android.view.View, int)` |  |
| `android.view.View findNextFocusFromRect(android.view.ViewGroup, android.graphics.Rect, int)` |  |
| `android.view.View findNextKeyboardNavigationCluster(@NonNull android.view.View, @Nullable android.view.View, int)` |  |
| `static android.view.FocusFinder getInstance()` |  |

---

### `class final FrameMetrics`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FrameMetrics(android.view.FrameMetrics)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ANIMATION_DURATION = 2` |  |
| `static final int COMMAND_ISSUE_DURATION = 6` |  |
| `static final int DRAW_DURATION = 4` |  |
| `static final int FIRST_DRAW_FRAME = 9` |  |
| `static final int INPUT_HANDLING_DURATION = 1` |  |
| `static final int INTENDED_VSYNC_TIMESTAMP = 10` |  |
| `static final int LAYOUT_MEASURE_DURATION = 3` |  |
| `static final int SWAP_BUFFERS_DURATION = 7` |  |
| `static final int SYNC_DURATION = 5` |  |
| `static final int TOTAL_DURATION = 8` |  |
| `static final int UNKNOWN_DELAY_DURATION = 0` |  |
| `static final int VSYNC_TIMESTAMP = 11` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getMetric(int)` |  |

---

### `class abstract FrameStats`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FrameStats()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long UNDEFINED_TIME_NANO = -1L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final long getEndTimeNano()` |  |
| `final int getFrameCount()` |  |
| `final long getFramePresentedTimeNano(int)` |  |
| `final long getRefreshPeriodNano()` |  |
| `final long getStartTimeNano()` |  |

---

### `class GestureDetector`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureDetector(android.content.Context, android.view.GestureDetector.OnGestureListener)` |  |
| `GestureDetector(android.content.Context, android.view.GestureDetector.OnGestureListener, android.os.Handler)` |  |
| `GestureDetector(android.content.Context, android.view.GestureDetector.OnGestureListener, android.os.Handler, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isLongpressEnabled()` |  |
| `boolean onGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean onTouchEvent(android.view.MotionEvent)` |  |
| `void setContextClickListener(android.view.GestureDetector.OnContextClickListener)` |  |
| `void setIsLongpressEnabled(boolean)` |  |
| `void setOnDoubleTapListener(android.view.GestureDetector.OnDoubleTapListener)` |  |

---

### `interface static GestureDetector.OnContextClickListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onContextClick(android.view.MotionEvent)` |  |

---

### `interface static GestureDetector.OnDoubleTapListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onDoubleTap(android.view.MotionEvent)` |  |
| `boolean onDoubleTapEvent(android.view.MotionEvent)` |  |
| `boolean onSingleTapConfirmed(android.view.MotionEvent)` |  |

---

### `interface static GestureDetector.OnGestureListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onDown(android.view.MotionEvent)` |  |
| `boolean onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)` |  |
| `void onLongPress(android.view.MotionEvent)` |  |
| `boolean onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)` |  |
| `void onShowPress(android.view.MotionEvent)` |  |
| `boolean onSingleTapUp(android.view.MotionEvent)` |  |

---

### `class static GestureDetector.SimpleOnGestureListener`

- **Implements:** `android.view.GestureDetector.OnContextClickListener android.view.GestureDetector.OnDoubleTapListener android.view.GestureDetector.OnGestureListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureDetector.SimpleOnGestureListener()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onContextClick(android.view.MotionEvent)` |  |
| `boolean onDoubleTap(android.view.MotionEvent)` |  |
| `boolean onDoubleTapEvent(android.view.MotionEvent)` |  |
| `boolean onDown(android.view.MotionEvent)` |  |
| `boolean onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)` |  |
| `void onLongPress(android.view.MotionEvent)` |  |
| `boolean onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)` |  |
| `void onShowPress(android.view.MotionEvent)` |  |
| `boolean onSingleTapConfirmed(android.view.MotionEvent)` |  |
| `boolean onSingleTapUp(android.view.MotionEvent)` |  |

---

### `class Gravity`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Gravity()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AXIS_CLIP = 8` |  |
| `static final int AXIS_PULL_AFTER = 4` |  |
| `static final int AXIS_PULL_BEFORE = 2` |  |
| `static final int AXIS_SPECIFIED = 1` |  |
| `static final int AXIS_X_SHIFT = 0` |  |
| `static final int AXIS_Y_SHIFT = 4` |  |
| `static final int BOTTOM = 80` |  |
| `static final int CENTER = 17` |  |
| `static final int CENTER_HORIZONTAL = 1` |  |
| `static final int CENTER_VERTICAL = 16` |  |
| `static final int CLIP_HORIZONTAL = 8` |  |
| `static final int CLIP_VERTICAL = 128` |  |
| `static final int DISPLAY_CLIP_HORIZONTAL = 16777216` |  |
| `static final int DISPLAY_CLIP_VERTICAL = 268435456` |  |
| `static final int END = 8388613` |  |
| `static final int FILL = 119` |  |
| `static final int FILL_HORIZONTAL = 7` |  |
| `static final int FILL_VERTICAL = 112` |  |
| `static final int HORIZONTAL_GRAVITY_MASK = 7` |  |
| `static final int LEFT = 3` |  |
| `static final int NO_GRAVITY = 0` |  |
| `static final int RELATIVE_HORIZONTAL_GRAVITY_MASK = 8388615` |  |
| `static final int RELATIVE_LAYOUT_DIRECTION = 8388608` |  |
| `static final int RIGHT = 5` |  |
| `static final int START = 8388611` |  |
| `static final int TOP = 48` |  |
| `static final int VERTICAL_GRAVITY_MASK = 112` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void apply(int, int, int, android.graphics.Rect, android.graphics.Rect)` |  |
| `static void apply(int, int, int, android.graphics.Rect, android.graphics.Rect, int)` |  |
| `static void apply(int, int, int, android.graphics.Rect, int, int, android.graphics.Rect)` |  |
| `static void apply(int, int, int, android.graphics.Rect, int, int, android.graphics.Rect, int)` |  |
| `static void applyDisplay(int, android.graphics.Rect, android.graphics.Rect)` |  |
| `static void applyDisplay(int, android.graphics.Rect, android.graphics.Rect, int)` |  |
| `static int getAbsoluteGravity(int, int)` |  |
| `static boolean isHorizontal(int)` |  |
| `static boolean isVertical(int)` |  |

---

### `class HapticFeedbackConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLOCK_TICK = 4` |  |
| `static final int CONFIRM = 16` |  |
| `static final int CONTEXT_CLICK = 6` |  |
| `static final int FLAG_IGNORE_GLOBAL_SETTING = 2` |  |
| `static final int FLAG_IGNORE_VIEW_SETTING = 1` |  |
| `static final int GESTURE_END = 13` |  |
| `static final int GESTURE_START = 12` |  |
| `static final int KEYBOARD_PRESS = 3` |  |
| `static final int KEYBOARD_RELEASE = 7` |  |
| `static final int KEYBOARD_TAP = 3` |  |
| `static final int LONG_PRESS = 0` |  |
| `static final int REJECT = 17` |  |
| `static final int TEXT_HANDLE_MOVE = 9` |  |
| `static final int VIRTUAL_KEY = 1` |  |
| `static final int VIRTUAL_KEY_RELEASE = 8` |  |

---

### `class InflateException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InflateException()` |  |
| `InflateException(String, Throwable)` |  |
| `InflateException(String)` |  |
| `InflateException(Throwable)` |  |

---

### `class final InputDevice`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int KEYBOARD_TYPE_ALPHABETIC = 2` |  |
| `static final int KEYBOARD_TYPE_NONE = 0` |  |
| `static final int KEYBOARD_TYPE_NON_ALPHABETIC = 1` |  |
| `static final int SOURCE_ANY = -256` |  |
| `static final int SOURCE_BLUETOOTH_STYLUS = 49154` |  |
| `static final int SOURCE_CLASS_BUTTON = 1` |  |
| `static final int SOURCE_CLASS_JOYSTICK = 16` |  |
| `static final int SOURCE_CLASS_MASK = 255` |  |
| `static final int SOURCE_CLASS_NONE = 0` |  |
| `static final int SOURCE_CLASS_POINTER = 2` |  |
| `static final int SOURCE_CLASS_POSITION = 8` |  |
| `static final int SOURCE_CLASS_TRACKBALL = 4` |  |
| `static final int SOURCE_DPAD = 513` |  |
| `static final int SOURCE_GAMEPAD = 1025` |  |
| `static final int SOURCE_HDMI = 33554433` |  |
| `static final int SOURCE_JOYSTICK = 16777232` |  |
| `static final int SOURCE_KEYBOARD = 257` |  |
| `static final int SOURCE_MOUSE = 8194` |  |
| `static final int SOURCE_MOUSE_RELATIVE = 131076` |  |
| `static final int SOURCE_ROTARY_ENCODER = 4194304` |  |
| `static final int SOURCE_STYLUS = 16386` |  |
| `static final int SOURCE_TOUCHPAD = 1048584` |  |
| `static final int SOURCE_TOUCHSCREEN = 4098` |  |
| `static final int SOURCE_TOUCH_NAVIGATION = 2097152` |  |
| `static final int SOURCE_TRACKBALL = 65540` |  |
| `static final int SOURCE_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getControllerNumber()` |  |
| `String getDescriptor()` |  |
| `static android.view.InputDevice getDevice(int)` |  |
| `static int[] getDeviceIds()` |  |
| `int getId()` |  |
| `android.view.KeyCharacterMap getKeyCharacterMap()` |  |
| `int getKeyboardType()` |  |
| `android.view.InputDevice.MotionRange getMotionRange(int)` |  |
| `android.view.InputDevice.MotionRange getMotionRange(int, int)` |  |
| `java.util.List<android.view.InputDevice.MotionRange> getMotionRanges()` |  |
| `String getName()` |  |
| `int getProductId()` |  |
| `int getSources()` |  |
| `int getVendorId()` |  |
| `android.os.Vibrator getVibrator()` |  |
| `boolean[] hasKeys(int...)` |  |
| `boolean hasMicrophone()` |  |
| `boolean isEnabled()` |  |
| `boolean isExternal()` |  |
| `boolean isVirtual()` |  |
| `boolean supportsSource(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final InputDevice.MotionRange`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAxis()` |  |
| `float getFlat()` |  |
| `float getFuzz()` |  |
| `float getMax()` |  |
| `float getMin()` |  |
| `float getRange()` |  |
| `float getResolution()` |  |
| `int getSource()` |  |
| `boolean isFromSource(int)` |  |

---

### `class abstract InputEvent`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `final android.view.InputDevice getDevice()` |  |
| `abstract int getDeviceId()` |  |
| `abstract long getEventTime()` |  |
| `abstract int getSource()` |  |
| `boolean isFromSource(int)` |  |

---

### `class final InputQueue`


---

### `interface static InputQueue.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onInputQueueCreated(android.view.InputQueue)` |  |
| `void onInputQueueDestroyed(android.view.InputQueue)` |  |

---

### `class KeyCharacterMap`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALPHA = 3` |  |
| `static final int COMBINING_ACCENT = -2147483648` |  |
| `static final int COMBINING_ACCENT_MASK = 2147483647` |  |
| `static final int FULL = 4` |  |
| `static final char HEX_INPUT = 61184` |  |
| `static final int MODIFIER_BEHAVIOR_CHORDED = 0` |  |
| `static final int MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED = 1` |  |
| `static final int NUMERIC = 1` |  |
| `static final char PICKER_DIALOG_INPUT = 61185` |  |
| `static final int PREDICTIVE = 2` |  |
| `static final int SPECIAL_FUNCTION = 5` |  |
| `static final int VIRTUAL_KEYBOARD = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static boolean deviceHasKey(int)` |  |
| `static boolean[] deviceHasKeys(int[])` |  |
| `int get(int, int)` |  |
| `static int getDeadChar(int, int)` |  |
| `char getDisplayLabel(int)` |  |
| `android.view.KeyEvent[] getEvents(char[])` |  |
| `int getKeyboardType()` |  |
| `char getMatch(int, char[])` |  |
| `char getMatch(int, char[], int)` |  |
| `int getModifierBehavior()` |  |
| `char getNumber(int)` |  |
| `boolean isPrintingKey(int)` |  |
| `static android.view.KeyCharacterMap load(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static KeyCharacterMap.KeyData` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static KeyCharacterMap.UnavailableException`

- **Extends:** `android.util.AndroidRuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyCharacterMap.UnavailableException(String)` |  |

---

### `class KeyEvent`

- **Extends:** `android.view.InputEvent`
- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyEvent(int, int)` |  |
| `KeyEvent(long, long, int, int, int)` |  |
| `KeyEvent(long, long, int, int, int, int)` |  |
| `KeyEvent(long, long, int, int, int, int, int, int)` |  |
| `KeyEvent(long, long, int, int, int, int, int, int, int)` |  |
| `KeyEvent(long, long, int, int, int, int, int, int, int, int)` |  |
| `KeyEvent(long, String, int, int)` |  |
| `KeyEvent(android.view.KeyEvent)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACTION_DOWN = 0` |  |
| `static final int ACTION_UP = 1` |  |
| `static final int FLAG_CANCELED = 32` |  |
| `static final int FLAG_CANCELED_LONG_PRESS = 256` |  |
| `static final int FLAG_EDITOR_ACTION = 16` |  |
| `static final int FLAG_FALLBACK = 1024` |  |
| `static final int FLAG_FROM_SYSTEM = 8` |  |
| `static final int FLAG_KEEP_TOUCH_MODE = 4` |  |
| `static final int FLAG_LONG_PRESS = 128` |  |
| `static final int FLAG_SOFT_KEYBOARD = 2` |  |
| `static final int FLAG_TRACKING = 512` |  |
| `static final int FLAG_VIRTUAL_HARD_KEY = 64` |  |
| `static final int KEYCODE_0 = 7` |  |
| `static final int KEYCODE_1 = 8` |  |
| `static final int KEYCODE_11 = 227` |  |
| `static final int KEYCODE_12 = 228` |  |
| `static final int KEYCODE_2 = 9` |  |
| `static final int KEYCODE_3 = 10` |  |
| `static final int KEYCODE_3D_MODE = 206` |  |
| `static final int KEYCODE_4 = 11` |  |
| `static final int KEYCODE_5 = 12` |  |
| `static final int KEYCODE_6 = 13` |  |
| `static final int KEYCODE_7 = 14` |  |
| `static final int KEYCODE_8 = 15` |  |
| `static final int KEYCODE_9 = 16` |  |
| `static final int KEYCODE_A = 29` |  |
| `static final int KEYCODE_ALL_APPS = 284` |  |
| `static final int KEYCODE_ALT_LEFT = 57` |  |
| `static final int KEYCODE_ALT_RIGHT = 58` |  |
| `static final int KEYCODE_APOSTROPHE = 75` |  |
| `static final int KEYCODE_APP_SWITCH = 187` |  |
| `static final int KEYCODE_ASSIST = 219` |  |
| `static final int KEYCODE_AT = 77` |  |
| `static final int KEYCODE_AVR_INPUT = 182` |  |
| `static final int KEYCODE_AVR_POWER = 181` |  |
| `static final int KEYCODE_B = 30` |  |
| `static final int KEYCODE_BACK = 4` |  |
| `static final int KEYCODE_BACKSLASH = 73` |  |
| `static final int KEYCODE_BOOKMARK = 174` |  |
| `static final int KEYCODE_BREAK = 121` |  |
| `static final int KEYCODE_BRIGHTNESS_DOWN = 220` |  |
| `static final int KEYCODE_BRIGHTNESS_UP = 221` |  |
| `static final int KEYCODE_BUTTON_1 = 188` |  |
| `static final int KEYCODE_BUTTON_10 = 197` |  |
| `static final int KEYCODE_BUTTON_11 = 198` |  |
| `static final int KEYCODE_BUTTON_12 = 199` |  |
| `static final int KEYCODE_BUTTON_13 = 200` |  |
| `static final int KEYCODE_BUTTON_14 = 201` |  |
| `static final int KEYCODE_BUTTON_15 = 202` |  |
| `static final int KEYCODE_BUTTON_16 = 203` |  |
| `static final int KEYCODE_BUTTON_2 = 189` |  |
| `static final int KEYCODE_BUTTON_3 = 190` |  |
| `static final int KEYCODE_BUTTON_4 = 191` |  |
| `static final int KEYCODE_BUTTON_5 = 192` |  |
| `static final int KEYCODE_BUTTON_6 = 193` |  |
| `static final int KEYCODE_BUTTON_7 = 194` |  |
| `static final int KEYCODE_BUTTON_8 = 195` |  |
| `static final int KEYCODE_BUTTON_9 = 196` |  |
| `static final int KEYCODE_BUTTON_A = 96` |  |
| `static final int KEYCODE_BUTTON_B = 97` |  |
| `static final int KEYCODE_BUTTON_C = 98` |  |
| `static final int KEYCODE_BUTTON_L1 = 102` |  |
| `static final int KEYCODE_BUTTON_L2 = 104` |  |
| `static final int KEYCODE_BUTTON_MODE = 110` |  |
| `static final int KEYCODE_BUTTON_R1 = 103` |  |
| `static final int KEYCODE_BUTTON_R2 = 105` |  |
| `static final int KEYCODE_BUTTON_SELECT = 109` |  |
| `static final int KEYCODE_BUTTON_START = 108` |  |
| `static final int KEYCODE_BUTTON_THUMBL = 106` |  |
| `static final int KEYCODE_BUTTON_THUMBR = 107` |  |
| `static final int KEYCODE_BUTTON_X = 99` |  |
| `static final int KEYCODE_BUTTON_Y = 100` |  |
| `static final int KEYCODE_BUTTON_Z = 101` |  |
| `static final int KEYCODE_C = 31` |  |
| `static final int KEYCODE_CALCULATOR = 210` |  |
| `static final int KEYCODE_CALENDAR = 208` |  |
| `static final int KEYCODE_CALL = 5` |  |
| `static final int KEYCODE_CAMERA = 27` |  |
| `static final int KEYCODE_CAPS_LOCK = 115` |  |
| `static final int KEYCODE_CAPTIONS = 175` |  |
| `static final int KEYCODE_CHANNEL_DOWN = 167` |  |
| `static final int KEYCODE_CHANNEL_UP = 166` |  |
| `static final int KEYCODE_CLEAR = 28` |  |
| `static final int KEYCODE_COMMA = 55` |  |
| `static final int KEYCODE_CONTACTS = 207` |  |
| `static final int KEYCODE_COPY = 278` |  |
| `static final int KEYCODE_CTRL_LEFT = 113` |  |
| `static final int KEYCODE_CTRL_RIGHT = 114` |  |
| `static final int KEYCODE_CUT = 277` |  |
| `static final int KEYCODE_D = 32` |  |
| `static final int KEYCODE_DEL = 67` |  |
| `static final int KEYCODE_DPAD_CENTER = 23` |  |
| `static final int KEYCODE_DPAD_DOWN = 20` |  |
| `static final int KEYCODE_DPAD_DOWN_LEFT = 269` |  |
| `static final int KEYCODE_DPAD_DOWN_RIGHT = 271` |  |
| `static final int KEYCODE_DPAD_LEFT = 21` |  |
| `static final int KEYCODE_DPAD_RIGHT = 22` |  |
| `static final int KEYCODE_DPAD_UP = 19` |  |
| `static final int KEYCODE_DPAD_UP_LEFT = 268` |  |
| `static final int KEYCODE_DPAD_UP_RIGHT = 270` |  |
| `static final int KEYCODE_DVR = 173` |  |
| `static final int KEYCODE_E = 33` |  |
| `static final int KEYCODE_EISU = 212` |  |
| `static final int KEYCODE_ENDCALL = 6` |  |
| `static final int KEYCODE_ENTER = 66` |  |
| `static final int KEYCODE_ENVELOPE = 65` |  |
| `static final int KEYCODE_EQUALS = 70` |  |
| `static final int KEYCODE_ESCAPE = 111` |  |
| `static final int KEYCODE_EXPLORER = 64` |  |
| `static final int KEYCODE_F = 34` |  |
| `static final int KEYCODE_F1 = 131` |  |
| `static final int KEYCODE_F10 = 140` |  |
| `static final int KEYCODE_F11 = 141` |  |
| `static final int KEYCODE_F12 = 142` |  |
| `static final int KEYCODE_F2 = 132` |  |
| `static final int KEYCODE_F3 = 133` |  |
| `static final int KEYCODE_F4 = 134` |  |
| `static final int KEYCODE_F5 = 135` |  |
| `static final int KEYCODE_F6 = 136` |  |
| `static final int KEYCODE_F7 = 137` |  |
| `static final int KEYCODE_F8 = 138` |  |
| `static final int KEYCODE_F9 = 139` |  |
| `static final int KEYCODE_FOCUS = 80` |  |
| `static final int KEYCODE_FORWARD = 125` |  |
| `static final int KEYCODE_FORWARD_DEL = 112` |  |
| `static final int KEYCODE_FUNCTION = 119` |  |
| `static final int KEYCODE_G = 35` |  |
| `static final int KEYCODE_GRAVE = 68` |  |
| `static final int KEYCODE_GUIDE = 172` |  |
| `static final int KEYCODE_H = 36` |  |
| `static final int KEYCODE_HEADSETHOOK = 79` |  |
| `static final int KEYCODE_HELP = 259` |  |
| `static final int KEYCODE_HENKAN = 214` |  |
| `static final int KEYCODE_HOME = 3` |  |
| `static final int KEYCODE_I = 37` |  |
| `static final int KEYCODE_INFO = 165` |  |
| `static final int KEYCODE_INSERT = 124` |  |
| `static final int KEYCODE_J = 38` |  |
| `static final int KEYCODE_K = 39` |  |
| `static final int KEYCODE_KANA = 218` |  |
| `static final int KEYCODE_KATAKANA_HIRAGANA = 215` |  |
| `static final int KEYCODE_L = 40` |  |
| `static final int KEYCODE_LANGUAGE_SWITCH = 204` |  |
| `static final int KEYCODE_LAST_CHANNEL = 229` |  |
| `static final int KEYCODE_LEFT_BRACKET = 71` |  |
| `static final int KEYCODE_M = 41` |  |
| `static final int KEYCODE_MANNER_MODE = 205` |  |
| `static final int KEYCODE_MEDIA_AUDIO_TRACK = 222` |  |
| `static final int KEYCODE_MEDIA_CLOSE = 128` |  |
| `static final int KEYCODE_MEDIA_EJECT = 129` |  |
| `static final int KEYCODE_MEDIA_FAST_FORWARD = 90` |  |
| `static final int KEYCODE_MEDIA_NEXT = 87` |  |
| `static final int KEYCODE_MEDIA_PAUSE = 127` |  |
| `static final int KEYCODE_MEDIA_PLAY = 126` |  |
| `static final int KEYCODE_MEDIA_PLAY_PAUSE = 85` |  |
| `static final int KEYCODE_MEDIA_PREVIOUS = 88` |  |
| `static final int KEYCODE_MEDIA_RECORD = 130` |  |
| `static final int KEYCODE_MEDIA_REWIND = 89` |  |
| `static final int KEYCODE_MEDIA_SKIP_BACKWARD = 273` |  |
| `static final int KEYCODE_MEDIA_SKIP_FORWARD = 272` |  |
| `static final int KEYCODE_MEDIA_STEP_BACKWARD = 275` |  |
| `static final int KEYCODE_MEDIA_STEP_FORWARD = 274` |  |
| `static final int KEYCODE_MEDIA_STOP = 86` |  |
| `static final int KEYCODE_MEDIA_TOP_MENU = 226` |  |
| `static final int KEYCODE_MENU = 82` |  |
| `static final int KEYCODE_META_LEFT = 117` |  |
| `static final int KEYCODE_META_RIGHT = 118` |  |
| `static final int KEYCODE_MINUS = 69` |  |
| `static final int KEYCODE_MOVE_END = 123` |  |
| `static final int KEYCODE_MOVE_HOME = 122` |  |
| `static final int KEYCODE_MUHENKAN = 213` |  |
| `static final int KEYCODE_MUSIC = 209` |  |
| `static final int KEYCODE_MUTE = 91` |  |
| `static final int KEYCODE_N = 42` |  |
| `static final int KEYCODE_NAVIGATE_IN = 262` |  |
| `static final int KEYCODE_NAVIGATE_NEXT = 261` |  |
| `static final int KEYCODE_NAVIGATE_OUT = 263` |  |
| `static final int KEYCODE_NAVIGATE_PREVIOUS = 260` |  |
| `static final int KEYCODE_NOTIFICATION = 83` |  |
| `static final int KEYCODE_NUM = 78` |  |
| `static final int KEYCODE_NUMPAD_0 = 144` |  |
| `static final int KEYCODE_NUMPAD_1 = 145` |  |
| `static final int KEYCODE_NUMPAD_2 = 146` |  |
| `static final int KEYCODE_NUMPAD_3 = 147` |  |
| `static final int KEYCODE_NUMPAD_4 = 148` |  |
| `static final int KEYCODE_NUMPAD_5 = 149` |  |
| `static final int KEYCODE_NUMPAD_6 = 150` |  |
| `static final int KEYCODE_NUMPAD_7 = 151` |  |
| `static final int KEYCODE_NUMPAD_8 = 152` |  |
| `static final int KEYCODE_NUMPAD_9 = 153` |  |
| `static final int KEYCODE_NUMPAD_ADD = 157` |  |
| `static final int KEYCODE_NUMPAD_COMMA = 159` |  |
| `static final int KEYCODE_NUMPAD_DIVIDE = 154` |  |
| `static final int KEYCODE_NUMPAD_DOT = 158` |  |
| `static final int KEYCODE_NUMPAD_ENTER = 160` |  |
| `static final int KEYCODE_NUMPAD_EQUALS = 161` |  |
| `static final int KEYCODE_NUMPAD_LEFT_PAREN = 162` |  |
| `static final int KEYCODE_NUMPAD_MULTIPLY = 155` |  |
| `static final int KEYCODE_NUMPAD_RIGHT_PAREN = 163` |  |
| `static final int KEYCODE_NUMPAD_SUBTRACT = 156` |  |
| `static final int KEYCODE_NUM_LOCK = 143` |  |
| `static final int KEYCODE_O = 43` |  |
| `static final int KEYCODE_P = 44` |  |
| `static final int KEYCODE_PAGE_DOWN = 93` |  |
| `static final int KEYCODE_PAGE_UP = 92` |  |
| `static final int KEYCODE_PAIRING = 225` |  |
| `static final int KEYCODE_PASTE = 279` |  |
| `static final int KEYCODE_PERIOD = 56` |  |
| `static final int KEYCODE_PICTSYMBOLS = 94` |  |
| `static final int KEYCODE_PLUS = 81` |  |
| `static final int KEYCODE_POUND = 18` |  |
| `static final int KEYCODE_POWER = 26` |  |
| `static final int KEYCODE_PROFILE_SWITCH = 288` |  |
| `static final int KEYCODE_PROG_BLUE = 186` |  |
| `static final int KEYCODE_PROG_GREEN = 184` |  |
| `static final int KEYCODE_PROG_RED = 183` |  |
| `static final int KEYCODE_PROG_YELLOW = 185` |  |
| `static final int KEYCODE_Q = 45` |  |
| `static final int KEYCODE_R = 46` |  |
| `static final int KEYCODE_REFRESH = 285` |  |
| `static final int KEYCODE_RIGHT_BRACKET = 72` |  |
| `static final int KEYCODE_RO = 217` |  |
| `static final int KEYCODE_S = 47` |  |
| `static final int KEYCODE_SCROLL_LOCK = 116` |  |
| `static final int KEYCODE_SEARCH = 84` |  |
| `static final int KEYCODE_SEMICOLON = 74` |  |
| `static final int KEYCODE_SETTINGS = 176` |  |
| `static final int KEYCODE_SHIFT_LEFT = 59` |  |
| `static final int KEYCODE_SHIFT_RIGHT = 60` |  |
| `static final int KEYCODE_SLASH = 76` |  |
| `static final int KEYCODE_SLEEP = 223` |  |
| `static final int KEYCODE_SOFT_LEFT = 1` |  |
| `static final int KEYCODE_SOFT_RIGHT = 2` |  |
| `static final int KEYCODE_SOFT_SLEEP = 276` |  |
| `static final int KEYCODE_SPACE = 62` |  |
| `static final int KEYCODE_STAR = 17` |  |
| `static final int KEYCODE_STB_INPUT = 180` |  |
| `static final int KEYCODE_STB_POWER = 179` |  |
| `static final int KEYCODE_STEM_1 = 265` |  |
| `static final int KEYCODE_STEM_2 = 266` |  |
| `static final int KEYCODE_STEM_3 = 267` |  |
| `static final int KEYCODE_STEM_PRIMARY = 264` |  |
| `static final int KEYCODE_SWITCH_CHARSET = 95` |  |
| `static final int KEYCODE_SYM = 63` |  |
| `static final int KEYCODE_SYSRQ = 120` |  |
| `static final int KEYCODE_SYSTEM_NAVIGATION_DOWN = 281` |  |
| `static final int KEYCODE_SYSTEM_NAVIGATION_LEFT = 282` |  |
| `static final int KEYCODE_SYSTEM_NAVIGATION_RIGHT = 283` |  |
| `static final int KEYCODE_SYSTEM_NAVIGATION_UP = 280` |  |
| `static final int KEYCODE_T = 48` |  |
| `static final int KEYCODE_TAB = 61` |  |
| `static final int KEYCODE_THUMBS_DOWN = 287` |  |
| `static final int KEYCODE_THUMBS_UP = 286` |  |
| `static final int KEYCODE_TV = 170` |  |
| `static final int KEYCODE_TV_ANTENNA_CABLE = 242` |  |
| `static final int KEYCODE_TV_AUDIO_DESCRIPTION = 252` |  |
| `static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN = 254` |  |
| `static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP = 253` |  |
| `static final int KEYCODE_TV_CONTENTS_MENU = 256` |  |
| `static final int KEYCODE_TV_DATA_SERVICE = 230` |  |
| `static final int KEYCODE_TV_INPUT = 178` |  |
| `static final int KEYCODE_TV_INPUT_COMPONENT_1 = 249` |  |
| `static final int KEYCODE_TV_INPUT_COMPONENT_2 = 250` |  |
| `static final int KEYCODE_TV_INPUT_COMPOSITE_1 = 247` |  |
| `static final int KEYCODE_TV_INPUT_COMPOSITE_2 = 248` |  |
| `static final int KEYCODE_TV_INPUT_HDMI_1 = 243` |  |
| `static final int KEYCODE_TV_INPUT_HDMI_2 = 244` |  |
| `static final int KEYCODE_TV_INPUT_HDMI_3 = 245` |  |
| `static final int KEYCODE_TV_INPUT_HDMI_4 = 246` |  |
| `static final int KEYCODE_TV_INPUT_VGA_1 = 251` |  |
| `static final int KEYCODE_TV_MEDIA_CONTEXT_MENU = 257` |  |
| `static final int KEYCODE_TV_NETWORK = 241` |  |
| `static final int KEYCODE_TV_NUMBER_ENTRY = 234` |  |
| `static final int KEYCODE_TV_POWER = 177` |  |
| `static final int KEYCODE_TV_RADIO_SERVICE = 232` |  |
| `static final int KEYCODE_TV_SATELLITE = 237` |  |
| `static final int KEYCODE_TV_SATELLITE_BS = 238` |  |
| `static final int KEYCODE_TV_SATELLITE_CS = 239` |  |
| `static final int KEYCODE_TV_SATELLITE_SERVICE = 240` |  |
| `static final int KEYCODE_TV_TELETEXT = 233` |  |
| `static final int KEYCODE_TV_TERRESTRIAL_ANALOG = 235` |  |
| `static final int KEYCODE_TV_TERRESTRIAL_DIGITAL = 236` |  |
| `static final int KEYCODE_TV_TIMER_PROGRAMMING = 258` |  |
| `static final int KEYCODE_TV_ZOOM_MODE = 255` |  |
| `static final int KEYCODE_U = 49` |  |
| `static final int KEYCODE_UNKNOWN = 0` |  |
| `static final int KEYCODE_V = 50` |  |
| `static final int KEYCODE_VOICE_ASSIST = 231` |  |
| `static final int KEYCODE_VOLUME_DOWN = 25` |  |
| `static final int KEYCODE_VOLUME_MUTE = 164` |  |
| `static final int KEYCODE_VOLUME_UP = 24` |  |
| `static final int KEYCODE_W = 51` |  |
| `static final int KEYCODE_WAKEUP = 224` |  |
| `static final int KEYCODE_WINDOW = 171` |  |
| `static final int KEYCODE_X = 52` |  |
| `static final int KEYCODE_Y = 53` |  |
| `static final int KEYCODE_YEN = 216` |  |
| `static final int KEYCODE_Z = 54` |  |
| `static final int KEYCODE_ZENKAKU_HANKAKU = 211` |  |
| `static final int KEYCODE_ZOOM_IN = 168` |  |
| `static final int KEYCODE_ZOOM_OUT = 169` |  |
| `static final int META_ALT_LEFT_ON = 16` |  |
| `static final int META_ALT_MASK = 50` |  |
| `static final int META_ALT_ON = 2` |  |
| `static final int META_ALT_RIGHT_ON = 32` |  |
| `static final int META_CAPS_LOCK_ON = 1048576` |  |
| `static final int META_CTRL_LEFT_ON = 8192` |  |
| `static final int META_CTRL_MASK = 28672` |  |
| `static final int META_CTRL_ON = 4096` |  |
| `static final int META_CTRL_RIGHT_ON = 16384` |  |
| `static final int META_FUNCTION_ON = 8` |  |
| `static final int META_META_LEFT_ON = 131072` |  |
| `static final int META_META_MASK = 458752` |  |
| `static final int META_META_ON = 65536` |  |
| `static final int META_META_RIGHT_ON = 262144` |  |
| `static final int META_NUM_LOCK_ON = 2097152` |  |
| `static final int META_SCROLL_LOCK_ON = 4194304` |  |
| `static final int META_SHIFT_LEFT_ON = 64` |  |
| `static final int META_SHIFT_MASK = 193` |  |
| `static final int META_SHIFT_ON = 1` |  |
| `static final int META_SHIFT_RIGHT_ON = 128` |  |
| `static final int META_SYM_ON = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.view.KeyEvent changeAction(android.view.KeyEvent, int)` |  |
| `static android.view.KeyEvent changeFlags(android.view.KeyEvent, int)` |  |
| `static android.view.KeyEvent changeTimeRepeat(android.view.KeyEvent, long, int)` |  |
| `static android.view.KeyEvent changeTimeRepeat(android.view.KeyEvent, long, int, int)` |  |
| `final boolean dispatch(android.view.KeyEvent.Callback, android.view.KeyEvent.DispatcherState, Object)` |  |
| `final int getAction()` |  |
| `static int getDeadChar(int, int)` |  |
| `final int getDeviceId()` |  |
| `char getDisplayLabel()` |  |
| `final long getDownTime()` |  |
| `final long getEventTime()` |  |
| `final int getFlags()` |  |
| `final android.view.KeyCharacterMap getKeyCharacterMap()` |  |
| `final int getKeyCode()` |  |
| `char getMatch(char[])` |  |
| `char getMatch(char[], int)` |  |
| `static int getMaxKeyCode()` |  |
| `final int getMetaState()` |  |
| `static int getModifierMetaStateMask()` |  |
| `final int getModifiers()` |  |
| `char getNumber()` |  |
| `final int getRepeatCount()` |  |
| `final int getScanCode()` |  |
| `final int getSource()` |  |
| `int getUnicodeChar()` |  |
| `int getUnicodeChar(int)` |  |
| `final boolean hasModifiers(int)` |  |
| `final boolean hasNoModifiers()` |  |
| `final boolean isAltPressed()` |  |
| `final boolean isCanceled()` |  |
| `final boolean isCapsLockOn()` |  |
| `final boolean isCtrlPressed()` |  |
| `final boolean isFunctionPressed()` |  |
| `static final boolean isGamepadButton(int)` |  |
| `final boolean isLongPress()` |  |
| `final boolean isMetaPressed()` |  |
| `static boolean isModifierKey(int)` |  |
| `final boolean isNumLockOn()` |  |
| `boolean isPrintingKey()` |  |
| `final boolean isScrollLockOn()` |  |
| `final boolean isShiftPressed()` |  |
| `final boolean isSymPressed()` |  |
| `final boolean isSystem()` |  |
| `final boolean isTracking()` |  |
| `static int keyCodeFromString(@NonNull String)` |  |
| `static String keyCodeToString(int)` |  |
| `static boolean metaStateHasModifiers(int, int)` |  |
| `static boolean metaStateHasNoModifiers(int)` |  |
| `static int normalizeMetaState(int)` |  |
| `final void setSource(int)` |  |
| `final void startTracking()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static KeyEvent.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |

---

### `class static KeyEvent.DispatcherState`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyEvent.DispatcherState()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void handleUpEvent(android.view.KeyEvent)` |  |
| `boolean isTracking(android.view.KeyEvent)` |  |
| `void performedLongPress(android.view.KeyEvent)` |  |
| `void reset()` |  |
| `void reset(Object)` |  |
| `void startTracking(android.view.KeyEvent, Object)` |  |

---

### `class final KeyboardShortcutGroup`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyboardShortcutGroup(@Nullable CharSequence, @NonNull java.util.List<android.view.KeyboardShortcutInfo>)` |  |
| `KeyboardShortcutGroup(@Nullable CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addItem(android.view.KeyboardShortcutInfo)` |  |
| `int describeContents()` |  |
| `java.util.List<android.view.KeyboardShortcutInfo> getItems()` |  |
| `CharSequence getLabel()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final KeyboardShortcutInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyboardShortcutInfo(CharSequence, int, int)` |  |
| `KeyboardShortcutInfo(CharSequence, char, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `char getBaseCharacter()` |  |
| `int getKeycode()` |  |
| `int getModifiers()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract LayoutInflater`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LayoutInflater(android.content.Context)` |  |
| `LayoutInflater(android.view.LayoutInflater, android.content.Context)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.view.LayoutInflater cloneInContext(android.content.Context)` |  |
| `final android.view.View createView(String, String, android.util.AttributeSet) throws java.lang.ClassNotFoundException, android.view.InflateException` |  |
| `static android.view.LayoutInflater from(android.content.Context)` |  |
| `android.content.Context getContext()` |  |
| `final android.view.LayoutInflater.Factory getFactory()` |  |
| `final android.view.LayoutInflater.Factory2 getFactory2()` |  |
| `android.view.LayoutInflater.Filter getFilter()` |  |
| `android.view.View inflate(@LayoutRes int, @Nullable android.view.ViewGroup)` |  |
| `android.view.View inflate(org.xmlpull.v1.XmlPullParser, @Nullable android.view.ViewGroup)` |  |
| `android.view.View inflate(@LayoutRes int, @Nullable android.view.ViewGroup, boolean)` |  |
| `android.view.View inflate(org.xmlpull.v1.XmlPullParser, @Nullable android.view.ViewGroup, boolean)` |  |
| `android.view.View onCreateView(String, android.util.AttributeSet) throws java.lang.ClassNotFoundException` |  |
| `android.view.View onCreateView(android.view.View, String, android.util.AttributeSet) throws java.lang.ClassNotFoundException` |  |
| `void setFactory(android.view.LayoutInflater.Factory)` |  |
| `void setFactory2(android.view.LayoutInflater.Factory2)` |  |
| `void setFilter(android.view.LayoutInflater.Filter)` |  |

---

### `interface static LayoutInflater.Factory`


---

### `interface static LayoutInflater.Factory2`

- **Extends:** `android.view.LayoutInflater.Factory`

---

### `interface static LayoutInflater.Filter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onLoadClass(Class)` |  |

---

### `interface Menu`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CATEGORY_ALTERNATIVE = 262144` |  |
| `static final int CATEGORY_CONTAINER = 65536` |  |
| `static final int CATEGORY_SECONDARY = 196608` |  |
| `static final int CATEGORY_SYSTEM = 131072` |  |
| `static final int FIRST = 1` |  |
| `static final int FLAG_ALWAYS_PERFORM_CLOSE = 2` |  |
| `static final int FLAG_APPEND_TO_GROUP = 1` |  |
| `static final int FLAG_PERFORM_NO_CLOSE = 1` |  |
| `static final int NONE = 0` |  |
| `static final int SUPPORTED_MODIFIERS_MASK = 69647` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.MenuItem add(CharSequence)` |  |
| `android.view.MenuItem add(@StringRes int)` |  |
| `android.view.MenuItem add(int, int, int, CharSequence)` |  |
| `android.view.MenuItem add(int, int, int, @StringRes int)` |  |
| `int addIntentOptions(int, int, int, android.content.ComponentName, android.content.Intent[], android.content.Intent, int, android.view.MenuItem[])` |  |
| `android.view.SubMenu addSubMenu(CharSequence)` |  |
| `android.view.SubMenu addSubMenu(@StringRes int)` |  |
| `android.view.SubMenu addSubMenu(int, int, int, CharSequence)` |  |
| `android.view.SubMenu addSubMenu(int, int, int, @StringRes int)` |  |
| `void clear()` |  |
| `void close()` |  |
| `android.view.MenuItem findItem(int)` |  |
| `android.view.MenuItem getItem(int)` |  |
| `boolean hasVisibleItems()` |  |
| `boolean isShortcutKey(int, android.view.KeyEvent)` |  |
| `boolean performIdentifierAction(int, int)` |  |
| `boolean performShortcut(int, android.view.KeyEvent, int)` |  |
| `void removeGroup(int)` |  |
| `void removeItem(int)` |  |
| `void setGroupCheckable(int, boolean, boolean)` |  |
| `default void setGroupDividerEnabled(boolean)` |  |
| `void setGroupEnabled(int, boolean)` |  |
| `void setGroupVisible(int, boolean)` |  |
| `void setQwertyMode(boolean)` |  |
| `int size()` |  |

---

### `class MenuInflater`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MenuInflater(android.content.Context)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void inflate(@MenuRes int, android.view.Menu)` |  |

---

### `interface MenuItem`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SHOW_AS_ACTION_ALWAYS = 2` |  |
| `static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8` |  |
| `static final int SHOW_AS_ACTION_IF_ROOM = 1` |  |
| `static final int SHOW_AS_ACTION_NEVER = 0` |  |
| `static final int SHOW_AS_ACTION_WITH_TEXT = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean collapseActionView()` |  |
| `boolean expandActionView()` |  |
| `android.view.ActionProvider getActionProvider()` |  |
| `android.view.View getActionView()` |  |
| `default int getAlphabeticModifiers()` |  |
| `char getAlphabeticShortcut()` |  |
| `default CharSequence getContentDescription()` |  |
| `int getGroupId()` |  |
| `android.graphics.drawable.Drawable getIcon()` |  |
| `android.content.Intent getIntent()` |  |
| `int getItemId()` |  |
| `android.view.ContextMenu.ContextMenuInfo getMenuInfo()` |  |
| `default int getNumericModifiers()` |  |
| `char getNumericShortcut()` |  |
| `int getOrder()` |  |
| `android.view.SubMenu getSubMenu()` |  |
| `CharSequence getTitle()` |  |
| `CharSequence getTitleCondensed()` |  |
| `default CharSequence getTooltipText()` |  |
| `boolean hasSubMenu()` |  |
| `boolean isActionViewExpanded()` |  |
| `boolean isCheckable()` |  |
| `boolean isChecked()` |  |
| `boolean isEnabled()` |  |
| `boolean isVisible()` |  |
| `android.view.MenuItem setActionProvider(android.view.ActionProvider)` |  |
| `android.view.MenuItem setActionView(android.view.View)` |  |
| `android.view.MenuItem setActionView(@LayoutRes int)` |  |
| `android.view.MenuItem setAlphabeticShortcut(char)` |  |
| `default android.view.MenuItem setAlphabeticShortcut(char, int)` |  |
| `android.view.MenuItem setCheckable(boolean)` |  |
| `android.view.MenuItem setChecked(boolean)` |  |
| `default android.view.MenuItem setContentDescription(CharSequence)` |  |
| `android.view.MenuItem setEnabled(boolean)` |  |
| `android.view.MenuItem setIcon(android.graphics.drawable.Drawable)` |  |
| `android.view.MenuItem setIcon(@DrawableRes int)` |  |
| `default android.view.MenuItem setIconTintList(@Nullable android.content.res.ColorStateList)` |  |
| `android.view.MenuItem setIntent(android.content.Intent)` |  |
| `android.view.MenuItem setNumericShortcut(char)` |  |
| `default android.view.MenuItem setNumericShortcut(char, int)` |  |
| `android.view.MenuItem setOnActionExpandListener(android.view.MenuItem.OnActionExpandListener)` |  |
| `android.view.MenuItem setOnMenuItemClickListener(android.view.MenuItem.OnMenuItemClickListener)` |  |
| `android.view.MenuItem setShortcut(char, char)` |  |
| `default android.view.MenuItem setShortcut(char, char, int, int)` |  |
| `void setShowAsAction(int)` |  |
| `android.view.MenuItem setShowAsActionFlags(int)` |  |
| `android.view.MenuItem setTitle(CharSequence)` |  |
| `android.view.MenuItem setTitle(@StringRes int)` |  |
| `android.view.MenuItem setTitleCondensed(CharSequence)` |  |
| `default android.view.MenuItem setTooltipText(CharSequence)` |  |
| `android.view.MenuItem setVisible(boolean)` |  |

---

### `interface static MenuItem.OnActionExpandListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onMenuItemActionCollapse(android.view.MenuItem)` |  |
| `boolean onMenuItemActionExpand(android.view.MenuItem)` |  |

---

### `interface static MenuItem.OnMenuItemClickListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onMenuItemClick(android.view.MenuItem)` |  |

---

### `class final MotionEvent`

- **Extends:** `android.view.InputEvent`
- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACTION_BUTTON_PRESS = 11` |  |
| `static final int ACTION_BUTTON_RELEASE = 12` |  |
| `static final int ACTION_CANCEL = 3` |  |
| `static final int ACTION_DOWN = 0` |  |
| `static final int ACTION_HOVER_ENTER = 9` |  |
| `static final int ACTION_HOVER_EXIT = 10` |  |
| `static final int ACTION_HOVER_MOVE = 7` |  |
| `static final int ACTION_MASK = 255` |  |
| `static final int ACTION_MOVE = 2` |  |
| `static final int ACTION_OUTSIDE = 4` |  |
| `static final int ACTION_POINTER_DOWN = 5` |  |
| `static final int ACTION_POINTER_INDEX_MASK = 65280` |  |
| `static final int ACTION_POINTER_INDEX_SHIFT = 8` |  |
| `static final int ACTION_POINTER_UP = 6` |  |
| `static final int ACTION_SCROLL = 8` |  |
| `static final int ACTION_UP = 1` |  |
| `static final int AXIS_BRAKE = 23` |  |
| `static final int AXIS_DISTANCE = 24` |  |
| `static final int AXIS_GAS = 22` |  |
| `static final int AXIS_GENERIC_1 = 32` |  |
| `static final int AXIS_GENERIC_10 = 41` |  |
| `static final int AXIS_GENERIC_11 = 42` |  |
| `static final int AXIS_GENERIC_12 = 43` |  |
| `static final int AXIS_GENERIC_13 = 44` |  |
| `static final int AXIS_GENERIC_14 = 45` |  |
| `static final int AXIS_GENERIC_15 = 46` |  |
| `static final int AXIS_GENERIC_16 = 47` |  |
| `static final int AXIS_GENERIC_2 = 33` |  |
| `static final int AXIS_GENERIC_3 = 34` |  |
| `static final int AXIS_GENERIC_4 = 35` |  |
| `static final int AXIS_GENERIC_5 = 36` |  |
| `static final int AXIS_GENERIC_6 = 37` |  |
| `static final int AXIS_GENERIC_7 = 38` |  |
| `static final int AXIS_GENERIC_8 = 39` |  |
| `static final int AXIS_GENERIC_9 = 40` |  |
| `static final int AXIS_HAT_X = 15` |  |
| `static final int AXIS_HAT_Y = 16` |  |
| `static final int AXIS_HSCROLL = 10` |  |
| `static final int AXIS_LTRIGGER = 17` |  |
| `static final int AXIS_ORIENTATION = 8` |  |
| `static final int AXIS_PRESSURE = 2` |  |
| `static final int AXIS_RELATIVE_X = 27` |  |
| `static final int AXIS_RELATIVE_Y = 28` |  |
| `static final int AXIS_RTRIGGER = 18` |  |
| `static final int AXIS_RUDDER = 20` |  |
| `static final int AXIS_RX = 12` |  |
| `static final int AXIS_RY = 13` |  |
| `static final int AXIS_RZ = 14` |  |
| `static final int AXIS_SCROLL = 26` |  |
| `static final int AXIS_SIZE = 3` |  |
| `static final int AXIS_THROTTLE = 19` |  |
| `static final int AXIS_TILT = 25` |  |
| `static final int AXIS_TOOL_MAJOR = 6` |  |
| `static final int AXIS_TOOL_MINOR = 7` |  |
| `static final int AXIS_TOUCH_MAJOR = 4` |  |
| `static final int AXIS_TOUCH_MINOR = 5` |  |
| `static final int AXIS_VSCROLL = 9` |  |
| `static final int AXIS_WHEEL = 21` |  |
| `static final int AXIS_X = 0` |  |
| `static final int AXIS_Y = 1` |  |
| `static final int AXIS_Z = 11` |  |
| `static final int BUTTON_BACK = 8` |  |
| `static final int BUTTON_FORWARD = 16` |  |
| `static final int BUTTON_PRIMARY = 1` |  |
| `static final int BUTTON_SECONDARY = 2` |  |
| `static final int BUTTON_STYLUS_PRIMARY = 32` |  |
| `static final int BUTTON_STYLUS_SECONDARY = 64` |  |
| `static final int BUTTON_TERTIARY = 4` |  |
| `static final int CLASSIFICATION_AMBIGUOUS_GESTURE = 1` |  |
| `static final int CLASSIFICATION_DEEP_PRESS = 2` |  |
| `static final int CLASSIFICATION_NONE = 0` |  |
| `static final int EDGE_BOTTOM = 2` |  |
| `static final int EDGE_LEFT = 4` |  |
| `static final int EDGE_RIGHT = 8` |  |
| `static final int EDGE_TOP = 1` |  |
| `static final int FLAG_WINDOW_IS_OBSCURED = 1` |  |
| `static final int FLAG_WINDOW_IS_PARTIALLY_OBSCURED = 2` |  |
| `static final int INVALID_POINTER_ID = -1` |  |
| `static final int TOOL_TYPE_ERASER = 4` |  |
| `static final int TOOL_TYPE_FINGER = 1` |  |
| `static final int TOOL_TYPE_MOUSE = 3` |  |
| `static final int TOOL_TYPE_STYLUS = 2` |  |
| `static final int TOOL_TYPE_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String actionToString(int)` |  |
| `void addBatch(long, float, float, float, float, int)` |  |
| `void addBatch(long, android.view.MotionEvent.PointerCoords[], int)` |  |
| `static int axisFromString(String)` |  |
| `static String axisToString(int)` |  |
| `int findPointerIndex(int)` |  |
| `int getAction()` |  |
| `int getActionButton()` |  |
| `int getActionIndex()` |  |
| `int getActionMasked()` |  |
| `float getAxisValue(int)` |  |
| `float getAxisValue(int, int)` |  |
| `int getButtonState()` |  |
| `int getClassification()` |  |
| `int getDeviceId()` |  |
| `long getDownTime()` |  |
| `int getEdgeFlags()` |  |
| `long getEventTime()` |  |
| `int getFlags()` |  |
| `float getHistoricalAxisValue(int, int)` |  |
| `float getHistoricalAxisValue(int, int, int)` |  |
| `long getHistoricalEventTime(int)` |  |
| `float getHistoricalOrientation(int)` |  |
| `float getHistoricalOrientation(int, int)` |  |
| `void getHistoricalPointerCoords(int, int, android.view.MotionEvent.PointerCoords)` |  |
| `float getHistoricalPressure(int)` |  |
| `float getHistoricalPressure(int, int)` |  |
| `float getHistoricalSize(int)` |  |
| `float getHistoricalSize(int, int)` |  |
| `float getHistoricalToolMajor(int)` |  |
| `float getHistoricalToolMajor(int, int)` |  |
| `float getHistoricalToolMinor(int)` |  |
| `float getHistoricalToolMinor(int, int)` |  |
| `float getHistoricalTouchMajor(int)` |  |
| `float getHistoricalTouchMajor(int, int)` |  |
| `float getHistoricalTouchMinor(int)` |  |
| `float getHistoricalTouchMinor(int, int)` |  |
| `float getHistoricalX(int)` |  |
| `float getHistoricalX(int, int)` |  |
| `float getHistoricalY(int)` |  |
| `float getHistoricalY(int, int)` |  |
| `int getHistorySize()` |  |
| `int getMetaState()` |  |
| `float getOrientation()` |  |
| `float getOrientation(int)` |  |
| `void getPointerCoords(int, android.view.MotionEvent.PointerCoords)` |  |
| `int getPointerCount()` |  |
| `int getPointerId(int)` |  |
| `void getPointerProperties(int, android.view.MotionEvent.PointerProperties)` |  |
| `float getPressure()` |  |
| `float getPressure(int)` |  |
| `float getRawX()` |  |
| `float getRawX(int)` |  |
| `float getRawY()` |  |
| `float getRawY(int)` |  |
| `float getSize()` |  |
| `float getSize(int)` |  |
| `int getSource()` |  |
| `float getToolMajor()` |  |
| `float getToolMajor(int)` |  |
| `float getToolMinor()` |  |
| `float getToolMinor(int)` |  |
| `int getToolType(int)` |  |
| `float getTouchMajor()` |  |
| `float getTouchMajor(int)` |  |
| `float getTouchMinor()` |  |
| `float getTouchMinor(int)` |  |
| `float getX()` |  |
| `float getX(int)` |  |
| `float getXPrecision()` |  |
| `float getY()` |  |
| `float getY(int)` |  |
| `float getYPrecision()` |  |
| `boolean isButtonPressed(int)` |  |
| `static android.view.MotionEvent obtain(long, long, int, int, android.view.MotionEvent.PointerProperties[], android.view.MotionEvent.PointerCoords[], int, int, float, float, int, int, int, int)` |  |
| `static android.view.MotionEvent obtain(long, long, int, float, float, float, float, int, float, float, int, int)` |  |
| `static android.view.MotionEvent obtain(long, long, int, float, float, int)` |  |
| `static android.view.MotionEvent obtain(android.view.MotionEvent)` |  |
| `static android.view.MotionEvent obtainNoHistory(android.view.MotionEvent)` |  |
| `void offsetLocation(float, float)` |  |
| `void recycle()` |  |
| `void setAction(int)` |  |
| `void setEdgeFlags(int)` |  |
| `void setLocation(float, float)` |  |
| `void setSource(int)` |  |
| `void transform(android.graphics.Matrix)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final MotionEvent.PointerCoords`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MotionEvent.PointerCoords()` |  |
| `MotionEvent.PointerCoords(android.view.MotionEvent.PointerCoords)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `float orientation` |  |
| `float pressure` |  |
| `float size` |  |
| `float toolMajor` |  |
| `float toolMinor` |  |
| `float touchMajor` |  |
| `float touchMinor` |  |
| `float x` |  |
| `float y` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `void copyFrom(android.view.MotionEvent.PointerCoords)` |  |
| `float getAxisValue(int)` |  |
| `void setAxisValue(int, float)` |  |

---

### `class static final MotionEvent.PointerProperties`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MotionEvent.PointerProperties()` |  |
| `MotionEvent.PointerProperties(android.view.MotionEvent.PointerProperties)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int id` |  |
| `int toolType` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `void copyFrom(android.view.MotionEvent.PointerProperties)` |  |

---

### `class abstract OrientationEventListener`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OrientationEventListener(android.content.Context)` |  |
| `OrientationEventListener(android.content.Context, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ORIENTATION_UNKNOWN = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean canDetectOrientation()` |  |
| `void disable()` |  |
| `void enable()` |  |
| `abstract void onOrientationChanged(int)` |  |

---

### `class abstract OrientationListener` ~~DEPRECATED~~

- **Implements:** `android.hardware.SensorListener`
- **Annotations:** `@Deprecated`

---

### `class final PixelCopy`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_DESTINATION_INVALID = 5` |  |
| `static final int ERROR_SOURCE_INVALID = 4` |  |
| `static final int ERROR_SOURCE_NO_DATA = 3` |  |
| `static final int ERROR_TIMEOUT = 2` |  |
| `static final int ERROR_UNKNOWN = 1` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void request(@NonNull android.view.SurfaceView, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |
| `static void request(@NonNull android.view.SurfaceView, @Nullable android.graphics.Rect, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |
| `static void request(@NonNull android.view.Surface, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |
| `static void request(@NonNull android.view.Surface, @Nullable android.graphics.Rect, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |
| `static void request(@NonNull android.view.Window, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |
| `static void request(@NonNull android.view.Window, @Nullable android.graphics.Rect, @NonNull android.graphics.Bitmap, @NonNull android.view.PixelCopy.OnPixelCopyFinishedListener, @NonNull android.os.Handler)` |  |

---

### `interface static PixelCopy.OnPixelCopyFinishedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onPixelCopyFinished(int)` |  |

---

### `class final PointerIcon`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_ALIAS = 1010` |  |
| `static final int TYPE_ALL_SCROLL = 1013` |  |
| `static final int TYPE_ARROW = 1000` |  |
| `static final int TYPE_CELL = 1006` |  |
| `static final int TYPE_CONTEXT_MENU = 1001` |  |
| `static final int TYPE_COPY = 1011` |  |
| `static final int TYPE_CROSSHAIR = 1007` |  |
| `static final int TYPE_DEFAULT = 1000` |  |
| `static final int TYPE_GRAB = 1020` |  |
| `static final int TYPE_GRABBING = 1021` |  |
| `static final int TYPE_HAND = 1002` |  |
| `static final int TYPE_HELP = 1003` |  |
| `static final int TYPE_HORIZONTAL_DOUBLE_ARROW = 1014` |  |
| `static final int TYPE_NO_DROP = 1012` |  |
| `static final int TYPE_NULL = 0` |  |
| `static final int TYPE_TEXT = 1008` |  |
| `static final int TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 1017` |  |
| `static final int TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 1016` |  |
| `static final int TYPE_VERTICAL_DOUBLE_ARROW = 1015` |  |
| `static final int TYPE_VERTICAL_TEXT = 1009` |  |
| `static final int TYPE_WAIT = 1004` |  |
| `static final int TYPE_ZOOM_IN = 1018` |  |
| `static final int TYPE_ZOOM_OUT = 1019` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.view.PointerIcon create(@NonNull android.graphics.Bitmap, float, float)` |  |
| `int describeContents()` |  |
| `static android.view.PointerIcon getSystemIcon(@NonNull android.content.Context, int)` |  |
| `static android.view.PointerIcon load(@NonNull android.content.res.Resources, @XmlRes int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ScaleGestureDetector`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScaleGestureDetector(android.content.Context, android.view.ScaleGestureDetector.OnScaleGestureListener)` |  |
| `ScaleGestureDetector(android.content.Context, android.view.ScaleGestureDetector.OnScaleGestureListener, android.os.Handler)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getCurrentSpan()` |  |
| `float getCurrentSpanX()` |  |
| `float getCurrentSpanY()` |  |
| `long getEventTime()` |  |
| `float getFocusX()` |  |
| `float getFocusY()` |  |
| `float getPreviousSpan()` |  |
| `float getPreviousSpanX()` |  |
| `float getPreviousSpanY()` |  |
| `float getScaleFactor()` |  |
| `long getTimeDelta()` |  |
| `boolean isInProgress()` |  |
| `boolean isQuickScaleEnabled()` |  |
| `boolean isStylusScaleEnabled()` |  |
| `boolean onTouchEvent(android.view.MotionEvent)` |  |
| `void setQuickScaleEnabled(boolean)` |  |
| `void setStylusScaleEnabled(boolean)` |  |

---

### `interface static ScaleGestureDetector.OnScaleGestureListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onScale(android.view.ScaleGestureDetector)` |  |
| `boolean onScaleBegin(android.view.ScaleGestureDetector)` |  |
| `void onScaleEnd(android.view.ScaleGestureDetector)` |  |

---

### `class static ScaleGestureDetector.SimpleOnScaleGestureListener`

- **Implements:** `android.view.ScaleGestureDetector.OnScaleGestureListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScaleGestureDetector.SimpleOnScaleGestureListener()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onScale(android.view.ScaleGestureDetector)` |  |
| `boolean onScaleBegin(android.view.ScaleGestureDetector)` |  |
| `void onScaleEnd(android.view.ScaleGestureDetector)` |  |

---

### `class SearchEvent`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SearchEvent(android.view.InputDevice)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.InputDevice getInputDevice()` |  |

---

### `class SoundEffectConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLICK = 0` |  |
| `static final int NAVIGATION_DOWN = 4` |  |
| `static final int NAVIGATION_LEFT = 1` |  |
| `static final int NAVIGATION_RIGHT = 3` |  |
| `static final int NAVIGATION_UP = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int getContantForFocusDirection(int)` |  |

---

### `interface SubMenu`

- **Extends:** `android.view.Menu`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearHeader()` |  |
| `android.view.MenuItem getItem()` |  |
| `android.view.SubMenu setHeaderIcon(@DrawableRes int)` |  |
| `android.view.SubMenu setHeaderIcon(android.graphics.drawable.Drawable)` |  |
| `android.view.SubMenu setHeaderTitle(@StringRes int)` |  |
| `android.view.SubMenu setHeaderTitle(CharSequence)` |  |
| `android.view.SubMenu setHeaderView(android.view.View)` |  |
| `android.view.SubMenu setIcon(@DrawableRes int)` |  |
| `android.view.SubMenu setIcon(android.graphics.drawable.Drawable)` |  |

---

### `class Surface`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Surface(@NonNull android.view.SurfaceControl)` |  |
| `Surface(android.graphics.SurfaceTexture)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FRAME_RATE_COMPATIBILITY_DEFAULT = 0` |  |
| `static final int FRAME_RATE_COMPATIBILITY_FIXED_SOURCE = 1` |  |
| `static final int ROTATION_0 = 0` |  |
| `static final int ROTATION_180 = 2` |  |
| `static final int ROTATION_270 = 3` |  |
| `static final int ROTATION_90 = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isValid()` |  |
| `android.graphics.Canvas lockCanvas(android.graphics.Rect) throws java.lang.IllegalArgumentException, android.view.Surface.OutOfResourcesException` |  |
| `android.graphics.Canvas lockHardwareCanvas()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void release()` |  |
| `void setFrameRate(@FloatRange(from=0.0) float, int)` |  |
| `void unlockCanvasAndPost(android.graphics.Canvas)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static Surface.OutOfResourcesException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Surface.OutOfResourcesException()` |  |
| `Surface.OutOfResourcesException(String)` |  |

---

### `class final SurfaceControl`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isValid()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void release()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static SurfaceControl.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SurfaceControl.Builder()` |  |

---

### `class static SurfaceControl.Transaction`

- **Implements:** `java.io.Closeable android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SurfaceControl.Transaction()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void apply()` |  |
| `void close()` |  |
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class SurfaceControlViewHost`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SurfaceControlViewHost(@NonNull android.content.Context, @NonNull android.view.Display, @Nullable android.os.IBinder)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void relayout(int, int)` |  |
| `void release()` |  |
| `void setView(@NonNull android.view.View, int, int)` |  |

---

### `class static final SurfaceControlViewHost.SurfacePackage`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void release()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface SurfaceHolder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addCallback(android.view.SurfaceHolder.Callback)` |  |
| `android.view.Surface getSurface()` |  |
| `android.graphics.Rect getSurfaceFrame()` |  |
| `boolean isCreating()` |  |
| `android.graphics.Canvas lockCanvas()` |  |
| `android.graphics.Canvas lockCanvas(android.graphics.Rect)` |  |
| `default android.graphics.Canvas lockHardwareCanvas()` |  |
| `void removeCallback(android.view.SurfaceHolder.Callback)` |  |
| `void setFixedSize(int, int)` |  |
| `void setFormat(int)` |  |
| `void setKeepScreenOn(boolean)` |  |
| `void setSizeFromLayout()` |  |
| `void unlockCanvasAndPost(android.graphics.Canvas)` |  |

---

### `class static SurfaceHolder.BadSurfaceTypeException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SurfaceHolder.BadSurfaceTypeException()` |  |
| `SurfaceHolder.BadSurfaceTypeException(String)` |  |

---

### `interface static SurfaceHolder.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void surfaceChanged(@NonNull android.view.SurfaceHolder, int, @IntRange(from=0) int, @IntRange(from=0) int)` |  |
| `void surfaceCreated(@NonNull android.view.SurfaceHolder)` |  |
| `void surfaceDestroyed(@NonNull android.view.SurfaceHolder)` |  |

---

### `interface static SurfaceHolder.Callback2`

- **Extends:** `android.view.SurfaceHolder.Callback`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void surfaceRedrawNeeded(@NonNull android.view.SurfaceHolder)` |  |
| `default void surfaceRedrawNeededAsync(@NonNull android.view.SurfaceHolder, @NonNull Runnable)` |  |

---

### `class SurfaceView`

- **Extends:** `android.view.View`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SurfaceView(android.content.Context)` |  |
| `SurfaceView(android.content.Context, android.util.AttributeSet)` |  |
| `SurfaceView(android.content.Context, android.util.AttributeSet, int)` |  |
| `SurfaceView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean gatherTransparentRegion(android.graphics.Region)` |  |
| `android.view.SurfaceHolder getHolder()` |  |
| `android.view.SurfaceControl getSurfaceControl()` |  |
| `void setChildSurfacePackage(@NonNull android.view.SurfaceControlViewHost.SurfacePackage)` |  |
| `void setSecure(boolean)` |  |
| `void setZOrderMediaOverlay(boolean)` |  |
| `void setZOrderOnTop(boolean)` |  |

---

### `class TextureView`

- **Extends:** `android.view.View`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextureView(@NonNull android.content.Context)` |  |
| `TextureView(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `TextureView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `TextureView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void draw(android.graphics.Canvas)` |  |
| `boolean isAvailable()` |  |
| `final void onDraw(android.graphics.Canvas)` |  |
| `void setBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setOpaque(boolean)` |  |
| `void setSurfaceTexture(@NonNull android.graphics.SurfaceTexture)` |  |
| `void setSurfaceTextureListener(@Nullable android.view.TextureView.SurfaceTextureListener)` |  |
| `void setTransform(@Nullable android.graphics.Matrix)` |  |
| `void unlockCanvasAndPost(@NonNull android.graphics.Canvas)` |  |

---

### `interface static TextureView.SurfaceTextureListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture, int, int)` |  |
| `boolean onSurfaceTextureDestroyed(@NonNull android.graphics.SurfaceTexture)` |  |
| `void onSurfaceTextureSizeChanged(@NonNull android.graphics.SurfaceTexture, int, int)` |  |
| `void onSurfaceTextureUpdated(@NonNull android.graphics.SurfaceTexture)` |  |

---

### `class TouchDelegate`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TouchDelegate(android.graphics.Rect, android.view.View)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ABOVE = 1` |  |
| `static final int BELOW = 2` |  |
| `static final int TO_LEFT = 4` |  |
| `static final int TO_RIGHT = 8` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onTouchEvent(@NonNull android.view.MotionEvent)` |  |
| `boolean onTouchExplorationHoverEvent(@NonNull android.view.MotionEvent)` |  |

---

### `class final VelocityTracker`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addMovement(android.view.MotionEvent)` |  |
| `void clear()` |  |
| `void computeCurrentVelocity(int)` |  |
| `void computeCurrentVelocity(int, float)` |  |
| `float getXVelocity()` |  |
| `float getXVelocity(int)` |  |
| `float getYVelocity()` |  |
| `float getYVelocity(int)` |  |
| `static android.view.VelocityTracker obtain()` |  |
| `void recycle()` |  |

---

### `class abstract VerifiedInputEvent`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDeviceId()` |  |
| `int getDisplayId()` |  |
| `long getEventTimeNanos()` |  |
| `int getSource()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final VerifiedKeyEvent`

- **Extends:** `android.view.VerifiedInputEvent`
- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAction()` |  |
| `long getDownTimeNanos()` |  |
| `int getKeyCode()` |  |
| `int getMetaState()` |  |
| `int getRepeatCount()` |  |
| `int getScanCode()` |  |

---

### `class final VerifiedMotionEvent`

- **Extends:** `android.view.VerifiedInputEvent`
- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getActionMasked()` |  |
| `int getButtonState()` |  |
| `long getDownTimeNanos()` |  |
| `int getMetaState()` |  |
| `float getRawX()` |  |
| `float getRawY()` |  |

---

### `class View`

- **Implements:** `android.view.accessibility.AccessibilityEventSource android.graphics.drawable.Drawable.Callback android.view.KeyEvent.Callback`
- **Annotations:** `@UiThread`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `View(android.content.Context)` |  |
| `View(android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `View(android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `View(android.content.Context, @Nullable android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2` |  |
| `static final int ACCESSIBILITY_LIVE_REGION_NONE = 0` |  |
| `static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> ALPHA` |  |
| `static final int AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 1` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE = "creditCardExpirationDate"` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY = "creditCardExpirationDay"` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH = "creditCardExpirationMonth"` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR = "creditCardExpirationYear"` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_NUMBER = "creditCardNumber"` |  |
| `static final String AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE = "creditCardSecurityCode"` |  |
| `static final String AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress"` |  |
| `static final String AUTOFILL_HINT_NAME = "name"` |  |
| `static final String AUTOFILL_HINT_PASSWORD = "password"` |  |
| `static final String AUTOFILL_HINT_PHONE = "phone"` |  |
| `static final String AUTOFILL_HINT_POSTAL_ADDRESS = "postalAddress"` |  |
| `static final String AUTOFILL_HINT_POSTAL_CODE = "postalCode"` |  |
| `static final String AUTOFILL_HINT_USERNAME = "username"` |  |
| `static final int AUTOFILL_TYPE_DATE = 4` |  |
| `static final int AUTOFILL_TYPE_LIST = 3` |  |
| `static final int AUTOFILL_TYPE_NONE = 0` |  |
| `static final int AUTOFILL_TYPE_TEXT = 1` |  |
| `static final int AUTOFILL_TYPE_TOGGLE = 2` |  |
| `static final int DRAG_FLAG_GLOBAL = 256` |  |
| `static final int DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION = 64` |  |
| `static final int DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION = 128` |  |
| `static final int DRAG_FLAG_GLOBAL_URI_READ = 1` |  |
| `static final int DRAG_FLAG_GLOBAL_URI_WRITE = 2` |  |
| `static final int DRAG_FLAG_OPAQUE = 512` |  |
| `static final int[] EMPTY_STATE_SET` |  |
| `static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET` |  |
| `static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] ENABLED_FOCUSED_STATE_SET` |  |
| `static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] ENABLED_SELECTED_STATE_SET` |  |
| `static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] ENABLED_STATE_SET` |  |
| `static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2` |  |
| `static final int FIND_VIEWS_WITH_TEXT = 1` |  |
| `static final int FOCUSABLE = 1` |  |
| `static final int FOCUSABLES_ALL = 0` |  |
| `static final int FOCUSABLES_TOUCH_MODE = 1` |  |
| `static final int FOCUSABLE_AUTO = 16` |  |
| `static final int[] FOCUSED_SELECTED_STATE_SET` |  |
| `static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] FOCUSED_STATE_SET` |  |
| `static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int FOCUS_BACKWARD = 1` |  |
| `static final int FOCUS_DOWN = 130` |  |
| `static final int FOCUS_FORWARD = 2` |  |
| `static final int FOCUS_LEFT = 17` |  |
| `static final int FOCUS_RIGHT = 66` |  |
| `static final int FOCUS_UP = 33` |  |
| `static final int GONE = 8` |  |
| `static final int HAPTIC_FEEDBACK_ENABLED = 268435456` |  |
| `static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0` |  |
| `static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2` |  |
| `static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4` |  |
| `static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1` |  |
| `static final int IMPORTANT_FOR_AUTOFILL_AUTO = 0` |  |
| `static final int IMPORTANT_FOR_AUTOFILL_NO = 2` |  |
| `static final int IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS = 8` |  |
| `static final int IMPORTANT_FOR_AUTOFILL_YES = 1` |  |
| `static final int IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS = 4` |  |
| `static final int IMPORTANT_FOR_CONTENT_CAPTURE_AUTO = 0` |  |
| `static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO = 2` |  |
| `static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO_EXCLUDE_DESCENDANTS = 8` |  |
| `static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES = 1` |  |
| `static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES_EXCLUDE_DESCENDANTS = 4` |  |
| `static final int INVISIBLE = 4` |  |
| `static final int KEEP_SCREEN_ON = 67108864` |  |
| `static final int LAYER_TYPE_HARDWARE = 2` |  |
| `static final int LAYER_TYPE_NONE = 0` |  |
| `static final int LAYER_TYPE_SOFTWARE = 1` |  |
| `static final int LAYOUT_DIRECTION_INHERIT = 2` |  |
| `static final int LAYOUT_DIRECTION_LOCALE = 3` |  |
| `static final int LAYOUT_DIRECTION_LTR = 0` |  |
| `static final int LAYOUT_DIRECTION_RTL = 1` |  |
| `static final int MEASURED_HEIGHT_STATE_SHIFT = 16` |  |
| `static final int MEASURED_SIZE_MASK = 16777215` |  |
| `static final int MEASURED_STATE_MASK = -16777216` |  |
| `static final int MEASURED_STATE_TOO_SMALL = 16777216` |  |
| `static final int NOT_FOCUSABLE = 0` |  |
| `static final int NO_ID = -1` |  |
| `static final int OVER_SCROLL_ALWAYS = 0` |  |
| `static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1` |  |
| `static final int OVER_SCROLL_NEVER = 2` |  |
| `static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_SELECTED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_STATE_SET` |  |
| `static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET` |  |
| `static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_SELECTED_STATE_SET` |  |
| `static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int[] PRESSED_STATE_SET` |  |
| `static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> ROTATION` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> ROTATION_X` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> ROTATION_Y` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> SCALE_X` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> SCALE_Y` |  |
| `static final int SCREEN_STATE_OFF = 0` |  |
| `static final int SCREEN_STATE_ON = 1` |  |
| `static final int SCROLLBARS_INSIDE_INSET = 16777216` |  |
| `static final int SCROLLBARS_INSIDE_OVERLAY = 0` |  |
| `static final int SCROLLBARS_OUTSIDE_INSET = 50331648` |  |
| `static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432` |  |
| `static final int SCROLLBAR_POSITION_DEFAULT = 0` |  |
| `static final int SCROLLBAR_POSITION_LEFT = 1` |  |
| `static final int SCROLLBAR_POSITION_RIGHT = 2` |  |
| `static final int SCROLL_AXIS_HORIZONTAL = 1` |  |
| `static final int SCROLL_AXIS_NONE = 0` |  |
| `static final int SCROLL_AXIS_VERTICAL = 2` |  |
| `static final int SCROLL_INDICATOR_BOTTOM = 2` |  |
| `static final int SCROLL_INDICATOR_END = 32` |  |
| `static final int SCROLL_INDICATOR_LEFT = 4` |  |
| `static final int SCROLL_INDICATOR_RIGHT = 8` |  |
| `static final int SCROLL_INDICATOR_START = 16` |  |
| `static final int SCROLL_INDICATOR_TOP = 1` |  |
| `static final int[] SELECTED_STATE_SET` |  |
| `static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET` |  |
| `static final int SOUND_EFFECTS_ENABLED = 134217728` |  |
| `static final int TEXT_ALIGNMENT_CENTER = 4` |  |
| `static final int TEXT_ALIGNMENT_GRAVITY = 1` |  |
| `static final int TEXT_ALIGNMENT_INHERIT = 0` |  |
| `static final int TEXT_ALIGNMENT_TEXT_END = 3` |  |
| `static final int TEXT_ALIGNMENT_TEXT_START = 2` |  |
| `static final int TEXT_ALIGNMENT_VIEW_END = 6` |  |
| `static final int TEXT_ALIGNMENT_VIEW_START = 5` |  |
| `static final int TEXT_DIRECTION_ANY_RTL = 2` |  |
| `static final int TEXT_DIRECTION_FIRST_STRONG = 1` |  |
| `static final int TEXT_DIRECTION_FIRST_STRONG_LTR = 6` |  |
| `static final int TEXT_DIRECTION_FIRST_STRONG_RTL = 7` |  |
| `static final int TEXT_DIRECTION_INHERIT = 0` |  |
| `static final int TEXT_DIRECTION_LOCALE = 5` |  |
| `static final int TEXT_DIRECTION_LTR = 3` |  |
| `static final int TEXT_DIRECTION_RTL = 4` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> TRANSLATION_X` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> TRANSLATION_Y` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> TRANSLATION_Z` |  |
| `static final String VIEW_LOG_TAG = "View"` |  |
| `static final int VISIBLE = 0` |  |
| `static final int[] WINDOW_FOCUSED_STATE_SET` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> X` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> Y` |  |
| `static final android.util.Property<android.view.View,java.lang.Float> Z` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addChildrenForAccessibility(java.util.ArrayList<android.view.View>)` |  |
| `void addExtraDataToAccessibilityNodeInfo(@NonNull android.view.accessibility.AccessibilityNodeInfo, @NonNull String, @Nullable android.os.Bundle)` |  |
| `void addFocusables(java.util.ArrayList<android.view.View>, int)` |  |
| `void addFocusables(java.util.ArrayList<android.view.View>, int, int)` |  |
| `void addKeyboardNavigationClusters(@NonNull java.util.Collection<android.view.View>, int)` |  |
| `void addOnAttachStateChangeListener(android.view.View.OnAttachStateChangeListener)` |  |
| `void addOnLayoutChangeListener(android.view.View.OnLayoutChangeListener)` |  |
| `void addOnUnhandledKeyEventListener(android.view.View.OnUnhandledKeyEventListener)` |  |
| `void addTouchables(java.util.ArrayList<android.view.View>)` |  |
| `android.view.ViewPropertyAnimator animate()` |  |
| `void announceForAccessibility(CharSequence)` |  |
| `void autofill(android.view.autofill.AutofillValue)` |  |
| `void autofill(@NonNull android.util.SparseArray<android.view.autofill.AutofillValue>)` |  |
| `boolean awakenScrollBars()` |  |
| `boolean awakenScrollBars(int)` |  |
| `boolean awakenScrollBars(int, boolean)` |  |
| `void bringToFront()` |  |
| `void buildLayer()` |  |
| `boolean callOnClick()` |  |
| `boolean canResolveLayoutDirection()` |  |
| `boolean canResolveTextAlignment()` |  |
| `boolean canResolveTextDirection()` |  |
| `boolean canScrollHorizontally(int)` |  |
| `boolean canScrollVertically(int)` |  |
| `final void cancelDragAndDrop()` |  |
| `void cancelLongPress()` |  |
| `final void cancelPendingInputEvents()` |  |
| `boolean checkInputConnectionProxy(android.view.View)` |  |
| `void clearAnimation()` |  |
| `void clearFocus()` |  |
| `static int combineMeasuredStates(int, int)` |  |
| `int computeHorizontalScrollExtent()` |  |
| `int computeHorizontalScrollOffset()` |  |
| `int computeHorizontalScrollRange()` |  |
| `void computeScroll()` |  |
| `android.view.WindowInsets computeSystemWindowInsets(android.view.WindowInsets, android.graphics.Rect)` |  |
| `int computeVerticalScrollExtent()` |  |
| `int computeVerticalScrollOffset()` |  |
| `int computeVerticalScrollRange()` |  |
| `android.view.accessibility.AccessibilityNodeInfo createAccessibilityNodeInfo()` |  |
| `void createContextMenu(android.view.ContextMenu)` |  |
| `android.view.WindowInsets dispatchApplyWindowInsets(android.view.WindowInsets)` |  |
| `boolean dispatchCapturedPointerEvent(android.view.MotionEvent)` |  |
| `void dispatchConfigurationChanged(android.content.res.Configuration)` |  |
| `void dispatchDisplayHint(int)` |  |
| `boolean dispatchDragEvent(android.view.DragEvent)` |  |
| `void dispatchDraw(android.graphics.Canvas)` |  |
| `void dispatchDrawableHotspotChanged(float, float)` |  |
| `boolean dispatchGenericFocusedEvent(android.view.MotionEvent)` |  |
| `boolean dispatchGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean dispatchGenericPointerEvent(android.view.MotionEvent)` |  |
| `boolean dispatchHoverEvent(android.view.MotionEvent)` |  |
| `boolean dispatchKeyEvent(android.view.KeyEvent)` |  |
| `boolean dispatchKeyEventPreIme(android.view.KeyEvent)` |  |
| `boolean dispatchKeyShortcutEvent(android.view.KeyEvent)` |  |
| `boolean dispatchNestedFling(float, float, boolean)` |  |
| `boolean dispatchNestedPreFling(float, float)` |  |
| `boolean dispatchNestedPrePerformAccessibilityAction(int, android.os.Bundle)` |  |
| `boolean dispatchNestedPreScroll(int, int, @Nullable @Size(2) int[], @Nullable @Size(2) int[])` |  |
| `boolean dispatchNestedScroll(int, int, int, int, @Nullable @Size(2) int[])` |  |
| `void dispatchPointerCaptureChanged(boolean)` |  |
| `boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |
| `void dispatchProvideAutofillStructure(@NonNull android.view.ViewStructure, int)` |  |
| `void dispatchProvideStructure(android.view.ViewStructure)` |  |
| `void dispatchRestoreInstanceState(android.util.SparseArray<android.os.Parcelable>)` |  |
| `void dispatchSaveInstanceState(android.util.SparseArray<android.os.Parcelable>)` |  |
| `void dispatchSetActivated(boolean)` |  |
| `void dispatchSetPressed(boolean)` |  |
| `void dispatchSetSelected(boolean)` |  |
| `boolean dispatchTouchEvent(android.view.MotionEvent)` |  |
| `boolean dispatchTrackballEvent(android.view.MotionEvent)` |  |
| `boolean dispatchUnhandledMove(android.view.View, int)` |  |
| `void dispatchVisibilityChanged(@NonNull android.view.View, int)` |  |
| `void dispatchWindowFocusChanged(boolean)` |  |
| `void dispatchWindowInsetsAnimationEnd(@NonNull android.view.WindowInsetsAnimation)` |  |
| `void dispatchWindowInsetsAnimationPrepare(@NonNull android.view.WindowInsetsAnimation)` |  |
| `void dispatchWindowVisibilityChanged(int)` |  |
| `android.view.View findFocus()` |  |
| `final <T extends android.view.View> T findViewById(@IdRes int)` |  |
| `final <T extends android.view.View> T findViewWithTag(Object)` |  |
| `void findViewsWithText(java.util.ArrayList<android.view.View>, CharSequence, int)` |  |
| `android.view.View focusSearch(int)` |  |
| `void forceHasOverlappingRendering(boolean)` |  |
| `void forceLayout()` |  |
| `static int generateViewId()` |  |
| `CharSequence getAccessibilityClassName()` |  |
| `android.view.View.AccessibilityDelegate getAccessibilityDelegate()` |  |
| `int getAccessibilityLiveRegion()` |  |
| `android.view.accessibility.AccessibilityNodeProvider getAccessibilityNodeProvider()` |  |
| `android.view.animation.Animation getAnimation()` |  |
| `android.os.IBinder getApplicationWindowToken()` |  |
| `final android.view.autofill.AutofillId getAutofillId()` |  |
| `int getAutofillType()` |  |
| `android.graphics.drawable.Drawable getBackground()` |  |
| `float getBottomFadingEdgeStrength()` |  |
| `int getBottomPaddingOffset()` |  |
| `float getCameraDistance()` |  |
| `android.graphics.Rect getClipBounds()` |  |
| `boolean getClipBounds(android.graphics.Rect)` |  |
| `final boolean getClipToOutline()` |  |
| `android.view.ContextMenu.ContextMenuInfo getContextMenuInfo()` |  |
| `static int getDefaultSize(int, int)` |  |
| `android.view.Display getDisplay()` |  |
| `final int[] getDrawableState()` |  |
| `void getDrawingRect(android.graphics.Rect)` |  |
| `long getDrawingTime()` |  |
| `java.util.ArrayList<android.view.View> getFocusables(int)` |  |
| `void getFocusedRect(android.graphics.Rect)` |  |
| `android.graphics.drawable.Drawable getForeground()` |  |
| `int getForegroundGravity()` |  |
| `boolean getGlobalVisibleRect(android.graphics.Rect, android.graphics.Point)` |  |
| `final boolean getGlobalVisibleRect(android.graphics.Rect)` |  |
| `android.os.Handler getHandler()` |  |
| `final boolean getHasOverlappingRendering()` |  |
| `void getHitRect(android.graphics.Rect)` |  |
| `int getHorizontalFadingEdgeLength()` |  |
| `int getHorizontalScrollbarHeight()` |  |
| `boolean getKeepScreenOn()` |  |
| `android.view.KeyEvent.DispatcherState getKeyDispatcherState()` |  |
| `int getLayerType()` |  |
| `float getLeftFadingEdgeStrength()` |  |
| `int getLeftPaddingOffset()` |  |
| `final boolean getLocalVisibleRect(android.graphics.Rect)` |  |
| `void getLocationInSurface(@NonNull @Size(2) int[])` |  |
| `void getLocationInWindow(@Size(2) int[])` |  |
| `void getLocationOnScreen(@Size(2) int[])` |  |
| `android.graphics.Matrix getMatrix()` |  |
| `final int getMeasuredHeight()` |  |
| `final int getMeasuredState()` |  |
| `final int getMeasuredWidth()` |  |
| `int getMinimumHeight()` |  |
| `int getMinimumWidth()` |  |
| `android.view.View.OnFocusChangeListener getOnFocusChangeListener()` |  |
| `android.view.ViewOutlineProvider getOutlineProvider()` |  |
| `int getOverScrollMode()` |  |
| `android.view.ViewOverlay getOverlay()` |  |
| `int getPaddingBottom()` |  |
| `int getPaddingEnd()` |  |
| `int getPaddingLeft()` |  |
| `int getPaddingRight()` |  |
| `int getPaddingStart()` |  |
| `int getPaddingTop()` |  |
| `final android.view.ViewParent getParent()` |  |
| `android.view.ViewParent getParentForAccessibility()` |  |
| `android.view.PointerIcon getPointerIcon()` |  |
| `android.content.res.Resources getResources()` |  |
| `final boolean getRevealOnFocusHint()` |  |
| `float getRightFadingEdgeStrength()` |  |
| `int getRightPaddingOffset()` |  |
| `android.view.View getRootView()` |  |
| `android.view.WindowInsets getRootWindowInsets()` |  |
| `int getScrollBarDefaultDelayBeforeFade()` |  |
| `int getScrollBarFadeDuration()` |  |
| `int getScrollBarSize()` |  |
| `int getScrollIndicators()` |  |
| `final int getScrollX()` |  |
| `final int getScrollY()` |  |
| `android.animation.StateListAnimator getStateListAnimator()` |  |
| `int getSuggestedMinimumHeight()` |  |
| `int getSuggestedMinimumWidth()` |  |
| `Object getTag(int)` |  |
| `float getTopFadingEdgeStrength()` |  |
| `int getTopPaddingOffset()` |  |
| `android.view.TouchDelegate getTouchDelegate()` |  |
| `java.util.ArrayList<android.view.View> getTouchables()` |  |
| `long getUniqueDrawingId()` |  |
| `int getVerticalFadingEdgeLength()` |  |
| `int getVerticalScrollbarPosition()` |  |
| `int getVerticalScrollbarWidth()` |  |
| `android.view.ViewTreeObserver getViewTreeObserver()` |  |
| `int getWindowAttachCount()` |  |
| `android.view.WindowId getWindowId()` |  |
| `android.os.IBinder getWindowToken()` |  |
| `int getWindowVisibility()` |  |
| `void getWindowVisibleDisplayFrame(android.graphics.Rect)` |  |
| `boolean hasExplicitFocusable()` |  |
| `boolean hasFocusable()` |  |
| `boolean hasNestedScrollingParent()` |  |
| `boolean hasOnClickListeners()` |  |
| `boolean hasOnLongClickListeners()` |  |
| `boolean hasPointerCapture()` |  |
| `boolean hasWindowFocus()` |  |
| `static android.view.View inflate(android.content.Context, @LayoutRes int, android.view.ViewGroup)` |  |
| `void invalidate()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void invalidateOutline()` |  |
| `boolean isAccessibilityFocused()` |  |
| `boolean isAccessibilityHeading()` |  |
| `boolean isAttachedToWindow()` |  |
| `boolean isContextClickable()` |  |
| `boolean isDirty()` |  |
| `boolean isDuplicateParentStateEnabled()` |  |
| `boolean isHorizontalFadingEdgeEnabled()` |  |
| `boolean isHorizontalScrollBarEnabled()` |  |
| `boolean isImportantForAccessibility()` |  |
| `final boolean isImportantForAutofill()` |  |
| `final boolean isImportantForContentCapture()` |  |
| `boolean isInEditMode()` |  |
| `boolean isInLayout()` |  |
| `boolean isLaidOut()` |  |
| `boolean isLayoutDirectionResolved()` |  |
| `boolean isLayoutRequested()` |  |
| `boolean isLongClickable()` |  |
| `boolean isNestedScrollingEnabled()` |  |
| `boolean isPaddingOffsetRequired()` |  |
| `boolean isPaddingRelative()` |  |
| `boolean isPivotSet()` |  |
| `boolean isSaveEnabled()` |  |
| `boolean isSaveFromParentEnabled()` |  |
| `boolean isScreenReaderFocusable()` |  |
| `boolean isScrollContainer()` |  |
| `boolean isScrollbarFadingEnabled()` |  |
| `final boolean isShowingLayoutBounds()` |  |
| `boolean isShown()` |  |
| `final boolean isTemporarilyDetached()` |  |
| `boolean isTextAlignmentResolved()` |  |
| `boolean isTextDirectionResolved()` |  |
| `boolean isVerticalFadingEdgeEnabled()` |  |
| `boolean isVerticalScrollBarEnabled()` |  |
| `boolean isVisibleToUserForAutofill(int)` |  |
| `android.view.View keyboardNavigationClusterSearch(android.view.View, int)` |  |
| `void layout(int, int, int, int)` |  |
| `final void measure(int, int)` |  |
| `static int[] mergeDrawableStates(int[], int[])` |  |
| `void offsetLeftAndRight(int)` |  |
| `void offsetTopAndBottom(int)` |  |
| `android.view.WindowInsets onApplyWindowInsets(android.view.WindowInsets)` |  |
| `void onCancelPendingInputEvents()` |  |
| `boolean onCapturedPointerEvent(android.view.MotionEvent)` |  |
| `boolean onCheckIsTextEditor()` |  |
| `void onConfigurationChanged(android.content.res.Configuration)` |  |
| `void onCreateContextMenu(android.view.ContextMenu)` |  |
| `int[] onCreateDrawableState(int)` |  |
| `android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo)` |  |
| `void onDisplayHint(int)` |  |
| `boolean onDragEvent(android.view.DragEvent)` |  |
| `void onDraw(android.graphics.Canvas)` |  |
| `void onDrawForeground(android.graphics.Canvas)` |  |
| `final void onDrawScrollBars(android.graphics.Canvas)` |  |
| `boolean onFilterTouchEventForSecurity(android.view.MotionEvent)` |  |
| `void onFinishTemporaryDetach()` |  |
| `boolean onGenericMotionEvent(android.view.MotionEvent)` |  |
| `void onHoverChanged(boolean)` |  |
| `boolean onHoverEvent(android.view.MotionEvent)` |  |
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyPreIme(int, android.view.KeyEvent)` |  |
| `boolean onKeyShortcut(int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |
| `void onLayout(boolean, int, int, int, int)` |  |
| `void onMeasure(int, int)` |  |
| `void onOverScrolled(int, int, boolean, boolean)` |  |
| `void onProvideAutofillStructure(android.view.ViewStructure, int)` |  |
| `void onProvideAutofillVirtualStructure(android.view.ViewStructure, int)` |  |
| `void onProvideContentCaptureStructure(@NonNull android.view.ViewStructure, int)` |  |
| `void onProvideStructure(android.view.ViewStructure)` |  |
| `void onProvideVirtualStructure(android.view.ViewStructure)` |  |
| `android.view.PointerIcon onResolvePointerIcon(android.view.MotionEvent, int)` |  |
| `void onRtlPropertiesChanged(int)` |  |
| `void onScreenStateChanged(int)` |  |
| `void onScrollChanged(int, int, int, int)` |  |
| `boolean onSetAlpha(int)` |  |
| `void onSizeChanged(int, int, int, int)` |  |
| `void onStartTemporaryDetach()` |  |
| `boolean onTouchEvent(android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.view.MotionEvent)` |  |
| `void onVisibilityChanged(@NonNull android.view.View, int)` |  |
| `void onWindowFocusChanged(boolean)` |  |
| `void onWindowVisibilityChanged(int)` |  |
| `boolean overScrollBy(int, int, int, int, int, int, int, int, boolean)` |  |
| `boolean performAccessibilityAction(int, android.os.Bundle)` |  |
| `boolean performClick()` |  |
| `boolean performContextClick(float, float)` |  |
| `boolean performContextClick()` |  |
| `boolean performHapticFeedback(int)` |  |
| `boolean performHapticFeedback(int, int)` |  |
| `boolean performLongClick()` |  |
| `boolean performLongClick(float, float)` |  |
| `void playSoundEffect(int)` |  |
| `boolean post(Runnable)` |  |
| `boolean postDelayed(Runnable, long)` |  |
| `void postInvalidate()` |  |
| `void postInvalidate(int, int, int, int)` |  |
| `void postInvalidateDelayed(long)` |  |
| `void postInvalidateDelayed(long, int, int, int, int)` |  |
| `void postInvalidateOnAnimation()` |  |
| `void postInvalidateOnAnimation(int, int, int, int)` |  |
| `void postOnAnimation(Runnable)` |  |
| `void postOnAnimationDelayed(Runnable, long)` |  |
| `void refreshDrawableState()` |  |
| `void releasePointerCapture()` |  |
| `boolean removeCallbacks(Runnable)` |  |
| `void removeOnAttachStateChangeListener(android.view.View.OnAttachStateChangeListener)` |  |
| `void removeOnLayoutChangeListener(android.view.View.OnLayoutChangeListener)` |  |
| `void removeOnUnhandledKeyEventListener(android.view.View.OnUnhandledKeyEventListener)` |  |
| `void requestApplyInsets()` |  |
| `final boolean requestFocus()` |  |
| `final boolean requestFocus(int)` |  |
| `boolean requestFocus(int, android.graphics.Rect)` |  |
| `final boolean requestFocusFromTouch()` |  |
| `void requestPointerCapture()` |  |
| `boolean requestRectangleOnScreen(android.graphics.Rect)` |  |
| `boolean requestRectangleOnScreen(android.graphics.Rect, boolean)` |  |
| `final void requestUnbufferedDispatch(android.view.MotionEvent)` |  |
| `final void requestUnbufferedDispatch(int)` |  |
| `void resetPivot()` |  |
| `static int resolveSize(int, int)` |  |
| `static int resolveSizeAndState(int, int, int)` |  |
| `boolean restoreDefaultFocus()` |  |
| `void restoreHierarchyState(android.util.SparseArray<android.os.Parcelable>)` |  |
| `final void saveAttributeDataForStyleable(@NonNull android.content.Context, @NonNull int[], @Nullable android.util.AttributeSet, @NonNull android.content.res.TypedArray, int, int)` |  |
| `void saveHierarchyState(android.util.SparseArray<android.os.Parcelable>)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void scrollBy(int, int)` |  |
| `void scrollTo(int, int)` |  |
| `void sendAccessibilityEvent(int)` |  |
| `void sendAccessibilityEventUnchecked(android.view.accessibility.AccessibilityEvent)` |  |
| `void setAccessibilityDelegate(@Nullable android.view.View.AccessibilityDelegate)` |  |
| `void setAccessibilityHeading(boolean)` |  |
| `void setAccessibilityLiveRegion(int)` |  |
| `void setAccessibilityPaneTitle(@Nullable CharSequence)` |  |
| `void setAccessibilityTraversalAfter(@IdRes int)` |  |
| `void setAccessibilityTraversalBefore(@IdRes int)` |  |
| `void setActivated(boolean)` |  |
| `void setAlpha(@FloatRange(from=0.0, to=1.0) float)` |  |
| `void setAnimation(android.view.animation.Animation)` |  |
| `void setAnimationMatrix(@Nullable android.graphics.Matrix)` |  |
| `void setAutofillHints(@Nullable java.lang.String...)` |  |
| `void setAutofillId(@Nullable android.view.autofill.AutofillId)` |  |
| `void setBackground(android.graphics.drawable.Drawable)` |  |
| `void setBackgroundColor(@ColorInt int)` |  |
| `void setBackgroundResource(@DrawableRes int)` |  |
| `void setBackgroundTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setBackgroundTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setBackgroundTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `final void setBottom(int)` |  |
| `void setCameraDistance(float)` |  |
| `void setClickable(boolean)` |  |
| `void setClipBounds(android.graphics.Rect)` |  |
| `void setClipToOutline(boolean)` |  |
| `void setContentCaptureSession(@Nullable android.view.contentcapture.ContentCaptureSession)` |  |
| `void setContentDescription(CharSequence)` |  |
| `void setContextClickable(boolean)` |  |
| `void setDefaultFocusHighlightEnabled(boolean)` |  |
| `void setDuplicateParentStateEnabled(boolean)` |  |
| `void setElevation(float)` |  |
| `void setEnabled(boolean)` |  |
| `void setFadingEdgeLength(int)` |  |
| `void setFilterTouchesWhenObscured(boolean)` |  |
| `void setFitsSystemWindows(boolean)` |  |
| `void setFocusable(boolean)` |  |
| `void setFocusable(int)` |  |
| `void setFocusableInTouchMode(boolean)` |  |
| `void setFocusedByDefault(boolean)` |  |
| `void setForceDarkAllowed(boolean)` |  |
| `void setForeground(android.graphics.drawable.Drawable)` |  |
| `void setForegroundGravity(int)` |  |
| `void setForegroundTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setForegroundTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setForegroundTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setHapticFeedbackEnabled(boolean)` |  |
| `void setHasTransientState(boolean)` |  |
| `void setHorizontalFadingEdgeEnabled(boolean)` |  |
| `void setHorizontalScrollBarEnabled(boolean)` |  |
| `void setHorizontalScrollbarThumbDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setHorizontalScrollbarTrackDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setHovered(boolean)` |  |
| `void setId(@IdRes int)` |  |
| `void setImportantForAccessibility(int)` |  |
| `void setImportantForAutofill(int)` |  |
| `void setImportantForContentCapture(int)` |  |
| `void setKeepScreenOn(boolean)` |  |
| `void setKeyboardNavigationCluster(boolean)` |  |
| `void setLabelFor(@IdRes int)` |  |
| `void setLayerPaint(@Nullable android.graphics.Paint)` |  |
| `void setLayerType(int, @Nullable android.graphics.Paint)` |  |
| `void setLayoutDirection(int)` |  |
| `void setLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `final void setLeft(int)` |  |
| `final void setLeftTopRightBottom(int, int, int, int)` |  |
| `void setLongClickable(boolean)` |  |
| `final void setMeasuredDimension(int, int)` |  |
| `void setMinimumHeight(int)` |  |
| `void setMinimumWidth(int)` |  |
| `void setNestedScrollingEnabled(boolean)` |  |
| `void setNextClusterForwardId(@IdRes int)` |  |
| `void setNextFocusDownId(@IdRes int)` |  |
| `void setNextFocusForwardId(@IdRes int)` |  |
| `void setNextFocusLeftId(@IdRes int)` |  |
| `void setNextFocusRightId(@IdRes int)` |  |
| `void setNextFocusUpId(@IdRes int)` |  |
| `void setOnApplyWindowInsetsListener(android.view.View.OnApplyWindowInsetsListener)` |  |
| `void setOnCapturedPointerListener(android.view.View.OnCapturedPointerListener)` |  |
| `void setOnClickListener(@Nullable android.view.View.OnClickListener)` |  |
| `void setOnContextClickListener(@Nullable android.view.View.OnContextClickListener)` |  |
| `void setOnCreateContextMenuListener(android.view.View.OnCreateContextMenuListener)` |  |
| `void setOnDragListener(android.view.View.OnDragListener)` |  |
| `void setOnFocusChangeListener(android.view.View.OnFocusChangeListener)` |  |
| `void setOnGenericMotionListener(android.view.View.OnGenericMotionListener)` |  |
| `void setOnHoverListener(android.view.View.OnHoverListener)` |  |
| `void setOnKeyListener(android.view.View.OnKeyListener)` |  |
| `void setOnLongClickListener(@Nullable android.view.View.OnLongClickListener)` |  |
| `void setOnScrollChangeListener(android.view.View.OnScrollChangeListener)` |  |
| `void setOnTouchListener(android.view.View.OnTouchListener)` |  |
| `void setOutlineAmbientShadowColor(@ColorInt int)` |  |
| `void setOutlineProvider(android.view.ViewOutlineProvider)` |  |
| `void setOutlineSpotShadowColor(@ColorInt int)` |  |
| `void setOverScrollMode(int)` |  |
| `void setPadding(int, int, int, int)` |  |
| `void setPaddingRelative(int, int, int, int)` |  |
| `void setPivotX(float)` |  |
| `void setPivotY(float)` |  |
| `void setPointerIcon(android.view.PointerIcon)` |  |
| `void setPressed(boolean)` |  |
| `final void setRevealOnFocusHint(boolean)` |  |
| `final void setRight(int)` |  |
| `void setRotation(float)` |  |
| `void setRotationX(float)` |  |
| `void setRotationY(float)` |  |
| `void setSaveEnabled(boolean)` |  |
| `void setSaveFromParentEnabled(boolean)` |  |
| `void setScaleX(float)` |  |
| `void setScaleY(float)` |  |
| `void setScreenReaderFocusable(boolean)` |  |
| `void setScrollBarDefaultDelayBeforeFade(int)` |  |
| `void setScrollBarFadeDuration(int)` |  |
| `void setScrollBarSize(int)` |  |
| `void setScrollBarStyle(int)` |  |
| `void setScrollContainer(boolean)` |  |
| `void setScrollIndicators(int)` |  |
| `void setScrollIndicators(int, int)` |  |
| `void setScrollX(int)` |  |
| `void setScrollY(int)` |  |
| `void setScrollbarFadingEnabled(boolean)` |  |
| `void setSelected(boolean)` |  |
| `void setSoundEffectsEnabled(boolean)` |  |
| `void setStateDescription(@Nullable CharSequence)` |  |
| `void setStateListAnimator(android.animation.StateListAnimator)` |  |
| `void setSystemGestureExclusionRects(@NonNull java.util.List<android.graphics.Rect>)` |  |
| `void setTag(Object)` |  |
| `void setTag(int, Object)` |  |
| `void setTextAlignment(int)` |  |
| `void setTextDirection(int)` |  |
| `void setTooltipText(@Nullable CharSequence)` |  |
| `final void setTop(int)` |  |
| `void setTouchDelegate(android.view.TouchDelegate)` |  |
| `void setTransitionAlpha(float)` |  |
| `final void setTransitionName(String)` |  |
| `void setTransitionVisibility(int)` |  |
| `void setTranslationX(float)` |  |
| `void setTranslationY(float)` |  |
| `void setTranslationZ(float)` |  |
| `void setVerticalFadingEdgeEnabled(boolean)` |  |
| `void setVerticalScrollBarEnabled(boolean)` |  |
| `void setVerticalScrollbarPosition(int)` |  |
| `void setVerticalScrollbarThumbDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setVerticalScrollbarTrackDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setVisibility(int)` |  |
| `void setWillNotDraw(boolean)` |  |
| `void setWindowInsetsAnimationCallback(@Nullable android.view.WindowInsetsAnimation.Callback)` |  |
| `void setX(float)` |  |
| `void setY(float)` |  |
| `void setZ(float)` |  |
| `boolean showContextMenu()` |  |
| `boolean showContextMenu(float, float)` |  |
| `android.view.ActionMode startActionMode(android.view.ActionMode.Callback)` |  |
| `android.view.ActionMode startActionMode(android.view.ActionMode.Callback, int)` |  |
| `void startAnimation(android.view.animation.Animation)` |  |
| `final boolean startDragAndDrop(android.content.ClipData, android.view.View.DragShadowBuilder, Object, int)` |  |
| `boolean startNestedScroll(int)` |  |
| `void stopNestedScroll()` |  |
| `void transformMatrixToGlobal(@NonNull android.graphics.Matrix)` |  |
| `void transformMatrixToLocal(@NonNull android.graphics.Matrix)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |
| `void unscheduleDrawable(android.graphics.drawable.Drawable)` |  |
| `final void updateDragShadow(android.view.View.DragShadowBuilder)` |  |

---

### `class static View.AccessibilityDelegate`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `View.AccessibilityDelegate()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addExtraDataToAccessibilityNodeInfo(@NonNull android.view.View, @NonNull android.view.accessibility.AccessibilityNodeInfo, @NonNull String, @Nullable android.os.Bundle)` |  |
| `boolean dispatchPopulateAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `android.view.accessibility.AccessibilityNodeProvider getAccessibilityNodeProvider(android.view.View)` |  |
| `void onInitializeAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `void onInitializeAccessibilityNodeInfo(android.view.View, android.view.accessibility.AccessibilityNodeInfo)` |  |
| `void onPopulateAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `boolean onRequestSendAccessibilityEvent(android.view.ViewGroup, android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `boolean performAccessibilityAction(android.view.View, int, android.os.Bundle)` |  |
| `void sendAccessibilityEvent(android.view.View, int)` |  |
| `void sendAccessibilityEventUnchecked(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |

---

### `class static View.BaseSavedState`

- **Extends:** `android.view.AbsSavedState`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `View.BaseSavedState(android.os.Parcel)` |  |
| `View.BaseSavedState(android.os.Parcel, ClassLoader)` |  |
| `View.BaseSavedState(android.os.Parcelable)` |  |

---

### `class static View.DragShadowBuilder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `View.DragShadowBuilder(android.view.View)` |  |
| `View.DragShadowBuilder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.view.View getView()` |  |
| `void onDrawShadow(android.graphics.Canvas)` |  |
| `void onProvideShadowMetrics(android.graphics.Point, android.graphics.Point)` |  |

---

### `class static View.MeasureSpec`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `View.MeasureSpec()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AT_MOST = -2147483648` |  |
| `static final int EXACTLY = 1073741824` |  |
| `static final int UNSPECIFIED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int getMode(int)` |  |
| `static int getSize(int)` |  |
| `static int makeMeasureSpec(@IntRange(from=0, to=0x40000000 - 1) int, int)` |  |
| `static String toString(int)` |  |

---

### `interface static View.OnApplyWindowInsetsListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.WindowInsets onApplyWindowInsets(android.view.View, android.view.WindowInsets)` |  |

---

### `interface static View.OnAttachStateChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onViewAttachedToWindow(android.view.View)` |  |
| `void onViewDetachedFromWindow(android.view.View)` |  |

---

### `interface static View.OnCapturedPointerListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onCapturedPointer(android.view.View, android.view.MotionEvent)` |  |

---

### `interface static View.OnClickListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onClick(android.view.View)` |  |

---

### `interface static View.OnContextClickListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onContextClick(android.view.View)` |  |

---

### `interface static View.OnCreateContextMenuListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)` |  |

---

### `interface static View.OnDragListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onDrag(android.view.View, android.view.DragEvent)` |  |

---

### `interface static View.OnFocusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFocusChange(android.view.View, boolean)` |  |

---

### `interface static View.OnGenericMotionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onGenericMotion(android.view.View, android.view.MotionEvent)` |  |

---

### `interface static View.OnHoverListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onHover(android.view.View, android.view.MotionEvent)` |  |

---

### `interface static View.OnKeyListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onKey(android.view.View, int, android.view.KeyEvent)` |  |

---

### `interface static View.OnLayoutChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onLayoutChange(android.view.View, int, int, int, int, int, int, int, int)` |  |

---

### `interface static View.OnLongClickListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onLongClick(android.view.View)` |  |

---

### `interface static View.OnScrollChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onScrollChange(android.view.View, int, int, int, int)` |  |

---

### `interface static View.OnSystemUiVisibilityChangeListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static View.OnTouchListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onTouch(android.view.View, android.view.MotionEvent)` |  |

---

### `interface static View.OnUnhandledKeyEventListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onUnhandledKeyEvent(android.view.View, android.view.KeyEvent)` |  |

---

### `class final ViewAnimationUtils`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.animation.Animator createCircularReveal(android.view.View, int, int, float, float)` |  |

---

### `class ViewConfiguration`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.view.ViewConfiguration get(android.content.Context)` |  |
| `static long getDefaultActionModeHideDuration()` |  |
| `static int getDoubleTapTimeout()` |  |
| `static int getJumpTapTimeout()` |  |
| `static int getKeyRepeatDelay()` |  |
| `static int getKeyRepeatTimeout()` |  |
| `static int getLongPressTimeout()` |  |
| `static int getPressedStateDuration()` |  |
| `int getScaledDoubleTapSlop()` |  |
| `int getScaledEdgeSlop()` |  |
| `int getScaledFadingEdgeLength()` |  |
| `float getScaledHorizontalScrollFactor()` |  |
| `int getScaledHoverSlop()` |  |
| `int getScaledMaximumDrawingCacheSize()` |  |
| `int getScaledMaximumFlingVelocity()` |  |
| `int getScaledMinimumFlingVelocity()` |  |
| `int getScaledMinimumScalingSpan()` |  |
| `int getScaledOverflingDistance()` |  |
| `int getScaledOverscrollDistance()` |  |
| `int getScaledPagingTouchSlop()` |  |
| `int getScaledScrollBarSize()` |  |
| `int getScaledTouchSlop()` |  |
| `float getScaledVerticalScrollFactor()` |  |
| `int getScaledWindowTouchSlop()` |  |
| `static int getScrollBarFadeDuration()` |  |
| `static int getScrollDefaultDelay()` |  |
| `static float getScrollFriction()` |  |
| `static int getTapTimeout()` |  |
| `static long getZoomControlsTimeout()` |  |
| `boolean hasPermanentMenuKey()` |  |
| `boolean shouldShowMenuShortcutsWhenKeyboardPresent()` |  |

---

### `class ViewDebug`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewDebug()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void dumpCapturedView(String, Object)` |  |
| `abstract boolean retrieveReturn() default false` |  |
| `abstract String category() default ""` |  |
| `abstract boolean deepExport() default false` |  |
| `abstract android.view.ViewDebug.FlagToString[] flagMapping() default {}` |  |
| `abstract boolean formatToHexString() default false` |  |
| `abstract boolean hasAdjacentMapping() default false` |  |
| `abstract android.view.ViewDebug.IntToString[] indexMapping() default {}` |  |
| `abstract android.view.ViewDebug.IntToString[] mapping() default {}` |  |
| `abstract String prefix() default ""` |  |
| `abstract boolean resolveId() default false` |  |
| `abstract int equals()` |  |
| `abstract int mask()` |  |
| `abstract String name()` |  |
| `abstract boolean outputIf() default true` |  |

---

### `enum ViewDebug.HierarchyTraceType` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract int from()` |  |
| `abstract String to()` |  |

---

### `enum ViewDebug.RecyclerTraceType` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract ViewGroup`

- **Extends:** `android.view.View`
- **Implements:** `android.view.ViewManager android.view.ViewParent`
- **Annotations:** `@UiThread`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewGroup(android.content.Context)` |  |
| `ViewGroup(android.content.Context, android.util.AttributeSet)` |  |
| `ViewGroup(android.content.Context, android.util.AttributeSet, int)` |  |
| `ViewGroup(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLIP_TO_PADDING_MASK = 34` |  |
| `static final int FOCUS_AFTER_DESCENDANTS = 262144` |  |
| `static final int FOCUS_BEFORE_DESCENDANTS = 131072` |  |
| `static final int FOCUS_BLOCK_DESCENDANTS = 393216` |  |
| `static final int LAYOUT_MODE_CLIP_BOUNDS = 0` |  |
| `static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addStatesFromChildren()` |  |
| `void addView(android.view.View)` |  |
| `void addView(android.view.View, int)` |  |
| `void addView(android.view.View, int, int)` |  |
| `void addView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `void addView(android.view.View, int, android.view.ViewGroup.LayoutParams)` |  |
| `boolean addViewInLayout(android.view.View, int, android.view.ViewGroup.LayoutParams)` |  |
| `boolean addViewInLayout(android.view.View, int, android.view.ViewGroup.LayoutParams, boolean)` |  |
| `void attachLayoutAnimationParameters(android.view.View, android.view.ViewGroup.LayoutParams, int, int)` |  |
| `void attachViewToParent(android.view.View, int, android.view.ViewGroup.LayoutParams)` |  |
| `void bringChildToFront(android.view.View)` |  |
| `boolean canAnimate()` |  |
| `boolean checkLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `void childDrawableStateChanged(android.view.View)` |  |
| `void childHasTransientStateChanged(android.view.View, boolean)` |  |
| `void cleanupLayoutState(android.view.View)` |  |
| `void clearChildFocus(android.view.View)` |  |
| `void clearDisappearingChildren()` |  |
| `void debug(int)` |  |
| `void detachAllViewsFromParent()` |  |
| `void detachViewFromParent(android.view.View)` |  |
| `void detachViewFromParent(int)` |  |
| `void detachViewsFromParent(int, int)` |  |
| `void dispatchFreezeSelfOnly(android.util.SparseArray<android.os.Parcelable>)` |  |
| `void dispatchSetActivated(boolean)` |  |
| `void dispatchSetSelected(boolean)` |  |
| `void dispatchThawSelfOnly(android.util.SparseArray<android.os.Parcelable>)` |  |
| `boolean drawChild(android.graphics.Canvas, android.view.View, long)` |  |
| `void endViewTransition(android.view.View)` |  |
| `android.view.View focusSearch(android.view.View, int)` |  |
| `void focusableViewAvailable(android.view.View)` |  |
| `boolean gatherTransparentRegion(android.graphics.Region)` |  |
| `android.view.ViewGroup.LayoutParams generateDefaultLayoutParams()` |  |
| `android.view.ViewGroup.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `android.view.View getChildAt(int)` |  |
| `int getChildCount()` |  |
| `int getChildDrawingOrder(int, int)` |  |
| `final int getChildDrawingOrder(int)` |  |
| `static int getChildMeasureSpec(int, int, int)` |  |
| `boolean getChildStaticTransformation(android.view.View, android.view.animation.Transformation)` |  |
| `boolean getChildVisibleRect(android.view.View, android.graphics.Rect, android.graphics.Point)` |  |
| `android.view.View getFocusedChild()` |  |
| `android.view.animation.LayoutAnimationController getLayoutAnimation()` |  |
| `android.view.animation.Animation.AnimationListener getLayoutAnimationListener()` |  |
| `int getLayoutMode()` |  |
| `android.animation.LayoutTransition getLayoutTransition()` |  |
| `int getNestedScrollAxes()` |  |
| `android.view.ViewGroupOverlay getOverlay()` |  |
| `int indexOfChild(android.view.View)` |  |
| `boolean isLayoutSuppressed()` |  |
| `boolean isMotionEventSplittingEnabled()` |  |
| `boolean isTransitionGroup()` |  |
| `final void layout(int, int, int, int)` |  |
| `void measureChild(android.view.View, int, int)` |  |
| `void measureChildWithMargins(android.view.View, int, int, int, int)` |  |
| `void measureChildren(int, int)` |  |
| `void notifySubtreeAccessibilityStateChanged(android.view.View, android.view.View, int)` |  |
| `final void offsetDescendantRectToMyCoords(android.view.View, android.graphics.Rect)` |  |
| `final void offsetRectIntoDescendantCoords(android.view.View, android.graphics.Rect)` |  |
| `boolean onInterceptHoverEvent(android.view.MotionEvent)` |  |
| `boolean onInterceptTouchEvent(android.view.MotionEvent)` |  |
| `abstract void onLayout(boolean, int, int, int, int)` |  |
| `boolean onNestedFling(android.view.View, float, float, boolean)` |  |
| `boolean onNestedPreFling(android.view.View, float, float)` |  |
| `boolean onNestedPrePerformAccessibilityAction(android.view.View, int, android.os.Bundle)` |  |
| `void onNestedPreScroll(android.view.View, int, int, int[])` |  |
| `void onNestedScroll(android.view.View, int, int, int, int)` |  |
| `void onNestedScrollAccepted(android.view.View, android.view.View, int)` |  |
| `boolean onRequestFocusInDescendants(int, android.graphics.Rect)` |  |
| `boolean onRequestSendAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `boolean onStartNestedScroll(android.view.View, android.view.View, int)` |  |
| `void onStopNestedScroll(android.view.View)` |  |
| `void onViewAdded(android.view.View)` |  |
| `void onViewRemoved(android.view.View)` |  |
| `void recomputeViewAttributes(android.view.View)` |  |
| `void removeAllViews()` |  |
| `void removeAllViewsInLayout()` |  |
| `void removeDetachedView(android.view.View, boolean)` |  |
| `void removeView(android.view.View)` |  |
| `void removeViewAt(int)` |  |
| `void removeViewInLayout(android.view.View)` |  |
| `void removeViews(int, int)` |  |
| `void removeViewsInLayout(int, int)` |  |
| `void requestChildFocus(android.view.View, android.view.View)` |  |
| `boolean requestChildRectangleOnScreen(android.view.View, android.graphics.Rect, boolean)` |  |
| `void requestDisallowInterceptTouchEvent(boolean)` |  |
| `boolean requestSendAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `void requestTransparentRegion(android.view.View)` |  |
| `void scheduleLayoutAnimation()` |  |
| `void setAddStatesFromChildren(boolean)` |  |
| `void setChildrenDrawingOrderEnabled(boolean)` |  |
| `void setClipChildren(boolean)` |  |
| `void setClipToPadding(boolean)` |  |
| `void setDescendantFocusability(int)` |  |
| `void setLayoutAnimation(android.view.animation.LayoutAnimationController)` |  |
| `void setLayoutAnimationListener(android.view.animation.Animation.AnimationListener)` |  |
| `void setLayoutMode(int)` |  |
| `void setLayoutTransition(android.animation.LayoutTransition)` |  |
| `void setMotionEventSplittingEnabled(boolean)` |  |
| `void setOnHierarchyChangeListener(android.view.ViewGroup.OnHierarchyChangeListener)` |  |
| `void setStaticTransformationsEnabled(boolean)` |  |
| `void setTouchscreenBlocksFocus(boolean)` |  |
| `void setTransitionGroup(boolean)` |  |
| `boolean shouldDelayChildPressedState()` |  |
| `boolean showContextMenuForChild(android.view.View)` |  |
| `boolean showContextMenuForChild(android.view.View, float, float)` |  |
| `android.view.ActionMode startActionModeForChild(android.view.View, android.view.ActionMode.Callback)` |  |
| `android.view.ActionMode startActionModeForChild(android.view.View, android.view.ActionMode.Callback, int)` |  |
| `void startLayoutAnimation()` |  |
| `void startViewTransition(android.view.View)` |  |
| `void suppressLayout(boolean)` |  |
| `void updateViewLayout(android.view.View, android.view.ViewGroup.LayoutParams)` |  |

---

### `class static ViewGroup.LayoutParams`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewGroup.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `ViewGroup.LayoutParams(int, int)` |  |
| `ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MATCH_PARENT = -1` |  |
| `static final int WRAP_CONTENT = -2` |  |
| `android.view.animation.LayoutAnimationController.AnimationParameters layoutAnimationParameters` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void resolveLayoutDirection(int)` |  |
| `void setBaseAttributes(android.content.res.TypedArray, int, int)` |  |

---

### `class static ViewGroup.MarginLayoutParams`

- **Extends:** `android.view.ViewGroup.LayoutParams`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewGroup.MarginLayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `ViewGroup.MarginLayoutParams(int, int)` |  |
| `ViewGroup.MarginLayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |
| `ViewGroup.MarginLayoutParams(android.view.ViewGroup.LayoutParams)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getLayoutDirection()` |  |
| `int getMarginEnd()` |  |
| `int getMarginStart()` |  |
| `boolean isMarginRelative()` |  |
| `void setLayoutDirection(int)` |  |
| `void setMarginEnd(int)` |  |
| `void setMarginStart(int)` |  |
| `void setMargins(int, int, int, int)` |  |

---

### `interface static ViewGroup.OnHierarchyChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onChildViewAdded(android.view.View, android.view.View)` |  |
| `void onChildViewRemoved(android.view.View, android.view.View)` |  |

---

### `class ViewGroupOverlay`

- **Extends:** `android.view.ViewOverlay`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(@NonNull android.view.View)` |  |
| `void remove(@NonNull android.view.View)` |  |

---

### `interface ViewManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `void removeView(android.view.View)` |  |
| `void updateViewLayout(android.view.View, android.view.ViewGroup.LayoutParams)` |  |

---

### `class abstract ViewOutlineProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewOutlineProvider()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.view.ViewOutlineProvider BACKGROUND` |  |
| `static final android.view.ViewOutlineProvider BOUNDS` |  |
| `static final android.view.ViewOutlineProvider PADDED_BOUNDS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void getOutline(android.view.View, android.graphics.Outline)` |  |

---

### `class ViewOverlay`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(@NonNull android.graphics.drawable.Drawable)` |  |
| `void clear()` |  |
| `void remove(@NonNull android.graphics.drawable.Drawable)` |  |

---

### `interface ViewParent`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void bringChildToFront(android.view.View)` |  |
| `boolean canResolveLayoutDirection()` |  |
| `boolean canResolveTextAlignment()` |  |
| `boolean canResolveTextDirection()` |  |
| `void childDrawableStateChanged(android.view.View)` |  |
| `void childHasTransientStateChanged(android.view.View, boolean)` |  |
| `void clearChildFocus(android.view.View)` |  |
| `void createContextMenu(android.view.ContextMenu)` |  |
| `android.view.View focusSearch(android.view.View, int)` |  |
| `void focusableViewAvailable(android.view.View)` |  |
| `boolean getChildVisibleRect(android.view.View, android.graphics.Rect, android.graphics.Point)` |  |
| `int getLayoutDirection()` |  |
| `android.view.ViewParent getParent()` |  |
| `android.view.ViewParent getParentForAccessibility()` |  |
| `int getTextAlignment()` |  |
| `int getTextDirection()` |  |
| `boolean isLayoutDirectionResolved()` |  |
| `boolean isLayoutRequested()` |  |
| `boolean isTextAlignmentResolved()` |  |
| `boolean isTextDirectionResolved()` |  |
| `android.view.View keyboardNavigationClusterSearch(android.view.View, int)` |  |
| `void notifySubtreeAccessibilityStateChanged(android.view.View, @NonNull android.view.View, int)` |  |
| `default void onDescendantInvalidated(@NonNull android.view.View, @NonNull android.view.View)` |  |
| `boolean onNestedFling(android.view.View, float, float, boolean)` |  |
| `boolean onNestedPreFling(android.view.View, float, float)` |  |
| `boolean onNestedPrePerformAccessibilityAction(android.view.View, int, android.os.Bundle)` |  |
| `void onNestedPreScroll(android.view.View, int, int, int[])` |  |
| `void onNestedScroll(android.view.View, int, int, int, int)` |  |
| `void onNestedScrollAccepted(android.view.View, android.view.View, int)` |  |
| `boolean onStartNestedScroll(android.view.View, android.view.View, int)` |  |
| `void onStopNestedScroll(android.view.View)` |  |
| `void recomputeViewAttributes(android.view.View)` |  |
| `void requestChildFocus(android.view.View, android.view.View)` |  |
| `boolean requestChildRectangleOnScreen(android.view.View, android.graphics.Rect, boolean)` |  |
| `void requestDisallowInterceptTouchEvent(boolean)` |  |
| `void requestFitSystemWindows()` |  |
| `void requestLayout()` |  |
| `boolean requestSendAccessibilityEvent(android.view.View, android.view.accessibility.AccessibilityEvent)` |  |
| `void requestTransparentRegion(android.view.View)` |  |
| `boolean showContextMenuForChild(android.view.View)` |  |
| `boolean showContextMenuForChild(android.view.View, float, float)` |  |
| `android.view.ActionMode startActionModeForChild(android.view.View, android.view.ActionMode.Callback)` |  |
| `android.view.ActionMode startActionModeForChild(android.view.View, android.view.ActionMode.Callback, int)` |  |

---

### `class ViewPropertyAnimator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.ViewPropertyAnimator alpha(float)` |  |
| `android.view.ViewPropertyAnimator alphaBy(float)` |  |
| `void cancel()` |  |
| `long getDuration()` |  |
| `android.animation.TimeInterpolator getInterpolator()` |  |
| `long getStartDelay()` |  |
| `android.view.ViewPropertyAnimator rotation(float)` |  |
| `android.view.ViewPropertyAnimator rotationBy(float)` |  |
| `android.view.ViewPropertyAnimator rotationX(float)` |  |
| `android.view.ViewPropertyAnimator rotationXBy(float)` |  |
| `android.view.ViewPropertyAnimator rotationY(float)` |  |
| `android.view.ViewPropertyAnimator rotationYBy(float)` |  |
| `android.view.ViewPropertyAnimator scaleX(float)` |  |
| `android.view.ViewPropertyAnimator scaleXBy(float)` |  |
| `android.view.ViewPropertyAnimator scaleY(float)` |  |
| `android.view.ViewPropertyAnimator scaleYBy(float)` |  |
| `android.view.ViewPropertyAnimator setDuration(long)` |  |
| `android.view.ViewPropertyAnimator setInterpolator(android.animation.TimeInterpolator)` |  |
| `android.view.ViewPropertyAnimator setListener(android.animation.Animator.AnimatorListener)` |  |
| `android.view.ViewPropertyAnimator setStartDelay(long)` |  |
| `android.view.ViewPropertyAnimator setUpdateListener(android.animation.ValueAnimator.AnimatorUpdateListener)` |  |
| `void start()` |  |
| `android.view.ViewPropertyAnimator translationX(float)` |  |
| `android.view.ViewPropertyAnimator translationXBy(float)` |  |
| `android.view.ViewPropertyAnimator translationY(float)` |  |
| `android.view.ViewPropertyAnimator translationYBy(float)` |  |
| `android.view.ViewPropertyAnimator translationZ(float)` |  |
| `android.view.ViewPropertyAnimator translationZBy(float)` |  |
| `android.view.ViewPropertyAnimator withEndAction(Runnable)` |  |
| `android.view.ViewPropertyAnimator withLayer()` |  |
| `android.view.ViewPropertyAnimator withStartAction(Runnable)` |  |
| `android.view.ViewPropertyAnimator x(float)` |  |
| `android.view.ViewPropertyAnimator xBy(float)` |  |
| `android.view.ViewPropertyAnimator y(float)` |  |
| `android.view.ViewPropertyAnimator yBy(float)` |  |
| `android.view.ViewPropertyAnimator z(float)` |  |
| `android.view.ViewPropertyAnimator zBy(float)` |  |

---

### `class abstract ViewStructure`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewStructure()` |  |
| `ViewStructure.HtmlInfo()` |  |
| `ViewStructure.HtmlInfo.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract int addChildCount(int)` |  |
| `abstract void asyncCommit()` |  |
| `abstract android.view.ViewStructure asyncNewChild(int)` |  |
| `abstract int getChildCount()` |  |
| `abstract android.os.Bundle getExtras()` |  |
| `abstract CharSequence getHint()` |  |
| `abstract CharSequence getText()` |  |
| `abstract int getTextSelectionEnd()` |  |
| `abstract int getTextSelectionStart()` |  |
| `abstract boolean hasExtras()` |  |
| `abstract android.view.ViewStructure newChild(int)` |  |
| `abstract android.view.ViewStructure.HtmlInfo.Builder newHtmlInfoBuilder(@NonNull String)` |  |
| `abstract void setAccessibilityFocused(boolean)` |  |
| `abstract void setActivated(boolean)` |  |
| `abstract void setAlpha(float)` |  |
| `abstract void setAutofillHints(@Nullable String[])` |  |
| `abstract void setAutofillId(@NonNull android.view.autofill.AutofillId)` |  |
| `abstract void setAutofillId(@NonNull android.view.autofill.AutofillId, int)` |  |
| `abstract void setAutofillOptions(CharSequence[])` |  |
| `abstract void setAutofillType(int)` |  |
| `abstract void setAutofillValue(android.view.autofill.AutofillValue)` |  |
| `abstract void setCheckable(boolean)` |  |
| `abstract void setChecked(boolean)` |  |
| `abstract void setChildCount(int)` |  |
| `abstract void setClassName(String)` |  |
| `abstract void setClickable(boolean)` |  |
| `abstract void setContentDescription(CharSequence)` |  |
| `abstract void setContextClickable(boolean)` |  |
| `abstract void setDataIsSensitive(boolean)` |  |
| `abstract void setDimens(int, int, int, int, int, int)` |  |
| `abstract void setElevation(float)` |  |
| `abstract void setEnabled(boolean)` |  |
| `abstract void setFocusable(boolean)` |  |
| `abstract void setFocused(boolean)` |  |
| `abstract void setHint(CharSequence)` |  |
| `void setHintIdEntry(@NonNull String)` |  |
| `abstract void setHtmlInfo(@NonNull android.view.ViewStructure.HtmlInfo)` |  |
| `abstract void setId(int, String, String, String)` |  |
| `void setImportantForAutofill(int)` |  |
| `abstract void setInputType(int)` |  |
| `abstract void setLocaleList(android.os.LocaleList)` |  |
| `abstract void setLongClickable(boolean)` |  |
| `void setMaxTextEms(int)` |  |
| `void setMaxTextLength(int)` |  |
| `void setMinTextEms(int)` |  |
| `abstract void setOpaque(boolean)` |  |
| `abstract void setSelected(boolean)` |  |
| `abstract void setText(CharSequence)` |  |
| `abstract void setText(CharSequence, int, int)` |  |
| `void setTextIdEntry(@NonNull String)` |  |
| `abstract void setTextLines(int[], int[])` |  |
| `abstract void setTextStyle(float, int, int, int)` |  |
| `abstract void setTransformation(android.graphics.Matrix)` |  |
| `abstract void setVisibility(int)` |  |
| `abstract void setWebDomain(@Nullable String)` |  |
| `abstract android.view.ViewStructure.HtmlInfo.Builder addAttribute(@NonNull String, @NonNull String)` |  |
| `abstract android.view.ViewStructure.HtmlInfo build()` |  |

---

### `class final ViewStub`

- **Extends:** `android.view.View`
- **Annotations:** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ViewStub(android.content.Context)` |  |
| `ViewStub(android.content.Context, @LayoutRes int)` |  |
| `ViewStub(android.content.Context, android.util.AttributeSet)` |  |
| `ViewStub(android.content.Context, android.util.AttributeSet, int)` |  |
| `ViewStub(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.LayoutInflater getLayoutInflater()` |  |
| `android.view.View inflate()` |  |
| `void setInflatedId(@IdRes int)` |  |
| `void setLayoutInflater(android.view.LayoutInflater)` |  |
| `void setLayoutResource(@LayoutRes int)` |  |
| `void setOnInflateListener(android.view.ViewStub.OnInflateListener)` |  |

---

### `interface static ViewStub.OnInflateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onInflate(android.view.ViewStub, android.view.View)` |  |

---

### `class final ViewTreeObserver`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnDrawListener(android.view.ViewTreeObserver.OnDrawListener)` |  |
| `void addOnGlobalFocusChangeListener(android.view.ViewTreeObserver.OnGlobalFocusChangeListener)` |  |
| `void addOnGlobalLayoutListener(android.view.ViewTreeObserver.OnGlobalLayoutListener)` |  |
| `void addOnPreDrawListener(android.view.ViewTreeObserver.OnPreDrawListener)` |  |
| `void addOnScrollChangedListener(android.view.ViewTreeObserver.OnScrollChangedListener)` |  |
| `void addOnSystemGestureExclusionRectsChangedListener(@NonNull java.util.function.Consumer<java.util.List<android.graphics.Rect>>)` |  |
| `void addOnTouchModeChangeListener(android.view.ViewTreeObserver.OnTouchModeChangeListener)` |  |
| `void addOnWindowAttachListener(android.view.ViewTreeObserver.OnWindowAttachListener)` |  |
| `void addOnWindowFocusChangeListener(android.view.ViewTreeObserver.OnWindowFocusChangeListener)` |  |
| `void dispatchOnDraw()` |  |
| `void dispatchOnGlobalLayout()` |  |
| `boolean dispatchOnPreDraw()` |  |
| `boolean isAlive()` |  |
| `void registerFrameCommitCallback(@NonNull Runnable)` |  |
| `void removeOnDrawListener(android.view.ViewTreeObserver.OnDrawListener)` |  |
| `void removeOnGlobalFocusChangeListener(android.view.ViewTreeObserver.OnGlobalFocusChangeListener)` |  |
| `void removeOnGlobalLayoutListener(android.view.ViewTreeObserver.OnGlobalLayoutListener)` |  |
| `void removeOnPreDrawListener(android.view.ViewTreeObserver.OnPreDrawListener)` |  |
| `void removeOnScrollChangedListener(android.view.ViewTreeObserver.OnScrollChangedListener)` |  |
| `void removeOnSystemGestureExclusionRectsChangedListener(@NonNull java.util.function.Consumer<java.util.List<android.graphics.Rect>>)` |  |
| `void removeOnTouchModeChangeListener(android.view.ViewTreeObserver.OnTouchModeChangeListener)` |  |
| `void removeOnWindowAttachListener(android.view.ViewTreeObserver.OnWindowAttachListener)` |  |
| `void removeOnWindowFocusChangeListener(android.view.ViewTreeObserver.OnWindowFocusChangeListener)` |  |
| `boolean unregisterFrameCommitCallback(@NonNull Runnable)` |  |

---

### `interface static ViewTreeObserver.OnDrawListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDraw()` |  |

---

### `interface static ViewTreeObserver.OnGlobalFocusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGlobalFocusChanged(android.view.View, android.view.View)` |  |

---

### `interface static ViewTreeObserver.OnGlobalLayoutListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGlobalLayout()` |  |

---

### `interface static ViewTreeObserver.OnPreDrawListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onPreDraw()` |  |

---

### `interface static ViewTreeObserver.OnScrollChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onScrollChanged()` |  |

---

### `interface static ViewTreeObserver.OnTouchModeChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTouchModeChanged(boolean)` |  |

---

### `interface static ViewTreeObserver.OnWindowAttachListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onWindowAttached()` |  |
| `void onWindowDetached()` |  |

---

### `interface static ViewTreeObserver.OnWindowFocusChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onWindowFocusChanged(boolean)` |  |

---

### `class abstract Window`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Window(android.content.Context)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DECOR_CAPTION_SHADE_AUTO = 0` |  |
| `static final int DECOR_CAPTION_SHADE_DARK = 2` |  |
| `static final int DECOR_CAPTION_SHADE_LIGHT = 1` |  |
| `static final int FEATURE_ACTION_BAR = 8` |  |
| `static final int FEATURE_ACTION_BAR_OVERLAY = 9` |  |
| `static final int FEATURE_ACTION_MODE_OVERLAY = 10` |  |
| `static final int FEATURE_ACTIVITY_TRANSITIONS = 13` |  |
| `static final int FEATURE_CONTENT_TRANSITIONS = 12` |  |
| `static final int FEATURE_CONTEXT_MENU = 6` |  |
| `static final int FEATURE_CUSTOM_TITLE = 7` |  |
| `static final int FEATURE_LEFT_ICON = 3` |  |
| `static final int FEATURE_NO_TITLE = 1` |  |
| `static final int FEATURE_OPTIONS_PANEL = 0` |  |
| `static final int FEATURE_RIGHT_ICON = 4` |  |
| `static final int ID_ANDROID_CONTENT = 16908290` |  |
| `static final String NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = "android:navigation:background"` |  |
| `static final String STATUS_BAR_BACKGROUND_TRANSITION_NAME = "android:status:background"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void addContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `void addFlags(int)` |  |
| `final void addOnFrameMetricsAvailableListener(@NonNull android.view.Window.OnFrameMetricsAvailableListener, android.os.Handler)` |  |
| `void clearFlags(int)` |  |
| `abstract void closeAllPanels()` |  |
| `abstract void closePanel(int)` |  |
| `<T extends android.view.View> T findViewById(@IdRes int)` |  |
| `boolean getAllowEnterTransitionOverlap()` |  |
| `boolean getAllowReturnTransitionOverlap()` |  |
| `final android.view.WindowManager.LayoutParams getAttributes()` |  |
| `final android.view.Window.Callback getCallback()` |  |
| `int getColorMode()` |  |
| `final android.view.Window getContainer()` |  |
| `android.transition.Scene getContentScene()` |  |
| `final android.content.Context getContext()` |  |
| `static int getDefaultFeatures(android.content.Context)` |  |
| `android.transition.Transition getEnterTransition()` |  |
| `android.transition.Transition getExitTransition()` |  |
| `final int getFeatures()` |  |
| `final int getForcedWindowFlags()` |  |
| `final int getLocalFeatures()` |  |
| `android.media.session.MediaController getMediaController()` |  |
| `android.transition.Transition getReenterTransition()` |  |
| `android.transition.Transition getReturnTransition()` |  |
| `android.transition.Transition getSharedElementEnterTransition()` |  |
| `android.transition.Transition getSharedElementExitTransition()` |  |
| `android.transition.Transition getSharedElementReenterTransition()` |  |
| `android.transition.Transition getSharedElementReturnTransition()` |  |
| `boolean getSharedElementsUseOverlay()` |  |
| `long getTransitionBackgroundFadeDuration()` |  |
| `android.transition.TransitionManager getTransitionManager()` |  |
| `abstract int getVolumeControlStream()` |  |
| `android.view.WindowManager getWindowManager()` |  |
| `final android.content.res.TypedArray getWindowStyle()` |  |
| `final boolean hasChildren()` |  |
| `boolean hasFeature(int)` |  |
| `final boolean hasSoftInputMode()` |  |
| `void injectInputEvent(android.view.InputEvent)` |  |
| `abstract void invalidatePanelMenu(int)` |  |
| `final boolean isActive()` |  |
| `abstract boolean isFloating()` |  |
| `boolean isNavigationBarContrastEnforced()` |  |
| `abstract boolean isShortcutKey(int, android.view.KeyEvent)` |  |
| `boolean isStatusBarContrastEnforced()` |  |
| `boolean isWideColorGamut()` |  |
| `final void makeActive()` |  |
| `abstract void onActive()` |  |
| `abstract void onConfigurationChanged(android.content.res.Configuration)` |  |
| `abstract void openPanel(int, android.view.KeyEvent)` |  |
| `abstract android.view.View peekDecorView()` |  |
| `abstract boolean performContextMenuIdentifierAction(int, int)` |  |
| `abstract boolean performPanelIdentifierAction(int, int, int)` |  |
| `abstract boolean performPanelShortcut(int, int, android.view.KeyEvent, int)` |  |
| `final void removeOnFrameMetricsAvailableListener(android.view.Window.OnFrameMetricsAvailableListener)` |  |
| `boolean requestFeature(int)` |  |
| `abstract void restoreHierarchyState(android.os.Bundle)` |  |
| `abstract android.os.Bundle saveHierarchyState()` |  |
| `void setAllowEnterTransitionOverlap(boolean)` |  |
| `void setAllowReturnTransitionOverlap(boolean)` |  |
| `void setAttributes(android.view.WindowManager.LayoutParams)` |  |
| `abstract void setBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setBackgroundDrawableResource(@DrawableRes int)` |  |
| `void setCallback(android.view.Window.Callback)` |  |
| `abstract void setChildDrawable(int, android.graphics.drawable.Drawable)` |  |
| `abstract void setChildInt(int, int)` |  |
| `void setClipToOutline(boolean)` |  |
| `void setColorMode(int)` |  |
| `void setContainer(android.view.Window)` |  |
| `abstract void setContentView(@LayoutRes int)` |  |
| `abstract void setContentView(android.view.View)` |  |
| `abstract void setContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `abstract void setDecorCaptionShade(int)` |  |
| `void setDecorFitsSystemWindows(boolean)` |  |
| `void setDefaultWindowFormat(int)` |  |
| `void setDimAmount(float)` |  |
| `void setElevation(float)` |  |
| `void setEnterTransition(android.transition.Transition)` |  |
| `void setExitTransition(android.transition.Transition)` |  |
| `abstract void setFeatureDrawable(int, android.graphics.drawable.Drawable)` |  |
| `abstract void setFeatureDrawableAlpha(int, int)` |  |
| `abstract void setFeatureDrawableResource(int, @DrawableRes int)` |  |
| `abstract void setFeatureDrawableUri(int, android.net.Uri)` |  |
| `abstract void setFeatureInt(int, int)` |  |
| `void setFlags(int, int)` |  |
| `void setFormat(int)` |  |
| `void setGravity(int)` |  |
| `void setIcon(@DrawableRes int)` |  |
| `void setLayout(int, int)` |  |
| `void setLocalFocus(boolean, boolean)` |  |
| `void setLogo(@DrawableRes int)` |  |
| `void setMediaController(android.media.session.MediaController)` |  |
| `abstract void setNavigationBarColor(@ColorInt int)` |  |
| `void setNavigationBarContrastEnforced(boolean)` |  |
| `void setNavigationBarDividerColor(@ColorInt int)` |  |
| `void setPreferMinimalPostProcessing(boolean)` |  |
| `void setReenterTransition(android.transition.Transition)` |  |
| `abstract void setResizingCaptionDrawable(android.graphics.drawable.Drawable)` |  |
| `final void setRestrictedCaptionAreaListener(android.view.Window.OnRestrictedCaptionAreaChangedListener)` |  |
| `void setReturnTransition(android.transition.Transition)` |  |
| `void setSharedElementEnterTransition(android.transition.Transition)` |  |
| `void setSharedElementExitTransition(android.transition.Transition)` |  |
| `void setSharedElementReenterTransition(android.transition.Transition)` |  |
| `void setSharedElementReturnTransition(android.transition.Transition)` |  |
| `void setSharedElementsUseOverlay(boolean)` |  |
| `void setSoftInputMode(int)` |  |
| `abstract void setStatusBarColor(@ColorInt int)` |  |
| `void setStatusBarContrastEnforced(boolean)` |  |
| `void setSustainedPerformanceMode(boolean)` |  |
| `void setSystemGestureExclusionRects(@NonNull java.util.List<android.graphics.Rect>)` |  |
| `abstract void setTitle(CharSequence)` |  |
| `void setTransitionBackgroundFadeDuration(long)` |  |
| `void setTransitionManager(android.transition.TransitionManager)` |  |
| `void setType(int)` |  |
| `void setUiOptions(int)` |  |
| `void setUiOptions(int, int)` |  |
| `abstract void setVolumeControlStream(int)` |  |
| `void setWindowAnimations(@StyleRes int)` |  |
| `void setWindowManager(android.view.WindowManager, android.os.IBinder, String)` |  |
| `void setWindowManager(android.view.WindowManager, android.os.IBinder, String, boolean)` |  |
| `abstract boolean superDispatchGenericMotionEvent(android.view.MotionEvent)` |  |
| `abstract boolean superDispatchKeyEvent(android.view.KeyEvent)` |  |
| `abstract boolean superDispatchKeyShortcutEvent(android.view.KeyEvent)` |  |
| `abstract boolean superDispatchTouchEvent(android.view.MotionEvent)` |  |
| `abstract boolean superDispatchTrackballEvent(android.view.MotionEvent)` |  |
| `abstract void takeInputQueue(android.view.InputQueue.Callback)` |  |
| `abstract void takeKeyEvents(boolean)` |  |
| `abstract void takeSurface(android.view.SurfaceHolder.Callback2)` |  |
| `abstract void togglePanel(int, android.view.KeyEvent)` |  |

---

### `interface static Window.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean dispatchGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean dispatchKeyEvent(android.view.KeyEvent)` |  |
| `boolean dispatchKeyShortcutEvent(android.view.KeyEvent)` |  |
| `boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |
| `boolean dispatchTouchEvent(android.view.MotionEvent)` |  |
| `boolean dispatchTrackballEvent(android.view.MotionEvent)` |  |
| `void onActionModeFinished(android.view.ActionMode)` |  |
| `void onActionModeStarted(android.view.ActionMode)` |  |
| `void onAttachedToWindow()` |  |
| `void onContentChanged()` |  |
| `boolean onCreatePanelMenu(int, @NonNull android.view.Menu)` |  |
| `void onDetachedFromWindow()` |  |
| `boolean onMenuItemSelected(int, @NonNull android.view.MenuItem)` |  |
| `boolean onMenuOpened(int, @NonNull android.view.Menu)` |  |
| `void onPanelClosed(int, @NonNull android.view.Menu)` |  |
| `default void onPointerCaptureChanged(boolean)` |  |
| `boolean onPreparePanel(int, @Nullable android.view.View, @NonNull android.view.Menu)` |  |
| `default void onProvideKeyboardShortcuts(java.util.List<android.view.KeyboardShortcutGroup>, @Nullable android.view.Menu, int)` |  |
| `boolean onSearchRequested()` |  |
| `boolean onSearchRequested(android.view.SearchEvent)` |  |
| `void onWindowAttributesChanged(android.view.WindowManager.LayoutParams)` |  |
| `void onWindowFocusChanged(boolean)` |  |

---

### `interface static Window.OnFrameMetricsAvailableListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFrameMetricsAvailable(android.view.Window, android.view.FrameMetrics, int)` |  |

---

### `interface static Window.OnRestrictedCaptionAreaChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onRestrictedCaptionAreaChanged(android.graphics.Rect)` |  |

---

### `class final WindowAnimationFrameStats`

- **Extends:** `android.view.FrameStats`
- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final WindowContentFrameStats`

- **Extends:** `android.view.FrameStats`
- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getFramePostedTimeNano(int)` |  |
| `long getFrameReadyTimeNano(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WindowId`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowId.FocusObserver()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isFocused()` |  |
| `void registerFocusObserver(android.view.WindowId.FocusObserver)` |  |
| `void unregisterFocusObserver(android.view.WindowId.FocusObserver)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `abstract void onFocusGained(android.view.WindowId)` |  |
| `abstract void onFocusLost(android.view.WindowId)` |  |

---

### `class final WindowInsets`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowInsets(android.view.WindowInsets)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasInsets()` |  |
| `boolean isConsumed()` |  |
| `boolean isRound()` |  |
| `boolean isVisible(int)` |  |

---

### `class static final WindowInsets.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowInsets.Builder()` |  |
| `WindowInsets.Builder(@NonNull android.view.WindowInsets)` |  |

---

### `class static final WindowInsets.Side`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BOTTOM = 8` |  |
| `static final int LEFT = 1` |  |
| `static final int RIGHT = 4` |  |
| `static final int TOP = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int all()` |  |

---

### `class static final WindowInsets.Type`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int captionBar()` |  |
| `static int displayCutout()` |  |
| `static int ime()` |  |
| `static int mandatorySystemGestures()` |  |
| `static int navigationBars()` |  |
| `static int statusBars()` |  |
| `static int systemBars()` |  |
| `static int systemGestures()` |  |
| `static int tappableElement()` |  |

---

### `class final WindowInsetsAnimation`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowInsetsAnimation(int, @Nullable android.view.animation.Interpolator, long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getDurationMillis()` |  |
| `float getInterpolatedFraction()` |  |
| `int getTypeMask()` |  |
| `void setAlpha(@FloatRange(from=0.0f, to=1.0f) float)` |  |
| `void setFraction(@FloatRange(from=0.0f, to=1.0f) float)` |  |

---

### `class static final WindowInsetsAnimation.Bounds`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowInsetsAnimation.Bounds(@NonNull android.graphics.Insets, @NonNull android.graphics.Insets)` |  |
| `WindowInsetsAnimation.Callback(int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DISPATCH_MODE_CONTINUE_ON_SUBTREE = 1` |  |
| `static final int DISPATCH_MODE_STOP = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int getDispatchMode()` |  |
| `void onEnd(@NonNull android.view.WindowInsetsAnimation)` |  |
| `void onPrepare(@NonNull android.view.WindowInsetsAnimation)` |  |

---

### `interface WindowInsetsAnimationControlListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCancelled(@Nullable android.view.WindowInsetsAnimationController)` |  |
| `void onFinished(@NonNull android.view.WindowInsetsAnimationController)` |  |
| `void onReady(@NonNull android.view.WindowInsetsAnimationController, int)` |  |

---

### `interface WindowInsetsAnimationController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finish(boolean)` |  |
| `float getCurrentAlpha()` |  |
| `int getTypes()` |  |
| `boolean isCancelled()` |  |
| `boolean isFinished()` |  |
| `default boolean isReady()` |  |
| `void setInsetsAndAlpha(@Nullable android.graphics.Insets, @FloatRange(from=0.0f, to=1.0f) float, @FloatRange(from=0.0f, to=1.0f) float)` |  |

---

### `interface WindowInsetsController`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int APPEARANCE_LIGHT_NAVIGATION_BARS = 16` |  |
| `static final int APPEARANCE_LIGHT_STATUS_BARS = 8` |  |
| `static final int BEHAVIOR_SHOW_BARS_BY_SWIPE = 1` |  |
| `static final int BEHAVIOR_SHOW_BARS_BY_TOUCH = 0` |  |
| `static final int BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnControllableInsetsChangedListener(@NonNull android.view.WindowInsetsController.OnControllableInsetsChangedListener)` |  |
| `void controlWindowInsetsAnimation(int, long, @Nullable android.view.animation.Interpolator, @Nullable android.os.CancellationSignal, @NonNull android.view.WindowInsetsAnimationControlListener)` |  |
| `int getSystemBarsAppearance()` |  |
| `int getSystemBarsBehavior()` |  |
| `void hide(int)` |  |
| `void removeOnControllableInsetsChangedListener(@NonNull android.view.WindowInsetsController.OnControllableInsetsChangedListener)` |  |
| `void setSystemBarsAppearance(int, int)` |  |
| `void setSystemBarsBehavior(int)` |  |
| `void show(int)` |  |

---

### `interface static WindowInsetsController.OnControllableInsetsChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onControllableInsetsChanged(@NonNull android.view.WindowInsetsController, int)` |  |

---

### `interface WindowManager`

- **Extends:** `android.view.ViewManager`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void removeViewImmediate(android.view.View)` |  |

---

### `class static WindowManager.BadTokenException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowManager.BadTokenException()` |  |
| `WindowManager.BadTokenException(String)` |  |

---

### `class static WindowManager.InvalidDisplayException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowManager.InvalidDisplayException()` |  |
| `WindowManager.InvalidDisplayException(String)` |  |

---

### `class static WindowManager.LayoutParams`

- **Extends:** `android.view.ViewGroup.LayoutParams`
- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowManager.LayoutParams()` |  |
| `WindowManager.LayoutParams(int)` |  |
| `WindowManager.LayoutParams(int, int)` |  |
| `WindowManager.LayoutParams(int, int, int)` |  |
| `WindowManager.LayoutParams(int, int, int, int, int)` |  |
| `WindowManager.LayoutParams(int, int, int, int, int, int, int)` |  |
| `WindowManager.LayoutParams(android.os.Parcel)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALPHA_CHANGED = 128` |  |
| `static final int ANIMATION_CHANGED = 16` |  |
| `static final float BRIGHTNESS_OVERRIDE_FULL = 1.0f` |  |
| `static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f` |  |
| `static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f` |  |
| `static final int DIM_AMOUNT_CHANGED = 32` |  |
| `static final int FIRST_APPLICATION_WINDOW = 1` |  |
| `static final int FIRST_SUB_WINDOW = 1000` |  |
| `static final int FIRST_SYSTEM_WINDOW = 2000` |  |
| `static final int FLAGS_CHANGED = 4` |  |
| `static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 1` |  |
| `static final int FLAG_ALT_FOCUSABLE_IM = 131072` |  |
| `static final int FLAG_DIM_BEHIND = 2` |  |
| `static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = -2147483648` |  |
| `static final int FLAG_HARDWARE_ACCELERATED = 16777216` |  |
| `static final int FLAG_IGNORE_CHEEK_PRESSES = 32768` |  |
| `static final int FLAG_KEEP_SCREEN_ON = 128` |  |
| `static final int FLAG_LAYOUT_IN_SCREEN = 256` |  |
| `static final int FLAG_LAYOUT_NO_LIMITS = 512` |  |
| `static final int FLAG_LOCAL_FOCUS_MODE = 268435456` |  |
| `static final int FLAG_NOT_FOCUSABLE = 8` |  |
| `static final int FLAG_NOT_TOUCHABLE = 16` |  |
| `static final int FLAG_NOT_TOUCH_MODAL = 32` |  |
| `static final int FLAG_SCALED = 16384` |  |
| `static final int FLAG_SECURE = 8192` |  |
| `static final int FLAG_SHOW_WALLPAPER = 1048576` |  |
| `static final int FLAG_SPLIT_TOUCH = 8388608` |  |
| `static final int FLAG_WATCH_OUTSIDE_TOUCH = 262144` |  |
| `static final int FORMAT_CHANGED = 8` |  |
| `static final int LAST_APPLICATION_WINDOW = 99` |  |
| `static final int LAST_SUB_WINDOW = 1999` |  |
| `static final int LAST_SYSTEM_WINDOW = 2999` |  |
| `static final int LAYOUT_CHANGED = 1` |  |
| `static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS = 3` |  |
| `static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT = 0` |  |
| `static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER = 2` |  |
| `static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1` |  |
| `static final int MEMORY_TYPE_CHANGED = 256` |  |
| `static final int ROTATION_ANIMATION_CHANGED = 4096` |  |
| `static final int ROTATION_ANIMATION_CROSSFADE = 1` |  |
| `static final int ROTATION_ANIMATION_JUMPCUT = 2` |  |
| `static final int ROTATION_ANIMATION_ROTATE = 0` |  |
| `static final int ROTATION_ANIMATION_SEAMLESS = 3` |  |
| `static final int SCREEN_BRIGHTNESS_CHANGED = 2048` |  |
| `static final int SCREEN_ORIENTATION_CHANGED = 1024` |  |
| `static final int SOFT_INPUT_ADJUST_NOTHING = 48` |  |
| `static final int SOFT_INPUT_ADJUST_PAN = 32` |  |
| `static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0` |  |
| `static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 256` |  |
| `static final int SOFT_INPUT_MASK_ADJUST = 240` |  |
| `static final int SOFT_INPUT_MASK_STATE = 15` |  |
| `static final int SOFT_INPUT_MODE_CHANGED = 512` |  |
| `static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3` |  |
| `static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5` |  |
| `static final int SOFT_INPUT_STATE_HIDDEN = 2` |  |
| `static final int SOFT_INPUT_STATE_UNCHANGED = 1` |  |
| `static final int SOFT_INPUT_STATE_UNSPECIFIED = 0` |  |
| `static final int SOFT_INPUT_STATE_VISIBLE = 4` |  |
| `static final int TITLE_CHANGED = 64` |  |
| `static final int TYPE_ACCESSIBILITY_OVERLAY = 2032` |  |
| `static final int TYPE_APPLICATION = 2` |  |
| `static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003` |  |
| `static final int TYPE_APPLICATION_MEDIA = 1001` |  |
| `static final int TYPE_APPLICATION_OVERLAY = 2038` |  |
| `static final int TYPE_APPLICATION_PANEL = 1000` |  |
| `static final int TYPE_APPLICATION_STARTING = 3` |  |
| `static final int TYPE_APPLICATION_SUB_PANEL = 1002` |  |
| `static final int TYPE_BASE_APPLICATION = 1` |  |
| `static final int TYPE_CHANGED = 2` |  |
| `static final int TYPE_DRAWN_APPLICATION = 4` |  |
| `static final int TYPE_INPUT_METHOD = 2011` |  |
| `static final int TYPE_INPUT_METHOD_DIALOG = 2012` |  |
| `static final int TYPE_KEYGUARD_DIALOG = 2009` |  |
| `static final int TYPE_PRIVATE_PRESENTATION = 2030` |  |
| `static final int TYPE_SEARCH_BAR = 2001` |  |
| `static final int TYPE_STATUS_BAR = 2000` |  |
| `static final int TYPE_SYSTEM_DIALOG = 2008` |  |
| `static final int TYPE_WALLPAPER = 2013` |  |
| `float alpha` |  |
| `float buttonBrightness` |  |
| `float dimAmount` |  |
| `int format` |  |
| `int gravity` |  |
| `float horizontalMargin` |  |
| `int layoutInDisplayCutoutMode` |  |
| `String packageName` |  |
| `boolean preferMinimalPostProcessing` |  |
| `int preferredDisplayModeId` |  |
| `int rotationAnimation` |  |
| `float screenBrightness` |  |
| `int screenOrientation` |  |
| `int softInputMode` |  |
| `android.os.IBinder token` |  |
| `float verticalMargin` |  |
| `int windowAnimations` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int copyFrom(android.view.WindowManager.LayoutParams)` |  |
| `String debug(String)` |  |
| `int describeContents()` |  |
| `int getColorMode()` |  |
| `int getFitInsetsSides()` |  |
| `int getFitInsetsTypes()` |  |
| `final CharSequence getTitle()` |  |
| `boolean isFitInsetsIgnoringVisibility()` |  |
| `static boolean mayUseInputMethod(int)` |  |
| `void setColorMode(int)` |  |
| `void setFitInsetsIgnoringVisibility(boolean)` |  |
| `void setFitInsetsSides(int)` |  |
| `void setFitInsetsTypes(int)` |  |
| `final void setTitle(CharSequence)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final WindowMetrics`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WindowMetrics(@NonNull android.graphics.Rect, @NonNull android.view.WindowInsets)` |  |

---

## Package: `android.view.accessibility`

### `class final AccessibilityEvent`

- **Extends:** `android.view.accessibility.AccessibilityRecord`
- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityEvent()` |  |
| `AccessibilityEvent(int)` |  |
| `AccessibilityEvent(@NonNull android.view.accessibility.AccessibilityEvent)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4` |  |
| `static final int CONTENT_CHANGE_TYPE_PANE_APPEARED = 16` |  |
| `static final int CONTENT_CHANGE_TYPE_PANE_DISAPPEARED = 32` |  |
| `static final int CONTENT_CHANGE_TYPE_PANE_TITLE = 8` |  |
| `static final int CONTENT_CHANGE_TYPE_STATE_DESCRIPTION = 64` |  |
| `static final int CONTENT_CHANGE_TYPE_SUBTREE = 1` |  |
| `static final int CONTENT_CHANGE_TYPE_TEXT = 2` |  |
| `static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0` |  |
| `static final int INVALID_POSITION = -1` |  |
| `static final int TYPES_ALL_MASK = -1` |  |
| `static final int TYPE_ANNOUNCEMENT = 16384` |  |
| `static final int TYPE_ASSIST_READING_CONTEXT = 16777216` |  |
| `static final int TYPE_GESTURE_DETECTION_END = 524288` |  |
| `static final int TYPE_GESTURE_DETECTION_START = 262144` |  |
| `static final int TYPE_NOTIFICATION_STATE_CHANGED = 64` |  |
| `static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024` |  |
| `static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512` |  |
| `static final int TYPE_TOUCH_INTERACTION_END = 2097152` |  |
| `static final int TYPE_TOUCH_INTERACTION_START = 1048576` |  |
| `static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768` |  |
| `static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536` |  |
| `static final int TYPE_VIEW_CLICKED = 1` |  |
| `static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608` |  |
| `static final int TYPE_VIEW_FOCUSED = 8` |  |
| `static final int TYPE_VIEW_HOVER_ENTER = 128` |  |
| `static final int TYPE_VIEW_HOVER_EXIT = 256` |  |
| `static final int TYPE_VIEW_LONG_CLICKED = 2` |  |
| `static final int TYPE_VIEW_SCROLLED = 4096` |  |
| `static final int TYPE_VIEW_SELECTED = 4` |  |
| `static final int TYPE_VIEW_TEXT_CHANGED = 16` |  |
| `static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192` |  |
| `static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072` |  |
| `static final int TYPE_WINDOWS_CHANGED = 4194304` |  |
| `static final int TYPE_WINDOW_CONTENT_CHANGED = 2048` |  |
| `static final int TYPE_WINDOW_STATE_CHANGED = 32` |  |
| `static final int WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED = 128` |  |
| `static final int WINDOWS_CHANGE_ACTIVE = 32` |  |
| `static final int WINDOWS_CHANGE_ADDED = 1` |  |
| `static final int WINDOWS_CHANGE_BOUNDS = 8` |  |
| `static final int WINDOWS_CHANGE_CHILDREN = 512` |  |
| `static final int WINDOWS_CHANGE_FOCUSED = 64` |  |
| `static final int WINDOWS_CHANGE_LAYER = 16` |  |
| `static final int WINDOWS_CHANGE_PARENT = 256` |  |
| `static final int WINDOWS_CHANGE_PIP = 1024` |  |
| `static final int WINDOWS_CHANGE_REMOVED = 2` |  |
| `static final int WINDOWS_CHANGE_TITLE = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void appendRecord(android.view.accessibility.AccessibilityRecord)` |  |
| `int describeContents()` |  |
| `static String eventTypeToString(int)` |  |
| `int getAction()` |  |
| `int getContentChangeTypes()` |  |
| `long getEventTime()` |  |
| `int getEventType()` |  |
| `int getMovementGranularity()` |  |
| `CharSequence getPackageName()` |  |
| `android.view.accessibility.AccessibilityRecord getRecord(int)` |  |
| `int getRecordCount()` |  |
| `int getWindowChanges()` |  |
| `void initFromParcel(android.os.Parcel)` |  |
| `static android.view.accessibility.AccessibilityEvent obtain(int)` |  |
| `static android.view.accessibility.AccessibilityEvent obtain(android.view.accessibility.AccessibilityEvent)` |  |
| `static android.view.accessibility.AccessibilityEvent obtain()` |  |
| `void setAction(int)` |  |
| `void setContentChangeTypes(int)` |  |
| `void setEventTime(long)` |  |
| `void setEventType(int)` |  |
| `void setMovementGranularity(int)` |  |
| `void setPackageName(CharSequence)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface AccessibilityEventSource`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void sendAccessibilityEvent(int)` |  |
| `void sendAccessibilityEventUnchecked(android.view.accessibility.AccessibilityEvent)` |  |

---

### `class final AccessibilityManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_CONTENT_CONTROLS = 4` |  |
| `static final int FLAG_CONTENT_ICONS = 1` |  |
| `static final int FLAG_CONTENT_TEXT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addAccessibilityRequestPreparer(android.view.accessibility.AccessibilityRequestPreparer)` |  |
| `boolean addAccessibilityStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener)` |  |
| `void addAccessibilityStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener, @Nullable android.os.Handler)` |  |
| `boolean addTouchExplorationStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener)` |  |
| `void addTouchExplorationStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener, @Nullable android.os.Handler)` |  |
| `java.util.List<android.accessibilityservice.AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int)` |  |
| `java.util.List<android.accessibilityservice.AccessibilityServiceInfo> getInstalledAccessibilityServiceList()` |  |
| `int getRecommendedTimeoutMillis(int, int)` |  |
| `void interrupt()` |  |
| `static boolean isAccessibilityButtonSupported()` |  |
| `boolean isEnabled()` |  |
| `boolean isTouchExplorationEnabled()` |  |
| `void removeAccessibilityRequestPreparer(android.view.accessibility.AccessibilityRequestPreparer)` |  |
| `boolean removeAccessibilityStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener)` |  |
| `boolean removeTouchExplorationStateChangeListener(@NonNull android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener)` |  |
| `void sendAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |

---

### `interface static AccessibilityManager.AccessibilityStateChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAccessibilityStateChanged(boolean)` |  |

---

### `interface static AccessibilityManager.TouchExplorationStateChangeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTouchExplorationStateChanged(boolean)` |  |

---

### `class AccessibilityNodeInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo()` |  |
| `AccessibilityNodeInfo(@NonNull android.view.View)` |  |
| `AccessibilityNodeInfo(@NonNull android.view.View, int)` |  |
| `AccessibilityNodeInfo(@NonNull android.view.accessibility.AccessibilityNodeInfo)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACTION_ACCESSIBILITY_FOCUS = 64` |  |
| `static final String ACTION_ARGUMENT_COLUMN_INT = "android.view.accessibility.action.ARGUMENT_COLUMN_INT"` |  |
| `static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN"` |  |
| `static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING"` |  |
| `static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT"` |  |
| `static final String ACTION_ARGUMENT_MOVE_WINDOW_X = "ACTION_ARGUMENT_MOVE_WINDOW_X"` |  |
| `static final String ACTION_ARGUMENT_MOVE_WINDOW_Y = "ACTION_ARGUMENT_MOVE_WINDOW_Y"` |  |
| `static final String ACTION_ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT = "android.view.accessibility.action.ARGUMENT_PRESS_AND_HOLD_DURATION_MILLIS_INT"` |  |
| `static final String ACTION_ARGUMENT_PROGRESS_VALUE = "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"` |  |
| `static final String ACTION_ARGUMENT_ROW_INT = "android.view.accessibility.action.ARGUMENT_ROW_INT"` |  |
| `static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT"` |  |
| `static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT"` |  |
| `static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE"` |  |
| `static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128` |  |
| `static final int ACTION_CLEAR_FOCUS = 2` |  |
| `static final int ACTION_CLEAR_SELECTION = 8` |  |
| `static final int ACTION_CLICK = 16` |  |
| `static final int ACTION_COLLAPSE = 524288` |  |
| `static final int ACTION_COPY = 16384` |  |
| `static final int ACTION_CUT = 65536` |  |
| `static final int ACTION_DISMISS = 1048576` |  |
| `static final int ACTION_EXPAND = 262144` |  |
| `static final int ACTION_FOCUS = 1` |  |
| `static final int ACTION_LONG_CLICK = 32` |  |
| `static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256` |  |
| `static final int ACTION_NEXT_HTML_ELEMENT = 1024` |  |
| `static final int ACTION_PASTE = 32768` |  |
| `static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512` |  |
| `static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048` |  |
| `static final int ACTION_SCROLL_BACKWARD = 8192` |  |
| `static final int ACTION_SCROLL_FORWARD = 4096` |  |
| `static final int ACTION_SELECT = 4` |  |
| `static final int ACTION_SET_SELECTION = 131072` |  |
| `static final int ACTION_SET_TEXT = 2097152` |  |
| `static final String EXTRA_DATA_RENDERING_INFO_KEY = "android.view.accessibility.extra.DATA_RENDERING_INFO_KEY"` |  |
| `static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH"` |  |
| `static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX"` |  |
| `static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_KEY"` |  |
| `static final int FOCUS_ACCESSIBILITY = 2` |  |
| `static final int FOCUS_INPUT = 1` |  |
| `static final int MOVEMENT_GRANULARITY_CHARACTER = 1` |  |
| `static final int MOVEMENT_GRANULARITY_LINE = 4` |  |
| `static final int MOVEMENT_GRANULARITY_PAGE = 16` |  |
| `static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8` |  |
| `static final int MOVEMENT_GRANULARITY_WORD = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addAction(android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction)` |  |
| `void addChild(android.view.View)` |  |
| `void addChild(android.view.View, int)` |  |
| `boolean canOpenPopup()` |  |
| `int describeContents()` |  |
| `java.util.List<android.view.accessibility.AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String)` |  |
| `java.util.List<android.view.accessibility.AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(String)` |  |
| `android.view.accessibility.AccessibilityNodeInfo findFocus(int)` |  |
| `android.view.accessibility.AccessibilityNodeInfo focusSearch(int)` |  |
| `java.util.List<android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction> getActionList()` |  |
| `java.util.List<java.lang.String> getAvailableExtraData()` |  |
| `void getBoundsInScreen(android.graphics.Rect)` |  |
| `android.view.accessibility.AccessibilityNodeInfo getChild(int)` |  |
| `int getChildCount()` |  |
| `CharSequence getClassName()` |  |
| `android.view.accessibility.AccessibilityNodeInfo.CollectionInfo getCollectionInfo()` |  |
| `android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo getCollectionItemInfo()` |  |
| `CharSequence getContentDescription()` |  |
| `int getDrawingOrder()` |  |
| `CharSequence getError()` |  |
| `android.os.Bundle getExtras()` |  |
| `CharSequence getHintText()` |  |
| `int getInputType()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getLabelFor()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getLabeledBy()` |  |
| `int getLiveRegion()` |  |
| `int getMaxTextLength()` |  |
| `int getMovementGranularities()` |  |
| `CharSequence getPackageName()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getParent()` |  |
| `android.view.accessibility.AccessibilityNodeInfo.RangeInfo getRangeInfo()` |  |
| `CharSequence getText()` |  |
| `int getTextSelectionEnd()` |  |
| `int getTextSelectionStart()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getTraversalAfter()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getTraversalBefore()` |  |
| `String getViewIdResourceName()` |  |
| `android.view.accessibility.AccessibilityWindowInfo getWindow()` |  |
| `int getWindowId()` |  |
| `boolean isAccessibilityFocused()` |  |
| `boolean isCheckable()` |  |
| `boolean isChecked()` |  |
| `boolean isClickable()` |  |
| `boolean isContentInvalid()` |  |
| `boolean isContextClickable()` |  |
| `boolean isDismissable()` |  |
| `boolean isEditable()` |  |
| `boolean isEnabled()` |  |
| `boolean isFocusable()` |  |
| `boolean isFocused()` |  |
| `boolean isHeading()` |  |
| `boolean isImportantForAccessibility()` |  |
| `boolean isLongClickable()` |  |
| `boolean isMultiLine()` |  |
| `boolean isPassword()` |  |
| `boolean isScreenReaderFocusable()` |  |
| `boolean isScrollable()` |  |
| `boolean isSelected()` |  |
| `boolean isShowingHintText()` |  |
| `boolean isTextEntryKey()` |  |
| `boolean isVisibleToUser()` |  |
| `static android.view.accessibility.AccessibilityNodeInfo obtain(android.view.View)` |  |
| `static android.view.accessibility.AccessibilityNodeInfo obtain(android.view.View, int)` |  |
| `static android.view.accessibility.AccessibilityNodeInfo obtain()` |  |
| `static android.view.accessibility.AccessibilityNodeInfo obtain(android.view.accessibility.AccessibilityNodeInfo)` |  |
| `boolean performAction(int)` |  |
| `boolean performAction(int, android.os.Bundle)` |  |
| `void recycle()` |  |
| `boolean refresh()` |  |
| `boolean refreshWithExtraData(String, android.os.Bundle)` |  |
| `boolean removeAction(android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction)` |  |
| `boolean removeChild(android.view.View)` |  |
| `boolean removeChild(android.view.View, int)` |  |
| `void setAccessibilityFocused(boolean)` |  |
| `void setAvailableExtraData(java.util.List<java.lang.String>)` |  |
| `void setBoundsInScreen(android.graphics.Rect)` |  |
| `void setCanOpenPopup(boolean)` |  |
| `void setCheckable(boolean)` |  |
| `void setChecked(boolean)` |  |
| `void setClassName(CharSequence)` |  |
| `void setClickable(boolean)` |  |
| `void setCollectionInfo(android.view.accessibility.AccessibilityNodeInfo.CollectionInfo)` |  |
| `void setCollectionItemInfo(android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo)` |  |
| `void setContentDescription(CharSequence)` |  |
| `void setContentInvalid(boolean)` |  |
| `void setContextClickable(boolean)` |  |
| `void setDismissable(boolean)` |  |
| `void setDrawingOrder(int)` |  |
| `void setEditable(boolean)` |  |
| `void setEnabled(boolean)` |  |
| `void setError(CharSequence)` |  |
| `void setFocusable(boolean)` |  |
| `void setFocused(boolean)` |  |
| `void setHeading(boolean)` |  |
| `void setHintText(CharSequence)` |  |
| `void setImportantForAccessibility(boolean)` |  |
| `void setInputType(int)` |  |
| `void setLabelFor(android.view.View)` |  |
| `void setLabelFor(android.view.View, int)` |  |
| `void setLabeledBy(android.view.View)` |  |
| `void setLabeledBy(android.view.View, int)` |  |
| `void setLiveRegion(int)` |  |
| `void setLongClickable(boolean)` |  |
| `void setMaxTextLength(int)` |  |
| `void setMovementGranularities(int)` |  |
| `void setMultiLine(boolean)` |  |
| `void setPackageName(CharSequence)` |  |
| `void setPaneTitle(@Nullable CharSequence)` |  |
| `void setParent(android.view.View)` |  |
| `void setParent(android.view.View, int)` |  |
| `void setPassword(boolean)` |  |
| `void setRangeInfo(android.view.accessibility.AccessibilityNodeInfo.RangeInfo)` |  |
| `void setScreenReaderFocusable(boolean)` |  |
| `void setScrollable(boolean)` |  |
| `void setSelected(boolean)` |  |
| `void setShowingHintText(boolean)` |  |
| `void setSource(android.view.View)` |  |
| `void setSource(android.view.View, int)` |  |
| `void setStateDescription(@Nullable CharSequence)` |  |
| `void setText(CharSequence)` |  |
| `void setTextEntryKey(boolean)` |  |
| `void setTextSelection(int, int)` |  |
| `void setTooltipText(@Nullable CharSequence)` |  |
| `void setTouchDelegateInfo(@NonNull android.view.accessibility.AccessibilityNodeInfo.TouchDelegateInfo)` |  |
| `void setTraversalAfter(android.view.View)` |  |
| `void setTraversalAfter(android.view.View, int)` |  |
| `void setTraversalBefore(android.view.View)` |  |
| `void setTraversalBefore(android.view.View, int)` |  |
| `void setViewIdResourceName(String)` |  |
| `void setVisibleToUser(boolean)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final AccessibilityNodeInfo.AccessibilityAction`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo.AccessibilityAction(int, @Nullable CharSequence)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_ACCESSIBILITY_FOCUS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CLEAR_ACCESSIBILITY_FOCUS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CLEAR_FOCUS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CLEAR_SELECTION` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CLICK` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_COLLAPSE` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CONTEXT_CLICK` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_COPY` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_CUT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_DISMISS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_EXPAND` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_FOCUS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_HIDE_TOOLTIP` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_LONG_CLICK` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_MOVE_WINDOW` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_NEXT_AT_MOVEMENT_GRANULARITY` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_NEXT_HTML_ELEMENT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PAGE_DOWN` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PAGE_LEFT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PAGE_RIGHT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PAGE_UP` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PASTE` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_PREVIOUS_HTML_ELEMENT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_BACKWARD` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_DOWN` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_FORWARD` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_LEFT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_RIGHT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_TO_POSITION` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SCROLL_UP` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SELECT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SET_PROGRESS` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SET_SELECTION` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SET_TEXT` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SHOW_ON_SCREEN` |  |
| `static final android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction ACTION_SHOW_TOOLTIP` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getId()` |  |
| `CharSequence getLabel()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final AccessibilityNodeInfo.CollectionInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo.CollectionInfo(int, int, boolean)` |  |
| `AccessibilityNodeInfo.CollectionInfo(int, int, boolean, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SELECTION_MODE_MULTIPLE = 2` |  |
| `static final int SELECTION_MODE_NONE = 0` |  |
| `static final int SELECTION_MODE_SINGLE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getColumnCount()` |  |
| `int getRowCount()` |  |
| `int getSelectionMode()` |  |
| `boolean isHierarchical()` |  |
| `static android.view.accessibility.AccessibilityNodeInfo.CollectionInfo obtain(int, int, boolean)` |  |
| `static android.view.accessibility.AccessibilityNodeInfo.CollectionInfo obtain(int, int, boolean, int)` |  |

---

### `class static final AccessibilityNodeInfo.CollectionItemInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo.CollectionItemInfo(int, int, int, int, boolean)` |  |
| `AccessibilityNodeInfo.CollectionItemInfo(int, int, int, int, boolean, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getColumnIndex()` |  |
| `int getColumnSpan()` |  |
| `int getRowIndex()` |  |
| `int getRowSpan()` |  |
| `boolean isSelected()` |  |
| `static android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo obtain(int, int, int, int, boolean)` |  |
| `static android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo obtain(int, int, int, int, boolean, boolean)` |  |

---

### `class static final AccessibilityNodeInfo.ExtraRenderingInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getTextSizeInPx()` |  |
| `int getTextSizeUnit()` |  |

---

### `class static final AccessibilityNodeInfo.RangeInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo.RangeInfo(int, float, float, float)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int RANGE_TYPE_FLOAT = 1` |  |
| `static final int RANGE_TYPE_INT = 0` |  |
| `static final int RANGE_TYPE_PERCENT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getCurrent()` |  |
| `float getMax()` |  |
| `float getMin()` |  |
| `int getType()` |  |
| `static android.view.accessibility.AccessibilityNodeInfo.RangeInfo obtain(int, float, float, float)` |  |

---

### `class static final AccessibilityNodeInfo.TouchDelegateInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeInfo.TouchDelegateInfo(@NonNull java.util.Map<android.graphics.Region,android.view.View>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getRegionCount()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract AccessibilityNodeProvider`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityNodeProvider()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int HOST_VIEW_ID = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addExtraDataToAccessibilityNodeInfo(int, android.view.accessibility.AccessibilityNodeInfo, String, android.os.Bundle)` |  |
| `android.view.accessibility.AccessibilityNodeInfo createAccessibilityNodeInfo(int)` |  |
| `java.util.List<android.view.accessibility.AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String, int)` |  |
| `android.view.accessibility.AccessibilityNodeInfo findFocus(int)` |  |
| `boolean performAction(int, int, android.os.Bundle)` |  |

---

### `class AccessibilityRecord`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityRecord()` |  |
| `AccessibilityRecord(@NonNull android.view.accessibility.AccessibilityRecord)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAddedCount()` |  |
| `CharSequence getBeforeText()` |  |
| `CharSequence getClassName()` |  |
| `CharSequence getContentDescription()` |  |
| `int getCurrentItemIndex()` |  |
| `int getFromIndex()` |  |
| `int getItemCount()` |  |
| `int getMaxScrollX()` |  |
| `int getMaxScrollY()` |  |
| `android.os.Parcelable getParcelableData()` |  |
| `int getRemovedCount()` |  |
| `int getScrollDeltaX()` |  |
| `int getScrollDeltaY()` |  |
| `int getScrollX()` |  |
| `int getScrollY()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getSource()` |  |
| `java.util.List<java.lang.CharSequence> getText()` |  |
| `int getToIndex()` |  |
| `int getWindowId()` |  |
| `boolean isChecked()` |  |
| `boolean isEnabled()` |  |
| `boolean isFullScreen()` |  |
| `boolean isPassword()` |  |
| `boolean isScrollable()` |  |
| `static android.view.accessibility.AccessibilityRecord obtain(android.view.accessibility.AccessibilityRecord)` |  |
| `static android.view.accessibility.AccessibilityRecord obtain()` |  |
| `void recycle()` |  |
| `void setAddedCount(int)` |  |
| `void setBeforeText(CharSequence)` |  |
| `void setChecked(boolean)` |  |
| `void setClassName(CharSequence)` |  |
| `void setContentDescription(CharSequence)` |  |
| `void setCurrentItemIndex(int)` |  |
| `void setEnabled(boolean)` |  |
| `void setFromIndex(int)` |  |
| `void setFullScreen(boolean)` |  |
| `void setItemCount(int)` |  |
| `void setMaxScrollX(int)` |  |
| `void setMaxScrollY(int)` |  |
| `void setParcelableData(android.os.Parcelable)` |  |
| `void setPassword(boolean)` |  |
| `void setRemovedCount(int)` |  |
| `void setScrollDeltaX(int)` |  |
| `void setScrollDeltaY(int)` |  |
| `void setScrollX(int)` |  |
| `void setScrollY(int)` |  |
| `void setScrollable(boolean)` |  |
| `void setSource(android.view.View)` |  |
| `void setSource(@Nullable android.view.View, int)` |  |
| `void setToIndex(int)` |  |

---

### `class abstract AccessibilityRequestPreparer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityRequestPreparer(android.view.View, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int REQUEST_TYPE_EXTRA_DATA = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void onPrepareExtraData(int, String, android.os.Bundle, android.os.Message)` |  |

---

### `class final AccessibilityWindowInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityWindowInfo()` |  |
| `AccessibilityWindowInfo(@NonNull android.view.accessibility.AccessibilityWindowInfo)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_ACCESSIBILITY_OVERLAY = 4` |  |
| `static final int TYPE_APPLICATION = 1` |  |
| `static final int TYPE_INPUT_METHOD = 2` |  |
| `static final int TYPE_SPLIT_SCREEN_DIVIDER = 5` |  |
| `static final int TYPE_SYSTEM = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.view.accessibility.AccessibilityNodeInfo getAnchor()` |  |
| `void getBoundsInScreen(android.graphics.Rect)` |  |
| `android.view.accessibility.AccessibilityWindowInfo getChild(int)` |  |
| `int getChildCount()` |  |
| `int getDisplayId()` |  |
| `int getId()` |  |
| `int getLayer()` |  |
| `android.view.accessibility.AccessibilityWindowInfo getParent()` |  |
| `void getRegionInScreen(@NonNull android.graphics.Region)` |  |
| `android.view.accessibility.AccessibilityNodeInfo getRoot()` |  |
| `int getType()` |  |
| `boolean isAccessibilityFocused()` |  |
| `boolean isActive()` |  |
| `boolean isFocused()` |  |
| `boolean isInPictureInPictureMode()` |  |
| `static android.view.accessibility.AccessibilityWindowInfo obtain()` |  |
| `static android.view.accessibility.AccessibilityWindowInfo obtain(android.view.accessibility.AccessibilityWindowInfo)` |  |
| `void recycle()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class CaptioningManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addCaptioningChangeListener(@NonNull android.view.accessibility.CaptioningManager.CaptioningChangeListener)` |  |
| `final float getFontScale()` |  |
| `final boolean isEnabled()` |  |
| `void removeCaptioningChangeListener(@NonNull android.view.accessibility.CaptioningManager.CaptioningChangeListener)` |  |

---

### `class static final CaptioningManager.CaptionStyle`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CaptioningManager.CaptioningChangeListener()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int EDGE_TYPE_DEPRESSED = 4` |  |
| `static final int EDGE_TYPE_DROP_SHADOW = 2` |  |
| `static final int EDGE_TYPE_NONE = 0` |  |
| `static final int EDGE_TYPE_OUTLINE = 1` |  |
| `static final int EDGE_TYPE_RAISED = 3` |  |
| `static final int EDGE_TYPE_UNSPECIFIED = -1` |  |
| `final int backgroundColor` |  |
| `final int edgeColor` |  |
| `final int edgeType` |  |
| `final int foregroundColor` |  |
| `final int windowColor` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasBackgroundColor()` |  |
| `boolean hasEdgeColor()` |  |
| `boolean hasEdgeType()` |  |
| `boolean hasForegroundColor()` |  |
| `boolean hasWindowColor()` |  |
| `void onEnabledChanged(boolean)` |  |
| `void onFontScaleChanged(float)` |  |
| `void onLocaleChanged(@Nullable java.util.Locale)` |  |
| `void onUserStyleChanged(@NonNull android.view.accessibility.CaptioningManager.CaptionStyle)` |  |

---

## Package: `android.view.animation`

### `class AccelerateDecelerateInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccelerateDecelerateInterpolator()` |  |
| `AccelerateDecelerateInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class AccelerateInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccelerateInterpolator()` |  |
| `AccelerateInterpolator(float)` |  |
| `AccelerateInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class AlphaAnimation`

- **Extends:** `android.view.animation.Animation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlphaAnimation(android.content.Context, android.util.AttributeSet)` |  |
| `AlphaAnimation(float, float)` |  |

---

### `class abstract Animation`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Animation()` |  |
| `Animation(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ABSOLUTE = 0` |  |
| `static final int INFINITE = -1` |  |
| `static final int RELATIVE_TO_PARENT = 2` |  |
| `static final int RELATIVE_TO_SELF = 1` |  |
| `static final int RESTART = 1` |  |
| `static final int REVERSE = 2` |  |
| `static final int START_ON_FIRST_FRAME = -1` |  |
| `static final int ZORDER_BOTTOM = -1` |  |
| `static final int ZORDER_NORMAL = 0` |  |
| `static final int ZORDER_TOP = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyTransformation(float, android.view.animation.Transformation)` |  |
| `void cancel()` |  |
| `android.view.animation.Animation clone() throws java.lang.CloneNotSupportedException` |  |
| `long computeDurationHint()` |  |
| `void ensureInterpolator()` |  |
| `long getDuration()` |  |
| `boolean getFillAfter()` |  |
| `boolean getFillBefore()` |  |
| `android.view.animation.Interpolator getInterpolator()` |  |
| `int getRepeatCount()` |  |
| `int getRepeatMode()` |  |
| `float getScaleFactor()` |  |
| `long getStartOffset()` |  |
| `long getStartTime()` |  |
| `boolean getTransformation(long, android.view.animation.Transformation)` |  |
| `boolean getTransformation(long, android.view.animation.Transformation, float)` |  |
| `int getZAdjustment()` |  |
| `boolean hasEnded()` |  |
| `boolean hasStarted()` |  |
| `void initialize(int, int, int, int)` |  |
| `boolean isFillEnabled()` |  |
| `boolean isInitialized()` |  |
| `void reset()` |  |
| `float resolveSize(int, float, int, int)` |  |
| `void restrictDuration(long)` |  |
| `void scaleCurrentDuration(float)` |  |
| `void setAnimationListener(android.view.animation.Animation.AnimationListener)` |  |
| `void setDuration(long)` |  |
| `void setFillAfter(boolean)` |  |
| `void setFillBefore(boolean)` |  |
| `void setFillEnabled(boolean)` |  |
| `void setInterpolator(android.content.Context, @AnimRes @InterpolatorRes int)` |  |
| `void setInterpolator(android.view.animation.Interpolator)` |  |
| `void setRepeatCount(int)` |  |
| `void setRepeatMode(int)` |  |
| `void setStartOffset(long)` |  |
| `void setStartTime(long)` |  |
| `void setZAdjustment(int)` |  |
| `void start()` |  |
| `void startNow()` |  |
| `boolean willChangeBounds()` |  |
| `boolean willChangeTransformationMatrix()` |  |

---

### `interface static Animation.AnimationListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAnimationEnd(android.view.animation.Animation)` |  |
| `void onAnimationRepeat(android.view.animation.Animation)` |  |
| `void onAnimationStart(android.view.animation.Animation)` |  |

---

### `class static Animation.Description`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Animation.Description()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int type` |  |
| `float value` |  |

---

### `class AnimationSet`

- **Extends:** `android.view.animation.Animation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnimationSet(android.content.Context, android.util.AttributeSet)` |  |
| `AnimationSet(boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addAnimation(android.view.animation.Animation)` |  |
| `android.view.animation.AnimationSet clone() throws java.lang.CloneNotSupportedException` |  |
| `java.util.List<android.view.animation.Animation> getAnimations()` |  |

---

### `class AnimationUtils`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnimationUtils()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static long currentAnimationTimeMillis()` |  |
| `static android.view.animation.Animation loadAnimation(android.content.Context, @AnimRes int) throws android.content.res.Resources.NotFoundException` |  |
| `static android.view.animation.Interpolator loadInterpolator(android.content.Context, @AnimRes @InterpolatorRes int) throws android.content.res.Resources.NotFoundException` |  |
| `static android.view.animation.LayoutAnimationController loadLayoutAnimation(android.content.Context, @AnimRes int) throws android.content.res.Resources.NotFoundException` |  |
| `static android.view.animation.Animation makeInAnimation(android.content.Context, boolean)` |  |
| `static android.view.animation.Animation makeInChildBottomAnimation(android.content.Context)` |  |
| `static android.view.animation.Animation makeOutAnimation(android.content.Context, boolean)` |  |

---

### `class AnticipateInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnticipateInterpolator()` |  |
| `AnticipateInterpolator(float)` |  |
| `AnticipateInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class AnticipateOvershootInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnticipateOvershootInterpolator()` |  |
| `AnticipateOvershootInterpolator(float)` |  |
| `AnticipateOvershootInterpolator(float, float)` |  |
| `AnticipateOvershootInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class abstract BaseInterpolator`

- **Implements:** `android.view.animation.Interpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BaseInterpolator()` |  |

---

### `class BounceInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BounceInterpolator()` |  |
| `BounceInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class CycleInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CycleInterpolator(float)` |  |
| `CycleInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class DecelerateInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DecelerateInterpolator()` |  |
| `DecelerateInterpolator(float)` |  |
| `DecelerateInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class GridLayoutAnimationController`

- **Extends:** `android.view.animation.LayoutAnimationController`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GridLayoutAnimationController(android.content.Context, android.util.AttributeSet)` |  |
| `GridLayoutAnimationController(android.view.animation.Animation)` |  |
| `GridLayoutAnimationController(android.view.animation.Animation, float, float)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DIRECTION_BOTTOM_TO_TOP = 2` |  |
| `static final int DIRECTION_HORIZONTAL_MASK = 1` |  |
| `static final int DIRECTION_LEFT_TO_RIGHT = 0` |  |
| `static final int DIRECTION_RIGHT_TO_LEFT = 1` |  |
| `static final int DIRECTION_TOP_TO_BOTTOM = 0` |  |
| `static final int DIRECTION_VERTICAL_MASK = 2` |  |
| `static final int PRIORITY_COLUMN = 1` |  |
| `static final int PRIORITY_NONE = 0` |  |
| `static final int PRIORITY_ROW = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getColumnDelay()` |  |
| `int getDirection()` |  |
| `int getDirectionPriority()` |  |
| `float getRowDelay()` |  |
| `void setColumnDelay(float)` |  |
| `void setDirection(int)` |  |
| `void setDirectionPriority(int)` |  |
| `void setRowDelay(float)` |  |

---

### `class static GridLayoutAnimationController.AnimationParameters`

- **Extends:** `android.view.animation.LayoutAnimationController.AnimationParameters`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GridLayoutAnimationController.AnimationParameters()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int column` |  |
| `int columnsCount` |  |
| `int row` |  |
| `int rowsCount` |  |

---

### `interface Interpolator`

- **Extends:** `android.animation.TimeInterpolator`

---

### `class LayoutAnimationController`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LayoutAnimationController(android.content.Context, android.util.AttributeSet)` |  |
| `LayoutAnimationController(android.view.animation.Animation)` |  |
| `LayoutAnimationController(android.view.animation.Animation, float)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ORDER_NORMAL = 0` |  |
| `static final int ORDER_RANDOM = 2` |  |
| `static final int ORDER_REVERSE = 1` |  |
| `android.view.animation.Animation mAnimation` |  |
| `android.view.animation.Interpolator mInterpolator` |  |
| `java.util.Random mRandomizer` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.animation.Animation getAnimation()` |  |
| `final android.view.animation.Animation getAnimationForView(android.view.View)` |  |
| `float getDelay()` |  |
| `long getDelayForView(android.view.View)` |  |
| `android.view.animation.Interpolator getInterpolator()` |  |
| `int getOrder()` |  |
| `int getTransformedIndex(android.view.animation.LayoutAnimationController.AnimationParameters)` |  |
| `boolean isDone()` |  |
| `void setAnimation(android.content.Context, @AnimRes int)` |  |
| `void setAnimation(android.view.animation.Animation)` |  |
| `void setDelay(float)` |  |
| `void setInterpolator(android.content.Context, @InterpolatorRes int)` |  |
| `void setInterpolator(android.view.animation.Interpolator)` |  |
| `void setOrder(int)` |  |
| `void start()` |  |
| `boolean willOverlap()` |  |

---

### `class static LayoutAnimationController.AnimationParameters`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LayoutAnimationController.AnimationParameters()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int count` |  |
| `int index` |  |

---

### `class LinearInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LinearInterpolator()` |  |
| `LinearInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class OvershootInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OvershootInterpolator()` |  |
| `OvershootInterpolator(float)` |  |
| `OvershootInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class PathInterpolator`

- **Extends:** `android.view.animation.BaseInterpolator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PathInterpolator(android.graphics.Path)` |  |
| `PathInterpolator(float, float)` |  |
| `PathInterpolator(float, float, float, float)` |  |
| `PathInterpolator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class RotateAnimation`

- **Extends:** `android.view.animation.Animation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RotateAnimation(android.content.Context, android.util.AttributeSet)` |  |
| `RotateAnimation(float, float)` |  |
| `RotateAnimation(float, float, float, float)` |  |
| `RotateAnimation(float, float, int, float, int, float)` |  |

---

### `class ScaleAnimation`

- **Extends:** `android.view.animation.Animation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScaleAnimation(android.content.Context, android.util.AttributeSet)` |  |
| `ScaleAnimation(float, float, float, float)` |  |
| `ScaleAnimation(float, float, float, float, float, float)` |  |
| `ScaleAnimation(float, float, float, float, int, float, int, float)` |  |

---

### `class Transformation`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Transformation()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_ALPHA = 1` |  |
| `static final int TYPE_BOTH = 3` |  |
| `static final int TYPE_IDENTITY = 0` |  |
| `static final int TYPE_MATRIX = 2` |  |
| `float mAlpha` |  |
| `android.graphics.Matrix mMatrix` |  |
| `int mTransformationType` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `void compose(android.view.animation.Transformation)` |  |
| `float getAlpha()` |  |
| `android.graphics.Matrix getMatrix()` |  |
| `int getTransformationType()` |  |
| `void set(android.view.animation.Transformation)` |  |
| `void setAlpha(@FloatRange(from=0.0, to=1.0) float)` |  |
| `void setTransformationType(int)` |  |
| `String toShortString()` |  |

---

### `class TranslateAnimation`

- **Extends:** `android.view.animation.Animation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TranslateAnimation(android.content.Context, android.util.AttributeSet)` |  |
| `TranslateAnimation(float, float, float, float)` |  |
| `TranslateAnimation(int, float, int, float, int, float, int, float)` |  |

---

## Package: `android.view.autofill`

### `class final AutofillId`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final AutofillManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AutofillManager.AutofillCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_ASSIST_STRUCTURE = "android.view.autofill.extra.ASSIST_STRUCTURE"` |  |
| `static final String EXTRA_AUTHENTICATION_RESULT = "android.view.autofill.extra.AUTHENTICATION_RESULT"` |  |
| `static final String EXTRA_CLIENT_STATE = "android.view.autofill.extra.CLIENT_STATE"` |  |
| `static final int EVENT_INPUT_HIDDEN = 2` |  |
| `static final int EVENT_INPUT_SHOWN = 1` |  |
| `static final int EVENT_INPUT_UNAVAILABLE = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `void commit()` |  |
| `void disableAutofillServices()` |  |
| `boolean hasEnabledAutofillServices()` |  |
| `boolean isAutofillSupported()` |  |
| `boolean isEnabled()` |  |
| `boolean isFieldClassificationEnabled()` |  |
| `void notifyValueChanged(android.view.View)` |  |
| `void notifyValueChanged(android.view.View, int, android.view.autofill.AutofillValue)` |  |
| `void notifyViewClicked(@NonNull android.view.View)` |  |
| `void notifyViewClicked(@NonNull android.view.View, int)` |  |
| `void notifyViewEntered(@NonNull android.view.View)` |  |
| `void notifyViewEntered(@NonNull android.view.View, int, @NonNull android.graphics.Rect)` |  |
| `void notifyViewExited(@NonNull android.view.View)` |  |
| `void notifyViewExited(@NonNull android.view.View, int)` |  |
| `void notifyViewVisibilityChanged(@NonNull android.view.View, boolean)` |  |
| `void notifyViewVisibilityChanged(@NonNull android.view.View, int, boolean)` |  |
| `void registerCallback(@Nullable android.view.autofill.AutofillManager.AutofillCallback)` |  |
| `void requestAutofill(@NonNull android.view.View)` |  |
| `void requestAutofill(@NonNull android.view.View, int, @NonNull android.graphics.Rect)` |  |
| `void setUserData(@Nullable android.service.autofill.UserData)` |  |
| `void unregisterCallback(@Nullable android.view.autofill.AutofillManager.AutofillCallback)` |  |
| `void onAutofillEvent(@NonNull android.view.View, int)` |  |
| `void onAutofillEvent(@NonNull android.view.View, int, int)` |  |

---

### `class final AutofillValue`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.view.autofill.AutofillValue forDate(long)` |  |
| `static android.view.autofill.AutofillValue forList(int)` |  |
| `static android.view.autofill.AutofillValue forText(@Nullable CharSequence)` |  |
| `static android.view.autofill.AutofillValue forToggle(boolean)` |  |
| `long getDateValue()` |  |
| `int getListValue()` |  |
| `boolean getToggleValue()` |  |
| `boolean isDate()` |  |
| `boolean isList()` |  |
| `boolean isText()` |  |
| `boolean isToggle()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.view.contentcapture`

### `class final ContentCaptureCondition`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ContentCaptureCondition(@NonNull android.content.LocusId, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_IS_REGEX = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final ContentCaptureContext`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ContentCaptureContext.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ContentCaptureContext.Builder(@NonNull android.content.LocusId)` |  |

---

### `class final ContentCaptureManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DATA_SHARE_ERROR_CONCURRENT_REQUEST = 2` |  |
| `static final int DATA_SHARE_ERROR_TIMEOUT_INTERRUPTED = 3` |  |
| `static final int DATA_SHARE_ERROR_UNKNOWN = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isContentCaptureEnabled()` |  |
| `void removeData(@NonNull android.view.contentcapture.DataRemovalRequest)` |  |
| `void setContentCaptureEnabled(boolean)` |  |
| `void shareData(@NonNull android.view.contentcapture.DataShareRequest, @NonNull java.util.concurrent.Executor, @NonNull android.view.contentcapture.DataShareWriteAdapter)` |  |

---

### `class abstract ContentCaptureSession`

- **Implements:** `java.lang.AutoCloseable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `final void destroy()` |  |
| `final void notifySessionPaused()` |  |
| `final void notifySessionResumed()` |  |
| `final void notifyViewAppeared(@NonNull android.view.ViewStructure)` |  |
| `final void notifyViewDisappeared(@NonNull android.view.autofill.AutofillId)` |  |
| `final void notifyViewInsetsChanged(@NonNull android.graphics.Insets)` |  |
| `final void notifyViewTextChanged(@NonNull android.view.autofill.AutofillId, @Nullable CharSequence)` |  |
| `final void notifyViewsDisappeared(@NonNull android.view.autofill.AutofillId, @NonNull long[])` |  |
| `final void setContentCaptureContext(@Nullable android.view.contentcapture.ContentCaptureContext)` |  |

---

### `class final ContentCaptureSessionId`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final DataRemovalRequest`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_IS_PREFIX = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isForEverything()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final DataRemovalRequest.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataRemovalRequest.Builder()` |  |

---

### `class final DataRemovalRequest.LocusIdRequest`


---

### `class final DataShareRequest`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DataShareRequest(@Nullable android.content.LocusId, @NonNull String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface DataShareWriteAdapter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void onError(int)` |  |
| `void onRejected()` |  |
| `void onWrite(@NonNull android.os.ParcelFileDescriptor)` |  |

---

## Package: `android.view.inputmethod`

### `class BaseInputConnection`

- **Implements:** `android.view.inputmethod.InputConnection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BaseInputConnection(android.view.View, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean beginBatchEdit()` |  |
| `boolean clearMetaKeyStates(int)` |  |
| `boolean commitCompletion(android.view.inputmethod.CompletionInfo)` |  |
| `boolean commitContent(android.view.inputmethod.InputContentInfo, int, android.os.Bundle)` |  |
| `boolean commitCorrection(android.view.inputmethod.CorrectionInfo)` |  |
| `boolean commitText(CharSequence, int)` |  |
| `boolean deleteSurroundingText(int, int)` |  |
| `boolean deleteSurroundingTextInCodePoints(int, int)` |  |
| `boolean endBatchEdit()` |  |
| `boolean finishComposingText()` |  |
| `static int getComposingSpanEnd(android.text.Spannable)` |  |
| `static int getComposingSpanStart(android.text.Spannable)` |  |
| `int getCursorCapsMode(int)` |  |
| `android.text.Editable getEditable()` |  |
| `android.view.inputmethod.ExtractedText getExtractedText(android.view.inputmethod.ExtractedTextRequest, int)` |  |
| `android.os.Handler getHandler()` |  |
| `CharSequence getSelectedText(int)` |  |
| `CharSequence getTextAfterCursor(int, int)` |  |
| `CharSequence getTextBeforeCursor(int, int)` |  |
| `boolean performContextMenuAction(int)` |  |
| `boolean performEditorAction(int)` |  |
| `boolean performPrivateCommand(String, android.os.Bundle)` |  |
| `static final void removeComposingSpans(android.text.Spannable)` |  |
| `boolean reportFullscreenMode(boolean)` |  |
| `boolean requestCursorUpdates(int)` |  |
| `boolean sendKeyEvent(android.view.KeyEvent)` |  |
| `boolean setComposingRegion(int, int)` |  |
| `static void setComposingSpans(android.text.Spannable)` |  |
| `boolean setComposingText(CharSequence, int)` |  |
| `boolean setSelection(int, int)` |  |

---

### `class final CompletionInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CompletionInfo(long, int, CharSequence)` |  |
| `CompletionInfo(long, int, CharSequence, CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getId()` |  |
| `CharSequence getLabel()` |  |
| `int getPosition()` |  |
| `CharSequence getText()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CorrectionInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CorrectionInfo(int, CharSequence, CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `CharSequence getNewText()` |  |
| `int getOffset()` |  |
| `CharSequence getOldText()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CursorAnchorInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CursorAnchorInfo(android.os.Parcel)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_HAS_INVISIBLE_REGION = 2` |  |
| `static final int FLAG_HAS_VISIBLE_REGION = 1` |  |
| `static final int FLAG_IS_RTL = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.graphics.RectF getCharacterBounds(int)` |  |
| `int getCharacterBoundsFlags(int)` |  |
| `CharSequence getComposingText()` |  |
| `int getComposingTextStart()` |  |
| `float getInsertionMarkerBaseline()` |  |
| `float getInsertionMarkerBottom()` |  |
| `int getInsertionMarkerFlags()` |  |
| `float getInsertionMarkerHorizontal()` |  |
| `float getInsertionMarkerTop()` |  |
| `android.graphics.Matrix getMatrix()` |  |
| `int getSelectionEnd()` |  |
| `int getSelectionStart()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final CursorAnchorInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CursorAnchorInfo.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.inputmethod.CursorAnchorInfo.Builder addCharacterBounds(int, float, float, float, float, int)` |  |
| `android.view.inputmethod.CursorAnchorInfo build()` |  |
| `void reset()` |  |
| `android.view.inputmethod.CursorAnchorInfo.Builder setComposingText(int, CharSequence)` |  |
| `android.view.inputmethod.CursorAnchorInfo.Builder setInsertionMarkerLocation(float, float, float, float, int)` |  |
| `android.view.inputmethod.CursorAnchorInfo.Builder setMatrix(android.graphics.Matrix)` |  |
| `android.view.inputmethod.CursorAnchorInfo.Builder setSelectionRange(int, int)` |  |

---

### `class EditorInfo`

- **Implements:** `android.text.InputType android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EditorInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int IME_ACTION_DONE = 6` |  |
| `static final int IME_ACTION_GO = 2` |  |
| `static final int IME_ACTION_NEXT = 5` |  |
| `static final int IME_ACTION_NONE = 1` |  |
| `static final int IME_ACTION_PREVIOUS = 7` |  |
| `static final int IME_ACTION_SEARCH = 3` |  |
| `static final int IME_ACTION_SEND = 4` |  |
| `static final int IME_ACTION_UNSPECIFIED = 0` |  |
| `static final int IME_FLAG_FORCE_ASCII = -2147483648` |  |
| `static final int IME_FLAG_NAVIGATE_NEXT = 134217728` |  |
| `static final int IME_FLAG_NAVIGATE_PREVIOUS = 67108864` |  |
| `static final int IME_FLAG_NO_ACCESSORY_ACTION = 536870912` |  |
| `static final int IME_FLAG_NO_ENTER_ACTION = 1073741824` |  |
| `static final int IME_FLAG_NO_EXTRACT_UI = 268435456` |  |
| `static final int IME_FLAG_NO_FULLSCREEN = 33554432` |  |
| `static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 16777216` |  |
| `static final int IME_MASK_ACTION = 255` |  |
| `static final int IME_NULL = 0` |  |
| `int actionId` |  |
| `CharSequence actionLabel` |  |
| `android.os.Bundle extras` |  |
| `int fieldId` |  |
| `String fieldName` |  |
| `CharSequence hintText` |  |
| `int imeOptions` |  |
| `int initialCapsMode` |  |
| `int initialSelEnd` |  |
| `int initialSelStart` |  |
| `int inputType` |  |
| `CharSequence label` |  |
| `String packageName` |  |
| `String privateImeOptions` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `final void makeCompatible(int)` |  |
| `void setInitialSurroundingSubText(@NonNull CharSequence, int)` |  |
| `void setInitialSurroundingText(@NonNull CharSequence)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ExtractedText`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExtractedText()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_SELECTING = 2` |  |
| `static final int FLAG_SINGLE_LINE = 1` |  |
| `int flags` |  |
| `CharSequence hint` |  |
| `int partialEndOffset` |  |
| `int partialStartOffset` |  |
| `int selectionEnd` |  |
| `int selectionStart` |  |
| `int startOffset` |  |
| `CharSequence text` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ExtractedTextRequest`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExtractedTextRequest()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int flags` |  |
| `int hintMaxChars` |  |
| `int hintMaxLines` |  |
| `int token` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final InlineSuggestion`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void inflate(@NonNull android.content.Context, @NonNull android.util.Size, @NonNull java.util.concurrent.Executor, @NonNull java.util.function.Consumer<android.widget.inline.InlineContentView>)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final InlineSuggestionInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SOURCE_AUTOFILL = "android:autofill"` |  |
| `static final String SOURCE_PLATFORM = "android:platform"` |  |
| `static final String TYPE_ACTION = "android:autofill:action"` |  |
| `static final String TYPE_SUGGESTION = "android:autofill:suggestion"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isPinned()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final InlineSuggestionsRequest`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SUGGESTION_COUNT_UNLIMITED = 2147483647` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getMaxSuggestionCount()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final InlineSuggestionsRequest.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InlineSuggestionsRequest.Builder(@NonNull java.util.List<android.widget.inline.InlinePresentationSpec>)` |  |

---

### `class final InlineSuggestionsResponse`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final InputBinding`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputBinding(android.view.inputmethod.InputConnection, android.os.IBinder, int, int)` |  |
| `InputBinding(android.view.inputmethod.InputConnection, android.view.inputmethod.InputBinding)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.view.inputmethod.InputConnection getConnection()` |  |
| `android.os.IBinder getConnectionToken()` |  |
| `int getPid()` |  |
| `int getUid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface InputConnection`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CURSOR_UPDATE_IMMEDIATE = 1` |  |
| `static final int CURSOR_UPDATE_MONITOR = 2` |  |
| `static final int GET_EXTRACTED_TEXT_MONITOR = 1` |  |
| `static final int GET_TEXT_WITH_STYLES = 1` |  |
| `static final int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean beginBatchEdit()` |  |
| `boolean clearMetaKeyStates(int)` |  |
| `void closeConnection()` |  |
| `boolean commitCompletion(android.view.inputmethod.CompletionInfo)` |  |
| `boolean commitContent(@NonNull android.view.inputmethod.InputContentInfo, int, @Nullable android.os.Bundle)` |  |
| `boolean commitCorrection(android.view.inputmethod.CorrectionInfo)` |  |
| `boolean commitText(CharSequence, int)` |  |
| `boolean deleteSurroundingText(int, int)` |  |
| `boolean deleteSurroundingTextInCodePoints(int, int)` |  |
| `boolean endBatchEdit()` |  |
| `boolean finishComposingText()` |  |
| `int getCursorCapsMode(int)` |  |
| `android.view.inputmethod.ExtractedText getExtractedText(android.view.inputmethod.ExtractedTextRequest, int)` |  |
| `android.os.Handler getHandler()` |  |
| `CharSequence getSelectedText(int)` |  |
| `CharSequence getTextAfterCursor(int, int)` |  |
| `CharSequence getTextBeforeCursor(int, int)` |  |
| `boolean performContextMenuAction(int)` |  |
| `boolean performEditorAction(int)` |  |
| `boolean performPrivateCommand(String, android.os.Bundle)` |  |
| `boolean reportFullscreenMode(boolean)` |  |
| `boolean requestCursorUpdates(int)` |  |
| `boolean sendKeyEvent(android.view.KeyEvent)` |  |
| `boolean setComposingRegion(int, int)` |  |
| `boolean setComposingText(CharSequence, int)` |  |
| `boolean setSelection(int, int)` |  |

---

### `class InputConnectionWrapper`

- **Implements:** `android.view.inputmethod.InputConnection`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputConnectionWrapper(android.view.inputmethod.InputConnection, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean beginBatchEdit()` |  |
| `boolean clearMetaKeyStates(int)` |  |
| `void closeConnection()` |  |
| `boolean commitCompletion(android.view.inputmethod.CompletionInfo)` |  |
| `boolean commitContent(android.view.inputmethod.InputContentInfo, int, android.os.Bundle)` |  |
| `boolean commitCorrection(android.view.inputmethod.CorrectionInfo)` |  |
| `boolean commitText(CharSequence, int)` |  |
| `boolean deleteSurroundingText(int, int)` |  |
| `boolean deleteSurroundingTextInCodePoints(int, int)` |  |
| `boolean endBatchEdit()` |  |
| `boolean finishComposingText()` |  |
| `int getCursorCapsMode(int)` |  |
| `android.view.inputmethod.ExtractedText getExtractedText(android.view.inputmethod.ExtractedTextRequest, int)` |  |
| `android.os.Handler getHandler()` |  |
| `CharSequence getSelectedText(int)` |  |
| `CharSequence getTextAfterCursor(int, int)` |  |
| `CharSequence getTextBeforeCursor(int, int)` |  |
| `boolean performContextMenuAction(int)` |  |
| `boolean performEditorAction(int)` |  |
| `boolean performPrivateCommand(String, android.os.Bundle)` |  |
| `boolean reportFullscreenMode(boolean)` |  |
| `boolean requestCursorUpdates(int)` |  |
| `boolean sendKeyEvent(android.view.KeyEvent)` |  |
| `boolean setComposingRegion(int, int)` |  |
| `boolean setComposingText(CharSequence, int)` |  |
| `boolean setSelection(int, int)` |  |
| `void setTarget(android.view.inputmethod.InputConnection)` |  |

---

### `class final InputContentInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputContentInfo(@NonNull android.net.Uri, @NonNull android.content.ClipDescription)` |  |
| `InputContentInfo(@NonNull android.net.Uri, @NonNull android.content.ClipDescription, @Nullable android.net.Uri)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void releasePermission()` |  |
| `void requestPermission()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface InputMethod`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.view.InputMethod"` |  |
| `static final String SERVICE_META_DATA = "android.view.im"` |  |
| `static final int SHOW_EXPLICIT = 1` |  |
| `static final int SHOW_FORCED = 2` |  |

---

### `interface static InputMethod.SessionCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void sessionCreated(android.view.inputmethod.InputMethodSession)` |  |

---

### `class final InputMethodInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodInfo(android.content.Context, android.content.pm.ResolveInfo) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `InputMethodInfo(String, String, CharSequence, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `android.content.ComponentName getComponent()` |  |
| `String getId()` |  |
| `int getIsDefaultResourceId()` |  |
| `String getPackageName()` |  |
| `android.content.pm.ServiceInfo getServiceInfo()` |  |
| `String getServiceName()` |  |
| `String getSettingsActivity()` |  |
| `android.view.inputmethod.InputMethodSubtype getSubtypeAt(int)` |  |
| `int getSubtypeCount()` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final InputMethodManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int HIDE_IMPLICIT_ONLY = 1` |  |
| `static final int HIDE_NOT_ALWAYS = 2` |  |
| `static final int RESULT_HIDDEN = 3` |  |
| `static final int RESULT_SHOWN = 2` |  |
| `static final int RESULT_UNCHANGED_HIDDEN = 1` |  |
| `static final int RESULT_UNCHANGED_SHOWN = 0` |  |
| `static final int SHOW_FORCED = 2` |  |
| `static final int SHOW_IMPLICIT = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void dispatchKeyEventFromInputMethod(@Nullable android.view.View, @NonNull android.view.KeyEvent)` |  |
| `void displayCompletions(android.view.View, android.view.inputmethod.CompletionInfo[])` |  |
| `android.view.inputmethod.InputMethodSubtype getCurrentInputMethodSubtype()` |  |
| `java.util.List<android.view.inputmethod.InputMethodInfo> getEnabledInputMethodList()` |  |
| `java.util.List<android.view.inputmethod.InputMethodSubtype> getEnabledInputMethodSubtypeList(android.view.inputmethod.InputMethodInfo, boolean)` |  |
| `java.util.List<android.view.inputmethod.InputMethodInfo> getInputMethodList()` |  |
| `android.view.inputmethod.InputMethodSubtype getLastInputMethodSubtype()` |  |
| `java.util.Map<android.view.inputmethod.InputMethodInfo,java.util.List<android.view.inputmethod.InputMethodSubtype>> getShortcutInputMethodsAndSubtypes()` |  |
| `boolean hideSoftInputFromWindow(android.os.IBinder, int)` |  |
| `boolean hideSoftInputFromWindow(android.os.IBinder, int, android.os.ResultReceiver)` |  |
| `boolean isAcceptingText()` |  |
| `boolean isActive(android.view.View)` |  |
| `boolean isActive()` |  |
| `boolean isFullscreenMode()` |  |
| `void restartInput(android.view.View)` |  |
| `void sendAppPrivateCommand(android.view.View, String, android.os.Bundle)` |  |
| `void showInputMethodAndSubtypeEnabler(String)` |  |
| `void showInputMethodPicker()` |  |
| `boolean showSoftInput(android.view.View, int)` |  |
| `boolean showSoftInput(android.view.View, int, android.os.ResultReceiver)` |  |
| `void toggleSoftInput(int, int)` |  |
| `void toggleSoftInputFromWindow(android.os.IBinder, int, int)` |  |
| `void updateCursorAnchorInfo(android.view.View, android.view.inputmethod.CursorAnchorInfo)` |  |
| `void updateExtractedText(android.view.View, int, android.view.inputmethod.ExtractedText)` |  |
| `void updateSelection(android.view.View, int, int, int, int)` |  |

---

### `interface InputMethodSession`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void appPrivateCommand(String, android.os.Bundle)` |  |
| `void dispatchGenericMotionEvent(int, android.view.MotionEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `void dispatchKeyEvent(int, android.view.KeyEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `void dispatchTrackballEvent(int, android.view.MotionEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `void displayCompletions(android.view.inputmethod.CompletionInfo[])` |  |
| `void finishInput()` |  |
| `void toggleSoftInput(int, int)` |  |
| `void updateCursor(android.graphics.Rect)` |  |
| `void updateCursorAnchorInfo(android.view.inputmethod.CursorAnchorInfo)` |  |
| `void updateExtractedText(int, android.view.inputmethod.ExtractedText)` |  |
| `void updateSelection(int, int, int, int, int, int)` |  |
| `void viewClicked(boolean)` |  |

---

### `interface static InputMethodSession.EventCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finishedEvent(int, boolean)` |  |

---

### `class final InputMethodSubtype`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean containsExtraValueKey(String)` |  |
| `int describeContents()` |  |
| `String getExtraValue()` |  |
| `String getExtraValueOf(String)` |  |
| `int getIconResId()` |  |
| `String getMode()` |  |
| `int getNameResId()` |  |
| `boolean isAsciiCapable()` |  |
| `boolean isAuxiliary()` |  |
| `boolean overridesImplicitlyEnabledSubtype()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static InputMethodSubtype.InputMethodSubtypeBuilder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodSubtype.InputMethodSubtypeBuilder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.inputmethod.InputMethodSubtype build()` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setIsAsciiCapable(boolean)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setIsAuxiliary(boolean)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setLanguageTag(String)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setOverridesImplicitlyEnabledSubtype(boolean)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeExtraValue(String)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeIconResId(int)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeId(int)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeLocale(String)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeMode(String)` |  |
| `android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder setSubtypeNameResId(int)` |  |

---

## Package: `android.view.inspector`

### `interface InspectionCompanion<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void mapProperties(@NonNull android.view.inspector.PropertyMapper)` |  |
| `void readProperties(@NonNull T, @NonNull android.view.inspector.PropertyReader)` |  |

---

### `class static InspectionCompanion.UninitializedPropertyMapException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InspectionCompanion.UninitializedPropertyMapException()` |  |

---

### `interface InspectionCompanionProvider`


---

### `class final IntFlagMapping`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IntFlagMapping()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(int, int, @NonNull String)` |  |

---

### `interface PropertyMapper`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int mapBoolean(@NonNull String, @AttrRes int)` |  |
| `int mapByte(@NonNull String, @AttrRes int)` |  |
| `int mapChar(@NonNull String, @AttrRes int)` |  |
| `int mapColor(@NonNull String, @AttrRes int)` |  |
| `int mapDouble(@NonNull String, @AttrRes int)` |  |
| `int mapFloat(@NonNull String, @AttrRes int)` |  |
| `int mapGravity(@NonNull String, @AttrRes int)` |  |
| `int mapInt(@NonNull String, @AttrRes int)` |  |
| `int mapIntEnum(@NonNull String, @AttrRes int, @NonNull java.util.function.IntFunction<java.lang.String>)` |  |
| `int mapIntFlag(@NonNull String, @AttrRes int, @NonNull java.util.function.IntFunction<java.util.Set<java.lang.String>>)` |  |
| `int mapLong(@NonNull String, @AttrRes int)` |  |
| `int mapObject(@NonNull String, @AttrRes int)` |  |
| `int mapResourceId(@NonNull String, @AttrRes int)` |  |
| `int mapShort(@NonNull String, @AttrRes int)` |  |

---

### `class static PropertyMapper.PropertyConflictException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyMapper.PropertyConflictException(@NonNull String, @NonNull String, @NonNull String)` |  |

---

### `interface PropertyReader`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void readBoolean(int, boolean)` |  |
| `void readByte(int, byte)` |  |
| `void readChar(int, char)` |  |
| `void readColor(int, @ColorInt int)` |  |
| `void readColor(int, @ColorLong long)` |  |
| `void readColor(int, @Nullable android.graphics.Color)` |  |
| `void readDouble(int, double)` |  |
| `void readFloat(int, float)` |  |
| `void readGravity(int, int)` |  |
| `void readInt(int, int)` |  |
| `void readIntEnum(int, int)` |  |
| `void readIntFlag(int, int)` |  |
| `void readLong(int, long)` |  |
| `void readObject(int, @Nullable Object)` |  |
| `void readResourceId(int, @AnyRes int)` |  |
| `void readShort(int, short)` |  |

---

### `class static PropertyReader.PropertyTypeMismatchException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PropertyReader.PropertyTypeMismatchException(int, @NonNull String, @NonNull String, @Nullable String)` |  |
| `PropertyReader.PropertyTypeMismatchException(int, @NonNull String, @NonNull String)` |  |

---

### `class StaticInspectionCompanionProvider`

- **Implements:** `android.view.inspector.InspectionCompanionProvider`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StaticInspectionCompanionProvider()` |  |

---

### `class final WindowInspector`


---

## Package: `android.view.textclassifier`

### `class final ConversationAction`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String TYPE_CALL_PHONE = "call_phone"` |  |
| `static final String TYPE_CREATE_REMINDER = "create_reminder"` |  |
| `static final String TYPE_OPEN_URL = "open_url"` |  |
| `static final String TYPE_SEND_EMAIL = "send_email"` |  |
| `static final String TYPE_SEND_SMS = "send_sms"` |  |
| `static final String TYPE_SHARE_LOCATION = "share_location"` |  |
| `static final String TYPE_TEXT_REPLY = "text_reply"` |  |
| `static final String TYPE_TRACK_FLIGHT = "track_flight"` |  |
| `static final String TYPE_VIEW_CALENDAR = "view_calendar"` |  |
| `static final String TYPE_VIEW_MAP = "view_map"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ConversationAction.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConversationAction.Builder(@NonNull String)` |  |

---

### `class final ConversationActions`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConversationActions(@NonNull java.util.List<android.view.textclassifier.ConversationAction>, @Nullable String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ConversationActions.Message`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ConversationActions.Message.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConversationActions.Message.Builder(@NonNull android.app.Person)` |  |

---

### `class static final ConversationActions.Request`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String HINT_FOR_IN_APP = "in_app"` |  |
| `static final String HINT_FOR_NOTIFICATION = "notification"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ConversationActions.Request.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConversationActions.Request.Builder(@NonNull java.util.List<android.view.textclassifier.ConversationActions.Message>)` |  |

---

### `class final SelectionEvent`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACTION_ABANDON = 107` |  |
| `static final int ACTION_COPY = 101` |  |
| `static final int ACTION_CUT = 103` |  |
| `static final int ACTION_DRAG = 106` |  |
| `static final int ACTION_OTHER = 108` |  |
| `static final int ACTION_OVERTYPE = 100` |  |
| `static final int ACTION_PASTE = 102` |  |
| `static final int ACTION_RESET = 201` |  |
| `static final int ACTION_SELECT_ALL = 200` |  |
| `static final int ACTION_SHARE = 104` |  |
| `static final int ACTION_SMART_SHARE = 105` |  |
| `static final int EVENT_AUTO_SELECTION = 5` |  |
| `static final int EVENT_SELECTION_MODIFIED = 2` |  |
| `static final int EVENT_SELECTION_STARTED = 1` |  |
| `static final int EVENT_SMART_SELECTION_MULTI = 4` |  |
| `static final int EVENT_SMART_SELECTION_SINGLE = 3` |  |
| `static final int INVOCATION_LINK = 2` |  |
| `static final int INVOCATION_MANUAL = 1` |  |
| `static final int INVOCATION_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getDurationSincePreviousEvent()` |  |
| `long getDurationSinceSessionStart()` |  |
| `int getEnd()` |  |
| `int getEventIndex()` |  |
| `long getEventTime()` |  |
| `int getEventType()` |  |
| `int getInvocationMethod()` |  |
| `int getSmartEnd()` |  |
| `int getSmartStart()` |  |
| `int getStart()` |  |
| `static boolean isTerminal(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final TextClassification`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.List<android.app.RemoteAction> getActions()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextClassification.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassification.Builder()` |  |

---

### `class static final TextClassification.Request`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextClassification.Request.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassification.Request.Builder(@NonNull CharSequence, @IntRange(from=0) int, @IntRange(from=0) int)` |  |

---

### `class final TextClassificationContext`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextClassificationContext.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassificationContext.Builder(@NonNull String, @NonNull String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.textclassifier.TextClassificationContext.Builder setWidgetVersion(@Nullable String)` |  |

---

### `class final TextClassificationManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void setTextClassificationSessionFactory(@Nullable android.view.textclassifier.TextClassificationSessionFactory)` |  |
| `void setTextClassifier(@Nullable android.view.textclassifier.TextClassifier)` |  |

---

### `interface TextClassificationSessionFactory`


---

### `class final TextClassificationSessionId`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface TextClassifier`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_FROM_TEXT_CLASSIFIER = "android.view.textclassifier.extra.FROM_TEXT_CLASSIFIER"` |  |
| `static final String HINT_TEXT_IS_EDITABLE = "android.text_is_editable"` |  |
| `static final String HINT_TEXT_IS_NOT_EDITABLE = "android.text_is_not_editable"` |  |
| `static final android.view.textclassifier.TextClassifier NO_OP` |  |
| `static final String TYPE_ADDRESS = "address"` |  |
| `static final String TYPE_DATE = "date"` |  |
| `static final String TYPE_DATE_TIME = "datetime"` |  |
| `static final String TYPE_EMAIL = "email"` |  |
| `static final String TYPE_FLIGHT_NUMBER = "flight"` |  |
| `static final String TYPE_OTHER = "other"` |  |
| `static final String TYPE_PHONE = "phone"` |  |
| `static final String TYPE_UNKNOWN = ""` |  |
| `static final String TYPE_URL = "url"` |  |
| `static final String WIDGET_TYPE_CUSTOM_EDITTEXT = "customedit"` |  |
| `static final String WIDGET_TYPE_CUSTOM_TEXTVIEW = "customview"` |  |
| `static final String WIDGET_TYPE_CUSTOM_UNSELECTABLE_TEXTVIEW = "nosel-customview"` |  |
| `static final String WIDGET_TYPE_EDITTEXT = "edittext"` |  |
| `static final String WIDGET_TYPE_EDIT_WEBVIEW = "edit-webview"` |  |
| `static final String WIDGET_TYPE_NOTIFICATION = "notification"` |  |
| `static final String WIDGET_TYPE_TEXTVIEW = "textview"` |  |
| `static final String WIDGET_TYPE_UNKNOWN = "unknown"` |  |
| `static final String WIDGET_TYPE_UNSELECTABLE_TEXTVIEW = "nosel-textview"` |  |
| `static final String WIDGET_TYPE_WEBVIEW = "webview"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `default void destroy()` |  |
| `default boolean isDestroyed()` |  |
| `default void onSelectionEvent(@NonNull android.view.textclassifier.SelectionEvent)` |  |
| `default void onTextClassifierEvent(@NonNull android.view.textclassifier.TextClassifierEvent)` |  |

---

### `class static final TextClassifier.EntityConfig`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.Collection<java.lang.String> getHints()` |  |
| `java.util.Collection<java.lang.String> resolveEntityListModifications(@NonNull java.util.Collection<java.lang.String>)` |  |
| `boolean shouldIncludeTypesFromTextClassifier()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextClassifier.EntityConfig.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassifier.EntityConfig.Builder()` |  |

---

### `class abstract TextClassifierEvent`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CATEGORY_CONVERSATION_ACTIONS = 3` |  |
| `static final int CATEGORY_LANGUAGE_DETECTION = 4` |  |
| `static final int CATEGORY_LINKIFY = 2` |  |
| `static final int CATEGORY_SELECTION = 1` |  |
| `static final int TYPE_ACTIONS_GENERATED = 20` |  |
| `static final int TYPE_ACTIONS_SHOWN = 6` |  |
| `static final int TYPE_AUTO_SELECTION = 5` |  |
| `static final int TYPE_COPY_ACTION = 9` |  |
| `static final int TYPE_CUT_ACTION = 11` |  |
| `static final int TYPE_LINKS_GENERATED = 21` |  |
| `static final int TYPE_LINK_CLICKED = 7` |  |
| `static final int TYPE_MANUAL_REPLY = 19` |  |
| `static final int TYPE_OTHER_ACTION = 16` |  |
| `static final int TYPE_OVERTYPE = 8` |  |
| `static final int TYPE_PASTE_ACTION = 10` |  |
| `static final int TYPE_SELECTION_DESTROYED = 15` |  |
| `static final int TYPE_SELECTION_DRAG = 14` |  |
| `static final int TYPE_SELECTION_MODIFIED = 2` |  |
| `static final int TYPE_SELECTION_RESET = 18` |  |
| `static final int TYPE_SELECTION_STARTED = 1` |  |
| `static final int TYPE_SELECT_ALL = 17` |  |
| `static final int TYPE_SHARE_ACTION = 12` |  |
| `static final int TYPE_SMART_ACTION = 13` |  |
| `static final int TYPE_SMART_SELECTION_MULTI = 4` |  |
| `static final int TYPE_SMART_SELECTION_SINGLE = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getEventCategory()` |  |
| `int getEventIndex()` |  |
| `int getEventType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextClassifierEvent.ConversationActionsEvent`

- **Extends:** `android.view.textclassifier.TextClassifierEvent`
- **Implements:** `android.os.Parcelable`

---

### `class static final TextClassifierEvent.ConversationActionsEvent.Builder`

- **Extends:** `android.view.textclassifier.TextClassifierEvent.Builder<android.view.textclassifier.TextClassifierEvent.ConversationActionsEvent.Builder>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassifierEvent.ConversationActionsEvent.Builder(int)` |  |

---

### `class static final TextClassifierEvent.LanguageDetectionEvent`

- **Extends:** `android.view.textclassifier.TextClassifierEvent`
- **Implements:** `android.os.Parcelable`

---

### `class static final TextClassifierEvent.LanguageDetectionEvent.Builder`

- **Extends:** `android.view.textclassifier.TextClassifierEvent.Builder<android.view.textclassifier.TextClassifierEvent.LanguageDetectionEvent.Builder>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassifierEvent.LanguageDetectionEvent.Builder(int)` |  |

---

### `class static final TextClassifierEvent.TextLinkifyEvent`

- **Extends:** `android.view.textclassifier.TextClassifierEvent`
- **Implements:** `android.os.Parcelable`

---

### `class static final TextClassifierEvent.TextLinkifyEvent.Builder`

- **Extends:** `android.view.textclassifier.TextClassifierEvent.Builder<android.view.textclassifier.TextClassifierEvent.TextLinkifyEvent.Builder>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassifierEvent.TextLinkifyEvent.Builder(int)` |  |

---

### `class static final TextClassifierEvent.TextSelectionEvent`

- **Extends:** `android.view.textclassifier.TextClassifierEvent`
- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getRelativeSuggestedWordEndIndex()` |  |
| `int getRelativeSuggestedWordStartIndex()` |  |
| `int getRelativeWordEndIndex()` |  |
| `int getRelativeWordStartIndex()` |  |

---

### `class static final TextClassifierEvent.TextSelectionEvent.Builder`

- **Extends:** `android.view.textclassifier.TextClassifierEvent.Builder<android.view.textclassifier.TextClassifierEvent.TextSelectionEvent.Builder>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextClassifierEvent.TextSelectionEvent.Builder(int)` |  |

---

### `class final TextLanguage`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextLanguage.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextLanguage.Builder()` |  |

---

### `class static final TextLanguage.Request`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextLanguage.Request.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextLanguage.Request.Builder(@NonNull CharSequence)` |  |

---

### `class final TextLinks`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int APPLY_STRATEGY_IGNORE = 0` |  |
| `static final int APPLY_STRATEGY_REPLACE = 1` |  |
| `static final int STATUS_DIFFERENT_TEXT = 3` |  |
| `static final int STATUS_LINKS_APPLIED = 0` |  |
| `static final int STATUS_NO_LINKS_APPLIED = 2` |  |
| `static final int STATUS_NO_LINKS_FOUND = 1` |  |
| `static final int STATUS_UNSUPPORTED_CHARACTER = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int apply(@NonNull android.text.Spannable, int, @Nullable java.util.function.Function<android.view.textclassifier.TextLinks.TextLink,android.view.textclassifier.TextLinks.TextLinkSpan>)` |  |
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextLinks.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextLinks.Builder(@NonNull String)` |  |

---

### `class static final TextLinks.Request`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextLinks.Request.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextLinks.Request.Builder(@NonNull CharSequence)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.textclassifier.TextLinks.Request.Builder setExtras(@Nullable android.os.Bundle)` |  |

---

### `class static final TextLinks.TextLink`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getEnd()` |  |
| `int getEntityCount()` |  |
| `int getStart()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static TextLinks.TextLinkSpan`

- **Extends:** `android.text.style.ClickableSpan`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextLinks.TextLinkSpan(@NonNull android.view.textclassifier.TextLinks.TextLink)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.view.textclassifier.TextLinks.TextLink getTextLink()` |  |
| `void onClick(android.view.View)` |  |

---

### `class final TextSelection`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSelectionEndIndex()` |  |
| `int getSelectionStartIndex()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextSelection.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextSelection.Builder(@IntRange(from=0) int, @IntRange(from=0) int)` |  |

---

### `class static final TextSelection.Request`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final TextSelection.Request.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextSelection.Request.Builder(@NonNull CharSequence, @IntRange(from=0) int, @IntRange(from=0) int)` |  |

---

## Package: `android.view.textservice`

### `class final SentenceSuggestionsInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SentenceSuggestionsInfo(android.view.textservice.SuggestionsInfo[], int[], int[])` |  |
| `SentenceSuggestionsInfo(android.os.Parcel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getLengthAt(int)` |  |
| `int getOffsetAt(int)` |  |
| `int getSuggestionsCount()` |  |
| `android.view.textservice.SuggestionsInfo getSuggestionsInfoAt(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SpellCheckerInfo`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.content.ComponentName getComponent()` |  |
| `String getId()` |  |
| `String getPackageName()` |  |
| `android.content.pm.ServiceInfo getServiceInfo()` |  |
| `String getSettingsActivity()` |  |
| `android.view.textservice.SpellCheckerSubtype getSubtypeAt(int)` |  |
| `int getSubtypeCount()` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SpellCheckerSession`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_META_DATA = "android.view.textservice.scs"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `void close()` |  |
| `void getSentenceSuggestions(android.view.textservice.TextInfo[], int)` |  |
| `android.view.textservice.SpellCheckerInfo getSpellChecker()` |  |
| `boolean isSessionDisconnected()` |  |

---

### `interface static SpellCheckerSession.SpellCheckerSessionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGetSentenceSuggestions(android.view.textservice.SentenceSuggestionsInfo[])` |  |
| `void onGetSuggestions(android.view.textservice.SuggestionsInfo[])` |  |

---

### `class final SpellCheckerSubtype`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean containsExtraValueKey(String)` |  |
| `int describeContents()` |  |
| `CharSequence getDisplayName(android.content.Context, String, android.content.pm.ApplicationInfo)` |  |
| `String getExtraValue()` |  |
| `String getExtraValueOf(String)` |  |
| `int getNameResId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SuggestionsInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SuggestionsInfo(int, String[])` |  |
| `SuggestionsInfo(int, String[], int, int)` |  |
| `SuggestionsInfo(android.os.Parcel)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int RESULT_ATTR_HAS_RECOMMENDED_SUGGESTIONS = 4` |  |
| `static final int RESULT_ATTR_IN_THE_DICTIONARY = 1` |  |
| `static final int RESULT_ATTR_LOOKS_LIKE_TYPO = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCookie()` |  |
| `int getSequence()` |  |
| `String getSuggestionAt(int)` |  |
| `int getSuggestionsAttributes()` |  |
| `int getSuggestionsCount()` |  |
| `void setCookieAndSequence(int, int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final TextInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextInfo(String)` |  |
| `TextInfo(String, int, int)` |  |
| `TextInfo(CharSequence, int, int, int, int)` |  |
| `TextInfo(android.os.Parcel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `CharSequence getCharSequence()` |  |
| `int getCookie()` |  |
| `int getSequence()` |  |
| `String getText()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final TextServicesManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.textservice.SpellCheckerSession newSpellCheckerSession(android.os.Bundle, java.util.Locale, android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener, boolean)` |  |

---

