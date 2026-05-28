package com.amarly.data

import android.R.attr.type
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

object TimerD {
    val DAY_NONE = 0 shl 0
    val SUNDAY = 1 shl 1
    val MONDAY = 1 shl 2
    val TUESDAY = 1 shl 3
    val WEDNESDAY = 1 shl 4
    val THURSDAY = 1 shl 5
    val FRIDAY = 1 shl 6
    val SATURDAY = 1 shl 7

    val days = listOf(
        "S", "M", "T", "W", "T", "F", "S"
    )

    val defaultVibePattern = arrayOf(
        0,
        500,
        500
    )

    data class
    TimerData(
        var id: Int = 0,
        var triggerTime: Calendar = Calendar.getInstance(),
        var activeDays: Int = 0,
        var running: Boolean = false,
        var soundUri: String = "default",
        var vibration: Array<Int> = defaultVibePattern,
        var maxSnooze: Int = 5,
        var puzzleType: Int = 0,
        var message: String = ""
    ) {
        var enabled by mutableStateOf(running)

        // TODO: fix tis
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

        fun triggerMillis(): Long {
            val now = Calendar.getInstance()
            val nextTrigger = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                set(
                    Calendar.HOUR_OF_DAY,
                    triggerTime.get(Calendar.HOUR_OF_DAY)
                )
                set(
                    Calendar.MINUTE,
                    triggerTime.get(Calendar.MINUTE)
                )
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            // Non-repeating alarm
            if (activeDays == DAY_NONE) {
                // If already passed today -> tomorrow
                if (nextTrigger.before(now)) {
                    nextTrigger.add(Calendar.DAY_OF_YEAR, 1)
                }
                return nextTrigger.timeInMillis
            }
            // Repeating alarm
            for (i in 0..6) {
                val checkDay =
                    ((nextTrigger.get(Calendar.DAY_OF_WEEK) - 1 + i) % 7) + 1
                val mask = when (checkDay) {
                    Calendar.SUNDAY -> SUNDAY
                    Calendar.MONDAY -> MONDAY
                    Calendar.TUESDAY -> TUESDAY
                    Calendar.WEDNESDAY -> WEDNESDAY
                    Calendar.THURSDAY -> THURSDAY
                    Calendar.FRIDAY -> FRIDAY
                    Calendar.SATURDAY -> SATURDAY
                    else -> DAY_NONE
                }
                val matches = (activeDays and mask) != 0
                if (matches) {
                    val candidate = nextTrigger.clone() as Calendar
                    candidate.add(Calendar.DAY_OF_YEAR, i)

                    // Today but already passed
                    if (i == 0 && candidate.before(now)) {
                        continue
                    }
                    return candidate.timeInMillis
                }
            }
            return nextTrigger.timeInMillis
        }
    }
}