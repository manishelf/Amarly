package com.amarly

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.amarly.data.TimerD
import com.amarly.data.TimerD.TimerData
import com.amarly.service.AlarmReceiver
import com.amarly.ui.theme.AmarlyTheme
import com.amarly.ui.timer.Timer
import java.util.Calendar


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val timers = remember() {
                mutableStateListOf<TimerData>(
                    TimerData(
                        triggerTime = Calendar.getInstance(),
                        activeDays = TimerD.SUNDAY or TimerD.THURSDAY,
                        message = "This is some random message I made",
                    )
                )
            }

            var displayTimePicker by remember() {
                mutableStateOf(false)
            }

            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Timer.TopBar(timers, Modifier)
                    },
                    floatingActionButton = {
                        Timer.AddButton(
                            modifier = Modifier,
                            onClick = {
                                displayTimePicker = true
                            }
                        )
                    }
                ) { innerPadding ->
                    Timer.TimerList(
                        Modifier.padding(
                            innerPadding
                        ),
                        timers,
                        deleteHandler = { timers.remove(it) }
                    )

                    if (displayTimePicker) {
                        Timer.TimePicker(
                            Modifier,
                            onConfirm = { timerData ->
                                timers.add(timerData)
                                displayTimePicker = false
                                val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
                                    putExtra("timer_id", timerData.id)
                                }

                                val triggerMillis = timerData.triggerMillis()
                                val alarmManager =
                                    getSystemService(Context.ALARM_SERVICE) as AlarmManager

                                val pendingIntent = PendingIntent.getBroadcast(
                                    this,
                                    timerData.id,
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

                                        val intent =
                                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                        startActivity(intent)

                                    }

                                } else {

                                    alarmManager.setExactAndAllowWhileIdle(
                                        AlarmManager.RTC_WAKEUP,
                                        triggerMillis,
                                        pendingIntent
                                    )
                                }
                            },
                            onDismiss = {
                                displayTimePicker = false
                            }
                        )
                    }
                }
            }
        }
    }
}

