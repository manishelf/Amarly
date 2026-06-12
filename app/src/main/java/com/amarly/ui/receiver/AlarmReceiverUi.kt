package com.amarly.ui.receiver

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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

        // Lock screen keyguard
        // TODO: this requires additional permission from settings for display on lock screen
        val km = getSystemService(KeyguardManager::class.java)
        km?.requestDismissKeyguard(this, null)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Ignore back button
                }
            }
        )

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
                                    Modifier.padding(15.dp, 50.dp, 15.dp, 15.dp),
                                    style = Typography.headlineLarge
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
                            progress = viewModel.interactionCountdownProgress
                        ) {
                            Puzzle(
                                type = viewModel.currPuzzle,
                                totalQuestions = viewModel.puzzleQuestionCount,
                                onDismiss = {
                                    viewModel.dismiss(
                                        activity = this@AlarmReceiverUi,
                                    )
                                },
                                onSnooze = viewModel::snooze,
                                onInteraction = {
                                    viewModel.snooze(
                                        snoozeCount = 1,
                                        onComplete = {},
                                        snoozeTimeInMin = viewModel.INTERACTION_TIME_IN_MIN
                                    )
                                },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (!viewModel.isDismissed) {
            startActivity(
                Intent(this, AlarmReceiverUi::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }
            )
        }
    }
}