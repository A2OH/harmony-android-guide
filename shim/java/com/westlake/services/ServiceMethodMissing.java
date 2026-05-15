// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR2 -- ServiceMethodMissing
//
// Shared helper for Westlake M4 system-service shims to fail LOUDLY when
// app/framework code reaches a method that was not observed during M4
// discovery and therefore has no real implementation.
//
// Rationale (codex review 2026-05-12, Tier 2 §2; AGENT_SWARM_PLAYBOOK.md
// §3.5 "Speculative completeness"):
//   Each WestlakeXxxService extends a framework.jar Stub with ~hundreds
//   of abstract methods.  Only a small subset (the M4_DISCOVERY.md Tier-1
//   set) have real implementations.  The rest must have a method body so
//   the JVM accepts the class as concrete; previously they returned
//   0/null/false/no-op which SILENTLY masked unobserved service calls.
//
//   Per codex Tier 2 #1/#2: unobserved methods should fail loud so that
//   discovery surfaces them as a clear signal rather than letting noice
//   (or any real app) limp along on safe defaults that happen to be the
//   wrong answer.
//
// Usage pattern from WestlakeActivityManagerService / WestlakePowerManagerService:
//     @Override
//     public void someUnobservedMethod(...) {
//         throw ServiceMethodMissing.fail("activity", "someUnobservedMethod");
//     }
//
//   ...or with the typed-return convenience overload if a non-void return
//   is required and the compiler can't see through `throw`:
//     @Override
//     public boolean someUnobservedBooleanMethod(...) {
//         throw ServiceMethodMissing.fail("activity", "someUnobservedBooleanMethod");
//     }
//
// We throw java.lang.UnsupportedOperationException (unchecked) rather than
// android.os.RemoteException (checked) because:
//   - RemoteException requires every caller site to be inside a method
//     that declares `throws RemoteException`, which would constrain how
//     we can plumb the fail-loud helper through the codebase.
//   - UnsupportedOperationException is the JDK-canonical "you called a
//     method I don't implement" signal -- a stack trace from this
//     exception unambiguously points at the unobserved service method.
//   - The codex review explicitly allowed either ("`UnsupportedOperationException`
//     or a loud `RemoteException`").

package com.westlake.services;

/**
 * Helper for Westlake M4 service shims to throw a loud, diagnosable
 * exception when an app/framework reaches a method that was not observed
 * during M4 discovery and therefore has no real implementation.
 */
public final class ServiceMethodMissing {

    /** Project-wide marker so log greps can find every fail-loud site. */
    public static final String MARKER = "WestlakeServiceMethodMissing";

    private ServiceMethodMissing() {}

    /**
     * Build (do not throw) an UnsupportedOperationException for an
     * unobserved service method.  Callers do
     *   throw ServiceMethodMissing.fail("activity", "frobnicate");
     * so the JVM sees the method exit via throw and the stack trace
     * points at the call site.
     *
     * @param service  short service name ("activity", "power", ...)
     *                 used in the exception message; matches the
     *                 ServiceManager registration name.
     * @param method   name of the unobserved method.
     * @return a populated UnsupportedOperationException; never null.
     */
    public static UnsupportedOperationException fail(String service, String method) {
        // Also stderr-log so that even if the throw is somehow swallowed
        // by a defensive try/catch in the caller, the discovery harness
        // sees the marker line.  Keep the format on one line and easily
        // greppable: marker, service, method.
        try {
            System.err.println("[" + MARKER + "] " + service + "." + method
                    + "() called but not observed needed during discovery; "
                    + "throwing UnsupportedOperationException. If you see this, "
                    + "the method is a Tier-1 candidate -- add a real impl per "
                    + "docs/engine/M4_DISCOVERY.md.");
        } catch (Throwable ignored) { /* defensive: we are mid-failure */ }

        return new UnsupportedOperationException(
                MARKER + ": " + service + "." + method + "() not implemented. "
                + "This method was not observed needed during M4 discovery. "
                + "If you reached this from an app, treat it as a new "
                + "Tier-1 candidate (see docs/engine/M4_DISCOVERY.md).");
    }
}
