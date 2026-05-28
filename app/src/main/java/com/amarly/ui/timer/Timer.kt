package com.amarly.ui.timer

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.amarly.data.TimerD.TimerData
import com.amarly.data.TimerD.days
import com.amarly.data.TimerD.defaultVibePattern
import com.amarly.service.TimerService
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import com.amarly.ui.theme.WHITE
import com.example.amarly.R
import java.util.Calendar
import kotlin.math.roundToInt

object Timer {

    // TODO: Temp
    var i = 0;
    fun formatAsTime(millis: Long): String {

        val totalSeconds = millis / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val days = totalHours / 24

        val hours = totalHours % 24
        val minutes = totalMinutes % 60
        val seconds = totalSeconds % 60

        return buildList {
            if (days > 0) add("${days}d")
            if (hours > 0) add("${hours}h")
            if (minutes > 0) add("${minutes}m")
            if (seconds > 0) add("${seconds}s")
        }.joinToString(" ")
    }

    @Composable
    fun TopBar(timers: List<TimerData>, modifier: Modifier = Modifier) {
        val now = Calendar.getInstance()
        val firstTriggeringTimer = timers
            .filter { it.triggerMillis() - now.timeInMillis > 0 }
            .map { it.triggerMillis() }
            .minByOrNull { it }
        Text(
            text = if (firstTriggeringTimer != null) {
                "Will be ringing in \n ${
                    formatAsTime(firstTriggeringTimer - now.timeInMillis)
                }"
            } else {
                "No upcoming alarms"
            },
            Modifier
                .absolutePadding(10.dp, 30.dp, 10.dp, 10.dp),
            style = Typography.headlineLarge,
            color = GRAYISH_WHITE
        )
    }

    @Composable
    fun TimerActiveDays(modifier: Modifier = Modifier, activeDays: Int) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEachIndexed { index, day ->
                val enabled = (activeDays and (1 shl (index + 1))) != 0
                Text(
                    text = day,
                    modifier = Modifier,
                    color =
                        if (enabled)
                            WHITE
                        else
                            GRAYISH_WHITE,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    @Composable
    fun TimerActiveDaysInput(
        modifier: Modifier = Modifier,
        currActiveDays: Int = 0,
        onChange: (Int) -> Unit
    ) {
        var activeDays by remember {
            mutableStateOf(currActiveDays)
        }
        var rowWidth by remember {
            mutableFloatStateOf(0f)
        }

        val selectedColor = Color.White
        val unselectedColor = Color.Transparent

        fun toggleDay(index: Int) {
            activeDays = activeDays xor (1 shl (index + 1))
            onChange(activeDays)
        }

        fun setDay(index: Int, enabled: Boolean) {
            activeDays =
                if (enabled) {
                    activeDays or (1 shl (index + 1))
                } else {
                    activeDays and (1 shl (index + 1)).inv()
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

                            val itemWidth = rowWidth / days.size
                            val index = (offset.x / itemWidth)
                                .toInt()
                                .coerceIn(0, days.lastIndex)

                            toggleDay(index)
                        },
                        onDrag = { change, _ ->

                            val itemWidth = rowWidth / days.size
                            val index = (change.position.x / itemWidth)
                                .toInt()
                                .coerceIn(0, days.lastIndex)

                            setDay(index, true)
                        }
                    )
                }
        ) {
            Row(
                modifier,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEachIndexed { index, day ->
                    val selected = (activeDays and (1 shl (index + 1))) != 0
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
                                Color.White,
                                RoundedCornerShape(12.dp)
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
                                    Color.Black
                                else
                                    Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TimerTime(modifier: Modifier = Modifier, triggerTime: Calendar) {
        Row(
            modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hour = String.format("%02d", triggerTime.get(Calendar.HOUR))
            val minute = String.format("%02d", triggerTime.get(Calendar.MINUTE))
            Text(
                text = hour,
                Modifier,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = ":",
                modifier = Modifier.padding(
                    5.dp, 0.dp
                ),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = minute,
                Modifier,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = when (triggerTime.get(Calendar.AM_PM)) {
                    Calendar.AM -> "am"
                    Calendar.PM -> "pm"
                    else -> ""
                },
                modifier = Modifier.padding(5.dp, 0.dp),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }

    @Composable
    fun TimerMessage(modifier: Modifier = Modifier, text: String) {
        Text(
            text = text,
            modifier = modifier.padding(10.dp, 2.dp)
        )
    }

    @Composable
    fun Timer(modifier: Modifier = Modifier, state: TimerData) {
        Card(modifier) {
            if (state.activeDays > 0)
                TimerActiveDays(
                    Modifier.padding(10.dp, 5.dp),
                    state.activeDays,
                )
            else
                Row(Modifier.padding(5.dp)) {}

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerTime(triggerTime = state.triggerTime)
                Switch(
                    checked = state.enabled,
                    onCheckedChange = { newState ->
                        state.enabled = newState
                    },
                )
            }
            if (!state.message.isEmpty())
                TimerMessage(Modifier, state.message)
            else
                Row(Modifier) {}
        }
    }

    @Composable
    fun TimerWithDelete(
        timer: TimerData,
        onDelete: (TimerData) -> Unit
    ) {
        val shape = RoundedCornerShape(20.dp)

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
                        onDelete(timer)
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
                Timer(
                    modifier = Modifier,
                    state = timer
                )
            }
        }
    }

    @Composable
    fun TimerList(
        modifier: Modifier = Modifier,
        timers: List<TimerData>,
        deleteHandler: (item: TimerData) -> Unit
    ) {
        LazyColumn(modifier = modifier) {
            items(timers) { timer ->
                TimerWithDelete(
                    timer = timer,
                    onDelete = deleteHandler
                )
            }
        }
    }

    @Composable
    fun AddButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
        FloatingActionButton(
            modifier = Modifier.border(
                width = 2.dp,
                color = GRAYISH_WHITE,
                shape = RoundedCornerShape(20.dp)
            ),
            onClick = onClick
        ) {
            Text(
                "+"
            )
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun TimePicker(
        modifier: Modifier = Modifier,
        onConfirm: (TimerData) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        val currTime = Calendar.getInstance()
        val timePickerState = rememberTimePickerState(
            initialHour = currTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currTime.get(Calendar.MINUTE),
            is24Hour = false
        )
        var timePickerMode by remember { mutableStateOf(true) }

        var message = rememberTextFieldState("");
        var activeDays by remember {
            mutableIntStateOf(1 shl currTime.get(Calendar.DAY_OF_WEEK))
        }
        var ringtoneUri by remember {
            mutableStateOf(Uri.EMPTY)
        }
        var vibrate = mutableListOf(0)
        vibrate.addAll(defaultVibePattern)
        val ringtonePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ringtoneUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        result.data?.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                            Uri::class.java
                        )
                    } else {
                        result.data?.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                        )
                    }
            } else {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
        }
        AlertDialog(
            modifier = Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, _ ->
                    },
                    onDragEnd = {
                        timePickerMode = !timePickerMode
                    }
                )
            },
            onDismissRequest = onDismiss,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (timePickerMode) {
                        androidx.compose.material3.TimePicker(state = timePickerState)
                    } else {
                        androidx.compose.material3.TimeInput(state = timePickerState)
                    }

                    TextField(
                        message,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = InputTransformation.maxLength(30),
                        placeholder = { Text("Title") },
                        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 10.dp)
                    )
                    TimerActiveDaysInput(
                        Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 5.dp),
                        currActiveDays = activeDays,
                        onChange = { activeDaysIn ->
                            activeDays = activeDaysIn
                        }
                    )
                    Row(
                        modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                    putExtra(
                                        RingtoneManager.EXTRA_RINGTONE_TYPE,
                                        RingtoneManager.TYPE_ALL //TODO: causes duplicates in list
                                    )
                                    putExtra(
                                        RingtoneManager.EXTRA_RINGTONE_TITLE,
                                        "Select Alarm Tone"
                                    )
                                    putExtra(
                                        RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                        ringtoneUri
                                    )
                                }

                                ringtonePickerLauncher.launch(intent)
                            }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.notification_sound_24px),
                                modifier = Modifier.width(20.dp),
                                contentDescription = "Ring"
                            )
                        }
                        Button(
                            onClick = {},
                            modifier = modifier
                        ) {
                            Image(
                                painter = painterResource(R.drawable.mobile_sensor_hi_24px),
                                modifier = Modifier.width(20.dp),
                                contentDescription = "Buzz"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {

                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onConfirm(
                            TimerService.Register(
                                TimerData(
                                    // TODO: id
                                    ++i,
                                    message = message.text.toString(),
                                    activeDays = activeDays,
                                    triggerTime = calendar,
                                    running = true,
                                    soundUri = ringtoneUri.toString(),
                                    vibration = vibrate.toTypedArray()
                                )
                            )
                        )

                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
