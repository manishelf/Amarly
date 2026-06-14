package com.amarly.ui.puzzle.Math

import android.content.Context
import com.amarly.data.Difficulty
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

class MathQuestionFactory(
    private val context: Context,
) {

    fun getNextQuestion(difficulty: Difficulty = Difficulty.MIX): Node {

        val operandCount = when (difficulty) {
            Difficulty.EASY -> 2
            Difficulty.MEDIUM, Difficulty.HARD, Difficulty.MIX -> 3
            Difficulty.ADVANCE -> 4
        }
        val operatorRange = when (difficulty) {
            Difficulty.EASY -> EASY[0] until EASY[1]
            Difficulty.MEDIUM -> EASY[0] until MEDIUM[1]
            Difficulty.HARD -> MEDIUM[0] until MEDIUM[1]
            Difficulty.ADVANCE -> ADVANCE[0] until ADVANCE[1]
            Difficulty.MIX -> EASY[0] until ADVANCE[0]
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
}