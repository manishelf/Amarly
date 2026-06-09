package com.amarly.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import com.amarly.ui.theme.GRAYISH_WHITE

@Composable
fun ActionButton(
    onAddOnceAlarm: () -> Unit,
    onAddRegularAlarm: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = painterResource(R.drawable.charger_24px),
                            contentDescription = "Quick alarm",
                            modifier = Modifier.padding(5.dp)
                        )
                        Text("Quick alarm")
                    }
                },
                onClick = {
                    menuExpanded = false
                    onAddOnceAlarm()
                }
            )

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = painterResource(R.drawable.alarm_24px),
                            contentDescription = "Alarm",
                            modifier = Modifier.padding(5.dp)
                        )
                        Text("Alarm")
                    }
                },
                onClick = {
                    menuExpanded = false
                    onAddRegularAlarm()
                }
            )
        }
        FloatingActionButton(
            modifier = Modifier.border(
                width = 2.dp, color = GRAYISH_WHITE,
                shape = RoundedCornerShape(if (menuExpanded) 100.dp else 20.dp)
            ),
            onClick = {
                menuExpanded = !menuExpanded
            }
        ) {
            Text("+")
        }
    }
}