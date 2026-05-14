package com.westlake.multiproctest

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private var b1: IBinder? = null
    private var b2: IBinder? = null
    private lateinit var status: TextView
    private val TAG = "MultiProcTest"

    private val conn1 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            b1 = service
            Log.i(TAG, "conn1 bound to $name binder=$service")
            refresh()
        }
        override fun onServiceDisconnected(name: ComponentName?) { b1 = null; refresh() }
    }
    private val conn2 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            b2 = service
            Log.i(TAG, "conn2 bound to $name binder=$service")
            refresh()
        }
        override fun onServiceDisconnected(name: ComponentName?) { b2 = null; refresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
            setBackgroundColor(0xFF101820.toInt())
        }
        status = TextView(this).apply {
            textSize = 14f; setTextColor(0xFFFFFFFF.toInt()); typeface = android.graphics.Typeface.MONOSPACE
        }
        root.addView(status)
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        row.addView(Button(this).apply {
            text = "++ proc1"
            setOnClickListener { tx(b1, CounterBinder.TX_INCREMENT) }
        })
        row.addView(Button(this).apply {
            text = "++ proc2"
            setOnClickListener { tx(b2, CounterBinder.TX_INCREMENT) }
        })
        row.addView(Button(this).apply {
            text = "refresh"
            setOnClickListener { refresh() }
        })
        root.addView(row)
        setContentView(root)

        bindService(Intent(this, CounterServiceProc1::class.java), conn1, BIND_AUTO_CREATE)
        bindService(Intent(this, CounterServiceProc2::class.java), conn2, BIND_AUTO_CREATE)
        refresh()
    }

    private fun tx(b: IBinder?, code: Int) {
        b ?: return refresh()
        val r = CounterBinder.call(b, code)
        Log.i(TAG, "tx code=$code → pid=${r?.first} counter=${r?.second}")
        refresh()
    }

    private fun refresh() {
        val main = "main pid=${android.os.Process.myPid()}"
        val p1 = b1?.let { CounterBinder.call(it, CounterBinder.TX_PEEK)?.let { (pid, c) -> "proc1 pid=$pid counter=$c" } } ?: "proc1 not bound"
        val p2 = b2?.let { CounterBinder.call(it, CounterBinder.TX_PEEK)?.let { (pid, c) -> "proc2 pid=$pid counter=$c" } } ?: "proc2 not bound"
        status.text = "$main\n$p1\n$p2"
    }

    override fun onDestroy() {
        try { unbindService(conn1) } catch (_: Throwable) {}
        try { unbindService(conn2) } catch (_: Throwable) {}
        super.onDestroy()
    }
}
