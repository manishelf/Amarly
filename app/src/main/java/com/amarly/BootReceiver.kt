package com.amarly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amarly.repo.AlarmScheduler
import com.amarly.repo.FileRepo

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        FLog.i(context, "Received: ${intent.action}")

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            FLog.i(context, "BOOT_COMPLETED triggered")

            val repo = FileRepo(context)
            val scheduler = AlarmScheduler(context)

            val size = repo.getAllAlarms().size
            FLog.i(context, "Alarms found: $size")

            scheduler.registerAll(repo.getAllAlarms())
        }
    }
}