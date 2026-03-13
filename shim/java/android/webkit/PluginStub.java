package android.webkit;
import android.content.Context;
import android.view.View;

public interface PluginStub {
    View getEmbeddedView(int p0, Context p1);
    View getFullScreenView(int p0, Context p1);
}
