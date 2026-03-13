package android.app;

import android.content.Intent;
import java.util.List;
import java.util.ArrayList;

/**
 * Android-compatible LauncherActivity shim. Stub — returns empty item list.
 */
public abstract class LauncherActivity extends ListActivity {

    public static class ListItem {
        public String className;
        public String packageName;
        public CharSequence label;
        public Object icon;

        public ListItem() {}
    }

    protected List<ListItem> makeListItems() {
        return new ArrayList<ListItem>();
    }

    protected Intent intentForPosition(int position) {
        return new Intent();
    }
}
