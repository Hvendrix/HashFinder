package com.example.hashfinder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException

class AdditionService : Service() {
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    var mBinder: IAdditionService.Stub = object : IAdditionService.Stub() {
        @Throws(RemoteException::class)
        override fun add(x: Hash, y: String): Int {
            return if (x.checkContains(y)) {
                1
            } else 2
        }
    }
}