package android.widget;
import android.view.ViewGroup;
import android.view.ViewGroup;

import android.view.ViewGroup;

/**
 * Shim: android.widget.TableRow → row within a TableLayout
 *
 * Extends LinearLayout with horizontal orientation.
 */
public class TableRow extends LinearLayout {

    public TableRow() {
        super();
        setOrientation(HORIZONTAL);
    }

    public int getVirtualChildCount() {
        return 0;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public int column = -1;
        public int span = 1;

        public LayoutParams() {
            super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int column) {
            super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.column = column;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
