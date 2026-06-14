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
import com.amarly.ui.puzzle.Math.MathQuestionFactory
import com.amarly.ui.puzzle.QNA.QNA
import com.amarly.ui.puzzle.QNA.QNAQuestionFactory
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

class PuzzleRegistry {
    var map: MutableMap<PuzzleType, PuzzleComp>? = null

    fun getPuzzle(context: Context, type: PuzzleType): PuzzleComp {
        if (map == null) {
            map = init(context)
            return map?.get(type)!!
        }
        return map?.get(type)!!
    }

    fun init(context: Context): MutableMap<PuzzleType, PuzzleComp> {
        val map = mutableMapOf<PuzzleType, PuzzleComp>()

        val mqf = MathQuestionFactory(context)
        val qqf = QNAQuestionFactory(context)

        map[PuzzleType.SIMPLE_DISMISS] = SnoozeDissmiss()

        map[PuzzleType.MATH_HARD] = MathQ(mqf, Difficulty.HARD)
        map[PuzzleType.MATH_EASY] = MathQ(mqf, Difficulty.EASY)
        map[PuzzleType.MATH_MEDIUM] = MathQ(mqf, Difficulty.MEDIUM)
        map[PuzzleType.MATH_ADVANCE] = MathQ(mqf, Difficulty.ADVANCE)
        map[PuzzleType.MATH_MIX] = MathQ(mqf, Difficulty.MIX)

        map[PuzzleType.QNA_EASY] = QNA(qqf, Difficulty.EASY)
        map[PuzzleType.QNA_MEDIUM] = QNA(qqf, Difficulty.MEDIUM)
        map[PuzzleType.QNA_HARD] = QNA(qqf, Difficulty.HARD)
        map[PuzzleType.QNA_ADVANCE] = QNA(qqf, Difficulty.ADVANCE)
        map[PuzzleType.QNA_MIX] = QNA(qqf, Difficulty.MIX)
        return map
    }
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

    val registry by remember {
        mutableStateOf(PuzzleRegistry())
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

            val comp = registry.getPuzzle(context, type)
            comp.Comp(
                context = context,
                onSnooze = onSnooze,
                onDismiss = onDismissHandler,
                onInteraction = onInteraction,
                modifier = Modifier,
            )
        }
    }
}