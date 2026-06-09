package com.amarly.ui.puzzle

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.amarly.ui.theme.Typography

@SuppressLint("DefaultLocale")
@Composable
fun SnoozeDissmiss(
    onSnooze: (Int) -> Boolean,
    onDissmiss: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    var snoozeCount by remember {
        mutableStateOf(0)
    }
    val startTime = System.currentTimeMillis()
    var snoozeEnabled by remember {
        mutableStateOf(true)
    }
    var dismissEnabled by remember {
        mutableStateOf(true)
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(7 / 9f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                enabled = dismissEnabled,
                onClick = {
                    dismissEnabled = onDissmiss(System.currentTimeMillis() - startTime)
                    snoozeEnabled = dismissEnabled
                }
            ) {
                Text(
                    "Dismiss",
                    style = Typography.displayMedium
                )
            }
            Button(
                enabled = snoozeEnabled,
                onClick = {
                    snoozeEnabled = onSnooze(++snoozeCount)
                }
            ) {
                Text(
                    if (snoozeCount > 0) String.format("Snoozed \n%d", snoozeCount)
                    else "Snooze",
                    style = Typography.displayMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}