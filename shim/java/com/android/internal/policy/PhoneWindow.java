// SPDX-License-Identifier: Apache-2.0
//
// Westlake V2-Step5 PhoneWindow stub (decision 12-A from
// BINDER_PIVOT_DESIGN_V2.md §3.5).
//
// Westlake-owned classpath-shadowed PhoneWindow: no chrome, no action
// bar, no title bar, no system insets. Just an installable content view
// hosted by a DecorView (which is a FrameLayout). This satisfies
// AppCompatActivity / framework Activity code paths that reach
// `getWindow()` and expect a PhoneWindow shape, without dragging in the
// real framework PhoneWindow's ~4000 LOC of system_server cold-init
// dependencies.

package com.android.internal.policy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class PhoneWindow extends Window {

    public PhoneWindow(Context context) {
        super(context);
    }

    /**
     * AOSP-shape ctor with preservedWindow + WindowControllerCallback.
     * Both extras are ignored — we have no chrome to preserve.
     */
    public PhoneWindow(Context context, Window preservedWindow,
            WindowControllerCallback callback) {
        super(context);
    }

    /**
     * AOSP also exposes a 2-arg ctor (Context, Window). Provide it so
     * reflective callers don't fall through to NoSuchMethodError.
     */
    public PhoneWindow(Context context, Window preservedWindow) {
        super(context);
    }
}
