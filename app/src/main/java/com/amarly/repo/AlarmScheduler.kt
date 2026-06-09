package com.amarly.repo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.amarly.AlarmReceiver
import com.amarly.data.AlarmData
import java.time.Instant


class AlarmScheduler(val context: Context) {
    fun register(timer: AlarmData): Boolean {

        if (timer.triggerInstant().isBefore(Instant.now())) return false

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", timer.id())
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val triggerMillis = timer.triggerInstant().toEpochMilli()
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timer.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )

            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }

        return true
    }

    fun clear(alarm: AlarmData) {
        val alarmManager =
            context.getSystemService(AlarmManager::class.java)
                ?: return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun registerAll(timers: List<AlarmData>): List<AlarmData> {
        timers.forEach { it ->
            if (it.running && it.triggerInstant().isAfter(Instant.now())) {
                register(it)
            } else {
                clear(it)
            }
        }
        return timers
    }
}