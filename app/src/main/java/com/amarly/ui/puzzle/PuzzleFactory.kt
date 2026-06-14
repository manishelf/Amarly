package com.amarly.ui.puzzle

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amarly.data.Difficulty
import com.amarly.ui.puzzle.Math.MathQ
import com.amarly.ui.puzzle.QNA.QNA
import com.amarly.ui.theme.Typography


enum class PuzzleType {
    SIMPLE_DISMISS,

    MATH_EASY,
    MATH_MEDIUM,
    MATH_HARD,
    MATH_ADVANCE,
    MATH_MIX,

    QNA_EASY,
    QNA_MEDIUM,
    QNA_HARD,
    QNA_ADVANCE,
    QNA_MIX,

    // TODO:
    TYPING,
    SCAN,
}

interface PuzzleComp {
    @Composable
    fun Comp(
        context: Context,
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier = Modifier,
        questionNumber: Int = 0,
    )
}

@Composable
fun Puzzle(
    context: Context,
    type: PuzzleType,
    onSnooze: (Int, () -> Unit) -> Unit,
    onDismiss: () -> Boolean,
    onInteraction: () -> Unit,
    totalQuestions: Int = 3,
    modifier: Modifier = Modifier
) {

    var questionNo by remember {
        mutableStateOf(1)
    }

    val onDismissHandler = {
        if (questionNo < totalQuestions && type != PuzzleType.SIMPLE_DISMISS) {
            questionNo += 1
            false
        } else {
            onDismiss()
            true
        }
    }

    Card(
        modifier
    ) {

        // include question no simply for re-rendering
        if (type != PuzzleType.SIMPLE_DISMISS && questionNo > 0) {
            Text(
                text = "Q $questionNo/$totalQuestions",
                modifier = Modifier
                    .absolutePadding(20.dp, 50.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Justify,
                style = Typography.displayMedium
            )
            Spacer(Modifier.height(20.dp))
        }

        AnimatedContent(
            targetState = questionNo,
            transitionSpec = {
                // this is some vodo syntax
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "PuzzleSlide"
        ) { it ->
            val ignore = it

            // TODO: this should be a registry with auto discovery or something
            when (type) {
                PuzzleType.SIMPLE_DISMISS -> {
                    SnoozeDissmiss().Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                    )
                }

                PuzzleType.MATH_EASY -> {
                    MathQ(Difficulty.EASY).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.MATH_MEDIUM -> {
                    MathQ(Difficulty.MEDIUM).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.MATH_HARD -> {
                    MathQ(Difficulty.HARD).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.MATH_ADVANCE -> {
                    MathQ(Difficulty.ADVANCE).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.MATH_MIX -> {
                    MathQ(Difficulty.MIX).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.QNA_EASY -> {
                    QNA(Difficulty.EASY).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.QNA_MEDIUM -> {
                    QNA(Difficulty.MEDIUM).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.QNA_HARD -> {
                    QNA(Difficulty.HARD).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.QNA_ADVANCE -> {
                    QNA(Difficulty.ADVANCE).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                PuzzleType.QNA_MIX -> {
                    QNA(Difficulty.MIX).Comp(
                        context = context,
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNumber = questionNo
                    )
                }

                else -> {
                    Text("Unknown puzzle type")
                }
            }
        }
    }
}