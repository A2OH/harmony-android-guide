package com.westlake.host

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Generic in-process service-slot proxy. Runs in its own host process (declared via
 * android:process=":mp_procN" in the host manifest). Loads the foreign service class
 * via createPackageContext + classloader and forwards onBind/onStartCommand/onDestroy.
 *
 * Routing decision (which foreign service to load) is conveyed via the launching intent:
 *   intent.getStringExtra(EXTRA_TARGET_CLASS) = "com.westlake.multiproctest.CounterServiceProc1"
 *
 * Subclasses differ ONLY in their android:process attribute in the manifest. Their class
 * here just exists so the manifest has something distinct to point at; the actual logic
 * is identical.
 */
abstract class MpServiceSlot : Service() {
    private val TAG = "MpServiceSlot"
    private var inner: Service? = null

    override fun onBind(intent: Intent): IBinder? {
        val targetCls = intent.getStringExtra(EXTRA_TARGET_CLASS)
            ?: intent.component?.className?.takeIf { it.startsWith(FOREIGN_PKG) }
            ?: run {
                Log.w(TAG, "no target class in intent: $intent")
                return null
            }
        val svc = inner ?: instantiate(targetCls) ?: return null
        inner = svc
        Log.i(TAG, "onBind $targetCls in pid=${android.os.Process.myPid()} → $svc")
        return svc.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val svc = inner
        if (svc != null && intent != null) {
            return svc.onStartCommand(intent, flags, startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        try { inner?.onDestroy() } catch (_: Throwable) {}
        inner = null
        super.onDestroy()
    }

    private fun instantiate(targetCls: String): Service? {
        return try {
            val foreignCtx = createPackageContext(FOREIGN_PKG,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            val cls = foreignCtx.classLoader.loadClass(targetCls)
            val svc = cls.newInstance() as Service
            // Reflective Service.attach(...)
            val attach = Service::class.java.declaredMethods.firstOrNull { it.name == "attach" }
                ?: throw IllegalStateException("Service.attach method not found")
            attach.isAccessible = true
            val at = Class.forName("android.app.ActivityThread")
                .getDeclaredField("sCurrentActivityThread")
                .also { it.isAccessible = true }
                .get(null) ?: throw IllegalStateException("No ActivityThread")
            val args = mutableListOf<Any?>()
            for (param in attach.parameterTypes) {
                args += when {
                    param == Context::class.java -> foreignCtx
                    param.name == "android.app.ActivityThread" -> at
                    param == String::class.java -> cls.name
                    param == android.os.IBinder::class.java -> android.os.Binder()
                    param == android.app.Application::class.java -> application
                    else -> null
                }
            }
            attach.invoke(svc, *args.toTypedArray())
            svc.onCreate()
            svc
        } catch (e: Throwable) {
            val r = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
            Log.e(TAG, "instantiate($targetCls) FAIL: ${r.javaClass.simpleName}: ${r.message}", r)
            null
        }
    }

    companion object {
        const val EXTRA_TARGET_CLASS = "mp_slot_target"
        const val FOREIGN_PKG = "com.westlake.multiproctest"
    }
}

class MpServiceSlot1 : MpServiceSlot()
class MpServiceSlot2 : MpServiceSlot()
class MpServiceSlot3 : MpServiceSlot()
class MpServiceSlot4 : MpServiceSlot()
