package com.example.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Single-Activity calculator with a programmatic button grid.
 * Supports: 0-9, +, -, *, /, =, C, .
 */
public class CalculatorActivity extends Activity {
    private TextView display;

    // Internal state
    private double accumulator = 0;
    private String pendingOp = "";
    private String currentInput = "";
    private boolean startNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        // Display
        display = new TextView();
        display.setText("0");
        display.setTextSize(32);
        display.setTextColor(0xFF000000);
        root.addView(display);

        // Button rows
        String[][] rows = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"},
            {"C"}
        };

        for (int r = 0; r < rows.length; r++) {
            LinearLayout row = new LinearLayout(new android.content.Context());
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int c = 0; c < rows[r].length; c++) {
                final String label = rows[r][c];
                Button btn = new Button(new android.content.Context());
                btn.setText(label);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleButton(label);
                    }
                });
                row.addView(btn);
            }
            root.addView(row);
        }

        setContentView(root);
    }

    /** Public API: get current display text. */
    public String getDisplay() {
        return display.getText().toString();
    }

    /** Public API: simulate a button press by label. */
    public void pressButton(String label) {
        handleButton(label);
    }

    private void handleButton(String label) {
        if ("C".equals(label)) {
            accumulator = 0;
            pendingOp = "";
            currentInput = "";
            startNewInput = true;
            display.setText("0");
            return;
        }

        if ("=".equals(label)) {
            if (currentInput.length() > 0 && pendingOp.length() > 0) {
                double operand = parseDouble(currentInput);
                accumulator = compute(accumulator, operand, pendingOp);
                pendingOp = "";
                currentInput = "";
                startNewInput = true;
                display.setText(formatResult(accumulator));
            }
            return;
        }

        if ("+".equals(label) || "-".equals(label) || "*".equals(label) || "/".equals(label)) {
            if (currentInput.length() > 0 && pendingOp.length() > 0) {
                // Chain: evaluate pending op first
                double operand = parseDouble(currentInput);
                accumulator = compute(accumulator, operand, pendingOp);
            } else if (currentInput.length() > 0) {
                accumulator = parseDouble(currentInput);
            }
            pendingOp = label;
            currentInput = "";
            startNewInput = true;
            display.setText(formatResult(accumulator));
            return;
        }

        // Digit or decimal point
        if (startNewInput) {
            currentInput = "";
            startNewInput = false;
        }
        currentInput = currentInput + label;
        display.setText(currentInput);
    }

    private double compute(double a, double b, String op) {
        if ("+".equals(op)) return a + b;
        if ("-".equals(op)) return a - b;
        if ("*".equals(op)) return a * b;
        if ("/".equals(op)) {
            if (b == 0) return 0;
            return a / b;
        }
        return b;
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatResult(double val) {
        // If the value is a whole number, display without decimal
        if (val == (long) val) {
            return String.valueOf((long) val);
        }
        return String.valueOf(val);
    }
}
