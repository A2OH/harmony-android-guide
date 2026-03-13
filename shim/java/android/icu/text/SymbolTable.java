package android.icu.text;

public interface SymbolTable {

    public static final int SYMBOL_REF = 0;
    char lookup(Object p0);
    Object lookupMatcher(Object p0);
    Object parseReference(Object p0, Object p1, Object p2);
}
