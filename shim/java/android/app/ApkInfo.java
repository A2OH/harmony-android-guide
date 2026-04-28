package android.app;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * ApkInfo — parsed APK metadata.
 * Populated by ApkLoader after extracting and parsing an APK file.
 */
public class ApkInfo {
    private static final class SmallList<E> extends AbstractList<E> implements RandomAccess {
        private Object[] elements;
        private int size;

        SmallList(int initialCapacity) {
            elements = new Object[Math.max(1, initialCapacity)];
        }

        @Override
        public E get(int index) {
            checkIndex(index);
            return (E) elements[index];
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public E set(int index, E element) {
            checkIndex(index);
            E old = (E) elements[index];
            elements[index] = element;
            return old;
        }

        @Override
        public void add(int index, E element) {
            checkPosition(index);
            ensureCapacity(size + 1);
            if (index < size) {
                System.arraycopy(elements, index, elements, index + 1, size - index);
            }
            elements[index] = element;
            size++;
            modCount++;
        }

        @Override
        public E remove(int index) {
            checkIndex(index);
            E old = (E) elements[index];
            int moved = size - index - 1;
            if (moved > 0) {
                System.arraycopy(elements, index + 1, elements, index, moved);
            }
            elements[--size] = null;
            modCount++;
            return old;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity <= elements.length) {
                return;
            }
            int newCapacity = Math.max(minCapacity, elements.length * 2);
            Object[] next = new Object[newCapacity];
            System.arraycopy(elements, 0, next, 0, size);
            elements = next;
        }

        private void checkIndex(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
            }
        }

        private void checkPosition(int index) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
            }
        }
    }

    public String packageName;
    public int versionCode;
    public String versionName;
    public int minSdkVersion;
    public int targetSdkVersion;
    public String launcherActivity;
    public String applicationClassName;
    public String appComponentFactoryClassName;

    /** Fully-qualified Activity class names declared in the manifest */
    public final List<String> activities = new SmallList<>(4);

    /** Fully-qualified Service class names */
    public final List<String> services = new SmallList<>(4);

    /** Requested permissions */
    public final List<String> permissions = new SmallList<>(8);

    /** Paths to extracted DEX files (classes.dex, classes2.dex, ...) */
    public final List<String> dexPaths = new SmallList<>(4);

    /** Path to extracted APK directory */
    public String extractDir;

    /** Path to extracted assets/ directory */
    public String assetDir;

    /** Path to extracted native libs */
    public String nativeLibDir;

    /** Paths to individual .so files */
    public final List<String> nativeLibPaths = new SmallList<>(8);

    /** Path to extracted res/ directory */
    public String resDir;

    /** ResourceTable (not serializable) */
    public transient Object resourceTable;

    @Override
    public String toString() {
        return "ApkInfo{pkg=" + packageName
                + ", ver=" + versionCode
                + ", activities=" + activities.size()
                + ", dex=" + dexPaths.size()
                + ", factory=" + appComponentFactoryClassName + "}";
    }
}
