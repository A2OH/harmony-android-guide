package com.westlake.cutoffcanary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class StageActivity extends Activity {
    private String stage = "L1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stage = resolveStage();
        CanaryLog.mark(stage + "_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        try {
            super.onCreate(savedInstanceState);
            CanaryLog.raw(stage + "_SUPER_ON_CREATE_OK", "activity=" + getClass().getName());
        } catch (Throwable t) {
            CanaryLog.raw(stage + "_SUPER_ON_CREATE_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
            throw t;
        }

        CanaryLog.mark(stage + "_ON_CREATE", "saved=" + (savedInstanceState != null));

        if ("L1".equals(stage)) {
            CanaryLog.mark("L1_VIEW_BUILD_BEGIN", "plain view");
            View content = buildL1ProgrammaticLayout();
            CanaryLog.mark("L1_VIEW_BUILD_OK", "plain view");
            setContentView(content);
            CanaryLog.mark("L1_OK", "programmatic activity view ready");
            return;
        }

        if ("L2".equals(stage)) {
            setContentView(R.layout.activity_stage);
            bindL2Layout();
            CanaryLog.mark("L2_OK", "xml resources/layout inflated");
            return;
        }

        setContentView(buildPlaceholderLayout(stage));
        CanaryLog.mark(stage + "_UNIMPLEMENTED", "placeholder only");
    }

    @Override
    protected void onStart() {
        CanaryLog.mark(stage + "_ON_START_ENTER", "activity=" + getClass().getName());
        try {
            super.onStart();
            CanaryLog.raw(stage + "_SUPER_ON_START_OK", "activity=" + getClass().getName());
        } catch (Throwable t) {
            CanaryLog.raw(stage + "_SUPER_ON_START_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
            throw t;
        }
        CanaryLog.mark(stage + "_ON_START", "activity=" + getClass().getName());
    }

    @Override
    protected void onResume() {
        CanaryLog.mark(stage + "_ON_RESUME_ENTER", "package=" + getPackageName());
        try {
            super.onResume();
            CanaryLog.raw(stage + "_SUPER_ON_RESUME_OK", "package=" + getPackageName());
        } catch (Throwable t) {
            CanaryLog.raw(stage + "_SUPER_ON_RESUME_ERR",
                    t.getClass().getName() + ": " + t.getMessage());
            throw t;
        }
        CanaryLog.mark(stage + "_ON_RESUME", "package=" + getPackageName());
    }

    private String resolveStage() {
        String candidate = null;
        if (getIntent() != null) {
            candidate = getIntent().getStringExtra("stage");
        }
        if (candidate == null || candidate.length() == 0) {
            candidate = "L1";
        }
        return candidate;
    }

    private View buildL1ProgrammaticLayout() {
        return new View(this);
    }

    private void bindL2Layout() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView stageLabel = (TextView) findViewById(R.id.stage);
        TextView details = (TextView) findViewById(R.id.details);
        Button action = (Button) findViewById(R.id.action);

        if (title != null) {
            title.setText(getString(R.string.title_l2));
        }
        if (stageLabel != null) {
            stageLabel.setText("Stage " + stage + " via XML layout");
        }
        if (details != null) {
            details.setText("Resources, layout inflation, and widget lookup are alive.");
        }
        if (action != null) {
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CanaryLog.mark("L2_CLICK", "xml button click");
                }
            });
        }
    }

    private View buildPlaceholderLayout(String unresolvedStage) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Cutoff Canary " + unresolvedStage);
        root.addView(title);

        TextView details = new TextView(this);
        details.setText("Stage not implemented yet.");
        root.addView(details);
        return root;
    }
}
