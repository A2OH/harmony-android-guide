// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-PRE9 -- shim/java/android/content/IContentProvider.java
//
// COMPILE-TIME STUB for android.content.IContentProvider.  AOSP marks the
// real interface @hide / @SystemApi so it isn't in the public SDK
// android.jar.  We only need the FQCN to compile against; at runtime
// framework.jar's real IContentProvider wins and aosp-shim.dex's copy
// is stripped via scripts/framework_duplicates.txt.
//
// Why this is minimal (no methods declared here):
//   The shim's only consumers are:
//     - com.westlake.services.WestlakeContentResolver -- needs the FQCN
//       to declare `protected IContentProvider acquireProvider(...)`
//       and `public boolean releaseProvider(IContentProvider)` and
//       friends.  Does NOT invoke any IContentProvider method directly
//       (it constructs a Proxy whose InvocationHandler returns defaults
//       for whatever method the runtime asks for).
//     - All other call sites are inside framework.jar at runtime; the
//       shim's IContentProvider is invisible to them once stripped.
//
//   Because no method is ever resolved through this shim, leaving the
//   method list empty keeps the shim immune to framework.jar IContentProvider
//   surface drift across Android 14/15/16 (which adds new transactions
//   regularly).  Adding methods here would not help anything and would
//   add a maintenance burden.
//
// If you add a real shim-side implementation of IContentProvider (e.g.
// for unit tests against a synthetic provider), you will need to declare
// the methods you actually call -- but at that point you're writing
// against framework.jar at runtime, not this stub, so declare them only
// to satisfy javac, not for runtime dispatch.

package android.content;

import android.os.IInterface;

public interface IContentProvider extends IInterface {
    // Intentionally empty.  See header comment.
    //
    // The real interface (Android 16) has 30+ abstract methods; we don't
    // declare any here because nothing in aosp-shim.dex calls them, and
    // at runtime framework.jar's IContentProvider provides the full
    // surface via the boot classpath.
}
