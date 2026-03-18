package com.example.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Intent data = getIntent();
        String name = "";
        String email = "";
        boolean agreed = false;
        if (data != null) {
            name = data.getStringExtra("name");
            email = data.getStringExtra("email");
            agreed = data.getBooleanExtra("agreed", false);
        }

        TextView tvName = new TextView(this);
        tvName.setText("Name: " + name);
        layout.addView(tvName);

        TextView tvEmail = new TextView(this);
        tvEmail.setText("Email: " + email);
        layout.addView(tvEmail);

        TextView tvAgreed = new TextView(this);
        tvAgreed.setText("Agreed: " + agreed);
        layout.addView(tvAgreed);

        setContentView(layout);
    }
}
