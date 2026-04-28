package com.westlake.materialxmlprobe;

import android.app.Application;

public final class MaterialXmlProbeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MaterialXmlProbeLog.mark("MATERIAL_XML_APP_OK package=" + getPackageName());
    }
}
