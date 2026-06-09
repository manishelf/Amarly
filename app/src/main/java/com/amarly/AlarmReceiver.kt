package com.amarly

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.amarly.ui.reciever.AlarmReceiverUi

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        var alarmId: String? = null
        var alarmMessage: String = ""
        var alarmSound: String = ""
        val NOTIFICATION_CHANNEL_ID = "Amarly_alarm"
        val NOTIFICATION_CHANNEL_NAME = "Amarly Alarm Notifications"
        var mediaPlayer: MediaPlayer? = null
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        alarmId = intent.getStringExtra("alarm_id") ?: return
        alarmMessage = intent.getStringExtra("alarm_message") ?: ""
        alarmSound = intent.getStringExtra("alarm_sound") ?: ""

        createNotificationChannel(context)
        val fullScreenIntent = Intent(context, AlarmReceiverUi::class.java).apply {
            putExtra("alarm_id", alarmId)
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // technically not recommended but required to get the full screen intent launched
        context.startActivity(fullScreenIntent)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_sound_24px)
            .setContentTitle("Alarm")
            .setContentText("Amarly: " + alarmMessage.ifEmpty { "Your timer is complete!" })
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(alarmId.hashCode(), notification)

        val alarmSoundUri: Uri = if (alarmSound.isNotEmpty()) {
            alarmSound.toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, alarmSoundUri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = NOTIFICATION_CHANNEL_NAME
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = context.getSystemService(
            NotificationManager::class.java
        )

        manager.createNotificationChannel(channel)
    }
}