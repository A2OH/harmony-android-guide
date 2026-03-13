package android.content.pm;

/**
 * Android-compatible ActivityInfo shim. Stub — no-op implementation for A2OH migration.
 */
public class ActivityInfo extends PackageItemInfo {

    // Screen orientation constants
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE    =  0;
    public static final int SCREEN_ORIENTATION_PORTRAIT     =  1;

    // Config change bitmask constants
    public static final int CONFIG_ORIENTATION  = 0x0080;
    public static final int CONFIG_SCREEN_SIZE  = 0x0400;

    // Launch mode constants
    public static final int LAUNCH_MULTIPLE        = 0;
    public static final int LAUNCH_SINGLE_TOP      = 1;
    public static final int LAUNCH_SINGLE_TASK     = 2;
    public static final int LAUNCH_SINGLE_INSTANCE = 3;

    public int    theme;
    public int    launchMode;
    public int    screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED;
    public int    configChanges;
    public int    softInputMode;
    public String taskAffinity;
    public String permission;
    public int    flags;
    public String targetActivity;
    public String parentActivityName;

    public ActivityInfo() {}

    public ActivityInfo(ActivityInfo orig) {
        super(orig);
        theme               = orig.theme;
        launchMode          = orig.launchMode;
        screenOrientation   = orig.screenOrientation;
        configChanges       = orig.configChanges;
        softInputMode       = orig.softInputMode;
        taskAffinity        = orig.taskAffinity;
        permission          = orig.permission;
        flags               = orig.flags;
        targetActivity      = orig.targetActivity;
        parentActivityName  = orig.parentActivityName;
    }
}
