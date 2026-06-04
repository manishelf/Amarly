package com.amarly.ui

import android.app.Activity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.amarly.data.Puzzle
import com.amarly.service.AlarmReceiver
import com.amarly.ui.puzzle.card.SimpleDismiss
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmPuzzleUi {

    @Composable
    fun PuzzleFactory(puzzleType: Int, modifier: Modifier = Modifier){
        val scope = rememberCoroutineScope()
        var stopJob by remember { mutableStateOf<Job?>(null) }
        val context = LocalContext.current

        when(puzzleType) {
            Puzzle.TYPE_DISMISS -> {
                SimpleDismiss(Modifier, {
                    AlarmReceiver.mediaPlayer?.stop()
                    NotificationManagerCompat.from(context).cancel(AlarmReceiver.alarmId.hashCode())
                    (context as? Activity)?.finish()
                },
                    {
                        AlarmReceiver.mediaPlayer?.pause()
                        stopJob?.cancel()
                        stopJob = scope.launch {
                                delay(5 * 60 * 1000L)
                                AlarmReceiver.mediaPlayer?.start()
                        }
                },
                    )
            }
            else -> {
                Text(
                    "Unknown puzzle type"
                )
            }
        }
    }
}