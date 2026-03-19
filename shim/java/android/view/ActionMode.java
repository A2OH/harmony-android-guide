package android.view;
import java.util.Set;

/**
 * Shim: android.view.ActionMode — pure Java stub.
 * Represents a contextual action mode (e.g. text selection toolbar).
 */
public class ActionMode {

    public static final int TYPE_PRIMARY = 0;
    public static final int TYPE_FLOATING = 1;
    public static final int DEFAULT_HIDE_DURATION = 2000;

    private Object mTag;
    private boolean mTitleOptionalHint;

    /** Set the current title of this action mode. */
    public void setTitle(CharSequence title) {}

    /** Set the current title from a string resource. */
    public void setTitle(int resId) {}

    /** Set the current subtitle of this action mode. */
    public void setSubtitle(CharSequence subtitle) {}

    /** Set the current subtitle from a string resource. */
    public void setSubtitle(int resId) {}

    /** Return the current title of this action mode. */
    public CharSequence getTitle() { return null; }

    /** Return the current subtitle of this action mode. */
    public CharSequence getSubtitle() { return null; }

    /** Return the menu of actions provided by this action mode. */
    public Menu getMenu() { return null; }

    /** Finish and close this action mode. */
    public void finish() {}

    /** Hide the action mode view for the given duration. */
    public void hide(long duration) {}

    /** Invalidate the action mode and refresh the menu/action views. */
    public void invalidate() {
        // no-op stub
    }

    /** Invalidate the content rect for the action mode. */
    public void invalidateContentRect() {}

    /** Set a custom view for the action mode. */
    public void setCustomView(View view) {}

    /** Return the custom view, if any. */
    public View getCustomView() { return null; }

    /** Return the menu inflater for this action mode. */
    public MenuInflater getMenuInflater() { return null; }

    /** Set a tag object associated with this action mode. */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /** Retrieve the tag object associated with this action mode. */
    public Object getTag() {
        return mTag;
    }

    /** Set whether the title is optional. */
    public void setTitleOptionalHint(boolean titleOptional) {
        mTitleOptionalHint = titleOptional;
    }

    /** Return the optional-title hint. */
    public boolean getTitleOptionalHint() {
        return mTitleOptionalHint;
    }

    /**
     * Callback interface for action mode events.
     */
    public interface Callback {
        /** Called when an action mode is first created. */
        boolean onCreateActionMode(ActionMode mode, Menu menu);

        /** Called to refresh an action mode's menu whenever it is invalidated. */
        boolean onPrepareActionMode(ActionMode mode, Menu menu);

        /** Called when the user selects a contextual action. */
        boolean onActionItemClicked(ActionMode mode, MenuItem item);

        /** Called when an action mode is about to be destroyed. */
        void onDestroyActionMode(ActionMode mode);
    }

    public int getType() { return TYPE_PRIMARY; }
    public void setType(int type) {}
    public boolean isTitleOptional() { return false; }

    /** Callback2 extends Callback with content rect support. */
    public static abstract class Callback2 implements Callback {
        public void onGetContentRect(ActionMode mode, View view, android.graphics.Rect outRect) {
            if (outRect != null) {
                outRect.set(0, 0, view != null ? view.getWidth() : 0, view != null ? view.getHeight() : 0);
            }
        }
    }
}
