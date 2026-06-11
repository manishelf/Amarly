package com.amarly.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amarly.R
import com.amarly.ui.theme.Typography

@Composable
fun ActionButton(
    onAddOnceAlarm: () -> Unit,
    onAddRegularAlarm: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val radius by animateDpAsState(
        if (expanded) 100.dp else 20.dp
    )
    Column(
        horizontalAlignment = Alignment.End
    ) {

        AnimatedVisibility(visible = expanded) {
            Column(
                horizontalAlignment = Alignment.End
            ) {

                FabOption(
                    text = "Quick alarm",
                    icon = R.drawable.charger_24px
                ) {
                    expanded = false
                    onAddOnceAlarm()
                }

                Spacer(Modifier.height(8.dp))

                FabOption(
                    text = "Alarm",
                    icon = R.drawable.alarm_24px
                ) {
                    expanded = false
                    onAddRegularAlarm()
                }

                // TODO: add a timer / stopwatch
            }
        }
        Spacer(Modifier.height(8.dp))
        FloatingActionButton(
            onClick = {
                expanded = !expanded
            },
        ) {
            Text(
                text = if (expanded) "×" else "+",
                style = Typography.headlineSmall
            )
        }
    }
}

@Composable
private fun FabOption(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text
            )
        }
    }
}