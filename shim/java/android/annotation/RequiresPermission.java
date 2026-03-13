package android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface RequiresPermission {
    String value() default "";
    String[] allOf() default {};
    String[] anyOf() default {};
}
