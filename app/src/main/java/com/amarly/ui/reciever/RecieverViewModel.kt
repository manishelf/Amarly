package com.amarly.ui.reciever

import android.app.Activity
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarly.AlarmReceiver
import com.amarly.data.AlarmData
import com.amarly.repo.AlarmScheduler
import com.amarly.repo.FileRepo
import com.amarly.ui.puzzle.PuzzleType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecieverViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        val SNOOZE_TIME_MIN = 5
    }

    val maxSnoozeCount: Int
        get() {
            return alarm?.maxSnooze ?: 5
        }
    var currPuzzle by mutableStateOf(PuzzleType.SNOOZE_DISMISS)

    var snoozeJob by mutableStateOf<Job?>(null)

    private val repo = FileRepo(app)
    private val scheduler = AlarmScheduler(app)
    private var alarm: AlarmData? = null
    fun getAlarm(alarmId: String): AlarmData {
        if (alarm == null) {
            alarm = repo.getById(alarmId)
        }
        return alarm!! // assertion
    }

    fun getAlarmMessage(): String? {
        return alarm?.message
    }

    fun dissmiss(activity: Activity, dismissTime: Long): Boolean {
        if (alarm?.puzzleType == PuzzleType.SNOOZE_DISMISS) {
            AlarmReceiver.mediaPlayer?.stop()
            NotificationManagerCompat.from(getApplication())
                .cancel(AlarmReceiver.alarmId.hashCode())
            activity.finish()

        } else {
            currPuzzle = alarm!!.puzzleType
        }
        snoozeJob?.cancel()
        return false
    }

    fun snooze(snoozeCount: Int): Boolean {
        // TODO: the snooze button should be dismissed during a snooze
        if (snoozeCount > alarm?.maxSnooze ?: 5) {
            return false
        } else {
            AlarmReceiver.mediaPlayer?.pause()
            snoozeJob?.cancel()
            snoozeJob = viewModelScope.launch {
                delay(SNOOZE_TIME_MIN * 60 * 1000L)
                AlarmReceiver.mediaPlayer?.start()
            }
        }
        return true
    }

}

