package android.content.pm;

import android.content.Intent;

/**
 * Android-compatible LabeledIntent shim.
 * An Intent that also contains a label and icon attribute, used for intents
 * that are displayed to the user in a list (e.g., chooser dialog).
 * Stub — label/icon loading returns null; no resource resolution.
 */
public class LabeledIntent extends Intent {

    private final String mSourcePackage;
    private final int mLabelRes;
    private final int mIcon;

    /**
     * Create a LabeledIntent from an existing Intent, with an explicit label
     * resource and icon.
     *
     * @param origIntent the original Intent to copy
     * @param sourcePackage the package from which the label/icon are loaded
     * @param labelRes    resource id of the label string
     * @param icon        resource id of the icon drawable
     */
    public LabeledIntent(Intent origIntent, String sourcePackage, int labelRes, int icon) {
        super(origIntent);
        mSourcePackage = sourcePackage;
        mLabelRes = labelRes;
        mIcon = icon;
    }

    /**
     * Create a LabeledIntent with an explicit label resource and icon,
     * without a base Intent.
     *
     * @param sourcePackage the package from which the label/icon are loaded
     * @param labelRes      resource id of the label string
     * @param icon          resource id of the icon drawable
     */
    public LabeledIntent(String sourcePackage, int labelRes, int icon) {
        super();
        mSourcePackage = sourcePackage;
        mLabelRes = labelRes;
        mIcon = icon;
    }

    /**
     * Returns the name of the package that is the source of this LabeledIntent.
     * This is the package that provided the label and icon.
     */
    public String getSourcePackage() {
        return mSourcePackage;
    }

    /**
     * Returns the resource identifier for the label associated with this Intent,
     * or 0 if none was supplied.
     */
    public int getLabelResource() {
        return mLabelRes;
    }

    /**
     * Returns the resource identifier for the icon associated with this Intent,
     * or 0 if none was supplied.
     */
    public int getIconResource() {
        return mIcon;
    }

    /**
     * Returns a non-localized label for this Intent.
     * In the shim, this always returns null (no resource resolution available).
     *
     * @return null in the shim layer
     */
    public CharSequence getNonLocalizedLabel() {
        return null; // stub — no resource resolution in shim
    }

    /**
     * Load the label associated with this object. If the object does not have
     * a label, null is returned.
     * Stub — returns null (no PackageManager available in shim layer).
     *
     * @param pm ignored in the shim; typed as Object to avoid dependency
     * @return null in the shim layer
     */
    public Object loadLabel(Object pm) {
        return null; // stub — no resource resolution in shim
    }

    /**
     * Load the icon associated with this object. If the object does not have
     * an icon, null is returned.
     * Stub — returns null (no PackageManager available in shim layer).
     *
     * @param pm ignored in the shim; typed as Object to avoid dependency
     * @return null in the shim layer
     */
    public Object loadIcon(Object pm) {
        return null; // stub — no resource resolution in shim
    }

    @Override
    public String toString() {
        return "LabeledIntent{sourcePackage=" + mSourcePackage
                + ", labelRes=" + mLabelRes
                + ", icon=" + mIcon + "}";
    }
}
