package android.icu.text;
import android.net.ParseException;
import android.net.ParseException;

public class StringPrepParseException extends ParseException {
    public static final int ACE_PREFIX_ERROR = 0;
    public static final int BUFFER_OVERFLOW_ERROR = 0;
    public static final int CHECK_BIDI_ERROR = 0;
    public static final int DOMAIN_NAME_TOO_LONG_ERROR = 0;
    public static final int ILLEGAL_CHAR_FOUND = 0;
    public static final int INVALID_CHAR_FOUND = 0;
    public static final int LABEL_TOO_LONG_ERROR = 0;
    public static final int PROHIBITED_ERROR = 0;
    public static final int STD3_ASCII_RULES_ERROR = 0;
    public static final int UNASSIGNED_ERROR = 0;
    public static final int VERIFICATION_ERROR = 0;
    public static final int ZERO_LENGTH_LABEL = 0;

    public StringPrepParseException(String p0, int p1) {}
    public StringPrepParseException(String p0, int p1, String p2, int p3) {}
    public StringPrepParseException(String p0, int p1, String p2, int p3, int p4) {}

    public int getError() { return 0; }
}
