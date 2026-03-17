package android.widget;

public class TextView extends android.view.View {
    private static final int NODE_TYPE_TEXT = 6;
    private static final int ATTR_TEXT_CONTENT = 6000;
    private static final int ATTR_TEXT_COLOR = 6001;
    private static final int ATTR_TEXT_SIZE = 6002;

    private CharSequence mText = "";
    private CharSequence mHint = "";
    private int mTextColor;
    private float mTextSize;
    private int mMaxLines = Integer.MAX_VALUE;
    private int mMinLines = 0;
    private int mInputType;
    private int mGravity = android.view.Gravity.TOP | android.view.Gravity.LEFT;
    private android.text.TextUtils.TruncateAt mEllipsize;
    private float mLineSpacingMult = 1.0f;
    private float mLineSpacingAdd = 0.0f;
    private float mShadowRadius;
    private float mShadowDx;
    private float mShadowDy;
    private int mShadowColor;
    private int mHighlightColor;
    private boolean mSingleLine;
    private android.graphics.drawable.Drawable mDrawableLeft;
    private android.graphics.drawable.Drawable mDrawableTop;
    private android.graphics.drawable.Drawable mDrawableRight;
    private android.graphics.drawable.Drawable mDrawableBottom;
    private int mCompoundDrawablePadding;
    private android.text.StaticLayout mLayout;

    public enum BufferType { NORMAL, SPANNABLE, EDITABLE }

    public TextView(android.content.Context context) {}
    public TextView(int nodeType) { super(nodeType); }
    public TextView() {}

    // ── Properly-typed API used by tests ──
    public void setText(int resId) {
        android.content.res.Resources res = android.content.res.Resources.getSystem();
        setText(res != null ? res.getString(resId) : "res_" + resId);
    }

    public void setText(CharSequence text) {
        mText = text != null ? text : "";
        mLayout = null;
        if (nativeHandle != 0 && mText != null) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_CONTENT, mText.toString());
        }
        requestLayout();
        invalidate();
    }
    public void setText(CharSequence text, BufferType type) { setText(text); }
    public CharSequence getText() { return mText; }

    public void setHint(int resId) {
        android.content.res.Resources res = android.content.res.Resources.getSystem();
        setHint(res != null ? res.getString(resId) : "");
    }
    public void setHint(CharSequence hint) { mHint = hint != null ? hint : ""; }
    public CharSequence getHint() { return mHint; }

    public void setTextColor(int color) {
        mTextColor = color;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrColor(nativeHandle, ATTR_TEXT_COLOR, color);
        }
    }
    public int getCurrentTextColor() { return mTextColor; }

    public void setTextSize(float size) {
        mTextSize = size;
        mLayout = null;
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_TEXT_SIZE, size, 0, 0, 0, 1);
        }
    }
    public float getTextSize() { return mTextSize; }

    public void setMaxLines(int maxLines) { mMaxLines = maxLines; mLayout = null; }
    public int getMaxLines() { return mMaxLines; }

    public void setMinLines(int minLines) { mMinLines = minLines; }

    public void setSingleLine(boolean single) {
        mSingleLine = single;
        mMaxLines = single ? 1 : Integer.MAX_VALUE;
        mLayout = null;
    }

    public void setInputType(int type) { mInputType = type; }
    public int getInputType() { return mInputType; }

    public void setEllipsize(android.text.TextUtils.TruncateAt where) { mEllipsize = where; }
    public android.text.TextUtils.TruncateAt getEllipsize() { return mEllipsize; }

    public void setLineSpacing(float add, float mult) {
        mLineSpacingAdd = add; mLineSpacingMult = mult; mLayout = null;
    }

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
        mShadowRadius = radius; mShadowDx = dx; mShadowDy = dy; mShadowColor = shadowColor;
    }

    public void setCompoundDrawables(android.graphics.drawable.Drawable left,
            android.graphics.drawable.Drawable top,
            android.graphics.drawable.Drawable right,
            android.graphics.drawable.Drawable bottom) {
        mDrawableLeft = left; mDrawableTop = top; mDrawableRight = right; mDrawableBottom = bottom;
    }
    public android.graphics.drawable.Drawable[] getCompoundDrawables() {
        return new android.graphics.drawable.Drawable[] { mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom };
    }
    public void setCompoundDrawablePadding(int pad) { mCompoundDrawablePadding = pad; }

    private android.graphics.Paint makePaint() {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTextSize(mTextSize > 0 ? mTextSize : 14);
        return paint;
    }
    private int calcLineHeight() {
        android.graphics.Paint paint = makePaint();
        android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.round((-fm.ascent + fm.descent + fm.leading) * mLineSpacingMult + mLineSpacingAdd);
    }
    private int calcCompoundPaddingLeft() {
        int pad = getPaddingLeft();
        if (mDrawableLeft != null) pad += mDrawableLeft.getIntrinsicWidth() + mCompoundDrawablePadding;
        return pad;
    }
    private int calcCompoundPaddingRight() {
        int pad = getPaddingRight();
        if (mDrawableRight != null) pad += mDrawableRight.getIntrinsicWidth() + mCompoundDrawablePadding;
        return pad;
    }
    private int calcCompoundPaddingTop() {
        int pad = getPaddingTop();
        if (mDrawableTop != null) pad += mDrawableTop.getIntrinsicHeight() + mCompoundDrawablePadding;
        return pad;
    }
    private int calcCompoundPaddingBottom() {
        int pad = getPaddingBottom();
        if (mDrawableBottom != null) pad += mDrawableBottom.getIntrinsicHeight() + mCompoundDrawablePadding;
        return pad;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        android.graphics.Paint paint = makePaint();
        int lineHeight = calcLineHeight();
        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);
        int hPad = calcCompoundPaddingLeft() + calcCompoundPaddingRight();
        int vPad = calcCompoundPaddingTop() + calcCompoundPaddingBottom();
        int availWidth;
        if (wMode == android.view.View.MeasureSpec.UNSPECIFIED) {
            availWidth = Integer.MAX_VALUE;
        } else {
            availWidth = wSize - hPad;
            if (availWidth < 0) availWidth = 0;
        }
        int layoutWidth = mSingleLine ? Integer.MAX_VALUE : Math.max(1, availWidth);
        mLayout = new android.text.StaticLayout(mText, paint, layoutWidth,
            android.text.Layout.Alignment.ALIGN_NORMAL, mLineSpacingMult, mLineSpacingAdd);
        int lineCount = mLayout.getLineCount();
        int visibleLines = lineCount;
        if (visibleLines > mMaxLines) visibleLines = mMaxLines;
        if (visibleLines < mMinLines) visibleLines = mMinLines;
        float maxLineWidth = 0;
        for (int i = 0; i < Math.min(visibleLines, lineCount); i++) {
            float lw = mLayout.getLineWidth(i);
            if (lw > maxLineWidth) maxLineWidth = lw;
        }
        int textWidth = (int) Math.ceil(maxLineWidth) + hPad;
        int textHeight = visibleLines * lineHeight + vPad;
        int measuredW;
        if (wMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredW = wSize;
        } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredW = Math.min(textWidth, wSize);
        } else {
            measuredW = textWidth;
        }
        int measuredH;
        if (hMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredH = hSize;
        } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredH = Math.min(textHeight, hSize);
        } else {
            measuredH = textHeight;
        }
        setMeasuredDimension(measuredW, measuredH);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if (mText == null || mText.length() == 0) return;
        android.graphics.Paint paint = makePaint();
        paint.setColor(mTextColor != 0 ? mTextColor : 0xFF000000);
        paint.setStyle(android.graphics.Paint.Style.FILL);
        android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
        int lineHeight = calcLineHeight();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int cpLeft = calcCompoundPaddingLeft();
        int cpRight = calcCompoundPaddingRight();
        int cpTop = calcCompoundPaddingTop();
        int cpBottom = calcCompoundPaddingBottom();
        int availW = viewWidth - cpLeft - cpRight;
        int availH = viewHeight - cpTop - cpBottom;
        String drawText = mText.toString();
        if (mSingleLine && mEllipsize != null && availW > 0) {
            android.text.TextPaint tp = new android.text.TextPaint(paint);
            CharSequence ellipsized = android.text.TextUtils.ellipsize(drawText, tp, (float) availW, mEllipsize);
            if (ellipsized != null) drawText = ellipsized.toString();
        }
        if (mLayout == null) {
            // If view not yet measured (width==0), use MAX_VALUE so text draws on one line
            int lw = (mSingleLine || availW <= 0) ? Integer.MAX_VALUE : availW;
            mLayout = new android.text.StaticLayout(drawText, paint, lw,
                android.text.Layout.Alignment.ALIGN_NORMAL, mLineSpacingMult, mLineSpacingAdd);
        }
        int lineCount = mLayout.getLineCount();
        int visibleLines = lineCount;
        if (visibleLines > mMaxLines) visibleLines = mMaxLines;
        int totalTextHeight = visibleLines * lineHeight;
        float yStart = cpTop;
        int vGrav = mGravity & android.view.Gravity.VERTICAL_GRAVITY_MASK;
        if (vGrav == android.view.Gravity.BOTTOM) {
            yStart = viewHeight - cpBottom - totalTextHeight;
        } else if (vGrav == android.view.Gravity.CENTER_VERTICAL
                || (mGravity & android.view.Gravity.CENTER) == android.view.Gravity.CENTER) {
            yStart = cpTop + (availH - totalTextHeight) / 2.0f;
        }
        int hGrav = mGravity & android.view.Gravity.HORIZONTAL_GRAVITY_MASK;
        String fullText = drawText;
        for (int i = 0; i < visibleLines; i++) {
            int lineStart = mLayout.getLineStart(i);
            int lineEnd = mLayout.getLineEnd(i);
            if (lineEnd > fullText.length()) lineEnd = fullText.length();
            if (lineStart > fullText.length()) break;
            String lineText = fullText.substring(lineStart, lineEnd);
            if (lineText.endsWith("\n")) lineText = lineText.substring(0, lineText.length() - 1);
            float lineW = paint.measureText(lineText);
            float x;
            if (hGrav == android.view.Gravity.RIGHT) {
                x = cpLeft + availW - lineW;
            } else if (hGrav == android.view.Gravity.CENTER_HORIZONTAL
                    || (mGravity & android.view.Gravity.CENTER) == android.view.Gravity.CENTER) {
                x = cpLeft + (availW - lineW) / 2.0f;
            } else {
                x = cpLeft;
            }
            float y = yStart + i * lineHeight + (-fm.ascent);
            if (i == visibleLines - 1 && visibleLines < lineCount
                    && mEllipsize == android.text.TextUtils.TruncateAt.END) {
                android.text.TextPaint tp = new android.text.TextPaint(paint);
                CharSequence ellipsized = android.text.TextUtils.ellipsize(lineText, tp, (float) availW, mEllipsize);
                if (ellipsized != null) lineText = ellipsized.toString();
            }
            canvas.drawText(lineText, x, y, paint);
        }
        if (mDrawableLeft != null) mDrawableLeft.draw(canvas);
        if (mDrawableRight != null) mDrawableRight.draw(canvas);
        if (mDrawableTop != null) mDrawableTop.draw(canvas);
        if (mDrawableBottom != null) mDrawableBottom.draw(canvas);
    }

    public void setGravity(int gravity) { mGravity = gravity; }

    public interface TextWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
        void onTextChanged(CharSequence s, int start, int before, int count);
        void afterTextChanged(Object s);
    }

    private TextWatcher mTextWatcher;

    public void addTextChangedListener(TextWatcher watcher) { mTextWatcher = watcher; }

    @Override
    public void onNativeEvent(int eventType, int eventCode, String data) {
        if (eventCode == 7000 && mTextWatcher != null) {
            mText = data != null ? data : "";
            mLayout = null;
            mTextWatcher.onTextChanged(mText, 0, 0, mText.length());
        }
        super.onNativeEvent(eventType, eventCode, data);
    }

    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 0;
    public void addTextChangedListener(Object p0) { if (p0 instanceof TextWatcher) mTextWatcher = (TextWatcher) p0; }
    public void append(CharSequence text) {
        if (text != null) setText(mText.toString() + text.toString());
    }
    public void append(CharSequence text, int start, int end) {
        if (text != null) setText(mText.toString() + text.subSequence(start, end).toString());
    }
    public void append(Object p0) {
        if (p0 instanceof CharSequence) append((CharSequence) p0);
    }
    public void append(Object p0, Object p1, Object p2) {}
    public void beginBatchEdit() {}
    public boolean bringPointIntoView(Object p0) { return false; }
    public void clearComposingText() {}
    public void debug(Object p0) {}
    public boolean didTouchFocusSelect() { return false; }
    public void endBatchEdit() {}
    public boolean extractText(Object p0, Object p1) { return false; }
    public int getAutoLinkMask() { return 0; }
    public int getAutoSizeMaxTextSize() { return 0; }
    public int getAutoSizeMinTextSize() { return 0; }
    public int getAutoSizeStepGranularity() { return 0; }
    public int getAutoSizeTextAvailableSizes() { return 0; }
    public int getAutoSizeTextType() { return 0; }
    public int getBreakStrategy() { return 0; }
    public int getCompoundDrawablePadding() { return mCompoundDrawablePadding; }
    public Object getCompoundDrawableTintList() { return null; }
    public Object getCompoundDrawableTintMode() { return null; }
    public int getCompoundPaddingBottom() { return calcCompoundPaddingBottom(); }
    public int getCompoundPaddingEnd() { return calcCompoundPaddingRight(); }
    public int getCompoundPaddingLeft() { return calcCompoundPaddingLeft(); }
    public int getCompoundPaddingRight() { return calcCompoundPaddingRight(); }
    public int getCompoundPaddingStart() { return calcCompoundPaddingLeft(); }
    public int getCompoundPaddingTop() { return calcCompoundPaddingTop(); }
    public Object getCustomInsertionActionModeCallback() { return null; }
    public Object getCustomSelectionActionModeCallback() { return null; }
    public boolean getDefaultEditable() { return false; }
    public Object getDefaultMovementMethod() { return null; }
    public Object getEditableText() { return null; }
    public Object getError() { return null; }
    public int getExtendedPaddingBottom() { return calcCompoundPaddingBottom(); }
    public int getExtendedPaddingTop() { return calcCompoundPaddingTop(); }
    public Object getFilters() { return null; }
    public int getFirstBaselineToTopHeight() { return 0; }
    public boolean getFreezesText() { return false; }
    public int getGravity() { return mGravity; }
    public Object getHintTextColors() { return null; }
    public int getHyphenationFrequency() { return 0; }
    public int getImeActionId() { return 0; }
    public Object getImeActionLabel() { return null; }
    public int getImeOptions() { return 0; }
    public boolean getIncludeFontPadding() { return false; }
    public Object getInputExtras(Object p0) { return null; }
    // getInputType() defined above with mInputType
    public int getJustificationMode() { return 0; }
    public Object getKeyListener() { return null; }
    public int getLastBaselineToBottomHeight() { return 0; }
    public android.text.Layout getLayout() { return mLayout; }
    public float getLetterSpacing() { return 0f; }
    public int getLineBounds(Object p0, Object p1) { return 0; }
    public int getLineCount() {
        if (mText == null || mText.length() == 0) return 0;
        if (mLayout != null) return mLayout.getLineCount();
        String s = mText.toString();
        int count = 1;
        int idx = 0;
        while (true) { idx = s.indexOf('\n', idx); if (idx < 0) break; count++; idx++; }
        return count;
    }
    public int getLineHeight() { return calcLineHeight(); }
    public float getLineSpacingExtra() { return mLineSpacingAdd; }
    public float getLineSpacingMultiplier() { return mLineSpacingMult; }
    public Object getLinkTextColors() { return null; }
    public boolean getLinksClickable() { return false; }
    public int getMarqueeRepeatLimit() { return 0; }
    public int getMaxEms() { return 0; }
    public int getMaxHeight() { return 0; }
    // getMaxLines() defined above with mMaxLines
    public int getMaxWidth() { return 0; }
    public int getMinEms() { return 0; }
    public int getMinHeight() { return 0; }
    public int getMinLines() { return mMinLines; }
    public int getMinWidth() { return 0; }
    public Object getMovementMethod() { return null; }
    public int getOffsetForPosition(Object p0, Object p1) { return 0; }
    public android.graphics.Paint getPaint() { return makePaint(); }
    public int getPaintFlags() { return 0; }
    public Object getPrivateImeOptions() { return null; }
    public float getShadowDx() { return mShadowDx; }
    public float getShadowDy() { return mShadowDy; }
    public float getShadowRadius() { return mShadowRadius; }
    public int getShadowColor() { return mShadowColor; }
    public boolean getShowSoftInputOnFocus() { return false; }
    public Object getTextColors() { return null; }
    public float getTextScaleX() { return 0f; }
    public int getTextSizeUnit() { return 0; }
    public int getTotalPaddingBottom() { return calcCompoundPaddingBottom(); }
    public int getTotalPaddingEnd() { return calcCompoundPaddingRight(); }
    public int getTotalPaddingLeft() { return calcCompoundPaddingLeft(); }
    public int getTotalPaddingRight() { return calcCompoundPaddingRight(); }
    public int getTotalPaddingStart() { return calcCompoundPaddingLeft(); }
    public int getTotalPaddingTop() { return calcCompoundPaddingTop(); }
    public Object getTransformationMethod() { return null; }
    public Object getTypeface() { return null; }
    public Object getUrls() { return null; }
    public boolean hasSelection() { return false; }
    public boolean isAllCaps() { return false; }
    public boolean isCursorVisible() { return false; }
    public boolean isElegantTextHeight() { return false; }
    public boolean isFallbackLineSpacing() { return false; }
    public boolean isHorizontallyScrollable() { return false; }
    public boolean isInputMethodTarget() { return false; }
    public boolean isSingleLine() { return mSingleLine; }
    public boolean isSuggestionsEnabled() { return false; }
    public boolean isTextSelectable() { return false; }
    public int length() { return mText != null ? mText.length() : 0; }
    public boolean moveCursorToVisibleOffset() { return false; }
    public void onBeginBatchEdit() {}
    public void onCommitCompletion(Object p0) {}
    public void onCommitCorrection(Object p0) {}
    public void onEditorAction(Object p0) {}
    public void onEndBatchEdit() {}
    public boolean onPreDraw() { return false; }
    public boolean onPrivateIMECommand(Object p0, Object p1) { return false; }
    public void onRestoreInstanceState(Object p0) {}
    public Object onSaveInstanceState() { return null; }
    public void onTextChanged(Object p0, Object p1, Object p2, Object p3) {}
    public boolean onTextContextMenuItem(Object p0) { return false; }
    public void removeTextChangedListener(Object p0) {}
    public void setAllCaps(Object p0) {}
    public void setAutoLinkMask(Object p0) {}
    public void setAutoSizeTextTypeUniformWithConfiguration(Object p0, Object p1, Object p2, Object p3) {}
    public void setAutoSizeTextTypeUniformWithPresetSizes(Object p0, Object p1) {}
    public void setAutoSizeTextTypeWithDefaults(Object p0) {}
    public void setBreakStrategy(Object p0) {}
    public void setCompoundDrawablePadding(Object p0) {}
    public void setCompoundDrawableTintBlendMode(Object p0) {}
    public void setCompoundDrawableTintList(Object p0) {}
    public void setCompoundDrawableTintMode(Object p0) {}
    public void setCompoundDrawables(Object p0, Object p1, Object p2, Object p3) {}
    public void setCompoundDrawablesRelative(Object p0, Object p1, Object p2, Object p3) {}
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Object p0, Object p1, Object p2, Object p3) {}
    public void setCompoundDrawablesWithIntrinsicBounds(Object p0, Object p1, Object p2, Object p3) {}
    public void setCursorVisible(Object p0) {}
    public void setCustomInsertionActionModeCallback(Object p0) {}
    public void setCustomSelectionActionModeCallback(Object p0) {}
    public void setEditableFactory(Object p0) {}
    public void setElegantTextHeight(Object p0) {}
    public void setEllipsize(Object p0) {
        if (p0 instanceof android.text.TextUtils.TruncateAt) setEllipsize((android.text.TextUtils.TruncateAt) p0);
    }
    public void setEms(Object p0) {}
    public void setError(Object p0) {}
    public void setError(Object p0, Object p1) {}
    public void setExtractedText(Object p0) {}
    public void setFallbackLineSpacing(Object p0) {}
    public void setFilters(Object p0) {}
    public void setFirstBaselineToTopHeight(Object p0) {}
    public void setFontFeatureSettings(Object p0) {}
    public boolean setFontVariationSettings(Object p0) { return false; }
    public boolean setFrame(Object p0, Object p1, Object p2, Object p3) { return false; }
    public void setFreezesText(Object p0) {}
    public void setGravity(Object p0) {
        if (p0 instanceof Number) setGravity(((Number) p0).intValue());
    }
    public void setHeight(Object p0) {}
    public void setHighlightColor(Object p0) {
        if (p0 instanceof Number) mHighlightColor = ((Number) p0).intValue();
    }
    public void setHint(Object p0) {
        if (p0 instanceof CharSequence) setHint((CharSequence) p0);
    }
    public void setHintTextColor(Object p0) {}
    public void setHorizontallyScrolling(Object p0) {}
    public void setHyphenationFrequency(Object p0) {}
    public void setImeActionLabel(Object p0, Object p1) {}
    public void setImeHintLocales(Object p0) {}
    public void setImeOptions(Object p0) {}
    public void setIncludeFontPadding(Object p0) {}
    public void setInputExtras(Object p0) {}
    public void setInputType(Object p0) {}
    public void setJustificationMode(Object p0) {}
    public void setKeyListener(Object p0) {}
    public void setLastBaselineToBottomHeight(Object p0) {}
    public void setLetterSpacing(Object p0) {}
    public void setLineHeight(Object p0) {}
    public void setLineSpacing(Object p0, Object p1) {
        if (p0 instanceof Number && p1 instanceof Number) setLineSpacing(((Number) p0).floatValue(), ((Number) p1).floatValue());
    }
    public void setLines(Object p0) {}
    public void setLinkTextColor(Object p0) {}
    public void setLinksClickable(Object p0) {}
    public void setMarqueeRepeatLimit(Object p0) {}
    public void setMaxEms(Object p0) {}
    public void setMaxHeight(Object p0) {}
    public void setMaxLines(Object p0) {
        if (p0 instanceof Number) setMaxLines(((Number) p0).intValue());
    }
    public void setMaxWidth(Object p0) {}
    public void setMinEms(Object p0) {}
    public void setMinHeight(Object p0) {}
    public void setMinLines(Object p0) {
        if (p0 instanceof Number) setMinLines(((Number) p0).intValue());
    }
    public void setMinWidth(Object p0) {}
    public void setMovementMethod(Object p0) {}
    public void setOnEditorActionListener(Object p0) {}
    public void setPaintFlags(Object p0) {}
    public void setPrivateImeOptions(Object p0) {}
    public void setRawInputType(Object p0) {}
    public void setScroller(Object p0) {}
    public void setSelectAllOnFocus(Object p0) {}
    public void setShadowLayer(Object p0, Object p1, Object p2, Object p3) {
        if (p0 instanceof Number && p1 instanceof Number && p2 instanceof Number && p3 instanceof Number) {
            setShadowLayer(((Number) p0).floatValue(), ((Number) p1).floatValue(),
                           ((Number) p2).floatValue(), ((Number) p3).intValue());
        }
    }
    public void setShowSoftInputOnFocus(Object p0) {}
    public void setSingleLine() { setSingleLine(true); }
    public void setSingleLine(Object p0) {
        if (p0 instanceof Boolean) setSingleLine(((Boolean) p0).booleanValue());
    }
    public void setSpannableFactory(Object p0) {}
    public void setText(Object p0) {
        if (p0 instanceof CharSequence) setText((CharSequence) p0);
        else if (p0 != null) setText(p0.toString());
    }
    public void setText(Object p0, Object p1) {
        if (p0 instanceof CharSequence) setText((CharSequence) p0);
        else if (p0 != null) setText(p0.toString());
    }
    public void setText(Object p0, Object p1, Object p2) {}
    public void setTextAppearance(Object p0) {}
    public void setTextClassifier(Object p0) {}
    public void setTextColor(Object p0) {
        if (p0 instanceof Number) setTextColor(((Number) p0).intValue());
    }
    public void setTextCursorDrawable(Object p0) {}
    public void setTextIsSelectable(Object p0) {}
    public void setTextKeepState(Object p0) {}
    public void setTextKeepState(Object p0, Object p1) {}
    public void setTextLocale(Object p0) {}
    public void setTextLocales(Object p0) {}
    public void setTextMetricsParams(Object p0) {}
    public void setTextScaleX(Object p0) {}
    public void setTextSelectHandle(Object p0) {}
    public void setTextSelectHandleLeft(Object p0) {}
    public void setTextSelectHandleRight(Object p0) {}
    public void setTextSize(Object p0) {
        if (p0 instanceof Number) setTextSize(((Number) p0).floatValue());
    }
    public void setTextSize(Object p0, Object p1) {
        if (p1 instanceof Number) setTextSize(((Number) p1).floatValue());
    }
    public void setTransformationMethod(Object p0) {}
    public void setTypeface(Object p0, Object p1) {}
    public void setTypeface(Object p0) {}
    public void setWidth(Object p0) {}
}
