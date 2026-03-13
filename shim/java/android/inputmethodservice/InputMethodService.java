package android.inputmethodservice;

public class InputMethodService {
    public InputMethodService() {}

    public static final int BACK_DISPOSITION_ADJUST_NOTHING = 0;
    public static final int BACK_DISPOSITION_DEFAULT = 0;
    public int getBackDisposition() { return 0; }
    public int getCandidatesHiddenVisibility() { return 0; }
    public Object getCurrentInputBinding() { return null; }
    public Object getCurrentInputConnection() { return null; }
    public Object getCurrentInputEditorInfo() { return null; }
    public boolean getCurrentInputStarted() { return false; }
    public Object getLayoutInflater() { return null; }
    public int getMaxWidth() { return 0; }
    public Object getTextForImeAction(Object p0) { return null; }
    public Object getWindow() { return null; }
    public void hideStatusIcon() {}
    public void hideWindow() {}
    public boolean isExtractViewShown() { return false; }
    public boolean isFullscreenMode() { return false; }
    public boolean isInputViewShown() { return false; }
    public boolean isShowInputRequested() { return false; }
    public void onAppPrivateCommand(Object p0, Object p1) {}
    public void onBindInput() {}
    public void onComputeInsets(Object p0) {}
    public void onConfigureWindow(Object p0, Object p1, Object p2) {}
    public Object onCreateCandidatesView() { return null; }
    public Object onCreateExtractTextView() { return null; }
    public Object onCreateInputMethodInterface() { return null; }
    public Object onCreateInputMethodSessionInterface() { return null; }
    public Object onCreateInputView() { return null; }
    public void onCurrentInputMethodSubtypeChanged(Object p0) {}
    public void onDisplayCompletions(Object p0) {}
    public boolean onEvaluateFullscreenMode() { return false; }
    public boolean onExtractTextContextMenuItem(Object p0) { return false; }
    public void onExtractedCursorMovement(Object p0, Object p1) {}
    public void onExtractedSelectionChanged(Object p0, Object p1) {}
    public void onExtractedTextClicked() {}
    public void onExtractingInputChanged(Object p0) {}
    public void onFinishCandidatesView(Object p0) {}
    public void onFinishInput() {}
    public void onFinishInputView(Object p0) {}
    public void onInitializeInterface() {}
    public boolean onInlineSuggestionsResponse(Object p0) { return false; }
    public boolean onKeyDown(Object p0, Object p1) { return false; }
    public boolean onKeyLongPress(Object p0, Object p1) { return false; }
    public boolean onKeyMultiple(Object p0, Object p1, Object p2) { return false; }
    public boolean onKeyUp(Object p0, Object p1) { return false; }
    public boolean onShowInputRequested(Object p0, Object p1) { return false; }
    public void onStartCandidatesView(Object p0, Object p1) {}
    public void onStartInput(Object p0, Object p1) {}
    public void onStartInputView(Object p0, Object p1) {}
    public void onUnbindInput() {}
    public void onUpdateCursorAnchorInfo(Object p0) {}
    public void onUpdateExtractedText(Object p0, Object p1) {}
    public void onUpdateExtractingViews(Object p0) {}
    public void onUpdateExtractingVisibility(Object p0) {}
    public void onUpdateSelection(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void onWindowHidden() {}
    public void onWindowShown() {}
    public void requestHideSelf(Object p0) {}
    public void requestShowSelf(Object p0) {}
    public boolean sendDefaultEditorAction(Object p0) { return false; }
    public void sendDownUpKeyEvents(Object p0) {}
    public void sendKeyChar(Object p0) {}
    public void setBackDisposition(Object p0) {}
    public void setCandidatesView(Object p0) {}
    public void setCandidatesViewShown(Object p0) {}
    public void setExtractView(Object p0) {}
    public void setExtractViewShown(Object p0) {}
    public void setInputView(Object p0) {}
    public boolean shouldOfferSwitchingToNextInputMethod() { return false; }
    public void showStatusIcon(Object p0) {}
    public void showWindow(Object p0) {}
    public void switchInputMethod(Object p0) {}
    public void switchInputMethod(Object p0, Object p1) {}
    public boolean switchToNextInputMethod(Object p0) { return false; }
    public boolean switchToPreviousInputMethod() { return false; }
    public void updateFullscreenMode() {}
    public void updateInputViewShown() {}
}
