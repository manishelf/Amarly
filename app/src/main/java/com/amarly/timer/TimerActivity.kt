package com.amarly.timer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Calendar

object TimerActivity {

    @Composable
    fun TimerList(timers: List<TimerData>, modifier: Modifier = Modifier){
        LazyColumn(
            modifier = modifier
        ) {
                items(timers) { timer ->
                    TimerFragment(
                        timer,
                        modifier = Modifier
                            .padding(
                                10.dp, 2.dp
                            )
                    )
                }
            }
        }
    }