package com.amarly.ui.puzzle

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
import com.amarly.ui.theme.Typography


enum class PuzzleType {
    SIMPLE_DISMISS,

    MATH_EASY,
    MATH_MEDIUM,
    MATH_HARD,
    MATH_ADVANCE,

    // TODO:
    TYPING,
    SCAN,
    QNA
}

interface PuzzleComp {
    @Composable
    fun Comp(
        onSnooze: (Int, () -> Unit) -> Unit,
        onDismiss: () -> Boolean,
        onInteraction: () -> Unit,
        modifier: Modifier = Modifier,
        questionNumber: Int = 0,
    )
}

@Composable
fun Puzzle(
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

    Card(modifier) {

        // include question no simply for re-rendering
        if (type != PuzzleType.SIMPLE_DISMISS && questionNo > 0) {
            Text(
                text = "Q $questionNo/$totalQuestions",
                modifier = Modifier
                    .absolutePadding(20.dp, 100.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Justify,
                style = Typography.displayMedium
            )
            Spacer(Modifier.height(40.dp))
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
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                    )
                }

                PuzzleType.MATH_EASY -> {
                    Math(1).Comp(
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNo = questionNo
                    )
                }

                PuzzleType.MATH_MEDIUM -> {
                    Math(2).Comp(
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNo = questionNo
                    )
                }

                PuzzleType.MATH_HARD -> {
                    Math(3).Comp(
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                        questionNo = questionNo
                    )
                }

                PuzzleType.MATH_ADVANCE -> {
                    Math(3).Comp(
                        onSnooze = onSnooze,
                        onDismiss = onDismissHandler,
                        onInteraction = onInteraction,
                        modifier = Modifier,
                    )
                }

                else -> {
                    Text("Unknown puzzle type")
                }
            }
        }
    }
}