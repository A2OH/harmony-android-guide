package com.westlake.multiproctest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CounterServiceProc2 : Service() {
    private val binder = CounterBinder()
    override fun onBind(intent: Intent): IBinder {
        Log.i("MultiProcTest", "CounterServiceProc2.onBind() pid=${android.os.Process.myPid()}")
        return binder
    }
}
