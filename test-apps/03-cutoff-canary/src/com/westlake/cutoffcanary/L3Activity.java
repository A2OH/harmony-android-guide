package com.westlake.cutoffcanary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public final class L3Activity extends AppCompatActivity {
    private static final int L3_CONTAINER_ID = 0x7f030003;
    private static final String FRAGMENT_TAG = "cutoff-l3-fragment";
    private static final String L3_LOOKUP_STAGE = "L3LOOKUP";
    private static final String L3_INTERFACE_STAGE = "L3IFACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CanaryLog.mark("L3_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        try {
            super.onCreate(savedInstanceState);
            CanaryLog.mark("L3_SUPER_ON_CREATE_OK", "activity=true");
        } catch (Throwable t) {
            CanaryLog.mark("L3_SUPER_ON_CREATE_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
            return;
        }

        CanaryLog.mark("L3_ON_CREATE", "saved=" + (savedInstanceState != null));
        FrameLayout container = new FrameLayout(this);
        container.setId(L3_CONTAINER_ID);
        setContentView(container);
        CanaryLog.mark("L3_CONTAINER_OK", "programmatic frame");

        FragmentManager fm = getSupportFragmentManager();
        CanaryLog.mark("L3_FRAGMENT_MANAGER_OK", "manager=" + (fm != null)
                + " id=" + System.identityHashCode(fm)
                + " class=" + safeClassName(fm));
        CanaryFragment fragment = new CanaryFragment();
        FragmentTransaction tx = fm.beginTransaction();
        CanaryLog.mark("L3_FRAGMENT_TX_OK", "transaction=" + (tx != null)
                + " id=" + System.identityHashCode(tx)
                + " class=" + safeClassName(tx));
        try {
            tx.add(L3_CONTAINER_ID, fragment, FRAGMENT_TAG);
            CanaryLog.mark("L3_FRAGMENT_ADD_OK", "fragment queued txId="
                    + System.identityHashCode(tx)
                    + " empty=" + tx.isEmpty());
        } catch (Throwable t) {
            CanaryLog.mark("L3_FRAGMENT_ADD_ERR", t.getMessage() == null
                    ? "add failed"
                    : t.getMessage());
            return;
        }
        tx.commitNow();
        CanaryLog.mark("L3_FRAGMENT_COMMIT_RETURNED", "commitNow returned");
        CanaryLog.mark("L3_FRAGMENT_COMMIT_OK", "commitNow returned");
        if (isInterfaceProbe()) {
            runInterfaceProbe(fm, fragment);
        }
        if (isLookupProbe()) {
            runLookupProbe(fm, fragment);
        }
        CanaryLog.mark("L3_OK", "appcompat fragment committed");
    }

    private boolean isLookupProbe() {
        try {
            return sameText(L3_LOOKUP_STAGE, getIntent().getStringExtra("stage"));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isInterfaceProbe() {
        try {
            return sameText(L3_INTERFACE_STAGE, getIntent().getStringExtra("stage"));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void runInterfaceProbe(FragmentManager fm, Fragment fragment) {
        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 iface probe before fragments");
            java.util.List<Fragment> fragments = fm.getFragments();
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 iface probe after fragments count="
                    + (fragments != null ? fragments.size() : -1));
            if (fragments == null || fragments.size() == 0) {
                CanaryLog.mark("L3_FRAGMENT_INTERFACE_GET_ERR", "empty fragments");
                return;
            }
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 iface probe before list.get");
            Fragment first = firstViaInterface(fragments);
            com.westlake.engine.WestlakeLauncher.noteMarker(first == fragment
                    ? "CV L3 iface probe after list.get matched"
                    : "CV L3 iface probe after list.get mismatch");
            if (first != fragment) {
                CanaryLog.mark("L3_FRAGMENT_INTERFACE_GET_ERR", "first=" + (first != null));
                return;
            }
            CanaryLog.mark("L3_FRAGMENT_INTERFACE_GET_OK", "List.get returned committed fragment");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 iface probe caught");
            CanaryLog.mark("L3_FRAGMENT_INTERFACE_GET_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
        }
    }

    private static Fragment firstViaInterface(java.util.List<Fragment> fragments) {
        return fragments.get(0);
    }

    private void runLookupProbe(FragmentManager fm, Fragment fragment) {
        try {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 lookup probe before id");
            Fragment byId = fm.findFragmentById(L3_CONTAINER_ID);
            com.westlake.engine.WestlakeLauncher.noteMarker(byId == fragment
                    ? "CV L3 lookup probe after id matched"
                    : "CV L3 lookup probe after id mismatch");
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 lookup probe before fragments");
            java.util.List<Fragment> fragments = fm.getFragments();
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 lookup probe after fragments count="
                    + (fragments != null ? fragments.size() : -1));
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 lookup probe before tag");
            Fragment found = fm.findFragmentByTag(FRAGMENT_TAG);
            com.westlake.engine.WestlakeLauncher.noteMarker(found == fragment
                    ? "CV L3 lookup probe after tag matched"
                    : "CV L3 lookup probe after tag mismatch");
            if (found != fragment) {
                CanaryLog.mark("L3_FRAGMENT_LOOKUP_ERR", "found=" + (found != null));
                return;
            }
            CanaryLog.mark("L3_FRAGMENT_LOOKUP_OK", "lookup matched stored tag");
        } catch (Throwable t) {
            com.westlake.engine.WestlakeLauncher.noteMarker("CV L3 lookup probe caught");
            CanaryLog.mark("L3_FRAGMENT_LOOKUP_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
        }
    }

    private static boolean sameText(String expected, String actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        int length = expected.length();
        if (length != actual.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static String safeClassName(Object value) {
        if (value == null) {
            return "null";
        }
        try {
            return value.getClass().getName();
        } catch (Throwable t) {
            return "class-error:" + t.getClass().getName();
        }
    }

    @Override
    protected void onStart() {
        CanaryLog.mark("L3_ON_START_ENTER", "activity=" + getClass().getName());
        super.onStart();
        CanaryLog.mark("L3_ON_START", "activity=" + getClass().getName());
    }

    @Override
    protected void onResume() {
        CanaryLog.mark("L3_ON_RESUME_ENTER", "package=" + getPackageName());
        super.onResume();
        CanaryLog.mark("L3_ON_RESUME", "package=" + getPackageName());
    }

    public static final class CanaryFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            CanaryLog.mark("L3_FRAGMENT_ON_CREATE", "saved=" + (savedInstanceState != null));
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            CanaryLog.mark("L3_FRAGMENT_ON_CREATE_VIEW_ENTER",
                    "container=" + (container != null));
            TextView view = new TextView(getContext());
            view.setText("Fragment view alive");
            CanaryLog.mark("L3_FRAGMENT_VIEW_OK", "view=" + view.getClass().getName());
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            CanaryLog.mark("L3_FRAGMENT_ON_VIEW_CREATED", "view=" + (view != null));
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onStart() {
            CanaryLog.mark("L3_FRAGMENT_ON_START_ENTER", "fragment=true");
            super.onStart();
            CanaryLog.mark("L3_FRAGMENT_ON_START", "fragment=true");
        }

        @Override
        public void onResume() {
            CanaryLog.mark("L3_FRAGMENT_ON_RESUME_ENTER", "fragment=true");
            super.onResume();
            CanaryLog.mark("L3_FRAGMENT_ON_RESUME", "fragment=true");
        }
    }
}
