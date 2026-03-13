package android.app;
import android.graphics.Rect;
import android.graphics.Rect;

import android.graphics.Rect;
import java.util.List;

/**
 * Shim: android.app.PictureInPictureParams — parameters for entering
 * picture-in-picture mode.  Constructed exclusively via {@link Builder}.
 */
public class PictureInPictureParams {

    // ── Internal state ─────────────────────────────────────────────────────────
    private Object  aspectRatio;
    private Rect    sourceRectHint;
    private List<?> actions;
    private boolean autoEnterEnabled;
    private boolean seamlessResizeEnabled;

    /** Package-private constructor — use {@link Builder}. */
    PictureInPictureParams() {}

    // ── Getters ────────────────────────────────────────────────────────────────

    public Object  getAspectRatio()          { return aspectRatio; }
    public Rect    getSourceRectHint()       { return sourceRectHint; }
    public List<?> getActions()              { return actions; }
    public boolean hasSetAutoEnterEnabled()  { return autoEnterEnabled; }
    public boolean hasSetSeamlessResizeEnabled() { return seamlessResizeEnabled; }

    // ── Builder ────────────────────────────────────────────────────────────────

    public static class Builder {

        private Object  aspectRatio;
        private Rect    sourceRectHint;
        private List<?> actions;
        private boolean autoEnterEnabled;
        private boolean seamlessResizeEnabled = true;

        public Builder() {}

        public Builder setAspectRatio(Object rational) {
            this.aspectRatio = rational;
            return this;
        }

        public Builder setSourceRectHint(Rect sourceRectHint) {
            this.sourceRectHint = sourceRectHint;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder setActions(List actions) {
            this.actions = actions;
            return this;
        }

        public Builder setAutoEnterEnabled(boolean autoEnterEnabled) {
            this.autoEnterEnabled = autoEnterEnabled;
            return this;
        }

        public Builder setSeamlessResizeEnabled(boolean seamlessResizeEnabled) {
            this.seamlessResizeEnabled = seamlessResizeEnabled;
            return this;
        }

        public PictureInPictureParams build() {
            PictureInPictureParams params = new PictureInPictureParams();
            params.aspectRatio          = this.aspectRatio;
            params.sourceRectHint       = this.sourceRectHint;
            params.actions              = this.actions;
            params.autoEnterEnabled     = this.autoEnterEnabled;
            params.seamlessResizeEnabled = this.seamlessResizeEnabled;
            return params;
        }
    }
}
