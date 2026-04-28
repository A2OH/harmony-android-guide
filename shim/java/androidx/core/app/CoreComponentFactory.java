package androidx.core.app;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Intent;

public class CoreComponentFactory extends AppComponentFactory {
    public interface CompatWrapped {
        Object getWrapper();
    }

    @Override
    public Activity instantiateActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return checkCompatWrapper(super.instantiateActivity(cl, className, intent));
    }

    @Override
    public Application instantiateApplication(ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return checkCompatWrapper(super.instantiateApplication(cl, className));
    }

    @Override
    public Service instantiateService(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return checkCompatWrapper(super.instantiateService(cl, className, intent));
    }

    @Override
    public BroadcastReceiver instantiateReceiver(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return checkCompatWrapper(super.instantiateReceiver(cl, className, intent));
    }

    @Override
    public ContentProvider instantiateProvider(ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return checkCompatWrapper(super.instantiateProvider(cl, className));
    }

    @SuppressWarnings("unchecked")
    static <T> T checkCompatWrapper(T obj) {
        if (obj instanceof CompatWrapped) {
            Object wrapper = ((CompatWrapped) obj).getWrapper();
            if (wrapper != null) {
                return (T) wrapper;
            }
        }
        return obj;
    }
}
