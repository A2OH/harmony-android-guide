package com.example.multi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThirdActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(R.string.made_it);
        layout.addView(tv);

        TextView tvChain = new TextView(this);
        String chain = "";
        Intent incoming = getIntent();
        if (incoming != null) {
            chain = incoming.getStringExtra("chain");
        }
        tvChain.setText("Navigation chain: " + chain);
        layout.addView(tvChain);

        setContentView(layout);
    }
}
