package android.animation;

/**
 * Android-compatible PropertyValuesHolder shim.
 * Holds a property name and a set of values for use with ObjectAnimator.
 * Pure Java stub — no animation is driven on OpenHarmony.
 */
public class PropertyValuesHolder {

    private String mPropertyName;
    private Object[] mValues;

    private PropertyValuesHolder(String propertyName) {
        mPropertyName = propertyName;
    }

    // ── Factory methods ──

    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            pvh.mValues[i] = values[i];
        }
        return pvh;
    }

    public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            pvh.mValues[i] = values[i];
        }
        return pvh;
    }

    public static PropertyValuesHolder ofObject(String propertyName, Object evaluator, Object... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mValues = values != null ? values.clone() : new Object[0];
        return pvh;
    }

    public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mValues = values != null ? values.clone() : new Object[0];
        return pvh;
    }

    // ── Accessors ──

    public String getPropertyName() {
        return mPropertyName;
    }

    public void setPropertyName(String propertyName) {
        mPropertyName = propertyName;
    }

    public void setFloatValues(float... values) {
        mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            mValues[i] = values[i];
        }
    }

    public void setIntValues(int... values) {
        mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            mValues[i] = values[i];
        }
    }

    @Override
    public String toString() {
        return "PropertyValuesHolder{property=" + mPropertyName + "}";
    }
}
