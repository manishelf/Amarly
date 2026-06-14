package com.amarly.ui.puzzle.QNA

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarly.data.Difficulty
import com.amarly.ui.puzzle.PuzzleComp
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import com.amarly.ui.theme.WHITE
import kotlinx.coroutines.delay

class QNA(
    private val qf: QNAQuestionFactory,
    private val difficulty: Difficulty = Difficulty.EASY,
) : PuzzleComp {

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
            mutableStateOf(qf.getNextQuestion(difficulty))
        }

        val answers = remember {
            mutableStateListOf<Int>()
        }

        val checkAnswer = { index: Int ->
            if (!answers.contains(index)) {
                answers.add(index)
            }
            val answersArr = answers.toTypedArray()
            answersArr.sort()
            if (answersArr.contentEquals(question.answers)) {
                showReasoning = true
                answers.clear()
            } else if (!question.answers.contains(index)) {
                penaltyActive = true
                answers.clear()
            }
            onInteraction()
        }

        LaunchedEffect(penaltyActive) {
            if (penaltyActive) {
                val duration = 20_000L
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

            Question(
                text = question.text,
                mcq = question.answers.size > 1
            )

            if (penaltyActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
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
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .heightIn(300.dp)
                        .verticalScroll(rememberScrollState())
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
                                    checkAnswer(i)
                                }
                            )

                            if (i + 1 < question.options.size) {
                                Option(
                                    text = question.options[i + 1],
                                    enabled = !penaltyActive,
                                    onClick = {
                                        checkAnswer(i + 1)
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .border(
                    2.dp,
                    if (enabled) GRAYISH_WHITE else WHITE,
                    RoundedCornerShape(30)
                )
                .widthIn(50.dp, 150.dp)
                .heightIn(60.dp, 100.dp)
                .clickable(
                    enabled = enabled,
                    onClick = onClick
                )
                .alpha(if (enabled) 1f else 0.5f)
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                textAlign = TextAlign.Center,
                style = Typography.bodyMedium
            )
        }
    }

    // TODO: merge this and MathQ.Question()
    @Composable
    fun Question(text: String = "", mcq: Boolean = false, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10),
            border = BorderStroke(2.dp, GRAYISH_WHITE),
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(100.dp)
                    .verticalScroll(rememberScrollState()),
                style = Typography.headlineMedium
            )

            if (mcq) {
                Text(
                    text = "Select all correct options",
                    modifier = Modifier.absolutePadding(20.dp, 0.dp, 20.dp, 20.dp),
                    style = Typography.bodyLarge
                )
            }
        }
    }

    @Composable
    fun ShowReasoning(reasoning: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Text(
                    text = reasoning,
                    modifier = Modifier
                        .padding(20.dp)
                        .heightIn(300.dp)
                        .verticalScroll(rememberScrollState()),
                    style = Typography.titleLarge
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            modifier = modifier
        )
    }
}