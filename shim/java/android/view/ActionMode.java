package android.view;

/**
 * Shim: android.view.ActionMode — pure Java stub.
 * Represents a contextual action mode (e.g. text selection toolbar).
 */
public abstract class ActionMode {

    private Object mTag;
    private boolean mTitleOptionalHint;

    /** Set the current title of this action mode. */
    public abstract void setTitle(CharSequence title);

    /** Set the current title from a string resource. */
    public abstract void setTitle(int resId);

    /** Set the current subtitle of this action mode. */
    public abstract void setSubtitle(CharSequence subtitle);

    /** Set the current subtitle from a string resource. */
    public abstract void setSubtitle(int resId);

    /** Return the current title of this action mode. */
    public abstract CharSequence getTitle();

    /** Return the current subtitle of this action mode. */
    public abstract CharSequence getSubtitle();

    /** Return the menu of actions provided by this action mode. */
    public abstract Menu getMenu();

    /** Finish and close this action mode. */
    public abstract void finish();

    /** Invalidate the action mode and refresh the menu/action views. */
    public void invalidate() {
        // no-op stub
    }

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
}
