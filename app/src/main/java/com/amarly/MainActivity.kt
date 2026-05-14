package com.amarly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.amarly.ui.theme.AmarlyTheme
import com.example.amarly.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val timer : TimerActivity = TimerActivity();
        setContent {
            AmarlyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    timer.Timer(
                        name = stringResource(R.string.timer_title),
                        modifier = Modifier.padding(innerPadding)
                                            .fillMaxSize()
                                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}

