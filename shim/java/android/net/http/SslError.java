package android.net.http;

public class SslError {
    public static final int SSL_DATE_INVALID = 0;
    public static final int SSL_EXPIRED = 0;
    public static final int SSL_IDMISMATCH = 0;
    public static final int SSL_INVALID = 0;
    public static final int SSL_NOTYETVALID = 0;
    public static final int SSL_UNTRUSTED = 0;

    public SslError(int p0, SslCertificate p1, String p2) {}
    public SslError(int p0, Object p1, String p2) {}

    public boolean addError(int p0) { return false; }
    public SslCertificate getCertificate() { return null; }
    public int getPrimaryError() { return 0; }
    public String getUrl() { return null; }
    public boolean hasError(int p0) { return false; }
}
