package android.icu.util;
import android.renderscript.Element;
import android.renderscript.Element;

public interface ValueIterator {
    boolean next(Element p0);
    void reset();
    void setRange(int p0, int p1);
}
