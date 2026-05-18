package com.amarly.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.concurrent.timer

@Composable
fun TimerFragment(timerData: TimerData, modifier: Modifier = Modifier){
    Card(modifier){
        TimerDaysFragment(
            modifierGrp = modifier,
            activeDays = timerData.activeDays
        )
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerTimeFragment(
                modifierGrp = modifier,
                dateTime = timerData.dateTime
            )
            Switch(
                checked = timerData.enabled,
                onCheckedChange = { newState ->
                    timerData.enabled = newState
                },
                modifier
            )
        }
        TimerMessageFragment(
            text = timerData.message,
            modifier
        )
    }
}