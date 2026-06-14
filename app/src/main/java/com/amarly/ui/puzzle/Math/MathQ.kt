package com.amarly.ui.puzzle.Math

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amarly.data.Difficulty
import com.amarly.ui.puzzle.PuzzleComp
import com.amarly.ui.theme.FULL_BLACK
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import com.amarly.ui.theme.WHITE

class MathQ(
    private val difficulty: Difficulty = Difficulty.EASY,
) : PuzzleComp {

    @Composable
    override fun Comp(
        context: Context,
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier,
        questionNumber: Int, // For triggering re-render TODO: maybe use for progressive difficulty?
    ) {

        val qf =
            MathQuestionFactory(context, difficulty) // TODO:fix this, is created every recompose

        val currQuestion by remember {
            mutableStateOf(qf.getNextQuestion())
        }

        var answerText by remember {
            mutableStateOf("")
        }

        val showAnswerWhenDespirate = true
        val equalsClickUntil = 10
        var equalsClickCounter by remember {
            mutableStateOf(0)
        }

        Card(
            Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Question(
                    text = when (difficulty) {
                        Difficulty.EASY, Difficulty.MEDIUM, Difficulty.MIX -> currQuestion.infix()
                        Difficulty.HARD -> currQuestion.prefix()
                        else -> currQuestion.postfix()
                    }
                )
                Spacer(Modifier.padding(20.dp))
                val result = currQuestion.eval()
                NumberBox(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        val answer = it.toFloatOrNull()
                        if (answer != null && kotlin.math.abs(answer - result) < 0.1f) {
                            onDismiss()
                        }
                        onInteraction()
                    },
                    onEqualsClick = {
                        equalsClickCounter++
                        if (equalsClickCounter > equalsClickUntil) {
                            equalsClickCounter = 0
                            if (showAnswerWhenDespirate) {
                                answerText = result.toString()
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun Question(text: String = "", modifier: Modifier = Modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .sizeIn(80.dp, 80.dp)
                .fillMaxWidth()
                .heightIn(300.dp)
                .verticalScroll(rememberScrollState())
                .border(
                    2.dp, GRAYISH_WHITE, RoundedCornerShape(10)
                )
        ) {
            Text(
                text = text,
                style = Typography.displayMedium,
                modifier = modifier
            )
        }
    }

    @Composable
    fun NumberBox(
        value: String,
        readOnly: Boolean = false,
        onValueChange: (String) -> Unit,
        onEqualsClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val size = 50.dp
        Surface(
            modifier = modifier.padding(10.dp),
            shape = RoundedCornerShape(10.dp),
            color = FULL_BLACK,
            border = BorderStroke(2.dp, GRAYISH_WHITE)
        ) {
            val fontSize = 50.sp
            Row(Modifier) {
                Surface(
                    modifier = Modifier
                        .padding(5.dp)
                        .width(50.dp)
                        .clickable(enabled = true, onClick = onEqualsClick),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(2.dp, GRAYISH_WHITE)
                ) {
                    Text(
                        "=",
                        style = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = WHITE,
                            fontSize = fontSize
                        ),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        color = WHITE,
                        fontSize = fontSize,
                        lineHeight = 2.sp
                    ),
                    cursorBrush = SolidColor(WHITE),
                    modifier = Modifier.padding(0.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .sizeIn(size, size)
                        ) {
                            innerTextField()
                        }
                    },
                )
            }
        }
    }
}