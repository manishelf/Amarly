package com.amarly.ui.receiver

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
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
import kotlin.math.pow

class RecieverViewModel(app: Application) : AndroidViewModel(app) {

    val SNOOZE_TIME_IN_MIN = 5
    val INTERACTION_TIME_IN_MIN = 1

    val maxSnoozeCount: Int
        get() {
            return alarm.maxSnooze
        }

    val autoDismissEnabled: Boolean = true
    val autoDismissInMin: Int = 20

    var interactionCountStart by mutableStateOf(System.currentTimeMillis())
    var interactionCountdownProgress by mutableStateOf(1f)
        private set

    var currPuzzle by mutableStateOf(PuzzleType.SIMPLE_DISMISS)

    var snoozeJob by mutableStateOf<Job?>(null)

    private val repo = FileRepo(app)
    private val scheduler = AlarmScheduler(app)

    val puzzleQuestionCount: Int
        get() {
            return alarm.puzzleQuestionCount
        }

    var mediaPlayer: MediaPlayer? = null
    var vibrator: Vibrator? = null
    var playingSound: Boolean = false
    var playingVibration: Boolean = false

    var isDismissed: Boolean = false
        private set

    private lateinit var alarm: AlarmData;

    fun init(activity: Activity, alarmId: String) {

        alarm = repo.getById(alarmId) ?: alarm
        if (alarm.activeDays == AlarmData.DAY_NONE) {
            alarm.running = false
            viewModelScope.launch {
                repo.saveOne(alarm)
                scheduler.clear(alarm)
                //repo.deleteOneAlarm(alarm)
            }
        }

        val alarmSoundUri: Uri = if (alarm.soundUri.isNotEmpty()) {
            alarm.soundUri.toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val devices = audioManager.getAvailableCommunicationDevices()

            val speaker = devices.firstOrNull {
                it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
            }

            speaker?.let {
                audioManager.setCommunicationDevice(it)
            }
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(activity, alarmSoundUri)
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
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

        startAutoDismissTimer(activity)
    }

    val alarmMessage: String
        get() {
            return alarm.message
        }

    fun dismiss(activity: Activity): Boolean {
        if (alarm.puzzleType == PuzzleType.SIMPLE_DISMISS || currPuzzle == alarm.puzzleType) {
            stopPlayback()
            NotificationManagerCompat.from(getApplication())
                .cancel(alarm.id().hashCode())
            activity.finish()
            isDismissed = true
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
        mediaPlayer?.apply {
            setVolume(0f, 0f) // Start silent
            start()
        }
        viewModelScope.launch {
            val durationMs = 10_000L // Fade in over 10 seconds
            val steps = 50
            val delayPerStep = durationMs / steps

            // log10 increase of volume
            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                val volume = (10.0.pow(progress.toDouble()) - 1) / 9
                mediaPlayer?.setVolume(volume.toFloat(), volume.toFloat())
                delay(delayPerStep)
            }
        }

        vibrator?.vibrate(
            VibrationEffect.createWaveform(alarm.vibration, 0)
        )

        playingSound = true
        playingVibration = true
    }

    fun snooze(
        snoozeCount: Int,
        onComplete: () -> Unit = {},
        snoozeTimeInMin: Int = SNOOZE_TIME_IN_MIN,
    ) {
        if (snoozeCount > (alarm.maxSnooze)) {
            onComplete()
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
                onComplete()
            }
        }
    }

    fun startAutoDismissTimer(activity: Activity) {
        if (autoDismissEnabled && autoDismissInMin > 0) {
            val startTime = System.currentTimeMillis()
            var triggerTime = autoDismissInMin * 60 * 1000 + startTime
            val oneMin = 1 * 60 * 1000
            viewModelScope.launch {
                while (triggerTime > 0) {
                    triggerTime -= oneMin
                    delay(oneMin.toLong())
                }
                alarm.puzzleType = PuzzleType.SIMPLE_DISMISS
                dismiss(activity)
            }
        }
    }

}

