package com.amarly.ui.puzzle.QNA

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarly.ui.puzzle.PuzzleComp
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import com.amarly.ui.theme.WHITE
import kotlinx.coroutines.delay

class QNA : PuzzleComp {
    @Composable
    override fun Comp(
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier,
        questionNumber: Int
    ) {
        var penaltyActive by remember { mutableStateOf(false) }
        var showReasoning by remember { mutableStateOf(false) }
        var progress by remember { mutableFloatStateOf(0f) }
        val question by remember {
            mutableStateOf(getNextQuestion())
        }

        LaunchedEffect(penaltyActive) {
            if (penaltyActive) {
                val duration = 30_000L
                val start = System.currentTimeMillis()
                while (true) {
                    val elapsed = System.currentTimeMillis() - start
                    progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                    if (elapsed >= duration) break
                    delay(16)
                }
                progress = 0f
                penaltyActive = false
            }
        }

        if (showReasoning && !question.reasoning.isEmpty()) {
            ShowReasoning(
                reasoning = question.reasoning,
                onDismiss = {
                    showReasoning = false
                    onDismiss()
                }
            )
        }

        Card(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = question.text,
                modifier = Modifier.padding(20.dp),
                style = Typography.headlineMedium
            )

            if (penaltyActive) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "Wrong answer!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.padding(5.dp)
                ) {
                    for (i in question.options.indices step 2) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Option(
                                text = question.options[i],
                                enabled = !penaltyActive,
                                onClick = {
                                    if (i == question.answer) {
                                        showReasoning = true
                                    } else {
                                        penaltyActive = true
                                    }

                                    onInteraction()
                                }
                            )

                            if (i + 1 < question.options.size) {
                                Option(
                                    text = question.options[i + 1],
                                    enabled = !penaltyActive,
                                    onClick = {
                                        if (i + 1 == question.answer) {
                                            showReasoning = true
                                        } else {
                                            penaltyActive = true
                                        }

                                        onInteraction()
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun Option(
        text: String,
        enabled: Boolean = true,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .border(
                    2.dp,
                    if (enabled) GRAYISH_WHITE else WHITE,
                    RoundedCornerShape(30)
                )
                .widthIn(50.dp, 150.dp)
                .clickable(
                    enabled = enabled,
                    onClick = onClick
                )
                .alpha(if (enabled) 1f else 0.5f)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun ShowReasoning(reasoning: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Text(
                    text = reasoning,
                    modifier = Modifier.padding(20.dp),
                    style = Typography.titleLarge
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
        )
    }
}