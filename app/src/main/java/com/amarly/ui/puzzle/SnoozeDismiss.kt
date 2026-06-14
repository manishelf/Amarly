package com.amarly.ui.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AssistChip
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

class SnoozeDismiss : PuzzleComp {
    @Composable
    override fun Comp(
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier,
        questionNumber: Int,
    ) {
        var snoozeCount by remember {
            mutableStateOf(0)
        }
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
                        dismissEnabled = onDismiss()
                        snoozeEnabled = dismissEnabled
                    }
                ) {
                    Text(
                        "Dismiss",
                        style = Typography.displayMedium
                    )
                }
                if (snoozeCount > 0) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                text = "Snoozed $snoozeCount times already",
                                style = Typography.titleLarge
                            )
                        }
                    )
                }
                Button(
                    enabled = snoozeEnabled,
                    onClick = {
                        snoozeEnabled = false
                        onSnooze(++snoozeCount, {
                            snoozeEnabled = true
                        })
                    }
                ) {
                    Text(
                        text = "Snooze",
                        style = Typography.displayMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}