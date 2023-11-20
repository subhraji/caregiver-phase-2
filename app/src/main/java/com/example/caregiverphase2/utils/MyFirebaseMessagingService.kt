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
import com.example.caregiverphase2.ui.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("message","message recieved..."+message.data)
        if(message.notification != null){

            if(message.data["notification_type"] == "fullscreen"){
                getFullScreenNotification(message.notification?.title.toString(),
                    message.notification?.body,
                    message.data["job_title"],
                    message.data["job_amount"],
                    message.data["job_start_date"],
                    message.data["job_start_time"],
                    message.data["job_end_date"],
                    message.data["job_end_time"],
                    message.data["care_type"],
                    message.data["address"],
                    message.data["rewards"],
                    )
            }else{
                getNotification(message.notification?.title.toString(), message.notification?.body)
            }

        }
    }

    private fun getRemoveView(title: String?, body: String?): RemoteViews? {
        val remoteview = RemoteViews("com.example.caregiverphase2",com.example.caregiverphase2.R.layout.notification)
        remoteview.setTextViewText(com.example.caregiverphase2.R.id.noti_title_tv,title)
        remoteview.setTextViewText(com.example.caregiverphase2.R.id.desc_tv,body)
        remoteview.setImageViewResource(com.example.caregiverphase2.R.id.notification_img,com.example.caregiverphase2.R.drawable.ic_baseline_notifications_24)

        return remoteview
    }

    private fun getFullScreenNotification(title: String?,
                                          body: String?,
                                          job_title: String?,
                                          job_amount: String?,
                                          job_start_date: String?,
                                          job_start_time: String?,
                                          job_end_date: String?,
                                          job_end_time: String?,
                                          care_type: String?,
                                          address: String?,
                                          rewards: String?
                                          ){
        val intent = Intent(this, FullScreenNotifyActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.putExtra("title",job_title)
        intent.putExtra("job_amount",job_amount)
        intent.putExtra("job_start_date",job_start_date)
        intent.putExtra("job_start_time",job_start_time)
        intent.putExtra("job_end_date",job_end_date)
        intent.putExtra("job_end_time",job_end_time)
        intent.putExtra("care_type",care_type)
        intent.putExtra("address",address)
        intent.putExtra("rewards",rewards)
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

    private fun getNotification(title: String?, body: String?){
        val intent = Intent(this, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "Default"
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification_clear_all)
            .setContentTitle(title)
            .setContentText(body)
            .setCategory(Notification.CATEGORY_CALL)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), AudioManager.STREAM_ALARM)
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