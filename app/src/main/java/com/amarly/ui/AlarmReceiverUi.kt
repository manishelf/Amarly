package com.amarly.ui

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.data.AlarmRepository
import com.amarly.ui.theme.AmarlyTheme
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography

class AlarmReceiverUi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND or
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
        )
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val km = getSystemService(KeyguardManager::class.java)
        //km?.requestDismissKeyguard(this, null)

        val alarm = AlarmRepository(this)
            .getById(intent.getStringExtra("timer_id") ?: return) ?: return
        enableEdgeToEdge()
        setContent {
            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Card(Modifier) {
                            Text(
                                alarm.message,
                                Modifier.padding(10.dp, 30.dp, 10.dp, 0.dp),
                                style = Typography.displaySmall
                            )
                        }
                    }
                ){
                    Box(
                        Modifier.fillMaxSize().padding(it),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            Modifier
                                .fillMaxWidth(7/8f)
                                .fillMaxHeight(7/8f),
                            shape = RoundedCornerShape(10),
                            border = BorderStroke(10.dp, GRAYISH_WHITE),
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                AlarmPuzzleUi().PuzzleFactory(0)
                            }
                        }
                    }
                }
            }
        }
    }
}