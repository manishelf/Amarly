package com.amarly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.amarly.ui.theme.Black
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.amarly.ui.theme.Transparent
import com.amarly.ui.theme.White

class TimerActivity {

    @Composable
    fun Alarm(
        name: String,
        modifier: Modifier = Modifier
    ) {

        val layoutDirection = LocalLayoutDirection.current
        var enabled by remember {
            mutableStateOf(true)
        }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor = Black
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "S M T W T F S",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "5:32",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )

                        Text(
                            text = "AM",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.LightGray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.DarkGray
                            )
                        ) {

                            Text(
                                text = "*",
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "You need to get to the train",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        uncheckedThumbColor = White,

                        checkedTrackColor = Transparent,
                        uncheckedTrackColor = Transparent,

                        checkedBorderColor = White,
                        uncheckedBorderColor = White
                    )

                )
            }
        }
    }
}