package com.amarly.ui.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.data.AlarmData
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.WHITE

@Composable
fun AlarmActiveDays(activeDays: Int = 0, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlarmData.DAYS.forEachIndexed { index, day ->
            val enabled = (activeDays and (1 shl index)) != 0
            Text(
                text = day,
                modifier = Modifier,
                color =
                    if (enabled)
                        WHITE
                    else
                        GRAYISH_WHITE,
            )
        }
    }
}