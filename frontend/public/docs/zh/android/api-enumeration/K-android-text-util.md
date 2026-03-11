# Android 11 (API 30) Public API Enumeration: Android Text Util

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.text` | 53 | 284 | 96 | 27 |
| `android.text.format` | 4 | 26 | 26 | 3 |
| `android.text.method` | 25 | 116 | 7 | 21 |
| `android.text.style` | 59 | 221 | 94 | 110 |
| `android.text.util` | 5 | 23 | 7 | 3 |
| `android.util` | 64 | 355 | 111 | 56 |
| `android.util.proto` | 1 | 16 | 33 | 3 |
| **TOTAL** | **211** | **1041** | **374** | **223** |

---

## Package: `android.text`

### `class AlteredCharSequence` ~~DEPRECATED~~

- **实现：** `java.lang.CharSequence android.text.GetChars`
- **注解：** `@Deprecated`

---

### `class AndroidCharacter` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class Annotation`

- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Annotation(String, String)` |  |
| `Annotation(android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getKey()` |  |
| `int getSpanTypeId()` |  |
| `String getValue()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class AutoText`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String get(CharSequence, int, int, android.view.View)` |  |
| `static int getSize(android.view.View)` |  |

---

### `class final BidiFormatter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.BidiFormatter getInstance()` |  |
| `static android.text.BidiFormatter getInstance(boolean)` |  |
| `static android.text.BidiFormatter getInstance(java.util.Locale)` |  |
| `boolean getStereoReset()` |  |
| `boolean isRtl(String)` |  |
| `boolean isRtl(CharSequence)` |  |
| `boolean isRtlContext()` |  |
| `String unicodeWrap(String, android.text.TextDirectionHeuristic)` |  |
| `CharSequence unicodeWrap(CharSequence, android.text.TextDirectionHeuristic)` |  |
| `String unicodeWrap(String, boolean)` |  |
| `CharSequence unicodeWrap(CharSequence, boolean)` |  |
| `String unicodeWrap(String)` |  |
| `CharSequence unicodeWrap(CharSequence)` |  |

---

### `class static final BidiFormatter.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BidiFormatter.Builder()` |  |
| `BidiFormatter.Builder(boolean)` |  |
| `BidiFormatter.Builder(java.util.Locale)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.BidiFormatter build()` |  |
| `android.text.BidiFormatter.Builder setTextDirectionHeuristic(android.text.TextDirectionHeuristic)` |  |
| `android.text.BidiFormatter.Builder stereoReset(boolean)` |  |

---

### `class BoringLayout`

- **继承：** `android.text.Layout`
- **实现：** `android.text.TextUtils.EllipsizeCallback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BoringLayout(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean)` |  |
| `BoringLayout(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean, android.text.TextUtils.TruncateAt, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void ellipsized(int, int)` |  |
| `int getBottomPadding()` |  |
| `int getEllipsisCount(int)` |  |
| `int getEllipsisStart(int)` |  |
| `boolean getLineContainsTab(int)` |  |
| `int getLineCount()` |  |
| `int getLineDescent(int)` |  |
| `final android.text.Layout.Directions getLineDirections(int)` |  |
| `int getLineStart(int)` |  |
| `int getLineTop(int)` |  |
| `int getParagraphDirection(int)` |  |
| `int getTopPadding()` |  |
| `static android.text.BoringLayout.Metrics isBoring(CharSequence, android.text.TextPaint)` |  |
| `static android.text.BoringLayout.Metrics isBoring(CharSequence, android.text.TextPaint, android.text.BoringLayout.Metrics)` |  |
| `static android.text.BoringLayout make(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean)` |  |
| `static android.text.BoringLayout make(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean, android.text.TextUtils.TruncateAt, int)` |  |
| `android.text.BoringLayout replaceOrMake(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean)` |  |
| `android.text.BoringLayout replaceOrMake(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float, android.text.BoringLayout.Metrics, boolean, android.text.TextUtils.TruncateAt, int)` |  |

---

### `class static BoringLayout.Metrics`

- **继承：** `android.graphics.Paint.FontMetricsInt`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BoringLayout.Metrics()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int width` |  |

---

### `class abstract ClipboardManager` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class DynamicLayout`

- **继承：** `android.text.Layout`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getBottomPadding()` |  |
| `int getEllipsisCount(int)` |  |
| `int getEllipsisStart(int)` |  |
| `boolean getLineContainsTab(int)` |  |
| `int getLineCount()` |  |
| `int getLineDescent(int)` |  |
| `final android.text.Layout.Directions getLineDirections(int)` |  |
| `int getLineStart(int)` |  |
| `int getLineTop(int)` |  |
| `int getParagraphDirection(int)` |  |
| `int getTopPadding()` |  |

---

### `class static final DynamicLayout.Builder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.DynamicLayout.Builder setEllipsize(@Nullable android.text.TextUtils.TruncateAt)` |  |

---

### `interface Editable`

- **继承：** `java.lang.CharSequence java.lang.Appendable android.text.GetChars android.text.Spannable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.Editable append(CharSequence)` |  |
| `android.text.Editable append(CharSequence, int, int)` |  |
| `android.text.Editable append(char)` |  |
| `void clear()` |  |
| `void clearSpans()` |  |
| `android.text.Editable delete(int, int)` |  |
| `android.text.InputFilter[] getFilters()` |  |
| `android.text.Editable insert(int, CharSequence, int, int)` |  |
| `android.text.Editable insert(int, CharSequence)` |  |
| `android.text.Editable replace(int, int, CharSequence, int, int)` |  |
| `android.text.Editable replace(int, int, CharSequence)` |  |
| `void setFilters(android.text.InputFilter[])` |  |

---

### `class static Editable.Factory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Editable.Factory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.Editable.Factory getInstance()` |  |
| `android.text.Editable newEditable(CharSequence)` |  |

---

### `interface GetChars`

- **继承：** `java.lang.CharSequence`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void getChars(int, int, char[], int)` |  |

---

### `class Html`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FROM_HTML_MODE_COMPACT = 63` |  |
| `static final int FROM_HTML_MODE_LEGACY = 0` |  |
| `static final int FROM_HTML_OPTION_USE_CSS_COLORS = 256` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 32` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 16` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 2` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 8` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 4` |  |
| `static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 1` |  |
| `static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0` |  |
| `static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String escapeHtml(CharSequence)` |  |
| `static android.text.Spanned fromHtml(String, int)` |  |
| `static android.text.Spanned fromHtml(String, int, android.text.Html.ImageGetter, android.text.Html.TagHandler)` |  |
| `static String toHtml(android.text.Spanned, int)` |  |

---

### `interface static Html.ImageGetter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.Drawable getDrawable(String)` |  |

---

### `interface static Html.TagHandler`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void handleTag(boolean, String, android.text.Editable, org.xml.sax.XMLReader)` |  |

---

### `interface InputFilter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence filter(CharSequence, int, int, android.text.Spanned, int, int)` |  |

---

### `class static InputFilter.AllCaps`

- **实现：** `android.text.InputFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InputFilter.AllCaps()` |  |
| `InputFilter.AllCaps(@NonNull java.util.Locale)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence filter(CharSequence, int, int, android.text.Spanned, int, int)` |  |

---

### `class static InputFilter.LengthFilter`

- **实现：** `android.text.InputFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InputFilter.LengthFilter(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence filter(CharSequence, int, int, android.text.Spanned, int, int)` |  |
| `int getMax()` |  |

---

### `interface InputType`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_CLASS_DATETIME = 4` |  |
| `static final int TYPE_CLASS_NUMBER = 2` |  |
| `static final int TYPE_CLASS_PHONE = 3` |  |
| `static final int TYPE_CLASS_TEXT = 1` |  |
| `static final int TYPE_DATETIME_VARIATION_DATE = 16` |  |
| `static final int TYPE_DATETIME_VARIATION_NORMAL = 0` |  |
| `static final int TYPE_DATETIME_VARIATION_TIME = 32` |  |
| `static final int TYPE_MASK_CLASS = 15` |  |
| `static final int TYPE_MASK_FLAGS = 16773120` |  |
| `static final int TYPE_MASK_VARIATION = 4080` |  |
| `static final int TYPE_NULL = 0` |  |
| `static final int TYPE_NUMBER_FLAG_DECIMAL = 8192` |  |
| `static final int TYPE_NUMBER_FLAG_SIGNED = 4096` |  |
| `static final int TYPE_NUMBER_VARIATION_NORMAL = 0` |  |
| `static final int TYPE_NUMBER_VARIATION_PASSWORD = 16` |  |
| `static final int TYPE_TEXT_FLAG_AUTO_COMPLETE = 65536` |  |
| `static final int TYPE_TEXT_FLAG_AUTO_CORRECT = 32768` |  |
| `static final int TYPE_TEXT_FLAG_CAP_CHARACTERS = 4096` |  |
| `static final int TYPE_TEXT_FLAG_CAP_SENTENCES = 16384` |  |
| `static final int TYPE_TEXT_FLAG_CAP_WORDS = 8192` |  |
| `static final int TYPE_TEXT_FLAG_IME_MULTI_LINE = 262144` |  |
| `static final int TYPE_TEXT_FLAG_MULTI_LINE = 131072` |  |
| `static final int TYPE_TEXT_FLAG_NO_SUGGESTIONS = 524288` |  |
| `static final int TYPE_TEXT_VARIATION_EMAIL_ADDRESS = 32` |  |
| `static final int TYPE_TEXT_VARIATION_EMAIL_SUBJECT = 48` |  |
| `static final int TYPE_TEXT_VARIATION_FILTER = 176` |  |
| `static final int TYPE_TEXT_VARIATION_LONG_MESSAGE = 80` |  |
| `static final int TYPE_TEXT_VARIATION_NORMAL = 0` |  |
| `static final int TYPE_TEXT_VARIATION_PASSWORD = 128` |  |
| `static final int TYPE_TEXT_VARIATION_PERSON_NAME = 96` |  |
| `static final int TYPE_TEXT_VARIATION_PHONETIC = 192` |  |
| `static final int TYPE_TEXT_VARIATION_POSTAL_ADDRESS = 112` |  |
| `static final int TYPE_TEXT_VARIATION_SHORT_MESSAGE = 64` |  |
| `static final int TYPE_TEXT_VARIATION_URI = 16` |  |
| `static final int TYPE_TEXT_VARIATION_VISIBLE_PASSWORD = 144` |  |
| `static final int TYPE_TEXT_VARIATION_WEB_EDIT_TEXT = 160` |  |
| `static final int TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS = 208` |  |
| `static final int TYPE_TEXT_VARIATION_WEB_PASSWORD = 224` |  |

---

### `class abstract Layout`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Layout(CharSequence, android.text.TextPaint, int, android.text.Layout.Alignment, float, float)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BREAK_STRATEGY_BALANCED = 2` |  |
| `static final int BREAK_STRATEGY_HIGH_QUALITY = 1` |  |
| `static final int BREAK_STRATEGY_SIMPLE = 0` |  |
| `static final float DEFAULT_LINESPACING_ADDITION = 0.0f` |  |
| `static final float DEFAULT_LINESPACING_MULTIPLIER = 1.0f` |  |
| `static final int DIR_LEFT_TO_RIGHT = 1` |  |
| `static final int DIR_RIGHT_TO_LEFT = -1` |  |
| `static final int HYPHENATION_FREQUENCY_FULL = 2` |  |
| `static final int HYPHENATION_FREQUENCY_NONE = 0` |  |
| `static final int HYPHENATION_FREQUENCY_NORMAL = 1` |  |
| `static final int JUSTIFICATION_MODE_INTER_WORD = 1` |  |
| `static final int JUSTIFICATION_MODE_NONE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `void draw(android.graphics.Canvas, android.graphics.Path, android.graphics.Paint, int)` |  |
| `final android.text.Layout.Alignment getAlignment()` |  |
| `abstract int getBottomPadding()` |  |
| `void getCursorPath(int, android.graphics.Path, CharSequence)` |  |
| `static float getDesiredWidth(CharSequence, android.text.TextPaint)` |  |
| `static float getDesiredWidth(CharSequence, int, int, android.text.TextPaint)` |  |
| `abstract int getEllipsisCount(int)` |  |
| `abstract int getEllipsisStart(int)` |  |
| `int getEllipsizedWidth()` |  |
| `int getHeight()` |  |
| `final int getLineAscent(int)` |  |
| `final int getLineBaseline(int)` |  |
| `final int getLineBottom(int)` |  |
| `int getLineBounds(int, android.graphics.Rect)` |  |
| `abstract boolean getLineContainsTab(int)` |  |
| `abstract int getLineCount()` |  |
| `abstract int getLineDescent(int)` |  |
| `abstract android.text.Layout.Directions getLineDirections(int)` |  |
| `final int getLineEnd(int)` |  |
| `int getLineForOffset(int)` |  |
| `int getLineForVertical(int)` |  |
| `float getLineLeft(int)` |  |
| `float getLineMax(int)` |  |
| `float getLineRight(int)` |  |
| `abstract int getLineStart(int)` |  |
| `abstract int getLineTop(int)` |  |
| `int getLineVisibleEnd(int)` |  |
| `float getLineWidth(int)` |  |
| `int getOffsetForHorizontal(int, float)` |  |
| `int getOffsetToLeftOf(int)` |  |
| `int getOffsetToRightOf(int)` |  |
| `final android.text.TextPaint getPaint()` |  |
| `final android.text.Layout.Alignment getParagraphAlignment(int)` |  |
| `abstract int getParagraphDirection(int)` |  |
| `final int getParagraphLeft(int)` |  |
| `final int getParagraphRight(int)` |  |
| `float getPrimaryHorizontal(int)` |  |
| `float getSecondaryHorizontal(int)` |  |
| `void getSelectionPath(int, int, android.graphics.Path)` |  |
| `final float getSpacingAdd()` |  |
| `final float getSpacingMultiplier()` |  |
| `final CharSequence getText()` |  |
| `abstract int getTopPadding()` |  |
| `final int getWidth()` |  |
| `final void increaseWidthTo(int)` |  |
| `boolean isRtlCharAt(int)` |  |
| `final boolean isSpanned()` |  |

---

### `enum Layout.Alignment`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.text.Layout.Alignment ALIGN_CENTER` |  |
| `static final android.text.Layout.Alignment ALIGN_NORMAL` |  |
| `static final android.text.Layout.Alignment ALIGN_OPPOSITE` |  |

---

### `class static Layout.Directions`


---

### `class abstract LoginFilter` ~~DEPRECATED~~

- **实现：** `android.text.InputFilter`
- **注解：** `@Deprecated`

---

### `class static LoginFilter.PasswordFilterGMail` ~~DEPRECATED~~

- **继承：** `android.text.LoginFilter`
- **注解：** `@Deprecated`

---

### `class static LoginFilter.UsernameFilterGMail` ~~DEPRECATED~~

- **继承：** `android.text.LoginFilter`
- **注解：** `@Deprecated`

---

### `class static LoginFilter.UsernameFilterGeneric` ~~DEPRECATED~~

- **继承：** `android.text.LoginFilter`
- **注解：** `@Deprecated`

---

### `interface NoCopySpan`


---

### `class static NoCopySpan.Concrete`

- **实现：** `android.text.NoCopySpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NoCopySpan.Concrete()` |  |

---

### `interface ParcelableSpan`

- **继承：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSpanTypeId()` |  |

---

### `class PrecomputedText`

- **实现：** `android.text.Spannable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `char charAt(int)` |  |
| `static android.text.PrecomputedText create(@NonNull CharSequence, @NonNull android.text.PrecomputedText.Params)` |  |
| `void getBounds(@IntRange(from=0) int, @IntRange(from=0) int, @NonNull android.graphics.Rect)` |  |
| `int getSpanEnd(Object)` |  |
| `int getSpanFlags(Object)` |  |
| `int getSpanStart(Object)` |  |
| `<T> T[] getSpans(int, int, Class<T>)` |  |
| `int length()` |  |
| `int nextSpanTransition(int, int, Class)` |  |
| `void removeSpan(Object)` |  |
| `void setSpan(Object, int, int, int)` |  |
| `CharSequence subSequence(int, int)` |  |

---

### `class static final PrecomputedText.Params`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getBreakStrategy()` |  |
| `int getHyphenationFrequency()` |  |

---

### `class static PrecomputedText.Params.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PrecomputedText.Params.Builder(@NonNull android.text.TextPaint)` |  |
| `PrecomputedText.Params.Builder(@NonNull android.text.PrecomputedText.Params)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.PrecomputedText.Params.Builder setBreakStrategy(int)` |  |
| `android.text.PrecomputedText.Params.Builder setHyphenationFrequency(int)` |  |
| `android.text.PrecomputedText.Params.Builder setTextDirection(@NonNull android.text.TextDirectionHeuristic)` |  |

---

### `class Selection`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final Object SELECTION_END` |  |
| `static final Object SELECTION_START` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean extendDown(android.text.Spannable, android.text.Layout)` |  |
| `static boolean extendLeft(android.text.Spannable, android.text.Layout)` |  |
| `static boolean extendRight(android.text.Spannable, android.text.Layout)` |  |
| `static final void extendSelection(android.text.Spannable, int)` |  |
| `static boolean extendToLeftEdge(android.text.Spannable, android.text.Layout)` |  |
| `static boolean extendToRightEdge(android.text.Spannable, android.text.Layout)` |  |
| `static boolean extendUp(android.text.Spannable, android.text.Layout)` |  |
| `static final int getSelectionEnd(CharSequence)` |  |
| `static final int getSelectionStart(CharSequence)` |  |
| `static boolean moveDown(android.text.Spannable, android.text.Layout)` |  |
| `static boolean moveLeft(android.text.Spannable, android.text.Layout)` |  |
| `static boolean moveRight(android.text.Spannable, android.text.Layout)` |  |
| `static boolean moveToLeftEdge(android.text.Spannable, android.text.Layout)` |  |
| `static boolean moveToRightEdge(android.text.Spannable, android.text.Layout)` |  |
| `static boolean moveUp(android.text.Spannable, android.text.Layout)` |  |
| `static final void removeSelection(android.text.Spannable)` |  |
| `static final void selectAll(android.text.Spannable)` |  |
| `static void setSelection(android.text.Spannable, int, int)` |  |
| `static final void setSelection(android.text.Spannable, int)` |  |

---

### `interface SpanWatcher`

- **继承：** `android.text.NoCopySpan`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onSpanAdded(android.text.Spannable, Object, int, int)` |  |
| `void onSpanChanged(android.text.Spannable, Object, int, int, int, int)` |  |
| `void onSpanRemoved(android.text.Spannable, Object, int, int)` |  |

---

### `interface Spannable`

- **继承：** `android.text.Spanned`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void removeSpan(Object)` |  |
| `void setSpan(Object, int, int, int)` |  |

---

### `class static Spannable.Factory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Spannable.Factory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.Spannable.Factory getInstance()` |  |
| `android.text.Spannable newSpannable(CharSequence)` |  |

---

### `class SpannableString`

- **实现：** `java.lang.CharSequence android.text.GetChars android.text.Spannable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SpannableString(CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final char charAt(int)` |  |
| `final void getChars(int, int, char[], int)` |  |
| `int getSpanEnd(Object)` |  |
| `int getSpanFlags(Object)` |  |
| `int getSpanStart(Object)` |  |
| `<T> T[] getSpans(int, int, Class<T>)` |  |
| `final int length()` |  |
| `int nextSpanTransition(int, int, Class)` |  |
| `void removeSpan(Object)` |  |
| `void setSpan(Object, int, int, int)` |  |
| `final CharSequence subSequence(int, int)` |  |
| `final String toString()` |  |
| `static android.text.SpannableString valueOf(CharSequence)` |  |

---

### `class SpannableStringBuilder`

- **实现：** `java.lang.Appendable java.lang.CharSequence android.text.Editable android.text.GetChars android.text.Spannable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SpannableStringBuilder()` |  |
| `SpannableStringBuilder(CharSequence)` |  |
| `SpannableStringBuilder(CharSequence, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.SpannableStringBuilder append(CharSequence)` |  |
| `android.text.SpannableStringBuilder append(CharSequence, Object, int)` |  |
| `android.text.SpannableStringBuilder append(CharSequence, int, int)` |  |
| `android.text.SpannableStringBuilder append(char)` |  |
| `char charAt(int)` |  |
| `void clear()` |  |
| `void clearSpans()` |  |
| `android.text.SpannableStringBuilder delete(int, int)` |  |
| `void getChars(int, int, char[], int)` |  |
| `android.text.InputFilter[] getFilters()` |  |
| `int getSpanEnd(Object)` |  |
| `int getSpanFlags(Object)` |  |
| `int getSpanStart(Object)` |  |
| `<T> T[] getSpans(int, int, @Nullable Class<T>)` |  |
| `int getTextWatcherDepth()` |  |
| `android.text.SpannableStringBuilder insert(int, CharSequence, int, int)` |  |
| `android.text.SpannableStringBuilder insert(int, CharSequence)` |  |
| `int length()` |  |
| `int nextSpanTransition(int, int, Class)` |  |
| `void removeSpan(Object)` |  |
| `android.text.SpannableStringBuilder replace(int, int, CharSequence)` |  |
| `android.text.SpannableStringBuilder replace(int, int, CharSequence, int, int)` |  |
| `void setFilters(android.text.InputFilter[])` |  |
| `void setSpan(Object, int, int, int)` |  |
| `CharSequence subSequence(int, int)` |  |
| `static android.text.SpannableStringBuilder valueOf(CharSequence)` |  |

---

### `interface Spanned`

- **继承：** `java.lang.CharSequence`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SPAN_COMPOSING = 256` |  |
| `static final int SPAN_EXCLUSIVE_EXCLUSIVE = 33` |  |
| `static final int SPAN_EXCLUSIVE_INCLUSIVE = 34` |  |
| `static final int SPAN_INCLUSIVE_EXCLUSIVE = 17` |  |
| `static final int SPAN_INCLUSIVE_INCLUSIVE = 18` |  |
| `static final int SPAN_INTERMEDIATE = 512` |  |
| `static final int SPAN_MARK_MARK = 17` |  |
| `static final int SPAN_MARK_POINT = 18` |  |
| `static final int SPAN_PARAGRAPH = 51` |  |
| `static final int SPAN_POINT_MARK = 33` |  |
| `static final int SPAN_POINT_MARK_MASK = 51` |  |
| `static final int SPAN_POINT_POINT = 34` |  |
| `static final int SPAN_PRIORITY = 16711680` |  |
| `static final int SPAN_PRIORITY_SHIFT = 16` |  |
| `static final int SPAN_USER = -16777216` |  |
| `static final int SPAN_USER_SHIFT = 24` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSpanEnd(Object)` |  |
| `int getSpanFlags(Object)` |  |
| `int getSpanStart(Object)` |  |
| `<T> T[] getSpans(int, int, Class<T>)` |  |
| `int nextSpanTransition(int, int, Class)` |  |

---

### `class final SpannedString`

- **实现：** `java.lang.CharSequence android.text.GetChars android.text.Spanned`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SpannedString(CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final char charAt(int)` |  |
| `final void getChars(int, int, char[], int)` |  |
| `int getSpanEnd(Object)` |  |
| `int getSpanFlags(Object)` |  |
| `int getSpanStart(Object)` |  |
| `<T> T[] getSpans(int, int, Class<T>)` |  |
| `final int length()` |  |
| `int nextSpanTransition(int, int, Class)` |  |
| `CharSequence subSequence(int, int)` |  |
| `static android.text.SpannedString valueOf(CharSequence)` |  |

---

### `class StaticLayout`

- **继承：** `android.text.Layout`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getBottomPadding()` |  |
| `int getEllipsisCount(int)` |  |
| `int getEllipsisStart(int)` |  |
| `boolean getLineContainsTab(int)` |  |
| `int getLineCount()` |  |
| `int getLineDescent(int)` |  |
| `final android.text.Layout.Directions getLineDirections(int)` |  |
| `int getLineStart(int)` |  |
| `int getLineTop(int)` |  |
| `int getParagraphDirection(int)` |  |
| `int getTopPadding()` |  |

---

### `class static final StaticLayout.Builder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.StaticLayout.Builder setText(CharSequence)` |  |

---

### `interface TextDirectionHeuristic`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isRtl(char[], int, int)` |  |
| `boolean isRtl(CharSequence, int, int)` |  |

---

### `class TextDirectionHeuristics`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextDirectionHeuristics()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.text.TextDirectionHeuristic ANYRTL_LTR` |  |
| `static final android.text.TextDirectionHeuristic FIRSTSTRONG_LTR` |  |
| `static final android.text.TextDirectionHeuristic FIRSTSTRONG_RTL` |  |
| `static final android.text.TextDirectionHeuristic LOCALE` |  |
| `static final android.text.TextDirectionHeuristic LTR` |  |
| `static final android.text.TextDirectionHeuristic RTL` |  |

---

### `class TextPaint`

- **继承：** `android.graphics.Paint`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextPaint()` |  |
| `TextPaint(int)` |  |
| `TextPaint(android.graphics.Paint)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int baselineShift` |  |
| `float density` |  |
| `int[] drawableState` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void set(android.text.TextPaint)` |  |

---

### `class TextUtils`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CAP_MODE_CHARACTERS = 4096` |  |
| `static final int CAP_MODE_SENTENCES = 16384` |  |
| `static final int CAP_MODE_WORDS = 8192` |  |
| `static final android.os.Parcelable.Creator<java.lang.CharSequence> CHAR_SEQUENCE_CREATOR` |  |
| `static final int SAFE_STRING_FLAG_FIRST_LINE = 4` |  |
| `static final int SAFE_STRING_FLAG_SINGLE_LINE = 2` |  |
| `static final int SAFE_STRING_FLAG_TRIM = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence concat(java.lang.CharSequence...)` |  |
| `static void copySpansFrom(android.text.Spanned, int, int, Class, android.text.Spannable, int)` |  |
| `static void dumpSpans(CharSequence, android.util.Printer, String)` |  |
| `static CharSequence ellipsize(CharSequence, android.text.TextPaint, float, android.text.TextUtils.TruncateAt)` |  |
| `static CharSequence ellipsize(CharSequence, android.text.TextPaint, float, android.text.TextUtils.TruncateAt, boolean, @Nullable android.text.TextUtils.EllipsizeCallback)` |  |
| `static boolean equals(CharSequence, CharSequence)` |  |
| `static CharSequence expandTemplate(CharSequence, java.lang.CharSequence...)` |  |
| `static int getCapsMode(CharSequence, int, int)` |  |
| `static void getChars(CharSequence, int, int, char[], int)` |  |
| `static int getLayoutDirectionFromLocale(java.util.Locale)` |  |
| `static int getOffsetAfter(CharSequence, int)` |  |
| `static int getOffsetBefore(CharSequence, int)` |  |
| `static int getTrimmedLength(CharSequence)` |  |
| `static String htmlEncode(String)` |  |
| `static int indexOf(CharSequence, char)` |  |
| `static int indexOf(CharSequence, char, int)` |  |
| `static int indexOf(CharSequence, char, int, int)` |  |
| `static int indexOf(CharSequence, CharSequence)` |  |
| `static int indexOf(CharSequence, CharSequence, int)` |  |
| `static int indexOf(CharSequence, CharSequence, int, int)` |  |
| `static boolean isDigitsOnly(CharSequence)` |  |
| `static boolean isEmpty(@Nullable CharSequence)` |  |
| `static boolean isGraphic(CharSequence)` |  |
| `static String join(@NonNull CharSequence, @NonNull Object[])` |  |
| `static String join(@NonNull CharSequence, @NonNull Iterable)` |  |
| `static int lastIndexOf(CharSequence, char)` |  |
| `static int lastIndexOf(CharSequence, char, int)` |  |
| `static int lastIndexOf(CharSequence, char, int, int)` |  |
| `static CharSequence listEllipsize(@Nullable android.content.Context, @Nullable java.util.List<java.lang.CharSequence>, @NonNull String, @NonNull android.text.TextPaint, @FloatRange(from=0.0, fromInclusive=false) float, @PluralsRes int)` |  |
| `static boolean regionMatches(CharSequence, int, CharSequence, int, int)` |  |
| `static CharSequence replace(CharSequence, String[], CharSequence[])` |  |
| `static String[] split(String, String)` |  |
| `static String[] split(String, java.util.regex.Pattern)` |  |
| `static CharSequence stringOrSpannedString(CharSequence)` |  |
| `static String substring(CharSequence, int, int)` |  |
| `static void writeToParcel(@Nullable CharSequence, @NonNull android.os.Parcel, int)` |  |

---

### `interface static TextUtils.EllipsizeCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void ellipsized(int, int)` |  |

---

### `class static TextUtils.SimpleStringSplitter`

- **实现：** `java.util.Iterator<java.lang.String> android.text.TextUtils.StringSplitter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextUtils.SimpleStringSplitter(char)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean hasNext()` |  |
| `java.util.Iterator<java.lang.String> iterator()` |  |
| `String next()` |  |
| `void setString(String)` |  |

---

### `interface static TextUtils.StringSplitter`

- **继承：** `java.lang.Iterable<java.lang.String>`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setString(String)` |  |

---

### `enum TextUtils.TruncateAt`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.text.TextUtils.TruncateAt END` |  |
| `static final android.text.TextUtils.TruncateAt MARQUEE` |  |
| `static final android.text.TextUtils.TruncateAt MIDDLE` |  |
| `static final android.text.TextUtils.TruncateAt START` |  |

---

### `interface TextWatcher`

- **继承：** `android.text.NoCopySpan`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void afterTextChanged(android.text.Editable)` |  |
| `void beforeTextChanged(CharSequence, int, int, int)` |  |
| `void onTextChanged(CharSequence, int, int, int)` |  |

---

## Package: `android.text.format`

### `class DateFormat`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateFormat()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence format(CharSequence, long)` |  |
| `static CharSequence format(CharSequence, java.util.Date)` |  |
| `static CharSequence format(CharSequence, java.util.Calendar)` |  |
| `static String getBestDateTimePattern(java.util.Locale, String)` |  |
| `static java.text.DateFormat getDateFormat(android.content.Context)` |  |
| `static char[] getDateFormatOrder(android.content.Context)` |  |
| `static java.text.DateFormat getLongDateFormat(android.content.Context)` |  |
| `static java.text.DateFormat getMediumDateFormat(android.content.Context)` |  |
| `static java.text.DateFormat getTimeFormat(android.content.Context)` |  |
| `static boolean is24HourFormat(android.content.Context)` |  |

---

### `class DateUtils`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateUtils()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ABBREV_WEEKDAY_FORMAT = "%a"` |  |
| `static final long DAY_IN_MILLIS = 86400000L` |  |
| `static final int FORMAT_ABBREV_ALL = 524288` |  |
| `static final int FORMAT_ABBREV_MONTH = 65536` |  |
| `static final int FORMAT_ABBREV_RELATIVE = 262144` |  |
| `static final int FORMAT_ABBREV_TIME = 16384` |  |
| `static final int FORMAT_ABBREV_WEEKDAY = 32768` |  |
| `static final int FORMAT_NO_MIDNIGHT = 2048` |  |
| `static final int FORMAT_NO_MONTH_DAY = 32` |  |
| `static final int FORMAT_NO_NOON = 512` |  |
| `static final int FORMAT_NO_YEAR = 8` |  |
| `static final int FORMAT_NUMERIC_DATE = 131072` |  |
| `static final int FORMAT_SHOW_DATE = 16` |  |
| `static final int FORMAT_SHOW_TIME = 1` |  |
| `static final int FORMAT_SHOW_WEEKDAY = 2` |  |
| `static final int FORMAT_SHOW_YEAR = 4` |  |
| `static final long HOUR_IN_MILLIS = 3600000L` |  |
| `static final long MINUTE_IN_MILLIS = 60000L` |  |
| `static final String MONTH_DAY_FORMAT = "%-d"` |  |
| `static final String MONTH_FORMAT = "%B"` |  |
| `static final String NUMERIC_MONTH_FORMAT = "%m"` |  |
| `static final long SECOND_IN_MILLIS = 1000L` |  |
| `static final String WEEKDAY_FORMAT = "%A"` |  |
| `static final long WEEK_IN_MILLIS = 604800000L` |  |
| `static final String YEAR_FORMAT = "%Y"` |  |
| `static final String YEAR_FORMAT_TWO_DIGITS = "%g"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String formatDateRange(android.content.Context, long, long, int)` |  |
| `static java.util.Formatter formatDateRange(android.content.Context, java.util.Formatter, long, long, int)` |  |
| `static java.util.Formatter formatDateRange(android.content.Context, java.util.Formatter, long, long, int, String)` |  |
| `static String formatDateTime(android.content.Context, long, int)` |  |
| `static String formatElapsedTime(long)` |  |
| `static String formatElapsedTime(StringBuilder, long)` |  |
| `static final CharSequence formatSameDayTime(long, long, int, int)` |  |
| `static CharSequence getRelativeDateTimeString(android.content.Context, long, long, long, int)` |  |
| `static CharSequence getRelativeTimeSpanString(long)` |  |
| `static CharSequence getRelativeTimeSpanString(long, long, long)` |  |
| `static CharSequence getRelativeTimeSpanString(long, long, long, int)` |  |
| `static CharSequence getRelativeTimeSpanString(android.content.Context, long, boolean)` |  |
| `static CharSequence getRelativeTimeSpanString(android.content.Context, long)` |  |
| `static boolean isToday(long)` |  |

---

### `class final Formatter`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Formatter()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String formatFileSize(@Nullable android.content.Context, long)` |  |
| `static String formatShortFileSize(@Nullable android.content.Context, long)` |  |

---

### `class Time` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

## Package: `android.text.method`

### `class ArrowKeyMovementMethod`

- **继承：** `android.text.method.BaseMovementMethod`
- **实现：** `android.text.method.MovementMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ArrowKeyMovementMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.method.MovementMethod getInstance()` |  |

---

### `class abstract BaseKeyListener`

- **继承：** `android.text.method.MetaKeyKeyListener`
- **实现：** `android.text.method.KeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BaseKeyListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean backspace(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |
| `boolean forwardDelete(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |
| `boolean onKeyOther(android.view.View, android.text.Editable, android.view.KeyEvent)` |  |

---

### `class BaseMovementMethod`

- **实现：** `android.text.method.MovementMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BaseMovementMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean bottom(android.widget.TextView, android.text.Spannable)` |  |
| `boolean canSelectArbitrarily()` |  |
| `boolean down(android.widget.TextView, android.text.Spannable)` |  |
| `boolean end(android.widget.TextView, android.text.Spannable)` |  |
| `int getMovementMetaState(android.text.Spannable, android.view.KeyEvent)` |  |
| `boolean handleMovementKey(android.widget.TextView, android.text.Spannable, int, int, android.view.KeyEvent)` |  |
| `boolean home(android.widget.TextView, android.text.Spannable)` |  |
| `void initialize(android.widget.TextView, android.text.Spannable)` |  |
| `boolean left(android.widget.TextView, android.text.Spannable)` |  |
| `boolean lineEnd(android.widget.TextView, android.text.Spannable)` |  |
| `boolean lineStart(android.widget.TextView, android.text.Spannable)` |  |
| `boolean onGenericMotionEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `boolean onKeyDown(android.widget.TextView, android.text.Spannable, int, android.view.KeyEvent)` |  |
| `boolean onKeyOther(android.widget.TextView, android.text.Spannable, android.view.KeyEvent)` |  |
| `boolean onKeyUp(android.widget.TextView, android.text.Spannable, int, android.view.KeyEvent)` |  |
| `void onTakeFocus(android.widget.TextView, android.text.Spannable, int)` |  |
| `boolean onTouchEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `boolean pageDown(android.widget.TextView, android.text.Spannable)` |  |
| `boolean pageUp(android.widget.TextView, android.text.Spannable)` |  |
| `boolean right(android.widget.TextView, android.text.Spannable)` |  |
| `boolean top(android.widget.TextView, android.text.Spannable)` |  |
| `boolean up(android.widget.TextView, android.text.Spannable)` |  |

---

### `class CharacterPickerDialog`

- **继承：** `android.app.Dialog`
- **实现：** `android.widget.AdapterView.OnItemClickListener android.view.View.OnClickListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CharacterPickerDialog(android.content.Context, android.view.View, android.text.Editable, String, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onClick(android.view.View)` |  |
| `void onItemClick(android.widget.AdapterView, android.view.View, int, long)` |  |

---

### `class DateKeyListener`

- **继承：** `android.text.method.NumberKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateKeyListener(@Nullable java.util.Locale)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getInputType()` |  |

---

### `class DateTimeKeyListener`

- **继承：** `android.text.method.NumberKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateTimeKeyListener(@Nullable java.util.Locale)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getInputType()` |  |

---

### `class DialerKeyListener`

- **继承：** `android.text.method.NumberKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DialerKeyListener()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final char[] CHARACTERS` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `char[] getAcceptedChars()` |  |
| `int getInputType()` |  |
| `static android.text.method.DialerKeyListener getInstance()` |  |

---

### `class DigitsKeyListener`

- **继承：** `android.text.method.NumberKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DigitsKeyListener(@Nullable java.util.Locale)` |  |
| `DigitsKeyListener(@Nullable java.util.Locale, boolean, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `char[] getAcceptedChars()` |  |
| `int getInputType()` |  |

---

### `class HideReturnsTransformationMethod`

- **继承：** `android.text.method.ReplacementTransformationMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HideReturnsTransformationMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.method.HideReturnsTransformationMethod getInstance()` |  |
| `char[] getOriginal()` |  |
| `char[] getReplacement()` |  |

---

### `interface KeyListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearMetaKeyState(android.view.View, android.text.Editable, int)` |  |
| `int getInputType()` |  |
| `boolean onKeyDown(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |
| `boolean onKeyOther(android.view.View, android.text.Editable, android.view.KeyEvent)` |  |
| `boolean onKeyUp(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |

---

### `class LinkMovementMethod`

- **继承：** `android.text.method.ScrollingMovementMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LinkMovementMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.method.MovementMethod getInstance()` |  |

---

### `class abstract MetaKeyKeyListener`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MetaKeyKeyListener()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int META_ALT_LOCKED = 512` |  |
| `static final int META_ALT_ON = 2` |  |
| `static final int META_CAP_LOCKED = 256` |  |
| `static final int META_SHIFT_ON = 1` |  |
| `static final int META_SYM_LOCKED = 1024` |  |
| `static final int META_SYM_ON = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void adjustMetaAfterKeypress(android.text.Spannable)` |  |
| `static long adjustMetaAfterKeypress(long)` |  |
| `void clearMetaKeyState(android.view.View, android.text.Editable, int)` |  |
| `static void clearMetaKeyState(android.text.Editable, int)` |  |
| `long clearMetaKeyState(long, int)` |  |
| `static final int getMetaState(CharSequence)` |  |
| `static final int getMetaState(CharSequence, android.view.KeyEvent)` |  |
| `static final int getMetaState(CharSequence, int)` |  |
| `static final int getMetaState(CharSequence, int, android.view.KeyEvent)` |  |
| `static final int getMetaState(long)` |  |
| `static final int getMetaState(long, int)` |  |
| `static long handleKeyDown(long, int, android.view.KeyEvent)` |  |
| `static long handleKeyUp(long, int, android.view.KeyEvent)` |  |
| `static boolean isMetaTracker(CharSequence, Object)` |  |
| `static boolean isSelectingMetaTracker(CharSequence, Object)` |  |
| `boolean onKeyDown(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(android.view.View, android.text.Editable, int, android.view.KeyEvent)` |  |
| `static void resetLockedMeta(android.text.Spannable)` |  |
| `static long resetLockedMeta(long)` |  |
| `static void resetMetaState(android.text.Spannable)` |  |

---

### `interface MovementMethod`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canSelectArbitrarily()` |  |
| `void initialize(android.widget.TextView, android.text.Spannable)` |  |
| `boolean onGenericMotionEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `boolean onKeyDown(android.widget.TextView, android.text.Spannable, int, android.view.KeyEvent)` |  |
| `boolean onKeyOther(android.widget.TextView, android.text.Spannable, android.view.KeyEvent)` |  |
| `boolean onKeyUp(android.widget.TextView, android.text.Spannable, int, android.view.KeyEvent)` |  |
| `void onTakeFocus(android.widget.TextView, android.text.Spannable, int)` |  |
| `boolean onTouchEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |

---

### `class MultiTapKeyListener`

- **继承：** `android.text.method.BaseKeyListener`
- **实现：** `android.text.SpanWatcher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MultiTapKeyListener(android.text.method.TextKeyListener.Capitalize, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getInputType()` |  |
| `static android.text.method.MultiTapKeyListener getInstance(boolean, android.text.method.TextKeyListener.Capitalize)` |  |
| `void onSpanAdded(android.text.Spannable, Object, int, int)` |  |
| `void onSpanChanged(android.text.Spannable, Object, int, int, int, int)` |  |
| `void onSpanRemoved(android.text.Spannable, Object, int, int)` |  |

---

### `class abstract NumberKeyListener`

- **继承：** `android.text.method.BaseKeyListener`
- **实现：** `android.text.InputFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NumberKeyListener()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence filter(CharSequence, int, int, android.text.Spanned, int, int)` |  |
| `int lookup(android.view.KeyEvent, android.text.Spannable)` |  |
| `static boolean ok(char[], char)` |  |

---

### `class PasswordTransformationMethod`

- **实现：** `android.text.TextWatcher android.text.method.TransformationMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PasswordTransformationMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void afterTextChanged(android.text.Editable)` |  |
| `void beforeTextChanged(CharSequence, int, int, int)` |  |
| `static android.text.method.PasswordTransformationMethod getInstance()` |  |
| `CharSequence getTransformation(CharSequence, android.view.View)` |  |
| `void onFocusChanged(android.view.View, CharSequence, boolean, int, android.graphics.Rect)` |  |
| `void onTextChanged(CharSequence, int, int, int)` |  |

---

### `class QwertyKeyListener`

- **继承：** `android.text.method.BaseKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `QwertyKeyListener(android.text.method.TextKeyListener.Capitalize, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getInputType()` |  |
| `static android.text.method.QwertyKeyListener getInstance(boolean, android.text.method.TextKeyListener.Capitalize)` |  |
| `static android.text.method.QwertyKeyListener getInstanceForFullKeyboard()` |  |
| `static void markAsReplaced(android.text.Spannable, int, int, String)` |  |

---

### `class abstract ReplacementTransformationMethod`

- **实现：** `android.text.method.TransformationMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ReplacementTransformationMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract char[] getOriginal()` |  |
| `abstract char[] getReplacement()` |  |
| `CharSequence getTransformation(CharSequence, android.view.View)` |  |
| `void onFocusChanged(android.view.View, CharSequence, boolean, int, android.graphics.Rect)` |  |

---

### `class ScrollingMovementMethod`

- **继承：** `android.text.method.BaseMovementMethod`
- **实现：** `android.text.method.MovementMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScrollingMovementMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.method.MovementMethod getInstance()` |  |

---

### `class SingleLineTransformationMethod`

- **继承：** `android.text.method.ReplacementTransformationMethod`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SingleLineTransformationMethod()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.text.method.SingleLineTransformationMethod getInstance()` |  |
| `char[] getOriginal()` |  |
| `char[] getReplacement()` |  |

---

### `class TextKeyListener`

- **继承：** `android.text.method.BaseKeyListener`
- **实现：** `android.text.SpanWatcher`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextKeyListener(android.text.method.TextKeyListener.Capitalize, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void clear(android.text.Editable)` |  |
| `int getInputType()` |  |
| `static android.text.method.TextKeyListener getInstance(boolean, android.text.method.TextKeyListener.Capitalize)` |  |
| `static android.text.method.TextKeyListener getInstance()` |  |
| `void onSpanAdded(android.text.Spannable, Object, int, int)` |  |
| `void onSpanChanged(android.text.Spannable, Object, int, int, int, int)` |  |
| `void onSpanRemoved(android.text.Spannable, Object, int, int)` |  |
| `void release()` |  |
| `static boolean shouldCap(android.text.method.TextKeyListener.Capitalize, CharSequence, int)` |  |

---

### `enum TextKeyListener.Capitalize`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.text.method.TextKeyListener.Capitalize CHARACTERS` |  |
| `static final android.text.method.TextKeyListener.Capitalize NONE` |  |
| `static final android.text.method.TextKeyListener.Capitalize SENTENCES` |  |
| `static final android.text.method.TextKeyListener.Capitalize WORDS` |  |

---

### `class TimeKeyListener`

- **继承：** `android.text.method.NumberKeyListener`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TimeKeyListener(@Nullable java.util.Locale)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getInputType()` |  |

---

### `class Touch`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int getInitialScrollX(android.widget.TextView, android.text.Spannable)` |  |
| `static int getInitialScrollY(android.widget.TextView, android.text.Spannable)` |  |
| `static boolean onTouchEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)` |  |
| `static void scrollTo(android.widget.TextView, android.text.Layout, int, int)` |  |

---

### `interface TransformationMethod`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequence getTransformation(CharSequence, android.view.View)` |  |
| `void onFocusChanged(android.view.View, CharSequence, boolean, int, android.graphics.Rect)` |  |

---

## Package: `android.text.style`

### `class AbsoluteSizeSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AbsoluteSizeSpan(int)` |  |
| `AbsoluteSizeSpan(int, boolean)` |  |
| `AbsoluteSizeSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getDip()` |  |
| `int getSize()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void updateMeasureState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface AlignmentSpan`

- **继承：** `android.text.style.ParagraphStyle`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.Layout.Alignment getAlignment()` |  |

---

### `class static AlignmentSpan.Standard`

- **实现：** `android.text.style.AlignmentSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AlignmentSpan.Standard(@NonNull android.text.Layout.Alignment)` |  |
| `AlignmentSpan.Standard(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.text.Layout.Alignment getAlignment()` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class BackgroundColorSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.ParcelableSpan android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BackgroundColorSpan(@ColorInt int)` |  |
| `BackgroundColorSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class BulletSpan`

- **实现：** `android.text.style.LeadingMarginSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BulletSpan()` |  |
| `BulletSpan(int)` |  |
| `BulletSpan(int, @ColorInt int)` |  |
| `BulletSpan(int, @ColorInt int, @IntRange(from=0) int)` |  |
| `BulletSpan(@NonNull android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STANDARD_GAP_WIDTH = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void drawLeadingMargin(@NonNull android.graphics.Canvas, @NonNull android.graphics.Paint, int, int, int, int, int, @NonNull CharSequence, int, int, boolean, @Nullable android.text.Layout)` |  |
| `int getBulletRadius()` |  |
| `int getColor()` |  |
| `int getGapWidth()` |  |
| `int getLeadingMargin(boolean)` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class abstract CharacterStyle`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CharacterStyle()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.CharacterStyle getUnderlying()` |  |
| `abstract void updateDrawState(android.text.TextPaint)` |  |
| `static android.text.style.CharacterStyle wrap(android.text.style.CharacterStyle)` |  |

---

### `class abstract ClickableSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ClickableSpan()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void onClick(@NonNull android.view.View)` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |

---

### `class DrawableMarginSpan`

- **实现：** `android.text.style.LeadingMarginSpan android.text.style.LineHeightSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DrawableMarginSpan(@NonNull android.graphics.drawable.Drawable)` |  |
| `DrawableMarginSpan(@NonNull android.graphics.drawable.Drawable, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void chooseHeight(@NonNull CharSequence, int, int, int, int, @NonNull android.graphics.Paint.FontMetricsInt)` |  |
| `void drawLeadingMargin(@NonNull android.graphics.Canvas, @NonNull android.graphics.Paint, int, int, int, int, int, @NonNull CharSequence, int, int, boolean, @NonNull android.text.Layout)` |  |
| `int getLeadingMargin(boolean)` |  |

---

### `class abstract DynamicDrawableSpan`

- **继承：** `android.text.style.ReplacementSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DynamicDrawableSpan()` |  |
| `DynamicDrawableSpan(int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALIGN_BASELINE = 1` |  |
| `static final int ALIGN_BOTTOM = 0` |  |
| `static final int ALIGN_CENTER = 2` |  |
| `final int mVerticalAlignment` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(@NonNull android.graphics.Canvas, CharSequence, @IntRange(from=0) int, @IntRange(from=0) int, float, int, int, int, @NonNull android.graphics.Paint)` |  |
| `abstract android.graphics.drawable.Drawable getDrawable()` |  |
| `int getSize(@NonNull android.graphics.Paint, CharSequence, @IntRange(from=0) int, @IntRange(from=0) int, @Nullable android.graphics.Paint.FontMetricsInt)` |  |
| `int getVerticalAlignment()` |  |

---

### `class EasyEditSpan`

- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EasyEditSpan()` |  |
| `EasyEditSpan(android.app.PendingIntent)` |  |
| `EasyEditSpan(@NonNull android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_TEXT_CHANGED_TYPE = "android.text.style.EXTRA_TEXT_CHANGED_TYPE"` |  |
| `static final int TEXT_DELETED = 1` |  |
| `static final int TEXT_MODIFIED = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class ForegroundColorSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.ParcelableSpan android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ForegroundColorSpan(@ColorInt int)` |  |
| `ForegroundColorSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class IconMarginSpan`

- **实现：** `android.text.style.LeadingMarginSpan android.text.style.LineHeightSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IconMarginSpan(@NonNull android.graphics.Bitmap)` |  |
| `IconMarginSpan(@NonNull android.graphics.Bitmap, @IntRange(from=0) int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void chooseHeight(CharSequence, int, int, int, int, android.graphics.Paint.FontMetricsInt)` |  |
| `void drawLeadingMargin(android.graphics.Canvas, android.graphics.Paint, int, int, int, int, int, CharSequence, int, int, boolean, android.text.Layout)` |  |
| `int getLeadingMargin(boolean)` |  |

---

### `class ImageSpan`

- **继承：** `android.text.style.DynamicDrawableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageSpan(@NonNull android.content.Context, @NonNull android.graphics.Bitmap)` |  |
| `ImageSpan(@NonNull android.content.Context, @NonNull android.graphics.Bitmap, int)` |  |
| `ImageSpan(@NonNull android.graphics.drawable.Drawable)` |  |
| `ImageSpan(@NonNull android.graphics.drawable.Drawable, int)` |  |
| `ImageSpan(@NonNull android.graphics.drawable.Drawable, @NonNull String)` |  |
| `ImageSpan(@NonNull android.graphics.drawable.Drawable, @NonNull String, int)` |  |
| `ImageSpan(@NonNull android.content.Context, @NonNull android.net.Uri)` |  |
| `ImageSpan(@NonNull android.content.Context, @NonNull android.net.Uri, int)` |  |
| `ImageSpan(@NonNull android.content.Context, @DrawableRes int)` |  |
| `ImageSpan(@NonNull android.content.Context, @DrawableRes int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.Drawable getDrawable()` |  |

---

### `interface LeadingMarginSpan`

- **继承：** `android.text.style.ParagraphStyle`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void drawLeadingMargin(android.graphics.Canvas, android.graphics.Paint, int, int, int, int, int, CharSequence, int, int, boolean, android.text.Layout)` |  |
| `int getLeadingMargin(boolean)` |  |

---

### `interface static LeadingMarginSpan.LeadingMarginSpan2`

- **继承：** `android.text.style.LeadingMarginSpan android.text.style.WrapTogetherSpan`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getLeadingMarginLineCount()` |  |

---

### `class static LeadingMarginSpan.Standard`

- **实现：** `android.text.style.LeadingMarginSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LeadingMarginSpan.Standard(int, int)` |  |
| `LeadingMarginSpan.Standard(int)` |  |
| `LeadingMarginSpan.Standard(android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void drawLeadingMargin(android.graphics.Canvas, android.graphics.Paint, int, int, int, int, int, CharSequence, int, int, boolean, android.text.Layout)` |  |
| `int getLeadingMargin(boolean)` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface LineBackgroundSpan`

- **继承：** `android.text.style.ParagraphStyle`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void drawBackground(@NonNull android.graphics.Canvas, @NonNull android.graphics.Paint, @Px int, @Px int, @Px int, @Px int, @Px int, @NonNull CharSequence, int, int, int)` |  |

---

### `class static LineBackgroundSpan.Standard`

- **实现：** `android.text.style.LineBackgroundSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LineBackgroundSpan.Standard(@ColorInt int)` |  |
| `LineBackgroundSpan.Standard(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void drawBackground(@NonNull android.graphics.Canvas, @NonNull android.graphics.Paint, @Px int, @Px int, @Px int, @Px int, @Px int, @NonNull CharSequence, int, int, int)` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface LineHeightSpan`

- **继承：** `android.text.style.ParagraphStyle android.text.style.WrapTogetherSpan`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void chooseHeight(CharSequence, int, int, int, int, android.graphics.Paint.FontMetricsInt)` |  |

---

### `class static LineHeightSpan.Standard`

- **实现：** `android.text.style.LineHeightSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LineHeightSpan.Standard(@IntRange(from=1) @Px int)` |  |
| `LineHeightSpan.Standard(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void chooseHeight(@NonNull CharSequence, int, int, int, int, @NonNull android.graphics.Paint.FontMetricsInt)` |  |
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static LineHeightSpan.WithDensity`

- **继承：** `android.text.style.LineHeightSpan`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void chooseHeight(CharSequence, int, int, int, int, android.graphics.Paint.FontMetricsInt, android.text.TextPaint)` |  |

---

### `class LocaleSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LocaleSpan(@Nullable java.util.Locale)` |  |
| `LocaleSpan(@NonNull android.os.LocaleList)` |  |
| `LocaleSpan(android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void updateMeasureState(android.text.TextPaint)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class MaskFilterSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MaskFilterSpan(android.graphics.MaskFilter)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.MaskFilter getMaskFilter()` |  |
| `void updateDrawState(android.text.TextPaint)` |  |

---

### `class abstract MetricAffectingSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.style.UpdateLayout`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MetricAffectingSpan()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.MetricAffectingSpan getUnderlying()` |  |
| `abstract void updateMeasureState(@NonNull android.text.TextPaint)` |  |

---

### `interface ParagraphStyle`


---

### `class QuoteSpan`

- **实现：** `android.text.style.LeadingMarginSpan android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `QuoteSpan()` |  |
| `QuoteSpan(@ColorInt int)` |  |
| `QuoteSpan(@ColorInt int, @IntRange(from=0) int, @IntRange(from=0) int)` |  |
| `QuoteSpan(@NonNull android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STANDARD_GAP_WIDTH_PX = 2` |  |
| `static final int STANDARD_STRIPE_WIDTH_PX = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void drawLeadingMargin(@NonNull android.graphics.Canvas, @NonNull android.graphics.Paint, int, int, int, int, int, @NonNull CharSequence, int, int, boolean, @NonNull android.text.Layout)` |  |
| `int getGapWidth()` |  |
| `int getLeadingMargin(boolean)` |  |
| `int getSpanTypeId()` |  |
| `int getStripeWidth()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class RelativeSizeSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RelativeSizeSpan(@FloatRange(from=0) float)` |  |
| `RelativeSizeSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `float getSizeChange()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void updateMeasureState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class abstract ReplacementSpan`

- **继承：** `android.text.style.MetricAffectingSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ReplacementSpan()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void draw(@NonNull android.graphics.Canvas, CharSequence, @IntRange(from=0) int, @IntRange(from=0) int, float, int, int, int, @NonNull android.graphics.Paint)` |  |
| `abstract int getSize(@NonNull android.graphics.Paint, CharSequence, @IntRange(from=0) int, @IntRange(from=0) int, @Nullable android.graphics.Paint.FontMetricsInt)` |  |
| `void setContentDescription(@Nullable CharSequence)` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void updateMeasureState(android.text.TextPaint)` |  |

---

### `class ScaleXSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScaleXSpan(@FloatRange(from=0) float)` |  |
| `ScaleXSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `float getScaleX()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void updateMeasureState(android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class StrikethroughSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.ParcelableSpan android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StrikethroughSpan()` |  |
| `StrikethroughSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class StyleSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StyleSpan(int)` |  |
| `StyleSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `int getStyle()` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void updateMeasureState(android.text.TextPaint)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SubscriptSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SubscriptSpan()` |  |
| `SubscriptSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void updateMeasureState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SuggestionSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SuggestionSpan(android.content.Context, String[], int)` |  |
| `SuggestionSpan(java.util.Locale, String[], int)` |  |
| `SuggestionSpan(android.content.Context, java.util.Locale, String[], int, Class<?>)` |  |
| `SuggestionSpan(android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_AUTO_CORRECTION = 4` |  |
| `static final int FLAG_EASY_CORRECT = 1` |  |
| `static final int FLAG_MISSPELLED = 2` |  |
| `static final int SUGGESTIONS_MAX_SIZE = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `int getSpanTypeId()` |  |
| `String[] getSuggestions()` |  |
| `void setFlags(int)` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SuperscriptSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SuperscriptSpan()` |  |
| `SuperscriptSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void updateMeasureState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface TabStopSpan`

- **继承：** `android.text.style.ParagraphStyle`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getTabStop()` |  |

---

### `class static TabStopSpan.Standard`

- **实现：** `android.text.style.TabStopSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TabStopSpan.Standard(@IntRange(from=0) int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getTabStop()` |  |

---

### `class TextAppearanceSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextAppearanceSpan(android.content.Context, int)` |  |
| `TextAppearanceSpan(android.content.Context, int, int)` |  |
| `TextAppearanceSpan(String, int, int, android.content.res.ColorStateList, android.content.res.ColorStateList)` |  |
| `TextAppearanceSpan(android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getFamily()` |  |
| `android.content.res.ColorStateList getLinkTextColor()` |  |
| `int getShadowColor()` |  |
| `float getShadowDx()` |  |
| `float getShadowDy()` |  |
| `float getShadowRadius()` |  |
| `int getSpanTypeId()` |  |
| `android.content.res.ColorStateList getTextColor()` |  |
| `int getTextFontWeight()` |  |
| `int getTextSize()` |  |
| `int getTextStyle()` |  |
| `boolean isElegantTextHeight()` |  |
| `void updateDrawState(android.text.TextPaint)` |  |
| `void updateMeasureState(android.text.TextPaint)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class TtsSpan`

- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan(String, android.os.PersistableBundle)` |  |
| `TtsSpan(android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ANIMACY_ANIMATE = "android.animate"` |  |
| `static final String ANIMACY_INANIMATE = "android.inanimate"` |  |
| `static final String ARG_ANIMACY = "android.arg.animacy"` |  |
| `static final String ARG_CASE = "android.arg.case"` |  |
| `static final String ARG_COUNTRY_CODE = "android.arg.country_code"` |  |
| `static final String ARG_CURRENCY = "android.arg.money"` |  |
| `static final String ARG_DAY = "android.arg.day"` |  |
| `static final String ARG_DENOMINATOR = "android.arg.denominator"` |  |
| `static final String ARG_DIGITS = "android.arg.digits"` |  |
| `static final String ARG_DOMAIN = "android.arg.domain"` |  |
| `static final String ARG_EXTENSION = "android.arg.extension"` |  |
| `static final String ARG_FRACTIONAL_PART = "android.arg.fractional_part"` |  |
| `static final String ARG_FRAGMENT_ID = "android.arg.fragment_id"` |  |
| `static final String ARG_GENDER = "android.arg.gender"` |  |
| `static final String ARG_HOURS = "android.arg.hours"` |  |
| `static final String ARG_INTEGER_PART = "android.arg.integer_part"` |  |
| `static final String ARG_MINUTES = "android.arg.minutes"` |  |
| `static final String ARG_MONTH = "android.arg.month"` |  |
| `static final String ARG_MULTIPLICITY = "android.arg.multiplicity"` |  |
| `static final String ARG_NUMBER = "android.arg.number"` |  |
| `static final String ARG_NUMBER_PARTS = "android.arg.number_parts"` |  |
| `static final String ARG_NUMERATOR = "android.arg.numerator"` |  |
| `static final String ARG_PASSWORD = "android.arg.password"` |  |
| `static final String ARG_PATH = "android.arg.path"` |  |
| `static final String ARG_PORT = "android.arg.port"` |  |
| `static final String ARG_PROTOCOL = "android.arg.protocol"` |  |
| `static final String ARG_QUANTITY = "android.arg.quantity"` |  |
| `static final String ARG_QUERY_STRING = "android.arg.query_string"` |  |
| `static final String ARG_TEXT = "android.arg.text"` |  |
| `static final String ARG_UNIT = "android.arg.unit"` |  |
| `static final String ARG_USERNAME = "android.arg.username"` |  |
| `static final String ARG_VERBATIM = "android.arg.verbatim"` |  |
| `static final String ARG_WEEKDAY = "android.arg.weekday"` |  |
| `static final String ARG_YEAR = "android.arg.year"` |  |
| `static final String CASE_ABLATIVE = "android.ablative"` |  |
| `static final String CASE_ACCUSATIVE = "android.accusative"` |  |
| `static final String CASE_DATIVE = "android.dative"` |  |
| `static final String CASE_GENITIVE = "android.genitive"` |  |
| `static final String CASE_INSTRUMENTAL = "android.instrumental"` |  |
| `static final String CASE_LOCATIVE = "android.locative"` |  |
| `static final String CASE_NOMINATIVE = "android.nominative"` |  |
| `static final String CASE_VOCATIVE = "android.vocative"` |  |
| `static final String GENDER_FEMALE = "android.female"` |  |
| `static final String GENDER_MALE = "android.male"` |  |
| `static final String GENDER_NEUTRAL = "android.neutral"` |  |
| `static final int MONTH_APRIL = 3` |  |
| `static final int MONTH_AUGUST = 7` |  |
| `static final int MONTH_DECEMBER = 11` |  |
| `static final int MONTH_FEBRUARY = 1` |  |
| `static final int MONTH_JANUARY = 0` |  |
| `static final int MONTH_JULY = 6` |  |
| `static final int MONTH_JUNE = 5` |  |
| `static final int MONTH_MARCH = 2` |  |
| `static final int MONTH_MAY = 4` |  |
| `static final int MONTH_NOVEMBER = 10` |  |
| `static final int MONTH_OCTOBER = 9` |  |
| `static final int MONTH_SEPTEMBER = 8` |  |
| `static final String MULTIPLICITY_DUAL = "android.dual"` |  |
| `static final String MULTIPLICITY_PLURAL = "android.plural"` |  |
| `static final String MULTIPLICITY_SINGLE = "android.single"` |  |
| `static final String TYPE_CARDINAL = "android.type.cardinal"` |  |
| `static final String TYPE_DATE = "android.type.date"` |  |
| `static final String TYPE_DECIMAL = "android.type.decimal"` |  |
| `static final String TYPE_DIGITS = "android.type.digits"` |  |
| `static final String TYPE_ELECTRONIC = "android.type.electronic"` |  |
| `static final String TYPE_FRACTION = "android.type.fraction"` |  |
| `static final String TYPE_MEASURE = "android.type.measure"` |  |
| `static final String TYPE_MONEY = "android.type.money"` |  |
| `static final String TYPE_ORDINAL = "android.type.ordinal"` |  |
| `static final String TYPE_TELEPHONE = "android.type.telephone"` |  |
| `static final String TYPE_TEXT = "android.type.text"` |  |
| `static final String TYPE_TIME = "android.type.time"` |  |
| `static final String TYPE_VERBATIM = "android.type.verbatim"` |  |
| `static final int WEEKDAY_FRIDAY = 6` |  |
| `static final int WEEKDAY_MONDAY = 2` |  |
| `static final int WEEKDAY_SATURDAY = 7` |  |
| `static final int WEEKDAY_SUNDAY = 1` |  |
| `static final int WEEKDAY_THURSDAY = 5` |  |
| `static final int WEEKDAY_TUESDAY = 3` |  |
| `static final int WEEKDAY_WEDNESDAY = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.os.PersistableBundle getArgs()` |  |
| `int getSpanTypeId()` |  |
| `String getType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static TtsSpan.Builder<C`

- **继承：** `android.text.style.TtsSpan.Builder<?>>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.Builder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan build()` |  |
| `C setIntArgument(String, int)` |  |
| `C setLongArgument(String, long)` |  |
| `C setStringArgument(String, String)` |  |

---

### `class static TtsSpan.CardinalBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.CardinalBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.CardinalBuilder()` |  |
| `TtsSpan.CardinalBuilder(long)` |  |
| `TtsSpan.CardinalBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.CardinalBuilder setNumber(long)` |  |
| `android.text.style.TtsSpan.CardinalBuilder setNumber(String)` |  |

---

### `class static TtsSpan.DateBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.DateBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.DateBuilder()` |  |
| `TtsSpan.DateBuilder(Integer, Integer, Integer, Integer)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.DateBuilder setDay(int)` |  |
| `android.text.style.TtsSpan.DateBuilder setMonth(int)` |  |
| `android.text.style.TtsSpan.DateBuilder setWeekday(int)` |  |
| `android.text.style.TtsSpan.DateBuilder setYear(int)` |  |

---

### `class static TtsSpan.DecimalBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.DecimalBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.DecimalBuilder()` |  |
| `TtsSpan.DecimalBuilder(double, int, int)` |  |
| `TtsSpan.DecimalBuilder(String, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.DecimalBuilder setArgumentsFromDouble(double, int, int)` |  |
| `android.text.style.TtsSpan.DecimalBuilder setFractionalPart(String)` |  |
| `android.text.style.TtsSpan.DecimalBuilder setIntegerPart(long)` |  |
| `android.text.style.TtsSpan.DecimalBuilder setIntegerPart(String)` |  |

---

### `class static TtsSpan.DigitsBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.DigitsBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.DigitsBuilder()` |  |
| `TtsSpan.DigitsBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.DigitsBuilder setDigits(String)` |  |

---

### `class static TtsSpan.ElectronicBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.ElectronicBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.ElectronicBuilder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.ElectronicBuilder setDomain(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setEmailArguments(String, String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setFragmentId(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setPassword(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setPath(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setPort(int)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setProtocol(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setQueryString(String)` |  |
| `android.text.style.TtsSpan.ElectronicBuilder setUsername(String)` |  |

---

### `class static TtsSpan.FractionBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.FractionBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.FractionBuilder()` |  |
| `TtsSpan.FractionBuilder(long, long, long)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.FractionBuilder setDenominator(long)` |  |
| `android.text.style.TtsSpan.FractionBuilder setDenominator(String)` |  |
| `android.text.style.TtsSpan.FractionBuilder setIntegerPart(long)` |  |
| `android.text.style.TtsSpan.FractionBuilder setIntegerPart(String)` |  |
| `android.text.style.TtsSpan.FractionBuilder setNumerator(long)` |  |
| `android.text.style.TtsSpan.FractionBuilder setNumerator(String)` |  |

---

### `class static TtsSpan.MeasureBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.MeasureBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.MeasureBuilder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.MeasureBuilder setDenominator(long)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setDenominator(String)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setFractionalPart(String)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setIntegerPart(long)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setIntegerPart(String)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setNumber(long)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setNumber(String)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setNumerator(long)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setNumerator(String)` |  |
| `android.text.style.TtsSpan.MeasureBuilder setUnit(String)` |  |

---

### `class static TtsSpan.MoneyBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.MoneyBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.MoneyBuilder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.MoneyBuilder setCurrency(String)` |  |
| `android.text.style.TtsSpan.MoneyBuilder setFractionalPart(String)` |  |
| `android.text.style.TtsSpan.MoneyBuilder setIntegerPart(long)` |  |
| `android.text.style.TtsSpan.MoneyBuilder setIntegerPart(String)` |  |
| `android.text.style.TtsSpan.MoneyBuilder setQuantity(String)` |  |

---

### `class static TtsSpan.OrdinalBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.OrdinalBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.OrdinalBuilder()` |  |
| `TtsSpan.OrdinalBuilder(long)` |  |
| `TtsSpan.OrdinalBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.OrdinalBuilder setNumber(long)` |  |
| `android.text.style.TtsSpan.OrdinalBuilder setNumber(String)` |  |

---

### `class static TtsSpan.SemioticClassBuilder<C`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<?>> extends android.text.style.TtsSpan.Builder<C>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.SemioticClassBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `C setAnimacy(String)` |  |
| `C setCase(String)` |  |
| `C setGender(String)` |  |
| `C setMultiplicity(String)` |  |

---

### `class static TtsSpan.TelephoneBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.TelephoneBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.TelephoneBuilder()` |  |
| `TtsSpan.TelephoneBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.TelephoneBuilder setCountryCode(String)` |  |
| `android.text.style.TtsSpan.TelephoneBuilder setExtension(String)` |  |
| `android.text.style.TtsSpan.TelephoneBuilder setNumberParts(String)` |  |

---

### `class static TtsSpan.TextBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.TextBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.TextBuilder()` |  |
| `TtsSpan.TextBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.TextBuilder setText(String)` |  |

---

### `class static TtsSpan.TimeBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.TimeBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.TimeBuilder()` |  |
| `TtsSpan.TimeBuilder(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.TimeBuilder setHours(int)` |  |
| `android.text.style.TtsSpan.TimeBuilder setMinutes(int)` |  |

---

### `class static TtsSpan.VerbatimBuilder`

- **继承：** `android.text.style.TtsSpan.SemioticClassBuilder<android.text.style.TtsSpan.VerbatimBuilder>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TtsSpan.VerbatimBuilder()` |  |
| `TtsSpan.VerbatimBuilder(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.text.style.TtsSpan.VerbatimBuilder setVerbatim(String)` |  |

---

### `class TypefaceSpan`

- **继承：** `android.text.style.MetricAffectingSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TypefaceSpan(@Nullable String)` |  |
| `TypefaceSpan(@NonNull android.graphics.Typeface)` |  |
| `TypefaceSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void updateMeasureState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class URLSpan`

- **继承：** `android.text.style.ClickableSpan`
- **实现：** `android.text.ParcelableSpan`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `URLSpan(String)` |  |
| `URLSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `String getURL()` |  |
| `void onClick(android.view.View)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class UnderlineSpan`

- **继承：** `android.text.style.CharacterStyle`
- **实现：** `android.text.ParcelableSpan android.text.style.UpdateAppearance`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UnderlineSpan()` |  |
| `UnderlineSpan(@NonNull android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSpanTypeId()` |  |
| `void updateDrawState(@NonNull android.text.TextPaint)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `interface UpdateAppearance`


---

### `interface UpdateLayout`

- **继承：** `android.text.style.UpdateAppearance`

---

### `interface WrapTogetherSpan`

- **继承：** `android.text.style.ParagraphStyle`

---

## Package: `android.text.util`

### `class Linkify`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Linkify()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALL = 15` |  |
| `static final int EMAIL_ADDRESSES = 2` |  |
| `static final int PHONE_NUMBERS = 4` |  |
| `static final int WEB_URLS = 1` |  |
| `static final android.text.util.Linkify.MatchFilter sPhoneNumberMatchFilter` |  |
| `static final android.text.util.Linkify.TransformFilter sPhoneNumberTransformFilter` |  |
| `static final android.text.util.Linkify.MatchFilter sUrlMatchFilter` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static final boolean addLinks(@NonNull android.text.Spannable, int)` |  |
| `static final boolean addLinks(@NonNull android.text.Spannable, int, @Nullable java.util.function.Function<java.lang.String,android.text.style.URLSpan>)` |  |
| `static final boolean addLinks(@NonNull android.widget.TextView, int)` |  |
| `static final void addLinks(@NonNull android.widget.TextView, @NonNull java.util.regex.Pattern, @Nullable String)` |  |
| `static final void addLinks(@NonNull android.widget.TextView, @NonNull java.util.regex.Pattern, @Nullable String, @Nullable android.text.util.Linkify.MatchFilter, @Nullable android.text.util.Linkify.TransformFilter)` |  |
| `static final void addLinks(@NonNull android.widget.TextView, @NonNull java.util.regex.Pattern, @Nullable String, @Nullable String[], @Nullable android.text.util.Linkify.MatchFilter, @Nullable android.text.util.Linkify.TransformFilter)` |  |
| `static final boolean addLinks(@NonNull android.text.Spannable, @NonNull java.util.regex.Pattern, @Nullable String)` |  |
| `static final boolean addLinks(@NonNull android.text.Spannable, @NonNull java.util.regex.Pattern, @Nullable String, @Nullable android.text.util.Linkify.MatchFilter, @Nullable android.text.util.Linkify.TransformFilter)` |  |
| `static final boolean addLinks(@NonNull android.text.Spannable, @NonNull java.util.regex.Pattern, @Nullable String, @Nullable String[], @Nullable android.text.util.Linkify.MatchFilter, @Nullable android.text.util.Linkify.TransformFilter)` |  |
| `static final boolean addLinks(@NonNull android.text.Spannable, @NonNull java.util.regex.Pattern, @Nullable String, @Nullable String[], @Nullable android.text.util.Linkify.MatchFilter, @Nullable android.text.util.Linkify.TransformFilter, @Nullable java.util.function.Function<java.lang.String,android.text.style.URLSpan>)` |  |

---

### `interface static Linkify.MatchFilter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean acceptMatch(CharSequence, int, int)` |  |

---

### `interface static Linkify.TransformFilter`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String transformUrl(java.util.regex.Matcher, String)` |  |

---

### `class Rfc822Token`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Rfc822Token(@Nullable String, @Nullable String, @Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String quoteComment(String)` |  |
| `static String quoteName(String)` |  |
| `static String quoteNameIfNecessary(String)` |  |
| `void setAddress(@Nullable String)` |  |
| `void setComment(@Nullable String)` |  |
| `void setName(@Nullable String)` |  |

---

### `class Rfc822Tokenizer`

- **实现：** `android.widget.MultiAutoCompleteTextView.Tokenizer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Rfc822Tokenizer()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int findTokenEnd(CharSequence, int)` |  |
| `int findTokenStart(CharSequence, int)` |  |
| `CharSequence terminateToken(CharSequence)` |  |
| `static void tokenize(CharSequence, java.util.Collection<android.text.util.Rfc822Token>)` |  |
| `static android.text.util.Rfc822Token[] tokenize(CharSequence)` |  |

---

## Package: `android.util`

### `class AndroidException`

- **继承：** `java.lang.Exception`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AndroidException()` |  |
| `AndroidException(String)` |  |
| `AndroidException(String, Throwable)` |  |
| `AndroidException(Exception)` |  |

---

### `class AndroidRuntimeException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AndroidRuntimeException()` |  |
| `AndroidRuntimeException(String)` |  |
| `AndroidRuntimeException(String, Throwable)` |  |
| `AndroidRuntimeException(Exception)` |  |

---

### `class final ArrayMap<K, V>`

- **实现：** `java.util.Map<K,V>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ArrayMap()` |  |
| `ArrayMap(int)` |  |
| `ArrayMap(android.util.ArrayMap<K,V>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `boolean containsAll(java.util.Collection<?>)` |  |
| `boolean containsKey(Object)` |  |
| `boolean containsValue(Object)` |  |
| `void ensureCapacity(int)` |  |
| `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` |  |
| `V get(Object)` |  |
| `int indexOfKey(Object)` |  |
| `int indexOfValue(Object)` |  |
| `boolean isEmpty()` |  |
| `K keyAt(int)` |  |
| `java.util.Set<K> keySet()` |  |
| `V put(K, V)` |  |
| `void putAll(android.util.ArrayMap<? extends K,? extends V>)` |  |
| `void putAll(java.util.Map<? extends K,? extends V>)` |  |
| `V remove(Object)` |  |
| `boolean removeAll(java.util.Collection<?>)` |  |
| `V removeAt(int)` |  |
| `boolean retainAll(java.util.Collection<?>)` |  |
| `V setValueAt(int, V)` |  |
| `int size()` |  |
| `V valueAt(int)` |  |
| `java.util.Collection<V> values()` |  |

---

### `class final ArraySet<E>`

- **实现：** `java.util.Collection<E> java.util.Set<E>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ArraySet()` |  |
| `ArraySet(int)` |  |
| `ArraySet(android.util.ArraySet<E>)` |  |
| `ArraySet(java.util.Collection<? extends E>)` |  |
| `ArraySet(@Nullable E[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean add(E)` |  |
| `void addAll(android.util.ArraySet<? extends E>)` |  |
| `boolean addAll(java.util.Collection<? extends E>)` |  |
| `void clear()` |  |
| `boolean contains(Object)` |  |
| `boolean containsAll(java.util.Collection<?>)` |  |
| `void ensureCapacity(int)` |  |
| `int indexOf(Object)` |  |
| `boolean isEmpty()` |  |
| `java.util.Iterator<E> iterator()` |  |
| `boolean remove(Object)` |  |
| `boolean removeAll(android.util.ArraySet<? extends E>)` |  |
| `boolean removeAll(java.util.Collection<?>)` |  |
| `E removeAt(int)` |  |
| `boolean retainAll(java.util.Collection<?>)` |  |
| `int size()` |  |
| `Object[] toArray()` |  |
| `<T> T[] toArray(T[])` |  |
| `E valueAt(int)` |  |

---

### `class AtomicFile`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AtomicFile(java.io.File)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void delete()` |  |
| `void failWrite(java.io.FileOutputStream)` |  |
| `void finishWrite(java.io.FileOutputStream)` |  |
| `java.io.File getBaseFile()` |  |
| `java.io.FileInputStream openRead() throws java.io.FileNotFoundException` |  |
| `byte[] readFully() throws java.io.IOException` |  |
| `java.io.FileOutputStream startWrite() throws java.io.IOException` |  |

---

### `interface AttributeSet`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getAttributeBooleanValue(String, String, boolean)` |  |
| `boolean getAttributeBooleanValue(int, boolean)` |  |
| `int getAttributeCount()` |  |
| `float getAttributeFloatValue(String, String, float)` |  |
| `float getAttributeFloatValue(int, float)` |  |
| `int getAttributeIntValue(String, String, int)` |  |
| `int getAttributeIntValue(int, int)` |  |
| `int getAttributeListValue(String, String, String[], int)` |  |
| `int getAttributeListValue(int, String[], int)` |  |
| `String getAttributeName(int)` |  |
| `int getAttributeNameResource(int)` |  |
| `default String getAttributeNamespace(int)` |  |
| `int getAttributeResourceValue(String, String, int)` |  |
| `int getAttributeResourceValue(int, int)` |  |
| `int getAttributeUnsignedIntValue(String, String, int)` |  |
| `int getAttributeUnsignedIntValue(int, int)` |  |
| `String getAttributeValue(int)` |  |
| `String getAttributeValue(String, String)` |  |
| `String getClassAttribute()` |  |
| `String getIdAttribute()` |  |
| `int getIdAttributeResourceValue(int)` |  |
| `String getPositionDescription()` |  |
| `int getStyleAttribute()` |  |

---

### `class Base64`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CRLF = 4` |  |
| `static final int DEFAULT = 0` |  |
| `static final int NO_CLOSE = 16` |  |
| `static final int NO_PADDING = 1` |  |
| `static final int NO_WRAP = 2` |  |
| `static final int URL_SAFE = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static byte[] decode(String, int)` |  |
| `static byte[] decode(byte[], int)` |  |
| `static byte[] decode(byte[], int, int, int)` |  |
| `static byte[] encode(byte[], int)` |  |
| `static byte[] encode(byte[], int, int, int)` |  |
| `static String encodeToString(byte[], int)` |  |
| `static String encodeToString(byte[], int, int, int)` |  |

---

### `class Base64DataException`

- **继承：** `java.io.IOException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Base64DataException(String)` |  |

---

### `class Base64InputStream`

- **继承：** `java.io.FilterInputStream`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Base64InputStream(java.io.InputStream, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int available()` |  |
| `void reset()` |  |

---

### `class Base64OutputStream`

- **继承：** `java.io.FilterOutputStream`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Base64OutputStream(java.io.OutputStream, int)` |  |

---

### `class final CloseGuard`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CloseGuard()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void open(@NonNull String)` |  |
| `void warnIfOpen()` |  |

---

### `class final Config` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class DebugUtils`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isObjectSelected(Object)` |  |

---

### `class DisplayMetrics`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DisplayMetrics()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DENSITY_140 = 140` |  |
| `static final int DENSITY_180 = 180` |  |
| `static final int DENSITY_200 = 200` |  |
| `static final int DENSITY_220 = 220` |  |
| `static final int DENSITY_260 = 260` |  |
| `static final int DENSITY_280 = 280` |  |
| `static final int DENSITY_300 = 300` |  |
| `static final int DENSITY_340 = 340` |  |
| `static final int DENSITY_360 = 360` |  |
| `static final int DENSITY_400 = 400` |  |
| `static final int DENSITY_420 = 420` |  |
| `static final int DENSITY_440 = 440` |  |
| `static final int DENSITY_450 = 450` |  |
| `static final int DENSITY_560 = 560` |  |
| `static final int DENSITY_600 = 600` |  |
| `static final int DENSITY_DEFAULT = 160` |  |
| `static final int DENSITY_DEVICE_STABLE` |  |
| `static final int DENSITY_HIGH = 240` |  |
| `static final int DENSITY_LOW = 120` |  |
| `static final int DENSITY_MEDIUM = 160` |  |
| `static final int DENSITY_TV = 213` |  |
| `static final int DENSITY_XHIGH = 320` |  |
| `static final int DENSITY_XXHIGH = 480` |  |
| `static final int DENSITY_XXXHIGH = 640` |  |
| `float density` |  |
| `int densityDpi` |  |
| `int heightPixels` |  |
| `float scaledDensity` |  |
| `int widthPixels` |  |
| `float xdpi` |  |
| `float ydpi` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean equals(android.util.DisplayMetrics)` |  |
| `void setTo(android.util.DisplayMetrics)` |  |
| `void setToDefaults()` |  |

---

### `class EventLog`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int getTagCode(String)` |  |
| `static String getTagName(int)` |  |
| `static void readEvents(int[], java.util.Collection<android.util.EventLog.Event>) throws java.io.IOException` |  |
| `static int writeEvent(int, int)` |  |
| `static int writeEvent(int, long)` |  |
| `static int writeEvent(int, float)` |  |
| `static int writeEvent(int, String)` |  |
| `static int writeEvent(int, java.lang.Object...)` |  |

---

### `class static final EventLog.Event`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object getData()` |  |
| `int getProcessId()` |  |
| `int getTag()` |  |
| `int getThreadId()` |  |
| `long getTimeNanos()` |  |

---

### `class EventLogTags` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static EventLogTags.Description` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class FloatMath` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class abstract FloatProperty<T>`

- **继承：** `android.util.Property<T,java.lang.Float>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FloatProperty(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void set(T, Float)` |  |
| `abstract void setValue(T, float)` |  |

---

### `class final Half`

- **继承：** `java.lang.Number`
- **实现：** `java.lang.Comparable<android.util.Half>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Half(@HalfFloat short)` |  |
| `Half(float)` |  |
| `Half(double)` |  |
| `Half(@NonNull String) throws java.lang.NumberFormatException` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MAX_EXPONENT = 15` |  |
| `static final int MIN_EXPONENT = -14` |  |
| `static final int SIZE = 16` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int compare(@HalfFloat short, @HalfFloat short)` |  |
| `int compareTo(@NonNull android.util.Half)` |  |
| `double doubleValue()` |  |
| `static boolean equals(@HalfFloat short, @HalfFloat short)` |  |
| `float floatValue()` |  |
| `static int getExponent(@HalfFloat short)` |  |
| `static int getSign(@HalfFloat short)` |  |
| `static int getSignificand(@HalfFloat short)` |  |
| `static boolean greater(@HalfFloat short, @HalfFloat short)` |  |
| `static boolean greaterEquals(@HalfFloat short, @HalfFloat short)` |  |
| `static int halfToIntBits(@HalfFloat short)` |  |
| `static int halfToRawIntBits(@HalfFloat short)` |  |
| `static int hashCode(@HalfFloat short)` |  |
| `int intValue()` |  |
| `static boolean isInfinite(@HalfFloat short)` |  |
| `boolean isNaN()` |  |
| `static boolean isNaN(@HalfFloat short)` |  |
| `static boolean isNormalized(@HalfFloat short)` |  |
| `static boolean less(@HalfFloat short, @HalfFloat short)` |  |
| `static boolean lessEquals(@HalfFloat short, @HalfFloat short)` |  |
| `long longValue()` |  |
| `static float toFloat(@HalfFloat short)` |  |

---

### `class abstract IntProperty<T>`

- **继承：** `android.util.Property<T,java.lang.Integer>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `IntProperty(String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void set(T, Integer)` |  |
| `abstract void setValue(T, int)` |  |

---

### `class final JsonReader`

- **实现：** `java.io.Closeable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JsonReader(java.io.Reader)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void beginArray() throws java.io.IOException` |  |
| `void beginObject() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void endArray() throws java.io.IOException` |  |
| `void endObject() throws java.io.IOException` |  |
| `boolean hasNext() throws java.io.IOException` |  |
| `boolean isLenient()` |  |
| `boolean nextBoolean() throws java.io.IOException` |  |
| `double nextDouble() throws java.io.IOException` |  |
| `int nextInt() throws java.io.IOException` |  |
| `long nextLong() throws java.io.IOException` |  |
| `String nextName() throws java.io.IOException` |  |
| `void nextNull() throws java.io.IOException` |  |
| `String nextString() throws java.io.IOException` |  |
| `android.util.JsonToken peek() throws java.io.IOException` |  |
| `void setLenient(boolean)` |  |
| `void skipValue() throws java.io.IOException` |  |

---

### `enum JsonToken`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.util.JsonToken BEGIN_ARRAY` |  |
| `static final android.util.JsonToken BEGIN_OBJECT` |  |
| `static final android.util.JsonToken BOOLEAN` |  |
| `static final android.util.JsonToken END_ARRAY` |  |
| `static final android.util.JsonToken END_DOCUMENT` |  |
| `static final android.util.JsonToken END_OBJECT` |  |
| `static final android.util.JsonToken NAME` |  |
| `static final android.util.JsonToken NULL` |  |
| `static final android.util.JsonToken NUMBER` |  |
| `static final android.util.JsonToken STRING` |  |

---

### `class final JsonWriter`

- **实现：** `java.io.Closeable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `JsonWriter(java.io.Writer)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.util.JsonWriter beginArray() throws java.io.IOException` |  |
| `android.util.JsonWriter beginObject() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `android.util.JsonWriter endArray() throws java.io.IOException` |  |
| `android.util.JsonWriter endObject() throws java.io.IOException` |  |
| `void flush() throws java.io.IOException` |  |
| `boolean isLenient()` |  |
| `android.util.JsonWriter name(String) throws java.io.IOException` |  |
| `android.util.JsonWriter nullValue() throws java.io.IOException` |  |
| `void setIndent(String)` |  |
| `void setLenient(boolean)` |  |
| `android.util.JsonWriter value(String) throws java.io.IOException` |  |
| `android.util.JsonWriter value(boolean) throws java.io.IOException` |  |
| `android.util.JsonWriter value(double) throws java.io.IOException` |  |
| `android.util.JsonWriter value(long) throws java.io.IOException` |  |
| `android.util.JsonWriter value(Number) throws java.io.IOException` |  |

---

### `class final LayoutDirection`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INHERIT = 2` |  |
| `static final int LOCALE = 3` |  |
| `static final int LTR = 0` |  |
| `static final int RTL = 1` |  |

---

### `class final Log`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ASSERT = 7` |  |
| `static final int DEBUG = 3` |  |
| `static final int ERROR = 6` |  |
| `static final int INFO = 4` |  |
| `static final int VERBOSE = 2` |  |
| `static final int WARN = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int d(@Nullable String, @NonNull String)` |  |
| `static int d(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `static int e(@Nullable String, @NonNull String)` |  |
| `static int e(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `static int i(@Nullable String, @NonNull String)` |  |
| `static int i(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `static boolean isLoggable(@Nullable @Size(max=23, apis="..23") String, int)` |  |
| `static int println(int, @Nullable String, @NonNull String)` |  |
| `static int v(@Nullable String, @NonNull String)` |  |
| `static int v(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `static int w(@Nullable String, @NonNull String)` |  |
| `static int w(@Nullable String, @Nullable String, @Nullable Throwable)` |  |
| `static int w(@Nullable String, @Nullable Throwable)` |  |
| `static int wtf(@Nullable String, @Nullable String)` |  |
| `static int wtf(@Nullable String, @NonNull Throwable)` |  |
| `static int wtf(@Nullable String, @Nullable String, @Nullable Throwable)` |  |

---

### `class LogPrinter`

- **实现：** `android.util.Printer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LogPrinter(int, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void println(String)` |  |

---

### `class LongSparseArray<E>`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LongSparseArray()` |  |
| `LongSparseArray(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void append(long, E)` |  |
| `void clear()` |  |
| `android.util.LongSparseArray<E> clone()` |  |
| `void delete(long)` |  |
| `E get(long)` |  |
| `E get(long, E)` |  |
| `int indexOfKey(long)` |  |
| `int indexOfValue(E)` |  |
| `long keyAt(int)` |  |
| `void put(long, E)` |  |
| `void remove(long)` |  |
| `void removeAt(int)` |  |
| `void setValueAt(int, E)` |  |
| `int size()` |  |
| `E valueAt(int)` |  |

---

### `class LruCache<K, V>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LruCache(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `V create(K)` |  |
| `final int createCount()` |  |
| `void entryRemoved(boolean, K, V, V)` |  |
| `final void evictAll()` |  |
| `final int evictionCount()` |  |
| `final V get(K)` |  |
| `final int hitCount()` |  |
| `final int maxSize()` |  |
| `final int missCount()` |  |
| `final V put(K, V)` |  |
| `final int putCount()` |  |
| `final V remove(K)` |  |
| `void resize(int)` |  |
| `final int size()` |  |
| `int sizeOf(K, V)` |  |
| `final java.util.Map<K,V> snapshot()` |  |
| `final String toString()` |  |
| `void trimToSize(int)` |  |

---

### `class final MalformedJsonException`

- **继承：** `java.io.IOException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MalformedJsonException(String)` |  |

---

### `class MonthDisplayHelper`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MonthDisplayHelper(int, int, int)` |  |
| `MonthDisplayHelper(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getColumnOf(int)` |  |
| `int getDayAt(int, int)` |  |
| `int[] getDigitsForRow(int)` |  |
| `int getFirstDayOfMonth()` |  |
| `int getMonth()` |  |
| `int getNumberOfDaysInMonth()` |  |
| `int getOffset()` |  |
| `int getRowOf(int)` |  |
| `int getWeekStartDay()` |  |
| `int getYear()` |  |
| `boolean isWithinCurrentMonth(int, int)` |  |
| `void nextMonth()` |  |
| `void previousMonth()` |  |

---

### `class final MutableBoolean` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableByte` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableChar` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableDouble` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableFloat` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableInt` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableLong` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final MutableShort` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class NoSuchPropertyException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NoSuchPropertyException(String)` |  |

---

### `class Pair<F, S>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Pair(F, S)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `final F first` |  |
| `final S second` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static <A, B> android.util.Pair<A,B> create(A, B)` |  |

---

### `class Patterns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final java.util.regex.Pattern DOMAIN_NAME` |  |
| `static final java.util.regex.Pattern EMAIL_ADDRESS` |  |
| `static final java.util.regex.Pattern IP_ADDRESS` |  |
| `static final java.util.regex.Pattern PHONE` |  |
| `static final java.util.regex.Pattern WEB_URL` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String concatGroups(java.util.regex.Matcher)` |  |
| `static final String digitsAndPlusOnly(java.util.regex.Matcher)` |  |

---

### `class PrintStreamPrinter`

- **实现：** `android.util.Printer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PrintStreamPrinter(java.io.PrintStream)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void println(String)` |  |

---

### `class PrintWriterPrinter`

- **实现：** `android.util.Printer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PrintWriterPrinter(java.io.PrintWriter)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void println(String)` |  |

---

### `interface Printer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void println(String)` |  |

---

### `class abstract Property<T, V>`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Property(Class<V>, String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract V get(T)` |  |
| `String getName()` |  |
| `Class<V> getType()` |  |
| `boolean isReadOnly()` |  |
| `static <T, V> android.util.Property<T,V> of(Class<T>, Class<V>, String)` |  |
| `void set(T, V)` |  |

---

### `class final Range<T`

- **继承：** `java.lang.Comparable<? super T>>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Range(T, T)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `T clamp(T)` |  |
| `boolean contains(T)` |  |
| `boolean contains(android.util.Range<T>)` |  |
| `static <T extends java.lang.Comparable<? super T>> android.util.Range<T> create(T, T)` |  |
| `android.util.Range<T> extend(android.util.Range<T>)` |  |
| `android.util.Range<T> extend(T, T)` |  |
| `android.util.Range<T> extend(T)` |  |
| `T getLower()` |  |
| `T getUpper()` |  |
| `android.util.Range<T> intersect(android.util.Range<T>)` |  |
| `android.util.Range<T> intersect(T, T)` |  |

---

### `class final Rational`

- **继承：** `java.lang.Number`
- **实现：** `java.lang.Comparable<android.util.Rational>`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Rational(int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.util.Rational NEGATIVE_INFINITY` |  |
| `static final android.util.Rational NaN` |  |
| `static final android.util.Rational POSITIVE_INFINITY` |  |
| `static final android.util.Rational ZERO` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int compareTo(android.util.Rational)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `int getDenominator()` |  |
| `int getNumerator()` |  |
| `int intValue()` |  |
| `boolean isFinite()` |  |
| `boolean isInfinite()` |  |
| `boolean isNaN()` |  |
| `boolean isZero()` |  |
| `long longValue()` |  |
| `static android.util.Rational parseRational(String) throws java.lang.NumberFormatException` |  |

---

### `class final Size`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Size(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getHeight()` |  |
| `int getWidth()` |  |
| `static android.util.Size parseSize(String) throws java.lang.NumberFormatException` |  |

---

### `class final SizeF`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SizeF(float, float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getHeight()` |  |
| `float getWidth()` |  |
| `static android.util.SizeF parseSizeF(String) throws java.lang.NumberFormatException` |  |

---

### `class SparseArray<E>`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SparseArray()` |  |
| `SparseArray(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void append(int, E)` |  |
| `void clear()` |  |
| `android.util.SparseArray<E> clone()` |  |
| `boolean contains(int)` |  |
| `void delete(int)` |  |
| `E get(int)` |  |
| `E get(int, E)` |  |
| `int indexOfKey(int)` |  |
| `int indexOfValue(E)` |  |
| `int keyAt(int)` |  |
| `void put(int, E)` |  |
| `void remove(int)` |  |
| `void removeAt(int)` |  |
| `void removeAtRange(int, int)` |  |
| `void setValueAt(int, E)` |  |
| `int size()` |  |
| `E valueAt(int)` |  |

---

### `class SparseBooleanArray`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SparseBooleanArray()` |  |
| `SparseBooleanArray(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void append(int, boolean)` |  |
| `void clear()` |  |
| `android.util.SparseBooleanArray clone()` |  |
| `void delete(int)` |  |
| `boolean get(int)` |  |
| `boolean get(int, boolean)` |  |
| `int indexOfKey(int)` |  |
| `int indexOfValue(boolean)` |  |
| `int keyAt(int)` |  |
| `void put(int, boolean)` |  |
| `void removeAt(int)` |  |
| `void setValueAt(int, boolean)` |  |
| `int size()` |  |
| `boolean valueAt(int)` |  |

---

### `class SparseIntArray`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SparseIntArray()` |  |
| `SparseIntArray(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void append(int, int)` |  |
| `void clear()` |  |
| `android.util.SparseIntArray clone()` |  |
| `void delete(int)` |  |
| `int get(int)` |  |
| `int get(int, int)` |  |
| `int indexOfKey(int)` |  |
| `int indexOfValue(int)` |  |
| `int keyAt(int)` |  |
| `void put(int, int)` |  |
| `void removeAt(int)` |  |
| `void setValueAt(int, int)` |  |
| `int size()` |  |
| `int valueAt(int)` |  |

---

### `class SparseLongArray`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SparseLongArray()` |  |
| `SparseLongArray(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void append(int, long)` |  |
| `void clear()` |  |
| `android.util.SparseLongArray clone()` |  |
| `void delete(int)` |  |
| `long get(int)` |  |
| `long get(int, long)` |  |
| `int indexOfKey(int)` |  |
| `int indexOfValue(long)` |  |
| `int keyAt(int)` |  |
| `void put(int, long)` |  |
| `void removeAt(int)` |  |
| `int size()` |  |
| `long valueAt(int)` |  |

---

### `class StateSet`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int[] NOTHING` |  |
| `static final int[] WILD_CARD` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String dump(int[])` |  |
| `static boolean isWildCard(int[])` |  |
| `static boolean stateSetMatches(int[], int[])` |  |
| `static boolean stateSetMatches(int[], int)` |  |
| `static int[] trimStateSet(int[], int)` |  |

---

### `class final StatsLog`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean logEvent(int)` |  |
| `static boolean logStart(int)` |  |
| `static boolean logStop(int)` |  |

---

### `class StringBuilderPrinter`

- **实现：** `android.util.Printer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StringBuilderPrinter(StringBuilder)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void println(String)` |  |

---

### `class TimeFormatException`

- **继承：** `java.lang.RuntimeException`

---

### `class TimeUtils`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static java.util.TimeZone getTimeZone(int, boolean, long, String)` |  |
| `static String getTimeZoneDatabaseVersion()` |  |
| `static boolean isTimeBetween(@NonNull java.time.LocalTime, @NonNull java.time.LocalTime, @NonNull java.time.LocalTime)` |  |

---

### `class TimingLogger` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class TypedValue`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TypedValue()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int COMPLEX_MANTISSA_MASK = 16777215` |  |
| `static final int COMPLEX_MANTISSA_SHIFT = 8` |  |
| `static final int COMPLEX_RADIX_0p23 = 3` |  |
| `static final int COMPLEX_RADIX_16p7 = 1` |  |
| `static final int COMPLEX_RADIX_23p0 = 0` |  |
| `static final int COMPLEX_RADIX_8p15 = 2` |  |
| `static final int COMPLEX_RADIX_MASK = 3` |  |
| `static final int COMPLEX_RADIX_SHIFT = 4` |  |
| `static final int COMPLEX_UNIT_DIP = 1` |  |
| `static final int COMPLEX_UNIT_FRACTION = 0` |  |
| `static final int COMPLEX_UNIT_FRACTION_PARENT = 1` |  |
| `static final int COMPLEX_UNIT_IN = 4` |  |
| `static final int COMPLEX_UNIT_MASK = 15` |  |
| `static final int COMPLEX_UNIT_MM = 5` |  |
| `static final int COMPLEX_UNIT_PT = 3` |  |
| `static final int COMPLEX_UNIT_PX = 0` |  |
| `static final int COMPLEX_UNIT_SHIFT = 0` |  |
| `static final int COMPLEX_UNIT_SP = 2` |  |
| `static final int DATA_NULL_EMPTY = 1` |  |
| `static final int DATA_NULL_UNDEFINED = 0` |  |
| `static final int DENSITY_DEFAULT = 0` |  |
| `static final int DENSITY_NONE = 65535` |  |
| `static final int TYPE_ATTRIBUTE = 2` |  |
| `static final int TYPE_DIMENSION = 5` |  |
| `static final int TYPE_FIRST_COLOR_INT = 28` |  |
| `static final int TYPE_FIRST_INT = 16` |  |
| `static final int TYPE_FLOAT = 4` |  |
| `static final int TYPE_FRACTION = 6` |  |
| `static final int TYPE_INT_BOOLEAN = 18` |  |
| `static final int TYPE_INT_COLOR_ARGB4 = 30` |  |
| `static final int TYPE_INT_COLOR_ARGB8 = 28` |  |
| `static final int TYPE_INT_COLOR_RGB4 = 31` |  |
| `static final int TYPE_INT_COLOR_RGB8 = 29` |  |
| `static final int TYPE_INT_DEC = 16` |  |
| `static final int TYPE_INT_HEX = 17` |  |
| `static final int TYPE_LAST_COLOR_INT = 31` |  |
| `static final int TYPE_LAST_INT = 31` |  |
| `static final int TYPE_NULL = 0` |  |
| `static final int TYPE_REFERENCE = 1` |  |
| `static final int TYPE_STRING = 3` |  |
| `int assetCookie` |  |
| `int changingConfigurations` |  |
| `int data` |  |
| `int density` |  |
| `int sourceResourceId` |  |
| `CharSequence string` |  |
| `int type` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static float applyDimension(int, float, android.util.DisplayMetrics)` |  |
| `final CharSequence coerceToString()` |  |
| `static final String coerceToString(int, int)` |  |
| `static float complexToDimension(int, android.util.DisplayMetrics)` |  |
| `static int complexToDimensionPixelOffset(int, android.util.DisplayMetrics)` |  |
| `static int complexToDimensionPixelSize(int, android.util.DisplayMetrics)` |  |
| `static float complexToFloat(int)` |  |
| `static float complexToFraction(int, float, float)` |  |
| `int getComplexUnit()` |  |
| `float getDimension(android.util.DisplayMetrics)` |  |
| `final float getFloat()` |  |
| `float getFraction(float, float)` |  |
| `boolean isColorType()` |  |
| `void setTo(android.util.TypedValue)` |  |

---

### `class Xml`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static String FEATURE_RELAXED` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.util.AttributeSet asAttributeSet(org.xmlpull.v1.XmlPullParser)` |  |
| `static android.util.Xml.Encoding findEncodingByName(String) throws java.io.UnsupportedEncodingException` |  |
| `static org.xmlpull.v1.XmlPullParser newPullParser()` |  |
| `static org.xmlpull.v1.XmlSerializer newSerializer()` |  |
| `static void parse(String, org.xml.sax.ContentHandler) throws org.xml.sax.SAXException` |  |
| `static void parse(java.io.Reader, org.xml.sax.ContentHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |
| `static void parse(java.io.InputStream, android.util.Xml.Encoding, org.xml.sax.ContentHandler) throws java.io.IOException, org.xml.sax.SAXException` |  |

---

### `enum Xml.Encoding`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.util.Xml.Encoding ISO_8859_1` |  |
| `static final android.util.Xml.Encoding US_ASCII` |  |
| `static final android.util.Xml.Encoding UTF_16` |  |
| `static final android.util.Xml.Encoding UTF_8` |  |

---

## Package: `android.util.proto`

### `class final ProtoOutputStream`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ProtoOutputStream()` |  |
| `ProtoOutputStream(int)` |  |
| `ProtoOutputStream(@NonNull java.io.OutputStream)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final long FIELD_COUNT_MASK = 16492674416640L` |  |
| `static final long FIELD_COUNT_PACKED = 5497558138880L` |  |
| `static final long FIELD_COUNT_REPEATED = 2199023255552L` |  |
| `static final int FIELD_COUNT_SHIFT = 40` |  |
| `static final long FIELD_COUNT_SINGLE = 1099511627776L` |  |
| `static final long FIELD_COUNT_UNKNOWN = 0L` |  |
| `static final int FIELD_ID_SHIFT = 3` |  |
| `static final long FIELD_TYPE_BOOL = 34359738368L` |  |
| `static final long FIELD_TYPE_BYTES = 51539607552L` |  |
| `static final long FIELD_TYPE_DOUBLE = 4294967296L` |  |
| `static final long FIELD_TYPE_ENUM = 60129542144L` |  |
| `static final long FIELD_TYPE_FIXED32 = 30064771072L` |  |
| `static final long FIELD_TYPE_FIXED64 = 25769803776L` |  |
| `static final long FIELD_TYPE_FLOAT = 8589934592L` |  |
| `static final long FIELD_TYPE_INT32 = 21474836480L` |  |
| `static final long FIELD_TYPE_INT64 = 12884901888L` |  |
| `static final long FIELD_TYPE_MASK = 1095216660480L` |  |
| `static final long FIELD_TYPE_MESSAGE = 47244640256L` |  |
| `static final long FIELD_TYPE_SFIXED32 = 64424509440L` |  |
| `static final long FIELD_TYPE_SFIXED64 = 68719476736L` |  |
| `static final int FIELD_TYPE_SHIFT = 32` |  |
| `static final long FIELD_TYPE_SINT32 = 73014444032L` |  |
| `static final long FIELD_TYPE_SINT64 = 77309411328L` |  |
| `static final long FIELD_TYPE_STRING = 38654705664L` |  |
| `static final long FIELD_TYPE_UINT32 = 55834574848L` |  |
| `static final long FIELD_TYPE_UINT64 = 17179869184L` |  |
| `static final int WIRE_TYPE_END_GROUP = 4` |  |
| `static final int WIRE_TYPE_FIXED32 = 5` |  |
| `static final int WIRE_TYPE_FIXED64 = 1` |  |
| `static final int WIRE_TYPE_LENGTH_DELIMITED = 2` |  |
| `static final int WIRE_TYPE_MASK = 7` |  |
| `static final int WIRE_TYPE_START_GROUP = 3` |  |
| `static final int WIRE_TYPE_VARINT = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int checkFieldId(long, long)` |  |
| `void dump(@NonNull String)` |  |
| `void end(long)` |  |
| `void flush()` |  |
| `int getRawSize()` |  |
| `static long makeFieldId(int, long)` |  |
| `static long makeToken(int, boolean, int, int, int)` |  |
| `long start(long)` |  |
| `void write(long, double)` |  |
| `void write(long, float)` |  |
| `void write(long, int)` |  |
| `void write(long, long)` |  |
| `void write(long, boolean)` |  |
| `void write(long, @Nullable String)` |  |
| `void write(long, @Nullable byte[])` |  |
| `void writeTag(int, int)` |  |

---

