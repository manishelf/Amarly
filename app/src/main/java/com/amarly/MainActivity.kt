package com.amarly

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import com.amarly.ui.theme.AmarlyTheme
import com.amarly.ui.theme.Black
import com.example.amarly.R
import androidx.core.graphics.drawable.toDrawable
import com.amarly.timer.FRIDAY
import com.amarly.timer.MONDAY
import com.amarly.timer.THURSDAY
import com.amarly.timer.TUESDAY
import com.amarly.timer.TimerActivity
import com.amarly.timer.TimerData
import com.amarly.timer.TimerFragment
import com.amarly.timer.WEDNESDAY
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmarlyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        val timers = listOf(

                            TimerData(
                                message = "Get ready for the train",
                                activeDays = MONDAY or WEDNESDAY or FRIDAY,
                                dateTime = Calendar.getInstance(),
                                enabledInitial = true
                            ),

                            TimerData(
                                message = "Morning workout",
                                activeDays = TUESDAY or THURSDAY,
                                dateTime = Calendar.getInstance(),
                                enabledInitial = false
                            )
                        )
                        val x = innerPadding

                        TimerActivity.TimerList(timers, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

