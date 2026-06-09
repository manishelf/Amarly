package com.amarly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AmarlyTheme(
    content: @Composable () -> Unit
) {
    // TODO: support light theme?
    val colorScheme = darkColorScheme();

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}