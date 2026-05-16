package java.io;

public class DirectPrintStream extends PrintStream {
    private final int fd;
    
    public DirectPrintStream(int fd) {
        super((OutputStream) null);
        this.fd = fd;
    }
    
    private static native void nativeWrite(int fd, byte[] data, int off, int len);
    private static native void nativeWriteByte(int fd, int b);
    
    @Override public void write(int b) { nativeWriteByte(fd, b); }
    @Override public void write(byte[] buf, int off, int len) { nativeWrite(fd, buf, off, len); }
    
    private void writeString(String s) {
        if (s == null) { writeString("null"); return; }
        // Manual UTF-8 encode without using Charset (which needs ICU).
        // #614: must produce the literal em-dash so the harness marker
        // matches verbatim ("westlake-dalvik on OHOS — main reached").
        int len = s.length();
        // Worst-case size: 3 bytes per char (BMP); 4 for surrogate pairs.
        byte[] buf = new byte[len * 4];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int c = s.charAt(i);
            if (c < 0x80) {
                buf[j++] = (byte) c;
            } else if (c < 0x800) {
                buf[j++] = (byte) (0xC0 | (c >> 6));
                buf[j++] = (byte) (0x80 | (c & 0x3F));
            } else if (c >= 0xD800 && c <= 0xDBFF && i + 1 < len) {
                // High surrogate; combine with low surrogate.
                int low = s.charAt(i + 1);
                if (low >= 0xDC00 && low <= 0xDFFF) {
                    int code = 0x10000 + (((c - 0xD800) << 10) | (low - 0xDC00));
                    buf[j++] = (byte) (0xF0 | (code >> 18));
                    buf[j++] = (byte) (0x80 | ((code >> 12) & 0x3F));
                    buf[j++] = (byte) (0x80 | ((code >> 6) & 0x3F));
                    buf[j++] = (byte) (0x80 | (code & 0x3F));
                    i++;
                } else {
                    buf[j++] = '?';
                }
            } else {
                buf[j++] = (byte) (0xE0 | (c >> 12));
                buf[j++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buf[j++] = (byte) (0x80 | (c & 0x3F));
            }
        }
        write(buf, 0, j);
    }
    
    @Override public void println(String s) { writeString(s); write('\n'); }
    @Override public void print(String s) { writeString(s); }
    @Override public void println(Object o) { println(String.valueOf(o)); }
    @Override public void println(int i) { println(String.valueOf(i)); }
    @Override public void println(long l) { println(String.valueOf(l)); }
    @Override public void println(boolean b) { println(String.valueOf(b)); }
    @Override public void println() { write('\n'); }
    @Override public void flush() {}
    @Override public void close() {}
}
