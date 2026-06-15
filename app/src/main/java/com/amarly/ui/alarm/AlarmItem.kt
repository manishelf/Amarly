package com.amarly.ui.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.data.AlarmData

@Composable
fun AlarmItem(
    alarm: AlarmData,
    onToggle: (AlarmData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // TODO: bug where once alarms cause others to look disabled in the list
    var running by remember(alarm.id(), alarm.running) {
        mutableStateOf(alarm.running)
    }
    Card(modifier) {
        if (alarm.activeDays > 0)
            AlarmActiveDays(
                alarm.activeDays,
                Modifier.padding(10.dp, 5.dp),
            )
        else
            Row(Modifier.padding(5.dp)) {}

        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlarmTime(triggerTime = alarm.triggerTime)
            Switch(
                checked = running,
                onCheckedChange = { newState ->
                    running = newState
                    onToggle(alarm)
                },
            )
        }
        if (!alarm.message.isEmpty())
            AlarmMessage(alarm.message, Modifier)
        else
            Row(Modifier) {}
    }
}
