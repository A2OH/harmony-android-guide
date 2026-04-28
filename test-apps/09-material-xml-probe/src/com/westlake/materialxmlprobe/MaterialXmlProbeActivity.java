package com.westlake.materialxmlprobe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public final class MaterialXmlProbeActivity extends Activity {
    private MaterialButton materialButton;
    private volatile boolean materialButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialXmlProbeLog.mark("MATERIAL_XML_INFLATE_BEGIN layout=activity_material_xml_probe");
        try {
            setContentView(R.layout.activity_material_xml_probe);
            verifyInflatedTree();
            wireClickMarker();
        } catch (Throwable t) {
            MaterialXmlProbeLog.mark("MATERIAL_XML_INFLATE_FAIL err="
                    + MaterialXmlProbeLog.token(t.getClass().getName())
                    + " msg=" + MaterialXmlProbeLog.token(t.getMessage()));
            throw t;
        }
    }

    private void verifyInflatedTree() {
        View root = require(R.id.probe_root, android.widget.LinearLayout.class, "root");
        require(R.id.probe_input_layout, TextInputLayout.class, "TextInputLayout");
        require(R.id.probe_input, TextInputEditText.class, "TextInputEditText");
        require(R.id.probe_card, MaterialCardView.class, "MaterialCardView");
        require(R.id.probe_chip_group, ChipGroup.class, "ChipGroup");
        require(R.id.probe_chip_one, Chip.class, "Chip");
        require(R.id.probe_chip_two, Chip.class, "Chip");
        require(R.id.probe_slider, Slider.class, "Slider");
        materialButton = (MaterialButton) require(
                R.id.probe_button, MaterialButton.class, "MaterialButton");
        require(R.id.probe_bottom_nav, BottomNavigationView.class, "BottomNavigationView");

        int materialCount = countMaterialViews(root);
        MaterialXmlProbeLog.mark("MATERIAL_XML_TREE_OK materialViews=" + materialCount
                + " root=" + root.getClass().getName());
    }

    private View require(int id, Class expectedClass, String label) {
        View view = findViewById(id);
        if (view == null) {
            throw new IllegalStateException(label + " missing");
        }
        String actual = view.getClass().getName();
        if (!expectedClass.isInstance(view)) {
            MaterialXmlProbeLog.mark("MATERIAL_XML_TAG_FAIL tag=" + label
                    + " actual=" + MaterialXmlProbeLog.token(actual)
                    + " expected=" + MaterialXmlProbeLog.token(expectedClass.getName()));
            throw new IllegalStateException(label + " was " + actual);
        }
        MaterialXmlProbeLog.mark("MATERIAL_XML_TAG_OK tag=" + label + " class=" + actual);
        return view;
    }

    private int countMaterialViews(View view) {
        if (view == null) {
            return 0;
        }
        int count = view.getClass().getName().startsWith("com.google.android.material.") ? 1 : 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countMaterialViews(group.getChildAt(i));
            }
        }
        return count;
    }

    private void wireClickMarker() {
        materialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                materialButtonClicked = true;
                MaterialXmlProbeLog.mark("MATERIAL_GENERIC_HIT_OK source=MaterialButton class="
                        + view.getClass().getName());
            }
        });
    }

}
