package com.amarly

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amarly.ui.receiver.AlarmReceiverUi

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        val NOTIFICATION_CHANNEL_ID = "Amarly_alarm"
        val NOTIFICATION_CHANNEL_NAME = "Amarly Alarm Notifications"
    }


    @SuppressLint("FullScreenIntentPolicy")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val alarmId = intent.getStringExtra("alarm_id") ?: return
        val alarmMessage = intent.getStringExtra("alarm_message") ?: ""

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