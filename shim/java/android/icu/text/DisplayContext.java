package android.icu.text;
import android.renderscript.Type;
import android.renderscript.Type;

public enum DisplayContext {
    CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE,
    CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE,
    CAPITALIZATION_FOR_STANDALONE,
    CAPITALIZATION_FOR_UI_LIST_OR_MENU,
    CAPITALIZATION_NONE,
    DIALECT_NAMES,
    LENGTH_FULL,
    LENGTH_SHORT,
    NO_SUBSTITUTE,
    STANDARD_NAMES,
    SUBSTITUTE;
    public Type type() { return null; }
    public int value() { return 0; }
}
