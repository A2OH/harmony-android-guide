package com.westlake.multiproctest

import android.os.Binder
import android.os.IBinder
import android.os.Parcel

/**
 * Custom Binder protocol — keeps it dead simple, no AIDL.
 *  - tx 1: peek          → writes int(pid) + int(counter)
 *  - tx 2: increment     → counter++, writes int(pid) + int(counter)
 */
class CounterBinder : Binder() {
    @Volatile var counter = 0

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        when (code) {
            1 -> { /* peek */
                reply?.writeNoException()
                reply?.writeInt(android.os.Process.myPid())
                reply?.writeInt(counter)
                return true
            }
            2 -> { /* increment */
                counter++
                reply?.writeNoException()
                reply?.writeInt(android.os.Process.myPid())
                reply?.writeInt(counter)
                return true
            }
        }
        return super.onTransact(code, data, reply, flags)
    }

    companion object {
        const val TX_PEEK = 1
        const val TX_INCREMENT = 2

        /** Helper to call a remote CounterBinder. Returns Pair(pid, counter) or null. */
        fun call(remote: IBinder, code: Int): Pair<Int, Int>? {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            return try {
                if (remote.transact(code, data, reply, 0)) {
                    reply.readException()
                    Pair(reply.readInt(), reply.readInt())
                } else null
            } finally {
                data.recycle()
                reply.recycle()
            }
        }
    }
}
