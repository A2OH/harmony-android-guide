package android.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LayoutInflater — basic stub implementation for MiniServer.
 *
 * Since we don't have a resource system yet, inflate() returns stub views:
 * - A FrameLayout container for any resource ID
 * - The View gets the resource ID stored for later identification
 *
 * This is enough for apps that call setContentView(R.layout.xxx) to get
 * a non-null View tree that can hold child views.
 */
public class LayoutInflater {
    private Context mContext;

    public LayoutInflater(Context context) {
        mContext = context;
    }

    public LayoutInflater(LayoutInflater original, Context newContext) {
        mContext = newContext;
    }

    /**
     * Get a LayoutInflater from a Context.
     * This is the standard way apps obtain a LayoutInflater.
     */
    public static LayoutInflater from(Context context) {
        if (context == null) return new LayoutInflater(context);
        Object svc = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (svc instanceof LayoutInflater) {
            LayoutInflater base = (LayoutInflater) svc;
            // Return a clone bound to the caller's context (matches AOSP behavior)
            return base.cloneInContext(context);
        }
        return new LayoutInflater(context);
    }

    public Context getContext() { return mContext; }

    public LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this, newContext);
    }

    /**
     * Inflate a layout resource. Since we don't have real resources,
     * returns a stub FrameLayout with the resource ID set.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a layout resource with attachToRoot control.
     * Attempts to load and parse the binary layout XML from the APK's extracted res/ directory.
     * Falls back to a stub FrameLayout if the layout can't be found or parsed.
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        View view = null;

        // Try to inflate from the real binary layout XML
        if (mContext != null) {
            android.content.res.Resources res = mContext.getResources();
            if (res != null) {
                android.content.res.ResourceTable table = res.getResourceTable();
                if (table != null) {
                    String layoutFile = table.getLayoutFileName(resource);
                    if (layoutFile != null) {
                        byte[] xmlData = loadLayoutXml(layoutFile);
                        if (xmlData != null && xmlData.length > 0) {
                            BinaryLayoutParser parser = new BinaryLayoutParser(mContext);
                            view = parser.parse(xmlData);
                        }
                    }
                }
            }
        }

        // Fallback: stub FrameLayout
        if (view == null) {
            view = new FrameLayout(mContext);
        }
        view.setId(resource);

        if (root != null && attachToRoot) {
            root.addView(view);
            return root;
        }
        return view;
    }

    /**
     * Load binary layout XML bytes from the extracted APK res/ directory.
     */
    private byte[] loadLayoutXml(String layoutPath) {
        // Try to find the file in the APK's extracted directory
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                String resDir = null;
                android.app.ApkInfo info = server.getApkInfo();
                if (info != null) resDir = info.resDir;
                if (resDir == null) resDir = info != null ? info.extractDir : null;

                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    if (!xmlFile.exists()) {
                        // Try without res/ prefix
                        xmlFile = new java.io.File(resDir, layoutPath.startsWith("res/") ? layoutPath.substring(4) : layoutPath);
                    }
                    if (xmlFile.exists()) {
                        return readFile(xmlFile);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static byte[] readFile(java.io.File file) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int offset = 0;
            while (offset < data.length) {
                int n = fis.read(data, offset, data.length - offset);
                if (n < 0) break;
                offset += n;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public View createView(String name, String prefix, AttributeSet attrs) {
        try {
            String fullName = (prefix != null) ? prefix + name : name;
            Class<?> cls = Class.forName(fullName);
            return (View) cls.getConstructor(Context.class).newInstance(mContext);
        } catch (Exception e) {
            return null;
        }
    }

    public View onCreateView(String name, AttributeSet attrs) { return null; }
    public View onCreateView(View parent, String name, AttributeSet attrs) { return null; }

    public Object getFactory() { return null; }
    public Object getFactory2() { return null; }
    public Filter getFilter() { return null; }
    public void setFactory(Object factory) {}
    public void setFactory2(Object factory) {}
    public void setFilter(Filter filter) {}
}
