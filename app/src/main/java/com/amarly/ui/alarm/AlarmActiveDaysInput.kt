package com.amarly.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.amarly.data.AlarmData
import com.amarly.ui.theme.FULL_BLACK
import com.amarly.ui.theme.TRANSPARENT_WHITE
import com.amarly.ui.theme.WHITE

@Composable
fun AlarmActiveDaysInput(
    modifier: Modifier = Modifier,
    currActiveDays: Int = 0,
    onChange: (Int) -> Unit
) {
    var activeDays by remember {
        mutableIntStateOf(currActiveDays)
    }
    var rowWidth by remember {
        mutableFloatStateOf(0f)
    }

    val selectedColor = WHITE
    val unselectedColor = TRANSPARENT_WHITE

    fun toggleDay(index: Int) {
        activeDays = activeDays xor (1 shl index)
        onChange(activeDays)
    }

    fun setDay(index: Int, enabled: Boolean) {
        activeDays =
            if (enabled) {
                activeDays or (1 shl index)
            } else {
                activeDays and (1 shl index).inv()
            }

        onChange(activeDays)
    }

    Box( // to allow drag and select
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .onSizeChanged {
                rowWidth = it.width.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->

                        val itemWidth = rowWidth / AlarmData.DAYS.size
                        val index = (offset.x / itemWidth)
                            .toInt()
                            .coerceIn(0, AlarmData.DAYS.lastIndex)

                        toggleDay(index)
                    },
                    onDrag = { change, _ ->

                        val itemWidth = rowWidth / AlarmData.DAYS.size
                        val index = (change.position.x / itemWidth)
                            .toInt()
                            .coerceIn(0, AlarmData.DAYS.lastIndex)

                        setDay(index, true)
                    }
                )
            }
    ) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AlarmData.DAYS.forEachIndexed { index, day ->
                val selected = (activeDays and (1 shl index)) != 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected)
                                selectedColor
                            else
                                unselectedColor
                        )
                        .border(
                            1.dp,
                            WHITE,
                            androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                        .clickable(true, onClick = {
                            toggleDay(index)
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color =
                            if (selected)
                                FULL_BLACK
                            else
                                WHITE
                    )
                }
            }
        }
    }
}