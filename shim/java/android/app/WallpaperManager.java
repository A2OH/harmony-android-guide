package android.app;

public class WallpaperManager {
    public WallpaperManager() {}

    public static final int ACTION_CHANGE_LIVE_WALLPAPER = 0;
    public static final int ACTION_CROP_AND_SET_WALLPAPER = 0;
    public static final int ACTION_LIVE_WALLPAPER_CHOOSER = 0;
    public static final int COMMAND_DROP = 0;
    public static final int COMMAND_SECONDARY_TAP = 0;
    public static final int COMMAND_TAP = 0;
    public static final int EXTRA_LIVE_WALLPAPER_COMPONENT = 0;
    public static final int FLAG_LOCK = 0;
    public static final int FLAG_SYSTEM = 0;
    public static final int WALLPAPER_PREVIEW_META_DATA = 0;
    public void addOnColorsChangedListener(Object p0, Object p1) {}
    public void clearWallpaperOffsets(Object p0) {}
    public void forgetLoadedWallpaper() {}
    public Object getBuiltInDrawable() { return null; }
    public Object getBuiltInDrawable(Object p0) { return null; }
    public Object getBuiltInDrawable(Object p0, Object p1, Object p2, Object p3, Object p4) { return null; }
    public Object getBuiltInDrawable(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) { return null; }
    public Object getCropAndSetWallpaperIntent(Object p0) { return null; }
    public int getDesiredMinimumHeight() { return 0; }
    public int getDesiredMinimumWidth() { return 0; }
    public Object getDrawable() { return null; }
    public static Object getInstance(Object p0) { return null; }
    public int getWallpaperId(Object p0) { return 0; }
    public Object getWallpaperInfo() { return null; }
    public boolean hasResourceWallpaper(Object p0) { return false; }
    public boolean isSetWallpaperAllowed() { return false; }
    public boolean isWallpaperSupported() { return false; }
    public Object peekDrawable() { return null; }
    public void removeOnColorsChangedListener(Object p0) {}
    public void sendWallpaperCommand(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void setWallpaperOffsetSteps(Object p0, Object p1) {}
    public void setWallpaperOffsets(Object p0, Object p1, Object p2) {}
    public void suggestDesiredDimensions(Object p0, Object p1) {}
}
