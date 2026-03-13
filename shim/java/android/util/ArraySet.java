package android.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Shim: android.util.ArraySet<E>
 * Implements {@link Set} backed by a {@link HashSet}.
 * Provides Android's index-based {@link #valueAt(int)} accessor (O(n)).
 * Pure Java — no Android or OHBridge dependencies.
 */
public class ArraySet<E> implements Set<E> {

    private final HashSet<E> mSet;

    public ArraySet() {
        mSet = new HashSet<>();
    }

    public ArraySet(int capacity) {
        mSet = new HashSet<>(capacity);
    }

    public ArraySet(ArraySet<E> set) {
        mSet = new HashSet<>();
        if (set != null) mSet.addAll(set.mSet);
    }

    @Override public int      size()                             { return mSet.size(); }
    @Override public boolean  isEmpty()                          { return mSet.isEmpty(); }
    @Override public boolean  contains(Object o)                 { return mSet.contains(o); }
    @Override public boolean  add(E e)                           { return mSet.add(e); }
    @Override public boolean  remove(Object o)                   { return mSet.remove(o); }
    @Override public boolean  containsAll(Collection<?> c)       { return mSet.containsAll(c); }
    @Override public boolean  addAll(Collection<? extends E> c)  { return mSet.addAll(c); }
    @Override public boolean  retainAll(Collection<?> c)         { return mSet.retainAll(c); }
    @Override public boolean  removeAll(Collection<?> c)         { return mSet.removeAll(c); }
    @Override public void     clear()                            { mSet.clear(); }
    @Override public Iterator<E> iterator()                      { return mSet.iterator(); }
    @Override public Object[] toArray()                          { return mSet.toArray(); }
    @Override public <T> T[]  toArray(T[] a)                     { return mSet.toArray(a); }

    public E valueAt(int index) {
        if (index < 0 || index >= mSet.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        List<E> list = new ArrayList<>(mSet);
        return list.get(index);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ArraySet) return mSet.equals(((ArraySet<?>) o).mSet);
        if (o instanceof Set)      return mSet.equals(o);
        return false;
    }

    @Override public int    hashCode()   { return mSet.hashCode(); }
    @Override public String toString()   { return mSet.toString(); }
}
