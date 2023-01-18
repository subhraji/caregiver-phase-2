package com.example.caregiverphase2.utils

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.example.caregiverphase2.ui.activity.FullScreenNotifyActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("message","message recieved..."+message.notification?.title)
        if(message.notification != null){
            getNotification(message.notification?.title.toString(), message.notification?.body.toString())
        }
    }

    private fun getRemoveView(title: String?, body: String?): RemoteViews? {
        val remoteview = RemoteViews("com.example.caregiverphase2",com.example.caregiverphase2.R.layout.notification)
        remoteview.setTextViewText(com.example.caregiverphase2.R.id.noti_title_tv,title)
        remoteview.setTextViewText(com.example.caregiverphase2.R.id.desc_tv,body)
        remoteview.setImageViewResource(com.example.caregiverphase2.R.id.notification_img,com.example.caregiverphase2.R.drawable.ic_baseline_notifications_24)

        return remoteview
    }

    private fun getNotification(title: String?, body: String?){
        //val intent = Intent(this, MainActivity::class.java)

        val intent = Intent(this, FullScreenNotifyActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.putExtra("title",title)
        startActivity(intent)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent
            .getActivity(this, 0, intent, 0)

        val channelId = "Default"
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification_clear_all)
            .setContentTitle(title)
            .setContentText(body)
            .setCategory(Notification.CATEGORY_CALL)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), AudioManager.STREAM_ALARM)
            .setPriority(Notification.PRIORITY_MAX)
            .setTicker(title)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoveView(title,body))

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("token","new token")
    }

}