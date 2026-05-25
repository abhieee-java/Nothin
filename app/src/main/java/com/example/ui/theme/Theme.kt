package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NothingColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = Color.White,
    primaryContainer = NothingRedMuted,
    onPrimaryContainer = NothingTextPrimary,
    secondary = NothingTextSecondary,
    onSecondary = Color.White,
    tertiary = NothingWhiteMuted,
    background = NothingBlack,
    onBackground = NothingTextPrimary,
    surface = NothingDarkGray,
    onSurface = NothingTextPrimary,
    surfaceVariant = NothingCardGray,
    onSurfaceVariant = NothingTextPrimary,
    outline = NothingBorderGray,
    error = NothingRed,
    onError = Color.White
)

@Composable
fun NothingTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NothingColorScheme,
        typography = Typography,
        content = content
    )
}
