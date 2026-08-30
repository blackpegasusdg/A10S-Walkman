package com.dip.a10swalkman.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WalkmanDarkScheme = darkColorScheme(
    primary = WalkmanColors.Accent,
    onPrimary = WalkmanColors.Background,
    primaryContainer = WalkmanColors.SurfacePressed,
    onPrimaryContainer = WalkmanColors.TextPrimary,
    secondary = WalkmanColors.TextSecondary,
    onSecondary = WalkmanColors.Background,
    background = WalkmanColors.Background,
    onBackground = WalkmanColors.TextPrimary,
    surface = WalkmanColors.Surface,
    onSurface = WalkmanColors.TextPrimary,
    surfaceVariant = WalkmanColors.SurfaceElevated,
    onSurfaceVariant = WalkmanColors.TextSecondary,
    outline = WalkmanColors.Divider
)

/**
 * Stable, non-dynamic theme for the A10S Walkman redesign.
 * Dynamic Android colors are intentionally not used so the visual identity
 * remains consistent across devices.
 */
@Composable
fun NeoWalkmanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WalkmanDarkScheme,
        typography = Typography,
        shapes = WalkmanShapes,
        content = content
    )
}
