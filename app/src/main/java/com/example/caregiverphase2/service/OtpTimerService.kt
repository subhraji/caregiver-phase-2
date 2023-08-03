package com.example.caregiverphase2.service

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData

class OtpTimerService: LifecycleService() {

    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
    }

    var cTimer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        doSomethingRepeatedly()

        return START_STICKY
    }

    private fun doSomethingRepeatedly() {
        cTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val millis = millisUntilFinished / 1000L
                timeRunInMillis.postValue(millis)
            }
            override fun onFinish() {
                cancelTimer()
            }
        }
        (cTimer as CountDownTimer).start()
    }

    fun cancelTimer() {
        if (cTimer != null) {
            cTimer!!.cancel()
            killService()
        }
    }

    fun killService(){
        cTimer!!.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    override fun onDestroy() {
        super.onDestroy()
        killService()
    }
}