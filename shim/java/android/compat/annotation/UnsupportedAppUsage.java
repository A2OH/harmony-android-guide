package android.compat.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface UnsupportedAppUsage {
    long maxTargetSdk() default Long.MAX_VALUE;
    String publicAlternatives() default "";
    long trackingBug() default 0;
    String expectedSignature() default "";
    String implicitMember() default "";
    long overrideSourcePosition() default 0;
}
