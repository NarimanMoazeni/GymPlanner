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
    secondary = EmeraldGreen,
    onSecondary = OnKineticGreen,
    secondaryContainer = DeepGreen,
    onSecondaryContainer = KineticGreenDim,
    tertiary = DarkAmberGold,
    onTertiary = Color.White,
    tertiaryContainer = OrangeContainer,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutlineBorder,
    outlineVariant = DarkOutlineVariant,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = ElectricIndigo,
    inversePrimary = DeepGreen,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = DeepGreen,
    tertiary = AmberGold,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFEF3C7),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceContainerLow,
    onSurfaceVariant = LightTextSecondary,
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
