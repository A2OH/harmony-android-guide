# Android 11 (API 30) Public API Enumeration: Android Widget

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.widget` | 177 | 1525 | 113 | 325 |
| `android.widget.inline` | 4 | 8 | 0 | 1 |
| **TOTAL** | **181** | **1533** | **113** | **326** |

---

## Package: `android.widget`

### `class abstract AbsListView`

- **继承：** `android.widget.AdapterView<android.widget.ListAdapter>`
- **实现：** `android.widget.Filter.FilterListener android.text.TextWatcher android.view.ViewTreeObserver.OnGlobalLayoutListener android.view.ViewTreeObserver.OnTouchModeChangeListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AbsListView(android.content.Context)` |  |
| `AbsListView(android.content.Context, android.util.AttributeSet)` |  |
| `AbsListView(android.content.Context, android.util.AttributeSet, int)` |  |
| `AbsListView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CHOICE_MODE_MULTIPLE = 2` |  |
| `static final int CHOICE_MODE_MULTIPLE_MODAL = 3` |  |
| `static final int CHOICE_MODE_NONE = 0` |  |
| `static final int CHOICE_MODE_SINGLE = 1` |  |
| `static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2` |  |
| `static final int TRANSCRIPT_MODE_DISABLED = 0` |  |
| `static final int TRANSCRIPT_MODE_NORMAL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void afterTextChanged(android.text.Editable)` |  |
| `void beforeTextChanged(CharSequence, int, int, int)` |  |
| `boolean canScrollList(int)` |  |
| `void clearChoices()` |  |
| `void clearTextFilter()` |  |
| `void deferNotifyDataSetChanged()` |  |
| `void fling(int)` |  |
| `android.widget.AbsListView.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `int getCheckedItemCount()` |  |
| `long[] getCheckedItemIds()` |  |
| `int getCheckedItemPosition()` |  |
| `android.util.SparseBooleanArray getCheckedItemPositions()` |  |
| `int getChoiceMode()` |  |
| `int getListPaddingBottom()` |  |
| `int getListPaddingLeft()` |  |
| `int getListPaddingRight()` |  |
| `int getListPaddingTop()` |  |
| `android.graphics.drawable.Drawable getSelector()` |  |
| `CharSequence getTextFilter()` |  |
| `int getTranscriptMode()` |  |
| `void handleDataChanged()` |  |
| `boolean hasTextFilter()` |  |
| `void invalidateViews()` |  |
| `boolean isDrawSelectorOnTop()` |  |
| `boolean isFastScrollAlwaysVisible()` |  |
| `boolean isInFilterMode()` |  |
| `boolean isItemChecked(int)` |  |
| `void layoutChildren()` |  |
| `void onFilterComplete(int)` |  |
| `void onGlobalLayout()` |  |
| `void onInitializeAccessibilityNodeInfoForItem(android.view.View, int, android.view.accessibility.AccessibilityNodeInfo)` |  |
| `boolean onRemoteAdapterConnected()` |  |
| `void onRemoteAdapterDisconnected()` |  |
| `void onRestoreInstanceState(android.os.Parcelable)` |  |
| `android.os.Parcelable onSaveInstanceState()` |  |
| `void onTextChanged(CharSequence, int, int, int)` |  |
| `void onTouchModeChanged(boolean)` |  |
| `int pointToPosition(int, int)` |  |
| `long pointToRowId(int, int)` |  |
| `void reclaimViews(java.util.List<android.view.View>)` |  |
| `void scrollListBy(int)` |  |
| `void setAdapter(android.widget.ListAdapter)` |  |
| `void setBottomEdgeEffectColor(@ColorInt int)` |  |
| `void setCacheColorHint(@ColorInt int)` |  |
| `void setChoiceMode(int)` |  |
| `void setDrawSelectorOnTop(boolean)` |  |
| `void setEdgeEffectColor(@ColorInt int)` |  |
| `void setFastScrollAlwaysVisible(boolean)` |  |
| `void setFastScrollEnabled(boolean)` |  |
| `void setFastScrollStyle(int)` |  |
| `void setFilterText(String)` |  |
| `void setFriction(float)` |  |
| `void setItemChecked(int, boolean)` |  |
| `void setMultiChoiceModeListener(android.widget.AbsListView.MultiChoiceModeListener)` |  |
| `void setOnScrollListener(android.widget.AbsListView.OnScrollListener)` |  |
| `void setRecyclerListener(android.widget.AbsListView.RecyclerListener)` |  |
| `void setRemoteViewsAdapter(android.content.Intent)` |  |
| `void setScrollIndicators(android.view.View, android.view.View)` |  |
| `void setScrollingCacheEnabled(boolean)` |  |
| `void setSelectionFromTop(int, int)` |  |
| `void setSelector(@DrawableRes int)` |  |
| `void setSelector(android.graphics.drawable.Drawable)` |  |
| `void setSmoothScrollbarEnabled(boolean)` |  |
| `void setStackFromBottom(boolean)` |  |
| `void setTextFilterEnabled(boolean)` |  |
| `void setTopEdgeEffectColor(@ColorInt int)` |  |
| `void setTranscriptMode(int)` |  |
| `void setVelocityScale(float)` |  |
| `void smoothScrollBy(int, int)` |  |
| `void smoothScrollToPosition(int)` |  |
| `void smoothScrollToPosition(int, int)` |  |
| `void smoothScrollToPositionFromTop(int, int, int)` |  |
| `void smoothScrollToPositionFromTop(int, int)` |  |
| `boolean verifyDrawable(@NonNull android.graphics.drawable.Drawable)` |  |

---

### `class static AbsListView.LayoutParams`

- **继承：** `android.view.ViewGroup.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AbsListView.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `AbsListView.LayoutParams(int, int)` |  |
| `AbsListView.LayoutParams(int, int, int)` |  |
| `AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |

---

### `interface static AbsListView.MultiChoiceModeListener`

- **继承：** `android.view.ActionMode.Callback`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onItemCheckedStateChanged(android.view.ActionMode, int, long, boolean)` |  |

---

### `interface static AbsListView.OnScrollListener`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SCROLL_STATE_FLING = 2` |  |
| `static final int SCROLL_STATE_IDLE = 0` |  |
| `static final int SCROLL_STATE_TOUCH_SCROLL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onScroll(android.widget.AbsListView, int, int, int)` |  |
| `void onScrollStateChanged(android.widget.AbsListView, int)` |  |

---

### `interface static AbsListView.RecyclerListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onMovedToScrapHeap(android.view.View)` |  |

---

### `interface static AbsListView.SelectionBoundsAdjuster`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void adjustListItemSelectionBounds(android.graphics.Rect)` |  |

---

### `class abstract AbsSeekBar`

- **继承：** `android.widget.ProgressBar`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AbsSeekBar(android.content.Context)` |  |
| `AbsSeekBar(android.content.Context, android.util.AttributeSet)` |  |
| `AbsSeekBar(android.content.Context, android.util.AttributeSet, int)` |  |
| `AbsSeekBar(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getKeyProgressIncrement()` |  |
| `boolean getSplitTrack()` |  |
| `android.graphics.drawable.Drawable getThumb()` |  |
| `int getThumbOffset()` |  |
| `android.graphics.drawable.Drawable getTickMark()` |  |
| `void setKeyProgressIncrement(int)` |  |
| `void setSplitTrack(boolean)` |  |
| `void setThumb(android.graphics.drawable.Drawable)` |  |
| `void setThumbOffset(int)` |  |
| `void setThumbTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setThumbTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setThumbTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setTickMark(android.graphics.drawable.Drawable)` |  |
| `void setTickMarkTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setTickMarkTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setTickMarkTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |

---

### `class abstract AbsSpinner`

- **继承：** `android.widget.AdapterView<android.widget.SpinnerAdapter>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AbsSpinner(android.content.Context)` |  |
| `AbsSpinner(android.content.Context, android.util.AttributeSet)` |  |
| `AbsSpinner(android.content.Context, android.util.AttributeSet, int)` |  |
| `AbsSpinner(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.SpinnerAdapter getAdapter()` |  |
| `android.view.View getSelectedView()` |  |
| `void onRestoreInstanceState(android.os.Parcelable)` |  |
| `android.os.Parcelable onSaveInstanceState()` |  |
| `int pointToPosition(int, int)` |  |
| `void setAdapter(android.widget.SpinnerAdapter)` |  |
| `void setSelection(int, boolean)` |  |
| `void setSelection(int)` |  |

---

### `class AbsoluteLayout` ~~DEPRECATED~~

- **继承：** `android.view.ViewGroup`
- **注解：** `@Deprecated @android.widget.RemoteViews.RemoteView`

---

### `class static AbsoluteLayout.LayoutParams` ~~DEPRECATED~~

- **继承：** `android.view.ViewGroup.LayoutParams`
- **注解：** `@Deprecated`

---

### `class ActionMenuView`

- **继承：** `android.widget.LinearLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ActionMenuView(android.content.Context)` |  |
| `ActionMenuView(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dismissPopupMenus()` |  |
| `android.widget.ActionMenuView.LayoutParams generateDefaultLayoutParams()` |  |
| `android.widget.ActionMenuView.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.widget.ActionMenuView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `android.view.Menu getMenu()` |  |
| `int getPopupTheme()` |  |
| `boolean hideOverflowMenu()` |  |
| `boolean isOverflowMenuShowing()` |  |
| `void onConfigurationChanged(android.content.res.Configuration)` |  |
| `void onDetachedFromWindow()` |  |
| `void setOnMenuItemClickListener(android.widget.ActionMenuView.OnMenuItemClickListener)` |  |
| `void setOverflowIcon(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setPopupTheme(@StyleRes int)` |  |
| `boolean showOverflowMenu()` |  |

---

### `class static ActionMenuView.LayoutParams`

- **继承：** `android.widget.LinearLayout.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ActionMenuView.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `ActionMenuView.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `ActionMenuView.LayoutParams(android.widget.ActionMenuView.LayoutParams)` |  |
| `ActionMenuView.LayoutParams(int, int)` |  |

---

### `interface static ActionMenuView.OnMenuItemClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onMenuItemClick(android.view.MenuItem)` |  |

---

### `interface Adapter`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int IGNORE_ITEM_VIEW_TYPE = -1` |  |
| `static final int NO_SELECTION = -2147483648` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCount()` |  |
| `Object getItem(int)` |  |
| `long getItemId(int)` |  |
| `int getItemViewType(int)` |  |
| `android.view.View getView(int, android.view.View, android.view.ViewGroup)` |  |
| `int getViewTypeCount()` |  |
| `boolean hasStableIds()` |  |
| `boolean isEmpty()` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class abstract AdapterView<T`

- **继承：** `android.widget.Adapter> extends android.view.ViewGroup`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AdapterView(android.content.Context)` |  |
| `AdapterView(android.content.Context, android.util.AttributeSet)` |  |
| `AdapterView(android.content.Context, android.util.AttributeSet, int)` |  |
| `AdapterView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INVALID_POSITION = -1` |  |
| `static final long INVALID_ROW_ID = -9223372036854775808L` |  |
| `static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2` |  |
| `static final int ITEM_VIEW_TYPE_IGNORE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract T getAdapter()` |  |
| `android.view.View getEmptyView()` |  |
| `int getFirstVisiblePosition()` |  |
| `Object getItemAtPosition(int)` |  |
| `long getItemIdAtPosition(int)` |  |
| `int getLastVisiblePosition()` |  |
| `final android.widget.AdapterView.OnItemLongClickListener getOnItemLongClickListener()` |  |
| `int getPositionForView(android.view.View)` |  |
| `Object getSelectedItem()` |  |
| `abstract android.view.View getSelectedView()` |  |
| `boolean performItemClick(android.view.View, int, long)` |  |
| `abstract void setAdapter(T)` |  |
| `void setEmptyView(android.view.View)` |  |
| `void setOnItemClickListener(@Nullable android.widget.AdapterView.OnItemClickListener)` |  |
| `void setOnItemLongClickListener(android.widget.AdapterView.OnItemLongClickListener)` |  |
| `void setOnItemSelectedListener(@Nullable android.widget.AdapterView.OnItemSelectedListener)` |  |
| `abstract void setSelection(int)` |  |

---

### `class static AdapterView.AdapterContextMenuInfo`

- **实现：** `android.view.ContextMenu.ContextMenuInfo`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AdapterView.AdapterContextMenuInfo(android.view.View, int, long)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `long id` |  |
| `int position` |  |
| `android.view.View targetView` |  |

---

### `interface static AdapterView.OnItemClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onItemClick(android.widget.AdapterView<?>, android.view.View, int, long)` |  |

---

### `interface static AdapterView.OnItemLongClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onItemLongClick(android.widget.AdapterView<?>, android.view.View, int, long)` |  |

---

### `interface static AdapterView.OnItemSelectedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onItemSelected(android.widget.AdapterView<?>, android.view.View, int, long)` |  |
| `void onNothingSelected(android.widget.AdapterView<?>)` |  |

---

### `class abstract AdapterViewAnimator`

- **继承：** `android.widget.AdapterView<android.widget.Adapter>`
- **实现：** `android.widget.Advanceable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AdapterViewAnimator(android.content.Context)` |  |
| `AdapterViewAnimator(android.content.Context, android.util.AttributeSet)` |  |
| `AdapterViewAnimator(android.content.Context, android.util.AttributeSet, int)` |  |
| `AdapterViewAnimator(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void advance()` |  |
| `void deferNotifyDataSetChanged()` |  |
| `void fyiWillBeAdvancedByHostKThx()` |  |
| `android.widget.Adapter getAdapter()` |  |
| `android.view.View getCurrentView()` |  |
| `int getDisplayedChild()` |  |
| `android.animation.ObjectAnimator getInAnimation()` |  |
| `android.animation.ObjectAnimator getOutAnimation()` |  |
| `android.view.View getSelectedView()` |  |
| `boolean onRemoteAdapterConnected()` |  |
| `void onRemoteAdapterDisconnected()` |  |
| `void onRestoreInstanceState(android.os.Parcelable)` |  |
| `android.os.Parcelable onSaveInstanceState()` |  |
| `void setAdapter(android.widget.Adapter)` |  |
| `void setAnimateFirstView(boolean)` |  |
| `void setDisplayedChild(int)` |  |
| `void setInAnimation(android.animation.ObjectAnimator)` |  |
| `void setInAnimation(android.content.Context, int)` |  |
| `void setOutAnimation(android.animation.ObjectAnimator)` |  |
| `void setOutAnimation(android.content.Context, int)` |  |
| `void setRemoteViewsAdapter(android.content.Intent)` |  |
| `void setSelection(int)` |  |
| `void showNext()` |  |
| `void showPrevious()` |  |

---

### `class AdapterViewFlipper`

- **继承：** `android.widget.AdapterViewAnimator`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AdapterViewFlipper(android.content.Context)` |  |
| `AdapterViewFlipper(android.content.Context, android.util.AttributeSet)` |  |
| `AdapterViewFlipper(android.content.Context, android.util.AttributeSet, int)` |  |
| `AdapterViewFlipper(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getFlipInterval()` |  |
| `boolean isAutoStart()` |  |
| `boolean isFlipping()` |  |
| `void setAutoStart(boolean)` |  |
| `void setFlipInterval(int)` |  |
| `void startFlipping()` |  |
| `void stopFlipping()` |  |

---

### `interface Advanceable`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void advance()` |  |
| `void fyiWillBeAdvancedByHostKThx()` |  |

---

### `class AlphabetIndexer`

- **继承：** `android.database.DataSetObserver`
- **实现：** `android.widget.SectionIndexer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AlphabetIndexer(android.database.Cursor, int, CharSequence)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence mAlphabet` |  |
| `int mColumnIndex` |  |
| `android.database.Cursor mDataCursor` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int compare(String, String)` |  |
| `int getPositionForSection(int)` |  |
| `int getSectionForPosition(int)` |  |
| `Object[] getSections()` |  |
| `void setCursor(android.database.Cursor)` |  |

---

### `class AnalogClock` ~~DEPRECATED~~

- **继承：** `android.view.View`
- **注解：** `@Deprecated @android.widget.RemoteViews.RemoteView`

---

### `class ArrayAdapter<T>`

- **继承：** `android.widget.BaseAdapter`
- **实现：** `android.widget.Filterable android.widget.ThemedSpinnerAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int)` |  |
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int, @IdRes int)` |  |
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int, @NonNull T[])` |  |
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int, @IdRes int, @NonNull T[])` |  |
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int, @NonNull java.util.List<T>)` |  |
| `ArrayAdapter(@NonNull android.content.Context, @LayoutRes int, @IdRes int, @NonNull java.util.List<T>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void add(@Nullable T)` |  |
| `void addAll(@NonNull java.util.Collection<? extends T>)` |  |
| `void addAll(T...)` |  |
| `void clear()` |  |
| `int getCount()` |  |
| `long getItemId(int)` |  |
| `int getPosition(@Nullable T)` |  |
| `void insert(@Nullable T, int)` |  |
| `void remove(@Nullable T)` |  |
| `void setDropDownViewResource(@LayoutRes int)` |  |
| `void setDropDownViewTheme(@Nullable android.content.res.Resources.Theme)` |  |
| `void setNotifyOnChange(boolean)` |  |
| `void sort(@NonNull java.util.Comparator<? super T>)` |  |

---

### `class AutoCompleteTextView`

- **继承：** `android.widget.EditText`
- **实现：** `android.widget.Filter.FilterListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AutoCompleteTextView(android.content.Context)` |  |
| `AutoCompleteTextView(android.content.Context, android.util.AttributeSet)` |  |
| `AutoCompleteTextView(android.content.Context, android.util.AttributeSet, int)` |  |
| `AutoCompleteTextView(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `AutoCompleteTextView(android.content.Context, android.util.AttributeSet, int, int, android.content.res.Resources.Theme)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearListSelection()` |  |
| `CharSequence convertSelectionToString(Object)` |  |
| `void dismissDropDown()` |  |
| `boolean enoughToFilter()` |  |
| `android.widget.ListAdapter getAdapter()` |  |
| `CharSequence getCompletionHint()` |  |
| `int getDropDownAnchor()` |  |
| `android.graphics.drawable.Drawable getDropDownBackground()` |  |
| `int getDropDownHeight()` |  |
| `int getDropDownHorizontalOffset()` |  |
| `int getDropDownVerticalOffset()` |  |
| `int getDropDownWidth()` |  |
| `android.widget.Filter getFilter()` |  |
| `int getInputMethodMode()` |  |
| `int getListSelection()` |  |
| `android.widget.AdapterView.OnItemClickListener getOnItemClickListener()` |  |
| `android.widget.AdapterView.OnItemSelectedListener getOnItemSelectedListener()` |  |
| `int getThreshold()` |  |
| `android.widget.AutoCompleteTextView.Validator getValidator()` |  |
| `boolean isPerformingCompletion()` |  |
| `boolean isPopupShowing()` |  |
| `void onFilterComplete(int)` |  |
| `void performCompletion()` |  |
| `void performFiltering(CharSequence, int)` |  |
| `void performValidation()` |  |
| `final void refreshAutoCompleteResults()` |  |
| `void replaceText(CharSequence)` |  |
| `<T extends android.widget.ListAdapter & android.widget.Filterable> void setAdapter(T)` |  |
| `void setCompletionHint(CharSequence)` |  |
| `void setDropDownAnchor(int)` |  |
| `void setDropDownBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setDropDownBackgroundResource(@DrawableRes int)` |  |
| `void setDropDownHeight(int)` |  |
| `void setDropDownHorizontalOffset(int)` |  |
| `void setDropDownVerticalOffset(int)` |  |
| `void setDropDownWidth(int)` |  |
| `void setInputMethodMode(int)` |  |
| `void setListSelection(int)` |  |
| `void setOnDismissListener(android.widget.AutoCompleteTextView.OnDismissListener)` |  |
| `void setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)` |  |
| `void setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)` |  |
| `void setText(CharSequence, boolean)` |  |
| `void setThreshold(int)` |  |
| `void setValidator(android.widget.AutoCompleteTextView.Validator)` |  |
| `void showDropDown()` |  |

---

### `interface static AutoCompleteTextView.OnDismissListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDismiss()` |  |

---

### `interface static AutoCompleteTextView.Validator`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence fixText(CharSequence)` |  |
| `boolean isValid(CharSequence)` |  |

---

### `class abstract BaseAdapter`

- **实现：** `android.widget.ListAdapter android.widget.SpinnerAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BaseAdapter()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean areAllItemsEnabled()` |  |
| `android.view.View getDropDownView(int, android.view.View, android.view.ViewGroup)` |  |
| `int getItemViewType(int)` |  |
| `int getViewTypeCount()` |  |
| `boolean hasStableIds()` |  |
| `boolean isEmpty()` |  |
| `boolean isEnabled(int)` |  |
| `void notifyDataSetChanged()` |  |
| `void notifyDataSetInvalidated()` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `void setAutofillOptions(@Nullable java.lang.CharSequence...)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class abstract BaseExpandableListAdapter`

- **实现：** `android.widget.ExpandableListAdapter android.widget.HeterogeneousExpandableList`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BaseExpandableListAdapter()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean areAllItemsEnabled()` |  |
| `int getChildType(int, int)` |  |
| `int getChildTypeCount()` |  |
| `long getCombinedChildId(long, long)` |  |
| `long getCombinedGroupId(long)` |  |
| `int getGroupType(int)` |  |
| `int getGroupTypeCount()` |  |
| `boolean isEmpty()` |  |
| `void notifyDataSetChanged()` |  |
| `void notifyDataSetInvalidated()` |  |
| `void onGroupCollapsed(int)` |  |
| `void onGroupExpanded(int)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class Button`

- **继承：** `android.widget.TextView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Button(android.content.Context)` |  |
| `Button(android.content.Context, android.util.AttributeSet)` |  |
| `Button(android.content.Context, android.util.AttributeSet, int)` |  |
| `Button(android.content.Context, android.util.AttributeSet, int, int)` |  |

---

### `class CalendarView`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CalendarView(@NonNull android.content.Context)` |  |
| `CalendarView(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `CalendarView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int)` |  |
| `CalendarView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int, @StyleRes int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getDate()` |  |
| `int getFirstDayOfWeek()` |  |
| `long getMaxDate()` |  |
| `long getMinDate()` |  |
| `void setDate(long)` |  |
| `void setDate(long, boolean, boolean)` |  |
| `void setDateTextAppearance(@StyleRes int)` |  |
| `void setFirstDayOfWeek(int)` |  |
| `void setMaxDate(long)` |  |
| `void setMinDate(long)` |  |
| `void setOnDateChangeListener(android.widget.CalendarView.OnDateChangeListener)` |  |
| `void setWeekDayTextAppearance(@StyleRes int)` |  |

---

### `interface static CalendarView.OnDateChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onSelectedDayChange(@NonNull android.widget.CalendarView, int, int, int)` |  |

---

### `class CheckBox`

- **继承：** `android.widget.CompoundButton`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CheckBox(android.content.Context)` |  |
| `CheckBox(android.content.Context, android.util.AttributeSet)` |  |
| `CheckBox(android.content.Context, android.util.AttributeSet, int)` |  |
| `CheckBox(android.content.Context, android.util.AttributeSet, int, int)` |  |

---

### `interface Checkable`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isChecked()` |  |
| `void setChecked(boolean)` |  |
| `void toggle()` |  |

---

### `class CheckedTextView`

- **继承：** `android.widget.TextView`
- **实现：** `android.widget.Checkable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CheckedTextView(android.content.Context)` |  |
| `CheckedTextView(android.content.Context, android.util.AttributeSet)` |  |
| `CheckedTextView(android.content.Context, android.util.AttributeSet, int)` |  |
| `CheckedTextView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.Drawable getCheckMarkDrawable()` |  |
| `void setCheckMarkDrawable(@DrawableRes int)` |  |
| `void setCheckMarkDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setCheckMarkTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setCheckMarkTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setCheckMarkTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setChecked(boolean)` |  |
| `void toggle()` |  |

---

### `class Chronometer`

- **继承：** `android.widget.TextView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Chronometer(android.content.Context)` |  |
| `Chronometer(android.content.Context, android.util.AttributeSet)` |  |
| `Chronometer(android.content.Context, android.util.AttributeSet, int)` |  |
| `Chronometer(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getBase()` |  |
| `String getFormat()` |  |
| `android.widget.Chronometer.OnChronometerTickListener getOnChronometerTickListener()` |  |
| `boolean isCountDown()` |  |
| `boolean isTheFinalCountDown()` |  |
| `void setBase(long)` |  |
| `void setCountDown(boolean)` |  |
| `void setFormat(String)` |  |
| `void setOnChronometerTickListener(android.widget.Chronometer.OnChronometerTickListener)` |  |
| `void start()` |  |
| `void stop()` |  |

---

### `interface static Chronometer.OnChronometerTickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onChronometerTick(android.widget.Chronometer)` |  |

---

### `class abstract CompoundButton`

- **继承：** `android.widget.Button`
- **实现：** `android.widget.Checkable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CompoundButton(android.content.Context)` |  |
| `CompoundButton(android.content.Context, android.util.AttributeSet)` |  |
| `CompoundButton(android.content.Context, android.util.AttributeSet, int)` |  |
| `CompoundButton(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setButtonDrawable(@DrawableRes int)` |  |
| `void setButtonDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setButtonTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setButtonTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setButtonTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setChecked(boolean)` |  |
| `void setOnCheckedChangeListener(@Nullable android.widget.CompoundButton.OnCheckedChangeListener)` |  |
| `void toggle()` |  |

---

### `interface static CompoundButton.OnCheckedChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCheckedChanged(android.widget.CompoundButton, boolean)` |  |

---

### `class abstract CursorAdapter`

- **继承：** `android.widget.BaseAdapter`
- **实现：** `android.widget.Filterable android.widget.ThemedSpinnerAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CursorAdapter(android.content.Context, android.database.Cursor, boolean)` |  |
| `CursorAdapter(android.content.Context, android.database.Cursor, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_REGISTER_CONTENT_OBSERVER = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void bindView(android.view.View, android.content.Context, android.database.Cursor)` |  |
| `void changeCursor(android.database.Cursor)` |  |
| `CharSequence convertToString(android.database.Cursor)` |  |
| `int getCount()` |  |
| `android.database.Cursor getCursor()` |  |
| `android.content.res.Resources.Theme getDropDownViewTheme()` |  |
| `android.widget.Filter getFilter()` |  |
| `android.widget.FilterQueryProvider getFilterQueryProvider()` |  |
| `Object getItem(int)` |  |
| `long getItemId(int)` |  |
| `android.view.View getView(int, android.view.View, android.view.ViewGroup)` |  |
| `android.view.View newDropDownView(android.content.Context, android.database.Cursor, android.view.ViewGroup)` |  |
| `abstract android.view.View newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)` |  |
| `void onContentChanged()` |  |
| `void setDropDownViewTheme(android.content.res.Resources.Theme)` |  |
| `void setFilterQueryProvider(android.widget.FilterQueryProvider)` |  |
| `android.database.Cursor swapCursor(android.database.Cursor)` |  |

---

### `class abstract CursorTreeAdapter`

- **继承：** `android.widget.BaseExpandableListAdapter`
- **实现：** `android.widget.Filterable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CursorTreeAdapter(android.database.Cursor, android.content.Context)` |  |
| `CursorTreeAdapter(android.database.Cursor, android.content.Context, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void bindChildView(android.view.View, android.content.Context, android.database.Cursor, boolean)` |  |
| `abstract void bindGroupView(android.view.View, android.content.Context, android.database.Cursor, boolean)` |  |
| `void changeCursor(android.database.Cursor)` |  |
| `String convertToString(android.database.Cursor)` |  |
| `android.database.Cursor getChild(int, int)` |  |
| `long getChildId(int, int)` |  |
| `android.view.View getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `int getChildrenCount(int)` |  |
| `abstract android.database.Cursor getChildrenCursor(android.database.Cursor)` |  |
| `android.database.Cursor getCursor()` |  |
| `android.widget.Filter getFilter()` |  |
| `android.widget.FilterQueryProvider getFilterQueryProvider()` |  |
| `android.database.Cursor getGroup(int)` |  |
| `int getGroupCount()` |  |
| `long getGroupId(int)` |  |
| `android.view.View getGroupView(int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `boolean hasStableIds()` |  |
| `boolean isChildSelectable(int, int)` |  |
| `abstract android.view.View newChildView(android.content.Context, android.database.Cursor, boolean, android.view.ViewGroup)` |  |
| `abstract android.view.View newGroupView(android.content.Context, android.database.Cursor, boolean, android.view.ViewGroup)` |  |
| `void notifyDataSetChanged(boolean)` |  |
| `android.database.Cursor runQueryOnBackgroundThread(CharSequence)` |  |
| `void setChildrenCursor(int, android.database.Cursor)` |  |
| `void setFilterQueryProvider(android.widget.FilterQueryProvider)` |  |
| `void setGroupCursor(android.database.Cursor)` |  |

---

### `class DatePicker`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DatePicker(android.content.Context)` |  |
| `DatePicker(android.content.Context, android.util.AttributeSet)` |  |
| `DatePicker(android.content.Context, android.util.AttributeSet, int)` |  |
| `DatePicker(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getDayOfMonth()` |  |
| `int getFirstDayOfWeek()` |  |
| `long getMaxDate()` |  |
| `long getMinDate()` |  |
| `int getMonth()` |  |
| `int getYear()` |  |
| `void init(int, int, int, android.widget.DatePicker.OnDateChangedListener)` |  |
| `void setFirstDayOfWeek(int)` |  |
| `void setMaxDate(long)` |  |
| `void setMinDate(long)` |  |
| `void setOnDateChangedListener(android.widget.DatePicker.OnDateChangedListener)` |  |
| `void updateDate(int, int, int)` |  |

---

### `interface static DatePicker.OnDateChangedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDateChanged(android.widget.DatePicker, int, int, int)` |  |

---

### `class DialerFilter` ~~DEPRECATED~~

- **继承：** `android.widget.RelativeLayout`
- **注解：** `@Deprecated`

---

### `class DigitalClock` ~~DEPRECATED~~

- **继承：** `android.widget.TextView`
- **注解：** `@Deprecated`

---

### `class EdgeEffect`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EdgeEffect(android.content.Context)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.graphics.BlendMode DEFAULT_BLEND_MODE` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean draw(android.graphics.Canvas)` |  |
| `void finish()` |  |
| `int getMaxHeight()` |  |
| `boolean isFinished()` |  |
| `void onAbsorb(int)` |  |
| `void onPull(float)` |  |
| `void onPull(float, float)` |  |
| `void onRelease()` |  |
| `void setBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setColor(@ColorInt int)` |  |
| `void setSize(int, int)` |  |

---

### `class EditText`

- **继承：** `android.widget.TextView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EditText(android.content.Context)` |  |
| `EditText(android.content.Context, android.util.AttributeSet)` |  |
| `EditText(android.content.Context, android.util.AttributeSet, int)` |  |
| `EditText(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void extendSelection(int)` |  |
| `android.text.Editable getText()` |  |
| `void selectAll()` |  |
| `void setSelection(int, int)` |  |
| `void setSelection(int)` |  |

---

### `interface ExpandableListAdapter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean areAllItemsEnabled()` |  |
| `Object getChild(int, int)` |  |
| `long getChildId(int, int)` |  |
| `android.view.View getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `int getChildrenCount(int)` |  |
| `long getCombinedChildId(long, long)` |  |
| `long getCombinedGroupId(long)` |  |
| `Object getGroup(int)` |  |
| `int getGroupCount()` |  |
| `long getGroupId(int)` |  |
| `android.view.View getGroupView(int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `boolean hasStableIds()` |  |
| `boolean isChildSelectable(int, int)` |  |
| `boolean isEmpty()` |  |
| `void onGroupCollapsed(int)` |  |
| `void onGroupExpanded(int)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class ExpandableListView`

- **继承：** `android.widget.ListView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExpandableListView(android.content.Context)` |  |
| `ExpandableListView(android.content.Context, android.util.AttributeSet)` |  |
| `ExpandableListView(android.content.Context, android.util.AttributeSet, int)` |  |
| `ExpandableListView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CHILD_INDICATOR_INHERIT = -1` |  |
| `static final int PACKED_POSITION_TYPE_CHILD = 1` |  |
| `static final int PACKED_POSITION_TYPE_GROUP = 0` |  |
| `static final int PACKED_POSITION_TYPE_NULL = 2` |  |
| `static final long PACKED_POSITION_VALUE_NULL = 4294967295L` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean collapseGroup(int)` |  |
| `boolean expandGroup(int)` |  |
| `boolean expandGroup(int, boolean)` |  |
| `android.widget.ExpandableListAdapter getExpandableListAdapter()` |  |
| `long getExpandableListPosition(int)` |  |
| `int getFlatListPosition(long)` |  |
| `static int getPackedPositionChild(long)` |  |
| `static long getPackedPositionForChild(int, int)` |  |
| `static long getPackedPositionForGroup(int)` |  |
| `static int getPackedPositionGroup(long)` |  |
| `static int getPackedPositionType(long)` |  |
| `long getSelectedId()` |  |
| `long getSelectedPosition()` |  |
| `boolean isGroupExpanded(int)` |  |
| `void setAdapter(android.widget.ExpandableListAdapter)` |  |
| `void setChildDivider(android.graphics.drawable.Drawable)` |  |
| `void setChildIndicator(android.graphics.drawable.Drawable)` |  |
| `void setChildIndicatorBounds(int, int)` |  |
| `void setChildIndicatorBoundsRelative(int, int)` |  |
| `void setGroupIndicator(android.graphics.drawable.Drawable)` |  |
| `void setIndicatorBounds(int, int)` |  |
| `void setIndicatorBoundsRelative(int, int)` |  |
| `void setOnChildClickListener(android.widget.ExpandableListView.OnChildClickListener)` |  |
| `void setOnGroupClickListener(android.widget.ExpandableListView.OnGroupClickListener)` |  |
| `void setOnGroupCollapseListener(android.widget.ExpandableListView.OnGroupCollapseListener)` |  |
| `void setOnGroupExpandListener(android.widget.ExpandableListView.OnGroupExpandListener)` |  |
| `boolean setSelectedChild(int, int, boolean)` |  |
| `void setSelectedGroup(int)` |  |

---

### `class static ExpandableListView.ExpandableListContextMenuInfo`

- **实现：** `android.view.ContextMenu.ContextMenuInfo`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ExpandableListView.ExpandableListContextMenuInfo(android.view.View, long, long)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `long id` |  |
| `long packedPosition` |  |
| `android.view.View targetView` |  |

---

### `interface static ExpandableListView.OnChildClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)` |  |

---

### `interface static ExpandableListView.OnGroupClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onGroupClick(android.widget.ExpandableListView, android.view.View, int, long)` |  |

---

### `interface static ExpandableListView.OnGroupCollapseListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onGroupCollapse(int)` |  |

---

### `interface static ExpandableListView.OnGroupExpandListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onGroupExpand(int)` |  |

---

### `class abstract Filter`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Filter()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence convertResultToString(Object)` |  |
| `final void filter(CharSequence)` |  |
| `final void filter(CharSequence, android.widget.Filter.FilterListener)` |  |
| `abstract android.widget.Filter.FilterResults performFiltering(CharSequence)` |  |
| `abstract void publishResults(CharSequence, android.widget.Filter.FilterResults)` |  |

---

### `interface static Filter.FilterListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFilterComplete(int)` |  |

---

### `class static Filter.FilterResults`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Filter.FilterResults()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int count` |  |
| `Object values` |  |

---

### `interface FilterQueryProvider`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.database.Cursor runQuery(CharSequence)` |  |

---

### `interface Filterable`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.Filter getFilter()` |  |

---

### `class FrameLayout`

- **继承：** `android.view.ViewGroup`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FrameLayout(@NonNull android.content.Context)` |  |
| `FrameLayout(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `FrameLayout(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int)` |  |
| `FrameLayout(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int, @StyleRes int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.FrameLayout.LayoutParams generateDefaultLayoutParams()` |  |
| `android.widget.FrameLayout.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `boolean getMeasureAllChildren()` |  |
| `void setMeasureAllChildren(boolean)` |  |

---

### `class static FrameLayout.LayoutParams`

- **继承：** `android.view.ViewGroup.MarginLayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FrameLayout.LayoutParams(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `FrameLayout.LayoutParams(int, int)` |  |
| `FrameLayout.LayoutParams(int, int, int)` |  |
| `FrameLayout.LayoutParams(@NonNull android.view.ViewGroup.LayoutParams)` |  |
| `FrameLayout.LayoutParams(@NonNull android.view.ViewGroup.MarginLayoutParams)` |  |
| `FrameLayout.LayoutParams(@NonNull android.widget.FrameLayout.LayoutParams)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int UNSPECIFIED_GRAVITY = -1` |  |
| `int gravity` |  |

---

### `class Gallery` ~~DEPRECATED~~

- **继承：** `android.widget.AbsSpinner`
- **实现：** `android.view.GestureDetector.OnGestureListener`
- **注解：** `@Deprecated`

---

### `class static Gallery.LayoutParams` ~~DEPRECATED~~

- **继承：** `android.view.ViewGroup.LayoutParams`
- **注解：** `@Deprecated`

---

### `class GridLayout`

- **继承：** `android.view.ViewGroup`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GridLayout(android.content.Context)` |  |
| `GridLayout(android.content.Context, android.util.AttributeSet)` |  |
| `GridLayout(android.content.Context, android.util.AttributeSet, int)` |  |
| `GridLayout(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALIGN_BOUNDS = 0` |  |
| `static final int ALIGN_MARGINS = 1` |  |
| `static final android.widget.GridLayout.Alignment BASELINE` |  |
| `static final android.widget.GridLayout.Alignment BOTTOM` |  |
| `static final android.widget.GridLayout.Alignment CENTER` |  |
| `static final android.widget.GridLayout.Alignment END` |  |
| `static final android.widget.GridLayout.Alignment FILL` |  |
| `static final int HORIZONTAL = 0` |  |
| `static final android.widget.GridLayout.Alignment LEFT` |  |
| `static final android.widget.GridLayout.Alignment RIGHT` |  |
| `static final android.widget.GridLayout.Alignment START` |  |
| `static final android.widget.GridLayout.Alignment TOP` |  |
| `static final int UNDEFINED = -2147483648` |  |
| `static final int VERTICAL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.GridLayout.LayoutParams generateDefaultLayoutParams()` |  |
| `android.widget.GridLayout.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.widget.GridLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `int getAlignmentMode()` |  |
| `int getColumnCount()` |  |
| `int getOrientation()` |  |
| `int getRowCount()` |  |
| `boolean getUseDefaultMargins()` |  |
| `boolean isColumnOrderPreserved()` |  |
| `boolean isRowOrderPreserved()` |  |
| `void setAlignmentMode(int)` |  |
| `void setColumnCount(int)` |  |
| `void setColumnOrderPreserved(boolean)` |  |
| `void setOrientation(int)` |  |
| `void setRowCount(int)` |  |
| `void setRowOrderPreserved(boolean)` |  |
| `void setUseDefaultMargins(boolean)` |  |
| `static android.widget.GridLayout.Spec spec(int, int, android.widget.GridLayout.Alignment, float)` |  |
| `static android.widget.GridLayout.Spec spec(int, android.widget.GridLayout.Alignment, float)` |  |
| `static android.widget.GridLayout.Spec spec(int, int, float)` |  |
| `static android.widget.GridLayout.Spec spec(int, float)` |  |
| `static android.widget.GridLayout.Spec spec(int, int, android.widget.GridLayout.Alignment)` |  |
| `static android.widget.GridLayout.Spec spec(int, android.widget.GridLayout.Alignment)` |  |
| `static android.widget.GridLayout.Spec spec(int, int)` |  |
| `static android.widget.GridLayout.Spec spec(int)` |  |

---

### `class static GridLayout.LayoutParams`

- **继承：** `android.view.ViewGroup.MarginLayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GridLayout.LayoutParams(android.widget.GridLayout.Spec, android.widget.GridLayout.Spec)` |  |
| `GridLayout.LayoutParams()` |  |
| `GridLayout.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `GridLayout.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |
| `GridLayout.LayoutParams(android.widget.GridLayout.LayoutParams)` |  |
| `GridLayout.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.GridLayout.Spec columnSpec` |  |
| `android.widget.GridLayout.Spec rowSpec` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setGravity(int)` |  |

---

### `class static GridLayout.Spec`


---

### `class GridView`

- **继承：** `android.widget.AbsListView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GridView(android.content.Context)` |  |
| `GridView(android.content.Context, android.util.AttributeSet)` |  |
| `GridView(android.content.Context, android.util.AttributeSet, int)` |  |
| `GridView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUTO_FIT = -1` |  |
| `static final int NO_STRETCH = 0` |  |
| `static final int STRETCH_COLUMN_WIDTH = 2` |  |
| `static final int STRETCH_SPACING = 1` |  |
| `static final int STRETCH_SPACING_UNIFORM = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.ListAdapter getAdapter()` |  |
| `int getColumnWidth()` |  |
| `int getGravity()` |  |
| `int getHorizontalSpacing()` |  |
| `int getRequestedColumnWidth()` |  |
| `int getRequestedHorizontalSpacing()` |  |
| `int getStretchMode()` |  |
| `int getVerticalSpacing()` |  |
| `void setColumnWidth(int)` |  |
| `void setGravity(int)` |  |
| `void setHorizontalSpacing(int)` |  |
| `void setNumColumns(int)` |  |
| `void setSelection(int)` |  |
| `void setStretchMode(int)` |  |
| `void setVerticalSpacing(int)` |  |
| `void smoothScrollByOffset(int)` |  |

---

### `class HeaderViewListAdapter`

- **实现：** `android.widget.Filterable android.widget.WrapperListAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HeaderViewListAdapter(java.util.ArrayList<android.widget.ListView.FixedViewInfo>, java.util.ArrayList<android.widget.ListView.FixedViewInfo>, android.widget.ListAdapter)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean areAllItemsEnabled()` |  |
| `int getCount()` |  |
| `android.widget.Filter getFilter()` |  |
| `int getFootersCount()` |  |
| `int getHeadersCount()` |  |
| `Object getItem(int)` |  |
| `long getItemId(int)` |  |
| `int getItemViewType(int)` |  |
| `android.view.View getView(int, android.view.View, android.view.ViewGroup)` |  |
| `int getViewTypeCount()` |  |
| `android.widget.ListAdapter getWrappedAdapter()` |  |
| `boolean hasStableIds()` |  |
| `boolean isEmpty()` |  |
| `boolean isEnabled(int)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `boolean removeFooter(android.view.View)` |  |
| `boolean removeHeader(android.view.View)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `interface HeterogeneousExpandableList`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getChildType(int, int)` |  |
| `int getChildTypeCount()` |  |
| `int getGroupType(int)` |  |
| `int getGroupTypeCount()` |  |

---

### `class HorizontalScrollView`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HorizontalScrollView(android.content.Context)` |  |
| `HorizontalScrollView(android.content.Context, android.util.AttributeSet)` |  |
| `HorizontalScrollView(android.content.Context, android.util.AttributeSet, int)` |  |
| `HorizontalScrollView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean arrowScroll(int)` |  |
| `int computeScrollDeltaToGetChildRectOnScreen(android.graphics.Rect)` |  |
| `boolean executeKeyEvent(android.view.KeyEvent)` |  |
| `void fling(int)` |  |
| `boolean fullScroll(int)` |  |
| `int getMaxScrollAmount()` |  |
| `boolean isFillViewport()` |  |
| `boolean isSmoothScrollingEnabled()` |  |
| `boolean pageScroll(int)` |  |
| `void setEdgeEffectColor(@ColorInt int)` |  |
| `void setFillViewport(boolean)` |  |
| `void setLeftEdgeEffectColor(@ColorInt int)` |  |
| `void setRightEdgeEffectColor(@ColorInt int)` |  |
| `void setSmoothScrollingEnabled(boolean)` |  |
| `final void smoothScrollBy(int, int)` |  |
| `final void smoothScrollTo(int, int)` |  |

---

### `class ImageButton`

- **继承：** `android.widget.ImageView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageButton(android.content.Context)` |  |
| `ImageButton(android.content.Context, android.util.AttributeSet)` |  |
| `ImageButton(android.content.Context, android.util.AttributeSet, int)` |  |
| `ImageButton(android.content.Context, android.util.AttributeSet, int, int)` |  |

---

### `class ImageSwitcher`

- **继承：** `android.widget.ViewSwitcher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageSwitcher(android.content.Context)` |  |
| `ImageSwitcher(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setImageDrawable(android.graphics.drawable.Drawable)` |  |
| `void setImageResource(@DrawableRes int)` |  |
| `void setImageURI(android.net.Uri)` |  |

---

### `class ImageView`

- **继承：** `android.view.View`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageView(android.content.Context)` |  |
| `ImageView(android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `ImageView(android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `ImageView(android.content.Context, @Nullable android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void animateTransform(@Nullable android.graphics.Matrix)` |  |
| `final void clearColorFilter()` |  |
| `boolean getAdjustViewBounds()` |  |
| `boolean getBaselineAlignBottom()` |  |
| `android.graphics.ColorFilter getColorFilter()` |  |
| `boolean getCropToPadding()` |  |
| `android.graphics.drawable.Drawable getDrawable()` |  |
| `int getImageAlpha()` |  |
| `android.graphics.Matrix getImageMatrix()` |  |
| `int getMaxHeight()` |  |
| `int getMaxWidth()` |  |
| `android.widget.ImageView.ScaleType getScaleType()` |  |
| `int[] onCreateDrawableState(int)` |  |
| `void setAdjustViewBounds(boolean)` |  |
| `void setBaseline(int)` |  |
| `void setBaselineAlignBottom(boolean)` |  |
| `final void setColorFilter(int, android.graphics.PorterDuff.Mode)` |  |
| `final void setColorFilter(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setCropToPadding(boolean)` |  |
| `boolean setFrame(int, int, int, int)` |  |
| `void setImageAlpha(int)` |  |
| `void setImageBitmap(android.graphics.Bitmap)` |  |
| `void setImageDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setImageIcon(@Nullable android.graphics.drawable.Icon)` |  |
| `void setImageLevel(int)` |  |
| `void setImageMatrix(android.graphics.Matrix)` |  |
| `void setImageResource(@DrawableRes int)` |  |
| `void setImageState(int[], boolean)` |  |
| `void setImageTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setImageTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setImageTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setImageURI(@Nullable android.net.Uri)` |  |
| `void setMaxHeight(int)` |  |
| `void setMaxWidth(int)` |  |
| `void setScaleType(android.widget.ImageView.ScaleType)` |  |

---

### `enum ImageView.ScaleType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.widget.ImageView.ScaleType CENTER` |  |
| `static final android.widget.ImageView.ScaleType CENTER_CROP` |  |
| `static final android.widget.ImageView.ScaleType CENTER_INSIDE` |  |
| `static final android.widget.ImageView.ScaleType FIT_CENTER` |  |
| `static final android.widget.ImageView.ScaleType FIT_END` |  |
| `static final android.widget.ImageView.ScaleType FIT_START` |  |
| `static final android.widget.ImageView.ScaleType FIT_XY` |  |
| `static final android.widget.ImageView.ScaleType MATRIX` |  |

---

### `class LinearLayout`

- **继承：** `android.view.ViewGroup`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LinearLayout(android.content.Context)` |  |
| `LinearLayout(android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `LinearLayout(android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `LinearLayout(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int HORIZONTAL = 0` |  |
| `static final int SHOW_DIVIDER_BEGINNING = 1` |  |
| `static final int SHOW_DIVIDER_END = 4` |  |
| `static final int SHOW_DIVIDER_MIDDLE = 2` |  |
| `static final int SHOW_DIVIDER_NONE = 0` |  |
| `static final int VERTICAL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.LinearLayout.LayoutParams generateDefaultLayoutParams()` |  |
| `android.widget.LinearLayout.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.widget.LinearLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `int getBaselineAlignedChildIndex()` |  |
| `android.graphics.drawable.Drawable getDividerDrawable()` |  |
| `int getDividerPadding()` |  |
| `int getGravity()` |  |
| `int getOrientation()` |  |
| `int getShowDividers()` |  |
| `float getWeightSum()` |  |
| `boolean isBaselineAligned()` |  |
| `boolean isMeasureWithLargestChildEnabled()` |  |
| `void setBaselineAligned(boolean)` |  |
| `void setBaselineAlignedChildIndex(int)` |  |
| `void setDividerDrawable(android.graphics.drawable.Drawable)` |  |
| `void setDividerPadding(int)` |  |
| `void setGravity(int)` |  |
| `void setHorizontalGravity(int)` |  |
| `void setMeasureWithLargestChildEnabled(boolean)` |  |
| `void setOrientation(int)` |  |
| `void setShowDividers(int)` |  |
| `void setVerticalGravity(int)` |  |
| `void setWeightSum(float)` |  |

---

### `class static LinearLayout.LayoutParams`

- **继承：** `android.view.ViewGroup.MarginLayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LinearLayout.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `LinearLayout.LayoutParams(int, int)` |  |
| `LinearLayout.LayoutParams(int, int, float)` |  |
| `LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `LinearLayout.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |
| `LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String debug(String)` |  |

---

### `interface ListAdapter`

- **继承：** `android.widget.Adapter`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean areAllItemsEnabled()` |  |
| `boolean isEnabled(int)` |  |

---

### `class ListPopupWindow`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ListPopupWindow(@NonNull android.content.Context)` |  |
| `ListPopupWindow(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `ListPopupWindow(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int)` |  |
| `ListPopupWindow(@NonNull android.content.Context, @Nullable android.util.AttributeSet, @AttrRes int, @StyleRes int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INPUT_METHOD_FROM_FOCUSABLE = 0` |  |
| `static final int INPUT_METHOD_NEEDED = 1` |  |
| `static final int INPUT_METHOD_NOT_NEEDED = 2` |  |
| `static final int MATCH_PARENT = -1` |  |
| `static final int POSITION_PROMPT_ABOVE = 0` |  |
| `static final int POSITION_PROMPT_BELOW = 1` |  |
| `static final int WRAP_CONTENT = -2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearListSelection()` |  |
| `android.view.View.OnTouchListener createDragToOpenListener(android.view.View)` |  |
| `void dismiss()` |  |
| `int getHeight()` |  |
| `int getHorizontalOffset()` |  |
| `int getInputMethodMode()` |  |
| `int getPromptPosition()` |  |
| `long getSelectedItemId()` |  |
| `int getSelectedItemPosition()` |  |
| `int getSoftInputMode()` |  |
| `int getVerticalOffset()` |  |
| `int getWidth()` |  |
| `boolean isInputMethodNotNeeded()` |  |
| `boolean isModal()` |  |
| `boolean isShowing()` |  |
| `boolean onKeyDown(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyPreIme(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, @NonNull android.view.KeyEvent)` |  |
| `boolean performItemClick(int)` |  |
| `void postShow()` |  |
| `void setAdapter(@Nullable android.widget.ListAdapter)` |  |
| `void setAnchorView(@Nullable android.view.View)` |  |
| `void setAnimationStyle(@StyleRes int)` |  |
| `void setBackgroundDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setContentWidth(int)` |  |
| `void setDropDownGravity(int)` |  |
| `void setEpicenterBounds(@Nullable android.graphics.Rect)` |  |
| `void setHeight(int)` |  |
| `void setHorizontalOffset(int)` |  |
| `void setInputMethodMode(int)` |  |
| `void setListSelector(android.graphics.drawable.Drawable)` |  |
| `void setModal(boolean)` |  |
| `void setOnDismissListener(@Nullable android.widget.PopupWindow.OnDismissListener)` |  |
| `void setOnItemClickListener(@Nullable android.widget.AdapterView.OnItemClickListener)` |  |
| `void setOnItemSelectedListener(@Nullable android.widget.AdapterView.OnItemSelectedListener)` |  |
| `void setPromptPosition(int)` |  |
| `void setPromptView(@Nullable android.view.View)` |  |
| `void setSelection(int)` |  |
| `void setSoftInputMode(int)` |  |
| `void setVerticalOffset(int)` |  |
| `void setWidth(int)` |  |
| `void setWindowLayoutType(int)` |  |
| `void show()` |  |

---

### `class ListView`

- **继承：** `android.widget.AbsListView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ListView(android.content.Context)` |  |
| `ListView(android.content.Context, android.util.AttributeSet)` |  |
| `ListView(android.content.Context, android.util.AttributeSet, int)` |  |
| `ListView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addFooterView(android.view.View, Object, boolean)` |  |
| `void addFooterView(android.view.View)` |  |
| `void addHeaderView(android.view.View, Object, boolean)` |  |
| `void addHeaderView(android.view.View)` |  |
| `boolean areFooterDividersEnabled()` |  |
| `boolean areHeaderDividersEnabled()` |  |
| `android.widget.ListAdapter getAdapter()` |  |
| `int getDividerHeight()` |  |
| `int getFooterViewsCount()` |  |
| `int getHeaderViewsCount()` |  |
| `boolean getItemsCanFocus()` |  |
| `int getMaxScrollAmount()` |  |
| `android.graphics.drawable.Drawable getOverscrollFooter()` |  |
| `android.graphics.drawable.Drawable getOverscrollHeader()` |  |
| `boolean removeFooterView(android.view.View)` |  |
| `boolean removeHeaderView(android.view.View)` |  |
| `void setDivider(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setDividerHeight(int)` |  |
| `void setFooterDividersEnabled(boolean)` |  |
| `void setHeaderDividersEnabled(boolean)` |  |
| `void setItemsCanFocus(boolean)` |  |
| `void setOverscrollFooter(android.graphics.drawable.Drawable)` |  |
| `void setOverscrollHeader(android.graphics.drawable.Drawable)` |  |
| `void setSelection(int)` |  |
| `void setSelectionAfterHeaderView()` |  |
| `void smoothScrollByOffset(int)` |  |

---

### `class ListView.FixedViewInfo`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ListView.FixedViewInfo()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `Object data` |  |
| `boolean isSelectable` |  |
| `android.view.View view` |  |

---

### `class final Magnifier`

- **注解：** `@UiThread`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SOURCE_BOUND_MAX_IN_SURFACE = 0` |  |
| `static final int SOURCE_BOUND_MAX_VISIBLE = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dismiss()` |  |
| `float getZoom()` |  |
| `boolean isClippingEnabled()` |  |
| `void setZoom(@FloatRange(from=0.0f) float)` |  |
| `void show(@FloatRange(from=0) float, @FloatRange(from=0) float)` |  |
| `void show(@FloatRange(from=0) float, @FloatRange(from=0) float, float, float)` |  |
| `void update()` |  |

---

### `class static final Magnifier.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Magnifier.Builder(@NonNull android.view.View)` |  |

---

### `class MediaController`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaController(android.content.Context, android.util.AttributeSet)` |  |
| `MediaController(android.content.Context, boolean)` |  |
| `MediaController(android.content.Context)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void hide()` |  |
| `boolean isShowing()` |  |
| `void onFinishInflate()` |  |
| `void setAnchorView(android.view.View)` |  |
| `void setMediaPlayer(android.widget.MediaController.MediaPlayerControl)` |  |
| `void setPrevNextListeners(android.view.View.OnClickListener, android.view.View.OnClickListener)` |  |
| `void show()` |  |
| `void show(int)` |  |

---

### `interface static MediaController.MediaPlayerControl`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canPause()` |  |
| `boolean canSeekBackward()` |  |
| `boolean canSeekForward()` |  |
| `int getAudioSessionId()` |  |
| `int getBufferPercentage()` |  |
| `int getCurrentPosition()` |  |
| `int getDuration()` |  |
| `boolean isPlaying()` |  |
| `void pause()` |  |
| `void seekTo(int)` |  |
| `void start()` |  |

---

### `class MultiAutoCompleteTextView`

- **继承：** `android.widget.AutoCompleteTextView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MultiAutoCompleteTextView(android.content.Context)` |  |
| `MultiAutoCompleteTextView(android.content.Context, android.util.AttributeSet)` |  |
| `MultiAutoCompleteTextView(android.content.Context, android.util.AttributeSet, int)` |  |
| `MultiAutoCompleteTextView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void performFiltering(CharSequence, int, int, int)` |  |
| `void setTokenizer(android.widget.MultiAutoCompleteTextView.Tokenizer)` |  |

---

### `class static MultiAutoCompleteTextView.CommaTokenizer`

- **实现：** `android.widget.MultiAutoCompleteTextView.Tokenizer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MultiAutoCompleteTextView.CommaTokenizer()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int findTokenEnd(CharSequence, int)` |  |
| `int findTokenStart(CharSequence, int)` |  |
| `CharSequence terminateToken(CharSequence)` |  |

---

### `interface static MultiAutoCompleteTextView.Tokenizer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int findTokenEnd(CharSequence, int)` |  |
| `int findTokenStart(CharSequence, int)` |  |
| `CharSequence terminateToken(CharSequence)` |  |

---

### `class NumberPicker`

- **继承：** `android.widget.LinearLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NumberPicker(android.content.Context)` |  |
| `NumberPicker(android.content.Context, android.util.AttributeSet)` |  |
| `NumberPicker(android.content.Context, android.util.AttributeSet, int)` |  |
| `NumberPicker(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String[] getDisplayedValues()` |  |
| `int getMaxValue()` |  |
| `int getMinValue()` |  |
| `int getValue()` |  |
| `boolean getWrapSelectorWheel()` |  |
| `void setDisplayedValues(String[])` |  |
| `void setFormatter(android.widget.NumberPicker.Formatter)` |  |
| `void setMaxValue(int)` |  |
| `void setMinValue(int)` |  |
| `void setOnLongPressUpdateInterval(long)` |  |
| `void setOnScrollListener(android.widget.NumberPicker.OnScrollListener)` |  |
| `void setOnValueChangedListener(android.widget.NumberPicker.OnValueChangeListener)` |  |
| `void setSelectionDividerHeight(@IntRange(from=0) @Px int)` |  |
| `void setTextColor(@ColorInt int)` |  |
| `void setTextSize(@FloatRange(from=0.0, fromInclusive=false) float)` |  |
| `void setValue(int)` |  |
| `void setWrapSelectorWheel(boolean)` |  |

---

### `interface static NumberPicker.Formatter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String format(int)` |  |

---

### `interface static NumberPicker.OnScrollListener`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SCROLL_STATE_FLING = 2` |  |
| `static final int SCROLL_STATE_IDLE = 0` |  |
| `static final int SCROLL_STATE_TOUCH_SCROLL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onScrollStateChange(android.widget.NumberPicker, int)` |  |

---

### `interface static NumberPicker.OnValueChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onValueChange(android.widget.NumberPicker, int, int)` |  |

---

### `class OverScroller`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `OverScroller(android.content.Context)` |  |
| `OverScroller(android.content.Context, android.view.animation.Interpolator)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void abortAnimation()` |  |
| `boolean computeScrollOffset()` |  |
| `void fling(int, int, int, int, int, int, int, int)` |  |
| `void fling(int, int, int, int, int, int, int, int, int, int)` |  |
| `final void forceFinished(boolean)` |  |
| `float getCurrVelocity()` |  |
| `final int getCurrX()` |  |
| `final int getCurrY()` |  |
| `final int getFinalX()` |  |
| `final int getFinalY()` |  |
| `final int getStartX()` |  |
| `final int getStartY()` |  |
| `final boolean isFinished()` |  |
| `boolean isOverScrolled()` |  |
| `void notifyHorizontalEdgeReached(int, int, int)` |  |
| `void notifyVerticalEdgeReached(int, int, int)` |  |
| `final void setFriction(float)` |  |
| `boolean springBack(int, int, int, int, int, int)` |  |
| `void startScroll(int, int, int, int)` |  |
| `void startScroll(int, int, int, int, int)` |  |

---

### `class PopupMenu`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PopupMenu(android.content.Context, android.view.View)` |  |
| `PopupMenu(android.content.Context, android.view.View, int)` |  |
| `PopupMenu(android.content.Context, android.view.View, int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dismiss()` |  |
| `android.view.View.OnTouchListener getDragToOpenListener()` |  |
| `int getGravity()` |  |
| `android.view.Menu getMenu()` |  |
| `android.view.MenuInflater getMenuInflater()` |  |
| `void inflate(@MenuRes int)` |  |
| `void setForceShowIcon(boolean)` |  |
| `void setGravity(int)` |  |
| `void setOnDismissListener(android.widget.PopupMenu.OnDismissListener)` |  |
| `void setOnMenuItemClickListener(android.widget.PopupMenu.OnMenuItemClickListener)` |  |
| `void show()` |  |

---

### `interface static PopupMenu.OnDismissListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDismiss(android.widget.PopupMenu)` |  |

---

### `interface static PopupMenu.OnMenuItemClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onMenuItemClick(android.view.MenuItem)` |  |

---

### `class PopupWindow`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PopupWindow(android.content.Context)` |  |
| `PopupWindow(android.content.Context, android.util.AttributeSet)` |  |
| `PopupWindow(android.content.Context, android.util.AttributeSet, int)` |  |
| `PopupWindow(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `PopupWindow()` |  |
| `PopupWindow(android.view.View)` |  |
| `PopupWindow(int, int)` |  |
| `PopupWindow(android.view.View, int, int)` |  |
| `PopupWindow(android.view.View, int, int, boolean)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INPUT_METHOD_FROM_FOCUSABLE = 0` |  |
| `static final int INPUT_METHOD_NEEDED = 1` |  |
| `static final int INPUT_METHOD_NOT_NEEDED = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dismiss()` |  |
| `int getAnimationStyle()` |  |
| `android.graphics.drawable.Drawable getBackground()` |  |
| `android.view.View getContentView()` |  |
| `float getElevation()` |  |
| `int getHeight()` |  |
| `int getInputMethodMode()` |  |
| `int getMaxAvailableHeight(@NonNull android.view.View)` |  |
| `int getMaxAvailableHeight(@NonNull android.view.View, int)` |  |
| `int getMaxAvailableHeight(@NonNull android.view.View, int, boolean)` |  |
| `boolean getOverlapAnchor()` |  |
| `int getSoftInputMode()` |  |
| `int getWidth()` |  |
| `int getWindowLayoutType()` |  |
| `boolean isAboveAnchor()` |  |
| `boolean isAttachedInDecor()` |  |
| `boolean isClippedToScreen()` |  |
| `boolean isClippingEnabled()` |  |
| `boolean isFocusable()` |  |
| `boolean isLaidOutInScreen()` |  |
| `boolean isOutsideTouchable()` |  |
| `boolean isShowing()` |  |
| `boolean isSplitTouchEnabled()` |  |
| `boolean isTouchModal()` |  |
| `boolean isTouchable()` |  |
| `void setAnimationStyle(int)` |  |
| `void setAttachedInDecor(boolean)` |  |
| `void setBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setClippingEnabled(boolean)` |  |
| `void setContentView(android.view.View)` |  |
| `void setElevation(float)` |  |
| `void setEnterTransition(@Nullable android.transition.Transition)` |  |
| `void setEpicenterBounds(@Nullable android.graphics.Rect)` |  |
| `void setExitTransition(@Nullable android.transition.Transition)` |  |
| `void setFocusable(boolean)` |  |
| `void setHeight(int)` |  |
| `void setIgnoreCheekPress()` |  |
| `void setInputMethodMode(int)` |  |
| `void setIsClippedToScreen(boolean)` |  |
| `void setIsLaidOutInScreen(boolean)` |  |
| `void setOnDismissListener(android.widget.PopupWindow.OnDismissListener)` |  |
| `void setOutsideTouchable(boolean)` |  |
| `void setOverlapAnchor(boolean)` |  |
| `void setSoftInputMode(int)` |  |
| `void setSplitTouchEnabled(boolean)` |  |
| `void setTouchInterceptor(android.view.View.OnTouchListener)` |  |
| `void setTouchModal(boolean)` |  |
| `void setTouchable(boolean)` |  |
| `void setWidth(int)` |  |
| `void setWindowLayoutType(int)` |  |
| `void showAsDropDown(android.view.View)` |  |
| `void showAsDropDown(android.view.View, int, int)` |  |
| `void showAsDropDown(android.view.View, int, int, int)` |  |
| `void showAtLocation(android.view.View, int, int, int)` |  |
| `void update()` |  |
| `void update(int, int)` |  |
| `void update(int, int, int, int)` |  |
| `void update(int, int, int, int, boolean)` |  |
| `void update(android.view.View, int, int)` |  |
| `void update(android.view.View, int, int, int, int)` |  |

---

### `interface static PopupWindow.OnDismissListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDismiss()` |  |

---

### `class ProgressBar`

- **继承：** `android.view.View`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ProgressBar(android.content.Context)` |  |
| `ProgressBar(android.content.Context, android.util.AttributeSet)` |  |
| `ProgressBar(android.content.Context, android.util.AttributeSet, int)` |  |
| `ProgressBar(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.Drawable getIndeterminateDrawable()` |  |
| `android.view.animation.Interpolator getInterpolator()` |  |
| `android.graphics.drawable.Drawable getProgressDrawable()` |  |
| `final void incrementProgressBy(int)` |  |
| `final void incrementSecondaryProgressBy(int)` |  |
| `boolean isAnimating()` |  |
| `void onRestoreInstanceState(android.os.Parcelable)` |  |
| `android.os.Parcelable onSaveInstanceState()` |  |
| `void setIndeterminate(boolean)` |  |
| `void setIndeterminateDrawable(android.graphics.drawable.Drawable)` |  |
| `void setIndeterminateDrawableTiled(android.graphics.drawable.Drawable)` |  |
| `void setIndeterminateTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setIndeterminateTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setIndeterminateTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setInterpolator(android.content.Context, @InterpolatorRes int)` |  |
| `void setInterpolator(android.view.animation.Interpolator)` |  |
| `void setMax(int)` |  |
| `void setMaxHeight(@Px int)` |  |
| `void setMaxWidth(@Px int)` |  |
| `void setMin(int)` |  |
| `void setMinHeight(@Px int)` |  |
| `void setMinWidth(@Px int)` |  |
| `void setProgress(int)` |  |
| `void setProgress(int, boolean)` |  |
| `void setProgressBackgroundTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setProgressBackgroundTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setProgressBackgroundTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setProgressDrawable(android.graphics.drawable.Drawable)` |  |
| `void setProgressDrawableTiled(android.graphics.drawable.Drawable)` |  |
| `void setProgressTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setProgressTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setProgressTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setSecondaryProgress(int)` |  |
| `void setSecondaryProgressTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setSecondaryProgressTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setSecondaryProgressTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |

---

### `class QuickContactBadge`

- **继承：** `android.widget.ImageView`
- **实现：** `android.view.View.OnClickListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `QuickContactBadge(android.content.Context)` |  |
| `QuickContactBadge(android.content.Context, android.util.AttributeSet)` |  |
| `QuickContactBadge(android.content.Context, android.util.AttributeSet, int)` |  |
| `QuickContactBadge(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String[] mExcludeMimes` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void assignContactFromEmail(String, boolean)` |  |
| `void assignContactFromEmail(String, boolean, android.os.Bundle)` |  |
| `void assignContactFromPhone(String, boolean)` |  |
| `void assignContactFromPhone(String, boolean, android.os.Bundle)` |  |
| `void assignContactUri(android.net.Uri)` |  |
| `void onClick(android.view.View)` |  |
| `void setExcludeMimes(String[])` |  |
| `void setImageToDefault()` |  |
| `void setMode(int)` |  |
| `void setOverlay(android.graphics.drawable.Drawable)` |  |
| `void setPrioritizedMimeType(String)` |  |

---

### `class RadioButton`

- **继承：** `android.widget.CompoundButton`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RadioButton(android.content.Context)` |  |
| `RadioButton(android.content.Context, android.util.AttributeSet)` |  |
| `RadioButton(android.content.Context, android.util.AttributeSet, int)` |  |
| `RadioButton(android.content.Context, android.util.AttributeSet, int, int)` |  |

---

### `class RadioGroup`

- **继承：** `android.widget.LinearLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RadioGroup(android.content.Context)` |  |
| `RadioGroup(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void check(@IdRes int)` |  |
| `void clearCheck()` |  |
| `android.widget.RadioGroup.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `void setOnCheckedChangeListener(android.widget.RadioGroup.OnCheckedChangeListener)` |  |

---

### `class static RadioGroup.LayoutParams`

- **继承：** `android.widget.LinearLayout.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RadioGroup.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `RadioGroup.LayoutParams(int, int)` |  |
| `RadioGroup.LayoutParams(int, int, float)` |  |
| `RadioGroup.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `RadioGroup.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |

---

### `interface static RadioGroup.OnCheckedChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCheckedChanged(android.widget.RadioGroup, @IdRes int)` |  |

---

### `class RatingBar`

- **继承：** `android.widget.AbsSeekBar`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RatingBar(android.content.Context, android.util.AttributeSet, int)` |  |
| `RatingBar(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `RatingBar(android.content.Context, android.util.AttributeSet)` |  |
| `RatingBar(android.content.Context)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getNumStars()` |  |
| `android.widget.RatingBar.OnRatingBarChangeListener getOnRatingBarChangeListener()` |  |
| `float getRating()` |  |
| `float getStepSize()` |  |
| `boolean isIndicator()` |  |
| `void setIsIndicator(boolean)` |  |
| `void setNumStars(int)` |  |
| `void setOnRatingBarChangeListener(android.widget.RatingBar.OnRatingBarChangeListener)` |  |
| `void setRating(float)` |  |
| `void setStepSize(float)` |  |

---

### `interface static RatingBar.OnRatingBarChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onRatingChanged(android.widget.RatingBar, float, boolean)` |  |

---

### `class RelativeLayout`

- **继承：** `android.view.ViewGroup`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RelativeLayout(android.content.Context)` |  |
| `RelativeLayout(android.content.Context, android.util.AttributeSet)` |  |
| `RelativeLayout(android.content.Context, android.util.AttributeSet, int)` |  |
| `RelativeLayout(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ABOVE = 2` |  |
| `static final int ALIGN_BASELINE = 4` |  |
| `static final int ALIGN_BOTTOM = 8` |  |
| `static final int ALIGN_END = 19` |  |
| `static final int ALIGN_LEFT = 5` |  |
| `static final int ALIGN_PARENT_BOTTOM = 12` |  |
| `static final int ALIGN_PARENT_END = 21` |  |
| `static final int ALIGN_PARENT_LEFT = 9` |  |
| `static final int ALIGN_PARENT_RIGHT = 11` |  |
| `static final int ALIGN_PARENT_START = 20` |  |
| `static final int ALIGN_PARENT_TOP = 10` |  |
| `static final int ALIGN_RIGHT = 7` |  |
| `static final int ALIGN_START = 18` |  |
| `static final int ALIGN_TOP = 6` |  |
| `static final int BELOW = 3` |  |
| `static final int CENTER_HORIZONTAL = 14` |  |
| `static final int CENTER_IN_PARENT = 13` |  |
| `static final int CENTER_VERTICAL = 15` |  |
| `static final int END_OF = 17` |  |
| `static final int LEFT_OF = 0` |  |
| `static final int RIGHT_OF = 1` |  |
| `static final int START_OF = 16` |  |
| `static final int TRUE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.RelativeLayout.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `int getGravity()` |  |
| `int getIgnoreGravity()` |  |
| `void setGravity(int)` |  |
| `void setHorizontalGravity(int)` |  |
| `void setIgnoreGravity(int)` |  |
| `void setVerticalGravity(int)` |  |

---

### `class static RelativeLayout.LayoutParams`

- **继承：** `android.view.ViewGroup.MarginLayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RelativeLayout.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `RelativeLayout.LayoutParams(int, int)` |  |
| `RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `RelativeLayout.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |
| `RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addRule(int)` |  |
| `void addRule(int, int)` |  |
| `String debug(String)` |  |
| `int getRule(int)` |  |
| `int[] getRules()` |  |
| `void removeRule(int)` |  |

---

### `class RemoteViews`

- **实现：** `android.view.LayoutInflater.Filter android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteViews(String, int)` |  |
| `RemoteViews(android.widget.RemoteViews, android.widget.RemoteViews)` |  |
| `RemoteViews(android.widget.RemoteViews)` |  |
| `RemoteViews(android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_SHARED_ELEMENT_BOUNDS = "android.widget.extra.SHARED_ELEMENT_BOUNDS"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addView(int, android.widget.RemoteViews)` |  |
| `android.view.View apply(android.content.Context, android.view.ViewGroup)` |  |
| `int describeContents()` |  |
| `int getLayoutId()` |  |
| `String getPackage()` |  |
| `void reapply(android.content.Context, android.view.View)` |  |
| `void removeAllViews(int)` |  |
| `void setAccessibilityTraversalAfter(int, int)` |  |
| `void setAccessibilityTraversalBefore(int, int)` |  |
| `void setBitmap(int, String, android.graphics.Bitmap)` |  |
| `void setBoolean(int, String, boolean)` |  |
| `void setBundle(int, String, android.os.Bundle)` |  |
| `void setByte(int, String, byte)` |  |
| `void setChar(int, String, char)` |  |
| `void setCharSequence(int, String, CharSequence)` |  |
| `void setChronometer(int, long, String, boolean)` |  |
| `void setChronometerCountDown(int, boolean)` |  |
| `void setContentDescription(int, CharSequence)` |  |
| `void setDisplayedChild(int, int)` |  |
| `void setDouble(int, String, double)` |  |
| `void setEmptyView(int, int)` |  |
| `void setFloat(int, String, float)` |  |
| `void setIcon(int, String, android.graphics.drawable.Icon)` |  |
| `void setImageViewBitmap(int, android.graphics.Bitmap)` |  |
| `void setImageViewIcon(int, android.graphics.drawable.Icon)` |  |
| `void setImageViewResource(int, int)` |  |
| `void setImageViewUri(int, android.net.Uri)` |  |
| `void setInt(int, String, int)` |  |
| `void setIntent(int, String, android.content.Intent)` |  |
| `void setLabelFor(int, int)` |  |
| `void setLightBackgroundLayoutId(@LayoutRes int)` |  |
| `void setLong(int, String, long)` |  |
| `void setOnClickFillInIntent(int, android.content.Intent)` |  |
| `void setOnClickPendingIntent(int, android.app.PendingIntent)` |  |
| `void setOnClickResponse(int, @NonNull android.widget.RemoteViews.RemoteResponse)` |  |
| `void setPendingIntentTemplate(int, android.app.PendingIntent)` |  |
| `void setProgressBar(int, int, int, boolean)` |  |
| `void setRelativeScrollPosition(int, int)` |  |
| `void setRemoteAdapter(int, android.content.Intent)` |  |
| `void setScrollPosition(int, int)` |  |
| `void setShort(int, String, short)` |  |
| `void setString(int, String, String)` |  |
| `void setTextColor(int, @ColorInt int)` |  |
| `void setTextViewCompoundDrawables(int, int, int, int, int)` |  |
| `void setTextViewCompoundDrawablesRelative(int, int, int, int, int)` |  |
| `void setTextViewText(int, CharSequence)` |  |
| `void setTextViewTextSize(int, int, float)` |  |
| `void setUri(int, String, android.net.Uri)` |  |
| `void setViewPadding(int, int, int, int, int)` |  |
| `void setViewVisibility(int, int)` |  |
| `void showNext(int)` |  |
| `void showPrevious(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static RemoteViews.ActionException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteViews.ActionException(Exception)` |  |
| `RemoteViews.ActionException(String)` |  |

---

### `class static RemoteViews.RemoteResponse`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteViews.RemoteResponse()` |  |

---

### `class abstract RemoteViewsService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RemoteViewsService()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract android.widget.RemoteViewsService.RemoteViewsFactory onGetViewFactory(android.content.Intent)` |  |

---

### `interface static RemoteViewsService.RemoteViewsFactory`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCount()` |  |
| `long getItemId(int)` |  |
| `android.widget.RemoteViews getLoadingView()` |  |
| `android.widget.RemoteViews getViewAt(int)` |  |
| `int getViewTypeCount()` |  |
| `boolean hasStableIds()` |  |
| `void onCreate()` |  |
| `void onDataSetChanged()` |  |
| `void onDestroy()` |  |

---

### `class abstract ResourceCursorAdapter`

- **继承：** `android.widget.CursorAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ResourceCursorAdapter(android.content.Context, int, android.database.Cursor, boolean)` |  |
| `ResourceCursorAdapter(android.content.Context, int, android.database.Cursor, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)` |  |
| `void setDropDownViewResource(int)` |  |
| `void setViewResource(int)` |  |

---

### `class abstract ResourceCursorTreeAdapter`

- **继承：** `android.widget.CursorTreeAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ResourceCursorTreeAdapter(android.content.Context, android.database.Cursor, int, int, int, int)` |  |
| `ResourceCursorTreeAdapter(android.content.Context, android.database.Cursor, int, int, int)` |  |
| `ResourceCursorTreeAdapter(android.content.Context, android.database.Cursor, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View newChildView(android.content.Context, android.database.Cursor, boolean, android.view.ViewGroup)` |  |
| `android.view.View newGroupView(android.content.Context, android.database.Cursor, boolean, android.view.ViewGroup)` |  |

---

### `class ScrollView`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScrollView(android.content.Context)` |  |
| `ScrollView(android.content.Context, android.util.AttributeSet)` |  |
| `ScrollView(android.content.Context, android.util.AttributeSet, int)` |  |
| `ScrollView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean arrowScroll(int)` |  |
| `int computeScrollDeltaToGetChildRectOnScreen(android.graphics.Rect)` |  |
| `boolean executeKeyEvent(android.view.KeyEvent)` |  |
| `void fling(int)` |  |
| `boolean fullScroll(int)` |  |
| `int getMaxScrollAmount()` |  |
| `boolean isFillViewport()` |  |
| `boolean isSmoothScrollingEnabled()` |  |
| `boolean pageScroll(int)` |  |
| `void scrollToDescendant(@NonNull android.view.View)` |  |
| `void setBottomEdgeEffectColor(@ColorInt int)` |  |
| `void setEdgeEffectColor(@ColorInt int)` |  |
| `void setFillViewport(boolean)` |  |
| `void setSmoothScrollingEnabled(boolean)` |  |
| `void setTopEdgeEffectColor(@ColorInt int)` |  |
| `final void smoothScrollBy(int, int)` |  |
| `final void smoothScrollTo(int, int)` |  |

---

### `class Scroller`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Scroller(android.content.Context)` |  |
| `Scroller(android.content.Context, android.view.animation.Interpolator)` |  |
| `Scroller(android.content.Context, android.view.animation.Interpolator, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void abortAnimation()` |  |
| `boolean computeScrollOffset()` |  |
| `void extendDuration(int)` |  |
| `void fling(int, int, int, int, int, int, int, int)` |  |
| `final void forceFinished(boolean)` |  |
| `float getCurrVelocity()` |  |
| `final int getCurrX()` |  |
| `final int getCurrY()` |  |
| `final int getDuration()` |  |
| `final int getFinalX()` |  |
| `final int getFinalY()` |  |
| `final int getStartX()` |  |
| `final int getStartY()` |  |
| `final boolean isFinished()` |  |
| `void setFinalX(int)` |  |
| `void setFinalY(int)` |  |
| `final void setFriction(float)` |  |
| `void startScroll(int, int, int, int)` |  |
| `void startScroll(int, int, int, int, int)` |  |
| `int timePassed()` |  |

---

### `class SearchView`

- **继承：** `android.widget.LinearLayout`
- **实现：** `android.view.CollapsibleActionView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SearchView(android.content.Context)` |  |
| `SearchView(android.content.Context, android.util.AttributeSet)` |  |
| `SearchView(android.content.Context, android.util.AttributeSet, int)` |  |
| `SearchView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getImeOptions()` |  |
| `int getInputType()` |  |
| `int getMaxWidth()` |  |
| `CharSequence getQuery()` |  |
| `android.widget.CursorAdapter getSuggestionsAdapter()` |  |
| `boolean isIconified()` |  |
| `boolean isIconifiedByDefault()` |  |
| `boolean isQueryRefinementEnabled()` |  |
| `boolean isSubmitButtonEnabled()` |  |
| `void onActionViewCollapsed()` |  |
| `void onActionViewExpanded()` |  |
| `void setIconified(boolean)` |  |
| `void setIconifiedByDefault(boolean)` |  |
| `void setImeOptions(int)` |  |
| `void setInputType(int)` |  |
| `void setMaxWidth(int)` |  |
| `void setOnCloseListener(android.widget.SearchView.OnCloseListener)` |  |
| `void setOnQueryTextFocusChangeListener(android.view.View.OnFocusChangeListener)` |  |
| `void setOnQueryTextListener(android.widget.SearchView.OnQueryTextListener)` |  |
| `void setOnSearchClickListener(android.view.View.OnClickListener)` |  |
| `void setOnSuggestionListener(android.widget.SearchView.OnSuggestionListener)` |  |
| `void setQuery(CharSequence, boolean)` |  |
| `void setQueryHint(@Nullable CharSequence)` |  |
| `void setQueryRefinementEnabled(boolean)` |  |
| `void setSearchableInfo(android.app.SearchableInfo)` |  |
| `void setSubmitButtonEnabled(boolean)` |  |
| `void setSuggestionsAdapter(android.widget.CursorAdapter)` |  |

---

### `interface static SearchView.OnCloseListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onClose()` |  |

---

### `interface static SearchView.OnQueryTextListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onQueryTextChange(String)` |  |
| `boolean onQueryTextSubmit(String)` |  |

---

### `interface static SearchView.OnSuggestionListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onSuggestionClick(int)` |  |
| `boolean onSuggestionSelect(int)` |  |

---

### `interface SectionIndexer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getPositionForSection(int)` |  |
| `int getSectionForPosition(int)` |  |
| `Object[] getSections()` |  |

---

### `class SeekBar`

- **继承：** `android.widget.AbsSeekBar`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SeekBar(android.content.Context)` |  |
| `SeekBar(android.content.Context, android.util.AttributeSet)` |  |
| `SeekBar(android.content.Context, android.util.AttributeSet, int)` |  |
| `SeekBar(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setOnSeekBarChangeListener(android.widget.SeekBar.OnSeekBarChangeListener)` |  |

---

### `interface static SeekBar.OnSeekBarChangeListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onProgressChanged(android.widget.SeekBar, int, boolean)` |  |
| `void onStartTrackingTouch(android.widget.SeekBar)` |  |
| `void onStopTrackingTouch(android.widget.SeekBar)` |  |

---

### `class ShareActionProvider`

- **继承：** `android.view.ActionProvider`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ShareActionProvider(android.content.Context)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFAULT_SHARE_HISTORY_FILE_NAME = "share_history.xml"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View onCreateActionView()` |  |
| `void setOnShareTargetSelectedListener(android.widget.ShareActionProvider.OnShareTargetSelectedListener)` |  |
| `void setShareHistoryFileName(String)` |  |
| `void setShareIntent(android.content.Intent)` |  |

---

### `interface static ShareActionProvider.OnShareTargetSelectedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onShareTargetSelected(android.widget.ShareActionProvider, android.content.Intent)` |  |

---

### `class SimpleAdapter`

- **继承：** `android.widget.BaseAdapter`
- **实现：** `android.widget.Filterable android.widget.ThemedSpinnerAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SimpleAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String,?>>, @LayoutRes int, String[], @IdRes int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCount()` |  |
| `android.content.res.Resources.Theme getDropDownViewTheme()` |  |
| `android.widget.Filter getFilter()` |  |
| `Object getItem(int)` |  |
| `long getItemId(int)` |  |
| `android.view.View getView(int, android.view.View, android.view.ViewGroup)` |  |
| `android.widget.SimpleAdapter.ViewBinder getViewBinder()` |  |
| `void setDropDownViewResource(int)` |  |
| `void setDropDownViewTheme(android.content.res.Resources.Theme)` |  |
| `void setViewBinder(android.widget.SimpleAdapter.ViewBinder)` |  |
| `void setViewImage(android.widget.ImageView, int)` |  |
| `void setViewImage(android.widget.ImageView, String)` |  |
| `void setViewText(android.widget.TextView, String)` |  |

---

### `interface static SimpleAdapter.ViewBinder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean setViewValue(android.view.View, Object, String)` |  |

---

### `class SimpleCursorAdapter`

- **继承：** `android.widget.ResourceCursorAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SimpleCursorAdapter(android.content.Context, int, android.database.Cursor, String[], int[], int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void bindView(android.view.View, android.content.Context, android.database.Cursor)` |  |
| `void changeCursorAndColumns(android.database.Cursor, String[], int[])` |  |
| `android.widget.SimpleCursorAdapter.CursorToStringConverter getCursorToStringConverter()` |  |
| `int getStringConversionColumn()` |  |
| `android.widget.SimpleCursorAdapter.ViewBinder getViewBinder()` |  |
| `void setCursorToStringConverter(android.widget.SimpleCursorAdapter.CursorToStringConverter)` |  |
| `void setStringConversionColumn(int)` |  |
| `void setViewBinder(android.widget.SimpleCursorAdapter.ViewBinder)` |  |
| `void setViewImage(android.widget.ImageView, String)` |  |
| `void setViewText(android.widget.TextView, String)` |  |

---

### `interface static SimpleCursorAdapter.CursorToStringConverter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence convertToString(android.database.Cursor)` |  |

---

### `interface static SimpleCursorAdapter.ViewBinder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean setViewValue(android.view.View, android.database.Cursor, int)` |  |

---

### `class abstract SimpleCursorTreeAdapter`

- **继承：** `android.widget.ResourceCursorTreeAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SimpleCursorTreeAdapter(android.content.Context, android.database.Cursor, int, int, String[], int[], int, int, String[], int[])` |  |
| `SimpleCursorTreeAdapter(android.content.Context, android.database.Cursor, int, int, String[], int[], int, String[], int[])` |  |
| `SimpleCursorTreeAdapter(android.content.Context, android.database.Cursor, int, String[], int[], int, String[], int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void bindChildView(android.view.View, android.content.Context, android.database.Cursor, boolean)` |  |
| `void bindGroupView(android.view.View, android.content.Context, android.database.Cursor, boolean)` |  |
| `android.widget.SimpleCursorTreeAdapter.ViewBinder getViewBinder()` |  |
| `void setViewBinder(android.widget.SimpleCursorTreeAdapter.ViewBinder)` |  |
| `void setViewImage(android.widget.ImageView, String)` |  |
| `void setViewText(android.widget.TextView, String)` |  |

---

### `interface static SimpleCursorTreeAdapter.ViewBinder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean setViewValue(android.view.View, android.database.Cursor, int)` |  |

---

### `class SimpleExpandableListAdapter`

- **继承：** `android.widget.BaseExpandableListAdapter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SimpleExpandableListAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String,?>>, int, String[], int[], java.util.List<? extends java.util.List<? extends java.util.Map<java.lang.String,?>>>, int, String[], int[])` |  |
| `SimpleExpandableListAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String,?>>, int, int, String[], int[], java.util.List<? extends java.util.List<? extends java.util.Map<java.lang.String,?>>>, int, String[], int[])` |  |
| `SimpleExpandableListAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String,?>>, int, int, String[], int[], java.util.List<? extends java.util.List<? extends java.util.Map<java.lang.String,?>>>, int, int, String[], int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object getChild(int, int)` |  |
| `long getChildId(int, int)` |  |
| `android.view.View getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `int getChildrenCount(int)` |  |
| `Object getGroup(int)` |  |
| `int getGroupCount()` |  |
| `long getGroupId(int)` |  |
| `android.view.View getGroupView(int, boolean, android.view.View, android.view.ViewGroup)` |  |
| `boolean hasStableIds()` |  |
| `boolean isChildSelectable(int, int)` |  |
| `android.view.View newChildView(boolean, android.view.ViewGroup)` |  |
| `android.view.View newGroupView(boolean, android.view.ViewGroup)` |  |

---

### `class SlidingDrawer` ~~DEPRECATED~~

- **继承：** `android.view.ViewGroup`
- **注解：** `@Deprecated`

---

### `interface static SlidingDrawer.OnDrawerCloseListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static SlidingDrawer.OnDrawerOpenListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static SlidingDrawer.OnDrawerScrollListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final Space`

- **继承：** `android.view.View`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Space(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `Space(android.content.Context, android.util.AttributeSet, int)` |  |
| `Space(android.content.Context, android.util.AttributeSet)` |  |
| `Space(android.content.Context)` |  |

---

### `class Spinner`

- **继承：** `android.widget.AbsSpinner`
- **实现：** `android.content.DialogInterface.OnClickListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Spinner(android.content.Context)` |  |
| `Spinner(android.content.Context, int)` |  |
| `Spinner(android.content.Context, android.util.AttributeSet)` |  |
| `Spinner(android.content.Context, android.util.AttributeSet, int)` |  |
| `Spinner(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `Spinner(android.content.Context, android.util.AttributeSet, int, int, int)` |  |
| `Spinner(android.content.Context, android.util.AttributeSet, int, int, int, android.content.res.Resources.Theme)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MODE_DIALOG = 0` |  |
| `static final int MODE_DROPDOWN = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getDropDownHorizontalOffset()` |  |
| `int getDropDownVerticalOffset()` |  |
| `int getDropDownWidth()` |  |
| `int getGravity()` |  |
| `android.graphics.drawable.Drawable getPopupBackground()` |  |
| `android.content.Context getPopupContext()` |  |
| `CharSequence getPrompt()` |  |
| `void onClick(android.content.DialogInterface, int)` |  |
| `void setDropDownHorizontalOffset(int)` |  |
| `void setDropDownVerticalOffset(int)` |  |
| `void setDropDownWidth(int)` |  |
| `void setGravity(int)` |  |
| `void setPopupBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setPopupBackgroundResource(@DrawableRes int)` |  |
| `void setPrompt(CharSequence)` |  |
| `void setPromptId(int)` |  |

---

### `interface SpinnerAdapter`

- **继承：** `android.widget.Adapter`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View getDropDownView(int, android.view.View, android.view.ViewGroup)` |  |

---

### `class StackView`

- **继承：** `android.widget.AdapterViewAnimator`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StackView(android.content.Context)` |  |
| `StackView(android.content.Context, android.util.AttributeSet)` |  |
| `StackView(android.content.Context, android.util.AttributeSet, int)` |  |
| `StackView(android.content.Context, android.util.AttributeSet, int, int)` |  |

---

### `class Switch`

- **继承：** `android.widget.CompoundButton`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Switch(android.content.Context)` |  |
| `Switch(android.content.Context, android.util.AttributeSet)` |  |
| `Switch(android.content.Context, android.util.AttributeSet, int)` |  |
| `Switch(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getShowText()` |  |
| `boolean getSplitTrack()` |  |
| `int getSwitchMinWidth()` |  |
| `int getSwitchPadding()` |  |
| `CharSequence getTextOff()` |  |
| `CharSequence getTextOn()` |  |
| `android.graphics.drawable.Drawable getThumbDrawable()` |  |
| `int getThumbTextPadding()` |  |
| `android.graphics.drawable.Drawable getTrackDrawable()` |  |
| `void onMeasure(int, int)` |  |
| `void setShowText(boolean)` |  |
| `void setSplitTrack(boolean)` |  |
| `void setSwitchMinWidth(int)` |  |
| `void setSwitchPadding(int)` |  |
| `void setSwitchTextAppearance(android.content.Context, @StyleRes int)` |  |
| `void setSwitchTypeface(android.graphics.Typeface, int)` |  |
| `void setSwitchTypeface(android.graphics.Typeface)` |  |
| `void setTextOff(CharSequence)` |  |
| `void setTextOn(CharSequence)` |  |
| `void setThumbDrawable(android.graphics.drawable.Drawable)` |  |
| `void setThumbResource(@DrawableRes int)` |  |
| `void setThumbTextPadding(int)` |  |
| `void setThumbTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setThumbTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setThumbTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setTrackDrawable(android.graphics.drawable.Drawable)` |  |
| `void setTrackResource(@DrawableRes int)` |  |
| `void setTrackTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setTrackTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setTrackTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |

---

### `class TabHost` ~~DEPRECATED~~

- **继承：** `android.widget.FrameLayout`
- **实现：** `android.view.ViewTreeObserver.OnTouchModeChangeListener`
- **注解：** `@Deprecated`

---

### `interface static TabHost.OnTabChangeListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static TabHost.TabContentFactory` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class TabHost.TabSpec` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class TabWidget` ~~DEPRECATED~~

- **继承：** `android.widget.LinearLayout`
- **实现：** `android.view.View.OnFocusChangeListener`
- **注解：** `@Deprecated`

---

### `class TableLayout`

- **继承：** `android.widget.LinearLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TableLayout(android.content.Context)` |  |
| `TableLayout(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.TableLayout.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `boolean isColumnCollapsed(int)` |  |
| `boolean isColumnShrinkable(int)` |  |
| `boolean isColumnStretchable(int)` |  |
| `boolean isShrinkAllColumns()` |  |
| `boolean isStretchAllColumns()` |  |
| `void setColumnCollapsed(int, boolean)` |  |
| `void setColumnShrinkable(int, boolean)` |  |
| `void setColumnStretchable(int, boolean)` |  |
| `void setShrinkAllColumns(boolean)` |  |
| `void setStretchAllColumns(boolean)` |  |

---

### `class static TableLayout.LayoutParams`

- **继承：** `android.widget.LinearLayout.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TableLayout.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `TableLayout.LayoutParams(int, int)` |  |
| `TableLayout.LayoutParams(int, int, float)` |  |
| `TableLayout.LayoutParams()` |  |
| `TableLayout.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `TableLayout.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |

---

### `class TableRow`

- **继承：** `android.widget.LinearLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TableRow(android.content.Context)` |  |
| `TableRow(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.TableRow.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.view.View getVirtualChildAt(int)` |  |
| `int getVirtualChildCount()` |  |

---

### `class static TableRow.LayoutParams`

- **继承：** `android.widget.LinearLayout.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TableRow.LayoutParams(android.content.Context, android.util.AttributeSet)` |  |
| `TableRow.LayoutParams(int, int)` |  |
| `TableRow.LayoutParams(int, int, float)` |  |
| `TableRow.LayoutParams()` |  |
| `TableRow.LayoutParams(int)` |  |
| `TableRow.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `TableRow.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |

---

### `class TextClock`

- **继承：** `android.widget.TextView`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextClock(android.content.Context)` |  |
| `TextClock(android.content.Context, android.util.AttributeSet)` |  |
| `TextClock(android.content.Context, android.util.AttributeSet, int)` |  |
| `TextClock(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getTimeZone()` |  |
| `boolean is24HourModeEnabled()` |  |
| `void refreshTime()` |  |
| `void setFormat12Hour(CharSequence)` |  |
| `void setFormat24Hour(CharSequence)` |  |
| `void setTimeZone(String)` |  |

---

### `class TextSwitcher`

- **继承：** `android.widget.ViewSwitcher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextSwitcher(android.content.Context)` |  |
| `TextSwitcher(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setCurrentText(CharSequence)` |  |
| `void setText(CharSequence)` |  |

---

### `class TextView`

- **继承：** `android.view.View`
- **实现：** `android.view.ViewTreeObserver.OnPreDrawListener`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextView(android.content.Context)` |  |
| `TextView(android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `TextView(android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `TextView(android.content.Context, @Nullable android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUTO_SIZE_TEXT_TYPE_NONE = 0` |  |
| `static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addTextChangedListener(android.text.TextWatcher)` |  |
| `final void append(CharSequence)` |  |
| `void append(CharSequence, int, int)` |  |
| `void beginBatchEdit()` |  |
| `boolean bringPointIntoView(int)` |  |
| `void clearComposingText()` |  |
| `void debug(int)` |  |
| `boolean didTouchFocusSelect()` |  |
| `void endBatchEdit()` |  |
| `boolean extractText(android.view.inputmethod.ExtractedTextRequest, android.view.inputmethod.ExtractedText)` |  |
| `final int getAutoLinkMask()` |  |
| `int getAutoSizeMaxTextSize()` |  |
| `int getAutoSizeMinTextSize()` |  |
| `int getAutoSizeStepGranularity()` |  |
| `int[] getAutoSizeTextAvailableSizes()` |  |
| `int getAutoSizeTextType()` |  |
| `int getBreakStrategy()` |  |
| `int getCompoundDrawablePadding()` |  |
| `android.content.res.ColorStateList getCompoundDrawableTintList()` |  |
| `android.graphics.PorterDuff.Mode getCompoundDrawableTintMode()` |  |
| `int getCompoundPaddingBottom()` |  |
| `int getCompoundPaddingEnd()` |  |
| `int getCompoundPaddingLeft()` |  |
| `int getCompoundPaddingRight()` |  |
| `int getCompoundPaddingStart()` |  |
| `int getCompoundPaddingTop()` |  |
| `android.view.ActionMode.Callback getCustomInsertionActionModeCallback()` |  |
| `android.view.ActionMode.Callback getCustomSelectionActionModeCallback()` |  |
| `boolean getDefaultEditable()` |  |
| `android.text.method.MovementMethod getDefaultMovementMethod()` |  |
| `android.text.Editable getEditableText()` |  |
| `CharSequence getError()` |  |
| `int getExtendedPaddingBottom()` |  |
| `int getExtendedPaddingTop()` |  |
| `android.text.InputFilter[] getFilters()` |  |
| `int getFirstBaselineToTopHeight()` |  |
| `boolean getFreezesText()` |  |
| `int getGravity()` |  |
| `final android.content.res.ColorStateList getHintTextColors()` |  |
| `int getHyphenationFrequency()` |  |
| `int getImeActionId()` |  |
| `CharSequence getImeActionLabel()` |  |
| `int getImeOptions()` |  |
| `boolean getIncludeFontPadding()` |  |
| `android.os.Bundle getInputExtras(boolean)` |  |
| `int getInputType()` |  |
| `int getJustificationMode()` |  |
| `final android.text.method.KeyListener getKeyListener()` |  |
| `int getLastBaselineToBottomHeight()` |  |
| `final android.text.Layout getLayout()` |  |
| `float getLetterSpacing()` |  |
| `int getLineBounds(int, android.graphics.Rect)` |  |
| `int getLineCount()` |  |
| `int getLineHeight()` |  |
| `float getLineSpacingExtra()` |  |
| `float getLineSpacingMultiplier()` |  |
| `final android.content.res.ColorStateList getLinkTextColors()` |  |
| `final boolean getLinksClickable()` |  |
| `int getMarqueeRepeatLimit()` |  |
| `int getMaxEms()` |  |
| `int getMaxHeight()` |  |
| `int getMaxLines()` |  |
| `int getMaxWidth()` |  |
| `int getMinEms()` |  |
| `int getMinHeight()` |  |
| `int getMinLines()` |  |
| `int getMinWidth()` |  |
| `final android.text.method.MovementMethod getMovementMethod()` |  |
| `int getOffsetForPosition(float, float)` |  |
| `android.text.TextPaint getPaint()` |  |
| `int getPaintFlags()` |  |
| `String getPrivateImeOptions()` |  |
| `float getShadowDx()` |  |
| `float getShadowDy()` |  |
| `float getShadowRadius()` |  |
| `final boolean getShowSoftInputOnFocus()` |  |
| `final android.content.res.ColorStateList getTextColors()` |  |
| `float getTextScaleX()` |  |
| `int getTextSizeUnit()` |  |
| `int getTotalPaddingBottom()` |  |
| `int getTotalPaddingEnd()` |  |
| `int getTotalPaddingLeft()` |  |
| `int getTotalPaddingRight()` |  |
| `int getTotalPaddingStart()` |  |
| `int getTotalPaddingTop()` |  |
| `final android.text.method.TransformationMethod getTransformationMethod()` |  |
| `android.graphics.Typeface getTypeface()` |  |
| `android.text.style.URLSpan[] getUrls()` |  |
| `boolean hasSelection()` |  |
| `boolean isAllCaps()` |  |
| `boolean isCursorVisible()` |  |
| `boolean isElegantTextHeight()` |  |
| `boolean isFallbackLineSpacing()` |  |
| `final boolean isHorizontallyScrollable()` |  |
| `boolean isInputMethodTarget()` |  |
| `boolean isSingleLine()` |  |
| `boolean isSuggestionsEnabled()` |  |
| `boolean isTextSelectable()` |  |
| `int length()` |  |
| `boolean moveCursorToVisibleOffset()` |  |
| `void onBeginBatchEdit()` |  |
| `void onCommitCompletion(android.view.inputmethod.CompletionInfo)` |  |
| `void onCommitCorrection(android.view.inputmethod.CorrectionInfo)` |  |
| `void onEditorAction(int)` |  |
| `void onEndBatchEdit()` |  |
| `boolean onPreDraw()` |  |
| `boolean onPrivateIMECommand(String, android.os.Bundle)` |  |
| `void onRestoreInstanceState(android.os.Parcelable)` |  |
| `android.os.Parcelable onSaveInstanceState()` |  |
| `void onTextChanged(CharSequence, int, int, int)` |  |
| `boolean onTextContextMenuItem(int)` |  |
| `void removeTextChangedListener(android.text.TextWatcher)` |  |
| `void setAllCaps(boolean)` |  |
| `final void setAutoLinkMask(int)` |  |
| `void setAutoSizeTextTypeUniformWithConfiguration(int, int, int, int)` |  |
| `void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[], int)` |  |
| `void setAutoSizeTextTypeWithDefaults(int)` |  |
| `void setBreakStrategy(int)` |  |
| `void setCompoundDrawablePadding(int)` |  |
| `void setCompoundDrawableTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setCompoundDrawableTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setCompoundDrawableTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `void setCompoundDrawables(@Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable)` |  |
| `void setCompoundDrawablesRelative(@Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable)` |  |
| `void setCompoundDrawablesRelativeWithIntrinsicBounds(@DrawableRes int, @DrawableRes int, @DrawableRes int, @DrawableRes int)` |  |
| `void setCompoundDrawablesRelativeWithIntrinsicBounds(@Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable)` |  |
| `void setCompoundDrawablesWithIntrinsicBounds(@DrawableRes int, @DrawableRes int, @DrawableRes int, @DrawableRes int)` |  |
| `void setCompoundDrawablesWithIntrinsicBounds(@Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable)` |  |
| `void setCursorVisible(boolean)` |  |
| `void setCustomInsertionActionModeCallback(android.view.ActionMode.Callback)` |  |
| `void setCustomSelectionActionModeCallback(android.view.ActionMode.Callback)` |  |
| `final void setEditableFactory(android.text.Editable.Factory)` |  |
| `void setElegantTextHeight(boolean)` |  |
| `void setEllipsize(android.text.TextUtils.TruncateAt)` |  |
| `void setEms(int)` |  |
| `void setError(CharSequence)` |  |
| `void setError(CharSequence, android.graphics.drawable.Drawable)` |  |
| `void setExtractedText(android.view.inputmethod.ExtractedText)` |  |
| `void setFallbackLineSpacing(boolean)` |  |
| `void setFilters(android.text.InputFilter[])` |  |
| `void setFirstBaselineToTopHeight(@IntRange(from=0) @Px int)` |  |
| `void setFontFeatureSettings(@Nullable String)` |  |
| `boolean setFontVariationSettings(@Nullable String)` |  |
| `boolean setFrame(int, int, int, int)` |  |
| `void setFreezesText(boolean)` |  |
| `void setGravity(int)` |  |
| `void setHeight(int)` |  |
| `void setHighlightColor(@ColorInt int)` |  |
| `final void setHint(CharSequence)` |  |
| `final void setHint(@StringRes int)` |  |
| `final void setHintTextColor(@ColorInt int)` |  |
| `final void setHintTextColor(android.content.res.ColorStateList)` |  |
| `void setHorizontallyScrolling(boolean)` |  |
| `void setHyphenationFrequency(int)` |  |
| `void setImeActionLabel(CharSequence, int)` |  |
| `void setImeHintLocales(@Nullable android.os.LocaleList)` |  |
| `void setImeOptions(int)` |  |
| `void setIncludeFontPadding(boolean)` |  |
| `void setInputExtras(@XmlRes int) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void setInputType(int)` |  |
| `void setJustificationMode(int)` |  |
| `void setKeyListener(android.text.method.KeyListener)` |  |
| `void setLastBaselineToBottomHeight(@IntRange(from=0) @Px int)` |  |
| `void setLetterSpacing(float)` |  |
| `void setLineHeight(@IntRange(from=0) @Px int)` |  |
| `void setLineSpacing(float, float)` |  |
| `void setLines(int)` |  |
| `final void setLinkTextColor(@ColorInt int)` |  |
| `final void setLinkTextColor(android.content.res.ColorStateList)` |  |
| `final void setLinksClickable(boolean)` |  |
| `void setMarqueeRepeatLimit(int)` |  |
| `void setMaxEms(int)` |  |
| `void setMaxHeight(int)` |  |
| `void setMaxLines(int)` |  |
| `void setMaxWidth(int)` |  |
| `void setMinEms(int)` |  |
| `void setMinHeight(int)` |  |
| `void setMinLines(int)` |  |
| `void setMinWidth(int)` |  |
| `final void setMovementMethod(android.text.method.MovementMethod)` |  |
| `void setOnEditorActionListener(android.widget.TextView.OnEditorActionListener)` |  |
| `void setPaintFlags(int)` |  |
| `void setPrivateImeOptions(String)` |  |
| `void setRawInputType(int)` |  |
| `void setScroller(android.widget.Scroller)` |  |
| `void setSelectAllOnFocus(boolean)` |  |
| `void setShadowLayer(float, float, float, int)` |  |
| `final void setShowSoftInputOnFocus(boolean)` |  |
| `void setSingleLine()` |  |
| `void setSingleLine(boolean)` |  |
| `final void setSpannableFactory(android.text.Spannable.Factory)` |  |
| `final void setText(CharSequence)` |  |
| `void setText(CharSequence, android.widget.TextView.BufferType)` |  |
| `final void setText(char[], int, int)` |  |
| `final void setText(@StringRes int)` |  |
| `final void setText(@StringRes int, android.widget.TextView.BufferType)` |  |
| `void setTextAppearance(@StyleRes int)` |  |
| `void setTextClassifier(@Nullable android.view.textclassifier.TextClassifier)` |  |
| `void setTextColor(@ColorInt int)` |  |
| `void setTextColor(android.content.res.ColorStateList)` |  |
| `void setTextCursorDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setTextCursorDrawable(@DrawableRes int)` |  |
| `void setTextIsSelectable(boolean)` |  |
| `final void setTextKeepState(CharSequence)` |  |
| `final void setTextKeepState(CharSequence, android.widget.TextView.BufferType)` |  |
| `void setTextLocale(@NonNull java.util.Locale)` |  |
| `void setTextLocales(@NonNull @Size(min=1) android.os.LocaleList)` |  |
| `void setTextMetricsParams(@NonNull android.text.PrecomputedText.Params)` |  |
| `void setTextScaleX(float)` |  |
| `void setTextSelectHandle(@NonNull android.graphics.drawable.Drawable)` |  |
| `void setTextSelectHandle(@DrawableRes int)` |  |
| `void setTextSelectHandleLeft(@NonNull android.graphics.drawable.Drawable)` |  |
| `void setTextSelectHandleLeft(@DrawableRes int)` |  |
| `void setTextSelectHandleRight(@NonNull android.graphics.drawable.Drawable)` |  |
| `void setTextSelectHandleRight(@DrawableRes int)` |  |
| `void setTextSize(float)` |  |
| `void setTextSize(int, float)` |  |
| `final void setTransformationMethod(android.text.method.TransformationMethod)` |  |
| `void setTypeface(@Nullable android.graphics.Typeface, int)` |  |
| `void setTypeface(@Nullable android.graphics.Typeface)` |  |
| `void setWidth(int)` |  |

---

### `enum TextView.BufferType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.widget.TextView.BufferType EDITABLE` |  |
| `static final android.widget.TextView.BufferType NORMAL` |  |
| `static final android.widget.TextView.BufferType SPANNABLE` |  |

---

### `interface static TextView.OnEditorActionListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onEditorAction(android.widget.TextView, int, android.view.KeyEvent)` |  |

---

### `class static TextView.SavedState`

- **继承：** `android.view.View.BaseSavedState`

---

### `interface ThemedSpinnerAdapter`

- **继承：** `android.widget.SpinnerAdapter`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setDropDownViewTheme(@Nullable android.content.res.Resources.Theme)` |  |

---

### `class TimePicker`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TimePicker(android.content.Context)` |  |
| `TimePicker(android.content.Context, android.util.AttributeSet)` |  |
| `TimePicker(android.content.Context, android.util.AttributeSet, int)` |  |
| `TimePicker(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getHour()` |  |
| `int getMinute()` |  |
| `boolean is24HourView()` |  |
| `void setHour(@IntRange(from=0, to=23) int)` |  |
| `void setIs24HourView(@NonNull Boolean)` |  |
| `void setMinute(@IntRange(from=0, to=59) int)` |  |
| `void setOnTimeChangedListener(android.widget.TimePicker.OnTimeChangedListener)` |  |
| `boolean validateInput()` |  |

---

### `interface static TimePicker.OnTimeChangedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onTimeChanged(android.widget.TimePicker, int, int)` |  |

---

### `class Toast`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Toast(android.content.Context)` |  |
| `Toast.Callback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int LENGTH_LONG = 1` |  |
| `static final int LENGTH_SHORT = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addCallback(@NonNull android.widget.Toast.Callback)` |  |
| `void cancel()` |  |
| `int getDuration()` |  |
| `int getGravity()` |  |
| `float getHorizontalMargin()` |  |
| `float getVerticalMargin()` |  |
| `int getXOffset()` |  |
| `int getYOffset()` |  |
| `static android.widget.Toast makeText(android.content.Context, CharSequence, int)` |  |
| `static android.widget.Toast makeText(android.content.Context, @StringRes int, int) throws android.content.res.Resources.NotFoundException` |  |
| `void removeCallback(@NonNull android.widget.Toast.Callback)` |  |
| `void setDuration(int)` |  |
| `void setGravity(int, int, int)` |  |
| `void setMargin(float, float)` |  |
| `void setText(@StringRes int)` |  |
| `void setText(CharSequence)` |  |
| `void show()` |  |
| `void onToastHidden()` |  |
| `void onToastShown()` |  |

---

### `class ToggleButton`

- **继承：** `android.widget.CompoundButton`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ToggleButton(android.content.Context, android.util.AttributeSet, int, int)` |  |
| `ToggleButton(android.content.Context, android.util.AttributeSet, int)` |  |
| `ToggleButton(android.content.Context, android.util.AttributeSet)` |  |
| `ToggleButton(android.content.Context)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence getTextOff()` |  |
| `CharSequence getTextOn()` |  |
| `void setBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setTextOff(CharSequence)` |  |
| `void setTextOn(CharSequence)` |  |

---

### `class Toolbar`

- **继承：** `android.view.ViewGroup`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Toolbar(android.content.Context)` |  |
| `Toolbar(android.content.Context, android.util.AttributeSet)` |  |
| `Toolbar(android.content.Context, android.util.AttributeSet, int)` |  |
| `Toolbar(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void collapseActionView()` |  |
| `void dismissPopupMenus()` |  |
| `android.widget.Toolbar.LayoutParams generateDefaultLayoutParams()` |  |
| `android.widget.Toolbar.LayoutParams generateLayoutParams(android.util.AttributeSet)` |  |
| `android.widget.Toolbar.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams)` |  |
| `int getContentInsetEnd()` |  |
| `int getContentInsetEndWithActions()` |  |
| `int getContentInsetLeft()` |  |
| `int getContentInsetRight()` |  |
| `int getContentInsetStart()` |  |
| `int getContentInsetStartWithNavigation()` |  |
| `int getCurrentContentInsetEnd()` |  |
| `int getCurrentContentInsetLeft()` |  |
| `int getCurrentContentInsetRight()` |  |
| `int getCurrentContentInsetStart()` |  |
| `android.graphics.drawable.Drawable getLogo()` |  |
| `CharSequence getLogoDescription()` |  |
| `android.view.Menu getMenu()` |  |
| `int getPopupTheme()` |  |
| `CharSequence getSubtitle()` |  |
| `CharSequence getTitle()` |  |
| `int getTitleMarginBottom()` |  |
| `int getTitleMarginEnd()` |  |
| `int getTitleMarginStart()` |  |
| `int getTitleMarginTop()` |  |
| `boolean hasExpandedActionView()` |  |
| `boolean hideOverflowMenu()` |  |
| `void inflateMenu(@MenuRes int)` |  |
| `boolean isOverflowMenuShowing()` |  |
| `void setCollapseContentDescription(@StringRes int)` |  |
| `void setCollapseContentDescription(@Nullable CharSequence)` |  |
| `void setCollapseIcon(@DrawableRes int)` |  |
| `void setCollapseIcon(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setContentInsetEndWithActions(int)` |  |
| `void setContentInsetStartWithNavigation(int)` |  |
| `void setContentInsetsAbsolute(int, int)` |  |
| `void setContentInsetsRelative(int, int)` |  |
| `void setLogo(@DrawableRes int)` |  |
| `void setLogo(android.graphics.drawable.Drawable)` |  |
| `void setLogoDescription(@StringRes int)` |  |
| `void setLogoDescription(CharSequence)` |  |
| `void setNavigationContentDescription(@StringRes int)` |  |
| `void setNavigationContentDescription(@Nullable CharSequence)` |  |
| `void setNavigationIcon(@DrawableRes int)` |  |
| `void setNavigationIcon(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setNavigationOnClickListener(android.view.View.OnClickListener)` |  |
| `void setOnMenuItemClickListener(android.widget.Toolbar.OnMenuItemClickListener)` |  |
| `void setOverflowIcon(@Nullable android.graphics.drawable.Drawable)` |  |
| `void setPopupTheme(@StyleRes int)` |  |
| `void setSubtitle(@StringRes int)` |  |
| `void setSubtitle(CharSequence)` |  |
| `void setSubtitleTextAppearance(android.content.Context, @StyleRes int)` |  |
| `void setSubtitleTextColor(@ColorInt int)` |  |
| `void setTitle(@StringRes int)` |  |
| `void setTitle(CharSequence)` |  |
| `void setTitleMargin(int, int, int, int)` |  |
| `void setTitleMarginBottom(int)` |  |
| `void setTitleMarginEnd(int)` |  |
| `void setTitleMarginStart(int)` |  |
| `void setTitleMarginTop(int)` |  |
| `void setTitleTextAppearance(android.content.Context, @StyleRes int)` |  |
| `void setTitleTextColor(@ColorInt int)` |  |
| `boolean showOverflowMenu()` |  |

---

### `class static Toolbar.LayoutParams`

- **继承：** `android.app.ActionBar.LayoutParams`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Toolbar.LayoutParams(@NonNull android.content.Context, android.util.AttributeSet)` |  |
| `Toolbar.LayoutParams(int, int)` |  |
| `Toolbar.LayoutParams(int, int, int)` |  |
| `Toolbar.LayoutParams(int)` |  |
| `Toolbar.LayoutParams(android.widget.Toolbar.LayoutParams)` |  |
| `Toolbar.LayoutParams(android.app.ActionBar.LayoutParams)` |  |
| `Toolbar.LayoutParams(android.view.ViewGroup.MarginLayoutParams)` |  |
| `Toolbar.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |

---

### `interface static Toolbar.OnMenuItemClickListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onMenuItemClick(android.view.MenuItem)` |  |

---

### `class TwoLineListItem` ~~DEPRECATED~~

- **继承：** `android.widget.RelativeLayout`
- **注解：** `@Deprecated`

---

### `class VideoView`

- **继承：** `android.view.SurfaceView`
- **实现：** `android.widget.MediaController.MediaPlayerControl`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VideoView(android.content.Context)` |  |
| `VideoView(android.content.Context, android.util.AttributeSet)` |  |
| `VideoView(android.content.Context, android.util.AttributeSet, int)` |  |
| `VideoView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addSubtitleSource(java.io.InputStream, android.media.MediaFormat)` |  |
| `boolean canPause()` |  |
| `boolean canSeekBackward()` |  |
| `boolean canSeekForward()` |  |
| `int getAudioSessionId()` |  |
| `int getBufferPercentage()` |  |
| `int getCurrentPosition()` |  |
| `int getDuration()` |  |
| `boolean isPlaying()` |  |
| `void pause()` |  |
| `int resolveAdjustedSize(int, int)` |  |
| `void resume()` |  |
| `void seekTo(int)` |  |
| `void setAudioAttributes(@NonNull android.media.AudioAttributes)` |  |
| `void setAudioFocusRequest(int)` |  |
| `void setMediaController(android.widget.MediaController)` |  |
| `void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener)` |  |
| `void setOnErrorListener(android.media.MediaPlayer.OnErrorListener)` |  |
| `void setOnInfoListener(android.media.MediaPlayer.OnInfoListener)` |  |
| `void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener)` |  |
| `void setVideoPath(String)` |  |
| `void setVideoURI(android.net.Uri)` |  |
| `void setVideoURI(android.net.Uri, java.util.Map<java.lang.String,java.lang.String>)` |  |
| `void start()` |  |
| `void stopPlayback()` |  |
| `void suspend()` |  |

---

### `class ViewAnimator`

- **继承：** `android.widget.FrameLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ViewAnimator(android.content.Context)` |  |
| `ViewAnimator(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getAnimateFirstView()` |  |
| `android.view.View getCurrentView()` |  |
| `int getDisplayedChild()` |  |
| `android.view.animation.Animation getInAnimation()` |  |
| `android.view.animation.Animation getOutAnimation()` |  |
| `void setAnimateFirstView(boolean)` |  |
| `void setDisplayedChild(int)` |  |
| `void setInAnimation(android.view.animation.Animation)` |  |
| `void setInAnimation(android.content.Context, @AnimRes int)` |  |
| `void setOutAnimation(android.view.animation.Animation)` |  |
| `void setOutAnimation(android.content.Context, @AnimRes int)` |  |
| `void showNext()` |  |
| `void showPrevious()` |  |

---

### `class ViewFlipper`

- **继承：** `android.widget.ViewAnimator`
- **注解：** `@android.widget.RemoteViews.RemoteView`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ViewFlipper(android.content.Context)` |  |
| `ViewFlipper(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isAutoStart()` |  |
| `boolean isFlipping()` |  |
| `void setAutoStart(boolean)` |  |
| `void setFlipInterval(@IntRange(from=0) int)` |  |
| `void startFlipping()` |  |
| `void stopFlipping()` |  |

---

### `class ViewSwitcher`

- **继承：** `android.widget.ViewAnimator`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ViewSwitcher(android.content.Context)` |  |
| `ViewSwitcher(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View getNextView()` |  |
| `void reset()` |  |
| `void setFactory(android.widget.ViewSwitcher.ViewFactory)` |  |

---

### `interface static ViewSwitcher.ViewFactory`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.view.View makeView()` |  |

---

### `interface WrapperListAdapter`

- **继承：** `android.widget.ListAdapter`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.widget.ListAdapter getWrappedAdapter()` |  |

---

### `class ZoomButton` ~~DEPRECATED~~

- **继承：** `android.widget.ImageButton`
- **实现：** `android.view.View.OnLongClickListener`
- **注解：** `@Deprecated`

---

### `class ZoomButtonsController` ~~DEPRECATED~~

- **实现：** `android.view.View.OnTouchListener`
- **注解：** `@Deprecated`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onTouch(android.view.View, android.view.MotionEvent)` |  |

---

### `interface static ZoomButtonsController.OnZoomListener` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class ZoomControls` ~~DEPRECATED~~

- **继承：** `android.widget.LinearLayout`
- **注解：** `@Deprecated`

---

## Package: `android.widget.inline`

### `class InlineContentView`

- **继承：** `android.view.ViewGroup`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isZOrderedOnTop()` |  |
| `void onLayout(boolean, int, int, int, int)` |  |
| `void setSurfaceControlCallback(@Nullable android.widget.inline.InlineContentView.SurfaceControlCallback)` |  |
| `boolean setZOrderedOnTop(boolean)` |  |

---

### `interface static InlineContentView.SurfaceControlCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onCreated(@NonNull android.view.SurfaceControl)` |  |
| `void onDestroyed(@NonNull android.view.SurfaceControl)` |  |

---

### `class final InlinePresentationSpec`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final InlinePresentationSpec.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InlinePresentationSpec.Builder(@NonNull android.util.Size, @NonNull android.util.Size)` |  |

---

