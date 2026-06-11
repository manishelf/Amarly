package com.amarly.ui.reciever

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarly.ui.puzzle.Puzzle
import com.amarly.ui.theme.AmarlyTheme
import com.amarly.ui.theme.Typography

class AlarmReceiverUi : ComponentActivity() {

    private val viewModel: RecieverViewModel by viewModels()

    override fun onCreate(savedInstantState: Bundle?) {
        super.onCreate(savedInstantState)

        val km = getSystemService(KeyguardManager::class.java)
        km?.requestDismissKeyguard(this, null)

        viewModel.init(this, intent.getStringExtra("alarm_id") ?: return)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND or
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
        )
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        enableEdgeToEdge()
        setContent {
            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Card(Modifier) {
                            if (!viewModel.alarmMessage.isEmpty())
                                Text(
                                    text = viewModel.alarmMessage,
                                    Modifier.padding(10.dp, 30.dp, 10.dp, 0.dp),
                                    style = Typography.displayLarge
                                )
                        }
                    }
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentAlignment = Alignment.Center
                    ) {
                        CountDownTimerCard(
                            perogress = viewModel.interactionCountdownProgress
                        ) {
                            Puzzle(
                                type = viewModel.currPuzzle,
                                maxSnoozeCount = viewModel.maxSnoozeCount,
                                onDismiss = {
                                    viewModel.dissmiss(
                                        activity = this@AlarmReceiverUi,
                                        dismissTime = it
                                    )
                                },
                                onSnooze = viewModel::snooze,
                                onInteraction = {
                                    viewModel.snooze(1, viewModel.INTERACTION_TIME_IN_MIN)
                                },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}