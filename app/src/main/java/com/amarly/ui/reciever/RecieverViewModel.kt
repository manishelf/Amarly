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
import com.amarly.data.AlarmData
import com.amarly.repo.AlarmScheduler
import com.amarly.repo.FileRepo
import com.amarly.ui.puzzle.PuzzleType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecieverViewModel(app: Application) : AndroidViewModel(app) {

    val SNOOZE_TIME_IN_MIN = 5
    val INTERACTION_TIME_IN_MIN = 1

    val maxSnoozeCount: Int
        get() {
            return alarm.maxSnooze
        }

    var interactionCountStart by mutableStateOf(System.currentTimeMillis())
    var interactionCountdownProgress by mutableStateOf(1f)
        private set

    var currPuzzle by mutableStateOf(PuzzleType.SIMPLE_DISMISS)

    var snoozeJob by mutableStateOf<Job?>(null)

    private val repo = FileRepo(app)
    private val scheduler = AlarmScheduler(app)
    var mediaPlayer: MediaPlayer? = null
    var vibrator: Vibrator? = null
    var playingSound: Boolean = false
    var playingVibration: Boolean = false

    private lateinit var alarm: AlarmData;

    fun init(activity: Activity, alarmId: String) {

        alarm = repo.getById(alarmId) ?: alarm
        if (alarm.activeDays == AlarmData.DAY_NONE) {
            alarm.running = false
            viewModelScope.launch {
                repo.saveOne(alarm)
                scheduler.registerAll(repo.getAllAlarms())
            }
        }

        val alarmSoundUri: Uri = if (alarm.soundUri.isNotEmpty()) {
            alarm.soundUri.toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(activity, alarmSoundUri)
            isLooping = true
            prepare()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        startPlayback()
    }

    val alarmMessage: String
        get() {
            return alarm.message
        }

    fun dissmiss(activity: Activity, dismissTime: Long): Boolean {
        if (alarm.puzzleType == PuzzleType.SIMPLE_DISMISS || currPuzzle == alarm.puzzleType) {
            stopPlayback()
            NotificationManagerCompat.from(getApplication())
                .cancel(alarm.id().hashCode())
            activity.finish()
        } else {
            currPuzzle = alarm.puzzleType
        }
        snoozeJob?.cancel()
        return false
    }

    fun stopPlayback() {
        mediaPlayer?.pause() // TODO: should it be stop() here? is it a leak otherwise?
        vibrator?.cancel()
        playingSound = false
        playingVibration = false
    }

    fun startPlayback() {
        mediaPlayer?.start()
        vibrator?.vibrate(
            VibrationEffect.createWaveform(alarm.vibration, 0)
        )
        playingSound = true
        playingVibration = true
    }

    fun snooze(snoozeCount: Int, snoozeTimeInMin: Int = SNOOZE_TIME_IN_MIN): Boolean {
        if (snoozeCount > (alarm.maxSnooze)) {
            return false
        } else {
            interactionCountdownProgress = 1f
            interactionCountStart = System.currentTimeMillis()
            stopPlayback()

            snoozeJob?.cancel()
            snoozeJob = viewModelScope.launch {
                while (interactionCountdownProgress > 0) {
                    val elapsed = System.currentTimeMillis() - interactionCountStart
                    val p = 1f - (elapsed / (snoozeTimeInMin * 60 * 1000).toFloat())
                    interactionCountdownProgress = p.coerceIn(0f, 1f)
                    delay(16)
                }
                startPlayback()
            }
        }
        return true
    }

}

