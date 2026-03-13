package android.widget;

/**
 * Shim: android.widget.QuickContactBadge
 *
 * An ImageView that, when tapped, opens the Quick Contact dialog for a
 * specific contact. In the shim this is a pure no-op image view; contact
 * lookup and the dialog are not available on OpenHarmony.
 *
 * OH mapping: ARKUI_NODE_IMAGE — contact data wiring not implemented.
 */
public class QuickContactBadge extends ImageView {

    /**
     * Mode constants — mirror android.provider.ContactsContract.QuickContact.
     * Defined here to avoid pulling in the ContactsContract shim.
     */
    public static final int MODE_SMALL  = 1;
    public static final int MODE_MEDIUM = 2;
    public static final int MODE_LARGE  = 3;

    private Object  mContactUri;    // android.net.Uri — Object to break dep
    private String  mContactEmail;
    private String  mContactPhone;
    private int     mExcludeMimes;
    private boolean mOverlay = true;

    public QuickContactBadge() {
        super();
    }

    public QuickContactBadge(Object context) {
        super();
    }

    public QuickContactBadge(Object context, Object attrs) {
        super();
    }

    public QuickContactBadge(Object context, Object attrs, int defStyleAttr) {
        super();
    }

    // ── Contact assignment ────────────────────────────────────────────────────

    /**
     * Assign a contact URI directly.
     *
     * @param contactUri  a contacts URI such as
     *                    {@code ContactsContract.Contacts.getLookupUri(…)}.
     *                    Accepted as Object to avoid the ContentProvider shim dep.
     */
    public void assignContactUri(Object contactUri) {
        this.mContactUri = contactUri;
    }

    /**
     * Assign the contact by e-mail address.
     *
     * @param emailAddress  the contact's primary e-mail address.
     * @param lazyLookup    if true, the lookup is deferred until the badge is tapped.
     */
    public void assignContactFromEmail(String emailAddress, boolean lazyLookup) {
        this.mContactEmail = emailAddress;
    }

    /**
     * Assign the contact by e-mail address with a pre-resolved contact URI.
     */
    public void assignContactFromEmail(String emailAddress, boolean lazyLookup,
                                       Object contactUri) {
        this.mContactEmail = emailAddress;
        this.mContactUri   = contactUri;
    }

    /**
     * Assign the contact by phone number.
     *
     * @param phoneNumber  the contact's phone number.
     * @param lazyLookup   if true, the lookup is deferred until the badge is tapped.
     */
    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup) {
        this.mContactPhone = phoneNumber;
    }

    /**
     * Assign the contact by phone number with a pre-resolved contact URI.
     */
    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup,
                                       Object contactUri) {
        this.mContactPhone = phoneNumber;
        this.mContactUri   = contactUri;
    }

    // ── Exclude MIME types ────────────────────────────────────────────────────

    /**
     * Filter out specific MIME-type action buttons from the Quick Contact panel.
     * No-op in shim.
     *
     * @param excludeMimes  array of MIME-type strings to exclude.
     */
    public void setExcludeMimes(String[] excludeMimes) {
        // no-op in shim — Quick Contact panel not available on OH
    }

    // ── Overlay image ─────────────────────────────────────────────────────────

    /**
     * Whether to draw a circular overlay on top of the badge image to
     * indicate that it is tappable.
     *
     * @param overlay  true (default) to draw the overlay.
     */
    public void setOverlay(boolean overlay) {
        this.mOverlay = overlay;
    }

    /** @return the currently assigned contact URI, or null if not set. */
    public Object getContactUri() {
        return mContactUri;
    }
}
