package android.app;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Icon;

/**
 * Shim: android.app.RemoteAction (API 26+)
 * Tier 1 — direct field mapping.
 *
 * Represents a remote action that can be sent across processes.
 * Icon and PendingIntent are typed as Object to avoid pulling in
 * the full android.graphics.drawable.Icon / android.app.PendingIntent
 * dependency trees.
 */
public class RemoteAction {
    private final Object mIcon;
    private final CharSequence mTitle;
    private final CharSequence mContentDescription;
    private final Object mActionIntent;
    private boolean mEnabled = true;
    private boolean mShouldShowIcon = true;

    public RemoteAction(Object icon, CharSequence title,
                        CharSequence contentDescription, Object actionIntent) {
        mIcon = icon;
        mTitle = title;
        mContentDescription = contentDescription;
        mActionIntent = actionIntent;
    }

    /** Returns null — Icon not yet bridged to OH. */
    public android.graphics.drawable.Icon getIcon() { return null; }

    public CharSequence getTitle() { return mTitle; }

    public CharSequence getContentDescription() { return mContentDescription; }

    /** Returns null — PendingIntent not yet bridged to OH. */
    public PendingIntent getActionIntent() { return null; }

    public boolean isEnabled() { return mEnabled; }

    public void setEnabled(boolean enabled) { mEnabled = enabled; }

    public boolean shouldShowIcon() { return mShouldShowIcon; }

    public void setShouldShowIcon(boolean shouldShowIcon) {
        mShouldShowIcon = shouldShowIcon;
    }

    @Override
    public String toString() {
        return "RemoteAction{title=" + mTitle + ", enabled=" + mEnabled + "}";
    }
}
