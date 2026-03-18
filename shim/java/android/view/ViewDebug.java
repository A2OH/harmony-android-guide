package android.view;

import java.lang.annotation.*;

public class ViewDebug {
    public ViewDebug() {}

    public static void dumpCapturedView(String p0, Object p1) {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface ExportedProperty {
        String category() default "";
        boolean deepExport() default false;
        FlagToString[] flagMapping() default {};
        IntToString[] mapping() default {};
        IntToString[] indexMapping() default {};
        boolean resolveId() default false;
        String prefix() default "";
        boolean formatToHexString() default false;
        boolean hasAdjacentMapping() default false;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    public @interface FlagToString {
        int mask() default 0;
        int equals() default 0;
        String name() default "";
        boolean outputIf() default true;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    public @interface IntToString {
        int from() default 0;
        String to() default "";
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface CapturedViewProperty {
        boolean retrieveReturn() default false;
    }
}
