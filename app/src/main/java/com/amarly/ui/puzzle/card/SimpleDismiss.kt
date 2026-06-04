package com.amarly.ui.puzzle.card

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
import java.util.Locale

@Composable
    fun SimpleDismiss(modifier: Modifier = Modifier, onClickDismiss: ()->Unit , onClickSnooze: ()->Unit){
        var maxSnoozeCount = 5
        var snoozeCount by remember {
            mutableStateOf(0)
        }
        var snoozeCountDown by remember {
            mutableStateOf(0)
        }
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(
                Modifier.fillMaxSize(7/9f),
                Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(
                    onClick = onClickDismiss
                ){
                    Text(
                        "Dismiss",
                        style = Typography.displayMedium
                    )
                }
                Button(
                    onClick = {
                        snoozeCount += 1
                        onClickSnooze()
                    }
                ) {
                    Text(
                        if(snoozeCount > 0)
                            String.format(Locale.getDefault(), "Snooze\n%d/%d", snoozeCount, maxSnoozeCount)
                        else "Snooze",
                        style = Typography.displayMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
