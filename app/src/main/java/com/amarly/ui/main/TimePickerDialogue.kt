package com.amarly.ui.main

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarly.R
import com.amarly.data.AlarmData
import com.amarly.ui.alarm.AlarmActiveDaysInput
import com.amarly.ui.puzzle.PuzzleType
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.TRANSPARENT_WHITE
import java.time.ZonedDateTime

enum class TimePickerType {
    REGULAR,
    QUICK
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerDialogue(
    onConfirm: (AlarmData) -> Unit = {},
    onDismiss: () -> Unit = {},
    type: TimePickerType = TimePickerType.REGULAR,
    modifier: Modifier = Modifier
) {
    val currTime = ZonedDateTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = currTime.plusMinutes(1).hour,
        initialMinute = currTime.plusMinutes(1).minute,
        is24Hour = false
    )
    var timePickerMode by remember { mutableStateOf(true) }
    var puzzleMenuVisible by remember { mutableStateOf(false) }

    var message = rememberTextFieldState("");
    var activeDays by remember {
        mutableIntStateOf(1 shl (currTime.dayOfWeek.value - 1))
    }
    var ringtoneUri by remember {
        mutableStateOf(Uri.EMPTY)
    }
    var puzzleType by remember {
        mutableStateOf(PuzzleType.SIMPLE_DISMISS)
    }

    // TODO: custom vibration patterns
    var vibrate by remember {
        mutableStateOf(AlarmData.DEFAULT_VIB_PATTERN)
    }

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
                    TimePicker(state = timePickerState)
                } else {
                    TimeInput(state = timePickerState)
                }

                TextField(
                    message,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = InputTransformation.maxLength(30),
                    placeholder = { Text("Title") },
                    modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 10.dp)
                )
                if (type == TimePickerType.REGULAR) {
                    AlarmActiveDaysInput(
                        Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 5.dp),
                        currActiveDays = activeDays,
                        onChange = { activeDaysIn ->
                            activeDays = activeDaysIn
                        }
                    )
                } else {
                    activeDays = AlarmData.DAY_NONE
                }
                ExposedDropdownMenuBox(
                    expanded = puzzleMenuVisible,
                    onExpandedChange = { puzzleMenuVisible = it },
                    modifier = Modifier
                        .wrapContentSize()
                        .border(2.dp, GRAYISH_WHITE, shape = RoundedCornerShape(20.dp))
                ) {
                    TextField(
                        value = puzzleType.name,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryEditable,
                            true
                        ),
                        trailingIcon = {
                            Text("v")
                        },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = TRANSPARENT_WHITE,
                            unfocusedIndicatorColor = TRANSPARENT_WHITE,
                            disabledIndicatorColor = TRANSPARENT_WHITE,
                            errorIndicatorColor = TRANSPARENT_WHITE
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = puzzleMenuVisible,
                        onDismissRequest = { puzzleMenuVisible = false }
                    ) {
                        PuzzleType.entries.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.name,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                onClick = {
                                    puzzleMenuVisible = false
                                    puzzleType = it
                                }
                            )
                        }
                    }
                }
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
                    val triggerTime = ZonedDateTime.now()
                        .withHour(timePickerState.hour)
                        .withMinute(timePickerState.minute)
                        .withSecond(0)
                        .withNano(0)
                    onConfirm(
                        AlarmData(
                            version = AlarmData.VERSION,
                            message = message.text.toString(),
                            activeDays = activeDays,
                            triggerTime = triggerTime,
                            running = true,
                            soundUri = ringtoneUri.toString(),
                            vibration = vibrate,
                            puzzleType = puzzleType
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