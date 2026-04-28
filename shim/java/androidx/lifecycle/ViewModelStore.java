package androidx.lifecycle;

import java.util.HashMap;
import java.util.Map;

public class ViewModelStore {
    private final Map<String, ViewModel> mMap = new HashMap<>();
    final void put(String key, ViewModel vm) {
        ViewModel old = mMap.put(key, vm);
        if (old != null && old != vm) {
            old.clear();
        }
    }
    final ViewModel get(String key) { return mMap.get(key); }
    public final void clear() {
        for (ViewModel vm : mMap.values()) vm.clear();
        mMap.clear();
    }
}
