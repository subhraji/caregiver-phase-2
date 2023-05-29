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
import com.example.caregiverphase2.ui.activity.FullScreenNotifyActivity
import com.example.caregiverphase2.ui.activity.LocationConfirmActivity
import com.example.caregiverphase2.ui.activity.MainActivity
import com.example.caregiverphase2.utils.Constants.CHANNEL_ID
import com.example.caregiverphase2.utils.Constants.MUSIC_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.concurrent.timerTask

class ForegroundLocationService: Service() {
    private lateinit var musicPlayer: MediaPlayer

    var counter = 0
    private val timer: Timer? = Timer()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

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
        scope.launch {
            doSomethingRepeatedly()
        }

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
            val intent = Intent(baseContext, LocationConfirmActivity::class.java)
            /*intent.putExtra("from","timer") */
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            startActivity(intent)
        },900000,900000)
    }

    private fun showNotification(){
        val notificationIntent = Intent(this, LocationConfirmActivity::class.java)
        notificationIntent.putExtra("from","notification")

        /*val intent = Intent(this, LocationConfirmActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.putExtra("from","notification")
        startActivity(intent)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)*/

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
        job.cancel()
    }
}