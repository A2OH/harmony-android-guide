package android.support.v4.app;

/**
 * Android support-library Fragment shim. Minimal stub.
 *
 * android.support.v4.app.Fragment is the support-library version of
 * android.app.Fragment. Apps that use FragmentActivity must use this variant.
 *
 * OH migration note: Fragments map to ArkUI @Component pages / Navigation destinations.
 */
public class Fragment {

    private int mId = 0;
    private String mTag;
    private boolean mAdded = false;
    private boolean mDetached = false;
    private boolean mHidden = false;

    // ── Identity ──────────────────────────────────────────────────────────────

    public int getId()           { return mId; }
    public void setId(int id)    { mId = id; }

    public String getTag()              { return mTag; }
    public void setTag(String tag)      { mTag = tag; }

    public boolean isAdded()            { return mAdded; }
    public void setAdded(boolean added) { mAdded = added; }

    public boolean isDetached()                  { return mDetached; }
    public void setDetached(boolean detached)    { mDetached = detached; }

    public boolean isHidden()                { return mHidden; }
    public void setHidden(boolean hidden)    { mHidden = hidden; }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public void onAttach(Object context) {}
    public void onCreate(Object savedInstanceState) {}
    public void onStart() {}
    public void onResume() {}
    public void onPause() {}
    public void onStop() {}
    public void onDestroyView() {}
    public void onDestroy() {}
    public void onDetach() {}

    public void onActivityCreated(Object savedInstanceState) {}

    // ── View ─────────────────────────────────────────────────────────────────

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater  the LayoutInflater (typed as Object)
     * @param container the parent container (typed as Object)
     * @param savedInstanceState the saved state bundle (typed as Object)
     * @return the root View of the fragment's layout (typed as Object), or null
     */
    public Object onCreateView(Object inflater, Object container, Object savedInstanceState) {
        return null;
    }

    public void onViewCreated(Object view, Object savedInstanceState) {}

    public Object getView() { return null; }

    // ── Host activity ─────────────────────────────────────────────────────────

    public FragmentActivity getActivity() { return null; }

    // ── Arguments ────────────────────────────────────────────────────────────

    private Object mArguments;

    public void setArguments(Object args)  { mArguments = args; }
    public Object getArguments()           { return mArguments; }
}
