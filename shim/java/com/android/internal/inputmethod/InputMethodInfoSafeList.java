// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- compile-time stub for InputMethodInfoSafeList.
//
// Android 16 added InputMethodInfoSafeList as a Parcelable wrapper that
// safely lists InputMethodInfo across binder boundaries.  At runtime
// framework.jar provides the real class; this stub gives us a public
// no-arg constructor + an `empty()` factory so the shim's WestlakeIMM
// can return an empty list without depending on the real Parcelable
// wiring at compile time.  Reflection inside the shim looks up the real
// `empty()` static so we don't accidentally smuggle a shim instance
// across into framework code.

package com.android.internal.inputmethod;

public class InputMethodInfoSafeList {
    public InputMethodInfoSafeList() {}

    /** Compile-time stub for the real Android 16 `empty()` factory. */
    public static InputMethodInfoSafeList empty() { return new InputMethodInfoSafeList(); }
}
