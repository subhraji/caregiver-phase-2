package com.example.caregiverphase2.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.caregiverphase2.R
import com.example.caregiverphase2.ui.activity.AskLocationActivity
import com.example.caregiverphase2.ui.activity.MainActivity
import com.example.caregiverphase2.utils.Constants.CHANNEL_ID
import com.example.caregiverphase2.utils.Constants.MUSIC_NOTIFICATION_ID
import java.io.IOException
import java.util.*
import kotlin.concurrent.timerTask

class ForegroundLocationService: Service() {
    private lateinit var musicPlayer: MediaPlayer

    var counter = 0
    private val timer: Timer? = Timer()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initMusic()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()

        //playPauseMusic()
        doSomethingRepeatedly()

        return START_STICKY
    }

    private fun playPauseMusic(){
        if(musicPlayer.isPlaying){
            musicPlayer.stop()
        }else{
            musicPlayer.start()
        }
    }

    private fun doSomethingRepeatedly() {
        timer?.scheduleAtFixedRate(timerTask {
            Log.e("NIlu_TAG","${counter++} Hello World")
            musicPlayer.start()
            val intent = Intent(baseContext, AskLocationActivity::class.java)
            intent.putExtra("from","other")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        },0,20000)
    }

    private fun showNotification(){
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        val notification = Notification
            .Builder(this, CHANNEL_ID)
            .setContentText("Music Player")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(MUSIC_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "my service channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun initMusic(){
        musicPlayer = MediaPlayer()
        try {
            musicPlayer?.setDataSource("https://peaceworc-phase2-live.ekodusproject.tech/Caregiver/Uploads/mp3/siren.mp3")
            musicPlayer?.isLooping = false
            musicPlayer?.prepare()
        } catch (e: IOException) {
            Log.e(PackageManagerCompat.LOG_TAG, "prepare() failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicPlayer.isPlaying){
            musicPlayer.stop()
        }

        timer?.let {
            it.cancel()
        }
    }
}