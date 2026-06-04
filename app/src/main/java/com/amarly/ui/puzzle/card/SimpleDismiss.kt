package com.amarly.ui.puzzle.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amarly.ui.theme.Typography

@Composable
    fun SimpleDismiss(modifier: Modifier = Modifier, onClickDismiss: ()->Unit , onClickSnooze: ()->Unit){
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
                    onClick = onClickSnooze
                ) {
                    Text(
                        "Snooze",
                        style = Typography.displayMedium
                    )
                }
            }
        }
    }
