package com.amarly.ui.alarm

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amarly.data.AlarmData

@Composable
fun AlarmList(
    alarms: List<AlarmData>,
    onToggle: (AlarmData) -> Unit = {},
    onDelete: (AlarmData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    // TODO: This is messed up, deleting actually one alarm deletes different
    LazyColumn(modifier = modifier) {
        items(
            alarms.sortedBy {
                val nextTrigger = it.triggerInstant().toEpochMilli()
                if (nextTrigger >= now) {
                    nextTrigger - now
                } else {
                    Long.MAX_VALUE
                }
            },
            key = { it.id() }
        ) { alarm ->
            if (onDelete != {})
                AlarmItemWithDelete(
                    alarm,
                    onToggle = onToggle,
                    onDelete = onDelete,
                )
            else
                AlarmItem(
                    alarm = alarm,
                    onToggle = onToggle
                )
        }
    }
}