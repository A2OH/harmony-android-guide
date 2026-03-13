package android.content.pm;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class LabeledIntent extends Intent {
    public LabeledIntent(Intent p0, String p1, int p2, int p3) {}
    public LabeledIntent(Intent p0, String p1, CharSequence p2, int p3) {}
    public LabeledIntent(String p0, int p1, int p2) {}
    public LabeledIntent(String p0, CharSequence p1, int p2) {}

    public int getIconResource() { return 0; }
    public int getLabelResource() { return 0; }
    public CharSequence getNonLocalizedLabel() { return null; }
    public String getSourcePackage() { return null; }
    public Drawable loadIcon(PackageManager p0) { return null; }
    public CharSequence loadLabel(PackageManager p0) { return null; }
}
