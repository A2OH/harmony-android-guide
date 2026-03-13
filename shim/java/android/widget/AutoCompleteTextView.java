package android.widget;

/**
 * Android-compatible AutoCompleteTextView shim. Pure Java stub for compilation.
 */
public class AutoCompleteTextView extends EditText {
    public AutoCompleteTextView() { super(); }

    public void setAdapter(ListAdapter adapter) {}
    public void setThreshold(int threshold)     {}
    public int  getThreshold()                  { return 2; }
    public void setDropDownWidth(int width)     {}
    public void setDropDownHeight(int height)   {}
    public void showDropDown()                  {}
    public void dismissDropDown()               {}
    public boolean isPopupShowing()             { return false; }
    public void setOnItemClickListener(android.widget.AdapterView.OnItemClickListener l) {}
    public void setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener l) {}
    public void performCompletion()             {}
    public void clearListSelection()            {}
    public void setCompletionHint(CharSequence hint) {}
}
