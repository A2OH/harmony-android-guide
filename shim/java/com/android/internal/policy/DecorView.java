// SPDX-License-Identifier: Apache-2.0
//
// Westlake V2-Step5 DecorView stub (decision 12-A from
// BINDER_PIVOT_DESIGN_V2.md §3.5).
//
// A trivial FrameLayout. The real framework DecorView (~3000 LOC) wires
// system insets, action bar, status bar, navigation bar, and floating
// window borders. V2 has none of those — DecorView is just a
// FrameLayout container that hosts the content view returned by
// `Window.getDecorView()`.

package com.android.internal.policy;

import android.content.Context;
import android.widget.FrameLayout;

public class DecorView extends FrameLayout {

    public DecorView(Context context) {
        super(context);
    }
}
