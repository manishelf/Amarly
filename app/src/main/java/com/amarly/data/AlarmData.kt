package com.amarly.data

import com.amarly.ui.puzzle.PuzzleType
import java.time.Instant
import java.time.ZonedDateTime

data class AlarmData(
    val version: Int = VERSION,
    val triggerTime: ZonedDateTime,
    var activeDays: Int = 0,
    var running: Boolean = false,
    var soundUri: String = "default",
    var vibration: LongArray = DEFAULT_VIB_PATTERN,
    var maxSnooze: Int = DEFAULT_MAX_SNOOZE,
    var puzzleType: PuzzleType = PuzzleType.SIMPLE_DISMISS,

    var puzzleQuestionCount: Int = DEFAULT_PUZZLE_QUESTION_COUNT,
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

        val DEFAULT_VIB_PATTERN = longArrayOf(0, 500, 500)

        val DEFAULT_PUZZLE_QUESTION_COUNT = 3

        val DEFAULT_MAX_SNOOZE = 3

        val DAYS = listOf("S", "M", "T", "W", "T", "F", "S")
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
                "${triggerTime.hour}"
                        + "-"
                        + "${triggerTime.minute}"
                        + "-"
                        + "${triggerTime.second}"
                        + "-"
                        + "${triggerTime.nano / 1000}"
                        + "-"
                        + "${activeDays}"
                        + "-"
                        + "V${version}"
                )
    }


    fun triggerInstant(): Instant {
        val now = ZonedDateTime.now()
        var nextTrigger = triggerTime
        // Non-repeating alarm
        if (activeDays == DAY_NONE) {
            // If already passed today -> tomorrow
            if (nextTrigger.isBefore(now)) {
                nextTrigger = nextTrigger.plusDays(1)
            }
        }
        // Repeating alarm
        for (i in 0..6) {
            val candidate = triggerTime.plusDays(i.toLong())
            val checkDay = 1 shl (candidate.dayOfWeek.value - 1)

            if ((activeDays and checkDay) != 0) {
                if (candidate.isAfter(now)) {
                    nextTrigger = candidate
                    break
                } else {
                    nextTrigger = candidate.plusDays(7)
                }
            }
        }
        return nextTrigger.toInstant()
    }
}