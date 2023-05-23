package com.example.caregiverphase2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.caregiverphase2.ui.activity.AskLocationActivity
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
            val intent = Intent(baseContext, AskLocationActivity::class.java)
            intent.putExtra("from","other")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        },0,35000)
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