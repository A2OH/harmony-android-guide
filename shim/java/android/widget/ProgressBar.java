package android.widget;
import android.view.View;
import android.view.View;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ProgressBar → ARKUI_NODE_PROGRESS
 */
public class ProgressBar extends View {
    static final int NODE_TYPE_PROGRESS = 14;
    static final int ATTR_PROGRESS_VALUE = 14000;
    static final int ATTR_PROGRESS_TOTAL = 14001;

    private int max = 100;
    private int progress = 0;
    private boolean indeterminate = false;

    public ProgressBar() {
        super(NODE_TYPE_PROGRESS);
    }

    public int getMax() { return max; }

    public void setMax(int max) {
        this.max = max;
        if (nativeHandle != 0) {
// FIXME OHBridge: // FIXME OHBridge: // FIXME OHBridge:             OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PROGRESS_TOTAL, (float) max, 0, 0, 0);
        }
    }

    public int getProgress() { return progress; }

    public void setProgress(int progress) {
        this.progress = progress;
        if (nativeHandle != 0) {
// FIXME OHBridge: // FIXME OHBridge: // FIXME OHBridge:             OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PROGRESS_VALUE, (float) progress, 0, 0, 0);
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        // ArkUI LoadingProgress handles indeterminate — different node type
    }

    public boolean isIndeterminate() { return indeterminate; }

    public void incrementProgressBy(int diff) {
        setProgress(progress + diff);
    }
}
