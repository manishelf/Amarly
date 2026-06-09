package com.amarly.ui.alarm

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlarmMessage(message: String = "", modifier: Modifier = Modifier) {
    // TODO:
    Text(
        text = message,
        modifier = modifier.padding(10.dp, 2.dp)
    )
}