package android.view;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface RemotableViewMethod {
    String asyncImpl() default "";
}
