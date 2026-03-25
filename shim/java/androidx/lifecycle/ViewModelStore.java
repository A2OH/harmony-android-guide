package androidx.lifecycle;

import java.util.HashMap;

public class ViewModelStore {
    private final HashMap<String, ViewModel> map = new HashMap<>();

    public final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = map.put(key, viewModel);
        if (oldViewModel != null) {
            oldViewModel.onCleared();
        }
    }

    public final ViewModel get(String key) {
        return map.get(key);
    }

    public void clear() {
        for (ViewModel vm : map.values()) {
            vm.clear();
        }
        map.clear();
    }
}
