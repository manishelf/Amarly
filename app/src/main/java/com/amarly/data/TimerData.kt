package com.amarly.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

object TimerData {
    val SUNDAY = 1 shl 0
    val MONDAY = 1 shl 1
    val TUESDAY = 1 shl 2
    val WEDNESDAY = 1 shl 3
    val THURSDAY = 1 shl 4
    val FRIDAY = 1 shl 5
    val SATURDAY = 1 shl 6

    val days = listOf(
        "S", "M", "T", "W", "T", "F", "S"
    )

    enum class TimerType {
        ONCE,
        REPEAT
    }

    val defaultVibePattern = arrayOf(
        0,
        500,
        500
    )

    data class
    TimerData(
        val id: Int = 0,
        var triggerTime: Calendar = Calendar.getInstance(),
        var activeDays: Int = 0,
        var type: TimerType = TimerType.ONCE,
        var running: Boolean = false,
        var soundUri: String = "default",
        var vibration: Array<Int> = defaultVibePattern,
        var maxSnooze: Int = 5,
        var puzzleType: Int = 0,
        var message: String = ""
    ) {
        var enabled by mutableStateOf(running)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TimerData

            if (id != other.id) return false
            if (activeDays != other.activeDays) return false
            if (running != other.running) return false
            if (maxSnooze != other.maxSnooze) return false
            if (puzzleType != other.puzzleType) return false
            if (triggerTime != other.triggerTime) return false
            if (type != other.type) return false
            if (soundUri != other.soundUri) return false
            if (!vibration.contentEquals(other.vibration)) return false
            if (message != other.message) return false
            if (enabled != other.enabled) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + activeDays
            result = 31 * result + running.hashCode()
            result = 31 * result + maxSnooze
            result = 31 * result + puzzleType
            result = 31 * result + triggerTime.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + soundUri.hashCode()
            result = 31 * result + vibration.contentHashCode()
            result = 31 * result + message.hashCode()
            result = 31 * result + enabled.hashCode()
            return result
        }


    }
}