package com.amarly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AmarlyTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme();

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}