package android.service.controls.actions;

public class ControlAction {
    public static final int RESPONSE_CHALLENGE_ACK = 0;
    public static final int RESPONSE_CHALLENGE_PASSPHRASE = 0;
    public static final int RESPONSE_CHALLENGE_PIN = 0;
    public static final int RESPONSE_FAIL = 0;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_UNKNOWN = 0;
    public static final int TYPE_BOOLEAN = 0;
    public static final int TYPE_COMMAND = 0;
    public static final int TYPE_ERROR = 0;
    public static final int TYPE_FLOAT = 0;
    public static final int TYPE_MODE = 0;

    public ControlAction() {}

    public  int getActionType() { return 0; }
    public static boolean isValidResponse(int p0) { return false; }
}
