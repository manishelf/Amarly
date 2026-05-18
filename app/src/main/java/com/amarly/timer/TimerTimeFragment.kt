package com.amarly.timer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun TimerTimeFragment(dateTime: Calendar, modifierGrp: Modifier = Modifier, modifierIndividual: Modifier = Modifier) {
    Row(
        modifierGrp,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hour = String.format("%02d", dateTime.get(Calendar.HOUR))
        val minute = String.format("%02d", dateTime.get(Calendar.MINUTE))
        Text(
            text = hour,
            modifierIndividual,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = ":",
            modifier = modifierIndividual.padding(
                5.dp, 0.dp
            ),
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = minute,
            modifierIndividual,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = when (dateTime.get(Calendar.AM_PM)) {
                Calendar.AM -> "am"
                Calendar.PM -> "pm"
                else -> ""
            },
            modifier = modifierIndividual.padding(5.dp, 0.dp),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}