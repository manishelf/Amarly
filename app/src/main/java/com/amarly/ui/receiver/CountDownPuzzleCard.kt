package com.amarly.ui.receiver

import androidx.compose.animation.core.EaseInOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.amarly.ui.theme.FULL_BLACK
import com.amarly.ui.theme.WHITE

@Composable
fun CountDownTimerCard(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val borderRadius = 20.dp
    val visualProgress = EaseInOut.transform(progress)
    Box(
        modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(borderRadius)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val diameter = maxOf(size.width, size.height) * 1.5f

            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            drawArc(
                color = WHITE,
                startAngle = 90f,
                sweepAngle = 180f * visualProgress,
                useCenter = true,
                size = Size(diameter, diameter),
                topLeft = topLeft,
            )
            drawArc(
                color = WHITE,
                startAngle = 90f,
                sweepAngle = -180f * visualProgress,
                useCenter = true,
                size = Size(diameter, diameter),
                topLeft = topLeft,
            )
        }
        Box(
            Modifier
                .padding(10.dp)
                .background(FULL_BLACK, RoundedCornerShape(borderRadius)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}