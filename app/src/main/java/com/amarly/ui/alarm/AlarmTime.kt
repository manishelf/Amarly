package com.amarly.ui.alarm

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.ui.theme.Typography
import java.time.ZonedDateTime

@Composable
@SuppressLint("DefaultLocale")
fun AlarmTime(triggerTime: ZonedDateTime, modifier: Modifier = Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hour = String.format("%02d", triggerTime.hour % 12)
        val minute = String.format("%02d", triggerTime.minute)
        Text(
            text = hour,
            Modifier,
            style = Typography.displayLarge
        )
        Text(
            text = ":",
            modifier = Modifier.padding(
                5.dp, 0.dp
            ),
            style = Typography.displaySmall
        )
        Text(
            text = minute,
            modifier = Modifier,
            style = Typography.displayLarge
        )
        Text(
            text = if (triggerTime.hour <= 12) "AM" else "PM",
            modifier = Modifier.padding(5.dp, 0.dp),
            style = Typography.headlineLarge
        )
    }
}