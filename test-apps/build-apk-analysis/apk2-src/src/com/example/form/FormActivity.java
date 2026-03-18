package com.example.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class FormActivity extends Activity {
    private EditText nameInput;
    private EditText emailInput;
    private CheckBox agreeCheck;
    private RadioGroup genderGroup;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView nameLabel = new TextView(this);
        nameLabel.setText(R.string.label_name);
        layout.addView(nameLabel);

        nameInput = new EditText(this);
        nameInput.setHint(R.string.hint_name);
        layout.addView(nameInput);

        TextView emailLabel = new TextView(this);
        emailLabel.setText(R.string.label_email);
        layout.addView(emailLabel);

        emailInput = new EditText(this);
        emailInput.setHint(R.string.hint_email);
        layout.addView(emailInput);

        agreeCheck = new CheckBox(this);
        agreeCheck.setText(R.string.label_agree);
        layout.addView(agreeCheck);

        genderGroup = new RadioGroup(this);
        maleRadio = new RadioButton(this);
        maleRadio.setText("Male");
        genderGroup.addView(maleRadio);
        femaleRadio = new RadioButton(this);
        femaleRadio.setText("Female");
        genderGroup.addView(femaleRadio);
        layout.addView(genderGroup);

        categorySpinner = new Spinner(this);
        layout.addView(categorySpinner);

        Button submitBtn = new Button(this);
        submitBtn.setText(R.string.btn_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this, ResultActivity.class);
                intent.putExtra("name", nameInput.getText().toString());
                intent.putExtra("email", emailInput.getText().toString());
                intent.putExtra("agreed", agreeCheck.isChecked());
                startActivity(intent);
            }
        });
        layout.addView(submitBtn);

        setContentView(layout);
    }
}
