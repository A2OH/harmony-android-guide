package com.westlake.cutoffcanary;

import android.app.Activity;
import android.app.Application;
import android.app.AppComponentFactory;
import android.content.Intent;

public final class CanaryAppComponentFactory extends AppComponentFactory {
    private static final String REFLECT_APP_STAGE = "L4WATAPPREFLECT";

    public CanaryAppComponentFactory() {
        CanaryLog.mark("L4FACTORY_CTOR_OK", "factory=" + getClass().getName());
    }

    @Override
    public Activity instantiateActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        CanaryLog.mark("L4FACTORY_INSTANTIATE_ACTIVITY_OK", "class=" + className);
        Activity activity = super.instantiateActivity(cl, className, intent);
        CanaryLog.mark("L4FACTORY_ACTIVITY_RETURNED_OK",
                "activity=" + activity.getClass().getName());
        return activity;
    }

    @Override
    public Application instantiateApplication(ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        CanaryLog.mark("L4APPFACTORY_INSTANTIATE_APPLICATION_OK", "class=" + className);
        Application app;
        if ("com.westlake.cutoffcanary.CanaryApp".equals(className)) {
            if (REFLECT_APP_STAGE.equals(System.getProperty("westlake.canary.stage"))) {
                CanaryLog.mark("L4APPREFLECT_STAGE_OK", "stage=" + REFLECT_APP_STAGE);
                CanaryLog.mark("L4APPREFLECT_SUPER_CALL", "class=" + className);
                app = super.instantiateApplication(cl, className);
                CanaryLog.mark("L4APPREFLECT_SUPER_RETURNED",
                        "application=" + app.getClass().getName());
                CanaryLog.mark("L4APPREFLECT_APPLICATION_RETURNED_OK",
                        "application=" + app.getClass().getName());
            } else {
                CanaryLog.mark("L4APPFACTORY_DIRECT_CANARY_APP_OK", "class=" + className);
                app = new CanaryApp();
            }
        } else {
            try {
                Class<?> raw = cl.loadClass(className);
                java.lang.reflect.Constructor<?> ctor = raw.getDeclaredConstructor();
                ctor.setAccessible(true);
                app = Application.class.cast(ctor.newInstance());
            } catch (ClassNotFoundException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw e;
            } catch (Throwable ignored) {
                InstantiationException e = new InstantiationException(className);
                try {
                    e.initCause(ignored);
                } catch (Throwable causeIgnored) {
                }
                throw e;
            }
        }
        CanaryLog.mark("L4APPFACTORY_APPLICATION_RETURNED_OK",
                "application=" + app.getClass().getName());
        return app;
    }
}
