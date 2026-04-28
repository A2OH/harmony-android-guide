package androidx.appcompat.app;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentActivity;

public class AppCompatActivity extends FragmentActivity {
    public AppCompatActivity() { super(); }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() { return null; }
    public void setSupportActionBar(Object toolbar) { /* stub */ }
    public void supportRequestWindowFeature(int featureId) { /* stub */ }

    public static class ActionBar {
        public void setDisplayHomeAsUpEnabled(boolean enabled) {}
        public void setDisplayShowTitleEnabled(boolean enabled) {}
        public void setTitle(CharSequence title) {}
        public void hide() {}
        public void show() {}
    }
}
