package android.view.inspector;

/**
 * A2OH shim: android.view.inspector.InspectionCompanion
 *
 * Companion interface for view inspection, allowing property
 * mapping and reading for a given view type.
 */
public interface InspectionCompanion<Object> {
    void mapProperties(PropertyMapper propertyMapper);
    void readProperties(Object node, Object propertyReader);
}
