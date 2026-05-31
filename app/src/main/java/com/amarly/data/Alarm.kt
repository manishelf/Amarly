package com.amarly.data

import java.util.Calendar
import java.util.TimeZone

data class AlarmData(
    var version: Int = VERSION,
    var triggerTime: Calendar = Calendar.getInstance(),
    var timeZone: TimeZone = TimeZone.getTimeZone("GMT"),
    var activeDays: Int = 0,
    var running: Boolean = false,
    var soundUri: String = "default",
    var vibration: Array<Int> = DEFAULT_VIB_PATTERN,
    var maxSnooze: Int = 5,
    var puzzleType: Int = 0,
    var message: String = ""
) {
    companion object {
        val VERSION = 1
        val DAY_NONE = 0
        val SUNDAY = 1 shl 0
        val MONDAY = 1 shl 1
        val TUESDAY = 1 shl 2
        val WEDNESDAY = 1 shl 3
        val THURSDAY = 1 shl 4
        val FRIDAY = 1 shl 5
        val SATURDAY = 1 shl 6

        val DAYS = listOf("S", "M", "T", "W", "T", "F", "S")
        val DEFAULT_VIB_PATTERN = arrayOf(0,500, 500)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (other !is AlarmData) return false

        return id() == other.id()
    }

    override fun hashCode(): Int {
        return id().hashCode()
    }

    fun id(): String {
        return (
                "${triggerTime.get(Calendar.HOUR_OF_DAY)}"
                + "-"
                + "${triggerTime.get(Calendar.MINUTE)}"
                + "-"
                + "${triggerTime.get(Calendar.SECOND)}"
                + "-"
                + "${triggerTime.get(Calendar.MILLISECOND)}"
                + "-"
                + "${activeDays}"
                + "-"
                + "V${version}"
                )
    }

    fun triggerMillis(): Long {
        val now = Calendar.getInstance()
        val nextTrigger = triggerTime.clone() as Calendar
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
            val candidate = nextTrigger.clone() as Calendar
            candidate.add(Calendar.DAY_OF_YEAR, i)
            val checkDay = 1 shl (candidate.get(Calendar.DAY_OF_WEEK) - 1)

            if ((activeDays and checkDay) != 0) {
                // Today but already passed
                if (candidate.before(now)) {
                    continue
                }
                return candidate.timeInMillis
            }
        }
        return nextTrigger.timeInMillis
    }
}