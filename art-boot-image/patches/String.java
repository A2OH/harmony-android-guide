package java.lang;
import java.io.Serializable;
import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Locale;

public final class String implements Serializable, Comparable<String>, CharSequence {
    private final int count;
    private int hash;
    private static final long serialVersionUID = -6849794470754667710L;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
    public static final Comparator<String> CASE_INSENSITIVE_ORDER = null;

    public String() { count = 0; }
    public String(String original) { count = 0; }
    public String(char[] value) { count = 0; }
    public String(char[] value, int offset, int count) { this.count = 0; }
    public String(int[] codePoints, int offset, int count) { this.count = 0; }
    public String(byte[] ascii, int hibyte, int offset, int count) { this.count = 0; }
    public String(byte[] ascii, int hibyte) { count = 0; }
    public String(byte[] bytes, int offset, int length, String charsetName) throws UnsupportedEncodingException { count = 0; }
    public String(byte[] bytes, int offset, int length, Charset charset) { count = 0; }
    public String(byte[] bytes, String charsetName) throws UnsupportedEncodingException { count = 0; }
    public String(byte[] bytes, Charset charset) { count = 0; }
    public String(byte[] bytes, int offset, int length) { count = 0; }
    public String(byte[] bytes) { count = 0; }
    public String(StringBuffer buffer) { count = 0; }
    public String(StringBuilder builder) { count = 0; }

    // 53 public virtual methods + 2 package-private = 55 + bridge + 2 defaults = 67 vtable
    public int length() { return 0; }
    public boolean isEmpty() { return true; }
    public native char charAt(int index);
    public int codePointAt(int index) { return 0; }
    public int codePointBefore(int index) { return 0; }
    public int codePointCount(int beginIndex, int endIndex) { return 0; }
    public int offsetByCodePoints(int index, int codePointOffset) { return 0; }
    void getChars(char[] dst, int dstBegin) {}
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {}
    native void getCharsNoCheck(int start, int end, char[] buffer, int index);
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {}
    public byte[] getBytes(String charsetName) throws UnsupportedEncodingException { return new byte[0]; }
    public byte[] getBytes(Charset charset) { return new byte[0]; }
    public byte[] getBytes() { return new byte[0]; }
    public boolean equals(Object anObject) { return this == anObject; }
    public boolean contentEquals(StringBuffer sb) { return false; }
    public boolean contentEquals(CharSequence cs) { return false; }
    public boolean equalsIgnoreCase(String anotherString) { return false; }
    public native int compareTo(String anotherString);
    public int compareToIgnoreCase(String str) { return 0; }
    public boolean regionMatches(int toffset, String other, int ooffset, int len) { return false; }
    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) { return false; }
    public boolean startsWith(String prefix, int toffset) { return false; }
    public boolean startsWith(String prefix) { return false; }
    public boolean endsWith(String suffix) { return false; }
    public int hashCode() { return hash; }
    public int indexOf(int ch) { return -1; }
    public int indexOf(int ch, int fromIndex) { return -1; }
    public int lastIndexOf(int ch) { return -1; }
    public int lastIndexOf(int ch, int fromIndex) { return -1; }
    public int indexOf(String str) { return -1; }
    public int indexOf(String str, int fromIndex) { return -1; }
    public int lastIndexOf(String str) { return -1; }
    public int lastIndexOf(String str, int fromIndex) { return -1; }
    public String substring(int beginIndex) { return ""; }
    public String substring(int beginIndex, int endIndex) { return ""; }
    public CharSequence subSequence(int beginIndex, int endIndex) { return substring(beginIndex, endIndex); }
    public native String concat(String str);
    public String replace(char oldChar, char newChar) { return this; }
    public boolean matches(String regex) { return false; }
    public boolean contains(CharSequence s) { return false; }
    public String replaceFirst(String regex, String replacement) { return this; }
    public String replaceAll(String regex, String replacement) { return this; }
    public String replace(CharSequence target, CharSequence replacement) { return this; }
    public String[] split(String regex, int limit) { return new String[]{this}; }
    public String[] split(String regex) { return new String[]{this}; }
    public String toLowerCase(Locale locale) { return this; }
    public String toLowerCase() { return this; }
    public String toUpperCase(Locale locale) { return this; }
    public String toUpperCase() { return this; }
    public String trim() { return this; }
    public String toString() { return this; }
    public native char[] toCharArray();
    public native String intern();

    public static String join(CharSequence delimiter, CharSequence... elements) { return ""; }
    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) { return ""; }
    public static String format(String format, Object... args) { return format; }
    public static String format(Locale l, String format, Object... args) { return format; }
    public static String valueOf(Object obj) { return (obj == null) ? "null" : obj.toString(); }
    public static String valueOf(char[] data) { return new String(data); }
    public static String valueOf(char[] data, int offset, int count) { return new String(data, offset, count); }
    public static String copyValueOf(char[] data, int offset, int count) { return new String(data, offset, count); }
    public static String copyValueOf(char[] data) { return new String(data); }
    public static String valueOf(boolean b) { return b ? "true" : "false"; }
    public static String valueOf(char c) { return ""; }
    public static String valueOf(int i) { return Integer.toString(i); }
    public static String valueOf(long l) { return Long.toString(l); }
    public static String valueOf(float f) { return Float.toString(f); }
    public static String valueOf(double d) { return Double.toString(d); }
}
