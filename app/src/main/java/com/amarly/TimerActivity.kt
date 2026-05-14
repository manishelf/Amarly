package com.amarly

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.amarly.R

class TimerActivity {
    @Composable
    public final fun Timer(name: String, modifier: Modifier = Modifier){
        Card(
            modifier
        ) {
            Column(
                modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = modifier,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(R.string.timer_title),
                    modifier = modifier,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}