package com.amarly.ui.reciever

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.amarly.ui.puzzle.PuzzleFactory
import com.amarly.ui.puzzle.PuzzleType
import com.amarly.ui.theme.AmarlyTheme
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography

class AlarmReceiverUi : ComponentActivity() {

    private val viewModel: RecieverViewModel by viewModels()

    override fun onCreate(savedInstantState: Bundle?) {
        super.onCreate(savedInstantState)

        val km = getSystemService(KeyguardManager::class.java)
        //km?.requestDismissKeyguard(this, null)

        val alarm = viewModel.getAlarm(intent.getStringExtra("alarm_id") ?: return)
        viewModel.currPuzzle = PuzzleType.SNOOZE_DISMISS

        val puzzleFactory = PuzzleFactory()


        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
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
                            if (!alarm.message.isEmpty())
                                Text(
                                    text = alarm.message,
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
                        Card(
                            Modifier
                                .fillMaxWidth(7 / 8f)
                                .fillMaxHeight(7 / 8f),
                            shape = RoundedCornerShape(10),
                            border = BorderStroke(10.dp, GRAYISH_WHITE),
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                puzzleFactory.Puzzle(
                                    type = viewModel.currPuzzle,
                                    maxSnoozeCount = viewModel.maxSnoozeCount,
                                    onDissmiss = {
                                        viewModel.dissmiss(
                                            activity = this@AlarmReceiverUi,
                                            dismissTime = it
                                        )
                                    },
                                    onSnooze = viewModel::snooze,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}