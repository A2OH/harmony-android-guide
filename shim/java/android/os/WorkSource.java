package android.os;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible WorkSource shim. Tracks which UIDs caused work.
 */
public class WorkSource {
    private final List<Integer> mUids = new ArrayList<>();

    public WorkSource() {}

    public WorkSource(int uid) {
        mUids.add(uid);
    }

    public WorkSource(WorkSource other) {
        if (other != null) mUids.addAll(other.mUids);
    }

    public boolean add(int uid) {
        if (!mUids.contains(uid)) {
            mUids.add(uid);
            return true;
        }
        return false;
    }

    public boolean add(WorkSource other) {
        if (other == null) return false;
        boolean changed = false;
        for (int uid : other.mUids) {
            changed |= add(uid);
        }
        return changed;
    }

    public boolean remove(int uid) {
        return mUids.remove(Integer.valueOf(uid));
    }

    public boolean remove(WorkSource other) {
        if (other == null) return false;
        boolean changed = false;
        for (int uid : other.mUids) {
            changed |= remove(uid);
        }
        return changed;
    }

    public void clear() {
        mUids.clear();
    }

    public int size() {
        return mUids.size();
    }

    public int get(int index) {
        return mUids.get(index);
    }

    public boolean isEmpty() {
        return mUids.isEmpty();
    }

    @Override
    public String toString() {
        return "WorkSource" + mUids;
    }
}
