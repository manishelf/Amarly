package com.amarly.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.ui.theme.GrayishWhite
import com.amarly.ui.theme.White

const val SUNDAY    = 1 shl 0
const val MONDAY    = 1 shl 1
const val TUESDAY   = 1 shl 2
const val WEDNESDAY = 1 shl 3
const val THURSDAY  = 1 shl 4
const val FRIDAY    = 1 shl 5
const val SATURDAY  = 1 shl 6

@Composable
fun TimerDaysFragment(
    activeDays: Int,
    modifierGrp: Modifier = Modifier,
    modifierIndivisual: Modifier = Modifier
) {
    val days = listOf(
        "S", "M", "T", "W", "T", "F", "S"
    )
    Row(
        modifier = modifierGrp.padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val enabled = (activeDays and (1 shl index)) != 0
            Text(
                text = day,
                modifier = modifierIndivisual,
                color =
                    if (enabled)
                        White
                    else
                        GrayishWhite,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}