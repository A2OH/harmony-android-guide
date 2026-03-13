package android.net;
import java.util.Map;

public class MailTo {
    public static final int MAILTO_SCHEME = 0;

    public MailTo() {}

    public String getBody() { return null; }
    public String getCc() { return null; }
    public Map<?,?> getHeaders() { return null; }
    public String getSubject() { return null; }
    public String getTo() { return null; }
    public static boolean isMailTo(String p0) { return false; }
    public static MailTo parse(String p0) { return null; }
}
