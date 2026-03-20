package android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Stub: VisibleForTesting annotation with visibility levels.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface VisibleForTesting {
    int visibility() default Visibility.PRIVATE;

    class Visibility {
        public static final int PRIVATE = 0;
        public static final int PACKAGE = 1;
        public static final int PROTECTED = 2;
        public static final int NONE = 3;
    }
}
