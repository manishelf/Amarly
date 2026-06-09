package com.amarly.ui.main

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import kotlinx.coroutines.delay

fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60
    val days = totalHours / 24

    val hours = totalHours % 24
    val minutes = totalMinutes % 60
    val seconds = totalSeconds % 60

    return buildList {
        if (days > 0) add("${days}d")
        if (hours > 0) add("${hours}h")
        if (minutes > 0) add("${minutes}m")
        if (seconds > 0) add("${seconds}s")
    }.joinToString(" ")
}

@Composable
fun TopBar(
    triggerTimeMillis: Long = -1,
    modifier: Modifier = Modifier
) {
    var currentTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            currentTime = System.currentTimeMillis()
        }
    }
    var message = ""
    if (triggerTimeMillis > -1)
        message = "Will be ringing in\n" + formatMillis(triggerTimeMillis - currentTime)
    else
        message = "No alarms scheduled"

    Text(
        message,
        Modifier
            .absolutePadding(10.dp, 30.dp, 10.dp, 10.dp)
            .fillMaxWidth(),
        style = Typography.headlineLarge,
        color = GRAYISH_WHITE,
        textAlign = TextAlign.Center
    )
}