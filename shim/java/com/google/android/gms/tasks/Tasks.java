package com.google.android.gms.tasks;

import java.util.Collection;

public class Tasks {
    public static <TResult> Task<TResult> forResult(TResult result) {
        return new Task<>(result);
    }

    public static <TResult> Task<TResult> forException(Exception e) {
        Task<TResult> t = new Task<>();
        t.setException(e);
        return t;
    }

    public static Task<Void> whenAll(Collection<? extends Task<?>> tasks) {
        return new Task<>((Void) null);
    }

    public static Task<Void> whenAll(Task<?>... tasks) {
        return new Task<>((Void) null);
    }
}
