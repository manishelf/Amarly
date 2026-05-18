package com.amarly.timer

import java.util.Calendar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class TimerData(
    var dateTime: Calendar,
    var activeDays: Int,
    var message: String = "",
    var activityType: Int = 0,
    val enabledInitial: Boolean = false

){
    var enabled by mutableStateOf(enabledInitial)
}