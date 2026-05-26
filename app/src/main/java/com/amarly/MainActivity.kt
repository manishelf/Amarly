package com.amarly

import android.os.Build
import android.os.Bundle
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
import com.amarly.data.TimerData.TimerData
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
                        0,
                        Calendar.getInstance(),
                        0,
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

