package com.amarly.service

import android.Manifest
import android.annotation.SuppressLint
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
import com.amarly.data.AlarmRepository
import com.amarly.ui.AlarmReceiverUi

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var mediaPlayer: MediaPlayer? = null

        const val CHANNEL_ID = "alarm_channel"
        const val CHANNEL_NAME = "Alarm Notifications"
    }

    @SuppressLint("FullScreenIntentPolicy")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alarmRepo = AlarmRepository(context)
        val alarmId = intent.getStringExtra("alarm_id") ?: return
        val alarm = alarmRepo.getById(alarmId) ?: return
        createNotificationChannel(context)


        val fullScreenIntent = Intent(context, AlarmReceiverUi::class.java).apply{
            putExtra("timer_id", alarmId)
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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.amarly.R.drawable.notification_sound_24px)
            .setContentTitle("Alarm")
            .setContentText("Amarly: " + alarm.message.ifEmpty { "Your timer is complete!" })
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(alarmId.hashCode(), notification)

        val alarmSoundUri: Uri = if (alarm.soundUri.isNotEmpty()) {
            alarm.soundUri.toUri()
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
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for alarms"
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = context.getSystemService(
            NotificationManager::class.java
        )

        manager.createNotificationChannel(channel)
    }
}