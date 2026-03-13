package android.service.quicksettings;

/**
 * Android-compatible Tile shim. Stub — no-op implementation.
 */
public class Tile {

    public static final int STATE_UNAVAILABLE = 0;
    public static final int STATE_INACTIVE = 1;
    public static final int STATE_ACTIVE = 2;

    private CharSequence label;
    private CharSequence contentDescription;
    private int state = STATE_INACTIVE;
    private Object icon;

    public Tile() {}

    public CharSequence getLabel() { return label; }

    public void setLabel(CharSequence label) { this.label = label; }

    public CharSequence getContentDescription() { return contentDescription; }

    public void setContentDescription(CharSequence contentDescription) {
        this.contentDescription = contentDescription;
    }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    public Object getIcon() { return icon; }

    public void setIcon(Object icon) { this.icon = icon; }

    public void updateTile() {}
}
