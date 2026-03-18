package com.example.multi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(this);
        String from = "";
        Intent incoming = getIntent();
        if (incoming != null) {
            from = incoming.getStringExtra("from");
        }
        tv.setText("Arrived from: " + from);
        layout.addView(tv);

        Button btn = new Button(this);
        btn.setText(R.string.go_third);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("chain", "Main->Second->Third");
                startActivity(intent);
            }
        });
        layout.addView(btn);

        setContentView(layout);
    }
}
