package com.amarly.ui.puzzle

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


enum class PuzzleType {
    SNOOZE_DISMISS,
}

class PuzzleFactory {

    @Composable
    fun Puzzle(
        type: PuzzleType,
        maxSnoozeCount: Int = 5,
        onSnooze: (Int) -> Boolean,
        onDissmiss: (Long) -> Boolean,
        modifier: Modifier = Modifier
    ) {
        when (type) {
            PuzzleType.SNOOZE_DISMISS -> {
                SnoozeDissmiss(
                    onSnooze = onSnooze,
                    onDissmiss = onDissmiss,
                    modifier = Modifier,
                )
            }

            else -> {
                Text("Unknown puzzle type")
            }
        }
    }
}