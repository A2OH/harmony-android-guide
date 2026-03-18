package android.view.inspector;

/**
 * Stub: android.view.inspector.InspectionCompanion
 */
public interface InspectionCompanion<T> {
    void mapProperties(PropertyMapper propertyMapper);
    void readProperties(T node, PropertyReader propertyReader);

    class UninitializedPropertyMapException extends RuntimeException {
        public UninitializedPropertyMapException() { super(); }
        public UninitializedPropertyMapException(String msg) { super(msg); }
    }
}
