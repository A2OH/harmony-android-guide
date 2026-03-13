package android.widget;

import android.view.ViewGroup;

/**
 * Shim: android.widget.TableLayout → ArkUI table-like layout
 *
 * TableLayout arranges children in rows and columns.
 * Extends LinearLayout (vertical orientation by default).
 */
public class TableLayout extends LinearLayout {

    private boolean stretchAllColumns;

    public TableLayout() {
        super();
        setOrientation(VERTICAL);
    }

    public void setStretchAllColumns(boolean stretchAllColumns) {
        this.stretchAllColumns = stretchAllColumns;
    }

    public boolean isStretchAllColumns() {
        return stretchAllColumns;
    }

    public void setColumnStretchable(int columnIndex, boolean isStretchable) {
        // no-op stub
    }

    public boolean isColumnStretchable(int columnIndex) {
        return stretchAllColumns;
    }

    public void setColumnShrinkable(int columnIndex, boolean isShrinkable) {
        // no-op stub
    }

    public boolean isColumnShrinkable(int columnIndex) {
        return false;
    }

    public void setColumnCollapsed(int columnIndex, boolean isCollapsed) {
        // no-op stub
    }

    public boolean isColumnCollapsed(int columnIndex) {
        return false;
    }

    public void setShrinkAllColumns(boolean shrinkAllColumns) {
        // no-op stub
    }

    public boolean isShrinkAllColumns() {
        return false;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public LayoutParams() {
            super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
