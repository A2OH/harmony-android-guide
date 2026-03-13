package android.widget;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import java.net.URI;

import android.view.View;

/**
 * Shim: android.widget.ImageSwitcher
 *
 * An extension of ViewSwitcher that wraps two ImageView children and lets the
 * caller set an image on the "next" view with a smooth transition.
 *
 * The real implementation calls {@link ViewSwitcher.ViewFactory#makeView()} to
 * create each ImageView child.  This stub delegates factory management to the
 * superclass and provides no-op image setters.
 */
public class ImageSwitcher extends ViewSwitcher {

    public ImageSwitcher() {
        super();
    }

    /**
     * Sets a new image on the next ImageView child using a drawable resource ID.
     *
     * @param resId the drawable resource identifier
     */
    public void setImageResource(int resId) {
        View nextView = getNextView();
        if (nextView instanceof ImageView) {
            ((ImageView) nextView).setImageResource(resId);
        }
        showNext();
    }

    /**
     * Sets a new image on the next ImageView child using a URI.
     *
     * @param uri the URI of the image (may be a {@code android.net.Uri} or any
     *            object whose {@link Object#toString()} yields a valid URI string)
     */
    public void setImageURI(Object uri) {
        View nextView = getNextView();
        if (nextView instanceof ImageView) {
            ((ImageView) nextView).setImageURI(uri);
        }
        showNext();
    }

    /**
     * Sets a new image on the next ImageView child using a drawable object.
     * Accepts any object (avoids pulling in {@code android.graphics.drawable.Drawable}).
     *
     * @param drawable the drawable to display
     */
    public void setImageDrawable(Object drawable) {
        // No-op: drawable type not modelled in shim
        showNext();
    }
}
