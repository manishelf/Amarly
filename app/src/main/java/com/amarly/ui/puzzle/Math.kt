package com.amarly.ui.puzzle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import com.amarly.ui.theme.FULL_BLACK
import com.amarly.ui.theme.GRAYISH_WHITE
import com.amarly.ui.theme.Typography
import com.amarly.ui.theme.WHITE
import kotlin.math.log2
import kotlin.math.pow
import kotlin.random.Random

enum class OPERATOR(
    val symbol: String,
    val apply: (Float, Float) -> Float,
    val isUnary: Boolean = false
) {
    // easy
    PLUS("+", { x, y -> x + y }),
    MINUS("-", { x, y -> x - y }),

    // medium
    // TODO: these are broken as they require precedence
    INC("++", { x, y -> x + 1 }, true),
    DEC("--", { x, y -> x - 1 }, true),
    MUL("*", { x, y -> x * y }),
    DIV("/", { x, y -> if (y == 0f) 0f else x / y }),
    MOD("%", { x, y -> if (y == 0f) 0f else x % y }),

    // ADVANCED
    POW("^", { x, y -> x.pow(y) }, true),
    AVG("avg", { x, y -> (x + y) / 2f }),
    MAX("max", { x, y -> maxOf(x, y) }),
    MIN("min", { x, y -> minOf(x, y) }),
    ABS_DIFF("|-|", { x, y -> kotlin.math.abs(x - y) }),
    CLAMP("clamp", { x, y -> x.coerceIn(0f, y) }),
    SWAP("swap", { x, y -> y - x }),
    LOG2("log2", { x, _ -> if (x <= 0f) 0f else log2(x) }, true)
}

val EASY = arrayOf(0, 2);
val MEDIUM = arrayOf(0, 2);
val ADVANCE = arrayOf(0, 2);

class Expression(
    val operators: Array<OPERATOR> = arrayOf(),
    val operands: Array<Int> = arrayOf(),
) {
    // TODO: this does not do precedence or association, that requires a expression tree
    fun eval(): Float {
        var ptr = 1
        var result = operands[0].toFloat()
        for (i in operators.reversed()) {
            result = i.apply(result, operands[ptr].toFloat())
            if (!i.isUnary) {
                ptr += 1
            }
        }

        return result
    }

    fun toStringPolish(): String {
        val sb = StringBuilder();
        for (i in operators) {
            sb.append(i.symbol)
            sb.append(" ")
        }
        for (i in operands) {
            sb.append(i.toString())
            sb.append(" ")
        }
        return sb.toString()
    }

    fun toStringReversePollish(): String {
        val sb = StringBuilder();
        for (i in operators) {
            sb.append(i.symbol)
            sb.append(" ")
        }
        for (i in operands) {
            sb.append(i.toString())
            sb.append(" ")
        }
        return sb.toString()
    }

    fun toStringInfix(): String {
        val sb = StringBuilder()

        for (i in operands.indices) {
            sb.append(operands[i])
            sb.append(" ")

            if (i < operators.size) {
                sb.append(operators[i].symbol)
                sb.append(" ")
            }
        }

        return sb.toString()
    }
}

class Math(
    private val difficulty: Int = 1,
) : PuzzleComp {

    fun getNextQuestion(): Expression {
        val operandCount = when (difficulty) {
            1 -> 2
            2 -> 3
            3 -> 3
            else -> difficulty // TODO
        }

        // TODO: this does not do precedence or association, that requires a expression tree
        val operatorRange = when (difficulty) {
            1, 2 -> EASY[0] until EASY[1]
            3 -> MEDIUM[0] until MEDIUM[1]
            else -> EASY[0] until ADVANCE[1] // TODO
        }

        val operators = Array(operandCount - 1) {
            OPERATOR.entries[operatorRange.random()]
        }

        val operands = Array(operandCount) {
            Random.nextInt(1, 1000)
        }

        return Expression(
            operators = operators,
            operands = operands
        )
    }

    @Composable
    override fun Comp(
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier,
        questionNo: Int, // For triggering re-render TODO: maybe use for progressive difficulty?
    ) {

        val currQuestion by remember {
            mutableStateOf(getNextQuestion())
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
                        1, 2 -> currQuestion.toStringInfix()
                        3, 4 -> currQuestion.toStringPolish()
                        else -> currQuestion.toStringReversePollish()
                    }
                )
                Spacer(Modifier.padding(20.dp))
                val result = currQuestion.eval()
                NumberBox(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        val answer = it.toFloatOrNull()
                        if (answer != null && kotlin.math.abs(answer - result) < 0.001f) {
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
        ) {
            Text(
                text = text,
                style = Typography.displayMedium
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
            Row() {
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