package com.example.caregiverphase2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.concurrent.timerTask

class BackgroundLocationService: Service() {
    var counter = 0
    private val timer: Timer? = Timer()
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        doSomethingRepeatedly()
        return START_STICKY
    }

    private fun doSomethingRepeatedly() {
        timer?.scheduleAtFixedRate(timerTask {
            Log.e("NIlu_TAG","${counter++} Hello World")
        },0,10000)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.let {
            it.cancel()
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val UPDATE_INTERVAL = 1000
    }
}