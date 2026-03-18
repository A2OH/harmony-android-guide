package android.annotation;

/** Auto-generated stub for AOSP compilation. */
import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER,
         ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE,
         ElementType.ANNOTATION_TYPE})
public @interface UiThread {
    String[] value() default {};
    long maxTargetSdk() default Long.MAX_VALUE;
    int[] apis() default {};
    long from() default 0;
    long to() default Long.MAX_VALUE;
}
