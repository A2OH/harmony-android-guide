package android.view.autofill;
import android.renderscript.Type;
import android.renderscript.Type;

/**
 * Android-compatible AutofillValue shim. Represents the value of a field to autofill.
 */
public final class AutofillValue {

    private static final int TYPE_TEXT   = 1;
    private static final int TYPE_TOGGLE = 2;
    private static final int TYPE_LIST   = 3;
    private static final int TYPE_DATE   = 4;

    private final int    mType;
    private final Object mValue;

    private AutofillValue(int type, Object value) {
        mType  = type;
        mValue = value;
    }

    // --- Factory methods ---

    public static AutofillValue forText(CharSequence value) {
        return new AutofillValue(TYPE_TEXT, value);
    }

    public static AutofillValue forToggle(boolean value) {
        return new AutofillValue(TYPE_TOGGLE, value);
    }

    public static AutofillValue forList(int selectedIndex) {
        return new AutofillValue(TYPE_LIST, selectedIndex);
    }

    public static AutofillValue forDate(long value) {
        return new AutofillValue(TYPE_DATE, value);
    }

    // --- Type checks ---

    public boolean isText()   { return mType == TYPE_TEXT; }
    public boolean isToggle() { return mType == TYPE_TOGGLE; }
    public boolean isList()   { return mType == TYPE_LIST; }
    public boolean isDate()   { return mType == TYPE_DATE; }

    // --- Accessors ---

    public CharSequence getTextValue() {
        if (!isText()) throw new IllegalStateException("Not a text value");
        return (CharSequence) mValue;
    }

    public boolean getToggleValue() {
        if (!isToggle()) throw new IllegalStateException("Not a toggle value");
        return (Boolean) mValue;
    }

    public int getListValue() {
        if (!isList()) throw new IllegalStateException("Not a list value");
        return (Integer) mValue;
    }

    public long getDateValue() {
        if (!isDate()) throw new IllegalStateException("Not a date value");
        return (Long) mValue;
    }
}
