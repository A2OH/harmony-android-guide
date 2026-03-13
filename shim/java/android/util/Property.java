package android.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Android-compatible Property shim.
 * Represents a property on an object; supports get/set via reflection or subclassing.
 *
 * @param <T> the host object type
 * @param <V> the property value type
 */
public abstract class Property<T, V> {

    private final Class<V> mType;
    private final String mName;

    /**
     * A constructor that takes a type and name.
     */
    public Property(Class<V> type, String name) {
        mType = type;
        mName = name;
    }

    /**
     * Returns the name of this property.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the type of this property.
     */
    public Class<V> getType() {
        return mType;
    }

    /**
     * Returns whether this property's {@link #set(Object, Object)} method is supported.
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Sets the value of this property on the given object.
     */
    public void set(T object, V value) {
        throw new UnsupportedOperationException("Property " + getName() + " is read-only");
    }

    /**
     * Returns the current value of this property from the given object.
     */
    public abstract V get(T object);

    /**
     * Creates a {@link Property} for the given class/name pair by using
     * the standard getter/setter naming convention.
     *
     * @param hostType    the class that has the property
     * @param valueType   the type of the property value
     * @param name        the name of the property (e.g. "alpha" maps to getAlpha/setAlpha)
     */
    public static <T, V> Property<T, V> of(final Class<T> hostType,
                                            final Class<V> valueType,
                                            final String name) {
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        String prefix = (valueType == Boolean.class || valueType == boolean.class) ? "is" : "get";
        final String getterName = prefix + capitalized;
        final String setterName = "set" + capitalized;

        return new Property<T, V>(valueType, name) {
            private Method getter;
            private Method setter;
            private boolean noSetter = false;

            @SuppressWarnings("unchecked")
            @Override
            public V get(T object) {
                try {
                    if (getter == null) {
                        getter = hostType.getMethod(getterName);
                        getter.setAccessible(true);
                    }
                    return (V) getter.invoke(object);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("No such method: " + getterName, e);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Error calling " + getterName, e);
                }
            }

            @Override
            public void set(T object, V value) {
                if (noSetter) {
                    throw new UnsupportedOperationException(
                            "Property " + getName() + " is read-only");
                }
                try {
                    if (setter == null) {
                        setter = hostType.getMethod(setterName, valueType);
                        setter.setAccessible(true);
                    }
                    setter.invoke(object, value);
                } catch (NoSuchMethodException e) {
                    noSetter = true;
                    throw new UnsupportedOperationException(
                            "Property " + getName() + " is read-only", e);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Error calling " + setterName, e);
                }
            }

            @Override
            public boolean isReadOnly() {
                return noSetter;
            }
        };
    }
}
