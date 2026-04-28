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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.MutableCreationExtras;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;

public class L4Activity extends AppCompatActivity {
    private static final int L4_CONTAINER_ID = 0x7f030004;
    private static final String FRAGMENT_TAG = "cutoff-l4-fragment";
    private static final String ACTIVITY_STATE_KEY = "l4.activity";
    private static final String L4_STATE_STAGE = "L4STATE";
    private static final String L4_RECREATE_STAGE = "L4RECREATE";
    private static final String L4_WAT_RECREATE_STAGE = "L4WATRECREATE";
    private static final String L4_WAT_FACTORY_STAGE = "L4WATFACTORY";
    private static final String L4_WAT_APP_FACTORY_STAGE = "L4WATAPPFACTORY";
    private static final String L4_WAT_APP_REFLECT_STAGE = "L4WATAPPREFLECT";
    private static final String L4_WAT_CORE_APP_STAGE = "L4WATCOREAPP";
    private static final String L4_WAT_HILT_APP_STAGE = "L4WATHILTAPP";
    private static final String RECREATE_DIRECT_KEY = "l4.recreate.direct";
    private static final String RECREATE_TOKEN = "recreated-activity";
    private static final CreationExtras.Key<String> EXTRA_TOKEN_KEY =
            new CreationExtras.Key<String>() {};
    private static int sOriginalIdentity;
    private static boolean sRecreateRequested;
    private static boolean sRecreateSaveSeen;
    private static boolean sRecreatePauseSeen;
    private static boolean sRecreateStopSeen;
    private static boolean sRecreateDestroySeen;
    private static boolean sRecreateCompleted;
    private boolean mRecreatedInstance;
    private boolean mRecreateRestoreOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CanaryLog.mark("L4_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        try {
            super.onCreate(savedInstanceState);
            CanaryLog.mark("L4_SUPER_ON_CREATE_OK", "activity=true");
        } catch (Throwable t) {
            CanaryLog.mark("L4_SUPER_ON_CREATE_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
            return;
        }

        CanaryLog.mark("L4_ON_CREATE", "saved=" + (savedInstanceState != null));
        if (isRecreateProbe()) {
            if (savedInstanceState == null && !sRecreateRequested) {
                resetRecreateProbeState();
            } else if (savedInstanceState != null && !probeRecreateRestored(savedInstanceState)) {
                return;
            }
        }
        if (!probeActivitySavedState()) {
            return;
        }
        if (!probeActivityViewModel()) {
            return;
        }
        if (isStateProbe()) {
            if (!probeActivitySavedStateRestore()) {
                return;
            }
            if (!probeSavedStateHandle()) {
                return;
            }
            if (!probeCreationExtras()) {
                return;
            }
        }

        FrameLayout container = new FrameLayout(this);
        container.setId(L4_CONTAINER_ID);
        setContentView(container);
        CanaryLog.mark("L4_CONTAINER_OK", "programmatic frame");
        if (isStateProbe() && !probeActivityViewTreeOwners(container)) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        CanaryLog.mark("L4_FRAGMENT_MANAGER_OK", "manager=" + (fm != null)
                + " id=" + System.identityHashCode(fm)
                + " class=" + safeClassName(fm));
        L4Fragment fragment = new L4Fragment();
        FragmentTransaction tx = fm.beginTransaction();
        CanaryLog.mark("L4_FRAGMENT_TX_OK", "transaction=" + (tx != null)
                + " id=" + System.identityHashCode(tx)
                + " class=" + safeClassName(tx));
        try {
            tx.add(L4_CONTAINER_ID, fragment, FRAGMENT_TAG);
            CanaryLog.mark("L4_FRAGMENT_ADD_OK", "fragment queued txId="
                    + System.identityHashCode(tx)
                    + " empty=" + tx.isEmpty());
        } catch (Throwable t) {
            CanaryLog.mark("L4_FRAGMENT_ADD_ERR", t.getMessage() == null
                    ? "add failed"
                    : t.getMessage());
            return;
        }
        tx.commitNow();
        CanaryLog.mark("L4_FRAGMENT_COMMIT_RETURNED", "commitNow returned");
        CanaryLog.mark("L4_FRAGMENT_COMMIT_OK", "commitNow returned");
        Fragment found = fm.findFragmentByTag(FRAGMENT_TAG);
        if (found != fragment) {
            CanaryLog.mark("L4_FRAGMENT_LOOKUP_ERR", "found=" + (found != null));
            return;
        }
        CanaryLog.mark("L4_FRAGMENT_LOOKUP_OK", "lookup matched stored tag");
        if (isStateProbe()) {
            CanaryLog.mark("L4STATE_OK", "savedstate handle extras viewtree boundary passed");
        }
        CanaryLog.mark("L4_OK", "savedstate viewmodel fragment boundary passed");
    }

    private void resetRecreateProbeState() {
        sOriginalIdentity = System.identityHashCode(this);
        sRecreateRequested = false;
        sRecreateSaveSeen = false;
        sRecreatePauseSeen = false;
        sRecreateStopSeen = false;
        sRecreateDestroySeen = false;
        sRecreateCompleted = false;
        mRecreatedInstance = false;
        mRecreateRestoreOk = false;
        CanaryLog.mark("L4RECREATE_ORIGINAL_OK", "id=" + sOriginalIdentity);
    }

    private boolean isStateProbe() {
        return isStage(L4_STATE_STAGE) || isRecreateProbe();
    }

    private boolean isRecreateProbe() {
        return isStage(L4_RECREATE_STAGE)
                || isStage(L4_WAT_RECREATE_STAGE)
                || isStage(L4_WAT_FACTORY_STAGE)
                || isStage(L4_WAT_APP_FACTORY_STAGE)
                || isStage(L4_WAT_APP_REFLECT_STAGE)
                || isStage(L4_WAT_CORE_APP_STAGE)
                || isStage(L4_WAT_HILT_APP_STAGE);
    }

    private boolean isStage(String expected) {
        try {
            return sameText(expected, getIntent().getStringExtra("stage"));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean probeRecreateRestored(Bundle savedInstanceState) {
        mRecreatedInstance = true;
        String token = savedInstanceState.getString(RECREATE_DIRECT_KEY);
        if (!RECREATE_TOKEN.equals(token)) {
            CanaryLog.mark("L4RECREATE_RESTORE_ERR", "direct token=" + token);
            return false;
        }
        CanaryLog.mark("L4RECREATE_ON_CREATE_RESTORED_OK", "direct token restored");
        int id = System.identityHashCode(this);
        if (sOriginalIdentity != 0 && id == sOriginalIdentity) {
            CanaryLog.mark("L4RECREATE_NEW_INSTANCE_ERR", "same id=" + id);
            return false;
        }
        CanaryLog.mark("L4RECREATE_NEW_INSTANCE_OK", "old=" + sOriginalIdentity
                + " new=" + id);
        SavedStateRegistry registry = getSavedStateRegistry();
        Bundle consumed = registry != null
                ? registry.consumeRestoredStateForKey(ACTIVITY_STATE_KEY)
                : null;
        if (consumed == null || !"activity".equals(consumed.getString("owner"))) {
            CanaryLog.mark("L4RECREATE_REGISTRY_RESTORED_ERR", "consume mismatch");
            return false;
        }
        CanaryLog.mark("L4RECREATE_REGISTRY_RESTORED_OK", "activity provider restored");
        mRecreateRestoreOk = true;
        return true;
    }

    private boolean probeActivitySavedState() {
        if (!(this instanceof SavedStateRegistryOwner)) {
            CanaryLog.mark("L4_SAVEDSTATE_OWNER_ERR", "activity owner=false");
            return false;
        }
        SavedStateRegistry registry = getSavedStateRegistry();
        if (registry == null) {
            CanaryLog.mark("L4_SAVEDSTATE_OWNER_ERR", "registry=null");
            return false;
        }
        registry.registerSavedStateProvider(ACTIVITY_STATE_KEY,
                new SavedStateRegistry.SavedStateProvider() {
                    @Override
                    public Bundle saveState() {
                        Bundle out = new Bundle();
                        out.putString("owner", "activity");
                        return out;
                    }
                });
        SavedStateRegistry.SavedStateProvider provider = registry.b(ACTIVITY_STATE_KEY);
        if (provider == null) {
            CanaryLog.mark("L4_SAVEDSTATE_PROVIDER_ERR", "readback=null");
            return false;
        }
        Bundle saved = provider.saveState();
        if (saved == null || !"activity".equals(saved.getString("owner"))) {
            CanaryLog.mark("L4_SAVEDSTATE_PROVIDER_ERR", "saveState mismatch");
            return false;
        }
        CanaryLog.mark("L4_SAVEDSTATE_OWNER_OK", "activity=true restored="
                + registry.isRestored());
        CanaryLog.mark("L4_SAVEDSTATE_PROVIDER_OK", "activity provider readback");
        return true;
    }

    private boolean probeActivitySavedStateRestore() {
        Bundle out = new Bundle();
        onSaveInstanceState(out);
        Bundle direct = out.getBundle(ACTIVITY_STATE_KEY);
        if (direct == null || !"activity".equals(direct.getString("owner"))) {
            CanaryLog.mark("L4STATE_REGISTRY_RESTORE_ERR", "saved bundle missing");
            return false;
        }
        SavedStateRegistry restored = new SavedStateRegistry();
        restored.performRestore(out);
        Bundle consumed = restored.consumeRestoredStateForKey(ACTIVITY_STATE_KEY);
        if (consumed == null || !"activity".equals(consumed.getString("owner"))) {
            CanaryLog.mark("L4STATE_REGISTRY_RESTORE_ERR", "consume mismatch");
            return false;
        }
        Bundle consumedAgain = restored.consumeRestoredStateForKey(ACTIVITY_STATE_KEY);
        if (consumedAgain != null) {
            CanaryLog.mark("L4STATE_REGISTRY_RESTORE_ERR", "consume not one-shot");
            return false;
        }
        CanaryLog.mark("L4STATE_REGISTRY_RESTORE_OK", "activity save restore consume");
        return true;
    }

    private boolean probeSavedStateHandle() {
        SavedStateHandle handle = new SavedStateHandle();
        handle.set("owner", "activity");
        if (!handle.contains("owner") || !"activity".equals(handle.get("owner"))) {
            CanaryLog.mark("L4STATE_SAVEDSTATE_HANDLE_ERR", "get/set mismatch");
            return false;
        }
        if (!handle.keys().contains("owner")) {
            CanaryLog.mark("L4STATE_SAVEDSTATE_HANDLE_ERR", "keys missing");
            return false;
        }
        CanaryLog.mark("L4STATE_SAVEDSTATE_HANDLE_OK", "contains get set keys");
        return true;
    }

    private boolean probeCreationExtras() {
        MutableCreationExtras extras = new MutableCreationExtras();
        extras.set(EXTRA_TOKEN_KEY, "activity-extra");
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return create(modelClass, CreationExtras.Empty.c);
            }

            @Override
            public <T extends ViewModel> T create(Class<T> modelClass, CreationExtras creationExtras) {
                if (modelClass == ExtraViewModel.class) {
                    String token = creationExtras != null ? creationExtras.a(EXTRA_TOKEN_KEY) : null;
                    return modelClass.cast(new ExtraViewModel(token));
                }
                try {
                    return modelClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ViewModelProvider provider = new ViewModelProvider(getViewModelStore(), factory, extras);
        ExtraViewModel first = provider.get("activity-extra", ExtraViewModel.class);
        ExtraViewModel second = provider.get("activity-extra", ExtraViewModel.class);
        if (first != second || !"activity-extra".equals(second.token)) {
            CanaryLog.mark("L4STATE_CREATION_EXTRAS_ERR", "same=" + (first == second)
                    + " token=" + second.token);
            return false;
        }
        CanaryLog.mark("L4STATE_CREATION_EXTRAS_OK", "factory extras retained");
        return true;
    }

    private boolean probeActivityViewTreeOwners(View content) {
        if (ViewTreeLifecycleOwner.get(content) != this) {
            CanaryLog.mark("L4STATE_VIEWTREE_LIFECYCLE_OWNER_ERR", "activity mismatch");
            return false;
        }
        CanaryLog.mark("L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK", "activity owner");
        if (ViewTreeViewModelStoreOwner.get(content) != this) {
            CanaryLog.mark("L4STATE_VIEWTREE_VIEWMODEL_OWNER_ERR", "activity mismatch");
            return false;
        }
        CanaryLog.mark("L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK", "activity owner");
        if (ViewTreeSavedStateRegistryOwner.get(content) != this) {
            CanaryLog.mark("L4STATE_VIEWTREE_SAVEDSTATE_OWNER_ERR", "activity mismatch");
            return false;
        }
        CanaryLog.mark("L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK", "activity owner");
        return true;
    }

    private boolean probeActivityViewModel() {
        if (!(this instanceof ViewModelStoreOwner)) {
            CanaryLog.mark("L4_VIEWMODEL_OWNER_ERR", "activity owner=false");
            return false;
        }
        ViewModelProvider provider = new ViewModelProvider((ViewModelStoreOwner) this);
        CountViewModel first = provider.get("activity-canary", CountViewModel.class);
        first.value++;
        CountViewModel second = provider.get("activity-canary", CountViewModel.class);
        if (first != second || second.value != 1) {
            CanaryLog.mark("L4_VIEWMODEL_ERR", "same=" + (first == second)
                    + " value=" + second.value);
            return false;
        }
        CanaryLog.mark("L4_VIEWMODEL_OWNER_OK", "activity=true");
        CanaryLog.mark("L4_VIEWMODEL_OK", "same=true value=" + second.value);
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
        CanaryLog.mark("L4_ON_START_ENTER", "activity=" + getClass().getName());
        super.onStart();
        CanaryLog.mark("L4_ON_START", "activity=" + getClass().getName());
    }

    @Override
    protected void onResume() {
        CanaryLog.mark("L4_ON_RESUME_ENTER", "package=" + getPackageName());
        super.onResume();
        CanaryLog.mark("L4_ON_RESUME", "package=" + getPackageName());
        if (isRecreateProbe()) {
            if (mRecreatedInstance) {
                probeRecreateComplete();
            } else if (!sRecreateRequested) {
                sRecreateRequested = true;
                CanaryLog.mark("L4RECREATE_REQUEST_OK", "calling recreate");
                recreate();
                CanaryLog.mark("L4RECREATE_REQUEST_RETURNED", "recreate returned");
            }
        }
    }

    @Override
    protected void onPause() {
        if (isRecreateProbe() && sRecreateRequested && !mRecreatedInstance) {
            sRecreatePauseSeen = true;
            CanaryLog.mark("L4RECREATE_ON_PAUSE", "old activity");
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (isRecreateProbe() && sRecreateRequested && !mRecreatedInstance) {
            sRecreateStopSeen = true;
            CanaryLog.mark("L4RECREATE_ON_STOP", "old activity");
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isRecreateProbe() && sRecreateRequested && !mRecreatedInstance) {
            sRecreateDestroySeen = true;
            CanaryLog.mark("L4RECREATE_ON_DESTROY", "old activity");
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isRecreateProbe()) {
            outState.putString(RECREATE_DIRECT_KEY, RECREATE_TOKEN);
            if (sRecreateRequested && !mRecreatedInstance) {
                sRecreateSaveSeen = true;
                CanaryLog.mark("L4RECREATE_SAVE_STATE_OK", "token saved");
            }
        }
    }

    private void probeRecreateComplete() {
        if (sRecreateCompleted) {
            return;
        }
        if (!mRecreateRestoreOk) {
            CanaryLog.mark("L4RECREATE_COMPLETE_ERR", "restore=false");
            return;
        }
        if (!sRecreateSaveSeen || !sRecreatePauseSeen
                || !sRecreateStopSeen || !sRecreateDestroySeen) {
            CanaryLog.mark("L4RECREATE_COMPLETE_ERR",
                    "save=" + sRecreateSaveSeen
                            + " pause=" + sRecreatePauseSeen
                            + " stop=" + sRecreateStopSeen
                            + " destroy=" + sRecreateDestroySeen);
            return;
        }
        CanaryLog.mark("L4RECREATE_ON_RESUME_RESTORED_OK", "restored activity resumed");
        CanaryLog.mark("L4RECREATE_OK", "recreate save destroy restore boundary passed");
        sRecreateCompleted = true;
    }

    public static final class CountViewModel extends ViewModel {
        public int value;
    }

    public static final class ExtraViewModel extends ViewModel {
        public final String token;

        public ExtraViewModel(String token) {
            this.token = token;
        }
    }

    public static final class L4Fragment extends Fragment {
        private static final String FRAGMENT_STATE_KEY = "l4.fragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            CanaryLog.mark("L4_FRAGMENT_ON_CREATE", "saved=" + (savedInstanceState != null));
            super.onCreate(savedInstanceState);
            probeFragmentSavedState();
            probeFragmentViewModel();
        }

        private void probeFragmentSavedState() {
            SavedStateRegistry registry = getSavedStateRegistry();
            if (registry == null) {
                CanaryLog.mark("L4_FRAGMENT_SAVEDSTATE_ERR", "registry=null");
                return;
            }
            registry.registerSavedStateProvider(FRAGMENT_STATE_KEY,
                    new SavedStateRegistry.SavedStateProvider() {
                        @Override
                        public Bundle saveState() {
                            Bundle out = new Bundle();
                            out.putString("owner", "fragment");
                            return out;
                        }
                    });
            SavedStateRegistry.SavedStateProvider provider = registry.b(FRAGMENT_STATE_KEY);
            if (provider == null) {
                CanaryLog.mark("L4_FRAGMENT_SAVEDSTATE_ERR", "readback=null");
                return;
            }
            Bundle saved = provider.saveState();
            if (saved == null || !"fragment".equals(saved.getString("owner"))) {
                CanaryLog.mark("L4_FRAGMENT_SAVEDSTATE_ERR", "saveState mismatch");
                return;
            }
            CanaryLog.mark("L4_FRAGMENT_SAVEDSTATE_OK", "fragment provider readback");
            if (isParentStateProbe()) {
                Bundle out = new Bundle();
                registry.performSave(out);
                SavedStateRegistry restored = new SavedStateRegistry();
                restored.performRestore(out);
                Bundle consumed = restored.consumeRestoredStateForKey(FRAGMENT_STATE_KEY);
                if (consumed == null || !"fragment".equals(consumed.getString("owner"))) {
                    CanaryLog.mark("L4STATE_FRAGMENT_REGISTRY_RESTORE_ERR", "consume mismatch");
                    return;
                }
                CanaryLog.mark("L4STATE_FRAGMENT_REGISTRY_RESTORE_OK",
                        "fragment save restore consume");
            }
        }

        private void probeFragmentViewModel() {
            ViewModelProvider provider = new ViewModelProvider(this);
            CountViewModel first = provider.get("fragment-canary", CountViewModel.class);
            first.value++;
            CountViewModel second = provider.get("fragment-canary", CountViewModel.class);
            if (first != second || second.value != 1) {
                CanaryLog.mark("L4_FRAGMENT_VIEWMODEL_ERR", "same=" + (first == second)
                        + " value=" + second.value);
                return;
            }
            CanaryLog.mark("L4_FRAGMENT_VIEWMODEL_OK", "same=true value=" + second.value);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            CanaryLog.mark("L4_FRAGMENT_ON_CREATE_VIEW_ENTER",
                    "container=" + (container != null));
            TextView view = new TextView(getContext());
            view.setText("SavedState + ViewModel alive");
            CanaryLog.mark("L4_FRAGMENT_VIEW_OK", "view=" + view.getClass().getName());
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            CanaryLog.mark("L4_FRAGMENT_ON_VIEW_CREATED", "view=" + (view != null));
            if (isParentStateProbe()) {
                if (ViewTreeLifecycleOwner.get(view) != this) {
                    CanaryLog.mark("L4STATE_FRAGMENT_VIEWTREE_ERR", "lifecycle owner mismatch");
                } else if (ViewTreeViewModelStoreOwner.get(view) != this) {
                    CanaryLog.mark("L4STATE_FRAGMENT_VIEWTREE_ERR", "viewmodel owner mismatch");
                } else if (ViewTreeSavedStateRegistryOwner.get(view) != this) {
                    CanaryLog.mark("L4STATE_FRAGMENT_VIEWTREE_ERR", "savedstate owner mismatch");
                } else {
                    CanaryLog.mark("L4STATE_FRAGMENT_VIEWTREE_OK", "fragment owners on view");
                }
            }
            super.onViewCreated(view, savedInstanceState);
        }

        private boolean isParentStateProbe() {
            try {
                return getActivity() instanceof L4Activity
                        && ((L4Activity) getActivity()).isStateProbe();
            } catch (Throwable ignored) {
                return false;
            }
        }

        @Override
        public void onStart() {
            CanaryLog.mark("L4_FRAGMENT_ON_START_ENTER", "fragment=true");
            super.onStart();
            CanaryLog.mark("L4_FRAGMENT_ON_START", "fragment=true");
        }

        @Override
        public void onResume() {
            CanaryLog.mark("L4_FRAGMENT_ON_RESUME_ENTER", "fragment=true");
            super.onResume();
            CanaryLog.mark("L4_FRAGMENT_ON_RESUME", "fragment=true");
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
}
