package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SleekIndigo,
    onPrimary = Color.White,
    primaryContainer = SleekIndigoHover,
    onPrimaryContainer = Color.White,
    inversePrimary = DeepGreen,
    secondary = KineticGreen,
    onSecondary = OnKineticGreen,
    secondaryContainer = DeepGreen,
    onSecondaryContainer = KineticGreenDim,
    tertiary = OrangeAccent,
    onTertiary = Color.White,
    tertiaryContainer = OrangeContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutlineBorder,
    outlineVariant = DarkOutlineVariant,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest
)

private val LightColorScheme = lightColorScheme(
    primary = SleekIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = SleekIndigoHover,
    inversePrimary = DeepGreen,
    secondary = KineticGreenDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = DeepGreen,
    tertiary = OrangeAccent,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceContainerLow,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutlineBorder,
    outlineVariant = LightOutlineVariant,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest
)

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

@Composable
fun FitPlanTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
