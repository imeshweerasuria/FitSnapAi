package com.adheesha.fitsnapai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FitGreen,
    secondary = FitGray,
    background = FitLight,
    surface = FitLight,
    onPrimary = FitLight,
    onSecondary = FitDark,
    onBackground = FitDark,
    onSurface = FitDark
)

private val DarkColorScheme = darkColorScheme(
    primary = FitGreen,
    secondary = FitGray,
    background = FitDark,
    surface = FitDark,
    onPrimary = FitDark,
    onSecondary = FitLight,
    onBackground = FitLight,
    onSurface = FitLight
)

@Composable
fun FitSnapAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}