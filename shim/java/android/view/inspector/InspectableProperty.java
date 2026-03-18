package android.view.inspector;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface InspectableProperty {
    String name() default "";
    boolean hasAttributeId() default true;
    int attributeId() default 0;

    EnumEntry[] enumMapping() default {};
    FlagEntry[] flagMapping() default {};
    ValueType valueType() default ValueType.NONE;

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    @interface EnumEntry {
        String name() default "";
        int value() default 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    @interface FlagEntry {
        String name() default "";
        int target() default 0;
        int mask() default 0;
    }

    enum ValueType {
        NONE,
        INFERRED,
        INT_ENUM,
        INT_FLAG,
        RESOURCE_ID,
        COLOR,
        GRAVITY,
    }
}
