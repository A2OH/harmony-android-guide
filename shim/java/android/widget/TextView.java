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
    private int mInputType;

    public TextView(android.content.Context context) {}
    public TextView(int nodeType) { super(nodeType); }
    public TextView() {}

    // ── Properly-typed API used by tests ──
    public void setText(CharSequence text) {
        mText = text != null ? text : "";
        if (nativeHandle != 0 && mText != null) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrString(nativeHandle, ATTR_TEXT_CONTENT, mText.toString());
        }
    }
    public CharSequence getText() { return mText; }

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
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_TEXT_SIZE, size, 0, 0, 0, 1);
        }
    }
    public float getTextSize() { return mTextSize; }

    public void setMaxLines(int maxLines) { mMaxLines = maxLines; }
    public int getMaxLines() { return mMaxLines; }

    public void setSingleLine(boolean single) { mMaxLines = single ? 1 : Integer.MAX_VALUE; }

    public void setInputType(int type) { mInputType = type; }
    public int getInputType() { return mInputType; }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if (mText != null && mText.length() > 0) {
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setColor(mTextColor != 0 ? mTextColor : 0xFF000000);
            float ts = mTextSize > 0 ? mTextSize : 14;
            paint.setTextSize(ts);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // Use FontMetrics for proper baseline positioning
            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float x = getPaddingLeft();
            float y = getPaddingTop() + (-fm.ascent); // baseline = padding + |ascent|
            canvas.drawText(mText.toString(), x, y, paint);
        }
    }

    public void setGravity(int gravity) {}

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
            mTextWatcher.onTextChanged(mText, 0, 0, mText.length());
        }
        super.onNativeEvent(eventType, eventCode, data);
    }

    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 0;
    public void addTextChangedListener(Object p0) { if (p0 instanceof TextWatcher) mTextWatcher = (TextWatcher) p0; }
    public void append(Object p0) {}
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
    public int getCompoundDrawablePadding() { return 0; }
    public Object getCompoundDrawableTintList() { return null; }
    public Object getCompoundDrawableTintMode() { return null; }
    public int getCompoundPaddingBottom() { return 0; }
    public int getCompoundPaddingEnd() { return 0; }
    public int getCompoundPaddingLeft() { return 0; }
    public int getCompoundPaddingRight() { return 0; }
    public int getCompoundPaddingStart() { return 0; }
    public int getCompoundPaddingTop() { return 0; }
    public Object getCustomInsertionActionModeCallback() { return null; }
    public Object getCustomSelectionActionModeCallback() { return null; }
    public boolean getDefaultEditable() { return false; }
    public Object getDefaultMovementMethod() { return null; }
    public Object getEditableText() { return null; }
    public Object getError() { return null; }
    public int getExtendedPaddingBottom() { return 0; }
    public int getExtendedPaddingTop() { return 0; }
    public Object getFilters() { return null; }
    public int getFirstBaselineToTopHeight() { return 0; }
    public boolean getFreezesText() { return false; }
    public int getGravity() { return 0; }
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
    public Object getLayout() { return null; }
    public float getLetterSpacing() { return 0f; }
    public int getLineBounds(Object p0, Object p1) { return 0; }
    public int getLineCount() { return 0; }
    public int getLineHeight() { return 0; }
    public float getLineSpacingExtra() { return 0f; }
    public float getLineSpacingMultiplier() { return 0f; }
    public Object getLinkTextColors() { return null; }
    public boolean getLinksClickable() { return false; }
    public int getMarqueeRepeatLimit() { return 0; }
    public int getMaxEms() { return 0; }
    public int getMaxHeight() { return 0; }
    // getMaxLines() defined above with mMaxLines
    public int getMaxWidth() { return 0; }
    public int getMinEms() { return 0; }
    public int getMinHeight() { return 0; }
    public int getMinLines() { return 0; }
    public int getMinWidth() { return 0; }
    public Object getMovementMethod() { return null; }
    public int getOffsetForPosition(Object p0, Object p1) { return 0; }
    public Object getPaint() { return null; }
    public int getPaintFlags() { return 0; }
    public Object getPrivateImeOptions() { return null; }
    public float getShadowDx() { return 0f; }
    public float getShadowDy() { return 0f; }
    public float getShadowRadius() { return 0f; }
    public boolean getShowSoftInputOnFocus() { return false; }
    public Object getTextColors() { return null; }
    public float getTextScaleX() { return 0f; }
    public int getTextSizeUnit() { return 0; }
    public int getTotalPaddingBottom() { return 0; }
    public int getTotalPaddingEnd() { return 0; }
    public int getTotalPaddingLeft() { return 0; }
    public int getTotalPaddingRight() { return 0; }
    public int getTotalPaddingStart() { return 0; }
    public int getTotalPaddingTop() { return 0; }
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
    public boolean isSingleLine() { return false; }
    public boolean isSuggestionsEnabled() { return false; }
    public boolean isTextSelectable() { return false; }
    public int length() { return 0; }
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
    public void setEllipsize(Object p0) {}
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
    public void setGravity(Object p0) {}
    public void setHeight(Object p0) {}
    public void setHighlightColor(Object p0) {}
    public void setHint(Object p0) {}
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
    public void setLineSpacing(Object p0, Object p1) {}
    public void setLines(Object p0) {}
    public void setLinkTextColor(Object p0) {}
    public void setLinksClickable(Object p0) {}
    public void setMarqueeRepeatLimit(Object p0) {}
    public void setMaxEms(Object p0) {}
    public void setMaxHeight(Object p0) {}
    public void setMaxLines(Object p0) {}
    public void setMaxWidth(Object p0) {}
    public void setMinEms(Object p0) {}
    public void setMinHeight(Object p0) {}
    public void setMinLines(Object p0) {}
    public void setMinWidth(Object p0) {}
    public void setMovementMethod(Object p0) {}
    public void setOnEditorActionListener(Object p0) {}
    public void setPaintFlags(Object p0) {}
    public void setPrivateImeOptions(Object p0) {}
    public void setRawInputType(Object p0) {}
    public void setScroller(Object p0) {}
    public void setSelectAllOnFocus(Object p0) {}
    public void setShadowLayer(Object p0, Object p1, Object p2, Object p3) {}
    public void setShowSoftInputOnFocus(Object p0) {}
    public void setSingleLine() {}
    public void setSingleLine(Object p0) {}
    public void setSpannableFactory(Object p0) {}
    public void setText(Object p0) {}
    public void setText(Object p0, Object p1) {}
    public void setText(Object p0, Object p1, Object p2) {}
    public void setTextAppearance(Object p0) {}
    public void setTextClassifier(Object p0) {}
    public void setTextColor(Object p0) {}
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
    public void setTextSize(Object p0) {}
    public void setTextSize(Object p0, Object p1) {}
    public void setTransformationMethod(Object p0) {}
    public void setTypeface(Object p0, Object p1) {}
    public void setTypeface(Object p0) {}
    public void setWidth(Object p0) {}
}
