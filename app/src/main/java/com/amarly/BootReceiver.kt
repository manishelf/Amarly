package com.amarly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amarly.repo.AlarmScheduler
import com.amarly.repo.FileRepo

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repo = FileRepo(context)
            val scheduler = AlarmScheduler(context)
            scheduler.registerAll(repo.getAllAlarms())
        }
    }
}