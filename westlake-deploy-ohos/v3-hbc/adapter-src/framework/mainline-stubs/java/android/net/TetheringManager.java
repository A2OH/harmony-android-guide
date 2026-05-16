// Mainline APEX stub.  android.net.TetheringManager lives in the Tethering
// APEX module that OH does not ship.  SystemServiceRegistry.<clinit> registers
// "tethering" service with this Class literal; without this stub the entire
// SSR.<clinit> hits a bare NoClassDefFoundError, which then poisons every
// downstream Resources / ContextImpl init.  AA.23 root-cause: this was the
// SOLE missing class out of 323 SSR class refs.
//
// Constructor signature matches AOSP 14's
//   public TetheringManager(@NonNull Context context, @NonNull Supplier<IBinder> connectorSupplier)
// — empty body; the underlying tethering connector is never used by Hello
// World, so a no-op is fine.

package android.net;

import android.content.Context;

import java.util.function.Supplier;

public class TetheringManager {
    public TetheringManager(Context context, Supplier<android.os.IBinder> connectorSupplier) {
        // no-op
    }
}
