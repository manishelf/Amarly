package com.amarly.ui.alarm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.amarly.R
import com.amarly.data.AlarmData
import kotlin.math.roundToInt

@Composable
fun AlarmItemWithDelete(
    alarm: AlarmData,
    onToggle: (AlarmData) -> Unit,
    onDelete: (AlarmData) -> Unit
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)

    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    val maxRevealPx = with(LocalDensity.current) {
        80.dp.toPx()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {

        // BACKGROUND DELETE BUTTON
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.secondaryContainer),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = {
                    onDelete(alarm)
                    offsetX = 0f
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.delete_24px),
                    contentDescription = "Delete",
                )
            }
        }

        // FOREGROUND CARD
        Card(
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(offsetX.roundToInt(), 0)
                }
                .pointerInput(Unit) {

                    detectHorizontalDragGestures(

                        onHorizontalDrag = { _, dragAmount ->

                            offsetX =
                                (offsetX + dragAmount)
                                    .coerceIn(-maxRevealPx, 0f)
                        },

                        onDragEnd = {

                            offsetX =
                                if (offsetX < -maxRevealPx / 2)
                                    -maxRevealPx
                                else
                                    0f
                        }
                    )
                }
        ) {
            AlarmItem(
                alarm = alarm,
                onToggle = onToggle,
                Modifier
            )
        }
    }
}