package com.amarly.ui.reciever

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarly.AlarmReceiver
import com.amarly.AlarmReceiver.Companion.alarmSound
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
    var mediaPlayer: MediaPlayer? = null
    var vibrator: Vibrator? = null

    private var alarm: AlarmData? = null

    fun init(activity: Activity, alarmId: String) {
        alarm = repo.getById(alarmId)

        val alarmSoundUri: Uri = if (alarmSound.isNotEmpty()) {
            alarmSound.toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(activity, alarmSoundUri)
            isLooping = true
            prepare()
            start()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(alarm?.vibration, 0)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(alarm?.vibration, 0)
        }
    }

    val alarmMessage: String?
        get() {
            return alarm?.message
        }

    fun dissmiss(activity: Activity, dismissTime: Long): Boolean {
        if (alarm?.puzzleType == PuzzleType.SNOOZE_DISMISS) {
            mediaPlayer?.stop()
            vibrator?.cancel()
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
            mediaPlayer?.pause()
            vibrator?.cancel()

            snoozeJob?.cancel()
            snoozeJob = viewModelScope.launch {
                delay(SNOOZE_TIME_MIN * 60 * 1000L)
                mediaPlayer?.start()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(alarm?.vibration, 0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(alarm?.vibration, 0)
                }
            }
        }
        return true
    }

}

