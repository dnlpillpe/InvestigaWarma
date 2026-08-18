package com.investigawarma.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = LabViolet,
    onPrimary = PaperWhite,
    secondary = DiscoveryCyan,
    onSecondary = DeepSpace,
    tertiary = SparkAmber,
    onTertiary = DeepSpace,
    background = PaperWhite,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = DeepSpaceLight,
    error = MissionCoral,
)

private val DarkColors = darkColorScheme(
    primary = LabVioletLight,
    onPrimary = DeepSpace,
    secondary = DiscoveryCyan,
    onSecondary = DeepSpace,
    tertiary = SparkAmber,
    onTertiary = DeepSpace,
    background = DeepSpace,
    onBackground = PaperWhite,
    surface = DeepSpaceLight,
    onSurface = PaperWhite,
    error = MissionCoral,
)

@Composable
fun InvestigaWarmaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = InvestigaWarmaTypography,
        content = content,
    )
}
