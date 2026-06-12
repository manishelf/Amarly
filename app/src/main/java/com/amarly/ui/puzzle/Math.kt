package com.amarly.ui.puzzle

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
    val precedence: Int,
    val apply: (Float, Float) -> Float,
    val lrAssoc: Boolean = true,
    val isUnary: Boolean = false
) {
    // easy
    PLUS("+", 2, { x, y -> x + y }),
    MINUS("-", 2, { x, y -> x - y }),

    // medium
    // TODO: these are broken as they require precedence
    INC("++", 1, { _, y -> y + 1 }, false, true),
    DEC("--", 1, { _, y -> y - 1 }, false, true),
    MUL("*", 3, { x, y -> x * y }),
    DIV("/", 3, { x, y -> if (y == 0f) 0f else x / y }),
    MOD("%", 4, { x, y -> if (y == 0f) 0f else ((x % y) + y) % y }),

    // ADVANCED
    POW("^", 5, { x, y -> x.pow(y) }),
    AVG("avg", 2, { x, y -> (x + y) / 2f }),
    MAX("max", 2, { x, y -> maxOf(x, y) }),
    MIN("min", 2, { x, y -> minOf(x, y) }),
    ABS_DIFF("|-|", 2, { x, y -> kotlin.math.abs(x - y) }),
    CLAMP("clamp", 2, { x, y -> x.coerceIn(0f, y) }),
    SWAP("swap", 2, { x, y -> y - x }),
    LOG2("log2", 1, { _, y -> if (y <= 0f) 0f else log2(y) }, false, true)
}

val EASY = arrayOf(0, 2)
val MEDIUM = arrayOf(EASY[0] + 1, EASY[1] + 5)
val ADVANCE = arrayOf(MEDIUM[0] + 1, MEDIUM[1] + 8)

val numberRange = 1 until 1000
val includeFloatNumbers = false

val maxResult = 10_000

class Node(
    var op: OPERATOR? = null,
    var value: Float = Float.NEGATIVE_INFINITY,
    var l: Node? = null,
    var r: Node? = null
) {
    fun eval(): Float {
        if (l == null && r == null) return value

        val operator = op ?: return Float.NEGATIVE_INFINITY

        return if (operator.isUnary) {
            if (operator.lrAssoc)
                operator.apply(l!!.eval(), Float.NEGATIVE_INFINITY)
            else
                operator.apply(Float.NEGATIVE_INFINITY, r!!.eval())
        } else {
            operator.apply(l!!.eval(), r!!.eval())
        }
    }

    fun forDisplay(value: Float): String {
        return if (value - value.toInt() > 0f) {
            String.format("%.2f", value)
        } else {
            value.toInt().toString()
        }
    }

    fun infix(parentPrec: Int = Int.MAX_VALUE): String {
        if (l == null && r == null) {
            return forDisplay(value)
        }

        val operator = op!!
        val p = operator.precedence

        val expr = if (operator.isUnary) {
            if (operator.lrAssoc)
                "${l!!.infix(p)}${operator.symbol}"
            else
                "${operator.symbol}${r!!.infix(p)}"
        } else {
            "${l!!.infix(p)} ${operator.symbol} ${r!!.infix(p)}"
        }

        return if (p < parentPrec && parentPrec != Int.MAX_VALUE) "($expr)" else expr
    }

    fun prefix(): String {
        val sb = StringBuilder()

        if (op != null) {
            op?.let { sb.append(it.symbol).append(" ") }
        } else {
            sb.append(forDisplay(value)).append(" ")
        }
        l?.let { sb.append(it.prefix()).append(" ") }
        r?.let { sb.append(it.prefix()).append(" ") }

        return sb.toString()
    }

    fun postfix(): String {
        val sb = StringBuilder()

        l?.let { sb.append(it.postfix()).append(" ") }
        r?.let { sb.append(it.postfix()).append(" ") }
        if (op != null) {
            op?.let { sb.append(it.symbol).append(" ") }
        } else {
            sb.append(forDisplay(value)).append(" ")
        }

        return sb.toString()
    }
}

class Math(
    private val difficulty: Int = 1,
) : PuzzleComp {

    fun getNextQuestion(): Node {

        val operandCount = when (difficulty) {
            1 -> 2
            2, 3 -> 3
            4, 5 -> 4
            else -> difficulty
        }
        val operatorRange = when (difficulty) {
            1 -> EASY[0] until EASY[1]
            2 -> EASY[0] until MEDIUM[1]
            3 -> MEDIUM[0] until MEDIUM[1]
            else -> EASY[0] until ADVANCE[1]
        }
        val operators = Array(operandCount - 1) { OPERATOR.entries[operatorRange.random()] }

        val nodes = MutableList(operandCount) {
            if (includeFloatNumbers) {
                Node(
                    value = Random.nextDouble(
                        numberRange.first.toDouble(),
                        numberRange.last().toDouble()
                    ).toFloat()
                )
            } else {
                Node(value = Random.nextInt(numberRange.first, numberRange.last()).toFloat())
            }
        }

        // This is cool
        // same node list is used to make tree leaf to root
        while (nodes.size > 1) {
            val node = Node()
            val op = operators.random()
            node.op = op

            if (op.isUnary) {
                val childIndex = Random.nextInt(nodes.size)
                val child = nodes.removeAt(childIndex)

                if (op.lrAssoc) {
                    node.l = child
                } else {
                    node.r = child
                }
            } else {
                val i = Random.nextInt(nodes.size)
                val left = nodes.removeAt(i)

                val j = Random.nextInt(nodes.size)
                val right = nodes.removeAt(j)

                node.l = left
                node.r = right
            }

            nodes.add(node)
        }
        val root = nodes.single()
        val result = root.eval()
        if (result > maxResult || result < -maxResult) {
            return getNextQuestion()
        }
        return root
    }

    @Composable
    override fun Comp(
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier,
        questionNumber: Int, // For triggering re-render TODO: maybe use for progressive difficulty?
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
                        1, 2 -> currQuestion.infix()
                        3, 4 -> currQuestion.prefix()
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
                .border(
                    2.dp, GRAYISH_WHITE, RoundedCornerShape(10)
                )
        ) {
            Text(
                text = text,
                style = Typography.displayMedium,
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